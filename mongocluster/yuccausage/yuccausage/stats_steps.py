from datetime import datetime
from bson import ObjectId
from .mongodb import get_support_db, get_data_collections


def step_date(stats, day, **kwargs):
    """Gathers datetime informations of stats collection.

    datetime: ISODate(),
    date: {
        year: int,
        month: int,
        day: int,
        week: int,
        weekday: int,
    },

    """
    now = datetime.utcnow()
    isocal = day.isocalendar()

    stats.update({
        'datetime': now,
        'date': {
            'year': day.year,
            'month': day.month,
            'day': day.day,
            'week': isocal[1],
            'weekday': isocal[2],
        }
    })
    return stats, kwargs


def step_gather_tenant_info(stats, **kwargs):
    tenants_info = {}

    db_support = get_support_db()
    tenants = db_support['tenant'].find()

    for tenant in tenants:
        tenant_code = tenant['tenantCode']

        tenants_info[tenant_code] = {
            'tenant': tenant,
            'streams': []
        }

        tenants_info[tenant_code]['streams'] = list(db_support['stream'].find({
            'configData.tenantCode': tenant_code
        }))
    return stats, dict(kwargs, tenants_info=tenants_info)


def step_tenant_totals(stats, tenants_info, **kwargs):
    """Gathers data totals for all tenants.

    lifetime: {
        tenant_total_data: {
            tenantCode: {
                data: int,
                measures: int,
                social: int,
                media: int,
                binary: int
            },
            ...
        },
        tenant_streams_measures_data: {
            tenantCode: {
                streamCode:{
                    total: int,
                    visibility: 'public',
                    streamName: 'Temperature',
                    virtualEntityDescription: 'Formaldehyde sensor Haladins',
                    streamTags:[
                        {tagCode: 'INDOOR'},
                        {tagCode: 'QUALITY'},
                        ...
                    ],
                    components:[
                        {componentName: 'value', measureUnit: 'C'},
                        ...
                    ]
                },
                ...
            },
            ...
        },
        tenant_data_datasets_data: {
            tenantCode: {
                idDataset:{
                    datasetVersion:{
                        total: int,
                        visibility: 'public',
                        datasetName: 'T'.
                        description: 'Dataset Temperature',
                        license: 'CC BY 4.0',
                        disclaimer: None
                        copyright: 'Copyright (C) 2014, CSP Innovazione nelle ICT',
                        datasetTags:[
                            {tagCode: 'AIR'},
                            {tagCode: 'INDOOR'},
                            {tagCode: 'QUALITY'},
                            ...
                        ]
                    },
                    ...
                },
                ...
            },
            ...
        }
    },
    """
    for curtenant, curtenant_info in tenants_info.items():
        tenant_data = {
            'data': 0,
            'measures': 0,
            'social': 0,
            'media': 0,
            'binary': 0
        }

        measures_col, data_col, social_col, media_col, binary_col = get_data_collections(
            curtenant_info['tenant']
        )

        tenant_data['measures'] = measures_col.count()
        tenant_data['data'] = data_col.count()
        tenant_data['social'] = social_col.count()
        tenant_data['media'] = media_col.count()
        tenant_data['binary'] = binary_col.count()
        stats['lifetime']['tenant_total_data'][curtenant] = tenant_data

        streams = curtenant_info.get('streams')
        db_support = get_support_db()
        tenant_streams_measures_data = {}
        for stream in streams:
            stream_data = {}
            idStream = stream['idStream']
            idDataset = stream['configData']['idDataset']
            datasetVersion = stream['configData']['datasetVersion']
            stream_data['total'] = measures_col.find({'idDataset': idDataset}).count()
            stream_data['visibility'] = db_support['metadata'].find_one(
                {'idDataset': idDataset,
                 'datasetVersion': datasetVersion}
            )['info']['visibility']

            stream_info = stream['streams']['stream']
            stream_data['streamName'] = stream['streamName']
            stream_data['streamCode'] = stream.get('streamCode')
            stream_data['virtualEntityCode'] = stream_info.get('virtualEntityCode')
            stream_data['virtualEntityDescription'] = stream_info.get('virtualEntityDescription')
            stream_data['streamTags'] = stream_info['streamTags']['tags']
            stream_data['components'] = [{'componentName': element['componentName'],
                                          'measureUnit': element['measureUnit']}
                                         for element in stream_info['components']['element']]

            tenant_streams_measures_data[str(idStream)] = stream_data

        stats['lifetime']['tenant_streams_measures_data'][curtenant] = tenant_streams_measures_data

        datasetSubtype = 'bulkDataset'
        metadatas = db_support['metadata'].find({'configData.subtype': datasetSubtype,
                                                 'configData.tenantCode': curtenant})
        tenant_data_datasets_data = {}
        for metadata in metadatas:
            idDataset = metadata['idDataset']
            dataset_data = tenant_data_datasets_data.get(str(idDataset), {})
            datasetVersion = metadata['datasetVersion']
            datasetVersion_data = {}
            datasetVersion_data['total'] = data_col.find({'idDataset': idDataset}).count()
            datasetVersion_data['visibility'] = metadata['info']['visibility']
            datasetVersion_data['datasetName'] = metadata['info']['datasetName']
            datasetVersion_data['description'] = metadata['info'].get('description')
            datasetVersion_data['license'] = metadata['info'].get('license')
            datasetVersion_data['disclaimer'] = metadata['info'].get('disclaimer')
            datasetVersion_data['copyright'] = metadata['info'].get('copyright')
            datasetVersion_data['datasetTags'] = metadata['info'].get('tags')

            dataset_data[str(datasetVersion)] = datasetVersion_data
            tenant_data_datasets_data[str(idDataset)] = dataset_data
        stats['lifetime']['tenant_data_datasets_data'][curtenant] = tenant_data_datasets_data

    return stats, dict(kwargs, tenants_info=tenants_info)


