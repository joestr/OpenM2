/******************************************************************************
 * All stored procedures regarding the Discussion_01 Object. <BR>
 *
 * @version     $Id: Discussion_01Proc.sql,v 1.7 2003/10/31 00:13:14 klaus Exp $
 *
 * @author      Mario Stegbauer (MS)  980805
 ******************************************************************************
 */


/******************************************************************************
 * Creates a new Discussion_01 Object (incl. rights check). <BR>
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
CREATE OR REPLACE FUNCTION p_Discussion_01$create(
userId         NUMBER ,
op         NUMBER ,
tVersionId     NUMBER ,
name         VARCHAR2 ,
containerId_s     VARCHAR2 ,
containerKind     NUMBER ,
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
linkedObjectId     RAW (8);
ALL_RIGHT     NUMBER(10,0);
INSUFFICIENT_RIGHTS     NUMBER(10,0);
ALREADY_EXISTS     NUMBER(10,0);
retValue     NUMBER(10,0);
oid         RAW (8);

BEGIN
    BEGIN
    p_stringToByte(p_Discussion_01$create.containerId_s,
     p_Discussion_01$create.containerId);
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error := SQLCODE;

            StoO_errmsg := SQLERRM;

    END;
    BEGIN
    p_stringToByte(p_Discussion_01$create.linkedObjectId_s,
     p_Discussion_01$create.linkedObjectId);
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error := SQLCODE;

            StoO_errmsg := SQLERRM;

    END;

    p_Discussion_01$create.ALL_RIGHT :=  1;
    p_Discussion_01$create.INSUFFICIENT_RIGHTS :=  2;
    p_Discussion_01$create.ALREADY_EXISTS :=  21;

    p_Discussion_01$create.retValue :=  p_Discussion_01$create.ALL_RIGHT;

    p_Discussion_01$create.oid := p_Discussion_01$create.c_NOOID;

    /*[SPCONV-ERR(39)]:BEGIN TRAN statement ignored*/
    NULL;
    BEGIN
    p_Discussion_01$create.retValue:=p_Object$performCreate(p_Discussion_01$create.userId,
     p_Discussion_01$create.op,
     p_Discussion_01$create.tVersionId,
     p_Discussion_01$create.name,
     p_Discussion_01$create.containerId_s,
     p_Discussion_01$create.containerKind,
     p_Discussion_01$create.isLink,
     p_Discussion_01$create.linkedObjectId_s,
     p_Discussion_01$create.description,
     p_Discussion_01$create.oid_s,
     p_Discussion_01$create.oid);
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error := SQLCODE;

            StoO_errmsg := SQLERRM;

    END;
    IF  ( p_Discussion_01$create.retValue = p_Discussion_01$create.ALL_RIGHT) THEN
    BEGIN
        BEGIN
        StoO_error   := 0;
        StoO_rowcnt  := 0;
        INSERT INTO m2_Discussion_01 (oid, maxlevels, defaultView)VALUES (p_Discussion_01$create.oid, 6, 0);
        StoO_rowcnt := SQL%ROWCOUNT;
        EXCEPTION
            WHEN OTHERS THEN
                StoO_error  := SQLCODE;
                StoO_errmsg := SQLERRM;
        END;
    END;
    END IF;

    COMMIT WORK;
    RETURN p_Discussion_01$create.retValue;
    RETURN 0;

EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_Discussion_01$create',
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
END p_Discussion_01$create;
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
CREATE OR REPLACE FUNCTION p_Discussion_01$change(
oid_s 	VARCHAR2 ,
userId 	INTEGER ,
op 	INTEGER ,
name 	VARCHAR2 ,
validUntil 	DATE ,
description 	VARCHAR2 ,
ai_showInNews  INTEGER,
maxlevels 	INTEGER ,
defaultView 	INTEGER )
RETURN INTEGER
AS
StoO_selcnt	INTEGER;
StoO_error 	INTEGER;
StoO_rowcnt	INTEGER;
StoO_errmsg	VARCHAR2(255);
StoO_sqlstatus	INTEGER;
ALL_RIGHT 	INTEGER;
INSUFFICIENT_RIGHTS 	INTEGER;
OBJECTNOTFOUND 	INTEGER;
retValue 	INTEGER;
oid 		RAW (8);

