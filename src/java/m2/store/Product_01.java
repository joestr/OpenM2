/*
 * Class: Product_01.java
 */

// package:
package m2.store;

// imports:
import ibs.app.AppConstants;
import ibs.app.AppFunctions;
import ibs.app.CssConstants;
import ibs.bo.BOArguments;
import ibs.bo.BOConstants;
import ibs.bo.BOHelpers;
import ibs.bo.BOListConstants;
import ibs.bo.BOMessages;
import ibs.bo.BOPathConstants;
import ibs.bo.BOTokens;
import ibs.bo.BusinessObject;
import ibs.bo.Buttons;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.bo.ObjectNotFoundException;
import ibs.bo.Operations;
import ibs.bo.SelectionList;
import ibs.bo.States;
import ibs.di.DataElement;
import ibs.io.HtmlConstants;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.LayoutConstants;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.BlankElement;
import ibs.tech.html.BuildException;
import ibs.tech.html.CenterElement;
import ibs.tech.html.Font;
import ibs.tech.html.FormElement;
import ibs.tech.html.FrameElement;
import ibs.tech.html.FrameSetElement;
import ibs.tech.html.GroupElement;
import ibs.tech.html.IE302;
import ibs.tech.html.ImageElement;
import ibs.tech.html.InputElement;
import ibs.tech.html.Page;
import ibs.tech.html.RowElement;
import ibs.tech.html.ScriptElement;
import ibs.tech.html.StyleSheetElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TableElement;
import ibs.tech.html.TextElement;
import ibs.tech.http.HttpArguments;
import ibs.tech.sql.DBError;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;
import ibs.tech.sql.StoredProcedureConstants;
import ibs.util.AlreadyDeletedException;
import ibs.util.DateTimeHelpers;
import ibs.util.Helpers;
import ibs.util.NoAccessException;
import ibs.util.UtilConstants;

import java.util.Date;
import java.util.Vector;


/******************************************************************************
 * This class represents one BusinessObject of type Product with version 03.
 * <BR/>
 *
 * @version     $Id: Product_01.java,v 1.49 2013/01/16 16:14:12 btatzmann Exp $
 *
 * @author      Bernhard Walter (BW), 981215
 ******************************************************************************
 */
public class Product_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Product_01.java,v 1.49 2013/01/16 16:14:12 btatzmann Exp $";


    ///////////////////////////////////////////////////////////////////////////
    // properties
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Product number of the product. <BR/>
     */
    private String productNo = null;
    /**
     * European article number. <BR/>
     */
    private String ean = null;

//! AJ 000427
    /**
     * long description for product (max. 65K bytes) in description which is
     * shown in the list the text is cut to 250 characters and the string
     * '...' is added
     */
    public String fullDescription = "";

    /**
     * The date from which the product is available. <BR/>
     */
    public Date availableFrom = DateTimeHelpers.getCurAbsDate (); // set current date

    /**
     * Smallest unit of quantity a customer may order. <BR/>
     */
    private int unitOfQty = 1;
    /**
     * Name of the unit. <BR/>
     */
    private String packingUnit = "";
    /**
     * Flag if thumbnail is the same as image only smaller. <BR/>
     */
    private boolean thumbAsImage = false;
    /**
     * Thumbnail of the product used for the catalog view. <BR/>
     */
    private String thumbnail = null;
    /**
     * Image of the product used for the info view. <BR/>
     */
    private String image = null;
    /**
     * Info  about availability. <BR/>
     */
    private String stock = null;
    /**
     * Additional path info. <BR/>
     */
    private String path = null;
    /**
     * If product is packed in assortments for ordering. <BR/>
     * 0.. no, 1.. yes
     */
    private int hasAssortment = 0;
    /**
     * The oid of the brand name. <BR/>
     */
    private OID brandNameOid = null;
    /**
     * The brand name of the product. <BR/>
     */
