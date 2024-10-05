@ECHO OFF
REM ##########################################################################
REM !!!!! PLEASE CONFIGURE PATH AND SERVER !!!!!!
SET m2PATH=c:\wwwroot\appName
SET m2Server=m2Server
SET m2Application=appName
SET m2User=Administrator
SET m2UserPassword=m2UserPwd
SET NTDomain=domain
SET NTUSER=ntuser
SET NTPWD=ntuserpwd
REM ##########################################################################
SET libPATH=%m2PATH%\WEB-INF\lib
REM ##########################################################################
REM SET CLASSPATH=%m2PATH%\WEB-INF\classes;%libPATH%\ibs.jar;%libPATH%\ibsbase_2.7.1_cryptix-jce-provider.jar;%libPATH%\ibsbase_2.7.1_mail.jar
call setClasspath %m2PATH%

SET NOTIFY=-NOTIFY YES -NOTIFYSUBJECT "NOTIFYSUBJECT" -NOTIFYRECEIVER "emailReceiver@domain.at" -NOTIFYSENDER "emailSender@trinitec.at"

ECHO ##########################################################################
ECHO # The agents are started in separate dos boxes
ECHO #  using m2Server: %m2Server%
ECHO #  using m2PATH  : %m2PATH%
ECHO #
ECHO # Version 1.0 (c) DIBB 2003
ECHO ##########################################################################

REM ##########################################################################
REM # Agent for <insert Description>
REM # !!!!! PLEASE CONFIGURE AGENT SPECIFIC PARAMETERS !!!!!!
REM ##########################################################################
start cmd /C java ibs.di.agent.ImportAgent_01 -SERVER %m2Server% -APPPATH /%m2Application%/ -USER %m2User% -PW %m2UserPwd% -CONTAINER "Administration/Data Interchange/Import" -FREQUENCY day -EVERY 1 -TIME 23:00:00 -CONNECTOR CONNECTOR -TRANSLATOR TRANSLATOR -SCRIPT IMPORTSCRIPT -FILTER FILTER.xml -WAIT -DELETE -SORT ASC -NTDOMAIN %NTDomain% -NTUSER %NTUSER% -NTPW %NTPWD% %NOTIFY%
