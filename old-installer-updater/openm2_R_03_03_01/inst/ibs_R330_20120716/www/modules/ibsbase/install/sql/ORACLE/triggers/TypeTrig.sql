/******************************************************************************
 * The triggers for ibs_Type. <BR>
 *
 * @version     1.10.0001, 04.08.1999
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */

-- create the trigger
CREATE OR REPLACE TRIGGER TrigTypeInsert 
BEFORE INSERT ON ibs_Type
FOR EACH ROW

DECLARE 
    -- constants:
    c_tVersionId    CONSTANT INTEGER := 16851713; -- 0x1012301

    -- local variables:
    l_posNo         INTEGER;
    l_posNoCh       RAW (2);
    l_posNoPath     RAW (254);

BEGIN
    -- get next sequence number:
    IF (:new.id <= 0)
    THEN
        SELECT  DECODE (MIN (id), NULL, stringToInt ('0x01010010'),
                        MIN (id) + 16)
        INTO    :new.id
        FROM    ibs_Type
        WHERE   (id + 16) NOT IN 
                (
                    SELECT  id 
                    FROM    ibs_Type
                )
            AND id > 0;
    END IF;

    SELECT  DECODE (MAX (t.posNo), NULL, 1, MAX (t.posNo) + 1)
    INTO    l_posNo
    FROM    ibs_Type t
    WHERE   t.superTypeId = :new.superTypeId;

    l_posNoCh := intToRaw (l_posNo, 4);

    IF (:new.superTypeId <> 0)
    THEN
        -- if type is a subtype
        -- compute the posNoPath as posNoPath of super type concatenated by
        -- the posNo of this type:
        SELECT  DISTINCT t.posNoPath || l_posNoCh
        INTO    l_posNoPath
        FROM    ibs_Type t
        WHERE   t.id = :new.superTypeId;
    ELSE             
                            -- type is not a subtype
                                        -- i.e. it is on top level
        -- compute the posNoPath as posNo of this object:
        l_posNoPath := l_posNoCh;
    END IF;

    :new.posNo := l_posNo;
    :new.posNoPath := l_posNoPath;
    :new.oid := createOid (c_tVersionId, :new.id);
    :new.icon := :new.code || '.gif';
END TrigTypeInsert;
/

exit;
