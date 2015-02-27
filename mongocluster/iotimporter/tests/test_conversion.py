import json
import os
from itertools import islice
from nose.tools import raises
from iotimporter.mongodb import get_support_db, setup_connection
from iotimporter.utils import sensor_from_name
from iotimporter.converter import lookup_dataset, convert


class BaseConversionTests(object):
    @classmethod
    def setupClass(cls):
        setup_connection('mongodb://localhost', 'test_tenant')
        get_support_db().stream.remove({'configData.tenantCode': 'test_tenant'})

        testdir = os.path.dirname(__file__)
        sample_file = os.path.join(testdir, 'example.json')
        with open(sample_file) as samples:
            cls.samples = json.loads(samples.read())

        dataset_info = {
            'idStream': -1,
            'streamCode': 'Light',
            'streamName': 'Lightning',
            'configData': {
                'idTenant': 1,
                'tenantCode': 'test_tenant',
                'idDataset': -99,
                'datasetVersion': -100,
            },
            'streams': {'stream': {
                'idVirtualEntity': 18,
                'idCategoriaVe': 3,
                'idTipoVe': 1,
                'virtualEntityName': 'CSI_FrmHyd_WSN001',
                'virtualEntityDescription': 'Formaldehyde sensor Haladins',
                'virtualEntityCode': 'b42e342f-d87b-56bf-96ef-c52b313ba61e',
                'virtualEntityType': 'Device',
                'virtualEntityCategory': 'Smart',
                'lastUpdate': 'never',
                'lastMessage': 'never',
                'streamStatus': 'draft',
                'domainStream': 'ENVIRONMENT',
                'licence': 'CC BY 4.0',
                'disclaimer': None,
                'copyright': 'Copyright (C) 2014, CSP Innovazione nelle ICT. All rights reserved.',
                'visibility': 'public',
                'saveData': 1,
                'fabricControllerOutcome': "Code: ok; Descr: BHO;    Operaction: inst;      ",
                'deploymentVersion': -1,
                'deploymentStatusCode': 'inst',
                'deploymentStatusDesc': 'installed',
                'publishStream': 0,
                'fps': 0.0033,
                'privacyAcceptance': 1,
                'registrationDate': '2014-10-08',
                'requesterName': 'xhesiand',
                'requesterSurname': 'topalli',
                'requesterMail': 'xhesiand.topalli@consulenti.csi.it',
                'internalQuery': None,
                'streamInternalChildren': {'streamChildren': []},
                'components': {'element': [{
                    'idComponent': 95,
                    'componentName': 'value',
                    'componentAlias': 'value',
                    'tolerance': 1,
                    'idMeasureUnit': 1,
                    'measureUnit': 'lux',
                    'measureUnitCategory': 'lightning',
                    'idPhenomenon': 6,
                    'phenomenon': 'lightning',
                    'phenomenonCategory': 'environment',
                    'dataType': 'float',
                    'idDataType': 4,
                }]},
                'streamTags': {'tags': [{'tagCode': 'INDOOR'},
                               {'tagCode': 'QUALITY'}]},
            }},
        }

        get_support_db().stream.save(dataset_info)

        dataset_info = {
            'idStream': -1,
            'streamCode': 'STREAMCODE',
            'streamName': 'Lightning',
            'configData': {
                'idTenant': 1,
                'tenantCode': 'test_tenant',
                'idDataset': -98,
                'datasetVersion': -100,
            },
            'streams': {'stream': {
                'idVirtualEntity': 17,
                'idCategoriaVe': 3,
                'idTipoVe': 1,
                'virtualEntityName': 'CSI_FrmHyd_WSN001',
                'virtualEntityDescription': 'Formaldehyde sensor Haladins',
                'virtualEntityCode': 'SENSORID',
                'virtualEntityType': 'Device',
                'virtualEntityCategory': 'Smart',
                'lastUpdate': 'never',
                'lastMessage': 'never',
                'streamStatus': 'draft',
                'domainStream': 'ENVIRONMENT',
                'licence': 'CC BY 4.0',
                'disclaimer': None,
                'copyright': 'Copyright (C) 2014, CSP Innovazione nelle ICT. All rights reserved.',
                'visibility': 'public',
                'saveData': 1,
                'fabricControllerOutcome': "Code: ok; Descr: BHO;    Operaction: inst;      ",
                'deploymentVersion': -1,
                'deploymentStatusCode': 'inst',
                'deploymentStatusDesc': 'installed',
                'publishStream': 0,
                'fps': 0.0033,
                'privacyAcceptance': 1,
                'registrationDate': '2014-10-08',
                'requesterName': 'xhesiand',
                'requesterSurname': 'topalli',
                'requesterMail': 'xhesiand.topalli@consulenti.csi.it',
                'internalQuery': None,
                'streamInternalChildren': {'streamChildren': []},
                'components': {'element': [{
                    'idComponent': 95,
                    'componentName': 'value',
                    'componentAlias': 'value',
                    'tolerance': 1,
                    'idMeasureUnit': 1,
                    'measureUnit': 'lux',
                    'measureUnitCategory': 'lightning',
                    'idPhenomenon': 6,
                    'phenomenon': 'lightning',
                    'phenomenonCategory': 'environment',
                    'dataType': 'float',
                    'idDataType': 4,
                }]},
                'streamTags': {'tags': [{'tagCode': 'INDOOR'},
                               {'tagCode': 'QUALITY'}]},
            }},
        }

        get_support_db().stream.save(dataset_info)

    @classmethod
    def teardownClass(cls):
        get_support_db().stream.remove({'configData.tenantCode': 'test_tenant'})


