/******************************************************************************
 * The ibs protocol triggers. <BR>
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
CREATE OR REPLACE TRIGGER TrigProtocolInsert 
BEFORE INSERT ON ibs_Protocol_01
FOR EACH ROW 
BEGIN

    IF (:new.id <= 0)
    THEN
    SELECT protocolIdSeq.NEXTVAL
    INTO :new.id
    FROM sys.DUAL;
    END IF;

END TrigProtocolInsert;
/

show errors;

exit;
