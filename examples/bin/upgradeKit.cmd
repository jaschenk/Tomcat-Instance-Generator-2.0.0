@echo off
set APACHE_MIRROR_URL="http://apache.cs.utah.edu/tomcat"
set JAR="..\lib\TomcatInstanceGenerator-2.0.0.jar"
java -Dapache.mirror.head.url=%APACHE_MIRROR_URL% -jar %JAR% --upgradeKit
