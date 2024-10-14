/******************************************************************************
 * Creates a Catalog Object.
 *
 * @version     $Id: Catalog_01Proc.sql,v 1.12 2009/12/02 18:35:03 rburgermann Exp $
 *
 * @author      Rupert Thurner   (RT)  980521
 *
 * @returns
 *  ALL_RIGHT               action performed, values returned, everything ok
 *  INSUFFICIENT_RIGHTS     user has no right to perform action
 ******************************************************************************
 */

EXEC p_dropProc N'p_Catalog_01$create'
GO

-- create the new procedure:
CREATE PROCEDURE p_Catalog_01$create

(
    -- input parameters:
    @userId         USERID,
    @op             INT,
    @tVersionId     TVERSIONID,
    @name           NAME,
    @containerId_s  OBJECTIDSTRING,
    @containerKind  INT,
    @isLink         BOOL,
    @linkedObjectId_s OBJECTIDSTRING,
    @description    DESCRIPTION,
    -- output parameters:
    @oid_s          OBJECTIDSTRING OUTPUT
)
AS
    ---------------------------------------------------------------------------
    -- CONVERTIONS (OBJECTIDSTRING) - all input objectids must be converted
    DECLARE @containerId    OBJECTID
    DECLARE @linkedObjectId OBJECTID
    DECLARE @contactsOid_s  OBJECTIDSTRING

    EXEC p_stringToByte @containerId_s, @containerId OUTPUT
    EXEC p_stringToByte @linkedObjectId_s, @linkedObjectId OUTPUT


    ---------------------------------------------------------------------------
    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT, @NOT_OK INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2, @NOT_OK = 0 -- return values
    -- define return values
    DECLARE @retValue INT                   -- return value of this procedure
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT

    ---------------------------------------------------------------------------
    -- START

    BEGIN TRANSACTION

        DECLARE @oid    OBJECTID
        
        EXEC @retValue = p_Object$performCreate @userId, @op, @tVersionId, @name, @containerId_s,
                @containerKind, @isLink, @linkedObjectId_s, @description, 
                @oid_s OUTPUT, @oid OUTPUT
        
	    IF @retValue = @ALL_RIGHT
        BEGIN
            -- Insert the other values - will be set to null
 	        INSERT INTO m2_Catalog_01 (oid, companyOid, ordResp,
 	                    ordRespMed, contResp, contRespMed, locked,
 	                    description1, description2, isorderexport, 
                            connectorOid, translatoroid, filterid,
                            -- special columns for datakom/mos
                            notifyByEmail, subject, content)
	        VALUES (@oid, NULL, NULL, NULL, NULL, NULL, 0, N' ', 
                        N' ', 0, NULL, NULL, 0, 0, NULL, NULL)
            -- check if insertion was performed properly:
            IF (@@ROWCOUNT <= 0)        -- no row affected?
            BEGIN
                SELECT  @retValue = @NOT_OK -- set return value
            END -- if no row affected
            
        END -- if object created successfully

    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue

GO

/******************************************************************************
 *
 * Changes a Catalog Object.
 *
 * @version     1.00.0001, 26.08.1998
 *
 * @author      Rupert Thurner   (RT)  980521
 *
 * @returns
 *  ALL_RIGHT               action performed, values returned, everything ok
 *  INSUFFICIENT_RIGHTS     user has no right to perform action
 * 
 * <DT><B>Updates:</B>
 * <DD>CM 980826    Added new attributes
 ******************************************************************************
 */

EXEC p_dropProc N'p_Catalog_01$change'
GO

