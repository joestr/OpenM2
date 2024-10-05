/*
 * Class: m2ReminderObserverJobRepresentation.java
 */

// package:
package ibs.service.observer;

// imports:
import ibs.app.AppFunctions;
import ibs.app.CssConstants;
import ibs.app.FilenameElement;
import ibs.bo.BOArguments;
import ibs.bo.BOConstants;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.bo.States;
import ibs.io.HtmlConstants;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.service.observer.M2ObserverArguments;
import ibs.service.observer.M2ObserverEvents;
import ibs.service.observer.M2ObserverJobRepresentation;
import ibs.service.observer.M2ReminderObserverJob;
import ibs.service.observer.M2ReminderObserverJobData;
import ibs.service.observer.ObserverConstants;
import ibs.service.user.User;
import ibs.tech.html.BuildException;
import ibs.tech.html.FormElement;
import ibs.tech.html.GroupElement;
import ibs.tech.html.InputElement;
import ibs.tech.html.LineElement;
import ibs.tech.html.Page;
import ibs.tech.html.RowElement;
import ibs.tech.html.ScriptElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TableElement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;


/******************************************************************************
 * This class.... <BR/>
 *
 * @version     $Id: M2ReminderObserverJobRepresentation.java,v 1.4 2010/04/07 13:37:16 rburgermann Exp $
 *
 * @author      hpichler
 ******************************************************************************
 */
