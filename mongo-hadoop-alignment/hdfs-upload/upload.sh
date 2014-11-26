#!/bin/sh
###########################################################
#	Sintax: upload.sh [-prod][-preprod]             #
###########################################################


# loading settings
source /home/hdfs/config.sh

###############################
######## UPLOAD SCRIPT ########
###############################

ERR="ERROR: environment to deploy not found (-prod | -preprod)"

# standard error redirection (only for this script)

if [ -z $1 ]; then
    echo $ERR
elif [ $1 = "-prod" ]; then
    ORIGIN_FOLDER=$PROD_STAGE_FOLDER
    USER_PREF="prod-"
    GROUP_PREF="tnt-"
    HEAD_FOLDER=$HEAD_PROD
    LOGDIR=$LOGPRO
elif [ $1 = "-preprod" ]; then
    ORIGIN_FOLDER=$PREPROD_STAGE_FOLDER
    USER_PREF="preprod-"
    GROUP_PREF="pretnt-"
    HEAD_FOLDER=$HEAD_PRE
    LOGDIR=$LOGPRE
else
   echo $ERR
fi

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
    tenant=${array[1]}
    collection=${array[2]}
    dataset=${array[3]}
    version=${array[4]}
    flagpriv=${array[5]}
    fext=${array[6]}
    huser=$USER_PREF$tenant
    hgroup=$GROUP_PREF$tenant
    
    if [ $flagpriv = "pri" ]; then
		dest_folder="$HEAD_FOLDER/$tenant/$RAWDATA_FOLDER/$collection/$dataset"
		dirper=770
		fileper=660
	elif [ $flagpriv = "pub" ]; then
		dest_folder="$HEAD_FOLDER/$tenant/$SHARE_FOLDER/$collection/$dataset"
		dirper=776
		fileper=664
	fi
	
	
	dest_fn="$dest_folder/$timestamp-$dataset-$version.$fext"
    
    #logfile="$LOGDIR/$tenant.log"
    
    # if destination folder not exist --> create it
	
	hdfs dfs -test -d $dest_folder
	
	if [ $? != "0" ]; then
		echo "Create destination folder $dest_folder"
		#echo "Create destination folder $dest_folder" >> $logfile
		hdfs dfs -mkdir $dest_folder
		hdfs dfs -chown "$huser:$hgroup" $dest_folder
		hdfs dfs -chmod $dirper $dest_folder
	fi
	hdfs dfs -moveFromLocal $file $dest_fn
	hdfs dfs -chown "$huser:$hgroup" $dest_fn
	hdfs dfs -chmod $fileper $dest_fn
	echo "Upload file:   $dest_fn"
	#echo "Upload file:   $dest_fn" >> $logfile
done

OIFS=$IFS








