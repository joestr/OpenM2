/******************************************************************************
 * The ibs user triggers. <BR>
 * The object table contains all currently existing system objects.
 * 
 *
 * @version     1.10.0001, 04.08.1999
 *
 * @author      Christine Keim (CK)  980803
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990804   Code cleaning.
 ******************************************************************************
 */

-- create the trigger:
CREATE OR REPLACE TRIGGER TrigUserInsert 
BEFORE INSERT ON ibs_User
FOR EACH ROW
DECLARE 
    -- constants:
    c_tVersionId    CONSTANT INTEGER := 16842913;
    c_NOOID         CONSTANT RAW (8) := hexToRaw ('0000000000000000');

BEGIN
    -- get the id and oid:
    IF (:new.id <= 0)
    THEN
        SELECT  userIdSeq.NEXTVAL
        INTO    :new.id
        FROM    sys.DUAL;

        :new.id := :new.domainId * 16777216 + :new.id;
    END IF;

    IF (:new.oid = c_NOOID)
    THEN
        :new.oid := createOid (c_tVersionId, :new.id);
    END IF;
END TrigUserInsert;
/

show errors;

exit;
