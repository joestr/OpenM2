/*
 * Class: RefContainerElement_01.java
 */

// package:
package ibs.obj.ref;

// imports:
//KR TODO: unsauber
import ibs.app.AppFunctions;
import ibs.bo.BOConstants;
import ibs.bo.BOListConstants;
import ibs.bo.BOPathConstants;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.bo.cache.ObjectPool;
import ibs.bo.type.TypeConstants;
import ibs.bo.type.TypeContainer;
import ibs.io.Environment;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.session.ApplicationInfo;
import ibs.tech.html.BlankElement;
import ibs.tech.html.GroupElement;
import ibs.tech.html.ImageElement;
import ibs.tech.html.LinkElement;
import ibs.tech.html.RowElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TextElement;


/******************************************************************************
 * a RefContainerElement_01
 *
 * @version     $Id: RefContainerElement_01.java,v 1.15 2010/04/07 13:37:16 rburgermann Exp $
 *
 * @author      Keim Christine (CK), 990527
 ******************************************************************************
 */
public class RefContainerElement_01 extends ContainerElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: RefContainerElement_01.java,v 1.15 2010/04/07 13:37:16 rburgermann Exp $";


    /**
     * Holds the actual application info. <BR/>
     * This application info object is used to store the global (= user
     * independent) data within the application.
     */
    private ApplicationInfo p_app = null;


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a RefContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public RefContainerElement_01 ()
    {
        // call constructor of super class:
        super ();

        // initialize the instance's private properties:

        // initialize the instance's public properties:
    } // RefContainerElement_01


    /**************************************************************************
     * Creates a RefContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     *
     * @param   oid         Value for the compound object id.
     */
    public RefContainerElement_01 (OID oid)
    {
        // call constructor of super class:
        super (oid);

        // initialize the instance's private properties:

        // set the instance's public properties:
    } // RefContainerElement_01


    /**************************************************************************
     * Set the application info object. <BR/>
     *
     * @param   app     The application info object to be set.
     */
    public void setApp (ApplicationInfo app)
    {
        // set the property value:
        this.p_app = app;
    } // setApp


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

        tr = new RowElement (BOListConstants.LST_HEADINGS_REDUCED.length + 2);
        tr.classId = classId;

        // set text to be shown on mouse over:

        group = new GroupElement ();    // create new group element
        if (this.isNew)                      // the element is new?
        {
            group.addElement (new ImageElement (BOPathConstants.PATH_GLOBAL + "new.gif"));
                                        // show "new" icon
        } // if
        else
        {
            group.addElement (new BlankElement ()); // show blank space
        } // else

        td = new TableDataElement (group); // compose table data element
        td.width = BOListConstants.LST_NEWCOLWIDTH;
        td.classId = classId;

        tr.addElement (td);

        if (this.icon == null)               // no icon provided?
        {
            this.icon = this.typeName + ".gif";   // get icon from type
        } // if
        ImageElement img = new ImageElement (this.layoutpath +
            BOPathConstants.PATH_OBJECTICONS + this.icon);
        td = new TableDataElement (img); // compose table data element
        td.width = BOListConstants.LST_NEWCOLWIDTH;
        td.classId = classId;

        tr.addElement (td);

        GroupElement nameGroup = new GroupElement ();
//        nameGroup.addElement (img);
        if (this.isLink)                 // object is a link to another object?
        {
            img = new ImageElement (this.layoutpath +
                BOPathConstants.PATH_OBJECTICONS + "Referenz.gif");
            nameGroup.addElement (img);
        } // if
        nameGroup.addElement (new BlankElement ());
        nameGroup.addElement (new TextElement (this.name));

        group = new GroupElement ();

