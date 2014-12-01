#!/bin/sh
###############################################################################
# Questo script crea su HDFS un TENANT sia in produzione che in preproduzione #
# Lo script è stato creatos SOLO a fini di test per avere un ambiente locale  #
# su cui testare gli script di upload da mongoDB.                             #
# Lo script esegue:													          #
#	- creazione di tutte le cartelle del tenant sia in prod che in preprod	  #
#	- assegnazione delle utenze corrette alle singole cartelle				  #
#	- assegnazione dei permessi di accesso alle singole cartelle			  #
#																			  #
# ATTENZIONE: essendo stato creato per scopi di test LOCALE... lo script non  #
#             esegue nessun controllo di coerenza dei tenant e, soprattutto,  #
#			  quando esegue la creazione di un tenant, se questo esiste, ne   #
#			  cancella tutti i contenuti.									  #
#																			  #
# Lo script suppone che esistano le cartelle /tenant e /pre-tenant su HDFS    #
#																			  #
###############################################################################
#																			  #
#	USO: ./creaTenant.sh <nometenant>										  #
#	es.  ./craTenant csi													  #
#																			  #
###############################################################################

# creazione delle cartelle su HDFS

#hdfs dfs -rm -r /tenant/$1
#hdfs dfs -rm -r /pre-tenant/$1
hdfs dfs -mkdir /tenant/$1
hdfs dfs -mkdir /pre-tenant/$1
hdfs dfs -mkdir /tenant/$1/rawdata
hdfs dfs -mkdir /pre-tenant/$1/rawdata
hdfs dfs -mkdir /tenant/$1/rawdata/data
hdfs dfs -mkdir /pre-tenant/$1/rawdata/data
hdfs dfs -mkdir /tenant/$1/rawdata/measures
hdfs dfs -mkdir /pre-tenant/$1/rawdata/measures
hdfs dfs -mkdir /tenant/$1/rawdata/media
hdfs dfs -mkdir /pre-tenant/$1/rawdata/media
hdfs dfs -mkdir /tenant/$1/rawdata/social
hdfs dfs -mkdir /pre-tenant/$1/rawdata/social
hdfs dfs -mkdir /tenant/$1/dwh
hdfs dfs -mkdir /pre-tenant/$1/dwh
hdfs dfs -mkdir /tenant/$1/output
hdfs dfs -mkdir /pre-tenant/$1/output
hdfs dfs -mkdir /tenant/$1/staging
hdfs dfs -mkdir /pre-tenant/$1/staging
hdfs dfs -mkdir /tenant/$1/temp
hdfs dfs -mkdir /pre-tenant/$1/temp
hdfs dfs -mkdir /tenant/$1/share
hdfs dfs -mkdir /pre-tenant/$1/share
hdfs dfs -mkdir /tenant/$1/share/data
hdfs dfs -mkdir /pre-tenant/$1/share/data
hdfs dfs -mkdir /tenant/$1/share/measures
hdfs dfs -mkdir /pre-tenant/$1/share/measures
hdfs dfs -mkdir /tenant/$1/share/media
hdfs dfs -mkdir /pre-tenant/$1/share/media
hdfs dfs -mkdir /tenant/$1/share/social
hdfs dfs -mkdir /pre-tenant/$1/share/social

# assegnazione delle utenze in base alle specifiche

