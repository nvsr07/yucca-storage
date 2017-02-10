set pig.datetime.default.tz '+00:00'

data = LOAD 'hbase://query/SELECT $phoenixColumns FROM $phoenixSchema.$phoenixTable($phoenixDynamicColumns) WHERE ID > \'$minObjectId\' and ID < \'$maxObjectId\' and $query' 
USING org.apache.phoenix.pig.PhoenixHBaseLoader('$zookeeperQuorum') as ($pigSchema);

STORE data INTO 'hbase://$phoenixSchema.$phoenixTable/$phoenixUpsertColumns'
USING it.csi.yucca.phoenix.pig.YuccaPhoenixHBaseStorage('$zookeeperQuorum', '-batchSize 1000');