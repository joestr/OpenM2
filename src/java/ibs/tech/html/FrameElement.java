/*
 * Class: FrameElement.java
 */

// package:
package ibs.tech.html;

// imports:
//KR TODO: unsauber
import ibs.io.Environment;
import ibs.tech.html.BuildException;
import ibs.tech.html.Frame;
import ibs.tech.html.IE302;


/******************************************************************************
 * This is the FrameElement Object, which builds a HTML-String
 * needed for a Frame to be displayed
 *
 * @version     $Id: FrameElement.java,v 1.8 2007/07/23 08:17:32 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980318
 ******************************************************************************
 */
public class FrameElement extends Frame
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: FrameElement.java,v 1.8 2007/07/23 08:17:32 kreimueller Exp $";


    /**
     * Specifies the url to get the source of the frame.
     */
    public String src;

    /**
     * Controls the margin height for the frame in pixels.
     * default : 0
     */
    public int marginheight;

    /**
     * Controls the margin width for the frame in pixels.
     * default : 0
     */
    public int marginwidth;

    /**
     * Creates a scrolling frame if set to true.
     * default : scrolling enabled (true);
     */
    public boolean scrolling;

    /**
     * Whether the user should be allowed to resize the frame
     * default : resize not allowed (false)
     */
    public boolean resize;


    /**************************************************************************
     * Creates a new instance of a FrameElement with the given name.and the given
     * URL.
     * Sets parameters to default values except the name and the URL.
     * See the variables to know their default-values.
     *
     * @param   pName   name of the frame
     * @param   pSrc    URL to load
     */
    public FrameElement (String pName, String pSrc)
    {
        this.name = pName;
        this.id = null;
        this.classId = null;
        this.marginheight = 0;
        this.src = pSrc;
        this.frameborder = false;
        this.scrolling = true;
        this.resize = false;
    } // FrameElement


    /**************************************************************************
     * Creates a new instance of a SelectElement with the given URL.
     * Sets parameters to default values except the URL.
     * See the variables to know their default-values.
     *
     * @param   pSrc    URL to load
     */
    public FrameElement (String pSrc)
    {
        this.name = null;
        this.id = null;
        this.classId = null;
        this.marginheight = 0;
        this.src = pSrc;
        this.frameborder = false;
        this.scrolling = true;
        this.resize = false;
    } // FrameElement


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
            buf.append (IE302.TAG_FRAMEBEGIN);
            buf.append (IE302.TA_SRC + this.inBrackets (this.src));
            if (this.name != null)
            {
                buf.append (IE302.TA_NAME + this.inBrackets (this.name));
            } // if
            if (this.title != null)
            {
                buf.append (IE302.TA_TITLE + this.inBrackets (this.title));
            } // if
            if (this.frameborder)
            {
                buf.append (IE302.TA_FRAMEBORDER + this.inBrackets ("1"));
            } // if
            else
            {
                buf.append (IE302.TA_FRAMEBORDER + this.inBrackets ("0"));
            } // else
            buf.append (IE302.TA_MARGINWIDTH + this.inBrackets ("" + this.marginwidth));
            buf.append (IE302.TA_MARGINHEIGHT + this.inBrackets ("" + this.marginheight));
            if (!this.resize)
            {
                buf.append (IE302.TA_NORESIZE);
            } // if
            if (this.scrolling)
            {
                buf.append (IE302.TA_SCROLLING + this.inBrackets (IE302.TO_SCROLLING));
            } // if
            else
            {
                buf.append (IE302.TA_SCROLLING + this.inBrackets (IE302.TO_NOSCROLLING));
            } // else
            buf.append (IE302.TO_TAGEND + "\n");
        } // if browser is supported
    } // build

} // class FrameElement
