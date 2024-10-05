/*
 * Class: Order_01.java
 */

// package:
package m2.store;

// imports:
import ibs.app.AppConstants;
import ibs.app.CssConstants;
import ibs.bo.BOArguments;
import ibs.bo.BOHelpers;
import ibs.bo.BOMessages;
import ibs.bo.BOTokens;
import ibs.bo.BusinessObject;
import ibs.bo.Buttons;
import ibs.bo.Datatypes;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.bo.ObjectNotFoundException;
import ibs.bo.Operations;
import ibs.bo.SelectionList;
import ibs.bo.States;
import ibs.di.DIConstants;
import ibs.di.DataElement;
import ibs.di.DataElementList;
import ibs.di.connect.Connector_01;
import ibs.di.exp.ExportIntegrator;
import ibs.di.filter.Filter;
import ibs.di.trans.Translator_01;
import ibs.io.HtmlConstants;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.LayoutConstants;
import ibs.io.SsiFileNotFoundException;
import ibs.ml.MultilingualTextProvider;
import ibs.service.conf.Configuration;
import ibs.service.notification.INotificationService;
import ibs.service.notification.NotificationFailedException;
import ibs.service.notification.NotificationService;
import ibs.service.notification.NotificationServiceFactory;
import ibs.service.notification.NotificationTemplate;
import ibs.service.user.User;
import ibs.tech.html.BlankElement;
import ibs.tech.html.BuildException;
import ibs.tech.html.FormElement;
import ibs.tech.html.GroupElement;
import ibs.tech.html.IE302;
import ibs.tech.html.InputElement;
import ibs.tech.html.LineElement;
import ibs.tech.html.Page;
import ibs.tech.html.ParagraphElement;
import ibs.tech.html.RowElement;
import ibs.tech.html.ScriptElement;
import ibs.tech.html.SpanElement;
import ibs.tech.html.StyleSheetElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TableElement;
import ibs.tech.html.TextElement;
import ibs.tech.sql.DBError;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;
import ibs.tech.sql.StoredProcedureConstants;
import ibs.util.AlreadyDeletedException;
import ibs.util.DateTimeHelpers;
import ibs.util.FormFieldRestriction;
import ibs.util.Helpers;
import ibs.util.NameAlreadyGivenException;
import ibs.util.NoAccessException;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;

import java.util.Date;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import m2.diary.DiaryTokens;


/******************************************************************************
 * This class represents one BusinessObject of type Order with version 02.
 * It's a special class in the way that it is not a standard businnes object.
 *    - it's not a container though it contains elements (positions)
 *        reason: don't want the standard content tab, only info necessary
 *                because it's not possible to edit order position anyway.
 *    - it uses its own functions to create and send orders. <BR/>
 *
 * @version     $Id: Order_01.java,v 1.51 2013/01/16 16:14:12 btatzmann Exp $
 *
 * @author      Bernhard Walter (BW), 980923
 ******************************************************************************
 */
public class Order_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Order_01.java,v 1.51 2013/01/16 16:14:12 btatzmann Exp $";


    /**
     * Name of the supplier of the products. <BR/>
     */
    private String supplier = "";
    /**
     * A number which identifies the voucher. <BR/>
     */
    private String voucherNo = null;
    /**
     * The data when this order is created. <BR/>
     */
    private Date voucherDate = DateTimeHelpers.getCurAbsDate (); // set current date

    /**
     * The company where to send the order to. <BR/>
     */
    private String supplierCompany  = "";
    /**
     * The person responsible for orders in the supplier company. <BR/>
     */
    private String contactSupplier = "";
    /**
     * The oid of the user who is responsible for orders in the supplier company. <BR/>
     */
    public OID orderResponsibleOid = null;
    /**
     * The email-address of the user who is responsible for orders
     * in the supplier company. <BR/>
     */
    public String eMailOfOrderResponsible = null;
    /**
     * EMailAddress of current User - it's used as email - sender. <BR/>
     */
    public String eMailOfCurrentUser = null;
    /**
     * The oid of the media used to transmit the order. <BR/>
     */
    public OID orderResponsibleMediaOid = null;
    /**
     * Name of the company which makes the order. <BR/>
     */
    private String customerCompany = "";
    /**
     * Name of the person of the company which is the customer. <BR/>
     */
    private String contactCustomer = "";
    /**
     * The name where to deliver the order to. <BR/>
     */
    private String deliveryName = "";
    /**
     * The address where to deliver the order to. <BR/>
     */
    private String deliveryAddress = "";
    /**
     * The ZIP code of the delivery address. <BR/>
     */
    private String deliveryZIP = "";
    /**
     * The town of the delivery address. <BR/>
     */
    private String deliveryTown = "";
    /**
     * The country of the delivery address. <BR/>
     */
    private String deliveryCountry = "";
    /**
     * The name where to deliver the order to. <BR/>
     */
    private String paymentName = "";
    /**
     * The address where to deliver the bill to. <BR/>
     */
    private String paymentAddress = "";
    /**
     * The ZIP code of the payment address. <BR/>
     */
    private String paymentZIP = "";
    /**
     * The town of the payment address. <BR/>
     */
    private String paymentTown = "";
    /**
     * The country of the payment address. <BR/>
     */
    private String paymentCountry = "";
    /**
     * The address where to send the order to. <BR/>
     */
    private String deliveryAddressComplete = "";
    /**
     * The address where to send the bill to. <BR/>
     */
    private String paymentAddressComplete = "";

    /**
     * The address where to send the order to. <BR/>
     */
    private String supplierAddressComplete = "";

    /**
     * Description how the order should be handled if products not available
     * as wished by the customer. <BR/>
     */
    private String notPossibleDescription = "";
    /**
     * Description of the wished delivery modality. <BR/>
     */
    private String deliveryDescription = "";
    /**
     * Description of delivery method. <BR/>
     */
    private String shippmentDescription = "";
    /**
     * The date when the order should be delivered. <BR/>
     */
    private Date deliveryDate = DateTimeHelpers.getCurAbsDate (); // set current date

    /**
     * This vector holds the positions of the order. The element is
     * the OrderElement_01 class. <BR/>
     */
    private Vector<OrderElement_01> positions = null;
    /**
     * The initial size of the positions vector. <BR/>
     */
    private static final int ORDER_INITIAL_SIZE = 10;
    /**
     * The increment of the positions vector . <BR/>
     */
    private static final int ORDER_INCREMENT = 10;
    /**
     * The oid of the catalog. <BR/>
     */
    private OID catalogOid;

    /**
     * Send order after creating. <BR/>
     */
    public boolean sendOrder = true;

    /**
     * The name of the stored procedure giving all positions of the order. <BR/>
     */
    private static final String PROC_RETRIEVE_ORDER_DEFAULTS =
        "p_Order_01$retrDefaults";

    /**
     * The name of the stored procedure which changes the order date to the
     * current date. <BR/>
     */
    private static final String PROC_ACTUALIZE_ORDER_DATE =
        "p_Order_01$actOrderDate";


    /**
     * . <BR/>
     */
    private static String[] orderPosHeading =
    {
        StoreTokens.ML_ORDERQTY,
        StoreTokens.ML_UNITOFQTY_SHORT,
        StoreTokens.ML_PACKINGUNIT_SHORT,
        StoreTokens.ML_PRODUCT_DESCRIPTION,
        StoreTokens.ML_UNITPRICE,
        StoreTokens.ML_TOTALPRICE,
    };

    /**
     * Percentage: 5%. <BR/>
     */
    private static final String PERC_05 = "5%";
    /**
     * Percentage: 10%. <BR/>
     */
    private static final String PERC_10 = "10%";
    /**
     * Percentage: 55%. <BR/>
     */
    private static final String PERC_55 = "55%";

    /**
     * The widhts of the columns. <BR/>
     */
    private static String[] orderPosWidhts =
    {
        Order_01.PERC_10,
        Order_01.PERC_05,
        Order_01.PERC_10,
        Order_01.PERC_55,
        Order_01.PERC_10,
        Order_01.PERC_10,
    };

    /**
     * The alignments of the columns. <BR/>
     */
    private static String[] orderPosAlignments =
    {
        IOConstants.ALIGN_CENTER,
        IOConstants.ALIGN_CENTER,
        IOConstants.ALIGN_CENTER,
        IOConstants.ALIGN_LEFT,
        IOConstants.ALIGN_RIGHT,
        IOConstants.ALIGN_RIGHT,
    };

    /**
     * edit only state or edit all attributes (showFormProperties)
     */
    public boolean changeStateForm = false;

    /**
     * The identifier of the payment type. <BR/>
     */
    public int p_paymentTypeIdentifier = -1;

    /**
     * The oid of the payment type. <BR/>
     */
    public OID p_paymentTypeOid = null;

    /**
     * The name of the payment type. <BR/>
     */
    public String p_paymentTypeName = null;

    /**
     * The name of the creditcard owner. <BR/>
     */
    public String p_creditCardOwner = null;

    /**
     * The number of the creditcard. <BR/>
     */
    public String p_creditCardNumber = null;

    /**
     * The month expiry of the creditcard. <BR/>
     */
    public String p_creditCardExpiryMonth = null;

    /**
     * The year expiry of the creditcard. <BR/>
     */
    public String p_creditCardExpiryYear = null;

    /**
     * Icon for the object. <BR/>
     */
    private static final String CLASS_ICON = "Order.gif";



    /**************************************************************************
     * This constructor creates a new instance of the class Order.
     * <BR/>
     */
    public Order_01 ()
    {
        // call constructor of super class:
        super ();

        this.positions = null;
        this.positions = new Vector<OrderElement_01> (Order_01.ORDER_INITIAL_SIZE,
            Order_01.ORDER_INCREMENT);
        this.name = 
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_ORDER_FROM, env) + " " +
            DateTimeHelpers.dateToString (this.voucherDate);
        this.state = States.ST_ACTIVE;
    } // Order_01


    /**************************************************************************
     * This constructor creates a new instance of the class Order.
     * <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public Order_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize the instance's private properties:
        this.procCreate =     "p_Order_01$create";
        this.procRetrieve =   "p_Order_01$retrieve";
        this.procDelete =     "p_Order_01$delete";

        this.positions = null;
        this.positions = new Vector<OrderElement_01> (Order_01.ORDER_INITIAL_SIZE,
            Order_01.ORDER_INCREMENT);
        this.name = 
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_ORDER_FROM, env) + " " +
            DateTimeHelpers.dateToString (this.voucherDate);
        this.state = States.ST_ACTIVE;
       // set the instance's attributes:
    } // Order_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // initialize the instance's private properties:
        this.procCreate =     "p_Order_01$create";
        this.procRetrieve =   "p_Order_01$retrieve";
        this.procDelete =     "p_Order_01$delete";

        // set number of parameters for procedure calls:
        this.specificRetrieveParameters = 26;
    } // initClassSpecifics


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
        // voucherNo
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // voucherDate
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_DATE);
        // supplierCompany
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // contactSupplier
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // customerCompany
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // contactCustomer
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // deliveryAddress
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // paymentAddress
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // supplierAddress
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // description1
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // description2
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // description3
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // deliveryDate
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_DATE);
        // orderResponsibleOid
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // paymentTypeOid
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // paymentTypeId
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
        // paymentTypeName
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // creditCardOwner
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // creditCardNumber
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // creditCardExpiryMonth
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // creditCardExpiryYear
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // OrderrespEmail
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // currentUserEmail
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // catalogOid
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
        this.voucherNo                  = params[++i].getValueString ();
        this.voucherDate                = params[++i].getValueDate ();
        this.supplierCompany            = params[++i].getValueString ();
        this.contactSupplier            = params[++i].getValueString ();
        this.customerCompany            = params[++i].getValueString ();
        this.contactCustomer            = params[++i].getValueString ();
        this.deliveryAddressComplete    = params[++i].getValueString ();
        this.paymentAddressComplete     = params[++i].getValueString ();
        this.supplierAddressComplete    = params[++i].getValueString ();
        this.notPossibleDescription     = params[++i].getValueString ();
        this.deliveryDescription        = params[++i].getValueString ();
        this.shippmentDescription       = params[++i].getValueString ();
        this.deliveryDate               = params[++i].getValueDate ();
        this.orderResponsibleOid        = SQLHelpers.getSpOidParam (params[++i]);
        this.p_paymentTypeOid           = SQLHelpers.getSpOidParam (params[++i]);
        this.p_paymentTypeIdentifier    = params[++i].getValueInteger ();
        this.p_paymentTypeName          = params[++i].getValueString ();
        this.p_creditCardOwner          = params[++i].getValueString ();
        this.p_creditCardNumber         = params[++i].getValueString ();
        this.p_creditCardExpiryMonth    = params[++i].getValueString ();
        this.p_creditCardExpiryYear     = params[++i].getValueString ();
        this.eMailOfOrderResponsible    = params[++i].getValueString ();
        this.eMailOfCurrentUser         = params[++i].getValueString ();
        this.catalogOid                 = SQLHelpers.getSpOidParam (params[++i]);
    } // getSpecificRetrieveParameters


    /**************************************************************************
     * Retrieve the type specific data that is not got from the stored
     * procedure. <BR/>
     * This method must be overwritten by all subclasses that have to get type
     * specific data that cannot be got from the retrieve data stored procedure.
     *
     * @param   action  SQLAction for Databaseoperation.
     *
     * @throws  DBError
     *          This exception is always thrown, if there happens an error
     *          during accessing data.
     */
    protected void performRetrieveSpecificData (SQLAction action) throws DBError
    {
        try
        {
            // get the products of the order out of the database
            this.performRetrieveOrderPositions (action);

            this.retrieveProcessState ();
        } // try
        catch (NoAccessException e)
        {
            // get all errors (can be chained)
            String allErrors = new String ("");
            String h = new String (e.getMessage ());
            h += e.getError ();
            while (h != null)
            {
                allErrors += h;
                h = e.getError ();
            } // while
            // show the message
            IOHelpers.showMessage (allErrors,
                this.app, this.sess, this.env);
        } // catch
    } // performRetrieveSpecificData


    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     * The <A HREF="ibs.bo.BusinessObject.html#env">env</A> property is used
     * for getting the parameters. This property must be set before calling
     * this method. <BR/>
     */
    public void getParameters ()
    {
        // special Order_01 parameters
        OID oid = null;
        String str = "";
        int intval = 0;
        Date date = null;

        // get parameters relevant for super class:
        super.getParameters ();

        // get the catalog oid for which to generate the order
        if ((oid = this.env.getOidParam (StoreArguments.ARG_CATALOGOID)) != null)
        {
            this.catalogOid = this.env.getOidParam (StoreArguments.ARG_CATALOGOID);
        } // if

        if ((oid = this.env.getOidParam (StoreArguments.ARG_CAT_ORDRESP)) != null)
        {
            this.orderResponsibleOid = this.env.getOidParam (StoreArguments.ARG_CAT_ORDRESP);
        } // if
        if ((oid = this.env.getOidParam (StoreArguments.ARG_CAT_ORDRESPMED)) != null)
        {
            this.orderResponsibleMediaOid = this.env.getOidParam (StoreArguments.ARG_CAT_ORDRESPMED);
        } // if

        if ((str = this.env.getStringParam (StoreArguments.ARG_EMAIL_ORDERRESP)) != null)
        {
            this.eMailOfOrderResponsible = str;
        } // if
        if ((str = this.env.getStringParam (StoreArguments.ARG_EMAIL_CURRENTUSER)) != null)
        {
            this.eMailOfCurrentUser = str;
        } // if

        if ((str = this.env.getStringParam (StoreArguments.ARG_VOUCHERNO)) != null)
        {
            this.voucherNo = str;
        } // if

        if ((date = this.env.getDateParam (StoreArguments.ARG_DELIVERYDATE)) != null)
        {
            this.deliveryDate = date;
        } // if
        if ((str = this.env.getStringParam (StoreArguments.ARG_SUPPLIERCOMPANY)) != null)
        {
            this.supplierCompany = str;
        } // if
        if ((str = this.env.getStringParam (StoreArguments.ARG_CONTACTSUPPLIER)) != null)
        {
            this.contactSupplier = str;
        } // if
        if ((str = this.env.getStringParam (StoreArguments.ARG_CUSTOMERCOMPANY)) != null)
        {
            this.customerCompany = str;
        } // if
        if ((str = this.env.getStringParam (StoreArguments.ARG_CONTACTCUSTOMER)) != null)
        {
            this.contactCustomer = str;
        } // uf
        if ((str = this.env.getStringParam (StoreArguments.ARG_DELIVERYADDRESS)) != null)
        {
            this.deliveryAddressComplete = str;
        } // if
        if ((str = this.env.getStringParam (StoreArguments.ARG_PAYMENTADDRESS)) != null)
        {
            this.paymentAddressComplete = str;
        } // if
        if ((str = this.env.getStringParam (StoreArguments.ARG_NOTAVAILABLE_CHOICE)) != null)
        {
            this.notPossibleDescription = str;
        } // if
        if ((str = this.env.getStringParam (StoreArguments.ARG_SHIPPMENT_CHOICE)) != null)
        {
            this.shippmentDescription = str;
        } // if
        if ((str = this.env.getStringParam (StoreArguments.ARG_DELIVERY_DESCRIPTION)) != null)
        {
            this.deliveryDescription = str;
        } // if

        if ((intval = this.env.getBoolParam (StoreArguments.ARG_SENDORDER)) >=
            IOConstants.BOOLPARAM_FALSE)
        {
            this.sendOrder = intval == IOConstants.BOOLPARAM_TRUE;
        } // if
        if ((intval = this.env.getIntParam (BOArguments.ARG_ORDERSTATE)) !=
            IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            this.processState = intval;
        } // if

        // available payment types
        if (this.env.getIntParam (StoreArguments.ARG_PAYMENT_TYPES_AVAILABLE) > 0)
        {
            // payment types
            if ((oid = this.env.getOidParam (StoreArguments.ARG_PAYMENT_TYPE)) != null)
            {
                this.p_paymentTypeOid = oid;
                this.p_paymentTypeName = "";

                // creditcard number
                if ((str = this.env
                    .getStringParam (StoreArguments.ARG_CREDITCARD_NUMBER)) != null)
                {
                    this.p_creditCardNumber = str;
                } // if
                // outlet date for the creditcard month
                if ((str = this.env
                    .getStringParam (StoreArguments.ARG_CREDITCARD_EXPIRYMONTH)) != null)
                {
                    this.p_creditCardExpiryMonth = str;
                } // if
                // outlet date for the creditcard year
                if ((str = this.env
                    .getStringParam (StoreArguments.ARG_CREDITCARD_EXPIRYYEAR)) != null)
                {
                    this.p_creditCardExpiryYear = str;
                } // if
                // name of the creditcard owner
                if ((str = this.env
                    .getStringParam (StoreArguments.ARG_CREDITCARD_OWNER)) != null)
                {
                    this.p_creditCardOwner = str;
                } // if
            } // if payment types
        } // if available payment types
    } // getParameters


   /***************************************************************************
    * Show the selection box if products in shopping cart are from more than one
    * catalog. <BR/>
    *
    * @return   Flag which indicates if there a products from more than one
    *           catalog.
    */
    public boolean showCatalogSelection ()
    {
        Page page = new Page (
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_ORDER, env), false);

        this.insertChangeFormStyles (page);

        // AJ:  BUG -  what happens if catalog was deleted ??

        String queryStr =
            " SELECT DISTINCT oid, name " +
            " FROM v_ShoppingCart$catalogs " +
            " WHERE  userId = " + this.user.id +
            " ORDER BY name ASC";

        // get all the selection lists from the database
