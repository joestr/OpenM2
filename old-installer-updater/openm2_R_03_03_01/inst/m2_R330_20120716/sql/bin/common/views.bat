@echo off
rem ***************************************************************************
rem * Install the procedures during the database installation.
rem ***************************************************************************

setlocal

rem execute the several scripts:

rem install basic views:
rem install the other views:
%dbexec% %pkgSourceDir%views

:end
endlocal
