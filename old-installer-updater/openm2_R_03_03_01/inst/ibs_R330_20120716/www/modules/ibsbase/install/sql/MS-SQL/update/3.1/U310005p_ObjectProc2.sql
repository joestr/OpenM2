/******************************************************************************
 * Update stored procedure to copy object according to issue 'IBS-734 Exception 
 * when copy the same object twice'. <BR>
 *
 * @version     $Id: U310005p_ObjectProc2.sql,v 1.1 2012/02/14 09:23:17 rburgermann Exp $
 *
 * @author      Roland Burgermann (RB)  20120214
 ******************************************************************************
 */

-- remove eventual existing entries within the table 'ibs_copy'
DELETE
FROM    ibs_Copy
GO

/******************************************************************************
 * Copies a selected BusinessObject and its childs.(incl. rights check). <BR>
 * The rightcheck is done before we start to copy. When one BO of the tree is
 * not able to be copied because of a negativ rightcheckresult, the action is
 * stopped.
 *
 * @input parameters:
 * @param   ai_oid_s            ID of the rootobject to be copied.
 * @param   ai_userId           ID of the user who is copying the object.
 * @param   ai_op               Operation to be performed (used for rights
 *                              check).
 * @param   ai_targetId_s       Oid of the target BusinessObjectContainer where
 *                              the root object is copied to.
 * @param   ai_isRecursive      Perform recursive operation, i.e. copy the
 *                              actual object and all objects below.
 *                              Dealt: 1 (true)
 *
 * @output parameters:
 * @param   ao_newOid_s         The oid of the newly created object.
 * @return  A value representing the state of the procedure.
 *  c_ALL_RIGHT             Action performed, values returned, everything ok.
 *  c_NOT_OK                An error occurred.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Object$copy'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$copy
(
    -- input parameters:
    @ai_oid_s               OBJECTIDSTRING,
    @ai_userId              USERID,
    @ai_op                  INT,
    @ai_targetId_s          OBJECTIDSTRING,
    @ai_isRecursive         BOOL = 1,
    -- ouput parameters:
    @ao_newOid_s            OBJECTIDSTRING OUTPUT
)
AS
DECLARE
    -- constants:
    @c_NOOID                OBJECTID,       -- oid of no valid object
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_NOT_OK               INT,            -- error occured
    @c_INSUFFICIENT_RIGHTS  INT,            -- the user has no rights to copy
    @c_RIGHT_UPDATE         INT,
    @c_RIGHT_INSERT         INT,
    @c_CHECKEDOUT           INT,
    @c_EMPTYPOSNOPATH       VARCHAR(4),     -- empty posNoPath
    @c_ST_ACTIVE            INT,            -- state value of active object
    @c_MIN_COPYID           INT,            -- minimum copy id number

    -- local variables:
    -- ... for various purposes
    @l_retValue             INT,
    @l_rights               RIGHTS,
    @l_structuralError      INT,            -- indicates error in copied structure
    @l_copyId               INT,            -- id for entries in copy-table
    -- ... of target-container
    @l_targetOid            OBJECTID,
    @l_targetPosNoPath      POSNOPATH_VC,
    @l_targetOLevel         INT,
    -- ... of object to copy (object)
    @l_rootOid              OBJECTID,
    @l_rootPosNoPath        POSNOPATH_VC,
    @l_rootOLevel           INT,
    -- ... of new object that must be copied sub-sequently (in loop)
    @l_newId                INT,
    @l_newContainerId       OBJECTID,
    @l_newContainerOid2     OBJECTID,
    @l_newOid               OBJECTID,
    @l_newPosNoPath         POSNOPATH_VC,
    @l_newPosNo             POSNO,
    @l_newPosNoHex          VARCHAR (4),    -- hex representation of posNo
    @l_newOLevel            INT,
    @l_newTVersionId        TVERSIONID,
    @l_newTargettVersion    TVERSIONID,
    @l_newRights            INT,
    @l_newName              NAME,
    @l_newDescription       DESCRIPTION,
    @l_newIcon              NAME,
    @l_newRKey              INT,
    @l_newState             INT,
    @l_newTypeCode          NAME,
    @l_newTypeName          NAME,
    @l_newIsContainer       BOOL,
    @l_newContainerKind     INT,
    @l_newIsLink            BOOL,
    @l_newLinkedObjectId    OBJECTID,
    @l_newShowInMenu        BOOL,
    @l_newFlags             INT,
    @l_newProcessState      INT,
    @l_newCreationDate      DATETIME,
    @l_newCreator           INT,
    @l_newValidUntil        DATETIME,
    @l_newConsistsOfId      INT,
    -- ... of old object that must be copied sub-sequently (in loop)
    @l_oldOid               OBJECTID,
    @l_oldContainerId       OBJECTID

    -- assign constants:
SELECT
    @c_NOOID                    = 0x0000000000000000,
    @c_ALL_RIGHT                = 1,
    @c_NOT_OK                   = 0,
    @c_INSUFFICIENT_RIGHTS      = 2,
    @c_RIGHT_UPDATE             = 8,
    @c_RIGHT_INSERT             = 1,
    @c_CHECKEDOUT               = 16,
    @c_EMPTYPOSNOPATH           = '0000',
    @c_ST_ACTIVE                = 2,
    @c_MIN_COPYID               = 1000000000

    -- initialize local variables:
SELECT
    @l_retValue             = @c_ALL_RIGHT,
    @l_rights               = 0,
    @l_structuralError      = 0,
    @l_copyId               = 0,
    @l_targetOid            = @c_NOOID,
    @l_targetPosNoPath      = @c_EMPTYPOSNOPATH,
    @l_targetOLevel         = 0,
    @l_rootOid              = @c_NOOID,
    @l_rootPosNoPath        = @c_EMPTYPOSNOPATH,
    @l_rootOLevel           = 0,
    @l_newId                = 0,
    @l_newContainerId       = @c_NOOID,
    @l_newContainerOid2     = @c_NOOID,
    @l_newOid               = @c_NOOID,
    @l_newPosNoPath         = @c_EMPTYPOSNOPATH,
    @l_newPosNo             = 0,
    @l_newPosNoHex          = '0000',
    @l_newOLevel            = 0,
    @l_newTVersionId        = 0,
    @l_newTargettVersion    = 0,
    @l_newRights            = 0,
    @l_newName              = '',
    @l_newDescription       = '',
    @l_newIcon              = '',
    @l_newRKey              = 0,
    @l_newState             = 0,
    @l_newTypeCode          = '',
    @l_newTypeName          = '',
    @l_newIsContainer       = 0,
    @l_newContainerKind     = 0,
    @l_newIsLink            = 0,
    @l_newLinkedObjectId    = @c_NOOID,
    @l_newShowInMenu        = 0,
    @l_newFlags             = 0,
    @l_newProcessState      = 0,
    @l_newCreationDate      = getDate(),
    @l_newCreator           = 0,
    @l_newValidUntil        = getDate(),
    @l_oldOid               = @c_NOOID,
    @l_oldContainerId       = @c_NOOID

-- body:
    -- conversions: (OBJECTIDSTRING) - all input objectids must be converted
    EXEC p_stringToByte @ai_oid_s, @l_rootOid OUTPUT
    EXEC p_stringToByte @ai_targetId_s, @l_targetOid OUTPUT

    -- read structural data of object to be copied
    SELECT  @l_rootPosNoPath = posNoPath, @l_rootOLevel = oLevel
    FROM    ibs_Object
    WHERE   oid = @l_rootOid
    --
    -- any entries found?
    IF (@@ROWCOUNT <= 0)
        RETURN @c_NOT_OK        -- EXCEPTION!!!!!

    IF (@ai_isRecursive = 1)            -- recursive operation?
    BEGIN
        -- get (maximum) rights for this user on object and all its
        -- sub-objects for given operation:
        SELECT  @l_rights = MIN (rights & @ai_op)
        FROM
        (
            SELECT  MAX (rights & @ai_op) AS rights, oid
            FROM    v_Container$rights
            WHERE   posNoPath LIKE @l_rootPosNoPath + '%'
                AND userId = @ai_userId
                AND state = @c_ST_ACTIVE
            GROUP BY oid
        ) s

        -- any entries found?
        IF (@@ROWCOUNT <= 0)
            RETURN @c_NOT_OK        -- EXCEPTION!!!!!
    END -- if recursive operation
    ELSE                                -- not recursive
    BEGIN
        -- get rights for this user on the object for the given operation:
        SELECT  @l_rights = MAX (rights & @ai_op)
        FROM    v_Container$rights
        WHERE   oid = @l_rootOid
            AND userId = @ai_userId
            AND state = @c_ST_ACTIVE

        -- any entries found?
        IF (@@ROWCOUNT <= 0)
            RETURN @c_NOT_OK        -- EXCEPTION!!!!!
    END -- else not recursive


    -- check if the user has the necessary rights:
    IF (@l_rights = @ai_op)                  -- the user has the rights?
    BEGIN
        -- read out information of the target container-object
        SELECT  @l_targetPosNoPath = posNoPath, @l_newRKey = rKey,
                @l_targetOLevel= oLevel
        FROM    ibs_Object
        WHERE   oid = @l_targetOid

        -- any entries found?
        IF (@@ROWCOUNT <= 0)
            RETURN @c_NOT_OK        -- EXCEPTION!!!!!

        BEGIN TRANSACTION

            -- specify the highest copyId which is set at the beginning of
            -- every copyprocess and is deleted at the end of the process
            SELECT  @l_copyId = COALESCE (MAX (copyId) + 1, @c_MIN_COPYID)
            FROM    ibs_Copy

            -- check if copy id is high enough:
            IF (@l_copyId < @C_MIN_COPYID)
            BEGIN
                -- set copy id to minimum value:
                SELECT  @l_copyId = @C_MIN_COPYID
            END -- if

            IF (@ai_isRecursive = 1)    -- recursive operation?
            BEGIN
                -- now copy object AND its subobjects to target-container
                -- (write entry for each object to ibs_Copy table,
                --  will be needed to bend existing object-references)
                --
                -- define cursor:
                DECLARE containerCursor INSENSITIVE CURSOR FOR
                    SELECT  oid, containerId
                    FROM    ibs_Object
                    WHERE   posNoPath LIKE @l_rootPosNoPath + '%'
                        AND state = @c_ST_ACTIVE
                    ORDER BY posNoPath ASC
                FOR READ ONLY
            END -- if recursive operation
            ELSE                        -- not recursive
            BEGIN
                -- now copy object to target-container
                -- (write entry for each object to ibs_Copy table,
                --  will be needed to bend existing object-references)
                --
                -- define cursor:
                DECLARE containerCursor INSENSITIVE CURSOR FOR
                    SELECT  oid, containerId
                    FROM    ibs_Object
                    WHERE   oid = @l_rootOid
                        AND state = @c_ST_ACTIVE
                    ORDER BY posNoPath ASC
                FOR READ ONLY
            END -- else not recursive

            -- insert the target and the container of the copied object
            OPEN containerCursor
            --
            -- get first object to be copied:
            FETCH NEXT FROM containerCursor INTO @l_oldOid, @l_oldContainerId
            --
            WHILE (@@FETCH_STATUS = 0)
            BEGIN
                --
                -- read and create entries for new object
                --

                -- read information of object that shall be copied
                SELECT  @l_newState = state,
                        @l_newTVersionId = tVersionId,
                        @l_newTypeCode = typeCode,
                        @l_newTypeName = typeName,
                        @l_newIsContainer = isContainer,
                        @l_newName = name,
                        @l_newContainerId = containerId,
                        @l_newContainerKind = containerKind,
                        @l_newContainerOid2 = containerOid2,
                        @l_newIsLink = isLink,
                        @l_newLinkedObjectId = linkedObjectId,
                        @l_newShowInMenu = showInMenu,
                        @l_newFlags = flags & (0xFFFFFFFF ^ @c_CHECKEDOUT),
                        /*owner, oLevel,*/
                        @l_newPosNo = posNo,
                        /*posNoPath,*/
                        @l_newCreationDate = creationDate,
                        @l_newCreator = creator,
                        /*lastChanged, changer,*/
                        @l_newValidUntil =  validUntil,
                        @l_newDescription = description,
                        @l_newIcon = icon,
                        @l_newProcessState = processState,
                        @l_newConsistsOfId = consistsOfId
                FROM    ibs_Object
                WHERE   oid = @l_oldOid

                --
                -- get the new id and compute oid; convert
                --
                EXEC @l_newId = p_ObjectId$getNext