/*
    private String brandName = null;
*/
    /**
     * The brand name of the product. <BR/>
     */
    private String brandImage = null;
    /**
     * If product uses self defined keys. <BR/>
     */
    private int predefinedKeys = 0;
    /**
     * If product has already been created. <BR/>
     */
    private int created = 0;

    //------------- Attributes for the property lists
    /**
     * The oid of the productProfile. <BR/>
     */
    private OID productProfileOid = null;
    /**
     * An array of all product codes (color, size etc.) used in this product. <BR/>
     */
    private ProductCode[] codes = null;

    /**
     * Array of product codes with contains at least two values<BR/>
     */
    private Vector<ProductCode> codesWithValues = new Vector<ProductCode> ();

    /**
     * Delimiter string for the properties list. <BR/>
     */
    private static final String DELIMITER = ";";

    //------------- Cost and price of the product
    /**
     * Oid of the price tab. <BR/>
     */
    private OID priceContainer = null;
    /**
     * The array holding the price info. <BR/>
     */
    private Vector<Price_01> prices = null;
    /**
     * The oid of the container holding the product collections. <BR/>
     */
    private OID collectionContainerOid = null;
    /**
     * The vector of the product collection object. <BR/>
     */
    private Vector<ProductCollection_01> productCollections = null;


    ///////////////////////////////////////////////////////////////////////////
    // constants
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Procedure to put the product in the shopping cart. <BR/>
     */
    private static final String PROC_CREATECARTENTRY =
        "p_Product_01$createCartEntry";

    /**
     * This icon name is used for the order window. Just wanted to save another
     * not necessary DB access. <BR/>
     */
    private static final String CONST_SHOPPINGCART_ICON = "ShoppingCartLine.gif";

    /**
     * ???. <BR/>
     */
    private int productDialogStep = 0;

    /**
     * ???. <BR/>
     */
    private static String[] ORDERFORMS =
    {
        "A", "B", "C", "D",
    };

    /**
     * ???. <BR/>
     */
    private String orderForm = null;

    /**
     * ???. <BR/>
     */
    private Vector<String> currencies = null;

    /**
     * ???. <BR/>
     */
    private String successMessage = null;


    /**
     * indicates if new form was called because extended formfooter was shown and
     * the checkbox save_and_new was selected.
     */
    private boolean resetObject = false;

    /**
     * Name of the table. <BR/>
     */
    private static final String TABLE_NAME = "m2_Product_01";

    /**
     * Field name: product number. <BR/>
     */
    private static final String FIELD_PRODUCTNO = "productNo";
    /**
     * Field name: EAN code. <BR/>
     */
    private static final String FIELD_EAN_CODE = "ean";
    /**
     * Field name: available from date. <BR/>
     */
    private static final String FIELD_AVAILABLEFROM = "availableFrom";
    /**
     * Field name: unit of quantity. <BR/>
     */
    private static final String FIELD_UNITOFQUANTITY = "unitOfQty";
    /**
     * Field name: packing unit. <BR/>
     */
    private static final String FIELD_PACKINGUNIT = "packingUnit";
    /**
     * Field name: stock. <BR/>
     */
    private static final String FIELD_STOCK = "stock";


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * This constructor creates a new instance of the class product.
     * <BR/>
     */
    public Product_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // set tablename for extended search
        this.tableName = Product_01.TABLE_NAME;
        // initialize instance variables
        this.prices = new Vector<Price_01> (3);
        // Initialize the packingUnit with the correct ML token
        this.packingUnit = MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
            StoreTokens.ML_CONST_DEFAULTPACKINGUNIT, env);
    } // Product_01


    /**************************************************************************
     * This constructor creates a new instance of the class product.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public Product_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
        // set extended search flag
        this.searchExtended = true;
        // initialize instance variables
        this.prices = new Vector<Price_01> (3);
        // Initialize the packingUnit with the correct ML token
        this.packingUnit = MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
            StoreTokens.ML_CONST_DEFAULTPACKINGUNIT, env);
    } // Product_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // initialize the instance's private properties:
        this.procCreate =     "p_Product_01$create";
        this.procChange =     "p_Product_01$change";
        this.procRetrieve =   "p_Product_01$retrieve";
        this.showExtendedCreationMenu = false;

        // set extended search flag
        this.searchExtended = true;
        // set tablename for extended search
        this.tableName = Product_01.TABLE_NAME;

        // set number of parameters for procedure calls:
        this.specificRetrieveParameters = 18;
        this.specificChangeParameters = 14;
    } // initClassSpecifics


    /**************************************************************************
     * This method resets the object properties to make sure that the new object
     * is empty. <BR/>
     */
    private void resetDialogStep ()
    {
        // initialize the instance properties
        this.created = 0;
        this.codes = null;
        this.prices = null;
        this.productCollections = null;
        this.productDialogStep = 0;
        this.resetObject = false;
        this.prices = new Vector<Price_01> (3);
    } // initObject



    ///////////////////////////////////////////////////////////////////////////
    // functions called from application level
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Sets the object actual. <BR/>
     * This method sets the object actual. <BR/>
     */
    protected void setActual ()
    {
        // nothing to do
    } // setActual


    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     * The <A HREF="ibs.bo.BusinessObject.html#env">env</A> property is used
     * for getting the parameters. This property must be set before calling
     * this method. <BR/>
     */
    public void getParameters ()
    {
        OID oid = null;
        String str = "";
        int intval = 0;
        int k;
        Date date = null;

        super.getParameters ();

        // if last form was the order form processing is done in
        // putProductInShoppingCart
        if ((this.orderForm = this.env
            .getStringParam (StoreArguments.ARG_ORDERFORM)) != null)
        {
            return;
        } // if

        // the edit dialog for this class has three steps:
        //   1) choose the code categories
        //   2) optionaly: choose predefined keys
        //   3) ordinary change dialog
        if ((intval = this.env.getIntParam (StoreArguments.ARG_PRODUCTDIALOGSTEP)) !=
            IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            this.productDialogStep = intval;
        } // if


        // first step: get the product profile and some flags
        if (this.productDialogStep == 1)
        {
            // oid of productprofile
            if ((oid = this.env.getOidParam (StoreArguments.ARG_PRODUCTPROFILE)) != null)
            {
                this.productProfileOid = oid;
            } // if

            // product uses predifined keys
            if ((this.env.getBoolParam (StoreArguments.ARG_PREDEFINEDKEYS)) ==
                IOConstants.BOOLPARAM_TRUE)
            {
                this.predefinedKeys = 1;
            } // if

            // product containes assortments
            if ((this.env.getBoolParam (StoreArguments.ARG_HASASSORTMENT)) ==
                IOConstants.BOOLPARAM_TRUE)
            {
                this.hasAssortment = 1;
            } // if
        // second step: get the product info
        } // if
        else if (this.productDialogStep == 2)
        {
            int nrCodes = 0;
            if ((nrCodes = this.env.getIntParam (StoreArguments.ARG_NRCODES)) !=
                                                     IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
            {
                this.codes = new ProductCode[nrCodes];
                k = 0;
                while (k < nrCodes)
                {
                    this.codes[k] = new ProductCode ();
                    this.codes[k].predefinedCodeOid = this.env.getOidParam (StoreArguments.ARG_CODE + k);
                    this.codes[k].categoryOid = this.env.getOidParam (StoreArguments.ARG_CATEGORYOID + k);
                    k++;
                } // while
            } // if
        } // else if
        // third step: ordinary change dialog for a product
        else if (this.productDialogStep == 3)
        {
            // special Product_01 parameters
            // product number
            if ((str = this.env.getStringParam (StoreArguments.ARG_PRODUCTNO)) != null)
            {
                this.productNo = str;
            } // if

            // ean code
            if ((str = this.env.getStringParam (StoreArguments.ARG_EAN)) != null)
            {
                this.ean = str;
            } // if
            // full product description
            if ((str = this.env.getStringParam (StoreArguments.ARG_PRODUCTDESCRIPTION)) != null)
            {
                this.fullDescription = str;
                // convert long product description to short description wich is shown in productgroup
                if (this.fullDescription.length () > 250)
                {
                    this.description = str.substring (0, 250) + " ...";
                } // if
                else
                {
                    this.description = str;
                } // else
            } // if

            // available-from date
            if ((date = this.env.getDateParam (StoreArguments.ARG_AVAILABLEFROM)) != null)
            {
                this.availableFrom = date;
            } // if

            // unit of Quantity
            if ((intval = this.env.getIntParam (StoreArguments.ARG_UNITOFQTY)) != IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
            {
                this.unitOfQty = intval;
            } // if

            // packagingUnit
            if ((str = this.env.getStringParam (StoreArguments.ARG_PACKINGUNIT)) != null)
            {
                this.packingUnit = str;
            } // if

            // check box thumb as image
            this.thumbAsImage =
                this.env.getBoolParam (StoreArguments.ARG_THUMBASIMAGE) ==
                IOConstants.BOOLPARAM_TRUE;
            // thumb nail and path
            if ((str = this.getFileParamBO (StoreArguments.ARG_THUMBNAIL)) != null)
            {
                this.thumbnail = str;
                this.path = "" + this.oid;
            } // if

            // image and path
            if ((str = this.getFileParamBO (StoreArguments.ARG_IMAGE)) != null)
            {
                this.image = str;
                this.path = "" + this.oid;
            } // if

            // stock
            if ((str = this.env.getStringParam (StoreArguments.ARG_STOCK)) != null)
            {
                this.stock = str;
            } // if

            // oid of brand
            if ((oid = this.env.getOidParam (StoreArguments.ARG_PRODUCTBRAND)) != null)
            {
                this.brandNameOid = oid;
            } // if


            // get the parameters for the product properties
            int nrCodes = 0;
            // how many codes
            if ((nrCodes = this.env.getIntParam (StoreArguments.ARG_NRCODES)) !=
                IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
            {
                this.codes = new ProductCode[nrCodes];
                for (k = 0; k < nrCodes; k++)
                {
                    this.codes[k] = new ProductCode ();
                    this.codes[k].categoryOid = this.env.getOidParam (StoreArguments.ARG_CATEGORYOID + k);
                    // if the values where self defined
                    if ((this.env.getBoolParam (StoreArguments.ARG_USERDEFINED_PREFIX + k)) ==
                        IOConstants.BOOLPARAM_TRUE)
                    {
                        str = this.env.getStringParam (StoreArguments.ARG_CODEX + k);
                        if (str != null)
                        {
                            // parse the string an dput values in array
                            this.codes[k].parseStringToValues (str, "\015\012");
                        } // if
                    } // if
                    // predefined codes were used
                    else
                    {
                        this.codes[k].values =
                            this.env.getMultipleFormParam (StoreArguments.ARG_CODEX + k);
                    } // else
                } // for (int i = 0; i < nrCodes; i++)
            } // if ((nrCodes = ..


            // Object is not just created - try to read checkboxes from extended footer
            String newObjectAction;
            // checkbox in extended formfooter
            newObjectAction = this.env.getParam (BOConstants.NEW_BUSINESS_OBJECT_MENU);
            if (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SAVE_AND_NEW, env).equals (newObjectAction))
                // checkbox SAVEANDNEW is checked
            {
                this.resetObject = true;
            } // if
        } // else third step: ordinary change dialog for a product
    } // getParameters

    /**************************************************************************
     * Put the quantities and product descriptions from the order into the
     * shopping cart.
     */
    public void putProductInShoppingCart ()
    {
//debug ("AJ Product_01.putProductInShoppingCart ANFANG");
        int unitOfQty;
        String productName;
        String packingUnit;
        String currency;
        boolean letWindowOpen = false;

        this.successMessage = MultilingualTextProvider.getMessage (StoreMessages.MSG_BUNDLE,
            StoreMessages.ML_MSG_NOT_PUTINCART, env);
        productName = this.env.getStringParam (BOArguments.ARG_NAME);
        unitOfQty = this.env.getIntParam (StoreArguments.ARG_UNITOFQTY);
        packingUnit = this.env.getStringParam (StoreArguments.ARG_PACKINGUNIT);
        currency = this.env.getStringParam (StoreArguments.ARG_ORDERCURRENCY);

        // if the order form was the simple one
        if (Product_01.ORDERFORMS[0].equals (this.orderForm))
        {
            letWindowOpen = this.processSimpleOrderForm (productName,
                unitOfQty, packingUnit, currency);
        } // if
        // if the order form was the matrix
        else if (Product_01.ORDERFORMS[1].equals (this.orderForm))
        {
            letWindowOpen = this.processMatrixOrderForm (productName,
                unitOfQty, packingUnit, currency);
        } // else if
        // if the order form was the one with the selection boxes
        else if (Product_01.ORDERFORMS[2].equals (this.orderForm))
        {
            letWindowOpen = this.processSelectionOrderForm (productName,
                unitOfQty, packingUnit, currency);
        } // else if
        else if (Product_01.ORDERFORMS[3].equals (this.orderForm))
        {
            letWindowOpen = this.processCollectionOrderForm (productName,
                unitOfQty, packingUnit);
        } // else if

        // create the answering page with the alert message
        Page page = new Page (
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_PUTIN_CART, env), false);
        ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);

        // put in a script to show success message and close the window
        if (letWindowOpen)
        {
            script.addScript ("alert (\"" + this.successMessage + "\");\n");
        } // if
        else
        {
            if (this.successMessage == null)
            {
                script.addScript ("top.close ();");
            } // if
            else
            {
                script.addScript ("alert (\"" + this.successMessage + "\");\n" +
                    "top.close ();");
            } // else
        } // else


        page.body.addElement (script);

        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
        if (letWindowOpen)
        {
            this.performShowOrderForm ();
        } // if

//debug ("AJ Product_01.putProductInShoppingCart ENDE");
    } // putProductInShoppingCart


    ///////////////////////////////////////////////////////////////////////////
    // viewing functions
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Show the object, i.e. its properties within a form. <BR/>
     * The properties are gotten from the database (with rights checked) and
     * represented to the user in the required form. <BR/>
     *
     * @param   representationForm  Kind of representation.
     *
     * @return  <CODE>true</CODE> if the change form or its frameset was
     *          displayed, <CODE>false</CODE> otherwise.
     */
    public boolean showChangeForm (int representationForm)
    {
        if (true)                       // business object is on this server
        {
            if (this.showChangeFormAsFrameset && this.framesetPossible)
                                        // show as frameset?
            {
                // create a frameset to show the actual view within:
                this.showFrameset (representationForm, AppFunctions.FCT_OBJECTCHANGEFORM);
            } // if show as frameset
            else                        // don't show as frameset
            {
                if (this.state == States.ST_CREATED) // object was just created
                {
                    this.performGetRightsContainerData (this.oid, this.user);
                } // if
                if (this.getContainerRights ().checkObjectPermissions (
                    Operations.OP_SETRIGHTS))
                {
                    this.canSetRights = true;
                } // if object was just created
                if ((this.created == 1) ||
                    ((this.productDialogStep == 1) && (this.predefinedKeys == 0)) ||
                    (this.productDialogStep == 2))
                {
                    // show the other change forms
                    this.performShowChangeForm (representationForm,
                        AppFunctions.FCT_OBJECTCHANGE);
                } // if
                else
                {
                    // show the usual change form
                    this.performShowChangeForm (representationForm,
                        AppFunctions.FCT_OBJECTCHANGE_CHANGEFORM);
                } // else

                this.framesetPossible = true; // frameset view is possible again
            } // else don't show as frameset
        } // if business object resists on this server
        else                            // object resists on another server
        {
            // invoke the object on the other server:
        } // else object resists on another server

        return true;
    } // showChangeForm


    /**************************************************************************
     * Represent the properties of a Product_01 object to the user. <BR/>
     *
     * @param   table   Table where the properties shall be added.
     *
     * @see BusinessObject#showProperties
     * @see ibs.IbsObject#showProperty (ibs.tech.html.TableElement, java.lang.String, java.lang.String, int, ibs.service.user.User, java.util.Date)
     */
    protected void showProperties (TableElement table)
    {
        int numProperties = 12;         // number of properties to be shown
                                        // used for image rowspan
                                        // rowspan = 0 doesn't work properly

        Font nameFont = new Font (AppConstants.FONT_NAME, AppConstants.FONTSIZE_NAME);
                                        // Font for name of property
        nameFont.bold = true;
        Font valueFont = new Font (AppConstants.FONT_VALUE, AppConstants.FONTSIZE_VALUE);
                                        // Font for value of property
        TableDataElement tde;
        RowElement row;
        TextElement text;
        GroupElement group;
        ImageElement ie;


        text = new TextElement (
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_PRODUCTNAME, env) + ": ");
        text.font = nameFont;
        row = new RowElement (3);
        row.addElement (new TableDataElement (text));

        // show the name
        text = new TextElement ("" + this.name);
        text.font = valueFont;
        group = new GroupElement ();
        group.addElement (text);
        group.addElement (new InputElement (BOArguments.ARG_NAME, InputElement.INP_HIDDEN, "" + this.name));
        row.addElement (new TableDataElement (text));

        // add the image to the row
        GroupElement brandPlusProduct = new GroupElement ();
        if (this.brandImage != null && this.brandImage.trim ().length () > 0)
        {
            ie = new ImageElement (this.sess.home +
                BOPathConstants.PATH_UPLOAD_PICTURES + this.brandImage);

            ie.alt = this.name;
            brandPlusProduct.addElement (ie);
        } // if
        tde = new TableDataElement (brandPlusProduct);

        if (this.image != null && this.image.trim ().length () > 0)
        {
            if (this.path != null && this.path.trim ().length () > 0)
            {
                ie = new ImageElement (this.sess.home +
                    BOPathConstants.PATH_UPLOAD_PICTURES + this.path + "/" +
                    this.image);
            } // if
            else
            {
                ie = new ImageElement (this.sess.home +
                    BOPathConstants.PATH_UPLOAD_PICTURES + this.image);
            } // else
            ie.alt = this.name;
            ie.width = "" + StoreConstants.CONST_WIDTH_IMAGE;
            brandPlusProduct.addElement (ie);
        } // if

        tde.width = "" + (StoreConstants.CONST_WIDTH_IMAGE + 20);
        tde.rowspan = numProperties;
        tde.valign = IOConstants.ALIGN_TOP;
        tde.alignment = IOConstants.ALIGN_CENTER;
        row.addElement (tde);
        row.classId = BOListConstants.LST_CLASSINFOROWS[1];

        table.addElement (row);

        // show the product no

        this.showProperty (table, StoreArguments.ARG_PRODUCTNO,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_PRODUCTNO, env),
            Datatypes.DT_TEXT, "" + this.productNo);

        this.showProperty (table, BOArguments.ARG_INNEWS, 
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                BOTokens.ML_INNEWS, env),
            Datatypes.DT_BOOL, "" + this.showInNews);
        // show the full description of the product
        this.showProperty (table, BOArguments.ARG_DESCRIPTION,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                BOTokens.ML_DESCRIPTION, env), Datatypes.DT_DESCRIPTION,
            this.fullDescription);

        if (this.getUserInfo ().userProfile.showExtendedAttributes)
        {
            // separator
            this.showProperty (table, null, null, Datatypes.DT_SEPARATOR, (String) null);

            // owner
            this.showProperty (table, BOArguments.ARG_OWNER,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                    BOTokens.ML_OWNER, env), Datatypes.DT_USER, this.owner);
            // created
            this.showProperty (table, BOArguments.ARG_CREATED,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                    BOTokens.ML_CREATED, env), Datatypes.DT_USERDATE, this.creator,
                this.creationDate);
            // changed
            this.showProperty (table, BOArguments.ARG_CHANGED,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                    BOTokens.ML_CHANGED, env), Datatypes.DT_USERDATE, this.changer,
                this.lastChanged);
            // separator
            this.showProperty (table, null, null, Datatypes.DT_SEPARATOR, (String) null);
        } // if (app.userInfo.userProfile.showExtendedAttributes)

        // show the ean
        this.showProperty (table, StoreArguments.ARG_EAN, 
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_EAN, env),
            Datatypes.DT_TEXT, "" + this.ean);
        // show the date from which the product is available
        this.showProperty (table, StoreArguments.ARG_AVAILABLEFROM,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_AVAILABLEFROM, env),
            Datatypes.DT_DATE, this.availableFrom);
        // show the date until the offer is valid
        this.showProperty (table, BOArguments.ARG_VALIDUNTIL,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                BOTokens.ML_VALIDUNTIL, env),
            Datatypes.DT_DATE, this.validUntil);

        // show the amount of products on stock
        this.showProperty (table, StoreArguments.ARG_STOCK, 
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_STOCK, env),
            Datatypes.DT_TEXT, "" + this.stock);

        if (this.codes != null)
        {
            for (int j = 0; j < this.codes.length; j++)
            {
                this.showProperty (table, StoreArguments.ARG_CODEX,
                    this.codes[j].name, Datatypes.DT_TEXT, "" +
                        this.codes[j].toString (", "));
            } // for j
        } // if
        if (this.hasAssortment == 1)
        {
            this.showProperty (table, BOArguments.ARG_NOARG,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_COLLECTIONS, env),
                Datatypes.DT_NAME, this.showProductCollections ());
        } // if
        else
        {
            // show the unit of quantity
            this.showProperty (table, StoreArguments.ARG_UNITOFQTY,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_UNITOFQTY, env),
                Datatypes.DT_TEXT, "" + this.unitOfQty);
            // show the type of unit
            this.showProperty (table, StoreArguments.ARG_PACKINGUNIT,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_PACKINGUNIT, env),
                Datatypes.DT_TEXT, "" + this.packingUnit);
            // show the prices
            this.showProperty (table, BOArguments.ARG_NOARG, 
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_PRICE, env),
                Datatypes.DT_NAME, this.showPrices ());
        } // else
    } // showProperties


    /***************************************************************************
     * Add a hidden field to the current object. <BR/>
     *
     * @param hiddenFields The layout element to add the hidden field.
     * @param argument The name of the hidden field.
     * @param value The value of the hidden field.
     */
    private void addHiddenField (GroupElement hiddenFields, String argument, String value)
    {
        hiddenFields.addElement (new InputElement (argument,
            InputElement.INP_HIDDEN, "" + value));
    } // addHiddenField


    /**************************************************************************
     * Add the hidden fields to the current page. <BR/>
     *
     * @param   hiddenFields The layout element containing the hidden fields.
     * @param   form        The form object.
     *
     * @deprecated  This method shall not longer be used.
     */
    private void addHiddenFieldsToForm (GroupElement hiddenFields, FormElement form)
    {
        if (form != null)
        {
            form.addElement (hiddenFields);
        } // if
    } // addHiddenFieldsToForm


    /**************************************************************************
     * Represent the properties of a Product_01 object to the user
     * within a form. <BR/>
     *
     * @param   table   Table where the properties shall be added.
     *
     * @see BusinessObject#showFormProperties
     * @see ibs.IbsObject#showFormProperty (ibs.tech.html.TableElement, java.lang.String, java.lang.String, java.lang.String[], int, int, int, java.lang.String)
     */
    protected void showFormProperties (TableElement table)
    {
        Font nameFont = new Font (AppConstants.FONT_NAME, AppConstants.FONTSIZE_NAME);
                                        // Font for name of property
        nameFont.bold = true;
        Font valueFont = new Font (AppConstants.FONT_VALUE, AppConstants.FONTSIZE_VALUE);
                                        // Font for value of property
        GroupElement hiddenFields = new GroupElement ();
                                        // a Vector holding the hidden fields
                                        // of the object

        // dialogstep must be set to 0 if checkbox saveAndNew was selected in
        // extended formfooter (see getParameters)
        if (this.resetObject)
        {
            this.resetDialogStep ();
        } // if

        // if product has not been created yet and its the first dialog step
        if ((this.created == 0) && (this.productDialogStep == 0))
        {

            // name
            this.showFormProperty (table, BOArguments.ARG_NAME,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_PRODUCTNAME, env),
                Datatypes.DT_NAME, this.name);
            // display the object in the news
            this.showFormProperty (table, BOArguments.ARG_INNEWS,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                    BOTokens.ML_INNEWS, env), 
                Datatypes.DT_BOOL, "" + this.showInNews);
            // show a selecton of product profiles
            SelectionList profileSelList = this.performRetrieveSelectionListData (
                this.getTypeCache ().getTVersionId (StoreTypeConstants.TC_ProductProfile), true);
            // (index of oid: 1)
            this.showFormProperty (table, StoreArguments.ARG_PRODUCTPROFILE,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_PRODUCTPROFILE, env),
                Datatypes.DT_SELECT, "" + this.productProfileOid, profileSelList.ids,
                profileSelList.values, 1);
            // if product has an assortment
            this.showFormProperty (table, StoreArguments.ARG_HASASSORTMENT,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_HASASSORTMENT, env), 
                Datatypes.DT_BOOL, "" + false);
            // thumb as image flag
            this.showFormProperty (table, StoreArguments.ARG_PREDEFINEDKEYS,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_PREDEFINEDKEYS, env),
                Datatypes.DT_BOOL, "" + false);
            this.addHiddenField (hiddenFields,
                StoreArguments.ARG_PRODUCTDIALOGSTEP, "1");
        } // if ((this.created == 0)
        // the second dialog step (optional)
        else if ((this.predefinedKeys == 1) && (this.created == 0) && (this.productDialogStep == 1))
        {
            // name
            this.showFormProperty (table, BOArguments.ARG_NAME,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_PRODUCTNAME, env),
                Datatypes.DT_NAME, this.name);

            if (this.codes != null)
            {
                int nrCodes = this.codes.length;
                SelectionList sl;
                StringBuffer queryStr;  // the query

                // add the number of following codes as hidden field
                this.addHiddenField (hiddenFields, StoreArguments.ARG_NRCODES,
                    "" + nrCodes);
                for (int i = 0; i < nrCodes; i++)
                {
                    queryStr = new StringBuffer ()
                        .append ("SELECT distinct oid, name ")
                        .append (" FROM v_PredefinedCodes$selList")
                        .append (" WHERE categoryOid = ")
                            .append (this.codes[i].categoryOid.toStringQu ())
                        .append (" AND state = ").append (States.ST_ACTIVE);
                    sl = this.performRetrieveSelectionListDataQuery (true,
                        queryStr, 
                        MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                            StoreTokens.ML_SELFDEFINEDKEY, env));
                    // show in a selection
                    // (index of oid: 0)
                    this.showFormProperty (table, StoreArguments.ARG_CODE + i,
                        this.codes[i].name, Datatypes.DT_SELECT, "" +
                            this.codes[i].predefinedCodeOid, sl.ids, sl.values,
                        0);
                    // put the the oid of the category as a hidden
                    // field in the form
                    this.addHiddenField (hiddenFields,
                        StoreArguments.ARG_CATEGORYOID + i, "" +
                            this.codes[i].categoryOid);
                } // for
            } // if (this.codes ! = null)
            this.addHiddenField (hiddenFields,
                StoreArguments.ARG_PRODUCTDIALOGSTEP, "2");
        } // else if ((this.created == 0)
        else
        {
            // product dialogstep 3
            // third dialog step  (last step)
            // name
            this.showFormProperty (table, BOArguments.ARG_NAME,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_PRODUCTNAME, env),
                Datatypes.DT_NAME, this.name);
            // display the object in the news
            this.showFormProperty (table, BOArguments.ARG_INNEWS,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                    BOTokens.ML_INNEWS, env), 
                Datatypes.DT_BOOL, "" + this.showInNews);
            // productbrand
            SelectionList brandNameSelList = this
                .performRetrieveSelectionListData (this.getTypeCache ()
                    .getTVersionId (StoreTypeConstants.TC_ProductBrand), true);

            // (0: // index of preselected if preselected value (brandNameOid)
            // is not set)
            this.showFormProperty (table, StoreArguments.ARG_PRODUCTBRAND,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_PRODUCTBRAND, env),
                Datatypes.DT_SELECT, "" + this.brandNameOid, brandNameSelList.ids,
                brandNameSelList.values, 0);


            // product no
            this.showFormProperty (table, StoreArguments.ARG_PRODUCTNO,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_PRODUCTNO, env),
                Datatypes.DT_TEXT, this.productNo);
            // description
            this.showFormProperty (table,
                StoreArguments.ARG_PRODUCTDESCRIPTION,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                    BOTokens.ML_DESCRIPTION, env),
                Datatypes.DT_TEXTAREA, this.fullDescription);
            // ean
            this.showFormProperty (table, StoreArguments.ARG_EAN,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_EAN, env), 
                Datatypes.DT_TEXT, this.ean);
            // available from
            this.showFormProperty (table, StoreArguments.ARG_AVAILABLEFROM,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_AVAILABLEFROM, env),
                Datatypes.DT_DATE, this.availableFrom);
            // valid until
            this.showFormProperty (table, BOArguments.ARG_VALIDUNTIL,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                    BOTokens.ML_VALIDUNTIL, env), 
                Datatypes.DT_DATE, this.validUntil);
            // propose the collection token
            if (this.hasAssortment != 1)
            {
                // packing unit
                this.showFormProperty (table, StoreArguments.ARG_PACKINGUNIT,
                    MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                        StoreTokens.ML_PACKINGUNIT, env),
                    Datatypes.DT_TEXT, this.packingUnit);
                // unit of product
                this.showFormProperty (table, StoreArguments.ARG_UNITOFQTY,
                    MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                        StoreTokens.ML_UNITOFQTY, env),
                    Datatypes.DT_INTEGER, this.unitOfQty);
            } // if

            // picture file
            this.showFormProperty (table, StoreArguments.ARG_IMAGE,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_IMAGE, env),
                Datatypes.DT_PICTURE, this.image, "" + this.oid);
            // thumbnail image flag
            this.showFormProperty (table, StoreArguments.ARG_THUMBASIMAGE,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_THUMBASIMAGE, env),
                Datatypes.DT_BOOL, "" + this.thumbAsImage);
            // thumbnail file
            this.showFormProperty (table, StoreArguments.ARG_THUMBNAIL,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_THUMBNAIL, env), 
                Datatypes.DT_THUMBNAIL, this.thumbnail, "" + this.oid);

            // stock quantity of product
            this.showFormProperty (table, StoreArguments.ARG_STOCK,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_STOCK, env),
                Datatypes.DT_TEXT, this.stock);

            // add the dialog step as hidden field
            this.addHiddenField (hiddenFields,
                StoreArguments.ARG_PRODUCTDIALOGSTEP, "3");

            // show the product codes
            if (this.codes != null)
            {
                int nrCodes = this.codes.length;
                // put the number of codes as a hidden field in the form
                this.addHiddenField (hiddenFields, StoreArguments.ARG_NRCODES,
                    "" + nrCodes);
                for (int i = 0; i < nrCodes; i++)
                {
                    // put the the oid of the category as a hidden
                    // field in the form
                    this.addHiddenField (hiddenFields,
                        StoreArguments.ARG_CATEGORYOID + i, "" +
                            this.codes[i].categoryOid);
                    // show the codes selection
                    if (this.codes[i].selfDefined)
                    {
                        this.showFormProperty (table, StoreArguments.ARG_CODEX +
                            i, this.codes[i].name, Datatypes.DT_DESCRIPTION,
                            this.codes[i].toString ("\n"));

                        // set the hidden flag to indicate that the user has
                        // chosen
                        // to define the keys on his own
                        this.addHiddenField (hiddenFields,
                            StoreArguments.ARG_USERDEFINED_PREFIX + i,
                            Datatypes.BOOL_TRUE);
                    } // if (this.codes[i].selfDefined)
                    else
                    {
                        this.codes[i]
                            .showFormProperty (
                                table,
                                StoreArguments.ARG_CODEX + i,
                                nameFont,
                                valueFont,
                                BOListConstants.LST_CLASSINFOROWS[this.properties++ %
                                    BOListConstants.LST_CLASSINFOROWS.length]);
                    } // else
                } // for (int i = 0; i < nrCodes; i++)

            } // if (this.codes ! = null)

            this.showExtendedCreationMenu = true;
        } // else
    } // showFormProperties


    /**************************************************************************
     * Load the frame set of the order form. <BR/>
     */
    public void loadOrderFrameSet ()
    {
        Page page = new Page (
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_PUTIN_CART, env), true);

        ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);
