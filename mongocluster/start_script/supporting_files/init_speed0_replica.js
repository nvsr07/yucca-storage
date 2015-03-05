db.runCommand({ "replSetInitiate" : {"_id": "speed0",
                                     "members":[
                                         {
                                             "_id":1,
                                             "host": "10.102.67.1:10000"
                                         },
                                         {
                                             "_id":2,
                                             "host": "10.102.67.2:10000"
                                         },
                                         {
                                             "_id":3,
                                             "host": "10.102.67.3:10000"
                                         }]}})
