@echo off
rem ***************************************************************************
rem * Install the procedures during the database installation.
rem ***************************************************************************

setlocal

rem execute the several scripts:
call %bindir%getDateTime
echo starting installdata at %dateTime%

%db% %pkgSourceDir%install\createBaseData.sql
%db% %pkgSourceDir%install\createSchemes.sql
%db% %pkgSourceDir%install\createBaseDomain.sql

call %bindir%getDateTime
echo finished installdata at %dateTime%

:end
endlocal
