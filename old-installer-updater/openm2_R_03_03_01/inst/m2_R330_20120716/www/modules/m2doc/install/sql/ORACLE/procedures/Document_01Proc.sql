/******************************************************************************
 * All stored procedures regarding the object table. <BR>
 *
 * @version     $Id: Document_01Proc.sql,v 1.13 2003/10/31 00:13:14 klaus Exp $
 *
 * @author      Mario Stegbauer (MS)  980805
 *****************************************************************************/


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
CREATE OR REPLACE FUNCTION p_Document_01$create
(
    userId              NUMBER ,
    op                  NUMBER ,
    tVersionId          NUMBER ,
    name                VARCHAR2 ,
    containerId_s       VARCHAR2 ,
    containerKind       NUMBER ,
    IsLink              NUMBER ,
    linkedObjectId_s    VARCHAR2 ,
    description         VARCHAR2 ,
    oid_s           OUT VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_NOOID         CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    
    -- local variables
    StoO_selcnt         INTEGER;
    StoO_error          INTEGER;
    StoO_rowcnt         INTEGER;
    StoO_errmsg         VARCHAR2(255);
    StoO_sqlstatus      INTEGER;
    oid                 RAW (8);
    masterId            RAW (8);
    containerId         RAW (8);
    linkedObjectId      RAW (8);
    partofId_s          VARCHAR2 (18);
    ALL_RIGHT           NUMBER (10,0);
    INSUFFICIENT_RIGHTS NUMBER (10,0);
    OBJECTNOTFOUND      NUMBER (10,0);
    retValue            NUMBER (10,0);
    partofTVersionId    NUMBER (10,0);

BEGIN
    masterId            := c_NOOID;
    oid                 := c_NOOID;
    ALL_RIGHT           := 1;
    INSUFFICIENT_RIGHTS := 2;
    retValue            := ALL_RIGHT;
    partofTVersionId    := 16842849;
    
    BEGIN
        retValue:= p_Object$performCreate (userId, op, tVersionId, name, 
            containerId_s, containerKind, isLink, linkedObjectId_s, 
            description, oid_s, oid);
    EXCEPTION
        WHEN OTHERS THEN
            StoO_error := SQLCODE;
            StoO_errmsg := SQLERRM;
    END;

    COMMIT WORK;
    RETURN retValue;
    
EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error ( ibs_error.error, 'p_Document_01$create',
            'userId: ' || userId ||
            ', op: ' || op ||
            ', tVersionId: ' || tVersionId ||
            ', name: ' || name ||
            ', containerId_s: ' || containerId_s ||
            ', containerKind: ' || containerKind ||
            ', isLink: ' || IsLink ||
            ', linkedObjectId_s: ' || linkedObjectId_s ||
            ', description: ' || description ||
            ', sqlcode: ' || sqlcode ||
            ', sqlerrm: ' || sqlerrm );
    return 0;
END p_Document_01$create;
/

show errors;

/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be changed.
 * @param   @userId                ID of the user who is changing the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 * @param   @name               Name of the object.
 * @param   @validUntil         Date until which the object is valid.
 * @param   @description        Description of the object.
 * @param   @showInNews         Display object in News or not.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_Document_01$change
(
    -- input parameters:
    ai_oid_s             VARCHAR2,
    ai_userId            INTEGER,
    ai_op                INTEGER,
    ai_name              VARCHAR2,
    ai_validUntil        DATE,
    ai_description       VARCHAR2,
    ai_showInNews        NUMBER
)
RETURN INTEGER
AS
    -- define return constants
    c_ALL_RIGHT 		CONSTANT	INTEGER := 1;
    -- local variables
    l_retValue 		INTEGER := c_ALL_RIGHT;
    l_oid 		RAW(8);
    
BEGIN
    -- perform the change of the object:
    l_retValue := p_Object$performChange(ai_oid_s, ai_userId, ai_op, ai_name, 
                	ai_validUntil, ai_description, ai_showInNews, l_oid);
            
    COMMIT WORK;
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error ( ibs_error.error, 'p_Document_01$change',
        ', oid_s = ' || ai_oid_s ||
        ', userId = ' || ai_userId  ||
        ', op = ' || ai_op ||
        ', name = ' || ai_name ||
        ', validUntil = ' || ai_validUntil ||
        ', description = ' || ai_description ||
        ', showInNews = ' || ai_showInNews ||
        ', errorcode = ' || SQLCODE ||
        ', errormessage = ' || SQLERRM);
END p_Document_01$change;
/

show errors;

/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be changed.
 * @param   @userId                ID of the user who is creating the object.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 *
 * @output parameters:
 * @param   @state              The object's state.
 * @param   @tVersionId         ID of the object's type (correct version).
 * @param   @typeName           Name of the object's type.
 * @param   @name               Name of the object itself.
 * @param   @containerId        ID of the object's container.
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
 * @returns A value representing the state of the procedure.
 * ALL_RIGHT                    Action performed, values returned, everything ok.
 * INSUFFICIENT_RIGHTS          User has no right to perform action.
 * OBJECTNOTFOUND               The required object was not found within the 
 *                              database.
 */
CREATE OR REPLACE FUNCTION p_Document_01$retrieve
(
    -- input parameters:
    ai_oid_s           VARCHAR2,
    ai_userId          INTEGER,
    ai_op              INTEGER,
    -- output parameters
    ao_state           OUT INTEGER,
    ao_tVersionId      OUT INTEGER,
    ao_typeName        OUT VARCHAR2,
    ao_name            OUT VARCHAR2,
    ao_containerId     OUT RAW,
    ao_containerName   OUT VARCHAR2,
    ao_containerKind   OUT INTEGER,
    ao_isLink          OUT NUMBER,
    ao_linkedObjectId  OUT RAW,
    ao_owner           OUT INTEGER,
    ao_ownerName       OUT VARCHAR2,
    ao_creationDate    OUT DATE,
    ao_creator         OUT INTEGER,
    ao_creatorName     OUT VARCHAR2,
    ao_lastChanged     OUT DATE,
    ao_changer         OUT INTEGER,
    ao_changerName     OUT VARCHAR2,
    ao_validUntil      OUT DATE,
    ao_description     OUT VARCHAR2,
    ao_showInNews      OUT NUMBER,
    ao_checkedOut          OUT NUMBER,
    ao_checkOutDate        OUT DATE,
    ao_checkOutUser        OUT INTEGER,
    ao_checkOutUserOid     OUT RAW,
    ao_checkOutUserName    OUT VARCHAR2,
    ao_attachmentContainerId_s OUT VARCHAR2, 
    ao_masterId_s      OUT VARCHAR2, 
    ao_fileName        OUT VARCHAR2,
    ao_url             OUT VARCHAR2,
    ao_path            OUT VARCHAR2,
    ao_attachmentType  OUT INTEGER
    
)
RETURN INTEGER
AS
    -- constants:
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    -- define local variables:
    l_oid                   RAW (8) := c_NOOID;
    l_retValue              INTEGER := c_ALL_RIGHT; 
    l_masterId              RAW (8) := c_NOOID; -- id of the new master
    l_partofTVersionId      INTEGER := 16842849;  -- AttachmentContainer
    l_attachmentContainerId RAW (8) := c_NOOID;
    l_Dummy                 INTEGER;
    l_counter               INTEGER;
    l_masterRights          INTEGER;
    l_necessaryRights       INTEGER := 0;   -- necessary rights for actual
                                            -- operation
    

BEGIN 

    ao_attachmentType := -1;
    ao_fileName := '';
    ao_url := '';
    ao_path := '';

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

    IF (l_retValue = c_ALL_RIGHT) -- object is processed properly
    THEN     	
        -- find the attachmentcontainer of the document
	    BEGIN
	        SELECT oid
	        INTO   l_attachmentContainerId
	        FROM   ibs_object
	        WHERE  containerId = l_oid 
	           AND  tVersionId = l_partofTVersionId;
	    EXCEPTION
	        WHEN OTHERS THEN
	        ibs_error.log_error ( ibs_error.error, 'p_Document_01$retrieve',
	    				'Error in find attachmentContainer');
	        RAISE;
	    END;

        -- ensures that in the attachment container is a master set
        l_retValue := p_Attachment_01$ensureMaster (l_attachmentContainerId, null);      
            
	    BEGIN    
	        --search the actual master
	        SELECT  a.oid 
            INTO    l_masterId
        	FROM    ibs_Attachment_01 a, ibs_Object o
	        WHERE   o.containerId = l_attachmentContainerId 
            AND     a.isMaster = 1
            AND     o.oid = a.oid
            AND     o.state = 2;
        EXCEPTION
             WHEN NO_DATA_FOUND THEN
                  l_masterId := '';
             WHEN OTHERS THEN
                 ibs_error.log_error ( ibs_error.error, 'p_Attachment_01$change',
                     'Error when selecting TOKEN');
             RAISE;        
        END;

        -- get the rights for the master attachment
        BEGIN
            -- get the necessary rights:
            SELECT  SUM (id)
            INTO    l_necessaryRights
            FROM    ibs_Operation
            WHERE   name IN ('view', 'read');

            l_masterRights := p_Rights$checkRights (l_masterId, l_attachmentContainerId,
                ai_userId, l_necessaryRights, l_masterRights);

        EXCEPTION
            WHEN OTHERS THEN
		        ibs_error.log_error (ibs_error.error, 'p_Document_01$retrieve',
		    	   		              'Error in check rights');
        END;
        -- check if user is granted view rigths
        IF (l_masterRights = l_necessaryRights)
        THEN            
            -- read out the properties
            BEGIN
                SELECT filename, url, path, attachmentType
                INTO   ao_fileName, ao_url, ao_path, ao_attachmentType
                FROM   ibs_Attachment_01
                WHERE oid = l_masterId;
		    EXCEPTION
                WHEN NO_DATA_FOUND THEN
                     ao_fileName := '';
                     ao_url := '';
                     ao_path := '';
                     ao_attachmentType := '';
                     
		        WHEN OTHERS THEN
		        ibs_error.log_error (ibs_error.error, 'p_Document_01$retrieve',
		    	   		              'Error in read out properties');
		        RAISE;
		    END;
		END IF;		
        -- convert to output
        p_byteToString (l_attachmentContainerId, ao_attachmentContainerId_s);
        p_byteToString (l_masterId, ao_masterId_s);
    END IF; -- object is processed properly

    COMMIT WORK;
    -- return the state value
    RETURN  l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_Document_01$retrieve',
    ', oid_s = ' || ai_oid_s ||
    ', userId = ' || ai_userId  ||
    ', op = ' || ai_op ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);
END p_Document_01$retrieve;
/

show errors;

EXIT;
