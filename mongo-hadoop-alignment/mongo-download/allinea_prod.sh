#!/bin/bash

eexit() {
    local error_str="$@"
    echo $error_str
    exit 1
}


cd /home/sdp/allinea_yucca
echo "`date +%H.%M.%S` ---- `date +'%a, %d %h %Y'` ----   Start procedure allinea_prod "
echo ""
echo "Verifica istanza unica"
exec 200>/home/sdp/.allinea_prod.lock
flock -n 200 || eexit "Procedura allinea_prod gia in esecuzione"
#
echo "acquisisce keytab"
kinit -kt /etc/security/keytabs/sdp.service.keytab -p sdp/sdnet-master3.sdp.csi.it@SDP.CSI.IT
#
data_corr=`date +%Y%m%d_%H%M`
echo "`date +%H.%M.%S` ---- `date +'%a, %d %h %Y'` ----   lancio ./scarico_tenant_convergenza.sh prod "
./scarico_tenant_convergenza.sh prod >>/home/sdp/allinea_yucca/log/allinea_prod_${data_corr}.log 2>>/home/sdp/allinea_yucca/log/allinea_prod_${data_corr}.log
if [ $? -eq 0 ]
then
  echo "`date +%H.%M.%S` ---- `date +'%a, %d %h %Y'` ----   lancio ./upload_convergenza.sh prod "
  ./upload_convergenza.sh prod >>/home/sdp/allinea_yucca/log/allinea_prod_${data_corr}.log 2>>/home/sdp/allinea_yucca/log/allinea_prod_${data_corr}.log
  if [ $? -ne 0 ]
  then
    echo " !!!!!    ERRORE    !!!!!  "
  fi
else
  echo " !!!!!    ERRORE    !!!!!  "
fi
echo ""
echo "Elimina file di lock"
rm /home/sdp/.allinea_prod.lock
#
echo "`date +%H.%M.%S` ---- `date +'%a, %d %h %Y'` ----   End procedure allinea_prod "
