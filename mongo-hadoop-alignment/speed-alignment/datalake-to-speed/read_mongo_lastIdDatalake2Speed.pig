set mongo.input.query '{tenantCode:"$tenantCode"}'

rmf output/lastId

lastId = LOAD 'mongodb://$mongoUsr:$mongoPwd@$mongoHost:$mongoPort/DB_SUPPORT.allineamento?$mongoConnParams'
USING com.mongodb.hadoop.pig.MongoLoader('id, tenantCode, last_datalake2speed_objectid' , 'id')
AS (id:chararray, tenantCode:chararray,last_datalake2speed_objectid:chararray);

STORE lastId INTO 'output/lastId';
