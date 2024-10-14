SET NEWPAGE 0
SET SPACE 0
SET LINESIZE 160
SET PAGESIZE 0
SET ECHO OFF
SET FEEDBACK OFF
SET HEADING OFF

select 'ALTER PROCEDURE ' || object_name || ' COMPILE;' 
from obj where status = 'INVALID'
and object_type = 'PROCEDURE';

exit;
