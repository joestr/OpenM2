-------------------------------------------------------------------------------
-- Create Databasemedium (.dbf file) and database with standarduser. <BR>
--
-- @version     $Revision: 1.1 $, $Date: 2002/12/05 19:39:10 $
--              $Author: kreimueller $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS) 020828
-------------------------------------------------------------------------------
--/

-- create tablespace and medium (.dbf file) 

--    CREATE TABLESPACE #m2DbName# DATAFILE '#m2DbPath#/#m2DbName#.dbf' SIZE
--    #m2DbSizeMb#M REUSE AUTOEXTEND ON NEXT 1M MAXSIZE #m2DbSizeMb#M;

-- create user

--    CREATE USER sa IDENTIFIED BY sa DEFAULT TABLESPACE m2;
--    GRANT CONNECT,RESOURCE, drop any sequence, alter any sequence, create any sequence TO sa;

