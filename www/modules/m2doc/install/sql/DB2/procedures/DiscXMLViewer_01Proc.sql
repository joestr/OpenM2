--------------------------------------------------------------------------------
-- All stored procedures regarding the DiscXMLViewer_01 Object. <BR>
--
-- @version     $Id: DiscXMLViewer_01Proc.sql,v 1.4 2003/10/31 00:12:50 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020830
--------------------------------------------------------------------------------


--------------------------------------------------------------------------------
-- Creates a new DiscXMLViewer_01 Object (incl. rights check). <BR>
--
-- @input parameters:
-- @param   @userId             ID of the user who is creating the object.
-- @param   @op                 Operation to be performed (used for rights 
--                              check).
-- @param   @tVersionId         Type of the new object.
-- @param   @name               Name of the object.
-- @param   @containerId_s      ID of the container where object shall be 
--                              created in.
-- @param   @containerKind      Kind of object/container relationship
-- @param   @isLink             Defines if the object is a link
-- @param   @linkedObjectId_s   If the object is a link this is the ID of the
--                              where the link shows to.
-- @param   @description        Description of the object.
-- @param   @templateOid_s      oid of the template this object uses
-- @param   @wfTemplateOid_s    oid of the workflow this object uses
--
-- @output parameters:
-- @param   @oid_s              OID of the newly created object.
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_DiscXMLViewer_01$create');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_DiscXMLViewer_01$create
(
    -- input parameters:
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_tVersionId       INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_containerId_s    VARCHAR (18),
    IN  ai_containerKind    INT,
    IN  ai_isLink           SMALLINT,
    IN  ai_linkedObjectId_s VARCHAR (18),
    IN  ai_description      VARCHAR (255),
    -- xml viewer specific input parameters:
    --@templateOid_s  OBJECTIDSTRING,
    --@wfTemplateOid_s OBJECTIDSTRING,
    -- output parameters:
    OUT ao_oid_s            VARCHAR (18)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_INSUFFICIENT_RIGHTS INT;
    DECLARE c_ALREADY_EXISTS INT;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_posNoPath     VARCHAR (254);
    DECLARE l_discussionId  CHAR (8) FOR BIT DATA;
    DECLARE l_rights        INT;
    DECLARE l_actRights     INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_linkedObjectId CHAR (8) FOR BIT DATA;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- set constants:
    SET c_ALL_RIGHT         = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_ALREADY_EXISTS    = 21;
  
    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_oid               = c_NOOID;
    SET ao_oid_s            = c_NOOID_s;
  
-- body:
    -- conversions (objectidstring) - all input objectids must be converted
    CALL IBSDEV1.p_stringToByte (ai_containerId_s, l_containerId);
    CALL IBSDEV1.p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);

    -- create base object:
    CALL IBSDEV1.p_XMLViewer_01$create(ai_userId, ai_op, ai_tVersionId, ai_name,
        ai_containerId_s, ai_containerKind, ai_isLink, ai_linkedObjectId_s,
        ai_description, ao_oid_s);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    IF l_retValue = c_ALL_RIGHT THEN 
        -- conversions
        CALL IBSDEV1.p_stringToByte (ao_oid_s, l_oid);
        -- retrieve the posNoPath of the entry

        SELECT posNoPath
        INTO l_posNoPath
        FROM IBSDEV1.ibs_Object
        WHERE oid = l_oid;
        -- retrieve the oid of the discussion
        SELECT oid
        INTO l_discussionId
        FROM IBSDEV1.ibs_Object
        WHERE tVersionId = 16843553
              AND l_posNoPath LIKE posNoPath || '%';
 
        -- insert the other values
        INSERT INTO IBSDEV1.m2_Article_01 (oid, content, discussionId)
        VALUES( l_oid, ai_description, l_discussionid );
    END IF;
    COMMIT;
  
    -- return the state value:
    RETURN l_retValue;
END;

