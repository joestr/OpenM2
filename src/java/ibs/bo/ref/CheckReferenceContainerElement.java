/*
 * Class: CheckReferenceContainerElement.java
 */
/*
 * Created by IntelliJ IDEA.
 * User: kreimueller
 * Date: Dec 18, 2001
 * Time: 3:10:04 PM
 */

// package:
package ibs.bo.ref;

// imports:
import ibs.bo.ContainerElement;
import ibs.io.session.SessionInfo;
import ibs.tech.html.RowElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TextElement;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;


/******************************************************************************
 * This class .... <BR/>
 *
 * @version     $Id: CheckReferenceContainerElement.java,v 1.8 2007/07/31 19:13:52 kreimueller Exp $
 *
 * @author      kreimueller, 011218
 ******************************************************************************
 */
public class CheckReferenceContainerElement extends ContainerElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: CheckReferenceContainerElement.java,v 1.8 2007/07/31 19:13:52 kreimueller Exp $";


    /**
     * Number of references for the specific object. <BR/>
     * This are the number of references which are pointing to the object which
     * is represented by this container element.
     */
    private int p_refCount = 0;


    /**************************************************************************
     * Get the element type specific data out of the actual tuple of the query
     * result. <BR/>
     * This metod is used to get all attributes of one element out of the
     * resultset. The attribute names which can be used are the ones which
     * are defined within the resultset of
     * <CODE>createQueryRetrieveContentData</CODE>. <BR/>
     *
     * <B>Format:</B>. <BR/>
     * for oid properties:
     *      obj.&lt;property> = getQuOidValue (action, "&lt;attribute>");. <BR/>
     * for other properties:
     *      obj.&lt;property> = action.get&lt;type> ("&lt;attribute>");. <BR/>
     * The property <B>oid</B> is already gotten. This must not be done within this
     * method. <BR/>
     * This method can be overwritten in subclasses. <BR/>
     *
     * @param   action  The database connection object.
     * @param   sess    The actual session info object.
     *
     * @exception   DBError
     *              Error when executing database statement.
     */
    protected void getData (SQLAction action, SessionInfo sess)
        throws DBError
    {
        // get the data out of the query result:
        this.oid = SQLHelpers.getQuOidValue (action, "oid");
        this.name = action.getString ("name");
        this.icon = action.getString ("icon");
        this.isLink = action.getBoolean ("isLink");
        this.linkedObjectId = SQLHelpers.getQuOidValue (action, "linkedObjectId");
        this.p_refCount = action.getInt ("refCount");

        // ensure that the element is checked only if there are no references:
        this.checked = this.p_refCount == 0;

        // set the layout directory:
        if ((sess != null) && (sess.activeLayout != null))
        {
            this.layoutpath = sess.activeLayout.path;
        } // if
        else
        {
            this.layoutpath = "";
        } // else
    } // getData


    /**************************************************************************
     * Get the number of references for this element. <BR/>
     *
     * @return  The number of references for this element, <CODE>0</CODE> if
     *          there are no references.
     */
    protected int getRefCount ()
    {
        // get the value and return it:
        return this.p_refCount;
    } // getRefCount


    /**************************************************************************
     * Represent the type specific attributes to the user. <BR/>
     *
     * @param   row     The table row where the attributes shall be inserted.
     * @param   classId Class for the stylesheet.
     */
    public void showSpecificAttributes (RowElement row, String classId)
    {
        TextElement text = null;        // the text
        TableDataElement td = null;     // a table cell

        // refCount:
        text = new TextElement (String.valueOf (this.p_refCount));
        td = new TableDataElement (text);
        td.classId = classId;
        row.addElement (td);
    } // showSpecificAttributes


    /**************************************************************************
     * Represent the extended attributes to the user. <BR/>
     *
     * @param   row     The table row where the attributes shall be inserted.
     * @param   classId Class for the stylesheet.
     */
    public void showExtendedAttributes (RowElement row, String classId)
    {
        // there are no extended attributes
    } // showExtendedAttributes


    /**************************************************************************
     * Represent this object to the user. <BR/>
     *
     * @param   classId             class for the stylesheet.
     *
     * @return  The constructed table row.
     */
