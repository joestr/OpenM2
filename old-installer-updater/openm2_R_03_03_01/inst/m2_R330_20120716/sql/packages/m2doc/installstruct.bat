@echo off
rem ***************************************************************************
rem * Install the structures during the database installation.
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
