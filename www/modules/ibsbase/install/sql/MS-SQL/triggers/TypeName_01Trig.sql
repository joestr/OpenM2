/******************************************************************************
 * The triggers for the ibs_TypeName_01 table. <BR>
 *
 * @version     1.11.0001, 14.12.1999
 *
 * @author      Harald Buzzi    (HB)  991214
 *
 * <DT><B>Updates:</B>
 ******************************************************************************
 */

/******************************************************************************
 * insert trigger
 */
-- delete existing trigger:
EXEC p_dropTrig N'TrigTypeNameInsert'
GO

-- create the trigger:
CREATE TRIGGER TrigTypeNameInsert ON ibs_TypeName_01
FOR INSERT
AS 

    -- declare variables:
    DECLARE @id INT, @oldId INT

        -- get the inserted id:
        SELECT  @oldId = id, @id = id
        FROM    inserted

        IF (@oldId <= 0)                    -- id not set?
        BEGIN
            -- compute new id:
            SELECT  @id = COALESCE (MAX (id) + 1, 1)
            FROM    ibs_TypeName_01
        END -- if id not set

        -- set new attributes:
        UPDATE  ibs_TypeName_01
        SET     id = @id
        WHERE   id = @oldId

GO
-- TrigTypeNameInsert
