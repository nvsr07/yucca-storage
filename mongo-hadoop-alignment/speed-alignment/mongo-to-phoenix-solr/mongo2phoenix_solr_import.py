#!/usr/bin/python
from __future__ import with_statement
import sys
import os
sys.path.append( os.path.dirname( os.path.dirname( os.path.abspath('__file__') ) ) )
import globalVars
from org.apache.pig.scripting import Pig
sys.path.append('../lib/jyson-1.0.2.jar')
from com.xhaus.jyson import JysonCodec as json
from subprocess import call
import java.util as util
import java.io as javaio
    
if len(sys.argv) != 4:
    print "Usage: " + sys.argv[0] + " tenantCode start-date end-date"
    print "Data format: yyyy/MM/dd"
    sys.exit()

tenantCode = sys.argv[1]
startDate = sys.argv[2]
endDate = sys.argv[3]

globalVars.init(tenantCode)
minObjectId = globalVars.dateToObjectId(startDate)
maxObjectId = globalVars.dateToObjectId(endDate)

Pig.registerJar("../lib/mongo-java-driver-3.4.0.jar")
Pig.registerJar("../lib/mongo-hadoop-core-1.5.2.jar")
Pig.registerJar("../lib/mongo-hadoop-pig-1.5.2.jar")
Pig.registerJar("/usr/hdp/current/phoenix-client/phoenix-client.jar")
Pig.registerJar("../lib/yucca-phoenix-pig.jar")
#Pig.registerJar("../lib/lucidworks-pig-functions-2.0.3-hd2.jar")
Pig.registerJar("/usr/hdp/current/pig-client/piggybank.jar")

props = util.Properties()
#add try catch for this
propertiesfis = javaio.FileInputStream("mongo_parameters.txt")
props.load(propertiesfis)

mongo1 = props.getProperty('mongoHost') + ":" + props.getProperty('mongoPort') + "/DB_SUPPORT"
mongo2 = " -u " + props.getProperty('mongoUsr')
mongo3 = " -p " + props.getProperty('mongoPwd') + ''' --authenticationDatabase admin  --quiet --eval "'''

callResult = call("mongo " + mongo1 + " " + mongo2 + " " + mongo3 + " var param1='" + tenantCode + "' " + ''' "  ../list_tenant_defaults.js > tenant.json''', shell = True)
if callResult == 0:
    
    with open('tenant.json') as tenantdata_file:
        tenantdata = json.loads(tenantdata_file.read())
        
    globalVars.collectionName['bulkDataset'] = tenantdata.get('dataCollectionName', globalVars.collectionName['bulkDataset'])
    globalVars.collectionDb['bulkDataset'] = tenantdata.get('dataCollectionDb', globalVars.collectionDb['bulkDataset'])
    globalVars.collectionName['streamDataset'] = tenantdata.get('measuresCollectionName', globalVars.collectionName['streamDataset'])
    globalVars.collectionDb['streamDataset'] = tenantdata.get('measuresCollectionDb', globalVars.collectionDb['streamDataset'])
    globalVars.collectionName['socialDataset'] = tenantdata.get('socialCollectionName', globalVars.collectionName['socialDataset'])
    globalVars.collectionDb['socialDataset'] = tenantdata.get('socialCollectionDb', globalVars.collectionDb['socialDataset'])
    globalVars.collectionName['binaryDataset'] = tenantdata.get('mediaCollectionName', globalVars.collectionName['binaryDataset']) 
    globalVars.collectionDb['binaryDataset'] = tenantdata.get('mediaCollectionDb', globalVars.collectionDb['binaryDataset'])
    globalVars.phoenixTableName['bulkDataset'] = tenantdata.get('dataPhoenixTableName', globalVars.phoenixTableName['bulkDataset'])
    globalVars.phoenixSchemaName['bulkDataset'] = tenantdata.get('dataPhoenixSchemaName', globalVars.phoenixSchemaName['bulkDataset'])
    globalVars.phoenixTableName['streamDataset'] = tenantdata.get('measuresPhoenixTableName', globalVars.phoenixTableName['streamDataset'])
    globalVars.phoenixSchemaName['streamDataset'] = tenantdata.get('measuresPhoenixSchemaName', globalVars.phoenixSchemaName['streamDataset'])
    globalVars.phoenixTableName['binaryDataset'] = tenantdata.get('mediaPhoenixTableName', globalVars.phoenixTableName['binaryDataset'])
    globalVars.phoenixSchemaName['binaryDataset'] = tenantdata.get('mediaPhoenixSchemaName', globalVars.phoenixSchemaName['binaryDataset'])      
    globalVars.phoenixTableName['socialDataset'] = tenantdata.get('socialPhoenixTableName', globalVars.phoenixTableName['socialDataset'])
    globalVars.phoenixSchemaName['socialDataset'] = tenantdata.get('socialPhoenixSchemaName', globalVars.phoenixSchemaName['socialDataset'])
