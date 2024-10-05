/*
 Class: LineElement.java
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
 * This is the LineElement Object, which makes a line in the
 * HTML-Page.
 *
 * @version     $Id: LineElement.java,v 1.8 2007/07/23 08:17:32 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980314
 ******************************************************************************
 */
public class LineElement extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: LineElement.java,v 1.8 2007/07/23 08:17:32 kreimueller Exp $";


    /**
     * height of the line
     * default : none (null)
     */
    public int size;

    /**
     * specifies the horizontal width of the line
     */
    public String width;

    /**
     * specifies the alignemnt
     */
    public String align;


    /**************************************************************************
     * Constructs a LineElement. <BR/>
     */
    public LineElement ()
    {
        this.size = -1;
        this.width = null;
        this.align = null;
    } // LineElement


    /**************************************************************************
     * Constructs a LineElement. <BR/>
     *
     * @param   pSize   The size of the line.
     * @param   pWidth  The widht of the line.
     */
    public LineElement (int pSize, String pWidth)
    {
        this.size = pSize;
        this.width = pWidth;
    } // LineElement


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
        buf.append (IE302.TAG_HR);
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
        if (this.size != -1)
        {
            buf.append (IE302.TA_SIZE + this.inBrackets ("" + this.size));
        } // if
        if (this.width != null)
        {
            buf.append (IE302.TA_WIDTH + this.inBrackets (this.width));
        } // if
        if (this.align != null)
        {
            buf.append (IE302.TA_ALIGN + this.inBrackets (this.align));
        } // if
        buf.append (IE302.TO_TAGEND);
    } // build

} // class LineElement
