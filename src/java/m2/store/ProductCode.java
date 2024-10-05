/*
 * Class: ProductCode.java
 */

// package:
package m2.store;

// imports:
import ibs.BaseObject;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.io.HtmlConstants;
import ibs.io.IOConstants;
import ibs.tech.html.BlankElement;
import ibs.tech.html.Font;
import ibs.tech.html.GroupElement;
import ibs.tech.html.IE302;
import ibs.tech.html.InputElement;
import ibs.tech.html.RowElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TableElement;
import ibs.tech.html.TextElement;

import m2.store.StoreConstants;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;


/******************************************************************************
 * This class encapsulates the handling of a property string. A property string
 * is a string with values separated by a delimiter.
 * <BR/>
 *
 * @version     $Id: ProductCode.java,v 1.8 2007/07/23 08:21:37 kreimueller Exp $
 *
 * @author      Bernhard Walter (BW), 980908
 ******************************************************************************
 */
public class ProductCode extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ProductCode.java,v 1.8 2007/07/23 08:21:37 kreimueller Exp $";


    /**
     * The name of the code e.g. Color, Size, etc. <BR/>
     */
    public String name;
    /**
     * The String holding the possible properties. <BR/>
     */
    public String[] possibleValues = null;
    /**
     * The String holding the properties. <BR/>
     */
    public String[] values = null;
    /**
     * The String holding one property. <BR/>
     */
    public String value = null;
    /**
     * If the product properties are self defined. <BR/>
     */
    public boolean selfDefined = false;
    /**
     * The oid of the object holding the name of the code category. <BR/>
     */
    public OID categoryOid = null;
    /**
     * The oid of the object holding the predefined keys. <BR/>
     */
    public OID predefinedCodeOid = null;
    /**
     * Price valid for all values. <BR/>
     */
    public boolean priceValidForAllValues = false;

    /**
     * The number of columns when property string shown as a number
     * of checkboxes. <BR/>
     */
    private static final int CONST_COLUMNS = 5;

    /**
     * The tracing string. <BR/>
     */
    public static String trace;

   /**************************************************************************
    * Creates a Code object. <BR/>
    */
    public ProductCode ()
    {
        // nothing to do
    } // ProductCode


   /**************************************************************************
    * Creates a Code object. <BR/>
    *
    * @param    name    The name of the property list
    * @param    values  The properties which are set for one
    */
    public ProductCode (String name, String[] values)
    {
        this.name = name;
        this.values = values;
    } // ProductCode


   /**************************************************************************
    * Creates a Code object. <BR/>
    *
    * @param    name            The name of the property list.
    * @param    possibleValues  The properties list.
    * @param    values          The properties which are set for one.
    * @param    selfDefined     Is the code self defined?
    */
    public ProductCode (String name, String[] possibleValues,
                        String[] values, boolean selfDefined)
    {
        this.name = name;
        this.selfDefined = selfDefined;
        this.possibleValues = possibleValues;
        this.values = values;
    } // PropertySelection


    /**************************************************************************
     * Show the selection of possible values as checkboxes. <BR/>
     *
     * @param   t           The table to which the property shall be added.
     * @param   htmlName    The HTML name of the check boxes.
     * @param   nameFont    The font for the name.
     * @param   valueFont   The font for the value.
     * @param   classId     The CSS class.
     */
    public void showFormProperty (TableElement t, String htmlName,
                                  Font nameFont, Font valueFont,
                                  String classId)
    {
        TextElement text;
        TableElement table;
        RowElement tr;

        table = new TableElement ();
        table.border = 0;
        table.width = HtmlConstants.TAV_FULLWIDTH;

        this.showSelection (table, htmlName, nameFont, valueFont);

        tr = new RowElement (2);
        text = new TextElement (this.name + ": ");
        tr.addElement (new TableDataElement (text));
        tr.addElement (new TableDataElement (table));
        tr.classId = classId;
        t.addElement (tr);          // add new property to table
    } // showFormProperty


    /**************************************************************************
     * Specialized function to create a property option selection field. <BR/>
     *
     * @param   t               The table to which the property shall be added.
     * @param   headLine        The header of the selection.
     * @param   yes             The string shown after the yes option field.
     * @param   no              The string shown after the no option field.
     * @param   htmlName        The HTML name of the selection field.
     * @param   optionFieldName The name of the option field.
     * @param   checked         If the yes-radio button is checked.
     * @param   nameFont        The font for the name.
     * @param   valueFont       The font for the value.
     * @param   classId         The CSS class.
     */
    public void showFormPropertyWithOptionField (TableElement t,
                                                 String headLine, String yes,
                                                 String no, String htmlName,
                                                 String optionFieldName,
                                                 boolean checked,
                                                 Font nameFont, Font valueFont,
                                                 String classId)
    {
        boolean checkedLocal = checked; // variable for local assignments
        TextElement text;
        TableElement table = new TableElement (4);
        RowElement row;
        TableDataElement td;
        InputElement input;
        GroupElement optionGroup;

        table.width = HtmlConstants.TAV_FULLWIDTH;
        table.border = 0;
        // show the header of the field
        text = new TextElement (headLine +  ": ");
        text.font = nameFont;
        td = new TableDataElement (text);
        td.colspan = 3;
        row = new RowElement (3);
        row.addElement (td);
        table.addElement (row);

        // show a blank element
        row = new RowElement (3);
        td = new TableDataElement (new BlankElement ());
        td.width = "" + StoreConstants.CONST_FIRSTTAB;
        td.rowspan = 3;
        row.addElement (td);

        // show the yes radio button
        optionGroup = new GroupElement ();
        input = new InputElement (optionFieldName, InputElement.INP_RADIO, Datatypes.BOOL_TRUE);
        input.checked = checkedLocal;
        optionGroup.addElement (input);
        optionGroup.addElement (new TextElement (yes));
        td = new TableDataElement (optionGroup);
        td.colspan = 2;
        row.addElement (td);
        table.addElement (row);

        // if no properties defined for this product return
        if ((this.possibleValues == null) || (this.possibleValues.length < 2))
        {
            input.checked = true;
            checkedLocal = true;
        } // if
        else
        {
            // show the no radio button
            row = new RowElement (2);
            optionGroup = new GroupElement ();
            input = new InputElement (optionFieldName, InputElement.INP_RADIO, Datatypes.BOOL_FALSE);
            input.checked = !checkedLocal;
            optionGroup.addElement (input);
            optionGroup.addElement (new TextElement (no));
            td = new TableDataElement (optionGroup);
            td.colspan = 2;
            row.addElement (td);
            table.addElement (row);

            // add a blank element
            row = new RowElement (2);
            td = new TableDataElement (new BlankElement ());
            td.width = "" + StoreConstants.CONST_SECONDTAB;
            row.addElement (td);

            // add the table with the checkboxes
            TableElement tcodes = new TableElement ();
            tcodes.width = HtmlConstants.TAV_FULLWIDTH;
            tcodes.border = 0;
            this.showSelection (tcodes, htmlName, nameFont, valueFont);
            td = new TableDataElement (tcodes);
            td.colspan = 0;
            row.addElement (td);

            table.addElement (row);
        } // else no properties defined

        // add the table to the row and return it
        row = new RowElement (2);
        row.classId = classId;
        td = new TableDataElement (table);
        td.colspan = 2;
        row.addElement (td);
        t.addElement (row);
    } // showFormPropertySelection


   /**************************************************************************
    * Converts the String array to a string with the given delimiter. <BR/>
    *
    * @param    delimiter   The delimiter.
    *
    * @return   String representation of the object.
    */
    public String toString (String delimiter)
    {
        StringBuffer sb = new StringBuffer ();
        if ((this.values != null) && (this.values.length > 0))
        {
            int i;
            for (i = 0; i < this.values.length - 1; i++)
            {
                sb.append (this.values[i]);
                sb.append (delimiter);
            } // for i
            // append last value
            sb.append (this.values[i]);
        } // if
        else
        {
            sb.append ("");
        } // else
        return sb.toString ();
    } // toString

   /**************************************************************************
    * Converts the String to a string array with the given delimiter. <BR/>
    *
    * @param    str         The string to be parsed.
    * @param    delimiter   The delimiter .
    *
    * @return   The number of values.
    */
    public int parseStringToValues (String str, String delimiter)
    {
        int nrValues  = 0;

        if (str != null && delimiter != null)
        {
            StringTokenizer st = new StringTokenizer (str, delimiter);
            if ((nrValues = st.countTokens ()) > 0)
            {
                this.values = new String[nrValues];
                try
                {
                    int i = 0;
                    while (true)
                    {
                        this.values[i++] = st.nextToken ();
                    } // while
                } // try
                catch (NoSuchElementException e)
                {
                    // should not occur
                } // catch
            } // if
        } // if
        return nrValues;
    } // parseString


   /***************************************************************************
    * Converts the String to a string array with the given delimiter. <BR/>
    *
    * @param    str         The string to be parsed.
    * @param    delimiter   The delimiter.
    *
    * @return   The number of values.
    */
    public int parseStringToPossibleValues (String str, String delimiter)
    {
        int nrValues  = 0;
        if (str != null && delimiter != null)
        {
            StringTokenizer st = new StringTokenizer (str, delimiter);
            if ((nrValues = st.countTokens ()) > 0)
            {
                this.possibleValues = new String[nrValues];
                try
                {
                    int i = 0;
                    while (true)
                    {
                        this.possibleValues[i++] = st.nextToken ();
                    } // while
                } // try
                catch (NoSuchElementException e)
                {
                    // should not occur
                } // catch
            } // if
        } // if

        return nrValues;
    } // parseString


     /*************************************************************************
     * Show the selection of possible values as check boxes. <BR/>
     *
     * @param   table       Table into which to fill the output.
     * @param   htmlName    Html name of the element.
     * @param   nameFont    Font to be used for name.
     * @param   valueFont   Font to be used for value.
     */
    private void showSelection (TableElement table, String htmlName,
                               Font nameFont, Font valueFont)
    {
        TextElement text;
        GroupElement group;
        RowElement row;
        TableDataElement td;
        InputElement elem;

        row = new RowElement (ProductCode.CONST_COLUMNS);
        row.valign = IOConstants.ALIGN_MIDDLE;
        table.addElement (row);

        int i;
        int j = 0;

        for (i = 0; i < this.possibleValues.length; i++)
        {
            group = new GroupElement ();

            // generate checkbox
            // the name of the checkbox is the
            elem = new InputElement (htmlName,
                                          InputElement.INP_CHECKBOX,
                                          this.possibleValues[i]);

            // if the properties are the same, check the box
            if ((this.values != null) && (j < this.values.length) &&
                (this.possibleValues[i].equals (this.values[j])))
            {
                elem.checked = true;
                j++;
            } // if

            group.addElement (elem);
            text = new TextElement (this.possibleValues[i]);
            text.font = valueFont;
            group.addElement (text);

            td = new TableDataElement (group);
            row.addElement (td);

            // max. number of columns reached
            if ((i % ProductCode.CONST_COLUMNS) == ProductCode.CONST_COLUMNS - 1)
            {
                row = new RowElement (ProductCode.CONST_COLUMNS);
                table.addElement (row);
                row.valign = IOConstants.ALIGN_MIDDLE;
            } // if
        } // while
        // add the generated table to the upper table
    } // showSelection


    /**************************************************************************
     * Returns a string representation of the object. In general, the
     * <code>toString</code> method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     *
     * @return  a string representation of the object.
     */
    public String toString ()
    {
        StringBuffer sb = new StringBuffer ();
        sb.append ("name = " + this.name);
        sb.append (IE302.TAG_NEWLINE);
        if ((this.values != null) && (this.values.length > 0))
        {
            int i;
            for (i = 0; i < this.values.length - 1; i++)
            {
                sb.append (this.values[i]);
                sb.append (", ");
            } // for i
            // append last value
            sb.append (this.values[i]);
        } // if

        return sb.toString ();
    } // toString

} // class ProductCode
