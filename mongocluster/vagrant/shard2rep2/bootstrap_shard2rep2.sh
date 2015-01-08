#!/usr/bin/env bash


mkdir -p /etc/mongodb/logs
mkdir -p /data/db/shard2rep2
chown -R vagrant /data/db/ 
LC_ALL=C /home/vagrant/mongo/bin/mongod --fork --config /etc/mongodb/shard2rep2.conf
/home/vagrant/mongo/bin/mongo 10.0.1.20:20000/admin ./setup_shard2rep.js
