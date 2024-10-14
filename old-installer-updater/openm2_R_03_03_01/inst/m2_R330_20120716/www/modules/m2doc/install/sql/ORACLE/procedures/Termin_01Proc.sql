/******************************************************************************
 * All stored procedures regarding the Termin_01 object. <BR>
 *
 * @version     $Id: Termin_01Proc.sql,v 1.9 2003/10/31 00:13:15 klaus Exp $
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
CREATE OR REPLACE FUNCTION p_Termin_01$create
(
    ai_userId           INTEGER,
    ai_op               INTEGER,
    ai_tVersionId       INTEGER,
    ai_name             VARCHAR2,
    ai_containerId_s    VARCHAR2,
    ai_containerKind    INTEGER,
    ai_isLink           NUMBER,
    ai_linkedObjectId_s VARCHAR2,
    ai_description      VARCHAR2,
    ao_oid_s            OUT VARCHAR2
)
RETURN INTEGER
AS
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2;
   -- define local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_oid                   RAW (8);
    l_containerId           RAW (8);
    l_linkedObjectId        RAW (8);
    l_tabTVersionId         INTEGER;
    l_tabName               VARCHAR2 (63);
    l_tabDescription        VARCHAR2 (255); 
    l_partOfOid_s           VARCHAR2 (18);
    l_rowCount              INTEGER := 0;


BEGIN
    p_stringToByte(ai_containerId_s, l_containerId);
    p_stringToByte(ai_linkedObjectId_s, l_linkedObjectId);

    l_retValue:=p_Object$performCreate (ai_userId,
     ai_op, ai_tVersionId, ai_name, ai_containerId_s,
     ai_containerKind, ai_isLink, ai_linkedObjectId_s,
     ai_description, ao_oid_s, l_oid);

    IF (l_retValue = c_ALL_RIGHT) 
    THEN
        BEGIN
            INSERT INTO m2_Termin_01 (oid, startDate, endDate, place, participants, maxNumParticipants, showParticipants, deadline, attachments)
            VALUES (l_oid, SYSDATE, SYSDATE, ' ', 0, 0,0,NULL, 0);     

                -- check if insertion was performed properly:
                IF (SQL%ROWCOUNT <= 0)        -- no row affected?
                THEN
                    l_retValue := c_NOT_OK; -- set return value
                end if; -- if no row affacted 

        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error, 'p_Termin_01$create',
                                      'Error in INSERT INTO' );
                RAISE;
        END;
    END IF;  -- if object create successfully
COMMIT WORK;
    RETURN l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Termin_01$create',
    ', userId = ' || ai_userId  ||
    ', op = ' || ai_op  ||
    ', tVersionId = ' || ai_tVersionId ||
    ', name = ' || ai_name ||
    ', containerId_s = ' || ai_containerId_s ||
    ', containerKind = ' || ai_containerKind ||
    ', isLink = ' || ai_isLink ||
    ', linkedObjectId_s = ' || ai_linkedObjectId_s ||
    ', description = ' || ai_description ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);
        -- return error code:
        RETURN c_NOT_OK;
END p_Termin_01$create;
/

show errors;

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
 * @param   @showInNews         Show in News flag.
 *
 * @param   @startDate          begin date and time of term
 * @param   @endDate            end date and time of term
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
CREATE OR REPLACE FUNCTION p_Termin_01$change
(
    -- input parameters:
    ai_oid_s               VARCHAR2,     -- objects id as a string
    ai_userId              INTEGER,      -- users id
    ai_op                  INTEGER,      -- operation
    ai_name                VARCHAR2,     -- name of object
    ai_validUntil          DATE,         -- valid in system
    ai_description         VARCHAR2,  -- textual description
    ai_showInNews          INTEGER,      -- show in news flag
    ---- object specific attributes:
    ai_startDate           DATE,         -- begin date and time of term
    ai_endDate             DATE,         -- end date and time of term
    ai_place                VARCHAR2,  -- place where term happens
    ai_participants        NUMBER,       -- does term have participants
    ai_maxNumParticipants  INTEGER,      -- max. number of participants
    ai_showParticipants    NUMBER,       -- view participants allowed?
    ai_deadline            DATE,         -- last announcements
    ai_attachments         NUMBER        -- may term have attachments
)
RETURN INTEGER
AS
    -- conversions
    l_oid                 RAW (8);
    -- participants container id - string
    l_partContId          RAW (8);
    l_partContId_s        VARCHAR2 (18);
    l_rights              INTEGER; 
    l_grpAll              INTEGER; 
    l_domainId            INTEGER;  
    l_owner               INTEGER; -- HP
    -- define return constants
    c_NOT_OK              CONSTANT INTEGER := 0;
    c_ALL_RIGHT           CONSTANT INTEGER := 1;
    -- define return values
    l_retValue            INTEGER := c_NOT_OK;
    -- local varibles
    c_NOOID               RAW (8) := '0000000000000000';
    l_containerName       VARCHAR2 (63) := 'Teilnehmer';
    l_participantsLocal       NUMBER (1);
    l_maxNumParticipantsLocal INTEGER;
    l_showParticipantsLocal   NUMBER (1);
    l_deadlineLocal           DATE;

BEGIN
    p_stringToByte (ai_oid_s, l_oid);

    -- get values from input parameters
    l_participantsLocal       := ai_participants;
    l_maxNumParticipantsLocal := ai_maxNumParticipants;
    l_showParticipantsLocal   := ai_showParticipants;
    l_deadlineLocal           := ai_deadline;


        -- perform the change of the object:
    l_retValue := p_Object$performChange (ai_oid_s, 
                ai_userId, ai_op, ai_name,
                ai_validUntil, ai_description, ai_showInNews, l_oid);

        IF (l_retValue = c_ALL_RIGHT)     -- operation properly performed?
        THEN
            -- get participant container for term
            -- oid of term is stored as containerId in participant container
            -- only 1 participant container for each term is possible
            BEGIN
                SELECT  oid
                INTO    l_partContId
                FROM    ibs_Object
                WHERE   containerId = l_oid
                    AND TVersionId =  16850945;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    l_partContId := NULL; -- valid exception
                WHEN OTHERS THEN
                  ibs_error.log_error ( ibs_error.error, 'p_Termin_01$change',
                    'Error in SQL Statement 1');
                raise;
            END;

            -- participant container needed?
            IF (l_participantsLocal <> 0)
            THEN
                -- yes,  participant container needed!

                -- get group-id of group "all users" in current domain 
                /*
                -- divide by 0x1000000 to get users current domainId
                l_domainId := ai_userId /  16777216;
                */
                BEGIN
                    SELECT domainId
                    INTO   l_domainId
                    FROM   ibs_User
                    WHERE  id = ai_userid;

                    SELECT allGroupId
                    INTO   l_grpAll
                    FROM   ibs_Domain_01
                    WHERE  id = l_domainId;
                EXCEPTION
                   WHEN OTHERS THEN
                      ibs_error.log_error ( ibs_error.error, 'p_Termin_01$change',
                        'Error in SQL Statement 2');
                    raise;
                END;

                -- check if part-cont already exists
                IF l_partContId IS NULL
                THEN
                    -- create a new participant container object
                    l_retValue := p_Object$create (
                        ai_userId, ai_op, 
                        16850945,         -- typeVersion:ParticipantContainer
                        l_containerName,
                        ai_oid_s,             -- its container is the term
                        2,                  -- container kind: part of
                        0,
                        '0', 
                        '', 
                        l_partContId_s);           
                    -- convert
                    p_stringToByte ( l_partContId_s, l_partContId);
                END IF;-- if partContId IS NULL

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

                -- is flag 'showParticipants' set?
                IF (l_showParticipantsLocal <> 0)   -- flag 'showParticipants' set 
                THEN
                    -- set rights-value for SOME operations (viewElems, 
                    -- read, view, new, delElem) for ALL users.
                    -- will recursively be set for all participants in container
                    BEGIN
                        SELECT  SUM (id)
                        INTO    l_rights
                        FROM    ibs_Operation
                        WHERE   name = 'viewElems'
                            OR  name = 'read'
                            OR  name =  'view'
                            OR  name = 'new'
                            OR  name = 'addElem'
                            OR  name = 'delElem';
                        EXCEPTION
                            WHEN OTHERS THEN
                              ibs_error.log_error ( ibs_error.error, 'p_Termin_01$change',
                                'Error in SQL Statement 3');
                            raise;
                    END;
                ELSE    -- flag 'showParticipants' not set
                    -- set rights-value for only a FEW operations (viewElems, 
                    -- read, view, new, delElem) for ALL users
                    -- will recursively be set for all participants in container
                    BEGIN
                        SELECT  SUM (id)
                        INTO    l_rights
                        FROM    ibs_Operation
                        WHERE   name = 'viewElems'
                            OR  name = 'read'
                            OR  name = 'new'
                            OR  name = 'addElem'
                            OR  name = 'delElem';
                    EXCEPTION
                        WHEN OTHERS THEN
                          ibs_error.log_error ( ibs_error.error, 'p_Termin_01$change',
                            'Error in SQL Statement 4');
                        raise;
                    END;
                END IF;

                -- add rights 
                p_Rights$addRights (l_partContId, l_grpAll,  l_rights, 1);

                -- reset rights for owner; they are always the same: ALL rights!
                -- get rights
                BEGIN
                    SELECT  SUM (id)
                    INTO    l_rights
                    FROM    ibs_Operation;
                EXCEPTION
                    WHEN OTHERS THEN
                          ibs_error.log_error ( ibs_error.error, 'p_Termin_01$change',
                            'Error in SQL Statement 5');
                        raise;
                END;                

                -- get owner
                BEGIN
                    SELECT  owner
                    INTO    l_owner
                    FROM    ibs_Object
                    WHERE   oid = l_partContId;
                EXCEPTION
                    WHEN OTHERS THEN
                          ibs_error.log_error ( ibs_error.error, 'p_Termin_01$change',
                            'Error in SQL Statement 6');
                        raise;
                END;    

                -- reset all rights for owner (recursively)
                p_Rights$addRights (l_partContId, l_owner,  l_rights, 1);

            ELSIF l_partContId IS NOT NULL   -- delete unnecessary participants container
            THEN
                -- delete container object and content
                DELETE ibs_Object 
                WHERE containerId = l_partContId;
                
                DELETE ibs_Object 
                WHERE oid = l_partContId;
                
                -- reset values
                l_partContId := NULL;
                l_participantsLocal := 0;
                l_maxNumParticipantsLocal := 0;
                l_showParticipantsLocal := 0;
            END IF;-- if

            -- check if deadline is set
            IF l_deadlineLocal IS NULL
            THEN
                l_deadlineLocal := ai_startDate;
            END IF;

            -- update object specific values in table Termin_01
            BEGIN
                UPDATE  m2_Termin_01 
                SET     startDate = ai_startDate,
                        endDate = ai_endDate,
                        place = ai_place,
                        participants = l_participantsLocal,
                        maxNumParticipants = l_maxNumParticipantsLocal,
                        showParticipants = l_showParticipantsLocal,
                        deadline = l_deadlineLocal,
                        attachments = ai_attachments
                WHERE   oid = l_oid;
                EXCEPTION
                    WHEN OTHERS THEN
                    ibs_error.log_error ( ibs_error.error, 'p_Termin_01$change',
                        'Error in SQL - UPDATE Statement 1');
                    raise;
                END;

        END IF;-- if operation properly performed

