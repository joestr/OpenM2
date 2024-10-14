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

-- masterd_CustomerMapping (m_cust)
INSERT INTO backup_IBS820 
    SELECT oid, 'dbm_masterd_cust', 'm_cust', m_cust, NULL
    FROM dbm_masterd_cust
    WHERE m_cust IN (0xF000F0F0F0F00010, 0xF0F0F0F0F0F0F0F0, 0xF0F0F0F000100000)
GO
UPDATE dbm_masterd_cust SET m_cust = 0x0000000000000000 
    WHERE m_cust IN (0xF000F0F0F0F00010, 0xF0F0F0F0F0F0F0F0, 0xF0F0F0F000100000)
GO
-- masterd_CustomerMapping (m_assignedto)
INSERT INTO backup_IBS820 
    SELECT oid, 'dbm_masterd_cust', 'm_assignedto', m_assignedto, NULL
    FROM dbm_masterd_cust
    WHERE m_assignedto IN (0xF000F0F0F0F00010, 0xF0F0F0F0F0F0F0F0, 0xF0F0F0F000100000)
GO
UPDATE dbm_masterd_cust SET m_assignedto = 0x0000000000000000 
    WHERE m_assignedto IN (0xF000F0F0F0F00010, 0xF0F0F0F0F0F0F0F0, 0xF0F0F0F000100000)
GO

-- masterd_Person (m_user)
INSERT INTO backup_IBS820 
    SELECT oid, 'dbm_masterd_person', 'm_user', m_user, NULL 
    FROM dbm_masterd_person
    WHERE m_user IN (0xF000F0F0F0F00010, 0xF0F0F0F0F0F0F0F0, 0xF0F0F0F000100000)
GO
UPDATE dbm_masterd_person SET m_user = 0x0000000000000000 
    WHERE m_user IN (0xF000F0F0F0F00010, 0xF0F0F0F0F0F0F0F0, 0xF0F0F0F000100000)
GO
-- masterd_Person (m_title)
INSERT INTO backup_IBS820 
    SELECT oid, 'dbm_masterd_person', 'm_title', m_title, NULL 
    FROM dbm_masterd_person
    WHERE m_title IN (0xF000F0F0F0F00010, 0xF0F0F0F0F0F0F0F0, 0xF0F0F0F000100000)
GO
UPDATE dbm_masterd_person SET m_title = 0x0000000000000000 
    WHERE m_title IN (0xF000F0F0F0F00010, 0xF0F0F0F0F0F0F0F0, 0xF0F0F0F000100000)
GO

-- masterd_RoleMapper (m_staffm)
INSERT INTO backup_IBS820 
    SELECT oid, 'dbm_masterd_rolem', 'm_staffm', m_staffm, NULL 
    FROM dbm_masterd_rolem
    WHERE m_staffm IN (0xF000F0F0F0F00010, 0xF0F0F0F0F0F0F0F0, 0xF0F0F0F000100000)
GO
UPDATE dbm_masterd_rolem SET m_staffm = 0x0000000000000000 
    WHERE m_staffm IN (0xF000F0F0F0F00010, 0xF0F0F0F0F0F0F0F0, 0xF0F0F0F000100000)
GO
-- masterd_RoleMapper (m_role)
INSERT INTO backup_IBS820 
    SELECT oid, 'dbm_masterd_rolem', 'm_role', NULL, m_role
    FROM dbm_masterd_rolem
    WHERE m_role LIKE '%0xF000F0F0F0F00010%'
       OR m_role LIKE '%0xF0F0F0F0F0F0F0F0%'
       OR m_role LIKE '%0xF0F0F0F000100000%'
GO
UPDATE dbm_masterd_rolem SET m_role = 
(SELECT SUBSTRING (intab.m_role, 0, PATINDEX ('%0xF000F0F0F0F00010%', intab.m_role)) + 
        '0x0000000000000000' + 
        SUBSTRING (intab.m_role, PATINDEX ('%0xF000F0F0F0F00010%', intab.m_role) + 18, LEN(intab.m_role))
 FROM dbm_masterd_rolem intab
 WHERE intab.oid = dbm_masterd_rolem.oid)
WHERE dbm_masterd_rolem.m_role LIKE '%0xF000F0F0F0F00010%'
GO
UPDATE dbm_masterd_rolem SET m_role = 
(SELECT SUBSTRING (intab.m_role, 0, PATINDEX ('%0xF0F0F0F0F0F0F0F0%', intab.m_role)) + 
        '0x0000000000000000' + 
        SUBSTRING (intab.m_role, PATINDEX ('%0xF0F0F0F0F0F0F0F0%', intab.m_role) + 18, LEN(intab.m_role))
 FROM dbm_masterd_rolem intab
 WHERE intab.oid = dbm_masterd_rolem.oid)
