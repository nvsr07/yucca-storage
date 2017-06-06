#!/bin/sh
###########################################################################
# Questo script crea su HDFS un TENANT o in produzione o in preproduzione #
#                                                                         #
# Lo script esegue la creazione delle cartelle per l'organizzazione e     #
# delle cartelle dell'area STAGE del tenant indicato                      #
#                                                                         #
# Lo script suppone che esistano le cartelle /datalake e /int-datalake    #
# su HDFS e che l'utente possa scrivere all'interno di questi path        #
###########################################################################
#                                                                         #
# USO: ./creaTenant.sh <organizzazione> <nometenant> prod|preprod         #
# es.  ./creaTenant.sh RP csi_ambiente prod|preprod                       #
#                                                                         #
###########################################################################

echo `date +%Y%m%d%H%M%S`" lancio creaTenant.sh" >> /home/sdp/scripts/log/creaTen

ERR="USAGE: creaTenant.sh <organizzazione> <nometenant> prod|preprod"

# ottiene ticket kerberos
kinit -kt /etc/security/keytabs/sdp.service.keytab -p sdp/sdnet-master3.sdp.csi.i

if [ -z $3 ]; then
    echo $ERR
    exit
elif [ $3 = "prod" ]; then
    TF="/datalake"
    TN=$2
    OG=$1
elif [ $3 = "preprod" ]; then
    TF="/int-datalake"
    TN=$2
    OG=$1
else
    echo $ERR
    exit
fi

# creazione delle cartelle su HDFS
echo "Create folders"
hdfs dfs -mkdir -p $TF/$OG/rawdata
hdfs dfs -mkdir -p $TF/$OG/dataset
hdfs dfs -mkdir -p $TF/$OG/dataset_transformed
hdfs dfs -mkdir -p $TF/$OG/job_transformation
hdfs dfs -mkdir -p $TF/$OG/job_ingestion
hdfs dfs -mkdir -p $TF/$OG/streams
#hdfs dfs -chmod -R 755 $TF/$OG
hdfs dfs -chmod 755 $TF/$OG/*
echo "Create stage folders"
hdfs dfs -mkdir -p $TF/$OG/dataset_transformed/STAGE/$TN/db_stage_${OG}_${TN}
hdfs dfs -mkdir -p $TF/$OG/dataset_transformed/STAGE/$TN/files
hdfs dfs -chmod 755 $TF/$OG/dataset_transformed/STAGE
#hdfs dfs -chmod -R 750 $TF/$OG/dataset_transformed/STAGE/$TN
hdfs dfs -chmod 750 $TF/$OG/dataset_transformed/STAGE/$TN
hdfs dfs -chmod 750 $TF/$OG/dataset_transformed/STAGE/$TN/*

echo "Creation of tenant $TN completed"