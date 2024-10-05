/*
 * Class: ProductProperties_01.java
 */

// package:
package m2.store;

// imports:
import ibs.bo.BOArguments;
import ibs.bo.BOHelpers;
import ibs.bo.BOTokens;
import ibs.bo.BusinessObject;
import ibs.bo.Buttons;
import ibs.bo.Datatypes;
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
import ibs.util.StringHelpers;

import m2.store.StoreArguments;
import m2.store.StoreTokens;
import m2.store.StoreTypeConstants;


/******************************************************************************
 * This class represents one BusinessObject of type Properties with version 01.
 * <BR/>
 *
 * @version     $Id: ProductProperties_01.java,v 1.16 2013/01/16 16:14:12 btatzmann Exp $
 *
 * @author      Bernhard Walter (BW), 980527
 ******************************************************************************
 */
public class ProductProperties_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ProductProperties_01.java,v 1.16 2013/01/16 16:14:12 btatzmann Exp $";


    /**
     * Oid of category. <BR/>
     */
    private OID categoryOid = null;
    /**
     * Name of category. <BR/>
     */
    private String categoryName = null;
    /**
     * This string holds all the values. <BR/>
     */
    private String values = null;
    /**
     * This string holds the values (max. 255 characters). <BR/>
     */
    private String values1 = null;
    /**
     * This string holds the values (max. 255 characters). <BR/>
     */
    private String values2 = null;
    /**
     * This string holds the values (max. 255 characters). <BR/>
     */
    private String values3 = null;
    /**
     * This string holds the values (max. 255 characters). <BR/>
     */
    private String values4 = null;
    /**
     * Delimiter used to separate between properties. <BR/>
     */
    private static final String CONST_DELIMITER = ";";

    /**
     * Icon for the object. <BR/>
     */
    private static final String CLASS_ICON = "ProductProperties.gif";


    /**************************************************************************
     * Creates a Properties_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     */
    public ProductProperties_01 ()
    {
        // call constructor of super class:
        super ();
    } // Properties_01


    /**************************************************************************
     * This constructor creates a new instance of the class Properties_01.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public ProductProperties_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
    } // Properties_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // initialize the instance's private properties:
        this.procCreate =     "p_ProdProperties_01$create";
        this.procChange =     "p_ProdProperties_01$change";
        this.procRetrieve =   "p_ProdProperties_01$retrieve";
        this.procDelete =     "p_ProdProperties_01$delete";
        this.categoryOid = OID.getEmptyOid ();
        this.icon = ProductProperties_01.CLASS_ICON;

        // set number of parameters for procedure calls:
        this.specificChangeParameters = 6;
        this.specificRetrieveParameters = 7;
    } // initClassSpecifics


    /**************************************************************************
     * Set the icon of the actual business object. <BR/>
     * If the icon is already set this method leaves it as is.
     * If there is no icon defined yet, the icon name is derived from the name
     * of the type of this object. <BR/>
     */
    protected void setIcon ()
    {
        this.icon = ProductProperties_01.CLASS_ICON;
    } // setIcon


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

        // get parameters relevant for super class:
        super.getParameters ();

        if ((oid = this.env.getOidParam (StoreArguments.ARG_CATEGORYOID)) != null)
        {
            this.categoryOid = oid;
        } // if
        // if the value string could be longer than 255 characters then
        // at this point a partitioning in values1, values2... has to be
        // done for the database
        // currently not implemented
        if ((str = this.env.getStringParam (StoreArguments.ARG_VALUES)) != null)
        {
            this.values1 = StringHelpers.changeDelimiter (str, "\015\012",
                ProductProperties_01.CONST_DELIMITER);
        } // if
    } // getParameters


    /**************************************************************************
     * Represent the properties of a Properties_01 object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     *
     * @see ibs.bo.BusinessObject#showProperty
     */
    protected void showProperties (TableElement table)
    {
        this.showProperty (table
                        , BOArguments.ARG_NAME
                        , MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NAME, env)
                        , Datatypes.DT_NAME
                        , this.name);
        // display the object in the news
        this.showProperty (table, BOArguments.ARG_INNEWS, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_INNEWS, env),
            Datatypes.DT_BOOL, "" + this.showInNews);
        // show object specific attributes
        // categoryName
        this.showProperty (table
                        , BOArguments.ARG_NOARG
                        , MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                              StoreTokens.ML_CATEGORY, env)
                        , Datatypes.DT_TEXT
                        , this.categoryName);
        this.showProperty (table
                        , StoreArguments.ARG_VALUES
                        , MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                              StoreTokens.ML_VALUES, env)
                        , Datatypes.DT_DESCRIPTION
                        , this.values);
    } // showProperties


    /**************************************************************************
     * Represent the properties of a Properties_01 object to the user
     * within a form. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     *
     * @see ibs.bo.BusinessObject#showFormProperty
     */
    protected void showFormProperties (TableElement table)
    {
//debug (" --- ProductProperties_01.showFormProperties ANFANG --- ");
        SelectionList categorySelList = this.performRetrieveSelectionListData (
            this.getTypeCache ().getTVersionId (StoreTypeConstants.TC_PropertyCategory), false);
        // loop through all properties of this object and display them:
        this.showFormProperty (table
                            , BOArguments.ARG_NAME
                            , MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NAME, env)
                            , Datatypes.DT_NAME
                            , this.name);
        OID selectedOid;
        if (this.categoryOid == null)
        {
            selectedOid = OID.getEmptyOid ();
        } // if
        else
        {
            selectedOid = this.categoryOid;
        } // else
        // display the object in the news
        this.showFormProperty (table, BOArguments.ARG_INNEWS, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_INNEWS, env),
                          Datatypes.DT_BOOL, "" + this.showInNews);
        // show object specific attributes
        // (index of oid: 1)
        this.showFormProperty (table, StoreArguments.ARG_CATEGORYOID,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_CATEGORY, env),
            Datatypes.DT_SELECT, "" + selectedOid,
            categorySelList.ids, categorySelList.values, 1);
        this.showFormProperty (table
                            , StoreArguments.ARG_VALUES
                            , MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                                StoreTokens.ML_VALUES, env)
                            , Datatypes.DT_DESCRIPTION
                            , this.values);
