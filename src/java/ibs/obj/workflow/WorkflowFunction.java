/*
 * Class: WorkflowFunction.java
 */

// package:
package ibs.obj.workflow;

// imports:
import ibs.bo.BOArguments;
import ibs.bo.BOHelpers;
import ibs.bo.BusinessObject;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.bo.ObjectInitializeException;
import ibs.di.DIArguments;
import ibs.di.DIHelpers;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.func.IbsFunction;
import ibs.obj.workflow.WorkflowGUIHandler;
import ibs.obj.workflow.WorkflowService;
import ibs.obj.workflow.WorkflowTemplate_01;
import ibs.obj.workflow.Workflow_01;
import ibs.service.workflow.UserInteractionRequiredException;
import ibs.service.workflow.WorkflowArguments;
import ibs.service.workflow.WorkflowEvents;
import ibs.service.workflow.WorkflowMessages;


/******************************************************************************
 * Implementation of function interface for all workflow-functions to be
 * performed within the framework. <BR/>
 * This class should ensure, that all objects, data etc. regarding to
 * one function, are encapsulated together in one topclass. <BR/>
 *
 * In first Version, the function extends BusinessObject, to be able to
 * use the cachingalgorithm of Application - should be an abstract class
 * in final implementation.
 *
 * @version     $Id: WorkflowFunction.java,v 1.21 2010/04/07 13:37:10 rburgermann Exp $
 *
 * @author      Horst Pichler (HP), 010201
 ******************************************************************************
 */
