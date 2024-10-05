/******************************************************************************
 * Create some known schemes. <BR>
 *
 * @version     1.10.0001, 04.08.1999
 *
 * @author      Klaus Reimüller (KR)  990323
 ******************************************************************************
 */

-- ä => ä, ö => ö, ü => ü, ß => ß, Ä => Ä, Ö => Ö, Ü => Ü

-- declarations:
DECLARE
    l_oid                   RAW (8);        -- oid of the current scheme

BEGIN
    debug ('Creating domain schemes...');

    -- base scheme:
    p_DomainScheme_01$new ('Standard', 'The standard scheme.', 
        'p_Workspace_01$createObjects', '', 1, 1, l_oid);

    -- scheme for Sport2000:
    p_DomainScheme_01$new ('SP2000', 'Scheme specially created for Sport2000.', 
        'p_Workspace_01$createSport2000', 'Sport2000%', 1, 1, l_oid);

    -- scheme for Verbund:
    p_DomainScheme_01$new ('Verbund', 'Scheme specially created for Verbund.', 
        'p_Workspace_01$createVerbund', 'TeleRing%', 0, 1, l_oid);

    -- scheme for NWR:
    p_DomainScheme_01$new ('Central Network', 'Scheme specially created for Nord-West-Ring.', 
        'p_Workspace_01$createNWR', 'Central Network%', 0, 1, l_oid);

    -- scheme for Garant:
    p_DomainScheme_01$new ('Easy', 'Scheme specially created for Garant.', 
        'p_Workspace_01$createGarant', 'Garant%', 0, 1, l_oid);

    -- scheme for MCC:
    p_DomainScheme_01$new ('MCC', 'Scheme specially created for MCC.', 
        'p_Workspace_01$createMCC', 'MCC%', 0, 1, l_oid);

    -- scheme for Kärntenwerbung:
    p_DomainScheme_01$new ('KW', 'Scheme specially created for Kärntenwerbung.', 
        'p_Workspace_01$createObjects', 'KW%', 1, 1, l_oid);

    -- scheme for MRI2:
    p_DomainScheme_01$new ('MRI2', 'Scheme specially created for Modering.', 
        'p_Workspace_01$createMRI2', 'MRI%', 1, 1, l_oid);


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
            );

    -- set correct workspace procedures of the domains:
    UPDATE  ibs_Domain_01
    SET     workspaceProc = 
            (
                SELECT  s.workspaceProc
                FROM    ibs_Domain_01 d, ibs_DomainScheme_01 s
                WHERE   d.scheme = s.id
            );

    debug ('Schemes created.');
END;
/

EXIT;
