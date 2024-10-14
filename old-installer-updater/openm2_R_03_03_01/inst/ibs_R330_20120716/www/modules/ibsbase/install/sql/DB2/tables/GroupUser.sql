-------------------------------------------------------------------------------
-- The ibs groupuser table incl. indexes. <BR>
-- The groupuser table contains all relations between groups and users.
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:54 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_GROUPUSER
(
   ID               INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY ( 
                        START WITH 1 INCREMENT BY 1 
	                    NO MINVALUE NO MAXVALUE 
	                    NO CYCLE NO ORDER 
	                    CACHE 20 ),
                                        -- id of the relation
   STATE            INTEGER NOT NULL WITH DEFAULT 2,
                                        -- state of object
   GROUPID          INTEGER NOT NULL WITH DEFAULT 0,
                                        -- id of group
   USERID           INTEGER NOT NULL WITH DEFAULT 0,
                                        -- id of user/group
   ROLEID           INTEGER WITH DEFAULT 0,
                                        -- id of role
   ORIGGROUPID      INTEGER NOT NULL WITH DEFAULT 0,
                                        -- id of the original group
   IDPATH           VARCHAR (254) NOT NULL FOR BIT DATA WITH DEFAULT X'00'
                                        -- idPath of the Object
);

-- Create index statements:
CREATE INDEX IBSDEV1.I_GROUPUSERGROUPID ON IBSDEV1.IBS_GROUPUSER
    (GROUPID ASC);
CREATE INDEX IBSDEV1.I_GROUPUSERID ON IBSDEV1.IBS_GROUPUSER
    (ID ASC);
CREATE INDEX IBSDEV1.I_GR_US_ORIGROUPID ON IBSDEV1.IBS_GROUPUSER
    (ORIGGROUPID ASC);
CREATE INDEX IBSDEV1.I_GR_USERUSERID ON IBSDEV1.IBS_GROUPUSER
    (USERID ASC);
CREATE INDEX IBSDEV1.I_GR_USERUS_IDPATH ON IBSDEV1.IBS_GROUPUSER
    (IDPATH ASC);
