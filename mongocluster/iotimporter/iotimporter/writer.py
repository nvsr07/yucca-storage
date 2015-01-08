from itertools import islice
from pymongo.errors import BulkWriteError
from .mongodb import get_tenant_db


def insert_dataset(entries, conversion, bulk_size=1, _limit=None):
    """Inserts a entries into the tenant measures DB

    Inserted entries are converted using ``conversion``
    function before being inserted. The ``bulk_size``
    option permits to insert data in groups of
    specified size with Bulk insertions, but makes error
    reporting more unreliable.

    ``_limit`` parameter is mostly for testing and should not
    be relied on.
    """
    tenant_db = get_tenant_db()
    measures_col = tenant_db.measures

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

        if _limit is not None and total >= _limit:
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


class InsertError(Exception):
    def __init__(self, index, msg):
        super(InsertError, self).__init__(msg)
        self.index = index
