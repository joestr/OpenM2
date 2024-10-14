/******************************************************************************
 * All procedures regarding reference objects. <BR>
 *
 * @version     2.21.0006, 06.06.2002 KR
 *
 * @author      Mario Stegbauer (MS)  980805
 ******************************************************************************
 */


CREATE OR REPLACE FUNCTION p_Referenz_01$create
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
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3;
    c_ST_ACTIVE             CONSTANT INTEGER := 2; -- state of active object

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_rights                INTEGER;
    l_returnValue           INTEGER;
    l_linkedtVersionId      INTEGER;
    l_targetVersionId       INTEGER;
    l_linkedObjectId        RAW (8);
    l_containerId           RAW (8);
    l_copyContainerId       RAW (8);
    l_oid                   RAW (8);
    l_dummyObjectId         RAW (8);
    l_dummyObjectId_s       VARCHAR2 (255);
    l_dummy                 INTEGER;

-- body:
BEGIN
    -- conversions
    p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);
    p_stringToByte (ai_containerId_s, l_containerId);
    p_stringToByte (ao_oid_s, l_oid);


    BEGIN
        SELECT  tVersionId
        INTO    l_linkedtVersionId
        FROM    ibs_Object
        WHERE   oid = l_linkedObjectId;
    EXCEPTION
        WHEN OTHERS THEN
            ibs_error.log_error ( ibs_error.error, 'p_Referenz_01$create',
                                'Error in get tVersionId of linkedObject');
        RAISE;
    END;

    BEGIN
        SELECT  tVersionId, containerId
        INTO    l_targetVersionId, l_copyContainerId
        FROM    ibs_Object
        WHERE   oid = l_containerId;
    EXCEPTION
        WHEN OTHERS THEN
            ibs_error.log_error ( ibs_error.error, 'p_Referenz_01$create',
                                'Error in get tVersionId of Container');
        RAISE;
    END;
    
    -- check tVersionId
    IF (l_targetVersionId = 16842929)  -- Group
    THEN
   
        IF (l_linkedtVersionId = 16842929) -- Group
        THEN
            l_retValue := p_Group_01$addGroup( ai_userId,
                    l_containerId, l_linkedObjectId, c_NOOID );
        END IF;
          
        IF (l_linkedtVersionId = 16842913) -- User
        THEN  
            l_retValue := p_Group_01$addUser( ai_userId,
                      l_containerId, l_linkedObjectId, c_NOOID);
        END IF;
        ao_oid_s := ai_linkedObjectId_s;

    ElSIF (l_targetVersionId = 16842913) -- User
    THEN
        
        -- try to find linked person
        l_dummy := 1;
        BEGIN    
            SELECT    linkedObjectId
            INTO      l_dummyObjectId
            from      ibs_Object
            WHERE     containerId = l_containerId
              AND     tVersionId = 16842801;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                l_dummy := 0; -- no current linked person
            WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error, 'p_Referenz_01$create',
                                    'Error in get linkedObjectId containerId = ' || l_containerId);
            RAISE;
        END;
        
        IF (l_dummy = 1)  -- if there is an linked person
        THEN
            BEGIN   -- delete reference to person in user
                DELETE  ibs_Object 
                WHERE   containerId = l_containerId
                    AND tVersionId = 16842801
                    AND state = c_ST_ACTIVE;
            EXCEPTION
                WHEN OTHERS THEN
                    ibs_error.log_error ( ibs_error.error, 'p_Referenz_01$create',
                                        'Error in DELETE ibs_Object I containerId = ' || l_containerId);
                RAISE;
            END;
 
            BEGIN       -- delete reference to user in person
                DELETE  ibs_Object 
                WHERE   linkedObjectId = l_containerId
                    AND containerId IN
                        (
                            SELECT  o2.oid 
                            FROM    ibs_Object o2
                            WHERE   o2.containerId = l_dummyObjectId
                              AND   tVersionId = 16866817
                              AND   state = c_ST_ACTIVE
                        )
                    AND tVersionId = 16842801;
            EXCEPTION
                WHEN OTHERS THEN
                    ibs_error.log_error ( ibs_error.error, 'p_Referenz_01$create',
                                        'Error in DELETE ibs_Object II containerId = ' || l_dummyObjectId);
                RAISE;
            END;
        END IF; -- if reference to person exists

        l_retValue := p_Object$performCreate( ai_userId,
                ai_op, ai_tVersionId, ai_name,
                ai_containerId_s, ai_containerKind,
                ai_isLink, ai_linkedObjectId_s,
                ai_description, ao_oid_s, 
                l_dummyObjectId );

        BEGIN
            UPDATE ibs_User
            SET fullname = (SELECT  name 
                            FROM    ibs_Object
                            WHERE   oid = l_linkedObjectId
                            AND   state = c_ST_ACTIVE)
            WHERE oid = l_containerId;
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error, 'p_Referenz_01$create',
                                    'Error in UPDATE ibs_User');
            RAISE;
        END;

        BEGIN
            SELECT  oid
            INTO    l_dummyObjectId
            FROM    ibs_Object 
            WHERE   containerId = l_linkedObjectId
              AND   tVersionId = 16866817
              AND   state = c_ST_ACTIVE;
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error, 'p_Referenz_01$create',
                                    'Error in get SELECT oid FROM ibs_Object');
            RAISE;
        END;


      p_byteToString( l_dummyObjectId, l_dummyObjectId_s);

      l_retValue := p_Object$performCreate (ai_userId,
                ai_op, ai_tVersionId, ai_name,
                l_dummyObjectId_s, ai_containerKind,
                ai_isLink, ai_containerId_s,
                ai_description, ao_oid_s,
                l_dummyObjectId );

    ELSIF (l_targetVersionId = 16863745) -- Membership
    THEN
        l_retValue := p_Group_01$addUser( ai_userId,
                l_linkedObjectId, l_copyContainerId, c_NOOID );

        ao_oid_s := ai_linkedObjectId_s;
    ELSE
        -- create a referenceobject
        l_retValue := p_Object$performCreate (ai_userId,
                    ai_op, ai_tVersionId, ai_name,
                    ai_containerId_s, ai_containerKind, 
                    ai_isLink, ai_linkedObjectId_s, 
                    ai_description, ao_oid_s,
                    l_dummyObjectId);
    END IF;  -- check tVersionId

