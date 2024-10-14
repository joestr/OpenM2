/******************************************************************************
 * Table for dynamic search queries on databases.
 *
 * @version     2.21.0001, 020604 KR
 *
 * @author      Klaus Reimüller (KR)  020604
 ******************************************************************************
 */
CREATE TABLE /*USER*/ibs_DBQueryCreator_01
(
    oid                 RAW (8) UNIQUE  NOT NULL, -- oid of the object
    connectorOid        RAW (8)                 -- oid of database connector
)  /*TABLESPACE*/;
-- ibs_DBQueryCreator_01

-- set default values:
ALTER TABLE ibs_DBQueryCreator_01 MODIFY (oid DEFAULT hextoraw ('0000000000000000'));
ALTER TABLE ibs_DBQueryCreator_01 MODIFY (connectorOid DEFAULT hextoraw ('0000000000000000'));


EXIT;
