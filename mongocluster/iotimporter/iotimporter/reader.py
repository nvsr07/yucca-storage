import json
import ijson
import os
import uuid

READ_STREAMED_JSON = 5


class FileReader(object):
    """Actual FileReader that keeps track of the current entry.

    Reads are performed as a ``generator`` or loading the whole data
    in memory according to the ``generate`` parameter. Calling
    ``read_entries`` will return entries one by one in both cases.

    Permits to recover read from a starting point and keep track of
    the last processed entry so that it can be send back as the
    starting point.
    """
    def __init__(self, filepath, generate=False):
        self._current = -1
        self._filepath = filepath
        self._generate = generate

    @property
    def last_read(self):
        return self._current

    def read_entries(self, skip=0):
        print('Reading %s' % self._filepath)
        sensor_id, stream_code = self.detect_sensor_and_streamcode_from_filename(self._filepath)
        skip = skip or 0
        self._current = 0
        with open(self._filepath, 'rb') as f:
            if self._generate is False:
                entries = self._read_file_inmemory(f, skip)
            elif self._generate is READ_STREAMED_JSON:
                entries = self._read_streamed_json(f, skip)
            else:
                entries = self._read_file_generator(f, skip)

            for e in entries:
                if self._current < skip:
                    print('Skipping: %s < %s' % (self._current, skip))
                else:
                    if sensor_id is not None:
                        e['sensor'] = sensor_id
                    if stream_code is not None:
                        e['streamCode'] = stream_code
                    yield e

                self._current += 1

    @classmethod
    def detect_sensor_and_streamcode_from_filename(cls, filepath):
        _, filename = os.path.split(filepath)
        filename, _ = os.path.splitext(filename)

        try:
            sensor_id, stream_code = filename.split('_', 1)
        except ValueError:
            return None, None

        try:
            uuid.UUID(sensor_id)
        except ValueError:
            print("File name doesn't seem to contain a valid sensor uuid")
            return None, None

        print('! Filename seem to contain a sensor_id:<%s> and stream_code:<%s>, forcing those' %
              (sensor_id, stream_code))
        return sensor_id, stream_code

    def _read_file_inmemory(self, f, skip):
        objs = json.loads(f.read().decode('utf-8'))
        for v in objs.values():
            yield v

    def _read_streamed_json(self, f, skip):
        for l in f.readlines():
            v = json.loads(l.decode('utf-8'))
            yield v

    def _read_file_generator(self, f, skip):
        try:
            events = iter(ijson.parse(f))
            while True:
                current, event, value = next(events)
                if current and '.' not in current:
                    if event in ('start_map', 'start_array'):
                        builder = ijson.ObjectBuilder()
                        end_event = event.replace('start', 'end')
                        while '.' in current or event != end_event:
                            builder.event(event, value)
                            current, event, value = next(events)
                        yield builder.value
        except StopIteration:
            pass


def read_file(filepath, generate=False, skip=0):
    fr = FileReader(filepath, generate)
    return fr.read_entries(skip)
