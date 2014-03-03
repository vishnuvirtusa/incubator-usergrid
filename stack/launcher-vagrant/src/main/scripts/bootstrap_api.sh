#!/bin/bash
if [ "$#" -ne 3 ]; then
    echo "Must specify IP address, a list of DB server IPs and the Cassandra replication factor"
fi
echo $1
export PUBLIC_HOSTNAME=$1
export DB_SERVERS=$2
export REPL_FACTOR=$2

apt-get update
apt-get -y install openjdk-7-jdk
apt-get -y install groovy
apt-get -y install vim 
apt-get -y install curl 

pushd /vagrant
./install_usergrid.sh

