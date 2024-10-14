@echo off
rem ***************************************************************************
rem * update the structures during the database update.
rem ***************************************************************************

setlocal

rem execute the several scripts:
call %bindir%getDateTime
echo starting structures at %dateTime%

rem echo   updating system procedures...
rem call %pkgBinDir%systemproc      >> "%pkgLogName%01_sysproc_%fileName%.log"
rem echo   dropping old structures...
rem call %pkgBinDir%dropstructures  >> "%pkgLogName%02_dropstruct_%fileName%.log"
rem echo   updating datatypes...
rem %db% %pkgSourceDir%install\Datatypes.sql >> "%pkgLogName%03_datatypes_%fileName%.log"
echo   updating system procedures...
call %pkgBinDir%systemproc      >> "%pkgLogName%04_sysproc_%fileName%.log"

rem update common structures:
call %binDir%common\updatestruct

call %bindir%getDateTime
echo finished structures at %dateTime%

:end
endlocal
