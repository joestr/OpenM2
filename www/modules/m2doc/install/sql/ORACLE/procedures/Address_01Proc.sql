/******************************************************************************
 * All stored procedures regarding the Adress_01 Object. <BR>
 * 
 *
 * @version     1.10.0001, 05.08.1999
 *
 * @author      Mario Stegbauer (MS)  980805
 *
 * <DT><B>Updates:</B>
 * <DD>HB 990805    Code cleaning.
 * <DD>HB 990810    EXIT inserted.
 ******************************************************************************
 */


CREATE OR REPLACE FUNCTION p_Address_01$create
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
    ao_oid_s                OUT VARCHAR2
) 
RETURN INTEGER 
AS 
    c_NOOID                 CONSTANT RAW (8) := '0000000000000000';
    c_ALL_RIGHT             CONSTANT INTEGER := 1; 
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2; 
    c_ALREADY_EXISTS        CONSTANT INTEGER := 21; 
    l_retValue              INTEGER := c_ALL_RIGHT; 
    l_oid                   RAW (8) := c_NOOID; 
    l_containerId           RAW (8) := c_NOOID; 
    l_linkedObjectId        RAW (8) := c_NOOID; 

BEGIN 
    p_stringToByte(ai_containerId_s, 
     l_containerId); 

    p_stringToByte(ai_linkedObjectId_s, 
     l_linkedObjectId); 
 
    l_retValue := p_Object$performCreate(ai_userId, 
        ai_op, 
        ai_tVersionId, 
        ai_name, 
        ai_containerId_s, 
        ai_containerKind, 
        ai_isLink, 
        ai_linkedObjectId_s, 
        ai_description, 
        ao_oid_s, 
        l_oid); 

    IF  ( l_retValue = c_ALL_RIGHT) 
    THEN 
    BEGIN 
        INSERT INTO m2_Address_01 (oid, street, zip, town, mailbox, country, tel, fax, email, homepage)
        VALUES (l_oid, ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '); 
    EXCEPTION 
        WHEN OTHERS THEN 
            ibs_error.log_error ( ibs_error.error, 'p_Address_01$create',
                                  'Error in INSERT - Statement');
        RAISE;
    END; 
    END IF; 
 
    COMMIT WORK; 
    RETURN l_retValue; 

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Address_01$create',        
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

    RETURN 0;
    
END p_Address_01$create; 
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
CREATE OR REPLACE FUNCTION p_Address_01$change(
oid_s 		VARCHAR2 ,
userId 		INTEGER ,
op 		NUMBER ,
in_name 	VARCHAR2 ,
validUntil 	DATE ,
description  VARCHAR2 ,
ai_showInNews  INTEGER,
street 		VARCHAR2 ,
zip 		VARCHAR2 ,
town 		VARCHAR2 ,
mailbox 	VARCHAR2 ,
country 	VARCHAR2 ,
tel 		VARCHAR2 ,
fax 		VARCHAR2 ,
email 		VARCHAR2 ,
homepage 	VARCHAR2 )
RETURN INTEGER
AS
name 		VARCHAR2(63);
StoO_selcnt	INTEGER;
StoO_error 	INTEGER;
StoO_rowcnt	INTEGER;
StoO_errmsg	VARCHAR2(255);
StoO_sqlstatus	INTEGER;
ALL_RIGHT 	NUMBER(10,0);
INSUFFICIENT_RIGHTS 	NUMBER(10,0);
OBJECTNOTFOUND 	NUMBER(10,0);
retValue 	NUMBER(10,0);
oid 		RAW (8);

BEGIN
	p_Address_01$change.name := p_Address_01$change.in_name;
	p_Address_01$change.ALL_RIGHT :=  1;
	p_Address_01$change.INSUFFICIENT_RIGHTS :=  2;
	p_Address_01$change.OBJECTNOTFOUND :=  3;

	p_Address_01$change.retValue :=  p_Address_01$change.ALL_RIGHT;

	IF  ( p_Address_01$change.name IS NULL) THEN
	BEGIN
		p_Address_01$change.name :=  'Adresse';
	END;
	END IF;

	/*[SPCONV-ERR(40)]:BEGIN TRAN statement ignored*/
	NULL;
	BEGIN
	p_Address_01$change.retValue:=p_Object$performChange(p_Address_01$change.oid_s,
	 p_Address_01$change.userId,
	 p_Address_01$change.op,
	 p_Address_01$change.name,
	 p_Address_01$change.validUntil,
         '',
     p_Address_01$change.ai_showInNews,
	 p_Address_01$change.oid);
	EXCEPTION
		WHEN OTHERS THEN
			StoO_error := SQLCODE;

			StoO_errmsg := SQLERRM;

	END;
	IF  ( p_Address_01$change.retValue = p_Address_01$change.ALL_RIGHT) THEN
	BEGIN
		BEGIN
		StoO_error   := 0;
		StoO_rowcnt  := 0;
		UPDATE m2_Address_01
		SET street = p_Address_01$change.street,
		zip = p_Address_01$change.zip,
		town = p_Address_01$change.town,
		mailbox = p_Address_01$change.mailbox,
		country = p_Address_01$change.country,
		tel = p_Address_01$change.tel,
		fax = p_Address_01$change.fax,
		email = p_Address_01$change.email,
		homepage = p_Address_01$change.homepage
		
		WHERE oid = p_Address_01$change.oid;
		StoO_rowcnt := SQL%ROWCOUNT;
		EXCEPTION
			WHEN OTHERS THEN
				StoO_error  := SQLCODE;
				StoO_errmsg := SQLERRM;
		END;
	END;
	END IF;

	COMMIT WORK;
	RETURN p_Address_01$change.retValue;
	RETURN 0;

EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_Address_01$change',
    ', oid_s = ' || oid_s ||
    ', userId = ' || userId  ||
    ', op = ' || op ||
    ', in_name = ' || in_name ||
    ', validUntil = ' || validUntil  ||
    ', street = ' || street ||
    ', zip = ' || zip ||
    ', town = ' || town ||
    ', mailbox = ' || mailbox ||
    ', country = ' || country ||
    ', tel = ' || tel ||
    ', fax = ' || fax ||
    ', email = ' || email ||
    ', homepage = ' || homepage ||
    ', errorcode = ' || StoO_error ||
    ', errormessage = ' || StoO_errmsg);
