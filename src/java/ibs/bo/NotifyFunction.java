/*
 * Class: NotifyFunction.java
 */

// package:
package ibs.bo;

// imports:
import ibs.app.AppFunctions;
import ibs.app.CssConstants;
import ibs.bo.type.TypeNotFoundException;
import ibs.io.Environment;
import ibs.io.HtmlConstants;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.LayoutConstants;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.func.FunctionArguments;
import ibs.obj.func.IbsFunction;
import ibs.service.notification.INotificationService;
import ibs.service.notification.NotificationFailedException;
import ibs.service.notification.NotificationTemplate;
import ibs.service.user.User;
import ibs.tech.html.BuildException;
import ibs.tech.html.FormElement;
import ibs.tech.html.GroupElement;
import ibs.tech.html.ImageElement;
import ibs.tech.html.InputElement;
import ibs.tech.html.LinkElement;
import ibs.tech.html.NewLineElement;
import ibs.tech.html.Page;
import ibs.tech.html.RowElement;
import ibs.tech.html.ScriptElement;
import ibs.tech.html.SelectElement;
import ibs.tech.html.SpanElement;
import ibs.tech.html.StyleSheetElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TableElement;
import ibs.tech.html.TextElement;
import ibs.util.FormFieldRestriction;

import java.util.Enumeration;
import java.util.Vector;


/******************************************************************************
 * The NotifyFunction handles distributing objects. The function shows the DisributeForm
 * where you can choose the receivers who should be notified.
 * The function calls the NotificationService which performs the notification. <BR/>
 *
 * @version     $Id: NotifyFunction.java,v 1.27 2010/11/12 10:17:53 btatzmann Exp $
 *
 * @author      Monika Eisenkolb (ME), 000123
 ******************************************************************************
 */
