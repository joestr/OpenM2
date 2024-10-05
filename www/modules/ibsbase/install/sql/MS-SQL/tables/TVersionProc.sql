/******************************************************************************
 * The ibs TVersionProc table incl. indexes. <BR>
 * This table contains names of standard stored procedures for each type
 * version.
 *
 * @version     2.10.0001, 22.01.2001
 *
 * @author      Mario Oberdorfer (MO)  010122
 ******************************************************************************
 */
CREATE TABLE ibs_TVersionProc
(
    tVersionId      TVERSIONID      NOT NULL,   -- id of type version for which
                                                -- to define the procedures
    code            NAME            NOT NULL,   -- definite description of tab 
    inheritedFrom   TVERSIONID      NOT NULL,   -- id of type version from which 
                                                -- this tuple was inherited
    name            STOREDPROCNAME  NOT NULL    -- unique name of procedure
)
GO 
-- ibs_TVersionProc