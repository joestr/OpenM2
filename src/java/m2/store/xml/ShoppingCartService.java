/*
 * Class: ShoppingCartService.java
 */

// package:
package m2.store.xml;

// imports:
import ibs.bo.BOHelpers;
import ibs.bo.BusinessObject;
import ibs.bo.OID;
import ibs.bo.States;
import ibs.io.Environment;
import ibs.io.IOHelpers;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.obj.query.QueryConstants;
import ibs.obj.query.QueryExecutive;
import ibs.service.user.User;
import ibs.tech.sql.DBError;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.StoredProcedure;
import ibs.tech.sql.StoredProcedureConstants;
import ibs.util.Helpers;
import ibs.util.NoAccessException;

import m2.store.StoreTypeConstants;


/******************************************************************************
 * This class represents all necessary properties of one element of a
 * Catalog. <BR/>
 *
 * @version     $Id: ShoppingCartService.java,v 1.9 2009/09/04 20:17:15 kreimueller Exp $
 *
 * @author      Andreas Jansa (BW) 040201
 ******************************************************************************
 */
public class ShoppingCartService extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ShoppingCartService.java,v 1.9 2009/09/04 20:17:15 kreimueller Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class OrderSevice. <BR/>
     */
    public ShoppingCartService ()
    {
        // nothing to do
    } // ShoppingCartService


    ///////////////////////////////////////////////////////////////////////////
    // constants
    ///////////////////////////////////////////////////////////////////////////


    /**
     * Procedure to put the product in the shopping cart. <BR/>
     */
    private static final String PROC_CREATE_CART_ENTRY =
        "p_ShoppingCart_01$createEntry";


    /**************************************************************************
     * Initializes a Servize. <BR/>
     *
     * @param   aUser   Object representing the user.
     * @param   aEnv    The actual environment object.
     * @param   aSess   The actual session info.
     * @param   aApp    The actual application info.
     */
    public void initService (User aUser, Environment aEnv,
                             SessionInfo aSess, ApplicationInfo aApp)
    {
// HACK AJ BEGIN
        // there has to be an empty oid to mark this function as
        // non-physical object. this is necessary for some GUI-Methods which are
        // trying to read rights from DB
        this.oid = OID.getEmptyOid ();
        this.isPhysical = false;
// HACK AJ END
        super.initObject (this.oid, aUser, aEnv, aSess, aApp);
    } // initService


    /**************************************************************************
     * Puts the product order in the shopping cart of current user. <BR/>
     *
     * Uses queryexecutive  as interface. <BR/>
     *
     * Interfacedescription:
     * name             =   _putproductinshoppingcartdata
     * inputparameter   =   productOid
     *                      orderqtyStart
     *                      orderqtyStop
     * outputparameter  =   name
     *                      unitOfQty
     *                      pckUnit
     *                      description
     *                      price
     *                      price2
     *                      price3
     *                      price4
     *                      price5
     *                      currency
     *                      orderType
     *                      orderRespOid
     *                      orderText
     * <BR/>
     *
     * and uses procedure p_ShoppingCart_01$createEntry  to create entry in
     * shoppingcart. <BR/>
     *
     * @param   productOid          The oid of the product to be put in cart
     * @param   qty                 orderquantity
     * @param   queryName           The name of the query to get data of product
     *
     * @return  an array with button ids that can be displayed
     *
     * @exception   NoAccessException
     *              ???
     */
    public boolean addShoppingCartEntry (OID productOid, int qty,
                                         String queryName)
        throws NoAccessException
    {
        // instance query interface
        QueryExecutive qe = new QueryExecutive ();
        String productOidStr = productOid != null ? productOid.toString () : OID.EMPTYOID;

        // leider noch immer notwendig : (
        qe.initObject (this.oid, this.user, this.env, this.sess, this.app);

        // add input parameter for interface
        qe.addInParameter ("productOid", QueryConstants.FIELDTYPE_OBJECTID,
            productOidStr);
        qe.addInParameter ("orderQtyStart", QueryConstants.FIELDTYPE_NUMBER,
            new Integer (qty).toString (), QueryConstants.MATCH_LESSEQUAL);
        qe.addInParameter ("orderQtyStop", QueryConstants.FIELDTYPE_NUMBER,
            new Integer (qty).toString (), QueryConstants.MATCH_GREATEREQUAL);

        String  productName = null;
        int     unitOfQty = 0;
        String  packingUnit = null;
        String  productDescription = null;
        long    price = 0;
        long    price2 = 0;
        long    price3 = 0;
        long    price4 = 0;
        long    price5 = 0;
        String  priceCurrency = null;
        String  orderType = null;
        String  orderResp = null;
        String  orderText = null;

        // if query with name exist and could be executed
        if (qe.execute (queryName))
        {
            // set headers
            if (!qe.getEOF ())
            {
                // getOutParameters
                productName = qe.getColValue ("name");
                unitOfQty = new Integer (qe.getColValue ("unitofqty")).intValue ();
                packingUnit = qe.getColValue ("pckunit");
                productDescription = qe.getColValue ("description");
                price = new Long (Helpers.stringToMoney (
                    qe.getColValue ("price"))).longValue ();
                price2 = new Long (Helpers.stringToMoney (
                    qe.getColValue ("price2"))).longValue ();
                price3 = new Long (Helpers.stringToMoney (
                    qe.getColValue ("price3"))).longValue ();
                price4 = new Long (Helpers.stringToMoney (
                    qe.getColValue ("price4"))).longValue ();
                price5 = new Long (Helpers.stringToMoney (
                    qe.getColValue ("price5"))).longValue ();
                priceCurrency = qe.getColValue ("currency");
                orderType = qe.getColValue ("ordertype");
                orderResp = qe.getColValue ("orderrespoid");
                orderText = qe.getColValue ("ordertext");
            } // if
        } // if
        else
        {
            return false;
        } // else


        if (qe.getRowCount () == 1)
        {
            // create stored procedure call:
            StoredProcedure sp = new StoredProcedure (ShoppingCartService.PROC_CREATE_CART_ENTRY,
                StoredProcedureConstants.RETURN_VALUE);

            // parameter definitions:
            // must be in right sequence (like SQL stored procedure def.)
            // input parameters
            // oid
            BOHelpers.addInParameter (sp, productOid);
            // user id
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.user.id);
            // operation
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, 0);
            // name
            sp.addInParameter (ParameterConstants.TYPE_STRING, productName);
            // tVersionId
            sp.addInParameter (ParameterConstants.TYPE_INTEGER,
                this.getTypeCache ().getTVersionId (
                    StoreTypeConstants.TC_ShoppingCartLine));
            // state
            sp.addInParameter (
                ParameterConstants.TYPE_INTEGER, States.ST_ACTIVE);

            //---------------------------------------------------------------------
            // quantity
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, qty);
            // unitOfQty
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, unitOfQty);
            // packingUnit
            sp.addInParameter (ParameterConstants.TYPE_STRING, packingUnit);
            // productDescription
            sp.addInParameter (
                ParameterConstants.TYPE_STRING, productDescription);
            // price
            sp.addInParameter (ParameterConstants.TYPE_CURRENCY, price);
            // price2
            sp.addInParameter (ParameterConstants.TYPE_CURRENCY, price2);
            // price3
            sp.addInParameter (ParameterConstants.TYPE_CURRENCY, price3);
            // price4
            sp.addInParameter (ParameterConstants.TYPE_CURRENCY, price4);
            // price5
            sp.addInParameter (ParameterConstants.TYPE_CURRENCY, price5);
            // priceCurrency
            sp.addInParameter (ParameterConstants.TYPE_STRING, priceCurrency);
            // orderType
            sp.addInParameter (ParameterConstants.TYPE_STRING, orderType);
            // orderRespOid
            sp.addInParameter (ParameterConstants.TYPE_STRING, orderResp);
            // orderText
            sp.addInParameter (ParameterConstants.TYPE_STRING, orderText);

            // perform the function call:
            BOHelpers.performCallFunctionData (sp, this.env);
        } // if rowcount == 1
        else
        {
            IOHelpers.showMessage (
                "ShoppingCartService.addShoppingCartEntry (" + productOid +
                    "ERROR: Wrong number of resultrows in query " +
                    "_putproductinshoppingcartdata. Number of results = " +
                    qe.getRowCount () + " but should be 1", this.app,
                this.sess, this.env);
            return false;
        } // wrong rowcount
        return true;
    } // addShoppintCartEntry

