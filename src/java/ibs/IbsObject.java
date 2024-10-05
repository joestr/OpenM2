/*
 * Class: IbsObject.java
 */

// package:
package ibs;

// imports:
import ibs.app.AppConstants;
import ibs.app.AppMessages;
import ibs.app.CssConstants;
import ibs.app.FilenameElement;
import ibs.app.UserInfo;
import ibs.app.func.FunctionHandlerContainer;
import ibs.bo.BOArguments;
import ibs.bo.BOConstants;
import ibs.bo.BOListConstants;
import ibs.bo.BOMessages;
import ibs.bo.BOPathConstants;
import ibs.bo.BOTokens;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.bo.SelectionList;
import ibs.bo.cache.ObjectPool;
import ibs.bo.path.ObjectPathNode;
import ibs.bo.tab.TabContainer;
import ibs.bo.type.Type;
import ibs.bo.type.TypeConstants;
import ibs.bo.type.TypeContainer;
import ibs.di.DITokens;
import ibs.io.Environment;
import ibs.io.HtmlConstants;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.LayoutConstants;
import ibs.io.Ssl;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.menu.MenuData_01;
import ibs.service.conf.IConfiguration;
import ibs.service.user.User;
import ibs.tech.html.BlankElement;
import ibs.tech.html.BuildException;
import ibs.tech.html.CenterElement;
import ibs.tech.html.Element;
import ibs.tech.html.Font;
import ibs.tech.html.FormElement;
import ibs.tech.html.GroupElement;
import ibs.tech.html.HTMLButtonElement;
import ibs.tech.html.HtmlHelpers;
import ibs.tech.html.IE302;
import ibs.tech.html.ImageElement;
import ibs.tech.html.InputElement;
import ibs.tech.html.LineElement;
import ibs.tech.html.LinkElement;
import ibs.tech.html.NewLineElement;
import ibs.tech.html.Page;
import ibs.tech.html.RowElement;
import ibs.tech.html.ScriptElement;
import ibs.tech.html.SelectElement;
import ibs.tech.html.SpanElement;
import ibs.tech.html.StyleSheetElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TableElement;
import ibs.tech.html.TextAreaElement;
import ibs.tech.html.TextElement;
import ibs.tech.http.HttpArguments;
import ibs.util.DateTimeHelpers;
import ibs.util.FormFieldRelation;
import ibs.util.FormFieldRestriction;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;

import java.io.File;
import java.util.Date;
import java.util.Vector;


/******************************************************************************
 * This is a base class for business objects. <BR/>
 *
 * @version     $Id: IbsObject.java,v 1.117 2011/09/27 10:52:35 btatzmann Exp $
 *
 * @author      Klaus Reimüller (KR), 980528
 ******************************************************************************
 */
public class IbsObject extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: IbsObject.java,v 1.117 2011/09/27 10:52:35 btatzmann Exp $";

    /**
     * Type of error for this class. <BR/>
     * This String is the name of an error which occurs within this class.
     */
    public String ERR_TYPE = this.getClass ().getName () + " Error";


    /**
     * Environment for getting input and generating output. <BR/>
     */
    protected Environment env = null;


    /**
     * Number of already shown properties. <BR/>
     */
    protected int properties = 0;

    /**
     * The system wide unique id of the referenced object. <BR/>
     */
    public OID oid = null;

    /**
     * Object representing the actual user. <BR/>
     * This object must contain all information which is necessary for checking
     * the actual user's rights, i.e. user name, groups, etc.
     */
    public User user = null;

    /**
     * Object containing the formfield restrictions for ONE  formfield. <BR/>
     * Set restrictions are always related to the next showFormProperty
     * method. The formfield restrictions must be set before the showForm-
     * Property call, If not field will be displayed with default parameters.
     *
     * Side effects: In last line of method showFormProperty this property will
     * be reset to null - this happens to avoid its usage by mistake.
     */
    public FormFieldRestriction formFieldRestriction = null;

    /**
     * String containing the check formfield restrictions java script function
     * for all formfields. <BR/>
     * This means: Formfield restrictions will be checked twice - first if user
     * leaves the formfield (while editing form data - on blur) and second when
     * user clicks the submit button (on submit).
     * This 2nd check must be done, because formvalidation for one field can be
     * skipped by simply pressing return (while in form) - onSubmit check
     * should avoid this behaviour.
     *
     * Side effects: This attribute will be expanded in every showFormProperty
     * method and will be used in the buildOnSubmitFormCheck method to build
     * one part (the restriction part) of the (on submit) JavaScript form check
     * function.
     */
    public String formFieldRestrictions = "";

    /**
     * String containing the check formfield relations java script statements. <BR/>
     *
     * Side effects: This attribute will be expanded in every addFormFieldRelation
     * call and will be used in the buildOnSubmitFormCheck method to build
     * one part (the relation part) of the (on submit) JavaScript form check
     * function.
     */
    public String formFieldRelations = "";

    /**
     * The actual form. <BR/>
     */
    public FormElement actForm = null;


    /**
     * Can the user set rights?. <BR/>
     */
    public boolean canSetRights = false;

    /**
     * Holds the actual session info. <BR/>
     */
    public SessionInfo sess = null;

    /**
     * Holds the actual application info. <BR/>
     * This application info object is used to store the global (= user
     * independent) data within the application.
     */
    public ApplicationInfo app = null;

    /**
     * Holds the actual dummy page. <BR/>
     */
    public Page page = null;

    /**************************************************************************
     * Creates a IbsObject object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     * <A HREF="#env">env</A> is initialized to null. <BR/>
     * <A HREF="#user">user</A> is initialized to null. <BR/>
     */
    public IbsObject ()
    {
        // call constructor of super class:
        super ();

        // initialize the instance's private properties:

        // initialize the instance's public/protected properties:
        this.env = null;
        this.user = null;
    } // IbsObject


    ///////////////////////////////////////////////////////////////////////////
    // functions called from application level
    ///////////////////////////////////////////////////////////////////////////


    /**************************************************************************
     * Set the actual session info object. <BR/>
     * The p_tracer object ist set, too.
     *
     * @param   sess    The session info object to be set.
     */
    public void setSession (SessionInfo sess)
    {
        // set the session info property:
        this.sess = sess;
    } // setSession


    /**************************************************************************
     * Sets the environment of this object. <BR/>
     * It is stored in the <A HREF="#env">env</A> property of this object.
     * <BR/>
     *
     * @param   env         Value for the environment.
     */
    public void setEnv (Environment env)
    {
        // set the new environment:
        this.env = env;
    } // setEnv


    /**************************************************************************
     * Gets the environment of this object. <BR/>
     *
     * @return  The environment object.
     */
    public Environment getEnv ()
    {
        // get the value and return it:
        return this.env;
    } // getEnv


    /**************************************************************************
     * Sets the properties value. <BR/>
     * It is stored in the <A HREF="#properties">properties</A> property of this object.
     * <BR/>
     *
     * @param   properties      properties value.
     */
    public void setProperties (int properties)
    {
        // set the properties value:
        this.properties = properties;
    } // setProperties


    /**************************************************************************
     * Get the properties value. <BR/>
     * It is stored in the <A HREF="#properties">properties</A> property of this object.
     * <BR/>
     *
     * @return the properties value
     */
    public int getProperties ()
    {
        // return the properties value:
        return this.properties;
    } // getProperties


    /**************************************************************************
     * delete object path - no object path is shown when object is shown. <BR/>
     *
     * @deprecated  This method shall not be used because it changes the
     *              object's behaviour. If this functionality is really
     *              necessary a new method for implementing this shall be
     *              created.
     */
    @Deprecated
    protected void dropObjectPath ()
    {
        // this method may be overwritten in subclasses
    } // dropObjectPath


    /**************************************************************************
     * Retrieves the parent tree for a BusinessObject. <BR/>
     *
     * @param   oid     The oid of the required business object.
     *
     * @return  Leaf ObjectPathNode for this business object.
     */
    protected ObjectPathNode getObjectPath (OID oid)
    {
        return null;
    } // getObjectPath


    /**************************************************************************
     * retrieves the parent tree of one BusinessObject when it is called
     * for the first time.
     *
     * @return  Leaf ObjectPathNode for this business object.
     */
    protected ObjectPathNode getObjectPath ()
    {
        return null;
    } // getObjectPath


    /**************************************************************************
     * Creates the string representation of the object path of a business
     * object. <BR/>
     *
     * @param   oid     The oid of the required business object.
     *
     * @return  The object path as string.
     *          <CODE>null</CODE> if there could not be an object path found.
     *
     * @see #getObjectPath ()
     */
    protected String getObjectPathString (OID oid)
    {
        return null;
    } // getObjectPathString


    /***************************************************************************
     * Creates the objectpath groupelement for the header of an object view.
     * <BR/>
     *
     * @param navItems The navigation items to be within the object path.
     *
     * @return  The constructed row element.
     */
    protected RowElement createObjectPath (int[] navItems)
    {
        GroupElement group = new GroupElement ();
        int navsize = 0;                // Counter for Items in Path
        ScriptElement js;               // Element fo9r calling Java Script (Buttons)
        String bid = null;              // Button ID for JavaScript Call
        String jsc;                     // String to call JavaScript
        RowElement tr = null;
        TableDataElement td;

        // create objectpath for output from object to menutab
        ObjectPathNode node = this.getObjectPath ();

        if (node != null)
        {
            if (navItems != null)               // Checking how many buttons to be shown
            {
                navsize = navItems.length + 1;  // navItems.length Button + 1 row for path
            } // if
            else
            {
                navsize = 1;                    // Just one row for path
            } // else
            tr = new RowElement (navsize);      // creating Table rows
            tr.classId = CssConstants.CLASS_PATH;

            //loop for showing buttons in path bar
            if (navItems != null)
            {
                for (int b = 0; b < navItems.length; b++)
                {
                    //getting OIDs for buttons from history
                    bid = Integer.toString (navItems[b]);       // converting buttonID to String for Javascript Call
                    jsc = new String ();
                    // replacing constants with OID and ButtonID for JavaScript call
                    jsc =
                        StringHelpers.replace (
                            StringHelpers.replace (
                                AppConstants.CALL_SHOWNAVITEM,
                                UtilConstants.TAG_NAME, this.oid.toString ()),
                            UtilConstants.TAG_NAME2, bid);
                    // setting JavaScript Call
                    js = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);
                    js.addScript (jsc);
                    td = new TableDataElement (js);
                    td.width = "1%";
                    tr.addElement (td);
                } // for
            } // if

            // createOBJECTPATH
            SpanElement sep = new SpanElement ();
            sep.addElement (new TextElement (">"));
            sep.classId = CssConstants.CLASS_PATHSEPARATOR;
            SpanElement tabSepBegin = new SpanElement ();
            tabSepBegin.addElement (new TextElement ("["));
            tabSepBegin.classId = CssConstants.CLASS_PATHSEPARATOR;
            SpanElement tabSepEnd = new SpanElement ();
            tabSepEnd.addElement (new TextElement ("]"));
            tabSepEnd.classId = CssConstants.CLASS_PATHSEPARATOR;


            // create Vector with oids of all menus
            Vector<OID> menuOids = new Vector<OID> ();

            MenuData_01 menDat = null;
            for (int i = 0; i < this.sess.menus.size (); i++)
            {
                menDat = this.sess.menus.elementAt (i);
                menuOids.addElement (menDat.oid);
            } // for

            // while there is still a parent node
            // and last node was not a menutab
            while (node != null)
            {
                TextElement text = new TextElement (node.getMlName (this.env));

                // if it is last element
                if (node.getNodeType () == ObjectPathNode.TYPE_LEAF)
                {
                    if (node.isPartOfParent ())
                    {
                        group.addElement (tabSepEnd, 0);
                    } // if

                    SpanElement sp = new SpanElement ();
                    sp.classId = CssConstants.CLASS_PATHOBJECT;
                    sp.addElement (text);
                    group.addElement (sp, 0);

                } // if
                else
                {
                    if (node.isPartOfParent ())
                    {
                        group.addElement (tabSepEnd, 0);
                    } // if

                    LinkElement linkToPath = new LinkElement (text, IOHelpers
                        .getShowObjectJavaScriptUrl ("" + node.getOid ()));
                    linkToPath.classId = CssConstants.CLASS_PATH;
                    group.addElement (linkToPath, 0);
                } // else


                SpanElement separator = null;

                // if node is tab add other separator
                if (node.isPartOfParent ())
                {
                    separator = tabSepBegin;
                } // if
                else
                {
                    separator = sep;
                } // else

                // show only tree until any menutab is reached
                if (menuOids.contains (node.getOid ()))
                {
                    node = null;
                } // if
                else
                {
                    node = node.getParent ();
                } // else

                // add separator if there will be an additional path element
                if (node != null)
                {
                    group.addElement (separator, 0);
                } // if

            } // while

            td = new TableDataElement (group);
            td.width = "99%";
            td.classId = CssConstants.CLASS_PATH;
            td.valign = IOConstants.ALIGN_MIDDLE;
            tr.addElement (td);
        } // if node != null

        return tr;
    }  // createObjectPath


    /**************************************************************************
     * Creates the header of an object view. <BR/>
     * This method also sets the page properties.
     *
     * @param   page        Page to add a header.
     * @param   name        Name of the object.
     * @param   navItems    Buttons to be shown in NavBar
     * @param   masterName  Name of object being placed above the object.
     * @param   icon        Name of the object icon.
     * @param   elements    Number of elements within the object if this
     *                      is a container view.
     *                      A value of -1 means not to display the number.
     *
     * @return  The empty group of I/O elements where the body of the page shall
     *          be inserted. <CODE>null</CODE> if there is no body.
     *
     * @see #createFormFooter (FormElement, String, String, String, String, boolean, boolean)
     */
    protected GroupElement createHeader (Page page, String name,
                                         int[] navItems, String masterName,
                                         String icon, int elements)
    {
        // call common method and return the result:
        return this.createHeader (page, name, navItems, masterName, null, icon,
            null, false, elements);
    } // createHeader


    /**************************************************************************
     * Creates the header of an object view. <BR/>
     * This method also sets the page properties.
     *
     * @param   page        Page to add a header.
     * @param   name        Name of the object.
     * @param   navItems    Buttons to be shown in NavBar
     * @param   masterName  Name of object being placed above the object.
     * @param   operation   Operation to be performed.
     * @param   icon        Name of the object icon.
     * @param   containerName The name of the container where the object
     *                      resides.
     *
     * @return  The empty group of I/O elements where the body of the page shall
     *          be inserted. <CODE>null</CODE> if there is no body.
     *
     * @see #createFormFooter (FormElement, String, String, String, String, boolean, boolean)
     */
    protected GroupElement createHeader (Page page, String name,
                                         int[] navItems, String masterName,
                                         String operation, String icon,
                                         String containerName)
    {
        // call common method and return the result:
        return this.createHeader (page, name, navItems, masterName, operation,
            icon, containerName, false, -1);
    } // createHeader


    /**************************************************************************
     * Creates the header of an object view. <BR/>
     * This method also sets the page properties.
     *
     * @param   page        Page to add a header.
     * @param   name        Name of the object.
     * @param   navItems    Buttons to be shown in NavBar
     * @param   masterName  Name of object being placed above the object.
     * @param   operation   Operation to be performed.
     * @param   icon        Name of the object icon.
     * @param   containerName The name of the container where the object
     *                      resides.
     * @param   showFrame   Shall a frame be displayed.
     * @param   elements    Number of elements within the object if this
     *                      is a container view.
     *                      A value of -1 means not to display the number.
     *
     * @return  The empty group of I/O elements where the body of the page shall
     *          be inserted. <CODE>null</CODE> if there is no body.
     *
     * @see #createFormFooter (FormElement, String, String, String, String, boolean, boolean)
     */
    protected GroupElement createHeader (Page page, String name,
                                         int[] navItems, String masterName,
                                         String operation, String icon,
                                         String containerName,
                                         boolean showFrame, int elements)
    {
        String caption = IE302.HCH_NBSP;
        String containerNameLocal = containerName; // local version of containerName

        // set the document's base:
        IOHelpers.setBase (page, this.app, this.sess, this.env);

        if (containerNameLocal != null)
        {
            containerNameLocal = null;
        } // if

        if (name != null && operation != null && masterName != null &&
            containerNameLocal != null && !masterName.equals (containerNameLocal))
        {
            caption = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJHEADER_NAMEMASTERCONTAINEROPERATION,
                new String[] {name, masterName, containerNameLocal, operation}, env);
        } // if
        else if (name != null && masterName != null && containerNameLocal != null &&
            !masterName.equals (containerNameLocal))
        {
            caption = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJHEADER_NAMEMASTERCONTAINER, 
                new String[] {name, masterName, containerNameLocal}, env);
        } // else if
        else if (name != null && operation != null && containerNameLocal != null)
        {
            caption = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJHEADER_NAMECONTAINEROPERATION,
                new String[] {name, containerNameLocal, operation}, env);
        } // else if
        else if (name != null && operation != null && masterName != null)
        {
            caption = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJHEADER_NAMEMASTEROPERATION,
                new String[] {masterName, operation}, env);
        } // else if
        else if (name != null && operation != null)
        {
            caption = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJHEADER_NAMEOPERATION,
                new String[] {name, operation}, env);
        } // else if
        else if (name != null && masterName != null)
        {
            caption = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJHEADER_NAMEMASTER, new String[] {masterName}, env);
        } // else if
        else if (name != null && containerNameLocal != null)
        {
            caption = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJHEADER_NAMECONTAINER,
                new String[] {name, containerNameLocal}, env);
        } // else if
        else if (name != null)
        {
            caption = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJHEADER_NAME, new String[] {name}, env);
        } // else if
        else if (operation != null)
        {
            caption = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJHEADER_OPERATION, new String[] {operation}, env);
        } // else if

        // init variables for table
        RowElement tr;
        TableDataElement td;
        NewLineElement nl = new NewLineElement ();
        GroupElement group = new GroupElement ();
        GroupElement body = new GroupElement ();
        TableElement table = new TableElement (1);
        CenterElement cel;
        table.width = HtmlConstants.TAV_FULLWIDTH;
        // outer table
        table.border = 0;
        table.frametypes = IOConstants.FRAME_VOID;

        StyleSheetElement style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () + "/" +
                         this.sess.activeLayout.elems[LayoutConstants.HEADER].styleSheet;
        page.head.addElement (style);

        table.classId = CssConstants.CLASS_HEADER;
        tr = new RowElement (1);
        if (icon != null)           // icon exists?
        {
            group.addElement (new ImageElement (this.sess.activeLayout.path + BOPathConstants.PATH_OBJECTICONS + icon));
        } //if
        else
        {
            group.addElement (new BlankElement ());
        } // if icon exists
        group.addElement (new BlankElement ());
        group.addElement (new TextElement (caption));

        tr.classId = CssConstants.CLASS_HEADER;
        td = new TableDataElement (group);
        td.classId = CssConstants.CLASS_HEADER;
        tr.addElement (td);
        String howMany = "";
        if (elements >= 0)
        {
            if (elements == 1)      // just one element?
            {
                howMany += " (" + MultilingualTextProvider.getMessage(BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_ELEMENT, new String[] {"" + elements}, env) + ")";
            } // if
            else                    // 0 or several elements
            {
                howMany += " (" + MultilingualTextProvider.getMessage(BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_ELEMENTS, new String[] {"" + elements}, env) + ")";
            } // else
        } // if
        Font dummy = new Font ();
        dummy.classId = CssConstants.CLASS_HEADERNUM;
        group.addElement (new TextElement (howMany, dummy));

        table.addElement (tr);
        group = new GroupElement ();
        cel = new CenterElement (table);
        page.body.addElement (cel);

        // modified MW 000221
        table = new TableElement (1);        // new table for buttons and path
        table.width = HtmlConstants.TAV_FULLWIDTH;
        table.border = 0;
        table.frametypes = IOConstants.FRAME_VOID;
        table.classId = CssConstants.CLASS_PATH;

        // add object path to table:
        if ((tr = this.createObjectPath (navItems)) != null)
        {
            table.addElement (tr);
        } // if

        // center the output:

        page.body.addElement (table);
        page.body.addElement (body);

        return body;                    // return the body
    } // createHeader


    /**************************************************************************
     * Creates the header of a form. <BR/>
     *
     * @param   page        Page to add a form.
     * @param   name        Name of the object.
     * @param   navItems    Buttons to be shown in NavBar
     * @param   masterName  Name of the master object.
     * @param   operation   Operation to be performed on sumbit.
     * @param   target      Target of the action to be performed when
     *                      submitting the form.
     * @param   icon        Name of the object icon.
     *
     * @return  The empty group of I/O elements where the body of the page shall
     *          be inserted. <CODE>null</CODE> if there is no body.
     *
     * @see #createFormFooter (FormElement, String, String, String, String, boolean, boolean)
     */
    protected FormElement createFormHeader (Page page, String name,
                                            int[] navItems, String masterName,
                                            String operation, String target,
                                            String icon)
    {
        // call common method and return the result:
        return this.createFormHeader (page, name, navItems, masterName,
            operation, target, icon, null, -1);
    } // createFormHeader


    /**************************************************************************
     * Creates the header of a form. <BR/>
     *
     * @param   page        Page to add a form.
     * @param   name        Name of the object.
     * @param   navItems    Buttons to be shown in NavBar
     * @param   masterName  Name of the master object.
     * @param   operation   Operation to be performed on sumbit.
     * @param   target      Target of the action to be performed when
     *                      submitting the form.
     * @param   icon        Name of the object icon.
     * @param   containerName Name of the container where the object resides.
     *
     * @return  The empty group of I/O elements where the body of the page shall
     *          be inserted. <CODE>null</CODE> if there is no body.
     *
     * @see #createFormFooter (FormElement, String, String, String, String, boolean, boolean)
     */
    protected FormElement createFormHeader (Page page, String name,
                                            int[] navItems, String masterName,
                                            String operation, String target,
                                            String icon, String containerName)
    {
        // call common method and return the result:
        return this.createFormHeader (page, name, navItems, masterName,
            operation, target, icon, containerName, -1);
    } // createFormHeader


    /**************************************************************************
     * Creates the header of a form. <BR/>
     *
     * @param   page        Page to add a form.
     * @param   name        Name of the object.
     * @param   navItems    Buttons to be shown in NavBar
     * @param   masterName  Name of the master object.
     * @param   operation   Operation to be performed on sumbit.
     * @param   target      Target of the action to be performed when
     *                      submitting the form.
     * @param   icon        Name of the object icon.
     * @param   containerName Name of the container where the object resides.
     * @param   elements    Number of elements in an container
     *
     * @return  The empty group of I/O elements where the body of the page shall
     *          be inserted. <CODE>null</CODE> if there is no body.
     *
     * @see #createFormFooter (FormElement, String, String, String, String, boolean, boolean)
     */
    protected FormElement createFormHeader (Page page, String name,
                                            int[] navItems, String masterName,
                                            String operation, String target,
                                            String icon, String containerName,
                                            int elements)
    {
        String targetStr = target;      // local representation of target

        // set the document's base:
        IOHelpers.setBase (page, this.app, this.sess, this.env);

        if (this.sess.wizardRegistration) // LoginWizard activated
        {
            // formValidation has to be included
            ScriptElement jscript = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);
            jscript.type = ScriptElement.TYPE_JAVASCRIPT;
            String path = "";
            path = this.getUserInfo ().homepagePath;
            jscript.src = path + "formvalidation.js";
            page.head.addElement (jscript);

            // target of Form is _top
            targetStr = "_top";
        } // if

        // define form
        // ensure that a correct target is set:
        if (targetStr == null)         // no correct target set?
        {
            targetStr = HtmlConstants.FRM_SHEET; // set default target
        } // if

        // create form:
        FormElement form = new FormElement (this.getBaseUrlPost (),
            UtilConstants.HTTP_POST, targetStr);
        this.actForm = form;
        form.name = "sheetForm";
        GroupElement body = this.createHeader (page, name, navItems,
            masterName, operation, icon, containerName, false, elements);
        body.addElement (form);

        /*
         * Infinity loop with IE 6.0:
         * If the focus is set to a mulitselectionbox, it runs into a infinity
         * loop, because the multiselectionbox will be enlarged and minimized
         * without end.
         *
        // set the script for the page loader:
        if (page.body.onLoad == null)   // no on load script set?
        {
            // set the new script:
            page.body.onLoad =
                "top.scripts.setFieldFocus (this." + form.name + ")";
        } // if no on load script set
        else                            // load script already set
        {
            // append to existing script:
            page.body.onLoad +=
                "top.scripts.setFieldFocus (this." + form.name + ")";
        } // else load script already set
        */
        return form;
    } // createFormHeader


    /**************************************************************************
     * Creates the footer of a form with an ok and a cancel button. <BR/>
     *
     * @param   form    Form to add the footer.
     *
     * @see #createFormHeader (Page, String, int[], String, String, String, String, String, int)
     */
    protected void createFormFooter (FormElement form)
    {
        this.createFormFooter (form, null);
    } // createFormFooter


    /**************************************************************************
     * Creates the footer of a form with an ok and a cancel button. <BR/>
     *
     * @param   form        Form to add the footer.
     * @param   okAction    JavaScript code to be performed when OK button
     *                      pressed.
     *
     * @see #createFormHeader (Page, String, int[], String, String, String, String, String, int)
     */
    protected void createFormFooter (FormElement form, String okAction)
    {
        this.createFormFooter (form, okAction, null);
    } // createFormFooter


    /**************************************************************************
     * Creates the footer of a form with an ok and a cancel button. <BR/>
     *
     * @param   form        Form to add the footer.
     * @param   okAction    JavaScript code to be performed when OK button
     *                      pressed.
     * @param   cancelAction JavaScript code to be performed when Cancel button
     *                      pressed.
     *
     * @see #createFormHeader (Page, String, int[], String, String, String, String, String, int)
     */
    protected void createFormFooter (FormElement form, String okAction,
        String cancelAction)
    {
        this.createFormFooter (form, okAction, cancelAction, null, null, false, false);
    } // createFormFooter


    /**************************************************************************
     * Creates the footer of a form with an ok and a cancel button. <BR/>
     *
     * This is a wrapper method. <BR/>
     *
     * @param   form        Form to add the footer.
     * @param   okAction    JavaScript code to be performed when OK button
     *                      pressed.
     * @param   cancelAction JavaScript code to be performed when Cancel button
     *                      pressed.
     * @param   okText      Text to be displayed on top of the OK button.
     * @param   cancelText  Text to be displayed on top of the Cancel button.
     * @param   isNewObject true: shows, that a new object just has been
     *                          created. In this case we show a small
     *                          additional menu for the handling of the new
     *                          object.
     *                      false: standard behavior for editing objects.
     * @param   isNoCancelButton if true there will be no cancel button shown
     *
     * @see #createFormHeader (Page, String, int[], String, String, String, String, String, int)
     * @see #createFormFooter (FormElement, String, String, String, String, boolean, boolean, GroupElement)
     */
    protected void createFormFooter (FormElement form,
                                     String okAction,
                                     String cancelAction,
                                     String okText,
                                     String cancelText,
                                     boolean isNewObject,
                                     boolean isNoCancelButton)
    {
        this.createFormFooter (form, okAction, cancelAction,
                okText, cancelText, isNewObject, isNoCancelButton, null);
    } // createFormFooter

    /**************************************************************************
     * Creates the footer of a form with an ok and a cancel button and any
     * additional custom HTML code to be added next to the buttons. <BR/>
     *
     * This is a wrapper method. <BR/>
     *
     * @param   form        Form to add the footer.
     * @param   okAction    JavaScript code to be performed when OK button
     *                      pressed.
     * @param   cancelAction JavaScript code to be performed when Cancel button
     *                      pressed.
     * @param   okText      Text to be displayed on top of the OK button.
     * @param   cancelText  Text to be displayed on top of the Cancel button.
     * @param   isNewObject true: shows, that a new object just has been
     *                          created. In this case we show a small
     *                          additional menu for the handling of the new
     *                          object.
     *                      false: standard behavior for editing objects.
     * @param   isNoCancelButton if true there will be no cancel button shown
     * @param   custom      any custom group element to be added next to the buttons
     *
     * @see #createFormFooter (FormElement, String, String, String, String, boolean, boolean, boolean, GroupElement)
     */
    protected void createFormFooter (FormElement form,
            String okAction,
            String cancelAction,
            String okText,
            String cancelText,
            boolean isNewObject,
            boolean isNoCancelButton,
            GroupElement custom)
    {
        this.createFormFooter (form, okAction, cancelAction, okText, cancelText, isNewObject, isNoCancelButton,
                false, custom);       
    } // createFormFooter

    
    /**************************************************************************
     * Creates the footer of a form with an ok and a cancel button and any
     * additional custom HTML code to be added next to the buttons. <BR/>
     *
     * @param   form        Form to add the footer.
     * @param   okAction    JavaScript code to be performed when OK button
     *                      pressed.
     * @param   cancelAction JavaScript code to be performed when Cancel button
     *                      pressed.
     * @param   okText      Text to be displayed on top of the OK button.
     * @param   cancelText  Text to be displayed on top of the Cancel button.
     * @param   isNewObject true: shows, that a new object just has been
     *                          created. In this case we show a small
     *                          additional menu for the handling of the new
     *                          object.
     *                      false: standard behavior for editing objects.
     * @param   isNoCancelButton if true there will be no cancel button shown
     * @param   isShowCurrentObjectOnCancel if true show object for current
     *          object is called on cancel.
     * @param   custom      any custom group element to be added next to the buttons
     *
     * @see #createFormHeader (Page, String, int[], String, String, String, String, String, int)
     */
    protected void createFormFooter (FormElement form,
                                     String okAction,
                                     String cancelAction,
                                     String okText,
                                     String cancelText,
                                     boolean isNewObject,
                                     boolean isNoCancelButton,
                                     boolean isShowCurrentObjectOnCancel,
                                     GroupElement custom)
    {
        String btnSubmitText =
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_BUTTONOK, env); // text of submit button
        String btnCancelText = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_BUTTONCANCEL, env); // text of cancel button

        if (okText != null)
        {
            btnSubmitText = okText;
        } // if
        if (cancelText != null)
        {
            btnCancelText = cancelText; // text of cancel button
        } // if

        // set text of submit button:
        if (okText != null)             // there is a text?
        {
            btnSubmitText = okText;     // set the text
        } // if
        // set text of cancel button:
        if (cancelText != null)         // there is a text?
        {
            btnCancelText = cancelText; // set the text
        } // if

        // init variables for table
        TableElement footerTable = new TableElement ();
        String[] alignments = {IOConstants.ALIGN_RIGHT};
        footerTable.border = 0;
        footerTable.ruletype = IOConstants.RULE_GROUPS;
        footerTable.width = HtmlConstants.TAV_FULLWIDTH;
        footerTable.alignment = alignments;
        footerTable.classId = isNewObject ? CssConstants.CLASS_FOOTER_HR : CssConstants.CLASS_FOOTER;
        TableElement footerTable2 = new TableElement ();
        footerTable2.border = 0;
        footerTable2.ruletype = IOConstants.RULE_GROUPS;
        footerTable2.width = "50%";
        footerTable2.alignment = alignments;
        footerTable2.classId = CssConstants.CLASS_FOOTER;
        TableElement footerTable3 = new TableElement ();
        footerTable3.border = 0;
        footerTable3.ruletype = IOConstants.RULE_GROUPS;
        footerTable3.width = HtmlConstants.TAV_FULLWIDTH;
        footerTable3.alignment = alignments;
        footerTable3.classId = CssConstants.CLASS_FOOTER_HR;

        RowElement tr = new RowElement (1);
        GroupElement group = new GroupElement ();
        TableDataElement td = new TableDataElement (group);
        td.classId = CssConstants.CLASS_BUTTONS;
        tr.addElement (td);
        footerTable.addElement (tr);
        td.alignment = IOConstants.ALIGN_MIDDLE;
        LineElement line = new LineElement ();
        line.width = "90%";
        line.size = 0;
        group.addElement (line);
