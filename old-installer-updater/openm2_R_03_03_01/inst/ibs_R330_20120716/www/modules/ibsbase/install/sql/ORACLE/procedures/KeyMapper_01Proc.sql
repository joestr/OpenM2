/******************************************************************************
 * All stored procedures regarding the KeyMapper Object. <BR>
 * 
 * @version     2.2.1.0006, 22.03.2002 KR
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
 ******************************************************************************
 */
CREATE OR REPLACE FUNCTION p_KeyMapper$new
(
    -- input parameters:
    ai_oid_s            VARCHAR2,
    ai_id               VARCHAR2,
    ai_idDomain         VARCHAR2
    -- output parameters:
)    
RETURN INTEGER
AS
    -- declarations:
    -- error messages
    StoO_rowcnt         INTEGER;
    StoO_error 	        INTEGER;
    StoO_errmsg	        VARCHAR2(255);
    -- locals
    l_oid               RAW(8);
    l_foundOid          RAW(8);
    -- define return constants
    c_NOT_OK            INTEGER := 0;
    c_ALL_RIGHT         INTEGER := 1;
    -- define return values
    l_retValue          INTEGER := c_ALL_RIGHT;
    l_rowCount              INTEGER := 0;   -- row counter

-- body:
BEGIN
    -- initializations:
    -- conversions (objectidstring) - all input objectids must be converted:
    p_stringToByte (ai_oid_s, l_oid);

    BEGIN
        -- count rows; did not find the rowcount facility ... please replace!
        SELECT  k.oid
        INTO    l_foundOid
        FROM    ibs_KeyMapper k, ibs_Object o
        WHERE   k.id = ai_id
          AND   k.idDomain = ai_idDomain
          AND   k.oid = o.oid
          AND   o.state = 2;
        StoO_rowcnt := SQL%ROWCOUNT;
    EXCEPTION
    WHEN TOO_MANY_ROWS THEN
        StoO_error  := SQLCODE;
        StoO_errmsg := SQLERRM;
    WHEN NO_DATA_FOUND THEN
        StoO_error  := SQLCODE;
        StoO_errmsg := SQLERRM;
    WHEN OTHERS THEN
        RAISE; 
    END;

    -- check if according row(s) has(have) been found
    IF (StoO_rowcnt > 0)    -- at least one row found?
    THEN
        -- check the the external key is already attached to another object
        IF (l_foundOid != l_oid)
        THEN
            l_retValue := c_NOT_OK;  -- set return value
        END IF;
        -- else everything ok. relation is already stored            
    ELSE
        -- check if an entry for this OID already exists
        BEGIN
            SELECT  COUNT (k.oid)
            INTO    l_rowCount
            FROM    ibs_KeyMapper k
            WHERE   k.oid = l_oid;
            StoO_rowcnt := SQL%ROWCOUNT;            
        EXCEPTION
        WHEN TOO_MANY_ROWS THEN
            StoO_error  := SQLCODE;
            StoO_errmsg := SQLERRM;
        WHEN NO_DATA_FOUND THEN
            StoO_error  := SQLCODE;
            StoO_errmsg := SQLERRM;
        WHEN OTHERS THEN
            RAISE; 
        END;
        
        IF (StoO_rowcnt = 0)    -- no row found?
        THEN  
            -- insert the values:
            INSERT INTO ibs_KeyMapper (oid, id, idDomain)
            VALUES  (l_oid, ai_id, ai_idDomain);
        ELSIF (StoO_rowcnt = 1)    -- one row found?
        THEN
            -- overwrite the old values
            UPDATE ibs_KeyMapper 
            SET id = ai_id, 
                idDomain = ai_idDomain
            WHERE oid = l_oid;
        ELSE
           l_retValue := c_NOT_OK;  -- set return value
        END IF;                
    END IF;

    COMMIT WORK;

    -- return the state value:
    RETURN  l_retValue;
    
EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error (ibs_error.error, 'p_KeyMapper$new',
            'Input: ai_oid_s = ' || ai_oid_s ||
            ', ai_id = ' || ai_id ||
            ', ai_idDomain = ' || ai_idDomain ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
    -- return error code:
    RETURN c_NOT_OK;
END p_KeyMapper$new;
/

show errors;

/******************************************************************************
 * Gets all data from a given object (incl. rights check). <BR>
 *
 * input parameters:
 * param   id                 id of the external Object
 * param   idDomain           id domain of the external object
 *
 * output parameters:
 * param   oid                oid of the related object.
 *
 * returns A value representing the state of the procedure.
 *  ALL_RIGHT                 Action performed, values returned, everything ok.
 *  INSUFFICIENT_RIGHTS       User has no right to perform action.
 *  OBJECTNOTFOUND            The required object was not found within the 
 *                            database.
 ******************************************************************************
 */
-- create the new procedure:
CREATE OR REPLACE FUNCTION p_KeyMapper$getOid
(
    -- input parameters:
    ai_id             VARCHAR2,
    ai_idDomain       VARCHAR2,
    -- output parameters
    ao_oid            OUT RAW
)
RETURN INTEGER
AS
    -- declarations:
    -- error messages
    StoO_rowcnt	        INTEGER;
    StoO_error 	        INTEGER;
    StoO_errmsg	        VARCHAR2(255);
    -- locals
    l_oid               RAW(8);
    l_foundOid          RAW(8);
    l_rowcount          INTEGER;
    -- define return constants
    c_NOT_OK            INTEGER := 0;
    c_ALL_RIGHT         INTEGER := 1;
    c_NOOID             CONSTANT RAW (8) := hexToRaw ('0000000000000000');
    -- define return values
    l_retValue          INTEGER := c_NOT_OK;
BEGIN
    -- initializations:
    -- set constants:
    c_NOT_OK := 0;
    c_ALL_RIGHT := 1;
    -- initialize return values:
    l_retValue := c_NOT_OK;
    ao_oid := c_NOOID;
   
    BEGIN
        -- count rows; did not find the rowcount facility ... please replace!
        SELECT  k.oid
        INTO    ao_oid
        FROM    ibs_KeyMapper k, ibs_Object o
        WHERE   k.id = ai_id
          AND   k.idDomain = ai_idDomain
          AND   k.oid = o.oid
          AND   o.state = 2;
        StoO_rowcnt := SQL%ROWCOUNT; 
      EXCEPTION
      WHEN TOO_MANY_ROWS THEN
          StoO_error  := SQLCODE;
          StoO_errmsg := SQLERRM;
      WHEN NO_DATA_FOUND THEN
          StoO_error  := SQLCODE;
          StoO_errmsg := SQLERRM;
      WHEN OTHERS THEN
          RAISE; 
      END;

    IF (StoO_rowcnt > 0)    -- at least one row found?
    THEN
        l_retValue := c_ALL_RIGHT;  -- set return value
    END IF;
        
    COMMIT WORK;

    -- return the state value:
    RETURN  l_retValue;

EXCEPTION
  WHEN OTHERS THEN
    StoO_error  := SQLCODE;
    StoO_errmsg := SQLERRM;
    ibs_error.log_error (ibs_error.error, 'p_KeyMapper$getOid',
            'Input: ai_id = ' || ai_id ||
            ', ai_idDomain = ' || ai_idDomain ||
            '; sqlcode = ' || SQLCODE || ' sqlerrm = ' || SQLERRM);
    -- return error code:
    RETURN c_NOT_OK;
END p_KeyMapper$getOid;
/

show errors;


/******************************************************************************
 * Gets all data from a given object. <BR>
 *
 * @input parameters:
 * @param   ai_oid_s            Oid of the related object.
 *
 * @output parameters:
 * @param   ao_id               Id of the external Object.
 * @param   ao_idDomain         Id domain of the external object.
 *
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_NOT_OK                 The required object was not found within the 
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_KeyMapper$getDomainID
(
    -- input parameters:
    ai_oid_s            VARCHAR2,
    -- output parameters:
    ao_id               OUT VARCHAR2,
    ao_idDomain         OUT VARCHAR2
)    
RETURN INTEGER
AS
    -- constants:
    c_NOT_OK 			    CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT 		    CONSTANT INTEGER := 1; -- everything was o.k.

    -- local variables:
    l_retValue              INTEGER := c_NOT_OK; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_oid                   RAW (8);

-- body:
BEGIN
    -- initializations:
    -- conversions (objectidstring) - all input objectids must be converted:
    p_stringToByte (ai_oid_s, l_oid);

    BEGIN
        SELECT  k.id, k.idDomain
        INTO    ao_id, ao_idDomain
        FROM    ibs_KeyMapper k
        WHERE   k.oid = l_oid;

        -- at this point we know that exactly one tuple was found
        l_retValue := c_ALL_RIGHT;      -- set return value
    EXCEPTION
        WHEN TOO_MANY_ROWS THEN
            -- create error entry:
            l_ePos := 'too many rows in select';
            RAISE;                      -- call common exception handler
        WHEN NO_DATA_FOUND THEN
            -- create error entry:
            l_ePos := 'no data found in select';
            RAISE;                      -- call common exception handler
        WHEN OTHERS THEN
            -- create error entry:
            l_ePos := 'error in select';
            RAISE;                      -- call common exception handler
    END;

    COMMIT WORK;

    -- return the state value:
    RETURN  l_retValue;
    
EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_oid_s = ' || ai_oid_s  ||
            ', ao_id = ' || ao_id  ||
            ', ao_idDomain = ' || ao_idDomain ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_KeyMapper$getDomainID', l_eText);
        -- return error code:
        RETURN c_NOT_OK; 
END p_KeyMapper$getDomainID;
/
show errors;


/******************************************************************************
 * Moves all EXTKEYs of the business objects below the given posNoPath
 * from the KeyMapper table to archive table.<BR>
 *
 * @input parameters:
 * @param   ai_posNoPath        The posNoPath of the toplevel object.
 *
 * @output parameters:
 * @return  A value representing the state of the procedure.
 * c_ALL_RIGHT              Action performed, values returned, everything ok.
 * c_NOT_OK                 The required object was not found within the 
 *                          database.
 */
CREATE OR REPLACE FUNCTION p_KeyMapper$archiveExtKeys
( 
    -- input parameters: 
    ai_posNoPath            VARCHAR2
) 
RETURN INTEGER 
AS 
    -- constants: 
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
 
    -- local variables: 
    l_retValue              INTEGER := c_NOT_OK; -- return value of function
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_count                 INTEGER := 0;   -- counter
     
-- body:
BEGIN
    BEGIN
        -- copy the extkeys from the ibs_KeyMapper table
        -- to the ibs_KeyMapperArchive table
        INSERT INTO ibs_KeyMapperArchive (oid, id, idDomain)
        SELECT  m.oid, m.id, m.idDomain
        FROM    ibs_KeyMapper m, ibs_Object o
        WHERE   o.oid = m.oid
            AND o.posNoPath LIKE ai_posNoPath || '%';

        BEGIN
            -- delete the copied tuples from the ibs_KeyMapper table
            DELETE  ibs_KeyMapper
            WHERE   oid IN
                    (   SELECT  m.oid
                        FROM    ibs_KeyMapper m, ibs_Object o
                        WHERE   o.oid = m.oid
                            AND o.posNoPath LIKE ai_posNoPath || '%'
                    );

        EXCEPTION
            WHEN OTHERS THEN            -- any error
                -- create error entry:
                l_ePos := 'error in delete';
                RAISE;                  -- call common exception handler
        END;
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- create error entry:
            l_ePos := 'error in insert';
            RAISE;                      -- call common exception handler
    END;

    -- return the state value:
    RETURN  c_ALL_RIGHT;

EXCEPTION 
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_ePos ||
            '; ai_posNoPath = ' || ai_posNoPath ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        ibs_error.log_error (ibs_error.error, 'p_KeyMapper$archiveExtKeys', l_eText);
        -- return error code:
        RETURN c_NOT_OK; 
END p_KeyMapper$archiveExtKeys;
/

show errors;


EXIT;
