from datetime import datetime, timedelta
from bson import ObjectId
from yuccausage.mongodb import (get_support_db, setup_connection, get_data_collections,
                                get_connection)
from yuccausage.stats_steps import (step_date, step_gather_tenant_info, step_tenant_totals,
                                    step_monthly_data, step_midnight_data, step_thirtydays_data,
                                    step_sevendays_data)


class TestStatisticsSteps(object):
    @classmethod
    def setup_class(cls):
        setup_connection('mongodb://localhost',
                         'yuccausage_TESTS_DB_STATS',
                         'test_statistics',
                         'yuccausage_TESTS_DB_SUPPORT')

        tenants = [
            {"idTenant": -4, "tenantName": "smartlab",
             "tenantDescription": "Smartlab and reference environment",
             "tenantCode": "test-smartlab",
             "dataCollectionName": "data",
             "dataCollectionDb": "yuccausage_TESTS_DB_smartlab",
             "measuresCollectionName": "measures",
             "measuresCollectionDb": "yuccausage_TESTS_DB_smartlab",
             "socialCollectionName": "social",
             "socialCollectionDb": "yuccausage_TESTS_DB_smartlab",
             "mediaCollectionName": "media",
             "mediaCollectionDb": "yuccausage_TESTS_DB_smartlab",
             "archiveDataCollectionName": "archivedata",
             "archiveDataCollectionDb": "yuccausage_TESTS_DB_smartlab",
             "archiveMeasuresCollectionName": "archivemeasures",
             "archiveMeasuresCollectionDb": "yuccausage_TESTS_DB_smartlab",
             "binaryCollectionName": "binary",
             "binaryCollectionDb": "yuccausage_TESTS_DB_smartlab"},
            {"idTenant": -1, "tenantName": "csp",
             "tenantDescription": "CSP - Innovazione nelle ICT",
             "tenantCode": "test-csp",
             "dataCollectionName": "data",
             "dataCollectionDb": "yuccausage_TESTS_DB_csp",
             "measuresCollectionName": "measures",
             "measuresCollectionDb": "yuccausage_TESTS_DB_csp",
             "socialCollectionName": "social",
             "socialCollectionDb": "yuccausage_TESTS_DB_csp",
             "mediaCollectionName": "media",
             "mediaCollectionDb": "yuccausage_TESTS_DB_csp",
             "archiveDataCollectionName": "archivedata",
             "archiveDataCollectionDb": "yuccausage_TESTS_DB_csp",
             "archiveMeasuresCollectionName": "archivemeasures",
             "archiveMeasuresCollectionDb": "yuccausage_TESTS_DB_csp",
             "binaryCollectionName": "binary",
             "binaryCollectionDb": "yuccausage_TESTS_DB_csp"},
        ]

        streams = [{
            "idStream": 12,
            "streamCode": "temperature",
            "streamName": "Temperatura rilevata",
            "configData": {
                "idTenant": -4,
                "tenantCode": "test-smartlab",
                "idDataset": 1,
                "datasetVersion": 1
            }
        }, {
            "idStream": 12,
            "streamCode": "temperature",
            "streamName": "Temperatura rilevata",
            "configData": {
                "idTenant": -1,
                "tenantCode": "test-csp",
                "idDataset": 2,
                "datasetVersion": 1
            }
        }]

        metadatas = [{
            "idDataset": 1,
            "datasetCode": "ds_Trfl_3",
            "datasetVersion": 1,
            "configData": {
                "idTenant": -1,
                "tenantCode": "test-smartlab",
                "type": "dataset",
                "subtype": "streamDataset",
                "entityNameSpace": "it.csi.smartdata.odata.csp.ds_Trfl_3",
                "datasetStatus": "inst",
                "current": 1
            },
            "info": {
                "datasetName": "TrFl",
                "description": "Dataset TrafficFlow",
                "license": "CC BY 4.0",
                "copyright": "Copyright (C) 2014, CSP Innovazione nelle ICT. All rights reserved.",
                "visibility": "public",
                "registrationDate": "Nov 25, 2014 4:30:43 PM",
                "dataDomain": "TRANSPORT",
                "fps": 0.017,
                "tags": [
                    {
                        "tagCode": "OUTDOOR"
                    },
                    {
                        "tagCode": "TRAFFIC"
                    }
                ],
                "fields": [
                    {
                        "fieldName": "value",
                        "fieldAlias": "traffic flow",
                        "dataType": "float",
                        "isKey": 0,
                        "measureUnit": "vehicle/min"
                    }
                ]
            }
        }, {
            "idDataset": 2,
            "datasetCode": "ds_Trfl_3",
            "datasetVersion": 1,
            "configData": {
                "idTenant": -1,
                "tenantCode": "test-csp",
                "type": "dataset",
                "subtype": "streamDataset",
                "entityNameSpace": "it.csi.smartdata.odata.csp.ds_Trfl_3",
                "datasetStatus": "inst",
                "current": 1
            },
            "info": {
                "datasetName": "TrFl",
                "description": "Dataset TrafficFlow",
                "license": "CC BY 4.0",
                "copyright": "Copyright (C) 2014, CSP Innovazione nelle ICT. All rights reserved.",
                "visibility": "public",
                "registrationDate": "Nov 25, 2014 4:30:43 PM",
                "dataDomain": "TRANSPORT",
                "fps": 0.017,
                "tags": [
                    {
                        "tagCode": "OUTDOOR"
                    },
                    {
                        "tagCode": "TRAFFIC"
                    }
                ],
                "fields": [
                    {
                        "fieldName": "value",
                        "fieldAlias": "traffic flow",
                        "dataType": "float",
                        "isKey": 0,
                        "measureUnit": "vehicle/min"
                    }
                ]
            }
        }, {
            "idDataset": 1,
            "datasetCode": "ds_Trfl_3",
            "datasetVersion": 1,
            "configData": {
                "idTenant": -1,
                "tenantCode": "test-smartlab",
                "type": "dataset",
                "subtype": "bulkDataset",
                "entityNameSpace": "it.csi.smartdata.odata.csp.ds_Trfl_3",
                "datasetStatus": "inst",
                "current": 1
            },
            "info": {
                "datasetName": "TrFl",
                "description": "Dataset TrafficFlow",
                "license": "CC BY 4.0",
                "copyright": "Copyright (C) 2014, CSP Innovazione nelle ICT. All rights reserved.",
                "visibility": "public",
                "registrationDate": "Nov 25, 2014 4:30:43 PM",
                "dataDomain": "TRANSPORT",
                "fps": 0.017,
                "tags": [
                    {
                        "tagCode": "OUTDOOR"
                    },
                    {
                        "tagCode": "TRAFFIC"
                    }
                ],
                "fields": [
                    {
                        "fieldName": "value",
                        "fieldAlias": "traffic flow",
                        "dataType": "float",
                        "isKey": 0,
                        "measureUnit": "vehicle/min"
                    }
                ]
            }
        }, {
            "idDataset": 2,
            "datasetCode": "ds_Trfl_3",
            "datasetVersion": 1,
            "configData": {
                "idTenant": -1,
                "tenantCode": "test-csp",
                "type": "dataset",
                "subtype": "bulkDataset",
                "entityNameSpace": "it.csi.smartdata.odata.csp.ds_Trfl_3",
                "datasetStatus": "inst",
                "current": 1
            },
            "info": {
                "datasetName": "TrFl",
                "description": "Dataset TrafficFlow",
                "license": "CC BY 4.0",
                "copyright": "Copyright (C) 2014, CSP Innovazione nelle ICT. All rights reserved.",
                "visibility": "public",
                "registrationDate": "Nov 25, 2014 4:30:43 PM",
                "dataDomain": "TRANSPORT",
                "fps": 0.017,
                "tags": [
                    {
                        "tagCode": "OUTDOOR"
                    },
                    {
                        "tagCode": "TRAFFIC"
                    }
                ],
                "fields": [
                    {
                        "fieldName": "value",
                        "fieldAlias": "traffic flow",
                        "dataType": "float",
                        "isKey": 0,
                        "measureUnit": "vehicle/min"
                    }
                ]
            }
        }]
        support_db = get_support_db()
        support_db.tenant.insert(tenants)
        support_db.stream.insert(streams)
        support_db.metadata.insert(metadatas)

        cls.tenants = [t['tenantCode'] for t in tenants]

        cls.counts = {}
        for index, tenant_info in enumerate(tenants):
            cols = get_data_collections(tenant_info)
            for idx, col in enumerate(cols):
                items_to_insert = (idx+1)*10
                cls.counts[col.name] = items_to_insert

                ids = [ObjectId.from_datetime(datetime(2001, 1, 1, 1, x) + timedelta(days=x))
                       for x in range(items_to_insert)]
                for i in range(items_to_insert):
                    col.insert((dict(count=i, _id=ids[i], idDataset=index+1, datasetVersion=1)))

    @classmethod
    def teardown_class(cls):
        get_connection().drop_database('yuccausage_TESTS_DB_SUPPORT')
        get_connection().drop_database('yuccausage_TESTS_DB_smartlab')
        get_connection().drop_database('yuccausage_TESTS_DB_csp')

    def test_date(self):
        day = datetime(2001, 1, 8)
        stats, __ = step_date({}, day)

        assert (stats['datetime'] - datetime.utcnow()) < timedelta(seconds=5)
        assert stats['date'] == {'week': 2, 'weekday': 1, 'day': 8, 'month': 1, 'year': 2001}

    def test_tenant_totals(self):
        stats, kwargs = step_gather_tenant_info({'lifetime': {'tenant_total_data': {},
                                                              'tenant_streams_measures_data': {},
                                                              'tenant_data_datasets_data': {}}})
        stats, __ = step_tenant_totals(stats, **kwargs)

        assert len(stats['lifetime']['tenant_total_data']) == len(self.tenants), \
            stats['lifetime']['tenant_total_data']

        for tenant in self.tenants:
            for col_name, count in self.counts.items():
                col_name = col_name.lower()
                assert stats['lifetime']['tenant_total_data'][tenant][col_name] == count, \
                    stats['lifetime']['tenant_total_data'][tenant]

    def test_monthly(self):
        stats, kwargs = step_gather_tenant_info({'monthly': {'tenant_total_data': {},
                                                             'tenant_streams_measures_data': {},
                                                             'tenant_data_datasets_data': {}}})
        stats, __ = step_monthly_data(stats, datetime(2001, 1, 1), **kwargs)
        assert stats['monthly'] == {
            'tenant_total_data': {
                'test-csp': {
                    'media': self.counts['media'],
                    'data': self.counts['data'],
                    'social': self.counts['social'],
                    'binary': self.counts['binary'],
                    'measures': self.counts['measures']
                },
                'test-smartlab': {
                    'media': self.counts['media'],
                    'data': self.counts['data'],
                    'social': self.counts['social'],
                    'binary': self.counts['binary'],
                    'measures': self.counts['measures']
                }
            },
            'tenant_streams_measures_data': {
                'test-smartlab': {12: {'total': self.counts['measures'], 'visibility': 'public'}},
                'test-csp': {12: {'total': self.counts['measures'], 'visibility': 'public'}}},
            'tenant_data_datasets_data': {
                'test-smartlab': {
                    1: {
                        1: {'total': self.counts['data'], 'visibility': 'public'}
                    }
                },
                'test-csp': {
                    2: {
                        1: {'total': self.counts['data'], 'visibility': 'public'}}}
            },
            'origin': datetime(2001, 1, 1, 0, 0)}, stats

    def test_midnight(self):
        stats, kwargs = step_gather_tenant_info({'midnight': {'tenant_total_data': {},
                                                              'tenant_streams_measures_data': {},
                                                              'tenant_data_datasets_data': {}}})
        stats, __ = step_midnight_data(stats, datetime(2001, 2, 9), **kwargs)
        assert stats['midnight'] == {'origin': datetime(2001, 2, 9, 0, 0),
                                     'tenant_total_data': {
                                         'test-smartlab': {
                                             'measures': 0,
                                             'social': 0,
                                             'media': 1,
                                             'data': 0,
                                             'binary': 11
                                         },
                                         'test-csp': {
                                             'measures': 0,
                                             'social': 0,
                                             'media': 1,
                                             'data': 0,
                                             'binary': 11
                                         }
                                     },
                                     'tenant_streams_measures_data': {
                                         'test-smartlab': {12: {'total': 0,
                                                                'visibility': 'public'}},
                                         'test-csp': {12: {'total': 0,
                                                           'visibility': 'public'}}},
                                     'tenant_data_datasets_data': {
                                         'test-smartlab': {
                                             1: {
                                                 1: {'total': 0, 'visibility': 'public'}}},
                                         'test-csp': {
                                             2: {
                                                 1: {'total': 0, 'visibility': 'public'}}}}}, stats

    def test_thirtydays(self):
        stats, kwargs = step_gather_tenant_info({'30days': {'tenant_total_data': {},
                                                            'tenant_streams_measures_data': {},
                                                            'tenant_data_datasets_data': {}}})
        stats, __ = step_thirtydays_data(stats, datetime(2001, 1, 10), **kwargs)

        removed_items = 9  # remove first 9 days of data (gets 31 of 40 days)

        expected = {
            'tenant_total_data': {
                'test-csp': {
                    'media': self.counts['media'] - removed_items,
                    'data': self.counts['data'] - removed_items,
                    'social': self.counts['social'] - removed_items,
                    'binary': self.counts['binary'] - removed_items,
                    'measures': self.counts['measures'] - removed_items
                },
                'test-smartlab': {
                    'media': self.counts['media'] - removed_items,
                    'data': self.counts['data'] - removed_items,
                    'social': self.counts['social'] - removed_items,
                    'binary': self.counts['binary'] - removed_items,
                    'measures': self.counts['measures'] - removed_items
                }
            },
            'tenant_streams_measures_data': {
                'test-smartlab': {12: {'total': self.counts['measures'] - removed_items,
                                       'visibility': 'public'}},
                'test-csp': {12: {'total': self.counts['measures'] - removed_items,
                                  'visibility': 'public'}}},
            'tenant_data_datasets_data': {
                'test-smartlab': {
                    1: {
                        1: {'total': self.counts['data'] - removed_items, 'visibility': 'public'}}},
                'test-csp': {
                    2: {
                        1: {'total': self.counts['data'] - removed_items, 'visibility': 'public'}}}
            },
            'origin': datetime(2001, 1, 10, 0, 0)}

        assert stats['30days'] == expected, stats

    def test_sevendays(self):
        stats, kwargs = step_gather_tenant_info({'7days': {'tenant_total_data': {},
                                                           'tenant_streams_measures_data': {},
                                                           'tenant_data_datasets_data': {}}})
        stats, __ = step_sevendays_data(stats, datetime(2001, 1, 8), **kwargs)

        removed_items = 7

        expected = {
            'tenant_total_data': {
                'test-csp': {
                    'media': self.counts['media'] - removed_items,
                    'data': self.counts['data'] - removed_items,
                    'social': self.counts['social'] - removed_items,
                    'binary': self.counts['binary'] - removed_items,
                    'measures': self.counts['measures'] - removed_items
                },
                'test-smartlab': {
                    'media': self.counts['media'] - removed_items,
                    'data': self.counts['data'] - removed_items,
                    'social': self.counts['social'] - removed_items,
                    'binary': self.counts['binary'] - removed_items,
                    'measures': self.counts['measures'] - removed_items
                }
            },
            'tenant_streams_measures_data': {
                'test-smartlab': {12: {'total': self.counts['measures'] - removed_items,
                                       'visibility': 'public'}},
                'test-csp': {12: {'total': self.counts['measures'] - removed_items,
                                  'visibility': 'public'}}},
            'tenant_data_datasets_data': {
                'test-smartlab': {
                    1: {
                        1: {'total': self.counts['data'] - removed_items, 'visibility': 'public'}}},
                'test-csp': {
                    2: {
                        1: {'total': self.counts['data'] - removed_items, 'visibility': 'public'}}}
            },
            'origin': datetime(2001, 1, 8, 0, 0)}

        assert stats['7days'] == expected, stats


