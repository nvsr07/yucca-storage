#!/usr/bin/env bash


mkdir -p /etc/mongodb/logs
mkdir -p /data/db/shard1
chown -R vagrant /data/db/ 
LC_ALL=C /home/vagrant/mongo/bin/mongod --fork --config /etc/mongodb/shard1.conf