public class NotifyFunction extends IbsFunction
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: NotifyFunction.java,v 1.27 2010/11/12 10:17:53 btatzmann Exp $";


    /**
     * the actual representation form. <BR/>
     */
    private int representationForm = 1;

    /**
     * oid of the chosen object. <BR/>
     */
    private OID objectOid = null;


    /**************************************************************************
     * This constructor creates a new instance of the class NotifyFunction. <BR/>
     */
    public NotifyFunction ()
    {
        // nothing to do
    } // NotifyFunction


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // nothing to do
    } // initClassSpecifics


    /**************************************************************************
     * Initializes a Function. <BR/>
     *
     * The user object is also stored in a specific
     * property of this object to make sure that the user's context can be used
     * for getting his/her rights. <BR/>
     * {@link #env env} is initialized to the provided object. <BR/>
     * {@link #sess sess} is initialized to the provided object. <BR/>
     * {@link #app app} is initialized to the provided object. <BR/>
     *
     * @param   aId     The id of the function.
     * @param   aUser   Object representing the user.
     * @param   aEnv    The actual call environment.
     * @param   aSess   The actual session info.
     * @param   aApp    The global application info.
     */
    public void initFunction (int aId, User aUser, Environment aEnv,
                            SessionInfo aSess, ApplicationInfo aApp)
    {
        super.initFunction (aId, aUser, aEnv, aSess, aApp);

// HACK AJ BEGIN
        // there has to be an empty oid to mark this function as
        // non-physical object. this is necessary for some GUI-Methods which are
        // trying to read rights from DB
        this.setOid (OID.getEmptyOid ());
// HACK AJ END
    } // initFunction


    /**************************************************************************
     * mainmethod = sequence control of this function. <BR/>
     */
    public void start ()
    {
        // get current event
        int event;
        event = this.getEvent ();

        // get the oid of the chosen object
        this.setOid (
            this.objectOid = this.env.getOidParam (BOArguments.ARG_OID));

        if (this.oid != null)
        {
            // check which event was thrown
            switch (event)
            {
                case NotifyEvents.EVT_SHOWFORM:
                    // shows the DistributeForm where you can choose the receivers
                    this.showDistributeForm (this.representationForm);
                    break;

                case NotifyEvents.EVT_DONOTIFICATION:
                    // calls the NotificationService
                    this.callNotificationService ();
                    break;

                default:
                    // DistributeForm is shown
                    this.showDistributeForm (this.representationForm);
                    break;

            } // switch
        } // if

    } // start

    /**************************************************************************
     * Generates the form for distributing objects to a set of receivers. <BR/>
     * There will be 3 main selection boxes displayed
     * <UL>
     * <LI>the group selection box
     * <LI>the users and roles selection box (multiple selection)
     * <LI>the receivers selection box (multiple selection)
     * </UL>
     * The groups selection box shows the groups the user is allowed to access.
     * By selecting a group and clicking the "apply" button, the corresponding
     * members and roles of this group will be displayed in the users selection
     * box. the user can add and remove entries from the users selection box
     * to the receivers selection box by clicking the "add" and the "remove"
     * buttons. Additionly the are 2 inputfields created to specify a filter
     * and appropriate "set filter" buttons in order to shorten the entries
     * displayed in the groups and then users selection boxes.
     * There is also an inputfield generated to add an postIt (comment) to
     * the distributed object.
     *
     * @param   representationForm  Kind of representation.
     * @param   receiverOids    Oids of receivers to be selected when form is
     *                          opened the first time.
     * @param   stdReceiversDeleteable
     *                          Allow to delete stdreceivers (in receiverOids)
     *                          from receiverlist or not.
     */
    protected void performShowDistributeForm (int representationForm,
                                              OID[] receiverOids,
                                              boolean stdReceiversDeleteable)
    {
        int i;
        String button;
        boolean isNewForm = false;      // indicated that form has been initialized
        boolean newGroupSelected = false;  // indicates if a group has been newly selected
//        SelectElement receivers = null;
        String usersFilter = null;      // the Filterstring for the users
        String groupsFilter = null;     // the filterstring for the groups
        String group = null;            // group which is selected

        // check if the form has just been initialized
        String init = this.env.getParam (BOArguments.ARG_INITFORM);
        if (init == null || init.length () == 0)
        {
            isNewForm = true;
            // if the form is just initialized, then the groupSelectionBox
            // has to be created (a DBConnection is necessary)
            // it is stored in the session
            this.sess.groupBox = this.createGroupsSelectionBox ("", "");
            // create users
            this.sess.users = new Vector<String[]> ();
            // create receivers
            this.sess.receivers = new Vector<String[]> ();


/*
            // insert standardrecievers to recieverlist
            if (receiverOids != null)
            {
                receivers = createUsersSelectionBox (receiverOids);
            } // if
*/
        } // if

        // check if the set users filter button has been pressed
        button = this.env.getParam (BOArguments.ARG_SETUSERSFILTER);
        if (button != null && button.length () > 0)
        {
            usersFilter = this.env.getParam (BOArguments.ARG_USERSFILTER);
            // new Filter has been set, so a new Vector has to be created
            // and stored in the session
            group = this.env.getParam (BOArguments.ARG_ACTIVEGROUP);
            // group has to be retreived before
            if (group == null)
            {
                group = "";
            } // if
            this.sess.users = this.createUsersVector (group, usersFilter);
        } //if
        else
        {
            usersFilter = this.env.getParam (BOArguments.ARG_ACTIVEUSERSFILTER);
        } // else
        if (usersFilter == null)
        {
            usersFilter = "";
        } // if

        // check if the set group button has been pressed
        button = this.env.getParam (BOArguments.ARG_SETGROUPS);
        if (button != null && button.length () > 0)
        {
            group = this.env.getParam (BOArguments.ARG_GROUPS);
            // a new group has been selected
            // the selection of the options of the
            // GroupsSelectionBox has to be updatet
            newGroupSelected = true;
            // a new group has been selected, so the users Vector
            // has to be retreieved from the Database
            // and stored in the session
            this.sess.users = this.createUsersVector (group, usersFilter);
        } //if
        else
        {
            group = this.env.getParam (BOArguments.ARG_ACTIVEGROUP);
        } // else
        if (group == null)
        {
            group = "";
        } // if

        // check if the set group filter button has been pressed
        button = this.env.getParam (BOArguments.ARG_SETGROUPSFILTER);
        if (button != null && button.length () > 0)
        {
            groupsFilter = this.env.getParam (BOArguments.ARG_GROUPSFILTER);
            // activate the filter for groups has been pressed
            // consequentally, the groupsSelectionBox has to be
            // actuallized from the DB
            // and it is stored in the session
            this.sess.groupBox = this.createGroupsSelectionBox (groupsFilter, group);
        } //if
        else
        {
            groupsFilter = this.env.getParam (BOArguments.ARG_ACTIVEGROUPSFILTER);
        } // else
        if (groupsFilter == null)
        {
            groupsFilter = "";
        } // if

        // check if the add user button has been pressed
        // get the active receivers out of the application objects
        button = this.env.getParam (BOArguments.ARG_ADD);
        if (button != null && button.length () > 0)
        {
            String[] addReceiversArray = this.env.getMultipleParam (BOArguments.ARG_USERS);
            if (addReceiversArray != null)
            {
                // loop through the array of users to be added
                for (i = 0; i < addReceiversArray.length; i++)
                {
                    // check if entry is not empty
                    if (addReceiversArray[i].length () > 0)
                    {
                        // get the name to the corresponding oid
                        boolean found = false;
                        Enumeration<String[]> vectEnum = this.sess.users.elements ();
                        while (vectEnum.hasMoreElements () && !found)
                        {
                            String[] t = vectEnum.nextElement ();
                            if (t != null)
                            {
                                if (t[1].equalsIgnoreCase (addReceiversArray[i]))
                                {
                                    found = true;
                                    // add this element to the receicers array
                                    this.sess.receivers.addElement (t);
                                } // if
                            } // if
                        } // while
                    } //if
                } //for
            } // if (addReceiversArray != null)
        } // if

        // check if the remove receivers button has been pressed
        button = this.env.getParam (BOArguments.ARG_REMOVE);
        if (button != null && button.length () > 0)
        {
            String[] rmReceiversArray = this.env
                .getMultipleParam (BOArguments.ARG_RECEIVERS);
            if (rmReceiversArray != null)
            {
                // loop through the array of users to be deleted
                for (i = 0; i < rmReceiversArray.length; i++)
                {
                    // get the corresponding object
                    boolean found = false;
                    Enumeration<String[]> vectEnum = this.sess.receivers.elements ();
                    while (vectEnum.hasMoreElements () && !found)
                    {
                        Object o = vectEnum.nextElement ();
                        String[] t = (String[]) o;
                        if (t != null)
                        {
                            if (t[1].equalsIgnoreCase (rmReceiversArray[i]))
                            {
                                found = true;
                                // remove the object from the receivers Vector in the session
                                this.sess.receivers.removeElement (o);
                            } // if
                        } // if
                    } // while
                } // for
            } // if (rmReceiversArray != null)
        } // if

        // init variables for page
        Page page = new Page ("Distribute Object", false);
        // Stylesheetfile wird geladen
        StyleSheetElement style = new StyleSheetElement ();
        style.importSS =
            this.sess.activeLayout.path + this.env.getBrowser () + "/" +
            this.sess.activeLayout.elems[LayoutConstants.SHEETINFO].styleSheet;
        page.head.addElement (style);
        // init variables for table
        RowElement tr;
        TableDataElement td;
        NewLineElement nl = new NewLineElement ();
        InputElement iel;
        GroupElement gel;
        ImageElement img;
        LinkElement link;
        // get properties of object
        String name = this.env.getParam (BOArguments.ARG_NAME);
        if (name == null || name.length () == 0)
        {
/* KR 020125: not necessary because already done before
            try
            {
                // get object properties from the db
                retrieve (Operations.OP_READ);
            } // try
            catch (NoAccessException e) // no access to object allowed
            {
                showNoAccessMessage (Operations.OP_READ);
            } // catch
            catch (AlreadyDeletedException e) // no access to objects allowed
            {
                // send message to the user:
                showAlreadyDeletedMessage ();
            } // catch
*/
        } // if
        else
        {
            this.name = name;
        } // else

        // deactivate tabs and buttons:
        page.body.addElement (this.getButtonsTabsDeactivationScript ());

        // create header
        FormElement form;

        // get the object that will be distributed
        BusinessObject moObj = null;
        moObj = this.getObject (this.objectOid);
        // get the actual icon to this object
        String actIcon = "";
        if (moObj != null)
        {
            actIcon = moObj.icon;
        } // if


        if (this.isTab ())              // object is part of upper object?
        {
            form = this.createFormHeader (page, this.name, this.getNavItems (),
                this.containerName, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NOTIFY, env), null, actIcon,
                this.containerName);
        } // if
        else                        // object exists independently
        {
            form = this.createFormHeader (page, this.name, this.getNavItems (),
                null, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NOTIFY, env), null, actIcon, this.containerName);
        } // else

        // set form parameters:
        form.addElement (new InputElement (BOArguments.ARG_OID,
            InputElement.INP_HIDDEN, "" + this.oid));
        form.addElement (new InputElement (BOArguments.ARG_NAME,
            InputElement.INP_HIDDEN, this.name));
        form.addElement (new InputElement (BOArguments.ARG_FUNCTION,
            InputElement.INP_HIDDEN, "" + AppFunctions.FCT_NOTIFICATION));
        //form.addElement (new InputElement (BOArguments.ARG_F, InputElement.INP_HIDDEN, "" + AppFunctions.FCT_OBJECTDISTRIBUTEFORM));
        form.addElement (new InputElement (FunctionArguments.ARG_EVENT,
            InputElement.INP_HIDDEN, "" + NotifyEvents.EVT_SHOWFORM));

        // set filter arguments:
        form.addElement (new InputElement (BOArguments.ARG_ACTIVEUSERSFILTER,
            InputElement.INP_HIDDEN, usersFilter));
        form.addElement (new InputElement (BOArguments.ARG_ACTIVEGROUPSFILTER,
            InputElement.INP_HIDDEN, groupsFilter));
        form.addElement (new InputElement (BOArguments.ARG_ACTIVEGROUP,
            InputElement.INP_HIDDEN, group));
        // indicate that form has been initialized:
        form.addElement (new InputElement (BOArguments.ARG_INITFORM,
            InputElement.INP_HIDDEN, "1"));

        // construct inner table
        TableElement table =  new TableElement ();

        table.classId = CssConstants.CLASS_INFO;

        table.border = 0;
        table.ruletype = IOConstants.RULE_NONE;
        table.frametypes = IOConstants.FRAME_BOX;
        table.width = HtmlConstants.TAV_FULLWIDTH;
        table.cellpadding = 5;

        // add groups selection box
        tr = new RowElement (5);

        tr.classId = CssConstants.CLASS_SELECT;

        gel = new GroupElement ();

        SpanElement span = new SpanElement ();
        span.classId = CssConstants.CLASS_NAME;
        span.addElement (new TextElement (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_GROUPS, env)));
        gel.addElement (span);

        gel.addElement (nl);
        iel = new InputElement (BOArguments.ARG_GROUPSFILTER,
            InputElement.INP_TEXT, groupsFilter);
        iel.size = 10;
        iel.maxlength = 10;
        gel.addElement (iel);
        gel.addElement (nl);
        iel = new InputElement (BOArguments.ARG_SETGROUPSFILTER,
            InputElement.INP_SUBMIT, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SETFILTER, env));
        gel.addElement (iel);
        gel.addElement (nl);

        // before adding the selectionbox,
        // the Selected option has to be altered if
        // a new group was selected
        if (newGroupSelected)
        {
            ((SelectElement) this.sess.groupBox).changeSelected (null, group);
        } // if
        // The cached SelectElement in the session
        // is used in the form
        gel.addElement ((SelectElement) this.sess.groupBox);

        td = new TableDataElement (gel);
        tr.addElement (td);

        // add select group button
        gel = new GroupElement ();
        gel.addElement (nl);
        gel.addElement (nl);
        gel.addElement (nl);
        gel.addElement (nl);
        gel.addElement (nl);
        iel = new InputElement (BOArguments.ARG_SETGROUPS,
            InputElement.INP_HIDDEN, "");
        gel.addElement (iel);
        img = new ImageElement (this.sess.activeLayout.path +
            BOPathConstants.PATH_ARROWICONS + "arrow_right.jpg");
        link = new LinkElement (img, IOConstants.URL_JAVASCRIPT +
            HtmlConstants.JREF_SHEETFORM +
            BOArguments.ARG_SETGROUPS + HtmlConstants.JREF_VALUEASSIGN + "'1';" +
            HtmlConstants.JREF_SHEETFORMSUBMIT);
        gel.addElement (link);
        td = new TableDataElement (gel);
        tr.addElement (td);

        // users selection box
        gel = new GroupElement ();

        span = new SpanElement ();
        span.classId = CssConstants.CLASS_NAME;
        span.addElement (new TextElement (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_USERS, env)));
        gel.addElement (span);

        gel.addElement (nl);
        iel = new InputElement (BOArguments.ARG_USERSFILTER,
            InputElement.INP_TEXT, usersFilter);
        iel.size = 10;
        iel.maxlength = 10;
        gel.addElement (iel);
        gel.addElement (nl);
        iel = new InputElement (BOArguments.ARG_SETUSERSFILTER,
            InputElement.INP_SUBMIT, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SETFILTER, env));
        gel.addElement (iel);
        gel.addElement (nl);

        // generate the users selection box
        gel.addElement (this.createSelectionBox (BOArguments.ARG_USERS,
            this.sess.users, this.sess.receivers));
        td = new TableDataElement (gel);
        tr.addElement (td);

        // add buttons to add or remove users from the receiver list
        gel = new GroupElement ();
        gel.addElement (nl);
        gel.addElement (nl);
        gel.addElement (nl);
        gel.addElement (nl);
        gel.addElement (nl);
