/******************************************************************************
 * Task:        IBS-400 m2ml - MLI - Extend the user object by a locale
 *              property
 *
 * Description: This file contains all structural information which is
 *              necessary to create an update file for specific changes in the
 *              database content.
 *              Throughout this script the following tags are used:
 *              ibs_UserProfile ....... The name of the table to be updated.
 *              ibs_UserProfile_temp ... The name of the temporary table containing
 *                                  the new table scheme.
 *
 * Repeatable:  yes
 *
 * @version     $Id: U300002a_UserPofileTableChange.sql,v 1.6 2010/05/12 11:38:04 btatzmann Exp $
 *
 * @author      Bernhard Tatzmann (BT) 20100312
 ******************************************************************************
 */ 

PRINT 'starting $RCSFile$...'
GO

-- don't show count messages:
SET NOCOUNT ON
GO

-- create the table:
CREATE TABLE ibs_UserProfile_temp
(
    oid                     OBJECTID        NOT NULL,
    userId                  USERID          NOT NULL PRIMARY KEY,
    newsTimeLimit           INT             NULL DEFAULT 5,
    newsShowOnlyUnread      BOOL            NOT NULL,
    outboxUseTimeLimit      BOOL            NOT NULL,
    outboxTimeLimit         INT             NULL,
    outboxUseTimeFrame      BOOL            NOT NULL,
    outboxTimeFrameFrom     DATETIME        NULL,
    outboxTimeFrameTo       DATETIME        NULL,
    showExtendedAttributes  BOOL            NOT NULL,
    showFilesInWindows      BOOL            NOT NULL,
    lastLogin               DATETIME        NULL,
    layoutId                OBJECTID        NULL,
    showRef                 BOOL            NOT NULL,
    showExtendedRights      BOOL            NOT NULL,
    saveProfile             BOOL            NOT NULL,
    notificationKind        INT             NULL,
    sendSms                 BOOL            NOT NULL,
    addWeblink              BOOL            NOT NULL,
    localeId                OBJECTID        NULL
)
GO
-- ibs_UserProfile_temp


DECLARE
    -- constants:

    -- local variables:
    @l_file                 VARCHAR (7),    -- name of actual file
    @l_error                INT,            -- the actual error code
    @l_ePos                 VARCHAR (2000), -- error position description
    @l_msg                  VARCHAR (5000), -- the actual message
    @l_tableName            VARCHAR (30),   -- the table name
    @l_tempTableName        VARCHAR (30),   -- the temporary table name
    @l_defaulLocale         OBJECTID,       -- the default locale oid
    @l_defaulLocale_s       OBJECTIDSTRING  -- the default locale oid

-- assign constants:

-- initialize local variables:
SELECT
    @l_file = 'U300002a_UserPofileTableChange',
    @l_error = 0,
    @l_tableName = 'ibs_UserProfile',
    @l_tempTableName = 'ibs_UserProfile_temp'
    
-- retrieve the default locale:  
SELECT  @l_defaulLocale = MAX (l.oid)
FROM    ibs_Locale_01 l, ibs_object o
WHERE   o.oid = l.oid
        AND o.state = 2
        AND isDefault = 1

SELECT  @l_defaulLocale_s = dbo.f_byteToString(@l_defaulLocale)

-- body:
    -- call the procedure which changes the old table scheme to the new one:
    -- for each new attribute set a default value either as number or as string
    EXEC p_changeTable @l_file, @l_tableName, @l_tempTableName,
        'localeId', @l_defaulLocale_s

    -- ensure that the temporary table is dropped:
    EXEC p_dropTable @l_tempTableName

    -- jump to end of code block:
    GOTO finish

exception:                              -- an error occurred
    -- log the error:
    EXEC ibs_error.logError 500, @l_file, @l_error, @l_ePos,
        '', 0,
        'l_tableName', @l_tableName,
        '', 0,
        'l_tempTableName', @l_tempTableName
    SELECT  @l_msg = @l_file + ': Error when changing table ' +
            @l_tableName + ':'
    PRINT @l_msg
    SELECT  @l_msg = 'Error ' + CONVERT (VARCHAR, @l_error) +
            '; position: ' + @l_ePos
    PRINT @l_msg

finish:
    -- show state message:
    SELECT  @l_msg = @l_file + ': finished'
    PRINT @l_msg
GO


-- here come the trigger definitions:
GO

-- show count messages again:
SET NOCOUNT OFF
GO

PRINT '$RCSFile$: finished.'
GO
