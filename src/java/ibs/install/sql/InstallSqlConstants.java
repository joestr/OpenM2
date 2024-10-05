/*
 * Class: InstallSqlConstants.java
 */

// package:
package ibs.install.sql;

// imports:


/******************************************************************************
 * Constants for sql installation. <BR/>
 * This abstract class contains all constants which are necessary to deal with
 * the sql installation functionalities delivered within this package.
 *
 * @version     $Id: InstallSqlConstants.java,v 1.1 2007/07/31 19:14:37 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 20070729
 ******************************************************************************
 */
public abstract class InstallSqlConstants extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: InstallSqlConstants.java,v 1.1 2007/07/31 19:14:37 kreimueller Exp $";


    // states:
    /**
     * Parser state: start and standard state. <BR/>
     */
    protected static final int ST_START = 0;
    /**
     * Parser state: within quotes (string). <BR/>
     */
    protected static final int ST_QUOTE = 1;
    /**
     * Parser state: after semicolon. <BR/>
     */
    protected static final int ST_SEMICOLON = 2;
    /**
     * Parser state: start and standard state. <BR/>
     */
    protected static final int ST_SPACE = 3;
    /**
     * Parser state: after BEGIN token. <BR/>
     */
    protected static final int ST_BEGIN = 4;
    /**
     * Parser state: after END token. <BR/>
     */
    protected static final int ST_END = 5;

    // special constants:
    /**
     * Start character of line comment. <BR/>
     */
    protected static final char LCOMMSTART = '-';    // start character of line comment
} // class InstallSqlConstants