//        group.addElement (new NewLineElement ());

        if (isNewObject)
        {
            int counter = 0;
            RowElement trActions = new RowElement (2);
            GroupElement grActions = new GroupElement ();
            GroupElement grButtons = new GroupElement ();
            InputElement ie;
            // Blanks zum Einrücken
            BlankElement be = new BlankElement ();
            GroupElement blankGroup = new GroupElement ();

            for (int i = 1; i <= 6; i++)
            {
                blankGroup.addElement (be);
            } // for i
            ie = new InputElement (BOConstants.NEW_BUSINESS_OBJECT_MENU, InputElement.INP_RADIO, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SAVE_AND_BACK, env));
            if (this.oid.tVersionId != this.getTypeCache ().getTVersionId (TypeConstants.TC_XMLViewer))
                                        // no xml viewer?
            {
                ie.checked = true;      // preselect the actual option
            } // if no xml viewer
            grActions.addElement (ie);
            LinkElement link =
                new LinkElement (
                    new TextElement (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SAVE_AND_BACK, env)),
                    IOConstants.URL_JAVASCRIPT + "sh (" + counter++ + ")");
            link.classId = CssConstants.CLASS_INVISLINK;
            grActions.addElement (link);
            grActions.addElement (new NewLineElement ());
            ie = new InputElement (BOConstants.NEW_BUSINESS_OBJECT_MENU, InputElement.INP_RADIO, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SAVE_AND_TO_OBJECT, env));
            if (this.oid.tVersionId == this.getTypeCache ().getTVersionId (TypeConstants.TC_XMLViewer))
                                        // xml viewer?
            {
                ie.checked = true;      // preselect the actual option
            } // if xml viewer
            grActions.addElement (ie);
            link = new LinkElement (
                    new TextElement (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SAVE_AND_TO_OBJECT, env)), IOConstants.URL_JAVASCRIPT + "sh (" + counter++ + ")");
            link.classId = CssConstants.CLASS_INVISLINK;
            grActions.addElement (link);

            if (this.isSafeAndNewAllowed ())
            {
                grActions.addElement (new NewLineElement ());
                ie = new InputElement (BOConstants.NEW_BUSINESS_OBJECT_MENU, InputElement.INP_RADIO, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SAVE_AND_NEW, env));
                grActions.addElement (ie);
                link = new LinkElement (
                    new TextElement (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SAVE_AND_NEW, env)), IOConstants.URL_JAVASCRIPT + "sh (" + counter++ + ")");
                link.classId = CssConstants.CLASS_INVISLINK;
                grActions.addElement (link);
            } // if

            if (this.canSetRights)
            {
                grActions.addElement (new NewLineElement ());
                ie = new InputElement (BOConstants.NEW_BUSINESS_OBJECT_MENU, InputElement.INP_RADIO, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_TO_RIGHTS, env));
                grActions.addElement (ie);
                link =
                    new LinkElement (
                        new TextElement (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_TO_RIGHTS, env)), IOConstants.URL_JAVASCRIPT + "sh (" + counter++ + ")");
                link.classId = CssConstants.CLASS_INVISLINK;
                grActions.addElement (link);
                grActions.addElement (new NewLineElement ());
            } // if

            TableDataElement tdActions = new TableDataElement (grActions);
            tdActions.alignment = IOConstants.ALIGN_LEFT;
            tdActions.nowrap = true;
            tdActions.classId = CssConstants.CLASS_ACTIONS;
            trActions.addElement (tdActions);
            grButtons.addElement (new TextElement ("<NOBR>"));
            grButtons.addElement (blankGroup);

            HTMLButtonElement okButton =
                new HTMLButtonElement (IOConstants.BUTTONID_SUBMIT, HTMLButtonElement.INP_SUBMIT);

            okButton.classId = CssConstants.CLASS_BUTTONSUBMIT;
            okButton.addLabel (HTMLButtonElement.BUTTON_LABEL_BOUNDARY + btnSubmitText + HTMLButtonElement.BUTTON_LABEL_BOUNDARY);
            okButton.onClick = "";
            if (okAction != null)       // OK action shall be performed?
            {
                okButton.onClick = okAction + HTMLButtonElement.ONCLICK_SUBMIT;
            } // if
            else    // no okAction set
            {
                okButton.onClick = HTMLButtonElement.ONCLICK_SUBMIT;
            } // else
            grButtons.addElement (okButton);

            if (!isNoCancelButton)
            {
                grButtons.addElement (new BlankElement ());
                grButtons.addElement (new BlankElement ());
                grButtons.addElement (new BlankElement ());
                HTMLButtonElement cancelButton =
                    new HTMLButtonElement (IOConstants.BUTTONID_CANCEL, HTMLButtonElement.INP_BUTTON);
                cancelButton.classId = CssConstants.CLASS_BUTTONCANCEL;
                cancelButton.addLabel (btnCancelText);
                if (cancelAction != null)       // OK action shall be performed?
                {
                    cancelButton.onClick = cancelAction;
                } // if
                else                        // no OK action
                {
                    cancelButton.onClick =
                        isShowCurrentObjectOnCancel ?
                                IOHelpers.getShowObjectJavaScript (this.oid.toString ()) :
                                    HTMLButtonElement.ONCLICK_GOBACK;
                } // else
                grButtons.addElement (cancelButton);
            } // if

            // check if any custom code has been set
            if (custom != null)
            {
                grButtons.addElement (custom);
            } // if (custom != null)

            grButtons.addElement (new TextElement ("</NOBR>"));
            TableDataElement tdButtons = new TableDataElement (grButtons);
            tdButtons.classId = CssConstants.CLASS_BUTTONS;

            trActions.addElement (tdButtons);
            trActions.valign = IOConstants.ALIGN_MIDDLE;
            footerTable2.addElement (trActions);

            RowElement trForSecondLine = new RowElement (1);
            GroupElement groupSecondLine = new GroupElement ();
            LineElement secondLine = new LineElement ();
            secondLine.width = "90%";
            groupSecondLine.addElement (secondLine);
            TableDataElement tdForSecondLine = new TableDataElement (groupSecondLine);
            tdForSecondLine.alignment = IOConstants.ALIGN_MIDDLE;
            trForSecondLine.addElement (tdForSecondLine);
            footerTable3.addElement (trForSecondLine);

            ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);
            script.addScript ("function sh (f) {document." + form.name + "." + BOConstants.NEW_BUSINESS_OBJECT_MENU + "[f].checked = true;}");
            form.addElement (script);
        } // if isNewObject
        else                            // not a new object
        {
            HTMLButtonElement okButton =
                new HTMLButtonElement (IOConstants.BUTTONID_SUBMIT, HTMLButtonElement.INP_SUBMIT);

            okButton.classId = CssConstants.CLASS_BUTTONSUBMIT;
            okButton.addLabel (HTMLButtonElement.BUTTON_LABEL_BOUNDARY + btnSubmitText + HTMLButtonElement.BUTTON_LABEL_BOUNDARY);
            if (okAction != null)       // OK action shall be performed?
            {
                okButton.onClick = okAction + HTMLButtonElement.ONCLICK_SUBMIT;
            } // if
            else    // no okAction set
            {
                okButton.onClick = HTMLButtonElement.ONCLICK_SUBMIT;
            } // else
            group.addElement (okButton);

            if (!isNoCancelButton)
            {
                group.addElement (new BlankElement ());
                group.addElement (new BlankElement ());
                group.addElement (new BlankElement ());

                HTMLButtonElement cancelButton =
                    new HTMLButtonElement (IOConstants.BUTTONID_CANCEL, HTMLButtonElement.INP_BUTTON);
                cancelButton.classId = CssConstants.CLASS_BUTTONCANCEL;
                cancelButton.addLabel (btnCancelText);
                if (cancelAction != null)       // OK action shall be performed?
                {
                    cancelButton.onClick = cancelAction;
                } // if
                else                        // no OK action
                {
                    cancelButton.onClick = HTMLButtonElement.ONCLICK_GOBACK;
                } // else
                group.addElement (cancelButton);
            } //if
            // check if any custom code has been set
            if (custom != null)
            {
                group.addElement (custom);
            } // if (custom != null)
            line = new LineElement ();
            line.width = "90%";
            line.size = 0;
            group.addElement (line);

        } // if

        if (this.getUserInfo ().userProfile == null)
        {
            // do nothing
        } // if

        form.addElement (footerTable);
        if (isNewObject)
        {
            GroupElement gr = new GroupElement ();
            gr.addElement (footerTable2);
            gr.addElement (footerTable3);
            form.addElement (new CenterElement (gr));
        } // if

        // add the script to enable an element (used for buttons):
        ScriptElement enableFunction =
            new ScriptElement (ScriptElement.LANG_JAVASCRIPT);
        enableFunction.addScript (
                "function setEnableTimeout (elem, time)" +
                "{" +
                " window.setTimeout ('enable (\"' + elem.id + '\")', time);" +
                "}" +
                "function enable (elemId)" +
                "{" +
                " var elem = document.getElementById (elemId);" +
                " if (elem) {elem.disabled = false;}" +
                "}");
        form.addElement (enableFunction);

        // now add script function which checks form field restrictions on
        // submit of form
        ScriptElement checkFunction = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);
        checkFunction.addScript (this.buildOnSubmitFormCheck ());
        form.addElement (checkFunction);
        form.onSubmit = "return submitAllowed ();";
    } // createFormFooter


    /**************************************************************************
     * Check if the "safe and new" option within the NEW dialog is allowed.
     * <BR/>
     * This method shall be overwritten by classes which do not allow this
     * feature.
     *
     * @return  <CODE>true</CODE> if the option may be displayed,
     *          <CODE>false</CODE> otherwise.
     *          Default: <CODE>true</CODE>
     */
    protected boolean isSafeAndNewAllowed ()
    {
        // return the default value:
        return true;
    } // isSafeAndNewAllowed


    /**************************************************************************
     * Creates the mark and unmark buttons. <BR/>
     *
     * @param name      name of checkbox
     *
     * @return a groupElement that holds the buttons and the javascript
     *
     * @see #createFormHeader (Page, String, int[], String, String, String, String, String, int)
     */
    protected GroupElement createMarkButtons (String name)
    {
        ScriptElement script;
        GroupElement group;
        InputElement button;

        group = new GroupElement ();

        script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);

        script.addScript (
            "function mark (cb, act)\n" +
            "{\n" +
            "    if (cb != \"undefined\" && cb != null)\n" +
            "    {\n" +
            "        if (!cb.length)\n" +
            "        {\n" +
            "            if (act == 1)\n" +
            "                cb.checked = true;\n" +
            "            else if (act == 2)\n" +
            "                cb.checked = false;\n" +
            "            else\n" +
            "                cb.checked = !cb.checked\n" +
            "        }\n" +
            "        else\n" +
            "        {\n" +
            "           for (var i = cb.length - 1; i >= 0; i--)\n" +
            "           {\n" +
            "            if (act == 1)\n" +
            "                  cb[i].checked = true;\n" +
            "            else if (act == 2)\n" +
            "                  cb[i].checked = false;\n" +
            "               else\n" +
            "                  cb[i].checked = !cb[i].checked\n" +
            "           }\n" +
            "        }\n" +
            "    }\n" +
            "}\n");

        group.addElement (script);
        button = new InputElement ("BUTT_MARK", InputElement.INP_BUTTON +
            "\" onClick=\"mark (" + HtmlConstants.JREF_SHEETFORM + name +
            ", 1);" + "mark (" + HtmlConstants.JREF_SHEETFORM +
            BOArguments.ARG_VIRTUALOBJECTPREFIX + name + ", 1);",
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MARK, env));
        group.addElement (button);
        group.addElement (new BlankElement ());
        button = new InputElement ("BUTT_UNMARK", InputElement.INP_BUTTON +
            "\" onClick=\"mark (" + HtmlConstants.JREF_SHEETFORM + name +
            ", 2);" + "mark (" + HtmlConstants.JREF_SHEETFORM +
            BOArguments.ARG_VIRTUALOBJECTPREFIX + name + ", 2);",
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_UNMARK, env));
        group.addElement (button);
        group.addElement (new BlankElement ());
        button = new InputElement ("BUTT_INVERTMARK", InputElement.INP_BUTTON +
            "\" onClick=\"mark (" + HtmlConstants.JREF_SHEETFORM + name +
            ", 3);" + "mark (" + HtmlConstants.JREF_SHEETFORM +
            BOArguments.ARG_VIRTUALOBJECTPREFIX + name + ", 3);",
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_INVERTMARK, env));
        group.addElement (button);
        return group;
    } // createMarkButtons


    ///////////////////////////////////////////////////////////////////////////
    // internal helper functions
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     * The <A HREF="#env">env</A> property is used for getting the parameters.
     * This property must be set before calling this method. <BR/>
     */
    public void getParameters ()
    {
        // this method may be overwritten in subclasses
    } // getParameters



    ///////////////////////////////////////////////////////////////////////////
    // database functions
    ///////////////////////////////////////////////////////////////////////////



    ///////////////////////////////////////////////////////////////////////////
    // viewing functions
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Create a table representing the frame around an output of one or more
     * lines without a border. <BR/>
     * This method is used to create a frame for info or form view.
     * It calls <A HREF="#createFrame">createFrame</A> and sets the border to 0.
     *
     * @param   representationForm  Kind of representation.
     *
     * @return  The computed table.
     */
    protected TableElement createFrame (int representationForm)
    {
        // call common method with border = 0:
        TableElement table = this.createFrame (representationForm, 0);
        table.ruletype = IOConstants.RULE_GROUPS;
        table.frametypes = IOConstants.FRAME_VOID;
        return table;                   // return the computed table
    } // createFrame


    /**************************************************************************
     * Create a table representing the frame around an output of one or more
     * lines. <BR/>
     * This method is used to create a frame for info or form view.
     * It creates a table with or without border consisting of 2 columns.
     *
     * @param   representationForm  Kind of representation.
     * @param   border              Width of border.
     *
     * @return  The computed table.
     */
    protected TableElement createFrame (int representationForm, int border)
    {
        TableElement table = new TableElement (2);
        table.borderColor = "#FFFFFF";
        table.border = border;
        table.ruletype = IOConstants.RULE_NONE;
        table.frametypes = IOConstants.FRAME_BOX;
        table.width = HtmlConstants.TAV_FULLWIDTH;
        table.cellpadding = 5;
        table.cellspacing = 0;

        return table;                   // return the computed table
    } // createFrame


    /**************************************************************************
     * Show a float to the user. <BR/>
     *
     * @param   table       Table where the property shall be added.
     * @param   fieldName   Name of the form field representing the property.
     * @param   name        Name of the property.
     * @param   type        Type of property.
     * @param   value       Value of the property (float number).
     */
    protected void showProperty (TableElement table, String fieldName,
        String name, int type, float value)
    {
        this.showProperty (table, fieldName, name, Datatypes.DT_INTEGER,
            Float.toString (value));
    } // showProperty


    /**************************************************************************
     * Show a double to the user. <BR/>
     *
     * @param   table       Table where the property shall be added.
     * @param   fieldName   Name of the form field representing the property.
     * @param   name        Name of the property.
     * @param   type        Type of property.
     * @param   value       Value of the property (float number).
     */
    protected void showProperty (TableElement table, String fieldName,
        String name, int type, double value)
    {
        this.showProperty (table, fieldName, name, Datatypes.DT_INTEGER,
            Double.toString (value));
    } // showProperty


    /**************************************************************************
     * Show a integer to the user. <BR/>
     *
     * @param   table       Table where the property shall be added.
     * @param   fieldName   Name of the form field representing the property.
     * @param   name        Name of the property.
     * @param   type        Type of property.
     * @param   value       Value of the property (int number).
     */
    protected void showProperty (TableElement table, String fieldName,
        String name, int type, int value)
    {
        this.showProperty (table, fieldName, name, type,
            Integer.toString (value));
    } // showProperty


    /**************************************************************************
     * Show a date property to the user. <BR/>
     *
     * @param   table       Table where the property shall be added.
     * @param   fieldName   Name of the form field representing the property.
     * @param   name        Name of the property.
     * @param   type        Type of property.
     * @param   value       Value of the property (Date object).
     */
    protected void showProperty (TableElement table, String fieldName,
        String name, int type, Date value)
    {
        String dateStr = "";            // string representing the date part
        String timeStr = "";            // string representing the time part
        String dateTimeStr = "";        // string representing the date/time part

        if (value != null)              // there is a date?
        {
            // get date and time strings:
            dateStr = DateTimeHelpers.dateToString (value);
            timeStr = DateTimeHelpers.timeToString (value);
            dateTimeStr = DateTimeHelpers.dateTimeToString (value);
        } // if there is a date

        switch (type)
        {
            case Datatypes.DT_DATETIME: // date + time
                this.showProperty (table, fieldName, name, type, dateTimeStr);
                break;
            case Datatypes.DT_DATE:     // date
                this.showProperty (table, fieldName, name, type, dateStr);
                break;
            case Datatypes.DT_TIME:     // time
                this.showProperty (table, fieldName, name, type, timeStr);
                break;

            default:                    // unknown field type
                this.showProperty (table, fieldName, name, type, dateTimeStr);
        } // switch type
    } // showProperty


    /**************************************************************************
     * Show an user property to the user. <BR/>
     *
     * @param   table       Table where the property shall be added.
     * @param   fieldName   Name of the form field representing the property.
     * @param   name        Name of the property.
     * @param   type        Type of property.
     * @param   value       Value of the property (User object).
     */
    protected void showProperty (TableElement table, String fieldName,
        String name, int type, User value)
    {
        TableDataElement td;

        switch (type)
        {
            case Datatypes.DT_USER:               // user name
                RowElement tr = new RowElement (2);
                TextElement text;
                GroupElement group = new GroupElement ();
                Element elem;

                // get the actual property and represent it to the user:
                text = new TextElement (name + ": ");

                td = new TableDataElement (text);
                td.classId = CssConstants.CLASS_NAME;
                tr.addElement (td);

                text = new TextElement ("" + value);

                group.addElement (text);

                group.addElement (new InputElement (fieldName, InputElement.INP_HIDDEN, "" + value));
                elem = group;

                tr.addElement (new TableDataElement (elem));
                tr.classId = BOListConstants.LST_CLASSINFOROWS[this.properties++ %
                    BOListConstants.LST_CLASSINFOROWS.length];

                table.addElement (tr);  // add new property to table
                break;

            default:                    // unknown field type
                this.showProperty (table, fieldName, name, type, "" + value);
        } // switch type
    } // showProperty


    /**************************************************************************
     * Show an OID property to the user. <BR/>
     *
     * @param   table       Table where the property shall be added.
     * @param   fieldName   Name of the form field representing the property.
     * @param   name        Name of the property.
     * @param   type        Type of property.
     * @param   value       Value of the property (OID object).
     */
    protected void showProperty (TableElement table, String fieldName,
        String name, int type, OID value)
    {
        switch (type)
        {
            case Datatypes.DT_LINK:               // link
                RowElement tr = new RowElement (2);
                TextElement text;
                GroupElement group = new GroupElement ();
                Element elem;

                // get the actual property and represent it to the user:
                text = new TextElement (name + ": ");

                tr.addElement (new TableDataElement (text));

                text = new TextElement (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_NOTIMPLEMENTED, env));

                group.addElement (text);

                group.addElement (new InputElement (fieldName, InputElement.INP_HIDDEN, "" + value));
                elem = group;

                tr.addElement (new TableDataElement (elem));

                tr.classId = BOListConstants.LST_CLASSINFOROWS[this.properties++ %
                    BOListConstants.LST_CLASSINFOROWS.length];

                table.addElement (tr);  // add new property to table
                break;

            default:                    // unknown field type
                this.showProperty (table, fieldName, name, type, "" + value);
        } // switch type
    } // showProperty


    /**************************************************************************
     * Show a link to the user. <BR/>
     *
     * @param   table       Table where the property shall be added.
     * @param   fieldName   Name of the form field representing the property.
     * @param   name        Name of the property.
     * @param   type        Type of property.
     * @param   valueName   Value showed to the user
     * @param   value       Value of the property (OID object).
     */
    protected void showProperty (TableElement table, String fieldName,
        String name, int type, String valueName, OID value)
    {
        switch (type)
        {
            case Datatypes.DT_LINK:               // link
                RowElement tr = new RowElement (2);
                TextElement text;
                GroupElement group = new GroupElement ();
                Element elem;

                // get the actual property and represent it to the user:
                text = new TextElement (name + ": ");
                tr.addElement (new TableDataElement (text));
                text = new TextElement (valueName);
                LinkElement link = new LinkElement (text,
                    IOHelpers.getShowObjectJavaScriptUrl (value.toString ()));
                group.addElement (link);
                elem = group;
                tr.addElement (new TableDataElement (elem));
                tr.classId = BOListConstants.LST_CLASSINFOROWS[this.properties++ %
                    BOListConstants.LST_CLASSINFOROWS.length];
                table.addElement (tr);  // add new property to table
                break;

            default:                    // unknown field type
                this.showProperty (table, fieldName, name, type, "" + value);
        } // switch type
    } // showProperty


    /**************************************************************************
     * Show a link to the user. <BR/>
     *
     * @param   table       Table where the property shall be added.
     * @param   fieldName   Name of the form field representing the property.
     * @param   name        Name of the property.
     * @param   type        Type of property.
     * @param   valueName   Value showed to the user
     * @param   value       Value of the property (OID object).
     * @param   icon        the icon for the object
     * @param   classId     the classId to use
     */
    protected void showProperty (TableElement table, String fieldName,
                                 String name, int type, String valueName,
                                 OID value, String icon, String classId)
    {
        switch (type)
        {
            case Datatypes.DT_LINK:               // link
                RowElement tr = new RowElement (2);
                TextElement text;
                TableDataElement td;

                // get the actual property and represent it to the user:
                text = new TextElement (name + ": ");
                tr.addElement (new TableDataElement (text));

                GroupElement group = new GroupElement ();
                // has an icon been set
                if (icon != null)
                {
                    ImageElement img = new ImageElement (
                        this.sess.activeLayout.path +
                        BOPathConstants.PATH_OBJECTICONS + icon);
                    group.addElement (img);
                    group.addElement (new BlankElement ());
                } // if (icon != null)
                text = new TextElement (valueName);
                group.addElement (text);
                LinkElement link = new LinkElement (group,
                    IOHelpers.getShowObjectJavaScriptUrl (value.toString ()));
                td = new TableDataElement (link);
                // has a classId been set?
                if (classId != null)
                {
                    td.classId = classId;
                } // if
                tr.addElement (td);
                tr.classId = BOListConstants.LST_CLASSINFOROWS[this.properties++ %
                    BOListConstants.LST_CLASSINFOROWS.length];
                table.addElement (tr);  // add new property to table
                break;
            default:                    // unknown field type
                this.showProperty (table, fieldName, name, type, "" + value);
        } // switch type
    } // showProperty


    /**************************************************************************
     * Show an user/date property to the user. <BR/>
     *
     * @param   table       Table where the property shall be added.
     * @param   fieldName   Name of the form field representing the property.
     * @param   name        Name of the property.
     * @param   type        Type of property.
     * @param   userValue   User value of the property (User object).
     * @param   dateValue   Date value of the property (Date object).
     */
    protected void showProperty (TableElement table, String fieldName,
        String name, int type, User userValue, Date dateValue)
    {
        String dateTimeStr = "";        // string representing the date/time part
        int typeLocal = type;

        if (dateValue != null)          // there is a date value?
        {
            // get date/time string:
            dateTimeStr = DateTimeHelpers.dateTimeToString (dateValue);
        } // if

        // set representation type:
        if (typeLocal == Datatypes.DT_USERDATE) // show user/date property?
        {
            typeLocal = Datatypes.DT_TEXT; // show the constructed string as text
        } // if

        // show the property:
        this.showProperty (table, fieldName, name, typeLocal, "" + userValue +
            ", " + dateTimeStr);
    } // showProperty


    /**************************************************************************
     * Show an image or file property to the user - incl. path info. <BR/>
     *
     * @param   table       Table where the property shall be added.
     * @param   fieldName   Name of the form field representing the property.
     * @param   name        Name of the property.
     * @param   type        Type of property.
     * @param   value       Value of the property (OID object).
     * @param   path        Path where to get the file from.
     */
    protected void showProperty (TableElement table, String fieldName,
                                 String name, int type, String value,
                                 String path)
    {
        RowElement tr;
        TextElement text;
        GroupElement group = new GroupElement ();
        ImageElement img;
        LinkElement linkElem;
        LinkElement linkElem2;
        String pathStr = path;
        TableDataElement td;
        
        // Encode the value which may be the filename which is used later on as request parameter
        String valueEncoded = HtmlHelpers.encodeRequestParameter (value);

        // base path
        String basePath = "";

        // is path given?
        if (pathStr == null)
        {
            // set emtpy
            pathStr = "";
        } // if
        else    // path is not null.
        {
            if (!pathStr.endsWith ("/"))
            {
                pathStr += "/";
            } // if (! path.endsWith (File.separator))
        } // else path is not null

        // set base path according to datatype
        switch (type)
        {
            case Datatypes.DT_PICTURE:
                basePath = this.sess.home + BOPathConstants.PATH_UPLOAD_PICTURES;
                break;

            case Datatypes.DT_THUMBNAIL:
                basePath = this.sess.home + BOPathConstants.PATH_UPLOAD_THUMBS;
                break;

            case Datatypes.DT_IMAGE:
                basePath = this.sess.home + BOPathConstants.PATH_UPLOAD_IMAGES;
                break;

            case Datatypes.DT_FILE:
                basePath = this.sess.home + BOPathConstants.PATH_UPLOAD_FILES;
                break;

            case Datatypes.DT_IMPORTFILE:
                basePath = this.sess.home + BOPathConstants.PATH_UPLOAD_IMPORTFILES;
                break;

            default:
                break;
        } // switch


        switch (type)
        {
            case Datatypes.DT_PICTURE:
            case Datatypes.DT_THUMBNAIL:
            case Datatypes.DT_IMAGE:              // image
                // initialize
                tr = new RowElement (2);
                group = new GroupElement ();

                // get the actual property and represent it to the user:
                text = new TextElement (name + ": ");

                tr.addElement (new TableDataElement (text));

                //check if image is empty
                if (value != null && value.length () > 0)
                {
                    img = new ImageElement (basePath + pathStr + value);
                    group.addElement (img);
                    tr.addElement (new TableDataElement (group));
                } //if
                else
                {
                    tr.addElement (new TableDataElement (new BlankElement ()));
                } //else

                tr.classId = BOListConstants.LST_CLASSINFOROWS[this.properties++ %
                    BOListConstants.LST_CLASSINFOROWS.length];

                table.addElement (tr);  // add new property to table
                break;

            case Datatypes.DT_FILE:               // file
                // initialize
                tr = new RowElement (2);

                // get the actual property and represent it to the user:
                text = new TextElement (name + ": ");

                td = new TableDataElement (text);
                td.classId = CssConstants.CLASS_NAME;
                tr.addElement (td);

                // create link element
                // !!! attention-attention: path info is a special case -
                // because in ibs_attachment_01 the whole path info
                // is stored in db (incl. base path)!!! NO BASE PATH ADDED!
                //
                // To cut of the oid prefix we have to check if the filename
                // length > 18 to avoid out of index exeption.
                // If length > 18 we check if the first 18 chars of the string
                // equal the active oid - if yes we cut them out.
                // Important: The oid prefix is only for files and not for
                // file-attachments in documents

                // due to the new FileAccessServlet implementation
                // the full path is not needed anymore when calling
                // the loadFile and loadWindowFile javascript methods
                // the FileAccessServlet awaits the format
                // <OID>/<filename>
                // we therefore need the last part of the path
                if (pathStr.length () > 0)
                {
                    // note that the path has the format
                    // /<appdir>/upload/files/<oid>/
                    pathStr = pathStr.substring (pathStr.lastIndexOf ("/", pathStr.length () - 2));
                } // if

                TextElement nameElement = null;
                String url = null;

                // check for the length of the filename:
                // check if the first 18 chars equals the oid:
                if (value != null && value.length () > 18 &&
                    this.oid.equals (value.substring (0, 18)))
                {
                    nameElement = new TextElement (value.substring (18));
                } // if
                else
                {
                    nameElement = new TextElement (value);
                } // else

                // check which url shall be executed:
                if (this.getUserInfo ().userProfile.showFilesInWindows)
                {
                    url = IOConstants.URL_JAVASCRIPT +
                        "top.loadWindowFile ('" + pathStr + valueEncoded + "', '" +
                        value + "')";
                } // if
                else
                {
                    url = IOConstants.URL_JAVASCRIPT + "top.loadFile ('" +
                        pathStr + valueEncoded + "');";
                } // else

                // create the link element:
                linkElem = new LinkElement (nameElement, url);

                group = new GroupElement ();
                group.addElement (linkElem);

                // Checking if filename <> null or ""
                // to activate display of downloadbutton
                String filename = value.trim ();

                if (filename != null && filename.length () > 0)
                {
                    group.addElement (new BlankElement ());
                    // allow opening in new window:
                    img = new ImageElement (this.sess.activeLayout.path +
                        BOPathConstants.PATH_GLOBAL + "newwindow.gif");
                    img.classId = CssConstants.CLASS_BUTTONFILE;
                    img.alt = "Open File in New Window";
                    // set the loadFileInNewWindow method as link for the icon:
                    linkElem2 = new LinkElement (img,
                        IOConstants.URL_JAVASCRIPT + "top.loadFileInNewWindow ('" + pathStr + valueEncoded + "');");
                    group.addElement (linkElem2);

                    group.addElement (new BlankElement ());
                    // display download dialog:
                    img = new ImageElement (this.sess.activeLayout.path +
                        BOPathConstants.PATH_OBJECTICONS + "Download.gif");
                    img.classId = CssConstants.CLASS_BUTTONFILE;
                    img.alt = "Download";
/* BB: this is replaced by a the saveFile method that uses the FileAccessServlet
                    linkElem2 = new LinkElement (img, path + value);
*/
                    // set the saveFile method as link behind the icon
                    linkElem2 = new LinkElement (img,
                        IOConstants.URL_JAVASCRIPT + "top.saveFile ('" + pathStr + valueEncoded + "');");
                    group.addElement (linkElem2);
                } // if

                tr.addElement (new TableDataElement (group));

                tr.classId = BOListConstants.LST_CLASSINFOROWS[this.properties++ %
                    BOListConstants.LST_CLASSINFOROWS.length];

                table.addElement (tr);  // add new property to table
                break;

            case Datatypes.DT_WEBDAVFOLDER: // webdav file
                // initialize
                tr = new RowElement (2);

                // get the actual property and represent it to the user:
                text = new TextElement (name + ": ");

                td = new TableDataElement (text);
                td.classId = CssConstants.CLASS_NAME;
                tr.addElement (td);

                group = new GroupElement ();
                if (pathStr.endsWith ("/"))
                {
                    pathStr = pathStr.substring (0, pathStr.length () - 1);
                } // if
                img = new ImageElement (this.sess.activeLayout.path +
                    BOPathConstants.PATH_OBJECTICONS + "WebDav.gif");
                linkElem = new LinkElement (img, pathStr + value);
                group.addElement (linkElem);

                tr.addElement (new TableDataElement (group));

                tr.classId = BOListConstants.LST_CLASSINFOROWS[this.properties++ %
                    BOListConstants.LST_CLASSINFOROWS.length];

                table.addElement (tr);  // add new property to table
                break;

            default:                    // unknown field type
                this.showProperty (table, fieldName, name, type, value);
        } // switch type
    } // showProperty


    /**************************************************************************
     * Show a property to the user. <BR/>
     *
     * @param   table       Table where the property shall be added.
     * @param   fieldName   Name of the form field representing the property.
     * @param   name        Name of the property.
     * @param   type        Type of property.
     * @param   value       Value of the property.
     */
    protected void showProperty (TableElement table, String fieldName,
        String name, int type, String value)
    {
        // Calls the method showProperty with one parameter more because
        // the weblink needs this parameter to not be shown in an additional
        // frame. If the Object is not an attachment and also no hyperlink
        // the last 2 parameters are false. If the object is an attachment
        // and also a hyperlink then this method would not be called but in
        // class Attachment_01 the call of the method which is also called
        // in this method would be called.
        this.showProperty (table, fieldName, name, type, value, false, false);
    } // showProperty


    /**************************************************************************
     * Show a property to the user. <BR/>
     *
     * @param   table       Table where the property shall be added.
     * @param   fieldName   Name of the form field representing the property.
     * @param   name        Name of the property.
     * @param   type        Type of property.
     * @param   value       Value of the property.
     * @param   isWeblink   Is the property a weblink?
     * @param   isInternalWeblink   Is the property an internal weblink?
     */
    protected void showProperty (TableElement table, String fieldName,
                                 String name, int type, String value,
                                 boolean isWeblink, boolean isInternalWeblink)
    {
        RowElement tr = new RowElement (2);
        TextElement text = null;
        GroupElement group = new GroupElement ();
        Element elem = null;
        String valueLocal = value;
        String help = null;             // string for helper purposes
        TableDataElement td;

        // check if value is null or only one blank
        if (valueLocal == null || valueLocal.equals (" "))
        {
            // delete space: oracle is not able to store emptystring
            // emptystring is always represented as " "
            valueLocal = "";
        } // if

        elem = group;

        if (type == Datatypes.DT_HIDDEN)
        {
            group.addElement (new InputElement (fieldName,
                InputElement.INP_HIDDEN, "" + valueLocal));

/* Necessary because in Netscape backgrounds and other stuff are ignored when only a hidden field is set*/
            group.addElement (new BlankElement (1));

            tr.addElement (new TableDataElement (elem));

            tr.classId = BOListConstants.LST_CLASSINFOROWS[this.properties %
                BOListConstants.LST_CLASSINFOROWS.length];

            table.addElement (tr);      // add new property to table
            return;
        } // if
        else if (type == Datatypes.DT_SEPARATOR) // separator between properties
        {
            tr = new RowElement (1);
            group.addElement (new TextElement ("<FONT SIZE=-6>"));
            group.addElement (new LineElement ());
            group.addElement (new TextElement ("</FONT>"));
            td = new TableDataElement (group);
            td.colspan = 2;
            tr.addElement (td);

            tr.classId = BOListConstants.LST_CLASSINFOROWS[this.properties++ %
                BOListConstants.LST_CLASSINFOROWS.length];

            table.addElement (tr);      // add new property to table
            return;
        } // else if separator between properties


        // get the actual property and represent it to the user:
        // ensure that there is nothing displayed if there is no property name
        if (name != null && name.length () > 0)
        {
            elem = new TextElement (name + ": ");
        } // if
        else
        {
            elem = new BlankElement ();
        } // else

        td = new TableDataElement (elem);
        td.classId = CssConstants.CLASS_NAME;
        tr.addElement (td);

        switch (type)
        {
            case Datatypes.DT_BOOL:     // boolean field
                if (valueLocal.equalsIgnoreCase (Datatypes.BOOL_FALSE))
                {
                    text = new TextElement (MultilingualTextProvider
                        .getMessage (AppMessages.MSG_BUNDLE, AppMessages.ML_MSG_BOOLFALSE, env));
                } // if
                else
                {
                    text = new TextElement (MultilingualTextProvider
                        .getMessage (AppMessages.MSG_BUNDLE, AppMessages.ML_MSG_BOOLTRUE, env));
                } // else

                group.addElement (text);
                group.addElement (new InputElement (fieldName, InputElement.INP_HIDDEN, "" + valueLocal));
                break;

            case Datatypes.DT_INTEGER:  // integer field
                text = new TextElement (valueLocal);

                group.addElement (text);
                group.addElement (new InputElement (fieldName, InputElement.INP_HIDDEN, "" + valueLocal));
                break;

            case Datatypes.DT_TEXT:     // text field
                if (valueLocal.length () == 0)
                {
                    group.addElement (new BlankElement (1));
                } // if

                group.addElement (IOHelpers.getTextField (valueLocal));

                // substitute all quotes with the XML-tag when displayed
                // in a hidden field
                help = StringHelpers.replace (valueLocal, "\"", IE302.HCH_QUOT);
                group.addElement (new InputElement (fieldName, InputElement.INP_HIDDEN, "" + help));
                break;

            case Datatypes.DT_TEXTAREA: // text area
                group.addElement (IOHelpers.getTextField (valueLocal));

                // substitute all quotes with the XML-tag when displayed
                // in a hidden field
                help = StringHelpers.replace (valueLocal, "\"", IE302.HCH_QUOT);
                group.addElement (new InputElement (fieldName, InputElement.INP_HIDDEN, "" + help));
                break;

            case Datatypes.DT_HTMLTEXT: // html text area
                group.addElement (IOHelpers.getHtmlTextField (valueLocal));
                break;

            case Datatypes.DT_DATE:     // date field
                text = new TextElement (valueLocal);

                group.addElement (text);
                group.addElement (new InputElement (fieldName, InputElement.INP_HIDDEN, "" + valueLocal));
                break;

            case Datatypes.DT_SELECT:   // selection field
                text = new TextElement (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_NOTIMPLEMENTED, env));

                group.addElement (text);
                group.addElement (new InputElement (fieldName, InputElement.INP_HIDDEN, "" + valueLocal));
                break;

            case Datatypes.DT_SELECTEMPTY: // selection field empty allowed
                text = new TextElement (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_NOTIMPLEMENTED, env));
                group.addElement (text);
                group.addElement (new InputElement (fieldName, InputElement.INP_HIDDEN, "" + valueLocal));
                break;

            case Datatypes.DT_FILE:               // file
                if (this.getUserInfo ().userProfile.showFilesInWindows)
                {
                    elem = new LinkElement (new TextElement (valueLocal),
                           this.sess.home + valueLocal, HtmlConstants.FRM_DOCUMENT);
                } // if
                else
                {
                    elem = new LinkElement (new TextElement (valueLocal),
                           this.sess.home + valueLocal);
                } // else
                group.addElement (elem);
                group.addElement (new InputElement (fieldName, InputElement.INP_HIDDEN, "" + valueLocal));
                break;

            case Datatypes.DT_TYPE:               // object type
                text = new TextElement (valueLocal);

                group.addElement (text);
                group.addElement (new InputElement (fieldName, InputElement.INP_HIDDEN, "" + valueLocal));
                break;

            case Datatypes.DT_USER:               // user name
                text = new TextElement (valueLocal);

                group.addElement (text);
                group.addElement (new InputElement (fieldName, InputElement.INP_HIDDEN, "" + valueLocal));
                break;

            case Datatypes.DT_PASSWORD:           // password
                if (valueLocal != null)      // value exists?
                {                       // block
                    String passwd = "";
                    for (int i = 0; i < valueLocal.length (); i++)
                    {
                        passwd += "*";
                    } // for i
                    text = new TextElement (passwd);
                } // if value exists
                else                    // there is no value
                {
                    text = new TextElement ("");
                } // else

                group.addElement (text);
                group.addElement (new InputElement (fieldName, InputElement.INP_HIDDEN, "" + valueLocal));
                break;

            case Datatypes.DT_NAME:               // object name
                if (valueLocal.length () == 0)
                {
                    group.addElement (new BlankElement (1));
                } // of
                text = new TextElement (valueLocal);

                group.addElement (text);
                group.addElement (new InputElement (fieldName, InputElement.INP_HIDDEN, "" + valueLocal));
                break;

            case Datatypes.DT_DESCRIPTION:        // description
                if (valueLocal.length () == 0)
                {
                    group.addElement (new BlankElement (1));
                } // if

                group.addElement (IOHelpers.getTextField (valueLocal));
                group.addElement (new InputElement (fieldName, InputElement.INP_HIDDEN, "" + valueLocal));
                break;

            case Datatypes.DT_EMAIL:              // email property
                group.addElement (new LinkElement (
                    new TextElement (valueLocal), "mailto:" + valueLocal));
                break;

            case Datatypes.DT_URL:                // URL property
                String masterUrl = "";
                String tempurl = "";

                if (valueLocal.indexOf (":") == -1)
                {
                    tempurl = IOConstants.URL_HTTP + valueLocal;
                } // if
                else
                {
                    tempurl = valueLocal;
                } // if

                if (this.getUserInfo ().userProfile.showFilesInWindows && !isWeblink)
                {
                    // a normal web-url is called
                    masterUrl = IOConstants.URL_JAVASCRIPT + "top.loadWindowLink (\'" + tempurl + "\');";
                    group.addElement (new LinkElement (
                        new TextElement (valueLocal), masterUrl));
                } // if
                else if (isWeblink && isInternalWeblink)
                {
                    String target = null;
                    // the url is a internal weblink
                    // show weblink directly in the window
                    if (tempurl.indexOf ("frame=true") > 0)
                    {
                        target = HtmlConstants.FRM_TOP;
                    } // if
                    masterUrl = tempurl;
                    group.addElement (new LinkElement (new TextElement (valueLocal),
                        masterUrl, target));
                } // else
                else if (isWeblink && !isInternalWeblink)
                {
                    masterUrl = tempurl;
                    group.addElement (new LinkElement (new TextElement (valueLocal),
                        masterUrl, HtmlConstants.FRM_DOCUMENT));
                } // else if
                else
                {
                    masterUrl = IOConstants.URL_JAVASCRIPT + "top.loadLink (\'" + tempurl + "\');";
                    group.addElement (new LinkElement (new TextElement (valueLocal), masterUrl));
                } // else

                break;

            case Datatypes.DT_HIDDEN:             // hidden property
                text = new TextElement (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_NOTIMPLEMENTED, env));

                group.addElement (text);
                group.addElement (new InputElement (fieldName, InputElement.INP_HIDDEN, "" + valueLocal));
                break;

            default:                    // unknown field type
                text = new TextElement (valueLocal);

                group.addElement (text);
                group.addElement (new InputElement (fieldName, InputElement.INP_HIDDEN, "" + valueLocal));
                break;
        } // switch type

        tr.addElement (new TableDataElement (group));

        tr.classId = BOListConstants.LST_CLASSINFOROWS[this.properties++ %
            BOListConstants.LST_CLASSINFOROWS.length];

        table.addElement (tr);          // add new property to table
    } // showProperty


    /**************************************************************************
     * Show a property to the user within a form. <BR/>
     *
     * @param   table       Table where the property shall be added.
     * @param   fieldName   Name of the form field representing the property.
     * @param   name        Name of the property.
     * @param   type        Type of property.
     * @param   value       Value of the property.
     */
    protected void showFormProperty (TableElement table, String fieldName,
        String name, int type, String value)
    {
        this.showFormProperty (table, fieldName, name, type, value, null, null, 0);
    } // showFormProperty


    /**************************************************************************
     * Show a property to the user within a form
     * and set the match type. <BR/>
     *
     * @param   table       Table where the property shall be added.
     * @param   fieldName   Name of the form field representing the property.
     * @param   name        Name of the property.
     * @param   type        Type of property.
     * @param   value       Value of the property.
     * @param   matchType   Match type of the property.
     */
    protected void showFormProperty (TableElement table, String fieldName,
        String name, int type, String value, int matchType)
    {
        this.showFormProperty (table, fieldName, name, type,
                value, null, null, matchType, false);
    } // showFormProperty

    /**************************************************************************
     * Show a float property to the user within a form. <BR/>
     *
     * @param   table       Table where the property shall be added.
     * @param   fieldName   Name of the form field representing the property.
     * @param   name        Name of the property.
     * @param   type        Type of property.
     * @param   value       Value of the property (int number).
     */
    protected void showFormProperty (TableElement table, String fieldName,
        String name, int type, float value)
    {
        this.showFormProperty (table, fieldName, name, Datatypes.DT_NUMBER,
            Float.toString (value), null, null, 0);
    } // showFormProperty


    /**************************************************************************
     * Show a double property to the user within a form. <BR/>
     *
     * @param   table       Table where the property shall be added.
     * @param   fieldName   Name of the form field representing the property.
     * @param   name        Name of the property.
     * @param   type        Type of property.
     * @param   value       Value of the property (int number).
     */
    protected void showFormProperty (TableElement table, String fieldName,
        String name, int type, double value)
    {
        this.showFormProperty (table, fieldName, name, Datatypes.DT_NUMBER,
            Double.toString (value), null, null, 0);
    } // showFormProperty


    /**************************************************************************
     * Show a number range property to the user within a form. <BR/>
     *
     * @param   table       Table where the property shall be added.
     * @param   fieldName   Name of the form field representing the property.
     * @param   name        Name of the property.
     * @param   type        Type of property.
     * @param   value1      lower bound of the property.
     * @param   value2      upper bound of the property.
     */
    protected void showFormProperty (TableElement table, String fieldName,
        String name, int type, double value1, double value2)
    {
        this.showFormRangeProperty (table, fieldName, name, Datatypes.DT_NUMBERRANGE,
            Double.toString (value1), Double.toString (value2));
    } // showFormProperty


    /**************************************************************************
     * Show an integer property to the user within a form. <BR/>
     *
     * @param   table       Table where the property shall be added.
     * @param   fieldName   Name of the form field representing the property.
     * @param   name        Name of the property.
     * @param   type        Type of property.
     * @param   value       Value of the property (int number).
     */
    protected void showFormProperty (TableElement table, String fieldName,
        String name, int type, int value)
    {
        this.showFormProperty (table, fieldName, name, type,
            Integer.toString (value), null, null, 0);
    } // showFormProperty


    /**************************************************************************
     * Show an integer range property to the user within a form. <BR/>
     *
     * @param   table       Table where the property shall be added.
     * @param   fieldName   Name of the form field representing the property.
     * @param   name        Name of the property.
     * @param   type        Type of property.
     * @param   value1      lower bound of the property (integer).
     * @param   value2      upper bound of the property (integer).
     */
    protected void showFormProperty (TableElement table, String fieldName,
        String name, int type, int value1, int value2)
    {
        this.showFormRangeProperty (table, fieldName, name, Datatypes.DT_INTEGERRANGE,
            Integer.toString (value1), Integer.toString (value2));
    } // showFormProperty


    /**************************************************************************
     * Show a money range property to the user within a form. <BR/>
     *
     * @param   table       Table where the property shall be added.
     * @param   fieldName   Name of the form field representing the property.
     * @param   name        Name of the property.
     * @param   type        Type of property.
     * @param   value1      lower bound of the property.
     * @param   value2      upper bound of the property.
     */
    protected void showFormProperty (TableElement table, String fieldName,
        String name, int type, long value1, long value2)
    {
        this.showFormRangeProperty (table, fieldName, name, Datatypes.DT_MONEYRANGE,
            Long.toString (value1), Long.toString (value2));
    } // showFormProperty


    /**************************************************************************
     * Show a date range property to the user within a form. <BR/>
     *
     * @param   table       Table where the property shall be added.
     * @param   fieldName   Name of the form field representing the property.
     * @param   name        Name of the property.
     * @param   type        Type of property.
     * @param   value1      lower bound of the property (date).
     * @param   value2      upper bound of the property (date).
     */
    protected void showFormProperty (TableElement table, String fieldName,
        String name, int type, Date value1, Date value2)
    {
        String strVal1;                 // string representation of value1
        String strVal2;                 // string representation of value2

        String dateStr1 = "";           // string representing the date part
        String timeStr1 = "";           // string representing the time part
        String dateTimeStr1 = "";       // string representing the date/time part
        String dateStr2 = "";           // string representing the date part
        String timeStr2 = "";           // string representing the time part
        String dateTimeStr2 = "";       // string representing the date/time part

        // convert first value to string:
        if (value1 != null)             // there is a value?
        {
            // get date and time strings:
            dateStr1 = DateTimeHelpers.dateToString (value1);
            timeStr1 = DateTimeHelpers.timeToString (value1);
            dateTimeStr1 = DateTimeHelpers.dateTimeToString (value1);
        } // if there is a date

        // convert second value to string:
        if (value2 != null)             // there is a value?
        {
            // get date and time strings:
            dateStr2 = DateTimeHelpers.dateToString (value2);
            timeStr2 = DateTimeHelpers.timeToString (value2);
            dateTimeStr2 = DateTimeHelpers.dateTimeToString (value2);
        } // if there is a date

        switch (type)
        {
            case Datatypes.DT_DATERANGE:          // date range field
                strVal1 = dateStr1;
                strVal2 = dateStr2;
                break;

            case Datatypes.DT_TIMERANGE:          // time range field
                strVal1 = timeStr1;
                strVal2 = timeStr2;
                break;

            case Datatypes.DT_DATETIMERANGE:      // datetime range field
                strVal1 = dateTimeStr1;
                strVal2 = dateTimeStr2;
                break;

            default:                    // unknown field type
                // call common form property method:
                this.showFormProperty (table, fieldName, name, Datatypes.DT_DATETIME,
                    dateTimeStr1, null, null, 0);
                return;                 // abort this method
        } // switch type

        this.showFormRangeProperty (table, fieldName, name, type, strVal1, strVal2);
    } // showFormProperty


    /**************************************************************************
     * Show a range property to the user within a form. <BR/>
     *
     * @param   table       Table where the property shall be added.
     * @param   fieldName   Name of the form field representing the property.
     * @param   name        Name of the property.
     * @param   type        Type of property. (INTEGER / TIME / DATERANGE)
     * @param   value1      lower bound of the property (string).
     * @param   value2      upper bound of the property (string).
     */
    protected void showFormRangeProperty (TableElement table, String fieldName,
                                          String name, int type, String value1,
                                          String value2)
    {
        int size;                       // size of each input field
        int maxLength;                  // maximum length of content within
                                        // each input field
        String classId = null;          // class id for HTML element
        String standardFormat = "";        // standard format for input field
        boolean addRelations = true;      // show relations?
        TableDataElement td;

        // a word to the form field restrictions in this case:
        // due to the complications using 2 fields as one datatype
        // some constraints must be taken for granted.
        // so the restriction evaluation (e.g. upper or lower bounds)
        // will in this case be performed for both fields.
        FormFieldRestriction restriction;

        // a word to the form field relations in this case:
        // form field relations for range types will be set
        // automatically (first field < second field).
        FormFieldRelation relation;

        // check field restrictions - it is possible that they are
        // not set yet
        if (this.formFieldRestriction == null)
        {
            // not set yet
            restriction = new FormFieldRestriction ();
        } // if
        else
        {
            // get it from objects attribute
            restriction = this.formFieldRestriction;
        } // else

        // set 1st fields name in restriction
        restriction.name = fieldName;

        // set field sizes:
        switch (type)
        {
            case Datatypes.DT_INTEGERRANGE: // integer range field
                size = 10;
                maxLength = 10;
                classId = CssConstants.CLASS_INTEGER;
                // set data type of form field in restriction field
                restriction.dataType = Datatypes.DT_INTEGER;
                // do not add relation for this datatype, because it is not
                // implemented yet
                addRelations = false;
                break;

            case Datatypes.DT_NUMBERRANGE: // number range field
                size = 10;
                maxLength = 15;
                classId = CssConstants.CLASS_NUMBER;
                // set data type of form field in restriction field
                restriction.dataType = Datatypes.DT_NUMBER;
                // do not add relation for this datatype, because it is not
                // implemented yet
                addRelations = false;
                break;

            case Datatypes.DT_MONEYRANGE: // money range field
                size = 10;
                maxLength = 15;
                classId = CssConstants.CLASS_MONEY;
                // set data type of form field in restriction field
                restriction.dataType = Datatypes.DT_MONEY;
                // do not add relation for this datatype, because it is not
                // implemented yet
                addRelations = false;
                break;

            case Datatypes.DT_DATERANGE: // date range field
                size = 10;
                maxLength = 10;
                classId = CssConstants.CLASS_DATE;
                // set data type of form field in restriction field
                restriction.dataType = Datatypes.DT_DATE;
                // set format information for input field
                standardFormat = " (" + MultilingualTextProvider.getText (
                        BOTokens.TOK_BUNDLE, BOTokens.ML_STANDARDDATEFORMAT, env)  + ")";
                break;

            case Datatypes.DT_TIMERANGE: // time range field
                size = 5;
                maxLength = 5;
                classId = CssConstants.CLASS_TIME;
                // set data type of form field in restriction field
                restriction.dataType = Datatypes.DT_TIME;
                // set format information for input field
                standardFormat = " (" + MultilingualTextProvider.getText (
                        BOTokens.TOK_BUNDLE, BOTokens.ML_STANDARDTIMEFORMAT, env)  + ")";
                break;

            case Datatypes.DT_DATETIMERANGE: // datetime range field
                size = 15;
                maxLength = 15;
                classId = CssConstants.CLASS_DATE;
                // set data type of form field in restriction field
                restriction.dataType = Datatypes.DT_DATETIME;
                // set format information for input field
                standardFormat = " (" +
                        MultilingualTextProvider.getText (
                            BOTokens.TOK_BUNDLE, BOTokens.ML_STANDARDDATEFORMAT, env) + " " +
                        MultilingualTextProvider.getText (
                            BOTokens.TOK_BUNDLE, BOTokens.ML_STANDARDTIMEFORMAT, env)  + ")";
                break;

            default:                    // unknown fiel type
                size = 20;
                maxLength = 63;
                restriction.dataType = Datatypes.DT_UNKNOWN;
                break;
        } // switch type

        // build JavaScript code for form field restrictions
        // of 1st field
        StringBuffer restrictionScript = restriction.buildRestrictScriptCode (env);
        // now expand form field restriction for the onSubmit functionality
        // of the form
        this.expandFormFieldRestrictions (restrictionScript);

        // create form field relation
        relation = new FormFieldRelation (restriction.dataType,
            fieldName, name + " " + MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_LEFTFIELD, env),
            fieldName + BOArguments.ARG_RANGE_EXTENSION,
            name + " " + MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_RIGHTFIELD, env), UtilConstants.FF_REL_LOWEREQUAL);

        // check if relations shall be added
        if (addRelations)
        {
            // add to relations
            this.addFormFieldRelation (relation);
        } // if

        // create fields
        RowElement tr = new RowElement (2);
        TextElement text;
        Element elem;
        InputElement input;
        GroupElement group = new GroupElement ();

        // get the actual property and represent it to the user:
        text = new TextElement (name + ": ");

        td = new TableDataElement (text);
        td.classId = CssConstants.CLASS_NAME;
        tr.addElement (td);

        // first value:
        input = new InputElement (fieldName, InputElement.INP_TEXT, value1);
        // set field size:
        input.setSize (restriction, size, maxLength);

        // set the class id:
        if (classId != null)
        {
            input.classId = classId;
        } // if

        // add restricition script
        input.onChange = restrictionScript.toString ();
        group.addElement (input);

        // standarddate format
        text = new TextElement (standardFormat);
        group.addElement (text);

        // field separator
        group.addElement (new TextElement (" - "));

        // second value:
        input = new InputElement (fieldName + BOArguments.ARG_RANGE_EXTENSION,
            InputElement.INP_TEXT, value2);

        // set name of 2nd field in restriction
        restriction.name = fieldName + BOArguments.ARG_RANGE_EXTENSION;
        // build JavaScript code for form field restrictions
        // of 2nd field
        restrictionScript = restriction.buildRestrictScriptCode (env);

        // now expand form field restriction for the onSubmit functionality
        // of the form
        this.expandFormFieldRestrictions (restrictionScript);
        // set field size:
        input.setSize (restriction, size, maxLength);

        // set the class id:
        if (classId != null)
        {
            input.classId = classId;
        } // if

        // add restricition script
        input.onChange = restrictionScript.toString ();
        group.addElement (input);

        // standarddate format
        text = new TextElement (standardFormat);
        group.addElement (text);

        elem = group;

        tr.addElement (new TableDataElement (elem));

        tr.classId = BOListConstants.LST_CLASSINFOROWS[this.properties++ %
            BOListConstants.LST_CLASSINFOROWS.length];

        table.addElement (tr);  // add new property to table
    } // showFormRangeProperty


    /**************************************************************************
     * Show a User property to the user within a form. <BR/>
     *
     * @param   table       Table where the property shall be added.
     * @param   fieldName   Name of the form field representing the property.
     * @param   name        Name of the property.
     * @param   type        Type of property.
     * @param   value       Value of the property (User object).
     */
    protected void showFormProperty (TableElement table, String fieldName,
        String name, int type, User value)
    {
        this.showFormProperty (table, fieldName, name, type,
            "" + value.id, null, null, 0);
    } // showFormProperty


    /**************************************************************************
     * Show a Date property to the user within a form. <BR/>
     *
     * @param   table       Table where the property shall be added.
     * @param   fieldName   Name of the form field representing the property.
     * @param   name        Name of the property.
     * @param   type        Type of property.
     * @param   value       Value of the property (Date object).
     */
    protected void showFormProperty (TableElement table, String fieldName,
                                     String name, int type, Date value)
    {
        String dateStr = "";            // string representing the date part
        String timeStr = "";            // string representing the time part
        String dateTimeStr = "";        // string representing the date/time part

        if (value != null)              // there is a date?
        {
            // get date and time strings:
            dateStr = DateTimeHelpers.dateToString (value);
            timeStr = DateTimeHelpers.timeToString (value);
            dateTimeStr = DateTimeHelpers.dateTimeToString (value);
        } // if there is a date

        switch (type)
        {
            case Datatypes.DT_DATETIME:           // date + time field
                // a word to the form field restrictions in this case:
                // due to the complications using 2 fields as one datatype
                // some constraints must be taken for granted.
                // so the restriction evaluation (e.g. upper or lower bounds)
                // will in this case only be be performed for the time field.
                // the date field will only be checked for correct input.
                // assuming that the user inserts data from the left to
                // the right this is the right approach, but nevertheless
                // somtimes the user will insert data in another sequnce.
                // for this case the insertions must also be checked before
                // submitting the form. sad but true.
                //
                // BB: the introduction of the XMLViewer Object made it
                // neccessary, that the date part in a datetime field
                // can be left empty
                FormFieldRestriction restriction;

                // check field restrictions - it is possible that they are
                // not set yet
                if (this.formFieldRestriction == null)
                {
                    // not set yet
                    restriction = new FormFieldRestriction ();
                } // if
                else
                {
                    // get it from objects attribute
                    restriction = this.formFieldRestriction;
                } // else

                // set name and data type of form field in restriction field
                restriction.name = fieldName;
                restriction.dataType = type;
                // build JavaScript code for form field restrictions
                StringBuffer restrictionScript = restriction.buildRestrictScriptCode (env);

                // now expand form field restriction for the onSubmit functionality
                // of the form
                this.expandFormFieldRestrictions (restrictionScript);

                // create elements
                RowElement tr = new RowElement (2);
                TextElement text;
                Element elem;
                InputElement input;
                GroupElement group = new GroupElement ();

                // get the actual property and represent it to the user:
                text = new TextElement (name + ": ");

                tr.addElement (new TableDataElement (text));

                // create date field - extension of field is '_d'
                input = new InputElement (fieldName + BOArguments.ARG_DATE_EXTENSION,
                    InputElement.INP_TEXT, dateStr);
                // set view-size of input field
                if (restriction.viewLength > 0)
                {
                    input.size = restriction.viewLength;    // set length
                } // if
                else
                {
                    input.size = 10;                        // set default
                } // else
                // set maximum size of input fields content
                if (restriction.maxLength > 0)
                {
                    input.maxlength = restriction.maxLength;
                } // if
                else
                {
                    input.maxlength = 10;
                } // else

                input.classId = CssConstants.CLASS_DATE;
                input.onChange = "top.iD (" + HtmlConstants.JREF_SHEETFORM +
                    fieldName + BOArguments.ARG_DATE_EXTENSION + ", " +
                    restriction.emptyAllowed + ")";

                // now expand form field restriction for the onSubmit functionality
                // of the form
                this.expandFormFieldRestrictions (input.onChange);

                group.addElement (input);

                // create time field - extension of field is '_t'
                input = new InputElement (fieldName + BOArguments.ARG_TIME_EXTENSION,
                    InputElement.INP_TEXT, timeStr);
                // set view-size of input field
                if (restriction.viewLength > 0)
                {
                    input.size = restriction.viewLength;    // set length
                } // if
                else
                {
                    input.size = 5;                        // set default
                } // else
                // set maximum size of input fields content
                if (restriction.maxLength > 0)
                {
                    input.maxlength = restriction.maxLength;
                } // if
                else
                {
                    input.maxlength = 5;
                } // else

                input.classId = CssConstants.CLASS_TIME;
                input.onChange = "top.iT (" + HtmlConstants.JREF_SHEETFORM +
                    fieldName + BOArguments.ARG_TIME_EXTENSION + ", " +
                    restriction.emptyAllowed + ")";
//                input.onChange = restrictionScript; // add restriction script

                group.addElement (input);

                // get the actual property and represent it to the user:
                text = new TextElement (" (" +
                        MultilingualTextProvider.getText (
                            BOTokens.TOK_BUNDLE, BOTokens.ML_STANDARDDATEFORMAT, env) + " - " +
                        MultilingualTextProvider.getText (
                            BOTokens.TOK_BUNDLE, BOTokens.ML_STANDARDTIMEFORMAT, env) + ")");

                group.addElement (text);
                elem = group;

                tr.addElement (new TableDataElement (elem));

                tr.classId = BOListConstants.LST_CLASSINFOROWS[this.properties++ %
                    BOListConstants.LST_CLASSINFOROWS.length];

                table.addElement (tr);  // add new property to table

                // reset restrictions for this field - this avoids that the next
                // method call uses previous restrictions
                this.formFieldRestriction = null;
                break;

            case Datatypes.DT_DATE:               // date field
                this.showFormProperty (table, fieldName, name, type,
                    dateStr, null, null, 0);
                break;

            case Datatypes.DT_TIME:               // time field
                this.showFormProperty (table, fieldName, name, type,
                    timeStr, null, null, 0);
                break;

            default:                    // unknown field type
                this.showFormProperty (table, fieldName, name, type,
                    dateTimeStr, null, null, 0);
                break;
        } // switch type
    } // showFormProperty


    /**************************************************************************
     * Show a radio button to the user within a form. <BR/>
     *
     * @param   table       Table where the property shall be added.
     * @param   fieldName   Name of the form field representing the property.
     * @param   name        Name of the property.
     * @param   type        Type of property.
     * @param   value       Value of the property (Date object).
     * @param   checked     Shall the radio button be checked?
     */
    protected void showFormProperty (TableElement table, String fieldName,
        String name, int type, String value, boolean checked)
    {
        TableDataElement td;

        switch (type)
        {
            case Datatypes.DT_RADIO:              // radio button
                RowElement tr = new RowElement (2);
                Element elem;
                InputElement input;
                TextElement text;

                // get the actual property and represent it to the user:
                text = new TextElement (name + ": ");

                td = new TableDataElement (text);
                td.classId = CssConstants.CLASS_NAME;
                tr.addElement (td);

                input = new InputElement (fieldName, InputElement.INP_RADIO, value);
                input.checked = checked;
                input.classId = CssConstants.CLASS_RADIO;
                elem = input;

                tr.addElement (new TableDataElement (elem));

                tr.classId = BOListConstants.LST_CLASSINFOROWS[this.properties++ % BOListConstants.LST_CLASSINFOROWS.length];

                table.addElement (tr);  // add new property to table
                break;

            default:                    // unsupported field type
                this.showFormProperty (table, fieldName, name, type,
                    value, null, null, 0);
                break;
        } // switch type
    } // showFormProperty


    /**************************************************************************
     * Show radio buttons to the user within a form. <BR/>
     *
     * @param   table       Table where the property shall be added.
     * @param   fieldName   Name of the form field representing the property.
     * @param   name        Name of the property.
     * @param   tokens      labels of the parts of the property.
     * @param   type        Type of property.
     * @param   direction   direction of the parts of the property
     *                      = AppConstants.DIR_HORIZONTAL or
     *                      AppConstants.DIR_VERTICAL
     * @param   checked     which radio button shall be checked?
     *                      (starting with 0)
     * @param   afterText   Text to be displayed after the property.
     */
    protected void showFormProperty (TableElement table, String fieldName,
        String name, String [] tokens, int type, int direction, int checked,
        String afterText)
    {
        RowElement tr = new RowElement (2);
        GroupElement gr = new GroupElement ();
        InputElement input;
        TextElement text;
        TableDataElement td;

        switch (type)
        {
            case Datatypes.DT_RADIO:              // radio button
                // get the actual property and represent it to the user:
                text = new TextElement (name + ": ");
                td = new TableDataElement (text);
                td.classId = CssConstants.CLASS_NAME;
                tr.addElement (td);

                for (int i = 0; i < tokens.length; i++)
                {
                    // get the actual property and represent it to the user:
                    input = new InputElement (fieldName, InputElement.INP_RADIO, tokens [i]);
                    input.checked = i == checked;
                    input.classId = CssConstants.CLASS_RADIO;

                    gr.addElement (input);

                    text = new TextElement (" " + tokens [i]);

                    gr.addElement (text);

                    if (direction == AppConstants.DIR_VERTICAL)
                    {
                        gr.addElement (new NewLineElement ());
                    } // if
                    else
                    {
                        gr.addElement (new BlankElement (2));
                    } // else
                } // for i

                text = new TextElement (afterText);
                gr.addElement (text);
                tr.addElement (new TableDataElement (gr));

                tr.classId = BOListConstants.LST_CLASSINFOROWS[this.properties++ %
                    BOListConstants.LST_CLASSINFOROWS.length];

                table.addElement (tr);  // add new property to table
                break;
            default:                    // unsupported field type
                this.showFormProperty (table, fieldName, tokens [0], type,
                    "", null, null, 0);
                break;
        } // switch type
    } // showFormProperty


    /**************************************************************************
     * Show an upload file to be changed to the user within a form. <BR/>
     *
     * @param   table       Table where the property shall be added.
     * @param   fieldName   Name of the form field representing the property.
     * @param   name        Name of the property.
     * @param   type        Type of property.
     * @param   value       Value of the property (file name).
     * @param   path        Path of the file (additional to upload path)
     *                      = subdirectory (ies) of uploadpath
     */
    protected void showFormProperty (TableElement table, String fieldName,
                                     String name, int type, String value,
                                     String path)
    {
        String absPath = this.app.p_system.p_m2AbsBasePath;
        String webPath = this.sess.home;
        String classId = null;
        String valueLocal = value;
        TableDataElement td;

        // check if value is only one blank (" ")
        if (valueLocal.equals (" "))
        {
            // delete space: ms sql server is not able to store empty string
            // empty string is always represented as " "
            valueLocal = "";
        } // if


        // differ upload types and set path infos
        switch (type)
        {
            // differ between upload types
            case Datatypes.DT_IMAGE:              // image
/*
                absPath += BOPathConstants.PATH_UPLOAD_ABS_IMAGES;
                webPath += BOPathConstants.PATH_UPLOAD_IMAGES;
*/
                absPath += BOPathConstants.PATH_UPLOAD_ABS_FILES;
                webPath += BOPathConstants.PATH_UPLOAD_FILES;
                classId = CssConstants.CLASS_IMAGE;
                break;

            case Datatypes.DT_PICTURE:            // object picture
                absPath += BOPathConstants.PATH_UPLOAD_ABS_PICTURES;
                webPath += BOPathConstants.PATH_UPLOAD_PICTURES;
                classId = CssConstants.CLASS_PICTURE;
                break;

            case Datatypes.DT_THUMBNAIL:          // object thumbnail
                absPath += BOPathConstants.PATH_UPLOAD_ABS_THUMBS;
                webPath += BOPathConstants.PATH_UPLOAD_THUMBS;
                classId = CssConstants.CLASS_THUMBNAIL;
                break;

            case Datatypes.DT_FILE:               // file
                absPath += BOPathConstants.PATH_UPLOAD_ABS_FILES;
                webPath += BOPathConstants.PATH_UPLOAD_FILES;
                classId = CssConstants.CLASS_FILE;
                break;

            case Datatypes.DT_IMPORTFILE:              // image
                absPath += BOPathConstants.PATH_UPLOAD_ABS_IMPORTFILES;
                webPath += BOPathConstants.PATH_UPLOAD_IMPORTFILES;
                classId = CssConstants.CLASS_IMPORTFILE;
                break;

            default:                    // unsupported field type
                this.showFormProperty (table, fieldName, name, type,
                    valueLocal, null, null, 0);
                return;
        } // switch type

        // set type of form:
        this.actForm.enctype = "multipart/form-data";

        // set additional path info (given parameter)
        if (path != null)
        {
            // set absolute path in userinfo (needed to create dir)
            absPath += path;
            // set webpath
            webPath += path;
        } // if

        FormFieldRestriction restriction;

        // check field restriction - it is possible that they are not set yet
        if (this.formFieldRestriction == null)
        {
            // not set yet
            restriction = new FormFieldRestriction ();
        } // if
        else
        {
            // get it from objects attribute
            restriction = this.formFieldRestriction;
        } // else

        // set name and data type of form field in restriction field
        restriction.name = fieldName;
        restriction.dataType = type;
        // build JavaScript code for form field restrictions
        StringBuffer restrictionScript = restriction.buildRestrictScriptCode (env);

        // now expand form field restriction for the onSubmit functionality
        // of the form
        this.expandFormFieldRestrictions (restrictionScript);

        RowElement tr = new RowElement (2);
        InputElement input;
        TextElement text;
        GroupElement group = new GroupElement ();

        // get the actual property and represent it to the user:
        text = new TextElement (name + ": ");

        td = new TableDataElement (text);
        td.classId = CssConstants.CLASS_NAME;
        tr.addElement (td);

        // show actual field value:
        if (type == Datatypes.DT_FILE)
        {
            if (valueLocal.length () > 18)
            {
                if (valueLocal.substring (0, 18).equals (this.oid.toString ()))
                {
                    text = new TextElement (valueLocal.substring (18));
                } // if
                else
                {
                    text = new TextElement (valueLocal);
                } //else
            } // if
            else
            {
                text = new TextElement (valueLocal);
            } // else
        } // if
        else
        {
            text = new TextElement (valueLocal);
        } // else
        group.addElement (text);
        group.addElement (new NewLineElement ());

        input = new InputElement (fieldName, InputElement.INP_FILE, valueLocal);
        // set field size:
        input.setSize (restriction, 30, 63);

        // add restriction script
        restrictionScript = restriction.buildRestrictScriptCode (env);

        // set the css class id if necessary:
        if (classId != null)
        {
            input.classId = classId;
        } // if

        input.onChange = restrictionScript.toString ();

        group.addElement (input);

        // now expand form field restriction for the onSubmit functionality
        // of the form
        this.expandFormFieldRestrictions (restrictionScript);

        // show hidden field - wich holds path info
        // name of field: filenames field + extension
        if (!absPath.endsWith (File.separator + File.separator))
        {
            absPath += File.separator + File.separator;
        } // if
        if (!webPath.endsWith ("/"))
        {
            webPath += "/";
        } // else
        input = new InputElement (fieldName + AppConstants.DT_FILE_PATH_EXT,
            InputElement.INP_HIDDEN, absPath);
        group.addElement (input);
        input = new InputElement (fieldName + AppConstants.DT_WWW_PATH_EXT,
            InputElement.INP_HIDDEN, webPath);
        group.addElement (input);

        // store fieldname for file upload in users session info

        // create new vector element - to hold filename/field
        FilenameElement file = new FilenameElement ();
        // set fields name, init files name, changed flag, paths
        file.filenameField = fieldName;
        file.filename = "";
        file.changed = false;
        file.uploadPath = webPath;
        file.uploadPathAbs = absPath;

        // add element to vector - stored in userinfo
        this.getUserInfo ().filenames.addElement (file);

        // construct table row:
        tr.addElement (new TableDataElement (group));

        tr.classId = BOListConstants.LST_CLASSINFOROWS[this.properties++ %
            BOListConstants.LST_CLASSINFOROWS.length];

        // add table row to table:
        table.addElement (tr);  // add new property to table
    } // showFormProperty


    /**************************************************************************
     * Show a field for searching with a function to perform the search
     * directly. <BR/>
     *
     * @param   table       Table where the property shall be added.
     * @param   fieldName   Name of the form field representing the property.
     * @param   name        Name of the property.
     * @param   type        Type of property.
     * @param   value       Value of the property.
     * @param   oidValue    oid of object wich is represented by value
     * @param   url         URL which performs the search (with
     *                      &&lt;fieldname>=value as the value of the search
     *                      field).
     * @param   target      Target frame where the search shall be performed.
     *                      <BR><CODE>null</CODE> => default frame.
     */
    protected void showFormProperty (TableElement table, String fieldName,
                                     String name, int type, String value,
                                     String oidValue, String url, String target)
    {
        String urlLocal = url;          // value for local assignments
        String targetLocal = target;    // value for local assignments
        TableDataElement td;

        switch (type)
        {
            case Datatypes.DT_SEARCHTEXTFUNCTION: // search text property
                                        // with function to perform directly
                RowElement tr = new RowElement (2);
                InputElement input;
                TextElement text;
                GroupElement gel;
                SelectElement select;
                String selectedValue = value + "\" onFocus=\"this.select ();";

                FormFieldRestriction restriction;

                // check field restriction - it is possible that they are not set yet
                if (this.formFieldRestriction == null)
                {
                    // not set yet
                    restriction = new FormFieldRestriction ();
                } // if
                else
                {
                    // get it from objects attribute
                    restriction = this.formFieldRestriction;
                } // else

                // set name and data type of form field in restriction field
                restriction.name = fieldName;
                restriction.dataType = type;
                // build JavaScript code for form field restrictions
                StringBuffer restrictionScript = restriction.buildRestrictScriptCode (env);

                // now expand form field restriction for the onSubmit
                // functionality of the form
                this.expandFormFieldRestrictions (restrictionScript);

                // get the actual property and represent it to the user:
                text = new TextElement (name + ": ");

                td = new TableDataElement (text);
                td.classId = CssConstants.CLASS_NAME;
                tr.addElement (td);

                // construct the readonly field:
                gel = new GroupElement ();
                input = new InputElement (fieldName,
                    InputElement.INP_TEXT, selectedValue);
                input.size = 50;
                input.maxlength = 256;
                input.readonly = true;
                gel.addElement (input);

                gel.addElement (new NewLineElement ());

                // show matchtypes selection box
                select = new SelectElement (fieldName + BOArguments.ARG_MATCHTYPE_EXTENSION, false);
                select.addOption (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MATCHSUBSTRING, env), BOConstants.MATCH_SUBSTRING);
                select.addOption (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MATCHEXACT, env), BOConstants.MATCH_EXACT);
                select.addOption (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MATCHSOUNDEX, env), BOConstants.MATCH_SOUNDEX);
                gel.addElement (select);

                // construct the search field:
                input = new InputElement (fieldName + BOArguments.ARG_NAME_EXTENSION,
                    InputElement.INP_TEXT, "");
                // set field size:
                input.setSize (restriction, 23, 255);

                // set onChange script:
                // add restriction script
                input.onChange = restrictionScript.toString ();
                gel.addElement (input);

                // create the hidden element that holds the OID
                input = new InputElement (
                    fieldName + BOArguments.ARG_OID_EXTENSION,
                    InputElement.INP_HIDDEN, "" + oidValue);
                gel.addElement (input);

                // create the search button
                input = new InputElement (fieldName + "_BT",
                    InputElement.INP_BUTTON, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_DTSEARCHTEXTSEARCH, env));

                if (targetLocal == null)     // no target set?
                {
                    targetLocal = "parent";
                } // if no target set
                else                    // target set
                {
                    targetLocal = "parent." + targetLocal;
                } // else target set

                // construct the onClick script for the search button:
                targetLocal = " (" + targetLocal + " == top ? \'top\' : " + targetLocal + ".name)";
                urlLocal = "\'" + urlLocal +
                    HttpArguments.createArg (BOArguments.ARG_FIELDNAME, fieldName) +
                    HttpArguments.createArgNoEncode (fieldName,
                        "\' + escape (" + HtmlConstants.JREF_SHEETFORM +
                        fieldName + BOArguments.ARG_NAME_EXTENSION +
                        ".value) + \'") +
                    HttpArguments.createArgNoEncode (
                        fieldName + BOArguments.ARG_MATCHTYPE_EXTENSION,
                        "\' + " + HtmlConstants.JREF_SHEETFORM +
                        fieldName + BOArguments.ARG_MATCHTYPE_EXTENSION +
                        ".options[" + HtmlConstants.JREF_SHEETFORM +
                        fieldName + BOArguments.ARG_MATCHTYPE_EXTENSION +
                        ".selectedIndex].value");
                input.onClick = "top.callUrl (" + urlLocal + ", null, null, " + targetLocal + ");" +
                    "parent.document.body.rows='*,33%';";
                gel.addElement (input);

                gel.addElement (new BlankElement ());
                // construct the delete button:
                input = new InputElement (fieldName + "_BTD",
                    InputElement.INP_BUTTON, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_BUTTONDELETE, env));
                input.onClick =
                    HtmlConstants.JREF_SHEETFORM + fieldName +
                    BOArguments.ARG_OID_EXTENSION +
                    HtmlConstants.JREF_VALUEASSIGN + "''; " +
                    HtmlConstants.JREF_SHEETFORM + fieldName +
                    HtmlConstants.JREF_VALUEASSIGN + "'';";
                gel.addElement (input);

                tr.addElement (new TableDataElement (gel));
                tr.classId = BOListConstants.LST_CLASSINFOROWS[this.properties++ %
                    BOListConstants.LST_CLASSINFOROWS.length];

                table.addElement (tr);  // add new property to table
                break;

            default:                    // unsupported field type
                this.showFormProperty (table, fieldName, name, type,
                    value, null, null, 0);
                break;
        } // switch type
    } // showFormProperty


    /**************************************************************************
     * Show a property to the user within a form. <BR/>
     *
     * @param   table       Table where the property shall be added.
     * @param   fieldName   Name of the form field representing the property.
     * @param   name        Name of the property.
     * @param   type        Type of property.
     * @param   value       Value of the property.
     * @param   ids         Array of ids for select field.
     * @param   values      Array of display values for select field.
     * @param   preselected Number of preselected element of select field.
     */
    protected void showFormProperty (TableElement table, String fieldName,
        String name, int type,
        String value, String[] ids, String[] values, int preselected)
    {
        // call common method:
        this.showFormProperty (table, fieldName, name, type,
            value, ids, values, preselected, false);
    } // showFormProperty


    /**************************************************************************
     * Show a property to the user within a form. <BR/>
     *
     * @param   table       Table where the property shall be added.
     * @param   fieldName   Name of the form field representing the property.
     * @param   name        Name of the property.
     * @param   type        Type of property.
     * @param   value       Value of the property.
     * @param   ids         Array of ids for select field.
     * @param   values      Array of display values for select field.
     * @param   preselected Number of preselected element of select field.
     * @param   isSorted    Tells whether the values are already sorted.
     *                      If no they are sorted in ASCII sequence.
     */
    protected void showFormProperty (TableElement table, String fieldName,
                                     String name, int type, String value,
                                     String[] ids, String[] values,
                                     int preselected, boolean isSorted)
    {
        String valueLocal = value;      // variable for local assignments
        int preselectedLocal = preselected; // variable for local assignments
        Font valueFont =
            new Font (AppConstants.FONT_VALUE, AppConstants.FONTSIZE_VALUE);
                                        // Font for value of property

        // check if value is null or only one blank
        if (valueLocal == null || valueLocal.equals (" "))
        {
            // delete space: oracle is not able to store emptstring
            // emptystring is always represented as " "
            valueLocal = "";
        } // if

        RowElement tr = new RowElement (2);
        TableDataElement td;
        TextElement text;
        Element elem;
        InputElement input;
        SelectElement select;
        TextAreaElement textArea;
        GroupElement gel;
        String range = "";
        String[] typeIds = null;
        String[] typeNames = null;
/* KR deprecated
 * The following code is not correct and currently not used.
 */
        String[] userIds =
        {
            Integer.toString (0x01800002),
            Integer.toString (0x01800001),
        }; // userIds

        String[] userNames =
        {
            "Admin",
            "Anonym",
        }; // userNames
/* KR deprecated end
 */
        TypeContainer types = null;     // the actual types list
        Type objectType = null;         // the actual object type
        String dateStr;
        String timeStr;
        String selectedValue = valueLocal + "\" onFocus=\"this.select ();";
                                        // value with autoselect
        FormFieldRestriction restriction;

        // check field restriction - it is possible that they are not set yet
        if (this.formFieldRestriction == null)
        {
            // not set yet
            restriction = new FormFieldRestriction ();
        } // if
        else
        {
            // get it from objects attribute
            restriction = this.formFieldRestriction;
        } // else

        // set name and data type of form field in restriction field
        restriction.name = fieldName;
        restriction.dataType = type;
        // build JavaScript code for form field restrictions
        StringBuffer restrictionScript = restriction.buildRestrictScriptCode (env);

        // now expand form field restriction for the onSubmit functionality
        // of the form
        this.expandFormFieldRestrictions (restrictionScript);

        if (valueLocal == null)              // no value?
        {
            valueLocal = "";                 // set empty string
        } // if

        if (type == Datatypes.DT_HIDDEN)          // hidden property?
        {
            elem = new InputElement (fieldName, InputElement.INP_HIDDEN, valueLocal);
            tr = new RowElement (1);

            td = new TableDataElement (elem);
            td.colspan = 2;           
            tr.addElement (td);
            table.addElement (tr);      // add new property to table
            // reset restrictions for this field - this avoids that the next
            // method call uses previous restrictions
            this.formFieldRestriction = null;
            return;                     // terminate method
        } // if hidden property

        // get the actual property and represent it to the user:
        if (name != null && name.length () > 1)
        {
            text = new TextElement (name + ":");
            td = new TableDataElement (text);
            td.classId = CssConstants.CLASS_NAME;
            tr.addElement (td);
        } // if (name != null && name.length() > 1)
        else // no name set
        {
            tr.addElement (new TableDataElement (new BlankElement ()));
        } // else no name set

        switch (type)
        {
            case Datatypes.DT_BOOL: // boolean field
                gel = new GroupElement ();
                elem = new InputElement (fieldName, InputElement.INP_CHECKBOX, Datatypes.BOOL_TRUE);
                elem.classId = CssConstants.CLASS_BOOLEAN;
                // set additional field to complementary value:
                input = new InputElement (fieldName + "_bool", InputElement.INP_HIDDEN, Datatypes.BOOL_FALSE);
                // set the value of the check box:
                ((InputElement) elem).checked =
                    !valueLocal.equalsIgnoreCase (Datatypes.BOOL_FALSE);
                gel.addElement (elem);
                gel.addElement (input);
                elem = gel;             // remember the new element
                break;

            case Datatypes.DT_EMPTYBOOL: // boolean field with empty option
                gel = new GroupElement ();
                // create new selection box
                select = new SelectElement (fieldName, false);
                // add option to selection box
                // the mod-statement defines which element is selected (via 'preselected')
                select.addOption ("", "", valueLocal.length () == 0);
                select.addOption (MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                    AppMessages.ML_MSG_BOOLTRUE, env), Datatypes.BOOL_TRUE,
                    valueLocal.equalsIgnoreCase (Datatypes.BOOL_TRUE));
                select.addOption (MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                    AppMessages.ML_MSG_BOOLFALSE, env), Datatypes.BOOL_FALSE,
                    valueLocal.equalsIgnoreCase (Datatypes.BOOL_FALSE));
                select.classId = CssConstants.CLASS_BOOLEANEMPTY;
                gel.addElement (select);
                elem = gel;             // remember the new element
                break;

            case Datatypes.DT_INTEGER: // integer field
                input = new InputElement (fieldName, InputElement.INP_TEXT, selectedValue);
                // set field size:
                input.setSize (restriction, 10, 10);

                input.classId = CssConstants.CLASS_INTEGER;
                // add restriction script:
                input.onChange = restrictionScript.toString ();
                elem = input;
                break;

            case Datatypes.DT_INTEGERRANGE:       // integer range field
                gel = new GroupElement ();
                input = new InputElement (fieldName, InputElement.INP_TEXT, valueLocal);
                input.size = 10;
                input.classId = CssConstants.CLASS_INTEGER;
                input.onChange = "top.iI (" + fieldName + ", true);";
                gel.addElement (input);

                text = new TextElement (" - ");
                gel.addElement (text);

                range = this.env.getParam (fieldName + BOArguments.ARG_RANGE_EXTENSION);
                input = new InputElement (fieldName + BOArguments.ARG_RANGE_EXTENSION,
                                          InputElement.INP_TEXT, range);
                input.size = 10;
                input.classId = CssConstants.CLASS_INTEGER;
                input.onChange = "top.iI (" + fieldName + ", true);";
                gel.addElement (input);
                elem = gel;
                break;

            case Datatypes.DT_NUMBER:            // integer field
                input = new InputElement (fieldName, InputElement.INP_TEXT, selectedValue);
                // set field size:
                input.setSize (restriction, 10, 10);

                input.classId = CssConstants.CLASS_NUMBER;
                // add restriction script
                input.onChange = restrictionScript.toString ();
                elem = input;
                break;

            case Datatypes.DT_NUMBERRANGE:       // number range field
                gel = new GroupElement ();
                input = new InputElement (fieldName, InputElement.INP_TEXT, valueLocal);
                input.size = 10;
                input.classId = CssConstants.CLASS_NUMBER;
                input.onChange = "top.iNu (" + fieldName + ", true);";
                gel.addElement (input);

                text = new TextElement (" - ");
                gel.addElement (text);

                range = this.env.getParam (fieldName + BOArguments.ARG_RANGE_EXTENSION);
                input = new InputElement (fieldName + BOArguments.ARG_RANGE_EXTENSION,
                                          InputElement.INP_TEXT, range);
                input.size = 10;
                input.classId = CssConstants.CLASS_NUMBER;
                input.onChange = "top.iNu (" + fieldName + ", true);";
                gel.addElement (input);
                elem = gel;
                break;

            case Datatypes.DT_TEXT:               // text field
                input = new InputElement (fieldName, InputElement.INP_TEXT, selectedValue);
                // set field size:
                input.setSize (restriction, 20, 255);

                input.classId = CssConstants.CLASS_TEXT;
                // add restriction script
                input.onChange = restrictionScript.toString ();
                elem = input;
                break;

            case Datatypes.DT_MONEY:               // text field
                input = new InputElement (fieldName, InputElement.INP_TEXT,
                    selectedValue);
                // set field size:
                input.setSize (restriction, 15, 255);

                input.classId = CssConstants.CLASS_MONEY;
                // add restriction script
                input.onChange = restrictionScript.toString ();
                elem = input;
                break;

            case Datatypes.DT_MONEYRANGE:       // money range field
                gel = new GroupElement ();
                input =
                    new InputElement (fieldName, InputElement.INP_TEXT, valueLocal);
                input.size = 15;
                input.classId = CssConstants.CLASS_MONEY;
                input.onChange = "top.iM (" + fieldName + ", true);";
                gel.addElement (input);

                text = new TextElement (" - ");
                gel.addElement (text);

                range = this.env.getParam (fieldName +
                    BOArguments.ARG_RANGE_EXTENSION);
                input = new InputElement (fieldName +
                    BOArguments.ARG_RANGE_EXTENSION, InputElement.INP_TEXT,
                    range);
                input.size = 15;
                input.classId = CssConstants.CLASS_MONEY;
                input.onChange = "top.iM (" + fieldName + ", true);";
                gel.addElement (input);
                elem = gel;
                break;

            case Datatypes.DT_SEARCHTEXT: // search text property
                gel = new GroupElement ();

                // create new selection box
                select = new SelectElement (fieldName +
                    BOArguments.ARG_MATCHTYPE_EXTENSION, false);

                // check 'preselected' value
                if (preselectedLocal == 0)
                {
                    preselectedLocal = 1;
                } // if

                // add option to selection box
                // the mod-statement defines which element is selected
                // (via 'preselected')
                select.addOption (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MATCHSUBSTRING, env),
                    BOConstants.MATCH_SUBSTRING, preselectedLocal == 1);
                select.addOption (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MATCHEXACT, env),
                    BOConstants.MATCH_EXACT, preselectedLocal == 2);
                select.addOption (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MATCHSOUNDEX, env),
                    BOConstants.MATCH_SOUNDEX, preselectedLocal == 3);

                gel.addElement (select);
                input = new InputElement (fieldName, InputElement.INP_TEXT,
                    selectedValue);
                // set field size:
                input.setSize (restriction, 30, 255);

                input.classId = CssConstants.CLASS_TEXT;
                // add restriction script
                input.onChange = restrictionScript.toString ();
                gel.addElement (input);
                elem = gel;
                break;

            case Datatypes.DT_SEARCHTEXT_EXT: // search text extended property
                gel = new GroupElement ();

                // create new selection box
                select = new SelectElement (fieldName +
                    BOArguments.ARG_MATCHTYPE_EXTENSION, false);

                // check 'preselected' value
                if (preselectedLocal == 0)
                {
                    preselectedLocal = 1;
                } // if

                // add option to selection box
                // the mod-statement defines which element is selected
                // (via 'preselected')
                select.addOption (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MATCHSUBSTRING, env),
                    BOConstants.MATCH_SUBSTRING, preselectedLocal == 1);
                select.addOption (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MATCHEXACT, env),
                    BOConstants.MATCH_EXACT, preselectedLocal == 2);
                select.addOption (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MATCHSOUNDEX, env),
                    BOConstants.MATCH_SOUNDEX, preselectedLocal == 3);
                select.addOption (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MATCHGREATER, env),
                    BOConstants.MATCH_GREATER, preselectedLocal == 4);
                select.addOption (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MATCHGREATEREQUAL, env),
                    BOConstants.MATCH_GREATEREQUAL, preselectedLocal == 5);
                select.addOption (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MATCHLESS, env),
                    BOConstants.MATCH_LESS, preselectedLocal == 6);
                select.addOption (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MATCHLESSEQUAL, env),
                    BOConstants.MATCH_LESSEQUAL, preselectedLocal == 7);

                gel.addElement (select);
                input = new InputElement (fieldName, InputElement.INP_TEXT,
                    selectedValue);
                // set field size:
                input.setSize (restriction, 30, 255);

                input.classId = CssConstants.CLASS_TEXT;
                // add restriction script
                input.onChange = restrictionScript.toString ();
                gel.addElement (input);
                elem = gel;
                break;

            case Datatypes.DT_SEARCHNUMBER: // search number property - or
            case Datatypes.DT_SEARCHMONEY: // search money property - use the same field as number
                gel = new GroupElement ();

                // create new selection box
                select = new SelectElement (fieldName + BOArguments.ARG_MATCHTYPE_EXTENSION, false);

                // check 'preselected' value
                if (preselectedLocal == 0)
                {
                    preselectedLocal = 1;
                } // if

                // add option to selection box
                // the mod-statement defines which element is selected (via 'preselected')
                select.addOption (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MATCHEXACT, env), BOConstants.MATCH_EXACT, preselectedLocal == 1);
                select.addOption (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MATCHGREATER, env), BOConstants.MATCH_GREATER, preselectedLocal == 2);
                select.addOption (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MATCHGREATEREQUAL, env), BOConstants.MATCH_GREATEREQUAL, preselectedLocal == 3);
                select.addOption (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MATCHLESS, env), BOConstants.MATCH_LESS, preselectedLocal == 4);
                select.addOption (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MATCHLESSEQUAL, env), BOConstants.MATCH_LESSEQUAL, preselectedLocal == 5);

                gel.addElement (select);
                input = new InputElement (fieldName, InputElement.INP_TEXT, selectedValue);
                // set field size:
                input.setSize (restriction, 30, 255);

                if (type == Datatypes.DT_SEARCHNUMBER)
                {
                    input.classId = CssConstants.CLASS_NUMBER;
                } // if
                else if (type == Datatypes.DT_SEARCHMONEY)
                {
                    input.classId = CssConstants.CLASS_MONEY;
                } // else

                // add restriction script
                input.onChange = restrictionScript.toString ();
                gel.addElement (input);
                elem = gel;
                break;

            case Datatypes.DT_SEARCHDATE: // search date property
                gel = new GroupElement ();

                // create new selection box
                select = new SelectElement (fieldName +
                    BOArguments.ARG_MATCHTYPE_EXTENSION, false);

                // check 'preselected' value
                if (preselectedLocal == 0)
                {
                    preselectedLocal = 1;
                } // if

                // add option to selection box
                // the mod-statement defines which element is selected (via 'preselected')
                select.addOption (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MATCHEXACT, env), BOConstants.MATCH_EXACT, preselectedLocal == 1);
                select.addOption (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MATCHGREATER, env), BOConstants.MATCH_GREATER, preselectedLocal == 2);
                select.addOption (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MATCHGREATEREQUAL, env), BOConstants.MATCH_GREATEREQUAL, preselectedLocal == 3);
                select.addOption (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MATCHLESS, env), BOConstants.MATCH_LESS, preselectedLocal == 4);
                select.addOption (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MATCHLESSEQUAL, env), BOConstants.MATCH_LESSEQUAL, preselectedLocal == 5);

                gel.addElement (select);
                input = new InputElement (fieldName, InputElement.INP_TEXT, selectedValue);
                // set field size:
                input.setSize (restriction, 10, 10);

                input.classId = CssConstants.CLASS_DATE;
                // add restriction script
                input.onChange = restrictionScript.toString ();
                gel.addElement (input);

                text = new TextElement (" (" + MultilingualTextProvider.getText (
                        BOTokens.TOK_BUNDLE, BOTokens.ML_STANDARDDATEFORMAT, env)  + ")");
                gel.addElement (text);
                elem = gel;
                break;

            case Datatypes.DT_SEARCHTIME: // search time property
                gel = new GroupElement ();

                // create new selection box
                select = new SelectElement (fieldName +
                    BOArguments.ARG_MATCHTYPE_EXTENSION, false);

                // check 'preselected' value
                if (preselectedLocal == 0)
                {
                    preselectedLocal = 1;
                } // if

                // add option to selection box
                // the mod-statement defines which element is selected (via 'preselected')
                select.addOption (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MATCHEXACT, env), BOConstants.MATCH_EXACT, preselectedLocal == 1);
                select.addOption (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MATCHGREATER, env), BOConstants.MATCH_GREATER, preselectedLocal == 2);
                select.addOption (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MATCHGREATEREQUAL, env), BOConstants.MATCH_GREATEREQUAL, preselectedLocal == 3);
                select.addOption (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MATCHLESS, env), BOConstants.MATCH_LESS, preselectedLocal == 4);
                select.addOption (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MATCHLESSEQUAL, env), BOConstants.MATCH_LESSEQUAL, preselectedLocal == 5);

                gel.addElement (select);
                input = new InputElement (fieldName, InputElement.INP_TEXT, selectedValue);
                // set field size:
                input.setSize (restriction, 5, 5);

                input.classId = CssConstants.CLASS_TIME;
                // add restriction script
                input.onChange = restrictionScript.toString ();
                gel.addElement (input);

                text = new TextElement (" (" + MultilingualTextProvider.getText (
                        BOTokens.TOK_BUNDLE, BOTokens.ML_STANDARDTIMEFORMAT, env)  + ")");
                gel.addElement (text);
                elem = gel;
                break;

            case Datatypes.DT_SEARCHDATETIME: // search date property
                // a word to the form field restrictions in this case:
                // due to the complications using 2 fields as one datatype
                // some constraints must be taken for granted.
                // so the restriction evaluation (e.g. upper or lower bounds)
                // will in this case only be be performed for the time field.
                // the date field will only be checked for correct input.
                // assuming that the user inserts data from the left to
                // the right this is the right approach, but nevertheless
                // somtimes the user will insert data in another sequnce.
                // for this case the insertions must also be checked before
                // submitting the form. sad but true.

                gel = new GroupElement ();

                // create new selection box
                select = new SelectElement (fieldName +
                    BOArguments.ARG_MATCHTYPE_EXTENSION, false);

                // check 'preselected' value
                if (preselectedLocal == 0)
                {
                    preselectedLocal = 1;
                } // if

                // add option to selection box
                // the mod-statement defines which element is selected (via 'preselected')
                select.addOption (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MATCHEXACT, env), BOConstants.MATCH_EXACT, preselectedLocal == 1);
                select.addOption (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MATCHGREATER, env), BOConstants.MATCH_GREATER, preselectedLocal == 2);
                select.addOption (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MATCHGREATEREQUAL, env), BOConstants.MATCH_GREATEREQUAL, preselectedLocal == 3);
                select.addOption (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MATCHLESS, env), BOConstants.MATCH_LESS, preselectedLocal == 4);
                select.addOption (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MATCHLESSEQUAL, env), BOConstants.MATCH_LESSEQUAL, preselectedLocal == 5);

                gel.addElement (select);

                // get date and time strings:
                if (valueLocal.length () == 0)
                {
                    dateStr = "";
                    timeStr = "";
                } // if
                else
                {
                    dateStr = DateTimeHelpers.dateToString (DateTimeHelpers.stringToDateTime (valueLocal));
                    timeStr = DateTimeHelpers.timeToString (DateTimeHelpers.stringToDateTime (valueLocal));
                } // else

                // create date field - extension of field is '_d'
                input = new InputElement (fieldName + BOArguments.ARG_DATE_EXTENSION,
                    InputElement.INP_TEXT, dateStr);
                // set view-size of input field
                input.size = 10;                        // set default
                // set maximum size of input fields content
                input.maxlength = 10;

                input.classId = CssConstants.CLASS_DATE;
                input.onChange = "top.iD (" + HtmlConstants.JREF_SHEETFORM +
                    fieldName + BOArguments.ARG_DATE_EXTENSION + ", " +
                    restriction.emptyAllowed + ")";

                gel.addElement (input);

                // create time field - extension of field is '_t'
                input = new InputElement (fieldName + BOArguments.ARG_TIME_EXTENSION,
                    InputElement.INP_TEXT, timeStr);
                // set view-size of input field:
                input.size = 5;                        // set default
                // set maximum size of input fields content
                input.maxlength = 5;

                input.classId = CssConstants.CLASS_TIME;
                input.onChange = "top.iT (" + HtmlConstants.JREF_SHEETFORM +
                    fieldName + BOArguments.ARG_TIME_EXTENSION + ", " +
                    restriction.emptyAllowed + ")";

                gel.addElement (input);

                // get the actual property and represent it to the user:
                text = new TextElement (" (" + 
                        MultilingualTextProvider.getText (
                            BOTokens.TOK_BUNDLE, BOTokens.ML_STANDARDDATEFORMAT, env) + " - " +
                        MultilingualTextProvider.getText (
                            BOTokens.TOK_BUNDLE, BOTokens.ML_STANDARDTIMEFORMAT, env) + ")");

                gel.addElement (text);
                elem = gel;
                break;

            case Datatypes.DT_TEXTAREA:           // text area
                textArea = new TextAreaElement (fieldName, valueLocal);
                textArea.cols = 40;
                textArea.rows = 5;
                textArea.maxlength = 255;
                textArea.wrap = "VIRTUAL";
                textArea.classId = CssConstants.CLASS_TEXTAREA;
                elem = textArea;
                break;

            case Datatypes.DT_HTMLTEXT:               // textarea wich may contain HTML-Code
                textArea = new TextAreaElement (fieldName, valueLocal);
                textArea.cols = 60;
                textArea.rows = 12;
                textArea.maxlength = 65535;
                textArea.wrap = "VIRTUAL";
                textArea.classId = CssConstants.CLASS_HTMLTEXT;
                elem = textArea;
                break;

            case Datatypes.DT_DATE:               // date field
                input = new InputElement (fieldName, InputElement.INP_TEXT, selectedValue);
                // set field size:
                input.setSize (restriction, 10, 10);

                input.classId = CssConstants.CLASS_DATE;
                // add restriction script
                input.onChange = restrictionScript.toString ();
                gel = new GroupElement ();
                gel.addElement (input);

                text = new TextElement (" (" + MultilingualTextProvider.getText (
                        BOTokens.TOK_BUNDLE, BOTokens.ML_STANDARDDATEFORMAT, env)  + ")");
                gel.addElement (text);
                elem = gel;
                break;

            case Datatypes.DT_DATERANGE:          // date range field
                gel = new GroupElement ();
                input = new InputElement (fieldName, InputElement.INP_TEXT, valueLocal);
                input.size = 10;
                input.maxlength = 10;
                input.classId = CssConstants.CLASS_DATE;
                input.onChange = "top.iD (" + HtmlConstants.JREF_SHEETFORM +
                    fieldName + ", " + restriction.emptyAllowed + ")";

                gel.addElement (input);

                text = new TextElement (" - ");
                gel.addElement (text);

                range = this.env.getParam (fieldName + BOArguments.ARG_RANGE_EXTENSION);
                input = new InputElement (fieldName + BOArguments.ARG_RANGE_EXTENSION,
                                          InputElement.INP_TEXT, range);
                input.size = 10;
                input.maxlength = 10;
                input.classId = CssConstants.CLASS_DATE;
                input.onChange = "top.iD (" + HtmlConstants.JREF_SHEETFORM +
                    fieldName + BOArguments.ARG_RANGE_EXTENSION + ", " +
                    restriction.emptyAllowed + ");";

                gel.addElement (input);

                text = new TextElement (" (" + MultilingualTextProvider.getText (
                        BOTokens.TOK_BUNDLE, BOTokens.ML_STANDARDDATEFORMAT, env)  + ")");

                gel.addElement (text);

                elem = gel;
                break;

            case Datatypes.DT_TIMERANGE:          // date range field
                gel = new GroupElement ();
                input = new InputElement (fieldName, InputElement.INP_TEXT, valueLocal);
                input.size = 5;
                input.maxlength = 5;
                input.classId = CssConstants.CLASS_TIME;
                if (restriction.emptyAllowed)
                {
                    input.onChange = "top.iT (" + HtmlConstants.JREF_SHEETFORM +
                        fieldName + ", true)";
                } // if
                else
                {
                    input.onChange = "top.iT (" + HtmlConstants.JREF_SHEETFORM +
                        fieldName + ")";
                } // else

                gel.addElement (input);

                text = new TextElement (" - ");
                gel.addElement (text);

                range = this.env.getParam (fieldName + BOArguments.ARG_RANGE_EXTENSION);
                input = new InputElement (fieldName + BOArguments.ARG_RANGE_EXTENSION,
                                          InputElement.INP_TEXT, range);
                input.size = 5;
                input.maxlength = 5;
                input.classId = CssConstants.CLASS_TIME;
                if (restriction.emptyAllowed)
                {
                    input.onChange = "top.iT (" + HtmlConstants.JREF_SHEETFORM +
                        fieldName + BOArguments.ARG_RANGE_EXTENSION + ", true)";
                } // if
                else
                {
                    input.onChange = "top.iT (" + HtmlConstants.JREF_SHEETFORM +
                        fieldName + BOArguments.ARG_RANGE_EXTENSION + ")";
                } // else

                gel.addElement (input);

                text = new TextElement (" (" + MultilingualTextProvider.getText (
                        BOTokens.TOK_BUNDLE, BOTokens.ML_STANDARDTIMEFORMAT, env)  + ")");

                gel.addElement (text);

                elem = gel;
                break;

            case Datatypes.DT_DATETIMERANGE:          // date range field
                gel = new GroupElement ();

                // get date and time strings:
                if (valueLocal.length () == 0)
                {
                    dateStr = "";
                    timeStr = "";
                } // if
                else
                {
                    dateStr = DateTimeHelpers.dateToString (DateTimeHelpers.stringToDateTime (valueLocal));
                    timeStr = DateTimeHelpers.timeToString (DateTimeHelpers.stringToDateTime (valueLocal));
                } // else

                // create date field - extension of field is '_d'
                input = new InputElement (fieldName + BOArguments.ARG_DATE_EXTENSION,
                    InputElement.INP_TEXT, dateStr);
                // set view-size of input field
                input.size = 10;                        // set default
                // set maximum size of input fields content
                input.maxlength = 10;

                input.classId = CssConstants.CLASS_DATE;
                input.onChange = "top.iD (" + HtmlConstants.JREF_SHEETFORM +
                    fieldName + BOArguments.ARG_DATE_EXTENSION + ", " +
                    restriction.emptyAllowed + ")";

                gel.addElement (input);

                // create time field - extension of field is '_d'
                input = new InputElement (fieldName + BOArguments.ARG_TIME_EXTENSION,
                    InputElement.INP_TEXT, timeStr);
                // set view-size of input field
                input.size = 5;                        // set default
                // set maximum size of input fields content
                input.maxlength = 5;

                input.classId = CssConstants.CLASS_TIME;
                input.onChange = "top.iT (" + HtmlConstants.JREF_SHEETFORM +
                    fieldName + BOArguments.ARG_TIME_EXTENSION + ", " +
                    restriction.emptyAllowed + ")";

                gel.addElement (input);

                text = new TextElement (" - ");
                gel.addElement (text);


                // get date and time strings:
                if (valueLocal.length () == 0)
                {
                    dateStr = "";
                    timeStr = "";
                } // if
                else
                {
                    dateStr = DateTimeHelpers.dateToString (DateTimeHelpers.stringToDateTime (range));
                    timeStr = DateTimeHelpers.timeToString (DateTimeHelpers.stringToDateTime (range));
                } // else

                // create date field - extension of field is '_d'
                input = new InputElement (fieldName + BOArguments.ARG_RANGE_EXTENSION +
                    BOArguments.ARG_DATE_EXTENSION, InputElement.INP_TEXT, dateStr);
                // set view-size of input field
                input.size = 10;                        // set default
                // set maximum size of input fields content
                input.maxlength = 10;

                input.classId = CssConstants.CLASS_DATE;
                input.onChange = "top.iD (" + HtmlConstants.JREF_SHEETFORM +
                    fieldName + BOArguments.ARG_RANGE_EXTENSION +
                    BOArguments.ARG_DATE_EXTENSION + ", " +
                    restriction.emptyAllowed + ")";

                gel.addElement (input);

                // create time field - extension of field is '_d'
                input = new InputElement (fieldName + BOArguments.ARG_RANGE_EXTENSION +
                    BOArguments.ARG_TIME_EXTENSION, InputElement.INP_TEXT, timeStr);
                // set view-size of input field
                input.size = 5;                        // set default
                // set maximum size of input fields content
                input.maxlength = 5;

                input.classId = CssConstants.CLASS_TIME;
                input.onChange = "top.iT (" + HtmlConstants.JREF_SHEETFORM +
                    fieldName + BOArguments.ARG_RANGE_EXTENSION +
                    BOArguments.ARG_TIME_EXTENSION + ", " +
                    restriction.emptyAllowed + ")";

                gel.addElement (input);

                // get the actual property and represent it to the user:
                text = new TextElement (" (" +
                        MultilingualTextProvider.getText (
                            BOTokens.TOK_BUNDLE, BOTokens.ML_STANDARDDATEFORMAT, env) + " - " +
                        MultilingualTextProvider.getText (
                            BOTokens.TOK_BUNDLE, BOTokens.ML_STANDARDTIMEFORMAT, env) + ")");

                gel.addElement (text);
                elem = gel;
                break;

            case Datatypes.DT_TIME:               // time field
                gel = new GroupElement ();
                input = new InputElement (fieldName, InputElement.INP_TEXT, selectedValue);
                // set field size:
                input.setSize (restriction, 5, 5);

                input.classId = CssConstants.CLASS_TIME;
                // add restriction script
                input.onChange = restrictionScript.toString ();
                gel.addElement (input);
                text = new TextElement (" (" + MultilingualTextProvider.getText (
                        BOTokens.TOK_BUNDLE, BOTokens.ML_STANDARDTIMEFORMAT, env)  + ")");
                gel.addElement (text);
                elem = gel;
                break;

            case Datatypes.DT_SELECT:           // selection field
                elem = this.getFormSelectProperty (fieldName, valueLocal,
                    ids, values, preselectedLocal, isSorted);
                break;

            case Datatypes.DT_SELECTMULTIPLE:           // selection field
                elem = this.getFormMultiSelectProperty (fieldName, valueLocal,
                    ids, values, isSorted);
                break;

            case Datatypes.DT_SELECTEMPTY:      // selection field - empty allowed
                elem = this.getFormSelectProperty (fieldName, valueLocal,
                    ids, values, preselectedLocal, "0x00000000", "", isSorted);
                break;

            case Datatypes.DT_RADIO:              // radio button
                input = new InputElement (fieldName, InputElement.INP_RADIO, valueLocal);
                input.checked = false;
                input.classId = CssConstants.CLASS_RADIO;
                elem = input;
                break;

            case Datatypes.DT_IMAGE:
            case Datatypes.DT_THUMBNAIL:
            case Datatypes.DT_PICTURE:
            case Datatypes.DT_FILE:             // file
            case Datatypes.DT_IMPORTFILE:       // importfile
                // create form property for upload
                this.showFormProperty (table, fieldName, name, type, valueLocal, null);
                // exit method
                return;

            case Datatypes.DT_TYPE:     // object type
                // get the object type out of the cache:
                if ((objectType = this.getTypeCache ().getType (
                    this.oid.tVersionId)) != null) // type was found?
                {
//trace ("type = " + objectType);
                    // get the may contain types for the actual type:
                    types = (TypeContainer) objectType.getMayContainTypes ();
//trace ("mayContainTypes: " + types);

                    // set the typeIds array:
                    typeIds = types.ids;
                    
                    // set the typeNames array for the current user:
                    typeNames = types.getMlNames (this.env);
                } // if type was found

                elem = this.getFormSelectProperty (fieldName, valueLocal,
                    typeIds, typeNames, preselectedLocal, isSorted);
                break;

            case Datatypes.DT_TYPEWITHALL: // object with additional value for all
                // get the object type out of the cache:
                if ((objectType = this.getTypeCache ().getType (
                    this.oid.tVersionId)) != null) // type was found?
                {
//trace ("type = " + objectType);
                    // get the may contain types for the actual type:
                    types = (TypeContainer) objectType.getMayContainTypes ();
//trace ("mayContainTypes: " + types);

                    // set the typeIds and typeNames arrays:
                    typeIds = types.ids;
                    typeNames = types.names;
                } // if type was found

                elem = this.getFormSelectProperty (fieldName, valueLocal,
                    typeIds, typeNames, preselectedLocal,
                    Integer.toString (TypeConstants.TYPE_NOTYPE), TypeConstants.TN_NOTYPE,
                    isSorted);
                break;

