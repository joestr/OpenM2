@echo off
rem ***************************************************************************
rem * Perform the database installation for one package.
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

echo installing package %pkgName% in database %dbName%...

rem execute the several scripts:
rem showinfo
rem clean
rem dropdb
rem createdb
rem delSQLErrors
echo installing structures...

set execbat=%pkgBinDir%installstruct.bat
if NOT EXIST %execbat% set execbat=%binDir%common\installstruct.bat
call %execbat%

rem call %pkgBinDir%installstruct      >> "%actLogDir%%pkgName%_struct_%fileName%.log"
rem checkSQLErrors
echo installing base data...
set execbat=%pkgBinDir%installdata.bat
if NOT EXIST %execbat% set execbat=%binDir%common\installdata.bat
call %execbat%                      >> "%pkgLogName%10_data_%fileName%.log"

goto end

:syntax
rem display the syntax:
set errorNum=1

:end
call %binDir%standardEnd
echo.

endlocal
