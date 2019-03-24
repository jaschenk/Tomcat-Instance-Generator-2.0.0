#!/bin/sh
# -----------------------------------------------------------
#  Tomcat Instance Environment Settings
# -----------------------------------------------------------

#
#  *************************************************************************
#   Tomcat Instance Generation
#  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
#
#   Instance UUID: ${TOMCAT_INSTANCE_UUID}
#   Instance Name: ${TOMCAT_INSTANCE_NAME}
#     Environment: ${TOMCAT_ENVIRONMENT_NAME}
#
#  *************************************************************************
#

#
# set JRE (Java Runtime Environment)
JRE_HOME=${CATALINA_HOME}/jre
CATALINA_PID=${CATALINA_BASE}/pid/tc.pid
HOSTNAME_SHORT=$(echo -n $HOSTNAME | cut -d'.' -f 1)
ENV_NAME_LC=$(echo -n $HOSTNAME | cut -d'-' -f 2 | awk '{print tolower($0)}')
ENV_NAME_UC=$(echo -n $HOSTNAME | cut -d'-' -f 2 | awk '{print toupper($0)}')
export JRE_HOME CATALINA_PID HOSTNAME_SHORT ENV_NAME_LC ENV_NAME_UC

#
# Set Logging
CATALINA_OUT=${CATALINA_BASE}/logs/${TOMCAT_INSTANCE_NAME}_catalina.out
export CATALINA_OUT

#
# Set JAVA JVM Options
${JVM_OPTS}

#
# Set Global Instance Properties
CATALINA_OPTS="${CATALINA_OPTS} -Dinfra.tomcat.env=\"BASE ${TOMCAT_INSTANCE_NAME}\""
CATALINA_OPTS="${CATALINA_OPTS} -Dinfra.tomcat.instance.name=${TOMCAT_INSTANCE_NAME}"
${INSTANCE_PROPERTIES}

#
# Set Runtime Management Properties
#if [ ${ENV_NAME_UC} = "PRD" ]; then
#        CATALINA_OPTS="${CATALINA_OPTS} -Dcom.managecat.controller.url=http://spitfire:8085/controller/"
#        CATALINA_OPTS="${CATALINA_OPTS} -Dcom.managecat.collector.url=http://spitfire:8085/collector/"
#        CATALINA_OPTS="${CATALINA_OPTS} -Dcom.managecat.console.agent.accountkey=5fc4aef328866ade38ec33c7879b96e4"
#
#else
#        CATALINA_OPTS="${CATALINA_OPTS} -Dcom.managecat.controller.url=http://rh-tst-01:8080/controller/"
#        CATALINA_OPTS="${CATALINA_OPTS} -Dcom.managecat.collector.url=http://rh-tst-01:8080/collector/"
#        CATALINA_OPTS="${CATALINA_OPTS} -Dcom.managecat.console.agent.accountkey=00000000000000000000000000000000"
#fi
#CATALINA_OPTS="${CATALINA_OPTS} -Dcom.managecat.console.agent.groupname=${ENV_NAME_UC}"
#CATALINA_OPTS="${CATALINA_OPTS} -Dcom.managecat.console.agent.servername=${HOSTNAME_SHORT}_${TOMCAT_INSTANCE_NAME}"
#CATALINA_OPTS="${CATALINA_OPTS} -Dcom.managecat.console.agent.hostname=${HOSTNAME_SHORT}"
#CATALINA_OPTS="${CATALINA_OPTS} -Dcom.managecat.console.agent.port=8081"
#CATALINA_OPTS="${CATALINA_OPTS} -Dcom.managecat.console.agent.contextname=console"
#CATALINA_OPTS="${CATALINA_OPTS} -Dcom.managecat.console.agent.secure=false"
#CATALINA_OPTS="${CATALINA_OPTS} -Djava.library.path=/opt/tomcat-home/mcatlib/sigar-lib/lib"
${INSTANCE_MANAGEMENT_PROPERTIES}

#
#
# Set Garbage Collection Properties
#CATALINA_OPTS="${CATALINA_OPTS} -XX:SurvivorRatio=8"
#CATALINA_OPTS="${CATALINA_OPTS} -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled"
#CATALINA_OPTS="${CATALINA_OPTS} -XX:+UseCMSInitiatingOccupancyOnly"
#CATALINA_OPTS="${CATALINA_OPTS} -XX:CMSInitiatingOccupancyFraction=70"
#CATALINA_OPTS="${CATALINA_OPTS} -XX:+ScavengeBeforeFullGC -XX:+CMSScavengeBeforeRemark"
#CATALINA_OPTS="${CATALINA_OPTS} -XX:+PrintGCDateStamps -verbose:gc -XX:+PrintGCDetails"
#CATALINA_OPTS="${CATALINA_OPTS} -Xloggc:\"${CATALINA_BASE}/logs/${TOMCAT_INSTANCE_NAME}_gclog\""
#CATALINA_OPTS="${CATALINA_OPTS} -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=100M"
#CATALINA_OPTS="${CATALINA_OPTS} -XX:+HeapDumpOnOutOfMemoryError"
#CATALINA_OPTS="${CATALINA_OPTS} -XX:+PrintConcurrentLocks"
#CATALINA_OPTS="${CATALINA_OPTS} -XX:HeapDumpPath=${CATALINA_BASE}/logs/${TOMCAT_INSTANCE_NAME}_dump.hprof"

# Specific Properties for ${TOMCAT_INSTANCE_NAME} JVM

#
# New Relic
#CATALINA_OPTS="${CATALINA_OPTS} -Dnewrelic.environment=${ENV_NAME_LC}"
#CATALINA_OPTS="${CATALINA_OPTS} -Dnewrelic.config.file=/opt/newrelic/newrelic-${TOMCAT_INSTANCE_NAME}.yml"
#CATALINA_OPTS="${CATALINA_OPTS} -javaagent:/opt/newrelic/newrelic.jar"

#
# Export Property
export CATALINA_OPTS

#
#
# Set Load Library Path
LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$CATALINA_HOME/lib
export LD_LIBRARY_PATH

#
#
# ########################################################################################
# Additional Settings for Garbage Collection Processing ...
#
#    -XX:SurvivorRatio=<ratio>
#    -XX:+UseConcMarkSweepGC -XX:+CMSParallel#arkEnabled
#    -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=<percent>
#    -XX:+ScavengeBeforeFullGC -XX:+CMSScavengeBefore#ark
#    -XX:+PrintGCDateStamps -verbose:gc -XX:+PrintGCDetails -Xloggc:"<path to log>"
#    -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=100M
#    -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=<path to dump>`date`.hprof
#
# #######################################################################################