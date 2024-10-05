/******************************************************************************
 * All stored procedures regarding the PersonContainer_01 Object. <BR>
 * 
 * @version     $Id: PersonContainer_01Proc.sql,v 1.9 2003/10/31 00:13:14 klaus Exp $
 *
 * @author      Mario Stegbauer (MS)  980805
 ******************************************************************************
 */


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
CREATE OR REPLACE FUNCTION p_PersonContainer_01$retrieve(
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
ao_showInNews   OUT INTEGER,
    ao_checkedOut          OUT NUMBER,
    ao_checkOutDate        OUT DATE,
    ao_checkOutUser        OUT INTEGER,
    ao_checkOutUserOid     OUT RAW,
    ao_checkOutUserName    OUT VARCHAR2)
RETURN INTEGER
AS
StoO_selcnt	INTEGER;
StoO_error 	INTEGER;
StoO_rowcnt	INTEGER;
StoO_errmsg	VARCHAR2(255);
StoO_sqlstatus	INTEGER;
oid 		RAW (8);
INSUFFICIENT_RIGHTS 	NUMBER(10,0);
ALL_RIGHT 	NUMBER(10,0);
OBJECTNOTFOUND 	NUMBER(10,0);
RIGHT_READ 	NUMBER(10,0);
retValue 	NUMBER(10,0);
rights 		NUMBER(10,0);
dummy           INTEGER;
c_INNEWS        CONSTANT INTEGER := 4;
c_ISCHECKEDOUT  CONSTANT INTEGER := 16;
    l_tempName              VARCHAR2(255);
    l_tempOid               RAW (8) := hexToRaw ('0000000000000000');
    l_rights                    INTEGER := 0;    
    l_containerId           RAW (8) := hexToRaw ('0000000000000000');
