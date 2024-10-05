/*
 * Class: ParticipantContainer_01.java
 */

/*
 * Class: CheckParticipantsValues.java
 * You will find the source at the bottom of this file, my friend!
 * It is a small class, created to hold some return values of
 * the stored procedure 'checkParticipants'
 */

// package:
package m2.diary;

// imports:
import ibs.bo.BOConstants;
import ibs.bo.BOHelpers;
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.bo.ObjectNotFoundException;
import ibs.bo.Operations;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.BuildException;
import ibs.tech.html.Page;
import ibs.tech.html.ScriptElement;
import ibs.tech.sql.DBError;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;
import ibs.tech.sql.StoredProcedureConstants;
import ibs.util.DateTimeHelpers;
import ibs.util.NoAccessException;
import ibs.util.UtilConstants;
import ibs.util.UtilExceptions;

import java.util.Date;
import java.util.Vector;


/******************************************************************************
 * This class represents one object of type TerminplanContainer with version 01.
 * <BR/>
 *
 * @version     $Id: ParticipantContainer_01.java,v 1.21 2013/01/18 10:38:18 rburgermann Exp $
 *
 * @author      Horst Pichler   (HP), 980428
 ******************************************************************************
 */
public class ParticipantContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ParticipantContainer_01.java,v 1.21 2013/01/18 10:38:18 rburgermann Exp $";


    /**
     * Stored procedure: checkParticipants. <BR/>
     * Gets following information:
     * - is user anounced (participant)?
     * - number of free places
     */
    protected String procCheckParticipants = "p_ParticipantCont_01$chkPart";

    /**
     * Stored procedure: checkParticipantsViewable. <BR/>
     * Gets following information:
     * - is list of all participants viewable for all users?
     */
    protected String procCheckParticipantsViewable = "p_ParticipantCont_01$chkPVwabl";

    /**
     * Name of referring term (container object)
     */
    protected String termName;


    /**************************************************************************
     * This constructor creates a new instance of the class ParticipantContainer_01.
     * <BR/>
     */
    public ParticipantContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // ParticipantContainer_01


    /**************************************************************************
     * Creates a ParticipantContainer_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public ParticipantContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // ParticipantContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
/*
        // set header-display function of container
        fct = FCT_TERM_PARTICIPANTS_FRAME_LIST;
*/

        // reset name
        this.termName = "";

        // class name of row element
        this.elementClassName = "m2.diary.ParticipantContainerElement_01";
    } // initClassSpecifics


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * containers content view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * OVERWRITTEN: participant container has 3 additional buttons:
     * 'Anmelden', 'Abmelden', 'Andere Anmelden'. viewing of these buttons
     * is not mandatory. it depends on:
     * 1. rights the user has on container object (addElem, delElem)
     * 2. number of already announced participants and max-number
     * 3. [un]announcement date (can't be after deadlines)
     *
     * @return  An array with button ids that can potentially be displayed.
     */
    protected int[] setContentButtons ()
    {
        // init buttons
        int[] buttons =
        {
//            Buttons.BTN_NEW,
//            Buttons.BTN_PASTE,
//            Buttons.BTN_CLEAN,
//            Buttons.BTN_ANNOUNCE,
            Buttons.BTN_SEARCH,
//            Buttons.BTN_HELP,

        }; // buttons

        // first check if user is allowed to annonunce himself or others
        // depends on:
        // - rights user has on participant container
        // - number of already announced participants
        // - user already announced
        // - startdate and deadline of term
        // if everything ok: execute stored procedure 'checkParticipants'

        // Check, if it is allowed to add Participants, or if there is no
        // free place, or if the start time of the term is already reached

        // create return-value container-object for stored procedure
        CheckParticipantsValues cp = new  CheckParticipantsValues ();
        // execute stored procedure and store return values in cp
        cp = this.checkParticipants ();

        // check if announcements still possible
        // create date - today
        // get current date
        Date today = DateTimeHelpers.getCurAbsDate ();
        Vector<Integer> hButtons = new Vector<Integer> ();   // HelpVariable for Creating the Array Button - big size
        int[] hButtons2;    // HelpVariable for Creating the Array Button - right size

        // now that we have the needed information about this term, show
        // buttons accourding to this info

        // are there some free places left?
        // and user has right to announce participants
        // notice: if there is no announcementlimit, free = max int
        if (cp.free > 0)
        {
            // is user already announced to term?
            if (cp.announced)
            {
                // user already announced:
                // - user himself can only unannounce if deadline isn`t reached
                // - user can announce other persons if startdate isn´t reached

                // today´s day is before deadline: add buttons to unannounce!
                if (DateTimeHelpers.compareDateTimes (today, cp.deadline) <= 0)
                {
                    hButtons.addElement (new Integer (Buttons.BTN_UNANNOUNCE));
                    hButtons.addElement (new Integer (Buttons.BTN_LISTDELETE));
                } // if

                // startdate of term isn`t reached: add button to announce others
                if (DateTimeHelpers.compareDateTimes (today, cp.startDate) < 0)
                {
                    hButtons.addElement (new Integer (Buttons.BTN_ANNOUNCE_OTHER));
                } // if

                // add standard buttons

                hButtons.addElement (new Integer (Buttons.BTN_SEARCH));

            } // if
            else
            {
                // user himself not announced
                // - user himself can announce
                // - user can announce other persons

                // startdate of term isn`t reached:
                // add button to announce others and himself
                // ... + button for list-delete
                if (DateTimeHelpers.compareDateTimes (today, cp.startDate) < 0)
                {
                    hButtons.addElement (new Integer (Buttons.BTN_ANNOUNCE));
                    hButtons.addElement (new Integer (Buttons.BTN_ANNOUNCE_OTHER));
                    hButtons.addElement (new Integer (Buttons.BTN_LISTDELETE));
                } // if

                // add standard buttons
                hButtons.addElement (new Integer (Buttons.BTN_SEARCH));
            } // else
        } // if free
        // no free places and user allowed to unannounce participants?
        else
        {
            // no free places!

            // if today is before deadline: add list-delete button
            if (DateTimeHelpers.compareDateTimes (today, cp.deadline) <= 0)
            {
                hButtons.addElement (new Integer (Buttons.BTN_LISTDELETE));
            } // if

            // is user already announced to term?
            if (cp.announced)
            {
                // user already announced:
                // - user himself can only unannounce

                // today is before deadline: add button to unannounce himself
                if (DateTimeHelpers.compareDateTimes (today, cp.deadline) <= 0)
                {
                    hButtons.addElement (new Integer (Buttons.BTN_UNANNOUNCE));
                } // if

                // add standard buttons
                hButtons.addElement (new Integer (Buttons.BTN_SEARCH));
            } // if
        } // else (no free places)

        // make int Array with right length
        hButtons2 = new int[hButtons.size ()];
        for (int i = 0; i < hButtons.size (); i++)
        {
            hButtons2[i] = (hButtons.elementAt (i)).intValue ();
        } // for i

        buttons = hButtons2;

        // return button array
        return buttons;
    } // setContentButtons



    /**************************************************************************
     * Create the query to get the container's content out of the database.
     * <BR/>
     * The query or view must at least have the attributes uid and rights.
     * Queries on these attributes have to be addable to this query. <BR/>
     * The query must have at least one constraint within WHERE to ensure that
     * other constraints can be added with AND. <BR/>
     * <B>Standard format:</B>. <BR/>
     *      "SELECT DISTINCT oid, &lt;other attributes> " +
     *      " FROM " + this.viewContent +
     *      " WHERE containerId = " + this.oid;. <BR/>
     * This method can be overwritten in subclasses. <BR/>
     * As you can see the query should contain at least the oid.
     *
     * @return  The constructed query.
     */
    protected String createQueryRetrieveContentData ()
    {
        String query =
            "SELECT DISTINCT c.oid, c.state, c.name as name , c.typeCode, c.typeName, " +
            "        c.isLink, c.linkedObjectId, c.userId, c.owner, c.ownerName, " +
            "        c.ownerOid, c.ownerFullname, c.lastChanged, c.isNew, c.icon, " +
            "        c.description, c.creationDate AS announcementDate, " +
            "        v.announcerName " +
            " FROM " + this.viewContent + " c, m2_Participant_01 v " +
            " WHERE   c.containerId = " + this.oid.toStringQu () +
            " AND c.oid = v.oid ";

        return query;
    } // createQueryRetrieveContentData


    /**************************************************************************
     * Get the element type specific data out of the actual tuple of the query
     * result. <BR/>
     * This metod is used to get all attributes of one element out of the
     * resultset. The attribute names which can be used are the ones which
     * are defined within the resultset of
     * <A HREF="#createQueryRetrieveContentData">createQueryRetrieveContentData</A>.
     * <BR/>
     * <B>Format:</B>. <BR/>
     * for oid properties:
     *      obj&lt;property> = getQuOidValue (action, "&lt;attribute>");. <BR/>
     * for other properties:
     *      obj.&lt;property> = action.get&lt;type> ("&lt;attribute>");. <BR/>
     * The property <B>oid</B> is already gotten. This must not be done within this
     * method. <BR/>
     * This method can be overwritten in subclasses. <BR/>
     *
     * @param   action      The action for the database connection.
     * @param   commonObj   Object of common representing the list element.
     *
     * @throws  DBError
     *          Error when executing database statement.
     */
    protected void getContainerElementData (SQLAction action, ContainerElement commonObj)
        throws DBError
    {
        // convert common element object to actual type:
        ParticipantContainerElement_01 obj = (ParticipantContainerElement_01) commonObj;
        // get common attributes:
        super.getContainerElementData (action, obj);

        // get element type specific attributes:
        obj.fullName = action.getString ("name");
        obj.announcementDate = action.getDate ("announcementDate");
        obj.announcer = action.getString ("announcerName");
    } // getContainerElementData


    /**************************************************************************
     * Adds participant to terms participant container.
     */
    public void addParticipant ()
    {
        // Notice: for most operations VIEW-rights will be enough,
        // because if you are allowed to view the participants list
        // you should be allowed to announce to the referring term.

        // Check, if it is allowed to add Participants, or if there is no
        // free place, or if the start time of the term is already reached

        // create return-value container-object for stored procedure
        CheckParticipantsValues cp = new  CheckParticipantsValues ();
        // execute stored procedure and store return values in cp
        cp = this.checkParticipants ();
        // get current date
        Date today = DateTimeHelpers.getCurAbsDate ();

        // check if announcements still possible
        // no places left or term began already or user is already announced
        if (cp.free <= 0 || today.after (cp.startDate) || cp.announced)
                                        // announcements are not possible
        {
            // show message and containers content
            Page page;

            // create the answering page with the alert message
            // and the show Object
            page = new Page (false);
            ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);
// HACK!!
            script.addScript (
                    "alert (\"Maximale Teilnehmeranzahl bereits überschritten." +
                    " Keine Anmeldung mehr möglich!\");" +
                    IOHelpers.getShowObjectJavaScript (this.oid.toString ()) + "\n");
            page.body.addElement (script);

            // build the page and show it to the user:
            try
            {
                page.build (this.env);
            } // try
            catch (BuildException e)
            {
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            } // catch
            return;
        } // if

        // execute stored procedure: p_Object_01$create with Participants

/*
        i = 0;                      // number of actual parameter
        announced = false;          // return parameter
        free = 0;                   // number of free places
        params = new Parameter[10]; // contains the parameters
*/

        // create the stored procedure call:
        StoredProcedure sp = new StoredProcedure(
                "p_Participant_01$create",
                StoredProcedureConstants.RETURN_VALUE);

        // input parameters - basic object attributes
        // user id
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
            (this.getUser ().id != 0) ? this.user.id : 0);

        // operation
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, ibs.bo.Operations.OP_NEW);
        // tversionid
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.getTypeCache ().getTVersionId (DiaryTypeConstants.TC_Participant));
        // name - set the users name as Participant objects name
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.user.fullname);
        // container oid - set this (containers) oid as container id of
        // participant
        sp.addInParameter (ParameterConstants.TYPE_STRING, (this.oid != null) ? this.oid.toString () : OID.EMPTYOID);
        // container kind
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, BOConstants.CONT_STANDARD);
        // isLink -> no
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN, false);
        // linkedObjectId -> no object
        sp.addInParameter (ParameterConstants.TYPE_STRING, OID.EMPTYOID);
        // description
        sp.addInParameter (ParameterConstants.TYPE_STRING, "");

        // output parameters - not needed in this context, but
        // must be given to execute stored procedure
        // oid
        sp.addOutParameter (ParameterConstants.TYPE_STRING);

        try
        {
            // perform the function call:
            BOHelpers.performCallFunctionData (sp, this.env);
        } // try
        catch (NoAccessException e)
        {
            // nothing to do
        } // catch

        // reload frameset
