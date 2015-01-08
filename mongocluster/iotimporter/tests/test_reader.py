import json
import os
from iotimporter.reader import read_file


class ReaderTests(object):
    @classmethod
    def setupClass(cls):
        testdir = os.path.dirname(__file__)
        cls.sample_file = os.path.join(testdir, 'example.json')

        with open(cls.sample_file) as samples:
            cls.samples = json.loads(samples.read())

    def _read_samples(self):
        return read_file(self.sample_file, self.using_generator)

    def test_reader_entries_count(self):
        entries = list(self._read_samples())
        entries_len = len(entries)
        samples_len = len(self.samples.values())
        assert entries_len == samples_len, (entries_len, samples_len)

    def test_reader_entries(self):
        entries = self._read_samples()
        sorted_entries = sorted(entries, key=lambda e: e['_id']['$id'])
        sorted_samples = sorted(self.samples.values(), key=lambda e: e['_id']['$id'])

        first_entry = sorted_entries[0]
        first_sample = sorted_samples[0]
        assert first_entry == first_sample, (first_entry, first_sample)
        assert sorted_entries == sorted_samples


class TestInMemoryReader(ReaderTests):
    using_generator = False


class TestGeneratedReader(ReaderTests):
    using_generator = True
