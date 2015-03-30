#!/bin/sh
#######################################################################
#	Sintax: contarecord.sh [prod|preprod] output_file [new|append     #
#######################################################################

#
# get all dataset in a given collection and call the compacting funcion
#

#
# compact a single dataset
#

function cyclingTenant(){
	
	# $1 = tenant
	# $2 = folder
	# $3 = collection
	
	fullpath="$1/$2/$3"
	
	echo "==== Analize Collection: $fullpath"

	
	hdfs dfs -du $fullpath | 
	while IFS= read -r dataset; do
		pos=`expr index "$dataset" "/"`
		pos2=`echo "$pos-1" |bc`
		dset=${dataset:$pos2}
		echo "===== Analyse dataset: $dset"
		contaDataset $dset $1 $2 $3
	done

}

function contaDataset(){
	# $1 = fullpath folder (dwh, output, share,...)
	# $2 = tenant
	# $3 = folder
	# $4 = collection
  echo "0" >tmp%%.tmp
  dsetname=$(basename $1)	
  hdfs dfs -du $1 | 
	while IFS= read -r filename; do
		pp=`expr index "$filename" "/"`
		pp2=`echo "$pp-1" |bc`
		fn=${filename:$pp2}
		fname=$(basename $fn)
		dirname=$(dirname $fn)
		
		#remove file extension
		IFS="." read -a array <<< "$fname"
		fileprefix=${array[0]}
		
		IFS="-" read -a array <<< "$fileprefix"

		timestamp=${array[0]}
		dataset=${array[1]}
		version=${array[2]}
		
		n=$(hdfs dfs -cat $fn | sed '1d' | wc -l)
		rectot=$(cat tmp%%.tmp) 
		rectot=$(expr $rectot + $n)
		echo "$rectot" >tmp%%.tmp
		echo "======== Processing file: $fname      nrec = $n    tot = $rectot"
	done
	rectot=$(cat tmp%%.tmp)
	echo "     Record in dataset: $dsetname  =  $rectot"
	echo "$globaltime, $2, $3, $4, $dsetname, $rectot" >> $outfilename
	rm tmp%%.tmp
}


# read and verify parameter: permitted value prod|preprod 

ERR="ERROR: environment to deploy not found (prod | preprod)"
if [ -z $1 ]; then
    echo $ERR
    exit
elif [ $1 = "prod" ]; then
    HEAD_FOLDER="/tenant"
elif [ $1 = "preprod" ]; then
    HEAD_FOLDER="/pre-tenant"
else
   echo $ERR
   exit
fi

if [ -z $2 ]; then
	echo "Insert destination filename as second parameter"
	exit
fi

if [ -z $3 ]; then
	echo "set the third parameter to new or append"
	exit
fi

if [ $3 = "new" ]; then
   echo "create new output file: $2"
   echo "TIME, TENANT, FOLDER, COLLECTION, DATASET, NUMREC" > $2
else
   echo "append data to output file: $2"
fi

outfilename=$2
globaltime=$(date +"%Y-%m-%dT%H:%M:%S")

# get all tenant in HEAD_FOLDER

hdfs dfs -du $HEAD_FOLDER | 
while IFS= read -r line; do
	p=`expr index "$line" "/"`
	p2=`echo "$p-1" |bc`
	d=${line:$p2}
	
    echo "======================================================================"
	echo "Analize Tenant: $d"
	echo "======================================================================"
	
	cyclingTenant $d "rawdata" "measures"
	cyclingTenant $d "rawdata" "data"
	cyclingTenant $d "rawdata" "media"
	cyclingTenant $d "rawdata" "social"
	cyclingTenant $d "share" "measures"
	cyclingTenant $d "share" "data"
	cyclingTenant $d "share" "media"
	cyclingTenant $d "share" "social"
	
done

