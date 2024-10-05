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
CREATE OR REPLACE FUNCTION p_Recipient_01$create
(
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_tVersionId           INTEGER,
    ai_name                 VARCHAR2,
    ai_containerId_s        VARCHAR2,
    ai_containerKind        INTEGER,
    ai_isLink               NUMBER,
    ai_linkedObjectId_s     VARCHAR2,
    ai_description          VARCHAR2,
    ai_recipientOid_s       VARCHAR2,
    ai_sentObjectOid_s      VARCHAR2,
    ai_recipientRights      INTEGER,
    ai_frooze               NUMBER,
    ai_oid_s                OUT VARCHAR2
)
RETURN INTEGER
AS
    -- constants
    c_NOOID                 CONSTANT RAW (8) := '0000000000000000';
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3;
    -- variables
    l_oid                   RAW (8) := c_NOOID;
    l_containerId           RAW (8) := c_NOOID;
    l_linkedObjectId        RAW (8) := c_NOOID;
    l_sentObjectOid         RAW (8) := c_NOOID;
    l_recipientOid          RAW (8) := c_NOOID;
    l_inboxOid              RAW (8) := c_NOOID;
    l_inboxOid_s            VARCHAR2 (18);
    l_recipientName         VARCHAR2 (63);
    l_returnValue           INTEGER;
    l_returnFlag            INTEGER;
    l_rId                   INTEGER;
    l_ReceivedObjecttVersionId  INTEGER := 16864769;
    l_distributedId         RAW (8) := c_NOOID;
    l_distributedTypeName   VARCHAR2 (63);
    l_distributedName       VARCHAR2 (63);
    l_distributedIcon       VARCHAR2 (63);
    l_activities            VARCHAR2 (63);
    l_sentObjectId          INTEGER;
    l_senderFullName        VARCHAR2 (63);
    l_distributedTVersionId INTEGER;
    l_retValue              INTEGER := c_NOT_OK;
    l_rights                INTEGER := 0;
    l_ContainerId_s         VARCHAR2 (18);

BEGIN
  p_stringToByte( ai_linkedObjectId_s, l_linkedObjectId );
  p_stringToByte( ai_containerId_s, l_containerId );
  p_stringToByte( ai_recipientOid_s, l_recipientOid );
  p_stringToByte( ai_sentObjectOid_s, l_sentObjectOid );

  SELECT    oid
  INTO      l_containerId
  FROM      ibs_Object
  WHERE     containerId = l_sentObjectOid
    AND     tVersionId = 16849665; -- Empfänger

  SELECT    fullname, id
  INTO      l_recipientName, l_rId
  FROM      ibs_User
  WHERE     oid = l_recipientOid;

  p_byteToString( l_containerId, l_ContainerId_s );

  l_retValue := p_Object$performCreate(
    ai_userId, ai_op, ai_tVersionId, ai_name, l_ContainerId_s,
    ai_containerKind, ai_isLink, ai_linkedObjectId_s, ai_description, 
    ai_oid_s, l_oid );

debug (' after p_Objectcreate retVal = ' || l_retValue);

  IF (l_retValue = c_ALL_RIGHT) 
  THEN
    SELECT  distributeId, distributeTVersionId, distributeTypeName, distributeName,
            distributeIcon, activities
    INTO    l_distributedId, l_distributedTVersionId,
            l_distributedTypeName, l_distributedName,
            l_distributedIcon, l_activities
    FROM ibs_SentObject_01
    WHERE oid = l_sentObjectOid;

    SELECT fullname, id
    INTO l_recipientName, l_rId
    FROM ibs_User
    WHERE oid = l_recipientOid;

    INSERT INTO ibs_Recipient_01
                ( oid, recipientId, recipientName, readDate, sentObjectId, deleted )
    VALUES      ( l_oid, l_recipientOid, l_recipientName, null, l_sentObjectOid, 0 );

    p_Rights$AddRights(l_distributedId, l_rId, ai_recipientRights, 1);

    SELECT  inbox
    INTO    l_inboxOid
    FROM    ibs_Workspace
    WHERE   userId = l_rId;

    p_byteToString(l_inboxOid, l_inboxOid_s );

    SELECT  SUM (id)
    INTO    l_rights
    FROM    ibs_Operation
    WHERE name IN ('new', 'read', 'view','change','delete', 'viewRights','setRights',
                   'createLink','distribute','addElem', 'delElem','viewElems');

    p_rights$set( l_inboxOid, l_rId, l_rights, 1);
  END IF;


