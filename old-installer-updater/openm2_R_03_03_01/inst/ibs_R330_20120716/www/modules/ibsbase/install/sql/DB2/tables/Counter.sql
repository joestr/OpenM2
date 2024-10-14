-------------------------------------------------------------------------------
-- TASK#1579 countertable to simulate Sequences.
-- 
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:54 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_COUNTER
(
    COUNTERNAME         VARCHAR (63) NOT NULL, -- name of counter
    CURRENTCOUNT        INTEGER NOT NULL    -- current count of counter
);

-- Unique constraints:
ALTER TABLE IBSDEV1.IBS_COUNTER ADD UNIQUE (COUNTERNAME);
