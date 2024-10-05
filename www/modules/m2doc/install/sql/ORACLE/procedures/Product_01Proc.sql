/******************************************************************************
 * All stored procedures regarding the Product_01 table. <BR>
 *
 * @version     $Id: Product_01Proc.sql,v 1.13 2003/10/31 00:13:15 klaus Exp $
 *
 * @author      Andreas Jansa 05.05.1999
 ******************************************************************************
 */


/******************************************************************************
 * Create a new object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @userId                ID of the user who is creating the object.
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
 * @return A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */

-- create the new procedure:
CREATE OR REPLACE FUNCTION p_Product_01$create
(
    -- common input parameters:
    ai_userId           INTEGER,
    ai_op               INTEGER,
    ai_tVersionId       INTEGER,
    ai_name             VARCHAR2,
    ai_containerId_s    VARCHAR2,
    ai_containerKind    INTEGER,
    ai_isLink           NUMBER,
    ai_linkedObjectId_s VARCHAR2,
    ai_description      VARCHAR2,
    -- common output parameters:
    ao_oid_s            OUT VARCHAR2
)
RETURN INTEGER
AS

    -- define constants
    c_NOOID             CONSTANT RAW (8) := '0000000000000000';
    c_NOOID_S           CONSTANT VARCHAR2 (18) := '0x0000000000000000';
    -- define return constants
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3;
    c_ALREADY_EXISTS        CONSTANT INTEGER := 21;
    -- define return values
    l_retValue              INTEGER := c_NOT_OK;       -- return value of this procedure
    -- define local variables
    l_containerId           RAW (8) := c_NOOID;
    l_linkedObjectId        RAW (8) := c_NOOID;
    l_oid                   RAW (8) := c_NOOID;
    -- define local variables for tab
    l_tabTVersionId         INTEGER;
    l_tabName               VARCHAR2 (63);
    l_tabDescription        VARCHAR2 (255);
    l_partOfOid_s           VARCHAR2 (18);
    -- counter
    l_counter               INTEGER := 0;
BEGIN

    p_stringToByte (ai_containerId_s, l_containerId);
    p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);

    -- create base object:
    l_retValue := p_Object$performCreate (ai_userId, ai_op, ai_tVersionId,
                        ai_name, ai_containerId_s, ai_containerKind,
                        ai_isLink, ai_linkedObjectId_s, ai_description,
                        ao_oid_s, l_oid);

    IF (l_retValue = c_ALL_RIGHT)     -- object created successfully?
    THEN
        BEGIN
            -- create object type specific data:
            INSERT INTO m2_Product_01
                    (oid, productNo, ean, availableFrom,
                     unitOfQty, packingUnit, thumbAsImage, thumbNail,
                     image, stock, created, productDescription)
            VALUES  (l_oid, ' ', ' ', SYSDATE,
                     1, 'Stk.', 0, null,
                     null, ' ', 0, ' ');
        EXCEPTION
            WHEN OTHERS THEN
                    ibs_error.log_error ( ibs_error.error, 'p_Product_01$create',
                        'Error in create type specific data');
            RAISE;
        END;

        -- check if insertion was performed properly:
        IF (SQL%ROWCOUNT <= 0)        -- no row affected?
        THEN
            l_retValue := c_NOT_OK; -- set return value
        END IF; -- if no row affected
    END IF;-- if object created successfully
COMMIT WORK;
    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Product_01$create',
    ', userId = ' || ai_userId  ||
    ', tVersionId = ' || ai_tVersionId ||
    ', name = ' || ai_name ||
    ', containerId_s = ' || ai_containerId_s ||
    ', containerKind = ' || ai_containerKind ||
    ', isLink = ' || ai_isLink ||
    ', linkedObjectId_s = ' || ai_linkedObjectId_s ||
    ', description = ' || ai_description ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);
    RETURN c_NOT_OK;
END p_Product_01$create;
/

show errors;


