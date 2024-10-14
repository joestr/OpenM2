/******************************************************************************
 * All data for m2 - installation wich had to be set from installer. <BR>
 *
 * @version     $Id: installConfig.sql,v 1.16 2005/08/11 16:12:37 klreimue Exp $
 *
 * @author      Andreas Jansa (AJ)  000121
 ******************************************************************************
 */

-- declare constants:
DECLARE
--########## begin of changeable code ... #####################################
--
--  DomainName
--  The Name of the domain to be installed.
--  Example:   DECLARE c_domainName CONSTANT VARCHAR2 (63) := 'ibs'
--
    c_domainName CONSTANT VARCHAR2 (63) := '@DOMAINNAME@';

-- 
-- absolute m2-basepath
-- absolute path of m2-webdirectory (under NT, use double '\\' for one '\' in path)
-- = directory wich is used for uploading
-- Example: c_absBasePath CONSTANT VARCHAR2 (255) := '/opt/apache/htdocs/';
--
    c_absBasePath CONSTANT VARCHAR2 (255) := '@ABSBASEPATH@';

--
-- m2 - HomepagePath of current domain to be installed
-- part of URL wich is used after webroot of domain - this means if you use 
-- 'http://www.netbiz.to/m2/' as full - m2 root for domain, set c_homepagePath to '/m2/' 
-- Example: c_homepagePath CONSTANT VARCHAR2 (255) := '/m2/';
--
    c_homepagePath CONSTANT VARCHAR2 (255) := '@HOMEPAGEPATH@';


--
-- m2 - fileuploadbasepath
-- part of URL to directory after WebRoot-Address wich is used for fileupload - this means if you use 
-- 'http://www.netbiz.to/m2/' as full - m2 uploadWwwPath, set c_uploadWwwPath to '/m2/' 
-- Example: c_uploadWwwPath CONSTANT VARCHAR2 (255) := '/m2/';
--
    c_uploadWwwPath CONSTANT VARCHAR2 (255) := '@UPLOADWWWPATH@';
    

--
-- customer name
-- unique identifier for m2 customer
--
    c_customerName CONSTANT VARCHAR2 (255) := '@CUSTOMERNAME@';

--
-- system name
-- unique identifier for m2 installation
--
    c_systemName CONSTANT VARCHAR2 (255) := '@SYSTEMNAME@';

--
--  Number of Build
--  The current buildnumber, build like version.postversion.incremental-number.
--  The incremental-number starts with 300 on 1.1.1999
--  Example:   c_BUILD_NO CONSTANT VARCHAR2 (18) := '2.23.1443';
--
    c_BUILD_NO CONSTANT VARCHAR2 (18) := '#BUILD_NUMBER#';

--
--  Name of Version
--  The Name of the current version.
--  Example:   c_VERSION_NAME CONSTANT VARCHAR2 (63) := '2.3 Beta';
--
    c_VERSION_NAME CONSTANT VARCHAR2 (63) := '#VERSION_NAME#';

    --
--  Language
--  The Language of the current version.
--  Example:   c_LANGUAGE CONSTANT VARCHAR2 (63) := 'ENGLISH';
--
    c_LANGUAGE CONSTANT VARCHAR2 (63) := '@LANGUAGE@';


--########## ... end of changeable code #######################################

    l_date                  DATE;           -- the actual date and time
    l_value                 VARCHAR2 (255); -- the actual value
    
BEGIN

    -- set the system values:
    -- Syntax: p_System$new ('name', 'type', 'value');
    -- This procedure checks if the value already exists. If it exists an update
    -- is performed, otherwise the value is created.

    -- DOMAIN_NAME
    p_System$new ('DOMAIN_NAME', 'PATH', c_domainName);

    -- ABS_BASE_PATH
    p_System$new ('ABS_BASE_PATH', 'PATH', c_absBasePath);

    -- WWW_HOME_PATH
    p_System$new ('WWW_HOME_PATH', 'PATH', c_homepagePath);

    -- WWW_BASE_PATH
    p_System$new ('WWW_BASE_PATH', 'PATH', c_uploadWwwPath);
    
    -- INSTALL_DATE/LAST_UPDATED
    SELECT  SYSDATE
    INTO    l_date
    FROM    SYS.DUAL;

    BEGIN
        p_System$get ('INSTALL_DATE', l_value);
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            l_value := NULL;
        WHEN OTHERS THEN
            RAISE; 
    END;

    
    IF (l_value IS NULL)
    THEN
        p_System$new ('INSTALL_DATE', 'DATE', l_date);
    ELSE
        p_System$new ('LAST_UPDATED', 'DATE', l_date);
    END IF;

    -- INSTALL CHECKOUT TIMELIMIT
    p_System$new ('CO_TIMEOUT', 'INTEGER', '10');
    
    -- CUSTOMER_NAME
    p_System$new ('CUSTOMER_NAME', 'STRING', c_customerName);

    -- SYSTEM_NAME
    p_System$new ('SYSTEM_NAME', 'STRING', c_systemName);

    -- BUILD_NUMBER
    p_System$new ('BUILD_NUMBER', 'STRING', c_BUILD_NO);

    -- VERSION_NAME
    p_System$new ('VERSION_NAME', 'NAME', c_VERSION_NAME);

    -- LANGUAGE
    p_System$new ('LANGUAGE', 'STRING', c_LANGUAGE);
    
END;
/

EXIT;