//            iel = new InputElement (BOArguments.ARG_ADD, InputElement.INP_IMAGE);
//            iel.src = PATH_ARROWICONS + "arrow_right.jpg;
        iel = new InputElement (BOArguments.ARG_ADD, InputElement.INP_HIDDEN, "");
        gel.addElement (iel);
        img = new ImageElement (this.sess.activeLayout.path + BOPathConstants.PATH_ARROWICONS + "arrow_right.jpg");
        link = new LinkElement (img, IOConstants.URL_JAVASCRIPT +
            HtmlConstants.JREF_SHEETFORM + BOArguments.ARG_ADD +
            HtmlConstants.JREF_VALUEASSIGN + "'1';" +
            HtmlConstants.JREF_SHEETFORMSUBMIT);

        gel.addElement (link);
        gel.addElement (nl);
        gel.addElement (nl);
        iel = new InputElement (BOArguments.ARG_REMOVE, InputElement.INP_HIDDEN, "");
        gel.addElement (iel);
        img = new ImageElement (this.sess.activeLayout.path + BOPathConstants.PATH_ARROWICONS + "arrow_left.jpg");
        link = new LinkElement (img, IOConstants.URL_JAVASCRIPT +
            HtmlConstants.JREF_SHEETFORM + BOArguments.ARG_REMOVE +
            HtmlConstants.JREF_VALUEASSIGN + "'1';" +
            HtmlConstants.JREF_SHEETFORMSUBMIT);

        gel.addElement (link);
        td = new TableDataElement (gel);
        tr.addElement (td);

        // reviecer selection box
        gel = new GroupElement ();

        span = new SpanElement ();
        span.classId = CssConstants.CLASS_NAME;
        span.addElement (new TextElement (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_RECEIVERS, env)));
        gel.addElement (span);

        gel.addElement (nl);

        // generate the ReceiverSelectionBox
        // out of the Vector in the session
        gel.addElement (this.createSelectionBox (BOArguments.ARG_RECEIVERS,
            this.sess.receivers));

        td = new TableDataElement (gel);
        tr.addElement (td);
        table.addElement (tr);

        form.addElement (table);

        table = new TableElement (2);

        table.border = 0;
        table.classId = CssConstants.CLASS_INFO;
        String[] classIds = new String[2];
        classIds[0] = CssConstants.CLASS_NAME;
        classIds[1] = CssConstants.CLASS_VALUE;
        table.classIds = classIds;

        table.width = HtmlConstants.TAV_FULLWIDTH;

        this.formFieldRestriction =
            new FormFieldRestriction (true, BOConstants.MAX_LENGTH_NAME, 0);

        // Subject
        if (isNewForm)
        {
            this.showFormProperty (table, BOArguments.ARG_SUBJECT,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SUBJECT, env), Datatypes.DT_TEXT, this.name);
        } // if
        else
        {
            String subject = this.env.getStringParam (BOArguments.ARG_SUBJECT);
            if (subject == null)
            {
                subject = "";
            } // if
            this.showFormProperty (table, BOArguments.ARG_SUBJECT,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SUBJECT, env), Datatypes.DT_TEXT, subject);
        } // else

        // freeze
