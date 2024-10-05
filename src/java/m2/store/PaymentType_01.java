/*
 * Class: PaymentType.java
 */

// package:
package m2.store;

// imports:
import ibs.bo.BOTokens;
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
 * This class represents one BusinessObject of type PaymentType. <BR/>
 *
 * @version     $Id: PaymentType_01.java,v 1.8 2013/01/16 16:14:12 btatzmann Exp $
 *
 * @author      Daniel Janesch (DJ), 001122
 ******************************************************************************
 */
public class PaymentType_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: PaymentType_01.java,v 1.8 2013/01/16 16:14:12 btatzmann Exp $";


    ///////////////////////////////////////////////////////////////////////////
    // properties
    ///////////////////////////////////////////////////////////////////////////

    /**
     * id of the payment type, e. g. creditcard or automatic debit transfer
     * system (Bankeinzug)
     */
    protected int paymentTypeIdentifierID;


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * This constructor creates a new instance of the class PaymentType. <BR/>
     */
    public PaymentType_01 ()
    {
        // call constructor of super class:
        super ();
    } // PaymentType_01


    /**************************************************************************
     * This constructor creates a new instance of the class PaymentType.
     * <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public PaymentType_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // PaymentType_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // initialize the instance's private properties:
        this.procCreate =     "p_PaymentType_01$create";
        this.procRetrieve =   "p_PaymentType_01$retrieve";
        this.procDelete =     "p_PaymentType_01$delete";
        this.procChange =     "p_PaymentType_01$change";

        // set the number of parameters for procedure calls:
        this.specificRetrieveParameters = 1;
        this.specificChangeParameters = 1;
    } // initClassSpecifics


    ///////////////////////////////////////////////////////////////////////////
    // database functions
    ///////////////////////////////////////////////////////////////////////////

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
        // paymentTypeIdentifierID
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);

        return i;                       // return the current index
    } // setSpecificRetrieveParameters


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
        // paymentTypeIdentifierID
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
            this.paymentTypeIdentifierID);
    } // setSpecificChangeParameters


    /**************************************************************************
     * Get the data for the additional (typespecific) parameters for
     * performRetrieveData. <BR/>
     * This method must be overwritten by all subclasses that have to get
     * typespecific data from the retrieve data stored procedure.
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
        this.paymentTypeIdentifierID     = params[++i].getValueInteger ();
    } // getSpecificRetrieveParameters


    ///////////////////////////////////////////////////////////////////////////
    // functions called from application level
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     * The <A HREF="#env">env</A> property is used for getting the parameters.
     * This property must be set before calling this method. <BR/>
     */
    public void getParameters ()
    {
        String str;                     // for all strings
        int id = -1;                    // get the parameter for the
                                        // paymentTypeIdentifierID

        // get parameters of super class:
        super.getParameters ();

        // get the name of the payment type
        if ((str = this.env
            .getStringParam (StoreArguments.ARG_PAYMENT_TYPE_NAME)) != null)
        {
            this.name = str;
        } // if

        // get the description of the payment type
        if ((str = this.env
            .getStringParam (StoreArguments.ARG_PAYMENT_TYPE_DESC)) != null)
        {
            this.description = str;
        } // if

        // get the id of the payment type
        if ((id = this.env.getIntParam (StoreArguments.ARG_PAYMENT_TYPE)) >= 0)
        {
            this.paymentTypeIdentifierID = id;
        } // if
    } // getParameters


    ///////////////////////////////////////////////////////////////////////////
    // viewing functions
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Represent the properties of a PaymentType_01 object to the user. <BR/>
     *
     * @param   table   Table where the properties shall be added.
     *
     * @see ibs.bo.BusinessObject#showProperty
     */
    protected void showProperties (TableElement table)
    {
        // display the base object's properties:
        super.showProperties (table);

        // show PaymentType_01 specific attributes
        this.showProperty (table, StoreArguments.ARG_PAYMENT_TYPE,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_PAYMENT_TYPE, env), Datatypes.DT_NAME,
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.TOK_PAYMENT_NAMES[this.paymentTypeIdentifierID], env));
    } // showProperties


    /***************************************************************************
     * Represent the properties of a PaymentType_01 object to the user within a
     * form. <BR/>
     *
     * @param table Table where the properties shall be added.
     *
     * @see ibs.bo.BusinessObject#showFormProperty
     */
    protected void showFormProperties (TableElement table)
    {
        // loop through all properties of this object and display them:
        super.showFormProperties (table);

        // show PaymentType_01 specific attributes
        // (index of preselected: 0)
        this.showFormProperty (table, StoreArguments.ARG_PAYMENT_TYPE,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_TYPE, env), 
            Datatypes.DT_SELECT, "" + this.oid,
            StoreTokens.TOK_PAYMENT_IDS, StoreTokens.TOK_PAYMENT_NAMES, 0);
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
        }; // buttons
        // return button array
        return buttons;
    } // setInfoButtons

} // class PaymentType_01
