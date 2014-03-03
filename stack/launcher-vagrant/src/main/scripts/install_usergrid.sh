#!/bin/bash

# Install and stop Tomcat
apt-get -y install tomcat7 
apt-get -y install unzip 
/etc/init.d/tomcat7 stop

# Configure Tomcat
cat >> /usr/share/tomcat7/bin/setenv.sh << EOF
export JAVA_OPTS=-Xmx400m
EOF
chmod +x setenv.sh

# Deploy and configure Usergrid stack WAR
rm -rf /var/lib/tomcat7/webapps/*
cp -r ROOT.war /var/lib/tomcat7/webapps

mkdir -p /usr/share/tomcat7/lib 
groovy config_usergrid.groovy > /usr/share/tomcat7/lib/usergrid-custom.properties 

# Deploy Usergrid Portal
mkdir /var/lib/tomcat7/webapps/portal
unzip appsvc-ui.2.0.34.zip
cp -r appsvc-ui.2.0.34/* /var/lib/tomcat7/webapps/portal

# Go!
/etc/init.d/tomcat7 start
