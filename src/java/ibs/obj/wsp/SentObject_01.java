/*******************************
 * Class: SentObject_01.java
 */

// package:
package ibs.obj.wsp;

// imports:
import ibs.bo.BOArguments;
import ibs.bo.BOHelpers;
import ibs.bo.BOTokens;
import ibs.bo.BusinessObject;
import ibs.bo.Buttons;
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
import ibs.util.DateTimeHelpers;


/******************************************************************************
 * This class represents one object of type Dokument with version 01. <BR/>
 *
 * @version     $Id: SentObject_01.java,v 1.15 2013/01/16 16:14:12 btatzmann Exp $
 *
 * @author      Heinz Josef Stampfer (HJ), 980526
 ******************************************************************************
 */
public class SentObject_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SentObject_01.java,v 1.15 2013/01/16 16:14:12 btatzmann Exp $";


    /**
     * Oid of the BO we have sent. <BR/>
     */
    public OID distributeId = null;

    /**
     * Oid of the recipientlist we have sent. <BR/>
     */
    public OID recipientContainerId = null;
    /**
     * The Type of the distributed Object. <BR/>
     */
    public int distributeType = 0x00000000;

    /**
     * The Type of the distributed BusinesObject. <BR/>
     */
    public String distributeTypeName = null;

    /**
     * The Name of the distributed BusinesObject. <BR/>
     */
    public String distributeName = "";

    /**
     * The Name of the icon of a type of BusinesObject. <BR/>
     */
    public String distributeIcon = null;

    /**
     * The Name of the sended BusinesObject. <BR/>
     */
    public String activities = null;

    /**
     * the Flag is set when the distributor want to delete a sentObject. <BR/>
     */
    public boolean deleted = false;

    /**
     * Type of the partOf  - Object Empfängerliste. <BR/>
     */
    public int partOfType = 0x01011b01;

    /**
     * the Flag is set when the distributor want set the senderRights. <BR/>
     */
    public boolean freeze = true;
    /**
     * The Type of the distributed Object. <BR/>
     */
    public int senderRights = Operations.OP_READ | Operations.OP_VIEW |
        Operations.OP_CREATELINK | Operations.OP_VIEWELEMS;


    /**************************************************************************
     * This constructor creates a new instance of the class SentObject_01.
     * <BR/>
     */
    public SentObject_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // SentObject_01


    /**************************************************************************
     * Creates a SentObject_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public SentObject_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:
    } // SentObject_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set the instance's attributes:
        this.procCreate =     "p_SentObject_01$create";
        this.procChange =     "p_SentObject_01$change";
        this.procRetrieve =   "p_SentObject_01$retrieve";
        this.procDelete =     "p_SentObject_01$delete";

        // set number of parameters for procedure calls:
        this.specificCreateParameters = 5;
        this.specificRetrieveParameters = 8;
        this.specificChangeParameters = 3;
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
        // deleted
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN, this.deleted);
        // distributeId
        BOHelpers.addInParameter (sp, this.distributeId);
        // operation distribute
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, Operations.OP_DISTRIBUTE);
        // senderRights
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.senderRights);
        // freeze
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN, this.freeze);
    } // setSpecificCreateParameters


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
        //distributeId
        BOHelpers.addInParameter (sp, this.distributeId);
        //activities
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.activities);
        //deleted
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN, this.deleted);
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
        // distributeId
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // distributeType
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
        // distributeTypeName
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // distributeName
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // icon
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // activities
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // deleted
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);
        // recipientContainerId
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
        this.distributeId = SQLHelpers.getSpOidParam (params[++i]);
        this.distributeType =       params[++i].getValueInteger ();
        this.distributeTypeName =   params[++i].getValueString ();
        this.distributeName =       params[++i].getValueString ();
        this.distributeIcon =       params[++i].getValueString ();
        this.activities =           params[++i].getValueString ();
        this.deleted =              params[++i].getValueBoolean ();
        this.recipientContainerId = SQLHelpers.getSpOidParam (params[++i]);
    } // getSpecificRetrieveParameters


    /**************************************************************************
     * Represent the properties of a SentObject_01 object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showProperties (TableElement table)
    {
        // display the base object's properties:
        super.showProperties (table);
        // distributeId
        // distributeType
        // loop through all properties of this object and display them:
        if (this.distributeId != null && !this.distributeId.isEmpty ())
        {
            this.showProperty (table, BOArguments.ARG_DISTRIBUTENAME,
                          MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_DISTRIBUTENAME, env), Datatypes.DT_LINK,
                          this.distributeName, this.distributeId);
        } // if
        // sentDate
        this.showProperty (table, BOArguments.ARG_SENTDATE,
                      MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SENTDATE, env), Datatypes.DT_DATETIME,
                      DateTimeHelpers.dateTimeToString (this.creationDate));
        this.showProperty (table, BOArguments.ARG_ACTIVITIES,
                      MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_ACTIVITIES, env), Datatypes.DT_NAME,
                      this.activities);

//        showProperty (table, BOArguments.ARG_OBJECT,
//                      TOK_DISTRIBUTENAME, Datatypes.DT_OBJECTLINK,
//                      distributeName, distributeId.toString ());

        // loop through all properties of this object and display them:
//        showProperty (table, BOArguments.ARG_VALIDUNTIL,
//                      TOK_VALIDUNTIL, Datatypes.DT_DATE,
//                      Helpers.dateToString (validUntil));
    } // showProperties


    /**************************************************************************
     * Represent the properties of a SentObject_01 object to the user
     * within a form. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showFormProperties (TableElement table)
    {
        // display the base object's properties:
        this.showProperty (table, BOArguments.ARG_NAME, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NAME, env),
            Datatypes.DT_NAME, this.name);
        this.showProperty (table, BOArguments.ARG_TYPE, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_TYPE, env),
            Datatypes.DT_TYPE, this.getMlTypeName ());
        this.showProperty (table, BOArguments.ARG_DESCRIPTION,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_DESCRIPTION, env), Datatypes.DT_DESCRIPTION, this.description);
        this.showProperty (table, BOArguments.ARG_VALIDUNTIL,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_VALIDUNTIL, env), Datatypes.DT_DATE, this.validUntil);
        if (this.isLink)
        {
            this.showProperty (table, BOArguments.ARG_LINKEDOBJECTID,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_LINKEDOBJECT, env), Datatypes.DT_LINK, this.linkedObjectId);
        } // if
        this.showProperty (table, BOArguments.ARG_OWNER, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_OWNER, env),
            Datatypes.DT_USER, this.owner);
        this.showProperty (
            table, BOArguments.ARG_CREATED, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_CREATED, env),
            Datatypes.DT_TEXT, this.creator.toString () + ", " +
                DateTimeHelpers.dateTimeToString (this.creationDate));
        this.showProperty (
            table, BOArguments.ARG_CHANGED, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_CHANGED, env),
            Datatypes.DT_TEXT, this.changer.toString () + ", " +
                DateTimeHelpers.dateTimeToString (this.lastChanged));

        // distributeId
        // distributeType
        // loop through all properties of this object and display them:
        // distributeType
        this.showProperty (table, BOArguments.ARG_DISTRIBUTENAME,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_DISTRIBUTENAME, env), Datatypes.DT_NAME, this.distributeName);
        // sentDate
        this.showProperty (table, BOArguments.ARG_SENTDATE, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SENTDATE, env),
            Datatypes.DT_DATE, DateTimeHelpers.dateToString (this.creationDate));
        this.showProperty (table, BOArguments.ARG_ACTIVITIES,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_ACTIVITIES, env), Datatypes.DT_NAME, this.activities);

/*
        // display the base object's properties:
        super.showFormProperties (table);
        // loop through all properties of this object and display them:

        // distributeName
        this.showProperty (table, BOArguments.ARG_DISTRIBUTENAME, TOK_DISTRIBUTENAME, Datatypes.DT_NAME, distributeName);
        // sentDate
        this.showProperty (table, BOArguments.ARG_SENTDATE, TOK_SENTDATE, Datatypes.DT_DATE, Helpers.dateToString (creationDate));
        this.showProperty (table, BOArguments.ARG_ACTIVITIES, TOK_ACTIVITIES, Datatypes.DT_NAME, activities);
*/
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
            Buttons.BTN_DELETE,
           //Buttons.BTN_CUT,
           //Buttons.BTN_COPY,
           //Buttons.BTN_DISTRIBUTE,
           //Buttons.BTN_CLEAN,
           //Buttons.BTN_SEARCH,
           //Buttons.BTN_HELP,
        }; // buttons

        // return button array
        return buttons;
    } // showInfoButtons

} // class SentObject_01
