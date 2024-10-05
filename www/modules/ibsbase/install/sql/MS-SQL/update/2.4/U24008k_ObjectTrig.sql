/******************************************************************************
 * The triggers for ibs_Object. <BR>
 *
 * @version     $Id: U24008k_ObjectTrig.sql,v 1.1 2005/03/04 22:06:37 klaus Exp $
 *
 * @author      Klaus Reimüller (KR)  980401
 ******************************************************************************
 */

/******************************************************************************
 * insert trigger
 */
-- delete existing trigger:
EXEC p_dropTrig 'TrigObjectInsert'
GO

--
-- do not create - removed due to performance-issues
--


/******************************************************************************
 * update trigger
 */
-- delete existing trigger:
EXEC p_dropTrig 'TrigObjectUpdate'
GO

-- create the trigger
CREATE TRIGGER TrigObjectUpdate ON ibs_Object
FOR UPDATE
AS 
DECLARE
    -- constants:
    @c_NOOID                OBJECTID,       -- oid of no object

    -- local variables:
    @l_id                   ID,             -- the id of the inserted object
    @l_oldContainerId       OBJECTID,       -- old containerId
    @l_oldPosNoPath         POSNOPATH_VC,   -- the old posNoPath
    @l_oldOLevel            INT,            -- the old oLevel
    @l_newContainerId       OBJECTID,       -- new containerId
    @l_newContainerOid2     OBJECTID,       -- new containerOid2
    @l_posNo                POSNO,          -- the new position number
    @l_posNoHex             VARCHAR (4),    -- hex style of position number
    @l_posNoPath            POSNOPATH_VC,   -- the new posNoPath
    @l_oLevel               INT,            -- the new oLevel
    @l_oid                  OBJECTID        -- the oid of the object

    -- assign constants:
SELECT
    @c_NOOID                = 0x0000000000000000

    -- initialize local variables:
SELECT
    @l_id = id
FROM    inserted

-- body:
    -- set actual date:
    -- check if one of the date relevant attributes was changed:
    IF EXISTS
        (
            SELECT  i.oid
            FROM    inserted i, deleted d
            WHERE   i.name <> d.name
                OR  i.linkedObjectId <> d.linkedObjectId
                OR  i.owner <> d.owner
                OR  i.changer <> d.changer
                OR  i.validUntil <> d.validUntil
                OR  i.description <> d.description
                OR  i.icon <> d.icon
        )
                                        -- the object was changed?
    BEGIN
        -- set the date when the object was last changed:
        UPDATE  ibs_Object
        SET     lastChanged = getDate ()
        WHERE   id = @l_id
    END -- if the object was changed


    -- check if the containerId was changed:
    IF UPDATE (containerId)             -- containerId possibly changed?
    BEGIN
        -- get the old and the new containerId and compare them:
        SELECT  @l_oldContainerId = containerId
        FROM    deleted
        SELECT  @l_newContainerId = containerId
        FROM    inserted

        IF (@l_oldContainerId <> @l_newContainerId) -- containerId changed?
        BEGIN
            -- get oid and old posNoPath:
            SELECT  @l_oid = oid, @l_oldPosNoPath = posNoPath,
                    @l_oldOLevel = oLevel
            FROM    ibs_Object
            WHERE   id = @l_id

            -- get new position number:
            -- The position number is one more than the actual highest position
            -- number of all other objects within the container or 1 if there
            -- is no object within the container yet.
            SELECT  @l_posNo = COALESCE (MAX (o.posNo) + 1, 1)
            FROM    ibs_Object o
            WHERE   o.containerId = @l_newContainerId
                AND o.id <> @l_id
            -- convert the position number into hex representation:
            EXEC p_IntToHexString @l_posNo, @l_posNoHex OUTPUT

            -- get new level:
            -- The level is one more than the level of the container.
            SELECT  @l_oLevel = COALESCE (MAX (oLevel) + 1, 1)
            FROM    ibs_Object
            WHERE   oid = @l_newContainerId

            -- get new position path:
            IF (@l_newContainerId <> @c_NOOID) -- object is within a container?
            BEGIN
                -- compute the posNoPath as posNoPath of container concatenated
                -- by the posNo of this object:
                SELECT  DISTINCT @l_posNoPath = posNoPath + @l_posNoHex,
                        @l_newContainerOid2 = containerId
                FROM    ibs_Object
                WHERE   oid = @l_newContainerId
            END -- if object is within a container
            ELSE                        -- object is not within a container
                                        -- i.e. it is on top level
            BEGIN
                -- compute the posNoPath as posNo of this object:
                SELECT  @l_posNoPath = @l_posNoHex
            END -- else object is not within a container

            -- update object:
            UPDATE  ibs_Object
            SET     containerOid2 = @l_newContainerOid2,
                    oLevel = @l_oLevel,
                    posNo = @l_posNo,
                    posNoPath = @l_posNoPath
            WHERE   oid = @l_oid

            -- compute and store levels and posNoPaths of underlying objects:
            -- The new posNoPath is the posNoPath of the actual object plus
            -- the rest of the old posNoPath from this object downwards.
            -- The new oLevel is the oLevel of the actual object plus the
            -- difference between the old oLevels of the actual object and
            -- each object.
            UPDATE  ibs_Object
            SET     posNoPath = @l_posNoPath +
                        SUBSTRING (posNoPath, @l_oldOLevel * 4 + 1, 254),
                    oLevel = oLevel + @l_oLevel - @l_oldOLevel
            WHERE   (posNoPath LIKE @l_oldPosNoPath + '%')
                AND oid <> @l_oid

            -- set new containerOid2 for all objects below the actual one:
            UPDATE  ibs_Object
            SET     containerOid2 = @l_newContainerId
            WHERE   containerId = @l_oid
        END -- if containerId changed
    END -- if containerId possibly changed

    -- set the common attributes of all links pointing to this object:
    -- check if one of the relevant attributes was changed:
    IF EXISTS
        (
            SELECT  i.oid
            FROM    inserted i, deleted d
            WHERE   i.isLink = 0
                AND (
                        i.name <> d.name
                    OR  i.description <> d.description
                    OR  i.icon <> d.icon
                    OR  i.rKey <> d.rKey
                    )
        )
                                        -- the object was changed?
    BEGIN
        -- update the linked objects:
        UPDATE  ibs_Object
        SET     name = i.name,
                description = i.description,
                icon = i.icon,
                rKey = i.rKey
        FROM    ibs_Object o, inserted i
        WHERE   o.isLink = 1
            AND i.isLink = 0
            AND o.linkedObjectId = i.oid
    END -- if the object was changed
GO
-- TrigObjectUpdate


/******************************************************************************
 * delete trigger
 */
-- delete existing trigger:
EXEC p_dropTrig 'TrigObjectDelete'
GO


PRINT 'Triggers for table ibs_Object created.'
GO
