/*
 * class: SelectUserContainerElement_01
 */

// package:
package m2.store;

// imports:
import ibs.bo.BOListConstants;
import ibs.bo.BOPathConstants;
import ibs.bo.OID;
import ibs.bo.SingleSelectionContainerElement_01;
import ibs.io.Environment;
import ibs.io.IOConstants;
import ibs.tech.html.BlankElement;
import ibs.tech.html.GroupElement;
import ibs.tech.html.ImageElement;
import ibs.tech.html.LinkElement;
import ibs.tech.html.RowElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TextElement;


/******************************************************************************
 * This class represents all necessary properties of one element of a
 * SelectUserContainerElement_01. <BR/>
 *
 * @version     $Id: SelectUserContainerElement_01.java,v 1.8 2010/04/07 13:37:06 rburgermann Exp $
 *
 * @author      Andreas Jansa (AJ), 990122
 ******************************************************************************
 */
public class SelectUserContainerElement_01 extends SingleSelectionContainerElement_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SelectUserContainerElement_01.java,v 1.8 2010/04/07 13:37:06 rburgermann Exp $";


    /**
     * fullname the current user. <BR/>
     */
    public String fullname = "";


    /**************************************************************************
     * Creates a MasterDataContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public SelectUserContainerElement_01 ()
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
    public SelectUserContainerElement_01 (OID oid)
    {
        // call constructor of super class:
        super (oid);
    } // PersonSearchContainerElement_01


    /**************************************************************************
     * Represent this object to the user. <BR/>
     *
     * @param   classId             class for the stylesheet.
     * @param   env         The current environment 
     *
     * @return  The row element which can be integrated into an HTML table.
     */
    public RowElement show (String classId, Environment env)
    {
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

        // full name + icon
        if (this.icon == null)               // no icon provided?
        {
            this.icon = this.typeName + ".gif";   // get icon from type
        } // if no icon provided
        ImageElement img = new ImageElement (this.layoutpath +
                                             BOPathConstants.PATH_OBJECTICONS +
                                             this.icon);

        GroupElement nameGroup = new GroupElement ();
        nameGroup.addElement (img);
        nameGroup.addElement (new BlankElement ());
        nameGroup.addElement (new TextElement (this.fullname));

        group = new GroupElement ();
        String oidStr = null;

        if (this.isLink)  // object is a link to another object?
        {
            oidStr = "" + this.linkedObjectId;
        } // if object is a link to another object
        else  // object is no link
        {
            oidStr = "" + this.oid;
        } // else object is no link
        group.addElement (new LinkElement (nameGroup,
            IOConstants.URL_JAVASCRIPT +
            "top.setSearchTextValues ('" + this.updateFieldName + "','" + this.fullname + "','" + oidStr + "');"));

        td = new TableDataElement (group);

        td.classId = classId;

        tr.addElement (td);

//! AJ 990913 deleted ... /////////////////////////////////////////////////////
// do not show the usernames of the other users to the current user -
// it's bad security
/*

        //name
//! AJ 990215 changed ... /////////////////////////////////////////////////////
        if (this.name != null && this.name.trim () != "")
            td = new TableDataElement (new TextElement ("" + this.name));
        else
            td = new TableDataElement (new BlankElement ());
//! AJ 990215 ... changed ... /////////////////////////////////////////////////
/ *
        text = new TextElement ("" + owner);
* /
//! ... AJ 990215 changed /////////////////////////////////////////////////////
        td = new TableDataElement (text);

        td.classId = classId;
        tr.addElement (td);

/ *
        // type of the object:
        GroupElement typeGroup = new GroupElement ();
        if (isLink)                     // object is a link?
        {
            img = new ImageElement (AppConstants.PATH_OBJECTICONS + "Referenz.gif");


            typeGroup.addElement (img);
            text = new TextElement (typeName);
            typeGroup.addElement (text);
        } // if object is a link?
        else                            // object is not a link
        {
            text = new TextElement (typeName);
            typeGroup.addElement (text);
        } // else object is not a link

        td = new TableDataElement (typeGroup);

        td.classId = classId;
        tr.addElement (td);
* /

        if (this.showExtendedAttributes)
        {
            text = new TextElement ("" + owner);
            td = new TableDataElement (text);

            td.classId = classId;

            tr.addElement (td);

/ *
            text = new TextElement (Helpers.dateTimeToString (lastChanged));
            td = new TableDataElement (text);

       td.classId = classId;
            td.nowrap = true;
            tr.addElement (td);
* /

         } //if
*/
//! ... AJ 990913 deleted /////////////////////////////////////////////////////

        return tr;                      // return the constructed row
    } // show

} // class SelectUserContainer_01
