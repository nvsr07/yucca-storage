cd /home/oozie/scripts
./scarico_tenant.sh preprod
if [ $? -eq 0 ]
then
./upload.sh preprod
fi

