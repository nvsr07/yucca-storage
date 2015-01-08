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
    usage: iotimport [-h] [-s SKIP] [-b BULK] [-m] file mongourl tenantid

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
      -b BULK, --bulk BULK  Size of insertion group for Bulk Writes, more than 1
                            makes insertion faster but error reporting more
                            unreliable in case of errors.
      -m, --inmemory        Read data in memory instead of reading it line by
                            line, might solve some issues with JSON Parser and
                            speeds up the import process at the cost of as much
                            memory as the whole dataset.
      --sensordata          Instead of performing data insertion just print
                            sensors data to configure the sensor before insertion.

.. note::

    By default the importer reads the data using a generative
    parser, which means that memory consumption will be constant
    as only memory used is to parse the current entry.
    In case the generative parser has issues reading the data or
    it is required to speed up the insertion process the ``-m``
    option can be used. This will switch to a faster *in memory*
    parser the reads the whole dataset in memory before parsing it.
    This will usually result in a 3x speed boost at the cost of
    as much memory as the whole dataset used.

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
     'tags': ['tag1', 'tag2'],
     'visibility': 'public'}

Running Test Suite
==================

IOTImport comes with a full test suite that checks the importer
respects some requirements and properly handles errors.

To run the test suite just move yourself inside the source code
and run::

    $ pip install -e .[testing]
    $ python setup.py nosetests

It will correctly install test suite dependencies and run it.
