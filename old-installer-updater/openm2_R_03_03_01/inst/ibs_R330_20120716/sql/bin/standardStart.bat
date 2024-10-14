@echo off
rem ***************************************************************************
rem * Perform one file for the database installation.
rem *
rem * @input parameters:
rem * @param    callName        The name of the calling file
rem * @param    pkgname         The name of the package.
rem *                           This name must correspond to a directory name
rem *                           within the sources directory.
rem * @param    [dbname]        The name of the database.
rem * @param    [displaytype]   The display type:
rem *                           dispno ..... don't display the executed stmts.
rem *                           dispone .... display first line of stmt.
rem *                           dispfull ... display full statement.
rem * @param    [username]      The user for connecting to the database.
rem * @param    [password]      The password for the user.
rem ***************************************************************************

rem store the call name:
set callName=%1
set pkgName=%~2
set errorNum=0
set syntax=%callName% pkgname [libraryname [displaymode [username [password]]]]

rem check the syntax:
rem if "%dirName%"=="" goto syntax

rem get the environment settings:
if "%confDir%" == "" (
    call %~dp1..\conf\envStandard
) else (
    call %confDir%envStandard
)

rem ensure that the log directory was created:
if NOT EXIST %actLogDir% mkdir %actLogDir%

rem go to the base execution directory:
rem pushd %pkgSourceDir%

rem set parameters:
if NOT "%3" == "" set dbName=%3
if NOT "%4" == "" set dispType=%4
if NOT "%5" == "" set dbUsername=%5
if NOT "%6" == "" set dbPassword=%6
