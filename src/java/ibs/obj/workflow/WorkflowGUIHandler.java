/*
 * Class: WorkflowGUIHandler.java
 */

// package:
package ibs.obj.workflow;

// imports:
//KR TODO: unsauber
import ibs.app.CssConstants;
//KR TODO: unsauber
import ibs.app.AppFunctions;
import ibs.bo.BOArguments;
import ibs.bo.BOConstants;
import ibs.bo.BOPathConstants;
import ibs.bo.BOTokens;
import ibs.bo.BusinessObject;
import ibs.bo.OID;
import ibs.di.DITokens;
import ibs.io.Environment;
import ibs.io.HtmlConstants;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.LayoutConstants;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.func.FunctionArguments;
import ibs.obj.workflow.WorkflowTokens;
import ibs.service.user.User;
import ibs.service.workflow.State;
import ibs.service.workflow.WorkflowArguments;
import ibs.service.workflow.WorkflowEvents;
import ibs.service.workflow.WorkflowMessages;
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
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.util.FormFieldRestriction;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;


/******************************************************************************
 * Handles all user-interfaces for the workflow-component.
 *
 * @version     $Id: WorkflowGUIHandler.java,v 1.23 2012/07/13 10:28:00 rburgermann Exp $
 *
 * @author      Horst Pichler (HP), 010201
 ******************************************************************************
 */