//debug (queryStr);
        SelectionList catalogsSelectionList =
            this.performRetrieveSelectionListDataQuery (false, queryStr);

        // don't do anything if only product from one catalog
        if (catalogsSelectionList.ids[0].equals (OID.EMPTYOID))
        {
            IOHelpers.showMessage (
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_CART_NOENTRIES, env),
                this.app, this.sess, this.env);
            return false;
        } // if
        else if (catalogsSelectionList.ids.length == 1)
        {
            try
            {
                this.catalogOid = new OID (catalogsSelectionList.ids[0]);
            } // try
            catch (IncorrectOidException e)
            {
                this.catalogOid = null;
            } // catch IncorrectOidException
            return true;
        } // else if

        FormElement form = this.createFormHeader (page, this.name,
            this.getNavItems (), null, 
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_SELECT_CATALOG, env),
            HtmlConstants.FRM_SHEET, Order_01.CLASS_ICON, this.containerName);
        TableElement table = this.createFrame (0, 0);

        table.classId = CssConstants.CLASS_INFO;
        String[] classIds = new String[2];
        classIds[0] = CssConstants.CLASS_NAME;
        classIds[1] = CssConstants.CLASS_VALUE;
        table.classIds = classIds;

        table.border = 0;

        // start with the object representation: show header
        form.addElement (new InputElement (BOArguments.ARG_FUNCTION,
                                           InputElement.INP_HIDDEN,
                                           "" + StoreFunctions.FCT_ORDER_FORM2));

        // (index of oid: 0)
        this.showFormProperty (table, StoreArguments.ARG_CATALOGOID,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_CATALOGOID, env), Datatypes.DT_SELECT, "",
            catalogsSelectionList.ids, catalogsSelectionList.values, 0);
        // add the table
        form.addElement (table);
        // set ok action to
        this.createFormFooter (form, null, IOHelpers.getShowObjectJavaScript (
            this.getUserInfo ().workspace.shoppingCart.toString ()));
        // build the page and show it to the user:
        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
        return false;
    } // showCatalogSelection


    /**************************************************************************
     * Insert style sheet information in a standard change form. <BR/>
     *
     * @param   page    The page containing the change form.
     */
    protected void insertChangeFormStyles (Page page)
    {
        // Stylesheetfile wird geladen
        StyleSheetElement style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () + "/" +
            this.sess.activeLayout.elems[LayoutConstants.SHEETINFO].styleSheet;
        page.head.addElement (style);
        // Stylesheetfile wird geladen
        style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () + "/" +
            this.sess.activeLayout.elems[LayoutConstants.PRODUCTCATALOG].styleSheet;
        page.head.addElement (style);
    } // insertChangeFormStyles


    /**************************************************************************
     * Insert style sheet information in a standard info view . <BR/>
     *
     * @param   page    The page containing the info view.
     */
    protected void insertInfoStyles (Page page)
    {
        // Stylesheetfile wird geladen
        StyleSheetElement style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () + "/" +
            this.sess.activeLayout.elems[LayoutConstants.SHEETINFO].styleSheet;
        page.head.addElement (style);
        // Stylesheetfile wird geladen
        style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () + "/" +
            this.sess.activeLayout.elems[LayoutConstants.PRODUCTCATALOG].styleSheet;
        page.head.addElement (style);
    } // insertInfoStyles


   /***************************************************************************
    * Show an order form for the shopping cart. <BR/>
    *
    * @see ibs.IbsObject#showProperty (ibs.tech.html.TableElement, java.lang.String, java.lang.String, int, ibs.service.user.User, java.util.Date)
    */
    public void showOrder ()
    {
        // get the catalog out of the database
        try
        {
            Page page;
            int positions = this.performRetrievePositions ();
            if (positions == 0)
            {
                // show some alert like 'keine Einträge im Warenkorb vorhanden'
                // create the answering page with the alert message
                page = new Page (
                    MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                        StoreTokens.ML_PUTIN_CART, env), false);
                ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);
                script.addScript (
                        "alert (\"" +  
                        MultilingualTextProvider.getMessage (StoreMessages.MSG_BUNDLE,
                            StoreMessages.ML_MSG_NOSHOPPINGCARTENTRIES, env) + "\");\n");
                script.addScript (
                    "top.goback (0);\n");
                page.body.addElement (script);
            } // if (positions == 0)
            else        // positions exist
            {
                // do the other things in this method !!!
                // retrieve the address of the user
                this.performRetrieveOrderDefaults ();

                // check if there is someone to send the order to
                if (this.orderResponsibleOid == null)
                {
                    IOHelpers.showMessage (
                        MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                            StoreTokens.ML_NO_ORDERRESP, env),
                        this.app, this.sess, this.env);
                    this.showShoppingCart ();
                    return;
                } // if (this.orderResponsibleOid == null)

                page = new Page (
                    MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                        StoreTokens.ML_ORDER, env), false);

                // Stylesheetfile wird geladen
                StyleSheetElement style = new StyleSheetElement ();
                style.importSS = this.sess.activeLayout.path + this.env.getBrowser () + "/" +
                    this.sess.activeLayout.elems[LayoutConstants.SHEETINFO].styleSheet;
                page.head.addElement (style);
                // Stylesheetfile wird geladen
                style = new StyleSheetElement ();
                style.importSS = this.sess.activeLayout.path + this.env.getBrowser () + "/" +
                    this.sess.activeLayout.elems[LayoutConstants.PRODUCTCATALOG].styleSheet;
                page.head.addElement (style);

                FormElement form = this.createFormHeader (page, this.name,
                    this.getNavItems (), null, null, HtmlConstants.FRM_SHEET,
                    Order_01.CLASS_ICON, this.containerName);

                TableElement table = this.createFrame (0, 0);
                table.border = 0;
                table.classId = CssConstants.CLASS_INFO;
                String[] classIds = new String[2];
                classIds[0] = CssConstants.CLASS_NAME;
                classIds[1] = CssConstants.CLASS_VALUE;
                table.classIds = classIds;

                // start with the object representation: show header
                form.addElement (new InputElement (BOArguments.ARG_FUNCTION,
                                                   InputElement.INP_HIDDEN,
                                                    "" + StoreFunctions.FCT_STORE_ORDER));

                // loop through the properties
                this.showFormProperties (table);
                form.addElement (table);

                // set ok action
                this
                    .createFormFooter (
                        form,
                        null,
                        IOHelpers
                            .getShowObjectJavaScript (this.getUserInfo ().workspace.shoppingCart
                                .toString ()));
            } // else positions exist

            // build the page and show it to the user:
            try
            {
                page.build (this.env);
            } // try
            catch (BuildException e)
            {
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            } // catch
        } // try
        catch (NoAccessException e)
        {
            // no access to upper object -> just show a message:
            IOHelpers.showMessage (MultilingualTextProvider
                .getMessage(BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTNOTFOUND, this.env),
                this.app, this.sess, this.env);
        } // catch
    } // showOrder


    /**************************************************************************
     * Forward the order to the user who is
     * responsible. <BR/>
     */
