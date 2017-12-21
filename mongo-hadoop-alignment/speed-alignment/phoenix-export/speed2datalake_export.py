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
import csv

Pig.registerJar("/usr/hdp/current/phoenix-client/phoenix-client.jar")
Pig.registerJar("../lib/piggybankExtended.jar")

#Jar to use AVRO
#Pig.registerJar("/usr/hdp/current/pig-client/lib/avro-1.7.5.jar")
#Pig.registerJar("/usr/hdp/current/pig-client/lib/json-simple-1.1.jar")
#Pig.registerJar("/usr/hdp/current/pig-client/lib/jackson-core-asl-1.9.13.jar")
#Pig.registerJar("/usr/hdp/current/pig-client/lib/jackson-mapper-asl-1.9.13.jar")

if len(sys.argv) != 2:
    print "Usage: " + sys.argv[0] + " parameters-file"
    sys.exit(1)
    
paramFile = sys.argv[1]

try:
    props = util.Properties()
    propertiesfis = javaio.FileInputStream(paramFile)
    props.load(propertiesfis)
except:
    print "Errore leggendo mongo_parameters_prod.txt: ", sys.exc_info()[0]
    sys.exit(1)

pid = os.getpid()
outDir = props.getProperty('tmpFolder')
headFolder = props.getProperty('headFolder')
rawdataFolder = props.getProperty('rawdataFolder')
zookeeperQuorum = props.getProperty('zookeeperQuorum')

if not os.path.exists(outDir):
    os.makedirs(outDir)

mongoConnectString = props.getProperty('mongoHost') + ":" + props.getProperty('mongoPort') + "/DB_SUPPORT"
mongoConnectString += " -u " + props.getProperty('mongoUsr')
mongoConnectString += " -p " + props.getProperty('mongoPwd') + " --authenticationDatabase admin  --quiet "

callResult = call("mongo " + mongoConnectString + " ../list_tenants.js > " + outDir + "/lista_tenant_org." + str(pid) + ".json", shell = True)
if callResult != 0:
    print "FATAL ERROR during tenants list creation"
    sys.exit(1)
    
with open(outDir + "/lista_tenant_org." + str(pid) + ".json") as tenant_file:
    allTenants = json.loads(tenant_file.read())
    
