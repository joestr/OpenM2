--------------------------------------------------------------------------------
-- All stored procedures regarding the MasterDataContainer_01 Object. <BR>
-- 
-- @version     $Id: MasterDataContainer_01Proc.sql,v 1.5 2003/10/31 00:12:50 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020831
--------------------------------------------------------------------------------

 
 --------------------------------------------------------------------------------
-- Gets the oid of a Container from which the name and the containerName 
-- are given (incl. rights check). <BR>
--
-- @input parameters:
-- @param   @userId             ID of the user who is creating the object.
-- @param   @op                 Operation to be performed (used for rights 
--                              check).
-- @param   @containerName   Name of the Container where the seached one is in
-- @param   @name                 Name of the Container that is searched
--
-- @output parameters:
-- @param   @oid        OID of the container
--
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the 
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_MasterDataContainer_01$gOid');

-- create the  new procedure:
CREATE PROCEDURE IBSDEV1.p_MasterDataContainer_01$gOid(
    -- input parameters:
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_containerName    VARCHAR (63),
    IN  ai_name             VARCHAR (63),
    -- output parameters
    OUT ao_oid              CHAR (8) FOR BIT DATA
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
    DECLARE l_retValue      INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;
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
    -- initialize return values:
    SET l_retValue          = c_ALL_RIGHT;
-- body:
    -- retrieve the base object data:
    SET l_sqlcode = 0;

    SELECT v.oid 
    INTO ao_oid
    FROM IBSDEV1.v_Container$content v INNER JOIN IBSDEV1.ibs_Object o ON v.containerId = o.oid
    WHERE v.userId = ai_userId
        AND v.tVersionId = 16853249
        AND b_and( rights, ai_op ) = ai_op
        AND o.name = ai_containerName
        AND v.name = ai_name;

    -- check if insert was performed correctly:
    IF l_sqlcode <> 0 THEN 
        SET l_retValue = c_OBJECTNOTFOUND;
    END IF;
    COMMIT;
    -- return the state value
    RETURN l_retValue;
END;
-- p_MasterDataContainer_01$gOid

