/******************************************************************************
 * All stored procedures regarding the ibs_Object table which have to
 * be inserted to the DB after all the others. <BR>
 *
 * @version     2.21.0023, 04.06.2002 KR
 *
 * @author      Mario Stegbauer (MS)  980805
 ******************************************************************************
 */

/******************************************************************************
 * Works as a switch to idenify all BusinessObjectTypes specific copymethods.
 * The Result is the oid of the duplicated BusinessObject. <BR>
 * This procedure also copies all links pointing to this object.
 *
 * @input parameters:
 * @param   ai_oid              Oid of the object to be copied.
 * @param   ai_userId           Id of the user who is copying the object.
 * @param   ai_tVersionId       The tVersionId of the object to be copied.
 * @param   ai_newOid           The oid of the newly created object.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  c_ALL_RIGHT             Action performed, values returned, everything ok.
 *  c_NOT_OK                An error occurred.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_Object$switchCopy
(
    -- input parameters:
    ai_oid                  ibs_Object.oid%TYPE,
    ai_userId               ibs_User.id%TYPE,
    ai_tVersionId           ibs_Object.tVersionId%TYPE,
    ai_newOid               ibs_Object.oid%TYPE
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER := 0;   -- number of rows
    l_templateOid           ibs_Object.oid%TYPE;
    l_typeCode              ibs_Type.code%TYPE; -- the type code for the object


-- body:
BEGIN
    BEGIN
        -- get the type code:
        SELECT  t.code
        INTO    l_typeCode
        FROM    ibs_Type t, ibs_TVersion tv
        WHERE   t.id = tv.typeId
            AND tv.id = ai_tVersionId;
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'get type code';
            RAISE;                      -- call common exception handler
    END;

/*
----------------------
--  HP Performance
--
--    Procedure removed: functionality is now in p_Object$copy
----------------------
    l_retValue := p_Object$performCopy (ai_oid,
                    ai_userId, ai_newOid);
*/

    -- distuingish between the several types of objects which have own data
    -- structures:
    IF ((ai_tVersionId = 16842833) OR   -- attachment?
        (ai_tVersionId = 16869377) OR   -- file?
        (ai_tVersionId = 16869633) OR   -- hyperlink?
        (ai_tVersionId = 16874497) OR   -- DocumentTemplate
        (ai_tVersionId = 16872465) OR   -- ImportScript
        (ai_tVersionId = 16862209)      -- WorkflowTemplate
	)
    THEN
        -- copy Attachment
        l_retValue := p_Attachment_01$BOCopy (ai_oid, ai_userId, ai_newOid);

    ELSIF ((ai_tVersionId = 16843521) -- discussion
            OR (ai_tVersionId = 16845313) -- black board
            OR (ai_tVersionId = 16843553)) -- Xmldiscussion
    THEN
        l_retValue := p_Diskussion_01$BOCopy (ai_oid, ai_userId, ai_newOid);
    ELSIF (ai_tVersionId = 16854785) -- Address_01
    THEN
        l_retValue := p_Address_01$BOCopy (ai_oid, ai_userId, ai_newOid);
    ELSIF (ai_tVersionId = 16853505) -- Person_01
    THEN
        l_retValue := p_Person_01$BOCopy (ai_oid, ai_userId, ai_newOid);
    ELSIF (ai_tVersionId = 16854017) -- Company_01
    THEN
        l_retValue := p_Company_01$BOCopy (ai_oid, ai_userId, ai_newOid);
    ELSIF (ai_tVersionId = 16843777) -- Thema_01
    THEN
        l_retValue := p_Beitrag_01$BOCopy (ai_oid, ai_userId, ai_newOid);
    ELSIF (ai_tVersionId = 16844033) -- Beitrag_01
    THEN
        l_retValue := p_Beitrag_01$BOCopy (ai_oid, ai_userId, ai_newOid);
    ELSIF (ai_tVersionId = 16842929) -- Group_01
    THEN
        l_retValue := p_Group_01$BOCopy (ai_oid, ai_userId, ai_newOid);
    ELSIF (ai_tVersionId = 16842913) -- User_01
    THEN
        l_retValue := p_User_01$BOCopy (ai_oid, ai_userId, ai_newOid);
    ELSIF (ai_tVersionId = 16842993) -- Domäne
    THEN
        l_retValue := p_Domain_01$BOCopy (ai_oid, ai_userId, ai_newOid);
    ELSIF (ai_tVersionId = 16843265) -- Termin_01
    THEN
        l_retValue := p_Termin_01$BOCopy (ai_oid, ai_userId, ai_newOid);
    ELSIF (ai_tVersionId = 16851457) -- Price_01
    THEN
        l_retValue := p_Price_01$BOCopy (ai_oid, ai_userId, ai_newOid);
    ELSIF (ai_tVersionId = 16848129) -- Product_01
    THEN
        l_retValue := p_Product_01$BOCopy (ai_oid, ai_userId, ai_newOid);
    ELSIF (ai_tVersionId = 16870145) -- Notiz_01
    THEN
        l_retValue := p_Note_01$BOCopy (ai_oid, ai_userId, ai_newOid);
    ELSIF (ai_tVersionId = 16875009) -- XMLViewerContainer_01
    THEN
        l_retValue := p_XMLViewerContainer_01$BOCopy (ai_oid, ai_userId, ai_newOid);
    ELSIF (ai_tVersionId = 16872705) -- XMLViewer_01
    THEN
        l_retValue := p_XMLViewer_01$BOCopy (ai_oid, ai_userId, ai_newOid);
    ELSIF ((ai_tVersionId = 16872561) OR   --  FileConnector_01
           (ai_tVersionId = 16872577) OR   --  FTPConnector_01
           (ai_tVersionId = 16872593) OR   --  MailConnector_01
           (ai_tVersionId = 16872609) OR   --  HTTPConnector_01
           (ai_tVersionId = 16872625) OR   --  EDISwitchConnector_01
           (ai_tVersionId = 16872641)      --  CGIConnector_01
          )
    THEN
        l_retValue := p_Connector_01$BOCopy (ai_oid, ai_userId, ai_newOid);
    ELSIF (ai_tVersionId = 16874497)    -- DocumentTemplate_01
    THEN
        l_retValue := p_DocumentTemplate_01$BOCopy (ai_oid, ai_userId, ai_newOid);
    ELSIF (ai_tVersionId = 16843537)    -- XMLDiscussionTemplate_01
    THEN
        l_retValue := p_XMLDiscTemplate_01$BOCopy (ai_oid, ai_userId, ai_newOid);
    ELSIF (ai_tVersionId = 16872721)    -- DiscXMLViewer_01
    THEN
        l_retValue := p_DiscXMLViewer_01$BOCopy (ai_oid, ai_userId, ai_newOid);
    ELSIF (ai_tVersionId = 16875297)    -- QueryCreator_01
    THEN
        l_retValue := p_QueryCreator_01$BOCopy (ai_oid, ai_userId, ai_newOid);
    ELSIF (ai_tVersionId = 16875345)    -- QueryExecutive_01
    THEN
        l_retValue := p_QueryExecutive_01$BOCopy (ai_oid, ai_userId, ai_newOid);
    ELSIF (ai_tVersionId = 16872529)    -- Translator
    THEN
        l_retValue := p_Translator_01$BOCopy (ai_oid, ai_userId, ai_newOid);
    ELSIF (ai_tVersionId = 16872321)    -- ASCIITranslator_01
    THEN
        l_retValue := p_ASCIITranslator_01$BOCopy (ai_oid, ai_userId, ai_newOid);
    ELSIF (ai_tVersionId = 16872321)    -- EDITranslator_01
    THEN
        l_retValue := p_EDITranslator_01$BOCopy (ai_oid, ai_userId, ai_newOid);
    ELSE

        -- check if the tVersionId belongs to a m2 type defined by a document template.
        -- in this case call the copy procedure for the XMLViewer object type.
        BEGIN
            SELECT  oid
            INTO    l_templateOid
            FROM    ibs_DocumentTemplate_01
            WHERE   tVersionId = ai_tVersionId;

            l_retValue := p_XMLViewer_01$BOCopy (ai_oid, ai_userId, ai_newOid);
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                null; --debug ('keine spezielle proz gefunden');
        END;
    END IF;

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid = ' || ai_oid ||
            ', ai_userId = ' || ai_userId ||
            ', ai_tVersionId = ' || ai_tVersionId ||
            ', ai_newOid = ' || ai_newOid ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Object$switchCopy', l_eText);
        -- return error code:
        RETURN c_NOT_OK;
