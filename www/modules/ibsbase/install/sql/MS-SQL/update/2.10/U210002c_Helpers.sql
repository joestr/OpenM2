/******************************************************************************
 * Create several help objects which are used during the migration. <BR>
 *
 * @version     $Id: U210002c_Helpers.sql,v 1.1 2010/02/25 13:53:47 btatzmann Exp $
 *
 * @author      Roland Burgermann (RB)  100105
 ******************************************************************************
 */

/******************************************************************************
 * Function which returns whether the column is ASC or DESC  
 *
 * @input parameters:
 * @param   object_id           Id of the object.
 * @param   index_id            Id of the index.
 * @param   column_id           Id of the column. 
 *
 * @output parameters:
 * @param   NVARCHAR(5)         When ASC then '' otherwise 'DESC'
 */
-- Delete existing function:
EXEC p_dropFunc 'GetIndexColumnOrder'
GO

-- Returns whether the column is ASC or DESC 
CREATE FUNCTION dbo.GetIndexColumnOrder 
( 
    @object_id INT, 
    @index_id TINYINT, 
    @column_id TINYINT 
) 
RETURNS NVARCHAR(5) 
AS 
BEGIN 
    DECLARE @r NVARCHAR(5) 
    SELECT @r = CASE INDEXKEY_PROPERTY 
    ( 
        @object_id, 
        @index_id, 
        @column_id, 
        'IsDescending' 
    ) 
        WHEN 1 THEN N' DESC' 
        ELSE N'' 
    END 
    RETURN @r 
END 
GO
-- GetIndexColumnOrder

/******************************************************************************
 * Function which returns the list of columns in the index  
 *
 * @input parameters:
 * @param   table_name          Name of the table.
 * @param   object_id           Id of the object.
 * @param   index_id            Id of the index. 
 *
 * @output parameters:
 * @param   NVARCHAR(4000)      List of index columns seperated with a comma
 */
-- Delete existing function:
EXEC p_dropFunc 'GetIndexColumns'
GO

-- Returns the list of columns in the index 
CREATE FUNCTION dbo.GetIndexColumns 
( 
    @table_name SYSNAME, 
    @object_id INT, 
    @index_id TINYINT 
) 
RETURNS NVARCHAR(4000) 
AS 
BEGIN 
    DECLARE 
        @colnames NVARCHAR(4000),  
        @thisColID INT, 
        @thisColName SYSNAME 
         
    SET @colnames = INDEX_COL(@table_name, @index_id, 1) 
        + dbo.GetIndexColumnOrder(@object_id, @index_id, 1) 
 
    SET @thisColID = 2 
    SET @thisColName = INDEX_COL(@table_name, @index_id, @thisColID) 
        + dbo.GetIndexColumnOrder(@object_id, @index_id, @thisColID) 
 
    WHILE (@thisColName IS NOT NULL) 
    BEGIN 
        SET @thisColID = @thisColID + 1 
        SET @colnames = @colnames + ', ' + @thisColName 
 
        SET @thisColName = INDEX_COL(@table_name, @index_id, @thisColID) 
            + dbo.GetIndexColumnOrder(@object_id, @index_id, @thisColID) 
    END 
    RETURN @colNames 
END 
GO
-- GetIndexColumns

/******************************************************************************
 * View whichs returns a list of all indexes excluded auto_statistics and 
 * system indexes from Microsoft
 */
-- Delete existing view:
DROP VIEW dbo.vAllIndexes
GO

-- Create the view:
CREATE VIEW dbo.vAllIndexes  
AS 
    SELECT  
        TABLE_NAME = OBJECT_NAME(i.id), 
        INDEX_NAME = i.name, 
        COLUMN_LIST = dbo.GetIndexColumns(OBJECT_NAME(i.id), i.id, i.indid), 
        IS_CLUSTERED = INDEXPROPERTY(i.id, i.name, 'IsClustered'), 
        IS_UNIQUE = INDEXPROPERTY(i.id, i.name, 'IsUnique'), 
        FILE_GROUP = g.GroupName 
    FROM 
        sysindexes i 
    INNER JOIN 
        sysfilegroups g 
    ON 
        i.groupid = g.groupid 
    WHERE 
        (i.indid BETWEEN 1 AND 254) 
        -- leave out AUTO_STATISTICS: 
        AND (i.Status & 64)=0 
        -- leave out system tables: 
        AND OBJECTPROPERTY(i.id, 'IsMsShipped') = 0 
GO
-- vAllIndexes


/******************************************************************************
 * Table which contains a list of stored procedures names including schema name
 * Is used to compare the list of stored procedures after migration
 */
-- Drop the table
DROP TABLE uc_stproclist
GO

-- Create the table
CREATE TABLE uc_stproclist
(
    procname    NVARCHAR(512)   NULL,   -- name of the stored procedure
                                        -- including schema name
    procstatus  NVARCHAR(512)   NULL    -- contains the status of the procudure
)
GO
-- uc_stproclist