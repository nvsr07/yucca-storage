from .utils import sensor_from_name, timedict_to_datetime
from .mongodb import get_support_db, get_tenant_code


def convert(input_dict):
    """Converts data from CSP .json export to the MongoDB measures schema"""
    sensor, stream_code = sensor_from_name(input_dict['Name'])
    time = timedict_to_datetime(input_dict['Timestamp'])
    id_dataset, dataset_version = lookup_dataset(get_tenant_code(), stream_code, sensor)

    measure = {
        "sensor": sensor,
        "time": time,
        "idDataset": id_dataset,
        "datasetVersion": dataset_version,
        "streamCode": stream_code,
        "value": input_dict['Value'],
        'validity': 'unknown'
    }

    current_location = input_dict.get('Location')
    if current_location:
        measure['idxLocation'] = [float(x) for x in current_location[:2]]

    return measure


class DataSetLookupWithCache(object):
    """Functor that lookups up the idDataset and datasetVersion for a measure,

    It provides an in memory cache that keeps the result cached for the whole process
    lifetime.
    """
    def __init__(self):
        self._cache = {}

    def __call__(self, tenant, stream_code, sensor):
        cache_key = '$'.join((tenant, stream_code, sensor))

        try:
            value = self._cache[cache_key]
        except KeyError:
            value = self._cache[cache_key] = self._lookup_on_db(tenant, stream_code, sensor)

        return value

    def _lookup_on_db(self, tenant_code, stream_code, sensor):
        db_support = get_support_db()

        stream = db_support['stream'].find_one({
            'streamCode': stream_code,
            'configData.tenantCode': tenant_code,
            'streams.stream.virtualEntityCode': sensor
        })

        if stream is None:
            raise ValueError('Stream %s - %s - %s not registered on DB' % (tenant_code,
                                                                           stream_code,
                                                                           sensor))

        config_data = stream['configData']
        return config_data['idDataset'], config_data['datasetVersion']

lookup_dataset = DataSetLookupWithCache()
