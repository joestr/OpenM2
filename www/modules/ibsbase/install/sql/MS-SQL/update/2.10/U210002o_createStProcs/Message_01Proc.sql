/******************************************************************************
 * All stored procedures regarding the ibs_Message_01 table. <BR>
 *
 * @version     1.11.0001, 14.12.1999
 *
 * @author      Ralf Werl    (RW)  991214
 *
 * <DT><B>Updates:</B>
 *
 ******************************************************************************
 */

/******************************************************************************
 * Creates a new tupel in the table. <BR>
 * This procedure inserts a tupel into ibs_Message_01 or updates it, if it exists
 *
 * @input parameters:
 * @param   @languageId ID of the language (0 = default).
 * @param   @name       name of the Message
 * @param   @value      text of the Message
 * @param   @className  which javaclass refers to that Message
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */
-- delete existing procedure:
IF EXISTS ( SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.p_Message_01$new') 
                AND sysstat & 0xf = 4)
	DROP PROCEDURE #CONFVAR.ibsbase.dbOwner#.p_Message_01$new
GO

-- create the new procedure:
CREATE PROCEDURE p_Message_01$new
(
    -- input parameters:
    @languageId  ID,
    @name        DESCRIPTION,
    @value       NTEXT,
    @className   FILENAME
    -- output parameters
)
AS
    -- check if tupel exists. if exists then update the value, else insert tupel
    IF EXISTS (
        SELECT id
        FROM   ibs_Message_01
        WHERE  languageId   = @languageId
           AND name         = @name
           AND classname    = @className
           )
    BEGIN                               -- Message exists --> UPDATE
    
        UPDATE ibs_Message_01
        SET    value = @value
        WHERE  languageId   = @languageId
           AND name         = @name
           AND classname    = @className
           
    END -- Message exists
    ELSE
    BEGIN                               -- Message does not exist --> INSERT
    
        INSERT INTO ibs_Message_01
            (languageId, name, value, className)
        VALUES
            (@languageId, @name, @value, @className)
    
    END -- Message does not exist

GO

-- p_Message_01$new
