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

CREATE TABLE ibs_Connector_01
(
    oid                 OBJECTID        NOT NULL,
    connectorType       INT             NOT NULL,
    isImportConnector   BOOL,
    isExportConnector   BOOL,
    arg1                NVARCHAR (255),
    arg2                NVARCHAR (255),
    arg3                NVARCHAR (255),
    arg4                NVARCHAR (255),
    arg5                NVARCHAR (128),
    arg6                NVARCHAR (128),
    arg7                NVARCHAR (128),
    arg8                NVARCHAR (128),
    arg9                NVARCHAR (128)
)
GO