////////////////////////////////////////////////////////////
//
// START: SERVICEPOINT HACK
//
        // is object a service point?
        if (this.oid.tVersionId ==
            this.getTypeCache ().getTVersionId (TypeConstants.TC_ServicePoint))
                // 0x01010191 = tVersionId of ServicePoint
        {
            // if this element is a servicepoint no 'show' shall be called:
            // - it should show the create/change form
            group.addElement (new LinkElement (nameGroup,
                IOConstants.URL_JAVASCRIPT + "top.scripts.callOidFunction ('" +
                    this.oid + "', 'top.loadCont(" +
                    AppFunctions.FCT_OBJECTNEWFORM + ")');"));
        } // if
        // is object a reference to a service point?
        else if (this.isLink && this.linkedObjectId.tVersionId ==
            this.getTypeCache ().getTVersionId (TypeConstants.TC_ServicePoint))
            // 0x01010191 = tVersionId of ServicePoint
        {
            // if this element is a link to a servicepoint no 'show'
            // shall be called:
            // - it should show the create/change form
            group.addElement (new LinkElement (nameGroup,
                IOConstants.URL_JAVASCRIPT + "top.scripts.callOidFunction ('" +
                    this.linkedObjectId + "', 'top.loadCont(" +
                    AppFunctions.FCT_OBJECTNEWFORM + ")');"));
        } // else if
/*
         if (((masterSelect != BOConstants.FLG_HYPERMASTER)
*/
//
// END: SERVICEPOINT HACK
//
////////////////////////////////////////////////////////////
        else if (this.masterSelect != BOConstants.FLG_HYPERMASTER &&
                 this.masterSelect != BOConstants.FLG_FILEMASTER)

        { // no attachment defined
            if (this.isLink)  // object is a link to another object?
            {
                group.addElement (new LinkElement (nameGroup,
                    IOHelpers.getShowObjectJavaScriptUrl ("" + this.linkedObjectId)));
            } // if
            else  // object is no link
            {
                group.addElement (new LinkElement (nameGroup,
                    IOHelpers.getShowObjectJavaScriptUrl ("" + this.oid)));
            } // else object is no link
        } // end block no attachment defined
        else                            // an attachment is defined
        {
            if (this.isLink && this.isWeblink)                 // is a linkobject
            {
                // display master icon:
                group.addElement (new LinkElement (nameGroup,
                    IOConstants.URL_JAVASCRIPT +
                    "top.loadWeblink ('" + this.linkedObjectId + "');"));
            }   // is a linkobject
            else if (this.isLink)                 // is a linkobject
            {
                // display master icon:
                group.addElement (new LinkElement (nameGroup,
                    IOConstants.URL_JAVASCRIPT +
                    "top.loadMaster ('" + this.linkedObjectId + "');"));
            }   // is a linkobject
            else if (this.isWeblink)         // it is no linkobject, but a weblink
            {
                // display master icon:
                group.addElement (new LinkElement (nameGroup,
                    IOConstants.URL_JAVASCRIPT +
                    "top.loadWeblink ('" + this.oid + "');"));
            } // it is no linkobject
            else                        // it is no linkobject
            {
                // display master icon:
                group.addElement (new LinkElement (nameGroup,
                    IOConstants.URL_JAVASCRIPT +
                    "top.loadMaster ('" + this.oid + "');"));
            } // it is no linkobject
        } // else an attachment is defined

        // add infoicon and create a link
        // to the Info of a object
        if ((this.masterSelect == BOConstants.FLG_HYPERMASTER) ||
            (this.masterSelect == BOConstants.FLG_FILEMASTER))
        {
            ImageElement imgFile = new ImageElement (this.layoutpath +
                BOPathConstants.PATH_OBJECTICONS + "info.gif");
            group.addElement (new BlankElement ());
            group.addElement (new BlankElement ());

            if (this.isLink) // object is a link to another object?
            {
                group.addElement (new LinkElement (imgFile,
                    IOHelpers.getShowObjectJavaScriptUrl ("" + this.oid)));
            } // else object is no link
        } // if
        td = new TableDataElement (group);
        td.classId = classId;

//        td.nowrap = true;
        tr.addElement (td);

        return tr;                      // return the constructed row
    } // show


    /**************************************************************************
     * Get the type cache. <BR/>
     *
     * @return  The cache object.
     */
    protected TypeContainer getTypeCache ()
    {
        return ((ObjectPool) this.p_app.cache).getTypeContainer ();
    } // getTypeCache

} // class RefContainerElement_01
