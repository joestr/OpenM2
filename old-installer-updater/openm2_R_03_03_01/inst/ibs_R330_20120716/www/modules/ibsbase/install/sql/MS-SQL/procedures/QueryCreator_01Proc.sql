/******************************************************************************
 * All stored procedures regarding to the QueryCreator_01 for dynamic
 * Search - Queries. <BR>
 *
 * @version     $Id: QueryCreator_01Proc.sql,v 1.9 2009/12/02 18:35:02 rburgermann Exp $
 *
 * @author      Monika Eisenkolb (ME)  051001
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
EXEC p_dropProc N'p_QueryCreator_01$create'
GO
-- create the new procedure:
CREATE PROCEDURE p_QueryCreator_01$create
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
            @c_INSUFFICIENT_RIGHTS INT

    -- set constants:
    SELECT  @c_NOT_OK = 0,
            @c_ALL_RIGHT = 1,
            @c_INSUFFICIENT_RIGHTS = 2

    -- define return values:
    DECLARE @l_retValue INT                   -- return value of this procedure
    -- initialize return values:
    SELECT  @l_retValue = @c_NOT_OK
    -- define local variables:
    DECLARE @l_oid OBJECTID
    -- initialize local variables:
    SELECT  @l_oid = 0x0000000000000000


    -- body:
    BEGIN TRANSACTION
        -- create base object:
        EXEC @l_retValue = p_Object$performCreate @ai_userId, @ai_op, @ai_tVersionId,
                            @ai_name, @ai_containerId_s, @ai_containerKind,
                            @ai_isLink, @ai_linkedObjectId_s, @ai_description,
                            @ao_oid_s OUTPUT, @l_oid OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT)     -- object created successfully?
        BEGIN
            -- create object type specific data:
            INSERT INTO ibs_QueryCreator_01 (oid, selectString, fromString,
                        whereString, queryType, groupByString, orderByString,
                        columnHeaders, queryAttrTypesForHeaders,
                        queryAttrForHeaders, searchFieldTokens,
                        queryAttrForFields, queryAttrTypesForFields, 
                        resultCounter, enableDebug, category)
            VALUES      (@l_oid, N' ', N' ',
                        N' ', 1, NULL, NULL,
                        N' ', N' ',
                        N' ', N' ',
                        N' ', N' ', 
                        -1, 0, N' ')
        END

    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_QueryCreator_01$create



/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
 *
 * @input parameters:
 *
 *
 * @param   @ai_queryString        desc
 * @param   @ai_columnHeaders      desc
 * @param   @ai_queryAttributes    desc
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_QueryCreator_01$change'
GO
-- create the new procedure:
CREATE PROCEDURE p_QueryCreator_01$change
(
    -- common input parameters:

    @ai_oid_s           OBJECTIDSTRING,
    @ai_userId          USERID,
    @ai_op              INT,
    @ai_name            NAME,
    @ai_validUntil      DATETIME,
    @ai_description     DESCRIPTION,
    @ai_showInNews      BOOL,
    -- typespecific input parameters
    @ai_queryType       INTEGER,
    @ai_groupByString   DESCRIPTION,
    @ai_orderByString   DESCRIPTION,
    @ai_resultCounter   INTEGER,
    @ai_enableDebug     BOOL,
    @ai_category   	    NAME


/*
    -- FOLLOWING TEXT-FIELDS ARE NOT POSSIBLE

    @ai_selectString    DESCRIPTION,
    @ai_fromString      DESCRIPTION,
    @ai_whereString     DESCRIPTION,
    @ai_columnHeaders   DESCRIPTION,
    @ai_queryAttributesForHeaders       DESCRIPTION,
    @ai_queryAttributesTypesForHeaders  DESCRIPTION,

    @ai_searchFieldTokens           DESCRIPTION,
    @ai_queryAttributesForFields        DESCRIPTION
    @ai_queryAttributesTypesForFields   DESCRIPTION
*/
)
AS

    -- definitions:
    -- define return constants:
    DECLARE @c_NOT_OK INT,
            @c_ALL_RIGHT INT,
            @c_INSUFFICIENT_RIGHTS INT,
            @c_ALREADY_EXISTS INT

    -- set constants:
    SELECT  @c_NOT_OK = 0,
            @c_ALL_RIGHT = 1,
            @c_INSUFFICIENT_RIGHTS = 2,
            @c_ALREADY_EXISTS = 21

    -- define return values:
    DECLARE @l_retValue INT                   -- return value of this procedure
    -- initialize return values:
    SELECT  @l_retValue = @c_NOT_OK
    -- define local variables:
    DECLARE @l_oid OBJECTID
    -- initialize local variables:
    SELECT  @l_oid = 0x0000000000000000

    -- convert oidString to oid
    EXEC p_stringToByte @ai_oid_s, @l_oid OUTPUT


    -- body:
    BEGIN TRANSACTION
        -- perform the change of the object:
        EXEC @l_retValue = p_Object$performChange @ai_oid_s, @ai_userId, @ai_op, @ai_name,
                @ai_validUntil, @ai_description, @ai_showInNews, @l_oid OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT)     -- object created successfully?
        BEGIN
            UPDATE  ibs_QueryCreator_01
            SET     queryType     = @ai_queryType,
                    groupByString = @ai_groupByString,
                    orderByString = @ai_orderByString,
                    resultCounter = @ai_resultCounter,
                    enableDebug   = @ai_enableDebug,
                    category   	  = @ai_category
                    
            WHERE   oid = @l_oid
        END

    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_QueryCreator_01$change



