/*
 * Class: ContainerElement.java
 */

// package:
package ibs.bo;

// imports:
import ibs.BaseObject;
import ibs.app.AppFunctions;
import ibs.io.Environment;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.session.ApplicationInfo;
import ibs.service.user.User;
import ibs.tech.html.BlankElement;
import ibs.tech.html.GroupElement;
import ibs.tech.html.ImageElement;
import ibs.tech.html.InputElement;
import ibs.tech.html.LinkElement;
import ibs.tech.html.RowElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TextElement;
import ibs.util.DateTimeHelpers;

import java.util.Date;


/******************************************************************************
 * This class represents all necessary properties of one element of a
 * Container. <BR/>
 *
 * @version     $Id: ContainerElement.java,v 1.27 2013/01/18 10:38:17 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR), 980401
 *
 * @see         ibs.bo.Container
 ******************************************************************************
 */
public class ContainerElement extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ContainerElement.java,v 1.27 2013/01/18 10:38:17 rburgermann Exp $";


    /**
     * Type of error for this class. <BR/>
     * This String is the name of an error which occurs within this class.
     */
    public String ERR_TYPE = this.getClass ().getName () + " Error";

    /**
     * The system wide unique id of the referenced object. <BR/>
     */
    public OID oid = null;

    /**
     * State of the element. <BR/>
     * The value of this property is one of the ST_* values defined in
     * <A HREF="#UtilConstants.html">Constants</A>.
     */
    public int state = States.ST_UNKNOWN;

    /**
     * Name of the object. <BR/>
     */
    public String name = null;

    /**
     * code of the type where the object belongs to. <BR/>
     */
    public String typeCode = null;

    /**
     * name of the type where the object belongs to. <BR/>
     */
    public String typeName = null;

    /**
     * Defines if this object is a link. <BR/>
     * Contains true, if the object is a link, false otherwise.
     */
    public boolean isLink = false;

    /**
     * The id of the object, which this is a link to. <BR/>
     * This property is null, if link is false, otherwise it contains the oid
     * of the object where the link shows to.
     */
    public OID linkedObjectId = null;

    /**
     * User who is the owner of this object. <BR/>
     */
    public User owner = null;

    /**
     * User who is the owner of this object. <BR/>
     */
    public User creator = null;

    /**
     * User who is the owner of this object. <BR/>
     */
    public User changer = null;

    /**
     * Date of last change. <BR/>
     */
    public Date lastChanged = null;

    /**
     * Date of creation. <BR/>
     */
    public Date creationDate = null;

    /**
     * Date of creation. <BR/>
     */
    public Date validUntil = null;

    /**
     * Tells if the object is new to the user. <BR/>
     */
    public boolean isNew = false;

    /**
     * URL of icon. <BR/>
     */
    public String icon = null;

    /**
     * The container oid. <BR/>
     */
    public OID p_containerOid = null;

    /**
     * The level of the object within the tree. <BR/>
     */
    public int p_oLevel = 0;

    /**
     * current state of the process wich is performed with this object
     */
    public String processState = null;

    /**
     * Description of this object. <BR/>
     */
    // TODO RB: Call
    //          MultilingualTextProvider.getMessage(BOMessages.MSG_BUNDLE,
    //              BOMessages.ML_MSG_SHOWOBJECT, new String[] {notFoundTypes.toString ()}, env)
    //          to get the text in the correct language
    public String description = BOMessages.ML_MSG_SHOWOBJECT;

    /**
     * Flag to display 'info' - Button. <BR/>
     */
    public  int masterSelect = 0;

    /**
     * Show the File in a new window. <BR/>
     */
    public  boolean showInWindow = false;


    /**
     * A kind of attachment. <BR/>
     */
    public static final short MAS_DOKUMENT = 1;

     /**
     * A kind of attachment. <BR/>
     */
    public static final short MAS_HYPERLINK = 2;

     /**
     * Flag to indicate if changed date and creator info should
     * be shown in the list. <BR/>
     */
    public boolean showExtendedAttributes = true;

    /**
     * Flag to indicate if checkbox ( if element is element of selectionlist)
     * is checked or not.
     */
    public boolean checked = false;

    /**
     * bgColor for row (only for IE3.02)
     */
    public String bgColor = null;

    /**
     * Actual layout path
     */
    public String layoutpath = "";

    /**
     * the object is a weblink
     */
    public boolean isWeblink = false;

    /**
     * The width, i.e. the number of columns. <BR/>
     * This property contains the number of columns to be represented within
     * each table row. <BR/>
     * Default: <CODE>0</CODE>
     */
    private int p_width = 0;

    /**
     * the object is a virtual object. <BR/>
     */
    public boolean p_isVirtual = false;

    /**
     * The ID of a virtual object. <BR/>
     */
    public String p_virtualId = null;


    /**************************************************************************
     * Creates a ContainerElement object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     * {@link #oid oid} is initialized to <CODE>null</CODE>. <BR/>
     * {@link #state state} is initialized to
     * {@link ibs.bo.States#ST_UNKNOWN ST_UNKNOWN}. <BR/>
     * {@link #name name} is initialized to <CODE>null</CODE>. <BR/>
     * {@link #typeCode typeCode} is initialized to <CODE>null</CODE>. <BR/>
     * {@link #typeName typeName} is initialized to <CODE>null</CODE>. <BR/>
     * {@link #isLink isLink} is initialized to false. <BR/>
     * {@link #linkedObjectId linkedObjectId} is initialized to <CODE>null</CODE>. <BR/>
     * {@link #owner owner} is initialized to <CODE>null</CODE>. <BR/>
     * {@link #lastChanged lastChanged} is initialized to actual Date. <BR/>
     * {@link #icon icon} is initialized to <CODE>"icon.gif"</CODE>. <BR/>
     */
    public ContainerElement ()
    {
        // call constructor of super class:
        super ();

        // initialize the instance's private properties:

        // initialize the instance's public properties:
        this.oid = null;
        this.state = States.ST_UNKNOWN;
        this.name = null;
        this.typeCode = null;
        this.typeName = null;
        this.isLink = false;
        this.linkedObjectId = null;
        this.owner = null;
        this.lastChanged = new Date ();
        this.icon = "icon.gif";
        this.bgColor = null;
        this.layoutpath = "";
    } // ContainerElement


    /**************************************************************************
     * Creates a ContainerElement object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     * The compound object id is stored in the {@link #oid oid} property
     * of this object. <BR/>
     * The user object is also stored in a specific
     * property of this object to make sure that the user's context can be used
     * for getting his/her rights. <BR/>
     * {@link #state state} is initialized to
     * {@link ibs.bo.States#ST_UNKNOWN States.ST_UNKNOWN}. <BR/>
     * {@link #name name} is initialized to <CODE>null</CODE>. <BR/>
     * {@link #typeCode typeCode} is initialized to <CODE>null</CODE>. <BR/>
     * {@link #typeName typeName} is initialized to <CODE>null</CODE>. <BR/>
     * {@link #isLink isLink} is initialized to false. <BR/>
     * {@link #linkedObjectId linkedObjectId} is initialized to <CODE>null</CODE>. <BR/>
     * {@link #owner owner} is initialized to <CODE>null</CODE>. <BR/>
     * {@link #lastChanged lastChanged} is initialized to actual Date. <BR/>
     * {@link #icon icon} is initialized to <CODE>"icon.gif"</CODE>. <BR/>
     *
     * @param   oid     Value for the compound object id.
     */
    public ContainerElement (OID oid)
    {
        // call constructor of super class:
        super ();

        // initialize the instance's private properties:

        // set the instance's public properties:
        this.oid = oid;                 // set the object id
        this.state = States.ST_UNKNOWN;
        this.name = null;
        this.typeCode = null;
        this.typeName = null;
        this.isLink = false;
        this.linkedObjectId = null;
        this.owner = null;
        this.lastChanged = new Date ();
        this.icon = "icon.gif";
        this.bgColor = null;
        this.layoutpath = "";
    } // ContainerElement


    /**************************************************************************
     * Represent this object to the user. <BR/>
     * (for IE3.02)
     *
     * @param   representationForm  Kind of representation.
     * @param   classId             class for the stylesheet.
     * @param   bgColor             bgColor for tablerow (for IE3.02).
     * @param   env                 The current environment
     *
     * @return  The table row which represents this object.
     */
    public RowElement showSelection (int representationForm, String bgColor,
                                     String classId, Environment env)
    {
        this.bgColor = bgColor;
        return this.showSelection (classId, env);
    } // showSelection


    /**************************************************************************
     * Represent the selectionList to the user. <BR/>
     *
     * @param   classId             class for the styleSheet
     * @param   env                 The current environment 
     *
     * @return  The table row which represents this object.
     */
    public RowElement showSelection (String classId, Environment env)
    {
        RowElement tr;
        TableDataElement td = null;
        InputElement iE = null;

        tr = this.show (classId, env);
        // check if this is a virtual object
        if (this.p_isVirtual)
        {
            iE = new InputElement (BOArguments.ARG_VIRTUALOBJECT,
                    InputElement.INP_CHECKBOX, this.p_virtualId);
        } // if (this.p_isVirtual)
        else    // physical object
        {
            iE = new InputElement (BOArguments.ARG_DELLIST,
                InputElement.INP_CHECKBOX, "" + this.oid);
        } // else physical object
        iE.checked = this.checked;

        td = new TableDataElement (iE);

        td.width = BOListConstants.LST_NEWCOLWIDTH;

        // replace the first table column with the new element:
        tr.replaceElement (td, 0);
        return tr;                      // return the constructed row
    } // showSelection


    /**************************************************************************
     * Set the number of columns. <BR/>
     */
    public void setWidth ()
    {
        // extended attributes enabled?
        if (this.showExtendedAttributes)
        {
            this.setWidth (BOListConstants.LST_HEADINGS);
        } // if showExtendedAttributes
        else
        {
            this.setWidth (BOListConstants.LST_HEADINGS_REDUCED);
        } // else
    } // setWidth


    /**************************************************************************
     * Set the number of columns depending on the headings to be displayed. <BR/>
     *
     * @param   headings    The headings to be displayed.
     */
    public void setWidth (String[] headings)
    {
        // set the width:
        // take into account that there is an isNew column
        this.p_width = headings.length + 1 + this.getSpecificWidth ();
    } // setWidth


    /**************************************************************************
     * Add the spcific number of columns to the other columns. <BR/>
     *
     * @return  The number of type specific columns.
     */
    public int getSpecificWidth ()
    {
        // this method may be overwritten in sub classes
        return 0;
    } // getSpecificWidth


    /**************************************************************************
     * Get the name group for this element. <BR/>
     *
     * @return  The constructed name group.
     */
    public GroupElement getNameGroup ()
    {
/*
        String altText = "";            // text to be shown when the mouse
                                        // pointer stays over the object
*/
        GroupElement group = null;      // group of layout elements
        ImageElement img = null;        // an image
        GroupElement nameGroup = null;  // the name group

// - ! KR 981026 temporary dropped ... /////////////////////////////////////
/*
        // set text to be shown on mouse over:
        if (description != null && !description.length () == 0) // description set?
            altText = description.trim (); // use description without spaces
*/
// - ! ... KR 981026 temporary dropped /////////////////////////////////////

        // create icon element:
        if (this.icon == null)          // no icon provided?
        {
            this.icon = this.typeName + ".gif"; // get icon from type name
        } // if no icon provided
        img = new ImageElement (this.layoutpath +
            BOPathConstants.PATH_OBJECTICONS + this.icon);

        // create the name group:
        nameGroup = new GroupElement ();
        nameGroup.addElement (img);
        nameGroup.addElement (new BlankElement ());
        if (this.isLink)                // object is a link to another object?
        {
            img = new ImageElement (this.layoutpath +
                BOPathConstants.PATH_OBJECTICONS + "Referenz.gif");
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
        if (this.oid.tVersionId == 0x01010191) // = TVersionId of ServicePoint
        {
            // if this element is a servicepoint no 'show' shall be called:
            // - it should show the create/change form
            group.addElement (new LinkElement (nameGroup,
                IOConstants.URL_JAVASCRIPT +
                "top.scripts.callOidFunction ('" + this.oid +
                    "', 'top.loadCont (" + AppFunctions.FCT_OBJECTNEWFORM +
                    ")');"));
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
                "top.scripts.callOidFunction ('" +
                    this.linkedObjectId + "', 'top.loadCont (" +
                    AppFunctions.FCT_OBJECTNEWFORM + ")');"));
        } // else if
