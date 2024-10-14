-------------------------------------------------------------------------------
-- The triggers for the ibs user table. <BR>
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:57 $
--              $Author: klaus $
--
-- @author      Marcel Samek (MS)  020910
-------------------------------------------------------------------------------

-- drop old trigger:
CALL IBSDEV1.p_DropTrig ('t_userInsertId');
-- create the trigger:
CREATE TRIGGER IBSDEV1.t_userInsertId
AFTER INSERT ON IBSDEV1.ibs_User
REFERENCING NEW ROW AS nRow
FOR EACH ROW
MODE DB2ROW
TRGR : BEGIN ATOMIC
-- body:
    -- set id if the id is not yet set:
    UPDATE  IBSDEV1.ibs_User
    SET     id = nRow.domainId * 16777216 + nRow.userNum
    WHERE   id = nRow.id
        AND id <= 0;
END;
-- t_userInsertId


-- drop old trigger:
CALL IBSDEV1.p_DropTrig ('t_userInsertOid');
-- create the trigger:
CREATE TRIGGER IBSDEV1.t_userInsertOid
AFTER INSERT ON IBSDEV1.ibs_User
REFERENCING NEW ROW AS nRow
FOR EACH ROW
MODE DB2ROW
TRGR : BEGIN ATOMIC
    -- constants:
    DECLARE c_tVersionId    INT DEFAULT 16842913; -- tVersionId of User
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid

-- body:
    -- set oid if the oid is not yet set:
    UPDATE  IBSDEV1.ibs_User
    SET     oid = createOid (c_tVersionId, id)
    WHERE   oid = nRow.oid
        AND oid = c_NOOID;
END;
-- t_userInsertOid
