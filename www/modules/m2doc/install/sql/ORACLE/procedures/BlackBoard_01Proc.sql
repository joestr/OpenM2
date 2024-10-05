/******************************************************************************
 * All stored procedures regarding the BlackBoard_01 Object. <BR>
 * 
 * @version     $Id: BlackBoard_01Proc.sql,v 1.3 2003/10/31 00:13:13 klaus Exp $
 *
 * @author      Mario Stegbauer (MS)  980805
 ******************************************************************************
 */


/******************************************************************************
 * Creates a new BlackBoard_01 Object (incl. rights check). <BR>
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
CREATE OR REPLACE FUNCTION p_BlackBoard_01$create(
userId 	INTEGER ,
op 	INTEGER ,
tVersionId 	INTEGER ,
name 	VARCHAR2 ,
containerId_s 	VARCHAR2 ,
containerKind 	INTEGER ,
isLink 	NUMBER ,
linkedObjectId_s 	VARCHAR2 ,
description 	VARCHAR2 ,
oid_s 		OUT VARCHAR2)
RETURN 		INTEGER
AS
c_NOOID         CONSTANT RAW (8) := hexToRaw ('0000000000000000');
StoO_selcnt	INTEGER;
StoO_error 	INTEGER;
StoO_rowcnt	INTEGER;
StoO_errmsg	VARCHAR2(255);
StoO_sqlstatus	INTEGER;
containerId 	RAW (8);
linkedObjectId 	RAW (8);
ALL_RIGHT 	INTEGER;
INSUFFICIENT_RIGHTS 	INTEGER;
ALREADY_EXISTS 	INTEGER;
retValue 	INTEGER;
oid 		RAW (8);

BEGIN
	BEGIN
	p_stringToByte(p_BlackBoard_01$create.containerId_s,
	 p_BlackBoard_01$create.containerId);
	EXCEPTION
		WHEN OTHERS THEN
			StoO_error := SQLCODE;

			StoO_errmsg := SQLERRM;

	END;
	BEGIN
	p_stringToByte(p_BlackBoard_01$create.linkedObjectId_s,
	 p_BlackBoard_01$create.linkedObjectId);
	EXCEPTION
		WHEN OTHERS THEN
			StoO_error := SQLCODE;

			StoO_errmsg := SQLERRM;

	END;

	p_BlackBoard_01$create.ALL_RIGHT :=  1;
	p_BlackBoard_01$create.INSUFFICIENT_RIGHTS :=  2;
	p_BlackBoard_01$create.ALREADY_EXISTS :=  21;

	p_BlackBoard_01$create.retValue :=  p_BlackBoard_01$create.ALL_RIGHT;

	p_BlackBoard_01$create.oid := p_BlackBoard_01$create.c_NOOID;

	/*[SPCONV-ERR(39)]:BEGIN TRAN statement ignored*/
	NULL;
	BEGIN
	p_BlackBoard_01$create.retValue:=p_Object$performCreate(p_BlackBoard_01$create.userId,
	 p_BlackBoard_01$create.op,
	 p_BlackBoard_01$create.tVersionId,
	 p_BlackBoard_01$create.name,
	 p_BlackBoard_01$create.containerId_s,
	 p_BlackBoard_01$create.containerKind,
	 p_BlackBoard_01$create.isLink,
	 p_BlackBoard_01$create.linkedObjectId_s,
	 p_BlackBoard_01$create.description,
	 p_BlackBoard_01$create.oid_s,
	 p_BlackBoard_01$create.oid);
	EXCEPTION
		WHEN OTHERS THEN
			StoO_error := SQLCODE;

			StoO_errmsg := SQLERRM;

	END;
	IF  ( p_BlackBoard_01$create.retValue = p_BlackBoard_01$create.ALL_RIGHT) THEN
	BEGIN
		BEGIN
		StoO_error   := 0;
		StoO_rowcnt  := 0;
		INSERT INTO m2_Discussion_01 (oid, maxlevels, defaultView)VALUES (p_BlackBoard_01$create.oid, 1, 1);
		StoO_rowcnt := SQL%ROWCOUNT;
		EXCEPTION
			WHEN OTHERS THEN
				StoO_error  := SQLCODE;
				StoO_errmsg := SQLERRM;
		END;

	END;
	END IF;

	COMMIT WORK;
	RETURN p_BlackBoard_01$create.retValue;
	RETURN 0;

EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_BlackBoard_01$creat',
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
END p_BlackBoard_01$create;
/

show errors;

EXIT;