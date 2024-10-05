/******************************************************************************
 * Deletes an object and all its values (incl. rights check). <BR>
 * This procedure also delets all links showing to this object.
 *
 * @input parameters:
 * @param   @oid_s              ID of the object to be deleted.
 * @param   @userId             ID of the user who is deleting the object.
 * @param   @op                 Operation to be performed (used for rights
 *                              check).
 *
 * @output parameters:
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_UserProfile_01$delete'
GO

-- create the new procedure:
CREATE PROCEDURE p_UserProfile_01$delete
(
    @oid_s          OBJECTIDSTRING,
    @userId         USERID,
    @op             INT
)
AS
    -- conversions (objectidstring) - all input objectids must be converted
    DECLARE @oid OBJECTID
    EXEC    p_stringToByte @oid_s, @oid OUTPUT

    -- definitions:
    -- define return constants
    DECLARE @INSUFFICIENT_RIGHTS INT, @ALL_RIGHT INT, @OBJECTNOTFOUND INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @INSUFFICIENT_RIGHTS = 2,   -- return values
            @OBJECTNOTFOUND = 3
    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT

    -- body:
    BEGIN TRANSACTION
        -- delete base object:
        EXEC @retValue = p_Object$performDelete @oid_s, @userId, @op,
                @oid OUTPUT

/* KR should not be done because of undo functionality!
        IF (@retValue = @ALL_RIGHT)     -- operation properly performed?
        BEGIN
            -- delete object specific data:
            -- (deletes all type specific tuples which are not within ibs_Object)
            DELETE  ibs_UserProfile
            WHERE   oid = @oid
        END -- if operation properly performed
*/
    COMMIT TRANSACTION

    -- return the state value:
    RETURN  @retValue
GO
-- p_UserProfile_01$delete
