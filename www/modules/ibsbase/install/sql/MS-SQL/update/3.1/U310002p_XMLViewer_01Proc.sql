/******************************************************************************
 * Update of stored procedure for the XMLViewer_01 objects. <BR>
 *
 * @author      Roland Burgermann(RB)  20120213
 ******************************************************************************
 */

-- delete existing procedure:
EXEC p_dropProc N'p_XMLViewer_01$BOCopy'
GO

-- create the new procedure:
CREATE PROCEDURE p_XMLViewer_01$BOCopy
(
    -- common input parameters:
    @oid            OBJECTID,
    @userId         USERID,
    @newOid         OBJECTID
)
AS
    -- definitions:
    -- define return constants:
    DECLARE @NOT_OK INT, @ALL_RIGHT INT
    -- set constants:
    SELECT  @NOT_OK = 0, @ALL_RIGHT = 1
    -- define return values:
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values:
    SELECT  @retValue = @NOT_OK
    -- declare local variables
    DECLARE @path NVARCHAR (255)
    DECLARE @oid_s OBJECTIDSTRING
    DECLARE @l_templateOid OBJECTID
    DECLARE @l_dbMapped BOOL
    DECLARE @l_showDOMTree BOOL
    DECLARE @l_procCopy NVARCHAR (30)

    -- CONVERTIONS (OBJECTID)
    EXEC    p_byteToString @oid, @oid_s OUTPUT

    SELECT @path = value + N'upload/files/'
    FROM ibs_System
    WHERE name = N'WWW_BASE_PATH'

    -- make an insert for all type specific tables:
    INSERT INTO ibs_Attachment_01
            (oid, filename, url, fileSize, path, attachmentType, isMaster)
    SELECT  @newOid, b.filename, b.url, b.fileSize,
            @path + @oid_s + RIGHT (@path, 1),
            b.attachmentType, b.isMaster
    FROM    ibs_Attachment_01 b
    WHERE   b.oid = @oid

    -- check if insert was performed correctly:
    IF (@@ROWCOUNT >= 1)                -- at least one row affected?
    BEGIN
        -- make an insert in the ibs_xmlviewer_01 table
        INSERT INTO ibs_XMLViewer_01
                (oid, templateOid)
        SELECT  @newOid, templateOid
        FROM    ibs_XMLViewer_01
        WHERE   oid = @oid

        -- check if insert was performed correctly:
        IF (@@ROWCOUNT >= 1)                -- at least one row affected?
        BEGIN
            -- for db-mapped objects we have to perform a insert in the
            -- mapping table too.

            SELECT @l_templateOid = templateOid
            FROM   ibs_XMLViewer_01
            WHERE oid = @newOid

            SELECT @l_dbMapped = 0
            SELECT @l_showDOMTree = 0
            SELECT @l_procCopy = N''

            SELECT @l_dbMapped = dbMapped,
                   @l_showDOMTree = showDOMTree,
                   @l_procCopy = procCopy
            FROM   ibs_DocumentTemplate_01
            WHERE  oid = @l_templateOid

            -- if the object is db-mapped perform the copy in the mapping table
            -- by calling the type specific copy procedure.
            -- the additional check on the procCopy is necessary, because
            -- not all objects must have a database table and thus a copy
            -- function (e.g. a simple overview with only a QUERY field)
            IF (@l_dbMapped = 1 AND @l_procCopy <> N'')
            BEGIN
                EXEC @retValue = @l_procCopy @oid, @newOid
                if (@retValue = 1)
                    SELECT  @retValue = @ALL_RIGHT  -- set return value
            ELSE
                SELECT  @retValue = @ALL_RIGHT  -- set return value
            END
        END
    END

    -- return the state value:
    RETURN  @retValue
GO
-- p_XMLViewer_01$BOCopy