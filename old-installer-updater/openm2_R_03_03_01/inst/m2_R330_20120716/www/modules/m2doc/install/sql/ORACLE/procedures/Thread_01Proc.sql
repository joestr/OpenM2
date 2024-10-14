/******************************************************************************
 * All stored procedures regarding the Thread_01 Object. <BR>
 * 
 * @version     $Id: Thread_01Proc.sql,v 1.9 2003/10/31 00:13:15 klaus Exp $
 *
 * @author      Keim Christine (Ck)  980504
 ******************************************************************************
 */

/******************************************************************************
 * Creates a new Thread_01 Object (incl. rights check). <BR>
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
CREATE OR REPLACE FUNCTION p_Thread_01$create(
ai_userId         INTEGER ,
ai_op         NUMBER ,
ai_tVersionId     NUMBER ,
ai_name         VARCHAR2 ,
ai_containerId_s     VARCHAR2 ,
ai_containerKind     NUMBER ,
ai_isLink         NUMBER ,
ai_linkedObjectId_s     VARCHAR2 ,
ai_description     VARCHAR2 ,
ao_oid_s         OUT VARCHAR2)
RETURN INTEGER
AS
c_NOOID         CONSTANT RAW (8) := hexToRaw ('0000000000000000');
StoO_selcnt    INTEGER;
StoO_error     INTEGER;
StoO_rowcnt    INTEGER;
StoO_errmsg    VARCHAR2(255);
StoO_sqlstatus    INTEGER;
l_containerId     RAW (8);
l_linkedObjectId     RAW (8);
c_ALL_RIGHT     NUMBER(10,0);
c_INSUFFICIENT_RIGHTS     NUMBER(10,0);
c_ALREADY_EXISTS     NUMBER(10,0);
l_retValue     NUMBER(10,0);
l_oid         RAW (8);
l_description VARCHAR2(255);

BEGIN
    BEGIN
    p_stringToByte(ai_containerId_s,
 l_containerId);
    EXCEPTION
        WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error,'p_Thread_01$create','Error in p_StringToByte');
            RAISE;
    END;
    BEGIN
    p_stringToByte(ai_linkedObjectId_s,
     l_linkedObjectId);
    EXCEPTION
        WHEN OTHERS THEN
            ibs_error.log_error (ibs_error.error,'p_Thread_01$create','Error in p_StringToByte');
            RAISE;
    END;

    c_ALL_RIGHT :=  1;
    c_INSUFFICIENT_RIGHTS :=  2;
    c_ALREADY_EXISTS :=  21;
    l_retValue :=  c_ALL_RIGHT;
    l_oid := c_NOOID;

--    /*[SPCONV-ERR(39)]:BEGIN TRAN statement ignored*/
--    NULL;
    BEGIN
    l_retValue:=p_Object$performCreate(ai_userId,
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
    EXCEPTION
        WHEN OTHERS THEN
            ibs_error.log_error (ibs_error.error,'p_Thread_01$create','Error in p_OBject$performcreate');
        RAISE;
    END;
    IF  (l_retValue = c_ALL_RIGHT) THEN
    BEGIN
    l_description := ai_description;
        IF (l_description IS NULL) THEN
          l_description := ' ';
        END IF;
        BEGIN
        StoO_error   := 0;
        StoO_rowcnt  := 0;
        INSERT INTO m2_Article_01 (oid, content, discussionId)
       VALUES(l_oid,l_description,l_containerId);

        StoO_rowcnt := SQL%ROWCOUNT;
        EXCEPTION
            WHEN OTHERS THEN
            ibs_error.log_error (ibs_error.error,'p_Thread_01$create','Error in INSERT INTO m2_Article_01');
            RAISE;
        END;
    END;
    END IF;

    COMMIT WORK;
    RETURN l_retValue;
--    RETURN 0;

EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_Thread_01$create',
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
END p_Thread_01$create;
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
CREATE OR REPLACE FUNCTION p_Thread_01$change(
oid_s     VARCHAR2 ,
userId     NUMBER ,
op     NUMBER ,
name     VARCHAR2 ,
validUntil     DATE ,
description  VARCHAR2,
ai_showInNews   INTEGER
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
retValue    INTEGER;
oid         RAW (8);

BEGIN
    p_Thread_01$change.ALL_RIGHT :=  1;
    p_Thread_01$change.INSUFFICIENT_RIGHTS :=  2;
    p_Thread_01$change.OBJECTNOTFOUND :=  3;

    p_Thread_01$change.retValue :=  p_Thread_01$change.ALL_RIGHT;

    BEGIN
    p_Thread_01$change.retValue:=p_Object$performChange(p_Thread_01$change.oid_s,
     p_Thread_01$change.userId,
     p_Thread_01$change.op,
     p_Thread_01$change.name,
     p_Thread_01$change.validUntil,
     '',
     p_Thread_01$change.ai_showInNews,
     p_Thread_01$change.oid);
    EXCEPTION
        WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error,'p_Thread_01$change',
                        'Error in p_Object$performChange');
        raise;
    END;
    BEGIN
    StoO_error   := 0;
    StoO_rowcnt  := 0;
    UPDATE m2_Article_01
    SET state = (
    SELECT  o.state
     FROM ibs_Object o 
    WHERE o.oid = p_Thread_01$change.oid)
    
    WHERE oid = p_Thread_01$change.oid;
    StoO_rowcnt := SQL%ROWCOUNT;
    EXCEPTION
        WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error,'p_Thread_01$change',
                        'Error in SQL Statement 1');
        raise;
    END;

    COMMIT WORK;
    RETURN p_Thread_01$change.retValue;
