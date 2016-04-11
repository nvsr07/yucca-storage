import operator

from pymongo import MongoClient

from functools import reduce

def setup_connection(host, user=None, password=None):
    client = MongoClient(host)
    if user:
        admin_db = client.admin
        admin_db.authenticate(user, password, mechanism='MONGODB-CR')
    return client


def discover_cluster_nodes(configsvr_client):
    shards = [shard['host'].split('/')[1] for shard in configsvr_client.config.shards.find()]
    nodes = reduce(operator.add, map(operator.methodcaller('split', ','), shards))
    return [setup_connection(node) for node in nodes]


def discover_cluser_dbs(configsvr_client):
    return [database['_id'] for database in configsvr_client.config.databases.find()]

