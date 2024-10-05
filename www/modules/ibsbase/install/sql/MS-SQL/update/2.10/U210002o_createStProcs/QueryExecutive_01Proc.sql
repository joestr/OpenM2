/******************************************************************************
 * All stored procedures regarding to the QueryExecutive for dynamic 
 * Search - Queries. <BR>
 *
 * @version     $Id: QueryExecutive_01Proc.sql,v 1.1 2010/02/25 13:53:47 btatzmann Exp $
 *
 * @author      Jansa Andreas (AJ)  20000918
 ******************************************************************************
 */

/******************************************************************************
 * Creates a new object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @userId             ID of the user who is creating the object.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 * @param   @tVersionId         Type of the new object.
 * @param   @name               Name of the object.
 * @param   @containerId_s      ID of the container where object shall be 
 *                              created in.
 * @param   @containerKind      Kind of object/container relationship
 * @param   @isLink             Defines if the object is a link
 * @param   @linkedObjectId_s   If the object is a link this is the ID of the
 *                              where the link shows to.
 * @param   @description        Description of the object.
 *
 * @output parameters:
 * @param   @oid_s              OID of the newly created object.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
 
-- delete existing procedure:
p_dropProc N'p_QueryExecutive_01$create'
GO
-- create the new procedure:
CREATE PROCEDURE p_QueryExecutive_01$create
(
    -- common input parameters:
    @ai_userId         USERID,
    @ai_op             INT,
    @ai_tVersionId     TVERSIONID,
    @ai_name           NAME,
    @ai_containerId_s  OBJECTIDSTRING,
    @ai_containerKind  INT,
    @ai_isLink         BOOL,
    @ai_linkedObjectId_s OBJECTIDSTRING,
    @ai_description    DESCRIPTION,
    -- common output parameters:
    @ao_oid_s          OBJECTIDSTRING OUTPUT
)
AS
-- definitions:
    -- define return constants:
    DECLARE @c_NOT_OK INT,
            @c_ALL_RIGHT INT, 
            @c_INSUFFICIENT_RIGHTS INT,
            @c_NOOID OBJECTID

    -- set constants:
    SELECT  @c_NOT_OK = 0,
            @c_ALL_RIGHT = 1, 
            @c_INSUFFICIENT_RIGHTS = 2,
            @c_NOOID = 0x0000000000000000

    -- define return values:
    DECLARE @l_retValue INT                   -- return value of this procedure
    -- initialize return values:
    SELECT  @l_retValue = @c_NOT_OK
    -- define local variables:
    DECLARE @l_oid OBJECTID
    -- initialize local variables:
    SELECT  @l_oid = @c_NOOID


-- body:
    BEGIN TRANSACTION
        -- create base object:
        EXEC @l_retValue = p_Object$performCreate
                            @ai_userId, @ai_op, @ai_tVersionId, 
                            @ai_name, @ai_containerId_s, @ai_containerKind, 
                            @ai_isLink, @ai_linkedObjectId_s, @ai_description, 
                            @ao_oid_s OUTPUT, @l_oid OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT)     -- object created successfully?
        BEGIN
            -- create object type specific data:
            INSERT INTO ibs_QueryExecutive_01
                        (oid, reportTemplateOid, searchValues, 
                        matchTypes, rootObjectOid, showSearchForm, showDOMTree)
            VALUES      (@l_oid, @c_NOOID, NULL, 
                        NULL, @c_NOOID, 0, 0)
        END -- if
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_QueryExecutive_01$create



/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
 *
 * @input parameters:
 *
 * @output parameters:
 *
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- delete existing procedure:
p_dropProc N'p_QueryExecutive_01$change'
GO
-- create the new procedure:
CREATE PROCEDURE p_QueryExecutive_01$change
(
    -- common input parameters:
    @ai_oid_s               OBJECTIDSTRING,
    @ai_userId              USERID,
    @ai_op                  INT,
    @ai_name                NAME,
    @ai_validUntil          DATETIME,
    @ai_description         DESCRIPTION,
    @ai_showInNews          BOOL,
    @ai_reportTemplateOid_s OBJECTIDSTRING,
    @ai_searchValues        DESCRIPTION,
    @ai_matchTypes          DESCRIPTION,
    @ai_rootObjectOid_s     OBJECTIDSTRING,
    @ai_showSearchForm      BOOL,
    @ai_showDOMTree         BOOL
)
AS
    -- definitions:
    -- define return constants:
    DECLARE @c_NOT_OK INT,
            @c_ALL_RIGHT INT, 
            @c_INSUFFICIENT_RIGHTS INT,
            @c_ALREADY_EXISTS INT,
            @c_NOOID OBJECTID

    -- set constants:
    SELECT  @c_NOT_OK = 0,
            @c_ALL_RIGHT = 1, 
            @c_INSUFFICIENT_RIGHTS = 2,
            @c_ALREADY_EXISTS = 21,
            @c_NOOID = 0x0000000000000000

    -- define return values:
    DECLARE @l_retValue INT                   -- return value of this procedure
    -- initialize return values:
    SELECT  @l_retValue = @c_NOT_OK
    -- define local variables:
    DECLARE @l_oid OBJECTID,
            @l_repTempOid OBJECTID,
            @l_rootObjectOid OBJECTID

    -- initialize local variables:
    SELECT  @l_oid = @c_NOOID,
            @l_repTempOid = @c_NOOID,
            @l_rootObjectOid = @c_NOOID

    -- convert oidString to oid
    EXEC p_stringToByte @ai_oid_s, @l_oid OUTPUT
    EXEC p_stringToByte @ai_reportTemplateOid_s, @l_repTempOid OUTPUT
    EXEC p_stringToByte @ai_rootObjectOid_s, @l_rootObjectOid OUTPUT


    -- body:
    BEGIN TRANSACTION
        -- perform the change of the object:
        EXEC @l_retValue = p_Object$performChange
                @ai_oid_s, @ai_userId, @ai_op, @ai_name,
                @ai_validUntil, @ai_description, @ai_showInNews, @l_oid OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT)     -- object created successfully?
        BEGIN
            UPDATE  ibs_QueryExecutive_01
            SET     reportTemplateOid = @l_repTempOid,
                    searchValues = @ai_searchValues,
                    matchTypes = @ai_matchTypes,
                    rootObjectOid = @l_rootObjectOid,
                    showSearchForm = @ai_showSearchForm,
                    showDOMTree = @ai_showDOMTree
            WHERE   oid = @l_oid
        END

    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_QueryExecutive_01$change



/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be retrieved.
 * @param   @userId             Id of the user who is getting the data.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 *
 * @output parameters:
 * @param   @state              The object's state.
 * @param   @tVersionId         ID of the object's type (correct version).
 * @param   @typeName           Name of the object's type.
 * @param   @name               Name of the object itself.
 * @param   @containerId        ID of the object's container.
 * @param   @containerName      Name of the object's container.
 * @param   @containerKind      Kind of object/container relationship.
 * @param   @isLink             Is the object a link?
 * @param   @linkedObjectId     Link if isLink is true.
 * @param   @owner              ID of the owner of the object.
 * @param   @creationDate       Date when the object was created.
 * @param   @creator            ID of person who created the object.
 * @param   @lastChanged        Date of the last change of the object.
 * @param   @changer            ID of person who did the last change to the
 *                              object.
 * @param   @validUntil         Date until which the object is valid.
 * @param   @description        Description of the object.
 * @param   @showInNews         flag if object should be shown in newscontainer
 * @param   @checkedOut         Is the object checked out?
 * @param   @checkOutDate       Date when the object was checked out
 * @param   @checkOutUser       id of the user which checked out the object
 * @param   @checkOutUserOid    Oid of the user which checked out the object
 *                              is only set if this user has the right to READ
 *                              the checkOut user
 * @param   @checkOutUserName   name of the user which checked out the object,
 *                              is only set if this user has the right to view
 *                              the checkOut-User
 *
 * @param   @ao_repTempOid      desc
 * @param   @ao_searchValues    desc
 * @param   @ao_matchTypes      desc
 * @param   @ao_rootObjectOid   desc
 * @param   ao_showSearchForm   Shall the search form be displayed?
 * @param   ao_showDOMTree      Shall the DOM tree be displayed?
 *
 * @returns A value representing the state of the procedure.
 *  c_ALL_RIGHT               Action performed, values returned, everything ok.
 *  c_INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  c_OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- delete existing procedure:
p_dropProc N'p_QueryExecutive_01$retrieve'
GO
-- create the new procedure:
CREATE PROCEDURE p_QueryExecutive_01$retrieve
(
    -- common input parameters:
    @ai_oid_s          OBJECTIDSTRING,
    @ai_userId         USERID,
    @ai_op             INT,
    -- common output parameters:
    @ao_state          STATE           OUTPUT,
    @ao_tVersionId     TVERSIONID      OUTPUT,
    @ao_typeName       NAME            OUTPUT,
    @ao_name           NAME            OUTPUT,
    @ao_containerId    OBJECTID        OUTPUT,
    @ao_containerName  NAME            OUTPUT,
    @ao_containerKind  INT             OUTPUT,
    @ao_isLink         BOOL            OUTPUT,
    @ao_linkedObjectId OBJECTID        OUTPUT,
    @ao_owner          USERID          OUTPUT,
    @ao_ownerName      NAME            OUTPUT,
    @ao_creationDate   DATETIME        OUTPUT,
    @ao_creator        USERID          OUTPUT,
    @ao_creatorName    NAME            OUTPUT,
    @ao_lastChanged    DATETIME        OUTPUT,
    @ao_changer        USERID          OUTPUT,
    @ao_changerName    NAME            OUTPUT,
    @ao_validUntil     DATETIME        OUTPUT,
    @ao_description    DESCRIPTION     OUTPUT,
    @ao_showInNews     BOOL            OUTPUT,
    @ao_checkedOut     BOOL            OUTPUT,
    @ao_checkOutDate   DATETIME        OUTPUT,
    @ao_checkOutUser   USERID          OUTPUT,
    @ao_checkOutUserOid OBJECTID       OUTPUT,
    @ao_checkOutUserName NAME          OUTPUT,

    -- type-specific output attributes:
    @ao_repTempOid      OBJECTID        OUTPUT,
    @ao_searchValues    DESCRIPTION     OUTPUT,
    @ao_matchTypes      DESCRIPTION     OUTPUT,
    @ao_rootObjectOid   OBJECTID        OUTPUT,
    @ao_showSearchForm  BOOL            OUTPUT,
    @ao_showDOMTree     BOOL            OUTPUT
)
AS
    -- definitions:
    -- define return constants:
    DECLARE @c_NOT_OK INT,
            @c_ALL_RIGHT INT, 
            @c_INSUFFICIENT_RIGHTS INT,
            @c_ALREADY_EXISTS INT,
            @c_NOOID OBJECTID

    -- set constants:
    SELECT  @c_NOT_OK = 0,
            @c_ALL_RIGHT = 1, 
            @c_INSUFFICIENT_RIGHTS = 2,
            @c_ALREADY_EXISTS = 21,
            @c_NOOID = 0x0000000000000000


    -- define return values:
    DECLARE @l_retValue INT                   -- return value of this procedure
    -- initialize return values:
    SELECT  @l_retValue = @c_NOT_OK
    -- define local variables:
    DECLARE @l_oid OBJECTID
    -- initialize local variables:
    SELECT  @l_oid = @c_NOOID

    -- body:
    BEGIN TRANSACTION
        -- retrieve the base object data:
        EXEC @l_retValue = p_Object$performRetrieve
                @ai_oid_s, @ai_userId, @ai_op,
                @ao_state OUTPUT, @ao_tVersionId OUTPUT, @ao_typeName OUTPUT, 
                @ao_name OUTPUT, @ao_containerId OUTPUT,
                @ao_containerName OUTPUT, @ao_containerKind OUTPUT,
                @ao_isLink OUTPUT, @ao_linkedObjectId OUTPUT, 
                @ao_owner OUTPUT, @ao_ownerName OUTPUT, 
                @ao_creationDate OUTPUT, @ao_creator OUTPUT,
                @ao_creatorName OUTPUT,
                @ao_lastChanged OUTPUT, @ao_changer OUTPUT,
                @ao_changerName OUTPUT,
                @ao_validUntil OUTPUT, @ao_description OUTPUT,
                @ao_showInNews OUTPUT, 
                @ao_checkedOut OUTPUT, @ao_checkOutDate OUTPUT, 
                @ao_checkOutUser OUTPUT, @ao_checkOutUserOid OUTPUT,
                @ao_checkOutUserName OUTPUT, 
                @l_oid OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT)     -- object created successfully?
        BEGIN
            SELECT  
                    @ao_repTempOid = reportTemplateOid,
                    @ao_searchValues = searchValues,
                    @ao_matchTypes = matchTypes,
                    @ao_rootObjectOid = rootObjectOid,
                    @ao_showSearchForm = showSearchForm,
                    @ao_showDOMTree = showDOMTree
            FROM    ibs_QueryExecutive_01
            WHERE   oid = @l_oid
        END -- if

    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_QueryExecutive_01$retrieve


/******************************************************************************
 * Deletes an object and all its values (incl. rights check). <BR>
 * This procedure also delets all links showing to this object.
 * 
 * @input parameters:
 * @param   @oid_s              ID of the object to be deleted.
 * @param   @userId             ID of the user who is deleting the object.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */

