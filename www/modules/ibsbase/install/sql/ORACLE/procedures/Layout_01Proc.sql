/******************************************************************************
 * All stored procedures regarding the Layout_01 Object. <BR>
 * 
 * @version     1.10.0001, 05.08.1999
 *
 * @author      Mario Stegbauer (MS)  980805
 ******************************************************************************
 */

/******************************************************************************
 * Creates a new Layout_01 Object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_userId           ID of the user who is creating the object.
 * @param   ai_op               Operation to be performed (used for rights 
 *                              check).
 * @param   ai_tVersionId       Type of the new object.
 * @param   ai_name             Name of the object.
 * @param   ai_containerId_s    ID of the container where object shall be 
 *                              created in.
 * @param   ai_containerKind    Kind of object/container relationship
 * @param   ai_isLink           Defines if the object is a link
 * @param   ai_linkedObjectId_s If the object is a link this is the ID of the
 *                              where the link shows to.
 * @param   ai_description      Description of the object.
 *
 * @output parameters:
 * @param   ao_oid_s            OID of the newly created object.
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
CREATE OR REPLACE FUNCTION p_Layout_01$create
(
    -- input parameters:
    ai_userId           INTEGER,
    ai_op               INTEGER,
    ai_tVersionId       INTEGER,
    ai_name             VARCHAR2,
    ai_containerId_s    VARCHAR2,
    ai_containerKind    INTEGER,
    ai_isLink           NUMBER,
    ai_linkedObjectId_s VARCHAR2,
    ai_description      VARCHAR2,
    -- output parameters:
    ao_oid_s            OUT VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2;
    c_ALREADY_EXISTS        CONSTANT INTEGER := 21;

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_oid                   RAW (8) := c_NOOID; -- the actual oid
    l_containerId           RAW (8);        -- oid of container
    l_linkedObjectId        RAW (8);        -- oid of linked object
    l_allGroupId            INTEGER;        -- group of all users
    l_structAdminGroupId    INTEGER;        -- group of structure administrators
    l_rights                INTEGER;        -- the actual permissions
    l_domainId              INTEGER := ((ai_userId - MOD (ai_userId, 16777216)) / 16777216);
                                            -- id of the actual domain
    l_sysadmin              INTEGER;        -- user id of system administrator

BEGIN
    -- conversions (objectidstring) - all input objectids must be converted
    p_stringToByte (ai_containerId_s, l_containerId);
    p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);

    -- get userId of systemadministrator
    SELECT value 
    INTO l_sysadmin 
    FROM ibs_System 
    WHERE name = 'ID_sysAdmin';

    l_retValue := p_Object$performCreate (ai_userId, ai_op,
        ai_tVersionId, ai_name, ai_containerId_s, ai_containerKind,
        ai_isLink, ai_linkedObjectId_s, ai_description, ao_oid_s, 
        l_oid);


    IF  (l_retValue = c_ALL_RIGHT)
                                        -- object created successfully?
    THEN
        BEGIN
            -- insert the other values:
            INSERT INTO ibs_Layout_01 (oid, name, domainId)
            VALUES (l_oid, ai_name, l_domainId);
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error, 'p_Layout_01$create',
                'Error in INSERT INTO');
            RAISE;
        END;
        
        -- if layout is for other users then sysadmin, set rights for layout
        -- while the layout for sysadmin is created, there are no domains available
        -- so it is not possible and not necessary to set rights for layout
        IF (NOT (ai_userId = l_sysadmin)) 
        THEN
            BEGIN
                -- set rights on layout:  
                SELECT  allGroupId, structAdminGroupId
                INTO    l_allGroupId, l_structAdminGroupId
                FROM    ibs_Domain_01
                WHERE   id = l_domainId;
            EXCEPTION
                WHEN OTHERS THEN
                    ibs_error.log_error ( ibs_error.error, 'p_Layout_01$create',
                    'Error in get GroupIds from domain  l_domainId = ' || l_domainId);
                RAISE;
            END;

            -- set rights for structure administrators:
            IF (NOT (l_structAdminGroupId IS NULL)) -- group found?
            THEN
                BEGIN
                    SELECT  SUM (id)
                    INTO    l_rights 
                    FROM    ibs_Operation;
                EXCEPTION
                    WHEN OTHERS THEN
                        ibs_error.log_error (ibs_error.error, 'p_Layout_01$create',
                        'Error in compute rights for structureadmin');
                    RAISE;
                END;

                p_Rights$setRights (l_oid, l_structAdminGroupId, l_rights, 1);

            END IF; -- if group found

            -- set rights for group of all users:
            IF (NOT (l_allGroupId IS NULL)) -- group found?
            THEN
                BEGIN
                    SELECT  SUM (id)
                    INTO    l_rights
                    FROM    ibs_Operation
                    WHERE   name IN ('view', 'read');
                EXCEPTION
                    WHEN OTHERS THEN
                        ibs_error.log_error (ibs_error.error, 'p_Layout_01$create',
                        'Error in compute rights for allusergroup');
                    RAISE;
                END;

                p_Rights$setRights (l_oid, l_allGroupId, l_rights, 1);

            END IF; -- if group found
        END IF; -- if other user then sysadmin
    END IF; -- object created successfully

    COMMIT WORK;

    RETURN p_Layout_01$create.l_retValue;
EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error ( ibs_error.error, 'p_Layout_01$create',
            ', ai_userId = ' || ai_userId  ||
            ', ai_op = ' || ai_op  ||
            ', ai_tVersionId = ' || ai_tVersionId  ||
            ', ai_name = ' || ai_name  ||
            ', ai_containerId_s = ' || ai_containerId_s  ||
            ', ai_containerKind = ' || ai_containerKind  ||
            ', ai_isLink = ' || ai_isLink  ||
            ', ai_linkedObjectId_s = ' || ai_linkedObjectId_s  ||
            ', ai_description = ' || ai_description  ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM);

    -- return the value computed so far:
    RETURN c_NOT_OK;
END p_Layout_01$create;
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
CREATE OR REPLACE FUNCTION p_Layout_01$change(
oid_s 	        VARCHAR2 ,
userId 	        INTEGER ,
op 	            INTEGER ,
name 	        VARCHAR2 ,
validUntil 	    DATE ,
description 	VARCHAR2 ,
showInNews      INTEGER )
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
	p_Layout_01$change.ALL_RIGHT :=  1;
	p_Layout_01$change.INSUFFICIENT_RIGHTS :=  2;
	p_Layout_01$change.OBJECTNOTFOUND :=  3;

	p_Layout_01$change.retValue :=  p_Layout_01$change.ALL_RIGHT;

	/*[SPCONV-ERR(28)]:BEGIN TRAN statement ignored*/
	NULL;
	BEGIN
	p_Layout_01$change.retValue:=p_Object$performChange(p_Layout_01$change.oid_s,
	 p_Layout_01$change.userId,
	 p_Layout_01$change.op,
	 p_Layout_01$change.name,
	 p_Layout_01$change.validUntil,
	 p_Layout_01$change.description,
	 p_Layout_01$change.showInNews,	 
	 p_Layout_01$change.oid);
	EXCEPTION
		WHEN OTHERS THEN
			StoO_error := SQLCODE;

			StoO_errmsg := SQLERRM;

	END;
	BEGIN
	StoO_error   := 0;
	StoO_rowcnt  := 0;
	UPDATE ibs_Layout_01
	SET name = p_Layout_01$change.name
	
	WHERE oid = p_Layout_01$change.oid;
	StoO_rowcnt := SQL%ROWCOUNT;
	EXCEPTION
		WHEN OTHERS THEN
			StoO_error  := SQLCODE;
			StoO_errmsg := SQLERRM;
	END;

	COMMIT WORK;
	RETURN p_Layout_01$change.retValue;
	RETURN 0;

EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_Layout_01$change',
    ', oid_s = ' || oid_s ||
    ', userId = ' || userId  ||
    ', op = ' || op ||
    ', name = ' || name ||
    ', validUntil = ' || validUntil  ||
    ', description = ' || description ||
    ', showInNews = ' || showInNews ||    
    ', errorcode = ' || StoO_error ||
    ', errormessage = ' || StoO_errmsg);
END p_Layout_01$change;
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
 * @param   @description        Description of the object
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
CREATE OR REPLACE FUNCTION p_Layout_01$retrieve(
oid_s 	        VARCHAR2 ,
userId 	        INTEGER ,
op 	            NUMBER ,
state 	        OUT NUMBER,
tVersionId 	    OUT INTEGER,
typeName 	    OUT VARCHAR2,
name 		    OUT VARCHAR2,
containerId 	OUT RAW,
containerName 	OUT VARCHAR2,
containerKind 	OUT INTEGER,
isLink 		    OUT NUMBER,
linkedObjectId 	OUT RAW,
owner 		    OUT INTEGER,
ownerName 	    OUT VARCHAR2,
creationDate 	OUT DATE,
creator 	    OUT INTEGER,
creatorName 	OUT VARCHAR2,
lastChanged 	OUT DATE,
changer 	    OUT INTEGER,
changerName 	OUT VARCHAR2,
validUntil 	    OUT DATE,
description 	OUT VARCHAR2,
showInNews      OUT INTEGER,
    ao_checkedOut          OUT NUMBER,
    ao_checkOutDate        OUT DATE,
    ao_checkOutUser        OUT INTEGER,
    ao_checkOutUserOid     OUT RAW,
    ao_checkOutUserName    OUT VARCHAR2
)
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
	p_Layout_01$retrieve.ALL_RIGHT :=  1;
	p_Layout_01$retrieve.INSUFFICIENT_RIGHTS :=  2;
	p_Layout_01$retrieve.OBJECTNOTFOUND :=  3;

	p_Layout_01$retrieve.retValue :=  p_Layout_01$retrieve.ALL_RIGHT;

	/*[SPCONV-ERR(43)]:BEGIN TRAN statement ignored*/
	NULL;
	BEGIN
	p_Layout_01$retrieve.retValue:=p_Object$performRetrieve(p_Layout_01$retrieve.oid_s,
	 p_Layout_01$retrieve.userId,
	 p_Layout_01$retrieve.op,
	 p_Layout_01$retrieve.state,
	 p_Layout_01$retrieve.tVersionId,
	 p_Layout_01$retrieve.typeName,
	 p_Layout_01$retrieve.name,
	 p_Layout_01$retrieve.containerId,
	 p_Layout_01$retrieve.containerName,
	 p_Layout_01$retrieve.containerKind,
	 p_Layout_01$retrieve.isLink,
	 p_Layout_01$retrieve.linkedObjectId,
	 p_Layout_01$retrieve.owner,
	 p_Layout_01$retrieve.ownerName,
	 p_Layout_01$retrieve.creationDate,
	 p_Layout_01$retrieve.creator,
	 p_Layout_01$retrieve.creatorName,
	 p_Layout_01$retrieve.lastChanged,
	 p_Layout_01$retrieve.changer,
	 p_Layout_01$retrieve.changerName,
	 p_Layout_01$retrieve.validUntil,
	 p_Layout_01$retrieve.description,
	 p_Layout_01$retrieve.showInNews,	 
         ao_checkedOut, ao_checkOutDate, 
         ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName,
	 p_Layout_01$retrieve.oid);
	EXCEPTION
		WHEN OTHERS THEN
			StoO_error := SQLCODE;

			StoO_errmsg := SQLERRM;

	END;

	BEGIN
		StoO_rowcnt := 0;
		StoO_selcnt := 0;
		StoO_error  := 0;

		SELECT   name
		INTO p_Layout_01$retrieve.name FROM ibs_Layout_01 
		WHERE oid = p_Layout_01$retrieve.oid;
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

	COMMIT WORK;
	RETURN p_Layout_01$retrieve.retValue;
	RETURN 0;

EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_Layout_01$retrieve',
    ', oid_s = ' || oid_s ||
    ', userId = ' || userId  ||
    ', op = ' || op ||
    ', errorcode = ' || StoO_error ||
    ', errormessage = ' || StoO_errmsg);
END p_Layout_01$retrieve;
/

show errors;

/******************************************************************************
 * Deletes a Layout_01 object and all its values (incl. rights check). <BR>
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
CREATE OR REPLACE FUNCTION p_Layout_01$delete(
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
oid 		RAW (8);
INSUFFICIENT_RIGHTS 	INTEGER;
ALL_RIGHT 	INTEGER;
OBJECTNOTFOUND 	INTEGER;
retValue 	INTEGER;

BEGIN
	BEGIN
	p_stringToByte(p_Layout_01$delete.oid_s,
	 p_Layout_01$delete.oid);
	EXCEPTION
		WHEN OTHERS THEN
			StoO_error := SQLCODE;

			StoO_errmsg := SQLERRM;

	END;

	p_Layout_01$delete.ALL_RIGHT :=  1;
	p_Layout_01$delete.INSUFFICIENT_RIGHTS :=  2;
	p_Layout_01$delete.OBJECTNOTFOUND :=  3;

	p_Layout_01$delete.retValue :=  p_Layout_01$delete.ALL_RIGHT;

	/*[SPCONV-ERR(23)]:BEGIN TRAN statement ignored*/
	NULL;
	BEGIN
	p_Layout_01$delete.retValue:=p_Object$performDelete(p_Layout_01$delete.oid_s,
	 p_Layout_01$delete.userId,
	 p_Layout_01$delete.op,
	 p_Layout_01$delete.oid);
	EXCEPTION
		WHEN OTHERS THEN
			StoO_error := SQLCODE;

			StoO_errmsg := SQLERRM;

	END;
	IF  ( p_Layout_01$delete.retValue = p_Layout_01$delete.ALL_RIGHT) THEN
	BEGIN
		BEGIN
		StoO_error   := 0;
		StoO_rowcnt  := 0;
		DELETE  ibs_Layout_01 
			WHERE oid = p_Layout_01$delete.oid;
		StoO_rowcnt := SQL%ROWCOUNT;
		EXCEPTION
			WHEN OTHERS THEN
				StoO_error  := SQLCODE;
				StoO_errmsg := SQLERRM;
		END;
	END;
	END IF;

	COMMIT WORK;
	RETURN p_Layout_01$delete.retValue;
	RETURN 0;

EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_Layout_01$delete',
    ', oid_s = ' || oid_s ||
    ', userId = ' || userId  ||
    ', op = ' || op ||
    ', errorcode = ' || StoO_error ||
    ', errormessage = ' || StoO_errmsg);
END p_Layout_01$delete;
/

show errors;

CREATE OR REPLACE FUNCTION p_Layout_01$BOCopy(
oid     RAW,
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
retValue     INTEGER;

BEGIN
    p_Layout_01$BOCopy.NOT_OK :=  0;
    p_Layout_01$BOCopy.ALL_RIGHT :=  1;

    p_Layout_01$BOCopy.retValue :=  p_Layout_01$BOCopy.NOT_OK;
    BEGIN
    StoO_error   := 0;
    StoO_rowcnt  := 0;
    INSERT INTO ibs_Layout_01 (oid, name)SELECT  p_Layout_01$BOCopy.newOid, b.name
         FROM ibs_Layout_01 b 
        WHERE b.oid = p_Layout_01$BOCopy.oid;
    StoO_rowcnt := SQL%ROWCOUNT;
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error  := SQLCODE;
            StoO_errmsg := SQLERRM;
    END;
    IF  ( StoO_rowcnt >= 1) THEN
        p_Layout_01$BOCopy.retValue :=  p_Layout_01$BOCopy.ALL_RIGHT;
    END IF;
    RETURN p_Layout_01$BOCopy.retValue;
    RETURN 0;

EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_Layout_01$BOCopy',
    ', userId = ' || userId  ||
    ', newOid = ' || newOid ||
    ', errorcode = ' || StoO_error ||
    ', errormessage = ' || StoO_errmsg);
END p_Layout_01$BOCopy;
/

show errors;

exit;