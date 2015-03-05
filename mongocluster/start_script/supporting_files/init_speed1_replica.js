db.runCommand({ "replSetInitiate" : {"_id": "speed1",
                                     "members":[
                                         {
                                             "_id":1,
                                             "host": "10.102.67.11:10000"
                                         },
                                         {
                                             "_id":2,
                                             "host": "10.102.67.12:10000"
                                         },
                                         {
                                             "_id":3,
                                             "host": "10.102.67.13:10000"
                                         }]}})
