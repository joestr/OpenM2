/******************************************************************************
 * All stored procedures regarding the Note_01 Object. <BR>
 *
 * @version     $Id: Note_01Proc.sql,v 1.7 2003/10/31 16:30:02 klaus Exp $
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
CREATE OR REPLACE FUNCTION p_Note_01$create(
userId     INTEGER ,
op     NUMBER ,
tVersionId     NUMBER ,
name     VARCHAR2 ,
containerId_s     VARCHAR2 ,
containerKind     NUMBER ,
isLink     NUMBER ,
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

NOT_OK         NUMBER(10,0);
ALL_RIGHT     NUMBER(10,0);
INSUFFICIENT_RIGHTS     NUMBER(10,0);
ALREADY_EXISTS     NUMBER(10,0);
retValue     NUMBER(10,0);
oid         RAW (8);
rights         NUMBER(10,0);
actRights     NUMBER(10,0);

BEGIN
    BEGIN
    p_stringToByte(p_Note_01$create.containerId_s,
     p_Note_01$create.containerId);
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error := SQLCODE;

            StoO_errmsg := SQLERRM;

    END;
    BEGIN
    p_stringToByte(p_Note_01$create.linkedObjectId_s,
     p_Note_01$create.linkedObjectId);
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error := SQLCODE;

            StoO_errmsg := SQLERRM;
                  ibs_error.log_error ( ibs_error.error, 'p_Note_01$create',
                    'Error in StringtoByte');
          RAISE;
    END;

    p_Note_01$create.NOT_OK :=  0;
    p_Note_01$create.ALL_RIGHT :=  1;
    p_Note_01$create.INSUFFICIENT_RIGHTS :=  2;
    p_Note_01$create.ALREADY_EXISTS :=  21;

    p_Note_01$create.retValue :=  p_Note_01$create.NOT_OK;

    p_Note_01$create.oid := p_Note_01$create.c_NOOID;

    /*[SPCONV-ERR(45)]:BEGIN TRAN statement ignored*/
    NULL;
    BEGIN
    p_Note_01$create.retValue:=p_Object$performCreate(p_Note_01$create.userId,
     p_Note_01$create.op,
     p_Note_01$create.tVersionId,
     p_Note_01$create.name,
     p_Note_01$create.containerId_s,
     p_Note_01$create.containerKind,
     p_Note_01$create.isLink,
     p_Note_01$create.linkedObjectId_s,
     p_Note_01$create.description,
     p_Note_01$create.oid_s,
     p_Note_01$create.oid);
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error := SQLCODE;

            StoO_errmsg := SQLERRM;

                  ibs_error.log_error ( ibs_error.error, 'p_Note_01$create',
                    'Error in p_object$perform create');
          RAISE;
    END;
    IF  ( p_Note_01$create.retValue = p_Note_01$create.ALL_RIGHT) THEN
    BEGIN

        BEGIN
        StoO_error   := 0;
        StoO_rowcnt  := 0;
        INSERT INTO ibs_Note_01 (oid, content)VALUES (p_Note_01$create.oid, p_Note_01$create.description);
        StoO_rowcnt := SQL%ROWCOUNT;
        EXCEPTION
            WHEN OTHERS THEN
                StoO_error  := SQLCODE;
                StoO_errmsg := SQLERRM;
                  ibs_error.log_error ( ibs_error.error, 'p_Note_01$create',
                    'Error in insert into');
          RAISE;
        END;
    END;
    END IF;

    COMMIT WORK;
    RETURN p_Note_01$create.retValue;
    RETURN 0;

EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_Note_01$create',
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
END p_Note_01$create;
/

show errors;

/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @id                 ID of the object to be changed.
 * @param   @userId                ID of the user who is changing the object.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 * @param   @name               Name of the object.
 * @param   @validUntil         Date until which the object is valid.
 * @param   @description        Description of the object.
 * @param   @showInNews         flag if object should be shown in newscontainer
 *
 * @param    @content            content of note => Text or HTML Code
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Note_01$change(
oid_s     VARCHAR2 ,
userId     INTEGER ,
op     NUMBER ,
name     VARCHAR2 ,
validUntil     DATE ,
description     VARCHAR2,
showInNews INTEGER )
RETURN INTEGER
AS
StoO_selcnt    INTEGER;
StoO_error     INTEGER;
StoO_rowcnt    INTEGER;
StoO_errmsg    VARCHAR2(255);
StoO_sqlstatus    INTEGER;
NOT_OK     NUMBER(10,0);
ALL_RIGHT     NUMBER(10,0);
INSUFFICIENT_RIGHTS     NUMBER(10,0);
OBJECTNOTFOUND     NUMBER(10,0);
retValue     NUMBER(10,0);
oid         RAW (8);

