/*
 * Class: TextAreaElement.java
 */

// package:
package ibs.tech.html;

// imports:
//KR TODO: unsauber
import ibs.io.Environment;
import ibs.tech.html.BuildException;
import ibs.tech.html.Element;
import ibs.tech.html.IE302;
import ibs.util.StringHelpers;


/******************************************************************************
 * This is the TextAreaElement Object, which builds a HTML-String
 * needed for a TextArea to be displayed from the browser.
 *
 * @version     $Id: TextAreaElement.java,v 1.9 2007/07/24 21:27:02 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980316
 ******************************************************************************
 */
public class TextAreaElement extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: TextAreaElement.java,v 1.9 2007/07/24 21:27:02 kreimueller Exp $";


    /**
     * Specifies the default value
     * default : none (null)
     */
    public String value;

    /**
     * Height of the text area
     * default : none (null)
     */
    public int cols;

    /**
     * width of the text area.
     * default : 1
     */
    public int rows;

    /**
     * Only for text and password and textarea,
     * default : no maximal length
     */
    public int maxlength;

    /**
     * wraptype
     * default : no wrapping
     */
    public String wrap;


    /**************************************************************************
     * Create a new instance of a TextAreaElement of a given type.
     * Sets parameters to default values except the name and the type.
     * See the variables to know their default-values.
     *
     * @param   pName   ?????
     */
    public TextAreaElement (String pName)
    {
        this.classId = null;
        this.id = null;
        this.name = pName;
        this.value = null;
        this.maxlength = -1;
        this.cols = -1;
        this.rows = -1;
        this.wrap = null;
    } // TextAreaElement


    /**************************************************************************
     * Create a new instance of a TextAreaElement with a given type with a
     * given value.
     * Sets parameters to default values except the name, the type and the value.
     * See the variables to know their default-values.
     *
     * @param   pName   ?????
     * @param   pValue  ?????
     */
    public TextAreaElement (String pName, String pValue)
    {
        this.classId = null;
        this.id = null;
        this.name = pName;
        this.value = pValue;
        this.maxlength = -1;
        this.cols = -1;
        this.rows = -1;
        this.wrap = null;
    } // TextAreaElement


    /**************************************************************************
     * Writes the element on the browser. <BR/>
     *
     * @param   env     OutputStream
     * @param   buf     Buffer in which to write the result.
     *
     * @exception   BuildException
     *              An error occurred during building the output.
     */
    public void build (Environment env, StringBuffer buf) throws BuildException
    {
        if (this.isBrowserSupported (env))
                                        // browser is supported?
        {
            buf.append (IE302.TAG_TEXTAREABEGIN);
            if (this.name != null)
            {
                buf.append (IE302.TA_NAME + this.inBrackets (this.name));
            } // if
            if (this.id != null)
            {
                buf.append (IE302.TA_ID + this.inBrackets (this.id));
            } // if
            if (this.classId != null)
            {
                buf.append (IE302.TA_CLASSID + this.inBrackets (this.classId));
            } // if
            if (this.title != null)
            {
                buf.append (IE302.TA_TITLE + this.inBrackets (this.title));
            } // if
            if (this.maxlength != -1)
            {
                buf.append (IE302.TA_MAXLENGTH + this.inBrackets ("" + this.maxlength));
            } // if
            if (this.wrap != null)
            {
                buf.append (IE302.TA_WRAP + this.inBrackets (this.wrap));
            } // if
            if (this.cols != -1)
            {
                buf.append (IE302.TA_COLS + this.inBrackets ("" + this.cols));
            } // if
            if (this.rows != -1)
            {
                buf.append (IE302.TA_ROWS + this.inBrackets ("" + this.rows));
            } // if
            buf.append (IE302.TO_TAGEND);
            if (this.value != null)
            {
                // create a new string in which the html specific characters
                // are replaced with named characters:
                StringBuffer newValue = new StringBuffer (this.value);
                newValue = StringHelpers.replace (newValue, "&", "&amp;");
                newValue = StringHelpers.replace (newValue, "<", "&lt;");
                newValue = StringHelpers.replace (newValue, ">", "&gt;");
                newValue = StringHelpers.replace (newValue, "\"", "&quot;");
                // append the computed value string as content of the text area:
                buf.append (newValue);
            } // if
            buf.append (IE302.TAG_TEXTAREAEND);
        } // if
    } // build

} // class TextAreaElement
