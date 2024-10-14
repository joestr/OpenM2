/******************************************************************************
 * Task:        TASK/BUG#xxx - Dummy file for update of type information.
 *
 * Description: This file contains all structural information which is
 *              necessary to create an update file for specific changes
 *              of object types.
 *
 * Repeatable:  yes/no
 *
 * @version     $Id: Uxx000u_updateTypeInfo.sql,v 1.3 2005/02/15 21:21:10 klaus Exp $
 *
 * @author      Klaus Reimüller (KR) 020626
 ******************************************************************************
 */ 

PRINT 'starting $RCSFile$...'
GO

-- don't show count messages:
SET NOCOUNT ON
GO

DECLARE
    -- constants:
    @c_NOT_OK               INT,            -- something went wrong
    @c_ALL_RIGHT            INT,            -- everything was o.k.
    @c_languageId           INT,            -- the current language

    -- local variables:
    @l_file                 VARCHAR (7),    -- name of actual file
    @l_retValue             INT,            -- return value of function
    @l_msg                  VARCHAR (5000)  -- the actual message

-- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_languageId           = 0

-- initialize local variables:
SELECT
    @l_file                 = 'Uxx000u',
    @l_retValue             = @c_ALL_RIGHT

-- body:

    ---------------------------------------------------------------------------
    -- The following types do not have predefined type ids.
    -- This is necessary due to the fact that type ids for other object types
    -- can be set dynamically and to avoid that different types have the same
    -- id.
    -- EXEC p_Type$newLang 0, superTypeCode, isContainer, isInheritable,
    --      isSearchable, showInMenu, showInNews, code, className, languageId,
    --      typeNameName

    -- SAPBCConnector
--    EXEC p_Type$newLang 0, 'Connector', 0, 1, 0, 0, 0, 'SAPBCConnector',
--        'ibs.di.SAPBCConnector_01', @c_languageId,
--        'TN_SAPBCConnector_01'

    SELECT  @l_msg = @l_file + ': Types created.'
    PRINT @l_msg


    -- set the tabs for the object types:
    -- EXEC p_Type$addTabs typeCode, activeTabCode
    --    , tabCode1, tabCode2, ... tabCode10

    -- WorkflowTemplate
--    EXEC p_Type$addTabs 'WorkflowTemplate', ''
--        , 'Info', 'References', 'Rights'

    SELECT  @l_msg = @l_file + ': Tabs set.'
    PRINT @l_msg


    -- mayContain entries:
    -- EXEC @l_retValue = p_MayContain$new majorTypeCode, minorTypeCode

--    EXEC @l_retValue = p_MayContain$new 'ConnectorContainer', 'SAPBCConnector'

    SELECT  @l_msg = @l_file + ': MayContain entries created.'
    PRINT @l_msg


    -- show state message:
    SELECT  @l_msg = @l_file + ': finished.'
    PRINT @l_msg
GO

-- show count messages again:
SET NOCOUNT OFF
GO

PRINT '$RCSFile$: finished.'
GO
