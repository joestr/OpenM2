--------------------------------------------------------------------------------
-- All stored procedures regarding the XMLDiscussionTemplate_01 Object. <BR>
--
-- @version     $Id: XMLDiscussionTemplateProc.sql,v 1.4 2003/10/31 00:12:52 klaus Exp $
--
-- @author      Marcel Samek (MS)  020910
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
 -- Gets all data from a given object (incl. rights check). <BR>
 --
 -- @input parameters:
 -- @param   ai_oid_s                ID of the object to be changed.
 -- @param   ai_userId               ID of the user who is creating the object.
 -- @param   ai_op                   Operation to be performed (used for rights 
 --                                  check).
 --
 -- @output parameters:
 -- @param   ao_state                The object's state.
 -- @param   ao_tVersionId           ID of the object's type (correct version).
 -- @param   ao_typeName             Name of the object's type.
 -- @param   ao_name                 Name of the object itself.
 -- @param   ao_containerId          ID of the object's container.
 -- @param   ao_containerName        Name of the object's container.
 -- @param   ao_containerKind        Kind of object/container relationship.
 -- @param   ao_isLink               Is the object a link?
 -- @param   ao_linkedObjectId       Link if isLink is true.
 -- @param   ao_owner                ID of the owner of the object.
 -- @param   ao_ownerName            Name of the owner of the object.
 -- @param   ao_creationDate         Date when the object was created.
 -- @param   ao_creator              ID of person who created the object.
 -- @param   ao_creatorName          Name of person who created the object.
 -- @param   ao_lastChanged          Date of the last change of the object.
 -- @param   ao_changer              ID of person who did the last change to the
 --                                  object.
 -- @param   ao_changerName          Name of person who did the last change to
 --                                  the object.
 -- @param   ao_validUntil           Date until which the object is valid.
 -- @param   ai_description          Description of the object.
 -- @param   ao_showInNews           show in news flag.
 -- @param   ao_checkedOut           Is the object checked out?
 -- @param   ao_checkOutDate         Date when the object was checked out
 -- @param   ao_checkOutUser         id of the user which checked out the object
 -- @param   ao_checkOutUserOid      Oid of the user which 
 --                                  checked out the object
 --                                  is only set if this user has the right to
 --                                  READ the checkOut user
 -- @param   ao_checkOutUserName     name of the user which checked out the
 --                                  object, is only set if this user has the
 --                                  right to view the checkOut-User
 -- @param   ao_level1               oid of the first level template
 -- @param   ao_level1Name           Name of the first level template
 -- @param   ao_level2               oid of the second level template
 -- @param   ao_level2Name           Name of the second level template
 -- @param   ao_level3               oid of the third level template
 -- @param   ao_level3Name           Name of the third level template
 -- @param   ao_numberOfReferences   The number of domains where 
 --                                  this domain scheme
 --                                  is used.
 --
 -- @returns A value representing the state of the procedure.
 --  c_ALL_RIGHT                     Action performed, values returned,
 --                                  everything ok.
 --  c_INSUFFICIENT_RIGHTS           User has no right to perform action.
 --  c_OBJECTNOTFOUND                The required object was not found within
 --                                  the database.
 
    -- delete procedure
CALL IBSDEV1.p_dropProc ('p_XMLDiscTemplate_01$retrieve');
    -- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_XMLDiscTemplate_01$retrieve
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
    OUT ao_level1           CHAR (8) FOR BIT DATA,
    OUT ao_level1Name       VARCHAR (63),
    OUT ao_level2           CHAR (8) FOR BIT DATA,
    OUT ao_level2Name       VARCHAR (63),
    OUT ao_level3           CHAR (8) FOR BIT DATA,
    OUT ao_level3Name       VARCHAR (63),
    OUT ao_numberOfReferences   INT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- reutrn value of this procedure
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- oid of the object
    -- assign constants:
    SET c_NOT_OK = 0;
    SET c_ALL_RIGHT = 1;

    -- initialize local variables and return values:
    SET l_retValue = c_ALL_RIGHT;
    SET l_oid = c_NOOID;

