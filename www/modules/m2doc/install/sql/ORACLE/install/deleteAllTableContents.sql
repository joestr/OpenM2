/******************************************************************************
 * Delete all data within m2. <BR>
 *
 * @version     $Id: deleteAllTableContents.sql,v 1.4 2003/10/31 00:13:12 klaus Exp $
 *
 * @author      Klaus Reimüller (KR)  990325
 ******************************************************************************
 */

-- ä => ä, ö => ö, ü => ü, ß => ß, Ä => Ä, Ö => Ö, Ü => Ü

-- don't show count messages:
/*
SET NOCOUNT ON
GO
*/

-- declarations:
DECLARE
    -- constants:
    c_stars         CONSTANT VARCHAR2 (15) := '**';

    -- local variables:
    l_tabName       VARCHAR2 (30);
    l_tabNameHeader VARCHAR2 (75);
    l_count         INTEGER := 0;
    l_msg           VARCHAR2 (255);
    CURSOR tabNameCursor IS
        SELECT  object_name AS name
        FROM    user_objects
        WHERE   object_type = 'TABLE'
            AND (object_name LIKE 'M2_%'
                OR object_name LIKE 'MAD_%')
        ORDER BY object_name;
    l_cursorRow     tabNameCursor%ROWTYPE;

BEGIN
    -- loop through the tables:
    FOR l_cursorRow IN tabNameCursor
    LOOP
        -- get next table:
        l_tabName := l_cursorRow.name;

        -- print table name:
--        debug (c_stars || '  ' || l_tabName || '  ' || c_stars);
        -- delete table content:
        EXEC_SQL ('TRUNCATE TABLE ' || l_tabName);

        debug ('');

        -- increment counter:
        l_count := l_count + 1;
    END LOOP; -- while another table found

    -- print final state:
    debug ('');
    debug ('');
--    debug (c_stars || '  KEINE WEITEREN TABELLEN  ' || c_stars);
--    debug ('');
    debug (l_count || ' Tabellen betroffen.');
    debug ('TRUNCATE wurde für alle m2 Tabellen durchgeführt.');
EXCEPTION
    WHEN OTHERS THEN
        err;
END;
/

-- show count messages again:
/*
SET NOCOUNT OFF
GO
*/

EXIT;
