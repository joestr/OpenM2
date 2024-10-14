/******************************************************************************
 * Create some known schemes. <BR>
 *
 * @version     $Id: createSchemes.sql,v 1.3 2003/10/05 02:00:23 klaus Exp $
 *
 * @author      Klaus Reimüller (KR)  980924
 ******************************************************************************
 */

-- ä => „, ö => ”, ü => ?, î => á, - => Ž, Ö => ™, _ => š

-- don't show count messages:
SET NOCOUNT ON
GO

PRINT 'Creating domain schemes...'

-- declarations:
DECLARE
    @l_oid                  OBJECTID        -- oid of the current scheme


    -- base scheme:
    EXEC p_DomainScheme_01$new 'Standard', 'The standard scheme.', 
        'p_Workspace_01$createObjects', '', 1, 1, @l_oid OUTPUT

    -- scheme for Sport2000:
    EXEC p_DomainScheme_01$new 'SP2000', 'Scheme specially created for Sport2000.', 
        'p_Workspace_01$createSport2000', 'Sport2000%', 1, 1, @l_oid OUTPUT

    -- scheme for Verbund:
    EXEC p_DomainScheme_01$new 'Verbund', 'Scheme specially created for Verbund.', 
        'p_Workspace_01$createVerbund', 'TeleRing%', 0, 1, @l_oid OUTPUT

    -- scheme for NWR:
    EXEC p_DomainScheme_01$new 'Central Network', 'Scheme specially created for Nord-West-Ring.', 
        'p_Workspace_01$createNWR', 'Central Network%', 0, 1, @l_oid OUTPUT

    -- scheme for Garant:
    EXEC p_DomainScheme_01$new 'Easy', 'Scheme specially created for Garant.', 
        'p_Workspace_01$createGarant', 'Garant%', 0, 1, @l_oid OUTPUT

    -- scheme for MCC:
    EXEC p_DomainScheme_01$new 'MCC', 'Scheme specially created for MCC.', 
        'p_Workspace_01$createMCC', 'MCC%', 0, 1, @l_oid OUTPUT

    -- scheme for K„rntenwerbung:
    EXEC p_DomainScheme_01$new 'KW', 'Scheme specially created for K„rntenwerbung.', 
        'p_Workspace_01$createObjects', 'KW%', 1, 1, @l_oid OUTPUT

    -- scheme for MRI2:
    EXEC p_DomainScheme_01$new 'MRI2', 'Scheme specially created for Modering.', 
        'p_Workspace_01$createMRI2', 'MRI%', 1, 1, @l_oid OUTPUT


    -- ensure that each domain has a defined scheme:
    UPDATE  ibs_Domain_01
    SET     scheme = 
            (
                SELECT  MIN (id)
                FROM    ibs_DomainScheme_01
            )
    WHERE   scheme NOT IN 
            (
                SELECT  id
                FROM    ibs_DomainScheme_01
            )

    -- set correct workspace procedures of the domains:
    UPDATE  ibs_Domain_01
    SET     workspaceProc = s.workspaceProc
    FROM    ibs_Domain_01 d, ibs_DomainScheme_01 s
    WHERE   d.scheme = s.id


PRINT 'Schemes created.'
GO

-- show count messages again:
SET NOCOUNT OFF
GO