public class WorkflowFunction extends IbsFunction
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: WorkflowFunction.java,v 1.21 2010/04/07 13:37:10 rburgermann Exp $";


    /**
     * Object to perform workflow-operations. <BR/>
     */
    WorkflowService workflowService = null;


    /**************************************************************************
     * This constructor creates a new instance of the class IbsFunction. <BR/>
     */
    public WorkflowFunction ()
    {
        // nothing to do
    } // WorkflowFunction


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // nothing to do
    } // initClassSpecifics


    /**************************************************************************
     * Instantiates a new object of type WorkflowService. <BR/>
     *
     * @return  the object of type WorkflowService
     *          null if no success
     */
    public WorkflowService createWorkflowService ()
    {
        // create/init new workflow-service object
        WorkflowService workflowService = null;
        try
        {
            workflowService
                = new WorkflowService (this.user, this.env, this.sess, this.app);
        } // try
        catch (ObjectInitializeException e)
        {
            workflowService = null;
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch

        // exit method
        return workflowService;
    } // createWorkflowService


    /**************************************************************************
     * Instantiates a new object of type WorkflowGUIHandler. <BR/>
     *
     * @return  the object of type WorkflowService
     *          null if no success
     */
    public WorkflowGUIHandler createWorkflowGUIHandler ()
    {
        // create/init new workflow-service object
        WorkflowGUIHandler workflowGUIHandler;
        workflowGUIHandler
            = new WorkflowGUIHandler (this.user, this.env, this.sess, this.app);

        // exit method
        return workflowGUIHandler;
    } // WorkflowGUIHandler


    ///////////////// control flow
    //
    //

    /**************************************************************************
     * mainmethod = sequence control of this function. Read environment
     * parameters and call function-handler. <BR/>
     */
    public void handleEvent ()
    {
        // init variables
        OID forwardObjOid = null;
        OID templateObjOid = null;
        String templateName = null;
        String str;

        // get current event
        int event = this.getEvent ();

        // get oid of forward object out of parameters
        forwardObjOid = this.env.getOidParam (BOArguments.ARG_OID);

        // get oid of workflow-template, but only if event 'EVT_CONNECTWORKFLOW'
        // is given
        if (event == WorkflowEvents.EVT_CONNECTWORKFLOW)
        {
            // get templateOid out of parameters
            if ((str = this.env.getStringParam (DIArguments.ARG_TEMPLATE)) != null)
            {
                try
                {
                    // extract the oid and the name out of the
                    // <oid>/<filename> pair (selectionbox: users-choice)
                    String [] tokens = DIHelpers.getTokens (str, "/");
                    templateObjOid = new OID (tokens[0]);
                } // try
                catch (IncorrectOidException e)
                {
                    templateObjOid = null;
                } // catch
            } // if
        } // if

        // get name of workflow-template, but only if event 'EVT_STARTNAMEDTWORKFLOW'
        // is given
        if (event == WorkflowEvents.EVT_STARTNAMEDWORKFLOW)
        {
            // get templateOid out of parameters
            templateName = this.env.getStringParam ("evtarg");
        } // if

        // 1st handle special event: user made changes in the
        // ad-hoc-selection dialog (not SUBMIT!!)
        if (event == WorkflowEvents.EVT_ADHOCSELECTIONCTRL)
        {
            // show the selection form
            this.showAdhocUserSelectionForm (forwardObjOid);
            // exit method
            return;
        } // if

        // execute workflow-operation (with parameters from environment)
        this.executeEvent (forwardObjOid, null, templateObjOid,
                                 null, templateName, event);

    } // handleEvent


    /**************************************************************************
     * Executes the given workflow-event for the given object. <BR/> This
     * execution includes generation of output and alert-boxes, according to
     * the wf-definition.
     *
     * @param   forwardObjOid   oid of the object to forward; should be null
     *                          if parameter 'forwardObj' is set
     * @param   forwardObj      the object to forward; should be null if
     *                          parameter 'forwardObjOid' is set
     * @param   templateObjOid  oid of the workflow-template; should be null
     *                          if parameter 'templateObj' is set
     * @param   templateObj     the workflow-template; should be null if
     *                          parameter 'templateObjOid' is set
     * @param   event           the workflow-event that shall be executed
     *
     * @return  <CODE>false</CODE>  indicates interruption of control-flow due to
     *                 errors or necessary user-interactions
     *          <CODE>true</CODE>   no interruption of control-flow
     */
    public boolean executeEvent (OID forwardObjOid,
                              BusinessObject forwardObj,
                              OID templateObjOid,
                              WorkflowTemplate_01 templateObj,
                              int event)
    {
        return this.executeEvent (forwardObjOid, forwardObj, templateObjOid,
            templateObj, null, event);
    } // executeEvent


    /**************************************************************************
     * Executes the given workflow-event for the given object. <BR/> This
     * execution includes generation of output and alert-boxes, according to
     * the wf-definition.
     *
     * @param   forwardObjOid   oid of the object to forward; should be null
     *                          if parameter 'forwardObj' is set
     * @param   forwardObj      the object to forward; should be null if
     *                          parameter 'forwardObjOid' is set
     * @param   templateObjOid  oid of the workflow-template; should be null
     *                          if parameter 'templateObj' is set
     * @param   templateObj     the workflow-template; should be null if
     *                          parameter 'templateObjOid' is set
     * @param   templateName    the workflow-templates name; should be null if
     *                          parameter 'templateObjOid' or
     *                          parameter 'templateObj' is set
     * @param   event           the workflow-event that shall be executed
     *
     * @return  <CODE>false</CODE>  indicates interruption of control-flow due to
     *                 errors or necessary user-interactions
     *          <CODE>true</CODE>   no interruption of control-flow
     */
    public boolean executeEvent (OID forwardObjOid,
                              BusinessObject forwardObj,
                              OID templateObjOid,
                              WorkflowTemplate_01 templateObj,
                              String templateName,
                              int event)
    {
        OID forwardObjOidLocal = forwardObjOid; // variable for local assignments
        boolean success = false;

        // indicates if forward-object itself or only its oid is given
        boolean objectGiven = false;

        // check if the forward-object itself is given:
        if (forwardObj != null)
        {
            forwardObjOidLocal = forwardObj.oid;
            objectGiven = true;
        } // if

        // indicates if workflow-template itself or only its oid is given
        boolean templateGiven = false;
        // check if the template itself is given
        if (templateObj != null)
        {
            templateGiven = true;
        } // if

        // create object for workflow-operations
        this.workflowService = this.createWorkflowService ();
        if (this.workflowService == null)
        {
            // CRITICAL: show error message and EXIT
            IOHelpers.showMessage (
                "Creation of WorkflowService failed",
                this.app, this.sess, this.env);
            return false;
        } // if

        // create GUI handler for outpt
        WorkflowGUIHandler workflowGUIHandler
            = this.createWorkflowGUIHandler ();
        // ceck success
        if (workflowGUIHandler == null)
        {
            // CRITICAL: show error message and EXIT
            IOHelpers.showMessage (
                "Creation of WorkflowGUIHandler failed",
                this.app, this.sess, this.env);
            return false;
        } // if

        // show header ... please wait ... message
        //IBS-91: This has been commented out for the reason that no logging info should be displayed to the user
        //        if the displayLog flag within the workflow definition is set to 'NO'. Therefore this messages
        //        has been moved to WorkflowService.initializeWorkflowInstance() where it is displayed via the
        //        workflow log, where it is displayed only if the flag is set to 'YES'.
//        workflowGUIHandler.showHeaderMessage();

        // encapsulation of switch statement in try-catch-block
        // all of the following events can be interrupted by
        // a UserInteractionRequiredException.
        try
        {
            // check which event was thrown
            switch (event)
            {

                // event fired by 'Workflow Starten' button
                case WorkflowEvents.EVT_STARTWORKFLOW:  // start workflow on an object
                    // start worfklow (mode depends: if object or oid is given)
                    if (objectGiven)
                    {
                        success = this.workflowService.start (forwardObj);
                    } // if
                    else
                    {
                        success = this.workflowService.start (forwardObjOidLocal);
                    } // else
                    break;

                // event fired by EVT_STARTNAMEDWORKFLOW call
                case WorkflowEvents.EVT_STARTNAMEDWORKFLOW:  // start workflow with given
                                                             // name on given object
                    // start worfklow (mode depends: if object or oid is given)
                    if (objectGiven)
                    {
                        success = this.workflowService.start (
                            forwardObj, templateName);
                    } // if
                    else
                    {
                        success = this.workflowService.start (
                            forwardObjOidLocal, templateName);
                    } // else
                    break;

                // event fired by OK-button in workflow-selection dialog
                case WorkflowEvents.EVT_CONNECTWORKFLOW: // connect and forward an object
                    // start workflow with given object and template
                    // (mode depends: if object or oid is given)
                    if (objectGiven && templateGiven)
                    {
                        success = this.workflowService.start (forwardObj, templateObj);
                    } // if
                    else if (objectGiven && !templateGiven)
                    {
                        success = this.workflowService.start (forwardObj, templateObjOid);
                    } // else if
                    else if (!objectGiven && !templateGiven)
                    {
                        success = this.workflowService.start (forwardObjOidLocal, templateObjOid);
                    } // else if
                    else
                    {
                        IOHelpers.showMessage (
                            "Error in WorkflowFunction: start(OID, obj) not possible!",
                            this.app, this.sess, this.env);
                    } // else
                    break;

                // event fired by the 'Workflow Weiterleiten' button
                case WorkflowEvents.EVT_FORWARD:
                    // forward object
                    // (mode depends: if object or oid is given)
                    if (objectGiven)
                    {
                        success = this.workflowService.forward (forwardObj);
                    } // if
                    else
                    {
                        success = this.workflowService.forward (forwardObjOidLocal);
                    } // else
                    break;

                // event fired by the ok button of the AD-HOC selection dialog
                case WorkflowEvents.EVT_ADHOCSELECTIONOK:
                {
                    // error indicator
                    boolean selectionError = false;
                    // check if receiver has been selected!
                    if (this.sess.receivers == null &&
                        this.sess.receivers.size () < 1)
                    {
                        // no receiver selection found
                        selectionError = true;
                    } // if

                    // if error in selection --> select again
                    if (selectionError)
                    {
                        // show error message
                        IOHelpers.showMessage (
                            MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                                WorkflowMessages.ML_MSG_NOADHOCRECEIVER, env),
                            this.app, this.sess, this.env);
                        // show the selection form
                        this.showAdhocUserSelectionForm (forwardObjOidLocal);
                        // exit method
                        return false;
                    } // if

                    // indicate that adhoc-receiver has been selected
                    // (stored in session) - now continue forwarding
                    this.workflowService.adhocReceiverSelected = true;

                    // continue workflows control-flow
                    // (mode depends: if object or oid is given)
                    if (objectGiven)
                    {
                        success = this.workflowService.forward (forwardObj);
                    } // if
                    else
                    {
                        success = this.workflowService.forward (forwardObjOidLocal);
                    } // else
                    break;
                } // EVT_ADHOCSELECTIONOK


                // event fired by the ok button of the alternative selection dialog
                case WorkflowEvents.EVT_SELECTALTERNATIVE:
                {
                    // get selected alternative from environment
                    String nextStateName = this.env
                        .getParam (WorkflowArguments.ARG_ALTERNATIVESTATE);
                    // indicate that alternative has been selected
                    this.workflowService.alternativeNexStateSelected = true;
                    this.workflowService.alternativeNextStateName = nextStateName;

                    // continue control-flow
                    // (mode depends: if object or oid is given)
                    if (objectGiven)
                    {
                        success = this.workflowService.forward (forwardObj);
                    } // if
                    else
                    {
                        success = this.workflowService.forward (forwardObjOidLocal);
                    } // else
                    break;
                } // EVT_SELECTALTERNATIVE


                // event fired by 'Workflow Abbrechen' button
                case WorkflowEvents.EVT_ABORTWORKFLOW:  // abort workflow on an object
//
// REMARK: Due to bad implementation in this case the forwardObj is the instance object
//         (the workflow-instance Workflow_01)
//
// TODO: change header of method (incl. instance object)
//
                    // abort
                    // (mode depends: if object or oid is given)
                    if (objectGiven)
                    {
                        success = this.workflowService.abort2 ((Workflow_01) forwardObj);
                    } // if
                    else
                    {
                        success = this.workflowService.abort2 (forwardObjOidLocal);
                    } // else
                    break;

                // event fired by 'Workflow Abschliessen' button
                case WorkflowEvents.EVT_FINISHWORKFLOW:
                    // finish
                    // (mode depends: if object or oid is given)
                    if (objectGiven)
                    {
                        success = this.workflowService.finish (forwardObj);
                    } // if
                    else
                    {
                        success = this.workflowService.finish (forwardObjOidLocal);
                    } // else
                    break;

                default:
                    IOHelpers.showMessage (
                        "ERROR: Event " + event +
                        " is not valid for this workflow-function",
                        this.app, this.sess, this.env);
            } // switch
        } // try
        catch (UserInteractionRequiredException e)
        {
            // user interaction necessary - interruption of workflows control-flow
            this.handleUserInteraction (e);

            // exit method
            return false;
        } // catch

        // check success - show footer with/out confirm-message
        if (success && this.workflowService.showConfirmActive ())
        {
            workflowGUIHandler.showSuccessConfirmation ();
//            workflowGUIHandler.goBackToContainer ();
            this.goToAfterTransition (workflowGUIHandler, forwardObjOidLocal);
        } // if
        else if (!success && this.workflowService.showConfirmActive ())
        {
            workflowGUIHandler.showNoSuccessConfirmation ();
//            workflowGUIHandler.goBackToContainer();
            this.goToAfterTransition (workflowGUIHandler, forwardObjOidLocal);
        } // else if
        else
        {
//            workflowGUIHandler.goBackToContainer();
            this.goToAfterTransition (workflowGUIHandler, forwardObjOidLocal);
        } // else

        // exit:
        return true;
    } // executeEvent


    /**************************************************************************
     * . <BR/>
     *
     * @param workflowGUIHandler    a workflowGUIHandler instance
     * @param forwardObjOid         the oid of the object that has been forwarded
     *
     */
    private void goToAfterTransition (WorkflowGUIHandler workflowGUIHandler, OID forwardObjOid)
    {
        OID destObjectOid = null;
        String path;
        BusinessObject destObj;

        // get the path
        path = this.workflowService.wfdefinition.pathAfterTransition;
//showMessage ("path: " + path);
        // check if a path has been set
        if (path != null && !path.isEmpty ())
        {
            // resolve the path
            destObjectOid = BOHelpers.resolveObjectPath (path, forwardObjOid,
                this, this.app, this.sess, this.env);
//showMessage ("destObjectOid: " + destObjectOid);
            // check if we got a valid oid)
            if (destObjectOid != null)
            {
                // display the object
                // BB TODO: using the java script will overwrite all messages
                // that have been printed during workflow process!
//                processJavaScriptCode (IOHelpers.getShowObjectJavaScript ("" + forwardObjOid));
                // get the object with the given oid:
                destObj = this.workflowService.objectHandler.fetchObject (
                    destObjectOid, this.user);
                // check if we got a valid instance
                if (destObj != null)
                {
                    destObj.show (0);
                } // if (destObj != null)
                else                    // could not fetch the object
                {
                    // show error message:
                    this.showPopupMessage ("Object with oid '" + forwardObjOid +
                        "' does not exist!");
                    // show an error message
                    workflowGUIHandler.goBackToContainer ();
                } // else could not fetch the object
            } // if (destObjectOid != null)
            else    // could not resolve path
            {
                // show error message
                this.showPopupMessage ("Invalid path: " + path);
                // show an error message
                workflowGUIHandler.goBackToContainer ();
            } // could not resolve path
        } // if (path != null && (! path.equals ("")))
        else                            // no after transition path set
        {
            workflowGUIHandler.goBackToContainer ();
        } // else no after transition path set
    } // goToAfterTransition


    /**************************************************************************
     * Represent a form to the user where a workflow can be selected. <BR/>
     * Method can be called from outside. <BR/>
     *
     * @param   e   the exception object
     */
    public void handleWorkflowUserInteraction (
                                               UserInteractionRequiredException e)
    {
        // create object for workflow-operations
        this.workflowService = this.createWorkflowService ();
        if (this.workflowService == null)
        {
            // CRITICAL: show error message and EXIT
            IOHelpers.showMessage (
                "Creation of WorkflowService failed",
                this.app, this.sess, this.env);
            return;
        } // if

        // call internal
        this.handleUserInteraction (e);

    } // handleUserInteraction


    /**************************************************************************
     * Represent a form to the user where a workflow can be selected. <BR/>
     *
     * @param   forwardObjOid   workflow forwarded object
     */
    private void showAdhocUserSelectionForm (OID forwardObjOid)
    {
        // create object for workflow-operations
        if (this.workflowService == null)
        {
            this.workflowService = this.createWorkflowService ();
            if (this.workflowService == null)
            {
                // CRITICAL: show error message and EXIT
                IOHelpers.showMessage (
                    "Creation of WorkflowService failed",
                    this.app, this.sess, this.env);
                return;
            } // if
        } // if

        // create GUI handler for output
        WorkflowGUIHandler workflowGUIHandler
            = this.createWorkflowGUIHandler ();
        // ceck success
        if (workflowGUIHandler == null)
        {
            // CRITICAL: show error message and EXIT
            IOHelpers.showMessage (
                "Creation of WorkflowGUIHandler failed",
                this.app, this.sess, this.env);
            return;
        } // if

        // get forwarding object
        BusinessObject forwardObj = this.workflowService.getObjectHandler ()
            .fetchObject (forwardObjOid, this.user);
        // check success
        if (forwardObj == null)
        {
            IOHelpers.showMessage (
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_FORWARDOBJECT_NOTFOUND, env),
                this.app, this.sess, this.env);
        } // if
        else
        {
            // control request from selection adhoc-dialog
            // causes: view dialog again with changed entries
            workflowGUIHandler.showAdhocUserSelectionForm (
                                                 forwardObj,
                                                 "",
                                                 "",
                                                 null,
                                                 false);
        } // else

        // exit method
        return;
    } // showAdhocUserSelectionForm

    /**************************************************************************
     * Handle exception for user interaction. <BR/>
     *
     * @param   e       The exception to be handled.
     */
    private void handleUserInteraction (UserInteractionRequiredException e)
    {
        // create GUI handler for outpt
        WorkflowGUIHandler workflowGUIHandler
            = this.createWorkflowGUIHandler ();
        // ceck success
        if (workflowGUIHandler == null)
        {
            // CRITICAL: show error message and EXIT
            IOHelpers.showMessage (
                "Creation of WorkflowGUIHandler failed",
                this.app, this.sess, this.env);
            return;
        } // if

        // differ between different types of internal states;
        // for each internal state a different user-interaction
        // must be called
        if (this.workflowService.showWorkflowAlternativeForm)
        {
            // call the form:
            // - with selectable next-states
            workflowGUIHandler.showAlternativeSelectionForm (
                e.forwardObj, e.state);
        } // if
        else if (this.workflowService.showWorkflowAdhocForm)
        {
            // call the form:
            // - with selectable groups and users in comma-separated lists
            // - multiple receiver selection is NOT allowed
            workflowGUIHandler.showAdhocUserSelectionForm (
                                             e.forwardObj,
                                             e.state.adhocUsers,
                                             e.state.adhocGroups,
                                             null, false);
        } // else if
        else if (this.workflowService.showWorkflowTemplateDialog)
        {
            // show dialog to select a workflow template
            workflowGUIHandler.showWorkflowSelectionForm (e.forwardObj);
        } // else if
    } // handleUserInteraction

} // WorkflowFunction
