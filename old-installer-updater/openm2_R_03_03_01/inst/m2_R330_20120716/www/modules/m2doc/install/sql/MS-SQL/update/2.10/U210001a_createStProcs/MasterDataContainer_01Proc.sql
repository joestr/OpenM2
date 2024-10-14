 /******************************************************************************
 * All stored procedures regarding the MasterDataContainer_01 Object. <BR>
 * 
 * @version     $Id: MasterDataContainer_01Proc.sql,v 1.1 2010/02/25 13:54:18 btatzmann Exp $
 *
 * @author      Keim Christine (CK)  990429
 ******************************************************************************
 */

 
 /******************************************************************************
 * Gets the oid of a Container from which the name and the containerName 
 * are given (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @userId             ID of the user who is creating the object.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 * @param   @containerName   Name of the Container where the seached one is in
 * @param   @name                 Name of the Container that is searched
 *
 * @output parameters:
 * @param   @oid        OID of the container
 *
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- delete existing procedure:
IF EXISTS ( SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.p_MasterDataContainer_01$gOid') 
                AND sysstat & 0xf = 4)
	DROP PROCEDURE #CONFVAR.ibsbase.dbOwner#.p_MasterDataContainer_01$gOid
GO

-- create the  new procedure:
CREATE PROCEDURE p_MasterDataContainer_01$gOid
(
    -- input parameters:
    @userId         USERID,
    @op             INT,
    @containerName      NAME,
    @name      NAME,
    -- output parameters
    @oid    OBJECTID        OUTPUT
)

AS
    -- definitions:
    -- define return constants:
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- set constants:
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2, @OBJECTNOTFOUND = 3
    -- define return values:
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values:
    SELECT  @retValue = @ALL_RIGHT
    -- define local variables:

    -- body:
    BEGIN TRANSACTION
        -- retrieve the base object data:
        
            SELECT @oid = v.oid
	        FROM v_Container$content v JOIN ibs_Object o ON v.containerId = o.oid
	        WHERE v.userId = @userId
	        AND v.tVersionId = 16853249 -- tVersionId of MasterDataContainer_01
	        AND (rights & @op) = @op
	        AND o.name = @containerName
	        AND v.name = @name

            -- check if insert was performed correctly:
            IF (@@ROWCOUNT <= 0)                -- none found?
                SELECT  @retValue = @OBJECTNOTFOUND  -- set return value
                
    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO
-- p_MasterDataContainer_01$gOid
