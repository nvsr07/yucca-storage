#!/usr/bin/python
from math import floor
import datetime
import time

def timeToSolrTime(param):
    return "ToString(" + param + ", 'yyyy-MM-dd HH:mm:ss.SSSZ')"

def isTimeType(dataType):
    if dataType in ['data', 'date', 'datetimeOffset', 'dateTime', 'time']:
        return True
    else:
        return False

def dateToObjectId(dateString):
    dt = datetime.datetime.strptime(dateString, '%Y/%m/%d')
    deltat = dt - datetime.datetime(1970,1,1)
    secondsFromEpoch = (deltat.microseconds + (deltat.seconds + deltat.days * 24 * 3600) * 10**6) / 10**6
    timeOffset = time.altzone if time.daylight else time.timezone
    objectId = (str(hex(int(floor(secondsFromEpoch + timeOffset)))) + "0000000000000000")[2:]
    return objectId

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
    'streamDataset' : 'ID,IDDATASET_L,DATASETVERSION_L,TIME_DT,SENSOR_S,STREAMCODE_S',
    'socialDataset' : 'ID,IDDATASET_L,DATASETVERSION_L,TIME_DT,SENSOR_S,STREAMCODE_S',
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

sanitizedFields = {
    'bulkDataset' : "id, idDataset_l, datasetVersion_l", 
    'streamDataset' : "id, idDataset_l, datasetVersion_l, time_dt, sensor_s, streamCode_s",
    'socialDataset' : "id, idDataset_l, datasetVersion_l, time_dt, sensor_s, streamCode_s",
    'binaryDataset' : "id, idDataset_l, datasetVersion_l"
}

collectionName = collectionDb = phoenixSchemaName = phoenixTableName = solrCollectionName =  {
    'bulkDataset' : '',
    'streamDataset' : '',
    'socialDataset' : '',
    'binaryDataset' : ''
}

def init(tenantCode, tenantData = None):
    
    if tenantData is None:
        tenantData = {}
     
    global collectionName
    collectionName = {
        'bulkDataset' : tenantData.get('dataCollectionName', 'data'),
        'streamDataset' : tenantData.get('measuresCollectionName', 'measures'),
        'socialDataset' : tenantData.get('socialCollectionName', 'social'),
        'binaryDataset' : tenantData.get('mediaCollectionName', 'media')
    }
    
    global collectionDb
    collectionDb = {
        'bulkDataset' : tenantData.get('dataCollectionDb', 'DB_' + tenantCode),
        'streamDataset' : tenantData.get('measuresCollectionDb', 'DB_' + tenantCode),
        'socialDataset' : tenantData.get('socialCollectionDb', 'DB_' + tenantCode),
        'binaryDataset' : tenantData.get('mediaCollectionDb', 'DB_' + tenantCode)
        }

    global phoenixSchemaName
    phoenixSchemaName = {
        'bulkDataset' : tenantData.get('dataPhoenixSchemaName', 'sdp_' + tenantCode),
        'streamDataset' : tenantData.get('measuresPhoenixSchemaName', 'sdp_' + tenantCode),
        'socialDataset' : tenantData.get('socialPhoenixSchemaName', 'sdp_' + tenantCode),
        'binaryDataset' : tenantData.get('mediaPhoenixSchemaName', 'sdp_' + tenantCode)
    }
    
    global phoenixTableName
    phoenixTableName = {
        'bulkDataset' : tenantData.get('dataPhoenixTableName', 'data'),
        'streamDataset' : tenantData.get('measuresPhoenixTableName', 'measures'),
        'socialDataset' : tenantData.get('socialPhoenixTableName', 'social'),
        'binaryDataset' : tenantData.get('mediaPhoenixTableName', 'media')
    }
    
    global solrCollectionName
    solrCollectionName = {
        'bulkDataset' : tenantData.get('dataSolrCollectionName', 'sdp_' + tenantCode + '_data'),
        'streamDataset' : tenantData.get('measuresSolrCollectionName', 'sdp_' + tenantCode + '_measures'),
        'socialDataset' : tenantData.get('socialSolrCollectionName', 'sdp_' + tenantCode + '_social'),
        'binaryDataset' : tenantData.get('mediaSolrCollectionName', 'sdp_' + tenantCode + '_media')
    }