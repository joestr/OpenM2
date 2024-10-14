/******************************************************************************
 * All stored procedures regarding the ibs_Exception_01 table. <BR>
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
 * This procedure inserts a tupel into ibs_Exception_01 or updates it, if it exists
 *
 * @input parameters:
 * @param   @languageId ID of the language (0 = default).
 * @param   @name       name of the Exception
 * @param   @value      text of the Exception
 * @param   @className  which javaclass refers to that Exception
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */
-- delete existing procedure:
IF EXISTS ( SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.p_Exception_01$new') 
                AND sysstat & 0xf = 4)
	DROP PROCEDURE #CONFVAR.ibsbase.dbOwner#.p_Exception_01$new
GO

-- create the new procedure:
CREATE PROCEDURE p_Exception_01$new
(
    -- input parameters:
    @languageId  ID,
    @name        DESCRIPTION,
    @value       DESCRIPTION,
    @className   FILENAME
    -- output parameters
)
AS
    -- declare local variables
    DECLARE @newValue DESCRIPTION       -- new value with '~' instead of trailing blanks
    
    -- trailing blanks will be changed into '~', 
    -- because MS-SQL-Server cuts all trailing blanks
    SELECT @newValue = RTRIM (@value)
    SELECT @newValue = @newValue + REPLICATE ('~', DATALENGTH (@value) - DATALENGTH (@newValue))

    -- check if tupel exists. if exists then update the value, else insert tupel
    IF EXISTS (
        SELECT id
        FROM   ibs_Exception_01
        WHERE  languageId   = @languageId
           AND name         = @name
           AND classname    = @className
           )
    BEGIN                               -- Exception exists --> UPDATE
    
        UPDATE ibs_Exception_01
        SET    value = @newValue
        WHERE  languageId   = @languageId
           AND name         = @name
           AND classname    = @className
           
    END -- Exception exists
    ELSE
    BEGIN                               -- Exception does not exist --> INSERT
    
        INSERT INTO ibs_Exception_01
            (languageId, name, value, className)
        VALUES
            (@languageId, @name, @newValue, @className)
    
    END -- Exception does not exist

GO

-- p_Exception_01$new
