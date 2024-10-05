/******************************************************************************
 * All stored procedures regarding the ibs_Object table which have to
 * be inserted to the DB after all the others. <BR>
 *
 * @version     $Id: ObjectProc2.sql,v 1.44 2012/10/03 08:55:10 rburgermann Exp $
 *
 * @author      Harald Buzzi (HB)  990723
 ******************************************************************************
 */

/******************************************************************************
 * Works as a switch to idenify all BusinessObjectTypes specific copymethods.
 * The Result is the oid of the duplicated BusinessObject. <BR>
 * This procedure also copies all links pointing to this object.
 *
 * @input parameters:
 * @param   ai_oid              Oid of the object to be copied.
 * @param   ai_userId           Id of the user who is copying the object.
 * @param   ai_tVersionId       The tVersionId of the object to be copied.
 * @param   ai_newOid           The oid of the newly created object.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  c_ALL_RIGHT             Action performed, values returned, everything ok.
 *  c_NOT_OK                An error occurred.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Object$switchCopy'
GO

-- create the new procedure:
CREATE PROCEDURE p_Object$switchCopy
(
    -- input parameters:
    @ai_oid                OBJECTID,
    @ai_userId             USERID,
    @ai_tVersionId         TVERSIONID,
    @ai_newOid             OBJECTID
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_OBJECTNOTFOUND       INT,            -- tuple not found

    -- local variables:
    @l_retValue             INT,            -- return value of function
    @l_error                INT,            -- the actual error code
    @l_ePos                 NVARCHAR (255), -- error position description
    @l_rowCount             INT,            -- row counter
    @l_typeCode             NAME            -- the type code for the object

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_OBJECTNOTFOUND       = 3

    -- initialize local variables:
SELECT
    @l_error = 0,
    @l_retValue = @c_ALL_RIGHT

-- body:
    -- get the type code:
    SELECT  @l_typeCode = t.code
    FROM    ibs_Type t, ibs_TVersion tv
    WHERE   t.id = tv.typeId
        AND tv.id = @ai_tVersionId

/*
----------------------
--  HP Performance
--
--    Procedure removed: functionality is now in p_Object$copy
----------------------

    EXEC @l_retValue = p_Object$performCopy @ai_oid, @ai_userId,
                            @ai_newOid OUTPUT
*/

    -- distuingish between the several types of objects which have own data
    -- structures:
    IF (@ai_tVersionId = 0x01010051     -- attachment
-- AJ 990525 inserted ... /////////////////////////////////////////////////////
        OR @ai_tVersionId = 0x01016801  -- file
        OR @ai_tVersionId = 0x01016901  -- hyperlink
        OR @ai_tVersionId = 0x01017c01  -- DocumentTemplate
        OR @ai_tVersionId = 0x01017411  -- ImportScript
        OR @ai_tVersionId = 0x01014c01  -- Workflowtemplate
-- ... AJ 990525 inserted /////////////////////////////////////////////////////
        )
    BEGIN
        -- copy Attachment
        EXEC @l_retValue =
            p_Attachment_01$BOCopy @ai_oid, @ai_userId, @ai_newOid
    END -- if attachment
    ELSE IF ((@ai_tVersionId = 0x01010301) -- discussion
              OR (@ai_tVersionId=0x01010a01) -- blackboard
      OR (@ai_tVersionId=0x01010321)) -- XMLdiscussion?
    BEGIN
        EXEC @l_retValue     =   p_Discussion_01$BOCopy @ai_oid, @ai_userId, @ai_newOid
    END -- else if discussion, blackboard

    ELSE IF (@ai_tVersionId=0x01012f01) -- Address_01
    BEGIN
        EXEC @l_retValue = p_Address_01$BOCopy @ai_oid, @ai_userId, @ai_newOid
    END -- Adress_01

    ELSE IF (@ai_tVersionId=0x01012a01) -- Person_01
    BEGIN
        EXEC @l_retValue = p_Person_01$BOCopy @ai_oid,  @ai_userId, @ai_newOid
    END -- Person_01

    ELSE IF (@ai_tVersionId=0x01012c01) -- Company_01
    BEGIN
        EXEC @l_retValue = p_Company_01$BOCopy @ai_oid, @ai_userId, @ai_newOid
    END -- Company
    ELSE IF (@ai_tVersionId=0x01010401) -- Thema_01
    BEGIN
        EXEC @l_retValue = p_Article_01$BOCopy @ai_oid, @ai_userId, @ai_newOid
    END -- Thema
    ELSE IF (@ai_tVersionId=0x01010501) -- Beitrag_01
    BEGIN
        EXEC @l_retValue = p_Article_01$BOCopy @ai_oid, @ai_userId, @ai_newOid
    END -- Beitrag
    ELSE IF (@ai_tVersionId=0x010100b1) -- Group_01
    BEGIN
        EXEC @l_retValue = p_Group_01$BOCopy @ai_oid, @ai_userId, @ai_newOid
    END -- Group
    ELSE IF (@ai_tVersionId=0x010100a1) -- User_01
    BEGIN
        EXEC @l_retValue = p_User_01$BOCopy @ai_oid, @ai_userId, @ai_newOid
    END -- User
    ELSE IF (@ai_tVersionId = 0x010100F1) --Domõnen
    BEGIN
        EXEC @l_retValue = p_Domain_01$BOCopy @ai_oid, @ai_userId, @ai_newOid
    END -- Domõnen
    ELSE IF (@ai_tVersionId = 0x01010201) --   Termin_01
    BEGIN
        EXEC @l_retValue = p_Termin_01$BOCopy @ai_oid, @ai_userId, @ai_newOid

    END -- Domõnen
    ELSE IF (@ai_tVersionId = 0x01012201) --   Price_01
    BEGIN
        EXEC @l_retValue = p_Price_01$BOCopy @ai_oid, @ai_userId, @ai_newOid
    END -- Price
    ELSE IF (@ai_tVersionId = 0x01011501) --   Product_01
    BEGIN
        EXEC @l_retValue = p_Product_01$BOCopy @ai_oid, @ai_userId, @ai_newOid
    END -- Product
    ELSE IF (@ai_tVersionId = 0x01016b01) --   Notiz_01
    BEGIN
        EXEC @l_retValue = p_Note_01$BOCopy @ai_oid, @ai_userId, @ai_newOid
    END -- Notiz
    ELSE IF (@ai_tVersionId = 0x01017e01) --   XMLViewerContainer_01
    BEGIN
        EXEC @l_retValue = p_XMLViewerContainer_01$BOCopy @ai_oid, @ai_userId, @ai_newOid
    END -- Notiz
    ELSE IF (@ai_tVersionId = 0x01017501) --   XMLViewer_01
    BEGIN
        EXEC @l_retValue = p_XMLViewer_01$BOCopy @ai_oid, @ai_userId, @ai_newOid
    END -- Notiz
    ELSE IF (@ai_tVersionId = 0x01017471 OR     --   FileConnector_01
             @ai_tVersionId = 0x01017481 OR     --   FTPConnector_01
             @ai_tVersionId = 0x01017491 OR     --   MailConnector_01
             @ai_tVersionId = 0x010174A1 OR     --   HTTPConnector_01
             @ai_tVersionId = 0x010174B1 OR     --   EDISwitchConnector_01
             @ai_tVersionId = 0x010174C1 OR     --   CGIScriptConnector_01
             @ai_tVersionId = 0x010174D1        --   SAPBCXMLRFCConnector_01
            )
    BEGIN
        EXEC @l_retValue = p_Connector_01$BOCopy @ai_oid, @ai_userId, @ai_newOid
    END -- Connectors
    ELSE IF (@ai_tVersionId = 0x01017C01) --   DocumentTemplate_01
    BEGIN
        EXEC @l_retValue = p_DocumentTemplate_01$BOCopy @ai_oid, @ai_userId, @ai_newOid
    END -- DocumentTemplate
    ELSE IF (@ai_tVersionId = 0x01010311) --   DiscussionTemplate_01
    BEGIN
        EXEC @l_retValue = p_XMLDiscTemplate_01$BOCopy @ai_oid, @ai_userId, @ai_newOid
    END -- XMLDiskussionTemplate
    ELSE IF (@ai_tVersionId = 0x01017511) --   DiscXMLViewer_01
    BEGIN
        EXEC @l_retValue = p_DiscXMLViewer_01$BOCopy @ai_oid, @ai_userId, @ai_newOid
    END -- DiscXMLViewer
    ELSE IF (@ai_tVersionId = 0x01017F21) --   QueryCreator_01
    BEGIN
        EXEC @l_retValue = p_QueryCreator_01$BOCopy @ai_oid, @ai_userId, @ai_newOid
    END -- QueryCreator
    ELSE IF (@ai_tVersionId = 0x01017F51) --   QueryExecutive
    BEGIN
        EXEC @l_retValue = p_QueryExecutive_01$BOCopy @ai_oid, @ai_userId, @ai_newOid
    END -- QueryExecutive
    ELSE IF (@ai_tVersionId = 0x01017451) --   Translator
    BEGIN
        EXEC @l_retValue = p_Translator_01$BOCopy @ai_oid, @ai_userId, @ai_newOid
    END -- Translator
    ELSE IF (@ai_tVersionId = 0x01017381) --   ASCIITranslator
    BEGIN
        EXEC @l_retValue = p_ASCIITranslator_01$BOCopy @ai_oid, @ai_userId, @ai_newOid
    END -- ASCIITranslator
    ELSE IF (@l_typeCode = N'EDITranslator') -- EDITranslator
    BEGIN
        EXEC @l_retValue = p_EDITranslator_01$BOCopy @ai_oid, @ai_userId, @ai_newOid
    END -- EDITranslator
    ELSE
    BEGIN
        -- check if the tVersionId belongs to a m2 type defined by a document template.
        -- in this case call the copy procedure for the XMLViewer object type.
        IF EXISTS ( SELECT  oid
                    FROM    ibs_DocumentTemplate_01
                    WHERE   tVersionId = @ai_tVersionId)
        BEGIN
            EXEC @l_retValue = p_XMLViewer_01$BOCopy @ai_oid, @ai_userId, @ai_newOid
        END -- if XMLViewer object type
    END -- else