WHERE dbm_masterd_rolem.m_role LIKE '%0xF0F0F0F0F0F0F0F0%'
GO
UPDATE dbm_masterd_rolem SET m_role = 
(SELECT SUBSTRING (intab.m_role, 0, PATINDEX ('%0xF0F0F0F000100000%', intab.m_role)) + 
        '0x0000000000000000' + 
        SUBSTRING (intab.m_role, PATINDEX ('%0xF0F0F0F000100000%', intab.m_role) + 18, LEN(intab.m_role))
 FROM dbm_masterd_rolem intab
 WHERE intab.oid = dbm_masterd_rolem.oid)
WHERE dbm_masterd_rolem.m_role LIKE '%0xF0F0F0F000100000%'
GO

-- masterd_StaffMember (m_contact)
INSERT INTO backup_IBS820 
    SELECT oid, 'dbm_masterd_staffmem', 'm_contact', m_contact, NULL 
    FROM dbm_masterd_staffmem
    WHERE m_contact IN (0xF000F0F0F0F00010, 0xF0F0F0F0F0F0F0F0, 0xF0F0F0F000100000)
GO
UPDATE dbm_masterd_staffmem SET m_contact = 0x0000000000000000 
    WHERE m_contact IN (0xF000F0F0F0F00010, 0xF0F0F0F0F0F0F0F0, 0xF0F0F0F000100000)
GO

-- masterd_Company (m_comptype)
INSERT INTO backup_IBS820 
    SELECT oid, 'dbm_masterd_company', 'm_comptype', NULL, m_comptype
    FROM dbm_masterd_company
    WHERE m_comptype LIKE '%0xF000F0F0F0F00010%'
       OR m_comptype LIKE '%0xF0F0F0F0F0F0F0F0%'
       OR m_comptype LIKE '%0xF0F0F0F000100000%'
GO
UPDATE dbm_masterd_company SET m_comptype = 
(SELECT SUBSTRING (intab.m_comptype, 0, PATINDEX ('%0xF000F0F0F0F00010%', intab.m_comptype)) + 
        '0x0000000000000000' + 
        SUBSTRING (intab.m_comptype, PATINDEX ('%0xF000F0F0F0F00010%', intab.m_comptype) + 18, LEN(intab.m_comptype))
 FROM dbm_masterd_company intab
 WHERE intab.oid = dbm_masterd_company.oid)
WHERE dbm_masterd_company.m_comptype LIKE '%0xF000F0F0F0F00010%'
GO
UPDATE dbm_masterd_company SET m_comptype = 
(SELECT SUBSTRING (intab.m_comptype, 0, PATINDEX ('%0xF0F0F0F0F0F0F0F0%', intab.m_comptype)) + 
        '0x0000000000000000' + 
        SUBSTRING (intab.m_comptype, PATINDEX ('%0xF0F0F0F0F0F0F0F0%', intab.m_comptype) + 18, LEN(intab.m_comptype))
 FROM dbm_masterd_company intab
 WHERE intab.oid = dbm_masterd_company.oid)
WHERE dbm_masterd_company.m_comptype LIKE '%0xF0F0F0F0F0F0F0F0%'
GO
UPDATE dbm_masterd_company SET m_comptype = 
(SELECT SUBSTRING (intab.m_comptype, 0, PATINDEX ('%0xF0F0F0F000100000%', intab.m_comptype)) + 
        '0x0000000000000000' + 
        SUBSTRING (intab.m_comptype, PATINDEX ('%0xF0F0F0F000100000%', intab.m_comptype) + 18, LEN(intab.m_comptype))
 FROM dbm_masterd_company intab
 WHERE intab.oid = dbm_masterd_company.oid)
WHERE dbm_masterd_company.m_comptype LIKE '%0xF0F0F0F000100000%'
GO

-- masterd_Subsidiary (m_subsid)
INSERT INTO backup_IBS820 
    SELECT oid, 'dbm_masterd_subsid', 'm_subsid', m_subsid, NULL 
    FROM dbm_masterd_subsid
    WHERE m_subsid IN (0xF000F0F0F0F00010, 0xF0F0F0F0F0F0F0F0, 0xF0F0F0F000100000)
GO
UPDATE dbm_masterd_subsid SET m_subsid = 0x0000000000000000 
    WHERE m_subsid IN (0xF000F0F0F0F00010, 0xF0F0F0F0F0F0F0F0, 0xF0F0F0F000100000)
GO

-- masterd_OrganizationalUnit (m_type)
INSERT INTO backup_IBS820 
    SELECT oid, 'dbm_masterd_ou', 'm_type', m_type, NULL 
    FROM dbm_masterd_ou
    WHERE m_type IN (0xF000F0F0F0F00010, 0xF0F0F0F0F0F0F0F0, 0xF0F0F0F000100000)
GO
UPDATE dbm_masterd_ou SET m_type = 0x0000000000000000
    WHERE m_type IN (0xF000F0F0F0F00010, 0xF0F0F0F0F0F0F0F0, 0xF0F0F0F000100000)
GO