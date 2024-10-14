--------------------------------------------------------------------------------
-- All stored procedures regarding the object table. <BR>
--
-- @version     $Id: Document_01Proc.sql,v 1.4 2003/10/31 00:12:50 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020830
--------------------------------------------------------------------------------


--------------------------------------------------------------------------------
-- Creates a new object (incl. rights check). <BR>
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
--
-- @output parameters:
-- @param   @oid_s              OID of the newly created object.
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--------------------------------------------------------------------------------
-- delete existing procedure
CALL IBSDEV1.p_dropProc ('p_Document_01$create');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Document_01$create
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
    -- output parameters:
    OUT ao_oid_s            VARCHAR (18)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_INSUFFICIENT_RIGHTS INT;
    DECLARE c_OBJECTNOTFOUND INT;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;
    DECLARE l_partofTVersionId INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_masterId      CHAR (8) FOR BIT DATA;
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_linkedObjectId CHAR (8) FOR BIT DATA;
    DECLARE l_partofId_s    VARCHAR (18);

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

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_partofTVersionId  = 16842849;
    SET l_oid               = c_NOOID;
    SET ao_oid_s            = c_NOOID_s;
    SET l_masterId          = c_NOOID;
    SET l_oid               = c_NOOID;

-- body:
    CALL IBSDEV1.p_Object$performCreate (ai_userId, ai_op, ai_tVersionId, ai_name,
        ai_containerId_s, ai_containerKind, ai_isLink,
        ai_linkedObjectId_s, ai_description, ao_oid_s, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    COMMIT;

    -- return the state value:
    RETURN l_retValue;
END;
-- p_Document_01$create


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
-- @param   @description        Description of the object.
-- @param   @showInNews         Show in news flag.
-- @param   @checkedOut         Is the object checked out?
-- @param   @checkOutDate       Date when the object was checked out
-- @param   @checkOutUser       id of the user which checked out the object
-- @param   @checkOutUserOid    Oid of the user which checked out the object
--                              is only set if this user has the right to READ
--                              the checkOut user
-- @param   @checkOutUserName   name of the user which checked out the object,
--                              is only set if this user has the right to view
--                              the checkOut-User
--
-- @returns A value representing the state of the procedure.
-- ALL_RIGHT                    Action performed, values returned, everything ok.
-- INSUFFICIENT_RIGHTS          User has no right to perform action.
-- OBJECTNOTFOUND               The required object was not found within the 
--                              database.
--------------------------------------------------------------------------------
-- delete existing procedure
CALL IBSDEV1.p_dropProc ('p_Document_01$retrieve');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Document_01$retrieve
(
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
    OUT ao_attachmentContainerId_s VARCHAR (18),
    OUT ao_masterId_s       VARCHAR (18),
    OUT ao_fileName         VARCHAR (255),
    OUT ao_url              VARCHAR (255),
    OUT ao_path             VARCHAR (255),
    OUT ao_attachmentType   INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_INSUFFICIENT_RIGHTS INT;
    DECLARE c_OBJECTNOTFOUND INT;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE SQLCODE INT;
    DECLARE l_retValue      INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_masterRights  INT;
    DECLARE l_necessaryRights INT;
    DECLARE l_masterId      CHAR (8) FOR BIT DATA;
    DECLARE l_partofTVersionId INT;
    DECLARE l_attachmentContainerId CHAR (8) FOR BIT DATA;
    DECLARE l_Dummy         INT;
    DECLARE l_sqlcode       INT DEFAULT 0;

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
    SET c_OBJECTNOTFOUND    = 3;

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_partofTVersionId  = 16842849;
    SET ao_attachmentType   = -1;
    SET ao_fileName         = '';
    SET ao_url              = '';
    SET ao_path             = '';
    SET l_masterId          = c_NOOID;
    SET l_attachmentContainerId = c_NOOID;
    SET l_oid               = c_NOOID;

-- body:
    -- retrieve the base object data:
    CALL IBSDEV1.p_Object$performRetrieve (ai_oid_s, ai_userId, ai_op, ao_state,
        ao_tVersionId, ao_typeName, ao_name, ao_containerId, ao_containerName,
        ao_containerKind, ao_isLink, ao_linkedObjectId, ao_owner,
        ao_ownerName, ao_creationDate, ao_creator, ao_creatorName,
        ao_lastChanged, ao_changer, ao_changerName, ao_validUntil,
        ao_description, ao_showInNews, ao_checkedOut, ao_checkOutDate,
        ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    IF (l_retValue = c_ALL_RIGHT)
    THEN
        -- find the attachmentcontainer of the document
        SELECT  oid
        INTO    l_attachmentContainerId
        FROM    IBSDEV1.ibs_object
        WHERE   containerId = l_oid
            AND tVersionId = l_partofTVersionId;
 
        -- ensures that in the attachment container is a master set
        CALL IBSDEV1.p_Attachment_01$ensureMaster (l_attachmentContainerId, NULL);
        -- search the actual master
        SELECT  a.oid 
        INTO    l_masterId
        FROM    IBSDEV1.ibs_Attachment_01 a, IBSDEV1.ibs_Object o
        WHERE   o.containerId = IBSDEV1.intToBinary (l_partofTVersionId)
            AND a.isMaster = 1
            AND o.oid = a.oid
            AND o.state = 2;

        -- get the necessary rights:
        SELECT  SUM (id) 
        INTO    l_necessaryRights
        FROM    IBSDEV1.ibs_Operation
        WHERE   name IN ('view', 'read');
    
        -- get rights of the user for the master attachment
        CALL IBSDEV1.p_Rights$checkRights (l_masterId, l_attachmentContainerId, ai_userId,
            l_necessaryRights, l_masterRights);
        GET DIAGNOSTICS l_masterRights = RETURN_STATUS;
    
        -- check if the user has the necessary rights
        IF (l_masterRights = l_necessaryRights)
        THEN
            -- read out the properties
            SELECT  filename, url, path, attachmentType
            INTO    ao_fileName, ao_url, ao_path, ao_attachmentType
            FROM    IBSDEV1.ibs_Attachment_01
            WHERE   oid = l_masterId;
        END IF;

        -- convert to output:
        CALL IBSDEV1.p_byteToString (l_attachmentContainerId, ao_attachmentContainerId_s);
        CALL IBSDEV1.p_byteToString (l_masterId, ao_masterId_s);
    END IF;

    COMMIT;

    -- return the state value:
    RETURN l_retValue;
END;
-- p_Document_01$retrieve


--------------------------------------------------------------------------------
-- Changes the attributes of an existing object (incl. rights check). <BR>
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be changed.
-- @param   @userId             ID of the user who is changing the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
-- @param   @name               Name of the object.
-- @param   @validUntil         Date until which the object is valid.
-- @param   @description        Description of the object.
-- @param   @showInNews         Display object in News or not.
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Document_01$change');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Document_01$change(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_validUntil       TIMESTAMP,
    IN  ai_description      VARCHAR (255),
    IN  ai_showInNews       SMALLINT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
    -- constants:
    DECLARE c_ALL_RIGHT     INT;            -- operation was o.k.
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of this procedure
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;        -- container Id of the object
    DECLARE l_tVersionId    INT;            -- tVersion Id of the object
    DECLARE l_isMaster      SMALLINT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;        -- converted input parameter
                                            -- oid_s
    DECLARE l_sqlcode       INT DEFAULT 0;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
  
    -- assign constants:
    SET c_ALL_RIGHT         = 1;
    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_oid               = c_NOOID;

-- body:
    -- perform the change of the object:
    CALL IBSDEV1.p_Object$performChange(ai_oid_s, ai_userId, ai_op, ai_name,
        ai_validUntil, ai_description, ai_showInNews, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    COMMIT;
    -- return the state value
    RETURN l_retValue;
END;
-- p_Document_01$change
