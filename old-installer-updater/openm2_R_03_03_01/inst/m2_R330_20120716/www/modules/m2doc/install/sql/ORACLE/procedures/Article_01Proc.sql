/******************************************************************************
 * All stored procedures regarding the Article_01 Object. <BR>
 *
 * @version     $Id: Article_01Proc.sql,v 1.13 2003/10/31 00:13:13 klaus Exp $
 *
 * @author      Keim Christine (CK)  990805
 ******************************************************************************
 */


/******************************************************************************
 * Creates a new Article_01 Object (incl. rights check). <BR>
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
CREATE OR REPLACE FUNCTION p_Article_01$create
(
    ai_userId 	        INTEGER,
    ai_op 	        INTEGER,
    ai_tVersionId 	INTEGER,
    ai_name 	        VARCHAR2,
    ai_containerId_s 	VARCHAR2,
    ai_containerKind 	INTEGER,
    ai_isLink 	        NUMBER,
    ai_linkedObjectId_s VARCHAR2,
    ai_description 	VARCHAR2,
    ai_oid_s 	        OUT     VARCHAR2
)
RETURN INTEGER
AS
    c_NOOID                CONSTANT  RAW (8) := hexToRaw ('0000000000000000');
    c_NOT_OK                CONSTANT INTEGER := 0; -- operation not o.k.
    c_ALL_RIGHT            CONSTANT  INTEGER := 1;
    c_INSUFFICIENT_RIGHTS  CONSTANT  INTEGER := 2;
    c_ALREADY_EXISTS       CONSTANT  INTEGER := 21;     

    StoO_selcnt	            INTEGER;
    StoO_error 	            INTEGER;
    StoO_rowcnt	            INTEGER;
    StoO_errmsg	            VARCHAR2(255);
    StoO_sqlstatus	    INTEGER;
    l_containerId 	    RAW (8);
    l_linkedObjectId 	    RAW (8);
    l_retValue 	            INTEGER := c_ALL_RIGHT;
    l_oid 		    RAW (8) := c_NOOID;
    l_discussionId 	    RAW (8);
    l_rights 	 	    INTEGER;
    l_actRights 	    INTEGER;
    l_icon		    ibs_Object.icon%Type;
    l_newContainerId        ibs_Object.containerId%Type;
    l_oLevel                ibs_Object.oLevel%Type;
    l_posNoPath             VARCHAR2 (254);
    l_desc                  VARCHAR2 (255);

BEGIN
	IF (ai_description IS NULL)
        THEN
            l_desc := ' ';
        ELSE
            l_desc := ai_description;
        END IF;
        p_stringToByte(ai_containerId_s, l_containerId);
	p_stringToByte(ai_linkedObjectId_s, l_linkedObjectId);

	l_retValue := p_Object$performCreate
        (  ai_userId,
	   ai_op,
	   ai_tVersionId,
	   ai_name,
	   ai_containerId_s,
	   ai_containerKind,
	   ai_isLink,
	   ai_linkedObjectId_s,
	   l_desc,
	   ai_oid_s,
	   l_oid  
        );
	IF  ( l_retValue = c_ALL_RIGHT) 
        THEN
	    BEGIN
		    SELECT  posNoPath
		    INTO    l_posNoPath 
            FROM    ibs_Object 
		    WHERE   oid = l_oid;
	    EXCEPTION
		WHEN OTHERS THEN
		     ibs_error.log_error(ibs_error.error, 'p_Article_01$create','Error SELECT posnopath');
                     RAISE;
	    END;
	    BEGIN
	        SELECT  oid
		    INTO    l_discussionId 
            FROM    ibs_Object 
		    WHERE   tVersionId  IN (16843521,16845313)
                    AND l_posNoPath LIKE posNoPath || '%';
	    EXCEPTION
		WHEN OTHERS THEN
                     ibs_error.log_error(ibs_error.error, 'p_Article_01$create','Error SELECT oid');
                     RAISE;
            END;
            BEGIN
		INSERT INTO m2_Article_01 (oid, content, discussionId)
                VALUES(l_oid,l_desc,l_discussionid);
	    EXCEPTION
		WHEN OTHERS THEN
	             ibs_error.log_error(ibs_error.error, 'p_Article_01$create','Error in INSERT INTO');
                     RAISE;
            END;
	    IF  ( INSTR(l_containerId,'01010501',1,1) > 0) 
            THEN
		BEGIN
		    UPDATE ibs_Object
		    SET name = (SELECT  'AW: ' || name
			        FROM ibs_Object 
			        WHERE oid = l_containerId)
		    WHERE oid = l_oid;
		EXCEPTION
                   WHEN OTHERS THEN
                      ibs_error.log_error(ibs_error.error, 'p_Article_01$create','Error in UPDATE ibs_Object');
                      RAISE;
                 END;
                 BEGIN
                     SELECT  oLevel, icon, containerId
                     INTO    l_oLevel, l_icon, l_newContainerId
                     FROM    ibs_object 
                     WHERE   oid = l_oid;
                 EXCEPTION
                     WHEN OTHERS THEN
                         ibs_error.log_error(ibs_error.error, 'p_Article_01$create','Error in UPDATE ibs_Object');
                         RAISE;
                 END;
                 BEGIN
                    p_ObjectUpdate ( ai_name,
                                     l_desc,
                                     l_icon,
                                     l_oid,
                                     l_newContainerId,
                                     l_newContainerId,
                                     l_oLevel,
                                     l_posNoPath );
                 EXCEPTION
                     WHEN OTHERS THEN
                         ibs_error.log_error ( ibs_error.error,
                                                  'p_Article_01$create',
                                                  'p_ObjectUpdate liefert ' ||
                                                  'sqlcode = ' || sqlcode ||
                                                  ', sqlerrm = ' || sqlerrm );                              
                     RAISE;
                 END; 
	    END IF;
	END IF;

	COMMIT WORK;
	RETURN l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_Article_01$create',
    ', userId = ' || ai_userId  ||
    ', op = ' || ai_op  ||
    ', tVersionId = ' || ai_tVersionId ||
    ', name = ' || ai_name ||
    ', containerId_s = ' || ai_containerId_s ||
    ', containerKind = ' || ai_containerKind ||
    ', isLink = ' || ai_isLink ||
    ', linkedObjectId_s = ' || ai_linkedObjectId_s ||
    ', description = ' || ai_description ||
    ', errorcode = ' || StoO_error ||
    ', errormessage = ' || StoO_errmsg);
    -- return error value:
    RETURN c_NOT_OK;
END p_Article_01$create;
/

show errors;


/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be changed.
 * @param   @userId                ID of the user who is creating the object.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 * @param   @name               Name of the object.
 * @param   @validUntil         Date until which the object is valid.
 * @param   @showInNews         show in news flag.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Article_01$change
(
  oid_s     VARCHAR2 ,
  userId     INTEGER ,
  op     NUMBER ,
  name     VARCHAR2 ,
  validUntil     DATE ,
  description  VARCHAR2,
  ai_showInNews INTEGER
)
RETURN INTEGER
AS
    StoO_selcnt    INTEGER;
    StoO_error     INTEGER;
    StoO_rowcnt    INTEGER;
    StoO_errmsg    VARCHAR2(255);
    StoO_sqlstatus    INTEGER;

    c_NOT_OK                CONSTANT INTEGER := 0; -- operation not o.k.
    ALL_RIGHT     INTEGER;
    INSUFFICIENT_RIGHTS     INTEGER;
    OBJECTNOTFOUND     INTEGER;
    retValue     INTEGER;
    oid         RAW (8);

BEGIN
    p_Article_01$change.ALL_RIGHT :=  1;
    p_Article_01$change.INSUFFICIENT_RIGHTS :=  2;
    p_Article_01$change.OBJECTNOTFOUND :=  3;

    p_Article_01$change.retValue :=  p_Article_01$change.ALL_RIGHT;

    /*[SPCONV-ERR(26)]:BEGIN TRAN statement ignored*/
    NULL;
    BEGIN
    p_Article_01$change.retValue:=p_Object$performChange(p_Article_01$change.oid_s,
     p_Article_01$change.userId,
     p_Article_01$change.op,
     p_Article_01$change.name,
     p_Article_01$change.validUntil,
     '', 
     p_Article_01$change.ai_showInNews,
     p_Article_01$change.oid);
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error := SQLCODE;

            StoO_errmsg := SQLERRM;

    END;
    BEGIN
    StoO_error   := 0;
    StoO_rowcnt  := 0;
    UPDATE m2_Article_01
    SET state = (
    SELECT  o.state
     FROM ibs_Object o 
    WHERE o.oid = p_Article_01$change.oid)
    
    WHERE oid = p_Article_01$change.oid;
    StoO_rowcnt := SQL%ROWCOUNT;
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error  := SQLCODE;
            StoO_errmsg := SQLERRM;
    END;

    COMMIT WORK;
    RETURN p_Article_01$change.retValue;
    RETURN 0;

EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_Article_01$change',
    ', oid_s = ' || oid_s ||
    ', userId = ' || userId  ||
    ', op = ' || op ||
    ', name = ' || name ||
    ', validUntil = ' || validUntil  ||
    ', errorcode = ' || StoO_error ||
    ', errormessage = ' || StoO_errmsg);
    -- return error value:
    RETURN c_NOT_OK;
END p_Article_01$change;
/

show errors;


/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be changed.
 * @param   ai_userId           ID of the user who is creating the object.
 * @param   ai_op               Operation to be performed (used for rights 
 *                              check).
 *
 * @output parameters:
 * @param   ao_state            The object's state.
 * @param   ao_tVersionId       ID of the object's type (correct version).
 * @param   ao_typeName         Name of the object's type.
 * @param   ao_name             Name of the object itself.
 * @param   ao_containerId      ID of the object's container.
 * @param   ao_containerName    Name of the object's container.
 * @param   ao_containerKind    Kind of object/container relationship.
 * @param   ao_isLink           Is the object a link?
 * @param   ao_linkedObjectId   Link if isLink is true.
 * @param   ao_owner            ID of the owner of the object.
 * @param   ao_ownerName        Name of the owner of the object.
 * @param   ao_creationDate     Date when the object was created.
 * @param   ao_creator          ID of person who created the object.
 * @param   ao_creatorName      Name of person who created the object.
 * @param   ao_lastChanged      Date of the last change of the object.
 * @param   ao_changer          ID of person who did the last change to the 
 *                              object.
 * @param   ao_changerName      Name of person who did the last change to the 
 *                              object.
 * @param   ao_validUntil       Date until which the object is valid.
 * @param   ao_description      Description of the object.
 * @param   ao_showInNews       Flag if object should be shown in newscontainer
 * @param   ao_checkedOut       Is the object checked out?
 * @param   ao_checkOutDate     Date when the object was checked out
 * @param   ao_checkOutUser     id of the user which checked out the object
 * @param   ao_checkOutUserOid  Oid of the user which checked out the object
 *                              is only set if this user has the right to READ
 *                              the checkOut user
 * @param   ao_checkOutUserName Name of the user which checked out the object,
 *                              is only set if this user has the right to view
 *                              the checkOut-User
 * @param   ao_discussionType   Type of discussion.
 * @param   ao_hasSubEntries    Does the object have sub entries?
 * @param   ao_rights           Permissions of the current user on the object.
 * @param   ao_discussionId     Id of discussion where the object belongs to.
 * @param   ao_containerDescription Description of container of the object
 *                              (out of ibs_Object).
 *
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Article_01$retrieve
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    -- commmon output parameters:
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
    -- specific output parameters:
    ao_discussionType       OUT INTEGER,
    ao_hasSubEntries        OUT INTEGER,
    ao_rights               OUT INTEGER,
    ao_discussionId         OUT RAW,
    ao_containerDescription OUT VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; -- not enough rights for this
                                            -- operation
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3; -- the object was not found                                        
    c_DISC_BLACKBOARD       CONSTANT INTEGER := 0; -- discussion type blackboard
    c_DISC_DISCUSSION       CONSTANT INTEGER := 1; -- standard discussion type
    c_ST_ACTIVE             CONSTANT INTEGER := 2; -- active object state

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_TV_Discussion_01      INTEGER := 16843521; -- tVersionId of
                                            -- standard discussion
    l_TV_Blackboard_01      INTEGER := 16845313; -- tVersionId of
                                            -- blackboard
    l_TV_DiscEntry_01       INTEGER := 16844033; -- tVersionId of
                                            -- discussion entry
    l_oid                   RAW (8);        -- the oid of the current object

