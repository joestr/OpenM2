/******************************************************************************
 * Stored procedures to set rights via import . <BR>
 *
 * @version     2.30.0006, 05.09.2002 KR
 *
 * @author      Bernd Buchegger (BB)  990519
 ******************************************************************************
 */


/******************************************************************************
 * Sets rights for a user or a group for a specific object . <BR>
 * Additionally all rights set for the object can be deleted first.
 * The reference to the user or the group to set the rights for will be
 * done though the name of the user or the group. This expects that there
 * are no ambiguous user or group names in the system.<BR>
 *
 * @input parameters:
 * @param   ai_oid_s            oid of the object to set the rights for
 * @param   ai_name             name of the user or the group
 * @param   ai_isUser           flag to set rights for user or otherwise group
 * @param   ai_rights           rights to set
 * @param   ai_isRecursive      Flag to set the rights recursively.
 * @param   ai_deleteRights     flag to first delete all rights set for the
 *                              object 
 * @param   ai_domainId         id of domain to look for the user or the group
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 *  NOT_OK                  An Error occured
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 */
-- delete existing procedure:
EXEC p_dropProc 'p_Integrator$setImportRights'
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
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_INSUFFICIENT_RIGHTS  INT,            -- not enough rights for this
                                            -- operation
    @c_OBJECTNOTFOUND       INT,            -- the object was not found                                        
    @c_ALREADY_EXISTS       INT,            -- the object already exists
    @c_CONT_PARTOF          INT,            -- containerKind part of

    -- local variables:
    @l_retValue             INT,            -- return value of a function
    @l_error                INT,            -- the actual error code
    @l_ePos                 VARCHAR (2000), -- error position description
    @l_rowCount             INT,            -- row counter
    @l_oid                  OBJECTID,       -- oid of the object for which to
                                            -- set the rights
    @l_personId             INT             -- id of user or group for which to
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

        -- check if we got the user or the group
        IF (@l_rowCount = 1)
        BEGIN
            -- now add the specific right
            EXEC p_Rights$setRights @l_oid, @l_personId, @ai_rights, @ai_isRecursive
        END
        ELSE
        BEGIN
            -- user or group could not have been found or was not unique
            SELECT @l_retValue = @c_NOT_OK
        END

    COMMIT TRANSACTION    
    -- return the state value:
    RETURN  @l_retValue
GO 
-- p_Integrator$setImportRights

print 'p_Integrator$setImportRights created'
