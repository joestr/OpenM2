/******************************************************************************
 * The triggers for ibs_System. <BR>
 * 
 *
 * @version     1.10.0001, 04.08.1999
 *
 * @author      Christine Keim (CK)  990804
 *
 * <DT><B>Updates:</B>
 * <DD>HB 990804    Code cleaning.
 ******************************************************************************
 */

/****** Object:  Table ibs_Table ******/
CREATE OR REPLACE TRIGGER TrigSystemInsert 
BEFORE INSERT ON ibs_System
FOR EACH ROW
  
  BEGIN 
    /*[SPCONV-ERR(7)]:BEGIN TRAN statement ignored*/

    IF (:new.id <= 0)
    THEN
    SELECT systemIdSeq.NEXTVAL
    INTO :new.id
    FROM sys.DUAL;
    END IF;

END TrigSystemInsert;
/

show errors;

exit;
