/******************************************************************************
 * The triggers for the ibs consistsOf table. <BR>
 *
 * @version     $Id: ConsistsOfTrig.sql,v 1.1 2010/02/25 13:53:48 btatzmann Exp $
 *
 * @author      Klaus Reimüller (KR)  980715
 ******************************************************************************
 */

/******************************************************************************
 * INSERT trigger for ibs_ConsistsOf
 */
-- delete existing trigger:
EXEC p_dropTrig N'TrigConsistsOfInsert'
GO

-- create the trigger:
CREATE TRIGGER TrigConsistsOfInsert ON ibs_ConsistsOf
FOR INSERT
AS 
DECLARE
    -- constants:
    @c_NOOID                OBJECTID,       -- default value for no defined oid

    -- local variables:
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_oldId                ID,             -- the original id of the tab
    @l_id                   ID,             -- id of the tab
    @l_tVersionId           TVERSIONID,     -- id of type version
    @l_tabId                ID,             -- id of the tab
    @l_oid                  OBJECTID,       -- oid of tab
    @l_tabTVersionId        TVERSIONID      -- tVersionId of the tab

    -- assign constants:
SELECT
    @c_NOOID                = 0x0000000000000000

    -- initialize local variables:
SELECT
    @l_id = 0,
    @l_tabTVersionId = 0x01012311

-- body:
    -- define cursor:
    -- get all entries which don't have an own id.
    DECLARE trigInsertCursor CURSOR FOR
        SELECT  id, oid, tVersionId, tabId
        FROM    inserted
        WHERE   id <= 0

    -- open the cursor:
    OPEN    trigInsertCursor

    -- get the first tuple:
    FETCH NEXT FROM trigInsertCursor
        INTO @l_oldId, @l_oid, @l_tVersionId, @l_tabId

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
            SELECT  @l_id = COALESCE (MAX (id) + 1, 1)
            FROM    ibs_ConsistsOf
            WHERE   id <> @l_oldId

            -- check if there occurred an error:
            EXEC @l_error = ibs_error.prepareError @@error,
                N'compute id', @l_ePos OUTPUT
            IF (@l_error <> 0) -- an error occurred?
                GOTO CursorException -- call exception handler

            -- check if there is a valid oid set:
            IF (@l_oid = @c_NOOID)      -- no valid oid?
            BEGIN
                -- compute a new oid:
                SELECT  @l_oid = CONVERT (BINARY (4), @l_tabTVersionId) +
                            CONVERT (BINARY (4), @l_id)
            END -- if no valid oid

            -- store the new values:
            UPDATE  ibs_ConsistsOf
            SET     id = @l_id,
                    oid = @l_oid
            WHERE   id = @l_oldId
                AND tVersionId = @l_tVersionId
                AND tabId = @l_tabId

            -- check if there occurred an error:
            EXEC @l_error = ibs_error.prepareError @@error,
                N'update id', @l_ePos OUTPUT
            IF (@l_error <> 0) -- an error occurred?
                GOTO CursorException -- call exception handler
        END -- if

        -- get next tuple:
        FETCH NEXT FROM trigInsertCursor
            INTO @l_oldId, @l_oid, @l_tVersionId, @l_tabId
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
    EXEC ibs_error.logError 500, N'TrigConsistsOfInsert', @l_error, @l_ePos,
            N'l_oldId', @l_oldId,
            N'', N'',
            N'l_id', @l_id,
            N'', N'',
            N'l_tVersionId', @l_tVersionId,
            N'', N'',
            N'l_tabId', @l_tabId
GO
-- TrigConsistsOfInsert



/******************************************************************************
 * update trigger
 */
-- delete existing trigger:
EXEC p_dropTrig N'TrigConsistsOfUpdate'
GO


/******************************************************************************
 * delete trigger
 */
-- delete existing trigger:
EXEC p_dropTrig N'TrigConsistsOfDelete'
GO


PRINT 'Triggers for table ibs_ConsistsOf created.'
GO
