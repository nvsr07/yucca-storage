#!/bin/bash
dest_folder=$1
HEAD_FOLDER=$2
organization=$3
RAWDATA_FOLDER=$4
dataDomain=$5
dest_fn=$6

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
	#echo "Destination file $dest_fn already exist"
	#echo "move file into WASTE folder"
	#mv $file $WASTE
	exit 1 
else
	#hdfs dfs -moveFromLocal $file $dest_fn
	#hdfs dfs -chmod $dirper $dest_folder
	#hdfs dfs -chmod $fileper $dest_fn
	#echo "Uploaded file:   $dest_fn"
	exit 0
fi