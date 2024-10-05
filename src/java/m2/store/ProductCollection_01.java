/*
 * Class: ProductCollection_01.java
 */

// package:
package m2.store;

// imports:
import ibs.app.AppConstants;
import ibs.bo.BOArguments;
import ibs.bo.BOHelpers;
import ibs.bo.BusinessObject;
import ibs.bo.Buttons;
import ibs.bo.ContainerElement;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.bo.Operations;
import ibs.io.HtmlConstants;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.LayoutConstants;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.BlankElement;
import ibs.tech.html.Font;
import ibs.tech.html.GroupElement;
import ibs.tech.html.IE302;
import ibs.tech.html.InputElement;
import ibs.tech.html.Page;
import ibs.tech.html.RowElement;
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
import ibs.util.DateTimeHelpers;
import ibs.util.FormFieldRestriction;
import ibs.util.Helpers;
import ibs.util.NoAccessException;
import ibs.util.UtilConstants;

import m2.store.ProductCode;
import m2.store.ProductCollectionElement;
import m2.store.ProductCollectionElementProperty;
import m2.store.Product_01;
import m2.store.StoreArguments;
import m2.store.StoreConstants;
import m2.store.StoreMessages;
import m2.store.StoreTokens;

import java.util.Date;
import java.util.Vector;


/******************************************************************************
 * This class represents one BusinessObject of type ProductCollection with
 * version 01. It implements the handling of a collection of products with
 * a maximum of 2 codes (eg. size and/or color). For more than 2 codes
 * the class ProductCollectionAsContainer has to be used.
 * <BR/>
 *
 * @version     $Id: ProductCollection_01.java,v 1.20 2013/01/16 16:14:12 btatzmann Exp $
 *
 * @author      Bernhard Walter (BW), 990115
 ******************************************************************************
 */
