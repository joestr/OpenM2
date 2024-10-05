/*
 * Class: StyleSheetElement.java
 */

// package:
package ibs.tech.html;

// imports:
//KR TODO: unsauber
import ibs.io.Environment;
import ibs.tech.html.BuildException;
import ibs.tech.html.Element;
import ibs.tech.html.IE302;

import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * This is the StyleSheetElement Object, which builds a HTML-String
 * needed for a StyleSheet.
 *
 * @version     $Id: StyleSheetElement.java,v 1.9 2007/07/23 08:17:32 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980316
 ******************************************************************************
 */
public class StyleSheetElement extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: StyleSheetElement.java,v 1.9 2007/07/23 08:17:32 kreimueller Exp $";


    /**
     * Constant initialElements of the Vector holding the
     * elements.
     * set to 20
     */
    protected static final int ELEMENTS_INITIAL = 20;

    /**
     * Constant incrementElements of the Vector holding the
     * elements.
     * set to 5
     */
    protected static final int ELEMENTS_INCREMENT = 5;

    /**
     * file to import as StyleSheet
     */
    public String importSS;

    /**
     * The styleSheets
     */
    protected Vector<String[]> styleSheets;


    /**************************************************************************
     * Creates a new instance of a StyleSheetElement
     */
    public StyleSheetElement ()
    {
        this.styleSheets = null;
        this.importSS = null;
    } // ScriptElement


    /**************************************************************************
     * Adds a statement
     *
     * @param tag               ......tag where the style is used
     * @param style             ......styles to be used
     */
    public void addStyle (String tag, String style)
    {
        if (this.styleSheets == null)
        {
            this.styleSheets = new Vector<String[]> (
                StyleSheetElement.ELEMENTS_INITIAL,
                StyleSheetElement.ELEMENTS_INCREMENT);
        } // if
        String[] ss = new String[2];
        ss[0] = tag;
        ss[1] = style;
        this.styleSheets.addElement (ss);
    } // addStyle


    /**************************************************************************
     * Clears the Script
     */
    public void clear ()
    {
        this.styleSheets = null;
        this.importSS = null;
    } // clear


    /**************************************************************************
     * Writes the element on the browser. <BR/>
     *
     * @param   env     OutputStream
     * @param   buf     Buffer where to write the output to.
     *
     * @exception   BuildException
     *              An error occurred during building the output.
     */
    public void build (Environment env, StringBuffer buf) throws BuildException
    {
        if (this.isBrowserSupported (env)) // browser is supported?
        {
            if (this.importSS != null)  // import style sheet file?
            {
                buf.append (IE302.CSS_IMPORTBEGIN + this.importSS + IE302.CSS_IMPORTEND);
//                buf.append ("<LINK REL=STYLESHEET TYPE=\"text/css\" HREF=\"" + importSS + "\">");
            } // if import style sheet file

            if (this.styleSheets != null)    // there are some elements in the
                                        // vector?
            {
                buf.append (IE302.TAG_STYLEBEGIN);
                buf.append (IE302.TO_TAGEND);

                for (Iterator<String[]> iter = this.styleSheets.iterator (); iter.hasNext ();)
                {
                    String[] css = iter.next ();
                    if (css != null)
                    {
                        buf.append (css[0] + IE302.CSS_BEGIN);
                        buf.append (css[1] + IE302.CSS_END);
                    } // if
                } // for iter
                buf.append (IE302.TAG_STYLEEND + "\n");
            } // if there are some elements in the vector
        } //if if (isBrowserSupported (env))
    } // build

} // class StyleSheetElement