COMMIT WORK;

    RETURN l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Referenz_01$create',
                          'userId: ' || ai_userId ||
                          ', op: ' || ai_op ||
                          ', tVersionId: ' || ai_tVersionId ||
                          ', name: ' || ai_name ||
                          ', containerId_s: ' || ai_containerId_s ||
                          ', containerKind: ' || ai_containerKind ||
                          ', isLink: ' || ai_isLink ||
                          ', linkedObjectId_s: ' || ai_linkedObjectId_s ||
                          ', description: ' || ai_description ||
                          ', sqlcode: ' || SQLCODE ||
                          ', sqlerrm: ' || SQLERRM );
    RETURN 0;
END p_Referenz_01$create;
/

show errors;

/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be changed.
 * @param   ai_userId           ID of the user who is changing the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @param   ai_name             Name of the object.
 * @param   ai_validUntil       Date until which the object is valid.
 * @param   ai_description      Description of the object.
 * @param   ai_showInNews       Display the current object in the news?
 * @param   ai_linkedObjectId_s The oid of the linked object.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Referenz_01$change
(
    -- common input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               ibs_User.id%TYPE,
    ai_op                   ibs_Operation.id%TYPE,
    ai_name                 ibs_Object.name%TYPE,
    ai_validUntil           ibs_Object.validUntil%TYPE,
    ai_description          ibs_Object.description%TYPE,
    ai_showInNews           ibs_Object.flags%TYPE,
    -- type-specific input parameters:
    ai_linkedObjectId_s     VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found
    c_REF_LINK              ibs_Reference.kind%TYPE := 1;-- reference kind: link

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (255); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER := 0;   -- row counter
    l_oid                   ibs_Object.oid%TYPE; -- oid of the reference object
    l_linkedObjectId        ibs_Object.linkedObjectId%TYPE;
                                            -- oid of the referenced object
    l_name                  ibs_Object.name%TYPE;
                                            -- the name of the referenced object
    l_description           ibs_Object.description%TYPE;
                                            -- desc. of the referenced object
    l_typeName              ibs_Object.typeName%TYPE;
                                            -- name of type of the ref. object
    l_icon                  ibs_Object.icon%TYPE;
                                            -- name of icon of the ref. object
    l_flags                 ibs_Object.flags%TYPE;
                                            -- flags of the referenced object
    l_rKey                  ibs_Object.rKey%TYPE;
                                            -- rKey of the referenced object

-- body:
BEGIN
    BEGIN
        -- get data of linked object into link:
        -- If the linked object is itself a link the link shall point to the
        -- original linked object.
        SELECT  name, typeName, description, flags, icon, rKey
        INTO    l_name, l_typeName, l_description, l_flags, l_icon, l_rKey
        FROM    ibs_Object
        WHERE   oid = l_linkedObjectId;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- object does not exist
            -- set return value:
            l_retValue := c_OBJECTNOTFOUND;
            -- create error entry:
            l_ePos := 'SELECT object data';
            RAISE;                      -- call common exception handler
        WHEN OTHERS THEN                -- any exception
            -- create error entry:
            l_ePos := 'SELECT object data';
            RAISE;                      -- call common exception handler
    END;

--    BEGIN TRANSACTION
    -- perform the change of the object:
    l_retValue := p_Object$performChange (ai_oid_s, ai_userId, ai_op, l_name,
        ai_validUntil, l_description, ai_showInNews, l_oid);

    -- check if the operation was successful:
    IF (l_retValue = c_ALL_RIGHT)       -- operation properly performed?
    THEN
        -- convert the linked object id:
        p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);

        BEGIN
            -- update further information:
            UPDATE  ibs_Object
            SET     name = l_name,
                    typeName = l_typeName,
                    description = l_description,
                    flags = l_flags,
                    icon = l_icon,
                    rKey = l_rKey,
                    linkedObjectId = l_linkedObjectId
            WHERE   oid = l_oid;
        EXCEPTION
            WHEN OTHERS THEN            -- any exception
                -- create error entry:
                l_ePos := 'Error in update';
                RAISE;                  -- call common exception handler
        END;

        -- store the reference:
        p_Reference$create (l_oid, null, l_linkedObjectId, c_REF_LINK);
    END IF; -- if operation properly performed

    COMMIT WORK;                        -- make changes permanent

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid_s = ' || ai_oid_s  ||
            ', ai_userId = ' || ai_userId  ||
            ', ai_op = ' || ai_op  ||
            ', ai_name = ' || ai_name ||
            ', ai_validUntil = ' || ai_validUntil ||
            ', ai_description = ' || ai_description ||
            ', ai_showInNews = ' || ai_showInNews ||    
            ', ai_linkedObjectId_s = ' || ai_linkedObjectId_s ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Referenz_01$change', l_eText);
        -- set new transaction starting point:
        COMMIT WORK;
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_Referenz_01$change;
/

show errors;

EXIT;
