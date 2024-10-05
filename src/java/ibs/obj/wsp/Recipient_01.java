/*
 * Class: Recipient_01.java
 */

// package:
package ibs.obj.wsp;

// imports:
import ibs.bo.BOArguments;
import ibs.bo.BOHelpers;
import ibs.bo.BOTokens;
import ibs.bo.BusinessObject;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.bo.Operations;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.TableElement;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;

import java.util.Date;


/******************************************************************************
 * This class represents one object of type Dokument with version 01. <BR/>
 *
 * @version     $Id: Recipient_01.java,v 1.13 2013/01/16 16:14:12 btatzmann Exp $
 *
 * @author      Heinz Josef Stampfer (HJ), 980526
 ******************************************************************************
 */
public class Recipient_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Recipient_01.java,v 1.13 2013/01/16 16:14:12 btatzmann Exp $";


    /**
     * The Id of the recipient
     */
    public OID recipientId = null;
    /**
     * The Name of the recipient
     */
    public String recipientName = " Herman Maier ";
    /**
     * Position of the person in the company
     */
    public String recipientPosition = "";
    /**
     * Email of the recipient
     */
    public String recipientEmail = "maier@tectum.at";
    /**
     * Title of the recipient
     */
    public String recipientTitle = "";
    /**
     * Company of the recipient
     */
    public String recipientCompany = "";
    /**
     * The recipiant has read at this time the the message
     */
    public Date readDate = null;    //new Date();
    /**
     * The Id of a distribution of one special object
     */
    public OID sentObjectId = null;
    /**
     * Delete (just mark it as deleted) it and dont show it in the recipientContainer
     */
    public boolean deleted = false;
    /**
     * The Name of the
     */
    public String sentObjectName = " sentObjectname ";

    /**
     * The Type of the distributed Object. <BR/>
     */
    public int recipientRights = Operations.OP_READ | Operations.OP_VIEW |
        Operations.OP_CREATELINK | Operations.OP_VIEWELEMS;
    /**
     * The Type of the distributed Object. <BR/>
     */
    public boolean freeze = false;


    /**************************************************************************
     * This constructor creates a new instance of the class Recipient_01.
     * <BR/>
     */
    public Recipient_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();
    } // Recipient_01


    /**************************************************************************
     * Creates a Recipient_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public Recipient_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // Recipient_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set the instance's attributes:
        this.procCreate =     "p_Recipient_01$create";
        this.procChange =     "p_Recipient_01$change";
        this.procRetrieve =   "p_Recipient_01$retrieve";
        this.procDelete =     "p_Recipient_01$delete";

        // set number of parameters for procedure calls:
        this.specificCreateParameters = 4;
        this.specificRetrieveParameters = 6;
    } // initClassSpecifics


    /***************************************************************************
     * Set the data for the additional (typespecific) parameters for
     * performCreateData. <BR/>
     * This method must be overwritten by all subclasses that have to pass
     * typespecific data to the create data stored procedure.
     *
      * @param sp        The stored procedure to add the create parameters to.
     */
     @Override
     protected void setSpecificCreateParameters (StoredProcedure sp)
     {
        // set the specific parameters:
        // recipientId
        BOHelpers.addInParameter (sp, this.recipientId);
        // sentObjectId
        BOHelpers.addInParameter (sp, this.sentObjectId);
        // recipientRights
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.recipientRights);
        // freeze
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN, this.freeze);
    } // setSpecificCreateParameters


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
        // recipientId
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // recipientName
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
         //recipientPosition
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
         //recipientEmail
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
         //recipientTitle
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
         //recipientCompany
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);

        return i;                       // return the current index
    } // setSpecificRetrieveParameters


    /**************************************************************************
     * Get the data for the additional (type specific) parameters for
     * performRetrieveData. <BR/>
     * This method must be overwritten by all subclasses that have to get
     * type specific data from the retrieve data stored procedure.
     *
     * @param params        The array of parameters from the retrieve data stored
     *                   procedure.
     * @param lastIndex    The index to the last element used in params thus far.
     */
    protected void getSpecificRetrieveParameters (Parameter[] params,
                                                  int lastIndex)
    {
        int i = lastIndex;              // initialize params index

        // get the specific parameters:
        this.recipientId    =  SQLHelpers.getSpOidParam (params[++i]);
        this.recipientName  = params[++i].getValueString ();
        this.recipientPosition = params[++i].getValueString ();
        this.recipientEmail     = params[++i].getValueString ();
        this.recipientTitle     = params[++i].getValueString ();
        this.recipientCompany   = params[++i].getValueString ();

    } // getSpecificRetrieveParameters


    /**************************************************************************
     * Represent the properties of a Recipient_01 object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showProperties (TableElement table)
    {
        // display the base object's properties:
        //  super.showProperties (table);
/*
        if (readDate == null)
            this.showProperty (table, BOArguments.ARG_READDATE, TOK_READDATE, Datatypes.DT_DATE, "noch nicht gelesen");
        else
            this.showProperty (table, BOArguments.ARG_READDATE, TOK_READDATE, Datatypes.DT_DATE, Helpers.dateToString (readDate));
        this.showProperty (table, BOArguments.ARG_NAME, TOK_RECIPIENTENNAME, Datatypes.DT_NAME, recipientName);
        this.showProperty (table, BOArguments.ARG_NAME, TOK_DISTRIBUTENAME, Datatypes.DT_NAME, sentObjectName );
*/
        this.showProperty (table, BOArguments.ARG_NAME,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_RECIPIENTENNAME, env), Datatypes.DT_NAME, this.recipientName);
        this.showProperty (table, BOArguments.ARG_NAME,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_RECIPIENTPOSITION, env), Datatypes.DT_NAME,
            this.recipientPosition);
        this.showProperty (table, BOArguments.ARG_NAME, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_RECIPIENTEMAIL, env),
            Datatypes.DT_EMAIL, this.recipientEmail);
        this.showProperty (table, BOArguments.ARG_NAME, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_RECIPIENTTITLE, env),
            Datatypes.DT_NAME, this.recipientTitle);
        this.showProperty (table, BOArguments.ARG_NAME,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_RECIPIENTCOMPANY, env), Datatypes.DT_NAME, this.recipientCompany);
    } // showProperties


    /**************************************************************************
     * Represent the properties of a Recipient_01 object to the user
     * within a form. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showFormProperties (TableElement table)
    {
        // display the base object's properties:
        // super.showFormProperties (table);
/*
        if (readDate == null)
            this.showProperty (table, BOArguments.ARG_READDATE, TOK_READDATE, Datatypes.DT_DATE, "noch nicht gelesen");
        else
            this.showProperty (table, BOArguments.ARG_READDATE, TOK_READDATE, Datatypes.DT_DATE, Helpers.dateToString (readDate));
        this.showProperty (table, BOArguments.ARG_NAME, TOK_RECIPIENTENNAME, Datatypes.DT_NAME, recipientName);
        this.showProperty (table, BOArguments.ARG_NAME, TOK_DISTRIBUTENAME, Datatypes.DT_NAME, sentObjectName );
*/
        this.showProperty (table, BOArguments.ARG_NAME,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_RECIPIENTENNAME, env), Datatypes.DT_NAME, this.recipientName);
        this.showProperty (table, BOArguments.ARG_NAME,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_RECIPIENTPOSITION, env), Datatypes.DT_NAME,
            this.recipientPosition);
        this.showProperty (table, BOArguments.ARG_NAME, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_RECIPIENTEMAIL, env),
            Datatypes.DT_EMAIL, this.recipientEmail);
        this.showProperty (table, BOArguments.ARG_NAME, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_RECIPIENTTITLE, env),
            Datatypes.DT_NAME, this.recipientTitle);
        this.showProperty (table, BOArguments.ARG_NAME,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_RECIPIENTCOMPANY, env), Datatypes.DT_NAME, this.recipientCompany);
    } // showFormProperties


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in an
     * object's info view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  An array with button ids that can potentially be displayed.
     */
    protected int[] setInfoButtons ()
    {
        // define buttons to be displayed:
        int [] buttons =
        {
           //Buttons.BTN_EDIT,
           //Buttons.BTN_DELETE,
           //Buttons.BTN_CUT,
           //Buttons.BTN_COPY,
           //Buttons.BTN_DISTRIBUTE,
           //Buttons.BTN_CLEAN,
           //Buttons.BTN_SEARCH,
           //Buttons.BTN_HELP,
           //Buttons.BTN_LOGIN
        }; // buttons

        // return button array
        return buttons;
    } // showInfoButtons

} // class Recipient_01
