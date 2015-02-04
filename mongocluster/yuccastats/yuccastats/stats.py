from .pipeline import _run_pipeline
from .stats_steps import (step_date, step_metadata_totals, step_tenant_totals,
                          step_gather_tenant_info, step_stream_frequencies, step_monthly_data,
                          step_midnight_data, step_thirtydays_data)


def gather_stats(month_origin_time, midnight_origin_time, thirtydays_origin_time,
                 day):
    """Retrieves from YUCCA the stats grouped in two time frames.

    ``faraway_origin_time`` will usually be a *a month ago*,
    while ``nearby_origin_time`` will usually be *a day ago*.

    Data will be recovered for the whole life of the system and
    for ``faraway_origin_time`` and ``nearby_origin_time`` providing
    the information on three time spans.

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
                total_tenants: int,
                total_streams: int,
                total_smart_objects: int,
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
                },
            },
            monthly: {
                origin: ISODate(),
                total_data: {
                    data: int,
                    measures: int,
                    social: int,
                    media: int
                },
            },
            midnight: {
                origin: ISODate(),
                total_data: {
                    data: int,
                    measures: int,
                    social: int,
                    media: int
                },
            },
            30days: {
                origin: ISODate(),
                total_data: {
                    data: int,
                    measures: int,
                    social: int,
                    media: int
                },
            }
        }

    """
    pipeline = (
        step_date,
        step_metadata_totals,
        step_gather_tenant_info,
        step_tenant_totals,
        step_stream_frequencies,
        step_monthly_data,
        step_midnight_data,
        step_thirtydays_data
    )

    origin = {
        'lifetime': {

        },
        'monthly': {

        },
        'midnight': {

        },
        '30days': {

        }
    }

    return _run_pipeline(pipeline, origin,
                         thirtydays_origin_time=thirtydays_origin_time,
                         month_origin_time=month_origin_time,
                         midnight_origin_time=midnight_origin_time,
                         day=day)
