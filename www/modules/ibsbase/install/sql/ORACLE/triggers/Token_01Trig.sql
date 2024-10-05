/******************************************************************************
 * The triggers for ibs_Token_01. <BR>
 * 
 *
 * @version     1.11.0001, 20.12.1999
 *
 * @author      Harald Buzzi    (HB)  991220
 *
 * <DT><B>Updates:</B>
 ******************************************************************************
 */


CREATE OR REPLACE TRIGGER TrigTokenInsert 
BEFORE INSERT
ON ibs_Token_01
FOR EACH ROW
DECLARE
BEGIN
    /*[SPCONV-ERR(7)]:BEGIN TRAN statement ignored*/
    IF (:new.id <= 0)                   -- id not set?
    THEN
        -- compute new id:
        SELECT  tokIdSeq.NEXTVAL
        INTO    :new.id
        FROM    DUAL;
    END IF;-- if id not set

EXCEPTION
    WHEN OTHERS THEN
        err;
END TrigTokenInsert;
/
show errors;

exit;
