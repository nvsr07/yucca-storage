db.runCommand({ "replSetInitiate" : {"_id": "replshard2",
                                     "members":[
                                         {
                                             "_id":1,
                                             "host": "10.0.1.20:20000"
                                         },
                                         {
                                             "_id":2,
                                             "host": "10.0.1.21:21000"
                                         },
                                         {
                                             "_id":3,
                                             "host": "10.0.1.22:22000"
                                         }]}})