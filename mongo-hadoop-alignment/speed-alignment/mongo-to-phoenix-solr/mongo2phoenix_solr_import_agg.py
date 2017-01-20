#!/usr/bin/python
from __future__ import with_statement
import sys
from os import path
sys.path.append( path.dirname( path.dirname( path.abspath('__file__') ) ) )
import globalVars
from org.apache.pig.scripting import Pig
from math import floor
import datetime
import time
sys.path.append('../lib/jyson-1.0.2.jar')
from com.xhaus.jyson import JysonCodec as json
from subprocess import call
import java.util as util
import java.io as javaio

if len(sys.argv) != 3:
    print "Usage: " + sys.argv[0] + " tenantCode data"
    print "Data format: yyyy/MM/dd"
    sys.exit()

tenantCode = sys.argv[1]
data = sys.argv[2]

dt = datetime.datetime.strptime(data, '%Y/%m/%d')
deltat = dt - datetime.datetime(1970,1,1)
secondsFromEpoch = (deltat.microseconds + (deltat.seconds + deltat.days * 24 * 3600) * 10**6) / 10**6
timeOffset = time.altzone if time.daylight else time.timezone
maxObjectId = (str(hex(int(floor(secondsFromEpoch + timeOffset)))) + "0000000000000000")[2:]

globalVars.init(tenantCode)

Pig.registerJar("../lib/mongo-java-driver-3.4.0.jar")
Pig.registerJar("../lib/mongo-hadoop-core-1.5.2.jar")
Pig.registerJar("../lib/mongo-hadoop-pig-1.5.2.jar")
Pig.registerJar("/usr/hdp/current/phoenix-client/phoenix-client.jar")
Pig.registerJar("../lib/yucca-phoenix-pig.jar")
Pig.registerJar("../lib/lucidworks-pig-functions-2.0.3-hd2.jar")

props = util.Properties()
#add try catch for this
propertiesfis = javaio.FileInputStream("mongo_parameters_prod.txt")
props.load(propertiesfis)

mongo1 = props.getProperty('mongoHost') + ":" + props.getProperty('mongoPort') + "/DB_SUPPORT"
mongo2 = " -u " + props.getProperty('mongoUsr')
mongo3 = " -p " + props.getProperty('mongoPwd') + ''' --authenticationDatabase admin  --quiet --eval "'''