debug ('p_Object$performCreate (' || 
    l_rId || ',' ||
    ai_op || ',' ||
    l_ReceivedObjecttVersionId || ',' ||
    ai_name || ',' ||
    l_inboxOid_s || ',' ||
    ai_containerKind || ',' ||
    ai_isLink || ',' ||
    ai_linkedObjectId_s || ',' ||
    ai_description || ',' ||
    ai_oid_s || ',' ||
    l_oid || ');');
    
  l_retValue := p_Object$performCreate(
                l_rId, ai_op, l_ReceivedObjecttVersionId, ai_name,
                l_inboxOid_s, ai_containerKind, ai_isLink, ai_linkedObjectId_s,
                ai_description, ai_oid_s, l_oid );

debug (' after p_Objectcreate II retVal = ' || l_retValue);

  IF (l_retValue = c_ALL_RIGHT) 
  THEN
    SELECT  u.fullname
    INTO    l_senderFullName
    FROM    ibs_Object o, ibs_user u
    WHERE   o.oid = l_sentObjectOid
      AND   u.id = o.owner;

    INSERT INTO ibs_ReceivedObject_01
                (oid, distributedId, distributedTVersionId, distributedTypeName, distributedName, 
                distributedIcon, activities, sentObjectId, senderFullName )
    VALUES  (l_oid, l_distributedId, l_distributedTVersionId, l_distributedTypeName, 
                l_distributedName, l_distributedIcon, l_activities, l_sentObjectOid,
                l_senderFullName);
    END IF;
       
COMMIT WORK;
  RETURN l_retValue;
EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error ( ibs_error.error, 'p_Recipient_01$create',
                          'userId: ' || ai_userId ||
                              ', op: ' || ai_op ||
                              ', tVersionId: ' || ai_tVersionId ||
                              ', name: ' || ai_name ||
                              ', containerId_s: ' || ai_containerId_s ||
                              ', containerKind: ' || ai_containerKind ||
                              ', isLink: ' || ai_isLink ||
                              ', linkedObjectId_s: ' || ai_linkedObjectId_s ||
                              ', description: ' || ai_description ||
                              ', recipientOid_s: ' || ai_recipientOid_s ||
                              ', sentObjectOid_s: ' || ai_sentObjectOid_s ||
                              ', recipientRights: ' || ai_recipientRights ||
                              ', frooze: ' || ai_frooze ||
                              ', oid_s: ' || ai_oid_s ||
                              ', sqlcode: ' || SQLCODE ||
                              ', sqlerrm: ' || SQLERRM );
    RETURN c_NOT_OK;
END;
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
CREATE OR REPLACE FUNCTION p_Recipient_01$change(
oid_s     VARCHAR2 ,
userId     INTEGER ,
op     NUMBER ,
name     VARCHAR2 ,
validUntil     DATE ,
description     VARCHAR2 ,
showInNews INTEGER  
)
RETURN INTEGER
AS
StoO_selcnt    INTEGER;
StoO_error     INTEGER;
StoO_rowcnt    INTEGER;
StoO_errmsg    VARCHAR2(255);
StoO_sqlstatus    INTEGER;
ALL_RIGHT     NUMBER(10,0);
INSUFFICIENT_RIGHTS     NUMBER(10,0);
OBJECTNOTFOUND     NUMBER(10,0);
retValue     NUMBER(10,0);
oid     RAW (8);
BEGIN
    p_Recipient_01$change.ALL_RIGHT :=  1;
    p_Recipient_01$change.INSUFFICIENT_RIGHTS :=  2;
    p_Recipient_01$change.OBJECTNOTFOUND :=  3;

    p_Recipient_01$change.retValue :=  p_Recipient_01$change.ALL_RIGHT;

    ibs_error.log_error ( ibs_error.warning, 'p_Recipient_01$change',
                          'This function does nothing' );
    RETURN p_Recipient_01$change.retValue;
