--------------------------------------------------------------------------------
-- Creates a Catalog Object.
--
-- @version     $Id: Catalog_01Proc.sql,v 1.4 2003/10/31 00:12:49 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020829
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Catalog_01$create');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Catalog_01$create
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
    DECLARE c_NOT_OK        INT;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_linkedObjectId CHAR (8) FOR BIT DATA;
    DECLARE l_contactsOid_s VARCHAR (18);

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
    SET c_NOT_OK            = 0;

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_oid               = c_NOOID;
    SET ao_oid_s            = c_NOOID_s;

-- body:
    ---------------------------------------------------------------------------
    -- conversionS (VARCHAR (18)) - all input objectids must be converted
    ---------------------------------------------------------------------------
    CALL IBSDEV1.p_stringToByte (ai_containerId_s, l_containerId);
    CALL IBSDEV1.p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);

    ---------------------------------------------------------------------------
    -- START
    CALL IBSDEV1.p_Object$performCreate(ai_userId, ai_op, ai_tVersionId, ai_name,
        ai_containerId_s, ai_containerKind, ai_isLink, ai_linkedObjectId_s,
        ai_description, ao_oid_s, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF l_retValue = c_ALL_RIGHT THEN 
        -- Insert the other values - will be set to null
        INSERT INTO IBSDEV1.m2_Catalog_01 (oid, companyOid, ordResp,
            ordRespMed, contResp, contRespMed, locked,
            description1, description2, isorderexport, 
            connectorOid, translatoroid, filterid,
            -- special columns for datakom/mos
            notifyByEmail, subject, content)
        VALUES (l_oid, NULL, NULL, NULL, NULL, NULL, 0, ' ', 
            ' ', 0, NULL, NULL, 0, 0, NULL, NULL);
        GET DIAGNOSTICS l_rowcount = ROW_COUNT;

        -- check if insertion was performed properly:
        IF l_rowcount <= 0 THEN 
            SET l_retValue = c_NOT_OK;
        END IF;
    END IF;
    COMMIT;
    -- return the state value
    RETURN l_retValue;
END;
-- p_Catalog_01$create

--------------------------------------------------------------------------------
--
-- Changes a Catalog Object.
--
-- @version     1.00.0001, 26.08.1998
--
-- @author      Rupert Thurner   (RT)  980521
--
-- @returns
--  ALL_RIGHT               action performed, values returned, everything ok
--  INSUFFICIENT_RIGHTS     user has no right to perform action
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Catalog_01$change');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Catalog_01$change(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_validUntil       TIMESTAMP,
    IN  ai_description      VARCHAR (255),
    IN  ai_showInNews       SMALLINT,
    IN  ai_companyOid_s     VARCHAR (18),
    IN  ai_ordResp_s        VARCHAR (18),
    IN  ai_ordRespMed_s     VARCHAR (18),
    IN  ai_contResp_s       VARCHAR (18),
    IN  ai_contRespMed_s    VARCHAR (18),
    IN  ai_locked           SMALLINT,
    IN  ai_description1     VARCHAR (255),
    IN  ai_description2     VARCHAR (255),
    IN  ai_isOrderExport    SMALLINT,
    IN  ai_connectorOid_s   VARCHAR (18),
    IN  ai_translatorOid_s  VARCHAR (18),
    IN  ai_filterId         INT,
    IN  ai_notifyByEmail    SMALLINT,
    IN  ai_subject          VARCHAR (255),
    IN  ai_content          VARCHAR (255))
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    ---------------------------------------------------------------------------
    -- conversionS (VARCHAR (18)) - all input object ids must be converted
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_companyOid    CHAR (8) FOR BIT DATA;
    DECLARE l_ordResp       CHAR (8) FOR BIT DATA;
    DECLARE l_ordRespMed    CHAR (8) FOR BIT DATA;
    DECLARE l_contResp      CHAR (8) FOR BIT DATA;
    DECLARE l_contRespMed   CHAR (8) FOR BIT DATA;
    DECLARE l_connectorOid  CHAR (8) FOR BIT DATA;
    DECLARE l_translatorOid CHAR (8) FOR BIT DATA;
    ---------------------------------------------------------------------------
    -- definitions:
    -- define return constants
    DECLARE c_ALL_RIGHT     INT;
    -- define return values
    DECLARE l_retValue      INT;
    DECLARE l_sqlcode       INT DEFAULT 0;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
    CALL IBSDEV1.p_stringToByte (ai_companyOid_s, l_companyOid);
    CALL IBSDEV1.p_stringToByte (ai_ordResp_s, l_ordResp);
    CALL IBSDEV1.p_stringToByte (ai_ordRespMed_s, l_ordRespMed);
    CALL IBSDEV1.p_stringToByte (ai_contResp_s, l_contResp);
    CALL IBSDEV1.p_stringToByte (ai_contRespMed_s, l_contRespMed);
    CALL IBSDEV1.p_stringToByte (ai_connectorOid_s, l_connectorOid);
    CALL IBSDEV1.p_stringToByte (ai_translatorOid_s, l_translatorOid);
    -- set constants
    SET c_ALL_RIGHT         = 1;
    -- initialize return values
    SET l_retValue          = c_ALL_RIGHT;
