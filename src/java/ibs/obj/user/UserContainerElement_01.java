/*
 * Class: UserContainerElement_01.java
 */

// package:
package ibs.obj.user;

// imports:
import ibs.bo.BOPathConstants;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.io.Environment;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
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
 * UserContainer. <BR/>
 *
 * @version     $Id: UserContainerElement_01.java,v 1.12 2010/04/07 13:37:09 rburgermann Exp $
 *
 * @author      Keim Christine (CK), 980702
 ******************************************************************************
 */
public class UserContainerElement_01 extends ContainerElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: UserContainerElement_01.java,v 1.12 2010/04/07 13:37:09 rburgermann Exp $";


    /**************************************************************************
     * Stores the full name of the represented user
     */
    public String fullName;


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a UserContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public UserContainerElement_01 ()
    {
        // call constructor of super class:
        super ();

        // initialize the instance's private properties:

        // initialize the instance's public properties:
        this.fullName = "";
    } // UserContainerElement_01


    /**************************************************************************
     * Creates a UserContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     *
     * @param   oid         Value for the compound object id.
     */
    public UserContainerElement_01 (OID oid)
    {
        // call constructor of super class:
        super (oid);

        // initialize the instance's private properties:

        // set the instance's public properties:
        this.fullName = "";
    } // UserContainerElement_01


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

        if (this.showExtendedAttributes)
        {
            tr = new RowElement (5);
        } // if
        else
        {
            tr = new RowElement (3);
        } // else

        tr.classId = classId;

        td = new TableDataElement (new BlankElement ());
        td.classId = classId;

        tr.addElement (td);

        if (this.icon == null)                // no icon provided?
        {
            this.icon = this.typeName + ".gif";   // get icon from type
        } // if
        ImageElement img = new ImageElement (this.layoutpath +
            BOPathConstants.PATH_OBJECTICONS + this.icon);
        GroupElement nameGroup = new GroupElement ();

        nameGroup.addElement (img);
        nameGroup.addElement (new BlankElement ());
        if (this.isLink)                 // object is a link to another object?
        {
            img = new ImageElement (this.layoutpath +
                BOPathConstants.PATH_OBJECTICONS + "Referenz.gif");
            nameGroup.addElement (img);
        } // if
        text = new TextElement (this.name);
        nameGroup.addElement (text);

        td = new TableDataElement (new LinkElement (nameGroup,
            IOHelpers.getShowObjectJavaScriptUrl (this.oid.toString ())));
        td.classId = classId;

        tr.addElement (td);
/*
        // column of the typename of the userobject
        text = new TextElement (typeName);
        td = new TableDataElement (text);
        tr.addElement (td);
*/

        // column of the fullname of the user
        text = new TextElement (this.fullName);
        td = new TableDataElement (text);
        td.classId = classId;

        tr.addElement (td);

        if (this.showExtendedAttributes)
        {
            // column of the owner of the userobject
            text = new TextElement ("" + this.owner);
            td = new TableDataElement (text);
            td.classId = classId;

            tr.addElement (td);

            // column of the last change of the userobject
            text = new TextElement (
                DateTimeHelpers.dateTimeToString (this.lastChanged));
            td = new TableDataElement (text);
            td.classId = classId;

            td.nowrap = true;
            td.alignment = IOConstants.ALIGN_RIGHT;
            tr.addElement (td);
        } //if
/*
        // column of the status of the user
        String st;
        switch (state) // state ?
        {
            case ST_ACTIVE:
                st = TOK_STACTIVE;
                break;
            case ST_INACTIVE:
                st = TOK_STINACTIVE;
                break;
            default:
                st = TOK_STUNKNOWN;
        } // switch state ?
        text = new TextElement(st);
        td = new TableDataElement (text);
        tr.addElement (td);
*/
        return tr;                      // return the constructed row
    } // show

} // class UserContainerElement_01
