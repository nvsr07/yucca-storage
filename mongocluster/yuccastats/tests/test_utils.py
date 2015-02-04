from datetime import datetime, timedelta
from nose.tools import raises
from yuccastats.mongodb import (get_support_db, setup_connection,
                                _clear_mongodb_configuration, get_statistics_collection,
                                get_data_collections)
from yuccastats.utils import first_month_day, thirtydays_ago, begin_of_day, isodate


class TestCollectionLookupUtilities(object):
    @classmethod
    def setup_class(cls):
        setup_connection('mongodb://localhost',
                         'YUCCASTATS_TESTS_DB_STATS',
                         'test_statistics',
                         'YUCCASTATS_TESTS_DB_SUPPORT')

    def test_connected_collection(self):
        col = get_statistics_collection()
        assert col.full_name == 'YUCCASTATS_TESTS_DB_STATS.test_statistics', col.full_name

    def test_tenant_collections(self):
        tenant_info = {"idTenant": -4, "tenantName": "smartlab",
                       "tenantDescription": "Smartlab and reference environment",
                       "tenantCode": "test-smartlab",
                       "dataCollectionName": "data",
                       "dataCollectionDb": "YUCCASTATS_TESTS_DB_smartlab",
                       "measuresCollectionName": "measures",
                       "measuresCollectionDb": "YUCCASTATS_TESTS_DB_smartlab",
                       "socialCollectionName": "social",
                       "socialCollectionDb": "YUCCASTATS_TESTS_DB_smartlab",
                       "mediaCollectionName": "media",
                       "mediaCollectionDb": "YUCCASTATS_TESTS_DB_smartlab",
                       "archiveDataCollectionName": "archivedata",
                       "archiveDataCollectionDb": "YUCCASTATS_TESTS_DB_smartlab",
                       "archiveMeasuresCollectionName": "archivemeasures",
                       "archiveMeasuresCollectionDb": "YUCCASTATS_TESTS_DB_smartlab"}

        measures_col, data_col, social_col, media_col = get_data_collections(
            tenant_info
        )

        assert measures_col.full_name == 'YUCCASTATS_TESTS_DB_smartlab.measures'
        assert data_col.full_name == 'YUCCASTATS_TESTS_DB_smartlab.data'
        assert social_col.full_name == 'YUCCASTATS_TESTS_DB_smartlab.social'
        assert media_col.full_name == 'YUCCASTATS_TESTS_DB_smartlab.media'


class TestMongoDBConnection(object):
    def setup(self):
        _clear_mongodb_configuration()

    @raises(RuntimeError)
    def test_missing_db_configuration_is_detected(self):
        get_support_db()

    @raises(RuntimeError)
    def test_missing_stats_col_configuration_is_detected(self):
        get_statistics_collection()


class TestDateTimeUtils(object):
    def test_first_month_day(self):
        assert first_month_day(datetime(2001, 1, 17, 11, 32, 52)) == datetime(2001, 1, 1)

    def test_begin_of_day(self):
        assert begin_of_day(datetime(2001, 1, 17, 11, 32, 52)) == datetime(2001, 1, 17)

    def test_thirtydays_ago(self):
        assert thirtydays_ago(datetime(2001, 1, 31, 11, 32, 52)) == datetime(2001, 1, 1)

    def test_isodate(self):
        assert isodate('2001-01-01') == datetime(2001, 1, 1)

    def test_yestertday(self):
        now = datetime.now()
        expected = now - timedelta(days=1)
        assert isodate('yesterday').date() == expected.date()
