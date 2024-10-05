/*
 * Class: Help_01.java
 */

// package:
package ibs.obj.help;

// imports:
import ibs.bo.BOArguments;
import ibs.bo.BOTokens;
import ibs.bo.BusinessObject;
import ibs.bo.Buttons;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.di.DataElement;
import ibs.io.Environment;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.TableElement;
import ibs.tech.sql.DBError;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.StoredProcedure;
import ibs.util.FormFieldRestriction;
import ibs.util.Helpers;


/******************************************************************************
 * This class represents one object of type Help with version 01. <BR/>
 *
 * @version     $Id: Help_01.java,v 1.17 2013/01/16 16:14:14 btatzmann Exp $
 *
 * @author      Harald Buzzi (HB), 990609
 ******************************************************************************
 */
public class Help_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Help_01.java,v 1.17 2013/01/16 16:14:14 btatzmann Exp $";


    ///////////////////////////////////////////////////////////////////////////
    // properties
    ///////////////////////////////////////////////////////////////////////////


    /**
     * URL containing Helptext
     */
    protected String helpUrl = "";

    /**
     * related searchcontents
     */
    protected String searchContent = "";

    /**
     * related searchcontents
     */
    protected String goal = "";


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////


    /**************************************************************************
     * This constructor creates a new instance of the class Note_01. <BR/>
     */
    public Help_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // Help_01


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
    public Help_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:
    } // Help_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set stored procedure names:
        this.procCreate =    "p_Help_01$create";
        this.procRetrieve =  "p_Help_01$retrieve";
        this.procDelete =    "p_Help_01$delete";
        this.procChange =    "p_Help_01$change";

        // set db table name:
        this.tableName = "ibs_Help_01";

        // set extended search flag:
        this.searchExtended = true;

        // should be shown as frameset:
        this.showInfoAsFrameset = true;

        // number of specific parameters:
        this.specificChangeParameters = 2;
        this.specificRetrieveParameters = 2;
    } // initClassSpecifics


    /**************************************************************************
     * Initializes a BusinessObject object. <BR/>
     * The compound object id is stored in the <A HREF="#oid">oid</A> property
     * of this object. <BR/>
     * The <A HREF="#user">user object</A> is also stored in a specific
     * property of this object to make sure that the user's context can be used
     * for getting his/her rights. <BR/>
     * <A HREF="#env">env</A> is initialized to the provided object. <BR/>
     * <A HREF="#sess">sess</A> is initialized to the provided object. <BR/>
     * <A HREF="#app">app</A> is initialized to the provided object. <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     * @param   env     The actual call environment.
     * @param   sess    The actual session info.
     * @param   app     The global application info.
     */
    public void initObject (OID oid, User user, Environment env,
                            SessionInfo sess, ApplicationInfo app)
    {
        super.initObject (oid, user, env, sess, app);

        this.frm1Size = "*";
        this.frm2Size = "2*";
    } // initObject


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

        // get specifics of the entry:
        if ((text = this.env.getParam (BOArguments.ARG_HELPURL)) != null)
        {
            this.helpUrl = text;

            if (!(this.helpUrl.isEmpty ()) && !(this.helpUrl.equals (" ")))
            {
                this.helpUrl = Helpers.createUrlString (this.helpUrl);
                this.helpUrl.trim ();
            } // if
            else
            {
                this.helpUrl = "";
            } // else

            this.frm2Url = this.helpUrl;
        } // if
        if ((text = this.env.getParam (BOArguments.ARG_SEARCHCONTENT)) != null)
        {
            this.searchContent = text;
        } // if
        if ((text = this.env.getParam (BOArguments.ARG_GOAL)) != null)
        {
            this.goal = text;
        } // if
    } // getParameters


    ///////////////////////////////////////////////////////////////////////////
    // database functions
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Set the data for the additional (type specific) parameters for
     * performChangeData. <BR/>
     * This method must be overwritten by all subclasses that have to pass
     * type specific data to the change data stored procedure.
     *
     * @param sp        The stored procedure to add the change parameters to.
     */
    @Override
    protected void setSpecificChangeParameters (StoredProcedure sp)
    {
        // type-specific parameters:
        // Help URL
        Parameter paramUrl = sp.addParameter (new Parameter ());
        paramUrl.setDataType (ParameterConstants.TYPE_STRING);
        paramUrl.setDirection (ParameterConstants.DIRECTION_IN);

        if (!(this.helpUrl.isEmpty ()) && !(this.helpUrl.equals (" ")))
        {
            this.helpUrl = Helpers.createUrlString (this.helpUrl);
            this.helpUrl.trim ();
        } // if
        else
        {
            this.helpUrl = "";
        } // else

        this.frm2Url = this.helpUrl;

        paramUrl.setValue (this.helpUrl);
        // Searchcontent
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.searchContent);
    } // setSpecificChangeParameters


    /**************************************************************************
     * Change all type specific data that is not changed by performChangeData.
     * <BR/>
     * This method must be overwritten by all subclasses that have to change
     * type specific data.
     *
     * @param action    SQL Action for Database
     *
     * @exception    DBError
     *               This exception is always thrown, if there happens
     *               an error during accessing data.
     */
    protected void performChangeSpecificData (SQLAction action) throws DBError
    {
        // additional update, workaround
        if (this.goal == null)
        {
            this.goal = " ";
        } // if

        // update goal in ibs_Help_01
        this.performChangeTextData (action, "ibs_Help_01", "goal", this.goal);
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
        int i;

        i = lastIndex;

        // type-specific output parameters:
        // Help URL
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // Searchcontent
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);

        return i;               // return the current index
    } // setSpecificRetrieveParameters


    /**************************************************************************
     * Get the data for the additional (typespecific) parameters for
     * performRetrieveData. <BR/>
     * This method must be overwritten by all subclasses that have to get
     * typespecific data from the retrieve data stored procedure.
     *
     * @param params    The array of parameters from the retrieve data stored
     *                  procedure.
     * @param i         The index to the last element used in params thus far.
     */
    protected void getSpecificRetrieveParameters (Parameter[] params, int i)
    {
        int iLocal = i;                 // variable for local assignments
        this.helpUrl = params[++iLocal].getValueString ();

        if (!(this.helpUrl.isEmpty ()) && !(this.helpUrl.equals (" ")))
        {
            this.helpUrl = Helpers.createUrlString (this.helpUrl);
            this.helpUrl.trim ();
        } // if
        else
        {
            this.helpUrl = "";
        } // else

        this.frm2Url = this.helpUrl;

        this.searchContent = params[++iLocal].getValueString ();
    } // getSpecificRetrieveParameters


    /**************************************************************************
     * Retrieve the type specific data that is not got from the stored
     * procedure. <BR/>
     * This method must be overwritten by all subclasses that have to get
     * type specific data that cannot be got from the retrieve data stored
     * procedure.
     *
     * @param action    SQL Action for Database
     *
     * @exception    DBError
     *               This exception is always thrown, if there happens
     *               an error during accessing data.
     */
    protected void performRetrieveSpecificData (SQLAction action) throws DBError
    {
        this.goal = this.performRetrieveTextData (action, "ibs_Help_01", "goal",
                                             "p_Help_01$getExtended");
    } // performRetrieveSpecificData


    ///////////////////////////////////////////////////////////////////////////
    // viewing functions
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Represent the properties of a Help_01 object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showProperties (TableElement table)
    {
        // display the base object's properties without description !!
        // super.showProperties (table);
        // loop through all properties except description of this object
        // and display them:
        this.showProperty (table, BOArguments.ARG_SEARCHCONTENT,
                      MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SEARCHCONTENT, env), Datatypes.DT_TEXT,
                      this.searchContent);
    } // showProperties


    /**************************************************************************
     * Represent the properties of a Help_01 object to the user
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

        // property 'helpUrl':
        // set form field restriction (will automatically be used in
        // next showFormProperty)
        // restrict: no empty entries allowed
        this.formFieldRestriction =
            new FormFieldRestriction (false);
        // loop through all properties of this object and display them:
        this.showFormProperty (table, BOArguments.ARG_HELPURL,
                          MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_DTURL, env), Datatypes.DT_URL, this.helpUrl);
        this.showFormProperty (table, BOArguments.ARG_SEARCHCONTENT,
                          MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SEARCHCONTENT, env), Datatypes.DT_NAME,
                          this.searchContent);
    } // showFormProperties


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in an
     * object's info view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  An array with button ids that can potentially be displayed.
     */
    protected int[] setInfoButtons ()
    {
        // define buttons to be displayed:
        int [] buttons =
        {
            Buttons.BTN_EDIT,
            Buttons.BTN_DELETE,
            Buttons.BTN_COPY,
            Buttons.BTN_SEARCH,
            Buttons.BTN_HELP,
        }; // buttons

        // return button array
        return buttons;
    } // setInfoButtons


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
        if (dataElement.exists ("helpUrl"))
        {
            this.helpUrl = dataElement.getImportStringValue ("helpUrl");
        } // if
        if (dataElement.exists ("searchContent"))
        {
            this.searchContent = dataElement
                .getImportStringValue ("searchContent");
        } // if
        if (dataElement.exists ("goal"))
        {
            this.goal = dataElement.getImportStringValue ("goal");
        } // if
    } // readImportData


    /**************************************************************************
     * Writes the object data to an dataElement. <BR/>
     *
     * @param dataElement     the dataElement to write the data to
     */
    public void writeExportData (DataElement dataElement)
    {
        // set the business object specific values
        super.writeExportData (dataElement);
        // set the type specific values
        dataElement.setExportHyperlinkValue ("helpUrl", this.helpUrl);
        dataElement.setExportValue ("searchContent", this.searchContent);
        dataElement.setExportValue ("goal", this.goal);
    } // writeExportData

} // class Help_01
