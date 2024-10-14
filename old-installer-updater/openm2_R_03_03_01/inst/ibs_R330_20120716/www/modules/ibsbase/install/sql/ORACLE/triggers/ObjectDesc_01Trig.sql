/******************************************************************************
 * The triggers for ibs_ObjectDesc_01. <BR>
 * 
 * @version     2.00.0001, 29.02.2000
 *
 * @author      Klaus Reimüller (KR)  000229
 ******************************************************************************
 */

/******************************************************************************
 * insert trigger
 */
CREATE OR REPLACE TRIGGER TrigObjectDescInsert 
    BEFORE INSERT
    ON ibs_ObjectDesc_01
    FOR EACH ROW
DECLARE
BEGIN
    IF (:new.id <= 0)                   -- id not set?
    THEN
        -- compute new id:
        SELECT  objectDescIdSeq.NEXTVAL
        INTO    :new.id
        FROM    DUAL;
    END IF;-- if id not set

EXCEPTION
    WHEN OTHERS THEN
        err;
END TrigObjectDescInsert;
/
show errors;

EXIT;