BEGIN
    p_Note_01$change.NOT_OK :=  0;
    p_Note_01$change.ALL_RIGHT :=  1;
    p_Note_01$change.INSUFFICIENT_RIGHTS :=  2;
    p_Note_01$change.OBJECTNOTFOUND :=  3;

    p_Note_01$change.retValue :=  p_Note_01$change.NOT_OK;

    /*[SPCONV-ERR(31)]:BEGIN TRAN statement ignored*/
    NULL;
    BEGIN
    p_Note_01$change.retValue:=p_Object$performChange(p_Note_01$change.oid_s,
     p_Note_01$change.userId,
     p_Note_01$change.op,
     p_Note_01$change.name,
     p_Note_01$change.validUntil,
     p_Note_01$change.description,
     p_Note_01$change.showInNews,
     p_Note_01$change.oid);
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error := SQLCODE;

            StoO_errmsg := SQLERRM;

    END;

    COMMIT WORK;
    RETURN p_Note_01$change.retValue;
    RETURN 0;

EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_Note_01$change',
    ', oid_s = ' || oid_s ||
    ', userId = ' || userId  ||
    ', op = ' || op ||
    ', name = ' || name ||
    ', validUntil = ' || validUntil  ||
    ', description = ' || description ||
    ', errorcode = ' || StoO_error ||
    ', errormessage = ' || StoO_errmsg);
END p_Note_01$change;
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
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Note_01$retrieve(
oid_s     VARCHAR2 ,
userId     INTEGER ,
op     NUMBER ,
state         OUT NUMBER,
tVersionId     OUT NUMBER,
typeName     OUT VARCHAR2,
name         OUT VARCHAR2,
containerId     OUT RAW,
containerName     OUT VARCHAR2,
containerKind     OUT NUMBER,
isLink         OUT NUMBER,
linkedObjectId     OUT RAW,
owner         OUT NUMBER,
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
    ao_checkOutUserName    OUT VARCHAR2
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
INSUFFICIENT_RIGHTS     NUMBER(10,0);
OBJECTNOTFOUND     NUMBER(10,0);
retValue     NUMBER(10,0);
oid         RAW (8);

BEGIN
    p_Note_01$retrieve.NOT_OK :=  0;
    p_Note_01$retrieve.ALL_RIGHT :=  1;
    p_Note_01$retrieve.INSUFFICIENT_RIGHTS :=  2;
    p_Note_01$retrieve.OBJECTNOTFOUND :=  3;

    p_Note_01$retrieve.retValue :=  p_Note_01$retrieve.NOT_OK;

    /*[SPCONV-ERR(49)]:BEGIN TRAN statement ignored*/
    NULL;
    BEGIN
    p_Note_01$retrieve.retValue:=p_Object$performRetrieve(p_Note_01$retrieve.oid_s,
     p_Note_01$retrieve.userId,
     p_Note_01$retrieve.op,
     p_Note_01$retrieve.state,
     p_Note_01$retrieve.tVersionId,
     p_Note_01$retrieve.typeName,
     p_Note_01$retrieve.name,
     p_Note_01$retrieve.containerId,
     p_Note_01$retrieve.containerName,
     p_Note_01$retrieve.containerKind,
     p_Note_01$retrieve.isLink,
     p_Note_01$retrieve.linkedObjectId,
     p_Note_01$retrieve.owner,
     p_Note_01$retrieve.ownerName,
     p_Note_01$retrieve.creationDate,
     p_Note_01$retrieve.creator,
     p_Note_01$retrieve.creatorName,
     p_Note_01$retrieve.lastChanged,
     p_Note_01$retrieve.changer,
     p_Note_01$retrieve.changerName,
     p_Note_01$retrieve.validUntil,
     p_Note_01$retrieve.description,
     p_Note_01$retrieve.showInNews,
    ao_checkedOut, ao_checkOutDate, 
    ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName,
     p_Note_01$retrieve.oid);
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error := SQLCODE;

            StoO_errmsg := SQLERRM;

    END;

    COMMIT WORK;
    RETURN p_Note_01$retrieve.retValue;
    RETURN 0;

EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_Note_01$retrieve',
    ', oid_s = ' || oid_s ||
    ', userId = ' || userId  ||
    ', op = ' || op ||
    ', errorcode = ' || StoO_error ||
    ', errormessage = ' || StoO_errmsg);