COMMIT WORK;
    -- return the state value
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
      ibs_error.log_error ( ibs_error.error, 'p_Termin_01$change',                   
      ', oid_s = ' || ai_oid_s ||  
      ', userId = ' || ai_userId  || 
      ', op = ' || ai_op || 
      ', name = ' || ai_name || 
      ', validUntil = ' || ai_validUntil || 
      ', description = ' || ai_description || 
      ', startDate = ' || ai_startDate || 
      ', endDate = ' || ai_endDate || 
      ', place = ' || ai_place || 
      ', participants = ' || ai_participants || 
      ', maxNumParticipants = ' || ai_maxNumParticipants || 
      ', showParticipants = ' || ai_showParticipants || 
      ', deadline = ' || ai_deadline ||
      ', attachments = ' || ai_attachments ||
      ', errorcode = ' || SQLCODE ||
      ', errormessage = ' || SQLERRM);
        -- return error code:
        RETURN c_NOT_OK;
END p_Termin_01$change;
/

show errors;

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
 * @param   ao_showInNews         flag if object should be shown in newscontainer
 * @param   ao_checkedOut         Is the object checked out?
 * @param   ao_checkOutDate       Date when the object was checked out
 * @param   ao_checkOutUser       id of the user which checked out the object
 * @param   ao_checkOutUserOid    Oid of the user which checked out the object
 *                              is only set if this user has the right to READ
 *                              the checkOut user
 * @param   ao_checkOutUserName   name of the user which checked out the object,
 *                              is only set if this user has the right to view
 *                              the checkOut-User
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
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Termin_01$retrieve
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    -- common output parameters
    ao_state                OUT INTEGER,
    ao_tVersionId           OUT INTEGER,
    ao_typeName             OUT VARCHAR2,
    ao_name                 OUT VARCHAR2,
    ao_containerId          OUT RAW,
    ao_containerName        OUT VARCHAR2,
    ao_containerKind        OUT INTEGER,
    ao_isLink               OUT NUMBER,
    ao_linkedObjectId       OUT RAW,
    ao_owner                OUT INTEGER,
    ao_ownerName            OUT VARCHAR2,
    ao_creationDate         OUT DATE,
    ao_creator              OUT INTEGER,
    ao_creatorName          OUT VARCHAR2,
    ao_lastChanged          OUT DATE,
    ao_changer              OUT INTEGER,
    ao_changerName          OUT VARCHAR2,
    ao_validUntil           OUT DATE,
    ao_description          OUT VARCHAR2,
    ao_showInNews           OUT INTEGER,
    ao_checkedOut           OUT NUMBER,
    ao_checkOutDate         OUT DATE,
    ao_checkOutUser         OUT INTEGER,
    ao_checkOutUserOid      OUT RAW,
    ao_checkOutUserName     OUT VARCHAR2,

    -- type-specific output parameters
    ao_startDateTime        OUT DATE,
    ao_endDateTime          OUT DATE,
    ao_place                OUT VARCHAR2,
    ao_participants         OUT NUMBER,
    ao_maxNumParticipants   OUT INTEGER,
    ao_showParticipants     OUT NUMBER,
    ao_curNumParticipants   OUT INTEGER,
    ao_partContId           OUT RAW,
    ao_deadline             OUT DATE,
    ao_attachments          OUT NUMBER
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;

    -- local variables:
    l_retValue              INTEGER := c_NOT_OK; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description    
    l_eText                 VARCHAR2 (5000); -- full error text
    l_oid                   RAW (8);        -- oid of the actual object
    
