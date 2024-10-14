/******************************************************************************
 * All stored procedures regarding the user administration. <BR>
 *
 * @version     1.10.0001, 03.08.1999
 *
 * @author      Keim Christine (CK)  980727
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990803    Code cleaning.
 ******************************************************************************
 */


/******************************************************************************
 * Creates a new object (incl. rights check). <BR>
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
EXEC p_dropProc N'p_UserAdminContainer_01$create'
GO

-- create the new procedure:
CREATE PROCEDURE p_UserAdminContainer_01$create
(
    -- input parameters:
    @ai_userId          USERID,
    @ai_op              INT,
    @ai_tVersionId      TVERSIONID,
    @ai_name            NAME,
    @ai_containerId_s   OBJECTIDSTRING,
    @ai_containerKind   INT,
    @ai_isLink          BOOL,
    @ai_linkedObjectId_s OBJECTIDSTRING,
    @ai_description     DESCRIPTION,
    -- output parameters:
    @ao_oid_s           OBJECTIDSTRING OUTPUT
)
AS
    -- convertions (objectidstring) - all input objectids must be converted
DECLARE 
    @l_containerId    OBJECTID,
    @l_linkedObjectId OBJECTID

-- definitions:
-- define return constants:
DECLARE 
    @c_ALL_RIGHT INT, 
    @c_INSUFFICIENT_RIGHTS INT, 
    @c_ALREADY_EXISTS INT,
    @c_LANGUAGEID INT
-- set constants:
SELECT  
    @c_ALL_RIGHT = 1, 
    @c_INSUFFICIENT_RIGHTS = 2, 
    @c_ALREADY_EXISTS = 21,
    @c_LANGUAGEID = 0

-- define local variables:
DECLARE 
    @l_retValue INT,                   -- return value of this procedure
    @l_oid OBJECTID,
    @l_objectName NAME,
    @l_objectDescription DESCRIPTION,
    @l_dummy_s OBJECTIDSTRING, 
    @l_dummy OBJECTID

	-- initialize local variables:
SELECT  
    @l_retValue = @c_ALL_RIGHT,
    @l_oid = 0x0000000000000000


    EXEC p_stringToByte @ai_containerId_s, @l_containerId OUTPUT
    EXEC p_stringToByte @ai_linkedObjectId_s, @l_linkedObjectId OUTPUT

    -- body:
    BEGIN TRANSACTION

        -- create base object:
        EXEC @l_retValue = p_Object$performCreate @ai_userId, @ai_op, @ai_tVersionId, @ai_name, @ai_containerId_s,
                @ai_containerKind, @ai_isLink, @ai_linkedObjectId_s, @ai_description, 
                @ao_oid_s OUTPUT, @l_oid OUTPUT

	    IF (@l_retValue = @c_ALL_RIGHT)     -- object created successfully?
        BEGIN

            -- create GroupContainer:
            EXEC p_ObjectDesc_01$get @c_LANGUAGEID, N'OD_domGroups', @l_objectName OUTPUT, @l_objectDescription OUTPUT

            EXEC @l_retValue = p_Object$performCreate @ai_userId, @ai_op, 0x01013401, @l_objectName, @ao_oid_s,
                    1, @ai_isLink, @ai_linkedObjectId_s, @l_objectDescription, 
                    @l_dummy_s OUTPUT, @l_dummy OUTPUT

            -- create UserContainer:
            EXEC p_ObjectDesc_01$get @c_LANGUAGEID, N'OD_domUsers', @l_objectName OUTPUT, @l_objectDescription OUTPUT

            EXEC @l_retValue = p_Object$performCreate @ai_userId, @ai_op, 0x01013301, @l_objectName, @ao_oid_s,
                    1, @ai_isLink, @ai_linkedObjectId_s, @l_objectDescription, 
                    @l_dummy_s OUTPUT, @l_dummy OUTPUT

        END -- if object created successfully
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_UserAdminContainer_01$create
