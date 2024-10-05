/******************************************************************************
 * Task:        IBS-399 - m2ml - MLI - Definition of available locales
 *
 * Description: This file creates the locale container, the default
 *              locale en_US and the locale de_AT.
 *
 * Repeatable:  no
 *
 * @version     $Id: U300001w_createLocales.sql,v 1.5 2010/06/09 16:20:42 btatzmann Exp $
 *
 * @author      Bernhard Tatzmann (BT) 20100430
 ******************************************************************************
 */ 

PRINT 'starting $RCSFile$...'
GO

-- don't show count messages:
SET NOCOUNT ON
BEGIN TRANSACTION
GO

-- declare variables:
DECLARE
    -- constants:
    @c_ALL_RIGHT            INT,            -- everything's all right
    @c_TVLocaleContainer    INT,            -- tVersionId of locale container
    @c_TVLocale             INT,            -- tVersionId of locale
    @c_NOOID                OBJECTID,       -- default value for no defined oid
    @c_NOOID_s              OBJECTIDSTRING, -- no oid as string
    @c_languageId           INT,            -- the current language

    -- local variables:
    @l_retVal               INT,            -- return value of function
    @l_cid                  OBJECTID,       -- oid of actual container
    @l_cid_s                OBJECTIDSTRING, -- oid of act. container as string
    @l_localeOid            OBJECTID,       -- oid of actual business object
    @l_localeOid_s          OBJECTIDSTRING, -- oid of actual BO as string
    @l_name                 NAME,           -- name of business object
    @l_desc                 DESCRIPTION,    -- description of business object
    @l_rights               RIGHTS,         -- the current rights
    @l_admin                USERID,         -- id of system administrator
    @l_adminOid             OBJECTID,       -- oid of system administrator
    @l_adminOid_s           OBJECTIDSTRING, -- string representation of oid
    @l_structAdminGroup     GROUPID,        -- id of group structure administrators
    @l_structAdminGroupOid  OBJECTID,       -- oid of group structure administrators
    @l_structAdminGroupOid_s OBJECTIDSTRING,-- string representation of group structure administrators
    @l_allUserGroup         GROUPID,        -- id of all user group
    @l_allUserGroupOid      OBJECTID,       -- oid of all user group
    @l_allUserGroupOid_s    OBJECTIDSTRING, -- string representation of all user group
    @l_public               OBJECTID,       -- oid of public container
    @l_public_s             OBJECTIDSTRING, -- oid of public container as string
    @l_msg                  NVARCHAR (255), -- current message
    @l_localOp              INTEGER         -- operation for local operations

-- assign constants:
SELECT
    @c_ALL_RIGHT = 1,
    @c_NOOID = 0x0000000000000000,
    @c_NOOID_s = '0x0000000000000000',
    @c_languageId = 0
    
    -- initialize local variables:
SELECT
    @l_localOp              = 0

