/*
 * Class: QuerySelectContainerElement_01
 */

// package:
package ibs.obj.menu;

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
 * QuerySelectContainerElement_01. <BR/>
 *
 * @version     $Id: QuerySelectContainerElement_01.java,v 1.10 2010/04/07 13:37:12 rburgermann Exp $
 *
 * @author      Monika Eisenkolb (ME), 161001
 ******************************************************************************
 */
public class QuerySelectContainerElement_01 extends SingleSelectionContainerElement_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: QuerySelectContainerElement_01.java,v 1.10 2010/04/07 13:37:12 rburgermann Exp $";


    /**************************************************************************
     * Creates a QuerySelectContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public QuerySelectContainerElement_01 ()
    {
        // call constructor of super class:
        super ();

    } // QuerySelectContainerElement_01


    /**************************************************************************
     * Creates a QuerySelectContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     *
     * @param   oid         Value for the compound object id.
     */
    public QuerySelectContainerElement_01 (OID oid)
    {
        // call constructor of super class:
        super (oid);

    } // QuerySelectContainerElement_01


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
        TableDataElement td = null;
        GroupElement group;             // group of layout elements

        tr = new RowElement (BOListConstants.LST_HEADINGS_REDUCED.length + 1);

        tr.classId = classId;

        // set text to be shown on mouse over:
        group = new GroupElement ();    // create new group element
        group.addElement (new BlankElement ()); // show blank space

        td = new TableDataElement (group); // compose table data element
        td.width = BOListConstants.LST_NEWCOLWIDTH;

        td.classId = classId;

        tr.addElement (td);

        // fullname + icon
        if (this.icon == null)               // no icon provided?
        {
            this.icon = this.typeName + ".gif"; // get icon from type
        } // if
        ImageElement img = new ImageElement (this.layoutpath +
            BOPathConstants.PATH_OBJECTICONS + this.icon);

        GroupElement nameGroup = new GroupElement ();
        nameGroup.addElement (img);
        nameGroup.addElement (new BlankElement ());
        nameGroup.addElement (new TextElement (this.name));

        group = new GroupElement ();
        if (this.isLink)  // object is a link to another object?
        {
            group.addElement (new LinkElement (nameGroup,
                IOConstants.URL_JAVASCRIPT + "top.setSearchTextValues ('" +
                this.updateFieldName + "','" +
                this.name + "','" +
                this.linkedObjectId + "');"));
        } // if
        else  // object is no link
        {
            group.addElement (new LinkElement (nameGroup,
                IOConstants.URL_JAVASCRIPT + "top.setSearchTextValues ('" +
                this.updateFieldName + "','" +
                this.name + "','" +
                this.oid + "');"));
        } // else object is no link

        td = new TableDataElement (group);
        td.classId = classId;
        tr.addElement (td);

        // add the typename
        td = new TableDataElement (new TextElement (this.typeName));
        td.classId = classId;
        tr.addElement (td);

        // add the description
        td = new TableDataElement (new TextElement (this.description));
        td.classId = classId;
        tr.addElement (td);

        return tr;                      // return the constructed row
    } // show

} // class QuerySelectContainerElement_01
