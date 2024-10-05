/*
 * Class: RowItem.java
 */

// package:
package ibs.tech.html;

// imports:
//KR TODO: unsauber
import ibs.io.Environment;
import ibs.tech.html.BlankElement;
import ibs.tech.html.BuildException;
import ibs.tech.html.Element;
import ibs.tech.html.IE302;


/******************************************************************************
 * This is the TableDataElement Object, which builds a HTML-String
 * needed for a TableDataElement to be displayed
 *
 * @version     $Id: TableDataElement.java,v 1.8 2007/07/23 08:17:32 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 980316
 ******************************************************************************
 */
public class TableDataElement extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: TableDataElement.java,v 1.8 2007/07/23 08:17:32 kreimueller Exp $";


    /**
     * Backgroundcolor
     * default : none (null)
     */
    public String bgcolor;

    /**
     * Backgroundimage
     * default : none (null)
     */
    public String bgimage;

    /**
     * The element in the Tabledata
     */
    Element elem;

    /**
     * center, right or left alignment?
     * default : none (null)
     */
    public String alignment;

    /**
     * top, middle or bottom alignment?
     * default : none (null)
     */
    public String valign;

    /**
     * spans through more rows?
     * default : none (-1)
     */
    public int rowspan;

    /**
     * spans through more cols?
     * default : none (-1)
     */
    public int colspan;

    /**
     * width of the tablecell
     * default : none (null)
     */
    public String width;

    /**
     * Should content be wrapped when it exceeds page
     * default : false (-1)
     */
    public boolean nowrap;

    /**
     * height of the TableDataElement. <BR/>
     */
    public String height;


    /**************************************************************************
     * Creates a new instance of a TableDataElement.
     * Sets parameters to default values except the element.
     * See the variables to know their default-values.
     *
     * @param pElem    ... Element in the TableData
     */
    public TableDataElement (Element pElem)
    {
        this.name = null;
        this.id = null;
        this.classId = null;
        this.elem = pElem;
        this.bgcolor = null;
        this.bgimage = null;
        this.alignment = null;
        this.width = null;
        this.rowspan = -1;
        this.valign = null;
        this.colspan = -1;
        this.nowrap = false;
        this.height = null;
    } // TableDataElement


    /**************************************************************************
     * Creates a new instance of a TableDataElement.
     * Sets parameters to default values except the element and the size.
     * See the variables to know their default-values.
     *
     * @param pElem    ... Element in the TableData
     * @param pSize    ... size of the tablecell!
     */
    public TableDataElement (Element pElem, String pSize)
    {
        this.name = null;
        this.id = null;
        this.classId = null;
        this.elem = pElem;
        this.bgcolor = null;
        this.bgimage = null;
        this.alignment = null;
        this.width = pSize;
        this.rowspan = -1;
        this.valign = null;
        this.colspan = -1;
        this.nowrap = false;
        this.height = null;
    } // TableDataElement


    /**************************************************************************
     * Creates a new instance of a TableDataElement.
     * Sets parameters to default values.
     */
    public TableDataElement ()
    {
        this.name = null;
        this.id = null;
        this.classId = null;
        this.elem = null;
        this.bgcolor = null;
        this.bgimage = null;
        this.alignment = null;
        this.width = null;
        this.rowspan = -1;
        this.valign = null;
        this.colspan = -1;
        this.nowrap = false;
        this.height = null;
    } // TableDataElement


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
            buf.append (IE302.TAG_TABLECELLBEGIN);
            if (this.id != null)
            {
                buf.append (IE302.TA_ID).append (this.inBrackets (this.id));
            } // if
            if (this.classId != null)
            {
                buf.append (IE302.TA_CLASSID).append (this.inBrackets (this.classId));
            } // if
            if (this.title != null)
            {
                buf.append (IE302.TA_TITLE).append (this.inBrackets (this.title));
            } // if
            if (this.width != null)
            {
                buf.append (IE302.TA_WIDTH).append (this.inBrackets (this.width));
            } // if
            if (this.bgcolor != null)
            {
                buf.append (IE302.TA_BGCOLOR).append (this.inBrackets (this.bgcolor));
            } // if
            if (this.bgimage != null)
            {
                buf.append (IE302.TA_BGIMAGE).append (this.inBrackets (this.bgimage));
            } // if
            if (this.rowspan != -1)
            {
                buf.append (IE302.TA_ROWSPAN).append (this.inBrackets ("" + this.rowspan));
            } // if
            if (this.colspan != -1)
            {
                buf.append (IE302.TA_COLSPAN).append (this.inBrackets ("" + this.colspan));
            } // if
            if (this.valign != null)
            {
                buf.append (IE302.TA_VALIGN).append (this.inBrackets (this.valign));
            } // if
            if (this.alignment != null)
            {
                buf.append (IE302.TA_ALIGN).append (this.inBrackets (this.alignment));
            } // if
            if (this.nowrap)
            {
                buf.append (IE302.TA_NOWRAP);
            } // if
            if (this.height != null)
            {
                buf.append (IE302.TA_HEIGHT).append (this.inBrackets (this.height));
            } // if
            buf.append (IE302.TO_TAGEND);

            if (this.elem != null)
            {
                this.elem.build (env, buf);
            } // if
            else
            {
                this.elem = new BlankElement ();
                this.elem.build (env, buf);
            } // else
            buf.append (IE302.TAG_TABLECELLEND);
        } // if
    } // build

} // class TableDataElement
