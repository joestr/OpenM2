/*
 * Class: LogConstants.java
 */

// package:
package ibs.obj.log;

// imports:


/******************************************************************************
 * Constants for log objects. <BR/>
 *
 * @version     $Id: LogConstants.java,v 1.2 2009/07/24 10:16:40 kreimueller Exp $
 *
 * @author      Klaus, 15.10.2003
 ******************************************************************************
 */
public abstract class LogConstants extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: LogConstants.java,v 1.2 2009/07/24 10:16:40 kreimueller Exp $";


    /**
     * LogContainer. <BR/>
     */
    public static final short LOG_ALL = 1;

    /**
     * LogContainer. <BR/>
     */
    public static final short LOG_CREATE = 2;

    /**
     * LogContainer. <BR/>
     */
    public static final short LOG_CHANGE = 3;

    /**
     * LogContainer. <BR/>
     */
    public static final short LOG_DELETE = 4;

    /**
     * LogContainer. <BR/>
     */
    public static final short LOG_FORWARD = 5;

} // class LogConstants
