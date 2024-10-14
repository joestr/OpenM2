@echo off
rem ***************************************************************************
rem * Set the environment variables.
rem ***************************************************************************

rem #####################################
rem # standard preliminaries            #
rem #####################################

rem get the actual date and time:
FOR /F "tokens=2 delims= " %%i IN ('date /T') DO set dateTime=%%i
FOR /F "tokens=1 delims= " %%i IN ('time /T') DO set dateTime=%dateTime% %%i

rem set the fileName:
if not exist "%binDir%getFileName.bat" goto nofilename
    call %binDir%getFileName
:nofilename

rem the directories:
set confDir=%~dp0
if "%javaDir%" == "" set javaDir=c:\Programme\Java
set appDir=%confDir%..\
rem set sourceDir=\\Intosdev2\AS400\
set packageDir=%appDir%packages\
set commonDir=%appDir%common\
set testDir=%commonDir%test\
set logDir=%appDir%log\

rem the java compiler:
rem set javac=%JAVA_HOME%\bin\javac
set javac=javac

rem the MS java interpreter:
set msjava=jview
rem the JDK java interpreter:
rem set jdkjava=%JAVA_HOME%\bin\java
set jdkjava=java
rem the default java interpreter:
set java=%jdkjava%

rem program to create JAR files:
set jar=jar

rem default execution parameters:
set dispType=dispone
set dbServer=klaus1
set dbName=m2
set dbType=MS-SQL
rem standard installation user name:
set dbUsername=sa
set dbPassword=sa
rem language for installation:
set language=german

rem package counter:
if "%pkgCount%" == "" set pkgCount=0
set /A _tmp2=pkgCount/10
set /A _tmp1=pkgCount - (pkgCount/10)*10
set pkgCountFormatted=%_tmp2%%_tmp1%

rem #####################################
rem # don't change the rest of the file #
rem #####################################

rem configuration:
call %confDir%conf

rem standard environment values:
call %confDir%envStandard
