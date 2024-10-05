/*
 * Class: ImageElement.java
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
 * This is the abstract ImageElement Object, which builds a HTML-String
 * needed for a Image to be displayed on the browser.
 *
 * @version     $Id: ImageElement.java,v 1.9 2007/07/23 08:17:32 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980316
 ******************************************************************************
 */
public class ImageElement extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ImageElement.java,v 1.9 2007/07/23 08:17:32 kreimueller Exp $";


    /**
     * Specifies the source where the image lies (URL)
     */
    public String source;

    /**
     * Along with <A HREF="#width">width</A> specifies the size at which
     * the picture is drawn. <BR/>
     * If the picture's actual dimensions differ from those specified, the
     * picture is stretched to match what is specified.
     * default : none (which means the original size of the image)
     */
    public String height;

    /**
     * Along with <A HREF="#height">height</A> specifies the size at which
     * the picture is drawn. <BR/>
     * If the picture's actual dimensions differ from those specified, the
     * picture is stretched to match what is specified.
     * default : none (which means the original size of the image)
     */
    public String width;

    /**
     * Text to show if picture is not drawn.
     * default : none (null)
     */
    public String alt;

    /**
     * Space between next element (horizontally)
     * default : none (-1)
     */
    public int hspace;

    /**
     * Space between next element (vertically)
     * default : none (-1)
     */
    public int vspace;

    /**
     * pixelwidth of the border drawn around the image.
     * default : 0
     */
    public int border;


    /**************************************************************************
     * Create a new instance of a ImageElement with a image at the given urls.
     * Sets the default-values for all variables but source.
     * See the variables to know their default-values.
     *
     * @param   pSrc    Url where the Image resides.
     */
    public ImageElement (String pSrc)
    {
        this.source = pSrc;
        this.height = null;
        this.width = null;
        this.alt = null;
        this.border = 0;
        this.name = null;
        this.id = null;
        this.classId = null;
        this.vspace = -1;
        this.hspace = -1;
    } // ImageElement


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
            this.appendStringBuffer (buf);
        } // if browser is supported
    } // build

    /**************************************************************************
     * Returns the string representation of this object. <BR/>
     * The string representation consists of the html text which represents the
     * object.
     *
     * @return  String represention of the object.
     */
    public String toString ()
    {
        StringBuffer buf = new StringBuffer (); // the string buffer

        // add the string representation to the string buffer:
        this.appendStringBuffer (buf);

        // return the string buffer as string:
        return buf.toString ();
    } // toString


    /**************************************************************************
     * Returns the StringBuffer representation of this object. <BR/>
     * The StringBuffer representation consists of the html text which
     * represents the object.
     *
     * @param   buf     StringBuffer represention of the object.
     */
    public void appendStringBuffer (StringBuffer buf)
    {
        buf.append (IE302.TAG_IMAGEBEGIN);
        buf.append (IE302.TA_SRC + this.inBrackets (this.source));
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
        if (this.height != null)
        {
            buf.append (IE302.TA_HEIGHT + this.inBrackets (this.height));
        } // if
        if (this.width != null)
        {
            buf.append (IE302.TA_WIDTH + this.inBrackets (this.width));
        } // if
        if (this.alt != null)
        {
            buf.append (IE302.TA_ALT + this.inBrackets (this.alt));
        } // if
        if (this.hspace != -1)
        {
            buf.append (IE302.TA_HSPACE + this.inBrackets ("" + this.hspace));
        } // if
        if (this.vspace != -1)
        {
            buf.append (IE302.TA_VSPACE + this.inBrackets ("" + this.vspace));
        } // if
        buf.append (IE302.TA_BORDER + this.inBrackets ("" + this.border));
        buf.append (IE302.TO_TAGEND);
    } // appendStringBuffer

} // class ImageElement