-- body:
BEGIN
    -- retrieve the base object data:
    l_retValue := p_Object$performRetrieve (
            ai_oid_s, ai_userId, ai_op,
            ao_state, ao_tVersionId, ao_typeName, ao_name, 
            ao_containerId, ao_containerName, ao_containerKind, 
            ao_isLink, ao_linkedObjectId, 
            ao_owner, ao_ownerName, 
            ao_creationDate, ao_creator, ao_creatorName,
            ao_lastChanged, ao_changer, ao_changerName,
            ao_validUntil, ao_description, ao_showInNews, 
            ao_checkedOut, ao_checkOutDate, 
            ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName,
            l_oid);

    IF (l_retValue = c_ALL_RIGHT) -- operation performed properly?
    THEN
        -- retrieve object type specific data:
        -- get the data of the tVersions:
        BEGIN
            SELECT  actVersion
            INTO    l_TV_Discussion_01
            FROM    ibs_Type
            WHERE   code = 'Discussion';

            SELECT  actVersion
            INTO    l_TV_Blackboard_01
            FROM    ibs_Type
            WHERE   code = 'BlackBoard';

            SELECT  actVersion
            INTO    l_TV_DiscEntry_01
            FROM    ibs_Type
            WHERE   code = 'Article';
        EXCEPTION
            WHEN OTHERS THEN -- any error
                -- create error entry:
                l_ePos := 'get tVersionIds';
                RAISE;                  -- call common exception handler
        END;

        -- retrieve the discussionId of the entry:
        BEGIN
            SELECT  discussionId
            INTO    ao_discussionId
            FROM    m2_Article_01
            WHERE   oid = l_oid;
        EXCEPTION
            WHEN OTHERS THEN -- any error
                -- create error entry:
                l_ePos := 'get discussionId';
                RAISE;                  -- call common exception handler
        END;

        -- retrieve the type of the discussion:
        BEGIN
            SELECT  DECODE (tVersionId, l_TV_Blackboard_01,
                            c_DISC_BLACKBOARD, c_DISC_DISCUSSION)
            INTO    ao_discussionType
            FROM    ibs_Object
            WHERE   tVersionId IN (l_TV_Discussion_01, l_TV_Blackboard_01)
                AND oid = ao_discussionId;
        EXCEPTION
            WHEN OTHERS THEN -- any error
                -- create error entry:
                l_ePos := 'get discussionType';
                RAISE;                  -- call common exception handler
        END;

        -- retrieve if entry has subEntries:
        IF (ao_discussionType = c_DISC_DISCUSSION) -- standard discussion?
        THEN
            -- get the number of sub entries of the current entry:
            BEGIN
                SELECT  COUNT (*) 
                INTO    ao_hasSubEntries
                FROM    ibs_Object
                WHERE   tVersionId = l_TV_DiscEntry_01
                    AND state = c_ST_ACTIVE
                    AND containerId = l_oid;
            EXCEPTION
                WHEN OTHERS THEN -- any error
                    -- create error entry:
                    l_ePos := 'get subEntries';
                    RAISE;              -- call common exception handler
            END;
        -- end if standard discussion
        ELSE                            -- other type of discussion
            -- there are no sub entries allowed:
            ao_hasSubEntries := 0;
        END IF; -- else other type of discussion

        -- retrieve the description of the container:
        BEGIN
            SELECT  description
            INTO    ao_containerDescription
            FROM    ibs_Object
            WHERE   oid = ao_containerId;
        EXCEPTION
            WHEN OTHERS THEN -- any error
                -- create error entry:
                l_ePos := 'get containerDescription';
                RAISE;                  -- call common exception handler
        END;

        p_Rights$getRights (l_oid, ai_userId, ao_rights);
    END IF; -- if operation performed properly

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid_s = ' || ai_oid_s ||
            ', ai_userId = ' || ai_userId ||
            ', ai_op = ' || ai_op ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Article_01$retrieve', l_eText);
        -- return error code:
        RETURN c_NOT_OK;