END p_Object$switchCopy;
/

show errors;


/******************************************************************************
 * Copies a selected BusinessObject and its childs.(incl. rights check). <BR>
 * The rightcheck is done before we start to copy. When one BO of the tree is
 * not able to be copied because of a negativ rightcheckresult, the action is
 * stopped.
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the rootobject to be copied.
 * @param   ai_userId           ID of the user who is copying the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @param   ai_targetId_s       Oid of the target BusinessObjectContainer where
 *                              the root object is copied to.
 *
 * @output parameters:
 * @param   ao_newOid_s         The oid of the newly created object.
 * @return  A value representing the state of the procedure.
 *  c_ALL_RIGHT             Action performed, values returned, everything ok.
 *  c_NOT_OK                An error occurred.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_Object$copy
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               ibs_User.id%TYPE,
    ai_op                   ibs_Operation.id%TYPE,
    ai_targetId_s           VARCHAR2,
    -- output parameters:
    ao_newOid_s             OUT VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_NOOID                 CONSTANT ibs_Object.oid%TYPE := createOid (0, 0);
                                            -- default value for no defined oid
    c_ST_ACTIVE             CONSTANT ibs_Object.state%TYPE := 2; -- active state
    c_RIGHT_VIEW            CONSTANT ibs_RightsKeys.rights%TYPE := 2;
    c_RIGHT_READ            CONSTANT ibs_RightsKeys.rights%TYPE := 4;
    c_ISCHECKEDOUT          CONSTANT ibs_Object.flags%TYPE := 16;
                                            -- bit value for check out state
    c_EMPTYPOSNOPATH        CONSTANT ibs_Object.posNoPath%TYPE := '0000';

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER := 0;   -- number of rows
    l_templateOid           ibs_Object.oid%TYPE;
    l_rights                ibs_RightsKeys.rights%TYPE := 0;
    l_structuralError       INTEGER := 0;
    l_copyId                ibs_Copy.copyId%TYPE := 0;

    -- ... of target-container
    l_targetOid             ibs_Object.oid%TYPE := c_NOOID;
    l_targetPosNoPath       ibs_Object.posNoPath%TYPE := c_EMPTYPOSNOPATH;
    l_targetOLevel          ibs_Object.oLevel%TYPE := 0;

    -- ... of object to copy (object)
    l_rootOid               ibs_Object.oid%TYPE := c_NOOID;
    l_rootPosNoPath         ibs_Object.posNoPath%TYPE := c_EMPTYPOSNOPATH;
    l_rootOLevel            ibs_Object.oLevel%TYPE := 0;

    -- ... of new object that must be copied sub-sequently (in loop)
    l_newId                 ibs_Object.id%TYPE := 0;
    l_newContainerId        ibs_Object.containerId%TYPE := c_NOOID;
                                            -- oid of the object's new container
    l_newOid                ibs_Object.oid%TYPE := c_NOOID;
                                            -- oid of the new object
    l_newPosNoPath          ibs_Object.posNoPath%TYPE := c_EMPTYPOSNOPATH;
    l_newPosNo              ibs_Object.posNo%TYPE := 0;
    l_newOLevel             ibs_Object.oLevel%TYPE := 0;
    l_newTVersionId         ibs_Object.tVersionId%TYPE := 0;
    l_newName               ibs_Object.name%TYPE := '';
    l_newDescription        ibs_Object.description%TYPE;
    l_newIcon               ibs_Object.icon%TYPE;
    l_newRKey               ibs_Object.rKey%TYPE := 0;
    l_newState              ibs_Object.state%TYPE := 0;
    l_newTypeName           ibs_Object.typeName%TYPE := 'UNKNOWN';
    l_newIsContainer        ibs_Object.isContainer%TYPE := 0;
    l_newContainerKind      ibs_Object.containerKind%TYPE := 0;
    l_newIsLink             ibs_Object.isLink%TYPE := 0;
    l_newLinkedObjectId     ibs_Object.linkedObjectId%TYPE := c_NOOID;
    l_newShowInMenu         ibs_Object.showInMenu%TYPE := 0;
    l_newFlags              ibs_Object.flags%TYPE := 0;
    l_newProcessState       ibs_Object.processState%TYPE := 0;
    l_newCreationDate       ibs_Object.creationDate%TYPE;
    l_newCreator            ibs_Object.creator%TYPE := 0;
    l_newConsistsOfId       ibs_Object.consistsOfId%TYPE := 0;

    -- ... of old object that must be copied sub-sequently (in loop)
    l_oldOid                ibs_Object.oid%TYPE := c_NOOID;
                                            -- oid of the old object
    l_oldContainerId        ibs_Object.oid%TYPE := c_NOOID;
                                            -- oid of the old objects container

     -- define cursor over all copy-object incl. subobjects:
    CURSOR  containerCursor IS
        SELECT  oid, containerId
        FROM    ibs_Object
        WHERE   oLevel >= l_rootOLevel
            AND posNoPath LIKE l_rootPosNoPath || '%'
            AND state = c_ST_ACTIVE
        ORDER BY posNoPath ASC;

     -- define cursor row
     l_containerCursorRow   containerCursor%ROWTYPE;


-- body:
BEGIN
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    p_stringToByte (ai_oid_s, l_rootOid);
    p_stringToByte (ai_targetId_s, l_targetOid);

    -- BEGIN TRANSACTION
    -- set a save point for the current transaction:
    SAVEPOINT s_Object$copy;

    BEGIN
        -- read structural data of object to be copied:
        SELECT  posNoPath, oLevel
        INTO    l_rootPosNoPath, l_rootOLevel
        FROM    ibs_Object
        WHERE   oid = l_rootOid;
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'reading posNoPath';
            RAISE;                      -- call common exception handler
    END;

    -- get (maximum) rights for this user on object and all its
    -- sub-objects for given operation:
    BEGIN
        SELECT  MIN (B_AND (rights, ai_op))
        INTO    l_rights
        FROM
        (
            SELECT  MAX (B_AND (o.rights, ai_op)) AS rights, o.oid
            FROM    v_Container$rights o
            WHERE   o.userId = ai_userId
                AND o.posNoPath LIKE l_rootPosNoPath || '%'
                AND state = c_ST_ACTIVE
            GROUP BY o.oid
        ) s;
    EXCEPTION
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'reading user rights';
            RAISE;                      -- call common exception handler
    END;

    -- check if the user has the necessary rights:
    IF (l_rights = ai_op)               -- the user has the rights?
    THEN
        -- read out information of the target container-object
        BEGIN
            SELECT  posNoPath, rKey, oLevel
            INTO    l_targetPosNoPath, l_newRKey, l_targetOLevel
            FROM    ibs_Object
            WHERE   oid = l_targetOid;
        EXCEPTION
            WHEN OTHERS THEN
                -- create error entry:
                l_ePos := 'fetching version info';
                RAISE;                  -- call common exception handler
        END;

        -- get max id of current running copy-transactions and add 1 to identify
        -- entries in ibs_Copy table regarding to this copy-transaction
        BEGIN
            SELECT  DECODE (MAX (copyid), NULL, 1, MAX (copyid) + 1)
            INTO    l_copyId
            FROM    ibs_Copy;
        EXCEPTION
            WHEN OTHERS THEN
                -- create error entry:
                l_ePos := 'getting maximum copyId';
                RAISE;                  -- call common exception handler
        END;

        -- now copy object AND its subobjects to target-container
        -- (write entry for each object to ibs_Copy table,
        --  will be needed to bend existing object-references)
        FOR l_containerCursorRow IN containerCursor -- another tuple found
        LOOP
            -- get the actual tuple values:
            l_oldOid := l_containerCursorRow.oid;
            l_oldContainerId := l_containerCursorRow.containerId;

            --
            -- read and create entries for new object
            --

            -- read information of object that shall be copied
            SELECT  state, tVersionId, typeName,
                    isContainer, name, containerId,
                    containerKind, isLink, linkedObjectId,
                    showInMenu,
                    B_AND (flags, B_XOR (2147483647, c_ISCHECKEDOUT)),
-- AJ HINT:
-- the right call would be B_AND (flags, B_XOR (4294967295, c_ISCHECKEDOUT))
-- but B_AND could not work with values greater then 2147483647
-- that meens the highest flag of column flags could not be used !!!
                    /*owner, oLevel,*/
                    posNo, /*posNoPath,*/ creationDate, creator,
                    /*lastChanged, changer, validUntil,*/
                    description, icon, processState,
                    consistsOfId
            INTO    l_newState, l_newTVersionId, l_newTypeName,
                    l_newIsContainer, l_newName, l_newContainerId,
                    l_newContainerKind, l_newIsLink, l_newLinkedObjectId,
                    l_newShowInMenu,
                    l_newFlags,
                    /* l_newOwner, l_newOLevel,*/
                    l_newPosNo, /*l_newPosNoPath,*/
                    l_newCreationDate, l_newCreator,
                    /*l_newLastChanged, l_newChanger, l_newValidUntil,*/
                    l_newDescription, l_newIcon, l_newprocessState,
                    l_newConsistsOfId
            FROM    ibs_Object
            WHERE   oid = l_oldOid;

            -- get id for new object:
            BEGIN
                SELECT  objectIdSeq.NEXTVAL
                INTO    l_newId
                FROM    DUAL;
            EXCEPTION
                WHEN OTHERS THEN
                    -- create error entry:
                    l_ePos := 'increasing objectIdSeq';
                    RAISE;              -- call common exception handler
            END;
            -- compute oid; convert
            l_newOid := createOid (l_newTVersionId, l_newId);

            -- read and calculate the following for the new object:
            -- * containerId
            -- * posNo
            -- * posNoPath
            -- * oLevel
            IF (l_oldOid = l_rootOid)   -- top-object (copy-object itself)
            THEN
                -- target is given target-container;
                l_newContainerId := l_targetOid;

                -- derive position number from other objects within container:
                -- The posNo is:
                -- * the actual highest posNo within container + 1
                -- * 1 if no objects within container
                BEGIN
                    SELECT  DECODE (MAX (posNo), NULL, 1, MAX (posNo) + 1)
                    INTO    l_newPosNo
                    FROM    ibs_Object
                    WHERE   containerId = l_targetOid;
                EXCEPTION
                    WHEN OTHERS THEN
                        -- create error entry:
                        l_ePos := 'getting posNo';
                        RAISE;          -- call common exception handler
                END;

                -- calculate new posNoPath:
                l_newPosNoPath := l_targetPosNoPath || intToRaw (l_newPosNo, 4);
                -- set new oLevel
                l_newOLevel := l_targetOLevel + 1;

                -- SET output value:
                -- convert oid to string:
                p_byteToString (l_newOid, ao_newOid_s);
            -- end if top-object (copy-object itself)
            ELSE                        -- any sub object
                -- reset structural-error information
                l_structuralError := 0;

                -- get/calc data out of container object in new structure!
                BEGIN
                    SELECT  c.newOid, o.posNoPath || intToRaw (l_newPosNo, 4),
                            o.oLevel + 1
                    INTO    l_newContainerId, l_newPosNoPath,
                            l_newOLevel
                    FROM    ibs_Copy c, ibs_Object o
                    WHERE   l_oldContainerId = c.oldOid
                        AND c.newOid = o.oid
                        AND c.copyId = l_copyId;
                EXCEPTION
                    WHEN OTHERS THEN
                        -- indicate structural error
                        l_structuralError := 1;
                        -- DO NOT RAISE THIS ERROR
                END;
            END IF; -- else any sub object


            --
            -- create object entries in ibs_Object and type-specific
            --

            -- check if structural error occured
            IF (l_structuralError = 0)  -- no structural error?
            THEN
                -- insert entry in ibs_Object:
                INSERT INTO ibs_Object
                       (id, oid, state, tVersionId,
                        typeName, isContainer,
                        name, containerId, containerKind,
                        isLink, linkedObjectId, showInMenu,
                        flags, owner, oLevel,
                        posNo, posNoPath,
                        creationDate, creator, lastChanged, changer,
                        validUntil, description, icon,
                        processState, rKey, consistsOfId)
                VALUES (l_newId, l_newOid, l_newState, l_newTVersionId,
                        l_newTypeName, l_newIsContainer,
                        l_newName, l_newContainerId, l_newContainerKind,
                        l_newIsLink, l_newLinkedObjectId, l_newShowInMenu,
                        l_newFlags, ai_userid, l_newOLevel,
                        l_newPosNo, l_newPosNoPath,
                        SYSDATE, ai_userId, SYSDATE, ai_userId,
                        ADD_MONTHS (SYSDATE, 3), l_newDescription, l_newIcon,
                        l_newProcessState, l_newRKey, l_newConsistsOfId);

                -- copy type-specific data of new object:
                l_retValue := p_Object$switchCopy
                    (l_oldOid, ai_userId, l_newTVersionId, l_newOid);

                -- store old and new oid of the copied object:
                BEGIN
                    INSERT INTO ibs_Copy (copyId, oldOid, newOid)
                    VALUES  (l_copyId, l_oldOid, l_newOid);
                EXCEPTION
                    WHEN OTHERS THEN
                        -- create error entry:
                        l_ePos := 'INSERT INTO ibs_Copy';
                        RAISE;          -- call common exception handler
                END;
