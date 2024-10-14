/******************************************************************************
 * Task:        IBS-820 Update empty OIDs of VALUEDOMAIN, FIELDREF and 
 *                      OBJECTREF fields
 *
 * Description: This file creates a temporary table for backup any changed data.
 *              Table is named 'backup_IBS820' and contains four columns
 *              - OID of object - OBJECTID
 *              - table name - NVARCHAR(50)
 *              - column name - NVARCHAR(50)
 *              - old OID value - OBJECTID       -- for singleselection fields
 *              - old OID value - NVARCHAR(4000) -- for multiselection fields
 *              After backup of the data the corresponding original entry
 *              will be updated with the correct empty oid '0x0000000000000000'
 *
 * Repeatable:  yes
 *
 * @version     $Id: U330001u_updateEmptyOIDs.sql,v 1.1 2012/08/07 11:46:43 rburgermann Exp $
 *
 * @author      Roland Burgermann (RB) 20120806
 ******************************************************************************
 */ 

-- Create backup table only when table not exists already
IF OBJECT_ID ('backup_IBS820') IS NULL
    CREATE TABLE backup_IBS820 (
        oid OBJECTID,
        tablename NVARCHAR(50),
        columnname NVARCHAR(50),
        oid_objectid OBJECTID,
        oid_nvarchar NVARCHAR(4000))
GO

-- For each column save the data which will be changed to the backup table and update the necessary data

-- ibs_reference (referencedOid)
INSERT INTO backup_IBS820 
    SELECT referencingOid, 'ibs_reference', 'referenceOid', referencedOid, NULL
    FROM ibs_reference
    WHERE referencedOid IN (0xF000F0F0F0F00010, 0xF0F0F0F0F0F0F0F0, 0xF0F0F0F000100000)
GO
UPDATE ibs_reference SET referencedOid = 0x0000000000000000 
    WHERE referencedOid IN (0xF000F0F0F0F00010, 0xF0F0F0F0F0F0F0F0, 0xF0F0F0F000100000)
GO