-- body:

    -- retrieve the base object data:
    -- p_Object$performCreate will set @l_retValue to @c_ALL_RIGHT,
    -- @c_INSUFFICIENT_RIGHTS or @c_OBJECTNOTFOUND
    CALL IBSDEV1.p_Object$performRetrieve(ai_oid_s, ai_userId, ai_op,
        ao_state, ao_tVersionId, ao_typeName, ao_name, ao_containerId,
        ao_containerName, ao_containerKind, ao_isLink, ao_linkedObjectId,
        ao_owner, ao_ownerName, ao_creationDate, ao_creator, ao_creatorName,
        ao_lastChanged, ao_changer, ao_changerName, ao_validUntil,
        ao_description, ao_showInNews, ao_checkedOut, ao_checkOutDate,
        ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName, l_oid);

    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    IF l_retValue = c_ALL_RIGHT THEN

        -- retrieve the 3 oids of the levels
        SET l_sqlcode = 0;

        SELECT level1, level2, level3
        INTO ao_level1, ao_level2, ao_level3
        FROM IBSDEV1.m2_XMLDiscussionTemplate_01
        WHERE oid = l_oid;

        -- check if retrieve was performed properly, 100 means 0 rows in select
        IF l_sqlcode <> 100 THEN

            -- retrieve the 3 names of the levels
            SELECT name
            INTO ao_level1Name
            FROM IBSDEV1.ibs_Object
            WHERE oid = ao_level1;

            -- retrieve the 3 names of the levels
            SELECT name
            INTO ao_level2Name
            FROM IBSDEV1.ibs_Object
            WHERE oid = ao_level2;

            -- retrieve the 3 names of the levels
            SELECT name
            INTO ao_level3Name
            FROM IBSDEV1.ibs_Object
            WHERE oid = ao_level3;

            -- get the number of objects where this template is used:
            SELECT COUNT(*)
            INTO ao_numberOfReferences
            FROM IBSDEV1.m2_Discussion_01 AS d, IBSDEV1.ibs_Object AS o
            WHERE d.oid = o.oid AND o.state = 2 AND
                d.refOid = l_oid;
        ELSE
            -- no row affected
            SET l_retValue = c_NOT_OK;
        END IF;
    END IF;

    -- if object changed successfully
    COMMIT;
    -- return the state value:
    RETURN l_retValue;
END;



--------------------------------------------------------------------------------
 -- Deletes a XMLDisctemplate_01 object and all 
 -- its values (incl. rights check). <BR>
 -- This procedure also delets all links showing to this object.
 -- 
 -- @input parameters:
 -- @param   ai_oid_s                ID of the object to be deleted.
 -- @param   ai_userId               ID of the user who is deleting the object.
 -- @param   ai_op                   Operation to be performed (used for rights 
 --                                  check).
 --
 -- @output parameters:
 --
 -- @returns A value representing the state of the procedure.
 --  c_ALL_RIGHT                     Action performed, values returned,
 --                                  everything ok.
 --  c_INSUFFICIENT_RIGHTS           User has no right to perform action.
 --  c_OBJECTNOTFOUND                The required object was not found within
 --                                  the database.
 --  c_DEPENDENT_OBJECT_EXISTS       When there are still objects 
 --                                  which depend on
 --                                  this template
 
    -- delete procedure
CALL IBSDEV1.p_dropProc ('p_XMLDiscTemplate_01$delete');
    -- create the new procedure
