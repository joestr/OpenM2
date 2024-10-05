/******************************************************************************
 * All procedures regarding a log container. <BR>
 *
 * @version     1.10.0001, 03.08.1999
 *
 * @author      ??? (??)  990311
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
IF EXISTS (
            SELECT  *
            FROM    sysobjects
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.p_LogContainer_01$create')
                AND sysstat & 0xf = 4
          )
    DROP PROCEDURE #CONFVAR.ibsbase.dbOwner#.p_LogContainer_01$create
GO

-- create the new procedure:
CREATE PROCEDURE p_LogContainer_01$create
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
    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2, -- return values
            @OBJECTNOTFOUND = 3
    -- define return values
    DECLARE @retValue INT                   -- return value of this procedure
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT

    SELECT  @name = N'Protokoll'

    BEGIN TRANSACTION

		EXEC @retValue = p_Object$performCreate @userId, @op, @tVersionId, @name, @containerId_s,
		        @containerKind, @isLink, @linkedObjectId_s, @description, 
		        @oid_s OUTPUT

/*
    IF (@retValue = @ALL_RIGHT)         -- no error occurred?
    BEGIN
        
    END -- if no error occurred
*/

    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO
-- p_LogContainer_01$create


/******************************************************************************
 * Clean the content of the ibs_protocol_01 table (incl. rights check). <BR>
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
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.p_LogContainer_01$clean')
                AND sysstat & 0xf = 4
          )
    DROP PROCEDURE #CONFVAR.ibsbase.dbOwner#.p_LogContainer_01$clean
GO

-- create the new procedure:
CREATE PROCEDURE p_LogContainer_01$clean
(
    -- input parameters:
    @userId         USERID,
    @op             INT

)
AS

    DECLARE @name NAME
    DECLARE @retValue INT

    SELECT @retvalue = 1

    BEGIN TRANSACTION

        DELETE ibs_protocol_01

    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO
-- p_LogContainer_01$clean
