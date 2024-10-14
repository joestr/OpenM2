--------------------------------------------------------------------------------
-- All stored procedures regarding the XMLDiscussion_01 Object. <BR>
--
-- @version     $Id: XMLDiscussion_01Proc.sql,v 1.4 2003/10/31 00:12:52 klaus Exp $
--
-- @author      Marcel Samek (MS)  020910
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
 -- Gets all data from a given object (incl. rights check). <BR>
 --
 -- @input parameters:
 -- @param   oid_s              ID of the object to be changed.
 -- @param   userId             ID of the user who is creating the object.
 -- @param   op                 Operation to be performed (used for rights 
 --                              check).
 --
 -- @output parameters:
 -- @param   state              The object's state.
 -- @param   tVersionId         ID of the object's type (correct version).
 -- @param   typeName           Name of the object's type.
 -- @param   name               Name of the object itself.
 -- @param   containerId        ID of the object's container.
 -- @param   containerKind      Kind of object/container relationship.
 -- @param   isLink             Is the object a link?
 -- @param   linkedObjectId     Link if isLink is true.
 -- @param   owner              ID of the owner of the object.
 -- @param   creationDate       Date when the object was created.
 -- @param   creator            ID of person who created the object.
 -- @param   lastChanged        Date of the last change of the object.
 -- @param   changer            ID of person who did the last change to the 
 --                              object.
 -- @param   validUntil         Date until which the object is valid.
 -- @param   description        Description of the object.
 -- @param   showInNews         show in news flag.
 -- @param   checkedOut         Is the object checked out?
 -- @param   checkOutDate       Date when the object was checked out
 -- @param   checkOutUser       id of the user which checked out the object
 -- @param   checkOutUserOid    Oid of the user which checked out the object
 --                              is only set if this user has the right to READ
 --                              the checkOut user
 -- @param   checkOutUserName   name of the user which checked out the object,
 --                              is only set if this user has the right to view
 --                              the checkOut-User
 --
 -- @param   maxlevels          Maximum of the levels allowed in the discussion
 -- @param   defaultView        is always Standardview
 -- @param   refOid             oid of the template used FROM IBSDEV1.the discussion
 -- @param   refName            Name of the template used from the discussion
 -- @returns A value representing the state of the procedure.
 --  ALL_RIGHT               Action performed, values returned, everything ok.
 --  INSUFFICIENT_RIGHTS     User has no right to perform action.
 --  OBJECTNOTFOUND          The required object was not found within the 
 --                          database.
 
    -- delete procedure
CALL IBSDEV1.p_dropProc ('p_XMLDiscussion_01$retrieve');
    -- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_XMLDiscussion_01$retrieve
