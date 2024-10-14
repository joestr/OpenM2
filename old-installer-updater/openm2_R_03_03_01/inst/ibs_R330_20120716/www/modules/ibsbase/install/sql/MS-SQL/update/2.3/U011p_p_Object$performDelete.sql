/******************************************************************************
 * Deletes an object and all its values (incl. rights check). <BR>
 * This procedure also deletes all links showing to this object. <BR>
 * This procedure does not make a transaction, so that it can be used from
 * within a transaction of another procedure. <BR>
 * This procedure is needed for deleting a business object which contains
 * several other objects.
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be deleted.
 * @param   @userId             ID of the user who is deleting the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 *
 * @output parameters:
 * [@param   @oid]              Oid of the deleted object.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Object$performDelete'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$performDelete
(
    -- input parameters:
    @ai_oid_s               OBJECTIDSTRING,
    @ai_userId              USERID,
    @ai_op                  INT,
    -- output parameters:
    @ao_oid                 OBJECTID = 0x0000000000000000 OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this
                                            -- operation
    @c_OBJECTNOTFOUND       INT,            -- the object was not found                                        
    @c_RIGHT_DELETE         INT,            -- right for deleting
    @c_CHECKEDOUT           INT,            -- flag for object being
                                            -- checked out
    @c_NOTDELETABLE         INT,            -- flag for object not being
                                            -- deletable

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_error                INT,            -- the actual error code
    @l_ePos                 VARCHAR (255),  -- error position description
    @l_rowCount             INT,            -- row counter
    @l_checkedOut           INT,            -- is the object checked out?
    @l_co_userId            USERID,         -- id of check out user
    @l_notDeletable         INT,            -- is the object deletable?
    @l_adminId              USERID,         -- id of administrator
    @l_rights               INT,            -- the rights the user has
    @l_containerId          OBJECTID,       -- the object's container
    @l_posNoPath            POSNOPATH_VC,   -- the object's posNoPath
    @l_oLevel               INT             -- the oLevel of the object

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_OBJECTNOTFOUND       = 3,
    @c_RIGHT_DELETE         = 16,
    @c_CHECKEDOUT           = 16,
    @c_NOTDELETABLE         = 128

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0,
    @l_rowCount = 0,
    @l_rights = 0

-- body:
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    EXEC    p_stringToByte @ai_oid_s, @ao_oid OUTPUT

    -- is the object checked out or not deletable?
    SELECT  @l_co_userId = co.userId, @l_checkedOut = o.flags & @c_CHECKEDOUT,
            @l_notDeletable = o.flags & @c_NOTDELETABLE
    FROM    ibs_Object o JOIN ibs_Checkout_01 co ON o.oid = co.oid
    WHERE   o.oid = @ao_oid

    -- get the id of the administrator:
    SELECT  @l_adminId = d.adminId
    FROM    ibs_Domain_01 d, ibs_User u
    WHERE   u.id = @ai_userId
        AND u.domainId = d.id

    -- check if the user may delete the object:
    IF ((@l_checkedOut = @c_CHECKEDOUT) AND (@l_co_userId <> @ai_userId)) OR
       (@l_notDeletable = @c_NOTDELETABLE AND @ai_userId <> @l_adminId)
    BEGIN
        SELECT @l_retValue = @c_INSUFFICIENT_RIGHTS
    END -- if
    ELSE
    BEGIN
        -- get container id, posNoPath and level within tree of object
        SELECT  @l_containerId = containerId, @l_posNoPath = posNoPath,
                @l_oLevel = oLevel
        FROM    ibs_Object
        WHERE   oid = @ao_oid

        -- check if the object exists:
        IF (@@ROWCOUNT > 0)             -- object exists?
        BEGIN
    
-- HP 990830    
-- PROBLEM: only one object right checked here - no subsequent objects!!!!
    
            -- get rights for this user
            EXEC p_Rights$checkRights
                 @ao_oid,               -- given object to be accessed by user
                 @l_containerId,        -- container of given object
                 @ai_userId,            -- user_id
                 @ai_op,                -- required rights user must have to
                                        -- delete object (operation to be perf.)
                 @l_rights OUTPUT       -- returned value

            -- check if the user has the necessary rights
            IF (@l_rights = @ai_op)     -- the user has the rights?
            BEGIN

-- HP 990830
-- PROBLEM: protocol only for object - no subsequent objects!!!!

                DECLARE @l_fullName       NAME
                DECLARE @l_tVersionId     TVERSIONID
                DECLARE @l_containerKind  INT
                DECLARE @l_name           NAME
                DECLARE @l_owner          USERID
                DECLARE @l_icon           NAME
  
                -- read out the  fullName of the User
                SELECT  @l_fullName = u.fullname, @l_icon = icon,
                        @l_tVersionId = o.tVersionId,
                        @l_name = o.name, @l_owner = o.owner,
                        @l_containerKind = containerKind
                FROM    ibs_user u, ibs_Object o  
                WHERE   u.id = @ai_userId
                    AND o.oid = @ao_oid

                -- add the new tuple to the ibs_Object table:
                INSERT INTO ibs_Protocol_01
                       ( fullName, userId, oid, objectName, icon, tVersionId,  
                         containerId, containerKind, owner, action, actionDate)
                VALUES (@l_fullName, @ai_userId, @ao_oid, @l_name, @l_icon,
                        @l_tVersionId, @l_containerId, @l_containerKind,
                        @ai_userId, @ai_op, getDate ())

                -- start deletion of object, subsequent objects AND references to
                -- deleted objects
            
                -- mark object and subsequent objects as 'deleted' via posnopath
                UPDATE  ibs_Object 
                SET     state = 1,
                        changer = @ai_userId,
                        lastChanged = getDate ()
                WHERE   posNoPath LIKE @l_posNoPath + '%'
            
                -- mark references to the object as 'deleted'
                -- ATTENTION: If you change this part you must change 
                --            p_Object$deleteAllRefs as well!!
                UPDATE  ibs_Object 
                SET     state = 1,
                        changer = @ai_userId,
                        lastChanged = getDate ()
                WHERE   linkedObjectId IN
                        (
                            SELECT  oid
                            FROM    ibs_Object
                            WHERE   posNoPath LIKE @l_posNoPath + '%'
                                AND state = 1
                        )
                
                -- the external keys of the deleted objects have to be archived
                EXEC p_KeyMapper$archiveExtKeys @l_posNoPath

            END -- if the user has the rights
            ELSE                                -- the user does not have the rights
            BEGIN
                SELECT  @l_retValue = @c_INSUFFICIENT_RIGHTS
            END -- else the user does not have the rights
        END -- if object exists
        ELSE                                -- the object does not exist
        BEGIN
            -- set the return value with the error code:
            SELECT  @l_retValue = @c_OBJECTNOTFOUND
        END -- else the object does not exist
    END
    -- return the state value
    RETURN  @l_retValue
GO
-- p_Object$performDelete
