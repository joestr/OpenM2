/******************************************************************************
 * All stored procedures regarding the ibs_UserProfile table. <BR>
 *
 * @version     $Revision: 1.11 $, $Date: 2002/12/05 20:05:11 $
 *              $Author: kreimueller $
 *
 * @author      Mario Stegbauer (MS)  980805
 *
  ******************************************************************************
 */


/******************************************************************************
 * Creates a new UserProfile (incl. rights check). <BR>
 * The rights are checked against the root of the system.
 *
 * @input parameters:
 * @param   @userId             ID of the user who is creating the UserProfile.
 * @param   @op                 Operation to be performed (possibly in the
 *                              future used for rights check).
 * @param   @upUserId           ID of the user for whom the UserProfile is
 *                              created.
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
 *  OBJECTNOTFOUND          The UserProfile was not created due to an unknown
 *                          error.
 */
CREATE OR REPLACE FUNCTION p_UserProfile_01$create
(
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_uUserId              INTEGER,
    ai_tVersionId           INTEGER,
    ai_name                 VARCHAR2,
    ai_containerId_s        VARCHAR2,
    ai_containerKind        INTEGER,
    ai_isLink               NUMBER,
    ai_linkedObjectId_s     VARCHAR2,
    ai_description          VARCHAR2,
    ao_oid_s                OUT VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3;

    -- local variables:
    l_containerId             RAW (8);
    l_linkedObjectId          RAW (8);
    l_retValue                INTEGER       := c_ALL_RIGHT;
    l_oid                     RAW (8)       := c_NOOID;
    l_newsTimeLimit           INTEGER       := 5;
    l_newsShowOnlyUnread      NUMBER (1)    := 0;
    l_outboxUseTimeLimit      NUMBER (1)    := 0;
    l_outboxTimeLimit         INTEGER       := 0;
    l_outboxUseTimeFrame      NUMBER (1)    := 0;
    l_outboxTimeFrameFrom     DATE;
    l_outboxTimeFrameTo       DATE;
    l_showExtendedAttributes  NUMBER (1)    := 0;
    l_saveProfile             NUMBER (1)    := 0;
    l_showFilesInWindows      NUMBER (1)    := 0;
    l_lastLogin               DATE;
    l_domainId                INTEGER       := 0;
    l_layoutId                RAW (8)       := c_NOOID;
    l_notificationKind        INTEGER       := 1;
    l_sendSms                 NUMBER (1)    := 0;
    l_addWeblink              NUMBER (1)    := 0;
    l_tabTVersionId           INTEGER;
    l_tabName                 VARCHAR2 (63);
    l_tabDescription          VARCHAR2 (255); 
    l_partOfOid_s             VARCHAR2 (18);
    l_count                   INTEGER        :=0;
BEGIN
    p_stringToByte (ai_containerId_s, l_containerId);
    p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);

    l_domainId := (ai_userId - MOD (ai_userId, 16777216)) / 16777216;

    -- finish previous and begin new transaction:
    COMMIT WORK;

    IF (l_domainId > 0)                   -- possibly existing domain?
    THEN
    BEGIN
        SELECT  oid
        INTO    l_layoutId 
        FROM    ibs_Layout_01 
        WHERE   UPPER (name) = UPPER ('standard')
        AND domainId = l_domainId;

        EXCEPTION
        WHEN OTHERS THEN
            ibs_error.log_error ( ibs_error.error, 'p_UserProfile_01$create.get layout id',
                'OTHER Error for domainId ' || l_domainId || '.');
            RAISE;
    END;
    END IF; -- possibly existing domain

    l_retValue := p_Object$performCreate (
            ai_userId, ai_op, ai_tVersionId,
            ai_name, ai_containerId_s, ai_containerKind,
            ai_isLink, ai_linkedObjectId_s, ai_description,
            ao_oid_s, l_oid);

    IF  (l_retValue = c_ALL_RIGHT) 
    THEN
        BEGIN
            INSERT INTO ibs_UserProfile (oid, userId, newsTimeLimit,
                    newsShowOnlyUnread, outboxUseTimeLimit,
                    outboxTimeLimit, outboxUseTimeFrame,
                    outboxTimeFrameFrom, outboxTimeFrameTo,
                    showExtendedAttributes, showFilesInWindows,
                    lastLogin, layoutId, showRef,
                    showExtendedRights, saveProfile, notificationKind, sendSMS,
                    addWeblink
                    )
            VALUES  (l_oid, ai_uUserId, l_newsTimeLimit,
                    l_newsShowOnlyUnread, l_outboxUseTimeLimit, l_outboxTimeLimit, 
                    l_outboxUseTimeFrame, l_outboxTimeFrameFrom, 
                    l_outboxTimeFrameTo, l_showExtendedAttributes,
                    l_showFilesInWindows, l_lastLogin,
                    l_layoutId,
                    1 - l_showExtendedAttributes, 0, l_saveProfile,
                    l_notificationKind, l_sendSMS,
                    l_addWeblink);
        EXCEPTION     
            WHEN OTHERS THEN     
                ibs_error.log_error ( ibs_error.error,'p_UserProfile_01$create.insert',
                    'Error at INSERT INTO');
                RAISE;
        END;
        IF  (SQL%ROWCOUNT <= 0) 
        THEN
            -- set the return value with the error code:
            l_retValue :=  c_OBJECTNOTFOUND;    -- set return vslue

         END IF; -- set the return value with the error code:
    END IF; -- if object created successfully

    -- finish transaction:
    COMMIT WORK;                        -- make changes permanent

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
    -- rollback to the transaction starting point:
    ROLLBACK;

        ibs_error.log_error (ibs_error.error, 'p_UserProfile_01$create',
            'userId: ' || ai_userId ||
            ', op: ' || ai_op ||
            ', uUserId: ' || ai_uUserId ||
            ', tVersionId: ' || ai_tVersionId ||
            ', name: ' || ai_name ||
            ', containerId_s: ' || ai_containerId_s ||
            ', containerKind: ' || ai_containerKind ||
            ', isLink: ' || ai_isLink ||
            ', linkedObjectId_s: ' || ai_linkedObjectId_s ||
            ', description: ' || ai_description ||
            '; sqlcode: ' || SQLCODE ||
            ', sqlerrm: ' || SQLERRM );
    RETURN c_NOT_OK;
   
