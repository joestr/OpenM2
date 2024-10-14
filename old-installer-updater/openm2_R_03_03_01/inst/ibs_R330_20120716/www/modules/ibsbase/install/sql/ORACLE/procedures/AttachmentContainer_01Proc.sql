/******************************************************************************
 * All stored procedures regarding the object table. <BR>
 * 
 *
 * @version     1.10.0001, 05.08.1999
 *
 * @author      Mario Stegbauer (MS)  980805
 *
 * <DT><B>Updates:</B>
 * <DD>HB 990805    Code cleaning.
 ******************************************************************************
 */

/******************************************************************************
 * Creates a new object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @userId                ID of the user who is creating the object.
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

CREATE OR REPLACE FUNCTION p_AC_01$create(
userId 		        INTEGER,
op 		            NUMBER,
tVersionId 	        NUMBER,
name 		        VARCHAR2,
containerId_s 	    VARCHAR2,
containerKind 	    NUMBER,
isLink 		        NUMBER,
linkedObjectId_s 	VARCHAR2,
description 	    VARCHAR2,
oid_s 		    OUT VARCHAR2)
RETURN INTEGER
AS
StoO_selcnt	            INTEGER;
StoO_error 	            INTEGER;
StoO_rowcnt	            INTEGER;
StoO_errmsg	            VARCHAR2(255);
StoO_sqlstatus	        INTEGER;
ALL_RIGHT 	            NUMBER(10,0);
INSUFFICIENT_RIGHTS 	NUMBER(10,0);
OBJECTNOTFOUND 	        NUMBER(10,0);
retValue 	            NUMBER(10,0);
dummy		            RAW (8);

BEGIN
	p_AC_01$create.ALL_RIGHT :=  1;
	p_AC_01$create.INSUFFICIENT_RIGHTS :=  2;

	p_AC_01$create.retValue :=  p_AC_01$create.ALL_RIGHT;
	BEGIN
	p_AC_01$create.retValue:=p_Object$performCreate(p_AC_01$create.userId,
	    p_AC_01$create.op,
	    p_AC_01$create.tVersionId,
	    p_AC_01$create.name,
	    p_AC_01$create.containerId_s,
	    p_AC_01$create.containerKind,
	    p_AC_01$create.isLink,
	    p_AC_01$create.linkedObjectId_s,
	    p_AC_01$create.description,
	    p_AC_01$create.oid_s,
	    p_AC_01$create.dummy);
	EXCEPTION
		WHEN OTHERS THEN
			StoO_error := SQLCODE;			
			StoO_errmsg := SQLERRM;
	END;

	RETURN p_AC_01$create.retValue;
	RETURN 0;

EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_AC_01$create',
    ', userId = ' || userId  ||
    ', op = ' || op  ||
    ', tVersionId = ' || tVersionId ||
    ', name = ' || name ||
    ', containerId_s = ' || containerId_s ||
    ', containerKind = ' || containerKind ||
    ', isLink = ' || isLink ||
    ', linkedObjectId_s = ' || linkedObjectId_s ||
    ', description = ' || description ||
    ', errorcode = ' || StoO_error ||
    ', errormessage = ' || StoO_errmsg);
END p_AC_01$create;
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
 * @param   @description        Description of the object.
 * @param   @showInNews         the showInNews flag
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
CREATE OR REPLACE FUNCTION  p_AC_01$change
(
    -- input parameters:
    oid_s           VARCHAR2,
    userId          INTEGER,
    op              INTEGER,
    name            VARCHAR2,
    validUntil      DATE,
    description     VARCHAR2,
    showInNews      INTEGER
)
RETURN INTEGER
AS
    -- exception values:
    StoO_error     INTEGER;
    StoO_errmsg    VARCHAR2(255);

    -- definitions:
    -- define return constants
    ALL_RIGHT INTEGER := 1; 
    INSUFFICIENT_RIGHTS INTEGER := 2;
    OBJECTNOTFOUND INTEGER := 3;
    -- define return values
    retValue INTEGER;       -- return value of this procedure
    dummy    RAW (8);
    BEGIN
    
    -- initialize return values
    p_AC_01$change.retValue := p_AC_01$change.ALL_RIGHT;

    -- perform the change of the object:
    p_AC_01$change.retValue := p_Object$performChange (p_AC_01$change.oid_s, 
                                                p_AC_01$change.userId, 
                                                p_AC_01$change.op, 
                                                p_AC_01$change.name, 
                                                p_AC_01$change.validUntil,
                                                p_AC_01$change.description,
                                                p_AC_01$change.showInNews,
                                                p_AC_01$change.dummy);
    COMMIT WORK;

    -- return the state value
    RETURN  p_AC_01$change.retValue;
    
EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;       
    ibs_error.log_error ( ibs_error.error, 'p_AC_01$change',                   
    ', oid_s = ' || oid_s ||  
    ', userId = ' || userId  || 
    ', op = ' || op || 
    ', name = ' || name || 
    ', validUntil = ' || validUntil || 
    ', description = ' || description ||             
    ', showInNews = ' || showInNews ||                 
    ', errorcode = ' || StoO_error ||    
    ', errormessage = ' || StoO_errmsg);                      
END p_AC_01$change;
/

show errors;

/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be changed.
 * @param   @userId                ID of the user who is creating the object.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 *
 * @output parameters:
 * @param   @state              The object's state.
 * @param   @tVersionId         ID of the object's type (correct version).
 * @param   @typeName           Name of the object's type.
 * @param   @name               Name of the object itself.
 * @param   @containerId        ID of the object's container.
 * @param   @containerKind      Kind of object/container relationship.
 * @param   @containerName      Name of the Container
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
 * @param   @checkOutUser       Oid of the user which checked out the object
 * @param   @checkOutUserName   name of the user which checked out the object,
 *                              is only set if this user has the right to read
 *                              the checkOut-User
 * 
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_AC_01$retrieve
(
    -- input parameters:
    ai_oid_s            VARCHAR2,
    ai_userId           INTEGER,
    ai_op               INTEGER,
    -- output parameters
    ao_state            OUT INTEGER,
    ao_tVersionId       OUT INTEGER,
    ao_typeName         OUT VARCHAR2,
    ao_name             OUT VARCHAR2,
    ao_containerId      OUT RAW,
    ao_containerName    OUT VARCHAR2,
    ao_containerKind    OUT INTEGER,
    ao_isLink           OUT NUMBER,
    ao_linkedObjectId   OUT RAW,
    ao_owner            OUT INTEGER,
    ao_ownerName        OUT VARCHAR2,
    ao_creationDate     OUT DATE,
    ao_creator          OUT INTEGER,
    ao_creatorName      OUT VARCHAR2,
    ao_lastChanged      OUT DATE,
    ao_changer          OUT INTEGER,
    ao_changerName      OUT VARCHAR2,
    ao_validUntil       OUT DATE,
    ao_description      OUT VARCHAR2,
    ao_showInNews       OUT INTEGER,
    ao_checkedOut          OUT NUMBER,
    ao_checkOutDate        OUT DATE,
    ao_checkOutUser        OUT INTEGER,
    ao_checkOutUserOid     OUT RAW,
    ao_checkOutUserName    OUT VARCHAR2,
    ao_masterId_s       OUT VARCHAR2, 
    ao_fileName         OUT VARCHAR2,
    ao_url              OUT VARCHAR2,
    ao_path             OUT VARCHAR2,
    ao_attachmentType   OUT INTEGER
)
RETURN INTEGER
AS
    -- constants:
    c_NOOID             CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    -- define return constants:
    c_ALL_RIGHT         CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND    CONSTANT INTEGER := 3;
    c_NOTOK             CONSTANT INTEGER := 0; 
    -- define return values:
    l_retValue          INTEGER := c_ALL_RIGHT;               -- return value of this procedure
    -- define local variables:
    l_oid               RAW (8);
    l_dummy             INTEGER;
    
    l_masterId          RAW(8) := c_NOOID;

-- body:
BEGIN
   
    -- retrieve the base object data:
    l_retValue := p_Object$performRetrieve (
            ai_oid_s, ai_userId, ai_op,
            ao_state , ao_tVersionId, ao_typeName, 
            ao_name , ao_containerId , ao_containerName, 
            ao_containerKind, ao_isLink, ao_linkedObjectId, 
            ao_owner, ao_ownerName, 
            ao_creationDate , ao_creator , ao_creatorName ,
            ao_lastChanged , ao_changer, ao_changerName,
            ao_validUntil, ao_description, ao_showInNews,
            ao_checkedOut, ao_checkOutDate, 
            ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName,
            l_oid);

    IF (l_retValue = c_ALL_RIGHT) --  all right with the basisobject
    THEN         
        BEGIN
            -- check if the container has an element or not ?
            SELECT  COUNT(*)
            INTO    l_dummy
            FROM    ibs_Object
            WHERE   containerId = l_oid 
                AND tVersionId = 16842833; -- typeName = attachment
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                l_dummy := 0;
            WHEN OTHERS THEN
                ibs_error.log_error (ibs_error.error, 'p_AC_01$retrieve',
                'check if there is an element: ' ||
                'OTHER error for oid ' || ao_containerId);
                RAISE;
        END;

        IF (l_dummy > 0) -- AttachmentContainer not empty 
        THEN
            l_dummy := 0;
            BEGIN
                -- search for actual master:
                SELECT  a.oid 
                INTO    l_masterId
                FROM    ibs_Attachment_01 a, ibs_Object o
                WHERE   o.containerId = l_oid 
                    AND a.isMaster = 1
                    AND o.oid = a.oid
                    AND o.state = 2;  -- ST_ACTIVE

                l_dummy := SQL%ROWCOUNT;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    l_dummy := 0;
                WHEN OTHERS THEN
                    ibs_error.log_error (ibs_error.error, 'p_AC_01$retrieve',
                    'search for actual master: ' ||
                    'OTHER error for container ' || l_oid);
                    RAISE;
            END;

            IF (l_dummy > 1)            -- more than one master found?
            THEN
                -- delete all isMaster entries in the attachmentcontainer
                l_dummy := 0;
            END IF; -- more than one master found

            IF (l_dummy = 1)    -- master was found?
            THEN
                BEGIN
                    -- get master data:
                    SELECT  filename, url, path, attachmentType
                    INTO    ao_fileName, ao_url, ao_path, ao_attachmentType
                    FROM    ibs_Attachment_01
                    WHERE   oid = l_masterId;
                EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                        ibs_error.log_error (ibs_error.error, 'p_AC_01$retrieve',
                        'get master data: ' ||
                        'NO_DATA_FOUND for oid ' || l_masterId);
                        RAISE;
                    WHEN OTHERS THEN
                        ibs_error.log_error (ibs_error.error, 'p_AC_01$retrieve',
                        'get master data: ' ||
                        'OTHER error for oid ' || l_masterId);
                        RAISE;
                END;
            ELSIF (l_dummy = 0) -- no master found
            THEN
                BEGIN
                    -- get new master (attachment with oldest oid):
                    SELECT  MIN (a.oid)
                    INTO    l_masterID
                    FROM    ibs_Attachment_01 a, ibs_Object o 
                    WHERE   o.containerId = l_oid
                        AND a.oid = o.oid
                        AND o.state = 2;  -- ST_ACTIVE;
                EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                        ibs_error.log_error (ibs_error.error, 'p_AC_01$retrieve',
                        'get new master: ' ||
                        'NO_DATA_FOUND for containerId ' || l_oid);
                        RAISE;
                    WHEN OTHERS THEN
                        ibs_error.log_error (ibs_error.error, 'p_AC_01$retrieve',
                        'get new master: ' ||
                        'OTHER error for containerId ' || l_oid);
                        RAISE;
                END;

                BEGIN
                    SELECT  filename, url, path, attachmentType
                    INTO    ao_fileName, ao_url, ao_path, ao_attachmentType
                    FROM    ibs_Attachment_01
                    WHERE   oid = l_masterId;
                EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                        ibs_error.log_error (ibs_error.error, 'p_AC_01$retrieve',
                        'get new master data: ' ||
                        'NO_DATA_FOUND for oid ' || l_masterId);
                        RAISE;
                    WHEN OTHERS THEN
                        ibs_error.log_error (ibs_error.error, 'p_AC_01$retrieve',
                        'get new master data: ' ||
                        'OTHER error for oid ' || l_masterId);
                        RAISE;
                END;
            ELSE                    -- container is empty and no master defined
                ao_fileName := 'kein Masterfile definiert';
                ao_url      := 'keine MasterUrl definiert';
            END IF; -- else container is empty and no master defined
        END IF;-- der Container ist nicht leer
        -- convert oid to string:
        p_byteToString (l_masterId, ao_masterId_s );
    END IF; --  all right with the basisobject

    COMMIT WORK;

    -- return the state value
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error ( ibs_error.error, 'p_AC_01$retrieve',                   
            ', oid_s = ' || ai_oid_s ||  
            ', userId = ' || ai_userId  || 
            ', op = ' || ai_op || 
            ', name = ' || ao_name || 
            ', validUntil = ' || ao_validUntil || 
            ', description = ' || ao_description ||
            ', showInNews = ' || ao_showInNews ||                         
            ', errorcode = ' || SQLCODE ||    
            ', errormessage = ' || SQLERRM);
        RETURN c_NOTOK;
END p_AC_01$retrieve;
/

show errors;

/******************************************************************************
 * Deletes an object and all its values (incl. rights check). <BR>
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
CREATE OR REPLACE FUNCTION p_AC_01$delete
(
    oid_s          VARCHAR2,
    userId         INTEGER,
    op             INTEGER
)
RETURN INTEGER
AS
    
    ---------------------------------------------------------------------------
    -- DEFINITIONS
    oid RAW (8);
    -- define return constants
    INSUFFICIENT_RIGHTS INTEGER := 2;
    ALL_RIGHT           INTEGER := 1;
    OBJECTNOTFOUND      INTEGER := 3;
    -- define right constants
    RIGHT_DELETE        INTEGER := 16;
    -- define return values
    retValue            INTEGER := p_AC_01$delete.ALL_RIGHT;    -- return value of this procedure
    rights              INTEGER := 0;           -- return value of called proc.
    -- define used variables
    containerId RAW (8);
--    posNoPath   VARCHAR2 (254);
    
    -- exception values:
    StoO_error      INTEGER;
    StoO_errmsg     VARCHAR2(255);
    dummy           INTEGER;
    l_count         INTEGER;

BEGIN
 
    p_stringToByte (p_AC_01$delete.oid_s, p_AC_01$delete.oid);
    p_AC_01$delete.dummy := 0;

    SELECT  COUNT (*)
    INTO    p_AC_01$delete.l_count
    FROM    ibs_Object
    WHERE   oid = p_AC_01$delete.oid;

    -- check if the object exists:
    IF (p_AC_01$delete.l_count > 0)                 -- object exists?
    THEN
         -- get container id of object
        SELECT  containerId
        INTO    p_AC_01$delete.containerId
        FROM    ibs_Object
        WHERE   oid = p_AC_01$delete.oid;
   
        -- get rights for this user
        p_AC_01$delete.retValue := p_Rights$checkRights (
             p_AC_01$delete.oid,                -- given object to be accessed by user
             p_AC_01$delete.containerId,        -- container of given object
             p_AC_01$delete.userId,             -- user_id
             p_AC_01$delete.op,                 -- required rights user must have to 
                                                -- delete object (operation to be perf.)
             p_AC_01$delete.rights);            -- returned value

        -- check if the user has the necessary rights

    IF (p_AC_01$delete.rights = p_AC_01$delete.op)  -- the user has the rights?
        THEN
            NULL;
/*
	    -- delete subsequent objects
            DELETE  ibs_Object
            WHERE   INSTR (posNoPath, p_AC_01$delete.posNoPath, 1, 1) = 1
                AND oid <> p_AC_01$delete.oid;

            -- delete references to the object
            DELETE  ibs_Object 
            WHERE   linkedObjectId = p_AC_01$delete.oid;

            -- delete object itself
            DELETE  ibs_Object 
            WHERE   oid = p_AC_01$delete.oid;
*/
        ELSE                            -- the user does not have the rights
            p_AC_01$delete.retValue := p_AC_01$delete.INSUFFICIENT_RIGHTS;   
        END IF; -- else the user does not have the rights
    ELSE                                -- the object does not exist
        -- set the return value with the error code:
        p_AC_01$delete.retValue := p_AC_01$delete.OBJECTNOTFOUND;
    END IF; -- else the object does not exist

    COMMIT WORK;

    -- return the state value
    RETURN  p_AC_01$delete.retValue;
 
EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;       
    ibs_error.log_error ( ibs_error.error, 'p_AC_01$delete',                   
    ', oid_s = ' || oid_s ||  
    ', userId = ' || userId  || 
    ', op = ' || op ||  
    ', errorcode = ' || StoO_error ||    
    ', errormessage = ' || StoO_errmsg);           
END p_AC_01$delete;
/

show errors;

/******************************************************************************
 * Copy an object and all its values. <BR>
 * 
 * @input parameters:
 * @param   @oid_s              ID of the object to be deleted.
 * @param   @userId                ID of the user who is deleting the object.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */
CREATE OR REPLACE FUNCTION p_AC_01$BOCopy
(
oid         RAW,
userId      INTEGER,
op          INTEGER,
newOid      OUT RAW
)
RETURN INTEGER
AS
StoO_selcnt     INTEGER;
StoO_error      INTEGER;
StoO_rowcnt     INTEGER;
StoO_errmsg     VARCHAR2(255);
StoO_sqlstatus  INTEGER;
retValue        INTEGER;
tVersionId      INTEGER;
ContainerId     RAW (8);
retVal          INTEGER;
name            VARCHAR2(255);
containerKind   INTEGER;
isLink          NUMBER(1,0);
linkedObjectId  RAW (8);
description     VARCHAR2(255);

