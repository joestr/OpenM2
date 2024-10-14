@echo off
rem ***************************************************************************
rem * Install m2 applications.
rem *
rem * @input parameters: none
rem ***************************************************************************

setlocal

set call=call installapp

call features

echo.
echo == installation finished.

pause
endlocal
