import json
import os
from iotimporter.reader import read_file, READ_STREAMED_JSON


class ReaderTests(object):
    @classmethod
    def setupClass(cls):
        testdir = os.path.dirname(__file__)
        cls.sample_file = os.path.join(testdir, cls.sample_file)

        with open(cls.sample_file) as samples:
            data = samples.read()
            if cls.using_generator is READ_STREAMED_JSON:
                # If testing a json stream, convert it to a list of objects.
                data = ''.join(('[', data.strip().replace('\n', ','), ']'))

            cls.samples = json.loads(data)

            if cls.using_generator is not READ_STREAMED_JSON:
                # For example.json we want only the values, not the keys
                cls.samples = cls.samples.values()

    def _read_samples(self):
        return read_file(self.sample_file, self.using_generator)

    def test_reader_entries_count(self):
        entries = list(self._read_samples())
        entries_len = len(entries)
        samples_len = len(self.samples)
        assert entries_len == samples_len, (entries_len, samples_len)

    def test_reader_entries(self):
        entries = list(self._read_samples())

        # Pop forced sensor and stream_code if available
        for e in entries:
            e.pop('sensor', None)
            e.pop('streamCode', None)

        def _get_item_id(e):
            _id = e['_id']
            return _id.get('$id') or _id.get('$oid')

        sorted_entries = sorted(entries, key=_get_item_id)
        sorted_samples = sorted(self.samples, key=_get_item_id)

        first_entry = sorted_entries[0]
        first_sample = sorted_samples[0]
        assert first_entry == first_sample, (first_entry, first_sample)
        assert sorted_entries == sorted_samples, (first_entry, first_sample)


class TestInMemoryReader(ReaderTests):
    using_generator = False
    sample_file = 'example.json'


class TestGeneratedReader(ReaderTests):
    using_generator = True
    sample_file = 'example.json'


class TestStreamedReader1(ReaderTests):
    using_generator = READ_STREAMED_JSON
    sample_file = 'example_streamed.json'

    def test_invalid_stream_id(self):
        entries = list(self._read_samples())
        first_entry = entries[0]

        assert 'sensor' not in first_entry
        assert 'streamCode' not in first_entry


class TestStreamedReader2(ReaderTests):
    using_generator = READ_STREAMED_JSON
    sample_file = '51800824-c084-42e2-a01c-fc3534506c87_Mmb.json'

    def test_stream_code_and_sensor_id_by_filename(self):
        entries = list(self._read_samples())
        first_entry = entries[0]

        assert first_entry['sensor'] == '51800824-c084-42e2-a01c-fc3534506c87', first_entry
        assert first_entry['streamCode'] == 'Mmb', first_entry
