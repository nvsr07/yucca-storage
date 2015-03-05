About iotimporter
=================

iotimporter is a set of tools to import data from CSP Internet Of Things to
CSI MongoDB based Internet Of Things.

Installing
==========

iotimporter supports Python **2.7** and **3.4**.
The ``iotimport`` command can be installed running::

    $ python setup.py install

from inside the iotimporter directory.
This will install the program system wide, running with ``sudo``,
might be required on some systems.

In case install process fails complaining about missing ``setuptools``,
you might be required to install ``python setuptools`` through your system
package manager or you might download and install it manually with::

    $ wget https://bootstrap.pypa.io/ez_setup.py -O - | sudo python

.. note::

    If the system runs other python softwares, to avoid messing with system
    wide packages and dependencies, installing in a ``virtualenv`` is usually
    suggested. Additional information regarding python virtual environments
    can be found at: http://docs.python-guide.org/en/latest/dev/virtualenvs/

Running
=======

To run iot importer just start it after install::

    $ iotimport filename mongodb://url:port tenant_code

EXAMPLE::

    $ iotimport tests/example.json mongodb://localhost csp

For a complete list of valid options run::

    $ iotimport --help
    usage: iotimport [-h] [-s SKIP] [-b BULK] [-p {inmemory,generated,streamed}]
                     [--sensordata] [-l LIMIT]
                     file mongourl tenantid

    import data from CSP Internet Of Things to CSI MongoDB based Internet Of
    Things.

    positional arguments:
      file                  .json file to import
      mongourl              mongodb URL to connect to (mongodb://server:port)
      tenantid              id of the tenant owner of the imported data

    optional arguments:
      -h, --help            show this help message and exit
      -s SKIP, --skip SKIP  Skip first SKIP elements, using --skip 1 will skip the
                            first entry. This can be used to recover a previous
                            insertion from the point where it stopped.
      -b BULK, --bulk BULK  Size of insertion group for Bulk Write, more than 1
                            makes insertion faster but error reporting more
                            unreliable in case of errors.
      -p {inmemory,generated,streamed}, --parser {inmemory,generated,streamed}
                            Parser that has to be used to read the json file,
                            "streamed" means that the input is a stream of JSON
                            Documents. "inmemory" and "generated" both read a
                            dictionary of documents where the key is the _id of
                            the document and the value is thedocument itself.
                            "inmemory" is a faster parser that might solvesome
                            issues with JSON Parser and speeds up the import
                            process at the cost of as much memory as the whole
                            dataset
      --sensordata          Instead of performing data insertion just print
                            sensors data to configure the sensor before insertion.
      -l LIMIT, --limit LIMIT
                            Limit the inserted entries to the specified amount. (0
                            means no limit)

Supported Parsers
-----------------

iotimporter supports three kind of parsers:

    * ``streamed`` which reads from a stream of JSON documents,
      the imported file is expected to be a sequence of JSON documents
      one for each line. *This is the default parser*
    * ``inmemory`` which reads the whole file in memory as a unique
      JSON document. This expects the file to be a unique JSON document
      where the keys are the entries id and the values are the entries
      themselves. This is the fastest parser for legacy dumps.
    * ``generated`` like the ``inmemory`` parser expects the file to
      contain an unique JSON document but instead of loading the whole
      file in memory it tries to parse one by one the entries listed
      into the file. This is the parser that requires less memory for
      legacy dumps.

Supported Dump Formats
----------------------

iotimporter supports two different kind of measures dumps that can be imported.

The default format, where each entry is a JSON document separated by a new line::

    { "Allowance" : "0.001", "Author" : "CSP s.c. a r.l. Innovazione nelle ICT", "Description" : "Average Wind Speed at Ciardoney glacier", "Frequency" : "10min", "Location" : [ 45.522, 7.407, 2850 ], "Model" : "Campbell 05103-5", "Name" : "Anemometer (AVG)", "ObservedProperty" : "WindSpeedA", "Refer" : "Ciardoney", "Supply" : "Mixed", "Timestamp" : { "$date" : 1422226800000 }, "Type" : "Sensor", "Unit" : "ms", "Validity" : "VALID", "Value" : "3.257", "_id" : { "$oid" : "54c5d800e82abb291b000010" } }


The legacy format, where each entry is a subdocument inside an unique huge document::

    {
        "542d6bf2e82abbf122000088": {
            "_id": {
                "$id": "542d6bf2e82abbf122000088"
            },
            "Name": "CSI_Light_WSN001",
            "Model": "Phidgets 1127",
            "Location": ["45.085642", "7.697700", "230"],
            "Description": "Light sensor",
            "Type": "Sensor",
            "Timestamp": {
                "sec": 1412255700,
                "usec": 0
            },
            "ObservedProperty": "Lightning",
            "Unit": "Lux",
            "Value": "450.78",
            "Allowance": "1.00",
            "Frequency": "300sec",
            "Author": "CSP s.c. a r.l. Innovazione nelle ICT",
            "Refer": "HALADINs",
            "Supply": "None"
        },
        ...
    }

``Timestap`` is always supported both as::

    "Timestamp": {
        "sec": 1412255700,
        "usec": 0
    },

and::

    "Timestamp" : {
        "$date" : 1422226800000
    }

both will be converted to an UTC datetime.

SensorID and streamCode
-----------------------

SensorID and streamCode are supported in three different ways:

    1. *provided inside the filename.*
       If the file is named ``UUID_streamCode.json`` the ``sensor UUID`` and
       the ``streamCode`` will be read from the filename.
    2. *provided inside the document*
       If the file has a different naming convention but ``sensor`` and ``streamCode``
       keys are inside the the imported documents those are used.
    3. *Generated from the "Name" field*
       If the ``Name`` field contains 3 values separated by ``_`` those three values
       are considered to be ``project``, ``streamCode`` and ``WSN code``. From the
       ``WSN Code`` and the ``project`` the ``sensor UUID`` is generated, while the
       ``streamCode`` is used as is.

Destination DB and Collection Detection
---------------------------------------

Both for destination DATABASE and COLLECTION when writing data,
the iotimporter detects them from the ``tenant_code`` argument and
from the ``idDataset`` specified in data itself.

Collection is looked up according to this chain:

    1) ``metadata.configData.collection`` for the ``tenant_code`` and ``idDataset``
       provided by *first entry* to insert.
    2) ``tenant.measuresCollectionName`` for the ``tenant_code``
    3) Fallback on ``measures``

