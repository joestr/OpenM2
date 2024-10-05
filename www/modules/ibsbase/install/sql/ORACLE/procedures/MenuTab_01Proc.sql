/******************************************************************************
 * All stored procedures regarding the MenuTab_01 Object. <BR>
 *
 * @version     1.10.0001, 08.10.2001
 *
 * @author      Monika Eisenkolb (ME)  081001
 *
 * <DT><B>Updates:</B>
 * <DD>
 ******************************************************************************
 */


/******************************************************************************
 * Creates a new MenuTab_01 Object (incl. rights check). <BR>
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
CREATE OR REPLACE FUNCTION p_MenuTab_01$create
(
    -- common input parameters:
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_tVersionId           INTEGER,
    ai_name                 VARCHAR2,
    ai_containerId_s        VARCHAR2,
    ai_containerKind        INTEGER,
    ai_isLink               NUMBER,
    ai_linkedObjectId_s     VARCHAR2,
    ai_description          VARCHAR2,
    -- common output parameters:
    ao_oid_s                OUT VARCHAR2
)RETURN INTEGER
AS
    -- definitions:
    -- variables:
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');

    -- local valriables
    l_retValue              INTEGER := c_NOT_OK; -- return value of procedure
    l_oid                   RAW (8) := c_NOOID;

    -- body:
    BEGIN
    -- create base object:
    l_retValue := p_Object$performCreate (ai_userId, ai_op, ai_tVersionId,
                        ai_name, ai_containerId_s, ai_containerKind,
                        ai_isLink, ai_linkedObjectId_s, ai_description,
                        ao_oid_s, l_oid);


    IF (l_retValue = c_ALL_RIGHT)       -- object created successfully:1
    THEN
        BEGIN
            -- create object type specific data:
            INSERT INTO ibs_MenuTab_01 (oid
                            , objectOid, description,
                            isPrivate, priorityKey, domainId, classFront, classBack,
                            fileName)
            VALUES      (l_oid
                         ,c_NOOID,' ',
                         0,0,0,'groupFront.gif','groupBack.gif',
                         'welcome.htm');

        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error,
                                      'p_MenuTab_01$create',
                                      'Error in INSERT INTO');
            RAISE;

        END;

    END IF; -- if object created successfully

    COMMIT WORK;

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_MenuTab_01$create',
            'Input: ai_userId = ' || ai_userId ||
            '; sqlcode = ' || SQLCODE ||
            ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;

END p_MenuTab_01$create;

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
CREATE OR REPLACE FUNCTION p_MenuTab_01$change
(
    -- common input parameters:
    ai_oid_s                VARCHAR2,
    ai_userId               INTEGER,
    ai_op                   INTEGER,
    ai_name                 VARCHAR2,
    ai_validUntil           DATE,
    ai_description          VARCHAR2,
    ai_showInNews           NUMBER,
    -- typespecific input parameters
    ai_objectoids           VARCHAR2,
    ai_filename             VARCHAR2,
    ai_tabpos               INTEGER,
    ai_front                VARCHAR2,
    ai_back                 VARCHAR2


)RETURN INTEGER
AS
    -- definitions:
    -- variables:
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    c_EMPTYPOSNOPATH        CONSTANT VARCHAR2(254) := '0000';
                                            -- default value for empty pos no
                                            -- path

    -- local valriables
    l_retValue              INTEGER := c_NOT_OK; -- return value of procedure
    l_oid                   RAW (8) := c_NOOID;
    l_objectoid             RAW (8) := c_NOOID;
    l_testoid               RAW (8) := c_NOOID;

    l_rValue                INTEGER := c_ALL_RIGHT;
                                            -- return value of this procedure
    l_posNoPath             VARCHAR2(254) := c_EMPTYPOSNOPATH;
                                            -- the pos no path of the object
    l_isPrivate             INTEGER := 0;   -- a flag which is 1 if object is
                                            -- a workspace
    l_domainId              INTEGER := 0;   -- id of the domain where the object
                                            -- exists
    l_showInMenu            NUMBER (1) := 0; -- the show in menu flag of the
                                             -- object
    l_count                 INTEGER := 0;   -- counter
    r                       INTEGER := 0;
    l_objectname            VARCHAR(254);
    l_oldObjectId           RAW (8);        -- oid before change



    -- body:
BEGIN
    -- convert oidString to oid
    p_stringToByte (ai_oid_s, l_oid);
    p_stringToByte (ai_objectoids, l_objectoid);


    -- perform the change of the object:
    l_retValue := p_Object$performChange (ai_oid_s, ai_userId, ai_op, ai_name,
            ai_validUntil, ai_description, ai_showInNews, l_oid);

    IF (l_retValue = c_ALL_RIGHT)       -- object created successfully:1
    THEN
        BEGIN

            select name, posNoPath into l_objectname,l_posNoPath from ibs_object
            where oid = l_objectoid;

                SELECT  id
                INTO    l_domainId
                FROM    ibs_Domain_01
                WHERE   oid = ( SELECT  oid
                                FROM    ibs_Object
                                -- the third parameter of SUBSTR is 8 because
                                -- the objects of type domain have always the
                                -- same length of pos no path
                                WHERE   SUBSTR(l_posNoPath, 1, 8) = posNoPath
                              );


            -- check if given object is a workspaceContainer
            SELECT  COUNT (*)
            INTO    l_count
            FROM    ibs_domain_01
            WHERE   workspacesOid = l_objectoid;

            IF (l_count > 0)                -- is it an private menutab :2
            THEN
                l_isPrivate := 1;
            END IF; -- if is it an private menutab

            BEGIN
                -- get oid of object which was assigned to menutab before change
                SELECT objectoid INTO l_oldObjectId
                FROM ibs_MenuTab_01 WHERE oid = l_oid;

                -- don't show the object menu again
                UPDATE  ibs_Object
                SET     showInMenu = 1
                WHERE   oid = l_oldObjectId;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                -- if no object was set before change - do nothing
                    NULL;
            END;

            -- set new data in menutab
            UPDATE  ibs_MenuTab_01
            SET     objectoid = l_objectoid,
                    description = l_objectname,
                    prioritykey = ai_tabpos,
                    filename = ai_filename,
                    classfront = substr(ai_front, 1, instr (ai_front,'.')-1),
                    classback = substr(ai_back, 1, instr (ai_back,'.')-1),
                    isprivate = l_isPrivate,
                    domainid = l_domainId
            WHERE   oid = l_oid;


             -- don't show the object in the menu because it's an menutab
             UPDATE  ibs_Object
             SET     showInMenu = 0
             WHERE   oid = l_objectoid;


        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error,
                                      'p_MenuTab_01$change',
                                      'Error in UPDATE');
            RAISE;
        END;


    END IF; -- if object created successfully

    COMMIT WORK;

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_MenuTab_01$change',
            'Input: ai_userId = ' || ai_userId ||
            '; sqlcode = ' || SQLCODE ||
            ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;


END p_MenuTab_01$change;
/
show errors;


/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 *
 */

