--------------------------------------------------------------------------------
-- All stored procedures regarding the Termin_01 object. <BR>
--
-- @version     $Id: Termin_01Proc.sql,v 1.4 2003/10/31 00:12:52 klaus Exp $
--
-- author      Marcel Samek (MS)  020910
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
 -- Gets all data from a given object (incl. rights check). <BR>
 --
 -- @input parameters:
 -- @param   @oid_s              ID of the object to be retrieved.
 -- @param   @userId             Id of the user who is getting the data.
 -- @param   @op                 Operation to be performed (used for rights
 --                              check).
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
 -- @param   @showInNews         Display object in the news.
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
 -- @param   @startDateTime      start of term/date
 -- @param   @endDateTime        end of term/date
 -- @param   @place              where does term take place
 -- @param   @participants       term with participants?
 -- @param   @maxNumParticipants max. number of participants
 -- @param   @showParticipants   participants viewable?
 -- @param   @curNumParticipants current num. of participants
 -- @param   @partContId         participants container
 -- @param   @deadline           last announcements
 -- @param   @showInNews         show in news?
 -- @returns A value representing the state of the procedure.
 --  ALL_RIGHT               Action performed, values returned, everything ok.
 --  INSUFFICIENT_RIGHTS     User has no right to perform action.
 --  OBJECTNOTFOUND          The required object was not found within the
 --                          database.
 --
    -- delete procedure
CALL IBSDEV1.p_dropProc ('p_Termin_01$retrieve');
    -- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Termin_01$retrieve
(
    -- input parameters:
    IN ai_oid_s             VARCHAR (18),
    IN ai_userId            INT,
    IN ai_op                INT,
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
    -- common output parameters:
    OUT ao_checkOutUserName VARCHAR (63),
    -- object-type specific output parameters:
    OUT ao_startDateTime    TIMESTAMP,
    OUT ao_endDateTime      TIMESTAMP,
    OUT ao_place            VARCHAR (255),
    OUT ao_participants     SMALLINT,
    OUT ao_maxNumParticipants   INT,
    OUT ao_showParticipants SMALLINT,
    OUT ao_curNumParticipants   INT,
    OUT ao_partContId       CHAR (8) FOR BIT DATA,
    OUT ao_deadline         TIMESTAMP,
    OUT ao_attachments      SMALLINT)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE         INT;
    -- constants:
    DECLARE c_NOT_OK        INT;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT;  -- everything was o.k.
    -- local variables:
    DECLARE l_retValue      INT;  -- return value of function
    DECLARE l_ePos          VARCHAR (255);  -- error position description
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- oid of the actual object
    -- assign constants:
    SET c_NOT_OK = 0;
    SET c_ALL_RIGHT = 1;

    -- initialize local variables:
    SET l_retValue = c_ALL_RIGHT;

