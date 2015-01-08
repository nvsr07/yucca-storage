from __future__ import division
from datetime import datetime
import re
import uuid


FREQUENCY_MULTIPLIERS = {
    'sec': 1,
    'min': 60,
    'hour': 3600
}


def frequency2fps(frequency):
    """Converts sensor frequency from VALUE+FREQ format to fraction of second FPS.

    Examples:

        "300sec" -> 0,003
        "1min" -> 0,016

    """
    frequency = frequency.strip()
    if frequency == '0':
        return 0

    m = re.match(r"(\d+)\s*(\w+)", frequency)
    if m is None:
        raise ValueError('Frequency should be in VALUE+FREQ format, EXAMPLE: "300sec"')

    try:
        num = int(m.group(1))
    except ValueError:  # pragma: nocover
        # This should never happen as regex checks for digits
        raise KeyError('Invalid Frequency Value: %s' % frequency)

    try:
        freq_unit = m.group(2).lower()
        multiplier = FREQUENCY_MULTIPLIERS[freq_unit]
    except KeyError:
        raise ValueError('Invalid Frequency Unit: %s' % frequency)

    num *= multiplier
    if num == 0:
        return 0

    return 1 / num


def sensor_from_name(measure_name):
    """Given a CSP IOT measure name it returns the sensor_id and stream_code"""
    iotnet_id = uuid.uuid5(uuid.NAMESPACE_DNS, 'iotnet.it')
    project, stream_code, wsn = measure_name.split('_')
    uuid5_name = project + '_' + wsn

    try:
        sensor_id = uuid.uuid5(iotnet_id, uuid5_name)
    except UnicodeDecodeError:  # pragma: no cover
        # This is required on PY2 when threatening with unicode
        sensor_id = uuid.uuid5(iotnet_id, uuid5_name.encode('utf-8'))

    return str(sensor_id), stream_code


def timedict_to_datetime(tsdict):
    """Given a timestamp dict in form {sec: X, usec: Y} converts it to a datetime"""
    return datetime.fromtimestamp(tsdict['sec'] +
                                  tsdict['usec'] / 1000000)
