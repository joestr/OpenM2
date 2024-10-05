/*
 * Class: PropertySelection.java
 */

// package:
package m2.store;

// imports:
import ibs.BaseObject;
import ibs.io.HtmlConstants;
import ibs.io.IOConstants;
import ibs.tech.html.GroupElement;
import ibs.tech.html.InputElement;
import ibs.tech.html.RowElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TableElement;
import ibs.tech.html.TextElement;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;


/******************************************************************************
 * This class encapsulates the handling of a property string. A property string
 * is a string with realProperties separated by a delimiter.
 * <BR/>
 *
 * @version     $Id: PropertySelection.java,v 1.7 2007/07/23 08:21:37 kreimueller Exp $
 *
 * @author      Bernhard Walter (BW), 980908
 ******************************************************************************
 */
public class PropertySelection extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: PropertySelection.java,v 1.7 2007/07/23 08:21:37 kreimueller Exp $";


    /**
     * This prefix is used for the checkboxes in the HTML-Output. <BR/>
     */
    private String prefix = null;
    /**
     * The delimiter character in the property list. <BR/>
     */
    private String delimiter;
    /**
     * The String holding the possible properties. <BR/>
     */
    private String possibleProperties;
    /**
     * The String holding the set properties. <BR/>
     */
    private String realProperties;
    /**
     * The number of columns when property string shown as a number
     * of checkboxes. <BR/>
     */
    private static final int CONST_COLUMNS = 5;


   /***************************************************************************
    * Creates a PropertySelection object. <BR/>
    *
    * @param    possbileProperties  The properties list.
    * @param    realProperties      The properties which are set for one.
    * @param    delim               The delimiter used in the properties list.
    * @param    prefix              Prefix String used for html output.
    */
    public PropertySelection (String possbileProperties, String realProperties, String delim, String prefix)
    {
        this.delimiter = delim;
        this.prefix = prefix;
        this.possibleProperties = possbileProperties;
        this.realProperties = realProperties;
    } // PropertySelection


    /**************************************************************************
     * Get the number of elements in the property list. <BR/>
     *
     * @return  Number of properties.
     */
    public int elements ()
    {
        StringTokenizer stprops =
            new StringTokenizer (this.possibleProperties, this.delimiter);
        return stprops.countTokens ();
    } // elements


    /**************************************************************************
     * Show the history in frame. <BR/>
     *
     * @return  A table containing the checkboxes.
     */
    public TableElement showSelection ()
    {
        TextElement text;
        GroupElement group;
        TableElement table;
        RowElement row;
        TableDataElement td;
        InputElement elem;
        String property1 = null;
        String property2 = null;
        boolean found = true;

        table = new TableElement ();
        table.border = 0;
        table.width = HtmlConstants.TAV_FULLWIDTH;
        row = new RowElement (PropertySelection.CONST_COLUMNS);
        row.valign = IOConstants.ALIGN_MIDDLE;

        // if property list is empty return empty table
        if ((this.possibleProperties == null) ||
            (this.possibleProperties.length () == 0))
        {
            return table;
        } // if

        StringTokenizer stprops =
            new StringTokenizer (this.possibleProperties, this.delimiter);
        StringTokenizer stvals =
            new StringTokenizer (this.realProperties, this.delimiter);

        int i = 0;
        try
        {
            property2 = stvals.nextToken ();
        } // try
        catch (NoSuchElementException e)
        {
            property2 = null;
        } // catch NoSuchElementException

        while (found)
        {
            try
            {
                group = new GroupElement ();
                // get next token from the possible property list
                property1 = stprops.nextToken ();
                // generate check box
                // the name of the check box is the prefix plus a
                // sequential number
                elem = new InputElement (this.prefix + i,
                                              InputElement.INP_CHECKBOX,
                                              property1);
                // if the properties are the same, check the box
                if (property1.equals (property2))
                {
                    elem.checked = true;
                    try
                    {
                        // get next token from real properties list
                        property2 = stvals.nextToken ();
                    } // try
                    catch (NoSuchElementException e)
                    {
                        property2 = null;
                    } // catch NoSuchElementException
                } // if

                group.addElement (elem);
                text = new TextElement (property1);
                group.addElement (text);

                td = new TableDataElement (group);
                row.addElement (td);

                // max. number of columns reached
                if ((++i % PropertySelection.CONST_COLUMNS) == 0)
                {
                    table.addElement (row);
                    row = new RowElement (PropertySelection.CONST_COLUMNS);
                    row.valign = IOConstants.ALIGN_MIDDLE;
                } // if
            } // try
            catch (NoSuchElementException e)
            {
                table.addElement (row);
                found = false;
            } // catch NoSuchElementException
        } // while
        // add the generated table to the upper table

        return table;
    } // showSelection

} // class PropertySelection_01
