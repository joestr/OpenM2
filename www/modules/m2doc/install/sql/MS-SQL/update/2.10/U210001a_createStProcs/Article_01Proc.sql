/******************************************************************************
 * All stored procedures regarding the Article_01 Object. <BR>
 *
 * @version     $Id: Article_01Proc.sql,v 1.1 2010/02/25 13:54:18 btatzmann Exp $
 *
 * @author      Keim Christine (CK)  980504
 ******************************************************************************
 */


/******************************************************************************
 * Creates a new Article_01 Object (incl. rights check). <BR>
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
 *
 * @output parameters:
 * @param   @oid_s              OID of the newly created object.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Article_01$create'
GO

-- create the new procedure:
CREATE PROCEDURE p_Article_01$create
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
        EXEC @retValue = p_Object$performCreate @userId, @op, @tVersionId, @name, @containerId_s,
                @containerKind, @isLink, @linkedObjectId_s, @description, 
                @oid_s OUTPUT, @oid OUTPUT

	    IF (@retValue = @ALL_RIGHT)     -- object created successfully?
        BEGIN
            -- retrieve the posNoPath of the entry
            SELECT @posNoPath = posNoPath
                    FROM ibs_Object
                    WHERE oid = @oid

            -- retrieve the oid of the discussion
            SELECT  @discussionId = oid
            FROM    ibs_Object
            WHERE   tVersionId IN (0x01010301, 0x01010A01)
                AND @posNoPath LIKE posNoPath + '%'

            -- insert the other values
            INSERT INTO m2_Article_01 (oid, content, discussionId)
            SELECT  @oid, @description, @discussionid

            IF (SUBSTRING (@containerId, 1, DATALENGTH(0x01010501)) = 0x01010501)
            BEGIN 
                UPDATE ibs_Object 
                SET name = (SELECT N'AW: ' + name
                            FROM ibs_Object
                            WHERE oid = @containerId)
                WHERE oid = @oid
            END

        END -- if object created successfully

    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @retValue
GO
-- p_Article_01$create


/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be changed.
 * @param   @userId             ID of the user who is creating the object.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 * @param   @name               Name of the object.
 * @param   @validUntil         Date until which the object is valid.
 * @param   @showInNews         show in news flag.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Article_01$change'
GO

-- create the new procedure:
CREATE PROCEDURE p_Article_01$change
(
    -- input parameters:
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT,
    @name           NAME,
    @validUntil     DATETIME,
    @description    DESCRIPTION,
    @showInNews     BOOL
)
AS
    -- definitions:
    -- define return constants:
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- set constants:
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2, @OBJECTNOTFOUND = 3
    -- define return values:
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values:
    SELECT  @retValue = @ALL_RIGHT
    -- define local variables:
    DECLARE @oid OBJECTID
	-- initialize local variables:


    -- body:
    BEGIN TRANSACTION
        -- perform the change of the object:
        EXEC @retValue = p_Object$performChange @oid_s, @userId, @op, @name,
                @validUntil, '', @showInNews, @oid OUTPUT

        UPDATE m2_Article_01
        SET state = 
        ( SELECT o.state
            FROM ibs_Object o
            WHERE o.oid = @oid)
        WHERE oid = @oid


    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @retValue
GO
-- p_Article_01$change


/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be changed.
 * @param   ai_userId           ID of the user who is creating the object.
 * @param   ai_op               Operation to be performed (used for rights 
 *                              check).
 *
 * @output parameters:
 * @param   ao_state            The object's state.
 * @param   ao_tVersionId       ID of the object's type (correct version).
 * @param   ao_typeName         Name of the object's type.
 * @param   ao_name             Name of the object itself.
 * @param   ao_containerId      ID of the object's container.
 * @param   ao_containerName    Name of the object's container.
 * @param   ao_containerKind    Kind of object/container relationship.
 * @param   ao_isLink           Is the object a link?
 * @param   ao_linkedObjectId   Link if isLink is true.
 * @param   ao_owner            ID of the owner of the object.
 * @param   ao_ownerName        Name of the owner of the object.
 * @param   ao_creationDate     Date when the object was created.
 * @param   ao_creator          ID of person who created the object.
 * @param   ao_creatorName      Name of person who created the object.
 * @param   ao_lastChanged      Date of the last change of the object.
 * @param   ao_changer          ID of person who did the last change to the 
 *                              object.
 * @param   ao_changerName      Name of person who did the last change to the 
 *                              object.
 * @param   ao_validUntil       Date until which the object is valid.
 * @param   ao_description      Description of the object.
 * @param   ao_showInNews       Flag if object should be shown in newscontainer
 * @param   ao_checkedOut       Is the object checked out?
 * @param   ao_checkOutDate     Date when the object was checked out
 * @param   ao_checkOutUser     id of the user which checked out the object
 * @param   ao_checkOutUserOid  Oid of the user which checked out the object
 *                              is only set if this user has the right to READ
 *                              the checkOut user
 * @param   ao_checkOutUserName Name of the user which checked out the object,
 *                              is only set if this user has the right to view
 *                              the checkOut-User
 * @param   ao_discussionType   Type of discussion.
 * @param   ao_hasSubEntries    Does the object have sub entries?
 * @param   ao_rights           Permissions of the current user on the object.
 * @param   ao_discussionId     Id of discussion where the object belongs to.
 * @param   ao_containerDescription Description of container of the object
 *                              (out of ibs_Object).
 *
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Article_01$retrieve'
GO

-- create the new procedure:
CREATE PROCEDURE p_Article_01$retrieve
(
    -- input parameters:
    @ai_oid_s               OBJECTIDSTRING,
    @ai_userId              USERID,
    @ai_op                  INT,
    -- commmon output parameters:
    @ao_state               STATE           OUTPUT,
    @ao_tVersionId          TVERSIONID      OUTPUT,
    @ao_typeName            NAME            OUTPUT,
    @ao_name                NAME            OUTPUT,
    @ao_containerId         OBJECTID        OUTPUT,
    @ao_containerName       NAME            OUTPUT,
    @ao_containerKind       INT             OUTPUT,
    @ao_isLink              BOOL            OUTPUT,
    @ao_linkedObjectId      OBJECTID        OUTPUT,
    @ao_owner               USERID          OUTPUT,
    @ao_ownerName           NAME            OUTPUT,
    @ao_creationDate        DATETIME        OUTPUT,
    @ao_creator             USERID          OUTPUT,
    @ao_creatorName         NAME            OUTPUT,
    @ao_lastChanged         DATETIME        OUTPUT,
    @ao_changer             USERID          OUTPUT,
    @ao_changerName         NAME            OUTPUT,
    @ao_validUntil          DATETIME        OUTPUT,
    @ao_description         DESCRIPTION     OUTPUT,
    @ao_showInNews          BOOL            OUTPUT,
    @ao_checkedOut          BOOL            OUTPUT,
    @ao_checkOutDate        DATETIME        OUTPUT,
    @ao_checkOutUser        USERID          OUTPUT,
    @ao_checkOutUserOid     OBJECTID        OUTPUT,
    @ao_checkOutUserName    NAME            OUTPUT,
    -- specific output parameters:
    @ao_discussionType      INT             OUTPUT,
    @ao_hasSubEntries       INT             OUTPUT,
    @ao_rights              RIGHTS          OUTPUT,
    @ao_discussionId        OBJECTID        OUTPUT,
    @ao_containerDescription DESCRIPTION    OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this
                                            -- operation
    @c_OBJECTNOTFOUND       INT,            -- the object was not found                                        
    @c_DISC_BLACKBOARD      INT,            -- discussion type blackboard
    @c_DISC_DISCUSSION      INT,            -- standard discussion type
    @c_ST_ACTIVE            INT,            -- active object state

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_TV_Discussion_01     INT,            -- tVersionId of
                                            -- standard discussion
    @l_TV_Blackboard_01     INT,            -- tVersionId of
                                            -- blackboard
    @l_TV_DiscEntry_01      INT,            -- tVersionId of
                                            -- discussion entry
    @l_oid                  OBJECTID        -- the oid of the current object

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_OBJECTNOTFOUND       = 3,
    @c_DISC_BLACKBOARD      = 0,
    @c_DISC_DISCUSSION      = 1,
    @c_ST_ACTIVE            = 2

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0

-- body:
    -- retrieve the base object data:
    EXEC @l_retValue = p_Object$performRetrieve
            @ai_oid_s, @ai_userId, @ai_op,
            @ao_state OUTPUT, @ao_tVersionId OUTPUT, @ao_typeName OUTPUT, 
            @ao_name OUTPUT,
            @ao_containerId OUTPUT, @ao_containerName OUTPUT,
            @ao_containerKind OUTPUT,
            @ao_isLink OUTPUT, @ao_linkedObjectId OUTPUT, 
            @ao_owner OUTPUT, @ao_ownerName OUTPUT, 
            @ao_creationDate OUTPUT, @ao_creator OUTPUT, @ao_creatorName OUTPUT,
            @ao_lastChanged OUTPUT, @ao_changer OUTPUT, @ao_changerName OUTPUT,
            @ao_validUntil OUTPUT, @ao_description OUTPUT,
            @ao_showInNews OUTPUT, 
            @ao_checkedOut OUTPUT, @ao_checkOutDate OUTPUT, 
            @ao_checkOutUser OUTPUT, @ao_checkOutUserOid OUTPUT,
            @ao_checkOutUserName OUTPUT, 
            @l_oid OUTPUT

    IF (@l_retValue = @c_ALL_RIGHT) -- operation performed properly?
    BEGIN
        -- retrieve object type specific data:
        -- get the data of the tVersions:
        SELECT  @l_TV_Discussion_01 = actVersion
        FROM    ibs_Type
        WHERE   code = N'Discussion'

        SELECT  @l_TV_Blackboard_01 = actVersion
        FROM    ibs_Type
        WHERE   code = N'BlackBoard'

        SELECT  @l_TV_DiscEntry_01 = actVersion
        FROM    ibs_Type
        WHERE   code = N'Article'

        -- retrieve the discussionId of the entry:
        SELECT  @ao_discussionId = discussionId
        FROM    m2_Article_01
        WHERE   oid = @l_oid

        -- check if there occurred an error:
        EXEC @l_error = ibs_error.prepareError @@error,
            N'get discussionId', @l_ePos OUTPUT
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call common exception handler

        -- retrieve the type of the discussion:
        SELECT  @ao_discussionType =
                CASE
                     WHEN tVersionId = @l_TV_Blackboard_01
                     THEN @c_DISC_BLACKBOARD
                     ELSE @c_DISC_DISCUSSION
                END
        FROM    ibs_Object
        WHERE   tVersionId IN (@l_TV_Discussion_01, @l_TV_Blackboard_01)
            AND oid = @ao_discussionId

        -- check if there occurred an error:
        EXEC @l_error = ibs_error.prepareError @@error,
            N'get discussionType', @l_ePos OUTPUT
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call common exception handler

        -- retrieve if entry has subEntries:
        IF (@ao_discussionType = @c_DISC_DISCUSSION) -- standard discussion?
        BEGIN
            -- get the number of sub entries of the current entry:
            SELECT  @ao_hasSubEntries = COUNT (*) 
            FROM    ibs_Object
            WHERE   tVersionId = @l_TV_DiscEntry_01
                AND state = @c_ST_ACTIVE
                AND containerId = @l_oid

            -- check if there occurred an error:
            EXEC @l_error = ibs_error.prepareError @@error,
                N'get subEntries', @l_ePos OUTPUT
            IF (@l_error <> 0)          -- an error occurred?
                GOTO exception          -- call common exception handler
        END -- if standard discussion
        ELSE                            -- other type of discussion
        BEGIN
            -- there are no sub entries allowed:
            SELECT  @ao_hasSubEntries = 0
        END -- else other type of discussion

        -- retrieve the description of the container:
        SELECT  @ao_containerDescription = description
        FROM    ibs_Object
        WHERE   oid = @ao_containerId

        -- check if there occurred an error:
        EXEC @l_error = ibs_error.prepareError @@error,
            N'get containerDescription', @l_ePos OUTPUT
        IF (@l_error <> 0)              -- an error occurred?
            GOTO exception              -- call common exception handler

        EXEC p_Rights$getRights @l_oid, @ai_userId, @ao_rights OUTPUT
    END -- if operation performed properly

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- log the error:
    EXEC ibs_error.logError 500, N'p_Article_01$retrieve', @l_error, @l_ePos,
            N'ai_userId', @ai_userId,
            N'ai_oid_s', @ai_oid_s,
            N'ai_op', @ai_op
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_Article_01$retrieve



/******************************************************************************
 * Deletes a Discussion_01 object and all its values (incl. rights check). <BR>
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
-- delete existing procedure:
EXEC p_dropProc N'p_Article_01$delete'
GO

-- create the new procedure:
CREATE PROCEDURE p_Article_01$delete
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

    -- body:
    BEGIN TRANSACTION
        -- delete base object:
        EXEC @retValue = p_Object$performDelete @oid_s, @userId, @op, 
                @oid OUTPUT

/*
        IF (@retValue = @ALL_RIGHT)     -- operation properly performed?
        BEGIN
            -- delete object type specific data:
            -- (deletes all type specific tuples which are not within ibs_Object)
	    DELETE  m2_Article_01
            WHERE   oid = @oid

            DELETE ibs_Object
            WHERE  oid IN (SELECT oid 
                           FROM ibs_Object 
                           LIKE posNoPath LIKE @posNoPath + '%')
        END -- if operation properly performed
*/        
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @retValue
GO
-- p_Article_01$delete




