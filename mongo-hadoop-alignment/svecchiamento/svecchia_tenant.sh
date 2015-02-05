#/bin/bash
#set -x
#
# Esegue lo svecchiamento di tutti i dataset di ciascun tenant  nella cartella specificata
# Riceve come parametro l ambiente di esecuzione (preprod / prod ) e carica il corrispondente file xxxx.conf
#
echo "`date +%H.%M.%S` ---- `date +'%a, %d %h %Y'` ----   Start procedure svecchia_tenant - param = $1 "
echo ""
#
# carica configurazione
ls $1.conf >/dev/null 2>/dev/null
if [ $? -ne 0 ]
then
  echo "FATAL ERROR: environment file $1.conf not found"
  exit 1
fi
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

echo "##  "Execute cleanup for tenant $tenantCode

# legge objectid soglia
objectidThreshold=`mongo $MONGO_HOST:$MONGO_PORT/DB_SUPPORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet --eval "var param1='$tenantCode'" leggi_data_svecc.js`
echo "objectidThreshold: $objectidThreshold"

# crea lista dataset
mongo $MONGO_HOST:$MONGO_PORT/DB_SUPPORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet --eval "var param1='$tenantCode'" elenco_dataset.js > $nomeDir/lista_dataset.$myPid.txt
if [ $? -ne 0 ]
then
  echo "FATAL ERROR during dataset list creation for $tenantCode tenant"
  exit 1
fi

# svecchia dati per ogni dataSet 
for riga in `cat $nomeDir/lista_dataset.$myPid.txt | sed "s/ //g"`
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
 #echo campi=$campi
 echo Cleaning up dataset $datasetCode - version $datasetVersion from collection $collection
 #echo mongo $MONGO_HOST:$MONGO_PORT/$database -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet --eval "var param1='$collection'; var param2=$idDataset; var param3=$datasetVersion; var param4='$objectidThreshold'" svecchia_dati.js 
 mongo $MONGO_HOST:$MONGO_PORT/$database -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet --eval "var param1='$collection'; var param2=$idDataset; var param3=$datasetVersion; var param4='$objectidThreshold'" svecchia_dati.js 
 if [ $? -ne 0 ]
 then
  echo "FATAL ERROR during dataset $datasetCode cleaning up"
  exit 1
 fi
#

done

# elimino lista dataset
rm $nomeDir/lista_dataset.$myPid.txt

# chiude ciclo tenant
done

# elimino lista tenant
rm $nomeDir/lista_tenant.$myPid.txt

echo " *** svecchia_tenant successfully executed *** "

