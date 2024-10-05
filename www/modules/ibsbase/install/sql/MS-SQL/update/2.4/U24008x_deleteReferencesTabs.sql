/******************************************************************************
 * Task:        TASK CRE4 - Optimization
 *
 * Description: Drop all references tabs of specific object types.
 *
 * Repeatable:  yes
 *
 * @version     $Id: U24008x_deleteReferencesTabs.sql,v 1.1 2005/03/04 22:05:44 klaus Exp $
 *
 * @author      Klaus Reimüller (KR) 20050303
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

    -- local variables:
    @l_file                 VARCHAR (7),    -- name of actual file
    @l_retValue             INT,            -- return value of function
    @l_error                INT,            -- the actual error code
    @l_ePos                 VARCHAR (255),  -- error position description
    @l_msg                  VARCHAR (2000), -- output message
    @l_tVersionId           TVERSIONID      -- id of the actual type version

-- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1

-- initialize local variables:
SELECT
    @l_file                 = 'U24008x',
    @l_retValue             = @c_ALL_RIGHT,
    @l_error = 0

-- body:

    ---------------------------------------------------------------------------
    -- Drop the reference tabs of some specific object types.
    -- These tabs are never used and thus are not necessary.
    DECLARE tabCursor INSENSITIVE CURSOR FOR
        SELECT  tv.id
        FROM    ibs_TVersion tv, ibs_Type t
        WHERE   tv.typeId = t.id
            AND t.code IN ('SentObject', 'ReceivedObject', 'Recipient')

    BEGIN
        -- insert the target and the container of the copied object
        OPEN tabCursor
        --
        -- get first object to be copied:
        FETCH NEXT FROM tabCursor INTO @l_tVersionId
        --
        WHILE (@@FETCH_STATUS = 0 AND @l_retValue = @c_ALL_RIGHT)
        BEGIN
            -- delete the references tab:
            EXEC @l_retValue =
                p_ConsistsOf$delete @l_tVersionId, 'References', 0

            IF (@l_retValue = @c_ALL_RIGHT)
            BEGIN
                -- get next object to be copied:
                FETCH NEXT FROM tabCursor INTO @l_tVersionId
            END -- if
        END -- while

        -- dump cursor structures
        CLOSE tabCursor
        DEALLOCATE tabCursor
    END

/* ignore the error. this does not matter
    IF (@l_retValue <> @c_ALL_RIGHT)
    BEGIN
        SELECT  @l_error = 500,
                @l_ePos = 'Error when dropping References tabs.'
        GOTO exception
    END -- if
*/
    SELECT @l_retValue = @c_ALL_RIGHT

    SELECT  @l_msg = @l_file + ': Reference tabs dropped for object types' +
            ' ''SentObject'', ''ReceivedObject'', ''Recipient'''
    PRINT @l_msg

    ---------------------------------------------------------------------------
    -- Delete all tab objects which alread exist for these object types.

    UPDATE  ibs_Object
    SET     state = 1
    WHERE   containerId IN
            (
                SELECT  o.oid
                FROM    ibs_Object o, ibs_TVersion tv, ibs_Type t
                WHERE   o.tVersionId = tv.id
                    AND tv.typeId = t.id
                    AND t.code IN ('SentObject', 'ReceivedObject', 'Recipient')
            )
        AND containerKind = 2
        AND tVersionId IN
            (
                SELECT  tv.id
                FROM    ibs_TVersion tv, ibs_Type t
                WHERE   tv.typeId = t.id
                    AND t.code = 'ReferenzContainer'
            )
        AND state <> 1

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareError @@error,
        'delete reference tab objects', @l_ePos OUTPUT
    IF (@l_error <> 0)                  -- an error occurred?
        GOTO exception                  -- call common exception handler

    SELECT  @l_msg = @l_file + ': Already existing reference tabs deleted.'
    PRINT @l_msg

    -- jump to end of code block:
    GOTO finish

exception:                              -- an error occurred
    -- log the error:
    EXEC ibs_error.logError 500, @l_file, @l_error, @l_ePos,
        'l_tVersionId', @l_tVersionId,
        '', '',
        'l_retValue', @l_retValue
    SELECT  @l_msg = @l_file + ': Error when deleting references tabs:'
    PRINT @l_msg
    SELECT  @l_msg = 'Error ' + CONVERT (VARCHAR, @l_error) +
            '; position: ' + @l_ePos
    PRINT @l_msg

finish:
    -- show state message:
    SELECT  @l_msg = @l_file + ': finished.'
    PRINT @l_msg
GO

-- show count messages again:
SET NOCOUNT OFF
GO

PRINT '$RCSFile$: finished.'
GO
