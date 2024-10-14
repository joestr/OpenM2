-------------------------------------------------------------------------------
-- The table contains the values for the object
-- TabTemplate_01.
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:55 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_TABTEMPLATE_01
(
    ID              INTEGER WITH DEFAULT 0,
    KIND            INTEGER WITH DEFAULT 0,          
                                            -- 1 = View, 2 = Object,
                                            -- 3 = Link, 4 = Function
    TVERSIONID      INTEGER WITH DEFAULT 0,    
                                            -- the tVersionId of the tab
                                            -- object (for kind = 2)
    FCT             INTEGER WITH DEFAULT 0,
                                            -- the function of the tab
    PRIORITY        INTEGER WITH DEFAULT 0,
                                            -- the priority of the tab
    NAME            VARCHAR (63),            
                                            -- the name of the tab
    DESCRIPTION     VARCHAR (255),           
                                            -- the description of the tab
    TABCODE         VARCHAR (63),            
                                            -- the tab code
    CLASS           VARCHAR (255),           
                                            -- the class to show the view tab
    OID             CHAR (8) NOT NULL FOR BIT DATA
                    WITH DEFAULT X'0000000000000000'
);
