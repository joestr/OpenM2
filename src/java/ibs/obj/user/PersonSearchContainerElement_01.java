/*
 * Class: PersonSearchContainerElement_01.java
 */

// package:
package ibs.obj.user;

// imports:
//KR TODO: unsauber
import ibs.app.AppFunctions;
import ibs.bo.BOConstants;
import ibs.bo.BOListConstants;
import ibs.bo.BOPathConstants;
import ibs.bo.OID;
import ibs.bo.SingleSelectionContainerElement_01;
import ibs.io.Environment;
import ibs.io.IOConstants;
import ibs.obj.user.UserConstants;
import ibs.tech.html.BlankElement;
import ibs.tech.html.GroupElement;
import ibs.tech.html.ImageElement;
import ibs.tech.html.LinkElement;
import ibs.tech.html.RowElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TextElement;


/******************************************************************************
 * This class represents all necessary properties of one element of a
 * PersonSearchContainerElement_01. <BR/>
 *
 * @version     $Id: PersonSearchContainerElement_01.java,v 1.12 2010/04/07 13:37:09 rburgermann Exp $
 *
 * @author      Andreas Jansa (AJ), 981217
 ******************************************************************************
 */
public class PersonSearchContainerElement_01 extends SingleSelectionContainerElement_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: PersonSearchContainerElement_01.java,v 1.12 2010/04/07 13:37:09 rburgermann Exp $";


    /**
     * opening for a person. <BR/>
     */
    public String prefix = "";

    /**
     * fullname of a person. <BR/>
     */
    public String fullname = "";

    /**
     * firm of a person. <BR/>
     */
    public String firm = "";

    /**
     * email of a person. <BR/>
     */
    public String email = "";


    /**
     * Oid of the user where a link to a specific person should be added. <BR/>
     */
    public OID callingOid = null;


    /**
     * Show the Link to callingObject, or show all matching Objects to
     * searchparameter. <BR/>
     */
    protected int showLink = 0;


    /**************************************************************************
     * Creates a MasterDataContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public PersonSearchContainerElement_01 ()
    {
        // call constructor of super class:
        super ();
        // initialize properties common to all subclasses:

        // init specifics of actual class:
        this.initClassSpecifics ();
    } // PersonSearchContainerElement_01


    /**************************************************************************
     * Creates a MasterDataContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     *
     * @param   oid         Value for the compound object id.
     */
    public PersonSearchContainerElement_01 (OID oid)
    {
        // call constructor of super class:
        super (oid);
        // initialize properties common to all subclasses:

        // init specifics of actual class:
        this.initClassSpecifics ();
    } // PersonSearchContainerElement_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set the instance's public properties:
        this.prefix = "";
        this.fullname = "";
        this.firm = "";
        this.email = "";
    } // initClassSpecifics


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
        LinkElement link = null;
        RowElement tr;
        TableDataElement td = null;
        GroupElement group;             // group of layout elements

        if (this.showExtendedAttributes)
        {
            tr = new RowElement (UserConstants.LST_HEADINGS_PERSONS.length + 1);
        } // if
        else
        {
            tr = new RowElement (UserConstants.LST_HEADINGS_PERSONS_REDUCED.length + 1);
        } // else

        tr.classId = classId;

        group = new GroupElement ();    // create new group element
        group.addElement (new BlankElement ()); // show blank space

        td = new TableDataElement (group); // compose table data element
        td.width = BOListConstants.LST_NEWCOLWIDTH;
        td.classId = classId;

        tr.addElement (td);

        GroupElement nameGroup = new GroupElement ();

        // check if all Attributes shall be shown
        if (this.showExtendedAttributes)
        {
            // column of the prefix of the person
            text = new TextElement ("" + this.prefix);
            td = new TableDataElement (text);
            td.classId = classId;

            tr.addElement (td);
        } // if

        // Image
        // persons name & icon
        if (this.icon == null)               // no icon provided?
        {
            this.icon = this.typeName + ".gif";   // get icon from type
        } // if

        ImageElement img = new ImageElement (this.layoutpath +
            BOPathConstants.PATH_OBJECTICONS + this.icon);
        nameGroup.addElement (img);
        nameGroup.addElement (new BlankElement ());
        nameGroup.addElement (new TextElement (this.fullname));

        // check if linkelement (name of person) should call a function
        // to show the object, or a function to add a link to this person
        if (this.showLink == BOConstants.SHOWLINKEDOBJECT)
        {
            // if you want to show the link to this person (show object) -> cancel this changes
            td = new TableDataElement (nameGroup);
            td.classId = classId;

        } // if
        else
        {
            // url should call a function to add a link in a other object to this person
            td = new TableDataElement (new LinkElement (nameGroup,
                IOConstants.URL_JAVASCRIPT + "top.loadForReference ('" +
                AppFunctions.FCT_CREATEREFERENCE + "' , '" + this.oid +
                "' , '" + this.callingOid + "' , '" + this.updateFieldName + "');"));

            td.classId = classId;

/*
                IOConstants.URL_JAVASCRIPT + "sheet.location.href= m2get.asp?" +
                AppArguments.ARG_FUNCTION + AppArguments.ARG_ASSIGN + AppFunctions.FCT_CREATEREFERENCE +
                AppArguments.ARG_SEP + AppArguments.ARG_OID + AppArguments.ARG_ASSIGN +
                this.oid + AppArguments.ARG_SEP + AppArguments.ARG_CALLINGOID + AppArguments.ARG_ASSIGN +
                this.callingOid));
*/
        } // else

        tr.addElement (td);

        // column of the firm of the person
        text = new TextElement ("" + this.firm);
        td = new TableDataElement (text);
        td.classId = classId;

        tr.addElement (td);

        // check if all Attributes shall be shown
        if (this.showExtendedAttributes)
        {
            // column of the  email of the person displayed
            text = new TextElement (this.email);
            link = new LinkElement (text, "mailto:" + this.email);
            td = new TableDataElement (link);
            td.classId = classId;

            tr.addElement (td);
        } // if

        return tr;                      // return the constructed row
    } // show

} // class PersonSearchContainerElement_01
