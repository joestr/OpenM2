@echo off
rem ***************************************************************************
rem * Install an application.
rem *
rem * @input parameters:
rem * @param    name    Name of application.
rem * @param    dir     Directory where to find the installation package.
rem ***************************************************************************

setlocal

echo.
echo.
echo == installing %~1 ...
echo ============================
xcopy ..\..\conf ..\%~2\sql\conf /S /I /Q /R /Y

echo installing db ...
pushd ..\%~2\sql\bin
call install.bat
popd

rem echo copying www directories ...
rem xcopy %~2\www www /S /I /Q /R /Y

endlocal