/******************************************************************************
 * Changes the attributes of an existing product (incl. rights check). <BR>
 * Currently all property lists are transferred via this procedure for
 * performance reasons.
 * The maximum is currently 6.
 *
 * @input parameters:
 * @param   @id                 ID of the object to be changed.
 * @param   @userId                ID of the user who is changing the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 * @param   @name               Name of the object.
 * @param   @validUntil         Date until which the object is valid.
 * @param   @description        Description of the object.
 * @param   @showInNews         flag if object should be shown in newscontainer
 *
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_Product_01$change
(
    -- common input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_name                 VARCHAR2,
    ai_validUntil           DATE,
    ai_description          VARCHAR2,
    ai_showInNews           INTEGER,
    -- type-specific input parameters:
    ai_productNo	        VARCHAR2,
    ai_ean                  VARCHAR2,
    ai_availableFrom        DATE,
    ai_unitOfQty            INTEGER,
    ai_packingUnit          VARCHAR2,
    ai_thumbAsImage         NUMBER,
    ai_thumbnail	        VARCHAR2,
    ai_image                VARCHAR2,
    ai_path                 VARCHAR2,
    ai_stock                VARCHAR2,
    ai_productDialogStep    INTEGER,
    ai_productProfileOid_s  VARCHAR2,
    ai_brandNameOid_s       VARCHAR2,
    ai_hasAssortment        INTEGER
)
RETURN INTEGER
AS
    -- define constants
    c_NOOID                 CONSTANT RAW (8) := '0000000000000000';
    c_NOOID_S           CONSTANT VARCHAR2 (18) := '0x0000000000000000';
    -- define return constants
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3;
    -- define return values
    l_retValue              INTEGER := c_NOT_OK;       -- return value of this procedure
    -- define local variables
    l_oid                   RAW (8) := c_NOOID;
    l_oldImage              VARCHAR2 (63);
    l_oldThumb              VARCHAR2 (63);
    l_productProfileOid     RAW (8);
    l_brandNameOid          RAW (8);
    l_notSupported          INTEGER := 0;
    l_image                 VARCHAR2 (63) := ai_image;
    l_thumbnail             VARCHAR2 (63) := ai_thumbnail;
    -- define local variables for tab
    l_tabTVersionId         INTEGER;
    l_tabName               VARCHAR2 (63);
    l_tabDescription        VARCHAR2 (255);
    l_partOfOid_s           VARCHAR2 (18);
    l_tVersionId            INTEGER;
    l_description           VARCHAR2 (255);
    -- counter
    l_counter               INTEGER := 0;

BEGIN
        -- don't set the description when coming from the first
        -- dialog
        p_StringToByte (ai_oid_s, l_oid);

        IF (ai_productDialogStep <> 3)
        THEN
            BEGIN
                SELECT  description
                INTO    l_description
                FROM    ibs_Object
                WHERE   oid = l_oid;
            EXCEPTION
                WHEN OTHERS THEN
                    ibs_error.log_error ( ibs_error.error, 'p_Product_01$change',
                    'Error in save description');
                RAISE;
            END;
        END IF; -- if dialogstep <> 3

        l_retValue := p_Object$performChange (ai_oid_s, ai_userId,
                ai_op, ai_name, ai_validUntil, ai_description, ai_showInNews, l_oid);

        IF (l_retValue = c_ALL_RIGHT)     -- operation properly performed?
        THEN
            -- update object type specific data:
            -- update other values
            IF (ai_productDialogStep = 1)
            THEN
                -- convert the oids of the property lists
                p_StringToByte (ai_productProfileOid_s, l_productProfileOid);
                BEGIN
                    -- create code values
	                INSERT INTO m2_ProductCodeValues_01
	                            (productOid, categoryOid, predefinedCodeOid)
	                SELECT      l_oid, categoryOid, c_NOOID
	                FROM        m2_ProfileCategory_01
	                WHERE       productProfileOid = l_productProfileOid;
                EXCEPTION
                    WHEN OTHERS THEN
                        ibs_error.log_error ( ibs_error.error, 'p_Product_01$change',
                        'Error in insert code values');
                    RAISE;
                END;
	            -- dont't use assortment if there are no codes
	            -- or more than two
	            IF ((SQL%ROWCOUNT = 0) OR (SQL%ROWCOUNT > 2))
	            THEN
	                l_notSupported := 1;
                END IF; -- if number of codes is supported

                -- delete not needed tabs:
                IF (ai_hasAssortment = 1) -- product has assortments?
                THEN
                    -- delete tab prices:
                    l_retValue := p_Object$deleteTab (ai_userId, ai_op, l_oid, 'Prices');
                     -- if product has assortments
                ELSE                    -- product has prices
                    -- delete tab assortments:
                    l_retValue := p_Object$deleteTab (ai_userId, ai_op, l_oid, 'Assortments');

                END IF; -- else product has prices


                BEGIN
	                -- update the product table
                    UPDATE  m2_Product_01
	                SET     productProfileOid = l_productProfileOid,
	                        hasAssortment = ai_hasAssortment
	                WHERE   oid = l_oid;
	            EXCEPTION
	                WHEN OTHERS THEN
                        ibs_error.log_error ( ibs_error.error, 'p_Product_01$change',
                        'Error in update product table');
	                RAISE;
	            END;

                -- set state to st_created for next changeform (productDialogStep 2 or 3)
                l_retValue :=
                    p_Object$changeState (ai_oid_s, ai_userId, ai_op, 4);
            -- END if productDialogStep = 1
	        ELSIF (ai_productDialogStep = 2)
	        THEN
	            -- set state to st_created for next changeform (productDialogStep 3)
                l_retValue :=
                    p_Object$changeState (ai_oid_s, ai_userId, ai_op, 4);
	        -- END if productDialogStep = 2
            ELSIF (ai_productDialogStep = 3)
            THEN
                BEGIN
                    -- don't change the images if null
                    SELECT  image, thumbnail
                    INTO    l_oldImage, l_oldThumb
                    FROM    m2_Product_01
                    WHERE   oid = l_oid;
                EXCEPTION
                    WHEN OTHERS THEN
                        ibs_error.log_error ( ibs_error.error, 'p_Product_01$change',
                        'Error in get image and thumbnail');
                    RAISE;
                END;

                IF (l_image IS NULL)
                THEN
                    l_image := l_oldImage;
                END IF;
                IF (l_thumbnail IS NULL)
                THEN
                    l_thumbnail := l_oldThumb;
                END IF;

                p_StringToByte (ai_brandNameOid_s, l_brandNameOid);

                BEGIN
                    -- change the property lists
                    UPDATE  m2_Product_01
	                SET     productNo = ai_productNo
                            ,ean = ai_ean
                            ,availableFrom = ai_availableFrom
                            ,unitOfQty = ai_unitOfQty
                            ,packingUnit = ai_packingUnit
                            ,thumbAsImage = ai_thumbAsImage
                            ,thumbnail = l_thumbnail
                            ,image = l_image
                            ,path = ai_path
                            ,stock = ai_stock
                            ,created = 1
	                        ,brandNameOid = l_brandNameOid
                    WHERE   oid = l_oid;
                EXCEPTION
                    WHEN OTHERS THEN
                        ibs_error.log_error ( ibs_error.error, 'p_Product_01$change',
                        'Error in change property list');
                    RAISE;
                END;
            -- END productDialogSte = 3
            END IF;

            -- check if change was performed properly:
            IF (SQL%ROWCOUNT <= 0)        -- no row affected?
            THEN
                l_retValue := c_NOT_OK; -- set return value
            END IF;
        END IF;-- if operation properly performed
COMMIT WORK;
    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Product_01$change',
    ', oid_s = ' || ai_oid_s  ||
    ', userId = ' || ai_userId  ||
    ', op = ' || ai_op  ||
    ', name = ' || ai_name  ||
    ', validUntil = ' || ai_validUntil  ||
    ', description = ' || ai_description  ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);
    RETURN c_NOT_OK;
END p_Product_01$change;
/

show errors;


/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   ai_oid_s              ID of the object to be retrieved.
 * @param   ai_userId                Id of the user who is getting the data.
 * @param   ai_op                 Operation to be performed (used for rights
 *                              check).
 *
 * @output parameters:
 * @param   ao_state              The object's state.
 * @param   ao_tVersionId         ID of the object's type (correct version).
 * @param   ao_typeName           Name of the object's type.
 * @param   ao_name               Name of the object itself.
 * @param   ao_containerId        ID of the object's container.
 * @param   ao_containerName      Name of the object's container.
 * @param   ao_containerKind      Kind of object/container relationship.
 * @param   ao_isLink             Is the object a link?
 * @param   ao_linkedObjectId     Link if isLink is true.
 * @param   ao_owner              ID of the owner of the object.
 * @param   ao_creationDate       Date when the object was created.
 * @param   ao_creator            ID of person who created the object.
 * @param   ao_lastChanged        Date of the last change of the object.
 * @param   ao_changer            ID of person who did the last change to the
 *                              object.
 * @param   ao_validUntil         Date until which the object is valid.
 * @param   ao_description        Description of the object.
 * @param   ao_showInNews         flag if object should be shown in newscontainer
 * @param   ao_checkedOut         Is the object checked out?
 * @param   ao_checkOutDate       Date when the object was checked out
 * @param   ao_checkOutUser       id of the user which checked out the object
 * @param   ao_checkOutUserOid    Oid of the user which checked out the object
 *                              is only set if this user has the right to READ
 *                              the checkOut user
 * @param   ao_checkOutUserName   name of the user which checked out the object,
 *                              is only set if this user has the right to view
 *                              the checkOut-User
 *
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */


