/*
 * Class: Font.java
 */

// package:
package ibs.tech.html;

// imports:
//KR TODO: unsauber
import ibs.io.Environment;
import ibs.tech.html.BuildException;
import ibs.tech.html.Element;
import ibs.tech.html.IE302;


/******************************************************************************
 * This is the Font Object which only specifies a font
 *
 * @version     $Id: Font.java,v 1.8 2007/07/23 08:17:32 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980316
 ******************************************************************************
 */
public class Font extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Font.java,v 1.8 2007/07/23 08:17:32 kreimueller Exp $";


    /**
     * Specifies the element which is in the link.
     * default : none (null)
     */
    public String fontname;

    /**
     * Specifies the target of the link
     * default : none (null)
     */
    public String color;

    /**
     * Size of the font
     * default : none (-1)
     */
    public int size;

    /**
     * bold font
     * default : false
     */
    public boolean bold;

    /**
     * underlined font
     * default : false
     */
    public boolean underline;

    /**
     * cursive font
     * default : false
     */
    public boolean italic;


    /**************************************************************************
     * Create a new instance of a Font
     * Sets the default-values for all variables.
     * See the variables to know their default-values.
     */
    public Font ()
    {
        this.fontname = null;
        this.color = null;
        this.size = -1;
        this.bold = false;
        this.italic = false;
        this.underline = false;
    } //Font


    /**************************************************************************
     * Create a new instance of a Font with the given Font
     * Sets the default-values for all variables but the given fontname.
     * See the variables to know their default-values.
     *
     * @param   name    Name of the Font
     */
    public Font (String name)
    {
        this.fontname = name;
        this.color = null;
        this.size = -1;
        this.bold = false;
        this.italic = false;
        this.underline = false;
    } // Font


    /**************************************************************************
     * Create a new instance of a Font with the given font and size.
     * Sets the default-values for all variables but the name and the size.
     * See the variables to know their default-values.
     *
     * @param   name    Name of the Font
     * @param   pSize   Size of the Font
     */
    public Font (String name, int pSize)
    {
        this.fontname = name;
        this.color = null;
        this.size = pSize;
        this.bold = false;
        this.italic = false;
        this.underline = false;
    } // Font


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
            buf.append (IE302.TAG_FONTBEGIN);
            if (this.name != null)
            {
                buf.append (IE302.TA_NAME + this.inBrackets (this.name));
            } // if
            if (this.id != null)
            {
                buf.append (IE302.TA_ID + this.inBrackets (this.id));
            } // if
            if (this.title != null)
            {
                buf.append (IE302.TA_TITLE + this.inBrackets (this.title));
            } // if
            if (this.fontname != null)
            {
                buf.append (IE302.TA_FACE + this.inBrackets (this.fontname));
            } // if
            if (this.color != null)
            {
                buf.append (IE302.TA_COLOR + this.inBrackets (this.color));
            } // if
            if (this.size != -1)
            {
                buf.append (IE302.TA_SIZE + this.inBrackets ("" + this.size));
            } // if
            if (this.classId != null)
            {
                buf.append (IE302.TA_CLASSID + this.inBrackets (this.classId));
            } // if
            buf.append (IE302.TO_TAGEND);
        } // if browser is supported
    } // build

} // class Font