//        showFrameSet ();
        this.show (UtilConstants.REP_STANDARD);
    } // addParticipant


    /**************************************************************************
     * Removes participant from term.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     * @exception   ObjectNotFoundException
     *              The required object was not found during search, i.e. there
     *              does not exist any tab of the actual object with the
     *              required name.
     */
    public void removeParticipant ()
        throws NoAccessException, ObjectNotFoundException
    {
        // Notice: for most operations VIEW-rights will be enough,
        // because if you are allowed to view the participants list
        // you should be allowed to announce to the referring term.

        SQLAction action = null;        // the action object used to access the
                                        // database
        int rowCount;                   // row counter
        OID partOid = null;             // oid of the participant

        // first: get oid of participant object
        // get the participant out of the database:
        // create the SQL String to select all tuples
        String queryStr =
            " SELECT o.oid FROM ibs_Object o, m2_Participant_01 v " +
            " WHERE containerId = " + this.oid.toStringQu () + " " +
            " AND tversionid = " + this.getTypeCache ().getTVersionId (DiaryTypeConstants.TC_Participant) +
            " AND o.oid = v.oid " +           // join with values table
            " AND v.announcerId = " + this.getUser ().id + // user announced?
            " AND o.state = 2" +              // only active participant objects
            " AND o.name = '" + this.getUser ().fullname + "'"; // user himself to unannounce

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection - only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();

            // execute non-action query
            rowCount = action.execute (queryStr, false);

            // empty resultset?
            if (rowCount == 0)
            {
                return;                 // terminate this method
            } // if
            // error while executing?
            else if (rowCount < 0)
            {
                // an error occurred
                IOHelpers.showMessage (
                    "Error while performing: " + queryStr,
                    this.app, this.sess, this.env);
                // exit
                return;
            } // if

            // everything ok - go on

            // get the participant's oid:
            partOid = SQLHelpers.getQuOidValue (action, "oid");

            // get next element
            action.next ();

            // the last tuple has been processed
            // end transaction
            action.end ();

        } // try
        catch (DBError dbErr)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (dbErr, this.app, this.sess, this.env, false);
            return;
        } // catch
        finally
        {
            // close db connection in every case - only workaround - db
            // connection must be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        // now delete object from ibs_object via stored procedure

        // define local variables:
        int retVal = UtilConstants.QRY_OK; // return value of query

        // create the stored procedure call:
        StoredProcedure sp = new StoredProcedure(
                "p_Object$delete",
                StoredProcedureConstants.RETURN_VALUE);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // input parameters - basic object attributes
        // oid of participants:
        BOHelpers.addInParameter (sp, partOid);
        // user id:
        if (this.user != null)
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.user.id);
        } // if
        else
        {
            sp.addInParameter (ParameterConstants.TYPE_INTEGER, 0);
        } // else
        // operation:
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, Operations.OP_VIEW);

        // perform the function call:
        retVal = BOHelpers.performCallFunctionData (sp, this.env);

        if (retVal == UtilConstants.QRY_OBJECTNOTFOUND) // object not found?
        {
            // raise no access exception
            ObjectNotFoundException error = new ObjectNotFoundException (
                MultilingualTextProvider.getMessage (UtilExceptions.EXC_BUNDLE, 
                    UtilExceptions.ML_E_OBJECTNOTFOUNDEXCEPTION, env));
            throw error;
        } // else if object not found

