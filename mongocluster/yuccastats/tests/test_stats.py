from datetime import datetime, timedelta
from bson import ObjectId
from yuccastats.mongodb import (get_support_db, setup_connection, get_data_collections,
                                get_connection)
from yuccastats.stats_steps import step_date, step_metadata_totals, step_gather_tenant_info, \
    step_tenant_totals, step_stream_frequencies, step_monthly_data, step_midnight_data, \
    step_thirtydays_data


class TestStatisticsSteps(object):
    @classmethod
    def setup_class(cls):
        setup_connection('mongodb://localhost',
                         'YUCCASTATS_TESTS_DB_STATS',
                         'test_statistics',
                         'YUCCASTATS_TESTS_DB_SUPPORT')

        tenants = [
            {"idTenant": -4, "tenantName": "smartlab",
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
             "archiveMeasuresCollectionDb": "YUCCASTATS_TESTS_DB_smartlab"},
            {"idTenant": -1, "tenantName": "csp",
             "tenantDescription": "CSP - Innovazione nelle ICT",
             "tenantCode": "test-csp",
             "dataCollectionName": "data",
             "dataCollectionDb": "YUCCASTATS_TESTS_DB_csp",
             "measuresCollectionName": "Measures",
             "measuresCollectionDb": "YUCCASTATS_TESTS_DB_csp",
             "socialCollectionName": "social",
             "socialCollectionDb": "YUCCASTATS_TESTS_DB_csp",
             "mediaCollectionName": "media",
             "mediaCollectionDb": "YUCCASTATS_TESTS_DB_csp",
             "archiveDataCollectionName": "archivedata",
             "archiveDataCollectionDb": "YUCCASTATS_TESTS_DB_csp",
             "archiveMeasuresCollectionName": "archivemeasures",
             "archiveMeasuresCollectionDb": "YUCCASTATS_TESTS_DB_csp"},
        ]
        get_support_db().tenant.insert(tenants)
        cls.tenants = [t['tenantCode'] for t in tenants]

        cls.counts = {}
        for tenant_info in tenants:
            cols = get_data_collections(tenant_info)
            for idx, col in enumerate(cols):
                items_to_insert = (idx+1)*10
                cls.counts[col.name] = items_to_insert

                ids = [ObjectId.from_datetime(datetime(2001, 1, 1, 1, x) + timedelta(days=x))
                       for x in range(items_to_insert)]
                col.insert((dict(count=i, _id=ids[i]) for i in range(items_to_insert)))

    @classmethod
    def teardown_class(cls):
        get_connection().drop_database('YUCCASTATS_TESTS_DB_SUPPORT')
        get_connection().drop_database('YUCCASTATS_TESTS_DB_smartlab')
        get_connection().drop_database('YUCCASTATS_TESTS_DB_csp')

    def test_date(self):
        day = datetime(2001, 1, 8)
        stats, __ = step_date({}, day)

        assert (stats['datetime'] - datetime.utcnow()) < timedelta(seconds=5)
        assert stats['date'] == {'week': 2, 'weekday': 1, 'day': 8, 'month': 1, 'year': 2001}

    def test_metadata_stats(self):
        stats, __ = step_metadata_totals({'lifetime': {}})
        assert stats['lifetime'] == {'total_tenants': 2,
                                     'total_streams': 0,
                                     'total_smart_objects': 0}, stats

    def test_stream_frequency_without_streams(self):
        stats, __ = step_stream_frequencies({'lifetime': {}})
        assert stats['lifetime']['stream_frequency'] == {
            'tags': [],
            'tenants': [],
            'domain': []
        }

    def test_tenant_totals(self):
        stats, kwargs = step_gather_tenant_info({'lifetime': {}})
        stats, __ = step_tenant_totals(stats, **kwargs)

        assert len(stats['lifetime']['tenant_total_data']) == len(self.tenants), \
            stats['lifetime']['tenant_total_data']

        for tenant in self.tenants:
            for col_name, count in self.counts.items():
                col_name = col_name.lower()
                assert stats['lifetime']['tenant_total_data'][tenant][col_name] == count, \
                    stats['lifetime']['tenant_total_data'][tenant]

        for col_name, count in self.counts.items():
            col_name = col_name.lower()
            assert stats['lifetime']['total_data'][col_name] == count*len(self.tenants)

    def test_monthly(self):
        stats, kwargs = step_gather_tenant_info({'monthly': {}})
        stats, __ = step_monthly_data(stats, datetime(2001, 1, 1), **kwargs)
        assert stats['monthly'] == {'total_data': {
            'media': self.counts['media']*len(self.tenants),
            'data': self.counts['data']*len(self.tenants),
            'social': self.counts['social']*len(self.tenants),
            'measures': self.counts['measures']*len(self.tenants)
        }, 'origin': datetime(2001, 1, 1, 0, 0)}

    def test_midnight(self):
        stats, kwargs = step_gather_tenant_info({'monthly': {}})
        stats, __ = step_midnight_data(stats, datetime(2001, 2, 9), **kwargs)
        assert stats['midnight'] == {'total_data': {
            'media': self.counts['media']*len(self.tenants) / 40,  # 2 each day
            'data': 0,
            'social': 0,
            'measures': 0
        }, 'origin': datetime(2001, 2, 9, 0, 0)}, stats

    def test_thirtydays(self):
        stats, kwargs = step_gather_tenant_info({'30days': {}})
        stats, __ = step_thirtydays_data(stats, datetime(2001, 1, 10), **kwargs)

        removed_items = len(self.tenants)*9  # remove first 9 days of data (gets 31 of 40 days)
        expected = {'total_data': {
            'media': self.counts['media']*len(self.tenants) - removed_items,
            'data': self.counts['data']*len(self.tenants) - removed_items,
            'social': self.counts['social']*len(self.tenants) - removed_items,
            'measures': self.counts['measures']*len(self.tenants) - removed_items
        }, 'origin': datetime(2001, 1, 10, 0, 0)}

        assert stats['30days'] == expected, stats


