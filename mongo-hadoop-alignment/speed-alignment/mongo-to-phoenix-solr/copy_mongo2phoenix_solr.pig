set pig.datetime.default.tz '+00:00'

set mongo.input.query '$query'

mongoMap = LOAD 'mongodb://$mongoUsr:$mongoPwd@$mongoHost:$mongoPort/$mongoDB.$mongoCollection?$mongoConnParams'
USING com.mongodb.hadoop.pig.MongoLoader;

mongoFields = FOREACH mongoMap GENERATE $mongoFields; 

STORE mongoFields INTO 'hbase://$phoenixSchema.$phoenixTable/$phoenixColumns'
USING it.csi.yucca.phoenix.pig.YuccaPhoenixHBaseStorage('$zookeeperQuorum', '-batchSize 1000');
