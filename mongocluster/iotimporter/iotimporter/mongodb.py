from pymongo import MongoClient

_tenant_code = None
_mongodb_connection = None


def _clear_mongodb_configuration():
    global _tenant_code, _mongodb_connection
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


def _get_tenant_default_db_name():
    return '_'.join(('DB', get_tenant_code()))


def _get_tenant_default_db():
    return get_connection()[_get_tenant_default_db_name()]


def get_support_db():
    return get_connection()['DB_SUPPORT']


def get_measures_collection_names(tenant_code, dataset_id):
    support_db = get_support_db()

    tenant_info = support_db.tenant.find_one({'tenantCode': tenant_code},
                                             fields=['measuresCollectionDb',
                                                     'measuresCollectionName'])
    if tenant_info is None:
        raise LookupError('Unable to find tenant information on db for: %s' % tenant_code)

    metadata = support_db.metadata.find_one({'idDataset': dataset_id,
                                             'configData.tenantCode': tenant_code},
                                            fields=['configData.database',
                                                    'configData.collection'])
    if metadata is None:
        raise LookupError('Unable to find metadata for: %s - %s' % (tenant_code, dataset_id))

    measures_db = metadata.get('configData', {}).get('database')
    if not measures_db:
        measures_db = tenant_info.get('measuresCollectionDb')
        if not measures_db:
            measures_db = _get_tenant_default_db_name()

    measures_col = metadata.get('configData', {}).get('collection')
    if not measures_col:
        measures_col = tenant_info.get('measuresCollectionName')
        if not measures_col:
            measures_col = 'measures'

    return measures_db, measures_col


def get_measures_collection(dataset_id):
    measures_db, measures_col = get_measures_collection_names(get_tenant_code(), dataset_id)
    return get_connection()[measures_db][measures_col]
