/******************************************************************************
 * All procedures regarding a references. <BR>
 *
 * @version     1.10.0001, 03.08.1999
 *
 * @author      ??? (??)  990311
 ******************************************************************************
 */

-- delete existing procedure:
EXEC p_dropProc N'p_Referenz_01$create'
GO

-- create the new procedure:
CREATE PROCEDURE p_Referenz_01$create
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
DECLARE
    -- constants:
    @c_REF_LINK             INT,            -- reference kind: link

    -- local variables:
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_rowCount             INT             -- row counter

    -- assign constants:
SELECT
    @c_REF_LINK             = 1

    -- initialize local variables:
SELECT
    @l_error = 0

-- body:
    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    DECLARE @ST_ACTIVE INT
    
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2, -- return values
            @OBJECTNOTFOUND = 3
    SELECT  @ST_ACTIVE = 2
    -- define return values
    DECLARE @retValue 	INT                   -- return value of this procedure
    DECLARE @rights 	INT                   -- rights
    DECLARE @returnValue   INT

    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT

    DECLARE @linkedtVersionId TVERSIONID
    DECLARE @targetVersionId TVERSIONID
    DECLARE @linkedObjectId OBJECTID
    DECLARE @containerId    OBJECTID
    DECLARE @copyContainerId    OBJECTID
    DECLARE @oid            OBJECTID
    DECLARE @dummyObjectId OBJECTID
    DECLARE @dummyObjectId_s OBJECTIDSTRING

    EXEC p_stringToByte @linkedObjectId_s,  @linkedObjectId OUTPUT
    EXEC p_stringToByte @containerId_s,     @containerId OUTPUT
    EXEC p_stringToByte @oid_s,             @oid OUTPUT

    BEGIN TRANSACTION

    SELECT  @linkedtVersionId = tVersionId
    FROM    ibs_object
    WHERE   oid = @linkedObjectId
        AND state = @ST_ACTIVE

    SELECT  @targetVersionId = tVersionId,
            @copyContainerId = containerId
    FROM    ibs_object
    WHERE   oid = @containerId
        AND state = @ST_ACTIVE

    IF (@targetVersionId = 0x010100b1) --Group
    BEGIN
        IF (@linkedtVersionId = 0x010100b1) -- Group
        BEGIN
            EXEC @retValue = p_Group_01$addGroup @userId, @containerId, 
                                                        @linkedObjectId, 0x0000000
        END
        IF (@linkedtVersionId = 0x010100a1) -- User
        BEGIN
            EXEC @retValue = p_Group_01$addUser @userId, @containerId, 
                                                        @linkedObjectId, 0x0000000
        END
        SELECT @oid_s = @linkedObjectId_s
    END
    ElSE
    IF (@targetVersionId = 0x010100a1) -- User
    BEGIN

		SELECT  @containerId AS users, @linkedObjectId AS person

		SELECT  @dummyObjectId = linkedObjectId from ibs_Object
        WHERE   containerId = @containerId
        AND     tVersionId = 0x01010031

        DELETE
        FROM    ibs_Object 
        WHERE   containerId = @containerId
        AND     tVersionId = 0x01010031

        DELETE
        FROM    ibs_Object 
        WHERE   linkedObjectId = @containerId
        AND     containerId IN 
                    (SELECT o2.oid 
                    FROM    ibs_Object o2
                    WHERE   o2.containerId = @dummyObjectId
                        AND tVersionId = 0x01015e01
                        AND state = @ST_ACTIVE)
        AND     tVersionId = 0x01010031

        EXEC @retValue = p_Object$performCreate @userId, @op, @tVersionId, @name, @containerId_s,
             @containerKind, @isLink, @linkedObjectId_s, @description, 
             @oid_s OUTPUT

        UPDATE ibs_User
        SET fullname = 
                    (SELECT name 
                     FROM   ibs_Object 
                     WHERE  oid = @linkedObjectId
                        AND state = @ST_ACTIVE)
        WHERE oid = @containerId

        SELECT  @dummyObjectId = oid
        FROM    ibs_Object 
        WHERE   containerId = @linkedObjectId
            AND tVersionId = 0x01015e01     -- tVersionId of the PersonUserContainer_01
            AND state = @ST_ACTIVE
        
        EXEC p_byteToString @dummyObjectId, @dummyObjectId_s OUTPUT

        EXEC @retValue = p_Object$performCreate @userId, @op, @tVersionId, @name, @dummyObjectId_s,
                @containerKind, @isLink, @containerId_s, @description, 
                @oid_s OUTPUT

    END        
    ELSE
    IF (@targetVersionId = 0x01015201) -- Membership
        BEGIN
            EXEC @retValue = p_Group_01$addUser @userId, @linkedObjectId, 
                    @copyContainerId, 0x0000000
            SELECT @oid_s = @linkedObjectId_s
        END
    ELSE
    BEGIN    
        -- create a referenceobject
        EXEC @retValue = p_Object$performCreate @userId, @op, @tVersionId, @name, @containerId_s,
                @containerKind, @isLink, @linkedObjectId_s, @description, 
                @oid_s OUTPUT

        -- check if there occurred an error:
        IF (@retValue = @ALL_RIGHT)     -- everything o.k.?
        BEGIN
            EXEC    p_stringToByte @oid_s, @oid OUTPUT
            -- store the reference:
            EXEC    p_Reference$create @oid, null, @linkedObjectId, @c_REF_LINK
        END -- if everything o.k.
    END
    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO
