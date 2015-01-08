import json
import os
import pymongo
from iotimporter.converter import convert
from iotimporter.mongodb import setup_connection, get_tenant_db, get_support_db
from iotimporter.reader import FileReader
from iotimporter.writer import insert_dataset, InsertError


class TestReadWrite(object):
    @classmethod
    def setupClass(cls):
        setup_connection('mongodb://localhost', 'test_tenant')
        get_tenant_db().drop_collection('measures')
        get_support_db().stream.remove({'configData.tenantCode': 'test_tenant'})

        testdir = os.path.dirname(__file__)
        cls.sample_file = os.path.join(testdir, 'example.json')
        with open(cls.sample_file) as samples:
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

    def setup(self):
        get_tenant_db().measures.ensure_index((('sensor', pymongo.ASCENDING),
                                               ('idDataset', pymongo.ASCENDING),
                                               ('datasetVersion', pymongo.ASCENDING),
                                               ('time', pymongo.DESCENDING)),
                                              unique=True)

    def teardown(self):
        get_tenant_db().drop_collection('measures')

    def test_insert_samples_twice(self):
        reader = FileReader(self.sample_file, generate=True)
        insert_dataset(
            reader.read_entries(skip=0),
            convert,
            bulk_size=1
        )

        assert get_tenant_db().measures.find().count() == len(self.samples)

    def test_insert_correctly_detects_last_inserted(self):
        reader = FileReader(self.sample_file, generate=True)
        insert_dataset(
            reader.read_entries(skip=10),
            convert,
            bulk_size=1
        )

        try:
            insert_dataset(
                reader.read_entries(skip=0),
                convert,
                bulk_size=1
            )
        except InsertError as e:
            assert e.index == reader.last_read
        else:
            assert False, 'Should have raise InsertError'

    def test_insert_correctly_recovers_insertion(self):
        reader = FileReader(self.sample_file, generate=True)
        insert_dataset(
            reader.read_entries(skip=10),
            convert,
            bulk_size=1,
            _limit=1
        )
        total_inserted = get_tenant_db().measures.find().count()
        assert total_inserted == 1

        skip = 0
        try:
            insert_dataset(
                reader.read_entries(skip=skip),
                convert,
                bulk_size=100
            )
        except InsertError as e:
            skip = e.index

        skip += 1  # 1 was already inserted by first insert
        insert_dataset(
            reader.read_entries(skip=skip),
            convert,
            bulk_size=100
        )

        total_inserted = get_tenant_db().measures.find().count()
        expected = len(self.samples)
        assert total_inserted == expected
