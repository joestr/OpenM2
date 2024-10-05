-------------------------------------------------------------------------------
-- The IBS_EDITRANSLATOR_01 table <BR>
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:54 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_EDITRANSLATOR_01
(
   oid                  CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000',
                                        -- oid of the object
   FILTERFILE           VARCHAR (255),  -- name of the filter file
   FORMATFILE           VARCHAR (255)   -- name of the format file
);

-- Unique constraints:
ALTER TABLE IBSDEV1.IBS_EDITRANSLATOR_01 ADD UNIQUE (OID);
