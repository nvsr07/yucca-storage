import json
import os
from datetime import datetime
from itertools import islice
from nose.tools import raises
from iotimporter.utils import frequency2fps, sensor_from_name, timedict_to_datetime


class TestFrequencyConversion(object):
    @classmethod
    def setupClass(cls):
        testdir = os.path.dirname(__file__)
        sample_file = os.path.join(testdir, 'example.json')

        with open(sample_file) as samples:
            cls.samples = json.loads(samples.read())

    def test_measure_sec(self):
        sample_frequency = 1.0 / 300.0  # All measures in test file are 300fps
        for measure in islice(self.samples.values(), 10):
            assert frequency2fps(measure['Frequency']) == sample_frequency, measure

    def test_measure_minute(self):
        converted_value = frequency2fps('10min')
        assert converted_value == 1.0 / (10.0 * 60.0), converted_value

    @raises(ValueError)
    def test_invalid_mesaure(self):
        frequency2fps('invalid')

    @raises(ValueError)
    def test_invalid_value(self):
        frequency2fps(u'0x300sec')

    @raises(ValueError)
    def test_invalid_unit(self):
        frequency2fps('10lightyear')

    def test_zero_means_zero(self):
        frequency2fps('0') == 0

    def test_zero_value_means_zero(self):
        frequency2fps('0sec') == 0


class TestNameConversion(object):
    def test_correct_name(self):
        sensor_id, streamCode = sensor_from_name('CSI_Light_WSN001')
        assert sensor_id.count('-') == 4, sensor_id
        assert streamCode == 'Light', streamCode

    def test_calling_twice_gives_same_result(self):
        sensor_id1, streamCode1 = sensor_from_name('CSI_Light_WSN001')
        sensor_id2, streamCode2 = sensor_from_name('CSI_Light_WSN001')
        assert sensor_id1 == sensor_id2, (sensor_id1, sensor_id2)
        assert streamCode1 == streamCode2, (streamCode1, streamCode2)


class TestDateTimeConversion(object):
    def test_with_sample(self):
        d = {
            "sec": 1412249400,
            "usec": 0
        }

        assert timedict_to_datetime(d) == datetime(2014, 10, 2, 13, 30)

    def test_with_usec(self):
        d = {
            "sec": 1412249400,
            "usec": 1000000
        }

        assert timedict_to_datetime(d) == datetime(2014, 10, 2, 13, 30, 1)
