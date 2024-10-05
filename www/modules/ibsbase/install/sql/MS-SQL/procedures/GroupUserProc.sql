/******************************************************************************
 * All stored procedures regarding the GroupUser table. <BR>
 *
 * @version     1.10.0001, 03.08.1999
 *
 * @author      Klaus Reimüller (KR)  980707
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990803    Code cleaning.
 ******************************************************************************
 */


/******************************************************************************
 * Add a new user to a group. <BR>
 *
 * @input parameters:
 * @param   @groupId            Id of the group where the user shall be added.
 * @param   @userId             Id of the user to be added.
 * @param   @group              Id of group, which has to have some rights on 
 *                              the GroupUser object.
 * @param   @rights             Rights of the group.
 *
 * @output parameters:
 * @param   @oid_s              OID of the newly created object.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
-- delete existing procedure:
IF EXISTS (
            SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.p_GroupUser_01$addUser') 
                AND sysstat & 0xf = 4
          )
    DROP PROCEDURE #CONFVAR.ibsbase.dbOwner#.p_GroupUser_01$addUser
GO


-- create the new procedure:
CREATE PROCEDURE p_GroupUser_01$addUser
(
    -- input parameters:
    @groupId        GROUPID,
    @userId         USERID,
    @group          GROUPID = null,
    @rights         RIGHTS = null
)
AS
    DECLARE @id ID, @oid OBJECTID, @tVersionId TVERSIONID,
            @admin USERID, @rightsAdmin RIGHTS

    -- set tVersionId:
    SELECT  @tVersionId = 0x010100b1    -- tVersionId of type Group_01

    -- set admin user:
    SELECT  @admin = id
    FROM    ibs_User
    WHERE   name = N'Admin'

    -- get rights for admin user:
    SELECT  @rightsAdmin = SUM (id)
    FROM    ibs_Operation

    BEGIN TRANSACTION
        -- insert user into group:
        INSERT INTO ibs_GroupUser
               (groupId, userId)
        VALUES (@groupId, @userId)

        -- get id of newly created tuple:
        SELECT  @id = MAX (Id)
        FROM    ibs_GroupUser
        WHERE   groupId = @groupId
            AND userId = @userId

        -- create oid for saving rights:
        SELECT  @oid = CONVERT (BINARY (4), @tVersionId) + CONVERT (BINARY (4), @id)

        -- set rights for group:
        IF (@group <> null AND @rights <> null) -- rights set?
        BEGIN
            EXEC    p_Rights$setRights @oid, @group, @rights, 0
        END -- rights set

        -- set rights for admin:
        EXEC    p_Rights$setRights @oid, @admin, @rightsAdmin, 0

        -- actualize all cumulated rights:
        EXEC    p_Rights$updateRightsCum
    COMMIT TRANSACTION
GO
-- p_GroupUser_01$addUser