(
    -- input parameters:
    IN ai_oid_s             VARCHAR (18),
    IN ai_userId            INT,
    IN ai_op                INT,
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
    OUT ao_maxlevels        INT,
    OUT ao_defaultView      INT,
    OUT ao_refOid           CHAR (8) FOR BIT DATA,
    OUT ao_refName          VARCHAR (63)
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE         INT;

    -- definitions:
    -- define return constants:
    DECLARE c_ALL_RIGHT     INT;

    -- define return values:
    DECLARE ao_retValue     INT;

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
    SET c_ALL_RIGHT = 1;

    -- return value of this procedure
    -- initialize return values:
    SET ao_retValue = c_ALL_RIGHT;

    -- conversions
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

-- body:

-- retrieve the base object data:
    CALL IBSDEV1.p_Discussion_01$retrieve(ai_oid_s, ai_userId, ai_op, ao_state,
        ao_tVersionId, ao_typeName, ao_name, ao_containerId, ao_containerName,
        ao_containerKind, ao_isLink, ao_linkedObjectId, ao_owner, ao_ownerName,
        ao_creationDate, ao_creator, ao_creatorName, ao_lastChanged,
        ao_changer, ao_changerName, ao_validUntil, ao_description, ao_showInNews,
        ao_checkedOut, ao_checkOutDate, ao_checkOutUser, ao_checkOutUserOid,
        ao_checkOutUserName, ao_maxlevels, ao_defaultView);

    GET DIAGNOSTICS ao_retValue = RETURN_STATUS;
    IF ao_retValue = c_ALL_RIGHT THEN

        -- get refOid and refOid name
        SELECT refOid
        INTO ao_refOid
        FROM IBSDEV1.m2_Discussion_01
        WHERE oid = l_oid;

        SELECT name
        INTO ao_refName
        FROM IBSDEV1.ibs_Object
        WHERE oid = ao_refOid;
    END IF;

    COMMIT;

    RETURN ao_retValue;
END;




--------------------------------------------------------------------------------
 -- Checks if there are templates to create a new XMLDiscussion
 -- (incl. rights check). <BR>
 --
 -- @input parameters:
 -- @param   ai_op                   Operation to be performed (used for rights
 --                                  check).
 -- @param   ai_userId               ID of the user who is deleting the object.
 --
 -- @output parameters:
 -- @param   ao_count                a counter of templates which the user is
 --                                  allowed to use.
 --
 -- @returns A value representing the state of the procedure.
 --  ALL_RIGHT               Action performed, values returned, everything ok.
 
    -- delete procedure
CALL IBSDEV1.p_dropProc ('p_XMLDiscussion_01$checkTempl');
    -- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_XMLDiscussion_01$checkTempl
(
    -- input parameters:
    IN ai_userId            INT,
    IN ai_op                INT,
    -- output parameters:
    OUT ao_count            INT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE         INT;

    -- constants:
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    -- local variables:
    DECLARE l_retValue      INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;


    -- return value of this procedure
    -- assign constants:
    SET c_ALL_RIGHT = 1;

    -- initialize local variables and return values:
    SET l_retValue = c_ALL_RIGHT;

-- body:
    -- gets all templates which can be used for Discussions
    SELECT COUNT(*)
    INTO ao_count
    FROM v_Container$rights AS v
    WHERE B_AND(v.rights, ai_op) = ai_op AND
          v.userId = ai_userId AND
          v.tVersionId = 16843537;

    -- XMLDiscussionTemplate
    -- return the state value:
  RETURN l_retValue;
END;





--------------------------------------------------------------------------------
 -- Changes the attributes of an existing object (incl. rights check). <BR>
 --
 -- @input parameters:
 -- @param   oid_s                   ID of the object to be changed.
 -- @param   userId                  ID of the user who is creating the object.
 -- @param   op                      Operation to be performed (used for rights 
 --                                  check).
 -- @param   name                    Name of the object.
 -- @param   validUntil              Date until which the object is valid.
 -- @param   description             Description of the object.
 -- @param   showInNews              Show in news flag.
 -- @param   refOid_s                Reference-oid of the template used.
 --
 -- @output parameters:
 -- @returns A value representing the state of the procedure.
 --  ALL_RIGHT                       Action performed, values returned,
 --                                  everything ok.
 --  INSUFFICIENT_RIGHTS             User has no right to perform action.
 --  OBJECTNOTFOUND                  The required object was not found within
 --                                  the database.
 
    -- delete procedure
CALL IBSDEV1.p_dropProc ('p_XMLDiscussion_01$change');
    -- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_XMLDiscussion_01$change
(
    -- input parameters:
    IN ai_oid_s             VARCHAR (18),
    IN ai_userId            INT,
    IN ai_op                INT,
    IN ai_name              VARCHAR (63),
    IN ai_validUntil        TIMESTAMP,
    IN ai_description       VARCHAR (255),
    IN ai_showInNews        SMALLINT,
    IN ai_maxlevels         INT,
    IN ai_defaultView       INT,
    IN ai_refOid_s          VARCHAR (18)
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE         INT;

    -- definitions:
    -- define return constants:

    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_INSUFFICIENT_RIGHTS INT;
    DECLARE c_OBJECTNOTFOUND INT;

    -- define return values:
    DECLARE ao_retValue     INT;

    -- define local variables:
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_refOid        CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- set constants:
    SET c_ALL_RIGHT = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_OBJECTNOTFOUND = 3;

    -- return value of this procedure
    -- initialize return values:
    SET ao_retValue = c_ALL_RIGHT;

    -- converts
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
    CALL IBSDEV1.p_stringToByte (ai_refOid_s, l_refOid);

-- body:
    -- perform the change of the object:
    CALL IBSDEV1.p_Discussion_01$change(ai_oid_s, ai_userId, ai_op, ai_name,
        ai_validUntil, ai_description, ai_showInNews, ai_maxlevels,
        ai_defaultView);
    GET DIAGNOSTICS ao_retValue = RETURN_STATUS;
    IF ao_retValue = c_ALL_RIGHT THEN

        -- update the other values
        UPDATE IBSDEV1.m2_Discussion_01
        SET refOid = l_refOid
        WHERE oid = l_oid;
    END IF;

    -- return the state value:

    RETURN ao_retValue;
END;
