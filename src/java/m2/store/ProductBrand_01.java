/*
 * Class: ProductBrand_01.java
 */

// package:
package m2.store;

// imports:
import ibs.bo.BusinessObject;
import ibs.bo.Buttons;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.TableElement;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.StoredProcedure;

import m2.store.StoreArguments;
import m2.store.StoreTokens;


/******************************************************************************
 * This class represents one BusinessObject of type ProductBrand with version 01.
 * <BR/>
 *
 * @version     $Id: ProductBrand_01.java,v 1.11 2013/01/16 16:14:12 btatzmann Exp $
 *
 * @author      Bernhard Walter (BW), 981226
 ******************************************************************************
 */
public class ProductBrand_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ProductBrand_01.java,v 1.11 2013/01/16 16:14:12 btatzmann Exp $";


    // special ProductBrand_01 attributes
    /**
     * Image of the brand name. <BR/>
     */
    private String image = null;


    /**************************************************************************
     * Creates a ProductBrand_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     */
    public ProductBrand_01 ()
    {
        // call constructor of super class:
        super ();
    } // ProductBrand_01


    /**************************************************************************
     * This constructor creates a new instance of the class Store.
     * <BR/>
     * The compound object id is used as base for getting the
     * <A HREF="#server">server</A>, <A HREF="#type">type</A>, and
     * <A HREF="#id">id</A> of the business object. These values are stored in the
     * special public attributes of this type. <BR/>
     * The <A HREF="#user">user object</A> is also stored in a specific attribute
     * of this object to make sure that the user's context can be used for getting
     * his/her rights.
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public ProductBrand_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
    } // ProductBrand_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // initialize the instance's private properties:
        this.procCreate =     "p_ProductBrand_01$create";
        this.procChange =     "p_ProductBrand_01$change";
        this.procRetrieve =   "p_ProductBrand_01$retrieve";
        this.procDelete =     "p_ProductBrand_01$delete";
        this.icon = "ProductBrand.gif";

        // set number of parameters for procedure calls:
        this.specificChangeParameters = 1;
        this.specificRetrieveParameters = 1;
    } // initClassSpecifics


    /**************************************************************************
     * Represent the properties of a ProductBrand_01 object to the user. <BR/>
     *
     * @param   table   Table where the properties shall be added.
     *
     * @see ibs.bo.BusinessObject#showProperty
     */
    protected void showProperties (TableElement table)
    {
        // common (bo) attributes
        super.showProperties (table);

        // object specific attributes
        this.showProperty (table, StoreArguments.ARG_IMAGE,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_PRODUCTBRAND_IMAGE, env),
            Datatypes.DT_PICTURE, this.image, "");
    } // showProperties


    /**************************************************************************
     * Represent the properties of a ProductBrand_01 object to the user
     * within a form. <BR/>
     *
     * @param   table   Table where the properties shall be added.
     *
     * @see ibs.bo.BusinessObject#showFormProperty
     */
    protected void showFormProperties (TableElement table)
    {
        // show common object (bo) attributes
        super.showFormProperties (table);

        // picture file
        this.showFormProperty (table, StoreArguments.ARG_IMAGE,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_PRODUCTBRAND_IMAGE, env),
            Datatypes.DT_PICTURE, this.image);
    } // showFormProperties


    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     * The <A HREF="ibs.bo.BusinessObject.html#env">env</A> property is used
     * for getting the parameters. This property must be set before calling
     * this method. <BR/>
     */
    public void getParameters ()
    {
        // get parameters relevant for super class:
        super.getParameters ();

        // object specific parameters
        // ... getFileParamBO returns NULL if not exist -> image should be null on DB
        this.image = this.getFileParamBO (StoreArguments.ARG_IMAGE);
    } // getParameters


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
        // image
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.image);
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
        // image
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
        // image
        this.image = params[++i].getValueString ();
    } // getSpecificRetrieveParameters


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
//            Buttons.BTN_SEARCH,
//            Buttons.BTN_HELP,
//            Buttons.BTN_LOGIN,
//            Buttons.BTN_SHOPPINGCART,
        }; // buttons
        // return button array
        return buttons;
    } // setInfoButtons

} // class ProductBrand_01
