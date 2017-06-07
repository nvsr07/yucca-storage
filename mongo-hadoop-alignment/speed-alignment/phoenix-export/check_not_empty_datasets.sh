#!/bin/bash

outFile=$1
schema=$2
minId=$3
tmpDir=$4
zookeeperQuorum=$5
myPid=$$

echo "select iddataset_l, datasetversion_l from $schema.data " > $tmpDir/checkDataset.$myPid.sql
echo "where id > '$minId' group by iddataset_l, datasetversion_l having count(*) > 0;" >> $tmpDir/checkDataset.$myPid.sql

echo "select iddataset_l, datasetversion_l from $schema.measures " >> $tmpDir/checkDataset.$myPid.sql
echo "where id > '$minId' group by iddataset_l, datasetversion_l having count(*) > 0;" >> $tmpDir/checkDataset.$myPid.sql

echo "select iddataset_l, datasetversion_l from $schema.media " >> $tmpDir/checkDataset.$myPid.sql
echo "where id > '$minId' group by iddataset_l, datasetversion_l having count(*) > 0;" >> $tmpDir/checkDataset.$myPid.sql

echo "select iddataset_l, datasetversion_l from $schema.social " >> $tmpDir/checkDataset.$myPid.sql
echo "where id > '$minId' group by iddataset_l, datasetversion_l having count(*) > 0;" >> $tmpDir/checkDataset.$myPid.sql

/usr/hdp/current/phoenix-client/bin/psql.py $zookeeperQuorum:2181 $tmpDir/checkDataset.$myPid.sql > $tmpDir/$myPid.tmp.txt 

cat $tmpDir/$myPid.tmp.txt | grep -v '\-\-\-' | grep -v 'IDDATASET_L' | grep -v 'Time' | grep -v 'no rows' | sed '/^\s*$/d' | sed 's/\s\+/,/g' | cut -d"," -f2,3 > $tmpDir/$outFile
rm $tmpDir/$myPid.tmp.txt
rm $tmpDir/checkDataset.$myPid.sql