class TestStatisticsStepsWithStreams(object):
    @classmethod
    def setup_class(cls):
        setup_connection('mongodb://localhost',
                         'yuccausage_TESTS_DB_STATS',
                         'test_statistics',
                         'yuccausage_TESTS_DB_SUPPORT')

        tenants = [
            {"idTenant": -4, "tenantName": "smartlab",
             "tenantDescription": "Smartlab and reference environment",
             "tenantCode": "test-smartlab",
             "dataCollectionName": "data",
             "dataCollectionDb": "yuccausage_TESTS_DB_smartlab",
             "measuresCollectionName": "measures",
             "measuresCollectionDb": "yuccausage_TESTS_DB_smartlab",
             "socialCollectionName": "social",
             "socialCollectionDb": "yuccausage_TESTS_DB_smartlab",
             "mediaCollectionName": "media",
             "mediaCollectionDb": "yuccausage_TESTS_DB_smartlab",
             "archiveDataCollectionName": "archivedata",
             "archiveDataCollectionDb": "yuccausage_TESTS_DB_smartlab",
             "archiveMeasuresCollectionName": "archivemeasures",
             "archiveMeasuresCollectionDb": "yuccausage_TESTS_DB_smartlab",
             "binaryCollectionName": "binary",
             "binaryCollectionDb": "yuccausage_TESTS_DB_smartlab"},
            {"idTenant": -1, "tenantName": "csp",
             "tenantDescription": "CSP - Innovazione nelle ICT",
             "tenantCode": "test-csp",
             "dataCollectionName": "data",
             "dataCollectionDb": "yuccausage_TESTS_DB_csp",
             "measuresCollectionName": "measures",
             "measuresCollectionDb": "yuccausage_TESTS_DB_csp",
             "socialCollectionName": "social",
             "socialCollectionDb": "yuccausage_TESTS_DB_csp",
             "mediaCollectionName": "media",
             "mediaCollectionDb": "yuccausage_TESTS_DB_csp",
             "archiveDataCollectionName": "archivedata",
             "archiveDataCollectionDb": "yuccausage_TESTS_DB_csp",
             "archiveMeasuresCollectionName": "archivemeasures",
             "archiveMeasuresCollectionDb": "yuccausage_TESTS_DB_csp",
             "binaryCollectionName": "binary",
             "binaryCollectionDb": "yuccausage_TESTS_DB_csp"},
        ]
        get_support_db().tenant.insert(tenants)

        streams = [
            {"idStream": 12,
             "streamCode": "temperature", "streamName": "Temperatura rilevata",
             "configData": {"idTenant": 4, "tenantCode": "test-smartlab", "idDataset": 1,
                            "datasetVersion": 1},
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
                            "datasetVersion": 1},
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
        get_connection().drop_database('yuccausage_TESTS_DB_SUPPORT')
        get_connection().drop_database('yuccausage_TESTS_DB_smartlab')
        get_connection().drop_database('yuccausage_TESTS_DB_csp')