public class M2ReminderObserverJobRepresentation
    extends M2ObserverJobRepresentation
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: M2ReminderObserverJobRepresentation.java,v 1.4 2010/04/07 13:37:16 rburgermann Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class OverlapContainer_01.
     */
    public M2ReminderObserverJobRepresentation ()
    {
        // call constructor of super class:
        super ();

        // initialize properties common to all subclasses:
    } // m2ReminderObserverJobRepresentation


    /**************************************************************************
     * Creates a m2ReminderObserverJobRepresentation object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     */
    public M2ReminderObserverJobRepresentation (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:
    } // OverlapContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // this objecttype is never persistent
        this.isPhysical = false;
        this.displayButtons = false;
        this.setIcon ();
    } // initClassSpecifics


    /**************************************************************************
     * Represent the object, i.e. its properties, to the user within a form.
     * <BR/>
     *
     * @param   representationForm  Kind of representation.
     * @param   changeFormFct       The function to be performed when submitting
     *                              the form.
     */
    protected void performShowChangeForm (int representationForm, int changeFormFct)
    {
        //////////////////////////////////
        //
        // check if jobdata object is set!
        if (this.job == null)
        {
            IOHelpers.showMessage (this.getClass ().getName () + ": JobObject not set!",
                                   this.app, this.sess, this.env);
            return;
        } // if

        // check if jobdata object is set!
        if (this.job.getJdata () == null)
        {
            IOHelpers.showMessage (this.getClass ().getName () + ": JobDataObject not set!",
                                   this.app, this.sess, this.env);
            return;
        } // if

        //
        // test and set data for form-properties
        //
        if (this.job.bo1 == null)
        {
            IOHelpers.showMessage (this.getClass ().getName () + ": ContextObject not set",
                                   this.app, this.sess, this.env);
            return;
        } // if
        //
        //
        //////////////////////////////////

        // clear filenames-vector in session (userinfo)
        // will be filled when Datatypes.DT_FILE input types are shown
        (this.getUserInfo ()).filenames = new Vector<FilenameElement> ();

        Page page = new Page ("Erinnerung", false);

        this.insertChangeFormStyles (page);

        // set the icon of this object:
        this.setIcon ();

        // show corresponding operation name:
        FormElement form = null;
        form = this.createFormHeader (page, "Erinnerung für " + this.job.bo1.name, null, null,
                                 null, null, this.job.bo1.icon, this.job.bo1.containerName);

        // generate function call
        form.addElement (new InputElement (BOArguments.ARG_OID,
                                           InputElement.INP_HIDDEN, "" + this.job.bo1.oid.toString ()));
        form.addElement (new InputElement (BOArguments.ARG_FUNCTION,
                                           InputElement.INP_HIDDEN,
                                           "" + AppFunctions.FCT_OBSERVERGUI));
        form.addElement (new InputElement (BOArguments.ARG_EVENT,
                                           InputElement.INP_HIDDEN,
                                           "" + M2ObserverEvents.EVT_GUIREGISTERPARAMETERJOB));
        form.addElement (new InputElement (M2ObserverArguments.ARG_OBS,
                                           InputElement.INP_HIDDEN,
                                           ObserverConstants.STANDARD_OBSERVER));
        form.addElement (new InputElement (M2ObserverArguments.ARG_JOBCLASS,
                                           InputElement.INP_HIDDEN,
                                           "ibs.service.observer.m2ReminderObserverJob"));
        form.addElement (new InputElement (M2ObserverArguments.ARG_JOBID,
                                           InputElement.INP_HIDDEN,
                                           "" + 0));

        TableElement table = this.createFrame (representationForm, 2);
        table.border = 0;
        table.classId = CssConstants.CLASS_INFO;
        String[] classIds = new String[2];
        classIds[0] = CssConstants.CLASS_NAME;
        classIds[1] = CssConstants.CLASS_VALUE;
        table.classIds = classIds;

        // loop through all properties of this object and display them:
        this.properties = 0;
        this.showFormProperties (table);
        form.addElement (table);

        this.createFormFooter (form, null, null, null, null, false, false);
        this.showChangeFormBottom (page);

        if (this.p_isShowCommonScript)
        {
            // create the script to be executed on client:
            ScriptElement script;
            if (this.state == States.ST_CREATED) // the object was just created?
            {
                script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);
                script.addScript (
                    "top.oid = \"" + this.oid + "\";" +
                    "top.majorOid = \"" + this.oid + "\";" +
                    "top.containerId = \"" + this.containerId + "\";" +
                    BOConstants.CALL_SHOWTABSEMPTY);
//                    AppConstants.CALL_SHOWBUTTONSEMPTY);
            } // if the object was just created
            else                        // the object was created in past times
            {
                script = this.getCommonScript (false);
            } // else the object was created in past times
            page.body.addElement (script);
        } // if

        // build the page and show it to the user:
        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
    } // performShowChangeForm


    /**************************************************************************
     * Represent the properties of a BusinessObject object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showFormProperties (TableElement table)
    {
        //
        // - paramOid1  oid of business-object. <BR/>
        // - paramOid2  oid of workflow-object (must be empty if not used). <BR/>
        // - param0     string that specifies date duration. <BR/>
        //              ... date value: dd.mm.yyyy [hh:mm]. <BR/>
        //              ... duration values: 4711 m|h|d|w|m|y. <BR/>
        // - param1     name of user to notify. <BR/>
        // - param2     subject of notification. <BR/>
        // - param3     content of notification. <BR/>
        // - param4     description of notification. <BR/>
        // - param5     activity of notification. <BR/>
        // - param6     package.class.method (optional)<BR>*
        //

        // perform typecast
        M2ReminderObserverJob rJob = (M2ReminderObserverJob) this.job;
        M2ReminderObserverJobData rJobData = (M2ReminderObserverJobData) this.job.getJdata ();

        //
        // test and set data for form-properties
        //
        // jobs id
        int jId = rJobData.getId ();

        // paramOid1
        OID paramOid1 = rJob.bo1.oid;
        //
        // paramOid2: not needed
        OID paramOid2 = OID.getEmptyOid ();
        //
        // param0: reminder-date
        String param0DateString = null;
        Date param0Date = null;
        param0DateString = rJobData.getParam0 ();
        if (param0DateString == null)
        {
            param0Date = null;
        } // if
        else
        {
            // try some create conversion formats
            SimpleDateFormat df = new SimpleDateFormat ("dd.MM.yyyy HH:mm");
            df.setTimeZone (TimeZone.getDefault ());

            // try some parsing possibilities
            try
            {
                param0Date = df.parse (param0DateString);
            } // try
            catch (ParseException e)
            {
                IOHelpers.showMessage (this.getClass ().getName () +
                    ": Error while parsing datestring: " + param0DateString,
                    this.app, this.sess, this.env);
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
                return;     // break
            } // exception
        } // else

        //
        // param1: user-name (current user)
        String param1 = rJobData.getParam1 ();
        if (param1 == null || param1.length () == 0)
        {
            param1 = this.user.username;
        } // if
        //
        // param2: subject
        String param2 = rJobData.getParam2 ();
        if (param2 == null || param2.length () == 0)
        {
            param2 = "Erinnerung: " + rJob.bo1.name;
        } // if
        // param3: subject
        String param3 = rJobData.getParam3 ();
        if (param3 == null)
        {
            param3 = "";
        } // if

        //  set date/duration properties
        this.showFormProperty (table, "param0Date", "Erinnerung am" , Datatypes.DT_DATETIME, param0Date);
        this.showFormProperty (table, "param0Duration", "Erinnerung in" , Datatypes.DT_NUMBER, "");
        String[] ids = {"mi", "h", "d", "w", "m", "y"};
        String[] values = {"Minuten", "Stunden", "Tage", "Wochen", "Monaten", "Jahren"};
        this.showFormProperty (table, "param0Unit", "", Datatypes.DT_SELECT, "", ids, values, 1);
        this.showFormProperty (table, "param2", "Betreff" , Datatypes.DT_TEXT, param2);
        this.showFormProperty (table, "param3", "Inhalt" , Datatypes.DT_TEXTAREA, param3);

        // hidden parameters
        this.showFormProperty (table, M2ObserverArguments.ARG_JOBID, "jobid" , Datatypes.DT_HIDDEN, "" + jId);
        this.showFormProperty (table, "paramOid1", "paramOid1" , Datatypes.DT_HIDDEN, paramOid1.toString ());
        this.showFormProperty (table, "paramOid2", "paramOid2" , Datatypes.DT_HIDDEN, paramOid2.toString ());
        this.showFormProperty (table, "param1", "param1" , Datatypes.DT_HIDDEN, param1);

        // skip the rest, not needed
        this.showFormProperty (table, "param4", "param4" , Datatypes.DT_HIDDEN, "");
        this.showFormProperty (table, "param5", "param5" , Datatypes.DT_HIDDEN, "");
        this.showFormProperty (table, "param6", "param6" , Datatypes.DT_HIDDEN, "");
        this.showFormProperty (table, "param7", "param7" , Datatypes.DT_HIDDEN, "");
        this.showFormProperty (table, "param8", "param8" , Datatypes.DT_HIDDEN, "");
        this.showFormProperty (table, "param9", "param9" , Datatypes.DT_HIDDEN, "");

/*
        this.showFormProperty (table, AppArguments.ARG_INNEWS, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_INNEWS, env), Datatypes.DT_BOOL,"" + this.showInNews);
        // restrict: empty entries allowed
        this.formFieldRestriction =
            new FormFieldRestriction (true, BOConstants.MAX_LENGTH_DESCRIPTION, 0);

        this.showFormProperty (table, AppArguments.ARG_DESCRIPTION, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_DESCRIPTION, env), Datatypes.DT_DESCRIPTION, description);

        // create date format for servers locale
        DateFormat shortdate = DateFormat.getDateInstance (DateFormat.SHORT, l);
        // create date (current time)
        Date curDate = new Date ();
        // create current date string (e.g. '10.8.99')
        String curDateString = shortdate.format (curDate);
        // property 'validUntil':
        // set form field restriction (will automatically be used in
        // next showFormProperty)
        // restrict: no empty entries allowed
        // 0 .. default size/length values for datatype will be taken
        // null .. no upper bound
        this.formFieldRestriction =
            new FormFieldRestriction (false, 0, 0, curDateString, null);
        this.showFormProperty (table, AppArguments.ARG_VALIDUNTIL,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_VALIDUNTIL, env), Datatypes.DT_DATE, validUntil);
*/
    } // showFormProperties


    /**************************************************************************
     * Represent the properties of a BusinessObject object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showProperties (TableElement table)
    {
        // perform typecast
        M2ReminderObserverJob rJob = (M2ReminderObserverJob) this.job;
        M2ReminderObserverJobData rJobData = (M2ReminderObserverJobData) this.job.getJdata ();

        //
        // param0: reminder-date
        String param0DateString = null;
        Date param0Date = null;
        param0DateString = rJobData.getParam0 ();
        if (param0DateString == null)
        {
            param0Date = null;
        } // if
        else
        {
            // try some create conversion formats
            SimpleDateFormat df = new SimpleDateFormat ("dd.MM.yyyy HH:mm");
            df.setTimeZone (TimeZone.getDefault ());

            // try some parsing possibilities
            try
            {
                param0Date = df.parse (param0DateString);
            } // try
            catch (ParseException e)
            {
                IOHelpers.showMessage (this.getClass ().getName () +
                                       ":Error while parsing datestring: " +
                                       param0DateString,
                                       this.app, this.sess, this.env);
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
                return;     // break
            } // exception
        } // else
        //
        // param2: subject
        String param2 = rJobData.getParam2 ();
        if (param2 == null || param2.length () == 0)
        {
            param2 = "Erinnerung: " + rJob.bo1.name;
        } // if
        // param3: subject
        String param3 = rJobData.getParam3 ();
        if (param3 == null)
        {
            param3 = "";
        } // if

        //  show properties
        this.showProperty (table, "param0Date", "Erinnerung am" , Datatypes.DT_DATETIME, param0Date);
        this.showProperty (table, "param2", "Betreff" , Datatypes.DT_TEXT, param2);
        this.showProperty (table, "param3", "Inhalt" , Datatypes.DT_TEXTAREA, param3);
    } // showFormProperties


    /**************************************************************************
     * Represent something on the bottom of the info - view of an object to
     * the user. <BR/>
     *
     * Job already exists: Show Delete/Change Buttons.
     *
     * @param   page    Page where the info shall be added.
     */
    protected void showInfoBottom (Page page)
    {
        TableElement footerTable = new TableElement ();
        String[] alignments = {IOConstants.ALIGN_RIGHT};
        footerTable.border = 0;
        footerTable.ruletype = IOConstants.RULE_GROUPS;
        footerTable.width = HtmlConstants.TAV_FULLWIDTH;
        footerTable.alignment = alignments;
        footerTable.classId = CssConstants.CLASS_FOOTER;
        TableElement footerTable2 = new TableElement ();
        footerTable2.border = 0;
        footerTable2.ruletype = IOConstants.RULE_GROUPS;
        footerTable2.width = "50%";
        footerTable2.alignment = alignments;
        footerTable2.classId = CssConstants.CLASS_FOOTER;
        TableElement footerTable3 = new TableElement ();
        footerTable3.border = 0;
        footerTable3.ruletype = IOConstants.RULE_GROUPS;
        footerTable3.width = HtmlConstants.TAV_FULLWIDTH;
        footerTable3.alignment = alignments;
        footerTable3.classId = CssConstants.CLASS_FOOTER;

        RowElement tr = new RowElement (1);
        GroupElement group = new GroupElement ();
        TableDataElement td = new TableDataElement (group);
        tr.addElement (td);
        footerTable.addElement (tr);
        td.alignment = IOConstants.ALIGN_MIDDLE;
        LineElement line = new LineElement ();
        line.width = "90%";

        group.addElement (line);

        group.addElement (new InputElement ("Bearbeiten",
                InputElement.INP_SUBMIT +
                "\" onClick=\"" + IOConstants.URL_JAVASCRIPT + "self.open (top.getBaseUrl () + '&fct=191&evt=" + M2ObserverEvents.EVT_GUIREGISTERPARAMETERJOB +
                "&jid=" + this.job.getJdata ().getId () + "&oid=" + this.job.bo1.oid.toString () +
                "&obs=standard&jclass=m2ReminderObserverJob&frame=false', 'sheet')",
                "Bearbeiten"));

        group.addElement (new InputElement ("Löschen",
                InputElement.INP_SUBMIT +
                "\" onClick=\"self.open (top.getBaseUrl () + '&fct=191&evt=" +
                    M2ObserverEvents.EVT_GUIUNREGISTERPARAMETERJOB +
                "&jid=" + this.job.getJdata ().getId () +
                "&oid=" + this.job.bo1.oid.toString () +
                "&obs=standard&jclass=m2ReminderObserverJob&frame=false','sheet')",
                "Löschen"));

        line = new LineElement ();
        line.width = "75%";
        group.addElement (line);

        page.body.addElement (group);
    } // showInfoBottom

} // m2ReminderObserverJobRepresentation
