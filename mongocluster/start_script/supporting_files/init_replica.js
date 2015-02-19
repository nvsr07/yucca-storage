db.runCommand({ "replSetInitiate" : {"_id": "replshard1",
                                     "members":[
                                         {
                                             "_id":1,
                                             "host": "10.102.67.51:10000"
                                         },
                                         {
                                             "_id":2,
                                             "host": "10.102.67.52:10000"
                                         },
                                         {
                                             "_id":3,
                                             "host": "10.102.67.53:10000"
                                         }]}})
