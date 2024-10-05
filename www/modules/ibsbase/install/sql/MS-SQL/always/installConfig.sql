/******************************************************************************
 * All data for m2 - installation wich had to be set from installer. <BR>
 *
 * @version     $Id: installConfig.sql,v 1.16 2010/01/13 16:42:13 rburgermann Exp $
 *
 * @author      Andreas Jansa (AJ)  000121
 ******************************************************************************
 */

-- don't show count messages:
SET NOCOUNT ON
GO

--########## begin of changeable code ... #####################################
--
--  DomainName
--  The Name of the domain to be installed.
--  Example:   SELECT  @c_domainName = 'ibs'
--
DECLARE @c_domainName   NAME
SELECT  @c_domainName = N'@DOMAINNAME@'

-- 
-- absolute m2-basepath
-- absolute path of m2-webdirectory (under NT, use double '\\' for one '\' in path)
-- = directory wich is used for uploading
-- Example: SELECT  @c_absBasePath = 'c:\\InetPub\\wwwroot\\m2\\'
--
DECLARE @c_absBasePath   NVARCHAR (255)
SELECT  @c_absBasePath = N'@ABSBASEPATH@'

--
-- m2 - HomepagePath of current domain to be installed
-- part of URL wich is used after webroot of domain - this means if you use 
-- 'http://www.netbiz.to/m2/' as full - m2 root for domain, set c_homepagePath to '/m2/' 
-- Example: c_homepagePath CONSTANT NVARCHAR (255) := '/m2/';
--
DECLARE @c_homepagePath   NVARCHAR (255)
SELECT  @c_homepagePath = N'@HOMEPAGEPATH@'

--
-- m2 - fileuploadbasepath
-- part of URL to directory after WebRoot-Address wich is used for fileupload - this means if you use 
-- 'http://www.netbiz.to/m2/' as full - m2 uploadWwwPath, set c_uploadWwwPath to '/m2/' 
-- Example: c_uploadWwwPath CONSTANT NVARCHAR (255) := '/m2/';
--
DECLARE @c_uploadWwwPath   NVARCHAR (255)
SELECT  @c_uploadWwwPath = N'@UPLOADWWWPATH@'

--
-- customer name
-- unique identifier for m2 customer
--
DECLARE @c_customerName   NVARCHAR (255) 
SELECT  @c_customerName = N'@CUSTOMERNAME@'

--
-- system name
-- unique identifier for m2 installation
--
DECLARE @c_systemName   NVARCHAR (255) 
SELECT  @c_systemName = N'@SYSTEMNAME@'

--
--  Number of Build
--  The current buildnumber, build like version.postversion.incremental-number.
--  The incremental-number starts with 300 on 1.1.1999
--  Example:   SELECT  @c_BUILD_NO = '2.23.1443'
--
DECLARE @c_BUILD_NO       NVARCHAR (18)
SELECT  @c_BUILD_NO     = N'#BUILD_NUMBER#'

--
--  Name of Version
--  The Name of the current version.
--  Example:   SELECT  @c_VERSION_NAME = '2.3 Beta'
--
DECLARE @c_VERSION_NAME   NAME 
SELECT  @c_VERSION_NAME = N'#VERSION_NAME#'

--  Language
--  The Language of the current version
--
--  Example:   SELECT  @c_LANGUAGE = 'ENGLISH'
--
DECLARE @c_LANGUAGE   NVARCHAR (18)
SELECT  @c_LANGUAGE = N'@LANGUAGE@'

--########## ... end of changeable code #######################################

DECLARE
    @l_date                 DATETIME,       -- the actual date and time
    @l_value                NVARCHAR (255)   -- the actual value

    -- set the system values:
    -- Syntax: EXEC p_System$new 'name', 'type', 'value'
    -- This procedure checks if the value already exists. If it exists an update
    -- is performed, otherwise the value is created.

    -- DOMAIN_NAME
    EXEC p_System$new N'DOMAIN_NAME', N'PATH', @c_domainName

    -- ABS_BASE_PATH
    EXEC p_System$new N'ABS_BASE_PATH', N'PATH', @c_absBasePath

    -- WWW_HOME_PATH
    EXEC p_System$new N'WWW_HOME_PATH', N'PATH', @c_homepagePath

    -- WWW_BASE_PATH
    EXEC p_System$new N'WWW_BASE_PATH', N'PATH', @c_uploadWwwPath
    
    -- INSTALL_DATE/LAST_UPDATED
    SELECT  @l_date = getDate ()
    EXEC p_System$get N'INSTALL_DATE', @l_value OUTPUT
    IF (@l_value IS NULL)
    BEGIN
        EXEC p_System$new N'INSTALL_DATE', N'DATE', @l_date
    END -- if
    ELSE
    BEGIN
        EXEC p_System$new N'LAST_UPDATED', N'DATE', @l_date
    END -- else

    -- INSTALL CHECKOUT TIMELIMIT
    EXEC p_System$new N'CO_TIMEOUT', N'INTEGER', N'10'
    
    -- CUSTOMER_NAME
    EXEC p_System$new N'CUSTOMER_NAME', N'STRING', @c_customerName

    -- SYSTEM_NAME
    EXEC p_System$new N'SYSTEM_NAME', N'STRING', @c_systemName

    -- BUILD_NUMBER
    EXEC p_System$new N'BUILD_NUMBER', N'STRING', @c_BUILD_NO

    -- VERSION_NAME
    EXEC p_System$new N'VERSION_NAME', N'NAME', @c_VERSION_NAME

    -- LANGUAGE
    EXEC p_System$new N'LANGUAGE', N'STRING', @c_LANGUAGE

-- show count messages again:
SET NOCOUNT OFF
GO
