/*
 * Class: ProductGroupProfile_01.java
 */

// package:
package m2.store;

// imports:
import ibs.bo.BOArguments;
import ibs.bo.BOTokens;
import ibs.bo.BusinessObject;
import ibs.bo.Buttons;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.di.DataElement;
import ibs.io.IOConstants;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.TableElement;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.StoredProcedure;
import ibs.util.FormFieldRestriction;

import m2.store.StoreArguments;
import m2.store.StoreTokens;


/******************************************************************************
 * This class represents one BusinessObject of type ProductGroup with version 01.
 * <BR/>
 *
 * @version     $Id: ProductGroupProfile_01.java,v 1.13 2013/01/16 16:14:12 btatzmann Exp $
 *
 * @author      Bernhard Walter (BW), 981202
 ******************************************************************************
 */
public class ProductGroupProfile_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ProductGroupProfile_01.java,v 1.13 2013/01/16 16:14:12 btatzmann Exp $";


    // special ProductGroupProfile_01 attributes
    /**
     * Code of the product group, used either numerically or as string. <BR/>
     */
    private String code = null;
    /**
     * The season for which this product group is relevant. <BR/>
     */
    private String season = null;
    /**
     * The thumb nail used for this product group. <BR/>
     */
    private String thumbnail = null;
    /**
     * Flag if thumb nail is the same as image only smaller. <BR/>
     */
    private boolean thumbAsImage = false;
    /**
     * The image used for this product group. <BR/>
     */
    private String image = null;

    /**
     * Data element name: code. <BR/>
     */
    private static final String DATAELEM_CODE = "code";
    /**
     * Data element name: code. <BR/>
     */
    private static final String DATAELEM_SEASON = "season";


    /**************************************************************************
     * Creates a ProductGroupProfile_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     */
    public ProductGroupProfile_01 ()
    {
        // call constructor of super class:
        super ();
        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // ProductGroupProfile_01


    /******************************************************************************
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
    public ProductGroupProfile_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // ProductGroupProfile_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // initialize the instance's private properties:
        this.procCreate =     "p_ProductGrpProfile_01$create";
        this.procChange =     "p_ProductGrpProfile_01$change";
        this.procRetrieve =   "p_ProductGrpProfile_01$retrv";
        this.procDelete =     "p_ProductGrpProfile_01$delete";

        this.specificRetrieveParameters = 5;
        this.specificChangeParameters = 5;
    } // initClassSpecifics


    /**************************************************************************
     * Set the icon of the actual business object. <BR/>
     * If the icon is already set this method leaves it as is.
     * If there is no icon defined yet, the icon name is derived from the name
     * of the type of this object. <BR/>
     */
    protected void setIcon ()
    {
        this.icon = "ProductGroupProfile.gif";
    } // setIcon


    /**************************************************************************
     * Represent the properties of a ProductGroupProfile_01 object to the user.
     * <BR/>
     *
     * @param   table       Table where the properties shall be added.
     *
     * @see ibs.bo.BusinessObject#showProperty
     */
    protected void showProperties (TableElement table)
    {
        // common (bo) attributes
        super.showProperties (table);

        // object specific attributes
        this.showProperty (table, StoreArguments.ARG_PRG_CODE,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_PRG_CODE, env),
            Datatypes.DT_NAME, this.code);
        
        this.showProperty (table, StoreArguments.ARG_PRG_SEASON,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_PRG_SEASON, env),
            Datatypes.DT_NAME, this.season);

        this.showProperty (table, StoreArguments.ARG_PRG_IMG,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_IMAGE, env),
            Datatypes.DT_PICTURE, this.image, "");

        this.showProperty (table, StoreArguments.ARG_THUMBASIMAGE,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_THUMBASIMAGE, env),
            Datatypes.DT_BOOL, "" + this.thumbAsImage);

        this.showProperty (table, StoreArguments.ARG_PRG_THUMB,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_PRG_THUMB, env),
            Datatypes.DT_THUMBNAIL, this.thumbnail, "");
    } // showProperties


    /**************************************************************************
     * Represent the properties of a ProductGroupProfile_01 object to the user
     * within a form. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     *
     * @see ibs.bo.BusinessObject#showFormProperty
     */
    protected void showFormProperties (TableElement table)
    {
        // show common object (bo) attributes
        this.formFieldRestriction =
            new FormFieldRestriction (false);
        // loop through all properties of this object and display them:
        this.showFormProperty (table, BOArguments.ARG_NAME, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NAME, env),
            Datatypes.DT_NAME, this.name);

        this.showFormProperty (table, BOArguments.ARG_INNEWS, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_INNEWS, env),
            Datatypes.DT_BOOL, "" + this.showInNews);

        this.formFieldRestriction =
            new FormFieldRestriction (true);
        this.showFormProperty (table, BOArguments.ARG_DESCRIPTION,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_DESCRIPTION, env), Datatypes.DT_DESCRIPTION, this.description);

        // show object specific attributes
        this.showFormProperty (table, StoreArguments.ARG_PRG_CODE,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_PRG_CODE, env),
            Datatypes.DT_NAME, this.code);
        // season
        this.showFormProperty (table, StoreArguments.ARG_PRG_SEASON,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_PRG_SEASON, env),
            Datatypes.DT_NAME, this.season);
        // picture file
        this.showFormProperty (table, StoreArguments.ARG_PRG_IMG,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_IMAGE, env),
            Datatypes.DT_PICTURE, this.image);
        // thumb as image flag
        this.showFormProperty (table, StoreArguments.ARG_THUMBASIMAGE,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_THUMBASIMAGE, env),
            Datatypes.DT_BOOL, "" + this.thumbAsImage);

        // thumbnail file
        this.showFormProperty (table, StoreArguments.ARG_PRG_THUMB,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_THUMBNAIL, env),
            Datatypes.DT_THUMBNAIL, this.thumbnail);
    } // showFormProperties


    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     * The <A HREF="ibs.bo.BusinessObject.html#env">env</A> property is used
     * for getting the parameters. This property must be set before calling
     * this method. <BR/>
     */
    public void getParameters ()
    {
        // used variables
        String str;
        int num;

        // get parameters relevant for super class:
        super.getParameters ();

        // object specific parameters
        // code
        if ((str = this.env.getStringParam (StoreArguments.ARG_PRG_CODE)) != null)
        {
            this.code = str;
        } // if
        // season
        if ((str = this.env.getStringParam (StoreArguments.ARG_PRG_SEASON)) != null)
        {
            this.season = str;
        } // if

        if ((num = this.env.getBoolParam (StoreArguments.ARG_THUMBASIMAGE)) >=
            IOConstants.BOOLPARAM_FALSE)
        {
            this.thumbAsImage = num == IOConstants.BOOLPARAM_TRUE;
        } // if

        if ((str = this.getFileParamBO (StoreArguments.ARG_PRG_THUMB)) != null)
        {
            this.thumbnail = str;
        } // if
        else
        {
            this.thumbnail = null;
        } // else
        if ((str = this.getFileParamBO (StoreArguments.ARG_PRG_IMG)) != null)
        {
            this.image = str;
        } // if
        else
        {
            this.image = null;
        } // else
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
        // code
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.code);
        // season
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.season);
        // thumbnail
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.thumbnail);
        // thumbAsImage
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN, this.thumbAsImage);
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
        // code
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // season
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // thumbnail
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // thumbAsImage
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);
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
        this.code = params[++i].getValueString ();
        // season
        this.season = params[++i].getValueString ();
        // thumbnail
        this.thumbnail = params[++i].getValueString ();
        // thumbAsImage
        this.thumbAsImage = params[++i].getValueBoolean ();
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
            Buttons.BTN_SEARCH,