CREATE OR REPLACE FUNCTION p_MenuTab_01$retrieve
(
    -- common input parameters:
    ai_oid_s                   VARCHAR2,
    ai_userId                  INTEGER,
    ai_op                      INTEGER,
    -- common output parameters:
    ao_state                   OUT INTEGER,
    ao_tVersionId              OUT INTEGER,
    ao_typeName                OUT VARCHAR2,
    ao_name                    OUT VARCHAR2,
    ao_containerId             OUT RAW,
    ao_containerName           OUT VARCHAR2,
    ao_containerKind           OUT INTEGER,
    ao_isLink                  OUT NUMBER,
    ao_linkedObjectId          OUT RAW,
    ao_owner                   OUT INTEGER,
    ao_ownerName               OUT VARCHAR2,
    ao_creationDate            OUT DATE,
    ao_creator                 OUT INTEGER,
    ao_creatorName             OUT VARCHAR2,
    ao_lastChanged             OUT DATE,
    ao_changer                 OUT INTEGER,
    ao_changerName             OUT VARCHAR2,
    ao_validUntil              OUT DATE,
    ao_description             OUT VARCHAR2,
    ao_showInNews              OUT NUMBER,
    ao_checkedOut              OUT NUMBER,
    ao_checkOutDate            OUT DATE,
    ao_checkOutUser            OUT INTEGER,
    ao_checkOutUserOid         OUT RAW,
    ao_checkOutUserName        OUT VARCHAR2,
    -- type-specific output attributes:
    ao_objectoid                OUT RAW,
    ao_objectname               OUT VARCHAR2,
    ao_tabpos                   OUT INTEGER,
    ao_front                    OUT VARCHAR2,
    ao_back                     OUT VARCHAR2,
    ao_filename                 OUT VARCHAR2

)RETURN INTEGER
AS
    -- definitions:
    -- variables:
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');

    -- local valriables
    l_retValue              INTEGER := c_NOT_OK; -- return value of procedure
    l_oid                   RAW (8) := c_NOOID;
    l_objectoid             RAW (8) := c_NOOID;


    -- body:
