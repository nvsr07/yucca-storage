from pymongo import MongoClient

_tenant_code = None
_mongodb_connection = None


def setup_connection(mongo_url, tenant_code):
    global _tenant_code, _mongodb_connection
    _tenant_code = tenant_code
    _mongodb_connection = MongoClient(mongo_url)


def get_connection():
    if _mongodb_connection is None:
        raise RuntimeError('Connection has not been setup')

    return _mongodb_connection


def get_tenant_code():
    if _tenant_code is None:
        raise RuntimeError('tenant code has not been configured')

    return _tenant_code


def get_tenant_db():
    return get_connection()['_'.join(('DB', get_tenant_code()))]


def get_support_db():
    return get_connection()['DB_SUPPORT']
