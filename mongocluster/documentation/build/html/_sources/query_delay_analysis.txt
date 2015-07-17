=========================================
Analisi tempi prolungati esecuzione query
=========================================

Introduzione
============

Analisi sulla lentezza in lettura utilizzando un filtro indicizzato nella collection ``measures``
appartenente al ``DB_smartlab``, precedentemente shardato all'interno di un cluster virtuale 
avente la stessa configurazione del cluster di INT, utilizzando come ``shard key`` 
``{idDataset: 1, datasetVersion: 1, time: 1}``::

    db.measures.find({$and: [{idDataset:161},{datasetVersion:1}]}).sort({time:-1}).limit(1)
    
Execution Stats della Query
===========================

Utilizzando il comando ``.explain("executionStats")`` si ottongono statistiche sull' esecuzione 
della query::

    db.measures.find({$and: [{idDataset:161},{datasetVersion:1}]}).sort({time:-1}).limit(1).explain("executionStats")
    {
        "clusteredType" : "ParallelSort",
        "shards" : {
            "speed1/10.102.67.11:10000,10.102.67.12:10000,10.102.67.13:10000" : [
                {
                    "cursor" : "BtreeCursor idDataset_1_datasetVersion_1_time_1 reverse",
                    "isMultiKey" : false,
                    "n" : 1,
                    "nscannedObjects" : 1,
                    "nscanned" : 1,
                    "nscannedObjectsAllPlans" : 1,
                    "nscannedAllPlans" : 1,
                    "scanAndOrder" : false,
                    "indexOnly" : false,
                    "nYields" : 0,
                    "nChunkSkips" : 0,
                    "millis" : 31,
                    "indexBounds" : {
                        "idDataset" : [
                            [
                                161,
                                161
                            ]
                        ],
                        "datasetVersion" : [
                            [
                                1,
                                1
                            ]
                        ],
                        "time" : [
                            [
                                {
                                    "$maxElement" : 1
                                },
                                {
                                    "$minElement" : 1
                                }
                            ]
                        ]
                    },
                    "allPlans" : [
                        {
                            "cursor" : "BtreeCursor idDataset_1_datasetVersion_1_time_1 reverse",
                            "isMultiKey" : false,
                            "n" : 1,
                            "nscannedObjects" : 1,
                            "nscanned" : 1,
                            "scanAndOrder" : false,
                            "indexOnly" : false,
                            "nChunkSkips" : 0,
                            "indexBounds" : {
                                "idDataset" : [
                                    [
                                        161,
                                        161
                                    ]
                                ],
                                "datasetVersion" : [
                                    [
                                        1,
                                        1
                                    ]
                                ],
                                "time" : [
                                    [
                                        {
                                            "$maxElement" : 1
                                        },
                                        {
                                            "$minElement" : 1
                                        }
                                    ]
                                ]
                            }
                        }
                    ],
                    "server" : "localhost.localdomain:10000",
                    "filterSet" : false,
                    "stats" : {
                        "type" : "LIMIT",
                        "works" : 1,
                        "yields" : 0,
                        "unyields" : 0,
                        "invalidates" : 0,
                        "advanced" : 1,
                        "needTime" : 0,
                        "needFetch" : 0,
                        "isEOF" : 1,
                        "children" : [
                            {
                                "type" : "SHARDING_FILTER",
                                "works" : 1,
                                "yields" : 0,
                                "unyields" : 0,
                                "invalidates" : 0,
                                "advanced" : 1,
                                "needTime" : 0,
                                "needFetch" : 0,
                                "isEOF" : 0,
                                "chunkSkips" : 0,
                                "children" : [
                                    {
                                        "type" : "FETCH",
                                        "works" : 1,
                                        "yields" : 0,
                                        "unyields" : 0,
                                        "invalidates" : 0,
                                        "advanced" : 1,
                                        "needTime" : 0,
                                        "needFetch" : 0,
                                        "isEOF" : 0,
                                        "alreadyHasObj" : 0,
                                        "forcedFetches" : 0,
                                        "matchTested" : 0,
                                        "children" : [
                                            {
                                                "type" : "IXSCAN",
                                                "works" : 1,
                                                "yields" : 0,
                                                "unyields" : 0,
                                                "invalidates" : 0,
                                                "advanced" : 1,
                                                "needTime" : 0,
                                                "needFetch" : 0,
                                                "isEOF" : 0,
                                                "keyPattern" : "{ idDataset: 1, datasetVersion: 1, time: 1 }",
                                                "isMultiKey" : 0,
                                                "boundsVerbose" : "field #0['idDataset']: [161.0, 161.0], field #1['datasetVersion']: [1.0, 1.0], field #2['time']: [MaxKey, MinKey]",
                                                "yieldMovedCursor" : 0,
                                                "dupsTested" : 0,
                                                "dupsDropped" : 0,
                                                "seenInvalidated" : 0,
                                                "matchTested" : 0,
                                                "keysExamined" : 1,
                                                "children" : [ ]
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    }
                }
            ]
        },
        "cursor" : "BtreeCursor idDataset_1_datasetVersion_1_time_1 reverse",
        "n" : 1,
        "nChunkSkips" : 0,
        "nYields" : 0,
        "nscanned" : 1,
        "nscannedAllPlans" : 1,
        "nscannedObjects" : 1,
        "nscannedObjectsAllPlans" : 1,
        "millisShardTotal" : 31,
        "millisShardAvg" : 31,
        "numQueries" : 1,
        "numShards" : 1,
        "indexBounds" : {
            "idDataset" : [
                [
                    161,
                    161
                ]
            ],
            "datasetVersion" : [
                [
                    1,
                    1
                ]
            ],
            "time" : [
                [
                    {
                        "$maxElement" : 1
                    },
                    {
                        "$minElement" : 1
                    }
                ]
            ]
        },
        "millis" : 111
    }


la query risulta utilizzare in modo corretto gli indici, non ``indexOnly`` in quanto non viene 
fatta una proiezione richiedendo solo valori presenti nell'indice::
    
    "cursor" : "BtreeCursor idDataset_1_datasetVersion_1_time_1 reverse",
    "isMultiKey" : false,
    "n" : 1,
    "nscannedObjects" : 1,
    "nscanned" : 1,
    "nscannedObjectsAllPlans" : 1,
    "nscannedAllPlans" : 1,
    "scanAndOrder" : false,
    "indexOnly" : false,
    
    
si conclude che il problema non è riguardante gli indici o l'esecuzione della query


Statistiche Collection
======================

Utilizzando il il comando ``.stats()`` si ottengono statistiche sulla collection in uso::

    db.measures.stats()
    {
        "sharded" : true,
        "systemFlags" : 0,
        "userFlags" : 1,
        "ns" : "DB_smartlab.measures",
        "count" : 11063013,
        "numExtents" : 47,
        "size" : 5273210544,
        "storageSize" : 7458181120,
        "totalIndexSize" : 1010954224,
        "indexSizes" : {
            "_id_" : 517164704,
            "idDataset_1_datasetVersion_1_time_1" : 493764992,
            "idxLocation_2dsphere_idDataset_1_datasetVersion_1" : 24528
        },
        "avgObjSize" : 476.6522957172698,
        "nindexes" : 3,
        "nchunks" : 155,
        "shards" : {
            "speed0" : {
                "ns" : "DB_smartlab.measures",
                "count" : 413541,
                "size" : 128132272,
                "avgObjSize" : 309,
                "storageSize" : 174735360,
                "numExtents" : 12,
                "nindexes" : 3,
                "lastExtentSize" : 50798592,
                "paddingFactor" : 1,
                "systemFlags" : 0,
                "userFlags" : 1,
                "totalIndexSize" : 49333984,
                "indexSizes" : {
                    "_id_" : 22042496,
                    "idxLocation_2dsphere_idDataset_1_datasetVersion_1" : 8176,
                    "idDataset_1_datasetVersion_1_time_1" : 27283312
                },
                "ok" : 1
            },
            "speed1" : {
                "ns" : "DB_smartlab.measures",
                "count" : 10219997,
                "size" : 5042004272,
                "avgObjSize" : 493,
                "storageSize" : 7159508992,
                "numExtents" : 24,
                "nindexes" : 3,
                "lastExtentSize" : 1861685248,
                "paddingFactor" : 1,
                "systemFlags" : 1,
                "userFlags" : 1,
                "totalIndexSize" : 920781120,
                "indexSizes" : {
                    "_id_" : 476284704,
                    "idxLocation_2dsphere_idDataset_1_datasetVersion_1" : 8176,
                    "idDataset_1_datasetVersion_1_time_1" : 444488240
                },
                "ok" : 1
            },
            "speed2" : {
                "ns" : "DB_smartlab.measures",
                "count" : 429475,
                "size" : 103074000,
                "avgObjSize" : 240,
                "storageSize" : 123936768,
                "numExtents" : 11,
                "nindexes" : 3,
                "lastExtentSize" : 37625856,
                "paddingFactor" : 1,
                "systemFlags" : 0,
                "userFlags" : 1,
                "totalIndexSize" : 40839120,
                "indexSizes" : {
                    "_id_" : 18837504,
                    "idxLocation_2dsphere_idDataset_1_datasetVersion_1" : 8176,
                    "idDataset_1_datasetVersion_1_time_1" : 21993440
                },
                "ok" : 1
            }
        },
        "ok" : 1
    }
    
Analizzando le statistiche della collezione si evince che la mole di dati è molto elevata e il 
problema di rallentamento potrebbe essere dovuto al ``page fault``, problema che è stato eliminato
con il passaggio allo Storage Engine System wiredTiger

http://docs.mongodb.org/manual/core/storage/#storage-wiredtiger

http://docs.mongodb.org/manual/faq/storage/


Conclusioni
===========

Visto il recente passaggio a mongoDB 3.0 e viste le recenti migliorie apportate dove il sistema
poteva presentare dei problemi si rimanda ad un test successivo nel caso in cui la query 
analizzata continuasse a risultare lenta, con una verifica giornaliera delle statistiche 
contestualmente ad una analisi utilizzando il tool ``mongostat``
http://docs.mongodb.org/manual/reference/program/mongostat/