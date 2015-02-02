#!/bin/sh
###############################################################################
# Questo script crea su HDFS un TENANT sia in produzione che in preproduzione #
# Lo script è stato creatos SOLO a fini di test per avere un ambiente locale  #
# su cui testare gli script di upload da mongoDB.                             #
# Lo script esegue:													          #
#	- creazione di tutte le cartelle del tenant                               #
#	- assegnazione delle utenze corrette alle singole cartelle				  #
#	- assegnazione dei permessi di accesso alle singole cartelle			  #
#																			  #
#																			  #
# Lo script suppone che esistano le cartelle /tenant e /pre-tenant su HDFS    #
# e i relativi gruppi e permessi											  #
#																			  #
###############################################################################
#																			  #
#	USO: ./creaTenant.sh <nometenant> prod|preprod							  #
#	es.  ./creaTenant csi prod											      #
#																			  #
###############################################################################

ERR="ERROR: environment to deploy not found (prod | preprod)"

# standard error redirection (only for this script)

if [ -z $2 ]; then
    echo $ERR
    exit
elif [ $2 = "prod" ]; then
    TF="/tenant"
    US="prod-$1"
    GP="tnt-$1"
elif [ $2 = "preprod" ]; then
    TF="/pre-tenant"
    US="preprod-$1"
    GP="pretnt-$1"
else
   echo $ERR
   exit
fi



# creazione delle cartelle su HDFS

echo "Create folder tenant tnt-$1 "
hdfs dfs -mkdir $TF/tnt-$1
echo "Create folder RAWDATA"
hdfs dfs -mkdir $TF/tnt-$1/rawdata
echo "Create folder SCRIPTS"
hdfs dfs -mkdir $TF/tnt-$1/scripts
echo "Create folder RAWDATA/DATA"
hdfs dfs -mkdir $TF/tnt-$1/rawdata/data
echo "Create folder RAWDATA/MEASURES"
hdfs dfs -mkdir $TF/tnt-$1/rawdata/measures
echo "Create folder RAWDATA/MEDIA"
hdfs dfs -mkdir $TF/tnt-$1/rawdata/media
echo "Create folder RAWDATA/SOCIAL"
hdfs dfs -mkdir $TF/tnt-$1/rawdata/social
echo "Create folder DWH"
hdfs dfs -mkdir $TF/tnt-$1/dwh
echo "Create folder OUTPUT"
hdfs dfs -mkdir $TF/tnt-$1/output
echo "Create folder STAGING"
hdfs dfs -mkdir $TF/tnt-$1/staging
echo "Create folder TEMP"
hdfs dfs -mkdir $TF/tnt-$1/temp
echo "Create folder SHARE"
hdfs dfs -mkdir $TF/tnt-$1/share
echo "Create folder SHARE/DATA"
hdfs dfs -mkdir $TF/tnt-$1/share/data
echo "Create folder SHARE/MEASURES"
hdfs dfs -mkdir $TF/tnt-$1/share/measures
echo "Create folder SHARE/MEDIA"
hdfs dfs -mkdir $TF/tnt-$1/share/media
echo "Create folder SHARE/SOCIAL"
hdfs dfs -mkdir $TF/tnt-$1/share/social

# assegnazione delle utenze in base alle specifiche

echo "Assiging user and role to folder"
hdfs dfs -chown -R "$US:$GP" $TF/tnt-$1
hdfs dfs -chown -R "hive:$GP" $TF/tnt-$1/share
hdfs dfs -chown -R "hive:$GP" $TF/tnt-$1/temp
hdfs dfs -chown -R "hive:$GP" $TF/tnt-$1/staging
hdfs dfs -chmod -R 770 $TF/tnt-$1
hdfs dfs -chmod -R 774 $TF/tnt-$1/share

echo "Creation of tenant $1 in $2 completed"