def step_monthly_data(stats, month_origin_time, tenants_info, **kwargs):
    """Gather statistics since the begin of the month.

    monthly: {
        origin: ISODate(),
        tenant_total_data: {
            tenantCode: {
                data: int,
                measures: int,
                social: int,
                media: int,
                binary: int
            },
            ...
        },
        tenant_streams_measures_data: {
            tenantCode: {
                streamCode:{
                    total: int,
                    visibility: 'public',
                    streamName: 'Temperature',
                    virtualEntityDescription: 'Formaldehyde sensor Haladins',
                    streamTags:[
                        {tagCode: 'INDOOR'},
                        {tagCode: 'QUALITY'},
                        ...
                    ],
                    components:[
                        {componentName: 'value', measureUnit: 'C'},
                        ...
                    ]
                },
                ...
            },
            ...
        },
        tenant_data_datasets_data: {
            tenantCode: {
                idDataset:{
                    datasetVersion:{
                        total: int,
                        visibility: 'public',
                        datasetName: 'T'.
                        description: 'Dataset Temperature',
                        license: 'CC BY 4.0',
                        disclaimer: None
                        copyright: 'Copyright (C) 2014, CSP Innovazione nelle ICT',
                        datasetTags:[
                            {tagCode: 'AIR'},
                            {tagCode: 'INDOOR'},
                            {tagCode: 'QUALITY'},
                            ...
                        ]
                    },
                    ...
                },
                ...
            },
            ...
        }
    },

    """
    stats['monthly'] = _data_stats_since(month_origin_time, tenants_info)
    return stats, dict(kwargs, tenants_info=tenants_info)


def step_midnight_data(stats, midnight_origin_time, tenants_info, **kwargs):
    """Gather statistics since midnight.

    midnight: {
        origin: ISODate(),
        tenant_total_data: {
            tenantCode: {
                data: int,
                measures: int,
                social: int,
                media: int,
                binary: int
            },
            ...
        },
        tenant_streams_measures_data: {
            tenantCode: {
                streamCode:{
                    total: int,
                    visibility: 'public',
                    streamName: 'Temperature',
                    virtualEntityDescription: 'Formaldehyde sensor Haladins',
                    streamTags:[
                        {tagCode: 'INDOOR'},
                        {tagCode: 'QUALITY'},
                        ...
                    ],
                    components:[
                        {componentName: 'value', measureUnit: 'C'},
                        ...
                    ]
                },
                ...
            },
            ...
        },
        tenant_data_datasets_data: {
            tenantCode: {
                idDataset:{
                    datasetVersion:{
                        total: int,
                        visibility: 'public',
                        datasetName: 'T'.
                        description: 'Dataset Temperature',
                        license: 'CC BY 4.0',
                        disclaimer: None
                        copyright: 'Copyright (C) 2014, CSP Innovazione nelle ICT',
                        datasetTags:[
                            {tagCode: 'AIR'},
                            {tagCode: 'INDOOR'},
                            {tagCode: 'QUALITY'},
                            ...
                        ]
                    },
                    ...
                },
                ...
            },
            ...
        }
    },

    """
    stats['midnight'] = _data_stats_since(midnight_origin_time, tenants_info)
    return stats, dict(kwargs, tenants_info=tenants_info)


