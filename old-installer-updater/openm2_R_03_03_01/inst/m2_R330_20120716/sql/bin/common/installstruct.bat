@echo off
rem ***************************************************************************
rem * Install the structures during the database installation.
rem ***************************************************************************

setlocal

rem execute the several scripts:
echo   installing tables...
%dbexec% %pkgSourceDir%tables   >> "%pkgLogName%05_tables_%fileName%.log"

echo   creating indexes...
%db% %pkgSourceDir%always\createIndices.sql >> "%pkgLogName%06_indices_%fileName%.log"

echo   installing views...
set execbat=%pkgBinDir%views.bat
if NOT EXIST %execbat% set execbat=%binDir%common\views.bat
call %execbat%                  >> "%pkgLogName%07_views_%fileName%.log"

echo   installing procedures...
set execbat=%pkgBinDir%procedures.bat
if NOT EXIST %execbat% set execbat=%binDir%common\procedures.bat
call %execbat%                  >> "%pkgLogName%08_procs_%fileName%.log"

echo   installing triggers...
%dbexec% %pkgSourceDir%triggers >> "%pkgLogName%09_trigs_%fileName%.log"

:end
endlocal