--------------------------------------------------------------------------------
-- Gets all data from a given object (incl. rights check). <BR>
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be changed.
-- @param   @userId             ID of the user who is creating the object.
-- @param   @op                 Operation to be performed (used for rights 
--                              check).
--
-- @output parameters:
-- @param   @state              The object's state.
-- @param   @tVersionId         ID of the object's type (correct version).
-- @param   @typeName           Name of the object's type.
-- @param   @name               Name of the object itself.
-- @param   @containerId        ID of the object's container.
-- @param   @containerKind      Kind of object/container relationship.
-- @param   @isLink             Is the object a link?
-- @param   @linkedObjectId     Link if isLink is true.
-- @param   @owner              ID of the owner of the object.
-- @param   @creationDate       Date when the object was created.
-- @param   @creator            ID of person who created the object.
-- @param   @lastChanged        Date of the last change of the object.
-- @param   @changer            ID of person who did the last change to the 
--                              object.
-- @param   @validUntil         Date until which the object is valid.
-- @param   @showInNews         show in news flag.
-- @param   @checkedOut         Is the object checked out?
-- @param   @checkOutDate       Date when the object was checked out
-- @param   @checkOutUser       id of the user which checked out the object
-- @param   @checkOutUserOid    Oid of the user which checked out the object
--                              is only set if this user has the right to READ
--                              the checkOut user
-- @param   @checkOutUserName   name of the user which checked out the object,
--                              is only set if this user has the right to view
--                              the checkOut-User
-- @param   @ao_templateOid     Oid of the template this object uses.
-- @param   @ao_wfTemplateOid_s oid of the workflow this object uses
-- @param   @ao_dbMapped        Object is dbmapped
-- @param   @ao_systemDisplayMode  should the system attributes be shown
-- @param   @level              Discussionlevel which this object is in
-- @param   @hasSubEntries      Number of the subentries the object(entry) has
-- @param   @rights             rights this user has on this object
-- @param   @discussionId       Oid of the discussion this object is pertinent to
--
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the 
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_DiscXMLViewer_01$retrieve');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_DiscXMLViewer_01$retrieve(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    -- output parameters
    OUT ao_state            INT,
    OUT ao_tVersionId       INT,
    OUT ao_typeName         VARCHAR (63),
    OUT ao_name             VARCHAR (63),
    OUT ao_containerId      CHAR (8) FOR BIT DATA,
    OUT ao_containerName    VARCHAR (63),
    OUT ao_containerKind    INT,
    OUT ao_isLink           SMALLINT,
    OUT ao_linkedObjectId   CHAR (8) FOR BIT DATA,
    OUT ao_owner            INT,
    OUT ao_ownerName        VARCHAR (63),
    OUT ao_creationDate     TIMESTAMP,
    OUT ao_creator          INT,
    OUT ao_creatorName      VARCHAR (63),
    OUT ao_lastChanged      TIMESTAMP,
    OUT ao_changer          INT,
    OUT ao_changerName      VARCHAR (63),
    OUT ao_validUntil       TIMESTAMP,
    OUT ao_description      VARCHAR (255),
    OUT ao_showInNews       SMALLINT,
    OUT ao_checkedOut       SMALLINT,
    OUT ao_checkOutDate     TIMESTAMP,
    OUT ao_checkOutUser     INT,
    OUT ao_checkOutUserOid  CHAR (8) FOR BIT DATA,
    OUT ao_checkOutUserName VARCHAR (63),
    OUT ao_templateOid      CHAR (8) FOR BIT DATA,
    OUT ao_wfTemplateOid    CHAR (8) FOR BIT DATA,
    OUT ao_systemDisplayMode INT,
    OUT ao_dbMapped         SMALLINT,
    OUT ao_showDOMTree      SMALLINT,
    OUT ao_level            INT,
    OUT ao_hasSubEntries    INT,
    OUT ao_rights           INT,
    OUT ao_discussionId     CHAR (8) FOR BIT DATA
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- definitions:
    -- define return constants:
    DECLARE c_ALL_RIGHT     INT;
    -- define return values:
    DECLARE l_retValue      INT;
    -- define local variables:
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_olevel        INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- set constants:
    SET c_ALL_RIGHT         = 1;
  
    -- initialize return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_olevel            = -1;
    -- conversions
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
-- body:
    -- retrieve the base object data:
    CALL IBSDEV1.p_XMLViewer_01$retrieve(ai_oid_s, ai_userId, ai_op, ao_state,
        ao_tVersionId, ao_typeName, ao_name, ao_containerId, ao_containerName,
        ao_containerKind, ao_isLink, ao_linkedObjectId, ao_owner, ao_ownerName,
        ao_creationDate, ao_creator, ao_creatorName, ao_lastChanged,
        ao_changer, ao_changerName, ao_validUntil, ao_description,
        ao_showInNews, ao_checkedOut, ao_checkOutDate, ao_checkOutUser,
        ao_checkOutUserOid, ao_checkOutUserName, ao_templateOid, ao_wfTemplateOid,
        ao_systemDisplayMode, ao_dbMapped, ao_showDOMTree);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- retrieve the olevel of the entry
    SELECT o.olevel, discussionId
    INTO l_olevel, ao_discussionId
    FROM IBSDEV1.ibs_Object o INNER JOIN IBSDEV1.m2_Article_01 b ON o.oid = b.oid
        WHERE o.oid = l_oid;

    -- compute the level of the entry
    SELECT l_oLevel - olevel
    INTO ao_level
    FROM IBSDEV1.ibs_Object
    WHERE oid = ao_discussionId;

    -- retrieve if entry has subEntries
    SELECT COUNT(*) 
    INTO ao_hasSubEntries
    FROM IBSDEV1.ibs_Object
    WHERE tVersionId IN (16872721)
        AND containerId = l_oid
        AND state = 2;

   -- retrieve the rights of the user for this entry
    CALL IBSDEV1.p_Rights$getRights(l_oid, ai_userId, ao_rights);
    COMMIT;
  
    -- return the state value:
    RETURN l_retValue;
END;

-- p_DiscXMLViewer_01$retrieve

