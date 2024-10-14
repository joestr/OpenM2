/******************************************************************************
 *
 * The ibs_XMLViewerContainer_01 indexes and triggers. <BR>
 *
 * @version         1.10.0001, 03.08.1999
 *
 * @author      Bernd Buchegger (BB)  980513
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990803	Code cleaning.
 ******************************************************************************
 */
CREATE TABLE ibs_XMLViewerContainer_01
(
    oid                 OBJECTID        NOT NULL,
    useStandardHeader   BIT             NOT NULL,
    templateOid         OBJECTID,
    headerFields        NVARCHAR (255),
    workflowTemplateOid OBJECTID,
    workflowAllowed     BOOL            NOT NULL DEFAULT (1)
)
GO
