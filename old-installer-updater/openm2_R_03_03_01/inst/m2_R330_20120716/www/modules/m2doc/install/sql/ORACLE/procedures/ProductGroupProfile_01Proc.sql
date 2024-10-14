/******************************************************************************
 * All stored procedures regarding the m2_ProductGroupProfile_01 table. <BR>
 *
 * @version     $Id: ProductGroupProfile_01Proc.sql,v 1.5 2003/10/31 00:13:15 klaus Exp $
 *
 * @author      Mario Stegbauer   (MS)  9905907
 *
 * @returns
 *  ALL_RIGHT               action performed, values returned, everything ok
 *  INSUFFICIENT_RIGHTS     user has no right to perform action
 ******************************************************************************
 */
 
/******************************************************************************
 * Creates a new object (incl. rights check). <BR>
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
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_ProductGrpProfile_01$create
(
    ai_userid               INTEGER,
    ai_op                   INTEGER,
    ai_tVersionId           INTEGER,
    ai_name                 VARCHAR2,
    ai_containerId_s        VARCHAR2,
    ai_containerKind        INTEGER,
    ai_isLink               NUMBER,
    ai_linkedObjectId_s     VARCHAR2,
    ai_description          VARCHAR2,
    -- output parameters:
    ao_oid_s                OUT         VARCHAR2
)
RETURN INTEGER
AS
    -- constants
    c_NOT_OK                    CONSTANT        INTEGER := 0;
    c_ALL_RIGHT                 CONSTANT        INTEGER := 1;
    c_INSUFFICIENT_RIGHTS       CONSTANT        INTEGER := 2;
    -- local variables
    l_containerId       RAW(8);
    l_linkedObjectId    RAW(8);
    l_retValue          INTEGER := c_ALL_RIGHT;
    l_oid               RAW(8);

BEGIN
    p_stringToByte (ai_containerId_s, l_containerId);
    p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);
    
    l_retValue := p_Object$performCreate (
                    ai_userid, ai_op, ai_tVersionId, ai_name, ai_containerId_s,
                    ai_containerKind, ai_isLink, ai_linkedObjectId_s, ai_description, 
                    ao_oid_s, l_oid);

	IF (l_retValue = c_ALL_RIGHT)
    THEN
        BEGIN
            -- Insert the other values
	        INSERT INTO m2_ProductGroupProfile_01 (oid, code, season, thumbnail, thumbAsImage, image)
    	    VALUES (l_oid, ' ', ' ', null, 0, null);

	    EXCEPTION
	        WHEN OTHERS THEN
                ibs_error.log_error(ibs_error.error, 'p_ProductGrpProfile_01$create','Error INSERT');	          
	        RAISE;
	    END;
    END IF;

    COMMIT WORK;
    RETURN  l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_ProductGrpProfile_01$create',
                          ', userid: ' || ai_userid ||
                          ', op: ' || ai_op ||
                          ', tVersionId: ' || ai_tVersionId ||
                          ', name: ' || ai_name ||
                          ', containerId_s: ' || ai_containerId_s ||
                          ', containerKind: ' || ai_containerKind ||
                          ', isLink: ' || ai_isLink ||
                          ', linkedObjectId_s: ' || ai_linkedObjectId_s ||
                          ', description: ' || ai_description ||
                          ', sqlcode: ' || sqlcode ||
                          ', sqlerrm: ' || sqlerrm );
    RETURN c_NOT_OK;
END p_ProductGrpProfile_01$create;
/

show errors;

/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
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
 * @param   @prop1              Description of the first type specific property.
 * @param   @prop2              Description of the second type specific
 *                              property.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 *
 * <DT><B>Updates:</B>
 * <DD> MS 990507   converted to oracle
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_ProductGrpProfile_01$change
(
    ai_oid_s            VARCHAR2,
    ai_userid           INTEGER,
    ai_op               INTEGER,
    ai_name             VARCHAR2,
    ai_validUntil       DATE,
    ai_description      VARCHAR2,
    ai_showInNews       INTEGER,
    ---- attributes of object attachment ---------------
    ai_code             VARCHAR2,
    ai_season           VARCHAR2,
    ai_thumbnail        VARCHAR2,
    ai_thumbAsImage     NUMBER,
    ai_image            VARCHAR2
)
RETURN INTEGER
AS
    -- constants
    c_NOT_OK                    CONSTANT        INTEGER := 0;
    c_ALL_RIGHT                 CONSTANT        INTEGER := 1;
    -- local variables
    l_oid               RAW(8);
    l_oldThumb          VARCHAR2(63);
    l_oldImage          VARCHAR2(63);
    l_retValue          INTEGER := c_ALL_RIGHT;
    l_thumbnail         VARCHAR2(63);
    l_image             VARCHAR2(63);
    l_dummy             RAW(8);
    
BEGIN
    p_stringToByte (ai_oid_s, l_oid);
    l_image := ai_image;
    l_thumbnail := ai_thumbnail;
    
    -- perform the change of the object:
    l_retValue := p_Object$performChange (
                    ai_oid_s, ai_userid, ai_op, ai_name,
                    ai_validUntil, ai_description, ai_showInNews, l_dummy);

    -- operation properly performed?
    IF (l_retValue = c_ALL_RIGHT)
    THEN
        SELECT  thumbnail, image
        INTO    l_oldThumb, l_oldImage
        FROM    m2_ProductGroupProfile_01
        WHERE   oid = l_oid;

        IF (l_thumbNail IS NULL)
        THEN
            l_thumbnail := l_oldThumb;
        END IF;
        IF (l_image IS NULL)
        THEN
            l_image := l_oldImage;
        END IF;     
        -- update other values
        BEGIN
            UPDATE  m2_ProductGroupProfile_01
	        SET     code = ai_code,
                    season = ai_season,
                    thumbnail = l_thumbnail,
                    thumbAsImage = ai_thumbAsImage,
                    image = l_image
	        WHERE   oid=l_oid;
	    EXCEPTION
	        WHEN OTHERS THEN
                ibs_error.log_error(ibs_error.error, 'p_ProductGrpProfile_01$change','Error INSERT');	          
	            RAISE;
        END;
    END IF;-- if operation properly performed

    COMMIT WORK;
    RETURN l_retValue;
    
EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_ProductGrpProfile_01$change',
                          ', oid_s: ' || ai_oid_s ||
                          ', userid: ' || ai_userid ||
                          ', op: ' || ai_op ||
                          ', name: ' || ai_name ||
                          ', validUntil: ' || ai_validUntil ||
                          ', description: ' || ai_description ||
                          ', code: ' || ai_code ||
                          ', season: ' || ai_season ||
                          ', thumbnail: ' || ai_thumbnail ||
                          ', thumbAsImage: ' || ai_thumbAsImage ||
                          ', image: ' || ai_image ||
                          ', sqlcode: ' || sqlcode ||
                          ', sqlerrm: ' || sqlerrm );
    RETURN c_NOT_OK;
END p_ProductGrpProfile_01$change;
/

show errors;

/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be retrieved.
 * @param   @userId                Id of the user who is getting the data.
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
 * @param   @prop1              Description of the first property.
 * @param   @prop2              Description of the second property.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_ProductGrpProfile_01$retrv
(
    ai_oid_s            VARCHAR2,
    ai_userid           INTEGER,
    ai_op               INTEGER,
    -- output parameters
    ao_state            OUT     INTEGER,
    ao_tVersionId       OUT     INTEGER,
    ao_typeName         OUT     VARCHAR2,
    ao_name             OUT     VARCHAR2,
    ao_containerId      OUT     RAW,
    ao_containerName    OUT     VARCHAR2,
    ao_containerKind    OUT     INTEGER,
    ao_isLink           OUT     NUMBER,
    ao_linkedObjectId   OUT     RAW,
    ao_owner            OUT     INTEGER,
    ao_ownerName        OUT     VARCHAR2,
    ao_creationDate     OUT     DATE,
    ao_creator          OUT     INTEGER,
    ao_creatorName      OUT     VARCHAR2,
    ao_lastChanged      OUT     DATE,
    ao_changer          OUT     INTEGER,
    ao_changerName      OUT     VARCHAR2,
    ao_validUntil       OUT     DATE,
    ao_description      OUT     VARCHAR2,
    ao_showInNews       OUT     INTEGER,
    ao_checkedOut                   OUT     NUMBER,
    ao_checkOutDate                 OUT     DATE,
    ao_checkOutUser                 OUT     INTEGER,
    ao_checkOutUserOid              OUT     RAW,
    ao_checkOutUserName             OUT     VARCHAR2,
    -----specific outputdata of ProductGroup--------------
    ao_code             OUT     VARCHAR2,
    ao_season           OUT     VARCHAR2,
    ao_thumbnail        OUT     VARCHAR2,
    ao_thumbAsImage     OUT     NUMBER,
    ao_image            OUT     VARCHAR2
)
RETURN INTEGER
AS
    -- constants
    c_NOT_OK                    CONSTANT        INTEGER := 0;
    c_ALL_RIGHT                 CONSTANT        INTEGER := 1;
    -- local variables
    l_oid           RAW(8);
    l_retValue      INTEGER := c_ALL_RIGHT;
    l_dummy         RAW(8);

BEGIN
    p_stringToByte (ai_oid_s, l_oid);

    l_retValue := p_Object$performRetrieve (
                    ai_oid_s, ai_userid, ai_op,
                    ao_state, ao_tVersionId, ao_typeName, ao_name,
                    ao_containerId, ao_containerName, ao_containerKind,
                    ao_isLink, ao_linkedObjectId, ao_owner, ao_ownerName,
                    ao_creationDate, ao_creator, ao_creatorName,
                    ao_lastChanged, ao_changer, ao_changerName,
                    ao_validUntil, ao_description, ao_showInNews, 
                    ao_checkedOut, ao_checkOutDate, 
                    ao_checkOutUser, ao_checkOutUserOid, ao_checkOutUserName,
                    l_dummy);

    -- operation properly performed?
    IF (l_retValue = c_ALL_RIGHT)
    THEN
	    BEGIN
	        SELECT code, season, thumbnail, thumbAsImage, image
    	    INTO ao_code, ao_season, ao_thumbnail, ao_thumbAsImage, ao_image
    	    FROM m2_ProductGroupProfile_01
	        WHERE oid =  l_oid;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                ibs_error.log_error(ibs_error.error, 'p_ProductGrpProfile_01$retrv','Error SELECT');	          
	            RAISE;
        END;
    END IF; -- if operation properly performed

    COMMIT WORK;
    RETURN  l_retValue;
    
EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_ProductGrpProfile_01$retrv',
                          ', oid_s: ' || ai_oid_s ||
                          ', userid: ' || ai_userid ||
                          ', op: ' || ai_op ||
                          ', sqlcode: ' || sqlcode ||
                          ', sqlerrm: ' || sqlerrm );
    RETURN c_NOT_OK;

END p_ProductGrpProfile_01$retrv;
/

show errors;

EXIT;
