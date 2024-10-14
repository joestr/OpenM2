@echo off
rem ***************************************************************************
rem * Perform one file for the database installation.
rem *
rem * @input parameters:
rem * @param    [erroNum}       Has there been a syntax exception ocurred?
rem *                           If parameter not set there is no exception.
rem *                           Values:
rem *                           0 ... no exception
rem *                           1 ... syntax exception
rem ***************************************************************************

rem go to original directory:
rem popd

rem check exception:
if "%errorNum%" == "" goto end
if "%errorNum%" == "1" goto syntax

rem no exception:
goto end

:syntax

rem display the syntax:
echo Syntax: %syntax%

:end
