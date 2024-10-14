-------------------------------------------------------------------------------
-- The IBS_USER table incl. indexes. <BR>
-- The user table contains all currently existing users.
--
-- @version     $Revision: 1.3 $, $Date: 2009/02/10 09:31:16 $
--              $Author: btatzmann $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.ibs_User
(                                           
    id              INTEGER NOT NULL WITH DEFAULT 0,
                                            -- <domainId>100{21--0|1}
    oid             CHAR (8) NOT NULL FOR BIT DATA
                    WITH DEFAULT X'0000000000000000',
                                            -- object id of user
    state           INTEGER NOT NULL WITH DEFAULT 2,
                                            -- state of object
    domainId        INTEGER NOT NULL WITH DEFAULT 0,
                                            -- domain where the user resides
    name            VARCHAR (63) NOT NULL WITH DEFAULT 'undefined',
                                            -- user name
    password        VARCHAR (63) NOT NULL WITH DEFAULT 'undefined',
    fullname        VARCHAR (63) NOT NULL WITH DEFAULT 'undefined',
                                            -- full name of user
    admin           SMALLINT NOT NULL WITH DEFAULT 0,
    changePwd       SMALLINT NOT NULL WITH DEFAULT 0,
    userNum         INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY ( 
                        START WITH 8388609 INCREMENT BY 1
	                    MINVALUE 8388609 MAXVALUE 10485759
	                    NO CYCLE NO ORDER 
	                    CACHE 20) 
); -- ibs_User

-- Create index statements:
CREATE UNIQUE INDEX IBSDEV1.i_userId ON IBSDEV1.ibs_User
    (id ASC);
CREATE INDEX IBSDEV1.i_userName ON IBSDEV1.ibs_User
    (name ASC);
CREATE INDEX IBSDEV1.i_userOid ON IBSDEV1.ibs_User
    (oid ASC);
CREATE INDEX IBSDEV1.i_userState ON IBSDEV1.ibs_User
    (state ASC);
