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
#	es.  ./craTenant csi												      #
#																			  #
###############################################################################

# creazione delle cartelle su HDFS

echo "Creazione del tenant tnt-$1"
hdfs dfs -mkdir /tenant/tnt-$1
hdfs dfs -mkdir /pre-tenant/tnt-$1
echo "Creazione cartella RAWDATA"
hdfs dfs -mkdir /tenant/tnt-$1/rawdata
hdfs dfs -mkdir /pre-tenant/tnt-$1/rawdata
echo "Creazione cartella SCRIPTS"
hdfs dfs -mkdir /tenant/tnt-$1/scripts
hdfs dfs -mkdir /pre-tenant/tnt-$1/scripts
echo "Creazione cartella RAWDATA/DATA"
hdfs dfs -mkdir /tenant/tnt-$1/rawdata/data
hdfs dfs -mkdir /pre-tenant/tnt-$1/rawdata/data
echo "Creazione cartella RAWDATA/MEASURES"
hdfs dfs -mkdir /tenant/tnt-$1/rawdata/measures
hdfs dfs -mkdir /pre-tenant/tnt-$1/rawdata/measures
echo "Creazione cartella RAWDATA/MEDIA"
hdfs dfs -mkdir /tenant/tnt-$1/rawdata/media
hdfs dfs -mkdir /pre-tenant/tnt-$1/rawdata/media
echo "Creazione cartella RAWDATA/SOCIAL"
hdfs dfs -mkdir /tenant/tnt-$1/rawdata/social
hdfs dfs -mkdir /pre-tenant/tnt-$1/rawdata/social
echo "Creazione cartella DWH"
hdfs dfs -mkdir /tenant/tnt-$1/dwh
hdfs dfs -mkdir /pre-tenant/tnt-$1/dwh
echo "Creazione cartella OUTPUT"
hdfs dfs -mkdir /tenant/tnt-$1/output
hdfs dfs -mkdir /pre-tenant/tnt-$1/output
echo "Creazione cartella STAGING"
hdfs dfs -mkdir /tenant/tnt-$1/staging
hdfs dfs -mkdir /pre-tenant/tnt-$1/staging
echo "Creazione cartella TEMP"
hdfs dfs -mkdir /tenant/tnt-$1/temp
hdfs dfs -mkdir /pre-tenant/tnt-$1/temp
echo "Creazione cartella SHARE"
hdfs dfs -mkdir /tenant/tnt-$1/share
hdfs dfs -mkdir /pre-tenant/tnt-$1/share
echo "Creazione cartella SHARE/DATA"
hdfs dfs -mkdir /tenant/tnt-$1/share/data
hdfs dfs -mkdir /pre-tenant/tnt-$1/share/data
echo "Creazione cartella SHARE/MEASURES"
hdfs dfs -mkdir /tenant/tnt-$1/share/measures
hdfs dfs -mkdir /pre-tenant/tnt-$1/share/measures
echo "Creazione cartella SHARE/MEDIA"
hdfs dfs -mkdir /tenant/tnt-$1/share/media
hdfs dfs -mkdir /pre-tenant/tnt-$1/share/media
echo "Creazione cartella SHARE/SOCIAL"
hdfs dfs -mkdir /tenant/tnt-$1/share/social
hdfs dfs -mkdir /pre-tenant/tnt-$1/share/social

# assegnazione delle utenze in base alle specifiche

echo "Assegnazione utenze alle cartelle"
echo "assegno utenti alla cartella tnt-$1 e alle sue sottocartelle"
hdfs dfs -chown -R "prod-$1:tnt-$1" /tenant/tnt-$1
hdfs dfs -chown -R "preprod-$1:pretnt-$1" /pre-tenant/tnt-$1
hdfs dfs -chown -R "hive:tnt-$1" /tenant/tnt-$1/share
hdfs dfs -chown -R "hive:pretnt-$1" /pre-tenant/tnt-$1/share

# assegnazione dei permessi in base alle specifiche

echo "assegnazione dei permessi alla cartella tnt-$1 e alle sottocartelle"
hdfs dfs -chmod -R 770 /tenant/tnt-$1
hdfs dfs -chmod -R 770 /pre-tenant/tnt-$1
hdfs dfs -chmod -R 774 /tenant/tnt-$1/share
hdfs dfs -chmod -R 774 /pre-tenant/tnt-$1/share