callResult = call("mongo " + mongo1 + " " + mongo2 + " " + mongo3 + " var param1='" + tenantCode + "' " + ''' "  ../list_tenant_defaults.js > tenant.json''', shell = True)
if callResult == 0:
    
    with open('tenant.json') as tenantdata_file:
        tenantdata = json.loads(tenantdata_file.read())
    
    if tenantdata['dataCollectionName'] is not None:
        globalVars.collectionName['bulkDataset'] = tenantdata['dataCollectionName']
    if tenantdata['dataCollectionDb'] is not None:
        globalVars.collectionDb['bulkDataset'] = tenantdata['dataCollectionDb']
    if tenantdata['measuresCollectionName'] is not None:
        globalVars.collectionName['streamDataset'] = tenantdata['measuresCollectionName']
    if tenantdata['measuresCollectionDb'] is not None:
        globalVars.collectionDb['streamDataset'] = tenantdata['measuresCollectionDb']
    if tenantdata['socialCollectionName'] is not None:
        globalVars.collectionName['socialDataset'] = tenantdata['socialCollectionName']
    if tenantdata['socialCollectionDb'] is not None:
        globalVars.collectionDb['socialDataset'] = tenantdata['socialCollectionDb']
    if tenantdata['mediaCollectionName'] is not None:
        globalVars.collectionName['binaryDataset'] = tenantdata['mediaCollectionName']
    if tenantdata['mediaCollectionDb'] is not None:
        globalVars.collectionDb['binaryDataset'] = tenantdata['mediaCollectionDb']
    if tenantdata['dataPhoenixTableName'] is not None:
        globalVars.phoenixTableName['bulkDataset'] = tenantdata['dataPhoenixTableName']
    if tenantdata['dataPhoenixSchemaName'] is not None:
        globalVars.phoenixSchemaName['bulkDataset'] = tenantdata['dataPhoenixSchemaName']
    if tenantdata['measuresPhoenixTableName'] is not None:
        globalVars.phoenixTableName['streamDataset'] = tenantdata['measuresPhoenixTableName']
    if tenantdata['measuresPhoenixSchemaName'] is not None:
        globalVars.phoenixSchemaName['streamDataset'] = tenantdata['measuresPhoenixSchemaName']
    if tenantdata['mediaPhoenixTableName'] is not None:
        globalVars.phoenixTableName['binaryDataset'] = tenantdata['mediaPhoenixTableName']
    if tenantdata['mediaPhoenixSchemaName'] is not None:
        globalVars.phoenixSchemaName['binaryDataset'] = tenantdata['mediaPhoenixSchemaName']
    if tenantdata['socialPhoenixTableName'] is not None:
        globalVars.phoenixTableName['socialDataset'] = tenantdata['socialPhoenixTableName']
    if tenantdata['socialPhoenixSchemaName'] is not None:
        globalVars.phoenixSchemaName['socialDataset'] = tenantdata['socialPhoenixSchemaName']
    if tenantdata['dataSolrCollectionName'] is not None:
        globalVars.solrCollectionName['bulkDataset'] = tenantdata['dataSolrCollectionName']
    if tenantdata['measuresSolrCollectionName'] is not None:
        globalVars.solrCollectionName['streamDataset'] = tenantdata['measuresSolrCollectionName']
    if tenantdata['mediaSolrCollectionName'] is not None:
        globalVars.solrCollectionName['binaryDataset'] = tenantdata['mediaSolrCollectionName']
    if tenantdata['socialSolrCollectionName'] is not None:
        globalVars.solrCollectionName['socialDataset'] = tenantdata['socialSolrCollectionName']
    
    callResult = call("mongo " + mongo1 + " " + mongo2 + " " + mongo3 + " var param1='" + tenantCode + "' " + ''' "  ../list_mongo_dataset_fields.js > dataset.json''', shell = True)
    if callResult == 0:
        
        with open('dataset.json') as metadata_file:
            metadata = json.loads(metadata_file.read())

        importJob = Pig.compileFromFile("""copy_mongo2phoenix_solr.pig""")

        for m in metadata:

            subtype = m['_id']['subtype']

            if subtype == 'binaryDataset':
                continue

            dynamicMongoFields = dynamicPigSchema = dynamicPhoenixColumns = dynamicSolrFields = ''
            solrFieldsNum = globalVars.solrFieldsNum[subtype]

            for field in m['_id']['fields']:

                name = field['fieldName']
                dataType = field['dataType']

                dynamicMongoFields += ', ' + name
                dynamicPigSchema +=  ', ' + name + globalVars.dataTypeSuffixes[dataType] + ':' + globalVars.dataType2Pig[dataType]
                dynamicPhoenixColumns += ',' + globalVars.dataType2Phoenix[dataType] + '#' + name + globalVars.dataTypeSuffixes[dataType]
                solrField = globalVars.timeToSolrTime("$" + str(solrFieldsNum)) if globalVars.isTimeType(dataType) else "$" + str(solrFieldsNum)
                dynamicSolrFields += ", '" + name + globalVars.dataTypeSuffixes[dataType] + "', " + solrField
                solrFieldsNum += 1

            if len(dynamicPhoenixColumns) > 0:
                dynamicPhoenixColumns = ";" + dynamicPhoenixColumns[1:].upper()

            query = ''
            for dataset in m['datasets']:
                query += '{"idDataset":' + str(dataset['idDataset']) +', "datasetVersion":' + str(dataset['datasetVersion']) + '},'

            importConfig = {
                'query' : '{$or: ['+ query[:-1] + '], _id:{$lt: {"$oid": "'+ maxObjectId +'"}}}',
                'mongoDB' : globalVars.collectionDb[subtype],
                'mongoCollection' : globalVars.collectionName[subtype],
                'mongoFields' : globalVars.mongoFields[subtype] + dynamicMongoFields,
                'pigSchema' : globalVars.pigSchema[subtype] + dynamicPigSchema,
                'phoenixSchema' : globalVars.phoenixSchemaName[subtype],
                'phoenixTable' :  globalVars.phoenixTableName[subtype],
                'phoenixColumns' : globalVars.phoenixColumns[subtype] + dynamicPhoenixColumns,
                'solrCollection' : globalVars.solrCollectionName[subtype],
                'solrFields' : globalVars.solrFields[subtype] + dynamicSolrFields
            }

            importResults = importJob.bind(importConfig).runSingle()
            if importResults.isSuccessful():
                print 'Dataset imported successfully'
            else:
                print 'Dataset import failed'

            '''
            print ''
            print 'query: ' + str(importConfig['query'])
            print 'mongoDB: ' + importConfig['mongoDB']
            print 'mongoCollection: ' + importConfig['mongoCollection']
            print 'mongoFields: ' + importConfig['mongoFields']
            print 'pigSchema: ' + importConfig['pigSchema']
            print 'phoenixSchema: ' + importConfig['phoenixSchema']
            print 'phoenixTable: ' + importConfig['phoenixTable']
            print 'phoenixColumns:' + importConfig['phoenixColumns']
            print 'solrCollection: ' + importConfig['solrCollection'],
            print 'solrFields: ' + importConfig['solrFields'],
            print ''
            '''
    else:
        print "Pig job failed to access MongoDB metadata"
else:
    print "Pig job failed to access MongoDB tenant"
