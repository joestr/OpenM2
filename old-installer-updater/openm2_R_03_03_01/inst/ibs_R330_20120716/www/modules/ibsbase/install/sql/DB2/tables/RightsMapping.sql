-------------------------------------------------------------------------------
-- The ibs_RightsMapping table. <BR>
-- 
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:55 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_RIGHTSMAPPING
(
    ALIASNAME       VARCHAR (63) NOT NULL WITH DEFAULT 'UNDEFINED',
                                            -- name of rights-alias
    RIGHTNAME       VARCHAR (63) NOT NULL WITH DEFAULT 'UNDEFINED'
                                            -- m2-right
);

-- Create index statements:
CREATE INDEX IBSDEV1.I_RIGHMAPP_ALSNAME ON IBSDEV1.IBS_RIGHTSMAPPING
    (ALIASNAME ASC);