END p_Address_01$change;
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
 * @param   @showInNews         show in news flag.
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
CREATE OR REPLACE FUNCTION p_Address_01$retrieve
(
    oid_s 	        VARCHAR2 ,
    userId 	        INTEGER ,
    op 	            INTEGER ,
    -- outputparameters
    state 	        OUT INTEGER,
    tVersionId      OUT INTEGER,
    typeName 	    OUT VARCHAR2,
    name 	        OUT VARCHAR2,
    containerId     OUT RAW,
    containerName   OUT VARCHAR2,
    containerKind   OUT INTEGER,
    isLink 	        OUT NUMBER,
    linkedObjectId  OUT RAW,
    owner 	        OUT INTEGER,
    ownerName 	    OUT VARCHAR2,
    creationDate    OUT DATE,
    creator 	    OUT INTEGER,
    creatorName     OUT VARCHAR2,
    lastChanged     OUT DATE,
    changer 	    OUT INTEGER,
    changerName     OUT VARCHAR2,
    validUntil      OUT DATE,
    description     OUT VARCHAR2,
    ao_showInNews   OUT INTEGER,
    ao_checkedOut          OUT NUMBER,
    ao_checkOutDate        OUT DATE,
    ao_checkOutUser        OUT INTEGER,
    ao_checkOutUserOid     OUT RAW,
    ao_checkOutUserName    OUT VARCHAR2,
    street 	        OUT VARCHAR2,
    zip 	        OUT VARCHAR2,
    town 	        OUT VARCHAR2,
    mailbox         OUT VARCHAR2,
    country         OUT VARCHAR2,
    tel 	        OUT VARCHAR2,
    fax 	        OUT VARCHAR2,
    email 	        OUT VARCHAR2,
    homepage 	    OUT VARCHAR2
)
RETURN INTEGER
AS
    -- exception values:
    StoO_error 	        INTEGER;
    StoO_errmsg	        VARCHAR2(255);
    -- return constants
    ALL_RIGHT 	        INTEGER := 1;
    INSUFFICIENT_RIGHTS INTEGER := 2;
    OBJECTNOTFOUND 	    INTEGER := 3;
    -- return value:
    retValue 	        INTEGER;
    -- local values:
    oid 	            RAW (8);


