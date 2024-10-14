/******************************************************************************
 * The triggers for ibs_Object. <BR>
 *
 * @version     2.10.0007, 07.11.2000
 *
 * @author      Christine Keim (CK)  990804
 ******************************************************************************
 */

/******************************************************************************
 * insert trigger
 */
--
-- do not create - removed due to performance-issues
--




/******************************************************************************
 * update trigger
 */
-- create the trigger
CREATE OR REPLACE TRIGGER TrigObjectUpdate 
BEFORE UPDATE
ON ibs_Object
FOR EACH ROW
DECLARE
    -- constants:
    c_NOOID             CONSTANT RAW (8) := hexToRaw ('0000000000000000');
                                        -- oid of no object

    -- local variables:

-- body:
BEGIN
    -- set actual date:
    -- check if one of the date relevant attributes was changed:
    IF (:new.name <> :old.name
        OR :new.linkedObjectId <> :old.linkedObjectId
        OR :new.owner <> :old.owner
        OR :new.changer <> :old.changer
        OR :new.validUntil <> :old.validUntil
        OR :new.description <> :old.description
        OR :new.icon <> :old.icon)
                                        -- the object was changed?
    THEN
        SELECT  sysDate
        INTO    :new.lastChanged
        FROM    DUAL;
    END IF; -- the object was changed


/*
    -- check if the containerId was changed:
    IF (:old.containerId <> :new.containerId) -- containerId changed?
    THEN
--debug ('TrigUpdate ... 1');
        -- get new position number:
        -- The position number is one more than the actual highest position
        -- number of all other objects within the container or 1 if there
        -- is no object within the container yet.
        SELECT  DECODE (MAX (posNo), NULL, 1, MAX (posNo) + 1)
        INTO    :new.posNo
        FROM    ibs_Object
        WHERE   containerId = :new.containerId;
        -- convert the position number into hex representation:
--        p_IntToHexString (:new.posNo, l_posNoHex);

--debug ('TrigUpdate ... 2');

        -- get new level:
        -- The level is one more than the level of the container.
        SELECT  DECODE (oLevel, NULL, 1, oLevel + 1)
        INTO    :new.oLevel
        FROM    ibs_Object
        WHERE   oid = :new.containerId;

--debug ('TrigUpdate ... 3');

        -- get new position path:
        IF (:new.containerId <> c_NOOID) -- object is within a container?  
        THEN
--debug ('TrigUpdate ... 4');
            -- compute the posNoPath as posNoPath of container concatenated by
            -- the posNo of this object:
            SELECT DISTINCT posNoPath || intToRaw (:new.posNo, 4)
            INTO    :new.posNoPath
            FROM    ibs_Object  
            WHERE   oid = :new.containerId;
        -- if object is within a container
        ELSE                            -- object is not within a container
                                        -- i.e. it is on top level
            -- compute the posNoPath as posNo of this object:
--debug ('TrigUpdate ... 5');
            :new.posNoPath := intToRaw (:new.posNo, 4);
        END IF; -- else object is not within a container

        -- compute and store levels and posNoPaths of underlying objects:
        -- The new posNoPath is the posNoPath of the actual object plus
        -- the rest of the old posNoPath from this object downwards.
        -- The new oLevel is the oLevel of the actual object plus the
        -- difference between the old oLevels of the actual object and
        -- each object.
        UPDATE  ibs_Object
        SET     posNoPath = :new.posNoPath ||
                    SUBSTR (posNoPath, :old.oLevel * 4),
                oLevel = oLevel + :new.oLevel - :old.oLevel
        WHERE   posNoPath LIKE :old.posNoPath || '%'
            AND oid <> :new.oid;
    END IF; -- containerId changed


    -- set the common attributes of all links pointing to this object:
    UPDATE  ibs_Object
    SET     name = :new.name,
            description = :new.description,
            icon = :new.icon
    WHERE   isLink = 1
        AND linkedObjectId = :new.oid;
*/
END TrigObjectUpdate;
/

show errors;

exit;