/*
    private void performSendOrder ()
    {
        Vector receivers = new Vector ();
        Vector receiversMedia = new Vector ();
        // get the catalog out of the database
        receivers.addElement ("" + this.orderResponsibleOid);
        receiversMedia.addElement ("" + this.orderResponsibleMediaOid);  // may be null, then default-medium

        Application app = new Application ();
        if (app == null)
        {
            showMessage (BOMessages.MSG_OBJECTNOTCREATED);
            return;
        } // if
        app.setEnv (this.env);          // set environment
        app.sess = this.sess;           // set session object
        app.user = this.user;           // set user

        try
        {
            // try to get the session object:
            app.getSessionObject ();
        } // try
        catch (Exception e)
        {
            // create a new session:
            app.createSessionObject ();
            try
            {
                // try to get the session object:
                app.getSessionObject ();
            } // try
            catch (Exception e1)
            {
            } // catch
        } // catch


        //app.objectSend (this.oid, receivers, receiversMedia.elements (), this.name, this.description, "")

        app.objectDistribute (
            this.oid                        // object to distribute
            , receivers.elements ()          // receiver enumeration
            , "" + this.name                // name
            , "" + this.description         // description
            , ""                            // activities
            , false);
    } // performSendOrder
*/


    /**************************************************************************
     * Represent the properties of a Order_01 object to the user. <BR/>
     *
     * @param   table   Table where the properties shall be added.
     *
     * @see ibs.IbsObject#showProperty (ibs.tech.html.TableElement, java.lang.String, java.lang.String, int, ibs.service.user.User, java.util.Date)
     */
    protected void showProperties (TableElement table)
    {
        // state of order
        String stateString;

        int index = StringHelpers.findString (States.PST_STATEIDS, Integer.toString (this.processState));

        if (index != -1)
        {
            stateString = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, States.PST_STATENAMES [index], env);
        } // if
        else
        {
            stateString = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_PST_NONE, env);
        } // else

        this.showProperty (table, BOArguments.ARG_ORDERSTATE,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_ORDERSTATE, env), 
            Datatypes.DT_TEXT, stateString);

        // company of supplier
        this.showProperty (table, StoreArguments.ARG_SUPPLIERCOMPANY,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_SUPPLIERCOMPANY, env), Datatypes.DT_TEXT,
            this.supplierCompany + IE302.TAG_NEWLINE + IE302.TAG_NEWLINE +
                this.contactSupplier);
        this.showProperty (table, StoreArguments.ARG_CUSTOMERCOMPANY,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_CUSTOMERCOMPANY, env), Datatypes.DT_TEXT,
            this.customerCompany + IE302.TAG_NEWLINE + IE302.TAG_NEWLINE +
                this.contactCustomer);
        this.showProperty (table, BOArguments.ARG_NAME,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_ORDERNAME, env), Datatypes.DT_NAME, this.name);
        this.showProperty (table, StoreArguments.ARG_VOUCHERNO,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_VOUCHERNO, env), Datatypes.DT_TEXT, this.voucherNo);

        if (this.voucherDate != null)
        {
            this.showProperty (table, StoreArguments.ARG_VOUCHERDATE,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_VOUCHERDATE, env),
                Datatypes.DT_DATE, this.voucherDate);
        } // if

        // address information for delivery
        this.showProperty (table, BOArguments.ARG_NOARG,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_DELIVERYADDRESS, env),
            Datatypes.DT_TEXT, this.deliveryAddressComplete);
        // address info for payment
        this.showProperty (table, BOArguments.ARG_NOARG,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_PAYMENTADDRESS, env),
            Datatypes.DT_TEXT, this.paymentAddressComplete);
        // show the positions of the order
        this.showPositions (table, true, false);

        //show the description of catalog when delivery not possible
        this.showProperty (table, StoreArguments.ARG_NOTAVAILABLE_CHOICE,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_NOTAVAILABLE_1, env),
            Datatypes.DT_NAME, this.notPossibleDescription);

        this.showProperty (table, StoreArguments.ARG_SHIPPMENT_CHOICE,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_SHIPPMENT_CHOICE, env),
            Datatypes.DT_NAME, this.shippmentDescription);

        // show the description of catalog when delivery not possible
