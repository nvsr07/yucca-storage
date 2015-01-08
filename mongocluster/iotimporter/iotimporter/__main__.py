import argparse
import traceback
import pprint
from iotimporter.reader import FileReader
from iotimporter.mongodb import setup_connection
from iotimporter.converter import convert
from iotimporter.utils import sensor_from_name, frequency2fps
from iotimporter.writer import insert_dataset, InsertError


def main():
    parser = argparse.ArgumentParser(
        description='import data from CSP Internet Of Things to '
                    'CSI MongoDB based Internet Of Things.'
    )

    parser.add_argument('file', help='.json file to import')
    parser.add_argument('mongourl', help='mongodb URL to connect to (mongodb://server:port)')
    parser.add_argument('tenantid', help='id of the tenant owner of the imported data')
    parser.add_argument('-s', '--skip',
                        type=int,
                        help='Skip first SKIP elements, using --skip 1 will skip the first entry. '
                             'This can be used to recover a previous insertion from the point '
                             'where it stopped.')
    parser.add_argument('-b', '--bulk',
                        default=1,
                        type=int,
                        help='Size of insertion group for Bulk Write, more than 1 makes insertion '
                             'faster but error reporting more unreliable in case of errors.')
    parser.add_argument('-m', '--inmemory',
                        action='store_true',
                        help='Read data in memory instead of reading it line by line, '
                             'might solve some issues with JSON Parser and speeds up the '
                             'import process at the cost of as much memory as the whole dataset')
    parser.add_argument('--sensordata',
                        action='store_true',
                        help='Instead of performing data insertion just print sensors data to '
                             'configure the sensor before insertion.')

    opts = parser.parse_args()

    setup_connection(opts.mongourl, opts.tenantid)
    reader = FileReader(opts.file, generate=not opts.inmemory)

    if opts.sensordata:
        run_sensordata(reader)
    else:
        run_insertion(reader, opts.skip, opts.bulk)


def run_insertion(reader, skip, bulk_size):
    try:
        insert_dataset(
            reader.read_entries(skip=skip),
            convert,
            bulk_size=bulk_size
        )
    except InsertError as e:
        offset = e.index
        traceback.print_exc()
        print_red('Write Failed at entry %s, use --skip %s to recover insertion from here.',
                  offset, offset)
    except Exception:
        offset = reader.last_read
        traceback.print_exc()
        print_red('Unknown Error at entry %s. '
                  'This is not the precise entry in case --bulk was used.', offset)


def run_sensordata(reader):
    measure = next(reader.read_entries(skip=0))
    sensor, stream_code = sensor_from_name(measure['Name'])

    sensor_data = {
        'location': {
            'positions': [{'lat': float(measure['Location'][0]),
                           'lon': float(measure['Location'][1]),
                           'ele': float(measure['Location'][2])}],
            'exposure': 'outdoor',
            'disposition': 'fixed' if measure['Type'].lower() == 'sensor' else 'mobile',
        },
        'id': sensor,
        'name': measure['Name'],
        'model': measure['Model'],
        'description': measure['Description'],
        'supply': 'network' if measure['Supply'].lower() == 'none' else 'auto',
        'status': 'active',
        'category': 'smart',
        'version': '1.0'
    }

    stream_data = {
        'id': stream_code,
        'sensor': sensor,
        'visibility': 'public',
        'fps': frequency2fps(measure['Frequency']),
        'domain': 'IoTNet',
        'license': 'CC BY 4.0',
        'copyright': 'Copyright (C) 2014, CSP Innovazione nelle ICT. All rights reserved.',
        'components': [{
            'id': 'value',
            'unit': measure['Unit'],
            'allowance': float(measure['Allowance']),
            'type': 'float',
            'event': measure['ObservedProperty'],
        }],
        'tags': ['tag1', 'tag2']
    }

    print_red('SENSOR DATA')
    pprint.pprint(sensor_data, width=100)
    print('')
    print_red('STREAM DATA')
    pprint.pprint(stream_data, width=100)


def print_red(s, *args):
    s = ''.join(('\033[31m', s, '\033[0m'))
    print(s % args)


if __name__ == '__main__':
    main()
