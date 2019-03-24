@echo off
set JAR="target\TomcatInstanceGenerator-2.0.0-SNAPSHOT.jar"
java -Dcolor=false -jar %JAR% --help
