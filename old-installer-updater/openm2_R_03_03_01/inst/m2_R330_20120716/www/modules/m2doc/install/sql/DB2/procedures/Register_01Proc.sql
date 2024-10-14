--------------------------------------------------------------------------------
-- All stored procedures regarding the Register_01 Object. <BR>
-- 
-- @version     $Id: Register_01Proc.sql,v 1.6 2003/10/31 16:29:02 klaus Exp $
--
-- @author      Keim Christine (CK)  000415
--------------------------------------------------------------------------------

/*
delete from ibs_System
WHERE name= 'MASTERDATACONTAINER'
insert into ibs_System
(state,name,type,value)
VALUES (2,'MASTERDATACONTAINER', 'OBJECTID', '0x0101290100000997')
UPDATE ibs_Token_01
SET value = 'Region'
WHERE name = 'TOK_COMPOWNER'
*/

--------------------------------------------------------------------------------
-- Creates the necessary objects. <BR>
--
-- @input parameters:
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the 
--                          database.
--

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Register_01$createObjects'); 
-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Register_01$createObjects
(
    -- input parameters:
    IN  ai_userId         INT,
    IN  ai_op             INT,
    IN  ai_name           VARCHAR (63),
    IN  ai_description    VARCHAR (255),
    IN  ai_region         VARCHAR (255),
    IN  ai_mwst           INT,
    IN  ai_street         VARCHAR (63),
    IN  ai_zip            VARCHAR (15),
    IN  ai_town           VARCHAR (63),
    IN  ai_tel            VARCHAR (63),
    IN  ai_fax            VARCHAR (63),
    IN  ai_email          VARCHAR (127),
    IN  ai_homepage       VARCHAR (255),
    IN  ai_aname          VARCHAR (63),
    -- output parameters:
    OUT ao_oid_s          VARCHAR (18)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- operation was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2;
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                        -- default value for no defined oid

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this function
    DECLARE l_oid           CHAR (8) FOR BIT DATA; -- oid of the object
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_addressId     CHAR (8) FOR BIT DATA;
    DECLARE l_userOid       CHAR (8) FOR BIT DATA;
    DECLARE l_personOid     CHAR (8) FOR BIT DATA;
    DECLARE l_personOid_s   VARCHAR (18);
    DECLARE l_containerId_s VARCHAR (18);
    DECLARE l_tempOid_s     VARCHAR (18);

    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_ePos          VARCHAR (2000); -- error position description

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_oid               = c_NOOID;
    SET ao_oid_s            = c_NOOID_s;
    SET l_personOid_s       = c_NOOID_s;

-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    -- select the oid of the masterdatacontaineroid
    SET l_sqlcode = 0;
    SELECT  value
    INTO    l_containerId_s
    FROM    IBSDEV1.ibs_System
    WHERE   name = 'MASTERDATACONTAINER'
        AND state = 2;

    IF (l_sqlcode = 0)                  -- oid has been found
    THEN
        -- create Company        
        SET l_sqlcode = 0;
        CALL IBSDEV1.p_Company_01$create( ai_userId, ai_op, 16854017,
            ai_name, l_containerId_s, 1, 0, c_NOOID, ai_description,
            ao_oid_s);

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in create Company';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception

        GET DIAGNOSTICS l_retValue = RETURN_STATUS;

        IF (l_retValue = c_ALL_RIGHT)
        THEN
            -- convert oid to string:
            SET l_sqlcode = 0;
            CALL IBSDEV1.p_stringToByte ( ao_oid_s, l_oid );
        
            -- check if there occurred an error:
            IF (l_sqlcode <> 0)         -- any exception?
            THEN
                -- create error entry:
                SET l_ePos = 'Error in conversion ao_oid_s';
                GOTO exception1;        -- call common exception handler
            END IF; -- if any exception

            -- update the rest of the company with the right values
            SET l_sqlcode = 0;
            UPDATE  IBSDEV1.mad_Company_01
            SET     owner = ai_region,
                    mwst = ai_mwst
            WHERE   oid = l_oid;
    
            -- check if there occurred an error:
            IF (l_sqlcode <> 0)         -- any exception?
            THEN
                -- create error entry:
                SET l_ePos = 'Error in update the rest of the company';
                GOTO exception1;        -- call common exception handler
            END IF; -- if any exception

            -- select the address of the company
            SET l_sqlcode = 0;
            SELECT  oid
            INTO    l_addressId
            FROM    IBSDEV1.ibs_Object
            WHERE   tVersionId = 16854785
                AND containerId = l_oid;

            -- check if there occurred an error:
            IF (l_sqlcode <> 0)         -- any exception?
            THEN
                -- create error entry:
                SET l_ePos = 'Error in update the rest of the company';
                GOTO exception1;        -- call common exception handler
            END IF; -- if any exception

            -- update the address ot the company
            SET l_sqlcode = 0;
            UPDATE IBSDEV1.m2_Address_01
            SET     street = ai_street,
                    zip = ai_zip,
                    town = ai_town,
                    tel = ai_tel,
                    fax = ai_fax,
                    email = ai_email,
                    homepage = ai_homepage
            WHERE   oid = l_addressId;

            -- check if there occurred an error:
            IF (l_sqlcode <> 0)         -- any exception?
            THEN
                -- create error entry:
                SET l_ePos = 'Error in update the address ot the company';
                GOTO exception1;        -- call common exception handler
            END IF; -- if any exception

            -- select the contactcontainer of the company
            SET l_sqlcode = 0;
            SELECT  oid
            INTO    l_containerId
            FROM    IBSDEV1.ibs_Object
            WHERE   tVersionId = 16853761
                AND containerId = l_oid;

            -- check if there occurred an error:
            IF (l_sqlcode <> 0)         -- any exception?
            THEN
                -- create error entry:
                SET l_ePos = 'Error in select the contactcontainer';
                GOTO exception1;        -- call common exception handler
            END IF; -- if any exception

            -- convert oid to string:
            SET l_sqlcode = 0;
            CALL IBSDEV1.p_byteToString ( l_containerId, l_containerId_s );

            -- check if there occurred an error:
            IF (l_sqlcode <> 0)         -- any exception?
            THEN
                -- create error entry:
                SET l_ePos = 'Error in convert oid to string';
                GOTO exception1;        -- call common exception handler
            END IF; -- if any exception

            -- create a new person as a contact
            SET l_sqlcode = 0;
            CALL IBSDEV1.p_Person_01$create( ai_userId, ai_op, 16853505,
                ai_name, l_containerId_s, 1, 0, c_NOOID, ai_description,
                l_personOid_s);

            -- check if there occurred an error:
            IF (l_sqlcode <> 0)         -- any exception?
            THEN
                -- create error entry:
                SET l_ePos = 'Error in convert oid to string';
                GOTO exception1;        -- call common exception handler
            END IF; -- if any exception

            SET l_sqlcode = 0;
            CALL IBSDEV1.p_stringToByte ( l_personOid_s, l_personOid );

            -- check if there occurred an error:
            IF (l_sqlcode <> 0)         -- any exception?
            THEN
                -- create error entry:
                SET l_ePos = 'Error in convert oid to string';
                GOTO exception1;        -- call common exception handler
            END IF; -- if any exception

            -- select the working directory of the user
            SET l_sqlcode = 0;
            SELECT  workbox
            INTO    l_containerId
            FROM    IBSDEV1.ibs_Workspace
            WHERE   userId = ai_userId;
        
            -- check if there occurred an error:
            IF (l_sqlcode <> 0)         -- any exception?
            THEN
                -- create error entry:
                SET l_ePos = 'Error in select the working directory of user';
                GOTO exception1;        -- call common exception handler
            END IF; -- if any exception

            -- convert oid to string:
            SET l_sqlcode = 0;
            CALL IBSDEV1.p_byteToString ( l_containerId, l_containerId_s );
    
            -- check if there occurred an error:
            IF (l_sqlcode <> 0)         -- any exception?
            THEN
                -- create error entry:
                SET l_ePos = 'Error in convert oid to string';
                GOTO exception1;        -- call common exception handler
            END IF; -- if any exception

            -- create reference of company in the working directory of the user
            SET l_sqlcode = 0;
            CALL IBSDEV1.p_Referenz_01$create( ai_userId, ai_op, 16842801,
                ai_name,  l_containerId_s, 1, 1, ao_oid_s, '', l_tempOid_s );

            -- check if there occurred an error:
            IF (l_sqlcode <> 0)         -- any exception?
            THEN
                -- create error entry:
                SET l_ePos = 'Error in create reference of company';
                GOTO exception1;        -- call common exception handler
            END IF; -- if any exception

            -- select oid of the user-object
            SET l_sqlcode = 0;
            SELECT  oid
            INTO    l_userOid
            FROM    IBSDEV1.ibs_User
            WHERE   id = ai_userId;

            -- check if there occurred an error:
            IF (l_sqlcode <> 0)         -- any exception?
            THEN
                -- create error entry:
                SET l_ePos = 'Error in select oid of the user-object';
                GOTO exception1;        -- call common exception handler
            END IF; -- if any exception

            -- update person set userOid of related user
            SET l_sqlcode = 0;
            UPDATE  IBSDEV1.mad_Person_01
            SET     userOid = l_userOid
            WHERE   oid = l_personOid;

            -- check if there occurred an error:
            IF (l_sqlcode <> 0)         -- any exception?
            THEN
                -- create error entry:
                SET l_ePos = 'Error in update person userOid of related user';
                GOTO exception1;        -- call common exception handler
            END IF; -- if any exception
        END IF; -- create not successfull
    ELSE 
        SET l_retValue = c_OBJECTNOTFOUND;
    END IF;
        
    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Register_01$createObjects',
        l_sqlcode, l_ePos,
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    
    -- return error value:
    RETURN c_NOT_OK;

END; 
-- p_Register_01$createObjects