BEGIN
	p_Address_01$retrieve.retValue :=  p_Address_01$retrieve.ALL_RIGHT;

	p_Address_01$retrieve.retValue:=p_Object$performRetrieve(p_Address_01$retrieve.oid_s,
	            p_Address_01$retrieve.userId, p_Address_01$retrieve.op, p_Address_01$retrieve.state,
	            p_Address_01$retrieve.tVersionId, p_Address_01$retrieve.typeName, p_Address_01$retrieve.name,
	            p_Address_01$retrieve.containerId, p_Address_01$retrieve.containerName, p_Address_01$retrieve.containerKind,
	            p_Address_01$retrieve.isLink, p_Address_01$retrieve.linkedObjectId, p_Address_01$retrieve.owner,
	            p_Address_01$retrieve.ownerName, p_Address_01$retrieve.creationDate, p_Address_01$retrieve.creator,
	            p_Address_01$retrieve.creatorName, p_Address_01$retrieve.lastChanged, p_Address_01$retrieve.changer,
	            p_Address_01$retrieve.changerName, p_Address_01$retrieve.validUntil, p_Address_01$retrieve.description,
	            p_Address_01$retrieve.ao_showInNews,
                    ao_checkedOut, ao_checkOutDate, 
                    ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName,
	            p_Address_01$retrieve.oid);

	IF  ( p_Address_01$retrieve.retValue = p_Address_01$retrieve.ALL_RIGHT) 
	THEN
		SELECT street, zip, town,  
		       mailbox, country, tel,
		       fax, email, homepage
		INTO   p_Address_01$retrieve.street, p_Address_01$retrieve.zip, p_Address_01$retrieve.town, 
		       p_Address_01$retrieve.mailbox, p_Address_01$retrieve.country, p_Address_01$retrieve.tel, 
		       p_Address_01$retrieve.fax, p_Address_01$retrieve.email, p_Address_01$retrieve.homepage 
		FROM m2_Address_01 
		WHERE oid = p_Address_01$retrieve.oid;
	END IF; -- if retValue = ALL_RIGHT

COMMIT WORK;
    RETURN p_Address_01$retrieve.retValue;
    
EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_Address_01$retrieve',                   
    ', oid_s = ' || oid_s ||  
    ', userId = ' || userId  || 
    ', op = ' || op || 
    ', errorcode = ' || StoO_error ||
    ', errormessage = ' || StoO_errmsg);     
END p_Address_01$retrieve;
/

show errors;

