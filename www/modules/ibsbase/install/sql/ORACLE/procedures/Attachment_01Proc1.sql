/******************************************************************************
 * All stored procedures regarding the object table. <BR>
 * 
 *
 * @version     1.10.0001, 05.08.1999
 *
 * @author      Mario Stegbauer (MS)  980805
 *
 * <DT><B>Updates:</B>
 * <DD>HB 990805    Code cleaning.
 ******************************************************************************
 */

/******************************************************************************
 * Attachment_01Proc is splited into Attachment_01Proc1 and Attachment_01Proc2
 * because of a cyclic-dependency. <BR>
 */

/******************************************************************************
 * Ensures that a master is set and also the flags in the object. <BR>
 * 
 * @input parameters:
 * @param   @ai_containerId   ID of the object's container.
 * @param   @ai_masterId      ID of the new master(only at copy) or null.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */
CREATE OR REPLACE FUNCTION p_Attachment_01$ensureMaster
(
    -- input parameters:
    ai_containerId          RAW,            -- the oid of the Container we want
                                            -- to controll
    ai_masterId             RAW             -- id of the new master
    -- output parameters:
)
RETURN INTEGER
AS
    -- constants:
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- operation was o.k.
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
                                            -- oid of no valid object
    c_MAS_FILE              CONSTANT INTEGER := 1; -- file Attachment
    c_MAS_HYPERLINK         CONSTANT INTEGER := 2; -- hyperlink Attachment

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_documentId            RAW (8) := c_NOOID; -- id of the owner document
    l_attachmentType        INTEGER;        -- type of Attachment
    l_count                 INTEGER := 0;   -- counter
    l_masterId              RAW (8) := ai_masterId; -- variable for masterId

BEGIN
    BEGIN
        -- control if there is no master for the document:
        SELECT  COUNT (a.oid)               -- search for actual master
        INTO    l_count
        FROM    ibs_Attachment_01 a, ibs_Object o
        WHERE   a.oid = o.oid
        AND     o.state = 2
        AND 	a.isMaster = 1
        AND 	o.containerId = ai_containerId;
    EXCEPTION
        WHEN OTHERS THEN
            ibs_error.log_error (ibs_error.error, 'p_Attachment_01$ensureMaster',
                                 'Error in SELECT COUNT (a.oid)');
        RAISE;
    END;

    IF (l_count > 1 OR l_masterId IS NOT null) -- are there more than one
    THEN                                       -- master ?
        BEGIN
        -- delete all masters:        
        UPDATE  ibs_Attachment_01
        SET     isMaster = 0
        WHERE   oid  IN (
                            SELECT  o.oid
                            FROM    ibs_object o
                            WHERE   o.containerId = ai_containerId
                        );
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error (ibs_error.error, 'p_Attachment_01$ensureMaster',
                                     'Error in UPDATE MasterAttachment');
            RAISE;
        END; 
    END IF; -- if are there more than one master

    IF (l_masterId IS null)                 -- should the actuall attachment be
                                            -- the new master ?
    THEN
        IF (l_count = 1)                    -- was a master set before ?
        THEN
            BEGIN
                -- get the actuall master
                SELECT  a.oid
                INTO    l_masterId
                FROM    ibs_Attachment_01 a, ibs_Object o
                WHERE   o.containerId = ai_containerId
                AND     a.oid = o.oid(+)
                AND     o.state = 2
                AND     a.isMaster = 1;
            EXCEPTION
                WHEN OTHERS THEN
                    ibs_error.log_error (ibs_error.error,
                                         'p_Attachment_01$ensureMaster',
                                         'Error in SELECT a.oid');
                RAISE;
            END;
        ELSE
            BEGIN
                -- find a new master, the oldest one:
                SELECT  MIN (a.oid)
                INTO    l_masterId
                FROM    ibs_Attachment_01 a, ibs_Object o
                WHERE   o.containerId = ai_containerId
                AND     a.oid = o.oid(+)
                AND     o.state = 2;
            EXCEPTION
                WHEN OTHERS THEN
                    ibs_error.log_error (ibs_error.error,
                                         'p_Attachment_01$ensureMaster',
                                         'Error in SELECT MIN (a.oid)');
                RAISE;
            END;
        END IF; -- if was a master set before
    END IF; -- if was the master set before the call of this procedure

    BEGIN
	    -- get the Id of the owner document:
        SELECT  containerId
        INTO    l_documentId 
        FROM    ibs_object
        WHERE   oid = ai_containerId;
    EXCEPTION
        WHEN OTHERS THEN
            ibs_error.log_error (ibs_error.error, 'p_Attachment_01$ensureMaster',
                                 'Error in SELECT containerId');
        RAISE;
    END;


    UPDATE  ibs_object -- set the selected property isMaster sero
--
-- CHANGED because OF internal ORACLE error using BITAND 
-- only values up to 2147483647 (= 0x7FFFFFFF) can be used (without highest bit!)
--
--         SET     flags = B_AND (flags, (4294967295 - c_MAS_FILE - c_MAS_HYPERLINK))
--                                     -- 0xFFFFFFFF
--
    SET     flags = B_AND (flags, (2147483647 - c_MAS_FILE - c_MAS_HYPERLINK))
                                -- 0x7FFFFFFF
    WHERE   oid = l_documentId;
        
        
    IF (l_masterId IS NOT null)         -- there is a master
    THEN
	    BEGIN
	        -- set the master object:
	        UPDATE  ibs_Attachment_01
	        SET     isMaster = 1
	        WHERE   oid = l_masterId;
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error (ibs_error.error, 'p_Attachment_01$ensureMaster',
                                     'Error in UPDATE ibs_Attachment_01');
            RAISE;
	    END;

        BEGIN
	        -- get the attachment type
	        SELECT  attachmentType
	        INTO    l_attachmentType     
            FROM    ibs_Attachment_01
            WHERE   oid = l_masterId;
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error (ibs_error.error, 'p_Attachment_01$ensureMaster',
                                     'Error in SELECT attachmentType');
            RAISE;
        END;
 
        IF  (l_attachmentType = c_MAS_FILE) -- is the attachment a File ?
        THEN
            BEGIN
                UPDATE ibs_object       -- set the selected property isMaster
                SET flags = B_OR  (flags, c_MAS_FILE )
                WHERE oid = l_documentId;
            EXCEPTION
                WHEN OTHERS THEN
                    ibs_error.log_error ( ibs_error.error,
                                          'p_Attachment_01$ensureMaster',
                                           'Error in set flags for FILE 1');
                RAISE;
            END;
        ELSIF  (l_attachmentType = c_MAS_HYPERLINK) THEN
                BEGIN
                    UPDATE ibs_object   -- set the selected property isMaster
                    SET flags = B_OR (flags, c_MAS_HYPERLINK )
                    WHERE oid = l_documentId;
                EXCEPTION
                    WHEN OTHERS THEN
                    ibs_error.log_error ( ibs_error.error,
                                          'p_Attachment_01$ensureMaster',
                                          'Error in set flags for HYPERLINK 2');
                    RAISE;
                END;
        END IF; -- if is the attachment a File
    END IF; -- if there is a master

    COMMIT WORK;
    -- return the state value
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Attachment_01$ensureMaster',
            'Input: ai_containerId = ' || ai_containerId || 
            ', ai_masterId = ' || ai_masterId || 
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;
END p_Attachment_01$ensureMaster;
/

show errors;

exit;
