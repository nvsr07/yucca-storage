set pig.datetime.default.tz '+00:00'

set mongo.input.query '$query'

set solr.zkhost '$solrZookeeperQuorum'
set solr.collection '$solrCollection'

rmf output/solrTmp
mkdir output/solrTmp

mongoData = LOAD 'mongodb://$mongoUsr:$mongoPwd@$mongoHost:$mongoPort/$mongoDB.$mongoCollection?$mongoConnParams'
USING com.mongodb.hadoop.pig.MongoLoader('$mongoFields', 'id') as ($pigSchema);

STORE mongoData INTO 'hbase://$phoenixSchema.$phoenixTable/$phoenixColumns'
USING it.csi.yucca.phoenix.pig.YuccaPhoenixHBaseStorage('$zookeeperQuorum','-batchSize 10');


solrData = FOREACH mongoData GENERATE $solrFields;
STORE solrData INTO 'output/solrTmp' USING com.lucidworks.hadoop.pig.SolrStoreFunc();