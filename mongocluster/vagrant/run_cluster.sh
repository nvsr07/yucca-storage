#!/bin/bash


PS3='Choose your network interface on wich vagrant will bridge: '
ifaces=($(ifconfig -s | awk '{print $1}' | tail -n +2))

select iface in "${ifaces[@]}";
do
   selected_iface=$iface
   break
done

echo "You choose $selected_iface"
sed -i "s/bridge: \"eth0\"/bridge: \"$selected_iface\"/g" Vagrantfile

vagrant up 
vagrant provision --provision-with shell
vagrant ssh conf1 -c /home/vagrant/run_mongos1.sh
vagrant ssh conf2 -c /home/vagrant/run_mongos2.sh
vagrant ssh conf3 -c /home/vagrant/run_mongos3.sh
