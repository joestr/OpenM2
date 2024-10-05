/*
 * Class: OrderCircleFunction.java
 */

// package:
package m2.store.xml;

// imports:
import ibs.app.CssConstants;
import ibs.bo.BOArguments;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.bo.ObjectClassNotFoundException;
import ibs.bo.ObjectInitializeException;
import ibs.bo.Operations;
import ibs.bo.SelectionList;
import ibs.bo.type.TypeNotFoundException;
import ibs.di.XMLViewer_01;
import ibs.io.Environment;
import ibs.io.HtmlConstants;
import ibs.io.IOHelpers;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.func.FunctionArguments;
import ibs.obj.func.IbsFunction;
import ibs.service.user.User;
import ibs.tech.html.BuildException;
import ibs.tech.html.FormElement;
import ibs.tech.html.InputElement;
import ibs.tech.html.Page;
import ibs.tech.html.ScriptElement;
import ibs.tech.html.TableElement;
import ibs.util.NoAccessException;

import m2.store.StoreArguments;
import m2.store.StoreMessages;
import m2.store.StoreTokens;
import m2.store.xml.OrderService;
import m2.store.xml.ShoppingCartService;
import m2.store.xml.StoreEvents;
import m2.store.xml.XmlStoreTokens;


/******************************************************************************
 * <BR/>
 *
 * In first Version, the function extends BusinessObject, to be able to
 * use the cachingalgorithm of Application - should be an abstract class
 * in final implementation.
 *
 * @version     $Id: OrderCircleFunction.java,v 1.18 2010/04/07 13:37:17 rburgermann Exp $
 *
 * @author      Andreas Jansa (AJ), 000918
 *******************************************************************************
 */
