@echo off
rem ***************************************************************************
rem * Update an application.
rem *
rem * @input parameters:
rem * @param    name    Name of application.
rem * @param    dir     Directory where to find the installation package.
rem ***************************************************************************

setlocal

echo.
echo.
echo == updating %~1 ...
echo ===========================
xcopy ..\..\conf ..\%~2\sql\conf /S /I /Q /R /Y

echo updating db ...
pushd ..\%~2\sql\bin
call update.bat
popd

rem echo copying www directories ...
rem xcopy %~2\www www /S /I /Q /R /Y

endlocal