/* KR 20050303 performance tuning:
 * use new table ibs_ObjectId to get the unique id
                SELECT  @l_newId = COALESCE (MAX (id) + 1, 1)
                FROM    ibs_Object
*/

                EXEC p_createOid @l_newTVersionId, @l_newId, @l_newOid OUTPUT

                -- read and calculate the following for the new object:
                -- * containerId
                -- * posNo
                -- * posNoPath
                -- * oLevel
                IF (@l_oldOid = @l_rootOid) -- top-object (copy-object itself)
                BEGIN
                    -- target is given target-container;
                    SELECT @l_newContainerId = @l_targetOid

                    -- get container of container:
                    SELECT  @l_newContainerOid2 = containerId
                    FROM    ibs_Object
                    WHERE   oid = @l_targetOid

                    -- derive position number from other objects within container:
                    -- The new posNo is:
                    -- * the actual highest posNo within container + 1
                    -- * 1 if no objects in container
                    SELECT  @l_newPosNo = COALESCE (MAX (posNo) + 1, 1)
                    FROM    ibs_Object
                    WHERE   containerId = @l_targetOid

                    -- convert the position number into hex representation:
                    EXEC p_IntToHexString @l_newPosNo, @l_newPosNoHex OUTPUT

                    -- calculate new posNoPath:
                    SELECT @l_newPosNoPath = @l_targetPosNoPath + @l_newPosNoHex

                    -- set new oLevel:
                    SELECT @l_newOLevel = @l_targetOLevel + 1

                    -- SET output value:
                    -- convert oid to string:
                    EXEC p_byteToString @l_newOid, @ao_newOid_s OUTPUT
                END

                ELSE                    -- any sub object
                BEGIN
                    -- reset structural-error information:
                    SELECT @l_structuralError = 0

                    -- convert the position number into hex representation:
                    EXEC p_IntToHexString @l_newPosNo, @l_newPosNoHex OUTPUT

                    -- get/calc data out of container object in new structure!
                    -- remark: ibs_Copy can be referenced, because this
                    --         branch  can never be accessed in first iteration
                    SELECT  @l_newContainerId = c.newOid,
                            @l_newContainerOid2 = o.containerId,
                            @l_newPosNoPath = o.posNoPath + @l_newPosNoHex,
                            @l_newOLevel = o.oLevel + 1
                    FROM    ibs_Copy c, ibs_Object o
                    WHERE   @l_oldContainerId = c.oldOid
                        AND c.newOid = o.oid
                        AND c.copyId = @l_copyId

                    -- any entries found?
                    IF (@@ROWCOUNT <= 0)
                        SELECT @l_structuralError = 1  -- DO NOT RAISE EXCEPTION
                                                       -- ERROR WILL BE HANDLED BELOW
                END


                --
                -- create object entries in ibs_Object and type-specific
                --
                IF (@l_structuralError = 0) -- no structural error?
                BEGIN
                    -- insert entry in ibs_Object:
                    INSERT INTO ibs_Object
                           (id, oid, state, tVersionId,
                            typeCode, typeName, isContainer, 
                            name, containerId, containerKind,
                            containerOid2,
                            isLink, linkedObjectId,
                            showInMenu, flags, owner,
                            oLevel, posNo, posNoPath,
                            creationDate, creator,
                            lastChanged, changer,
                            validUntil,
                            description, icon,
                            processState, rKey, consistsOfId)
                    VALUES (@l_newId, @l_newOid, @l_newState, @l_newTVersionId,
                            @l_newTypeCode, @l_newTypeName, @l_newIsContainer, 
                            @l_newName, @l_newContainerId, @l_newContainerKind,
                            @l_newContainerOid2,
                            @l_newIsLink, @l_newLinkedObjectId,
                            @l_newShowInMenu, @l_newFlags, @ai_userid,
                            @l_newOLevel, @l_newPosNo, @l_newPosNoPath,
                            getDate(), @ai_userId,
                            getDate(), @ai_userId,
                            @l_newValidUntil,
                            @l_newDescription, @l_newIcon,
                            @l_newProcessState, @l_newRKey, @l_newConsistsOfId)

                    -- create a copy of the actual business object:
                    EXEC @l_retValue = p_Object$switchCopy @l_oldOid, @ai_userId,
                                        @l_newTVersionId, @l_newOid

                    -- store old, new oid of the copied object and the copyId:
                    INSERT INTO ibs_Copy (oldOid, newOid, copyId)
                    VALUES  (@l_oldOid, @l_newOid, @l_copyId)

