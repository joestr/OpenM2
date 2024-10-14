-------------------------------------------------------------------------------
-- The ibs_DocumentTemplate_01 table contains the values for the object
-- DocumentTemplate_01.
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:54 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_DOCUMENTTEMPLATE_01
(
    oid                 CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
   OBJECTTYPE           VARCHAR (63),
   OBJECTSUPERTYPE      VARCHAR (63),
   TYPENAME             VARCHAR (63),
   CLASSNAME            VARCHAR (63),
   ICONNAME             VARCHAR (63),
   TYPEID               INTEGER WITH DEFAULT 0,
   TVERSIONID           INTEGER WITH DEFAULT 0,
   MAYEXISTIN           VARCHAR (255),
   ISCONTAINERTYPE      SMALLINT WITH DEFAULT 0,
   MAYCONTAIN           VARCHAR (255),
   ISSEARCHABLE         SMALLINT WITH DEFAULT 0,
   ISINHERITABLE        SMALLINT WITH DEFAULT 0,
   SHOWINMENU           SMALLINT WITH DEFAULT 0,
   SHOWINNEWS           SMALLINT WITH DEFAULT 0,
   SYSTEMDISPLAYMODE    INTEGER WITH DEFAULT 0,
   DBMAPPED             SMALLINT WITH DEFAULT 0,
   TABLENAME            VARCHAR (30),
   PROCCOPY             VARCHAR (30),
   ATTACHMENTCOPY       VARCHAR (30),
   LOGDIRECTORY         VARCHAR (255),
   SHOWDOMTREE          SMALLINT,
   MAPPINGINFO          CLOB(2M),
    workflowTemplateOid CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000'
);

-- Unique constraints:
ALTER TABLE IBSDEV1.IBS_DOCUMENTTEMPLATE_01 ADD UNIQUE (OID);
