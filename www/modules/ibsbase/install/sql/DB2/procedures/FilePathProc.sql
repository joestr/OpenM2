--------------------------------------------------------------------------------
-- A Stored Procedure which actualizes the path of moved/copied files. <BR>
--
-- @version     $Revision: 1.4 $, $Date: 2003/10/21 22:14:48 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 0208318
--
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
-- updates all moved/copied files in a hierarchie which starts at the given oid. <BR>
--
-- @input parameters:
-- @param   @rootoid    OID of the rootObject.
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--------------------------------------------------------------------------------

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_FilePathData$change');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_FilePathData$change
(
    -- input parameters:
    IN  ai_rootOid_s        VARCHAR (18),
    IN  ai_recursive        SMALLINT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- operation was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2;
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3;

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this function
    DECLARE l_oid           CHAR (8) FOR BIT DATA; -- oid of the object
    DECLARE l_rootOid       CHAR (8) FOR BIT DATA;
    DECLARE l_path          VARCHAR (255);
    DECLARE l_oid_s         VARCHAR (18);
    DECLARE l_tVersion      INT;
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_containerId_s VARCHAR (18);
    DECLARE l_fileName      VARCHAR (255);
    DECLARE l_attachmentType INT;
    DECLARE l_sqlstatus     INT DEFAULT 0;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_ePos          VARCHAR (2000); -- error position description

    -- define cursor:
    DECLARE FileHD_Cursor CURSOR WITH HOLD FOR 
    SELECT  f.path, o.oid,
            CASE
                WHEN f.attachmentType = 3
                THEN o.oid
            ELSE o.containerId
            END  AS containerId,
            f.fileName, f.attachmentType, o.tVersionId 
    FROM    IBSDEV1.ibs_Attachment_01 f 
            INNER JOIN IBSDEV1.ibs_Object o 
            ON f.oid = o.oid, IBSDEV1.ibs_Object root
    WHERE   root.oid = l_rootOid
        AND f.attachmentType <> 2
        AND o.state = 2
        AND fileSize <> 0.0
        AND o.posNoPath LIKE root.posNoPath || '%'
        AND (
                ai_recursive = 1
                OR o.oid = l_rootOID
            );

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    SET l_sqlcode = 0;
    CALL IBSDEV1.p_stringToByte (ai_rootOid_s, l_rootOid);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- if any exception
    then
        SET l_ePos = 'Error in convert l_containerId';
        GOTO cursorException;           -- call exception handler
    END IF; -- if any exception

    -- open the cursor:
    OPEN FileHD_Cursor;

    -- get the first user:
    SET l_sqlcode = 0;
    FETCH FROM FileHD_Cursor INTO l_path, l_oid, l_containerId, l_fileName,
        l_attachmentType, l_tVersion;
    SET l_sqlstatus = l_sqlcode;
  
    -- loop through all found tupels:
    WHILE (l_sqlcode <> 100)
    DO
        IF l_sqlstatus = 0 OR l_sqlstatus = 100 THEN 
            SET l_sqlcode = 0;
            CALL IBSDEV1.p_byteToString (l_containerId, l_containerId_s);
            CALL IBSDEV1.p_byteToString (l_oid, l_oid_s);

            -- check if there occurred an error:
            IF (l_sqlcode <> 0)         -- if any exception
            then
                SET l_ePos = 'Error in convert';
                GOTO cursorException;   -- call exception handler
            END IF; -- if any exception

            IF l_attachmentType = 1 THEN 
                IF ai_recursive = 1 AND l_tVersion <> 16842833 THEN 
                    SET l_fileName = l_oid_s ||
                    SUBSTR(l_fileName, 19, length(l_fileName));
                END IF;
            END IF;
            -- update the path
            SET l_sqlcode = 0;
            UPDATE  IBSDEV1.ibs_Attachment_01
            SET     path = SUBSTR( l_path, 1, LENGTH(l_path)-19) ||
                    l_containerId_s || SUBSTR( l_path,LENGTH(l_path),1),
                    fileName = l_fileName
            WHERE   oid = l_oid;

            -- check if there occurred an error:
            IF (l_sqlcode <> 0)         -- if any exception
            then
                SET l_ePos = 'Error in update the path';
                GOTO cursorException;   -- call exception handler
            END IF; -- if any exception

        END IF;
        -- get next tupel:
        SET l_sqlcode = 0;
        FETCH FROM FileHD_Cursor INTO l_path, l_oid, l_containerId, l_fileName,
             l_attachmentType, l_tVersion;
        SET l_sqlstatus = l_sqlcode;

    END WHILE;
 
    -- close the not longer needed cursor:
    CLOSE FileHD_Cursor;

    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

cursorException:                        -- an error occurred within cursor
    -- close the not longer needed cursor:
    CLOSE FileHD_Cursor;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_FilePathData$change',
        l_sqlcode, l_ePos,
        'ai_recursive', ai_recursive, 'ai_rootOid_s', ai_rootOid_s,
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
-- p_FilePathData$change
