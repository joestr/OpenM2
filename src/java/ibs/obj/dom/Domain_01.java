/*
 * Class: Domain_01.java
 */

// package:
package ibs.obj.dom;

// imports:
import ibs.bo.BOArguments;
import ibs.bo.BOMessages;
import ibs.bo.BOTokens;
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.bo.SelectionList;
import ibs.bo.States;
import ibs.di.DataElement;
import ibs.io.IOConstants;
import ibs.ml.MultilingualTextProvider;
import ibs.service.conf.ServerRecord;
import ibs.service.user.User;
import ibs.tech.html.TableElement;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.StoredProcedure;


/******************************************************************************
 * This class represents one object of type Type with version 01. <BR/>
 *
 * @version     $Id: Domain_01.java,v 1.19 2013/01/16 16:14:13 btatzmann Exp $
 *
 * @author      Klaus Reimüller (KR), 980727
 ******************************************************************************
 */
public class Domain_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Domain_01.java,v 1.19 2013/01/16 16:14:13 btatzmann Exp $";


    /**
     * The id of the scheme of this domain. <BR/>
     */
    public int domainScheme = 0;

    /**
     * The name of the scheme of this domain. <BR/>
     */
    public String domainSchemeName = null;

    /**
     * The path through which the domain is addressed within url. <BR/>
     */
    public String homepagePath = null;

    /**
     * The information if SSL should be used within this domain or not. <BR/>
     */
    public boolean sslRequired = false;


    /**************************************************************************
     * This constructor creates a new instance of the class Domain_01.
     * <BR/>
     */
    public Domain_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();
    } // Domain_01


    /**************************************************************************
     * Creates a Domain_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public Domain_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // Domain_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set class specifics of super class:
        super.initClassSpecifics ();

        // set common attributes:
        this.procCreate = "p_Domain_01$create";
        this.procChange = "p_Domain_01$change";
        this.procRetrieve = "p_Domain_01$retrieve";
//        this.procCopy = "p_Domain_01$copy";
        this.procDelete = "p_Domain_01$delete";
        this.procDeleteRec = "p_Domain_01$delete";

        // set the instance's attributes:

        // set number of parameters for procedure calls:
        this.specificRetrieveParameters += 4;
        this.specificChangeParameters += 3;
        this.specificCreateParameters += 1;
    } // initClassSpecifics


    ///////////////////////////////////////////////////////////////////////////
    // database functions
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // viewing functions
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     * The <A HREF="#env">env</A> property is used for getting the parameters.
     * This property must be set before calling this method. <BR/>
     */
    public void getParameters ()
    {
        String str = null;
        int num = 0;

        // call method of super class:
        super.getParameters ();

        // domainScheme
        if ((num = this.env.getIntParam (BOArguments.ARG_DOMAINSCHEME)) !=
                IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            this.domainScheme = num;
        } // if

        // homepagePath
        if ((str = this.env.getStringParam (BOArguments.ARG_HOMEPAGEPATH)) != null)
        {
            this.homepagePath = str;
        } // if

        // sslRequired option
        if ((num = this.env.getBoolParam (BOArguments.ARG_DOMAINSSLREQUIRED)) >=
            IOConstants.BOOLPARAM_FALSE)
        {
            this.sslRequired = num == IOConstants.BOOLPARAM_TRUE;
        } // if
    } // getParameters


    /**************************************************************************
     * Represent the properties of a Domain_01 object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showProperties (TableElement table)
    {
        // display the base object's properties:
        super.showProperties (table);

        // loop through all properties of this object and display them:
        this.showProperty (table, BOArguments.ARG_DOMAINSCHEME,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_DOMAINSCHEME, env), Datatypes.DT_TEXT, this.domainSchemeName);
        this.showProperty (table, BOArguments.ARG_HOMEPAGEPATH,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_HOMEPAGEPATH, env), Datatypes.DT_TEXT, this.homepagePath);
        this.showProperty (table, BOArguments.ARG_DOMAINSSLREQUIRED,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_DOMAINSSLREQUIRED, env), Datatypes.DT_BOOL,
            "" + this.sslRequired);
    } // showProperties


    /**************************************************************************
     * Represent the properties of a Domain_01 object to the user
     * within a form. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showFormProperties (TableElement table)
    {
        StringBuffer queryStr = null;   // query for selection list
        SelectionList selList = null;   // the selection list

        // display the base object's properties:
        super.showFormProperties (table);

        // loop through all properties of this object and display them:
        if (this.domainScheme == 0)     // the property was not set yet?
        {
/*
            selList = performRetrieveSelectionListData (
                createTVersionId (Types.TYPE_DomainScheme_01), true);
*/
/************
 * HACK:
 * The Query shall be reimplemented as view and be thrown away from this class.
 */
            queryStr = new StringBuffer ()
                .append ("SELECT ds.id AS oid, o.name ")
                .append (" FROM ibs_DomainScheme_01 ds, ibs_Object o ")
                .append (" WHERE ds.oid = o.oid ")
                .append (" AND o.state = ").append (States.ST_ACTIVE);
            selList = this.performRetrieveSelectionListDataQuery (
                true, queryStr, "");
            // edit the property only if it was not set yet:
            this.showFormProperty (table, BOArguments.ARG_DOMAINSCHEME,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_DOMAINSCHEME, env),
                Datatypes.DT_SELECT, "" + this.domainScheme, selList.ids,
                selList.values, 0);
            // provide the user with an explanation:
            this.showProperty (table, BOArguments.ARG_NOARG,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_EXPLANATION, env), 
                Datatypes.DT_HTMLTEXT,
                MultilingualTextProvider.getMessage(BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_DOMAINSCHEMECHANGE_DESCRIPTION, env));
        } // if the property was not set yet

        this.showFormProperty (table, BOArguments.ARG_HOMEPAGEPATH,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_HOMEPAGEPATH, env), 
            Datatypes.DT_TEXT, this.homepagePath);
        // provide the user with an explanation:
        this.showProperty (table, BOArguments.ARG_NOARG, 
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_EXPLANATION, env),
            Datatypes.DT_HTMLTEXT,
            MultilingualTextProvider.getMessage(BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_DOMAINHOMEPAGEPATH_DESCRIPTION, env));

        // edit the ssl-property
        this.showFormProperty (table, BOArguments.ARG_DOMAINSSLREQUIRED,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_DOMAINSSLREQUIRED, env),
            Datatypes.DT_BOOL, "" + this.sslRequired);

        // provide the user with an explanation:
        this.showProperty (table, BOArguments.ARG_NOARG, 
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_EXPLANATION, env),
            Datatypes.DT_HTMLTEXT,
            MultilingualTextProvider.getMessage(BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_DOMAINSSLREQUIRED_DESCRIPTION, env));
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
//            Buttons.BTN_HELP,
        }; // buttons

        // return button array
        return buttons;
    } // setInfoButtons


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * container's content view. <BR/>
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
            Buttons.BTN_SEARCH,
