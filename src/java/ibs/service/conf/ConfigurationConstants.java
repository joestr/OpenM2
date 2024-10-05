/*
 * Class: ConfigurationConstants.java
 */

// package:
package ibs.service.conf;

import ibs.io.IOConstants;

// imports:


/******************************************************************************
 * Constants for configuration utilities. <BR/>
 * This abstract class contains all constants which are necessary to deal with
 * the configuration.
 *
 * @version     $Id: ConfigurationConstants.java,v 1.15 2012/09/18 14:47:50 btatzmann Exp $
 *
 * @author      Klaus Reimüller (KR), 001201
 ******************************************************************************
 */
public abstract class ConfigurationConstants extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ConfigurationConstants.java,v 1.15 2012/09/18 14:47:50 btatzmann Exp $";


    // configuration files:
    /**
     * Name of ibssystem configuration file. <BR/>
     */
    public static final String FILE_IBSSYSTEM = "ibssystem.xml";

    /**
     * Name of feature configuration directory. <BR/>
     */
    public static final String DIR_FEATURES = "features";

    /**
     * Name of feature configuration file. <BR/>
     */
    public static final String FILE_FEATURE = "feature.xml";



    // default values:
    /**
     * Default port number for applicationserver
     */
    public static final int APPLICATIONSERVERPORT_DEFAULT = 80;

    /**
     * Default port number for p_ssl
     */
    public static final int SSLSERVERPORT_DEFAULT = 443;

    /**
     * Default port number for p_ssl
     */
    public static final boolean SSL_DEFAULT = false;

    /**
     * Default url for ssi files
     */
    public static final String SSIURL_DEFAULT =
        IOConstants.URL_HTTP + "localhost/m2/app/include/";

    /**
     * Default querytimeout for database connections
     */
    public static final int DBQUERYTIMEOUT_DEFAULT = 120;

    /**
     * Default logintimeout for database connections
     */
    public static final int DBLOGINTIMEOUT_DEFAULT = 45;


    // Tokens in configuration file:
    /**
     * . <BR/>
     */
    public static final String TOK_ROOT             = "m2Configuration";

    /**
     * . <BR/>
     */
    public static final String TOK_SERVER           = "server";

    /**
     * . <BR/>
     */
    public static final String TOK_NAME             = "name";

    /**
     * . <BR/>
     */
    public static final String TOK_PORT              = "port";

    /**
     * SSL server name token in system configuration file. <BR/>
     */
    public static final String TOK_SSLNAME            = "sslname";

    /**
     * SSL server port token in system configuration file. <BR/>
     */
    public static final String TOK_SSLPORT            = "sslport";

    /**
     * SSL enabled token in system configuration file. <BR/>
     */
    public static final String TOK_SSL                 = "ssl";

    /**
     * SSI URL token in system configuration file. <BR/>
     */
    public static final String TOK_SSIURL              = "ssiurl";

    /**
     * . <BR/>
     */
    public static final String TOK_VALUE                = "value";

    /**
     * SMTP server token in system configuration file. <BR/>
     */
    public static final String TOK_SMTPSERVER           = "smtpserver";

    /**
     * System's mail address token in system configuration file. <BR/>
     */
    public static final String TOK_MAILSYSTEM           = "mailsystem";

    /**
     * System administrator's mail address token in system configuration file. <BR/>
     */
    public static final String  TOK_MAILADMIN           = "mailadmin";

    /**
     * Mail account token in the system configuration file. <BR/>
     */
    public static final String TOK_ACCOUNT				= "account";
    
    /**
     * Driver class for the database connection. <BR/>
     */
    public static final String TOK_JDBCDRIVERCLASS      = "jdbcdriverclass";

    /**
     * connection string for the jdbc database connection. <BR/>
     */
    public static final String TOK_JDBCDCONNECTIONSTRING = "jdbcconnectionstring";

    /**
     * User name for database access token in system configuration file. <BR/>
     */
    public static final String TOK_USER                 = "user";

    /**
     * Password for access in system configuration file. <BR/>
     */
    public static final String TOK_PASSWORD             = "password";

    /**
     * Name of database token in system configuration file. <BR/>
     */
    public static final String TOK_DATABASE             = "database";

    /**
     * Name of database token in system configuration file. <BR/>
     */
    public static final String TOK_SID                  = "sid";

    /**
     * Timeout for database login token in system configuration file. <BR/>
     */
    public static final String TOK_DBLOGINTIMEOUT       = "logintimeout";

    /**
     * Timeout for database queries token in system configuration file. <BR/>
     */
    public static final String TOK_DBQUERYTIMEOUT       = "querytimeout";

    /**
     * Type of DBMS token in system configuration file. <BR/>
     */
    public static final String TOK_DBTYPE               = "type";

    /**
     * Regular size token in system configuration file. <BR/>
     */
    public static final String TOK_REGULARSIZE          = "regularsize";

    /**
     * Maximum size token in system configuration file. <BR/>
     */
    public static final String TOK_MAXSIZE              = "maxsize";

    /**
     * Number of actions per connection token in system configuration file. <BR/>
     */
    public static final String TOK_ACTIONSPERCONNECTION = "actionsperconnection";

    /**
     * Login wizard token in system configuration file. <BR/>
     */
    public static final String TOK_WIZARDLOGIN          = "wizardlogin";

    /**
     * Tracer active token in system configuration file. <BR/>
     */
    public static final String TOK_TRACESERVER    = "traceserver";

    /**
     * Tracer active token in system configuration file. <BR/>
     */
    public static final String TOK_ACTIVE         = "active";

    /**
     * Path for file tracer token in system configuration file. <BR/>
     */
    public static final String TOK_PATH           = "path";

    /**
     * URL for WebDav requests. <BR/>
     */
    public static final String TOK_WEBDAV         = "webdav";

    /**
     * WebDav Configuration. <BR/>
     */
    public static final String TOK_WEBDAVURL      = "webdavurl";

    /**
     * Absolute Path of WebDav enabled os folder. <BR/>
     */
    public static final String TOK_WEBDAVPATH     = "webdavpath";

    /**
     * Authentication token in system configuration file. <BR/>
     */
    public static final String TOK_AUTHENTICATION = "authentication";

    /**
     * ntlm for authentication token in system configuration file. <BR/>
     */
    public static final String TOK_NTLM           = "ntlm";

    /**
     * NT domains. <BR/>
     */
    public static final String TOK_NT_DOMAINS = "ntdomains";

} // class UtilConstants
