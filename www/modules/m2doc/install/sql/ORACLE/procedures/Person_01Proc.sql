/******************************************************************************
 * All stored procedures regarding the Person_01 Object. <BR>
 * 
 * @version     $Id: Person_01Proc.sql,v 1.10 2003/10/31 00:13:14 klaus Exp $
 *
 * @author      Mario Stegbauer (MS)  980805
 ******************************************************************************
 */


/******************************************************************************
 * Creates a new Person_01 Object (incl. rights check). <BR>
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
CREATE OR REPLACE FUNCTION p_Person_01$create
(
    -- input parameters:
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_tVersionId           INTEGER,
    ai_name                 VARCHAR2,
    ai_containerId_s        VARCHAR2,
    ai_containerKind        INTEGER,
    ai_isLink               NUMBER,
    ai_linkedObjectId_s     VARCHAR2,
    ai_description          VARCHAR2,
    -- output parameters:
    ao_oid_s                OUT VARCHAR2
)
RETURN INTEGER
AS
    -- constants
    c_NOOID                 CONSTANT RAW (8) := '0000000000000000';
    -- define return constants:
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2;
    c_ALREADY_EXISTS        CONSTANT INTEGER := 21;
    -- define return values:
    l_retValue              INTEGER := c_ALL_RIGHT;
    -- define local variables:
    l_oid                   RAW (8) := c_NOOID;
    l_addressOid_s          VARCHAR2 (18);
    l_personsOid_s          VARCHAR2 (18);
    l_dummy                 INTEGER;
    -- convertions (objectidstring) - all input objectids must be converted
    l_containerId           RAW (8) := c_NOOID;
    l_linkedObjectId        RAW (8) := c_NOOID;

BEGIN
    p_stringToByte (ai_containerId_s, l_containerId);
    p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);
    
    -- create base object:
    l_retValue := p_Object$performCreate (
                           ai_userId, ai_op, ai_tVersionId, 
                           ai_name, ai_containerId_s, ai_containerKind, 
                           ai_isLink, ai_linkedObjectId_s, ai_description, 
                           ao_oid_s, l_oid);

    IF (l_retValue = c_ALL_RIGHT)     -- object created successfully?
    THEN
        BEGIN
            -- insert the other values
            INSERT INTO mad_Person_01 (oid, fullname, prefix, title, position, company, offemail, offhomepage, useroid)
                 VALUES (l_oid, ai_name, ' ', ' ', ' ', ' ', ' ', ' ','0000000000000000');
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error, 'p_Person_01$create',
                                    'Error in INSERT - Statement');
            RAISE;
        END;

    END IF; -- if object created successfully

COMMIT WORK;
    -- return the state value
    RETURN  l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Person_01$create',               
    'userId = ' || ai_userId ||
    ', op = ' || ai_op ||    
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
END p_Person_01$create;
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
 * @param   @showInNews         show in news flag.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Person_01$change
(
    oid_s           VARCHAR2,
    userId          INTEGER,
    op              NUMBER,
    name            VARCHAR2,
    validUntil      DATE,
    description     VARCHAR2,
    ai_showInNews   INTEGER,
    title           VARCHAR2,
    prefix          VARCHAR2,
    position        VARCHAR2,
    company         VARCHAR2,
    offemail        VARCHAR2,
    offhomepage     VARCHAR2,
    useroid_s       VARCHAR2
)
RETURN INTEGER
AS
    c_NOT_OK                CONSTANT INTEGER := 0;
    StoO_selcnt     INTEGER;
    StoO_error      INTEGER;
    StoO_rowcnt     INTEGER;
    StoO_errmsg     VARCHAR2 (255);
    StoO_sqlstatus  INTEGER;
    ALL_RIGHT       NUMBER (10,0);
    INSUFFICIENT_RIGHTS     NUMBER (10,0);
    OBJECTNOTFOUND  NUMBER (10,0);
    retValue        NUMBER (10,0);
    oid             RAW (8);
    useroid         RAW (8);
BEGIN
    p_Person_01$change.ALL_RIGHT            :=  1;
    p_Person_01$change.INSUFFICIENT_RIGHTS  :=  2;
    p_Person_01$change.OBJECTNOTFOUND       :=  3;
    
    p_Person_01$change.retValue :=  p_Person_01$change.ALL_RIGHT;

    p_stringtobyte (useroid_s, useroid);

    /*[SPCONV-ERR(35)]:BEGIN TRAN statement ignored*/
    BEGIN
    p_Person_01$change.retValue:=p_Object$performChange(p_Person_01$change.oid_s,
     p_Person_01$change.userId,
     p_Person_01$change.op,
     p_Person_01$change.name,
     p_Person_01$change.validUntil,
     p_Person_01$change.description,
     p_Person_01$change.ai_showInNews,
     p_Person_01$change.oid);
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error := SQLCODE;
            StoO_errmsg := SQLERRM;
    END;
    IF  (p_Person_01$change.retValue = p_Person_01$change.ALL_RIGHT) THEN
    BEGIN
    
        BEGIN
        StoO_error   := 0;
        StoO_rowcnt  := 0;
        
        UPDATE mad_Person_01
        SET fullname = p_Person_01$change.name,
            title = p_Person_01$change.title,
            prefix = p_Person_01$change.prefix,
            position = p_Person_01$change.position,
            company = p_Person_01$change.company,
            offemail = p_Person_01$change.offemail,
            offhomepage = p_Person_01$change.offhomepage,
            useroid = p_Person_01$change.useroid        
        WHERE oid = p_Person_01$change.oid;
            StoO_rowcnt := SQL%ROWCOUNT;
        EXCEPTION
            WHEN OTHERS THEN
                StoO_error  := SQLCODE;
                StoO_errmsg := SQLERRM;
        END;
    END;
    END IF;
    COMMIT WORK;
    
    RETURN p_Person_01$change.retValue;
    
EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Person_01$change',
                          'oid_s: ' || oid_s ||
                          ', userId: ' || userId ||
                          ', op: ' || op ||
                          ', name: ' || name ||
                          ', validUntil: ' || validUntil ||
                          ', description: ' || description ||
                          ', title: ' || title ||
                          ', prefix: ' || prefix ||
                          ', position: ' || position ||
                          ', company: ' || company ||
                          ', offemail: ' || offemail ||
                          ', offhomepage: ' || offhomepage );
                          
    RETURN c_NOT_OK;
END p_Person_01$change;
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
 * @param   @maxlevels          Maximum of the levels allowed in the discussion
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Person_01$retrieve
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    -- output parameters
    ao_state                OUT      INTEGER,
    ao_tVersionId     OUT      INTEGER,
    ao_typeName       OUT      VARCHAR2,
    ao_name           OUT      VARCHAR2,
    ao_containerId    OUT      RAW,
    ao_containerName  OUT      VARCHAR2,
    ao_containerKind  OUT      INTEGER,
    ao_isLink         OUT      NUMBER,
    ao_linkedObjectId OUT      RAW,
    ao_owner          OUT      INTEGER,
    ao_ownerName      OUT      VARCHAR2,
    ao_creationDate   OUT      DATE,
    ao_creator        OUT      INTEGER,
    ao_creatorName    OUT      VARCHAR2,
    ao_lastChanged    OUT      DATE,
    ao_changer        OUT      INTEGER,
    ao_changerName    OUT      VARCHAR2,
    ao_validUntil     OUT      DATE,
    ao_description    OUT      VARCHAR2, 
    ao_showInNews     OUT      INTEGER,
    ao_checkedOut          OUT NUMBER,
    ao_checkOutDate        OUT DATE,
    ao_checkOutUser        OUT INTEGER,
    ao_checkOutUserOid     OUT RAW,
    ao_checkOutUserName    OUT VARCHAR2,
    ao_title                OUT VARCHAR2,
    ao_prefix               OUT      VARCHAR2,
    ao_position             OUT      VARCHAR2,
    ao_company              OUT      VARCHAR2,
    ao_offemail             OUT      VARCHAR2,
    ao_offhomepage          OUT      VARCHAR2,
    ao_adressId             OUT      RAW,
    ao_useroid              OUT      RAW,
    ao_username             OUT      VARCHAR2

)
RETURN INTEGER
AS
    -- define return constants:
    c_NOOID                 CONSTANT RAW (8) := '0000000000000000';
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1; 
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; 
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3;
    c_ST_ACTIVE             CONSTANT INTEGER := 2; -- active object state
    -- define return values:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of this procedure
    -- define local variables:
    l_oid                   RAW (8) := c_NOOID;
    
