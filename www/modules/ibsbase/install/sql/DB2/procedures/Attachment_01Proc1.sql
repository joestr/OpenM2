-------------------------------------------------------------------------------
-- All stored procedures regarding the ibs_attachment_01 table. <BR>
--
-- @version     $Revision: 1.4 $, $Date: 2003/10/21 22:14:47 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020817
-------------------------------------------------------------------------------

-------------------------------------------------------------------------------
-- Attachment_01Proc is splited into Attachment_01Proc1 and Attachment_01Proc2
-- because of a cyclic-dependency. <BR>
-------------------------------------------------------------------------------

-------------------------------------------------------------------------------
-- Ensures that a master is set and also the flags in the object. <BR>
-- 
-- @input parameters:
-- @param   @ai_containerId         ID of the object's container.
-- @param   @ai_masterId            ID of the new master(only at copy) or null
--
-- @output parameters:
--
-- @returns A value representing the state of the procedure.
--  @c_ALL_RIGHT                    Action performed, values returned,
--                                  everything ok.
-------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Attachment_01$ensureMaster');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Attachment_01$ensureMaster
(
    -- input parameters:
    IN  ai_containerId      CHAR (8) FOR BIT DATA, -- the oid of the Container
                                            -- we want to check
    IN  ai_masterId         CHAR (8) FOR BIT DATA -- id of the new master
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- operation was o.k.
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                        -- default value for no defined oid
    DECLARE c_MAS_FILE      INT DEFAULT 1; -- file Attachment
    DECLARE c_MAS_HYPERLINK INT DEFAULT 2; -- hyperlink Attachment

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of function
    DECLARE l_documentId    CHAR (8) FOR BIT DATA; -- id of the owner document
    DECLARE l_attachmentType INT;       -- type of Attachment
    DECLARE l_count         INT DEFAULT 0; -- counter
    DECLARE l_sqlcode       INT DEFAULT 0; 
    DECLARE l_masterId      CHAR (8) FOR BIT DATA; -- variable for masterId
    DECLARE l_ePos          VARCHAR (2000); -- error position description

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_documentId        = c_NOOID;
    SET l_masterId          = ai_masterId;
  
-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    -- control if there is no master for the document:
    SET l_sqlcode = 0;

    SELECT COUNT (a.oid) 
    INTO l_count
    FROM IBSDEV1.ibs_Attachment_01 a, IBSDEV1.ibs_Object o
    WHERE a.oid = o.oid
        AND o.state = 2
        AND a.isMaster = 1
        AND o.containerId = ai_containerId;

    -- check if there occurred an error:
    IF (l_sqlcode <> 0 AND l_sqlcode <> 100) -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in SELECT COUNT (a.oid)';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception


    IF (l_count > 1 OR l_masterId IS NOT null) -- are there more than one
    THEN                                -- master ?
        -- delete all masters:        
        SET l_sqlcode = 0;
        UPDATE IBSDEV1. ibs_Attachment_01
        SET     isMaster = 0
        WHERE   oid IN
                (
                    SELECT o.oid 
                    FROM IBSDEV1.ibs_object o
                    WHERE o.containerId = ai_containerId
                );

        -- check if there occurred an error:
        IF (l_sqlcode <> 0 AND l_sqlcode <> 100) -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in UPDATE MasterAttachment';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception

    END IF; -- if are there more than one master
  
    IF (l_masterId IS null)            -- should the actuall attachment be
                                       -- the new master ?
    THEN
        IF (l_count = 1)               -- was a master set before ?
        THEN
            -- get the actuall master
            SET l_sqlcode = 0;

            SELECT  a.oid 
            INTO    l_masterId
            FROM    IBSDEV1.ibs_Attachment_01 a
                    LEFT OUTER JOIN IBSDEV1.ibs_Object o
                    ON a.oid = o.oid
            WHERE   o.containerId = ai_containerId
                AND o.state = 2
                AND a.isMaster = 1;

            -- check if there occurred an error:
            IF (l_sqlcode <> 0)         -- any exception?
            THEN
                -- create error entry:
                SET l_ePos = 'Error in SELECT a.oid';
                GOTO exception1;        -- call common exception handler
            END IF; -- if any exception
        ELSE -- is a new master set
            -- find a new master, the oldest one
            SET l_sqlcode = 0;

            SELECT  MIN (a.oid) 
            INTO    l_masterId
            FROM    IBSDEV1.ibs_Attachment_01 a
                    LEFT OUTER JOIN IBSDEV1.ibs_Object o
                    ON a.oid = o.oid
            WHERE   o.containerId = ai_containerId
                AND o.state = 2;

            -- check if there occurred an error:
            IF (l_sqlcode <> 0)         -- any exception?
            THEN
                -- create error entry:
                SET l_ePos = 'Error in SELECT MIN (a.oid)';
                GOTO exception1;        -- call common exception handler
            END IF; -- if any exception
        END IF; -- else is a new master set
    END IF; -- if should the actuall attachment be the new master
  
    -- get the Id of the owner document:
    SET l_sqlcode = 0;

    SELECT  containerId
    INTO    l_documentId
    FROM    IBSDEV1.ibs_object
    WHERE   oid = ai_containerId;
  
    -- check if there occurred an error:
    IF (l_sqlcode <> 0) -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in SELECT containerId';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    UPDATE IBSDEV1.ibs_object         -- set the selected property isMaster
    SET     flags = IBSDEV1.b_AND(flags, 
            (2147483647 - c_MAS_FILE - c_MAS_HYPERLINK))
    WHERE   oid = l_documentId;
  
    IF (l_masterId IS NOT null)         -- there is a master
    THEN
        -- set the master object:
        SET l_sqlcode = 0;

        UPDATE IBSDEV1.ibs_Attachment_01
        SET     isMaster = 1
        WHERE   oid = ai_masterId;

        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in UPDATE ibs_Attachment_01';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception

        -- get the attachment type
        SET l_sqlcode = 0;

        SELECT attachmentType
        INTO    l_attachmentType
        FROM    IBSDEV1.ibs_Attachment_01
        WHERE   oid = ai_masterId;

        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in SELECT attachmentType';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception

        IF  (l_attachmentType = c_MAS_FILE) -- is the attachment a File ?
        THEN
            SET l_sqlcode = 0;

            UPDATE IBSDEV1.ibs_object -- set the selected property
                                        -- isMaster
            SET     flags = IBSDEV1.b_OR(flags, c_MAS_FILE)
            WHERE   oid = l_documentId;

            IF (l_sqlcode <> 0)         -- any exception?
            THEN
                -- create error entry:
                SET l_ePos = 'Error in set flags for FILE 1';
                GOTO exception1;        -- call common exception handler
            END IF; -- if any exception
        ELSE -- is the attachment a File ?
            IF l_attachmentType = c_MAS_HYPERLINK -- is the attachment
            THEN 
                SET l_sqlcode = 0;

                UPDATE IBSDEV1.ibs_object -- set the selected property
                                        -- isMaster
                SET     flags = IBSDEV1.b_OR(flags, c_MAS_HYPERLINK)
                WHERE   oid = l_documentId;

                IF (l_sqlcode <> 0)     -- any exception?
                THEN
                    -- create error entry:
                    SET l_ePos = 'Error in set flags for HYPERLINK 2';
                    GOTO exception1;    -- call common exception handler
                END IF; -- if any exception

            END IF;
        END IF; -- is the attachment a File ?
    END IF; -- if there is a master

    -- finish transaction:
    COMMIT;                             -- make changes permanent

    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_Attachment_01$ensureMaster',
        l_sqlcode, l_ePos,
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    
    -- return error value:
    RETURN c_NOT_OK;
END;
-- p_Attachment_01$ensureMaster