@echo off
rem ***************************************************************************
rem * Perform the database update for one package.
rem *
rem * @input parameters:
rem * @param    pkgName         The name of the package.
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

set /A pkgCount+=1

setlocal

set pkgName=%~1

rem check the syntax:
if "%pkgName%"=="" goto syntax

call standardStart %0 %*

echo updating package %pkgName% in database %dbName%...

rem execute the several scripts:

echo updating data...
set execbat=%pkgBinDir%updatedata.bat
if NOT EXIST %execbat% set execbat=%binDir%common\updatedata.bat
call %execbat%                      >> "%pkgLogName%10_data_%fileName%.log"

echo updating structures...

set execbat=%pkgBinDir%updatestruct.bat
if NOT EXIST %execbat% set execbat=%binDir%common\updatestruct.bat
call %execbat%

rem call %pkgBinDir%updatestruct      >> "%actLogDir%%pkgName%_struct_%fileName%.log"
rem checkSQLErrors

goto end

:syntax
rem display the syntax:
set errorNum=1

:end
call %binDir%standardEnd
echo.

endlocal
