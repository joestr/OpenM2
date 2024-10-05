/*
 * Class: Person_01.java
 */

// package:
package m2.mad;

// imports:
import ibs.app.AppFunctions;
import ibs.bo.BOArguments;
import ibs.bo.BOConstants;
import ibs.bo.BOHelpers;
import ibs.bo.BusinessObject;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.di.DataElement;
import ibs.io.HtmlConstants;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.TableElement;
import ibs.tech.http.HttpArguments;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;
import ibs.util.FormFieldRestriction;

import m2.mad.MadArguments;
import m2.mad.MadConstants;
import m2.mad.MadTokens;
import m2.store.StoreTypeConstants;


/******************************************************************************
 * This class represents one object of type Person with version 01. <BR/>
 *
 * @version     $Id: Person_01.java,v 1.18 2013/01/16 16:14:13 btatzmann Exp $
 *
 * @author      Keim Christine (CK), 980603
 ******************************************************************************
 */
public class Person_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Person_01.java,v 1.18 2013/01/16 16:14:13 btatzmann Exp $";


    /**
     * Email at work. <BR/>
     */
    public String offemail = "";

    /**
     * Homepage at work. <BR/>
     */
    public String offhomepage = "";

    /**
     * Title of the Person. <BR/>
     */
    public String title = "";

    /**
     * Prefix of the person (ms. mister). <BR/>
     */
    public String prefix = "";

    /**
     * Position at work. <BR/>
     */
    public String position = "";

    /**
     * Name of the company the person works for. <BR/>
     */
    public String company = "";

    /**
     * oid of the Adress. <BR/>
     */
    public OID tabAddress = null;

    /**
     * oid of assigned user. <BR/>
     */
    public OID userOid = null;

    /**
     * The name of the assigned user. <BR/>
     */
    public String userName = "";

    /**
     * Field name: office email. <BR/>
     */
    private static final String FIELD_OFFEMAIL = "offemail";
    /**
     * Field name: office homepage. <BR/>
     */
    private static final String FIELD_OFFHOMEPAGE = "offhomepage";
    /**
     * Field name: title. <BR/>
     */
    private static final String FIELD_TITLE = "title";
    /**
     * Field name: name prefix. <BR/>
     */
    private static final String FIELD_PREFIX = "prefix";
    /**
     * Field name: position. <BR/>
     */
    private static final String FIELD_POSITION = "position";
    /**
     * Field name: company name. <BR/>
     */
    private static final String FIELD_COMPANY = "company";


    /**************************************************************************
     * This constructor creates a new instance of the class Person_01.
     * <BR/>
     */
    public Person_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // Person_01


    /**************************************************************************
     * This constructor creates a new instance of the class Person_01. <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public Person_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:
    } // Person_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set the class-procedureNames
        this.procCreate   = "p_Person_01$create";
        this.procChange   = "p_Person_01$change";
        this.procRetrieve = "p_Person_01$retrieve";
        this.procDelete   = "p_Person_01$delete";

        //show changeForm as frame set for user selection
        this.showChangeFormAsFrameset = true;

        //set extended search flag
        this.searchExtended = true;

        // set db table name
        this.tableName = "mad_Person_01";

        // set number of parameters for procedure calls:
        this.specificRetrieveParameters = 9;
        this.specificChangeParameters = 7;
    } // initClassSpecifics


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
        // set the specific parameters:
        // title
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.title);
        // prefix
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.prefix);
        // position
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.position);
        // company
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.company);
        // offemail
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.offemail);
        // offhomepage
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.offhomepage);
        //assigned UserOID
        BOHelpers.addInParameter (sp, this.userOid);
    } // setSpecificChangeParameters


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
        // title
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // prefix
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // position
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // company
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // offemail
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // offhomepage
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // addressId
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // assigned usersOid
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // get the assigned usersName for the link
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
        this.title = params[++i].getValueString ();
        this.prefix = params[++i].getValueString ();
        this.position = params[++i].getValueString ();
        this.company = params[++i].getValueString ();
        this.offemail = params[++i].getValueString ();
        this.offhomepage = params[++i].getValueString ();
        this.tabAddress = SQLHelpers.getSpOidParam (params[++i]);
        this.userOid = SQLHelpers.getSpOidParam (params[++i]);
        this.userName = params[++i].getValueString ();
    } // getSpecificRetrieveParameters


    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     * The <A HREF="#env">env</A> property is used for getting the parameters.
     * This property must be set before calling this method. <BR/>
     */
    public void getParameters ()
    {
        String text = "";
        OID aOid = null;

        super.getParameters ();

        // get offemail
        if ((text = this.env.getParam (MadArguments.ARG_OFFEMAIL)) != null)
        {
            this.offemail = text;
        } // if

        // get offhomepage
        if ((text = this.env.getParam (MadArguments.ARG_OFFHOMEPAGE)) != null)
        {
            this.offhomepage = text;
        } // if

        // get title:
        if ((text = this.env.getParam (MadArguments.ARG_TITLE)) != null)
        {
            this.title = text;
        } // if

        // get prefix:
        if ((text = this.env.getParam (MadArguments.ARG_PREFIX)) != null)
        {
            this.prefix = text;
        } // if

        // get position:
        if ((text = this.env.getParam (MadArguments.ARG_POSITION)) != null)
        {
            this.position = text;
        } // if

        // get company:
        if ((text = this.env.getParam (MadArguments.ARG_COMPANY)) != null)
        {
            this.company = text;
        } // if


        if ((aOid = this.env.getOidParam (MadArguments.ARG_LINKED_TO +
            MadArguments.ARG_OID_EXTENSION)) != null)
        {
            this.userOid = aOid;
        } // if
/*
        // get the Object Identifier of the assigned user
        aOid = env.getOidParam (MadArguments.ARG_LINKED_TO
              + MadArguments.ARG_OID_EXTENSION);
        // Parameter should not be null
        if ((aOid != null))
        {   //set the value to the class property userOid
            this.userOid = aOid;
        } // if
*/
    } // getParameters


    /**************************************************************************
     * Represent the properties of a Person_01 object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     *
     * @see ibs.bo.BusinessObject#showProperty
     */
    protected void showProperties (TableElement table)
    {
        // loop through all properties of this object and display them:
        super.showProperties (table);
        this.showProperty (table, MadArguments.ARG_TITLE, 
            MultilingualTextProvider.getText (MadTokens.TOK_BUNDLE,
                MadTokens.ML_TITLE, env),
            Datatypes.DT_TEXT, this.title);
        this.showProperty (table, MadArguments.ARG_PREFIX, 
            MultilingualTextProvider.getText (MadTokens.TOK_BUNDLE,
                MadTokens.ML_PREFIX, env),
            Datatypes.DT_TEXT, this.prefix);
        this.showProperty (table, MadArguments.ARG_POSITION, 
            MultilingualTextProvider.getText (MadTokens.TOK_BUNDLE,
                MadTokens.ML_POSITION, env),
            Datatypes.DT_TEXT, this.position);
        this.showProperty (table, MadArguments.ARG_COMPANY, 
            MultilingualTextProvider.getText (MadTokens.TOK_BUNDLE,
                MadTokens.ML_COMPANY, env),
            Datatypes.DT_TEXT, this.company);
        this.showProperty (table, MadArguments.ARG_OFFEMAIL, 
            MultilingualTextProvider.getText (MadTokens.TOK_BUNDLE,
                MadTokens.ML_OFFEMAIL, env),
            Datatypes.DT_EMAIL, this.offemail);
        this.showProperty (table, MadArguments.ARG_OFFHOMEPAGE, 
            MultilingualTextProvider.getText (MadTokens.TOK_BUNDLE,
                MadTokens.ML_OFFHOMEPAGE, env),
            Datatypes.DT_URL, this.offhomepage);
        // look if there is no user linked with this Person
        if (!this.userOid.isTemp ())    // this row and the web link should be shown?
        {
            this.showProperty (table, MadArguments.ARG_LINKED_TO, 
                MultilingualTextProvider.getText (MadTokens.TOK_BUNDLE,
                    MadTokens.ML_ASSIGNED_TO_USER, env),
                Datatypes.DT_LINK, this.userName, this.userOid);
        } // if
    } // showProperties


    /***************************************************************************
     * Represent the properties of a Person_01 object to the user within a form.
     * <BR/>
     *
     * @param table Table where the properties shall be added.
     *
     * @see ibs.bo.BusinessObject#showFormProperty
     */
    protected void showFormProperties (TableElement table)
    {
        // loop through all properties of this object and display them:
        super.showFormProperties (table);

        // title
        // restrict: empty entries allowed and not more than 30 characters
        this.formFieldRestriction =
            new FormFieldRestriction (true, 30, 0);
        this.showFormProperty (table, MadArguments.ARG_TITLE, 
            MultilingualTextProvider.getText (MadTokens.TOK_BUNDLE,
                MadTokens.ML_TITLE, env),
            Datatypes.DT_TEXT, this.title);

        String[] token = {"",  
            MultilingualTextProvider.getText (MadTokens.TOK_BUNDLE,
                MadTokens.ML_PREMS, env),  
            MultilingualTextProvider.getText (MadTokens.TOK_BUNDLE,
                MadTokens.ML_PREMR, env)};
        int preselected;
        if (this.prefix.equalsIgnoreCase ( 
            MultilingualTextProvider.getText (MadTokens.TOK_BUNDLE,
                MadTokens.ML_PREMS, env)))
        {
            preselected = 1;
        } // if
        else if (this.prefix.equalsIgnoreCase ( 
            MultilingualTextProvider.getText (MadTokens.TOK_BUNDLE,
                MadTokens.ML_PREMR, env)))
        {
            preselected = 2;
        } // else if
        else
        {
            preselected = 3;
        } // else
        this.showFormProperty (table, MadArguments.ARG_PREFIX,
            MultilingualTextProvider.getText (MadTokens.TOK_BUNDLE,
                MadTokens.ML_PREFIX, env),
            Datatypes.DT_SELECT, this.prefix, token, token, preselected);

        // position
        // restrict: empty entries allowed and not more than 30 characters
        this.formFieldRestriction =
            new FormFieldRestriction (true, 30, 0);
        this.showFormProperty (table, MadArguments.ARG_POSITION, 
            MultilingualTextProvider.getText (MadTokens.TOK_BUNDLE,
                MadTokens.ML_POSITION, env),
            Datatypes.DT_TEXT, this.position);

        // position
        // restrict: empty entries allowed and not more than 63 characters
        this.formFieldRestriction =
            new FormFieldRestriction (true, BOConstants.MAX_LENGTH_NAME, 0);
        this.showFormProperty (table, MadArguments.ARG_COMPANY, 
            MultilingualTextProvider.getText (MadTokens.TOK_BUNDLE,
                MadTokens.ML_COMPANY, env),
            Datatypes.DT_TEXT, this.company);
        // homepage
        // restrict: empty entries allowed and not more than 127 characters
        this.formFieldRestriction =
            new FormFieldRestriction (true, 127, 0);
        this.showFormProperty (table, MadArguments.ARG_OFFEMAIL, 
            MultilingualTextProvider.getText (MadTokens.TOK_BUNDLE,
                MadTokens.ML_OFFEMAIL, env),
            Datatypes.DT_EMAIL, this.offemail);
        // homepage
        // restrict: empty entries allowed and not more than 63 characters
        this.formFieldRestriction =
            new FormFieldRestriction (true, BOConstants.MAX_LENGTH_DESCRIPTION, 0);
        this.showFormProperty (table, MadArguments.ARG_OFFHOMEPAGE, 
            MultilingualTextProvider.getText (MadTokens.TOK_BUNDLE,
                MadTokens.ML_OFFHOMEPAGE, env),
            Datatypes.DT_TEXT, this.offhomepage);

        // searchtext - field for assigned user returns the OID
        this
            .showFormProperty (table, MadArguments.ARG_LINKED_TO, 
                MultilingualTextProvider.getText (MadTokens.TOK_BUNDLE,
                    MadTokens.ML_ASSIGNED_TO_USER, env),
                Datatypes.DT_SEARCHTEXTFUNCTION, this.userName, "" +
                    this.userOid, this.getBaseUrl () +
                    HttpArguments.createArg (BOArguments.ARG_FUNCTION,
                        AppFunctions.FCT_SHOWOBJECTCONTENT) +
                    HttpArguments.createArg (BOArguments.ARG_OID, new OID (this
                        .getTypeCache ().getTVersionId (
                            StoreTypeConstants.TC_SelectUserContainer), 0)
                        .toString ()) +
                    HttpArguments.createArg (MadArguments.ARG_CALLINGOID,
                        this.oid.toString ()) +
                    HttpArguments.createArg (MadArguments.ARG_SHOWLINK,
                        MadConstants.SHOWSEARCHEDOBJECTS),
                HtmlConstants.FRM_SHEET2);
    } // showFormProperties


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
        if (dataElement.exists (Person_01.FIELD_OFFEMAIL))
        {
            this.offemail = dataElement
                .getImportStringValue (Person_01.FIELD_OFFEMAIL);
        } // if
        if (dataElement.exists (Person_01.FIELD_OFFHOMEPAGE))
        {
            this.offhomepage = dataElement
                .getImportStringValue (Person_01.FIELD_OFFHOMEPAGE);
        } // if
        if (dataElement.exists (Person_01.FIELD_TITLE))
        {
            this.title = dataElement
                .getImportStringValue (Person_01.FIELD_TITLE);
        } // if
        if (dataElement.exists (Person_01.FIELD_PREFIX))
        {
            this.prefix = dataElement
                .getImportStringValue (Person_01.FIELD_PREFIX);
        } // if
        if (dataElement.exists (Person_01.FIELD_POSITION))
        {
            this.position = dataElement
                .getImportStringValue (Person_01.FIELD_POSITION);
        } // if
        if (dataElement.exists (Person_01.FIELD_COMPANY))
        {
            this.company = dataElement
                .getImportStringValue (Person_01.FIELD_COMPANY);
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
        dataElement.setExportEmailValue (Person_01.FIELD_OFFEMAIL,
            this.offemail);
        dataElement.setExportHyperlinkValue (Person_01.FIELD_OFFHOMEPAGE,
            this.offhomepage);
        dataElement.setExportValue (Person_01.FIELD_TITLE, this.title);
        dataElement.setExportValue (Person_01.FIELD_PREFIX, this.prefix);
        dataElement.setExportValue (Person_01.FIELD_POSITION, this.position);
        dataElement.setExportValue (Person_01.FIELD_COMPANY, this.company);
    } // writeExportData

} // class Person_01