//          Buttons.BTN_HELP,
        }; // buttons

        // return button array
        return buttons;
    } // setContentButtons


    /***************************************************************************
     * Set the data for the additional (typespecific) parameters for
     * performCreateData. <BR/>
     * This method must be overwritten by all subclasses that have to pass
     * typespecific data to the create data stored procedure.
     *
      * @param sp        The stored procedure to add the create parameters to.
     */
     @Override
     protected void setSpecificCreateParameters (StoredProcedure sp)
     {
        // ssl for the domain,
        // default value = true if ssl is installed and configured, else = false
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN,
            ((ServerRecord) this.sess.actServerConfiguration).getSsl ());
    } // setSpecificCreateParameters


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
        // add specific parameters
        // domainScheme
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
            this.domainScheme);
        // homepagePath
        sp.addInParameter (ParameterConstants.TYPE_STRING,
            this.homepagePath);
        // ssl for the domain
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN,
            this.sslRequired);
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
        // domainScheme
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
        // domainSchemeName
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // homepagePath
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // ssl for the domain
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);

        return i;                       // return the current index
    } // setSpecificRetrieveParameters


    /**************************************************************************
     * Get the data for the additional (typespecific) parameters for
     * performRetrieveData. <BR/>
     * This method must be overwritten by all subclasses that have to get
     * typespecific data from the retrieve data stored procedure.
     *
     * @param params    The array of parameters from the retrieve data stored
     *                  procedure.
     * @param lastIndex The index to the last element used in params thus far.
     *
     * @see ibs.bo.BusinessObject#performRetrieveData
     */
    protected void getSpecificRetrieveParameters (Parameter[] params,
                                                  int lastIndex)
    {
        int i = lastIndex;              // initialize params index

        // get the specific parameters:
        this.domainScheme = params[++i].getValueInteger ();
        this.domainSchemeName = params[++i].getValueString ();
        this.homepagePath = params[++i].getValueString ();
        this.sslRequired = params[++i].getValueBoolean ();
    } // getSpecificRetrieveParameters


    /**************************************************************************
     * Reads the object data from a DataElement. <BR/>
     *
     * @param dataElement   The dataElement to read the data from.
     */
    public void readImportData (DataElement dataElement)
    {
        // get business object specific values
        super.readImportData (dataElement);

        // get the type specific values:
        if (dataElement.exists ("domainScheme"))
        {
            this.domainScheme = dataElement.getImportIntValue ("domainScheme");
        } // if
        if (dataElement.exists ("homepagePath"))
        {
            this.homepagePath = dataElement.getImportStringValue ("homepagePath");
        } // if
        if (dataElement.exists ("sslRequired"))
        {
            this.sslRequired = dataElement.getImportBooleanValue ("sslRequired");
        } // if
    } // readImportData


    /**************************************************************************
     * Writes the object data to a DataElement. <BR/>
     *
     * @param dataElement   The dataElement to write the data to.
     */
    public void writeExportData (DataElement dataElement)
    {
        // set the business object specific values
        super.writeExportData (dataElement);

        // set the type specific values:
        dataElement.setExportValue ("domainScheme", this.domainScheme);
        dataElement.setExportValue ("homepagePath", this.homepagePath);
        dataElement.setExportValue ("sslRequired", this.sslRequired);
    } // writeExportData

} // class Domain_01