BEGIN
	p_Discussion_01$change.ALL_RIGHT :=  1;
	p_Discussion_01$change.INSUFFICIENT_RIGHTS :=  2;
	p_Discussion_01$change.OBJECTNOTFOUND :=  3;

	p_Discussion_01$change.retValue :=  p_Discussion_01$change.ALL_RIGHT;

	/*[SPCONV-ERR(28)]:BEGIN TRAN statement ignored*/
	NULL;
	BEGIN
	p_Discussion_01$change.retValue:=p_Object$performChange(p_Discussion_01$change.oid_s,
	 p_Discussion_01$change.userId,
	 p_Discussion_01$change.op,
	 p_Discussion_01$change.name,
	 p_Discussion_01$change.validUntil,
	 p_Discussion_01$change.description,
	 p_Discussion_01$change.ai_showInNews,
	 p_Discussion_01$change.oid);
	EXCEPTION
		WHEN OTHERS THEN
			StoO_error := SQLCODE;

			StoO_errmsg := SQLERRM;

	END;
	IF  ( p_Discussion_01$change.retValue = p_Discussion_01$change.ALL_RIGHT) THEN
	BEGIN
		BEGIN
		StoO_error   := 0;
		StoO_rowcnt  := 0;
		UPDATE m2_Discussion_01
		SET maxlevels = p_Discussion_01$change.maxlevels,
		defaultView = p_Discussion_01$change.defaultView
		
		WHERE oid = p_Discussion_01$change.oid;
		StoO_rowcnt := SQL%ROWCOUNT;
		EXCEPTION
			WHEN OTHERS THEN
				StoO_error  := SQLCODE;
				StoO_errmsg := SQLERRM;
		END;
	END;
	END IF;

	COMMIT WORK;
	RETURN p_Discussion_01$change.retValue;
	RETURN 0;
EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_Discussion_01$change',
    ', oid_s = ' || oid_s ||
    ', userId = ' || userId  ||
    ', op = ' || op ||
    ', name = ' || name ||
    ', validUntil = ' || validUntil  ||
    ', description = ' || description ||
    ', maxlevels = ' || maxlevels ||
    ', defaultView = ' || defaultView ||
    ', errorcode = ' || StoO_error ||
    ', errormessage = ' || StoO_errmsg);
END p_Discussion_01$change;
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
CREATE OR REPLACE FUNCTION p_Discussion_01$retrieve(
oid_s 	VARCHAR2 ,
userId 	INTEGER ,
op 	INTEGER ,
state 	OUT NUMBER,
tVersionId 	OUT INTEGER,
typeName 	OUT VARCHAR2,
name 		OUT VARCHAR2,
containerId 	OUT RAW,
containerName 	OUT VARCHAR2,
containerKind 	OUT INTEGER,
isLink 		OUT INTEGER,
linkedObjectId 	OUT RAW,
owner 		OUT INTEGER,
ownerName 	OUT VARCHAR2,
creationDate 	OUT DATE,
creator 	OUT INTEGER,
creatorName 	OUT VARCHAR2,
lastChanged 	OUT DATE,
changer 	OUT INTEGER,
changerName 	OUT VARCHAR2,
validUntil 	OUT DATE,
description 	OUT VARCHAR2,
ao_showInNews  OUT INTEGER,
    ao_checkedOut          OUT NUMBER,
    ao_checkOutDate        OUT DATE,
    ao_checkOutUser        OUT INTEGER,
    ao_checkOutUserOid     OUT RAW,
    ao_checkOutUserName    OUT VARCHAR2,
maxlevels 	OUT INTEGER,
defaultView 	OUT INTEGER)
RETURN INTEGER
AS
StoO_selcnt	INTEGER;
StoO_error 	INTEGER;
StoO_rowcnt	INTEGER;
StoO_errmsg	VARCHAR2(255);
StoO_sqlstatus	INTEGER;
ALL_RIGHT 	INTEGER;
INSUFFICIENT_RIGHTS 	INTEGER;
OBJECTNOTFOUND 	INTEGER;
retValue 	INTEGER;
oid 		RAW (8);

