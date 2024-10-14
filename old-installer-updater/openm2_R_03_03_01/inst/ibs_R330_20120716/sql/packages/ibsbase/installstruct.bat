@echo off
rem ***************************************************************************
rem * Install the structures during the database installation.
rem ***************************************************************************

setlocal

rem execute the several scripts:
call %bindir%getDateTime
echo starting structures at %dateTime%

echo   dropping old structures...
call %pkgBinDir%dropstructures  >> "%pkgLogName%01_dropstruct_%fileName%.log"
echo   installing datatypes...
%db% %pkgSourceDir%install\Datatypes.sql >> "%pkgLogName%02_datatypes_%fileName%.log"
echo   installing system procedures...
call %pkgBinDir%systemproc      >> "%pkgLogName%03_sysproc_%fileName%.log"

rem install common structures:
call %binDir%common\installstruct

call %bindir%getDateTime
echo finished structures at %dateTime%

:end
endlocal