BEGIN
	BEGIN
	p_stringToByte(p_PersonContainer_01$retrieve.oid_s,
	 p_PersonContainer_01$retrieve.oid);
	EXCEPTION
		WHEN OTHERS THEN
			StoO_error := SQLCODE;

			StoO_errmsg := SQLERRM;

	END;

	p_PersonContainer_01$retrieve.ALL_RIGHT :=  1;
	p_PersonContainer_01$retrieve.INSUFFICIENT_RIGHTS :=  2;
	p_PersonContainer_01$retrieve.OBJECTNOTFOUND :=  3;
	p_PersonContainer_01$retrieve.RIGHT_READ :=  2;
	p_PersonContainer_01$retrieve.retValue :=  p_PersonContainer_01$retrieve.ALL_RIGHT;
	p_PersonContainer_01$retrieve.rights :=  0;

	BEGIN
		StoO_rowcnt := 0;
		StoO_selcnt := 0;
		StoO_error  := 0;

		SELECT   o.containerId,  o2.name
		INTO p_PersonContainer_01$retrieve.containerId, p_PersonContainer_01$retrieve.containerName FROM ibs_Object o, ibs_Object o2 
		WHERE o.oid = p_PersonContainer_01$retrieve.oid 
		 AND o2.oid = o.containerId;
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

	IF  ( StoO_rowcnt > 0) THEN
	BEGIN
		BEGIN
		p_PersonContainer_01$retrieve.rights := p_Rights$checkRights(p_PersonContainer_01$retrieve.oid,
		 p_PersonContainer_01$retrieve.containerId,
		 p_PersonContainer_01$retrieve.userId,
		 p_PersonContainer_01$retrieve.op,
		 p_PersonContainer_01$retrieve.rights);
		EXCEPTION
			WHEN OTHERS THEN
				StoO_error := SQLCODE;

				StoO_errmsg := SQLERRM;

		END;
		
		IF (p_PersonContainer_01$retrieve.rights > 0) THEN
		BEGIN
			BEGIN
			        StoO_rowcnt := 0;
			        StoO_selcnt := 0;
			        StoO_error   := 0;

				SELECT   o.state,  o.tVersionId,  o.typeName,  o.name,  o.containerId,  o.containerKind,
				  o.isLink,  o.linkedObjectId,  o.owner,  own.fullname,  o.creationDate,  o.creator,
				  cr.fullname,  o.lastChanged,  o.changer,  ch.fullname,  o.validUntil, B_AND(o.flags,c_INNEWS), B_AND(o.flags, c_ISCHECKEDOUT)
				INTO p_PersonContainer_01$retrieve.state, p_PersonContainer_01$retrieve.tVersionId, p_PersonContainer_01$retrieve.typeName, p_PersonContainer_01$retrieve.name, p_PersonContainer_01$retrieve.containerId, p_PersonContainer_01$retrieve.containerKind,
				 p_PersonContainer_01$retrieve.isLink, p_PersonContainer_01$retrieve.linkedObjectId, p_PersonContainer_01$retrieve.owner, p_PersonContainer_01$retrieve.ownerName, p_PersonContainer_01$retrieve.creationDate, p_PersonContainer_01$retrieve.creator,
				 p_PersonContainer_01$retrieve.creatorName, p_PersonContainer_01$retrieve.lastChanged, p_PersonContainer_01$retrieve.changer, p_PersonContainer_01$retrieve.changerName, p_PersonContainer_01$retrieve.validUntil, ao_showInNews, ao_checkedOut
				FROM ibs_Object o, ibs_User own, ibs_User cr, ibs_User ch 
				WHERE (o.owner = own.id(+)) AND (o.creator = cr.id(+)) AND (o.changer 
				   = ch.id(+)) AND
				(o.oid = p_PersonContainer_01$retrieve.oid);
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
			p_PersonContainer_01$retrieve.dummy := p_setRead(p_PersonContainer_01$retrieve.oid,
			 p_PersonContainer_01$retrieve.userId);
			EXCEPTION
				WHEN OTHERS THEN
					StoO_error := SQLCODE;

					StoO_errmsg := SQLERRM;

			END;

            -- is the checkout flag set?
            -- is the checkout flag set?
            IF (ao_checkedOut = c_ISCHECKEDOUT)
            THEN
                BEGIN
                    -- get the info who checked out the object
                    SELECT  ch.checkout, ch.userid, u.oid, u.name
                    INTO    ao_checkOutDate, ao_checkOutUser, l_tempOid, l_tempName
                    FROM   ibs_CheckOut_01 ch, ibs_User u
                    WHERE   (u.id = ch.userid(+))
                        AND ch.oid =  p_PersonContainer_01$retrieve.oid;

                EXCEPTION
                    WHEN OTHERS THEN
                        ibs_error.log_error ( ibs_error.error, 'p_Object$performRetrieve',
                                                'Error in get checkout values');
                    RAISE;
                END;
            END IF;

            -- rights set for viewing the User?
            l_rights := p_Rights$checkRights ( p_PersonContainer_01$retrieve.oid, l_containerId,
                                        p_PersonContainer_01$retrieve.userId, 2, l_rights);

             -- check if the user has the necessary rights
            IF (l_rights = 2)              -- the user has the rights?
            THEN
                 ao_checkOutUserName := l_tempName;
            END IF; -- if the user has the rights to see the user who checked out the object
            
            -- rights set for reading the User?
            l_rights := p_Rights$checkRights (l_tempOid, l_containerId,
                                        p_PersonContainer_01$retrieve.userId, 4, l_rights);

             -- check if the user has the necessary rights
            IF (l_rights = 4)              -- the user has the rights?
            THEN
                 ao_checkOutUserName := l_tempName;
                 ao_checkOutUserOid := l_tempOid;
            END IF; -- if the user has the rights to read the user who checked out the object

		
		END;
		ELSE
			p_PersonContainer_01$retrieve.retValue :=  p_PersonContainer_01$retrieve.INSUFFICIENT_RIGHTS;
		END IF; -- rights
	END;
	ELSE
	BEGIN
		p_PersonContainer_01$retrieve.retValue :=  p_PersonContainer_01$retrieve.OBJECTNOTFOUND;
	END;
	END IF;
	RETURN p_PersonContainer_01$retrieve.retValue;
	RETURN 0;

EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error,
'p_PersonContainer_01$retrieve',
    ', oid_s = ' || oid_s ||
    ', userId = ' || userId  ||
    ', op = ' || op ||
    ', errorcode = ' || StoO_error ||
    ', errormessage = ' || StoO_errmsg);
END p_PersonContainer_01$retrieve;
/

show errors;

EXIT;