-- delete existing procedure:
EXEC p_dropProc N'p_Article_01$BOCopy'
GO

-- create the new procedure:
CREATE PROCEDURE p_Article_01$BOCopy
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
    -- local variable ... OID of the discussion in which this Article is part of
    DECLARE @discussionId OBJECTID
    DECLARE @posNoPath POSNOPATH_VC
    DECLARE @copiedDisc BOOL
    SELECT @copiedDisc = 0

    -- retrieve the posNoPath of the entry
    SELECT @posNoPath = posNoPath
    FROM ibs_Object
    WHERE oid = @newOid

    -- retrieve the oid of the discussion
    SELECT  @discussionId = oid
    FROM    ibs_Object
    WHERE   tVersionId IN (0x01010301, 0x01010A01)
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

    -- return the state value:
    RETURN  @retValue
GO
-- p_Article_01$BOCopy


/******************************************************************************
 * Change the state of an existing object. <BR>
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be changed.
 * @param   ai_userId           ID of the user who is changing the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @param   ai_state            The new state of the object.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Article_01$changeState'
GO

-- create the new procedure:
CREATE PROCEDURE p_Article_01$changeState
(
    -- input parameters:
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT,
    @state          STATE
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this
                                            -- operation
    @c_OBJECTNOTFOUND       INT,            -- the object was not found
    @c_NOOID                OBJECTID,       -- default value for no defined oid
    @c_ST_ACTIVE            INT,            -- active state
    @c_ST_CREATED           INT,            -- created state

    -- local variables:
    @l_retValue             INT,            -- return value of function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255)  -- error position description

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_OBJECTNOTFOUND       = 3,
    @c_NOOID                = 0x0000000000000000,
    @c_ST_ACTIVE            = 2,
    @c_ST_CREATED           = 4

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0

-- body:
    -- convertions: (OBJECTIDSTRING) - all input objectids must be converted
    DECLARE @oid            OBJECTID
    EXEC p_stringToByte @oid_s, @oid OUTPUT


    -- definitions:
    -- define return constants
    DECLARE @INSUFFICIENT_RIGHTS INT, @ALL_RIGHT INT, @OBJECTNOTFOUND INT
    -- define right constants
    DECLARE @RIGHT_UPDATE RIGHTS, @RIGHT_INSERT RIGHTS
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,   -- return values
            @OBJECTNOTFOUND = 3,
            @RIGHT_UPDATE = 8, @RIGHT_INSERT = 1        -- access rights
    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    DECLARE @rights RIGHTS              -- return value of rights proc.
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT, @rights = 0
    -- define used variables
    DECLARE @containerId OBJECTID,      -- id of container where the object
                                        -- resides
            @oldState STATE             -- actual state of the object
    SELECT  @containerId = 0x0000000000000000, @oldState = 0


    -- get the actual container id and state of object:
    SELECT  @containerId = containerId, @oldState = state
    FROM    ibs_Object
    WHERE   oid = @oid


    -- check if the object exists:
    IF (@@ROWCOUNT > 0)                 -- object exists?
    BEGIN
        -- get rights for this user
        EXEC @rights = p_Rights$checkRights
            @oid,                       -- given object to be accessed by user
            @containerId,               -- container of given object
            @userId,                    -- user id
            @op,                        -- required rights user must have to
                                        -- update object
            @rights OUTPUT              -- returned value

        -- check if the user has the necessary rights
        IF (@rights = @op)              -- the user has the rights?
        BEGIN
            -- check if the state transition from the actual state to the new
            -- state is allowed:
            -- not implemented yet

            BEGIN TRANSACTION

                -- set the new state for the object and all tabs:
                UPDATE  ibs_Object
                SET     state = @state
                WHERE   oid = @oid
                    OR  (   containerId = @oid
                        AND containerKind = 2
                        AND state <> @state
                        AND (   state = @c_ST_ACTIVE
                            OR  state = @c_ST_CREATED
                            )
                        )

                -- update the state of the entry tuple:
                UPDATE  m2_Article_01
                SET     state = @state
                WHERE   oid = @oid

            COMMIT TRANSACTION
        END -- if the user has the rights
        ELSE                            -- the user does not have the rights
        BEGIN
            -- set the return value with the error code:
            SELECT  @retValue = @INSUFFICIENT_RIGHTS
        END -- else the user does not have the rights
    END -- if object exists

    ELSE                                -- the object does not exist
    BEGIN
        -- set the return value with the error code:
        SELECT  @retValue = @OBJECTNOTFOUND
    END -- else the object does not exist

    -- return the state value
    RETURN  @retValue
GO
-- p_Article_01$changeState
