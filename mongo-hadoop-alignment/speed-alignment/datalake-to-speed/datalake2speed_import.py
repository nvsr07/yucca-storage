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

globalVars.init(tenantCode)

# read properties file
import java.util as util
import java.io as javaio

props = util.Properties()
#add try catch for this
propertiesfis =javaio.FileInputStream("mongo_parameters_prod.txt")
props.load( propertiesfis)


mongo1 = props.getProperty('mongoHost')+":"+props.getProperty('mongoPort')+"/DB_SUPPORT"
mongo2 = " -u "+props.getProperty('mongoUsr')
mongo3 = " -p "+props.getProperty('mongoPwd')+''' --authenticationDatabase admin  --quiet --eval "'''

# var param1=438; var param2=1; var param3='datalake'" delete_dataset.js

Pig.registerJar("../lib/mongo-java-driver-3.4.0.jar")
Pig.registerJar("../lib/mongo-hadoop-core-1.5.2.jar")
Pig.registerJar("../lib/mongo-hadoop-pig-1.5.2.jar")
Pig.registerJar("/usr/hdp/current/phoenix-client/phoenix-client.jar")
#Pig.registerJar("../lib/yucca-phoenix-pig.jar")



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

# read from metadata source (mongoDB) all datasets with
# availableSpeed = true
# availableHive = true (getting also dbHiveSchema and dbHiveTable)
readDatasetListJob = Pig.compileFromFile("""../read_mongo_dataset.pig""")
readDatasetParams = {
    'mongoInputQuery':'{"configData.tenantCode":"' + tenantCode +'", "configData.current":1, "availableSpeed":true, "availableHive":true, $or: [{"configData.deleted":0}, {"configData.deleted":{$exists:false}}]}'
}
results = readDatasetListJob.bind(readDatasetParams).runSingle()
if results.isSuccessful():
    print "Dataset list read"
    iter = results.result("datasetList").iterator()
    while iter.hasNext():
        tuple = iter.next()
        idDataset = tuple.get(1)
        datasetVersion = tuple.get(2)
        subtype = tuple.get(3)['subtype']
        info = tuple.get(4)
        hiveSchema = tuple.get(5)
        hiveTable = tuple.get(6)
        print str(idDataset) + "|" + str(datasetVersion) + "|" + subtype + "|" + hiveSchema + "|" + hiveTable
        fields = info['fields']
        fieldsIt = fields.iterator()
        aliasString = ''
        pigSchema = ''
        mongoFields = ''
        i = 0
        while fieldsIt.hasNext():
            field = fieldsIt.next()
            name = field['fieldName']
            value = field['dataType'].lower()
            aliasString = aliasString +'$'+ str(i) +' as '+ name
            pigSchema = pigSchema + name + ":" + globalVars.dataType2Pig[value]
            mongoFields = mongoFields + name + ":chararray "
            i = i + 1
            if fieldsIt.hasNext():
                aliasString = aliasString + ','
                pigSchema = pigSchema +', '
                mongoFields = mongoFields + ', '
        aliasString = aliasString + ''', com.mongodb.hadoop.pig.udf.ToObjectId(bda_id) as bda_id, $'''+str(i+1)+'  as origin'
        pigSchema = pigSchema + ', bda_id:chararray, bda_origin:chararray'
        mongoFields = mongoFields + ', bda_id:chararray, origin:chararray, idDataset:long, datasetVersion:long'
# erase data from mongoDB (origin==datalake)
        if mode in ["REPLACE", "replace"]:
            call("mongo " + mongo1 +" "+ mongo2 + " " +mongo3 + " var param1="+str(idDataset)+"; var param2="+str(datasetVersion)+'''; var param3='datalake'"'''+ " " + ''' ../delete_mongo_dataset.js''',shell = True)

        copyHive2MongoJob = Pig.compileFromFile("""copy_hive2mongo.pig""")
        #print aliasString
        results = copyHive2MongoJob.bind({'mongoFields':mongoFields, 'pigSchema':pigSchema, 'aliasString':aliasString, 'idDataset':idDataset, 'datasetVersion':datasetVersion, 'hiveSchema':hiveSchema, 'hiveTable':hiveTable, 'tenantCode':tenantCode, 'collection':globalVars.collectionName[subtype] }).runSingle()
        if results.isSuccessful():
            print "Datalake ---> Speed Done!"
        else:
            raise "Pig job failed"

else:
    raise "Pig job failed"
