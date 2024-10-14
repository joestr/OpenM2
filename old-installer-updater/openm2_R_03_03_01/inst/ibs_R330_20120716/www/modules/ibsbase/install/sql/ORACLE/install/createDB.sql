/******************************************************************************
 * Create Databasemedium (.dbf file) and database with standarduser. <BR>
 *
 * @version     1.11.0000, 27.10.1999
 *
 * @author      Andreas Jansa (AJ)  991027
 *
 * <DT><B>Updates:</B>
 ******************************************************************************
 */

-- create tablespace and medium (.dbf file) 

--    CREATE TABLESPACE #m2DbName# DATAFILE '#m2DbPath#/#m2DbName#.dbf' SIZE
--    #m2DbSizeMb#M REUSE AUTOEXTEND ON NEXT 1M MAXSIZE #m2DbSizeMb#M;

-- create user

    CREATE USER sa IDENTIFIED BY sa DEFAULT TABLESPACE m2;
    GRANT CONNECT,RESOURCE, drop any sequence, alter any sequence, create any sequence TO sa;

exit;
