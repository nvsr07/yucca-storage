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
mongo $MONGO_HOST:$MONGO_PORT/DB_SUPPORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin --quiet elenco_tenant_nsl.js > $nomeDir/lista_tenant_org.$myPid.txt
if [ $? -ne 0 ]
then
  echo "FATAL ERROR during tenants list creation"
  exit 1
fi

# cicla sui tenant
for tenantInfo in `cat $nomeDir/lista_tenant_org.$myPid.txt` ;
do

  dataPhoenixTableName=`echo $tenantInfo | awk -F\; '{print $1}'`
  dataPhoenixSchemaName=`echo $tenantInfo | awk -F\; '{print $2}'`
  measuresPhoenixTableName=`echo $tenantInfo | awk -F\; '{print $3}'`
  measuresPhoenixSchemaName=`echo $tenantInfo | awk -F\; '{print $4}'`
  mediaPhoenixTableName=`echo $tenantInfo | awk -F\; '{print $5}'`
  mediaPhoenixSchemaName=`echo $tenantInfo | awk -F\; '{print $6}'`
  socialPhoenixTableName=`echo $tenantInfo | awk -F\; '{print $7}'`
  socialPhoenixSchemaName=`echo $tenantInfo | awk -F\; '{print $8}'`
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
	  
	  phoenixTableName=$dataPhoenixTableName
      phoenixSchemaName=$dataPhoenixSchemaName
      solrCollectionName=$dataSolrCollectionName
  
	elif [ "$domain" = "measures" ]
	then 
	
	  phoenixTableName=$measuresPhoenixTableName
      phoenixSchemaName=$measuresPhoenixSchemaName
      solrCollectionName=$measuresSolrCollectionName
	  
	elif [ "$domain" = "media" ]
	then
	
	  phoenixTableName=$mediaPhoenixTableName
      phoenixSchemaName=$mediaPhoenixSchemaName
	  solrCollectionName=$mediaSolrCollectionName
	  
	elif [ "$domain" = "social" ]
	then
	  
	  phoenixTableName=$socialPhoenixTableName
      phoenixSchemaName=$socialPhoenixSchemaName
	  solrCollectionName=$socialSolrCollectionName
	
	fi
	
	#cerca l'id minimo	
	echo "curl -g -u $solrUsr:$solrPwd \"https://$solrServer:8443/gateway/default/solr/$solrCollectionName/select?q=iddataset_l%3a$idDataset&sort=id%20desc&wt=json&indent=true&rows=1&start=$maxRecords\""
	curl -g -u $solrUsr:$solrPwd "https://$solrServer:8443/gateway/default/solr/$solrCollectionName/select?q=iddataset_l%3a$idDataset&sort=id%20desc&wt=json&indent=true&rows=1&start=$maxRecords" > $nomeDir/minId.$myPid.txt
    minId=`cat $nomeDir/minId.$myPid.txt | grep "\"id\"" | cut -d":" -f2 | tr -d "\"" | tr -d ","`
	echo "minId: "$minId

	#cancella dati in eccesso da phoenix	
	echo "DELETE FROM ${phoenixSchemaName}.${phoenixTableName} WHERE id < '${minId}' AND iddataset_l = ${idDataset};"
	phoenix-sqlline sdnet-master1.sdp.csi.it,sdnet-master2.sdp.csi.it,sdnet-master3.sdp.csi.it,sdnet-master4.sdp.csi.it,sdnet-master5.sdp.csi.it <<EOF
DELETE FROM ${phoenixSchemaName}.${phoenixTableName} WHERE id < '${minId}' AND iddataset_l = ${idDataset};
EOF

	#cancella dati in eccesso da solr	
	echo "curl -v -g -u $solrUsr:$solrPwd \"https://$solrServer:8443/gateway/default/solr/$solrCollectionName/update?stream.body=<delete><query>iddataset_l%3A$idDataset%20AND%20id%3A[*%20TO%20${minId}]</query></delete>&commit=true\""
	curl -v -g -u $solrUsr:$solrPwd "https://$solrServer:8443/gateway/default/solr/$solrCollectionName/update?stream.body=<delete><query>iddataset_l%3A$idDataset%20AND%20id%3A[*%20TO%20${minId}]</query></delete>&commit=true"
 
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