/*        showProperty (table
                            ,ARG_NOTAVAILABLE_CHOICE
                            ,TOK_NOTAVAILABLE_1
                            ,Datatypes.DT_NAME
                            ,this.notPossibleDescription);
        // show the description of catalog for delivery
        showProperty (table
                            ,ARG_SHIPPMENT_CHOICE
                            ,TOK_SHIPPMENT_1
                            ,Datatypes.DT_NAME
                            ,this.shippmentDescription);
*/
        // deliveryDate

        this.showProperty (table, StoreArguments.ARG_DELIVERY_DESCRIPTION,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_DELIVERY_DESCRIPTION, env),
            Datatypes.DT_NAME, this.deliveryDescription);

        this.showProperty (table, StoreArguments.ARG_DELIVERYDATE,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_DELIVERYDATE, env),
            Datatypes.DT_DATE, this.deliveryDate);

        // if there is an payment selected
        if (this.p_paymentTypeName != null)
        {
            this.showProperty (table, StoreArguments.ARG_CREDITCARD_NAME,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_PAYMENT_MEANS, env),
                Datatypes.DT_TEXT, this.p_paymentTypeName);
        } // if there is an payment selected

        // if payment type is credit card
        // and is the user owner or order responsible
        if (this.p_paymentTypeIdentifier > 0 &&
            (this.user.oid.equals (this.orderResponsibleOid) || this.user.id == this.owner.id))
        {
            this.showProperty (table, StoreArguments.ARG_CREDITCARD_NUMBER,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_CREDITCARD_NUMBER, env),
                Datatypes.DT_NUMBER, this.p_creditCardNumber);

            this.showProperty (table,
                StoreArguments.ARG_CREDITCARD_EXPIRYMONTH,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_CREDITCARD_EXPIRYMONTH, env),
                Datatypes.DT_TEXT, this.p_creditCardExpiryMonth);

            this.showProperty (table, StoreArguments.ARG_CREDITCARD_EXPIRYYEAR,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_CREDITCARD_EXPIRYYEAR, env),
                Datatypes.DT_TEXT, this.p_creditCardExpiryYear);

            this.showProperty (table, StoreArguments.ARG_CREDITCARD_OWNER,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_CREDITCARD_OWNER, env),
                Datatypes.DT_TEXT, this.p_creditCardOwner);
        } // if payment type is credit card
    } // showProperties


    /**************************************************************************
     * Retrives all payment types for the catalog from which this order is. <BR/>
     *
     * @return  This selection list contains all payment types
     *          which are on the database.
     */
    protected SelectionList getCatalogPaymentTypesData ()
    {
        SQLAction action = null;        // the action object used to access the
                                        // database
        Vector<String> ids = new Vector<String> (10, 10); // initialize elements vector
        Vector<String> values = new Vector<String> (10, 10); // initialize elements vector
        SelectionList paymentTypes = new SelectionList ();

        // open db connection -  only workaround - db connection must
        // be handled somewhere else
        action = this.getDBConnection ();

        // create the SQL String to select the content of a entry
        String queryStr =
            "SELECT cp.paymentOid, o.name" +
            " FROM  m2_CatalogPayments cp, ibs_Object o" +
            " WHERE o.oid = cp.paymentOid" +
            "   AND cp.catalogOid = " + this.catalogOid.toStringQu () +
            "   AND o.state = 2";

//debug (queryStr);

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            int rowCount = action.execute (queryStr, false);
            // no valid tuples
            if (rowCount == 0 || rowCount < 0)
            {
                return null;
            } // if
            // select the paymenttypes for the SelectionList
            while (!action.getEOF ())
            {
                // get the parameters out of the database into the
                // SelectionList paymentTypes
                ids.addElement (action.getString ("paymentOid"));
                values.addElement (action.getString ("name"));

                // step one tuple ahead for the next loop
                action.next ();
            } // while select the paymenttypes for the SelectionList

            // fills a selection list
            paymentTypes.ids = new String[ids.size ()];
            paymentTypes.values = new String[values.size ()];
            ids.copyInto (paymentTypes.ids);
            values.copyInto (paymentTypes.values);

            // the last tuple has been processed
            // end transaction
            action.end ();
        } // try
        catch (DBError dbErr)
        {
            this.env.write (queryStr);
            // an error occurred - show name and info
            IOHelpers.showMessage (dbErr, this.app, this.sess, this.env, false);
        } // catch
        finally
        {
            // close db connection in every case - only workaround -
            // db connection must be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        return paymentTypes;
    } // getCatalogPaymentTypesData


    /**************************************************************************
     * Represent the properties of a Order_01 object to the user
     * within a form. <BR/>
     *
     * @param   table   Table where the properties shall be added.
     *
     * @see ibs.IbsObject#showFormProperty (ibs.tech.html.TableElement, java.lang.String, java.lang.String, int, ibs.service.user.User)
     */
    protected void showFormProperties (TableElement table)
    {
        SQLAction action = null;        // the action object used to access the
                                        // database

        SelectionList allCatalogPayments = null;

        String[] expiryYearValues =
        {
            "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008",
            "2009", "2010", "2011", "2012", "2013", "2014", "2015", "2016",
        };
        int howManyPayments = 0;    // counter for available payments

        // get catalog payments
        // all catalog payments are only needed if the normal form is shown
        if (!this.changeStateForm)
        {
            allCatalogPayments = this.getCatalogPaymentTypesData ();
        } // if

        // change only processState
        if (this.changeStateForm)
        {
            // show selection list for state of order
            String [] stateArray = new String []
            {
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_PST_EXECUTING, env),
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_PST_REJECTED, env),
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_PST_DELIVERED, env),
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_PST_OPEN, env),
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_PST_COMPLETED, env),
            }; // stateArray

            String [] idArray = new String []
            {
                Integer.toString (States.PST_EXECUTING),
                Integer.toString (States.PST_REJECTED),
                Integer.toString (States.PST_DELIVERED),
                Integer.toString (States.PST_OPEN),
                Integer.toString (States.PST_COMPLETED),
            }; // idArray

            int index = StringHelpers.findString (idArray, Integer
                .toString (this.processState));

            // processState is not valid in this selectionbox
            if (index == -1)
            {
                // set processState of order to state open
                this.processState = States.PST_OPEN;
                index = StringHelpers.findString (idArray, Integer
                    .toString (this.processState));
            } // if processState is not valid in this selectionbox

            this.showFormProperty (table, BOArguments.ARG_ORDERSTATE,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_ORDERSTATE, env), Datatypes.DT_SELECT, Integer
                    .toString (this.processState), idArray, stateArray, index);
        } // if change only processstate

        this.showProperty (table, StoreArguments.ARG_SUPPLIERCOMPANY,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_SUPPLIERCOMPANY, env),
            Datatypes.DT_TEXT, this.supplierCompany);
        this.showProperty (table, StoreArguments.ARG_CONTACTSUPPLIER,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_CONTACTSUPPLIER, env), 
            Datatypes.DT_TEXT, this.contactSupplier);
        this.showProperty (table, StoreArguments.ARG_CUSTOMERCOMPANY,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_CUSTOMERCOMPANY, env),
            Datatypes.DT_TEXT, this.customerCompany);
        this.showProperty (table, StoreArguments.ARG_CONTACTCUSTOMER,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_CONTACTCUSTOMER, env),
            Datatypes.DT_TEXT, this.contactCustomer);
        // change only processState
        if (this.changeStateForm)
        {
            this.showProperty (table, BOArguments.ARG_NAME,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_ORDERNAME, env),
                Datatypes.DT_NAME, this.name);
            this.showProperty (table, StoreArguments.ARG_VOUCHERNO,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_VOUCHERNO, env),
                Datatypes.DT_TEXT, this.voucherNo);
            this.showProperty (table, StoreArguments.ARG_VOUCHERDATE,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_VOUCHERDATE, env),
                Datatypes.DT_DATE, this.voucherDate);
            // address information for delivery
            this.showProperty (table, BOArguments.ARG_NOARG,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_DELIVERYADDRESS, env),
                Datatypes.DT_TEXT, this.deliveryAddressComplete);
            // address info for payment
            this.showProperty (table, BOArguments.ARG_NOARG,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_PAYMENTADDRESS, env),
                Datatypes.DT_TEXT, this.paymentAddressComplete);
            // show the positions of the order
            this.showPositions (table, true, false);
            this.showProperty (table, StoreArguments.ARG_DELIVERY_DESCRIPTION,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_DELIVERY_DESCRIPTION, env),
                Datatypes.DT_NAME, this.deliveryDescription);

            // show date of delivery
            this.showProperty (table, StoreArguments.ARG_DELIVERYDATE,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_DELIVERYDATE, env),
                Datatypes.DT_DATE, this.deliveryDate);

            // if there is a payment type
            if (this.p_paymentTypeName != null)
            {
                this.showProperty (table, StoreArguments.ARG_CREDITCARD_NAME,
                    MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                        StoreTokens.ML_PAYMENT_MEANS, env),
                    Datatypes.DT_TEXT, this.p_paymentTypeName);
            } // if there is a payment type

            // check if payment type is credit card
            if (this.p_paymentTypeIdentifier > 0)
            {
                this.showProperty (table, StoreArguments.ARG_CREDITCARD_NUMBER,
                    MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                        StoreTokens.ML_CREDITCARD_NUMBER, env),
                    Datatypes.DT_NUMBER, this.p_creditCardNumber);

                this.showProperty (table,
                    StoreArguments.ARG_CREDITCARD_EXPIRYMONTH,
                    MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                        StoreTokens.ML_CREDITCARD_EXPIRYMONTH, env),
                    Datatypes.DT_TEXT, this.p_creditCardExpiryMonth);

                this.showProperty (table,
                    StoreArguments.ARG_CREDITCARD_EXPIRYYEAR,
                    MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                        StoreTokens.ML_CREDITCARD_EXPIRYYEAR, env),
                    Datatypes.DT_TEXT, this.p_creditCardExpiryYear);

                this.showProperty (table, StoreArguments.ARG_CREDITCARD_OWNER,
                    MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                        StoreTokens.ML_CREDITCARD_OWNER, env),
                    Datatypes.DT_TEXT, this.p_creditCardOwner);
            } // if payment type is credit card
        } // if change only processState
        else
        {
            // change everything except state
            this.showFormProperty (table, BOArguments.ARG_NAME,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_ORDERNAME, env),
                Datatypes.DT_NAME, this.name + " - " + this.supplierCompany);

            this.showFormProperty (table, StoreArguments.ARG_VOUCHERNO,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_VOUCHERNO, env),
                Datatypes.DT_TEXT, "");

            this.showFormProperty (table, StoreArguments.ARG_DELIVERYADDRESS,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_DELIVERYADDRESS, env),
                Datatypes.DT_DESCRIPTION,
                this.deliveryName +
                    "\n" +
                    this.deliveryAddress +
                    "\n" +
                    ((this.deliveryCountry == null) ? "" :
                        (this.deliveryCountry + " ")) + this.deliveryZIP +
                    " " + this.deliveryTown);
            this.showFormProperty (table, StoreArguments.ARG_PAYMENTADDRESS,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_PAYMENTADDRESS, env),
                Datatypes.DT_DESCRIPTION,
                this.paymentName +
                    "\n" +
                    this.paymentAddress +
                    "\n" +
                    ((this.paymentCountry == null) ? "" :
                        (this.paymentCountry + " ")) + this.paymentZIP + " " +
                    this.paymentTown + "\n");

            this.showPositions (table, false, false);

// Properties delivery description and not available choice
            // get delivery description and not available-choice from catalog out
            // of db
            String notavailable = "";
            String shippmentMethod = "";
            String str;
            String delimiter = AppConstants.WC_CRLF;
            int i = 0;
            String [] shippmentMethodArray;
            String [] notavailableArray;


            String queryStr = " SELECT description1, description2" +
                              " FROM m2_Catalog_01" +
                              " WHERE oid =" + this.catalogOid.toStringQu ();

            try
            {
                // open db connection - only workaround - db connection must
                // be handled somewhere else
                action = this.getDBConnection ();

                action.execute (queryStr, false);

                // get tuple out of db
                if (!action.getEOF ())
                {
                    // try to read  out of tuple
                    if ((str = action.getString ("description2")) != null)
                    {
                        shippmentMethod = str;
                    } // if

                    // try to read out of tuple
                    if ((str = action.getString ("description1")) != null)
                    {
                        notavailable = str;
                    } // if
                } // if
            } // try
            catch (DBError e)
            {
                // an error occurred - show name and info
                IOHelpers.showMessage (e, this.app, this.sess, this.env, false);
            } // catch
            finally
            {
                // close db connection in every case - only workaround - db
                // connection must be handled somewhere else
                this.releaseDBConnection (action);
            } // finally


            // PROPERTY: notPossibleDescription   (description1);
            StringTokenizer stringTok = new StringTokenizer (notavailable,
                delimiter);

             // got one tuple with not available modes - show selection list
            if (stringTok.countTokens () > 1 && !(" ".equals (notavailable)))
            {
                notavailableArray = new String [stringTok.countTokens ()];
                i = 0;
                try
                {
                    while (true)
                        // get Tokens
                    {
                        notavailableArray [i++] = stringTok.nextToken ();
                    } // while
                } // try
                catch (NoSuchElementException e)
                {
                    // TODO: handle the exception
                } // catch

                // show property not available choice as selection list
                this.showFormProperty (table,
                    StoreArguments.ARG_NOTAVAILABLE_CHOICE,
                    MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                        StoreTokens.ML_NOTAVAILABLE_CHOICE, env),
                    Datatypes.DT_SELECT, this.notPossibleDescription, 
                    notavailableArray, notavailableArray, 0);
            } // if got one tuple with not available modes - show selection list
            else
            {   // no delivery modes in catalog - show edit field
                this.showFormProperty (table,
                    StoreArguments.ARG_NOTAVAILABLE_CHOICE,
                    MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                        StoreTokens.ML_NOTAVAILABLE_CHOICE, env),
                    Datatypes.DT_TEXT, "" + this.notPossibleDescription);
            } // else

            // PROPERTY: shipment method (description2)
            stringTok = new StringTokenizer (shippmentMethod, delimiter);

            // got one tuple with delivery modes - show selection list
            if (stringTok.countTokens () > 1 && !(" ".equals (shippmentMethod)))
            {
                shippmentMethodArray = new String [stringTok.countTokens ()];
                i = 0;

                try
                {
                    // get Tokens
                    while (true)
                    {
                        shippmentMethodArray [i++] = stringTok.nextToken ();
                    } // get Tokens
                } // try
                catch (NoSuchElementException e)
                {
                    // TODO: handle the exception
                } // catch
                // show property deliverydescription as selectionlist
                this.showFormProperty (table, StoreArguments.ARG_SHIPPMENT_CHOICE,
                    MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                        StoreTokens.ML_SHIPPMENT_CHOICE, env),
                    Datatypes.DT_SELECT, this.shippmentDescription, 
                    shippmentMethodArray, shippmentMethodArray, 0);
            } // if got one tupl with deliverymodes - show selectionlist
            else
            {
                // no deliverymodes in catalog - show edit field
                this.showFormProperty (table,
                    StoreArguments.ARG_SHIPPMENT_CHOICE,
                    MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                        StoreTokens.ML_SHIPPMENT_CHOICE, env),
                    Datatypes.DT_TEXT, "" + this.shippmentDescription);
            } // else

            this.showFormProperty (table,
                StoreArguments.ARG_DELIVERY_DESCRIPTION,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_DELIVERY_DESCRIPTION, env),
                Datatypes.DT_DESCRIPTION, "");

