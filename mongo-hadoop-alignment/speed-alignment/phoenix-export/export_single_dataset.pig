SET mapred.output.compress false;
SET output.compression.enabled false;
SET phoenix.query.dateFormat 'yyyy-MM-dd HH:mm:ss z';

data = LOAD 'hbase://query/SELECT $phoenixColumns FROM $phoenixSchema.$phoenixTable($phoenixDynamicCol) WHERE ID > \'$minObjectId\' and ID < \'$maxObjectId\' and IDDATASET_L = $idDataset_l and DATASETVERSION_L = $datasetVersion_l'                           
USING org.apache.phoenix.pig.PhoenixHBaseLoader('$zookeeperQuorum');

A = GROUP data BY 1 PARALLEL 1; 
B = FOREACH A GENERATE FLATTEN(data);
finalData = FOREACH B GENERATE $allFields;

STORE finalData INTO '$outputFile' USING org.apache.pig.piggybank.storage.CSVExcelStorageExtended(',','NO_MULTILINE', 'NOCHANGE', 'WRITE_OUTPUT_HEADER', '$csvHeader');
--STORE finalData INTO '$outputFile' USING org.apache.pig.piggybank.storage.avro.AvroStorage('$avroSchema');