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

Pig.registerJar("/usr/hdp/current/phoenix-client/phoenix-client.jar")
Pig.registerJar("/usr/hdp/current/pig-client/piggybank.jar")

if len(sys.argv) != 2:
    print "Usage: " + sys.argv[0] + " parameters-file"
    sys.exit()

props = util.Properties()
#add try catch for this
propertiesfis = javaio.FileInputStream(sys.argv[1])
props.load(propertiesfis)

pid = os.getpid()
outDir = props.getProperty('tmpFolder')
headFolder = props.getProperty('headFolder')
wasteFolder = props.getProperty('wasteFolder')
rawdataFolder = props.getProperty('rawdataFolder')

if not os.path.exists(outDir):
    os.makedirs(outDir)

mongoConnectString = props.getProperty('mongoHost') + ":" + props.getProperty('mongoPort') + "/DB_SUPPORT"
mongoConnectString += " -u " + props.getProperty('mongoUsr')
mongoConnectString += " -p " + props.getProperty('mongoPwd') + ''' --authenticationDatabase admin  --quiet '''

callResult = call("mongo " + mongoConnectString + " ../list_tenants.js > " + outDir + "/lista_tenant_org." + str(pid) + ".json", shell = True)
if callResult != 0:
    print "FATAL ERROR during tenants list creation"
    sys.exit(1)
    
with open(outDir + "/lista_tenant_org." + str(pid) + ".json") as tenant_file:
    allTenants = json.loads(tenant_file.read())
    
