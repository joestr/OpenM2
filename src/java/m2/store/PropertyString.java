/*
 * Class: PropertyString.java
 */

// package:
package m2.store;

// imports:
import ibs.IbsObject;
import ibs.bo.Datatypes;
import ibs.io.Environment;
import ibs.io.IOConstants;
import ibs.tech.html.GroupElement;
import ibs.tech.html.InputElement;
import ibs.tech.html.RowElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TableElement;
import ibs.tech.html.TextElement;

import java.util.Vector;


/******************************************************************************
 * This class encapsulates the handling of a property string. A property string
 * is a string with values separated by a delimiter.
 * <BR/>
 *
 * @version     $Id: PropertyString.java,v 1.5 2007/07/23 08:21:37 kreimueller Exp $
 *
 * @author      Bernhard Walter (BW), 980908
 ******************************************************************************
 */
public class PropertyString extends IbsObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: PropertyString.java,v 1.5 2007/07/23 08:21:37 kreimueller Exp $";


    /**
     * The name of the property string. <BR/>
     */
    public String name = null;
    /**
     * This prefix is used for HTML-Output. <BR/>
     */
    private String prefix = null;
    /**
     * The delimiter character. <BR/>
     */
    private char delimiter;
    /**
     * The vector holding the properties. <BR/>
     */
    private Vector<String> properties;
    /**
     * The vector holding the values. <BR/>
     */
    private Vector<Boolean> values;
    /**
     * The size of the vector. <BR/>
     */
    private static final int SIZE = 10;
    /**
     * The increment of the vector. <BR/>
     */
    private static final int INCREMENT = 10;
    /**
     * The maximum length of a property. <BR/>
     */
    private static final int MAX_LENGTH = 20;
    /**
     * The number of columns when property string show as SelectionTable. <BR/>
     */
    private static final int CONST_COLUMNS = 5;


    /**************************************************************************
     * Creates a PropertyString object. <BR/>
     *
     * @param    name    The name of the property list
     * @param    props   The properties list
     * @param    values  A binary string inidicating which property is set
     * @param    delim   The delimiter used in the properties list
     * @param    prefix  Prefix String used for html output
     */
    public PropertyString (String name, String props, String values, char delim, String prefix)
    {
        this.name = name;
        this.delimiter = delim;
        this.properties = new Vector<String> (PropertyString.SIZE, PropertyString.INCREMENT);
        this.values = new Vector<Boolean> (PropertyString.SIZE, PropertyString.INCREMENT);
        this.parseString (props, values);
    } // PropertyString


    /**************************************************************************
     * Parses the property string and fills the Vector. <BR/>
     *
     * @param props      The property string
     * @param values     The binary string indicating which property is set
     */
    private void parseString (String props, String values)
    {
        int i = 0;
        int j;
        int k = 0;
        char[] value = new char[PropertyString.MAX_LENGTH];
        boolean notEnd = true;

        while (notEnd && ((j = props.indexOf (this.delimiter, i)) != -1))
        {
            // get the name of the property out of the string
            if ((i = props.indexOf (this.delimiter, j + 1)) != -1)
            {
                props.getChars (j + 1, i, value, 0);
                this.properties.addElement (new String (value));
            } // if
            else
            {
                props.getChars (j + 1, props.length (), value, 0);
                this.properties.addElement (new String (value));
                notEnd = false;
            } // else
            // get the flag if the property is set out of the binary string
            try
            {
                if (values.charAt (k++) == '0')
                {
                    this.values.addElement (new Boolean (false));
                } // if
                else
                {
                    this.values.addElement (new Boolean (true));
                } // else
            } // try
            catch (StringIndexOutOfBoundsException e)
            {
                this.values.addElement (new Boolean (false));
            } // catch
        } // while
    } // parseString


    /**************************************************************************
     * Show the history in frame. <BR/>
     *
     * @return  tabname of the object last visited
     */
    public TableElement showSelectionForm ()
    {
        TextElement text;
        GroupElement group;
        TableElement table;
        RowElement row;
        TableDataElement td;
        InputElement elem;

        table = new TableElement ();
        table.border = 0;
        // table.width = HtmlConstants.TAV_FULLWIDTH;
        row = new RowElement (PropertyString.CONST_COLUMNS);
        row.valign = IOConstants.ALIGN_MIDDLE;

        for (int i = 0; i < this.properties.size (); i++)
        {
            if ((i % PropertyString.CONST_COLUMNS) == 0)
            {
                table.addElement (row);
                row = new RowElement (PropertyString.CONST_COLUMNS);
            } // if
            group = new GroupElement ();

            elem = new InputElement (this.prefix + i, InputElement.INP_CHECKBOX, Datatypes.BOOL_TRUE);
            if (this.values.elementAt (i).equals (new Boolean (false)))
            {
                elem.checked = false;
            } // if
            else
            {
                elem.checked = true;
            } // else
            group.addElement (elem);
            text = new TextElement (this.properties.elementAt (i));
            group.addElement (text);

            td = new TableDataElement (group);
            row.addElement (td);
        } // for

        // add the generated table to the upper table:
        return table;
    } // showSelectionForm


    /**************************************************************************
     * Set the environment for the object. <BR/>
     *
     * @param   env     Environment
     */
    public void setEnv (Environment env)
    {
        this.env = env;
    } // setEnv

} // class PropertyString_01
