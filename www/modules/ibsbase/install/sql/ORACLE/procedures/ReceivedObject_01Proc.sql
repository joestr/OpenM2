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
CREATE OR REPLACE FUNCTION p_ReceivedObject_01$create(
userId     INTEGER ,
op     INTEGER ,
tVersionId     INTEGER ,
name         VARCHAR2 ,
containerId_s     VARCHAR2 ,
containerKind     INTEGER ,
isLink         NUMBER ,
linkedObjectId_s     VARCHAR2 ,
description     VARCHAR2 ,
oid_s         OUT VARCHAR2)
RETURN INTEGER
AS
c_NOOID         CONSTANT RAW (8) := hexToRaw ('0000000000000000');
StoO_selcnt    INTEGER;
StoO_error     INTEGER;
StoO_rowcnt    INTEGER;
StoO_errmsg    VARCHAR2(255);
StoO_sqlstatus    INTEGER;
containerId     RAW (8);
oid         RAW (8);
ALL_RIGHT     INTEGER ;
INSUFFICIENT_RIGHTS     INTEGER; 
OBJECTNOTFOUND     INTEGER;
retValue     INTEGER;
returnValue     INTEGER;
rights         INTEGER;
containerId_s2    VARCHAR2 (18);

BEGIN
    p_ReceivedObject_01$create.containerId_s2 := p_ReceivedObject_01$create.containerId_s;
    p_ReceivedObject_01$create.ALL_RIGHT :=  1;
    p_ReceivedObject_01$create.INSUFFICIENT_RIGHTS :=  2;

    p_ReceivedObject_01$create.retValue :=  p_ReceivedObject_01$create.INSUFFICIENT_RIGHTS;

    /*[SPCONV-ERR(34)]:BEGIN TRAN statement ignored*/
    NULL;
    BEGIN
        StoO_rowcnt := 0;
        StoO_selcnt := 0;
        StoO_error  := 0;

        SELECT   inBox
        INTO p_ReceivedObject_01$create.containerId FROM ibs_Workspace 
        WHERE userId = p_ReceivedObject_01$create.userId;
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
        StoO_rowcnt := 0;
        StoO_selcnt := 0;
        StoO_error  := 0;

        SELECT   SUM(id)
        INTO p_ReceivedObject_01$create.rights FROM ibs_Operation 
        WHERE name  IN ('new', 'read', 'view', 'change', 'delete', 'viewRights', 
           'setRights', 'createLink', 'distribute', 'addElem', 'delElem', 
           'viewElems');
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
    p_rights$set(p_ReceivedObject_01$create.containerId,
     p_ReceivedObject_01$create.userId,
     p_ReceivedObject_01$create.rights,
     1);
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error := SQLCODE;

            StoO_errmsg := SQLERRM;

    END;

    BEGIN
    p_byteToString(p_ReceivedObject_01$create.containerId,
     p_ReceivedObject_01$create.containerId_s2);
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error := SQLCODE;

            StoO_errmsg := SQLERRM;

    END;
    BEGIN
    p_ReceivedObject_01$create.retValue:=p_Object$performCreate(p_ReceivedObject_01$create.userId,
     p_ReceivedObject_01$create.op,
     p_ReceivedObject_01$create.tVersionId,
     p_ReceivedObject_01$create.name,
     p_ReceivedObject_01$create.containerId_s2,
     p_ReceivedObject_01$create.containerKind,
     p_ReceivedObject_01$create.isLink,
     p_ReceivedObject_01$create.linkedObjectId_s,
     p_ReceivedObject_01$create.description,
     p_ReceivedObject_01$create.oid_s,
     p_ReceivedObject_01$create.oid);
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error := SQLCODE;

            StoO_errmsg := SQLERRM;

    END;
    IF  ( p_ReceivedObject_01$create.retValue = p_ReceivedObject_01$create.ALL_RIGHT) THEN
    BEGIN
        BEGIN
        StoO_error   := 0;
        StoO_rowcnt  := 0;
        INSERT INTO ibs_ReceivedObject_01 (oid, distributedId, distributedTVersionId, distributedTypeName, distributedName, distributedIcon, activities, sentObjectId, senderFullName)
        VALUES (p_ReceivedObject_01$create.oid, p_ReceivedObject_01$create.c_NOOID, 0, NULL, 'no defined', 
               'icon.gif', 'no defined', p_ReceivedObject_01$create.c_NOOID, 'no defined');
        StoO_rowcnt := SQL%ROWCOUNT;
        EXCEPTION
            WHEN OTHERS THEN
                StoO_error  := SQLCODE;
                StoO_errmsg := SQLERRM;
        END;
    END;
    END IF;

    COMMIT WORK;
    RETURN p_ReceivedObject_01$create.retValue;
    RETURN 0;

EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, ' p_ReceivedObject_01$create',
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
END p_ReceivedObject_01$create;
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
CREATE OR REPLACE FUNCTION p_ReceivedObject_01$change
(
    oid_s                   VARCHAR2,
    userId                  INTEGER,
    op                      INTEGER,
    name                    VARCHAR2,
    validUntil              DATE,
    description             VARCHAR2,
    showInNews              INTEGER,
    showInForm              INTEGER,
    distributedId_s         OUT VARCHAR2,
    distributedTVersionId   OUT NUMBER,
    distributedTypeName     OUT VARCHAR2,
    distributedName         OUT VARCHAR2,
    distributedIcon         OUT VARCHAR2,
    activities              OUT VARCHAR2,
    sentObjectId_s          OUT VARCHAR2
)
RETURN INTEGER
AS
    -- constants
    ALL_RIGHT               NUMBER(10,0);
    INSUFFICIENT_RIGHTS     NUMBER(10,0);
    OBJECTNOTFOUND          NUMBER(10,0);
    -- locals
    retValue                NUMBER(10,0);
    senderFullName          VARCHAR2(63);
    senderuserId            NUMBER(10,0);
    recipientContainerId_s  VARCHAR2(18);
    oid                     RAW (8);
    sentObjectId            RAW (8);
    distributedId           RAW (8);
    dummy                   RAW (8);

BEGIN
    p_ReceivedObject_01$change.ALL_RIGHT :=  1;
    p_ReceivedObject_01$change.INSUFFICIENT_RIGHTS :=  2;
    p_ReceivedObject_01$change.OBJECTNOTFOUND :=  3;

    p_ReceivedObject_01$change.retValue :=  p_ReceivedObject_01$change.ALL_RIGHT;

    BEGIN
    p_stringToByte(p_ReceivedObject_01$change.oid_s,
     p_ReceivedObject_01$change.oid);
    EXCEPTION
        WHEN OTHERS THEN
        RAISE;
    END;
    BEGIN
    p_stringToByte(p_ReceivedObject_01$change.sentObjectId_s,
     p_ReceivedObject_01$change.sentObjectId);
    EXCEPTION
        WHEN OTHERS THEN
        RAISE;
    END;
    BEGIN
    p_stringToByte(p_ReceivedObject_01$change.distributedId_s,
     p_ReceivedObject_01$change.distributedId);
    EXCEPTION
        WHEN OTHERS THEN
        RAISE;
    END;

    BEGIN
    p_ReceivedObject_01$change.retValue:=p_Object$performChange(p_ReceivedObject_01$change.oid_s,
     p_ReceivedObject_01$change.userId,
     p_ReceivedObject_01$change.op,
     p_ReceivedObject_01$change.name,
     p_ReceivedObject_01$change.validUntil,
     p_ReceivedObject_01$change.description,
     p_ReceivedObject_01$change.showInNews,     
     p_ReceivedObject_01$change.dummy);
    EXCEPTION
        WHEN OTHERS THEN
        RAISE;
    END;
    IF  ( p_ReceivedObject_01$change.retValue = p_ReceivedObject_01$change.ALL_RIGHT) THEN
    BEGIN
        UPDATE ibs_ReceivedObject_01
        SET distributedId = p_ReceivedObject_01$change.distributedId,
        distributedTVersionId = p_ReceivedObject_01$change.distributedTVersionId,
        distributedName = p_ReceivedObject_01$change.distributedName,
        distributedIcon = p_ReceivedObject_01$change.distributedIcon,
        sentobjectId = p_ReceivedObject_01$change.sentobjectId
        WHERE oid = p_ReceivedObject_01$change.oid;
    EXCEPTION
        WHEN OTHERS THEN
        RAISE;
    END;
    END IF;
    RETURN p_ReceivedObject_01$change.retValue;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_ReceivedObject_01$change',
    ', oid_s = ' || oid_s ||
    ', userId = ' || userId  ||
    ', op = ' || op ||
    ', name = ' || name ||
    ', validUntil = ' || validUntil  ||
    ', description = ' || description ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);

    RETURN 0;
