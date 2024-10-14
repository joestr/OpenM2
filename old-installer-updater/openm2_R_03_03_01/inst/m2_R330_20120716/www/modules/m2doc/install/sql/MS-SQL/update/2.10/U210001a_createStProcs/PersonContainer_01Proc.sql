/******************************************************************************
 * All stored procedures regarding the PersonContainer_01 Object. <BR>
 * 
 * @version     $Id: PersonContainer_01Proc.sql,v 1.1 2010/02/25 13:54:18 btatzmann Exp $
 *
 * @author      Keim Christine (Ck)  980626
 ******************************************************************************
 */


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
 * @param   @containerName      Name of the object's container.
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
 * @param   @description        Description of the object.
 * @param   @showInNews         Display object in the news.
 * @param   @checkedOut         Is the object checked out?
 * @param   @checkOutDate       Date when the object was checked out
 * @param   @checkOutUser       id of the user which checked out the object
 * @param   @checkOutUserOid    Oid of the user which checked out the object
 *                              is only set if this user has the right to READ
 *                              the checkOut user
 * @param   @checkOutUserName   name of the user which checked out the object,
 *                              is only set if this user has the right to view
 *                              the checkOut-User
 *
 * @param   @maxlevels          Maximum of the levels allowed in the discussion
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- delete existing procedure:
IF EXISTS ( SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.p_PersonContainer_01$retrieve') 
                AND sysstat & 0xf = 4)
	DROP PROCEDURE #CONFVAR.ibsbase.dbOwner#.p_PersonContainer_01$retrieve
GO

-- create the new procedure:
CREATE PROCEDURE p_PersonContainer_01$retrieve
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
    @checkOutUserName NAME          OUTPUT
)
AS
    ---------------------------------------------------------------------------
    -- CONVERTIONS (OBJECTIDSTRING) - all input objectids must be converted
    DECLARE @oid OBJECTID
    EXEC    p_stringToByte @oid_s, @oid OUTPUT

    ---------------------------------------------------------------------------
    -- DEFINITIONS
    -- define return constants
    DECLARE @INSUFFICIENT_RIGHTS INT, @ALL_RIGHT INT, @OBJECTNOTFOUND INT
    -- define right constants
    DECLARE @RIGHT_READ RIGHTS
    -- define constants
    DECLARE @INNEWS INT
    DECLARE @ISCHECKEDOUT INT
    DECLARE @tempName NAME
    DECLARE @tempOid OBJECTID

    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,   -- return values
            @OBJECTNOTFOUND = 3,
            @RIGHT_READ = 2,                            -- access rights
            @INNEWS = 4,
            @ISCHECKEDOUT = 16

    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    DECLARE @rights RIGHTS              -- return value of called procedure
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT, @rights = 0

    ---------------------------------------------------------------------------
    -- START
    -- get container id of object
    SELECT  @containerId = o.containerId, @containerName = o2.name 
    FROM    ibs_Object o, ibs_Object o2
    WHERE   o.oid = @oid
    AND     o2.oid = o.containerId

    -- check if the object exists:
    IF (@@ROWCOUNT > 0)                 -- object exists?
    BEGIN
        -- get rights for this user
        EXEC p_Rights$checkRights
             @oid,                      -- given object to be accessed by user
             @containerId,              -- container of given object
             @userId,                   -- user_id
             @op,                       -- required rights user must have to 
                                        -- retrieve object (op. to be 
                                        -- performed)
             @rights OUTPUT             -- returned value

        -- check if the user has the necessary rights
        IF (@rights > 0)                -- the user has the rights?
        BEGIN

            -- get the data of the object and return it
            SELECT  @state = o.state, @tVersionId = o.tVersionId, 
                    @typeName = o.typeName, @name = o.name, 
                    @containerId = o.containerId, 
                    @containerKind = o.containerKind, @isLink = o.isLink, 
                    @linkedObjectId = o.linkedObjectId, 
                    @owner = o.owner, @ownerName = own.fullname,
                    @creationDate = o.creationDate, @creator = o.creator, 
                    @creatorName = cr.fullname,
                    @lastChanged = o.lastChanged, @changer = o.changer, 
                    @changerName = ch.fullname,
                    @validUntil = o.validUntil,
                    @showInNews = (o.flags & @INNEWS),
                    @checkedOut = (o.flags & @ISCHECKEDOUT)
            FROM    ibs_Object o LEFT JOIN ibs_User own ON o.owner = own.id
                        LEFT JOIN ibs_User cr ON o.creator = cr.id
                        LEFT JOIN ibs_User ch ON o.changer = ch.id
            WHERE   o.oid = @oid

            IF (@checkedOut=1)
            BEGIN
                -- get the info who checked out the object
                SELECT @checkOutDate = ch.checkout,
                       @checkOutUser = ch.userId,
		       @tempOid = u.oid,
                       @tempName = u.fullname
                FROM   ibs_CheckOut_01 ch LEFT JOIN ibs_User u ON u.id = ch.userid
                WHERE  ch.oid = @oid

                -- rights set for viewing the User?
                EXEC p_Rights$checkRights
                    @tempOid,                  -- given object to be accessed by user
                    0x0000000000000000,        -- containeroid - no oid in this case cause irrelevant
                    @userId,                   -- user_id
                    2,                         -- required rights user must have to
                                               -- view object (op. to be
                                               -- performed)
                    @rights OUTPUT             -- returned value

                 -- check if the user has the necessary rights
                 IF (@rights = 2)              -- the user has the rights?
                 BEGIN
                      SELECT @checkOutUserName = @tempName
                 END -- if the user has the rights to see the user who checked out the object


                -- rights set for reading the User?
                EXEC p_Rights$checkRights
                    @tempOid,                  -- given object to be accessed by user
                    0x0000000000000000,        -- containeroid - no oid in this case cause irrelevant
                    @userId,                   -- user_id
                    4,                         -- required rights user must have to
                                               -- read object (op. to be
                                               -- performed)
                    @rights OUTPUT             -- returned value

                 -- check if the user has the necessary rights
                 IF (@rights = 4)              -- the user has the rights?
                 BEGIN
                      SELECT @checkOutUserOid = @tempOid
                 END -- if the user has the rights to read the user who checked out the object


            END -- if the object is checked out

        -- Set Object as Read --
        EXEC    p_setRead @oid, @userId

        END -- if the user has the rights
        ELSE                            -- the user does not have the rights
        BEGIN
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
-- p_PersonContainer_01$retrieve
