#!/bin/bash

cd /etc/apt/sources.list.d
cat >> elasticsearch.sources.list << EOF
deb http://packages.elasticsearch.org/elasticsearch/1.0/debian stable main
EOF
apt-get update
apt-get --force-yes -y install elasticsearch

/etc/init.d/elasticsearch stop

update-rc.d elasticsearch defaults 95 10
groovy /vagrant/config_elasticsearch.groovy > /etc/elasticsearch/elasticsearch.yml

/etc/init.d/elasticsearch start
