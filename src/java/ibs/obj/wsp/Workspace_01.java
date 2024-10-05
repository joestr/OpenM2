/*
 * Class: Workspace_01.java
 */

// package:
package ibs.obj.wsp;

// imports:
import ibs.bo.BOHelpers;
import ibs.bo.BOTokens;
import ibs.bo.Container;
import ibs.bo.OID;
import ibs.bo.States;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.SelectElement;
import ibs.tech.html.TableElement;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;
import ibs.tech.sql.StoredProcedureConstants;
import ibs.util.NoAccessException;
import ibs.util.UtilExceptions;


/******************************************************************************
 * This class represents one object of type Workspace with version 01. <BR/>
 *
 * @version     $Id: Workspace_01.java,v 1.18 2013/01/16 16:14:12 btatzmann Exp $
 *
 * @author      Klaus Reimüller (KR), 980618
 ******************************************************************************
 */
public class Workspace_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Workspace_01.java,v 1.18 2013/01/16 16:14:12 btatzmann Exp $";


    /**
     * The domain where the workspace resists. <BR/>
     */
    public int domainId = 0;

    /**
     * The workspace object itself. <BR/>
     */
    public OID workspace = null;

    /**
     * The work box of the user containing all objects he works with. <BR/>
     */
    public OID workBox = null;

    /**
     * The out box of the user containing all objects the user has sent to
     * others. <BR/>
     */
    public OID outBox = null;

    /**
     * The in box of the user containing all objects the user got within a
     * specific range of time. <BR/>
     */
    public OID inBox = null;

    /**
     * The news container contains all object which are new, i.e. which are
     * created or changed within a specific time range. <BR/>
     */
    public OID news = null;

    /**
     * The hotlist is a list of references to objects which are relevant to
     * the user. <BR/>
     * The user himself copies objects and adds them to the hotlist.
     */
    public OID hotList = null;

    /**
     * The user profile holds user specific preferences. <BR/>
     */
    public OID profile = null;

    /**
     * The public container which is parallel to this workspace. <BR/>
     */
    public OID publicWsp = null;

    /**
     * The shopping cart of the user contains all objects the user wants to
     * order. <BR/>
     */
    public OID shoppingCart = null;

    /**
     * The orders which the user has made yet. <BR/>
     */
    public OID orders = null;

    /**
     * Stored procedure to retrieve the workspace data for the actual user. <BR/>
     * The value of this property can be overwritten within the
     * constructor of each subtype.
     */
    protected String procRetrieveForActualUser =
        "p_Workspace_01$retrieveForActU";


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * This constructor creates a new instance of the class Workspace_01.
     * <BR/>
     */
    public Workspace_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // Workspace_01


    /**************************************************************************
     * This constructor creates a new instance of the class Workspace_01. <BR/>
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
    public Workspace_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
    } // Workspace_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set common attributes:
        this.procCreate = "p_Workspace_01$create";
        this.procChange = "p_Workspace_01$change";
        this.procRetrieve = "p_Workspace_01$retrieve";
/*
        this.procCopy = "p_Workspace_01$copy";
*/
        this.procDelete = "p_Workspace_01$delete";
        // set number of parameters for procedure calls:
        this.specificRetrieveParameters = 11;
    } // initClassSpecifics


    ///////////////////////////////////////////////////////////////////////////
    // functions called from application level
    ///////////////////////////////////////////////////////////////////////////

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
        // domainId
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
        // workspace
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // workBox
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // outBox
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // inBox
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // news
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // hotList
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // profile
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // publicWsp
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // shoppingCart
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // orders
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);

        return i;                       // return the current index
    } // setSpecificRetrieveParameters


   /***************************************************************************
    * Get the data for the additional (type specific) parameters for
    * performRetrieveData. <BR/>
    * This method must be overwritten by all subclasses that have to get
    * type specific data from the retrieve data stored procedure.
    *
    * @param params        The array of parameters from the retrieve data stored
    *                   procedure.
    * @param lastIndex    The index to the last element used in params thus far.
    */
    protected void getSpecificRetrieveParameters (Parameter[] params,
                                                  int lastIndex)
    {

        int i = lastIndex;              // initialize params index

        // get the specific parameters:
        // get type-specific properties:
        this.domainId = params[++i].getValueInteger ();
        this.workspace = SQLHelpers.getSpOidParam (params[++i]);
        this.workBox = SQLHelpers.getSpOidParam (params[++i]);
        this.outBox = SQLHelpers.getSpOidParam (params[++i]);
        this.inBox = SQLHelpers.getSpOidParam (params[++i]);
        this.news = SQLHelpers.getSpOidParam (params[++i]);
        this.hotList = SQLHelpers.getSpOidParam (params[++i]);
        this.profile = SQLHelpers.getSpOidParam (params[++i]);
        this.publicWsp = SQLHelpers.getSpOidParam (params[++i]);
        this.shoppingCart = SQLHelpers.getSpOidParam (params[++i]);
        this.orders = SQLHelpers.getSpOidParam (params[++i]);
    } // getSpecificRetrieveParameters


    /**************************************************************************
     * Get a the workspace date of the actual user out of the database. <BR/>
     * First this method tries to load the object from the database. During this
     * operation a rights check is done, too. If this is all right the object is
     * returned otherwise an exception is raised. <BR/>
     *
     * @param   operation   Operation to be performed with the object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    protected void performRetrieveForActualUserData (int operation)
        throws NoAccessException
    {
        // create the stored procedure call:
        StoredProcedure sp = new StoredProcedure(
                this.procRetrieveForActualUser,
                StoredProcedureConstants.RETURN_VALUE);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // input parameters
/*
        // oid
        params[++i] = new Parameter ();
        params[i].setDataType (ParameterConstants.TYPE_STRING);
        params[i].setDirection (ParameterConstants.DIRECTION_IN);
        if (this.oid != null)
            params[i].setValue (this.oid.toString ());
        else
            params[i].setValue (BOConstants.OID_NOOBJECT);
*/
        // user id
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, (this.user != null) ? this.user.id : 0);
        // operation
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, operation);

        // output parameters
        // domainId
        Parameter domainIdOutParam = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
        // workspace
        Parameter workspaceOutParam = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // workBox
        Parameter workBoxOutParam = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // outBox
        Parameter outBoxOutParam = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // inBox
        Parameter inBoxOutParam = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // news
        Parameter newsOutParam = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // hotList
        Parameter hotListOutParam = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // profile
        Parameter profileOutParam = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // publicWsp
        Parameter publicWspOutParam = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // shoppingCart
        Parameter shoppingCartOutParam = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // orders
        Parameter ordersOutParam = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // name
        Parameter nameOutParam = sp.addOutParameter (ParameterConstants.TYPE_STRING);

        // perform the function call:
        BOHelpers.performCallFunctionData (sp, this.env);

        // set object properties - get them out of parameters
        this.domainId = domainIdOutParam.getValueInteger ();
        this.workspace = SQLHelpers.getSpOidParam (workspaceOutParam);
        this.setOid (this.workspace);
        this.workBox = SQLHelpers.getSpOidParam (workBoxOutParam);
        this.outBox = SQLHelpers.getSpOidParam (outBoxOutParam);
        this.inBox = SQLHelpers.getSpOidParam (inBoxOutParam);
        this.news = SQLHelpers.getSpOidParam (newsOutParam);
        this.hotList = SQLHelpers.getSpOidParam (hotListOutParam);
        this.profile = SQLHelpers.getSpOidParam (profileOutParam);
        this.publicWsp = SQLHelpers.getSpOidParam (publicWspOutParam);
        this.shoppingCart = SQLHelpers.getSpOidParam (shoppingCartOutParam);
        this.orders = SQLHelpers.getSpOidParam (ordersOutParam);

        this.name = nameOutParam.getValueString ();
    } // performRetrieveForActualUserData


    ///////////////////////////////////////////////////////////////////////////
    // database function interfaces
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Get a business object out of the database. <BR/>
     * This method checks if the object was already loaded into memory. In this
     * case it checks if the user's rights are sufficient to perform the
     * requested operation on the object. If this is all right the object is
     * returned otherwise an exception is raised. <BR/>
     * If the object is not already in the memory it must be loaded from the
     * database. In this case there is also a rights check done. If this is all
     * right the object is returned otherwise an exception is raised. <BR/>
     *
     * @param   operation   Operation to be performed with the object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    public final void retrieveForActualUser (int operation)
        throws NoAccessException
    {
        if (this.isActual ())           // object already in memory?
        {
            // check user's rights:
            if (!this.user.hasRights (this.oid, operation)) // access not allowed?
            {
                // raise no access exception
                NoAccessException error = new NoAccessException (MultilingualTextProvider
                    .getMessage (UtilExceptions.EXC_BUNDLE, UtilExceptions.ML_E_NOACCESSEXCEPTION, env));
                throw error;
            } // if access not allowed
        } // if object already in memory
        else                            // object not in memory
        {
            // call the object type specific method:
            this.state = States.ST_UNKNOWN;
            this.performRetrieveForActualUserData (operation);
            if (this.state == States.ST_DELETED)
            {
//              showMessage ("Objekt konnte nicht gefunden werden!"); // HACK!!!
                throw new NoAccessException (MultilingualTextProvider
                    .getMessage (UtilExceptions.EXC_BUNDLE, UtilExceptions.ML_E_OBJECTDELETED,
                        new String[] {this.name}, env) + " (" + this.oid + ")");
            } // if
            this.setActual ();          // the data within the object are
                                        // actual
        } // else object not in memory
    } // retrieveForActualUser


    ///////////////////////////////////////////////////////////////////////////
    // viewing functions
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Represent the properties of a Workspace_01 object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showProperties (TableElement table)
    {
        // display the base object's properties:
        super.showProperties (table);
        // loop through all properties of this object and display them:
/*
        this.showProperty (table, ibs.app.AppArguments.ARG_VALIDUNTIL, TOK_VALIDUNTIL, ibs.bo.Datatypes.DT_DATE,
            Helpers.dateToString (validUntil));
*/
    } // showProperties


    /**************************************************************************
     * Represent the properties of a Workspace_01 object to the user
     * within a form. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showFormProperties (TableElement table)
    {
        // display the base object's properties:
        super.showFormProperties (table);
        // loop through all properties of this object and display them:
/*
        this.showFormProperty (table, ibs.app.AppArguments.ARG_VALIDUNTIL, TOK_VALIDUNTIL, ibs.bo.Datatypes.DT_DATE,
            Helpers.dateToString (validUntil));
*/
    } // showFormProperties


    /**************************************************************************
     * Defines the activity entries being displayed in the activities selection
     * box. <BR/>
     *
     * @param   sel     Selection box element.
     * @param   pa      Preselected activity.
     */
    protected void addActivities (SelectElement sel, String pa)
    {
        // display the base object's acitivities:
        super.addActivities (sel, pa);
        // loop through all activities of this class and display them:
        this.addActivity (sel, pa, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_ACT_CHIEF_ONLY, env));
    } // addActivities

} // class Workspace_01
