@echo off
rem ***************************************************************************
rem * Install the procedures during the database installation.
rem ***************************************************************************

setlocal

rem execute the several scripts:
rem install basic procedures:
rem install other procedures:
%dbexec% %pkgSourceDir%procedures

:end
endlocal
