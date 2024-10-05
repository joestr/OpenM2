/******************************************************************************
 * All stored procedures regarding the ibs_attachment_01 table. <BR>
 * 
 *
 * @version     1.10.0001, 03.08.1999
 *
 * @author      Heinz Stampfer (KR)  980521
 *
 * <DT><B>Updates:</B>
 * 
 * <DD>MS 990803    Code cleaning.
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
 * @param   @ai_containerId         ID of the object's container.
 * @param   @ai_masterId            ID of the new master(only at copy) or null.
 *
 * @output parameters:
 *
 * @returns A value representing the state of the procedure.
 *  @c_ALL_RIGHT                    Action performed, values returned,
 *                                  everything ok.
 */

-- delete existing procedure:
EXEC p_dropProc N'p_Attachment_01$ensureMaster'
GO

-- create the new procedure:
CREATE PROCEDURE p_Attachment_01$ensureMaster
(
    -- input parameters:
    @ai_containerId         OBJECTID,       -- the oid of the Container we want
                                            -- to check
    @ai_masterId            OBJECTID        -- id of the new master
    -- output parameters:
)
AS
DECLARE
    -- constants:
    @c_ALL_RIGHT            INT,            -- operation was o.k.
    @c_NOOID                OBJECTID,       -- oid of no valid object
    @c_MAS_FILE             INT,            -- constant for file Attachment
    @c_MAS_HYPERLINK        INT,            -- constant for hyperlink Attachment

    -- local variables:
    @l_retValue             INT,            -- return value of this function
    @l_documentId           OBJECTID,       -- id of the owner document
    @l_attachmentType       INT,            -- type of Attachment
    @l_count                INT             -- counter                                       

    -- assign constants:
SELECT
    @c_ALL_RIGHT            = 1,
    @c_NOOID                = 0x0000000000000000,
    @c_MAS_FILE             = 1,
    @c_MAS_HYPERLINK        = 2
    
    -- initialize local variables and return values:
SELECT
    @l_retValue             = @c_ALL_RIGHT,
    @l_documentId           = @c_NOOID,
    @l_count                = 0

    -- body:
    BEGIN TRANSACTION
        -- control if there is no master for the document:
        SELECT  @l_count = COUNT (a.oid)    -- search for actual master
        FROM    ibs_Attachment_01 a
        JOIN	ibs_Object o ON a.oid = o.oid
         AND    o.state = 2
         AND 	a.isMaster = 1
         AND 	o.containerId = @ai_containerId
    
        IF (@l_count > 1 OR @ai_masterId <> null) -- are there more than one
                                                  -- master ?
        BEGIN
            -- delete all masters:
            UPDATE  ibs_Attachment_01
            SET     isMaster = 0
            WHERE   oid IN (SELECT  o.oid 
                            FROM    ibs_object o
	                        WHERE   o.containerId = @ai_containerId
                           )
        END -- if are there more than one master
            
        IF (@ai_masterId = null)            -- should the actuall attachment be
                                            -- the new master ?
        BEGIN
            IF (@l_count = 1)               -- was a master set before ?
            BEGIN
                -- get the actuall master
                SELECT          @ai_masterId = a.oid
                FROM            ibs_Attachment_01 a
                LEFT OUTER JOIN ibs_Object o ON a.oid = o.oid
                WHERE           o.containerId = @ai_containerId
                  AND           o.state = 2
                  AND           a.isMaster = 1
            END -- if was a master set before
            ELSE                            -- is a new master set
            BEGIN
                -- find a new master, the oldest one
                SELECT          @ai_masterId = MIN (a.oid)
                FROM            ibs_Attachment_01 a
                LEFT OUTER JOIN ibs_Object o ON a.oid = o.oid
                WHERE           o.containerId = @ai_containerId
                  AND           o.state = 2
            END -- else is a new master set
        END -- if should the actuall attachment be the new master

	    -- get the Id of the owner document:
        SELECT  @l_documentId = containerId
        FROM    ibs_object
        WHERE   oid = @ai_containerId

        UPDATE  ibs_object -- set the selected property isMaster
        SET     flags = (flags & (0xFFFFFFFF ^ (@c_MAS_FILE | @c_MAS_HYPERLINK)))
        WHERE   oid = @l_documentId

        IF (@ai_masterId <> null)           -- there is a master
	    BEGIN
	        -- set the master object:
	        UPDATE  ibs_Attachment_01
	        SET     isMaster = 1
	        WHERE   oid = @ai_masterId

	        -- get the attachment type
	        SELECT  @l_attachmentType = attachmentType 
            FROM    ibs_Attachment_01
            WHERE   oid = @ai_masterId

            IF (@l_attachmentType = @c_MAS_FILE) -- is the attachment a file ?
            BEGIN
                UPDATE  ibs_object          -- set the selected property
                                            -- isMaster
                SET     flags = (flags  | @c_MAS_FILE)
                WHERE   oid = @l_documentId
            END -- if is the attachment a file
            ELSE IF (@l_attachmentType = @c_MAS_HYPERLINK) -- is the attachment
                                                           -- a hyperlink ?
            BEGIN
                UPDATE  ibs_object          -- set the selected property
                                            -- isMaster
                SET     flags = (flags  | @c_MAS_HYPERLINK)
                WHERE   oid = @l_documentId
            END -- if is the attachment a hyperlink
        END -- if there is a master
    COMMIT TRANSACTION

    -- return the state value:
    RETURN @l_retValue
GO
-- p_Attachment_01$ensureMaster