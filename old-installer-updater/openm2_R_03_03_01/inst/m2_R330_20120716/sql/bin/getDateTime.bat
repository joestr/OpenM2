@echo off
FOR /F "tokens=2 delims= " %%i IN ('date /T') DO set dateTime=%%i
FOR /F "tokens=1 delims= " %%i IN ('time /T') DO set dateTime=%dateTime% %%i
