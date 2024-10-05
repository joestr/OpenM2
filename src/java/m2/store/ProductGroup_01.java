/*
 * Class: ProductGroup_01.java
 */

// package:
package m2.store;

// imports:
import ibs.bo.BOHelpers;
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.ContainerElement;
import ibs.bo.Datatypes;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.bo.SelectionList;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.TableElement;
import ibs.tech.sql.DBError;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;


/******************************************************************************
 * This class represents one container object of type ProductGroup
 * with version 01.
 * <BR/>
 *
 * @version     $Id: ProductGroup_01.java,v 1.29 2013/01/16 16:14:12 btatzmann Exp $
 *
 * @author      Bernhard Walter (BW), 980923
 ******************************************************************************
 */
public class ProductGroup_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ProductGroup_01.java,v 1.29 2013/01/16 16:14:12 btatzmann Exp $";

    /**
     * Headings of columns. <BR/>
     * These headings are shown at the top of lists.
     */
    public static final String [] LST_HEADINGS_PRODUCTGROUP =
    {
        StoreTokens.ML_PRODUCT_DESCRIPTION,
        StoreTokens.ML_HEADINGCART,
        StoreTokens.ML_IMAGE,
    }; // LST_HEADINGS_PRODUCTGROUP

    /**
     * Name of a container column. <BR/>
     * These attributes are used for ordering the elements.
     */
    public static final String [] LST_ORDERINGS_PRODUCTGROUP =
    {
        ProductGroup_01.COL_NAME,
        null,
        null,
    }; // LST_ORDERINGS_PRODUCTGROUP

    // object specific attributes
    /**
     * Oid of productGroup of the CatalogProductGroup. <BR/>
     */
    private OID productGroupOid = null;

    /**
     * productGroup of the CatalogProductGroup. <BR/>
     */
    private String productGroup = null;

    /**
     * Query column: name. <BR/>
     */
    private static final String COL_NAME = "name";


     /******************************************************************************
     * This constructor creates a new instance of the class Store.
     * <BR/>
     */
    public ProductGroup_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();
    } // ProductGroup_01


    /**************************************************************************
     * This constructor creates a new instance of the class Store.
     * <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public ProductGroup_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
    } // ProductGroup_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // initialize the instance's private properties:
        this.procCreate =     "p_ProductGroup_01$create";
        this.procChange =     "p_ProductGroup_01$change";
        this.procRetrieve =   "p_ProductGroup_01$retrieve";
        this.procDelete =     "p_ProductGroup_01$delete";

        this.viewContent = "v_ProductGroup_01$content";

        this.elementClassName = "m2.store.ProductGroupElement_01";

        // set number of parameters for procedure calls:
        this.specificRetrieveParameters = 2;
        this.specificChangeParameters = 1;
    } // initClassSpecifics


    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     * The <A HREF="ibs.bo.BusinessObject.html#env">env</A> property is used
     * for getting the parameters. This property must be set before calling
     * this method. <BR/>
     */
    public void getParameters ()
    {
        // used variables
        OID oid;

        // get parameters relevant for super class:
        super.getParameters ();

        // object specific parameters
        // code
        if ((oid = this.env.getOidParam (StoreArguments.ARG_CPG_PRG)) != null)
        {
            this.productGroupOid = oid;
        } // if
    } // getParameters


    /**************************************************************************
     * Create the query to get the container's content out of the database.
     * <BR/>
     *
     * @return  The constructed query.
     */
    protected String createQueryRetrieveContentData ()
    {
        return
            " SELECT DISTINCT oid, name, description, icon, isNew, " +
            "                 productNo, thumbAsImage, thumbnail, image, " +
            "                 minBuyPrice, maxBuyPrice, costCurrencyPrice, " +
            "                 minSalesPrice, maxSalesPrice, priceCurrency, " +
            "                 path, isLink, linkedObjectId " +
            " FROM    " + this.viewContent + " " +
            " WHERE   containerId = " + this.oid.toStringQu ();
    } // createQueryRetrieveContentData


