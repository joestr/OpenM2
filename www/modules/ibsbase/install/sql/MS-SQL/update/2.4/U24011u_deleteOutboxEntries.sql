/******************************************************************************
 * Task:        TASK CRE4 - Optimization
 *
 * Description: ´Drop all currently existing outbox entries.
 *
 * Repeatable:  yes
 *
 * @version     $Id: U24011u_deleteOutboxEntries.sql,v 1.1 2005/04/11 19:23:04 klaus Exp $
 *
 * @author      Klaus Reimüller (KR) 050404
 ******************************************************************************
 */ 

PRINT 'starting $RCSFile$...'
GO

-- don't show count messages:
SET NOCOUNT ON
GO

DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_languageId           INT,            -- the current language

    -- local variables:
    @l_file                 VARCHAR (7),    -- name of actual file
    @l_retValue             INT,            -- return value of function
    @l_msg                  VARCHAR (5000), -- the actual message
    @l_cnt                  INTEGER         -- counter

-- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_languageId           = 0

-- initialize local variables:
SELECT
    @l_file                 = 'U24011u',
    @l_retValue             = @c_ALL_RIGHT

-- body:
    -- get the number of SentObjects to be deleted:
    SELECT  @l_cnt = COUNT (osent.oid)
    FROM    ibs_Object osent,
            ibs_TVersion tv, ibs_Type t
    WHERE   osent.tVersionId = tv.id
        AND tv.typeId = t.id
        AND t.code = 'SentObject'
        AND osent.state = 2

    -- delete all SentObject entries and objects which are below these:
    UPDATE  ibs_Object
    SET     state = 1
    WHERE   oid IN
            (
                SELECT  o.oid
                FROM    ibs_Object o, ibs_Object osent,
                        ibs_TVersion tv, ibs_Type t
                WHERE   osent.tVersionId = tv.id
                    AND tv.typeId = t.id
                    AND t.code = 'SentObject'
                    AND osent.state = 2
                    AND o.posNoPath LIKE osent.posNoPath + '%'
            )

    -- show message:
    SELECT  @l_msg = @l_file + ': ' + LTRIM (STR (@l_cnt, 10)) +
            ' SentObjects deleted.'
    PRINT @l_msg

    -- show state message:
    SELECT  @l_msg = @l_file + ': finished.'
    PRINT @l_msg
GO

-- show count messages again:
SET NOCOUNT OFF
GO

PRINT '$RCSFile$: finished.'
GO
