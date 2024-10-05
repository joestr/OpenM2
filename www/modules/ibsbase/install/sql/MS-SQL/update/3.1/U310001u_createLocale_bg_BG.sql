/******************************************************************************
 * Task:        IBS-718 Create new locale for Bulgaria
 *
 * Description: This file creates the locale bg_BG.
 *
 * Repeatable:  no
 *
 * @version     $Id: U310001u_createLocale_bg_BG.sql,v 1.2 2011/10/18 14:23:53 btatzmann Exp $
 *
 * @author      Bernhard Tatzmann (BT) 20100525
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
    UPDATE ibs_object set state = 1 where name like 'bg_BG'

    -- retrieve the destination folder ('locales')
    EXEC p_KeyMapper$getOid 'locales', 'ibs_instobj', @l_cid OUTPUT
	SELECT  @l_cid_s = dbo.f_byteToString(@l_cid)
   
	-- create locale 'bg_BG' for the domain within the locale
	-- container:
	EXEC @l_retVal = p_Locale_01$create @l_admin, @l_localOp, @c_TVLocale,
		'bg_BG', @l_cid_s, 1, 0, @c_NOOID_s, '',
		@l_localeOid_s OUTPUT
		
	IF (@l_retVal = @c_ALL_RIGHT)   -- locale bg_BG created?
	BEGIN
		EXEC p_stringToByte @l_localeOid_s, @l_localeOid OUTPUT
	
		-- create key mapper for locale bg_BG:
		EXEC p_KeyMapper$new @l_localeOid_s, 'locale_bg_BG', 'ibs_instobj'
	
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
			'bg_BG', N'#CONFVAR.ibsbase.validUntil#', '', 1, 'bg', 'BG', 0
	END -- if locale en_US created
	ELSE                            -- locale en_US not created
	BEGIN
		SELECT  @l_msg = 'cBData: Error when creating Locale bg_BG:' +
				' retVal = ' + CONVERT (VARCHAR (10), @l_retVal) + '.'
		PRINT @l_msg
	END -- else locale en_US not created

GO

COMMIT TRANSACTION
-- show count messages again:
SET NOCOUNT OFF
GO
PRINT '$RCSFile$: finished.'
GO