-------------------------------------------------------------------------------
-- The IBS_WORKFLOWVARIABLES table incl. indexes. <BR>
-- 
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:55 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_WORKFLOWVARIABLES
(
    VARIABLENAME        VARCHAR (64) NOT NULL,  -- the name of the variable
    VARIABLEVALUE       VARCHAR (255) NOT NULL, -- the value of the variable
    INSTANCEID          CHAR (8) FOR BIT DATA NOT NULL
                        WITH DEFAULT X'0000000000000000'
                                            -- oid of workflow-instance
);

-- Create index statements:
CREATE UNIQUE INDEX IBSDEV1.I_WFVAR_IDNAME ON
    IBSDEV1.IBS_WORKFLOWVARIABLES (INSTANCEID ASC, VARIABLENAME ASC);
