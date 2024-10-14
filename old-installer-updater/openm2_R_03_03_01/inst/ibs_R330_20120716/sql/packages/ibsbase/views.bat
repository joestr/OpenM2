@echo off
rem ***************************************************************************
rem * Install the procedures during the database installation.
rem ***************************************************************************

setlocal

rem execute the several scripts:
call %bindir%getDateTime
echo starting views at %dateTime%

rem install basic views:
%db% %pkgSourceDir%views\ContainerViews.sql
rem install the other views:
%dbexec% %pkgSourceDir%views
%db% %pkgSourceDir%views\AttachmentContainer_01Views.sql
%db% %pkgSourceDir%views\Catalog_01Views.sql
%db% %pkgSourceDir%views\CleanContainerViews.sql
%db% %pkgSourceDir%views\MayContainViews.sql

call %bindir%getDateTime
echo finished views at %dateTime%

:end
endlocal
