from itertools import islice, chain
from pymongo.errors import BulkWriteError
from .mongodb import get_measures_collection


def insert_dataset(entries, conversion, bulk_size=1, limit=None):
    """Inserts a entries into the tenant measures DB

    Inserted entries are converted using ``conversion``
    function before being inserted. The ``bulk_size``
    option permits to insert data in groups of
    specified size with Bulk insertions, but makes error
    reporting more unreliable.

    ``_limit`` parameter is mostly for testing and should not
    be relied on.
    """
    if limit and bulk_size > 1:
        raise ValueError('limit cannot be used with bulks bigger than 1 item')

    entries, measures_col = lookup_measures_col(entries, conversion)
    if entries is None:
        return

    if measures_col is None:  # pragma: nocover
        # Should never happen as lookup_measures_col raises exceptions
        raise LookupError('Unable to lookup measures collection from data entries')

    total = 0
    print('Inserting on %s' % measures_col)
    for idx, group in enumerate(islicegroups(entries, bulk_size)):
        print('> Inserting: ~%s' % idx)
        bulk = measures_col.initialize_ordered_bulk_op()
        for entry in group:
            bulk.insert(conversion(entry))

        try:
            res = bulk.execute()
            total += res['nInserted']
        except BulkWriteError as e:
            total += e.details['nInserted']
            raise InsertError(total,
                              e.details.get('writeErrors', ({},))[0].get('errmsg'))

        if limit and total >= limit:
            # Not reliable test in case bulk_size > 1,
            # only used in test suite.
            break

    print('Inserted %s Documents' % total)


def islicegroups(iterable, n):
    """Splits an iterator in a subset of iterators each of fixed size."""
    i = iter(iterable)
    piece = list(islice(i, n))
    while piece:
        yield piece
        piece = list(islice(i, n))


def lookup_measures_col(entries, conversion):
    """Looks up where a set of entries should be inserted.

    Given the entries and conversion function will try to lookup the database
    and collection where they should be written to from the ``idDataset`` in
    the first entry.
    """
    entries = iter(entries)

    try:
        first_entry = next(entries)
    except StopIteration:
        return None, None

    entry_with_dataset = conversion(first_entry)

    return (chain((first_entry,), entries),
            get_measures_collection(entry_with_dataset.get('idDataset')))


class InsertError(Exception):
    def __init__(self, index, msg):
        super(InsertError, self).__init__(msg)
        self.index = index
