/*
 * Class: BlackBoard_01.java
 */

// package:
package m2.bbd;

// imports:
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.di.DataElement;
import ibs.io.Environment;
import ibs.io.IOConstants;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.TableElement;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.StoredProcedure;

import m2.bbd.BbdArguments;
import m2.bbd.BbdConstants;
import m2.bbd.BbdMessages;
import m2.bbd.BbdTokens;


/******************************************************************************
 * This class represents one object of type BlackBoard with version 01. <BR/>
 *
 * @version     $Id: BlackBoard_01.java,v 1.20 2013/01/16 16:14:10 btatzmann Exp $
 *
 * @author      Keim Christine (CK), 980521
 ******************************************************************************
 */
public class BlackBoard_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: BlackBoard_01.java,v 1.20 2013/01/16 16:14:10 btatzmann Exp $";


    /**
     * Type of the discussion. <BR/>
     */
    protected String discType;

    /**
     * Maximum height of the subtree. <BR/>
     * If it is one, then the discussion is a Black Board.
     */
    public int maxLevels;

    /**
     * 1 = normal, 0 = QuickView. <BR/>
     */
    public int defaultView;

    /**
     * The disc type string as it is used in the xml import file.
     */
    private static final String XML_DISCTYPE = "discType";


    /**************************************************************************
     * This constructor creates a new instance of the class BlackBoard_01.
     * <BR/>
     */
    public BlackBoard_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // init specifics of actual class:
    } // BlackBoard_01


    /**************************************************************************
     * Creates a BlackBoard_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public BlackBoard_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // init specifics of actual class:
    } // BlackBoard_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        this.maxLevels = -1;
        this.defaultView = -1;

        // initialize properties common to all subclasses:
        this.specificRetrieveParameters = 2;
        this.specificChangeParameters = 2;

        // set the class-procedureNames
        this.procCreate   = "p_BlackBoard_01$create";
        this.procChange   = "p_Discussion_01$change";
        this.procRetrieve = "p_Discussion_01$retrieve";
        this.procDelete   = "p_Discussion_01$delete";

        this.msgBundleContainerEmpty = BbdMessages.MSG_BUNDLE;
        this.msgContainerEmpty = BbdMessages.ML_MSG_BLACKBOARDEMPTY;
    } // initClassSpecifics


    /**************************************************************************
     * Initializes an object of this type. <BR/>
     * The <A HREF="m2.bbd.Discussion_01#discType">discType</A> is set to the
     * name of this object type.
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     * @param   env     The actual environment object.
     * @param   sess    The actual session info object.
     * @param   app     The global application info object.
     *
     * @see     ibs.bo.BusinessObject#initObject
     */
    public void initObject (OID oid, User user, Environment env,
                            SessionInfo sess, ApplicationInfo app)
    {
        // call method of super class:
        super.initObject (oid, user, env, sess, app);

        // set the instance's public/protected properties:
        this.discType = this.typeObj != null ? this.typeObj.getName () : null;
    } // initObject


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * containers content view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  an array with button ids that can potentially be displayed.
     */
    protected int[] setContentButtons ()
    {
        // define buttons to be displayed:
        int[] buttons =
        {
            Buttons.BTN_NEW,
            Buttons.BTN_LISTDELETE,
            Buttons.BTN_SEARCH,
        }; // buttons

        // return button array
        return buttons;
    } // setContentButtons


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * object info view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  an array with button ids that can be displayed.
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
            Buttons.BTN_SEARCH,
        }; // buttons

        // return button array
        return buttons;
    } // showInfoButtons


    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     * The <A HREF="#env">env</A> property is used for getting the parameters.
     * This property must be set before calling this method. <BR/>
     */
    public void getParameters ()
    {
        int num = 0;

        super.getParameters ();

        // get maxlevels:
        if ((num = this.env.getIntParam (BbdArguments.ARG_MAXLEVELS)) !=
            IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            this.maxLevels = num;
        } // if

        // get defaultView:
        if ((num = this.env.getIntParam (BbdArguments.ARG_DEFAULTVIEW)) !=
            IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            this.defaultView = num;
        } // if

        if (this.defaultView == BbdConstants.VAL_STANDARD)
        {
            // frameset shall not be shown
            this.showContentAsFrameset = false;
        } // if
        else
        {
            // frameset shall be shown
            this.showContentAsFrameset = true;
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
        // maxlevels
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.maxLevels);
        // defaultView
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.defaultView);
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
        // maxlevels
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
        // defaultView
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);

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
        this.maxLevels = params[++i].getValueInteger ();
        this.defaultView = params[++i].getValueInteger ();

        if (this.defaultView == BbdConstants.VAL_STANDARD)
        {
            // frameset shall not be shown
            this.showContentAsFrameset = false;
        } // if
        else
        {
            this.showContentAsFrameset = true;
        } // else
    } // getSpecificRetrieveParameters


    /**************************************************************************
     * Represent the properties of a DummyContainer_01 object to the user
     * within a form. <BR/>
     *
     * @param   table   Table where the properties shall be added.
     *
     * @see ibs.bo.BusinessObject#showFormProperties
     * @see ibs.bo.BusinessObject#showFormProperty
     */
    protected void showFormProperties (TableElement table)
    {
        // loop through all properties of this object and display them:
        super.showFormProperties (table);
        this.showProperty (table, BbdArguments.ARG_ART,  
            MultilingualTextProvider.getText (BbdTokens.TOK_BUNDLE,
                BbdTokens.ML_ART, env),
            Datatypes.DT_HIDDEN, "" + this.maxLevels);
        if (this.maxLevels == 1)
        {
            this.showProperty (table, BbdArguments.ARG_DEFAULTVIEW,  
                MultilingualTextProvider.getText (BbdTokens.TOK_BUNDLE,
                    BbdTokens.ML_ART, env),
                Datatypes.DT_HIDDEN, "" + this.defaultView);
        } // if
    } // showFormProperties


    //
    // IMPORT / EXPORT METHODS
    //
    /**************************************************************************
     * Reads the object data from an dataElement. <BR/>
     *
     * @param   dataElement The dataElement to read the data from.
     *
     * @see ibs.bo.BusinessObject#readImportData
     */
    public void readImportData (DataElement dataElement)
    {
        // get business object specific values
        super.readImportData (dataElement);
        // get the type specific values
        if (dataElement.exists (BlackBoard_01.XML_DISCTYPE))
        {
            this.discType = dataElement.getImportStringValue (BlackBoard_01.XML_DISCTYPE);
        } // if
    } // readImportData


    /**************************************************************************
     * writes the object data to an dataElement. <BR/>
     *
     * @param   dataElement The dataElement to write the data to.
     *
     * @see ibs.bo.BusinessObject#writeExportData
     */
    public void writeExportData (DataElement dataElement)
    {
        // set the business object specific values
        super.writeExportData (dataElement);
        // set the type specific values
        dataElement.setExportValue (BlackBoard_01.XML_DISCTYPE, this.discType);
    } // writeExportData

} // class BlackBoard_01