CREATE OR REPLACE FUNCTION p_Product_01$retrieve
(
    -- common input parameters
    ai_oid_s          VARCHAR2,
    ai_userId         INTEGER,
    ai_op             INTEGER,
    -- common output parameters
    ao_state          OUT      INTEGER,
    ao_tVersionId     OUT      INTEGER,
    ao_typeName       OUT      VARCHAR2,
    ao_name           OUT      VARCHAR2,
    ao_containerId    OUT      RAW,
    ao_containerName  OUT      VARCHAR2,
    ao_containerKind  OUT      INTEGER,
    ao_isLink         OUT      NUMBER,
    ao_linkedObjectId OUT      RAW,
    ao_owner          OUT      INTEGER,
    ao_ownerName      OUT      VARCHAR2,
    ao_creationDate   OUT      DATE,
    ao_creator        OUT      INTEGER,
    ao_creatorName    OUT      VARCHAR2,
    ao_lastChanged    OUT      DATE,
    ao_changer        OUT      INTEGER,
    ao_changerName    OUT      VARCHAR2,
    ao_validUntil     OUT      DATE,
    ao_description    OUT      VARCHAR2,
    ao_showInNews     OUT      INTEGER,
    ao_checkedOut          OUT NUMBER,
    ao_checkOutDate        OUT DATE,
    ao_checkOutUser        OUT INTEGER,
    ao_checkOutUserOid     OUT RAW,
    ao_checkOutUserName    OUT VARCHAR2,
    -- type-specific output attributes
    ao_productNo	  OUT      VARCHAR2,
    ao_ean            OUT      VARCHAR2,
    ao_availableFrom  OUT      DATE,
    ao_unitOfQty      OUT      INTEGER,
    ao_packingUnit    OUT      VARCHAR2,
    ao_thumbAsImage   OUT      NUMBER,
    ao_thumbnail	  OUT      VARCHAR2,
    ao_image          OUT      VARCHAR2,
    ao_path           OUT      VARCHAR2,
    ao_stock          OUT      VARCHAR2,
    ao_priceCont_s    OUT      VARCHAR2,
    -- 1
    ao_productProfileOid_s         OUT     VARCHAR2,
    ao_hasAssortment               OUT     INTEGER,
    ao_created                     OUT     INTEGER,
    ao_brandName                   OUT     VARCHAR2,
    ao_brandNameOid_s              OUT     VARCHAR2,
    ao_brandImage                  OUT     VARCHAR2,
    ao_collectionContainerOid_s    OUT     VARCHAR2
)
RETURN INTEGER
AS
    -- definitions:
    -- define return constants
    c_NOT_OK                    CONSTANT INTEGER := 0;
    c_ALL_RIGHT                 CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS       CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND            CONSTANT INTEGER := 3;
    -- define return values:
    l_retValue                  INTEGER := c_NOT_OK;
    l_oid                       RAW (8);
    -- initialize local variables
    l_priceCont                 RAW (8);
    l_pgThumb                   VARCHAR2 (255);
    l_pgImage                   VARCHAR2 (255);
    l_pgThumbAsImage            NUMBER (1);
    l_productProfileOid         RAW (8);
    l_brandNameOid              RAW (8);
    l_collectionContainerOid    RAW (8);

