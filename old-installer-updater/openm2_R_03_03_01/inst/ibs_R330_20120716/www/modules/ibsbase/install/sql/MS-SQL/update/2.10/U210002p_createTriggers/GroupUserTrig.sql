/******************************************************************************
 * The triggers for the ibs groupuser table. <BR>
 *
 * @version     $Id: GroupUserTrig.sql,v 1.1 2010/02/25 13:53:48 btatzmann Exp $
 *
 * @author      Keim Christine (CK)  980715
 ******************************************************************************
 */

/******************************************************************************
 * insert trigger
 */
-- delete existing trigger:
EXEC p_dropTrig N'TrigGroupUserInsert'
GO

-- create the trigger:
CREATE TRIGGER TrigGroupUserInsert ON ibs_GroupUser
FOR INSERT
AS 
DECLARE
    -- constants:
    @c_MINID                INT,            -- minimum id

    -- local variables:
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_oldId                ID,             -- the original id of the tab
    @l_id                   ID,             -- id of the tab
    @l_groupId              GROUPID,        -- id of current group
    @l_userId               USERID,         -- id of current user
    @l_origGroupId          GROUPID         -- id of original group

    -- assign constants:
SELECT
    @c_MINID                = 0x01A00001

    -- initialize local variables:
SELECT
    @l_id = 0

-- body:
    -- define cursor:
    -- get all entries which don't have an own id.
    DECLARE trigInsertCursor CURSOR FOR
        SELECT  id, groupId, userId, origGroupId
        FROM    inserted
        WHERE   id <= 0

    -- open the cursor:
    OPEN    trigInsertCursor

    -- get the first tuple:
    FETCH NEXT FROM trigInsertCursor
        INTO @l_oldId, @l_groupId, @l_userId, @l_origGroupId

    -- loop through all tuples:
    WHILE (@@FETCH_STATUS <> -1)        -- another tuple found?
    BEGIN
        -- Because @@FETCH_STATUS may have one of the three values
        -- -2, -1, or 0 all of these cases must be checked.
        -- In this case the tuple is skipped if it was deleted
        -- during the execution of this procedure.
        IF (@@FETCH_STATUS <> -2)
        BEGIN
            -- compute new id:
            SELECT  @l_id = COALESCE (MAX (id) + 1, 0x01A00001)
            FROM    ibs_GroupUser
            WHERE   id >= 0
                AND id <> @l_oldId

            -- check if there occurred an error:
            EXEC @l_error = ibs_error.prepareError @@error,
                N'compute id', @l_ePos OUTPUT
            IF (@l_error <> 0) -- an error occurred?
                GOTO CursorException -- call exception handler

            -- store the new values:
            UPDATE  ibs_GroupUser
            SET     id = @l_id
            WHERE   id = @l_oldId
                AND groupId = @l_groupId
                AND userId = @l_userId
                AND origGroupId = @l_origGroupId

            -- check if there occurred an error:
            EXEC @l_error = ibs_error.prepareError @@error,
                N'update id', @l_ePos OUTPUT
            IF (@l_error <> 0) -- an error occurred?
                GOTO CursorException -- call exception handler
        END -- if

        -- get next tuple:
        FETCH NEXT FROM trigInsertCursor
            INTO @l_oldId, @l_groupId, @l_userId, @l_origGroupId
    END -- while another tuple found

    -- close the not longer needed cursor:
    CLOSE trigInsertCursor
    DEALLOCATE trigInsertCursor

    -- terminate the trigger:
    RETURN

cursorException:                        -- an error occurred within cursor
    -- close the not longer needed cursor:
    CLOSE trigInsertCursor
    DEALLOCATE trigInsertCursor
exception:                              -- an error occurred
    -- log the error:
    EXEC ibs_error.logError 500, N'TrigGroupUserInsert', @l_error, @l_ePos,
            N'l_oldId', @l_oldId,
            N'', N'',
            N'l_id', @l_id,
            N'', N'',
            N'l_groupId', @l_groupId,
            N'', N'',
            N'l_userId', @l_userId,
            N'', N'',
            N'l_origGroupId', @l_origGroupId
GO
-- TrigGroupUserInsert


PRINT 'Trigger for table ibs_GroupUser created.'
GO