public class WorkflowGUIHandler extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: WorkflowGUIHandler.java,v 1.23 2012/07/13 10:28:00 rburgermann Exp $";


    /**************************************************************************
     * DO NOT USE THIS CONSTRUCTOR. <BR/>
     */
    public WorkflowGUIHandler ()
    {
        // nothing to do
    } // WorkflowGUIHandler


    /**************************************************************************
     * Initializes WorkflowService object; creates all needed base objects
     * like WorkflowLog, etc. <BR/>
     *
     * @param   user    The user for whom to create the instance.
     * @param   env     The current environment.
     * @param   sess    The user session.
     * @param   app     The global application info.
     */
    public WorkflowGUIHandler (User user, Environment env, SessionInfo sess,
                               ApplicationInfo app)
    {
        // set needed m2 environment-objects
        this.initObject (OID.getEmptyOid (), user, env, sess, app);
    } // WorkflowService



    /**************************************************************************
     * Shows a header message on start of a workflow-operation. <BR/>
     */
    public void showHeaderMessage ()
    {
        // init variables
        ScriptElement script;
        TextElement text;
        NewLineElement newline;
        Page page;

        // create the 1st part of the page that shows the forwarding output
        page = new Page (
            MultilingualTextProvider.getText (WorkflowTokens.TOK_BUNDLE,
                WorkflowTokens.ML_FORWARD_HEADER, env), false);
        // set the document's base:
        IOHelpers.setBase (page, this.app, this.sess, this.env);
// TO-CHANGE: create stylesheet for workflow output
//        page.head.addElement(style);

        // add 'javascript' to clear buttonbar and tab bar
        script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);
        script.addScript ("top.scripts.clearTabBar ();");
        script.addScript ("top.scripts.clearButtonBar ();");
        page.body.addElement (script);

        // add message:
        text = new TextElement ("<DIV ALIGN=\"LEFT\">" +
            MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                WorkflowMessages.ML_MSG_WORKFLOW_OPERATION_STARTED, env) + "</DIV>");
        page.body.addElement (text);
        newline = new NewLineElement ();
        page.body.addElement (newline);

        // show the page:
        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage ("WorkflowGUIHandler.showHeaderMessage",
                                   e.getMsg (), this.app, this.sess, this.env);
        } // catch
    } // showHeaderMessage


    /**************************************************************************
     * Shows an alert box with the success message. <BR/>
     */
    public void showSuccessConfirmation ()
    {
        this.showPopupMessage (
            MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                WorkflowMessages.ML_MSG_OBJECT_FORWARDED, env));
    } // showSuccessConfirmation


    /**************************************************************************
     * Shows an alert box with the success message. <BR/>
     */
    public void showNoSuccessConfirmation ()
    {
        this.showPopupMessage (
            MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                WorkflowMessages.ML_MSG_OBJECT_NOT_FORWARDED, env));
    } // showSuccessConfirmation


    /**************************************************************************
     * Java-Script that causes display to return to container-object. <BR/>
     */
    public void goBackToContainer ()
    {
        // create the output:
        this.processJavaScriptCode ("top.goback (1);");
    } // goBackToContainer

    /**************************************************************************
     * Java-Script that clears the tab bar and the button bar. <BR/>
     * Both JS methods are only processed when 'top.scripts' is available. <BR/>
     */
    public void clearTabAndButtonBar ()
    {
        try
        {
            StringBuffer buf = new StringBuffer ();

            ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);
            script.addScript ("if (top.scripts)");
            script.addScript ("{");
            script.addScript ("    if (top.scripts.clearTabBar) top.scripts.clearTabBar ();");
            script.addScript ("    if (top.scripts.clearButtonBar) top.scripts.clearButtonBar ();");
            script.addScript ("}");
            script.build (this.env, buf);

            this.env.write (buf.toString ());
        } // try
        catch (BuildException e)
        {
            // Do nothing
        } // catch
    } // clearTabAndButtonBar

    /**************************************************************************
     * Represent a form to the user where the alternative nex step of a
     * workflow can be selected. <BR/>
     *
     * @param   forwardObj  the forwarded object
     * @param   state       the state where the alternative-transition is defined
     */
    public void showAlternativeSelectionForm (BusinessObject forwardObj, State state)
    {
        // define local variables
        Page page;
        FormElement form;
        TableElement table;
        String[] classIds;
        GroupElement gel;

        // create page
        page = new Page ("Alternative Transition Selection Form", false);

        // include stylesheetfile
        StyleSheetElement style = new StyleSheetElement ();
        style.importSS =
            this.sess.activeLayout.path +
            this.env.getBrowser () + "/" +
            this.sess.activeLayout.elems[LayoutConstants.SHEETINFO].styleSheet;
        page.head.addElement (style);

        // create the form's header - no buttons
        form = this.createFormHeader (page,
                                 forwardObj.name,
                                 null/*forwardObj.getNavItems()*/,
                                 null,
                                 MultilingualTextProvider.getText (WorkflowTokens.TOK_BUNDLE,
                                     WorkflowTokens.ML_ALTERNATIVE_HEADER, env),
                                 null,
                                 forwardObj.icon);

        // add hidden elements:
        form.addElement (new InputElement (BOArguments.ARG_OID, InputElement.INP_HIDDEN, "" + forwardObj.oid));
        form.addElement (new InputElement (BOArguments.ARG_FUNCTION, InputElement.INP_HIDDEN, "" + AppFunctions.FCT_WORKFLOW));
        form.addElement (new InputElement (FunctionArguments.ARG_EVENT, InputElement.INP_HIDDEN, "" + WorkflowEvents.EVT_SELECTALTERNATIVE));

        // create inner table
        table = new TableElement (2);
        table.border = 0;
        table.ruletype = IOConstants.RULE_NONE;
        table.frametypes = IOConstants.FRAME_BOX;
        table.width = HtmlConstants.TAV_FULLWIDTH;
        table.cellpadding = 5;
        table.classId = CssConstants.CLASS_INFO;
        classIds = new String[2];
        classIds[0] = CssConstants.CLASS_NAME;
        classIds[1] = CssConstants.CLASS_VALUE;
        table.classIds = classIds;

//////////////////
//
// PROTOTYPED START
//

        /////////////////////////////////////////
        //
        // now create a selection box wich holds:
        // all available alternative next-states of the given state
        // (and the current user).
        //
        gel = new GroupElement ();
        SelectElement sel;

        // get all alternative transitions of given state
        Vector<String> alternatives = state.transition.getAllNextStates ();

        // any alternative transitions defined?
        if (!alternatives.isEmpty ())
        {
            // found at least one transition!

            // create selection box element for alternative states; init
            sel = new SelectElement (WorkflowArguments.ARG_ALTERNATIVESTATE, false);
            sel.size = 1;

            // build selection box with all entries of transition-list
            for (Iterator<String> iter = alternatives.iterator (); iter.hasNext ();)
            {
                // get next element:
                String name = iter.next ();

                // create the option with next entry in list:
                sel.addOption (name, name);
            } // for iter

            gel.addElement (sel);
// TOK
            this.showFormProperty (table,
                MultilingualTextProvider.getText (WorkflowTokens.TOK_BUNDLE,
                    WorkflowTokens.ML_ALTERNATIVENEXTSTATE, env), gel);
// TOK
            form.addElement (table);

            // create footer:
            this.createFormFooter (form, HtmlConstants.JREF_SHEETFORM +
                BOArguments.ARG_FUNCTION + HtmlConstants.JREF_VALUEASSIGN +
                AppFunctions.FCT_WORKFLOW + ";" + HtmlConstants.JREF_SHEETFORM +
                FunctionArguments.ARG_EVENT + HtmlConstants.JREF_VALUEASSIGN +
                WorkflowEvents.EVT_SELECTALTERNATIVE + ";",
                HtmlConstants.JREF_SHEETFORMTARGET + "'" +
                    HtmlConstants.FRM_SHEET + "';" +
                    HtmlConstants.JREF_SHEETFORM + BOArguments.ARG_FUNCTION +
                    HtmlConstants.JREF_VALUEASSIGN +
                    AppFunctions.FCT_SHOWOBJECT + "; " +
                    HtmlConstants.JREF_SHEETFORM + BOArguments.ARG_OID +
                    HtmlConstants.JREF_VALUEASSIGN + "'" + forwardObj.oid +
                    "'; " + HtmlConstants.JREF_SHEETFORMSUBMIT, null, null,
                false, false);
        } // if (!alternatives.isEmpty())
        else
        {
            // show an alert box
            ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);
            script.addScript ("alert('" + 
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_NO_ALTERNATIVES_FOUND, env) + "');");
            page.body.addElement (script);

            // show the object which should have been forwarded
            script.addScript (IOHelpers.getShowObjectJavaScript ("" + forwardObj.oid));
        } // else

        // build the page and show it to the user:
        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage ("WorkflowGUIHandler.showAlternativeSelectionForm",
                                   e.getMsg (), this.app, this.sess, this.env);
        } // catch
