/*
 * Class: Thread_01.java
 */

// package:
package m2.bbd;

// imports:
import ibs.app.CssConstants;
import ibs.bo.BOArguments;
import ibs.bo.BOConstants;
import ibs.bo.BOMessages;
import ibs.bo.BOTokens;
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.bo.Operations;
import ibs.io.HtmlConstants;
import ibs.io.IOHelpers;
import ibs.io.LayoutConstants;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.BlankElement;
import ibs.tech.html.BuildException;
import ibs.tech.html.GroupElement;
import ibs.tech.html.Page;
import ibs.tech.html.RowElement;
import ibs.tech.html.ScriptElement;
import ibs.tech.html.StyleSheetElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TableElement;
import ibs.tech.sql.DBError;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.StoredProcedure;
import ibs.util.FormFieldRestriction;
import ibs.util.NoAccessException;

import m2.bbd.BbdTokens;
import m2.bbd.DiscussionEntry;


/******************************************************************************
 * This class represents one object of type Dokument with version 01. <BR/>
 *
 * @version     $Id: Thread_01.java,v 1.24 2013/01/16 16:14:10 btatzmann Exp $
 *
 * @author      Keim Christine (CK)  980507
 ******************************************************************************
 */
public class Thread_01 extends Container implements DiscussionEntry
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Thread_01.java,v 1.24 2013/01/16 16:14:10 btatzmann Exp $";


    /**
     * has the Entry Subentries. <BR/>
     */
    private int hasSubEntries;

    /**
     * Rights of the User on the topic. <BR/>
     */
    private int rights;

    /**
     * Query column name for content.
     */
    private static final String QCN_CONTENT = "content";


    /**************************************************************************
     * This constructor creates a new instance of the class Thread_01. <BR/>
     */
    public Thread_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // Thread_01



    /**************************************************************************
     * Creates a Thread_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public Thread_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // Thread_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        this.hasSubEntries = 0;

        this.msgDeleteConfirm = BOMessages.ML_MSG_OBJECTDELETECONFIRM;

        this.procCreate = "p_Thread_01$create";
        this.procChange = "p_Thread_01$change";
        this.procRetrieve = "p_Thread_01$retrieve";
        this.procDelete = "p_Thread_01$delete";
        this.procChangeState = "p_Article_01$changeState";

        // set db table name:
        this.tableName = "m2_Article_01";

        this.specificRetrieveParameters = 2;
    } // initClassSpecifics


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in an
     * object info view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  An array with button ids that can be displayed.
     */
    protected int[] setInfoButtons ()
    {
        int [] buttons;
        if (this.hasSubEntries == 0)
        {
            // define buttons to be displayed:
            int [] temp =
            {
                Buttons.BTN_NEW,
                Buttons.BTN_EDIT,
                Buttons.BTN_DELETE,
    //            Buttons.BTN_COPY,
                Buttons.BTN_DISTRIBUTE,
    //            Buttons.BTN_CLEAN,
                Buttons.BTN_SEARCH,
    //            Buttons.BTN_HELP,
            }; // buttons
            buttons = temp;
        } // if
        else
        {
            int [] temp =
            {
                Buttons.BTN_NEW,
    //            Buttons.BTN_COPY,
                Buttons.BTN_DELETE,
                Buttons.BTN_DISTRIBUTE,
    //            Buttons.BTN_CLEAN,
                Buttons.BTN_SEARCH,
    //            Buttons.BTN_HELP,
            }; // buttons
            buttons = temp;
        } // else

        // return button array:
        return buttons;
    } // showInfoButtons


    /**************************************************************************
     * Retrieve the type specific data that is not got from the stored
     * procedure. <BR/>
     * This method must be overwritten by all subclasses that have to get type
     * specific data that cannot be got from the retrieve data stored procedure.
     *
     * @param   action  SQL Action for database.
     *
     * @exception   DBError
     *              This exception is always thrown, if there happens an error
     *              during accessing data.
     */
    protected void performRetrieveSpecificData (SQLAction action) throws DBError
    {
        this.description = this.performRetrieveTextData (action,
            this.tableName, Thread_01.QCN_CONTENT, "p_Thread_01$getExtended");
    } // performRetrieveSpecificData


    /**************************************************************************
     * Change all type specific data that is not changed by performChangeData.
     * <BR/>
     * This method must be overwritten by all subclasses that have to change
     * type specific data.
     *
     * @param   action  SQL Action for database.
     *
     * @exception   DBError
     *              This exception is always thrown, if there happens an error
     *              during accessing data.
     */
    protected void performChangeSpecificData (SQLAction action) throws DBError
    {
        this.performChangeTextData (action, this.tableName, Thread_01.QCN_CONTENT, this.description);
    } // performChangeSpecificData


    /**************************************************************************
     * Set the data for the additional (type specific) parameters for
     * performRetrieveData. <BR/>
     * This method must be overwritten by all subclasses that have to pass
     * type specific data to the retrieve data stored procedure.
     *
     * @param sp        The stored procedure the specific retrieve parameters
     *                  should be added to.
     * @param params    Array of parameters the specific retrieve parameters
     *                  have to be added to for beeing able to retrieve the
     *                  results within getSpecificRetrieveParameters.
     * @param lastIndex The index to the last element used in params thus far.
     *
     * @return  The index of the last element used in params.
     */
    @Override
    protected int setSpecificRetrieveParameters (StoredProcedure sp, Parameter[] params,
                                                 int lastIndex)
    {
        int i = lastIndex;              // initialize params index

        // set the specific parameters:
        // hasSubEntries
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
        // rights
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);

        return i;                       // return the current index
    } // setSpecificRetrieveParameters


    /**************************************************************************
     * Get the data for the additional (type specific) parameters for
     * performRetrieveData. <BR/>
     * This method must be overwritten by all subclasses that have to get
     * type specific data from the retrieve data stored procedure.
     *
     * @param   params      The array of parameters from the retrieve data stored
     *                      procedure.
     * @param   lastIndex   The index to the last element used in params thus far.
     */
    protected void getSpecificRetrieveParameters (Parameter[] params,
                                                  int lastIndex)
    {
        int i = lastIndex;              // initialize params index

        // get the specific parameters:
        this.hasSubEntries = params[++i].getValueInteger ();
        this.rights = params[++i].getValueInteger ();
    } // getSpecificRetrieveParameters


    /**************************************************************************
     * Show the object, i.e. its content. <BR/>
     * For the common Thread_01 this method calls
     * <A HREF="#showInfo">showInfo</A>.
     *
     * @param   representationForm  Kind of representation.
     */
    public void show (int representationForm)
    {
        // the view is not a content View
        (this.getUserInfo ()).inContentView = false;
        // show the properties of the object:
        this.showInfo (representationForm);
    } // show


    /**************************************************************************
     * Show the quick view of the object. <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    public void quickView (int representationForm)
    {
        // show the properties of the object:
        this.showQuickView (representationForm);
    } // show


    /**************************************************************************
     * Show the object's quickView. <BR/>
     * The properties are gotten from the database (with rights checked) and
     * represented to the user in the required form. <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    public void showQuickView (int representationForm)
    {
        if (true)                       // business object resists on this
                                        // server?
        {
            try
            {
/* KR 020125: not necessary because already done before
                // try to retrieve the object:
                retrieve (Operations.OP_READ);
*/
                if (this.getUserInfo ().userProfile.showRef)
                {
                    this.performRetrieveRefs (Operations.OP_VIEW);
                } // if

                // show the object's data:
                this.performShowQuickView (representationForm);
            } // try
            catch (NoAccessException e) // no access to objects allowed
            {
                // send message to the user:
                this.showNoAccessMessage (Operations.OP_READ);
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
            // invoke the object on the other server:
        } // else object resists on another server
    } // showQuickView


    /**************************************************************************
     * Represent the object, i.e. its properties, to the user. <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    private void performShowQuickView (int representationForm)
    {
        Page page = new Page (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
            BOTokens.ML_BUSINESSOBJECT, env), false);

/* ********
Stylesheet
********** */

        // create the style sheets:
        StyleSheetElement stylesheet = new StyleSheetElement ();

        // buttons
        stylesheet = new StyleSheetElement ();
        stylesheet.importSS = this.sess.activeLayout.path + this.env.getBrowser () + "/" + this.sess.activeLayout.elems[LayoutConstants.BUTTONBAR].styleSheet;
        page.head.addElement (stylesheet);
        stylesheet = new StyleSheetElement ();
        stylesheet.importSS = this.sess.activeLayout.path + this.env.getBrowser () + "/" + this.sess.activeLayout.elems[LayoutConstants.SHEETINFO].styleSheet;

        // add the stylesheet to the page
        page.head.addElement (stylesheet);

        // set the icon of this object:
        this.setIcon ();

        // create Header
        GroupElement body = this.createHeader (page, this.name,
                                null, null, null, this.icon, this.containerName);
