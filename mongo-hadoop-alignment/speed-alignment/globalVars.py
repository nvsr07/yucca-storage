#!/usr/bin/python
from math import floor
import datetime
import time

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
    'datetime' : '_dt',
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
    'datetime' : 'datetime',
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
    'datetime' : 'timestamp',
    'time' : 'timestamp',
    'float' : 'float',
    'longitude' : 'double',
    'latitude' : 'double',
    'binary' : 'varchar'
}

dataType2Avro = {
    'boolean' : 'boolean',
    'string' : 'string',
    'int' : 'int',
    'long' : 'long',
    'double' : 'double',
    'data' : 'timestamp-millis',
    'date' : 'timestamp-millis',
    'datetimeOffset' : 'timestamp-millis',
    'datetime' : 'timestamp-millis',
    'time' : 'timestamp-millis',
    'float' : 'float',
    'longitude' : 'double',
    'latitude' : 'double',
    'binary' : 'string'
}

mongoFields = {
    'bulkDataset' : "(chararray)$0#'_id', (int)$0#'idDataset', (int)$0#'datasetVersion'",
    'streamDataset' : "(chararray)$0#'_id', (int)$0#'idDataset', (int)$0#'datasetVersion', (datetime)$0#'time', (chararray)$0#'sensor', (chararray)$0#'streamCode'",
    'socialDataset' : "(chararray)$0#'_id', (int)$0#'idDataset', (int)$0#'datasetVersion', (datetime)$0#'time', (chararray)$0#'sensor', (chararray)$0#'streamCode'",
    'binaryDataset' : "(chararray)$0#'_id', (int)$0#'idDataset', (int)$0#'datasetVersion'"
}

pigSchemaExport = {
    'bulkDataset' : '',
    'streamDataset' : 'time:chararray, ',
    'socialDataset' : 'time:chararray, ',
    'binaryDataset' : ''
}

phoenixColumns = {
    'bulkDataset' : 'ID,IDDATASET_L,DATASETVERSION_L',
    'streamDataset' : 'ID,IDDATASET_L,DATASETVERSION_L,TIME_DT,SENSOR_S,STREAMCODE_S',
    'socialDataset' : 'ID,IDDATASET_L,DATASETVERSION_L,TIME_DT,SENSOR_S,STREAMCODE_S',
    'binaryDataset' : 'ID,IDDATASET_L,DATASETVERSION_L'
}

phoenixExportColumns = {
    'bulkDataset' : '',
    'streamDataset' : 'TO_CHAR(TIME_DT), ',
    'socialDataset' : 'TO_CHAR(TIME_DT), ',
    'binaryDataset' : ''
}

collectionName = collectionDb = phoenixSchemaName = phoenixTableName =  {
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