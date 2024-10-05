-------------------------------------------------------------------------------
-- Delete all data within m2. <BR>
--
-- @version     $Id: deleteAllTableContents.sql,v 1.7 2003/10/31 00:12:48 klaus Exp $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020828
-------------------------------------------------------------------------------
--/

-- delete existing procedure:
CALL IBSDEV1.p_dropProc ('pim2_deleteAllTableContents');
-- create new procedure:
CREATE PROCEDURE IBSDEV1.pim2_deleteAllTableContents ()
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- local variables:
    DECLARE SQLCODE         INT;
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE l_sqlstatus     INT;
    DECLARE l_count         INT DEFAULT 0;
    DECLARE l_name          VARCHAR (32);
    DECLARE l_exec          VARCHAR (255);

    -- define cursor:
    DECLARE object_Cursor INSENSITIVE CURSOR FOR 
        SELECT  s.name
        FROM    IBSDEV1.SYSTABLES s
        WHERE   s.TABLE_SCHEMA = 'IBSDEV1'
            AND (
                    (UPPER (s.name) LIKE UPPER ('M2_%'))
                OR  (UPPER (s.name) LIKE UPPER ('MAD_%'))
                )
        ORDER BY s.name;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

-- body:
    -- open the cursor:
    OPEN object_Cursor;

    -- get the first table:
    SET l_sqlcode = 0;
    FETCH FROM object_Cursor INTO l_name;
    SET l_sqlstatus = l_sqlcode;
  
    -- loop through all found objects:
    WHILE (l_sqlstatus = 0)             -- another tuple found
    DO
		-- define and perform operation:
        SET l_exec = 'DELETE FROM IBSDEV1.' || l_name;
        EXECUTE IMMEDIATE l_exec;

        -- increment counter:
        SET l_count = l_count + 1;

        -- get the next tuple:
        SET l_sqlcode = 0;
        FETCH FROM object_Cursor INTO l_name;
        SET l_sqlstatus = l_sqlcode;
    END WHILE; -- another tuple found

    -- close the cursor:
    CLOSE object_Cursor;

    -- return the number of tuples:
    return l_count;
END;
-- pim2_deleteAllTableContents

-- execute procedure:
CALL IBSDEV1.pim2_deleteAllTableContents;
-- delete procedure:
CALL IBSDEV1.p_dropProc ('pim2_deleteAllTableContents');
