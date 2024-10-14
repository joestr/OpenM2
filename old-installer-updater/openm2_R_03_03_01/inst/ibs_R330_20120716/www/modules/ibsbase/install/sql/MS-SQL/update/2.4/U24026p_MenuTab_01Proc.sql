/******************************************************************************
 * All stored procedures regarding the MenuTab_01 Object. <BR>
 *
 * @version     $Id: U24026p_MenuTab_01Proc.sql,v 1.1 2006/04/11 15:52:20 klreimue Exp $
 *
 * @author      Andreas Jansa (AJ)  011105
 ******************************************************************************
 */


/******************************************************************************
 * Creates a new MenuTab_01 Object (incl. rights check). <BR>
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

-- delete existing procedure:
EXEC p_dropProc 'p_MenuTab_01$create'
GO

-- create the new procedure:
CREATE PROCEDURE p_MenuTab_01$create
(
    -- common input parameters:
    @ai_userId              INT,
    @ai_op                  INT,
    @ai_tVersionId          INT,
    @ai_name                NAME,
    @ai_containerId_s       OBJECTIDSTRING,
    @ai_containerKind       INT,
    @ai_isLink              INT,
    @ai_linkedObjectId_s    OBJECTIDSTRING,
    @ai_description         DESCRIPTION,
    -- common output parameters:
    @ao_oid_s               OBJECTIDSTRING OUTPUT
)
AS
DECLARE
    -- definitions:
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_NOOID                OBJECTID,       -- default value for no defined oid

    -- local valriables
    @l_retValue              INT,           -- return value of function
    @l_oid                   OBJECTID       -- to save oid of created menutab

    -- initialize
    SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_NOOID                = 0x0000000000000000

    -- initialize local variables and return values:
    SELECT
    @l_retValue             = @c_ALL_RIGHT,
    @l_oid                  = @c_NOOID

    -- body
    BEGIN TRANSACTION

    -- create base object:
    EXEC @l_retValue = p_Object$performCreate @ai_userId, @ai_op, @ai_tVersionId,
                        @ai_name, @ai_containerId_s, @ai_containerKind,
                        @ai_isLink, @ai_linkedObjectId_s, @ai_description,
                        @ao_oid_s OUTPUT, @l_oid OUTPUT


    IF (@l_retValue = @c_ALL_RIGHT)       -- object created successfully:1
    BEGIN
        -- create object type specific data:
        INSERT INTO ibs_MenuTab_01 (oid, objectOid, description,
            isPrivate, priorityKey, domainId, classFront, classBack, fileName,
            levelStep, levelStepMax)
        VALUES (@l_oid, @c_NOOID, ' ',
            0,0,0, 'groupFront.gif','groupBack.gif', 'welcome.htm', 0, 0)
    END -- if object created successfully

    -- commit
    COMMIT TRANSACTION

    -- return the state value:
    -- return the state value:
    RETURN  @l_retValue
GO
-- p_MenuTab_01$create

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
 * @param   @description        Description of the object.
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
EXEC p_dropProc 'p_MenuTab_01$change'
GO

-- delete existing procedure:
CREATE PROCEDURE p_MenuTab_01$change
(
    -- common input parameters:
    @ai_oid_s               OBJECTIDSTRING,
    @ai_userId              INT,
    @ai_op                  INT,
    @ai_name                NAME,
    @ai_validUntil          DATETIME,
    @ai_description         DESCRIPTION,
    @ai_showInNews          BOOL,
    -- typespecific input parameters
    @ai_objectoid_s         OBJECTIDSTRING,
    @ai_filename            DESCRIPTION,
    @ai_tabpos              INT,
    @ai_front               DESCRIPTION,
    @ai_back                DESCRIPTION,
    @ai_levelStep           INT,
    @ai_levelStepMax        INT
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_NOOID                OBJECTID,       -- default value for no defined oid
    @c_EMPTYPOSNOPATH       POSNOPATH_VC,   -- default value for empty pos no
                                            -- path

    -- local variables:
    @l_retValue             ID,             -- return value of this procedure
    @l_posNoPath            POSNOPATH_VC,   -- the pos no path of the object
    @l_isPrivate            INT,            -- a flag which is 1 if object is
                                            -- a workspace
    @l_domainId             DOMAINID,       -- id of the domain where the object
                                            -- exists
    @l_showInMenu           BOOL,           -- the show in menu flag of the
                                            -- object
    @l_count                INT,            -- counter
    @l_oldObjectId          OBJECTID,       -- assigned oid before change
    @l_objectoid            OBJECTID,       -- oid of new assigned object
    @l_objectname           NAME,           -- name of new assigned object
    @l_oid                  OBJECTID        -- to save oid of created menutab

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_NOOID                = 0x0000000000000000,
    @c_EMPTYPOSNOPATH       = '0000'

    -- initialize local variables and return values:
SELECT
    @l_retValue             = @c_ALL_RIGHT,
    @l_domainId             = @c_NOOID,
    @l_count                = 0,
    @l_isPrivate            = 0,
    @l_posNoPath            = @c_EMPTYPOSNOPATH,
    @l_showInMenu           = 0

-- body

    -- convert oidString to oid
    EXEC p_stringToByte @ai_oid_s, @l_oid  OUTPUT
    EXEC p_stringToByte @ai_objectoid_s, @l_objectoid OUTPUT

    BEGIN TRANSACTION

    -- perform the change of the object:
    EXEC @l_retValue = p_Object$performChange @ai_oid_s, @ai_userId, @ai_op,
            @ai_name, @ai_validUntil, @ai_description, @ai_showInNews,
            @l_oid OUTPUT

    IF (@l_retValue = @c_ALL_RIGHT)       -- object created successfully:1
    BEGIN
        -- get the object data:
        SELECT @l_objectname = name, @l_posNoPath = posNoPath
        FROM   ibs_Object
        WHERE  oid = @l_objectoid

        -- get the domain id:
        SELECT  @l_domainId = d.id
        FROM    ibs_Domain_01 d, ibs_Object o
        WHERE   @l_posNoPath LIKE o.posNoPath + '%'
            AND d.oid = o.oid

        -- check if given object is a workspaceContainer
        SELECT  @l_count = COUNT (*)
        FROM ibs_domain_01
        WHERE   workspacesOid = @l_objectoid

        IF (@l_count > 0)               -- it is the workspacecontainer?
        BEGIN
            SELECT @l_isPrivate = 1
        END -- if it is a private menu tab

        -- get oid of object which was assigned to menutab before change
        SELECT  @l_oldObjectId = objectoid
        FROM    ibs_MenuTab_01
        WHERE   oid = @l_oid

        -- don't show the object menu again:
        UPDATE  ibs_Object
        SET     showInMenu = 1
        WHERE   oid = @l_oldObjectId

        -- ensure that ai_front and ai_back contain '.':
        SET @ai_front = @ai_front + '.'
        SET @ai_back = @ai_back + '.'

        -- set new data in menutab:
        UPDATE  ibs_MenuTab_01
        SET     objectoid = @l_objectoid,
                description = @l_objectname,
                prioritykey = @ai_tabpos,
                filename = @ai_filename,
                classfront = substring (@ai_front, 1, charindex ('.', @ai_front)-1),
                classback = substring (@ai_back, 1, charindex ('.', @ai_back)-1),
                isprivate = @l_isPrivate,
                domainid = @l_domainId,
                levelStep = @ai_levelStep,
                levelStepMax = @ai_levelStepMax
        WHERE   oid = @l_oid


         -- don't show the object in the menu because it's a menutab:
         UPDATE  ibs_Object
         SET     showInMenu = 0
         WHERE   oid = @l_objectoid

    END -- if object created successfully

    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_MenuTab_01$change


/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 *
 */

