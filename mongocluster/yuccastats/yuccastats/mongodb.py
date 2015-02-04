from pymongo import MongoClient

_stats_collection = None
_stats_database = None
_mongodb_connection = None
_support_database = None


def _clear_mongodb_configuration():
    global _stats_database, _stats_collection, _mongodb_connection, _support_database
    _stats_database = None
    _stats_collection = None
    _mongodb_connection = None
    _support_database = None


def setup_connection(mongo_url, stats_database, stats_collection, support_database='DB_SUPPORT'):
    global _stats_database, _stats_collection, _mongodb_connection, _support_database
    _stats_database = stats_database
    _stats_collection = stats_collection
    _support_database = support_database
    _mongodb_connection = MongoClient(mongo_url)


def get_connection():
    if _mongodb_connection is None:
        raise RuntimeError('Connection has not been setup')

    return _mongodb_connection


def get_support_db():
    return get_connection()[_support_database]


def get_statistics_collection():
    return get_connection()[_stats_database][_stats_collection]


def get_data_collections(tenant_info):
    measures_db = tenant_info['measuresCollectionDb']
    measures_col = tenant_info['measuresCollectionName']

    data_db = tenant_info['dataCollectionDb']
    data_col = tenant_info['dataCollectionName']

    social_db = tenant_info['socialCollectionDb']
    social_col = tenant_info['socialCollectionName']

    media_db = tenant_info['mediaCollectionDb']
    media_col = tenant_info['mediaCollectionName']

    return (
        get_connection()[measures_db][measures_col],
        get_connection()[data_db][data_col],
        get_connection()[social_db][social_col],
        get_connection()[media_db][media_col]
    )
