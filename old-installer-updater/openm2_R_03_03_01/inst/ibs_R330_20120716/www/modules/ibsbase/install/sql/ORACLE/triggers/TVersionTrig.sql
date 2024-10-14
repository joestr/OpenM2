/******************************************************************************
 * The triggers for the ibs type version table. <BR>
 * 
 * @version     2.10.0001, 05.02.2001
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */

/******************************************************************************
 * INSERT trigger for ibs_TVersion.
 */
-- create the trigger:
CREATE OR REPLACE TRIGGER TrigTVersionInsert 
BEFORE INSERT ON ibs_TVersion
FOR EACH ROW
DECLARE
    -- constants:

    -- local variables:
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_thisSeq               INTEGER := 1;   -- sequence number of the tVersion
    l_id                    INTEGER := 0;   -- id of actual tVersion
    l_posNoPath             VARCHAR2 (254); -- posNoPath of actual tVersion
    l_posNo                 NUMBER (10, 0); -- position number
    l_posNoHex              VARCHAR2 (4);   -- hex representation of posNo

BEGIN
-- body:
    l_id := :new.id;

    -- check if an id was defined:
    IF (l_id IS NULL OR l_id <= 0)      -- no id defined?
    THEN
        -- create a new sequence number:
        SELECT  DECODE (MAX (tVersionSeq), NULL, 1, MAX (tVersionSeq) + 1)
        INTO    l_thisSeq
        FROM    ibs_TVersion
        WHERE   typeId = :new.typeId;

        -- compute the id as sum of typeId and sequnce number:
        l_id := B_OR (:new.typeId, l_thisSeq);
    ELSE                                    -- there was an id defined
        -- compute the sequence number out of the id:
        l_id := B_AND (:new.typeId, stringToInt ('0F'));
    END IF; -- else there was an id defined

    -- get position number:
    SELECT  DECODE (MAX (posNo), NULL, 1, MAX (posNo) + 1)
    INTO    l_posNo
    FROM    ibs_TVersion
    WHERE   superTVersionId = :new.superTVersionId
        AND id <> :new.id;

    -- convert the position number into hex representation:
    l_posNoHex := intToRaw (l_posNo, 4);

    -- get position path:
    IF (:new.superTVersionId <> 0)        -- tVersion is a subtversion?
    THEN
        -- compute the posNoPath as posNoPath of super tVersion concatenated by
        -- the posNo of this tVersion:
        SELECT  DISTINCT posNoPath || l_posNoHex
        INTO    l_posNoPath
        FROM    ibs_TVersion
        WHERE   id = :new.superTVersionId;
    ELSE                                -- type is not a subtype
                                        -- i.e. it is on top level
        -- compute the posNoPath as posNo of this object:
        l_posNoPath := l_posNoHex;
    END IF; -- else type is not subtype

    -- set the values:
    :new.id := l_id;
    :new.tVersionSeq := l_thisSeq;
    :new.posNo := l_posNo;
    :new.posNoPath := l_posNoPath;
    :new.idProperty := l_id * 256;
    :new.orderProperty := l_id * 256;
END TrigTVersionInsert;
/

show errors;

EXIT;