-- body:
    -- perform the change of the object:
    CALL IBSDEV1.p_Object$performChange(ai_oid_s, ai_userId, ai_op, ai_name,
        ai_validUntil, ai_description, ai_showInNews);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    -- operation properly performed?
    IF l_retValue = c_ALL_RIGHT THEN 
        -- update object specific (Catalog_01) values
        UPDATE IBSDEV1.m2_Catalog_01
        SET companyOid = l_companyOid,
            ordResp = l_ordResp,
            ordRespMed = l_ordRespMed,
            contResp = l_contResp,
            contRespMed = l_contRespMed,
            locked = ai_locked,
            description1 = ai_description1,
            description2 = ai_description2,
            isOrderExport = ai_isOrderExport,
            connectorOid = l_connectorOid,
            translatorOid = l_translatorOid,
            filterId = ai_filterId,
            notifyByEmail = ai_notifyByEmail,
            subject = ai_subject,
            content = ai_content
        WHERE oid = l_oid;
    END IF;
    COMMIT;
    -- return the state value
    RETURN l_retValue;
END;
-- p_Catalog_01$change

--------------------------------------------------------------------------------
--
-- Retrieves a Catalog Object.
--
-- @version     1.00.0004, 26.08.1998
--
-- @author      Rupert Thurner   (RT)  980521
--
-- @returns
--  ALL_RIGHT               action performed, values returned, everything ok
--  INSUFFICIENT_RIGHTS     user has no right to perform action
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
-- Gets all data from a given object (incl. rights check). <BR>
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be retrieved.
-- @param   @userId             Id of the user who is getting the data.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
--
-- @output parameters:
-- @param   @state              The object's state.
-- @param   @tVersionId         ID of the object's type (correct version).
-- @param   @typeName           Name of the object's type.
-- @param   @name               Name of the object itself.
-- @param   @containerId        ID of the object's container.
-- @param   @containerName      Name of the object's container.
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
-- @param   @showInNews         flag if object should be shown in newscontainer
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
-- @param   @ordRespOid_s       oid of the ordering responsible 
-- @param   @ordRespName        name of the ordering responsibel
-- @param   @ordRespMedOid_s    oid of the medium for orderings
-- @param   @ordRespMedName     name of the medium for orderings
-- @param   @contRespOid_s      oid of the catalog responsible
-- @param   @contRespName       name of the catalog responsible
-- @param   @contRespMedOid_s   oid of the medium the the catalog responsible is reached
-- @param   @contRespMedName    name of the medium the the catalog responsible is reached
-- @param   @isOrderExport      flag if order export is activated
-- @param   @connectorOid_s     oid of the connector to be used for order export
-- @param   @translatorOid_s    oid of the translator to be used for order export 
-- @param   @filterId           id of the filter to be used
--
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Catalog_01$retrieve');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Catalog_01$retrieve(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    -- output parameters:
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
    OUT ao_companyOid       CHAR (8) FOR BIT DATA,
    OUT ao_company          VARCHAR (126),
    OUT ao_ordResp          CHAR (8) FOR BIT DATA,
    OUT ao_ordRespName      VARCHAR (63),
    OUT ao_ordRespMed       CHAR (8) FOR BIT DATA,
    OUT ao_ordRespMedName   VARCHAR (63),
    OUT ao_contResp         CHAR (8) FOR BIT DATA,
    OUT ao_contRespName     VARCHAR (63),
    OUT ao_contRespMed      CHAR (8) FOR BIT DATA,
    OUT ao_contRespMedName  VARCHAR (63),
    OUT ao_locked           SMALLINT,
    OUT ao_description1     VARCHAR (255),
    OUT ao_description2     VARCHAR (255),
    OUT ao_isOrderExport    SMALLINT,
    OUT ao_connectorOid     CHAR (8) FOR BIT DATA,
    OUT ao_connectorName    VARCHAR (63),
    OUT ao_translatorOid    CHAR (8) FOR BIT DATA,
    OUT ao_translatorName   VARCHAR (63),
    OUT ao_filterId         INT,
    OUT ao_notifyByEmail    SMALLINT,
    OUT ao_subject          VARCHAR (255),
    OUT ao_content          VARCHAR (255)
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- definitions:
    -- define return constants:
    DECLARE c_NOT_OK        INT;
    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_INSUFFICIENT_RIGHTS INT;
    DECLARE c_OBJECTNOTFOUND INT;
    -- define return values:
    DECLARE l_retValue      INT;
    -- define local variables:
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
    -- set constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_OBJECTNOTFOUND    = 3;
  
    -- initialize return values:
    SET l_retValue          = c_NOT_OK;
