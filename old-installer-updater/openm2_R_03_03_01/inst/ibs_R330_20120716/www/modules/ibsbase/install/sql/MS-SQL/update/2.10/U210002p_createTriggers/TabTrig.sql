/******************************************************************************
 * The triggers for the ibs tab table. <BR>
 *
 * @version     2.10.0001, 25.01.2001
 *
 * @author      Klaus Reimüller (KR)  010125
 ******************************************************************************
 */

/******************************************************************************
 * INSERT trigger for ibs_Tab
 */
-- delete existing trigger:
EXEC p_dropTrig N'TrigTabInsert'
GO

-- create the trigger
CREATE TRIGGER TrigTabInsert ON ibs_Tab
FOR INSERT
AS 
DECLARE
    -- constants:

    -- local variables:
    @l_id                   ID,             -- id of the tab
    @l_multilangKey         NAME            -- key in multilanguage table

    -- assign constants:

    -- initialize local variables:
SELECT
    @l_id = 0

-- body:
    -- get next sequence number:
    -- get actual id and multilangKey:
    SELECT  @l_id = id, @l_multilangKey = multilangKey
    FROM    inserted

    IF (@l_id = 0)                          -- no id defined?
    BEGIN
        -- generate a new id:
        SELECT  @l_id = COALESCE (MAX (id) + 1, 1)
        FROM    ibs_Tab

        -- set the id (if <= 0):
        UPDATE  ibs_Tab
        SET     id = @l_id
        WHERE   id IN ( SELECT  id
                        FROM    inserted
                        WHERE   id <= 0)
    END -- if no id defined

    -- check if multilanguage key is defined:
    IF (@l_multilangKey IS NULL OR @l_multilangKey = '')
                                            -- no multilanguage key defined?
    BEGIN
        -- compute and set the new key:
        UPDATE  ibs_Tab
        SET     multilangKey = N'TAB_' + CONVERT (VARCHAR (10), @l_id)
        WHERE   id = @l_id
    END -- if no multilanguage key defined        
GO
-- TrigTabInsert


PRINT 'Created triggers for table ibs_Tab'
GO
