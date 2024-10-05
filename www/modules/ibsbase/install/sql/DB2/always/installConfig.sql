--------------------------------------------------------------------------------
-- All data for m2 - installation wich had to be set from installer. <BR>
--
-- @version     $Revision: 1.7 $, $Date: 2005/08/11 16:12:36 $
--              $Author: klreimue $
--
-- @author      Marcel Samek (MS)  020921
----------------------------------------------------------------------------------
    -- don't show count messages:

    -- begin of changeable code ... 
    --
    --  DomainName
    --  The Name of the domain to be installed.
    --  Example:   SELECT  @c_domainName = 'ibs'
    --
-- delete existing procedure
CALL IBSDEV1.p_dropProc ('pi_installConfig');
-- create new procedure
CREATE PROCEDURE IBSDEV1.pi_installConfig ()
DYNAMIC RESULT SETS 1 
LANGUAGE SQL 
BEGIN 
    DECLARE SQLCODE INT;
    DECLARE c_domainName VARCHAR (63);
    -- 
    -- absolute m2-basepath
    -- absolute path of m2-webdirectory 
    -- (under NT, use double '\\' for one '\' in path)
    -- = directory wich is used for uploading
    -- Example: SELECT  @c_absBasePath = 'c:\\InetPub\\wwwroot\\m2\\'
    --
    DECLARE c_absBasePath VARCHAR (255);
    
    --
    -- m2 - HomepagePath of current domain to be installed
    -- part of URL wich is used after webroot of domain - this means if you use 
    -- 'http://www.netbiz.to/m2/' as full - 
    -- m2 root for domain, set c_homepagePath to '/m2/' 
    -- Example: c_homepagePath CONSTANT VARCHAR (255) := '/m2/';
    --
    DECLARE c_homepagePath VARCHAR (255);
    
    --
    -- m2 - fileuploadbasepath
    -- part of URL to directory after WebRoot-Address 
    -- wich is used for fileupload - this means if you use 
    -- 'http://www.netbiz.to/m2/' as full 
    -- - m2 uploadWwwPath, set c_uploadWwwPath to '/m2/' 
    -- Example: c_uploadWwwPath CONSTANT VARCHAR (255) := '/m2/';
    --
    DECLARE c_uploadWwwPath VARCHAR (255);
    
    --
    -- customer name
    -- unique identifier for m2 customer
    --
    DECLARE c_customerName VARCHAR (255);
    
    --
    -- system name
    -- unique identifier for m2 installation
    --
    DECLARE c_systemName VARCHAR (255);
    
    --
    --  Number of Build
    --  The current buildnumber, 
    -- build like version.postversion.incremental-number.
    --  The incremental-number starts with 300 on 1.1.1999
    --  Example:   SELECT  @c_BUILD_NO = '2.23.1443'
    --
    DECLARE c_BUILD_NO VARCHAR (18);
    
    --
    --  Name of Version
    --  The Name of the current version.
    --  Example:   SELECT  @c_VERSION_NAME = '2.3 Beta'
    --
    DECLARE c_VERSION_NAME VARCHAR (63);
    
    --  Language
    --  The Language of the current version
    --
    --  Example:   SELECT  @c_LANGUAGE = 'ENGLISH'
    --
    DECLARE c_LANGUAGE VARCHAR (18);
    DECLARE l_date          TIMESTAMP;      -- the actual date and time
    DECLARE l_value         VARCHAR (255);
    DECLARE l_sqlcode       INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR SQLWARNING
    	SET l_sqlcode = SQLCODE;
    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET l_sqlcode = SQLCODE;




--########## ... begin of changeable code #####################

    SET c_domainName = '@DOMAINNAME@';
    SET c_absBasePath = '@ABSBASEPATH@';
    SET c_homepagePath = '@HOMEPAGEPATH@';
    SET c_uploadWwwPath = '@UPLOADWWWPATH@';
    SET c_customerName = '@CUSTOMERNAME@';
    SET c_systemName = '@SYSTEMNAME@';
    SET c_BUILD_NO = '#BUILD_NUMBER#';
    SET c_VERSION_NAME = '#VERSION_NAME#';
    SET c_LANGUAGE = '@LANGUAGE@';

/*
    SET c_domainName = 'schwancosmetics';
    SET c_absBasePath = 'c:\\inetpub\\wwwroot\\schwancosmetics\\';
    SET c_homepagePath = '/schwancosmetics/';
    SET c_uploadWwwPath = '/schwancosmetics/';
    SET c_customerName = 'SSC';
    SET c_systemName = 'SSC.ibs2';
    SET c_BUILD_NO = '1796';
    SET c_VERSION_NAME = 'm2 2.3 DB2';
    SET c_LANGUAGE = 'german';
*/

/*
    SET c_domainName = 'IBSDEV1.dom3';
    SET c_absBasePath = 'c:\\Programme\\m2\\www\\';
    SET c_homepagePath = '/m2/';
    SET c_uploadWwwPath = '/m2/';
    SET c_customerName = 'tectum';
    SET c_systemName = 'tectumas4.tectumdev2';
    SET c_BUILD_NO = '1796';
    SET c_VERSION_NAME = 'm2 2.3 DB2';
    SET c_LANGUAGE = 'german';
*/

/*
    SET c_domainName = 'IBSDEV1.dom6';
    SET c_absBasePath = 'c:\\inetpub\\wwwroot\\m2test\\';
    SET c_homepagePath = '/m2test/';
    SET c_uploadWwwPath = '/m2test/';
    SET c_customerName = 'tectum';
    SET c_systemName = 'tectumas4.tectumdev1';
    SET c_BUILD_NO = '1796';
    SET c_VERSION_NAME = 'm2 2.3 DB2';
    SET c_LANGUAGE = 'german';
*/

--########## ... end of changeable code #####################




    -- DOMAIN_NAME
    CALL IBSDEV1.p_System$new('DOMAIN_NAME','PATH', c_domainName);
  
    -- ABS_BASE_PATH
    CALL IBSDEV1.p_System$new('ABS_BASE_PATH', 'PATH', c_absBasePath);
  
    -- WWW_HOME_PATH
    CALL IBSDEV1.p_System$new('WWW_HOME_PATH', 'PATH', c_homepagePath);
  
    -- WWW_BASE_PATH
    CALL IBSDEV1.p_System$new('WWW_BASE_PATH', 'PATH', c_uploadWwwPath);
  
    -- INSTALL_DATE/LAST_UPDATED
        SET l_date = CURRENT TIMESTAMP;
    CALL IBSDEV1.p_System$get('INSTALL_DATE', l_value);
    IF l_value IS NULL THEN 
        CALL IBSDEV1.p_System$new('INSTALL_DATE', 'DATE', l_date);
    ELSE 
        CALL IBSDEV1.p_System$new('LAST_UPDATED', 'DATE', l_date);
    END IF;
  
    -- else
    -- INSTALL CHECKOUT TIMELIMIT
    CALL IBSDEV1.p_System$new('CO_TIMEOUT', 'INTEGER', '10');
  
    -- CUSTOMER_NAME
    CALL IBSDEV1.p_System$new('CUSTOMER_NAME', 'STRING', c_customerName);
  
    -- SYSTEM_NAME
    CALL IBSDEV1.p_System$new('SYSTEM_NAME', 'STRING', c_systemName);
  
    -- BUILD_NUMBER
    CALL IBSDEV1.p_System$new('BUILD_NUMBER', 'STRING', c_BUILD_NO);
  
    -- VERSION_NAME
    CALL IBSDEV1.p_System$new('VERSION_NAME', 'NAME', c_VERSION_NAME);
  
    -- LANGUAGE
    CALL IBSDEV1.p_System$new('LANGUAGE', 'STRING', c_LANGUAGE);
  
    -- show count messages again:
END;

-- execute procedure:
CALL IBSDEV1.pi_installConfig;
-- delete procedure:
CALL IBSDEV1.p_dropProc ('pi_installConfig');
