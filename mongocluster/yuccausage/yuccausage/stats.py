from .pipeline import _run_pipeline
from .stats_steps import (step_date, step_tenant_totals, step_gather_tenant_info, step_monthly_data,
                          step_midnight_data, step_thirtydays_data, step_sevendays_data)


def gather_stats(month_origin_time, midnight_origin_time, thirtydays_origin_time,
                 seven_days_origin_time, day):
    """Retrieves from YUCCA the usage stats grouped in time frames.

    Data will be recovered for the whole life of the system and
    for ``month_origin_time``,  ``midnight_origin_time``,  ``thirtydays_origin_time``,
    ``seven_days_origin_time`` providing the information on four time spans.

    Format of the recovered document is as follows::

        {
            datetime: ISODate(),
            date: {
                year: int,
                month: int,
                day: int,
                week: int,
                weekday: int,
            },
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
                            visibility: 'public'
                        },
                        ...
                    },
                    ...
                },
                tenant_data_datasets_data: {
                    tenantCode: {
                        streamCode:{
                            total: int,
                            visibility: 'public'
                        },
                        ...
                    },
                    ...
                }
            },
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
                            visibility: 'public'
                        },
                        ...
                    },
                    ...
                },
                tenant_data_datasets_data: {
                    tenantCode: {
                        streamCode:{
                            total: int,
                            visibility: 'public'
                        },
                        ...
                    },
                    ...
                }
            },
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
                            visibility: 'public'
                        },
                        ...
                    },
                    ...
                },
                tenant_data_datasets_data: {
                    tenantCode: {
                        streamCode:{
                            total: int,
                            visibility: 'public'
                        },
                        ...
                    },
                    ...
                }
            },
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
                            visibility: 'public'
                        },
                        ...
                    },
                    ...
                },
                tenant_data_datasets_data: {
                    tenantCode: {
                        streamCode:{
                            total: int,
                            visibility: 'public'
                        },
                        ...
                    },
                    ...
                }
            }
        }

    """
    pipeline = (
        step_date,
        step_gather_tenant_info,
        step_tenant_totals,
        step_monthly_data,
        step_thirtydays_data,
        step_sevendays_data,
        step_midnight_data
    )

    origin = {
        'lifetime': {
            'tenant_total_data': {},
            'tenant_streams_measures_data': {},
            'tenant_data_datasets_data': {}
        },
        'monthly': {
            'tenant_total_data': {},
            'tenant_streams_measures_data': {},
            'tenant_data_datasets_data': {}
        },
        '30days': {
            'tenant_total_data': {},
            'tenant_streams_measures_data': {},
            'tenant_data_datasets_data': {}
        },
        '7days': {
            'tenant_total_data': {},
            'tenant_streams_measures_data': {},
            'tenant_data_datasets_data': {}
        },
        'midnight': {
            'tenant_total_data': {},
            'tenant_streams_measures_data': {},
            'tenant_data_datasets_data': {}
        },
    }

    return _run_pipeline(pipeline, origin,
                         thirtydays_origin_time=thirtydays_origin_time,
                         month_origin_time=month_origin_time,
                         midnight_origin_time=midnight_origin_time,
                         sevendays_origin_time=seven_days_origin_time,
                         day=day)
