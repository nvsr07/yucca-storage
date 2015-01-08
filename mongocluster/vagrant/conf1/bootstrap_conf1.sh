#!/usr/bin/env bash


mkdir -p /etc/mongodb/logs
mkdir -p /data/db/conf1
chown -R vagrant /data/db/conf1 
chmod +x /home/vagrant/run_mongos1.sh
LC_ALL=C /home/vagrant/mongo/bin/mongod --configsvr --fork --config /etc/mongodb/conf1.conf