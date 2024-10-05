/*
 * Class: LayoutContainer_01.java
 */

// package:
package ibs.obj.layout;

// imports:
//KR TODO: unsauber
import ibs.app.AppConstants;
import ibs.bo.BOConstants;
import ibs.bo.BOHelpers;
import ibs.bo.BOPathConstants;
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.bo.Operations;
import ibs.bo.type.TypeConstants;
import ibs.io.LayoutConstants;
import ibs.io.LayoutElement;
import ibs.obj.layout.LayoutContainerElement_01;
import ibs.service.user.User;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.util.NameAlreadyGivenException;
import ibs.util.NoAccessException;

import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * This class represents one object of type LayoutContainer with version 01. <BR/>
 *
 * @version     $Id: LayoutContainer_01.java,v 1.20 2013/01/18 10:38:16 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR), 980428
 ******************************************************************************
 */
public class LayoutContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: LayoutContainer_01.java,v 1.20 2013/01/18 10:38:16 rburgermann Exp $";

    /**
     * The OID of the referenced default layout. <BR/>
     */
    public OID p_defaultLayoutOid = null;



    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * This constructor creates a new instance of the class LayoutContainer_01.
     * <BR/>
     */
    public LayoutContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // LayoutContainer_01


    /**************************************************************************
     * Creates a LayoutContainer_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    @Deprecated
    public LayoutContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // LayoutContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    @Override
    public void initClassSpecifics ()
    {
        // set common attributes:
        this.elementClassName = "ibs.obj.layout.LayoutContainerElement_01";

        // set the instance's attributes:
    } // initClassSpecifics


    ///////////////////////////////////////////////////////////////////////////
    // database functions
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Get the element type specific data out of the actual tuple of the query
     * result. <BR/>
     * This metod is used to get all attributes of one element out of the
     * resultset. The attribute names which can be used are the ones which
     * are defined within the resultset of
     * <A HREF="#createQueryRetrieveContentData">createQueryRetrieveContentData</A>.
     * <BR/>
     * <B>Format:</B><BR/>
     * for oid properties:
     *      obj.&lt;property> = getQuOidValue (action, "&lt;attribute>"); <BR/>
     * for other properties:
     *      obj.&lt;property> = action.get&lt;type> ("&lt;attribute>"); <BR/>
     * The property <B>oid</B> is already gotten. This must not be done within this
     * method. <BR/>
     * This method can be overwritten in subclasses. <BR/>
     *
     * @param   action      The action for the database connection.
     * @param   commonObj   Object representing the list element.
     *
     * @exception   DBError
     *              Error when executing database statement.
     */
    @Override
    protected void getContainerElementData (SQLAction action,
                                            ContainerElement commonObj)
        throws DBError
    {
        // convert common element object to actual type:
        LayoutContainerElement_01 obj = (LayoutContainerElement_01) commonObj;
        // get common attributes:
        super.getContainerElementData (action, obj);

        obj.p_isDefault = action.getBoolean ("isDefault");
        // check if this is the default layout object:
        if (obj.p_isDefault)
        {
            this.p_defaultLayoutOid = obj.oid;
        } // if

        obj.path = BOPathConstants.PATH_LAYOUT + obj.name + "/";
        obj.frameset = AppConstants.FILE_FRAMESET;

        // get element type specific attributes:
        LayoutElement temp = new LayoutElement ();
        temp.styleSheet = AppConstants.CSS_BUTTONS;
        temp.images = BOPathConstants.PATH_IMAGE_BUTTONS;
        temp.javascript = AppConstants.JS_BUTTONS;

        obj.elems[LayoutConstants.BUTTONBAR] = temp;

        temp = new LayoutElement ();
        temp.styleSheet = AppConstants.CSS_TABS;
        temp.images = BOPathConstants.PATH_IMAGE_TABS;
        temp.javascript = AppConstants.JS_TABS;

        obj.elems[LayoutConstants.TABBAR] = temp;

        temp = new LayoutElement ();
        temp.styleSheet = AppConstants.CSS_SHEET;
        temp.images = BOPathConstants.PATH_IMAGE_SHEET;
        temp.javascript = AppConstants.JS_SHEET;

        obj.elems[LayoutConstants.SHEETINFO] = temp;

        temp = new LayoutElement ();
        temp.styleSheet = AppConstants.CSS_LIST;
        temp.images = BOPathConstants.PATH_IMAGE_LIST;
        temp.javascript = AppConstants.JS_LIST;

        obj.elems[LayoutConstants.SHEETLIST] = temp;

        temp = new LayoutElement ();
        temp.styleSheet = AppConstants.CSS_MENU;
        temp.images = BOPathConstants.PATH_IMAGE_MENU;
        temp.javascript = AppConstants.JS_MENU;

        obj.elems[LayoutConstants.MENUBAR] = temp;

        temp = new LayoutElement ();
        temp.styleSheet = AppConstants.CSS_NAVTABS;
        temp.images = BOPathConstants.PATH_IMAGE_TABS;
        temp.javascript = AppConstants.JS_NAVTABS;

        obj.elems[LayoutConstants.NAVBAR] = temp;

        temp = new LayoutElement ();
        temp.styleSheet = AppConstants.CSS_HEADER;
        temp.images = BOPathConstants.PATH_IMAGE_HEADER;
        temp.javascript = AppConstants.JS_HEADER;

        obj.elems[LayoutConstants.HEADER] = temp;

        temp = new LayoutElement ();
        temp.styleSheet = AppConstants.CSS_SHEETORDER;
        temp.images = BOPathConstants.PATH_IMAGE_SHEETORDER;
        temp.javascript = AppConstants.JS_SHEETORDER;

        obj.elems[LayoutConstants.SHEETORDER] = temp;

        temp = new LayoutElement ();
        temp.styleSheet = AppConstants.CSS_SHEETCOLLECTION;
        temp.images = BOPathConstants.PATH_IMAGE_SHEETCOLLECTION;
        temp.javascript = AppConstants.JS_SHEETCOLLECTION;

        obj.elems[LayoutConstants.SHEETCOLLECTION] = temp;

        temp = new LayoutElement ();
        temp.styleSheet = AppConstants.CSS_CALENDAR;
        temp.images = BOPathConstants.PATH_IMAGE_CALENDAR;
        temp.javascript = AppConstants.JS_CALENDAR;

        obj.elems[LayoutConstants.CALENDAR] = temp;

        temp = new LayoutElement ();
        temp.styleSheet = AppConstants.CSS_MESSAGE;
        temp.images = BOPathConstants.PATH_IMAGE_MESSAGE;
        temp.javascript = AppConstants.JS_MESSAGE;

        obj.elems[LayoutConstants.MESSAGE] = temp;

        temp = new LayoutElement ();
        temp.styleSheet = AppConstants.CSS_SHEETPRINT;
        temp.images = BOPathConstants.PATH_IMAGE_SHEETORDER;
        temp.javascript = AppConstants.JS_SHEETORDER;

        obj.elems[LayoutConstants.PRINTSHEET] = temp;

        temp = new LayoutElement ();
        temp.styleSheet = AppConstants.CSS_PRODUCTCATALOG;
        temp.images = BOPathConstants.PATH_IMAGE_PRODUCTCATALOG;
        temp.javascript = AppConstants.JS_PRODUCTCATALOG;

        obj.elems[LayoutConstants.PRODUCTCATALOG] = temp;

        temp = new LayoutElement ();
        temp.styleSheet = AppConstants.CSS_REFERENCES;
        temp.images = BOPathConstants.PATH_IMAGE_SHEET;
        temp.javascript = AppConstants.JS_SHEET;

        obj.elems[LayoutConstants.REFERENCES] = temp;

        temp = new LayoutElement ();
        temp.styleSheet = AppConstants.CSS_FOOTER;
        temp.images = BOPathConstants.PATH_IMAGE_SHEET;
        temp.javascript = null;

        obj.elems[LayoutConstants.FOOTER] = temp;
    } // getContainerElementData


    /**************************************************************************
     * Create the query to get the container's content out of the database.
     * <BR/>
     * The query or view must at least have the attributes userId and rights.
     * Queries on these attributes have to be addable to this query. <BR/>
     * The query must have at least one constraint within WHERE to ensure that
     * other constraints can be added with AND. <BR/>
     * <B>Standard format:</B><BR/>
     *      "SELECT DISTINCT oid, &lt;other attributes> " +
     *      " FROM " + this.viewContent +
     *      " WHERE containerId = " + this.oid;<BR/>
     * This method can be overwritten in subclasses. <BR/>
     * As you can see the query should contain at least the oid.
     *
     * @return  The constructed query.
     */
    @Override
    protected String createQueryRetrieveContentData ()
    {
        StringBuffer queryStr = null;

        queryStr = new StringBuffer ()
            .append ("SELECT DISTINCT v.oid, v.state, v.name, v.typeName,")
                .append (" v.typeCode, v.isLink, linkedObjectId,")
                .append (" v.owner, v.ownerName, v.ownerOid, v.ownerFullname,")
                .append (" v.lastChanged, v.isNew, v.icon, v.description,")
                .append (" l.isDefault")
            .append (" FROM ").append (this.viewContent).append (" v,")
                .append (" (SELECT oid, isDefault FROM ibs_Layout_01) l")
//            .append (" WHERE   containerId = ").append (this.oid.toStringQu ());
            .append (" WHERE v.tVersionId = ")
                .append (this.getTypeCache ().getTVersionId (TypeConstants.TC_Layout))
                .append (" AND v.oid = l.oid");

        // return the computed query string:
        return queryStr.toString ();
    } // createQueryRetrieveContentData


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * layout container's content view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  An array with button ids that can potentially be displayed.
     */
    @Override
    protected int[] setContentButtons ()
    {
        // define buttons to be displayed:
        int[] buttons =
        {
            Buttons.BTN_NEW,
            Buttons.BTN_PASTE,
            Buttons.BTN_SEARCH,
//            Buttons.BTN_HELP,
            Buttons.BTN_LISTDELETE,
            Buttons.BTN_REFERENCE,
        }; // buttons

        // return button array:
        return buttons;
    } // setContentButtons


    /***************************************************************************
     * Set the default layout for the container. <BR/>
     *
     * @param   defaultLayout   Layout to be used as default layout.
     */
    protected void setDefaultLayout (Layout_01 defaultLayout)
    {
        Vector<OID> defaultLayoutOids = new Vector<OID> ();
                                        // the oids of all default layouts

        try
        {
            // get all the layouts:
            this.retrieveContent (Operations.OP_NONE, 0, BOConstants.ORDER_ASC);
        } // try
        catch (NoAccessException e)
        {
            // cannot occur
        } // catch

        // get all layouts which are defined as default layout and shall not be
        // default layouts:
        for (Iterator<ContainerElement> iter = this.elements.iterator (); iter.hasNext ();)
        {
            LayoutContainerElement_01 elem = (LayoutContainerElement_01) iter
                .next ();

            // check if the actual layout is defined as default layout:
            // (don't hesitate if it is the proposed default layout)
            if (elem.p_isDefault && !elem.oid.equals (defaultLayout.oid))
            {
                // remember the default layout:
                defaultLayoutOids.add (elem.oid);
            } // if
        } // for iter

        // now check the number of default layouts:
        if (defaultLayoutOids.size () > 0) // wrong number of default layouts?
        {
            // drop the default flag of all default layouts:
            this.setNoDefault (defaultLayoutOids);
        } // if wrong number of default layouts

        // set the default layout:
        defaultLayout.setDefault (true);
    } // setDefaultLayout


    /***************************************************************************
     * Ensure that no layout out of the vector is defined as default layout.
     * <BR/>
     *
     * @param   layoutOids  The oids of the layouts to be handled.
     */
    protected void setNoDefault (Vector<OID> layoutOids)
    {
        Layout_01 obj = null;           // the actual layout object

        // loop through all layouts, get the objects, set the isDefault flag
        // and store the layout:
        for (Iterator<OID> iter = layoutOids.iterator (); iter.hasNext ();)
        {
            OID oid = iter.next ();

            // get the layout object:
            obj = (Layout_01) BOHelpers.getObject (
                oid, this.env, false, false, false);

            // check if we got the object:
            if (obj != null)
            {
                // set the isDefault value:
                obj.setDefault (false);
                try
                {
                    // store the object:
                    obj.performChange (Operations.OP_NONE);
                } // try
                catch (NoAccessException e)
                {
                    // cannot occur
                } // catch
                catch (NameAlreadyGivenException e)
                {
                    // cannot occur
                } // catch
            } // if
        } // for iter
    } // setNoDefault

} // class LayoutContainer_01
