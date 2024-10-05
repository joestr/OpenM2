/******************************************************************************
 * Task:        TASK/BUG#xxx - Dummy file for update of type information.
 *
 * Description: This file contains all structural information which is
 *              necessary to create an update file for specific changes
 *              of object types.
 *
 * Repeatable:  yes/no
 *
 * @version     $Id: U000u_updateTypeInfo.sql,v 1.2 2003/10/06 22:06:00 klaus Exp $
 *
 * @author      Klaus Reimüller (KR) 020626
 ******************************************************************************
 */ 

-- p_Type$newLang (id, superTypeCode, isContainer, isInheritable,
--      isSearchable, showInMenu, showInNews, code, className, languageId,
--      typeNameName);
-- ex.:
-- p_Type$newLang (stringToInt ('0x01010050'), 'BusinessObject', 0, 1, 1, 0, 1,
--    'Attachment', 'ibs.bo.Attachment_01', @c_languageId, 'TN_Attachment_01');

DECLARE
    -- constants:
    c_NOT_OK                CONSTANT INTEGER := 0; -- something went wrong
    c_ALL_RIGHT             CONSTANT INTEGER := 1; -- everything was o.k.
    c_languageId            CONSTANT INTEGER := 0; -- the current language

    -- local variables:
    l_file                  VARCHAR2 (5) := 'U000u'; -- name of actual file
    l_retValue              INTEGER := c_ALL_RIGHT; -- return value of function

-- body:
BEGIN
    NULL;                               -- just for the case of nothing done

    ---------------------------------------------------------------------------
    -- The following types do not have predefined type ids.
    -- This is necessary due to the fact that type ids for other object types
    -- can be set dynamically and to avoid that different types have the same
    -- id.
    -- p_Type$newLang (id, superTypeCode, isContainer, isInheritable,
    --      isSearchable, showInMenu, showInNews, code, className, languageId,
    --      typeNameName);

--    -- SAPBCConnector
--    p_Type$newLang (0, 'Connector', 0, 1, 0, 0, 0, 'SAPBCConnector',
--        'ibs.di.SAPBCConnector_01', c_languageId, 'TN_SAPBCConnector_01');

    debug (l_file || ': Types created.');


    -- set the tabs for the object types:
    -- p_Type$addTabs (typeCode, activeTabCode
    --    , tabCode1, tabCode2, ... tabCode10);

--    p_Type$addTabs ('WorkflowTemplate', NULL
--        , 'Info', 'References', 'Rights', '', '', '', '', '', '', '');

    debug (l_file || ': Tabs set.');

    
    -- mayContain entries:
    -- l_retValue := p_MayContain$new (majorTypeCode, minorTypeCode);

--    l_retValue := p_MayContain$new ('ConnectorContainer', 'HTTPMultipartConnector');
--    l_retValue := p_MayContain$new ('ConnectorContainer', 'SAPBCConnector');

    debug (l_file || ': MayContain entries created.');


    -- show state message:
    debug (l_file || ': finished.');
END;
/

COMMIT WORK;

EXIT;