hdfs dfs -chown "hdfs:hdfs" /tenant
hdfs dfs -chown "hdfs:hdfs" /pre-tenant
hdfs dfs -chown "prod-$1:tnt-$1" /tenant/$1
hdfs dfs -chown "preprod-$1:pretnt-$1" /pre-tenant/$1
hdfs dfs -chown "prod-$1:tnt-$1" /tenant/$1/rawdata
hdfs dfs -chown "preprod-$1:pretnt-$1" /pre-tenant/$1/rawdata
hdfs dfs -chown "prod-$1:tnt-$1" /tenant/$1/rawdata/data
hdfs dfs -chown "preprod-$1:pretnt-$1" /pre-tenant/$1/rawdata/data
hdfs dfs -chown "prod-$1:tnt-$1" /tenant/$1/rawdata/measures
hdfs dfs -chown "preprod-$1:pretnt-$1" /pre-tenant/$1/rawdata/measures
hdfs dfs -chown "prod-$1:tnt-$1" /tenant/$1/rawdata/media
hdfs dfs -chown "preprod-$1:pretnt-$1" /pre-tenant/$1/rawdata/media
hdfs dfs -chown "prod-$1:tnt-$1" /tenant/$1/rawdata/social
hdfs dfs -chown "preprod-$1:pretnt-$1" /pre-tenant/$1/rawdata/social
hdfs dfs -chown "prod-$1:tnt-$1" /tenant/$1/dwh
hdfs dfs -chown "preprod-$1:pretnt-$1" /pre-tenant/$1/dwh
hdfs dfs -chown "prod-$1:tnt-$1" /tenant/$1/output
hdfs dfs -chown "preprod-$1:pretnt-$1" /pre-tenant/$1/output
hdfs dfs -chown "prod-$1:tnt-$1" /tenant/$1/staging
hdfs dfs -chown "hive:pretnt-$1" /pre-tenant/$1/staging
hdfs dfs -chown "hive:tnt-$1" /tenant/$1/temp
hdfs dfs -chown "hive:pretnt-$1" /pre-tenant/$1/temp
hdfs dfs -chown "hive:tnt-$1" /tenant/$1/share
hdfs dfs -chown "hive:pretnt-$1" /pre-tenant/$1/share
hdfs dfs -chown "hive:tnt-$1" /tenant/$1/share/data
hdfs dfs -chown "hive:pretnt-$1" /pre-tenant/$1/share/data
hdfs dfs -chown "hive:tnt-$1" /tenant/$1/share/measures
hdfs dfs -chown "hive:pretnt-$1" /pre-tenant/$1/share/measures
hdfs dfs -chown "hive:tnt-$1" /tenant/$1/share/media
hdfs dfs -chown "hive:pretnt-$1" /pre-tenant/$1/share/media
hdfs dfs -chown "hive:tnt-$1" /tenant/$1/share/social
hdfs dfs -chown "hive:pretnt-$1" /pre-tenant/$1/share/social

# assegnazione dei permessi in base alle specifiche

hdfs dfs -chmod 777 /tenant
hdfs dfs -chmod 777 /pre-tenant
hdfs dfs -chmod 770 /tenant/$1
hdfs dfs -chmod 770 /pre-tenant/$1
hdfs dfs -chmod 770 /tenant/$1/rawdata
hdfs dfs -chmod 770 /pre-tenant/$1/rawdata
hdfs dfs -chmod 770 /tenant/$1/rawdata/data
hdfs dfs -chmod 770 /pre-tenant/$1/rawdata/data
hdfs dfs -chmod 770 /tenant/$1/rawdata/measures
hdfs dfs -chmod 770 /pre-tenant/$1/rawdata/measures
hdfs dfs -chmod 770 /tenant/$1/rawdata/media
hdfs dfs -chmod 770 /pre-tenant/$1/rawdata/media
hdfs dfs -chmod 770 /tenant/$1/rawdata/social
hdfs dfs -chmod 770 /pre-tenant/$1/rawdata/social
hdfs dfs -chmod 770 /tenant/$1/dwh
hdfs dfs -chmod 770 /pre-tenant/$1/dwh
hdfs dfs -chmod 770 /tenant/$1/output
hdfs dfs -chmod 770 /pre-tenant/$1/output
hdfs dfs -chmod 770 /tenant/$1/staging
hdfs dfs -chmod 770 /pre-tenant/$1/staging
hdfs dfs -chmod 770 /tenant/$1/temp
hdfs dfs -chmod 770 /pre-tenant/$1/temp
hdfs dfs -chmod 774 /tenant/$1/share
hdfs dfs -chmod 774 /pre-tenant/$1/share
hdfs dfs -chmod 774 /tenant/$1/share/data
hdfs dfs -chmod 774 /pre-tenant/$1/share/data
hdfs dfs -chmod 774 /tenant/$1/share/measures
hdfs dfs -chmod 774 /pre-tenant/$1/share/measures
hdfs dfs -chmod 774 /tenant/$1/share/media
hdfs dfs -chmod 774 /pre-tenant/$1/share/media
hdfs dfs -chmod 774 /tenant/$1/share/social
hdfs dfs -chmod 774 /pre-tenant/$1/share/social
