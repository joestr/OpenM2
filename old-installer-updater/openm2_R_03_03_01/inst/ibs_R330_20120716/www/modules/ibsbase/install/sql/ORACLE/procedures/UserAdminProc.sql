/******************************************************************************
 * All stored procedures regarding the user administration. <BR>
 *
 * @version     1.10.0001, 05.08.1999
 *
 * @author      Mario Stegbauer (MS)  980805
 *
 * <DT><B>Updates:</B>
 * <DD>HB 990805    Code cleaning.
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
CREATE OR REPLACE FUNCTION p_UserAdminContainer_01$create
(
    -- input parameters:
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_tVersionId           INTEGER,
    ai_name                 VARCHAR2,
    ai_containerId_s        VARCHAR2,
    ai_containerKind        INTEGER,
    ai_isLink               NUMBER,
    ai_linkedObjectId_s     VARCHAR2,
    ai_description          VARCHAR2,
    -- output parameters:
    ao_oid_s                OUT VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2;
    c_ALREADY_EXISTS        CONSTANT INTEGER := 21;
    c_LANGUAGEID            CONSTANT INTEGER := 0;

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_linkedObjectId        RAW (8);
    l_containerId           RAW (8);
    l_oid                   RAW (8) := c_NOOID;
    l_objectName            VARCHAR2 (63);
    l_objectDescription     VARCHAR2 (255);
    l_dummy_s               VARCHAR2 (18);
    l_dummy                 RAW (8);
    containerId         RAW (8);
BEGIN
    -- conversions (objectidstring) - all input objectids must be converted
    p_stringToByte (ai_containerId_s, l_containerId);
    p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);
    
    -- create base object:
    l_retValue := p_Object$performCreate (
        ai_userId, ai_op, ai_tVersionId, ai_name, 
        ai_containerId_s, ai_containerKind, 
        ai_isLink, ai_linkedObjectId_s, ai_description, 
        ao_oid_s, l_oid);

    IF (l_retValue = c_ALL_RIGHT)       -- object created successfully?
    THEN
        -- create GroupContainer (0x01013401):
        p_ObjectDesc_01$get (c_LANGUAGEID, 'OD_domGroups', l_objectName, l_objectDescription);
        l_retValue := p_Object$performCreate (
            ai_userId, ai_op, 16856065, l_objectName, ao_oid_s,
            1, ai_isLink, ai_linkedObjectId_s, l_objectDescription, 
            l_dummy_s, l_dummy);

        -- create UserContainer (0x01013301):
        p_ObjectDesc_01$get (c_LANGUAGEID, 'OD_domUsers', l_objectName, l_objectDescription);
        l_retValue := p_Object$performCreate (
            ai_userId, ai_op, 16855809, l_objectName, ao_oid_s,
            1, ai_isLink, ai_linkedObjectId_s, l_objectDescription,
            l_dummy_s, l_dummy);
    END IF; -- if object created successfully

    COMMIT WORK;

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_UserAdminContainer_01$create',
            'Input: ai_userId: ' || ai_userId ||
            ', ai_op: ' || ai_op ||
            ', ai_tVersionId: ' || ai_tVersionId ||
            ', ai_name: ' || ai_name ||
            ', ai_containerId_s: ' || ai_containerId_s ||
            ', ai_containerKind: ' || ai_containerKind ||
            ', ai_isLink: ' || ai_isLink ||
            ', ai_linkedObjectId_s: ' || ai_linkedObjectId_s ||
            ', ai_description: ' || ai_description ||
            ', sqlcode: ' || SQLCODE ||
            ', sqlerrm: ' || SQLERRM );
    -- return error code
    return c_NOT_OK;
END p_UserAdminContainer_01$create;
/

show errors;

exit;