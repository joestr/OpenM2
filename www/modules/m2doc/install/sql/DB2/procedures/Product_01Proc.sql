--------------------------------------------------------------------------------
-- All stored procedures regarding the Product_01 table. <BR>
--
-- @version     $Id: Product_01Proc.sql,v 1.4 2003/10/31 00:12:51 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020901
--------------------------------------------------------------------------------


--------------------------------------------------------------------------------
-- Create a new object (incl. rights check). <BR>
--
-- @input parameters:
-- @param   @userId             ID of the user who is creating the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
-- @param   @tVersionId         Type of the new object.
-- @param   @name               Name of the object.
-- @param   @containerId_s      ID of the container where object shall be
--                              created in.
-- @param   @containerKind      Kind of object/container relationship
-- @param   @isLink             Defines if the object is a link
-- @param   @linkedObjectId_s   If the object is a link this is the ID of the
--                              where the link shows to.
-- @param   @description        Description of the object.
--
-- @output parameters:
-- @param   @oid_s              OID of the newly created object.
-- @return  A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Product_01$create');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Product_01$create
(
    -- common input parameters:
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_tVersionId       INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_containerId_s    VARCHAR (18),
    IN  ai_containerKind    INT,
    IN  ai_isLink           SMALLINT,
    IN  ai_linkedObjectId_s VARCHAR (18),
    IN  ai_description      VARCHAR (255),
    -- common output parameters:
    OUT ao_oid_s            VARCHAR (18)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- conversions (objectidstring) - all input objectids must be converted
    DECLARE l_containerId   CHAR (8) FOR BIT DATA;
    DECLARE l_linkedObjectId CHAR (8) FOR BIT DATA;

    -- constants:
    DECLARE c_NOT_OK        INT;
    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_INSUFFICIENT_RIGHTS INT;
    DECLARE c_ALREADY_EXISTS INT;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- locla variables:
    DECLARE l_retValue      INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    CALL IBSDEV1.p_stringToByte (ai_containerId_s, l_containerId);
    CALL IBSDEV1.p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);

    -- set constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_ALREADY_EXISTS    = 21;

    -- initialize local variables and return values:
    SET l_retValue          = c_NOT_OK;
    SET l_oid               = c_NOOID;
    SET ao_oid_s            = c_NOOID_s;

