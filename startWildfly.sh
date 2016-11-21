#!/bin/bash
 
JAVA_OPTS="-server -XX:+UseCompressedOops -Xms1024m -Xmx2048m -Djava.net.preferIPv4Stack=true -Djboss.modules.system.pkgs=org.jboss.byteman -Djava.awt.headless=true"
export JAVA_OPTS
 
/opt/wildfly/bin/standalone.sh -c standalone-full.xml -b 0.0.0.0 --debug