-- create the new procedure:
CREATE PROCEDURE p_Catalog_01$change
(
    -- input parameters:
    @oid_s           OBJECTIDSTRING
    ,@userId         USERID
    ,@op             INT
    ,@name           NAME
    ,@validUntil     DATETIME
    ,@description    DESCRIPTION
    ,@showInNews     BOOL
    ---- attributes of object attachment ---------------
    ,@companyOid_s   OBJECTIDSTRING
    ,@ordResp_s     OBJECTIDSTRING      -- The responsibel User, Group or 
                                        -- Person for orderings
    ,@ordRespMed_s  OBJECTIDSTRING      -- The medium the ordering is done 
                                        -- through
    ,@contResp_s    OBJECTIDSTRING      -- The responsibel User, Group or
                                        -- Person for the contens of this 
                                        -- catalog
    ,@contRespMed_s OBJECTIDSTRING      -- The medium the contes responsible is
                                        -- reached
    ,@locked        BOOL
    ,@description1  DESCRIPTION
    ,@description2  DESCRIPTION
    ,@isOrderExport BOOL      
    ,@connectorOid_s  OBJECTIDSTRING  
    ,@translatorOid_s OBJECTIDSTRING  
    ,@filterId      INT               
    -- special parameters for datakom/mos
    ,@notifyByEmail BOOL
    ,@subject       DESCRIPTION
    ,@content       DESCRIPTION
)
AS
    ---------------------------------------------------------------------------
    -- CONVERTIONS (OBJECTIDSTRING) - all input object ids must be converted
    DECLARE @oid        OBJECTID, 
            @companyOid OBJECTID,
            @ordResp    OBJECTID,
            @ordRespMed OBJECTID,
            @contResp   OBJECTID,
            @contRespMed OBJECTID,
            @connectorOid OBJECTID,
            @translatorOid OBJECTID

    EXEC p_stringToByte @oid_s, @oid    OUTPUT
    EXEC p_stringToByte @companyOid_s, @companyOid      OUTPUT
    EXEC p_stringToByte @ordResp_s,     @ordResp        OUTPUT
    EXEC p_stringToByte @ordRespMed_s,  @ordRespMed     OUTPUT
    EXEC p_stringToByte @contResp_s,    @contResp       OUTPUT
    EXEC p_stringToByte @contRespMed_s, @contRespMed    OUTPUT
    EXEC p_stringToByte @connectorOid_s, @connectorOid  OUTPUT
    EXEC p_stringToByte @translatorOid_s, @translatorOid  OUTPUT

    ---------------------------------------------------------------------------
    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT
    -- set constants
    SELECT  @ALL_RIGHT = 1
    -- define return values
    DECLARE @retValue INT
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT

    ---------------------------------------------------------------------------
    -- START
    BEGIN TRANSACTION

        -- perform the change of the object:
        EXEC @retValue = p_Object$performChange @oid_s, @userId, @op, @name,
                @validUntil, @description, @showInNews

        -- operation properly performed?
        IF (@retValue = @ALL_RIGHT)
        BEGIN
            -- update object specific (Catalog_01) values
            UPDATE  m2_Catalog_01
	        SET     companyOid = @companyOid
                    ,ordResp = @ordResp
                    ,ordRespMed = @ordRespMed
                    ,contResp = @contResp
                    ,contRespMed = @contRespMed
                    ,locked = @locked
                    ,description1 = @description1
                    ,description2 = @description2
                    ,isOrderExport = @isOrderExport
                    ,connectorOid = @connectorOid
                    ,translatorOid = @translatorOid
                    ,filterId = @filterId
                    -- special columns for datakom/mos
                    ,notifyByEmail = @notifyByEmail
                    ,subject = @subject
                    ,content = @content
                WHERE oid=@oid
        END -- if operation properly performed

    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO


/******************************************************************************
 *
 * Retrieves a Catalog Object.
 *
 * @version     1.00.0004, 26.08.1998
 *
 * @author      Rupert Thurner   (RT)  980521
 *
 * @returns
 *  ALL_RIGHT               action performed, values returned, everything ok
 *  INSUFFICIENT_RIGHTS     user has no right to perform action
 * 
 * <DT><B>Updates:</B>
 * <DD>RT 980616    containerKind BOOL -> containerKind INT
 * <DD>RT 980610    same changes as in p_Object$retrieve
 *                  - name -> fullname
 *                  - join user -> left join user (outer join)
 * <DD>HP 970702    Changed attributes (changes in specification)
 * <DD>CM 980826    Added new attributes
 ******************************************************************************
 */

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
 * @param   @ordRespOid_s       oid of the ordering responsible 
 * @param   @ordRespName        name of the ordering responsibel
 * @param   @ordRespMedOid_s    oid of the medium for orderings
 * @param   @ordRespMedName     name of the medium for orderings
 * @param   @contRespOid_s      oid of the catalog responsible
 * @param   @contRespName       name of the catalog responsible
 * @param   @contRespMedOid_s   oid of the medium the the catalog responsible is reached
 * @param   @contRespMedName    name of the medium the the catalog responsible is reached
 * @param   @isOrderExport      flag if order export is activated
 * @param   @connectorOid_s     oid of the connector to be used for order export
 * @param   @translatorOid_s    oid of the translator to be used for order export 
 * @param   @filterId           id of the filter to be used
 *
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Catalog_01$retrieve'
GO

