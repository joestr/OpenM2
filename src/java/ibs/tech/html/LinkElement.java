/*
 Class: LinkElement.java
*/

// package:
package ibs.tech.html;

// imports:
//KR TODO: unsauber
import ibs.io.Environment;


/******************************************************************************
 * This is the abstract LinkElement Object, which can hold more elements
 * at a time.
 *
 * @version     $Id: LinkElement.java,v 1.8 2007/07/23 08:17:32 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980316
 ******************************************************************************
 */
public class LinkElement extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: LinkElement.java,v 1.8 2007/07/23 08:17:32 kreimueller Exp $";


    /**
     * Specifies the element which is in the link
     */
    public Element elem;

    /**
     * Specifies the target of the link
     * default : none (null)
     */
    public String target;

    /**
     * Specifies the URL the link is pointing at
     */
    public String link;

    /**
     * OnMouseOver
     */
    public String onMouseOver;


    /**************************************************************************
     * Create a new instance of a LinkElement with a Link on the given Element.
     * Sets parameters to default values except the element and the URL href.
     * See the variables to know their default-values.
     *
     * @param   el      Element which represents the Link.
     * @param   href    Represents the URL to go to if Link is activated.
     */
    public LinkElement (Element el, String href)
    {
        this.name = null;
        this.classId = null;
        this.id = null;
        this.target = null;
        this.elem = el;
        this.link = href;
        this.onMouseOver = null;
    } // LinkElement


    /**************************************************************************
     * Create a new instance of a LinkElement with a Link on the given Element.
     * Sets parameters to default values except the element, the URL href and
     * the target.
     * See the variables to know their default-values.
     *
     * @param   el      Element which represents the Link.
     * @param   href    Represents the URL to go to if Link is activated.
     * @param   targ    Target frame to open link to.
     */
    public LinkElement (Element el, String href, String targ)
    {
        this.name = null;
        this.classId = null;
        this.id = null;
        this.target = targ;
        this.elem = el;
        this.link = href;
        this.onMouseOver = null;
    } // LinkElement


    /**************************************************************************
     * Create a new instance of a LinkElement with a Link on the given Element.
     * Sets parameters to default values except the element, the URL href and
     * the target.
     * See the variables to know their default-values.
     *
     * @param   el      Element which represents the Link.
     * @param   href    Represents the URL to go to if Link is activated.
     * @param   targ    Target frame to open link to.
     * @param   onMouse Event handler for onMouseOver event.
     */
    public LinkElement (Element el, String href, String targ, String onMouse)
    {
        this.name = null;
        this.classId = null;
        this.id = null;
        this.target = targ;
        this.elem = el;
        this.link = href;
        this.onMouseOver = onMouse;
    } // LinkElement


    /**************************************************************************
     * Writes the element on the browser. <BR/>
     *
     * @param   env         OutputStream
     * @param   buf         The string buffer for the result.
     *
     * @exception   BuildException
     *              An error occurred during building the output.
     */
    public void build (Environment env, StringBuffer buf) throws BuildException
    {
        if (this.isBrowserSupported (env)) // browser is supported?
        {
            buf.append (IE302.TAG_LINKBEGIN);
            buf.append (IE302.TA_HREF + this.inBrackets (this.link));
            if (this.classId != null)
            {
                buf.append (IE302.TA_CLASSID + this.inBrackets (this.classId));
            } // if
            if (this.name != null)
            {
                buf.append (IE302.TA_NAME + this.inBrackets (this.name));
            } // if
            if (this.id != null)
            {
                buf.append (IE302.TA_ID + this.inBrackets (this.id));
            } // if
            if (this.target != null)
            {
                buf.append (IE302.TA_TARGET + this.inBrackets (this.target));
            } // if
            if (this.onMouseOver != null)
            {
                buf.append (IE302.TA_ONMOUSEOVER +
                            this.inBrackets (this.onMouseOver));
            } // if
            if (this.title != null)
            {
                buf.append (IE302.TA_TITLE + this.inBrackets (this.title));
            } // if
            buf.append (IE302.TO_TAGEND);
            this.elem.build (env, buf);
            buf.append (IE302.TAG_LINKEND);
        } //if
    } // build

} // class LinkElement
