/*
 * Class: Termin_01.java
 */

// package:
package m2.diary;

// imports:
import ibs.bo.BOArguments;
import ibs.bo.BOConstants;
import ibs.bo.BOHelpers;
import ibs.bo.BusinessObject;
import ibs.bo.Buttons;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.bo.ObjectNotFoundException;
import ibs.bo.Operations;
import ibs.bo.States;
import ibs.di.DataElement;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.BuildException;
import ibs.tech.html.FrameElement;
import ibs.tech.html.FrameSetElement;
import ibs.tech.html.Page;
import ibs.tech.html.TableElement;
import ibs.tech.http.HttpArguments;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;
import ibs.tech.sql.StoredProcedureConstants;
import ibs.util.DateTimeHelpers;
import ibs.util.FormFieldRelation;
import ibs.util.FormFieldRestriction;
import ibs.util.NoAccessException;
import ibs.util.UtilConstants;

import java.util.Date;


/******************************************************************************
 * This class represents one object of type Termin with version 01. <BR/>
 *
 * @version     $Id: Termin_01.java,v 1.27 2013/01/16 16:14:11 btatzmann Exp $
 *
 * @author      Horst Pichler   (HP), 980403
 ******************************************************************************
 */
public class Termin_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Termin_01.java,v 1.27 2013/01/16 16:14:11 btatzmann Exp $";


    // define date formats for locale

    /**
     * Begin of term: date & time. <BR/>
     */
    protected Date startDateTime;

    /**
     * End of term: date & time. <BR/>
     */
    protected Date endDateTime;

    /**
     * Place where date happens. <BR/>
     */
    protected String place;

    /**
     * Duration of term is whole day. <BR/>
     */
    protected boolean wholeDay;

    /**
     * Are there any participants to this term allowed?. <BR/>
     */
    protected boolean participants;

    /**
     * Is the list of participants viewable for everyone?. <BR/>
     */
    protected boolean showParticipants;

    /**
     * Number of participants allowed for this term.
     * -1 .. infinity.
     */
    protected int maxNumParticipants;

    /**
     * Number of participants announced to this term.
     */
    protected int curNumParticipants;

    /**
     * Number of participants announced to this term.
     */
    protected Date deadline = null;

    /**
     * OID of the participants container.
     */
    protected OID partContId;

    /**
     * Name of stored procedure which checks term overlappings. <BR/>
     */
    private static final String PROC_CHECKOVERLAP = "p_Termin_01$checkOverlap";
    /**
     * Number of participants announced to this term.
     */
    protected String procCheckOverlap = Termin_01.PROC_CHECKOVERLAP;

    /**
     * Is this term an overlapping term?.
     * This attribute is used to check if term is overlapping term.
     * Dependent on this value, overlapping terms are viewed/not viewed and
     * user is able to change data of current term.
     */
    protected boolean overlaps = false;

    /**
     * If this term is an overlapping term this attribute is
     * used to check if the time of the term was changed.
     */
    protected boolean timeChanged = false;


    /**
     * Are there any attachments to this term
     */
    protected boolean attachments = false;

    /**
     * Helpvariable to see if there was an entry in method
     * overlapOccurred before execution of method
     * {@link #performRetrieveData (int) performRetrieveData}.
     */
    protected boolean afterOverlapOcc = false;

    /**
     * Field name: start date + time. <BR/>
     */
    private static final String FIELD_STARTDATETIME = "startDateTime";
    /**
     * Field name: end date + time. <BR/>
     */
    private static final String FIELD_ENDDATETIME = "endDateTime";
    /**
     * Field name: location of term. <BR/>
     */
    private static final String FIELD_LOCATION = "place";
    /**
     * Field name: term is whole day? <BR/>
     */
    private static final String FIELD_ISWHOLEDAY = "wholeDay";
    /**
     * Field name: participants allowed? <BR/>
     */
    private static final String FIELD_HASPARTICIPANTS = "participants";
    /**
     * Field name: show participants? <BR/>
     */
    private static final String FIELD_ISSHOWPARTICIPANTS = "showParticipants";
    /**
     * Field name: maximum number of participants. <BR/>
     */
    private static final String FIELD_MAXNUMPARTICPANTS = "maxNumParticipants";
    /**
     * Field name: deadline for announcement of participants. <BR/>
     */
    private static final String FIELD_DEADLINE = "deadline";

    /**
     * Post fix for argument name: date value. <BR/>
     */
    private static final String ARGPF_DATE = "_d";
    /**
     * Post fix for argument name: time value. <BR/>
     */
    private static final String ARGPF_TIME = "_t";


    /**************************************************************************
     * This constructor creates a new instance of the class Termin_01.
     * <BR/>
     */
    public Termin_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // Termin_01


    /**************************************************************************
     * Creates a Termin_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public Termin_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // Termin_01



    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set stored procedure names:
        this.procCreate = "p_Termin_01$create";
        this.procRetrieve = "p_Termin_01$retrieve";
        this.procDelete = "p_Termin_01$delete";
        this.procChange = "p_Termin_01$change";
        this.procCheckOverlap = Termin_01.PROC_CHECKOVERLAP;

        // set number of parameters for procedure calls:
        this.specificChangeParameters = 8;
        this.specificRetrieveParameters = 10;

        // set term specific attributes:
        // set date initially to current date
        this.startDateTime = DateTimeHelpers.getCurAbsDate ();
        this.endDateTime = DateTimeHelpers.getCurAbsDate ();
        this.place = "";
        this.wholeDay = false;
        this.showParticipants = false;
        this.maxNumParticipants = 0;
        this.curNumParticipants = 0;
        this.partContId = null;
        this.deadline = null;

        // set extended search flag:
        this.searchExtended = true;

        // set db table name:
        this.tableName = "m2_Termin_01";
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
        // startDate
        sp.addInParameter (ParameterConstants.TYPE_DATE,
                        this.startDateTime);
        // endDate
        sp.addInParameter (ParameterConstants.TYPE_DATE,
                        this.endDateTime);
        // place
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        this.place);
        // participants
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN,
                        this.participants);
        // maxNumParticipants
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
                        this.maxNumParticipants);
        // showParticipants
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN,
                        this.showParticipants);
        // deadline
        sp.addInParameter (ParameterConstants.TYPE_DATE,
                        this.deadline);
        // attachments
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN,
                        this.attachments);
    } // setSpecificChangeParameters


    /**************************************************************************
     * Sets the object actual. <BR/>
     * This method sets the object actual. <BR/>
     */
    protected void setActual ()
    {
        // set the object actual:
        this.isActual = true;
    } // setActual


    /**************************************************************************
     * Get parameters. <BR/>
     */
    public void getParameters ()
    {
        // declare needed variables:
        OID oid = null;
        String str = null;
        int num = 0;
        Date date;

        super.getParameters ();

        // initialize time changed property to indicate that there is normally
        // no change to the time values:
        this.timeChanged = false;

        // set Termin_01 specific object attributes:
        // startDate:
        if ((date = this.env
            .getDateTimeParam (DiaryArguments.ARG_TERM_START_DATE)) != null)
        {
            if (DateTimeHelpers.compareDateTimes (date, this.startDateTime) != 0)
            {
                this.timeChanged = true;
            } // if

            this.startDateTime = date;
        } // if

        // endDate:
        if ((date = this.env.getDateTimeParam (DiaryArguments.ARG_TERM_END_DATE)) != null)
        {
// trace ("AJ 1 environment trace = " + ((AEnvironment) env).trace);
            if (DateTimeHelpers.compareDateTimes (date, this.endDateTime) != 0)
            {
                this.timeChanged = true;
            } // if
            this.endDateTime = date;
        } // if

        // place:
        if ((str = this.env.getStringParam (DiaryArguments.ARG_TERM_PLACE)) != null)
        {
            this.place = str;
        } // if

        // hidden attribute: overlaps - checks if term was already checked
        // for other overlapping terms
        if ((num = this.env.getBoolParam (DiaryArguments.ARG_TERM_OVERLAP)) >= IOConstants.BOOLPARAM_FALSE)
        {
            this.overlaps = num == IOConstants.BOOLPARAM_TRUE;
        } // if


        // participants:
        if ((num = this.env.getBoolParam (DiaryArguments.ARG_TERM_PARTICIPANTS)) >= IOConstants.BOOLPARAM_FALSE)
        {
            this.participants = num == IOConstants.BOOLPARAM_TRUE;
            this.displayTabs = true;
        } // if

        // showParticipants:
        if ((num = this.env
            .getBoolParam (DiaryArguments.ARG_TERM_SHOW_PARTICIPANTS)) >= IOConstants.BOOLPARAM_FALSE)
        {
            this.showParticipants = num == IOConstants.BOOLPARAM_TRUE;
        } // if

        // maxNumParticipants:
        if ((num = this.env
            .getIntParam (DiaryArguments.ARG_TERM_MAX_PARTICIPANTS)) != IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            this.maxNumParticipants = num;
        } // if

        // curNumParticipants:
        if ((num = this.env
            .getIntParam (DiaryArguments.ARG_TERM_CUR_PARTICIPANTS)) != IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            this.curNumParticipants = num;
        } // if

        // partContId:
        if ((oid = this.env.getOidParam (DiaryArguments.ARG_TERM_PART_CONT)) != null)
        {
            this.partContId = oid;
        } // if

        // deadline:
        if ((date = this.env.getDateParam (DiaryArguments.ARG_TERM_DEADLINE)) != null)
        {
            // create date/time at end of day:
            this.deadline = DateTimeHelpers.stringToDateTime (
                DateTimeHelpers.dateToString (date) + " 23:59:59");
        } // if

        // attachments:
        if ((num = this.env.getBoolParam (DiaryArguments.ARG_TERM_ATTACHMENTS)) >= IOConstants.BOOLPARAM_FALSE)
        {
            this.attachments = num == IOConstants.BOOLPARAM_TRUE;
            this.displayTabs = true;
        } // if
    } // getParameters


    /**************************************************************************
     * NOTICE: unusal method overriding to avoid caching - inserted to
     * solve problems with actual announced participant counting.
     *
     * Show the object, i.e. its properties. <BR/>
     * The properties are gotten from the database (with rights checked) and
     * represented to the user in the required form. <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    public void showInfo (int representationForm)
    {
        // ensure that the data are always retrieved from data store:
        this.isActual = false;

        // assure that info will be retrieved from db (not from cache)
        //
        // WHY: because there is a hack in performRetrieveData (return
        // without retrieving data when overlap occurred before)
        // which has some side effects when showing the Termin info after
        // dialogs create->overlap->rights->showInfo
        //
        // SIDE EFFECT: the name of Termin is suddenly 'Rechte' (only in
        // application) , because it gets the parameters (nomen) from the rights
        // dialog and it doesn't update data from db when overlap occurred
        // before.
        //
        // retrieval on showinfo does not affect the program-flow logic
        // of the overlap dialogs.
        this.afterOverlapOcc = false;

        // call common method of super object:
        super.showInfo (representationForm);
    } // showInfo



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
            Buttons.BTN_CUT,
            Buttons.BTN_COPY,
            Buttons.BTN_DISTRIBUTE,
            Buttons.BTN_SEARCH,
        }; // buttons

        // return button array
        return buttons;
    } // setInfoButtons


    /**************************************************************************
     * Is the object type allowed in workflows? <BR/>
     * This method shall be overwritten in subclasses.
     *
     * @return  <CODE>true</CODE> if the object type is allowed in workflows,
     *          <CODE>false</CODE> otherwise.
     */
    protected boolean isWfAllowed ()
    {
        return true;
    } // isWfAllowed


    /**************************************************************************
     * Represent the properties of a Termin_01 object to the user. <BR/>
     *
     * @param   table   Table where the properties shall be added.
     */
    protected void showProperties (TableElement table)
    {
        // loop through all properties of this object and display them:
        super.showProperties (table);

        // show object specific properties
        // start date of term:
        this.showProperty (table, DiaryArguments.ARG_TERM_START_DATE,
            MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                DiaryTokens.ML_TERM_START_DATE, env),
            Datatypes.DT_DATETIME, this.startDateTime);

        // end date of term:
        this.showProperty (table, DiaryArguments.ARG_TERM_END_DATE,
            MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                DiaryTokens.ML_TERM_END_DATE, env),
            Datatypes.DT_DATETIME, this.endDateTime);

        // happening-place of term:
        this.showProperty (table, DiaryArguments.ARG_TERM_PLACE,
            MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                DiaryTokens.ML_TERM_PLACE, env),
            Datatypes.DT_TEXT, this.place);

        // show participant dependent properties:
        if (this.participants)
        {
            // max. number of participants for this term
            // show this property only if there is a limit on number
            // of participants
            if (this.maxNumParticipants > 0)
            {
                this.showProperty (table, DiaryArguments.ARG_TERM_MAX_PARTICIPANTS,
                    MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                        DiaryTokens.ML_TERM_MAX_PARTICIPANTS, env),
                    Datatypes.DT_INTEGER, Integer.toString (this.maxNumParticipants));
            } // if
            else
            {
                this.showProperty (table, DiaryArguments.ARG_TERM_MAX_PARTICIPANTS,
                    MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                        DiaryTokens.ML_TERM_MAX_PARTICIPANTS, env),
                    Datatypes.DT_TEXT,
                    MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                        DiaryTokens.ML_NOLIMIT, env));
            } // else

            // current number of participants:
            this.showProperty (table, DiaryArguments.ARG_TERM_CUR_PARTICIPANTS,
                MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                    DiaryTokens.ML_TERM_CUR_PARTICIPANTS, env),
                Datatypes.DT_INTEGER, Integer.toString (this.curNumParticipants));

            // is list of participants viewable for term:
            this.showProperty (table, DiaryArguments.ARG_TERM_SHOW_PARTICIPANTS,
                MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                    DiaryTokens.ML_TERM_SHOW_PARTICIPANTS, env),
                Datatypes.DT_BOOL, "" + this.showParticipants);

            // deadline for announcements:
            if (this.deadline != null)
            {
                this.showProperty (table, DiaryArguments.ARG_TERM_DEADLINE,
                    MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                        DiaryTokens.ML_TERM_DEADLINE, env),
                    Datatypes.DT_DATE, this.deadline);
            } // if
            else
            {
                // if there is no deadline, deadline is automaticly set to
                // startDate in Database  (stored procedure: p_Termin_01$change)
                // but there is no retrieveData between changeData and
                // showProperties - so deadline must be set to startDate in
                // classfile too.
                this.deadline = this.startDateTime;
                this.showProperty (table, DiaryArguments.ARG_TERM_DEADLINE,
                    MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                        DiaryTokens.ML_TERM_DEADLINE, env),
                    Datatypes.DT_DATE, this.deadline);
            } // else
        } // if (participants
    } // showProperties


    /**************************************************************************
     * Represent the properties of a Termin_04 object to the user
     * within a form. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showFormProperties (TableElement table)
    {
        // loop through all properties of this object and display them:
        super.showFormProperties (table);

        // show object specific properties

        // property: start date
        // restriction: empty not allowed
        this.formFieldRestriction = new FormFieldRestriction (false);

        this.showFormProperty (table, DiaryArguments.ARG_TERM_START_DATE,
            MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                DiaryTokens.ML_TERM_START_DATE, env),
            Datatypes.DT_DATETIME, this.startDateTime);

        // property: end date
        // restriction: empty not allowed
        this.formFieldRestriction = new FormFieldRestriction (false);
        this.showFormProperty (table, DiaryArguments.ARG_TERM_END_DATE,
            MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                DiaryTokens.ML_TERM_END_DATE, env),
            Datatypes.DT_DATETIME, this.endDateTime);

        // property: place
        this.showFormProperty (table, DiaryArguments.ARG_TERM_PLACE,
            MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                DiaryTokens.ML_TERM_PLACE, env),
            Datatypes.DT_TEXT, this.place);

        // property: participants
        if (this.state == States.ST_CREATED) // it´s only possible to set this
                                        // state if the term was just created
        {
            this.showFormProperty (table, DiaryArguments.ARG_TERM_PARTICIPANTS,
                MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                    DiaryTokens.ML_TERM_PARTICIPANTS, env),
                Datatypes.DT_BOOL, "" + this.participants);
        } // if

        if (this.state == States.ST_CREATED || this.participants)
                                        // show properties for participants?
        {
            // property: max. number of participants
            // restriction: max. number of participants can not be set under
            // current number of participants

            // first make sure that maxNumParticipants isn´t negative
            if (this.maxNumParticipants < 0)
            {
                this.maxNumParticipants = 0;
            } // if

            // changed: value can be now empty
            // this has effects when participants flag is set
            // but reduces problems with handling
            this.formFieldRestriction =
                new FormFieldRestriction (true, 0, 0,
                    Integer.toString (this.curNumParticipants), null);

            // property: maximal number of participants
            this.showFormProperty (table, DiaryArguments.ARG_TERM_MAX_PARTICIPANTS,
                MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                    DiaryTokens.ML_TERM_MAX_PARTICIPANTS, env),
                Datatypes.DT_INTEGER, Integer.toString (this.maxNumParticipants));

            // property: list of participants viewable?
            this.showFormProperty (table, DiaryArguments.ARG_TERM_SHOW_PARTICIPANTS,
                MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                    DiaryTokens.ML_TERM_SHOW_PARTICIPANTS, env),
                Datatypes.DT_BOOL, "" + this.showParticipants);

            // deadline for announcements
            this.showFormProperty (table, DiaryArguments.ARG_TERM_DEADLINE,
                MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                    DiaryTokens.ML_TERM_DEADLINE, env),
                Datatypes.DT_DATE, this.deadline);

            // FormFieldRelations
            // 1st relation: startdate must be lower or equal end-date
            //FormFieldRelation rel1 =
                //new FormFieldRelation (Datatypes.DT_DATETIME,
                    //DiaryArguments.ARG_TERM_START_DATE,
                    //DiaryTokens.TOK_TERM_START_DATE,
                    //DiaryArguments.ARG_TERM_END_DATE,
                    //DiaryTokens.TOK_TERM_END_DATE,
                    //UtilConstants.FF_REL_LOWEREQUAL);

            // add 1st relation
            //addFormFieldRelation (rel1);
        } // if show properties for participants

        // FormFieldRelations
        // 1st relation: startdate must be lower or equal end-date
        FormFieldRelation rel1 =
            new FormFieldRelation (Datatypes.DT_DATETIME,
                DiaryArguments.ARG_TERM_START_DATE,
                MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                    DiaryTokens.ML_TERM_START_DATE, env),
                DiaryArguments.ARG_TERM_END_DATE,
                MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                    DiaryTokens.ML_TERM_END_DATE, env),
                UtilConstants.FF_REL_LOWEREQUAL);

        // add 1st relation
        this.addFormFieldRelation (rel1);

        // property: attachments
        if (this.state == States.ST_CREATED) // it´s only possible to set this
                                        // state if the term was just created
        {
            this.showFormProperty (table, DiaryArguments.ARG_TERM_ATTACHMENTS,
                MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                    DiaryTokens.ML_TERM_ATTACHMENTS, env),
                Datatypes.DT_BOOL, "" + this.attachments);
        } // if

        // hidden form property: partContId?
        this.showFormProperty (table, DiaryArguments.ARG_TERM_PART_CONT,
            null, Datatypes.DT_HIDDEN, this.partContId.toString ());

        // hidden form property: overlapping term?
        this.showFormProperty (table, DiaryArguments.ARG_TERM_OVERLAP,
            null, Datatypes.DT_HIDDEN, "" + this.overlaps);

/*
// TEST
// set form fields restrictions
restr = new FormFieldRestriction (false, 10, 10, null, "1.1.1998");
this.formFieldRestriction = restr;
showFormProperty (table, "TestDate1", "TestDate1",
    Datatypes.DT_DATE, new Date ());

// set form fields restrictions
restr = new FormFieldRestriction (false, 10, 10, null, "1.1.1998");
this.formFieldRestriction = restr;
showFormProperty (table, "TestDate2", "TestDate2",
    Datatypes.DT_DATE, new Date ());

            FormFieldRelation rel1 =
                new FormFieldRelation (DT_DATE, "TestDate1", "Testdatum Eins",
                    "TestDate2", "Testdatum Zwei", FF_REL_LOWEREQUAL);
            addFormFieldRelation (rel1);
*/

