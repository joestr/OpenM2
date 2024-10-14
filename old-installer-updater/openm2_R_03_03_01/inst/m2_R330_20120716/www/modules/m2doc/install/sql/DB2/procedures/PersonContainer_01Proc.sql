--------------------------------------------------------------------------------
-- All stored procedures regarding the PersonContainer_01 Object. <BR>
-- 
-- @version     $Id: PersonContainer_01Proc.sql,v 1.5 2003/10/31 00:12:50 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020901
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
-- @param   @maxlevels          Maximum of the levels allowed in the discussion
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the 
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_PersonContainer_01$retrieve');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_PersonContainer_01$retrieve
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
    DECLARE c_INSUFFICIENT_RIGHTS INT;
    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_OBJECTNOTFOUND INT;
    DECLARE c_RIGHT_READ    INT;
    DECLARE c_INNEWS        INT;
    DECLARE c_ISCHECKEDOUT  INT;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;
    DECLARE l_rights        INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;
    DECLARE l_tempName      VARCHAR (63);
    DECLARE l_tempOid       CHAR (8) FOR BIT DATA;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- set constants:
    SET c_ALL_RIGHT         = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_OBJECTNOTFOUND    = 3;
    SET c_RIGHT_READ        = 2;
    SET c_INNEWS            = 4;
    SET c_ISCHECKEDOUT      = 16;

    -- initialize local variables and return values:
    SET l_retValue          = c_ALL_RIGHT;
    SET l_rights            = 0;

-- body:
    ---------------------------------------------------------------------------
    -- conversionS (OBJECTIDSTRING) - all input objectids must be converted
    ---------------------------------------------------------------------------
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);

    -- get container id of object:
    SET l_sqlcode = 0;

    SELECT o.containerId, o2.name 
    INTO ao_containerId, ao_containerName
    FROM IBSDEV1.ibs_Object o, IBSDEV1.ibs_Object o2
    WHERE o.oid = l_oid
        AND o2.oid = o.containerId;

    -- check if the object exists:
    IF (l_sqlcode = 0)
    THEN
        -- get rights for this user
        CALL IBSDEV1.p_Rights$checkRights(l_oid, ao_containerId, ai_userId, ai_op, l_rights);
        -- check if the user has the necessary rights
        IF (l_rights > 0)
        THEN
            -- get the data of the object and return it
            SELECT  o.state, o.tVersionId, o.typeName, o.name, o.containerId,
                    o.containerKind, o.isLink, o.linkedObjectId, o.owner, own.fullname,
                    o.creationDate, o.creator, cr.fullname, o.lastChanged, o.changer,
                    ch.fullname, o.validUntil,
                    B_AND (o.flags,c_INNEWS), B_AND (o.flags,c_ISCHECKEDOUT)
            INTO    ao_state, ao_tVersionId, ao_typeName, ao_name, ao_containerId,
                    ao_containerKind, ao_isLink, ao_linkedObjectId, ao_owner,
                    ao_ownerName, ao_creationDate, ao_creator, ao_creatorName,
                    ao_lastChanged, ao_changer, ao_changerName, ao_validUntil,
                    ao_showInNews, ao_checkedOut
            FROM    IBSDEV1.ibs_Object o
                    LEFT OUTER JOIN IBSDEV1.ibs_User own ON o.owner = own.id 
                    LEFT OUTER JOIN IBSDEV1.ibs_User cr ON o.creator = cr.id
                    LEFT OUTER JOIN IBSDEV1.ibs_User ch ON o.changer = ch.id
            WHERE   o.oid = l_oid;
      
            IF (ao_checkedOut = 1)
            THEN
                -- get the info who checked out the object
                SELECT  ch.checkout, ch.userId, u.oid, u.fullname 
                INTO    ao_checkOutDate, ao_checkOutUser, l_tempOid, l_tempName
                FROM    IBSDEV1.ibs_CheckOut_01 ch
                        LEFT OUTER JOIN IBSDEV1.ibs_User u ON u.id = ch.userid
                WHERE   ch.oid = l_oid;

                -- rights set for viewing the User?
                CALL IBSDEV1.p_Rights$checkRights (l_tempOid, c_NOOID,
                    ai_userId, 2, l_rights);
                -- check if the user has the necessary rights
                IF l_rights = 2 THEN 
                    SET ao_checkOutUserName = l_tempName;
                END IF;
                -- rights set for reading the User?
                CALL IBSDEV1.p_Rights$checkRights (l_tempOid, c_NOOID,
                    ai_userId, 4, l_rights);
                -- check if the user has the necessary rights
                IF l_rights = 4 THEN 
                    SET ao_checkOutUserOid = l_tempOid;
                END IF;
            END IF;
            -- Set Object as Read --
            CALL IBSDEV1.p_setRead(l_oid, ai_userId);
        ELSE 
            SET l_retValue = c_INSUFFICIENT_RIGHTS;
        END IF;
    ELSE 
        -- set the return value with the error code:
        SET l_retValue = c_OBJECTNOTFOUND;
    END IF;
    -- return the state value
    RETURN l_retValue;
END;
-- p_PersonContainer_01$retrieve
