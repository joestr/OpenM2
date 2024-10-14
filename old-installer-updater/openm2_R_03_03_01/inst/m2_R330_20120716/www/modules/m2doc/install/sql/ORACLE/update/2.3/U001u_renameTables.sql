/******************************************************************************
 * Task:        TASK Release 2.4: Separate ibs and m2.
 *
 * Description: Rename all changed tables.
 *
 * Repeatable:  yes
 *
 * @version     $Id: U001u_renameTables.sql,v 1.1 2003/10/31 16:27:28 klaus Exp $
 *
 * @author      Klaus Reimüller (KR) 031031
 ******************************************************************************
 */

DECLARE
    -- constants:

    -- local variables:
    l_file                  VARCHAR2 (5) := 'U001u'; -- name of actual file
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text

-- body:
BEGIN
    p_renameTable (l_file, 'ibs_Address_01', 'm2_Address_01');
    p_renameTable (l_file, 'm2_Beitrag_01', 'm2_Article_01');
    p_renameTable (l_file, 'm2_Diskussion_01', 'm2_Discussion_01');

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