//debug (" --- ProductProperties_01.showFormProperties ENDE --- ");
    } // showFormProperties


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
//            Buttons.BTN_CUT,
//            Buttons.BTN_COPY,
//            Buttons.BTN_DISTRIBUTE,
//            Buttons.BTN_CLEAN,
            Buttons.BTN_SEARCH,
//            Buttons.BTN_HELP,
//            Buttons.BTN_LOGIN,
//            Buttons.BTN_SHOPPINGCART,
        }; // buttons

        // return button array
        return buttons;
    } // setInfoButtons


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
        // categoryOid
        BOHelpers.addInParameter (sp, this.categoryOid);
        // CONST_DELIMITER
        sp.addInParameter (ParameterConstants.TYPE_STRING, ProductProperties_01.CONST_DELIMITER);
        // values1
        sp.addInParameter (ParameterConstants.TYPE_STRING, (this.values1 != null) ?
            this.values1 : "");
        // values2
        sp.addInParameter (ParameterConstants.TYPE_STRING, (this.values2 != null) ?
            this.values2 : "");
        // values3
        sp.addInParameter (ParameterConstants.TYPE_STRING, (this.values3 != null) ?
            this.values3 : "");
        // values4
        sp.addInParameter (ParameterConstants.TYPE_STRING, (this.values4 != null) ?
            this.values4 : "");
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
        // categoryOid
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // categoryName
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // CONST_DELIMITER
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // values1
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // values2
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // values3
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // values4
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
        this.categoryOid = SQLHelpers.getSpOidParam (params[++i]);
        this.categoryName = params[++i].getValueString ();
        ++i;                            // currently not used
        this.values1 = params[++i].getValueString ();
        this.values2 = params[++i].getValueString ();
        this.values3 = params[++i].getValueString ();
        this.values4 = params[++i].getValueString ();
    } // getSpecificRetrieveParameters


    /**************************************************************************
     * Retrieve the type specific data that is not got from the stored
     * procedure. <BR/>
     * This method must be overwritten by all subclasses that have to get type
     * specific data that cannot be got from the retrieve data stored procedure.
     *
     * @param   action  SQLAction for Databaseoperation
     *
     * @exception    DBError
     *               This exception is always thrown, if there happens an error
     *               during accessing data.
     */
    protected void performRetrieveSpecificData (SQLAction action) throws DBError
    {
        // get the values out of the database and put it all in values
        // at this point all four strings (values1-4) should be examined
        // currently only values1 (max.255 characters) implemented
        this.values = "";
        if (this.values1 != null)
        {
            if (this.values1.length () > 0)
            {
                this.values = StringHelpers.changeDelimiter (this.values1,
                    ProductProperties_01.CONST_DELIMITER, "\n");
            } // if
        } // if
    } // performRetrieveSpecificData

} // class ProductProperties_01
