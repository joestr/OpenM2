/*
 * Class: Discussion_01.java
 */

// package:
package m2.bbd;

// imports:
import ibs.app.AppFunctions;
import ibs.app.CssConstants;
import ibs.bo.BOArguments;
import ibs.bo.BOConstants;
import ibs.bo.BOListConstants;
import ibs.bo.BOMessages;
import ibs.bo.BOPathConstants;
import ibs.bo.BOTokens;
import ibs.bo.Buttons;
import ibs.bo.ContainerElement;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.bo.Operations;
import ibs.di.DataElement;
import ibs.io.HtmlConstants;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.LayoutConstants;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.BlankElement;
import ibs.tech.html.BuildException;
import ibs.tech.html.DiscElement;
import ibs.tech.html.Element;
import ibs.tech.html.Font;
import ibs.tech.html.GroupElement;
import ibs.tech.html.IE302;
import ibs.tech.html.ImageElement;
import ibs.tech.html.Page;
import ibs.tech.html.RowElement;
import ibs.tech.html.ScriptElement;
import ibs.tech.html.StyleSheetElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TableElement;
import ibs.tech.html.TextElement;
import ibs.tech.http.HttpArguments;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;
import ibs.util.DateTimeHelpers;
import ibs.util.FormFieldRestriction;
import ibs.util.NoAccessException;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;

import m2.bbd.BbdArguments;
import m2.bbd.BbdConstants;
import m2.bbd.BbdFunctions;
import m2.bbd.BbdMessages;
import m2.bbd.BbdTokens;
import m2.bbd.BbdTypeConstants;
import m2.bbd.BlackBoard_01;
import m2.bbd.DiscussionElement_01;
import m2.store.CatalogContainer_01;
import m2.store.StoreTokens;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * This class represents one object of type Discussion_01 with version 01. <BR/>
 *
 * @version     $Id: Discussion_01.java,v 1.49 2010/05/20 07:59:00 btatzmann Exp $
 *
 * @author      Keim Christine (CK), 980515
 ******************************************************************************
 */