exception
  when OTHERS then
    ibs_error.log_error ( ibs_error.error, 'p_Recipient_01$change',
                          'oid_s: ' || oid_s ||
                          ', userId: ' || userId ||
                          ', op: ' || op ||
                          ', name: ' || name ||
                          ', validUntil: ' || validUntil ||
                          ', description: ' || description ||
                          ', showInNews: ' || showInNews );                          
    return 0;
END p_Recipient_01$change;
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
CREATE OR REPLACE FUNCTION p_Recipient_01$retrieve(
oid_s     VARCHAR2 ,
userId     INTEGER ,
op     NUMBER ,
state     OUT NUMBER,
tVersionId     OUT NUMBER,
typeName     OUT VARCHAR2,
name     OUT VARCHAR2,
containerId     OUT RAW,
containerName     OUT VARCHAR2,
containerKind     OUT NUMBER,
isLink     OUT NUMBER,
linkedObjectId     OUT RAW,
owner     OUT NUMBER,
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
 
recipientOid_s     OUT VARCHAR2,
recipientName     OUT VARCHAR2,
recipientPosition     OUT VARCHAR2,
recipientEmail     OUT VARCHAR2,
recipientTitle     OUT VARCHAR2,
recipientCompany     OUT VARCHAR2)
RETURN INTEGER
AS
StoO_selcnt    INTEGER;
StoO_error     INTEGER;
StoO_rowcnt    INTEGER;
StoO_errmsg    VARCHAR2(255);
StoO_sqlstatus    INTEGER;
ALL_RIGHT     NUMBER(10,0);
INSUFFICIENT_RIGHTS     NUMBER(10,0);
OBJECTNOTFOUND     NUMBER(10,0);
retValue     NUMBER(10,0);
oid     RAW (8);
recipientOid     RAW (8);

BEGIN
    p_Recipient_01$retrieve.ALL_RIGHT :=  1;
    p_Recipient_01$retrieve.INSUFFICIENT_RIGHTS :=  2;
    p_Recipient_01$retrieve.OBJECTNOTFOUND :=  3;

    p_Recipient_01$retrieve.retValue :=  p_Recipient_01$retrieve.ALL_RIGHT;

    p_Recipient_01$retrieve.recipientPosition :=  'undefined';
    p_Recipient_01$retrieve.recipientEmail :=  'undefined';
    p_Recipient_01$retrieve.recipientTitle :=  'undefined';

    p_Recipient_01$retrieve.recipientCompany :=  'undefined';

    /*[SPCONV-ERR(61)]:BEGIN TRAN statement ignored*/
    BEGIN
    p_Recipient_01$retrieve.retValue:=p_Object$performRetrieve(p_Recipient_01$retrieve.oid_s,
     p_Recipient_01$retrieve.userId, p_Recipient_01$retrieve.op,
     p_Recipient_01$retrieve.state, p_Recipient_01$retrieve.tVersionId,
     p_Recipient_01$retrieve.typeName, p_Recipient_01$retrieve.name,
     p_Recipient_01$retrieve.containerId, p_Recipient_01$retrieve.containerName,
     p_Recipient_01$retrieve.containerKind, p_Recipient_01$retrieve.isLink,
     p_Recipient_01$retrieve.linkedObjectId, p_Recipient_01$retrieve.owner,
     p_Recipient_01$retrieve.ownerName, p_Recipient_01$retrieve.creationDate,
     p_Recipient_01$retrieve.creator, p_Recipient_01$retrieve.creatorName,
     p_Recipient_01$retrieve.lastChanged, p_Recipient_01$retrieve.changer,
     p_Recipient_01$retrieve.changerName, p_Recipient_01$retrieve.validUntil,
     p_Recipient_01$retrieve.description, p_Recipient_01$retrieve.showInNews, 
        ao_checkedOut, ao_checkOutDate, 
        ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName,
     p_Recipient_01$retrieve.oid);
     
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error := SQLCODE;
            StoO_errmsg := SQLERRM;
    END;
    IF  ( p_Recipient_01$retrieve.retValue = p_Recipient_01$retrieve.ALL_RIGHT) THEN
    BEGIN
        BEGIN
            StoO_rowcnt := 0;
            StoO_selcnt := 0;
            StoO_error  := 0;

            SELECT   recipientId
            INTO p_Recipient_01$retrieve.recipientOid FROM ibs_Recipient_01 
            WHERE oid = p_Recipient_01$retrieve.oid;
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

            SELECT   fullname
            INTO p_Recipient_01$retrieve.recipientName FROM ibs_User 
            WHERE oid = p_Recipient_01$retrieve.recipientOid;
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
        p_byteToString(p_Recipient_01$retrieve.recipientOid,
         p_Recipient_01$retrieve.recipientOid_s);
        EXCEPTION
            WHEN OTHERS THEN
                StoO_error := SQLCODE;
                StoO_errmsg := SQLERRM;
        END;
    END;
    END IF;

    COMMIT WORK;
    RETURN p_Recipient_01$retrieve.retValue;
