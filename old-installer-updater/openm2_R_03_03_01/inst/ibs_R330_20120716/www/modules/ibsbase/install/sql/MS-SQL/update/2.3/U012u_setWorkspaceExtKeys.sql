/******************************************************************************
 * Task:        Set EXTKEYs for existing workspace objects.
 *
 * Description: Set the EXTKEYs for all predefined objects in the user
 *              workspaces.
 *
 * Repeatable:  yes
 *
 * @version     $Id: U012u_setWorkspaceExtKeys.sql,v 1.2 2010/11/09 13:01:20 rburgermann Exp $
 *
 * @author      Klaus Reimueller (KR) 27.07.2004
 ******************************************************************************
 */ 


-- don't show count messages:
SET NOCOUNT ON
GO


/******************************************************************************
 * Set the EXTKEY for a specific object within the workspaces. <BR>
 * All user workspaces are searched for this object and the EXTKEY ist set, if
 * it is currently not existing.
 *
 * @input parameters:
 * @param   @ai_idDomain            Domain for EXTKEY.
 * @param   @ai_id                  EXTKEY id. To this value the workspace user
 *                                  name is appended like this:
 *                                  @ai_id + '_' + username
 *                                  check).
 * @param   @ai_typeCode            The type code of the object to search for.
 * @param   @ai_name                Name of the object.
 * @param   @ai_levels              The number of levels the object is below
 *                                  the workspace (1, 2, ...)
 *
 * @output parameters: none
 */
 
-- delete existing procedure: 
EXEC p_dropProc 'pi_setExtKey'
GO

-- create the new procedure:
CREATE PROCEDURE pi_setExtKey
(
    -- input parameters:
    @ai_idDomain            VARCHAR (63),
    @ai_id                  VARCHAR (255),
    @ai_typeCode            NAME,
    @ai_name                NAME,
    @ai_levels              INT
    -- output parameters:
)
AS
DECLARE
    -- constants:
    @c_ALL_RIGHT            INT,            -- everything was o.k.

    -- local variables:
    @l_retValue             INT,            -- return value of this function
    @l_msg                  VARCHAR (255)   -- output message

    -- assign constants:
SELECT
    @c_ALL_RIGHT            = 1
    
    -- initialize local variables and return values:
SELECT
    @l_retValue             = @c_ALL_RIGHT,
    @l_msg                  = '    ' + @ai_name

-- body:
    -- display the actual object name:
    RAISERROR (@l_msg, 0, 1) WITH NOWAIT

    BEGIN TRANSACTION

    SELECT  @ai_id = @ai_id + '_'

    -- create the entries in the keymapper table:
    INSERT INTO ibs_KeyMapper
            (oid, id, idDomain)
    SELECT  o.oid, @ai_id + u.name AS id, @ai_idDomain AS idDomain
    FROM    ibs_Object o, ibs_User u, ibs_Workspace wsp, ibs_Object wspO,
            ibs_Type t, ibs_TVersion tv
    WHERE   u.id = wsp.userId
        AND wsp.workspace = wspO.oid
        AND wspO.state = 2
        AND o.posNoPath LIKE wspO.posNoPath + '%'
        AND o.id <> wspO.id
        AND o.state = 2
        AND o.oLevel = wspO.oLevel + @ai_levels
        AND o.name = @ai_name
        AND o.tVersionId = tv.id
        AND tv.typeId = t.id
        AND t.code = @ai_typeCode
        AND NOT EXISTS
            (
                SELECT  *
                FROM    ibs_KeyMapper km
                WHERE   idDomain = @ai_idDomain
                    AND id = @ai_id + u.name
            )

    -- drop duplicate entries:
    DELETE  ibs_KeyMapper
    WHERE   oid NOT IN
            (
                SELECT  MIN (km.oid) AS oid
                FROM    ibs_KeyMapper km, ibs_Object o
                WHERE   km.idDomain = @ai_idDomain
                    AND km.id LIKE @ai_id + '%'
                    AND km.oid = o.oid
                    AND o.state = 2
                GROUP BY km.idDomain, km.id
            )
        AND idDomain = @ai_idDomain
        AND id LIKE @ai_id + '%'

    COMMIT TRANSACTION
GO
-- pi_setExtKey


DECLARE
    -- constants:

    -- local variables:
    @l_file                 VARCHAR (5),    -- name of actual file
    @l_error                INT,            -- the actual error code
    @l_ePos                 VARCHAR (255),  -- error position description
    @l_msg                  VARCHAR (255)   -- the actual message

-- assign constants:

-- initialize local variables:
SELECT
    @l_file = 'U012u',
    @l_error = 0

-- body:
    -- display a message:
    SELECT  @l_msg = @l_file +
                     ' - Setted EXTKEY for the following workspace objects:'
    RAISERROR (@l_msg, 0, 1) WITH NOWAIT

    -- set the extkeys for predefined objects:
    EXEC pi_setExtKey 'ibs_instobj', 'wsp_workbox',
        'Container', 'Arbeitskorb', 1
    EXEC pi_setExtKey 'ibs_instobj', 'wsp_workboxnote',
        'Note', 'Willkommen', 2
    EXEC pi_setExtKey 'ibs_instobj', 'wsp_hotlist',
        'ReferenzContainer', 'Hotlist', 1
    EXEC pi_setExtKey 'ibs_instobj', 'wsp_news',
        'NewsContainer', 'Neuigkeiten', 1
    EXEC pi_setExtKey 'ibs_instobj', 'wsp_inbox',
        'Inbox', 'Eingangskorb', 1
    EXEC pi_setExtKey 'ibs_instobj', 'wsp_outbox',
        'SentObjectContainer', 'Ausgangskorb', 1
    EXEC pi_setExtKey 'ibs_instobj', 'wsp_checkoutobjects',
        'QueryExecutive', 'Ausgecheckte Objekte', 1
    EXEC pi_setExtKey 'm2_instobj', 'wsp_orders',
        'OrderContainer', 'Bestellungen', 1
    EXEC pi_setExtKey 'm2_instobj', 'wsp_shoppingcart',
        'ShoppingCart', 'Warenkorb', 1
    EXEC pi_setExtKey 'pmc_instobj', 'wsp_keywords',
        'RecordPrivateKeywordContainer', 'Persönliche Schlagwörter', 1
    EXEC pi_setExtKey 'pmc_instobj', 'wsp_contacts',
        'PrivateContactsContainer', 'Kontakte', 1
    EXEC pi_setExtKey 'pmc_instobj', 'wsp_todolist',
        'QueryExecutive', 'To-Do Liste', 1

    -- update the tabs:
    -- rights tab:
    UPDATE  ibs_Tab
    SET     class = 'ibs.obj.user.RightsContainer_01'
    WHERE   class = 'ibs.user.RightsContainer_01'

    -- log tab:
    UPDATE  ibs_Tab
    SET     class = 'ibs.obj.log.LogView_01'
    WHERE   class = 'ibs.bo.LogView_01'

    -- jump to end of code block:
    GOTO finish

finish:
    -- show state message:
    SELECT  @l_msg = @l_file + ': finished'
    PRINT @l_msg
GO

-- delete the procedure: 
EXEC p_dropProc 'pi_setExtKey'
GO

-- show count messages again:
SET NOCOUNT OFF
GO