/*
         if (((masterSelect != BOConstants.FLG_HYPERMASTER)
*/
//
// END: SERVICEPOINT HACK
//
////////////////////////////////////////////////////////////
        else if ((this.masterSelect != ContainerElement.MAS_HYPERLINK) &&
            (this.masterSelect != ContainerElement.MAS_DOKUMENT))
                                        // no attachment defined?
        {
            if (this.isLink)            // object is a link to another object?
            {
                group.addElement (new LinkElement (nameGroup,
                    IOHelpers.getShowObjectJavaScriptUrl ("" + this.linkedObjectId)));
            } // if object is a link to another object
            else                        // object is no link
            {
                // check if the object is a virtual object
                // in that case to link will be generated
                if (this.p_isVirtual)
                {
                    group.addElement (nameGroup);
                } // if
                else
                {
                    group.addElement (new LinkElement (nameGroup,
                        IOHelpers.getShowObjectJavaScriptUrl ("" + this.oid)));
                } // else
            } // else object is no link
        } // if no attachment defined
        else                            // an attachment is defined
        {
            if (this.isLink && this.isWeblink) // is a link object
            {
                // display master icon:
                group.addElement (new LinkElement (nameGroup,
                    IOConstants.URL_JAVASCRIPT +
                    "top.loadWeblink ('" + this.linkedObjectId + "');"));
            } // if is a link object
            else if (this.isLink)       // is a link object
            {
                // display master icon:
                group.addElement (new LinkElement (nameGroup,
                    IOConstants.URL_JAVASCRIPT +
                    "top.loadMaster ('" + this.linkedObjectId + "');"));
            } // else if is a link object
            else if (this.isWeblink)    // it is no link object, but a weblink
            {
                // display master icon:
                group.addElement (new LinkElement (nameGroup,
                    IOConstants.URL_JAVASCRIPT +
                    "top.loadWeblink ('" + this.oid + "');"));
            } // else if it is no link object, but a weblink
            else                        // it is no link object
            {
                // display master icon:
                group.addElement (new LinkElement (nameGroup,
                    IOConstants.URL_JAVASCRIPT +
                    "top.loadMaster ('" + this.oid + "');"));
            } // else it is no link object
        } // else an attachment is defined


        // add infoicon and create a link
        // to the Info of a object
        if ((this.masterSelect == ContainerElement.MAS_HYPERLINK) ||
            (this.masterSelect == ContainerElement.MAS_DOKUMENT)
////////////////////////////////////////////////////////////
// START: SERVICEPOINT HACK
            ||
            (this.oid.tVersionId == 0x01010191)
//                getCache ().getTversionId (TypeConstants.TC_Servicepoint))
            // 0x01010191)
// END: SERVICEPOINT HACK
////////////////////////////////////////////////////////////
        )
        {
            img = new ImageElement (this.layoutpath + BOPathConstants.PATH_OBJECTICONS + "info.gif");

            group.addElement (new BlankElement ());
            group.addElement (new BlankElement ());

            if (this.isLink)            // object is a link to another object?
            {
                group.addElement (new LinkElement (img,
                    IOHelpers.getShowObjectJavaScriptUrl ("" + this.linkedObjectId)));
            } // if object is a link to another object
            else                        // object is no link
            {
                group.addElement (new LinkElement (img,
                    IOHelpers.getShowObjectJavaScriptUrl ("" + this.oid)));
            } // else object is no link
        } // if

        // return the constructed group element:
        return group;
    } // getNameGroup


    /**************************************************************************
     * Represent the type specific attributes to the user. <BR/>
     *
     * @param   row     The table row where the attributes shall be inserted.
     * @param   classId Class for the stylesheet.
     */
    public void showSpecificAttributes (RowElement row, String classId)
    {
        // this method may be overwritten in sub classes
    } // showSpecificAttributes


    /**************************************************************************
     * Represent the extended attributes to the user. <BR/>
     *
     * @param   row     The table row where the attributes shall be inserted.
     * @param   classId Class for the stylesheet.
     */
    public void showExtendedAttributes (RowElement row, String classId)
    {
        TextElement text = null;        // a text element
        TableDataElement td = null;     // the current table cell
        GroupElement group;             // group of layout elements

        // type of the object:
        group = new GroupElement ();
        text = new TextElement (this.typeName);
        group.addElement (text);
        td = new TableDataElement (group);
        td.classId = classId;
        row.addElement (td);

        // owner
        text = new TextElement ("" + this.owner);
        td = new TableDataElement (text);
        td.classId = classId;
        row.addElement (td);

        // date/time
        text = new TextElement (DateTimeHelpers.dateTimeToString (this.lastChanged));
        td = new TableDataElement (text);
        td.classId = classId;
        td.nowrap = true;
        td.alignment = IOConstants.ALIGN_RIGHT;
        row.addElement (td);
    } // showExtendedAttributes


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
                                        // pointer stays over the object
        GroupElement group;             // group of layout elements

        // ensure that the width is set:
        if (this.p_width == 0)          // width not yet set?
        {
            // set the width:
            this.setWidth ();
        } // if width not yet set

        // create the table row:
        tr = new RowElement (this.p_width);

        // set class id for this element
        tr.classId = classId;

        // create isNew group element
        group = new GroupElement ();    // create new group element
        if (this.isNew)                 // the element is new?
        {
            // show "new" icon:
            group.addElement (new ImageElement (BOPathConstants.PATH_GLOBAL + "new.gif"));
        } // if the element is new
        else                            // the element is not new
        {
            group.addElement (new BlankElement ()); // show blank space
        } // else the element is not new

        // create td element:
        td = new TableDataElement (group);
        td.width = BOListConstants.LST_NEWCOLWIDTH;
        td.classId = classId;
        tr.addElement (td);

        // create the name group:
        td = new TableDataElement (this.getNameGroup ());
        td.classId = classId;
        tr.addElement (td);

        if (this.showExtendedAttributes)
        {
            // show the extended attributes:
            this.showExtendedAttributes (tr, classId);
        } // if (this.showExtendedAttributes)

        // display the type specific attributes:
        this.showSpecificAttributes (tr, classId);
        
        return tr;                      // return the constructed row
    } // show


    /**************************************************************************
     * Check if the element is exportable. <BR/>
     *
     * @param   appInfo The application info object which can be used to get the
     *                  type information.
     *
     * @return  <CODE>true</CODE> if the object is exportable,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean isExportable (ApplicationInfo appInfo)
    {
        // almost all objects are exportable:
        return true;
    } // isExportable

} // class ContainerElement
