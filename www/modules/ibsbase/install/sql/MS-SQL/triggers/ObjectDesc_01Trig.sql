/******************************************************************************
 * The triggers for the ibs_ObjectDesc_01 table. <BR>
 *
 * @version     2.00.0001, 29.02.2000
 *
 * @author      Klaus Reimüller (KR)  000229
 ******************************************************************************
 */

/******************************************************************************
 * insert trigger
 */
-- delete existing trigger:
EXEC p_dropTrig N'TrigObjectDescInsert'
GO

-- create the trigger:
CREATE TRIGGER TrigObjectDescInsert ON ibs_ObjectDesc_01
    FOR INSERT
AS 
-- declare variables:
DECLARE
    @l_id                   INT,            -- the id of the object
    @l_oldId                INT             -- the id which was originally set

-- body:
    -- get the inserted id:
    SELECT  @l_oldId = id, @l_id = id
    FROM    inserted

    IF (@l_oldId <= 0)                  -- id not set?
    BEGIN
        -- compute new id:
        SELECT  @l_id = COALESCE (MAX (id) + 1, 1)
        FROM    ibs_ObjectDesc_01

        -- set new id:
        UPDATE  ibs_ObjectDesc_01
        SET     id = @l_id
        WHERE   id = @l_oldId
    END -- if id not set
GO
-- TrigObjectDescInsert
