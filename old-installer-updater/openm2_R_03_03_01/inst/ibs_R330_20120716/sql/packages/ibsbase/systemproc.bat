@echo off
rem ***************************************************************************
rem * Install the procedures during the database installation.
rem ***************************************************************************

setlocal

rem execute the several scripts:
call %bindir%getDateTime
echo starting system procedures at %dateTime%

%db% %pkgSourceDir%procedures\Helpers.sql
%db% %pkgSourceDir%procedures\ErrorProc.sql
%db% %pkgSourceDir%procedures\Helpers2.sql

call %bindir%getDateTime
echo finished system procedures at %dateTime%

:end
endlocal
