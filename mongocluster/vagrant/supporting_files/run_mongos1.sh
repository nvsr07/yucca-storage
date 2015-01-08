#!/usr/bin/env bash

LC_ALL=C /home/vagrant/mongo/bin/mongos --fork --config /etc/mongodb/mongos1.conf
LC_ALL=C /home/vagrant/mongo/bin/mongo 10.0.1.3:30000/admin ./add_shards.js