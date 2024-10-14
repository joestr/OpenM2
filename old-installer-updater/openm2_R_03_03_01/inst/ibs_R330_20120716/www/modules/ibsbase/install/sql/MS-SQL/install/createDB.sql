/************************************************************************
 * Create database medium with SIZE/512 MB (i.e. 102400 means 200 MB)
 ************************************************************************/

PRINT 'Datenbankmedium anlegen'
GO

DISK INIT
    NAME = '<db>',
    PHYSNAME = '<dbpath>\<db>.dat',
    VDEVNO = 31,
    SIZE = <dbsize>
GO

IF @@ERROR = 0
    PRINT 'Datenbankmedium angelegt'
ELSE
    PRINT 'Fehler beim Anlegen des Datenbankmediums'
GO


PRINT 'Protokollmedium anlegen'
GO

DISK INIT
    NAME = '<dblog>',
    PHYSNAME = '<dbpath>\<dblog>.dat',
    VDEVNO = 32,
    SIZE = <dblogsize>
GO

IF @@ERROR = 0
    PRINT 'Protokollmedium angelegt'
ELSE
    PRINT 'Fehler beim Anlegen des Protokollmediums'
GO


/************************************************************************
 * Create database with n MB
 ************************************************************************/


PRINT 'Datenbank anlegen'
GO

CREATE DATABASE <db>
ON <db> = <dbsizeMB>
LOG ON <dblog> = <dblogsizeMB>
GO

IF @@ERROR = 0
    PRINT 'Datenbank angelegt'
ELSE
    PRINT 'Fehler beim Anlegen der Datenbank'
GO