BEGIN

    l_retValue := p_Object$performRetrieve (
                ai_oid_s, ai_userId, ai_op,
                ao_state, ao_tVersionId, ao_typeName,
                ao_name, ao_containerId, ao_containerName,
                ao_containerKind, ao_isLink, ao_linkedObjectId,
                ao_owner, ao_ownerName,
                ao_creationDate, ao_creator, ao_creatorName,
                ao_lastChanged, ao_changer, ao_changerName,
                ao_validUntil, ao_description, ao_showInNews,
                ao_checkedOut, ao_checkOutDate,
                ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName,
                l_oid);

    IF (l_retValue = c_ALL_RIGHT)     -- operation properly performed?
    THEN
        -- retrieve object type specific data
        BEGIN
            -- get object specific data
	        SELECT  p.productNo, p.ean, p.availableFrom, p.unitOfQty,
                    p.packingUnit, p.thumbAsImage, p.thumbnail, p.image,
                    p.stock, p.hasAssortment, p.productProfileOid, p.created,
                    o.name, p.brandNameOid, bn.image, p.path
            INTO    ao_productNo, ao_ean, ao_availableFrom, ao_unitOfQty,
                    ao_packingUnit, ao_thumbAsImage, ao_thumbnail, ao_image,
                    ao_stock, ao_hasAssortment, l_productProfileOid, ao_created,
                    ao_brandName, l_brandNameOid, ao_brandImage, ao_path
    	    FROM    m2_Product_01 p, m2_ProductBrand_01 bn, ibs_Object o
	        WHERE   p.oid =  l_oid
	          AND   bn.oid(+) = p.brandNameOid
	          AND   bn.oid = o.oid(+);
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error, 'p_Product_01$retrieve',
                'Error in get object specific data');
            RAISE;
        END;

        p_ByteToString (l_brandNameOid , ao_brandNameOid_s);


        -- get the images from the product group
        BEGIN
            SELECT  pg.thumbAsImage, pg.image, pg.thumbnail
            INTO    l_pgThumbAsImage,
                    l_pgImage,
                    l_pgThumb
            FROM    m2_ProductGroupProfile_01 pg, m2_ProductGroup_01 cpg, ibs_Object o1
            WHERE   cpg.productGroupProfileOid = pg.oid
            AND     o1.containerId = cpg.oid
            AND     o1.oid = l_oid;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                NULL;   -- valid exception
            WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error, 'p_Product_01$retrieve',
                'Error in get images from product group');
            RAISE;
        END;

        -- if there is no image for this product get it from product group
        IF (ao_image IS NULL)
        THEN
            ao_image := l_pgImage;
        END IF;

        -- if there is no thumbnail for this product get it
        -- from product group
        IF ((ao_thumbnail IS NULL) AND (ao_thumbAsImage = 0))
        THEN
            IF (l_pgThumbAsImage = 1)
            THEN
                    ao_thumbAsImage := 1;
            ELSE
                    ao_thumbnail := l_pgThumb;
            END IF;
        END IF;


        -- get price container id
        BEGIN
            SELECT  oid
            INTO    l_priceCont
            FROM    ibs_Object
            WHERE   tversionid  =  16851201     -- productSizeColorContainer
            AND     containerId = l_oid;        -- sub element of container
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                NULL;   -- valid exception
            WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error, 'p_Product_01$retrieve',
                'Error in get price container id');
            RAISE;
        END;

        p_ByteToString (l_priceCont, ao_priceCont_s);

        -- get collection container id
        BEGIN
            SELECT  oid
            INTO    l_collectionContainerOid
            FROM    ibs_Object
            WHERE   tversionid  =  16873217     -- productCollectionContainer
            AND     containerId = l_oid;        -- sub element of container
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                NULL;   -- valid exception
            WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error, 'p_Product_01$retrieve',
                'Error in get collection container id');
            RAISE;
        END;

        p_ByteToString (l_collectionContainerOid , ao_collectionContainerOid_s);


        -- check if retrieve was performed properly:
        IF (SQL%ROWCOUNT <= 0)        -- no row affected?
        THEN
            l_retValue := c_NOT_OK; -- set return value
        END IF; -- if operation properly performed
    END IF; -- if performRetrieve properly performed

