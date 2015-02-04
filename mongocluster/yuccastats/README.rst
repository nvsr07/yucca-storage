About yuccastats
================

yuccastats is a script to gather statistics for YUCCA platform and can
save them on a support collection for third party entities to read them back.

Installing
==========

yuccastats supports Python **2.7** and **3.4**.
The ``yuccastats`` command can be installed running::

    $ python setup.py install

from inside the yuccastats directory.
This will install the program system wide, running with ``sudo``,
might be required on some systems.

In case install process fails complaining about missing ``setuptools``,
you might be required to install ``python setuptools`` through your system
package manager or you might download and install it manually with::

    $ wget https://bootstrap.pypa.io/ez_setup.py -O - | sudo python

.. note::

    If the system runs other python software, to avoid messing with system
    wide packages and dependencies, installing in a ``virtualenv`` is usually
    suggested. Additional information regarding python virtual environments
    can be found at: http://docs.python-guide.org/en/latest/dev/virtualenvs/

Running
=======

To run yuccastats just start it after install::

    $ yuccastats mongodb://url:port/db day

EXAMPLE::

    $ yuccastats mongodb://localhost/DB_SUPPORT 2015-01-29

OR::

    $ yuccastats mongodb://localhost/DB_SUPPORT yesterday

Due to issues with the way the script might be scheduled the script explicitly requires
the ``day`` parameter which refers to the day for which the stats should be collected.
The ``day`` parameter supports the ``yesterday`` value to gather stats starting from the
previous day.
*This can be used if the script is started every day at midnight to gather daily statistics*.

Command Line Options
--------------------