class TestStatisticsStepsWithStreams(object):
    @classmethod
    def setup_class(cls):
        setup_connection('mongodb://localhost',
                         'YUCCASTATS_TESTS_DB_STATS',
                         'test_statistics',
                         'YUCCASTATS_TESTS_DB_SUPPORT')

        tenants = [
            {"idTenant": -4, "tenantName": "smartlab",
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
             "archiveMeasuresCollectionDb": "YUCCASTATS_TESTS_DB_smartlab"},
            {"idTenant": -1, "tenantName": "csp",
             "tenantDescription": "CSP - Innovazione nelle ICT",
             "tenantCode": "test-csp",
             "dataCollectionName": "data",
             "dataCollectionDb": "YUCCASTATS_TESTS_DB_csp",
             "measuresCollectionName": "Measures",
             "measuresCollectionDb": "YUCCASTATS_TESTS_DB_csp",
             "socialCollectionName": "social",
             "socialCollectionDb": "YUCCASTATS_TESTS_DB_csp",
             "mediaCollectionName": "media",
             "mediaCollectionDb": "YUCCASTATS_TESTS_DB_csp",
             "archiveDataCollectionName": "archivedata",
             "archiveDataCollectionDb": "YUCCASTATS_TESTS_DB_csp",
             "archiveMeasuresCollectionName": "archivemeasures",
             "archiveMeasuresCollectionDb": "YUCCASTATS_TESTS_DB_csp"},
        ]
        get_support_db().tenant.insert(tenants)

        streams = [
            {"idStream": 12,
             "streamCode": "temperature", "streamName": "Temperatura rilevata",
             "configData": {"idTenant": 4, "tenantCode": "test-smartlab", "idDataset": 1,
                            "datasetVersion": 2},
             "streams": {
                 "stream": {
                     "idVirtualEntity": 9, "idCategoriaVe": 3, "idTipoVe": 1,
                     "virtualEntityName": "ArduinoReference",
                     "virtualEntityDescription": "Arduino Reference",
                     "virtualEntityCode": "550e8400-e29b-41d4-a716-446655440000",
                     "virtualEntityType": "Device", "virtualEntityCategory": "Smart",
                     "lastUpdate": "never", "lastMessage": "never",
                     "streamStatus": "notused",
                     "domainStream": "ENVIRONMENT",
                     "components": {"element": [
                         {"idComponent": 68, "componentName": "c0",
                          "componentAlias": "c0",
                          "tolerance": 0,
                          "idMeasureUnit": 7, "measureUnit": "C",
                          "measureUnitCategory": "temperature",
                          "idPhenomenon": 2, "phenomenon": "air temperature",
                          "phenomenonCategory": "environment",
                          "dataType": "float",
                          "idDataType": 4}]
                     },
                     "streamTags": {"tags": [{"tagCode": "AIR"}]}}
             }},
            {"idStream": 13,
             "streamCode": "temperature", "streamName": "Temperatura rilevata",
             "configData": {"idTenant": 4, "tenantCode": "test-smartlab", "idDataset": 1,
                            "datasetVersion": 2},
             "streams": {
                 "stream": {
                     "idVirtualEntity": 9, "idCategoriaVe": 3, "idTipoVe": 1,
                     "virtualEntityName": "ArduinoReference",
                     "virtualEntityDescription": "Arduino Reference",
                     "virtualEntityCode": "550e8400-e29b-41d4-a716-446655440000",
                     "virtualEntityType": "Device", "virtualEntityCategory": "Smart",
                     "lastUpdate": "never", "lastMessage": "never",
                     "streamStatus": "notused",
                     "domainStream": "SCHOOL",
                     "components": {"element": [
                         {"idComponent": 68, "componentName": "c0",
                          "componentAlias": "c0",
                          "tolerance": 0,
                          "idMeasureUnit": 7, "measureUnit": "C",
                          "measureUnitCategory": "temperature",
                          "idPhenomenon": 2, "phenomenon": "air temperature",
                          "phenomenonCategory": "environment",
                          "dataType": "float",
                          "idDataType": 4},
                         {"idComponent": 69, "componentName": "c0",
                          "componentAlias": "c0",
                          "tolerance": 0,
                          "idMeasureUnit": 7, "measureUnit": "C",
                          "measureUnitCategory": "temperature",
                          "idPhenomenon": 2, "phenomenon": "air temperature",
                          "phenomenonCategory": "environment",
                          "dataType": "float",
                          "idDataType": 5}]
                     },
                     "streamTags": {"tags": [{"tagCode": "AIR"},
                                             {"tagCode": "POLLUTION"}]}}
             }},
        ]

        for stream in streams:
            get_support_db()['stream'].save(stream)

    @classmethod
    def teardown_class(cls):
        get_connection().drop_database('YUCCASTATS_TESTS_DB_SUPPORT')
        get_connection().drop_database('YUCCASTATS_TESTS_DB_smartlab')
        get_connection().drop_database('YUCCASTATS_TESTS_DB_csp')

    def test_metadata_stats_with_streams(self):
        stats, __ = step_metadata_totals({'lifetime': {}})
        assert stats['lifetime'] == {'total_tenants': 2,
                                     'total_streams': 2,
                                     'total_smart_objects': 1}, stats

    def test_stream_frequencies(self):
        stats, __ = step_stream_frequencies({'lifetime': {}})
        stats = stats['lifetime']['stream_frequency']

        assert stats['domain'] == [{'count': 1, '_id': 'SCHOOL'},
                                   {'count': 1, '_id': 'ENVIRONMENT'}]
        assert stats['tags'] == [{'count': 2, '_id': 'AIR'},
                                 {'count': 1, '_id': 'POLLUTION'}]
        assert stats['tenants'] == [{'count': 2, '_id': 'test-smartlab'}]
