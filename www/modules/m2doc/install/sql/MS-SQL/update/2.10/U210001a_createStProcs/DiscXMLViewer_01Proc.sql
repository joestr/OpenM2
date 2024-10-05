/******************************************************************************
 * All stored procedures regarding the DiscXMLViewer_01 Object. <BR>
 *
 * @version     $Id: DiscXMLViewer_01Proc.sql,v 1.1 2010/02/25 13:54:18 btatzmann Exp $
 *
 * @author      Keim Christine (CK)  001010
 ******************************************************************************
 */


/******************************************************************************
 * Creates a new DiscXMLViewer_01 Object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @userId             ID of the user who is creating the object.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 * @param   @tVersionId         Type of the new object.
 * @param   @name               Name of the object.
 * @param   @containerId_s      ID of the container where object shall be 
 *                              created in.
 * @param   @containerKind      Kind of object/container relationship
 * @param   @isLink             Defines if the object is a link
 * @param   @linkedObjectId_s   If the object is a link this is the ID of the
 *                              where the link shows to.
 * @param   @description        Description of the object.
 * @param   @templateOid_s      oid of the template this object uses
 * @param   @wfTemplateOid_s    oid of the workflow this object uses
 *
 * @output parameters:
 * @param   @oid_s              OID of the newly created object.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
-- delete existing procedure:
IF EXISTS ( SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.p_DiscXMLViewer_01$create') 
                AND sysstat & 0xf = 4)
    DROP PROCEDURE #CONFVAR.ibsbase.dbOwner#.p_DiscXMLViewer_01$create
GO

-- create the new procedure:
CREATE PROCEDURE p_DiscXMLViewer_01$create
(
    -- input parameters:
    @userId         USERID,
    @op             INT,
    @tVersionId     TVERSIONID,
    @name           NAME,
    @containerId_s  OBJECTIDSTRING,
    @containerKind  INT,
    @isLink         BOOL,
    @linkedObjectId_s OBJECTIDSTRING,
    @description    DESCRIPTION,
    -- xml viewer specific input parameters:
    --@templateOid_s  OBJECTIDSTRING,
    --@wfTemplateOid_s OBJECTIDSTRING,
    -- output parameters:
    @oid_s          OBJECTIDSTRING OUTPUT
)
AS
    -- convertions (objectidstring) - all input objectids must be converted
    DECLARE @containerId    OBJECTID
    DECLARE @linkedObjectId OBJECTID

    EXEC p_stringToByte @containerId_s, @containerId OUTPUT
    EXEC p_stringToByte @linkedObjectId_s, @linkedObjectId OUTPUT

    -- definitions:
    -- define return constants:
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @ALREADY_EXISTS INT
    -- set constants:
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2, @ALREADY_EXISTS = 21
    -- define return values:
    DECLARE @retValue INT                   -- return value of this procedure
    -- initialize return values:
    SELECT  @retValue = @ALL_RIGHT
    -- define local variables:
    DECLARE @oid OBJECTID
	-- initialize local variables:
    SELECT  @oid = 0x0000000000000000
    DECLARE @posNoPath POSNOPATH_VC
    DECLARE @discussionId OBJECTID
    DECLARE @rights RIGHTS
    DECLARE @actRights RIGHTS


    -- body:
    BEGIN TRANSACTION
        -- create base object:
        EXEC @retValue = p_XMLViewer_01$create @userId, @op, @tVersionId, @name, @containerId_s,
                @containerKind, @isLink, @linkedObjectId_s, @description, @oid_s OUTPUT

        IF (@retValue = @ALL_RIGHT)     -- object and xmlviewer entries created successfully?
        BEGIN
	    -- conversions
	    EXEC p_stringToByte @oid_s, @oid OUTPUT

            -- retrieve the posNoPath of the entry
            SELECT @posNoPath = posNoPath
                    FROM ibs_Object
                    WHERE oid = @oid

            -- retrieve the oid of the discussion
            SELECT  @discussionId = oid
            FROM    ibs_Object
            WHERE   tVersionId = 0x01010321
                AND @posNoPath LIKE posNoPath + '%'

            -- insert the other values
            INSERT INTO m2_Article_01 (oid, content, discussionId)
            SELECT  @oid, @description, @discussionid

        END -- if object created successfully

    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @retValue
GO


/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be changed.
 * @param   @userId             ID of the user who is creating the object.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 *
 * @output parameters:
 * @param   @state              The object's state.
 * @param   @tVersionId         ID of the object's type (correct version).
 * @param   @typeName           Name of the object's type.
 * @param   @name               Name of the object itself.
 * @param   @containerId        ID of the object's container.
 * @param   @containerKind      Kind of object/container relationship.
 * @param   @isLink             Is the object a link?
 * @param   @linkedObjectId     Link if isLink is true.
 * @param   @owner              ID of the owner of the object.
 * @param   @creationDate       Date when the object was created.
 * @param   @creator            ID of person who created the object.
 * @param   @lastChanged        Date of the last change of the object.
 * @param   @changer            ID of person who did the last change to the 
 *                              object.
 * @param   @validUntil         Date until which the object is valid.
 * @param   @showInNews         show in news flag.
 * @param   @checkedOut         Is the object checked out?
 * @param   @checkOutDate       Date when the object was checked out
 * @param   @checkOutUser       id of the user which checked out the object
 * @param   @checkOutUserOid    Oid of the user which checked out the object
 *                              is only set if this user has the right to READ
 *                              the checkOut user
 * @param   @checkOutUserName   name of the user which checked out the object,
 *                              is only set if this user has the right to view
 *                              the checkOut-User
 * @param   @ao_templateOid     Oid of the template this object uses.
 * @param   @ao_wfTemplateOid_s oid of the workflow this object uses
 * @param   @ao_dbMapped        Object is dbmapped
 * @param   @ao_systemDisplayMode  should the system attributes be shown
 * @param   @level              Discussionlevel which this object is in
 * @param   @hasSubEntries      Number of the subentries the object(entry) has
 * @param   @rights             rights this user has on this object
 * @param   @discussionId       Oid of the discussion this object is pertinent to
 *
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- delete existing procedure:
IF EXISTS ( SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.p_DiscXMLViewer_01$retrieve') 
                AND sysstat & 0xf = 4)
    DROP PROCEDURE #CONFVAR.ibsbase.dbOwner#.p_DiscXMLViewer_01$retrieve
GO

-- create the new procedure:
CREATE PROCEDURE p_DiscXMLViewer_01$retrieve
(
    -- input parameters:
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT,
    -- output parameters
    @state          STATE           OUTPUT,
    @tVersionId     TVERSIONID      OUTPUT,
    @typeName       NAME            OUTPUT,
    @name           NAME            OUTPUT,
    @containerId    OBJECTID        OUTPUT,
    @containerName  NAME            OUTPUT,
    @containerKind  INT             OUTPUT,
    @isLink         BOOL            OUTPUT,
    @linkedObjectId OBJECTID        OUTPUT,
    @owner          USERID          OUTPUT,
    @ownerName      NAME            OUTPUT,
    @creationDate   DATETIME        OUTPUT,
    @creator        USERID          OUTPUT,
    @creatorName    NAME            OUTPUT,
    @lastChanged    DATETIME        OUTPUT,
    @changer        USERID          OUTPUT,
    @changerName    NAME            OUTPUT,
    @validUntil     DATETIME        OUTPUT,
    @description    DESCRIPTION     OUTPUT,
    @showInNews     BOOL            OUTPUT,
    @checkedOut     BOOL            OUTPUT,
    @checkOutDate   DATETIME        OUTPUT,
    @checkOutUser   USERID          OUTPUT,
    @checkOutUserOid OBJECTID       OUTPUT,
    @checkOutUserName NAME          OUTPUT,
    @ao_templateOid OBJECTID        OUTPUT,
    @ao_wfTemplateOid OBJECTID      OUTPUT,
    @ao_systemDisplayMode    INT    OUTPUT,
    @ao_dbMapped    BOOL            OUTPUT,
    @ao_showDOMTree BOOL            OUTPUT,
    @level          INT             OUTPUT,
    @hasSubEntries  INT             OUTPUT,
    @rights         INT             OUTPUT,
    @discussionId   OBJECTID        OUTPUT
)
AS
    -- definitions:
    -- define return constants:
    DECLARE @ALL_RIGHT INT
    -- set constants:
    SELECT  @ALL_RIGHT = 1
    -- define return values:
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values:
    SELECT  @retValue = @ALL_RIGHT
    -- define local variables:
    DECLARE @oid OBJECTID
    DECLARE @olevel INT
    SELECT @olevel = -1

    -- conversions
    EXEC p_stringToByte @oid_s, @oid OUTPUT

    -- body:
    BEGIN TRANSACTION
        -- retrieve the base object data:
        EXEC @retValue = p_XMLViewer_01$retrieve
                @oid_s, @userId, @op,
                @state OUTPUT, @tVersionId OUTPUT, @typeName OUTPUT, 
                @name OUTPUT, @containerId OUTPUT, @containerName OUTPUT, 
                @containerKind OUTPUT, @isLink OUTPUT, @linkedObjectId OUTPUT, 
                @owner OUTPUT, @ownerName OUTPUT, 
                @creationDate OUTPUT, @creator OUTPUT, @creatorName OUTPUT,
                @lastChanged OUTPUT, @changer OUTPUT, @changerName OUTPUT,
                @validUntil OUTPUT, @description OUTPUT, @showInNews OUTPUT, 
                @checkedOut OUTPUT, @checkOutDate OUTPUT, 
                @checkOutUser OUTPUT, @checkOutUserOid OUTPUT, @checkOutUserName OUTPUT, 
		        @ao_templateOid OUTPUT, @ao_wfTemplateOid OUTPUT, 
                @ao_systemDisplayMode OUTPUT, @ao_dbMapped OUTPUT, @ao_showDOMTree OUTPUT

        -- retrieve the olevel of the entry
        SELECT @olevel = o.olevel, @discussionId = discussionId
                FROM ibs_Object o JOIN m2_Article_01 b ON o.oid = b.oid
                WHERE o.oid = @oid

        -- compute the level of the entry
	SELECT @level = @olevel - olevel
	FROM ibs_Object
	WHERE oid = @discussionId

        -- retrieve if entry has subEntries
        SELECT  @hasSubEntries = COUNT(*) 
                    FROM ibs_Object
                    WHERE tVersionId IN (0x01017511)
                    AND containerId = @oid                    
                    AND state = 2

        -- retrieve the rights of the user for this entry
        EXEC p_Rights$getRights @oid, @userId, @rights OUTPUT

    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @retValue
GO
-- p_DiscXMLViewer_01$retrieve

/******************************************************************************
 * Deletes a DiscXMLViewer_01 object and all its values (incl. rights check). <BR>
 * This procedure also delets all links showing to this object.
 * 
 * @input parameters:
 * @param   @oid_s              ID of the object to be deleted.
 * @param   @userId             ID of the user who is deleting the object.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
IF EXISTS ( SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.p_DiscXMLViewer_01$delete') 
                AND sysstat & 0xf = 4)
    DROP PROCEDURE #CONFVAR.ibsbase.dbOwner#.p_DiscXMLViewer_01$delete
GO

CREATE PROCEDURE p_DiscXMLViewer_01$delete
(
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT
)
AS
    -- convertions (objectidstring) - all input objectids must be converted
    DECLARE @oid OBJECTID
    EXEC    p_stringToByte @oid_s, @oid OUTPUT

    -- definitions:
    -- define return constants:
    DECLARE @INSUFFICIENT_RIGHTS INT, @ALL_RIGHT INT, @OBJECTNOTFOUND INT
    -- set constants:
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2, @OBJECTNOTFOUND = 3
    -- define return values:
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values:
    SELECT  @retValue = @ALL_RIGHT
    -- define local variables:
	-- initialize local variables:
    DECLARE @containerId OBJECTID

    -- conversions
    EXEC p_stringToByte @oid_s, @oid OUTPUT

    -- body:
    BEGIN TRANSACTION
        -- delete base object:
        EXEC @retValue = p_XMLViewer_01$delete @oid_s, @userId, @op

        IF (@retValue = @ALL_RIGHT)     -- operation properly performed?
        BEGIN
            -- delete object type specific data:
            -- (deletes all type specific tuples which are not within ibs_Object)
            DELETE  m2_Article_01
            WHERE   oid = @oid

        END -- if operation properly performed
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @retValue
GO
-- p_DiscXMLViewer_01$delete

/******************************************************************************
 * Copy an object and all its values. <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be deleted.
 * @param   @userId             ID of the user who is deleting the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */
