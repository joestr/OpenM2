/******************************************************************************
 * All stored procedures regarding the Help_01 Object. <BR>
 *
 * @version     2.30.0010, 29.10.2002 KR
 *
 * @author      Mario Stegbauer (MS)  980805
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
CREATE OR REPLACE FUNCTION p_Help_01$create
(
    -- common input parameters:
    ai_userId            INTEGER,
    ai_op                INTEGER,
    ai_tVersionId        INTEGER,
    ai_name              VARCHAR2,
    ai_containerId_s     VARCHAR2,
    ai_containerKind     INTEGER,
    ai_isLink            NUMBER,
    ai_linkedObjectId_s  VARCHAR2,
    ai_description       VARCHAR2,
    -- common output parameters:
    ao_oid_s             OUT		 VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK 			CONSTANT INTEGER := 0;
    c_ALL_RIGHT 		CONSTANT INTEGER := 1; 
    c_INSUFFICIENT_RIGHTS 	CONSTANT INTEGER := 2;
    c_ALREADY_EXISTS 		CONSTANT INTEGER := 21;
    c_NOOID                 	CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    -- local variables:
    l_containerId       RAW(8);
    l_linkedObjectId    RAW(8);
    l_retValue 		INTEGER := c_NOT_OK;
    l_oid 		RAW(8) := c_NOOID;
    l_rights 		INTEGER;
    l_actRights 	INTEGER;

BEGIN
    p_stringToByte (ai_containerId_s, l_containerId);
    p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);

    l_retValue := p_Object$performCreate (
    			ai_userId, ai_op, ai_tVersionId, 
                        ai_name, ai_containerId_s, ai_containerKind, 
                        ai_isLink, ai_linkedObjectId_s, ai_description, 
                            ao_oid_s, l_oid);

    IF (l_retValue = c_ALL_RIGHT)     -- object created successfully?
    THEN
    	BEGIN
            -- create object type specific data:
            INSERT INTO ibs_Help_01 (oid, searchContent, goal, helpUrl)
            VALUES  (l_oid, '',  ai_description, '');
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error(ibs_error.error, 'p_Help_01$create','Error in insert');
                RAISE;
        END;
    END IF;
        
    COMMIT WORK;
    RETURN  l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Help_01$create',
    ', userId = ' || ai_userId  ||
    ', op = ' || ai_op  ||
    ', tVersionId = ' || ai_tVersionId ||
    ', name = ' || ai_name ||
    ', containerId_s = ' || ai_containerId_s ||
    ', containerKind = ' || ai_containerKind ||
    ', isLink = ' || ai_isLink ||
    ', linkedObjectId_s = ' || ai_linkedObjectId_s ||
    ', description = ' || ai_description ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);
    
END p_Help_01$create;
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
 * @param   @showInNews         the showInNews flag
 *
 * @param   @searchContent      topics related to actuell helpobject
 * @param   @helpUrl            Url to the attached Dokument.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Help_01$change
(
    -- common input parameters:
    ai_oid_s          VARCHAR2,
    ai_userId         INTEGER,
    ai_op             INTEGER,
    ai_name           VARCHAR2,
    ai_validUntil     DATE,
    ai_description    VARCHAR2,
    ai_showInNews     INTEGER,
    -- type-specific input parameters:
    ai_helpUrl          VARCHAR2,
    ai_searchContent    VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK 			CONSTANT INTEGER := 0;
    c_ALL_RIGHT 		CONSTANT INTEGER := 1; 
    c_INSUFFICIENT_RIGHTS 	CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND		CONSTANT INTEGER := 3;
    c_NOOID                 	CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    -- local variables:
    l_retValue 	INTEGER := c_NOT_OK;
    l_oid 	RAW(8);
BEGIN
    -- perform the change of the object:
    l_retValue := p_Object$performChange (
			        ai_oid_s, ai_userId, ai_op, ai_name,
                	ai_validUntil, ai_description, ai_showInNews,
                	l_oid);

    IF (l_retValue = c_ALL_RIGHT)     -- operation properly performed?
    THEN
        BEGIN
            -- update further information
	    UPDATE ibs_Help_01
	    SET    helpUrl = ai_helpUrl,
                   searchContent = ai_searchContent
	    WHERE  oid=l_oid;
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error(ibs_error.error, 'p_Help_01$change','Error in update');
                RAISE;
        END;
    END IF;
    
    COMMIT WORK;       
    RETURN  l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Help_01$change',
    ', oid_s = ' || ai_oid_s  ||
    ', userId = ' || ai_userId  ||
    ', op = ' || ai_op  ||
    ', name = ' || ai_name ||
    ', validUntil = ' || ai_validUntil ||
    ', description = ' || ai_description ||
    ', showInNews = ' || ai_showInNews ||
    ', helpUrl = ' || ai_helpUrl ||
    ', searchContent = ' || ai_searchContent ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);
END p_Help_01$change;
/

show errors;

/******************************************************************************
 * Creates a new object and sets all attributes of this object (incl. rights 
 * check). <BR>
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
 * @param   @validUntil         Date until which the object is valid.
 * @param   @description        Description of the object.
 * @param   @helpUrl            Url to the attached Dokument.
 * @param   @searchContent      topics related to actuell helpobject
 * @param   @goal               Goal of the help topic.
 *
 * @output parameters:
 * @param   @oid_s              OID of the newly created object.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
CREATE OR REPLACE FUNCTION p_Help_01$createFast
(
    -- common input parameters:
    userId              INTEGER,
    op                  INTEGER,
    tVersionId          INTEGER,
    name                VARCHAR2,
    containerId_s       VARCHAR2,
    containerKind       INTEGER,
    isLink              NUMBER,
    linkedObjectId_s    VARCHAR2,
    validUntil          DATE,
    description         VARCHAR2,
    helpUrl             VARCHAR2,
    searchContent       VARCHAR2,
    goal                VARCHAR2,
    -- common output parameters:
    oid_s               OUT     VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK 			CONSTANT INTEGER := 0;
    c_ALL_RIGHT 		CONSTANT INTEGER := 1; 
    c_INSUFFICIENT_RIGHTS 	CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND		CONSTANT INTEGER := 3;
    c_NOOID                 	CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    -- local variables:
    l_retValue 	INTEGER := c_NOT_OK;
    l_oid 	RAW(8);
BEGIN
    -- create the object:
    l_retValue := p_Help_01$create ( userId, op, tVersionId, name,
                	containerId_s, containerKind, isLink, linkedObjectId_s,
                	description, oid_s);

    -- convert oid
    p_stringToByte (oid_s, l_oid);

    IF (l_retValue = c_ALL_RIGHT)     -- operation properly performed?
    THEN
        BEGIN
            -- update further information
            l_retValue := p_Help_01$change (oid_s, userId, op, name,
                            validUntil, description, 0, helpUrl, searchContent);
            -- and set goal	    
	        UPDATE ibs_Help_01 h
	        SET    h.goal = p_Help_01$createFast.goal
	        WHERE  h.oid=l_oid;

        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error(ibs_error.error, 'p_Help_01$createFast','Error in createFast');
                RAISE;
        END;
    END IF;
    
    COMMIT WORK;       
    RETURN  l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Help_01$createFast',
    ', oid_s = ' || oid_s  ||
    ', userId = ' || userId  ||
    ', op = ' || op  ||
    ', name = ' || name ||
    ', validUntil = ' || validUntil ||
    ', description = ' || description ||
    ', helpUrl = ' || helpUrl ||
    ', searchContent = ' || searchContent ||
    ', goal = ' || goal ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);

RETURN  l_retValue;
    
END p_Help_01$createFast;
/

show errors;

/******************************************************************************
 * Creates a new referenz to a help object. <BR>
 *
 * @input parameters:
 * @param   l_userId               ID of the user who is creating the object.
 * @param   l_op                Operation to be performed (used for rights 
 *                              check).
 * @param   l_tVersionId        Type of the new object.
 * @param   l_validUntil        Date until which the object is valid.
 * @param   l_orginOid_s        Id of the orgin object.
 * @param   l_targetOid_s       Id of the target object.
 *
 * @output parameters:
 * @param   l_oid_s              OID of the newly created refernece.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 */