END p_ReceivedObject_01$change;
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
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_ReceivedObject_01$retrieve
(
    -- input parameters:
    oid_s          VARCHAR2,
    userId           INTEGER,
    op             INTEGER,
    -- output parameters
    state          OUT  INTEGER,
    tVersionId     OUT  INTEGER,
    typeName       OUT  VARCHAR2,
    name           OUT  VARCHAR2,
    containerId    OUT  RAW,
    containerName  OUT  VARCHAR2,
    containerKind  OUT  INTEGER,
    isLink         OUT  NUMBER,
    linkedObjectId OUT  RAW, 
    owner          OUT  INTEGER,
    ownerName      OUT  VARCHAR2,
    creationDate   OUT  DATE,
    creator        OUT  INTEGER,
    creatorName    OUT  VARCHAR2,
    lastChanged    OUT  DATE,
    changer        OUT  INTEGER,
    changerName    OUT  VARCHAR2,
    validUntil     OUT  DATE,
    description    OUT  VARCHAR2,
    showInNews     OUT  INTEGER,
    ao_checkedOut          OUT NUMBER,
    ao_checkOutDate        OUT DATE,
    ao_checkOutUser        OUT INTEGER,
    ao_checkOutUserOid     OUT RAW,
    ao_checkOutUserName    OUT VARCHAR2,
    distributedId_s         OUT  VARCHAR2,
    distributedTVersionId   OUT  INTEGER,
    distributedTypeName     OUT  VARCHAR2,
    distributedName         OUT  VARCHAR2,    
    distributedIcon         OUT  VARCHAR2,
    activities              OUT  VARCHAR2,
    recipientContainerId_s  OUT  VARCHAR2,
    senderFullName          OUT  VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_NOOID         CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    -- exception values:
    StoO_error     INTEGER;
    StoO_errmsg    VARCHAR2(255);
    -- define return constants:
    ALL_RIGHT               INTEGER := 1;
    INSUFFICIENT_RIGHTS     INTEGER := 2;
    OBJECTNOTFOUND          INTEGER := 3;
    -- define return values:
    retValue                INTEGER;
    -- define local variables:
    oid RAW (8);
    recipientContainerId    RAW (8) := p_ReceivedObject_01$retrieve.c_NOOID;        -- used for "Reiter" definition
    sentObjectId            RAW (8) := p_ReceivedObject_01$retrieve.c_NOOID;        -- used for "Reiter" definition
    distributedId           RAW (8) := p_ReceivedObject_01$retrieve.c_NOOID;        -- used for "Reiter" definition
    recipientId             RAW (8) := p_ReceivedObject_01$retrieve.c_NOOID;        -- used for "Reiter" definition
    
BEGIN

    -- initialize return values:
    p_ReceivedObject_01$retrieve.retValue := p_ReceivedObject_01$retrieve.ALL_RIGHT;

    -- retrieve the base object data:
    p_ReceivedObject_01$retrieve.retValue := p_Object$performRetrieve (
                p_ReceivedObject_01$retrieve.oid_s, p_ReceivedObject_01$retrieve.userId, 
                p_ReceivedObject_01$retrieve.op, p_ReceivedObject_01$retrieve.state,
                p_ReceivedObject_01$retrieve.tVersionId, p_ReceivedObject_01$retrieve.typeName, 
                p_ReceivedObject_01$retrieve.name, p_ReceivedObject_01$retrieve.containerId, 
                p_ReceivedObject_01$retrieve.containerName, p_ReceivedObject_01$retrieve.containerKind,
                p_ReceivedObject_01$retrieve.isLink, p_ReceivedObject_01$retrieve.linkedObjectId, 
                p_ReceivedObject_01$retrieve.owner, p_ReceivedObject_01$retrieve.ownerName, 
                p_ReceivedObject_01$retrieve.creationDate, p_ReceivedObject_01$retrieve.creator, 
                p_ReceivedObject_01$retrieve.creatorName, p_ReceivedObject_01$retrieve.lastChanged,
                p_ReceivedObject_01$retrieve.changer, p_ReceivedObject_01$retrieve.changerName,
                p_ReceivedObject_01$retrieve.validUntil, p_ReceivedObject_01$retrieve.description, 
                p_ReceivedObject_01$retrieve.showInNews,     
                ao_checkedOut, ao_checkOutDate, 
                ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName,
                p_ReceivedObject_01$retrieve.oid);

        IF (p_ReceivedObject_01$retrieve.retValue = p_ReceivedObject_01$retrieve.ALL_RIGHT)
        THEN
            SELECT  
                distributedId, distributedTVersionId,
                distributedTypeName, distributedName,
                distributedIcon, activities,
                sentObjectId, senderFullName
            INTO
                p_ReceivedObject_01$retrieve.distributedId, p_ReceivedObject_01$retrieve.distributedTVersionId,
                p_ReceivedObject_01$retrieve.distributedTypeName, p_ReceivedObject_01$retrieve.distributedName,
                p_ReceivedObject_01$retrieve.distributedIcon, p_ReceivedObject_01$retrieve.activities,
                p_ReceivedObject_01$retrieve.sentObjectId, p_ReceivedObject_01$retrieve.senderFullName
            FROM ibs_ReceivedObject_01
            WHERE oid = p_ReceivedObject_01$retrieve.oid;

            UPDATE ibs_Recipient_01 
            SET readDate = SYSDATE
            WHERE sentObjectId = p_ReceivedObject_01$retrieve.sentObjectId 
            AND readDate IS NULL
            AND recipientID = (
                               SELECT oid 
                               FROM ibs_user 
                               WHERE id = p_ReceivedObject_01$retrieve.userId
                               );

            -- read out the recipientcontainerOid
            SELECT  oid
            INTO    p_ReceivedObject_01$retrieve.recipientContainerId
            FROM    ibs_object
            WHERE   containerId = p_ReceivedObject_01$retrieve.sentObjectId 
                AND (tVersionId = 16849665 ); -- recipientlist

            -- convert sendedObjectId to output
            p_byteToString (p_ReceivedObject_01$retrieve.distributedId, p_ReceivedObject_01$retrieve.distributedId_s);

            -- convert sendedObjectId to output
            p_byteToString (p_ReceivedObject_01$retrieve.recipientContainerId,
                        p_ReceivedObject_01$retrieve.recipientContainerId_s);

        END IF;  -- if retValue = ALL_RIGHT;

COMMIT WORK;
    -- return the state value
    RETURN  p_ReceivedObject_01$retrieve.retValue;

EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;    
    ibs_error.log_error ( ibs_error.error, 'p_ReceivedObject_01$retrieve',                   
    ', oid_s = ' || oid_s ||  
    ', userId = ' || userId  || 
    ', op = ' || op || 
    ', errorcode = ' || StoO_error ||
    ', errormessage = ' || StoO_errmsg);    
END p_ReceivedObject_01$retrieve;
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
CREATE OR REPLACE FUNCTION p_ReceivedObject_01$delete(
oid_s     VARCHAR2 ,
userId     INTEGER ,
op     NUMBER )
RETURN INTEGER
AS
c_NOOID         CONSTANT RAW (8) := hexToRaw ('0000000000000000');
StoO_selcnt    INTEGER;
StoO_error     INTEGER;
StoO_rowcnt    INTEGER;
StoO_errmsg    VARCHAR2(255);
StoO_sqlstatus    INTEGER;
oid         RAW (8);
INSUFFICIENT_RIGHTS     NUMBER(10,0);
ALL_RIGHT     NUMBER(10,0);
OBJECTNOTFOUND     NUMBER(10,0);
RIGHT_DELETE     NUMBER(10,0);
retValue     NUMBER(10,0);
rights     NUMBER(10,0);
containerId     RAW (8);
isMaster     NUMBER(1,0);
masterId     RAW(8);
dummy        RAW(8);

BEGIN
    BEGIN
    p_stringToByte(p_ReceivedObject_01$delete.oid_s,
     p_ReceivedObject_01$delete.oid);
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error := SQLCODE;

            StoO_errmsg := SQLERRM;

    END;

    p_ReceivedObject_01$delete.ALL_RIGHT :=  1;
    p_ReceivedObject_01$delete.INSUFFICIENT_RIGHTS :=  2;
    p_ReceivedObject_01$delete.OBJECTNOTFOUND :=  3;
    p_ReceivedObject_01$delete.RIGHT_DELETE :=  16;

    p_ReceivedObject_01$delete.retValue :=  p_ReceivedObject_01$delete.ALL_RIGHT;
    p_ReceivedObject_01$delete.rights :=  0;

    p_ReceivedObject_01$delete.masterId :=  p_ReceivedObject_01$delete.c_NOOID;

    /*[SPCONV-ERR(39)]:BEGIN TRAN statement ignored*/
    NULL;
    BEGIN
    p_ReceivedObject_01$delete.retValue:=p_Object$performDelete(p_ReceivedObject_01$delete.oid_s,
     p_ReceivedObject_01$delete.userId,
     p_ReceivedObject_01$delete.op,
     p_ReceivedObject_01$delete.dummy);
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error := SQLCODE;

            StoO_errmsg := SQLERRM;

    END;
    IF  ( p_ReceivedObject_01$delete.retValue = p_ReceivedObject_01$delete.ALL_RIGHT) THEN
    BEGIN
        BEGIN
        StoO_error   := 0;
        StoO_rowcnt  := 0;
        DELETE  ibs_ReceivedObject_01 
            WHERE oid = p_ReceivedObject_01$delete.oid;
        StoO_rowcnt := SQL%ROWCOUNT;
        EXCEPTION
            WHEN OTHERS THEN
                StoO_error  := SQLCODE;
                StoO_errmsg := SQLERRM;
        END;
    END;
    END IF;

    COMMIT WORK;
    RETURN p_ReceivedObject_01$delete.retValue;
    RETURN 0;

EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_ReceivedObject_01$delete',
    ', oid_s = ' || oid_s ||
    ', userId = ' || userId  ||
    ', op = ' || op ||
    ', errorcode = ' || StoO_error ||
    ', errormessage = ' || StoO_errmsg);
END p_ReceivedObject_01$delete;
/

show errors;

exit;