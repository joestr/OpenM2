/******************************************************************************
 * Create the table ibs_XMLViewerContainer_01. <BR>
 * 
 *
 * @version     1.10.0001, 04.08.1999
 *
 * @author      Bernd Buchegger (BB)  990519
 *
 * <DT><B>Updates:</B>
 * <DD>MS 990804	Code cleaning.
 ******************************************************************************
 */

CREATE TABLE ibs_XMLViewerContainer_01
(
    oid                 RAW(8)          NOT NULL,
    useStandardHeader   NUMBER(1)       NOT NULL,
    templateOid         RAW(8),
    headerFields        VARCHAR2(255),
    workflowTemplateOid RAW(8),
    workflowAllowed     NUMBER(1)       NOT NULL
) /*TABLESPACE*/;

ALTER TABLE /*USER*/ibs_XMLViewerContainer_01  MODIFY (workflowAllowed DEFAULT 1);

exit;
