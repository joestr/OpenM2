@echo off
rem ***************************************************************************
rem * Update m2 applications.
rem *
rem * @input parameters: none
rem ***************************************************************************

setlocal

set call=call updateapp

call features

echo.
echo == update finished.

pause
endlocal