-- delete existing procedure:
EXEC p_dropProc 'p_MenuTab_01$retrieve'
GO

-- delete existing procedure:
CREATE PROCEDURE p_MenuTab_01$retrieve
(
    -- common input parameters:
    @ai_oid_s                   OBJECTIDSTRING,
    @ai_userId                  INT,
    @ai_op                      INT,
    -- common output parameters:
    @ao_state                   INT         OUTPUT,
    @ao_tVersionId              INT         OUTPUT,
    @ao_typeName                NAME        OUTPUT,
    @ao_name                    NAME        OUTPUT,
    @ao_containerId             OBJECTID    OUTPUT,
    @ao_containerName           NAME        OUTPUT,
    @ao_containerKind           INT         OUTPUT,
    @ao_isLink                  BOOL        OUTPUT,
    @ao_linkedObjectId          OBJECTID    OUTPUT,
    @ao_owner                   INT         OUTPUT,
    @ao_ownerName               NAME        OUTPUT,
    @ao_creationDate            DATETIME    OUTPUT,
    @ao_creator                 INT         OUTPUT,
    @ao_creatorName             NAME        OUTPUT,
    @ao_lastChanged             DATETIME    OUTPUT,
    @ao_changer                 INT         OUTPUT,
    @ao_changerName             NAME        OUTPUT,
    @ao_validUntil              DATETIME    OUTPUT,
    @ao_description             DESCRIPTION OUTPUT,
    @ao_showInNews              BOOL        OUTPUT,
    @ao_checkedOut              BOOL        OUTPUT,
    @ao_checkOutDate            DATETIME    OUTPUT,
    @ao_checkOutUser            INT         OUTPUT,
    @ao_checkOutUserOid         OBJECTID    OUTPUT,
    @ao_checkOutUserName        NAME        OUTPUT,
    -- type-specific output attributes:
    @ao_objectoid               OBJECTID    OUTPUT,
    @ao_objectname              NAME        OUTPUT,
    @ao_tabpos                  INT         OUTPUT,
    @ao_front                   DESCRIPTION OUTPUT,
    @ao_back                    DESCRIPTION OUTPUT,
    @ao_filename                DESCRIPTION OUTPUT,
    @ao_levelStep               INT         OUTPUT,
    @ao_levelStepMax            INT         OUTPUT
)
AS
DECLARE
    -- definitions:
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_NOOID                OBJECTID,       -- default value for no defined oid

    -- local valriables
    @l_retValue              INT,           -- return value of function
    @l_oid                   OBJECTID       -- to save oid of created menutab

    -- initialize
    SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_NOOID                = 0x0000000000000000

    -- initialize local variables and return values:
    SELECT
    @l_retValue             = @c_ALL_RIGHT,
    @l_oid                  = @c_NOOID

    -- body
    BEGIN TRANSACTION

    -- retrieve the base object data:
    EXEC @l_retValue = p_Object$performRetrieve @ai_oid_s, @ai_userId, @ai_op,
            @ao_state OUTPUT, @ao_tVersionId OUTPUT, @ao_typeName OUTPUT,
            @ao_name OUTPUT, @ao_containerId OUTPUT, @ao_containerName OUTPUT,
            @ao_containerKind OUTPUT, @ao_isLink OUTPUT,
            @ao_linkedObjectId OUTPUT, @ao_owner OUTPUT, @ao_ownerName OUTPUT,
            @ao_creationDate OUTPUT, @ao_creator OUTPUT, @ao_creatorName OUTPUT,
            @ao_lastChanged OUTPUT, @ao_changer OUTPUT, @ao_changerName OUTPUT,
            @ao_validUntil OUTPUT, @ao_description OUTPUT,
            @ao_showInNews OUTPUT, @ao_checkedOut OUTPUT,
            @ao_checkOutDate OUTPUT, @ao_checkOutUser OUTPUT,
            @ao_checkOutUserOid OUTPUT, @ao_checkOutUserName OUTPUT,
            @l_oid OUTPUT

    IF (@l_retValue = @c_ALL_RIGHT)       -- object created successfully:1
    BEGIN

        SELECT  @ao_objectoid = objectoid,
                @ao_tabpos = prioritykey,
                @ao_front = classfront,
                @ao_back = classback,
                @ao_filename = filename,
                @ao_levelStep = levelStep,
                @ao_levelStepMax = levelStepMax
        FROM    ibs_MenuTab_01
        WHERE   oid = @l_oid

        SELECT  @ao_objectname = name
        FROM    ibs_object
        WHERE   oid = @ao_objectoid

    END -- if object created sucessfully

    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_MenuTab_01$retrieve;


