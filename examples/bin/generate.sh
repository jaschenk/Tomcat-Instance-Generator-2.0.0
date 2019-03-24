#!/bin/bash
APACHE_MIRROR_URL="http://apache.cs.utah.edu/tomcat"
JAR="../lib/TomcatInstanceGenerator-2.0.0.jar"

TOMCAT_YAML_CONFIG="../yaml/Generate-MyContainers.yaml"
java -Dapache.mirror.head.url=${APACHE_MIRROR_URL} -jar ${JAR} --generate ${TOMCAT_YAML_CONFIG}
