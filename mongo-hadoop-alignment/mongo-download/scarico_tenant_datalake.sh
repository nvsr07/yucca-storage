#/bin/bash
#set -x
#
# Esegue lo scarico di tutti i dataset di ciascun tenant  nella cartella specificata
# Riceve come parametro l ambiente di esecuzione (preprod / prod ) e carica il corrispondente file xxxx.conf
#
echo "`date +%H.%M.%S` ---- `date +'%a, %d %h %Y'` ----   Start procedure scarico_tenant_datalake.sh - param = $1 "
echo ""

USAGE="USAGE: $0 prod|preprod tenantCode"
if [ -z $2 ]; then
  echo "ERRORE: parametro non valorizzato"
  echo $USAGE
  exit 1
elif [ ! -e $1.conf ]; then
  echo "ERRORE: File $1.conf non trovato"
  exit 1
fi
#
# carica configurazione
. $1.conf

nomeDir=$EXP_DIR
myPid=$$
totale_scaricate=0

tenantCode=$2
tenantOrg=`mongo $MONGO_HOST:$MONGO_PORT/DB_SUPPORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet --eval "printjson(db.tenant.find({tenantCode : \"$tenantCode\"}, {_id : 0, organizationCode : 1}).toArray()[0].organizationCode);" | tr -d \" `
newTS=`mongo $MONGO_HOST:$MONGO_PORT/DB_SUPPORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet --eval "ObjectId().valueOf();" `
echo "##  "Execute export for tenant $tenantCode - organization $tenantOrg in folder $nomeDir

# locka tenant
#mongo $MONGO_HOST:$MONGO_PORT/DB_SUPPORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet --eval "var param1='$tenantCode'" lock_tenant.js
#if [ $? -ne 0 ]
#then
#  echo "FATAL ERROR during $tenantCode tenant locking"
#  exit 1
#fi

# crea lista dataset
mongo $MONGO_HOST:$MONGO_PORT/DB_SUPPORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet --eval "var param1='$tenantCode'" elenco_dataset_datalake.js > $nomeDir/lista_dataset.$myPid.txt
if [ $? -ne 0 ]
then
  echo "FATAL ERROR during dataset list creation for $tenantCode tenant"
  exit 1
fi

# crea file dati per ogni dataSet
for riga in `cat $nomeDir/lista_dataset.$myPid.txt | sed "s/ //g"`
do
 datasetCode=`echo $riga | awk -F\; '{print $1}'`
 idDataset=`echo $riga | awk -F\; '{print $2}'`
 datasetVersion=`echo $riga | awk -F\; '{print $3}'`
 #database=`echo $riga | awk -F\; '{print $4}'`
 database=`echo $riga | awk -v nnTenant=$tenantCode -F\; '{ if ($4 == "null" || $4 == "undefined") {print "DB_"nnTenant} else {print $4} }'`
 #collection=`echo $riga | awk -F\; '{print $5}'`
 collection=`echo $riga | awk -F\; '{ if ($5 == "null" || $5 == "undefined") { if ($6 == "bulkDataset") {print "data"} else if ($6 == "socialDataset") {print "social"} else {print "measures"} } else {print $5} }'`
 tenant=`echo $riga | awk -F\; '{print $7}'`
 visibility=`echo $riga | awk -F\; '{print $8}'`
 campi=`echo $riga | awk -F\; '{print $9}'`
 dominio=`echo $riga | awk -F\; '{print $10}'`
 streamCode=`echo $riga | awk -F\; '{print $11}'`
 vESlug=`echo $riga | awk -F\; '{print $12}'`
 sottoDominio=`echo $riga | awk -F\; '{print $13}'`
 echo Exporting dataset $datasetCode - version $datasetVersion from collection $collection in temporary file
# set metadati java-script and
# add time field, just for measures, social and media collection
 if [ $collection == "measures" ]
 then
  echo enriches the file with measures metadata
  script_metadati="metadati_measures.js"
  timestamp_field="time,"
 elif  [ $collection == "data" ]
 then
  # echo enriches the file with generic metadata
  # script_metadati="metadati_data.js"
  echo for data collections does not enriches the file
  script_metadati="metadati_empty.js"
  timestamp_field=""
 elif  [ $collection == "social" ]
 then
  echo does not enriches the file
  script_metadati="metadati_empty.js"
  timestamp_field="time,"
 elif  [ $collection == "media" ]
 then
  echo does not enriches the file
  script_metadati="metadati_empty.js"
  timestamp_field="time,"
 else
  echo does not enriches the file
  script_metadati="metadati_empty.js"
  timestamp_field=""
 fi
 campi=${timestamp_field}${campi}
