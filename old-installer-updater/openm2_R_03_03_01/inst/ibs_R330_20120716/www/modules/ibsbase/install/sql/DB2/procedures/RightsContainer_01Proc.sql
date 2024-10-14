--------------------------------------------------------------------------------
-- All stored procedures regarding the RightsContainer_01 Object. <BR>
-- 
-- @version     $Revision: 1.4 $, $Date: 2003/10/21 22:14:50 $
--              $Author: klaus $
--
-- @author      MArcel Samek (MS)  020910
--------------------------------------------------------------------------------


--------------------------------------------------------------------------------
-- Gets all data from a given object (incl. rights check). <BR>
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be changed.
-- @param   @userId             ID of the user who is creating the object.
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
-- @param   @showInNews         flag if object should be shown in newscontainer
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
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the 
--                          database.
--

-- delete procedure
CALL IBSDEV1.p_dropProc ('p_RightsContainer_01$retrieve');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_RightsContainer_01$retrieve
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
    OUT ao_checkOutUserName VARCHAR (63)
)
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- constants:
    DECLARE c_NOT_OK        INT DEFAULT 0;  -- something went wrong
    DECLARE c_ALL_RIGHT     INT DEFAULT 1;  -- operation was o.k.
    DECLARE c_INSUFFICIENT_RIGHTS INT DEFAULT 2;
    DECLARE c_OBJECTNOTFOUND INT DEFAULT 3;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                        -- default value for no defined oid
    DECLARE c_RIGHT_READ    INT DEFAULT 2;
    DECLARE c_INNEWS        INT DEFAULT 4;

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;        -- return value of this function
    DECLARE l_oid           CHAR (8) FOR BIT DATA; -- oid of the object
    DECLARE l_rights        INT;
    DECLARE l_dummy         INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_ePos          VARCHAR (2000); -- error position description

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;

-- body:
    -- finish previous and begin new transaction:
    COMMIT;

    SET l_sqlcode = 0;
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

    -- check if there occurred an error:
    IF (l_sqlcode <> 0)                 -- any exception?
    THEN
        -- create error entry:
        SET l_ePos = 'Error in conversion ai_oid_s';
        GOTO exception1;                -- call common exception handler
    END IF; -- if any exception

    -- get container id of object:
    SET l_sqlcode = 0;
    SELECT  o.containerId, o2.name
    INTO    ao_containerId, ao_containerName
    FROM    IBSDEV1.ibs_Object AS o, IBSDEV1.ibs_Object AS o2
    WHERE   o.oid = l_oid AND o2.oid = o.containerId;

    IF (l_sqlcode = 0)                  -- the object exists:
    THEN
        -- get rights for this user
        CALL IBSDEV1.p_Rights$checkRights(
            l_oid,        -- given object to be accessed by user
            ao_containerId,
                          -- container of given object
            ai_userId,    -- user_id
            ai_op,        -- required rights user must have to
                          -- retrieve object (op. to be
                          -- performed)
            l_rights
            );

        IF (l_rights > 0)               -- user has the necessary rights
        THEN
            -- get the data of the object and return it
            SELECT  o1.state, o1.tVersionId, o1.typeName, o1.name,
                    o1.containerId, o1.containerKind,o1.isLink,
                    o1.linkedObjectId, o1.owner, own.fullname,
                    o1.creationDate, o1.creator, cr.fullname,
                    o1.lastChanged, o1.changer, ch.fullname,
                    o1.validUntil, IBSDEV1.B_AND (o1.flags, c_INNEWS)
            INTO    ao_state, ao_tVersionId, ao_typeName, ao_name, ao_containerId,
                    ao_containerKind, ao_isLink, ao_linkedObjectId, ao_owner,
                    ao_ownerName, ao_creationDate, ao_creator, ao_creatorName,
                    ao_lastChanged, ao_changer, ao_changerName, ao_validUntil,
                    ao_showInNews
            FROM    IBSDEV1.ibs_Object AS o1
                    LEFT OUTER JOIN IBSDEV1.ibs_User AS own
                    ON  o1.owner = own.id
                    LEFT OUTER JOIN IBSDEV1.ibs_User AS cr
                    ON o1.creator = cr.id
                    LEFT OUTER JOIN IBSDEV1.ibs_User AS ch
                    ON o1.changer = ch.id
            WHERE   o1.oid = l_oid;

            -- Set Object as Read --
            CALL IBSDEV1.p_setRead(l_oid, ai_userId);
        ELSE                            -- user has not the necessary rights
            SET l_retValue = c_INSUFFICIENT_RIGHTS;
        END IF; -- else user has not the necessary rights

        -- check if there occurred an error:
        IF (l_sqlcode <> 0)         -- any exception?
        THEN
            -- create error entry:
            SET l_ePos = '';
            GOTO exception1;        -- call common exception handler
        END IF; -- if any exception
    ELSE                                -- the object not exists:
        -- set the return value with the error code:
        SET l_retValue = c_OBJECTNOTFOUND;
    END IF; -- else the object not exists:

    -- finish transaction:
    COMMIT;                             -- make changes permanent
  
    -- return the state value:
    RETURN l_retValue;

exception1:
    -- log the error:
    CALL IBSDEV1.logError (500, 'p_RightsContainer_01$retrieve',
        l_sqlcode, l_ePos,
        'ai_userId', ai_userId, '', '',
        'ai_op', ai_op, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '',
        '', 0, '', '');
    
    -- return error value:
    RETURN c_NOT_OK;

END;
-- p_RightsContainer_01$retrieve