/*
// set form fields restrictions
restr = new FormFieldRestriction (false, 0, 0, null, "12:00");
this.formFieldRestriction = restr;
showFormProperty (table, "TestTime", "TestTime",
    Datatypes.DT_TIME, new Date ());

// set form fields restrictions
restr = new FormFieldRestriction (false, 0, 0, "1.1.1999 12:00", null);
this.formFieldRestriction = restr;
showFormProperty (table, "TestDateTime", "TestDateTime",
    Datatypes.DT_DATETIME, new Date ());
*/
    } // showFormProperties


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
        // startDate
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_DATE);
        // endDate
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_DATE);
        // place
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // participants
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);
        // maxNumParticipants
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
        // showParticipants
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);
        // curNumParticipants
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
        // partContId
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // deadline
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_DATE);
        // attachment
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);

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
        // start date:
        this.startDateTime = params[++i].getValueDate ();
        // end date:
        this.endDateTime = params[++i].getValueDate ();
        // place:
        this.place = params[++i].getValueString ();
        // participants:
        this.participants = params[++i].getValueBoolean ();
        // maximum number of participants:
        this.maxNumParticipants = params[++i].getValueInteger ();
        // display participants:
        this.showParticipants = params[++i].getValueBoolean ();
        // actual number of participants:
        this.curNumParticipants = params[++i].getValueInteger ();
        // oid of participant container:
        this.partContId = SQLHelpers.getSpOidParam (params[++i]);
        // deadline:
        this.deadline = params[++i].getValueDate ();
        // attachments:
        this.attachments = params[++i].getValueBoolean ();
    } // getSpecificRetrieveParameters


    /**************************************************************************
     * Get a Termin_01 object out of the database. <BR/>
     * First this method tries to load the object from the database. During this
     * operation a rights check is done, too. If this is all right the object is
     * returned, otherwise an exception is raised. <BR/>
     *
     * @param   operation   Operation to be performed with the object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     * @exception   ObjectNotFoundException
     *              The required object was not found during search, i.e. there
     *              does not exist any object with the required oid.
     */
    protected void performRetrieveData (int operation)
        throws NoAccessException, ObjectNotFoundException
    {
//debug (" ---- Termin_01.performRetrieveData ----");
        if (this.afterOverlapOcc)
        {
            // if method overlapOccurred () was executed before this function
            this.afterOverlapOcc = false;
            return;
        } // if

        super.performRetrieveData (operation);
    } // performRetrieveData


    /**************************************************************************
     * Checks if there are any overlapping terms for given term.
     *
     * @return  Are there any overlapping terms?
     */
    protected boolean checkOverlap ()
    {
        boolean retValue = false;       // return value of method

        // create stored procedure call:
        StoredProcedure sp = new StoredProcedure (this.procCheckOverlap,
            StoredProcedureConstants.RETURN_NOTHING);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // input parameters
        // oid
        BOHelpers.addInParameter (sp, this.oid);
        // user id
        if (this.user != null)
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.user.id);
        } // if
        else
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, 0);
        } // else
        // startDate
        sp.addInParameter (ParameterConstants.TYPE_DATE, this.startDateTime);
        // endDate
        sp.addInParameter (ParameterConstants.TYPE_DATE, this.endDateTime);

        // output parameter - number of found overlapping terms
        Parameter outParam = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);

        // perform the function call:
        BOHelpers.performCallProcedureData (sp, this.env);

        // check number of overlapping terms:
        retValue = outParam.getValueInteger () > 0;

        // exit method:
        return retValue;
    } // checkOverlap


    /**************************************************************************
     * Term overlap occurred while : show frameset with overlapping terms and
     * form with values of current term. <BR/>
     */
    private void overlapOccurred ()
    {
        // URL for list (upper) frame
        String upperFrameURL =
            this.getBaseUrlGet () +
            HttpArguments.createArg (BOArguments.ARG_FUNCTION,
                                     DiaryFunctions.FCT_TERM_OVERLAP_FRAME_FORM) +
            HttpArguments.createArg (BOArguments.ARG_OID, this.oid.toString ());

        // URL for form (lower) frame
        String lowerFrameURL =
            this.getBaseUrlGet () +
            HttpArguments.createArg (BOArguments.ARG_FUNCTION,
                                     DiaryFunctions.FCT_TERM_OVERLAP_FRAME_LIST) +
            HttpArguments.createArg (BOArguments.ARG_OID, this.oid.toString ()) +
            HttpArguments.createArg (BOArguments.ARG_CONTAINERID,
                                     this.containerId.toString ());

        String dateStr = "";            // string representing the date part
        String timeStr = "";            // string representing the time part


        // set afterOverlapOcc to true, to remember that this method was
        // executed:
        this.afterOverlapOcc = true;

        if (this.startDateTime != null) // there is a date?
        {
            // get date and time strings:
            dateStr = DateTimeHelpers.dateToString (this.startDateTime);
            timeStr = DateTimeHelpers.timeToString (this.startDateTime);

            lowerFrameURL +=
                HttpArguments.createArg (DiaryArguments.ARG_TERM_START_DATE +
                    Termin_01.ARGPF_DATE, dateStr) +
                HttpArguments.createArg (DiaryArguments.ARG_TERM_START_DATE +
                    Termin_01.ARGPF_TIME, timeStr);
        } // if there is a date


        if (this.endDateTime != null)   // there is a date?
        {
            // get date and time strings:
            dateStr = DateTimeHelpers.dateToString (this.endDateTime);
            timeStr = DateTimeHelpers.timeToString (this.endDateTime);

            lowerFrameURL +=
                HttpArguments.createArg (DiaryArguments.ARG_TERM_END_DATE +
                    Termin_01.ARGPF_DATE, dateStr) +
                HttpArguments.createArg (DiaryArguments.ARG_TERM_END_DATE +
                    Termin_01.ARGPF_TIME, timeStr);
        } // if there is a date


        // build page with frame set and frames:
        Page page = new Page (true);
        FrameElement upperFrame = new FrameElement (
            DiaryConstants.FRM_TERM_OVERLAP_FORM, upperFrameURL);
        upperFrame.frameborder = true;
        upperFrame.resize = true;
        FrameElement lowerFrame = new FrameElement (
            DiaryConstants.FRM_TERM_OVERLAP_LIST, lowerFrameURL);
        lowerFrame.frameborder = true;
        lowerFrame.resize = true;
        FrameSetElement frameSet = new FrameSetElement (true, 2);
        frameSet.frameborder = true;
        frameSet.frameSpacing = 5;
        frameSet.addElement (upperFrame, "75%");
        frameSet.addElement (lowerFrame, "*");

        // add frameset to page:
        page.body.addElement (frameSet);

        // build page:
        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
    } // overlapOccurred


    /**************************************************************************
     * Term overlap occurred : show form of current terms. <BR/>
     */
    public void overlapShowForm ()
    {
        // set overlap flag
        this.overlaps = true;

/* KR The following statement causes an error, because it handles an already
  existing object as new object. Thus the radio selections at the end of the
  edit dialog are shown.
        // set object state to 'created'
//        this.state = States.ST_CREATED;
*/

        // now show change form
        this.showChangeForm (UtilConstants.REP_STANDARD);
    } // overlapShowForm


    /**************************************************************************
     * Return the oid of term's participant container. <BR/>
     *
     * @return  OID of participant container of this term.
     */
    protected OID getParticipantContainer ()
    {
        if (this.participants && this.partContId == null)
                                        // participant container not yet known?
        {
            // retrieve data:
            try
            {
                this.performRetrieveData (Operations.OP_READ);
            } // try
            catch (NoAccessException e)
            {
                // TODO: handle the exception
            } // catch
            catch (ObjectNotFoundException e)
            {
                // TODO: handle the exception
            } // catch
        } // if participant container not yet known

        // exit
        return this.partContId;
    } // getParticipantContainer



    /**************************************************************************
     * Change the object, i.e. store its properties within the database. <BR/>
     * The properties are gotten from the environment. <BR/>
     *
     * @param   representationForm  Kind of representation.
     * @param   showChangeForm      Shall the change form be displayed after
     *                              the operation?
     *                              Default: false = showInfo
     *
     * @return  The id of the action which should be taken after the change
     *          operation.
     */
    public int change (int representationForm, boolean showChangeForm)
    {
        int retValue = BOConstants.BONEWMENU_NOTHING;
                                        // return value of this method

        // store the object's data within the database:
        // check overlapping terms:
        if (((this.state == States.ST_CREATED && !this.overlaps) ||
             this.timeChanged) &&
             this.checkOverlap ())
                                        // an overlap check had to be done and
                                        // an overlap occurred?
        {
            // yes there are overlapping terms
            this.overlapOccurred ();
        } // if an overlap check had to be done and an overlap occurred
        else                            // no overlap
        {
            this.overlaps = false;
            // change the object and get the return value:
            retValue = super.change (representationForm, showChangeForm);
        } // else no overlap

        return retValue;                // return the return value
    } // change


    //
    // import / export methods
    //
    /**************************************************************************
     * Read the object data from a DataElement. <BR/>
     *
     * @param   dataElement The dataElement to read the data from.
     *
     * @see ibs.bo.BusinessObject#readImportData
     */
    public void readImportData (DataElement dataElement)
    {
        // get business object specific values:
        super.readImportData (dataElement);

        // get the type specific values
        if (dataElement.exists (Termin_01.FIELD_STARTDATETIME))
        {
            this.startDateTime =
                dataElement.getImportDateTimeValue (Termin_01.FIELD_STARTDATETIME);
        } // if
        if (dataElement.exists (Termin_01.FIELD_ENDDATETIME))
        {
            this.endDateTime =
                dataElement.getImportDateTimeValue (Termin_01.FIELD_ENDDATETIME);
        } // if
        if (dataElement.exists (Termin_01.FIELD_LOCATION))
        {
            this.place = dataElement.getImportStringValue (Termin_01.FIELD_LOCATION);
        } // if
        if (dataElement.exists (Termin_01.FIELD_ISWHOLEDAY))
        {
            this.wholeDay = dataElement.getImportBooleanValue (Termin_01.FIELD_ISWHOLEDAY);
        } // if
        if (dataElement.exists (Termin_01.FIELD_HASPARTICIPANTS))
        {
            this.participants =
                dataElement.getImportBooleanValue (Termin_01.FIELD_HASPARTICIPANTS);
        } // if
        if (dataElement.exists (Termin_01.FIELD_ISSHOWPARTICIPANTS))
        {
            this.showParticipants =
                dataElement.getImportBooleanValue (Termin_01.FIELD_ISSHOWPARTICIPANTS);
        } // if
        if (dataElement.exists (Termin_01.FIELD_MAXNUMPARTICPANTS))
        {
            this.maxNumParticipants =
                dataElement.getImportIntValue (Termin_01.FIELD_MAXNUMPARTICPANTS);
        } // if
        if (dataElement.exists (Termin_01.FIELD_DEADLINE))
        {
            this.deadline = dataElement.getImportDateTimeValue (Termin_01.FIELD_DEADLINE);
        } // if
    } // readImportData


    /**************************************************************************
     * Write the object data to a DataElement. <BR/>
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
        dataElement.setExportValue (Termin_01.FIELD_STARTDATETIME, this.startDateTime);
        dataElement.setExportValue (Termin_01.FIELD_ENDDATETIME, this.endDateTime);
        dataElement.setExportValue (Termin_01.FIELD_LOCATION, this.place);
        dataElement.setExportValue (Termin_01.FIELD_ISWHOLEDAY, this.wholeDay);
        dataElement.setExportValue (Termin_01.FIELD_HASPARTICIPANTS, this.participants);
        dataElement.setExportValue (Termin_01.FIELD_ISSHOWPARTICIPANTS, this.showParticipants);
        dataElement.setExportValue (Termin_01.FIELD_MAXNUMPARTICPANTS, this.maxNumParticipants);
        dataElement.setExportValue (Termin_01.FIELD_DEADLINE, this.deadline);
    } // writeExportData

} // class Termin_01