def step_thirtydays_data(stats, thirtydays_origin_time, tenants_info, **kwargs):
    """Gather statistics since 30 days ago.

    30days: {
        origin: ISODate(),
        tenant_total_data: {
            tenantCode: {
                data: int,
                measures: int,
                social: int,
                media: int,
                binary: int
            },
            ...
        },
        tenant_streams_measures_data: {
            tenantCode: {
                streamCode:{
                    total: int,
                    visibility: 'public',
                    streamName: 'Temperature',
                    virtualEntityDescription: 'Formaldehyde sensor Haladins',
                    streamTags:[
                        {tagCode: 'INDOOR'},
                        {tagCode: 'QUALITY'},
                        ...
                    ],
                    components:[
                        {componentName: 'value', measureUnit: 'C'},
                        ...
                    ]
                },
                ...
            },
            ...
        },
        tenant_data_datasets_data: {
            tenantCode: {
                idDataset:{
                    datasetVersion:{
                        total: int,
                        visibility: 'public',
                        datasetName: 'T'.
                        description: 'Dataset Temperature',
                        license: 'CC BY 4.0',
                        disclaimer: None
                        copyright: 'Copyright (C) 2014, CSP Innovazione nelle ICT',
                        datasetTags:[
                            {tagCode: 'AIR'},
                            {tagCode: 'INDOOR'},
                            {tagCode: 'QUALITY'},
                            ...
                        ]
                    },
                    ...
                },
                ...
            },
            ...
        }
    },

    """
    stats['30days'] = _data_stats_since(thirtydays_origin_time, tenants_info)
    return stats, dict(kwargs, tenants_info=tenants_info)


def step_sevendays_data(stats, sevendays_origin_time, tenants_info, **kwargs):
    """Gather statistics since 7 days ago.

    7days: {
        origin: ISODate(),
        tenant_total_data: {
            tenantCode: {
                data: int,
                measures: int,
                social: int,
                media: int,
                binary: int
            },
            ...
        },
        tenant_streams_measures_data: {
            tenantCode: {
                streamCode:{
                    total: int,
                    visibility: 'public',
                    streamName: 'Temperature',
                    virtualEntityDescription: 'Formaldehyde sensor Haladins',
                    streamTags:[
                        {tagCode: 'INDOOR'},
                        {tagCode: 'QUALITY'},
                        ...
                    ],
                    components:[
                        {componentName: 'value', measureUnit: 'C'},
                        ...
                    ]
                },
                ...
            },
            ...
        },
        tenant_data_datasets_data: {
            tenantCode: {
                idDataset:{
                    datasetVersion:{
                        total: int,
                        visibility: 'public',
                        datasetName: 'T'.
                        description: 'Dataset Temperature',
                        license: 'CC BY 4.0',
                        disclaimer: None
                        copyright: 'Copyright (C) 2014, CSP Innovazione nelle ICT',
                        datasetTags:[
                            {tagCode: 'AIR'},
                            {tagCode: 'INDOOR'},
                            {tagCode: 'QUALITY'},
                            ...
                        ]
                    },
                    ...
                },
                ...
            },
            ...
        }
    },

    """
    stats['7days'] = _data_stats_since(sevendays_origin_time, tenants_info)
    return stats, dict(kwargs, tenants_info=tenants_info)