/*
 * not needed anymore //show the description of catalog when delivery not
 * possible showProperty (table , StoreArguments.ARG_NOARG ,
 * StoreTokens.TOK_NOTAVAILABLE_1 , Datatypes.DT_DESCRIPTION ,
 * this.notPossibleDescription); showFormProperty (table ,
 * StoreArguments.ARG_NOTAVAILABLE_CHOICE , StoreTokens.TOK_NOTAVAILABLE_CHOICE ,
 * Datatypes.DT_NAME , ""); // show the description of catalog for delivery
 * showProperty (table , StoreArguments.ARG_NOARG , StoreTokens.TOK_SHIPPMENT_1 ,
 * Datatypes.DT_DESCRIPTION , this.shippmentDescription); showFormProperty
 * (table , StoreArguments.ARG_SHIPPMENT_CHOICE ,
 * StoreTokens.TOK_SHIPPMENT_CHOICE , Datatypes.DT_NAME , "");
 */
            this.showFormProperty (table, StoreArguments.ARG_DELIVERYDATE,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_DELIVERYDATE, env),
                Datatypes.DT_DATE, this.deliveryDate);

            this.showFormProperty (table, StoreArguments.ARG_SENDORDER,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_SENDORDER, env),
                Datatypes.DT_BOOL, "" + this.sendOrder);

            // are there payments selected ?
            if (allCatalogPayments != null &&
                (howManyPayments = allCatalogPayments.ids.length) > 0)
            {
                this.showProperty (table, null, null,
                    ibs.bo.Datatypes.DT_SEPARATOR, (String) null);

                // all for this catalog available payments
                this.showFormProperty (table, StoreArguments.ARG_PAYMENT_TYPE,
                    MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                        StoreTokens.ML_PAYMENT_MEANS, env), Datatypes.DT_SELECT, "",
                    allCatalogPayments.ids, allCatalogPayments.values, 1);
                // credit card number
                this.formFieldRestriction =
                    new FormFieldRestriction (true, 16, 0);
                this.showFormProperty (table,
                    StoreArguments.ARG_CREDITCARD_NUMBER,
                    MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                        StoreTokens.ML_CREDITCARD_NUMBER, env), Datatypes.DT_NUMBER, "");

                // Get the month names with the correct language of the current user 
                String[] monthNames = MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE, DiaryTokens.MONTHS, env);

                // credit card expiry month
                this.showFormProperty (table,
                    StoreArguments.ARG_CREDITCARD_EXPIRYMONTH,
                    MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                        StoreTokens.ML_CREDITCARD_EXPIRYMONTH, env),
                    Datatypes.DT_SELECT, "", DiaryTokens.MONTHS,
                    monthNames, 0, true);
                                        // is true because the array should
                                        // not be sorted

                // credit card expiry year
                this.showFormProperty (table,
                    StoreArguments.ARG_CREDITCARD_EXPIRYYEAR,
                    MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                        StoreTokens.ML_CREDITCARD_EXPIRYYEAR, env), Datatypes.DT_SELECT,
                    "", expiryYearValues, expiryYearValues, 1);

                // credit card owner
                this.formFieldRestriction =
                       new FormFieldRestriction (true, 200, 0);
                this.showFormProperty (table,
                    StoreArguments.ARG_CREDITCARD_OWNER,
                    MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                        StoreTokens.ML_CREDITCARD_OWNER, env),
                    Datatypes.DT_DESCRIPTION, "");
            } // if are there payments selected

            // hidden field for availabel types
            // is false if NO payment types are available
            // is true if AT LEAST ONE payment types are available
            this.showFormProperty (table,
                StoreArguments.ARG_PAYMENT_TYPES_AVAILABLE, "",
                Datatypes.DT_HIDDEN, "" + howManyPayments);
        } // else change everything except state

        this.showFormProperty (table, StoreArguments.ARG_CATALOGOID, "",
            Datatypes.DT_HIDDEN, "" + this.catalogOid);

        this.showFormProperty (table, StoreArguments.ARG_CAT_ORDRESP, "",
            Datatypes.DT_HIDDEN, "" + this.orderResponsibleOid);
        this.showFormProperty (table, StoreArguments.ARG_CAT_ORDRESPMED, "",
            Datatypes.DT_HIDDEN, "" + this.orderResponsibleMediaOid);

        this.showFormProperty (table, StoreArguments.ARG_EMAIL_ORDERRESP, "",
            Datatypes.DT_HIDDEN, "" + this.eMailOfOrderResponsible);
        this.showFormProperty (table, StoreArguments.ARG_EMAIL_CURRENTUSER, "",
            Datatypes.DT_HIDDEN, "" + this.eMailOfCurrentUser);
    } // showFormProperties


    /***************************************************************************
     * Show the order for printing purposes (different representation). <BR/>
     */
    public void showOrderInWindow ()
    {
        try
        {
            // try to retrieve the object:
            this.retrieve (Operations.OP_READ);
            this.performShowOrderForPrint ();
        } // try
        catch (NoAccessException e) // no access to object allowed
        {
            // send message to the user:
            this.showNoAccessMessage (Operations.OP_READ);
        } // catch
        catch (AlreadyDeletedException e)
        {
            // TODO: handle the exception
        } // catch
        catch (ObjectNotFoundException e)
        {
            // TODO: handle the exception
        } // catch
    } // showOrderInWindow


    /**************************************************************************
     * Show the order for printing purposes (different representation). <BR/>
     */
    private void performShowOrderForPrint ()
    {
        Page page = new Page (
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_ORDER, env), false);
        StyleSheetElement style = new StyleSheetElement ();

        TableElement table = this.createFrame (0, 0);
        TableDataElement td;
        table.border = 0;
        RowElement row;
        TextElement text;
        SpanElement spElem;
        LineElement line;
        GroupElement group;


        // set the document's base:
        IOHelpers.setBase (page, this.app, this.sess, this.env);

        // style sheet file is loading:
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () + "/" +
            this.sess.activeLayout.elems[LayoutConstants.PRINTSHEET].styleSheet;
        page.head.addElement (style);

        try                             // try to find ssi file
        {
            // order header - server side include - file "order header.htm"
            text = new TextElement (IOHelpers.getSSIFile (
                StoreConstants.SSI_ORDERHEADER, this.env,
                (Configuration) this.app.configuration));
            page.body.addElement (text);
        } // try to find ssi file
        catch (SsiFileNotFoundException e)
                                        // ssi file was not found
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch ssi file not found

 //  check AJ .....................................
        // add the supplier company
//        text = new TextElement (IE302.TAG_NEWLINE + IE302.TAG_NEWLINE + this.supplierCompany + IE302.TAG_NEWLINE + this.contactSupplier);

        // replace delimiter ; with carrige-return linefeed
        if (this.supplierAddressComplete.length () > 0)
        {
            int index = this.supplierAddressComplete.indexOf (";");
            if (index > -1)
            {
                this.supplierAddressComplete = this.supplierAddressComplete
                    .substring (0, index) +
                    IE302.TAG_NEWLINE +
                    this.supplierAddressComplete.substring (index + 1,
                        this.supplierAddressComplete.length ());
            } // if
        } // if

        text = new TextElement (this.supplierCompany + IE302.TAG_NEWLINE +
            this.supplierAddressComplete + IE302.TAG_NEWLINE +
            this.contactSupplier);


        spElem = new SpanElement ();
        spElem.addElement (text);
        spElem.classId = CssConstants.CLASS_VALUE;
        page.body.addElement (spElem);

        // line
        line = new LineElement (3, HtmlConstants.TAV_FULLWIDTH);
        page.body.addElement (line);


        // add the payment and delivery address
        TableElement addresses = this.createFrame (0, 0);
        table.cellspacing = 20;
        addresses.border = 0;
        row = new RowElement (2);

        //delivery address
        group = new GroupElement ();
        row = new RowElement (2);
        text = new TextElement (
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_DELIVERYADDRESS, env)
            + ":" + IE302.TAG_NEWLINE);

        spElem = new SpanElement ();
        spElem.addElement (text);
        spElem.classId = CssConstants.CLASS_NAME;
        group.addElement (spElem);

        group.addElement (IOHelpers.getTextField (this.deliveryAddressComplete +
            IE302.TAG_NEWLINE + this.contactCustomer));


        td = new TableDataElement (group);
        row.addElement (td);

        // payment address
        group = new GroupElement ();
        text = new TextElement (
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_PAYMENTADDRESS, env)
            + ":" + IE302.TAG_NEWLINE);

        spElem = new SpanElement ();
        spElem.addElement (text);
        spElem.classId = CssConstants.CLASS_NAME;
        group.addElement (spElem);

        group.addElement (IOHelpers.getTextField (this.paymentAddressComplete +
            IE302.TAG_NEWLINE + this.contactCustomer));

        td = new TableDataElement (group);
        row.addElement (td);
        addresses.addElement (row);
        page.body.addElement (addresses);

        // order token
        text = new TextElement (IE302.TAG_NEWLINE + this.name);

        spElem = new SpanElement ();
        spElem.addElement (text);
        spElem.classId = CssConstants.CLASS_HEADER;
        page.body.addElement (spElem);

        //voucherNo
        ParagraphElement pe = new ParagraphElement ();
        pe.alignment = IOConstants.ALIGN_RIGHT;

        text = new TextElement (
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_VOUCHERNO, env) + ": ");

        spElem = new SpanElement ();
        spElem.addElement (text);
        spElem.classId = CssConstants.CLASS_NAME;
        pe.addElement (text);
        // page.body.addElement (spElem);
        text = new TextElement ("" + this.voucherNo + IE302.TAG_NEWLINE +
            IE302.TAG_NEWLINE);

        spElem = new SpanElement ();
        spElem.addElement (text);
        spElem.classId = CssConstants.CLASS_VALUE;
        pe.addElement (text);
