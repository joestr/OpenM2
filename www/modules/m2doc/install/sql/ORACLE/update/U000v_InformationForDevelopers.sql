/******************************************************************************
 * Task:        TASK/BUG#xxx - Dummy file for Updatescript.
 *
 * Description: This file contains all structural information which is
 *              necessary to create an update file for specific changes in the
 *              database content.
 *
 * Repeatable:  yes/no
 *
 * @version     $Id: U000v_InformationForDevelopers.sql,v 1.8 2003/10/06 22:06:14 klaus Exp $
 *
 * @author      Horst Pichler (HP) 020626
 ******************************************************************************
 */ 

DECLARE
    -- constants:
    c_CONST1                CONSTANT INTEGER := 1234567; -- description

    -- local variables:
    l_file                  VARCHAR2 (5) := 'U000v'; -- name of actual file
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_var1                  INTEGER;        -- description
    l_var2                  INTEGER := 7;   -- description
    l_var3                  VARCHAR2 (255) := 'var3Text'; -- description

-- body:
BEGIN
    BEGIN
/*
        Here comes some code, e.g. an UPDATE statement.
*/
    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- log the error:
            l_ePos := 'UPDATE problems';
            RAISE;                      -- call common exception handler
    END;

/*
    Some other code...
*/

    -- show state message:
    debug (l_file || ': finished');

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_file || ': ' || l_ePos ||
            '; l_var1 = ' || l_var1 ||
            ', l_var2 = ' || l_var2 ||
            ', l_var3 = ' || l_var3 ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        debug (l_eText);
        ibs_error.log_error (ibs_error.error, l_file, l_eText);
        -- show state message:
        debug (l_file || ': finished with errors');
END;
/

COMMIT WORK
/

EXIT;