Database is looked up according to this chain:

    1) ``metadata.configData.database`` for the ``tenant_code`` and ``idDataset``
       provided by *first entry* to insert.
    2) ``tenant.measuresCollectionDb`` for the ``tenant_code``
    3) Fallback on ``DB_[tenant_code]``


Recover In Case Of Errors
-------------------------

In case of errors during Insertion, a *red* message will be printed
on the console reporting the last inserted item. If the insertion
was performed 1 item at time (default ``--bulk`` option value) the
insertion process can be recovered from there using the ``--skip``
option with the same value reported by the error message.

In case insertion was performed with bulks greater than 1, recovering
will only be possible for write errors and not for networks errors or
other generic errors. In this case to recover insertion is necessary to
check on the database which items got inserted and properly decide
the ``--skip`` parameter according to the values already on DB.

Getting Sensor and Stream Metadata
==================================

Before being able to import the data, it is necessary that the sensor
and stream are properly configured. To get the basic data required to
configure both the ``--sensordata`` option can be used. It will print
the sensor and stream metadata as dictionaries::

    $ iotimport --sensordata tests/example.json mongodb://localhost:27017 csp
    Reading tests/example.json
    SENSOR DATA
    {'category': 'smart',
     'description': 'Light sensor',
     'id': 'b42e342f-d87b-56bf-96ef-c52b313ba61e',
     'location': {'disposition': 'fixed',
                  'exposure': 'outdoor',
                  'positions': [{'ele': 230.0, 'lat': 45.085642, 'lon': 7.6977}]},
     'model': 'Phidgets 1127',
     'name': 'CSI_Light_WSN001',
     'status': 'active',
     'supply': 'network',
     'version': '1.0'}

    STREAM DATA
    {'components': [{'allowance': 1.0,
                     'event': 'Lightning',
                     'id': 'value',
                     'type': 'float',
                     'unit': 'Lux'}],
     'copyright': 'Copyright (C) 2014, CSP Innovazione nelle ICT. All rights reserved.',
     'domain': 'IoTNet',
     'fps': 0.0033333333333333335,
     'id': 'Light',
     'license': 'CC BY 4.0',
     'sensor': 'b42e342f-d87b-56bf-96ef-c52b313ba61e',
     'tags': [],
     'visibility': 'public'}

Running Test Suite
==================

IOTImport comes with a full test suite that checks the importer
respects some requirements and properly handles errors.

To run the test suite just move yourself inside the source code
and run::

    $ pip install -e .[testing]
    $ python setup.py nosetests

