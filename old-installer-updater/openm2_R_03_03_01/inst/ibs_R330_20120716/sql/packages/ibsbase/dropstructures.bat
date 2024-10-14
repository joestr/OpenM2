@echo off
rem ***************************************************************************
rem * Install the procedures during the database installation.
rem ***************************************************************************

setlocal

rem execute the several scripts:
call %bindir%getDateTime
echo starting dropstructures at %dateTime%

%db% %pkgSourceDir%install\dropAllStructures.sql
rem %db% %pkgSourceDir%install\dropAllTriggers.sql
rem %db% %pkgSourceDir%install\dropAllFunctions.sql
rem %db% %pkgSourceDir%install\dropAllProcedures.sql
rem %db% %pkgSourceDir%install\dropAllViews.sql
rem %db% %pkgSourceDir%install\dropAllTables.sql
rem %db% %pkgSourceDir%install\dropAllProcedures.sql

call %bindir%getDateTime
echo finished procedures at %dateTime%

:end
endlocal
