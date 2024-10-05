/*
 * Class: Article_01.java
 */

// package:
package m2.bbd;

// imports:
import ibs.app.CssConstants;
import ibs.bo.BOArguments;
import ibs.bo.BOMessages;
import ibs.bo.BOTokens;
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.Datatypes;
import ibs.bo.NoMoreElementsException;
import ibs.bo.OID;
import ibs.bo.ObjectNotFoundException;
import ibs.bo.Operations;
import ibs.di.DataElement;
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
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;
import ibs.util.DateTimeHelpers;
import ibs.util.FormFieldRestriction;
import ibs.util.NoAccessException;


/******************************************************************************
 * This class represents one object of type Article with version 01. <BR/>
 *
 * @version     $Id: Article_01.java,v 1.33 2013/01/16 16:14:10 btatzmann Exp $
 *
 * @author      Keim Christine (CK), 980522
 ******************************************************************************
 */
public class Article_01 extends Container implements DiscussionEntry
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Article_01.java,v 1.33 2013/01/16 16:14:10 btatzmann Exp $";


    /**
     * content of the entry. <BR/>
     */
    public String content;

    /**
     * The description of the container of this object. <BR/>
     * If {@link #containerContent containerContent} is empty this is used
     * instead.
     */
    public String containerDescription;

    /**
     * content of the container. <BR/>
     */
    public String containerContent;

    /**
     * level. <BR/>
     */
    public int level;

    /**
     * part of discussion of type. <BR/>
     */
    public int discussionType;

    /**
     * has the Entry Subentries. <BR/>
     */
    public int hasSubEntries;

    /**
     * Rights of the user on the Entry. <BR/>
     */
    public int rights;

    /**
     * Oid of the Discussion which the entry is part of. <BR/>
     */
    public OID discussionId;

    /**
     * Query column name for content.
     */
    private static final String QCN_CONTENT = "content";

    /**
     * XML tag name for content.
     */
    private static final String XML_CONTENT = Article_01.QCN_CONTENT;


    /**************************************************************************
     * This constructor creates a new instance of the class Article_01.
     * <BR/>
     */
    public Article_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // Article_01


    /**************************************************************************
     * Creates a Article_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public Article_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // Article_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set the class-procedureNames
        this.procCreate   = "p_Article_01$create";
        this.procChange   = "p_Article_01$change";
        this.procRetrieve = "p_Article_01$retrieve";
        this.procDelete   = "p_Article_01$delete";
        this.procChangeState = "p_Article_01$changeState";

        // set the instance's attributes:
        this.content = "";
        this.level = -1;
        this.hasSubEntries = 0;

        this.msgDeleteConfirm = BOMessages.ML_MSG_OBJECTDELETECONFIRM;

        // set extended search flag
        this.searchExtended = true;

        // set db table name
        this.tableName = "m2_Article_01";

        this.specificRetrieveParameters = 5;
    } // initClassSpecifics


    /**************************************************************************
     * Get the oid of the object which is the virtual container of the actual
     * object. <BR/>
     * For standard object this is the containerId.
     *
     * @return  The oid of the object which is the container of the actual
     *          object.
     *          <CODE>null</CODE> if there is no container found.
     */
    public OID getMajorContainerOid ()
    {
        // just return the actual container oid:
        return this.discussionId;
    } // getMajorContainerOid


    /**************************************************************************
     * Get the oid of the object which is in the hierarchy above the actual
     * object out of the database. <BR/>
     *
     * @return  The oid of the object which is above the actual object.
     *          null if there is no upper object.
     *
     * @exception   NoMoreElementsException
     *              The object is the topmost one.
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     * @exception   ObjectNotFoundException
     *              The object was not found.
     */
    public OID getUpperOid ()
        throws NoAccessException, ObjectNotFoundException, NoMoreElementsException
    {
        OID upperOid = null;            // oid of upper object
        if (true)                       // actual object resists on this
                                        // server?
        {
            // try to retrieve the upper object of this object:
            upperOid = this.discussionId;
        } // if actual object resists on this server
        else                            // object resists on another server
        {
            // invoke the object on the other server:
        } // else object resists on another server
        return upperOid;                // return the oid of the upper object
    } // getUpperOid


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * object info view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  An array with button ids that can be displayed.
     */
    protected int[] setInfoButtons ()
    {

        int[] buttons;

        // define buttons to be displayed:
        if (this.discussionType == 0)
        {
            int [] temp =
            {
                Buttons.BTN_EDIT,
                Buttons.BTN_DELETE,
//                Buttons.BTN_CUT,
//                Buttons.BTN_COPY,
                Buttons.BTN_DISTRIBUTE,
//                Buttons.BTN_CLEAN,
                Buttons.BTN_SEARCH,
    //            Buttons.BTN_HELP,
            }; // buttons
            buttons = temp;
        } // if
        else
        {
            int [] temp;
            if (this.hasSubEntries == 0)
            {

                int [] temp2 =
                {
                    Buttons.BTN_ANSWER,
                    Buttons.BTN_EDIT,
                    Buttons.BTN_DELETE,
//                    Buttons.BTN_COPY,
                    Buttons.BTN_DISTRIBUTE,
                    Buttons.BTN_SEARCH,
//                    Buttons.BTN_HELP,
                }; // buttons
                temp = temp2;
            } // if
            else
            {
                int [] temp2 =
                {
                    Buttons.BTN_ANSWER,
//                    Buttons.BTN_COPY,
                    Buttons.BTN_DISTRIBUTE,
                    Buttons.BTN_SEARCH,
//                    Buttons.BTN_HELP,
                }; // buttons
                temp = temp2;
            } // else
            buttons = temp;
        } // else

        // return button array
        return buttons;
    } // showInfoButtons


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
        // discussionType
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
        // hasSubEntries
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
        // rights
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
        // discussionId
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // containerDescription
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);

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
        this.discussionType = params[++i].getValueInteger ();
        this.hasSubEntries = params[++i].getValueInteger ();
        this.rights = params[++i].getValueInteger ();
        this.discussionId = SQLHelpers.getSpOidParam (params[++i]);
        this.containerDescription = params[++i].getValueString ();
    } // getSpecificRetrieveParameters


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
        OID backupOid = this.oid;       // used to back up the current oid
        String procGetExtended = "p_Article_01$getExtended";

        // get the content information of the object:
        this.content = this.performRetrieveTextData (action, this.tableName,
            Article_01.QCN_CONTENT, procGetExtended);

        // get the content information of the container:
        if (this.discussionType == 0)   // type blackboard
        {
            this.containerContent = this.containerDescription;
        } // if type blackboard
        else                            // other discussion type
        {
            backupOid = this.oid;       // store the actual oid
            this.oid = this.containerId; // set the containerId
            this.containerContent = this.performRetrieveTextData (action,
                this.tableName, Article_01.QCN_CONTENT, procGetExtended);
            this.oid = backupOid;       // re-set the actual oid
        } // else other discussion type
    } // performRetrieveSpecificData


    /***************************************************************************
     * Change all type specific data that is not changed by performChangeData.
     * <BR/>
     * This method must be overwritten by all subclasses that have to change
     * type specific data.
     *
     * @param   action  SQL Action for Database
     *
     * @exception    DBError
     *               This exception is always thrown, if there happens an error
     *               during accessing data.
     */
    protected void performChangeSpecificData (SQLAction action) throws DBError
    {
        this.performChangeTextData (action, this.tableName, Article_01.QCN_CONTENT, this.content);
    } // performChangeSpecificData


    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     * The <A HREF="#env">env</A> property is used for getting the parameters.
     * This property must be set before calling this method. <BR/>
     */
    public void getParameters ()
    {
        String text = null;

        // get other parameters
        super.getParameters ();

        // get content of the entry:
        if ((text = this.env.getParam (BbdArguments.ARG_CONTENT)) != null)
        {
            this.content = text;
        } // if
    } // getParameters


    /**************************************************************************
     * Represent the properties of a Article_01 object to the user. <BR/>
     *
     * @param   table   Table where the properties shall be added.
     *
     * @see ibs.bo.BusinessObject#showProperties
     * @see ibs.bo.BusinessObject#showProperty
     */
    protected void showProperties (TableElement table)
    {
        // loop through all properties of this object and display them:
        this.showProperty (table, BOArguments.ARG_NAME, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SUBJECT, env), Datatypes.DT_NAME, this.name);
        this.showProperty (table, BOArguments.ARG_TYPE, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_TYPE, env), Datatypes.DT_TYPE, this.getMlTypeName ());
        this.showProperty (table, BOArguments.ARG_INNEWS, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_INNEWS, env), Datatypes.DT_BOOL, "" + this.showInNews);
        this.showProperty (table, BbdArguments.ARG_CONTENT, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_CONTENT, env), Datatypes.DT_DESCRIPTION, this.content);
        this.showProperty (table, BOArguments.ARG_CREATED, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_CREATED, env), Datatypes.DT_TEXT,
            this.creator.toString () + ", " + DateTimeHelpers.dateTimeToString (this.creationDate));

        if (this.getUserInfo ().userProfile.showExtendedAttributes)
        {
            this.showProperty (table, null, null, Datatypes.DT_SEPARATOR, (String) null);
            this.showProperty (table, BOArguments.ARG_OWNER, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_OWNER, env), Datatypes.DT_USER, this.owner);
