/******************************************************************************
 * All stored procedures regarding a container. <BR>
 * 
 * @version     $Id: ContainerProc.sql,v 1.1 2010/02/25 13:53:47 btatzmann Exp $
 *
 * @author      Klaus Reimüller (KR)  980507
 ******************************************************************************
 */


/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights check). 
 * <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be changed.
 * @param   @userId             ID of the user who is creating the object.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 * @param   @orderBy            Attribute to order by.
 * @param   @orderHow           Type of ordering (ASCending or DESCending).
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- delete existing procedure:
IF EXISTS ( SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.p_Container$retrieveContent') 
                AND sysstat & 0xf = 4)
	DROP PROCEDURE #CONFVAR.ibsbase.dbOwner#.p_Container$retrieveContent
GO

-- create the new procedure:
CREATE PROCEDURE p_Container$retrieveContent
(
    -- input parameters:
    @oid_s          OBJECTIDSTRING,
    @userId_s       NVARCHAR (255),
    @op_s           NVARCHAR (255),
    @orderBy        NVARCHAR (255),
    @orderHow       NVARCHAR (255)
    -- output parameters
)
AS
    EXEC (
        'SELECT  uid, isNew, rights, * ' +
        'FROM    v_Container$content ' +
        'WHERE   uid = ' + @userId_s +
        '    AND containerId = ' + @oid_s +
        '    AND (rights & ' + @op_s + ') > 0 ' +
        'ORDER BY ' + @orderBy + ' ' + @orderHow
    )
GO
-- p_Container$retrieveContent



/******************************************************************************
 * Deletes an object and all its values (incl. rights check). <BR>
 * This procedure also delets all links showing to this object.
 * 
 * @input parameters:
 * @param   @oid_s              ID of the object to be deleted.
 * @param   @userId             ID of the user who is deleting the object.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
IF EXISTS ( SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.p_Container$delete') 
                AND sysstat & 0xf = 4)
	DROP PROCEDURE #CONFVAR.ibsbase.dbOwner#.p_Container$delete
GO

CREATE PROCEDURE p_Container$delete
(
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT
)
AS
    ---------------------------------------------------------------------------
    -- CONVERTIONS (OBJECTIDSTRING) - all input objectids must be converted
    DECLARE @oid OBJECTID
    EXEC    p_stringToByte @oid_s, @oid OUTPUT

    -- definitions:
    -- define return constants
    DECLARE @INSUFFICIENT_RIGHTS INT, @ALL_RIGHT INT, @OBJECTNOTFOUND INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,   -- return values
            @OBJECTNOTFOUND = 3
    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT

    BEGIN TRANSACTION

    -- perform deletion of object:
    EXEC @retValue = p_Object$performDelete @oid_s, @userId, @op

/*
    IF (@retValue = @ALL_RIGHT)         -- operation properly performed?
    BEGIN
        ...
    END -- if operation properly performed
*/

    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO
-- p_Container$delete
