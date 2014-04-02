@echo off

set PROJECTNAME=JMSClientApp

echo Setting Environment Variables to install the %PROJECTNAME% project.

if "%WEBLOGICHOME%"=="" set WEBLOGICHOME=c:\bea\wlserver_10.3
if "%PROJECTSHOME%"=="" set PROJECTSHOME=c:\bea\wlserver_10.3\JMSClientApp

set JAVAC=javac
set JAVARUN=java

set PROJECTSRC=%PROJECTSHOME%
set EJBSRC=%PROJECTSRC%\ejb

 
@echo on

echo "Call setExamplesEnv"
call %WEBLOGICHOME%\samples\domains\wl_server\setExamplesEnv.cmd
