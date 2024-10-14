@echo off
rem ***************************************************************************
rem * Install the procedures during the database installation.
rem ***************************************************************************

setlocal

rem execute the several scripts:
call %bindir%getDateTime
echo starting installdata at %dateTime%

%db% %wwwDir%\modules\%moduleName%\install\sql\%dbType%\install\createObjectDesc.sql
%db% %wwwDir%\modules\%moduleName%\install\sql\%dbType%\install\createTypeNames.sql

call %bindir%getDateTime
echo finished installdata at %dateTime%

:end
endlocal
