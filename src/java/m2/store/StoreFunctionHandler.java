/*
 * Class: StoreFunctionHandler.java
 */

// package:
package m2.store;

// imports:
import ibs.app.AppFunctions;
import ibs.app.func.FunctionValues;
import ibs.app.func.GeneralFunctionHandler;
import ibs.bo.BOHelpers;
import ibs.bo.BOMessages;
import ibs.bo.BOTokens;
import ibs.bo.Buttons;
import ibs.bo.Operations;
import ibs.bo.States;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.service.notification.INotificationService;
import ibs.service.notification.NotificationFailedException;
import ibs.service.notification.NotificationTemplate;
import ibs.tech.html.ButtonBarElement;
import ibs.util.UtilConstants;
import ibs.util.list.IElementId;

import m2.store.OrderNotificationService;
import m2.store.Order_01;
import m2.store.Product_01;
import m2.store.ShoppingCart_01;
import m2.store.StoreTokens;
import m2.store.xml.OrderCircleFunction;

import java.util.Vector;


/******************************************************************************
 * Application object which is created with each call of a page. <BR/>
 * An object of this class represents the interface between the network and the
 * business logic itself. <BR/>
 * It gets arguments from the user, controls the program flow, and sends data
 * back to the user and his browser. <BR/>
 * There has to be generated an extension class of this class to realize the
 * functions which are specific to the required application.
 *
 * @version     $Id: StoreFunctionHandler.java,v 1.66 2010/11/12 10:18:21 btatzmann Exp $
 *
 * @author      Klaus Reimüller (KR), 980402
 ******************************************************************************
 */
