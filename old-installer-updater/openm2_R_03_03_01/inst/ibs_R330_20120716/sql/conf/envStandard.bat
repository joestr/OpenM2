@echo off
rem ***************************************************************************
rem * Set the standard environment variables.
rem ***************************************************************************

rem #####################################
rem # don't change the rest of the file #
rem #####################################

rem define application standards:
set binDir=%appDir%bin\
set confDir=%appDir%conf\
set libDir=%binDir%lib\
set classDir=%binDir%classes\
set actLogDir=%logDir%%fileName%\

set /A _tmp2=pkgCount/10
set /A _tmp1=pkgCount - (pkgCount/10)*10
set pkgCountFormatted=%_tmp2%%_tmp1%

rem package specific directories:
set pkgDir=%packageDir%%pkgName%\
set pkgBinDir=%pkgDir%
set pkgLogName=%actLogDir%%pkgCountFormatted%%pkgName%
if NOT "%pkgName%" == "" call %pkgBinDir%conf

rem the package source directory:
set pkgSourceDir=%wwwDir%\app\install\sql_%moduleName%\%dbType%\
rem set pkgSourceDir=%appDir%..\www\modules\%moduleName%\install\sql\%dbType%\
set pkgMultilangDir=%wwwDir%\app\install\lang_%moduleName%\%language%\%dbType%\
rem set pkgMultilangDir=%appDir%..\www\modules\%moduleName%\lang\%language%\%dbType%\

rem the classes:
if "%javaClasses%" == "" set javaClasses=%JAVA_HOME%\lib\rt.jar
if NOT EXIST "%javaClasses%" set javaClasses=%JAVA_HOME%\jre\lib\rt.jar
if "%appClasses%" == "" set appClasses=.;%classDir%
if "%dbDriver%" == "" set dbDriver=%libDir%Sprinta2000.jar

rem directories to get used classes from:
set classP=%appClasses%;%javaClasses%;%dbDriver%;

rem calling program parts:
set db=call %binDir%db
set dbexec=call %binDir%dbexec