-- body:
    -- create base object:
    CALL IBSDEV1.p_Object$performCreate(ai_userId, ai_op, ai_tVersionId, ai_name,
        ai_containerId_s, ai_containerKind, ai_isLink, ai_linkedObjectId_s,
        ai_description, ao_oid_s, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
    IF l_retValue = c_ALL_RIGHT THEN 
        -- create object type specific data:
        INSERT INTO IBSDEV1.m2_Product_01
            (oid, productNo, ean, availableFrom,
            unitOfQty, packingUnit, thumbAsImage, thumbNail,
            image, stock, created, productDescription)
        VALUES  (l_oid, '', '', CURRENT TIMESTAMP,
            1, 'Stk.', 0, null,  null, '', 0, '');
       GET DIAGNOSTICS l_rowcount = ROW_COUNT;
    
        -- check if insertion was performed properly:
        IF l_rowcount <= 0 THEN 
            SET l_retValue = c_NOT_OK;
        END IF;
    END IF;
    COMMIT;
    -- return the state value:
    RETURN l_retValue;
END;
-- p_Product_01$create

--------------------------------------------------------------------------------
-- Changes the attributes of an existing product (incl. rights check). <BR>
-- Currently all property lists are transferred via this procedure for
-- performance reasons.
-- The maximum is currently 6.
--
-- @input parameters:
-- @param   @id                 ID of the object to be changed.
-- @param   @userId             ID of the user who is changing the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
-- @param   @name               Name of the object.
-- @param   @validUntil         Date until which the object is valid.
-- @param   @description        Description of the object.
-- @param   @showInNews         flag if object should be shown in newscontainer
--
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Product_01$change');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Product_01$change
(
    -- common input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_validUntil       TIMESTAMP,
    IN  ai_description      VARCHAR (255),
    IN  ai_showInNews       SMALLINT,
    IN  ai_productNo        VARCHAR (63),
    IN  ai_ean              VARCHAR (63),
    IN  ai_availableFrom    TIMESTAMP,
    IN  ai_unitOfQty        INT,
    IN  ai_packingUnit      VARCHAR (63),
    IN  ai_thumbAsImage     SMALLINT,
    IN  ai_thumbnail        VARCHAR (63),
    IN  ai_image            VARCHAR (63),
    IN  ai_path             VARCHAR (20),
    IN  ai_stock            VARCHAR (63),
    IN  ai_productDialogStep    INT,
    IN  ai_productProfileOid_s  VARCHAR (18),
    IN  ai_brandNameOid_s   VARCHAR (18),
    IN  ai_hasAssortment    INT
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    -- constants:
    DECLARE c_NOT_OK        INT;
    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_INSUFFICIENT_RIGHTS INT;
    DECLARE c_OBJECTNOTFOUND INT;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_retValue      INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_oldImage      VARCHAR (63);
    DECLARE l_oldThumb      VARCHAR (63);
    DECLARE l_productProfileOid CHAR (8) FOR BIT DATA;
    DECLARE l_brandNameOid  CHAR (8) FOR BIT DATA;
    DECLARE l_notSupported  INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- set constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_OBJECTNOTFOUND    = 3;

    -- initialize local variables and return values:
    SET l_retValue          = c_NOT_OK;
    SET l_notSupported      = 0;
    SET l_oid               = c_NOOID;

-- body:
    -- perform the change of the object:
    -- don't set the description when coming from the first
    -- dialog
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
    IF (ai_productDialogStep <> 3)
    THEN
        SELECT  description
        INTO    ai_description
        FROM    IBSDEV1.ibs_Object
        WHERE   oid = l_oid;
    END IF;

    CALL IBSDEV1.p_Object$performChange (ai_oid_s, ai_userId, ai_op, ai_name, ai_validUntil,
        ai_description, ai_showInNews, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF (l_retValue = c_ALL_RIGHT)
    THEN
        -- update object type specific data:
        -- update other values
        IF (ai_productDialogStep = 1)
        THEN
            -- convert the oids of the property lists
            CALL IBSDEV1.p_stringToByte (ai_productProfileOid_s,
                l_productProfileOid);
            INSERT INTO IBSDEV1.m2_ProductCodeValues_01
                    (productOid, categoryOid, predefinedCodeOid)
            SELECT  l_oid, categoryOid, c_NOOID
            FROM    IBSDEV1.m2_ProfileCategory_01
            WHERE   productProfileOid = l_productProfileOid;

            GET DIAGNOSTICS l_rowcount = ROW_COUNT;
            -- dont't use assortment if there are no codes
            -- or more than two
            IF (l_rowcount = 0 OR l_rowcount > 2)
            THEN
                SET l_notSupported = 1;
            END IF;

            -- delete not needed tabs:
            IF (ai_hasAssortment = 1)
            THEN
                -- delete tab prices:
                CALL IBSDEV1.p_Object$deleteTab (ai_userId, ai_op, l_oid,
                    'Prices');
            ELSE
                -- delete tab assortments:
                CALL IBSDEV1.p_Object$deleteTab (ai_userId, ai_op, l_oid,
                    'Assortments');
            END IF;

            -- update the product table:
            UPDATE  IBSDEV1.m2_Product_01
            SET     productProfileOid = l_productProfileOid,
                    hasAssortment = ai_hasAssortment
            WHERE   oid = l_oid;

            -- set state to st_created for next changeform (productDialogStep 2 or 3):
            CALL IBSDEV1.p_Object$changeState (ai_oid_s, ai_userId, ai_op, 4);
        ELSE
            IF (ai_productDialogStep = 2)
            THEN
                -- set state to st_created for next changeform (productDialogStep 3):
                CALL IBSDEV1.p_Object$changeState (ai_oid_s, ai_userId, ai_op, 4);
            ELSE
                IF (ai_productDialogStep = 3)
                THEN
                    -- don't change the images if null
                    SELECT  image, thumbnail
                    INTO    l_oldImage, l_oldThumb
                    FROM    IBSDEV1.m2_Product_01
                    WHERE   oid = l_oid;

                    IF (ai_image IS NULL)
                    THEN
                        SET ai_image = l_oldImage;
                    END IF;

                    IF (ai_thumbnail IS NULL)
                    THEN
                        SET ai_thumbnail = l_oldThumb;
                    END IF;

                    CALL IBSDEV1.p_stringToByte (ai_brandNameOid_s, l_brandNameOid);

                    -- change the property lists:
                    UPDATE  IBSDEV1.m2_Product_01
                    SET     productNo = ai_productNo,
                            ean = ai_ean,
                            availableFrom = ai_availableFrom,
                            unitOfQty = ai_unitOfQty,
                            packingUnit = ai_packingUnit,
                            thumbAsImage = ai_thumbAsImage,
                            thumbnail = ai_thumbnail,
                            image = ai_image,
                            path = ai_path,
                            stock = ai_stock,
                            created = 1,
                            brandNameOid = l_brandNameOid
                    WHERE   oid = l_oid;
                    GET DIAGNOSTICS l_rowcount = ROW_COUNT;
                END IF;
            END IF;
        END IF;
        IF (l_rowcount <= 0)
        THEN
            SET l_retValue = c_NOT_OK;
        END IF;
    END IF;

    COMMIT;

    -- return the state value:
    RETURN l_retValue;
END;
-- p_Product_01$change


--------------------------------------------------------------------------------
-- Gets all data from a given object (incl. rights check). <BR>
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be retrieved.
-- @param   @userId             Id of the user who is getting the data.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
--
-- @output parameters:
-- @param   @state              The object's state.
-- @param   @tVersionId         ID of the object's type (correct version).
-- @param   @typeName           Name of the object's type.
-- @param   @name               Name of the object itself.
-- @param   @containerId        ID of the object's container.
-- @param   @containerName      Name of the object's container.
-- @param   @containerKind      Kind of object/container relationship.
-- @param   @isLink             Is the object a link?
-- @param   @linkedObjectId     Link if isLink is true.
-- @param   @owner              ID of the owner of the object.
-- @param   @creationDate       Date when the object was created.
-- @param   @creator            ID of person who created the object.
-- @param   @lastChanged        Date of the last change of the object.
-- @param   @changer            ID of person who did the last change to the
--                              object.
-- @param   @validUntil         Date until which the object is valid.
-- @param   @description        Description of the object.
-- @param   @showInNews         flag if object should be shown in newscontainer
-- @param   @checkedOut         Is the object checked out?
-- @param   @checkOutDate       Date when the object was checked out
-- @param   @checkOutUser       id of the user which checked out the object
-- @param   @checkOutUserOid    Oid of the user which checked out the object
--                              is only set if this user has the right to READ
--                              the checkOut user
-- @param   @checkOutUserName   name of the user which checked out the object,
--                              is only set if this user has the right to view
--                              the checkOut-User
--
--
-- @param   @prop1              Description of the first property.
-- @param   @prop2              Description of the second property.
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Product_01$retrieve');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Product_01$retrieve(
    -- common input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    -- common output parameters:
    OUT ao_state            INT,
    OUT ao_tVersionId       INT,
    OUT ao_typeName         VARCHAR (63),
    OUT ao_name             VARCHAR (63),
    OUT ao_containerId      CHAR (8) FOR BIT DATA,
    OUT ao_containerName    VARCHAR (63),
    OUT ao_containerKind    INT,
    OUT ao_isLink           SMALLINT,
    OUT ao_linkedObjectId   CHAR (8) FOR BIT DATA,
    OUT ao_owner            INT,
    OUT ao_ownerName        VARCHAR (63),
    OUT ao_creationDate     TIMESTAMP,
    OUT ao_creator          INT,
    OUT ao_creatorName      VARCHAR (63),
    OUT ao_lastChanged      TIMESTAMP,
    OUT ao_changer          INT,
    OUT ao_changerName      VARCHAR (63),
    OUT ao_validUntil       TIMESTAMP,
    OUT ao_description      VARCHAR (255),
    OUT ao_showInNews       SMALLINT,
    OUT ao_checkedOut       SMALLINT,
    OUT ao_checkOutDate     TIMESTAMP,
    OUT ao_checkOutUser     INT,
    OUT ao_checkOutUserOid  CHAR (8) FOR BIT DATA,
    OUT ao_checkOutUserName VARCHAR (63),
    OUT ao_productNo        VARCHAR (63),
    OUT ao_ean              VARCHAR (63),
    OUT ao_availableFrom    TIMESTAMP,
    OUT ao_unitOfQty        INT,
    OUT ao_packingUnit      VARCHAR (63),
    OUT ao_thumbAsImage     SMALLINT,
    OUT ao_thumbnail        VARCHAR (63),
    OUT ao_image            VARCHAR (63),
    OUT ao_path             VARCHAR (20),
    OUT ao_stock            VARCHAR (63),
    OUT ao_priceCont_s      VARCHAR (18),
    OUT ao_productProfileOid_s  VARCHAR (18),
    OUT ao_hasAssortment    INT,
    OUT ao_created          INT,
    OUT ao_brandName        VARCHAR (63),
    OUT ao_brandNameOid_s   VARCHAR (18),
    OUT ao_brandImage       VARCHAR (63),
    OUT ao_collectionContainerOid_s VARCHAR (18)
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- definitions:
    -- define return constants:
    DECLARE c_NOT_OK        INT;
    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_INSUFFICIENT_RIGHTS INT;
    DECLARE c_OBJECTNOTFOUND INT;
    -- define return values:
    DECLARE l_retValue      INT;
    -- define local variables:
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    -- initialize local variables:
    DECLARE l_priceCont     CHAR (8) FOR BIT DATA;
    DECLARE l_pgThumb       VARCHAR (63);
    DECLARE l_pgImage       VARCHAR (63);
    DECLARE l_pgThumbAsImage SMALLINT;
    DECLARE l_productProfileOid CHAR (8) FOR BIT DATA;
    DECLARE l_brandNameOid  CHAR (8) FOR BIT DATA;
    -- get collection container id
    DECLARE l_collectionContainerOid CHAR (8) FOR BIT DATA;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
    -- set constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_OBJECTNOTFOUND    = 3;
  
    -- initialize return values:
    SET l_retValue          = c_NOT_OK;
-- body:
    -- retrieve the base object data:
    CALL IBSDEV1.p_Object$performRetrieve(ai_oid_s, ai_userId, ai_op, ao_state,
        ao_tVersionId, ao_typeName, ao_name, ao_containerId, ao_containerName,
        ao_containerKind, ao_isLink, ao_linkedObjectId, ao_owner, ao_ownerName,
        ao_creationDate, ao_creator, ao_creatorName, ao_lastChanged, 
        ao_changer, ao_changerName, ao_validUntil, ao_description,
        ao_showInNews, ao_checkedOut, ao_checkOutDate, ao_checkOutUser,
        ao_checkOutUserOid, ao_checkOutUserName, l_oid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;
  
    IF l_retValue = c_ALL_RIGHT THEN 
        -- retrieve object type specific data:
        -- get object specific data
        SELECT p.productNo, p.ean, p.availableFrom, p.unitOfQty, p.packingUnit,
            p.thumbAsImage, p.thumbnail, p.image, p.stock, p.hasAssortment,
            p.productProfileOid, p.created, o.name, p.brandNameOid, bn.image,
            p.path 
        INTO ao_productNo, ao_ean, ao_availableFrom, ao_unitOfQty,
            ao_packingUnit, ao_thumbAsImage, ao_thumbnail, ao_image, ao_stock,
            ao_hasAssortment, l_productProfileOid, ao_created, ao_brandName,
            l_brandNameOid, ao_brandImage, ao_path
        FROM IBSDEV1.m2_Product_01 p LEFT OUTER JOIN
            IBSDEV1.m2_ProductBrand_01 bn ON bn.oid = p.brandNameOid
            LEFT OUTER JOIN IBSDEV1.ibs_Object o ON bn.oid = o.oid
        WHERE p.oid = l_oid;
    
        CALL IBSDEV1.p_byteToString (l_brandNameOid, ao_brandNameOid_s);
    
        -- get the images from the product group
        SELECT pg.thumbAsImage, pg.image, pg.thumbnail 
        INTO l_pgThumbAsImage, l_pgImage, l_pgThumb
        FROM IBSDEV1.m2_ProductGroupProfile_01 pg INNER JOIN IBSDEV1.m2_ProductGroup_01 cpg
            ON cpg.productGroupProfileOid = pg.oid INNER JOIN IBSDEV1.ibs_Object o1
            ON o1.containerId = cpg.oid AND o1.oid = l_oid;
    
        -- if there is no image for this product get it from product group
        IF ao_image IS NULL THEN 
            SET ao_image = l_pgImage;
        END IF;
        -- if there is no thumbnail for this product get it
        -- from product group
        IF (ao_thumbnail IS NULL) AND ao_thumbAsImage = 0 THEN 
            IF l_pgThumbAsImage = 1 THEN 
                SET ao_thumbAsImage = 1;
            ELSE 
                SET ao_thumbnail = l_pgThumb;
            END IF;
        END IF;
        -- get price container id
        SELECT oid
         INTO l_priceCont
        FROM IBSDEV1.ibs_Object
        WHERE tversionid = 16851201
            AND containerId = l_oid;

        CALL IBSDEV1.p_byteToString (l_priceCont, ao_priceCont_s);

        SELECT oid
         INTO l_collectionContainerOid
        FROM IBSDEV1.ibs_Object
        WHERE tversionid = 16873217
               AND ao_containerId = l_oid;

        SELECT COUNT(*)
        INTO l_rowcount
        FROM IBSDEV1.ibs_Object
        WHERE tversionid = 16873217
            AND ao_containerId = l_oid;

        CALL IBSDEV1.p_byteToString (l_collectionContainerOid, ao_collectionContainerOid_s);
        -- check if retrieve was performed properly:
        IF l_rowcount <= 0 THEN 
            SET l_retValue = c_NOT_OK;
        END IF;
    END IF;
    COMMIT;
    -- return the state value:
    RETURN l_retValue;
END;
-- p_Product_01$retrieve

--------------------------------------------------------------------------------
-- Deletes an object and all its values (incl. rights check). <BR>
-- This procedure also delets all links showing to this object.
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be deleted.
-- @param   @userId             ID of the user who is deleting the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
-- @output parameters:
-- @returns A value representing the state of the procedure.
--  ALL_RIGHT               Action performed, values returned, everything ok.
--  INSUFFICIENT_RIGHTS     User has no right to perform action.
--  OBJECTNOTFOUND          The required object was not found within the
--                          database.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Product_01$delete');


-- create the new procedure:
-- CREATE PROCEDURE IBSDEV1.p_Product_01$delete
-- (
    -- common input parameters:
--     @oid_s          OBJECTIDSTRING,
--     @userId         USERID,
--     @op             INT
-- )
-- AS
    -- conversions (objectidstring) - all input objectids must be converted
--     DECLARE @oid OBJECTID
--     EXEC    p_stringToByte @oid_s, @oid OUTPUT

    -- definitions:
    -- define return constants:
--     DECLARE @NOT_OK INT, @ALL_RIGHT INT, @INSUFFICIENT_RIGHTS INT,
--             @OBJECTNOTFOUND INT
    -- set constants:
--     SELECT  @NOT_OK = 0, @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,
--             @OBJECTNOTFOUND = 3
    -- define return values:
--     DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values:
--     SELECT  @retValue = @NOT_OK
    -- define local variables:
    -- initialize local variables:

-- body:
--     BEGIN TRANSACTION
        -- delete base object:
--         EXEC @retValue = p_Object$performDelete @oid_s, @userId, @op,
--                 @oid OUTPUT

--         IF (@retValue = @ALL_RIGHT)     -- operation properly performed?
--         BEGIN
            -- delete object type specific data:
            -- (deletes all type specific tuples which are not within ibs_Object)
--             DELETE  m2_Product_01
--             WHERE   oid = @oid
            -- check if deletion was performed properly:
--             IF (;@ROWCOUNT <= 0)        -- no row affected?
--                 SELECT  @retValue = @NOT_OK -- set return value
--         END -- if operation properly performed
--     COMMIT TRANSACTION

    -- return the state value:
--     RETURN  @retValue
-- p_Product_01$delete


--------------------------------------------------------------------------------
-- Copy an object and all its values. <BR>
--
-- @input parameters:
-- @param   @oid_s              ID of the object to be deleted.
-- @param   @userId             ID of the user who is deleting the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--/
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Product_01$BOCopy');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Product_01$BOCopy(
    IN  ai_oid          CHAR (8) FOR BIT DATA,
    IN  ai_userId       INT,
    IN  ai_newOid       CHAR (8) FOR BIT DATA
    )
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;
    -- definitions:
    -- define return constants:
    DECLARE c_NOT_OK        INT;
    DECLARE c_ALL_RIGHT     INT;
    -- define return values:
    DECLARE l_retValue      INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
  
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;
    -- set constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    -- initialize return values:
    SET l_retValue          = c_NOT_OK;
    -- make an insert for all type specific tables:
    INSERT  INTO m2_Product_01
        (oid, productNo, ean, availableFrom, unitOfQty, packingUnit,
        thumbAsImage, thumbnail, image, stock, productProfileOid,
        hasAssortment, created, brandNameOid, path, productDescription )
    SELECT ai_newOid, productNo, ean, availableFrom, unitOfQty, packingUnit,
        thumbAsImage, thumbnail, image, stock, productProfileOid,
        hasAssortment, created, brandNameOid, path, productDescription
    FROM IBSDEV1.   m2_Product_01
    WHERE   oid = ai_oid;
    COMMIT;
    -- insert the code values of the product
    INSERT INTO IBSDEV1.m2_ProductCodeValues_01 (productOid, categoryOid,
        predefinedCodeOid, codeValues)
    SELECT      ai_newOid, categoryOid, predefinedCodeOid, codeValues
    FROM IBSDEV1.m2_ProductCodeValues_01
    WHERE       productOid = ai_oid;
    GET DIAGNOSTICS l_rowcount = ROW_COUNT;
    COMMIT;
    -- check if insert was performed correctly:
    IF l_rowcount >= 1 THEN 
        SET l_retValue = c_ALL_RIGHT;
    END IF;
    -- return the state value:
    RETURN l_retValue;
END;
-- p_Product_01$BOCopy

--------------------------------------------------------------------------------
-- Retrieve the price information
--
-- @input parameters:
-- @param   @oid_s              Object id string
-- @param   @userId             ID of the user who is deleting the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
--
-- @output parameters:
-- @returns     A result set with the price information
--              priceCurrency, price, cost, costCurrency, colors, sizes
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Product_01$retrievePrices');

-- AJ 990425 deleted ... //////////////////////////////////////////////////////

-- create the new procedure:
-- CREATE PROCEDURE IBSDEV1.p_Product_01$retrievePrices
-- (
    -- common input parameters:
--     @oid_s          OBJECTIDSTRING,
--     @userId         USERID,
--     @op             INT
-- )
-- AS
--     DECLARE @priceCont OBJECTID, @oid OBJECTID

    -- convert the oid string to OBJECTID
--     EXEC    p_StringToByte @oid_s, @oid OUTPUT

    -- get price-container id
--     SELECT  @priceCont = oid
--     FROM    ibs_Object
--     WHERE   tversionid  = 0x01012101        -- PriceContainer
--     AND     containerId = @oid              -- sub element of container

--     SELECT  priceCurrency, price, cost, costCurrency, userValue1, userValue2, values1, values2
--     FROM    v_Container$rights crr
--     JOIN    m2_Price_01 p
--     ON      crr.oid = p.oid
--     WHERE   crr.containerId = @priceCont
--     AND     (crr.rights & @op) > 0
-- AJ 990416 changed ... //////////////////////////////////////////////////////
--     AND     crr.userId = @userId
-- ... AJ 990416 changed ... //////////////////////////////////////////////////

--     AND     crr.uid = @userId

-- ... AJ 990416 changed //////////////////////////////////////////////////////
-- ... AJ 990425 deleted //////////////////////////////////////////////////////

--------------------------------------------------------------------------------
-- Puts the product with the price and color information in shopping cart of
-- the user
--
-- @input parameters:
-- @param   @oid_s              Object id string
-- @param   @userId             ID of the user who is deleting the object.
-- @param   @op                 Operation to be performed (used for rights
--                              check).
--
-- @output parameters:
-- @returns A value representing the state of the procedure.
--------------------------------------------------------------------------------
-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('p_Product_01$createCartEntry');

-- create the new procedure:
CREATE PROCEDURE IBSDEV1.p_Product_01$createCartEntry
(
    -- common input parameters:
    IN  ai_oid_s            VARCHAR (18),
    IN  ai_userId           INT,
    IN  ai_op               INT,
    IN  ai_name             VARCHAR (63),
    IN  ai_tVersionId       INT,
    IN  ai_state            INT,
    IN  ai_qty              INT,
    IN  ai_unitOfQty        INT,
    IN  ai_packingUnit      VARCHAR (63),
    IN  ai_productDescription   VARCHAR (255),
    IN  ai_price            DECIMAL(19,4),
    IN  ai_priceCurrency    VARCHAR (63)
)
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE         INT;

    -- constants:
    DECLARE c_NOT_OK        INT;
    DECLARE c_ALL_RIGHT     INT;
    DECLARE c_INSUFFICIENT_RIGHTS INT;
    DECLARE c_ALREADY_EXISTS INT;
    DECLARE c_NOOID         CHAR (8) FOR BIT DATA DEFAULT X'0000000000000000';
                                            -- default value for no defined oid
    DECLARE c_NOOID_s       VARCHAR (18) DEFAULT '0x0000000000000000';
                                            -- no oid as string

    -- local variables:
    DECLARE l_retValue      INT;
    DECLARE l_oid           CHAR (8) FOR BIT DATA;
    DECLARE l_priceCont     CHAR (8) FOR BIT DATA;
    DECLARE l_shoppingCartOid CHAR (8) FOR BIT DATA;
    DECLARE l_shoppingCartOid_s VARCHAR (18);
    DECLARE l_newOid_s      VARCHAR (18);
    DECLARE l_newOid        CHAR (8) FOR BIT DATA;
    DECLARE l_catalogOid    CHAR (8) FOR BIT DATA;
    DECLARE l_catalogName   VARCHAR (63);
    DECLARE l_orderRespOid  CHAR (8) FOR BIT DATA;
    DECLARE l_existsProductOid INT;
    DECLARE l_oldQty        INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_rowcount      INT;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
        SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

    -- set constants:
    SET c_NOT_OK            = 0;
    SET c_ALL_RIGHT         = 1;
    SET c_INSUFFICIENT_RIGHTS = 2;
    SET c_ALREADY_EXISTS    = 21;

    -- initialize local variables and return values:
    SET l_retValue          = c_NOT_OK;
    SET l_orderRespOid      = c_NOOID;
    SET l_existsProductOid  = 0;
    SET l_oldQty            = 0;
    SET l_newOid            = c_NOOID;
    SET l_newOid_s          = c_NOOID_s;
    SET l_oid               = c_NOOID;

-- body:
    -- convert the oid string to OBJECTID
    CALL IBSDEV1.p_stringToByte (ai_oid_s, l_oid);
    -- get the shopping cart
    SELECT shoppingCart
    INTO l_shoppingCartOid
    FROM IBSDEV1.ibs_Workspace
    WHERE userId = ai_userId;
  
    -- convert the oid
    CALL IBSDEV1.p_byteToString (l_shoppingCartOid, l_shoppingCartOid_s);

    -- create base object:
    CALL IBSDEV1.p_Object$performCreate(ai_userId, ai_op, ai_tVersionId, ai_name,
        l_shoppingCartOid_s, 1, 0, '0x00', ai_productDescription,
        l_newOid_s, l_newOid);
    GET DIAGNOSTICS l_retValue = RETURN_STATUS;

    IF l_retValue = c_ALL_RIGHT THEN 
        -- set the state to active:
        CALL IBSDEV1.p_Object$changeState(ai_oid_s, ai_userId, ai_op, 2);
        -- search for catalog where this product is from
        SELECT ocat.oid, ocat.name, cat.ordresp 
        INTO l_catalogOid, l_catalogName, l_orderRespOid
        FROM IBSDEV1.ibs_object prod, IBSDEV1.ibs_object prodgr, IBSDEV1.ibs_object ocat,
            IBSDEV1.m2_Catalog_01 cat
        WHERE prod.oid = l_oid
            AND prodgr.oid = prod.containerId
            AND ocat.oid = prodgr.containerId
            AND ocat.oid = cat.oid;

        SELECT COUNT(*) 
        INTO l_existsProductOid
        FROM IBSDEV1.ibs_Workspace ws, IBSDEV1.ibs_Object osce, IBSDEV1.m2_ShoppingCartEntry_01 sce
        WHERE ws.userId = ai_userId
            AND ws.shoppingCart = osce.containerId
            AND osce.state = 2
            AND sce.oid = osce.oid
            AND sce.productOid = l_oid;
    
        IF l_existsProductOid > 0 THEN 
            -- if there exists a productOid update the values - price and unit
            SELECT sce.qty, sce.oid 
            INTO l_oldQty, l_shoppingCartOid
            FROM IBSDEV1.ibs_Workspace ws, IBSDEV1.ibs_Object osce, IBSDEV1.m2_ShoppingCartEntry_01 sce
            WHERE ws.userId = ai_userId
                AND ws.shoppingCart = osce.containerId
                AND osce.state = 2
                AND sce.oid = osce.oid
                AND sce.productOid = l_oid;
            -- update the quantity
            UPDATE IBSDEV1.m2_ShoppingCartEntry_01
            SET qty = l_oldQty + ai_qty, price = ai_price
            WHERE productOid = l_oid
                AND oid = l_shoppingCartOid;
            GET DIAGNOSTICS l_rowcount = ROW_COUNT;
        ELSE 
            -- create object type specific data:
            INSERT INTO IBSDEV1.m2_ShoppingCartEntry_01
                (oid, qty, catalogOid, unitOfQty, packingUnit, productOid,
                productDescription,price, price2, price3, price4, price5,
                priceCurrency, orderType, ordResp, orderText)
            VALUES (l_newOid, ai_qty, l_catalogOid, ai_unitOfQty,
                ai_packingUnit, l_oid, ai_productDescription, ai_price, 0,
                0, 0, 0, ai_priceCurrency, 'Order', l_orderRespOid,
                l_catalogName);
            GET DIAGNOSTICS l_rowcount = ROW_COUNT;
        END IF;
        -- check if insertion was performed properly:
        IF l_rowcount <= 0 THEN 
            SET l_retValue = c_NOT_OK;
        END IF;
    END IF;
    COMMIT;
    -- return the state value:
    RETURN l_retValue;
END;
-- p_Product_01$createCartEntry