public class StoreFunctionHandler extends GeneralFunctionHandler
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: StoreFunctionHandler.java,v 1.66 2010/11/12 10:18:21 btatzmann Exp $";


    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a StoreFunctionHandler object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   id      Id of the element.
     * @param   name    The element's name.
     */
    public StoreFunctionHandler (IElementId id, String name)
    {
        // call constructor of super class:
        super (id, name);

        // initialize the other instance properties:
    } // StoreFunctionHandler


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Evaluate the function to be performed. <BR/>
     *
     * @param   function    The function to be performed.
     * @param   values      The values for the function handler.
     *
     * @return  Function to be performed. <BR/>
     *          <CODE>AppFunctions.FCT_NOFUNCTION</CODE> if there is no function
     *          or the function was already performed.
     */
    @Override
    public int evalFunction (int function, FunctionValues values)
    {
        Product_01 p;
        Order_01 o;

        switch (function)       // perform function
        {
            case StoreFunctions.FCT_LOADORDER_FRAMESET:
                p = (Product_01) values.getObject ();
                // show form
                if (p != null)  // got object?
                {
                    p.loadOrderFrameSet ();
                } // if got object
                break;

            case StoreFunctions.FCT_SHOWORDER_FORM:
                p = (Product_01) values.getObject ();
                // show form
                if (p != null)  // got object?
                {
                    p.showOrderForm ();
                } // if got object
                break;

            case StoreFunctions.FCT_PUTIN_CART:
                p = (Product_01) values.getObject ();
                // show form
                if (p != null)  // got object?
                {
                    p.putProductInShoppingCart ();
                } // if got object
                break;

            case StoreFunctions.FCT_ORDER_FORM1:
                o = (Order_01)
                    values.getNewObject (StoreTypeConstants.TC_Order);
                // show form
                if (o != null)  // got object?
                {
                    if (o.showCatalogSelection ())
                    {
                        o.showOrder ();
                    } // if
                } // if
                break;

            case StoreFunctions.FCT_ORDER_FORM2:
                o = (Order_01)
                    values.getNewObject (StoreTypeConstants.TC_Order);
                // show form
                if (o != null)  // got object?
                {
                    o.showOrder ();
                } // if got object
                break;

            case StoreFunctions.FCT_STORE_ORDER:
                this.storeOrder (values);
                break;

            case StoreFunctions.FCT_SHOWORDER_PRINT:
                o = (Order_01) values.getObject ();
                // show form:
                if (o != null)  // got object?
                {
                    o.showOrderInWindow ();
                } // if got object
                break;

            case StoreFunctions.FCT_SHOWSHOPPINGCART:
                ShoppingCart_01 sc = (ShoppingCart_01) BOHelpers.getObject (
                    values.getUserInfo ().workspace.shoppingCart, values.p_env,
                    false, false, false);

                // check if instance of shopping cart could be created:
                if (sc != null)             // instance was not created?
                {
                    sc.showContent (UtilConstants.REP_STANDARD);
                } // if
                else
                {
                    IOHelpers.showMessage (MultilingualTextProvider
                        .getMessage(BOMessages.MSG_BUNDLE,
                        BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                        values.p_env);
                } // else
                break;

            case StoreFunctions.FCT_SHOWCHANGESTATEORDERFORM:
                if (values.p_oid.type !=
                    values.getTypeCache ().getTypeId (StoreTypeConstants.TC_Order))
                {
                    break;
                } // if

                o = (Order_01) values.getObject ();

                o.changeStateForm = true;
                o.showChangeForm (values.p_representationForm);
                break;

            case StoreFunctions.FCT_XMLORDER_FORM:
                OrderCircleFunction fct = new OrderCircleFunction ();
                fct.initFunction (StoreFunctions.FCT_XMLORDER_FORM,
                    values.getUser (), values.p_env,
                    values.p_sess, values.p_app);
                fct.start ();
                break;

            default:            // unknown function
                // return the function to be performed:
                return function;
        } // switch function

        // return that no further function shall be performed:
        return AppFunctions.FCT_NOFUNCTION;
    } // evalFunction


    /**************************************************************************
     * Store the current order. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    private void storeOrder (FunctionValues values)
    {
        Order_01 o = (Order_01) values
            .getNewObject (StoreTypeConstants.TC_Order);
        // show form
        if (o != null) // got object?
        {
            o.createOrder ();
            o.processState = States.PST_DISCARDED;

            if (o.orderResponsibleOid != null && o.sendOrder)
            // distribute order to order responsible
            {
                Vector<String[]> receivers = new Vector<String[]> ();
                // single object in Vector receivers has to be String
                // array with oid and name of receiver
                String[] singleReceiver =
                {
                    "",
                    "" + o.orderResponsibleOid,
                };
                receivers.addElement (singleReceiver);

                INotificationService ons = o.getNotificationService ();
                ons.initService (values.getUser (), values.p_env,
                    values.p_sess, values.p_app);
                NotificationTemplate template = null;

                // set the template
                template = new NotificationTemplate (o.name, "",
                    MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                        StoreTokens.ML_ORDER_FROM, values.p_env),
                    MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                        BOTokens.ML_ACT_PLEASE_TAKE_NOTE_OF, values.p_env));

                // notification is performed
                try
                {
                    ons.performNotification (receivers, o.oid, template, true);
                    // distribution was ok
                    o.processState = States.PST_ORDERED;

                } // try
                catch (NotificationFailedException e)
                { // notification service failed
                } // catch

            } // if (o.orderResponsibleOid != null && o.sendOrder)

            o.changeProcessState (Operations.OP_CHANGEPROCSTATE +
                o.processState);
        } // if
        values.getUserInfo ().cache.remove (o.oid); // remove from cache
    } // storeOrder


    /**************************************************************************
     * Sets the dependencies of the files filled. <BR/>
     */
    @Override
    public void setDependentProperties ()
    {
        super.setDependentProperties ();

        StoreTokens.setDependentProperties ();
    } // setDependentProperties


    /**************************************************************************
     * Sets all possible buttons and constructs a buttonBar element. <BR/>
     *
     * @param   buttonBar   a ButtonBarElement Object where the buttons shall
     *                      be added to.
     */
    @Override
    public void setButtons (ButtonBarElement buttonBar)
    {
        // call corresponding method of super class:
        super.setButtons (buttonBar);

        // shopping cart:
        this.setButton (buttonBar, Buttons.BTN_SHOPPINGCART,
            "top.loadOrderWindow (" + StoreFunctions.FCT_LOADORDER_FRAMESET +
                ")", 0, 0);

        // order:
        this.setButton (buttonBar, Buttons.BTN_ORDER, IOHelpers
            .getLoadJavaScript (StoreFunctions.FCT_ORDER_FORM1), 0, 0);

        // change order state:
        this.setButton (buttonBar, Buttons.BTN_CHANGEORDERSTATE, IOHelpers
            .getLoadJavaScript (StoreFunctions.FCT_SHOWCHANGESTATEORDERFORM),
            0, 0);

        // show order window:
        this.setButton (buttonBar, Buttons.BTN_PRINT, "top.loadInWindow (" +
            StoreFunctions.FCT_SHOWORDER_PRINT + ")", 0, 0);
    } // setButtons

} // class StoreFunctionHandler
