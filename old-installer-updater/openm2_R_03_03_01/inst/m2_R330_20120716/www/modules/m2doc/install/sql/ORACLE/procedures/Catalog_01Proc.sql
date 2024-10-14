/******************************************************************************
 * All stored procedures regarding the m2_Catalog_01 table. <BR>
 * 
 * @version     $Id: Catalog_01Proc.sql,v 1.12 2003/10/31 00:13:13 klaus Exp $
 *
 * @author      Mario Stegbauer (MS)  990507
 ******************************************************************************
 */

/******************************************************************************
 * Creates a Catalog Object.
 *
 * @returns
 *  ALL_RIGHT               action performed, values returned, everything ok
 *  INSUFFICIENT_RIGHTS     user has no right to perform action
 *
 */

-- create the new procedure:
CREATE OR REPLACE FUNCTION p_Catalog_01$create
(
    -- input parameters:
    ai_userid           INTEGER,
    ai_op               INTEGER,
    ai_tVersionId       INTEGER,
    ai_name             VARCHAR2,
    ai_containerId_s    VARCHAR2,
    ai_containerKind    INTEGER,
    ai_isLink           NUMBER,
    ai_linkedObjectId_s VARCHAR2,
    ai_description      VARCHAR2,
    -- output parameters:
    ao_oid_s            OUT   VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_NOOID_S           CONSTANT VARCHAR2 (18) := '0x0000000000000000';
    c_NOOID             CONSTANT RAW (8) := '0000000000000000';
    c_ALL_RIGHT             CONSTANT    INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT    INTEGER := 2;
    c_OBJECTNOTFOUND        CONSTANT    INTEGER := 3;
    c_NOT_OK                CONSTANT    INTEGER := 0;
    -- local variables:
    l_containerId       RAW(8);
    l_linkedObjectId    RAW(8);
    l_contactsOid_s     VARCHAR2(18);
    l_retValue          INTEGER := c_ALL_RIGHT;
    l_oid               RAW(8);
    l_tabTVersionId     INTEGER;
    l_tabName           VARCHAR2(63);
    l_tabDescription    VARCHAR2(255);
    l_partOfOid_s       VARCHAR2(18);
    l_id                INTEGER;
    l_counter           INTEGER := 0;

    
BEGIN
    p_stringToByte (ai_containerId_s, l_containerId);
    p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);

    l_retValue := p_Object$performCreate (
                    ai_userid, ai_op, 
                    ai_tVersionId, ai_name, 
                    ai_containerId_s, ai_containerKind, 
                    ai_isLink, ai_linkedObjectId_s, 
                    ai_description, ao_oid_s, l_oid);
        
    IF (l_retValue = c_ALL_RIGHT)
    THEN
        BEGIN-- Insert the other values - will be set to null
 	        INSERT INTO m2_Catalog_01 (oid, companyOid, ordResp,
 	                    ordRespMed, contResp, contRespMed, locked,
 	                    description1, description2, isorderexport, connectorOid,
                            translatoroid, filterid, notifyByEmail, subject, content)
	        VALUES (l_oid, c_NOOID, c_NOOID, 
	                c_NOOID, c_NOOID, c_NOOID, 0, 
	                ' ', ' ', 0, c_NOOID, 
	                c_NOOID, 0, 0, ' ', ' ');
	    EXCEPTION
	        WHEN OTHERS THEN
                    ibs_error.log_error ( ibs_error.error, 'p_Catalog_01$create',
                        'Error in get tab data for insert tupl into m2_Catalog_01');
	            RAISE;
	    END;    

        -- check if insertion was performed properly:
        IF (SQL%ROWCOUNT <= 0)        -- no row affected?
        THEN
            l_retValue := c_NOT_OK; -- set return value
        END IF;                     -- insertion performed properly
    END IF; -- if object created successfully
       

COMMIT WORK;

    -- return the state value
    RETURN  l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Catalog_01$create',
                          'userId: ' || ai_userId ||
                          ', op: ' || ai_op ||
                          ', tVersionId: ' || ai_tVersionId ||
                          ', name: ' || ai_name ||
                          ', containerId_s: ' || ai_containerId_s ||
                          ', containerKind: ' || ai_containerKind ||
                          ', isLink: ' || ai_IsLink ||
                          ', linkedObjectId_s: ' || ai_linkedObjectId_s ||
                          ', description: ' || ai_description ||
                          ', sqlcode: ' || sqlcode ||
                          ', sqlerrm: ' || sqlerrm );
    RETURN c_NOT_OK;
END p_Catalog_01$create;
/

show errors;

/******************************************************************************
 *
 * Changes a Catalog Object.
 *
 * @returns
 *  ALL_RIGHT               action performed, values returned, everything ok
 *  INSUFFICIENT_RIGHTS     user has no right to perform action
 *
 */

