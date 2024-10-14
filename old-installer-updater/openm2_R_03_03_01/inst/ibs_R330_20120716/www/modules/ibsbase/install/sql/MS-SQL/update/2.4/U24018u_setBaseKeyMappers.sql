/******************************************************************************
 * Task:        TRI050811_1 Set KeyMappers for base objects.
 *
 * Description: The standard base objects shall get standardized key mappers.
 *              These key mappers will be used to reference these objects in
 *              case there shall be added something.
 *              Ensure that all existing base objects have the correct key
 *              mapper.
 *
 * Repeatable:  yes
 *
 * @version     $Id: U24018u_setBaseKeyMappers.sql,v 1.3 2006/02/02 16:54:16 bernd Exp $
 *
 * @author      Klaus Reimüller (KR) 20050811
 ******************************************************************************
 */

PRINT 'starting $RCSFile$...'
GO

-- don't show count messages:
SET NOCOUNT ON
GO

-- delete existing procedure:
EXEC p_dropProc 'pi_setKeyMaper'
GO
-- create the new procedure:
CREATE PROCEDURE pi_setKeyMapper
(
    -- input parameters:
    @ai_name                NAME,
    @ai_typeName            NAME,
    @ai_idDomain            VARCHAR (63),
    @ai_id                  VARCHAR (255)
    -- output parameters:
)
AS
DECLARE
    -- constants:

    -- local variables:
    @l_msg                  VARCHAR (2000)  -- output message

    -- assign constants:

    -- initialize local variables:

-- body:
    -- check if the key mapper already exists:
    IF NOT EXISTS (
        SELECT  km.oid
        FROM    ibs_KeyMapper km
        WHERE   km.idDomain = @ai_idDomain
            AND km.id = @ai_id
    )
    BEGIN
        -- the key mapper does not exist yet
        -- create the key mapper:
        -- note: the query has to check the domain to ensure that the ids are
        -- not duplicate.
        INSERT INTO ibs_KeyMapper
                (oid, id, idDomain)
        SELECT  o.oid, @ai_id, @ai_idDomain
        FROM    ibs_Object o, ibs_Domain_01 dom, ibs_Object odom
        WHERE   odom.oid = dom.oid
            AND dom.id = (SELECT MIN (id) FROM ibs_Domain_01)
            AND o.name = @ai_name
            AND o.typeName = @ai_typeName
            AND o.posNoPath LIKE odom.posNoPath + '%'
    END -- if
GO
-- pi_createTab


-- set the several key mappers:
EXEC pi_setKeyMapper '#CONFVAR.ibsbase.menuPublic#', 'Ablage',
                     'ibs_instobj', 'menuPublic'
EXEC pi_setKeyMapper 'Data Interchange', 'Data Interchange', 'ibs_instobj', 'di'
EXEC pi_setKeyMapper 'Import', 'Import-Ablage', 'ibs_instobj', 'diimport'
EXEC pi_setKeyMapper 'Export', 'Export-Ablage', 'ibs_instobj', 'diexport'
EXEC pi_setKeyMapper 'Benutzerverwaltung', 'Benutzerverwaltung',
                     'ibs_instobj', 'userAdmin'
EXEC pi_setKeyMapper 'Benutzer', 'Benutzer-Ablage', 'ibs_instobj', 'users'
EXEC pi_setKeyMapper 'Gruppen', 'Gruppen-Ablage', 'ibs_instobj', 'groups'
EXEC pi_setKeyMapper 'Arbeitsumgebungen', 'Gruppen-Ablage', 'ibs_instobj', 'userWorkspaces'
EXEC pi_setKeyMapper 'Layouts', 'Layout-Ablage', 'ibs_instobj', 'layouts'
EXEC pi_setKeyMapper 'MenuTabContainer', 'MenuTabContainer', 'ibs_instobj', 'menutabs'
EXEC pi_setKeyMapper 'Verwaltung', 'MenuTab', 'ibs_instobj', 'menutabPublic'
EXEC pi_setKeyMapper 'MenuObjects', 'Ablage', 'ibs_instobj', 'menuobjects'

GO


-- delete not longer needed procedure:
EXEC p_dropProc 'pi_setKeyMapper'
GO

-- show count messages again:
SET NOCOUNT OFF
GO

PRINT '$RCSFile$: finished.'
GO
