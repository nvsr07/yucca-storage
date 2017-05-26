hiveTable = LOAD '$hiveSchema.$hiveTable' using org.apache.hive.hcatalog.pig.HCatLoader();

hiveTableFiltered2 = filter hiveTable by (bda_origin is null OR bda_origin == 'datalake' ) AND bda_id is not null;
hiveTableCorrected = foreach hiveTableFiltered2 GENERATE $aliasString;

--STORE hiveTableCorrected INTO 'mongodb://$mongoUsr:$mongoPwd@$mongoHost:$mongoPort/DB_$tenantCode.$collection?$mongoConnParams'
--USING com.mongodb.hadoop.pig.MongoInsertStorage('bda_id');

STORE hiveTableCorrected INTO 'hbase://$phoenixSchema.$phoenixTable/$phoenixColumns'
USING it.csi.yucca.phoenix.pig.YuccaPhoenixHBaseStorage('$zookeeperQuorum', '-batchSize 1000');

hiveTableGrouped = GROUP hiveTableCorrected ALL;
maxId = FOREACH hiveTableCorrected GENERATE MAX(bda_id);
STORE maxId INTO '$tempOutput' USING PigStorage(',');