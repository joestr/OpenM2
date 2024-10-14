/******************************************************************************
 *
 * The ibs_Connector_01 table. <BR>
 *
 * @version         1.11.0001, 08.12.1999
 *
 * @author      Harald Buzzi    (HB)  991208
 *
 * <DT><B>Updates:</B>
 ******************************************************************************
 */

CREATE TABLE /*USER*/ibs_Connector_01
(
    oid                 RAW(8)          NOT NULL,
    connectorType       INTEGER         NOT NULL,
    isImportConnector   INTEGER,
    isExportConnector   INTEGER,
    arg1                VARCHAR2(255),
    arg2                VARCHAR2(255),
    arg3                VARCHAR2(255),
    arg4                VARCHAR2(255),
    arg5                VARCHAR2(128),
    arg6                VARCHAR2(128),
    arg7                VARCHAR2(128),
    arg8                VARCHAR2(128),
    arg9                VARCHAR2(128)
) /*TABLESPACE*/;

EXIT;

