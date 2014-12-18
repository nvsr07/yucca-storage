#/bin/bash
#set -x
#
. /home/oozie/scripts/preprod.conf
rm /home/oozie/temp/preprod/*
echo 'db.allineamento.update({},{"$set":{"last_timestamp":1388534400000}},{"multi":true});' | mongo DB_SUPPORT --host $MONGO_HOST --port $MONGO_PORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin
echo ' db.allineamento.update({},{"$set":{"locked":0}},{"multi":true});' | mongo DB_SUPPORT --host $MONGO_HOST --port $MONGO_PORT -u $MONGO_USER -p $MONGO_PWD --authenticationDatabase admin
