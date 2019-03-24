@echo off

rem @Copyright 2012, ManageCat Corporation
rem All rights reserved.
rem Use is subject to license terms.

rem This script is used for starting Tomcat restart agent.
rem After starting the agent, you are enable to restart Tomcat
rem from administration console.
rem Each parameters are explained below.

rem
rem  *************************************************************************
rem   Tomcat Instance Generation
rem  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
rem
rem   Instance UUID: ${TOMCAT_INSTANCE_UUID}
rem   Instance Name: ${TOMCAT_INSTANCE_NAME}
rem     Environment: ${TOMCAT_ENVIRONMENT_NAME}
rem
rem  *************************************************************************
rem


rem **************************************************************
rem Please Edit your JAVA_HOME and JRE_HOME
rem the Path must not contain any spaces to avoid path issues!
rem
rem set JAVA_HOME=C:\Java\jdk1.8.0_112
rem set JRE_HOME=C:\Java\jre1.8.0_112
rem 
rem **************************************************************

cls

set "CURRENT_DIR=%cd%"
if not "%CATALINA_HOME%" == "" goto gotHome
set "CATALINA_HOME=%CURRENT_DIR%"
if exist "%CATALINA_HOME%\bin\catalina.bat" goto okHome
cd ..
set "CATALINA_HOME=%cd%"
cd "%CURRENT_DIR%"

:gotHome

if exist "%CATALINA_HOME%\bin\catalina.bat" goto okHome
echo The CATALINA_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
goto end

:okHome

if not "%CATALINA_BASE%" == "" goto gotBase
set "CATALINA_BASE=%CATALINA_HOME%"

:gotBase


if not "%JAVA_HOME%" == "" goto gotJdkHome
if not "%JRE_HOME%" == "" goto gotJreHome
echo Neither the JAVA_HOME nor the JRE_HOME environment variable is defined
echo At least one of these environment variable is needed to run this program
goto exit

:gotJreHome
if not exist "%JRE_HOME%\bin\java.exe" goto noJavaHome
if not exist "%JRE_HOME%\bin\javaw.exe" goto noJavaHome
if not ""%1"" == ""debug"" goto okJavaHome
echo JAVA_HOME should point to a JDK in order to run in debug mode.
goto exit


:gotJdkHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
if not "%JRE_HOME%" == "" goto okJavaHome
set "JRE_HOME=%JAVA_HOME%"
goto okJavaHome

:noJavaHome
echo The JAVA_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
echo NB: JAVA_HOME should point to a JDK not a JRE
goto exit

:okJavaHome

rem Set standard command for invoking Java.
rem Note that NT requires a window name argument when using start.
rem Also note the quoting as JAVA_HOME may contain spaces.
set _RUNJAVA="%JRE_HOME%\bin\java"

rem Agent jar location. Default to tomcat lib directory.
set AGENT_JAR="%CATALINA_BASE%\lib\agent.jar"

rem Agent listens on this port. If your controller is outside of firewall, you have to 
rem add exception rule to the firewall.
rem Defaults to "9999"
set AGENT_RESTART_PORT=9999

rem Agent listens on this address.
rem Default to "localhost"
#set AGENT_RESTART_ADDRESS=${AGENT_RESTART_ADDRESS}
set AGENT_RESTART_ADDRESS=localhost

rem Console application send restart command to agent.
rem Default to "RESTART" string.
set AGENT_RESTART_COMMAND=RESTART

rem After getting restart command, agent starts a process that
rem is responsible for restarting tomcat. Java Process class takes
rem format as "cmd args script". For example, "new Process(cmd, /C, catalina.bat start)"
set TOMCAT_STARTUP_PROCESS_CMD=cmd
set TOMCAT_STARTUP_PROCESS_CMD_ARGS=/C
set TOMCAT_STARTUP_SCRIPT=catalina.bat start
rem set TOMCAT_STARTUP_SCRIPT=HELLO.bat

rem After getting shutdown command, agent shutdowns current tomcat server
rem via shutdown command. It contacts with tomcat via shutdown address,
rem port and command.
rem Shutdown address.
rem If this is not a localhost, it also must be defined in server.xml <Server address="address"> attribute.
set TOMCAT_SHUTDOWN_ADDRESS=localhost

rem Tomcat standard shutdown port.
set TOMCAT_SHUTDOWN_PORT=${TOMCAT_SHUTDOWN_PORT}

rem Shutdown command.
set TOMCAT_SHUTDOWN_COMMAND=SHUTDOWN
set JPDA_OPTS=-agentlib:jdwp=transport=dt_socket,address=18000,server=y,suspend=n

if not ""%1"" == ""debug"" goto runJava

%_RUNJAVA% -cp %AGENT_JAR% %JPDA_OPTS% com.managecat.hook.Agent
goto end

:runJava
echo CATALINA_HOME = %CATALINA_HOME%
echo _RUNJAVA = %_RUNJAVA%
%_RUNJAVA% -cp %AGENT_JAR% com.managecat.hook.Agent
goto end

:exit
exit /b 1

:end