#!/bin/bash

phoenixSchema=$1
phoenixTable=$2
solrCollection=$3
idDataset=$4
datasetVersion=$5
origin=$6
zookeeperQuorum=$7
solrServer=$8

phoenix-sqlline $zookeeperQuorum:2181 <<EOF
delete from $phoenixSchema.$phoenixTable(ORIGIN_S VARCHAR) where iddataset_l = $idDataset and datasetversion_l = $datasetVersion and origin_s = '$origin'; 
EOF

curl --negotiate -u : "http://$solrServer:8983/solr/$solrCollection/update?stream.body=<delete><query>iddataset_l:$idDataset and datasetversion_l:$datasetVersion and origin_s:$origin</query></delete>&commit=true"
