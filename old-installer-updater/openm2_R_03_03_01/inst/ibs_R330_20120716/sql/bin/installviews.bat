@echo off
rem ***************************************************************************
rem * Perform the database installation.
rem *
rem * @input parameters:
rem * @param    [dbname]        The name of the database.
rem * @param    [displaytype]   The display type:
rem *                           dispno ..... don't display the executed stmts.
rem *                           dispone .... display first line of stmt.
rem *                           dispfull ... display full statement.
rem * @param    [username]      The user for connecting to the database.
rem * @param    [password]      The password for the user.
rem ***************************************************************************

setlocal

set call=call installpkgviews

rem check the syntax:
rem if "%dirName%"=="" goto syntax

rem get the environment settings:
if "%confDir%" == "" (
    call %~dp0..\conf\env
) else (
    call %confDir%env
)

rem install the several packages:
call packages %*

rem errors in sql scripts are marked with "==> SQL state" in the logging files

echo ready.

goto end

:syntax
rem display the syntax:
set errorNum=1

:end
pause
endlocal
