/*
 * Class: AppArguments.java
 */

// package:
package ibs.tech.http;

// imports:
import ibs.io.IOHelpers;


/******************************************************************************
 * Arguments for ibs applications. <BR/>
 * This abstract class contains all Arguments which are necessary to deal with
 * the classes delivered within this package. <P>
 *
 * @version     $Id: HttpArguments.java,v 1.27 2007/07/10 19:25:59 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 980429
 ******************************************************************************
 */
public abstract class HttpArguments extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: HttpArguments.java,v 1.27 2007/07/10 19:25:59 kreimueller Exp $";


    // argument handling for url:
    /**
     * Begin of argument list in URL. <BR/>
     */
    public static final String ARG_BEGIN = "?";

    /**
     * Argument separator in URL.< BR>
     */
    public static final String ARG_SEP = "&";

    /**
     * Argument assignment in URL.< BR>
     */
    public static final String ARG_ASSIGN = "=";


    /**************************************************************************
     * Create an argument for adding to an url. <BR/>
     * This method creates a string which can be concatenated to an existing
     * url. <BR/>
     * format: <CODE>&amp;&lt;name&gt;=&lt;value&gt;</CODE>. <BR/>
     * The value is url encoded.
     *
     * @param   name        The name of the argument.
     * @param   value       The value of the argument.
     *
     * @return  The constructed argument string.
     */
    public static String createArg (String name, String value)
    {
        // construct the string and return the result:
        return HttpArguments.ARG_SEP + name + HttpArguments.ARG_ASSIGN +
            IOHelpers.urlEncode (value);
    } // createArg


    /**************************************************************************
     * Create an argument for adding to an url. <BR/>
     * This method creates a string which can be concatenated to an existing
     * url. <BR/>
     * format: <CODE>&amp;&lt;name&gt;=&lt;value&gt;</CODE>. <BR/>
     * There is no encoding done for the value.
     *
     * @param   name        The name of the argument.
     * @param   value       The value of the argument.
     *
     * @return  The constructed argument string.
     */
    public static String createArgNoEncode (String name, String value)
    {
        // construct the string and return the result:
        return HttpArguments.ARG_SEP + name + HttpArguments.ARG_ASSIGN + value;
    } // createArgNoEncode


    /**************************************************************************
     * Create an argument for adding to an url. <BR/>
     * This method creates a string which can be concatenated to an existing
     * url. <BR/>
     * format: <CODE>&amp;&lt;name&gt;=&lt;value&gt;</CODE>.
     *
     * @param   name        The name of the argument.
     * @param   value       The value of the argument.
     *
     * @return  The constructed argument string.
     */
    public static String createArg (String name, int value)
    {
        // construct the string and return the result:
        return HttpArguments.ARG_SEP + name + HttpArguments.ARG_ASSIGN + value;
    } // createArg


    /**************************************************************************
     * Create an argument for adding to an url. <BR/>
     * This method creates a string which can be concatenated to an existing
     * url. <BR/>
     * format: <CODE>&amp;&lt;name&gt;=&lt;value&gt;</CODE>.
     *
     * @param   name        The name of the argument.
     * @param   value       The value of the argument.
     *
     * @return  The constructed argument string.
     */
    public static String createArg (String name, boolean value)
    {
        // construct the string and return the result:
        return HttpArguments.ARG_SEP + name + HttpArguments.ARG_ASSIGN + value;
    } // createArg

} // class HttpArguments
