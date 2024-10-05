/*
 * Class: QueryExecutiveElement_01.java
 */

// package:
package ibs.obj.query;

// imports:
import ibs.bo.BOPathConstants;
import ibs.bo.ContainerElement;
import ibs.bo.Datatypes;
import ibs.io.Environment;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.obj.query.QueryConstants;
import ibs.tech.html.BlankElement;
import ibs.tech.html.GroupElement;
import ibs.tech.html.ImageElement;
import ibs.tech.html.InputElement;
import ibs.tech.html.LinkElement;
import ibs.tech.html.RowElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TextElement;
import ibs.util.FormFieldRestriction;
import ibs.util.StringHelpers;

import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * This class represents all necessary properties of one element of a
 * Catalog. <BR/>
 *
 * @version     $Id: QueryExecutiveElement_01.java,v 1.15 2010/04/15 15:31:13 rburgermann Exp $
 *
 * @author      Bernhard Walter (BW) 980908
 ******************************************************************************
 */
public class QueryExecutiveElement_01 extends ContainerElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: QueryExecutiveElement_01.java,v 1.15 2010/04/15 15:31:13 rburgermann Exp $";


    /**
     *
     */
    Environment env = null;

    /**
     *
     */
    public Vector<String> colData = new Vector<String> ();

    /**
     * datatypes of columns (image, string ...)
     */
    public Vector<String> colDataType = new Vector<String> ();

    /**
     * names of columns
     */
    public Vector<String> colName = new Vector<String> ();

    /**
     * multilang names of columns
     */
    public Vector<String> colMlName = new Vector<String> ();

    /**
     * multilang descripitions of columns
     */
    public Vector<String> colMlDescription = new Vector<String> ();
    
    /**
     * true if objectIcon should be added to the first column
     */
    public boolean addObjectIcon = false;

    /**
     * true if isNew - flag exist in query and new icon should be displayed if
     * object was not read allready from current user
     */
    public boolean addIsNewIcon = false;

    /**
     * true if isLink - flag exist in query and link icon should be displayed
     * if current object is link to other object
     */
    public boolean addIsLinkIcon = false;


    /**************************************************************************
     * Get the name of a column which is on a specific index position. <BR/>
     *
     * @param   index   Index of required column.
     *
     * @return  The name of the column.
     */
    public String getColName (int index)
    {
        if (this.colName != null && this.colName.size () > index)
        {
            return this.colName.elementAt (index);
        } // if
        return null;
    } // getColName


    /**************************************************************************
     * Get the type of a column which is on a specific index position. <BR/>
     *
     * @param   index   Index of required column.
     *
     * @return  The type of the column.
     */
    public String getColType (int index)
    {
        if (this.colDataType != null && this.colDataType.size () > index)
        {
            return this.colDataType.elementAt (index);
        } // if
        return null;
    } // getColName


    /**************************************************************************
     * Get the value of a column which is on a specific index position. <BR/>
     *
     * @param   index   Index of required column.
     *
     * @return  The value of the column.
     */
    public String getColValue (int index)
    {
        if (this.colData != null && this.colData.size () > index)
        {
            return this.colData.elementAt (index);
        } // if
        return null;
    } // getColName


    /**
     * Error message if the restrictions are not satisfied. <BR/>
     */
    protected String fieldRestriction = "alert ('wrong inputfieldtype for restriction')";


    /**************************************************************************
     * Represent this object to the user. <BR/>
     *
     * @param   classId     The CSS class to be set for the actual element.
     * @param   env         The current environment 
     *
     * @return  The constructed table row element.
     */
    public RowElement show (String classId, Environment env)
    {
        RowElement tr;
        tr = new RowElement (this.colData.size ());
        tr.classId = classId;
        TableDataElement td = null;
        GroupElement group = new GroupElement (); // groupElement for first
                                        // column - maybe with image and name
        TextElement columnValue = null; // value of current column in current
                                        // line
        ImageElement img = null;        // objecttype - image for current
                                        // object in list is displayed on the
                                        // left side of value in first column
        InputElement button = null;     // objecttype - button for current
                                        // object in list is displayed
        InputElement input = null;      // objecttype - input for current
                                        // object in list is displayed

        // if FUNCCOL_ISNEW is used
        // if object which is displayed in current line was not allready read by current user
        if (this.addIsNewIcon && this.isNew) // the element is new?
        {
            // display new icon:
            td = new TableDataElement (new ImageElement (BOPathConstants.PATH_GLOBAL + "new.gif"));
        } // if
        else
        {
            // add blank element:
            td = new TableDataElement (new BlankElement ());
        } // else

        // set classid of stylesheet for tabledata element:
        td.classId = classId;

        // add the before created new icon:
        tr.addElement (td);

        // if FUNCCOL_ISLINK is used
        // if linkicon should be added and if object is link to an other object
        if (this.addIsLinkIcon && this.isLink)
        {
            // add isLinkIcon before objecticon and value of first column:
            img = new ImageElement (this.layoutpath + BOPathConstants.PATH_OBJECTICONS + "Referenz.gif");
            group.addElement (img);
            group.addElement (new BlankElement ());
        } // if isLink


        int fieldDataType = 0;
        int fieldIndex = 0;
        this.fieldRestriction = "";

        // add columns to row:
        for (Iterator<String> iter = this.colDataType.iterator (); iter.hasNext (); fieldIndex++)
        {
            String colValueType = iter.next ();
            boolean found = false;

            // INPUT_STRING
            if (colValueType.indexOf (QueryConstants.COLUMNTYPE_INPUT_STRING) == 0)
            {
                fieldDataType = Datatypes.DT_TEXT;
                found = true;
            } // if

            // INPUT_INTEGER
            else if (colValueType.indexOf (QueryConstants.COLUMNTYPE_INPUT_INTEGER) == 0)
            {
                fieldDataType = Datatypes.DT_INTEGER;
                found = true;
            } // if

            // INPUT_NUMBER
            else if (colValueType.indexOf (QueryConstants.COLUMNTYPE_INPUT_NUMBER) == 0)
            {
                fieldDataType = Datatypes.DT_NUMBER;
                found = true;
            } // if

            // INPUT_MONEY
            else if (colValueType.indexOf (QueryConstants.COLUMNTYPE_INPUT_MONEY) == 0)
            {
                fieldDataType = Datatypes.DT_MONEY;
                found = true;
            } // if

            // INPUT_DATE
            else if (colValueType.indexOf (QueryConstants.COLUMNTYPE_INPUT_DATE) == 0)
            {
                fieldDataType = Datatypes.DT_DATE;
                found = true;
            } // if

            // create formFieldRestriction for existing inputfield
            if (found)
            {
                FormFieldRestriction fr = new FormFieldRestriction ();

                fr.dataType = fieldDataType;
                fr.name = this.colData.elementAt (fieldIndex);
                this.fieldRestriction += fr.buildRestrictScriptCode (env).toString ();
// AJ  DO NO LOOK ON THIS CODE  !!!!
// TURBOHACK FOR WOODSTARS  (will survive only 1 month)
                this.fieldRestriction = StringHelpers.replace (this.fieldRestriction,
                    "document.sheetForm", "document.forms[0]");
            } // if
        } // for iter


        fieldIndex = 0;
        // add columns to row:
        for (Iterator<String> iter = this.colDataType.iterator (); iter.hasNext (); fieldIndex++)
        {
            String colValueType = iter.next ();
            String colValue = this.colData.elementAt (fieldIndex);

            // get value of current column in current line:
            columnValue = new TextElement ("" + colValue);

            // if FUNCCOL_OBJECTID is used
            // create html-link to object in first column if oid is set:
            if (fieldIndex == 0 && this.oid != null && !this.oid.isEmpty ())
            {
                // if objecticon should be added - that means this.icon is set:
                if (this.addObjectIcon)
                {
                    // add objecticon to html-group of first column:
                    img = new ImageElement (this.layoutpath +
                        BOPathConstants.PATH_OBJECTICONS + this.icon);

                    group.addElement (img);
                    group.addElement (new BlankElement ());
                } // if addObjectIcon

                // add value to html-group of first column:
                group.addElement (columnValue);
                td = new TableDataElement (new LinkElement (group,
                    IOHelpers.getShowObjectJavaScriptUrl ("" + this.oid)));
            } // if
            // COLUMNTYPE_IMAGE
            else if (colValueType.equals (QueryConstants.COLUMNTYPE_IMAGE))
            {
                if (colValue != null)
                {
                    img = new ImageElement (colValue);
                    // AJ -> hardcoded width !!!
                    img.width = "" + 75;
                    td = new TableDataElement (img);
                } // if
                else
                {
                    td = new TableDataElement (new BlankElement ());
                } // else

            } // else if IMAGE
            // COLUMNTYPE_BUTTON
            else if (colValueType.equals (QueryConstants.COLUMNTYPE_BUTTON) ||
                colValueType.equals (QueryConstants.COLUMNTYPE_BUTTON_TEXT) ||
                colValueType.indexOf (QueryConstants.COLUMNTYPE_BUTTON_IMAGE) == 0)
                                        // if type of column is any type of
                                        // BUTTON?
            {

                // name of the button:
                String btnText = this.colName.elementAt (fieldIndex);
                // get the data of current column:
                String javaScript = colValue;
                // read the String for the BUTTON_IMAGE:
                String image      = colValueType;
                ImageElement iE   = null;


                if ((javaScript != null && javaScript.indexOf (QueryConstants.SYSVAR_ELEMOID) != -1 &&
                    this.oid != null && !this.oid.isEmpty ()) ||
                    (javaScript != null && javaScript.indexOf (QueryConstants.SYSVAR_ELEMOID) == -1))
                    // if the objectid is needed in the javaScript-call and the objectid is set or
                    // the objectid is not needed in javascript-call
                {
                    // check if SYSVAR #FIELDVALIDATION is used in javascript - call
                    if (javaScript.indexOf (QueryConstants.SYSVAR_FIELDVALIDATION) != -1)
                    {
                        // change the systemvariable #FIELDVALUATION to the real-oid:
                        javaScript = StringHelpers.replace (javaScript, QueryConstants.SYSVAR_FIELDVALIDATION,
                            this.fieldRestriction);
                    } // if SYSVAR #FIELDVALIDATION is used in javaScript - call


                    // check if SYSVAR #OBJECTID is used in javascript - call
                    if (javaScript.indexOf (QueryConstants.SYSVAR_ELEMOID) != -1)
                    {
                        // change the systemvariable #OBJECTID to the real-oid:
                        javaScript = StringHelpers.replace (javaScript,
                            QueryConstants.SYSVAR_ELEMOID, this.oid.toString ());
                    } // if FUNCCOL #OBJECTID is used in javaScript - call

                    // add javaScript prefix to javaScript url
                    javaScript = IOConstants.URL_JAVASCRIPT +
                        "top.scripts." + javaScript;

                    // create button

                    // BROWSERBUTTON
                    if (colValueType.equals (
                        QueryConstants.COLUMNTYPE_BUTTON))
                    {
                        // create a button:
                        button = new InputElement ("BUTTON",
                            InputElement.INP_BUTTON + "\" onClick=\"" +
                                javaScript, btnText);

                        // create browser button
                        td = new TableDataElement (button);
                    } // if
                    // BUTTON_TEXT
                    else if (colValueType.equals (
                        QueryConstants.COLUMNTYPE_BUTTON_TEXT))
                    {
                        // create the button text:
                        td = new TableDataElement (
                            new LinkElement (new TextElement (btnText),
                                javaScript));
                    } // else if
                    // BUTTON_IMAGE
                    else if (colValueType
                        .indexOf (QueryConstants.COLUMNTYPE_BUTTON_IMAGE) == 0)
                    {
                        // separate the image from BUTTON_IMAGE:
                        image = image.substring (image.indexOf (" (") + 1, image.indexOf (")"));
                        // create the syntax to the image:
                        iE = new ImageElement (this.layoutpath +
                            BOPathConstants.PATH_BUTTONS + image);

                        // altname of the image:
                        iE.alt = btnText;

                        // create the button image:
                        td = new TableDataElement (new LinkElement (iE , javaScript));
                    } // else if
                } // if there exists a valid oid and the javaScript string ...
                else                    // invalid oid
                {
                    // check which error should be shown
                    if (javaScript != null && javaScript.indexOf (QueryConstants.FUNCCOL_OBJECTID) != -1)
                    {
                        td = new TableDataElement (new TextElement (QueryConstants.EXC_NOOID));
                    } // if
                    else
                    {
                        td = new TableDataElement (new TextElement (QueryConstants.EXC_NOJAVASCRIPT));
                    } // else
                } // else invalid oid
            } // else if columntype is any BUTTON... type

            // COLUMNTYPE_INPUT
            else if (colValueType.indexOf (QueryConstants.COLUMNTYPE_INPUT_STRING) == 0 ||
                colValueType.indexOf (QueryConstants.COLUMNTYPE_INPUT_INTEGER) == 0 ||
                colValueType.indexOf (QueryConstants.COLUMNTYPE_INPUT_NUMBER) == 0 ||
                colValueType.indexOf (QueryConstants.COLUMNTYPE_INPUT_MONEY) == 0 ||
                colValueType.indexOf (QueryConstants.COLUMNTYPE_INPUT_DATE) == 0)
                                    // if type of column is any type of INPUT?
            {
                // get the data of current column:
                String fieldName = colValue;
                String inputValue = "";
                String inputSize = "";
                String inputType = colValueType;

                // create a textfield:
                input = new InputElement (fieldName, InputElement.INP_TEXT, inputValue);


                // check if inputsize is used
                if (inputType.indexOf (" (") != -1 && inputType.indexOf (")") != -1)
                {
                    // separate the inputsize from INPUT type:
                    inputSize = inputType.substring (inputType.indexOf (" (") + 1,
                        inputType.indexOf (")"));

                    try
                    {
                        input.size = new Integer (inputSize.trim ()).intValue ();
                    } // try
                    catch (NumberFormatException e)
                    {
                        // do nothing - standard size is used for inputfield
                    } //
                } // if


                // add fieldrestriction
                input.onChange = this.fieldRestriction;

                // add inputfield to table
                td = new TableDataElement (input);
            } // else if column is of type INPUT_NUMBER

            else
            {   // defaulttype = STRING
                // just add columnValue to tablerow as tabledataelement
                td = new TableDataElement (columnValue);
            } // else

            td.classId = classId;

            // add tabledataelement to tablerow
            tr.addElement (td);
        } // for iter

        // return the constructed row:
        return tr;
    } // show

} // class QueryObjectElement
