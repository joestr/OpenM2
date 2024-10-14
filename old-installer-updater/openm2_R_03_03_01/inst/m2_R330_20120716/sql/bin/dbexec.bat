@echo off
rem ***************************************************************************
rem * Loop through one directory and execute each sql file on the database.
rem *
rem * @input parameters:
rem * @param    dirname The name of the directory with the files.
rem ***************************************************************************

setlocal

rem store the call name:
set callName=%0
set dirName=%1

rem check the syntax:
if #%dirName%#==## goto syntax

rem get the environment settings:
if #%confDir%# == ## (
    call %~dp0..\conf\env
) else (
    call %confDir%env
)

if NOT EXIST %dirName% goto dirnotfound

rem echo executing all sql files in directory "%dirName%"...

rem execute the files:
%java% -cp %classP% ibs.install.SQLSExecDir %dbServer% %dbName% %dbUsername% %dbPassword% %dirName% %dispType%
rem for /F " usebackq" %%i in (`dir /B /ON "%dirName%\*.sql"`) do call %binDir%db %dirName%\%%i

goto end
:dirnotfound
echo "==> Warning: Directory %dirName% was not found."
goto end

:syntax
rem display the syntax:
echo Syntax: %callName% dirname
pause

:end
endlocal
