#!/bin/bash

phoenixSchema=$1
phoenixTable=$2
solrCollection=$3
idDataset=$4
datasetVersion=$5
origin=$6
zookeeperQuorum=$7
solrServer=$8

myPid=$$

echo "Deleting from Phoenix: "
echo "delete from $phoenixSchema.$phoenixTable where id in(select id from $phoenixSchema.$phoenixTable(ORIGIN_S VARCHAR) where iddataset_l = $idDataset and datasetversion_l = $datasetVersion and origin_s = '$origin');"

echo "delete from $phoenixSchema.$phoenixTable where id in(" > deletePhoenix.$idDataset.$datasetVersion.$myPid.sql
echo "select id from $phoenixSchema.$phoenixTable(ORIGIN_S VARCHAR) " >> deletePhoenix.$idDataset.$datasetVersion.$myPid.sql
echo "where iddataset_l = $idDataset and datasetversion_l = $datasetVersion and origin_s = '$origin');" >> deletePhoenix.$idDataset.$datasetVersion.$myPid.sql

/usr/hdp/current/phoenix-client/bin/psql.py $zookeeperQuorum:2181 deletePhoenix.$idDataset.$datasetVersion.$myPid.sql
rm deletePhoenix.$idDataset.$datasetVersion.$myPid.sql

echo "Deleting from Solr: "
echo "http://$solrServer:8983/solr/$solrCollection/update?stream.body=<delete><query>iddataset_l:$idDataset AND datasetversion_l:$datasetVersion AND origin_s:$origin</query></delete>&commit=true"

curl --negotiate -u : http://$solrServer:8983/solr/$solrCollection/update --data '<delete><query>iddataset_l:$idDataset AND datasetversion_l:$datasetVersion AND origin_s:$origin</query></delete>' -H 'Content-type:text/xml; charset=utf-8'
curl --negotiate -u : http://$solrServer:8983/solr/$solrCollection/update --data '<commit/>' -H 'Content-type:text/xml; charset=utf-8'