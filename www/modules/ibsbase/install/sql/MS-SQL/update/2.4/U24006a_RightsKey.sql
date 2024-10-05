/******************************************************************************
 * The ibs rights key table incl. indexes. <BR>
 * The rights key table contains all rights keys defined within the system.
 * The specific data of all rights keys can be found in the table
 * ibs_RightsKeys.
 *
 * @version     $id$
 *
 * @author      Klaus Reimüller (KR)  990304
 ******************************************************************************
 */
CREATE TABLE ibs_RightsKey
(
    id          ID              IDENTITY (1, 1), -- the key id
    rKeysId     ID              NOT NULL,   -- id of entries within table
                                            -- ibs_RightsKeys
    owner       USERID          NOT NULL,   -- the user who is the owner of
                                            -- the objects having this RightsKey
    oid         OBJECTID        NULL,       -- object id of the rights key
                                            -- (used if the key itself is a 
                                            -- business object)
    cnt         INT             NOT NULL DEFAULT (0)
                                            -- number of tuples for the actual
                                            -- key witin ibs_RightsKeys
)
GO
-- ibs_RightsKey
 
-- ibs_RightsKey indexes
CREATE UNIQUE INDEX I_RightsKeyId ON ibs_RightsKey (id)
GO
CREATE INDEX I_RightsKeyRKeysId ON ibs_RightsKey (rKeysId)
GO
CREATE INDEX I_RightsKeyRKeysIdOwner ON ibs_RightsKey (rKeysId, owner)
GO
