#!/bin/bash
###########################################################
#	Sintax: upload_datalake.sh [prod][preprod]                 #
###########################################################

echo "`date +%H.%M.%S` ---- `date +'%a, %d %h %Y'` ----   Start procedure upload - param = $1 "
echo ""

# loading settings
source /home/sdp/allinea_yucca/config_hdfs_sdp.sh

###############################
######## UPLOAD SCRIPT ########
###############################

ERR="ERROR: environment to deploy not found (-prod | -preprod)"
if [ -z $1 ]; then
    echo $ERR
    exit
elif [ $1 == "prod" ]; then
    ORIGIN_FOLDER=$PROD_STAGE_FOLDER
    WASTE=$WASTE_PROD
    HEAD_FOLDER=$HEAD_PROD
elif [ $1 == "preprod" ]; then
    ORIGIN_FOLDER=$PREPROD_STAGE_FOLDER
    WASTE=$WASTE_PREPROD
    HEAD_FOLDER=$HEAD_PRE
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

# salva Internal Field Separator originale
OIFS=$IFS

# get all file in origin folder
for file in $ORIGIN_FOLDER/*
do
    f=$(basename $file)
    IFS="." read -a array <<< "$f"
    timestamp=${array[0]}
    tenant=${array[1]}
    organization=${array[2]}
    dataDomain=${array[3]}
    codSubDomain=${array[4]}
    collection=${array[5]}
    streamCode=${array[6]}
    veSlug=${array[7]}
    dataset=${array[8]}
    version=${array[9]}
    flagpriv=${array[10]}
	righeFile=${array[11]}
    fext=${array[12]}
	

    if [ $flagpriv = "private" ]; then
      dirper=750
      fileper=640
    elif [ $flagpriv = "public" ]; then
      dirper=750
      fileper=640
    fi

    if [[ $collection == "measures" || $collection == "social" ]]; then
      dest_folder="$HEAD_FOLDER/$organization/$RAWDATA_FOLDER/$dataDomain/so_${veSlug}/$streamCode"
    else
      #dest_folder="$HEAD_FOLDER/$organization/$RAWDATA_FOLDER/$dataDomain/db_${tenant}/$dataset"
      dest_folder="$HEAD_FOLDER/$organization/$RAWDATA_FOLDER/$dataDomain/db_${codSubDomain}/$dataset"
    fi
    dest_fn="$dest_folder/$timestamp""_$righeFile""_datalake-$dataset-$version.$fext"
	
	#remove old files
	hdfs dfs -rmr $dest_folder/*_datalake-$dataset-$version.$fext
    
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
      echo "move file into WASTE folder"
      mv $file $WASTE 
    else
      hdfs dfs -moveFromLocal $file $dest_fn
      hdfs dfs -chmod $dirper $dest_folder
      hdfs dfs -chmod $fileper $dest_fn
      echo "Uploaded file:   $dest_fn"
    fi
done

OIFS=$IFS
echo " *** `date +%H.%M.%S` ---- `date +'%a, %d %h %Y'` ***  upload successfully executed *** "

