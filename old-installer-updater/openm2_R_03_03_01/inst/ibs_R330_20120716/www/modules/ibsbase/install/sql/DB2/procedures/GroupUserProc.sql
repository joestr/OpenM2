--------------------------------------------------------------------------------
-- All stored procedures regarding the GroupUser table. <BR>
--
-- @version     $Revision: 1.3 $, $Date: 2003/10/21 22:14:49 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020819
-------------------------------------------------------------------------------


--------------------------------------------------------------------------------
-- Add a new user to a group. <BR>
--
-- @input parameters:
-- @param   @groupId            Id of the group where the user shall be added.
-- @param   @userId             Id of the user to be added.
-- @param   @group              Id of group, which has to have some rights on 
--                              the GroupUser object.
-- @param   @rights             Rights of the group.
--
-- @output parameters:
-- @param   @oid_s              OID of the newly created object.
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_GroupUser_01$addUser');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_GroupUser_01$addUser(
    -- input parameters:
    IN    ai_groupId        INT,
    IN    ai_userId         INT,
    IN    ai_group          INT,
    IN    ai_rights         INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    DECLARE l_id            INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_tVersionId    INT;
    DECLARE l_admin         INT;
    DECLARE l_rightsAdmin   INT;
    DECLARE l_sqlcode       INT DEFAULT 0;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
    -- set tVersionId:
    SET l_tVersionId = 16842929; -- tVersionId of type Group_01

    -- set admin user:
    SELECT id
    INTO l_admin
    FROM IBSDEV1.ibs_User
    WHERE name = 'Admin';
    -- get rights for admin user:
    SELECT SUM (id) 
    INTO l_rightsAdmin
    FROM IBSDEV1.ibs_Operation;
    -- insert user into group:
    INSERT INTO IBSDEV1.ibs_GroupUser
        (groupId, userId)
    VALUES (ai_groupId, ai_userId);
    -- get id of newly created tuple:
    SELECT MAX (Id) 
    INTO l_id
    FROM IBSDEV1.ibs_GroupUser
    WHERE groupId = ai_groupId
        AND userId = ai_userId;
    -- create oid for saving rights:
    SET l_oid = IBSDEV1.p_intToBinary(l_tVersionId) ||
        IBSDEV1.p_intToBinary(l_id);
    -- set rights for group:
    IF ai_group <> 0 AND ai_rights <> 0 THEN 
        CALL IBSDEV1.p_Rights$setRights(l_oid, ai_group, ai_rights, 0);
    END IF;
    -- set rights for admin:
    CALL IBSDEV1.p_Rights$setRights(l_oid,l_admin, l_rightsAdmin, 0);
    -- actualize all cumulated rights:
    CALL IBSDEV1.p_Rights$updateRightsCum();
    COMMIT;
END;
-- p_GroupUser_01$addUser