/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 *
 */

-- delete existing procedure:
EXEC p_dropProc 'p_MenuTab_01$delete'
GO

-- delete existing procedure:
CREATE PROCEDURE p_MenuTab_01$delete
(
    @ai_oid_s               OBJECTIDSTRING,
    @ai_userId              INT,
    @ai_op                  INT
)
AS
DECLARE
    -- definitions:
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_NOOID                OBJECTID,       -- default value for no defined oid

    -- local valriables
    @l_retValue              INT,           -- return value of function
    @l_oid                   OBJECTID       -- to save oid of created menutab

    -- initialize
    SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_NOOID                = 0x0000000000000000

    -- initialize local variables and return values:
    SELECT
    @l_retValue             = @c_ALL_RIGHT,
    @l_oid                  = @c_NOOID

    -- body
    EXEC p_stringToByte @ai_oid_s, @l_oid OUTPUT

    BEGIN TRANSACTION

    -- show the object in the menu because it's no longer a menutab
    UPDATE  ibs_Object
    SET     showInMenu = 1
    WHERE   oid = ( SELECT  objectoid
                    FROM    ibs_MenuTab_01
                    WHERE   oid = @l_oid)

    -- important for creating the right application path in m2
    UPDATE  ibs_MenuTab_01
    SET     objectoid = @c_NOOID
    WHERE   oid = @l_oid

    EXEC @l_retValue = p_Object$performDelete @ai_oid_s, @ai_userId,
                                             @ai_op, @l_oid OUTPUT

    COMMIT TRANSACTION

    RETURN @l_retValue
GO
-- p_MenuTab_01$delete




