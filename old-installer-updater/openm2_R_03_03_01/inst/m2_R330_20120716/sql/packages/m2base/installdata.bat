@echo off
rem ***************************************************************************
rem * Install the procedures during the database installation.
rem ***************************************************************************

setlocal

rem execute the several scripts:
call %bindir%getDateTime
echo starting installdata at %dateTime%

%db% %pkgSourceDir%install\deleteAllTableContents.sql

%db% %confDir%installConfig.sql

call %binDir%common\installdata

%db% %pkgSourceDir%always\createBaseObjectTypes.sql
%db% %pkgSourceDir%always\createTVersionProc.sql
%db% %pkgSourceDir%install\createMayContainEntries.sql

call %bindir%getDateTime
echo finished installdata at %dateTime%

:end
endlocal