public class ProductCollection_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ProductCollection_01.java,v 1.20 2013/01/16 16:14:12 btatzmann Exp $";


    ///////////////////////////////////////////////////////////////////////////
    // properties
    ///////////////////////////////////////////////////////////////////////////
    /**
     * The cost of a product = the price for a dealer. <BR/>
     */
    public long cost = 0;
    /**
     * Currency used for the cost. <BR/>
     */
    public String costCurrency = "";
    /**
     * The total quantity of products in this collection. <BR/>
     */
    public int totalQuantity = 0;
    /**
     * The date from which the price is valid. <BR/>
     */
    public Date validFrom = DateTimeHelpers.getCurAbsDate (); // set current date

    /**
     * What kind of codes are in this collection. <BR/>
     */
    public OID categoryOidX = null;
    /**
     * What kind of codes are in this collection. <BR/>
     */
    public OID categoryOidY = null;
    /**
     * How many codes are used in this collection. <BR/>
     */

    public int nrDims = 0;
    /**
     * The procedure to create the elements of a collection. <BR/>
     */
    private String procCreateQty = null;
    /**
     * Oid of the product. <BR/>
     */
    private OID productOid = null;
    /**
     * The product object. <BR/>
     */
    private Product_01 product = null;
    /**
     * Number of product codes. <BR/>
     */
    private int nrCodes;
    /**
     * The tracer. <BR/>
     */
    public static String trace = "";
    /**
     * The product codes. <BR/>
     */
    private ProductCode[] codes;

    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * This constructor creates a new instance of the class product.
     * <BR/>
     */
    public ProductCollection_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
        // initialize instance variables
        this.name = "";
        this.description = "";
    } // ProductCollection_01


    /**************************************************************************
     * This constructor creates a new instance of the class product.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public ProductCollection_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);

        // initialize properties common to all subclasses:
        // initialize instance variables
        this.name = "";
        this.description = "";
    } // ProductCollection_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        this.procCreate =       "p_ProductCollect_01$create";
        this.procChange =       "p_ProductCollect_01$change";
        this.procRetrieve =     "p_ProductCollect_01$retrieve";
        this.procDelete =       "p_ProductCollect_01$delete";
        this.procCreateQty =    "p_ProductCollect_01$createQty";

        this.specificRetrieveParameters = 9;
        this.specificChangeParameters = 7;
    } // initClassSpecifics


    /**************************************************************************
     * Set the icon of the actual business object. <BR/>
     * If the icon is already set this method leaves it as is.
     * If there is no icon defined yet, the icon name is derived from the name
     * of the type of this object. <BR/>
     */
    protected void setIcon ()
    {
        this.icon = "ProductCollection.gif";
    } // setIcon


    ///////////////////////////////////////////////////////////////////////////
    // functions called from application level
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     * The <A HREF="ibs.bo.BusinessObject.html#env">env</A> property is used
     * for getting the parameters. This property must be set before calling
     * this method. <BR/>
     */
    public void getParameters ()
    {
        OID oid;
        OID first;
        OID second;
        String str = "";
        int nrRows = 0;
        int nrColumns = 0;
        int nrInputs = 0;
        Date date = null;
        boolean processMatrix = false;
        boolean processX = false;
        long longval;
        String argPrefix = null;
        OID categoryOid = null;
        OID categoryOid2 = null;
        String argCode = null;
        String argCode2 = null;
        String code2 = null;
        int j = 0;

        super.getParameters ();
        if ((str = this.env.getStringParam (StoreArguments.ARG_COSTCURRENCY)) != null)
        {
            this.costCurrency = str;
        } // if
        // cost of a product
        if ((longval = Helpers.stringToMoney (this.env.getStringParam (StoreArguments.ARG_COST))) != -1)
        {
            this.cost = longval;
        } // if
        // the date from which offer is valid
        if ((date = this.env.getDateParam (StoreArguments.ARG_VALIDFROM)) != null)
        {
            this.validFrom = date;
        } // if
        // the chosen codes
        if ((oid = this.env.getOidParam (StoreArguments.ARG_CATEGORYOID + "0")) != null)
        {
            this.categoryOidX = oid;
        } // if
        if ((oid = this.env.getOidParam (StoreArguments.ARG_CATEGORYOID + "1")) != null)
        {
            this.categoryOidY = oid;
        } // if

        first = this.env.getOidParam (StoreArguments.ARG_CODE + "0");
        second = this.env.getOidParam (StoreArguments.ARG_CODE + "1");

        // codes in the matrix - may be in arbitr. order
        nrColumns = this.env.getIntParam (StoreArguments.ARG_NR_VALUES + "0");
        nrRows = this.env.getIntParam (StoreArguments.ARG_NR_VALUES + "1");

//trace (""+first+"-"+second+"-"+this.categoryOidX+"-"+this.categoryOidY+"-"+nrColumns+"-"+nrRows);

        // determine whether to process the matrix or only one dimension
        if (nrRows <= 1 || (first != null && second != null))
        {
            processMatrix = true;
        } // if
        else if ((first == null) && (second == null))
        {
            return;
        } // else if
        else
        {
            if ((first != null) && (first.equals (this.categoryOidX)))
            {
                processX = true;
            } // if
            if ((second != null) && (second.equals (this.categoryOidX)))
            {
                processX = true;
            } // if
        } // else
//        this.trace ("nach processMatrix");

        // process the collection matrix
        if (processMatrix)
        {
            this.nrDims = 2;

            if (nrRows > 0)
            {
                nrInputs = nrColumns * nrRows;
            } // if
            else
            {
                nrInputs = nrColumns;
            } // else
            argPrefix = StoreArguments.ARG_COLLECTION_PREFIX;
            categoryOid = this.categoryOidX;
            argCode = StoreArguments.ARG_CODEX;
            categoryOid2 = this.categoryOidY;
            argCode2 = StoreArguments.ARG_CODEY;
        } // if processMatrix
        // only process one dimension
        else if (processX)
        {
//            this.trace ("processX");
            this.nrDims = 1;
            argPrefix = StoreArguments.ARG_COLLECTION_PREFIX1;
            categoryOid = this.categoryOidX;
            argCode = StoreArguments.ARG_CODEX1;
        } // else if (processX)
        else // processY
        {
//            this.trace ("processX");
            this.nrDims = 1;

            nrInputs = nrRows;
            argPrefix = StoreArguments.ARG_COLLECTION_PREFIX2;
            categoryOid = this.categoryOidY;
            argCode = StoreArguments.ARG_CODEY1;
        } // else

//      this.trace ("nrInputs=" + nrInputs);
        this.elements = new Vector<ContainerElement> ();

        while (j < nrInputs)
        {
            int qty;
            // get the quantity
            if ((qty = this.env.getIntParam (argPrefix + j)) !=
                IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
            {
//                this.trace ("qty=" + qty);
                this.totalQuantity += qty;
                ProductCollectionElement pce = new ProductCollectionElement ();
                ProductCollectionElementProperty pcep = new ProductCollectionElementProperty ();
                pce.quantity = qty;
                pce.properties = new Vector<ProductCollectionElementProperty> ();
                pcep.categoryOid = categoryOid;
//                this.trace ("categoryOid=" + categoryOid);
                pcep.value = this.env.getStringParam (argCode + j);
                pce.properties.addElement (pcep);
                if (argCode2 != null &&
                    (code2 = this.env.getStringParam (argCode2 + j)) != null)
                {
//                    this.trace ("categoryOid=" + categoryOid);
                    pcep = new ProductCollectionElementProperty ();
                    pcep.categoryOid = categoryOid2;
                    pcep.value = code2;
                    pce.properties.insertElementAt (pcep, 0);
                } // if
                this.elements.addElement (pce);
                // put into the database
            } // if
            j++;
        } // while (j < i)
    } // getParameters


    ///////////////////////////////////////////////////////////////////////////
    // viewing functions
    ///////////////////////////////////////////////////////////////////////////


    /**************************************************************************
     * Represent the properties of a ProductCollection_01 object to the user.
     * <BR/>
     *
     * @param   table   Table where the properties shall be added.
     *
     * @see ibs.IbsObject#showProperty (ibs.tech.html.TableElement, java.lang.String, java.lang.String, int, ibs.service.user.User, java.util.Date)
     */
    protected void showProperties (TableElement table)
    {
        this.showProperty (table, BOArguments.ARG_NAME,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_COLLECTIONNAME, env),
            Datatypes.DT_NAME, this.name);
        // show the select for the currency
        this.showProperty (table, StoreArguments.ARG_COSTCURRENCY,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_CURRENCY, env),
            Datatypes.DT_NAME, this.costCurrency);
        // show the cost of the product
        this.showProperty (table, StoreArguments.ARG_COST,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_COST, env),
            Datatypes.DT_TEXT, "" + Helpers.moneyToString (this.cost));
        // show the valid from date
        this.showProperty (table, StoreArguments.ARG_VALIDFROM,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_VALIDFROM, env),
            Datatypes.DT_DATE, this.validFrom);
        // total quantity of products in this collection
        this.showProperty (table, StoreArguments.ARG_QTY,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_TOTALQUANTITY, env),
            Datatypes.DT_INTEGER, this.totalQuantity);
        this.showProperty (table, "", 
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_COLLECTIONELEMENTS, env),
            Datatypes.DT_DESCRIPTION, "" + this.showCollection ());
    } // showProperties


    /***************************************************************************
     * Represent the properties of a ProductCollection_01 object to the user
     * within a form. <BR/>
     *
     * @param table Table where the properties shall be added.
     *
     * @see ibs.IbsObject#showFormProperty (ibs.tech.html.TableElement,
     *      java.lang.String, java.lang.String, int, ibs.service.user.User)
     */
    protected void showFormProperties (TableElement table)
    {
        // the name of the collection
        this
            .showFormProperty (table, BOArguments.ARG_NAME,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_COLLECTIONNAME, env),
                Datatypes.DT_NAME, this.name);
        // show the select for the currency
        this.showFormProperty (table, StoreArguments.ARG_COSTCURRENCY,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_CURRENCY, env),
            Datatypes.DT_SELECT, this.costCurrency,
            UtilConstants.TOK_CURRENCIES, UtilConstants.TOK_CURRENCIES, 1);

        // price must not be negative (>= 0)
        this.formFieldRestriction = new FormFieldRestriction (false, 0, 0, "0", null);

        // show the cost of the product
        this.showFormProperty (table, StoreArguments.ARG_COST,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_COST, env),
            Datatypes.DT_MONEY, "" + Helpers.moneyToString (this.cost));
        // show the valid from date
        this.showFormProperty (table, StoreArguments.ARG_VALIDFROM,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_VALIDFROM, env),
            Datatypes.DT_DATE, this.validFrom);

        // retrieve the product
        this.retrieveProduct ();
        // show the checkboxes
        if (this.codes.length > 1)
        {
            this.showCodesSelection (table, StoreArguments.ARG_CODE,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_COLLECTION_CODES, env));
        } // if

        // the matrix with max. 2 productcodecategories
        TableElement innerTable = this.createFrame (0, 0);
        innerTable.cellpadding = 5;
        innerTable.border = 0;

        this.showCollectionMatrix (innerTable);

        TableDataElement td = new TableDataElement (innerTable);
        td.colspan = 2;
        td.width = HtmlConstants.TAV_FULLWIDTH;
        RowElement row = new RowElement (1);
        row.addElement (td);
        table.addElement (row);
    } // showFormProperties


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

        // cost
        sp.addInParameter (ParameterConstants.TYPE_CURRENCY, this.cost);
        // costCurrency
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.costCurrency);
        // totalQuantity
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.totalQuantity);
        // validFrom
        sp.addInParameter (ParameterConstants.TYPE_DATE, this.validFrom);
        // categoryOidX
        sp.addInParameter (ParameterConstants.TYPE_STRING, (this.categoryOidX != null) ?
            this.categoryOidX.toString (): null);
        // categoryOidY
        sp.addInParameter (ParameterConstants.TYPE_STRING, (this.categoryOidY != null) ?
            this.categoryOidY.toString (): null);
        // nrDims
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.nrDims);
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
        try
        {
            int i;
            int nrElements = this.elements.size ();
            int id = -1;
            ProductCollectionElement elem = null;
            String updateStr = "";

            // start a new transaction
            // insert the values
            if (this.elements != null)
            {
                action.beginTrans ();
                for (i = 0; i < nrElements; i++)
                {
                    elem = (ProductCollectionElement) this.elements.elementAt (i);
                    if (elem != null)
                    {
                        int nrProps = elem.properties.size ();
                        ProductCollectionElementProperty prop = null;

                        id = this.performCreateQty (elem.quantity);

                        for (int j = 0; j < nrProps; j++)
                        {
                            prop = elem.properties.elementAt (j);
                            if (prop != null)
                            {
                                updateStr =
                                        " INSERT INTO m2_ProductCollectionValue_01 (id, categoryOid, value) " +
                                        " VALUES (" + id + "," + prop.categoryOid.toStringQu () + ",'" + prop.value + "')";

                                // execute the queryString, indicate that we're  performing an
                                // action query:
                                action.execute (updateStr, true);
                            } // if
                        } // for
                    } // if elem != null
                } // for
                action.commitTrans ();
            } // if this.elements != null
        } // try
        catch (NoAccessException e)
        {
            // show Message to the user
            this.showNoAccessMessage (Operations.OP_READ);
        } // catch
    } // performChangeSpecificData


    /**************************************************************************
     * Store a new quantity element in the database. <BR/>
     *
     * @param   quantity    The quantity value.
     *
     * @return  id of the newly created object entry
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    protected int performCreateQty (int quantity) throws NoAccessException
    {
        // create stored procedure call:
        StoredProcedure sp = new StoredProcedure (this.procCreateQty,
            StoredProcedureConstants.RETURN_VALUE);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // input parameters
        // objectId
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.oid.toString ());
        // quantity
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, quantity);
        // output parameter id
        Parameter idParam = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);

        // perform the function call:
        BOHelpers.performCallFunctionData (sp, this.env);

        return idParam.getValueInteger ();
    } // performCreateQty


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

        // productOid
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // nrCodes
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
        // cost
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_CURRENCY);
        // costCurrency
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // totalQuantity
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
        // validFrom
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_DATE);
        // categoryOidX
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // categoryOidY
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // nrDims
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);

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

        this.productOid = SQLHelpers.getSpOidParam (params[++i]);
        this.nrCodes = params[++i].getValueInteger ();
        this.cost = params[++i].getValueCurrency ();
        this.costCurrency = params[++i].getValueString ();
        this.totalQuantity = params[++i].getValueInteger ();
        this.validFrom = params[++i].getValueDate ();
        this.categoryOidX = SQLHelpers.getSpOidParam (params[++i]);
        this.categoryOidY = SQLHelpers.getSpOidParam (params[++i]);
        this.nrDims = params[++i].getValueInteger ();
    } // getSpecificRetrieveParameters


    /**************************************************************************
     * Retrieve the type specific data that is not got from the stored
     * procedure. <BR/>
     * This method must be overwritten by all subclasses that have to get type
     * specific data that cannot be got from the retrieve data stored procedure.
     *
     * @param   action  SQLAction for Databaseoperation.
     *
     * @exception    DBError
     *               This exception is always thrown, if there happens an error
     *               during accessing data.
     */
    protected void performRetrieveSpecificData (SQLAction action) throws DBError
    {
        try
        {
            // retrieve the code values of that product
            this.performRetrieveCollectionProperties (action);
        } // try
        catch (NoAccessException e)
        {
            // send message to the user:
            this.showNoAccessMessage (Operations.OP_READ);
        } // catch
    } // performRetrieveSpecificData


    /**************************************************************************
     * ????
     *
     * @param   action  SQLAction for Databaseoperation.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    protected void performRetrieveCollectionProperties (SQLAction action)
        throws NoAccessException
    {
        int rowCount  = 0;

        try
        {
            String queryStr = " " +
                " SELECT  DISTINCT id, quantity, categoryName, categoryOid, value " +
                " FROM    v_ProductCollection$content " +
                " WHERE   collectionOid = " + this.oid.toStringQu ()  +
                " ORDER BY id";

            rowCount = action.execute (queryStr, false);

            // everything ok - go on
            if (rowCount > 0)
            {
                try
                {
                    int oldId = -1;
                    int newId;
                    ProductCollectionElement obj = null;
                    ProductCollectionElementProperty prop = null;

                    this.elements = new Vector<ContainerElement> ();
                    // get tuples out of db
                    while (!action.getEOF ())
                    {
                        newId = action.getInt ("id");
                        // if this is the same object just add
                        // the code values
                        if (oldId == newId)
                        {
                            prop = new ProductCollectionElementProperty ();
                            prop.categoryOid = SQLHelpers.getQuOidValue (action, "categoryOid");
                            prop.categoryName = action.getString ("categoryName");
                            prop.value = action.getString ("value");
                            obj.properties.addElement (prop);
                        } // if
                        else
                        {
                            String str;

                            // if it's not the first object
                            if (obj != null)
                            {
                                this.elements.addElement (obj);
                            } // if
                            obj = new ProductCollectionElement ();
                            obj.properties = new Vector<ProductCollectionElementProperty> ();
                            obj.quantity = action.getInt ("quantity");
                            if ((str = action.getString ("categoryName")) != null)
                            {
                                prop = new ProductCollectionElementProperty ();
                                prop.categoryOid = SQLHelpers.getQuOidValue (action, "categoryOid");
                                prop.categoryName = str;
                                prop.value = action.getString ("value");
                                obj.properties.addElement (prop);
                            } //if
                        } // else
                        oldId = newId;
                       // step one tuple ahead for the next loop
                        action.next ();
                    } // while
                    this.elements.addElement (obj);
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
    } // performRetrieveCollectionProperties


    /**************************************************************************
     * Represent the object, i.e. its properties, to the user within a form.
     * <BR/>
     *
     * @param   representationForm  Kind of representation.
     * @param   function            Function to be called when the user clicks
     *                              on the OK button.
     */
    protected void performShowChangeForm (int representationForm, int function)
    {
        // check if sortiment is possible - it is only possible for 2 codes maximum
        if (this.nrCodes <= 0)
        {
            IOHelpers.showMessage ( 
                MultilingualTextProvider.getMessage (StoreMessages.MSG_BUNDLE,
                    StoreMessages.ML_MSG_NOCODES, this.env),
                this.app, this.sess, this.env);
            return;
        } // if
        else if (this.nrCodes > 2)
        {
            IOHelpers.showMessage ( 
                MultilingualTextProvider.getMessage (StoreMessages.MSG_BUNDLE,
                    StoreMessages.ML_MSG_TOMANY_CODES, this.env),
                this.app, this.sess, this.env);
            return;
        } // else

        super.performShowChangeForm (representationForm, function);
    } // performShowChangeForm


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
//            Buttons.BTN_EDIT,
            Buttons.BTN_DELETE,
