#!/bin/bash
if [ "$#" -ne 1 ]; then
    echo "Must specify IP address"
fi
echo $1
export PUBLIC_HOSTNAME=$1

apt-get update
apt-get -y install openjdk-7-jdk
apt-get -y install groovy
apt-get -y install vim 
apt-get -y install curl 

pushd /vagrant
./install_cassandra.sh
./install_usergrid.sh

