@echo off
REM # **********************************************
REM # Clean Up Tomcat Instance One JVM
REM # **********************************************
REM #
set CATALINA_BASE=..\..\tomcat-BASE-01
set CATALINA_HOME=..\..\tomcat-HOME

REM #
REM # Clear All Logs 
rm -r %CATALINA_BASE%\logs\*

REM #
REM # Clear all Deployed Applications, tailor as needed...
rm -r %CATALINA_BASE%\webapps\HelloWorld*
rm -r %CATALINA_BASE%\webapps\DynamicContentTool*
rm -r %CATALINA_BASE%\webapps\CorpReports*
rm -r %CATALINA_BASE%\webapps\PubRptWeb*
rm -r %CATALINA_BASE%\webapps\ReportBrowserWeb*
rm -r %CATALINA_BASE%\webapps\signon*
rm -r %CATALINA_BASE%\webapps\WebAuthentication*
rm -r %CATALINA_BASE%\webapps\SharedServicesWeb*
rm -r %CATALINA_BASE%\webapps\LoggingServices*
rm -r %CATALINA_BASE%\webapps\EOSWeb*
rm -r %CATALINA_BASE%\webapps\portinator*

REM #
REM # Clear out Work
rm -r %CATALINA_BASE%\work\Catalina