//
// PROTOTYPED END
//
//////////////////
    } // showAlternativeSelectionForm


    /**************************************************************************
     * Represent a form to the user where a workflow can be selected. <BR/>
     *
     * @param   forwardObj  The object to be forwarded through the workflow.
     */
    public void showWorkflowSelectionForm (BusinessObject forwardObj)
    {
        // define local variables
        Page page;
        FormElement form;
        TableElement table;
        String[] classIds;
        GroupElement gel;

        // create page
        page = new Page ("Workflow Template Selection Form", false);

        // include stylesheetfile
        StyleSheetElement style = new StyleSheetElement ();
        style.importSS =
            this.sess.activeLayout.path +
            this.env.getBrowser () + "/" +
            this.sess.activeLayout.elems[LayoutConstants.SHEETINFO].styleSheet;
        page.head.addElement (style);

        // create the form's header (no buttons)
        form = this.createFormHeader (page,
                                 forwardObj.name,
                                 null /*forwardObj.getNavItems()*/,
                                 null,
                                 MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_BUTTONSTARTWORKFLOW, env),
                                 null,
                                 forwardObj.icon);

        // add hidden elements:
        form.addElement (new InputElement (BOArguments.ARG_OID, InputElement.INP_HIDDEN, "" + forwardObj.oid));
        form.addElement (new InputElement (BOArguments.ARG_FUNCTION, InputElement.INP_HIDDEN, "" + ibs.app.AppFunctions.FCT_WORKFLOW));
        form.addElement (new InputElement (FunctionArguments.ARG_EVENT, InputElement.INP_HIDDEN, "" + WorkflowEvents.EVT_CONNECTWORKFLOW));

        // create inner table
        table = new TableElement (2);
        table.border = 0;
        table.ruletype = IOConstants.RULE_NONE;
        table.frametypes = IOConstants.FRAME_BOX;
        table.width = HtmlConstants.TAV_FULLWIDTH;
        table.cellpadding = 5;
        table.classId = CssConstants.CLASS_INFO;
        classIds = new String[2];
        classIds[0] = CssConstants.CLASS_NAME;
        classIds[1] = CssConstants.CLASS_VALUE;
        table.classIds = classIds;

        // now create a selection box wich holds:
        // all available WORKFLOW templates for the forwardObject
        // (and the current user).
        gel = forwardObj.createWorkflowTemplatesSelectionBox ("tmpl", null, false);

        // check if group element valid
        if (gel != null)
        {
            this.showFormProperty (table,    
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                    DITokens.ML_WORKFLOWTEMPLATE, env), gel);
            form.addElement (table);

            // create footer:
            this.createFormFooter (form, HtmlConstants.JREF_SHEETFORM +
                BOArguments.ARG_FUNCTION + HtmlConstants.JREF_VALUEASSIGN +
                AppFunctions.FCT_WORKFLOW + ";" + HtmlConstants.JREF_SHEETFORM +
                FunctionArguments.ARG_EVENT + HtmlConstants.JREF_VALUEASSIGN +
                WorkflowEvents.EVT_CONNECTWORKFLOW + ";",
                HtmlConstants.JREF_SHEETFORMTARGET + "'" +
                    HtmlConstants.FRM_SHEET + "';" +
                    HtmlConstants.JREF_SHEETFORM + BOArguments.ARG_FUNCTION +
                    HtmlConstants.JREF_VALUEASSIGN +
                    AppFunctions.FCT_SHOWOBJECT + "; " +
                    HtmlConstants.JREF_SHEETFORM + BOArguments.ARG_OID +
                    HtmlConstants.JREF_VALUEASSIGN + "'" + forwardObj.oid +
                    "'; " + HtmlConstants.JREF_SHEETFORMSUBMIT, null, null,
                false, false);
        } // if
        else
        {
            // not valid!
// gel.addElement (new TextElement (WorkflowMessages.MSG_NOTEMPLATESAVAILABLE));

            // show an alert box
            ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);
            script.addScript ("alert('" + 
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_NO_TEMPLATES_AVAILABLE, env) + "');");
            page.body.addElement (script);

            // show the object which should have been forwarded
            script.addScript (IOHelpers.getShowObjectJavaScript ("" + forwardObj.oid));
        } // else

        // build the page and show it to the user:
        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage ("WorkflowGUIHandler.showWorkflowSelectionForm",
                                   e.getMsg (), this.app, this.sess, this.env);
        } // catch

    } // showWorkflowSelectionForm


    /**************************************************************************
     * Generates the form for distributing objects to a set of receivers. <BR/>
     * There will be 3 main selection boxes displayed
     * <UL>
     * <LI>the group selection box
     * <LI>the users and roles selection box
     * <LI>the ad-hoc receivers selection box
     * </UL>
     * The groups selection box shows the groups the user is allowed to access.
     * By selecting a group and clicking the "apply" button, the corresponding
     * members and roles of this group will be displayed in the users selection
     * box. the user can add and remove entries from the users selection box
     * to the receivers selection box by clicking the "add" and the "remove"
     * buttons. Additionly the are 2 inputfields created to specify a filter
     * and appropriate "set filter" buttons in order to shorten the entries
     * displayed in the groups and then users selection boxes.
     *
     * @param   forwardObject       ???
     * @param   predefinedUsers     ???
     * @param   predefinedGroups    ???
     * @param   receiverOids        ???
     * @param   allowMultiple       ???
     */
    public void showAdhocUserSelectionForm (
        BusinessObject forwardObject, String predefinedUsers,
        String predefinedGroups, OID[] receiverOids, boolean allowMultiple)
    {
        String predefinedUsersLocal = predefinedUsers; // variable for local assignments
        String predefinedGroupsLocal = predefinedGroups; // variable for local assignments
        boolean allowMultipleLocal = allowMultiple; // variable for local assignments
        int i;
        String button;
        boolean newGroupSelected = false;  // indicates if a group has been newly selected
        String usersFilter = "";      // the Filterstring for the users
        String groupsFilter = "";     // the filterstring for the groups
        String group = "";            // group which is selected
        String allowMultipleString = "0";   // string mapping of allow-multiple flag


        /////////////////////////////////////////////////
        //
        // Handle user-interaction
        //

        // check if this is the 1st call
        String init = this.env.getParam (BOArguments.ARG_INITFORM);
        if (init == null || init.isEmpty ())
        {
            // creation of groupSelectionBox (only on init)
            // selects only predefined groups
            this.sess.groupBox
                = this.createGroupsSelectionBoxAdhoc (predefinedGroupsLocal, "", "");

            // create list of predefined users
            this.sess.users
                =  this.createUsersVectorAdhoc (predefinedUsersLocal, "", "");

            // create receivers
            this.sess.receivers = new Vector<String[]> ();

/*
            // initialize receiverslist
            if (receiverOids != null)
                receivers = super.createUsersSelectionBox (receiverOids);
*/

            // map allow multiple
            if (allowMultipleLocal)
            {
                allowMultipleString = "0"; // --> true
            } // if
            else
            {
                allowMultipleString = "1"; // --> false
            } // else
        } // if, 1st call
        else
        {
            // not 1st call - get environment parameters for predefined groups/users
            predefinedGroupsLocal = this.env.getParam (WorkflowArguments.ARG_PREDEFGROUPS);
            predefinedUsersLocal  = this.env.getParam (WorkflowArguments.ARG_PREDEFUSERS);
            allowMultipleString = this.env.getParam (WorkflowArguments.ARG_ALLOWMULTIPLE);
            // map to flag
            if (allowMultipleString.equals ("0"))
            {
                allowMultipleLocal = true;  // --> "0"
            } // if
            else
            {
                allowMultipleLocal = false; // --> "1"
            } // else
        } // else, not 1st call


        // check if the set users filter button has been pressed
        button = this.env.getParam (BOArguments.ARG_SETUSERSFILTER);
        if (button != null && !button.isEmpty ())
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
            this.sess.users = this.createUsersVectorAdhoc (
                predefinedUsersLocal, group, usersFilter);
        } // if
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
        if (button != null && !button.isEmpty ())
        {
            // get the group which has been selected
            group = this.env.getParam (BOArguments.ARG_GROUPS);

            // a new group has been selected
            // the selection of the options of the
            // GroupsSelectionBox has to be updatet
            newGroupSelected = true;

            // a new group has been selected, so the users Vector
            // has to be retreieved from the Database
            // and stored in the session
            this.sess.users = this.createUsersVectorAdhoc (predefinedUsersLocal,
                                                      group,
                                                      usersFilter);
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
        if (button != null && !button.isEmpty ())
        {
            groupsFilter = this.env.getParam (BOArguments.ARG_GROUPSFILTER);
            // activate the filter for groups has been pressed
            // consequentally, the groupsSelectionBox has to be
            // actuallized from the DB
            // and it is stored in the session
            this.sess.groupBox
                = this.createGroupsSelectionBoxAdhoc (predefinedGroupsLocal,
                                                      groupsFilter,
                                                      group);
        } //if
        else
        {
            groupsFilter =
                this.env.getParam (BOArguments.ARG_ACTIVEGROUPSFILTER);
        } // else

        if (groupsFilter == null)
        {
            groupsFilter = "";
        } // if


        // check if the add receiver button has been pressed
        // get the active receivers out of the application objects
        button = this.env.getParam (BOArguments.ARG_ADD);
        if (button != null && !button.isEmpty ())
        {
            String[] addReceiversArray =
                this.env.getMultipleParam (BOArguments.ARG_USERS);
            if (addReceiversArray != null)
            {
                // loop through the array of users to be added
                for (i = 0; i < addReceiversArray.length; i++)
                {
                    // check if entry is not empty
                    if (!addReceiversArray[i].isEmpty ())
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
                                    if (!allowMultipleLocal)
                                    {
                                        this.sess.receivers.removeAllElements ();
                                    } // if
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
        if (button != null && !button.isEmpty ())
        {
            String[] rmReceiversArray =
                this.env.getMultipleParam (BOArguments.ARG_RECEIVERS);

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


        /////////////////////////////////////////////////
        //
        // Build the page
        //
        // init variables
        Page page = new Page ("adhoc user selection", false);
        RowElement tr;
        TableDataElement td;
        NewLineElement nl = new NewLineElement ();
        InputElement iel;
        GroupElement gel;
        ImageElement img;
        LinkElement link;

        // add stylesheetfile
        StyleSheetElement style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () +
            "/" +
            this.sess.activeLayout.elems[LayoutConstants.SHEETINFO].styleSheet;
        page.head.addElement (style);

        // create header
        FormElement form;
        if (this.isTab ())              // object is part of upper object?
        {
            form = this.createFormHeader (page, forwardObject.name, this.getNavItems (),
                                     forwardObject.containerName,
                                     MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_FUNCTIONDISTRIBUTE, env), null,
                                     forwardObject.icon,
                                     forwardObject.containerName);
        } // if
        else                        // object exists independently
        {
            form = this.createFormHeader (page, forwardObject.name, this.getNavItems (),
                                     null, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_FUNCTIONDISTRIBUTE, env), null,
                                     forwardObject.icon, forwardObject.containerName);
        } // else

        // set hidden form parameters:
        // indicate that form has been initialized
        form.addElement (new InputElement (BOArguments.ARG_INITFORM,
            InputElement.INP_HIDDEN, "1"));
        // set common control parameters
        form.addElement (new InputElement (BOArguments.ARG_OID,
            InputElement.INP_HIDDEN, "" + forwardObject.oid));
        form.addElement (new InputElement (BOArguments.ARG_NAME,
            InputElement.INP_HIDDEN, forwardObject.name));
        form.addElement (new InputElement (BOArguments.ARG_FUNCTION,
            InputElement.INP_HIDDEN, "" + AppFunctions.FCT_WORKFLOW));
        form.addElement (new InputElement (FunctionArguments.ARG_EVENT,
                InputElement.INP_HIDDEN, "" +
                    WorkflowEvents.EVT_ADHOCSELECTIONCTRL));
        // set predefined group/user as parameter
        form.addElement (new InputElement (WorkflowArguments.ARG_PREDEFGROUPS,
            InputElement.INP_HIDDEN, predefinedGroupsLocal));
        form.addElement (new InputElement (WorkflowArguments.ARG_PREDEFUSERS,
            InputElement.INP_HIDDEN, predefinedUsersLocal));
        // set parameter that indicates if multiple receicers are allowed
        form.addElement (new InputElement (WorkflowArguments.ARG_ALLOWMULTIPLE,
            InputElement.INP_HIDDEN, allowMultipleString));
        // set filter arguments
        form.addElement (new InputElement (BOArguments.ARG_ACTIVEUSERSFILTER,
            InputElement.INP_HIDDEN, usersFilter));
        form.addElement (new InputElement (BOArguments.ARG_ACTIVEGROUPSFILTER,
            InputElement.INP_HIDDEN, groupsFilter));
        form.addElement (new InputElement (BOArguments.ARG_ACTIVEGROUP,
            InputElement.INP_HIDDEN, group));


        // construct inner table
        TableElement table =  new TableElement ();
        table.classId = CssConstants.CLASS_INFO;
        table.border = 0;
        table.ruletype = IOConstants.RULE_NONE;
        table.frametypes = IOConstants.FRAME_BOX;
        table.width = HtmlConstants.TAV_FULLWIDTH;
        table.cellpadding = 5;


        // add groups filter and selection box
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
        // the selected option has to be altered if
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
        iel = new InputElement (BOArguments.ARG_SETGROUPS, InputElement.INP_HIDDEN, "");
        gel.addElement (iel);
        img = new ImageElement (this.sess.activeLayout.path + BOPathConstants.PATH_ARROWICONS + "arrow_right.jpg");
        link = new LinkElement (img, IOConstants.URL_JAVASCRIPT +
            HtmlConstants.JREF_SHEETFORM + BOArguments.ARG_SETGROUPS +
            HtmlConstants.JREF_VALUEASSIGN + "'1';" +
            HtmlConstants.JREF_SHEETFORMSUBMIT);
        gel.addElement (link);
        td = new TableDataElement (gel);
        tr.addElement (td);


        // add users filter/selection box
        gel = new GroupElement ();
        span = new SpanElement ();
        span.classId = CssConstants.CLASS_NAME;
        span.addElement (new TextElement (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_USERS, env)));
        gel.addElement (span);
        gel.addElement (nl);
        iel = new InputElement (BOArguments.ARG_USERSFILTER, InputElement.INP_TEXT, usersFilter);
        iel.size = 10;
        iel.maxlength = 10;
        gel.addElement (iel);
        gel.addElement (nl);
        iel = new InputElement (BOArguments.ARG_SETUSERSFILTER, InputElement.INP_SUBMIT, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SETFILTER, env));
        gel.addElement (iel);
        gel.addElement (nl);
        // generate the users selection box
        gel.addElement (this.createSelectionBox (BOArguments.ARG_USERS, this.sess.users, this.sess.receivers));
        td = new TableDataElement (gel);
        tr.addElement (td);


        // add buttons to add or remove users from the receiver list
        gel = new GroupElement ();
        gel.addElement (nl);
        gel.addElement (nl);
        gel.addElement (nl);
        gel.addElement (nl);
        gel.addElement (nl);
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
        img = new ImageElement (this.sess.activeLayout.path +
            BOPathConstants.PATH_ARROWICONS + "arrow_left.jpg");
        link = new LinkElement (img, IOConstants.URL_JAVASCRIPT +
            HtmlConstants.JREF_SHEETFORM + BOArguments.ARG_REMOVE +
            HtmlConstants.JREF_VALUEASSIGN + "'1';" +
            HtmlConstants.JREF_SHEETFORMSUBMIT);
        gel.addElement (link);
        td = new TableDataElement (gel);
        tr.addElement (td);


        // add reviecer selection box
        gel = new GroupElement ();
        span = new SpanElement ();
        span.classId = CssConstants.CLASS_NAME;
        span.addElement (new TextElement (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_RECEIVERS, env)));
        gel.addElement (span);
        gel.addElement (nl);
        // generate the ReceiverSelectionBox
        // out of the Vector in the session
        gel.addElement (this.createSelectionBox (BOArguments.ARG_RECEIVERS, this.sess.receivers));
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
        form.addElement (table);

        // add footer
        this.createFormFooter (form, "this.form." + BOArguments.ARG_FUNCTION +
                                ".value = '" + AppFunctions.FCT_WORKFLOW + "';" +
                                "this.form." + FunctionArguments.ARG_EVENT +
                                ".value = '" + WorkflowEvents.EVT_ADHOCSELECTIONOK + "';");

        // build the page and show it to the user:
        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage ("WorkflowGUIHandler.showAdhocUserSelectionForm",
                                   e.getMsg (), this.app, this.sess, this.env);
        } // catch

    } // showAdhocUserSelectionForm


    /***************************************************************************
     * Generates a Selection Box with given predefind Groups (in a comma-
     * separated list). <BR/>
     *
     * @param   predefinedGroups    which groups shall be selectable
     *                              (comma-separated list)
     * @param   groupsFilter        filter for the query
     * @param   activeGroupOID      OID of actually set group to be marked in
     *                              the selection box
     *
     * @return  SelectElement that holds all groups.
     */
    private SelectElement createGroupsSelectionBoxAdhoc (
                                                         String predefinedGroups,
                                                         String groupsFilter,
                                                         String activeGroupOID)
    {
        // local variables
        SQLAction action = null;
        SelectElement sel;
        int rowCount;
        String activeGroupString = "";
        activeGroupString = activeGroupOID;
        sel = new SelectElement (BOArguments.ARG_GROUPS, false);
        sel.size = 10;

        // create the SQL String to select all tuples
        String queryStr =
            "SELECT DISTINCT id AS oid, name AS name" +
            " FROM  ibs_Group " +
            " WHERE domainId = " + this.getUser ().domain;

        // create & add SQL-format of given comma-separated group names
        // e.g.: "Jeder;Administratoren" ->
        //       " AND (UPPER(v.name) = UPPER('Jeder') OR
        //              UPPER(v.name) = UPPER('Administratoren')) "
        StringTokenizer st = new StringTokenizer (predefinedGroups, ";");
        String token;
        String addToQuery = "";
        while (st.hasMoreTokens ())
        {
            // get nex token
            token = st.nextToken ();

            // create SQL-code for token
            addToQuery += " UPPER(name) = UPPER('" + token + "') ";

            // add 'OR' only if more tokens
            if (st.hasMoreTokens ())
            {
                addToQuery += "OR";
            } // if
        } // while
        if (!addToQuery.isEmpty ())     // add if any tokens
        {
            queryStr += " AND (" + addToQuery + ") ";
        } // if

        // add filter restriction
        if (groupsFilter != null && !groupsFilter.isEmpty ())
        {
            queryStr +=
                " AND " +
                SQLHelpers.getQueryConditionString (
                    "name", SQLConstants.MATCH_SUBSTRING, groupsFilter, false);
        } //if

        // add ordering
        queryStr = queryStr + " ORDER BY name ";

        this.debug ("Query: " + queryStr);

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();

            // execute the query
            rowCount = action.execute (queryStr, false);

            // empty resultset?
            if (rowCount == 0)
            {
                return sel;
            } // if
            // error while executing?
            else if (rowCount < 0)
            {
                // ERROR!
                return sel;
            } // else if
            // everything ok - go on

            // get tuples out of db
            while (!action.getEOF ())
            {
                // create entries in list
                String oid = action.getString ("oid");
                if (oid.equalsIgnoreCase (activeGroupString))
                {
                    sel.addOption (action.getString ("Name"),
                        action.getString ("oid"), true);
                } // if
                else
                {
                    sel.addOption (action.getString ("Name"),
                        action.getString ("oid"));
                } //else
                // step one tuple ahead for the next loop
                action.next ();
            } // while

            // the last tuple has been processed
            // end transaction
            action.end ();
        } // try
        catch (DBError dbErr)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage ("WorkflowGUIHandler.createGroupsSelectionBoxAdhoc",
                                   dbErr, this.app, this.sess, this.env, true);
        } // catch
        finally
        {
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        return sel;
    } // createGroupsSelectionBoxAdhoc


    /***************************************************************************
     * Generates users of given group + predefinedUsers. <BR/>
     *
     * @param   predefinedUsers     which users are preselted
     *                              (comma-separated list)
     * @param   groupId             OID of a group
     * @param   usersFilter         Filterstring for the query
     *
     * @return  vector of users (oid/name; packed in string array)
     */
    private Vector<String[]> createUsersVectorAdhoc (String predefinedUsers,
                                                     String groupId,
                                                     String usersFilter)
    {
        String groupIdLocal = groupId;  // variable for local assignments
        Vector<String[]> result = new Vector<String[]> ();
        SQLAction action = null;
        int rowCount;
        String queryStr;

        // if groupId not set: intialize (no user/group-info will be selected)
        if (groupIdLocal != null && groupIdLocal.isEmpty ())
        {
            groupIdLocal = "0";
        } // if

        // create the SQL String to select all tuples
        // (attention: there are no right checks done)
        queryStr =
            "SELECT DISTINCT u.oid as oid, u.fullname as name, u.id as id" +
            " FROM  ibs_user u, ibs_groupUser gu" +
            " WHERE u.domainId = " + this.getUser ().domain +
            " AND   ((gu.groupId = " + groupIdLocal +
            "         AND   gu.userId = u.id) ";

        // predefined users:
        // create & add SQL-format of given comma-separated user names
        // e.g.: "Horst;Sepp" ->
        //       " (UPPER(u.username) = UPPER('Horst') OR
        //          UPPER(u.username) = UPPER('Sepp') "
        StringTokenizer st = new StringTokenizer (predefinedUsers, ";");
        String token;
        String addToQuery = "";
        while (st.hasMoreTokens ())
        {
            // get nex token
            token = st.nextToken ();

            // create SQL-code for token
            addToQuery += " UPPER(u.name) = UPPER('" + token + "') ";

            // add 'OR' only if last token
            if (st.hasMoreTokens ())
            {
                addToQuery += "OR";
            } // if
        } // while

        // add if any tokens
        if (!addToQuery.isEmpty ())
        {
            queryStr += " OR (" + addToQuery + ")) ";
        } // if
        else
        {
            queryStr += ") ";
        } // else


        // check if a name filter has been set
        if (usersFilter != null && !usersFilter.isEmpty ())
        {
            queryStr +=
                " AND " +
                SQLHelpers.getQueryConditionString (
                    "u.fullname", SQLConstants.MATCH_SUBSTRING, usersFilter, false);
        } // if
        queryStr = queryStr + " ORDER BY name ";

        this.debug ("Query: " + queryStr);

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();

            // execute
            rowCount = action.execute (queryStr, false);

            // empty resultset?
            if (rowCount == 0)
            {
                return result;
            } //if
            // error while executing?
            else if (rowCount < 0)
            {
                return result;
            } //else if
            // everything ok - go on

            // get tuples out of db
            while (!action.getEOF ())
            {
                // create entries in list
                String[] t = new String [3];
                t[0] = action.getString ("Name");
                t[1] = action.getString ("oid");
                t[2] = action.getString ("id");
                result.addElement (t);
                // step one tuple ahead for the next loop
                action.next ();
            } // while

            // the last tuple has been processed
            // end transaction
            action.end ();
        } // try
        catch (DBError dbErr)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage ("WorkflowGUIHandler.createUsersVectorAdhoc",
                                   dbErr, this.app, this.sess, this.env, true);
        } // catch
        finally
        {
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        return result;
    } // createUsersVectorAdhoc

} // WorkflowFunction
