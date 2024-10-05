/*
 * Class: Participant_01.java
 */

// package:
package m2.diary;

// imports:
import ibs.bo.BOArguments;
import ibs.bo.BOConstants;
import ibs.bo.BOHelpers;
import ibs.bo.BOTokens;
import ibs.bo.BusinessObject;
import ibs.bo.Buttons;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.bo.States;
import ibs.io.IOConstants;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.TableElement;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.StoredProcedure;
import ibs.util.DateTimeHelpers;

import m2.diary.DiaryArguments;
import m2.diary.DiaryTokens;
import m2.diary.ParticipantContainer_01;

import java.util.Date;


/******************************************************************************
 * This class represents one object of type Participant_01. <BR/>
 * A participant is announced to a specific term, all participants to a term
 * are held in a ParticipantContainer.
 *
 * @version     $Id: Participant_01.java,v 1.16 2013/01/16 16:14:11 btatzmann Exp $
 *
 * @author      Horst Pichler (HP), 980529
 ******************************************************************************
 */
public class Participant_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Participant_01.java,v 1.16 2013/01/16 16:14:11 btatzmann Exp $";


    /**
     * Announcers id. User who announced participant. <BR/>
     * It is possible to announce non-system-users.
     */
    protected int announcerId = 0;

    /**
     * Announcers full name. User who announced participant. <BR/>
     * It is possible to announce non-system-users.
     */
    protected String announcerName = "";


    /**************************************************************************
     * This constructor creates a new instance of the class Participant_01.
     * <BR/>
     */
    public Participant_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // Participant_01


    /**************************************************************************
     * Creates a Participant_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    @Deprecated
    public Participant_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // Participant_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    @Override
    public void initClassSpecifics ()
    {
        // set stored procedure names
        this.procCreate =    "p_Participant_01$create";
        this.procRetrieve =  "p_Participant_01$retrieve";
        this.procDelete =    "p_Participant_01$delete";
        this.procChange =    "p_Participant_01$change";

        // set number of parameters for procedure calls:
        this.specificRetrieveParameters = 2;
        this.specificChangeParameters = 2;

        // set participant specific attributes
        this.showExtendedCreationMenu = false;
        this.displayTabs = true;
    } // initClassSpecifics


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
        // announcerId
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.announcerId);

        // announcerName
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.announcerName);
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
        // announcerId
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
        // announcerName
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
    @Override
    protected void getSpecificRetrieveParameters (Parameter[] params,
                                                  int lastIndex)
    {
        int i = lastIndex;              // initialize params index

        // get the specific parameters:
        this.announcerId = params[++i].getValueInteger ();
        this.announcerName = params[++i].getValueString ();
    } // getSpecificRetrieveParameters


    /**************************************************************************
     * Get parameters. <BR/>
     */
    @Override
    public void getParameters ()
    {
        // declare needed variables
        OID oid = null;
        String str = null;
        int num = 0;
        Date date;

        this.displayTabs = true;

        // set basic object attributes
        // oid
        if ((oid = this.env.getOidParam (BOArguments.ARG_CONTAINERID)) != null)
        {
            this.containerId = oid;
        } // if
        // state
        if ((num = this.env.getIntParam (BOArguments.ARG_STATE)) !=
            IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            this.state = num;
        } // if
        // name
        if ((str = this.env.getStringParam (BOArguments.ARG_NAME)) != null)
        {
            this.name = str;
        } // if
        // owner
        if ((num = this.env.getIntParam (BOArguments.ARG_OWNER)) !=
            IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            this.owner = new User (num);
        } // if
        // type
        if ((num = this.env.getIntParam (BOArguments.ARG_TYPE)) !=
            IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            this.type = num;
        } // if
        // description
        if ((str = this.env.getStringParam (BOArguments.ARG_DESCRIPTION)) != null)
        {
            this.description = str;
        } // if
        if ((date = this.env.getDateParam (BOArguments.ARG_VALIDUNTIL)) != null)
        {
            this.validUntil = date;
        } // if
        //showInNews
        if ((num = this.env.getBoolParam (BOArguments.ARG_INNEWS)) >= IOConstants.BOOLPARAM_FALSE)
        {
            this.showInNews = num == IOConstants.BOOLPARAM_TRUE;
        } // if

        // set Participant_01 specific object attributes

        // hidden attribute: announcerId
        if ((str = this.env.getStringParam (DiaryArguments.ARG_TERM_ANN_ID)) != null)
        {
            this.announcerId = Integer.parseInt (str);
        } // if
        else
        {
            this.announcerId = 0;
        } // else

        // announcerName
        if ((str = this.env.getStringParam (DiaryArguments.ARG_TERM_ANN_NAME)) != null)
        {
            this.announcerName = str;
        } // if
    } // getParameters


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * object info view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  An array with button ids that can be displayed.
     */
    @Override
    protected int[] setInfoButtons ()
    {
        // create standard buttons
        int [] buttons =
        {
            Buttons.BTN_HELP,
        };


        // get current date
        Date today = DateTimeHelpers.getCurAbsDate ();

        // create container object: needed to get announcement-deadline
        ParticipantContainer_01 pc
            = new ParticipantContainer_01 ();

        pc.initObject (this.containerId, this.user, this.env, this.sess, this.app);

        // get deadline
        Date deadline = pc.getDeadline ();

        // check if unannouncements still possible
        if (DateTimeHelpers.compareDateTimes (today, deadline) <= 0)
        {
            // unannouncements (and changes) possible
            int [] buttons1 =
            {
                Buttons.BTN_EDIT,
                Buttons.BTN_DELETE,
                Buttons.BTN_HELP,
            };

            // return buttons1 array
            return buttons1;
        } // if

        // else return standard buttons array
        return buttons;
    } // showInfoButtons

