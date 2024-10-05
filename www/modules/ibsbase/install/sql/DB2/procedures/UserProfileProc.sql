-------------------------------------------------------------------------------
-- All stored procedures regarding the ibs_UserProfile table. <BR>
--
-- @version     $Revision: 1.4 $, $Date: 2003/10/21 22:14:52 $
--              $Author: klaus $
--
-- @author      Marcel Samek (MS)  020818
-------------------------------------------------------------------------------

-------------------------------------------------------------------------------
-- Creates a new UserProfile (incl. rights check). <BR>
-- The rights are checked against the root of the system.
--
-- @input parameters:
-- @param   @userId             INT of the user who is creating 
--                              the UserProfile.
-- @param   @op                 Operation to be performed (possibly in the
--                              future used for rights check).
-- @param   @upUserId           ID of the user for whom the UserProfile is
--                              created.
-- @param   @tVersionId         Type of the new object.
-- @param   @name               Name of the object.
-- @param   @containerId_s      ID of the container where object shall be
--                              created in.
-- @param   @containerKind      Kind of object/container relationship
-- @param   @isLink             Defines if the object is a link
-- @param   @linkedObjectId_s   If the object is a link this is the ID of the
--                              where the link shows to.
-- @param   @description        Description of the object.
--
-- @output parameters:
-- @param   @oid_s              OID of the newly created object.
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The UserProfile was not created because there is no
--                          layout.
--/

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_UserProfile_01$create');
-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_UserProfile_01$create
(
    -- input parameters:
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_uUserId          INT,
    IN  ai_tVersionId       INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_containerId_s    VARCHAR (18),
    IN  ai_containerKind    INT,
    IN  ai_isLink           SMALLINT,
    IN  ai_linkedObjectId_s VARCHAR (18),
    IN  ai_description      VARCHAR (255),
    -- output parameters:
    OUT ao_oid_s            VARCHAR (18)
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of function
    DECLARE l_ePos          VARCHAR (2000); -- error position description
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_linkedObjectId CHAR (8) FOR BIT DATA;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_newsTimeLimit INT DEFAULT 5;
    DECLARE l_newsShowOnlyUnread SMALLINT DEFAULT 0;
    DECLARE l_outboxUseTimeLimit SMALLINT DEFAULT 0;
    DECLARE l_outboxTimeLimit INT DEFAULT 0;
    DECLARE l_outboxUseTimeFrame SMALLINT DEFAULT 0;
    DECLARE l_outboxTimeFrameFrom TIMESTAMP;
    DECLARE l_outboxTimeFrameTo TIMESTAMP;
    DECLARE l_showExtendedAttributes SMALLINT DEFAULT 0;
    DECLARE l_showRef       SMALLINT DEFAULT 1;
    DECLARE l_showExtendedRights SMALLINT DEFAULT 0;
    DECLARE l_saveProfile   SMALLINT DEFAULT 0;
    DECLARE l_showFilesInWindows SMALLINT DEFAULT 0;
    DECLARE l_lastLogin     TIMESTAMP;
    DECLARE l_notificationKind INT DEFAULT 1;
    DECLARE l_sendSms       SMALLINT DEFAULT 0;
    DECLARE l_addWeblink    SMALLINT DEFAULT 0;
    DECLARE l_domainId      INT DEFAULT 0;
    DECLARE l_layoutId      CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_oid               = c_NOOID;
    -- SET l_outboxTimeFrameFrom DATETIME
    -- SET l_outboxTimeFrameTo DATETIME
    SET l_domainId          = ai_userId / 16777216;
    SET l_showRef           = 1 - l_showExtendedAttributes;
    SET ao_oid_s            = c_NOOID_s;

-- body:
    -- conversions (objectidstring) - all input objectids must be converted
    CALL IBSDEV1.p_stringToByte (ai_containerId_s, l_containerId);
    CALL IBSDEV1.p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);

    -- finish previous and begin new transaction:
    COMMIT;

    IF (l_domainId > 0)                 -- possibly existing domain?
    THEN
        -- get the layout oid:
        SET l_sqlcode = 0;
        SELECT  oid
        INTO    l_layoutId
        FROM    IBSDEV1.ibs_Layout_01
        WHERE   UPPER (name) = UPPER ('standard')
            AND domainId = l_domainId;

        -- check if there occurred an error:
        IF (l_sqlcode = 100)            -- no data found?
        THEN 
            -- create error entry:
            SET l_ePos = 'get layout id; not found';
            SET l_retValue = c_OBJECTNOTFOUND;
            GOTO exception1;            -- call common exception handler
        -- end if no data found
        ELSEIF (l_sqlcode <> 0)         -- any other exception?
        THEN
            -- create error entry:
            SET l_ePos = 'get layout id; other';
            GOTO exception1;            -- call common exception handler
        END IF; -- else if any other exception
    END IF; -- if possibly existing domain

    -- create base object:
    CALL IBSDEV1.p_Object$performCreate
        (ai_userId, ai_op, ai_tVersionId,
        ai_name, ai_containerId_s, ai_containerKind,
        ai_isLink, ai_linkedObjectId_s, ai_description,
        ao_oid_s, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

CALL IBSDEV1.logError (100, 'p_UserProfile_01$create', l_sqlcode, 'l_profile_s', 'ai_userId',ai_userId, 'ai_name', ai_name, 'ai_op',ai_op, '', '', 'ai_tVersionId', ai_tVersionId, 'ai_containerId_s', ai_containerId_s, '', 0, 'ai_linkedObjectId_s', ai_linkedObjectId_s, '', 0, 'ai_description', ai_description, '', 0, 'ao_oid_s', ao_oid_s, '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '');

    IF (l_retValue = c_ALL_RIGHT)       -- object created successfully?
    THEN
        -- create object specific data:
        SET l_sqlcode = 0;
        INSERT INTO IBSDEV1.ibs_UserProfile
                (oid, userId, newsTimeLimit, newsShowOnlyUnread,
                outboxUseTimeLimit, outboxTimeLimit, outboxUseTimeFrame,
                outboxTimeFrameFrom, outboxTimeFrameTo,
                showExtendedAttributes, showFilesInWindows,
                lastLogin, layoutId, showRef, showExtendedRights,
                saveProfile, notificationKind, sendSms, addWeblink
                )
        VALUES  (l_oid, ai_uUserId, l_newsTimeLimit, l_newsShowOnlyUnread,
                l_outboxUseTimeLimit, l_outboxTimeLimit, l_outboxUseTimeFrame,
                l_outboxTimeFrameFrom, l_outboxTimeFrameTo,
                l_showExtendedAttributes, l_showFilesInWindows,
                l_lastLogin, l_layoutId, l_showRef, l_showExtendedRights,
                l_saveProfile, l_notificationKind, l_sendSms, l_addWeblink
                );
        GET DIAGNOSTICS l_rowcount = ROW_COUNT;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error at INSERT INTO';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception

        -- check if the UserProfile was created:
        IF (l_rowcount <= 0)
        THEN
            -- set the return value with the error code:
            SET l_retValue = c_OBJECTNOTFOUND; -- set return value
         END IF; -- set the return value with the error code:
    END IF; -- if object created successfully

    -- finish transaction:
    COMMIT;                             -- make changes permanent

    -- return the state value:
    RETURN l_retValue;

exception1:
    -- rollback to the transaction starting point:
    ROLLBACK;

    -- log the error:
    CALL IBSDEV1.logError (500, 'p_UserProfile_01$create', l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_name', ai_name,
        'ai_op', ai_op, 'ai_description', ai_description,
        'ai_uUserId', ai_uUserId, '', '',
        'ai_tVersionId', ai_tVersionId, '', '',
        'ai_containerKind', ai_containerKind, 'ai_containerId_s', ai_containerId_s,
        'ai_isLink', ai_isLink, 'ai_linkedObjectId_s', ai_linkedObjectId_s,
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    -- set error code:
    IF (l_retValue = c_ALL_RIGHT)       -- no error code set?
    THEN
        SET l_retValue = c_NOT_OK;
    END IF; -- if no error code set
    -- return error code:
    RETURN l_retValue;
END;
-- p_UserProfile_01$create


--------------------------------------------------------------------------------
-- Changes the attributes of an existing UserProfile. 
-- (incl. rights check).<BR>
--
-- @input parameters:
-- @param   @id                 ID of the object to be changed.
-- @param   @userId             ID of the user who is changing the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
-- @param   @name               Name of the object.
-- @param   @validUntil         Date until which the object is valid.
-- @param   @description        Description of the object.
-- @param   @showInNews         the showInNews flag
--
-- @param   @newsTimeLimit      Time limit for the newslist
-- @param   @newsShowOnlyUnread Flag to show only unread messages in newslist
-- @param   @outboxUseTimeLimit Flag to use time limit filter in outbox
-- @param   @outboxTimeLimit    Time limit filter for outbox (in days)
-- @param   @outboxUseTimeFrame Flag to use time frame filter in outbox
-- @param   @outboxTimeFrameFrom Begin date of time frame filter in outbox
-- @param   @outboxTimeFrameTo  End date of time frame filter in outbox
-- @param   @showExtendedAttributes Flag to show complete object attributes
-- @param   @showFilesInWindows Flag to show files in a separate window
-- @param   @lastLogin          Date of last login
-- @param   @layoutId_s
-- @param   @showExtendedRights
-- @param   @saveProfile
-- @param   @notificationKind      bit-pattern for kind of notification
-- @param   @sendSms               should sms be send when notify?
-- @param   @addWeblink            should weblink be added to email ?
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--/
-- delete existing procedure:
-- CALL IBSDEV1.p_dropProc ('p_UserProfile_01$change');
CALL IBSDEV1.p_dropProc ('p_UserProfile_01$change');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_UserProfile_01$change
(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_validUntil       TIMESTAMP,
    IN  ai_description      VARCHAR (255),
    IN  ai_showInNews       SMALLINT,
    -- type-specific parameters:
    IN  ai_newsTimeLimit    INT,
    IN  ai_newsShowOnlyUnread SMALLINT,
    IN  ai_outboxUseTimeLimit SMALLINT,
    IN  ai_outboxTimeLimit  INT,
    IN  ai_outboxUseTimeFrame SMALLINT,
    IN  ai_outboxTimeFrameFrom TIMESTAMP,
    IN  ai_outboxTimeFrameTo TIMESTAMP,
    IN  ai_showExtendedAttributes SMALLINT,
    IN  ai_showFilesInWindows SMALLINT,
    IN  ai_lastLogin        TIMESTAMP,
    IN  ai_layoutId_s       VARCHAR (18),
    IN  ai_showExtendedRights SMALLINT,
    IN  ai_saveProfile      SMALLINT,
    IN  ai_notificationKind INT,
    IN  ai_sendSms          SMALLINT,
    IN  ai_addWeblink       SMALLINT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of function
    DECLARE l_ePos          VARCHAR (2000); -- error position description
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_layoutId      CHAR (8) FOR BIT DATA;
    DECLARE l_showRef       SMALLINT;
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_retValue = c_ALL_RIGHT;           -- return value of this procedure
    SET l_showRef = 1 - ai_showExtendedAttributes;

-- body:
    -- conversions: all input object ids must be converted:
    CALL IBSDEV1.p_stringToByte (ai_layoutId_s, l_layoutId);

    -- finish previous and begin new transaction:
    COMMIT;

    -- perform the change of the object:
    CALL IBSDEV1.p_Object$performChange
        (ai_oid_s, ai_userId, ai_op, ai_name,
        ai_validUntil, ai_description, ai_showInNews, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_retValue = c_ALL_RIGHT)       -- operation properly performed?
    THEN
        -- update object type specific data:
        SET l_sqlcode = 0;
        UPDATE  IBSDEV1.ibs_UserProfile
        SET     newsTimeLimit = ai_newsTimeLimit,
                newsShowOnlyUnread = ai_newsShowOnlyUnread,
                outboxUseTimeLimit = ai_outboxUseTimeLimit,
                outboxTimeLimit = ai_outboxTimeLimit,
                outboxUseTimeFrame = ai_outboxUseTimeFrame,
                outboxTimeFrameFrom = ai_outboxTimeFrameFrom,
                outboxTimeFrameTo = ai_outboxTimeFrameTo,
                showExtendedAttributes = ai_showExtendedAttributes,
                showFilesInWindows = ai_showFilesInWindows,
                lastLogin = ai_lastLogin,
                layoutId = l_layoutId,
                showRef = l_showRef,
                showExtendedRights = ai_showExtendedRights,
                saveProfile = ai_saveProfile,
                notificationKind = ai_notificationKind,
                sendSms = ai_sendSms,
                addWeblink = ai_addWeblink
        WHERE   oid = l_oid;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in UPDATE - Statement';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception
    END IF; -- if operation properly performed

    -- finish transaction:
    COMMIT;                             -- make changes permanent

    -- return the state value:
    RETURN l_retValue;

exception1:
    -- rollback to the transaction starting point:
    ROLLBACK;

    -- log the error:
    CALL IBSDEV1.logError (500, 'p_UserProfile_01$change', l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_oid_s', ai_oid_s,
        'ai_op', ai_op, 'ai_name', ai_name,
        'ai_showInNews', ai_showInNews, 'ai_description', ai_description,
        'ai_newsTimeLimit', ai_newsTimeLimit, 'ai_layoutId_s', ai_layoutId_s,
        'ai_newsShowOnlyUnread', ai_newsShowOnlyUnread, '', '',
        'ai_outboxUseTimeLimit', ai_outboxUseTimeLimit, '', '',
        'ai_outboxTimeLimit', ai_outboxTimeLimit, '', '',
        'ai_outboxUseTimeFrame', ai_outboxUseTimeFrame, '', '',
        'ai_showExtendedAttributes', ai_showExtendedAttributes, '', '',
        'ai_showFilesInWindows', ai_showFilesInWindows, '', '');
    CALL IBSDEV1.logError (500, 'p_UserProfile_01$change', l_sqlcode, l_ePos,
        'ai_showExtendedRights', ai_showExtendedRights, '', '',
        'ai_saveProfile', ai_saveProfile, '', '',
        'ai_notificationKind', ai_notificationKind, '', '',
        'ai_sendSms', ai_sendSms, '', '',
        'ai_addWeblink', ai_addWeblink, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    -- set error code:
    IF (l_retValue = c_ALL_RIGHT)       -- no error code set?
    THEN
        SET l_retValue = c_NOT_OK;
    END IF; -- if no error code set
    -- return error code:
    RETURN l_retValue;
END;
-- p_UserProfile_01$change


--------------------------------------------------------------------------------
-- Gets all data from a given UserProfile.(incl. rights check) <BR>
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be retrieved.
-- @param   @userId             Id of the user who is getting the data.
-- @param   @op                 Operation to be performed (used for rights
--
-- @output parameters:
-- @param   @state              The object's state.
-- @param   @tVersionId         ID of the object's type (correct version).
-- @param   @typeName           Name of the object's type.
-- @param   @name               Name of the object itself.
-- @param   @containerId        ID of the object's container.
-- @param   @containerName      Name of the object's container.
-- @param   @containerKind      Kind of object/container relationship.
-- @param   @isLink             Is the object a link?
-- @param   @linkedObjectId     Link if isLink is true.
-- @param   @owner              ID of the owner of the object.
-- @param   @creationDate       Date when the object was created.
-- @param   @creator            ID of person who created the object.
-- @param   @lastChanged        Date of the last change of the object.
-- @param   @changer            ID of person who did the last change to the
--                              object.
-- @param   @validUntil         Date until which the object is valid.
-- @param   @description        Description of the object.
-- @param   @showInNews         the showInNews flag
-- @param   @checkedOut         Is the object checked out?
-- @param   @checkOutDate       Date when the object was checked out
-- @param   @checkOutUser       id of the user which checked out the object
-- @param   @checkOutUserOid    Oid of the user which checked out the object
--                              is only set if this user has the right to READ
--                              the checkOut user
-- @param   @checkOutUserName   name of the user which checked out the object,
--                              is only set if this user has the right to view
--                              the checkOut-User
--
--
-- @param   @newsTimeLimit       time limit for the newslist
-- @param   @newsShowOnlyUnread  flag to show only unread messages in newslist
-- @param   @outboxUseTimeLimit  flag to use time limit filter in outbox
-- @param   @outboxTimeLimit     time limit filter for outbox (in days)
-- @param   @outboxUseTimeFrame  flag to use time frame filter in outbox
-- @param   @outboxTimeFrameFrom begin date of time frame filter in outbox
-- @param   @outboxTimeFrameTo   end date of time frame filter in outbox
-- @param   @showExtendedAttributes  flag to show complete object attributes
-- @param   @showFilesInWindows      flag to show files in a separate window
-- @param   @lastLogin               date of last login
-- @param   @m2AbsBasePath           absolute m2 base path - workaround!
-- @param   @home                home path of the web
-- @param   @layoutId
-- @param   @layoutName
-- @param   @showRef
-- @param   @showExtendedRights
-- @param   @saveProfile
-- @param   @notificationKind      bit-pattern for kind of notification
-- @param   @sendSms               should sms be send when notify?
-- @param   @addWeblink            should weblink be added to email ?

-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--/

-- delete existing procedure:
-- CALL IBSDEV1.p_dropProc ('p_UserProfile_01$retrieve');
CALL IBSDEV1.p_dropProc ('p_UserProfile_01$retrieve');

    -- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_UserProfile_01$retrieve
(
    -- input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    -- output parameters
    OUT ao_state            INT,
    OUT ao_tVersionId       INT,
    OUT ao_typeName         VARCHAR (63),
    OUT ao_name             VARCHAR (63),
    OUT ao_containerId      CHAR (8) FOR BIT DATA,
    OUT ao_containerName    VARCHAR (63),
    OUT ao_containerKind    INT,
    OUT ao_isLink           SMALLINT,
    OUT ao_linkedObjectId   CHAR (8) FOR BIT DATA,
    OUT ao_owner            INT,
    OUT ao_ownerName        VARCHAR (63),
    OUT ao_creationDate     TIMESTAMP,
    OUT ao_creator          INT,
    OUT ao_creatorName      VARCHAR (63),
    OUT ao_lastChanged      TIMESTAMP,
    OUT ao_changer          INT,
    OUT ao_changerName      VARCHAR (63),
    OUT ao_validUntil       TIMESTAMP,
    OUT ao_description      VARCHAR (255),
    OUT ao_showInNews       SMALLINT,
    OUT ao_checkedOut       SMALLINT,
    OUT ao_checkOutDate     TIMESTAMP,
    OUT ao_checkOutUser     INT,
    OUT ao_checkOutUserOid  CHAR (8) FOR BIT DATA,
    OUT ao_checkOutUserName VARCHAR (63),
    -- type-specific attributes:
    OUT ao_newsTimeLimit    INT,
    OUT ao_newsShowOnlyUnread SMALLINT,
    OUT ao_outboxUseTimeLimit SMALLINT,
    OUT ao_outboxTimeLimit  INT,
    OUT ao_outboxUseTimeFrame SMALLINT,
    OUT ao_outboxTimeFrameFrom TIMESTAMP,
    OUT ao_outboxTimeFrameTo TIMESTAMP,
    OUT ao_showExtendedAttributes SMALLINT,
    OUT ao_showFilesInWindows SMALLINT,
    OUT ao_lastLogin        TIMESTAMP,
    OUT ao_m2AbsBasePath    VARCHAR (255),
    OUT ao_home             VARCHAR (255),
    OUT ao_layoutId         CHAR (8) FOR BIT DATA,
    OUT ao_layoutName       VARCHAR (63),
    OUT ao_showRef          SMALLINT,
    OUT ao_showExtendedRights SMALLINT,
    OUT ao_saveProfile      SMALLINT,
    OUT ao_notificationKind INT,
    OUT ao_sendSms          SMALLINT,
    OUT ao_addWeblink       SMALLINT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of function
    DECLARE l_ePos          VARCHAR (2000); -- error position description
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_retValue = c_ALL_RIGHT;

-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    -- retrieve the base object data:
    CALL IBSDEV1.p_Object$performRetrieve
        (ai_oid_s, ai_userId, ai_op,
        ao_state, ao_tVersionId, ao_typeName, ao_name,  ao_containerId,
        ao_containerName, ao_containerKind, ao_isLink, ao_linkedObjectId,
        ao_owner, ao_ownerName, ao_creationDate, ao_creator, ao_creatorName,
        ao_lastChanged, ao_changer, ao_changerName, ao_validUntil,
        ao_description, ao_showInNews, ao_checkedOut, ao_checkOutDate,
        ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_retValue = c_ALL_RIGHT)       -- operation properly performed?
    THEN
        -- get the data of the UserProfile and return them:
        SET l_sqlcode = 0;
        SELECT  newsTimeLimit, newsShowOnlyUnread, outboxUseTimeLimit,
                outboxTimeLimit, outboxUseTimeFrame,
                outboxTimeFrameFrom, outboxTimeFrameTo,
                showExtendedAttributes, showFilesInWindows,
                lastLogin, layoutId, showRef,
                showExtendedRights, saveProfile,
                notificationKind, sendSms, addWeblink
        INTO    ao_newsTimeLimit, ao_newsShowOnlyUnread, ao_outboxUseTimeLimit,
                ao_outboxTimeLimit, ao_outboxUseTimeFrame,
                ao_outboxTimeFrameFrom, ao_outboxTimeFrameTo,
                ao_showExtendedAttributes, ao_showFilesInWindows,
                ao_lastLogin, ao_layoutId, ao_showRef,
                ao_showExtendedRights, ao_saveProfile,
                ao_notificationKind, ao_sendSms, ao_addWeblink
        FROM    IBSDEV1.ibs_UserProfile
        WHERE   oid = l_oid;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'rror in get userprofiledata';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception

        SET l_sqlcode = 0;
        SELECT  name
        INTO    ao_layoutName
        FROM    IBSDEV1.ibs_Layout_01
        WHERE   oid = ao_layoutId;

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in get layoutname';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception

        -- WORKAROUND
        -- get m2 absolute base path
        SET l_sqlcode = 0;
        SELECT  value
        INTO    ao_m2AbsBasePath
        FROM    IBSDEV1.ibs_System
        WHERE   name = 'ABS_BASE_PATH';

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in get basepath';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception

        -- get web absolute base path
        SET l_sqlcode = 0;
        SELECT  value
        INTO    ao_home
        FROM    IBSDEV1.ibs_System
        WHERE   name = 'WWW_BASE_PATH';

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)             -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = 'Error in get www_base_path';
            GOTO exception1;            -- call common exception handler
        END IF; -- if any exception
    END IF; -- if operation properly performed

    -- finish transaction:
    COMMIT;                             -- make changes permanent

    -- return the state value:
    RETURN l_retValue;

exception1:
    -- rollback to the transaction starting point:
    ROLLBACK;

    -- log the error:
    CALL IBSDEV1.logError (500, 'p_UserProfile_01$retrieve', l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_oid_s', ai_oid_s,
        'ai_op', ai_op, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    -- set error code:
    IF (l_retValue = c_ALL_RIGHT)       -- no error code set?
    THEN
        SET l_retValue = c_NOT_OK;
    END IF; -- if no error code set
    -- return error code:
    RETURN l_retValue;
END;
-- p_UserProfile_01$retrieve


--------------------------------------------------------------------------------
-- Deletes an object and all its values (incl. rights check). <BR>
-- This procedure also delets all links showing to this object.
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be deleted.
-- @param   @userId             ID of the user who is deleting the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--/

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_UserProfile_01$delete');
-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_UserProfile_01$delete
(
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0; -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1; -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2; -- not enough rights for this
                                            -- operation
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3; -- the object was not found

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of function
    DECLARE l_ePos          VARCHAR (2000); -- error position description
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
     SET l_retValue = c_ALL_RIGHT;          -- return value of this procedure

-- body:
    -- conversions (objectidstring) - all input objectids must be converted
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

    -- finish previous and begin new transaction:
    COMMIT;

    -- delete base object:
    CALL IBSDEV1.p_Object$performDelete (ai_oid_s, ai_userId, ai_op, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

/* KR should not be done because of undo functionality!
    IF (l_retValue = c_ALL_RIGHT)       -- operation properly performed?
    THEN
        -- delete object specific data:
        -- (deletes all type specific tuples which are not within ibs_Object)
        DELETE  FROM IBSDEV1.ibs_UserProfile
        WHERE   oid = l_oid;
    END IF; -- if operation properly performed
*/

    -- finish transaction:
    COMMIT;                             -- make changes permanent

    -- return the state value:
    RETURN l_retValue;

exception1:
    -- rollback to the transaction starting point:
    ROLLBACK;

    -- log the error:
    CALL IBSDEV1.logError (500, 'p_UserProfile_01$delete', l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_oid_s', ai_oid_s,
        'ai_op', ai_op, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    -- set error code:
    IF (l_retValue = c_ALL_RIGHT)       -- no error code set?
    THEN
        SET l_retValue = c_NOT_OK;
    END IF; -- if no error code set
    -- return error code:
    RETURN l_retValue;
END;
-- p_UserProfile_01$delete
