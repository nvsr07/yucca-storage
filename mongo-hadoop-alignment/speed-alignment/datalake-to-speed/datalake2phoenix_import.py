#!/usr/bin/python
from __future__ import with_statement
import sys
import os
sys.path.append( os.path.dirname( os.path.dirname( os.path.abspath('__file__') ) ) )
sys.path.append('../lib/jyson-1.0.2.jar')
from com.xhaus.jyson import JysonCodec as json
import globalVars
from org.apache.pig.scripting import Pig
from subprocess import call
#from commands import getstatusoutput

if len(sys.argv) != 4:
    print "Usage: " + sys.argv[0] + " tenantCode [REPLACE/APPEND] parameters-file"
    print "Data format: yyyy/MM/dd"
    sys.exit()

tenantCode = sys.argv[1]
mode = sys.argv[2]
paramFile = sys.argv[3]

# mode = REPLACE --> delete all record marked as datalake
# mode = APPEND --> only add new record (using lastId)
#TO DO gestire bda_id non presente
#TO DO completare modalita append (filtrare dati in Pig)

pid = os.getpid()

# read properties file
import java.util as util
import java.io as javaio

try:
    props = util.Properties()
    propertiesfis =javaio.FileInputStream(paramFile)
    props.load(propertiesfis)
except:
    print "Errore leggendo " + paramFile + ": ", sys.exc_info()[0]
    sys.exit(1)

mongoConn = (props.getProperty('mongoHost') + ":" + props.getProperty('mongoPort') + "/DB_SUPPORT" +
             " -u " + props.getProperty('mongoUsr') + " -p " + props.getProperty('mongoPwd') + 
             " --authenticationDatabase admin --quiet ")
mongoParam = ''' --eval "''' + "var param1='" + tenantCode + "' " + ''' " '''

Pig.registerJar("/usr/hdp/current/phoenix-client/phoenix-client.jar")
Pig.registerJar("../lib/yucca-phoenix-pig.jar")

#lastId = '000000000000000000000000'
#if mode in ["APPEND", "append"]:
    # read from metadata source (mongoDB) lastIdDatalake2Speed for tenant
#    callResult, output = getstatusoutput('mongo ' + mongoConn + mongoParam + ' read_mongo_lastIdDatalake2Speed.js')
#    if callResult == 0:
#        print "Last id read successfully"
#        lastId = output
#    else:
#        print "Error while reading last id"
#        sys.exit(1)
print("mongo " + mongoConn + mongoParam + " ../list_tenant_defaults.js > tenant." + str(pid) + ".json")
callResult = call("mongo " + mongoConn + mongoParam + " ../list_tenant_defaults.js > tenant." + str(pid) + ".json", shell = True)
if callResult == 0:
    
    with open('tenant.' + str(pid) + '.json') as tenantdata_file:
        tenantData = json.loads(tenantdata_file.read())
        
    globalVars.init(tenantCode, tenantData)
#    newLastId = 0

    # read from metadata source (mongoDB) all datasets with
    # availableSpeed = true
    # availableHive = true (getting also dbHiveSchema and dbHiveTable)    
    callResult = call("mongo " + mongoConn + mongoParam + " ../list_mongo_hive_dataset.js > dataset." + str(pid) + ".json", shell = True)
    if callResult == 0:
        
        print "Dataset list read"
        copyHive2MongoJob = Pig.compileFromFile("""copy_hive2phoenix.pig""")
        
        with open('dataset.' + str(pid) + '.json') as metadata_file:
            metadata = json.loads(metadata_file.read())
        
        for m in metadata:
            
            subtype = m['configData']['subtype']
            fields = m['info']['fields']
            idDataset = str(m['idDataset'])
            datasetVersion = str(m['datasetVersion'])
            dynamicPhoenixColumns = ''
            aliasString = ('bda_id\ as\ bda_id:chararray,\ ' + idDataset + '\ as\ idDataset:int,\ ' +
                           datasetVersion + '\ as\ datasetVersion:int,\ ')
                        
            for field in fields:
                dataType = field['dataType']     
                name = field['fieldName'].lower()
                aliasString += "(" + globalVars.dataType2Pig[dataType] + ")" + name + ",\ "   
                dynamicPhoenixColumns += ',' + globalVars.dataType2Phoenix[dataType] + '#' + name + globalVars.dataTypeSuffixes[dataType]
            
            dynamicPhoenixColumns += ',varchar#origin_s'
            aliasString += 'bda_origin\ as\ origin:chararray'
           
            # erase data from phoenix and solr (origin==datalake)
            if mode in ["REPLACE", "replace"]:     
                args = ['../delete_phoenix_solr_dataset.sh', 
                        globalVars.phoenixSchemaName[subtype], 
                        globalVars.phoenixTableName[subtype], 
                        globalVars.solrCollectionName[subtype], 
                        idDataset, datasetVersion, 
                        'datalake', 
                        props.getProperty('zookeeperQuorum'), 
                        props.getProperty('solrServer')
                        ]
                call(args, shell = False)
                
            pigConfig = {
                'aliasString' : aliasString, 
                'hiveSchema' : m['dbHiveSchema'], 
                'hiveTable' : m['dbHiveTable'],
                'phoenixSchema' : globalVars.phoenixSchemaName[subtype],
                'phoenixTable' :  globalVars.phoenixTableName[subtype],
                'phoenixColumns' : globalVars.phoenixColumns[subtype] + ";" + dynamicPhoenixColumns[1:].upper()
            } 
            results = copyHive2MongoJob.bind(pigConfig).runSingle()
            if results.isSuccessful():
                print "Datalake ---> Speed Done! Dataset: " + idDataset + " version: " + datasetVersion
#                iter = results.result("maxId").iterator()
#                if iter.hasNext():
#                    tuple = iter.next()
#                    maxId = tuple.get(0)
#                    newLastId = max(newLastId, maxId)
            else:
                print "Pig job failed! Dataset: " + idDataset + " version: " + datasetVersion

    else:
        print "Pig job failed to access MongoDB metadata"
else:
    print "Pig job failed to access MongoDB tenant"

#if mode in ["APPEND", "append"]:
#    mongoParam = ''' --eval "''' + "var param1='" + tenantCode + "';var param2='" + lastId + "' " + ''' " '''
#    callResult = call('mongo ' + mongoConn + mongoParam + ' update_mongo_lastIdDatalake2Speed.js', shell = True)
#    if callResult != 0:
#        print "Error while updating last_datalake2speed_objectid for tenant " + tenantCode
    
os.remove('tenant.' + str(pid) + '.json')
os.remove('dataset.' + str(pid) + '.json')