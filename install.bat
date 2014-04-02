@echo off
echo "Be sure to create JNDI names: Worker.Request and Worker.Response Queue in examples Server"
pause
echo "Removing jar files, if any"
del application\APP-INF\lib\Workingclass.jar
del ejb\messageBean.jar

echo "Deleting previous ejb_worker.jar WorkServlet.war and JMSWorker.ear, if any"
del application\WorkServlet.war
del application\JMSWorker.ear
del application\ejb_worker.jar

@echo off

echo .
echo Installing %PROJECTNAME%
cd %PROJECTSHOME%
%JAVAC% -d . UnitOfWork.java SimpleMatrix.java
jar cvf Workingclass.jar examples
move Workingclass.jar application\APP-INF\lib\.


cd %EJBSRC%
echo Compiling ejb java files...
%JAVAC% -classpath ..\application\APP-INF\lib\Workingclass.jar;%classpath% -d . *.java
echo Running jar on ejb files
jar cvf messageBean.jar META-INF examples
echo Running ejbc...
%JAVARUN% weblogic.ejbc -classpath %classpath%;..\application\APP-INF\lib\Workingclass.jar messageBean.jar ejb_worker.jar
move ejb_worker.jar ..\application\.

echo .
cd %PROJECTSRC%
echo Building class files from java files
%JAVAC% -classpath %classpath%;application\APP-INF\lib\Workingclass.jar -d .\web\web-inf\classes WorkServlet.java

echo .
echo Build WorkServlet.war
cd web
jar cvf WorkServlet.war META-INF WEB-INF
move WorkServlet.war ..\application\.

echo .
echo Build ear; Then Install JMSWorker.ear
cd %PROJECTSRC%\application
echo "Building ear file"
jar cvf JMSWorker.ear WorkServlet.war ejb_worker.jar META-INF APP-INF
java weblogic.Deployer -adminurl t3://localhost:7001 -username weblogic -password weblogic -undeploy -name JMSWorker
java weblogic.Deployer -adminurl t3://localhost:7001 -username weblogic -password weblogic -deploy -upload C:\bea\wlserver_10.3\JMSClientApp\application\JMSWorker.ear
cd ..

echo .
echo build JMS Clients
cd %PROJECTSRC%\JMSClient
%JAVAC% -classpath %classpath%;..\application\APP-INF\lib\Workingclass.jar -d . *.java

cd %PROJECTSRC%

echo Done

@echo on