BEGIN
	p_Discussion_01$retrieve.ALL_RIGHT :=  1;
	p_Discussion_01$retrieve.INSUFFICIENT_RIGHTS :=  2;
	p_Discussion_01$retrieve.OBJECTNOTFOUND :=  3;

	p_Discussion_01$retrieve.retValue :=  p_Discussion_01$retrieve.ALL_RIGHT;

	/*[SPCONV-ERR(46)]:BEGIN TRAN statement ignored*/
	NULL;
	BEGIN
	p_Discussion_01$retrieve.retValue:=p_Object$performRetrieve(p_Discussion_01$retrieve.oid_s,
	 p_Discussion_01$retrieve.userId,
	 p_Discussion_01$retrieve.op,
	 p_Discussion_01$retrieve.state,
	 p_Discussion_01$retrieve.tVersionId,
	 p_Discussion_01$retrieve.typeName,
	 p_Discussion_01$retrieve.name,
	 p_Discussion_01$retrieve.containerId,
	 p_Discussion_01$retrieve.containerName,
	 p_Discussion_01$retrieve.containerKind,
	 p_Discussion_01$retrieve.isLink,
	 p_Discussion_01$retrieve.linkedObjectId,
	 p_Discussion_01$retrieve.owner,
	 p_Discussion_01$retrieve.ownerName,
	 p_Discussion_01$retrieve.creationDate,
	 p_Discussion_01$retrieve.creator,
	 p_Discussion_01$retrieve.creatorName,
	 p_Discussion_01$retrieve.lastChanged,
	 p_Discussion_01$retrieve.changer,
	 p_Discussion_01$retrieve.changerName,
	 p_Discussion_01$retrieve.validUntil,
	 p_Discussion_01$retrieve.description,
	 p_Discussion_01$retrieve.ao_showInNews,
         ao_checkedOut, ao_checkOutDate, 
         ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName,
	 p_Discussion_01$retrieve.oid);
	EXCEPTION
		WHEN OTHERS THEN
			StoO_error := SQLCODE;

			StoO_errmsg := SQLERRM;

	END;
	IF  ( p_Discussion_01$retrieve.retValue = p_Discussion_01$retrieve.ALL_RIGHT) THEN
	BEGIN
		BEGIN
			StoO_rowcnt := 0;
			StoO_selcnt := 0;
			StoO_error  := 0;

			SELECT   maxlevels,  defaultView
			INTO p_Discussion_01$retrieve.maxlevels, p_Discussion_01$retrieve.defaultView FROM m2_Discussion_01 
			WHERE oid = p_Discussion_01$retrieve.oid;
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
	RETURN p_Discussion_01$retrieve.retValue;
	RETURN 0;

EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_Discussion_01$retrieve',
    ', oid_s = ' || oid_s ||
    ', userId = ' || userId  ||
    ', op = ' || op ||
    ', errorcode = ' || StoO_error ||
    ', errormessage = ' || StoO_errmsg);
