/******************************************************************************
 * All stored procedures regarding the XMLDiscussion_01 Object. <BR>
 *
 * @version     $Id: XMLDiscussion_01Proc.sql,v 1.4 2003/10/31 00:13:16 klaus Exp $
 *
 * @author      Keim Christine (Ck)  001009
 ******************************************************************************
 */

/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_oid_s              ID of the object to be changed.
 * @param   ai_userId             ID of the user who is creating the object.
 * @param   ai_op                 Operation to be performed (used for rights 
 *                              check).
 * @param   ai_name               Name of the object.
 * @param   ai_validUntil         Date until which the object is valid.
 * @param   ai_description        Description of the object.
 * @param   ai_showInNews         show in news flag.
 * @param   ai_refOid_s           reference-oid of the template used.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */

-- create the new procedure:
CREATE OR REPLACE FUNCTION p_XMLDiscussion_01$change
(
    -- input parameters:
    ai_oid_s          VARCHAR2,
    ai_userId         INTEGER,
    ai_op             INTEGER,
    ai_name           VARCHAR2,
    ai_validUntil     DATE,
    ai_description    VARCHAR2,
    ai_showInNews     NUMBER,
    ai_maxlevels      INTEGER,
    ai_defaultView    INTEGER,
    ai_refOid_s       VARCHAR2
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
    l_refOid                RAW (8) := c_NOOID;


    -- body:
BEGIN
    -- converts
    p_stringToByte (ai_oid_s, l_oid);
    p_stringToByte (ai_refOid_s, l_refOid);
    
    -- perform the change of the object:
    l_retValue := p_Discussion_01$change (ai_oid_s, ai_userId, ai_op, ai_name,
                    ai_validUntil, ai_description, ai_showInNews, ai_maxlevels,
                    ai_defaultView);

    IF (l_retValue = c_ALL_RIGHT)           -- operation properly performed?
    THEN
    BEGIN
        BEGIN
            -- update the other values
            UPDATE  m2_Discussion_01
            SET     refOid = l_refOid
            WHERE   oid = l_oid;
        EXCEPTION
            WHEN OTHERS THEN
               ibs_error.log_error ( ibs_error.error, 
                                     'p_XMLDiscussion_01$change',
                                     'Error in UPDATE');
        END;
    END;
    END IF; -- if operation properly performed
    COMMIT WORK;
    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 
            'p_XMLDiscussion_01$change',
            'Input: ai_userId = ' || ai_userId || 
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;

END p_XMLDiscussion_01$change;
/

show errors;
-- p_XMLDiscussion_01$change


/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_oid_s              ID of the object to be changed.
 * @param   ai_userId             ID of the user who is creating the object.
 * @param   ai_op                 Operation to be performed (used for rights 
 *                              check).
 *
 * @output parameters:
 * @param   ai_state              The object's state.
 * @param   ai_tVersionId         ID of the object's type (correct version).
 * @param   ai_typeName           Name of the object's type.
 * @param   ai_name               Name of the object itself.
 * @param   ai_containerId        ID of the object's container.
 * @param   ai_containerKind      Kind of object/container relationship.
 * @param   ai_isLink             Is the object a link?
 * @param   ai_linkedObjectId     Link if isLink is true.
 * @param   ai_owner              ID of the owner of the object.
 * @param   ai_creationDate       Date when the object was created.
 * @param   ai_creator            ID of person who created the object.
 * @param   ai_lastChanged        Date of the last change of the object.
 * @param   ai_changer            ID of person who did the last change to the 
 *                              object.
 * @param   ai_validUntil         Date until which the object is valid.
 * @param   ai_description        Description of the object.
 * @param   ai_showInNews         show in news flag.
 * @param   ai_checkedOut         Is the object checked out?
 * @param   ai_checkOutDate       Date when the object was checked out
 * @param   ai_checkOutUser       id of the user which checked out the object
 * @param   ai_checkOutUserOid    Oid of the user which checked out the object
 *                              is only set if this user has the right to READ
 *                              the checkOut user
 * @param   ai_checkOutUserName   name of the user which checked out the object,
 *                              is only set if this user has the right to view
 *                              the checkOut-User
 *
 * @param   ai_maxlevels          Maximum of the levels allowed in the discussion
 * @param   ai_defaultView        is always Standardview
 * @param   l_refOid             oid of the template used from the discussion
 * @param   ai_refName            Name of the template used from the discussion
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */

    -- create the new procedure:
CREATE OR REPLACE FUNCTION p_XMLDiscussion_01$retrieve
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    -- parameters
    ao_state                OUT INTEGER,
    ao_tVersionId           OUT INTEGER,
    ao_typeName             OUT VARCHAR2,
    ao_name                 OUT VARCHAR2,
    ao_containerId          OUT RAW,
    ao_containerName        OUT VARCHAR2,
    ao_containerKind        OUT INTEGER,
    ao_isLink               OUT NUMBER,
    ao_linkedObjectId       OUT RAW,
    ao_owner                OUT INTEGER,
    ao_ownerName            OUT VARCHAR2,
    ao_creationDate         OUT DATE,
    ao_creator              OUT INTEGER,
    ao_creatorName          OUT VARCHAR2,
    ao_lastChanged          OUT DATE,
    ao_changer              OUT INTEGER,
    ao_changerName          OUT VARCHAR2,
    ao_validUntil           OUT DATE,
    ao_description          OUT VARCHAR2,
    ao_showInNews           OUT NUMBER,
    ao_checkedOut           OUT NUMBER,
    ao_checkOutDate         OUT DATE,
    ao_checkOutUser         OUT INTEGER,
    ao_checkOutUserOid      OUT RAW,
    ao_checkOutUserName     OUT VARCHAR2,
    ao_maxlevels            OUT INTEGER,
    ao_defaultView          OUT INTEGER,
    ao_refOid               OUT RAW,
    ao_refName              OUT VARCHAR2
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


    -- body:
BEGIN
    -- conversions
    p_stringToByte (ai_oid_s, l_oid);
    
    -- retrieve the base object data:
    l_retValue := p_Discussion_01$retrieve (ai_oid_s, ai_userId, ai_op, ao_state,
                    ao_tVersionId, ao_typeName, ao_name, ao_containerId,
                    ao_containerName, ao_containerKind, ao_isLink,
                    ao_linkedObjectId, ao_owner, ao_ownerName, ao_creationDate,
                    ao_creator, ao_creatorName, ao_lastChanged, ao_changer,
                    ao_changerName, ao_validUntil, ao_description, ao_showInNews,
                    ao_checkedOut, ao_checkOutDate, ao_checkOutUser,
                    ao_checkOutUserOid, ao_checkOutUserName, ao_maxlevels,
                    ao_defaultView);

    IF (l_retValue = c_ALL_RIGHT)           -- operation properly performed?
    THEN
    BEGIN
        BEGIN
	        -- get refOid and refOid name
            SELECT  refOid
            INTO    ao_refOid
            FROM    m2_Discussion_01
            WHERE   oid = l_oid;
        EXCEPTION
            WHEN OTHERS THEN
               ibs_error.log_error ( ibs_error.error, 
                                     'p_XMLDiscussion_01$retrieve',
                                     'Error in SELECT refOid');
        END;
        
        BEGIN
	        SELECT  name
	        INTO    ao_refName
	        FROM    ibs_Object
	        WHERE   oid = ao_refOid;
        EXCEPTION
            WHEN OTHERS THEN
               ibs_error.log_error ( ibs_error.error, 
                                     'p_XMLDiscussion_01$retrieve',
                                     'Error in SELECT name');
        END;
    END;
    END IF; -- if operation properly performed
    COMMIT WORK;
    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 
            'p_XMLDiscussion_01$retrieve',
            'Input: ai_userId = ' || ai_userId || 
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;

END p_XMLDiscussion_01$retrieve;
/

show errors;

-- p_XMLDiscussion_01$retrieve


/******************************************************************************
 * Checks if there are templates to create a new XMLDiscussion
 * (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_op                   Operation to be performed (used for rights
 *                                  check).
 * @param   ai_userId               ID of the user who is deleting the object.
 *
 * @output parameters:
 * @param   ao_count                a counter of templates which the user is
 *                                  allowed to use.
 *
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_XMLDiscussion_01$checkTempl
(
    -- input parameters:
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    -- output parameters:
    ao_count                OUT INTEGER
)RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- operation not o.k.
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    
    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of procedure


    -- body:
BEGIN
    -- gets all templates which can be used for Discussions
    SELECT  COUNT (*)
    INTO    ao_count
    FROM    v_Container$rights v
    WHERE   B_AND (v.rights, ai_op) = ai_op
      AND   v.userId = ai_userId
      AND   v.tVersionId = 16843537; -- XMLDiscussionTemplate

    COMMIT WORK;
    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 
            'p_XMLDiscussion_01$checkTempl',
            'Input: ai_userId = ' || ai_userId || 
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;

END p_XMLDiscussion_01$checkTempl;
/

show errors;
-- p_XMLDiscussion_01$checkTempl

EXIT;