//        script.addScript ("var iI = window.opener.top.iI;");
        script.addScript ("function iI (a, b) {return window.opener.top.fVWiI (a, b, top);}");
        page.body.addElement (script);

        String upperFrameURL = AppConstants.FILE_EMPTYPAGE;
        // URL for lower frame - load upload activeX
        String lowerFrameURL =
            this.getBaseUrl () +
//            AppConstants.URL_GET +   AppArguments.ARG_BEGIN +
            HttpArguments.createArg (BOArguments.ARG_FUNCTION, StoreFunctions.FCT_SHOWORDER_FORM) +
            HttpArguments.createArg (BOArguments.ARG_OID, this.oid.toString ());

        // build page with frameset and frames
        FrameElement upperFrame =
            new FrameElement (StoreConstants.FRM_ORDER_UP, upperFrameURL);
        upperFrame.frameborder = true;
        upperFrame.resize = false;
        FrameElement lowerFrame =
            new FrameElement (StoreConstants.FRM_ORDER_DOWN, lowerFrameURL);
        lowerFrame.frameborder = true;
        lowerFrame.resize = true;
        FrameSetElement frameSet = new FrameSetElement (true, 2);

        frameSet.frameborder = false;
        frameSet.frameSpacing = 0;
        frameSet.addElement (upperFrame, "0"); // invisible frame
        frameSet.addElement (lowerFrame, "*"); // full page
        page.body.addElement (frameSet);

        // build the page and show it to the user:
        try
        {
            page.build (this.env);
            page = null;
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
    } // loadOrderFrameSet


    /**************************************************************************
     * check if product has prices or sortiments and show message and formfooter
     * if there are no prices or sortiments. <BR/>
     *
     * @return  if hasAssortments = true but Product has no assortments -> false
     *          if hasAssortments = false and Product has no prices -> false
     *          otherwise -> true
     */
    private boolean checkShowOrderForm ()
    {
        // check if it product has one or more sortiments or one or more prices
        if (this.hasAssortment == 1)
            // product shall be orderd in assortments
        {
            if (!(this.productCollections != null && this.productCollections.size () > 0))
            {   // product has no collections
                IOHelpers.showMessage (
                    MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                        BOMessages.ML_MSG_NOASSORTMENTS, this.env),
                    this.app, this.sess, this.env);
                return false;
            } // if
        } // if
        else
        {
            if (!(this.prices != null && this.prices.size () > 0))
            {
                IOHelpers.showMessage (
                    MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                        BOMessages.ML_MSG_NOPRICES, this.env),
                    this.app, this.sess, this.env);
                return false;
            } // if
        } // else
        return true;
    } // checkShowOrderForm


    /**************************************************************************
     * Show the order form (put-in-shoppingcart form). <BR/>
     */
    public void showOrderForm ()
    {
        try
        {
            this.retrieve (Operations.OP_READ);
        } // try
        catch (NoAccessException e) // no access to objects allowed
        {
            // send message to the user:
            this.showNoAccessMessage (Operations.OP_READ);
        } // catch
        catch (AlreadyDeletedException e)
        {
            this.showAlreadyDeletedMessage ();
        } // catch
        catch (ObjectNotFoundException e)
        {
            // send message to the user:
            this.showObjectNotFoundMessage ();
        } // catch


        if (this.checkShowOrderForm ())
        {
            this.performShowOrderForm ();
        } // if
    } // showOrderForm


    /**************************************************************************
     * Show the order form (put-in-shoppingcart form). <BR/>
     */
    public void performShowOrderForm ()
    {
        GroupElement hiddenFields = new GroupElement ();
                                        // a Vector holding the hidden fields
                                        // of the object
        Page page = new Page (
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

/*
        FormElement form =
            createFormHeader (page, this.name, getNavItems (), null,
                StoreTokens.TOK_PUTIN_CART, StoreConstants.FRM_ORDER_DOWN,
                CONST_SHOPPINGCART_ICON, this.containerName);
*/
        // do not show object path in header!!
        this.dropObjectPath ();

        // create Header with object (form)name but without path and without
        // nav items
        FormElement form = this.createFormHeader (page, this.name, null, null,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_PUTIN_CART, env), StoreConstants.FRM_ORDER_DOWN,
            Product_01.CONST_SHOPPINGCART_ICON, this.containerName);


        TableElement table = this.createFrame (0, 0);
        table.cellpadding = 5;
        table.border = 0;

        table.classId = CssConstants.CLASS_INFO;
        String[] classIds = new String[2];
        classIds[0] = CssConstants.CLASS_NAME;
        classIds[1] = CssConstants.CLASS_VALUE;
        table.classIds = classIds;

        RowElement tr;

        // start with the object representation: show header
        form.addElement (new InputElement (BOArguments.ARG_OID, InputElement.INP_HIDDEN, "" + this.oid));
        form.addElement (new InputElement (BOArguments.ARG_FUNCTION, InputElement.INP_HIDDEN, "" + StoreFunctions.FCT_PUTIN_CART));


        // show the name of the product
        this.showProperty (table, BOArguments.ARG_NAME,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_PRODUCTNAME, env),
            Datatypes.DT_NAME, this.name);
        // show the unit of quantity
        this.showProperty (table, StoreArguments.ARG_UNITOFQTY,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_UNITOFQTY, env),
            Datatypes.DT_TEXT, "" + this.unitOfQty);
        // show the type of unit
        this.showProperty (table, StoreArguments.ARG_PACKINGUNIT,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_PACKINGUNIT, env),
            Datatypes.DT_TEXT, "" + this.packingUnit);
        // show the selection for the currency

        if (this.currencies != null)
        {
            int size = this.currencies.size ();
            String [] currencyValues = new String[size];
            for (int i = 0; i < size; i++)
            {
                currencyValues[i] = this.currencies.elementAt (i);
            } // for i
            if (size > 0)
            {
                // (index of oid: 1)
                this.showFormProperty (table, StoreArguments.ARG_ORDERCURRENCY,
                    MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                        StoreTokens.ML_ORDERCURRENCY, env),
                    Datatypes.DT_SELECT, "", currencyValues, currencyValues, 1);
            } // if
        } // if
        form.addElement (table);

        // table
        table = this.createFrame (0, 0);
        table.cellpadding = 5;
        table.border = 0;
        table.classId = CssConstants.CLASS_INFO;
        //String[] classIds = new String[2];
        classIds[0] = CssConstants.CLASS_NAME;
        classIds[1] = CssConstants.CLASS_VALUE;
        table.classIds = classIds;


        tr = new RowElement (2);
        TableDataElement td = new TableDataElement (new BlankElement ());
        td.colspan = 2;
        tr.addElement (td);
        table.addElement (tr);

        // if product is ordered in collections show special form
        if (this.hasAssortment == 1)
        {
            this.addHiddenField (hiddenFields, StoreArguments.ARG_ORDERFORM,
                            "" + Product_01.ORDERFORMS[3]);
            // show only a simple input field for the quantity
            this.showOrderCollectionsForm (table);
        } // if
        else if (this.codesWithValues.size () <= 0)
        {
            this.addHiddenField (hiddenFields, StoreArguments.ARG_ORDERFORM,
                 "" + Product_01.ORDERFORMS[0]);
            // show only a simple input field for the quantity
            this.showOrderSimpleForm (table);
        } // else if
        else if (this.codesWithValues.size () > 0 && this.codesWithValues.size () <= 2)
        {
            this.addHiddenField (hiddenFields, StoreArguments.ARG_ORDERFORM,
                 "" + Product_01.ORDERFORMS[1]);
            // show the order matrix if not more than
            // two keys are used
            this.showOrderMatrixForm (table);
        } // else if
        else
        {
            this.addHiddenField (hiddenFields, StoreArguments.ARG_ORDERFORM,
                 "" + Product_01.ORDERFORMS[2]);
            // else show a form with selection boxes for every
            // key value
            this.showOrderSelectionForm (table);
        } // else

        form.addElement (table);
        // set ok action to
        this.createFormFooter (form, null, "top.close ();");
        // add the hidden fields en bloc in the form
        this.addHiddenFieldsToForm (hiddenFields, form);
        // build the page and show it to the user:
        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
    } // performShowOrderForm


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
        // set the specific parameters:
        // productNo
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.productNo);
        // ean
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.ean);
        // availableFrom
        sp.addInParameter (ParameterConstants.TYPE_DATE, this.availableFrom);
        // unitOfQty
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.unitOfQty);
        // packingUnit
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.packingUnit);
        // thumbAsImage
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN, this.thumbAsImage);
        // thumbnail
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.thumbnail);
        // image
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.image);
        // path
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.path);
        // stock
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.stock);
        // productDialogStep
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.productDialogStep);
        // productProfileOid
        BOHelpers.addInParameter (sp, this.productProfileOid);
        //brandNameOid
        BOHelpers.addInParameter (sp, this.brandNameOid);
        // hasAssortment
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.hasAssortment);
    } // setSpecificChangeParameters


    /**************************************************************************
     * Change all type specific data that is not changed by performChangeData.
     * <BR/>
     * This method must be overwritten by all subclasses that have to change
     * type specific data.
     *
     * @param   action  The database connection object.
     *
     * @exception    DBError
     *               This exception is always thrown, if there happens an error
     *               during accessing data.
     */
    protected void performChangeSpecificData (SQLAction action) throws DBError
    {
        // update the product code values and selected predefined keys
        if ((this.productDialogStep == 2) && (this.codes != null))
        {
            int nrCodes = this.codes.length;
            String updateStr = "";
            try
            {
                action.beginTrans ();
                for (int j = 0; j < nrCodes; j++)
                {
                    updateStr =
                        " UPDATE m2_ProductCodeValues_01" +
                        " SET predefinedCodeOid = " + this.codes[j].predefinedCodeOid.toStringQu () +
                        " WHERE productOid = " + this.oid.toStringQu () +
                        " AND categoryOid = " + this.codes[j].categoryOid.toStringQu ();

                    // execute the queryString, indicate that we're not performing an
                    // action query:
                    action.execute (updateStr, true);
                } // for (int j = 0; j < nrCodes; j++)
                action.commitTrans ();
            } // try
            catch (DBError dbErr)
            {
                this.env.write (updateStr);
                // an error occurred - show name and info
                IOHelpers.showMessage (dbErr, this.app, this.sess, this.env, false);
            } // catch
        } // if
        else if (this.productDialogStep == 3)
        {

            // change long product description on db
            this.performChangeTextData (action, this.tableName,
                "productDescription", this.fullDescription);

            if (this.codes != null)
            // change codes if there are any
            {
                int nrCodes = this.codes.length;
                String updateStr = "";
                try
                {

                    action.beginTrans ();
                    for (int j = 0; j < nrCodes; j++)
                    {
                        updateStr =
                            " UPDATE m2_ProductCodeValues_01" +
                            " SET codeValues = \'" + this.codes[j].toString (Product_01.DELIMITER) + "\'" +
                            " WHERE productOid = " + this.oid.toStringQu () +
                            " AND categoryOid = " + this.codes[j].categoryOid.toStringQu ();
                        // execute the queryString, indicate that we're not performing an
                        // action query:
                        action.execute (updateStr, true);
                    } // for (int j = 0; j < nrCodes; j++)
                    action.commitTrans ();
                } // try
                catch (DBError dbErr)
                {
                    this.env.write (updateStr);
                    // an error occurred - show name and info
                    IOHelpers.showMessage (dbErr, this.app, this.sess, this.env, false);
                } // catch
            } // if codes != null
        } // else if ((this.productDialogStep == 3))
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
        int i = lastIndex;              // initialize params index

        // set the specific parameters:
        // productNo
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // ean
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // availableFrom
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_DATE);
        // unitOfQty
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
        // packingUnit
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // thumbAsImage
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);
        // thumbnail
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // image
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // path
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // stock
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // price tab oid
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // product profile oid
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // hasAssortment
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
        // created
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
        // brand name
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // brand name oid
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // brand name image
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // product collection container id
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
        // productNo
        this.productNo = params[++i].getValueString ();
        // ean
        this.ean = params[++i].getValueString ();
        // availableFrom
        this.availableFrom  = params[++i].getValueDate ();
        // unitOfQty
        this.unitOfQty = params[++i].getValueInteger ();
        // packingUnit
        this.packingUnit = params[++i].getValueString ();
        // thumbAsImage
        this.thumbAsImage = params[++i].getValueBoolean ();
        // thumbnail
        this.thumbnail = params[++i].getValueString ();
        // image
        this.image = params[++i].getValueString ();
        // path
        this.path = params[++i].getValueString ();
        // stock
        this.stock = params[++i].getValueString ();
        // oid of the price container
        this.priceContainer = SQLHelpers.getSpOidParam (params[++i]);
        // oid of the product profile
        this.productProfileOid = SQLHelpers.getSpOidParam (params[++i]);
        // hasAssortment
        this.hasAssortment = params[++i].getValueInteger ();
        // created
        this.created = params[++i].getValueInteger ();
        // brand name