exception
  when OTHERS then
    ibs_error.log_error ( ibs_error.error, 'p_Thread_01$change',
                          'oid_s: ' || oid_s ||
                          ', userId: ' || userId ||
                          ', op: ' || op ||
                          ', name: ' || name ||
                          ', validUntil: ' || validUntil ||
                          ', sqlcode: ' || SQLCODE ||
                          ', sqlerrm: ' || SQLERRM );
    return 0;
END p_Thread_01$change;
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
CREATE OR REPLACE FUNCTION p_Thread_01$retrieve(
oid_s 	VARCHAR2 ,
userId 	INTEGER ,
op 	NUMBER ,
state 	OUT NUMBER,
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
hasSubEntries 	OUT NUMBER,
rights 		OUT NUMBER)
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
	p_Thread_01$retrieve.ALL_RIGHT :=  1;
	p_Thread_01$retrieve.INSUFFICIENT_RIGHTS :=  2;
	p_Thread_01$retrieve.OBJECTNOTFOUND :=  3;

	p_Thread_01$retrieve.retValue :=  p_Thread_01$retrieve.ALL_RIGHT;

	/*[SPCONV-ERR(46)]:BEGIN TRAN statement ignored*/
	NULL;
	BEGIN
	p_Thread_01$retrieve.retValue:=p_Object$performRetrieve(p_Thread_01$retrieve.oid_s,
	 p_Thread_01$retrieve.userId,
	 p_Thread_01$retrieve.op,
	 p_Thread_01$retrieve.state,
	 p_Thread_01$retrieve.tVersionId,
	 p_Thread_01$retrieve.typeName,
	 p_Thread_01$retrieve.name,
	 p_Thread_01$retrieve.containerId,
	 p_Thread_01$retrieve.containerName,
	 p_Thread_01$retrieve.containerKind,
	 p_Thread_01$retrieve.isLink,
	 p_Thread_01$retrieve.linkedObjectId,
	 p_Thread_01$retrieve.owner,
	 p_Thread_01$retrieve.ownerName,
	 p_Thread_01$retrieve.creationDate,
	 p_Thread_01$retrieve.creator,
	 p_Thread_01$retrieve.creatorName,
	 p_Thread_01$retrieve.lastChanged,
	 p_Thread_01$retrieve.changer,
	 p_Thread_01$retrieve.changerName,
	 p_Thread_01$retrieve.validUntil,
	 p_Thread_01$retrieve.description,
	 p_Thread_01$retrieve.ao_showInNews,
	 ao_checkedOut, ao_checkOutDate, 
         ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName,
	 p_Thread_01$retrieve.oid);
	EXCEPTION
		WHEN OTHERS THEN
			StoO_error := SQLCODE;

			StoO_errmsg := SQLERRM;

	END;
	IF  ( p_Thread_01$retrieve.retValue = p_Thread_01$retrieve.ALL_RIGHT) THEN
	BEGIN
		BEGIN
			StoO_rowcnt := 0;
			StoO_selcnt := 0;
			StoO_error  := 0;

			SELECT   COUNT(*)
			INTO p_Thread_01$retrieve.hasSubEntries FROM ibs_Object 
			WHERE tVersionId  IN (16844033) 
                         AND state = 2       
			 AND containerId = p_Thread_01$retrieve.oid;
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
	BEGIN
	p_Rights$getRights(p_Thread_01$retrieve.oid,
	 p_Thread_01$retrieve.userId,
	 p_Thread_01$retrieve.rights);
	EXCEPTION
		WHEN OTHERS THEN
			StoO_error := SQLCODE;

			StoO_errmsg := SQLERRM;

	END;

	COMMIT WORK;
	RETURN p_Thread_01$retrieve.retValue;
	RETURN 0;

EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_Thread_01$retrieve',
    ', oid_s = ' || oid_s ||
    ', userId = ' || userId  ||
    ', op = ' || op ||
    ', errorcode = ' || StoO_error ||
    ', errormessage = ' || StoO_errmsg);
END p_Thread_01$retrieve;
/

show errors;

/******************************************************************************
 * Deletes a Thread_01 object and all its values (incl. rights check). <BR>
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
CREATE OR REPLACE FUNCTION p_Thread_01$delete(
oid_s 	VARCHAR2 ,
userId 	INTEGER ,
op 	NUMBER )
RETURN INTEGER
AS
StoO_selcnt	INTEGER;
StoO_error 	INTEGER;
StoO_rowcnt	INTEGER;
StoO_errmsg	VARCHAR2(255);
StoO_sqlstatus	INTEGER;
oid 		RAW (8);
ALL_RIGHT 	NUMBER(10,0);
INSUFFICIENT_RIGHTS 	NUMBER(10,0);
OBJECTNOTFOUND 	NUMBER(10,0);
RIGHT_DELETE 	NUMBER(10,0);
retValue 	NUMBER(10,0);
rights 	NUMBER(10,0);
containerId 	RAW (8);
dummy		RAW (8);

BEGIN
	BEGIN
	p_stringToByte(p_Thread_01$delete.oid_s,
	 p_Thread_01$delete.oid);
	EXCEPTION
		WHEN OTHERS THEN
			StoO_error := SQLCODE;

			StoO_errmsg := SQLERRM;

	END;

	p_Thread_01$delete.ALL_RIGHT :=  1;
	p_Thread_01$delete.INSUFFICIENT_RIGHTS :=  2;
	p_Thread_01$delete.OBJECTNOTFOUND :=  3;
	p_Thread_01$delete.RIGHT_DELETE :=  16;

	p_Thread_01$delete.retValue :=  p_Thread_01$delete.ALL_RIGHT;
	p_Thread_01$delete.rights :=  0;

	/*[SPCONV-ERR(36)]:BEGIN TRAN statement ignored*/
	NULL;
	BEGIN
	p_Thread_01$delete.retValue:=p_Object$performDelete(p_Thread_01$delete.oid_s,
	 p_Thread_01$delete.userId,
	 p_Thread_01$delete.op,
	 p_Thread_01$delete.dummy);
	EXCEPTION
		WHEN OTHERS THEN
			StoO_error := SQLCODE;

			StoO_errmsg := SQLERRM;

	END;
	IF  ( p_Thread_01$delete.retValue = p_Thread_01$delete.ALL_RIGHT) THEN
	BEGIN
	    NULL;
/*
		BEGIN
		StoO_error   := 0;
		StoO_rowcnt  := 0;
		DELETE  m2_Article_01 
			WHERE oid = p_Thread_01$delete.oid;
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
	RETURN p_Thread_01$delete.retValue;
	RETURN 0;

EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_Thread_01$delete',
    ', oid_s = ' || oid_s ||
    ', userId = ' || userId  ||
    ', op = ' || op ||
    ', errorcode = ' || StoO_error ||
    ', errormessage = ' || StoO_errmsg);
END p_Thread_01$delete;
/

show errors;

/******************************************************************************
 * Get the CLOB value out of the DB. <BR>
 */
create or replace function p_Thread_01$getExtended 
( 
    ai_oid VARCHAR2, 
    ao_content OUT CLOB 
)
return integer
as
    l_oid RAW(8);
begin
    p_stringToByte(ai_oid, l_oid);
  
    select  content 
    into    ao_content
    from    m2_Article_01 
    where   oid = l_oid;

    return 1;
exception
    when OTHERS then
        ibs_error.log_error(ibs_error.error, 'p_Article_01$getExtended',
                        'Input: ' || ai_oid || ', errorcode = ' || SQLCODE ||
                        ', errmsg = ' || SQLERRM );
        p_Thread_01$getExtended.ao_content := EMPTY_CLOB ();
    return 0;
end p_Thread_01$getExtended;
/

show errors;

EXIT;
