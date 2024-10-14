/******************************************************************************
 * The ibs rights cum table incl. indexes. <BR>
 * The rights cum table contains the cumulated rights for each user within all
 * rights keys.
 * This table contains only tuples for rights keys where the user has already 
 * rights.
 * Rights keys, which does not contain the user, are not necessary within
 * this table.
 *
 * @version     $Id: RightsCum.sql,v 1.3 2005/02/15 21:38:47 klaus Exp $
 *
 * @author      Klaus Reimüller (KR)  990304
 ******************************************************************************
 */
CREATE TABLE ibs_RightsCum
(
    userId      USERID          NOT NULL, -- the user who has the rights
    rKey        ID              NOT NULL, -- the rights key for which the 
                                          -- rights are cumulated
    rights      RIGHTS          NOT NULL  -- the cumulated rights
)
GO
-- ibs_RightsCum
