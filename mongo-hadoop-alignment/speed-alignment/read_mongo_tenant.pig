set mongo.input.query '$mongoInputQuery'

rmf output/tenantInfo

tenantInfo = LOAD 'mongodb://$mongoUsr:$mongoPwd@$mongoHost:$mongoPort/DB_SUPPORT.tenant?$mongoConnParams'

USING com.mongodb.hadoop.pig.MongoLoader('id, tenantCode, dataCollectionName, dataCollectionDb, measuresCollectionName, measuresCollectionDb,
socialCollectionName, socialCollectionDb, mediaCollectionName, mediaCollectionDb, dataPhoenixTableName, dataPhoenixSchemaName,
measuresPhoenixTableName, measuresPhoenixSchemaName, mediaPhoenixTableName, mediaPhoenixSchemaName, socialPhoenixTableName,
socialPhoenixSchemaName' , 'id')

AS (id:chararray, tenantCode:chararray, dataCollectionName:chararray, dataCollectionDb:chararray, measuresCollectionName:chararray, 
measuresCollectionDb:chararray, socialCollectionName:chararray, socialCollectionDb:chararray, mediaCollectionName:chararray, 
mediaCollectionDb:chararray, dataPhoenixTableName:chararray, dataPhoenixSchemaName:chararray, measuresPhoenixTableName:chararray, 
measuresPhoenixSchemaName:chararray, mediaPhoenixTableName:chararray, mediaPhoenixSchemaName:chararray, socialPhoenixTableName:chararray, 
socialPhoenixSchemaName:chararray);

store tenantInfo into 'output/tenantInfo' USING BinStorage;