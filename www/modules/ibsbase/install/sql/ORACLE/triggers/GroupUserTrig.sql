/******************************************************************************
 * The ibs groupuser triggers. <BR>
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
CREATE OR REPLACE TRIGGER TrigGroupUserInsert 
BEFORE INSERT ON ibs_GroupUser
FOR EACH ROW 
BEGIN
    IF (:new.id <= 0)
    THEN
        SELECT  groupUserIdSeq.NEXTVAL
        INTO    :new.id
        FROM    sys.DUAL;
    END IF;
END TrigGroupUserInsert;
/

show errors;

exit;
