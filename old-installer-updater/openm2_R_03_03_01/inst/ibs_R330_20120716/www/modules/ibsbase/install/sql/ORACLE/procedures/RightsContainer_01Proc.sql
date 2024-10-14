/******************************************************************************
 * All stored procedures regarding the RightsContainer_01 Object. <BR>
 * 
 * @version     1.10.0001, 05.08.1999
 *
 * @author      Mario Stegbauer (MS)  980805
 *
 * <DT><B>Updates:</B>
 * <DD>HB 990805    Code cleaning.
 ******************************************************************************
 */


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
 * @param   @showInNews         flag if content should be shown in newscontainer
 * @param   @checkedOut         Is the object checked out?
 * @param   @checkOutDate       Date when the object was checked out
 * @param   @checkOutUser       Oid of the user which checked out the object
 * @param   @checkOutUserName   name of the user which checked out the object,
 *                              is only set if this user has the right to read
 *                              the checkOut-User
 * @returns A value representing the state of the procedure.
 *      ALL_RIGHT               Action performed, values returned, everything ok.
 *      INSUFFICIENT_RIGHTS     User has no right to perform action.
 *      OBJECTNOTFOUND          The required object was not found within the 
 *                              database.
 */
CREATE OR REPLACE FUNCTION p_RightsContainer_01$retrieve
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    -- output parameters:
    ao_state                OUT INTEGER,
    ao_tVersionId           OUT INTEGER,
    ao_typeName             OUT VARCHAR2,
    ao_name                 OUT VARCHAR2,
    ao_containerId          OUT RAW,
    ao_containerName        OUT VARCHAR2,
    ao_containerKind        OUT INTEGER,
    ao_isLink               OUT NUMBER,
    ao_linkedObjectId       OUT RAW,
    ao_owner                OUT INTEGER,
    ao_ownerName            OUT VARCHAR2,
    ao_creationDate         OUT DATE,
    ao_creator              OUT INTEGER,
    ao_creatorName          OUT VARCHAR2,
    ao_lastChanged          OUT DATE,
    ao_changer              OUT INTEGER,
    ao_changerName          OUT VARCHAR2,
    ao_validUntil           OUT DATE,
    ao_description          OUT VARCHAR2,
    ao_showInNews           OUT INTEGER,
    ao_checkedOut          OUT NUMBER,
    ao_checkOutDate        OUT DATE,
    ao_checkOutUser        OUT INTEGER,
    ao_checkOutUserOid     OUT RAW,
    ao_checkOutUserName    OUT VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3;
    c_RIGHT_READ            CONSTANT INTEGER := 2;
    c_INNEWS                CONSTANT INTEGER := 4;
    c_ISCHECKEDOUT          CONSTANT INTEGER := 16;

    -- local variables:
    l_retValue              INTEGER := c_All_RIGHT;
    l_oid                   RAW (8);
    l_rights                INTEGER := 0;
    l_dummy                 INTEGER := 0;

BEGIN
    -- CONVERSIONS (OBJECTIDSTRING) - all input objectids must be converted
    p_stringToByte (ai_oid_s, l_oid);

    BEGIN
        -- get container id of object:
        SELECT  o.containerId, o2.name
        INTO    ao_containerId, ao_containerName 
        FROM    ibs_Object o, ibs_Object o2 
        WHERE   o.oid = l_oid 
            AND o2.oid = o.containerId;

        -- at this point we know that the container exists.
        -- get rights for the actual user:
        l_rights := p_Rights$checkRights (
            l_oid,                      -- given object to be accessed by user
            ao_containerId,             -- container of given object
            ai_userId,                  -- user_id
            ai_op,                      -- required rights user must have to 
                                        -- retrieve object (op. to be 
                                        -- performed)
            l_rights                    -- returned value
            );

        -- check if the user has the necessary rights:
        IF (l_rights > 0)               -- the user has the rights?
        THEN
	    -- get the data of the object and return it
             SELECT  o.state, o.tVersionId, o.typeName, o.name, 
                    o.containerId, o.containerKind, 
                    o.isLink, o.linkedObjectId, 
                    o.owner, own.fullname,
                    o.creationDate, o.creator, cr.fullname,
                    o.lastChanged, o.changer, ch.fullname,
                    o.validUntil,
                    B_AND (o.flags, c_INNEWS)
            INTO    ao_state, ao_tVersionId, ao_typeName, ao_name, 
                    ao_containerId, ao_containerKind, 
                    ao_isLink, ao_linkedObjectId, 
                    ao_owner, ao_ownerName,
                    ao_creationDate, ao_creator, ao_creatorName,
                    ao_lastChanged, ao_changer, ao_changerName,
                    ao_validUntil, ao_showInNews
            FROM    ibs_Object o, ibs_User own, ibs_User cr, ibs_User ch
            WHERE   o.owner = own.id(+)
                AND o.creator = cr.id(+)
                AND o.changer = ch.id(+)
                AND o.oid = l_oid;
   
            -- set object as read:
            l_dummy := p_setRead (l_oid, ai_userId);
        ELSE                            -- the user does not have the rights
            l_retValue := c_INSUFFICIENT_RIGHTS;
        END IF; -- if object exists

    EXCEPTION
        WHEN NO_DATA_FOUND THEN     -- the rights container does not exist?
            l_retValue := c_OBJECTNOTFOUND;
    END;

    COMMIT WORK;

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_RightsContainer_01$retrieve',
            'Input: ai_oid_s = ' || ai_oid_s ||
            ', ai_userId = ' || ai_userId  ||
            ', ai_op = ' || ai_op ||
            ', errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM);
END p_RightsContainer_01$retrieve;
/

show errors;

exit;
