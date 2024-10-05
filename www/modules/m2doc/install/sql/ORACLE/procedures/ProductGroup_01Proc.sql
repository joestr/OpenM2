/******************************************************************************
 * All stored procedures regarding the m2_ProductGroup_01 table. <BR>
 *
 * @version     $Id: ProductGroup_01Proc.sql,v 1.5 2003/10/31 00:13:15 klaus Exp $
 *
 * @author      Mario Stegbauer (MS)  990507
 ******************************************************************************
 */


/******************************************************************************
 * Creates a ProductGroup_01 Object.
 *
 * @return
 * ALL_RIGHT               action performed, values returned, everything ok
 * INSUFFICIENT_RIGHTS     user has no right to perform action
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_ProductGroup_01$create

(
    -- input parameters:
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
    ao_oid_s                OUT             VARCHAR2
)
RETURN INTEGER
AS
    -- constants
    c_NOT_OK                 CONSTANT        INTEGER := 0;
    c_ALL_RIGHT              CONSTANT        INTEGER := 1;
    c_INSUFFICIENT_RIGHTS    CONSTANT        INTEGER := 2;
    -- local variables
    l_containerId           RAW(8);
    l_linkedObjectId        RAW(8);
    l_retValue              INTEGER;
    l_oid                   RAW(8);
    
BEGIN
    p_stringToByte (ai_containerId_s, l_containerId);
    p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);

    l_retValue := p_Object$performCreate (
                    ai_userid, ai_op, ai_tVersionId, ai_name, ai_containerId_s,
                    ai_containerKind, ai_isLink, ai_linkedObjectId_s, ai_description, 
                    ao_oid_s, l_oid);
        
	IF (l_retValue = c_ALL_RIGHT)
    THEN
            -- Insert the other values
 	        INSERT INTO m2_ProductGroup_01 (oid, productGroupProfileOid)
	        VALUES (l_oid, createOid (0, 0));
    END IF;    

    COMMIT WORK;
    RETURN  l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_ProductGroup_01$create',
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

END p_ProductGroup_01$create;
/

show errors;


/******************************************************************************
 *
 * ??Changes a ProductGroup_01 Object.
 *
 * @returns
 *  ALL_RIGHT               action performed, values returned, everything ok
 *  INSUFFICIENT_RIGHTS     user has no right to perform action
 * 
 * <DT><B>Updates:</B>
 * <DD> MS 009507    converted procedure to oracle
 */
CREATE OR REPLACE FUNCTION p_ProductGroup_01$change
(
    -- input parameters:
    ai_oid_s                            VARCHAR2,
    ai_userid                           INTEGER,
    ai_op                               INTEGER,
    ai_name                             VARCHAR2,
    ai_validUntil                       DATE,
    ai_description                      VARCHAR2,
    ai_showInNews                       INTEGER,
    ---- attributes of object attachment ---------------
    ai_productGroupProfileOid_s         VARCHAR2
)
RETURN INTEGER
AS
    -- constants
    c_NOT_OK            CONSTANT        INTEGER := 0;
    c_ALL_RIGHT         CONSTANT        INTEGER := 1;
    -- local variables
    l_oid                       RAW(8);
    l_productGroupProfileOid    RAW(8);
    l_retValue                  INTEGER := c_ALL_RIGHT;
    l_dummy                     RAW(8);

BEGIN

    p_stringToByte (ai_productGroupProfileOid_s, l_productGroupProfileOid);
    p_stringToByte (ai_oid_s, l_oid);

    -- perform the change of the object:
    l_retValue := p_Object$performChange (
                    ai_oid_s, ai_userid, ai_op, ai_name,
                    ai_validUntil, ai_description, ai_showInNews, l_dummy);

    -- operation properly performed?
    IF (l_retValue = c_ALL_RIGHT)
    THEN
        -- update other values
        UPDATE  m2_ProductGroup_01
	    SET     productGroupProfileOid = l_productGroupProfileOid
	    WHERE	oid=l_oid;
    END IF; -- if operation properly performed

    COMMIT WORK;
    RETURN  l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_ProductGroup_01$change',
                          ', oid_s: ' || ai_oid_s ||
                          ', userid: ' || ai_userid ||
                          ', op: ' || ai_op ||
                          ', name: ' || ai_name ||
                          ', validUntil: ' || ai_validUntil ||
                          ', description: ' || ai_description ||
                          ', productGroupProfileOid_s: ' || ai_productGroupProfileOid_s ||
                          ', sqlcode: ' || sqlcode ||
                          ', sqlerrm: ' || sqlerrm );
    RETURN c_NOT_OK;