/*
    ELSE
    BEGIN
        select 'keine spezielle proz gefunden'
    END
*/

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_Object$switchCopy


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


/******************************************************************************
 * Update all referenced OIDs within the table ibs_reference. <BR>
 * This method ONLY handles DocumentTemplates. <BR>
 * References without a document templates must be migrated manually. <BR>
 *  
 * @input parameters:
 * @param   ai_oldOid           OID of the old object
 * @param   ai_newOid           OID of the new object
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_changeIbsTableReferences'
GO

-- create the new procedure:
CREATE PROCEDURE p_changeIbsTableReferences
(
    -- input parameters:
    @ai_oldOid              OBJECTID,
    @ai_newOid              OBJECTID
)
AS
-- local variables
DECLARE
    @l_tableName            sysname,
      @l_columnName           sysname,
    @sql                    nvarchar(4000),
    @ai_oldOid_s            OBJECTIDSTRING,
    @ai_newOid_s            OBJECTIDSTRING

BEGIN

    -- Update all referencedOid fields in all document template tables
    DECLARE selectCursor2 CURSOR FOR
    SELECT col.TABLE_NAME, 
           col.COLUMN_NAME
    FROM ibs_documenttemplate_01 doct,
         ibs_object o,
         ibs_reference ref,
         INFORMATION_SCHEMA.COLUMN_DOMAIN_USAGE col
    WHERE doct.tVersionID = o.tversionId
    AND   o.oid = ref.referencingOid
    AND   ref.referencedOid = @ai_oldOid
    AND   doct.tableName = col.TABLE_NAME
    AND   col.DOMAIN_NAME = 'OBJECTID'
    AND   col.COLUMN_NAME != 'oid'
    
    -- open the cursor:
    OPEN    selectCursor2

    -- get the first object:
    FETCH NEXT FROM selectCursor2 INTO @l_tableName, @l_columnName

    -- loop through all objects:
    WHILE (@@FETCH_STATUS <> -1)
                                       -- another object found?
    BEGIN
        
        PRINT 'UPDATE: referencedOid in ' + @l_tableName + ' / FIELD: ' + @l_columnName
    -- convert oids to strings:
    EXEC p_byteToString @ai_oldOid, @ai_oldOid_s OUTPUT
    EXEC p_byteToString @ai_newOid, @ai_newOid_s OUTPUT

    SET @sql = 'UPDATE ' + @l_tableName +
               ' SET ' + @l_columnName + ' = ' + @ai_newOid_s +
               ' WHERE ' + @l_columnName + ' = ' + @ai_oldOid_s
  
        EXEC sp_executesql @sql
    
        -- get next tuple:
        FETCH NEXT FROM selectCursor2 INTO @l_tableName, @l_columnName

    END -- while another tuple found

    -- close the not longer needed cursor:
    CLOSE selectCursor2
    DEALLOCATE selectCursor2

    -- update referencedOid of object within ibs_reference 
    PRINT 'UPDATE: ibs_reference / FIELD: referencedOid ...'
    UPDATE ibs_reference
    SET referencedOid = @ai_newOid
    WHERE referencedOid = @ai_oldOid

    -- update referencingOid of object within ibs_reference 
    PRINT 'UPDATE: ibs_reference / FIELD: referencingOid ...'
    UPDATE ibs_reference
    SET referencingOid = @ai_newOid
    WHERE referencingOid = @ai_oldOid
    
END
GO
-- p_changeIbsTableReferences


/******************************************************************************
 * Update all references OIDs of an object with a new OID. <BR>
 *  
 * @input parameters:
 * @param   ai_oldOid           OID of the old object
 * @param   ai_newOid           OID of the new object
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_changeObjectReferences'
GO

-- create the new procedure:
CREATE PROCEDURE p_changeObjectReferences
(
    -- input parameters:
    @ai_oldOid              OBJECTID,
    @ai_newOid              OBJECTID
)
AS
BEGIN
    -- update linkedObjectId of object within ibs_object 
    PRINT 'UPDATE: ibs_object / FIELD: linkedObjectId ...'
    UPDATE ibs_object
    SET linkedObjectId = @ai_newOid
    WHERE linkedObjectId = @ai_oldOid

    -- update distributeId of object within ibs_sentObject_01 
    PRINT 'UPDATE: ibs_sentObject_01 / FIELD: distributeId ...'
    UPDATE ibs_sentObject_01
    SET distributeId = @ai_newOid
    WHERE distributeId = @ai_oldOid

    -- update distributedId of object within ibs_receivedObject_01 
    PRINT 'UPDATE: ibs_sentObject_01 / FIELD: distributedId ...'
    UPDATE ibs_receivedObject_01
    SET distributedId = @ai_newOid
    WHERE distributedId = @ai_oldOid

    -- update paramOid1 of object within obs_standard_reminder 
    PRINT 'UPDATE: obs_standard_reminder / FIELD: paramOid1 ...'
    UPDATE obs_standard_reminder
    SET paramOid1 = @ai_newOid
    WHERE paramOid1 = @ai_oldOid

    -- update paramOid2 of object within obs_standard_reminder 
    PRINT 'UPDATE: obs_standard_reminder / FIELD: paramOid2 ...'
    UPDATE obs_standard_reminder
    SET paramOid2 = @ai_newOid
    WHERE paramOid2 = @ai_oldOid

    -- update oid of object within ibs_protocol_01 
    PRINT 'UPDATE: ibs_protocol_01 / FIELD: oid ...'
    UPDATE ibs_protocol_01
    SET oid = @ai_newOid
    WHERE oid = @ai_oldOid

    -- update containerId of object within ibs_protocol_01 
    PRINT 'UPDATE: ibs_protocol_01 / FIELD: containerId ...'
    UPDATE ibs_protocol_01
    SET containerId = @ai_newOid
    WHERE containerId = @ai_oldOid

    -- SPECIAL HANDLING to INFORM user for MANUAL changes
    EXEC p_changeIbsTableReferences @ai_oldOid, @ai_newOid

END
GO
-- p_changeObjectReferences


/******************************************************************************
 * Update an object with a new OID. <BR>
 * This includes the update of all referenced objects of this object. <BR>
 * 
 * @input parameters:
 * @param   ai_oldOid           OID of the old object
 * @param   ai_newOid           OID of the new object
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_changeOid'
GO

-- create the new procedure:
CREATE PROCEDURE p_changeOid
(
    -- input parameters:
    @ai_oldOid              OBJECTID,
    @ai_newOid              OBJECTID
)
AS
BEGIN
    -- update oid of object within ibs_object 
    PRINT 'UPDATE: ibs_object / FIELD: oid ...'
    UPDATE ibs_object
    SET oid = @ai_newOid
    WHERE oid = @ai_oldOid

    -- update containerid of object within ibs_object 
    PRINT 'UPDATE: ibs_object / FIELD: containerId ...'
    UPDATE ibs_object
    SET containerId = @ai_newOid
    WHERE containerId = @ai_oldOid

    -- the containerOid2 IS NOT UPDATED through the existing trigger
    -- thus we update all references of containerOid2 here manually
    PRINT 'UPDATE: ibs_object / FIELD: containerOid2 ...'
    UPDATE ibs_object
    SET containerOid2 = @ai_newOid
    WHERE containerOid2 = @ai_oldOid
    
    -- update OIDs of object in the respective tables
    EXEC p_changeObjectReferences @ai_oldOid, @ai_newOid
    
END
GO
-- p_changeOid


/******************************************************************************
 * Update the type (tversion, typcode, typename) of an object. <BR>
 * This updates the necessary update of the OID of the object too. <BR>
 * 
 * @input parameters:
 * @param   ai_oldOid           OID of the old object
 * @param   ai_newTVersionId    TVersionId of the new object
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_changeType'
GO

-- create the new procedure:
CREATE PROCEDURE p_changeType
(
    -- input parameters:
    @ai_oldOid               OBJECTID,
    @ai_tVersionId           TVERSIONID
)
AS
-- local variables
DECLARE
    @l_newOid               OBJECTID,           -- new generate OID
    @l_typeName             NAME,           -- the name of the type
    @l_typeCode             NAME,           -- the code of the type
    @l_id                   int             -- the code of the type
    
BEGIN
    -- get the typecode and typename from the ibs_type table
    SELECT @l_typecode = t.code,
           @l_typename = t.name
    FROM   ibs_tversion tv, ibs_type t
    WHERE  tv.typeId = t.id
    AND    tv.id = @ai_tVersionId

    -- get the id of the given object
    SELECT @l_id = o.id
    FROM   ibs_object o
    WHERE  o.oid = @ai_oldOid

    -- create new OID for the new object
    EXEC p_createOid @ai_tVersionId, @l_id, @l_newOid OUTPUT
    
    -- update OIDs of object in the respective tables
    EXEC p_changeOid @ai_oldOid, @l_newOid
    
    -- update tversionId of object within ibs_object 
    PRINT 'UPDATE: ibs_object / FIELD: tversionId ...'
    UPDATE ibs_object
    SET tversionId = @ai_tVersionId
    WHERE oid = @l_newOid

    -- update typecode, typename of object within ibs_object 
    PRINT 'UPDATE: ibs_object / FIELD: typeName, typeCode ...'
    UPDATE ibs_object
    SET typeName = @l_typeName, 
        typeCode = @l_typeCode
    WHERE oid = @l_newOid
END
GO
-- p_changeType


/******************************************************************************
 * Updates the has file flag of an object based on the provided file info. <BR>
 * 
 * @input parameters:
 * @param     @ai_oid_s     OBJECTIDSTRING
 * @param     @ai_path      NVARCHAR(255)
 * @param     @ai_fileName  NVARCHAR(255)
 *
 * @output parameters:
 * @return  A value representing the performed action.
 *  c_NO_FLAG_SET             No flag has been set.
 *  c_FOLDER_FLAG_SET         The flag has been set on the object represented by the oid of the folder.
 *  c_OBJECT_FLAG_SET         The flag has been set on the object defined within the found ibs_attachment_01 entry.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_setHasFile'
GO

-- create the new procedure:
CREATE PROCEDURE p_setHasFile
(
  -- input parameters:
  @ai_oid_s     OBJECTIDSTRING,
  @ai_path      NVARCHAR(255),
  @ai_fileName  NVARCHAR(255)
)
AS
DECLARE
  -- constants:
  @c_HAS_FILE            INT,
  @c_NO_FLAG_SET         INT,            -- error occured
  @c_FOLDER_FLAG_SET  INT,            -- everything was o.k.
  @c_OBJECT_FLAG_SET     INT,            -- error occured
  -- local variables:
  @l_oid OBJECTID

BEGIN
  -- assign constants:
  SELECT
    @c_HAS_FILE = 512,
    @c_NO_FLAG_SET        = 0,
    @c_FOLDER_FLAG_SET    = 1,
    @c_OBJECT_FLAG_SET    = 3
  
  EXEC p_stringToByte @ai_oid_s, @l_oid OUTPUT

  -- check if the path ends with the separator
  IF (RIGHT (@ai_path, 1) <> '/')
  BEGIN
    -- add the separator
    SELECT @ai_path = @ai_path + '/'
  END -- if

  -- if the file is found within ibs_Attachment_01 set the flag for the object stored within ibs_Attachment_01
  UPDATE ibs_Object
  SET flags = flags | @c_HAS_FILE
  WHERE oid IN
  (
      -- find the file within ibs_Attachment_01
      SELECT oid
      FROM ibs_Attachment_01
      WHERE filename = @ai_filename
      AND path = @ai_path
  )

  -- if the file was not found within ibs_Attachment_01 it must belong to the folder's object itself
  IF (@@ROWCOUNT = 0)
  BEGIN
      UPDATE ibs_Object
      SET flags = flags | @c_HAS_FILE
      WHERE oid IN
      (
        -- find the object within ibs_object and check if it is no container
        SELECT oid
        FROM ibs_Object
        WHERE oid = @l_oid
        -- this check is necessary since files may not exist within ibs_attachment_01 but do not belong to the folder's oid anyway
        AND isContainer = 0
      )
      
      IF (@@ROWCOUNT = 0)       
        RETURN @c_NO_FLAG_SET
      ELSE
        RETURN @c_FOLDER_FLAG_SET
  END -- if
  ELSE
      RETURN @c_OBJECT_FLAG_SET
END -- p_setHasFile