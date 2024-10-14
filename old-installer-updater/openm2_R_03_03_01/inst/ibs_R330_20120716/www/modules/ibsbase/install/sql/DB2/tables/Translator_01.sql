-------------------------------------------------------------------------------
-- The ibs_Translator_01 table. <BR>
-- 
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:55 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_TRANSLATOR_01
(
    EXTENSION       VARCHAR (15) WITH DEFAULT 'xml',
                                            -- extension of generated
                                            -- output file
    OID             CHAR (8) FOR BIT DATA NOT NULL
                    WITH DEFAULT X'0000000000000000'
                                            -- oid of the object
);