//        page.body.addElement (spElem);

        page.body.addElement (pe);

        // orderPositions
        this.showPositions (table, false, true);
        page.body.addElement (table);

        // delivery description
        text = new TextElement (IE302.TAG_NEWLINE +
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_DELIVERY_DESCRIPTION, env) + ": ");

        spElem = new SpanElement ();
        spElem.addElement (text);
        spElem.classId = CssConstants.CLASS_NAME;
        page.body.addElement (spElem);

        text = new TextElement ("" + this.deliveryDescription);

        spElem = new SpanElement ();
        spElem.addElement (text);
        spElem.classId = CssConstants.CLASS_VALUE;
        page.body.addElement (spElem);

        // choice of product not in stock
        text = new TextElement (IE302.TAG_NEWLINE + IE302.TAG_NEWLINE +
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_NOTAVAILABLE_CHOICE, env) + ": ");

        spElem = new SpanElement ();
        spElem.addElement (text);
        spElem.classId = CssConstants.CLASS_NAME;
        page.body.addElement (spElem);

        text = new TextElement ("" + this.notPossibleDescription);

        spElem = new SpanElement ();
        spElem.addElement (text);
        spElem.classId = CssConstants.CLASS_VALUE;
        page.body.addElement (spElem);

        // delivery date
        text = new TextElement (IE302.TAG_NEWLINE + 
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_DELIVERYDATE, env) + ": ");

        spElem = new SpanElement ();
        spElem.addElement (text);
        spElem.classId = CssConstants.CLASS_NAME;
        page.body.addElement (spElem);

        text = new TextElement ("" + DateTimeHelpers.dateToString (this.deliveryDate) +
            IE302.TAG_NEWLINE + IE302.TAG_NEWLINE);

        spElem = new SpanElement ();
        spElem.addElement (text);
        spElem.classId = CssConstants.CLASS_VALUE;
        page.body.addElement (spElem);

        // state of order
        String stateString;
        int index = StringHelpers.findString (States.PST_STATEIDS, Integer
            .toString (this.processState));

        if (index != -1)
        {
            stateString = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, States.PST_STATENAMES [index], env);
        } // if
        else
        {
            stateString = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_PST_NONE, env);
        } // else

        text = new TextElement (
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_ORDERSTATE, env) + ": ");

        spElem = new SpanElement ();
        spElem.addElement (text);
        spElem.classId = CssConstants.CLASS_NAME;
        page.body.addElement (spElem);

        text = new TextElement ("" + stateString + IE302.TAG_NEWLINE +
            IE302.TAG_NEWLINE + IE302.TAG_NEWLINE + IE302.TAG_NEWLINE);

        spElem = new SpanElement ();
        spElem.addElement (text);
        spElem.classId = CssConstants.CLASS_VALUE;
        page.body.addElement (spElem);

        // last line
        TableElement lastLine = new TableElement ();
        lastLine.border = 0;
        lastLine.borderColor = "white";
        lastLine.width = HtmlConstants.TAV_FULLWIDTH;
        row = new RowElement (2);

        // signature field
        text = new TextElement (StoreConstants.CONST_SIGNATURE_LINE +
            IE302.TAG_NEWLINE + 
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_SIGNATURE, env) + IE302.TAG_NEWLINE);

        td = new TableDataElement (text);
        td.classId = CssConstants.CLASS_SIGNATURE;

        row.addElement (td);


        text = new TextElement (IE302.TAG_NEWLINE + 
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_DATE, env) +
            ": " + DateTimeHelpers.dateToString (DateTimeHelpers.getCurAbsDate ()) +
            IE302.TAG_NEWLINE);
        td = new TableDataElement (text);
        td.alignment = IOConstants.ALIGN_RIGHT;
        row.addElement (td);
        lastLine.addElement (row);
        page.body.addElement (lastLine);


        try                             // try to find ssi file
        {
            // order footer (ssi - file 'orderfooter.htm'
            text = new TextElement (IOHelpers.getSSIFile (
                StoreConstants.SSI_ORDERFOOTER, this.env,
                (Configuration) this.app.configuration));
            page.body.addElement (text);
        } // try to find ssi file
        catch (SsiFileNotFoundException e)
                                        // ssi file was not found
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch ssi file not found

        // build the page and show it to the user:
        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
    } // performShowOrderForPrint


    /**************************************************************************
     * Show the positions of the order within a form. <BR/>
     *
     * @param   t           Table where the properties shall be added.
     * @param   withLink    Flag which indicates whether the product is a
     *                      link or not
     * @param   forPrint    Shall the output be generated for printing?
     */
    private void showPositions (TableElement t, boolean withLink, boolean forPrint)
    {
        TableElement table = new TableElement ();
        Vector<OrderElement_01> totals = new Vector<OrderElement_01> ();
        String currency = null;
        long total = 0;
        OrderElement_01 oe;

        table.alignment = Order_01.orderPosAlignments;
        table.frametypes = IOConstants.FRAME_BOX;
        table.border = 1;
        if (forPrint)
        {
            table.borderColor = "#000000";
        } // if
        else
        {
            table.classId = StoreConstants.CLASS_PRODUCT_ORDER_TABLE;
        } // else
        table.width = HtmlConstants.TAV_FULLWIDTH;
        table.cellpadding = 5;
        RowElement row = new RowElement (Order_01.orderPosHeading.length);
        TableDataElement td;
        TextElement text;

        // add the header row
        TextElement header = new TextElement (
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_ORDERPOSITIONS, env));
        td = new TableDataElement (header);
        if (!forPrint)
        {
            td.classId = StoreConstants.CLASS_ORDER_HEADER;
        } // if
        else
        {
            td.classId = StoreConstants.CLASS_ORDER_LISTDESCRIPTION;
        } // else

        td.alignment = IOConstants.ALIGN_LEFT;
        td.colspan = 6;
        row.addElement (td);
        table.addElement (row);

        row = new RowElement (Order_01.orderPosHeading.length);

        // create heading:
        for (int i = 0; i < Order_01.orderPosHeading.length; i++)
        {
            text = new TextElement (Order_01.orderPosHeading[i]);

            td = new TableDataElement (text);
            td.width = Order_01.orderPosWidhts[i];
            row.addElement (td);
        } // for

        row.classId = StoreConstants.CLASS_ORDER_HEADER;

        table.addElement (row);

        // show the positions of the order
        for (int i = 0; i < this.positions.size (); i++)
        {
            int index = 0;
            if (!forPrint)
            {
                index = i % 2;
            } // if !forPrint
            table.addElement (this.positions.elementAt (i).showPosition (
                Order_01.orderPosHeading.length, withLink,
                StoreConstants.LST_CLASSORDERROWS[index]));

            currency = this.positions.elementAt (i).priceCurrency;
            total = this.positions.elementAt (i).totalPrice;
            int size = totals.size ();
            boolean found = false;
            for (int k = 0; k < size && !found; k++)
            {
                oe = totals.elementAt (k);
                if (currency.equals (oe.priceCurrency))
                {
                    oe.totalPrice += total;
                    found = true;
                } //if
            } // for
            if (!found)
            {
                oe = new OrderElement_01 ();
                oe.totalPrice = total;
                oe.priceCurrency = currency;
                totals.addElement (oe);
            } // if
        } // showPositions

        // calculate the total price
        for (int k = 0; k < totals.size (); k++)
        {

            // get next totals - line  (there is one for each currency)
            oe = totals.elementAt (k);

            // create output strings for amount and currencies
            String currencyString = oe.priceCurrency;
            String amountString   = Helpers.moneyToString (oe.totalPrice);
            // show currencies - if currency is not euro, show calculated euro amount
            if (!UtilConstants.TOK_CURRENCY_EUR.equals (oe.priceCurrency))
            {
                currencyString += IE302.TAG_NEWLINE +
                    UtilConstants.TOK_CURRENCY_EUR;
                amountString += IE302.TAG_NEWLINE +
                    Helpers.moneyToString (Helpers.getEuroAmount (
                        oe.priceCurrency, oe.totalPrice));
            } // if currency is not euro


            // generate html-output for total-lines
            row = new RowElement (Order_01.orderPosHeading.length);

            // show text 'TOTAL' in first column
            text = new TextElement (
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_TOTALPRICE, env));
            td = new TableDataElement (text);
            td.classId = StoreConstants.CLASS_ORDER_HEADER;
            row.addElement (td);

            // add blank elements in next columns
            for (int i = 1; i < (Order_01.orderPosHeading.length - 2); i++)
            {
                td = new TableDataElement (new BlankElement ());
                td.classId = StoreConstants.CLASS_ORDER_HEADER;
                row.addElement (td);
            } // for

            // generate html code for currencies and totalamounts
            text = new TextElement (currencyString);
            td = new TableDataElement (text);
            td.classId = StoreConstants.CLASS_ORDER_HEADER;
            row.addElement (td);
            text = new TextElement (amountString);
            td = new TableDataElement (text);
            td.classId = StoreConstants.CLASS_ORDER_HEADER;
            row.addElement (td);
            table.addElement (row);
        } // for

        // add the constructed table to the upper table
        row = new RowElement (1);
        td = new TableDataElement (table);
        td.colspan = 2;
        td.width = HtmlConstants.TAV_FULLWIDTH;
        row.addElement (td);
        t.addElement (row);
    } // showPositions


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in an
     * object info view. <BR/>
     *
     * @return  An array with button ids that can be displayed.
     */
    protected int[] setInfoButtons ()
    {
        // define buttons to be displayed:
        int[] buttons;

        this.retrieveProcessState ();

        if (this.processState == States.PST_COMPLETED ||
                this.processState == States.PST_REJECTED)
        {
            buttons = new int[]
            {
                Buttons.BTN_DELETE,
                Buttons.BTN_PRINT,
            }; // buttons
        } // if
        else if (this.processState == States.PST_DISCARDED)
        {
            buttons = new int[]
            {
                Buttons.BTN_DELETE,
                Buttons.BTN_DISTRIBUTE,
                Buttons.BTN_PRINT,
            }; // buttons
        } // else if
        else
        {
            buttons = new int[]
            {
                Buttons.BTN_CHANGEORDERSTATE,
                Buttons.BTN_DISTRIBUTE,
                Buttons.BTN_PRINT,
            }; // buttons
        } // else

        // return button array
        return buttons;
    } // showInfoButtons


    /**************************************************************************
     * Get the catalogobject regarding to this order. <BR/>
     *
     * @return  The catalog object.
     */
    public Catalog_01 getOrderCatalogObject ()
    {
        // create the catalog object
        Catalog_01 catalog = new Catalog_01 ();
        catalog.initObject (this.catalogOid, this.user, this.env, this.sess, this.app);
        try
        {
            // retrieve the catalog object
            catalog.retrieve (Operations.OP_VIEW);
        } // try
        catch (NoAccessException e) // no access to object allowed
        {
            // show a message that the catalog object can not be accessed
            this.showNoAccessMessage (Operations.OP_READ);
        } // catch
        catch (AlreadyDeletedException e)
        {
            // show a message that the catalog object has been deleted
            this.showAlreadyDeletedMessage ();
        } // catch
        catch (ObjectNotFoundException e)
        {
            // show a message that the catalog object was not found:
            this.showObjectNotFoundMessage ();
        } // catch

        return catalog;
    } // getOrderCatalogObject


    /**************************************************************************
     * Store the order in the database and forward it to the recipient of. <BR/>
     */
    public void createOrder ()
    {
        // if there is someone to send the order to
        if (this.orderResponsibleOid != null)
        {
            // create the order object in the database
            this.performCreateOrder ();
            // if order successfully created send order
            if (this.oid != null)
            {
                // check if order has been send
                if (this.sendOrder)
                {
                    IOHelpers.showMessage (
                        MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                            StoreTokens.ML_ORDER_SEND, env),
                        this.app, this.sess, this.env);
                    // start order export
                    this.exportOrder ();
                } // if (this.sendOrder)
                else    // order has been stored
                {
                    IOHelpers.showMessage (
                        MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                            StoreTokens.ML_ORDER_STORE, env),
                        this.app, this.sess, this.env);
                } // else order has been stored
                // show the content of the shopping cart
                this.showShoppingCart ();
            } // if (this.oid != null)
            else    // order has not been created
            {
                IOHelpers.showMessage (
                    MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                        StoreTokens.ML_ORDER_NOTSEND, env),
                    this.app, this.sess, this.env);
                this.showShoppingCart ();
            } // else order has not been created
        } // if (this.orderResponsibleOid != null)
        else // no order responsible defined
        {
            IOHelpers.showMessage (
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_NO_ORDERRESP, env),
                this.app, this.sess, this.env);
            this.showShoppingCart ();
        } // else no order responsible defined
    } // createOrder


    /**************************************************************************
     * Store the order in the database. <BR/>
     */
    private void performCreateOrder ()
    {
        StoredProcedure sp = new StoredProcedure (this.procCreate, StoredProcedureConstants.RETURN_VALUE);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)

        // input parameters:
        // user id
        if (this.user != null)
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER,
                            this.user.id);
        } // if
        else
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, 0);
        } // else
        // operation
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, 0);
        // tVersionId
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
                        this.getTypeCache ().getTVersionId (StoreTypeConstants.TC_Order));
        // name
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        this.name);
        // state
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
                        this.state);
        // description
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        this.name);
        //---------- special Order_01 parameters --------------
        // voucherNo
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        this.voucherNo);
        // voucherDate
        if (this.sendOrder)
        {
            sp.addInParameter (ParameterConstants.TYPE_DATE,
                            this.voucherDate);
        } // if
        else
        {
            sp.addInParameter (ParameterConstants.TYPE_DATE,
                            (Date) null);
        } // else
        // supplierCompany
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        this.supplierCompany);
        // contactSupplier
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        this.contactSupplier);
        // customerCompany
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        this.customerCompany);
        // contactCustomer
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        this.contactCustomer);
        // deliveryAddress
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        this.deliveryAddressComplete);
        // paymentAddress
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        this.paymentAddressComplete);
        // description1
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        this.notPossibleDescription);
        // description2
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        this.deliveryDescription);
        // description3
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        this.shippmentDescription);
        // deliveryDate
        sp.addInParameter (ParameterConstants.TYPE_DATE,
                        this.deliveryDate);
        // catalogOid
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        "" + this.catalogOid);
        // paymentTypeOid
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        "" + this.p_paymentTypeOid);
        // creditCardOwner
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        this.p_creditCardOwner);
        // creditCardNumber
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        this.p_creditCardNumber);
        // creditCardExpiryMonth
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        this.p_creditCardExpiryMonth);
        // creditCardExpiryYear
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        this.p_creditCardExpiryYear);
        // output parameters:
        // oid
        Parameter oidParam = sp.addOutParameter (ParameterConstants.TYPE_STRING);

        try
        {
            // perform the function call:
            BOHelpers.performCallFunctionData (sp, this.env);
        } // try
        catch (NoAccessException e)
        {
            // TODO: handle the exception
        } // catch

        // set the new oid
        this.oid = SQLHelpers.getSpOidParam (oidParam);
    } // performCreateOrder


    /**************************************************************************
     * Get the default values for the order out of the database. <BR/>
     *
     * @throws  NoAccessException
     *          The user does not have access to this object to perform the
     *          required operation.
     */
    protected void performRetrieveOrderDefaults () throws NoAccessException
    {
        // create the stored procedure call:
        StoredProcedure sp = new StoredProcedure (
            Order_01.PROC_RETRIEVE_ORDER_DEFAULTS,
            StoredProcedureConstants.RETURN_VALUE);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // input parameters
        // user id
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.user != null ? this.user.id : 0);
        // catalog_oid
        sp.addInParameter (ParameterConstants.TYPE_STRING, "" + this.catalogOid);
        // output parameters
        // voucherNo
        Parameter paramVoucherNo =
            sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // voucherDate
        Parameter paramVoucherDate =
            sp.addOutParameter (ParameterConstants.TYPE_DATE);
        // supplierCompany
        Parameter paramsupplierCompany =
            sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // contactSupplier
        Parameter paramContactSupplier =
            sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // orderResponsibleOid
        Parameter paramOrderResponsible =
            sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // orderResponsibleMediaOid
        Parameter paramOrderResponsibleMedia =
            sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // customerCompany
        Parameter paramCustomerCompany =
            sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // contactCustomer
        Parameter paramContactCustomer =
            sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // deliveryName
        Parameter paramDeliveryName =
            sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // deliveryAddress
        Parameter paramDeliveryAddress =
            sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // deliveryZIP
        Parameter paramDeliveryZip =
            sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // deliveryTown
        Parameter paramDeliveryTown =
            sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // deliveryCountry
        Parameter paramDeliveryCountry =
            sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // paymentName
        Parameter paramPaymentName =
            sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // paymentAddress
        Parameter paramPaymentAddress =
            sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // paymentZIP
        Parameter paramPaymentZip =
            sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // paymentTown
        Parameter paramPaymentTown =
            sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // paymentCountry
        Parameter paramPaymentCountry =
            sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // description1
        Parameter paramDescription1 =
            sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // description2
        Parameter paramDescription2 =
            sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // description3
        Parameter paramDescription3 =
            sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // deliveryDate
        Parameter paramDeliveryDate =
            sp.addOutParameter (ParameterConstants.TYPE_DATE);
        //
        Parameter paramEmailOrderResponsible =
            sp.addOutParameter (ParameterConstants.TYPE_STRING);
        //
        Parameter paramEmailCurrentUser =
            sp.addOutParameter (ParameterConstants.TYPE_STRING);

        // perform the function call:
        BOHelpers.performCallFunctionData (sp, this.env);

        this.voucherNo = paramVoucherNo.getValueString ();
        this.voucherDate     = paramVoucherDate.getValueDate ();
        this.supplierCompany = paramsupplierCompany.getValueString ();
        this.contactSupplier = paramContactSupplier.getValueString ();

        this.orderResponsibleOid = SQLHelpers.getSpOidParam (paramOrderResponsible);

