/*
 * Class: SingleSelectionContainerElement.java
 */

// package:
package ibs.bo;

// imports:
import ibs.bo.BOListConstants;
import ibs.bo.BOPathConstants;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.io.Environment;
import ibs.io.IOConstants;
import ibs.tech.html.BlankElement;
import ibs.tech.html.GroupElement;
import ibs.tech.html.ImageElement;
import ibs.tech.html.LinkElement;
import ibs.tech.html.RowElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TextElement;
import ibs.util.DateTimeHelpers;


/******************************************************************************
 * This class represents all necessary properties of one element of a
 * SingleSelectionContainerElement_01. <BR/>
 *
 * @version     $Id: SingleSelectionContainerElement_01.java,v 1.14 2010/04/07 13:37:08 rburgermann Exp $
 *
 * @author      Andreas Jansa (AJ), 990120
 ******************************************************************************
 */
public class SingleSelectionContainerElement_01 extends ContainerElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SingleSelectionContainerElement_01.java,v 1.14 2010/04/07 13:37:08 rburgermann Exp $";


    /**
     * Fieldname of field to be updated in user_changeform after selection of person. <BR/>
     */
    public String updateFieldName = "";


    /**************************************************************************
     * Creates a MasterDataContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public SingleSelectionContainerElement_01 ()
    {
        // call constructor of super class:
        super ();
    } // PersonSearchContainerElement_01


    /**************************************************************************
     * Creates a MasterDataContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     *
     * @param   oid         Value for the compound object id.
     */
    public SingleSelectionContainerElement_01 (OID oid)
    {
        // call constructor of super class:
        super (oid);
    } // PersonSearchContainerElement_01


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
        TextElement text = null;
        RowElement tr;
        TableDataElement td = null;
        GroupElement group;             // group of layout elements

        if (this.showExtendedAttributes)
        {
            tr = new RowElement (BOListConstants.LST_HEADINGS.length + 1);
        } // if
        else
        {
            tr = new RowElement (BOListConstants.LST_HEADINGS_REDUCED.length + 1);
        } // else

        tr.classId = classId;

        // set text to be shown on mouse over:
        group = new GroupElement ();    // create new group element
        group.addElement (new BlankElement ()); // show blank space

        td = new TableDataElement (group); // compose table data element
        td.width = BOListConstants.LST_NEWCOLWIDTH;

        td.classId = classId;

        tr.addElement (td);

        if (this.icon == null)               // no icon provided?
        {
            this.icon = this.typeName + ".gif"; // get icon from type
        } // if
        ImageElement img = new ImageElement (this.layoutpath +
            BOPathConstants.PATH_OBJECTICONS + this.icon);

        GroupElement nameGroup = new GroupElement ();
        nameGroup.addElement (img);
        nameGroup.addElement (new BlankElement ());

        if (this.isLink)                // object is a link to another object?
        {
            img = new ImageElement (this.layoutpath +
                BOPathConstants.PATH_OBJECTICONS + "Referenz.gif");
            nameGroup.addElement (img);
        } // if

        nameGroup.addElement (new TextElement (this.name));

        group = new GroupElement ();
        String oidStr = null;
        if (this.isLink)                // object is a link to another object?
        {
            oidStr = "" + this.linkedObjectId;
        } // if
        else  // object is no link
        {
            oidStr = "" + this.oid;
        } // else object is no link
        group.addElement (new LinkElement (nameGroup,
            IOConstants.URL_JAVASCRIPT + "top.setObjectRef ('" +
                this.updateFieldName + "','" + this.name + "','" +
                oidStr + "');"));

        td = new TableDataElement (group);

        td.classId = classId;

        tr.addElement (td);

        if (this.showExtendedAttributes)
        {
            // type of the object:
            GroupElement typeGroup = new GroupElement ();

            text = new TextElement (this.typeName);
            typeGroup.addElement (text);

            td = new TableDataElement (typeGroup);
            td.classId = classId;

            tr.addElement (td);

            text = new TextElement ("" + this.owner);
            td = new TableDataElement (text);
            td.classId = classId;

            tr.addElement (td);

            text = new TextElement (DateTimeHelpers.dateTimeToString (this.lastChanged));
            td = new TableDataElement (text);
            td.classId = classId;

            td.nowrap = true;
            td.alignment = IOConstants.ALIGN_RIGHT;
            tr.addElement (td);
        } //if
        return tr;                      // return the constructed row
    } // show

} // class SingleSelectionContainerElement_01