for tenant in allTenants:
    tenantCode = tenant["tenantCode"]
    tenantOrg = tenant["organizationCode"]
    
    print "Execute export for tenant " + tenantCode + " - organization " + tenantOrg
    
    # locka tenant
    callResult = call("mongo " + mongoConnectString + ''' --eval "''' + " var param1='" + tenantCode + "' " + ''' " '''  + " lock_tenant.js ", shell = True)
    if callResult != 0:
        print "FATAL ERROR during " + tenantCode + " tenant locking"
        sys.exit(1)
    
    # legge vecchio e nuovo objectid
    callResult = call("mongo " + mongoConnectString + ''' --eval "''' + " var param1='" + tenantCode + "' " + ''' " '''  + " read_objectid.js > " + outDir + "/object_ids." + str(pid) + ".json", shell = True)
    if callResult != 0:
        print "FATAL ERROR while reading objectIds for tenant " + tenantCode
        sys.exit(1)
    
    with open(outDir + "/object_ids." + str(pid) + ".json") as objectids_file:
        objectids = json.loads(objectids_file.read())
        
    precTS = objectids['lastTS']
    newTS = objectids['newTS']
    print "Old objectid: " + precTS + " ;  New objectid: " + newTS

    callResult = call("mongo " + mongoConnectString + " " + ''' --eval "''' + " var param1='" + tenantCode + "' " + ''' "  ../list_tenant_defaults.js > '''+ outDir + '''/tenant.''' + str(pid) + '''.json''', shell = True)
    if callResult != 0:
        print "FATAL ERROR while reading data for tenant " + tenantCode
        sys.exit(1)
    
    with open(outDir + '/tenant.'  + str(pid) + '.json') as tenantdata_file:
        tenantData = json.loads(tenantdata_file.read())
    
    if('dataPhoenixSchemaName' not in tenantData.keys() and 'measuresPhoenixSchemaName' not in tenantData.keys() and 
       'mediaPhoenixSchemaName' not in tenantData.keys() and 'socialPhoenixSchemaName' not in tenantData.keys()):
        print "Skipping tenant " + tenantCode + " because is still on MongDB "
        continue
    
    globalVars.init(tenantCode, tenantData)
     
    callResult = call("mongo " + mongoConnectString + " " + ''' --eval "''' + " var param1='" + tenantCode + "' " + ''' "  list_dataset_conv.js > ''' + outDir + '''/lista_dataset.''' + str(pid) + ".json", shell = True)
    if callResult == 0:
        
        with open(outDir + "/lista_dataset." + str(pid) + ".json") as metadata_file:
            metadata = json.loads(metadata_file.read())

        exportJob = Pig.compileFromFile("""export_single_dataset.pig""")
        destFileList = []

        for m in metadata:

            subtype = m['configData']['subtype']
            dynamicPigSchema = dynamicPhoenixColumns = phoenixColumns = ''
            
            for field in m['info']['fields']:

                name = field['fieldName']
                dataType = field['dataType']

                dynamicPigSchema +=  ', ' + name + globalVars.dataTypeSuffixes[dataType] + ':' + globalVars.dataType2Pig[dataType]
                phoenixColumns += ',' + name + globalVars.dataTypeSuffixes[dataType] 
                dynamicPhoenixColumns += name + globalVars.dataTypeSuffixes[dataType] + '\ ' + globalVars.dataType2Phoenix[dataType] + ','
                        
            flagpriv = m['info']['visibility']
            if flagpriv == "private":
                dirper = 750
                fileper = 640
            elif flagpriv == "public":
                dirper = 750
                fileper = 640
                
            if subtype == "measures" or subtype == "social":
                destFolder = headFolder + "/" + tenantOrg + "/" + rawdataFolder + "/" + m['info']['dataDomain'] + "/so_" + m['virtualEntitySlug'] + "/" + m['streamCode']
            else:
                destFolder = headFolder + "/" + tenantOrg + "/" + rawdataFolder + "/" + m['info']['dataDomain'] + "/db_" + m['codSubDomain'] + "/" + str(m['datasetCode'])
    
            destFn = destFolder + "/" + newTS + "-" + str(m['datasetCode']) + "-" + str(m['datasetVersion'])
            destFileList.append(destFn)
            
            print "Creating folders on HDFS..."    
            callResult = call("./create_export_folder.sh " + destFolder + " " + headFolder + " " + tenantOrg + " " + rawdataFolder + " " + m['info']['dataDomain'] + " '" + destFn + "'", shell=True)
            if callResult != 0:
                destFn = wasteFolder + "/" + newTS + "-" + str(m['datasetCode']) + "-" + str(m['datasetVersion'])
                                                           
            exportConfig = {
                'minObjectId' : precTS,
                'maxObjectId' : newTS,
                'idDataset_l' : m['idDataset'],
                'datasetVersion_l' : m['datasetVersion'],
                'pigSchema' : globalVars.pigSchema[subtype] + dynamicPigSchema,
                'phoenixSchema' : globalVars.phoenixSchemaName[subtype].upper(),
                'phoenixTable' :  globalVars.phoenixTableName[subtype].upper(),
                'phoenixColumns' : globalVars.phoenixColumns[subtype] + phoenixColumns.upper(),
                'phoenixDynamicCol' : dynamicPhoenixColumns[:-1].upper(),
                'outputFile' : destFn
            }

            exportResults = exportJob.bind(exportConfig).runSingle()
            if exportResults.isSuccessful():
                call("hdfs dfs -chmod " + str(dirper) + " " + destFolder, shell = True)
                call("hdfs dfs -chmod " + str(fileper) + " " + destFn, shell = True)
                print 'Dataset exported successfully'
            else:
                print 'Dataset export failed'
               
    else:
        print "FATAL ERROR while reading datasets list for tenant " + tenantCode 
        sys.exit(1) #si dovrebbe gestire meglio questa situazione       
                  
    # aggiorno objectid
    callResult = call("mongo " + mongoConnectString + ''' --eval "''' + " var param1='" + tenantCode + "' var param2='" + newTS + "' " + ''' " '''  + " update_objectid.js", shell = True)
    if callResult != 0:
        print "FATAL ERROR during last_objectid update for " + tenantCode + " tenant"
        print "Removing all files"
        call("hdfs dfs -rmr " + ' '.join(destFileListd))
        sys.exit(1)
           
    # unlock tenant
    callResult = call("mongo " + mongoConnectString + ''' --eval "''' + " var param1='" + tenantCode + "' " + ''' " '''  + " unlock_tenant.js ", shell = True)
    if callResult != 0:
        print "WARNING : tenant " + tenantCode + " unlocking FAILED"
        sys.exit(1)

os.remove(outDir + "/lista_dataset." + str(pid) + ".json")   
os.remove(outDir + "/lista_tenant_org." + str(pid) + ".json")
os.remove(outDir + "/object_ids." + str(pid) + ".json")
os.remove(outDir + "/tenant." + str(pid) + ".json") 