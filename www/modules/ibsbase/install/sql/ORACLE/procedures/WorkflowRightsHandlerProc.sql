/******************************************************************************
 * All stored procedures regarding the workflow service. <BR>
 *
 * @version     2.21.0002, 21.06.2002 KR
 *
 * @author      Horst Pichler, 5.10.2000
 ******************************************************************************
 */

/******************************************************************************
 * Set the rights for a given user/group on given object handled by the 
 * workflow. <BR> This procedure is only a wrapper to call p_Rights$setRights 
 * from JAVA.
 * 
 * @input parameters:
 * @param   @ai_oid_s           ID of the object to be deleted.
 * @param   @ai_userId          ID of the user who is deleting the object.
 * @param   @ai_rights          New rights (if 0 rights entry will be deleted)
 * @param   @ai_rec             Set rights recursive?
 *                              (1 true, 0 false).
 *
 * @output parameters:          problem: called procedure provides no error-level
 *
 */
CREATE OR REPLACE PROCEDURE p_Workflow$setRights
(
    -- common input parameters:
    ai_oid_s           VARCHAR2,
    ai_rPersonId       INTEGER,
    ai_rights          INTEGER,
    ai_rec             NUMBER
)
AS
    -- local variables:
    l_oid              RAW(8);
            
BEGIN
-- initialize local variables:
    p_stringToByte (ai_oid_s, l_oid);
    
-- body:
    p_Rights$setRights (l_oid, ai_rPersonId, ai_rights, ai_rec);
    
    COMMIT WORK;
EXCEPTION
    WHEN OTHERS THEN
        ibs_error.log_error (ibs_error.error, 'p_Workflow$setRights',
            'Input: ' ||
            ', ai_oid_s = ' || ai_oid_s ||
            ', ai_rPersonId = ' || ai_rPersonId ||
            ', ai_rights = ' || ai_rights ||
            ', ai_rec = ' || ai_rec ||                        
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
END p_Workflow$setRights;
/

show errors;
-- p_Workflow$setRights

/**************************************************************************
 * Set the rights-key of the object with the given oid1 (incl. sub-objects) 
 * to the rightskey of the object with the given oid2.<BR>
 * <BR>
 *
 * @input parameters:
 * @param   oid1    oid of the object for which rights-key will be changed
 *                  (incl. sub-objects)
 * @param   oid2    oid of the object of which rights-key will be copied
 *
 * @output paramters:
 * @return  A value representing the state of the procedure.
 *  ALL_RIGHT               Action performed, values returned, everything ok.
 */
CREATE OR REPLACE FUNCTION p_Workflow$copyRightsRec
(
    -- common input parameters:
    ai_oid1_s               VARCHAR2,
    ai_oid2_s               VARCHAR2
)
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_NOOID                 CONSTANT ibs_Object.oid%TYPE := createOid (0, 0);
                                            -- default value for no defined oid
    c_EMPTYPOSNOPATH        CONSTANT ibs_Object.posNoPath%TYPE := '0000';

    -- local variables:
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_rowCount              INTEGER;        -- row counter
    l_oid1                  ibs_Object.oid%TYPE := c_NOOID;
    l_oid2                  ibs_Object.oid%TYPE := c_NOOID;
    l_rKey                  ibs_Object.rKey%TYPE := 0;
    l_posNoPath             ibs_Object.posNoPath%TYPE := c_EMPTYPOSNOPATH;

-- body:
BEGIN
    -- convert oids:
    p_stringToByte (ai_oid1_s, l_oid1);
    p_stringToByte (ai_oid2_s, l_oid2);

    -- get the rkey of the 2nd object:
    BEGIN
        SELECT  rKey 
        INTO    l_rKey
        FROM    ibs_Object
        WHERE   oid = l_oid2;
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'get rKey of 2nd object';
            RAISE;                      -- call common exception handler
    END;
    
    -- get posnopath of 1st object:
    BEGIN
        SELECT  posNoPath
        INTO    l_posNoPath
        FROM    ibs_Object
        WHERE   oid = l_oid1;
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'get posNoPath of 1st object';
            RAISE;                      -- call common exception handler
    END;
    
    --
    -- change rkeys
    --
    -- 1. set new rKey for first object + sub-objects
    BEGIN
        UPDATE  ibs_Object
        SET     rKey = l_rKey
        WHERE   posNoPath LIKE l_posNoPath || '%';
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'set rKey for first object and sub objects';
            RAISE;                      -- call common exception handler
    END;

    --   
    -- 2. set rkey for linked-objects (object + sub-objects)
    BEGIN
        UPDATE  ibs_Object
        SET     rKey = l_rKey
        WHERE   linkedObjectId IN
                (   SELECT  oid 
                    FROM    ibs_Object
                    WHERE   posNoPath LIKE l_posNoPath || '%'
                );
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'set rKey for linked objects und their sub objects';
            RAISE;                      -- call common exception handler
    END;

    -- return the state value:
    RETURN l_retValue;

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid1_s = ' || ai_oid1_s ||
            ', ai_oid2_s = ' || ai_oid2_s ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_Workflow$copyRightsRec', l_eText);
        -- set error code:
        IF (l_retValue = c_ALL_RIGHT)    -- no error code set?
        THEN
            l_retValue := c_NOT_OK;
        END IF; -- if no error code set
        -- return error code:
        RETURN l_retValue;
END p_Workflow$copyRightsRec;
/

show errors;


EXIT;
