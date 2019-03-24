@echo off
REM # **********************************************
REM # Manage Up Tomcat Instance One JVM
REM # **********************************************
REM #
set CATALINA_BASE=..\..\tomcat-base-01
set CATALINA_HOME=..\..\tomcat-home

%CATALINA_HOME%\bin\catalina.bat %*