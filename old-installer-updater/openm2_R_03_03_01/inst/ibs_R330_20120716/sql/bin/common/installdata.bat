@echo off
rem ***************************************************************************
rem * Install the procedures during the database installation.
rem ***************************************************************************

setlocal

rem execute the several scripts:
%db% %pkgSourceDir%install\createObjectDesc.sql
%db% %pkgSourceDir%install\createTypeNames.sql

:end
endlocal
