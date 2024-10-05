-------------------------------------------------------------------------------
-- The IBS_DB_ERRORS table incl. indexes. <BR>
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:54 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)  020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.ibs_db_errors
(
   errortype        INTEGER NOT NULL,   -- type of error
   errorno          INTEGER,            -- number of error
   errordate        TIMESTAMP NOT NULL WITH DEFAULT CURRENT TIMESTAMP,
                                        -- date when the error occurred
   errorproc        VARCHAR (255) NOT NULL,
                                        -- procedure where the error
                                        -- occurred
   errorpos         VARCHAR (255),      -- position of error within proc
   errordesc        VARCHAR (5000)      -- description of error
); -- ibs_db_errors
