/******************************************************************************
 * All stored procedures regarding the Termin_01 object. <BR>
 *
 * @version     $Id: Termin_01Proc.sql,v 1.11 2009/12/02 18:35:03 rburgermann Exp $
 *
 * @author      Horst Pichler   (HP)  98xxxx
 ******************************************************************************
 */


/******************************************************************************
 * Creates a new object (incl. rights check). <BR>
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
EXEC p_dropProc N'p_Termin_01$create'
GO

-- create the new procedure:
CREATE PROCEDURE p_Termin_01$create
(
    -- input parameters:
    @userId         USERID,                 -- users id
    @op             INT,                    -- operation
    @tVersionId     TVERSIONID,             -- type version id of object (term)
    @name           NAME,                   -- name of term
    @containerId_s  OBJECTIDSTRING,         -- objects container
    @containerKind  INT,                    -- 'part-of' or 'normal' container
    @isLink         BOOL,                   -- is object a link
    @linkedObjectId_s OBJECTIDSTRING,       -- link to object ..
    @description    DESCRIPTION,            -- textual description
    -- output parameters:               
    @oid_s          OBJECTIDSTRING OUTPUT   -- created oid
)
AS
    ---------------------------------------------------------------------------
    -- CONVERTIONS (OBJECTIDSTRING) - all input objectids must be converted
    DECLARE @containerId    OBJECTID
    DECLARE @linkedObjectId OBJECTID

    EXEC p_stringToByte @containerId_s, @containerId OUTPUT
    EXEC p_stringToByte @linkedObjectId_s, @linkedObjectId OUTPUT

    ---------------------------------------------------------------------------
    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT, @NOT_OK INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2, @NOT_OK = 0 -- return values
    -- define return values
    DECLARE @retValue INT                   -- return value of this procedure
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT

    ---------------------------------------------------------------------------
    -- START
    BEGIN TRANSACTION

        DECLARE @oid    OBJECTID
        
        EXEC @retValue = p_Object$performCreate @userId, @op, @tVersionId, @name, @containerId_s,
                @containerKind, @isLink, @linkedObjectId_s, @description, 
                @oid_s OUTPUT, @oid OUTPUT

	    IF @retValue = @ALL_RIGHT       -- object created successfully?
        BEGIN
            -- create object specific data:
            -- insert dummies into table Termin_01 for given oid
            
            INSERT INTO m2_Termin_01 
                (oid, startDate, endDate, place, 
                 participants, maxNumParticipants, showParticipants, deadline, attachments)
            VALUES  (@oid, getDate (), getDate (), N'', 0, 0, 0, NULL, 0)

            -- check if insertion was performed properly:
            IF (@@ROWCOUNT <= 0)        -- no row affected?
            BEGIN
                SELECT  @retValue = @NOT_OK -- set return value
            END -- if no row affected
        END -- if object created successfully
    COMMIT TRANSACTION

    RETURN  @retValue
GO
-- p_Termin_01$create


/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
 * This procedure also creates or deletes a participant container (is sub-
 * object which holds all participating users).
 *
 * @input parameters:
 * @param   @id                 ID of the object to be changed.
 * @param   @userId             ID of the user who is changing the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 * @param   @name               Name of the object.
 * @param   @validUntil         Date until which the object is valid.
 * @param   @description        Description of the object.
 * @param   @showInNews         show in news flag
 *
 * @param   @startDate          begin date&time of term
 * @param   @endDate            end date&time of term
 * @param   @place              place where term happens
 * @param   @participants       does term have participants
 * @param   @maxNumParticipants max. number of participants
 * @param   @showParticipants   view participants allowed?
 * @param   @deadline           last announcements
 *
 * @param   @attachments        may term have attachments?
 * 
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Termin_01$change'
GO

-- create the new procedure:
CREATE PROCEDURE p_Termin_01$change
(
    -- input parameters:
    @oid_s          OBJECTIDSTRING,     -- objects id as a string
    @userId            USERID,             -- users id
    @op             INT,                -- operation
    @name           NAME,               -- name of object
    @validUntil     DATETIME,           -- valid in system
    @description    DESCRIPTION,        -- textual description
    @showInNews     BOOL,               -- show in news ?
	---- object specific attributes:
    @startDate      DATETIME,           -- begin date&time of term
	@endDate        DATETIME,           -- end date&time of term
	@place	        NVARCHAR(255),      -- place where term happens
    @participants       BOOL,           -- does term have participants
    @maxNumParticipants    INT,         -- max. number of participants
    @showParticipants   BOOL,           -- view participants allowed?
    @deadline       DATETIME,           -- last announcements
    @attachments    BOOL                -- may term have attachments
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this
                                            -- operation
    @c_OBJECTNOTFOUND       INT,            -- the object was not found                                        
    @c_ALREADY_EXISTS       INT,            -- the object already exists
    @c_NOOID_s              OBJECTIDSTRING, -- no oid as string
    @c_TAB_PARTICIPANTS     NAME,           -- code of participants tab

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_rowCount             INT,            -- row counter
    @l_oid_s                OBJECTIDSTRING, -- string representation of oid
    @l_tabOid               OBJECTID,       -- the oid of the tab object
    @l_tabOid_s             OBJECTIDSTRING, -- string representation of oid
    @l_consistsOfId         ID,             -- id of tab in ibs_ConsistsOf
    @l_tabTVersionId        TVERSIONID,     -- tVersionId of the actual tab
    @l_tabName              NAME, 
    @l_tabDescription       DESCRIPTION,
    @l_tabProc              STOREDPROCNAME  -- name of stored procedure for
                                            -- creating the tab object
    
    
    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_OBJECTNOTFOUND       = 3,
    @c_ALREADY_EXISTS       = 21,
    @c_NOOID_s              = '0x0000000000000000',
    @c_TAB_PARTICIPANTS     = N'Participants'
    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0,
    @l_rowCount = 0

-- body:
    ---------------------------------------------------------------------------
    -- CONVERTIONS (OBJECTIDSTRING) - all input object ids must be converted
    DECLARE @oid            OBJECTID
    EXEC p_stringToByte @oid_s, @oid OUTPUT

    ---------------------------------------------------------------------------
    -- DEFINITIONS
    -- participants container id - string
    DECLARE @partContId     OBJECTID
    DECLARE @partContId_s   OBJECTIDSTRING
    DECLARE @rights RIGHTS, @grpAll GROUPID

    DECLARE @owner USERID


    ---------------------------------------------------------------------------
    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT
    -- set constants
    SELECT  @ALL_RIGHT = 1
    -- define return values
    DECLARE @retValue INT
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT

    BEGIN TRANSACTION
        -- perform the change of the object:
        EXEC @retValue = p_Object$performChange @oid_s, @userId, @op, @name,
                @validUntil, @description, @showInNews

        IF (@retValue = @ALL_RIGHT)     -- operation properly performed?
        BEGIN
            -- check if participants container is needed:
            IF (@participants <> 0)     -- participants container needed?
            BEGIN
                -- get participant container for term
                -- oid of term is stored as containerId in participant container
                EXEC @retValue = p_Object$getTabOid @oid, @c_TAB_PARTICIPANTS,
                        @partContId OUTPUT

                -- check if part. cont. object already exists:
                IF (@retValue = @c_OBJECTNOTFOUND)
                                        -- no participants container exists?
                BEGIN
                    -- create the participant container:
                    EXEC @retValue = p_Object$createTab @userId, @op, @oid,
                        @c_TAB_PARTICIPANTS, @partContId OUTPUT
                END -- if no participants container exists

                -- now modify rights of participant container!
                -- rule: everyone who is allowed to see the term itself,
                -- is also allowed to see the participant-tab and 
                -- [un]announce participants.
                -- exception:
                -- flag 'showParticipants' not set: 
                --  * owner of term allowed to see/modify all
                --  * cancel 'view' right for all other users; they are only
                --    allowed to see/modify participants-objects of which they
                --    are owner!

                -- get group-id of all users in current domain
                SELECT  @grpAll = d.allGroupId
                FROM    ibs_Domain_01 d, ibs_User u
                WHERE   u.id = @userId
                    AND d.id = u.domainId

                -- is flag 'showParticipants' set?
                IF (@showParticipants <> 0) -- flag 'showParticipants' set?
                BEGIN
                    -- set rights-value for SOME operations (viewElems, 
                    -- read, view, new, delElem) for ALL users.
                    -- will recursively be set for all participants in container
                    SELECT  @rights = SUM (id)
                    FROM    ibs_Operation
                    WHERE   name IN (N'viewElems', N'read', N'view', N'new',
                                N'addElem', N'delElem')
                END -- if flag 'showParticipants' set
                ELSE                    -- flag 'showParticipants' not set
                BEGIN
                    -- set rights-value for only a FEW operations (viewElems, 
                    -- read, view, new, delElem) for ALL users
                    -- will recursively be set for all participants in container
                    SELECT  @rights = SUM (id)
                    FROM    ibs_Operation
                    WHERE   name IN (N'viewElems', N'read', N'new',
                                N'addElem', N'delElem')
                END -- else flag 'showParticipants' not set

                -- set rights for participant container (recursively) for ALL
                -- users:
                EXEC p_Rights$setRights @partContId, @grpAll, @rights, 1

/* not necessary because owner rights are already set by system functions                
                -- reset rights for owner; they are always the same: ALL rights!
                -- get rights
                SELECT  @rights = SUM (id)
                FROM    ibs_Operation
                -- get owner
                SELECT @owner = owner
                FROM ibs_Object
                WHERE oid = @partContId
                -- reset all rights for owner (recursively)
                EXEC p_Rights$addRights @partContId, @owner, @rights, 1
*/
            END -- if participants container needed
            ELSE                        -- no participants container needed
            BEGIN
                -- delete not needed tab participants container:
                EXEC @retValue =
                    p_Object$deleteTab @userId, @op, @oid, @c_TAB_PARTICIPANTS

                -- reset values:
                SELECT  @partContId = NULL,
                        @participants = 0,
                        @maxNumParticipants = 0,
                        @showParticipants = 0
            END -- else no participants container needed

            -- check if dead line is set:
            IF @deadline IS NULL        -- no dead line set?
            BEGIN
                -- set start date as dead line:
                SELECT  @deadline = @startDate
            END -- if no dead line set

            -- update object specific values in table Termin_01:
            UPDATE  m2_Termin_01 
            SET     startDate = @startDate,
                    endDate = @endDate,
                    place = @place,
                    participants = @participants,
                    maxNumParticipants = @maxNumParticipants,
                    showParticipants = @showParticipants,
                    deadline = @deadline,
                    attachments = @attachments
            WHERE   oid = @oid
        END -- if operation properly performed

    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO
