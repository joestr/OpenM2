/******************************************************************************
 * All stored procedures regarding the DiscussionContainer_01 Object. <BR>
 *
 * @version     $Id: DiscussionContainer_01Proc.sql,v 1.5 2004/01/16 00:44:39 klaus Exp $
 *
 * @author      Keim Christine (CK)  001002
 ******************************************************************************
 */


/******************************************************************************
 * Creates a new DiscussionContainer_01 Object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_userId             ID of the user who is creating the object.
 * @param   ai_op                 Operation to be performed (used for rights 
 *                              check).
 * @param   ai_tVersionId         Type of the new object.
 * @param   ai_name               Name of the object.
 * @param   ai_containerId_s      ID of the container where object shall be 
 *                              created in.
 * @param   ai_containerKind      Kind of object/container relationship
 * @param   ai_isLink             Defines if the object is a link
 * @param   ai_linkedObjectId_s   If the object is a link this is the ID of the
 *                              where the link shows to.
 * @param   ai_description        Description of the object.
 *
 * @output parameters:
 * @param   ai_oid_s              OID of the newly created object.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */

-- create the new procedure:
CREATE OR REPLACE FUNCTION p_DiscContainer_01$create
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
    ai_oid_s                OUT VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- operation not o.k.
    c_ALL_RIGHT             CONSTANT INTEGER := 1; 
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_oid                   RAW (8) := c_NOOID;
    l_tabTVersionId         INTEGER;
    l_tabName               VARCHAR2 (63);
    l_tabDescription        VARCHAR2 (255);
    l_partOfOid_s           VARCHAR2 (18);
    l_count                 INTEGER := 0;

    -- body:
BEGIN
    -- create base object:
    l_retValue := p_Object$performCreate (ai_userId, ai_op, ai_tVersionId, 
                    ai_name, ai_containerId_s, ai_containerKind, ai_isLink,
                    ai_linkedObjectId_s, ai_description, ai_oid_s, l_oid);

    COMMIT WORK;
    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 
            'p_DiscContainer_01$create',
            'Input: ai_userId = ' || ai_userId || 
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;

END p_DiscContainer_01$create;
/

show errors;

EXIT;
-- p_DiscContainer_01$create