CREATE OR REPLACE FUNCTION p_Help_01$createRef
(
    l_userId            INTEGER,
    l_op             INTEGER,
    l_tVersionId     INTEGER,
    l_validUntil     DATE,
    -- type-specific input parameters:
    l_orginOid_s     VARCHAR2,
    l_targetOid_s    VARCHAR2,
    -- common output parameters:
    l_oid_s          OUT    VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK 			CONSTANT INTEGER := 0;
    c_ALL_RIGHT 		CONSTANT INTEGER := 1; 
    c_INSUFFICIENT_RIGHTS 	CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND		CONSTANT INTEGER := 3;
    c_NOOID                 	CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    -- local variables:
    l_retValue      INTEGER := c_NOT_OK;
    l_oid           RAW(8);
    l_orginOid      RAW(8);
    l_targetOid     RAW(8);
    l_targetName    VARCHAR2(63);
    l_containerId   RAW(8);
    l_containerId_s     VARCHAR2(18);
    l_TV_ReferenzContainer   INTEGER;

BEGIN
    -- convert oids
    p_stringToByte (l_orginOid_s, l_orginOid);
    p_stringToByte (l_targetOid_s, l_targetOid);
        
    -- get tVersionId of the reference container
    SELECT  actVersion
    INTO    l_TV_ReferenzContainer
    FROM    ibs_Type
    WHERE   code like 'ReferenzContainer';
       
    -- get referenz container of the orgin object
    SELECT  r.oid
    INTO    l_containerId
    FROM    ibs_object r, ibs_object o
    WHERE   r.containerId = o.oid 
        AND o.oid = l_orginOid
        AND r.tVersionId = l_TV_ReferenzContainer;
           
    -- get name of target object
    SELECT  o.name
    INTO    l_targetName
    FROM    ibs_object o
    WHERE   o.oid = l_targetOid;
                    
    -- convert oid
    p_byteToString (l_containerId, l_containerId_s);
        
    -- create the referenz:
    l_retValue := p_referenz_01$create (l_userId, l_op, l_tVersionId, 
                        l_targetName, l_containerId_s, 1, 1, l_targetOid_s, ' ', l_oid_s);

    COMMIT WORK;
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error ( ibs_error.error, 'p_Help_01$createRef',
        ', l_userId = ' || l_userId  ||
        ', l_op = ' || l_op  ||
        ', l_validUntil = ' || l_validUntil ||
        ', l_tVersionId = ' || l_tVersionId ||
        ', l_TV_ReferenzContainer = ' || l_TV_ReferenzContainer ||
        ', l_containerId = ' || l_containerId ||
        ', l_targetOid = ' || l_targetOid ||
        ', l_targetName = ' || l_targetName ||
        ', errorcode = ' || SQLCODE ||
        ', errormessage = ' || SQLERRM);
    RETURN  l_retValue;
    
END p_Help_01$createRef;
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
 * @param   @showInNews         the showInNews flag
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
 * @param   @searchContent      topics related to actuell helpobject
 * @param   @helpUrl            Url to the attached Dokument.
 *
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Help_01$retrieve
(
    -- common input parameters:
    ai_oid_s          VARCHAR2,
    ai_userId         INTEGER,
    ai_op             INTEGER,
    -- common output parameters:
    ao_state          OUT	INTEGER,
    ao_tVersionId     OUT	INTEGER,
    ao_typeName       OUT	VARCHAR2,
    ao_name           OUT	VARCHAR2,
    ao_containerId    OUT	RAW,
    ao_containerName  OUT	VARCHAR2,
    ao_containerKind  OUT	INTEGER,
    ao_isLink         OUT	NUMBER,
    ao_linkedObjectId OUT	RAW,
    ao_owner          OUT	INTEGER,
    ao_ownerName      OUT	VARCHAR2,
    ao_creationDate   OUT	DATE,
    ao_creator        OUT	INTEGER,
    ao_creatorName    OUT	VARCHAR2,
    ao_lastChanged    OUT	DATE,
    ao_changer        OUT	INTEGER,
    ao_changerName    OUT	VARCHAR2,
    ao_validUntil     OUT	DATE,
    ao_description    OUT	VARCHAR2,
    ao_showInNews     OUT   INTEGER,
    ao_checkedOut          OUT NUMBER,
    ao_checkOutDate        OUT DATE,
    ao_checkOutUser        OUT INTEGER,
    ao_checkOutUserOid     OUT RAW,
    ao_checkOutUserName    OUT VARCHAR2,
    -- type-specific output attributes:
    ao_helpUrl        OUT	VARCHAR2,
    ao_searchContent  OUT	VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK 			CONSTANT INTEGER := 0;
    c_ALL_RIGHT 		CONSTANT INTEGER := 1; 
    c_INSUFFICIENT_RIGHTS 	CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND		CONSTANT INTEGER := 3;
    -- local variables
    l_retValue 	INTEGER := c_NOT_OK;
    l_oid 	RAW(8);

BEGIN
    -- retrieve the base object data:
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

    IF (l_retValue = c_ALL_RIGHT)
    THEN
        BEGIN
            SELECT  helpUrl, searchContent
	    INTO    ao_helpUrl, ao_searchContent
            FROM    ibs_Help_01
	    WHERE   oid=l_oid;
        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error(ibs_error.error, 'p_Help_01$change','Error im SELECT');
                RAISE;
        END;
    END IF;

    COMMIT WORK;
    RETURN l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Help_01$retrieve',
    ', oid_s = ' || ai_oid_s  ||
    ', userId = ' || ai_userId  ||
    ', op = ' || ai_op  ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);
END p_Help_01$retrieve;
/

show errors;

/******************************************************************************
 * Deletes an object and all its values (incl. rights check). <BR>
 * This procedure also delets all links showing to this object.
 * 
 * @input parameters:
 * @param   @oid_s              ID of the object to be deleted.
 * @param   @userId                ID of the user who is deleting the object.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Help_01$delete
(
    ai_oid_s          VARCHAR2,
    ai_userId         INTEGER,
    ai_op             INTEGER
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK 			CONSTANT INTEGER := 0;
    c_ALL_RIGHT 		CONSTANT INTEGER := 1; 
    c_INSUFFICIENT_RIGHTS 	CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND		CONSTANT INTEGER := 3;
    -- local variables
    l_oid 	RAW(8);
    l_retValue  INTEGER := c_NOT_OK;

BEGIN
    p_stringToByte (ai_oid_s, l_oid);
    l_retValue := p_Object$performDelete (
			ai_oid_s, ai_userId, ai_op, l_oid);

    IF (l_retValue = c_ALL_RIGHT)     -- operation properly performed?
    THEN
        BEGIN
            -- delete object type specific data:
            DELETE  ibs_Help_01
            WHERE   oid NOT IN 
                    (SELECT oid 
                    FROM    ibs_Object);
	EXCEPTION
	    WHEN OTHERS THEN
                l_retValue := c_NOT_OK;
                ibs_error.log_error(ibs_error.error, 'p_Help_01$change','Error im SELECT');
                RAISE;
        END;
    END IF; -- if operation properly performed

    COMMIT WORK;
    RETURN l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Help_01$delete',
    ', oid_s = ' || ai_oid_s  ||
    ', userId = ' || ai_userId  ||
    ', op = ' || ai_op  ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);

END p_Help_01$delete;
/

show errors;

create or replace function p_Help_01$getExtended 
( 
    ai_oid       VARCHAR2, 
    ao_goal      OUT VARCHAR2
)
return integer
as
    l_oid       RAW(8);
begin
  p_stringToByte (ai_oid, l_oid);
  
  select goal 
  into  ao_goal
  from  ibs_Help_01 
  where oid = l_oid;
commit work;
  return 1;
exception
  when OTHERS then
    ibs_error.log_error(ibs_error.error, 'p_Help_01$getExtended',
                        'Input: ' || ai_oid || 
                        ', errorcode = ' || SQLCODE ||
                        ', errmsg = ' || SQLERRM );
    return 0;
end p_Help_01$getExtended;
/

show errors;

exit;