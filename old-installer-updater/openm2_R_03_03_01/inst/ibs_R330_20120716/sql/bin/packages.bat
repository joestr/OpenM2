@echo off
rem ***************************************************************************
rem * Packages to be installed/updated.
rem *
rem * @input parameters:
rem * @param    [dbname]        The name of the database.
rem * @param    [displaytype]   The display type:
rem *                           dispno ..... don't display the executed stmts.
rem *                           dispone .... display first line of stmt.
rem *                           dispfull ... display full statement.
rem * @param    [username]      The user for connecting to the database.
rem * @param    [password]      The password for the user.
rem ***************************************************************************

rem install the several packages:
%call% ibsbase %*
%call% ibsdoc %*
%call% ibswebdav %*
%call% ibsworkflow %*
%call% ibsfinish %*