///////////////////////////////////////////////////////
/////
/////   DRAFT
/////


    /**************************************************************************
     * <BR/>
     *
     * @param   sqlStatement    The SQL statement to be executed.
     *
     * @return  ???
     *
     * @exception   DBError
     *              An error occurred during access to database.
     */
    private boolean performSQLStatement (String sqlStatement) throws DBError
    {

        SQLAction aAction = null;
        // open db connection -  only workaround - db connection must
        // be handled somewhere else
        aAction = this.getDBConnection ();

        // perform the query:
        int rowCount = aAction.execute (sqlStatement, true);

        // check if updatestring was executed
        if (rowCount <= 0)
        {
            return false;
        } //if

        // end transaction
        aAction.end ();

        // close db connection in every case -  only workaround - db connection must
        // be handled somewhere else
        this.releaseDBConnection (aAction);

        return true;
    } // performSQLStatement


    /**************************************************************************
     * Move shoppingcartentry to order.
     *
     * @param   entryOid          The oid of the shoppingcartentry to be moved
     * @param   orderOid          The oid of the order to which the entry should
     *                            be moved
     *
     * @return  true if everything was ok
     */
    public boolean moveEntryToOrder (OID entryOid, OID orderOid)
    {
        // check input parameters
        if (entryOid == null || orderOid == null)
        {
            IOHelpers.showMessage ("ShoppingCartService.moveEntryToOrder " +
                                   "entryOid or orderOid IS NULL",
                                   this.app, this.sess, this.env);

            return false;
        } // if

        String statement =
            " UPDATE ibs_Object SET containerId = " + orderOid.toStringQu () +
            " WHERE oid = " + entryOid.toStringQu ();

        try
        {
            this.performSQLStatement (statement);
        } // try
        catch (DBError e)
        {
            IOHelpers.showMessage ("ShoppingCartService.sendOrder - DBError",
                                   this.app, this.sess, this.env);
            return false;
        } // catch

        return true;
    } // moveEntryToOrder

/////
/////   DRAFT
/////
///////////////////////////////////////////////////////

} // class QueryObjectElement