/*
-- delete existing procedure:
p_dropProc 'p_QueryExecutive_01$delete'
G O
-- create the new procedure:
CREATE PROCEDURE p_QueryExecutive_01$delete
(
    -- common input parameters:
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT
)
AS
    DECLARE @retValue INT               -- return value of this procedure

    -- return the state value:
    RETURN  @retValue
G O
-- p_QueryExecutive_01$delete
*/


/******************************************************************************
 * Copy an object and all its values. <BR>
 * 
 * @input parameters:
 * @param   @oid                ID of the object to be copy.
 * @param   @userId             ID of the user who is copying the object.
 * @param   @newOid             ID of the copy of the object.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */
-- delete existing procedure:
p_dropProc N'p_QueryExecutive_01$BOCopy'
GO
-- create the new procedure:
CREATE PROCEDURE p_QueryExecutive_01$BOCopy
(
    -- common input parameters:
    @ai_oid        OBJECTID ,
    @ai_userId     INTEGER ,
    @ai_newOid     OBJECTID 
)
AS
    -- definitions:
    -- define return constants:
    DECLARE @c_NOT_OK INT,
            @c_ALL_RIGHT INT, 
            @c_INSUFFICIENT_RIGHTS INT,
            @c_ALREADY_EXISTS INT,
            @c_NOOID OBJECTID

    -- set constants:
    SELECT  @c_NOT_OK = 0,
            @c_ALL_RIGHT = 1, 
            @c_INSUFFICIENT_RIGHTS = 2,
            @c_ALREADY_EXISTS = 21,
            @c_NOOID = 0x0000000000000000
            

    -- define return values:
    DECLARE @l_retValue INT                   -- return value of this procedure
    -- initialize return values:
    SELECT  @l_retValue = @c_NOT_OK
    -- define local variables:
    DECLARE @l_oid OBJECTID
    -- initialize local variables:
    SELECT  @l_oid = @c_NOOID
    -- body:
    
    INSERT INTO ibs_QueryExecutive_01 
                (oid, reportTemplateOid, searchValues, matchTypes, 
                rootObjectOid)
    SELECT      @ai_newOid, reportTemplateOid, searchValues, matchTypes, 
                rootObjectOid
    FROM        ibs_QueryExecutive_01 
    WHERE       oid = @ai_oid

    -- check if insert was performed correctly:
    IF (@@ROWCOUNT >= 1)                -- at least one row affected?
        SELECT  @l_retValue = @c_ALL_RIGHT  -- set return value

    RETURN @l_retValue
GO
-- p_QueryExecutive_01$BOCopy
