/******************************************************************************
 *
 * The ibs_XMLViewer_01 table. <BR>
 *
 * @version         1.10.0001, 09.10.2000
 *
 * @author      Michael Steiner (MS)  09.10.2000
 *
 * <DT><B>Updates:</B>
 ******************************************************************************
 */
CREATE TABLE /*USER*/ibs_XMLViewer_01
(
    oid                 RAW (8)        NOT NULL UNIQUE,
    templateOid         RAW (8),
    workflowTemplateOid RAW (8)
) /*TABLESPACE*/;

EXIT;