END p_Article_01$retrieve;
/

show errors;


/******************************************************************************
 * Deletes a Discussion_01 object and all its values (incl. rights check). <BR>
 * This procedure also delets all links showing to this object.
 * 
 * @input parameters:
 * @param   @oid_s              ID of the object to be deleted.
 * @param   @userId                ID of the user who is deleting the object.
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
CREATE OR REPLACE FUNCTION p_Article_01$delete(
oid_s     VARCHAR2 ,
userId     INTEGER ,
op     NUMBER )
RETURN INTEGER
AS
    StoO_selcnt    INTEGER;
    StoO_error     INTEGER;
    StoO_rowcnt    INTEGER;
    StoO_errmsg    VARCHAR2(255);
    StoO_sqlstatus    INTEGER;
    oid         RAW(8);
    c_NOT_OK                CONSTANT INTEGER := 0; -- operation not o.k.
    INSUFFICIENT_RIGHTS     NUMBER(10,0);
    ALL_RIGHT     NUMBER(10,0);
    OBJECTNOTFOUND     NUMBER(10,0);
    retValue     NUMBER(10,0);
    containerId     RAW(8);
BEGIN
    BEGIN
    p_stringToByte(p_Article_01$delete.oid_s,
     p_Article_01$delete.oid);
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error := SQLCODE;
            StoO_errmsg := SQLERRM;
    END;

    p_Article_01$delete.ALL_RIGHT :=  1;
    p_Article_01$delete.INSUFFICIENT_RIGHTS :=  2;
    p_Article_01$delete.OBJECTNOTFOUND :=  3;

    p_Article_01$delete.retValue :=  p_Article_01$delete.ALL_RIGHT;
    /*[SPCONV-ERR(26)]:BEGIN TRAN statement ignored*/
    BEGIN
    p_Article_01$delete.retValue:=p_Object$performDelete(p_Article_01$delete.oid_s,
     p_Article_01$delete.userId,
     p_Article_01$delete.op,
     p_Article_01$delete.oid);
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error := SQLCODE;
            StoO_errmsg := SQLERRM;
    END;
    IF  ( p_Article_01$delete.retValue = p_Article_01$delete.ALL_RIGHT) THEN
    BEGIN
        BEGIN
        StoO_error   := 0;
        StoO_rowcnt  := 0;
        DELETE  m2_Article_01 
            WHERE oid = p_Article_01$delete.oid;
        StoO_rowcnt := SQL%ROWCOUNT;
        EXCEPTION
            WHEN OTHERS THEN
                StoO_error  := SQLCODE;
                StoO_errmsg := SQLERRM;
        END;
    END;
    END IF;

    COMMIT WORK;
    RETURN p_Article_01$delete.retValue;