/************/
/* HACK !!! */
/************/
// the only class, where the info buttons doensn't work
// correctly
// instead of info buttons, the content buttons are shown
// copied info buttons to content buttons
    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * object info view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  An array with button ids that can be displayed.
     */
    @Override
    protected int[] setContentButtons ()
    {
        // create standard buttons
        int [] buttons =
        {
            Buttons.BTN_HELP,
        };

        // get current date
        Date today = DateTimeHelpers.getCurAbsDate ();

        // create container object: needed to get announcement-deadline
        ParticipantContainer_01 pc = (ParticipantContainer_01)
            BOHelpers.getObject (this.containerId, this.env, false, false, false);

        // get deadline
        Date deadline = pc.getDeadline ();

        // check if unannouncements still possible
        if (DateTimeHelpers.compareDateTimes (today, deadline) <= 0)
        {
            // unannouncements (and changes) possible
            int [] buttons1 =
            {
                Buttons.BTN_EDIT,
                Buttons.BTN_DELETE,
                Buttons.BTN_HELP,
            };

            // return buttons1 array
            return buttons1;
        } // if

        // return button array
        return buttons;
    } // showContentButtons
/*****************/
/* HACK ENDE !!! */
/*****************/

    /**************************************************************************
     * Represent the properties of a Participant_01 object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    @Override
    protected void showProperties (TableElement table)
    {
        // loop through all properties of this object and display them:
        super.showProperties (table);

        // show object specific properties
        // announcers name
        this.showProperty (table, DiaryArguments.ARG_TERM_ANN_NAME, 
            MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                DiaryTokens.ML_TERM_ANN_NAME, env),
            Datatypes.DT_TEXT, this.announcerName);
    } // showProperties


    /**************************************************************************
     * Represent the properties of a Participant_01 object to the user
     * within a form. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    @Override
    protected void showFormProperties (TableElement table)
    {
        // loop through all properties of this object and display them:
        super.showFormProperties (table);

        // show object specific properties
        // announcers name
        this.showProperty (table, DiaryArguments.ARG_TERM_ANN_NAME, 
            MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                DiaryTokens.ML_TERM_ANN_NAME, env),
            Datatypes.DT_TEXT, this.announcerName);

        // show object specific properties
        // property: announcerName
        this.showFormProperty (table, DiaryArguments.ARG_TERM_ANN_NAME, null, Datatypes.DT_HIDDEN,
            this.announcerName);

        // hidden form property: announcerId
        this.showFormProperty (table, DiaryArguments.ARG_TERM_ANN_ID, null, Datatypes.DT_HIDDEN,
            new Integer (this.announcerId).toString ());

        if (this.state == States.ST_CREATED) // object was just created?
        {
            // hidden form property: go to container after o.k.
            this.showFormProperty (table, BOConstants.NEW_BUSINESS_OBJECT_MENU, null,
                Datatypes.DT_HIDDEN, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                    BOTokens.ML_SAVE_AND_BACK, env));
        } // if
    } // showFormProperties

} // Participant_01
