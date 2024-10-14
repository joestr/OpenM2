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

exit;