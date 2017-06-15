#!/bin/bash

if [ "$#" -ne "3" ] || [ "$2" != "prod" -a  "$2" != "preprod" ]; then 

   echo "usage : creaTenantMongo nometenant prod|preprod rootPwd"

else

   tenantName=$1
   environment=$2
   rootPwd=$3

   if [ "$environment" == "prod" ]; then 

      export EXP_DIR=/home/sdp/temp/prod 
      export MONGO_HOST=sdnet-speed1.sdp.csi.it
      export MONGO_PORT=27017
      export MONGO_USER=root 
      export MONGO_PWD=$rootPwd

   else
  
      export EXP_DIR=/home/sdp/temp/preprod 
      export MONGO_HOST=int-sdnet-speed1.sdp.csi.it 
      export MONGO_PORT=27017 
      export MONGO_USER=root
      export MONGO_PWD=$rootPwd

   fi


      commandString="mongo "$MONGO_HOST:$MONGO_PORT"/admin -u "$MONGO_USER" -p "$MONGO_PWD" --authenticationDatabase admin --eval \"var tenant='"$tenantName"',rootPwd='"$rootPwd"' \" addTenantMongo.js >> logCreazioneTenantMongo.txt"

      eval $commandString

fi


