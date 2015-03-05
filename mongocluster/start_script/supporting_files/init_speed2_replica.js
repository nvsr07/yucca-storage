db.runCommand({ "replSetInitiate" : {"_id": "speed2",
                                     "members":[
                                         {
                                             "_id":1,
                                             "host": "10.102.67.21:10000"
                                         },
                                         {
                                             "_id":2,
                                             "host": "10.102.67.22:10000"
                                         },
                                         {
                                             "_id":3,
                                             "host": "10.102.67.23:10000"
                                         }]}})
