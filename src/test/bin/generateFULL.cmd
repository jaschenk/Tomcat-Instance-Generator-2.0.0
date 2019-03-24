@echo off
set APACHE_MIRROR_URL="http://apache.cs.utah.edu/tomcat"
set JAR="target\TomcatInstanceGenerator-2.0.0-SNAPSHOT.jar"
set TOMCAT_YAML_CONFIG="C:\Users\schenkje\MyTomcatWorkSpace\TomcatInstanceGenerator\src\test\resources\yaml\Test-TomcatInstanceFullGeneration.yaml"
java -Dapache.mirror.head.url=%APACHE_MIRROR_URL% -jar %JAR% --generate %TOMCAT_YAML_CONFIG%
