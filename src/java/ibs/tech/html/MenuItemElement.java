/*
 * Class: MenuItemElement.java
 */

// package:
package ibs.tech.html;

// imports:
//KR TODO: unsauber
import ibs.io.Environment;
import ibs.tech.html.BuildException;
import ibs.tech.html.Element;
import ibs.tech.html.Font;
import ibs.tech.html.IE302;
import ibs.tech.html.ImageElement;
import ibs.tech.html.LinkElement;
import ibs.tech.html.Menu;


/******************************************************************************
 * This is the MenuItem, which displays a MenuItem on the Browser
 *
 * @version     $Id: MenuItemElement.java,v 1.8 2007/07/23 08:17:32 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980328
 ******************************************************************************
 */
public class MenuItemElement extends Menu
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: MenuItemElement.java,v 1.8 2007/07/23 08:17:32 kreimueller Exp $";


    /**
     * Specifies the font of the written text.
     */
    public Font font;

    /**
     * url of application
     * default : get.asp
     */
    public String url;

    /**
     * target of the link
     * default : null
     */
    public String target;

    /**
     * Holds the Element.
     *
     */
    protected Element elem;

    /**
     * Icon of the element
     * default : none (null)
     */
    public String elemSrc;


    /**************************************************************************
     * dummy, does nothing
     *
     * @param   pId     ?????
     *
     * @return  if the status was changed, is always <CODE>false</CODE>.
     */
    public boolean changeStatus (String pId)
    {
        return false;
    } // changeStatus


    /**************************************************************************
     * Creates a default MenuItem. <BR/>
     *
     * @param pElem     .......Element
     */
    public MenuItemElement (Element pElem)
    {
        this.elem = pElem;
        this.url = "get.asp";
        this.target = null;
    } // MenuItemElement


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
            buf.append (IE302.TO_TAGEND);
            buf.append (IE302.TO_BLANK);
            if (this.elemSrc != null)
            {
                ImageElement temp = new ImageElement (this.elemSrc);
                temp.hspace = 5;
                temp.build (env, buf);
            } // if
            if (this.font != null)
            {
                this.font.build (env, buf);
                if (this.font.bold)
                {
                    buf.append (IE302.TAG_BOLDBEGIN);
                } // if
                if (this.font.italic)
                {
                    buf.append (IE302.TAG_ITALICBEGIN);
                } // if
                if (this.font.underline)
                {
                    buf.append (IE302.TAG_UNDERBEGIN);
                } // if
            } // if
            LinkElement le = new LinkElement (this.elem, this.url);
            if (this.target != null)
            {
                le.target = this.target;
            } // if
            le.build (env, buf);
            if (this.font != null)
            {

                if (this.font.underline)
                {
                    buf.append (IE302.TAG_UNDEREND);
                } // if
                if (this.font.italic)
                {
                    buf.append (IE302.TAG_ITALICEND);
                } // if
                if (this.font.bold)
                {
                    buf.append (IE302.TAG_BOLDEND);
                } // if
                buf.append (IE302.TAG_FONTEND);
            } // if
        } // if
    } // build


    /**************************************************************************
     * Is the Menu a MenuItem?
     *
     * @return info about menu
     */
    public boolean isItem ()
    {
        return true;
    } // isItem

} // class MenuItemElement