// HACK - because of problems with getSPOidParam - instead of null (like in db)
// i get an oid with content 0x0000000000000000
        if (this.orderResponsibleOid != null &&
            this.orderResponsibleOid.isEmpty ())
        {
            this.orderResponsibleOid = null;
        } // if

        this.orderResponsibleMediaOid = SQLHelpers.getSpOidParam (paramOrderResponsibleMedia);
        this.customerCompany = paramCustomerCompany.getValueString ();
        this.contactCustomer = paramContactCustomer.getValueString ();
        this.deliveryName = paramDeliveryName.getValueString ();
        this.deliveryAddress = paramDeliveryAddress.getValueString ();
        this.deliveryZIP = paramDeliveryZip.getValueString ();
        this.deliveryTown = paramDeliveryTown.getValueString ();
        this.deliveryCountry = paramDeliveryCountry.getValueString ();
        this.paymentName = paramPaymentName.getValueString ();
        this.paymentAddress = paramPaymentAddress.getValueString ();
        this.paymentZIP = paramPaymentZip.getValueString ();
        this.paymentTown = paramPaymentTown.getValueString ();
        this.paymentCountry = paramPaymentCountry.getValueString ();

        this.notPossibleDescription = paramDescription1.getValueString ();
        this.shippmentDescription = paramDescription2.getValueString ();
        this.deliveryDescription = paramDescription3.getValueString ();

        this.deliveryDate    = paramDeliveryDate.getValueDate ();
        this.eMailOfOrderResponsible = paramEmailOrderResponsible.getValueString ();
        this.eMailOfCurrentUser = paramEmailCurrentUser.getValueString ();
    } // performRetrieveOrderDefaults


    /**************************************************************************
     * Get the order positions out of the database. <BR/>
     *
     * @return  rowCount which was returned from SQLAction  (1 or 0 with JDBC)
     *
     * @throws  NoAccessException
     *          The user does not have access to this object to perform the
     *          required operation.
     */
    protected int performRetrievePositions () throws NoAccessException
    {
//debug ("--- START performRetrievePositions ---");
        SQLAction action = null;        // the action object used to access the
                                        // database
        int rowCount  = 0;

        // execute stored procedure
        if (this.catalogOid == null)
        {
            this.catalogOid = OID.getEmptyOid ();
        } // if (this.catalogOid == null)

        try
        {
            // execute stored procedure - return value
            // gives right-information
            String queryStr =
                "SELECT name, quantity, unitOfQty, packingUnit, productOid, productDescription, priceCurrency, price" +
                " FROM v_ShoppingCartUser$content " +
                " WHERE userId = " + this.user.id +
                " AND catalogOid = " + this.catalogOid.toStringQu ();

            // open db connection - only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();

            rowCount = action.execute (queryStr, false);

            // everything ok - go on
            if (rowCount > 0)
            {
                try
                {
                    OrderElement_01 pos;
                    // get tuples out of db
                    this.positions.removeAllElements ();
                    while (!action.getEOF ())
                    {
                        pos = new OrderElement_01 ();
                        pos.name = action.getString ("name");
                        pos.productDescription = action.getString ("productDescription");
                        pos.quantity = action.getInt ("quantity");
                        pos.unitOfQty = action.getInt ("unitOfQty");
                        pos.packingUnit = action.getString ("packingUnit");
                        pos.priceCurrency = action.getString ("priceCurrency");
                        pos.price = action.getCurrency ("price");
                        pos.productOid = SQLHelpers.getQuOidValue (action, "productOid");
                        this.positions.addElement (pos);
                       // step one tuple ahead for the next loop
                        action.next ();
                    } // while (!action.getEOF ())
                    // the last tuple has been processed
                    // end transaction
                    action.end ();
                } // try
                catch (DBError e)
                {
                    // an error occurred - show name and info
                    IOHelpers.showMessage (e, this.app, this.sess, this.env, false);
                } // catch
            } // if (rowCount > 0)
            // end action
            action.end ();
        } // try
        catch (DBError e)
        {
            // get all errors (can be chained)
            String allErrors = new String ("");
            String h = new String (e.getMessage ());
            h += e.getError ();
            while (h != null)
            {
                allErrors += h;
                h = e.getError ();
            } // while (h != null)
            // show the message
            IOHelpers.showMessage (allErrors,
                this.app, this.sess, this.env);
        } // catch
        finally
        {
            // close db connection in every case - only workaround - db
            // connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        // be patient - JDBC only returns 0 or 1 !!!
        return rowCount;
    } // performRetrievePositions


    /**************************************************************************
     * Get the order positions out of the database. <BR/>
     *
     * @param   action   SQLAction for Datanaseoperation
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    protected void performRetrieveOrderPositions (SQLAction action)
        throws NoAccessException
    {
        int rowCount  = 0;

        // execute stored procedure
        try
        {
            // execute stored procedure - return value
            // gives right-information
            String queryStr =
                "SELECT name, productNo, qty, unitOfQty, packingUnit, productOid, productDescription, priceCurrency, price" +
                " FROM v_Order$content " +
                " WHERE containerId = " + this.oid.toStringQu ();

//debug (queryStr);

            rowCount = action.execute (queryStr, false);

            // everything ok - go on
            if (rowCount > 0)
            {
                try
                {
                    OrderElement_01 pos;
                    // get tuples out of db
                    this.positions.removeAllElements ();
                    while (!action.getEOF ())
                    {
                        pos = new OrderElement_01 ();
                        pos.name = action.getString ("name");
                        pos.productDescription = action.getString ("productDescription");
                        pos.quantity = action.getInt ("qty");
                        pos.unitOfQty = action.getInt ("unitOfQty");
                        pos.packingUnit = action.getString ("packingUnit");
                        pos.priceCurrency = action.getString ("priceCurrency");
                        pos.price = action.getCurrency ("price");
                        pos.productOid = SQLHelpers.getQuOidValue (action, "productOid");
                        pos.productno = action.getString ("productNo");
                        this.positions.addElement (pos);
                       // step one tuple ahead for the next loop
                        action.next ();
                    } // while

                    // the last tuple has been processed
                    // end transaction
                    action.end ();
                } // try
                catch (DBError e)
                {
                    // an error occurred - show name and info
                    IOHelpers.showMessage (e, this.app, this.sess, this.env, false);
                } // catch
            } // if
            // end action
            action.end ();
        } // try
        catch (DBError e)
        {
            // get all errors (can be chained)
            String allErrors = new String ("");
            String h = new String (e.getMessage ());
            h += e.getError ();
            while (h != null)
            {
                allErrors += h;
                h = e.getError ();
            } // while
            // show the message
            IOHelpers.showMessage (allErrors,
                this.app, this.sess, this.env);
        } // catch
        finally
        {
            // nothing to do
        } // finally
    } // performRetrieveOrderPositions


    /**************************************************************************
     * Shows the shopping cart contents
     */
    private void showShoppingCart ()
    {
        ShoppingCart_01 sc;
        if (this.oid != null && !this.oid.isEmpty ())
        {
            sc = (ShoppingCart_01) BOHelpers.getObject (
                this.getUserInfo ().workspace.shoppingCart, this.env, false,
                false, false);

            // check if instance of shopping cart could be created:
            if (sc != null)             // instance was not created?
            {
                sc.showContent (0);
            } // if
            else
            {
                IOHelpers.showMessage (MultilingualTextProvider
                    .getMessage(BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_OBJECTNOTFOUND, this.env), 
                    this.env);
                return;                 // terminate method
            } // else
        } //if catalogOid != null
    } // showShoppingCart


    /**************************************************************************
     * Change only the state of the order. <BR/>
     *
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
     * @exception   NameAlreadyGivenException
     *              An object with this name already exists. This exception is
     *              only raised by some specific object types which don't allow
     *              more than one object with the same name.
     */
    protected void performChangeData (int operation)
        throws NoAccessException, NameAlreadyGivenException
    {
        this.changeProcessState (Operations.OP_CHANGEPROCSTATE + this.processState);
    } // performChangeData


    /**************************************************************************
     * Actualize the OrderDate to todays date. <BR/>
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    public void actualizeOrderDate () throws NoAccessException
    {
        // create the stored procedure call:
        StoredProcedure sp = new StoredProcedure (
            Order_01.PROC_ACTUALIZE_ORDER_DATE,
            StoredProcedureConstants.RETURN_VALUE);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // input parameters
        // oid
        BOHelpers.addInParameter (sp, this.oid);
        // user id
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.user != null ? this.user.id : 0);
        // operation
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, Operations.OP_CHANGE);

        // perform the function call:
        BOHelpers.performCallFunctionData (sp, this.env);
    } // actualizeOrderDate


    /**************************************************************************
     * Writes the object data into a dataElement object. <BR/>
     *
     * @param dataElement     the dataElement to write the data to
     */
    public void writeExportData (DataElement dataElement)
    {
        // set the business object specific values
        super.writeExportData (dataElement);
        // set the type specific values
        dataElement.setExportValue ("supplier", this.supplier);
        dataElement.setExportValue ("voucherNo", this.voucherNo);
        dataElement.setExportValue ("voucherDate", this.voucherDate);
//debug ("this.voucherDate = " + this.voucherDate);

        dataElement.setExportValue ("supplierCompany", this.supplierCompany);
        dataElement.setExportValue ("contactSupplier", this.contactSupplier);
        dataElement.setExportValue ("customerCompany", this.customerCompany);
        dataElement.setExportValue ("contactCustomer", this.contactCustomer);
        dataElement.setExportValue ("deliveryName", this.deliveryName);
        dataElement.setExportValue ("deliveryAddress", this.deliveryAddress);
        dataElement.setExportValue ("deliveryZIP", this.deliveryZIP);
        dataElement.setExportValue ("deliveryTown", this.deliveryTown);
        dataElement.setExportValue ("deliveryCountry", this.deliveryCountry);
        dataElement.setExportValue ("paymentName", this.paymentName);
        dataElement.setExportValue ("paymentAddress", this.paymentAddress);
        dataElement.setExportValue ("paymentZIP", this.paymentZIP);
        dataElement.setExportValue ("paymentTown", this.paymentTown);
        dataElement.setExportValue ("paymentCountry", this.paymentCountry);
        dataElement.setExportValue ("deliveryAddressComplete", this.deliveryAddressComplete);
        dataElement.setExportValue ("paymentAddressComplete", this.paymentAddressComplete);
        dataElement.setExportValue ("supplierAddressComplete", this.supplierAddressComplete);
        dataElement.setExportValue ("notPossibleDescription", this.notPossibleDescription);
        dataElement.setExportValue ("deliveryDescription", this.deliveryDescription);
        dataElement.setExportValue ("shipmentDescription", this.shippmentDescription);
        dataElement.setExportValue ("deliveryDate", this.deliveryDate);

        // loop through positions vector and create dataElementList with
        // OrderElement objects
        DataElementList dataElementList = new DataElementList ();
        OrderElement_01 orderElement = null;
        DataElement posDataElement = null;
        // get the vector`s enumeration
        Enumeration<OrderElement_01> positionsEnum = this.positions.elements ();
        while (positionsEnum.hasMoreElements ())
        {
            // get the next orderElement from the enumeration
            // get a orderElement that represents an object
            orderElement = positionsEnum.nextElement ();
            // create an empty dataElement instance
            posDataElement = new DataElement ();
            // get the data from the orderElement and write it
            // into a DataElement instance
            orderElement.writeExportData (posDataElement);
            // add the dataElement instance to the list of
            // data elements
            dataElementList.addElement (posDataElement);
        } // while (positionsEnum.hasMoreElements ())
        // copy dataElementList with OrderElements into dataElement
        dataElement.dataElementList = dataElementList;
    } // writeExportData


    /**************************************************************************
     * Check the catalog the order is associated with if export order
     * has been activated. If yes it reads the connector, translator and
     * filter settings from the catalog and performs an export with
     * the settings. <BR/>
     */
    public void exportOrder ()
    {
//debug ("--- START exportOrder ---");
        try
        {
            // get the catalog object:
            Catalog_01 catalog = (Catalog_01)
                BOHelpers.getObject (this.catalogOid, this.env, false, false);
//showDebug ("catalog.isOrderExport = " + catalog.isOrderExport);

            // check if order export has been activated
            if (catalog != null && catalog.isOrderExport)
            {
                // check if there is a filter and a connector specified
                if (catalog.connectorOid != null && catalog.filterId > 0)
                {
                    // create an exportIntegrator object
                    ExportIntegrator exportIntegrator = new ExportIntegrator ();
                    // init the exportIntegrator object with the owner of the catalog
                    // this needs to be done to be able to read the connector and
                    // the translator object from the catalog. typically only
                    // the owner of the catalog will have permission to access
                    // the connector and the translator and not the user creating the order
                    exportIntegrator.initObject (OID.getEmptyOid (), catalog.owner, this.env, this.sess, this.app);
                    exportIntegrator.setContainerId (this.oid);
                    // set the connector in the exportIntegrator object
                    Connector_01 connector = exportIntegrator.createConnectorFromOid (catalog.connectorOid.toString ());
//showDebug ("connector = " + connector);
                    exportIntegrator.setConnector (connector);
                    // set the translator in the exportIntegrator object
                    Translator_01 translator = exportIntegrator.createTranslatorFromOid (catalog.translatorOid.toString ());
//showDebug ("translator = " + translator);
                    exportIntegrator.setTranslator (translator);
                    // set the filter in the exportIntegrator object
                    try
                    {
//showDebug ("DIConstants.EXPORTFILTER_CLASSES [catalog.filterId] = " + DIConstants.EXPORTFILTER_CLASSES [catalog.filterId]);
                        Filter filter = (Filter) Class.forName (DIConstants.EXPORTFILTER_CLASSES [catalog.filterId]).newInstance ();
                        filter.initObject (OID.getEmptyOid (), this.user, this.env, this.sess, this.app);
//showDebug ("filter = " + filter);
                        exportIntegrator.setFilter (filter);
                        // check if the order positions have been read
                        if (this.positions == null)
                        {
//showDebug ("this.retrieve");
                            // retrieve the content of the order object from the database
                            this.retrieve (Operations.OP_VIEW);
                        } // if (this.positions == null)

                        // check again if we now have a valid connector
                        // because an export without a connector will not
                        // be possible. we can assume that the filter has been
                        // correctly set
                        if (connector != null)
                        {
                            // display message that order export has been started
                            IOHelpers.showMessage ( 
                                MultilingualTextProvider.getMessage (StoreMessages.MSG_BUNDLE,
                                    StoreMessages.ML_MSG_ORDER_EXPORT_STARTED, env),
                                this.app, this.sess, this.env);
                            // settings
                            exportIntegrator.isShowSettings = false;
                            exportIntegrator.isGetSettingsFromEnv = false;
                            // set the user of the exportIntegrator back to the
                            // actual user in order to be able to read the order object
                            exportIntegrator.user = this.user;
                            // perform the export
                            String [] oidStrArray = {this.oid.toString ()};
//showDebug ("oidStrArray: " + oidStrArray);
                            exportIntegrator.startExport (oidStrArray);
//showDebug ("export finished: " + oidStrArray);
                        } // if (connector != null)
                        else    // no connector set
                        {
//showDebug ("no connector set");
                            // print an error message
                            IOHelpers.showMessage ( 
                                MultilingualTextProvider.getMessage (StoreMessages.MSG_BUNDLE,
                                    StoreMessages.ML_MSG_ORDER_EXPORT_FAILED, env),
                                this.app, this.sess, this.env);
                        } // no connector set
                    } // try
                    catch (Exception e)
                    {
//showDebug ("Exception: " + e);
                        // print an error message
                        IOHelpers.showMessage ( 
                            MultilingualTextProvider.getMessage (StoreMessages.MSG_BUNDLE,
                                StoreMessages.ML_MSG_ORDER_EXPORT_FAILED, env) +
                            " (" + e.getMessage () + ")",
                            this.app, this.sess, this.env);
                    } // catch
                } // if (catalog.connectorOid != null && catalog.filterId <= 0)
                else    // connector or filter has not been set in catalog
                {
//showDebug ("connector or filter has not been set in catalog");
                    // print an error message
                    IOHelpers.showMessage ( 
                        MultilingualTextProvider.getMessage (StoreMessages.MSG_BUNDLE,
                            StoreMessages.ML_MSG_ORDER_EXPORT_FAILED, env),
                        this.app, this.sess, this.env);
                } // connector or filter has nopt been set in catalog
            } // if (catalog.isOrderExport)
        } // try
        catch (NoAccessException e) // no access to object allowed
        {
            // show a message that the catalog object can not be accessed
            this.showNoAccessMessage (Operations.OP_READ);
            // print an error message
            IOHelpers.showMessage ( 
                MultilingualTextProvider.getMessage (StoreMessages.MSG_BUNDLE,
                    StoreMessages.ML_MSG_ORDER_EXPORT_FAILED, env),
                this.app, this.sess, this.env);
        } // catch
        catch (AlreadyDeletedException e)
        {
            // show a message that the catalog object has been deleted
            this.showAlreadyDeletedMessage ();
            // print an error message
            IOHelpers.showMessage ( 
                MultilingualTextProvider.getMessage (StoreMessages.MSG_BUNDLE,
                    StoreMessages.ML_MSG_ORDER_EXPORT_FAILED, env),
                this.app, this.sess, this.env);
        } // catch
        catch (ObjectNotFoundException e)
        {
            // show a message that the catalog object was not found:
            this.showObjectNotFoundMessage ();
            // print an error message:
            IOHelpers.showMessage ( 
                MultilingualTextProvider.getMessage (StoreMessages.MSG_BUNDLE,
                    StoreMessages.ML_MSG_ORDER_EXPORT_FAILED, env),
                this.app, this.sess, this.env);
        } // catch
    } // exportOrder


    /**************************************************************************
     * Shows a debug message. <BR/>
     *
     * @param   message Debug message to be shown.
     *
     * @deprecated  This method is not longer necessary. Instead the IDE
     *              debugging mechanism shall be used. All calls to this method
     *              shall be deleted.
     */
    public void showDebug (String message)
    {
        if (false)
        {
            this.env.write ("<DIV ALIGN=\"LEFT\">" + this.getClass ().getName () + ":" +
                      message + "</DIV><P>");
        } // if
        else
        {
            this.debug (message);
        } // else
    } // showDebug


    /**************************************************************************
     * Get the standard receivers for distributions of the current object. <BR/>
     *
     * @return  An array containing the oids of the standard receivers.
     *          <CODE>null</CODE> if there are no standard receivers.
     */
    public OID[] getStdNotificationReceivers ()
    {
        OID[] receivers = null;         // the receivers list

        // check the process state:
        if (this.processState == States.PST_DISCARDED)
        // if order is only discarded - add orderResponsible to receiversList
        {
            receivers = new OID [1];
            receivers[0] = this.orderResponsibleOid;
        } // if (this.processState == States.PST_DISCARDED)

        // return the result:
        return receivers;
    } // getStdNotificationReceivers


    /**************************************************************************
     * Get the notification service which can perform notifications for the
     * actual object. <BR/>
     *
     * @return  The notification service.
     */
    public INotificationService getNotificationService ()
    {
        // create a new instance for a notification service and return it:
        return NotificationServiceFactory.getInstance (env).getNotificationService (OrderNotificationService.class);
    } // getNotificationService


    /**************************************************************************
     * Perform a notification for the actual object. <BR/>
     *
     * @param   notiService     The notification service.
     * @param   template        The notification template.
     * @param   distributedOid  Oid of the distributed object.
     *
     * @throws  NotificationFailedException
     *          An exception occurred during notification.
     *
     * @see ibs.service.notification.NotificationService#performNotification (java.util.Vector, ibs.bo.OID, ibs.service.notification.NotificationTemplate)
     */
    public void callNotificationService (NotificationService notiService,
                                         NotificationTemplate template,
                                         OID distributedOid)
        throws NotificationFailedException
    {
        // check order state
        if (this.processState == States.PST_DISCARDED)
                                    // order was discarded?
        {
            // perform the notification:
            ((OrderNotificationService) notiService).performNotification (
                this.sess.receivers, distributedOid, template, false);
        } // if order was discarded
        else                            // a sent order will be distributed
                                        // again
        {
            // perform the notification:
            notiService.performNotification (this.sess.receivers,
                distributedOid, template, true);
        } // else a sent order will be distributed again
    } // callNotificationService

} // class Order_01
