/******************************************************************************
 * The ibs MayContain table. <BR>
 * The mayContain table contains the information about business objects of
 * which type may contain business objects of which other types.
 *
 * @version     2.10.0002, 17.10.2000
 *
 * @author      Rahul Soni (RS)  000628
 ******************************************************************************
 */

-- create the table:
CREATE TABLE /*USER*/ibs_MayContain
(
    majorTypeId     NUMBER (10,0)   NOT NULL,   -- the container type
    minorTypeId     NUMBER (10,0)   NOT NULL,   -- the contained type
    isInherited     NUMBER (1,0)                -- is this record inherited
                                                -- from the super type of
                                                -- the major type?
) /*TABLESPACE*/;

-- set default values:
ALTER TABLE /*USER*/ibs_MayContain MODIFY (isInherited DEFAULT 0);

EXIT;