-- body:
    CALL IBSDEV1.p_Object_performRetrieve(ai_oid_s, ai_userId, ai_op,
        ao_state, ao_tVersionId, ao_typeName, ao_name, ao_containerId,
        ao_containerName, ao_containerKind, ao_isLink, ao_linkedObjectId,
        ao_owner, ao_ownerName, ao_creationDate, ao_creator, ao_creatorName,
        ao_lastChanged, ao_changer, ao_changerName, ao_validUntil,
        ao_description, ao_showInNews, ao_checkedOut, ao_checkOutDate,
        ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    -- operation properly performed?
    IF l_retValue = c_ALL_RIGHT THEN
        -- get type-specific values out of table Termin_01:
        SET l_sqlcode = 0;

        SELECT startDate, endDate, place, participants,
            showParticipants, maxNumParticipants,
            deadline, attachments
        INTO ao_startDateTime, ao_endDateTime, ao_place, ao_participants,
            ao_showParticipants, ao_maxNumParticipants, ao_deadline,
            ao_attachments
        FROM IBSDEV1.m2_Termin_01
        WHERE oid = l_oid;

        -- check if there occurred an error:
        IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
            SET l_ePos = 'get type-specific data';
            GOTO exception1;
        END IF;

        -- CALL IBSDEV1.common exception1 handler
        -- get participant container for term
        -- oid of term is stored as containerId in participant container
        -- only 1 participant container for each term is possible
        CALL IBSDEV1.p_Object_getTabOid(l_oid, 'Participants', ao_partContId);
        GET DIAGNOSTICS l_retValue = RETURN_STATUS;

        -- get current number of participants:
        IF l_retValue = c_ALL_RIGHT AND ao_participants <> 0 THEN
            -- count number of participants for term:
            SET l_sqlcode = 0;

            SELECT COUNT (oid)
            INTO ao_curNumParticipants
            FROM IBSDEV1.ibs_Object
            WHERE containerId = ao_partContId
                AND tVersionId = 16854529 -- TV_Participant
                AND state = 2;

            -- check if there occurred an error:
            IF l_sqlcode <> 0 AND l_sqlcode <> 100 then
                SET l_ePos = 'count participants';
                GOTO exception1;
            END IF;
        ELSE
            -- reset return value and the number of participants:
            SET l_retValue = c_ALL_RIGHT;
            SET ao_curNumParticipants = 0;
        END IF;
    END IF;

    -- if operation properly performed
    COMMIT;

    -- return the state value:
    RETURN l_retValue;
    exception1:

    -- an error occurred
    -- roll back to the beginning of the transaction:
    ROLLBACK;

    -- undo changes
    -- log the error:
    CALL IBSDEV1.ibs_erro.logError (500, 'p_Termin_01$retrieve', l_sqlcode, l_ePos,
        'ai_userId', ai_userId, 'ai_oid_s', ai_oid_s, 'ai_op', ai_op, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0, '', '', '', 0
        , '', '', '', 0, '', '', '', 0, '', '');

    -- return error code:
    RETURN c_NOT_OK;
END;
-- p_Termin_01$retrieve


--------------------------------------------------------------------------------
 -- Deletes an object and all its values (incl. rights check). <BR>
 -- This procedure also delets all links showing to this object.
 --
 -- @input parameters:
 -- @param   @oid_s              ID of the object to be deleted.
 -- @param   @userId             ID of the user who is deleting the object.
 -- @param   @op                 Operation to be performed (used for rights
 --                              check).
 -- @output parameters:
 -- @returns A value representing the state of the procedure.
 --  ALL_RIGHT               Action performed, values returned, everything ok.
 --  INSUFFICIENT_RIGHTS     User has no right to perform action.
 --  OBJECTNOTFOUND          The required object was not found within the
 --                          database.
 --
    -- delete procedure
CALL IBSDEV1.p_dropProc ('p_Termin_01$delete');
    -- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Termin_01$delete
(
    IN ai_oid_s             VARCHAR (18),    -- objects id as string
    IN ai_userId            INT,            -- users id
    IN ai_op                INT             -- operation
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue     INT;
    DECLARE l_partContId    CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- set constants:
    SET c_ALL_RIGHT = 1;

    -- initialize local variables and return values:
    SET l_retValue = c_ALL_RIGHT;

-- body:
    ---------------------------------------------------------------------------
    -- conversionS (OBJECTIDSTRING) - all input objectids must be converted
    ---------------------------------------------------------------------------
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

    ---------------------------------------------------------------------------
    -- START

    -- perform deletion of object:
    CALL IBSDEV1.p_Object_performDelete (ai_oid_s, ai_userId, ai_op, c_NOOID);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    -- operation properly performed?
    IF (l_retValue = c_ALL_RIGHT)
    THEN
        -- delete object specific table entries
        DELETE FROM IBSDEV1.m2_Termin_01
        WHERE   oid = l_oid;
    END IF;

    COMMIT;

    -- return the state value:
    RETURN l_retValue;
END;
-- p_Termin_01$delete


--------------------------------------------------------------------------------
 -- Creates a new object (incl. rights check). <BR>
 --
 -- @input parameters:
 -- @param   @userId             ID of the user who is creating the object.
 -- @param   @op                 Operation to be performed (used for rights
 --                              check).
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
 --
-- delete procedure
CALL IBSDEV1.p_dropProc ('p_Termin_01$create');
-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Termin_01$create
(
    -- input parameters:
    IN  ai_userId           INT,            -- users id
    IN  ai_op               INT,            -- operation
    IN  ai_tVersionId       INT,            -- type version id of object (term)
    IN  ai_name             VARCHAR (63),   -- name of term
    IN  ai_containerId_s    VARCHAR (18),   -- objects container
    IN  ai_containerKind    INT,            -- 'part-of' or 'normal' container
    IN  ai_isLink           SMALLINT,       -- is object a link
    IN  ai_linkedObjectId_s VARCHAR (18),   -- link to object ..
    IN  ai_description      VARCHAR (255),  -- textual description
    -- output parameters:
    OUT ao_oid_s            VARCHAR (18)    -- created oid
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE         INT;

    ---------------------------------------------------------------------------
    -- conversionS (OBJECTIDSTRING) - all input objectids must be converted
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_linkedObjectId CHAR (8) FOR BIT DATA;
    ---------------------------------------------------------------------------

    -- constants:
    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_INSUFFICIENT_RIGHTS INT;
    DECLARE c_OBJECTNOTFOUND INT;
    DECLARE c_NOT_OK        INT;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE l_retValue      INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    CALL IBSDEV1.p_stringToByte (ai_containerId_s, l_containerId);
    CALL IBSDEV1.p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);

    -- set constants:
    SET c_ALL_RIGHT = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_NOT_OK = 0;

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_oid               = c_NOOID;
    SET ao_oid_s            = c_NOOID_s;

    ---------------------------------------------------------------------------
    -- START
    CALL IBSDEV1.p_Object_performCreate(ai_userId, ai_op, ai_tVersionId, ai_name,
         ai_containerId_s, ai_containerKind, ai_isLink, ai_linkedObjectId_s,
         ai_description, ao_oid_s, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_retValue = c_ALL_RIGHT)
    THEN
        -- create object specific data:
        -- insert dummies into table Termin_01 for given oid
        SET l_sqlcode = 0;

        INSERT INTO IBSDEV1.m2_Termin_01(oid, startDate, endDate, place,
            participants, maxNumParticipants, showParticipants, deadline,
            attachments)
        VALUES (l_oid, CURRENT TIMESTAMP, CURRENT TIMESTAMP, '', 0, 0, 0,
            NULL, 0);

        GET DIAGNOSTICS l_rowcount = ROW_COUNT;
        -- check if insertion was performed properly:
        IF (l_rowcount <= 0)
        THEN
            SET l_retValue = c_NOT_OK;
        END IF;
    END IF; -- if object created successfully

    COMMIT;

    RETURN l_retValue;
END;
-- p_Termin_01$create


--------------------------------------------------------------------------------
 -- This procedure checks if a given term has any overlapping terms. <BR>
 --
 -- @input parameters:
 -- @param   @oid_s              ID of the object.
 -- @param   @userId             ID of the user.
 --
 -- @output parameters:
 -- @param   @found              number of found overlapping terms
 --
 -- @output parameters:
 -- @returns A value representing the state of the procedure.
 --
-- delete procedure
CALL IBSDEV1.p_dropProc ('p_Termin_01$checkOverlap');
-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Termin_01$checkOverlap
(
    -- input parameters:
    IN ai_oid_s             VARCHAR (18),    -- oid of given term
    IN ai_userId            INT,            -- users id
    IN ai_startDate         TIMESTAMP,      -- beginning Date
    IN ai_endDate           TIMESTAMP,      -- end Date
    -- output parameter
    OUT ao_found            INT             -- number of found overlapping terms
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_RIGHT_VIEW    INT DEFAULT 2;  -- viewing rights

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

-- body:
    ---------------------------------------------------------------------------
    -- conversionS (OBJECTIDSTRING) - all input object ids must be converted
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

    ---------------------------------------------------------------------------
    -- START
    -- get numberof  overlapping terms for given current term
    SELECT  DISTINCT COUNT (*)
    INTO    ao_found
    FROM    IBSDEV1.m2_Termin_01 AS t, IBSDEV1.ibs_Object AS o,
            IBSDEV1.v_Container$rights AS c
    WHERE   t.oid <> l_oid
        -- check overlapping terms:
        AND (   (ai_startDate BETWEEN t.startDate AND t.endDate)
            OR  (ai_endDate BETWEEN t.startDate AND t.endDate)
            OR  (   ai_startDate <= t.startDate
                AND ai_endDate >= t.endDate
                )
            )
        -- join m2_termin_01
        AND t.oid = o.oid
        -- check if user has right to view overlapping terms
        AND c.oid = o.oid
        AND B_AND (c.rights, c_RIGHT_VIEW) = c_RIGHT_VIEW
        AND CAST (c.userId AS INT) = ai_userId;

    -- exit procedure:
    RETURN 0;
END;
-- p_Termin_01$checkOverlap

--------------------------------------------------------------------------------
 -- Changes the attributes of an existing object (incl. rights check). <BR>
 -- This procedure also creates or deletes a participant container (is sub-
 -- object which holds all participating users).
 --
 -- @input parameters:
 -- @param   @id                 ID of the object to be changed.
 -- @param   @userId             ID of the user who is changing the object.
 -- @param   @op                 Operation to be performed (used for rights
 --                              check).
 -- @param   @name               Name of the object.
 -- @param   @validUntil         Date until which the object is valid.
 -- @param   @description        Description of the object.
 -- @param   @showInNews         show in news flag
 --
 -- @param   @startDate          begin date&time of term
 -- @param   @endDate            end date&time of term
 -- @param   @place              place where term happens
 -- @param   @participants       does term have participants
 -- @param   @maxNumParticipants max. number of participants
 -- @param   @showParticipants   view participants allowed?
 -- @param   @deadline           last announcements
 --
 -- @param   @attachments        may term have attachments?
 -- 
 -- @output parameters:
 -- @returns A value representing the state of the procedure.
 --  ALL_RIGHT               Action performed, values returned, everything ok.
 --  INSUFFICIENT_RIGHTS     User has no right to perform action.
 --  OBJECTNOTFOUND          The required object was not found within the
 --                          database.
 --
    -- delete procedure
CALL IBSDEV1.p_dropProc ('p_Termin_01$change');
    -- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Termin_01$change
(
    -- input parameters:
    IN ai_oid_s             VARCHAR (18),    -- objects id as a string
    IN ai_userId            INT,            -- users id
    IN ai_op                INT,            -- operation
    IN ai_name              VARCHAR (63),    -- name of object
    IN ai_validUntil        TIMESTAMP,      -- valid in system
    IN ai_description       VARCHAR (255),   -- textual description
    IN ai_showInNews        SMALLINT,       -- show in news ?
    -- object specific attributes:
    IN ai_startDate         TIMESTAMP,      -- begin date&time of term
    IN ai_endDate           TIMESTAMP,      -- end date&time of term
    IN ai_place             VARCHAR (255),   -- place where term happens
    IN tmp_ai_participants  SMALLINT,       -- does term have participants
    IN tmp_ai_maxNumParticipants INT,       -- max. number of participants
    IN tmp_ai_showParticipants SMALLINT,    -- view participants allowed?
    IN tmp_ai_deadline      TIMESTAMP,      -- last announcements
    IN ai_attachments       SMALLINT        -- may term have attachments
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT;            -- something went wrong
    DECLARE c_ALL_RIGHT     INT;            -- everything was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT;      -- not enough rights for this
    DECLARE c_OBJECTNOTFOUND INT;           -- the object was not found
                                            -- operation
    DECLARE c_ALREADY_EXISTS INT;           -- the object already exists
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_TAB_PARTICIPANTS VARCHAR (63); -- code of participants tab

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;            -- return value of a function
    DECLARE l_ePos          VARCHAR (255);   -- error position description
    DECLARE l_rowCount      INT;            -- row counter
    DECLARE l_oid_s         VARCHAR (18);    -- string representation of oid
    DECLARE l_tabOid        CHAR (8) FOR BIT DATA;        -- the oid of the tab object
    DECLARE l_tabOid_s      VARCHAR (18);    -- string representation of oid
    DECLARE l_consistsOfId  INT;            -- id of tab in ibs_ConsistsOf
    DECLARE l_tabTVersionId INT;            -- tVersionId of the actual tab
    DECLARE l_tabName       VARCHAR (63);
    DECLARE l_tabDescription VARCHAR (255);
    DECLARE l_tabProc       VARCHAR (63);
    DECLARE l_participants  SMALLINT;
    DECLARE l_maxNumParticipants INT;
    DECLARE l_showParticipants SMALLINT;
    DECLARE l_deadline      TIMESTAMP;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    -- participants container id - string
    DECLARE l_partContId    CHAR (8) FOR BIT DATA;
    DECLARE l_partContId_s  VARCHAR (18);
    DECLARE l_rights        INT;
    DECLARE l_grpAll        INT;
    DECLARE l_owner         INT;
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- assign constants:
    SET c_NOT_OK = 0;
    SET c_ALL_RIGHT = 1;
    SET c_OBJECTNOTFOUND = 3;
    SET c_ALREADY_EXISTS = 21;
    SET c_TAB_PARTICIPANTS = 'Participants';

    -- initialize local variables and return values:
    SET l_retValue = c_ALL_RIGHT;
    SET l_rowCount = 0;
    SET l_participants = tmp_ai_participants;
    SET l_maxNumParticipants = tmp_ai_maxNumParticipants;
    SET l_showParticipants = tmp_ai_showParticipants;
    SET l_deadline = tmp_ai_deadline;

-- body:
    ---------------------------------------------------------------------------
    -- conversionS (OBJECTIDSTRING) - all input object ids must be converted

    -- name of stored procedure for
    -- creating the tab object
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

    -- perform the change of the object:
    CALL IBSDEV1.p_Object_performChange (ai_oid_s, ai_userId, ai_op, ai_name,
        ai_validUntil, ai_description, ai_showInNews, c_NOOID);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    IF l_retValue = c_ALL_RIGHT THEN
        -- check if participants container is needed:
        IF l_participants <> 0 THEN
            -- get participant container for term
            -- oid of term is stored as containerId in participant container
            CALL IBSDEV1.p_Object_getTabOid(l_oid, c_TAB_PARTICIPANTS, l_partContId);
            GET DIAGNOSTICS l_retValue = RETURN_STATUS;

            -- check if part. cont. object already exists:
            IF l_retValue = c_OBJECTNOTFOUND THEN
                -- create the participant container:
                CALL IBSDEV1.p_Object_createTab(ai_userId, ai_op, l_oid,
                    c_TAB_PARTICIPANTS,  l_partContId);
                GET DIAGNOSTICS l_retValue = RETURN_STATUS;
            END IF;

            -- if no participants container exists
            -- now modify rights of participant container!
            -- rule: everyone who is allowed to see the term itself,
            -- is also allowed to see the participant-tab and
            -- [un]announce participants.
            -- exception1:
            -- flag 'showParticipants' not set:
            --  * owner of term allowed to see/modify all
            --  * cancel 'view' right for all other users; they are only
            --    allowed to see/modify participants-objects of which they
            --    are owner!
            -- get group-id of all users in current domain
            SELECT d.allGroupId
            INTO l_grpAll
            FROM IBSDEV1.ibs_Domain_01 AS d, IBSDEV1.ibs_User AS u
            WHERE u.id = ai_userId AND d.id = u.domainId;

            -- is flag 'showParticipants' set?
            IF l_showParticipants <> 0 THEN
                -- set rights-value for SOME operations (viewElems,
                -- read, view, new, delElem) for ALL users.
                -- will recursively be set for all participants in container
                SELECT SUM (id)
                INTO l_rights
                FROM IBSDEV1.ibs_Operation
                WHERE name IN
                    ('viewElems', 'read', 'view', 'new', 'addElem', 'delElem');
            ELSE
                -- set rights-value for only a FEW operations (viewElems,
                -- read, view, new, delElem) for ALL users
                -- will recursively be set for all participants in container
                SELECT SUM (id)
                INTO l_rights
                FROM IBSDEV1.ibs_Operation
                WHERE name IN
                    ('viewElems', 'read', 'new', 'addElem', 'delElem');
            END IF;
            -- else flag 'showParticipants' not set
            -- set rights for participant container (recursively) for ALL
            -- users:
            CALL IBSDEV1.p_Rights$setRights(l_partContId, l_grpAll, l_rights,
                1);
        ELSE
            -- delete not needed tab participants container:
            CALL IBSDEV1.p_Object_deleteTab(ai_userId, ai_op, l_oid,
                c_TAB_PARTICIPANTS);
            GET DIAGNOSTICS l_retValue = RETURN_STATUS;
            -- reset values:
            SET l_partContId = NULL;
            SET l_participants = 0;
            SET l_maxNumParticipants = 0;
            SET l_showParticipants = 0;
        END IF;

        -- else no participants container needed
        -- check if dead line is set:
        IF l_deadline IS NULL THEN
            -- set start date as dead line:
            SET l_deadline = ai_startDate;
        END IF;

        -- if no dead line set
        -- update object specific values in table Termin_01:
        UPDATE IBSDEV1.m2_Termin_01
        SET startDate = ai_startDate,
            endDate = ai_endDate,
            place = ai_place,
            participants = l_participants,
            maxNumParticipants = l_maxNumParticipants,
            showParticipants = l_showParticipants,
            deadline = l_deadline,
            attachments = ai_attachments
        WHERE oid = l_oid;
    END IF;

    -- if operation properly performed

    -- return the state value
    RETURN l_retValue;
END;
-- p_Termin_01$change

--------------------------------------------------------------------------------
 -- Copy an object and all its values. <BR>
 --
 -- @input parameters:
 -- @param   @oid_s              ID of the object to be copied.
 -- @param   @userId             ID of the user who is copying the object.
 -- @param   @newOid             ID of the copy-object.
 --
 -- @output parameters:
 -- @returns A value representing the state of the procedure.
 --
    -- delete procedure
CALL IBSDEV1.p_dropProc ('p_Termin_01$BOCopy');
    -- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Termin_01$BOCopy
(
    -- common input parameters:
    IN ai_oid               CHAR (8) FOR BIT DATA,
    IN ai_userId            INT,
    IN ai_newOid            CHAR (8) FOR BIT DATA
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    DECLARE SQLCODE         INT;

    -- definitions:
    -- define return constants:
    DECLARE c_NOT_OK        INT;
    DECLARE c_ALL_RIGHT     INT;

    -- define return values:
    DECLARE l_retValue     INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- set constants:
    SET c_NOT_OK = 0;
    SET c_ALL_RIGHT = 1;

    -- return value of this procedure
    -- initialize return values:
    SET l_retValue = c_NOT_OK;

    -- make an insert for all type specific tables:
    INSERT INTO IBSDEV1.m2_Termin_01(oid, startDate, endDate, place, participants,
                             maxNumParticipants, showParticipants, deadline,
                             attachments)
    SELECT ai_newOid, startDate, endDate, place, participants,
           maxNumParticipants, showParticipants, deadline, attachments
    FROM IBSDEV1.m2_Termin_01
    WHERE oid = ai_oid;

    GET DIAGNOSTICS l_rowcount = ROW_COUNT;

    COMMIT;

    -- check if insert was performed correctly:
    IF l_rowcount >= 1 THEN
        -- at least one row affected?
        SET l_retValue = c_ALL_RIGHT;
    END IF;

    -- set return value
    -- return the state value:
    RETURN l_retValue;
END;
-- p_Termin_01$BOCopy