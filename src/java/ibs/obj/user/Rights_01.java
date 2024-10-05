/*
 * Class: Rights_01.java
 */

// package:
package ibs.obj.user;

// imports:
//KR TODO: unsauber
import ibs.app.AppConstants;
import ibs.app.CssConstants;
import ibs.bo.BOArguments;
import ibs.bo.AccessPermissions;
import ibs.bo.BOHelpers;
import ibs.bo.BOTokens;
import ibs.bo.BusinessObject;
import ibs.bo.Buttons;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.bo.ObjectNotAffectedException;
import ibs.bo.ObjectNotFoundException;
import ibs.bo.Operations;
import ibs.io.Environment;
import ibs.io.HtmlConstants;
import ibs.io.IOConstants;
import ibs.io.LayoutConstants;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.user.UserArguments;
import ibs.obj.user.UserConstants;
import ibs.obj.user.UserTokens;
import ibs.service.user.User;
import ibs.tech.html.BlankElement;
import ibs.tech.html.CenterElement;
import ibs.tech.html.LineElement;
import ibs.tech.html.NewLineElement;
import ibs.tech.html.Page;
import ibs.tech.html.RowElement;
import ibs.tech.html.SpanElement;
import ibs.tech.html.StyleSheetElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TableElement;
import ibs.tech.html.TextElement;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;
import ibs.tech.sql.StoredProcedureConstants;
import ibs.util.NoAccessException;
import ibs.util.StringHelpers;


/******************************************************************************
 * This class represents one object of type Rights with version 01. <BR/>
 *
 * @version     $Id: Rights_01.java,v 1.29 2013/01/16 16:14:10 btatzmann Exp $
 *
 * @author      Keim Christine (CK), 980615
 ******************************************************************************
 */
