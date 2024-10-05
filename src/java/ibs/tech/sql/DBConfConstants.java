/*
 * Class: DBConfConstants.java
 */

// package:
package ibs.tech.sql;

// imports:


/******************************************************************************
 * Constants for configuration utilities. <BR/>
 * This abstract class contains all constants which are necessary to deal with
 * the configuration.
 *
 * @version     $Id: DBConfConstants.java,v 1.8 2007/07/10 18:23:00 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 001201
 ******************************************************************************
 */
public abstract class DBConfConstants extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DBConfConstants.java,v 1.8 2007/07/10 18:23:00 kreimueller Exp $";


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
     * Log directory token in system configuration file. <BR/>
     */
    public static final String TOK_LOGDIR               = "logdir";

} // class DBConfConstants
