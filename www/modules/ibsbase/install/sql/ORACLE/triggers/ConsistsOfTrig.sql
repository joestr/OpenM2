/******************************************************************************
 * The triggers for ibs_ConsistsOf. <BR>
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

-- create the trigger
CREATE OR REPLACE TRIGGER TrigConsistsOfInsert 
BEFORE INSERT ON ibs_ConsistsOf
FOR EACH ROW

DECLARE 
Trig_tVersionId INT;

  BEGIN
    -- set the version id of this type:
    Trig_tVersionId :=  16851729; --0x01012311;

    /*[SPCONV-ERR(7)]:BEGIN TRAN statement ignored*/
        -- get the actual id and oid:

    IF (:new.id <= 0)
    THEN
        SELECT consistsOfIdSeq.NEXTVAL
        INTO :new.id
        FROM sys.DUAL;
    END IF;

    IF (:new.oid <= 0)
    THEN
        :new.oid := createOid (Trig_tVersionId, :new.id);
    END IF;

END TrigConsistsOfInsert;
/

show errors;

exit;
