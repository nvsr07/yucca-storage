#/bin/bash
#set -x
#
# Esegue lo scarico di tutti i dataset di ciascun tenant  nella cartella specificata
# Riceve come parametro l ambiente di esecuzione (preprod / prod ) e carica il corrispondente file xxxx.conf
#
echo "`date +%H.%M.%S` ---- `date +'%a, %d %h %Y'` ----   Start procedure scarico_tenant_convergenza.sh - param = $1 "
echo ""

USAGE="USAGE: $0 prod|preprod"
if [ -z $1 ]; then
  echo "ERRORE: parametro ambiente non valorizzato"
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

# crea lista tenant
mongo $MONGO_HOST:$MONGO_PORT/DB_SUPPORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet elenco_tenant_organizzazione_solr.js > $nomeDir/lista_tenant_org.$myPid.txt
if [ $? -ne 0 ]
then
  echo "FATAL ERROR during tenants list creation"
  exit 1
fi

# cicla sui tenant
for tenantInfo in `cat $nomeDir/lista_tenant_org.$myPid.txt`
do

tenantCode=`echo $tenantInfo | awk -F\; '{print $1}'`
tenantOrg=`echo $tenantInfo | awk -F\; '{print $2}'`
dataSolrCollectionName=`echo $tenantInfo | awk -F\; '{print $3}'`
measuresSolrCollectionName=`echo $tenantInfo | awk -F\; '{print $4}'`
socialSolrCollectionName=`echo $tenantInfo | awk -F\; '{print $5}'`
mediaSolrCollectionName=`echo $tenantInfo | awk -F\; '{print $6}'`

echo "##  "Execute export for tenant $tenantCode - organization $tenantOrg in folder $nomeDir

# locka tenant
mongo $MONGO_HOST:$MONGO_PORT/DB_SUPPORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet --eval "var param1='$tenantCode'" lock_tenant.js
if [ $? -ne 0 ]
then
  echo "FATAL ERROR during $tenantCode tenant locking"
  exit 1
fi

# legge vecchio e nuovo objectid
mongoData=`mongo $MONGO_HOST:$MONGO_PORT/DB_SUPPORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet --eval "var param1='$tenantCode'" leggi_objectid.js`
precTS=`echo $mongoData | awk -F\; '{print $1}'`
newTS=`echo $mongoData | awk -F\; '{print $2}'`
#echo "Old objectid: $precTS  ;  New objectid: $newTS"

# crea lista dataset
mongo $MONGO_HOST:$MONGO_PORT/DB_SUPPORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet --eval "var param1='$tenantCode'" elenco_dataset_convergenza_solr.js > $nomeDir/lista_dataset.$myPid.txt
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
  timestamp_field="time_dt,"
  solrCollectionName=$measuresSolrCollectionName
 elif  [ $collection == "data" ]
 then
  # echo enriches the file with generic metadata
  # script_metadati="metadati_data.js"
  echo for data collections does not enriches the file
  script_metadati="metadati_empty.js"
  timestamp_field=""
  solrCollectionName=$dataSolrCollectionName
 elif  [ $collection == "social" ]
 then
  echo does not enriches the file
  script_metadati="metadati_empty.js"
  timestamp_field="time_dt,"
  solrCollectionName=$socialSolrCollectionName
 elif  [ $collection == "media" ]
 then
  echo does not enriches the file
  script_metadati="metadati_empty.js"
  timestamp_field="time_dt,"
  solrCollectionName=$mediaSolrCollectionName
 else
  echo does not enriches the file
  script_metadati="metadati_empty.js"
  timestamp_field=""
  solrCollectionName=""
 fi
 campi=${timestamp_field}${campi}
#
 echo campi=$campi
 echo "curl -g -u $solrUsr:$solrPwd https://$solrServer:8443/gateway/default/solr/$solrCollectionName/select?q=iddataset_l%3a$idDataset%20AND%20datasetversion_l%3a$datasetVersion%20AND%20id%3a[$precTS%20TO%20newTS]&wt=csv&fl=$campi&rows=200000"
  
 curl -g -u $solrUsr:$solrPwd "https://$solrServer:8443/gateway/default/solr/$solrCollectionName/select?q=iddataset_l%3a$idDataset%20AND%20datasetversion_l%3a$datasetVersion%20AND%20id%3a[$precTS%20TO%20newTS]&wt=csv&fl=$campi&rows=200000" > $nomeDir/export_dataset.$myPid.txt
 
 if [ $? -ne 0 ]
 then
  echo "FATAL ERROR during dataset $datasetCode export"
  rm $nomeDir/export_dataset.$myPid.txt
  exit 1
 fi
 
 cat $nomeDir/export_dataset.$myPid.txt | grep "HTTP ERROR 403"
 if [ $? -e 0 ]
 then
  echo "FATAL ERROR during dataset $datasetCode export"
  #rm $nomeDir/export_dataset.$myPid.txt
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

## aggiorno objectid
#echo  Updating last objectid
#mongo DB_SUPPORT --host $MONGO_HOST --port $MONGO_PORT -u $MONGO_USER -p $MONGO_PWD  --authenticationDatabase admin --quiet --eval "var #param1='$tenantCode';var param2='$newTS'" update_objectid.js
#if [ $? -ne 0 ]
#then
#  echo "FATAL ERROR during last_objectid update for ${tenantCode} tenant"
#  echo "Removing all files"
#  rm $nomeDir/${newTS}.${tenantCode}.*.csv
#  exit 1
#fi

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
rm $nomeDir/lista_tenant_org.$myPid.txt

echo ""
echo " totale righe scaricate: $totale_scaricate"

echo " *** `date +%H.%M.%S` ---- `date +'%a, %d %h %Y'` ***  scarico_tenant_convergenza.sh successfully executed *** "