COMMIT WORK;
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Product_01$retrieve',
    ', oid_s = ' || ai_oid_s  ||
    ', userId = ' || ai_userId  ||
    ', op = ' || ai_op  ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);
    RETURN c_NOT_OK;
END p_Product_01$retrieve;
/

show errors;



/******************************************************************************
 * Get CLOB attribute (productDescription). <BR>
 *
 * @input parameters:
 * @param   ai_oid_s              ID of the object to be retrieved.
 *
 * @output parameters:
 * @param   ao_state              The object's state.
 *
 * @returns A value representing the state of the procedure.
 *  NOT_OK                  if any error occured
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *                          database.
 */
CREATE OR REPLACE function p_Product_01$getExtended
(
    ai_oid_s                VARCHAR2,
    ao_productDescription   OUT CLOB
)
RETURN INTEGER
AS
    -- constants
    c_NOT_OK                    CONSTANT INTEGER := 0;
    c_ALL_RIGHT                 CONSTANT INTEGER := 1;

    -- locals
    l_oid       RAW(8);
BEGIN
  p_stringToByte (ai_oid_s, l_oid);

  SELECT productDescription
  INTO   ao_productDescription
  FROM   m2_Product_01
  WHERE  oid = l_oid;

COMMIT WORK;
    RETURN c_ALL_RIGHT;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error(ibs_error.error, 'p_Product_01$getExtended',
                        'Input: ' || ai_oid_s ||
                        ', errorcode = ' || SQLCODE ||
                        ', errmsg = ' || SQLERRM );
    ao_productDescription := EMPTY_CLOB();
    RETURN c_NOT_OK;
