 /******************************************************************************
 * All stored procedures regarding the object table. <BR>
 *
 * @version     1.10.0001, 05.08.1999
 *
 * @author      Mario Stegbauer (MS)  980805
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
CREATE OR REPLACE FUNCTION p_SentObject_01$create
(
    -- common input parameters:
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_tVersionId           INTEGER,
    ai_name                 VARCHAR2,
    ai_containerId_s        VARCHAR2,
    ai_containerKind        INTEGER,
    ai_isLink               NUMBER,
    ai_linkedObjectId_s     VARCHAR2,
    ai_description          VARCHAR2,
    -- specific input parameters:
    ai_deleted              NUMBER,   
    ai_distributeId_s       VARCHAR2,
    ai_opDistribute         INTEGER,
    ai_senderRights         INTEGER,
    ai_freeze               NUMBER,
    -- commmon output parameters:
    ao_oid_s                OUT     VARCHAR2
)
RETURN INTEGER
AS
    -- definitions:
    -- constants:
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    -- variables:
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2;
    c_ALREADY_EXISTS        CONSTANT INTEGER := 21;
    l_retValue              INTEGER := c_NOT_OK;
    l_containerId           RAW (8) := c_NOOID; 
    l_linkedObjectId        RAW (8);
    l_distributeId          RAW (8);
    -- define local variables:
    l_oid                   RAW (8) := c_NOOID;
    l_rights                INTEGER;
    l_activities            VARCHAR2 (63);
    l_tabTVersionId         INTEGER;
    l_tabName               VARCHAR2 (63);
    l_tabDescription        VARCHAR2 (255);
    l_partOfOid_s           VARCHAR2 (18);
    l_containerId_s         VARCHAR2 (18) := ai_containerId_s;
    l_id                    INTEGER;
    -- exception values:
    l_dummy                 INTEGER;

BEGIN    
    p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);
    p_stringToByte (ai_distributeId_s, l_distributeId);


    BEGIN
        -- set the rights of the outboxcontainer:
        SELECT  outBox
        INTO    l_containerId
        FROM    ibs_Workspace
        WHERE   userId = ai_userId;
    EXCEPTION
        WHEN OTHERS THEN
            ibs_error.log_error ( ibs_error.error, 'p_SentObject_01$create',
            'Error in - get oid of oubox');
            RAISE;
    END;


    BEGIN
        -- compute the right to be added or set:
        SELECT  SUM (id)
        INTO    l_rights
        FROM    ibs_Operation
        WHERE   name IN ('new', 'read', 'view', 'change', 'delete',
                    'viewRights', 'setRights', 'createLink', 'distribute', 
                    'addElem', 'delElem', 'viewElems');
    EXCEPTION
        WHEN OTHERS THEN
            ibs_error.log_error ( ibs_error.error, 'p_SentObject_01$create',
            'Error in - get sum of all rights');
            RAISE;
    END;

    -- set new rights:
    p_rights$set (
        l_containerId, ai_userId, 
        l_rights,1);
    -- convert containerId to containerId_s:
    p_byteToString (l_containerId, l_containerId_s);

    -- create base object:
    l_retValue := p_Object$performCreate (
        ai_userId, ai_op, 
        ai_tVersionId, ai_name, 
        l_containerId_s, ai_containerKind, 
        ai_isLink, ai_linkedObjectId_s, 
        ai_description, ao_oid_s, 
        l_oid);

    IF (l_retValue = c_ALL_RIGHT)     -- object created successfully?
    THEN

        BEGIN
            -- create object type specific data:
            INSERT INTO ibs_SentObject_01 
                         (oid, distributeId, distributeTVersionId, 
                         distributeTypeName, distributeName, distributeIcon,
                         activities, deleted)
            SELECT  l_oid, l_distributeId, 
                    tVersionId, typeName, name, icon,
                    l_activities, ai_deleted
            FROM    ibs_Object
            WHERE   oid = l_distributeId;
    
            l_dummy := SQL%ROWCOUNT;
                -- check if insertion was performed properly:
            IF (l_dummy <= 0)        -- no row affected?
            THEN
                l_retValue := c_NOT_OK; -- set return value
            ELSE
                IF (ai_freeze  = 1)           -- freeze the distributed object?
                THEN 
                    -- start set rights of the distributed object
                    -- compute the right you want to add:
                    l_rights := ai_senderRights;
                    -- set the rights of the distributed Object
                    p_Rights$setRights (
                        l_distributeId, ai_userId, 
                        l_rights, 1);        
                END IF; -- if freeze the distributed object
            END IF; -- rowcount

        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error, 'p_SentObject_01$create',
                'Error in - insert into ibs_SentObject_01');
                RAISE;
        END;-- else insertion performed properly
    END IF; -- if object created successfully
            -- (p_SentObject_01$create.retValue = p_SentObject_01$create.ALL_RIGHT)
    COMMIT WORK;
    
    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_SentObject_01$create',
    ', userId = ' || ai_userId  ||
    ', op = ' || ai_op  ||
    ', tVersionId = ' || ai_tVersionId  ||
    ', name = ' || ai_name  ||
    ', containerId_s = ' || ai_containerId_s  ||
    ', containerKind = ' || ai_containerKind  ||
    ', isLink = ' || ai_isLink  ||
    ', linkedObjectId_s = ' || ai_linkedObjectId_s  ||
    ', description = ' || ai_description  ||
    ', deleted = ' || ai_deleted ||
    ', distributeId_s = ' || ai_distributeId_s ||
    ', opDistribute = ' || ai_opDistribute ||
    ', senderRights = ' || ai_senderRights ||
    ', freeze = ' || ai_freeze ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);
END p_SentObject_01$create;
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
CREATE OR REPLACE FUNCTION p_SentObject_01$change
(
    oid_s       VARCHAR2 ,
    userId      INTEGER ,
    op          NUMBER ,
    name        VARCHAR2 ,
    validUntil  DATE ,
    description VARCHAR2 ,
    showInNews  INTEGER , 
    distributeId_s     VARCHAR2 ,
    activities     VARCHAR2 ,
    deleted     NUMBER 
)
RETURN INTEGER
AS
    ALL_RIGHT     NUMBER(10,0);
    INSUFFICIENT_RIGHTS     NUMBER(10,0);
    OBJECTNOTFOUND     NUMBER(10,0);
    retValue     NUMBER(10,0);
    oid         RAW (8);
    distributeId     RAW (8);
    distributeTVersionId     NUMBER(10,0);
    distributeTypeName     VARCHAR2(63);
    distributeName     VARCHAR2(63);
    distributeIcon     VARCHAR2(63);
    dummy           RAW (8);

BEGIN
    p_SentObject_01$change.ALL_RIGHT :=  1;
    p_SentObject_01$change.INSUFFICIENT_RIGHTS :=  2;
    p_SentObject_01$change.OBJECTNOTFOUND :=  3;
    p_SentObject_01$change.retValue :=  p_SentObject_01$change.ALL_RIGHT;

    BEGIN
    p_stringToByte(p_SentObject_01$change.oid_s,
     p_SentObject_01$change.oid);
    EXCEPTION
        WHEN OTHERS THEN
        RAISE;
    END;
    BEGIN
    p_stringToByte(p_SentObject_01$change.distributeId_s,
     p_SentObject_01$change.distributeId);
    EXCEPTION
        WHEN OTHERS THEN
        RAISE;
    END;

    BEGIN
    p_SentObject_01$change.retValue:=p_Object$performChange(p_SentObject_01$change.oid_s,
     p_SentObject_01$change.userId,
     p_SentObject_01$change.op,
     p_SentObject_01$change.name,
     p_SentObject_01$change.validUntil,
     p_SentObject_01$change.description,
     p_SentObject_01$change.showInNews,
     p_SentObject_01$change.dummy);
    EXCEPTION
        WHEN OTHERS THEN
        RAISE;
    END;
    IF  ( p_SentObject_01$change.retValue = p_SentObject_01$change.ALL_RIGHT) THEN
    BEGIN
        BEGIN
            SELECT   tVersionId,  name,  icon
            INTO p_SentObject_01$change.distributeTVersionId, p_SentObject_01$change.distributeName, p_SentObject_01$change.distributeIcon FROM ibs_Object 
            WHERE oid = p_SentObject_01$change.distributeId;

        EXCEPTION
            WHEN OTHERS THEN
            RAISE;
        END;
        BEGIN
        UPDATE ibs_SentObject_01
        SET distributeId = p_SentObject_01$change.distributeId,
            distributeTVersionId = p_SentObject_01$change.distributeTVersionId,
            distributeName = p_SentObject_01$change.distributeName,
            distributeIcon = p_SentObject_01$change.distributeIcon,
            activities = p_SentObject_01$change.activities,
            deleted = p_SentObject_01$change.deleted        
        WHERE oid = p_SentObject_01$change.oid;

        EXCEPTION
            WHEN OTHERS THEN
            RAISE;
        END;
    END;
    END IF;

    COMMIT WORK;
    RETURN p_SentObject_01$change.retValue;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_SentObject_01$change',
    ', oid_s = ' || oid_s ||
    ', userId = ' || userId  ||
    ', op = ' || op ||
    ', name = ' || name ||
    ', validUntil = ' || validUntil  ||
    ', description = ' || description ||
    ', activities = ' || activities ||
    ', deleted = ' || deleted ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);

    RETURN 0;
END p_SentObject_01$change;
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
CREATE OR REPLACE FUNCTION p_SentObject_01$retrieve(
oid_s     VARCHAR2 ,
userId     INTEGER ,
op     NUMBER ,
state     OUT NUMBER,
tVersionId     OUT NUMBER,
typeName     OUT VARCHAR2,
name         OUT VARCHAR2,
containerId     OUT RAW,
containerName     OUT VARCHAR2,
containerKind     OUT NUMBER,
isLink         OUT NUMBER,
linkedObjectId     OUT RAW,
owner         OUT NUMBER,
ownerName     OUT VARCHAR2,
creationDate     OUT DATE,
creator     OUT NUMBER,
creatorName     OUT VARCHAR2,
lastChanged     OUT DATE,
changer     OUT NUMBER,
changerName     OUT VARCHAR2,
validUntil     OUT DATE,
description     OUT VARCHAR2,
showInNews      OUT INTEGER,
    ao_checkedOut          OUT NUMBER,
    ao_checkOutDate        OUT DATE,
    ao_checkOutUser        OUT INTEGER,
    ao_checkOutUserOid     OUT RAW,
    ao_checkOutUserName    OUT VARCHAR2,
distributeId_s     OUT VARCHAR2,
distributeTVersionId     OUT NUMBER,
distributeTypeName     OUT VARCHAR2,
distributeName     OUT VARCHAR2,
distributeIcon     OUT VARCHAR2,
activities     OUT VARCHAR2,
deleted     OUT NUMBER,
recipientContainerId_s     OUT VARCHAR2)
RETURN INTEGER
AS
c_NOOID         CONSTANT RAW (8) := hexToRaw ('0000000000000000');
StoO_selcnt    INTEGER;
StoO_error     INTEGER;
StoO_rowcnt    INTEGER;
StoO_errmsg    VARCHAR2(255);
StoO_sqlstatus    INTEGER;
ALL_RIGHT     NUMBER(10,0);
INSUFFICIENT_RIGHTS     NUMBER(10,0);
OBJECTNOTFOUND     NUMBER(10,0);
retValue     NUMBER(10,0);
oid         RAW (8);
recipientContainerId     RAW (8);
distributeId     RAW (8);

BEGIN
    p_SentObject_01$retrieve.ALL_RIGHT :=  1;
    p_SentObject_01$retrieve.INSUFFICIENT_RIGHTS :=  2;
    p_SentObject_01$retrieve.OBJECTNOTFOUND :=  3;

    p_SentObject_01$retrieve.retValue :=  p_SentObject_01$retrieve.ALL_RIGHT;

    p_SentObject_01$retrieve.recipientContainerId :=  p_SentObject_01$retrieve.c_NOOID;

    p_SentObject_01$retrieve.distributeId :=  p_SentObject_01$retrieve.c_NOOID;

    /*[SPCONV-ERR(57)]:BEGIN TRAN statement ignored*/
    NULL;
    BEGIN
    p_SentObject_01$retrieve.retValue:=p_Object$performRetrieve(p_SentObject_01$retrieve.oid_s,
     p_SentObject_01$retrieve.userId,
     p_SentObject_01$retrieve.op,
     p_SentObject_01$retrieve.state,
     p_SentObject_01$retrieve.tVersionId,
     p_SentObject_01$retrieve.typeName,
     p_SentObject_01$retrieve.name,
     p_SentObject_01$retrieve.containerId,
     p_SentObject_01$retrieve.containerName,
     p_SentObject_01$retrieve.containerKind,
     p_SentObject_01$retrieve.isLink,
     p_SentObject_01$retrieve.linkedObjectId,
     p_SentObject_01$retrieve.owner,
     p_SentObject_01$retrieve.ownerName,
     p_SentObject_01$retrieve.creationDate,
     p_SentObject_01$retrieve.creator,
     p_SentObject_01$retrieve.creatorName,
     p_SentObject_01$retrieve.lastChanged,
     p_SentObject_01$retrieve.changer,
     p_SentObject_01$retrieve.changerName,
     p_SentObject_01$retrieve.validUntil,
     p_SentObject_01$retrieve.description,
     p_SentObject_01$retrieve.showInNews,
        ao_checkedOut, ao_checkOutDate, 
        ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName,
     p_SentObject_01$retrieve.oid);
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error := SQLCODE;

            StoO_errmsg := SQLERRM;

    END;
    IF  ( p_SentObject_01$retrieve.retValue = p_SentObject_01$retrieve.ALL_RIGHT) THEN
    BEGIN
        BEGIN
            StoO_rowcnt := 0;
            StoO_selcnt := 0;
            StoO_error  := 0;

            SELECT   distributeId,  distributeTVersionId,  distributeTypeName,  distributeName,
              distributeIcon,  activities,  deleted
            INTO p_SentObject_01$retrieve.distributeId, p_SentObject_01$retrieve.distributeTVersionId, p_SentObject_01$retrieve.distributeTypeName, p_SentObject_01$retrieve.distributeName,
             p_SentObject_01$retrieve.distributeIcon, p_SentObject_01$retrieve.activities, p_SentObject_01$retrieve.deleted FROM ibs_SentObject_01 
            WHERE oid = p_SentObject_01$retrieve.oid;
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

            SELECT   oid
            INTO p_SentObject_01$retrieve.recipientContainerId FROM ibs_object 
            WHERE containerId = p_SentObject_01$retrieve.oid 
             AND 
            (tVersionId = 16849665);
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
        UPDATE ibs_recipient_01
        SET readDate = SYSDATE
        
        WHERE sentObjectId = p_SentObject_01$retrieve.oid 
         AND readDate IS NULL 
         AND recipientID = (
        SELECT  oid
         FROM ibs_user 
        WHERE id = p_SentObject_01$retrieve.userId);
        StoO_rowcnt := SQL%ROWCOUNT;
        EXCEPTION
            WHEN OTHERS THEN
                StoO_error  := SQLCODE;
                StoO_errmsg := SQLERRM;
        END;

        BEGIN
        p_byteToString(p_SentObject_01$retrieve.distributeId,
         p_SentObject_01$retrieve.distributeId_s);
        EXCEPTION
            WHEN OTHERS THEN
                StoO_error := SQLCODE;

                StoO_errmsg := SQLERRM;

        END;

        BEGIN
        p_byteToString(p_SentObject_01$retrieve.recipientContainerId,
         p_SentObject_01$retrieve.recipientContainerId_s);
        EXCEPTION
            WHEN OTHERS THEN
                StoO_error := SQLCODE;

                StoO_errmsg := SQLERRM;

        END;
    END;
    END IF;

    COMMIT WORK;
    RETURN p_SentObject_01$retrieve.retValue;
    RETURN 0;

EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_SentObject_01$retrieve',
    ', oid_s = ' || oid_s ||
    ', userId = ' || userId  ||
    ', op = ' || op ||
    ', errorcode = ' || StoO_error ||
    ', errormessage = ' || StoO_errmsg);
END p_SentObject_01$retrieve;
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
CREATE OR REPLACE FUNCTION p_Sentobject_01$delete(
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
oid         RAW (8);
INSUFFICIENT_RIGHTS     NUMBER(10,0);
ALL_RIGHT     NUMBER(10,0);
OBJECTNOTFOUND     NUMBER(10,0);
RIGHT_DELETE     NUMBER(10,0);
retValue     NUMBER(10,0);
rights         NUMBER(10,0);
containerId     RAW (8);
dummy        RAW (8);

BEGIN
    BEGIN
    p_stringToByte(p_Sentobject_01$delete.oid_s,
     p_Sentobject_01$delete.oid);
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error := SQLCODE;

            StoO_errmsg := SQLERRM;

    END;

    p_Sentobject_01$delete.ALL_RIGHT :=  1;
    p_Sentobject_01$delete.INSUFFICIENT_RIGHTS :=  2;
    p_Sentobject_01$delete.OBJECTNOTFOUND :=  3;
    p_Sentobject_01$delete.RIGHT_DELETE :=  16;

    p_Sentobject_01$delete.retValue :=  p_Sentobject_01$delete.ALL_RIGHT;
    p_Sentobject_01$delete.rights :=  0;

    /*[SPCONV-ERR(33)]:BEGIN TRAN statement ignored*/
    NULL;
    BEGIN
    p_Sentobject_01$delete.retValue:=p_Object$performDelete(p_Sentobject_01$delete.oid_s,
     p_Sentobject_01$delete.userId,
     p_Sentobject_01$delete.op,
     p_Sentobject_01$delete.dummy);
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error := SQLCODE;

            StoO_errmsg := SQLERRM;

    END;
    IF  ( p_Sentobject_01$delete.retValue = p_Sentobject_01$delete.ALL_RIGHT) THEN
    BEGIN
        BEGIN
        StoO_error   := 0;
        StoO_rowcnt  := 0;
        DELETE  ibs_sentobject_01 
            WHERE oid = p_Sentobject_01$delete.oid;
        StoO_rowcnt := SQL%ROWCOUNT;
        EXCEPTION
            WHEN OTHERS THEN
                StoO_error  := SQLCODE;
                StoO_errmsg := SQLERRM;
        END;
    END;
    END IF;

    COMMIT WORK;
    RETURN p_Sentobject_01$delete.retValue;
    RETURN 0;

EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_Sentobject_01$delete',
    ', oid_s = ' || oid_s ||
    ', userId = ' || userId  ||
    ', op = ' || op ||
    ', errorcode = ' || StoO_error ||
    ', errormessage = ' || StoO_errmsg);
END p_Sentobject_01$delete;
/

show errors;

exit;