/******************************************************************************
 * Save the list of the actual stored procedures to a specific database table<BR>
 *
 * @version     $Id: U210002i_saveListOfStProc.sql,v 1.1 2010/02/25 13:53:47 btatzmann Exp $
 *
 * @author      Roland Burgermann (RB)  100105
 ******************************************************************************
 */

-- Save the list of the actual stored procedures to a specific database table
INSERT INTO uc_stproclist 
    (procname, procstatus)
SELECT b.specific_schema + '.' + a.name,
       'MISSING IN R3.0.0'
FROM sys.objects a, 
     INFORMATION_SCHEMA.ROUTINES b
WHERE a.type = 'P'
  AND a.is_ms_shipped = 0 
  AND a.name not like 'dt%'
  AND a.name not like 'uc_migrateOneColumn'  
  AND a.name = b.ROUTINE_NAME
ORDER BY 1
GO