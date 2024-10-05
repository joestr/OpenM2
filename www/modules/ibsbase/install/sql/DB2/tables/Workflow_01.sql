-------------------------------------------------------------------------------
-- The IBS_WORKFLOW_01 table incl. indexes. <BR>
-- 
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:56 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_WORKFLOW_01
(
    OID                 CHAR (8) NOT NULL FOR BIT DATA
                        WITH DEFAULT X'0000000000000000',
                                            -- Workflow_01 object
    STARTDATE           TIMESTAMP NOT NULL WITH DEFAULT CURRENT TIMESTAMP,
                                            -- instantiation time
    ENDDATE             TIMESTAMP NOT NULL WITH DEFAULT CURRENT TIMESTAMP,
                                            -- instance finalization time
    WORKFLOWSTATE       VARCHAR (32) NOT NULL,   
                                            -- state (WfMC)
    CURRENTSTATE        VARCHAR (64),            
                                            -- state (m2)
    PROCESSMANAGER      INTEGER NOT NULL,       
                                            -- the process manager
    STARTER             INTEGER NOT NULL,       
                                            -- usert who starts wf
    CURRENTOWNER        INTEGER NOT NULL,       
                                            -- current owner of object
    WRITELOG            SMALLINT,               
                                            -- shall log be written?
    OBJECTID            CHAR (8) NOT NULL FOR BIT DATA
                        WITH DEFAULT X'0000000000000000',
                                            -- object to forward
    DEFINITIONID        CHAR (8) NOT NULL FOR BIT DATA
                        WITH DEFAULT X'0000000000000000',
                                            -- workflow xml-template
    PROCESSMANAGERCONT  CHAR (8) FOR BIT DATA
                        WITH DEFAULT X'0000000000000000',
                                            -- path to process manager
    STARTERCONTAINER    CHAR (8) FOR BIT DATA
                        WITH DEFAULT X'0000000000000000',
                                            -- oid of startercontainer
    CURRENTCONTAINER    CHAR (8) FOR BIT DATA
                        WITH DEFAULT X'0000000000000000'
                                            -- oid of current container
);

-- Create index statements:
CREATE INDEX IBSDEV1.I_WORKFL_CUR_OWN ON IBSDEV1.IBS_WORKFLOW_01
    (CURRENTOWNER ASC);
CREATE INDEX IBSDEV1.I_WORKFL_DEF_ID ON IBSDEV1.IBS_WORKFLOW_01
    (DEFINITIONID ASC);
CREATE INDEX IBSDEV1.I_WORKFL_ENDDATE ON IBSDEV1.IBS_WORKFLOW_01
    (ENDDATE ASC);
CREATE INDEX IBSDEV1.I_WORKFLOWOBJECTID ON IBSDEV1.IBS_WORKFLOW_01
    (OBJECTID ASC);
CREATE UNIQUE INDEX IBSDEV1.INDEXWORKFLOWOID ON IBSDEV1.IBS_WORKFLOW_01
    (OID ASC);
CREATE INDEX IBSDEV1.I_WORKFL_PROC_MAN ON IBSDEV1.IBS_WORKFLOW_01
    (PROCESSMANAGER ASC);
CREATE INDEX IBSDEV1.I_WORKFL_STARTDATE ON IBSDEV1.IBS_WORKFLOW_01
    (STARTDATE ASC);
CREATE INDEX IBSDEV1.I_WORKFLOWSTARTER ON IBSDEV1.IBS_WORKFLOW_01
    (STARTER ASC);
CREATE INDEX IBSDEV1.I_WORKFLOWSTATE ON IBSDEV1.IBS_WORKFLOW_01
    (WORKFLOWSTATE ASC);
