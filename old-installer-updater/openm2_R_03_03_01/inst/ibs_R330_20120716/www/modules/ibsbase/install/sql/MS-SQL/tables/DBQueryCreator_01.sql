/******************************************************************************
 * Table for dynamic search queries on databases.
 *
 * @version     2.21.0001, 020503 KR
 *
 * @author      Klaus Reimüller (KR)  020503
 ******************************************************************************
 */
CREATE TABLE ibs_DBQueryCreator_01
(
    oid                 OBJECTID        NOT NULL, -- oid of the object
    connectorOid        OBJECTID                -- oid of database connector
)
GO
-- ibs_DBQueryCreator_01

-- access indices: