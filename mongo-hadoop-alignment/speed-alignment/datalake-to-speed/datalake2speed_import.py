#!/usr/bin/python
import sys
from os import path
sys.path.append( path.dirname( path.dirname( path.abspath('__file__') ) ) )
from org.apache.pig.scripting import Pig


tenantCode = sys.argv[1]
mode = sys.argv[2]

# mode = REPLACE --> delete all record marked as datalake
# mode = APPEND --> only add new record (using lastId)

globalVars.init(tenantCode)



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
    'mongoInputQuery':'{"configData.tenantCode":"' + tenantCode +'", "configData.current":1, "availableSpeed":true, "availableHive":true"}'
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
        i = 0
        while fieldsIt.hasNext():
                field = fieldsIt.next()
                name = field['fieldName']
                value = field['dataType']
                aliasString = aliasString + '$' + str(i) +' as '+ name
                i = i + 1
                if fieldsIt.hasNext():
                        aliasString = aliasString + ","
        print aliasString
        copyHive2Mongo = Pig.compileFromFile("""copy_hive2mongo.pig""")
        results = copyHive2Mongo.bind({'aliasString':aliasString,'idDataset':idDataset, 'datasetVersion':datasetVersion, 'hiveSchema':hiveSchema, 'hiveTable':hiveTable, 'tenantCode':tenantCode, 'collection':globalVars.collectionName[subtype] }).runSingle()
        if results.isSuccessful():
                print "Datalake ---> Speed Done!"
        else:
                raise "Pig job failed"

else:
    raise "Pig job failed"
        