END p_Product_01$getExtended;
/

show errors;




/******************************************************************************
 * Copy an object and all its values. <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be deleted.
 * @param   @userId                ID of the user who is deleting the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_Product_01$BOCopy
(
    -- common input parameters:
    ai_oid            RAW,
    ai_userId         INTEGER,
    ai_newOid         RAW
)
RETURN INTEGER
AS

    -- definitions:
    -- define return constants:
    c_NOT_OK                    CONSTANT INTEGER := 0;
    c_ALL_RIGHT                 CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS       CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND            CONSTANT INTEGER := 3;
    -- define return values:
    l_retValue                  INTEGER := c_NOT_OK;

BEGIN

    BEGIN
        -- make an insert for all type specific tables:
        INSERT  INTO m2_Product_01
                (oid, productNo, ean, availableFrom, unitOfQty, packingUnit,
                thumbAsImage, thumbnail, image, stock,
                productProfileOid, hasAssortment, created, brandNameOid, path,
                productDescription)
        SELECT  ai_newOid, productNo, ean, availableFrom, unitOfQty, packingUnit,
                thumbAsImage, thumbnail, image, stock,
                productProfileOid, hasAssortment, created, brandNameOid, path,
                productDescription
        FROM    m2_Product_01
        WHERE   oid = ai_oid;
    EXCEPTION
        WHEN OTHERS THEN
            ibs_error.log_error ( ibs_error.error, 'p_Product_01$BOCopy',
            'Error in INSERT INTO m2_Product_01');
        RAISE;
    END;

    -- insert the code values of the product
    BEGIN
        INSERT INTO m2_ProductCodeValues_01
                (productOid, categoryOid, predefinedCodeOid, codeValues)
        SELECT  ai_newOid, categoryOid, predefinedCodeOid, codeValues
        FROM    m2_ProductCodeValues_01
        WHERE   productOid = ai_oid;
    EXCEPTION
        WHEN OTHERS THEN
            ibs_error.log_error ( ibs_error.error, 'p_Product_01$BOCopy',
            'Error in INSERT INTO m2_ProductCodeValues_01');
        RAISE;
    END;

    -- check if insert was performed correctly:
    IF (SQL%ROWCOUNT >= 1)                -- at least one row affected?
    THEN
        l_retValue := c_ALL_RIGHT;  -- set return value
    END IF;
    -- return the state value:
    RETURN  l_retValue;
EXCEPTION
    WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Product_01$BOCopy',
    ', oid = ' || ai_oid  ||
    ', userId = ' || ai_userId  ||
    ', newOid = ' || ai_newOid  ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);
    RETURN c_NOT_OK;
END  p_Product_01$BOCopy;
/

show errors;

/******************************************************************************
 * Puts the product with the price and color information in shopping cart of
 * the user
 *
 * @input parameters:
 * @param   @oid_s              Object id string
 * @param   @userId                ID of the user who is deleting the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 */