/*
    public RowElement show (String classId)
    {
        TextElement text = null;
        RowElement tr;
        TableDataElement td = null;
        String altText = "";            // text to be shown when the mouse
                                        // pointer stays over the object
        GroupElement group;             // group of layout elements

        // create the table row:
        tr = new RowElement (this.p_width);

        // set class id for this element
        tr.classId = classId;

        // create new group element
        group = new GroupElement ();    // create new group element
        group.addElement (new BlankElement ()); // show blank space

        // create td element:
        td = new TableDataElement (group);
        td.width = AppConstants.LST_NEWCOLWIDTH;
        td.classId = classId;
        tr.addElement (td);

        // create icon element
        ImageElement img =
            new ImageElement
                (this.layoutpath + AppConstants.PATH_OBJECTICONS + this.icon);

        // create new group element: name
        GroupElement nameGroup = new GroupElement ();
        nameGroup.addElement (img);
        nameGroup.addElement (new BlankElement ());
        if (this.isLink)                // object is a link to another object?
        {
            img = new ImageElement (this.layoutpath + AppConstants.PATH_OBJECTICONS + "Referenz.gif");
            nameGroup.addElement (img);
        } // if object is a link to another object
        nameGroup.addElement (new TextElement (this.name));


        // create new group element: javascript behind the name!
        // distinguish between different types
        group = new GroupElement ();
////////////////////////////////////////////////////////////
//
// START: SERVICEPOINT HACK
//
        // is object a service point?
        if ((this.oid.tVersionId == 0x01010191)) // = TVersionId of ServicePoint
        {
            // if this element is a servicepoint no 'show' shall be called:
            // - it should show the create/change form
            group.addElement (new LinkElement (nameGroup,
                IOConstants.URL_JAVASCRIPT +
                "top.scripts.callOidFunction ('"
                + this.oid + "', 'top.loadCont("
                + AppFunctions.FCT_OBJECTNEWFORM + ")');"));
        } // if
        // is object a reference to a service point?
        else if (this.isLink && this.linkedObjectId.tVersionId == 0x01010191)
            // = TVersionId of ServicePoint
        {
            // if this element is a link to a servicepoint no 'show'
            // shall be called:
            // - it should show the create/change form
            group.addElement (new LinkElement (nameGroup,
                IOConstants.URL_JAVASCRIPT +
                "top.scripts.callOidFunction ('"
                + this.linkedObjectId + "', 'top.loadCont("
                + AppFunctions.FCT_OBJECTNEWFORM + ")');"));
        } // else if
/ *
         if (((masterSelect != BOConstants.FLG_HYPERMASTER)
* /
//
// END: SERVICEPOINT HACK
//
////////////////////////////////////////////////////////////
        else if ((this.masterSelect != MAS_HYPERLINK)
          && (this.masterSelect != MAS_DOKUMENT))
                                        // no attachment defined?
        {
            if (this.isLink)            // object is a link to another object?
            {
                group.addElement (new LinkElement (nameGroup,
                    IOHelpers.getShowObjectUrl ("" + this.linkedObjectId)));
            }
            else                        // object is no link
            {
                group.addElement (new LinkElement (nameGroup,
                    IOHelpers.getShowObjectUrl ("" + this.oid)));
            } // else object is no link
        } // end no attachment defined
        else                            // a attachment is defined
        {
            if (this.isLink && this.isWeblink) // is a linkobject
            {
                // display master icon:
                group.addElement (new LinkElement (nameGroup,
                     IOConstants.URL_JAVASCRIPT +
                     "top.loadWeblink ('" + this.linkedObjectId + "');"));
            }   // is a linkobject
            else if (this.isLink)       // is a linkobject
            {
                // display master icon:
                group.addElement (new LinkElement (nameGroup,
                     IOConstants.URL_JAVASCRIPT +
                     "top.loadMaster ('" + this.linkedObjectId + "');"));
            }   // is a linkobject
            else if (this.isWeblink)    // it is no linkobject, but a weblink
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
         } // end of a attachment is defined


        // add infoicon and create a link
        // to the Info of a object
        if ((this.masterSelect == MAS_HYPERLINK) ||
            (this.masterSelect == MAS_DOKUMENT)
////////////////////////////////////////////////////////////
// START: SERVICEPOINT HACK
            || (this.oid.tVersionId == 0x01010191)
// END: SERVICEPOINT HACK
////////////////////////////////////////////////////////////
         )
        {
            ImageElement imgFile =
                new ImageElement (this.layoutpath + AppConstants.PATH_OBJECTICONS + "info.gif");

            group.addElement (new BlankElement ());
            group.addElement (new BlankElement ());

            if (this.isLink)            // object is a link to another object?
            {
                group.addElement (new LinkElement (imgFile,
                    IOHelpers.getShowObjectUrl ("" + this.linkedObjectId)));
            }
            else  // object is no link
            {
                group.addElement (new LinkElement (imgFile,
                    IOHelpers.getShowObjectUrl ("" + this.oid)));
            } // else object is no link
        } // if

        // create new group element
        td = new TableDataElement (group);
        td.classId = classId;
        tr.addElement (td);

        // refCount:
        text = new TextElement (String.valueOf (this.p_refCount));
        td = new TableDataElement (text);
        td.classId = classId;
        tr.addElement (td);

        return (tr);                    // return the constructed row
    } // show
*/

} // class CheckReferenceContainerElement
