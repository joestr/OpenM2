@ECHO OFF
REM ##########################################################################
REM Set class path for agents.
REM Parameters: m2Path
REM ##########################################################################
SET m2Path=%~1

if "%m2Path%"=="" goto EOF

SET libPATH=%m2PATH%\WEB-INF\lib
SET CLASSPATH=%m2PATH%\WEB-INF\classes;%libPATH%\ibs.jar

for /F " usebackq eol=; tokens=*" %%i in (`dir /B "%libPath%\*cryptix-jce-provider.jar"`) do SET CLASSPATH=%CLASSPATH%;%%i
for /F " usebackq eol=; tokens=*" %%i in (`dir /B "%libPath%\*mail.jar"`) do SET CLASSPATH=%CLASSPATH%;%%i

rem  SET CLASSPATH=%m2PATH%\WEB-INF\classes;%libPATH%\ibs.jar;%libPATH%\ibsbase_2.7.1_cryptix-jce-provider.jar;%libPATH%\ibsbase_2.7.1_mail.jar

:EOF
