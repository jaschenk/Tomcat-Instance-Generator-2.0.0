#!/bin/bash
# **********************************************
# Manage Tomcat Instance One JVM
# **********************************************
#
CATALINA_BASE=../../tomcat-base-01
#
CATALINA_HOME=../../tomcat-home
#
JRE_HOME=${CATALINA_HOME}/jre
export CATALINA_HOME CATALINA_BASE JRE_HOME
#
${CATALINA_HOME}/bin/catalina.sh ${1}
