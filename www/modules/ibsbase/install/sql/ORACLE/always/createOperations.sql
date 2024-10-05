/******************************************************************************
 * All operations within the framework. <BR>
 *
 * @version     $Id: createOperations.sql,v 1.4 2003/10/21 08:53:12 klaus Exp $
 *
 * @author      Andreas Jansa (AJ)  991102
 ******************************************************************************
 */

--*****************************************************************************
--** Declarations                                                            **
--*****************************************************************************
DECLARE
    l_retVal        INTEGER;

BEGIN

--*****************************************************************************
--** Initialize user independent data                                        **
--*****************************************************************************

-- create Operations:
l_retVal := p_Operation$new (         1, 'new', '');        -- 0x00000001
l_retVal := p_Operation$new (         2, 'view', '');       -- 0x00000002
l_retVal := p_Operation$new (         4, 'read', '');       -- 0x00000004
l_retVal := p_Operation$new (         8, 'change', '');     -- 0x00000008
l_retVal := p_Operation$new (        16, 'delete', '');     -- 0x00000010
l_retVal := p_Operation$new (        32, 'login', '');      -- 0x00000020
l_retVal := p_Operation$new (        64, 'UNKNOWN', '');    -- 0x00000040
l_retVal := p_Operation$new (       128, 'UNKNOWN', '');    -- 0x00000080
l_retVal := p_Operation$new (       256, 'viewRights', ''); -- 0x00000100
l_retVal := p_Operation$new (       512, 'setRights', '');  -- 0x00000200
l_retVal := p_Operation$new (      1024, 'UNKNOWN', '');    -- 0x00000400
l_retVal := p_Operation$new (      2048, 'UNKNOWN', '');    -- 0x00000800
l_retVal := p_Operation$new (      4096, 'createLink', ''); -- 0x00001000
l_retVal := p_Operation$new (      8192, 'distribute', ''); -- 0x00002000
l_retVal := p_Operation$new (     16384, 'UNKNOWN', '');    -- 0x00004000
l_retVal := p_Operation$new (     32768, 'UNKNOWN', '');    -- 0x00008000
l_retVal := p_Operation$new (     65536, 'UNKNOWN', '');    -- 0x00010000
l_retVal := p_Operation$new (    131072, 'UNKNOWN', '');    -- 0x00020000
l_retVal := p_Operation$new (    262144, 'UNKNOWN', '');    -- 0x00040000
l_retVal := p_Operation$new (    524288, 'UNKNOWN', '');    -- 0x00080000
l_retVal := p_Operation$new (   1048576, 'addElem', '');    -- 0x00100000
l_retVal := p_Operation$new (   2097152, 'delElem', '');    -- 0x00200000
l_retVal := p_Operation$new (   4194304, 'viewElems', '');  -- 0x00400000
l_retVal := p_Operation$new (   8388608, 'UNKNOWN', '');    -- 0x00800000
l_retVal := p_Operation$new (  16777216, 'viewProtocol', '');-- 0x01000000
l_retVal := p_Operation$new (  33554432, 'UNKNOWN', '');    -- 0x02000000
l_retVal := p_Operation$new (  67108864, 'UNKNOWN', '');    -- 0x04000000
l_retVal := p_Operation$new ( 134217728, 'UNKNOWN', '');    -- 0x08000000
l_retVal := p_Operation$new ( 268435456, 'UNKNOWN', '');    -- 0x10000000
l_retVal := p_Operation$new ( 536870912, 'UNKNOWN', '');    -- 0x20000000
l_retVal := p_Operation$new (1073741824, 'UNKNOWN', '');    -- 0x40000000

debug ('Operations inserted.');

END;
/

EXIT;
