/******************************************************************************
 * All stored procedures regarding the UserAddress_01 Object. <BR>
 * 
 * @version     1.10.0001, 03.08.1999
 *
 * @author      Koban Ferdinand (FF)  010122
 *
 * <DT><B>Updates:</B>
 * <DD>
 ******************************************************************************
 */


/******************************************************************************
 * Creates a new UserAddress_01 Object (incl. rights check). <BR>
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




CREATE OR REPLACE FUNCTION p_UserAddress_01$create
(
    -- input parameters:
    ai_userId           INTEGER,
    ai_op               INTEGER,
    ai_tVersionId       INTEGER,
    ai_name             VARCHAR2,
    ai_containerId_s    VARCHAR2,
    ai_containerKind    INTEGER,
    ai_isLink           NUMBER,
    ai_linkedObjectId_s VARCHAR2,
    ai_description      VARCHAR2,
    -- output parameters:
    ao_oid_s          OUT VARCHAR2
)
RETURN INTEGER
AS
   
    -- definitions:
    -- define return constants :
    c_NOT_OK                    CONSTANT INTEGER := 0;
    c_ALL_RIGHT                 CONSTANT INTEGER := 1; 
    c_INSUFFICIENT_RIGHTS       CONSTANT INTEGER := 2;
    c_ALREADY_EXISTS            CONSTANT INTEGER := 21;    
    
    -- return value of this procedure
    l_retValue                  INTEGER := c_ALL_RIGHT;                 
    -- define locale Variables :
    l_containerId               RAW (8) := hexToRaw ('0000000000000000');
    l_linkedObjectId            RAW (8) := hexToRaw ('0000000000000000');
    l_oid                       RAW (8) := hexToRaw ('0000000000000000');
    l_addressOid_s              VARCHAR2 (18);
    l_personsOid_s              VARCHAR2 (18);

-- body:
BEGIN
    -- convertions (objectidstring) - all input objectids must be converted

    p_stringToByte (ai_containerId_s, l_containerId);
    p_stringToByte (ai_linkedObjectId_s, l_linkedObjectId);
    
    -- create base object:
    l_retValue := p_Object$performCreate (ai_userId, ai_op, ai_tVersionId, ai_name, ai_containerId_s,
            ai_containerKind, ai_isLink, ai_linkedObjectId_s, ai_description, 
            ao_oid_s, l_oid );

    IF (l_retValue = c_ALL_RIGHT) THEN    -- object created successfully?
        BEGIN
            -- insert the other values
            INSERT INTO Ibs_UserAddress_01 (oid, email, smsemail)
            VALUES (l_oid, '', '');
    
        EXCEPTION
            WHEN OTHERS THEN
            ibs_error.log_error (ibs_error.error, 'p_UserAddress_01$create',
            'Error in INSERT INTO');
            RAISE;
        END;
    END IF; -- if object created successfully 

COMMIT WORK;
-- return the state value
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_UserAddress_01$create',
            'userId: ' || ai_userId ||
            ', op: ' || ai_op ||
            ', tVersionId: ' || ai_tVersionId ||
            ', name: ' || ai_name ||
            ', containerId_s: ' || ai_containerId_s ||
            ', containerKind: ' || ai_containerKind ||
            ', isLink: ' || ai_isLink ||
            ', linkedObjectId_s: ' || ai_linkedObjectId_s ||
            ', description: ' || ai_description ||
            '; sqlcode: ' || SQLCODE ||
            ', sqlerrm: ' || SQLERRM );
    RETURN c_NOT_OK;

END p_UserAddress_01$create;
/
show errors;


/******************************************************************************
 * Changes the attributes of an existing object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be changed.
 * @param   @userId             ID of the user who is creating the object.
 * @param   @op                 Operation to be performed (used for rights 
 *                              check).
 * @param   @name               Name of the object.
 * @param   @validUntil         Date until which the object is valid.
 * @param   @description        Description of the object.
 * @param   @showInNews         show in news flag.
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- delete existing procedure:
CREATE OR REPLACE FUNCTION p_UserAddress_01$change
(
    -- input parameters:
    ai_oid_s          VARCHAR2,
    ai_userId         INTEGER,
    ai_op             INTEGER,
    ai_name           VARCHAR2,
    ai_validUntil     DATE,
    ai_description    VARCHAR2,
    ai_showInNews     NUMBER,
    ai_email          VARCHAR2,
    ai_smsemail       VARCHAR2
)
RETURN INTEGER

AS
    
    -- definitions:
    -- define return constants :
    c_NOT_OK                    CONSTANT INTEGER := 0;
    c_ALL_RIGHT                 CONSTANT INTEGER := 1; 
    c_INSUFFICIENT_RIGHTS       CONSTANT INTEGER := 2;
    c_ALREADY_EXISTS            CONSTANT INTEGER := 21;    
    
    -- return value of this procedure
    l_retValue                  INTEGER := c_ALL_RIGHT;                 
    l_oid                       RAW (8) := hexToRaw ('0000000000000000');

-- body:
BEGIN 
        -- perform the change of the object:
        l_retValue := p_Object$performChange ( ai_oid_s, ai_userId, ai_op, ai_name,
                ai_validUntil, ai_description, ai_showInNews, l_oid );

        IF (l_retValue = c_ALL_RIGHT) THEN    -- operation properly performed?
        BEGIN
            -- update the other values
            UPDATE ibs_UserAddress_01
            SET    email = ai_email,
                   smsemail = ai_smsemail
            WHERE  oid = l_oid;
        EXCEPTION
            WHEN OTHERS THEN
            ibs_error.log_error (ibs_error.error, 'p_UserAddress_01$change',
            'Error in UPDATE TABLE');
            RAISE;
        END;
    END IF; -- if object created successfully 

COMMIT WORK;

    -- return the state value
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_UserAddress_01$create',
            'userId: ' || ai_userId ||
            ', op: ' || ai_op ||
            ', name: ' || ai_name ||
            ', description: ' || ai_description ||
            '; sqlcode: ' || SQLCODE ||
            ', sqlerrm: ' || SQLERRM );
    RETURN c_NOT_OK;

END p_UserAddress_01$change;
/
show errors;

/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be changed.
 * @param   @userId             ID of the user who is creating the object.
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
 * @param   @showInNews         show in news flag.
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
 * @param   @maxlevels          Maximum of the levels allowed in the discussion
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */

CREATE OR REPLACE FUNCTION p_UserAddress_01$retrieve
(
    -- input parameters:
  ai_oid_s                 VARCHAR2,
  ai_userId                INTEGER,                       
  ai_op                    INTEGER,                          
    -- output parameters                           
  ao_state             OUT INTEGER,          
  ao_tVersionId        OUT INTEGER,          
  ao_typeName          OUT VARCHAR2,         
  ao_name              OUT VARCHAR2,         
  ao_containerId       OUT RAW,              
  ao_containerName     OUT VARCHAR2,         
  ao_containerKind     OUT INTEGER,                        
  ao_isLink            OUT NUMBER,           
  ao_linkedObjectId    OUT RAW,              
  ao_owner             OUT INTEGER,          
  ao_ownerName         OUT VARCHAR2,         
  ao_creationDate      OUT DATE,             
  ao_creator           OUT INTEGER,          
  ao_creatorName       OUT VARCHAR2,         
  ao_lastChanged       OUT DATE,             
  ao_changer           OUT INTEGER,          
  ao_changerName       OUT VARCHAR2,         
  ao_validUntil        OUT DATE,             
  ao_description       OUT VARCHAR2,         
  ao_showInNews        OUT NUMBER,           
  ao_checkedOut        OUT NUMBER,           
  ao_checkOutDate      OUT DATE,             
  ao_checkOutUser      OUT INTEGER,          
  ao_checkOutUserOid   OUT RAW,              
  ao_checkOutUserName  OUT VARCHAR2,         
  -- type specific output parameters
  ao_email             OUT VARCHAR2,         
  ao_smsemail          OUT VARCHAR2          

)
RETURN INTEGER

AS
    
    -- definitions:
    -- define return constants :
    c_NOT_OK                    CONSTANT INTEGER := 0;
    c_ALL_RIGHT                 CONSTANT INTEGER := 1; 
    c_INSUFFICIENT_RIGHTS       CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND            CONSTANT INTEGER := 3;
    c_ST_ACTIVE                 CONSTANT INTEGER := 2;    
    
    -- return value of this procedure
    l_retValue                  INTEGER := c_ALL_RIGHT;                 
    l_oid                       RAW (8) := hexToRaw ('0000000000000000');

-- body:
BEGIN 
       -- retrieve the base object data:
     l_retValue := p_Object$performRetrieve (
                ai_oid_s, ai_userId, ai_op,
                ao_state , ao_tVersionId , ao_typeName , 
                ao_name , ao_containerId , ao_containerName , 
                ao_containerKind , ao_isLink , ao_linkedObjectId , 
                ao_owner , ao_ownerName , 
                ao_creationDate , ao_creator , ao_creatorName ,
                ao_lastChanged , ao_changer , ao_changerName ,
                ao_validUntil , ao_description , ao_showInNews , 
                ao_checkedOut , ao_checkOutDate , 
                ao_checkOutUser , ao_checkOutUserOid , ao_checkOutUserName , 
                l_oid );

        IF (l_retValue = c_ALL_RIGHT) THEN
        BEGIN
            
            SELECT email,
                   smsemail
            INTO ao_email, ao_smsemail
            FROM Ibs_UserAddress_01
            WHERE oid=l_oid; 
        EXCEPTION
            WHEN OTHERS THEN
            ibs_error.log_error (ibs_error.error, 'p_UserAddress_01$retrieve',
            'Error by retrieve the data');
            RAISE;
        END;
        END IF;

COMMIT WORK;

-- return the state value
RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_UserAddress_01$retrieve',
            'userId: ' || ai_userId ||
            ', op: ' || ai_op ||
            '; sqlcode: ' || SQLCODE ||
            ', sqlerrm: ' || SQLERRM );
    RETURN c_NOT_OK;

END p_UserAddress_01$retrieve;
/
show errors;

EXIT;




