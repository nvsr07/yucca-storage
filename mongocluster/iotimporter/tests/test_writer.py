import json
import os
import pymongo
from iotimporter.converter import convert
from iotimporter.mongodb import setup_connection, get_tenant_db, get_support_db
from iotimporter.writer import insert_dataset, InsertError


class TestWriter(object):
    @classmethod
    def setupClass(cls):
        setup_connection('mongodb://localhost', 'test_tenant')
        get_tenant_db().drop_collection('measures')
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

    @classmethod
    def teardownClass(cls):
        get_support_db().stream.remove({'configData.tenantCode': 'test_tenant'})

    def teardown(self):
        get_tenant_db().drop_collection('measures')

    def test_writer_inserts_all(self):
        entries = ({'count': c} for c in range(1000))
        insert_dataset(entries, conversion=lambda x: x)
        assert get_tenant_db().measures.find({}).count() == 1000

    def test_writer_matches_data(self):
        converted_samples = list(sorted((convert(s) for s in self.samples.values()),
                                        key=lambda e: e['time']))

        insert_dataset(self.samples.values(), conversion=convert)
        inserted_data = list(get_tenant_db().measures.find({}).sort('time', pymongo.ASCENDING))

        assert len(inserted_data) == len(converted_samples)
        for x, y in zip(converted_samples, inserted_data):
            y.pop('_id')
            assert x == y, (x, y)

    def test_bulkinsertion_error(self):
        entries = [{'count': c} for c in range(20)]
        get_tenant_db().measures.ensure_index('count', unique=True)

        insert_dataset(entries[10:], conversion=lambda x: x)

        try:
            insert_dataset(entries, conversion=lambda x: x)
        except InsertError as e:
            assert e.index == 10, e
        else:
            assert False, 'Insertion Error not detected'
