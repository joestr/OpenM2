/******************************************************************************
 * All stored procedures regarding the XMLDiscussion_01 Object. <BR>
 *
 * @version     $Id: XMLDiscussion_01Proc.sql,v 1.4 2009/12/02 18:35:03 rburgermann Exp $
 *
 * @author      Keim Christine (Ck)  001009
 ******************************************************************************
 */

/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   oid_s                   ID of the object to be changed.
 * @param   userId                  ID of the user who is creating the object.
 * @param   op                      Operation to be performed (used for rights 
 *                                  check).
 * @param   name                    Name of the object.
 * @param   validUntil              Date until which the object is valid.
 * @param   description             Description of the object.
 * @param   showInNews              Show in news flag.
 * @param   refOid_s                Reference-oid of the template used.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT                       Action performed, values returned,
 *                                  everything ok.
 *  INSUFFICIENT_RIGHTS             User has no right to perform action.
 *  OBJECTNOTFOUND                  The required object was not found within
 *                                  the database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_XMLDiscussion_01$change'
GO

-- create the new procedure:
CREATE PROCEDURE p_XMLDiscussion_01$change
(
    -- input parameters:
    @oid_s                  OBJECTIDSTRING,
    @userId                 USERID,
    @op                     INT,
    @name                   NAME,
    @validUntil             DATETIME,
    @description            DESCRIPTION,
    @showInNews             BOOL,
    @maxlevels              INT,
    @defaultView            INT,
    @refOid_s               OBJECTIDSTRING
)
AS
    -- definitions:
    -- define return constants:
    DECLARE @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT, @OBJECTNOTFOUND INT
    -- set constants:
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2, @OBJECTNOTFOUND = 3
    -- define return values:
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values:
    SELECT  @retValue = @ALL_RIGHT
    -- define local variables:
    DECLARE @oid OBJECTID, @refOid OBJECTID
    -- converts
    EXEC p_stringToByte @oid_s, @oid OUTPUT
    EXEC p_stringToByte @refOid_s, @refOid OUTPUT

    -- body:
    BEGIN TRANSACTION
        -- perform the change of the object:
        EXEC @retValue = p_Discussion_01$change @oid_s, @userId, @op, @name,
                @validUntil, @description, @showInNews, @maxlevels, @defaultView

        IF (@retValue = @ALL_RIGHT)     -- operation properly performed?
        BEGIN
            -- update the other values
            UPDATE m2_Discussion_01
            SET    refOid = @refOid
            WHERE oid = @oid

        END
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @retValue
GO
-- p_XMLDiscussion_01$change


/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   oid_s              ID of the object to be changed.
 * @param   userId             ID of the user who is creating the object.
 * @param   op                 Operation to be performed (used for rights 
 *                              check).
 *
 * @output parameters:
 * @param   state              The object's state.
 * @param   tVersionId         ID of the object's type (correct version).
 * @param   typeName           Name of the object's type.
 * @param   name               Name of the object itself.
 * @param   containerId        ID of the object's container.
 * @param   containerKind      Kind of object/container relationship.
 * @param   isLink             Is the object a link?
 * @param   linkedObjectId     Link if isLink is true.
 * @param   owner              ID of the owner of the object.
 * @param   creationDate       Date when the object was created.
 * @param   creator            ID of person who created the object.
 * @param   lastChanged        Date of the last change of the object.
 * @param   changer            ID of person who did the last change to the 
 *                              object.
 * @param   validUntil         Date until which the object is valid.
 * @param   description        Description of the object.
 * @param   showInNews         show in news flag.
 * @param   checkedOut         Is the object checked out?
 * @param   checkOutDate       Date when the object was checked out
 * @param   checkOutUser       id of the user which checked out the object
 * @param   checkOutUserOid    Oid of the user which checked out the object
 *                              is only set if this user has the right to READ
 *                              the checkOut user
 * @param   checkOutUserName   name of the user which checked out the object,
 *                              is only set if this user has the right to view
 *                              the checkOut-User
 *
 * @param   maxlevels          Maximum of the levels allowed in the discussion
 * @param   defaultView        is always Standardview
 * @param   refOid             oid of the template used from the discussion
 * @param   refName            Name of the template used from the discussion
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_XMLDiscussion_01$retrieve'
GO

-- create the new procedure:
CREATE PROCEDURE p_XMLDiscussion_01$retrieve
(
    -- input parameters:
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT,
    -- output parameters
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
    @maxlevels      INT             OUTPUT,
    @defaultView    INT             OUTPUT,
    @refOid         OBJECTID        OUTPUT,
    @refName        NAME            OUTPUT
)
AS
    -- definitions:
    -- define return constants:
    DECLARE @ALL_RIGHT INT
    -- set constants:
    SELECT  @ALL_RIGHT = 1
    -- define return values:
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values:
    SELECT  @retValue = @ALL_RIGHT
    -- define local variables:
    DECLARE @oid OBJECTID

    -- conversions
    EXEC p_stringToByte @oid_s, @oid OUTPUT

    -- body:
    BEGIN TRANSACTION
        -- retrieve the base object data:
        EXEC @retValue = p_Discussion_01$retrieve
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
		@maxlevels OUTPUT, @defaultView OUTPUT

        IF (@retValue = @ALL_RIGHT)
        BEGIN
	    -- get refOid and refOid name
            SELECT @refOid = refOid
            FROM m2_Discussion_01
            WHERE oid=@oid 

	    SELECT @refName = name
	    FROM ibs_Object
	    WHERE oid=@refOid
        END
	
    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO
-- p_XMLDiscussion_01$retrieve



/******************************************************************************
 * Checks if there are templates to create a new XMLDiscussion
 * (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_op                   Operation to be performed (used for rights
 *                                  check).
 * @param   ai_userId               ID of the user who is deleting the object.
 *
 * @output parameters:
 * @param   ao_count                a counter of templates which the user is
 *                                  allowed to use.
 *
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_XMLDiscussion_01$checkTempl'
GO

-- create the new procedure:
CREATE PROCEDURE p_XMLDiscussion_01$checkTempl
(
    -- input parameters:
    @ai_userId              USERID,
    @ai_op                  INT,
    -- output parameters:
    @ao_count               INT OUTPUT
)
AS
DECLARE
    -- constants:
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    
    -- local variables:
    @l_retValue             ID              -- return value of this procedure

    -- assign constants:
SELECT
    @c_ALL_RIGHT            = 1

    -- initialize local variables and return values:
SELECT
    @l_retValue = @c_ALL_RIGHT

    -- body:
    -- gets all templates which can be used for Discussions
    SELECT  @ao_count = COUNT (*)
    FROM    v_Container$rights v
    WHERE   (v.rights & @ai_op) = @ai_op
      AND   v.userId = @ai_userId
      AND   v.tVersionId = 0x01010311 -- XMLDiscussionTemplate

    -- return the state value:
    RETURN  @l_retValue
GO
-- p_XMLDiscussion_01$checkTempl