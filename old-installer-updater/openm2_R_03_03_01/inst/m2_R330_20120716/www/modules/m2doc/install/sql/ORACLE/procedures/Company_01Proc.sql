/******************************************************************************
 * All stored procedures regarding the Company_01 Object. <BR>
 * 
 * @version     $Id: Company_01Proc.sql,v 1.9 2003/10/31 16:27:54 klaus Exp $
 *
 * @author      Mario Stegbauer (MS)  980805
 ******************************************************************************
 */


/******************************************************************************
 * Creates a new Company_01 Object (incl. rights check). <BR>
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
CREATE OR REPLACE FUNCTION p_Company_01$create
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
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2;
    c_ALREADY_EXISTS        CONSTANT INTEGER := 21;
    -- local variables
    l_containerId           RAW (8);
    l_linkedObjectId        RAW (8);
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_oid                   RAW (8) := c_NOOID;
    l_addressOid_s          VARCHAR2 (18);
    l_contactsOid_s         VARCHAR2 (18);
    l_dummy		            INTEGER;
    
BEGIN
    -- convertions (objectidstring) - all input objectids must be converted
    p_stringToByte (ai_containerId_s, l_containerId);
    p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);

    l_retValue := p_Object$performCreate (
        ai_userId, ai_op, 
        ai_tVersionId, ai_name, 
        ai_containerId_s, ai_containerKind, 
        ai_isLink, ai_linkedObjectId_s, 
        ai_description, ao_oid_s, 
        l_oid);
        
	IF (l_retValue = c_ALL_RIGHT)     -- object created successfully?
    THEN
        BEGIN
	        -- insert the other values
		    INSERT INTO mad_Company_01 
                    (oid, owner, manager, legal_form, mwst)
		    VALUES  (l_oid, ' ', ' ', ' ', 0);
		EXCEPTION
		    WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error, 'p_Company_01$create',
                                      'Error in INSERT INTO mad_Company_01');
		    RAISE;
		END;

    END IF;-- if object created successfully
    
    COMMIT WORK;
    -- return the state value
    RETURN  l_retValue;
    
EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Company_01$create',
    ', userId = ' || ai_userId  ||
    ', op = ' || ai_op  ||
    ', tVersionId = ' || ai_tVersionId  ||
    ', name = ' || ai_name  ||
    ', containerId_s = ' || ai_containerId_s  ||
    ', containerKind = ' || ai_containerKind  ||
    ', isLink = ' || ai_isLink  ||
    ', linkedObjectId_s = ' || ai_linkedObjectId_s  ||
    ', description = ' || ai_description  ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);
    
    RETURN 0;
END p_Company_01$create;
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
CREATE OR REPLACE FUNCTION p_Company_01$change(
oid_s 	VARCHAR2 ,
userId 	INTEGER ,
op 	NUMBER ,
name 	VARCHAR2 ,
validUntil 	DATE ,
description 	VARCHAR2 ,
ai_showInNews  INTEGER,
compowner 	VARCHAR2 ,
manager 	VARCHAR2 ,
legal_form 	VARCHAR2 ,
mwst            INTEGER)
RETURN INTEGER
AS
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
	p_Company_01$change.ALL_RIGHT :=  1;
	p_Company_01$change.INSUFFICIENT_RIGHTS :=  2;
	p_Company_01$change.OBJECTNOTFOUND :=  3;

	p_Company_01$change.retValue :=  p_Company_01$change.ALL_RIGHT;

	/*[SPCONV-ERR(31)]:BEGIN TRAN statement ignored*/
	NULL;
	BEGIN
	p_Company_01$change.retValue:=p_Object$performChange(p_Company_01$change.oid_s,
	 p_Company_01$change.userId,
	 p_Company_01$change.op,
	 p_Company_01$change.name,
	 p_Company_01$change.validUntil,
	 p_Company_01$change.description,
	 p_Company_01$change.ai_showInNews,
	 p_Company_01$change.oid);
	EXCEPTION
		WHEN OTHERS THEN
			StoO_error := SQLCODE;

			StoO_errmsg := SQLERRM;

	END;
	IF  ( p_Company_01$change.retValue = p_Company_01$change.ALL_RIGHT) THEN
	BEGIN
		BEGIN
		StoO_error   := 0;
		StoO_rowcnt  := 0;
		UPDATE mad_Company_01
		SET owner = p_Company_01$change.compowner,
		manager = p_Company_01$change.manager,
		legal_form = p_Company_01$change.legal_form,
		mwst = p_Company_01$change.mwst

		WHERE oid = p_Company_01$change.oid;
		StoO_rowcnt := SQL%ROWCOUNT;
		EXCEPTION
			WHEN OTHERS THEN
				StoO_error  := SQLCODE;
				StoO_errmsg := SQLERRM;
		END;
	END;
	END IF;

	COMMIT WORK;
	RETURN p_Company_01$change.retValue;
	RETURN 0;

EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_Company_01$change',
    ', oid_s = ' || oid_s ||
    ', userId = ' || userId  ||
    ', op = ' || op ||
    ', name = ' || name ||
    ', validUntil = ' || validUntil  ||
    ', description = ' || description ||
    ', compowner = ' || compowner ||
    ', manager = ' || manager ||
    ', legal_form = ' || legal_form ||
    ', errorcode = ' || StoO_error ||
    ', errormessage = ' || StoO_errmsg);
END p_Company_01$change;
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
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Company_01$retrieve(
oid_s 	VARCHAR2 ,
userId 	INTEGER ,
op 	NUMBER ,
state 		OUT NUMBER,
tVersionId 	OUT NUMBER,
typeName 	OUT VARCHAR2,
name 		OUT VARCHAR2,
containerId 	OUT RAW,
containerName 	OUT VARCHAR2,
containerKind 	OUT NUMBER,
isLink 		OUT NUMBER,
linkedObjectId 	OUT RAW,
owner 		OUT NUMBER,
ownerName 	OUT VARCHAR2,
creationDate 	OUT DATE,
creator 	OUT NUMBER,
creatorName 	OUT VARCHAR2,
lastChanged 	OUT DATE,
changer 	OUT NUMBER,
changerName 	OUT VARCHAR2,
validUntil 	OUT DATE,
description 	OUT VARCHAR2,
ao_showInNews  OUT INTEGER,
    ao_checkedOut          OUT NUMBER,
    ao_checkOutDate        OUT DATE,
    ao_checkOutUser        OUT INTEGER,
    ao_checkOutUserOid     OUT RAW,
    ao_checkOutUserName    OUT VARCHAR2,
compowner 	OUT VARCHAR2,
manager 	OUT VARCHAR2,
legal_form 	OUT VARCHAR2,
addressId 	OUT RAW,
contactId 	OUT RAW,
mwst            OUT INTEGER)
RETURN INTEGER
AS
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
	p_Company_01$retrieve.ALL_RIGHT :=  1;
	p_Company_01$retrieve.INSUFFICIENT_RIGHTS :=  2;
	p_Company_01$retrieve.OBJECTNOTFOUND :=  3;

	p_Company_01$retrieve.retValue :=  p_Company_01$retrieve.ALL_RIGHT;

	/*[SPCONV-ERR(51)]:BEGIN TRAN statement ignored*/
	NULL;
	BEGIN
	p_Company_01$retrieve.retValue:=p_Object$performRetrieve(p_Company_01$retrieve.oid_s,
	 p_Company_01$retrieve.userId,
	 p_Company_01$retrieve.op,
	 p_Company_01$retrieve.state,
	 p_Company_01$retrieve.tVersionId,
	 p_Company_01$retrieve.typeName,
	 p_Company_01$retrieve.name,
	 p_Company_01$retrieve.containerId,
	 p_Company_01$retrieve.containerName,
	 p_Company_01$retrieve.containerKind,
	 p_Company_01$retrieve.isLink,
	 p_Company_01$retrieve.linkedObjectId,
	 p_Company_01$retrieve.owner,
	 p_Company_01$retrieve.ownerName,
	 p_Company_01$retrieve.creationDate,
	 p_Company_01$retrieve.creator,
	 p_Company_01$retrieve.creatorName,
	 p_Company_01$retrieve.lastChanged,
	 p_Company_01$retrieve.changer,
	 p_Company_01$retrieve.changerName,
	 p_Company_01$retrieve.validUntil,
	 p_Company_01$retrieve.description,
	 p_Company_01$retrieve.ao_showInNews,
        ao_checkedOut, ao_checkOutDate, 
        ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName,
	 p_Company_01$retrieve.oid);
	EXCEPTION
		WHEN OTHERS THEN
			StoO_error := SQLCODE;

			StoO_errmsg := SQLERRM;

	END;
	IF  ( p_Company_01$retrieve.retValue = p_Company_01$retrieve.ALL_RIGHT) THEN
	BEGIN

		BEGIN
			StoO_rowcnt := 0;
			StoO_selcnt := 0;
			StoO_error  := 0;

			SELECT   owner,  manager,  legal_form, mwst
			INTO p_Company_01$retrieve.compowner, p_Company_01$retrieve.manager, p_Company_01$retrieve.legal_form,
			     p_Company_01$retrieve.mwst 
			FROM mad_Company_01 
			WHERE oid = p_Company_01$retrieve.oid;
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
			INTO p_Company_01$retrieve.addressId FROM ibs_Object 
			WHERE containerKind = 2 
			 AND tVersionId = 16854785  -- type address
			 AND containerId = p_Company_01$retrieve.oid;
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
			INTO p_Company_01$retrieve.contactId FROM ibs_Object 
			WHERE containerKind = 2 
             AND tVersionId = 16853761  -- type personcontainer (tab contacts)
			 AND containerId = p_Company_01$retrieve.oid;
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

	END;
	END IF;

	COMMIT WORK;
	RETURN p_Company_01$retrieve.retValue;

EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_Company_01$retrieve',
    ', oid_s = ' || oid_s ||
    ', userId = ' || userId  ||
    ', op = ' || op ||
    ', errorcode = ' || StoO_error ||
    ', errormessage = ' || StoO_errmsg);
