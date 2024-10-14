@echo off
rem ***************************************************************************
rem * Execute one sql file on the database.
rem *
rem * @input parameters:
rem * @param    filename    The name (and path) of the file to be executed.
rem ***************************************************************************

setlocal

rem store the call name:
set callName=%0
set fileName=%1
set className=ibs.install.SQLSExec

rem check the syntax:
if #%fileName%#==## goto syntax

if NOT EXIST %fileName% goto filenotfound

call %bindir%getDateTime
echo %dateTime%
rem echo ____________________________________________________________
rem echo executing %fileName%...

rem execute the file:
rem echo %java% -cp %classP% %className% %dbServer% %dbName% %dbUsername% %dbPassword% %fileName% %dispType%
%java% -cp %classP% %className% %dbServer% %dbName% %dbUsername% %dbPassword% %fileName% %dispType%

goto end

:filenotfound
echo "==> Warning: File %fileName% was not found."
goto end

:syntax
rem display the syntax:
echo Syntax: %callName% filename
pause

:end
endlocal
