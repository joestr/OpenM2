-------------------------------------------------------------------------------
-- The IBS_WORKFLOWPROTOCOL table incl. indexes. <BR>
-- 
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:55 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_WORKFLOWPROTOCOL
(
    ID              INTEGER NOT NULL WITH DEFAULT 0,
                                            -- unique ascending number
    ENTRYDATE       TIMESTAMP NOT NULL WITH DEFAULT CURRENT TIMESTAMP,
                                            -- date of entry
    INSTANCEID      CHAR (8) NOT NULL FOR BIT DATA
                    WITH DEFAULT X'0000000000000000',
                                            -- oid of the workflow instance
    OBJECTID        CHAR (8) NOT NULL FOR BIT DATA
                    WITH DEFAULT X'0000000000000000',
                                            -- oid of the object (forwarded)
    OBJECTNAME      VARCHAR (63),           -- name of the object
    CURRENTSTATE    VARCHAR (63) NOT NULL WITH DEFAULT 'UNDEFINED',
                                            -- name of the current state
    OPERATIONTYPE   INTEGER NOT NULL WITH DEFAULT 0,
                                            -- 0=undefined,
                                            -- 1=sentToUser
                                            -- 2=sentCCToUser
                                            -- 3=sentToApplication
                                            -- 10=start,
                                            -- 11=finish,
                                            -- 12=abort,
                                            -- 13=terminate
    FROMPARTICIPANTID INTEGER NOT NULL WITH DEFAULT 0,
                                            -- id of the user/application
                                            -- who forwarded or performed
                                            -- operation (like 'terminate')
    TOPARTICIPANTID INTEGER,                -- id of the user/application
                                            -- to whom was forwarded
    FROMPARTICIPANTNAME VARCHAR (63),       -- full name
    TOPARTICIPANTNAME VARCHAR (63),         -- full name
    ADDITIONALCOMMENT VARCHAR (255)         -- comments
);

-- Create index statements:
CREATE INDEX IBSDEV1.I_WFPROT_INST_ID ON IBSDEV1.IBS_WORKFLOWPROTOCOL 
    (INSTANCEID ASC);
CREATE INDEX IBSDEV1.I_WFPROT_OBJECTID ON IBSDEV1.IBS_WORKFLOWPROTOCOL 
    (OBJECTID ASC);