if exists (select * 
           from sysobjects 
           where id = object_id('#CONFVAR.ibsbase.dbOwner#.p_DiscXMLViewer_01$BOCopy') 
               and sysstat & 0xf = 4)
    drop procedure #CONFVAR.ibsbase.dbOwner#.p_DiscXMLViewer_01$BOCopy
GO

-- create the new procedure:
CREATE PROCEDURE p_DiscXMLViewer_01$BOCopy
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
    DECLARE @path NVARCHAR (255)
    DECLARE @oid_s OBJECTIDSTRING
    DECLARE @discussionId OBJECTID
    DECLARE @posNoPath POSNOPATH_VC
    DECLARE @copiedDisc BOOL
    SELECT @copiedDisc = 0
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
        SELECT  @retValue = @ALL_RIGHT  -- set return value

    IF (@retValue = @ALL_RIGHT)
    BEGIN

        -- retrieve the posNoPath of the entry
        SELECT @posNoPath = posNoPath
        FROM ibs_Object
        WHERE oid = @newOid

        -- retrieve the oid of the discussion
        SELECT  @discussionId = oid
        FROM    ibs_Object
        WHERE   tVersionId = 0x01010321
            AND @posNoPath LIKE posNoPath + '%'

        SELECT @copiedDisc = COUNT (*)
        FROM ibs_Copy
        WHERE oldOid = @discussionId

        IF (@copiedDisc=1)
                SELECT @discussionId = newOid
                FROM ibs_Copy
                WHERE oldOid = @discussionId

        -- make an insert for all type specific tables:
        INSERT INTO m2_Article_01 
                 (oid, content, discussionId)
        SELECT  @newOid, b.content, @discussionId
        FROM    m2_Article_01 b
        WHERE   b.oid = @oid

        -- check if insert was performed correctly:
        IF (@@ROWCOUNT >= 1)                -- at least one row affected?
            SELECT  @retValue = @ALL_RIGHT  -- set return value

    END

    -- return the state value:
    RETURN  @retValue
GO
-- p_DiscXMLViewer_01$BOCopy
