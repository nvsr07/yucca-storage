#!/usr/bin/python
import sys
from os import path
sys.path.append( path.dirname( path.dirname( path.abspath('__file__') ) ) )
import globalVars
from org.apache.pig.scripting import Pig
from subprocess import call

tenantCode = sys.argv[1]
mode = sys.argv[2]

# mode = REPLACE --> delete all record marked as datalake
# mode = APPEND --> only add new record (using lastId)

pid = os.getpid()

# read properties file
import java.util as util
import java.io as javaio

try:
    props = util.Properties()
    propertiesfis =javaio.FileInputStream("mongo_parameters_prod.txt")
    props.load(propertiesfis)
except:
    print "Errore leggendo mongo_parameters_prod.txt: ", sys.exc_info()[0]
    sys.exit(1)

mongoConn = (props.getProperty('mongoHost') + ":" + props.getProperty('mongoPort') + "/DB_SUPPORT" +
             " -u " + props.getProperty('mongoUsr') + " -p " + props.getProperty('mongoPwd') + 
             ''' --authenticationDatabase admin --quiet --eval "''' + " var param1='" + tenantCode + "' " + ''' " ''')

Pig.registerJar("../lib/mongo-java-driver-3.4.0.jar")
Pig.registerJar("../lib/mongo-hadoop-core-1.5.2.jar")
Pig.registerJar("../lib/mongo-hadoop-pig-1.5.2.jar")
Pig.registerJar("/usr/hdp/current/phoenix-client/phoenix-client.jar")
Pig.registerJar("../lib/yucca-phoenix-pig.jar")

if mode in ["APPEND", "append"]:
    # read from metadata source (mongoDB) lastIdDatalake2Speed for tenant
    readLastIdJob = Pig.compileFromFile("""read_mongo_lastIdDatalake2Speed.pig""")
    results = readLastIdJob.bind({'tenantCode':tenantCode }).runSingle()
    if results.isSuccessful():
        print "Pig job succeeded"
        iter = results.result("lastId").iterator()
        if iter.hasNext():
            lastId = iter.next()
            print "lastId: " + str(lastId)
    else:
        raise "Pig job failed"

callResult = call("mongo " + mongoConn + " ../list_tenant_defaults.js > tenant." + str(pid) + ".json", shell = True)
if callResult == 0:
    
    with open('tenant.' + str(pid) + '.json') as tenantdata_file:
        tenantData = json.loads(tenantdata_file.read())
        
    globalVars.init(tenantCode, tenantData)
    newLastId = 0

    # read from metadata source (mongoDB) all datasets with
    # availableSpeed = true
    # availableHive = true (getting also dbHiveSchema and dbHiveTable)    
    callResult = call("mongo " + mongoConn + " ../list_mongo_hive_dataset.js > dataset." + str(pid) + ".json", shell = True)
    if callResult == 0:
        
        print "Dataset list read"
        copyHive2MongoJob = Pig.compileFromFile("""copy_hive2mongo.pig""")
        
        with open('dataset.' + str(pid) + '.json') as metadata_file:
            metadata = json.loads(metadata_file.read())
        
        for m in metadata:
            
            subtype = m['configData']['subtype']
            fields = m['info']['fields']
            dynamicPhoenixColumns = ''
                        
            for field in fields:
                dataType = field['dataType']        
                dynamicPhoenixColumns += ',' + globalVars.dataType2Phoenix[dataType] + '#' + field['fieldName'] + globalVars.dataTypeSuffixes[dataType]
            
            dynamicPhoenixColumns += ',varchar#origin_s'
            aliasString = ('com.mongodb.hadoop.pig.udf.ToObjectId(bda_id) as bda_id:chararray, ' + 
                           str(m['idDataset']) + ' as idDataset:int, ' +  
                           str(m['datasetVersion']) + ' as datasetVersion:int, ' +
                           '$0 .. , $' + str(len(fields) + 1 ) + ' as origin:chararray')
           
            # erase data from phoenix and solr (origin==datalake)
            if mode in ["REPLACE", "replace"]:
                bashCmd = ('../delete_phoenix_solr_dataset.sh' +
                           ' ' + globalVars.phoenixSchemaName[subtype] +
                           ' ' + globalVars.phoenixTableName[subtype] +
                           ' ' + globalVars.solrCollectionName[subtype] +
                           ' ' + str(m['idDataset']) +
                           ' ' + str(m['datasetVersion']) +
                           ' datalake' +
                           ' ' + props.getProperty('zookeeperQuorum') +
                           ' ' + props.getProperty('solrServer')) 
                call(bashCmd, shell = True)
            
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
                print "Datalake ---> Speed Done! Dataset: " + str(m['idDataset']) + " version: " + str(m['datasetVersion'])
                iter = results.result("maxId").iterator()
                if iter.hasNext():
                    tuple = iter.next()
                    maxId = tuple.get(0)
                    newLastId = max(newLastId, maxId)
            else:
                raise "Pig job failed! Dataset: " + str(m['idDataset']) + " version: " + str(m['datasetVersion'])

    else:
        print "Pig job failed to access MongoDB metadata"
else:
    print "Pig job failed to access MongoDB tenant"

os.remove('tenant.' + str(pid) + '.json')
os.remove('dataset.' + str(pid) + '.json')