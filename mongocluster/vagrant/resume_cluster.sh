vagrant resume shard1
vagrant provision shard1 --provision-with shell
vagrant resume shard1rep1
vagrant provision shard1rep1 --provision-with shell
vagrant resume shard1rep2
vagrant provision shard1rep2 --provision-with shell
vagrant resume shard2
vagrant provision shard2 --provision-with shell
vagrant resume shard2rep1
vagrant provision shard2rep1 --provision-with shell
vagrant resume shard2rep2
vagrant provision shard2rep2 --provision-with shell
vagrant resume conf1
vagrant provision conf1 --provision-with shell
vagrant resume conf2
vagrant provision conf2 --provision-with shell
vagrant resume conf3
vagrant provision conf3 --provision-with shell

vagrant ssh conf1 -c /home/vagrant/run_mongos1.sh
vagrant ssh conf2 -c /home/vagrant/run_mongos2.sh
vagrant ssh conf3 -c /home/vagrant/run_mongos3.sh
