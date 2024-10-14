/******************************************************************************
 * All stored procedures regarding the ibs_ObjectRead table. <BR>
 * 
 * @version     1.10.0001, 05.08.1999
 *
 * @author      Mario Stegbauer (MS)  980805
 *
 * <DT><B>Updates:</B>
 * <DD>HB 990805    Code cleaning.
 * 
 ******************************************************************************
 */

/******************************************************************************
 * Sets a business object as already read by the user. <BR>
 *
 * @input parameters:
 * @param   @oid                ID of the object to be changed.
 * @param   @userId             ID of the user who has read the object.
 *
 * @output parameters:
 * @param   @state              The object's state.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */

CREATE OR REPLACE FUNCTION p_setRead2(
oid     RAW,
userId     INTEGER )
RETURN INTEGER
AS
StoO_selcnt    INTEGER;
StoO_error     INTEGER;
StoO_rowcnt    INTEGER;
StoO_errmsg    VARCHAR2(255);
StoO_sqlstatus    INTEGER;
ALL_RIGHT     NUMBER(10,0);
OBJECTNOTFOUND     NUMBER(10,0);
retValue     NUMBER(10,0);
ALL_RIGHT    NUMBER(10,0);
OBJECTNOTFOUND    NUMBER(10,0);
RETVALUE    NUMBER(10,0);
BEGIN
    p_setRead2.ALL_RIGHT :=  1;
    p_setRead2.OBJECTNOTFOUND :=  3;
    p_setRead2.retValue :=  p_setRead2.ALL_RIGHT;
    BEGIN
    p_setRead2.StoO_error   := 0;
    p_setRead2.StoO_rowcnt  := 0;
    UPDATE ibs_ObjectRead
    SET hasRead = 1,
    lastRead = SYSDATE
    
    WHERE oid = p_setRead2.oid 
     AND userId = p_setRead2.userId;
    p_setRead2.StoO_rowcnt := SQL%ROWCOUNT;
    EXCEPTION
        WHEN OTHERS THEN
            p_setRead2.StoO_error  := SQLCODE;
            p_setRead2.StoO_errmsg := SQLERRM;
    END;
    IF  ( p_setRead2.StoO_rowcnt <= 0) THEN
    BEGIN
        BEGIN
        p_setRead2.StoO_error   := 0;
        p_setRead2.StoO_rowcnt  := 0;
        INSERT INTO ibs_ObjectRead (oid, userId, hasRead, lastRead)
                VALUES (p_setRead2.oid, p_setRead2.userId, 1, SYSDATE);
        p_setRead2.StoO_rowcnt := SQL%ROWCOUNT;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN     -- no key found?
                NULL;                   -- don't do anything
            WHEN OTHERS THEN
                p_setRead2.StoO_error  := SQLCODE;
                p_setRead2.StoO_errmsg := SQLERRM;
        END;
    END;
    END IF;
    RETURN p_setRead2.retValue;
exception
  when OTHERS then
    ibs_error.log_error ( ibs_error.error, 'p_setRead2',
                          'oid: ' || oid || 'userId = ' || userId );
    return 0;
END p_setRead2;
/

show errors;





/******************************************************************************
 * Sets a business object as already read by the user. <BR>
 *
 * @input parameters:
 * @param   @oid                ID of the object to be changed.
 * @param   @userId             ID of the user who has read the object.
 *
 * @output parameters:
 * @param   @state              The object's state.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */

CREATE OR REPLACE FUNCTION p_setRead(
oid     RAW,
userId     INTEGER )
RETURN INTEGER
AS
StoO_selcnt    INTEGER;
StoO_error     INTEGER;
StoO_rowcnt    INTEGER;
StoO_errmsg    VARCHAR2(255);
StoO_sqlstatus    INTEGER;
ALL_RIGHT     NUMBER(10,0);
OBJECTNOTFOUND     NUMBER(10,0);
retValue     NUMBER(10,0);
refOid     RAW (8);
dummy        NUMBER(10,0);
ALL_RIGHT    NUMBER(10,0);
OBJECTNOTFOUND    NUMBER(10,0);
RETVALUE    NUMBER(10,0);
CURSOR refCursor IS SELECT  oid
     FROM ibs_Object 
    WHERE linkedObjectId = p_setRead.oid;
BEGIN
    p_setRead.ALL_RIGHT :=  1;
    p_setRead.OBJECTNOTFOUND :=  3;

    p_setRead.retValue :=  p_setRead.ALL_RIGHT;
    BEGIN
    p_setRead.retValue:=p_setRead2(p_setRead.oid,
     p_setRead.userId);
    EXCEPTION
        WHEN OTHERS THEN
            p_setRead.StoO_error := SQLCODE;
            p_setRead.StoO_errmsg := SQLERRM;
    END;

    OPEN refCursor;
    FETCH refCursor INTO p_setRead.refOid;
    IF refCursor%NOTFOUND THEN
        p_setRead.StoO_sqlstatus := 2;
    ELSE
        p_setRead.StoO_sqlstatus := 0;
    END IF;

    <<i_loop1>>
    WHILE  ( p_setRead.refCursor%FOUND) LOOP
    BEGIN
        BEGIN
        dummy := p_setRead2(p_setRead.refOid, p_setRead.userId);
        EXCEPTION
            WHEN OTHERS THEN
                p_setRead.StoO_error := SQLCODE;
                p_setRead.StoO_errmsg := SQLERRM;
        END;
        FETCH p_setRead.refCursor INTO p_setRead.refOid;

        IF refCursor%NOTFOUND THEN
            p_setRead.StoO_sqlstatus := 2;
        ELSE
            p_setRead.StoO_sqlstatus := 0;
        END IF;
    END;
    END LOOP;

    CLOSE p_setRead.refCursor;
    
    RETURN p_setRead.retValue;
exception
  when OTHERS then
    ibs_error.log_error ( ibs_error.error, 'p_setRead',
                          'oid: ' || oid || 'userId = ' || userId );
    return 0;
END p_setRead;
/

show errors;

exit;