#    globalVars.solrCollectionName['bulkDataset'] = tenantdata.get('dataSolrCollectionName', globalVars.solrCollectionName['bulkDataset'])
#    globalVars.solrCollectionName['streamDataset'] = tenantdata.get('measuresSolrCollectionName', globalVars.solrCollectionName['streamDataset'])
#    globalVars.solrCollectionName['binaryDataset'] = tenantdata.get('mediaSolrCollectionName', globalVars.solrCollectionName['binaryDataset']) 
#    globalVars.solrCollectionName['socialDataset'] = tenantdata.get('socialSolrCollectionName', globalVars.solrCollectionName['socialDataset'])
    
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
 #           solrFieldsNum = globalVars.solrFieldsNum[subtype]
            sanitizedFields = globalVars .sanitizedFields[subtype]
            
            for field in m['_id']['fields']:

                name = field['fieldName']
                dataType = field['dataType']

                dynamicMongoFields += ', ' + name
                dynamicPhoenixColumns += ',' + globalVars.dataType2Phoenix[dataType] + '#' + name + globalVars.dataTypeSuffixes[dataType]
#                solrField = globalVars.timeToSolrTime("$" + str(solrFieldsNum)) if globalVars.isTimeType(dataType) else "$" + str(solrFieldsNum)
#                dynamicSolrFields += ", '" + name + globalVars.dataTypeSuffixes[dataType] + "', " + solrField
#                solrFieldsNum += 1
                                
                if (dataType == 'float' or dataType == 'double'):
                    dynamicPigSchema +=  ', ' + name + globalVars.dataTypeSuffixes[dataType] + ':chararray'
                    sanitizedFields += ", ((org.apache.pig.piggybank.evaluation.IsNumeric(" + name + globalVars.dataTypeSuffixes[dataType] + ")==true)?(" + dataType + ")" + name + globalVars.dataTypeSuffixes[dataType] +":null)"
                else:
                    dynamicPigSchema +=  ', ' + name + globalVars.dataTypeSuffixes[dataType] + ':' + globalVars.dataType2Pig[dataType]
                    sanitizedFields += ", " + name + globalVars.dataTypeSuffixes[dataType] 
                
            if len(dynamicPhoenixColumns) > 0:
                dynamicPhoenixColumns = ";" + dynamicPhoenixColumns[1:].upper()

            for dataset in m['datasets']:
                query = '{"idDataset":' + str(dataset['idDataset']) +', "datasetVersion":' + str(dataset['datasetVersion']) + '},'

                importConfig = {
                    'query' : '{$or: ['+ query[:-1] + '], _id:{$gt: {"$oid": "'+ minObjectId +'"}, $lt: {"$oid": "'+ maxObjectId +'"}}}',
                    'mongoDB' : globalVars.collectionDb[subtype],
                    'mongoCollection' : globalVars.collectionName[subtype],
                    'mongoFields' : globalVars.mongoFields[subtype] + dynamicMongoFields,
                    'pigSchema' : globalVars.pigSchema[subtype] + dynamicPigSchema,
                    'phoenixSchema' : globalVars.phoenixSchemaName[subtype],
                    'phoenixTable' :  globalVars.phoenixTableName[subtype],
                    'phoenixColumns' : globalVars.phoenixColumns[subtype] + dynamicPhoenixColumns,
#                    'solrCollection' : globalVars.solrCollectionName[subtype],
#                    'solrFields' : globalVars.solrFields[subtype] + dynamicSolrFields,
                    'sanitizedFields' : sanitizedFields
                }
    
                importResults = importJob.bind(importConfig).runSingle()
                if importResults.isSuccessful():
                    print 'Dataset imported successfully'
                else:
                    print 'Dataset import failed'

    else:
        print "Pig job failed to access MongoDB metadata"
else:
    print "Pig job failed to access MongoDB tenant"

os.remove('tenant.json')
os.remove('dataset.json')
