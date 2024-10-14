@echo off
rem ***************************************************************************
rem * Delete all log directories which are empty.
rem ***************************************************************************

setlocal

call standardStart %0 %*
set syntax=%callName%

rem check the syntax:
rem if "%dirName%"=="" goto syntax

rem delete all log directories which are empty:
for /F " usebackq" %%i in (`dir /B /ON %logDir%*`) do rmdir %logDir%%%i

goto end

:syntax
rem display the syntax:
set errorNum=1

:end
call %binDir%standardEnd

endlocal