BEGIN
    l_retValue := p_Object$performRetrieve (
        ai_oid_s, ai_userId, ai_op,
        ao_state, ao_tVersionId, ao_typeName,ao_name,
        ao_containerId, ao_containerName, ao_containerKind,
        ao_isLink, ao_linkedObjectId, ao_owner, 
        ao_ownerName, ao_creationDate, ao_creator, 
        ao_creatorName, ao_lastChanged, ao_changer , 
        ao_changerName, ao_validUntil, ao_description, ao_showInNews,
        ao_checkedOut, ao_checkOutDate, 
        ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName,
        l_oid);

    IF (l_retValue = c_ALL_RIGHT)       -- operation properly performed?
    THEN
        -- get type-specific values out of table Termin_01:
        BEGIN
            SELECT  startDate, endDate,
                    place, participants,
                    showParticipants, maxNumParticipants,
                    deadline, attachments
            INTO    ao_startDateTime, ao_endDateTime,
                    ao_place, ao_participants,
                    ao_showParticipants, ao_maxNumParticipants,
                    ao_deadline, ao_attachments
            FROM    m2_Termin_01
            WHERE   oid = l_oid;
        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'get type_specific data';
                RAISE;                  -- call common exception handler
        END;

        -- get participant container for term
        -- oid of term is stored as containerId in participant container
        -- only 1 participant container for each term is possible
        l_retValue := p_Object$getTabOid (l_oid, 'Participants', ao_partContId);
            
        -- get current number of participants:
        IF (l_retValue = c_ALL_RIGHT AND ao_participants <> 0)
                                        -- term with participants?
        THEN
            -- count number of participants for term:
            BEGIN
                SELECT COUNT (oid)
                INTO    ao_curNumParticipants 
                FROM    ibs_Object
                WHERE   containerId = ao_partContId
                    AND tVersionId = 16854529 -- TV_Participant
                    AND state = 2;
            EXCEPTION
                WHEN OTHERS THEN        -- any error
                    -- create error entry:
                    l_ePos := 'count participants';
                    RAISE;              -- call common exception handler
            END;

        ELSE                            -- no participants allowed
            -- reset return value and the number of participants:
            l_retValue := c_ALL_RIGHT;
            ao_curNumParticipants := 0;
        END IF; -- else no participants allowed
    END IF;-- if operation properly performed

    COMMIT WORK;

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the beginning of the transaction:
        ROLLBACK;
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid_s = ' || ai_oid_s ||  
            ', ai_userId = ' || ai_userId  || 
            ', ai_op = ' || ai_op || 
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Termin_01$retriev', l_eText);
        -- set new transaction starting point:
        COMMIT WORK;
        -- return error code:
        RETURN c_NOT_OK;