//            Buttons.BTN_HELP,
//            Buttons.BTN_LOGIN,
//            Buttons.BTN_SHOPPINGCART,
        }; // buttons
        // return button array
        return buttons;
    } // setInfoButtons


    //
    // import / export methods
    //
    /**************************************************************************
     * Reads the object data from an dataelement. <BR/>
     *
     * @param   dataElement The importElement to read the data from.
     */
    public void readImportData (DataElement dataElement)
    {
        // get business object specific values
        super.readImportData (dataElement);
        // get the type specific values
        if (dataElement.exists (ProductGroupProfile_01.DATAELEM_CODE))
        {
            this.code = dataElement
                .getImportStringValue (ProductGroupProfile_01.DATAELEM_CODE);
        } // if
        if (dataElement.exists (ProductGroupProfile_01.DATAELEM_SEASON))
        {
            this.season = dataElement
                .getImportStringValue (ProductGroupProfile_01.DATAELEM_SEASON);
        } // if
//        this.thumbnail = importElement.getImportStringValue ("thumbnail");
//        this.thumbAsImage = dataElement.getImportBooleanValue ("thumbAsImage");
//        this.image = importElement.getImportStringValue ("image");
    } // readImportData


    /**************************************************************************
     * writes the object data to an dataelement. <BR/>
     *
     * @param   dataElement The importElement to write the data to.
     */
    public void writeExportData (DataElement dataElement)
    {
        // set the business object specific values
        super.writeExportData (dataElement);
        // set the type specific values
        dataElement.setExportValue (ProductGroupProfile_01.DATAELEM_CODE,
            this.code);
        dataElement.setExportValue (ProductGroupProfile_01.DATAELEM_SEASON,
            this.season);
//        importElement.setExportValue ("thumbnail", this.thumbnail);
//        dataElement.setExportValue ("thumbAsImage", this.thumbAsImage);
//        importElement.setExportValue ("image", this.image);
    } // writeExportData

} // class ProductGroupProfile_01
