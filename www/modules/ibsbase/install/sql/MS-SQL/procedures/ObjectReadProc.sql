/******************************************************************************
 * All stored procedures regarding the ibs_ObjectRead table. <BR>
 * 
 * @version     1.10.0001, 02.08.1999
 *
 * @author      Klaus Reimüller (KR)  980614
 *
 * <DT><B>Updates:</B>
 * <DD>HB 990802    Code cleaning.
 * 
 ******************************************************************************
 */



/******************************************************************************
 * Sets a business object as already read by the user. <BR>
 *
 * @input parameters:
 * @param   @oid                ID of the object to be changed.
 * @param   @userId             ID of the user who has read the object.
 *
 * @output parameters:
 * @param   @state              The object's state.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- delete existing procedure:
IF EXISTS ( SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.p_setRead2') 
                AND sysstat & 0xf = 4)
	DROP PROCEDURE #CONFVAR.ibsbase.dbOwner#.p_setRead2
GO

-- create the new procedure:
CREATE PROCEDURE p_setRead2
(
    -- input parameters:
    @oid            OBJECTID,
    @userId         USERID
)
AS
    -- definitions:
    -- define return constants
    DECLARE @ALL_RIGHT INT, @OBJECTNOTFOUND INT
    -- set constants
    SELECT  @ALL_RIGHT = 1, @OBJECTNOTFOUND = 3
    -- define return values
    DECLARE @retValue INT               -- return value of this procedure
    -- initialize return values
    SELECT  @retValue = @ALL_RIGHT


    -- set object as already read:
    UPDATE  ibs_ObjectRead
    SET     hasRead = 1,
            lastRead = getDate ()
    WHERE   oid = @oid
        AND userId = @userId

    IF (@@ROWCOUNT <= 0)        -- no entry in ibs_ObjectRead table?
    BEGIN
        -- create new entry:
        INSERT  INTO ibs_ObjectRead (oid, userId, hasRead, lastRead)
        VALUES  (@oid, @userId, 1, getDate ())
    END -- no entry in ibs_ObjectRead table

    -- return the state value
    RETURN  @retValue
GO
-- p_setRead2

/******************************************************************************
 * Sets a business object as already read by the user. <BR>
 *
 * @input parameters:
 * @param   @oid                ID of the object to be changed.
 * @param   @userId             ID of the user who has read the object.
 *
 * @output parameters:
 * @param   @state              The object's state.
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- delete existing procedure:
IF EXISTS ( SELECT  * 
            FROM    sysobjects 
            WHERE   id = object_id ('#CONFVAR.ibsbase.dbOwner#.p_setRead') 
                AND sysstat & 0xf = 4)
	DROP PROCEDURE #CONFVAR.ibsbase.dbOwner#.p_setRead
GO

-- create the new procedure:
CREATE PROCEDURE p_setRead
(
    -- input parameters:
    @oid            OBJECTID,
    @userId         USERID
)
AS
    -- definitions:
    -- constants
    DECLARE
        @ALL_RIGHT      INT, 
        @OBJECTNOTFOUND INT
    SELECT 
        @ALL_RIGHT = 1, 
        @OBJECTNOTFOUND = 3
    -- local variables
    DECLARE 
        @retValue       INT             -- return value of this procedure

    -- initializations
    SELECT  @retValue = @ALL_RIGHT

    -- set read for actual object:
    EXEC @retValue = p_setRead2 @oid, @userId

    -- update existing entries for references to the object:
    UPDATE  ibs_ObjectRead
    SET     hasRead = 1,
            lastRead = getDate ()
    WHERE   oid IN 
            (
                SELECT  oid 
                FROM    ibs_Object
                WHERE   linkedObjectId = @oid
            )
        AND userId = @userId

    -- create new entries for references to the object:
    INSERT  INTO ibs_ObjectRead (oid, userId, hasRead, lastRead)
    SELECT  oid, @userId, 1, getDate ()
    FROM    ibs_Object
    WHERE   linkedObjectId = @oid
        AND oid NOT IN
            (
                SELECT  oid
                FROM    ibs_ObjectRead
                WHERE   userId = @userId
            )

    -- return the state value
    RETURN  @retValue
GO
-- p_setRead

