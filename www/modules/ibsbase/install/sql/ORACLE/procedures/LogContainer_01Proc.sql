/******************************************************************************
 * All procedures regarding a log container. <BR>
 *
 * @version     1.10.0001, 05.08.1999
 *
 * @author      Mario Stegbauer (MS)  980805
 *
 * <DT><B>Updates:</B>
 * <DD>HB 990805    Code cleaning.
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
CREATE OR REPLACE FUNCTION p_LogContainer_01$create(
userId     NUMBER ,
op     NUMBER ,
tVersionId     NUMBER ,
in_name     IN VARCHAR2 ,
containerId_s     VARCHAR2 ,
containerKind     NUMBER ,
isLink     NUMBER ,
linkedObjectId_s     VARCHAR2 ,
description     VARCHAR2 ,
oid_s     OUT VARCHAR2)
RETURN INTEGER
AS
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
name     VARCHAR2(63);
StoO_selcnt    INTEGER;
StoO_error     INTEGER;
StoO_rowcnt    INTEGER;
StoO_errmsg    VARCHAR2(255);
StoO_sqlstatus    INTEGER;
ALL_RIGHT     NUMBER(10,0);
INSUFFICIENT_RIGHTS     NUMBER(10,0);
OBJECTNOTFOUND     NUMBER(10,0);
retValue     NUMBER(10,0);
oid        RAW (8);

BEGIN
    p_LogContainer_01$create.name := p_LogContainer_01$create.in_name;
    p_LogContainer_01$create.ALL_RIGHT :=  1;
    p_LogContainer_01$create.INSUFFICIENT_RIGHTS :=  2;
    p_LogContainer_01$create.OBJECTNOTFOUND :=  3;

    p_LogContainer_01$create.retValue :=  p_LogContainer_01$create.ALL_RIGHT;

    p_LogContainer_01$create.name :=  'Protokoll';
    /*[SPCONV-ERR(33)]:BEGIN TRAN statement ignored*/
    NULL;
    BEGIN
    p_LogContainer_01$create.retValue:=p_Object$performCreate(p_LogContainer_01$create.userId,
     p_LogContainer_01$create.op,
     p_LogContainer_01$create.tVersionId,
     p_LogContainer_01$create.name,
     p_LogContainer_01$create.containerId_s,
     p_LogContainer_01$create.containerKind,
     p_LogContainer_01$create.isLink,
     p_LogContainer_01$create.linkedObjectId_s,
     p_LogContainer_01$create.description,
     p_LogContainer_01$create.oid_s,
         p_LogContainer_01$create.oid);
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error := SQLCODE;
            StoO_errmsg := SQLERRM;
    END;

    COMMIT WORK;
    RETURN p_LogContainer_01$create.retValue;
exception
  when OTHERS then
    ibs_error.log_error ( ibs_error.error, 'p_LogContainer_01$create',
                          'userId: ' || userId ||
                          ', op: ' || op ||
                          ', tVersionId: ' || tVersionId ||
                          ', in_name: ' || in_name ||
                          ', containerId_s: ' || containerId_s ||
                          ', containerKind: ' || containerKind ||
                          ', isLink: ' || isLink ||
                          ', linkedObjectId_s: ' || linkedObjectId_s ||
                          ', description: ' || description );
    return 0;
END p_LogContainer_01$create;
/

show errors;

/******************************************************************************
 * Clean the content of the ibs_protocol_01 table (incl. rights check). <BR>
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
CREATE OR REPLACE FUNCTION p_LogContainer_01$clean(
userId     INTEGER ,
op     NUMBER )
RETURN INTEGER
AS
StoO_selcnt    INTEGER;
StoO_error     INTEGER;
StoO_rowcnt    INTEGER;
StoO_errmsg    VARCHAR2(255);
StoO_sqlstatus    INTEGER;
name     VARCHAR2(63);
retValue     NUMBER(10,0);

BEGIN
    p_LogContainer_01$clean.retvalue :=  1;

    /*[SPCONV-ERR(16)]:BEGIN TRAN statement ignored*/

    BEGIN
    StoO_error   := 0;
    StoO_rowcnt  := 0;
    DELETE  ibs_protocol_01 ;
    StoO_rowcnt := SQL%ROWCOUNT;
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error  := SQLCODE;
            StoO_errmsg := SQLERRM;
    END;

    COMMIT WORK;
    RETURN p_LogContainer_01$clean.retValue;
END p_LogContainer_01$clean;
/

show errors;

exit;