class TestDatasetLookup(BaseConversionTests):
    def test_lookup(self):
        sensor_id, stream_code = sensor_from_name('CSI_Light_WSN001')
        id_dataset, dataset_version = lookup_dataset('test_tenant', stream_code, sensor_id)
        assert id_dataset == -99, id_dataset
        assert dataset_version == -100, dataset_version

    @raises(ValueError)
    def test_notexisting_lookup(self):
        sensor_id, stream_code = sensor_from_name('CSI_Light_WSN001')
        lookup_dataset('test_tenant', stream_code[:-1], sensor_id)

    def test_all_samples_imported(self):
        correctly_found = 0

        for sample in self.samples.values():
            sensor_id, stream_code = sensor_from_name(sample['Name'])
            id_dataset, dataset_version = lookup_dataset('test_tenant', stream_code, sensor_id)
            assert id_dataset == -99, id_dataset
            assert dataset_version == -100, dataset_version
            correctly_found += 1

        assert correctly_found == len(self.samples)


class TestMeasuresConversion(BaseConversionTests):
    def test_conversion(self):
        for sample in self.samples.values():
            converted = convert(sample)
            assert converted['value'] == sample['Value'], converted

    def test_convert_location(self):
        sample = list(islice(self.samples.values(), 1))[-1]
        sample['Location'] = ["45.1052", "7.66285", "247"]

        converted_loc = convert(sample)['idxLocation']
        assert converted_loc[0] == 45.1052, converted_loc
        assert converted_loc[1] == 7.66285, converted_loc

    def test_converter_forces_sensor_and_stream_code(self):
        sample = list(islice(self.samples.values(), 2))[-1]
        sample['sensor'] = 'SENSORID'
        sample['streamCode'] = 'STREAMCODE'

        converted = convert(sample)
        assert converted['sensor'] == 'SENSORID', converted
        assert converted['streamCode'] == 'STREAMCODE', converted

    def test_converter_skips_invalid_names(self):
        sample = list(islice(self.samples.values(), 3))[-1]
        sample['Name'] = 'INVALID'
        sample['sensor'] = 'SENSORID'
        sample['streamCode'] = 'STREAMCODE'

        converted = convert(sample)
        assert converted['sensor'] == 'SENSORID', converted
        assert converted['streamCode'] == 'STREAMCODE', converted

    def test_converter_verifies_name(self):
        sample = list(islice(self.samples.values(), 4))[-1]
        sample['Name'] = 'INVALID'

        try:
            convert(sample)
        except ValueError as e:
            assert '"sensor" and "streamCode" cannot be generated from "Name"' in str(e), e
        else:
            assert False, sample