-- create the new procedure:
CREATE OR REPLACE FUNCTION p_Catalog_01$change
(
    -- input parameters:
    ai_oid_s            VARCHAR2,
    ai_userid           INTEGER,
    ai_op               INTEGER,
    ai_name             VARCHAR2,
    ai_validUntil       DATE,
    ai_description      VARCHAR2,
    ai_showInNews       INTEGER,
    ---- attributes of object attachment ---------------
    ai_companyOid_s     VARCHAR2,
    ai_ordResp_s        VARCHAR2,       -- The responsibel User, Group or 
                                        -- Person for orderings
    ai_ordRespMed_s     VARCHAR2,       -- The medium the ordering is done through
    ai_contResp_s       VARCHAR2,       -- The responsibel User, Group or
                                        -- Person for the contens of this 
                                        -- catalog
    ai_contRespMed_s    VARCHAR2,       -- The medium the contes responsible is reached
    ai_locked           NUMBER,
    ai_description1     VARCHAR2,
    ai_description2     VARCHAR2,
    ai_isOrderExport    NUMBER,
    ai_connectorOid_s   VARCHAR2,
    ai_translatorOid_s  VARCHAR2,
    ai_filterId         NUMBER,
    ai_notifyByEmail    NUMBER,
    ai_subject          VARCHAR2,
    ai_content          VARCHAR2

)
RETURN INTEGER
AS
    -- constants
    c_NOT_OK            CONSTANT        INTEGER := 0;
    c_ALL_RIGHT         CONSTANT        INTEGER := 1;
    -- local variables
    l_oid               RAW(8); 
    l_companyOid        RAW(8);
    l_ordResp           RAW(8);
    l_ordRespMed        RAW(8);
    l_contResp          RAW(8);
    l_contRespMed       RAW(8);
    l_connectorOid      RAW(8);
    l_translatorOid     RAW(8);
    l_retValue          INTEGER := c_ALL_RIGHT;

BEGIN
    p_stringToByte (ai_companyOid_s, l_companyOid);
    p_stringToByte (ai_ordResp_s, l_ordResp);
    p_stringToByte (ai_ordRespMed_s, l_ordRespMed);
    p_stringToByte (ai_contResp_s, l_contResp);
    p_stringToByte (ai_contRespMed_s, l_contRespMed);
    p_stringToByte (ai_connectorOid_s, l_connectorOid);
    p_stringToByte (ai_translatorOid_s, l_translatorOid);    

    l_retValue := p_Object$performChange (
                    ai_oid_s, ai_userid, ai_op, ai_name,
                    ai_validUntil, ai_description, ai_showInNews, l_oid);

    -- operation properly performed?
    IF (l_retValue = c_ALL_RIGHT)
    THEN
        -- update object specific (Catalog_01) values
        UPDATE  m2_Catalog_01
	    SET     companyOid = l_companyOid,
	            ordResp = l_ordResp,
                ordRespMed = l_ordRespMed,
                contResp = l_contResp,
                contRespMed = l_contRespMed,
                locked = ai_locked,
                description1 = ai_description1,
                description2 = ai_description2,
                isOrderExport = ai_isOrderExport,
                connectorOid = l_connectorOid,
                translatorOid = l_translatorOid,
                filterId = ai_filterId,
                notifyByEmail = ai_notifyByEmail,
                subject = ai_subject,
                content = ai_content
	    WHERE oid=l_oid;
    END IF; -- if operation properly performed

    COMMIT WORK;

    -- return the state value
    RETURN  l_retValue;
    
EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Catalog_01$change',
                          'oid_s: ' || ai_oid_s ||
                          ', userid: ' || ai_userid ||
                          ', op: ' || ai_op ||
                          ', name: ' || ai_name ||
                          ', validUntil: ' || ai_validUntil ||
                          ', description: ' || ai_description ||
                          ', companyOid_s: ' || ai_companyOid_s ||
                          ', ordResp_s: ' || ai_ordResp_s ||
                          ', ordRespMed_s: ' || ai_ordRespMed_s ||
                          ', contResp_s: ' || ai_contResp_s ||
                          ', contRespMed_s: ' || ai_contRespMed_s ||
                          ', locked: ' || ai_locked ||
                          ', description1: ' || ai_description1 ||
                          ', description2: ' || ai_description2 ||
                          ', sqlcode: ' || sqlcode ||
                          ', sqlerrm: ' || sqlerrm );

    RETURN c_NOT_OK;
END p_Catalog_01$change;
/

show errors;

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
 * <DD>MS 990505    converted procedure p_Catalog_01$retrieve to oracle
 ******************************************************************************
 */

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
 *
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */

-- create the new procedure:
CREATE OR REPLACE FUNCTION p_Catalog_01$retrieve
(
    -- common input parameters:
    ai_oid_s            VARCHAR2,
    ai_userid           INTEGER,
    ai_op               INTEGER,
    -- common output parameters:
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
    ao_checkedOut          OUT NUMBER,
    ao_checkOutDate        OUT DATE,
    ao_checkOutUser        OUT INTEGER,
    ao_checkOutUserOid     OUT RAW,
    ao_checkOutUserName    OUT VARCHAR2,
    -- type-specific output attributes:
    ao_companyOid       OUT     RAW,
    ao_company          OUT     VARCHAR2,
    ao_ordResp          OUT     RAW,  
    ao_ordRespName      OUT     VARCHAR2,
    ao_ordRespMed       OUT     RAW,
    ao_ordRespMedName   OUT     VARCHAR2,
    ao_contResp         OUT     RAW,
    ao_contRespName     OUT     VARCHAR2,
    ao_contRespMed      OUT     RAW,
    ao_contRespMedName  OUT     VARCHAR2,
    ao_locked           OUT     NUMBER,
    ao_description1     OUT     VARCHAR2,
    ao_description2     OUT     VARCHAR2,
    ao_isOrderExport    OUT     NUMBER,
    ao_connectorOid     OUT     RAW,
    ao_connectorName    OUT     VARCHAR2,    
    ao_translatorOid    OUT     RAW,
    ao_translatorName   OUT     VARCHAR2,
    ao_filterId         OUT     NUMBER,
    ao_notifyByEmail    OUT     NUMBER,
    ao_subject          OUT     VARCHAR2,
    ao_content          OUT     VARCHAR2
)
RETURN INTEGER
AS
    -- constants
    c_NOT_OK                CONSTANT        INTEGER := 0;
    c_ALL_RIGHT             CONSTANT        INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT        INTEGER := 2;
    c_OBJECTNOTFOUND        CONSTANT        INTEGER := 3;
    -- local variables
    l_retValue              INTEGER := c_NOT_OK;
    l_oid                   RAW(8);
    l_company               VARCHAR(255);
    l_legal_form            VARCHAR(255);

BEGIN
    l_retValue := p_Object$performRetrieve (
                        ai_oid_s, ai_userid, ai_op,
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
        -- retrieve object type specific data:
	    SELECT  companyOid, ordResp,
                ordRespMed, contResp,
                contRespMed, locked,
                description1, description2,
                isOrderExport, connectorOid, 
                translatorOid, filterId,
                notifyByEmail, subject,
                content
    	INTO    ao_companyOid, ao_ordResp,
    	        ao_ordRespMed, ao_contResp,
    	        ao_contRespMed, ao_locked,
    	        ao_description1, ao_description2,
    	        ao_isOrderExport, ao_connectorOid,
    	        ao_translatorOid, ao_filterId,
    	        ao_notifyByEmail, ao_subject,
                ao_content
        FROM    m2_Catalog_01
	    WHERE   oid =  l_oid;

        BEGIN
            -- get company name
            SELECT  o.name, c.legal_form
            INTO    ao_company, l_legal_form    
            FROM    mad_Company_01 c, ibs_Object o
            WHERE   c.oid = ao_companyOid
                AND o.oid = c.oid;
            l_company := ao_company ||' '|| l_legal_form;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                NULL; -- valid exception
        END;

        BEGIN
            -- get order responsible name
            SELECT  fullname
            INTO    ao_ordRespName
            FROM    ibs_User
            WHERE   oid = ao_ordResp;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                NULL; -- valid exception
        END;

        BEGIN
            -- get order responsible medium name
            SELECT  name
            INTO    ao_ordRespMedName
            FROM    ibs_Object
            WHERE   oid = ao_ordRespMed;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                NULL; -- valid exception
        END;

        BEGIN
            -- get contens responsible nam
            SELECT  fullname
            INTO    ao_contRespName
            FROM    ibs_User
            WHERE   oid = ao_contResp;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                NULL; -- valid exception
        END;

        BEGIN
            -- get contens responsible medium name
            SELECT  name
            INTO    ao_contRespMedName
            FROM    ibs_Object
            WHERE   oid = ao_contRespMed;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                NULL; -- valid exception
        END;

        BEGIN
            -- get connector name
            SELECT  name
            INTO    ao_connectorName
            FROM    ibs_Object
            WHERE   oid = ao_connectorOid;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                NULL; -- valid exception
        END;

        BEGIN
            -- get translator name
            SELECT  name
            INTO    ao_translatorName
            FROM    ibs_Object
            WHERE   oid = ao_translatorOid;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                NULL; -- valid exception
        END;

    END IF; -- if operation properly performed
    COMMIT WORK;
    
    -- return the state value
    RETURN  l_retValue;
EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Catalog_01$retrieve',
                          'oid_s: ' || ai_oid_s ||
                          ', userid: ' || ai_userid ||
                          ', op: ' || ai_op ||
                          ', sqlcode: ' || sqlcode ||
                          ', sqlerrm: ' || sqlerrm );
    RETURN c_NOT_OK;
END p_Catalog_01$retrieve;
/

show errors;

EXIT;