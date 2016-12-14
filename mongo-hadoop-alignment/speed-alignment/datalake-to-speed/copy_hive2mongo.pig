

hiveTable = LOAD '$hiveSchema.$hiveTable' using org.apache.hive.hcatalog.pig.HCatLoader();

hiveTableCorrected = foreach hiveTable GENERATE $aliasString,$idDataset as idDataset, $datasetVersion as datasetVersion, 'datalake' as origin;
hiveTableFiltered = filter hiveTableCorrected by origin is null OR origin == 'datalake';

STORE hiveTableFiltered INTO 'mongodb://$mongoUsr:$mongoPwd@$mongoHost:$mongoPort/DB_$tenantCode.$collection?$mongoConnParams'
USING com.mongodb.hadoop.pig.MongoInsertStorage('', '' );

