

hiveTable = LOAD '$hiveSchema.$hiveTable' using org.apache.hive.hcatalog.pig.HCatLoader();

hiveTableFiltered2 = filter hiveTable by (bda_origin is null OR bda_origin == 'datalake' ) AND bda_id is not null;
hiveTableCorrected = foreach hiveTableFiltered2 GENERATE $aliasString ,$idDataset as idDataset, $datasetVersion as datasetVersion;
-- hiveTableFiltered = filter hiveTableCorrected by (origin is null OR origin == 'datalake' ) AND bda_id is not null;

STORE hiveTableCorrected INTO 'mongodb://$mongoUsr:$mongoPwd@$mongoHost:$mongoPort/DB_$tenantCode.$collection?$mongoConnParams'
USING com.mongodb.hadoop.pig.MongoInsertStorage('bda_id');

-- STORE hiveTableFiltered INTO 'mongodb://$mongoUsr:$mongoPwd@$mongoHost:$mongoPort/DB_$tenantCode.$collection?$mongoConnParams'
-- USING com.mongodb.hadoop.pig.MongoUpdateStorage(
--      '{idDataset:$idDataset, datasetVersion:$datasetVersion}',
--      '{origin:"\\$origin"}',
--      '$mongoFields',
--      '',
--      '{upsert = true, multi = true, replace = true }' );
