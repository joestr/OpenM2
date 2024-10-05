/*
 * Class: HttpConstants.java
 */

// package:
package ibs.tech.http;

// imports:


/******************************************************************************
 * Constants for Http interaction in the http-Components. <BR/>
 *
 * @version     $Id: HttpConstants.java,v 1.8 2007/07/31 19:13:59 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 981008
 ******************************************************************************
 */
public abstract class HttpConstants extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: HttpConstants.java,v 1.8 2007/07/31 19:13:59 kreimueller Exp $";


    /**
     * Type of form data: standard data coming from POST request. <BR/>
     */
    public static final int T_POST = 1;

    /**
     * Type of form data: file sent via POST request. <BR/>
     */
    public static final int T_FILE = 2;

    /**
     * Type of form data: multiple fields of standard data coming from POST
     * request. <BR/>
     */
    public static final int T_POSTMULTIPLE = 3;


    /**
     * temporary Path in which to store uploaded files. <BR/>
     */
    public static String PATH_UPLOAD_TEMP = null;

    /**
     * The name of http-header for define the name of file to show. <BR/>
     */
    public static String HTTP_HEADER_FILENAME = "Content-disposition";

    /**
     * The name of http-header for define the control browser caching. <BR/>
     * To use this tag, the value must be "no-cache". <BR/>
     * When this is included in a document, this prevents Netscape Navigator
     * from caching a page locally. <BR/>
     * @see #HTTP_HEADER_FILE_ON_PROXY_VALUE no-cache
     */
    public static String HTTP_HEADER_FILE_ON_PROXY = "Pragma";

    /**
     * The value of http-header pragma. <BR/>
     * @see #HTTP_HEADER_FILE_ON_PROXY http-header pragma
     */
    public static String HTTP_HEADER_FILE_ON_PROXY_VALUE = "no-cache";


    /**
     * The name of http-header for define when the file should be loaded from
     * the origin adress. <BR/>
     * Posible values can be 0 or a date time in following format:<BR>. <BR/>
     * Sat, 15 Dec 2001 12:00:00 GMT
     */
    public static String HTTP_HEADER_FILE_FROM_ORIGIN_DATETIME = "Expires";

} // class HttpConstants