for tenant in allTenants:
    tenantCode = tenant["tenantCode"]
    tenantOrg = tenant["organizationCode"]
    
    print "Executing export for tenant " + tenantCode + " - organization " + tenantOrg
    
    # locka tenant
    callResult = call("mongo " + mongoConnectString + ''' --eval "''' + " var param1='" + tenantCode + "' " + ''' " '''  + " lock_tenant.js ", shell = True)
    if callResult != 0:
        print "ERROR during " + tenantCode + " tenant locking, skipping tenant"
        continue
    
    # legge vecchio e nuovo objectid
    callResult = call("mongo " + mongoConnectString + ''' --eval "''' + " var param1='" + tenantCode + "' " + ''' " '''  + " read_objectid.js > " + outDir + "/object_ids." + str(pid) + ".json", shell = True)
    if callResult != 0:
        print "ERROR while reading objectIds for tenant " + tenantCode + ", skipping tenant"
        #unlock tenant
        callResult = call("mongo " + mongoConnectString + ''' --eval "''' + " var param1='" + tenantCode + "' " + ''' " '''  + " unlock_tenant.js ", shell = True)
        if callResult != 0:
            print "WARNING : tenant " + tenantCode + " unlocking FAILED"
        continue
    
    with open(outDir + "/object_ids." + str(pid) + ".json") as objectids_file:
        objectids = json.loads(objectids_file.read())
        
    precTS = objectids['lastTS']
    newTS = objectids['newTS']
    print "Old objectid: " + precTS + " ;  New objectid: " + newTS

    callResult = call("mongo " + mongoConnectString + " " + ''' --eval "''' + " var param1='" + tenantCode + "' " + ''' "  ../list_tenant_defaults.js > '''+ outDir + '''/tenant.''' + str(pid) + '''.json''', shell = True)
    if callResult != 0:
        print "ERROR while reading data for tenant " + tenantCode + ", skipping tenant"
        #unlock tenant 
        callResult = call("mongo " + mongoConnectString + ''' --eval "''' + " var param1='" + tenantCode + "' " + ''' " '''  + " unlock_tenant.js ", shell = True)
        if callResult != 0:
            print "WARNING : tenant " + tenantCode + " unlocking FAILED"
        continue
            
    with open(outDir + '/tenant.'  + str(pid) + '.json') as tenantdata_file:
        tenantData = json.loads(tenantdata_file.read())
    
    if('dataPhoenixSchemaName' not in tenantData.keys() and 'measuresPhoenixSchemaName' not in tenantData.keys() and 
       'mediaPhoenixSchemaName' not in tenantData.keys() and 'socialPhoenixSchemaName' not in tenantData.keys()):
        print "Tenant " + tenantCode + " does not exist on Phoenix yet, skipping tenant"
        #unlock tenant 
        callResult = call("mongo " + mongoConnectString + ''' --eval "''' + " var param1='" + tenantCode + "' " + ''' " '''  + " unlock_tenant.js ", shell = True)
        if callResult != 0:
            print "WARNING : tenant " + tenantCode + " unlocking FAILED"
        continue
    
    globalVars.init(tenantCode, tenantData)
    destFileList = []
            
    callResult = call("./check_not_empty_datasets.sh notEmptyDatasets." + str(pid) + ".csv " + globalVars.phoenixSchemaName['bulkDataset'] + " " + precTS + " " + outDir + " " + zookeeperQuorum, shell = True)
    if callResult != 0:
        print "ERROR while checking non-empty datasets for tenant " + tenantCode + ", skipping tenant"
        #unlock tenant 
        callResult = call("mongo " + mongoConnectString + ''' --eval "''' + " var param1='" + tenantCode + "' " + ''' " '''  + " unlock_tenant.js ", shell = True)
        if callResult != 0:
            print "WARNING : tenant " + tenantCode + " unlocking FAILED"
        continue
     
    notEmptyDatasets = ""
    with open(outDir + "/notEmptyDatasets." + str(pid) + ".csv") as csvfile:
        reader = csv.DictReader(csvfile, fieldnames=['idDataset', 'datasetVersion'])
        for row in reader:
            notEmptyDatasets += '{\\"idDataset\\": ' + row['idDataset'] +', \\"datasetVersion\\": ' + row['datasetVersion'] +'},'
        
        notEmptyDatasets = "[" + notEmptyDatasets[:-1] + "]"
    
    if notEmptyDatasets != "[]" :        
    
        tmpExportFolder = headFolder + "/" + tenantOrg + "/tmpCSVExport" + str(pid)
        callResult = call("hdfs dfs -mkdir -p " + tmpExportFolder, shell = True)
        if callResult != 0:
            print "ERROR while creating temp folder on HDFS for tenant " + tenantCode + ", skipping tenant"
            #unlock tenant 
            callResult = call("mongo " + mongoConnectString + ''' --eval "''' + " var param1='" + tenantCode + "' " + ''' " '''  + " unlock_tenant.js ", shell = True)
            if callResult != 0:
                print "WARNING : tenant " + tenantCode + " unlocking FAILED"
            continue
    
        print("mongo " + mongoConnectString + " " + ''' --eval "''' + " var param1='" + tenantCode + "';var param2='" + notEmptyDatasets + ''''"  list_dataset_conv.js > ''' + outDir + '''/lista_dataset.''' + str(pid) + ".json")
        
        callResult = call("mongo " + mongoConnectString + " " + ''' --eval "''' + " var param1='" + tenantCode + "';var param2='" + notEmptyDatasets + ''''"  list_dataset_conv.js > ''' + outDir + '''/lista_dataset.''' + str(pid) + ".json", shell = True)
        if callResult == 0:
            
            with open(outDir + "/lista_dataset." + str(pid) + ".json") as metadata_file:
                metadata = json.loads(metadata_file.read())
    
            exportJob = Pig.compileFromFile("""export_single_dataset.pig""")
            
            for m in metadata:
    
                subtype = m['configData']['subtype']
                dynamicPhoenixColumns = phoenixColumns = metadataFields = csvHeader = metadataHeader = ''
                
                for field in m['info']['fields']:
    
                    name = field['fieldName'].strip()
                    dataType = field['dataType'].lower()
                    
                    dynamicPhoenixColumns += '\\\"' + name + globalVars.dataTypeSuffixes[dataType] + '\\\"\ ' + globalVars.dataType2Phoenix[dataType] + ','
                    csvHeader += name + ','
                    
                    if globalVars.dataType2Pig[dataType] == 'datetime':
                        phoenixColumns += 'TO_CHAR(\\\"' + name + globalVars.dataTypeSuffixes[dataType] + '\\\"),'
                    else:
                        phoenixColumns += '\\\"' + name + globalVars.dataTypeSuffixes[dataType] + '\\\",'
                                        
                    if subtype == 'streamDataset': 
                        metadataFields += "'" + field['fieldAlias'] + "','" + field['measureUnit'] + "','" + dataType + "',"
                        metadataHeader += name + '_fieldAlias, ' + name + '_measureUnit, ' + name + '_dataType, '
                                      
                if subtype == 'streamDataset':
                    metadataFields = ", '" + m['virtualEntityName'].replace("'", "\\\\'") + "', " + metadataFields + str(m['info']['fps']) + ", '"
                    metadataHeader = ", Sensor_Name, " + metadataHeader + "Dataset_frequency, Dataset_Tags"
                                        
                    for tag in m['info']['tags']:
                        metadataFields += tag['tagCode'] + " "
                    
                    metadataFields += "'"
                                                                                                           
                exportConfig = {
                    'minObjectId' : precTS,
                    'maxObjectId' : newTS,
                    'idDataset_l' : m['idDataset'],
                    'datasetVersion_l' : m['datasetVersion'],
                    'phoenixSchema' : globalVars.phoenixSchemaName[subtype].upper(),
                    'phoenixTable' :  globalVars.phoenixTableName[subtype].upper(),
                    'phoenixColumns' : globalVars.phoenixExportColumns[subtype] + phoenixColumns[:-1].upper(),
                    'phoenixDynamicCol' : dynamicPhoenixColumns[:-1].upper(),
                    'allFields' : '$0 .. ' + metadataFields,
                    'csvHeader' : globalVars.pigSchemaExport[subtype] + csvHeader[:-1] + metadataHeader,
                    #'avroSchema' : avroSchema,
                    'outputFile' : tmpExportFolder + "/" + str(m['idDataset']) + "_" +  str(m['datasetVersion'])
                }
    
                exportResults = exportJob.bind(exportConfig).runSingle()
                if exportResults.isSuccessful():
                    
                    numRecords = exportResults.getRecordWritten()
                    if numRecords > 0:
                    
                        if subtype == "streamDataset" or subtype == "socialDataset":
                            destFolder = headFolder + "/" + tenantOrg + "/" + rawdataFolder + "/" + m['info']['dataDomain'] + "/so_" + m['virtualEntitySlug'] + "/" + m['streamCode']
                        else:
                            destFolder = headFolder + "/" + tenantOrg + "/" + rawdataFolder + "/" + m['info']['dataDomain'] + "/db_" + m['codSubDomain'] + "/" + str(m['datasetCode'])
                    
                        destFilename = newTS + "_" + str(numRecords) + "-" + str(m['datasetCode']) + "-" + str(m['datasetVersion']) + ".csv"
                                    
                        print "Creating folders on HDFS..."    
                        callResult = call("./create_export_folder.sh " + destFolder + " " + headFolder + " " + tenantOrg + " " + rawdataFolder + " " + m['info']['dataDomain'] + " '" + destFilename + "' " + m['info']['visibility'] + " " + tmpExportFolder + "/" + str(m['idDataset']) + "_" +  str(m['datasetVersion']), shell=True)
                        if callResult != 0:
                            destFileList.append(destFolder + "/" + destFilename)
                    
                    call("hdfs dfs -rmr " + tmpExportFolder + "/" + str(m['idDataset']) + "_" +  str(m['datasetVersion']), shell = True)                
                    print 'Dataset ' + str(m['datasetCode']) + "-" + str(m['datasetVersion']) + ' exported successfully'
                else:
                    print 'Dataset ' + str(m['datasetCode']) + "-" + str(m['datasetVersion']) + ' export failed'
               
        else:
            print "ERROR while reading datasets list for tenant " + tenantCode + ", skipping tenant"         
        
            call("hdfs dfs -rmr " + tmpExportFolder, shell = True)          
            
            # unlock tenant
            callResult = call("mongo " + mongoConnectString + ''' --eval "''' + " var param1='" + tenantCode + "' " + ''' " '''  + " unlock_tenant.js ", shell = True)
            if callResult != 0:
                print "WARNING : tenant " + tenantCode + " unlocking FAILED"
            
            continue 
    
        call("hdfs dfs -rmr " + tmpExportFolder, shell = True)       
    
    else:
        print "No new data for tenant " + tenantCode + ", skipping tenant" 
                     
    # aggiorno objectid
    callResult = call("mongo " + mongoConnectString + ''' --eval "''' + "var param1='" + tenantCode + "';var param2='" + newTS + "'" + '''" '''  + " update_objectid.js", shell = True)
    if callResult != 0:
        print "FATAL ERROR during last_objectid update for " + tenantCode + " tenant"
        print "Removing all files"
        call("hdfs dfs -rmr " + ' '.join(destFileList))
           
    # unlock tenant
    callResult = call("mongo " + mongoConnectString + ''' --eval "''' + " var param1='" + tenantCode + "' " + ''' " '''  + " unlock_tenant.js ", shell = True)
    if callResult != 0:
        print "WARNING : tenant " + tenantCode + " unlocking FAILED"

if os.path.exists(outDir + "/lista_dataset." + str(pid) + ".json"):
    os.remove(outDir + "/lista_dataset." + str(pid) + ".json")
if os.path.exists(outDir + "/lista_tenant_org." + str(pid) + ".json"):   
    os.remove(outDir + "/lista_tenant_org." + str(pid) + ".json")
if os.path.exists(outDir + "/object_ids." + str(pid) + ".json"):
    os.remove(outDir + "/object_ids." + str(pid) + ".json")
if os.path.exists(outDir + "/tenant." + str(pid) + ".json"):
    os.remove(outDir + "/tenant." + str(pid) + ".json") 
if os.path.exists(outDir + "/notEmptyDatasets." + str(pid) + ".csv"):
    os.remove(outDir + "/notEmptyDatasets." + str(pid) + ".csv")