#
 echo campi=$campi
 echo mongoexport -h $MONGO_HOST --port $MONGO_PORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin -d $database -c $collection -q "{ \$and: [ {idDataset: $idDataset}, {datasetVersion: $datasetVersion}, {origin:\"datalake\"} ] }" --type=csv -o $nomeDir/export_dataset.$myPid.txt -f $campi
 mongoexport -h $MONGO_HOST --port $MONGO_PORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin -d $database -c $collection -q "{ \$and: [ {idDataset: $idDataset}, {datasetVersion: $datasetVersion}, {origin:\"datalake\"} ] }" --type=csv -o $nomeDir/export_dataset.$myPid.txt -f $campi
 if [ $? -ne 0 ]
 then
  echo "FATAL ERROR during dataset $datasetCode export"
  rm $nomeDir/export_dataset.$myPid.txt
  exit 1
 fi
 # verifica file vuoto
 righe_file=`wc -l $nomeDir/export_dataset.$myPid.txt | awk '{print $1}'`
 if [ $righe_file -gt 1 ]
 then

   if [ $collection == "measures" ] || [ $collection == "social" ] || [ $collection == "media" ]
   then
     # riaggiusta righe spezzate, la riga deve iniziare con il timestamp
     mv $nomeDir/export_dataset.$myPid.txt $nomeDir/export_dataset.$myPid.txt.2
     cat $nomeDir/export_dataset.$myPid.txt.2 | awk --posix 'NR > 1 { if ($0 ~ /^[0-9]{4}[- \/.][0-9]{1,2}[- \/.][0-9]{1,2}/) ORS="\n"; else ORS=" "; print prev } { prev=$0 } END { ORS="\n"; print }' > $nomeDir/export_dataset.$myPid.txt
     rm $nomeDir/export_dataset.$myPid.txt.2
     righe_old=$righe_file
     righe_file=`wc -l $nomeDir/export_dataset.$myPid.txt | awk '{print $1}'`
     if [ $righe_old -ne $righe_file ]
     then
       echo Ricostruite righe spezzate
     fi
   fi

   righe_file=$((righe_file -1))
   totale_scaricate=$(($totale_scaricate + $righe_file))
   metadata=`mongo DB_SUPPORT --host $MONGO_HOST --port $MONGO_PORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet --eval "var param1='$tenantCode'; var param2=$idDataset; var param3=$datasetVersion" $script_metadati`
   if [ $? -ne 0 ]
   then
    echo "FATAL ERROR reading dataset $datasetCode / $datasetVersion metadata"
    echo $metadata
    rm $nomeDir/export_dataset.$myPid.txt
    exit 1
   fi
   meta_titles=`echo $metadata | awk -F\; '{print $1}'`
   meta_data=`echo $metadata | awk -F\; '{print $2}'`

   nome_file_dati="$nomeDir/${newTS}.${tenantCode}.${tenantOrg}.${dominio}.${sottoDominio}.${collection}.${streamCode}.${vESlug}.${datasetCode}.${datasetVersion}.${visibility}.${righe_file}.csv"
   head -1 $nomeDir/export_dataset.$myPid.txt | sed "s|$|,${meta_titles}|" > $nome_file_dati
   if [ $? -ne 0 ]
   then
    echo "FATAL ERROR during dataset $datasetCode titles enriching"
    #echo "meta_titles: $meta_titles - meta_data: $meta_data"
    rm $nomeDir/export_dataset.$myPid.txt
    rm $nome_file_dati
    exit 1
   fi
   tail -n +2 $nomeDir/export_dataset.$myPid.txt | sed "s|$|,${meta_data}|" >> $nome_file_dati
   if [ $? -ne 0 ]
   then
    echo "FATAL ERROR during dataset $datasetCode metadata enriching"
    echo "meta_titles: $meta_titles - meta_data: $meta_data"
    rm $nomeDir/export_dataset.$myPid.txt
    rm $nome_file_dati
    exit 1
   fi
 else
   echo "Dataset file empty: skip"
 fi
 rm $nomeDir/export_dataset.$myPid.txt

done

# elimino lista dataset
rm $nomeDir/lista_dataset.$myPid.txt

# unlock tenant
#mongo DB_SUPPORT --host $MONGO_HOST --port $MONGO_PORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet --eval "var param1='$tenantCode'" unlock_tenant.js
#if [ $? -ne 0 ]
#then
#  echo "WARNING : tenant ${tenantCode} unlocking FAILED"
#  echo ""
#fi

echo ""
echo " totale righe scaricate: $totale_scaricate"

echo " *** `date +%H.%M.%S` ---- `date +'%a, %d %h %Y'` ***  scarico_tenant_datalake.sh successfully executed *** "
