-------------------------------------------------------------------------------
-- The ibs domain scheme table incl. indexes. <BR>
-- The domain scheme table contains the schemes used for creating new domains.
-- These schemes contain not only the public structure of the domain but also
-- the user groups and the groups' access rights on this structure. Besides 
-- some default users can be defined. <BR>
-- There is also a scheme of an user workspace structure defined which belongs
-- to domains having this scheme. Users which are created within a domain of
-- a special scheme have the workspace scheme which belongs to the domain 
-- scheme.
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:54 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_DOMAINSCHEME_01
(
    oid                 CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
    ID                  INTEGER GENERATED ALWAYS AS IDENTITY ( 
                            START WITH 1 INCREMENT BY 1 
    	                    NO MINVALUE NO MAXVALUE 
    	                    NO CYCLE NO ORDER 
    	                    CACHE 20 ),
    WORKSPACEPROC       VARCHAR (63),   -- name of stored procedure 
                                        -- creating the workspace for one 
                                        -- user
    HASCATALOGMANAGEMENT SMALLINT,      -- does a domain with this scheme
                                        -- have a catalog management?
    HASDATAINTERCHANGE  SMALLINT        -- does a domain with this scheme
                                        -- have a data interchange component?
);

-- Create index statements:
CREATE UNIQUE INDEX IBSDEV1.I_DOMAINSCHEMEID ON IBSDEV1.IBS_DOMAINSCHEME_01
    (ID ASC);
CREATE UNIQUE INDEX IBSDEV1.I_DOMAINSCHEMEOID ON
    IBSDEV1.IBS_DOMAINSCHEME_01 (OID ASC);
