/**
 * class DBFactoryServletMessages.java
 */

// package:
package ibs.io.servlet;

// imports:


/******************************************************************************
 * This interface contains all messages that are needed for DBFactoryServlet.
 *
 * @version     $Id: DBFactoryServletMessages.java,v 1.5 2007/07/24 21:29:09 kreimueller Exp $
 *
 * @author      CHINNI RANJITH KUMAR
 ******************************************************************************
 */
public class DBFactoryServletMessages extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DBFactoryServletMessages.java,v 1.5 2007/07/24 21:29:09 kreimueller Exp $";


    /**
     * Message sent when any error occurs.
     */
    public static final String MSG_ERROR = "ERROR";
    /**
     * Message sent when client has no permission to access the
     * Servlet.
     */
    public static final String MSG_ACCESS_DENIED = "Access Denied";
    /**
     * Message sent when transaction is success.
     */
    public static final String MSG_OK = "OK";
    /**
     * Message for malformed query string
     */
    public static final String MSG_MALFORMED = "Malformed query string";
    /**
     * Message for No DBFactory Config file names found in Servlet config file.
     */
    public static final String MSG_NO_FILES = "No DBFactory config file names found in DBFactoryServlet config file";

} // DBFactoryServletMessages