-- p_Referenz_01$create


/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @ai_oid_s               ID of the object to be changed.
 * @param   @ai_userId              ID of the user who is changing the object.
 * @param   @ai_op                  Operation to be performed (used for rights
 *                                  check).
 * @param   @ai_name                Name of the object.
 * @param   @ai_validUntil          Date until which the object is valid.
 * @param   @ai_description         Description of the object.
 * @param   @ai_showInNews          Should the currrent object displayed in the news.
 * @param   @ai_linkedObjectId_s    The oid of the linked object.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Referenz_01$change'
GO

-- create the new procedure:
CREATE PROCEDURE p_Referenz_01$change
(
    -- input parameters:
    @ai_oid_s               OBJECTIDSTRING,
    @ai_userId              USERID,
    @ai_op                  INT,
    @ai_name                NAME,
    @ai_validUntil          DATETIME,
    @ai_description         DESCRIPTION,
    @ai_showInNews          BOOL,
    @ai_linkedObjectId_s    OBJECTIDSTRING
)
AS
DECLARE
    -- constants:
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this
                                            -- operation
    @c_OBJECTNOTFOUND       INT,            -- the object was not found
    @c_REF_LINK             INT,            -- reference kind: link

    -- local variables:
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_rowCount             INT,            -- row counter
    @l_retValue             INT,            -- return value of function
    @l_oid                  OBJECTID,       -- oid of the actual object
    @l_linkedObjectId       OBJECTID,       -- oid of the object which shall
                                            -- be referenced
    @l_name                 NAME,           -- the name of the referenced object
    @l_description          DESCRIPTION,    -- the description of the
                                            -- referenced object
    @l_typeName             NAME,           -- the name of the type of the
                                            -- referenced object
    @l_icon                 NAME,           -- the name of the icon of the
                                            -- referenced object
    @l_flags                INT,            -- the flags of the referenced object
    @l_rKey                 INT             -- the rKey of the referenced object

    -- assign constants:
SELECT
    @c_ALL_RIGHT            = 1,
    @c_INSUFFICIENT_RIGHTS  = 2,
    @c_OBJECTNOTFOUND       = 3,
    @c_REF_LINK             = 1

    -- initialize local variables:
SELECT
    @l_error = 0,
    @l_retValue = @c_OBJECTNOTFOUND

-- body:
    -- convert the object id and linked object id
    EXEC p_stringToByte @ai_oid_s, @l_oid OUTPUT
    EXEC p_stringToByte @ai_linkedObjectId_s, @l_linkedObjectId OUTPUT

    BEGIN TRANSACTION
        -- get data of linked object into link:
        -- If the linked object is itself a link the link shall point to the
        -- original linked object.
        SELECT  @l_name = name, @l_typeName = typeName,
                @l_description = description, @l_flags = flags,
                @l_icon = icon, @l_rKey = rKey
        FROM    ibs_Object
        WHERE   oid = @l_linkedObjectId

        IF (@@ROWCOUNT = 1)
        BEGIN
            -- perform the change of the object:
            EXEC @l_retValue = p_Object$performChange @ai_oid_s, @ai_userId, @ai_op,
                    @l_name, @ai_validUntil, @l_description, @ai_showInNews

            -- if the change operation was successful
            -- change the link specific attributes to
            IF (@l_retValue = @c_ALL_RIGHT)
            BEGIN
                -- perform the changes on ibs_Object:
                UPDATE  ibs_Object
                SET     name = @l_name,
                        typeName = @l_typeName,
                        description = @l_description,
                        flags = @l_flags,
                        icon = @l_icon,
                        rKey = @l_rKey,
                        linkedObjectId = @l_linkedObjectId
                WHERE   oid = @l_oid

                -- store the reference:
                EXEC    p_Reference$create @l_oid, null, @l_linkedObjectId, @c_REF_LINK
            END
        END
    COMMIT TRANSACTION

    -- return the state value
    RETURN  @l_retValue
GO
-- p_Referenz_01$change
