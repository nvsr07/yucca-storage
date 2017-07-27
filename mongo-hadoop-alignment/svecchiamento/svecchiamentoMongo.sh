#/bin/bash
#set -x
#
# Esegue lo svecchiamento di tutti i dataset di ciascun tenant 
# Riceve come parametro l ambiente di esecuzione (preprod / prod ) e carica il corrispondente file xxxx.conf
#
echo "`date +%H.%M.%S` ---- `date +'%a, %d %h %Y'` ----   Start procedure svecchiamento.sh - param = $1 "
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

# crea lista tenant
mongo $MONGO_HOST:$MONGO_PORT/DB_SUPPORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet elenco_tenant_osl.js > $nomeDir/lista_tenant_org.$myPid.txt
if [ $? -ne 0 ]
then
  echo "FATAL ERROR during tenants list creation"
  exit 1
fi

# cicla sui tenant
for tenantInfo in `cat $nomeDir/lista_tenant_org.$myPid.txt` ;
do
  
  dataCollectionDb=`echo $tenantInfo | awk -F\; '{print $1}'`
  dataCollectionName=`echo $tenantInfo | awk -F\; '{print $2}'`
  measuresCollectionDb=`echo $tenantInfo | awk -F\; '{print $3}'`
  measuresCollectionName=`echo $tenantInfo | awk -F\; '{print $4}'`
  mediaCollectionDb=`echo $tenantInfo | awk -F\; '{print $5}'`
  mediaCollectionName=`echo $tenantInfo | awk -F\; '{print $6}'`
  socialCollectionDb=`echo $tenantInfo | awk -F\; '{print $7}'`
  socialCollectionName=`echo $tenantInfo | awk -F\; '{print $8}'`
  dataSolrCollectionName=`echo $tenantInfo | awk -F\; '{print $9}'`
  measuresSolrCollectionName=`echo $tenantInfo | awk -F\; '{print $10}'`
  mediaSolrCollectionName=`echo $tenantInfo | awk -F\; '{print $11}'`
  socialSolrCollectionName=`echo $tenantInfo | awk -F\; '{print $12}'`
  maxRecords=`echo $tenantInfo | awk -F\; '{print $13}'`
  
  if [ -z "$maxRecords" ]
  then
	maxRecords=8035200 # 1 record/sec per 3 mesi da 31 giorni
  fi
  

  # locka tenant
  #mongo $MONGO_HOST:$MONGO_PORT/DB_SUPPORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet --eval "var param1='$tenantCode'" lock_tenant.js
  #if [ $? -ne 0 ]
  #then
    #echo "FATAL ERROR during $tenantCode tenant locking"
    #exit 1
  #fi
  
  #svecchiamento 
  curl -g -u $solrUsr:$solrPwd "https://$solrServer:8443/gateway/default/solr/$measuresSolrCollectionName/select?q=*:*&facet=true&json.facet={dataset:{type:terms,field:iddataset_l,mincount:$maxRecords}}&rows=0&wt=json&indent=true"  | grep "\"val\"" |cut -d":" -f2 | tr -d "," | sed -e 's/.*/&;measures/' > $nomeDir/lista_dataset.$myPid.json 
  curl -g -u $solrUsr:$solrPwd "https://$solrServer:8443/gateway/default/solr/$socialSolrCollectionName/select?q=*:*&facet=true&json.facet={dataset:{type:terms,field:iddataset_l,mincount:$maxRecords}}&rows=0&wt=json&indent=true"  | grep "\"val\"" |cut -d":" -f2 | tr -d "," | sed -e 's/.*/&;social/' >> $nomeDir/lista_dataset.$myPid.json

  for riga in `cat $nomeDir/lista_dataset.$myPid.json` ;
  do
    idDataset=`echo $riga | awk -F\; '{print $1}'`
	domain=`echo $riga | awk -F\; '{print $2}'`
	
	if [ "$domain" = "data" ]
	then
	  
	  collectionName=$dataCollectionName
      collectionDb=$dataCollectionDb
      solrCollectionName=$dataSolrCollectionName
  
	elif [ "$domain" = "measures" ]
	then 
	
	  collectionName=$measuresCollectionName
      collectionDb=$measuresCollectionDb
      solrCollectionName=$measuresSolrCollectionName
	  
	elif [ "$domain" = "media" ]
	then
	
	  collectionName=$mediaCollectionName
      collectionDb=$mediaCollectionDb
	  solrCollectionName=$mediaSolrCollectionName
	  
	elif [ "$domain" = "social" ]
	then
	  
	  collectionName=$socialCollectionName
      collectionDb=$socialCollectionDb
	  solrCollectionName=$socialSolrCollectionName
	
	fi
	
	#cerca l'id minimo	
	echo "curl -g -u $solrUsr:$solrPwd \"https://$solrServer:8443/gateway/default/solr/$solrCollectionName/select?q=iddataset_l%3a$idDataset&sort=id%20desc&wt=json&indent=true&rows=1&start=$maxRecords\""
	curl -g -u $solrUsr:$solrPwd "https://$solrServer:8443/gateway/default/solr/$solrCollectionName/select?q=iddataset_l%3a$idDataset&sort=id%20desc&wt=json&indent=true&rows=1&start=$maxRecords" > $nomeDir/minId.$myPid.txt
    minId=`cat $nomeDir/minId.$myPid.txt | grep "\"id\"" | cut -d":" -f2 | tr -d "\"" | tr -d ","`
	echo "minId: "$minId

	#cancella dati in eccesso da mongo	

    echo mongo $MONGO_HOST:$MONGO_PORT/$collectionDb -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet --eval "var param1='$collectionName'; var param2=$idDataset; var param3='$minId'" svecchia_dati_osl.js 
    mongo $MONGO_HOST:$MONGO_PORT/$collectionDb -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet --eval "var param1='$collectionName'; var param2=$idDataset; var param3='$minId'" svecchia_dati_osl.js 
    if [ $? -ne 0 ]
    then
      echo "FATAL ERROR during dataset $idDataset cleaning up"
      exit 1
    fi
    
  done  

  # unlock tenant
  #mongo DB_SUPPORT --host $MONGO_HOST --port $MONGO_PORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet --eval "var param1='$tenantCode'" unlock_tenant.js
  #if [ $? -ne 0 ]
  #then
    #echo "WARNING : tenant ${tenantCode} unlocking FAILED"
    #echo ""
  #fi
done

# elimino lista tenant
rm $nomeDir/lista_tenant_org.$myPid.txt
rm $nomeDir/lista_dataset.$myPid.json
rm $nomeDir/minId.$myPid.txt

echo " *** `date +%H.%M.%S` ---- `date +'%a, %d %h %Y'` ***  procedure svecchiamento.sh successfully executed *** "