END p_Discussion_01$retrieve;
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
CREATE OR REPLACE FUNCTION p_Discussion_01$delete(
oid_s 	VARCHAR2 ,
userId 	INTEGER ,
op 	INTEGER )
RETURN INTEGER
AS
StoO_selcnt	INTEGER;
StoO_error 	INTEGER;
StoO_rowcnt	INTEGER;
StoO_errmsg	VARCHAR2(255);
StoO_sqlstatus	INTEGER;
oid 	RAW (8);
INSUFFICIENT_RIGHTS 	INTEGER;
ALL_RIGHT 	INTEGER;
OBJECTNOTFOUND 	INTEGER;
RIGHT_DELETE 	INTEGER;
retValue 	INTEGER;
rights 	INTEGER;
containerId 	RAW (8);
dummy		RAW (8);
BEGIN
	BEGIN
	p_stringToByte(p_Discussion_01$delete.oid_s,
	 p_Discussion_01$delete.oid);
	EXCEPTION
		WHEN OTHERS THEN
			StoO_error := SQLCODE;

			StoO_errmsg := SQLERRM;

	END;

	p_Discussion_01$delete.ALL_RIGHT :=  1;
	p_Discussion_01$delete.INSUFFICIENT_RIGHTS :=  2;
	p_Discussion_01$delete.OBJECTNOTFOUND :=  3;
	p_Discussion_01$delete.RIGHT_DELETE :=  16;

	p_Discussion_01$delete.retValue :=  p_Discussion_01$delete.ALL_RIGHT;
	p_Discussion_01$delete.rights :=  0;

	/*[SPCONV-ERR(36)]:BEGIN TRAN statement ignored*/
	NULL;
	BEGIN
	p_Discussion_01$delete.retValue:=p_Object$performDelete(p_Discussion_01$delete.oid_s,
	 p_Discussion_01$delete.userId,
	 p_Discussion_01$delete.op, p_Discussion_01$delete.dummy);
	EXCEPTION
		WHEN OTHERS THEN
			StoO_error := SQLCODE;

			StoO_errmsg := SQLERRM;

	END;
	IF  ( p_Discussion_01$delete.retValue = p_Discussion_01$delete.ALL_RIGHT) THEN
	BEGIN
	    NULL;
/*	
		BEGIN
		StoO_error   := 0;
		StoO_rowcnt  := 0;
		DELETE  m2_Discussion_01 
			WHERE oid = p_Discussion_01$delete.oid;
		StoO_rowcnt := SQL%ROWCOUNT;
		EXCEPTION
			WHEN OTHERS THEN
				StoO_error  := SQLCODE;
				StoO_errmsg := SQLERRM;
		END;
*/
	END;
	END IF;

	COMMIT WORK;
	RETURN p_Discussion_01$delete.retValue;
	RETURN 0;
EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_Discussion_01$delete',
    ', oid_s = ' || oid_s ||
    ', userId = ' || userId  ||
    ', op = ' || op ||
    ', errorcode = ' || StoO_error ||
    ', errormessage = ' || StoO_errmsg);
END p_Discussion_01$delete;
/

show errors;

CREATE OR REPLACE FUNCTION p_Discussion_01$BOCopy
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
NOT_OK     NUMBER(10,0);
ALL_RIGHT     NUMBER(10,0);
retValue     NUMBER(10,0);

BEGIN
    p_Discussion_01$BOCopy.NOT_OK :=  0;
    p_Discussion_01$BOCopy.ALL_RIGHT :=  1;

    p_Discussion_01$BOCopy.retValue :=  p_Discussion_01$BOCopy.NOT_OK;
    BEGIN
    StoO_error   := 0;
    StoO_rowcnt  := 0;
    INSERT INTO m2_Discussion_01 (oid, maxlevels, defaultView, refOid)
    SELECT  p_Discussion_01$BOCopy.newOid, b.maxlevels, b.defaultView, b.refOid
         FROM m2_Discussion_01 b 
        WHERE b.oid = p_Discussion_01$BOCopy.oid;
    StoO_rowcnt := SQL%ROWCOUNT;
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error  := SQLCODE;
            StoO_errmsg := SQLERRM;
    END;

    IF  ( StoO_rowcnt >= 1) THEN
        p_Discussion_01$BOCopy.retValue :=  p_Discussion_01$BOCopy.ALL_RIGHT;
    END IF;
    RETURN p_Discussion_01$BOCopy.retValue;
    RETURN 0;

EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_Discussion_01$BOCopy',
    ', userId = ' || userId  ||
    ', newOid = ' || newOid ||
    ', errorcode = ' || StoO_error ||
    ', errormessage = ' || StoO_errmsg);
END p_Discussion_01$BOCopy;
/

show errors;

EXIT;
