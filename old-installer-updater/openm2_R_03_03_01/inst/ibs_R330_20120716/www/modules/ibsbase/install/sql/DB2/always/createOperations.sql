--------------------------------------------------------------------------------
-- All operations within the framework. <BR>
--
-- @version     $Id: createOperations.sql,v 1.4 2003/10/21 22:14:45 klaus Exp $
--
-- @author      Marcel Samek (MS)  020921
--------------------------------------------------------------------------------

-- delete existing procedure
CALL IBSDEV1.p_dropProc ('pi_createOperations');
-- create new procedure
CREATE PROCEDURE IBSDEV1.pi_createOperations ()
DYNAMIC RESULT SETS 1
LANGUAGE SQL
BEGIN
    -- local variables:
    DECLARE SQLCODE INT;
    DECLARE l_sqlcode       INT DEFAULT 0;

    -- exception handlers:
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;

-- body:
    -- create Operations:
    CALL IBSDEV1.p_Operation$new (         1, 'new', '');        -- 0x00000001
    CALL IBSDEV1.p_Operation$new (         2, 'view', '');       -- 0x00000002
    CALL IBSDEV1.p_Operation$new (         4, 'read', '');       -- 0x00000004
    CALL IBSDEV1.p_Operation$new (         8, 'change', '');     -- 0x00000008
    CALL IBSDEV1.p_Operation$new (        16, 'delete', '');     -- 0x00000010
    CALL IBSDEV1.p_Operation$new (        32, 'login', '');      -- 0x00000020
    CALL IBSDEV1.p_Operation$new (        64, 'UNKNOWN', '');    -- 0x00000040
    CALL IBSDEV1.p_Operation$new (       128, 'UNKNOWN', '');    -- 0x00000080
    CALL IBSDEV1.p_Operation$new (       256, 'viewRights', ''); -- 0x00000100
    CALL IBSDEV1.p_Operation$new (       512, 'setRights', '');  -- 0x00000200
    CALL IBSDEV1.p_Operation$new (      1024, 'UNKNOWN', '');    -- 0x00000400
    CALL IBSDEV1.p_Operation$new (      2048, 'UNKNOWN', '');    -- 0x00000800
    CALL IBSDEV1.p_Operation$new (      4096, 'createLink', ''); -- 0x00001000
    CALL IBSDEV1.p_Operation$new (      8192, 'distribute', ''); -- 0x00002000
    CALL IBSDEV1.p_Operation$new (     16384, 'UNKNOWN', '');    -- 0x00004000
    CALL IBSDEV1.p_Operation$new (     32768, 'UNKNOWN', '');    -- 0x00008000
    CALL IBSDEV1.p_Operation$new (     65536, 'UNKNOWN', '');    -- 0x00010000
    CALL IBSDEV1.p_Operation$new (    131072, 'UNKNOWN', '');    -- 0x00020000
    CALL IBSDEV1.p_Operation$new (    262144, 'UNKNOWN', '');    -- 0x00040000
    CALL IBSDEV1.p_Operation$new (    524288, 'UNKNOWN', '');    -- 0x00080000
    CALL IBSDEV1.p_Operation$new (   1048576, 'addElem', '');    -- 0x00100000
    CALL IBSDEV1.p_Operation$new (   2097152, 'delElem', '');    -- 0x00200000
    CALL IBSDEV1.p_Operation$new (   4194304, 'viewElems', '');  -- 0x00400000
    CALL IBSDEV1.p_Operation$new (   8388608, 'UNKNOWN', '');    -- 0x00800000
    CALL IBSDEV1.p_Operation$new (  16777216, 'viewProtocol', '');-- 0x01000000
    CALL IBSDEV1.p_Operation$new (  33554432, 'UNKNOWN', '');    -- 0x02000000
    CALL IBSDEV1.p_Operation$new (  67108864, 'UNKNOWN', '');    -- 0x04000000
    CALL IBSDEV1.p_Operation$new ( 134217728, 'UNKNOWN', '');    -- 0x08000000
    CALL IBSDEV1.p_Operation$new ( 268435456, 'UNKNOWN', '');    -- 0x10000000
    CALL IBSDEV1.p_Operation$new ( 536870912, 'UNKNOWN', '');    -- 0x20000000
    CALL IBSDEV1.p_Operation$new (1073741824, 'UNKNOWN', '');    -- 0x40000000

    CALL IBSDEV1.p_debug ('Operations inserted.');
END;
-- pi_createOperations

-- execute procedure:
CALL IBSDEV1.pi_createOperations;
-- delete procedure:
CALL IBSDEV1.p_dropProc ('pi_createOperations');
