/******************************************************************************
 * All stored procedures regarding the Participant_01 object. <BR>
 *
 * @version     $Id: Participant_01Proc.sql,v 1.9 2003/10/31 00:13:14 klaus Exp $
 *
 * @author      Mario Stegbauer (MS)  980805
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
CREATE OR REPLACE FUNCTION p_Participant_01$create
(
    ai_userId               INTEGER ,
    ai_op                   INTEGER,
    ai_tVersionId           INTEGER ,
    ai_name                 VARCHAR2 ,
    ai_containerId_s        VARCHAR2 ,
    ai_containerKind        INTEGER ,
    ai_isLink               NUMBER ,
    ai_linkedObjectId_s     VARCHAR2 ,
    ai_description          VARCHAR2 ,
    ao_oid_s                OUT VARCHAR2
) RETURN INTEGER
AS
    -- constants
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3;
    c_NOT_OK                CONSTANT INTEGER := 0;
  
    -- locals
    l_containerId           RAW (8);
    l_linkedObjectId        RAW (8);
    l_addressoid            VARCHAR2(18);
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_oid                   RAW (8);
    l_fullname              VARCHAR2(63);
    l_announced             NUMBER(1);
    l_free                  INTEGER;
    l_deadline              DATE;
    l_startdate             DATE;

BEGIN
    p_stringToByte(ai_containerId_s, l_containerId);
    p_stringToByte(ai_linkedObjectId_s, l_linkedObjectId);

    p_ParticipantCont_01$chkPart (ai_containerId_s, ai_userId, 
            l_announced, l_free, l_deadline, l_startdate);     
 
    IF l_free <= 0 THEN
        RETURN c_INSUFFICIENT_RIGHTS;
    END IF; 

    BEGIN
        SELECT  fullname
        INTO    l_fullname 
        FROM    ibs_User 
        WHERE   id = ai_userId;
    EXCEPTION
        WHEN OTHERS THEN
            ibs_error.log_error ( ibs_error.error, 'p_Participant_01$create',
                'Error in SELECT fullname FROM ibs_User');
        RAISE;
    END;

    l_retValue:= p_Object$performCreate ( ai_userId, ai_op, ai_tVersionId, 
            ai_name, ai_containerId_s, ai_containerKind, ai_isLink, ai_linkedObjectId_s, 
            ai_description, ao_oid_s, l_oid);

    IF  l_retValue = c_ALL_RIGHT THEN
    BEGIN
        INSERT INTO m2_Participant_01 (oid, announcerId, announcerName)
        VALUES (l_oid, ai_userId, l_fullname);
    EXCEPTION
        WHEN OTHERS THEN
            ibs_error.log_error ( ibs_error.error, 'p_Participant_01$create',
                'Error in INSERT INTO m2_Participant_01');
        RAISE;
    END;
    END IF;

    COMMIT WORK;
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Participant_01$create',
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

    RETURN c_NOT_OK;
END p_Participant_01$create;
/

show errors;

/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
 * This procedure also creates or deletes a participant container (is sub-
 * object which holds all participating users).
 *
 * @input parameters:
 * @param   @id                 ID of the object to be changed.
 * @param   @userId                ID of the user who is changing the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 * @param   @name               Name of the object.
 * @param   @validUntil         Date until which the object is valid.
 * @param   @description        Description of the object.
 * @param   @showInNews         show in news flag
 *
 * @param   @announcerId        Announcers user id
 * @param   @announcerName      Announcers full name
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Participant_01$change
(
    ai_oid_s             VARCHAR2,
    ai_userId             INTEGER,
    ai_op                 INTEGER,
    ai_name             VARCHAR2,
    ai_validUntil         DATE,
    ai_description         VARCHAR2,
    ai_showInNews         INTEGER,
    ai_announcerId         INTEGER,
    ai_announcerName     VARCHAR2
)
RETURN INTEGER
AS

    -- constants
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2;
    c_NOT_OK                CONSTANT INTEGER := 0;

    -- locals
    l_oid                     RAW (8);
    l_retValue                 INTEGER := c_ALL_RIGHT;
    l_dummy                    RAW (8);
    l_partContId            RAW (8);
    l_partContId_s          VARCHAR2(18);
    l_announced             NUMBER(1);
    l_free                  INTEGER;
    l_deadline              DATE;
    l_startdate             DATE;
BEGIN
    p_stringToByte(ai_oid_s, l_oid);

    BEGIN
        SELECT containerId
        INTO   l_partContId
        FROM   ibs_Object
        WHERE  oid = l_oid;
    EXCEPTION
        WHEN OTHERS THEN
            ibs_error.log_error ( ibs_error.error, 'p_Participant_01$change',
                'Error in SELECT containerId INTO l_partContId');
        RAISE;
    END;

    --p_byteToString (l_partContId, l_partContId_s);       

    --p_ParticipantCont_01$chkPart (l_partContId_s, 
    --    ai_userId, l_announced, l_free, l_deadline, l_startdate);     

    --IF l_free <= 0 THEN
    --    RETURN c_INSUFFICIENT_RIGHTS;
    --END IF; 

    l_retValue := p_Object$performChange(ai_oid_s, ai_userId,
        ai_op, ai_name, ai_validUntil, ai_description, ai_showInNews, l_dummy);

COMMIT WORK;
    RETURN l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Participant_01$change',
    ', oid_s = ' || ai_oid_s ||
    ', userId = ' || ai_userId  ||
    ', op = ' || ai_op ||
    ', name = ' || ai_name ||
    ', validUntil = ' || ai_validUntil  ||
    ', description = ' || ai_description ||
    ', announcerId = ' || ai_announcerId ||
    ', announcerName = ' || ai_announcerName ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);

    RETURN c_NOT_OK;
END p_Participant_01$change;
/

show errors;

/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be retrieved.
 * @param   @userId                Id of the user who is getting the data.
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
 * @param   @announcerId        Announcers id
 * @param   @announcerName      Announcers full name
 *
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Participant_01$retrieve
(
    ai_oid_s            VARCHAR2,
    ai_userId           INTEGER,
    ai_op               NUMBER,
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
    ao_announcerId      OUT INTEGER,
    ao_announcerName    OUT VARCHAR2    
)
RETURN INTEGER
AS
    -- constants
    c_ALL_RIGHT         CONSTANT INTEGER := 1;
    c_NOT_OK            CONSTANT INTEGER := 0;

    -- locals
    l_oid               RAW (8);
    l_retValue          INTEGER;

BEGIN
    p_stringToByte (ai_oid_s, l_oid);

    l_retValue := p_Object$performRetrieve(ai_oid_s,
        ai_userId, ai_op, ao_state, ao_tVersionId, ao_typeName, ao_name, ao_containerId,
        ao_containerName, ao_containerKind, ao_isLink, ao_linkedObjectId, ao_owner,
        ao_ownerName, ao_creationDate, ao_creator, ao_creatorName, ao_lastChanged,
        ao_changer, ao_changerName, ao_validUntil, ao_description, ao_showInNews, 
        ao_checkedOut, ao_checkOutDate, 
        ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName,
        l_oid);

    IF  ( l_retValue = c_ALL_RIGHT) THEN
    BEGIN
        SELECT  announcerId,  announcerName
        INTO    ao_announcerId, ao_announcerName
        FROM    m2_Participant_01 
        WHERE   oid = l_oid;
    EXCEPTION
        WHEN OTHERS THEN
            ibs_error.log_error ( ibs_error.error, 'p_Participant_01$retrieve',
                'Error in SELECT announcer');
        RAISE;
    END;
    END IF;

    COMMIT WORK;
    RETURN l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Participant_01$retrieve',
    ', oid_s = ' || ai_oid_s ||
    ', userId = ' || ai_userId  ||
    ', op = ' || ai_op ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);

    RETURN c_NOT_OK;
END p_Participant_01$retrieve;
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
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Participant_01$delete
(
    ai_oid_s     VARCHAR2,
    ai_userId    INTEGER,
    ai_op        INTEGER
)
RETURN INTEGER
AS

    
    -- constants
    c_ALL_RIGHT         CONSTANT INTEGER := 1;
    c_NOT_OK            CONSTANT INTEGER := 0;

    -- locals
    l_oid               RAW (8);
    l_retValue          INTEGER := c_ALL_RIGHT;
    l_partContId        RAW (8);
    l_dummy             RAW (8);

BEGIN
    p_stringToByte(ai_oid_s, l_oid);

    l_retValue:=p_Object$performDelete(ai_oid_s,
        ai_userId, ai_op, l_dummy);

COMMIT WORK;
    RETURN l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Participant_01$delete',
    ', oid_s = ' || ai_oid_s ||
    ', userId = ' || ai_userId  ||
    ', op = ' || ai_op ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);
    
    RETURN c_NOT_OK;
END p_Participant_01$delete;
/

show errors;

EXIT;