BEGIN
    -- retrieve the base object data:
    l_retValue := p_Object$performRetrieve (ai_oid_s, ai_userId, ai_op, ao_state, ao_tVersionId,
            ao_typeName, ao_name, ao_containerId, ao_containerName, ao_containerKind, ao_isLink,
            ao_linkedObjectId, ao_owner, ao_ownerName, ao_creationDate, ao_creator, ao_creatorName,
            ao_lastChanged, ao_changer, ao_changerName, ao_validUntil, ao_description,
            ao_showInNews, ao_checkedOut, ao_checkOutDate, ao_checkOutUser, ao_checkOutUserOid,
            ao_checkOutUserName, l_oid);

    IF (l_retValue = c_ALL_RIGHT)       -- object created successfully:1
    THEN
        BEGIN

            select objectoid, prioritykey, classfront, classback, filename
            into ao_objectoid, ao_tabpos, ao_front, ao_back, ao_filename
            from ibs_MenuTab_01
            where oid = l_oid;

            select name into ao_objectname from ibs_object where oid = ao_objectoid;

        EXCEPTION
            WHEN OTHERS THEN
                ibs_error.log_error ( ibs_error.error,
                                      'p_MenuTab_01$retrieve',
                                      'Error in SELECT');
            RAISE;
        END;

    END IF; -- if object created successfully

    COMMIT WORK;

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_MenuTab_01$retrieve',
            'oid_s = ' || ai_oid_s ||
            'userId = ' || ai_userId ||
            'op = ' || ai_op ||
            '; sqlcode = ' || SQLCODE ||
            ' sqlerrm = ' || SQLERRM);
        -- return error value:
        RETURN c_NOT_OK;

END p_MenuTab_01$retrieve;
/
show errors;

/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 *
 */

CREATE OR REPLACE FUNCTION p_MenuTab_01$delete
(
    ai_oid_s                VARCHAR2,
    ai_userId               INTEGER,
    ai_op                   INTEGER
)
RETURN INTEGER
AS
    l_oid                   RAW (8);
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_INSUFFICIENT_RIGHTS   CONSTANT INTEGER := 2;
    c_OBJECTNOTFOUND        CONSTANT INTEGER := 3;
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    l_retValue              INTEGER := c_NOT_OK;
    l_count                 INTEGER := 0;   -- counter

BEGIN
    p_stringToByte(ai_oid_s, l_oid);


    -- show the object in the menu because it's no longer a menutab
    UPDATE  ibs_Object
    SET     showInMenu = 1
    WHERE   oid = (select objectoid from ibs_MenuTab_01 where oid = l_oid);

    -- important for creating the right application path in m2
    UPDATE ibs_MenuTab_01
    SET objectoid = c_NOOID
    WHERE oid = l_oid;

    l_retValue:=p_Object$performDelete(ai_oid_s,
     ai_userId, ai_op, l_oid);


    COMMIT WORK;
    RETURN l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    ibs_error.log_error ( ibs_error.error, 'p_MenuTab_01$delete',
    ', oid_s = ' || ai_oid_s ||
    ', userId = ' || ai_userId  ||
    ', op = ' || ai_op ||
    ', errorcode = ' || SQLCODE ||
    ', errormessage = ' || SQLERRM);
END p_MenuTab_01$delete;
/
show errors;


EXIT;