public class Rights_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Rights_01.java,v 1.29 2013/01/16 16:14:10 btatzmann Exp $";


    /**
     * OID of the Object for that the right counts . <BR/>
     */
    public OID rOid = null;

    /**
     * Id of the person or the group for which the right counts. <BR/>
     */
    public int rPersonId = 0;

    /**
     * Name of the Person/Group/Role for that the right counts . <BR/>
     */
    public String pName = null;

    /**
     * Oid of the Person/Group/Role for that the right counts . <BR/>
     */
    public OID pOid = null;

    /**
     * rights for the object. <BR/>
     */
    public int rights = -1;

    /**
     * rightsupdate recursive? <BR/>
     */
    public boolean recursive = false;


    /**************************************************************************
     * This constructor creates a new instance of the class Rights_01. <BR/>
     */
    public Rights_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // Rights_01


    /**************************************************************************
     * Creates a Rights_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public Rights_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // Rights_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        this.deleteRecursive = false;

        // set the class-procedureNames
        this.procCreate = "p_Rights_01$create";
        this.procChange = "p_Rights_01$change";
        this.procRetrieve = "p_Rights_01$retrieve";
        this.procDelete = "p_Rights_01$delete";
        this.procDeleteRec = "p_Rights_01$deleteRightsRec";

        // set number of parameters for procedure calls:
        this.specificDeleteParameters = 4;
    } // initClassSpecifics


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
        // define buttons to be displayed:
        int [] buttons =
        {
            Buttons.BTN_RIGHTSEDIT,
            Buttons.BTN_RIGHTSDELETE,
            Buttons.BTN_SEARCH,
//            Buttons.BTN_HELP,
        }; // buttons
         // return button array
        return buttons;
    } // showInfoButtons


    /**************************************************************************
     * Insert style sheet information in info view. <BR/>
     *
     * @param   page    The HTML page to which the stylesheet information shall
     *                  be added.
     */
    protected void insertInfoStyles (Page page)
    {
        // Stylesheetfile wird geladen
        StyleSheetElement style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () +
            "/" +
            this.sess.activeLayout.elems[LayoutConstants.SHEETINFO].styleSheet;
        page.head.addElement (style);
        // styles for the legend
        style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () +
            "/" +
            this.sess.activeLayout.elems[LayoutConstants.SHEETLIST].styleSheet;
        page.head.addElement (style);
    } // insertInfoStyles


    /**************************************************************************
     * Represent the properties of a Rights_01 object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showProperties (TableElement table)
    {
        if (this.getUserInfo ().userProfile.showExtendedRights)
        {
            for (int i = 0; i < Operations.OP_IDS.length; i++)
            {
                this.showProperty (table,
                    UserArguments.ARG_OPPREFIX + MultilingualTextProvider.getText (
                        Operations.TOK_BUNDLE, Operations.OP_SHORTNAMES[i], env),
                    MultilingualTextProvider.getText (
                        Operations.TOK_BUNDLE, Operations.OP_NAMES[i], env), 
                    Datatypes.DT_BOOL,
                    "" + ((this.rights & Operations.OP_IDS[i]) == Operations.OP_IDS[i]));
            } // for i
        } // if show extended rights
        else
        {
            String rightsText = new String ();
            // loop trough all rightaliases
            for (int i = 0; i < UserTokens.ML_RIGHTALIASES.length; i++)
            {
                rightsText = "";
                // check wich radiobutton shall be checked
                if ((this.rights & UserConstants.RIGHTALIASES [i]) ==
                    UserConstants.RIGHTALIASES [i])
                {
                    rightsText += MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, UserTokens.ML_FULLRIGHTLONG, env);
                } // if
                else if ((this.rights & UserConstants.RIGHTALIASES[i]) == 0)
                {
                    rightsText += MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, UserTokens.ML_NORIGHTLONG, env);
                } // else if
                else // partial rights
                {
                    rightsText += MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, UserTokens.ML_HALFRIGHTLONG, env) + " => ";
                    // create text with shortnames of rights
                    for (int j = 0; j < Operations.OP_IDS.length; j++)
                    {
                        if (((Operations.OP_IDS[j] & UserConstants.RIGHTALIASES[i]) == Operations.OP_IDS[j]) &&
                            ((Operations.OP_IDS[j] & this.rights) == Operations.OP_IDS[j]))
                        {
                            rightsText += MultilingualTextProvider.getText (
                                Operations.TOK_BUNDLE, Operations.OP_SHORTNAMES[j], env) + " ";
                        } // if
                    } // for
                } // else
                this.showProperty (table, UserArguments.ARG_RIGHTALIASES[i],
                    MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, UserTokens.ML_RIGHTALIASES[i], env), Datatypes.DT_TEXT,
                    rightsText);
            } // for
        } // else
    } // showProperties


    /**************************************************************************
     * Insert style sheet information in the change form. <BR/>
     *
     * @param   page    The html page to be displayed.
     */
    protected void insertChangeFormStyles (Page page)
    {
        // Stylesheetfile wird geladen
        StyleSheetElement style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () +
            "/" +
            this.sess.activeLayout.elems[LayoutConstants.SHEETINFO].styleSheet;
        page.head.addElement (style);
        // styles for the legend
        style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () +
            "/" +
            this.sess.activeLayout.elems[LayoutConstants.SHEETLIST].styleSheet;
        page.head.addElement (style);
    } // insertChangeFormStyles


    /**************************************************************************
     * Represent the properties of a Rigths_01 object to the user
     * within a form. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showFormProperties (TableElement table)
    {
        if (this.getUserInfo ().userProfile.showExtendedRights)
        {
            for (int i = 0; i < Operations.OP_IDS.length; i++)
            {
                this.showFormProperty (table,
                    UserArguments.ARG_OPPREFIX + MultilingualTextProvider.getText (
                        Operations.TOK_BUNDLE, Operations.OP_SHORTNAMES[i], env),
                    MultilingualTextProvider.getText (
                        Operations.TOK_BUNDLE, Operations.OP_NAMES[i], env),
                    Datatypes.DT_BOOL,
                    "" + ((this.rights & Operations.OP_IDS[i]) == Operations.OP_IDS[i]));
            } // for i
        } // if show extended rights
        else
        {
            int checked = -1;
            String rightsText = new String ();

            // loop trough all rightaliases
            for (int i = 0; i < UserTokens.ML_RIGHTALIASES.length; i++)
            {
                rightsText = "";
                // check wich radiobutton shall be checked
                if ((this.rights & UserConstants.RIGHTALIASES[i]) == UserConstants.RIGHTALIASES[i])
                {
                    checked = StringHelpers.findString (UserTokens.ML_CHANGERIGHTS, UserTokens.ML_FULLRIGHTLONG);
                } // if
                else if ((this.rights & UserConstants.RIGHTALIASES[i]) == 0)
                {
                    checked = StringHelpers.findString (UserTokens.ML_CHANGERIGHTS, UserTokens.ML_NORIGHTLONG);
                } // else if
                else
                {
                    checked = StringHelpers.findString (UserTokens.ML_CHANGERIGHTS, UserTokens.ML_NOCHANGE);
                } // else

                // create text with shortnames of rights
                for (int j = 0; j < Operations.OP_IDS.length; j++)
                {
                    if (((Operations.OP_IDS[j] & UserConstants.RIGHTALIASES[i]) == Operations.OP_IDS[j]) &&
                        ((Operations.OP_IDS[j] & this.rights) == Operations.OP_IDS[j]))
                    {
                        rightsText += MultilingualTextProvider.getText (
                            Operations.TOK_BUNDLE, Operations.OP_SHORTNAMES[j], env) + " ";
                    } // if
                } // for j
                // show property rightalias 'READ', 'WRITE' or 'ADMIN'
                this.showFormProperty (table,
                                    UserArguments.ARG_RIGHTALIASES [i],
                                    MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, UserTokens.ML_RIGHTALIASES [i], env),
                                    UserTokens.ML_CHANGERIGHTS,
                                    Datatypes.DT_RADIO,
                                    AppConstants.DIR_HORIZONTAL,
                                    checked,
                                    rightsText);
            } // for i
        } // else show only right-aliases
        this.showProperty (table, null, null, Datatypes.DT_SEPARATOR,
            (String) null);
        this.showFormProperty (table, BOArguments.ARG_RECURSIVE,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, UserTokens.ML_RECURSIVE, env), Datatypes.DT_BOOL, "" + false);
        this.showFormProperty (table, BOArguments.ARG_ROID, "",
            Datatypes.DT_HIDDEN, this.rOid.toString ());
        this.showFormProperty (table, BOArguments.ARG_RPERSONID, "",
            Datatypes.DT_HIDDEN, "" + this.rPersonId);
        this.showFormProperty (table, UserArguments.ARG_RIGHTS, "",
            Datatypes.DT_HIDDEN, "" + this.rights);
    } // showFormProperties


    /**************************************************************************
     * Represent something on the bottom of the info - view of an object to
     * the user. <BR/>
     *
     * @param   page        The page to add the bottom.
     */
    protected void showInfoBottom (Page page)
    {
        if (!this.getUserInfo ().userProfile.showExtendedRights)
        {
            Rights_01.showLegend (page,
                this.getUserInfo ().userProfile.showExtendedRights, this.env);
        } // if
    } // showInfoBottom


    /**************************************************************************
     * Represent something after the formfooter (buttons) on the
     * changeform to the user. <BR/>
     *
     * @param   page        The page to add the bottom.
     */
    protected void showChangeFormBottom (Page page)
    {
        if (!this.getUserInfo ().userProfile.showExtendedRights)
        {
            Rights_01.showLegend (page,
                this.getUserInfo ().userProfile.showExtendedRights, this.env);
        } // if
    } // showChangeFormBottom


    /**************************************************************************
     * Get a Rights_01 object out of the database. <BR/>
     * First this method tries to load the object from the database. During
     * this operation a rights check is done, too. If this is all right the
     * object is returned otherwise an exception is raised.
     *
     * @param   operation   Operation to be performed with the object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    protected void performRetrieveData (int operation) throws NoAccessException
    {
        int operationLocal = operation; // variable for local assignments

/* ****************** */
/* momentane Lösung   */
/* ****************** */
        operationLocal = Operations.OP_VIEWRIGHTS;

        // create the stored procedure call:
        StoredProcedure sp = new StoredProcedure(
                this.procRetrieve,
                StoredProcedureConstants.RETURN_VALUE);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // input parameters
        // oid
        BOHelpers.addInParameter(sp, this.rOid);
        // Group or User Id
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.rPersonId);
        // user id
        if (this.user != null)
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.user.id);
        } // if
        else
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, 0);
        } // else
        // operation
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, operationLocal);
        // output parameters
        // containerId
        Parameter containerIdOutParam = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // objectName
        Parameter objectNameOutParam = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // pOid
        Parameter pOidOutParam = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // pName
        Parameter pNameOutParam = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // rights
        Parameter rightsOutParam = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);

        // perform the function call:
        BOHelpers.performCallFunctionData(sp, this.env);

        // set object properties - get them out of parameters
        this.containerId = SQLHelpers.getSpOidParam (containerIdOutParam);
        String objectName = objectNameOutParam.getValueString ();
        this.pOid = SQLHelpers.getSpOidParam (pOidOutParam);
        this.pName = pNameOutParam.getValueString ();
        this.rights = rightsOutParam.getValueInteger ();
