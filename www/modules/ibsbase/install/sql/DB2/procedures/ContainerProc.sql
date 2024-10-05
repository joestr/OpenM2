--------------------------------------------------------------------------------
-- All stored procedures regarding a container. <BR>
-- 
-- @version     $Revision: 1.4 $, $Date: 2003/10/21 22:14:48 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020817
-------------------------------------------------------------------------------


--------------------------------------------------------------------------------
-- Gets the data of the objects within a given container (incl. rights check). 
-- <BR>
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be changed.
-- @param   @userId             ID of the user who is creating the object.
-- @param   @op                 Operation to be performed (used for rights 
--                              check).
-- @param   @orderBy            Attribute to order by.
-- @param   @orderHow           Type of ordering (ASCending or DESCending).
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the 
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Container$retrieveContent');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Container$retrieveContent(
    -- input parameters:
    IN    ai_oid_s          VARCHAR (18),
    IN    ai_userId_s       VARCHAR (255),
    IN    ai_op_s           VARCHAR (255),
    IN    ai_orderBy        VARCHAR (255),
    IN    ai_orderHow       VARCHAR (255))
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    DECLARE l_SQLString     VARCHAR (1024);
    DECLARE l_sqlcode       INT DEFAULT 0;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
  
  
    SET l_SQLString =
        'SELECT  uid, isNew, rights, * ' || 'FROM    v_Container$content ' ||
        'WHERE   uid = ' || ai_userId_s || '    AND containerId = ' || ai_oid_s ||
        '    AND (rights & ' || ai_op_s || ') > 0 ' || 'ORDER BY ' || ai_orderBy ||
        ' ' || ai_orderHow;
    EXECUTE IMMEDIATE l_SQLString;
END; 
-- p_Container$retrieveContent


--------------------------------------------------------------------------------
-- Deletes an object and all its values (incl. rights check). <BR>
-- This procedure also delets all links showing to this object.
-- 
-- @input parameters:
-- @param   @oid_s              ID of the object to be deleted.
-- @param   @userId             ID of the user who is deleting the object.
-- @param   @op                 Operation to be performed (used for rights 
--                              check).
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the 
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Container$delete');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Container$delete(
    -- input parameters:
    IN    ai_oid_s          VARCHAR (18),
    IN    ai_userId         INT,
    IN    ai_op             INT
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- definitions:
    -- define return constants
    DECLARE c_INSUFFICIENT_RIGHTS INT;
    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_OBJECTNOTFOUND INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
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
  
    SET c_ALL_RIGHT = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_OBJECTNOTFOUND = 3;
  
    SET l_retValue = c_ALL_RIGHT;
    -- perform deletion of object:
    CALL IBSDEV1.p_Object$performDelete(ai_oid_s, ai_userId, ai_op);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    COMMIT;
    -- return the state value
 
    RETURN l_retValue;
END; 
-- p_Container$delete