/*
 * Class: LocaleContainer_01.java
 */

// package:
package ibs.obj.ml;

import ibs.app.AppConstants;
import ibs.bo.BOListConstants;
import ibs.bo.BOPathConstants;
import ibs.bo.BOTokens;
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.bo.type.TypeConstants;
import ibs.io.LayoutConstants;
import ibs.io.LayoutElement;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.user.UserContainerElement_01;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;


/******************************************************************************
 * This class represents one object of type LocaleContainer with version 01. <BR/>
 *
 * @version     $Id: LocaleContainer_01.java,v 1.5 2013/01/18 10:38:18 rburgermann Exp $
 *
 * @author      Bernhard Tatzmann 20100322
 ******************************************************************************
 */
public class LocaleContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: LocaleContainer_01.java,v 1.5 2013/01/18 10:38:18 rburgermann Exp $";

    /**
     * The OID of the referenced default layout. <BR/>
     */
    public OID p_defaultLocaleOid = null;



    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * This constructor creates a new instance of the class LocaleContainer_01.
     * <BR/>
     */
    public LocaleContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // LocaleContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    @Override
    public void initClassSpecifics ()
    {
        // set common attributes:
        this.elementClassName = "ibs.obj.ml.LocaleContainerElement_01";

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
        LocaleContainerElement_01 obj = (LocaleContainerElement_01) commonObj;
        // get common attributes:
        super.getContainerElementData (action, obj);

        obj.p_language = action.getString ("language");
        obj.p_country = action.getString ("country");
        
        obj.p_isDefault = action.getBoolean ("isDefault");
        // check if this is the default layout object:
        if (obj.p_isDefault)
        {
            this.p_defaultLocaleOid = obj.oid;
        } // if

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
                .append (" l.language, l.country, l.isDefault")
            .append (" FROM ").append (this.viewContent).append (" v,")
                .append (" (SELECT oid, language, country, isDefault FROM ibs_Locale_01) l")
//            .append (" WHERE   containerId = ").append (this.oid.toStringQu ());
            .append (" WHERE v.tVersionId = ")
                .append (this.getTypeCache ().getTVersionId (TypeConstants.TC_Locale))
                .append (" AND v.oid = l.oid");

        // return the computed query string:
        return queryStr.toString ();
    } // createQueryRetrieveContentData


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * locale container's content view. <BR/>
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
     * Set the headings and the orderings for this container. <BR/>
     * Overload this function if you need other headings as the standard
     * container. <BR/>
     */
    protected void setHeadingsAndOrderings ()
    {
        if (!this.getUserInfo ().userProfile.showExtendedAttributes)
        // if reduced list
        {
            // set headings:
            this.headings = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                BOListConstants.LST_HEADINGS_LOCALECONTAINERREDUCED, env);

            // set ordering attributes for the corresponding headings:
            this.orderings = BOListConstants.LST_ORDERINGS_LOCALECONTAINERREDUCED;
        } // if reduced list
        else
        // show extended attributes
        {
            // set headings:
            this.headings = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                BOListConstants.LST_HEADINGS_LOCALECONTAINER, env);

            // set ordering attributes for the corresponding headings:
            this.orderings = BOListConstants.LST_ORDERINGS_LOCALECONTAINER;
        } // else show extended attributes

        // ensure that there is an available ordering taken:
        this.ensureAvailableOrdering ();
    } // setHeadingsAndOrderings
    
    /**************************************************************************
     * Gets values of the buttons and returns the values as an aray of buttons. <BR/>
     *
     * @param   representationForm  Kind of representation.
     * @param   isContentView       Display the Info-View buttons or the Content-view buttons
     *
     * @return  An array which includes buttons
     */
    protected int[] buildButtonBar (int representationForm, boolean isContentView)
    {
        int[] buttons = super.buildButtonBar(representationForm, isContentView);
            
        for (int i = 0; i < buttons.length; i++)
        {
            // deactivate the list delete button
            if (buttons[i] == Buttons.BTN_LISTDELETE)
            {
                buttons[i] = Buttons.BTN_NONE;
            } // if
        } // for
        
        return buttons;
    } // buildButtonBar
} // class LocaleContainer_01
