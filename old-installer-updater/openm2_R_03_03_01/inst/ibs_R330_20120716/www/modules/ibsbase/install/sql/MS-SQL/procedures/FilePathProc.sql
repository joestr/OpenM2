/******************************************************************************
 * A Stored Procedure which actualizes the path of moved/copied files. <BR>
 *
 * @version     1.11.0001, 05.05.2000
 *
 * @author      Christine Keim    (CK)  000505
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

-- delete existing procedure:
EXEC p_dropProc N'p_FilePathData$change'
GO

-- create the new procedure:
CREATE PROCEDURE p_FilePathData$change
(
    -- input parameters:
    @rootOid_s   OBJECTIDSTRING,
    @recursive   BOOL
    -- output parameters
)
AS
    DECLARE @rootOid OBJECTID
    DECLARE @path NVARCHAR (255)
    DECLARE @oid OBJECTID
    DECLARE @oid_s OBJECTIDSTRING
    DECLARE @tVersion TVERSIONID
    DECLARE @containerId OBJECTID
    DECLARE @containerId_s OBJECTIDSTRING
    DECLARE @fileName NVARCHAR (255)
    DECLARE @attachmentType INTEGER

    EXEC p_stringToByte @rootOid_s, @rootOid OUTPUT

    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT, @OBJECTNOTFOUND INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @OBJECTNOTFOUND = 3
    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT

    BEGIN TRANSACTION

            -- define cursor:
            DECLARE FileHD_Cursor INSENSITIVE CURSOR FOR
                SELECT  f.path, o.oid, CASE
                                       WHEN (f.attachmentType = 3) THEN o.oid
                                       ELSE o.containerId
                                       END AS containerId,
                        f.fileName, f.attachmentType, o.tVersionId
	        FROM ibs_Attachment_01 f JOIN ibs_Object o ON f.oid = o.oid, ibs_Object root
	        WHERE root.oid = @rootOid
	        AND f.attachmentType <> 2
	        AND o.state = 2
	        AND fileSize <> 0.0
            AND o.posNoPath LIKE root.posNoPath + '%'
                AND (@recursive=1 OR o.oid = @rootOid )

            -- open the cursor:
            OPEN    FileHD_Cursor

            -- get the first user:
            FETCH NEXT FROM FileHD_Cursor INTO @path, @oid, @containerId, @fileName, @attachmentType, @tVersion

            -- loop through all found tupels:
            WHILE (@@FETCH_STATUS <> -1)            -- another file found?
            BEGIN
                -- Da @@FETCH_STATUS einen der drei Werte -2, -1 oder 0
                -- besitzen kann, müssen alle drei Fälle geprüft werden.
                -- In diesem Fall wird eine Tabelle, wenn sie während der
                -- Ausführung der Prozedur gelöscht wurde, übersprungen.
                -- Ein erfolgreicher Abruf (0) veranlaßt die Ausführung
                -- von DBCC innerhalb der BEGIN..END-Schleife.
                IF (@@FETCH_STATUS <> -2)
                BEGIN

                    EXEC p_byteToString @containerId, @containerId_s OUTPUT
                    EXEC p_byteToString @oid, @oid_s OUTPUT

                    IF (@attachmentType=1)
                    BEGIN
                        IF (@recursive=1) AND (@tVersion!=0x01010051)
                            SELECT @fileName = @oid_s + SUBSTRING (@fileName, 19, DATALENGTH (@fileName))
                    END

                    -- update the 
                    UPDATE ibs_Attachment_01
		    SET path = SUBSTRING (@path,1,DATALENGTH (@path) - 19) + @containerId_s + RIGHT (@path, 1),
                        fileName = @fileName
                    WHERE oid = @oid

                END -- if
                -- get next tupel:
                FETCH NEXT FROM FileHD_Cursor INTO @path, @oid, @containerId, @fileName, @attachmentType, @tVersion
            END -- while another user found

            DEALLOCATE FileHD_Cursor

        COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO

-- p_FilePathData$change
