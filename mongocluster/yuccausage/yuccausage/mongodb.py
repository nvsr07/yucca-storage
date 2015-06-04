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
    fallback_db = 'DB_%s' % tenant_info['tenantCode']

    measures_db = tenant_info.get('measuresCollectionDb', fallback_db)
    measures_col = tenant_info.get('measuresCollectionName', 'measures')

    data_db = tenant_info.get('dataCollectionDb', fallback_db)
    data_col = tenant_info.get('dataCollectionName', 'data')

    social_db = tenant_info.get('socialCollectionDb', fallback_db)
    social_col = tenant_info.get('socialCollectionName', 'social')

    media_db = tenant_info.get('mediaCollectionDb', fallback_db)
    media_col = tenant_info.get('mediaCollectionName', 'media')

    binary_db = tenant_info.get('binaryCollectionDb', fallback_db)
    binary_col = tenant_info.get('binaryCollectionName', 'binary')

    return (
        get_connection()[measures_db][measures_col],
        get_connection()[data_db][data_col],
        get_connection()[social_db][social_col],
        get_connection()[media_db][media_col],
        get_connection()[binary_db][binary_col],
    )

