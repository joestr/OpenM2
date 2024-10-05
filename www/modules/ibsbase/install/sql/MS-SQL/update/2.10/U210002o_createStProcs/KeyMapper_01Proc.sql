/******************************************************************************
 * All stored procedures regarding the KeyMapper Object. <BR>
 *
 * @version     2.2.1.0004, 21.03.2002 KR
 *
 * @author      Bernd Buchegger (CK)  990316
 ******************************************************************************
 */


/******************************************************************************
 * Creates a new KeyMapper Object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @oid                oid of the business object
 * @param   @id                 external object id
 * @param   @idDomain           id domain of the external id   
 *
 * @output parameters:
 *
 * @returns A value representing the state of the procedure.
 *  NOT_OK                  An Error occured
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_KeyMapper$new'
GO

-- ALTER  the new procedure:
CREATE PROCEDURE p_KeyMapper$new
(
    -- input parameters:
    @oid_s          OBJECTIDSTRING,
    @id             NVARCHAR (255),
    @idDomain       NVARCHAR (63)
    -- output parameters:
)
AS
    -- declarations:
    DECLARE @oid OBJECTID
    DECLARE @foundOid OBJECTID
    DECLARE @rowCount INT
    -- define return constants:
    DECLARE @NOT_OK INT, @ALL_RIGHT INT
    -- define return values:
    DECLARE @retValue INT               -- return value of this procedure

    -- initializations:
    -- conversions (objectidstring) - all input objectids must be converted:
    EXEC    p_stringToByte @oid_s, @oid OUTPUT
    -- set constants:
    SELECT  @NOT_OK = 0, @ALL_RIGHT = 1
    -- initialize return values:
    SELECT  @retValue = @ALL_RIGHT

    -- check if this ID/IDDOMAIN combination does already exist
    SELECT  @foundOid = k.oid
    FROM    ibs_KeyMapper k, ibs_Object o
    WHERE   k.id = @id
    AND     k.idDomain = @idDomain
    AND     o.oid = k.oid
    AND     o.state = 2

    -- check if an oid has been found
    IF (@@ROWCOUNT >= 1)                -- at least one row affected?
    BEGIN
        -- check the the external key is already attached to another object
        IF (@foundOid != @oid)
            SELECT  @retValue = @NOT_OK  -- set return value
        -- else everything ok. relation is already stored
    END
    ELSE
    BEGIN
        -- check if an entry for this OID already exists
        SELECT  k.oid
        FROM    ibs_KeyMapper k
        WHERE   k.oid = @oid

        -- @@ROWCOUNT seems to only can be used once
	    SELECT @rowCount = @@ROWCOUNT

    	IF (@rowCount = 0)                -- at least one row affected?
        BEGIN
            -- insert the values:
            INSERT INTO ibs_KeyMapper (oid, id, idDomain)
            VALUES  (@oid, @id, @idDomain)
        END
        ELSE IF (@rowCount = 1)
        BEGIN
            -- insert the values:
            UPDATE ibs_KeyMapper
            SET id = @id,
                idDomain = @idDomain
            WHERE oid = @oid
        END
        ELSE
        BEGIN
           SELECT  @retValue = @NOT_OK  -- set return value
        END
    END
    -- return the state value:
    RETURN  @retValue
GO 
-- p_KeyMapper$new

print 'p_KeyMapper$new created'

/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 * @input parameters:
 * @param   @id                 id of the external Object
 * @param   @idDomain           id domain of the external object
 *
 * @output parameters:
 * @param   @oid                oid of the related object.
 
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS     User has no right to perform action.
 *  OBJECTNOTFOUND          The required object was not found within the 
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_KeyMapper$getOid'
GO

-- create the new procedure:
CREATE PROCEDURE p_KeyMapper$getOid
(
    -- input parameters:
    @id             NVARCHAR (255),
    @idDomain       NVARCHAR (63),
    -- output parameters
    @oid            OBJECTID      OUTPUT
)
AS
    -- declarations:
    -- define return constants:
    DECLARE @NOT_OK INT, @ALL_RIGHT INT
    -- define return values:
    DECLARE @retValue INT               -- return value of this procedure

    -- initializations:
    -- set constants:
    SELECT  @NOT_OK = 0, @ALL_RIGHT = 1
    -- initialize return values:
    SELECT  @retValue = @NOT_OK        

    -- retrieve oid:
    SELECT  @oid = k.oid
    FROM    ibs_KeyMapper k, ibs_Object o
    WHERE   k.id = @id
    AND     k.idDomain = @idDomain
    AND     k.oid = o.oid
    AND     o.state = 2
 
    -- check if insert was performed correctly:
    IF (@@ROWCOUNT >= 1)                -- at least one row affected?   
        SELECT  @retValue = @ALL_RIGHT  -- set return value
        
    -- return the state value:
    RETURN  @retValue
GO 
-- p_KeyMapper$getOid

print 'p_KeyMapper$getOid created'


/******************************************************************************
 * Gets all data from a given object. <BR>
 *
 * @input parameters:
 * @param   @oid                oid of the related object.
 *
 * @output parameters:
 * @param   @id                 id of the external Object
 * @param   @idDomain           id domain of the external object
 *
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  NOT_OK                  The required object was not found within the 
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_KeyMapper$getDomainID'
GO

-- create the new procedure:
CREATE PROCEDURE p_KeyMapper$getDomainID
(
    -- input parameters
    @oid_s          OBJECTIDSTRING,
    -- output parameters:
    @id             NVARCHAR (255) OUTPUT,
    @idDomain       NVARCHAR (63)  OUTPUT
)
AS
    -- declarations:
    -- define return constants:
    DECLARE @NOT_OK INT, @ALL_RIGHT INT
    -- define return values:
    DECLARE @retValue INT               -- return value of this procedure
    DECLARE @oid OBJECTID

    -- initializations:
    -- set constants:
    SELECT  @NOT_OK = 0, @ALL_RIGHT = 1
    -- initialize return values:
    SELECT  @retValue = @NOT_OK
    -- convertions: (OBJECTIDSTRING) - all input objectids must be converted
    EXEC    p_stringToByte @oid_s, @oid OUTPUT


    -- retrieve id and domain:
    SELECT  @id = k.id, @idDomain = k.idDomain
    FROM    ibs_KeyMapper k
    WHERE   k.oid = @oid
 
    -- check if insert was performed correctly:
    IF (@@ROWCOUNT = 1)                 -- exactly one row affected?
        SELECT  @retValue = @ALL_RIGHT  -- set return value
        
    -- return the state value:
    RETURN  @retValue
GO 
-- p_KeyMapper$getDomainID


print 'p_KeyMapper$getDomianID created'


/******************************************************************************
 * Moves all EXTKEYs of the business objects below the given posNoPath
 * from the KeyMapper table to archive table.<BR>
 *
 * @input parameters:
 * @param   @posNoPath      the posNoPath of the toplevel object
 *
 * @returns A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 *  NOT_OK                  The required object was not found within the 
 *                          database.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_KeyMapper$archiveExtKeys'
GO

-- create the new procedure:
CREATE PROCEDURE p_KeyMapper$archiveExtKeys
(
    -- input parameters
    @posNoPath      POSNOPATH_VC
)
AS
    -- declarations:
    -- define return constants:
    DECLARE @NOT_OK INT, @ALL_RIGHT INT
    -- define return values:
    DECLARE @retValue INT               -- return value of this procedure

    -- initializations:
    -- set constants:
    SELECT  @NOT_OK = 0, @ALL_RIGHT = 1
    -- initialize return values:
    SELECT  @retValue = @NOT_OK

    -- copy the extkeys from the ibs_KeyMapper table
    -- to the ibs_KeyMapperArchive table
    INSERT INTO ibs_KeyMapperArchive (oid, id, idDomain)
        SELECT m.oid, m.id, m.idDomain
        FROM ibs_KeyMapper m, ibs_Object o
        WHERE o.oid = m.oid
        AND o.posNoPath LIKE @posNoPath + '%'

    -- delete the copied tuples from the ibs_KeyMapper table
    DELETE FROM ibs_KeyMapper
    WHERE oid IN
        (SELECT m.oid
         FROM ibs_Keymapper m, ibs_Object o
         WHERE o.oid = m.oid
         AND o.posNoPath LIKE @posNoPath + '%')

    -- return the state value:
    RETURN  @ALL_RIGHT
GO 
-- p_KeyMapper$archiveExtKeys

print 'p_KeyMapper$archiveExtKeys created'
