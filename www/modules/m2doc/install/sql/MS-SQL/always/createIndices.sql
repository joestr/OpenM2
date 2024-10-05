/******************************************************************************
 * create all indices of the whole base system. <BR>
 *
 * @version     $Id: createIndices.sql,v 1.18 2010/01/13 16:41:14 rburgermann Exp $
 *
 * @author      Andreas Jansa (AJ) 000814
 ******************************************************************************
 */
 -- don't show count messages:
SET NOCOUNT ON
GO

--
-- get and drop all indixes where dropping is possible
-- the following indixes can not be dropped:
-- * index created by 'primary key' column constraint
-- * unique index created by 'unique' column constraint
-- * index on text or image structures
-- * indexes on a non-clustered table itself
--
-- only m2 indexes will be selected and dropped
-- prefixes: M2_ MAD_ SP_
-- 
DECLARE 
    @indexName NVARCHAR (64),
    @tableName NVARCHAR (64)

DECLARE cursorAllIndexes CURSOR FOR
    select  i.name, o.name
    from    sysindexes i, sysobjects o
    where   (   o.type = 'U'
            and o.id = i.id
            )                           -- get tables for indixes
        and (   o.name like 'M2_%'      -- get only indixes on m2 tables
            or  o.name like 'MAD_%' 
            or  o.name like 'SP_%'
            )
        and i.name not like 'PK__%'     -- primary key constraint
        and i.name not like 'UQ__%'     -- unique constraint
        and i.name not like '_WA_%'     -- unknown???
        and i.indid <> 255              -- without indexes for text or image
                                        -- data
        and i.indid <> 0                -- without indexes for non-clustered
                                        -- table 

-- open the cursor:
OPEN cursorAllIndexes

-- get the first index:
FETCH NEXT FROM cursorAllIndexes INTO @indexName, @tableName

-- loop through all found indexes:
WHILE (@@FETCH_STATUS <> - 1)           -- another index found?
BEGIN
    -- Because @@FETCH_STATUS may have one of the three values
    -- -2, -1, or 0 all of these cases must be checked.
    -- In this case the tuple is skipped if it was deleted
    -- during the execution of this procedure.
    IF (@@FETCH_STATUS <> -2)
    BEGIN
        -- drop index:
        EXECUTE (N'DROP INDEX ' + @tableName + '.' + @indexName)
    END -- if

    -- get the next index:
    FETCH NEXT FROM cursorAllIndexes INTO @indexName, @tableName
END -- while another index found

-- close cursor:
CLOSE cursorAllIndexes

-- remove cursor from system:
DEALLOCATE cursorAllIndexes
GO

--
-- CREATE ALL INDEXES
--


-- 
-- MODULE: M2
--

-- M2_Article_01
CREATE INDEX IndexArticleDiscussionId ON m2_Article_01 (discussionId)
GO

-- M2_PRODUCT_01
CREATE INDEX IndexProductProductNo ON m2_Product_01 (productNo)
GO

-- M2_PRODUCTCODEVALUES_01
CREATE INDEX IndexProductCValues_01Oid ON m2_ProductCodeValues_01 (productOid, categoryOid)
GO

-- M2_PARTICIPANT_01
CREATE UNIQUE INDEX IndexParticipantOid ON m2_Participant_01 (oid)
GO

-- M2_SHOPPINGCARTENTRY_01
CREATE UNIQUE INDEX IndexCEntry_01Oid ON m2_ShoppingCartEntry_01 (oid)
GO

-- M2_TERMIN_01
CREATE INDEX IndexTerminStartDate ON m2_Termin_01 (startDate)
GO
CREATE INDEX IndexTerminEndDate ON m2_Termin_01 (endDate)
GO

-- don't show count messages:
SET NOCOUNT OFF
GO