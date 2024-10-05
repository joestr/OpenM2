/*
 * Class: Company_01.java
 */

// package:
package m2.mad;

// imports:
import ibs.bo.BusinessObject;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.di.DataElement;
import ibs.io.IOConstants;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.TableElement;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;
import ibs.util.FormFieldRestriction;

import m2.mad.MadArguments;
import m2.mad.MadTokens;


/******************************************************************************
 * This class represents one object of type Company with version 01. <BR/>
 *
 * @version     $Id: Company_01.java,v 1.13 2013/01/16 16:14:13 btatzmann Exp $
 *
 * @author      Keim Christine (Ck), 980603
 ******************************************************************************
 */
public class Company_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Company_01.java,v 1.13 2013/01/16 16:14:13 btatzmann Exp $";


    /**
     * Owner of the Company. <BR/>
     */
    public String compowner = "";

    /**
     * Manager of the Company. <BR/>
     */
    public String manager = "";

    /**
     * Legal Form of the company. <BR/>
     */
    public String legalForm = "";

    /**
     * oid of the Adress. <BR/>
     */
    public OID tabAddress = null;

    /**
     * Contacts. <BR/>
     */
    public OID tabContacts = null;

    /**
     * MwSt. <BR/>
     */
    public int mwSt = 0;

    /**
     * Field name: company owner. <BR/>
     */
    private static final String FIELD_COMPANYOWNER = "compowner";
    /**
     * Field name: manager. <BR/>
     */
    private static final String FIELD_MANAGER = "manager";
    /**
     * Field name: legal form of company. <BR/>
     */
    private static final String FIELD_LEGALFORM = "legalForm";
    /**
     * Field name: value added tax value. <BR/>
     */
    private static final String FIELD_VAT = "mwSt";


    /**************************************************************************
     * This constructor creates a new instance of the class Company_01.
     * <BR/>
     */
    public Company_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // Company_01


    /**************************************************************************
     * This constructor creates a new instance of the class company. <BR/>
     * The compound object id is used as base for getting the
     * <A HREF="#server">server</A>, <A HREF="#type">type</A>, and
     * <A HREF="#id">id</A> of the business object. These values are stored in the
     * special public attributes of this type. <BR/>
     * The <A HREF="#user">user object</A> is also stored in a specific attribute
     * of this object to make sure that the user's context can be used for getting
     * his/her rights.
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public Company_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // Company_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set the class-procedureNames
        this.procCreate   = "p_Company_01$create";
        this.procChange   = "p_Company_01$change";
        this.procRetrieve = "p_Company_01$retrieve";
        this.procDelete   = "p_Company_01$delete";

        // set extended search flag
        this.searchExtended = true;

        // set db table name
        this.tableName = "mad_Company_01";

        // set number of parameters for procedure calls:
        this.specificRetrieveParameters = 6;
        this.specificChangeParameters = 4;
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
        // compowner
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.compowner);
        // manager
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.manager);
        // legalForm
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.legalForm);
        // mwSt
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.mwSt);
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
        // compowner
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // manager
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // legalForm
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // addressoid
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // contactoid
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // mwSt
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);

        return i;                       // return the current index
    } // setSpecificRetrieveParameters


    /**************************************************************************
     * Get the data for the additional (type specific) parameters for
     * performRetrieveData. <BR/>
     * This method must be overwritten by all subclasses that have to get
     * type specific data from the retrieve data stored procedure.
     *
     * @param   params      The array of parameters from the retrieve data
     *                      stored procedure.
     * @param   lastIndex   The index to the last element used in params thus
     *                      far.
     */
    protected void getSpecificRetrieveParameters (Parameter[] params,
                                                  int lastIndex)
    {
        int i = lastIndex;              // initialize params index

        // get the specific parameters:
        this.compowner = params[++i].getValueString ();
        this.manager = params[++i].getValueString ();
        this.legalForm = params[++i].getValueString ();
        this.tabAddress = SQLHelpers.getSpOidParam (params[++i]);
        this.tabContacts = SQLHelpers.getSpOidParam (params[++i]);
        this.mwSt = params[++i].getValueInteger ();
    } // getSpecificRetrieveParameters


    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     * The <A HREF="#env">env</A> property is used for getting the parameters.
     * This property must be set before calling this method. <BR/>
     */
    public void getParameters ()
    {
        String text = "";
        int number = -1;

        super.getParameters ();

        // get compowner:
        if ((text = this.env.getParam (MadArguments.ARG_COMPOWNER)) != null)
        {
            this.compowner = text;
        } // if

        // get manager:
        if ((text = this.env.getParam (MadArguments.ARG_MANAGER)) != null)
        {
            this.manager = text;
        } // if

        // get legalForm:
        if ((text = this.env.getParam (MadArguments.ARG_LEGALFORM)) != null)
        {
            this.legalForm = text;
        } // if

        // get mwSt:
        if ((number = this.env.getIntParam (MadArguments.ARG_MWST)) !=
            IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            this.mwSt = number;
        } // if
    } // getParameters


    /**************************************************************************
     * Represent the properties of a Company_01 object to the user. <BR/>
     *
     * @param   table   Table where the properties shall be added.
     *
     * @see ibs.bo.BusinessObject#showProperties
     * @see ibs.bo.BusinessObject#showProperty
     */
    protected void showProperties (TableElement table)
    {
        // loop through all properties of this object and display them:
        super.showProperties (table);
        this.showProperty (table, MadArguments.ARG_COMPOWNER, 
            MultilingualTextProvider.getText (MadTokens.TOK_BUNDLE,
                MadTokens.ML_COMPOWNER, env),
            Datatypes.DT_TEXT, this.compowner);
        this.showProperty (table, MadArguments.ARG_MANAGER, 
            MultilingualTextProvider.getText (MadTokens.TOK_BUNDLE,
                MadTokens.ML_MANAGER, env),
            Datatypes.DT_TEXT, this.manager);
        this.showProperty (table, MadArguments.ARG_LEGALFORM, 
            MultilingualTextProvider.getText (MadTokens.TOK_BUNDLE,
                MadTokens.ML_LEGALFORM, env),
            Datatypes.DT_TEXT, this.legalForm);
        this.showProperty (table, MadArguments.ARG_MWST,  
            MultilingualTextProvider.getText (MadTokens.TOK_BUNDLE,
                MadTokens.ML_MWST, env),
            Datatypes.DT_INTEGER, this.mwSt);
    } // showProperties


    /**************************************************************************
     * Represent the properties of a Company_01 object to the user
     * within a form. <BR/>
     *
     * @param   table   Table where the properties shall be added.
     *
     * @see ibs.bo.BusinessObject#showFormProperties
     * @see ibs.bo.BusinessObject#showFormProperty
     */
    protected void showFormProperties (TableElement table)
    {
        // loop through all properties of this object and display them:
        super.showFormProperties (table);
        this.showFormProperty (table, MadArguments.ARG_COMPOWNER, 
            MultilingualTextProvider.getText (MadTokens.TOK_BUNDLE,
                MadTokens.ML_COMPOWNER, env),
            Datatypes.DT_TEXT, this.compowner);
        this.showFormProperty (table, MadArguments.ARG_MANAGER, 
            MultilingualTextProvider.getText (MadTokens.TOK_BUNDLE,
                MadTokens.ML_MANAGER, env),
            Datatypes.DT_TEXT, this.manager);
        this.showFormProperty (table, MadArguments.ARG_LEGALFORM, 
            MultilingualTextProvider.getText (MadTokens.TOK_BUNDLE,
                MadTokens.ML_LEGALFORM, env),
            Datatypes.DT_TEXT, this.legalForm);
        this.formFieldRestriction = new FormFieldRestriction (false, 3, 3, "0",
            "100");
        this.showFormProperty (table, MadArguments.ARG_MWST, 
            MultilingualTextProvider.getText (MadTokens.TOK_BUNDLE,
                MadTokens.ML_MWST, env),
            Datatypes.DT_INTEGER, this.mwSt);
    } // showFormProperties


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
        // get the type specific values:
        if (dataElement.exists (Company_01.FIELD_COMPANYOWNER))
        {
            this.compowner = dataElement
                .getImportStringValue (Company_01.FIELD_COMPANYOWNER);
        } // if
        if (dataElement.exists (Company_01.FIELD_MANAGER))
        {
            this.manager = dataElement
                .getImportStringValue (Company_01.FIELD_MANAGER);
        } // if
        if (dataElement.exists (Company_01.FIELD_LEGALFORM))
        {
            this.legalForm = dataElement
                .getImportStringValue (Company_01.FIELD_LEGALFORM);
        } // if
        if (dataElement.exists (Company_01.FIELD_VAT))
        {
            this.mwSt = dataElement.getImportIntValue (Company_01.FIELD_VAT);
        } // if
    }  // readImportData


    /**************************************************************************
     * Writes the object data to a DataElement. <BR/>
     *
     * @param dataElement     the dataElement to write the data to
     *
     * @see ibs.bo.BusinessObject#writeExportData
     */
    public void writeExportData (DataElement dataElement)
    {
        // set the business object specific values
        super.writeExportData (dataElement);
        // set the type specific values:
        dataElement.setExportValue (Company_01.FIELD_COMPANYOWNER,
            this.compowner);
        dataElement.setExportValue (Company_01.FIELD_MANAGER, this.manager);
        dataElement.setExportValue (Company_01.FIELD_LEGALFORM, this.legalForm);
        dataElement.setExportValue (Company_01.FIELD_VAT, this.mwSt);
    } // writeExportData

} // class Company_01
