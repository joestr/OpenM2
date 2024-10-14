@echo off
pushd inst\bin
call install
popd

rem install.bat
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

rem installapp.bat
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

rem features.bat
@echo off
rem ***************************************************************************
rem * m2 features.
rem *
rem * @input parameters: none
rem ***************************************************************************

setlocal
%call% "ibs" "ibs_R330_20120716" 
%call% "m2" "m2_R330_20120716" 
endlocal

