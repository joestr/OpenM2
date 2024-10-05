/*
 * Class: LogContainerElement_01.java
 */

// package:
package ibs.obj.log;

// imports:
import java.util.Map;

import ibs.bo.BOListConstants;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.bo.type.Type;
import ibs.io.Environment;
import ibs.ml.MlInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.tech.html.BlankElement;
import ibs.tech.html.RowElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TextElement;


/******************************************************************************
 * This class represents all necessary properties of one entry of a
 * ReferenzContainer Element. <BR/>
 *
 * @version     $Id: LogContainerElementEntry_01.java,v 1.4 2010/06/09 14:10:06 btatzmann Exp $
 *
 * @author      Bernhard Tatzmann (BT), 080414
 ******************************************************************************
 */
public class LogContainerElementEntry_01 extends ContainerElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: LogContainerElementEntry_01.java,v 1.4 2010/06/09 14:10:06 btatzmann Exp $";


    /**
     * Width of first column. <BR/>
     */
    private static final int FIRST_COL_WIDTH = 2;

    /**
     * The Name of the modified field. <BR/>
     */
    private String  fieldName;

    /**
     * The actual Value of the modified field. <BR/>
     */
    private String  oldValue;

    /**
     * The actual Value of the modified field. <BR/>
     */
    private String  value;

    /**************************************************************************
     * Creates a LogContainerElementEntry_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public LogContainerElementEntry_01 ()
    {
        // call constructor of super class:
        super ();
        // initialize the instance's public properties:
    } // LogContainerElementEntry_01


    /**************************************************************************
     * Creates a LogContainerElementEntry_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     *
     * @param   oid         Value for the compound object id.
     */
    public LogContainerElementEntry_01 (OID oid)
    {
        // call constructor of super class:
        super (oid);

        // initialize the instance's public properties:
    } // LogContainerElementEntry_01


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
        int columnWidth = (100 - LogContainerElementEntry_01.FIRST_COL_WIDTH) /
            BOListConstants.LST_HEADINGS_LOGCONTAINER_ENTRY.length;

        RowElement entryRow = new RowElement (
            BOListConstants.LST_HEADINGS_LOGCONTAINER_ENTRY.length + 1);
        entryRow.classId = classId;

        TableDataElement entryTd = null;

        entryTd = new TableDataElement (new BlankElement ());
        entryRow.classId = classId;
        entryTd.width = Integer.toString (LogContainerElementEntry_01.FIRST_COL_WIDTH) + "%";
        entryRow.addElement (entryTd);

        // add the fieldName column
        TextElement text = new TextElement (this.fieldName);
        entryTd = new TableDataElement (text);
        entryTd.width = Integer.toString (columnWidth) + "%";
        entryTd.classId = classId;
        entryRow.addElement (entryTd);

        // add the old value column
        text = new TextElement (this.oldValue);
        entryTd = new TableDataElement (text);
        entryTd.width = Integer.toString (columnWidth) + "%";
        entryTd.classId = classId;
        entryRow.addElement (entryTd);

        // add the new value column
        text = new TextElement (this.value);
        entryTd = new TableDataElement (text);
        entryTd.width = Integer.toString (columnWidth) + "%";
        entryTd.classId = classId;
        entryRow.addElement (entryTd);
        // return the constructed row:
        return entryRow;
    } // show


    /**************************************************************************
     * Returns the fieldName field. <BR/>
     *
     * @return  The field name.
     */
    public String getFieldName ()
    {
        return this.fieldName;
    } // getFieldName


    /**************************************************************************
     * Sets the given fieldName. <BR/>
     *
     * @param type      the object's type
     * @param fieldName fieldName to set
     * @param env       the environment
     */
    public void setFieldName (Type type, String fieldName, Environment env)
    {
        // check if the type and fieldname is not null
        if (type != null && fieldName != null)
        {
            // retrieve the ml info for the field within the type
            Map<String, MlInfo> mlInfos = type.getMultilangFieldInfo (fieldName);

            if (mlInfos != null)
            {
                // retrieve the field name for the current user's locale
                String mlFieldName = mlInfos.get (
                    MultilingualTextProvider.getUserLocale (env).getLocaleKey ()).getName ();
                
                if (mlFieldName != null && !mlFieldName.isEmpty ())
                {
                    this.fieldName = mlFieldName;
                    return;
                } // if
            } // if
        } // if
        
        this.fieldName = fieldName;
    } // setFieldName


    /**************************************************************************
     * Return the oldValue field. <BR/>
     *
     * @return  The old value.
     */
    public String getOldValue ()
    {
        return this.oldValue;
    } // getOldValue


    /**************************************************************************
     * Sets the given oldValue. <BR/>
     *
     * @param oldValue value to set
     */
    public void setOldValue (String oldValue)
    {
        this.oldValue = oldValue;
    } // setOldValue


    /**************************************************************************
     * Return the value field. <BR/>
     *
     * @return  The current value.
     */
    public String getValue ()
    {
        return this.value;
    } // getValue


    /**************************************************************************
     * Sets the given value. <BR/>
     *
     * @param value value to set
     */
    public void setValue (String value)
    {
        this.value = value;
    } // setValue

} // class LogContainerElementEntry_01
