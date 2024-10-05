/******************************************************************************
 * Stored procedure to set rights via import . <BR>
 * 
 * @version     2.30.0007, 06.09.2002 KR
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
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_Integrator$setImportRights
(
    -- input parameters:
    ai_oid_s                VARCHAR2,
    ai_name                 VARCHAR2,
    ai_isUser               NUMBER,
    ai_rights               INTEGER,
    ai_isRecursive          NUMBER,
    ai_deleteRights         NUMBER,
    ai_domainId             INTEGER    
    -- output parameters:
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                INTEGER := 0;
    c_ALL_RIGHT             INTEGER := 1;

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT;
    l_oid                   RAW(8);
    l_personId              INTEGER;

-- body:
BEGIN
    -- initializations:
    -- conversions (objectidstring) - all input objectids must be converted:
    p_stringToByte (ai_oid_s, l_oid);

    -- check if rights of the object need to be deleted first 
    IF (ai_deleteRights = 1)
    THEN
        -- check if the rights shall be deleted recursively:
        IF (ai_isRecursive = 1)         -- delete recursively?
        THEN
            -- delete all rights set for the objekt without checking the
            -- rights:
            p_Rights$deleteObjectRightsRec (l_oid);
        -- if delete recursively
        ELSE                            -- delete just for actual object
            -- delete all rights set for the objekt without checking the
            -- rights:
            p_Rights$deleteObjectRights (l_oid);
        END IF; -- else delete just for actual object
    END IF;
    
    -- now check if we have to set the right for a user else we set it for a group 
    BEGIN
        IF (ai_isUser = 1)
        THEN
            SELECT  u.id
            INTO    l_personId
            FROM    ibs_user u, ibs_object o
            WHERE   u.name = ai_name
            AND     u.domainId = ai_domainId        
            AND     o.oid = u.oid
            AND     o.state = 2
            AND     o.isLink = 0;
        ELSE            
            SELECT  g.id
            INTO    l_personId
            FROM    ibs_group g, ibs_object o
            WHERE   g.name = ai_name
            AND     g.domainId = ai_domainId        
            AND     o.oid = g.oid
            AND     o.state = 2
            AND     o.isLink = 0;
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            RETURN c_NOT_OK;
    END;
    
    -- check if according row has been found
    IF (SQL%ROWCOUNT = 1)    -- 1 row found?
    THEN
        -- now add the specific right
        p_Rights$setRights (l_oid, l_personId, ai_rights, ai_isRecursive);
        -- warning: changed l_rights to ai_rights (AT, 990614)
    ELSE
        -- user or group could not have been found or was not unique
        l_retValue := c_NOT_OK;
    END IF;
    
    COMMIT WORK;

    -- return the state value:
    RETURN  l_retValue;
    
EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error ( ibs_error.error, 'p_Integrator$setImportRights',
            'ai_oid_s = ' || ai_oid_s ||
            ', ai_name  = ' || ai_name ||
            ', ai_isUser = ' || ai_isUser ||
            ', ai_rights = ' || ai_rights ||
            ', ai_isRecursive = ' || ai_isRecursive ||
            ', ai_deleteRights = ' || ai_deleteRights ||
            ', ai_domainId = ' || ai_domainId);
        RETURN c_NOT_OK;    
END p_Integrator$setImportRights;
/

show errors;

exit;
