/*
 * Class: PreElement.java
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
 * This is the PreElement Object, which can hold an element. <BR/>
 *
 * @version     $Id: PreElement.java,v 1.8 2007/07/23 08:17:32 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980525
 ******************************************************************************
 */
public class PreElement extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: PreElement.java,v 1.8 2007/07/23 08:17:32 kreimueller Exp $";


    /**
     * Specifies the element which is to display
     */
    public Element elem;


    /**************************************************************************
     * Create a new instance of a PreElement with the given Element.
     *
     * @param   el      Element which represents the Link.
     */
    public PreElement (Element el)
    {
        this.name = null;
        this.classId = null;
        this.id = null;
        this.elem = el;
    } // PreElement


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
            buf.append (IE302.TAG_PREBEGIN);
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
            buf.append (IE302.TO_TAGEND);
            this.elem.build (env, buf);
            buf.append (IE302.TAG_PREEND);
        } // if
    } // build

} // class PreElement
