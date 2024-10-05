--------------------------------------------------------------------------------
 -- All stored procedures regarding the workflow service. <BR>
 --
-- @version     $Revision: 1.3 $, $Date: 2003/10/21 22:14:52 $
--              $Author: klaus $
 --
 -- author      Marcel Samek (MS)  020910
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
 -- Set the rights for a given user/group on given object handled by the 
 -- workflow. <BR> This procedure is only a wrapper to CALL IBSDEV1.p_Rights$setRights 
 -- from JAVA.
 -- 
 -- @input parameters:
 -- @param   @ai_oid_s           ID of the object to be deleted.
 -- @param   @ai_userId          ID of the user who is deleting the object.
 -- @param   @ai_rights          New rights (if 0 rights entry will be deleted)
 -- @param   @ai_rec             Set rights recursive?
 --                              (1 true, 0 false).
 --
 -- @output parameters:          problem: called procedure 
 --                              provides no error-level

    -- delete procedure
CALL IBSDEV1.p_dropProc ('p_Workflow$setRights');
    -- create the new procedure
CREATE PROCEDURE IBSDEV1.p_Workflow$setRights
(
    -- common input parameters:
    IN ai_oid_s             VARCHAR (18),
    IN ai_rPersonId         INT,
    IN ai_rights            INT,
    IN ai_rec               SMALLINT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE         INT;
    -- constants:
    -- local variables:
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
    -- initialize constants:
    -- initialize local variables:
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
-- body:
    CALL IBSDEV1.p_Rights$setRights(l_oid, ai_rPersonId, ai_rights, ai_rec);
    RETURN 0;
END;




--------------------------------------------------------------------------------
 -- Set the rights-key of the object with the given oid1 (incl. sub-objects) 
 -- to the rightskey of the object with the given oid2.<BR>
 -- <BR>
 --
 -- @input parameters:
 -- @param   oid1    oid of the object for which rights-key will be changed
 --                  (incl. sub-objects)
 -- @param   oid2    oid of the object of which rights-key will be copied
 --
 -- @returns true if ok; false if an error occured

-- delete procedure
CALL IBSDEV1.p_dropProc ('p_Workflow$copyRightsRec');
-- create the new proocedure
CREATE PROCEDURE IBSDEV1.p_Workflow$copyRightsRec
(
    -- common input parameters:
    IN ai_oid1_s            VARCHAR (18),
    IN ai_oid2_s            VARCHAR (18)
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_EMPTYPOSNOPATH VARCHAR (4);

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of a function
    DECLARE l_oid1          CHAR (8) FOR BIT DATA;
    DECLARE l_oid2          CHAR (8) FOR BIT DATA;
    DECLARE l_rKey          INT;
    DECLARE l_posNoPath     VARCHAR (254);
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND

    -- assign constants:
    SET c_NOT_OK = 0;
    SET c_ALL_RIGHT = 1;
    SET c_EMPTYPOSNOPATH = '0000';

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_oid1              = c_NOOID;
    SET l_oid2              = c_NOOID;
    SET l_rKey              = 0;
    SET l_posNoPath         = c_EMPTYPOSNOPATH;
    SET l_sqlcode           = SQLCODE;

-- body:
    -- convert oids
    CALL IBSDEV1.p_stringToByte (ai_oid1_s, l_oid1);
    CALL IBSDEV1.p_stringToByte (ai_oid2_s, l_oid2);

    -- get the rkey of the 2nd object:
    SET l_sqlcode = 0;

    SELECT  rKey
    INTO    l_rKey
    FROM    IBSDEV1.ibs_Object
    WHERE   oid = l_oid2;

    -- existence check code 100 means 0 rows in select
    IF l_sqlcode = 100 THEN
        RETURN c_NOT_OK;
    END IF;

    -- get posnopath of 1st object
    SET l_sqlcode = 0;

    SELECT posNoPath
    INTO l_posNoPath
    FROM IBSDEV1.ibs_Object
    WHERE oid = l_oid1;

    -- existence checkcode 100 means 0 rows in select
    IF l_sqlcode = 100 THEN
        RETURN c_NOT_OK;
    END IF;

    --
    -- change rkeys
    --
    -- 1. set new rKey for first object + sub-objects
    SET l_sqlcode = 0;

    UPDATE IBSDEV1.ibs_Object
    SET rKey = l_rKey
    WHERE posNoPath like l_posNoPath || '%';
    COMMIT;

    -- check for errors, 0 no error, 100 0 rows in select
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN
        -- an error occurred?
        RETURN c_NOT_OK;
    END IF;
    --
    -- 2. set rkey for linked-objects (object + sub-objects)
    SET l_sqlcode = 0;

    UPDATE IBSDEV1.ibs_Object
    SET rKey = l_rKey
    WHERE linkedObjectId IN (
                            SELECT oid
                            FROM IBSDEV1.ibs_Object
                            WHERE posNoPath like l_posNoPath || '%'
                            );
    COMMIT;

    -- check for errors, 0 no error, 100 0 rows in select
    IF l_sqlcode <> 0 AND l_sqlcode <> 100 THEN
        -- an error occurred?
        RETURN c_NOT_OK;
    END IF;

    -- no error occurred; exit
    RETURN c_ALL_RIGHT;
END;

