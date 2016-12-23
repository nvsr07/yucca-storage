#!/usr/bin/python

def timeToSolrTime(param):
    return "ToString(" + param + ", 'yyyy-MM-dd HH:mm:ss.SSSZ')"

def isTimeType(dataType):
    if dataType in ['data', 'date', 'datetimeOffset', 'dateTime', 'time']:
        return True
    else:
        return False

dataTypeSuffixes = {
    'boolean' : '_b',
    'string' : '_s',
    'int' : '_i',
    'long' : '_l',
    'double' : '_d',
    'data' : '_dt',
    'date' : '_dt',
    'datetimeOffset' : '_dt',
    'dateTime' : '_dt',
    'time' : '_dt',
    'float' : '_f',
    'longitude' : '_d',
    'latitude' : '_d',
    'binary' : '_s'
}

dataType2Pig = {
    'boolean' : 'boolean',
    'string' : 'chararray',
    'int' : 'int',
    'long' : 'long',
    'double' : 'double',
    'data' : 'datetime',
    'date' : 'datetime',
    'datetimeOffset' : 'datetime',
    'dateTime' : 'datetime',
    'time' : 'datetime',
    'float' : 'float',
    'longitude' : 'double',
    'latitude' : 'double',
    'binary' : 'chararray'
}

dataType2Phoenix = {
    'boolean' : 'boolean',
    'string' : 'varchar',
    'int' : 'integer',
    'long' : 'bigint',
    'double' : 'double',
    'data' : 'timestamp',
    'date' : 'timestamp',
    'datetimeOffset' : 'timestamp',
    'dateTime' : 'timestamp',
    'time' : 'timestamp',
    'float' : 'float',
    'longitude' : 'double',
    'latitude' : 'double',
    'binary' : 'varchar'
}

collectionName = {
    'bulkDataset' : 'data',
    'streamDataset' : 'measures',
    'socialDataset' : 'social',
    'binaryDataset' : 'media'
}

phoenixTableName = {
    'bulkDataset' : 'data',
    'streamDataset' : 'measures',
    'socialDataset' : 'social',
    'binaryDataset' : 'media'
}

mongoFields = {
    'bulkDataset' : 'id, idDataset, datasetVersion',
    'streamDataset' : 'id, idDataset, datasetVersion, time, sensor, streamCode',
    'socialDataset' : 'id, idDataset, datasetVersion, time, sensor, streamCode',
    'binaryDataset' : 'id, idDataset, datasetVersion'
}

pigSchema = {
    'bulkDataset' : 'id:chararray, idDataset_l:int, datasetVersion_l:int',
    'streamDataset' : 'id:chararray, idDataset_l:int, datasetVersion_l:int, time_dt:datetime, sensor_s:chararray, streamCode_s:chararray',
    'socialDataset' : 'id:chararray, idDataset_l:int, datasetVersion_l:int, time_dt:datetime, sensor_s:chararray, streamCode_s:chararray',
    'binaryDataset' : 'id:chararray, idDataset_l:int, datasetVersion_l:int'
}

phoenixColumns = {
    'bulkDataset' : 'ID,IDDATASET_L,DATASETVERSION_L',
    'streamDataset' : 'ID,IDDATASET_L,DATASETVERSION_L,TIME_dt,SENSOR_S,STREAMCODE_S',
    'socialDataset' : 'ID,IDDATASET_L,DATASETVERSION_L,TIME_dt,SENSOR_S,STREAMCODE_S',
    'binaryDataset' : 'ID,IDDATASET_L,DATASETVERSION_L'
}

solrFields = {
    'bulkDataset' : "$0 as id, 'idDataset_l', $1, 'datasetVersion_l', $2",
    'streamDataset' : "$0 as id, 'idDataset_l', $1, 'datasetVersion_l', $2, 'time_dt'," + timeToSolrTime("$3") + ", 'sensor_s', $4, 'streamCode_s', $5",
    'socialDataset' : "$0 as id, 'idDataset_l', $1, 'datasetVersion_l', $2, 'time_dt'," + timeToSolrTime("$3") + ", 'sensor_s', $4, 'streamCode_s', $5",
    'binaryDataset' : "$0 as id, 'idDataset_l', $1, 'datasetVersion_l', $2"
}

solrFieldsNum = { 
    'bulkDataset' : 3,
    'streamDataset' : 6,
    'socialDataset' : 6,
    'binaryDataset' : 3
}

collectionDb = phoenixSchemaName = solrCollectionName =  {
    'bulkDataset' : '',
    'streamDataset' : '',
    'socialDataset' : '',
    'binaryDataset' : ''
}

def init(tenantCode):
    
    global collectionDb
    collectionDb = {
        'bulkDataset' : 'DB_' + tenantCode,
        'streamDataset' : 'DB_' + tenantCode,
        'socialDataset' : 'DB_' + tenantCode,
        'binaryDataset' : 'DB_' + tenantCode
        }

    global phoenixSchemaName
    phoenixSchemaName = {
        'bulkDataset' : 'sdp_' + tenantCode,
        'streamDataset' : 'sdp_' + tenantCode,
        'socialDataset' : 'sdp_' + tenantCode,
        'binaryDataset' : 'sdp_' + tenantCode
    }
    
    global solrCollectionName
    solrCollectionName = {
        'bulkDataset' : 'sdp_' + tenantCode + '_data',
        'streamDataset' : 'sdp_' + tenantCode + '_measures',
        'socialDataset' : 'sdp_' + tenantCode + '_social',
        'binaryDataset' : 'sdp_' + tenantCode + '_media'
    }