def _data_stats_since(origin_time, tenants_info):
    """Gather data statistics since a moment in time.

    Uses ObjectId instead of dates to ensure that it works for any collection
    even those that don't provide a timestamp field. This also solves the issue
    with stats related to old data that is imported after it was registered.

    """
    # Should already be at 0:0:0.0 but better ensure it as ObjectId is not precise enough.
    origin_time = origin_time.replace(hour=0, minute=0, second=0, microsecond=0)
    origin_id = ObjectId.from_datetime(origin_time)

    tenant_total_data = {}
    tenant_streams_measures_data = {}
    tenant_data_datasets_data = {}

    for curtenant, curtenant_info in tenants_info.items():
        tenant_data = {
            'data': 0,
            'measures': 0,
            'social': 0,
            'media': 0,
            'binary': 0
        }

        measures_col, data_col, social_col, media_col, binary_col = get_data_collections(
            curtenant_info['tenant']
        )

        tenant_data['measures'] = measures_col.find({'_id': {'$gte': origin_id}}).count()
        tenant_data['data'] = data_col.find({'_id': {'$gte': origin_id}}).count()
        tenant_data['social'] = social_col.find({'_id': {'$gte': origin_id}}).count()
        tenant_data['media'] = media_col.find({'_id': {'$gte': origin_id}}).count()
        tenant_data['binary'] = binary_col.find({'_id': {'$gte': origin_id}}).count()

        tenant_total_data[curtenant] = tenant_data

        tenant_streams_measures_data[curtenant] = _tenant_streams_measure_since(origin_id,
                                                                                curtenant_info,
                                                                                measures_col)

        tenant_data_datasets_data[curtenant] = _tenant_datasets_data_since(origin_id,
                                                                           curtenant_info,
                                                                           data_col)

    return {
        'origin': origin_time,
        'tenant_total_data': tenant_total_data,
        'tenant_streams_measures_data': tenant_streams_measures_data,
        'tenant_data_datasets_data': tenant_data_datasets_data
    }


def _tenant_streams_measure_since(origin_id, tenant_info, measure_collection):
    tenant_streams_data = {}
    streams = tenant_info.get('streams')
    db_support = get_support_db()

    for stream in streams:
        stream_data = {}
        idStream = stream['idStream']
        idDataset = stream['configData']['idDataset']
        datasetVersion = stream['configData']['datasetVersion']
        stream_data['total'] = measure_collection.find(
            {'_id': {'$gte': origin_id}, 'idDataset': idDataset}).count()
        stream_data['visibility'] = db_support['metadata'].find_one(
            {'idDataset': idDataset,
             'datasetVersion': datasetVersion}
        )['info']['visibility']
        stream_info = stream['streams']['stream']
        stream_data['streamName'] = stream['streamName']
        stream_data['streamCode'] = stream.get('streamCode')
        stream_data['virtualEntityCode'] = stream_info.get('virtualEntityCode')
        stream_data['virtualEntityDescription'] = stream_info.get('virtualEntityDescription')
        stream_data['streamTags'] = stream_info['streamTags']['tags']
        stream_data['components'] = [{'componentName': element['componentName'],
                                      'measureUnit': element['measureUnit']}
                                     for element in stream_info['components']['element']]
        tenant_streams_data[str(idStream)] = stream_data

    return tenant_streams_data


def _tenant_datasets_data_since(origin_id, tenant_info, data_collection):
    tenant_datasets_data = {}
    db_support = get_support_db()
    datasetSubtype = 'bulkDataset'
    tenantCode = tenant_info['tenant']['tenantCode']
    metadatas = db_support['metadata'].find({'configData.subtype': datasetSubtype,
                                             'configData.tenantCode': tenantCode})

    for metadata in metadatas:

        idDataset = metadata['idDataset']
        dataset_data = tenant_datasets_data.get(str(idDataset), {})
        datasetVersion = metadata['datasetVersion']
        datasetVersion_data = {}
        datasetVersion_data['total'] = data_collection.find({'_id': {'$gte': origin_id},
                                                      'idDataset': idDataset}).count()
        datasetVersion_data['visibility'] = metadata['info']['visibility']

        datasetVersion_data['datasetName'] = metadata['info']['datasetName']
        datasetVersion_data['description'] = metadata['info'].get('description')
        datasetVersion_data['license'] = metadata['info'].get('license')
        datasetVersion_data['disclaimer'] = metadata['info'].get('disclaimer')
        datasetVersion_data['copyright'] = metadata['info'].get('copyright')
        datasetVersion_data['datasetTags'] = metadata['info'].get('tags')

        dataset_data[str(datasetVersion)] = datasetVersion_data
        tenant_datasets_data[str(idDataset)] = dataset_data

    return tenant_datasets_data
