@echo off
rem ***************************************************************************
rem * Update the procedures during the database update.
rem ***************************************************************************

setlocal

rem execute the several scripts:
call %bindir%getDateTime
echo starting updatedata at %dateTime%

rem nothing to do
rem call %binDir%common\updatedata

call %bindir%getDateTime
echo finished updatedata at %dateTime%

:end
endlocal
