--------------------------------------------------------------------------------
-- Stored procedure to set rights via import . <BR>
--
-- @version     $Revision: 1.3 $, $Date: 2003/10/21 22:14:49 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020831
--------------------------------------------------------------------------------


--------------------------------------------------------------------------------
-- Sets rights for a user or a group for a specific object . <BR>
-- Additionally all rights set for the object can be deleted first.
-- The reference to the user or the group to set the rights for will be
-- done though the name of the user or the group. This expects that there
-- are no ambiguous user or group names in the system.<BR>
--
-- @input parameters:
-- @param oid            oid of the object to set the rights for
-- @param name           name of the user or the group
-- @param isUser         flag to set rights for user or otherwise group
-- @param rights         rights to set
-- @param deleteRigths   flag to frist delete all rights set for the object 
-- @param domainId       id of domain to look for the user or the group
--
-- @output parameters:
--
-- @returns A value representing the state of the procedure.
--  NOT_OK                  An Error occured
--  ALL_RIGHT               Action performed, values returned, everything ok.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Integrator$setImportRight');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Integrator$setImportRight(
    -- input parameters:
    IN    ai_oid_s          VARCHAR (18),
    IN    ai_name           VARCHAR (63),
    IN    ai_isUser         SMALLINT,
    IN    ai_rights         INT,
    IN    ai_deleteRights   SMALLINT,
    IN    ai_domainId       INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- declarations:
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_personId      INT;
    -- define return constants:
    DECLARE c_NOT_OK        INT;
    DECLARE c_ALL_RIGHT     INT;
    -- define return values:
    DECLARE l_retValue      INT;
    DECLARE l_count         INT;
    DECLARE l_sqlcode       INT DEFAULT 0;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initializations:
    -- conversions (objectidstring) - all input objectids must be converted:
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
    -- set constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    -- initialize return values:
    SET l_retValue          = c_ALL_RIGHT;
    -- check if rights of the object need to be deleted first 
-- body:
    IF ai_deleteRights = 1 THEN 
       -- delete all rights set for the objekt without checking the rights
        CALL IBSDEV1.p_Rights$deleteObjectRights(l_oid);
    END IF;
  
   -- now check if we have to set the right for a user else we set it for a group 
    IF ai_isUser = 1 THEN 
        SELECT MIN (u.id), COUNT(*) 
        INTO l_personId, l_count
        FROM IBSDEV1.ibs_user u, IBSDEV1.ibs_object o
        WHERE u.name = ai_name
            AND u.domainId = ai_domainId
            AND o.oid = u.oid
            AND o.state = 2
            AND o.isLink = 0;
    ELSE 
        SELECT MIN (g.id), COUNT(*) 
        INTO l_personId, l_count
        FROM IBSDEV1.ibs_group g, IBSDEV1.ibs_object o
        WHERE g.name = ai_name
            AND g.domainId = ai_domainId
            AND o.oid = g.oid
            AND o.state = 2
            AND o.isLink = 0;
    END IF;
  
    -- check if we got the user or the group
    IF l_count = 1 THEN 
        -- now add the specific right
        CALL IBSDEV1.p_Rights$setRights(l_oid, l_personId, ai_rights, 0);
    ELSE 
        -- user or group could not have been found or was not unique
        SET l_retValue = c_NOT_OK;
    END IF;
    COMMIT;
    -- return the state value:
    RETURN l_retValue;
END;
-- p_KeyMapper$new
 
CALL IBSDEV1.p_Debug( 'p_Integrator$setImportRight created');

