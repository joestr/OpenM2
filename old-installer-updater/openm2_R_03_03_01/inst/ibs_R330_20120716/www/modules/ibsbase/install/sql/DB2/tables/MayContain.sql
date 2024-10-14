-------------------------------------------------------------------------------
-- The ibs MayContain table. <BR>
-- The mayContain table contains the information about business objects of
-- which type may contain business objects of which other types.
--
-- @version     $Revision: 1.2 $, $Date: 2003/10/21 22:14:54 $
--              $Author: klaus $
--
-- @author      Zdenek Kyncl (ZK) and Marcel Samek (MS)  020803
-------------------------------------------------------------------------------
--/

-- Create table statement:
CREATE TABLE IBSDEV1.IBS_MAYCONTAIN
(
    MAJORTYPEID         INTEGER NOT NULL,   
                                        -- the container type
    MINORTYPEID         INTEGER NOT NULL,   
                                        -- the contained type
    ISINHERITED         SMALLINT NOT NULL WITH DEFAULT 0
                                        -- is this record inherited
                                        -- from the super type of the
                                        -- major type?
);
