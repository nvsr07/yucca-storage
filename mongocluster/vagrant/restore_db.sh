echo Restoring dbs, be sure to have cluster running and at least DB_SUPPORT and 1 tenant db inside ./supporting_files/dbs folder
secs=$((1))
while [ $secs -gt 0 ]; do
   echo -ne _ "$secs\033[0K\r"
   sleep 1
   : $((secs--))
done
echo Restoring DB_SUPPORT
/opt/mongo/bin/mongorestore --port 30000 ./supporting_files/dbs/DB_SUPPORT/
for d in ./supporting_files/dbs/*/ ; do
    db=$(basename "$d")
    if [ $db != 'DB_SUPPORT' ]; 
        then echo restoring $db
        /opt/mongo/bin/mongorestore --port 30000 $d
        echo adding $db to shard
        /opt/mongo/bin/mongo --port 30000 --eval "sh.enableSharding('$db')"
    fi 
done
