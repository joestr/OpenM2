 /*
 * Class: TableElement.java
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
import ibs.tech.html.RowElement;
import ibs.tech.html.TableDataElement;

import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * This is the TableElement Object, which builds a HTML-String
 * needed for a Table to be displayed
 *
 * @version     $Id: TableElement.java,v 1.13 2011/06/22 07:35:43 btatzmann Exp $
 *
 * @author      Christine Keim (CK), 980316
 ******************************************************************************
 */
public class TableElement extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: TableElement.java,v 1.13 2011/06/22 07:35:43 btatzmann Exp $";


    /**
     * Constant initialElements of the Vector holding the
     * elements. <BR/>
     * Initially set to 20.
     */
    protected static final int ELEMENTS_INITIAL = 20;

    /**
     * Constant incrementElements of the Vector holding the
     * elements. <BR/>
     * Initially set to 5.
     */
    protected static final int ELEMENTS_INCREMENT = 5;

    /**
     * The column headers. <BR/>
     */
    public RowElement head = null;

    /**
     * The rows to display. <BR/>
     */
    protected Vector<RowElement> rows = null;

    /**
     * Border color. <BR/>
     * Default: none (<CODE>null</CODE>).
     */
    public String borderColor = null;

    /**
     * Border color dark. <BR/>
     * Default: none (<CODE>null</CODE>).
     */
    public String borderColorDark = null;

    /**
     * Border color light. <BR/>
     * Default: none (<CODE>null</CODE>).
     */
    public String borderColorLight = null;

    /**
     * Border in pixel. <BR/>
     * Default: -1 (not set).
     */
    public int border = -1;

    /**
     * Number of columns. <BR/>
     */
    public int cols = -1;

    /**
     * Width. <BR/>
     */
    public String width = null;

    /**
     * Border within the table. <BR/>
     */
    public String ruletype = null;

    /**
     * Background color. <BR/>
     */
    public String bgcolor = null;

    /**
     * Background image. <BR/>
     */
    public String bgimage = null;

    /**
     * Frame types: border, void, above, below, hsides, lhs, rhs, vsides, box. <BR/>
     * Default: none (<CODE>null</CODE>).
     */
    public String frametypes = null;

    /**
     * Sets the amount of space, in pixels, between the sides of a cell and its
     * contents. <BR/>
     */
    public int cellpadding = 0;

    /**
     * Sets the amount of space, in pixels, between the frame (exterior) of the
     * table and the cells in the table. <BR/>
     */
    public int cellspacing = 0;

    /**
     * Sets the alignment of each column. <BR/>
     */
    public String[] alignment;

    /**
     * Sets the classIds of each column. <BR/>
     */
    public String[] classIds;


    /**************************************************************************
     * Creates a new instance of a TableElement
     */
    public TableElement ()
    {
        // nothing to do
    } // TableElement


    /**************************************************************************
     * Creates a new instance of a TableElement
     *
     * @param pCols             ...number of columns
     */
    public TableElement (int pCols)
    {
        this.cols = pCols;
    } // TableElement


    /**************************************************************************
     * Adds a Row. <BR/>
     * If asHeader is true, then the given RowElement is added as the header.
     *
     * @param   row         The row to be added.
     * @param   asHeader    HeaderRow?
     */
    public void addElement (RowElement row, boolean asHeader)
    {
        if (asHeader)
        {
            this.head = row;
            return;
        } // if

        this.addElement (row);
    } // addElement


    /**************************************************************************
     * Adds a Row at the actual position
     *
     * @param   row     The row to be added.
     */
    public void addElement (RowElement row)
    {
        if (this.rows == null)
        {
            this.rows = new Vector<RowElement> (TableElement.ELEMENTS_INITIAL,
                TableElement.ELEMENTS_INCREMENT);
        } // if
        this.rows.addElement (row);
    } // addElement


    /**************************************************************************
     * Adds a Row at the given position
     *
     * @param   row     ?????
     * @param   order   ?????
     */
    public void addElement (RowElement row, int order)
    {
        if (this.rows == null)
        {
            this.rows = new Vector<RowElement> (TableElement.ELEMENTS_INITIAL,
                TableElement.ELEMENTS_INCREMENT);
        } // if
        try
        {
            this.rows.insertElementAt (row, order);
        } // try
        catch (ArrayIndexOutOfBoundsException e)
        {
            this.rows.setSize (order);
            this.rows.insertElementAt (row, order);
        } // catch
    } // addElement


    /**************************************************************************
     * Clears the Table (the rows and the head)
     */
    public void clear ()
    {
        this.rows = null;
        this.head = null;
    } // clear


    /**************************************************************************
     * Writes the element on the browser. <BR/>
     *
     * @param   env     OutputStream
     * @param   buf     Buffer into which to write the output.
     *
     * @exception   BuildException
     *              An error occurred during building the output.
     */
    public void build (Environment env, StringBuffer buf) throws BuildException
    {
        if (this.isBrowserSupported (env)) // browser is supported?
        {

//            buf.append ("" + cols);
            buf.append (IE302.TAG_TABLEBEGIN);
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
            if (this.bgcolor != null)
            {
                buf.append (IE302.TA_BGCOLOR + this.inBrackets (this.bgcolor));
            } // if
            if (this.bgimage != null)
            {
                buf.append (IE302.TA_BGIMAGE + this.inBrackets (this.bgimage));
            } // if
            if (this.borderColor != null)
            {
                buf.append (IE302.TA_BORDERCOLOR + this.inBrackets (this.borderColor));
            } // if
            if (this.borderColorDark != null)
            {
                buf.append (IE302.TA_BORDERCOLORDARK + this.inBrackets (this.borderColorDark));
            } // if
            if (this.borderColorLight != null)
            {
                buf.append (IE302.TA_BORDERCOLORLIGHT + this.inBrackets (this.borderColorLight));
            } // if
            if (this.width != null)
            {
                buf.append (IE302.TA_WIDTH + this.inBrackets (this.width));
            } // if
            if (this.border != -1)
            {
                buf.append (IE302.TA_BORDER + this.inBrackets ("" + this.border));
            } // if
            buf.append (IE302.TA_CELLSPACING + this.inBrackets ("" + this.cellspacing));
            buf.append (IE302.TA_CELLPADDING + this.inBrackets ("" + this.cellpadding));
            if (this.frametypes != null)
            {
                buf.append (IE302.TA_FRAMETYPE + this.inBrackets (this.frametypes));
            } // if
            if (this.ruletype != null)
            {
                buf.append (IE302.TA_RULETYPE + this.inBrackets (this.ruletype));
            } // if
            buf.append (IE302.TO_TAGEND);

            if (this.alignment != null)
            {
                buf.append (IE302.TAG_COLGROUPBEGIN);
                for (int i = 0; i < this.alignment.length; i++)
                {
                    buf.append (IE302.TAG_COLBEGIN2);
                    if (this.alignment[i] != null)
                    {
                        buf.append (IE302.TA_ALIGN + this.inBrackets (this.alignment[i]));
                    } // if
                    if ((this.classIds != null) && (this.classIds[i] != null))
                    {
                        buf.append (IE302.TA_CLASSID + this.inBrackets (this.classIds[i]));
                    } // if
                    buf.append (IE302.TO_TAGEND);
                } // for
                buf.append (IE302.TAG_COLGROUPEND);
            } // if
            else
            {
                if (this.cols != -1)
                {
                    buf.append (IE302.TAG_COLGROUPBEGIN);
                    for (int i = 0; i < this.cols; i++)
                    {
                        buf.append (IE302.TAG_COLBEGIN2);
                        if ((this.classIds != null) && (i < this.classIds.length) && (this.classIds[i] != null))
                        {
                            buf.append (IE302.TA_CLASSID + this.inBrackets (this.classIds[i]));
                        } // if
                        buf.append (IE302.TO_TAGEND);
                    } // for
                    buf.append (IE302.TAG_COLGROUPEND);
                } // if
            } // else

            if (this.head != null)
            {
                buf.append (IE302.TAG_TABLEHEADBEGIN);
                this.head.build (env, buf);
                buf.append (IE302.TAG_TABLEHEADEND);
            } // if

            buf.append (IE302.TAG_TABLEBODYBEGIN);
            if (this.rows != null)      // there are some elements in the vector?
            {
                for (Iterator<RowElement> iter = this.rows.iterator (); iter.hasNext ();)
                {
                    RowElement e = iter.next ();
                    if (e != null)
                    {
                        e.build (env, buf);
                    } // if
                } // for iter
            } // if
            buf.append (IE302.TAG_TABLEBODYEND);
            buf.append (IE302.TAG_TABLEEND + "\n");
        } // if browser is supported
    } // build


    /**************************************************************************
     * This method ... <BR/>
     */
    public void setColsRight ()
    {
        boolean toChange = false;
        Vector<int[]> rowSpanning = new Vector<int[]> ();
        if (this.rows != null) // there are some elements in the vector?
        {
            for (Iterator<RowElement> iter = this.rows.iterator (); iter.hasNext ();)
            {
                RowElement e = iter.next ();
                if (e != null)
                {
                    rowSpanning = e.setColsRight (rowSpanning);
                } // if
                if (e.rowCells > this.cols) // if rowCount > cols...
                {
                    toChange = true;
                    this.cols = e.rowCells;
                } // if
            } // for iter
            if (toChange)
            {
                for (Iterator<RowElement> iter = this.rows.iterator (); iter.hasNext ();)
                {
                    RowElement e = iter.next ();
                    if (e != null)
                    {
                        e.rowCells = this.cols;
                    } // if
                } // for iter
            } // if
        } // if
    } // setColsRight


    /**************************************************************************
     * This method ... <BR/>
     *
     * @return  ???
     */
    public int getRowNumber ()
    {
/*
        if (head == null)
            return this.rows.size ();
        else
*/
        return this.rows.size () + 1;
    } // getRowNumber


    /**************************************************************************
     * This method ... <BR/>
     *
     * @param  td  ???
     */
    public void addToFirstRow (TableDataElement td)
    {
/*
        if (head != null)
            head.addElement (td);
        else if (rows.size () > 0)
        {
*/
        if (this.head != null)
        {
            this.head.addElement (new TableDataElement (new BlankElement ()));
        } // if

        Iterator<RowElement> iter = this.rows.iterator ();
        if (iter.hasNext ())
        {
            RowElement e = iter.next ();
            e.addElement (td);
        } // if
//        } // else if
    } // addToFirstRow

} // class TableElement
