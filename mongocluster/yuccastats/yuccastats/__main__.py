import logging
import argparse
import pprint
from .mongodb import setup_connection, get_statistics_collection
from .stats import gather_stats
from .utils import first_month_day, begin_of_day, isodate, thirtydays_ago


logging.basicConfig()


def main():
    parser = argparse.ArgumentParser(
        description='Gathers cross tenant statistics from the YUCCA platform.'
    )

    parser.add_argument('mongourl',
                        help='mongodb URL to connect to (mongodb://server:port)')
    parser.add_argument('day',
                        type=isodate,
                        help='Gather stats starting from midnight of this day. '
                             'Can be "yesterday" if you want to gather past day stats.')
    parser.add_argument('-c', '--collection',
                        default='statistics',
                        help='name of the collection which should contains stats')
    parser.add_argument('-d', '--database',
                        default='DB_SUPPORT',
                        help='name of the database where to store the stats')
    parser.add_argument('-w', '--write',
                        action='store_true',
                        help='Write statistics on DB instead of printing them.')

    opts = parser.parse_args()
    setup_connection(opts.mongourl, opts.database, opts.collection)

    stats = gather_stats(first_month_day(opts.day),
                         begin_of_day(opts.day),
                         thirtydays_ago(opts.day),
                         day=opts.day)

    if opts.write:
        get_statistics_collection().save(stats)
    else:
        pprint.pprint(stats)