//        showFrameSet ();
        this.show (UtilConstants.REP_STANDARD);
    } // removeParticipant


    /**************************************************************************
     * Sets term to which participant container is announced. <BR/>
     *
     * @param   term    announced term
     */
    public void setTerm (OID term)
    {
        this.containerId = term;
    } // setTerm


    /**************************************************************************
     * Set the headings and the orderings for this container. <BR/>
     * Overload this function if you need other headings as the standard container. <BR/>
     */
    protected void setHeadingsAndOrderings ()
    {
        // set super attribute
        this.headings = MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE, 
            DiaryConstants.LST_HEADINGS_PARTICIPANTS, env);
        // set super attribute
        this.orderings = DiaryConstants.LST_ORDERINGS_PARTICIPANTS;

        // ensure that there is an available ordering taken:
        this.ensureAvailableOrdering ();
    } // setHeadingsAndOrderings


    /**************************************************************************
     * Perform stored procedure 'checkParticipants'
     *
     * @return  The participants values.
     */
    protected CheckParticipantsValues checkParticipants ()
    {
        SQLAction action = null;        // the action object used to access the
                                        // database

        // create new return-value object
        CheckParticipantsValues cp = new CheckParticipantsValues ();

        // create procedure
        StoredProcedure sp = new StoredProcedure ();
        // contains the parameters
        Parameter [] params = new Parameter[6];

        // set stored procedure return type
        sp.setReturnType (StoredProcedureConstants.RETURN_NOTHING);
        // set stored procedure name
        sp.setName (this.procCheckParticipants);
        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        int i = -1;                         // initialize parameter number

        // oid - id of container
        params[++i] = new Parameter ();
        params[i].setDataType (ParameterConstants.TYPE_STRING);
        params[i].setDirection (ParameterConstants.DIRECTION_IN);
        if (this.oid != null)
        {
            params[i].setValue (this.oid.toString ());
        } // if
        else
        {
            params[i].setValue (OID.EMPTYOID);
        } // else
        // user id
        params[++i] = new Parameter ();
        params[i].setDataType (ParameterConstants.TYPE_INTEGER);
        params[i].setDirection (ParameterConstants.DIRECTION_IN);
        if (this.getUser ().id != 0)
        {
            params[i].setValue (this.getUser ().id);
        } // if
        else
        {
            params[i].setValue (0);
        } // else

        // output parameters
        // announced
        params[++i] = new Parameter ();
        params[i].setDataType (ParameterConstants.TYPE_BOOLEAN);
        params[i].setDirection (ParameterConstants.DIRECTION_OUT);
        // free
        params[++i] = new Parameter ();
        params[i].setDataType (ParameterConstants.TYPE_INTEGER);
        params[i].setDirection (ParameterConstants.DIRECTION_OUT);
        // unanouncement deadline
        params[++i] = new Parameter ();
        params[i].setDataType (ParameterConstants.TYPE_DATE);
        params[i].setDirection (ParameterConstants.DIRECTION_OUT);
        // startDate  (anouncement deadline)
        params[++i] = new Parameter ();
        params[i].setDataType (ParameterConstants.TYPE_DATE);
        params[i].setDirection (ParameterConstants.DIRECTION_OUT);

        // add parameters to stored procedure
        for (int j = 0; j <= i; j++)
        {
            sp.addParameter (params[j]);
        } // for j

//IOHelpers.showProcCall (this, sp, this.sess, this.env);

        // execute stored procedure
        try
        {
            // open db connection - only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();
            // execute stored procedure - return value
            // gives right-information
            action.execStoredProc (sp);

            // end action
//            action.end ();

            // get parameters
            // set object properties - get them out of parameters
            i = 1;
            cp.announced = params[++i].getValueBoolean ();
            cp.free = params[++i].getValueInteger ();
            cp.deadline = params[++i].getValueDate ();
            cp.startDate = params[++i].getValueDate ();
        } // try
        catch (DBError e)
        {
            // get all errors (can be chained)
            String allErrors = new String ("");
            String h = new String (e.getMessage ());
            h += e.getError ();
            while (h != null)
            {
                allErrors += h;
                h = e.getError ();
            } // while
            // show the message
            IOHelpers.showMessage (allErrors,
                this.app, this.sess, this.env);
        } // catch
        finally
        {
            // close db connection in every case - only workaround - db
            // connection must be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        // exit
        return cp;
    } // checkParticipants

    /**************************************************************************
     * Delivers Date 'deadline' of referred term
     *
     * @return  The deadline date.
     */
    public Date getDeadline ()
    {
        // create return-value container-object for stored procedure
        CheckParticipantsValues cp = new  CheckParticipantsValues ();
        // execute stored procedure and store return values in cp
        cp = this.checkParticipants ();

        // return deadline
        return cp.deadline;
    } // getDeadline

    /**************************************************************************
     * Delivers number of  'free places' of referred term. <BR/>
     *
     * @return  The number of free places.
     */
    public int getFreePlaces ()
    {
        // create return-value container-object for stored procedure
        CheckParticipantsValues cp = new  CheckParticipantsValues ();
        // execute stored procedure and store return values in cp
        cp = this.checkParticipants ();

        // return deadline
        return cp.free;
    } // getDeadline
} // class ParticipantsContainer_01


/******************************************************************************
 * It is a small class, created to hold some return values of
 * the stored procedures 'checkParticipants'
 ******************************************************************************
 */
class CheckParticipantsValues
{
    /**
     * Are there any participants?
     */
    boolean announced = false;
    /**
     * How many free places?
     */
    int free = 0;
    /**
     * Deadlined term?
     */
    Date deadline = null;
    /**
     * Start date of term - announcements only possible until this date
     */
    Date startDate = null;

    /**
     * constructor
     */
    public CheckParticipantsValues ()
    {
        // nothing to do
    } // CheckParticipantsValues
} // class CheckParticipantValues