END p_ProductGroup_01$change;
/

show errors;

/******************************************************************************
 *
 * ??Retrieves a ProductGroup_01 Object.
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
 * @returns
 *  ALL_RIGHT               action performed, values returned, everything ok
 *  INSUFFICIENT_RIGHTS     user has no right to perform action
 * 
 *******************************************************************************
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_ProductGroup_01$retrieve
(
    ai_oid_s        VARCHAR2,
    ai_userid       INTEGER,
    ai_op           INTEGER,
    -- output parameters
    ao_state                        OUT     INTEGER,
    ao_tVersionId                   OUT     INTEGER,
    ao_typeName                     OUT     VARCHAR2,
    ao_name                         OUT     VARCHAR2,
    ao_containerId                  OUT     RAW,
    ao_containerName                OUT     VARCHAR2,
    ao_containerKind                OUT     INTEGER,
    ao_isLink                       OUT     NUMBER,
    ao_linkedObjectId               OUT     RAW,
    ao_owner                        OUT     INTEGER,
    ao_ownerName                    OUT     VARCHAR2,
    ao_lastChanged                  OUT     DATE,
    ao_changer                      OUT     INTEGER,
    ao_changerName                  OUT     VARCHAR2,
    ao_creationDate                 OUT     DATE,
    ao_creator                      OUT     INTEGER,
    ao_creatorName                  OUT     VARCHAR2,
    ao_validUntil                   OUT     DATE,
    ao_description                  OUT     VARCHAR2,
    ao_showInNews                   OUT     INTEGER,
    ao_checkedOut                   OUT     NUMBER,
    ao_checkOutDate                 OUT     DATE,
    ao_checkOutUser                 OUT     INTEGER,
    ao_checkOutUserOid              OUT     RAW,
    ao_checkOutUserName             OUT     VARCHAR2,
    -- object specific attributes
    ao_productGroupProfileOid_s     OUT     VARCHAR2,
    ao_productGroupProfile          OUT     VARCHAR2
)
RETURN INTEGER
AS
    -- constants
    c_NOT_OK            CONSTANT        INTEGER := 0;
    c_ALL_RIGHT         CONSTANT        INTEGER := 1;
    -- local variables
    l_oid                       RAW(8);
    l_productGroupProfileOid    RAW(8);
    l_retValue                  INTEGER := c_ALL_RIGHT;
    l_dummy                     RAW(8);

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
	        SELECT  pg.oid, pgp.name
    	    INTO    l_productGroupProfileOid, ao_productGroupProfile
    	    FROM	m2_ProductGroup_01 pg, ibs_Object pgp
    	    WHERE   pg.oid = l_oid
    	        AND pg.productGroupProfileOid = pgp.oid;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                NULL;   -- valid exception
        END;
        
        p_byteToString (l_productGroupProfileOid, ao_productGroupProfileOid_s);
    END IF; -- if operation properly performed

    COMMIT WORK;
    RETURN  l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_ProductGroup_01$retrieve',
                          ', oid_s: ' || ai_oid_s ||
                          ', userid: ' || ai_userid ||
                          ', op: ' || ai_op ||
                          ', sqlcode: ' || sqlcode ||
                          ', sqlerrm: ' || sqlerrm );
    RETURN c_NOT_OK;

END p_ProductGroup_01$retrieve;
/

show errors;

EXIT;