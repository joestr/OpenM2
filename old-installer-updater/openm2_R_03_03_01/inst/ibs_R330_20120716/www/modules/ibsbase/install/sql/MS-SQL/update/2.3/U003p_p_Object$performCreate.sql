/******************************************************************************
 * Creates a new object (incl. rights check). <BR>
 * This procedure does not make a transaction, so that it can be used from
 * within a transaction of another procedure. <BR>
 * This procedure is needed for creating a business object which contains
 * several other objects.
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
 * @param   @oid_s              String representation of OID of the newly 
 *                              created object.
 * [@param   @oid]              Oid of the newly created object.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Object$performCreate'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$performCreate
(
    -- input parameters:
    @ai_userId         USERID,
    @ai_op             INT,
    @ai_tVersionId     TVERSIONID,
    @ai_name           NAME,
    @ai_containerId_s  OBJECTIDSTRING,
    @ai_containerKind  INT,
    @ai_isLink         BOOL,
    @ai_linkedObjectId_s OBJECTIDSTRING,
    @ai_description    DESCRIPTION,
    -- output parameters:
    @ao_oid_s          OBJECTIDSTRING OUTPUT,
    @ao_oid            OBJECTID = 0x0000000000000000 OUTPUT
)
AS
-- declarations

    -- define constants
    DECLARE @c_NOOID OBJECTID,
            @c_EMPTYPOSNOPATH VARCHAR(4),
            @c_NOT_OK INT, 
            @c_ALL_RIGHT INT, 
            @c_INSUFFICIENT_RIGHTS INT, 
            @c_OBJECTNOTFOUND INT,
            @c_RIGHT_UPDATE RIGHTS,     -- access permission
            @c_RIGHT_INSERT RIGHTS,     -- access permission
            @c_CHECKEDOUT INT           -- 5th bit of attribute 'flags'
            
    SELECT  @c_NOOID = 0x0000000000000000,
            @c_EMPTYPOSNOPATH = '0000',
            @c_NOT_OK = 0, 
            @c_ALL_RIGHT = 1, 
            @c_INSUFFICIENT_RIGHTS = 2, 
            @c_OBJECTNOTFOUND = 3,
            @c_RIGHT_UPDATE = 8, 
            @c_RIGHT_INSERT = 1,
            @c_CHECKEDOUT = 16,
            @ao_oid_s = '0x0000000000000000',
            @ao_oid   = 0x0000000000000000

    -- define local variables
    DECLARE @l_retValue INT,        -- return value of this procedure
            @l_rights RIGHTS,       -- return value of rights proc.
            @l_co_userId USERID,
            @l_check INT,               -- check out value
            @l_fullName NAME,
            @l_icon NAME,
            @l_name NAME,
            @l_description DESCRIPTION,
            @l_containerId OBJECTID,
            @l_linkedObjectId OBJECTID
    SELECT  @l_retValue = @c_NOT_OK, 
            @l_rights = 0,
            @l_co_userId = 0,
            @l_check = 0,
            @l_fullName = '',
            @l_icon = 'icon.gif',
            @l_name = @ai_name,
            @l_description = @ai_description,
            @l_containerId = @c_NOOID,
            @l_linkedObjectId = @c_NOOID
    --
    --  TRIGGER variables: used by trigger reimplementation
    DECLARE
        @l_id                   ID,             -- the id of the inserted object
        @l_origId               ID,             -- originally set id
        @l_typeName             NAME,           -- the name of the type
        @l_isContainer          BOOL,           -- is the object a container?
        @l_showInMenu           BOOL,           -- shall the object be shown in
                                                -- the menu?
        @l_showInNews           INT,           -- shall the object be shown in
                                                -- the news container?
        @l_oLevel               INT,            -- level of object within
                                                -- hierarchy
        @l_posNo                POSNO,          -- position of object within
                                                -- container
        @l_posNoHex             VARCHAR (4),    -- hex representation of posNo
        @l_posNoPath            POSNOPATH_VC,   -- the posNoPath
        @l_flags                INT,            -- the flag which are set
        @l_validUntil           DATETIME,       -- date until which the object
                                                -- is valid
        @l_rKey                 INT             -- rights key
    SELECT
        @l_id = 0,
        @l_origId = 0,
        @l_typeName = 'UNKNOWN',
        @l_showInMenu = 0,
        @l_showInNews = 0,   
        @l_oLevel = 1,                          -- lowest possible object level
        @l_posNo = 0,
        @l_posNoHex = '0000',
        @l_posNoPath = @c_EMPTYPOSNOPATH,
        @l_flags = 0,
        @l_validUntil = getDate(),
        @l_rKey = 0
    --  TRIGGER variables (END)                                          
    --
   
-- convertions: 
    EXEC p_stringToByte @ai_containerId_s, @l_containerId OUTPUT
    EXEC p_stringToByte @ai_linkedObjectId_s, @l_linkedObjectId OUTPUT

    -- retrieve check-out-info for new objects container?
    SELECT @l_co_userId = co.userId, @l_check = o.flags & @c_CHECKEDOUT
    FROM ibs_Object o JOIN ibs_Checkout_01 co ON o.oid = co.oid
    WHERE o.oid = @l_containerId


    -- is the object checked out?    
    IF ((@l_check = @c_CHECKEDOUT) AND (@l_co_userId <> @ai_userId))
        SELECT @l_retValue = @c_INSUFFICIENT_RIGHTS
    ELSE
    BEGIN
        -- container is not checked out,
        -- now check if user has permission to create object
        EXEC @l_rights = p_Rights$checkRights
            @ao_oid,                        -- given object to be accessed by user
            @l_containerId,                 -- container of given object
            @ai_userId,                     -- user_id
            @ai_op,                         -- required rights user must have to
                                            -- insert/update object
            @l_rights OUTPUT                -- returned value

        -- check if the user has the necessary rights
        IF (@l_rights = @ai_op)             -- the user has the rights?
        BEGIN
            -- add the new tuple to the ibs_Object table:
---------            
--
-- START get and calculate base-data
--       (old trigger functionality!)
--
            --
            -- 1. compute id an oid for new object
            -- 
            SELECT  @l_id = COALESCE (MAX (id) + 1, 1)
            FROM    ibs_Object
           
            EXEC p_createOid @ai_tVersionId, @l_id, @ao_oid OUTPUT
            EXEC p_byteToString @ao_oid, @ao_oid_s OUTPUT

            --
            -- 2. compute olevel, posno and posnopath
            --
            -- derive position number from other objects within container:
            -- The posNo is one more than the actual highest posNo within the 
            -- container or 1 if there is no object within the container yet.
            SELECT  @l_posNo = COALESCE (MAX (posNo) + 1, 1)
            FROM    ibs_Object
            WHERE   containerId = @l_containerId
            -- convert the position number into hex representation:
            EXEC p_IntToHexString @l_posNo, @l_posNoHex OUTPUT

            -- derive position level and rkey from container:
            -- The level of an object is the level of the container plus 1
            -- or 0, if there is no container.
            SELECT  @l_oLevel = COALESCE (oLevel + 1, 1),
                    @l_rKey = rKey,
                    @l_validUntil = validUntil
            FROM    ibs_Object
            WHERE   oid = @l_containerId
            
            -- check if there were some data found:
            IF (@@ROWCOUNT = 0)             -- no data found?
            BEGIN
                -- no container found for given object; 
                -- must be root-object
                SELECT @l_oLevel = 1
                SELECT @l_rKey = 0
            END -- if no data found

            -- calculate new position path
            IF (@l_containerId <> @c_NOOID)     -- object is within a container?
            BEGIN
                -- compute the posNoPath as posNoPath of container concatenated by
                -- the posNo of this object:
                SELECT  DISTINCT @l_posNoPath = posNoPath + @l_posNoHex
                FROM    ibs_Object
                WHERE   oid = @l_containerId
        
                -- check if there were some data found:
                IF (@@ROWCOUNT = 0)             -- no data found?
                BEGIN
                    -- compute the posNoPath as posNo of this object:
                    SELECT  @l_posNoPath = @l_posNoHex
                END -- if no data found
            END -- if object is within a container
            ELSE                                -- object is not within a container
                                                -- i.e. it is on top level
            BEGIN
                -- compute the posNoPath as posNo of this object:
                SELECT  @l_posNoPath = @l_posNoHex
            END -- else object is not within a container


            --
            -- 3. get type-info: type name, icon and containerId, showInMenus
            --                   showInNews       
            --
            SELECT  @l_typeName = t.name, @l_isContainer = t.isContainer,
                    @l_showInMenu = t.showInMenu, @l_showInNews = t.showInNews * 4,
                    @l_icon = t.icon
            FROM    ibs_Type t, ibs_TVersion tv
            WHERE   tv.id = @ai_tVersionId
                AND t.id = tv.typeId

            --
            -- 4. distinguish between reference/no-reference objects
            -- 
            IF (@ai_isLink = 1)                  -- link object?
            BEGIN
                --
                -- IMPORTANT: rights-key will be set in here
                --
                -- get data of linked object into link:
                -- If the linked object is itself a link the link shall point to the
                -- original linked object.
                SELECT  @l_name = name, @l_typeName = typeName, 
                        @l_description = description, @l_flags = flags, 
                        @l_icon = icon, @l_rKey = rKey
                FROM    ibs_Object 
                WHERE   oid = @l_linkedObjectId
            END
            ELSE
            BEGIN
                IF (@l_name = '' OR @l_name = ' ' OR @l_name IS NULL)
                    SELECT @l_name = @l_typeName
            END

            --
            -- 5. calculate new flags value: add showInNews
            --
            SELECT @l_flags = (@l_flags & 0x7FFFFFFB) + @l_showInNews
--
-- END get and calculate base-data
--
---------     
            --
            -- last but not least: insert new information
            --
            INSERT INTO ibs_Object 
                   (id, oid, /*state,*/ tVersionId, typeName, isContainer,
                    name, containerId, containerKind, isLink, 
                    linkedObjectId, showInMenu, flags, owner, oLevel,
                    posNo, posNoPath, creationDate, creator, lastChanged,
                    changer, validUntil, description, icon, 
                    /*processState,*/ rKey)
            VALUES (@l_id, @ao_oid, /*???,*/ @ai_tVersionId, @l_typeName, @l_isContainer,
                    @l_name, @l_containerId, @ai_containerKind, @ai_isLink,
                    @l_linkedObjectId, @l_showInMenu, @l_flags, @ai_userid, @l_oLevel,
                    @l_posNo, @l_posNoPath, getDate(), @ai_userId, getDate(),                            
                    @ai_userId, @l_validUntil, @ai_description, @l_icon, 
                    /*???,*/ @l_rKey)
                    
            --
            -- create tabs (if necessary)
            --
            IF (@ai_containerKind <> 2)     -- object is independent?
            BEGIN
                -- create tabs for the object:
                EXEC    p_Object$createTabs @ai_userId, @ai_op, 
                            @ao_oid, @ai_tVersionId
                --
                -- insert protocol entry
                --
                -- gather missing information for protocol-entry
                SELECT  @l_fullName = fullname
                FROM    ibs_user
                WHERE   id = @ai_userId
    
                -- add the new tuple to the ibs_Object table:
                INSERT INTO ibs_Protocol_01
                       (fullName, userId, oid, objectName, icon, tVersionId,  
                        containerId, containerKind, owner, action, actionDate)
                VALUES (@l_fullName, @ai_userId, @ao_oid, @l_name, @l_icon, 
                        @ai_tVersionId, @l_containerId, @ai_containerKind, 
                        @ai_userId, @ai_op, getDate ())
            END -- if object is independent                        

            -- done!
            SELECT  @l_retValue = @c_ALL_RIGHT
                
       END -- if the user has the rights
       ELSE                                -- the user does not have the rights
             SELECT  @l_retValue = @c_INSUFFICIENT_RIGHTS
   END -- else the object is not cheked out or the user is the one who cheked the object out

   -- return the state value
   RETURN  @l_retValue
GO
-- p_Object$performCreate
