@echo off
rem ***************************************************************************
rem * Set the configuration values.
rem ***************************************************************************

rem the directories:
rem set javaDir=c:\Programme\Java
rem set sourceDir=%appDir%sources\
rem set sourceDir=C:\daten\work\cvs\m2c\src\sql\DB2\

rem the classes:
set javaClasses=%JAVA_HOME%\jre\lib\rt.jar

rem the used java interpreter:
rem possible values:
rem jdkjava ... Java interpreter of JDK
rem msjava .... Microsoft Java interpreter
rem Default: jdkjava
rem set java=%jdkjava%

rem execution parameters:
set dispType=dispone
set dbServer=openm2-VM
set dbName=openm2
rem database type:
set dbType=MS-SQL
rem standard installation user name:
set dbUsername=sa
set dbPassword=sa
rem language for installation:
set language=english

rem web directory of installation
set wwwDir=c:\wwwroot\openm2

rem just for update: version from which to update
rem this value is the directory within <sqlsources>/update from which to get
rem the update sql scripts.
rem set oldVersionNo=2.4