It will correctly install test suite dependencies and run it::

    tests.test_conversion.TestDatasetLookup.test_all_samples_imported ... ok
    tests.test_conversion.TestDatasetLookup.test_lookup ... ok
    tests.test_conversion.TestDatasetLookup.test_notexisting_lookup ... ok
    tests.test_conversion.TestMeasuresConversion.test_conversion ... ok
    tests.test_conversion.TestMeasuresConversion.test_convert_location ... ok
    tests.test_conversion.TestMeasuresConversion.test_converter_forces_sensor_and_stream_code ... ok
    tests.test_conversion.TestMeasuresConversion.test_converter_skips_invalid_names ... ok
    tests.test_conversion.TestMeasuresConversion.test_converter_verifies_name ... ok
    tests.test_reader.TestGeneratedReader.test_reader_entries ... ok
    tests.test_reader.TestGeneratedReader.test_reader_entries_count ... ok
    tests.test_reader.TestInMemoryReader.test_reader_entries ... ok
    tests.test_reader.TestInMemoryReader.test_reader_entries_count ... ok
    tests.test_reader.TestStreamedReader1.test_invalid_stream_id ... ok
    tests.test_reader.TestStreamedReader1.test_reader_entries ... ok
    tests.test_reader.TestStreamedReader1.test_reader_entries_count ... ok
    tests.test_reader.TestStreamedReader2.test_reader_entries ... ok
    tests.test_reader.TestStreamedReader2.test_reader_entries_count ... ok
    tests.test_reader.TestStreamedReader2.test_stream_code_and_sensor_id_by_filename ... ok
    tests.test_readwrite.TestReadWrite.test_insert_correctly_detects_last_inserted ... ok
    tests.test_readwrite.TestReadWrite.test_insert_correctly_recovers_insertion ... ok
    tests.test_readwrite.TestReadWrite.test_insert_samples_twice ... ok
    tests.test_utils.TestCollectionLookupUtilities.test_measures_col_and_db_detection_default ... ok
    tests.test_utils.TestCollectionLookupUtilities.test_measures_col_and_db_detection_tenant ... ok
    tests.test_utils.TestCollectionLookupUtilities.test_measures_collection_detection_onmetadata ... ok
    tests.test_utils.TestCollectionLookupUtilities.test_measures_database_detection_onmetadata ... ok
    tests.test_utils.TestCollectionLookupUtilities.test_missing_dataset_info ... ok
    tests.test_utils.TestCollectionLookupUtilities.test_missing_tenant_info ... ok
    tests.test_utils.TestDateTimeConversion.test_datetime_for_data ... ok
    tests.test_utils.TestDateTimeConversion.test_with_mongodate ... ok
    tests.test_utils.TestDateTimeConversion.test_with_sample ... ok
    tests.test_utils.TestDateTimeConversion.test_with_usec ... ok
    tests.test_utils.TestFrequencyConversion.test_invalid_mesaure ... ok
    tests.test_utils.TestFrequencyConversion.test_invalid_unit ... ok
    tests.test_utils.TestFrequencyConversion.test_invalid_value ... ok
    tests.test_utils.TestFrequencyConversion.test_measure_minute ... ok
    tests.test_utils.TestFrequencyConversion.test_measure_sec ... ok
    tests.test_utils.TestFrequencyConversion.test_zero_means_zero ... ok
    tests.test_utils.TestFrequencyConversion.test_zero_value_means_zero ... ok
    tests.test_utils.TestMongoDBConnection.test_missing_db_configuration_is_detected ... ok
    tests.test_utils.TestMongoDBConnection.test_missing_tenant_configuration_is_detected ... ok
    tests.test_utils.TestNameConversion.test_calling_twice_gives_same_result ... ok
    tests.test_utils.TestNameConversion.test_correct_name ... ok
    tests.test_writer.TestWriter.test_bulkinsertion_error ... ok
    tests.test_writer.TestWriter.test_limit_prevents_bulks ... ok
    tests.test_writer.TestWriter.test_limit_zero_means_no_limit ... ok
    tests.test_writer.TestWriter.test_writer_can_limit_entries ... ok
    tests.test_writer.TestWriter.test_writer_empty_dataset_generator ... ok
    tests.test_writer.TestWriter.test_writer_empty_dataset_list ... ok
    tests.test_writer.TestWriter.test_writer_inserts_all ... ok
    tests.test_writer.TestWriter.test_writer_matches_data ... ok

    Name                    Stmts   Miss  Cover   Missing
    -----------------------------------------------------
    iotimporter                 0      0   100%
    iotimporter.converter      31      0   100%
    iotimporter.mongodb        45      0   100%
    iotimporter.reader         72      0   100%
    iotimporter.utils          50      0   100%
    iotimporter.writer         43      0   100%
    -----------------------------------------------------
    TOTAL                     241      0   100%
    ----------------------------------------------------------------------
    Ran 50 tests in 3.124s

    OK