//! AJ 000523 code temporary dropped ... //////////////////////////////////////
/*
        if (isNewForm)
        {
            showFormProperty (table, BOArguments.ARG_FREEZE,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_FREEZE, env), Datatypes.DT_BOOL, "" + false);
        } // if
        else
        {
            int freeze = env.getBoolParam (BOArguments.ARG_FREEZE);
            if (freeze == IOConstants.BOOLPARAM_TRUE)
                showFormProperty (table, BOArguments.ARG_FREEZE,
                    MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_FREEZE, env), Datatypes.DT_BOOL,
                    Datatypes.BOOL_TRUE);
            else
                showFormProperty (table, BOArguments.ARG_FREEZE,
                    MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_FREEZE, env), Datatypes.DT_BOOL,
                    Datatypes.BOOL_FALSE);
        } // else
*/
//! AJ ... because of problems with specification and rights !!
//! ... AJ 000523 code temporary dropped //////////////////////////////////////


/*
        showFormProperty (table, BOArguments.ARG_ACTIVITIES, TOK_ACTIVITIES,
            Datatypes.DT_ACTIVITIES, env.getParam (BOArguments.ARG_ACTIVITIES));
*/
        // generate the activities selection box
        gel = new GroupElement ();
        String activities = this.env.getParam (BOArguments.ARG_ACTIVITIES);
        if (activities == null)
        {
            activities = "";
        } // if
        gel.addElement (this.createActivitiesSelectionBox (activities));
        this.showFormProperty (table, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_ACTIVITIES, env), gel);
        // description
        this.formFieldRestriction =
            new FormFieldRestriction (true, BOConstants.MAX_LENGTH_DESCRIPTION, 0);

        if (isNewForm)
        {
            this.showFormProperty (table, BOArguments.ARG_DESCRIPTION,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_POSTIT, env), Datatypes.DT_DESCRIPTION, "");
        } // if
        else
        {
            String description = this.env.getParam (BOArguments.ARG_DESCRIPTION);
            if (description == null)
            {
                description = "";
            } // if
            this.showFormProperty (table, BOArguments.ARG_DESCRIPTION,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_POSTIT, env), Datatypes.DT_DESCRIPTION, description);
        } // else

        form.addElement (table);

        // create footer
        this.createFormFooter (form, "this.form." + BOArguments.ARG_FUNCTION +
            ".value = '" + this.getUserInfo ().okDistributeFunction + "';" +
            "this.form." + FunctionArguments.ARG_EVENT + ".value = '" +
            NotifyEvents.EVT_DONOTIFICATION + "';", null, "   OK   ", null,
            false, false);

        // build the page and show it to the user:
        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e.getMsg (), this.app, this.sess, this.env);
        } // catch
    } // performShowDistributeForm


    /**************************************************************************
     * The DistributeForm is shown. <BR/>
     *
     * @param   representationForm  ???
     */
    public void showDistributeForm (int representationForm)
    {
        if (true)                       // business object resists on this
                                        // server?
        {
            if (this.showDistributeFormAsFrameset && this.framesetPossible)
                                        // show as frameset?
            {
                // create a frameset to show the actual view within:
                this.showFrameset (representationForm, AppFunctions.FCT_NOTIFICATION);
            } // if show as frameset
            else                        // don't show as frameset
            {
                OID[] receivers = null;
                boolean stdReceiversDeletable = true;

                if (this.oid != null)   // oid defined?
                {
                    // get instance of object to be distributed:
                    BusinessObject obj = this.getObject (this.oid);

                    // get the standard receivers:
                    receivers = obj.getStdNotificationReceivers ();
                } // if oid defined

                stdReceiversDeletable = receivers == null;

                // display the distribution form:
                this.performShowDistributeForm (representationForm, receivers,
                    stdReceiversDeletable);

                this.framesetPossible = true; // frameset view is possible again
            } // else don't show as frameset
        } // if business object resists on this server
        else                            // object resists on another server
        {
            // invoke the object on the other server:
        } // else object resists on another server
    } // showDistributeForm


    /**************************************************************************
     * A message box is shown telling if the notification was successfull. <BR/>
     *
     * @param result    true if notification was successfull
     * @param message   message to display
     * @param notiError the names or oids of the receivers who could not
     *                  be notified at all
     */
    private void showResultMessage (boolean result, String message, String notiError)
    {
        ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);
        String messageLocal = message;  // variable for local assignments

        script.addScript (IOHelpers.getShowObjectJavaScript ("" + this.oid));

        // set a message that informs the user if object could have been
        // distributed:
        if (!result)
        {
            messageLocal += "\\r" + notiError;
        } // if

        // display the message:
        this.showPopupMessage (messageLocal, script);
    } // showResultMessage


    /**************************************************************************
     * The important attributes for the NotififcationTemplate object are set.
     * Then the NotificationService that is performing the distributing of
     * the objects to the chosen receivers is called. At last a messagebox
     * tells if the notification was successfull. <BR/>
     */
    private void callNotificationService ()
    {
        // get the parameters necessary for initializing a NotifyTemplate
        String subject = this.env.getParam (BOArguments.ARG_SUBJECT);
        if (subject == null)
        {
            subject = "";
        } // if
        String description = this.env.getParam (BOArguments.ARG_DESCRIPTION);
        if (description == null)
        {
            description = "";
        } // if
        String activities = this.env.getParam (BOArguments.ARG_ACTIVITIES);
        if (activities == null)
        {
            activities = "";
        } // if

        // a Notificationtemplate and a NotificationService object are created
        INotificationService notiService = null;
        NotificationTemplate template = null;
        boolean result = true;          // telling if the notificationservice
                                        // was successfull or not
        String message = MultilingualTextProvider.getMessage(NotifyMessages.MSG_BUNDLE, 
            NotifyMessages.ML_MSG_FUNCTIONSUCCEEDED, env);
                                        // string holding the message shown
                                        // to the user after notification
        BusinessObject obj = null;      // the object which is performing the
                                        // notification

        if (this.oid != null)           // oid defined?
        {
            // get instance of object to be distributed:
            obj = this.getObject (this.oid);
        } // if oid defined
        else                            // no oid defined
        {
            // create dummy object:
            obj = new BusinessObject (OID.getEmptyOid (), this.user);
        } // else no oid defined

        notiService = obj.getNotificationService ();
        notiService.initService (this.user, this.env, this.sess, this.app);

        template = new NotificationTemplate (subject, "", description,
            activities);

        // check if there are any receivers:
        if (this.sess.receivers.size () > 0)
        {
            try
            {
                obj.callNotificationService (notiService, template, this.oid);
            } // try
            catch (NotificationFailedException e)
            { // notification service failed
                //this.functionOk = 0;
                result = false;
                message = MultilingualTextProvider.getMessage(NotifyMessages.MSG_BUNDLE, 
                    NotifyMessages.ML_MSG_FAILEDRECEIVERUSERNAMES, env);
            } // catch
        } // if
        else                            // no receivers
        {
            result = false;
            message = MultilingualTextProvider.getMessage(NotifyMessages.MSG_BUNDLE, 
                NotifyMessages.ML_MSG_NORECEIVERS, env);
        } // else no receivers

        // messagebox is shown if notification was done successfully
        this.showResultMessage (result, message, notiService
            .getFailedReceiverUserNames ());
    } // callNotificationService


    /**************************************************************************
     * Creates a java object representing the actual business object. <BR/>
     *
     * @param   oid     Object id of the required business object.
     *
     * @return  The generated object. <BR/>
     *          null if the object could not be generated.
     */
    protected BusinessObject getObject (OID oid)
    {
        BusinessObject obj = null;      // the business object

        if (oid != null)                // valid oid?
        {
            try
            {
                obj = this.getObjectCache ().fetchObject (oid, this.user,
                    this.sess, this.env, true);
            } // try
            catch (ObjectNotFoundException e)
            {
                // check if a real object should be found or a temporary object:
                if (oid.isTemp ())      // temporary object?
                {
                    // no problem - work as if the object where found:
                } // if temporary object
                else                    // real object
                {
                    // show corresponding error message:
                    IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                        BOMessages.ML_MSG_OBJECTNOTFOUND, this.env),
                        this.app, this.sess, this.env);
                } // else real object
            } // catch
            catch (TypeNotFoundException e)
            {
                // show corresponding error message:
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            } // catch
            catch (ObjectClassNotFoundException e)
            {
                // show corresponding error message:
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            } // catch
            catch (ObjectInitializeException e)
            {
                // show corresponding error message:
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            } // catch
            catch (Exception e)
            {
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            } // catch
        } // if valid oid
        else                            // no valid oid
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_NOOID, this.env),
                this.app, this.sess, this.env);
        } // else no valid oid

        return obj;                     // return the created object
    } // getObject

} // NotifyFunction