END p_Article_01$delete;
/

show errors;


/******************************************************************************
 * Change the state of an existing object. <BR>
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the object to be changed.
 * @param   ai_userId           ID of the user who is changing the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @param   ai_state            The new state of the object.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Article_01$changeState
(
    ai_oid_s        VARCHAR2,
    ai_userId       INTEGER,
    ai_op           INTEGER,
    ai_state        INTEGER
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- operation not o.k.
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3;
    c_RIGHT_INSERT          CONSTANT INTEGER := 1;
    c_RIGHT_UPDATE          CONSTANT INTEGER := 8;
    c_ST_ACTIVE             CONSTANT INTEGER := 2; -- active state
    c_ST_CREATED            CONSTANT INTEGER := 4; -- created state

    -- local variables:
    l_oid                   RAW (8);
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_rights                INTEGER := 0;
    l_containerId           RAW (8) := c_NOOID;
    l_oldState              INTEGER := 0;

BEGIN
    BEGIN
        p_stringToByte (ai_oid_s, l_oid);
    EXCEPTION
        WHEN OTHERS THEN
            ibs_error.log_error (ibs_error.error, 'p_Article_01$changestate', 
                'Error in p_StringToByte');
        RAISE;
    END;

    BEGIN
        SELECT   containerId,  state
        INTO     l_containerId, l_oldState 
        FROM     ibs_Object 
        WHERE    oid = l_oid;

        l_rights := p_Rights$checkRights
            (l_oid,
            l_containerId,
            ai_userId,
            ai_op,
            l_rights);
        IF  (l_rights = ai_op) 
        THEN
            BEGIN
                -- set the new state for the object and all tabs:
                UPDATE  ibs_Object
                SET     state = ai_state
                WHERE   oid = l_oid
                    OR  (   containerId = l_oid
                        AND containerKind = 2
                        AND state <> ai_state
                        AND (   state = c_ST_ACTIVE
                            OR  state = c_ST_CREATED
                            )
                        );
            EXCEPTION
                WHEN OTHERS THEN
                    ibs_error.log_error (ibs_error.error, 'p_Article_01$changestate',
                        'Error in UPDATE ibs_object');
                RAISE;
            END;

            BEGIN
                UPDATE m2_Article_01
                SET state = ai_state
                WHERE oid = l_oid;
            EXCEPTION
                WHEN OTHERS THEN
                    ibs_error.log_error (ibs_error.error, 'p_Article_01$changestate',
                        'Error in UPDATE m2_Article_01');
                RAISE;
            END;
            COMMIT WORK;

        ELSE
            l_retValue :=  c_INSUFFICIENT_RIGHTS;
        END IF;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            l_retValue := c_OBJECTNOTFOUND;
        WHEN TOO_MANY_ROWS THEN
            ibs_error.log_error (ibs_error.error, 'p_Article_01$changestate',
                'TOO_MANY_ROWS error in Select oldstate');
            RAISE; 
        WHEN OTHERS THEN
            ibs_error.log_error (ibs_error.error, 'p_Article_01$changestate',
                'OTHER error in Select oldstate');
            RAISE; 
    END;
    RETURN l_retValue;
EXCEPTION
    when OTHERS then
        ibs_error.log_error ( ibs_error.error, 'p_Article_01$changeState',
                              'Input: ' || ai_oid_s || ', ' || ai_userId || ', ' || ai_op || 
                              ', ' || ai_state);
    -- return error value:
    RETURN c_NOT_OK;
END p_Article_01$changeState;
/

show errors;


CREATE OR REPLACE FUNCTION p_Article_01$BOCopy
(
    oid                     RAW,
    userId                  INTEGER,
    newOid                  RAW
)
RETURN INTEGER
AS
    StoO_selcnt    INTEGER;
    StoO_error     INTEGER;
    StoO_rowcnt    INTEGER;
    StoO_errmsg    VARCHAR2(255);
    StoO_sqlstatus    INTEGER;
    c_NOT_OK                CONSTANT INTEGER := 0; -- operation not o.k.
    NOT_OK     NUMBER(10,0);
    ALL_RIGHT     NUMBER(10,0);
    retValue     NUMBER(10,0);
    l_discussionId   RAW(8);
    l_posNoPath      VARCHAR2 (254);
    l_copiedDisc     INTEGER;

BEGIN
    p_Article_01$BOCopy.NOT_OK :=  0;
    p_Article_01$BOCopy.ALL_RIGHT :=  1;

    p_Article_01$BOCopy.retValue :=  p_Article_01$BOCopy.NOT_OK;
    BEGIN
    StoO_error   := 0;
    StoO_rowcnt  := 0;

    -- retrieve the posNoPath of the entry
    SELECT posNoPath
    INTO l_posNoPath
    FROM ibs_Object
    WHERE oid = p_Article_01$BOCopy.newOid;
    
    -- retrieve the oid of the discussion
    SELECT oid
    INTO l_discussionId
    FROM ibs_Object
    WHERE tVersionId IN (16843521, 16845313)
        AND l_posNoPath LIKE posNoPath || '%';

    SELECT COUNT (*)
    INTO l_copiedDisc
    FROM ibs_Copy
    WHERE oldOid = l_discussionId;

    IF (l_copiedDisc=1) THEN
        SELECT newOid
        INTO l_discussionId
        FROM ibs_Copy
        WHERE oldOid = l_discussionId;
    END IF; 

    INSERT INTO m2_Article_01 (oid, content, discussionId)
        SELECT  p_Article_01$BOCopy.newOid, b.content, l_discussionid
        FROM m2_Article_01 b 
        WHERE b.oid = p_Article_01$BOCopy.oid;

    StoO_rowcnt := SQL%ROWCOUNT;
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error  := SQLCODE;
            StoO_errmsg := SQLERRM;
    END;

    IF  ( StoO_rowcnt >= 1) THEN
        p_Article_01$BOCopy.retValue :=  p_Article_01$BOCopy.ALL_RIGHT;
    END IF;
    RETURN p_Article_01$BOCopy.retValue;
END p_Article_01$BOCopy;
/

show errors;


/******************************************************************************
 * Get the CLOB value out of the DB. <BR>
 *
 * @input parameters:
 * @param   ai_oid_s              ID of the object (string representation).
 *
 * @output parameters:
 * @param   ao_content            Content of the actual object.
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_NOT_OK                 Some error occurred in the procedure.
 */
create or replace FUNCTION p_Article_01$getExtended 
( 
    -- input parameters:
    ai_oid_s                VARCHAR2,
    -- output parameters:
    ao_content              OUT CLOB 
)
return integer
as
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.

    -- local variables:
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_oid                   RAW (8);        -- the oid of the current object

-- body:
begin
    -- convert string representation to oid:
    p_stringToByte (ai_oid_s, l_oid);

    -- get the data:
    BEGIN
        select  content
        into    ao_content
        from    m2_Article_01
        where   oid = l_oid;
    EXCEPTION
        WHEN OTHERS THEN -- any error
            -- create error entry:
            l_ePos := 'get the data';
            RAISE;                      -- call common exception handler
    END;

    -- return the state value:
    RETURN  c_ALL_RIGHT;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid_s = ' || ai_oid_s ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Article_01$getExtended', l_eText);
        -- return error code:
        RETURN c_NOT_OK;
end p_Article_01$getExtended;
/

show errors;


EXIT;
