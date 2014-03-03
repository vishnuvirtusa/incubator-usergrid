#!/bin/bash

# Install and stop Cassandra
cat >> /etc/apt/sources.list.d/cassandra.sources.list << EOF
deb http://www.apache.org/dist/cassandra/debian 12x main
EOF
apt-get update
apt-get -y install libcap2
apt-get --force-yes -y install cassandra
/etc/init.d/cassandra stop

# Configure Cassandra 
mkdir -p /mnt/data/cassandra
chown cassandra /mnt/data/cassandra
groovy config_cassandra.groovy > /etc/cassandra/cassandra.yaml

# Go!
/etc/init.d/cassandra start

