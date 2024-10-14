/******************************************************************************
 * The ibs domain scheme table incl. indexes. <BR>
 * The domain scheme table contains the schemes used for creating new domains.
 * These schemes contain not only the public structure of the domain but also
 * the user groups and the groups' access rights on this structure. Besides 
 * some default users can be defined. <BR>
 * There is also a scheme of an user workspace structure defined which belongs
 * to domains having this scheme. Users which are created within a domain of
 * a special scheme have the workspace scheme which belongs to the domain 
 * scheme.
 *
 * @version     1.10.0001, 03.08.1999
 *
 * @author      Klaus Reimüller (KR)  980923
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990803    Code cleaning.
 ******************************************************************************
 */
CREATE TABLE ibs_DomainScheme_01
(
    id              ID              NOT NULL PRIMARY KEY,
    oid             OBJECTID        NULL,
    workspaceProc   STOREDPROCNAME  NULL,   -- name of stored procedure 
                                            -- creating the workspace for one 
                                            -- user
    hasCatalogManagement BOOL       NOT NULL DEFAULT (0),
                                            -- does a domain with this scheme
                                            -- have a catalog management?
    hasDataInterchange BOOL         NOT NULL DEFAULT (0)
                                            -- does a domain with this scheme
                                            -- have a data interchange component?
)
GO
-- ibs_DomainScheme_01
