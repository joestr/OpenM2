/*
 * Class: Note_01.java
 */

// package:
package ibs.obj.doc;

// imports:
import ibs.bo.BOArguments;
import ibs.bo.BOTokens;
import ibs.bo.BusinessObject;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.di.DataElement;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.workflow.WorkflowTokens;
import ibs.service.user.User;
import ibs.tech.html.TableElement;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.util.FormFieldRestriction;


/******************************************************************************
 * This class represents one object of type Dokument with version 01. <BR/>
 *
 * @version     $Id: Note_01.java,v 1.23 2010/05/20 07:59:00 btatzmann Exp $
 *
 * @author      Andreas Jansa (AJ), 981127
 ******************************************************************************
 */
public class Note_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Note_01.java,v 1.23 2010/05/20 07:59:00 btatzmann Exp $";


    ///////////////////////////////////////////////////////////////////////////
    // properties
    ///////////////////////////////////////////////////////////////////////////

    /**
     * content of the note (maybe HTML - code). <BR/>
     */
    protected String content = "";


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * This constructor creates a new instance of the class Note_01. <BR/>
     */
    public Note_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // Note_01


    /**************************************************************************
     * This constructor creates a new instance of the class Note_01. <BR/>
     * The compound object id is used as base for getting the
     * <A HREF="#server">server</A>, <A HREF="#type">type</A>, and
     * <A HREF="#id">id</A> of the business object. These values are stored in
     * the special public attributes of this type. <BR/>
     * The <A HREF="#user">user object</A> is also stored in a specific
     * attribute of this object to make sure that the user's context can be
     * used for getting his/her rights.
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public Note_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:
    } // Note_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set stored procedure names:
        this.procCreate =    "p_Note_01$create";
        this.procRetrieve =  "p_Note_01$retrieve";
        this.procDelete =    "p_Note_01$delete";
        this.procChange =    "p_Note_01$change";

        // set extended search flag:
        this.searchExtended = true;

        // set db table name:
        this.tableName = "ibs_Note_01";
    } // initClassSpecifics


    ///////////////////////////////////////////////////////////////////////////
    // functions called from application level
    ///////////////////////////////////////////////////////////////////////////

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
        if ((text = this.env.getParam (DocConstants.ARG_CONTENT)) != null)
        {
            this.content = text;
        } // if
    } // getParameters


    ///////////////////////////////////////////////////////////////////////////
    // database functions
    ///////////////////////////////////////////////////////////////////////////

   /***************************************************************************
    * Change all type specific data that is not changed by performChangeData.
    * <BR/>
    * This method must be overwritten by all subclasses that have to change
    * type specific data.
    *
    * @param action    SQL Action for Database
    *
    * @exception    DBError
    *               This exception is always thrown, if there happens an error
    *               during accessing data.
    */
    protected void performChangeSpecificData (SQLAction action) throws DBError
    {
        this.performChangeTextData (action, this.tableName, "content", this.content);
    } // performChangeSpecificData


    /**************************************************************************
     * Retrieve the type specific data that is not got from the stored
     * procedure. <BR/>
     * This method must be overwritten by all subclasses that have to get type
     * specific data that cannot be got from the retrieve data stored procedure.
     *
     * @param   action      The action object associated with the connection.
     *
     * @exception    DBError
     *               This exception is always thrown, if there happens an error
     *               during accessing data.
     */
    protected void performRetrieveSpecificData (SQLAction action) throws DBError
    {
        this.content = this.performRetrieveTextData (action, this.tableName,
            "content", "p_Note_01$getExtended");
    } // performRetrieveSpecificData


    ///////////////////////////////////////////////////////////////////////////
    // viewing functions
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Represent the properties of a Note_01 object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showProperties (TableElement table)
    {
        // display the base object's properties without description !!
        // super.showProperties (table);
//        marioQuery();
        // loop through all properties except description of this object and display them:
        this.showProperty (table, BOArguments.ARG_NAME, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NAME, env),
            Datatypes.DT_NAME, this.name);
        this.showProperty (table, BOArguments.ARG_TYPE, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_TYPE, env),
            Datatypes.DT_TYPE, this.getMlTypeName ());
        this.showProperty (table, BOArguments.ARG_INNEWS, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_INNEWS, env),
            Datatypes.DT_BOOL, "" + this.showInNews);
        this.showProperty (table, BOArguments.ARG_VALIDUNTIL,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_VALIDUNTIL, env), Datatypes.DT_DATE, this.validUntil);

        // ///////////////////////////////////////
        //
        // WORKFLOW-BLOCK: START
        // super not called - copy of super.showProperties.
        //
        // retrieve needed workflow instance information
        this.getWorkflowInstanceInfo ();

        // check if object has active workflow instance
        if (this.workflowInfo != null)
        {
            // show property to view workflow-instance
            this.showProperty (table,
                          "", //WorkflowArguments.xxx
                          MultilingualTextProvider.getText (WorkflowTokens.TOK_BUNDLE,
                              WorkflowTokens.ML_CURRENT_STATE, env),
                          Datatypes.DT_LINK,
                          this.workflowInfo.currentState,
                          this.workflowInfo.instanceId);
        } // if
        //
        // WORKFLOW-BLOCK: END
        //
        /////////////////////////////////////////

        if (this.getUserInfo ().userProfile.showExtendedAttributes)
        {
            this.showProperty (table, null, null, Datatypes.DT_SEPARATOR, (String) null);

            this.showProperty (table, BOArguments.ARG_OWNER,
                          MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_OWNER, env), Datatypes.DT_USER, this.owner);
            this.showProperty (table, BOArguments.ARG_CREATED,
                          MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_CREATED, env), Datatypes.DT_USERDATE,
                          this.creator, this.creationDate);
            this.showProperty (table, BOArguments.ARG_CHANGED,
                          MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_CHANGED, env), Datatypes.DT_USERDATE,
                          this.changer, this.lastChanged);
        } // if (this.getUserInfo ().userProfile.showExtendedAttributes)

        this.showProperty (table, null, null, Datatypes.DT_SEPARATOR, (String) null);

        // loop through all properties of this object and display them:
        this.showProperty (table, DocConstants.ARG_CONTENT,
                     MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_CONTENT, env), Datatypes.DT_HTMLTEXT, this.content);
    } // showProperties


    /**************************************************************************
     * Represent the properties of a Note_01 object to the user
     * within a form. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showFormProperties (TableElement table)
    {
        // display the base object's properties without description
        // super.showFormProperties (table);
        // property 'name':
        // set form field restriction (will automatically be used in
        // next showFormProperty)
        // restrict: no empty entries allowed
        this.formFieldRestriction =
            new FormFieldRestriction (false);
        // loop through all properties of this object and display them:
        this.showFormProperty (table, BOArguments.ARG_NAME,
                          MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NAME, env), Datatypes.DT_NAME, this.name);

        this.showFormProperty (table, BOArguments.ARG_INNEWS, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_INNEWS, env),
            Datatypes.DT_BOOL, "" + this.showInNews);
        // property 'validUntil':
        // set form field restriction (will automatically be used in
        // next showFormProperty)
        // restrict: no empty entries allowed
        // 0 .. default size/length values for datatype will be taken
        // null .. no upper bound
        this.formFieldRestriction =
            new FormFieldRestriction (false);
        this.showFormProperty (table, BOArguments.ARG_VALIDUNTIL,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_VALIDUNTIL, env), Datatypes.DT_DATE, this.validUntil);

        // loop through all properties of this object and display them:
        this.showFormProperty (table, DocConstants.ARG_CONTENT,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_CONTENT, env), Datatypes.DT_HTMLTEXT, this.content);
    } // showFormProperties


    /**************************************************************************
     * Is the object type allowed in workflows? <BR/>
     * This method shall be overwritten in subclasses.
     *
     * @return  <CODE>true</CODE> if the object type is allowed in workflows,
     *          <CODE>false</CODE> otherwise.
     */
    protected boolean isWfAllowed ()
    {
        return true;
    } // isWfAllowed


    //
    // import / export methods
    //

    /**************************************************************************
     * Reads the object data from an dataElement. <BR/>
     *
     * @param dataElement     the dataElement to read the data from
     */
    public void readImportData (DataElement dataElement)
    {
        // get business object specific values
        super.readImportData (dataElement);
        // get the type specific values
        if (dataElement.exists ("content"))
        {
            this.content = dataElement.getImportStringValue ("content");
        } // if
    } // readImportData


    /**************************************************************************
     * writes the object data to an dataElement. <BR/>
     *
     * @param dataElement     the dataElement to write the data to
     */
    public void writeExportData (DataElement dataElement)
    {
        // set the business object specific values
        super.writeExportData (dataElement);
        // set the type specific values
        dataElement.setExportValue ("content", this.content);
    } // writeExportData

} // class Note_01
