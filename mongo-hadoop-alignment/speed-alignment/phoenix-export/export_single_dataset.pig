SET default_parallel 1

data = LOAD 'hbase://query/SELECT $phoenixColumns FROM $phoenixSchema.$phoenixTable($phoenixDynamicCol) WHERE ID > \'$minObjectId\' and ID < \'$maxObjectId\' and IDDATASET_L = $idDataset_l and DATASETVERSION_L = $datasetVersion_l'                           
USING org.apache.phoenix.pig.PhoenixHBaseLoader('$zookeeperQuorum') as ($pigSchema);

STORE data INTO '$outputFile' USING org.apache.pig.piggybank.storage.CSVExcelStorage(',');