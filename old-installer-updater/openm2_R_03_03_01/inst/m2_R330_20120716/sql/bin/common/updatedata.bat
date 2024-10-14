@echo off
rem ***************************************************************************
rem * Install the procedures during the database installation.
rem ***************************************************************************

setlocal

rem execute the several scripts:
rem %db% %pkgMultilangDir%createObjectDesc.sql
%db% %pkgMultilangDir%createTokens.sql
%db% %pkgMultilangDir%createMessages.sql
%db% %pkgMultilangDir%createExceptions.sql
rem %db% %pkgMultilangDir%createTypeNames.sql

echo   installing update scripts...
%dbexec% %pkgSourceDir%update\%oldVersionNo% >> "%pkgLogName%11_update_%fileName%.log"


:end
endlocal
