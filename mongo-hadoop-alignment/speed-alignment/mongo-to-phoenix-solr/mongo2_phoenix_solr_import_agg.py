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

teantdataJob = Pig.compileFromFile("""../read_mongo_tenant.pig""")
tenantParams = {
            'mongoInputQuery':'{"tenantCode":"' + tenantCode +'"}'
        }
results = teantdataJob.bind(tenantParams).runSingle()

if results.isSuccessful():

    iter = results.result("tenantInfo").iterator()
    if iter.hasNext():
        tuple = iter.next()

        if tuple.get(2) is not None:
            globalVars.collectionName['bulkDataset'] = tuple.get(2)
        if tuple.get(3) is not None:
            globalVars.collectionDb['bulkDataset'] = tuple.get(3)
        if tuple.get(4) is not None:
            globalVars.collectionName['streamDataset'] = tuple.get(4)
        if tuple.get(5) is not None:
            globalVars.collectionDb['streamDataset'] = tuple.get(5)
        if tuple.get(6) is not None:
            globalVars.collectionName['socialDataset'] = tuple.get(6)
        if tuple.get(7) is not None:
            globalVars.collectionDb['socialDataset'] = tuple.get(7)
        if tuple.get(8) is not None:
            globalVars.collectionName['binaryDataset'] = tuple.get(8)
        if tuple.get(9) is not None:
            globalVars.collectionDb['binaryDataset'] = tuple.get(9)
        if tuple.get(10) is not None:
            globalVars.phoenixTableName['bulkDataset'] = tuple.get(10)
        if tuple.get(11) is not None:
            globalVars.phoenixSchemaName['bulkDataset'] = tuple.get(11)
        if tuple.get(12) is not None:
            globalVars.phoenixTableName['streamDataset'] = tuple.get(12)
        if tuple.get(13) is not None:
            globalVars.phoenixSchemaName['streamDataset'] = tuple.get(13)
        if tuple.get(14) is not None:
            globalVars.phoenixTableName['binaryDataset'] = tuple.get(14)
        if tuple.get(15) is not None:
            globalVars.phoenixSchemaName['binaryDataset'] = tuple.get(15)
        if tuple.get(16) is not None:
            globalVars.phoenixTableName['socialDataset'] = tuple.get(16)
        if tuple.get(17) is not None:
            globalVars.phoenixSchemaName['socialDataset'] = tuple.get(17)
        if tuple.get(18) is not None:
            globalVars.solrCollectionName['bulkDataset'] = tuple.get(18)
        if tuple.get(19) is not None:
            globalVars.solrCollectionName['streamDataset'] = tuple.get(19)
        if tuple.get(20) is not None:
            globalVars.solrCollectionName['binaryDataset'] = tuple.get(20)
        if tuple.get(21) is not None:
            globalVars.solrCollectionName['socialDataset'] = tuple.get(21)

        with open('data.json') as metadata_file:
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
            print 'solrFields: ' + importConfig['solrFields']
            print ''
            '''
else:
    print "Pig job failed to access MongoDB tenantdata"
