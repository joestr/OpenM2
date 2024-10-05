/*
 * Class: Element.java
 */

// package:
package ibs.tech.html;

// imports:
import ibs.BaseObject;
//KR TODO: unsauber
import ibs.io.Environment;
//KR TODO: unsauber
import ibs.io.IOConstants;
import ibs.tech.html.BuildException;


/******************************************************************************
 * This is the abstract Element Object, which is the Superclass
 * of all html-Elements
 *
 * @version     $Id: Element.java,v 1.11 2007/07/20 12:59:26 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980314
 *****************************************************************************/
public abstract class Element extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Element.java,v 1.11 2007/07/20 12:59:26 kreimueller Exp $";


    /**
     * Reference for style-sheets
     */
    public String classId = null;

    /**
     * title of the Element
     */
    public String title = null;

    /**
     * Reference for style-sheets
     */
    public String id = null;

    /**
     * Name of the element
     */
    public String name = null;

    /**
     * Name of the browser
     */
    public String browser = null;


    /**************************************************************************
     * Returns the given String in brackets.
     *
     * @param   s       String to put in brackets.
     *
     * @return  String in brackets.
     */
    protected String inBrackets (String s)
    {
        return "\"" + s + "\"";
    } // inBrackets


    /**************************************************************************
     * Check if the browser is supported by the current implementation. <BR/>
     *
     * @param   env     The environment from which to get the current browser.
     *
     * @return  <CODE>true</CODE> if the browser is supported,
     *          else raises an Exception.
     *
     * @throws  BuildException
     *          The browser is not supported.
     */
    protected boolean isBrowserSupported (Environment env) throws BuildException
    {
        String browser = env.getBrowser (); // the actual browser
        if (browser.equalsIgnoreCase (IOConstants.ALL_BROWSERS) ||
            browser.equalsIgnoreCase (IOConstants.MSIE4) ||
            browser.equalsIgnoreCase (IOConstants.NS4) ||
            browser.equalsIgnoreCase (IOConstants.NS6))
                                        // browser is supported?
        {
            return true;                // return that the browser is supported
        } // if browser is supported

        throw new BuildException (this.getClass () + " : " +
            IOConstants.BROWSERNOTSUPPORTED);
    } // isBrowserSupported


    /**************************************************************************
     * Writes the element on the browser. <BR/>
     *
     * @param   env     OutputStream
     * @param   buf     (currently not used)
     *
     * @exception   BuildException
     *              An error occurred during building the output.
     */
    public void build (Environment env, StringBuffer buf) throws BuildException
    {
        this.browser = env.getBrowser ();
//        build (env, buf, env.getBrowser ());
    } // build

} // class Element
