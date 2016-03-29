================================
About mongodb_benchmarking_tools
================================

mongodb_benchmarking_tools is a scripts suite used to make benchmarking analysis on mongodb
cluster.

==========
Installing
==========

mongodb_benchmarking_tools supports Python **2.7** and **3.4**.
The ``mongodb_benchmarking_tools`` can be installed running::

    $ python setup.py install

from inside the mongodb_benchmarking_tools directory.
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

==============
Suite Elements
==============

mongodb_benchmarking_tools is divided in different script used to gather benchmark data, such
profiling data

gather_profiler_data
====================

this script is intended for gather profiler data from each db on each cluster's node, it discover
cluster's nodes and cluster's dbs, then query the ``system.profile`` and merge results

To run gather_profiler_data just start it after install::

    $ gather_profiler_data

The output will be entire ``system.profile`` collection

Command Line Options
--------------------

gather_profiler_data script provides some options to change where profiling data will be taken,
or to filter output by ``start`` or ``end`` date::

    usage: gather_profiler_data [-h] [-u USER] [-p PASSWORD] [-c CONFIGSVR_URL]
                                [-s START] [-e END]

    Gathers profiler data from each cluster's replica set member.

    optional arguments:
      -h, --help            show this help message and exit
      -u USER, --user USER  username required for admin database authentication
      -p PASSWORD, --password PASSWORD
                            password required for admin database authentication
      -c CONFIGSVR_URL, --configsvr-url CONFIGSVR_URL
                            cluster config server mongodb URL to connect to
                            (mongodb://server:port)
      -s START, --start START
                            start date to filter data gathered in ISOFORMAT YYYY-MM-DD
      -e END, --end END     end date to filter data gathered in ISOFORMAT YYYY-MM-DD

