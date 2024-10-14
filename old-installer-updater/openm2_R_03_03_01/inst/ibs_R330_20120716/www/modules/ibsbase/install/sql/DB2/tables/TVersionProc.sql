-------------------------------------------------------------------------------
-- The ibs TVersionProc table incl. indexes. <BR>
-- This table contains names of standard stored procedures for each type
-- version.
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:55 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)    020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_TVERSIONPROC
(
    TVERSIONID      INTEGER NOT NULL WITH DEFAULT 0,
                                            -- id of type version for which
                                            -- to define the procedures
    CODE            VARCHAR (63) NOT NULL WITH DEFAULT 'undefined',
                                            -- definite description of tab 
    INHERITEDFROM   INTEGER NOT NULL WITH DEFAULT 0,
                                            -- id of type version from which 
                                            -- this tuple was inherited
    NAME            VARCHAR (63) NOT NULL WITH DEFAULT 'undefined'
                                            -- unique name of procedure
);

-- Create index statements:
CREATE UNIQUE INDEX IBSDEV1.I_TVERS_PROCIDCOD ON IBSDEV1.IBS_TVERSIONPROC
    (TVERSIONID ASC, CODE ASC);
