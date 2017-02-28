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
    
if len(sys.argv) != 5:
    print "Usage: " + sys.argv[0] + " tenantCode start-date end-date parameters-file"
    print "Data format: yyyy/MM/dd"
    sys.exit()

tenantCode = sys.argv[1]
startDate = sys.argv[2]
endDate = sys.argv[3]
paramFile = sys.argv[4]

minObjectId = globalVars.dateToObjectId(startDate)
maxObjectId = globalVars.dateToObjectId(endDate)

Pig.registerJar("../lib/mongo-java-driver-3.4.0.jar")
Pig.registerJar("../lib/mongo-hadoop-core-1.5.2.jar")
Pig.registerJar("../lib/mongo-hadoop-pig-1.5.2.jar")
Pig.registerJar("/usr/hdp/current/phoenix-client/phoenix-client.jar")
Pig.registerJar("../lib/yucca-phoenix-pig.jar")
Pig.registerJar("/usr/hdp/current/pig-client/piggybank.jar")

props = util.Properties()
#add try catch for this
propertiesfis = javaio.FileInputStream(paramFile)
props.load(propertiesfis)

mongo1 = props.getProperty('mongoHost') + ":" + props.getProperty('mongoPort') + "/DB_SUPPORT"
mongo2 = " -u " + props.getProperty('mongoUsr')
mongo3 = " -p " + props.getProperty('mongoPwd') + ''' --authenticationDatabase admin  --quiet --eval "'''

callResult = call("mongo " + mongo1 + " " + mongo2 + " " + mongo3 + " var param1='" + tenantCode + "' " + ''' "  ../list_tenant_defaults.js > tenant.json''', shell = True)
if callResult == 0:
    
    with open('tenant.json') as tenantdata_file:
        tenantData = json.loads(tenantdata_file.read())
        
    globalVars.init(tenantCode, tenantData)
    
    callResult = call("mongo " + mongo1 + " " + mongo2 + " " + mongo3 + " var param1='" + tenantCode + "' " + ''' "  ../list_mongo_dataset_fields.js > dataset.json''', shell = True)
    if callResult == 0:
        
        with open('dataset.json') as metadata_file:
            metadata = json.loads(metadata_file.read())

        importJob = Pig.compileFromFile("""copy_mongo2phoenix_solr.pig""")

        for m in metadata:

            subtype = m['_id']['subtype']
            dynamicMongoFields = ''
            dynamicPhoenixColumns = ''
            
            for field in m['_id']['fields']:

                name = field['fieldName'].strip()
                dataType = field['dataType']
                
                if subtype == 'binaryDataset' and (name == 'urlDownloadBinary' or name == 'idBinary'):
                    continue

                dynamicPhoenixColumns += ',' + globalVars.dataType2Phoenix[dataType] + '#' + name + globalVars.dataTypeSuffixes[dataType]
                
                if (dataType == 'float' or dataType == 'double'):
                    dynamicMongoFields += ", ((org.apache.pig.piggybank.evaluation.IsNumeric($0#'" + name + "')==true)?(" + globalVars.dataType2Pig[dataType] + ")$0#'" + name + "':null)"
                else:
                    dynamicMongoFields += ", (" + globalVars.dataType2Pig[dataType] + ")$0#'" + name + "'" 
                
            if subtype == 'binaryDataset': 
                dynamicMongoFields += ", (chararray)$0#'idBinary', (chararray)$0#'pathHdfsBinary', (chararray)$0#'tenantBinary'"
                dynamicPhoenixColumns += ',VARCHAR#idBinary_s,VARCHAR#pathHdfsBinary_s,VARCHAR#tenantBinary_s'                
            
            if len(dynamicPhoenixColumns) > 0:
                dynamicPhoenixColumns = ";" + dynamicPhoenixColumns[1:].upper()

            query = ''
            for dataset in m['datasets']:
                query += '{"idDataset":' + str(dataset['idDataset']) +', "datasetVersion":' + str(dataset['datasetVersion']) + '},'

            importConfig = {
                'query' : '{$or: ['+ query[:-1] + '], _id:{$gt: {"$oid": "'+ minObjectId +'"}, $lt: {"$oid": "'+ maxObjectId +'"}}}',
                'mongoDB' : globalVars.collectionDb[subtype],
                'mongoCollection' : globalVars.collectionName[subtype],
                'mongoFields' : globalVars.mongoFields[subtype] + dynamicMongoFields,
                'phoenixSchema' : globalVars.phoenixSchemaName[subtype],
                'phoenixTable' :  globalVars.phoenixTableName[subtype],
                'phoenixColumns' : globalVars.phoenixColumns[subtype] + dynamicPhoenixColumns
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