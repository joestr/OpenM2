/******************************************************************************
 * Task:        TASK FAC02 - SAP
 *              20050525_6 No RIGHTS tab in NewsContainer and Inbox.
 *
 * Description: Create missing rights tabs for private containers.
 *
 * Repeatable:  yes
 *
 * @version     $Id: U24015u_createPrivateRightsTabs.sql,v 1.1 2005/06/06 15:43:52 klaus Exp $
 *
 * @author      Klaus Reimüller (KR) 20050525
 ******************************************************************************
 */ 

PRINT 'starting $RCSFile$...'
GO

-- don't show count messages:
SET NOCOUNT ON
GO

-- delete existing procedure:
EXEC p_dropProc 'pi_createTab'
GO
-- create the new procedure:
CREATE PROCEDURE pi_createTab
(
    -- input parameters:
    @ai_typeCode            NAME,
    @ai_tabCode             NAME
    -- output parameters:
)
AS
DECLARE
    -- constants:

    -- local variables:
    @l_msg                  VARCHAR (2000)  -- output message

    -- assign constants:

    -- initialize local variables:

-- body:
    -- check if the type exists:
    IF EXISTS (
        SELECT  t.id
        FROM    ibs_Type t
        WHERE   t.code = @ai_typeCode
    )
    BEGIN
        -- check if the tab definition exists:
        IF EXISTS (
            SELECT  tab.id
            FROM    ibs_Tab tab
            WHERE   tab.code = @ai_tabCode
        )
        BEGIN
            -- check if the tab for the type already exists:
            IF NOT EXISTS (
                SELECT  co.id
                FROM    ibs_ConsistsOf co, ibs_Tab tab, ibs_Type t
                WHERE   t.actVersion = co.tVersionId
                    AND t.code = @ai_typeCode
                    AND co.tabId = tab.id
                    AND tab.code = @ai_tabCode
            )
            BEGIN
                -- create the desired tab if it does not exist for the current
                -- type:
                INSERT INTO ibs_ConsistsOf
                    (tVersionId, tabId, priority, rights, inheritedFrom)
                SELECT  t.actVersion, tab.id, tab.priority, tab.rights,
                        t.actVersion
                FROM    ibs_Type t, ibs_Tab tab
                WHERE   t.code = @ai_typeCode
                    AND tab.code = @ai_tabCode
                    AND NOT EXISTS
                    (
                        SELECT  co.id
                        FROM    ibs_ConsistsOf co, ibs_Tab tab, ibs_Type t
                        WHERE   t.actVersion = co.tVersionId
                            AND t.code = @ai_typeCode
                            AND co.tabId = tab.id
                            AND tab.code = @ai_tabCode
                    )

                -- check if everything was o.k.:
                IF (@@ROWCOUNT > 0)
                BEGIN
                    SELECT  @l_msg = 'OK: Added tab ''' + @ai_tabCode + '''' +
                            ' to type ''' + @ai_typeCode + '''.'
                    PRINT @l_msg
                END -- if
                ELSE
                BEGIN
                    SELECT  @l_msg =
                            'ERROR: Could not add tab ''' + @ai_tabCode + '''' +
                            ' to type ''' + @ai_typeCode + '''.'
                    PRINT @l_msg
                END -- else
            END -- if
            ELSE
            BEGIN
                SELECT  @l_msg = 'OK: Tab ''' + @ai_tabCode + '''' +
                        ' for type ''' + @ai_typeCode + ''' already existing.'
                PRINT @l_msg
            END -- else
        END -- if
        ELSE
        BEGIN
            SELECT  @l_msg =
                    'ERROR: Definition for tab ''' + @ai_tabCode + '''' +
                    ' does not exist.'
            PRINT @l_msg
        END -- else
    END -- if
    ELSE
    BEGIN
        SELECT  @l_msg = 'ERROR: Definition for type ''' + @ai_typeCode + '''' +
                ' does not exist.'
        PRINT @l_msg
    END -- else
GO
-- pi_createTab

 
EXEC pi_createTab 'Inbox', 'Rights'
EXEC pi_createTab 'NewsContainer', 'Rights'

/*
SELECT t.code, tab.code, co.*
FROM ibs_ConsistsOf co, ibs_Tab tab, ibs_Type t
WHERE co.tVersionId = t.actVersion
AND t.code LIKE @ai_typeCode
AND co.tabId = tab.id
*/

/*
SELECT *
FROM ibs_Type
WHERE code = @ai_typeCode
*/
/*
SELECT *
FROM ibs_ConsistsOf
*/

-- delete not longer needed procedure:
EXEC p_dropProc 'pi_createTab'
GO

-- show count messages again:
SET NOCOUNT OFF
GO

PRINT '$RCSFile$: finished.'
GO
