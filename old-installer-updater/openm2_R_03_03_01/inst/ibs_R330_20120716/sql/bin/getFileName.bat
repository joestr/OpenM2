@echo off
rem ***************************************************************************
rem * Get a file name which consists of the actual date and time.
rem * The result has the format yyyymmddhhmisshs.
rem *
rem * @input parameters:
rem * @param    [directory] The directory where the file shall resist in.
rem ***************************************************************************

rem get the actual date in format yyyymmdd:
FOR /F "tokens=1,2,3 delims=./abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ " %%i IN ("%DATE%") DO set fileName=%%k%%j%%i
rem FOR /F "tokens=1,2,3 delims=: " %%i IN ('time /T') DO set fileName=%fileName%%%i%%j%%k

rem get the actual time in format hmisshs:
FOR /F "tokens=1,2,3,4 delims=:,." %%i IN ("%TIME%") DO set temptime=%%i%%j%%k%%l
rem ensure that there is a "0" in front if the hour is less than 10:
if "%temptime:~0,1%" == " " SET temptime=0%temptime:~1%
rem add the time to the file name:
set fileName=%fileName%%temptime%

if "%1" == "" GOTO finish
set fileName=%1\%fileName%
:finish
