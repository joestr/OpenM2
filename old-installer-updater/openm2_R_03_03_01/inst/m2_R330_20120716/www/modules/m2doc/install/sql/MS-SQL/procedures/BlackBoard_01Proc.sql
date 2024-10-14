/******************************************************************************
 * All stored procedures regarding the BlackBoard_01 Object. <BR>
 * 
 * @version     $Id: BlackBoard_01Proc.sql,v 1.4 2006/01/19 15:56:45 klreimue Exp $
 *
 * @author      Keim Christine (Ck)  980504
 ******************************************************************************
 */


/******************************************************************************
 * Creates a new BlackBoard_01 Object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @userId             ID of the user who is creating the object.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 * @param   @tVersionId         Type of the new object.
 * @param   @name               Name of the object.
 * @param   @containerId_s      ID of the container where object shall be 
 *                              created in.
 * @param   @containerKind      Kind of object/container relationship
 * @param   @isLink             Defines if the object is a link
 * @param   @linkedObjectId_s   If the object is a link this is the ID of the
 *                              where the link shows to.
 * @param   @description        Description of the object.
 *
 * @output parameters:
 * @param   @oid_s              OID of the newly created object.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
-- delete existing procedure:
IF EXISTS (
            SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.p_BlackBoard_01$create') 
                AND sysstat & 0xf = 4
          )
    DROP PROCEDURE #CONFVAR.ibsbase.dbOwner#.p_BlackBoard_01$create
GO

-- create the new procedure:
CREATE PROCEDURE p_BlackBoard_01$create
(
    -- input parameters:
    @userId         USERID,
    @op             INT,
    @tVersionId     TVERSIONID,
    @name           NAME,
    @containerId_s  OBJECTIDSTRING,
    @containerKind  INT,
    @isLink         BOOL,
    @linkedObjectId_s OBJECTIDSTRING,
    @description    DESCRIPTION,
    -- output parameters:
    @oid_s          OBJECTIDSTRING OUTPUT
)
AS
    -- convertions (objectidstring) - all input objectids must be converted
    DECLARE @containerId    OBJECTID
    DECLARE @linkedObjectId OBJECTID

    EXEC p_stringToByte @containerId_s, @containerId OUTPUT
    EXEC p_stringToByte @linkedObjectId_s, @linkedObjectId OUTPUT

    -- definitions:
    -- define return constants:
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @ALREADY_EXISTS INT
    -- set constants:
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2, @ALREADY_EXISTS = 21
    -- define return values:
    DECLARE @retValue INT                   -- return value of this procedure
    -- initialize return values:
    SELECT  @retValue = @ALL_RIGHT
    -- define local variables:
    DECLARE @oid OBJECTID
	-- initialize local variables:
    SELECT  @oid = 0x0000000000000000


    -- body:
    BEGIN TRANSACTION
        -- create base object:
        EXEC @retValue = p_Object$performCreate @userId, @op, @tVersionId, @name, @containerId_s,
                @containerKind, @isLink, @linkedObjectId_s, @description, 
                @oid_s OUTPUT, @oid OUTPUT

	    IF (@retValue = @ALL_RIGHT)     -- object created successfully?
        BEGIN
	        -- insert the other values
		    INSERT INTO m2_Discussion_01 (oid, maxlevels, defaultView)
		    VALUES (@oid, 1, 1)
        END -- if object created successfully
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @retValue
GO
-- p_BlackBoard_01$create