BEGIN 
    -- set a save point for the current transaction:
    --SAVEPOINT s_Person_01$retrieve;

    -- retrieve the base object data:
    l_retValue := p_Object$performRetrieve (
                ai_oid_s, ai_userId, ai_op,
                ao_state, ao_tVersionId, ao_typeName, 
                ao_name, ao_containerId, ao_containerName, 
                ao_containerKind, ao_isLink, ao_linkedObjectId,
                ao_owner, ao_ownerName, 
                ao_creationDate, ao_creator, ao_creatorName,
                ao_lastChanged, ao_changer, ao_changerName,
                ao_validUntil, ao_description, ao_showInNews, 
                ao_checkedOut, ao_checkOutDate, 
                ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName,
                l_oid);

    IF (l_retValue = c_ALL_RIGHT)
    THEN
        BEGIN
            SELECT title, position,
                   prefix, company,
                   offemail, offhomepage,
                   useroid
            INTO   ao_title, ao_position,
                   ao_prefix, ao_company,
                   ao_offemail, ao_offhomepage, ao_useroid
		    FROM   mad_Person_01
		    WHERE  oid = l_oid;
		EXCEPTION
		    WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error, 'p_Person_01$retrieve',
                                      'Error in SELECT FROM mad_Person_01');
		    RAISE;
		END;

        BEGIN
            SELECT  oid
            INTO    ao_adressId 
            FROM    ibs_Object 
            WHERE   containerId = l_oid
                AND containerKind = 2
                AND tVersionId = 16854785;
		EXCEPTION
		    WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error, 'p_Person_01$retrieve',
                                      'Error in SELECT FROM ibs_Object 1');
		    RAISE;
		END;
      
        
        BEGIN
            SELECT  fullname
            INTO    ao_username
            FROM    ibs_user
            WHERE   oid = ao_useroid;
 
 		EXCEPTION
		    WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error, 'p_Person_01$retrieve',
                                      'Error in SELECT FROM ibs_user');
		    RAISE;
		END;
     
    END IF; -- if retValue = ALL_RIGHT

    COMMIT WORK;

    -- return the state value
    RETURN   l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- roll back to the save point:
        --ROLLBACK TO s_Person_01$retrieve;
        -- create a log entry:
        ibs_error.log_error ( ibs_error.error, 'p_Person_01$retrieve',
            ', oid_s = ' || ai_oid_s ||
            ', userId = ' || ai_userId  ||
            ', op = ' || ai_op ||
            ', errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM);
        -- commit the log entry:
        COMMIT WORK;
        -- return error code:
        RETURN c_NOT_OK;
END p_Person_01$retrieve;
/

show errors;

/******************************************************************************
 * Deletes a Person_01 object and all its values (incl. rights check). <BR>
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
CREATE OR REPLACE FUNCTION p_Person_01$delete
(
    oid_s     VARCHAR2 ,
    userId     INTEGER ,
    op     NUMBER 
)
RETURN INTEGER
AS
    c_NOT_OK                CONSTANT INTEGER := 0;

    StoO_selcnt     INTEGER;
    StoO_error      INTEGER;
    StoO_rowcnt     INTEGER;
    StoO_errmsg     VARCHAR2 (255);
    StoO_sqlstatus  INTEGER;
    oid             RAW (8);
    INSUFFICIENT_RIGHTS     NUMBER (10,0);
    ALL_RIGHT       NUMBER (10,0);
    OBJECTNOTFOUND  NUMBER (10,0);
    RIGHT_DELETE    NUMBER (10,0);
    retValue        NUMBER (10,0);
    rights          NUMBER (10,0);
    containerId     RAW (8);
BEGIN
    BEGIN
    p_stringToByte(p_Person_01$delete.oid_s,
    p_Person_01$delete.oid);
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error := SQLCODE;
            StoO_errmsg := SQLERRM;
    END;
    p_Person_01$delete.ALL_RIGHT            :=  1;
    p_Person_01$delete.INSUFFICIENT_RIGHTS  :=  2;
    p_Person_01$delete.OBJECTNOTFOUND       :=  3;
    p_Person_01$delete.RIGHT_DELETE         :=  16;

    p_Person_01$delete.retValue :=  p_Person_01$delete.ALL_RIGHT;
    p_Person_01$delete.rights :=  0;

    IF  ( StoO_rowcnt > 0) THEN
    BEGIN
        BEGIN
            NULL;
-- TODO: Was für eine Rights ist das?
--        p_Rights$checkRights(p_Person_01$delete.oid,
--         p_Person_01$delete.userId,
--         p_Person_01$delete.op,
--         p_Person_01$delete.rights);
        EXCEPTION
            WHEN OTHERS THEN
                StoO_error := SQLCODE;
                StoO_errmsg := SQLERRM;
        END;

        IF  ( p_Person_01$delete.rights > 0) THEN
        BEGIN
            /*[SPCONV-ERR(43)]:BEGIN TRAN statement ignored*/
            BEGIN
            StoO_error   := 0;
            StoO_rowcnt  := 0;
            DELETE  ibs_Object 
                WHERE linkedObjectId = p_Person_01$delete.oid;
            StoO_rowcnt := SQL%ROWCOUNT;
            EXCEPTION
                WHEN OTHERS THEN
                    StoO_error  := SQLCODE;
                    StoO_errmsg := SQLERRM;
            END;
            BEGIN
            StoO_error   := 0;
            StoO_rowcnt  := 0;
            DELETE  mad_Person_01 
                WHERE oid = p_Person_01$delete.oid;
            StoO_rowcnt := SQL%ROWCOUNT;
            EXCEPTION
                WHEN OTHERS THEN
                    StoO_error  := SQLCODE;
                    StoO_errmsg := SQLERRM;
            END;
            BEGIN
            StoO_error   := 0;
            StoO_rowcnt  := 0;
            DELETE  ibs_Object 
                WHERE oid = p_Person_01$delete.oid;
            StoO_rowcnt := SQL%ROWCOUNT;
            EXCEPTION
                WHEN OTHERS THEN
                    StoO_error  := SQLCODE;
                    StoO_errmsg := SQLERRM;
            END;
            COMMIT WORK;
        END;
        ELSE
        BEGIN
            p_Person_01$delete.retValue :=  p_Person_01$delete.INSUFFICIENT_RIGHTS;
        END;
        END IF;
    END;
    ELSE
    BEGIN
        p_Person_01$delete.retValue :=  p_Person_01$delete.OBJECTNOTFOUND;
    END;
    END IF;
    RETURN p_Person_01$delete.retValue;
END p_Person_01$delete;
/

show errors;

/******************************************************************************
 * Copies a Person_01 object and all its values (incl. rights check). <BR>
 */
CREATE OR REPLACE FUNCTION p_Person_01$BOCopy
(
oid     RAW ,
userId     INTEGER ,
newOid     RAW )
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
    p_Person_01$BOCopy.NOT_OK :=  0;
    p_Person_01$BOCopy.ALL_RIGHT :=  1;

    p_Person_01$BOCopy.retValue :=  p_Person_01$BOCopy.NOT_OK;
    BEGIN
    StoO_error   := 0;
    StoO_rowcnt  := 0;
    INSERT INTO mad_Person_01 (oid, fullname, title, prefix, position, company, offemail, offhomepage, useroid)
    
    SELECT  p_Person_01$BOCopy.newOid, fullname, title, prefix, position, 
           company, offemail, offhomepage, useroid
         FROM mad_Person_01 
        WHERE oid = p_Person_01$BOCopy.oid;
    StoO_rowcnt := SQL%ROWCOUNT;
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error  := SQLCODE;
            StoO_errmsg := SQLERRM;
    END;

    IF  ( StoO_rowcnt >= 1) THEN
        p_Person_01$BOCopy.retValue :=  p_Person_01$BOCopy.ALL_RIGHT;
    END IF;
    RETURN p_Person_01$BOCopy.retValue;
    RETURN 0;

EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_Person_01$BOCopy',
    ', userId = ' || userId  ||
    ', newOid = ' || newOid ||
    ', errorcode = ' || StoO_error ||
    ', errormessage = ' || StoO_errmsg);
END p_Person_01$BOCopy;
/

show errors;

EXIT;