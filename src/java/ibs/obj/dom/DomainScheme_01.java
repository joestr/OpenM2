/*
 * Class: DomainScheme_01.java
 */

// package:
package ibs.obj.dom;

// imports:
import ibs.bo.BOArguments;
import ibs.bo.BOTokens;
import ibs.bo.BusinessObject;
import ibs.bo.Buttons;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.di.DataElement;
import ibs.io.IOConstants;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.TableElement;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.StoredProcedure;
import ibs.util.FormFieldRestriction;


/******************************************************************************
 * This class represents one object of type DomainScheme with version 01. <BR/>
 * Attachments represent relationships between objects and files or webpages.
 * An object can have multiple attachments. In case multiple attachments have been
 * assigned one must be set as master which means that these master assignments
 * will be displayed when the content of an objects is viewed by the user.
 * Within a set of attachments there can only be one master attachment. <BR/>
 * This version of attachment does not support compound documents. (files that
 * includes other files like HTML files). <BR/>
 *
 * @version     $Id: DomainScheme_01.java,v 1.14 2013/01/16 16:14:13 btatzmann Exp $
 *
 * @author      Stampfer Heinz Josef (HJ), 980415
 ******************************************************************************
 */
public class DomainScheme_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DomainScheme_01.java,v 1.14 2013/01/16 16:14:13 btatzmann Exp $";


    /**
     * Does a domain created with this scheme has a catalog management? <BR/>
     */
    public boolean hasCatalogManagement = false;

    /**
     * Does a domain created with this scheme has data interchange? <BR/>
     */
    public boolean hasDataInterchange = false;

    /**
     * The name of the stored procedure used to create the workspace of an user
     * within domains having this scheme. <BR/>
     */
    public String workspaceProc = null;

    /**
     * The number of domains where this scheme is used.. <BR/>
     */
    public int numberOfDomains = 0;


    /**************************************************************************
     * This constructor creates a new instance of the class Attachment_01.
     * <BR/>
     */
    public DomainScheme_01 ()
    {
        // call constructor of super class:
        super ();

        // initialize properties common to all subclasses:
    } // DomainScheme_01


    /**************************************************************************
     * Creates a DomainScheme_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public DomainScheme_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:
    } // DomainScheme_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        this.procCreate =     "p_DomainScheme_01$create";
        this.procChange =     "p_DomainScheme_01$change";
        this.procRetrieve =   "p_DomainScheme_01$retrieve";
        this.procDelete =     "p_DomainScheme_01$delete";
        this.procDeleteRec =  "p_DomainScheme_01$delete";

        // set db table name:
        this.tableName = "ibs_DomainScheme_01";

        // set number of parameters for procedure calls:
        this.specificRetrieveParameters = 4;
        this.specificChangeParameters = 3;
    } // initClassSpecifics


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

        // hasCatalogManagement
        if ((num = this.env.getBoolParam (BOArguments.ARG_HASCATALOGMMT)) >=
            IOConstants.BOOLPARAM_FALSE)
        {
            this.hasCatalogManagement = num == IOConstants.BOOLPARAM_TRUE;
        } // if

        // hasDataInterchange
        if ((num = this.env.getBoolParam (BOArguments.ARG_HASDATAINTERCHANGE)) >=
            IOConstants.BOOLPARAM_FALSE)
        {
            this.hasDataInterchange = num == IOConstants.BOOLPARAM_TRUE;
        } // if

        // workspaceProc
        if ((str = this.env.getStringParam (BOArguments.ARG_WORKSPACEPROC)) != null)
        {
            this.workspaceProc = str;
        } // if
    } // getParameters


    /**************************************************************************
     * Represent the properties of a DomainScheme_01 object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showProperties (TableElement table)
    {
        // display the base object's properties:
        super.showProperties (table);

        // loop through all properties of this object and display them:
        this.showProperty (
            table, BOArguments.ARG_HASCATALOGMMT, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_HASCATALOGMMT, env),
            Datatypes.DT_BOOL, "" + this.hasCatalogManagement);
        this.showProperty (
            table, BOArguments.ARG_HASDATAINTERCHANGE,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_HASDATAINTERCHANGE, env), Datatypes.DT_BOOL, "" +
                this.hasDataInterchange);
        this.showProperty (
            table, BOArguments.ARG_WORKSPACEPROC, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_WORKSPACEPROC, env),
            Datatypes.DT_NAME, "" + this.workspaceProc);
        this.showProperty (
            table, BOArguments.ARG_NOARG, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NUMBEROFDOMAINS, env),
            Datatypes.DT_INTEGER, "" + this.numberOfDomains);
    } // showProperties


    /**************************************************************************
     * Represent the properties of a DomainScheme_01 object to the user
     * within a form. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showFormProperties (TableElement table)
    {
        // display the base object's properties:
        super.showFormProperties (table);

        // loop through all properties of this object and display them:
        this.showFormProperty (table, BOArguments.ARG_HASCATALOGMMT,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_HASCATALOGMMT, env), Datatypes.DT_BOOL,
            "" + this.hasCatalogManagement);
        this.showFormProperty (table, BOArguments.ARG_HASDATAINTERCHANGE,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_HASDATAINTERCHANGE, env), Datatypes.DT_BOOL,
            "" + this.hasDataInterchange);
        // restriction: empty not allowed
        this.formFieldRestriction = new FormFieldRestriction (false);
        this.showFormProperty (table, BOArguments.ARG_WORKSPACEPROC,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_WORKSPACEPROC, env), Datatypes.DT_NAME,
            "" + this.workspaceProc);
    } // showFormProperties


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
        // hasCatalogManagement
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN, this.hasCatalogManagement);
        // hasDataInterchange
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN, this.hasDataInterchange);
        // workspaceProc
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.workspaceProc);
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
        // hasCatalogManagement
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);
        // hasDataInterchange
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);
        // workspaceProc
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // numberOfDomains
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);

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
        this.hasCatalogManagement = params[++i].getValueBoolean ();
        this.hasDataInterchange = params[++i].getValueBoolean ();
        this.workspaceProc = params[++i].getValueString ();
        this.numberOfDomains = params[++i].getValueInteger ();
    } // getSpecificRetrieveParameters


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
        int [] buttons = null;          // the buttons to be displayed

        // define buttons to be displayed:
        if (this.numberOfDomains <= 0)  // the domain scheme is never used?
        {
            int [] buttonsNoDomain =
            {
                Buttons.BTN_EDIT,
                Buttons.BTN_DELETE,
                Buttons.BTN_CUT,
                Buttons.BTN_COPY,
                Buttons.BTN_DISTRIBUTE,
//                Buttons.BTN_STARTWORKFLOW,
//                Buttons.BTN_FORWARD,
//                Buttons.BTN_FINISHWORKFLOW,
                Buttons.BTN_SEARCH,
//                Buttons.BTN_HELP,
//                Buttons.BTN_EXPORT,
            }; // buttons
            buttons = buttonsNoDomain;
        } // if the domain scheme is never used
        else                            // the domain scheme is used
        {
            int [] buttonsWithDomain =
            {
                Buttons.BTN_COPY,
                Buttons.BTN_DISTRIBUTE,
                Buttons.BTN_FORWARD,
                Buttons.BTN_SEARCH,
//                Buttons.BTN_HELP,
//                Buttons.BTN_EXPORT,
            }; // buttons
            buttons = buttonsWithDomain;
        } // else the domain scheme is used

        // return button array
        return buttons;
    } // setInfoButtons


    /**************************************************************************
     * Reads the object data from a DataElement. <BR/>
     *
     * @param dataElement   The dataElement to read the data from.
     *
     * @see ibs.bo.BusinessObject#readImportData
     */
    public void readImportData (DataElement dataElement)
    {
        // get business object specific values
        super.readImportData (dataElement);

        // get the type specific values:
        if (dataElement.exists ("hasCatalogManagement"))
        {
            this.hasCatalogManagement = dataElement.getImportBooleanValue ("hasCatalogManagement");
        } // if
        if (dataElement.exists ("hasDataInterchange"))
        {
            this.hasDataInterchange = dataElement.getImportBooleanValue ("hasDataInterchange");
        } // if
        if (dataElement.exists ("workspaceProc"))
        {
            this.workspaceProc = dataElement.getImportStringValue ("workspaceProc");
        } // if
    } // readImportData


    /**************************************************************************
     * Writes the object data to a DataElement. <BR/>
     *
     * @param dataElement   The dataElement to write the data to.
     *
     * @see ibs.bo.BusinessObject#writeExportData
     */
    public void writeExportData (DataElement dataElement)
    {
        // set the business object specific values
        super.writeExportData (dataElement);

        // set the type specific values:
        dataElement.setExportValue ("hasCatalogManagement", this.hasCatalogManagement);
        dataElement.setExportValue ("hasDataInterchange", this.hasDataInterchange);
        dataElement.setExportValue ("workspaceProc", this.workspaceProc);
    } // writeExportData

} // class DomainScheme_01