// mh?
//        this.containerId = this.rOid;

        this.name = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, UserTokens.ML_PRENAME, env) + this.pName +
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, UserTokens.ML_POSTNAME, env) + objectName;

        // set the icon
        this.setIcon ();
    } // performRetrieveData


    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     * The {@link #env env} property is used for getting the parameters.
     * This property must be set before calling this method. <BR/>
     */
    public void getParameters ()
    {
        int num;
        OID oid;

        super.getParameters ();

        if ((oid = this.env.getOidParam (UserArguments.ARG_ROID)) != null)
        {
            this.rOid = oid;
        } // if

        if ((num = this.env.getIntParam (UserArguments.ARG_RIGHTS)) !=
            IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            this.rights = num;
        } // if

        if (this.getUserInfo ().userProfile.showExtendedRights)
        {
            this.rights = 0;
            for (int i = 0; i < Operations.OP_IDS.length; i++)
            {
                // TODO RB: Will this work with MLI support??
                if ((this.env.getBoolParam (UserArguments.ARG_OPPREFIX +
                    MultilingualTextProvider.getText (Operations.TOK_BUNDLE, 
                        Operations.OP_SHORTNAMES[i], env))) == IOConstants.BOOLPARAM_TRUE)
                {
                    this.rights = this.rights | Operations.OP_IDS[i];
                } // if
            } // for
        } // if showExtendedRights
        else
        {
            // loop trough all rightaliases
            for (int i = 0; i < UserTokens.ML_RIGHTALIASES.length; i++)
            {
                String changeRights =
                    this.env.getParam (UserArguments.ARG_RIGHTALIASES [i]);

                // check if the specified field was available
                if (changeRights == null)
                {
                    continue;
                } // if
                // check wich radiobutton is enabled
                if (changeRights.equals (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, UserTokens.ML_FULLRIGHTLONG, env)))
                {   // add rights of current rightsalias
                    this.debug ("set rights");
                    this.rights = this.rights | UserConstants.RIGHTALIASES[i];
                } // if
                else if (changeRights.equals (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, UserTokens.ML_NORIGHTLONG, env)))
                {   // delete rights of current rightalias
                    this.debug ("delete rights");
                    this.rights = this.rights & (~UserConstants.RIGHTALIASES[i]);
                } // else if
                else if (changeRights.equals (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, UserTokens.ML_NOCHANGE, env)))
                {   // do nothing
                    this.debug (" do not change rightalias " + UserConstants.RIGHTALIASES[i]);
                } // else if
            } // for i
        } // show rightaliases

        if ((num = this.env.getBoolParam (BOArguments.ARG_RECURSIVE)) >=
            IOConstants.BOOLPARAM_FALSE)
        {
            this.recursive = num == IOConstants.BOOLPARAM_TRUE;
        } // if

        if ((num = this.env.getIntParam (BOArguments.ARG_RPERSONID)) !=
            IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            this.rPersonId = num;
        } // if

        // set the icon
        this.setIcon ();
    } // getParameters


    /**************************************************************************
     * Change the data of a Beitrag_01 object in the database. <BR/>
     * This method tries to store the object into the database.
     * During this operation a rights check is done, too.
     * If this is all right the object is stored and this method terminates
     * otherwise an exception is raised. <BR/>
     *
     * @param   operation   Operation to be performed with the object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    protected void performChangeData (int operation) throws NoAccessException
    {
        int operationLocal = operation; // variable for local assignments

/* ****************** */
/* momentane Lösung   */
/* ****************** */
        operationLocal = Operations.OP_SETRIGHTS;

        // create the stored procedure call:
        StoredProcedure sp = new StoredProcedure(
                this.procChange,
                StoredProcedureConstants.RETURN_VALUE);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // input parameters
        // oid
        BOHelpers.addInParameter(sp, this.rOid);
        // Group or User Id
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.rPersonId);
        // user id
        if (this.user != null)
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.user.id);
        } // if
        else
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, 0);
        } // else
        // operation
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, operationLocal);
        // rights
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.rights);
        // recursive?
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN, this.recursive);

        // perform the function call:
        BOHelpers.performCallFunctionData(sp, this.env);
    } // performChangeData


    /**************************************************************************
     * Create the rights. The properties are gotten from the environment. <BR/>
     *
     * @param   representationForm  Kind of representation.
     *
     * @return  Oid of the newly created object. <BR/>
     *          <CODE>null</CODE> if the object could not be created.
     */
    public OID create (int representationForm)
    {
        OID newOid = null;              // oid of newly created object

        // store the object's data within the database:
        try
        {
            // try to store the object to the database:
            newOid = this.performCreateData (Operations.OP_SETRIGHTS);
        } // try
        catch (NoAccessException e) // no access to object allowed
        {
            // send message to the user:
            this.showNoAccessMessage (Operations.OP_SETRIGHTS);
            newOid = null;
        } // catch

        // show the container-object to the user:
        return newOid;                  // return the oid of the newly created
                                        // object
    } // create


    /**************************************************************************
     * Store the new rights in the database. <BR/>
     * This method tries to store the new object into the database.
     * During this operation a rights check is done, too.
     * If this is all right the object is stored and this method terminates,
     * otherwise an exception is raised. <BR/>
     *
     * @param   operation   Operation to be performed with the object.
     *
     * @return  Oid of the newly created object. <BR/>
     *          Null if the object could not be created.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    protected OID performCreateData (int operation) throws NoAccessException
    {
        OID newOid = null;              // oid of the newly created object

        // create the stored procedure call:
        StoredProcedure sp = new StoredProcedure(
                this.procCreate,
                StoredProcedureConstants.RETURN_VALUE);        

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // input parameters
        // user id
        if (this.user != null)
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.user.id);
        } // if
        else
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, 0);
        } // else
        // operation
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, operation);
        // rOid
        BOHelpers.addInParameter(sp, this.rOid);
        // rPersonId
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.rPersonId);
        // rights
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.rights);
        // recursive
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN, this.recursive);

        // output parameters:

        // perform the function call:
        BOHelpers.performCallFunctionData (sp, this.env);

        // just set the return value as the actual oid:
        newOid = this.oid;

        return newOid;                  // return the oid of the new object
    } // performCreateData


    /**************************************************************************
     * Get the rights of a business object out of the database.
     * In this case are the fetched rights directly from ibs_object. <BR/>
     *
     * @param   oid     oid of object we want to get the rights for
     * @param   user    User whose rights we want to check
     *
     * @return  The access permissions for the user on the business object.
     */
    protected AccessPermissions performGetRightsContainerData (OID oid, User user)
    {
        // get the rights for the referenced object:
        AccessPermissions permissions =
            super.performGetRightsContainerData (this.rOid, user);

        // ensure that the rights for the object are the same as the rights
        // for the container because the rights object does not have own rights:
        permissions.p_containerRights = permissions.p_objectRights;

        // return the computed permissions:
        return permissions;
    } // performGetRightsContainerData


    /***************************************************************************
     * Get the oid of the object which is in the hierarchy above the actual
     * object out of the database. <BR/>
     *
     * @return  The oid of the object which is above the actual object.
     *          <CODE>null</CODE> if there is no upper object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     * @exception   ObjectNotFoundException
     *              The required object was not found during search, i.e. there
     *              does not exist any object which is above the actual one
     *              within the hierarchy.
     */
    protected OID performRetrieveUpperData ()
        throws NoAccessException, ObjectNotFoundException
    {
        // check if the container is already known:
        if (this.containerId != null)
        {
            return this.containerId;
        } // if

        // call common method:
        return this.performRetrieveUpperData (this.rOid);
    } // performRetrieveUpperData


    /**************************************************************************
     * Set the data for the additional (type specific) parameters for
     * performDeleteData. <BR/>
     * This method must be overwritten by all subclasses that have to pass
     * type specific data to the delete data stored procedure.
     *
     * @param params     The stored procedure to add the delete parameters to.
     *
     * @param operation  Operation to be performed with the object.
     */
    @Override
    protected void setSpecificDeleteParameters (StoredProcedure sp, int operation)
    {
        // set the specific parameters:
        // input parameters
        // rOid
        BOHelpers.addInParameter (sp, this.rOid);
        // rPersonId
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
                        this.rPersonId);
        // user id
        if (this.user != null)
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER,
                            this.user.id);
        } // if
        else
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER,
                            0);
        } // else
        // operation
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
                        operation);
    } // setSpecificDeleteParameters


    /**************************************************************************
     * Show legend to different rights. <BR/>
     *
     * @param   page            The page to add the legend.
     * @param   extendedRights  Shall the normal or the extended rights be
     *                          displayed?
     * @param   env             The current environment                         
     */
    public static void showLegend (Page page, boolean extendedRights, Environment env)
    {
        String description;             // the actual right's description
        NewLineElement nl = new NewLineElement (); // a new line
        TextElement t;                  // the actual text element
        TableDataElement td;            // the actual table data element
        SpanElement s = new SpanElement (); // a span element
        TableElement legend;            // the legend as a whole
        String[] classIds;              // the class ids
        RowElement r;                   // the actual table row
        TableDataElement blank = new TableDataElement (new BlankElement ());
                                        // a blank table data element
        int columns = 0;                // number of columns for table

        // add the heading text to the span element and set the layout class:
        t = new TextElement (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, UserTokens.ML_LEGEND, env));
        s.addElement (t);
        s.classId = CssConstants.CLASS_LEGEND;

        // show a line on the page:
        TableElement lineElement = new TableElement (1);
        lineElement.border = 0;
        lineElement.width = HtmlConstants.TAV_FULLWIDTH;
        RowElement lineElementRow = new RowElement (1);
        lineElement.addElement (lineElementRow);
        TableDataElement lineElementData = new TableDataElement(new LineElement (0, "80%"));
        lineElementRow.addElement (lineElementData);
        page.body.addElement (lineElement);
        // add the span element to the page:
        page.body.addElement (new CenterElement (s));
        page.body.addElement (nl);

        // check which legend should be shown:
        if (extendedRights)             // show legend for extended rights?
        {
            columns = 5;                // set the number of columns
        } // if show legend for extended rights
        else                            // show legend for simple rights
        {
            columns = 2;                // set the number of columns
        } // else show legend for simple rights

        // create the legend object:
        legend = new TableElement (columns);
        legend.classId = CssConstants.CLASS_LEGEND;
        classIds = new String[columns];
        classIds[0] = CssConstants.CLASS_COLRIGHT;
        classIds[1] = CssConstants.CLASS_COLRIGHTDESCRIPTION;
        legend.classIds = classIds;
        legend.border = 0;
        legend.width = HtmlConstants.TAV_FULLWIDTH;

        r = new RowElement (columns);

        // check which legend should be shown:
        if (extendedRights)             // show legend for extended rights?
        {
            classIds[2] = null;
            classIds[3] = CssConstants.CLASS_COLRIGHT;
            classIds[4] = CssConstants.CLASS_COLRIGHTDESCRIPTION;

            for (int j = 0; j < Operations.OP_SHORTNAMES.length; j++)
            {
                // after each 2 rights begin a new line:
                if (j % 2 == 0)
                {
                    r = new RowElement (5);
                    legend.addElement (r);
                } // if
                else
                {
                    r.addElement (blank);
                } // else
                t = new TextElement (MultilingualTextProvider.getText (
                    Operations.TOK_BUNDLE, Operations.OP_SHORTNAMES[j], env) + "...");

                td = new TableDataElement (t);
                td.classId = CssConstants.CLASS_COLRIGHT;
                r.addElement (td);

                t = new TextElement (MultilingualTextProvider.getText (
                    Operations.TOK_BUNDLE, Operations.OP_NAMES[j], env));
                r.addElement (new TableDataElement (t));
            } // for j
        } // if show legend for extended rights
        else                            // show legend for simple rights
        {
            for (int i = 0; i < UserTokens.ML_RIGHTALIASES.length; i++)
            {
                r = new RowElement (2);
                t = new TextElement (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, UserTokens.ML_RIGHTALIASES[i], env) + "...");

                td = new TableDataElement (t);
                td.classId = CssConstants.CLASS_COLRIGHT;
                r.addElement (td);

                description = new String ();
                for (int j = 0; j < Operations.OP_SHORTNAMES.length; j++)
                {
                    // check if operation/right is part of current rightalias
                    if ((Operations.OP_IDS[j] & UserConstants.RIGHTALIASES[i]) != Operations.OP_IDS[j])
                    {
                        continue;
                    } // if

                    // if at least one Operation is in description add a ,
                    if (description.length () > 8)
                    {
                        description += ", ";
                    } // if
                    description += MultilingualTextProvider.getText (
                        Operations.TOK_BUNDLE, Operations.OP_NAMES[j], env) + " (" +
                        MultilingualTextProvider.getText (Operations.TOK_BUNDLE, 
                            Operations.OP_SHORTNAMES[j], env) + ")";
                }  // for

                t = new TextElement (description);
                r.addElement (new TableDataElement (t));
                legend.addElement (r);
                legend.addElement (new RowElement (1));
            } // for i
        } // else show legend for simple rights

        // add the legend to the page:
        page.body.addElement (legend);
    } // showLegend


    /**************************************************************************
     * Create the query to get the container's content out of the database.
     * <BR/>
     * The query or view must at least have the attributes userId and rights.
     * Queries on these attributes have to be addable to this query. <BR/>
     * The query must have at least one constraint within WHERE to ensure that
     * other constraints can be added with AND. <BR/>
     * <B>Standard format:</B><BR/>
     * <PRE>
     *      "SELECT DISTINCT oid, &lt;other attributes> " +
     *      " FROM " + this.viewContent +
     *      " WHERE containerId = " + oid;
     * </PRE>
     * This method can be overwritten in subclasses. <BR/>
     * As you can see the query should contain at least the oid.
     *
     * @return  The constructed query.
     */
    protected String createQueryGetPathData ()
    {
        StringBuffer queryStr;
        StringBuffer fieldRefFrom = new StringBuffer ();
        StringBuffer fieldRefWhere = new StringBuffer ();

        // create the join:
        SQLHelpers.getLeftOuterJoin (
            new StringBuffer ("ibs_MenuTab_01"),
            new StringBuffer ("m"),
            new StringBuffer ("o.oid = m.objectOid"),
            new StringBuffer ("AND"),
            fieldRefFrom, fieldRefWhere);

        // use when everybody has flags within his / her views:
        queryStr = new StringBuffer ()
            .append ("SELECT DISTINCT o.oid, o.name,")
            .append (SQLHelpers.getSelectCondition (
                     "m.oid", SQLConstants.DB_NULL, "o.containerId", "m.oid"))
            .append (" AS containerId,")
            .append (" o.containerKind, o.olevel, m.isPrivate")
            .append (" FROM ibs_Object o, ibs_Object b").append (fieldRefFrom)
            .append (" WHERE b.oid = ").append (this.containerId.toStringQu ())
            .append (" AND ").append (SQLHelpers.getQueryConditionAttribute (
                "b.posNoPath", SQLConstants.MATCH_STARTSWITH, "o.posNoPath", true))
            .append (fieldRefWhere)
            .append (" ORDER BY o.olevel DESC");

        // return the computed query:
        return queryStr.toString ();
    } // createQueryGetPathData


    /**************************************************************************
     * Check if the user has the necessary rights on the actual object and all
     * subsequent objects to perform the required operation(s) within the data
     * store. <BR/>
     * If the user is able to administrate the rights he always may delete it.
     * So this method always terminates successfully.
     *
     * @param   userId      Id of user whose rights we want to check.
     * @param   operation   Operation(s) the user wants to perform on the
     *                      object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object and the sub
     *              objects to perform the required operation.
     * @exception   ObjectNotAffectedException
     *              The user does not have access to at least the object or one
     *              of the sub objects.
     */
    protected void performCheckRightsRecursive (int userId, int operation)
        throws NoAccessException, ObjectNotAffectedException
    {
        // nothing to do
    } // performCheckRightsRecursive


    /**************************************************************************
     * Gets the container rights. <BR/>
     *
     * @return  The access permissions.
     */
    protected AccessPermissions getContainerRights ()
    {
        // get the rights:
        return this.performGetRightsContainerData (this.rOid, this.user);
//debug ("oid = " + this.oid);
//debug ("user = " + this.user);
//debug ("objectRights = " + this.objectRights);
//debug ("containerRights = "+ this.containerRights);
    } // getContainerRights

} // class Rights_01
