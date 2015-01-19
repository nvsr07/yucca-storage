import json
import os
from datetime import datetime
from itertools import islice
from nose.tools import raises
from iotimporter.utils import frequency2fps, sensor_from_name, timedict_to_datetime
from iotimporter.mongodb import get_support_db, get_measures_collection_names, setup_connection, \
    _clear_mongodb_configuration, _get_tenant_default_db_name


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


class TestCollectionLookupUtilities(object):
    @classmethod
    def setup_class(cls):
        setup_connection('mongodb://localhost', 'test_tenant')

        tenants = [
            {"idTenant": -4, "tenantName": "smartlab",
             "tenantDescription": "Smartlab and reference environment",
             "tenantCode": "test-smartlab",
             "dataCollectionName": "data", "dataCollectionDb": "DB_smartlab",
             "measuresCollectionName": "measures", "measuresCollectionDb": "DB_smartlab",
             "socialCollectionName": "social", "socialCollectionDb": "DB_smartlab",
             "mediaCollectionName": "media", "mediaCollectionDb": "DB_smartlab",
             "archiveDataCollectionName": "archivedata", "archiveDataCollectionDb": "DB_smartlab",
             "archiveMeasuresCollectionName": "archivemeasures",
             "archiveMeasuresCollectionDb": "DB_smartlab"},
            {"idTenant": -1, "tenantName": "csp",
             "tenantDescription": "CSP - Innovazione nelle ICT",
             "tenantCode": "test-csp",
             "dataCollectionName": "data", "dataCollectionDb": "DB_csp",
             "measuresCollectionName": "Measures", "measuresCollectionDb": "DB_csp",
             "socialCollectionName": "social", "socialCollectionDb": "DB_csp",
             "mediaCollectionName": "media", "mediaCollectionDb": "DB_csp",
             "archiveDataCollectionName": "archivedata", "archiveDataCollectionDb": "DB_csp",
             "archiveMeasuresCollectionName": "archivemeasures",
             "archiveMeasuresCollectionDb": "DB_csp"},
            {"idTenant": -2, "tenantName": "CIPPALIPPA",
             "tenantDescription": "CIPPALIPPA - Innovazione nelle ICT",
             "tenantCode": "test-cippalippa"}
        ]
        get_support_db().tenant.insert(tenants)

        config_entries = [
            {"idDataset": -1, "datasetCode": "test_ds_Temperature_1", "datasetVersion": 2,
             "configData": {"idTenant": -4, "tenantCode": "test-smartlab",
                            "type": "dataset", "subtype": "streamDataset",
                            "collection": "CIPPALIPPA",
                            "entityNameSpace": "it.csi.smartdata.odata.smartlab.ds_Temperature_1",
                            "datasetStatus": "inst", "current": 1},
             "info": {"datasetName": "temperature",
                      "description": "Dataset Temperatura rilevata",
                      "license": "CC BY 4.0", "visibility": "public",
                      "registrationDate": "Nov 25, 2014 4:22:11 PM",
                      "dataDomain": "ENVIRONMENT", "fps": 0.6,
                      "tags": [{"tagCode": "AIR"}], "fields": [{
                                                    "fieldName": "c0",
                                                    "fieldAlias": "air temperature",
                                                    "dataType": "float",
                                                    "isKey": 0,
                                                    "measureUnit": "C"}]}},
            {"idDataset": -2, "datasetCode": "test_ds_Trfl_2", "datasetVersion": 2,
             "configData": {"idTenant": -1, "tenantCode": "test-csp", "type": "dataset",
                            "subtype": "streamDataset",
                            "database": "CIPPALIPPADB",
                            "entityNameSpace": "it.csi.smartdata.odata.csp.ds_Trfl_2",
                            "datasetStatus": "inst", "current": 1},
             "info": {"datasetName": "TrFl", "description": "Dataset TrafficFlow",
                      "license": "CC BY 4.0",
                      "copyright": "Copyright (C) 2014, CSP",
                      "visibility": "public",
                      "registrationDate": "Nov 25, 2014 4:29:52 PM",
                      "dataDomain": "TRANSPORT", "fps": 0.017,
                      "tags": [{"tagCode": "OUTDOOR"}, {"tagCode": "TRAFFIC"}],
                      "fields": [{"fieldName": "value", "fieldAlias": "traffic flow",
                                  "dataType": "float", "isKey": 0,
                                  "measureUnit": "vehicle/min"}]}},
            {"idDataset": -3, "datasetCode": "test_ds_Trfl_3", "datasetVersion": 2,
             "configData": {"idTenant": -1, "tenantCode": "test-csp", "type": "dataset",
                            "subtype": "streamDataset",
                            "entityNameSpace": "it.csi.smartdata.odata.csp.ds_Trfl_3",
                            "datasetStatus": "inst", "current": 1},
             "info": {"datasetName": "TrFl", "description": "Dataset TrafficFlow",
                      "license": "CC BY 4.0",
                      "copyright": "Copyright (C) 2014, CSP",
                      "visibility": "public",
                      "registrationDate": "Nov 25, 2014 4:29:52 PM",
                      "dataDomain": "TRANSPORT", "fps": 0.017,
                      "tags": [{"tagCode": "OUTDOOR"}, {"tagCode": "TRAFFIC"}],
                      "fields": [{"fieldName": "value", "fieldAlias": "traffic flow",
                                  "dataType": "float", "isKey": 0,
                                  "measureUnit": "vehicle/min"}]}},
            {"idDataset": -4, "datasetCode": "test_ds_Trfl_4", "datasetVersion": 2,
             "configData": {"idTenant": -2, "tenantCode": "test-cippalippa", "type": "dataset",
                            "subtype": "streamDataset",
                            "entityNameSpace": "it.csi.smartdata.odata.csp.ds_Trfl_3",
                            "datasetStatus": "inst", "current": 1},
             "info": {"datasetName": "TrFl", "description": "Dataset TrafficFlow",
                      "license": "CC BY 4.0",
                      "copyright": "Copyright (C) 2014, CSP",
                      "visibility": "public",
                      "registrationDate": "Nov 25, 2014 4:29:52 PM",
                      "dataDomain": "TRANSPORT", "fps": 0.017,
                      "tags": [{"tagCode": "OUTDOOR"}, {"tagCode": "TRAFFIC"}],
                      "fields": [{"fieldName": "value", "fieldAlias": "traffic flow",
                                  "dataType": "float", "isKey": 0,
                                  "measureUnit": "vehicle/min"}]}}
        ]
        get_support_db().metadata.insert(config_entries)

    @classmethod
    def teardown_class(cls):
        get_support_db().tenant.remove({'idTenant': {'$lt': 0}})
        get_support_db().metadata.remove({'idDataset': {'$lt': 0}})

    def test_measures_collection_detection_onmetadata(self):
        db, col = get_measures_collection_names('test-smartlab', -1)
        assert db == 'DB_smartlab', db
        assert col == 'CIPPALIPPA', col

    def test_measures_database_detection_onmetadata(self):
        db, col = get_measures_collection_names('test-csp', -2)
        assert db == 'CIPPALIPPADB', db
        assert col == 'Measures', col

    def test_measures_col_and_db_detection_tenant(self):
        db, col = get_measures_collection_names('test-csp', -3)
        assert db == 'DB_csp', db
        assert col == 'Measures', col

    def test_measures_col_and_db_detection_default(self):
        db, col = get_measures_collection_names('test-cippalippa', -4)
        assert db == 'DB_test_tenant', db
        assert col == 'measures', col

    @raises(LookupError)
    def test_missing_tenant_info(self):
        get_measures_collection_names('test-missing', -1)

    @raises(LookupError)
    def test_missing_dataset_info(self):
        get_measures_collection_names('test-cippalippa', -10)


class TestMongoDBConnection(object):
    def setup(self):
        _clear_mongodb_configuration()

    @raises(RuntimeError)
    def test_missing_db_configuration_is_detected(self):
        get_support_db()

    @raises(RuntimeError)
    def test_missing_tenant_configuration_is_detected(self):
        _get_tenant_default_db_name()
