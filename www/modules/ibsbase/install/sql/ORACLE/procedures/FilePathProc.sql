/******************************************************************************
 * A Stored Procedure which actualizes the path of moved/copied files. <BR>
 *
 * @version     1.11.0001, 06.05.2000
 *
 * @author      Christine Keim    (CK)  000506
 *
 * <DT><B>Updates:</B>
 *
 ******************************************************************************
 */

/******************************************************************************
 * updates all moved/copied files in a hierarchie which starts at the given oid. <BR>
 *
 * @input parameters:
 * @param   @rootoid    OID of the rootObject.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */
-- delete existing:

-- create the new function:
CREATE OR REPLACE FUNCTION p_FilePathData$change
(
    -- input parameters:
    ai_rootOid_s   VARCHAR2,
    ai_recursive   NUMBER
    -- output parameters
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
     -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_rootOid               RAW (8);
    l_path                  VARCHAR2 (255);
    l_oid                   RAW (8);
    l_tVersionId            INTEGER;
    l_oid_s                 VARCHAR2 (255);
    l_containerId           RAW (8);
    l_containerId_s         VARCHAR2 (255);
    l_fileName              VARCHAR2 (255);
    l_attachmentType        INTEGER;
    CURSOR FileHD_Cursor IS
            SELECT  f.path, o.oid,
                    DECODE (f.attachmentType,3, o.oid, o.containerId) AS containerId,
                    f.fileName, f.attachmentType, o.tVersionId
            FROM ibs_Attachment_01 f, ibs_Object o, ibs_Object root
            WHERE root.oid = l_rootOid
            AND f.oid = o.oid
            AND (f.attachmentType <> 2)
            AND o.state = 2
            AND fileSize <> 0.0
            AND o.posNoPath LIKE root.posNoPath || '%'
            AND ((ai_recursive=1) OR (o.oid=l_rootOid));
    l_cursorRow FileHD_Cursor%ROWTYPE;
BEGIN
     -- conversions: (VARCHAR2 -- RAW) - all input objectids must be converted
    p_stringToByte (p_FilePathData$change.ai_rootOid_s, l_rootOid);
    BEGIN
        -- loop through the cursor rows:
        FOR l_cursorRow IN FileHD_Cursor
        LOOP
            -- get the object data:
            l_path := l_cursorRow.path;
            l_oid := l_cursorRow.oid;
	    l_containerId := l_cursorRow.containerId;
            l_fileName := l_cursorRow.fileName;
            l_attachmentType := l_cursorRow.attachmentType;
            l_tVersionId := l_cursorRow.tVersionId;
            BEGIN
                p_byteToString (l_containerId, l_containerId_s);
                p_byteToString (l_oid, l_oid_s);
                -- update the path
                IF (l_attachmentType =1) THEN
                    IF (ai_recursive=1) AND (l_tVersionId!=16842833) THEN
                        l_fileName := l_oid_s || SUBSTR (l_fileName, 19, LENGTH (l_fileName));
                    END IF;
                END IF;
                UPDATE ibs_Attachment_01
		SET path = SUBSTR (l_path, 1, LENGTH (l_path) - 19) || l_containerId_s || SUBSTR (l_path, LENGTH(l_path)),
		    fileName = l_fileName
		WHERE oid = l_oid;	
            END;
        END LOOP; -- while another tuple found
        COMMIT WORK;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- no files have been found
            l_retValue := c_NOT_OK;
    -- return the state value:
    END;
    RETURN l_retValue;
END p_FilePathData$change;
/
-- p_FilePathData$change

EXIT;