-- p_Termin_01$change


/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be retrieved.
 * @param   @userId             Id of the user who is getting the data.
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
 *
 * @param   @startDateTime      start of term/date
 * @param   @endDateTime        end of term/date
 * @param   @place              where does term take place
 * @param   @participants       term with participants?
 * @param   @maxNumParticipants max. number of participants
 * @param   @showParticipants   participants viewable?
 * @param   @curNumParticipants current num. of participants
 * @param   @partContId         participants container
 * @param   @deadline           last announcements
 * @param   @showInNews         show in news?
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Termin_01$retrieve'
GO

-- create the new procedure:
CREATE PROCEDURE p_Termin_01$retrieve
(
    -- input parameters:
    @ai_oid_s               OBJECTIDSTRING,
    @ai_userId              USERID,
    @ai_op                  INT,
    -- common output parameters:
    @ao_state               STATE       OUTPUT,
    @ao_tVersionId          TVERSIONID  OUTPUT,
    @ao_typeName            NAME        OUTPUT,
    @ao_name                NAME        OUTPUT,
    @ao_containerId         OBJECTID    OUTPUT,
    @ao_containerName       NAME        OUTPUT,
    @ao_containerKind       INT         OUTPUT,
    @ao_isLink              BOOL        OUTPUT,
    @ao_linkedObjectId      OBJECTID    OUTPUT,
    @ao_owner               USERID      OUTPUT,
    @ao_ownerName           NAME        OUTPUT,
    @ao_creationDate        DATETIME    OUTPUT,
    @ao_creator             USERID      OUTPUT,
    @ao_creatorName         NAME        OUTPUT,
    @ao_lastChanged         DATETIME    OUTPUT,
    @ao_changer             USERID      OUTPUT,
    @ao_changerName         NAME        OUTPUT,
    @ao_validUntil          DATETIME    OUTPUT,
    @ao_description         DESCRIPTION OUTPUT,
    @ao_showInNews          BOOL        OUTPUT,
    @ao_checkedOut          BOOL        OUTPUT,
    @ao_checkOutDate        DATETIME    OUTPUT,
    @ao_checkOutUser        USERID      OUTPUT,
    @ao_checkOutUserOid     OBJECTID    OUTPUT,
    @ao_checkOutUserName    NAME        OUTPUT,
    -- object-type specific output parameters:
    @ao_startDateTime       DATETIME    OUTPUT,
    @ao_endDateTime         DATETIME    OUTPUT,
    @ao_place               NVARCHAR (255) OUTPUT,
    @ao_participants        BOOL        OUTPUT,
    @ao_maxNumParticipants  INT         OUTPUT,
    @ao_showParticipants    BOOL        OUTPUT,
    @ao_curNumParticipants  INT         OUTPUT,
    @ao_partContId          OBJECTID    OUTPUT,
    @ao_deadline            DATETIME    OUTPUT,
    @ao_attachments         BOOL        OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.

    -- local variables:
    @l_retValue             INT,            -- return value of function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_oid                  OBJECTID        -- oid of the actual object

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0

-- body:
    BEGIN TRANSACTION
        EXEC @l_retValue = p_Object$performRetrieve
            @ai_oid_s, @ai_userId, @ai_op,
            @ao_state OUTPUT, @ao_tVersionId OUTPUT, @ao_typeName OUTPUT,
            @ao_name OUTPUT, @ao_containerId OUTPUT, @ao_containerName OUTPUT,
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

        -- operation properly performed?
        IF (@l_retValue = @c_ALL_RIGHT)
        BEGIN
            -- get type-specific values out of table Termin_01:
            SELECT  @ao_startDateTime = startDate,
                    @ao_endDateTime = endDate,
                    @ao_place = place,
                    @ao_participants = participants,
                    @ao_showParticipants = showParticipants,
                    @ao_maxNumParticipants = maxNumParticipants,
                    @ao_deadline = deadline,
                    @ao_attachments = attachments
            FROM    m2_Termin_01
            WHERE   oid = @l_oid

            -- check if there occurred an error:
            EXEC @l_error = ibs_error.prepareError @@error,
                N'get type-specific data', @l_ePos OUTPUT
            IF (@l_error <> 0)          -- an error occurred?
                GOTO exception          -- call common exception handler

            -- get participant container for term
            -- oid of term is stored as containerId in participant container
            -- only 1 participant container for each term is possible
            EXEC @l_retValue = p_Object$getTabOid @l_oid, N'Participants',
                @ao_partContId OUTPUT

            -- get current number of participants:
            IF (@l_retValue = @c_ALL_RIGHT AND @ao_participants <> 0)
                                        -- term with participants?
            BEGIN
                -- count number of participants for term:
                SELECT  @ao_curNumParticipants = COUNT (oid)
                FROM    ibs_Object
                WHERE   containerId = @ao_partContId
                    AND tVersionId = 0x01012E01 -- TV_Participant
                    AND state = 2

                -- check if there occurred an error:
                EXEC @l_error = ibs_error.prepareError @@error,
                    N'count participants', @l_ePos OUTPUT
                IF (@l_error <> 0)      -- an error occurred?
                    GOTO exception      -- call common exception handler
            END -- if term with participants
            ELSE                        -- no participants allowed
            BEGIN
                -- reset return value and the number of participants:
                SELECT  @l_retValue = @c_ALL_RIGHT,
                        @ao_curNumParticipants = 0
            END -- else no participants allowed
        END -- if operation properly performed

    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- roll back to the beginning of the transaction:
    ROLLBACK TRANSACTION                -- undo changes
    -- log the error:
    EXEC ibs_error.logError 500, N'p_Termin_01$retrieve', @l_error, @l_ePos,
            N'ai_userId', @ai_userId,
            N'ai_oid_s', @ai_oid_s,
            N'ai_op', @ai_op
    -- return error code:
    RETURN  @c_NOT_OK
GO
-- p_Termin_01$retrieve


/******************************************************************************
 * Deletes an object and all its values (incl. rights check). <BR>
 * This procedure also delets all links showing to this object.
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be deleted.
 * @param   @userId             ID of the user who is deleting the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Termin_01$delete'
GO

-- create the new procedure:
CREATE PROCEDURE p_Termin_01$delete
(
    @oid_s          OBJECTIDSTRING,     -- objects id as string
    @userId         USERID,             -- users id
    @op             INT                 -- operation
)
AS
    ---------------------------------------------------------------------------
    -- CONVERTIONS (OBJECTIDSTRING) - all input objectids must be converted
    DECLARE @oid OBJECTID
    EXEC    p_stringToByte @oid_s, @oid OUTPUT

    ---------------------------------------------------------------------------
    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT
    -- set constants
    SELECT  @ALL_RIGHT = 1
    -- define return values
    DECLARE @retValue INT
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT
    -- participants container
    DECLARE @partContId OBJECTID


    ---------------------------------------------------------------------------
    -- START
    BEGIN TRANSACTION

        -- perform deletion of object:
        EXEC @retValue = p_Object$performDelete @oid_s, @userId, @op

        -- operation properly performed?
        IF (@retValue = @ALL_RIGHT)
        BEGIN
            -- delete object specific table entries
            DELETE  m2_Termin_01
            WHERE oid = @oid
        END

    COMMIT TRANSACTION
    -- return the state value
    RETURN  @retValue
GO
-- p_Termin_01$delete


/******************************************************************************
 * This procedure checks if a given term has any overlapping terms. <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object.
 * @param   @userId             ID of the user.
 *
 * @output parameters:
 * @param   @found              number of found overlapping terms
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Termin_01$checkOverlap'
GO

-- create the new procedure:
CREATE PROCEDURE p_Termin_01$checkOverlap
(
    -- input parameters:
    @oid_s     OBJECTIDSTRING,     -- oid of given term
    @userId    USERID,             -- users id
    @startDate DATETIME,           -- beginning Date
    @endDate   DATETIME,           -- end Date
    -- output parameter
    @found  INT OUTPUT          -- number of found overlapping terms
)
AS
    ---------------------------------------------------------------------------
    -- CONVERTIONS (OBJECTIDSTRING) - all input object ids must be converted
    DECLARE @oid            OBJECTID
    EXEC p_stringToByte @oid_s, @oid OUTPUT

    -- VIEWING RIGHTS
    DECLARE @RIGHT_VIEW RIGHTS
    SELECT  @RIGHT_VIEW = 0x00000002
    ---------------------------------------------------------------------------
    -- START

    -- get numberof  overlapping terms for given current term
    SELECT DISTINCT @found = COUNT(*)
    FROM m2_Termin_01 t,
         ibs_Object o,
         v_Container$rights c
    WHERE t.oid <> @oid
    -- check overlapping terms
    AND (@startDate BETWEEN t.startDate AND t.endDate
         OR @endDate BETWEEN t.startDate AND t.endDate
         OR (@startDate <= t.startDate AND @endDate >= t.endDate))
    -- join m2_termin_01
    AND t.oid = o.oid 
    -- check if user has right to view overlapping terms
    AND c.oid = o.oid
    AND (c.rights & @RIGHT_VIEW) = @RIGHT_VIEW
    AND c.userId = @userId
/*
    -- get numberof  overlapping terms for given current term
    SELECT DISTINCT @found = COUNT(*)
    FROM m2_Termin_01 t,
         ibs_Object o,
         ibs_Object o1,
         m2_Termin_01 t1,
         v_Container_02$rights c, 
         v_Container_02$rights c1
    -- given term
    WHERE t1.oid = @oid
    -- do not check term with term itself
    AND t.oid <> @oid
    -- check overlapping terms
    AND (t1.startDate BETWEEN t.startDate AND t.endDate
         OR t1.endDate BETWEEN t.startDate AND t.endDate
         OR (t1.startDate <= t.startDate AND t1.endDate >= t.endDate))
    -- join m2_termin_01
    AND t.oid = o.oid 
    -- check if user has right to view overlapping terms
    AND c.oid = o.oid
    AND (c.rights & 2) > 0
    AND c.uid = @userId
    -- join with containers of overlapping terms
    AND o.containerid = o1.oid
    -- check if user has right to view containers
    AND c1.oid = o1.oid
    AND (c1.rights & 2) > 0 
    AND c1.uid = @userId
*/
  
    -- exit procedure
    RETURN
GO
-- p_Termin_01$checkOverlap


/******************************************************************************
 * Copy an object and all its values. <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be copied.
 * @param   @userId             ID of the user who is copying the object.
 * @param   @newOid             ID of the copy-object.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Termin_01$BOCopy'
GO

-- create the new procedure:
CREATE PROCEDURE p_Termin_01$BOCopy
(
    -- common input parameters:
    @oid            OBJECTID,
    @userId            USERID,
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

    -- make an insert for all type specific tables:

    INSERT  INTO m2_Termin_01
            (oid, startDate, endDate, place, participants, maxNumParticipants,
            showParticipants, deadline,attachments)
    SELECT  @newOid, startDate, endDate, place, participants, maxNumParticipants,
            showParticipants, deadline, attachments
    FROM    m2_Termin_01
    WHERE   oid = @oid

    -- check if insert was performed correctly:
    IF (@@ROWCOUNT >= 1)                -- at least one row affected?
        SELECT  @retValue = @ALL_RIGHT  -- set return value

    -- return the state value:
    RETURN  @retValue
GO
-- p_Termin_01$BOCopy