END p_UserProfile_01$create;
/

show errors;

/******************************************************************************
 * Changes the attributes of an existing UserProfile. (incl. rights check).<BR>
 *
 * @input parameters:
 * @param   @id                 ID of the object to be changed.
 * @param   @userId             ID of the user who is changing the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 * @param   @name               Name of the object.
 * @param   @validUntil         Date until which the object is valid.
 * @param   @description        Description of the object.
 * @param   @showInNews         the showInNews flag 
 *
 * @param   @newsTimeLimit      Time limit for the newslist
 * @param   @newsShowOnlyUnread Flag to show only unread messages in newslist
 * @param   @outboxUseTimeLimit Flag to use time limit filter in outbox
 * @param   @outboxTimeLimit    Time limit filter for outbox (in days)
 * @param   @outboxUseTimeFrame Flag to use time frame filter in outbox
 * @param   @outboxTimeFrameFrom Begin date of time frame filter in outbox
 * @param   @outboxTimeFrameTo  End date of time frame filter in outbox
 * @param   @showExtendedAttributes Flag to show complete object attributes
 * @param   @showFilesInWindows Flag to show files in a separate window
 * @param   @lastLogin          Date of last login
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_UserProfile_01$change
(
    ai_oid_s                    VARCHAR2,
    ai_userId                   INTEGER,
    ai_op                       INTEGER,
    ai_name                     VARCHAR2,
    ai_validUntil               DATE,
    ai_description              VARCHAR2,
    ai_showInNews               INTEGER,    
    ai_newsTimeLimit            INTEGER,
    ai_newsShowOnlyUnread       NUMBER,
    ai_outboxUseTimeLimit       NUMBER,
    ai_outboxTimeLimit          INTEGER,
    ai_outboxUseTimeFrame       NUMBER,
    ai_outboxTimeFrameFrom      DATE,
    ai_outboxTimeFrameTo        DATE,
    ai_showExtendedAttributes   NUMBER,
    ai_showFilesInWindows       NUMBER,
    ai_lastLogin                DATE,
    ai_layoutId_s               VARCHAR2,
    ai_showExtendedRights       NUMBER,
    ai_saveProfile              NUMBER,
    ai_notificationKind         INTEGER,
    ai_sendsms                  NUMBER,
    ai_addweblink               NUMBER
)
RETURN INTEGER
AS

    -- constants:
    c_NOOID                     CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    c_NOT_OK                    CONSTANT INTEGER := 0;
    c_ALL_RIGHT                 CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS       CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND            CONSTANT INTEGER := 3;
    -- local variables:
    l_retValue                  INTEGER := c_ALL_RIGHT;
    l_oid                       RAW (8) := c_NOOID;
    l_layoutId                  RAW (8) := c_NOOID;

BEGIN
 
    p_stringToByte(ai_layoutId_s, l_layoutId);

    -- finish previous and begin new transaction:
    COMMIT WORK;

    l_retValue := p_Object$performChange (  ai_oid_s, ai_userId, 
                                            ai_op, ai_name, ai_validUntil, 
                                            ai_description, ai_showInNews, l_oid);

    IF  ( l_retValue = c_ALL_RIGHT) THEN
        BEGIN
            UPDATE ibs_UserProfile
            SET newsTimeLimit = ai_newsTimeLimit,
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
                showRef = 1 - ai_showExtendedAttributes,
                showExtendedRights = ai_showExtendedRights,
                saveProfile = ai_saveProfile,
                notificationKind = ai_notificationKind,
                sendsms = ai_sendsms,
                addweblink = ai_addweblink
            WHERE oid = l_oid;
        EXCEPTION
            WHEN OTHERS THEN
               ibs_error.log_error ( ibs_error.error,'p_UserProfile_01$change',
                        'Error in UPDATE - Statement');
            RAISE;
        END;
    END IF;

    -- finish transaction:
    COMMIT WORK;                        -- make changes permanent

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    -- rollback to the transaction starting point:
    ROLLBACK;

    ibs_error.log_error ( ibs_error.error, 'p_UserProfile_01$change',
                          'ai_oid_s: ' || ai_oid_s ||
                          'ai_userId: ' || ai_userId ||
                          'ai_op: ' || ai_op ||
                          'ai_name: ' || ai_name ||
                          'ai_validUntil: ' || ai_validUntil ||
                          'ai_description: ' || ai_description ||
                          'ai_showInNews: ' || ai_showInNews ||                          
                          'ai_newsTimeLimit: ' || ai_newsTimeLimit ||
                          'ai_newsShowOnlyUnread: ' || ai_newsShowOnlyUnread ||
                          'ai_outboxUseTimeLimit: ' || ai_outboxUseTimeLimit ||
                          'ai_outboxTimeLimit: ' || ai_outboxTimeLimit ||
                          'ai_outboxUseTimeFrame: ' || ai_outboxUseTimeFrame ||
                          'ai_outboxTimeFrameFrom: ' || ai_outboxTimeFrameFrom ||
                          'ai_outboxTimeFrameTo: ' || ai_outboxTimeFrameTo ||
                          'ai_showExtendedAttributes: ' || ai_showExtendedAttributes ||
                          'ai_showFilesInWindows: ' || ai_showFilesInWindows ||
                          'ai_lastLogin: ' || ai_lastLogin ||
                          'ai_layoutId_s: ' || ai_layoutId_s ||
                          'ai_showExtendedRights: ' || ai_showExtendedRights ||
                          'ai_saveProfile: ' || ai_saveProfile ||
                          'ai_notificationKind: ' || ai_notificationKind ||
                          'ai_sendSms: ' || ai_sendSms ||
                          'ai_addWeblink: ' || ai_addWeblink ||
                          'sqlcode: ' || SQLCODE ||
                          'sqlerrm: ' || SQLERRM );
    RETURN c_NOT_OK;
END p_UserProfile_01$change;
/

show errors;

/******************************************************************************
 * Gets all data from a given UserProfile.(incl. rights check) <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be retrieved.
 * @param   @userId             Id of the user who is getting the data.
 * @param   @op                 Operation to be performed (used for rights
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
 * @param   @showInNews         the showInNews flag
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
 * @param   @newsTimeLimit       time limit for the newslist
 * @param   @newsShowOnlyUnread  flag to show only unread messages in newslist
 * @param   @outboxUseTimeLimit  flag to use time limit filter in outbox
 * @param   @outboxTimeLimit     time limit filter for outbox (in days)
 * @param   @outboxUseTimeFrame  flag to use time frame filter in outbox
 * @param   @outboxTimeFrameFrom begin date of time frame filter in outbox
 * @param   @outboxTimeFrameTo   end date of time frame filter in outbox
 * @param   @showExtendedAttributes  flag to show complete object attributes
 * @param   @showFilesInWindows      flag to show files in a separate window
 * @param   @lastLogin               date of last login
 * @param   @m2AbsBasePath           absolute m2 base path - workaround!
 * @param   @home                the web m2 path
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_UserProfile_01$retrieve(
    -- input parameters
    ai_oid_s                    VARCHAR2,
    ai_userId                   NUMBER,
    ai_op                       NUMBER,
    -- output parameters
    ao_state                    OUT NUMBER,
    ao_tVersionId               OUT NUMBER,
    ao_typeName                 OUT VARCHAR2,
    ao_name                     OUT VARCHAR2,
    ao_containerId              OUT RAW,
    ao_containerName            OUT VARCHAR2,
    ao_containerKind            OUT NUMBER,
    ao_isLink                   OUT NUMBER,
    ao_linkedObjectId           OUT RAW,
    ao_owner                    OUT NUMBER,
    ao_ownerName                OUT VARCHAR2,
    ao_creationDate             OUT DATE,
    ao_creator                  OUT NUMBER,
    ao_creatorName              OUT VARCHAR2,
    ao_lastChanged              OUT DATE,
    ao_changer                  OUT NUMBER,
    ao_changerName              OUT VARCHAR2,
    ao_validUntil               OUT DATE,
    ao_description              OUT VARCHAR2,
    ao_showInNews               OUT INTEGER,    
    ao_checkedOut               OUT NUMBER,
    ao_checkOutDate             OUT DATE,
    ao_checkOutUser             OUT INTEGER,
    ao_checkOutUserOid          OUT RAW,
    ao_checkOutUserName         OUT VARCHAR2,
    ao_newsTimeLimit            OUT NUMBER,
    ao_newsShowOnlyUnread       OUT NUMBER,
    ao_outboxUseTimeLimit       OUT NUMBER,
    ao_outboxTimeLimit          OUT NUMBER,
    ao_outboxUseTimeFrame       OUT NUMBER,
    ao_outboxTimeFrameFrom      OUT DATE,
    ao_outboxTimeFrameTo        OUT DATE,
    ao_showExtendedAttributes   OUT NUMBER,
    ao_showFilesInWindows       OUT NUMBER,
    ao_lastLogin                OUT DATE,
    ao_m2AbsBasePath            OUT VARCHAR2,
    ao_home                     OUT VARCHAR2,
    ao_layoutId                 OUT RAW,
    ao_layoutName               OUT VARCHAR2,
    ao_showRef                  OUT NUMBER,
    ao_showExtendedRights       OUT NUMBER,
    ao_saveProfile              OUT NUMBER,
    ao_notificationKind         OUT INTEGER,
    ao_sendsms                  OUT NUMBER,
    ao_addweblink               OUT NUMBER
)
RETURN INTEGER
AS
    -- constants:
    c_NOOID                     CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    c_NOT_OK                    CONSTANT INTEGER := 0;
    c_ALL_RIGHT                 CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS       CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND            CONSTANT INTEGER := 3;
    -- local variables:
    l_retValue                  INTEGER := c_ALL_RIGHT;
    l_oid                       RAW (8) := c_NOOID;

BEGIN
    -- finish previous and begin new transaction:
    COMMIT WORK;

    l_retValue := p_Object$performRetrieve(ai_oid_s, ai_userId, ai_op,
                    ao_state, ao_tVersionId, ao_typeName, ao_name, ao_containerId,
                    ao_containerName, ao_containerKind, ao_isLink, ao_linkedObjectId,
                    ao_owner, ao_ownerName, ao_creationDate, ao_creator, ao_creatorName,
                    ao_lastChanged, ao_changer, ao_changerName, ao_validUntil,
                    ao_description, ao_showInNews, 
                    ao_checkedOut, ao_checkOutDate, 
                    ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName,
                    l_oid);

    IF  ( l_retValue = c_ALL_RIGHT) THEN
        BEGIN
            SELECT newsTimeLimit,  newsShowOnlyUnread,  outboxUseTimeLimit,  outboxTimeLimit,
                   outboxUseTimeFrame,  outboxTimeFrameFrom,  outboxTimeFrameTo,  showExtendedAttributes,
                   showFilesInWindows,  lastLogin,  layoutId, 
                   showRef, showExtendedRights, saveProfile, notificationKind, sendsms,addWeblink
            INTO   ao_newsTimeLimit, ao_newsShowOnlyUnread, ao_outboxUseTimeLimit, ao_outboxTimeLimit,
                   ao_outboxUseTimeFrame, ao_outboxTimeFrameFrom, ao_outboxTimeFrameTo, ao_showExtendedAttributes,
                   ao_showFilesInWindows, ao_lastLogin, ao_layoutId, ao_showRef, ao_showExtendedRights, 
                   ao_saveProfile, ao_notificationKind, ao_sendsms, ao_addweblink
            FROM   ibs_UserProfile 
            WHERE  oid = l_oid;

        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error, 'p_UserProfile_01$retrieve',
                    'Error in get userprofiledata');
        RAISE;
        END;

        BEGIN
            SELECT  name
            INTO    ao_layoutName 
            FROM    ibs_Layout_01 
            WHERE   oid = ao_layoutId;
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error, 'p_UserProfile_01$retrieve',
                    'Error in get layoutname');
            RAISE;
        END;

        BEGIN
            SELECT  value
            INTO    ao_m2AbsBasePath 
            FROM    ibs_System 
            WHERE   name = 'ABS_BASE_PATH';

        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error, 'p_UserProfile_01$retrieve',
                    'Error in get basepath');
             RAISE;
        END;

        BEGIN
            SELECT  value
            INTO    ao_home 
            FROM    ibs_System 
            WHERE   name = 'WWW_BASE_PATH';

        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error, 'p_UserProfile_01$retrieve',
                    'Error in get www_base_path');
            RAISE;
        END;
    END IF;

    -- finish transaction:
    COMMIT WORK;                        -- make changes permanent

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    -- rollback to the transaction starting point:
    ROLLBACK;

    ibs_error.log_error ( ibs_error.error, 'p_UserProfile_01$retrieve',
                          'oid_s: ' || ai_oid_s ||
                          'userId: ' || ai_userId ||
                          'op: ' || ai_op ||
                          'sqlerrm: ' || SQLERRM ||
                          'sqlcode: ' || SQLCODE );
    RETURN c_NOT_OK;
END p_UserProfile_01$retrieve;
/

show errors;

/******************************************************************************
 * Deletes an object and all its values (incl. rights check). <BR>
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
CREATE OR REPLACE FUNCTION p_UserProfile_01$delete
(
    ai_oid_s        VARCHAR2,
    ai_userId       NUMBER,
    ai_op           NUMBER 
)
RETURN INTEGER
AS
    -- constants:
    c_NOOID                     CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    c_NOT_OK                    CONSTANT INTEGER := 0;
    c_ALL_RIGHT                 CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS       CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND            CONSTANT INTEGER := 3;
    -- local variables:
    l_retValue                  INTEGER := c_ALL_RIGHT;
    l_oid                       RAW (8) := c_NOOID;

BEGIN
    p_stringToByte(ai_oid_s, l_oid);

    -- finish previous and begin new transaction:
    COMMIT WORK;

    l_retValue := p_Object$performDelete
        (ai_oid_s, ai_userId, ai_op, l_oid);


/* KR should not be done because of undo functionality!
    IF (l_retValue = c_ALL_RIGHT)       -- operation properly performed?
    THEN
        BEGIN
            -- delete object specific data:
            -- (deletes all type specific tuples which are not within ibs_Object)
            DELETE  ibs_UserProfile
            WHERE   oid = l_oid;
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error (ibs_error.error, 'p_UserProfile_01$delete',
                      'Error in DELETE oid = ' || l_oid);
            RAISE;
        END;
    END IF; -- if operation properly performed
*/

    -- finish transaction:
    COMMIT WORK;                        -- make changes permanent

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
    -- rollback to the transaction starting point:
    ROLLBACK;

    ibs_error.log_error ( ibs_error.error, 'p_UserProfile_01$delete',
                          'oid_s: ' || ai_oid_s ||
                          'userId: ' || ai_userId ||
                          'op: ' || ai_op ||
                          'sqlcode: ' || SQLCODE ||
                          'sqlerrm: ' || SQLERRM );
    RETURN c_NOT_OK;
END p_UserProfile_01$delete;
/

show errors;

exit;
