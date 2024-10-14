/******************************************************************************
 * Task:        EVN CRE4 Performance tuning.
 *
 * Description: Create all entries in table ibs_RightsKey.
 *
 * Repeatable:  yes/no
 *
 * @version     $Id: U24006u_createRkeyEntries.sql,v 1.1 2005/02/15 21:38:48 klaus Exp $
 *
 * @author      Klaus Reimüller (KR) 050213
 ******************************************************************************
 */ 

PRINT 'starting $RCSFile$...'
GO

-- don't show count messages:
SET NOCOUNT ON
GO

DECLARE
    -- constants:
    @c_CONST1               INT,            -- description

    -- local variables:
    @l_file                 VARCHAR (7),    -- name of actual file
    @l_error                INT,            -- the actual error code
    @l_ePos                 VARCHAR (2000), -- error position description
    @l_msg                  VARCHAR (5000), -- the actual message
    @l_cnt                  INT             -- counter

-- assign constants:
SELECT
    @c_CONST1 = 1234567

-- initialize local variables and return values:
SELECT
    @l_file = 'U24006u',
    @l_error = 0,
    @l_cnt = 0

-- body:
    -- check if there are already some entries in ibs_RightsKey:
    SELECT  @l_cnt = COUNT (*)
    FROM    ibs_RightsKey

    IF (@l_cnt = 0)                     -- no entries in ibs_RightsKey?
    BEGIN
        -- create all rights keys within ibs_RightsKey table:
        INSERT INTO ibs_RightsKey
                (rKeysId, owner, cnt)
        SELECT  rks.id, o.owner, rks.cnt
        FROM    (
                    SELECT  DISTINCT rKey, owner
                    FROM    ibs_Object
                ) o,
                (
                    SELECT  DISTINCT id, cnt
                    FROM    ibs_RightsKeys
                ) rks
        WHERE   o.rKey = rks.id

        -- check if there occurred an error:
        EXEC @l_error = ibs_error.prepareError @@error,
            'Error when inserting values into table ibs_RightsKey',
            @l_ePos OUTPUT
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call common exception handler

        -- create all rights keys within ibs_RightsKey table having 0 as
        -- rKeysId:
        INSERT INTO ibs_RightsKey
                (rKeysId, owner, cnt)
        SELECT  0, o.owner, 1
        FROM    (
                    SELECT  DISTINCT rKey, owner
                    FROM    ibs_Object
                ) o
        WHERE   o.rKey = 0
            AND 0 NOT IN
                (
                    SELECT  DISTINCT rKeysId
                    FROm    ibs_RightsKey
                )

        -- check if there occurred an error:
        EXEC @l_error = ibs_error.prepareError @@error,
            'Error when inserting zero values into table ibs_RightsKey',
            @l_ePos OUTPUT
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call common exception handler

        -- set the rKeys in the ibs_Object table:
        UPDATE  ibs_Object
        SET     rKey =
                (
                    SELECT  rkId
                    FROM    (
                                SELECT  id AS rkId, owner AS rkOwner, rKeysId
                                FROM    ibs_RightsKey
                            ) rk
                    WHERE   owner = rkOwner
                        AND rKeysId = rKey
                )

        -- check if there occurred an error:
        EXEC @l_error = ibs_error.prepareError @@error,
            'Error when changing values of attribute rKey in ibs_Object',
            @l_ePos OUTPUT
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call common exception handler
    END -- if no entries in ibs_RightsKey
    ELSE                                -- ibs_RightsKey not empty
    BEGIn
        SELECt  @l_msg = @l_file + ': Table ibs_RightsKey is not empty.'
        PRINT @l_msg
        SELECT  @l_msg =
                @l_file + ': There are no entries inserted into the table.'
        PRINT @l_msg
    END -- else ibs_RightsKey not empty

    -- jump to end of code block:
    GOTO finish

exception:                              -- an error occurred
    -- log the error:
    EXEC ibs_error.logError 500, @l_file, @l_error, @l_ePos,
            'l_cnt', @l_cnt             -- integer
                                        -- varchar
                                        -- integer
                                        -- ...
    SELECt  @l_msg = @l_file + ': Error message:'
    PRINT @l_msg
    SELECT  @l_msg = 'Error ' + CONVERT (VARCHAR, @l_error) +
            '; position: ' + @l_ePos
    PRINT @l_msg

finish:
    -- show state message:
    SELECT  @l_msg = @l_file + ': finished'
    PRINT @l_msg
GO

-- show count messages again:
SET NOCOUNT OFF
GO

PRINT '$RCSFile$: finished.'
GO
