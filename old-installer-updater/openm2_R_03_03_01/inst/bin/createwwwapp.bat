@echo off
rem ***************************************************************************
rem * Create www content for an application within the www directory.
rem *
rem * @input parameters:
rem * @param    name    Name of application.
rem * @param    dir     Directory where to find the installation package.
rem ***************************************************************************

setlocal

echo.
echo.
echo == creating www content for %~1 ...
echo ============================================
echo copying www directories ...
xcopy ..\%~2\www %wwwDir% /S /I /Q /R /Y

endlocal
