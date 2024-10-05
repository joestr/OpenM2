/*
 * Class: Address_01.java
 */

// package:
package m2.mad;

// imports:
import ibs.bo.BOArguments;
import ibs.bo.BOTokens;
import ibs.bo.BusinessObject;
import ibs.bo.Buttons;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.di.DataElement;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.TableElement;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.StoredProcedure;
import ibs.util.FormFieldRestriction;


/******************************************************************************
 * This class represents one object of type Address with version 01. <BR/>
 *
 * @version     $Id: Address_01.java,v 1.18 2013/01/16 16:14:13 btatzmann Exp $
 *
 * @author      Keim Christine (CK), 980603
 ******************************************************************************
 */
public class Address_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Address_01.java,v 1.18 2013/01/16 16:14:13 btatzmann Exp $";


    /**
     * Street. <BR/>
     */
    public String street = "";

    /**
     * Zip. <BR/>
     */
    public String zip = "";

    /**
     * Town. <BR/>
     */
    public String town = "";

    /**
     * Mailbox. <BR/>
     */
    public String mailbox = "";

    /**
     * Country. <BR/>
     */
    public String country = "";

    /**
     * Tel. <BR/>
     */
    public String tel = "";

    /**
     * Fax. <BR/>
     */
    public String fax = "";

    /**
     * Email. <BR/>
     */
    public String email = "";

    /**
     * Homepage. <BR/>
     */
    public String homepage = "";

    /**
     * Field name: address - street. <BR/>
     */
    private static final String FIELD_STREET = "street";
    /**
     * Field name: address - zip. <BR/>
     */
    private static final String FIELD_ZIP = "zip";
    /**
     * Field name: address - city. <BR/>
     */
    private static final String FIELD_CITY = "town";
    /**
     * Field name: mailbox. <BR/>
     */
    private static final String FIELD_MAILBOX = "mailbox";
    /**
     * Field name: address - country. <BR/>
     */
    private static final String FIELD_COUNTRY = "country";
    /**
     * Field name: telephone number. <BR/>
     */
    private static final String FIELD_PHONE = "tel";
    /**
     * Field name: fax number. <BR/>
     */
    private static final String FIELD_FAX = "fax";
    /**
     * Field name: email address. <BR/>
     */
    private static final String FIELD_EMAIL = "email";
    /**
     * Field name: homepage address. <BR/>
     */
    private static final String FIELD_HOMEPAGE = "homepage";


    /**************************************************************************
     * This constructor creates a new instance of the class Address_01.
     * <BR/>
     */
    public Address_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // Address_01


    /**************************************************************************
     * Creates a Address_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public Address_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // Address_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set the class-procedureNames
        this.procCreate   = "p_Address_01$create";
        this.procChange   = "p_Address_01$change";
        this.procRetrieve = "p_Address_01$retrieve";
        this.procDelete   = "p_Address_01$delete";

        // set extended search flag
        this.searchExtended = true;

        // set db table name
        this.tableName = "m2_Address_01";

        // set number of parameters for procedure calls:
        this.specificRetrieveParameters = 9;
        this.specificChangeParameters = 9;
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
        // street
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.street);
        // zip
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.zip);
        // town
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.town);
        // mailbox
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.mailbox);
        // country
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.country);
        // tel
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.tel);
        // fax
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.fax);
        // email
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.email);
        // homepage
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.homepage);
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
        // street
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // zip
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // town
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // mailbox
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // country
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // tel
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // fax
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // email
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // homepage
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);

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
        this.street = params[++i].getValueString ();
        this.zip = params[++i].getValueString ();
        this.town = params[++i].getValueString ();
        this.mailbox = params[++i].getValueString ();
        this.country = params[++i].getValueString ();
        this.tel = params[++i].getValueString ();
        this.fax = params[++i].getValueString ();
        this.email = params[++i].getValueString ();
        this.homepage = params[++i].getValueString ();
    } // getSpecificRetrieveParameters


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
        if (this.isTab ())
        {
            // define buttons to be displayed:
            int [] temp =
            {
                Buttons.BTN_EDIT,
                Buttons.BTN_COPY,
                Buttons.BTN_DISTRIBUTE,
                Buttons.BTN_SEARCH,
//                Buttons.BTN_HELP,
//                Buttons.BTN_LOGIN,
            }; // buttons

            buttons = temp;
        } // if
        else
        {
            // define buttons to be displayed:
            int [] temp =
            {
                Buttons.BTN_EDIT,
                Buttons.BTN_DELETE,
                Buttons.BTN_CUT,
                Buttons.BTN_COPY,
                Buttons.BTN_DISTRIBUTE,
                Buttons.BTN_SEARCH,
//                Buttons.BTN_HELP,
//                Buttons.BTN_LOGIN,
            }; // buttons

            buttons = temp;
        } // else

        // return button array
        return buttons;
    } // showInfoButtons


    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     * The <A HREF="#env">env</A> property is used for getting the parameters.
     * This property must be set before calling this method. <BR/>
     */
    public void getParameters ()
    {
        String text = "";

        super.getParameters ();

        // get name:
        if ((text = this.env.getParam (BOArguments.ARG_NAME)) != null)
        {
            this.name = text;
        } // if

        // get street:
        if ((text = this.env.getParam (MadArguments.ARG_STREET)) != null)
        {
            this.street = text;
        } // if

        // get zip:
        if ((text = this.env.getParam (MadArguments.ARG_ZIP)) != null)
        {
            this.zip = text;
        } // if

        // get town:
        if ((text = this.env.getParam (MadArguments.ARG_TOWN)) != null)
        {
            this.town = text;
        } // if

        // get mailbox:
        if ((text = this.env.getParam (MadArguments.ARG_MAILBOX)) != null)
        {
            this.mailbox = text;
        } // if

        // get country:
        if ((text = this.env.getParam (MadArguments.ARG_COUNTRY)) != null)
        {
            this.country = text;
        } // if

        // get tel:
        if ((text = this.env.getParam (MadArguments.ARG_TEL)) != null)
        {
            this.tel = text;
        } // if

        // get fax:
        if ((text = this.env.getParam (MadArguments.ARG_FAX)) != null)
        {
            this.fax = text;
        } // if

        // get email:
        if ((text = this.env.getParam (MadArguments.ARG_EMAIL)) != null)
        {
            this.email = text;
        } // if

        // get homepage:
        if ((text = this.env.getParam (MadArguments.ARG_HOMEPAGE)) != null)
        {
            this.homepage = text;
        } // if
    } // getParameters


    /**************************************************************************
     * Represent the properties of a Adress_01 object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     *
     * @see BusinessObject#showProperties
     * @see ibs.IbsObject#showProperty
     */
    protected void showProperties (TableElement table)
    {
        // loop through all properties of this object and display them:
//        showProperty (table, BOArguments.ARG_NAME, TOK_NAME, Datatypes.DT_NAME, name);
//        showProperty (table, BOArguments.ARG_TYPE, TOK_TYPE, Datatypes.DT_TYPE, typeName);
        if (!this.isTab ())
        {
            this.showProperty (table, BOArguments.ARG_NAME, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NAME, env),
                Datatypes.DT_NAME, this.name);
        } // if
        else
        {
            this.showProperty (table, BOArguments.ARG_NAME, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NAME, env),
                Datatypes.DT_HIDDEN, this.name);
        } // else

        this.showProperty (table, BOArguments.ARG_INNEWS, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_INNEWS, env),
            Datatypes.DT_BOOL, "" + this.showInNews);
        this.showProperty (table, BOArguments.ARG_VALIDUNTIL,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_VALIDUNTIL, env), Datatypes.DT_DATE, this.validUntil);
        if (this.isLink)
        {
            this.showProperty (table, BOArguments.ARG_LINKEDOBJECTID,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_LINKEDOBJECT, env), Datatypes.DT_LINK,
                this.linkedObjectId);
        } // if
        if (this.getUserInfo ().userProfile.showExtendedAttributes)
        {
            this.showProperty (table, null, null, Datatypes.DT_SEPARATOR,
                (String) null);
            this.showProperty (table, BOArguments.ARG_OWNER,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_OWNER, env), Datatypes.DT_USER, this.owner);
            this.showProperty (table, BOArguments.ARG_CHANGED,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_CHANGED, env), Datatypes.DT_USERDATE, this.changer,
                this.lastChanged);
        } // if (app.userInfo.userProfile.showExtendedAttributes)

        this.showProperty (table, null, null, Datatypes.DT_SEPARATOR,
            (String) null);
        this.showProperty (table, MadArguments.ARG_STREET, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_STREET, env),
            Datatypes.DT_TEXT, this.street);
        this.showProperty (table, MadArguments.ARG_ZIP, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_ZIP, env),
            Datatypes.DT_TEXT, this.zip);
        this.showProperty (table, MadArguments.ARG_TOWN, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_TOWN, env),
            Datatypes.DT_TEXT, this.town);
        this.showProperty (table, MadArguments.ARG_MAILBOX,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MAILBOX, env), Datatypes.DT_TEXT, this.mailbox);
        this.showProperty (table, MadArguments.ARG_COUNTRY,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_COUNTRY, env), Datatypes.DT_TEXT, this.country);
        this.showProperty (table, MadArguments.ARG_TEL, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_TEL, env),
            Datatypes.DT_TEXT, this.tel);
        this.showProperty (table, MadArguments.ARG_FAX, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_FAX, env),
            Datatypes.DT_TEXT, this.fax);
        this.showProperty (table, MadArguments.ARG_EMAIL, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_EMAIL, env),
            Datatypes.DT_EMAIL, this.email);
        this.showProperty (table, MadArguments.ARG_HOMEPAGE,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_HOMEPAGE, env), Datatypes.DT_URL, this.homepage);

    } // showProperties


    /**************************************************************************
     * Represent the properties of a Address_01 object to the user
     * within a form. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     *
     * @see BusinessObject#showFormProperties
     * @see ibs.IbsObject#showFormProperty
     */
    protected void showFormProperties (TableElement table)
    {
        // loop through all properties of this object and display them:
        if (this.isTab ())
        {
            this.formFieldRestriction =
                new FormFieldRestriction (false);
            this.showFormProperty (table, BOArguments.ARG_NAME,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NAME, env), Datatypes.DT_NAME, this.name);
        } // if
        else
        {
            this.showProperty (table, BOArguments.ARG_NAME, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NAME, env),
                Datatypes.DT_HIDDEN, this.name);
        } // else
        this.showFormProperty (table, BOArguments.ARG_INNEWS,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_INNEWS, env), Datatypes.DT_BOOL, "" + this.showInNews);
        this.showFormProperty (table, BOArguments.ARG_VALIDUNTIL,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_VALIDUNTIL, env), Datatypes.DT_DATE, this.validUntil);
        this.showProperty (table, null, null, Datatypes.DT_SEPARATOR,
            (String) null);
        this.showFormProperty (table, MadArguments.ARG_STREET,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_STREET, env), Datatypes.DT_TEXT, this.street);
        this.formFieldRestriction = new FormFieldRestriction (true, 15, 15);
        this.showFormProperty (table, MadArguments.ARG_ZIP, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_ZIP, env),
            Datatypes.DT_TEXT, this.zip);
        this.showFormProperty (table, MadArguments.ARG_TOWN, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_TOWN, env),
            Datatypes.DT_TEXT, this.town);
        this.showFormProperty (table, MadArguments.ARG_MAILBOX,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_MAILBOX, env), Datatypes.DT_TEXT, this.mailbox);
        this.showFormProperty (table, MadArguments.ARG_COUNTRY,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_COUNTRY, env), Datatypes.DT_TEXT, this.country);
        this.showFormProperty (table, MadArguments.ARG_TEL, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_TEL, env),
            Datatypes.DT_TEXT, this.tel);
        this.showFormProperty (table, MadArguments.ARG_FAX, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_FAX, env),
            Datatypes.DT_TEXT, this.fax);
        this.showFormProperty (table, MadArguments.ARG_EMAIL,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_EMAIL, env), Datatypes.DT_EMAIL, this.email);
        this.showFormProperty (table, MadArguments.ARG_HOMEPAGE,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_HOMEPAGE, env), Datatypes.DT_TEXT, this.homepage);
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
        if (dataElement.exists (Address_01.FIELD_STREET))
        {
            this.street = dataElement
                .getImportStringValue (Address_01.FIELD_STREET);
        } // if
        if (dataElement.exists (Address_01.FIELD_ZIP))
        {
            this.zip = dataElement.getImportStringValue (Address_01.FIELD_ZIP);
        } // if
        if (dataElement.exists (Address_01.FIELD_CITY))
        {
            this.town = dataElement
                .getImportStringValue (Address_01.FIELD_CITY);
        } // if
        if (dataElement.exists (Address_01.FIELD_MAILBOX))
        {
            this.mailbox = dataElement
                .getImportStringValue (Address_01.FIELD_MAILBOX);
        } // if
        if (dataElement.exists (Address_01.FIELD_COUNTRY))
        {
            this.country = dataElement
                .getImportStringValue (Address_01.FIELD_COUNTRY);
        } // if
        if (dataElement.exists (Address_01.FIELD_PHONE))
        {
            this.tel = dataElement
                .getImportStringValue (Address_01.FIELD_PHONE);
        } // if
        if (dataElement.exists (Address_01.FIELD_FAX))
        {
            this.fax = dataElement.getImportStringValue (Address_01.FIELD_FAX);
        } // if
        if (dataElement.exists (Address_01.FIELD_EMAIL))
        {
            this.email = dataElement
                .getImportStringValue (Address_01.FIELD_EMAIL);
        } // if
        if (dataElement.exists (Address_01.FIELD_HOMEPAGE))
        {
            this.homepage = dataElement
                .getImportStringValue (Address_01.FIELD_HOMEPAGE);
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
        dataElement.setExportValue (Address_01.FIELD_STREET, this.street);
        dataElement.setExportValue (Address_01.FIELD_ZIP, this.zip);
        dataElement.setExportValue (Address_01.FIELD_CITY, this.town);
        dataElement.setExportValue (Address_01.FIELD_MAILBOX, this.mailbox);
        dataElement.setExportValue (Address_01.FIELD_COUNTRY, this.country);
        dataElement.setExportValue (Address_01.FIELD_PHONE, this.tel);
        dataElement.setExportValue (Address_01.FIELD_FAX, this.fax);
        dataElement.setExportEmailValue (Address_01.FIELD_EMAIL, this.email);
        dataElement.setExportHyperlinkValue (Address_01.FIELD_HOMEPAGE, this.homepage);
    } // writeExportData

} // class Address_01
