#!/usr/bin/env bash


mkdir -p /etc/mongodb/logs
mkdir -p /data/db/shard1rep2
chown -R vagrant /data/db/ 
LC_ALL=C /home/vagrant/mongo/bin/mongod --fork --config /etc/mongodb/shard1rep2.conf
/home/vagrant/mongo/bin/mongo 10.0.1.10:10000/admin ./setup_shard1rep.js