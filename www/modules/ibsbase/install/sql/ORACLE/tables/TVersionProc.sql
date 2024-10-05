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
CREATE TABLE /*USER*/ibs_TVersionProc
(
    tVersionId      INTEGER         NOT NULL,   -- id of type version for which
                                                -- to define the procedures
    code            VARCHAR2 (63)   NOT NULL,   -- definite description of tab
    inheritedFrom   INTEGER         NOT NULL,   -- id of type version from which
                                                -- this tuple was inherited
    name            VARCHAR2 (63)   NOT NULL    -- unique name of procedure
) /*TABLESPACE*/;

-- set default values:
ALTER TABLE /*USER*/ibs_TVersionProc MODIFY (tVersionId DEFAULT 0);
ALTER TABLE /*USER*/ibs_TVersionProc MODIFY (code DEFAULT 'undefined');
ALTER TABLE /*USER*/ibs_TVersionProc MODIFY (inheritedFrom DEFAULT 0);
ALTER TABLE /*USER*/ibs_TVersionProc MODIFY (name DEFAULT 'undefined');

exit;
