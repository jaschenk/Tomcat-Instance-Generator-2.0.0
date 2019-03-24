@echo off
set APACHE_MIRROR_URL="http://apache.cs.utah.edu/tomcat"
set JAR="target\TomcatInstanceGenerator-2.0.0-SNAPSHOT.jar"
java -Dapache.mirror.head.url=%APACHE_MIRROR_URL% -jar %JAR% --pull
