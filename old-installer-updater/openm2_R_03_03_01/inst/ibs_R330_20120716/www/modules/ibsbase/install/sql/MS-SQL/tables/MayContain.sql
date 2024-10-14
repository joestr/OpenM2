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
CREATE TABLE ibs_MayContain
(
    majorTypeId     TYPEID          NOT NULL,   -- the container type
    minorTypeId     TYPEID          NOT NULL,   -- the contained type
    isInherited     BOOL            NOT NULL DEFAULT (0)
                                                -- is this record inherited
                                                -- from the super type of the
                                                -- major type?
)
GO
-- ibs_MayContain