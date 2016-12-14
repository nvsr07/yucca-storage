set mongo.input.query '{idDataset:$idDataset, datasetVersion:$datasetVersion, 
_id:{$gt: ObjectId(Math.floor((new Date('$data'))/1000).toString(16) + "0000000000000000")}})}'

--set solr.zkhost '$zookeeperQuorum'
--set solr.collection '$solrCollection'

mongoData = LOAD 'mongodb://$mongoUsr:$mongoPwd@$mongoHost:$mongoPort/$mongoDB.$mongoCollection?$mongoConnParams'
USING com.mongodb.hadoop.pig.MongoLoader('$mongoFields', 'id') as ($pigSchema);

STORE mongoData INTO 'hbase://$phoenixSchema.$phoenixTable/$phoenixColumns'
USING it.csi.yucca.phoenix.pig.YuccaPhoenixHBaseStorage('$zookeeperQuorum');

--solrData = FOREACH mongoData GENERATE $solrFields
--STORE solrData INTO 'SOLR' USING com.lucidworks.hadoop.pig.SolrStoreFunc();