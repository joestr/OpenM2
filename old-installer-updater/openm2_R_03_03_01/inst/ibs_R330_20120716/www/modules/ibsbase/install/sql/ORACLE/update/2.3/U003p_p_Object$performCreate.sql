/******************************************************************************
 * Creates a new object (incl. rights check). <BR>
 * This procedure does not make a transaction, so that it can be used from
 * within a transaction of another procedure. <BR>
 * This procedure is needed for creating a business object which contains
 * several other objects.
 *
 * @input parameters:
 * @param   ai_userId           ID of the user who is creating the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @param   ai_tVersionId       Type of the new object.
 * @param   ai_name             Name of the object.
 * @param   ai_containerId_s    ID of the container where object shall be
 *                              created in.
 * @param   ai_containerKind    Kind of object/container relationship
 * @param   ai_isLink           Defines if the object is a link
 * @param   ai_linkedObjectId_s If the object is a link this is the ID of the
 *                              where the link shows to.
 * @param   ai_description      Description of the object.
 *
 * @output parameters:
 * @param   ao_oid_s            String representation of OID of the newly
 *                              created object.
 * [@param  ao_oid]             Oid of the newly created object.
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
CREATE OR REPLACE FUNCTION p_Object$performCreate
(
    -- input parameters:
    ai_userId               ibs_User.id%TYPE,
    ai_op                   ibs_Operation.id%TYPE,
    ai_tVersionId           ibs_Object.tVersionId%TYPE,
    ai_name                 ibs_Object.name%TYPE,
    ai_containerId_s        VARCHAR2,
    ai_containerKind        ibs_Object.containerKind%TYPE,
    ai_isLink               ibs_Object.isLink%TYPE,
    ai_linkedObjectId_s     VARCHAR2,
    ai_description          ibs_Object.description%TYPE,
    -- output parameters:
    ao_oid_s                OUT VARCHAR2,
    ao_oid                  OUT ibs_Object.oid%TYPE
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found
    c_NOOID                 CONSTANT ibs_Object.oid%TYPE := createOid (0, 0);
                                            -- default value for no defined oid
    c_ISCHECKEDOUT          CONSTANT ibs_Object.flags%TYPE := 16;
    c_EMPTYPOSNOPATH        CONSTANT ibs_Object.posNoPath%TYPE := '0000';

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (255); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_id                    ibs_Object.id%TYPE := 0;
    l_typeName              ibs_Object.typeName%TYPE := 'UNKNOWN';
    l_isContainer           ibs_Object.isContainer%TYPE := 0;
    l_showInMenu            ibs_Object.showInMenu%TYPE := 0;
    l_showInNews            ibs_Type.showInNews%TYPE := 0;
    l_icon                  ibs_Object.icon%TYPE := 'icon.gif';
    l_oLevel                ibs_Object.oLevel%TYPE := 1;
                                            -- lowest possible object level
    l_posNo                 ibs_Object.posNo%TYPE := 0;
    l_posNoPath             ibs_Object.posNoPath%TYPE := c_EMPTYPOSNOPATH;
    l_flags                 ibs_Object.flags%TYPE := 0;
    l_rKey                  ibs_Object.rKey%TYPE := 0;
    l_name                  ibs_Object.name%TYPE := ai_name;
    l_description           ibs_Object.description%TYPE := ai_description;
    l_op                    ibs_Operation.id%TYPE := ai_op;
    l_containerId           ibs_Object.containerId%TYPE := c_NOOID;
    l_linkedObjectId        ibs_Object.linkedObjectId%TYPE := c_NOOID;
    l_rights                ibs_RightsKeys.rights%TYPE := 0;
                                            -- the current rights
    l_coUserId              ibs_Checkout_01.userId%TYPE := 0;
                                            -- user who checked the object out
    l_check                 ibs_Object.flags%TYPE := 1;
    l_validUntil            ibs_Object.validUntil%TYPE := SYSDATE;

    -- exceptions:
    e_TAB_CREATION_ERROR    EXCEPTION;

-- body:
BEGIN
    -- conversions (OBJECTIDSTRING) - all input object ids must be converted:
    p_stringToByte (ai_containerId_s, l_containerId);
    p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);
    ao_oid := c_NOOID;

    -- retrieve check-out-info for new objects container?
    BEGIN
        -- get the check out data:
        SELECT  co.userId, B_AND (o.flags, c_ISCHECKEDOUT)
        INTO    l_coUserId, l_check
        FROM    ibs_CheckOut_01 co, ibs_Object o
        WHERE   o.oid = l_containerId
            AND co.oid = o.oid;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            l_check := 0;
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'get checkout user data';
            RAISE;                      -- call common exception handler
    END;

    -- cont-object checked out AND checkout-user is NOT current user
    IF (l_check = c_ISCHECKEDOUT AND l_coUserId <> ai_userId)
    THEN
        -- current user is not check-out user
        l_retValue := c_INSUFFICIENT_RIGHTS;
    ELSE                                -- object not checked out or
                                        -- user is check-out user?
        -- checkout-check ok
        -- now check if user has permission to create object

        -- check the container rights:
        l_retValue := p_Rights$checkRights (ao_oid, l_containerId,
                                            ai_userId, l_op, l_rights);

        -- check if the user has the necessary rights:
        IF (l_rights = l_op)            -- the user has the rights?
        THEN
---------
--
-- START get and calculate base-data
--         (old trigger functionality!)
--
            --
            -- 1. compute id and oid for new object
            --
            BEGIN
                -- get new id for new object:
                SELECT  objectIdSeq.NEXTVAL
                INTO    l_id
                FROM    DUAL;
            EXCEPTION
                WHEN OTHERS THEN
                    -- create error entry:
                    l_ePos := 'increasing objectIdSeq';
                    RAISE;              -- call common exception handler
            END;

            -- compute oid; convert
            p_createOid (ai_tVersionId, l_id, ao_oid);
            p_byteToString (ao_oid, ao_oid_s);

            --
            -- 2. compute olevel, posno and posnopath
            --
            -- derive position number from other objects within container:
            -- The posNo is one more than the actual highest posNo within the
            -- container or 1 if there is no object within the container yet.
            BEGIN
                SELECT  DECODE (MAX (posNo), NULL, 1, MAX (posNo) + 1)
                INTO    l_posNo
                FROM    ibs_Object
                WHERE   containerId = l_containerId;
            EXCEPTION
                WHEN OTHERS THEN
                    -- create error entry:
                    l_ePos := 'computing posNo-Information';
                    RAISE;              -- call common exception handler
            END;

            -- derive position level and rkey from container:
            -- The level of an object is the level of the container plus 1
            -- or 0, if there is no container.
            BEGIN
                SELECT  DECODE (oLevel, NULL, 1, oLevel + 1), rKey,
                        validUntil
                INTO    l_oLevel, l_rKey, l_validUntil
                FROM    ibs_Object
                WHERE   oid = l_containerId;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    -- no container found for given object;
                    -- must be root object
                    l_oLevel := 1;
                    l_rKey := 0;
                WHEN OTHERS THEN
                    -- create error entry:
                    l_ePos := 'computing oLevel information';
                    RAISE;              -- call common exception handler
            END;

            -- calculate new position path:
            IF (l_containerId <> c_NOOID) -- object is within a container?
            THEN
                -- compute the posNoPath as posNoPath of container
                -- concatenated by the posNo of this object:
                BEGIN
                    SELECT  DISTINCT posNoPath || intToRaw (l_posNo, 4)
                    INTO    l_posNoPath
                    FROM    ibs_Object
                    WHERE   oid = l_containerId;
                EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                        l_posNoPath := intToRaw (l_posNo, 4);
                WHEN OTHERS THEN
                    -- create error entry:
                    l_ePos := 'compute posNoPath';
                    RAISE;              -- call common exception handler
                END;
                -- if object is within a container
            ELSE                        -- object is not within a container
                                        -- i.e. it is on top level
                -- compute the posNoPath as posNo of this object:
                l_posNoPath := intToRaw (l_posNo, 4);
            END IF;-- else object is not within a container


            --
            -- 3. get type-info: type name, icon and containerId, showInMenu,
            --                     showInNews
            --
            BEGIN
                SELECT  t.name, t.isContainer, t.showInMenu,
                        t.showInNews * 4, t.icon
                INTO    l_typeName, l_isContainer, l_showInMenu,
                        l_showInNews, l_icon
                FROM    ibs_Type t, ibs_TVersion tv
                WHERE   tv.id = ai_tVersionId
                    AND t.id = tv.typeId;
            EXCEPTION
                WHEN OTHERS THEN
                    -- create error entry:
                    l_ePos := 'selecting type-information';
                    RAISE;              -- call common exception handler
            END;

            --
            -- 4. distinguish between reference/no-reference objects
            --
            IF (ai_isLink = 1)          -- reference object?
            THEN
                --
                -- IMPORTANT: rights-key will be set in here
                --
                -- get data of linked object into new reference object:
                -- If the linked object is itself a link the link shall point
                -- to the original linked object.
                BEGIN
                    SELECT  name, typeName, description, flags, icon, rKey
                    INTO    l_name, l_typeName, l_description, l_flags, l_icon,
                            l_rKey
                    FROM    ibs_Object
                    WHERE   oid = l_linkedObjectId;
                EXCEPTION
                    WHEN OTHERS THEN
                        -- create error entry:
                        l_ePos := 'get link data';
                        RAISE;          -- call common exception handler
                END;
            ELSE
                IF (l_name IS NULL OR l_name = '' OR l_name = ' ')
                THEN
                    l_name := l_typeName;
                END IF;
            END IF; -- if link object

            --
            -- 5. calculate new flags value: add showInNews
            --
            l_flags := B_AND (l_flags, 2147483643) + l_showInNews;

--
-- END get and calculate base data
--
---------

            --
            -- last but not least: insert new information
            --
            BEGIN
                INSERT INTO ibs_Object
                        (id, oid, /*state,*/ tVersionId, typeName,
                        isContainer, name, containerId, containerKind,
                        isLink, linkedObjectId, showInMenu, flags,
                        owner, oLevel, posNo, posNoPath, creationDate,
                        creator, lastChanged, changer, validUntil,
                        description, icon, /*processState,*/ rKey)
                VALUES    (l_id, ao_oid, /*???,*/ ai_tVersionId, l_typeName,
                        l_isContainer, l_name, l_containerId, ai_containerKind,
                        ai_isLink, l_linkedObjectId, l_showInMenu, l_flags,
                        ai_userId, l_oLevel, l_posNo, l_posNoPath, SYSDATE,
                        ai_userId, SYSDATE, ai_userId, l_validUntil,
                        ai_description, l_icon, /*???,*/ l_rKey);
            EXCEPTION
                WHEN OTHERS THEN
                    -- create error entry:
                    l_ePos := 'INSERT INTO ibs_Object';
                    RAISE;              -- call common exception handler
            END;

            -- check if creation of tabs is necessary:
            IF (ai_containerKind <> 2)  -- object is independent?
            THEN
                -- create tabs for the object:
                l_retValue := p_Object$createTabs
                                (ai_userId, ai_op, ao_oid, ai_tVersionId);

                -- check success:
                IF (l_retValue <> c_ALL_RIGHT)
                THEN
                    RAISE e_TAB_CREATION_ERROR;
                END IF;

                -- set the protocol entry:
                l_retValue := p_Object$performInsertProtocol
                    (ao_oid, ai_userId, ai_op, ai_userId);
            -- end if object is independent
            ELSE                        -- object is a tab
                -- set return value: ok
                l_retValue := c_ALL_RIGHT;
            END IF; -- else object is a tab
        -- if the user has the rights
        ELSE                            -- the user does not have the rights
            -- user has not enough rights!
            l_retValue := c_INSUFFICIENT_RIGHTS;
        END IF; -- else the user does not have the rights
    END IF; -- else object not checked out or user is check-out user

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_userId = ' || ai_userId || ', ' ||
            ', ai_op = ' || ai_op || ', ' ||
            ', ai_tVersionId = ' || ai_tVersionId || ', ' ||
            ', ai_name = ' || ai_name || ', ' ||
            ', ai_containerId_s = ' || ai_containerId_s || ', ' ||
            ', ai_containerKind = ' || ai_containerKind || ', ' ||
            ', ai_isLink = ' || ai_isLink ||
            ', ai_linkedObjectId_s = ' || ai_linkedObjectId_s ||
            ', ai_description = ' || ai_description ||
            '; sqlcode = ' || SQLCODE ||
            ', sqlerrm = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Object$performCreate', l_eText);
        -- return error code:
        RETURN c_NOT_OK;
END p_Object$performCreate;
/

show errors;

EXIT;