public class Discussion_01 extends BlackBoard_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Discussion_01.java,v 1.49 2010/05/20 07:59:00 btatzmann Exp $";

    
    /**
     * Headings of columns. <BR/>
     * These headings are shown at the top of lists.
     */
    public static final String [] LST_HEADINGS_DISCUSSION =
    {
        BOTokens.ML_NAME,
        BOTokens.ML_OWNER,
        BOTokens.ML_CHANGED,
    }; // LST_HEADINGS_DISCUSSION

    /**
     * Name of a container column. <BR/>
     * These attributes are used for ordering the elements.
     */
    public static final String [] LST_ORDERINGS_DISCUSSION =
    {
        "sortPath, name",
        null,
        null,
    }; // LST_ORDERINGS_DISCUSSION

    /**
     * how many entries has the discussion. <BR/>
     */
    public int entries = 0;

    /**
     * How many new entries (for the user) has the discussion. <BR/>
     */
    public int newEntries = 0;

    /**
     * the view used for the deletion in a list. <BR/>
     */
    public String delContent = null;

    /**
     * Discussion icon: element open. <BR/>
     */
    private static final String ICON_DISC_OPEN = "disc_open.gif";
    /**
     * Discussion icon: element closed. <BR/>
     */
    private static final String ICON_DISC_CLOSED = "disc_closed.gif";
    /**
     * Discussion icon: element disabled. <BR/>
     */
    private static final String ICON_DISC_DISABLED = "disc_disabled.gif";


    /**************************************************************************
     * This constructor creates a new instance of the class Discussion_01.
     * <BR/>
     */
    public Discussion_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // Discussion_01



    /**************************************************************************
     * Creates a Discussion_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @see     ibs.bo.BusinessObject
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public Discussion_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // Discussion_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        super.initClassSpecifics ();

        // set the instance's attributes:
        this.discType = BbdTypeConstants.TC_Discussion;

        // set the class-procedureNames
        this.procCreate   = "p_Discussion_01$create";
        this.procChange   = "p_Discussion_01$change";
        this.procRetrieve = "p_Discussion_01$retrieve";
        this.procDelete   = "p_Discussion_01$delete";

        this.msgBundleContainerEmpty = BbdMessages.MSG_BUNDLE;
        this.msgContainerEmpty = BbdMessages.ML_MSG_DISCUSSIONEMPTY;
//--DJ
        // this class is instanced for contaienrelements which
        // are shown in containercontent
        this.elementClassName = "m2.bbd.DiscussionElement_01";

        // show content as frameset, first frame for themetree, second frame
        // for articles
        this.showContentAsFrameset = true;
//DJ--
        this.viewContent = "v_Discussion_01$content";
        this.delContent = "v_Discussion_01$delcontent";
    } // initClassSpecifics


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * containers content view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  An array with button ids that can potentially be displayed.
     */
    protected int[] setContentButtons ()
    {
        // define buttons to be displayed:
        int[] buttons =
        {
            Buttons.BTN_TOPICNEW,
            Buttons.BTN_LISTDELETE,
            Buttons.BTN_SEARCH,
//            Buttons.BTN_HELP,
        }; // buttons

        // return button array
        return buttons;
    } // setContentButtons


    /**************************************************************************
     * Create the query to get the container's content out of the database.
     * <BR/>
     *
     * @return  The constructed query.
     */
    protected String createQueryRetrieveContentData ()
    {
        return
            "SELECT  DISTINCT oid, containerId, icon, state, name, posNoPath, typeName, isLink, " +
            "        linkedObjectId, owner, ownerName, ownerOid, ownerFullname, " +
            "        lastChanged, isNew, sortPath, discussionId " +
            " FROM   " + this.viewContent +
            " WHERE  discussionId = " + this.oid.toStringQu ();
    } // createQueryRetrieveContentData


    /**************************************************************************
     * Get the element type specific data out of the actual tuple of the query
     * result. <BR/>
     *
     * @param   action      The database connection object.
     * @param   commonObj   Object representing the list element.
     *
     * @throws  DBError
     *          Error when executing database statement.
     */
    protected void getContainerElementData (SQLAction action, ContainerElement commonObj)
        throws DBError
    {
        DiscussionElement_01 obj = (DiscussionElement_01) commonObj;

        // get element type specific attributes:
        obj.oid = SQLHelpers.getQuOidValue (action, "oid");
        obj.p_containerOid = SQLHelpers.getQuOidValue (action, "containerid");
        obj.state = action.getInt ("state");
        obj.name = action.getString ("name");
        obj.typeName = action.getString ("typeName");
        obj.isLink = action.getBoolean ("isLink");
        obj.icon = action.getString ("icon");
        obj.linkedObjectId = SQLHelpers.getQuOidValue (action, "linkedObjectId");
        obj.owner = new User (action.getInt ("owner"));
        obj.owner.username = action.getString ("ownerName");
        obj.owner.oid = SQLHelpers.getQuOidValue (action, "ownerOid");
        if (obj.owner.oid == null)
        {
            obj.owner.oid = OID.getEmptyOid ();
        } // if
        obj.owner.fullname = action.getString ("ownerFullname");
        obj.lastChanged = action.getDate ("lastChanged");
        obj.isNew = action.getBoolean ("isNew");
        obj.posNoPath = action.getString ("posNoPath");
        if ((this.sess != null) && (this.sess.activeLayout != null))
        {
            obj.layoutpath = this.sess.activeLayout.path;
        } // if
    } // getContainerElementData


    /***************************************************************************
     * Represent the content of the Discussion, i.e. its entries, to the user.
     * <BR/>
     *
     * @param   representationForm  Kind of representation.
     * @param   orderBy             Property, by which the result is
     *                              sorted.
     * @param   orderHow            Kind of ordering:
     *                              {@link ibs.bo.BOConstants#ORDER_ASC BOConstants.ORDER_ASC} or
     *                              {@link ibs.bo.BOConstants#ORDER_DESC BOConstants.ORDER_DESC}
     *                              <CODE>null</CODE> =>
     *                              {@link ibs.bo.BOConstants#ORDER_ASC BOConstants.ORDER_ASC}
     */
    public void performShowContent (int representationForm, int orderBy,
                                    String orderHow)
    {
        Font themaFont = new Font ();
        themaFont.bold = true;
        int size = this.elements.size ();    // number of elements within this
                                        // container
        TextElement text = new TextElement ("text");
        Page page = new Page (false);
        GroupElement body;

        // style sheet file is loaded
        StyleSheetElement style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () +
            "/" + this.sess.activeLayout.elems[LayoutConstants.SHEETLIST].styleSheet;
        page.head.addElement (style);

        // set the icon of this object:
        this.setIcon ();

        if (this.isTab ())
        {
            body = this.createHeader (page, 
                MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    this.headingName, new String[] {this.name}, this.env),
                this.getNavItems (), this.containerName, this.icon, size);
        } // if
        else
        {
            body = this.createHeader (page, 
                MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    this.headingName, new String[] {this.name}, this.env),
                this.getNavItems (), null, this.icon, size);
        } // else
        // show description of container:
        body.addElement (this.createDescription ());

        if (size > 0)                   // there are some elements?
        {
            int counter = 0;
            DiscElement[] tracking = new DiscElement[this.elements.size ()];
            int color = 0;
            DiscElement tree =
                new DiscElement ("disc767676", this.oid.toString ());
            tree.actlevel = 0;
            tree.open = true;

            String[] alignments = new String[4];
            alignments[0] = null;
            alignments[1] = null;
            alignments[2] = null;
            alignments[3] = IOConstants.ALIGN_RIGHT;
            tree.alignments = alignments;

            Element[] zusatzleft = new Element[1];
            zusatzleft[0] = new BlankElement ();
            tree.moreToShowLeft = zusatzleft;

            Element[] zusatz = new Element[2];
            zusatz[0] = new BlankElement ();
            zusatz[1] = new BlankElement ();
            tree.moreToShow = zusatz;

            tree.classId = BbdConstants.CLASS_DISC;
            String[] classIds = new String[4];
            classIds[0] = CssConstants.CLASS_COLICON;
            classIds[1] = CssConstants.CLASS_COLNAME;
            classIds[2] = CssConstants.CLASS_COLOWNER;
            classIds[3] = CssConstants.CLASS_COLLASTCHANGED;
            tree.classIds = classIds;

            Enumeration<ContainerElement> vectEnum = this.elements.elements ();

            while (vectEnum.hasMoreElements ())
            {
                this.performShowDiscussionElement (
                    (DiscussionElement_01) vectEnum.nextElement (), tree,
                    counter++, tracking, color++);
            } // while

            tree.maxlevels = tree.getMaxLevel ();
//                env.write ("maxlevels : " + tree.maxlevels);
            // create header
            tree.header = this.createHeading (this.headings,
                                this.orderings, orderBy, orderHow);

//                tree.header = createHeading (representationForm,
//                        this.headings, this.headerBgColor, orderBy, orderHow, tree.maxlevels+1);
            this.sess.discussion2 = tree;
            body.addElement (tree);
        } // size
        else
        {
             // show the according message to the user:
            TableElement table = new TableElement ();
            table.width = HtmlConstants.TAV_FULLWIDTH;
            RowElement tr = new RowElement (2);
            text = new TextElement (MultilingualTextProvider.getMessage(this.msgBundleContainerEmpty,
                this.msgContainerEmpty, new String[] {this.name}, env));
            TableDataElement td = new TableDataElement (text);
            td.classId = CssConstants.CLASS_BODY;
            tr.addElement (td);
            table.addElement (tr);

            body.addElement (table);

        } // else there are no elements

        if (this.p_isShowCommonScript)
        {
            // create the script to be executed on client:
            ScriptElement script = this.getCommonScript (true);
            page.head.addElement (script);
        } // if

        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            // show the according message to the user:
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
    } // performShowContent


    /***************************************************************************
     * Represent a discussion element of a discussion. <BR/>
     *
     * @param   elem    The discussion element to be represented.
     * @param   tree    Tree where the element shall be added.
     * @param   counter The actual counter value.
     * @param   tracking Tracking of all discussion elements.
     * @param   color   The color index for this discussion element.
     */
    public void performShowDiscussionElement (DiscussionElement_01 elem,
                                              DiscElement tree, int counter,
                                              DiscElement[] tracking, int color)
    {
        if (elem != null)
        {
            boolean expandNew = false;
            Vector<DiscElement> topics = new Vector<DiscElement> (5, 1);
            ImageElement elementNew = new ImageElement (BOPathConstants.PATH_GLOBAL + "new.gif");
            String url2 = this.getBaseUrlGet () +
                HttpArguments.createArg (BOArguments.ARG_FUNCTION, BbdFunctions.FCT_DISCCHANGESTATE) +
                HttpArguments.createArg (BOArguments.ARG_OID, this.oid.toString ()) +
                HttpArguments.createArg (BOArguments.ARG_NAME, this.name);
            Element[] addAttr = new Element[2];
            Element[] addAttrLeft = new Element[1];
            DiscElement m1 = new DiscElement (elem.name, elem.oid.toString ());
            m1.argName = BOArguments.ARG_TABID;
            m1.url2 = url2;

            // additional attributes:
            TextElement te2 = new TextElement (DateTimeHelpers.dateTimeToString (elem.lastChanged));
            TextElement te1 = new TextElement (elem.owner.fullname);
            addAttr[0] = te1;
            addAttr[1] = te2;
            m1.moreToShow = addAttr;
            m1.classId = BbdConstants.LST_CLASSDISCROWS[color % BOListConstants.LST_CLASSROWS.length];

            if (!elem.isNew)
            {
                addAttrLeft[0] = new BlankElement ();
            } // if
            else
            {
                addAttrLeft[0] = elementNew;
            } // else
            m1.moreToShowLeft = addAttrLeft;

            if (this.sess.discussion2 != null)
            {
                String open = ((DiscElement) this.sess.discussion2).open (elem.oid.toString ());
                m1.open =
                    open.equalsIgnoreCase (DiscElement.OP_TRUE) ||
                    elem.oid.tVersionId != this.getTypeCache ().getTVersionId (BbdTypeConstants.TC_Thread);
            } // if

            if ((this.sess.discussion2 == null) ||
                (!this.oid.equals (((DiscElement) this.sess.discussion2).discid)))
            {
                expandNew = true;
            } // if

/* ****************************************** */
/*        SCHWERER HACK DER IO KAPSEL         */
/* ****************************************** */
            if (this.defaultView == 0)
            {
                String url = this.getBaseUrlGet () +
                    HttpArguments.createArg (BOArguments.ARG_FUNCTION, BbdFunctions.FCT_DISC_QUICKVIEW) +
                    HttpArguments.createArg (BOArguments.ARG_OID, elem.oid.toString ());
                m1.url  = url;
                m1.target = HtmlConstants.FRM_SHEET2;
                String hack = "</A>" + IE302.HCH_NBSP + "<A HREF=\"" +
                    IOHelpers.getShowObjectJavaScriptUrl ("" + elem.oid) +
                    "\"" + "><IMG BORDER=\"0\" SRC=" +
                    this.sess.activeLayout.path +
                    BOPathConstants.PATH_OBJECTICONS + "Quickview.gif>";
                m1.text = m1.text + hack;
            } // if
            else
            {
                m1.url = IOHelpers.getShowObjectJavaScriptUrl ("" + elem.oid);
            } // else
/* ****************************************** */
/*                  HACK ENDE                 */
/* ****************************************** */

            // tracking of the DiscElements!
            tracking[counter] = m1;

            OID tempOid;
            if (elem.p_containerOid != null)
            {
                tempOid = elem.p_containerOid;
            } // if
            else
            {
                tempOid = OID.getEmptyOid ();
            } // else

            if (tempOid.equals (this.oid))
            {
                m1.openSrc = this.sess.activeLayout.path + BOPathConstants.PATH_DISC + Discussion_01.ICON_DISC_OPEN;
                m1.closeSrc = this.sess.activeLayout.path + BOPathConstants.PATH_DISC + Discussion_01.ICON_DISC_CLOSED;
                m1.disabledSrc = this.sess.activeLayout.path + BOPathConstants.PATH_DISC + Discussion_01.ICON_DISC_DISABLED;
                m1.icon  = this.sess.activeLayout.path + BOPathConstants.PATH_OBJECTICONS + elem.icon;
                m1.classId = BbdConstants.CLASS_DISCTOPIC;
                topics.addElement (m1);
                tree.addElement (m1);
            } // if
            else
            {
                // Searching for the right element to add!
                int j = counter - 1; // Element before actual Element

                while ((j > 0) &&
                    (elem.posNoPath
                        .indexOf (((DiscussionElement_01) this.elements
                            .elementAt (j)).posNoPath, 0) == -1))
                {
                    j--;
                } // while

                tracking[j].addElement (m1);

                if (this.sess.discussion2 == null)
                {
                    m1.open = true;
                } // if
                m1.openSrc = this.sess.activeLayout.path +
                    BOPathConstants.PATH_DISC + Discussion_01.ICON_DISC_OPEN;
                m1.closeSrc = this.sess.activeLayout.path +
                    BOPathConstants.PATH_DISC + Discussion_01.ICON_DISC_CLOSED;
                m1.disabledSrc = this.sess.activeLayout.path +
                    BOPathConstants.PATH_DISC + Discussion_01.ICON_DISC_DISABLED;
                m1.icon = this.sess.activeLayout.path +
                    BOPathConstants.PATH_OBJECTICONS + elem.icon;

                if (elem.isNew && expandNew)
                    // expand tree with new entries
                {
                    String op = DiscElement.OP_NOTFOUND;
                    for (Iterator<DiscElement> iter = topics.iterator (); iter
                        .hasNext ();)
                    {
                        DiscElement d = iter.next ();
                        op = d.open (elem.oid.toString ());
                        if (!op.equalsIgnoreCase (DiscElement.OP_NOTFOUND))
                        {
                            d.open = true;
                            // terminate the iteration:
                            break;
                        } // if
                    } // for iter
                } // if
            } // else
        } // if
    } // performShowDiscussionElement


    /**************************************************************************
     * open or close a branch of the tree showing the diskussion
     * <BR/>
     *
     * @param   id   OID of the element to be opened/closed
     */
    public void changeState (OID id)
    {
        if (this.sess.discussion2 == null) // lost the session !!!...
        {
/* ***************************************************** */
/*               SESSION verlorengegangen....            */
/* ***************************************************** */
            this.sess.oidLast = id;
            this.show (1);
            return;

/* ***************************************************** */
/*                      NEU AUFGEBAUT                    */
/* ***************************************************** */
        } // this.sess.discussion2 == null

        Page page = new Page (false); // the output page

        // Stylesheetfile wird geladen
        StyleSheetElement style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () +
            "/" + this.sess.activeLayout.elems[LayoutConstants.SHEETLIST].styleSheet;
        page.head.addElement (style);

        // set the icon of this object:
        this.setIcon ();

        GroupElement body;
        if (this.isTab ())
        {
            body = this.createHeader (page, 
                MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    this.headingName, new String[] {this.name}, this.env),
                this.getNavItems (), this.containerName, this.icon, this.size);
        } // if
        else
        {
            body = this.createHeader (page, 
                MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    this.headingName, new String[] {this.name}, this.env),
                this.getNavItems (), null, this.icon, this.size);
        } // else

        // show description of container:
        body.addElement (this.createDescription ());

        DiscElement disc = (DiscElement) this.sess.discussion2;

        disc.changeStatus (this.env.getParam (BOArguments.ARG_TABID));
        body.addElement (disc);
        try
        {
            page.build (this.env);
            page = null;
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            page = null;
        } // catch
    } // changeState


    /**************************************************************************
     * Represent the properties of a BusinessObject object to the user. <BR/>
     *
     * @param   table   Table where the properties shall be added.
     */
    protected void showFormProperties (TableElement table)
    {
        // property 'name':
        // set form field restriction (will automatically be used in
        // next showFormProperty)
        // restrict: no empty entries allowed
        this.formFieldRestriction =
            new FormFieldRestriction (false);
        // loop through all properties of this object and display them:
        this.showFormProperty (table, BOArguments.ARG_NAME, 
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                BOTokens.ML_NAME, env),
            Datatypes.DT_NAME, this.name);

        this.showFormProperty (table, BOArguments.ARG_INNEWS, 
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                BOTokens.ML_INNEWS, env),
            Datatypes.DT_BOOL, "" + this.showInNews);

        // property 'description':
        // set form field restriction (will automatically be used in
        // next showFormProperty)
        // restrict: empty entries allowed, maximum lenght is MAX_LENGTH_DESCRIPTION
        //           (actually (30.01.2001) it is 255)
        this.formFieldRestriction =
            new FormFieldRestriction (true, BOConstants.MAX_LENGTH_DESCRIPTION, 0);
        this.showFormProperty (table, BOArguments.ARG_DESCRIPTION,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                BOTokens.ML_CONTENT, env), Datatypes.DT_DESCRIPTION, this.description);

        // property 'validUntil':
        // set form field restriction (will automatically be used in
        // next showFormProperty)
        // restrict: no empty entries allowed
        // 0 .. default size/length values for datatype will be taken
        // null .. no upper bound
        this.formFieldRestriction =
            new FormFieldRestriction (false);
        this.showFormProperty (table, BOArguments.ARG_VALIDUNTIL,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                BOTokens.ML_VALIDUNTIL, env), Datatypes.DT_DATE, this.validUntil);

        String[] temp1 =
        {
            "" + BbdConstants.VAL_QUICK,
            "" + BbdConstants.VAL_STANDARD,
        };
        String[] temp2 = { 
            MultilingualTextProvider.getText (BbdTokens.TOK_BUNDLE,
                BbdTokens.ML_QUICKVIEW, env),  
            MultilingualTextProvider.getText (BbdTokens.TOK_BUNDLE,
                BbdTokens.ML_STNDVIEW, env)};
        if (this.defaultView == 0) // QuickView
        {
            this.showFormProperty (table, BbdArguments.ARG_DEFAULTVIEW,
                MultilingualTextProvider.getText (BbdTokens.TOK_BUNDLE,
                    BbdTokens.ML_DEFAULTVIEW, env),
                Datatypes.DT_SELECT, "" + BbdConstants.VAL_QUICK, temp1, temp2, 0);
        } // if
        else
        {
            this.showFormProperty (table, BbdArguments.ARG_DEFAULTVIEW,
                MultilingualTextProvider.getText (BbdTokens.TOK_BUNDLE,
                    BbdTokens.ML_DEFAULTVIEW, env),
                Datatypes.DT_SELECT, "" + BbdConstants.VAL_STANDARD, temp1, temp2, 1);
        } // else
    } // showFormProperties


    /**************************************************************************
     * Represent the properties of a BusinessObject object to the user. <BR/>
     *
     * @param   table   Table where the properties shall be added.
     */
    protected void showProperties (TableElement table)
    {
        // loop through all properties of this object and display them:
        this.showProperty (table, BOArguments.ARG_NAME, 
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                BOTokens.ML_NAME, env),
            Datatypes.DT_NAME, this.name);
        this.showProperty (table, BOArguments.ARG_TYPE, 
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                BOTokens.ML_TYPE, env),
            Datatypes.DT_TYPE, this.getMlTypeName ());
        this.showProperty (table, BOArguments.ARG_INNEWS, 
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                BOTokens.ML_INNEWS, env),
            Datatypes.DT_BOOL, "" + this.showInNews);
        this.showProperty (table, BOArguments.ARG_DESCRIPTION, 
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                BOTokens.ML_CONTENT, env),
            Datatypes.DT_DESCRIPTION, this.description);
        this.showProperty (table, BOArguments.ARG_VALIDUNTIL,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                BOTokens.ML_VALIDUNTIL, env),
            Datatypes.DT_DATE, this.validUntil);
        if (this.getUserInfo ().userProfile.showExtendedAttributes)
        {
            this.showProperty (table, null, null, Datatypes.DT_SEPARATOR,
                (String) null);

            this.showProperty (table, BOArguments.ARG_OWNER, 
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                    BOTokens.ML_OWNER, env),
                Datatypes.DT_USER, this.owner);
            this.showProperty (table, BOArguments.ARG_CREATED, 
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                    BOTokens.ML_CREATED, env),
                Datatypes.DT_USERDATE, this.creator, this.creationDate);
            this.showProperty (table, BOArguments.ARG_CHANGED, 
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                    BOTokens.ML_CHANGED, env),
                Datatypes.DT_USERDATE, this.changer, this.lastChanged);
        } // if (this.sess.userInfo.userProfile.showExtendedAttributes)
        this.showProperty (table, null, null, Datatypes.DT_SEPARATOR, (String) null);

        if (this.defaultView == 1)
        {
            this.showProperty (table, BbdArguments.ARG_DEFAULTVIEW,
                MultilingualTextProvider.getText (BbdTokens.TOK_BUNDLE,
                    BbdTokens.ML_DEFAULTVIEW, env), Datatypes.DT_TEXT,
                MultilingualTextProvider.getText (BbdTokens.TOK_BUNDLE,
                    BbdTokens.ML_STNDVIEW, env));
        } // if
        else
        {
            this.showProperty (table, BbdArguments.ARG_DEFAULTVIEW,
                MultilingualTextProvider.getText (BbdTokens.TOK_BUNDLE,
                    BbdTokens.ML_DEFAULTVIEW, env), Datatypes.DT_TEXT,
                MultilingualTextProvider.getText (BbdTokens.TOK_BUNDLE,
                    BbdTokens.ML_QUICKVIEW, env));
        } // else
    } // showProperties


    /**************************************************************************
     * Set the headings and the orderings for this container. <BR/>
     * Overload this function if you need other headings as the standard container. <BR/>
     */
    protected void setHeadingsAndOrderings ()
    {

        // check if columns shall be reduced
        // set headings:
        this.headings = MultilingualTextProvider.getText (BbdTokens.TOK_BUNDLE, 
            Discussion_01.LST_HEADINGS_DISCUSSION, env);

        // set ordering attributes for the corresponding headings:
        this.orderings = Discussion_01.LST_ORDERINGS_DISCUSSION;

        // ensure that there is an available ordering taken:
        this.ensureAvailableOrdering ();

    } // setHeadingsAndOrderings


    /**************************************************************************
     * Show the content of the Container, i.e. its elements. <BR/>
     * The objects are gotten from the database (with rights checked) and
     * represented to the user in the required form. <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    public void showDeleteForm (int representationForm)
    {
        // Backup the variables
        Vector<ContainerElement> backupElems = this.elements;
        String backupViewContent = this.viewContent;
        boolean backupContent = this.isActualContent ();
        this.elements = new Vector<ContainerElement> (10, 10);
        this.viewContent = this.delContent;
        this.setActualContent (false);
        String[] backupHeadings = this.headings;
        String[] backupOrderings = this.orderings;


        // set the function for the delete list
        this.fct = AppFunctions.FCT_LISTDELETEFORM;

        if (true)                       // container object resists on this
                                        // server?
        {
            try
            {
                // set headings and orderings:
                super.setHeadingsAndOrderings ();
/* KR 020125: not necessary because already done before
                // try to retrieve the container:
                retrieve (Operations.OP_VIEWELEMS);
*/
                // try to retrieve the content of this container:
                //retrieveDeleteContent (Operations.OP_DELETE, this.orderBy, this.orderHow);
                this.retrieveSelectionContentData (Operations.OP_DELETE, this.orderBy, this.orderHow);
                // show the container's content:
                // show the content:
                this.performShowSelectionContent (representationForm,
                    this.orderBy, this.orderHow, AppFunctions.FCT_LISTDELETE, 
                    MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                        BOTokens.ML_SELHEADERDELETE, env));
            } // try
            catch (NoAccessException e) // no access to objects allowed
            {
                // send message to the user:
                this.showNoAccessMessage (Operations.OP_VIEWELEMS);
            } // catch
