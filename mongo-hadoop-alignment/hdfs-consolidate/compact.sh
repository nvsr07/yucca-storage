#!/bin/sh
###########################################################
#	Sintax: compact.sh [prod][preprod]                    #
###########################################################
#
# NOTA:
# durante le operazioni di compattazione, l'archivio dei file è inconsistente
# è necessario non eseguire attività di analytics durante la compattazione.
################################################################################

#
# get all dataset in a given collection and call the compacting funcion
#

#
# compact a single dataset
#

function cyclingTenant(){
	echo "======================================================================"
	echo "COLLECTION: $1"
	echo "======================================================================"
	
	hdfs dfs -du $1 | 
	while IFS= read -r dataset; do
		pos=`expr index "$dataset" "/"`
		pos2=`echo "$pos-1" |bc`
		dset=${dataset:$pos2}
		echo "Compact dataset: $dset"
		compactDataset $dset

	done

}

function compactDataset(){

	hdfs dfs -du $1 | 
	while IFS= read -r filename; do
		pp=`expr index "$filename" "/"`
		pp2=`echo "$pp-1" |bc`
		fn=${filename:$pp2}
		fname=$(basename $fn)
		dirname=$(dirname $fn)
		echo "Processing file: $fname"
		
		#remove file extension
		IFS="." read -a array <<< "$fname"
		fileprefix=${array[0]}
		
		IFS="-" read -a array <<< "$fileprefix"

		timestamp=${array[0]}
		dataset=${array[1]}
		version=${array[2]}
		
		# manage only non compacted files
		if [ $timestamp != "comp" ]; then
			
			compfile="$dirname/comp-$dataset-$version.csv"
			
			hdfs dfs -test -e $compfile
	
			if [ $? = "0" ]; then	
				# if exist a compacted file, append the new file
				echo "append file $fname"
				hdfs dfs -cat $fn | sed '1d' | hdfs dfs -appendToFile - $compfile
				if [ $? = 0 ]; then
					echo "delete old file: $fn"
					hdfs dfs -rm $fn
				else
					echo "error to append file: $fn"
				fi
			else
				# else rename the file to compacted file
				echo "Rename $fn  to $compfile"
				hdfs dfs -mv $fn $compfile
			fi
		fi
		
		

	done
		
		

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

# get all tenant in HEAD_FOLDER

hdfs dfs -du $HEAD_FOLDER | 
while IFS= read -r line; do
	p=`expr index "$line" "/"`
	p2=`echo "$p-1" |bc`
	d=${line:$p2}
	
	# get all dataset in every tenant
	cyclingTenant "$d/rawdata/measures"
	cyclingTenant "$d/rawdata/data"
	cyclingTenant "$d/rawdata/media"
	cyclingTenant "$d/rawdata/social"
	cyclingTenant "$d/share/measures"
	cyclingTenant "$d/share/data"
	cyclingTenant "$d/share/media"
	cyclingTenant "$d/share/social"
done