END p_Termin_01$retrieve;
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
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */


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
CREATE OR REPLACE PROCEDURE p_Termin_01$checkOverlap
(
    -- input parameters:
    oid_s       VARCHAR2,       -- oid of given term
    userId        INTEGER,        -- users id
    startDate   DATE,           -- beginning Date
    endDate     DATE,           -- end Date
    -- output parameter
    found       OUT  INTEGER    -- number of found overlapping terms
)
AS
    -- error messages
    StoO_error 	        INTEGER;
    StoO_errmsg	        VARCHAR2(255);
    -- conversions
    oid         RAW (8);
    -- rights
    RIGHT_VIEW  INTEGER := 2;

BEGIN

    p_stringToByte (p_Termin_01$checkOverlap.oid_s, p_Termin_01$checkOverlap.oid);

    -- get numberof  overlapping terms for given current term
    SELECT DISTINCT COUNT(*)
    INTO p_Termin_01$checkOverlap.found
    FROM m2_Termin_01 t,
         ibs_Object o,
         v_Container$rights c
    WHERE t.oid <> p_Termin_01$checkOverlap.oid
    -- check overlapping terms
    AND (p_Termin_01$checkOverlap.startDate BETWEEN t.startDate AND t.endDate
         OR p_Termin_01$checkOverlap.endDate BETWEEN t.startDate AND t.endDate
         OR (p_Termin_01$checkOverlap.startDate <= t.startDate 
         AND p_Termin_01$checkOverlap.endDate >= t.endDate))
    -- join m2_termin_01
    AND t.oid = o.oid 
    -- check if user has right to view overlapping terms
    AND c.oid = o.oid
    AND B_AND(c.rights, RIGHT_VIEW) = RIGHT_VIEW
    AND c.userId = p_Termin_01$checkOverlap.userId;

    COMMIT WORK;
    
