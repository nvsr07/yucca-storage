set mongo.input.query '$query'

set solr.zkhost '$solrZookeeperQuorum'
set solr.collection '$solrCollection'

mongoData = LOAD 'mongodb://$mongoUsr:$mongoPwd@$mongoHost:$mongoPort/$mongoDB.$mongoCollection?$mongoConnParams'
USING com.mongodb.hadoop.pig.MongoLoader('$mongoFields', 'id') as ($pigSchema);

STORE mongoData INTO 'hbase://$phoenixSchema.$phoenixTable/$phoenixColumns'
USING it.csi.yucca.phoenix.pig.YuccaPhoenixHBaseStorage('$zookeeperQuorum','-batchSize 10');

<<<<<<< HEAD
solrData = FOREACH mongoData GENERATE $solrFields;
=======
solrData = FOREACH mongoData GENERATE $solrFields
>>>>>>> refs/remotes/origin/master
STORE solrData INTO 'SOLR' USING com.lucidworks.hadoop.pig.SolrStoreFunc();