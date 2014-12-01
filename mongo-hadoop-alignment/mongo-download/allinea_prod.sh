cd /home/oozie/scripts
./scarico_tenant.sh prod
if [ $? -eq 0 ]
then
./upload.sh prod
fi

