import argparse
import pprint

import operator

from mongodb_benchmarking_tools.gather_profiler_data.gatherer import gather_profiler_data
from mongodb_benchmarking_tools.lib.mongodb import setup_connection, discover_cluster_nodes, \
    discover_cluser_dbs
from mongodb_benchmarking_tools.lib.utils import isodate

SDP_CONFIG_HOST = 'mongodb://sdnet-config1.sdp.csi.it:27019'

def main():

    parser = argparse.ArgumentParser(
        description="Gathers profiler data from each cluster's replica set member."
    )

    parser.add_argument('-u', '--user',
                        help='username required for admin database authentication')
    parser.add_argument('-p', '--password',
                        help='password required for admin database authentication')
    parser.add_argument('-c', '--configsvr-url',
                        default=SDP_CONFIG_HOST,
                        help='cluster config server mongodb URL to connect to (mongodb://server:port)')
    parser.add_argument('-s', '--start',
                        type=isodate,
                        default='2000-01-01',
                        help='start date to filter data gathered in ISOFORMAT YYYY-MM-DD')
    parser.add_argument('-e', '--end',
                        type=isodate,
                        default='2100-01-01',
                        help='end date to filter data gathered in ISOFORMAT YYYY-MM-DD')

    opts = parser.parse_args()

    configsvr_client = setup_connection(opts.configsvr_url, opts.user, opts.password)

    nodes = discover_cluster_nodes(configsvr_client)
    dbs = discover_cluser_dbs(configsvr_client)

    result = []
    for node in nodes:
        for db in dbs:
            result += gather_profiler_data(node, db, opts.start, opts.end)

    result.sort(key=operator.itemgetter('ts'))

    pprint.pprint(result)

