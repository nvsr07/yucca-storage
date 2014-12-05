#/bin/bash
#set -x
#
# Esegue lo scarico di tutti i dataset di ciascun tenant  nella cartella specificata
# Riceve come parametro l ambiente di esecuzione (preprod / prod ) e carica il corrispondente file xxxx.conf
#
echo "`date +%H.%M.%S` ---- `date +'%a, %d %h %Y'` ----   Start procedure scarico_tenant - param = $1 "
echo ""
#
# carica configurazione
. $1.conf

nomeDir=$EXP_DIR
myPid=$$

# crea lista tenant
mongo $MONGO_HOST:$MONGO_PORT/DB_SUPPORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet elenco_tenant.js > $nomeDir/lista_tenant.$myPid.txt
if [ $? -ne 0 ]
then
  echo "FATAL ERROR during tenants list creation"
  exit 1
fi

# cicla sui tenant
for tenantCode in `cat $nomeDir/lista_tenant.$myPid.txt`
do

echo "##  "Execute export for tenant $tenantCode in folder $nomeDir

# locka tenant
mongo $MONGO_HOST:$MONGO_PORT/DB_SUPPORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet --eval "var param1='$tenantCode'" lock_tenant.js
if [ $? -ne 0 ]
then
  echo "FATAL ERROR during $tenantCode tenant locking"
  exit 1
fi

# legge vecchio e nuovo timestamp
mongoData=`mongo $MONGO_HOST:$MONGO_PORT/DB_SUPPORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet --eval "var param1='$tenantCode'" leggi_timestamp.js`
precTS=`echo $mongoData | awk -F\; '{print $1}'`
newTS=`echo $mongoData | awk -F\; '{print $2}'`
echo "Old Timestamp: $precTS  ;  New Timestamp: $newTS"
if [ $precTS -lt 0 -o $newTS -lt 0 ]
then
  echo "FATAL ERROR during timestamp reading"
  exit 1
fi

# crea lista dataset
mongo $MONGO_HOST:$MONGO_PORT/DB_SUPPORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet --eval "var param1='$tenantCode'" elenco_dataset.js > $nomeDir/lista_dataset.$myPid.txt
if [ $? -ne 0 ]
then
  echo "FATAL ERROR during dataset list creation for $tenantCode tenant"
  exit 1
fi

# eseguo mongoexport per ogni dataSet
for riga in `cat $nomeDir/lista_dataset.$myPid.txt`
do
 datasetCode=`echo $riga | awk -F\; '{print $1}'`
 idDataset=`echo $riga | awk -F\; '{print $2}'`
 datasetVersion=`echo $riga | awk -F\; '{print $3}'`
 #database=`echo $riga | awk -F\; '{print $4}'`
 database=`echo $riga | awk -v nnTenant=$tenantCode -F\; '{ if ($4 == "null" || $4 == "undefined") {print "DB_"nnTenant} else {print $4} }'`
 #collection=`echo $riga | awk -F\; '{print $5}'`
 collection=`echo $riga | awk -F\; '{ if ($5 == "null" || $5 == "undefined") {print "measures"} else {print $5} }'`
 tenant=`echo $riga | awk -F\; '{print $6}'`
 visibility=`echo $riga | awk -F\; '{print $7}'`
 campi=`echo $riga | awk -F\; '{print $8}'`
 echo Exporting dataset $datasetCode - version $datasetVersion from collection $collection
#echo mongoexport -h $MONGO_HOST --port $MONGO_PORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin -d $database -c $collection -q \'{ \$and: [ {idDataset: $idDataset}, {datasetVersion: $datasetVersion}, {time: { \$gte: new Date\($precTS\) } }, {time: { \$lt: new Date\($newTS\) } } ] }\' --csv -o $nomeDir/${newTS}.${tenantCode}.${collection}.${datasetCode}.${datasetVersion}.${visibility}.csv -f $campi
mongoexport -h $MONGO_HOST --port $MONGO_PORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin -d $database -c $collection -q "{ \$and: [ {idDataset: $idDataset}, {datasetVersion: $datasetVersion}, {time: { \$gte: new Date($precTS) } }, {time: { \$lt: new Date($newTS) } } ] }" --csv -o $nomeDir/${newTS}.${tenantCode}.${collection}.${datasetCode}.${datasetVersion}.${visibility}.csv -f $campi
if [ $? -ne 0 ]
then
  echo "FATAL ERROR during dataset $datasetCode export"
  rm $nomeDir/${newTS}.${tenantCode}.${collection}.${datasetCode}.${datasetVersion}.${visibility}.csv
  exit 1
fi

done

# elimino lista dataset
rm $nomeDir/lista_dataset.$myPid.txt

# aggiorno timestamp
mongo DB_SUPPORT --host $MONGO_HOST --port $MONGO_PORT -u $MONGO_USER -p $MONGO_PWD  --authenticationDatabase admin --quiet --eval "var param1='$tenantCode';var param2=$newTS" update_timestamp.js
if [ $? -ne 0 ]
then
  echo "FATAL ERROR during timestamp update for ${tenantCode} tenant"
  echo "Removing all files"
  rm $nomeDir/${newTS}.${tenantCode}.*.csv
  exit 1
fi

# unlock tenant
mongo DB_SUPPORT --host $MONGO_HOST --port $MONGO_PORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet --eval "var param1='$tenantCode'" unlock_tenant.js
if [ $? -ne 0 ]
then
  echo "WARNING : tenant ${tenantCode} unlocking FAILED"
  echo ""
fi

# chiude ciclo tenant
done

# elimino lista tenant
rm $nomeDir/lista_tenant.$myPid.txt

echo " *** scarico_tenant successfully executed *** "

