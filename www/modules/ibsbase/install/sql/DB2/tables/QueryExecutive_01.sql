-------------------------------------------------------------------------------
-- Table for dynamic Reports
-- 
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:55 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_QUERYEXECUTIVE_01
(
    SEARCHVALUES    VARCHAR (255),       
                                        -- search values for this query,
                                        -- seperated with ';'
    MATCHTYPES      VARCHAR (255),       
                                        -- matchTypes for this query, in same
                                        -- order as searchValues, 
                                        -- seperated by ';'
    OID             CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
                                        -- oid of object in ibs_object
    REPORTTEMPLATEOID CHAR (8) FOR BIT DATA
                    WITH DEFAULT X'0000000000000000',
                                        -- oid of assigned reporttemplate
                                        -- (querycreator)
    ROOTOBJECTOID   CHAR (8) FOR BIT DATA
                    WITH DEFAULT X'0000000000000000'
                                        -- oid of rootObject from which the
                                        -- search should be started, 0 if
                                        -- globalsearch
);

-- Primary key:
ALTER TABLE IBSDEV1.IBS_QUERYEXECUTIVE_01 ADD PRIMARY KEY (OID);