exception
  when OTHERS then
    ibs_error.log_error ( ibs_error.error, 'p_Recipient_01$retrieve',
                          'oid_s: ' || oid_s ||
                          ', userId: ' || userId ||
                          ', op: ' || op );
    return 0;
END p_Recipient_01$retrieve;
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
CREATE OR REPLACE FUNCTION p_Recipient_01$delete(
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
oid     RAW(8);
INSUFFICIENT_RIGHTS     NUMBER(10,0);
ALL_RIGHT     NUMBER(10,0);
OBJECTNOTFOUND     NUMBER(10,0);
RIGHT_DELETE     NUMBER(10,0);
retValue     NUMBER(10,0);
rights     NUMBER(10,0);

BEGIN
    BEGIN
    p_stringToByte(p_Recipient_01$delete.oid_s,
     p_Recipient_01$delete.oid);
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error := SQLCODE;
            StoO_errmsg := SQLERRM;
    END;

    p_Recipient_01$delete.ALL_RIGHT :=  1;
    p_Recipient_01$delete.INSUFFICIENT_RIGHTS :=  2;
    p_Recipient_01$delete.OBJECTNOTFOUND :=  3;
    p_Recipient_01$delete.RIGHT_DELETE :=  16;

    p_Recipient_01$delete.retValue :=  p_Recipient_01$delete.ALL_RIGHT;
    p_Recipient_01$delete.rights :=  0;

    /*[SPCONV-ERR(33)]:BEGIN TRAN statement ignored*/
    NULL;
    BEGIN
    p_Recipient_01$delete.retValue:=p_Object$performDelete(p_Recipient_01$delete.oid_s,
     p_Recipient_01$delete.userId,
     p_Recipient_01$delete.op,
         p_Recipient_01$delete.oid);
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error := SQLCODE;
            StoO_errmsg := SQLERRM;
    END;
    IF  ( p_Recipient_01$delete.retValue = p_Recipient_01$delete.ALL_RIGHT) THEN
    BEGIN

        BEGIN
        StoO_error   := 0;
        StoO_rowcnt  := 0;
        DELETE  ibs_Recipient_01 
            WHERE oid = p_Recipient_01$delete.oid;
        StoO_rowcnt := SQL%ROWCOUNT;
        EXCEPTION
            WHEN OTHERS THEN
                StoO_error  := SQLCODE;
                StoO_errmsg := SQLERRM;
        END;
    END;
    END IF;

    COMMIT WORK;
    RETURN p_Recipient_01$delete.retValue;
exception
  when OTHERS then
    ibs_error.log_error ( ibs_error.error, 'p_Recipient_01$delete',
                          'oid_s: ' || oid_s ||
                          ', userId: ' || userId ||
                          ', op: ' || op );
    return 0;
END p_Recipient_01$delete;
/

show errors;

exit;