/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @ai_oid_s              ID of the object to be retrieved.
 * @param   @ai_userId             Id of the user who is getting the data.
 * @param   @ai_op                 Operation to be performed (used for rights
 *                              check).
 *
 * @output parameters:
 * @param   @ao_state              The object's state.
 * @param   @ao_tVersionId         ID of the object's type (correct version).
 * @param   @ao_typeName           Name of the object's type.
 * @param   @ao_name               Name of the object itself.
 * @param   @ao_containerId        ID of the object's container.
 * @param   @ao_containerName      Name of the object's container.
 * @param   @ao_containerKind      Kind of object/container relationship.
 * @param   @ao_isLink             Is the object a link?
 * @param   @ao_linkedObjectId     Link if isLink is true.
 * @param   @ao_owner              ID of the owner of the object.
 * @param   @ao_creationDate       Date when the object was created.
 * @param   @ao_creator            ID of person who created the object.
 * @param   @ao_lastChanged        Date of the last change of the object.
 * @param   @ao_changer            ID of person who did the last change to the
 *                                 object.
 * @param   @ao_validUntil         Date until which the object is valid.
 * @param   @ao_description        Description of the object.
 * @param   @ao_showInNews         flag if object should be shown in newscontainer
 * @param   @ao_checkedOut         Is the object checked out?
 * @param   @ao_checkOutDate       Date when the object was checked out
 * @param   @ao_checkOutUser       id of the user which checked out the object
 * @param   @ao_checkOutUserOid    Oid of the user which checked out the object
 *                                 is only set if this user has the right to READ
 *                                 the checkOut user
 * @param   @ao_checkOutUserName   name of the user which checked out the object,
 *                                 is only set if this user has the right to view
 *                                 the checkOut-User
 *
 * @param   @ao_queryType          integer for querytype
 * @param   @ao_groupByString      string for groupby clause
 * @param   @ao_orderByString      string for orderby clause
 * @param   @ao_resultCounter      integer number of results to be shown
 * @param   @ao_enableDebug        flag if debug should be shown
 * @param   @ao_category           category to enable grouping
 *
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */

-- delete existing procedure:
EXEC p_dropProc N'p_QueryCreator_01$retrieve'
GO
-- create the new procedure:
CREATE PROCEDURE p_QueryCreator_01$retrieve
(
    -- common input parameters:
    @ai_oid_s           OBJECTIDSTRING,
    @ai_userId          USERID,
    @ai_op              INT,

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
    @ao_queryType      INTEGER         OUTPUT,
    @ao_groupByString  DESCRIPTION     OUTPUT,
    @ao_orderByString  DESCRIPTION     OUTPUT,
    @ao_resultCounter  INTEGER         OUTPUT,
    @ao_enableDebug    BOOL            OUTPUT,
    @ao_category       NAME			   OUTPUT

/*
    -- FOLLOWING TEXT-FIELDS ARE NOT POSIBBLE

    @ao_selectString    DESCRIPTION     OUTPUT,
    @ao_fromString      DESCRIPTION     OUTPUT,
    @ao_whereString     DESCRIPTION     OUTPUT,
    @ao_columnHeaders   DESCRIPTION     OUTPUT,
    @ao_queryAttributesForHeaders       DESCRIPTION     OUTPUT,
    @ao_queryAttributesTypesForHeaders  DESCRIPTION     OUTPUT,
    @ao_searchFieldTokens               DESCRIPTION     OUTPUT,
    @ao_queryAttributesForFields        DESCRIPTION     OUTPUT
    @ao_queryAttributesTypesForFields   DESCRIPTION     OUTPUT

*/
)
AS
    -- definitions:
    -- define return constants:
    DECLARE @c_NOT_OK INT,
            @c_ALL_RIGHT INT,
            @c_INSUFFICIENT_RIGHTS INT,
            @c_ALREADY_EXISTS INT

    -- set constants:
    SELECT  @c_NOT_OK = 0,
            @c_ALL_RIGHT = 1,
            @c_INSUFFICIENT_RIGHTS = 2,
            @c_ALREADY_EXISTS = 21

    -- define return values:
    DECLARE @l_retValue INT                   -- return value of this procedure
    -- initialize return values:
    SELECT  @l_retValue = @c_NOT_OK
    -- define local variables:
    DECLARE @l_oid OBJECTID
    -- initialize local variables:
    SELECT  @l_oid = 0x0000000000000000

    -- body:
    BEGIN TRANSACTION
        -- retrieve the base object data:
        EXEC @l_retValue = p_Object$performRetrieve
                @ai_oid_s, @ai_userId, @ai_op,
                @ao_state OUTPUT, @ao_tVersionId OUTPUT, @ao_typeName OUTPUT,
                @ao_name OUTPUT, @ao_containerId OUTPUT, @ao_containerName OUTPUT,
                @ao_containerKind OUTPUT, @ao_isLink OUTPUT, @ao_linkedObjectId OUTPUT,
                @ao_owner OUTPUT, @ao_ownerName OUTPUT,
                @ao_creationDate OUTPUT, @ao_creator OUTPUT, @ao_creatorName OUTPUT,
                @ao_lastChanged OUTPUT, @ao_changer OUTPUT, @ao_changerName OUTPUT,
                @ao_validUntil OUTPUT, @ao_description OUTPUT, @ao_showInNews OUTPUT,
                @ao_checkedOut OUTPUT, @ao_checkOutDate OUTPUT,
                @ao_checkOutUser OUTPUT, @ao_checkOutUserOid OUTPUT, 
                @ao_checkOutUserName OUTPUT,
                @l_oid OUTPUT

        IF (@l_retValue = @c_ALL_RIGHT)     -- object created successfully?
        BEGIN
            SELECT  @ao_queryType     = queryType,
                    @ao_groupByString = groupByString,
                    @ao_orderByString = orderByString,
                    @ao_resultCounter = resultCounter,
                    @ao_enableDebug   = enableDebug,
                    @ao_category      = category
            FROM    ibs_QueryCreator_01
            WHERE   oid = @l_oid

        END

    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_QueryCreator_01$retrieve


/******************************************************************************
 * Copy an object and all its values. <BR>
 *
 * @input parameters:
 * @param   @ai_oid                ID of the object to be copy.
 * @param   @ai_userId             ID of the user who is copying the object.
 * @param   @ai_newOid             ID of the copy of the object.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */

EXEC p_dropProc N'p_QueryCreator_01$BOCopy'
GO
-- create the new procedure:
CREATE PROCEDURE p_QueryCreator_01$BOCopy
(
    -- common input parameters:
    @ai_oid            OBJECTID,
    @ai_userId         USERID,
    @ai_newOid         OBJECTID
)
AS
    -- definitions:
    -- define return constants:
    DECLARE @c_NOT_OK INT,
            @c_ALL_RIGHT INT,
            @c_INSUFFICIENT_RIGHTS INT,
            @c_ALREADY_EXISTS INT

    -- set constants:
    SELECT  @c_NOT_OK = 0,
            @c_ALL_RIGHT = 1,
            @c_INSUFFICIENT_RIGHTS = 2,
            @c_ALREADY_EXISTS = 21

    -- define return values:
    DECLARE @l_retValue INT                   -- return value of this procedure
    -- initialize return values:
    SELECT  @l_retValue = @c_NOT_OK

    -- copy all values of querycreator
    INSERT INTO ibs_QueryCreator_01 (oid, selectString, fromString,
                whereString, queryType, groupByString, orderByString,
                columnHeaders, queryAttrTypesForHeaders,
                queryAttrForHeaders, searchFieldTokens,
                queryAttrForFields, queryAttrTypesForFields, resultCounter, 
                enableDebug, category)
    SELECT      @ai_newOid, selectString, fromString,
                whereString, queryType, groupByString, orderByString,
                columnHeaders, queryAttrTypesForHeaders,
                queryAttrForHeaders, searchFieldTokens,
                queryAttrForFields, queryAttrTypesForFields, resultCounter, 
                enableDebug, category
    FROM        ibs_QueryCreator_01
    WHERE       oid = @ai_oid

    -- check if insert was performed correctly:
    IF (@@ROWCOUNT >= 1)                -- at least one row affected?
        SELECT  @l_retValue = @c_ALL_RIGHT  -- set return value

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_QueryCreator_01$BOCopy