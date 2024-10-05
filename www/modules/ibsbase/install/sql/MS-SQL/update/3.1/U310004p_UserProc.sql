/******************************************************************************
 * [IBS-718] Updates the user procedure to create a user object fast <BR>
 *
 * @author      Roland Burgermann (RB) 20120213
 ******************************************************************************
 */

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
-- delete existing procedure:
EXEC p_dropProc N'p_User_01$createFast'
GO

-- create the new procedure:
CREATE PROCEDURE p_User_01$createFast
(
    -- input parameters:
    @userId         USERID,
    @domainId       DOMAINID,
    @username       NAME,
    @password       NAME,
    @fullname       NAME,
    -- output parameters:
    @oid            OBJECTID = 0x0000000000000000 OUTPUT
)
AS
    -- definitions:
    -- define return constants:
    DECLARE @ALL_RIGHT INT, @ALREADY_EXISTS INT
    -- set constants:
    SELECT  @ALL_RIGHT = 1, @ALREADY_EXISTS = 21
    -- define return values:
    DECLARE @retValue INT                   -- return value of this procedure
    -- initialize return values:
    SELECT  @retValue = @ALL_RIGHT
    -- define local variables:
    DECLARE @containerId OBJECTID, @containerId_s OBJECTIDSTRING,
            @oid_s OBJECTIDSTRING,
            @groupId GROUPID, @groupOid OBJECTID, 
            @validUntil DATETIME
    -- initialize local variables:


    -- body:
    BEGIN TRANSACTION
        -- get user container:
        SELECT  @containerId = usersOid
        FROM    ibs_Domain_01
        WHERE   id = @domainId

        -- check if the domain was found:
        IF (@@ROWCOUNT > 0)             -- the domain was found?
        BEGIN
            -- convert container oid to string representation:
            EXEC p_byteToString @containerId, @containerId_s OUTPUT


            -- create the user:
            EXEC @retValue = p_User_01$performCreate @userId, 0x00000001, 0x010100A1, 
                    @username, @containerId_s, 1,
                    0, '0x0000000000000000', N'', NULL,
                    @oid_s OUTPUT

            -- convert user oid string to oid representation:
            EXEC p_stringToByte @oid_s, @oid OUTPUT

            -- check if there was an error during creation:
            IF (@retValue = @ALL_RIGHT) -- user created successfully?
            BEGIN
                -- set valid Time to one year
                SELECT  @validUntil = DATEADD (month, 12, getDate ())
                -- store user specific data:
                EXEC p_User_01$change @oid_s, @userId, 0x00000001, @username, 
                        @validUntil, '', 0, @fullname, 2, @password, 0
            END -- if user created successfully
        END -- if the domain was found
    COMMIT TRANSACTION

    -- return the state value
    RETURN  @retValue
GO
-- p_User_01$createFast