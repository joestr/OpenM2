/******************************************************************************
 * Task:        TASK/BUG#xxx - Dummy file for table update script.
 *
 * Description: This file contains all structural information which is
 *              necessary to create an update file for specific changes in the
 *              database content.
 *              Throughout this script the following tags are used:
 *              <tableName> ....... The name of the table to be updated.
 *              <tempTableName> ... The name of the temporary table containing
 *                                  the new table scheme.
 *
 * Repeatable:  yes
 *
 * @version     $Id: U000a_TableChange.sql,v 1.12 2003/10/06 22:06:00 klaus Exp $
 *
 * @author      Klaus Reimüller (KR) 020626
 ******************************************************************************
 */ 

-- create the table:
CREATE TABLE /*USER*/<tempTableName>
(
/*
    standard description of table attributes
*/
)/*TABLESPACE*/;

-- set default values:
/*
ALTER TABLE /*USER*/<tempTableName> MODIFY (id DEFAULT 0);
ALTER TABLE /*USER*/<tempTableName> MODIFY (oid DEFAULT hextoraw ('0000000000000000'));
ALTER TABLE /*USER*/<tempTableName> MODIFY (state DEFAULT 2);
...
*/


DECLARE
    -- constants:

    -- local variables:
    l_file                  VARCHAR2 (5) := 'U000x'; -- name of actual file
    l_ePos                  VARCHAR2 (2000); -- error position description
    l_eText                 VARCHAR2 (5000); -- full error text
    l_tableName             VARCHAR2 (30) := '<tableName>';
                                            -- the table name
    l_tempTableName         VARCHAR2 (30) := '<tempTableName>';
                                            -- the temporary table name

-- body:
BEGIN
    -- call the procedure which changes the old table scheme to the new one:
    -- for each new attribute set a default value either as number or as string
    p_changeTable (l_file, l_tableName, l_tempTableName,
        '<attributeName1>', '<INT defaultValue1>',
        '<attributeName2>', '''<VARCHAR defaultValue2>''',
        '<attributeName3>', '''<VARCHAR defaulValue3>''',
        '<attributeName4>', '<INT defaultValue4>',
        '<attributeName5>', '<INT defaultValue5>',
        '<attributeName6>', '<INT defaultValue6>',
        '<attributeName7>', '''<VARCHAR defaulValue7>''',
        '<attributeName8>', '<INT defaultValue8>',
        '<attributeName9>', '<INT defaultValue9>',
        '<attributeName10>', '<INT defaultValue10>'
        );

    -- ensure that the temporary table is dropped:
    p_dropTable (l_tempTableName);

    debug (l_file || ': finished');

EXCEPTION
    WHEN OTHERS THEN
        -- create error entry:
        l_eText := l_file || ': Error when changing table ' ||
            l_tableName || ': ' ||
            '; l_tableName = ' || l_tableName ||
            ', l_tempTableName = ' || l_tempTableName ||
            '; errorcode = ' || SQLCODE ||
            ', errormessage = ' || SQLERRM;
        debug (l_eText);
--        ibs_error.log_error (ibs_error.error, l_file, l_eText);
        -- show state message:
        debug (l_file || ': finished');
END;
/


-- here come the trigger definitions:


COMMIT WORK;

EXIT;
