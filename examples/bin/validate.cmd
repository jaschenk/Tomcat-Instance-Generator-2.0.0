@echo off
set APACHE_MIRROR_URL="http://apache.cs.utah.edu/tomcat"
set JAR="..\lib\TomcatInstanceGenerator-2.0.0.jar"
set TOMCAT_YAML_CONFIG="..\yaml\Generate-STG9.yaml"
java -Dapache.mirror.head.url=%APACHE_MIRROR_URL% -jar %JAR% --validate %TOMCAT_YAML_CONFIG%