-- create the new procedure:
CREATE OR REPLACE FUNCTION p_Product_01$createCartEntry
(
    -- common input parameters:
    ai_oid_s          VARCHAR2,
    ai_userId         INTEGER,
    ai_op             INTEGER,
    ai_name           VARCHAR2,
    ai_tVersionId     INTEGER,
    ai_state          INTEGER,                    -- not used anymore
    -- special input parameters
    ai_qty            INTEGER,
    ai_unitOfQty      INTEGER,
    ai_packingUnit    VARCHAR2,
    ai_productDescription VARCHAR2,
    ai_price          NUMBER,                   -- MONEY = NUMBER (19,4)
    ai_priceCurrency  VARCHAR2
)
RETURN INTEGER
AS

    -- define constants
    c_NOOID             CONSTANT RAW (8) := '0000000000000000';
    c_NOOID_s           CONSTANT VARCHAR2 (18) := '0x0000000000000000';
    -- define return constants
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3;
    c_ALREADY_EXISTS        CONSTANT INTEGER := 21;
    -- define return values
    l_retValue              INTEGER := c_NOT_OK;       -- return value of this procedure
    -- define local variables
    l_oid                   RAW (8) := c_NOOID;
    l_priceCont             RAW (8);
    l_shoppingCartOid       RAW (8);
    l_shoppingCartOid_s     VARCHAR2 (18);
    l_newOid_s              VARCHAR2 (18);
    l_newOid                RAW (8) := c_NOOID;
    l_catalogOid            RAW (8) := c_NOOID;
    l_catalogName           VARCHAR2 (63);
    l_orderRespOid          RAW (8) := c_NOOID;
    l_existsProductOid      INTEGER := 0;
    l_oldQty                INTEGER := 0;


