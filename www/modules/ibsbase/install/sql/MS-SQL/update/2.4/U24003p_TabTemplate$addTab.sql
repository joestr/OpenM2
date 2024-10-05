/******************************************************************************
 * Update of the p_TabTemplate_01$addTab procedure to enable correct updating
 * of tab settings. <BR>
 *
 * @version     2.40.0001, 05.05.2005 BB
 *
 * @author      Bernd Buchegger (DIBB)  050105
 ******************************************************************************
 */


-- delete existing procedure:
EXEC p_dropProc 'p_TabTemplate_01$addTab'
GO

-- create the new procedure:
CREATE PROCEDURE p_TabTemplate_01$addTab
(
    -- input parameters:
    @ai_oid_s           OBJECTIDSTRING,
    @ai_id              INT,
    @ai_kind            INT,
    @ai_tVersionId      TVERSIONID,
    @ai_fct             INT,
    @ai_priority        INT,
    @ai_name            NAME,
    @ai_desc            DESCRIPTION,
    @ai_code            NAME,
    @ai_class           DESCRIPTION
)
AS
DECLARE
    -- constants:
    @c_ALL_RIGHT        INT,            -- everything was o.k.
    @c_NOT_OK           INT,            -- error

    -- local variables:
    @l_oid              OBJECTID,
    @l_code             NAME,
    @l_id               INT

    -- initialization:
SELECT
    @c_NOT_OK           = 0,
    @c_ALL_RIGHT        = 1

SELECT
    @l_code             = @ai_code

    EXEC p_stringToByte @ai_oid_s, @l_oid OUTPUT

-- body:
    -- drop leading and trailing spaces from the code:
    SELECT  @l_code = LTRIM (RTRIM (@ai_code))

    BEGIN TRANSACTION
        -- check if the tab already exists:
        IF EXISTS ( SELECT  oid
                    FROM    ibs_TabTemplate_01
                    WHERE   oid = @l_oid
                        AND id = @ai_id)
                                        -- the tab exists?
        BEGIN
            -- if the tab exists update the information

            -- get corresponding id from ibs_tab
            SELECT  @l_id = id
            FROM    ibs_Tab
            WHERE   code = @l_code

            -- update the tabTemplate
            UPDATE  ibs_TabTemplate_01
            SET     kind = @ai_kind,
                    tVersionId = @ai_tVersionId,
                    fct = @ai_fct,
                    priority = @ai_priority,
                    name = @ai_name,
                    description = @ai_desc,
                    tabCode = @l_code,
                    class = @ai_class
            WHERE   oid = @l_oid
                AND id = @ai_id

            -- update ibs_tab
            UPDATE  ibs_Tab
            SET     kind = @ai_kind,
                    tVersionId = @ai_tVersionId,
                    fct = @ai_fct,
                    priority = @ai_priority,
                    code = @l_code,
                    class = @ai_class
            WHERE   id = @l_id

            -- update priority in ibs_ConsistsOf
            UPDATE  ibs_ConsistsOf
            SET     priority = @ai_priority
            WHERE   tabId = @l_id

        END -- if the tab exists
        ELSE                            -- the tab does not exist
        BEGIN
            -- create the tab:
            INSERT INTO ibs_TabTemplate_01
                    (oid, id, kind, tVersionId, fct,
                    priority, name, description, tabCode, class)
            VALUES  (@l_oid, @ai_id, @ai_kind, @ai_tVersionId, @ai_fct,
                    @ai_priority, @ai_name, @ai_desc, @l_code, @ai_class)
        END -- else the tab does not exist

    COMMIT TRANSACTION

    RETURN @c_ALL_RIGHT
GO
-- p_TabTemplate_01$addTab