---------
--
-- START: SHOULD NOT BE IN BASE-FUNCTIONALITY
--
---------
                    -- for object-type ATTACHMENT only
                    -- only if copied object (root-object) is attachment
                    -- ensures that the flags and a master are set
                    IF (@l_oldOid = @l_rootOid AND @l_newTVersionId = 0x01010051)
                        EXEC p_Attachment_01$ensureMaster @l_newContainerId, null
---------
--
-- END: SHOULD NOT BE IN BASE-FUNCTIONALITY
--
---------
                END

                -- ELSE                    -- structural error occurred
                -- BEGIN
                    -- object with structural error was not copied
                    --
                    -- ENTRY IN ERRORLOG (should be made):
                    -- 'Error while reading/calc new objects structural data; ' +
                    -- 'possible cause of error: containerId/posNoPath-unconsistency; ' +
                    -- 'object will not be copied -> target-structure consistency granted; ' +
                    -- 'oid of object = ' + l_oldOid
                    --
                -- END -- else structural error occurred


                -- get next object to be copied:
                FETCH NEXT FROM containerCursor INTO @l_oldOid, @l_oldContainerId
            END -- while

            -- dump cursor structures
            CLOSE containerCursor
            DEALLOCATE containerCursor


            -- bend existing references which are linked to objects in
            -- copied sub-structure:
            UPDATE  ibs_Object
            SET     linkedObjectId =
                        (  SELECT  newOid
                           FROM    ibs_Copy
                           WHERE   oldOid = linkedObjectId
                           AND     copyId = @l_copyId
                        )
            WHERE   oid IN
                        (  SELECT newOid
                           FROM   ibs_Copy
                           WHERE  copyId = @l_copyId
                        )
              AND   linkedObjectId IN
                        (  SELECT oldOid
                           FROM   ibs_Copy
                           WHERE  copyId = @l_copyId
                        )


            -- copy the references:
            INSERT  INTO ibs_Reference (referencingOid, fieldName, referencedOid, kind)
            SELECT  c.newOid, ref.fieldName, ref.referencedOid, ref.kind
            FROM    ibs_Object o, ibs_Copy c, ibs_Object refO, ibs_Reference ref
            WHERE   c.copyId = @l_copyId
                AND o.oid = c.oldOid
                AND o.oid = ref.referencingOid
                AND refO.oid = ref.referencedOid
                AND refO.state = @c_ST_ACTIVE
                AND o.state = @c_ST_ACTIVE

            -- ensure that references to copied objects are correct:
            UPDATE  ibs_Reference
            SET     referencedOid =
                    (   SELECT  newOid
                        FROM    ibs_Copy
                        WHERE   copyId = @l_copyId
                            AND oldOid = referencedOid
                    )
            WHERE   referencingOid IN
                    (   SELECT  newOid
                        FROM    ibs_Copy
                        WHERE   copyId = @l_copyId
                    )
                AND referencedOid IN
                    (   SELECT  oldOid
                        FROM    ibs_Copy
                        WHERE   copyId = @l_copyId
                    )

            -- Enabled again due to IBS-734:
            --   Exception when copy the same object twice
            -- delete all tuples of this copy action:
            DELETE
            FROM    ibs_Copy
            WHERE   copyId = @l_copyId


        COMMIT TRANSACTION
    END -- if the user has the rights
    ELSE                                -- the user does not have the rights
    BEGIN
        SELECT  @l_retValue = @c_INSUFFICIENT_RIGHTS
    END -- else the user does not have the rights

    -- check if everything was o.k.:
    IF (@l_retValue = @C_ALL_RIGHT)
    BEGIN
        -- we can return the copy id:
        SELECT  @l_retValue = @l_copyId
    END -- if

    -- return the state value
    RETURN  @l_retValue
GO
-- p_Object$copy