-- body:
    -- retrieve the base object data:
    CALL IBSDEV1.p_Object$performRetrieve(ai_oid_s, ai_userId, ai_op, ao_state,
        ao_tVersionId, ao_typeName, ao_name, ao_containerId, ao_containerName,
        ao_containerKind, ao_isLink, ao_linkedObjectId, ao_owner, ao_ownerName,
        ao_creationDate, ao_creator, ao_creatorName, ao_lastChanged,
        ao_changer, ao_changerName, ao_validUntil, ao_description,
        ao_showInNews, ao_checkedOut, ao_checkOutDate, ao_checkOutUser,
        ao_checkOutUserOid, ao_checkOutUserName, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF l_retValue = c_ALL_RIGHT THEN 
        -- retrieve object type specific data:
        SELECT companyOid, ordResp, ordRespMed, contResp,
             contRespMed, locked, description1, description2,
             isOrderExport, connectorOid, translatorOid, filterId,
             notifyByEmail, subject, content
        INTO ao_companyOid, ao_ordResp, ao_ordRespMed, ao_contResp,
            ao_contRespMed, ao_locked, ao_description1, ao_description2,
            ao_isOrderExport, ao_connectorOid, ao_translatorOid, ao_filterId,
            ao_notifyByEmail, ao_subject, ao_content
        FROM IBSDEV1.m2_Catalog_01
        WHERE oid = l_oid;

        -- get company name
        SELECT (o.name || ' ' || c.legal_form) 
        INTO ao_company
        FROM IBSDEV1.mad_Company_01 c INNER JOIN IBSDEV1.ibs_Object o ON o.oid = c.oid
        WHERE c.oid = ao_companyOid;
        -- get order responsible name
        SELECT fullname
        INTO ao_ordRespName
        FROM IBSDEV1.ibs_User
        WHERE oid = ao_ordResp;

        -- get order responsible medium name
        SELECT name
        INTO ao_ordRespMedName
        FROM IBSDEV1.ibs_Object
        WHERE oid = ao_ordRespMed;

        -- get contens responsible nam
        SELECT fullname
        INTO ao_contRespName
        FROM IBSDEV1.ibs_User
        WHERE oid = ao_contResp;

        -- get contens responsible medium name
        SELECT name
        INTO ao_contRespMedName
        FROM IBSDEV1.ibs_Object
        WHERE oid = ao_ordRespMed;

        -- get connector name
        SELECT name
        INTO ao_connectorName
        FROM IBSDEV1.ibs_Object
        WHERE oid = ao_connectorOid;

        -- get translator name
        SELECT name
        INTO ao_translatorName
        FROM IBSDEV1.ibs_Object
        WHERE oid = ao_translatorOid;
    END IF;

    COMMIT;
    -- return the state value
    RETURN l_retValue;
END;
-- p_Catalog_01$retrieve


--------------------------------------------------------------------------------
--
-- Deletes a Catalog Object.
--
-- @version     1.00.0000, 21.05.1998
--
-- @author      Rupert Thurner   (RT)  980521
--
-- @returns
--  ALL_RIGHT               action performed, values returned, everything ok
--  INSUFFICIENT_RIGHTS     user has no right to perform action
-- 
--------------------------------------------------------------------------------
-- delete old procedure
CALL IBSDEV1.p_dropProc ('p_Catalog_01$delete');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Catalog_01$delete(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    ---------------------------------------------------------------------------
    -- conversionS (VARCHAR (18)) - all input objectids must be converted
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    ---------------------------------------------------------------------------
    -- definitions:
    -- define return constants
    DECLARE c_ALL_RIGHT     INT;
    -- define return values
    DECLARE l_retValue      INT;
    -- participants container
    DECLARE l_partContId    CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
    -- set constants
    SET c_ALL_RIGHT         = 1;
    -- initialize return values
    SET l_retValue          = c_ALL_RIGHT;
-- body:
    -- perform deletion of object:
    CALL IBSDEV1.p_Object$performDelete(ai_oid_s, ai_userId, ai_op);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    -- operation properly performed?
    IF l_retValue = c_ALL_RIGHT THEN 
        -- delete object specific attributes
        DELETE FROM IBSDEV1.m2_Catalog_01
        WHERE oid = l_oid;
    END IF;
    COMMIT;
    -- return the state value
    RETURN l_retValue;
END;
-- p_Catalog_01$delete