/******************************************************************************
 * All stored procedures regarding a container. <BR>
 * 
 *
 * @version     1.10.0001, 02.08.1999
 *
 * @author      Klaus Reimüller (KR)  980507
 *
 * <DT><B>Updates:</B>
 * <DD>HB 990802    code cleaning
 ******************************************************************************
 */


/******************************************************************************
 * Gets the data of the objects within a given container (incl. rights check). 
 * <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be changed.
 * @param   @userId             ID of the user who is creating the object.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 * @param   @orderBy            Attribute to order by.
 * @param   @orderHow           Type of ordering (ASCending or DESCending).
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */


/******************************************************************************
 * Deletes an object and all its values (incl. rights check). <BR>
 * This procedure also delets all links showing to this object.
 * 
 * @input parameters:
 * @param   @oid_s              ID of the object to be deleted.
 * @param   @userId             ID of the user who is deleting the object.
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
CREATE OR REPLACE FUNCTION p_Container$delete(
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
retValue     NUMBER(10,0);

BEGIN
    BEGIN
    p_stringToByte(p_Container$delete.oid_s,
     p_Container$delete.oid);
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error := SQLCODE;

            StoO_errmsg := SQLERRM;
    END;

    p_Container$delete.ALL_RIGHT :=  1;
    p_Container$delete.INSUFFICIENT_RIGHTS :=  2;
    p_Container$delete.OBJECTNOTFOUND :=  3;

    p_Container$delete.retValue :=  p_Container$delete.ALL_RIGHT;

    /*[SPCONV-ERR(24)]:BEGIN TRAN statement ignored*/
    NULL;
    BEGIN
    p_Container$delete.retValue:=p_Object$performDelete(
         p_Container$delete.oid_s,
     p_Container$delete.userId,
     p_Container$delete.op,
         p_Container$delete.oid);
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error := SQLCODE;

            StoO_errmsg := SQLERRM;

    END;

    COMMIT WORK;
    RETURN p_Container$delete.retValue;
exception
  when OTHERS then
    ibs_error.log_error ( ibs_error.error, 'p_Container$delete',
                          'oid_s: ' || oid_s ||
                          ', userId: ' || userId ||
                          ', op: ' || op ||
                          ', sqlcode: ' || SQLCODE ||
                          ', sqlerrm: ' || SQLERRM );
    return 0;
END p_Container$delete;
/

show errors;

exit;