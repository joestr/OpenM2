/******************************************************************************
 * The ibs rights keys table incl. indexes. <BR>
 * The rights keys table contains all rights keys defined within the system.
 * A rights key is a set of relationships person/rights where
 * - person is the person (group or user) who has the rights,
 * - rights are the rights the person has.
 * The objects' rights are defined through the rights key of the object.
 *
 * @version     $Id: RightsKeys.sql,v 1.3 2005/02/15 21:38:47 klaus Exp $
 *
 * @author      Klaus Reimüller (KR)  990304
 ******************************************************************************
 */
CREATE TABLE ibs_RightsKeys
(
    id          ID              NOT NULL, -- the key id
    oid         OBJECTID        NULL,     -- object id of the rights key
                                          -- (used if the key itself is a 
                                          -- business object)
    rPersonId   INT             NOT NULL, -- the person who has the rights
    rights      RIGHTS          NOT NULL, -- the rights the person has
    cnt         INT             NOT NULL DEFAULT (0),
                                          -- number of tuples for the actual key
    -- Single rights: Introduced to speed up rights cumulation
    r00     INT NULL,
    r01     INT NULL,
    r02     INT NULL,
    r03     INT NULL,
    r04     INT NULL,
    r05     INT NULL,
    r06     INT NULL,
    r07     INT NULL,
    r08     INT NULL,
    r09     INT NULL,
    r0A     INT NULL,
    r0B     INT NULL,
    r0C     INT NULL,
    r0D     INT NULL,
    r0E     INT NULL,
    r0F     INT NULL,
    r10     INT NULL,
    r11     INT NULL,
    r12     INT NULL,
    r13     INT NULL,
    r14     INT NULL,
    r15     INT NULL,
    r16     INT NULL,
    r17     INT NULL,
    r18     INT NULL,
    r19     INT NULL,
    r1A     INT NULL,
    r1B     INT NULL,
    r1C     INT NULL,
    r1D     INT NULL,
    r1E     INT NULL,
    r1F     INT NULL
)
GO
-- ibs_RightsKeys
 