/******************************************************************************
 * The triggers for DomainScheme_01. <BR>
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

-- create the trigger
CREATE OR REPLACE TRIGGER TrigDomainScheme_01Insert 
BEFORE INSERT ON ibs_DomainScheme_01
FOR EACH ROW
DECLARE 
    -- constants:
    c_NOOID         CONSTANT RAW (8) := hexToRaw ('0000000000000000');

    -- local variables:
    l_tVersionId    INTEGER := 1;
BEGIN

    IF (:new.id <= 0)
    THEN
        SELECT  domainSchemeIdSeq.NEXTVAL
        INTO    :new.id
        FROM    DUAL;
    END IF;

    IF (:new.oid = c_NOOID)
    THEN
        :new.oid := createOid (l_tVersionId, :new.id);
    END IF;

EXCEPTION
    WHEN OTHERS THEN
        NULL;
--        err;
END TrigDomainScheme_01Insert;
/

show errors;

exit;
