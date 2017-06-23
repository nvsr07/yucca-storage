#!/bin/bash
dest_folder=$1
HEAD_FOLDER=$2
organization=$3
RAWDATA_FOLDER=$4
dataDomain=$5
dest_fn=$6
visibility=$7
tmp_folder=$8

# if destination folder not exist --> create it
hdfs dfs -test -d $dest_folder
if [ $? != "0" ]; then
	echo "Create destination folder $dest_folder"
	hdfs dfs -mkdir -p $dest_folder
	# imposta permessi su cartelle
	hdfs dfs -chmod 755 $HEAD_FOLDER/$organization
	hdfs dfs -chmod 755 $HEAD_FOLDER/$organization/$RAWDATA_FOLDER
	hdfs dfs -chmod 755 $HEAD_FOLDER/$organization/$RAWDATA_FOLDER/$dataDomain
	hdfs dfs -chmod 755 $HEAD_FOLDER/$organization/$RAWDATA_FOLDER/$dataDomain/*
fi

hdfs dfs -test -e $dest_fn
if [ $? = "0" ]; then
	echo "Destination file $dest_fn already exist"
	hdfs dfs -rm $tmp_folder/part-r-00000
	exit 1 
else
	
	if [ $visibility = "private" ]; then
      dirper=750
      fileper=640
    elif [ $visibility = "public" ]; then
      dirper=750
      fileper=640
    fi
	
	hdfs dfs -mv $tmp_folder/part-r-00000 $dest_folder/$dest_fn
	hdfs dfs -chmod $dirper $dest_folder
	hdfs dfs -chmod $fileper $dest_folder/$dest_fn

	exit 0
fi