//            this.showProperty (table, BOArguments.ARG_CREATED, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_CREATED, env), Datatypes.DT_USERDATE, creator, creationDate);
            this.showProperty (table, BOArguments.ARG_CHANGED, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_CHANGED, env), Datatypes.DT_USERDATE, this.changer, this.lastChanged);
        } // if (app.userInfo.userProfile.showExtendedAttributes)

    } // showProperties


    /**************************************************************************
     * Represent the properties of a Article_01 object to the user
     * within a form. <BR/>
     *
     * @param   table   Table where the properties shall be added.
     *
     * @see ibs.bo.BusinessObject#showFormProperties
     * @see ibs.bo.BusinessObject#showFormProperty
     */
    protected void showFormProperties (TableElement table)
    {
        if (this.containerId.type == this.oid.type) // Answer to entry
        {
            this.showProperty (table, BbdArguments.ARG_DUMMY, 
                MultilingualTextProvider.getText (BbdTokens.TOK_BUNDLE,
                    BbdTokens.ML_ANSWER, env),
                Datatypes.DT_TEXT, this.containerName);
        } // if
        else
        {
            this.showProperty (table, BbdArguments.ARG_DUMMY,  
                MultilingualTextProvider.getText (BbdTokens.TOK_BUNDLE,
                    BbdTokens.ML_NEW, env),
                Datatypes.DT_TEXT, this.containerName);
        } // else
        if (this.discussionType != 0)
        {
            this.showProperty (table, BbdArguments.ARG_DUMMY, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_CONTENT, env), Datatypes.DT_TEXTAREA, this.containerContent);
        } // if
        this.showProperty (table, null, null, Datatypes.DT_SEPARATOR, (String) null);
        // property 'name':
        // set form field restriction (will automatically be used in
        // next showFormProperty)
        // restrict: no empty entries allowed
        this.formFieldRestriction =
            new FormFieldRestriction (false, 59, 0);

        // loop through all properties of this object and display them:
        this.showFormProperty (table, BOArguments.ARG_NAME, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SUBJECT, env), Datatypes.DT_NAME, this.name);
        this.showProperty (table, BOArguments.ARG_TYPE, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_TYPE, env), Datatypes.DT_TYPE, this.getMlTypeName ());
        this.showFormProperty (table, BOArguments.ARG_INNEWS, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_INNEWS, env), Datatypes.DT_BOOL, "" + this.showInNews);

        // restrict: no empty entries allowed
        this.formFieldRestriction =
            new FormFieldRestriction (true);
        this.showFormProperty (table, BbdArguments.ARG_CONTENT, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_CONTENT, env), Datatypes.DT_DESCRIPTION, this.content);
    } // showFormProperties


    /**************************************************************************
     * Show the object, i.e. its content. <BR/>
     * For the common ArticleObject this method calls
     * <A HREF="#showInfo">showInfo</A>.
     *
     * @param   representationForm  Kind of representation.
     */
    public void show (int representationForm)
    {
        // the view is not a content view:
        (this.getUserInfo ()).inContentView = false;
        // show the properties of the object:
        this.showInfo (representationForm);
    } // show


    /**************************************************************************
     * Show the quickview of the object. <BR/>
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
    } // show


    /**************************************************************************
     * Represent the object, i.e. its properties, to the user.
     * <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    private void performShowQuickView (int representationForm)
    {
        Page page = new Page (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_BUSINESSOBJECT, env), false);

/* ********
Stylesheet
********** */

        // create the stylesheets
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
        TableElement table = new TableElement (2);
        table.width = HtmlConstants.TAV_FULLWIDTH;
        table.border = 0;
        table.classId = CssConstants.CLASS_INFO;
        String[] classIds = new String[2];
        classIds[0] = CssConstants.CLASS_VALUE;
        classIds[1] = null;
        table.classIds = classIds;

        RowElement tr;
        if ((this.rights & Operations.OP_NEW) == Operations.OP_NEW)
        {
            tr = new RowElement (2);
        } // if
        else
        {
            tr = new RowElement (1);
        } // else

        // start with the object representation: show header

        TableDataElement td = new TableDataElement (IOHelpers.getTextField (this.content));

        td.width = HtmlConstants.TAV_FULLWIDTH;
        tr.addElement (td);

//            env.write (rights + " " + Operations.OP_NEW + " " + (rights & Operations.OP_NEW));
        if ((this.rights & (Operations.OP_NEW | Operations.OP_ADDELEM)) ==
            (Operations.OP_NEW | Operations.OP_ADDELEM))
        {
            // the answer button

            // create script for showing the button and add it to a new
            // cell within the current table row:
            ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);
            script.addScript ("top.showSingleButton ('" + this.oid + "', " + Buttons.BTN_ANSWER + ", document)");
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



    //
    // import / export methods
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
        if (dataElement.exists (Article_01.XML_CONTENT))
        {
            this.content = dataElement.getImportStringValue (Article_01.XML_CONTENT);
        } // if
    } // readImportData


    /**************************************************************************
     * writes the object data to an dataElement. <BR/>
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
        dataElement.setExportValue (Article_01.XML_CONTENT, this.content);

    } // writeExportData


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

} // class Article_01