//            Buttons.BTN_CUT,
//            Buttons.BTN_COPY,
//            Buttons.BTN_DISTRIBUTE,
//            Buttons.BTN_CLEAN,
//            Buttons.BTN_SEARCH,
//            Buttons.BTN_HELP,
//            Buttons.BTN_LOGIN,
//            Buttons.BTN_SHOPPINGCART,
        }; // buttons

        // return button array
        return buttons;
    } // setInfoButtons

    ///////////////////////////////////////////////////////////////////////////
    // Private
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Show the elements in the collection. <BR/>
     *
     * @return  The string containing the collection text.
     */
    public String showCollection ()
    {
        StringBuffer collectionInfo = new StringBuffer ();

        if (this.elements != null)
        {
            ProductCollectionElement elem;
            ProductCollectionElementProperty prop;
            boolean first = true;
            String oldValue = "";
            String newValue;
            int i = 0;

            try
            {
                while (true)
                {
                    elem = (ProductCollectionElement) this.elements.elementAt (i);
                    if (elem != null)
                    {
                        int nrProps = elem.properties.size ();
                        prop = elem.properties.elementAt (0);
                        newValue = prop.value;
                        if (first)
                        {
                            first = false;
                        } // if
                        else
                        {
                            collectionInfo.append (", ");
                        } // else

                        if (!oldValue.equals (newValue))
                        {
                            if (oldValue.length () > 0 && nrProps > 1)
                            {
                                collectionInfo.append (IE302.TAG_NEWLINE);
                            } // if
                            collectionInfo.append (newValue + ": ");
                            // not the first one
                            oldValue = newValue;
                        } // if
                        collectionInfo.append ("" + elem.quantity);
                        if (nrProps > 1)
                        {
                            prop = elem.properties.elementAt (1);
                            collectionInfo.append ("x" + prop.value);
                        } // if
                    } // if
                    i++;
                } // while
            } // try
            catch (ArrayIndexOutOfBoundsException  e)
            {
                // only end condition
            } // catch
        } // if (this.elements != null)

        return collectionInfo.toString ();
    } // showCollection


    /**************************************************************************
     * Show checkboxes with the codes to let the user select what kind of codes
     * are in this product collection. <BR/>
     *
     * @param   table       Table into which to fill the output.
     * @param   fieldName   Name of field used for input.
     * @param   name        Display name.
     */
    private void showCodesSelection (TableElement table, String fieldName,
                                     String name)
    {
        int nrCodes = this.codes.length;
        TextElement text;
        RowElement tr = new RowElement (2);
        GroupElement gel;
        InputElement elem;
        Font nameFont = new Font (AppConstants.FONT_NAME, AppConstants.FONTSIZE_NAME);
                                        // Font for name of property

        text = new TextElement (name + ": ");
        text.font = nameFont;
        tr.addElement (new TableDataElement (text));

        gel = new GroupElement ();
        for (int i = 0; i < nrCodes; i++)
        {
            elem = new InputElement (fieldName + i, InputElement.INP_CHECKBOX,
                "" + this.codes[i].categoryOid);
            elem.checked = true;
            gel.addElement (elem);
            text = new TextElement (this.codes[i].name);
            gel.addElement (text);
        } // for i
        tr.addElement (new TableDataElement (gel));
        table.addElement (tr);
    } // showCodesSelection


    /**************************************************************************
     * Insert style sheet information in a standard change form. <BR/>
     *
     * @param   page    The page which contains the change form.
     */
    protected void insertChangeFormStyles (Page page)
    {
        // Stylesheetfile wird geladen
        StyleSheetElement style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () + "/" +
            this.sess.activeLayout.elems[LayoutConstants.SHEETINFO].styleSheet;
        page.head.addElement (style);

        style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () + "/" +
            this.sess.activeLayout.elems[LayoutConstants.PRODUCTCATALOG].styleSheet;
        page.head.addElement (style);
    } // insertChangeFormStyle


    /**************************************************************************
     * Build the collection matrix (size/color) for this product. <BR/>
     *
     * @param t     The table to add the generated matrix
     */
    private void showCollectionMatrix (TableElement t)
    {
        try
        {
            TableElement table = new TableElement ();
            Font headerFont;
            Font valueFont;
            Font nameFont;
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
            String txtAll = "alle";


            ProductCollection_01.trace += "1 ";
            // determine the codes for the x and y axis
            // the one with more values is used for the horizontal dimension
            codeX  = this.codes[0];
            if (this.codes.length > 1)
            {
                ProductCollection_01.trace += "2 " + this.codes.length;
                codeY = this.codes[1];
                if (codeY.values.length > codeX.values.length)
                {
                    ProductCollection_01.trace += "3 ";
                    ProductCode temp = codeX;
                    codeX = codeY;
                    codeY = temp;
                } // if
                nrRows = codeY.values.length;
            } // if
            nrColumns = codeX.values.length;
            ProductCollection_01.trace += "4 ";

            // set table attributes
            // set table attributes
            table.classId = StoreConstants.CLASS_PRODUCT_ORDER_MATRIX;
            table.border = 1;
            table.ruletype = IOConstants.RULE_NONE;
            table.frametypes = IOConstants.FRAME_BOX;
            table.cellpadding = 5;
            table.cellspacing = 0;

            // set the font for the header and the first column
            headerFont = new Font ();
            headerFont.bold = true;
            headerFont.italic = true;
            valueFont = new Font ();
            valueFont.italic = true;
            nameFont = new Font ();
            nameFont.italic = true;
            nameFont.bold = true;

            // ------------first table row
            group = new GroupElement ();

            row = new RowElement (3);


            td = new TableDataElement (new BlankElement ());
            td.classId = StoreConstants.CLASS_PRODUCT_ORDER_DIMENSION;
            row.addElement (td);

            if (nrRows > 1)
            {   // number of Codecategories is 2
                row.addElement (td);
            } // if

            text = new TextElement (codeX.name);
            group.addElement (text);
            // add the number of input fields as hidden fields
            input = new InputElement (StoreArguments.ARG_NR_VALUES + "0",
                                      InputElement.INP_HIDDEN,
                                      "" + nrColumns);
            group.addElement (input);
            input = new InputElement (StoreArguments.ARG_NR_VALUES + "1",
                                      InputElement.INP_HIDDEN,
                                      "" + nrRows);
            group.addElement (input);
            // add the category oid of the x axis
            input = new InputElement (StoreArguments.ARG_CATEGORYOID + 0,
                                      InputElement.INP_HIDDEN,
                                      "" + codeX.categoryOid);
            group.addElement (input);
            // add the category oid of the y axis
            if (nrRows > 0)
            {
                input = new InputElement (StoreArguments.ARG_CATEGORYOID + 1,
                                          InputElement.INP_HIDDEN,
                                          "" + codeY.categoryOid);
                group.addElement (input);
            } // if
            td = new TableDataElement (group);
            td.classId = StoreConstants.CLASS_PRODUCT_ORDER_DIMENSION;


            td.colspan = nrColumns + 1;

            td.alignment = IOConstants.ALIGN_CENTER;
            row.addElement (td);
            table.addElement (row);
            // ------------end of first table row

            // ------------second table row
            // add a blank element
            text = null;


            if (nrRows > 1)
            // number of Codecategories is 2
            {
                row = new RowElement (nrColumns + 3);
                td = new TableDataElement (new BlankElement ());

                td.classId = StoreConstants.CLASS_PRODUCT_ORDER_DIMENSION;
                row.addElement (td);
            } // if
            else
            // number of Codecategories is 1
            {
                row = new RowElement (nrColumns + 1);
            } // else

            td = new TableDataElement (new BlankElement ());
            td.classId = StoreConstants.CLASS_PRODUCT_ORDER_VALUE;
            row.addElement (td);
            // add as column header
            // add first column header
            if (nrRows > 1)
            // number of Codecategories is 2
            {
                text = new TextElement (txtAll);
                td = new TableDataElement (text);
                td.alignment = IOConstants.ALIGN_MIDDLE;

                td.classId = StoreConstants.CLASS_PRODUCT_ORDER_HEADER;
                row.addElement (td);
            } // if nRows > 1
            // add other column headers
            for (int j = 0; j < codeX.values.length; j++)
            {
                text = new TextElement (codeX.values[j]);
                td = new TableDataElement (text);
                td.alignment = IOConstants.ALIGN_MIDDLE;
                td.classId = StoreConstants.CLASS_PRODUCT_ORDER_VALUE;
                row.addElement (td);
            } // for
            table.addElement (row);
            // ------------end of second table row

            // ------------third table row
            if (nrRows > 1)
            {
                // new row
                row = new RowElement (nrColumns + 3);

                // ------------first table column
                text = new TextElement (codeY.name);
                td = new TableDataElement (text);
                td.classId = StoreConstants.CLASS_PRODUCT_ORDER_DIMENSION;

                td.valign = IOConstants.ALIGN_MIDDLE;

                if (nrRows > 1)
                {
                    td.rowspan = nrRows + 1;
                } // if
                else
                {
                    td.rowspan = nrRows;
                } // else

                row.addElement (td);
                //-------------end of first table column

                text = new TextElement (txtAll);
                td = new TableDataElement (text);
                td.alignment = IOConstants.ALIGN_MIDDLE;
                td.classId = StoreConstants.CLASS_PRODUCT_ORDER_HEADER;
                row.addElement (td);

                td = new TableDataElement (new BlankElement ());
                td.classId = StoreConstants.CLASS_PRODUCT_COLLECTION_SPECIAL;
                row.addElement (td);

                // add the values in the x axis of the table
                for (int j = 0; j < codeX.values.length; j++)
                {
                    group = new GroupElement ();
                    input = new InputElement (StoreArguments.ARG_COLLECTION_PREFIX1 + j,
                                              InputElement.INP_TEXT, "");
                    input.size = StoreConstants.CONST_MAX_QTY;
                    input.maxlength = StoreConstants.CONST_MAX_QTY;
                    group.addElement (input);

                    input = new InputElement (StoreArguments.ARG_CODEX1 + j,
                                              InputElement.INP_HIDDEN,
                                              codeX.values [j]);
                    group.addElement (input);
                    td = new TableDataElement (group);
                    td.alignment = IOConstants.ALIGN_MIDDLE;
                    td.classId = StoreConstants.CLASS_PRODUCT_COLLECTION_SPECIAL;
                    row.addElement (td);
                } // for
                table.addElement (row);
            } // if nrRows > 1
            // ------------end of third table row

            int j = 0;
            int k = 0;
            int l = 0;

            if (nrRows > 1)
            // number of Codecategories is 2
            {
                row = new RowElement (nrColumns + 2);
            } // if
            else
            // number of Codecategories is 2
            {
                row = new RowElement (nrColumns + 1);
            } // else

            // go through all y - codes of the product
            while (found)
            {
                ProductCollection_01.trace += "6 " + nrRows + "/" + nrColumns;
                // if y-codes defined
                if (nrRows > 0)
                {
                    ProductCollection_01.trace += "7 ";
                    valueY = codeY.values[k];
                    text = new TextElement (valueY);
                    if (++k >= nrRows)
                    {
                        found = false;
                    } // if
                } // if
                else
                {
                    valueY = null;       // set dummy entry
                    found = false;      // go only through first loop
                    text = new TextElement ("");
                } // else
                ProductCollection_01.trace += "8 ";

                // input for the y values independent of x
                if (nrRows > 1)
                {
                    // show the value
                    td = new TableDataElement (text);
                    td.alignment = IOConstants.ALIGN_MIDDLE;
                    td.classId = StoreConstants.CLASS_PRODUCT_ORDER_VALUE;
                    row.addElement (td);
                    group = new GroupElement ();
                    input = new InputElement (StoreArguments.ARG_COLLECTION_PREFIX2 + l,
                                              InputElement.INP_TEXT, "");
                    input.size = StoreConstants.CONST_MAX_QTY;
                    input.maxlength = StoreConstants.CONST_MAX_QTY;
                    group.addElement (input);

                    input = new InputElement (StoreArguments.ARG_CODEY1 + l,
                                              InputElement.INP_HIDDEN,
                                              valueY);
                    group.addElement (input);
                    td = new TableDataElement (group);
                    td.classId = StoreConstants.CLASS_PRODUCT_COLLECTION_SPECIAL;
                    l++;
                } // if
                else
                {
                    td = new TableDataElement (new BlankElement ());
                    td.classId = StoreConstants.CLASS_PRODUCT_ORDER_VALUE;
                } // else
                row.addElement (td);

                ProductCollection_01.trace += "9 ";
                // go through all x-codes and show the input box
                for (int i = 0; i < nrColumns; i++)
                {
                    ProductCollection_01.trace += "10 ";
                    group = new GroupElement ();
                    ProductCollection_01.trace += "11 ";
                    valueX = codeX.values [i];
                    // add the text field to enter the quantity
                    input = new InputElement (StoreArguments.ARG_COLLECTION_PREFIX + j,
                                              InputElement.INP_TEXT, "");
                    input.size = StoreConstants.CONST_MAX_QTY;
                    input.maxlength = StoreConstants.CONST_MAX_QTY;
                    group.addElement (input);

                    // hidden input to show which value
                    input = new InputElement (StoreArguments.ARG_CODEX + j,
                                              InputElement.INP_HIDDEN,
                                              valueX);
                    group.addElement (input);
                    if (nrRows > 0)
                    {
                        input = new InputElement (StoreArguments.ARG_CODEY + j,
                                                  InputElement.INP_HIDDEN,
                                                  valueY);
                        group.addElement (input);
                    } // if
                    ProductCollection_01.trace += "14 ";
                    td = new TableDataElement (group);
                    td.alignment = IOConstants.ALIGN_MIDDLE;
                    td.classId = StoreConstants.CLASS_PRODUCT_ORDER_QUANTITY;
                    ProductCollection_01.trace += "15 ";
                    row.addElement (td);
                    ProductCollection_01.trace += "16 ";
                    j++;
                } // for
                table.addElement (row);
                row = new RowElement (nrColumns + 2);
            } // while (found)

            // add the generated table to the table above
            td = new TableDataElement (table);
            td.alignment = IOConstants.ALIGN_CENTER;
            row = new RowElement (1);
            row.addElement (td);
            t.addElement (row);
        } // try
        catch (Exception e)
        {
            this.trace (ProductCollection_01.trace);
        } // catch
    } // showCollectionMatrix


    /**************************************************************************
     * Retrieve the product info. <BR/>
     */
    private void retrieveProduct ()
    {
        this.product = (Product_01) BOHelpers.getObject (
            this.productOid, this.env, false, false, false);
        this.codes = this.product.getProductCodes ();
    } // retrieveProduct

} // class ProductCollection_01
