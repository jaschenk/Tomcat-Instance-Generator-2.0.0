@echo off
REM # -----------------------------------------------------------
REM #  Tomcat Instance Environment Settings
REM # -----------------------------------------------------------

REM
REM  *************************************************************************
REM   Tomcat Instance Generation
REM  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
REM
REM   Instance UUID: ${TOMCAT_INSTANCE_UUID}
REM   Instance Name: ${TOMCAT_INSTANCE_NAME}
REM     Environment: ${TOMCAT_ENVIRONMENT_NAME}
REM
REM  *************************************************************************
REM

REM #
REM # set PID file
set CATALINA_PID=%CATALINA_BASE%\pid\tc.pid

REM #
REM # Set  JAVA JVM Options
${JVM_OPTS}

REM #
REM # Set Global Instance Properties
set CATALINA_OPTS=%CATALINA_OPTS% -Dcom.moda.tomcat.env="BASE ${TOMCAT_INSTANCE_NAME}"
set CATALINA_OPTS=%CATALINA_OPTS% -Dcom.moda.tomcat.instance.name=${TOMCAT_INSTANCE_NAME}
${INSTANCE_PROPERTIES}

REM #
REM # Set Runtime Management Properties
${INSTANCE_MANAGEMENT_PROPERTIES}

REM #
REM # Set to correct Hostname
set CATALINA_OPTS=%CATALINA_OPTS% -Djava.rmi.server.hostname=127.0.0.1

REM #
REM # Set Garbage Collection Properties
REM # set CATALINA_OPTS=%CATALINA_OPTS% -XX:SurvivorRatio=8
REM # set CATALINA_OPTS=%CATALINA_OPTS% -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled
REM # set CATALINA_OPTS=%CATALINA_OPTS% -XX:+UseCMSInitiatingOccupancyOnly
REM # set CATALINA_OPTS=%CATALINA_OPTS% -XX:CMSInitiatingOccupancyFraction=70
REM # set CATALINA_OPTS=%CATALINA_OPTS% -XX:+ScavengeBeforeFullGC -XX:+CMSScavengeBeforeRemark
REM # set CATALINA_OPTS=%CATALINA_OPTS% -XX:+PrintGCDateStamps -verbose:gc -XX:+PrintGCDetails
REM # set CATALINA_OPTS=%CATALINA_OPTS% -Xloggc:"%CATALINA_BASE%\logs\${TOMCAT_INSTANCE_NAME}_gclog"
REM # set CATALINA_OPTS=%CATALINA_OPTS% -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=100M
REM # set CATALINA_OPTS=%CATALINA_OPTS% -XX:+HeapDumpOnOutOfMemoryError
REM # set CATALINA_OPTS=%CATALINA_OPTS% -XX:HeapDumpPath=%CATALINA_BASE%\logs
REM #
REM # set CATALINA_OPTS=%CATALINA_OPTS% -XX:+PrintTenuringDistribution -XX:+PrintConcurrentLocks

REM #
REM # Additional Setting for this Environment place Here ...

REM #
REM # YourKit
REM # set CATALINA_OPTS=%CATALINA_OPTS% -agentpath:"C:\Program Files (x86)\YourKit Java Profiler 2014 build 14124\bin\win64\yjpagent.dll=delay=10000"

REM #
REM # SOAP Debugging
REM # set CATALINA_OPTS=%CATALINA_OPTS% -Dcom.sun.xml.ws.transport.http.client.HttpTransportPipe.dump=true
REM # set CATALINA_OPTS=%CATALINA_OPTS% -Dcom.sun.xml.ws.transport.http.client.HttpTransportPipe.dumpThreshold=128
REM # set CATALINA_OPTS=%CATALINA_OPTS% -Dcom.sun.xml.ws.transport.http.HttpAdapter.dump=true
REM # set CATALINA_OPTS=%CATALINA_OPTS% -Dcom.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump=true
REM # set CATALINA_OPTS=%CATALINA_OPTS% -Dcom.sun.xml.internal.ws.transport.http.HttpAdapter.dump=true
REM # set CATALINA_OPTS=%CATALINA_OPTS% -Dcom.sun.xml.internal.ws.transport.http.HttpAdapter.dumpTreshold=128

REM #
REM # New Relic
REM # set CATALINA_OPTS=%CATALINA_OPTS% -Dnewrelic.environment=schenkje
REM # set CATALINA_OPTS=%CATALINA_OPTS% -Dnewrelic.config.file=C:\opt\newrelic\newrelic-tc01.yml
REM # set CATALINA_OPTS=%CATALINA_OPTS% -javaagent:C:\opt\newrelic\newrelic.jar

REM #
REM # Kafka properties for LOG4J2 Appenders and SOA Integrations.
REM # set CATALINA_OPTS=%CATALINA_OPTS% -Daccess.log.kafka.enabled=true
REM # set CATALINA_OPTS=%CATALINA_OPTS% -Daccess.log.kafka.bootstrap.servers=webdevtest-01:9092
REM # set CATALINA_OPTS=%CATALINA_OPTS% -Daccess.log.kafka.topic=TOMCAT_LOGS_DEV
REM # set CATALINA_OPTS=%CATALINA_OPTS% -Daccess.log.kafka.syncSend=true

REM #
REM # Log Debugging
REM # set CATALINA_OPTS=%CATALINA_OPTS% -Dlog4j.debug -Dlog4j2.debug
REM # set CATALINA_OPTS=%CATALINA_OPTS% -Dlog4j2.debug

REM #
REM # New LOG4J2 Logging Implementation properties
REM # set CATALINA_OPTS=%CATALINA_OPTS% -Dlog4j2.contextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector

REM #
REM echo %CATALINA_OPTS%
REM pause
REM
REM ########################################################################################
REM Additional Settings for Garbage Collection Processing ...
REM
REM    -XX:SurvivorRatio=<ratio>
REM    -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled
REM    -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=<percent>
REM    -XX:+ScavengeBeforeFullGC -XX:+CMSScavengeBeforeRemark
REM    -XX:+PrintGCDateStamps -verbose:gc -XX:+PrintGCDetails -Xloggc:"<path to log>"
REM    -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=100M
REM    -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=<path to dump>`date`.hprof
REM
REM #######################################################################################