/* KR 020125: not necessary because already done before
            catch (AlreadyDeletedException e) // no access to objects allowed
            {
                // send message to the user:
                showAlreadyDeletedMessage ();
            } // catch
*/
        } // if container object resists on this server
        else                            // object resists on another server
        {
          // invoke the object on the other server
        } // else object resists on another server


        // set the variables back
        this.elements = backupElems;
        this.viewContent = backupViewContent;
        this.setActualContent (backupContent);
        this.headings = backupHeadings;
        this.orderings = backupOrderings;
    } // showDeleteForm


    //
    // IMPORT / EXPORT METHODS
    //
    /**************************************************************************
     * Reads the object data from an dataElement. <BR/>
     *
     * @param   dataElement The dataElement to read the data from.
     *
     * @see ibs.bo.BusinessObject#readImportData
     */
    public void readImportData (DataElement dataElement)
    {
        // get business object specific values
        super.readImportData (dataElement);
        // get the type specific values
    } // readImportData


    /**************************************************************************
     * Writes the object data to an dataElement. <BR/>
     *
     * @param   dataElement The dataElement to write the data to.
     *
     * @see ibs.bo.BusinessObject#writeExportData
     */
    public void writeExportData (DataElement dataElement)
    {
        // set the business object specific values
        super.writeExportData (dataElement);
        // set the type specific values
    } // writeExportData

} // class Discussion_01
