--------------------------------------------------------------------------------
-- All stored procedures regarding the Reference table. <BR>
--
-- @version     $Revision: 1.8 $, $Date: 2008/02/26 15:39:30 $
--              $Author: btatzmann $
--
-- @author      Marcel Samek (MS)  020910
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
 -- Delete all references to and from objects of a specific type. <BR>
 -- This procedure deletes all references from and to objects which can be
 -- identified as being derived from a specific type. This means that both
 -- referencingOid and referencedOid are checked. The check is done through
 -- getting the tVersionId for each business object out of the ibs_Object table.
 -- If a business object is not found its data stay unchanged.
 --
 -- @input parameters:
 -- @param   ai_tVersionId       The id of the type version whose objects shall
 --                              be deleted.
 --
 -- @output parameters:
 --
-- delete procedure
CALL IBSDEV1.p_dropProc ('p_Reference$deleteTVersion');
-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Reference$deleteTVersion
(
-- input parameters:
  IN ai_tVersionId INT
-- output parameters:
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
NEW SAVEPOINT LEVEL
BEGIN
  DECLARE SQLCODE INT;

    -- constants:
    -- local variables:

    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_rowCount      INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;


    -- row counter
    -- assign constants:
    -- initialize local variables:

-- body:
    -- set a save point for the current transaction:
    SAVEPOINT s_Ref_delTV ON ROLLBACK RETAIN CURSORS;

    -- delete references from objects of the regarding type:
    SET l_sqlcode = 0;

    DELETE FROM IBSDEV1.ibs_Reference
    WHERE referencingOid IN (
                            SELECT oid
                            FROM IBSDEV1.ibs_Object
                            WHERE tVersionId = ai_tVersionId
                            );

    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
        SET l_ePos = 'delete all references from this type''s objects';
        GOTO exception1;
    END IF;

    -- call common exception1 handler
    -- delete references to objects of the regarding type:
    SET l_sqlcode = 0;
    DELETE FROM IBSDEV1.ibs_Reference
    WHERE referencedOid IN
                           (SELECT oid
                            FROM IBSDEV1.ibs_Object
                            WHERE CAST(tVersionId AS INT) = ai_tVersionId);

    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
        SET l_ePos = 'delete all references to this type''s objects';
        GOTO exception1;
    END IF;

    -- release the savepoint:
    RELEASE s_Ref_delTV;
    
    -- call common exception handler
    -- terminate the procedure:
    RETURN 0;
    exception1:

    -- an error occurred
    -- roll back to the save point:
    ROLLBACK TO SAVEPOINT s_Ref_delTV;
    -- release the savepoint:
    RELEASE s_Ref_delTV;

    -- log the error:

    CALL IBSDEV1.ibs_erro.logError (500, 'p_Reference$deleteTVersion', l_sqlcode, l_ePos,
        'ai_tVersionId', ai_tVersionId, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '', '', 0, '', '');
    COMMIT;
END;


--------------------------------------------------------------------------------
 -- Delete all references from a specific object. <BR>
 -- This procedure deletes all references from an object. This is necessary if
 -- the object is deleted.
 --
 -- @input parameters:
 -- @param   ai_referencingOid   The oid of the referencing object.
 --
 -- @output parameters:
 --
    -- delete procedure
CALL IBSDEV1.p_dropProc ('p_Reference$deleteReferencingOid');
    -- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Reference$deleteReferencingOid
(
    -- input parameters:
    IN ai_referencingOid    CHAR (8) FOR BIT DATA
    -- output parameters:
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
NEW SAVEPOINT LEVEL
BEGIN
    DECLARE SQLCODE INT;

    -- constants:
    -- local variables:
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_rowCount      INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;


    -- row counter
    -- assign constants:
    -- initialize local variables:

-- body:
    -- set a save point for the current transaction:
    SAVEPOINT s_Ref_delRefing ON ROLLBACK RETAIN CURSORS;

    -- delete the existing references:
    SET l_sqlcode = 0;
    DELETE FROM IBSDEV1.ibs_Reference
    WHERE referencingOid = ai_referencingOid;

    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
        SET l_ePos = 'delete all references from object';
        GOTO exception1;
    END IF;

    -- release the savepoint:
    RELEASE s_Ref_delRefing;

-- call common exception handler
    -- terminate the procedure:
    RETURN 0;
    exception1:

    -- an error occurred
    -- roll back to the save point:
    ROLLBACK TO SAVEPOINT s_Ref_delRefing;
    -- release the savepoint:
    RELEASE s_Ref_delRefOid;
    
    -- log the error:

    CALL IBSDEV1.ibs_erro.logError (500, 'p_Reference$deleteReferencingOid', l_sqlcode,
        l_ePos, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '');
    COMMIT;
END;




--------------------------------------------------------------------------------
 -- Delete all references from a specific object. <BR>
 -- This procedure deletes all references from an object. This is necessary if
 -- the object is deleted.
 --
 -- @input parameters:
 -- @param   ai_referencingOid   The oid of the referencing object.
 --
 -- @output parameters:
 --
    -- delete procedure
CALL IBSDEV1.p_dropProc ('p_Reference$deleteReferencedOid');
    -- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Reference$deleteReferencedOid
(
    -- input parameters:
    IN ai_referencedOid     CHAR (8) FOR BIT DATA
    -- output parameters:
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
NEW SAVEPOINT LEVEL
BEGIN
    DECLARE SQLCODE         INT;

    -- constants:
    -- local variables:
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_rowCount      INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;


    -- row counter
    -- assign constants:
    -- initialize local variables:

-- body:
    -- set a save point for the current transaction:
    SAVEPOINT s_Ref_delRefed ON ROLLBACK RETAIN CURSORS;

    -- delete the existing references:
        SET l_sqlcode = 0;
    DELETE FROM IBSDEV1.ibs_Reference
    WHERE referencedOid = ai_referencedOid;
    COMMIT;

    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
        SET l_ePos = 'delete all references from object';
        GOTO exception1;
    END IF;

    -- release the savepoint:
    RELEASE s_Ref_delRefed;
    -- call common exception handler
    -- terminate the procedure:
    RETURN 0;
    exception1:

    -- roll back to the save point:
    ROLLBACK TO SAVEPOINT s_Ref_delRefed;
    -- release the savepoint:
    RELEASE s_Ref_delRefed;

    -- an error occurred
    -- roll back to the save point:

    -- log the error:
    CALL IBSDEV1.ibs_erro.logError (500, 'p_Reference$deleteReferencedOid',
        l_sqlcode, l_ePos, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '');
    COMMIT;
END;







--------------------------------------------------------------------------------
 -- Delete an existing reference. <BR>
 -- If there exists a tuple within the reference table which corresponds to this
 -- oid/fieldName combination it is deleted. If not nothing happens.
 --
 -- @input parameters:
 -- @param   ai_referencingOid   The oid of the referencing object.
 -- @param   ai_fieldName        Name of the field which contains the reference.
 --
 -- @output parameters:
 --
    -- delete procedure
CALL IBSDEV1.p_dropProc ('p_Reference$delete');
    -- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Reference$delete
(
    -- input parameters:
    IN ai_referencingOid    CHAR (8) FOR BIT DATA,
    IN ai_fieldName         VARCHAR (63)
    -- output parameters:
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
NEW SAVEPOINT LEVEL
BEGIN
    DECLARE SQLCODE INT;
    -- constants:
    DECLARE c_REF_LINK      INT;            -- reference kind: link
    -- local variables:
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_rowCount      INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;


    -- row counter
    -- assign constants:
    SET c_REF_LINK = 1;

    -- initialize local variables:
-- body:
    -- set a save point for the current transaction:
    SAVEPOINT s_Ref_del ON ROLLBACK RETAIN CURSORS;

-- delete the existing reference:
    -- don't consider the fieldName for LINKs.
    SET l_sqlcode = 0;

    DELETE FROM IBSDEV1.ibs_Reference
    WHERE referencingOid = ai_referencingOid AND
         (
            kind = c_REF_LINK OR
            fieldName = ai_fieldName
         );
    GET DIAGNOSTICS l_rowcount = ROW_COUNT;

    -- check if there occurred an error:
    IF ( l_sqlcode <> 0 AND l_sqlcode <> 100 ) OR l_rowCount <= 0 then
        SET l_ePos = 'delete existing reference';
        -- an error occurred?
        GOTO exception1;
    END IF;

    -- release the savepoint:
    RELEASE s_Ref_del;
    -- call common exception handler
    -- terminate the procedure:
    
    RETURN 0;
    exception1:

    -- an error occurred
    -- roll back to the save point:
    ROLLBACK TO SAVEPOINT s_Ref_delRe;
    -- release the savepoint:
    RELEASE s_Ref_del;

    -- log the error:

    CALL IBSDEV1.ibs_erro.logError (500, 'p_Reference$delete', l_sqlcode, l_ePos,
        '', 0, 'ai_fieldName', ai_fieldName, '', 0, '', '', '', 0, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '', '', 0, '', '');
    COMMIT;
END;







--------------------------------------------------------------------------------
 -- Create a new reference. <BR>
 -- This procedure stores the reference within the references table.
 -- If there exists already a corresponding reference (same oid and fieldName)
 -- that reference is updated to the new referenced oid. Otherwise a new
 -- reference tuple is created.
 --
 -- @input parameters:
 -- @param   ai_referencingOid   The oid of the referencing object.
 -- @param   ai_fieldName        Name of the field which contains the reference.
 -- @param   ai_referencedOid    oid of the referenced object.
 -- @param   ai_kind             Kind of reference
 --                              1 ... link (reference object)
 --                              2 ... OBJECTREF
 --                              3 ... FIELDREF
 --
 -- @output parameters:
 --
    -- delete procedure
CALL IBSDEV1.p_dropProc ('p_Reference$create');
    -- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Reference$create
(
    -- input parameters:
    IN ai_referencingOid    CHAR (8) FOR BIT DATA,
    IN ai_fieldName         VARCHAR (63),
    IN ai_referencedOid     CHAR (8) FOR BIT DATA,
    IN ai_kind              INT
    -- output parameters:
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
NEW SAVEPOINT LEVEL
BEGIN
    -- constants:
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_REF_LINK      INT;            -- reference kind: link
	DECLARE c_MULTIPLE_LINK INT;            -- reference kind: multiple

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_rowCount      INT;            -- row counter
    DECLARE l_referencingOid CHAR (8) FOR BIT DATA; -- the local referencing oid
    DECLARE l_fieldName     VARCHAR (63);   -- the local field name
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- assign constants:
    SET c_REF_LINK = 1, c_MULTIPLE_LINK = 5;

    -- initialize local variables and return values:
    SET l_referencingOid = ai_referencingOid;
    SET l_fieldName = ai_fieldName;

-- body:
    -- set a save point for the current transaction:
    SAVEPOINT s_Ref_create ON ROLLBACK RETAIN CURSORS;

    -- set the correct referencing oid:
    IF l_referencingOid IS NULL THEN
        -- set default value:
        SET l_referencingOid = c_NOOID;
    END IF;

    -- if not allowed value
    -- set the correct fieldName value:
    IF ai_kind = c_REF_LINK THEN

    -- ensure that the field name has a correct value:
        SET l_fieldName = NULL;
    END IF;

    -- if link object
    -- update the existing reference:
    SET l_sqlcode = 0;

    UPDATE IBSDEV1.ibs_Reference
    SET referencedOid = ai_referencedOid
    WHERE referencingOid = ai_referencingOid
    AND ( kind = c_REF_LINK
    OR  (   fieldName = l_fieldName
		AND (   (   kind in (c_MULTIPLE_LINK)
	    		AND referencedOid = ai_referencedOid
	            )
	        OR  (   kind <> c_MULTIPLE_LINK)
	        )
	    )
        );
    GET DIAGNOSTICS l_rowcount = ROW_COUNT;
    -- release the savepoint:
    RELEASE s_Ref_create;

    -- check if there occurred an error:
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
        SET l_ePos = 'update existing reference';
        -- an error occurred?
        GOTO exception1;
    END IF;

    -- call common exception handler
    -- check if the reference was set:
    IF l_rowCount <= 0 THEN
        -- create a new reference:
        SET l_sqlcode = 0;
        INSERT INTO IBSDEV1.ibs_Reference(referencingOid, fieldName, referencedOid, kind)
        VALUES (ai_referencingOid, l_fieldName, ai_referencedOid, ai_kind);
   
    -- release the savepoint:
    RELEASE s_Ref_create;

        -- check if there occurred an error:
        IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
            SET l_ePos = 'update existing reference';
            GOTO exception1;
        END IF;
    END IF;

    -- if reference not existing
    -- terminate the procedure:

    RETURN 0;
    exception1:

    -- an error occurred
    -- roll back to the save point:
    ROLLBACK TO SAVEPOINT s_Ref_create;
    -- release the savepoint:
    RELEASE s_Ref_create;

    -- log the error:
    CALL IBSDEV1.ibs_erro.logError (500, 'p_Reference$create', l_sqlcode, l_ePos,
        'ai_kind', ai_kind, 'ai_fieldName', ai_fieldName, '', 0, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '');
    COMMIT;
END;
-- p_Reference$create
