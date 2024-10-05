/*
 * Class: Price_01.java
 */

// package:
package m2.store;

// imports:
import ibs.app.AppConstants;
import ibs.bo.BOArguments;
import ibs.bo.BOListConstants;
import ibs.bo.BusinessObject;
import ibs.bo.Buttons;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.Font;
import ibs.tech.html.GroupElement;
import ibs.tech.html.InputElement;
import ibs.tech.html.TableElement;
import ibs.tech.sql.DBError;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;
import ibs.util.DateTimeHelpers;
import ibs.util.FormFieldRestriction;
import ibs.util.Helpers;
import ibs.util.NoAccessException;
import ibs.util.UtilConstants;

import m2.store.ProductCode;
import m2.store.StoreArguments;
import m2.store.StoreTokens;

import java.util.Date;
import java.util.Vector;


/******************************************************************************
 * This class represents one BusinessObject of type Price with version 01.
 * <BR/>
 *
 * @version     $Id: Price_01.java,v 1.20 2013/01/16 16:14:12 btatzmann Exp $
 *
 * @author      Bernhard Walter (BW), 981228
 ******************************************************************************
 */
public class Price_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Price_01.java,v 1.20 2013/01/16 16:14:12 btatzmann Exp $";


    // special Price_01 attributes
    /**
     * Currency used for the cost. <BR/>
     */
    public String currency = "";
    /**
     * The cost of a product = the price for a dealer. <BR/>
     */
    public long cost = 0;
    /**
     * The price of a product for a customer. <BR/>
     */
    public long price = 0;
    /**
     * User defined money value. <BR/>
     */
    public long oldCost = 0;
    /**
     * User defined money value. <BR/>
     */
    public long oldPrice = 0;
    /**
     * The date from which the price is valid. <BR/>
     */
    public Date validFrom = DateTimeHelpers.getCurAbsDate (); // set current date

    /**
     * The quantity for which that price is valid. <BR/>
     */
    public int qty = 1;
    /**
     * The array of code values for which this price is valid. <BR/>
     */
    public Vector<ProductCode> codes = null;

    /**
     * . <BR/>
     */
    public OID productOid = null;

    /**
     * . <BR/>
     */
    public static final String DELIMITER = ";";
    /**
     * . <BR/>
     */
    public String priceCurrency = null;


    /**************************************************************************
     * Creates a Price object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     */
    public Price_01 ()
    {
        // call constructor of super class:
        super ();
    } // Price_01


    /**************************************************************************
     * This constructor creates a new instance of the class Price.
     * <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public Price_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
    } // Price_01


    /**************************************************************************
     * This constructor creates a new instance of the class Price and
     * sets the values of the relevat price info. <BR/>
     * <BR/>
     *
     * @param   currency    The currency of the price.
     * @param   cost        The price costs.
     * @param   price       The price value.
     * @param   oldCost     The old costs.
     * @param   oldPrice    The old price value.
     */
    public Price_01 (String currency, long cost, long price,
                     long oldCost, long oldPrice)
    {
        // call constructor of super class:
        super ();

        // initialize properties common to all subclasses:
        this.currency = currency;
        this.cost = cost;
        this.price = price;
        this.oldCost = oldCost;
        this.oldPrice = oldPrice;
    } // Price_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // initialize the instance's private properties:
        this.procCreate =     "p_Price_01$create";
        this.procChange =     "p_Price_01$change";
        this.procRetrieve =   "p_Price_01$retrieve";
        this.procDelete =     "p_Price_01$delete";

        this.name = 
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_PRICE, env);
        this.description = 
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_PRICE, env);
        this.icon = "ProductSizeColor.gif";

        // set number of parameters for procedure calls:
        this.specificRetrieveParameters = 9;
        this.specificChangeParameters = 8;
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
        // costCurrency
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.currency);
        // cost
        sp.addInParameter (ParameterConstants.TYPE_CURRENCY, this.cost);
        // priceCurrency
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.currency);
        // price
        sp.addInParameter (ParameterConstants.TYPE_CURRENCY, this.price);
        // oldCost
        sp.addInParameter (ParameterConstants.TYPE_CURRENCY, this.oldCost);
        // oldPrice
        sp.addInParameter (ParameterConstants.TYPE_CURRENCY, this.oldPrice);
        // validFrom
        sp.addInParameter (ParameterConstants.TYPE_DATE, this.validFrom);
        // qty
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.qty);
    } // setSpecificChangeParameters


    /**************************************************************************
     * Change all type specific data that is not changed by performChangeData.
     * <BR/>
     * This method must be overwritten by all subclasses that have to change
     * type specific data.
     *
     * @param   action  The database connection object.
     *
     * @throws  DBError
     *          This exception is always thrown, if there happens an error
     *          during accessing data.
     */
    protected void performChangeSpecificData (SQLAction action) throws DBError
    {
        if (this.codes != null)
        {
            int nrCodes = this.codes.size ();
            ProductCode code = null;
            for (int j = 0; j < nrCodes; j++)
            {
                code = this.codes.elementAt (j);
                int validForAllValues = 0;

                if (code.priceValidForAllValues)
                {
                    validForAllValues = 1;
                } // if
                String updateStr =
                    " UPDATE m2_PriceCodeValues_01" +
                    " SET codeValues = \'" + code.toString (Price_01.DELIMITER) + "\'," +
                    "     validForAllValues = " + validForAllValues +
                    " WHERE priceOid = " + this.oid.toStringQu () +
                    " AND categoryOid = " + code.categoryOid.toStringQu ();

                // execute the queryString, indicate that we're not performing an
                // action query:
                try
                {
                    action.execute (updateStr, true);
                } // try
                catch (DBError dbErr)
                {
                    this.env.write (updateStr);
                    // an error occurred - show name and info
                    IOHelpers.showMessage (dbErr, this.app, this.sess, this.env, false);
                    break;
                } // catch
            } // for (int j = 0; j < nrCodes; j++)
        } // if (this.codes != null)
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
        // costCurrency
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // cost
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_CURRENCY);
        // priceCurrency
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // price
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_CURRENCY);
        // oldCost
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_CURRENCY);
        // oldPrice
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_CURRENCY);
        // validFrom
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_DATE);
        // qty
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
        // productOid
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
        // costCurrency
        this.currency = params[++i].getValueString ();
        // cost
        this.cost = params[++i].getValueCurrency ();
        // priceCurrency
        this.priceCurrency = params[++i].getValueString ();
        // price
        this.price = params[++i].getValueCurrency ();
        // oldCost
        this.oldCost = params[++i].getValueCurrency ();
        // oldPrice
        this.oldPrice = params[++i].getValueCurrency ();
        // validFrom
        this.validFrom  = params[++i].getValueDate ();
        // qty
        this.qty = params[++i].getValueInteger ();
        // productOid
        this.productOid = SQLHelpers.getSpOidParam (params[++i]);
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
            this.performRetrieveCodeValues (action);
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
            IOHelpers.showMessage (allErrors, this.app, this.sess, this.env);
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
//debug ("in getparameters");
        // get parameters relevant for super class:
        super.getParameters ();

        String str = "";
        int num = 0;
        long longval = 0;
        Date date = null;


        // buying currency
        if ((str = this.env.getStringParam (StoreArguments.ARG_COSTCURRENCY)) != null)
        {
            this.currency = str;
        } // if
        else
        {
            this.currency = "";
        } // else
        // cost of a product
        if ((longval = Helpers.stringToMoney (this.env
            .getStringParam (StoreArguments.ARG_COST))) != -1)
        {
            this.cost = longval;
        } // if
        else
        {
            this.cost = 0;
        } // else
        // old costs of a product
        if ((longval = Helpers.stringToMoney (this.env
            .getStringParam (StoreArguments.ARG_OLDCOST))) != -1)
        {
            this.oldCost = longval;
        } // if
        else
        {
            this.oldCost = 0;
        } // else
        // selling price
        if ((longval = Helpers.stringToMoney (this.env
            .getStringParam (StoreArguments.ARG_PRICE))) != -1)
        {
            this.price = longval;
        } // if
        else
        {
            this.price = 0;
        } // else
        // old price of a product
        if ((longval = Helpers.stringToMoney (this.env
            .getStringParam (StoreArguments.ARG_OLDPRICE))) != -1)
        {
            this.oldPrice = longval;
        } // if
        else
        {
            this.oldPrice = 0;
        } // else
        // the date from which offer is valid
        if ((date = this.env.getDateParam (StoreArguments.ARG_VALIDFROM)) != null)
        {
            this.validFrom = date;
        } // if
        // the date until this offer is valid
        if ((date = this.env.getDateParam (BOArguments.ARG_VALIDUNTIL)) != null)
        {
            this.validUntil = date;
        } // if
        // quantity
        if ((num = this.env.getIntParam (StoreArguments.ARG_QTY)) !=
            IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            this.qty = num;
        } // if

        // get the parameters for the product properties
        int nrCodes = 0;
        // how many codes
        if ((nrCodes = this.env.getIntParam (StoreArguments.ARG_NRCODES)) !=
            IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            this.codes = new Vector<ProductCode> (nrCodes);
            ProductCode code;
            for (int i = 0; i < nrCodes; i++)
            {
                code = new ProductCode ();
                code.categoryOid = this.env.getOidParam (StoreArguments.ARG_CODECATEGORY + i);
                code.priceValidForAllValues =
                    this.env.getBoolParam (StoreArguments.ARG_VALIDFORALLVALUES + i) ==
                    IOConstants.BOOLPARAM_TRUE;
                if (!code.priceValidForAllValues)
                {
                    code.values = this.env
                        .getMultipleFormParam (StoreArguments.ARG_CODE + i);
                } // if
                else
                {
                    code.values = null;
                } // else
                this.codes.addElement (code);
            } // for (int i = 0; i < nrCodes; i++)
        } // if ((nrCodes = ..
    } // getParameters


    /**************************************************************************
     * ????
     *
     * @param   action  SQLAction for Databaseoperation.
     *
     * @throws  NoAccessException
     *          The user does not have access to this object to perform the
     *          required operation.
     */
    protected void performRetrieveCodeValues (SQLAction action)
        throws NoAccessException
    {
        int rowCount  = 0;

        try
        {
            String queryStr = " " +
                " SELECT  DISTINCT categoryOid, categoryName, " +
                "         productCodeValues, codeValues, validForAllValues" +
                " FROM    v_PriceCodeValues " +
                " WHERE   priceOid = " + this.oid.toStringQu ();

//debug (queryStr);

            rowCount = action.execute (queryStr, false);
            // everything ok - go on
            if (rowCount > 0)
            {
                try
                {
                    this.codes = new Vector<ProductCode> (10, 5);

                    ProductCode code = null;
                    // get tuples out of db
                    int j = 0;
                    while (!action.getEOF ())
                    {
                        code = new ProductCode ();
                        code.name = action.getString ("categoryName");
                        code.parseStringToPossibleValues (action
                            .getString ("productCodeValues"),
                            Price_01.DELIMITER);
                        code.parseStringToValues (action
                            .getString ("codeValues"), Price_01.DELIMITER);
                        code.categoryOid = SQLHelpers.getQuOidValue (action,
                            "categoryOid");
                        code.priceValidForAllValues = action
                            .getBoolean ("validForAllValues");
//trace ("retrieve " + code.name + "/" + code.priceValidForAllValues);
                        this.codes.addElement (code);
                        j++;
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
    } // performRetrieveCodeValues


    /**************************************************************************
     * Represent the properties of a Price_01 object to the user. <BR/>
     *
     * @param   table   Table where the properties shall be added.
     *
     * @see ibs.bo.BusinessObject#showProperty
     */
    protected void showProperties (TableElement table)
    {
        // loop through all properties of this object and display them:
        String costInfo = null;
        String priceInfo = null;

        costInfo = this.currency + " " + Helpers.moneyToString (this.cost);

        priceInfo = this.currency + " " + Helpers.moneyToString (this.price);

        // if currency is not EURO - show euro amount for this price and cost
        if (!UtilConstants.TOK_CURRENCY_EUR.equals (this.currency))
        {
            costInfo += "/" +
                UtilConstants.TOK_CURRENCY_EUR +
                " " +
                Helpers.moneyToString (Helpers.getEuroAmount (this.currency,
                    this.cost));

            priceInfo += "/" +
                UtilConstants.TOK_CURRENCY_EUR +
                " " +
                Helpers.moneyToString (Helpers.getEuroAmount (this.currency,
                    this.price));
        } // if

        if (this.oldCost > 0)
        {
            costInfo = costInfo + " " + 
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_INSTEAD, env) + " " +
                this.currency + " " + Helpers.moneyToString (this.oldCost);
        } // if
        if (this.oldPrice > 0)
        {
            priceInfo = priceInfo + " " + 
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_INSTEAD, env) + " " +
                this.currency + " " + Helpers.moneyToString (this.oldPrice);
        } // if

        // if currency is not EURO - show euro amount for this old price and
        // old cost
        if (!UtilConstants.TOK_CURRENCY_EUR.equals (this.currency))
        {
            if (this.oldCost > 0)
            {
                costInfo += "/" +
                    UtilConstants.TOK_CURRENCY_EUR +
                    " " +
                    Helpers.moneyToString (Helpers.getEuroAmount (
                        this.currency, this.oldCost));
            } // if

            if (this.oldPrice > 0)
            {
                priceInfo += "/" +
                    UtilConstants.TOK_CURRENCY_EUR +
                    " " +
                    Helpers.moneyToString (Helpers.getEuroAmount (
                        this.currency, this.oldPrice));
            } // if
        } // if

        // show the cost of the product
        this.showProperty (table, StoreArguments.ARG_COST,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_COST, env),
            Datatypes.DT_TEXT, costInfo);
        // show the price of the product
        this.showProperty (table, StoreArguments.ARG_PRICE,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_SALESPRICE, env),
            Datatypes.DT_TEXT, priceInfo);
        // show the validfrom date
        this.showProperty (table, StoreArguments.ARG_VALIDFROM,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_VALIDFROM, env),
            Datatypes.DT_DATE, this.validFrom);
        // show the valid until date
        this.showProperty (table, BOArguments.ARG_VALIDUNTIL,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_COSTGUARANTEEDTILL, env),
            Datatypes.DT_DATE, this.validUntil);


        if (this.codes != null)
        {
            ProductCode code = null;
            int size = this.codes.size ();
            for (int j = 0; j < size; j++)
            {
                code = this.codes.elementAt (j);
                this.showProperty (table, StoreArguments.ARG_CODEX, code.name,
                    Datatypes.DT_TEXT, code.toString (", "));
            } // for j
        } // if

        // the quantity for which this price is valid
        this.showProperty (table, StoreArguments.ARG_QTY, 
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_QTY, env),
            Datatypes.DT_INTEGER, this.qty);
    } // showProperties


    /**************************************************************************
     * Represent the properties of a Price_01 object to the user
     * within a form. <BR/>
     *
     * @param   table   Table where the properties shall be added.
     *
     * @see ibs.bo.BusinessObject#showFormProperty
     */
    protected void showFormProperties (TableElement table)
    {
        Font nameFont =
            new Font (AppConstants.FONT_NAME, AppConstants.FONTSIZE_NAME);
                                        // Font for name of property
        nameFont.bold = true;
        Font valueFont =
            new Font (AppConstants.FONT_VALUE, AppConstants.FONTSIZE_VALUE);
                                        // Font for value of property
        GroupElement hiddenFields = new GroupElement ();
                                        // a Vector holding the hidden fields
                                        // of the object

        // show the select for the currency
        this.showFormProperty (table, StoreArguments.ARG_COSTCURRENCY,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_CURRENCY, env),
            Datatypes.DT_SELECT, this.currency,
            UtilConstants.TOK_CURRENCIES, UtilConstants.TOK_CURRENCIES, 1);

        // show the cost of the product

        // price must not be negative (>= 0)
        this.formFieldRestriction = new FormFieldRestriction (false, 0, 0, "0",
            null);

        this.showFormProperty (table, StoreArguments.ARG_COST,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_COST, env),
            Datatypes.DT_MONEY, "" + Helpers.moneyToString (this.cost));

        // show the old cost of the product
        // price must not be negative (>= 0)
        this.formFieldRestriction = new FormFieldRestriction (false, 0, 0, "0",
            null);
        this.showFormProperty (table, StoreArguments.ARG_OLDCOST,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_OLDCOST, env),
            Datatypes.DT_MONEY, "" + Helpers.moneyToString (this.oldCost));

        // price must not be negative (>= 0)
        this.formFieldRestriction = new FormFieldRestriction (false, 0, 0, "0",
            null);
        // show the price
        this.showFormProperty (table, StoreArguments.ARG_PRICE,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_SALESPRICE, env), 
            Datatypes.DT_MONEY, "" + Helpers.moneyToString (this.price));

        // show the old price
        // price must not be negative (>= 0)
        this.formFieldRestriction = new FormFieldRestriction (false, 0, 0, "0", null);

        this.showFormProperty (table, StoreArguments.ARG_OLDPRICE,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_OLDPRICE, env),
            Datatypes.DT_MONEY, "" + Helpers.moneyToString (this.oldPrice));
        // show the valid from date
        this.showFormProperty (table, StoreArguments.ARG_VALIDFROM,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_VALIDFROM, env),
            Datatypes.DT_DATE, this.validFrom);
        // show the valid until date
        this.showFormProperty (table, BOArguments.ARG_VALIDUNTIL,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_COSTGUARANTEEDTILL, env),
            Datatypes.DT_DATE, this.validUntil);


        // show the codes
        if (this.codes != null)
        {
            int nrCodes = this.codes.size ();
            boolean checked = false;
            ProductCode code = null;

            // show the number of codes as a hidden field
            hiddenFields.addElement (new InputElement (
                StoreArguments.ARG_NRCODES, InputElement.INP_HIDDEN, "" +
                    nrCodes));
            for (int i = 0; i < nrCodes; i++)
            {
                code = this.codes.elementAt (i);
                if ((code.values == null) || code.values.length == 0 ||
                    code.priceValidForAllValues)
                {
                    checked = true;
                } // if
                code.showFormPropertyWithOptionField (table
                                , code.name
                                , MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                                    StoreTokens.ML_PRICEVALIDFOR, env) + " " + code.name
                                , MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                                    StoreTokens.ML_VALIDFORVALUES, env)
                                , StoreArguments.ARG_CODE + i
                                , StoreArguments.ARG_VALIDFORALLVALUES + i
                                , checked
                                , nameFont
                                , valueFont
                                , BOListConstants.LST_CLASSINFOROWS[i % 2]);
                hiddenFields.addElement (new InputElement (
                    StoreArguments.ARG_CODECATEGORY + i,
                    InputElement.INP_HIDDEN, "" + code.categoryOid));
                checked = false;
            } // for
        } // if

        // the quantity for which this price is valid
        this.showFormProperty (table, StoreArguments.ARG_QTY,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_QTY, env),
            Datatypes.DT_INTEGER, this.qty);
    } // showFormProperties


    /***************************************************************************
     * Sets the buttons that can be displayed when the user is in a object info
     * view. <BR/> This method can be overwritten in subclasses to redefine the
     * set of buttons that can be displayed. <BR/>
     *
     * @return An array with button ids that can be displayed.
     */
    protected int[] setInfoButtons ()
    {
        // define buttons to be displayed:
        int [] buttons =
        {
            Buttons.BTN_EDIT,
            Buttons.BTN_DELETE,
//            Buttons.BTN_CUT,
//            Buttons.BTN_COPY,
//            Buttons.BTN_DISTRIBUTE,
//            Buttons.BTN_CLEAN,
//            Buttons.BTN_SEARCH,
//            Buttons.BTN_HELP,
//            Buttons.BTN_LOGIN,
        }; // buttons

        // return button array
        return buttons;
    } // setInfoButtons

} // class Price_01