-- body:
--*****************************************************************************
--** Create Locale Management                                                **
--*****************************************************************************

    -- retrieve TVersion ids:
    SELECT  @c_TVLocaleContainer = id
    FROM    ibs_tversion
    WHERE   code = 'LocaleContainer_01'
        
    SELECT  @c_TVLocale = id
    FROM    ibs_tversion
    WHERE   code = 'Locale_01'

    -- retrieve the domain admin:
    EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domAdmin',
        @l_name OUTPUT, @l_desc OUTPUT
        
    SELECT  @l_admin = id
    FROM    ibs_user
    WHERE   name = @l_name

    -- retrieve id of group structure administrators:
    EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domGroupStructAdmins', @l_name OUTPUT, @l_desc OUTPUT

    SELECT  @l_structAdminGroup = id
    FROM    ibs_Group
    WHERE   name = @l_name

    -- retrieve id of group for all users:    
    EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domGroupAll', @l_name OUTPUT, @l_desc OUTPUT

    SELECT  @l_allUserGroup = id
    FROM    ibs_Group
    WHERE   name = @l_name

    -- cleanup
    UPDATE ibs_object set state = 1 where tVersionId = @c_TVLocaleContainer or tVersionId = @c_TVLocale

    -- retrieve the destination folder ('Administration')
    EXEC p_KeyMapper$getOid 'menuPublic', 'ibs_instobj', @l_public OUTPUT
	SELECT  @l_public_s = dbo.f_byteToString(@l_public)
  
    -- create locale container:
    EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domLocales',
        @l_name OUTPUT, @l_desc OUTPUT
    EXEC @l_retVal = p_Object$create @l_admin, @l_localOp, @c_TVLocaleContainer,
        @l_name, @l_public_s, 1, 0, @c_NOOID_s, @l_desc,
        @l_cid_s OUTPUT
        
    IF (@l_retVal = @c_ALL_RIGHT)       -- locale management created correctly?
    BEGIN 
        EXEC p_stringToByte @l_cid_s, @l_cid OUTPUT

        -- create key mapper for locale container:
        EXEC p_KeyMapper$new @l_cid_s, N'locales', N'ibs_instobj'
    
        -- set rights on locale container:
        SELECT  @l_rights = SUM (id)
        FROM    ibs_Operation
        WHERE   name IN (N'view', N'read', N'viewRights',
                N'new', N'addElem', N'delElem', N'viewElems', N'viewProtocol')
        EXEC p_Rights$setRights @l_cid, @l_structAdminGroup, @l_rights, 1
        EXEC p_Rights$setRights @l_cid, @l_allUserGroup, 0, 1
    
        -- create locale 'en_US' for the domain within the locale
        -- container:
        EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domLocaleEnUS', @l_name OUTPUT, @l_desc OUTPUT
    
        EXEC @l_retVal = p_Locale_01$create @l_admin, @l_localOp, @c_TVLocale,
            @l_name, @l_cid_s, 1, 0, @c_NOOID_s, @l_desc,
            @l_localeOid_s OUTPUT
            
        IF (@l_retVal = @c_ALL_RIGHT)   -- locale en_US created?
        BEGIN
            EXEC p_stringToByte @l_localeOid_s, @l_localeOid OUTPUT
        
            -- create key mapper for public container:
            EXEC p_KeyMapper$new @l_localeOid_s, 'locale_en_US', 'ibs_instobj'
        
            -- set rights on locale:
            SELECT  @l_rights = SUM (id)
            FROM    ibs_Operation
            EXEC p_Rights$setRights @l_localeOid, @l_structAdminGroup, @l_rights, 1
            SELECT  @l_rights = SUM (id)
            FROM    ibs_Operation
            WHERE   name IN (N'view', N'read')
            EXEC p_Rights$setRights @l_localeOid, @l_allUserGroup, @l_rights, 1
        
            -- set locale as default locale and set data for locale:
            EXEC @l_retVal = p_Locale_01$change @l_localeOid_s, @l_admin, 0,
                @l_name, N'#CONFVAR.ibsbase.validUntil#', @l_desc, 1, 'en', 'US', 1
        END -- if locale en_US created
        ELSE                            -- locale en_US not created
        BEGIN
            SELECT  @l_msg = 'cBData: Error when creating Locale en_US:' +
                    ' retVal = ' + CONVERT (VARCHAR (10), @l_retVal) + '.'
            PRINT @l_msg
        END -- else locale en_US not created
    
        -- create locale 'de_AT' for the domain within the locale
        -- container:
        EXEC p_ObjectDesc_01$get @c_languageId, N'OD_domLocaleDeAT', @l_name OUTPUT, @l_desc OUTPUT
    
        EXEC @l_retVal = p_Locale_01$create @l_admin, @l_localOp, @c_TVLocale,
            @l_name, @l_cid_s, 1, 0, @c_NOOID_s, @l_desc,
            @l_localeOid_s OUTPUT
        
        IF (@l_retVal = @c_ALL_RIGHT)   -- locale de_AT created?
        BEGIN
            EXEC p_stringToByte @l_localeOid_s, @l_localeOid OUTPUT
            
            -- set data for locale:
            EXEC @l_retVal = p_Locale_01$change @l_localeOid_s, @l_admin, 0,
                @l_name, N'#CONFVAR.ibsbase.validUntil#', @l_desc, 1, 'de', 'AT', 0
        
            -- create key mapper for public container:
            EXEC p_KeyMapper$new @l_localeOid_s, 'locale_de_AT', 'ibs_instobj'
        
            -- set rights on locale:
            SELECT  @l_rights = SUM (id)
            FROM    ibs_Operation
            EXEC p_Rights$setRights @l_localeOid, @l_structAdminGroup, @l_rights, 1
            SELECT  @l_rights = SUM (id)
            FROM    ibs_Operation
            WHERE   name IN (N'view', N'read')
            EXEC p_Rights$setRights @l_localeOid, @l_allUserGroup, @l_rights, 1
        END -- if locale de_AT created
        ELSE                            -- locale de_AT not created
        BEGIN
            SELECT  @l_msg = 'cBData: Error when creating Locale de_AT:' +
                    ' retVal = ' + CONVERT (VARCHAR (10), @l_retVal) + '.'
            PRINT @l_msg
        END -- else locale de_AT not created
    END -- if locale management created correctly
    ELSE                                -- locale management not created
                                        -- correctly
    BEGIN
        SELECT  @l_msg = 'cBData: Error when creating Locale Management:' +
                ' retVal = ' + CONVERT (VARCHAR (10), @l_retVal) + '.'
        PRINT @l_msg
    END -- else locale management not created correctly
GO

COMMIT TRANSACTION
-- show count messages again:
SET NOCOUNT OFF
GO
PRINT '$RCSFile$: finished.'
GO