/******************************************************************************
 * Task:        IBS-399 - m2ml - MLI - Definition of available locales
 *
 * Description: This file creates the necessary type info for locales.
 *
 * Repeatable:  yes
 *
 * @version     $Id: U300001u_updateTypeInfo.sql,v 1.1 2010/05/04 07:32:03 btatzmann Exp $
 *
 * @author      Bernhard Tatzmann (BT) 20100420
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
    @l_msg                  VARCHAR (5000), -- the actual message
    
    -- constants:
    @c_PC_create            NAME,           -- procedure code for create
    @c_PC_retrieve          NAME,           -- procedure code for retrieve
    @c_PC_change            NAME,           -- procedure code for change
    @c_PC_copy              NAME,           -- procedure code for copy
    @c_PC_delete            NAME            -- procedure code for delete

-- assign constants:
SELECT
    @c_NOT_OK               = 0,
    @c_ALL_RIGHT            = 1,
    @c_languageId           = 0

-- initialize local variables:
SELECT
    @l_file                 = 'U300002u_updateTypeInfo',
    @l_retValue             = @c_ALL_RIGHT,
    @c_PC_create            = 'create',
    @c_PC_retrieve          = 'retrieve',
    @c_PC_change            = 'change',
    @c_PC_copy              = 'copy',
    @c_PC_delete            = 'delete'

-- body:

    ---------------------------------------------------------------------------
    -- The following types do not have predefined type ids.
    -- This is necessary due to the fact that type ids for other object types
    -- can be set dynamically and to avoid that different types have the same
    -- id.
    -- EXEC p_Type$newLang 0, superTypeCode, isContainer, isInheritable,
    --      isSearchable, showInMenu, showInNews, code, className, languageId,
    --      typeNameName

    -- LocaleContainer
    EXEC p_Type$newLang 0, N'Container', 1, 1, 0, 1, 0, N'LocaleContainer',
        N'ibs.obj.ml.LocaleContainer_01', @c_languageId, N'TN_LocaleContainer_01'
    
    -- Locale
    EXEC p_Type$newLang 0, N'BusinessObject', 0, 1, 0, 0, 0, N'Locale',
        N'ibs.obj.ml.Locale_01', @c_languageId, N'TN_Locale_01'


    SELECT  @l_msg = @l_file + ': Types created.'
    PRINT @l_msg


    -- set the tabs for the object types:
    -- LocaleContainer
    EXEC p_Type$addTabs N'LocaleContainer', N''
        , N'Info', N'Content', N'Rights'
    
    -- Layout
    EXEC p_Type$addTabs N'Locale', N''
        , N'Info', N'Rights'

    SELECT  @l_msg = @l_file + ': Tabs set.'
    PRINT @l_msg


    -- mayContain entries:
    -- EXEC @l_retValue = p_MayContain$new majorTypeCode, minorTypeCode

    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'LocaleContainer'
    EXEC @l_retValue = p_MayContain$new 'ExportContainer', 'Locale'
    EXEC @l_retValue = p_MayContain$new 'LocaleContainer', 'Locale'
    
    SELECT  @l_msg = @l_file + ': MayContain entries created.'
    PRINT @l_msg

    -- Locale
    EXEC p_TVersionProc$new N'Locale', @c_PC_create, N'p_Locale_01$create'
    EXEC p_TVersionProc$new N'Locale', @c_PC_retrieve, N'p_Locale_01$retrieve'
    EXEC p_TVersionProc$new N'Locale', @c_PC_delete, N'p_Locale_01$delete'
    EXEC p_TVersionProc$new N'Locale', @c_PC_change, N'p_Locale_01$change'
    EXEC p_TVersionProc$new N'Locale', @c_PC_copy, N'p_Locale_01$copy'

    SELECT  @l_msg = @l_file + ': TVersion entries created.'
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