-- create the new procedure:
CREATE PROCEDURE p_Catalog_01$retrieve
(
    -- common input parameters:
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT,
    -- common output parameters:
    @state          STATE           OUTPUT,
    @tVersionId     TVERSIONID      OUTPUT,
    @typeName       NAME            OUTPUT,
    @name           NAME            OUTPUT,
    @containerId    OBJECTID        OUTPUT,
    @containerName  NAME            OUTPUT,
    @containerKind  INT             OUTPUT,
    @isLink         BOOL            OUTPUT,
    @linkedObjectId OBJECTID        OUTPUT,
    @owner          USERID          OUTPUT,
    @ownerName      NAME            OUTPUT,
    @creationDate   DATETIME        OUTPUT,
    @creator        USERID          OUTPUT,
    @creatorName    NAME            OUTPUT,
    @lastChanged    DATETIME        OUTPUT,
    @changer        USERID          OUTPUT,
    @changerName    NAME            OUTPUT,
    @validUntil     DATETIME        OUTPUT,
    @description    DESCRIPTION     OUTPUT,
    @showInNews     BOOL            OUTPUT,
    @checkedOut     BOOL            OUTPUT,
    @checkOutDate   DATETIME        OUTPUT,
    @checkOutUser   USERID          OUTPUT,
    @checkOutUserOid OBJECTID       OUTPUT,
    @checkOutUserName NAME          OUTPUT,
    -- type-specific output attributes:
    @companyOid     OBJECTID        OUTPUT,
    @company        NVARCHAR (126)  OUTPUT,
    @ordResp        OBJECTID        OUTPUT,  
    @ordRespName    NAME            OUTPUT,
    @ordRespMed     OBJECTID        OUTPUT,
    @ordRespMedName NAME            OUTPUT,
    @contResp       OBJECTID        OUTPUT,
    @contRespName   NAME            OUTPUT,
    @contRespMed    OBJECTID        OUTPUT,
    @contRespMedName NAME           OUTPUT,
    @locked         BOOL            OUTPUT,
    @description1   DESCRIPTION     OUTPUT,
    @description2   DESCRIPTION     OUTPUT,
    @isOrderExport  BOOL            OUTPUT,
    @connectorOid   OBJECTID        OUTPUT,
    @connectorName  NAME            OUTPUT,    
    @translatorOid  OBJECTID        OUTPUT,
    @translatorName NAME            OUTPUT,
    @filterId       INT             OUTPUT,
    @notifyByEmail  BOOL            OUTPUT,
    @subject        DESCRIPTION     OUTPUT,
    @content        DESCRIPTION     OUTPUT
)
AS
    -- definitions:
    -- define return constants:
    DECLARE @NOT_OK INT, @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT,
            @OBJECTNOTFOUND INT
    -- set constants:
    SELECT  @NOT_OK = 0, @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,
            @OBJECTNOTFOUND = 3
    -- define return values:
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values:
    SELECT  @retValue = @NOT_OK
    -- define local variables:
    DECLARE @oid            OBJECTID
	-- initialize local variables:

    -- body:
    BEGIN TRANSACTION
        -- retrieve the base object data:
        EXEC @retValue = p_Object$performRetrieve
                @oid_s, @userId, @op,
                @state OUTPUT, @tVersionId OUTPUT, @typeName OUTPUT,
                @name OUTPUT, @containerId OUTPUT, @containerName OUTPUT,
                @containerKind OUTPUT, @isLink OUTPUT, @linkedObjectId OUTPUT,
                @owner OUTPUT, @ownerName OUTPUT,
                @creationDate OUTPUT, @creator OUTPUT, @creatorName OUTPUT,
                @lastChanged OUTPUT, @changer OUTPUT, @changerName OUTPUT,
                @validUntil OUTPUT, @description OUTPUT, @showInNews OUTPUT, 
                @checkedOut OUTPUT, @checkOutDate OUTPUT, 
                @checkOutUser OUTPUT, @checkOutUserOid OUTPUT, @checkOutUserName OUTPUT, 
                @oid OUTPUT

        IF (@retValue = @ALL_RIGHT)     -- operation properly performed?
        BEGIN
            -- retrieve object type specific data:
	        SELECT  @companyOid = companyOid,
                    @ordResp = ordResp,
                    @ordRespMed = ordRespMed,
                    @contResp = contResp,
                    @contRespMed = contRespMed,
                    @locked = locked,
                    @description1 = description1,
                    @description2 = description2,
                    @isOrderExport = isOrderExport,
                    @connectorOid = connectorOid,
                    @translatorOid = translatorOid,
                    @filterId = filterId,
                    -- special columns for datakom/mos
                    @notifyByEmail = notifyByEmail,
                    @subject = subject,
                    @content = content
           FROM    m2_Catalog_01
	        WHERE   oid =  @oid

            -- get company name
            SELECT  @company = o.name + N' ' + c.legal_form
            FROM    mad_Company_01 c
            INNER JOIN ibs_Object o ON o.oid = c.oid
            WHERE   c.oid = @companyOid

            -- get order responsible name
            SELECT  @ordRespName = fullname
            FROM    ibs_User
            WHERE   oid = @ordResp
            -- get order responsible medium name
            SELECT  @ordRespMedName = name
            FROM    ibs_Object
            WHERE   oid = @ordRespMed
            -- get contens responsible nam
            SELECT  @contRespName = fullname
            FROM    ibs_User
            WHERE   oid = @contResp
            -- get contens responsible medium name
            SELECT  @contRespMedName = name
            FROM    ibs_Object
            WHERE   oid = @contRespMed
            -- get connector name
            SELECT  @connectorName = name
            FROM    ibs_Object
            WHERE   oid = @connectorOid
            -- get translator name
            SELECT  @translatorName = name
            FROM    ibs_Object
            WHERE   oid = @translatorOid

        END -- if operation properly performed
    COMMIT TRANSACTION
    -- return the state value
    RETURN  @retValue