public class OrderCircleFunction extends IbsFunction
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: OrderCircleFunction.java,v 1.18 2010/04/07 13:37:17 rburgermann Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class OrderCircleFunction.
     * <BR/>
     */
    public OrderCircleFunction ()
    {
        // nothing to do
    } // IbsFunction


    /**************************************************************************
     * Initializes a Function. <BR/>
     *
     * The <A HREF="#user">user object</A> is also stored in a specific
     * property of this object to make sure that the user's context can be used
     * for getting his/her rights. <BR/>
     * <A HREF="#env">env</A> is initialized to the provided object. <BR/>
     * <A HREF="#sess">sess</A> is initialized to the provided object. <BR/>
     * <A HREF="#app">app</A> is initialized to the provided object. <BR/>
     *
     * @param   aId     Value for the function id.
     * @param   aUser   Object representing the user.
     * @param   aEnv    The actual environment object.
     * @param   aSess   The actual session info.
     * @param   aApp    The application info.
     */
    public void initFunction (int aId, User aUser, Environment aEnv,
                              SessionInfo aSess, ApplicationInfo aApp)
    {
        super.initFunction (aId, aUser, aEnv, aSess, aApp);

// HACK AJ BEGIN
        // there has to be an empty oid to mark this function as
        // non-physical object. this is necessary for some GUI-Methods which are
        // trying to read rights from DB
        this.oid = OID.getEmptyOid ();
        this.isPhysical = false;
// HACK AJ END
    } // initFunction


    /**************************************************************************
     * Main method = sequence control of this function. <BR/>
     */
    public void start ()
    {
        // get current event
        int event = this.getEvent ();

        // check which event was thrown
        switch (event)
        {
            case StoreEvents.EVT_PUTPRODUCTINSHOPPINGCART:
                this.putProductInShoppingCart ();
                break;

            case StoreEvents.EVT_TRYTOCREATEORDER:
                String orderType = null;
                if ((orderType = this.showOrderTypeSelection ()) != null)
                {
                    this.performShowOrder (orderType);
                } // if
                break;

            case StoreEvents.EVT_CREATEORDER:
                this.showOrder ();
                break;

            default:
                IOHelpers.showMessage ("ERROR: Event " + event + " is not valid for function " + this.id,
                                       this.app, this.sess, this.env);
                break;
        } // switch
    } // start


    /**************************************************************************
     * show the searchform with the selection over all possible queries. <BR/>
     */
    protected void putProductInShoppingCart ()
    {
        ShoppingCartService scs = new ShoppingCartService ();
        scs.initService (this.user, this.env, this.sess, this.app);

        // get oid of product
        OID productOid = this.env.getOidParam ("oid");
        // name of query to get data of product
        String queryName = this.env.getStringParam ("query");
        if (queryName == null)
        {
            queryName = "_putproductinshoppingcartdata";
        } // if

        // get order qty for current line
        int qty = this.env.getIntParam ("count");

        try
        {
            if (scs.addShoppingCartEntry (productOid, qty, queryName))
            {
                // SHOW SUCESS MESSAGE
                // create the answering page with the alert message
                // this message has to be called with temp-frame as target
                Page page = new Page ("inshoppingcart", false);
                ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);

                // put in a script to show success message and close the window
                script.addScript ("alert ('" +  
                    MultilingualTextProvider.getMessage (StoreMessages.MSG_BUNDLE,
                        StoreMessages.ML_MSG_PUTINCART, env) +
                                  "');\n");

                page.body.addElement (script);

                try
                {
                    page.build (this.env);
                } // try
                catch (BuildException e)
                {
                    IOHelpers.showMessage (e.getMsg (),
                                           this.app, this.sess, this.env);
                } // catch
            } // if

        } // try
        catch (NoAccessException e)
        {
            // should not occur
        } // catch
    } // putProductInShoppingCart


   /***************************************************************************
    * Show the selection box if products in shopping cart are from more than one
    * catalog. <BR/>
    *
    * @return   xmltype to be used for order
    *           <CODE>null</CODE> if no sc-entry or no xmltype was set
    */
    public String showOrderTypeSelection ()
    {
//debug ("OrderCircleFunction.showOrderTypeSelection ()");

        String orderType = null;

        String queryStr =
            " SELECT DISTINCT sc.orderType AS oid, sc.orderText AS name " +
            " FROM    m2_ShoppingCartEntry_01 sc, ibs_Object osc, ibs_Workspace wsp " +
            " WHERE   sc.oid = osc.oid " +
            "     AND wsp.shoppingCart = osc.containerId " +
            "     AND osc.state = 2" +
            "     AND wsp.userId = " + this.user.id;


        // get all the selection lists from the database
//debug (queryStr);
        SelectionList orderTypeSelectionList =
            this.performRetrieveSelectionListDataQuery (false, queryStr);

        // don't do anything if only product from one catalog
        if (orderTypeSelectionList.ids[0].length () == 0)
        {
            IOHelpers.showMessage (
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_CART_NOENTRIES, env),
                this.app, this.sess, this.env);
            return null;
        } // if
        else if (orderTypeSelectionList.ids.length == 1)
        {
            orderType = orderTypeSelectionList.ids[0];

            return orderType;
        } // if


        // if more than one order type is possible, show selection list
        Page page = new Page (
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_ORDER, env), false);

        this.insertChangeFormStyles (page);


        FormElement form = this.createFormHeader (page, this.name,
            this.getNavItems (), null, XmlStoreTokens.TOK_ORDERKINDHEADER,
            HtmlConstants.FRM_SHEET, "Order.gif", this.containerName);

        // function
        form.addElement (new InputElement (BOArguments.ARG_FUNCTION,
            InputElement.INP_HIDDEN,
            "" + this.id));

        // event to be thrown on submit:
        form.addElement (new InputElement (FunctionArguments.ARG_EVENT,
            InputElement.INP_HIDDEN, "" +
            StoreEvents.EVT_CREATEORDER));

        TableElement table = this.createFrame (0, 0);

        table.classId = CssConstants.CLASS_INFO;
        String[] classIds = new String[2];
        classIds[0] = CssConstants.CLASS_NAME;
        classIds[1] = CssConstants.CLASS_VALUE;
        table.classIds = classIds;

        table.border = 0;

        // (index of oid: 0)
        this.showFormProperty (table, StoreArguments.ARG_ORDERTYPE,
            XmlStoreTokens.TOK_ORDERKINDSELECTION, Datatypes.DT_SELECT, "",
            orderTypeSelectionList.ids, orderTypeSelectionList.values, 0);
        // add the table
        form.addElement (table);
        // set ok action to
        this
            .createFormFooter (
                form,
                null,
                IOHelpers
                    .getShowObjectJavaScript (this.getUserInfo ().workspace.shoppingCart
                        .toString ()));

        // deactivate tabs and buttons:
        page.body.addElement (this.getButtonsTabsDeactivationScript ());

        // build the page and show it to the user:
        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e.getMsg (), this.app, this.sess, this.env);
        } // catch
        return null;
    } // showCatalogSelection



   /***************************************************************************
    * Displays the orderform. <BR/>
    * The argument ARG_ORDERSELECTION has to exist and has to content the
    * typecode for the order to be created. <BR/>
    *
    * When creating the order, the multilangtoken TOK_DEFAULTORDERNAME is
    * used to initialize the name of the order. <BR/>
    */
    public void showOrder ()
    {
//debug ("OrderCircleFunction.showOrder ()");
        String str = null;

        if ((str = this.env.getStringParam (StoreArguments.ARG_ORDERTYPE)) != null)
        {
            this.performShowOrder (str);
        } // if
    } // showOrder



   /***************************************************************************
    * Displays the orderform and initializes the name of the order with the
    * multilangtoken TOK_DEFAULTORDERNAME. <BR/>
    *
    * @param    orderType   typecode for the orderform to be displayed. <BR/>
    */
    protected void performShowOrder (String orderType)
    {
        XMLViewer_01 order = null;
        String methodName = "OrderCircleFunction.performShowOrder (String)";

        try
        {
            order = (XMLViewer_01) this.getObjectCache ().fetchNewObject (orderType, this.user,
                                                          this.sess, this.env);
        } // try
        catch (TypeNotFoundException e)
        {
            IOHelpers.showMessage (methodName + " TypeNotFoundException [" +
                orderType + "]", this.app, this.sess, this.env);
            return;
        } // catch
        catch (ObjectClassNotFoundException e)
        {
            IOHelpers.showMessage (
                methodName + " ObjectClassNotFoundException", this.app,
                this.sess, this.env);
            return;
        } // catch
        catch (ObjectInitializeException e)
        {
            IOHelpers.showMessage (methodName + " ObjectInitializeException",
                this.app, this.sess, this.env);
            return;
        } // catch


        // get oid of container to create order
        OrderService ods = new OrderService ();
        ods.initService (this.user, this.env, this.sess, this.app);

        order.containerId =
            ods.performRetrieveOrdersContainerOid (this.user.id);

        order.name = "";

        // create order
        order.create (0);

/* KR 020125: not necessary because already done in create
        try
        {
            order.retrieve (Operations.OP_NONE);
        } // try
        catch (ibs.util.NoAccessException e)
        {

        } // catch
        catch (ibs.util.AlreadyDeletedException e)
        {

        } // catch
*/

        order.showChangeForm (Operations.OP_NONE);
    } // performShowOrder

} // OrderCircleFunction