yuccastats script provides some options to change where the statistics should
be written to. By default stats are just printed, using the ``--write`` option
they can be saved on DB in a collection specified by ``-c`` and ``-d`` options::

    usage: yuccastats [-h] [-c COLLECTION] [-d DATABASE] [-w] mongourl day

    Gathers cross tenant statistics from the YUCCA platform.

    positional arguments:
      mongourl              mongodb URL to connect to (mongodb://server:port)
      day                   Gather stats starting from midnight of this day. Can
                            be "yesterday" if you want to gather past day stats.

    optional arguments:
      -h, --help            show this help message and exit
      -c COLLECTION, --collection COLLECTION
                            name of the collection which should contains stats
      -d DATABASE, --database DATABASE
                            name of the database where to store the stats
      -w, --write           Write statistics on DB instead of printing them.


Gathered Statistics
===================

The scripts will gather three kind of statistics:

    - Global Stats.
    - Stats of data gathered from a specific day.
    - Stats of data gathered from the begin of the month of the specific day.
    - Stats of data gathered since 30 days before the specified day.

The common use case is to gather data for *the past day*, in this case the script
can be scheduled at **midnight** and started with ``yesterday`` as the ``day`` parameter.

Recovered statistics are in the form::

    {'date': {'day': 1, 'month': 2, 'week': 5, 'weekday': 7, 'year': 2015},
     'datetime': datetime.datetime(2015, 2, 2, 16, 3, 17, 3961),
     'lifetime': {'stream_frequency': {'domain': [{'_id': 'SCHOOL',
                                                   'count': 25},
                                                  {'_id': 'ENVIRONMENT',
                                                   'count': 14},
                                                  {'_id': 'TRANSPORT',
                                                   'count': 3}],
                                       'tags': [{'_id': 'QUALITY', 'count': 38},
                                                {'_id': 'INDOOR', 'count': 35},
                                                {'_id': 'AIR', 'count': 30},
                                                {'_id': 'POLLUTION',
                                                 'count': 5},
                                                {'_id': 'TRAFFIC', 'count': 3},
                                                {'_id': 'OUTDOOR', 'count': 3},
                                                {'_id': 'RAIN', 'count': 1}],
                                       'tenants': [{'_id': 'csp', 'count': 41},
                                                   {'_id': 'smartlab',
                                                    'count': 1}]},
                  'tenant_total_data': {'all4all': {'data': 0,
                                                    'measures': 0,
                                                    'media': 0,
                                                    'social': 0},
                                        'comfortsense': {'data': 0,
                                                         'measures': 0,
                                                         'media': 0,
                                                         'social': 0},
                                        'csp': {'data': 0,
                                                'measures': 52069,
                                                'media': 0,
                                                'social': 0},
                                        'eden': {'data': 0,
                                                 'measures': 0,
                                                 'media': 0,
                                                 'social': 0},
                                        'elise': {'data': 0,
                                                  'measures': 0,
                                                  'media': 0,
                                                  'social': 0},
                                        'esgp': {'data': 0,
                                                 'measures': 0,
                                                 'media': 0,
                                                 'social': 0},
                                        'healthcommons': {'data': 0,
                                                          'measures': 0,
                                                          'media': 0,
                                                          'social': 0},
                                        'idem': {'data': 0,
                                                 'measures': 0,
                                                 'media': 0,
                                                 'social': 0},
                                        'iotibevo': {'data': 0,
                                                     'measures': 0,
                                                     'media': 0,
                                                     'social': 0},
                                        'leo': {'data': 0,
                                                'measures': 0,
                                                'media': 0,
                                                'social': 0},
                                        'ondeuwc': {'data': 0,
                                                    'measures': 0,
                                                    'media': 0,
                                                    'social': 0},
                                        'pitagora': {'data': 0,
                                                     'measures': 0,
                                                     'media': 0,
                                                     'social': 0},
                                        'quies': {'data': 0,
                                                  'measures': 0,
                                                  'media': 0,
                                                  'social': 0},
                                        'sandbox': {'data': 0,
                                                    'measures': 0,
                                                    'media': 0,
                                                    'social': 0},
                                        'seesatw': {'data': 0,
                                                    'measures': 0,
                                                    'media': 0,
                                                    'social': 0},
                                        'smartlab': {'data': 0,
                                                     'measures': 1,
                                                     'media': 0,
                                                     'social': 0},
                                        'smartowear': {'data': 0,
                                                       'measures': 0,
                                                       'media': 0,
                                                       'social': 0}},
                  'total_data': {'data': 0,
                                 'measures': 52070,
                                 'media': 0,
                                 'social': 0},
                  'total_smart_objects': 43,
                  'total_streams': 42,
                  'total_tenants': 17},
     '30days': {'origin': datetime.datetime(2015, 1, 2, 0, 0),
                'total_data': {'data': 0,
                               'measures': 52069,
                               'media': 0,
                               'social': 0}},
     'midnight': {'origin': datetime.datetime(2015, 2, 1, 0, 0),
                  'total_data': {'data': 0,
                                 'measures': 74,
                                 'media': 0,
                                 'social': 0}},
     'monthly': {'origin': datetime.datetime(2015, 2, 1, 0, 0),
                 'total_data': {'data': 0,
                                'measures': 74,
                                'media': 0,
                                'social': 0}}}

Time Details
------------

Time details are reported into the ``date`` and ``datetime`` fields::

     'date': {'day': 1, 'month': 2, 'week': 5, 'weekday': 7, 'year': 2015},
     'datetime': datetime.datetime(2015, 2, 2, 16, 3, 17, 3961)

``datetime`` fields reports the moment the script started, while the ``date``
field reports information related to the ``day`` script argument. They might be
useful to perform combined statistics, for example it might be possible to
check if data is inserted more often during wednesday by relying on the ``weekday``
field.

Daily Details
-------------

Daily details are reported into the ``daily`` field::

    {'midnight': {'origin': datetime.datetime(2015, 2, 1, 0, 0),
                  'total_data': {'data': 0, 'measures': 74, 'media': 0, 'social': 0}},

Those include the amount of data inserted since the ``origin`` field up to the
moment the script started. The origin field will always coincide with script ``day``
argument. This in facts report the data inserted between the ``date`` and ``datetime``
fields reported in *Time Details*. If script is started daily it will coincide with
daily inserted data.

Monthly Details
---------------

Details related to amount of data gathered since the begin of the month are
available into the ``monthly`` field::

     'monthly': {'origin': datetime.datetime(2015, 2, 1, 0, 0),
                 'total_data': {'data': 0,
                                'measures': 74,
                                'media': 0,
                                'social': 0}}

30Days Details
--------------

Details related to amount of data gathered during the last 30 days is available
inside the ``30days`` field::

     '30days': {'origin': datetime.datetime(2015, 1, 2, 0, 0),
                'total_data': {'data': 0,
                               'measures': 52069,
                               'media': 0,
                               'social': 0}}

LifeTime Details
----------------

Statistics related to data gathered during the whole platform life time are
available into the ``lifetime`` field::

    'lifetime': {'stream_frequency': {'domain': [{'_id': 'SCHOOL',
                                                       'count': 25},
                                                      {'_id': 'ENVIRONMENT',
                                                       'count': 14},
                                                      {'_id': 'TRANSPORT',
                                                       'count': 3}],
                                           'tags': [{'_id': 'QUALITY', 'count': 38},
                                                    {'_id': 'INDOOR', 'count': 35},
                                                    {'_id': 'AIR', 'count': 30},
                                                    {'_id': 'POLLUTION',
                                                     'count': 5},
                                                    {'_id': 'TRAFFIC', 'count': 3},
                                                    {'_id': 'OUTDOOR', 'count': 3},
                                                    {'_id': 'RAIN', 'count': 1}],
                                           'tenants': [{'_id': 'csp', 'count': 41},
                                                       {'_id': 'smartlab',
                                                        'count': 1}]},
                      'tenant_total_data': {'all4all': {'data': 0,
                                                        'measures': 0,
                                                        'media': 0,
                                                        'social': 0},
                                            'comfortsense': {'data': 0,
                                                             'measures': 0,
                                                             'media': 0,
                                                             'social': 0},
                                            'csp': {'data': 0,
                                                    'measures': 52069,
                                                    'media': 0,
                                                    'social': 0},
                                            'eden': {'data': 0,
                                                     'measures': 0,
                                                     'media': 0,
                                                     'social': 0},
                                            'elise': {'data': 0,
                                                      'measures': 0,
                                                      'media': 0,
                                                      'social': 0},
                                            'esgp': {'data': 0,
                                                     'measures': 0,
                                                     'media': 0,
                                                     'social': 0},
                                            'healthcommons': {'data': 0,
                                                              'measures': 0,
                                                              'media': 0,
                                                              'social': 0},
                                            'idem': {'data': 0,
                                                     'measures': 0,
                                                     'media': 0,
                                                     'social': 0},
                                            'iotibevo': {'data': 0,
                                                         'measures': 0,
                                                         'media': 0,
                                                         'social': 0},
                                            'leo': {'data': 0,
                                                    'measures': 0,
                                                    'media': 0,
                                                    'social': 0},
                                            'ondeuwc': {'data': 0,
                                                        'measures': 0,
                                                        'media': 0,
                                                        'social': 0},
                                            'pitagora': {'data': 0,
                                                         'measures': 0,
                                                         'media': 0,
                                                         'social': 0},
                                            'quies': {'data': 0,
                                                      'measures': 0,
                                                      'media': 0,
                                                      'social': 0},
                                            'sandbox': {'data': 0,
                                                        'measures': 0,
                                                        'media': 0,
                                                        'social': 0},
                                            'seesatw': {'data': 0,
                                                        'measures': 0,
                                                        'media': 0,
                                                        'social': 0},
                                            'smartlab': {'data': 0,
                                                         'measures': 1,
                                                         'media': 0,
                                                         'social': 0},
                                            'smartowear': {'data': 0,
                                                           'measures': 0,
                                                           'media': 0,
                                                           'social': 0}},
                      'total_data': {'data': 0,
                                     'measures': 52070,
                                     'media': 0,
                                     'social': 0},
                      'total_smart_objects': 43,
                      'total_streams': 42,
                      'total_tenants': 17}

Those are divided in multiple sets of data each related to some type of information:

    - ``stream_frequency`` reports the amount of streams for each *tag*, *domain* and *tenant*.
    - ``tenant_total_data`` reports the amount of data gathered for each tenant.
    - ``total_data`` reports the total data gathered since the platform exists.
    - ``total_smart_objects`` reports the amount of sensors registered into the platform.
    - ``total_streams`` reports the amount of streams registered into the platform.
    - ``total_tenants`` reports the amount of tenants registered into the platform.

Running Test Suite
==================

yuccastats comes with a full test suite that checks the statistics
respects some requirements and properly handles errors.

To run the test suite just move yourself inside the source code
and run::

    $ pip install -e .[testing]
    $ python setup.py nosetests

It should correctly install test suite dependencies and run it::

    tests.test_stats.TestStatisticsSteps.test_date ... ok
    tests.test_stats.TestStatisticsSteps.test_metadata_stats ... ok
    tests.test_stats.TestStatisticsSteps.test_midnight ... ok
    tests.test_stats.TestStatisticsSteps.test_monthly ... ok
    tests.test_stats.TestStatisticsSteps.test_stream_frequency_without_streams ... ok
    tests.test_stats.TestStatisticsSteps.test_tenant_totals ... ok
    tests.test_stats.TestStatisticsSteps.test_thirtydays ... ok
    tests.test_stats.TestStatisticsStepsWithStreams.test_metadata_stats_with_streams ... ok
    tests.test_stats.TestStatisticsStepsWithStreams.test_stream_frequencies ... ok
    tests.test_utils.TestCollectionLookupUtilities.test_connected_collection ... ok
    tests.test_utils.TestCollectionLookupUtilities.test_tenant_collections ... ok
    tests.test_utils.TestDateTimeUtils.test_begin_of_day ... ok
    tests.test_utils.TestDateTimeUtils.test_first_month_day ... ok
    tests.test_utils.TestDateTimeUtils.test_isodate ... ok
    tests.test_utils.TestDateTimeUtils.test_thirtydays_ago ... ok
    tests.test_utils.TestDateTimeUtils.test_yestertday ... ok
    tests.test_utils.TestMongoDBConnection.test_missing_db_configuration_is_detected ... ok
    tests.test_utils.TestMongoDBConnection.test_missing_stats_col_configuration_is_detected ... ok

    Name                     Stmts   Miss  Cover   Missing
    ------------------------------------------------------
    yuccastats                   0      0   100%
    yuccastats.mongodb          33      0   100%
    yuccastats.stats_steps      90      0   100%
    yuccastats.utils            12      0   100%
    ------------------------------------------------------
    TOTAL                      135      0   100%
    ----------------------------------------------------------------------
    Ran 18 tests in 0.307s

    OK
