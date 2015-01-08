#!/usr/bin/env bash


mkdir -p /etc/mongodb/logs
mkdir -p /data/db/conf2
chown -R vagrant /data/db/conf2 
chown -R vagrant /etc/mongodb/logs
chmod +x /home/vagrant/run_mongos2.sh
LC_ALL=C /home/vagrant/mongo/bin/mongod --configsvr --fork --config /etc/mongodb/conf2.conf
