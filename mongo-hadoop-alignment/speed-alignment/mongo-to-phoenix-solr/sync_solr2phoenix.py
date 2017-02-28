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

Pig.registerJar("/usr/hdp/current/phoenix-client/phoenix-client.jar")
Pig.registerJar("../lib/yucca-phoenix-pig.jar")

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

        syncJob = Pig.compileFromFile("""sync_solr2phoenix.pig""")

        for m in metadata:

            subtype = m['_id']['subtype']
            phoenixColumns = phoenixDynamicColumns = phoenixUpsertColumns = ''
            
            for field in m['_id']['fields']:

                name = field['fieldName'].strip()
                dataType = field['dataType']
                                
                if subtype == 'binaryDataset' and (name == 'urlDownloadBinary' or name == 'idBinary'):
                    continue
                
                phoenixColumns += ',' + name + globalVars.dataTypeSuffixes[dataType] 
                phoenixDynamicColumns += ',' + name + globalVars.dataTypeSuffixes[dataType] + '\ ' + globalVars.dataType2Phoenix[dataType]
                phoenixUpsertColumns += ',' + globalVars.dataType2Phoenix[dataType] + '#' + name + globalVars.dataTypeSuffixes[dataType]
                
            if subtype == 'binaryDataset': 
                phoenixColumns += ',idBinary_s,pathHdfsBinary_s,tenantBinary_s' 
                phoenixDynamicColumns += ',idBinary_s\ VARCHAR,pathHdfsBinary_s\ VARCHAR,tenantBinary_s\ VARCHAR'
                phoenixUpsertColumns += ',VARCHAR#idBinary_s,VARCHAR#pathHdfsBinary_s,VARCHAR#tenantBinary_s'
                
            if len(phoenixUpsertColumns) > 0:
                phoenixUpsertColumns = ";" + phoenixUpsertColumns[1:].upper()        
            
            query = '('
            for dataset in m['datasets']:
                query += '(IDDATASET_L = ' + str(dataset['idDataset']) + ' and DATASETVERSION_L = ' + str(dataset['datasetVersion']) + ') OR' 
            
            query = query[:-3] + ')'    

            syncConfig = {
                'minObjectId' : minObjectId,
                'maxObjectId' : maxObjectId,
                'query' : query,
                'phoenixSchema' : globalVars.phoenixSchemaName[subtype].upper(),
                'phoenixTable' :  globalVars.phoenixTableName[subtype].upper(),
                'phoenixColumns' : globalVars.phoenixColumns[subtype] + phoenixColumns.upper(),
                'phoenixDynamicColumns' : phoenixDynamicColumns[1:].upper(),
                'phoenixUpsertColumns' :  globalVars.phoenixColumns[subtype] + phoenixUpsertColumns.upper()   
            }
            
            syncResults = syncJob.bind(syncConfig).runSingle()
            if syncResults.isSuccessful():
                print 'Dataset synchronized successfully'
            else:
                print 'Dataset synchronization failed'
            
    else:
        print "Pig job failed to access MongoDB metadata"
else:
    print "Pig job failed to access MongoDB tenant"

os.remove('tenant.json')
os.remove('dataset.json')