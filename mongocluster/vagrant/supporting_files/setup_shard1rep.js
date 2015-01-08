db.runCommand({ "replSetInitiate" : {"_id": "replshard1",
                                     "members":[
                                         {
                                             "_id":1,
                                             "host": "10.0.1.10:10000"
                                         },
                                         {
                                             "_id":2,
                                             "host": "10.0.1.11:11000"
                                         },
                                         {
                                             "_id":3,
                                             "host": "10.0.1.12:12000"
                                         }]}})