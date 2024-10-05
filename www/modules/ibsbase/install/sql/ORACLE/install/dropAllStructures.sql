/******************************************************************************
 * Drop all structures of the framework. <BR>
 *
 * @version     $Id: dropAllStructures.sql,v 1.4 2003/10/21 08:53:13 klaus Exp $
 *
 * @author      Andreas Jansa (AJ)  991027
 ******************************************************************************
 */

-- drop tablespace

--    drop tablespace <m2DbName> including contents;

-- drop user and all his objects (procedures ...)

   drop user sa cascade;

exit;