CREATE PROCEDURE IBSDEV1.p_XMLDiscTemplate_01$delete
(
    IN ai_oid_s             VARCHAR (18),
    IN ai_userId            INT,
    IN ai_op                INT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE         INT;

    -- constants:
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_DEPENDENT_OBJECT_EXISTS INT;  -- value of the exception1
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE l_retValue      INT;            -- reutrn value of this procedure
    DECLARE l_oid           CHAR (8) FOR BIT DATA; -- converted input parameter
    DECLARE l_containerId   CHAR (8) FOR BIT DATA; -- oid of the container
    DECLARE l_count         INT;
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- counter for select
    -- assign constants:
    SET c_ALL_RIGHT = 1;
    SET c_DEPENDENT_OBJECT_EXISTS = 61;

    -- initialize local variables and return values:
    SET l_retValue = c_ALL_RIGHT;
    SET l_oid = c_NOOID;
    SET l_containerId = c_NOOID;
    SET l_count = 0;

-- body:

    -- conversions (objectidstring) - all input objectids must be converted
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

    -- check if there are still objects which depends on this template
    SELECT COUNT(*)
    INTO l_count
    FROM IBSDEV1.m2_Discussion_01 AS d
        INNER JOIN IBSDEV1.ibs_Object AS o
        ON d.refOid = l_oid AND d.oid = o.oid AND
        state = 2;

    IF l_count < 1 THEN
        -- delete base object:
        -- p_Object$performCreate will set @l_retValue to @c_ALL_RIGHT,
        -- @c_INSUFFICIENT_RIGHTS or @c_OBJECTNOTFOUND
        CALL IBSDEV1.p_Object$performDelete(ai_oid_s, ai_userId, ai_op, l_oid);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    ELSE
        -- there are objects which referencing on this template
        SET l_retValue = c_DEPENDENT_OBJECT_EXISTS;
    END IF;
    COMMIT;

    -- return the state value:
    RETURN l_retValue;
END;





--------------------------------------------------------------------------------
 -- Creates a new XMLDiscussionTemplate_01 Object (incl. rights check). <BR>
 --
 -- @input parameters:
 -- @param   ai_userId               ID of the user who is creating the object.
 -- @param   ai_op                   Operation to be performed (used for rights 
 --                                  check).
 -- @param   ai_tVersionId           Type of the new object.
 -- @param   ai_name                 Name of the object.
 -- @param   ai_containerId_s        ID of the container where object shall be 
 --                                  created in.
 -- @param   ai_containerKind        Kind of object/container relationship
 -- @param   ai_isLink               Defines if the object is a link
 -- @param   ai_linkedObjectId_s     If the object is a link this is the ID of
 --                                  the where the link shows to.
 -- @param   ai_description          Description of the object.
 --
 -- @output parameters:
 -- @param   ao_oid_s                OID of the newly created object.
 --
 -- @return  A value representing the state of the procedure.
 --  c_ALL_RIGHT                     Action performed, values returned,
 --                                  everything ok.
 --  c_INSUFFICIENT_RIGHTS           User has no right to perform action.
 --  c_NOT_OK                        Something went wrong.

    -- delete procedure
CALL IBSDEV1.p_dropProc ('p_XMLDiscTemplate_01$create');
    -- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_XMLDiscTemplate_01$create
(
    -- input parameters:
    IN ai_userId            INT,
    IN ai_op                INT,
    IN ai_tVersionId        INT,
    IN ai_name              VARCHAR (63),
    IN ai_containerId_s     VARCHAR (18),
    IN ai_containerKind     INT,
    IN ai_isLink            SMALLINT,
    IN ai_linkedObjectId_s  VARCHAR (18),
    IN ai_description       VARCHAR (255),
    -- output parameters:
    OUT ao_oid_s            VARCHAR (18)
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE l_retValue      INT;            -- reutrn value of this procedure
    DECLARE l_oid           CHAR (8) FOR BIT DATA; -- oid of the object
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
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
    SET ao_oid_s            = c_NOOID_s;

-- body:
    -- create base object:
    -- p_Object$performCreate will set @l_retValue to @c_ALL_RIGHT,
    -- @c_INSUFFICIENT_RIGHTS or @c_NOT_OK
    CALL IBSDEV1.p_Object$performCreate(ai_userId, ai_op, ai_tVersionId,
        ai_name, ai_containerId_s, ai_containerKind, ai_isLink,
        ai_linkedObjectId_s, ai_description, ao_oid_s, l_oid);

    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    IF l_retValue = c_ALL_RIGHT THEN

    -- insert the other values
        INSERT INTO IBSDEV1.m2_XMLDiscussionTemplate_01(oid, level1, level2, level3)
        VALUES (l_oid, c_NOOID, c_NOOID, c_NOOID);
    END IF;

    -- if object created successfully
    COMMIT;

    -- return the state value:
    RETURN l_retValue;
END;




--------------------------------------------------------------------------------
 -- Changes the attributes of an existing object (incl. rights check). <BR>
 --
 -- @input parameters:
 -- @param   ai_oid_s                ID of the object to be changed.
 -- @param   ai_userId               ID of the user who is creating the object.
 -- @param   ai_op                   Operation to be performed (used for rights 
 --                                  check).
 -- @param   ai_name                 Name of the object.
 -- @param   ai_description          Description of the object.
 -- @param   ai_validUntil           Date until which the object is valid.
 -- @param   ai_showInNews           show in news flag.
 -- @param   ai_level1_s             oid of the template in the first level of
 --                                  the discussion.
 -- @param   ai_level2_s             oid of the template in the second level of
 --                                  the discussion.
 -- @param   ai_level3_s             oid of the template in the third level of
 --                                  the discussion.
 --
 -- @output parameters:
 --
 -- @returns A value representing the state of the procedure.
 --  c_ALL_RIGHT                     Action performed, values returned,
 --                                  everything ok.
 --  c_INSUFFICIENT_RIGHTS           User has no right to perform action.
 --  c_OBJECTNOTFOUND                The required object was not found within
 --                                  the database.
 
    -- delete pürocedure
CALL IBSDEV1.p_dropProc ('p_XMLDiscTemplate_01$change');
    -- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_XMLDiscTemplate_01$change
(
    -- input parameters:
    IN ai_oid_s             VARCHAR (18),
    IN ai_userId            INT,
    IN ai_op                INT,
    IN ai_name              VARCHAR (63),
    IN ai_validUntil        TIMESTAMP,
    IN ai_description       VARCHAR (255),
    IN ai_showInNews        SMALLINT,
    IN ai_level1_s          VARCHAR (18),
    IN ai_level2_s          VARCHAR (18),
    IN ai_level3_s          VARCHAR (18)
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE l_retValue      INT;            -- reutrn value of this procedure
    DECLARE l_oid           CHAR (8) FOR BIT DATA;        -- oid of the object
    DECLARE l_level1        CHAR (8) FOR BIT DATA;        -- conterted input parameter
    -- ai_level1_s
    DECLARE l_level2        CHAR (8) FOR BIT DATA;        -- conterted input parameter
    -- ai_level2_s
    DECLARE l_level3        CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- conterted input parameter
    -- ai_level3_s
    -- assign constants:
    SET c_ALL_RIGHT = 1;

    -- initialize local variables and return values:
    SET l_retValue = c_ALL_RIGHT;
    SET l_oid = c_NOOID;
    SET l_level1 = c_NOOID;
    SET l_level2 = c_NOOID;
    SET l_level3 = c_NOOID;

-- body:
    -- convert ids:
    CALL IBSDEV1.p_stringToByte (ai_level1_s, l_level1);
    CALL IBSDEV1.p_stringToByte (ai_level2_s, l_level2);
    CALL IBSDEV1.p_stringToByte (ai_level3_s, l_level3);

    -- perform the change of the object:
    -- p_Object$performCreate will set @l_retValue to @c_ALL_RIGHT,
    -- @c_INSUFFICIENT_RIGHTS or @c_OBJECTNOTFOUND
    CALL IBSDEV1.p_Object$performChange(ai_oid_s, ai_userId, ai_op, ai_name,
        ai_validUntil, ai_description, ai_showInNews, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF l_retValue = c_ALL_RIGHT THEN

        UPDATE IBSDEV1.m2_XMLDiscussionTemplate_01
        SET level1 = l_level1,
            level2 = l_level2,
            level3 = l_level3
        WHERE oid = l_oid;
    END IF;

    -- if object changed successfully

    -- return the state value:
    RETURN l_retValue;
END;




--------------------------------------------------------------------------------
 -- Copy an object and all its values. <BR>
 --
 -- @input parameters:
 -- @param   ai_oid_s                ID of the object to be deleted.
 -- @param   ai_userId               ID of the user who is deleting the object.
 -- @param   ai_op                   Operation to be performed (used for rights
 --                                  check).
 --
 -- @output parameters:
 --
 -- @returns A value representing the state of the procedure.
 --  c_ALL_RIGHT                     Action performed, values returned,
 --                                  everything ok.
 --  c_NOT_OK                        Something went wrong.
 
    -- delete procedure
CALL IBSDEV1.p_dropProc ('p_XMLDiscTemplate_01$BOCopy');
    -- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_XMLDiscTemplate_01$BOCopy
(
    -- common input parameters:
    IN ai_oid               CHAR (8) FOR BIT DATA,
    IN ai_userId            INT,
    IN ai_newOid            CHAR (8) FOR BIT DATA
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE l_retValue      INT;            -- return value of this procedure
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- converted input parameter
    -- ai_oid_s
    -- assign constants:
    SET c_NOT_OK = 0;
    SET c_ALL_RIGHT = 1;

    -- initialize local variables and return values:
    SET l_retValue = c_NOT_OK;
    SET l_oid = c_NOOID;

-- body:
    -- make an insert for all type specific tables:
    INSERT INTO IBSDEV1.m2_XMLDiscussionTemplate_01(oid, level1, level2, level3)
    SELECT ai_newOid, level1, level2, level3
    FROM IBSDEV1.m2_XMLDiscussionTemplate_01 AS b
    WHERE b.oid = ai_oid;
    GET DIAGNOSTICS l_rowcount = ROW_COUNT;
    COMMIT;

    -- check if insert was performed correctly:
    IF l_rowcount >= 1 THEN

    -- at least one row affected?
        SET l_retValue = c_ALL_RIGHT;
    END IF;

    -- set return value
    -- return the state value:
    RETURN l_retValue;
END;