GO


/******************************************************************************
 *
 * Deletes a Catalog Object.
 *
 * @version     1.00.0000, 21.05.1998
 *
 * @author      Rupert Thurner   (RT)  980521
 *
 * @returns
 *  ALL_RIGHT               action performed, values returned, everything ok
 *  INSUFFICIENT_RIGHTS     user has no right to perform action
 * 
 */
-- delete old procedure
EXEC p_dropProc N'p_Catalog_01$delete'
GO

CREATE PROCEDURE p_Catalog_01$delete
(
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT
)
AS
    ---------------------------------------------------------------------------
    -- CONVERTIONS (OBJECTIDSTRING) - all input objectids must be converted
    DECLARE @oid OBJECTID
    EXEC    p_stringToByte @oid_s, @oid OUTPUT

    ---------------------------------------------------------------------------
    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT
    -- set constants
    SELECT  @ALL_RIGHT = 1
    -- define return values
    DECLARE @retValue INT
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT
    -- participants container
    DECLARE @partContId OBJECTID


    ---------------------------------------------------------------------------
    -- START
    BEGIN TRANSACTION

        -- perform deletion of object:
        EXEC @retValue = p_Object$performDelete @oid_s, @userId, @op

        -- operation properly performed?
        IF (@retValue = @ALL_RIGHT)
        BEGIN
            -- delete object specific attributes
            DELETE  m2_Catalog_01 
            WHERE   oid = @oid
        END

    COMMIT TRANSACTION
    -- return the state value
    RETURN  @retValue

GO
