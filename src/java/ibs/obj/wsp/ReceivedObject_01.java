/*******************************
 * Class: ReceivedObject_01.java
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
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.TableElement;
import ibs.tech.sql.DBError;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;
import ibs.util.DateTimeHelpers;
import ibs.util.NameAlreadyGivenException;
import ibs.util.NoAccessException;


/******************************************************************************
 * This class represents one object of type Dokument with version 01. <BR/>
 *
 * @version     $Id: ReceivedObject_01.java,v 1.19 2013/01/16 16:14:12 btatzmann Exp $
 *
 * @author      Heinz Josef Stampfer (HJ), 980526
 ******************************************************************************
 */
public class ReceivedObject_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ReceivedObject_01.java,v 1.19 2013/01/16 16:14:12 btatzmann Exp $";


    /**
     * Oid of the BO we have sent. <BR/>
     */
    public OID distributedId = null;

    /**
     * Oid of the recipientlist we have sent. <BR/>
     */
    public OID recipientContainerId = null;
    /**
     * The Type of the distributed Object. <BR/>
     */
    public int distributedType = 0x00000000;

    /**
     * The Type of the distributed BusinesObject. <BR/>
     */
    public String distributedTypeName = null;

    /**
     * The Name of the distributed BusinesObject. <BR/>
     */
    public String distributedName = "";

    /**
     * The Name of the icon of a type of BusinesObject. <BR/>
     */
    public String distributedIcon = null;

    /**
     * The Name of the sended BusinesObject. <BR/>
     */
    public String activities = "";

    /**
     * Oid of the BO created for SentobjectContainer. <BR/>
     */
    private OID p_sentObjectOid = null;

    /**
     * The full name of the sender. <BR/>
     */
    public String senderFullName = "";

    /**
     * The oid of the receiver. <BR/>
     */
    private OID p_receiverOid = null;


    /**************************************************************************
     * This constructor creates a new instance of the class ReceivedObject_01.
     * <BR/>
     */
    public ReceivedObject_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();
    } // ReceivedObject_01


    /**************************************************************************
     * Creates a ReceivedObject_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public ReceivedObject_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // ReceivedObject_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        this.procCreate =     "p_ReceivedObject_01$create";
        this.procChange =     "p_ReceivedObject_01$change";
        this.procRetrieve =   "p_ReceivedObject_01$retrieve";
        this.procDelete =     "p_ReceivedObject_01$delete";
        this.procDeleteRec =  "p_ReceivedObject_01$delete";

        // set number of parameters for procedure calls:
        this.specificRetrieveParameters = 8;
        this.specificChangeParameters = 7;
    } // initClassSpecifics


    /**************************************************************************
     * This method sets the receiverOid. <BR/>
     *
     * @param receiverOid The receiverOid to set.
     */
    public void setReceiverOid (OID receiverOid)
    {
        this.p_receiverOid = receiverOid;

        // check if the containerId is already set:
        if (this.containerId == null)
        {
            // compute the containerOid which must be the oid of the inbox of the
            // receiver:
            this.computeContainerOid ();
        } // if
    } // setReceiverOid


    /**************************************************************************
     * This method gets the receiverOid. <BR/>
     *
     * @return Returns the receiverOid.
     */
    public OID getReceiverOid ()
    {
        return this.p_receiverOid;
    } // getReceiverOid


    /**************************************************************************
     * This method sets the sentObjectId. <BR/>
     *
     * @param sentObjectId The sentObjectId to set.
     */
    public void setSentObjectOid (OID sentObjectId)
    {
        if (sentObjectId != null)
        {
            this.p_sentObjectOid = sentObjectId;
        } // if
        else
        {
            this.p_sentObjectOid = OID.getEmptyOid ();
        } // else
    } // setSentObjectId


    /**************************************************************************
     * This method gets the sentObjectId. <BR/>
     *
     * @return Returns the sentObjectId.
     */
    public OID getSentObjectOid ()
    {
        return this.p_sentObjectOid;
    } // getSentObjectId


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
        // distributedId
        BOHelpers.addInParameter (sp, this.distributedId);
        // distributedType
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
            this.distributedType);
        // distributedTypeName
        sp.addInParameter (ParameterConstants.TYPE_STRING,
            this.distributedTypeName);
        // distributedName
        sp.addInParameter (ParameterConstants.TYPE_STRING,
            this.distributedName);
        // distributedIcon
        sp.addInParameter (ParameterConstants.TYPE_STRING,
            this.distributedIcon);
        //activities
        sp.addInParameter (ParameterConstants.TYPE_STRING,
            this.activities);
        // sentObjectId
        BOHelpers.addInParameter (sp, this.p_sentObjectOid);
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
        // distributedId
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // distributedType
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
        // distributedTypenName
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // distributedName
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // distributedIcon
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        //activities
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // recipientContainerId
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // senderFullName
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);

        return i;                       // return the current index
    } // setSpecificRetrieveParameters


    /***************************************************************************
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
        this.distributedId =         SQLHelpers.getSpOidParam (params[++i]);
        this.distributedType =       params[++i].getValueInteger ();
        this.distributedTypeName =   params[++i].getValueString ();
        this.distributedName =       params[++i].getValueString ();
        this.distributedIcon =       params[++i].getValueString ();
        this.activities =            params[++i].getValueString ();
        this.recipientContainerId =  SQLHelpers.getSpOidParam (params[++i]);
        this.senderFullName =        params[++i].getValueString ();
    } // getSpecificRetrieveParameters


    /**************************************************************************
     * Represent the properties of a ReceivedObject_01 object to the user. <BR/>
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
        this.showProperty (table, BOArguments.ARG_SENDER,
                      MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SENDER, env), Datatypes.DT_NAME,
                      this.senderFullName);
        if (this.distributedId != null && !this.distributedId.isEmpty ())
        {
            this.showProperty (table, BOArguments.ARG_DISTRIBUTENAME,
                          MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_DISTRIBUTENAME, env), Datatypes.DT_LINK,
                          this.distributedName, this.distributedId,
                          this.distributedIcon, "objRef");
        } // if

        // sentDate
        this.showProperty (table, BOArguments.ARG_SENTDATE,
                      MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SENTDATE, env), Datatypes.DT_DATETIME,
                      DateTimeHelpers.dateTimeToString (this.creationDate));
        this.showProperty (table, BOArguments.ARG_ACTIVITIES,
                      MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_ACTIVITIES, env), Datatypes.DT_NAME,
                      this.activities);
        // loop through all properties of this object and display them:
//        showProperty (table, BOArguments.ARG_VALIDUNTIL,
//                      TOK_VALIDUNTIL, Datatypes.DT_DATE,
//                      Helpers.dateToString (this.validUntil));
    } // showProperties


    /**************************************************************************
     * Represent the properties of a ReceivedObject_01 object to the user
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
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_DISTRIBUTENAME, env), Datatypes.DT_NAME, this.distributedName);
        // sentDate
        this.showProperty (table, BOArguments.ARG_SENTDATE, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SENTDATE, env),
            Datatypes.DT_DATETIME, DateTimeHelpers.dateTimeToString (this.creationDate));
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


    /***************************************************************************
     * Compute the oid of the container where the object shall be placed in.
     * This container is the inbox of the receiver. <BR/>
     */
    private void computeContainerOid ()
    {
        int rowCount;
        SQLAction action = null;        // the action object used to access the DB

        // create the SQL String to select the project order
        StringBuffer queryStr = new StringBuffer ()
            .append ("SELECT wsp.inbox")
            .append (" FROM ibs_User u, ibs_Workspace wsp")
            .append (" WHERE u.oid = ").append (this.p_receiverOid.toStringQu ())
            .append (" AND u.id = wsp.userId");

        try
        {
            action = this.getDBConnection ();
            rowCount = action.execute (queryStr, false);
            // is the result exactly one row?
            if (rowCount > 0)
            {
                if (!action.getEOF ())
                {
                    this.containerId =
                        SQLHelpers.getQuOidValue (action, "inbox");

                    // go to next tupel (there should only be one)
//                    action.next ();
                } // if
            } // if (rowCount > 0)
            // end transaction
            action.end ();
        } // try
        catch (DBError e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
        finally
        {
            this.releaseDBConnection (action);
        } // finally
    } // checkSubObjects

    /**************************************************************************
     * Change the data of a business object in the database. <BR/>
     * <B>THIS METHOD IS A DUMMY WHICH MUST BE OVERWRITTEN IN SUB CLASSES!</B>
     * <BR/>
     * This method tries to store the object into the database.
     * During this operation a rights check is done, too.
     * If this is all right the object is stored and this method terminates
     * otherwise an exception is raised. <BR/>
     *
     * @param   operation   Operation to be performed with the object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     * @exception   NameAlreadyGivenException
     *              An object with this name already exists. This exception is
     *              only raised by some specific object types which don't allow
     *              more than one object with the same name.
     */
    public void performChangeData (int operation)
        throws NoAccessException, NameAlreadyGivenException
    {
    	super.performChangeData(operation);
    }

} // class ReceivedObject_01