/******************************************************************************
 * Deletes a diskussion_01 object and all its values (incl. rights check). <BR>
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
CREATE OR REPLACE FUNCTION p_Address_01$delete(
oid_s     VARCHAR2 ,
userId     NUMBER ,
op     NUMBER )
RETURN INTEGER
AS
StoO_selcnt    INTEGER;
StoO_error     INTEGER;
StoO_rowcnt    INTEGER;
StoO_errmsg    VARCHAR2(255);
StoO_sqlstatus    INTEGER;
oid         RAW (8);
ALL_RIGHT     NUMBER(10,0);
INSUFFICIENT_RIGHTS     NUMBER(10,0);
OBJECTNOTFOUND     NUMBER(10,0);
RIGHT_DELETE     NUMBER(10,0);
retValue     NUMBER(10,0);
rights     NUMBER(10,0);
containerId     RAW (8);
posNoPath     RAW (254);

BEGIN
    BEGIN
    p_stringToByte(p_Address_01$delete.oid_s,
     p_Address_01$delete.oid);
    EXCEPTION
        WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error,'p_Address_01$delete',
                        'Error in p_stringToByte');
        raise;        
    END;

    p_Address_01$delete.ALL_RIGHT :=  1;
    p_Address_01$delete.INSUFFICIENT_RIGHTS :=  2;
    p_Address_01$delete.OBJECTNOTFOUND :=  3;
    p_Address_01$delete.RIGHT_DELETE :=  16;

    p_Address_01$delete.retValue :=  p_Address_01$delete.ALL_RIGHT;
    p_Address_01$delete.rights :=  0;

    /*[SPCONV-ERR(35)]:BEGIN TRAN statement ignored*/
    NULL;
    BEGIN
    p_Address_01$delete.retValue:=p_Object$performDelete(
         p_Address_01$delete.oid_s,
     p_Address_01$delete.userId,
     p_Address_01$delete.op,
         p_Address_01$delete.oid);
    EXCEPTION
        WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error,'p_Address_01$delete',
                        'Error in p_Object$performDelete');
        raise;
    END;
    IF  ( p_Address_01$delete.retValue = p_Address_01$delete.ALL_RIGHT) THEN
    BEGIN

        BEGIN
        StoO_error   := 0;
        StoO_rowcnt  := 0;
        DELETE  m2_Address_01 
            WHERE oid = p_Address_01$delete.oid;
        StoO_rowcnt := SQL%ROWCOUNT;
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error,'p_Address_01$delete',
                        'Error in p_Object$performChange');
        raise;
        END;
    END;
    END IF;

    COMMIT WORK;
    RETURN p_Address_01$delete.retValue;
exception
  when OTHERS then
    ibs_error.log_error ( ibs_error.error, 'p_Address_01$delete',
                          'oid_s: ' || oid_s ||
                          ', userId: ' || userId ||
                          ', op: ' || op ||
                          ', sqlcode: ' || SQLCODE ||
                          ', sqlerrm: ' || SQLERRM );
     return 0;
END p_Address_01$delete;
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
CREATE OR REPLACE FUNCTION p_Address_01$BOCopy(
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
    p_Address_01$BOCopy.NOT_OK :=  0;
    p_Address_01$BOCopy.ALL_RIGHT :=  1;

    p_Address_01$BOCopy.retValue :=  p_Address_01$BOCopy.NOT_OK;
    BEGIN
    StoO_error   := 0;
    StoO_rowcnt  := 0;
    INSERT INTO m2_Address_01 (oid, street, zip, town, mailbox, country, tel, fax, email, homepage)SELECT  p_Address_01$BOCopy.newOid, street, zip, town, mailbox, 
           country, tel, fax, email, homepage
         FROM m2_Address_01 
        WHERE oid = p_Address_01$BOCopy.oid;
    StoO_rowcnt := SQL%ROWCOUNT;
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error  := SQLCODE;
            StoO_errmsg := SQLERRM;
    END;
    IF  ( StoO_rowcnt >= 1) THEN
        p_Address_01$BOCopy.retValue :=  p_Address_01$BOCopy.ALL_RIGHT;
    END IF;
    RETURN p_Address_01$BOCopy.retValue;
    RETURN 0;
EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_Address_01$BOCopy',
    ', userId = ' || userId  ||
    ', newOid = ' || newOid ||
    ', errorcode = ' || StoO_error ||
    ', errormessage = ' || StoO_errmsg);
END p_Address_01$BOCopy;
/

show errors;

EXIT;