---------
--
-- START: SHOULD NOT BE IN BASE-FUNCTIONALITY
--
---------
                -- for object-type ATTACHMENT only
                -- only if copied object (root-object) is attachment
                -- ensures that the flags and a master are set
                IF (l_oldOid = l_rootOid AND l_newTVersionId = 16842833)
                THEN
                    l_retValue :=
                        p_Attachment_01$ensureMaster (l_newContainerId, null);
                END IF;
---------
--
-- END: SHOULD NOT BE IN BASE-FUNCTIONALITY
--
---------
            -- end if no structural error
            ELSE                        -- structural error occurred
                -- create error entry:
                l_eText :=
                    'Error while reading/calc new objects structural data; ' ||
                    'possible cause of error: containerId/posNoPath inconsistency; ' ||
                    'object will not be copied -> target-structure consistency granted; ' ||
                    'oid of object = ' || l_oldOid;
                ibs_error.log_error (ibs_error.error, 'p_Object$copy', l_eText);
            END IF; -- else structural error occurred
        END LOOP;


        -- bend copied references which are linked to objects in
        -- copied sub-structure:
        BEGIN
            UPDATE  ibs_Object
            SET     linkedObjectId =
                    (   SELECT  newOid
                        FROM    ibs_Copy
                        WHERE   oldOid = linkedObjectId
                            AND copyId = l_copyId
                    )
            WHERE   oid IN
                    (   SELECT  newOid
                        FROM    ibs_Copy
                        WHERE   copyId = l_copyId
                    )
                AND linkedObjectId IN
                    (   SELECT  oldOid
                        FROM    ibs_Copy
                        WHERE   copyId = l_copyId
                    );
        EXCEPTION
            WHEN OTHERS THEN
                -- create error entry:
                l_ePos := 'updating references';
                RAISE;                  -- call common exception handler
        END;

        BEGIN
            -- copy the references:
            INSERT INTO ibs_Reference
                    (referencingOid, fieldName, referencedOid, kind)
            SELECT  c.newOid, ref.fieldName, ref.referencedOid, ref.kind
            FROM    ibs_Object o, ibs_Copy c, ibs_Object refO, ibs_Reference ref
            WHERE   c.copyId = l_copyId
                AND o.oid = c.oldOid
                AND o.oid = ref.referencingOid
                AND refO.oid = ref.referencedOid
                AND refO.state = c_ST_ACTIVE
                AND o.state = c_ST_ACTIVE;
        EXCEPTION
            WHEN OTHERS THEN
                -- create error entry:
                l_ePos := 'copy references';
                RAISE;                  -- call common exception handler
        END;

        BEGIN
            -- ensure that references to copied objects are correct:
            UPDATE  ibs_Reference
            SET     referencedOid =
                    (   SELECT  newOid
                        FROM    ibs_Copy
                        WHERE   copyId = l_copyId
                            AND oldOid = referencedOid
                    )
            WHERE   referencingOid IN
                    (   SELECT  newOid
                        FROM    ibs_Copy
                        WHERE   copyId = l_copyId
                    )
                AND referencedOid IN
                    (   SELECT  oldOid
                        FROM    ibs_Copy
                        WHERE   copyId = l_copyId
                    );
        EXCEPTION
            WHEN OTHERS THEN
                -- create error entry:
                l_ePos := 'ensure correct references';
                RAISE;                  -- call common exception handler
        END;


        -- delete all tuples of this copy action:
        BEGIN
            DELETE  ibs_Copy
            WHERE   copyId = l_copyId;
        EXCEPTION
            WHEN OTHERS THEN
                -- create error entry:
                l_ePos := 'deleting ibs_Copy entries';
                RAISE;                  -- call common exception handler
        END;
    -- if the user has the rights
    ELSE                                -- the user does not have the rights
        -- set return value:
        l_retValue := c_INSUFFICIENT_RIGHTS;
    END  IF;-- else the user does not have the rights

    -- make changes permanent:
    COMMIT WORK;

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the save point:
        ROLLBACK TO s_Object$copy;
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid_s = ' || ai_oid_s ||
            ', ai_userId = ' || ai_userId ||
            ', ai_op = ' || ai_op ||
            ', ai_targetId_s = ' || ai_targetId_s ||
            ', ao_newOid_s = ' || ao_newOid_s ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Object$copy', l_eText);
        -- return error code:
        RETURN -1;
END p_Object$copy;
/

show errors;

EXIT;
