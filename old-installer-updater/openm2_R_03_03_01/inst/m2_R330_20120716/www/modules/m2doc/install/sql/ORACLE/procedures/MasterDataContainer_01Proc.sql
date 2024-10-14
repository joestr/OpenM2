 /******************************************************************************
 * All stored procedures regarding the MasterDataContainer_01 Object. <BR>
 * 
 * @version     $Id: MasterDataContainer_01Proc.sql,v 1.5 2003/10/31 00:13:14 klaus Exp $
 *
 * @author      Mario Stegbauer (MS)  980805
 ******************************************************************************
 */

 
 /******************************************************************************
 * Gets the oid of a Container from which the name and the containerName 
 * are given (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @userId                ID of the user who is creating the object.
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
CREATE OR REPLACE FUNCTION p_MasterDataContainer_01$gOid
( 
     -- input parameters
     userId     INTEGER,
     op         INTEGER,
     containerName VARCHAR2,
     name          VARCHAR2,
     -- output parameters
     oid       OUT RAW
)
RETURN INTEGER
AS
    -- definitions:
    ALL_RIGHT   INTEGER := 1;
    OBJECTNOTFOUND INTEGER := 3;
    retValue    INTEGER := p_MasterDataContainer_01$gOid.ALL_RIGHT;
    -- exception values:
    StoO_error INTEGER;
    StoO_errmsg VARCHAR2(255);
BEGIN

    SELECT v.oid
    INTO p_MasterDataContainer_01$gOid.oid
    FROM v_Container$content v, ibs_Object o
    WHERE o.oid = v.containerId
    AND v.userId = p_MasterDataContainer_01$gOid.userId
    AND v.tVersionId = 16853249
    AND B_AND (rights, p_MasterDataContainer_01$gOid.op) = p_MasterDataContainer_01$gOid.op
    AND o.name = p_MasterDataContainer_01$gOid.containerName
    AND v.name = p_MasterDataContainer_01$gOid.name;

    IF (SQL%ROWCOUNT <= 0) -- none found?
    THEN
       p_MasterDataContainer_01$gOid.retValue := p_MasterDataContainer_01$gOid.OBJECTNOTFOUND;
    END IF;

COMMIT WORK;
-- return the state value
RETURN p_MasterDataContainer_01$gOid.retValue;

EXCEPTION
  WHEN OTHERS THEN
    StoO_error := SQLCODE;
    StoO_errmsg := SQLERRM;

END p_MasterDataContainer_01$gOid;
/

show errors;

EXIT;