/*
        this.brandName = params[++i].getValueString ();
*/
        ++i;                            // currently not used
        // brand name oid
        this.brandNameOid = SQLHelpers.getSpOidParam (params[++i]);
        // image of the brand
        this.brandImage = params[++i].getValueString ();
        // oid of the product profile
        this.collectionContainerOid = SQLHelpers.getSpOidParam (params[++i]);
    } // getSpecificRetrieveParameters


    /**************************************************************************
     * Retrieve the type specific data that is not got from the stored
     * procedure. <BR/>
     * This method must be overwritten by all subclasses that have to get type
     * specific data that cannot be got from the retrieve data stored procedure.
     *
     * @param   action  SQLAction for Databaseoperation
     *
     * @throws  DBError
     *          This exception is always thrown, if there happens an error
     *          during accessing data.
     */
    protected void performRetrieveSpecificData (SQLAction action) throws DBError
    {
        // get the price info of that product
        this.performRetrievePrices (action);
        // retrieve the code values of that product
        this.performRetrieveCodeValues (action);
        if (this.hasAssortment == 1)
        {
            this.performRetrieveProductCollections (action);
        } // if

        this.fullDescription = this.performRetrieveTextData (action,
            this.tableName, "productDescription", "p_Product_01$getExtended");
    } // performRetrieveSpecificData


    /**************************************************************************
     * ???
     *
     * @param   action  The action object associated with the connection.
     */
    protected void performRetrievePrices (SQLAction action)
    {
        int rowCount  = 0;

        this.prices = new Vector<Price_01> ();
        this.currencies = new Vector<String> ();
        try
        {
            String queryStr = " " +
                " SELECT  DISTINCT oid, price, cost, costCurrency, " +
                "                  userValue1, userValue2, categoryName, " +
                "                  categoryOid, codeValues, validForAllValues" +
                "                   ,qty" +
                " FROM    v_ProductPrices " +
                " WHERE   containerId = " + this.priceContainer.toStringQu () +
                SQLHelpers.getStringCheckRights (Operations.OP_READ) +
                " AND     userId = " +  this.user.id;

//debug (queryStr);

            rowCount = action.execute (queryStr, false);
            // everything ok - go on
            if (rowCount > 0)
            {
                Price_01 price = null;
                OID oldOid = OID.getEmptyOid ();
                OID newOid = null;
                ProductCode code = null;
                String str;
                // get tuples out of db
                while (!action.getEOF ())
                {
                    newOid = SQLHelpers.getQuOidValue (action, "oid");
                    // if this is the same price just add
                    // the code values
                    if (oldOid.equals (newOid))
                    {
                        code = new ProductCode ();
                        code.name = action.getString ("categoryName");
                        code.categoryOid = SQLHelpers.getQuOidValue (action, "categoryOid");
                        code.parseStringToValues (action.getString ("codeValues"), Product_01.DELIMITER);
                        code.priceValidForAllValues = action.getBoolean ("validForAllValues");
                        price.codes.addElement (code);
                    } // if
                    else
                    {
                        // if it's not the first price
                        if (price != null)
                        {
                            this.prices.addElement (price);
                        } // if

                        price = new Price_01 ();
                        price.codes = new Vector<ProductCode> ();
                        price.currency = action.getString ("costCurrency");
                        if (this.currencies.indexOf (price.currency) < 0)
                        {
                            this.currencies.addElement (price.currency);
                        } // if
                        price.cost = action.getCurrency ("cost");
                        price.price = action.getCurrency ("price");
                        price.oldCost = action.getCurrency ("userValue1");
                        price.oldPrice = action.getCurrency ("userValue2");
                        price.qty = action.getInt ("qty");

                        if ((str = action.getString ("categoryName")) != null)
                        {
                            code = new ProductCode ();
                            code.name = str;
                            code.categoryOid = SQLHelpers.getQuOidValue (action, "categoryOid");
                            code.parseStringToValues (action.getString ("codeValues"), Product_01.DELIMITER);
                            code.priceValidForAllValues = action.getBoolean ("validForAllValues");
                            price.codes.addElement (code);
                        } // if
                    } // else
                    oldOid = newOid;
                    // step one tuple ahead for the next loop
                    action.next ();
                } // while

                this.prices.addElement (price);
            } // if rowCount > 0

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
    } // performRetrievePrices


    /**************************************************************************
     * ???
     *
     * @param   action  The database connection object.
     */
    protected void performRetrieveCodeValues (SQLAction action)
    {
        int rowCount  = 0;
        int nrValues = 0;
        int nrPossibleValues = 0;
        // empty the vector of product codes (like color) with values (like green, yellow ...)
        this.codesWithValues.removeAllElements ();

        // initialize elements vector:
        Vector<ProductCode> vcodes = new Vector<ProductCode> (10, 10);
        ProductCode obj;

        try
        {
            String queryStr = " " +
                " SELECT  categoryOid, categoryName, predefinedCodeOid, " +
                "         predefinedCodeValues, codeValues " +
                " FROM    v_ProductCodeValues " +
                " WHERE   productOid = " + this.oid.toStringQu ();

            rowCount = action.execute (queryStr, false);
            // everything ok - go on
            if (rowCount > 0)
            {
                // get tuples out of db
                while (!action.getEOF ())
                {
                    obj = new ProductCode ();
                    obj.name = action.getString ("categoryName");
                    obj.categoryOid = SQLHelpers.getQuOidValue (action, "categoryOid");
                    nrPossibleValues = obj.parseStringToPossibleValues (action.getString ("predefinedCodeValues"), Product_01.DELIMITER);
                    nrValues = obj.parseStringToValues (action.getString ("codeValues"), Product_01.DELIMITER);
                    obj.predefinedCodeOid = SQLHelpers.getQuOidValue (action, "predefinedCodeOid");
                    obj.selfDefined = false;
                    if (obj.predefinedCodeOid == null ||
                        obj.predefinedCodeOid.isEmpty ())
                    {
                        obj.selfDefined = true;
                    } // if
                    // if code has more then one value add code to codewithvalues - list
                    if (obj.selfDefined && nrValues > 1 || !(obj.selfDefined) && nrPossibleValues > 1)
                    {
                        this.codesWithValues.addElement (obj);
                    } // if

                    vcodes.addElement (obj);
                    // step one tuple ahead for the next loop
                    action.next ();
                } // while

                // copy the elements of the vector into an array
                this.codes = new ProductCode[vcodes.size ()];
                vcodes.copyInto (this.codes);

            } // if rowCount > 0
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
    } // performRetrieveCodeValues


    /**************************************************************************
     * ???
     *
     * @param   action  The database connection object.
     */
    protected void performRetrieveProductCollections (SQLAction action)
    {
        int rowCount  = 0;

        try
        {
            // retrieve the oids of the product collections
            String queryStr = " " +
                " SELECT  DISTINCT oid" +
                " FROM    v_Container$rights" +
                " WHERE   containerId = " +
                    this.collectionContainerOid.toStringQu () +
                " AND     tversionId = " +
                    this.getTypeCache ()
                        .getTVersionId (StoreTypeConstants.TC_ProductCollection);

//debug (queryStr);

            rowCount = action.execute (queryStr, false);

            if (this.productCollections != null)
            {
                this.productCollections.removeAllElements ();
            } // if
            // everything ok - go on
            if (rowCount > 0)
            {
                ProductCollection_01 collection;
                OID newOid;

                this.productCollections = new Vector<ProductCollection_01> (10, 5);

                // get tuples out of db
                while (!action.getEOF ())
                {
                    newOid = SQLHelpers.getQuOidValue (action, "oid");
                    // try to get the object:
                    collection = (ProductCollection_01) BOHelpers.getObject (
                        newOid, this.env, false, false, true);

                    // check if we got the object:
                    if (collection != null) // got collection?
                    {
                        this.productCollections.addElement (collection);
                    } // if got collection

                    // step one tuple ahead for the next loop
                    action.next ();
                } // while
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
    } // performRetrieveProductCollections


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
            Buttons.BTN_EDIT,
            Buttons.BTN_DELETE,
            Buttons.BTN_CUT,
            Buttons.BTN_COPY,
            Buttons.BTN_DISTRIBUTE,
//            Buttons.BTN_CLEAN,
//            Buttons.BTN_SEARCH,
//            Buttons.BTN_HELP,
//            Buttons.BTN_LOGIN,
            Buttons.BTN_SHOPPINGCART,
        }; // buttons

        // return button array
        return buttons;
    } // setInfoButtons


    /**************************************************************************
     * Returns the codes of the product
     *
     * @return  An array of product codes.
     */
    public ProductCode[] getProductCodes ()
    {
        return this.codes;
    } // getProductCodes


    ///////////////////////////////////////////////////////////////////////////
    // Private
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Show the price information to the user. <BR/>
     *
     * @return  An HTML String with the price information of the product.
     */
    private String showPrices ()
    {
        String priceInfo = "";
        Price_01 price;
        int i = 0;

        while (i < this.prices.size ())
        {
            if (this.prices.elementAt (i) != null)
            {

                if (i > 0)
                {
                    priceInfo += IE302.TAG_NEWLINE + IE302.TAG_NEWLINE;
                } // if
                price = this.prices.elementAt (i);

                // priceInfo = "";
                // get the currency and cost of the product
                priceInfo += 
                    MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                        StoreTokens.ML_COST_SHORT, env)
                    + " " + price.currency + " " + Helpers.moneyToString (price.cost);

                // if current currency is not euro, show euroamount
                if (!UtilConstants.TOK_CURRENCY_EUR.equals (price.currency))
                {
                    priceInfo += "/" +
                        UtilConstants.TOK_CURRENCY_EUR +
                        " " +
                        Helpers.moneyToString (Helpers.getEuroAmount (
                            price.currency, price.cost));
                } // if

                // show oldCost if there is one
                if (price.oldCost != 0)
                {
                    priceInfo += IE302.TAG_NEWLINE;
                    priceInfo += " " + 
                        MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                            StoreTokens.ML_INSTEAD, env)
                        + " " + price.currency + " " +
                        Helpers.moneyToString (price.oldCost);
                    // if current currency is not euro, show euroamount
                    if (!UtilConstants.TOK_CURRENCY_EUR.equals (price.currency))
                    {
                        priceInfo += "/" +
                            UtilConstants.TOK_CURRENCY_EUR +
                            " " +
                            Helpers.moneyToString (Helpers.getEuroAmount (
                                price.currency, price.oldCost));
                    } // if
                } // if

                priceInfo += IE302.TAG_NEWLINE;

                // get the currency and price of the product
                priceInfo += 
                    MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                        StoreTokens.ML_SALESPRICE_SHORT, env) + "  " +
                    price.currency + " " + Helpers.moneyToString (price.price);
                // if current currency is not euro, show euroamount
                if (!UtilConstants.TOK_CURRENCY_EUR.equals (price.currency))
                {
                    priceInfo += "/" +
                        UtilConstants.TOK_CURRENCY_EUR +
                        " " +
                        Helpers.moneyToString (Helpers.getEuroAmount (
                            price.currency, price.price));
                } // if

                // show oldPrice if there is one
                if (price.oldPrice != 0)
                {
                    priceInfo += IE302.TAG_NEWLINE;
                    priceInfo += " " + 
                        MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                            StoreTokens.ML_INSTEAD, env)
                        + " " + price.currency + " " +
                        Helpers.moneyToString (price.oldPrice);
                    // if current currency is not euro, show euro amount
                    if (!UtilConstants.TOK_CURRENCY_EUR.equals (price.currency))
                    {
                        priceInfo += "/" +
                            UtilConstants.TOK_CURRENCY_EUR +
                            " " +
                            Helpers.moneyToString (Helpers.getEuroAmount (
                                price.currency, price.oldPrice));
                    } // if
                } // if

                // check if the price counts only for special codes (keys)
                if (price.codes != null)
                {
                    int size = price.codes.size ();
                    ProductCode code = null;
                    // Print out the code values for which this price
                    // is valid
                    for (int j = 0; j < size; j++)
                    {
                        code = price.codes.elementAt (j);
                        if (!code.priceValidForAllValues)
                        {
                            priceInfo += IE302.TAG_NEWLINE + 
                                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                                    StoreTokens.ML_FOR, env)
                                + " " + code.name + ": " + code.toString (", ");
                        } // if
                    } // for j
                } // if
                // show the minimum quantity for which this price is valid
                if (price.qty > 1)
                {
                    priceInfo += IE302.TAG_NEWLINE + 
                        MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                            StoreTokens.ML_QTY, env) + ": ";
                    priceInfo += Integer.toString (price.qty) + " ";
                    priceInfo += this.packingUnit + IE302.TAG_NEWLINE;
                } // if
            } // if (prices.elementAt (i) != null)
            i++;
        } // while (i < prices.size ())

        // if no prices found
        if (i == 0)
        {
            priceInfo = 
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_NOPRICES_DEFINED, env);
        } // if

        return priceInfo;
    } // showPrices


    /**************************************************************************
     * Show the product collection information to the user. <BR/>
     *
     * @return  An HTML String with the price information of the product.
     */
    private String showProductCollections ()
    {
        StringBuffer collectionInfo = new StringBuffer ();

        if (this.productCollections != null)
        {
            int size = this.productCollections.size ();
            ProductCollection_01 pc = null;

            if (size > 0)
            {
                for (int i = 0; i < size; i++)
                {
                    if (i > 0)
                    {
                        collectionInfo.append (IE302.TAG_NEWLINE).append (
                            IE302.TAG_NEWLINE);
                    } // if
                    pc = this.productCollections.elementAt (i);
                    collectionInfo.append (pc.name);
                    collectionInfo.append (IE302.TAG_NEWLINE);
                    collectionInfo.append (
                        MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                            StoreTokens.ML_COST_SHORT, env) + ": " + pc.costCurrency + " ");
                    collectionInfo.append (Helpers.moneyToString (pc.cost));
                    collectionInfo.append (IE302.TAG_NEWLINE + 
                        MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                            StoreTokens.ML_TOTALQUANTITY, env) + ": ");
                    collectionInfo.append (pc.totalQuantity);
                    collectionInfo.append (IE302.TAG_NEWLINE);
                    collectionInfo.append (pc.showCollection ());
                } // for
            } // if
            else
            {
                return MultilingualTextProvider.getMessage (StoreMessages.MSG_BUNDLE,
                    StoreMessages.ML_MSG_NO_COLLECTIONS, env);
            } // else
        } // if
        else
        {
            return MultilingualTextProvider.getMessage (StoreMessages.MSG_BUNDLE,
                StoreMessages.ML_MSG_NO_COLLECTIONS, env);
        } // else
        return collectionInfo.toString ();

    } // showPrices


    /**************************************************************************
     * . <BR/>
     *
     * @param   table   The table to add the generated matrix.
     */
    private void showOrderSimpleForm (TableElement table)
    {
        RowElement row;
        TableDataElement td;
        GroupElement group;
        InputElement input;

        // if there is a price defined for this combination
        // show the input dialog
        group = new GroupElement ();
        row = new RowElement (1);
        // hidden input to indicate how many input fields
        input = new InputElement (StoreArguments.ARG_NR_ORDERINPUTS,
                                  InputElement.INP_HIDDEN,
                                  "" + 1);

        group.addElement (input);
        td = new TableDataElement (group);
        row.addElement (td);
        table.addElement (row);

        // show a simple input dialog
        this.showFormProperty (table, StoreArguments.ARG_ORDER_PREFIX,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_ORDERQTY, env), Datatypes.DT_INTEGER, "");
    } // showOrderSimpleForm


    /***************************************************************************
     * Build the order matrix (size/color) for this product. <BR/>
     *
     * @param table The table to add the generated matrix.
     */
    private void showOrderMatrixForm (TableElement table)
    {
        String trace = "";
        try
        {
            TableElement table2 = new TableElement ();
            Font headerFont;
            Font valueFont;
            RowElement row;
            TextElement text;
            TableDataElement td;
            GroupElement group;
            InputElement input;
            ProductCode codeX = null;
            ProductCode codeY = null;
            String valueX = null;
            String valueY = null;
            int nrRows = 0;
            int nrColumns = 0;
            boolean found = true;

            codeX = this.codesWithValues.elementAt (0);
            // interchange the two categories to get the one
            // with the most values in horizontal order
            if (this.codesWithValues.size () > 1)
            {
                codeY = this.codesWithValues.elementAt (1);

                if (codeY.values.length > codeX.values.length)
                {
                    ProductCode temp = codeX;
                    codeX = codeY;
                    codeY = temp;
                } // if
                nrRows = codeY.values.length;
            } // if
            nrColumns = codeX.values.length;

            if (nrColumns < 1)
            {
                // if there are no values for the product codes
                // (product categories)
                return;
            } // if

            // set table attributes
            table2.classId = StoreConstants.CLASS_PRODUCT_ORDER_MATRIX;
            table2.border = 1;
            table2.ruletype = IOConstants.RULE_NONE;
            table2.frametypes = IOConstants.FRAME_BOX;
            table2.cellpadding = 2;
            table2.cellspacing = 0;

            // set the font for the header and the first column
            headerFont = new Font ();
            headerFont.bold = true;
            headerFont.italic = true;
            valueFont = new Font ();
            valueFont.italic = true;

            // first element is the qty token
            text = new TextElement (
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_ORDERQTY, env));
            td = new TableDataElement (text);
            td.classId = StoreConstants.CLASS_PRODUCT_ORDER_HEADER;
            row = new RowElement (nrColumns);
            row.addElement (td);

            // add the first category (size)
            group = new GroupElement ();
            text = new TextElement (codeX.name);
            group.addElement (text);
            // add the number of input fields as hidden fields
            int nrInputElements = 0;
            if (nrRows > 0)
            {
                nrInputElements = nrColumns * nrRows;
            } // if
            else
            {
                nrInputElements = nrColumns;
            } // else
            input = new InputElement (StoreArguments.ARG_NR_ORDERINPUTS,
                InputElement.INP_HIDDEN, "" + nrInputElements);
            group.addElement (input);

            // add the category oid of the x axis
            input = new InputElement (StoreArguments.ARG_CATEGORYOID + 0,
                InputElement.INP_HIDDEN, "" + codeX.categoryOid);
            group.addElement (input);
            // add the category oid of the y axis
            if (nrRows > 0)
            {
                input = new InputElement (StoreArguments.ARG_CATEGORYOID + 1,
                    InputElement.INP_HIDDEN, "" + codeY.categoryOid);
                group.addElement (input);
            } // if
            td = new TableDataElement (group);

            td.classId = StoreConstants.CLASS_PRODUCT_ORDER_DIMENSION;
            td.colspan = nrColumns;
            row.addElement (td);
            table2.addElement (row);

            // add the first value of first column
            row = new RowElement (nrColumns + 1);
            if (nrRows > 0)
            {
                text = new TextElement (codeY.name);
                td = new TableDataElement (text);
            } // if
            else
            {
                td = new TableDataElement (new BlankElement ());
            } // else
            td.classId = StoreConstants.CLASS_PRODUCT_ORDER_DIMENSION;
            row.addElement (td);
            // add as column header
            for (int j = 0; j < codeX.values.length; j++)
            {
                text = new TextElement (codeX.values[j]);
                td = new TableDataElement (text);
                td.classId = StoreConstants.CLASS_PRODUCT_ORDER_VALUE;
                row.addElement (td);
            } // for
            table2.addElement (row);

            int j = 0;
            int k = 0;
            row = new RowElement (nrColumns + 1);

            // go through all colors of the product
            while (found)
            {
                // if colors defined
                if (nrRows > 0)
                {
                    valueY = codeY.values[k];
                    text = new TextElement (valueY);
                    if (++k >= nrRows)
                    {
                        found = false;
                    } // if
                } // if
                else
                {
                    valueY = null; // set dummy entry
                    found = false; // go only through first loop
                    text = new TextElement ("");
                } // else
                // show the value
                td = new TableDataElement (text);
                td.classId = StoreConstants.CLASS_PRODUCT_ORDER_VALUE;
                row.addElement (td);
                // go through all sizes and show the input box
                for (int i = 0; i < nrColumns; i++)
                {
                    Price_01 price;

                    group = new GroupElement ();
                    valueX = codeX.values[i];

                    price = this.getPrice (codeX, codeY, valueX, valueY, null);
//debug ("price = " + price + " codes... " + codeX + ":" + valueX + "  " + codeY + ":" + valueY);

                // if there is a price defined for this combination
                    // show the input dialog

                    if (price != null)
                    {
                        // add the text field to enter the quantity
                        input = new InputElement (
                            StoreArguments.ARG_ORDER_PREFIX + j,
                            InputElement.INP_TEXT, "");
                        input.size = StoreConstants.CONST_MAX_QTY;
                        input.maxlength = StoreConstants.CONST_MAX_QTY;
                        group.addElement (input);

                        // hidden input to show which value
                        input = new InputElement (StoreArguments.ARG_CODEX + j,
                            InputElement.INP_HIDDEN, valueX);
                        group.addElement (input);
                        if (nrRows > 0)
                        {
                            input = new InputElement (StoreArguments.ARG_CODEY +
                                j, InputElement.INP_HIDDEN, valueY);
                            group.addElement (input);
                        } // if
                    } // if
                    else
                    {
                        group.addElement (new CenterElement (new TextElement (
                            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                                StoreTokens.ML_NOPRICEINMATRIX, env))));
                    } // else
                    trace += "14 ";
                    td = new TableDataElement (group);
                    td.classId = StoreConstants.CLASS_PRODUCT_ORDER_QUANTITY;
                    trace += "15 ";
                    row.addElement (td);
                    trace += "16 ";
                    j++;
                } // for
                table2.addElement (row);
                row = new RowElement (nrColumns + 1);
            } // while (found)

            // add the generated table to the table above
            td = new TableDataElement (table2);
            td.colspan = 2;
            td.width = HtmlConstants.TAV_FULLWIDTH;
            td.alignment = IOConstants.ALIGN_CENTER;
            row = new RowElement (1);
            row.addElement (td);
            table.addElement (row);
        } // try
        catch (Exception e)
        {
            this.debug (trace);
        } // catch
    } // showOrderMatrixForm


    /**************************************************************************
     * . <BR/>
     *
     * @param   table   The table to add the generated matrix.
     */
    private void showOrderSelectionForm (TableElement table)
    {
        // assert this.codes != null
        int size = this.codesWithValues.size ();
        ProductCode code = null;
        GroupElement hiddenFields = new GroupElement ();
                                        // a Vector holding the hidden fields
                                        // of the object

        this.addHiddenField (hiddenFields, StoreArguments.ARG_NRCODES, "" + size);

        for (int i = 0; i < size; i++)
        {
            code = this.codesWithValues.elementAt (i);

            this.addHiddenField (hiddenFields, StoreArguments.ARG_CATEGORYOID + i,
                "" + code.categoryOid);
            // (index of oid: 1)
            this
                .showFormProperty (table, StoreArguments.ARG_CODE + i,
                    code.name, Datatypes.DT_SELECT, "", code.values,
                    code.values, 1);
        } // for
        this.showFormProperty (table, StoreArguments.ARG_ORDER_PREFIX,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_ORDERQTY, env), Datatypes.DT_INTEGER, " ");
    } // showOrderSelection

    /***************************************************************************
     * Show the order form for product collections. <BR/>
     *
     * @param table The table to add the generated matrix.
     */
    private void showOrderCollectionsForm (TableElement table)
    {
//debug ("-------- schoOrderCollectionsForm ----------------  ");
        GroupElement hiddenFields = new GroupElement ();
                                        // a Vector holding the hidden fields
                                        // of the object

        if (this.productCollections != null)
        {
            int size = this.productCollections.size ();
            ProductCollection_01 pc = null;

            this.addHiddenField (hiddenFields, StoreArguments.ARG_NRCOLLECTIONS,
                 "" + size);
            if (size > 0)
            {
                for (int i = 0; i < size; i++)
                {
                    pc = this.productCollections.elementAt (i);
                    this.addHiddenField (hiddenFields,
                        StoreArguments.ARG_COLLECTIONNAME + i, pc.name);
                    this.addHiddenField (hiddenFields, StoreArguments.ARG_COST +
                        i, Helpers.moneyToString (pc.cost));
                    this.addHiddenField (hiddenFields,
                        StoreArguments.ARG_COSTCURRENCY + i, pc.costCurrency);
                    // product collection
                    if ((this.codes.length < 2) || (pc.nrDims > 1))
                    {
                        this.addHiddenField (hiddenFields,
                            StoreArguments.ARG_NRCODES + i, "1");
                        this.showFormProperty (table,
                            StoreArguments.ARG_COLLECTION + i,
                            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                                StoreTokens.ML_ORDERQTY, env)
                            + " " + pc.name, Datatypes.DT_INTEGER, "");
                    } // if
                    else
                    {
                        ProductCode code = this.codes [0];
                        if (code != null)
                        {
                            if (code.categoryOid != null)
                            {
//debug (" code.categoryOid = " + code.categoryOid.toString ());
                                if (code.categoryOid.equals (pc.categoryOidX))
                                {
//debug (" codecategoryOid = productcollectionCategoryOidX");
                                    code = this.codes [1];
                                } // if
//debug (" codevalues = " + code.values.toString ());
//__________________________________________________________________________
                                RowElement row = new RowElement (2);
                                TextElement text = new TextElement (
                                    MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                                        StoreTokens.ML_ORDERQTY, env)
                                    + " " + pc.name + ":");
                                TableDataElement td = new TableDataElement (text);
                                row.addElement (td);

                                InputElement input;
                                int size2 = code.values.length;

                                this.addHiddenField (hiddenFields,
                                    StoreArguments.ARG_NRCODES + i, "" + size2);
                                TableElement list = new TableElement ();
                                list.border = 0;
                                list.cellpadding = 2;
                                list.cellspacing = 5;

                                RowElement tr = new RowElement (2);
                                for (int j = 0; j < size2; j++)
                                {
                                    // text label
                                    text = new TextElement (code.values[j] +
                                        " ");
                                    td = new TableDataElement (text);
                                    tr.addElement (td);
                                    // input element
                                    input = new InputElement (
                                        StoreArguments.ARG_COLLECTION + i + j,
                                        InputElement.INP_TEXT, "");
                                    input.size = StoreConstants.CONST_MAX_QTY;
                                    input.maxlength = StoreConstants.CONST_MAX_QTY;
                                    td = new TableDataElement (input);
                                    tr.addElement (td);
                                    // hidden input for the code value
                                    this.addHiddenField (hiddenFields,
                                        StoreArguments.ARG_CODEX + i + j,
                                        code.values[j]);
                                    list.addElement (tr);
                                    tr = new RowElement (2);
                                } // for
                                td = new TableDataElement (list);
                                td.valign = IOConstants.ALIGN_MIDDLE;
                                row.addElement (td);
                                table.addElement (row);
                            } // if
                        } // if
                    } // else
                } // for
            } // if
        } // if

    } // showOrderCollectionsForm
    /**************************************************************************
     * Puts the product order in the shopping cart in the database
     *
     * @param   qty                 The quantity ordered of the product
     * @param   unitOfQty           The smallest unit to be ordered
     * @param   packingUnit         The packing unit
     * @param   productName         The name of the product.
     * @param   productDescription  The description of the product
     * @param   price               The price of the product
     * @param   priceCurrency       The currency used for the price
     */
    private void performCreateCartEntry (int qty, int unitOfQty,
        String packingUnit, String productName, String productDescription,
        long price, String priceCurrency)
    {
        // create stored procedure call:
        StoredProcedure sp = new StoredProcedure (Product_01.PROC_CREATECARTENTRY,
            StoredProcedureConstants.RETURN_VALUE);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // input parameters
        // oid
        BOHelpers.addInParameter (sp, this.oid);

        // user id
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
            this.user != null ? this.user.id : 0);
        // operation
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, 0);
        // name
        sp.addInParameter (ParameterConstants.TYPE_STRING, productName);
        // tVersionId
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.getTypeCache ()
            .getTVersionId (StoreTypeConstants.TC_ShoppingCartLine));
        // state
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, States.ST_ACTIVE);

        //---------------------------------------------------------------------
        // quantity
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, qty);
        // unitOfQty
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, unitOfQty);
        // packingUnit
        sp.addInParameter (ParameterConstants.TYPE_STRING, packingUnit);
        // productDescription
        sp.addInParameter (ParameterConstants.TYPE_STRING, productDescription);
        // price
        sp.addInParameter (ParameterConstants.TYPE_CURRENCY, price);
        //priceCurrency
        sp.addInParameter (ParameterConstants.TYPE_STRING, priceCurrency);

        try
        {
            // perform the function call:
            BOHelpers.performCallFunctionData (sp, this.env);
        } // try
        catch (NoAccessException e)
        {
            // nothing to do, this exception cannot occur
        } // catch
    } // performCreateCartEntry


    /**************************************************************************
     * Get the price for a spezific currency and quantity. <BR/>
     *
     * @param   currency    Currency of the seeked price.
     * @param   qty         Current order quantity to select between prices with
     *                      different minimum order quantities.
     *
     * @return  The price.
     *          <CODE>null</CODE> if the price was not found.
     */
    private Price_01 getPrice (String currency, int qty)
    {
//debug (" --- Price_01.getPrice (currency, qty) ANFANG --- ");
        Price_01 price;
        Price_01 foundPrice = null;

        if ((this.prices != null) && (currency != null))
        {
            for (int i = 0; i < this.prices.size (); i++)
            {
                price = this.prices.elementAt (i);
                if (price != null)
                {
                    if (currency.equals (price.currency) && qty >= price.qty)
                    {
                        if (foundPrice != null && foundPrice.qty > price.qty)
                        {
                            // price with higher minimum order quantity was
                            // already found
                            continue;
                        } // if

                        foundPrice = price;
                    } // if
                } // if
            } // for

            return foundPrice;
        } // if

//debug (" --- Price_01.getPrice (currency, qty) ENDE --- ");
        return null;
    } // getPrice




    /**************************************************************************
     * Get the code in one price via its categoryOid. <BR/>
     *
     * @param   price       The price.
     * @param   categoryOid The category.
     *
     * @return  The found code.
     */
    private ProductCode getPriceCode (Price_01 price, OID categoryOid)
    {
        // code means ProductCategorie
        // find code for specific category in price
        ProductCode code = null;
        for (int k = 0;
            k < price.codes.size ();
            k++)
        {
            code = price.codes.elementAt (k);
            if (code != null &&
                code.categoryOid != null &&
                code.categoryOid.equals (categoryOid))
            {
                return price.codes.elementAt (k);
            } // if
        } // for

        // if code with categoryOid was not found return null:
        return null;
    } // getPriceCode


    /**************************************************************************
     * Get the price for the size/color combination. <BR/>
     *
     * @param code1     The first code.
     * @param code2     The second code.
     * @param value1    The first value.
     * @param value2    The second value.
     * @param currency  The currency.
     * @param qty       The quantity.
     *
     * @return  An array with button ids that can be displayed.
     */
    private Price_01 getPrice (ProductCode code1, ProductCode code2,
                               String value1, String value2, String currency,
                               int qty)
    {
        Price_01 price;
        Price_01 foundPrice = null;

        // assert code1 != null
        if (this.prices != null)
        {
            for (int i = 0; i < this.prices.size (); i++)
            {
                price = this.prices.elementAt (i);
                if (price != null)
                {
                    if ((price.codes != null) && (price.codes.size () > 0))
                    {
                        if ((code2 == null) || (code2.categoryOid == null))
                        {
                            // get the price code with specific categoryOid
                            ProductCode code = this.getPriceCode (price,
                                code1.categoryOid);

                            // if code was not found return price anyway
                            if (code == null)
                            {
                                return price;
                            } // if

                            if (this.contains (code, value1))
                            {
                                if (currency != null)
                                {
                                    if (currency.equals (price.currency) && qty >= price.qty)
                                    {
                                        if (foundPrice != null && foundPrice.qty > price.qty)
                                        {
                                            // price with higher minimum-order-quantity was already found
                                            continue;
                                        } // if
//debug (" foundPrice not Null ??  price = " + price);
                                        foundPrice = price;
                                    } // if (currency)

                                } // if (contains ...)
                                else
                                {
                                    return price;
                                } // else
                            } // if (code == null ..)
                        } // if
                        else if (price.codes.size () > 1)
                        {
//debug ("7");
                            ProductCode codeX = price.codes.elementAt (0);
                            ProductCode codeY = price.codes.elementAt (1);

                            if (!code1.categoryOid.equals (codeX.categoryOid))
                            {
                                ProductCode temp = codeX;
                                codeX = codeY;
                                codeY = temp;
                            } // if

                            if (this.contains (codeX, value1) &&
                                this.contains (codeY, value2))
                            {
                                if (currency != null)
                                {
                                    if (currency.equals (price.currency) &&
                                        qty >= price.qty)
                                    {
                                        if (foundPrice != null &&
                                            foundPrice.qty > price.qty)
                                        {
                                            // price with higher
                                            // minimum-order-quantity was
                                            // already found
                                            continue;
                                        } // if

                                        foundPrice = price;
                                    } // if
                                } // if
                                else
                                {
                                    return price;
                                } // else
                            } // if
                        } // else if
                    } // if
                } // if
            } // for
//debug ("foundPrice = " + foundPrice);
            return foundPrice;
        } // if
        return null;
    } // getPrice

    /**************************************************************************
     * Get the price for the size/color combination. <BR/>
     *
     * @param   codes       The codes to search for.
     * @param   currency    The currency for which to get the price.
     * @param   qty         The quantity.
     *
     * @return  The price.
     *          <CODE>null</CODE> if there was not price found.
     */
    private Price_01 getPrice (ProductCode[] codes,  String currency, int qty)
    {
        Price_01 price;
        Price_01 foundPrice = null;

        // assert code1 != null
        if (this.prices != null)
        {
            for (int i = 0; i < this.prices.size (); i++)
            {
                price = this.prices.elementAt (i);
                if (price != null)
                {
                    if ((price.codes != null) && (price.codes.size () > 0))
                    {
                        if (this.contains (price.codes, codes))
                        {
                            if (currency != null)
                            {
                                if (currency.equals (price.currency) && qty >= price.qty)
                                {
                                    if (foundPrice != null && foundPrice.qty > price.qty)
                                    {
                                        // price with higher minimum-order-quantity was already found
                                        continue;
                                    } // if

                                    foundPrice = price;
                                } // if
                            } // if
                            else
                            {
                                if (qty >= price.qty)
                                {
                                    if (foundPrice != null && foundPrice.qty > price.qty)
                                    {
                                        // price with higher minimum-order-quantity was already found
                                        continue;
                                    } // if

                                    foundPrice = price;
                                } // if
                            } // else
                        } // if
                    } // if
                } // if
            } // for
        } // if

        return foundPrice;
    } // getPrice


    /**************************************************************************
     * Get the price for the size/color combination. <BR/>
     *
     * @param code1     The first code.
     * @param code2     The seconde code.
     * @param value1    The first value.
     * @param value2    The second value.
     * @param currency  The currency.
     *
     * @return An array with button ids that can be displayed.
     */
    private Price_01 getPrice (ProductCode code1, ProductCode code2,
                               String value1, String value2, String currency)
    {
        Price_01 price;

        // assert code1 != null
        if (this.prices != null)
        {
            for (int i = 0; i < this.prices.size (); i++)
            {
                price = this.prices.elementAt (i);
                if (price != null)
                {
                    if ((price.codes != null) && (price.codes.size () > 0))
                    {
                        // only one code is set
                        if ((code2 == null) || (code2.categoryOid == null))
                        {
                            // get the price code with specific categoryOid
                            ProductCode code =
                                this.getPriceCode (price, code1.categoryOid);

                            // if code was not found return price anyway
                            if (code == null)
                            {
                                return price;
                            } // if

                            if (this.contains (code, value1))
                            {
                                if (currency != null)
                                {
                                    if (currency.equals (price.currency))
                                    {
                                        return price;
                                    } // if
                                } // if
                                else
                                {
                                    return price;
                                } // else
                            } // if
                        } // if
                        else if (price.codes.size () > 1)
                        {
                            ProductCode codeX = price.codes.elementAt (0);
                            ProductCode codeY = price.codes.elementAt (1);

                            if (!code1.categoryOid.equals (codeX.categoryOid))
                            {
                                ProductCode temp = codeX;
                                codeX = codeY;
                                codeY = temp;
                            } // if
                            if (this.contains (codeX, value1) &&
                                this.contains (codeY, value2))
                            {
                                if (currency != null)
                                {
                                    if (currency.equals (price.currency))
                                    {
                                        return price;
                                    } // if
                                } // if
                                else
                                {
                                    return price;
                                } // else
                            } // if
                        } // else if
                    } // if
                } // if
            } // for
        } // if
        return null;
    } // getPrice



    /**************************************************************************
     * Check whether a string element is contained in a delimiter string. <BR/>
     *
     * @param   code    The product code.
     * @param   value   The value to search for.
     *
     * @return  <CODE>true</CODE> if the string is contained,
     *          <CODE>false</CODE> otherwise.
     */
    private boolean contains (ProductCode code, String value)
    {
        int i = 0;
        if ((code == null) || (value == null))
        {
//debug ("contains 1");
            return false;
        } // if

        if (code.priceValidForAllValues)
        {
//debug ("contains 2");
            return true;
        } // if

        if (code.values == null)
        {
//debug ("contains 3");
            return false;
        } // if

        while (i < code.values.length)
        {
            if (code.values[i].equals (value))
            {
//debug ("contains 4");
                return true;
            } // if

            i++;
        } // while

        return false;
    } // contains


    /**************************************************************************
     * Check whether a string element is contained in a delimiter string. <BR/>
     *
     * @param   codes           The codes.
     * @param   selectedCodes   The selected codes.
     *
     * @return  <CODE>true</CODE> if the string is contained,
     *          <CODE>false</CODE> otherwise.
     */
    private boolean contains (Vector<ProductCode> codes,
                              ProductCode[] selectedCodes)
    {
        int i = 0;
        int j = 0;
        int size = codes.size ();
        boolean found = true;
        ProductCode code = null;

        while ((i < size) && found)
        {
            j = 0;
            while (j < size)
            {
                code = codes.elementAt (j);
                if (code != null)
                {
                    if (code.categoryOid.equals (selectedCodes[i].categoryOid))
                    {
                        break;
                    } // if
                } // if
                j++;
            } // while
            if (j == size)
            {
                found = false;
                break;
            } // if

            found = found && this.contains (code, selectedCodes[i].value);
            i++;
        } // while
        return found;
    } // contains


    /**************************************************************************
     * Process the simple order form. <BR/>
     *
     * @param    productName        Name of the product
     * @param   unitOfQty       The unit of quantity (number)
     * @param   packingUnit     The unit in which this product may be ordered
     * @param   currency        The currency in which the product is ordered
     *
     * @return  <CODE>true</CODE> if everything was o.k.,
     *          <CODE>false</CODE> otherwise.
     */
    private boolean processSimpleOrderForm (String productName, int unitOfQty,
                                            String packingUnit, String currency)
    {

        SQLAction action = null;        //SQLAction for Databaseoperation
//        boolean letWindowOpen = false;
        Price_01 price;

        int qty;

        qty = this.env.getIntParam (StoreArguments.ARG_ORDER_PREFIX);

        if ((price = this.getPrice (currency, qty)) == null)
        {
            this.successMessage = 
                MultilingualTextProvider.getMessage (StoreMessages.MSG_BUNDLE,
                    StoreMessages.ML_MSG_NOPRICE_SIMPLE, env);
            return true;
        } // if

        action = this.getDBConnection ();
        this.performCreateCartEntry (qty, unitOfQty, packingUnit,
            productName, "", price.cost, currency);

        this.successMessage =  
            MultilingualTextProvider.getMessage (StoreMessages.MSG_BUNDLE,
                StoreMessages.ML_MSG_PUTINCART, env);
        this.releaseDBConnection (action);
        return false;
    } // processSimpleOrderForm


    /**************************************************************************
     * Process the simple order form. <BR/>
     *
     * @param    productName       Name of the product
     * @param   unitOfQty       The unit of quantity (number)
     * @param   packingUnit     The unit in which this product may be ordered
     * @param   currency        The currency in which the product is ordered
     *
     * @return  <CODE>true</CODE> if everything was o.k.,
     *          <CODE>false</CODE> otherwise.
     */
    private boolean processMatrixOrderForm (String productName, int unitOfQty,
                                           String packingUnit, String currency)
    {
        SQLAction action = null;        //SQLAction for Databaseoperation
        Price_01 price = null;

        int qty = 0;
        int i = 0;
        boolean letWindowOpen = false;

        int entries = 0;
        this.successMessage = null;

        if ((i = this.env.getIntParam (StoreArguments.ARG_NR_ORDERINPUTS)) !=
                                                      IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            int j = 0;
            String code1;
            String code2;
            ProductCode codeX = new ProductCode ();
            ProductCode codeY = new ProductCode ();
            String productDescription;

            codeX.categoryOid = this.env.getOidParam (StoreArguments.ARG_CATEGORYOID + 0);
            codeY.categoryOid = this.env.getOidParam (StoreArguments.ARG_CATEGORYOID + 1);

            action = this.getDBConnection ();
            // check if everything is allright
            while (j < i)
            {
                // get the quantity:
                if ((qty = this.env.getIntParam (StoreArguments.ARG_ORDER_PREFIX + j)) !=
                    IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
                {
                    code1 = this.env.getStringParam (StoreArguments.ARG_CODEX + j);
                    code2 = this.env.getStringParam (StoreArguments.ARG_CODEY + j);
                    price = this.getPrice (codeX, codeY, code1, code2, currency, qty);

                    entries++;
                } // if
                else
                {
                    j++;
                    continue;
                } // else

                if (price == null)
                    // no price available for this combination of currency, qantity and codes
                {
                    this.successMessage =  
                        MultilingualTextProvider.getMessage (StoreMessages.MSG_BUNDLE,
                            StoreMessages.ML_MSG_NOPRICE_COMPLEX, env);
                    letWindowOpen = true;
                } // if
/*
                // check if qty is greater or equal minimmum quantity for price
                else if (qty < price.qty)
                {
                    String str = Helpers.replace (StoreMessages.MSG_WRONGORDERQUANTITY, StoreMessages.TOKEN_PACKINGUNIT, this.packingUnit);
                    this.successMessage = Helpers.replace (str, StoreMessages.TOKEN_QUANTITY, "" + price.qty);
                    letWindowOpen = true; // return true to let order form open.
                } // if
*/
                j++;
            } // while

            // if there where some entries and there was a price for each entry
            if (entries > 0 && !letWindowOpen)

                // everything was allright
            {
                this.successMessage =  
                    MultilingualTextProvider.getMessage (StoreMessages.MSG_BUNDLE,
                        StoreMessages.ML_MSG_PUTINCART, env);
                j = 0;
            } // if

            // Create ShoppingCart Entries
            while (j < i)
            {
                // get the quantity
                if ((qty = this.env.getIntParam (StoreArguments.ARG_ORDER_PREFIX + j)) !=
                    IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
                {
                    code1 = this.env.getStringParam (StoreArguments.ARG_CODEX + j);
                    code2 = this.env.getStringParam (StoreArguments.ARG_CODEY + j);
                    price = this.getPrice (codeX, codeY, code1, code2, currency, qty);

                    if (price != null)

                    {
                        // size color information
                        if (code2 != null)
                        {
                            productDescription = code1 + ", " + code2;
                        } // if
                        else
                        {
                            productDescription = code1;
                        } // else

                        // put into the database
                        this.performCreateCartEntry (qty, unitOfQty, packingUnit,
                            productName, productDescription,
                            price.cost, currency);
                    } // if
                } // if (..)
                j++;
            } // while (j < i)

            this.releaseDBConnection (action);
        } // if (..)
        return letWindowOpen;
    } // processMatrixOrderForm


    /***************************************************************************
     * Process the selection order form. <BR/>
     *
     * @param    productName        Name of the product
     * @param   unitOfQty       The unit of quantity (number)
     * @param   packingUnit     The unit in which this product may be ordered
     * @param   currency        The currency in which the product is ordered
     *
     * @return  <CODE>true</CODE>if everything was o.k.,
     *          <CODE>false</CODE> otherwise.
     */
    private boolean processSelectionOrderForm (String productName, int unitOfQty,
                                               String packingUnit, String currency)
    {
        SQLAction action = null;        // SQLAction for Databaseoperation
        Price_01 price;

        int qty;
        int i;
        boolean letWindowOpen = false;

//debug ("orderSelection");
        if ((i = this.env.getIntParam (StoreArguments.ARG_NRCODES)) !=
            IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
//debug ("i = " + i);
            int j = 0;
            ProductCode[] selectedCodes = new ProductCode [i];
            StringBuffer productDescription = new StringBuffer ();

            while (j < i)
            {
                selectedCodes [j] = new ProductCode ();
                selectedCodes [j].categoryOid = this.env.getOidParam (StoreArguments.ARG_CATEGORYOID + j);
                selectedCodes [j].value = this.env.getStringParam (StoreArguments.ARG_CODE + j);
//debug ("selectedCodes [j].value= " + selectedCodes [j].value);
                productDescription.append (selectedCodes [j].value);
                if (j + 1 < i)
                {
                    productDescription.append (", ");
                } // if
                j++;
            } // while (j < i)

            qty = this.env.getIntParam (StoreArguments.ARG_ORDER_PREFIX);

            // put into the database
            action = this.getDBConnection ();
            price = this.getPrice (selectedCodes, currency, qty);
            if (price == null)
            {
                this.successMessage =  
                    MultilingualTextProvider.getMessage (StoreMessages.MSG_BUNDLE,
                        StoreMessages.ML_MSG_NOPRICE_COMPLEX, env);
                letWindowOpen = true;
            } // if
/*
            else if (qty <= price.qty)
            {
                String str = Helpers.replace (StoreMessages.MSG_WRONGORDERQUANTITY, StoreMessages.TOKEN_PACKINGUNIT, this.packingUnit);
                this.successMessage = Helpers.replace (str, StoreMessages.TOKEN_QUANTITY, "" + price.qty);
                letWindowOpen = true; // return true to let order form open.
           } // else if
*/
            else
            {
                this.performCreateCartEntry (qty, unitOfQty, packingUnit,
                    productName, productDescription.toString (),
                    price.cost, currency);
                this.successMessage =  
                    MultilingualTextProvider.getMessage (StoreMessages.MSG_BUNDLE,
                        StoreMessages.ML_MSG_PUTINCART, env);
            } // else

            this.releaseDBConnection (action);
        } // if (..)

        return letWindowOpen;
    } // processSelectionOrderForm


    /**************************************************************************
     * Process the collection order form. <BR/>
     *
     * @param   productName        Name of the product
     * @param   unitOfQty       The unit of quantity (number)
     * @param   packingUnit     The unit in which this product may be ordered
     *
     * @return  <CODE>true</CODE> if everything was o.k.,
     *          <CODE>false</CODE> otherwise.
     */
    private boolean processCollectionOrderForm (String productName, int unitOfQty,
                                                String packingUnit)
    {
        SQLAction action = null;        // SQLAction for Databaseoperation
        long cost;
        int qty;
        int nrCollections;
        boolean letWindowOpen = false;
        String collectionDescription;
        String costCurrency;

        if ((nrCollections = this.env.getIntParam (StoreArguments.ARG_NRCOLLECTIONS)) !=
                                                       IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            int j = 0;

            // put into the database
            try
            {
                action = this.getDBConnection ();
                action.beginTrans ();
                while (j < nrCollections)
                {
                    int nrCodes = this.env.getIntParam (StoreArguments.ARG_NRCODES + j);
                    collectionDescription = this.env.getStringParam (StoreArguments.ARG_COLLECTIONNAME + j);
                    cost = Helpers.stringToMoney (this.env.getStringParam (StoreArguments.ARG_COST + j));
                    costCurrency = this.env.getStringParam (StoreArguments.ARG_COSTCURRENCY + j);

                    // single qty input field
                    if (nrCodes == 1)
                    {
                        qty = this.env.getIntParam (StoreArguments.ARG_COLLECTION + j);
                        if (qty > 0)
                        {
                            this.performCreateCartEntry (qty, unitOfQty, packingUnit,
                                productName, collectionDescription,
                                cost, costCurrency);
                            this.successMessage =  
                                MultilingualTextProvider.getMessage (StoreMessages.MSG_BUNDLE,
                                    StoreMessages.ML_MSG_PUTINCART, env);
                        } // if
                    } // if
                    else
                    {
                        for (int k = 0; k < nrCodes; k++)
                        {
                            qty = this.env.getIntParam (StoreArguments.ARG_COLLECTION + j + k);
                            String code = this.env.getStringParam (StoreArguments.ARG_CODEX + j + k);
                            if (code != null)
                            {
                                collectionDescription += ", " + code;
                            } // if
                            if (qty > 0)
                            {
                                this.performCreateCartEntry (qty, unitOfQty, packingUnit,
                                    productName, collectionDescription,
                                    cost, costCurrency);
                                this.successMessage =  
                                    MultilingualTextProvider.getMessage (StoreMessages.MSG_BUNDLE,
                                        StoreMessages.ML_MSG_PUTINCART, env);
                            } // if
                        } // for
                    } // else
                    j++;
                } // while (j < i)
                action.commitTrans ();
            } // try
            catch (DBError e)
            {
                this.successMessage =  
                    MultilingualTextProvider.getMessage (StoreMessages.MSG_BUNDLE,
                        StoreMessages.ML_MSG_NOT_PUTINCART, env);
            } // catch DBError
            finally
            {
                this.releaseDBConnection (action);
            } // finally
        } // if (..)
        return letWindowOpen;
    } // processCollectionOrderForm


    //
    // IMPORT / EXPORT METHODS
    //
    /**************************************************************************
     * Reads the object data from an dataelement <BR/>
     *
     * @param dataElement     the dataElement to read the data from
     *
     * @see ibs.bo.BusinessObject#readImportData
     */
    public void readImportData (DataElement dataElement)
    {
        // get business object specific values
        super.readImportData (dataElement);
        // get the type specific values
        if (dataElement.exists (Product_01.FIELD_PRODUCTNO))
        {
            this.productNo = dataElement.getImportStringValue (Product_01.FIELD_PRODUCTNO);
        } // if
        if (dataElement.exists (Product_01.FIELD_EAN_CODE))
        {
            this.ean = dataElement.getImportStringValue (Product_01.FIELD_EAN_CODE);
        } // if
        if (dataElement.exists (Product_01.FIELD_AVAILABLEFROM))
        {
            this.availableFrom = dataElement.getImportDateValue (Product_01.FIELD_AVAILABLEFROM);
        } // if
        if (dataElement.exists (Product_01.FIELD_UNITOFQUANTITY))
        {
            this.unitOfQty = dataElement.getImportIntValue (Product_01.FIELD_UNITOFQUANTITY);
        } // if
        if (dataElement.exists (Product_01.FIELD_PACKINGUNIT))
        {
            this.packingUnit = dataElement.getImportStringValue (Product_01.FIELD_PACKINGUNIT);
        } // if
//        this.thumbAsImage = importElement.getImportStringValue ("thumbAsImage");
//        this.thumbnail = importElement.getImportStringValue ("thumbnail");
//        this.image = importElement.getImportStringValue ("image");
        if (dataElement.exists (Product_01.FIELD_STOCK))
        {
            this.stock = dataElement.getImportStringValue (Product_01.FIELD_STOCK);
        } // if
    } // readImportData


    /**************************************************************************
     * writes the object data to an dataelement <BR/>
     *
     * @param   dataElement The dataElement to write the data to.
     *
     * @see ibs.bo.BusinessObject#writeExportData
     */
    public void writeExportData (DataElement dataElement)
    {
        // set the business object specific values
        super.writeExportData (dataElement);
        // set the type specific values
        dataElement.setExportValue (Product_01.FIELD_PRODUCTNO, this.productNo);
        dataElement.setExportValue (Product_01.FIELD_EAN_CODE, this.ean);
        dataElement.setExportValue (Product_01.FIELD_AVAILABLEFROM, this.availableFrom);
        dataElement.setExportValue (Product_01.FIELD_UNITOFQUANTITY, this.unitOfQty);
        dataElement.setExportValue (Product_01.FIELD_PACKINGUNIT, this.packingUnit);
//        importElement.setExportValue ("thumbAsImage", this.thumbAsImage);
//        importElement.setExportValue ("thumbnail", this.thumbnail);
//        importElement.setExportValue ("image", this.image);
        dataElement.setExportValue (Product_01.FIELD_STOCK, this.stock);
    } // writeExportData

} // class Product_01
