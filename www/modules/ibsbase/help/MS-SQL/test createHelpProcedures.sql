-- PRINT "vor Prc"
if exists (select * from sysobjects where id = object_id('dbo.p_retrieveVariables') and sysstat & 0xf = 4)
	drop procedure dbo.p_retrieveVariables
GO
create procedure p_retrieveVariables (
  --- setting debug to "true" results some debugging messages
  --- set debug to _whatever_ for removing the messages
      @debug varchar(5) OUTPUT,
  -- declare constants:
      @l_domainName   NAME OUTPUT,
      @l_helpPath     VARCHAR (255) OUTPUT,
  -- declare constants:
      @c_TVHelpContainer	 TVERSIONID OUTPUT,
      @c_TVHelpObject 	 TVERSIONID OUTPUT,
      -- declare variables:
      @domainPosNoPath POSNOPATH OUTPUT,
      @tmpUrl VARCHAR(255) OUTPUT,
      @retVal INT OUTPUT,
      @userId USERID OUTPUT, 
      @op RIGHTS OUTPUT,
      @public OBJECTID OUTPUT, 
      @public_s OBJECTIDSTRING OUTPUT,
      @oid_s OBJECTIDSTRING OUTPUT

)
AS
     -- get configuration for installation  (is set in file installConfig.sql)
     /*******************CHANGEABLE CODE BEGIN****************************/
     SELECT @l_domainName = 'ibs',
     		@l_helpPath = "http://adam/m2/help/",
     		@debug = "false",
    /******************CHANGEABLE CODE END*******************************/
    -- set constants
     		@c_TVHelpContainer = 0x01017f01,    -- HelpContainer
            @c_TVHelpObject = 0x01017f11        -- HelpObject
    -- print "Leaving Procedure _retrieveVariables_..."
GO


-------------------------------------------------------------------------------------------

if exists (select * from sysobjects where id = object_id('dbo.p_retValue') and sysstat & 0xf = 4)
	drop procedure dbo.p_retValue
GO
 create procedure p_retValue (
     @input varchar (255),
     @oid_s OBJECTIDSTRING output
)
as
    DECLARE @oid OBJECTID

    --- get oid from desired object
    select @oid = oid
    FROM ibs_Object
    WHERE name = @input
    --- convert oid to oid_s

    EXEC p_byteToString @oid, @oid_s OUTPUT

    -- print "Leaving Procedure _retValue_..."
 GO