END p_Company_01$retrieve;
/

show errors;

/******************************************************************************
 * Deletes a Company_01 object and all its values (incl. rights check). <BR>
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
CREATE OR REPLACE FUNCTION p_Company_01$delete(
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
INSUFFICIENT_RIGHTS     NUMBER(10,0);
ALL_RIGHT     NUMBER(10,0);
OBJECTNOTFOUND     NUMBER(10,0);
RIGHT_DELETE     NUMBER(10,0);
retValue     NUMBER(10,0);
rights     NUMBER(10,0);
containerId     RAW (8);

BEGIN
    BEGIN
    p_stringToByte(p_Company_01$delete.oid_s,
     p_Company_01$delete.oid);
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error := SQLCODE;

            StoO_errmsg := SQLERRM;

    END;

    p_Company_01$delete.ALL_RIGHT :=  1;
    p_Company_01$delete.INSUFFICIENT_RIGHTS :=  2;
    p_Company_01$delete.OBJECTNOTFOUND :=  3;
    p_Company_01$delete.RIGHT_DELETE :=  16;

    p_Company_01$delete.retValue :=  p_Company_01$delete.ALL_RIGHT;
    p_Company_01$delete.rights :=  0;

    /*[SPCONV-ERR(35)]:BEGIN TRAN statement ignored*/
    NULL;
    BEGIN
    p_Company_01$delete.retValue:=p_Object$performDelete(
         p_Company_01$delete.oid_s,
     p_Company_01$delete.userId,
     p_Company_01$delete.op,
         p_Company_01$delete.oid);
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error := SQLCODE;

            StoO_errmsg := SQLERRM;

    END;
    
/*    
    IF  ( p_Company_01$delete.retValue = p_Company_01$delete.ALL_RIGHT) THEN
    BEGIN
        BEGIN
        StoO_error   := 0;
        StoO_rowcnt  := 0;
        DELETE  mad_Company_01 
            WHERE oid = p_Company_01$delete.oid;
        StoO_rowcnt := SQL%ROWCOUNT;
        EXCEPTION
            WHEN OTHERS THEN
                StoO_error  := SQLCODE;
                StoO_errmsg := SQLERRM;
        END;
        BEGIN
        StoO_error   := 0;
        StoO_rowcnt  := 0;
        DELETE  m2_Address_01 
            WHERE oid  IN (
        SELECT  oid
             FROM ibs_Object 
            WHERE INSTR(p_Company_01$delete.posNoPath, posNoPath) = 1);
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
            WHERE oid  IN (
        SELECT  oid
             FROM ibs_Object 
            WHERE INSTR(p_Company_01$delete.posNoPath, posNoPath) = 1);
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
            WHERE INSTR(p_Company_01$delete.posNoPath, posNoPath) = 1;
        StoO_rowcnt := SQL%ROWCOUNT;
        EXCEPTION
            WHEN OTHERS THEN
                StoO_error  := SQLCODE;
                StoO_errmsg := SQLERRM;
        END;
    END;
    END IF;
*/    

    COMMIT WORK;
    RETURN p_Company_01$delete.retValue;
exception
  when OTHERS then
    ibs_error.log_error ( ibs_error.error, 'p_Company_01$delete',
                          'oid_s: ' || oid_s ||
                          ', userId: ' || userId ||
                          ', op: ' || op ||
                          ', sqlcode: ' || sqlcode ||
                          ', sqlerrm: ' || sqlerrm );
    return 0;
END p_Company_01$delete;
/

show errors;

/******************************************************************************
 * Copies a Company_01 object and all its values (incl. rights check). <BR>
 */
CREATE OR REPLACE FUNCTION p_Company_01$BOCopy
(
  oid       RAW ,
  userId     INTEGER ,
  newOid     RAW 
)
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
    p_Company_01$BOCopy.NOT_OK :=  0;
    p_Company_01$BOCopy.ALL_RIGHT :=  1;

    p_Company_01$BOCopy.retValue :=  p_Company_01$BOCopy.NOT_OK;
    BEGIN
    StoO_error   := 0;
    StoO_rowcnt  := 0;
    INSERT INTO mad_Company_01 (oid, owner, manager, legal_form, mwst)
    SELECT  p_Company_01$BOCopy.newOid, owner, manager, legal_form, mwst
         FROM mad_Company_01 
        WHERE oid = p_Company_01$BOCopy.oid;
    StoO_rowcnt := SQL%ROWCOUNT;
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error  := SQLCODE;
            StoO_errmsg := SQLERRM;
    END;

    IF  ( StoO_rowcnt >= 1) THEN
        p_Company_01$BOCopy.retValue :=  p_Company_01$BOCopy.ALL_RIGHT;
    END IF;
    RETURN p_Company_01$BOCopy.retValue;
    RETURN 0;
EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_Company_01$BOCopy',
    ', userId = ' || userId  ||
    ', newOid = ' || newOid ||
    ', errorcode = ' || StoO_error ||
    ', errormessage = ' || StoO_errmsg);
END p_Company_01$BOCopy;
/

show errors;

EXIT;