/**********
 * KR 040612 dropped away because of performance reasons.
 *           This functionality is now fully implemented in ibs.bo.Container.
 * ...
    / **************************************************************************
     * Create the query to check if copied/cutted data is still valid. <BR/>
     * This method can be overwritten in subclasses. <BR/>
     *
     * @param   selectedElements    Elements that were previously copied/cutted.
     *
     * @return  The constructed query.
     * /
    protected String createQueryCopyData (Vector selectedElements)
    {
        String query = "";
        String oidList = "";

        // put all selected elements into a string, where the elements are separated by a comma
        for ( int j=0; j < selectedElements.size (); j++)
        {
            // add only existing elements
            if ( null != selectedElements.elementAt (j))
            {
                if (j > 0)
                    oidList += ", ";

                oidList += ((OID)selectedElements.elementAt (j)).toStringQu ();
            }
        }

        // select the data of all objects that are in the oidList

        query = "SELECT DISTINCT oid, name, description, icon, isNew, " + " " +
                "                productNo, thumbAsImage, thumbnail, image," +
                "                minBuyPrice, maxBuyPrice, costCurrencyPrice, " +
                "                minSalesPrice, maxSalesPrice, priceCurrency," +
                "                path, isLink, linkedObjectId" + " " +
                "FROM    " + this.viewContent + " ";

        // check if there are elements to search for
        if (oidList == "")              // no elements to search for
            // querey should return NO tuples
            query += "WHERE oid <> oid";
        else                            // there are elements to search for
            query += "WHERE oid IN (" + oidList + ")";

        return query;
    } // createQueryCopyData
 * ...
 * KR 040612
 **********/


    /**************************************************************************
     * Get the element type specific data out of the actual tuple of the query
     * result. <BR/>
     *
     * @param   action      The database connection object.
     * @param   commonObj   Object representing the list element.
     *
     * @throws  DBError
     *          Error when executing database statement.
     */
    protected void getContainerElementData (SQLAction action, ContainerElement commonObj)
        throws DBError
    {
        ProductGroupElement_01 obj;

        if ((obj = (ProductGroupElement_01) this.getElement (SQLHelpers
            .getQuOidValue (action, "oid"))) == null ||
            obj == commonObj)
            // if the current Product has never occurred in the resultset before - create new containerelement
        {
            obj = (ProductGroupElement_01) commonObj;

            // get element type specific attributes:
            obj.name = action.getString (ProductGroup_01.COL_NAME);
            obj.description = action.getString ("description");
            obj.icon = action.getString ("icon");
            obj.isNew = action.getBoolean ("isNew");
            if ((this.sess != null) && (this.sess.activeLayout != null))
            {
                obj.layoutpath = this.sess.activeLayout.path;
            } // if
            else
            {
                obj.layoutpath = "";
            } // else

            // special attributes of this class
            obj.productNo = action.getString ("productNo");
            obj.thumbAsImage = action.getBoolean ("thumbAsImage");
            obj.thumbnail = action.getString ("thumbnail");
            obj.image = action.getString ("image");
            obj.isLink = action.getBoolean ("isLink");
            obj.linkedObjectId = SQLHelpers.getQuOidValue (action, "linkedObjectId");

        } // if
        else
            // the current product has already occurred in the resultset
        {
            if (obj != commonObj)
                // check if found object is not the last added object
            {
                // delete last added element (Product) because it was already added to vector elements before
                // index of oidelements.removeElement (commonObj);
            } // if
        } // else

        // get all prices from resultset
        // if any part of one price is null the price is invalid
        // and salesprice and buyprice have to set to PRICE_NOT_SET

        // get values for one price

        Long priceNotSet = new Long (StoreConstants.PRICE_NOT_SET);
        boolean anyPriceWasNull = false;

        // minBuyPrice
        Long minBuyPrice    = new Long (action.getCurrency ("minBuyPrice"));
        if (action.wasNull ())
        {
            anyPriceWasNull = true;
        } // if

        // maxBuyPrice
        Long maxBuyPrice    = new Long (action.getCurrency ("maxBuyPrice"));
        if (action.wasNull ())
        {
            anyPriceWasNull = true;
        } // if

        // minSalesPrice
        Long minSalesPrice  = new Long (action.getCurrency ("minSalesPrice"));
        if (action.wasNull ())
        {
            anyPriceWasNull = true;
        } // if

        // maxSalesPrice
        Long maxSalesPrice  = new Long (action.getCurrency ("maxSalesPrice"));
        if (action.wasNull ())
        {
            anyPriceWasNull = true;
        } // if

        String priceCurrency = action.getString ("priceCurrency");
        String costCurrency = action.getString ("costCurrencyPrice");

        // check if everything was ok
        if (!anyPriceWasNull && priceCurrency != null && costCurrency != null)
        {
            // all parts of price-object where valid
            obj.minBuyPrice.addElement   (minBuyPrice);
            obj.maxBuyPrice.addElement   (maxBuyPrice);
            obj.minSalesPrice.addElement (minSalesPrice);
            obj.maxSalesPrice.addElement (maxSalesPrice);
            obj.priceCurrency.addElement (priceCurrency);
            obj.costCurrency.addElement  (costCurrency);
        } // if
        else
        {
            // something was wrong
            obj.minBuyPrice.addElement   (priceNotSet);
            obj.maxBuyPrice.addElement   (priceNotSet);
            obj.minSalesPrice.addElement (priceNotSet);
            obj.maxSalesPrice.addElement (priceNotSet);
            obj.priceCurrency.addElement (null);
            obj.costCurrency.addElement  (null);
        } // else

        obj.path = action.getString ("path");

        // set webpath in ContainerElement. it's needed to show the Productpicture
        obj.setSess (this.sess);
    } // getContainerElementData


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
        // productGroupOid
        BOHelpers.addInParameter (sp, this.productGroupOid);
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
        // productGroupOid
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        //productGroup
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
        this.productGroupOid = SQLHelpers.getSpOidParam (params[++i]);
        this.productGroup = params[++i].getValueString ();
    } // getSpecificRetrieveParameters


    /**************************************************************************
     * Set the headings for this container. This method MUST be overloaded if. <BR/>
     * you have your own subclass of ContainerElement and if you need other headings. <BR/>
     * You have to overload the method setOrderings () as well.
     */
    protected void setHeadingsAndOrderings ()
    {
        // set headings:
        this.headings = MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE, 
            ProductGroup_01.LST_HEADINGS_PRODUCTGROUP, env);

        // set orderings
        this.orderings = ProductGroup_01.LST_ORDERINGS_PRODUCTGROUP;

        // ensure that there is an available ordering taken:
        this.ensureAvailableOrdering ();
    } // setHeadingsAndOrderings


    /**************************************************************************
     * Represent the properties of a ProductGroup_01 object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     *
     * @see ibs.bo.BusinessObject#showProperties
     * @see ibs.bo.BusinessObject#showProperty
     */
    protected void showProperties (TableElement table)
    {
        // loop through all properties of this object and display them:
        super.showProperties (table);

        // object specific attributes
        this.showProperty (table,
                      StoreArguments.ARG_CPG_PRG,
                      MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                          StoreTokens.ML_CPG_PRG, env),
                      Datatypes.DT_TEXT,
                      this.productGroup);
    } // showProperties


    /**************************************************************************
     * Represent the properties of a ProductGroup_01 object to the user
     * within a form. <BR/>
     *
     * @param   table   Table where the properties shall be added.
     *
     * @see ibs.bo.BusinessObject#showFormProperties
     * @see ibs.bo.BusinessObject#showFormProperty
     */
    protected void showFormProperties (TableElement table)
    {
//debug (" --- ProductGroup_01.showFormProperties ANFANG --- ");
        // loop through all properties of this object and display them:
        super.showFormProperties (table);

        // get all the selection lists from the database
        SelectionList groupSelList = this.performRetrieveSelectionListData (
            this.getTypeCache ().getTVersionId (
                StoreTypeConstants.TC_ProductGroupProfile), true);

        String selectedOid;
        if (this.productGroupOid == null)
        {
            selectedOid = null;
        } // if
        else
        {
            selectedOid = "" + this.productGroupOid;
        } // else

        int selected = 0;

        if (groupSelList != null)
        {
            OID temp;
            for (int i = 0; i < groupSelList.values.length; i++)
            {
                try
                {
                    temp = new OID (groupSelList.ids[i]);
                } // try
                catch (IncorrectOidException err)
                {
                    temp = null;
                } // catch IncorrectOidException
                if ((temp != null) && (temp.equals (this.productGroupOid)))
                {
                    selected = i;
                    selectedOid = groupSelList.ids[i];
                } // if ((temp != null) ...
            } // for (int i = 0; ...
        } // if (groupSelList != null)

        // show Catalog_01 specific attributes
        // (index of oid: selected)
        this.showFormProperty (table, StoreArguments.ARG_CPG_PRG,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_CPG_PRG, env), Datatypes.DT_SELECT, selectedOid,
            groupSelList.ids, groupSelList.values, selected);
// debug (" --- ProductGroup_01.showFormProperties ENDE --- ");
    } // showFormProperties


    /***************************************************************************
     * Sets the buttons that can be displayed when the user is in an object info
     * view. <BR/>
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
            Buttons.BTN_CUT,
            Buttons.BTN_COPY,
            Buttons.BTN_DISTRIBUTE,
        }; // buttons

        // return button array
        return buttons;
    } // showInfoButtons


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
            Buttons.BTN_PASTE,
            Buttons.BTN_REFERENCE,
//            Buttons.BTN_CLEAN,
            Buttons.BTN_SEARCH,
            Buttons.BTN_LISTDELETE,
            Buttons.BTN_LIST_COPY,
//            Buttons.BTN_HELP,
//            Buttons.BTN_LOGIN
        }; // buttons

        // return button array
        return buttons;
    } // setContentButtons

} // class ProductGroup_01