BEGIN
    BEGIN
        StoO_rowcnt := 0;
        StoO_selcnt := 0;
        StoO_error  := 0;

        SELECT   tVersionId,  name,  containerId,  containerKind,  isLink,  linkedObjectId,
          description
        INTO p_AC_01$BOCopy.tVersionId, p_AC_01$BOCopy.name, p_AC_01$BOCopy.containerId, p_AC_01$BOCopy.containerKind, p_AC_01$BOCopy.isLink, p_AC_01$BOCopy.linkedObjectId,
         p_AC_01$BOCopy.description FROM ibs_Object 
        WHERE oid = p_AC_01$BOCopy.oid;
        StoO_rowcnt := SQL%ROWCOUNT;

        EXCEPTION
            WHEN TOO_MANY_ROWS THEN
                StoO_rowcnt := 2;
            WHEN OTHERS THEN
                StoO_rowcnt := 0;
                StoO_selcnt := 0;
                StoO_error := SQLCODE;
                StoO_errmsg := SQLERRM;
    END;

    BEGIN
    StoO_error   := 0;
    StoO_rowcnt  := 0;
    INSERT INTO ibs_Object (tVersionId, name, containerId, containerKind, isLink, linkedObjectId, owner, creator, changer, validUntil, description)VALUES (p_AC_01$BOCopy.tVersionId, p_AC_01$BOCopy.name, p_AC_01$BOCopy.containerId, 
           p_AC_01$BOCopy.containerKind, p_AC_01$BOCopy.isLink, p_AC_01$BOCopy.linkedObjectId, 
           p_AC_01$BOCopy.userId, p_AC_01$BOCopy.userId, p_AC_01$BOCopy.userId, 
           ADD_MONTHS(SYSDATE, 3), p_AC_01$BOCopy.description);
    StoO_rowcnt := SQL%ROWCOUNT;
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error  := SQLCODE;
            StoO_errmsg := SQLERRM;
    END;
    BEGIN
        StoO_rowcnt := 0;
        StoO_selcnt := 0;
        StoO_error  := 0;

        SELECT   oid
        INTO p_AC_01$BOCopy.newOid FROM ibs_Object 
        WHERE id = (
        SELECT  MAX(id)
         FROM ibs_Object 
        WHERE tVersionId = p_AC_01$BOCopy.tVersionId 
         AND name = p_AC_01$BOCopy.name 
         AND containerId = p_AC_01$BOCopy.containerId 
         AND containerKind = p_AC_01$BOCopy.containerKind 
         AND isLink = p_AC_01$BOCopy.isLink 
         AND linkedObjectId = p_AC_01$BOCopy.linkedObjectId);
        StoO_rowcnt := SQL%ROWCOUNT;

        EXCEPTION
            WHEN TOO_MANY_ROWS THEN
                StoO_rowcnt := 2;
            WHEN OTHERS THEN
                StoO_rowcnt := 0;
                StoO_selcnt := 0;
                StoO_error := SQLCODE;
                StoO_errmsg := SQLERRM;
    END;
COMMIT WORK;

    RETURN p_AC_01$BOCopy.retValue;
    RETURN 0;
EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_AC_01$BOCopy',
    ', userId = ' || userId  ||
    ', newOid = ' || newOid ||
    ', errorcode = ' || StoO_error ||
    ', errormessage = ' || StoO_errmsg);
END p_AC_01$BOCopy;
/

show errors;

exit;
