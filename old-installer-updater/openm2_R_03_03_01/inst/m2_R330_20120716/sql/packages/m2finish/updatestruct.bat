@echo off
rem ***************************************************************************
rem * Update the structures during the database update.
rem ***************************************************************************

setlocal

rem execute the several scripts:
call %bindir%getDateTime
echo starting structures at %dateTime%

rem nothing to do

call %bindir%getDateTime
echo finished structures at %dateTime%

:end
endlocal