EXCEPTION
  WHEN OTHERS THEN
    StoO_error := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_Termin_01$checkOverlap',            
    ', oid_s = ' || oid_s ||  
    ', userId = ' || userId  || 
    ', startDate = ' || startDate || 
    ', endDate = ' || endDate ||     
    ', errorcode = ' || StoO_error ||
    ', errormessage = ' || StoO_errmsg);              
END p_Termin_01$checkOverlap;
/

show errors;

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
CREATE OR REPLACE FUNCTION p_Termin_01$BOCopy
(
  oid     RAW,
  userId     INTEGER ,
  newOid     RAW 
)
RETURN INTEGER
AS
StoO_selcnt    INTEGER;
StoO_error     INTEGER;
StoO_rowcnt    INTEGER;
StoO_errmsg    VARCHAR2(255);
StoO_sqlstatus    INTEGER;
NOT_OK         NUMBER(10,0);
ALL_RIGHT     NUMBER(10,0);
retValue     NUMBER(10,0);

BEGIN
    p_Termin_01$BOCopy.NOT_OK :=  0;
    p_Termin_01$BOCopy.ALL_RIGHT :=  1;

    p_Termin_01$BOCopy.retValue :=  p_Termin_01$BOCopy.NOT_OK;
    BEGIN
    StoO_error   := 0;
    StoO_rowcnt  := 0;
    INSERT INTO m2_Termin_01 (oid, startDate, endDate, place, participants, maxNumParticipants, showParticipants, deadline, attachments)SELECT  p_Termin_01$BOCopy.newOid, startDate, endDate, place, 
           participants, maxNumParticipants, showParticipants, deadline, 
           attachments
         FROM m2_Termin_01 
        WHERE oid = p_Termin_01$BOCopy.oid;
    StoO_rowcnt := SQL%ROWCOUNT;
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error  := SQLCODE;
            StoO_errmsg := SQLERRM;
    END;

    IF  ( StoO_rowcnt >= 1) THEN
        p_Termin_01$BOCopy.retValue :=  p_Termin_01$BOCopy.ALL_RIGHT;
    END IF;
    RETURN p_Termin_01$BOCopy.retValue;
    RETURN 0;

EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, ' p_Termin_01$BOCopy',
    ', userId = ' || userId  ||
    ', newOid = ' || newOid ||
    ', errorcode = ' || StoO_error ||
    ', errormessage = ' || StoO_errmsg);
        -- return error code:
        RETURN NOT_OK;
END p_Termin_01$BOCopy;
/

show errors;

EXIT;