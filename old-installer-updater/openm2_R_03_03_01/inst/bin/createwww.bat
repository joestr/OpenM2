@echo off
rem ***************************************************************************
rem * Create www dir for m2 applications.
rem *
rem * @input parameters: none
rem ***************************************************************************

setlocal

rem get the environment settings:
if "%confDir%" == "" (
    call "%~dp0..\..\conf\env"
) else (
    call "%confDir%env"
)

set call=call createwwwapp
set wwwAddOnDir=%~dp0..\..\wwwAddOn

if exist "%wwwDir%" goto wwwdirexists
if not exist "%wwwDir%" md %wwwDir%

call features

rem check if there exist an additional www directory:
if not exist "%wwwAddOnDir%" goto wwwaddonfinished
rem copy the content of the add on directory over the www directory:
echo.
echo ============================================
echo copying add-on directories ...
xcopy "%wwwAddOnDir%" "%wwwDir%" /S /I /Q /R /Y

:wwwaddonfinished
echo.
echo == creating of www directory finished.

echo Please set the configuration values.
explorer "%wwwDir%\conf"
goto end

:wwwdirexists
echo.
echo The directory already exists.
echo Please ensure to delete it before.

:end
pause
endlocal