--------------------------------------------------------------------------------
-- Deletes a DiscXMLViewer_01 object and all its values (incl. rights check). <BR>
-- This procedure also delets all links showing to this object.
-- 
-- @input parameters:
-- @param   @oid_s              ID of the object to be deleted.
-- @param   @userId             ID of the user who is deleting the object.
-- @param   @op                 Operation to be performed (used for rights 
--                              check).
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the 
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_DiscXMLViewer_01$delete');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_DiscXMLViewer_01$delete(
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- conversions (objectidstring) - all input objectids must be converted
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    -- definitions:
    -- define return constants:
    DECLARE c_INSUFFICIENT_RIGHTS INT;
    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_OBJECTNOTFOUND INT;
    -- define return values:
    DECLARE l_retValue      INT;
    -- define local variables:
    -- initialize local variables:
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
    -- set constants:
    SET c_ALL_RIGHT         = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_OBJECTNOTFOUND    = 3;
  
    -- initialize return values:
    SET l_retValue          = c_ALL_RIGHT;
  
    -- conversions
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
-- body:
    -- delete base object:
    CALL IBSDEV1.p_XMLViewer_01$delete(ai_oid_s, ai_userId, ai_op);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    IF l_retValue = c_ALL_RIGHT THEN 
        -- delete object type specific data:
        -- (deletes all type specific tuples which are not within ibs_Object)
        DELETE FROM IBSDEV1.m2_Article_01
        WHERE oid = l_oid;
    END IF;
  
    COMMIT;
    -- return the state value:
    RETURN l_retValue;
END;
-- p_DiscXMLViewer_01$delete

--------------------------------------------------------------------------------
-- Copy an object and all its values. <BR>
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be deleted.
-- @param   @userId             ID of the user who is deleting the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_DiscXMLViewer_01$BOCopy');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_DiscXMLViewer_01$BOCopy(
    IN  ai_oid              CHAR (8) FOR BIT DATA,
    IN  ai_userId           INT,
    IN  ai_newOid           CHAR (8) FOR BIT DATA
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- definitions:
    -- define return constants:
    DECLARE c_NOT_OK        INT;
    DECLARE c_ALL_RIGHT     INT;
    -- define return values:
    DECLARE l_retValue      INT;
    DECLARE l_path          VARCHAR (255);
    DECLARE l_oid_s         VARCHAR (18);
    DECLARE l_discussionId  CHAR (8) FOR BIT DATA;
    DECLARE l_posNoPath     VARCHAR (254);
    DECLARE l_copiedDisc    SMALLINT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
  
    -- set constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
  
    -- initialize return values:
    SET l_retValue          = c_NOT_OK;
    SET l_copiedDisc        = 0;
    -- conversionS (BINARY(8)) 
    CALL IBSDEV1.p_byteToString (ai_oid, l_oid_s);

    SELECT value || 'upload/files/'
    INTO l_path
    FROM IBSDEV1.ibs_System
    WHERE name = 'WWW_BASE_PATH';

    -- make an insert for all type specific tables:
    INSERT INTO IBSDEV1.ibs_Attachment_01 
        (oid, filename, url, fileSize, path, attachmentType, isMaster)
    SELECT  ai_newOid, b.filename, b.url, b.fileSize,
        l_path || l_oid_s || SUBSTR(l_path, length(l_path), 1),
        b.attachmentType, b.isMaster 
    FROM IBSDEV1.   ibs_Attachment_01 b
    WHERE   b.oid = ai_oid;

    COMMIT;

    GET DIAGNOSTICS l_rowcount = ROW_COUNT;
    -- check if insert was performed correctly:
    IF l_rowcount >= 1 THEN 
        SET l_retValue = c_ALL_RIGHT;
    END IF;

    IF l_retValue = c_ALL_RIGHT THEN 
        -- retrieve the posNoPath of the entry
        SELECT posNoPath
        INTO l_posNoPath
        FROM IBSDEV1.ibs_Object
        WHERE oid = ai_newOID;
 
        -- retrieve the oid of the discussion
        SELECT oid
        INTO l_discussionId
        FROM IBSDEV1.ibs_Object
        WHERE tVersionId = 16843553
             AND l_posNoPath LIKE posNoPath || '%';

        SELECT COUNT(*) 
        INTO l_copiedDisc
        FROM IBSDEV1.ibs_Copy
        WHERE oldOid = l_discussionId;

        IF l_copiedDisc = 1 THEN 
            SELECT newOid
            INTO l_discussionId
            FROM IBSDEV1.ibs_Copy
            WHERE oldOid = l_discussionId;
        END IF;
        -- make an insert for all type specific tables:
        INSERT INTO IBSDEV1.m2_Article_01 
            (oid, content, discussionId)
        SELECT  ai_newOid, b.content, l_discussionId
        FROM IBSDEV1.   m2_Article_01 b
        WHERE   b.oid = ai_oid;

        GET DIAGNOSTICS l_rowcount = ROW_COUNT;
        COMMIT;
        -- check if insert was performed correctly:
        IF l_rowcount >= 1 THEN 
            SET l_retValue = c_ALL_RIGHT;
        END IF;
    END IF;

    -- return the state value:
    RETURN l_retValue;
END;
-- p_DiscXMLViewer_01$BOCopy