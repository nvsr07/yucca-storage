import logging
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
    }

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

        tenants_info[tenant_code]['streams'] = db_support['stream'].find({
            'configData.tenantCode': tenant_code
        })

    return stats, dict(kwargs, tenants_info=tenants_info)


def step_tenant_totals(stats, tenants_info, **kwargs):
    """Gathers data totals for all tenants.

    lifetime: {
        total_data: {
            data: int,
            measures: int,
            social: int,
            media: int
        },
        tenant_total_data: {
            tenantCode: {
                data: int,
                measures: int,
                social: int,
                media: int
            },
            ...
        },
    }
    """
    stats['lifetime'].update({
        'total_data': {
            'data': 0,
            'measures': 0,
            'social': 0,
            'media': 0
        },
        'tenant_total_data': {}
    })

    for curtenant, curtenant_info in tenants_info.items():
        tenant_data = {
            'data': 0,
            'measures': 0,
            'social': 0,
            'media': 0
        }

        measures_col, data_col, social_col, media_col = get_data_collections(
            curtenant_info['tenant']
        )

        tenant_data['measures'] = measures_col.count()
        tenant_data['data'] = data_col.count()
        tenant_data['social'] = social_col.count()
        tenant_data['media'] = media_col.count()

        stats['lifetime']['tenant_total_data'][curtenant] = tenant_data
        stats['lifetime']['total_data']['measures'] += tenant_data['measures']
        stats['lifetime']['total_data']['data'] += tenant_data['data']
        stats['lifetime']['total_data']['social'] += tenant_data['social']
        stats['lifetime']['total_data']['media'] += tenant_data['media']

    return stats, dict(kwargs, tenants_info=tenants_info)


def step_metadata_totals(stats, **kwargs):
    """Gathers metadata totals

    lifetime: {
        total_tenants: int,
        total_streams: int,
        total_smart_objects: int,
    }
    """
    db_support = get_support_db()
    stats['lifetime'].update({
        'total_tenants': db_support['tenant'].count(),
        'total_streams': db_support['stream'].count(),
        'total_smart_objects': len(db_support['stream'].distinct('streams.stream.idVirtualEntity'))
    })
    return stats, kwargs


def step_stream_frequencies(stats, **kwargs):
    """Gather frequency of streams per tags, tenants and domains.

    stream_frequency: {
        tags: [
            { _id: str,
              count: int },
            ...
        ],
        tenants: [
            { _id: str,
              count: int },
            ...
        ],
        domain: [
            { _id: str,
              count: int },
            ...
        ]
    }

    """
    stats['lifetime'].update({
        'stream_frequency': {
            'tags': [],
            'tenants': [],
            'domain': []
        },
    })

    db_support = get_support_db()

    # Gather statistics for tags frequencies
    try:
        tags = db_support['stream'].aggregate([
            {'$project': {'tags': '$streams.stream.streamTags.tags.tagCode'}},
            {'$unwind': '$tags'},
            {'$group': {'_id': '$tags', 'count': {'$sum': 1}}},
            {'$sort': {'count': -1}}
        ])['result']
        if not tags:
            raise IndexError('Empty Tags')
    except (IndexError, KeyError):
        logging.getLogger('steps.step_stream_frequencies').exception(
            'Missing expected details in aggregation framework answer for tags frequency'
        )
        tags = []
    stats['lifetime']['stream_frequency']['tags'] = tags

    # Gather statistics for tenants frequencies
    try:
        tenants = db_support['stream'].aggregate([
            {'$project': {'tenant': '$configData.tenantCode'}},
            {'$group': {'_id': '$tenant', 'count': {'$sum': 1}}},
            {'$sort': {'count': -1}}
        ])['result']
        if not tenants:
            raise IndexError('Empty Tenants')
    except (IndexError, KeyError):
        logging.getLogger('steps.step_stream_frequencies').exception(
            'Missing expected details in aggregation framework answer for tenants frequency'
        )
        tenants = []
    stats['lifetime']['stream_frequency']['tenants'] = tenants

    # Gather statistics for domain frequencies
    try:
        domain = db_support['stream'].aggregate([
            {'$project': {'domain': '$streams.stream.domainStream'}},
            {'$group': {'_id': '$domain', 'count': {'$sum': 1}}},
            {'$sort': {'count': -1}}
        ])['result']
        if not domain:
            raise IndexError('Empty Domains')
    except (IndexError, KeyError):
        logging.getLogger('steps.step_stream_frequencies').exception(
            'Missing expected details in aggregation framework answer for domain frequency'
        )
        domain = []
    stats['lifetime']['stream_frequency']['domain'] = domain

    return stats, kwargs


def step_monthly_data(stats, month_origin_time, tenants_info, **kwargs):
    """Gather statistics since the begin of the month.

    monthly: {
        origin: ISODate(),
        total_data: {
            data: int,
            measures: int,
            social: int,
            media: int
        },
    },

    """
    stats['monthly'] = _data_stats_since(month_origin_time, tenants_info)
    return stats, dict(kwargs, tenants_info=tenants_info)


def step_midnight_data(stats, midnight_origin_time, tenants_info, **kwargs):
    """Gather statistics since the begin of the month.

    midnight: {
        origin: ISODate(),
        total_data: {
            data: int,
            measures: int,
            social: int,
            media: int
        },
    },

    """
    stats['midnight'] = _data_stats_since(midnight_origin_time, tenants_info)
    return stats, dict(kwargs, tenants_info=tenants_info)


def step_thirtydays_data(stats, thirtydays_origin_time, tenants_info, **kwargs):
    """Gather statistics since the begin of the month.

    30days: {
        origin: ISODate(),
        total_data: {
            data: int,
            measures: int,
            social: int,
            media: int
        },
    },

    """
    stats['30days'] = _data_stats_since(thirtydays_origin_time, tenants_info)
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

    data_stats = {
        'data': 0,
        'measures': 0,
        'social': 0,
        'media': 0
    }

    for curtenant, curtenant_info in tenants_info.items():
        measures_col, data_col, social_col, media_col = get_data_collections(
            curtenant_info['tenant']
        )

        data_stats['measures'] += measures_col.find({'_id': {'$gte': origin_id}}).count()
        data_stats['data'] += data_col.find({'_id': {'$gte': origin_id}}).count()
        data_stats['social'] += social_col.find({'_id': {'$gte': origin_id}}).count()
        data_stats['media'] += media_col.find({'_id': {'$gte': origin_id}}).count()

    return {
        'origin': origin_time,
        'total_data': data_stats
    }
