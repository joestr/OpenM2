-------------------------------------------------------------------------------
-- The IBS_DOMAIN_01 table incl. indexes. <BR>
-- The domain table contains all currently running domains.
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:54 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_DOMAIN_01
(
    oid                 CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    ID                  INTEGER GENERATED ALWAYS AS IDENTITY (
                            START WITH 1 INCREMENT BY 1
    	                    NO MINVALUE NO MAXVALUE
    	                    NO CYCLE NO ORDER
    	                    CACHE 20),
    SCHEME              INTEGER NOT NULL WITH DEFAULT 0,
                                            -- scheme of the domain
    WORKSPACEPROC       VARCHAR (63),            
                                            -- procedure for creating a 
                                            -- workspace within this domain
    ADMINID             INTEGER NOT NULL WITH DEFAULT 0,
                                            -- default domain administrator
    ADMINGROUPID        INTEGER NOT NULL WITH DEFAULT 0,
                                            -- group for domain administrators
    ALLGROUPID          INTEGER NOT NULL WITH DEFAULT 0,
                                            -- group for all users
    USERADMINGROUPID    INTEGER NOT NULL WITH DEFAULT 0,
                                            -- group for administring users 
                                            -- and groups
    STRUCTADMINGROUPID  INTEGER NOT NULL WITH DEFAULT 0,
                                            -- group for administring the 
                                            -- structure
    HOMEPAGEPATH        VARCHAR (63) WITH DEFAULT '',
                                            -- path of the homepage of the 
                                            --  domain
    LOGO                VARCHAR (63) WITH DEFAULT '',
                                            -- logo for the domain
    SSLREQUIRED         SMALLINT NOT NULL WITH DEFAULT 0,
                                            -- ssl for the domain
    groupsOid           CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
                                            -- container for the groups
    usersOid            CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
                                            -- container for the users
    publicOid           CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
                                            -- public container of domain
    workspacesOid       CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000'
                                            -- container for workspaces of
                                            -- domain
);

-- Create index statements:
CREATE UNIQUE INDEX IBSDEV1.I_DOMAINID ON IBSDEV1.IBS_DOMAIN_01 
    (ID ASC);
CREATE UNIQUE INDEX IBSDEV1.I_DOMAINOID ON IBSDEV1.IBS_DOMAIN_01
    (OID ASC);
CREATE INDEX IBSDEV1.I_DOMAIN_01ADMINID ON IBSDEV1.IBS_DOMAIN_01
    (ADMINID ASC);
