/******************************************************************************
 * Stored procedures to set rights via import . <BR>
 *
 * @version     2.30.0006, 05.09.2002 KR
 *
 * @author      Bernd Buchegger (BB)  990519
 ******************************************************************************
 */


/******************************************************************************
 * Sets isRecursive for a user or a group for a specific object . <BR>
 * Additionally all isRecursive set for the object can be deleted first.
 * The reference to the user or the group to set the isRecursive for will be
 * done though the name of the user or the group. This expects that there
 * are no ambiguous user or group names in the system.<BR>
 *
 * @input parameters:
 * @param   ai_oid_s            oid of the object to set the isRecursive for
 * @param   ai_name             name of the user or the group
 * @param   ai_isUser           flag to set isRecursive for user or otherwise group
 * @param   ai_isRecursive           isRecursive to set
 * @param   ai_isRecursive      Flag to set the isRecursive recursively.
 * @param   ai_deleteisRecursive     flag to first delete all isRecursive set for the
 *                              object 
 * @param   ai_domainId         id of domain to look for the user or the group
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  NOT_OK                  An Error occured
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 */
-- delete existing procedure:
EXEC p_dropProc N'p_Integrator$setImportRights'
GO
-- create the new procedure:
CREATE PROCEDURE p_Integrator$setImportRights
(
    -- input parameters:
    @ai_oid_s               OBJECTIDSTRING,
    @ai_name                NAME,
    @ai_isUser              BOOL,
    @ai_rights              RIGHTS,
    @ai_isRecursive         BOOL,
    @ai_deleteRights        BOOL,
    @ai_domainId            int    
    -- output parameters:
)
AS
DECLARE
    -- constants:
    @c_NOT_OK               INT,             -- something went wrong
    @c_ALL_RIGHT            INT,             -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,             -- not enough rights for this
                                             -- operation
    @c_OBJECTNOTFOUND       INT,             -- the object was not found                                        
    @c_ALREADY_EXISTS       INT,             -- the object already exists
    @c_CONT_PARTOF          INT,             -- containerKind part of

    -- local variables:
    @l_retValue             INT,             -- return value of a function
    @l_error                INT,             -- the actual error code
    @l_ePos                 NVARCHAR (2000), -- error position description
    @l_rowCount             INT,             -- row counter
    @l_rowCount2            INT,             -- row counter
    @l_oid                  OBJECTID,        -- oid of the object for which to
                                             -- set the rights
    @l_personId             INT              -- id of user or group for which to
                                             -- set the rights

    -- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_OBJECTNOTFOUND       = 3,
    @c_ALREADY_EXISTS       = 21,
    @c_CONT_PARTOF          = 2

    -- initialize local variables:
SELECT
    @l_retValue = @c_ALL_RIGHT,
    @l_error = 0,
    @l_rowCount = 0

-- body:
    -- conversions (objectidstring) - all input objectids must be converted:
    EXEC    p_stringToByte @ai_oid_s, @l_oid OUTPUT

    -- now check if we have to set the right for a user else we set it for a
    -- group 
    IF (@ai_isUser = 1)
    BEGIN
        SELECT  @l_personId = min (u.id), @l_rowCount = count (*)
        FROM    ibs_User u, ibs_Object o
        WHERE   u.name = @ai_name
            AND u.domainId = @ai_domainId
            AND o.oid = u.oid
            AND o.state = 2
            AND o.isLink = 0
    END -- if
    ELSE
    BEGIN        
        SELECT  @l_personId = min (g.id), @l_rowCount = count (*)
        FROM    ibs_Group g, ibs_Object o
        WHERE   g.name = @ai_name
            AND g.domainId = @ai_domainId
            AND o.oid = g.oid
            AND o.state = 2
            AND o.isLink = 0
    END -- else

    -- check if there occurred an error:
    EXEC @l_error = ibs_error.prepareErrorCount @@error, @@ROWCOUNT,
        N'user or group could not have been found or was not unique',
        @l_ePos OUTPUT, @l_rowCount2 OUTPUT
    IF (@l_error <> 0 OR @l_rowCount <> 1 OR @l_rowCount2 <> 1)
                                        -- an error occurred?
        -- user or group could not have been found or was not unique
        GOTO exception              -- call common exception handler

    BEGIN TRANSACTION 

        -- check if rights of the object need to be deleted first 
        IF (@ai_deleteRights = 1)
        BEGIN
            -- check if the rights shall be deleted recursively:
            IF (@ai_isRecursive = 1)    -- delete recursively?
            BEGIN
                -- delete all rights set for the objekt without checking the
                -- rights:
                EXEC p_Rights$deleteObjectRightsRec @l_oid
            END -- if delete recursively
            ELSE                        -- delete just for actual object
            BEGIN
                -- delete all rights set for the objekt without checking the
                -- rights:
                EXEC p_Rights$deleteObjectRights @l_oid
            END -- else delete just for actual object
        END -- if

        -- now add the specific right
        EXEC p_Rights$setRights @l_oid, @l_personId, @ai_rights, @ai_isRecursive

    COMMIT TRANSACTION    
    -- return the state value:
    RETURN  @l_retValue

exception:                              -- an error occurred
    -- log the error:
    EXEC ibs_error.logError 500, N'p_Integrator$setImportRights', @l_error,
            @l_ePos,
            N'ai_rights', @ai_rights,
            N'ai_oid_s', @ai_oid_s,
            N'ai_isUser', @ai_isUser,
            N'ai_name', @ai_name,
            N'ai_isRecursive', @ai_isRecursive,
            N'', N'',
            N'ai_deleteRights', @ai_deleteRights,
            N'', N'',
            N'ai_domainId', @ai_domainId

    -- return error code:
    RETURN  @c_NOT_OK
GO 
-- p_Integrator$setImportRights

print 'p_Integrator$setImportRights created'
