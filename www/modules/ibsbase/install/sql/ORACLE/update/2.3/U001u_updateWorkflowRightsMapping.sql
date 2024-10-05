/******************************************************************************
 * Task:        BUG#2046 - Wrong rights aliases after finishing of workflow.
 *
 * Description: Changed rights entries for rights mapping.
 *
 * Repeatable:  yes
 *
 * @version     2.30.0001, 05.09.2002 KR
 *
 * @author      Klaus Reimüller (KR) 020905
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
    BEGIN
        -- delete all entries
        DELETE ibs_RightsMapping;
    
        -- create rights entries: rights are kind of hierarchical
        INSERT INTO  ibs_RightsMapping VALUES  ('READ', 'READ');
        INSERT INTO  ibs_RightsMapping VALUES  ('READ', 'VIEW');
        INSERT INTO  ibs_RightsMapping VALUES  ('READ', 'VIEWELEMS');

        INSERT INTO  ibs_RightsMapping VALUES  ('CREATE', 'READ');
        INSERT INTO  ibs_RightsMapping VALUES  ('CREATE', 'VIEW');
        INSERT INTO  ibs_RightsMapping VALUES  ('CREATE', 'VIEWELEMS');
        INSERT INTO  ibs_RightsMapping VALUES  ('CREATE', 'NEW');
        INSERT INTO  ibs_RightsMapping VALUES  ('CREATE', 'ADDELEM');

        INSERT INTO  ibs_RightsMapping VALUES  ('CHANGE', 'READ');
        INSERT INTO  ibs_RightsMapping VALUES  ('CHANGE', 'VIEW');
        INSERT INTO  ibs_RightsMapping VALUES  ('CHANGE', 'VIEWELEMS');
        INSERT INTO  ibs_RightsMapping VALUES  ('CHANGE', 'CREATELINK');
        INSERT INTO  ibs_RightsMapping VALUES  ('CHANGE', 'DISTRIBUTE');
        INSERT INTO  ibs_RightsMapping VALUES  ('CHANGE', 'NEW');
        INSERT INTO  ibs_RightsMapping VALUES  ('CHANGE', 'ADDELEM');
        INSERT INTO  ibs_RightsMapping VALUES  ('CHANGE', 'CHANGE');

        INSERT INTO  ibs_RightsMapping VALUES  ('CHANGEDELETE', 'READ');
        INSERT INTO  ibs_RightsMapping VALUES  ('CHANGEDELETE', 'VIEW');
        INSERT INTO  ibs_RightsMapping VALUES  ('CHANGEDELETE', 'VIEWELEMS');
        INSERT INTO  ibs_RightsMapping VALUES  ('CHANGEDELETE', 'CREATELINK');
        INSERT INTO  ibs_RightsMapping VALUES  ('CHANGEDELETE', 'DISTRIBUTE');
        INSERT INTO  ibs_RightsMapping VALUES  ('CHANGEDELETE', 'NEW');
        INSERT INTO  ibs_RightsMapping VALUES  ('CHANGEDELETE', 'ADDELEM');
        INSERT INTO  ibs_RightsMapping VALUES  ('CHANGEDELETE', 'CHANGE');
        INSERT INTO  ibs_RightsMapping VALUES  ('CHANGEDELETE', 'DELETE');
        INSERT INTO  ibs_RightsMapping VALUES  ('CHANGEDELETE', 'DELELEM');

        INSERT INTO  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'READ');
        INSERT INTO  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'VIEW');
        INSERT INTO  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'VIEWELEMS');
        INSERT INTO  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'CREATELINK');
        INSERT INTO  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'DISTRIBUTE');
        INSERT INTO  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'NEW');
        INSERT INTO  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'ADDELEM');
        INSERT INTO  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'CHANGE');
        INSERT INTO  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'DELETE');
        INSERT INTO  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'DELELEM');
        INSERT INTO  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'VIEWRIGHTS');
--    no setrights allowed for workflow-users
--        INSERT INTO  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'SETRIGHTS');
        INSERT INTO  ibs_RightsMapping VALUES  ('ALLNOSETRIGHTS', 'VIEWPROTOCOL');

        INSERT INTO  ibs_RightsMapping VALUES  ('ALL', 'READ');
        INSERT INTO  ibs_RightsMapping VALUES  ('ALL', 'VIEW');
        INSERT INTO  ibs_RightsMapping VALUES  ('ALL', 'VIEWELEMS');
        INSERT INTO  ibs_RightsMapping VALUES  ('ALL', 'CREATELINK');
        INSERT INTO  ibs_RightsMapping VALUES  ('ALL', 'DISTRIBUTE');
        INSERT INTO  ibs_RightsMapping VALUES  ('ALL', 'NEW');
        INSERT INTO  ibs_RightsMapping VALUES  ('ALL', 'ADDELEM');
        INSERT INTO  ibs_RightsMapping VALUES  ('ALL', 'CHANGE');
        INSERT INTO  ibs_RightsMapping VALUES  ('ALL', 'DELETE');
        INSERT INTO  ibs_RightsMapping VALUES  ('ALL', 'DELELEM');
        INSERT INTO  ibs_RightsMapping VALUES  ('ALL', 'VIEWRIGHTS');
        INSERT INTO  ibs_RightsMapping VALUES  ('ALL', 'SETRIGHTS');
        INSERT INTO  ibs_RightsMapping VALUES  ('ALL', 'VIEWPROTOCOL');


    EXCEPTION
        WHEN OTHERS THEN                -- any error
            -- log the error:
            l_ePos := 'UPDATE problems';
            RAISE;                      -- call common exception handler
    END;

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
        debug (l_file || ': finished');
END;
/
commit work
/

EXIT;