BEGIN
    -- convert the oid string to OBJECTID
    p_StringToByte (ai_oid_s, l_oid);

    BEGIN
        -- get the shopping cart
        SELECT  shoppingCart
        INTO    l_shoppingCartOid
        FROM    ibs_Workspace
        WHERE   userId = ai_userId;
    EXCEPTION
        WHEN OTHERS THEN
            ibs_error.log_error ( ibs_error.error, 'p_Product_01$createCartEntry',
            'Error in get shoppingCart');
        RAISE;
    END;

    -- convert the oid
    p_ByteToString (l_shoppingCartOid, l_shoppingCartOid_s);

    -- body:
    -- create base object:
    l_retValue := p_Object$performCreate (ai_userId, ai_op, ai_tVersionId,
                        ai_name, l_shoppingCartOid_s, 1,
                        0, c_NOOID_s, ai_productDescription,
                        l_newOid_s, l_newOid);

    IF (l_retValue = c_ALL_RIGHT)     -- object created successfully?
    THEN
        -- set the state to active:
        l_retValue :=
            p_Object$changeState (ai_oid_s, ai_userId, ai_op, 2);

        -- search for catalog where this product is from
        BEGIN
            SELECT  ocat.oid, ocat.name, cat.ordresp
            INTO    l_catalogOid, l_catalogName, l_orderRespOid
            FROM    ibs_object prod, ibs_object prodgr, ibs_object ocat,
                    m2_Catalog_01 cat
            WHERE   prod.oid = l_oid
              AND   prodgr.oid = prod.containerId
              AND   ocat.oid = prodgr.containerId
              AND   ocat.oid = cat.oid;
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error, 'p_Product_01$createCartEntry',
                'Error in get catalog data');
            RAISE;
        END;

        -- check if this product does exists already in shopping cart of current user
        SELECT count (*)
        INTO l_existsProductOid
        FROM ibs_Workspace ws, ibs_Object osce, m2_ShoppingCartEntry_01 sce
        WHERE ws.userId = ai_userId
        AND ws.shoppingCart = osce.containerId
        AND osce.state = 2
        AND sce.oid = osce.oid
        AND sce.productOid = l_oid;

        IF (l_existsProductOid > 0) -- if there already exists the product oid
        THEN
            -- if there exists a productOid update the values - price and unit
            SELECT sce.qty, sce.oid
            INTO l_oldQty,l_shoppingCartOid
            FROM ibs_Workspace ws, ibs_Object osce, m2_ShoppingCartEntry_01 sce
            WHERE ws.userId = ai_userId
            AND ws.shoppingCart = osce.containerId
            AND osce.state = 2
            AND sce.oid = osce.oid
            AND sce.productOid = l_oid;

            UPDATE m2_ShoppingCartEntry_01
            SET qty = l_oldQty + ai_qty, price = ai_price
            WHERE productOid = l_oid
            AND oid = l_shoppingCartOid;

        ELSE
             -- create object type specific data:
            INSERT INTO m2_ShoppingCartEntry_01
                    (oid, qty, catalogOid, unitOfQty, packingUnit, productOid, productDescription
                    ,price, price2, price3, price4, price5, priceCurrency,
                    orderType, ordResp, orderText)
            VALUES (l_newOid, ai_qty, l_catalogOid, ai_unitOfQty, ai_packingUnit, l_oid, ai_productDescription
                    ,ai_price, 0.0, 0.0, 0.0, 0.0, ai_priceCurrency,
                   'Order', l_orderRespOid, l_catalogName);
        END IF;

        -- check if insertion was performed properly:
        IF (SQL%ROWCOUNT <= 0)        -- no row affected?
        THEN
            l_retValue := c_NOT_OK; -- set return value
        END IF; -- if no row affected
    END IF;-- if object created successfully
COMMIT WORK;
    -- return the state value:
    RETURN  l_retValue;
EXCEPTION
    WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Product_01$createCartEntry',
    ', oid_s = ' || ai_oid_s  ||
    ', userId = ' || ai_userId  ||
    ', op = ' || ai_op  ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);
    RETURN c_NOT_OK;
END p_Product_01$createCartEntry;
/

show errors;

EXIT;
