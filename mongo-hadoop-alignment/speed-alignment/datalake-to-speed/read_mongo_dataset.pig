set mongo.input.query '{"configData.tenantCode":"$tenantCode","configData.current":1, "availableSpeed":true, "availableHive":true}'

rmf output/datasetList

datasetList = LOAD 'mongodb://$mongoUsr:$mongoPwd@$mongoHost:$mongoPort/DB_SUPPORT.metadata?$mongoConnParams'
USING com.mongodb.hadoop.pig.MongoLoader('id, idDataset, datasetVersion,configData,info, dbHiveSchema, dbHiveTable' , 'id')
AS (id:chararray, idDataset:long,datasetVersion:long,configData:map[],info:map[],dbHiveSchema:chararray, dbHiveTable:chararray);


STORE datasetList INTO 'output/datasetList' USING BinStorage;