/* KR deprecated
 * The following code is not correct and currently not used.
 */
            case Datatypes.DT_USER:               // user name
                elem = this.getFormSelectProperty (fieldName, valueLocal,
                    userIds, userNames, preselectedLocal, isSorted);
                break;
/* KR deprecated end
 */

            case Datatypes.DT_PASSWORD:           // password
                input = new InputElement (fieldName, InputElement.INP_PASSWORD, selectedValue);
                // set field size:
                input.setSize (restriction, 40, 63);

                // add restriction script:
                input.onChange = restrictionScript.toString ();
                elem = input;
                break;

            case Datatypes.DT_NAME:               // object name
                input = new InputElement (fieldName, InputElement.INP_TEXT, selectedValue);
                // set field size:
                input.setSize (restriction, 40, 63);

                input.classId = CssConstants.CLASS_NAME;
                // add restriction script:
                input.onChange = restrictionScript.toString ();
                elem = input;
                break;

            case Datatypes.DT_EMAIL:              // email property
                input = new InputElement (fieldName, InputElement.INP_TEXT, selectedValue);
                // set field size:
                input.setSize (restriction, 40, 127);

                input.classId = CssConstants.CLASS_EMAIL;
                // add restriction script:
                input.onChange = restrictionScript.toString ();
                elem = input;
                break;

            case Datatypes.DT_URL:      // URL property
                input = new InputElement (fieldName, InputElement.INP_TEXT, selectedValue);
                // set field size:
                input.setSize (restriction, 40, 255);

                input.classId = CssConstants.CLASS_URL;
                // add restriction script:
                input.onChange = restrictionScript.toString ();
                elem = input;
                break;

            case Datatypes.DT_DESCRIPTION: // description
                if (valueLocal == null)
                {
                    valueLocal = "";
                } // if
                textArea = new TextAreaElement (fieldName, valueLocal);
                textArea.cols = 40;
                textArea.rows = 5;
                textArea.maxlength = 255;
                textArea.wrap = "VIRTUAL";
                textArea.classId = CssConstants.CLASS_DESCRIPTION;
                elem = textArea;
                break;

            case Datatypes.DT_HIDDEN:             // hidden property
                elem = new TextElement (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_NOTIMPLEMENTED, env));
                ((TextElement) elem).font = valueFont;
                break;

            default:                    // unknown field type
                elem = new InputElement (fieldName, InputElement.INP_TEXT, selectedValue);
                break;
        } // switch type

        tr.addElement (new TableDataElement (elem));

        tr.classId = BOListConstants.LST_CLASSINFOROWS[this.properties %
            BOListConstants.LST_CLASSINFOROWS.length];

        this.properties++;              // remember number of properties

        table.addElement (tr);          // add new property to table

        // reset restrictions for this field - this avoids that the next
        // method call uses previous restrictions
        this.formFieldRestriction = null;
    } // showFormProperty


    /**************************************************************************
     * Show a property to the user within a form. <BR/>
     *
     * @param   table       Table where the property shall be added.
     * @param   fieldName   Name of the form field representing the property.
     * @param   name        Name of the property.
     * @param   type        Type of property.
     * @param   preselectedValues   Value of the property.
     * @param   ids         Array of ids for select field.
     * @param   values      Array of display values for select field.
     * @param   size        The size of the multiple selection form
     */
    protected void showFormProperty (TableElement table, String fieldName,
        String name, int type,  String[] preselectedValues,
        String[] ids, String[] values, int size)
    {
        RowElement tr = new RowElement (2);
        Element elem = null;
        TextElement text;
        TableDataElement td;

        // get the actual property and represent it to the user:
        text = new TextElement (name + ": ");

        td = new TableDataElement (text);
        td.classId = CssConstants.CLASS_NAME;
        tr.addElement (td);

        switch (type)
        {
            case Datatypes.DT_MULTISELECT:             // selection field
                elem = this.getMultipleFormSelectProperty (fieldName,
                    preselectedValues, ids, values, size);
                break;

            default:
                break;
        } // switch type

        tr.addElement (new TableDataElement (elem));

        tr.classId = BOListConstants.LST_CLASSINFOROWS[this.properties %
            BOListConstants.LST_CLASSINFOROWS.length];

        this.properties++;              // remember number of properties

        table.addElement (tr);          // add new property to table

        // reset restrictions for this field - this avoids that the next
        // method call uses previous restrictions
        this.formFieldRestriction = null;
    } // showFormProperty


    /**************************************************************************
     * Show a property to the user within a form. <BR/>
     *
     * @param   table       Table where the property shall be added.
     * @param   name        Name of the property.
     * @param   elem        element to be added to the table
     */
    protected void showFormProperty (TableElement table, String name,
        GroupElement elem)
    {
        RowElement tr = new RowElement (2);
        TextElement text = new TextElement (name + ": ");
        TableDataElement td;

        td = new TableDataElement (text);
        td.classId = CssConstants.CLASS_NAME;
        tr.addElement (td);
        tr.addElement (new TableDataElement (elem));
        tr.classId = BOListConstants.LST_CLASSINFOROWS[this.properties %
            BOListConstants.LST_CLASSINFOROWS.length];

        this.properties++;              // remember number of properties

        table.addElement (tr);          // add new property to table
    } // showFormProperty


    /**************************************************************************
     * used for the extended search, idtype still important
     * Show a selectionbox that is filled with the chosen query data.
     * The query is executed and the value belonging to the given id
     * is preselected.
     * You have to define two output columns (named id and value)
     * in the querycreator!. <BR/>
     * This is a wrapper method.<BR/>
     *
     * @param   table       Table where the property shall be added.
     * @param   fieldname   Name of the form field representing the property.
     * @param   field       Name of the property.
     * @param   type        Type of property.
     * @param   queryname   name of the query which is filling the selectionbox
     * @param   value       the preselected element
     * @param   idtype      databasetype of the ids
     * @param   emptyoption true if there is an empty item
     */
    protected void showFormProperty (TableElement table, String fieldname,
                                    String field, int type,
                                    String queryname, String value, String idtype,
                                    boolean emptyoption)
    {
        this.showFormProperty (table, fieldname, field, type, queryname,
                value, idtype, emptyoption, false);
    } // showFormProperty

    /**************************************************************************
     * used for the extended search, idtype still important
     * Show a selectionbox that is filled with the chosen query data.
     * The query is executed and the value belonging to the given id
     * is preselected.
     * You have to define two output columns (named id and value)
     * in the querycreator!. <BR/>
     *
     * @param   table       Table where the property shall be added.
     * @param   fieldname   Name of the form field representing the property.
     * @param   field       Name of the property.
     * @param   type        Type of property.
     * @param   queryname   name of the query which is filling the selectionbox
     * @param   value       the preselected element
     * @param   idtype      databasetype of the ids
     * @param   emptyoption true if there is an empty item
     * @param   isMultiple  true if it is a multiple selection form
     */
    protected void showFormProperty (TableElement table, String fieldname,
                                    String field, int type,
                                    String queryname, String value, String idtype,
                                    boolean emptyoption, boolean isMultiple)
    {
        TableDataElement td;

        // check datatype of field
        switch (type)
        {
            // selection field
            case Datatypes.DT_QUERYSELECTIONBOX:
                // get the query values
                SelectionList queryResult =
                    this.getQueryData (queryname, idtype, emptyoption);
                // if the query returns valid data
                if (queryResult != null)
                {
                    // is it a multiple selection box?
                    if (isMultiple)
                    {
                        // show the selectionbox that is filled with the query data
                        this.showFormProperty (table, fieldname, field, Datatypes.DT_SELECTMULTIPLE,
                            value, queryResult.ids, queryResult.values, 0, true);
                    } // if (isMultiple)
                    else  // single selection box
                    {
                        // show the selectionbox that is filled with the query data
                        this.showFormProperty (table, fieldname, field, Datatypes.DT_SELECT,
                            value, queryResult.ids, queryResult.values, 0, true);
                    } // else single selection box
                } // if
                else    // query result was null
                {
                    // there is no data to fill the selectionbox
                    // show a message
                    RowElement tr = new RowElement (2);
                    TextElement text;
                    GroupElement group = new GroupElement ();

                    // get the actual property and represent it to the user:
                    text = new TextElement (field + ": ");

                    td = new TableDataElement (text);
                    td.classId = CssConstants.CLASS_NAME;
                    tr.addElement (td);

                    if (value.length () == 0)
                    {
                        group.addElement (new BlankElement (1));
                    } // if

                    group.addElement (
                        IOHelpers.getTextField (
                            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                                DITokens.ML_NORESULT_FORQUERYSELECTIONBOX, env)));

                    tr.addElement (new TableDataElement (group));

                    tr.classId = BOListConstants.LST_CLASSINFOROWS[this.properties++ %
                        BOListConstants.LST_CLASSINFOROWS.length];

                    table.addElement (tr);          // add new property to table
                } // else query result was null

                break;
            default:
                break;
        } // switch type
    } // showFormProperty

    /**************************************************************************
     * used for the extended search
     * Show a selectionbox that is filled with the chosen query data.
     * The query is executed and the value belonging to the given id
     * is preselected.
     * You have to define two output columns (named id and value)
     * in the querycreator!. <BR/>
     *
     * @param   table       Table where the property shall be added.
     * @param   fieldname   Name of the form field representing the property.
     * @param   field       Name of the property.
     * @param   type        Type of property.
     * @param   value       the preselected element
     * @param   idtype      databasetype of the ids
     * @param   emptyoption true if there is an empty item
     * @param   context       Name of the context, which is used within query.
     */
    protected void showFormProperty (TableElement table, String fieldname,
                                    String field, int type, String value,
                                    String idtype, boolean emptyoption,
                                    String context)
    {
        TableDataElement td;

        // check datatype of field
        switch (type)
        {
            case Datatypes.DT_VALUEDOMAIN:
                // create the SQL String
                StringBuffer queryStr = new StringBuffer ()
                    .append (" SELECT *")
                    .append (" FROM v_getValueDomain")
                    .append (" WHERE context = '")
                    .append (context).append ("'")
                    .append (" ORDER BY orderCrit ASC ");

                // get the query values:
                SelectionList queryResult =
                    this.getQueryData (queryStr, idtype, true);

                // if the query returns valid data
                if (queryResult != null)
                {
                    // show the selectionbox that is filled with the query data
                    this.showFormProperty (table, fieldname, field, Datatypes.DT_SELECT,
                        value, queryResult.ids, queryResult.values, 0, true);
                } // if
                else
                {
                    // there is no data to fill the selectionbox
                    // show a message
                    RowElement tr = new RowElement (2);
                    TextElement text;
                    GroupElement group = new GroupElement ();

                    // get the actual property and represent it to the user:
                    text = new TextElement (field + ": ");

                    td = new TableDataElement (text);
                    td.classId = CssConstants.CLASS_NAME;
                    tr.addElement (td);

                    if (value.length () == 0)
                    {
                        group.addElement (new BlankElement (1));
                    } // if

                    group.addElement (
                        IOHelpers.getTextField (
                            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                                DITokens.ML_NORESULT_FORQUERYSELECTIONBOX, env)));

                    tr.addElement (new TableDataElement (group));

                    tr.classId = BOListConstants.LST_CLASSINFOROWS[this.properties++ %
                        BOListConstants.LST_CLASSINFOROWS.length];

                    table.addElement (tr);          // add new property to table
                } // else
                break;
            default:
                break;
        } // switch type
    } // showFormProperty

    /**************************************************************************
     * Show a select property to the user within a form. <BR/>
     *
     * @param   name        Name of the property.
     * @param   value       Value of the property.
     * @param   ids         Array of ids for select field.
     * @param   values      Array of display values for select field.
     * @param   preselected Number of preselected element of select field.
     *
     * @return  The constructed form element.
     */
    protected Element getFormSelectProperty (String name, String value,
        String[] ids, String[] values, int preselected)
    {
        // call common method:
        return this.getFormSelectProperty (name, value, ids, values, preselected,
            false);
    } // getFormSelectProperty


    /**************************************************************************
     * Show a select property to the user within a form. <BR/>
     *
     * @param   name        Name of the property.
     * @param   value       Value of the property.
     * @param   ids         Array of ids for select field.
     * @param   values      Array of display values for select field.
     * @param   preselected Number of preselected element of select field.
     * @param   isSorted    Tells whether the values are already sorted.
     *                      If no they are sorted in ASCII sequence.
     *
     * @return  The constructed form element.
     */
    protected Element getFormSelectProperty (String name, String value,
        String[] ids, String[] values, int preselected, boolean isSorted)
    {
        // call common method:
        return this.getFormSelectProperty (name, value, ids, values, preselected,
            null, null, isSorted);
    } // getFormSelectProperty


    /**************************************************************************
     * Show a select property to the user within a form. <BR/>
     *
     * @param   name        Name of the property.
     * @param   value       Value of the property.
     * @param   ids         Array of ids for select field.
     * @param   values      Array of display values for select field.
     * @param   preselected Number of preselected element of select field.
     * @param   idAll       ???
     * @param   valueAll    ???
     * @param   isSorted    Tells whether the values are already sorted.
     *                      If no they are sorted in ASCII sequence.
     *
     * @return  The constructed form element.
     */
    private Element getFormSelectProperty (String name, String value,
                                           String[] ids, String[] values,
                                           int preselected, String idAll,
                                           String valueAll, boolean isSorted)
    {
        Element elem = null;            // the return element
        int preselectedLocal = preselected; // variable for assignments

/*
//debug ("name = " + name + " value = " + value + " ids = " + ids +
       " values = " + values + " preselected = " + preselected +
       " idAll = " + idAll + " valueAll = " + valueAll +
       " isSorted = " + isSorted);
*/

        if (ids == null || ids.length == 0)
        {
// needed BlankElement - otherwise you have an Layoutproblem with Netscape
            elem = new BlankElement ();
            return elem;
        } // if

        int length = ids.length;        // number of elements in selection
        int [] sorts = null;            // sorted array (the indexes of values)
        int i;                          // loop variables

        // order the arrays by values:
        sorts = this.sortList (values, isSorted);

        if (length > 1)                 // more than one element?
        {
            SelectElement select = new SelectElement (name, false);
            select.size = 1;            // just one line
            select.classId = CssConstants.CLASS_SELECT;

            // ensure that all ids are set:
            this.ensureValidIds (ids, values);

            // search for preselected value:
            preselectedLocal = this.findValue (ids, value, preselectedLocal);

            // set the options of this select field:
            if (idAll != null)
            {
                select.addOption (valueAll, idAll,
                    value.equalsIgnoreCase (idAll));
            } // if

            for (i = 0; i < length; i++)
            {
                select.addOption (values[sorts[i]], ids[sorts[i]],
                    preselectedLocal == sorts[i]);
            } // for i

            elem = select;              // set the return element
        } // if more than one element
        else if (length == 1)           // exactly one element?
        {
            // create group containing the id as hidden field and the
            // value as output text:
            GroupElement group = new GroupElement ();
            group.addElement (
                new InputElement (name, InputElement.INP_HIDDEN, ids[0]));
            group.addElement (new TextElement (values[0])); // TODO: RB MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, values[0], env)));

            elem = group;               // set the return element
        } // else if exactly one element

        return elem;                    // return the created element
    } // getFormSelectProperty


    /**************************************************************************
     * Create a selectbox element with enhanced multiple selection ability. <BR/>
     *
     * @param   name        Name of the property.
     * @param   value       The preselected value.
     * @param   ids         Array of ids for select field.
     * @param   values      Array of display values for select field.
     * @param   isSorted    Tells whether the values are already sorted.
     *                      If no they are sorted in ASCII sequence.
     *
     * @return  The constructed form element.
     */
    private Element getFormMultiSelectProperty (String name, String value,
                                                   String[] ids,
                                                   String[] values,
                                                   boolean isSorted)
    {
        GroupElement group = new GroupElement ();            // the return element

        // did we get any values in the selection box?
        if (ids == null || ids.length == 0)
        {
            group.addElement (new BlankElement ());
        } // if (ids == null || ids.length == 0)
        else // values available
        {
            int [] sorts = null;            // sorted array (the indexes of values)
            // order the arrays by values:
            sorts = this.sortList (values, isSorted);
            // ensure that all ids are set:
            this.ensureValidIds (ids, values);

            SelectElement select = new SelectElement (name, name, false);
            select.size = 1;            // just one line
            // TODO: remove if this does not work
            select.multiple = true;
            select.classId = CssConstants.CLASS_SELECT;
            select.onFocus = "this.multiple = true;" +
                "this.size = (this.length > 10 ) ? 10 : this.length;" +
                "this.title = (this.length > 0) ? '" + MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_TIP_USE_MULTISELECTION, env) + "' : ''";
            select.onBlur = "setSelectedElements (this)";
            select.onDblClick = "this.blur();";
            select.style = "vertical-align: middle;";
            select.title = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_TIP_USE_MULTISELECTION, env);

            // contains preselected values:
            int[] preselectedLocal = null;

            // if there was a preselected value make sure the display of the
            // selected elements is activated
            if (value != null && !value.isEmpty ())
            {
                // check if there are any preselected values
                if (value.indexOf (",") > 0)
                {
                    // get preselected Values
                    String[] preselectedValues = value.split (",");

                    // set length to number of preselected value
                    preselectedLocal = new int[preselectedValues.length];

                    // add the preselected values
                    for (int i = 0; i < preselectedValues.length; i++)
                    {
                        // get index of selected values
                        preselectedLocal [i] = this.findValue (ids, preselectedValues [i], 0);
                    } // for i
                } // if
                // only one element is selected
                else
                {
                    // set length to number of preselected value
                    preselectedLocal = new int[1];

                    // get index of selected values
                    preselectedLocal [0] = this.findValue (ids, value, 0);
                } // else
            } // if

            // count preselected values
            int x = 0;
            boolean bIsSelected = false;

            // add the options
            for (int i = 0; i < ids.length; i++)
            {
                // do we got preselected elements?
                if (preselectedLocal != null && x < preselectedLocal.length)
                {
                    // is current value, the selected value?
                    if (preselectedLocal[x] == sorts[i])
                    {
                        // select value
                        bIsSelected = true;
                        x++;
                    } // if
                    else
                    {
                        // do not select value
                        bIsSelected = false;
                    } // else
                    // add option and select value
                    select.addOption (values[sorts[i]], ids[sorts[i]],
                            bIsSelected);
                } // if
                else
                {
                    // add option
                    select.addOption (values[sorts[i]], ids[sorts[i]]);
                } // els
            } // for i
            group.addElement (select);              // set the return element

            /* IBS-177: Remove info icons for multi selectionboxes
            // add the span element for info text
            SpanElement span = new SpanElement ();
            // create new image element for the info icon
            ImageElement infoImage = new ImageElement (this.sess.activeLayout.path + BOPathConstants.PATH_GLOBAL + BOConstants.IMG_INFO);
            // add tooltip text to info icon
            infoImage.title = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_TIP_USE_MULTISELECTION, env);
            // add info icon to span element
            span.addElement (infoImage);
            // align content
            span.style = "vertical-align: middle;";
            //span.style = "font-size: smaller; vertical-align: middle;";
            group.addElement (span);
            */

            // add span element for selected elements
            SpanElement span = new SpanElement ();
            span.style = "font-size: smaller;";
            span.id = "selected_" + name;
            group.addElement (span);

            StringBuffer scriptBuffer = new StringBuffer ()
                .append ("if (typeof printFocus == \"undefined\")")
                .append ("\n{")
                .append ("\n    function printFocus (elem)")
                .append ("\n    {")
                .append ("\n        var infoElem = this.document.getElementById (\"selected_\" + elem.name);")
                .append ("\n        infoElem.innerHTML += \"focus= \" + elem.name +")
                .append ("\n                              \" multiple= \" + elem.multiple + \"<BR>\";")
                .append ("\n    } // printFocus")
                .append ("\n    function printBlur (elem)")
                .append ("\n    {")
                .append ("\n        var infoElem = this.document.getElementById (\"selected_\" + elem.name);")
                .append ("\n        infoElem.innerHTML += \"blur= \" + elem.name +")
                .append ("\n                              \" multiple= \" + elem.multiple + \"<BR>\";")
                .append ("\n    } // printBlur")
                .append ("\n    function setSelectedElements (elem)")
                .append ("\n    {")
                .append ("\n        var infoElem = this.document.getElementById (\"selected_\" + elem.name);")
                .append ("\n        var text = \"\";")
                .append ("\n        var titletext = \"\";")
                .append ("\n        var comma = \"\";")
                .append ("\n        var titlecomma = \"\";")
                .append ("\n        var selectedLines = 0;")
                .append ("\n        var emptySelected = false;")
                .append ("\n        for (var i = 0; i < elem.length  ;i++)")
                .append ("\n        {")
                .append ("\n            if (elem.options [i].selected)")
                .append ("\n            {")
                .append ("\n                if (elem.options [i].text.length > 0)")
                .append ("\n                {")
                .append ("\n                    text += comma + elem.options [i].text;")
                .append ("\n                    comma = \", \";")
                .append ("\n                    selectedLines++;")
                .append ("\n                    titletext += titlecomma + selectedLines + \": \" + elem.options [i].text;")
                .append ("\n                    titlecomma = \"\\n\";")
                .append ("\n                } // if")
                .append ("\n                else")
                .append ("\n                {")
                .append ("\n                    emptySelected = true;")
                .append ("\n                } // else")
                .append ("\n            } // if")
                .append ("\n        } // for")
                .append ("\n        if (selectedLines == 1)")
                .append ("\n        {")
                .append ("\n            elem.size = 1;")
                .append ("\n            elem.multiple = false;")
                .append ("\n            elem.title = \"").append (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SELECTED, env)).append (":\\n\" + text;")
                .append ("\n            infoElem.innerHTML = \"\";")
                .append ("\n            infoElem.title = \"\";")
                .append ("\n        } // if")
                .append ("\n        else if (selectedLines > 0)")
                .append ("\n        {")
                .append ("\n            if (text.length > 80)")
                .append ("\n            {")
                .append ("\n                text = \"[\" + selectedLines + \"] \" + text.substring (0,80) + \"...\";")
                .append ("\n            } // if")
                .append ("\n            else")
                .append ("\n            {")
                .append ("\n                text = \"[\" + selectedLines + \"] \" + text;")
                .append ("\n            } // else")
                .append ("\n            text = \"<br>\" + text;")
                .append ("\n            titletext = \"").append (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SELECTED, env)).append (":\\n\" + titletext;")
                .append ("\n            elem.size = 2;")
                .append ("\n            infoElem.innerHTML = text;")
                .append ("\n            elem.title = titletext;")
                .append ("\n            infoElem.title = titletext;")
                .append ("\n        } // else if")
                .append ("\n        else")
                .append ("\n        {")
                .append ("\n            elem.size = 1;")
                .append ("\n            if (emptySelected)")
                .append ("\n            {")
                .append ("\n                elem.multiple = false;")
                .append ("\n            } // if")
                .append ("\n            elem.title = \"" + MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_TIP_USE_MULTISELECTION, env) + "\";")
                .append ("\n            infoElem.innerHTML = \"\";")
                .append ("\n            infoElem.title = \"\";")
                .append ("\n        } // else")
                .append ("\n    } // setSelectedElements")
                .append ("\n} // if");

            // if there was a preselected value make sure the display of the
            // selected elements is activated
            if (value != null && !value.isEmpty ())
            {
                scriptBuffer
                    .append ("\n setSelectedElements (this.document.getElementById ('")
                        .append (name).append ("'));");
            } // if

            ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);
            script.addScript (scriptBuffer);
            group.addElement (script);
        } // if more than one element
        return group;                    // return the created element
    } // getFormMultiSelectProperty


    /**************************************************************************
     * Sort a list of string elements. <BR/>
     *
     * @param   list        The list to be sorted.
     * @param   isSorted    Tells whether the values are already sorted.
     *                      If no they are sorted in ASCII sequence.
     *
     * @return  An array containing the indexes of the original array in
     *          sorted order.
     */
    protected int[] sortList (String[] list, boolean isSorted)
    {
        int length = list.length;       // number of elements in list
        int [] sorts = new int [length]; // sorted array (indexes of the list)
        int i;                          // loop variable
        int j;                          // loop variable
        int k;                          // loop variables

        // order the array:
        if (isSorted)                   // the array is already sorted?
        {
            // ensure that the current ordering stays unchanged:
            for (i = 0; i < length; i++)
            {
                sorts[i] = i;
            } // for i
        } // if the array is already sorted
        else                            // the array is not yet sorted
        {
            sorts[0] = 0;                   // initialize sorted array
            // loop through all elements of the list array:
            for (i = 1; i < length; i++)
            {
                // loop through all elements which are already sorted:
                for (j = i - 1; j > -1 &&
                    list[sorts[j]].compareTo (list[i]) > 0; j--)
                {
                    // nothing to do
                } // for j
                for (k = i - 1; k > j; k--)
                {
                    sorts[k + 1] = sorts[k];
                } // for
                sorts[j + 1] = i;
            } // for i
        } // else the array is not yet sorted

        // return the sorted list:
        return sorts;
    } // sortList


    /**************************************************************************
     * Ensure that there is no empty id. <BR/>
     * The function checks all elements of the ids vector. If an element is
     * <CODE>null</CODE> or empty it is replaced by the corresponding value.
     *
     * @param   ids         Array of ids for select field.
     * @param   values      Array of display values for select field.
     */
    protected void ensureValidIds (String[] ids, String[] values)
    {
        int length = ids.length;        // number of elements in id list
        int i;                          // loop variables

        if (length > 0)                 // at least one element?
        {
            // ensure that all ids are set:
            for (i = 0; i < length; i++)
            {
                // check if the current id is set:
                if (ids[i] == null || ids[i].length () == 0)
                {
                    // set the id to the value:
                    ids[i] = values[i];
                } // if
            } // for i
        } // if at least one element
    } // ensureValidIds


    /**************************************************************************
     * Find a value within a list. <BR/>
     * The method returns the first element of the list which is equal to value
     * or <CODE>-1</CODE> if the value does not exist within the list. <BR/>
     * If value is <CODE>null</CODE> and preIndex is an allowed index within
     * list the preIndex is returned.
     * If an element of the list is <CODE>null</CODE> a NullPointerException is
     * thrown.
     *
     * @param   list        List in which to search for the value.
     * @param   value       The value to search for.
     * @param   preIndex    Predefined index value.
     *
     * @return  Index of the found value.
     *          <CODE>-1</CODE> if the value was not found.
     *
     * @throws  NullPointerException
     *          An element of the list is <CODE>null</CODE>.
     */
    protected int findValue (String[] list, String value, int preIndex)
        throws NullPointerException
    {
        int length = list.length;       // number of elements in list
        int i = 0;                      // loop variable
        int valueIndex = -1;            // the found index
        String valueLocal = value;      // variable for assignments

        // check if the list is empty:
        if (length > 0)                 // at least one element?
        {
            if (valueLocal == null)          // no value defined?
            {
                if (preIndex >= 0 && preIndex < list.length)
                                        // preselected within allowed range?
                {
                    // set found index:
                    valueIndex = preIndex;
                } // if preselected within allowed range
            } // if no value defined
            else                        // value was defined
            {
                // search for the value:
                boolean found = false;  // nothing found thus far

                valueLocal = valueLocal.trim ();  // drop leading and ending spaces
                for (i = 0; i < length && !found; i++)
                {
                    found = valueLocal.equalsIgnoreCase (list[i].trim ());
                } // for i

                // check if we found the value:
                if (found)
                {
                    // remember the found index:
                    valueIndex = i - 1;
                } // if
            } // else value was defined
        } // if at least one element

        // return the result:
        return valueIndex;
    } // findValue


    /**************************************************************************
     * Show a multiple property to the user within a form. <BR/>
     *
     * @param   name        Name of the property.
     * @param   preselectedValues Value of the property.
     * @param   ids         Array of ids for select field.
     * @param   values      Array of display values for select field.
     * @param   size        The size of the selection field
     *
     * @return  The constructed form element.
     */
    private Element getMultipleFormSelectProperty (String name,
        String[] preselectedValues, String[] ids, String[] values, int size)
    {
        Element elem = null;            // the return element
        int length = 0;        // number of elements in selection
        boolean selected = false;
        if (ids != null)
        {
            length = ids.length;
        } // if
        int preselectedLength = 0;
        if (preselectedValues != null)
        {
            preselectedLength = preselectedValues.length;
        } // if
        if (length > 1)                 // more than one element?
        {
            SelectElement select = new SelectElement (name, name, false);
            select.size = size;            // just one line
            select.multiple = true;
            select.classId = CssConstants.CLASS_SELECT;

            int j;
            for (int i = 0; i < length; i++)
            {
                if (preselectedLength > 0)
                {
                    j = 0;
                    while (j < preselectedLength)
                    {
                        if (ids[i].equalsIgnoreCase (preselectedValues[j]))
                        {
                            select.addOption (values[i], ids[i], true);
                            selected = true;
                            break;
                        } // if

                        selected = false;
                        j++;
                    } // while
                } // if
                if (!selected)
                {
                    select.addOption (values[i], ids[i], false);
                } // if
            } // for

            elem = select;              // set the return element
        } // if more than one element
        else if (length == 1)           // exactly one element?
        {
            // create group containing the id as hidden field and the
            // value as output text:
            GroupElement group = new GroupElement ();
            group.addElement (new InputElement (name, InputElement.INP_HIDDEN, ids[0]));
            group.addElement (new TextElement (values[0]));

            elem = group;               // set the return element
        } // else if exactly one element
        else                            // no element
        {
            elem = new TextElement ("");
        } // else no element

        return elem;                    // return the created element
    } // getMultipleFormSelectProperty


    /**************************************************************************
     * Returns the base url where the several arguments can be appended. <BR/>
     * This URL contains a random value to ensure that the browser always
     * reloads the page. <BR/>
     * Arguments can be appended with the following code:
     * <CODE><PRE>
     * url = getBaseUrl () +
     *       HttpArguments.createArg (&lt;argument name BOArguments.ARG_xxx>, &lt;value>) +
     *       HttpArguments.createArg (&lt;argument name BOArguments.ARG_xxx>, &lt;value>) +
     *       ...
     *       HttpArguments.createArg (&lt;argument name BOArguments.ARG_xxx>, &lt;value>);
     * </PRE></CODE>. <BR/>
     *
     * @return  The base URL with a random parameter to prevent caching.
     */
    protected String getBaseUrl ()
    {
        // call common method and return the result:
        return IOHelpers.getBaseUrl (this.env);
    } // getBaseUrl


    /**************************************************************************
     * Returns the base url used for GET requests where the several arguments
     * can be appended. <BR/>
     * This URL contains a random value to ensure that the browser always
     * reloads the page. <BR/>
     * Arguments can be appended with the following code:
     * <CODE><PRE>
     * url = getBaseUrlGet () +
     *       HttpArguments.createArg (&lt;argument name BOArguments.ARG_xxx>, &lt;value>) +
     *       HttpArguments.createArg (&lt;argument name BOArguments.ARG_xxx>, &lt;value>) +
     *       ...
     *       HttpArguments.createArg (&lt;argument name BOArguments.ARG_xxx>, &lt;value>);
     * </PRE></CODE>. <BR/>
     *
     * @return  The base URL with a random parameter to prevent caching.
     */
    protected String getBaseUrlGet ()
    {
        // call common method and return the result:
        return IOHelpers.getBaseUrlGet (this.env);
    } // getBaseUrlGet


    /**************************************************************************
     * Returns the base url used for POST requests where the several arguments
     * can be appended. <BR/>
     * This URL contains a random value to ensure that the browser always
     * reloads the page. <BR/>
     * Arguments can be appended with the following code:
     * <CODE><PRE>
     * url = getBaseUrlPost () +
     *       HttpArguments.createArg (&lt;argument name BOArguments.ARG_xxx>, &lt;value>) +
     *       HttpArguments.createArg (&lt;argument name BOArguments.ARG_xxx>, &lt;value>) +
     *       ...
     *       HttpArguments.createArg (&lt;argument name BOArguments.ARG_xxx>, &lt;value>);
     * </PRE></CODE>. <BR/>
     *
     * @return The base URL with a random parameter to prevent caching.
     */
    protected String getBaseUrlPost ()
    {
        String temp = "";
        if (this.env != null)           // the environment is set?
        {
            // check if SSL is required and also available
            boolean sslRequired = Ssl.isSslRequired2 (this.sess);

            if (sslRequired)            // SSL is necessary
            {
                // get the secure URL
                temp = Ssl.getSecureUrl (IOHelpers.getBaseUrl
                    (this.env.getServerVariable (IOConstants.SV_URL)),
                    this.sess);
            } // if SSL is necessary
            else                        // SSL is not necessary
            {
                // get the non-secure URL
                temp = Ssl.getNonSecureUrl (IOHelpers.getBaseUrl
                    (this.env.getServerVariable (IOConstants.SV_URL)),
                    this.sess);
            } // else SSL is not necessary
        } // if the environment is set
        else                            // the environment is not set
        {
            temp = "";
        } // else the environment is not set

        return  temp;
    } // getBaseUrlPost


    /**************************************************************************
     * Returns the base URL for the actual mode (secure or non-secure) only. The
     * schema looks like: protocol + servername + serverport.
     * Note that this base URL is without the random number. <BR/>
     *
     * @return The base url.
     */
    protected String getBase ()
    {
        String temp = "";
        if (this.env != null)           // the environment is set?
        {
            // check if SSL is required and also available
            boolean sslRequired = Ssl.isSslRequired2 (this.sess);

            if (sslRequired)            // SSL is necessary
            {
                // get the secure base URL only, therefore the first parameter is ""
                temp = Ssl.getSecureUrl ("",
                                         this.sess);
            } // if SSL is necessary
            else                        // SSL is not necessary
            {
                // get the non-secure base URL only, therefore the first parameter is ""
                temp = Ssl.getNonSecureUrl ("",
                                            this.sess);
            } // else SSL is not necessary
        } // if the environment is set
        else                            // the environment is not set
        {
            temp = "";
        } // else the environment is not set

        return  temp;
    } // getBase


    /**************************************************************************
     * This method expands the formFieldRestrictions attribute with the given
     * JavaScript code. <BR/>
     * The formFieldRestrictions attribute will (at the end) hold all
     * restriction-check JavaScripts of the current form, which will be packed
     * into one function, wich will be called onSubmit.
     *
     * @param   restriction     JavaScript code of one form field restriction
     *                          check.
     *
     * @see #buildOnSubmitFormCheck
     */
    protected void expandFormFieldRestrictions (String restriction)
    {
        // restriction for one field will e.g. look like this:
        //
        // if (top.isIntGreaterOrEqual (tpam, 0, false)) {;;} else return false;
        //
        // what has to be added is "if (...) {} else return false;"

        // check if there restriction string is set
        if (restriction != null && restriction.length () > 0)
        {
            this.formFieldRestrictions +=
                "if (!" +
                restriction.toString () +
                ") return false;\n";
        } // if
    } // expandFormFieldRestrictions


    /**************************************************************************
     * This method expands the formFieldRestrictions attribute with the given
     * JavaScript code. <BR/>
     * The formFieldRestrictions attribute will (at the end) hold all
     * restriction-check JavaScripts of the current form, which will be packed
     * into one function, wich will be called onSubmit.
     *
     * @param   restriction     JavaScript code of one form field restriction
     *                          check.
     *
     * @see #buildOnSubmitFormCheck
     */
    protected void expandFormFieldRestrictions (StringBuffer restriction)
    {
        // call common method:
        this.expandFormFieldRestrictions (restriction.toString ());
    } // expandFormFieldRestrictions


    /**************************************************************************
     * This method adds one form field relation to the formFieldRelations
     * attribute by expanding the given formFieldRelation parameter. <BR/>
     *
     * The formFieldRelations attribute will (at the end) hold all
     * relation-check JavaScripts of the current form, which will be packed
     * into one function, wich will be called onSubmit.
     *
     * @param   relation     form field relation object
     *
     * @see #buildOnSubmitFormCheck
     */
    protected void addFormFieldRelation (FormFieldRelation relation)
    {
        // expand relation to java script code and add it to existing relations
        this.formFieldRelations += relation.buildRelationScriptCode (env) + "\n";
    } // expandFormFieldRestriction


    /**************************************************************************
     * This method finalizes the function wich will check all
     * form field restrictions of the current form. <BR/>
     * At this moment the formFieldRestrictions attribute holds all
     * restriction-check JavaScripts of the current form.
     * Now they must be included in one function wich will be called onSubmit.
     *
     * @return  JavaScript function which checks form fields on submit.
     *
     * @see #expandFormFieldRestrictions
     */
    protected String buildOnSubmitFormCheck ()
    {
        // function
        String submitAllowed;

        // build JavaScript function out of all formFieldRestriction and
        // formFieldRelation checks - this function will be added to current
        // form - will be called on submit
        submitAllowed =
            "function submitAllowed ()\n{\n" +
            "// restrictions\n" +
            this.formFieldRestrictions +
            "// relations\n" +
            this.formFieldRelations + "\n" +
            "// exit method\nreturn true;\n}";

        // reset restrictions and relations
        this.formFieldRestrictions = "";
        this.formFieldRelations = "";

        // exit method
        return submitAllowed;
    } // buildOnSubmitFormCheck


    /**************************************************************************
     * Show debugging text. <BR/>
     *
     * @param   text    Text to be printed out.
     */
    public void debug (StringBuffer text)
    {
        // call common method:
        this.debug (text.toString ());
    } // debug


    /**************************************************************************
     * Show debugging text. <BR/>
     *
     * @param   text    Text to be printed out.
     */
    public void debug (String text)
    {
        String textLocal = text;        // variable for local assignments

        textLocal = this.getClass ().getName () + ": " + textLocal;
//trace (text);

        IOHelpers.debug (textLocal, this.env);
    } // debug


    /**************************************************************************
     * Show a message as popup. <BR/>
     *
     * @param   message     Text of the message.
     */
    public void showPopupMessage (String message)
    {
        // call common method:
        IOHelpers.showPopupMessage (message, null, this.app, this.sess, this.env);
    } // showPopupMessage


    /**************************************************************************
     * Show a message as popup and has the option to add an additional script
     * to the page. The additional script will be executed after the message
     * alert. <BR/>
     *
     * @param   message     Text of the message.
     * @param   addScript   a javascript to add to the page
     */
    public void showPopupMessage (String message, ScriptElement addScript)
    {
        // call common method:
        IOHelpers.showPopupMessage (message, addScript, this.app, this.sess, this.env);
    } // showPopupMessage


    /**************************************************************************
     * Create a html page which is just processing a piece of JavaScript code
     * on the client. <BR/>
     *
     * @param   code    The complete code to be processed.
     */
    public void processJavaScriptCode (String code)
    {
        // call common method:
        IOHelpers.processJavaScriptCode (code, this.app, this.sess, this.env);
    } // processJavaScriptCode


    /**************************************************************************
     * Show a message that the requested oid is invalid. <BR/>
     *
     * @param   oidStr  String representation of incorrect oid to be shown
     *                  within message. <BR/>
     *                  <CODE>null</CODE>: don't show oid.
     */
    public void showIncorrectOidMessage (String oidStr)
    {
        // show the message to the user:
        if (oidStr == null)             // no oid for message?
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_INCORRECTOID, this.env),
                this.app, this.sess, this.env);
        } // if
        else                            // oid shall be shown in message
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_INCORRECTOID, new String[] {oidStr}, this.env),
                this.app, this.sess, this.env);
        } // else
    } // showIncorrectOidMessage


    /**************************************************************************
     * The chosen query is performed and the chosen column values
     * are returned. <BR/>
     * This method has to be implemented in a sub class which is able to
     * handle business objects.
     *
     * @param   queryname   name of the query which is filling the selectionbox
     * @param   idtype      databasetype of the ids
     * @param   emptyoption true if there is an empty item
     *
     * @return                  a SelectionList holding the desired
     *                          query data (ids and values)
     *                          or null
     */
    public SelectionList getQueryData (String queryname, String idtype, boolean emptyoption)
    {
        // return the default value:
        return null;
    } // getQueryData

    /**************************************************************************
     * The chosen query is performed and the chosen column values
     * are returned. <BR/>
     * This method has to be implemented in a sub class which is able to
     * handle business objects.
     *
     * @param   querystr           querystring to be executed
     * @param   idtype          databasetype of the ids
     * @param   emptyoption     true if there is an empty item
     *
     * @return                  a SelectionList holding the desired
     *                          query data (ids and values)
     *                          or null
     */
    public SelectionList getQueryData (StringBuffer querystr, String idtype,
                                        boolean emptyoption)
    {
        // return the default value:
        return null;
    } // getQueryData
    /**************************************************************************
     * Get the actual user info. <BR/>
     *
     * @return  The user info object.
     */
    protected UserInfo getUserInfo ()
    {
        return (UserInfo) this.sess.userInfo;
    } // getUserInfo


    /**************************************************************************
     * Get the actual user. <BR/>
     *
     * @return  The user object.
     */
    protected User getUser ()
    {
        return this.getUserInfo ().getUser ();
    } // getUser


    /**************************************************************************
     * Get the object cache. <BR/>
     *
     * @return  The cache object.
     */
    protected ObjectPool getObjectCache ()
    {
        return (ObjectPool) this.app.cache;
    } // getObjectCache


    /**************************************************************************
     * Get the type cache. <BR/>
     *
     * @return  The cache object.
     */
    protected TypeContainer getTypeCache ()
    {
        return ((ObjectPool) this.app.cache).getTypeContainer ();
    } // getTypeCache


    /**************************************************************************
     * Get the tab cache. <BR/>
     *
     * @return  The cache object.
     */
    protected TabContainer getTabCache ()
    {
        return ((ObjectPool) this.app.cache).getTabContainer ();
    } // getTabCache


    /**************************************************************************
     * Get the function handler cache. <BR/>
     *
     * @return  The cache object.
     */
    protected FunctionHandlerContainer getFunctionCache ()
    {
        return ((ObjectPool) this.app.cache).getFunctionHandlerContainer ();
    } // getFunctionCache


    /**************************************************************************
     * Get the tab cache. <BR/>
     *
     * @return  The cache object.
     */
    protected IConfiguration getConfiguration ()
    {
        return (IConfiguration) this.app.configuration;
    } // getConfiguration


    /**************************************************************************
     * This method creates a HTML header for the dummy page. <BR/>
     *
     * @param app The global application info.
     * @param sess The actual session info.
     * @param env Environment for getting input and generating output.
     */
    protected void createHTMLHeader (ApplicationInfo app,
                                  SessionInfo sess,
                                  Environment env)
    {
        // creates a new empty page
        this.page = new Page (false);

        // sets the base href
        IOHelpers.setBase (this.page, app, sess, env);

        // add a default Stylesheet
        this.insertInfoStyles (this.page);

        // builds the page header and show it to the user:
        try
        {
            this.page.buildHeader (env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e.getMsg (), app, sess, env);
        } // catch
    } // createHTMLHeader


    /**************************************************************************
     * This method creates a HTML footer for the dummy page. <BR/>
     *
     * * @param env Environment for getting input and generating output.
     */
    protected void createHTMLFooter (Environment env)
    {
        // builds the page header and show it to the user:
        try
        {
            this.page.buildFooter (env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e.getMsg (), this.app, this.sess, env);
        } // catch
    } // createHTMLFooter


    /**************************************************************************
     * Is HTML header for the dummy page already created. <BR/>
     *
     * @return <code>true</code> is page header has alreay been created or
     *            <code>false</code> otherwise
     */
    protected boolean isHTMLHeaderCreated ()
    {
        return this.page.getPageHeaderCreated ();
    } // isHTMLHeaderCreated


    /**************************************************************************
     * Insert style sheet information in a standard info view . <BR/>
     *
     * @param   page    The page into which the style sheets shall be inserted.
     */
    protected void insertInfoStyles (Page page)
    {
        // check if an active layout has been set
         // in case it is null this can indidicate an agent or observer login
        // that does not set the layout
        if (this.sess != null && this.sess.activeLayout != null)
        {
            StyleSheetElement style = new StyleSheetElement ();
            style.importSS = this.sess.activeLayout.path + this.env.getBrowser () +
                "/" +
                this.sess.activeLayout.elems[LayoutConstants.SHEETINFO].styleSheet;
            page.head.addElement (style);
        } // if (this.sess != null && this.sess.activeLayout != null)
    } // insertInfoStyles


} // class IbsObject
