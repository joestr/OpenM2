-------------------------------------------------------------------------------
-- All stored procedures regarding the ibs_Object table which have to
-- be inserted to the DB after all the others. <BR>
--
-- @version     $Revision: 1.5 $, $Date: 2003/10/21 22:14:49 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020812
-------------------------------------------------------------------------------

-------------------------------------------------------------------------------
-- Works as a switch to idenify all BusinessObjectTypes specific copymethods.
-- The Result is the oid of the duplicated BusinessObject. <BR>
-- This procedure also copies all links pointing to this object.
--
-- @input parameters:
-- @param   ai_oid              Oid of the object to be copied.
-- @param   ai_userId           Id of the user who is copying the object.
-- @param   ai_tVersionId       The tVersionId of the object to be copied.
-- @param   ai_newOid           The oid of the newly created object.
--
-- @output parameters:
-- @return  A value representing the state of the procedure.
--  c_ALL_RIGHT             Action performed, values returned, everything ok.
--  c_NOT_OK                An error occurred.
-------------------------------------------------------------------------------

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$switchCopy');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$switchCopy
(
    -- input parameters:
    IN  ai_oid              CHAR (8) FOR BIT DATA,--OBJECTID
    IN  ai_userId           INT,--USERID
    IN  ai_tVersionId       INT,--TVERSIONID
    IN  ai_newOid           CHAR (8) FOR BIT DATA--OBJECTID
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- everything was o.k.
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- tuple not found
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE SQLCODE INT;
    DECLARE l_retValue      INT;            -- return value of function
    DECLARE l_ePos          VARCHAR (2000); -- error position description
    DECLARE l_rowCount      INT;            -- row counter
    DECLARE l_typeCode      VARCHAR (63);   -- the type code for the object
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;

-- body:
    -- get the type code:
    SELECT  t.code 
    INTO    l_typeCode
    FROM    IBSDEV1.ibs_Type t, IBSDEV1.ibs_TVersion tv
    WHERE   t.id = tv.typeId
        AND tv.id = ai_tVersionId;

--CALL IBSDEV1.logError (100, 'p_Object$switchCopy', l_sqlcode, 'typecode', 'l_retValue', l_retValue, 'l_typeCode', l_typeCode, 'ai_tVersionId', ai_tVersionId, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
    -- distuingish between the several types of objects which have own data
    -- structures:
    IF (ai_tVersionId = IBSDEV1.p_hexStringToInt ('01010051') -- attachment
        OR ai_tVersionId = IBSDEV1.p_hexStringToInt ('01016801') -- file
        OR ai_tVersionId = IBSDEV1.p_hexStringToInt ('01016901') -- hyperlink
        OR ai_tVersionId = IBSDEV1.p_hexStringToInt ('01017c01') -- DocumentTemplate
        OR ai_tVersionId = IBSDEV1.p_hexStringToInt ('01017411') -- ImportScript
        OR ai_tVersionId = IBSDEV1.p_hexStringToInt ('01014c01') -- Workflowtemplate
    )
    THEN 
        -- copy Attachment
        CALL IBSDEV1.p_Attachment_01$BOCopy(ai_oid, ai_userId, ai_newOid);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    ELSEIF (ai_tVersionId = IBSDEV1.p_hexStringToInt ('01010301') -- discussion
        OR ai_tVersionId = IBSDEV1.p_hexStringToInt ('01010a01') -- blackboard
        OR ai_tVersionId = IBSDEV1.p_hexStringToInt ('01010321') -- XMLdiscussion?
    )
    THEN 
        CALL IBSDEV1.p_Diskussion_01$BOCopy(ai_oid, ai_userId, ai_newOid);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    ELSEIF ai_tVersionId = IBSDEV1.p_hexStringToInt ('01012f01') -- Address_01
    THEN 
        CALL IBSDEV1.p_Address_01$BOCopy(ai_oid, ai_userId, ai_newOid);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    ELSEIF ai_tVersionId = IBSDEV1.p_hexStringToInt ('01012a01') -- Person_01
    THEN 
        CALL IBSDEV1.p_Person_01$BOCopy(ai_oid, ai_userId, ai_newOid);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    ELSEIF ai_tVersionId = IBSDEV1.p_hexStringToInt ('01012c01') -- Company_01
    THEN 
        CALL IBSDEV1.p_Company_01$BOCopy(ai_oid, ai_userId, ai_newOid);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    ELSEIF ai_tVersionId = IBSDEV1.p_hexStringToInt ('01010401') -- Thema_01
    THEN 
        CALL IBSDEV1.p_Beitrag_01$BOCopy(ai_oid, ai_userId, ai_newOid);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    ELSEIF ai_tVersionId = IBSDEV1.p_hexStringToInt ('01010501') -- Beitrag_01
    THEN 
        CALL IBSDEV1.p_Beitrag_01$BOCopy(ai_oid, ai_userId, ai_newOid);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    ELSEIF ai_tVersionId = IBSDEV1.p_hexStringToInt ('010100b1') -- Group_01
    THEN 
        CALL IBSDEV1.p_Group_01$BOCopy(ai_oid, ai_userId, ai_newOid);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    ELSEIF ai_tVersionId = IBSDEV1.p_hexStringToInt ('010100a1') -- User_01
    THEN 
        CALL IBSDEV1.p_User_01$BOCopy (ai_oid, ai_userId, ai_newOid);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    ELSEIF ai_tVersionId = IBSDEV1.p_hexStringToInt ('010100F1') -- Domõnen
    THEN 
        CALL IBSDEV1.p_Domain_01$BOCopy(ai_oid, ai_userId, ai_newOid);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    ELSEIF ai_tVersionId = IBSDEV1.p_hexStringToInt ('01010201') -- Termin_01
    THEN 
        CALL IBSDEV1.p_Termin_01$BOCopy(ai_oid, ai_userId, ai_newOid);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    ELSEIF ai_tVersionId = IBSDEV1.p_hexStringToInt ('01012201') -- Price_01
    THEN 
        CALL IBSDEV1.p_Price_01$BOCopy(ai_oid, ai_userId, ai_newOid);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    ELSEIF ai_tVersionId = IBSDEV1.p_hexStringToInt ('01011501') -- Product_01
    THEN 
        CALL IBSDEV1.p_Product_01$BOCopy(ai_oid, ai_userId, ai_newOid);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    ELSEIF ai_tVersionId = IBSDEV1.p_hexStringToInt ('01016b01') -- Notiz_01
    THEN 
        CALL IBSDEV1.p_Note_01$BOCopy(ai_oid, ai_userId, ai_newOid);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    ELSEIF ai_tVersionId = IBSDEV1.p_hexStringToInt ('01017e01') -- XMLViewerContainer_01
    THEN 
        CALL IBSDEV1.p_XMLViewerContainer_01$BOCopy(ai_oid, ai_userId, ai_newOid);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    ELSEIF ai_tVersionId = IBSDEV1.p_hexStringToInt ('01017501') -- XMLViewer_01
    THEN 
        CALL IBSDEV1.p_XMLViewer_01$BOCopy(ai_oid, ai_userId, ai_newOid);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    ELSEIF (ai_tVersionId = IBSDEV1.p_hexStringToInt ('01017471') -- FileConnector_01
        OR ai_tVersionId = IBSDEV1.p_hexStringToInt ('01017481') -- FTPConnector_01
        OR ai_tVersionId = IBSDEV1.p_hexStringToInt ('01017491') -- MailConnector_01
        OR ai_tVersionId = IBSDEV1.p_hexStringToInt ('010174A1') -- HTTPConnector_01
        OR ai_tVersionId = IBSDEV1.p_hexStringToInt ('010174B1') -- EDISwitchConnector_01
        OR ai_tVersionId = IBSDEV1.p_hexStringToInt ('010174C1') -- CGIScriptConnector_01
        OR ai_tVersionId = IBSDEV1.p_hexStringToInt ('010174D1') -- SAPBCXMLRFCConnector_01
    )
    THEN
        CALL IBSDEV1.p_Connector_01$BOCopy(ai_oid, ai_userId, ai_newOid);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    ELSEIF ai_tVersionId = IBSDEV1.p_hexStringToInt ('01017C01') -- DocumentTemplate_01
    THEN 
        CALL IBSDEV1.p_DocumentTemplate_01$BOCopy(ai_oid, ai_userId, ai_newOid);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    ELSEIF ai_tVersionId = IBSDEV1.p_hexStringToInt ('01010311') -- DiscussionTemplate_01
    THEN 
        CALL IBSDEV1.p_XMLDiscTemplate_01$BOCopy(ai_oid, ai_userId, ai_newOid);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    ELSEIF ai_tVersionId = IBSDEV1.p_hexStringToInt ('01017511') -- DiscXMLViewer_01
    THEN 
        CALL IBSDEV1.p_DiscXMLViewer_01$BOCopy(ai_oid, ai_userId, ai_newOid);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    ELSEIF ai_tVersionId = IBSDEV1.p_hexStringToInt ('01017F21') -- QueryCreator_01
    THEN 
        CALL IBSDEV1.p_QueryCreator_01$BOCopy(ai_oid, ai_userId,ai_newOid);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    ELSEIF ai_tVersionId = IBSDEV1.p_hexStringToInt ('01017F51') -- QueryExecutive
    THEN 
        CALL IBSDEV1.p_QueryExecutive_01$BOCopy(ai_oid, ai_userId,ai_newOid);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    ELSEIF ai_tVersionId = IBSDEV1.p_hexStringToInt ('01017451') -- Translator
    THEN 
        CALL IBSDEV1.p_Translator_01$BOCopy(ai_oid, ai_userId,ai_newOid);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    ELSEIF ai_tVersionId = IBSDEV1.p_hexStringToInt ('01017381') -- ASCIITranslator
    THEN 
        CALL IBSDEV1.p_ASCIITranslator_01$BOCopy(ai_oid, ai_userId,ai_newOid);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    ELSEIF l_typeCode = 'EDITranslator' -- EDITranslator
    THEN 
        CALL IBSDEV1.p_EDITranslator_01$BOCopy(ai_oid, ai_userId,ai_newOid);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    ELSE
        -- check if the tVersionId belongs to a m2 type defined by a document template.
        IF EXISTS (
            SELECT  oid 
            FROM    IBSDEV1.ibs_DocumentTemplate_01
            WHERE   tVersionId = ai_tVersionId
        )
        THEN 
            -- call the copy procedure for the XMLViewer object type:
            CALL IBSDEV1.p_XMLViewer_01$BOCopy (ai_oid, ai_userId, ai_newOid);
            GET DIAGNOSTICS l_retValue = RETURN_STATUS;
        ELSE
            -- select 'keine spezielle proz gefunden'
            CALL IBSDEV1.p_Debug ('p_Object$switchCopy keine spezielle proz gefunden');
        END IF;
    END IF;
    RETURN l_retValue;
END; 
-- p_Object$switchCopy


-------------------------------------------------------------------------------
-- Copies a selected BusinessObject and its childs.(incl. rights check). <BR>
-- The rightcheck is done before we start to copy. When one BO of the tree is
-- not able to be copied because of a negativ rightcheckresult, the action is
-- stopped.
--
-- @input parameters:
-- @param   ai_oid_s            ID of the rootobject to be copied.
-- @param   ai_userId           ID of the user who is copying the object.
-- @param   ai_op               Operation to be performed (used for rights
--                              check).
-- @param   ai_targetId_s       Oid of the target BusinessObjectContainer where
--                              the root object is copied to.
--
-- @output parameters:
-- @param   ao_newOid_s         The oid of the newly created object.
-- @return  A value representing the state of the procedure.
--  c_ALL_RIGHT             Action performed, values returned, everything ok.
--  c_NOT_OK                An error occurred.
-------------------------------------------------------------------------------

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Object$copy');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Object$copy
(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),--OBJECTIDSTRING
    IN  ai_userId           INT,--USERID
    IN  ai_op               INT,
    IN  ai_targetId_s       VARCHAR (18),--OBJECTIDSTRING
    -- ouput parameters:
    OUT ao_newOid_s         VARCHAR (18)--OBJECTIDSTRING
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
NEW SAVEPOINT LEVEL
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- error occured
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- the user has no rights to copy
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string
    DECLARE c_RIGHT_UPDATE  INT DEFAULT 8;
    DECLARE c_RIGHT_INSERT  INT DEFAULT 1;
    DECLARE c_ISCHECKEDOUT  INTEGER DEFAULT 16;
    DECLARE c_EMPTYPOSNOPATH VARCHAR (254) DEFAULT '0000'; -- empty posNoPath
    DECLARE c_ST_ACTIVE     INT DEFAULT 2;  -- state value of active object

    -- local variables:
    DECLARE SQLCODE INT;
    -- ... for various purposes
    DECLARE l_retValue      INT;
    DECLARE l_ePos          VARCHAR (2000); -- error position description
    DECLARE l_rowCount      INT DEFAULT 0;
    DECLARE l_rights        INT DEFAULT 0; -- RIGHTS
    DECLARE l_structuralError INT DEFAULT 0; -- indicates error in copied structure
    DECLARE l_copyId        INT DEFAULT 0;  -- id for entries in copy-table
    -- ... of target-container
    DECLARE l_targetOid     CHAR (8) FOR BIT DATA;--OBJECTID
    DECLARE l_targetPosNoPath VARCHAR (254);--POSNOPATH_VC
    DECLARE l_targetOLevel  INT DEFAULT 0;
    -- ... of object to copy (object)
    DECLARE l_rootOid       CHAR (8) FOR BIT DATA;--OBJECTID
    DECLARE l_rootPosNoPath VARCHAR (254);--POSNOPATH_VC
    DECLARE l_rootOLevel    INT DEFAULT 0;
    -- ... of new object that must be copied sub-sequently (in loop)
    DECLARE l_newId         INT DEFAULT 0;
    DECLARE l_newContainerId CHAR (8) FOR BIT DATA;--OBJECTID
    DECLARE l_newOid        CHAR (8) FOR BIT DATA;--OBJECTID
    DECLARE l_newPosNoPath  VARCHAR (254);--POSNOPATH_VC
                                            -- hex representation of posNo
    DECLARE l_newPosNo      INT DEFAULT 0;--POSNO
    DECLARE l_newPosNoHex   VARCHAR (4) DEFAULT '0000';
    DECLARE l_newOLevel     INT DEFAULT 0;
    DECLARE l_newTVersionId INT DEFAULT 0;--TVERSIONID
    DECLARE l_newTargettVersion INT DEFAULT 0;--TVERSIONID
    DECLARE l_newRights     INT DEFAULT 0;
    DECLARE l_newName       VARCHAR (63) DEFAULT '';--NAME
    DECLARE l_newDescription VARCHAR (255) DEFAULT '';--DESCRIPTION
    DECLARE l_newIcon       VARCHAR (63) DEFAULT '';--NAME
    DECLARE l_newRKey       INT DEFAULT 0;
    DECLARE l_newState      INT DEFAULT 0;
    DECLARE l_newTypeName   VARCHAR (63) DEFAULT 'UNKNOWN';--NAME
    DECLARE l_newIsContainer SMALLINT DEFAULT 0;--BOOL
    DECLARE l_newContainerKind INT DEFAULT 0;
    DECLARE l_newIsLink     SMALLINT DEFAULT 0;--BOOL
    DECLARE l_newLinkedObjectId CHAR (8) FOR BIT DATA;--OBJECTID
    DECLARE l_newShowInMenu SMALLINT DEFAULT 0;--BOOL
    DECLARE l_newFlags      INT DEFAULT 0;
    DECLARE l_newProcessState INT DEFAULT 0;
    DECLARE l_newCreationDate TIMESTAMP;
    DECLARE l_newCreator    INT DEFAULT 0;
    DECLARE l_newConsistsOfId INT DEFAULT 0;
    -- ... of old object that must be copied sub-sequently (in loop)
    DECLARE l_oldOid        CHAR (8) FOR BIT DATA;--OBJECTID
    DECLARE l_oldContainerId CHAR (8) FOR BIT DATA;--OBJECTID
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE el_oldOid_s     VARCHAR (18);
    DECLARE el_oldContainerId_s VARCHAR (18);
    DECLARE el_newOid_s     VARCHAR (18);

    -- define cursor over all copied objects incl. subobjects:
    DECLARE containerCursor CURSOR WITH HOLD FOR 
        SELECT  oid, containerId 
        FROM    IBSDEV1.ibs_Object
        WHERE   oLevel >= l_rootOLevel
            AND posNoPath LIKE l_rootPosNoPath || '%'
            AND state = c_ST_ACTIVE
        ORDER BY posNoPath ASC
        FOR READ ONLY;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
   
    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_targetOid         = c_NOOID;
    SET l_targetPosNoPath   = c_EMPTYPOSNOPATH;
    SET l_rootOid           = c_NOOID;
    SET l_rootPosNoPath     = c_EMPTYPOSNOPATH;
    SET l_newContainerId    = c_NOOID;
    SET l_newOid            = c_NOOID;
    SET l_newPosNoPath      = c_EMPTYPOSNOPATH;
    SET l_newLinkedObjectId = c_NOOID;
    SET l_newCreationDate   = CURRENT TIMESTAMP;
    SET l_oldOid            = c_NOOID;
    SET l_oldContainerId    = c_NOOID;
    SET el_oldOid_s         = c_NOOID_s;
    SET el_oldContainerId_s = c_NOOID_s;
    SET el_newOid_s         = c_NOOID_s;
    SET ao_newOid_s         = c_NOOID_s;
 
-- body:
/* KR savepoint does not work because of committed transactions within called code
    -- set a save point for the current transaction:
    SAVEPOINT s_Object_copy ON ROLLBACK RETAIN CURSORS;
*/

    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_rootOid);
    CALL IBSDEV1.p_stringToByte (ai_targetId_s, l_targetOid);

    -- read structural data of object to be copied:
    SELECT  posNoPath, oLevel
    INTO    l_rootPosNoPath, l_rootOLevel
    FROM    IBSDEV1.ibs_Object
    WHERE   oid = l_rootOid;
    
    -- any entries found?
    IF (l_sqlcode <> 0)
    THEN 
        -- create error entry:
        SET l_ePos = 'reading posNoPath';
        GOTO exception1;                -- call common exception handler
    END IF;

    -- get (maximum) rights for this user on object and all its
    -- sub-objects for given operation:
    SELECT  MIN (B_AND (rights, ai_op))
    INTO    l_rights
    FROM
    (
        SELECT  MAX (B_AND (rights, ai_op)) AS rights, oid
        FROM    IBSDEV1.v_Container$rights
        WHERE   posNoPath LIKE l_rootPosNoPath || '%'
            AND userId = ai_userId
            AND state = c_ST_ACTIVE
        GROUP BY oid
    ) AS s;

    --
    -- any entries found?
    IF (l_sqlcode <> 0)
    THEN
        -- create error entry:
        SET l_ePos = 'reading user rights';
        GOTO exception1;                -- call common exception handler
    END IF;

    -- check if the user has the necessary rights:
    IF (l_rights = ai_op)               -- the user has the rights?
    THEN
        -- read out information of the target container-object:
        SELECT  posNoPath, rKey, oLevel
        INTO    l_targetPosNoPath, l_newRKey, l_targetOLevel
        FROM    IBSDEV1.ibs_Object
        WHERE   oid = l_targetOid;
        GET DIAGNOSTICS l_rowCount = ROW_COUNT;

        IF (l_sqlcode <> 0)
        THEN
            -- create error entry:
            SET l_ePos = 'fetching version info';
            GOTO exception1;            -- call common exception handler
        END IF;

        -- specify the highest copyId which is set at the beginning of
        -- every copyprocess and is deleted at the end of the process
        SELECT  coalesce (MAX (copyId) + 1, 1) 
        INTO    l_copyId
        FROM    IBSDEV1.ibs_Copy;

        -- now copy object AND its subobjects to target-container
        -- (write entry for each object to ibs_Copy table,
        --  will be needed to bend existing object-references)

        -- insert the target and the container of the copied object:
        OPEN containerCursor;
        --
        -- get first object to be copied:
        SET l_sqlcode = 0;
        FETCH FROM containerCursor INTO l_oldOid, l_oldContainerId;

        WHILE (l_sqlcode = 0 AND l_retValue = c_ALL_RIGHT)
                                        -- another tuple found
        DO
            -- read and create entries for new object
            -- read information of object that shall be copied:
            SELECT  state, tVersionId, typeName,
                    isContainer, name, containerId,
                    containerKind, isLink, linkedObjectId,
                    showInMenu,
                    B_AND(Flags, B_XOR (2147483647, c_ISCHECKEDOUT)),
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
            FROM    IBSDEV1.ibs_Object
            WHERE   oid = l_oldOid;

            -- get id for new object:
            SELECT  coalesce (MAX (id) + 1, 1) 
            INTO    l_newId
            FROM    IBSDEV1.ibs_Object;
            -- compute oid; convert
            CALL IBSDEV1.p_createOid (l_newTVersionId, l_newId, l_newOid);

            -- read and calculate the following for the new object:
            -- * containerId
            -- * posNo
            -- * posNoPath
            -- * oLevel
--CALL IBSDEV1.p_byteToString (l_oldOid, el_oldOid_s);
--CALL IBSDEV1.logError (100, 'p_Object$copy', l_sqlcode, 'checking root object', 'l_retValue', l_retValue, 'ai_oid_s', ai_oid_s, '', 0, 'l_oldOid', el_oldOid_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
            IF (l_oldOid = l_rootOid)   -- top-object (copy-object itself)
            THEN
--CALL IBSDEV1.logError (100, 'p_Object$copy', l_sqlcode, 'root object', 'l_retValue', l_retValue, 'ai_oid_s', ai_oid_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
                -- target is given target-container;
                SET l_newContainerId = l_targetOid;

                -- derive position number from other objects within container:
                -- The new posNo is:
                -- * the actual highest posNo within container + 1
                -- * 1 if no objects in container
                SELECT  COALESCE (MAX (posNo) + 1, 1) 
                INTO    l_newPosNo
                FROM    IBSDEV1.ibs_Object
                WHERE   containerId = l_targetOid;

                -- convert the position number into hex representation:
                CALL IBSDEV1.p_intToHexString (l_newPosNo, l_newPosNoHex);
   
                -- calculate new posNoPath:
                SET l_newPosNoPath = l_targetPosNoPath || l_newPosNoHex;
                -- set new oLevel:
                SET l_newOLevel = l_targetOLevel + 1;

                -- SET output value:
                -- convert oid to string:
                CALL IBSDEV1.p_byteToString (l_newOid, ao_newOid_s);
            -- end if top-object (copy-object itself)
            ELSE                        -- any sub object
--CALL IBSDEV1.logError (100, 'p_Object$copy', l_sqlcode, 'other object', 'l_retValue', l_retValue, 'ai_oid_s', ai_oid_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
                -- reset structural-error information:
                SET l_structuralError = 0;
                -- convert the position number into hex representation:
                CALL IBSDEV1.p_IntToHexString (l_newPosNo, l_newPosNoHex);

                -- get/calc data out of container object in new structure!
                -- remark: ibs_Copy can be referenced, because this
                --         branch  can never be accessed in first iteration
                SET l_sqlcode = 0;

                SELECT  c.newOid, o.posNoPath || l_newPosNoHex, o.oLevel + 1 
                INTO    l_newContainerId, l_newPosNoPath, l_newOLevel
                FROM    IBSDEV1.ibs_Copy c, IBSDEV1.ibs_Object o
                WHERE   l_oldContainerId = c.oldOid
                    AND c.newOid = o.oid
                    AND c.copyId = l_copyId;

               -- any entries found?
                IF (l_sqlcode = 100)
                THEN
                    SET l_structuralError = 1;
                    -- DO NOT RAISE EXCEPTION
                    -- ERROR WILL BE HANDLED BELOW
                END IF;
/*
SELECT  COUNT (*)
INTO    l_rowCount
FROM    IBSDEV1.ibs_Copy c, IBSDEV1.ibs_Object o
WHERE   l_oldContainerId = c.oldOid
    AND c.newOid = o.oid
    AND c.copyId = l_copyId;
CALL IBSDEV1.logError (100, 'p_Object$copy', l_sqlcode, 'other object, rowCount', 'l_retValue', l_retValue, 'ai_oid_s', ai_oid_s, 'l_rowCount', l_rowCount, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
*/
            END IF; -- else any sub object

            -- create object entries in ibs_Object and type-specific

            -- check if structural error occured
            IF (l_structuralError = 0)  -- no structural error?
            THEN
--CALL IBSDEV1.logError (100, 'p_Object$copy', l_sqlcode, 'no structural error', 'l_retValue', l_retValue, 'ai_oid_s', ai_oid_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
                -- insert entry in ibs_Object:
                INSERT INTO IBSDEV1.ibs_Object
                       (id, oid, state, tVersionId,
                        typeName, isContainer,
                        name, containerId, containerKind,
                        isLink, linkedObjectId, showInMenu,
                        flags, owner, oLevel,
                        posNo, posNoPath,
                        creationDate, creator,
                        lastChanged, changer,
                        validUntil, description, icon,
                        processState, rKey, consistsOfId)
                VALUES (l_newId, l_newOid, l_newState, l_newTVersionId,
                        l_newTypeName, l_newIsContainer,
                        l_newName, l_newContainerId, l_newContainerKind,
                        l_newIsLink, l_newLinkedObjectId, l_newShowInMenu,
                        l_newFlags, ai_userid, l_newOLevel,
                        l_newPosNo, l_newPosNoPath,
                        CURRENT TIMESTAMP, ai_userId,
                        CURRENT TIMESTAMP, ai_userId,
                        CURRENT TIMESTAMP + 3 MONTHS,
                        l_newDescription, l_newIcon,
                        l_newProcessState, l_newRKey, l_newConsistsOfId);

                -- copy type-specific data of new object:
                CALL IBSDEV1.p_Object$switchCopy
                    (l_oldOid, ai_userId, l_newTVersionId, l_newOid);
                GET DIAGNOSTICS l_retValue = RETURN_STATUS;
--CALL IBSDEV1.p_byteToString (l_newOid, el_newOid_s);
--CALL IBSDEV1.logError (100, 'p_Object$copy', l_sqlcode, 'after switchCopy', 'l_retValue', l_retValue, 'ai_oid_s', ai_oid_s, '', 0, 'l_newOid', el_newOid_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
            
                -- store old, new oid of the copied object and the copyId:
                INSERT INTO IBSDEV1.ibs_Copy (copyId, oldOid, newOid)
                VALUES (l_copyId, l_oldOid, l_newOid);

---------
-- START: SHOULD NOT BE IN BASE-FUNCTIONALITY
---------
                -- for object-type ATTACHMENT only
                -- only if copied object (root-object) is attachment
                -- ensures that the flags and a master are set
                IF (l_oldOid = l_rootOid
                    AND l_newTVersionId =
                        IBSDEV1.p_hexStringToInt ('01010051'))
                THEN 
                    CALL IBSDEV1.p_Attachment_01$ensureMaster
                        (l_newContainerId, NULL);
                    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
--CALL IBSDEV1.logError (100, 'p_Object$copy', l_sqlcode, 'after ensureMaster', 'l_retValue', l_retValue, 'ai_oid_s', ai_oid_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');
                END IF;
---------
-- END: SHOULD NOT BE IN BASE-FUNCTIONALITY
---------
            -- end if no structural error
            ELSE                        -- structural error occurred
                 -- create error entry:
                SET l_ePos =
                    'Error while reading/calc new objects structural data; ' ||
                    'possible cause of error: containerId/posNoPath inconsistency; ' ||
                    'object will not be copied -> target-structure consistency granted';
                GOTO exception1;        -- call common exception handler
            END IF; -- else structural error occurred
 
            -- get next object to be copied:
            SET l_sqlcode = 0;
            FETCH FROM containerCursor INTO l_oldOid, l_oldContainerId;
        END WHILE;

        -- dump cursor structures:
        CLOSE containerCursor;

        -- bend copied references which are linked to objects in
        -- copied sub-structure:
        UPDATE  IBSDEV1.ibs_Object
        SET     linkedObjectId =
                (   SELECT  newOid
                    FROM    IBSDEV1.ibs_Copy
                    WHERE   oldOid = linkedObjectId
                        AND copyId = l_copyId
                )
        WHERE   oid IN
                (   SELECT  newOid
                    FROM    IBSDEV1.ibs_Copy
                    WHERE   copyId = l_copyId
                )
            AND linkedObjectId IN
                (   SELECT  oldOid
                    FROM    IBSDEV1.ibs_Copy
                    WHERE   copyId = l_copyId
                );

        -- copy the references:
        INSERT INTO ibs_Reference
                (referencingOid, fieldName, referencedOid, kind)
        SELECT  c.newOid, ref.fieldName, ref.referencedOid, ref.kind
        FROM    IBSDEV1.ibs_Object o, IBSDEV1.ibs_Copy c,
                IBSDEV1.ibs_Object refO, IBSDEV1.ibs_Reference ref
        WHERE   c.copyId = l_copyId
            AND o.oid = c.oldOid
            AND o.oid = ref.referencingOid
            AND refO.oid = ref.referencedOid
            AND refO.state = c_ST_ACTIVE
            AND o.state = c_ST_ACTIVE;

        -- ensure that references to copied objects are correct:
        UPDATE  IBSDEV1.ibs_Reference
        SET     referencedOid =
                (   SELECT  newOid
                    FROM    IBSDEV1.ibs_Copy
                    WHERE   copyId = l_copyId
                        AND oldOid = referencedOid
                )
        WHERE   referencingOid IN
                (   SELECT  newOid
                    FROM    IBSDEV1.ibs_Copy
                    WHERE   copyId = l_copyId
                )
            AND referencedOid IN
                (   SELECT  oldOid
                    FROM    IBSDEV1.ibs_Copy
                    WHERE   copyId = l_copyId
                );

        -- delete all tuples of this copy action:
        DELETE FROM IBSDEV1.ibs_Copy
        WHERE   copyId = l_copyId;
    -- if the user has the rights
    ELSE                                -- the user does not have the rights
        -- set return value:
        SET l_retValue = c_INSUFFICIENT_RIGHTS;
    END IF; -- else the user does not have the rights

    -- finish the transaction:
    COMMIT;                             -- make changes permanent

    -- return the state value
    RETURN l_retValue;

exception1:
/* KR savepoint does not work because of committed transactions within called code
    -- roll back to the save point:
    ROLLBACK TO SAVEPOINT s_Object_copy;
    -- release the savepoint:
    RELEASE SAVEPOINT s_Object_copy;
*/

    CALL IBSDEV1.p_byteToString (l_oldOid, el_oldOid_s);
    CALL IBSDEV1.p_byteToString (l_newOid, el_newOid_s);
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Object$copy', l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_oid_s', ai_oid_s,
        'ai_op', ai_op, 'ai_targetId_s', ai_targetId_s,
        'l_copyId', l_copyId, 'ao_newOid_s', ao_newOid_s,
        '', 0, 'l_oldOid', el_oldOid_s,
        '', 0, 'l_oldContainerId', el_oldContainerId_s,
        '', 0, 'l_newOid', el_newOid_s,
        'l_rootOLevel', l_rootOLevel, 'l_rootPosNoPath', l_rootPosNoPath,
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_Object$copy
