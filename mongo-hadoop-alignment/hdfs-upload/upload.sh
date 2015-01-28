#!/bin/sh
###########################################################
#	Sintax: upload.sh [prod][preprod]                 #
###########################################################


# loading settings
source /home/oozie/scripts/config.sh

###############################
######## UPLOAD SCRIPT ########
###############################

ERR="ERROR: environment to deploy not found (-prod | -preprod)"

# standard error redirection (only for this script)

if [ -z $1 ]; then
    echo $ERR
    exit
elif [ $1 = "prod" ]; then
    ORIGIN_FOLDER=$PROD_STAGE_FOLDER
    USER_PREF="prod-"
    GROUP_PREF="tnt-"
    HEAD_FOLDER=$HEAD_PROD
    LOGDIR=$LOGPRO
    WASTE=$WASTE_PROD
elif [ $1 = "preprod" ]; then
    ORIGIN_FOLDER=$PREPROD_STAGE_FOLDER
    USER_PREF="preprod-"
    GROUP_PREF="pretnt-"
    HEAD_FOLDER=$HEAD_PRE
    LOGDIR=$LOGPRE
    WASTE=$WASTE_PREPROD
else
   echo $ERR
   exit
fi

echo "WASTE DIR is $WASTE"
# if the origin folder is void --> exit

n="$(ls $ORIGIN_FOLDER | wc -c)"

if [ $n = "0" ]; then
	echo "The origin folder is void"
	exit
fi

# get all file in origin folder

OIFS=$IFS
echo $IFS

for file in $ORIGIN_FOLDER/*;
do
    f=$(basename $file)
    IFS="." read -a array <<< "$f"
    timestamp=${array[0]}
    tnt=${array[1]}
    tenant="tnt-$tnt"
    collection=${array[2]}
    dataset=${array[3]}
    version=${array[4]}
    flagpriv=${array[5]}
    fext=${array[6]}
    hgroup=$GROUP_PREF$tnt
    
    if [ $flagpriv = "private" ]; then
		dest_folder="$HEAD_FOLDER/$tenant/$RAWDATA_FOLDER/$collection/$dataset"
		dirper=770
		fileper=660
		huser=$USER_PREF$tnt
	elif [ $flagpriv = "public" ]; then
		dest_folder="$HEAD_FOLDER/$tenant/$SHARE_FOLDER/$collection/$dataset"
		dirper=776
		fileper=664
		huser=hive
	fi
	
	
	dest_fn="$dest_folder/$timestamp-$dataset-$version.$fext"
    
    
    # if destination folder not exist --> create it
	
	hdfs dfs -test -d $dest_folder
	
	if [ $? != "0" ]; then
		echo "Create destination folder $dest_folder"
		hdfs dfs -mkdir $dest_folder
		hdfs dfs -chown "$huser:$hgroup" $dest_folder
		hdfs dfs -chmod $dirper $dest_folder
	fi

	hdfs dfs -test -e $dest_fn

	if [ $? = "0" ]; then
		echo "Destination file $dest_fn already exist"
		echo "move file into WASTE folder"
		mv $file $WASTE 
	else
	        hdfs dfs -moveFromLocal $file $dest_fn
	        hdfs dfs -chown "$huser:$hgroup" $dest_fn
	        hdfs dfs -chmod $fileper $dest_fn
	        echo "Upload file:   $dest_fn"
        fi
done

OIFS=$IFS