END p_Note_01$retrieve;
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
CREATE OR REPLACE FUNCTION p_Note_01$delete
(
    ai_oid_s                VARCHAR2,
    ai_userId               INTEGER,
    ai_op                   INTEGER
)
RETURN INTEGER
AS
    l_oid                   RAW (8);
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3;
    l_retValue              INTEGER := c_NOT_OK;

BEGIN
    p_stringToByte(ai_oid_s, l_oid);    
    l_retValue:=p_Object$performDelete(ai_oid_s,
     ai_userId, ai_op, l_oid);


    IF  ( l_retValue = c_ALL_RIGHT)
    THEN
    BEGIN
        DELETE  ibs_Note_01 
        WHERE oid  NOT IN (
                           SELECT  oid
                           FROM ibs_Object
                           );
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
             l_retValue :=  c_NOT_OK;
        WHEN OTHERS THEN
            ibs_error.log_error ( ibs_error.error, 'p_Note_01$delete',
                'Error in DELETE');
        RAISE;
    END;
    END IF;

    COMMIT WORK;
    RETURN l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Note_01$delete',
    ', oid_s = ' || ai_oid_s ||
    ', userId = ' || ai_userId  ||
    ', op = ' || ai_op ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);
END p_Note_01$delete;
/

show errors;

/******************************************************************************
 * Copy an object and all its values. <BR>
 * 
 * @input parameters:
 * @param   @oid                ID of the object to be copy.
 * @param   @userId                ID of the user who is copying the object.
 * @param   @newOid             ID of the copy of the object.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */
CREATE OR REPLACE FUNCTION p_Note_01$BOCopy
(
  oid             RAW ,
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
    p_Note_01$BOCopy.NOT_OK :=  0;
    p_Note_01$BOCopy.ALL_RIGHT :=  1;

    p_Note_01$BOCopy.retValue :=  p_Note_01$BOCopy.NOT_OK;
    BEGIN
    StoO_error   := 0;
    StoO_rowcnt  := 0;
    INSERT INTO ibs_Note_01 (oid, content)SELECT  p_Note_01$BOCopy.newOid, content
         FROM ibs_Note_01 
        WHERE oid = p_Note_01$BOCopy.oid;
    StoO_rowcnt := SQL%ROWCOUNT;
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error  := SQLCODE;
            StoO_errmsg := SQLERRM;
    END;

    IF  ( StoO_rowcnt >= 1) THEN
        p_Note_01$BOCopy.retValue :=  p_Note_01$BOCopy.ALL_RIGHT;
    END IF;
    RETURN p_Note_01$BOCopy.retValue;
    RETURN 0;

EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error ( ibs_error.error, 'p_Note_01$BOCopy',
    ', userId = ' || userId  ||
    ', newOid = ' || newOid ||
    ', errorcode = ' || StoO_error ||
    ', errormessage = ' || StoO_errmsg);
END p_Note_01$BOCopy;
/

show errors;

create or replace function p_Note_01$getExtended 
( 
    ai_oid       VARCHAR2, 
    ao_content   OUT CLOB
)
return integer
as
    l_oid       RAW(8);
begin
  p_stringToByte (ai_oid, l_oid);
  
  select content 
  into  ao_content
  from  ibs_Note_01 
  where oid = l_oid;
commit work;
  return 1;
exception
  when OTHERS then
    ibs_error.log_error(ibs_error.error, 'p_Note_01$getExtended',
                        'Input: ' || ai_oid || 
                        ', errorcode = ' || SQLCODE ||
                        ', errmsg = ' || SQLERRM );
    p_Note_01$getExtended.ao_content := EMPTY_CLOB();
    return 0;
end p_Note_01$getExtended;
/

show errors;

EXIT;