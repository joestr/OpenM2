--------------------------------------------------------------------------------
-- All stored procedures regarding the DiscussionContainer_01 Object. <BR>
--
-- @version     $Id: DiscussionContainer_01Proc.sql,v 1.5 2004/01/16 00:44:37 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020830
--------------------------------------------------------------------------------


--------------------------------------------------------------------------------
-- Creates a new DiscussionContainer_01 Object (incl. rights check). <BR>
--
-- @input parameters:
-- @param   @userId             ID of the user who is creating the object.
-- @param   @op                 Operation to be performed (used for rights 
--                              check).
-- @param   @tVersionId         Type of the new object.
-- @param   @name               Name of the object.
-- @param   @containerId_s      ID of the container where object shall be 
--                              created in.
-- @param   @containerKind      Kind of object/container relationship
-- @param   @isLink             Defines if the object is a link
-- @param   @linkedObjectId_s   If the object is a link this is the ID of the
--                              where the link shows to.
-- @param   @description        Description of the object.
--
-- @output parameters:
-- @param   @oid_s              OID of the newly created object.
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_DiscContainer_01$create');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_DiscContainer_01$create
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
    DECLARE c_ALREADY_EXISTS INT;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_tabTVersionId INT;
    DECLARE l_tabName       VARCHAR (63);
    DECLARE l_tabDescription VARCHAR (255);
    DECLARE l_partOfOid_s   VARCHAR (18);
    DECLARE l_sqlcode       INT DEFAULT 0;

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
    SET c_ALREADY_EXISTS    = 21;
  
    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_oid               = c_NOOID;
    SET ao_oid_s            = c_NOOID_s;
  
-- body:
    -- create base object:
    CALL IBSDEV1.p_Object$performCreate(ai_userId, ai_op, ai_tVersionId, ai_name,
        ai_containerId_s, ai_containerKind, ai_isLink, ai_linkedObjectId_s,
        ai_description, ao_oid_s, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    COMMIT;
    -- return the state value:
    RETURN l_retValue;
END;