//        GroupElement body = createHeader (page, this.name);
        TableElement table = new TableElement (1);
        table.width = HtmlConstants.TAV_FULLWIDTH;
        table.border = 0;

        table.classId = CssConstants.CLASS_INFO;
        String[] classIds = new String[2];
        classIds[0] = CssConstants.CLASS_VALUE;
        classIds[1] = null;
        table.classIds = classIds;

        RowElement tr;
        if ((this.rights & ibs.bo.Operations.OP_NEW) == ibs.bo.Operations.OP_NEW)
        {
            tr = new RowElement (2);
        } // if
        else
        {
            tr = new RowElement (1);
        } // else

        // start with the object representation: show header
        TableDataElement td = new TableDataElement (IOHelpers.getTextField (this.description));

        td.width = HtmlConstants.TAV_FULLWIDTH;
        tr.addElement (td);

//            this.env.write (rights + " " + Operations.OP_NEW + " " + (rights & Operations.OP_NEW));
        if ((this.rights & (Operations.OP_NEW | Operations.OP_ADDELEM)) == (Operations.OP_NEW | Operations.OP_ADDELEM))
        {
            // the new button
            // create script for showing the button and add it to a new
            // cell within the current table row:
            ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);
            script.addScript ("top.showSingleButton ('" + this.oid + "', " + Buttons.BTN_NEW + ", document)");
            tr.addElement (new TableDataElement (script));
            tr.addElement (new TableDataElement (new BlankElement (3)));
        } // if

        table.addElement (tr);

        if (this.getUserInfo ().userProfile.showRef)
        {
            this.showRefs (table, page);
        } // if

        body.addElement (table);

        // build the page and show it to the user:
        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
    } // performShowQuickView


    /**************************************************************************
     * Represent the properties of a BusinessObject object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
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
            MultilingualTextProvider.getText (BbdTokens.TOK_BUNDLE,
                BbdTokens.ML_TITLENAME, env), 
            Datatypes.DT_NAME, this.name);
        this.showFormProperty (table, BOArguments.ARG_INNEWS, 
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                BOTokens.ML_INNEWS, env),
            Datatypes.DT_BOOL, "" + this.showInNews);
        // restrict: empty entries allowed
        this.formFieldRestriction =
            new FormFieldRestriction (true, BOConstants.MAX_LENGTH_DESCRIPTION, 0);
        this.showFormProperty (table, BOArguments.ARG_DESCRIPTION, 
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                BOTokens.ML_DESCRIPTION, env),
            Datatypes.DT_DESCRIPTION, this.description);

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
                BOTokens.ML_VALIDUNTIL, env), 
            Datatypes.DT_DATE, this.validUntil);
    } // showFormProperties


    /**************************************************************************
     * Represent the properties of a BusinessObject object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showProperties (TableElement table)
    {
        // loop through all properties of this object and display them:
        this.showProperty (table, BOArguments.ARG_NAME,  
            MultilingualTextProvider.getText (BbdTokens.TOK_BUNDLE,
                BbdTokens.ML_TITLENAME, env), 
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
                BOTokens.ML_DESCRIPTION, env), 
            Datatypes.DT_DESCRIPTION, this.description);
        this.showProperty (table, BOArguments.ARG_VALIDUNTIL, 
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                BOTokens.ML_VALIDUNTIL, env), 
            Datatypes.DT_DATE, this.validUntil);

        if (this.getUserInfo ().userProfile.showExtendedAttributes)
        {
            this.showProperty (table, null, null, ibs.bo.Datatypes.DT_SEPARATOR, (String) null);

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
        } // if (app.userInfo.userProfile.showExtendedAttributes)

        this.showProperty (table, null, null, Datatypes.DT_SEPARATOR, (String) null);
    } // showProperties


    /**************************************************************************
     * Check if the "safe and new" option within the NEW dialog is allowed.
     * <BR/>
     * In this case the option is not allowed.
     *
     * @return  <CODE>true</CODE> if the option may be displayed,
     *          <CODE>false</CODE> otherwise.
     *          Default: <CODE>true</CODE>
     */
    protected boolean isSafeAndNewAllowed ()
    {
        // return the value:
        return false;
    } // isSafeAndNewAllowed

} // class Thread_01
