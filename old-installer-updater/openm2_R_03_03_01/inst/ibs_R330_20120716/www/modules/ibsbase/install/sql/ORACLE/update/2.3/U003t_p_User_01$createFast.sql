/******************************************************************************
 * Creates a new user. <BR>
 * This procedure also adds the user to a group and sets the rights of members 
 * of this group on the user.
 *
 * @input parameters:
 * @param   @userId             ID of the user who is creating the object.
 * @param   @domainId           Id of the domain where the user shall resist.
 * @param   @username           Name of the user.
 * @param   @password           Password initially set for this user.
 * @param   @fullname           Full name of the user.
 *
 * @output parameters:
 * @param   @oid                Oid of the newly generated user.
 * @return  A value representing the state of the procedure.
 * @ALL_RIGHT               Action performed, values returned, everything ok.
 * @ALREADY_EXISTS          An user with this id already exists.
 */
CREATE OR REPLACE FUNCTION p_User_01$createFast
(
    -- input parameters:
    ai_userId               INTEGER,
    ai_domainId             INTEGER,
    ai_username             VARCHAR2,
    ai_password             VARCHAR2,
    ai_fullname             VARCHAR2,
    -- output parameters:
    ao_oid                  OUT RAW
)
RETURN INTEGER
AS
    -- constants:
    c_NOOID                 CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    c_NOOID_s               CONSTANT VARCHAR2 (18) := '0x0000000000000000';
    c_NOT_OK                CONSTANT INTEGER := 0;
    c_ALL_RIGHT             CONSTANT INTEGER := 1;
    c_ALREADY_EXISTS        CONSTANT INTEGER := 21;

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_containerId           RAW (8);
    l_containerId_s         VARCHAR2 (18);
    l_oid_s                 VARCHAR2 (18);
    l_groupId               INTEGER;
    l_groupOid              RAW (8); 
    l_validUntil            DATE;
   
BEGIN
    -- initialize return values:
    ao_oid := c_NOOID;

    BEGIN
        -- get user container:
        SELECT  usersOid
        INTO    l_containerId
        FROM    ibs_Domain_01
        WHERE   id = ai_domainId;

        -- at this point we know that the domain exists.

        -- convert container oid to string representation:
        p_byteToString (l_containerId, l_containerId_s);

        -- create the user (tVersionId 0x010100A1):
        -- the current user does not need any rights because this procedure
        -- shall only be called during installation.
        l_retValue := p_User_01$performCreate (
            ai_userId, 0, 16842913, ai_username, l_containerId_s, 1,
            0, c_NOOID_s, ' ', NULL,
            l_oid_s);

        -- convert user oid string to oid representation:
        p_stringToByte (l_oid_s, ao_oid);

        -- check if there was an error during creation:
        IF (l_retValue = c_ALL_RIGHT)   -- user created successfully?
        THEN
            BEGIN
                l_validUntil := ADD_MONTHS (SYSDATE, 12);

                -- store user specific data:
                -- the user has created the object, so don't check rights.
                l_retValue := p_User_01$change (l_oid_s, ai_userId, 0,
                    ai_username, l_validUntil, ' ', 0, ai_fullname, 2,
                    ai_password);
            EXCEPTION
                WHEN OTHERS THEN
                    ibs_error.log_error (ibs_error.error,
                        'p_User_01$createFast.get group',
                        'OTHER error for domain ' || ai_domainId);
                    RAISE;
            END;
        END IF; -- if user created successfully

    EXCEPTION
        WHEN NO_DATA_FOUND THEN         -- domain does not exist
            NULL;
        WHEN OTHERS THEN
            ibs_error.log_error (ibs_error.error,
                'p_User_01$createFast.get Jeder',
                'OTHER error for domain ' || ai_domainId);
            RAISE;
    END;

    COMMIT WORK;

    -- return the state value:
    RETURN  p_User_01$createFast.l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_User_01$createFast',
            'Input: ai_userId = ' || ai_userId || 
            ', ai_domainId = ' || ai_domainId || 
            ', ai_username = ' || ai_username || 
            ', ai_password = ' || ai_password || 
            ', ai_fullname = ' || ai_fullname || 
            ', ao_oid = ' || ao_oid || 
            '; sqlcode = ' || SQLCODE || 
            ', sqlerrm = ' || SQLERRM); 
        -- return error code:
        RETURN c_NOT_OK;
END p_User_01$createFast;
/

show errors;

EXIT;
