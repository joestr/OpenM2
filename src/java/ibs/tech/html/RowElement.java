/*
 Class: RowElement.java
*/

// package:
package ibs.tech.html;

// imports:
//KR TODO: unsauber
import ibs.io.Environment;
//KR TODO: unsauber
import ibs.io.IOConstants;
import ibs.tech.html.BlankElement;
import ibs.tech.html.BuildException;
import ibs.tech.html.Element;
import ibs.tech.html.IE302;
import ibs.tech.html.TableDataElement;

import java.util.Iterator;
import java.util.Vector;



/******************************************************************************
 * This is the RowElement object, which builds a HTML String
 * needed for a Table to be displayed.
 *
 * @version     $Id: RowElement.java,v 1.8 2011/06/22 07:33:50 btatzmann Exp $
 *
 * @author      Christine Keim (CK), 980316
 ******************************************************************************
 */
public class RowElement extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: RowElement.java,v 1.8 2011/06/22 07:33:50 btatzmann Exp $";


    /**
     * Backgroundcolor. <BR/>
     * default : none (null)
     */
    public String bgcolor;

    /**
     * Backgroundimage. <BR/>
     * default : none (null)
     */
    public String bgimage;

    /**
     * how the elements are aligned vertically. <BR/>
     * default : top
     */
    public String valign;

    /**
     * holds the Elements in the Row. <BR/>
     */
    protected Vector<TableDataElement> elements;

    /**
     * Number of cells in the row. <BR/>
     */
    public int rowCells = 0;

    /**
     * actual cell. <BR/>
     */
    protected int actual;

    /**
     * height of the RowElement. <BR/>
     */
    public String height;

    /**
     * spanned rows...
     */
    private int spanned = 0;

    /**
     * realElements
     */
    private int realElements = 0;


    /**************************************************************************
     * Creates a new instance of a RowElement with cols Elements in
     * Sets parameters to default values except the number of the cells in the
     * row.
     * See the variables to know their default values.
     *
     * @param cols      Number of cells in the row.
     */
    public RowElement (int cols)
    {
        this.rowCells = cols;
        this.name = null;
        this.id = null;
        this.classId = null;
        this.bgcolor = null;
        this.bgimage = null;
        this.valign = null;
        this.actual = 0;
        this.height = null;
        this.elements = new Vector<TableDataElement> (2);
    } // RowElement


    /**************************************************************************
     * Adds a Item in to the row. <BR/>
     *
     * @param   elem        ???
     */
    public void addElement (TableDataElement elem)
    {
        this.elements.addElement (elem);
        this.actual++;
        if (this.actual > this.rowCells)
        {
            this.rowCells = this.actual;
        } // if
    } // addElement


    /**************************************************************************
     * Adds a Item at the given position. <BR/>
     *
     * @param   elem        ???
     * @param   order       ???
     */
    public void addElement (TableDataElement elem, int order)
    {
        try
        {
            this.elements.insertElementAt (elem, order);
        } // try
        catch (ArrayIndexOutOfBoundsException e)
        {
            this.elements.setSize (order);
            this.elements.insertElementAt (elem, order);
        } // catch
    } // addElement


    /**************************************************************************
     * Clears the bow. <BR/>
     */
    public void clear ()
    {
        this.elements = new Vector<TableDataElement> (2);
        this.actual = 0;
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
        if (this.isBrowserSupported (env))
                                        // browser is supported?
        {
            buf.append (IE302.TAG_TABLEROWBEGIN);
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
                env.write (IE302.TA_TITLE + this.inBrackets (this.title));
            } // if
            if (this.bgcolor != null)
            {
                buf.append (IE302.TA_BGCOLOR + this.inBrackets (this.bgcolor));
            } // if
            if (this.bgimage != null)
            {
                buf.append (IE302.TA_BGIMAGE + this.inBrackets (this.bgimage));
            } // if
            if (this.valign != null)
            {
                buf.append (IE302.TA_VALIGN + this.inBrackets (this.valign));
            } // if
            if (this.height != null)
            {
                buf.append (IE302.TA_HEIGHT + this.inBrackets (this.height));
            } // if
            buf.append (IE302.TO_TAGEND);
            int cells = 0;

            if (this.elements != null) // there are some elements in the vector?
            {
                for (Iterator<TableDataElement> iter = this.elements.iterator (); iter.hasNext ();)
                {
                    TableDataElement e = iter.next ();
                    if (e != null)
                    {
                        if (e.colspan < 2)
                        {
                            cells++;
                        } // if
                        else
                        {
                            cells += e.colspan;
                        } // else
                        e.build (env, buf);
                    } // if
                } // for iter
            } // if

            cells += this.spanned; // spawned cells should not be blankised.
            if (cells < this.rowCells)
            {
                for (; cells < this.rowCells; cells++)
                {
                    BlankElement b = new BlankElement ();
                    TableDataElement dummy = new TableDataElement (b);
                    dummy.build (env, buf);
                } // for
            } // if
            buf.append (IE302.TAG_TABLEROWEND + "\n");
        } // if
    } // build


    /**************************************************************************
     * Replaces a Item in the row. <BR/>
     *
     * @param   elem    Element to be set.
     * @param   place   Position at which to set the element.
     */
    public void replaceElement (TableDataElement elem, int place)
    {
        if (place < this.rowCells)
        {
            this.elements.setElementAt (elem, place);
        } // if
    } // replaceElement


    /**************************************************************************
     * This method ... <BR/>
     *
     * @param   rowSpanning ???
     *
     * @return  ???
     */
    public Vector<int[]> setColsRight (Vector<int[]> rowSpanning)
    {
        this.spanned = 0;
        if (rowSpanning.size () > 0) // there are some rows from before to be spanned
        {
            for (Iterator<int[]> iter = rowSpanning.iterator (); iter.hasNext ();)
            {
                int[] e = iter.next ();
                if (e != null)
                {
                    if (e[0] > 0)
                    {
                        e[0]--;
                        this.spanned++;
                    } // if
                } // if
            } // for iter
        } // if
        this.realElements = 0;

        if (this.elements.size () > 0) // there are some tabledataElements
        {
            for (Iterator<TableDataElement> iter = this.elements.iterator (); iter.hasNext ();)
            {
                TableDataElement e = iter.next ();
                if (e != null)
                {
                    int[] rowSpan = new int[1];
                    rowSpan[0] = e.rowspan - 1; // how many rows have to be spanned?
                    rowSpanning.addElement (rowSpan);
                } // if
            } // for iter
        } // if

/*
        for (int i = 0; i < elements.length; i++) // look if there are any elements.
        {
            if (elements[i] != null)
            {
                if (elements[i].rowspan > 1) // there is a rowSpan in here
                {
                    int[] rowSpan = new int[1];
                    rowSpan[0] = elements[i].rowspan -1; // how many rows have to be spanned?
                    rowSpanning.addElement (rowSpan);
                }
                realElements++ ;
            }
        } // for
*/
        if ((this.realElements + this.spanned) > this.rowCells)
                                        // less rowCells than necessary!!!
        {
            this.rowCells = this.realElements + this.spanned;
        } // if less rowCells than necessary

        return rowSpanning;
    } // setColsRight

} // class RowElement
