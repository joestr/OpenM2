/*
 * Class: PriceContainer_01.java
 */

// package:
package m2.store;

// imports:
import ibs.bo.BOConstants;
import ibs.bo.BOTokens;
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.OID;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;
import ibs.util.NoAccessException;

import m2.store.PriceContainerElement_01;
import m2.store.ProductCode;
import m2.store.StoreTokens;

import java.util.Vector;


/******************************************************************************
 * This class represents one container object of type Store with version 01.
 * <BR/>
 *
 * @version     $Id: PriceContainer_01.java,v 1.17 2010/04/13 15:55:57 rburgermann Exp $
 *
 * @author      Rupert Thurner (RT), 980528
 ******************************************************************************
 */
public class PriceContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: PriceContainer_01.java,v 1.17 2010/04/13 15:55:57 rburgermann Exp $";

    /**
     * Headings of columns. <BR/>
     * These headings are shown at the top of lists.
     */
    public static final String [] LST_HEADINGS_PRICECONTAINER =
    {
        BOTokens.ML_NAME,
        BOTokens.ML_STATE,
        BOTokens.ML_CHANGED,
    }; // LST_HEADINGS_PRICECONTAINER

    /**
     * Name of a container column. <BR/>
     * These attributes are used for ordering the elements.
     */
    public static final String [] LST_ORDERINGS_PRICECONTAINER =
    {
        "name",
        "state",
        "lastChanged",
    }; // LST_ORDERINGS_PRICECONTAINER

    /**
     * The delimiter. <BR/>
     */
    private String delimiter = ";";


    /**************************************************************************
     * This constructor creates a new instance of the class PriceContainer_01.
     * <BR/>
     */
    public PriceContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // PriceContainer_01


    /**************************************************************************
     * This constructor creates a new instance of the class Store.
     * <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public PriceContainer_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
    } // PriceContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set super attribute:
        this.viewContent = "v_PriceContainer$content";
        this.elementClassName = "m2.store.PriceContainerElement_01";
        this.icon = "ProductSizeColorContainer.gif";
    } // initClassSpecifics


    /**************************************************************************
     * Set the headings for this container. This method MUST be overloaded if. <BR/>
     * you have your own subclass of ContainerElement and if you need other headings. <BR/>
     * You have to overload the method setOrderings () as well.
     */
    protected void setHeadingsAndOrderings ()
    {
        // set headings for this container
        this.headings = MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE, 
            PriceContainer_01.LST_HEADINGS_PRICECONTAINER, env);

        // set orderings
        this.orderings = PriceContainer_01.LST_ORDERINGS_PRICECONTAINER;

        // ensure that there is an available ordering taken:
        this.ensureAvailableOrdering ();
    } // setHeadingsAndOrderings


    /**************************************************************************
     * Create the query to get the container's content out of the database.
     * <BR/>
     *
     * @return  The constructed query.
     */
    protected String createQueryRetrieveContentData ()
    {
        return new StringBuffer ()
            .append ("SELECT DISTINCT oid, isNew, icon, description, name, lastChanged,")
            .append ("                priceCurrency, price, costCurrency, cost, categoryName, codeValues")
            .append (" ,validForAllValues")
            .append (" ,qty")
            .append (" FROM    ").append (this.viewContent)
            .append (" WHERE   containerId = ").append (this.oid.toStringQu ())
            .toString ();
    } // createQueryRetrieveContentData


    /**************************************************************************
     * Get the Container's content out of the database. <BR/>
     * <B>THIS METHOD IS A DUMMY WHICH MUST BE OVERWRITTEN IN SUB CLASSES!</B>
     * <BR/>
     * First this method tries to load the objects from the database. During
     * this operation a rights check is done, too. If this is all right the
     * objects are returned otherwise an exception is raised. <BR/>
     *
     * @param   operation        Operation to be performed with the objects.
     * @param   orderBy          Property, by which the result shall be
     *                           sorted. If this parameter is null the
     *                           default order is by name.
     * @param   orderHow         Kind of ordering: BOConstants.ORDER_ASC or
     *                           BOConstants.ORDER_DESC null =>
     *                           BOConstants.ORDER_ASC
     * @param   selectedElements object ids that are marked for paste
     *
     * @exception   NoAccessException
     *              The user does not have access to these objects to perform
     *              the required operation.
     */
    protected void performRetrieveContentData (int operation, int orderBy,
                                               String orderHow,
                                               Vector<OID> selectedElements)
        throws NoAccessException
    {
        String orderHowLocal = orderHow; // variable for local assignments
        SQLAction action = null;        // the action object used to access the
                                        // database
        PriceContainerElement_01 obj = null;
        String str;
        boolean validForAllValues = false;
        int rowCount;                   // row counter
        StringBuffer queryStr = new StringBuffer (); // the query

        this.size = 0;

        // ensure a correct ordering:
        if (!orderHowLocal.equalsIgnoreCase (BOConstants.ORDER_DESC)) // not descending?
        {
            orderHowLocal = BOConstants.ORDER_ASC;       // order ascending
        } // if

        // empty the elements vector:
        this.elements.removeAllElements ();

        // get the elements out of the database:
        // create the SQL String to select all tuples
        queryStr
            .append (this.createQueryRetrieveContentData ())
            .append ("    AND userId = ").append (this.user.id)
            .append (SQLHelpers.getStringCheckRights (operation))
            .append ("ORDER BY ").append (this.orderings[orderBy]).append (" ")
            .append (orderHowLocal);

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();
            rowCount = action.execute (queryStr, false);

            // empty resultset?
            if (rowCount == 0)
            {
                return;                 // terminate this method
            } // if
            // error while executing?
            else if (rowCount < 0)
            {
                return;
            } // else if
            else
            {
                try
                {
                    OID oldOid = OID.getEmptyOid ();
                    OID newOid  = null;

                    ProductCode code = null;
                    // get tuples out of db
                    while (!action.getEOF ())
                    {
                        newOid = SQLHelpers.getQuOidValue (action, "oid");
                        // if this is the same price just add
                        // the code values
                        if (oldOid.equals (newOid))
                        {
                            validForAllValues = action
                                .getBoolean ("validForAllValues");
                            if (!validForAllValues)
                                // price isn`t valid for all codeValues
                            {
                                code = new ProductCode ();
                                code.name = action.getString ("categoryName");
                                code.parseStringToValues (action
                                    .getString ("codeValues"), this.delimiter);
                                obj.codes.addElement (code);
                            } // if
                        } // if
                        else
                        {
                            // if it's not the first price
                            if (obj != null)
                            {
                                this.elements.addElement (obj);
                            } // if
                            obj = new PriceContainerElement_01 ();
                            obj.codes = new Vector<ProductCode> ();
                            obj.oid = newOid;
                            obj.isNew = action.getBoolean ("isNew");
                            obj.icon = action.getString ("icon");
                            obj.description = action.getString ("description");
                            obj.name = action.getString ("name");
                            obj.lastChanged = action.getDate ("lastChanged");
                            obj.priceCurrency = action.getString ("priceCurrency");
                            obj.price = action.getCurrency ("price");
                            obj.costCurrency = action.getString ("costCurrency");
                            obj.cost = action.getCurrency ("cost");
                            obj.qty = action.getInt ("qty");
                            if ((this.sess != null) && (this.sess.activeLayout != null))
                            {
                                obj.layoutpath = this.sess.activeLayout.path;
                            } // if
                            validForAllValues = action.getBoolean ("validForAllValues");
                            if ((str = action.getString ("categoryName")) != null &&
                                !validForAllValues)
                                    // price isn`t valid for all codeValues
                            {
                                code = new ProductCode ();
                                code.name = str;
                                code.parseStringToValues (action.getString ("codeValues"), this.delimiter);
                                obj.codes.addElement (code);
                            } // if
                        } // else
                        oldOid = newOid;
                        this.size++;

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
            } // else
            // the last tuple has been processed
            // end transaction:
            action.end ();
        } // try
        catch (DBError e)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (e, this.app, this.sess, this.env, false);
        } // catch
        finally
        {
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally
    } // performRetrieveContentData


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * container's content view. <BR/>
     *
     * @return  An array with button ids that can potentially be displayed.
     */
    protected int[] setContentButtons ()
    {
        // define buttons to be displayed:
        int[] buttons =
        {
            Buttons.BTN_NEW,
//            Buttons.BTN_PASTE,
//            Buttons.BTN_CLEAN,
//            Buttons.BTN_SEARCH,
            Buttons.BTN_LISTDELETE,
//            Buttons.BTN_LIST_COPY,
//            Buttons.BTN_LIST_DISTRIBUTE,
//            Buttons.BTN_LIST_CUT,
//            Buttons.BTN_LIST_PASTE,
//            Buttons.BTN_LIST_PASTE_LINK
//            Buttons.BTN_HELP,
//            Buttons.BTN_LOGIN
        }; // buttons

        // return button array
        return buttons;
    } // setContentButtons

} // class PriceContainer_01
