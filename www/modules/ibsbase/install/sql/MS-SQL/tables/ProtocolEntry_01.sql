/******************************************************************************
 * The ibs_ProtocolEntry_01 table incl. indexes. <BR>
 *
 * @version     11.04.2008
 *
 * @author      Bernhard Tatzmann (BT)  080411
 *
 * <DT><B>Updates:</B>
 ******************************************************************************
 */
CREATE TABLE ibs_ProtocolEntry_01
(
    id                  ID              NOT NULL UNIQUE,
    protocolId          ID              NOT NULL,
    fieldName           NAME            NOT NULL DEFAULT ('UNKNOWN'),
    oldValue            NAME            NOT NULL DEFAULT ('UNKNOWN'),
    newValue            NAME            NOT NULL DEFAULT ('UNKNOWN')
)
GO
-- ibs_ProtocolEntry_01
