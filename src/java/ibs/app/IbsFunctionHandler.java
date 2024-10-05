/*
 * Class: IbsFunctionHandler.java
 */

// package:
package ibs.app;

// imports:
import ibs.app.func.FunctionValues;
import ibs.app.func.GeneralFunctionHandler;
import ibs.app.func.IFunctionHandler;
import ibs.bo.BOArguments;
import ibs.bo.BOConstants;
import ibs.bo.BOHelpers;
import ibs.bo.BOListConstants;
import ibs.bo.BOMessages;
import ibs.bo.BOPathConstants;
import ibs.bo.BOTokens;
import ibs.bo.BusinessObject;
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.ContainerElement;
import ibs.bo.IncorrectOidException;
import ibs.bo.NoMoreElementsException;
import ibs.bo.NotifyFunction;
import ibs.bo.OID;
import ibs.bo.ObjectInitializeException;
import ibs.bo.ObjectNotFoundException;
import ibs.bo.Operations;
import ibs.bo.States;
import ibs.bo.ref.CheckReferenceContainer;
import ibs.bo.tab.Tab;
import ibs.bo.tab.TabConstants;
import ibs.bo.tab.TabContainerLoader;
import ibs.bo.type.Type;
import ibs.bo.type.TypeConstants;
import ibs.di.DIArguments;
import ibs.di.DIConstants;
import ibs.di.Response;
import ibs.di.m2XMLResponse;
import ibs.di.exp.ExportIntegrator;
import ibs.di.imp.ImportIntegrator;
import ibs.di.service.ObjectTransformationException;
import ibs.di.service.ObjectTransformer;
import ibs.install.ApplicationInstaller;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.SsiFileNotFoundException;
import ibs.io.Ssl;
import ibs.io.servlet.ApplicationInitializationException;
import ibs.io.session.SessionInfo;
import ibs.ml.MultilangConstants;
import ibs.ml.MultilingualTextInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.doc.Attachment_01;
import ibs.obj.incl.Include_01;
import ibs.obj.layout.LayoutContainerElement_01;
import ibs.obj.layout.LayoutContainer_01;
import ibs.obj.layout.NoLayoutDefinedException;
import ibs.obj.layout.NoLayoutFoundException;
import ibs.obj.log.LogContainer_01;
import ibs.obj.menu.MenuContainer_01;
import ibs.obj.menu.MenuData_01;
import ibs.obj.menu.Menu_01;
import ibs.obj.search.FunctionSearch;
import ibs.obj.search.SearchEvents;
import ibs.obj.user.Group_01;
import ibs.obj.user.Rights_01;
import ibs.obj.user.UserProfile_01;
import ibs.obj.user.UserTokens;
import ibs.obj.user.User_01;
import ibs.obj.workflow.WorkflowFunction;
import ibs.obj.workflow.WorkflowService;
import ibs.obj.wsp.Inbox_01;
import ibs.obj.wsp.NewsContainer_01;
import ibs.obj.wsp.WasteBasket_01;
import ibs.obj.wsp.Workspace_01;
import ibs.service.conf.Configuration;
import ibs.service.conf.ConfigurationException;
import ibs.service.conf.ServerRecord;
import ibs.service.observer.M2ObserverFunctionHandler;
import ibs.service.user.ExtendedUserData;
import ibs.service.user.User;
import ibs.service.workflow.UserInteractionRequiredException;
import ibs.service.workflow.WorkflowEvents;
import ibs.service.workflow.WorkflowMessages;
import ibs.tech.html.BuildException;
import ibs.tech.html.ButtonBarElement;
import ibs.tech.html.ButtonElement;
import ibs.tech.html.Font;
import ibs.tech.html.IE302;
import ibs.tech.html.Page;
import ibs.tech.html.ParagraphElement;
import ibs.tech.html.PreElement;
import ibs.tech.html.ScriptElement;
import ibs.tech.html.TextElement;
import ibs.tech.http.HttpArguments;
import ibs.tech.http.HttpConstants;
import ibs.util.AlreadyDeletedException;
import ibs.util.DateTimeHelpers;
import ibs.util.NoAccessException;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;
import ibs.util.WeblinkInfo;
import ibs.util.file.FileHelpers;
import ibs.util.list.IElementId;
import ibs.util.list.ListException;

import java.io.File;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;


/******************************************************************************
 * IbsFunctionHandler object which is created with each call of a page. <BR/>
 * An object of this class represents the interface between the network and the
 * business logic itself. <BR/>
 * It gets arguments from the user, controls the program flow, and sends data
 * back to the user and his browser. <BR/>
 * There has to be generated an extension class of this class to realize the
 * functions which are specific to the required application.
 *
 * @version     $Id: IbsFunctionHandler.java,v 1.245 2011/12/20 15:06:59 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR), 980214
 ******************************************************************************
 */
public class IbsFunctionHandler extends GeneralFunctionHandler
    implements IFunctionHandler
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: IbsFunctionHandler.java,v 1.245 2011/12/20 15:06:59 rburgermann Exp $";

    /**
     * The application object itself. <BR/>
     */
//    private ApplicationInfo p_app = null;

    /**
     * The actual representation form. <BR/>
     */
//    protected int p_representationForm = UtilConstants.REP_STANDARD;



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a IbsFunctionHandler object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   id      Id of the element.
     * @param   name    The element's name.
     */
    public IbsFunctionHandler (IElementId id, String name)
    {
        // call constructor of super class:
        super (id, name);

        // initialize the other instance properties:
    } // IbsFunctionHandler


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Evaluate the function to be performed. <BR/>
     *
     * @param   function    The function to be performed.
     * @param   values      The values for the function handler.
     *
     * @return  Function to be performed. <BR/>
     *          <CODE>AppFunctions.FCT_NOFUNCTION</CODE> if there is no function
     *          or the function was already performed.
     */
    public int evalFunction (int function, FunctionValues values)
    {
        int num;                        // help variable
        OID oid = values.p_oid;         // the actual oid

        if (values.p_sess.wizardRegistration) // loginWizard activated?
        {
            this.loginWizard (values.p_function, values);
            return AppFunctions.FCT_NOFUNCTION;
        } // if loginWizard activated

        UserInfo userInfo = values.getUserInfo ();

        try
        {
            switch (function)           // perform function
            {
                case AppFunctions.FCT_READCONFIGFILE:
                                        // re-read configuration
                    // read the configuration from the same file as last time:
                    this.getConfig (values);
                    break;

                case AppFunctions.FCT_LAYOUTPERFORM: // show complete layout
                    this.showLayout (values);    // show layout
                    break;

                case AppFunctions.FCT_RETRIEVEMLCLIENTINFO: // retrieve ml client info
                    this.retrieveMultilangClientInfo (values);
                    break;
                    
                case AppFunctions.FCT_LISTPERFORM:   // show list of objects
                case AppFunctions.FCT_SHOWOBJECTCONTENT: // show content of object
                case AppFunctions.FCT_SHOWOBJECTCONTENTFRAMESET: // show content of object
                    // get the parameter for the tabdisplay
                    if ((num = values.p_env.getIntParam (BOArguments.ARG_SHOWTABBAR)) !=
                        IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
                    {
                        // get object oid and display the object content:
                        this.list (values, oid, num == 1);
                    } // if
                    else
                    {
                        // get object oid and display the object content:
                        this.list (values, oid);
                    } // else
                    break;

                case AppFunctions.FCT_SHOWOBJECT: // show a business object
                    // switch to content view
                    userInfo.inContentView = true;
                    // get object oid and display the object:
                    this.showObject (values, oid);
                    break;

                case AppFunctions.FCT_SHOWNEWSWOTABSBUTTONS:
                    // showObject without tabs and buttons:
                    this.showObject (values,
                        userInfo.workspace.news, true, false);
                    break;

                case AppFunctions.FCT_SHOWPREVOBJECT:// show previous business object in container
                    // get actual object oid and display the previous object:
                    this.showPrevObject (values);
                    break;

                case AppFunctions.FCT_SHOWNEXTOBJECT:// show next business object in container
                    // get actual object oid and display the next object:
                    this.showNextObject (values);
                    break;

                case AppFunctions.FCT_SHOWUPPEROBJECT: // show upper business object
                    // get actual object oid and display the upper object:
                    this.showUpperObject (values);
                    break;

                case AppFunctions.FCT_GOBACK:    // go back
                    this.goBack (values);
                    break;

                case AppFunctions.FCT_SHOWTABOBJECT: // show business object determined by tab
/* KR currently not implemented
                    // currently not in content view
                    this.sess.userInfo.inContentView = false;
                    // get actual object oid and display the upper object:
                    this.showTabObject (oid);
*/
                    break;

                case AppFunctions.FCT_SHOWTABLINK: // show business object determined by tab
/* KR currently not implemented
                    // currently not in content view
                    this.sess.userInfo.inContentView = false;
                    // get actual object oid and display the upper object:
                    this.showTabLink (oid);
*/
                    break;

                case AppFunctions.FCT_SHOWTABVIEW: // show specific view on object
                    this.showTabView (values, values.p_env
                        .getIntParam (BOArguments.ARG_TABID));
                    break;

                case AppFunctions.FCT_FORWARDTABVIEWEVENT: // show specific view on object
                    this.forwardTabViewEvent (values,
                                 values.p_env.getIntParam (BOArguments.ARG_TABID),
                                 values.p_env.getIntParam (BOArguments.ARG_EVENT));
                    break;

                case AppFunctions.FCT_SHOWTAB: // show tab of business object
                    // get actual object oid and display the upper object:
                    this.showTab (values);
                    break;

                case AppFunctions.FCT_GETAVAILABLETABS: // get available tab info to client
                    // get actual object oid and display the upper object:
                    this.showAvailableTabs (values);
                    break;

                case AppFunctions.FCT_USERPRIVATETAB: // show private tab of user
                    this.showUserPrivateTab (values);
                    break;

                case AppFunctions.FCT_SHOWOBJECTINFO: // show info of a business object
                    // currently not in content view
                    userInfo.inContentView = false;
                    // get object oid and display the info of the object:
                    this.showObjectInfo (values);
                    break;

                case AppFunctions.FCT_OBJECTNEWFORM: // show form for creating a new object
                    // show form for new object:
                    values.p_sess.p_showObjAfterNew = true;
                    values.p_sess.p_changeFormFct = AppFunctions.FCT_OBJECTCHANGE;
                    this.showObjectNewForm (values);
                    break;

                case AppFunctions.FCT_OBJECTNEW:     // create a new business object
                    // get container oid and create the object:
                    this.objectNew (values);
                    break;

                case AppFunctions.FCT_OBJECTNEW_EXTENDED: // create a new business object
                    // create the object with extended options
                    this.objectNewExtended (values);
                    break;

                case AppFunctions.FCT_SHOWSEARCHANDREFERENCE:
                    // create functionobject for searchfunction
                    FunctionSearch funcSearch = new FunctionSearch ();

                    // initialize function with current oid (is used for lokal search)
                    funcSearch.initFunction (AppFunctions.FCT_OBJECTSEARCH,
                                             values.getUser (), values.p_env,
                                             values.p_sess, values.p_app);

                    // show search with given searchQuery which was set
                    // in QueryLinkTabView.setSpecificProperties
                    funcSearch.showSearch (values.p_sess.searchQuery, oid, oid);
                    break;

                case AppFunctions.FCT_SHOWNEWANDREFERENCE:
                    // create functionobject for searchfunction
                    FunctionSearch funcNew = new FunctionSearch ();

                    // initialize function with current oid (is used for lokal search)
                    funcNew.initFunction (AppFunctions.FCT_OBJECTSEARCH,
                                          values.getUser (), values.p_env,
                                          values.p_sess, values.p_app);

                    // show search with given newQuery which was set
                    // in QueryLinkTabView.setSpecificProperties
                    funcNew.showSearch (values.p_sess.newQuery, oid, oid);
                    break;

                case AppFunctions.FCT_CREATELINKTABREFERENCE:
                    OID targetOid =
                        values.p_env.getOidParam (BOArguments.ARG_CALLINGOID);
                    // create reference in referencetab
                    OID refOid = this.createLinkTabReference (oid, targetOid, values);
                    if (refOid != null) // object was created, oid returned?
                    {
                        // show Object where execution was started
                        this.goBack (values, 0);
                    } // if object was created, oid returned
                    else
                    {
                        IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                            BOMessages.ML_MSG_OBJECTNOTCREATED, values.p_env),
                            values.p_app, values.p_sess, values.p_env);
                    } // else
                    break;

                case AppFunctions.FCT_NEWANDREFERENCEFORM:
                    // create a new business object and a reference on it
                    // get object containerId and create new object:
                    values.p_sess.p_showObjAfterNew = false;
                    values.p_sess.p_sourceObjectOid = oid;
                    values.p_sess.p_afterNewFct =
                        AppFunctions.FCT_NEWANDREFERENCE;
                    FunctionValues newValues = (FunctionValues) values.clone ();
                    newValues.setOid (values.getObject ().containerId);
                    this.showObjectNewForm (newValues);
                    break;

                case AppFunctions.FCT_NEWANDREFERENCE:
                    // create a new business object and a reference to it
                    // get object oid and display the object:
                    this.newAndReference (values);
                    break;

                case AppFunctions.FCT_OBJECTCHANGEFORM:// show form for changing an existing object
                    values.p_sess.p_changeFormFct = AppFunctions.FCT_OBJECTCHANGE;
                    // show form for existing object:
                    this.showObjectChangeForm (values);
                    break;

                case AppFunctions.FCT_OBJECTCHANGE:  // change an existing business object
//trace ("KR change the Object!!!");
                    // get object oid and change the object:
                    this.objectChange (oid, false, values);
                    break;

                case AppFunctions.FCT_OBJECTCHANGE_CHANGEFORM:  // change an existing
                    // business object and show change form
                    // get object oid and change the object:
                    values.p_sess.p_changeFormFct = AppFunctions.FCT_OBJECTCHANGE;
                    this.objectChange (oid, true, values);
                    break;

                case AppFunctions.FCT_OBJECTDELETECONFIRM: // show confirmation message for
                    // deleting an existing business object
                    // get object oid and delete the object:
                    this.objectDeleteConfirm (values);
                    break;

                case AppFunctions.FCT_OBJECTDELETE:  // delete an existing business object
                    // get object oid and delete the object:
                    userInfo.history.prev ();
                    this.objectDelete (values);
                    break;

                case AppFunctions.FCT_FILTER:  // delete an existing business object
                    // get object oid and delete the object:
                    this.protocolFilter (values);
                    break;

                case AppFunctions.FCT_OBJECTSEARCH:  // display search result
// HACK AJ
// this part should be dynamic
                    // create functionobject for searchfunction
                    FunctionSearch func = new FunctionSearch ();

                    // initialize function with current oid (is used for lokal search)
                    func.initFunction (AppFunctions.FCT_OBJECTSEARCH, values.getUser (),
                                       values.p_env, values.p_sess, values.p_app);

                    // check if the search has to be displayed:
                    int event = func.getEvent ();
                    if (event == SearchEvents.EVT_BUILDCALLINGSEARCHFORM)
                                            // don't display search button?
                    {
                        // set the search button inactive:
                        this.disableButton (values, Buttons.BTN_SEARCH);
                    } // if don't display search button

                    // call sequence control of function
                    func.start ();
                    break;
// HACK END

                case AppFunctions.FCT_SIMPLESEARCH: // display simple search result
                    // get object oid and display the object:
                    this.performObjectSimpleSearch (values);
                    break;

                case AppFunctions.FCT_SETFILTER :  // display search result
                    // get object oid and display the object:
                    this.showLogContent (values);
                    break;

                case AppFunctions.FCT_LISTDELETEFORM:  // show form for multiple delete
                    // get object oid and display the object:
                    this.showDeleteForm (values);
                    break;

                case AppFunctions.FCT_LISTDELETE:  // display search result
                    // get object oid and display the object:
                    this.deleteSelectedElements (values);
                    break;

                case AppFunctions.FCT_LISTUNDELETE:  // display search result
                    // get object oid and display the object:
                    this.undeleteSelectedElements (values);
                    break;

                case AppFunctions.FCT_LISTCHANGEFORM: // show form to change
                    // attributes of container content
                    // get container oid and display the listchangeform:
                    this.showListChangeForm (values);
                    break;

                case AppFunctions.FCT_LISTCHANGE: // change attributes of container content
                    // get container oid and change content
                    this.listChange (values);
                    break;

                case AppFunctions.FCT_SHOWMASTER:  // display the masterattachment
                    // get object oid and display the object:
                    this.showMaster (values);
                    break;

                case AppFunctions.FCT_OBJECTCLEANFORM:  // form to clean system from expired objects
                    // get object oid and display the clean form:
                    this.showClean (values);
                    break;

                case AppFunctions.FCT_OBJECTCLEAN:  // perform cleaning expired objects
                    // get object oid and celan expired objects
                    this.objectClean (values);
                    break;

                case AppFunctions.FCT_CLEANLOG:  // perform cleaning expired objects
                    // get object oid and celan expired objects
                    this.objectCleanLog (values);
                    break;

                case AppFunctions.FCT_NOTIFICATION:  // starting notification
                    this.objectDistribute (values);
                    break;

                case AppFunctions.FCT_OBJECTCUT: // store the oid of the marked BusinessObject
                    this.objectCut (values);
                    break;

                case AppFunctions.FCT_OBJECTCOPY: // store the oid of the marked BusinessObject
                    this.objectCopy (values);
                    // set a Flag to signal the user has marked a Object
                    break;

                case AppFunctions.FCT_REFERENCE:  // display search result
                    if (userInfo.copiedOids.size () > 1)
                    {
                        this.showPasteLinkSelForm (values);
                    } // if
                    else
                    {
                        // get object oid and display the object:
                        if (!userInfo.copiedOids.isEmpty ())
                        {
                            this.performReference (values);
                            // if the reference is put into a tab, remove the
                            // object from the history:
                            if ((oid != null) &&
                                !oid.equals (userInfo.history.getOid ()))
                            {
                                userInfo.history.prev ();
                            } // if
                        } // if
                        else
                        {
                            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                                BOMessages.ML_MSG_NOOBJECTMARKED, values.p_env),
                                values.p_app, values.p_sess, values.p_env);
                        } // else
                    } // else
                    break;

                case AppFunctions.FCT_OBJECTPASTE: // paste the BusinessObject
                    // calculate the number of copied physical and virtual objects
                    int copiedObjectsSize = userInfo.copiedOids.size () +
                        userInfo.copiedObjects.size ();
                    // did we copied more then 1 object
                    if (copiedObjectsSize > 1)
                    {
                        // show the mutliple selection form
                        this.showPasteSelForm (values);
                    } // if
                    else
                    {
                        if (!(userInfo.copiedOids.isEmpty () &&
                              userInfo.copiedObjects.isEmpty ()))
                        {
                            this.performCopy (values);
                        } // if
                        // remove the last entry because object is only redisplayed
                        else
                        {
                            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                                BOMessages.ML_MSG_NOOBJECTMARKED, values.p_env),
                                values.p_app, values.p_sess, values.p_env);
                        } // else
                    } // else
                    break;

                case AppFunctions.FCT_LOGINFORM:     // show login dialog
                    // get object oid and display the clean form:
                    this.showLoginForm (values, false);
                    break;

                case AppFunctions.FCT_LOGIN:  // perform login
                    // get object oid and clean expired objects
                    if (values.p_sess.weblink)
                    {
                        // weblink should be shown, login is done there
                        this.showWeblink (values);
                    } // if
                    else
                    {
                        // perform a normal login:
                        this.login (values);
                    } // else
                    break;

                case AppFunctions.FCT_LOGOUT:  // perform logout of the user
                    // perform a normal logout:
                    this.logout (values);
                    break;

                case AppFunctions.FCT_CHANGEPASSWORDFORM: // show change password dialog
                    User_01 usr = null;
                    usr = (User_01)
                           values.getObject (values.getUser ().oid);

                    // check if the redirect param has been set
                    String redirectParam = values.p_env.getStringParam (BOArguments.ARG_REDIRECT);
                    if (redirectParam != null)
                    {
                        // set the redirect param to the session
                        values.p_env.setSessionObject (BOArguments.ARG_REDIRECT, redirectParam);
                    } // redirect param != null

                    // show change password form
                    if (usr != null) // got object?
                    {
                        usr.showChangePasswordForm (values.p_representationForm);
                    } // if got object
                    break;

                case AppFunctions.FCT_CHANGEPASSWORD: // perform cleaning expired objects
                    // User_01 usr;
                    usr = (User_01)
                           values.getObject (values.getUser ().oid);

                    // show change password form
                    if (usr != null) // got object?
                    {
                        usr.changePassword (values.p_representationForm);
                    } // if got object

                    break;

                case AppFunctions.FCT_SHOWWELCOME: // show welcome page
                    // check if disable change password argument is set
                    if (values.p_env.getStringParam (BOArguments.ARG_DISABLE_CHG_PWD) != null)
                    {
                        // reset the change password flag
                        (values.getUser ()).p_changePwd = false;

                        // disable the redirect flag since the redirect has been performed
                        values.p_env.setSessionObject (BOArguments.ARG_REDIRECT, null);
                    } // if disable change password argument is set

                    if (values.p_sess.weblink)
                    {
                        this.showWeblink (values);
                    } // if
                    else
                    {
                        this.showWelcome (values);
                    } // else
                    break;

                case AppFunctions.FCT_SHOWWELCOMEFRAMESET: // show the frameset of the welcome page (redirect)
                    if (values.p_sess.weblink)
                    {
                        this.showWeblink (values);
                    } // if
                    else
                    {
                        values.p_env.redirect (
                            userInfo.homepagePath +
                            values.p_sess.activeLayout.path +
                            AppConstants.SSI_WELCOMEFRAMESET);
                    } // else
                    break;

                // OBSERVER functions - requested by observer thread
                case AppFunctions.FCT_OBSERVER: // show import form
                    this.performObserverFunction (values);
                    break;

                // OBSERVER functions - requested by gui-action
                case AppFunctions.FCT_OBSERVERGUI: // show import form
                    this.performObserverGUIFunction (values);
                    break;

                // AGENT functions
                case AppFunctions.FCT_AGENTLOGINIMPORT: // show import form
                    this.performAgentLoginImport (values);
                    break;

                    //ExportAgent
                case AppFunctions.FCT_AGENTLOGINEXPORT: // show export form
                    this.performAgentLoginExport (values);
                    break;

                case AppFunctions.FCT_AGENTLOGINRESTOREEXTERNALIDS: // resolve external ids
                    this.performAgentLoginRestoreExternalIds (values);
                    break;

                // IMPORT functions
                case AppFunctions.FCT_SHOWOBJECTIMPORTFORM: // show import form
                    this.showObjectImportForm (values);
                    break;

                case AppFunctions.FCT_OBJECTIMPORT: // import
                    this.objectImport (values);
                    break;

                case AppFunctions.FCT_SHOWMULTIPLEUPLOADFORM: // show multiple upload form
                    this.showMultipleUploadForm (values);
                    break;

                    // EXPORT functions
                case AppFunctions.FCT_LISTEXPORTFORM:
                    this.showListExportForm (values);
                    break;

                case AppFunctions.FCT_SHOWEXPORTFORM: // show export form
                    this.showExportForm (values);
                    break;

                case AppFunctions.FCT_LISTEXPORT: // export the selected objects
                    this.exportSelectedElements (values, values.p_env
                        .getMultipleFormParam (BOArguments.ARG_EXPORTOID));
                    break;

                case AppFunctions.FCT_OBJECTEXPORT: // export the selected objects
                    this.objectExport (values);
                    break;


                case AppFunctions.FCT_CREATEREFERENCE:
                    this.createReference (values, oid, values.p_env
                        .getOidParam (BOArguments.ARG_CALLINGOID));
                    break;

                case AppFunctions.FCT_SHOWSELECTFORM:
                    // get object oid and display the object:
                    this.showSelectionForm (values);
                    break;

                case AppFunctions.FCT_HANDLESELECTEDELEMENTS:
                    // get oid of selected elements and create or delete link to
                    // current container
                    this.handleSelectedElements (values);
                    break;

                case AppFunctions.FCT_LISTCOPYFORM: // display search result
                    // get object oid and display the object:
                    this.showCopySelForm (values);
                    break;

                case AppFunctions.FCT_LISTCOPY: // copy selected elements
                    // get object oid and display the object:
                    this.copySelectedElements (values);
                    break;

                case AppFunctions.FCT_LISTCUTFORM: // display search result
                    // get object oid and display the object:
                    this.showCutSelForm (values);
                    break;

                case AppFunctions.FCT_LISTCUT: // cut selected elements
                    // get object oid and display the object:
                    this.cutSelectedElements (values);
                    break;

                case AppFunctions.FCT_LISTPASTE: // copy selected elements
                    // get object oid and display the object:
                    this.pasteSelectedElements (values);
                    break;

                case AppFunctions.FCT_LISTPASTELINK: // copy selected elements
                    // get object oid and display the object:
                    this.addSelectedLinkElements (values);
                    break;

                case AppFunctions.FCT_LISTDISTRIBUTE: // copy selected elements
                    this.showMultDistributeForm (values);
                    break;

                case AppFunctions.FCT_PERFORMLISTDISTRIBUTE: // copy selected elements
                    // get object oid and display the object:
                    this.distributeSelectedElements (values);
                    userInfo.okDistributeFunction =
                        AppFunctions.FCT_NOTIFICATION;
                    break;

                case AppFunctions.FCT_ACTIVATE:
                    // activate the layout:
                    this.activateLayout (values, oid);
                    break;

                case AppFunctions.FCT_SHOWRIGHTOBJECT: // show a business object
                    // get object oid and display the object:
                    this.showRightsObject (values, values.p_env
                        .getIntParam (BOArguments.ARG_RPERSONID));
                    break;

                case AppFunctions.FCT_CHECKOUT: // check a business object out and display it
                    this.checkOut (values);
                    break;

                case AppFunctions.FCT_CHECKIN: // check the business object in and display it
                    this.checkIn (values);
                    break;

                case AppFunctions.FCT_CHANGECHECKIN: // change the business object, check it in and display it
                    this.changeCheckIn (values);
                    break;

                case AppFunctions.FCT_EDITBEFORECHECKIN:
                    this.editBeforeCheckIn (values);
                    break;

                case AppFunctions.FCT_EDITBEFORECHECKINCONTAINER:
                    this.editBeforeCheckInContainer (values);
                    break;

                case AppFunctions.FCT_CHECKINCONTAINER:
                    this.checkInContainer (values);
                    break;

                case AppFunctions.FCT_CHANGECHECKINCONTAINER: // change the business object, check it in within the container and display it
                    this.changeCheckInContainer (values);
                    break;

                case AppFunctions.FCT_CHECKOUTCONTAINER:
                    this.checkOutContainer (values);
                    break;

                case AppFunctions.FCT_WEBDAVCHECKOUT:
                    this.webdavcheckOut (values);
                    break;

                case AppFunctions.FCT_WEBDAVCHECKIN:
                    this.webdavcheckIn (values);
                    break;

                /////////////////////////////////
                //
                //  WORKFLOW-BLOCK START
                //
                // display search result:
                case AppFunctions.FCT_WORKFLOW:
                    // create functionobject for handling of workflow-events
                    WorkflowFunction wffunc = new WorkflowFunction ();

                    // initialize function with current oid (is used for lokal search)
                    wffunc.initFunction (AppFunctions.FCT_WORKFLOW, values.getUser (),
                                        values.p_env, values.p_sess, values.p_app);

                    // call sequence control of function
                    wffunc.handleEvent ();
                    break;

                // show selection list for multiple forward:
                case AppFunctions.FCT_LISTFORWARDFORM:
                    this.showForwardSelectionForm (values);
                    break;

                // forward selected elements:
                case AppFunctions.FCT_LISTFORWARD:
                    this.forwardSelectedElements (values);
                    break;
                //
                //  WORKFLOW-BLOCK END
                //
                /////////////////////////////////

                case AppFunctions.FCT_TABNAVPERFORM: // show tabs on the left side
                    this.tabNavigation (values);
                    break;

                case AppFunctions.FCT_MENUPERFORM: // show menu
                    this.createMenu (values);
                    break;

                case AppFunctions.FCT_CHANGETOINSECURESESSION:
                                            // the domain is non-secure
                    this.loginChangeToInsecure (values);
                    break;

                case AppFunctions.FCT_WEBLINK: // login and show object
                    this.showWeblink (values);
                    break;

                case AppFunctions.FCT_LOADWEBLINKURL: // login and show object
                    Attachment_01 att;      // the url object
                    if ((att = (Attachment_01) values.getObject ()) != null)
                    {
                        att.loadWeblinkUrl ();
                    } // if
                    break;

                case AppFunctions.FCT_SHOWUNDELETEFORM: // show form for undelete
                    this.showUnDeleteForm (values); // display the form
                    break;

                case AppFunctions.FCT_FORWARDEVENT:
                    // forward event to businessobject
                    this.forwardEvent (values, values.p_env.getIntParam (BOArguments.ARG_EVENT));
                    break;

                // functions for installer:
                case AppFunctions.FCT_INSTALL:
                    this.install (values);
                    // display the layout:
                    values.p_env.write (IE302.TAG_NEWLINE + IE302.TAG_NEWLINE +
                        "<BUTTON onClick=\"document.location.href = '" +
                        this.getLayoutUrl (values) + "'\">" +
                        IE302.HCH_NBSP + IE302.HCH_NBSP + IE302.HCH_NBSP +
                        "OK" +
                        IE302.HCH_NBSP + IE302.HCH_NBSP + IE302.HCH_NBSP +
                        "</BUTTON>");
                    break;

                case AppFunctions.FCT_GENERATEVCARD:
                    // forward event to businessobject
                    this.generateVCard (values);
                    break;

                case AppFunctions.FCT_GENERATEVCALENDAR:
                    // forward event to businessobject
                    this.generateVCalendar (values);
                    break;

                // functions for future use:
                case AppFunctions.FCT_DUMMYFUNCTION01:
                case AppFunctions.FCT_DUMMYFUNCTION02:
                case AppFunctions.FCT_DUMMYFUNCTION03:
                case AppFunctions.FCT_DUMMYFUNCTION04:
                case AppFunctions.FCT_DUMMYFUNCTION05:
                case AppFunctions.FCT_DUMMYFUNCTION06:
                case AppFunctions.FCT_DUMMYFUNCTION07:
                case AppFunctions.FCT_DUMMYFUNCTION08:
                case AppFunctions.FCT_DUMMYFUNCTION09:
                case AppFunctions.FCT_DUMMYFUNCTION10:
                    this.dummyFunction (values);
                    break;

                default:                // unknown function
                    return function;    // return the function to be performed
            } // switch function
        } // try
        catch (Exception e)
        {
            // any exception => show a message:
            IOHelpers.showMessage (e, values.p_app, values.p_sess, values.p_env, true);
        } // catch

        // return that no further function shall be performed:
        return AppFunctions.FCT_NOFUNCTION;
    } // evalFunction


    /*************************************************************************
     * Ensure that all wizards are initialized. <BR/>
     * After executing this method all currently open wizards are closed. No
     * wizard stays in an open or undefined state.
     *
     * @param   values      The values for the function handler.
     *
     * @deprecated  This method is never used.
     */
    @Deprecated
    public void initializeWizards (FunctionValues values)
    {
        // registration wizard:
        values.p_sess.wizardRegistration = false;

        // show object after new:
        values.p_sess.p_showObjAfterNew = true;

        // function to be performed after new:
        values.p_sess.p_afterNewFct = AppFunctions.FCT_NOFUNCTION;
    } // initializeWizards


    /**************************************************************************
     * Covers all functions the Login-Wizard has to perform. <BR/>
     *
     * @param   function    The function to be called.
     * @param   values      The values for the function handler.
     */
    public void loginWizard (int function, FunctionValues values)
    {
        // this method may be overwritten in subclasses
    } // loginWizard


    /**************************************************************************
     * Builds the layout of the application and writes it to the output.
     * <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void showLayout (FunctionValues values)
    {
        String url = this.getLayoutUrl (values);

        // build the frameset of the application and write it onto the browser:
//trace ("\n\n\nIE4\n\n\n");
//trace (url);

        values.p_env.redirect (url);
    } // showLayout


    /**************************************************************************
     * Get the url for the layout. <BR/>
     *
     * @param   values      The values for the function handler.
     *
     * @return  The layout url.
     */
    protected String getLayoutUrl (FunctionValues values)
    {
//        String path = values.getUserInfo ().homepagePath;
        String path = values.p_app.p_system.p_m2WwwBasePath + "app/";

        String url =
            path + values.p_sess.activeLayout.path +
            values.p_sess.activeLayout.frameset +
            HttpArguments.ARG_BEGIN +
            IOHelpers.urlEncode (
                values.getUser ().domainName + " - " + values.p_sess.userInfo.getExtendedUserData ());

        // return the result:
        return url;
    } // getLayoutUrl


    /**************************************************************************
     * Builds a list of objects within a container and writes it to the
     * output. <BR/>
     *
     * @param   values          The values for the function handler.
     * @param   containerOid    The oid of the current container.
     */
    protected void list (FunctionValues values, OID containerOid)
    {
        // show list, don't display tabs:
        this.list (values, containerOid, false);
    } // list


    /**************************************************************************
     * Builds a list of objects within a container and writes it to the
     * output. <BR/>
     *
     * @param   values          The values for the function handler.
     * @param   containerOid    The oid of the current container.
     * @param   displayTabs     Shall the tabs be displayed, too?
     */
    protected void list (FunctionValues values, OID containerOid, boolean displayTabs)
    {
        BusinessObject obj = null;      // the container with no specific type
        Container cont;                 // the container object

        if ((obj = values.getObject (containerOid)) != null)
            // show the data content of the object:
        {
            // convert object to container:
            cont = (Container) obj;
            // display also the tabs if necessary:
            cont.displayTabs = displayTabs;
            // show the content of the object:
            cont.showContent (values.p_representationForm);
        } // if
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else
    } // list


    /**************************************************************************
     * Builds a frameset representation of a list of objects within a container
     * and writes it onto the browser. <BR/>
     *
     * @param   values      The values for the function handler.
     * @param   displayTabs Shall the tabs be displayed, too?
     *
     * @deprecated  This method is never used.
     */
    @Deprecated
    protected void listFrameset (FunctionValues values, boolean displayTabs)
    {
        Container cont;                 // the container object

        if ((cont = (Container) values.getObject ()) != null)
            // show the data content of the object:
        {
            cont.displayTabs = displayTabs; // display also the tabs if necessary
            cont.showContentFrameset (values.p_representationForm);
        } // if
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else didn't get object
    } // listFrameset


    /**************************************************************************
     * Presents the data of a business object to the user. <BR/>
     *
     * @param   values      The values for the function handler.
     * @param   oid     Object id of the required business object.
     */
    protected void showObject (FunctionValues values, OID oid)
    {
        // call generic method:
        this.showObject (values, oid, true, true);
    } // showObject


    /**************************************************************************
     * Presents the data of a business object to the user. <BR/>
     *
     * @param   values      The values for the function handler.
     * @param   oid     Object id of the required business object.
     * @param   addToHistory    Shall the object be added to the history?
     */
    protected void showObject (FunctionValues values, OID oid, boolean addToHistory)
    {
        // call generic method:
        this.showObject (values, oid, addToHistory, true);
    } // showObject


    /**************************************************************************
     * Presents the data of a business object to the user. <BR/>
     *
     * @param   values      The values for the function handler.
     * @param   oid             Object id of the required business object.
     * @param   addToHistory    Shall the object be added to the history?
     * @param   displayButtons  Shall the buttons of the object be displayed?
     */
    protected void showObject (FunctionValues values, OID oid,
                               boolean addToHistory, boolean displayButtons)
    {
/* KR 011205: this retrieve is not necessary because it is already done in getObject
        boolean message=false;          // flag if the user has no right to see
        // the container elements
*/
        BusinessObject obj;             // the business object

        if ((obj = values.getObject (oid)) != null)
        {
//debug (" --- IbsFunctionHandler.showObject after getObject: " + obj.getClass ().toString ());
//trace ("isContainer = " + obj.isContainer);
            // check if the object shall be added to the history:
            // if the object is a tab it shall not be added to the history.
            if (addToHistory && !obj.isTab ())
                                        // object shall be added to history?
            {
                // add the object to the history if it is no query
                if (!obj.isQuery ())
                {
                    values.getUserInfo ().history.add
                        (oid, TabConstants.TAB_NONE, obj.name, obj.icon, obj.p_virtualId, obj.typeName);
                } // if
            } // if object shall be added to history

/* KR 011205: this retrieve is not necessary because it is already done in getObject
            // get the object data out of the data store:
            try
            {
trace ("KR showObject: retrieve obj begin");
                obj.retrieve (Operations.OP_READ);
trace ("KR showObject: retrieve obj end");
            } // try
            catch (NoAccessException e) // no access to object allowed
            {
                message = true;
                obj.showNoAccessMessage (Operations.OP_READ);
            } // catch
            catch (AlreadyDeletedException e) // no access to objects allowed
            {
                message = true;
                // send message to the user:
                obj.showAlreadyDeletedMessage ();
            } // catch

            if (!message)
            {
*/
            // check if the object is a tab of another object:
            if (obj.isTab ())           // object is tab of another object?
            {
                BusinessObject upperObject = values.getObject (obj.containerId);
                if (upperObject != null) // upper object was found?
                {
                    // get tab bar code of upper object and store it
                    // to the actual object:
                    obj.p_containerTabs = upperObject.getTabBar ();

                    // *-*-*-*-*-*-*-*-*-*-*-*
                    // *-*-* HACK BEGIN *-*-* 
                    // DUE TO THE NECESSITY OF A TRANSLATION OF THE DESCRIPTION OF TABS
                    // This should be done within a better place and not with an extra
                    // iteration of the tabs of the container object which cost performance
                    Iterator<Tab> tabsIter = obj.p_containerTabs.iterator ();
                    while (tabsIter.hasNext ())
                    {
                        Tab oneTab = tabsIter.next ();
                        // Do we have an OID of the object - should always be true
                        if (obj.oid != null)
                        {
                            // Is this TAB the correct one
                            if (obj.oid.equals (oneTab.getOid ()))
                            {
                                // retrieve the description for the tab with the defined lookup key
                                String lookupKey = MultilingualTextProvider.getTabBaseLookupKey (oneTab);
                                MultilingualTextInfo mlDescriptionInfo = MultilingualTextProvider.getMultilingualTextInfo (
                                    MultilangConstants.RESOURCE_BUNDLE_TABS_NAME,
                                    MultilingualTextProvider.getDescriptionLookupKey (lookupKey),
                                    MultilingualTextProvider.getUserLocale (values.p_env),
                                    values.p_env);
                                // did we found a translated description, override the existing one
                                if (mlDescriptionInfo.isFound ())
                                {
                                    obj.description = mlDescriptionInfo.getMLValue ();
                                } // if
                            } // if
                        } // if
                    } // while
                    // *-*-* HACK END *-*-*
                    // *-*-*-*-*-*-*-*-*-*-*
                } // if upper object was found
            } // if object is tab of another object

            if (obj.isLink && !obj.linkedObjectId.isEmpty ())
                                        // the object is a link?
            {
                // show the object which is referenced by the link:
                this.showObject (values, obj.linkedObjectId, addToHistory, displayButtons);
            } // if the object is a link
            else                    // the object is no link
            {
                obj.displayButtons = displayButtons;
                // show the data content of the object:
                obj.show (values.p_representationForm);
            } // else the object is no link
/* KR 011205: this retrieve is not necessary because it is already done in getObject
            } // if message
*/
        } // if
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTOIDNOTFOUND, new Object [] {oid.toString ()},values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else didn't get object
    } // showObject


    /**************************************************************************
     * Presents the rights of a business object to the user. <BR/>
     *
     * @param   values      The values for the function handler.
     * @param   rPersonId   The person/group for which the rights are defined.
     */
    protected void showRightsObject (FunctionValues values, int rPersonId)
    {
//trace ("KR showRightsObject oid = " + values.p_oid);
        Rights_01 obj = (Rights_01) values.getNewObject (TypeConstants.TC_Rights);
//trace ("KR after getNewObject (TypeConstants.TC_Rights)");

        obj.rOid = values.p_oid;
        obj.rPersonId = values.p_env.getIntParam (BOArguments.ARG_RPERSONID);

        // get the object data out of the data store:
        try
        {
//trace ("KR showRightsObject: retrieve obj begin");
            obj.retrieve (Operations.OP_SETRIGHTS);
//trace ("KR showRightsObject: retrieve obj end");
        } // try
        catch (NoAccessException e) // no access to object allowed
        {
            obj.showNoAccessMessage (Operations.OP_SETRIGHTS);
        } // catch
        catch (AlreadyDeletedException e) // no access to objects allowed
        {
            // send message to the user:
            obj.showAlreadyDeletedMessage ();
        } // catch
        catch (ObjectNotFoundException e)
        {
            // send message to the user:
            obj.showObjectNotFoundMessage ();
        } // catch

        // store the object in the session
        (values.getUserInfo ()).actRightsObject = obj;

        // show the data content of the object:
        obj.show (values.p_representationForm);
    } // showRightsObject


    /**************************************************************************
     * Present the data of the previous business object within the actual
     * container to the user. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void showPrevObject (FunctionValues values)
    {
        Container cont;                 // the container object

        if ((cont = (Container) values.getObject (values.p_containerOid)) != null)
        {
            // show the previous object within the container:
            try
            {
                // get the oid of the previous object and show it:
                this.showObject (values, cont.prevElement (values.p_oid));
            } // try
            catch (NoMoreElementsException e)
            {
                // show message to user:
                IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_NOPREVELEMENT, values.p_env),
                    values.p_app, values.p_sess, values.p_env);
            } // catch
        } // if
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else didn't get object
    } // showPrevObject


    /**************************************************************************
     * Presents the data of the next business object within the actual
     * container to the user. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void showNextObject (FunctionValues values)
    {
        Container cont;                 // the container object

        if ((cont = (Container) values.getObject (values.p_containerOid)) != null)
        {
            // show the next object within the container:
            try
            {
                // get the oid of the next object and show it:
                this.showObject (values, cont.nextElement (values.p_oid));
            } // try
            catch (NoMoreElementsException e)
            {
                // show message to user:
                IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_NONEXTELEMENT, values.p_env),
                    values.p_app, values.p_sess, values.p_env);
            } // catch
        } // if
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else didn't get object
    } // showNextObject


    /**************************************************************************
     * Presents the data of the nearest upper business object to the user. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void showUpperObject (FunctionValues values)
    {
        BusinessObject obj;             // the actual business object

        if ((obj = values.getObject ()) != null) // got object?
        {
            // show the nearest upper object in the hierarchy:
            try
            {
                OID upperObjOid = obj.getUpperOid ();
                if (upperObjOid != null)
                {
                    this.showObject (values, upperObjOid);
                } // if
                else
                {
                    IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                        BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                        values.p_app, values.p_sess, values.p_env);
                } // else
            } // try
            catch (NoMoreElementsException e)
            {
                // show message to user:
                IOHelpers.showMessage (MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                    AppMessages.ML_MSG_TOPLEVEL, values.p_env),
                    values.p_app, values.p_sess, values.p_env);
            } // catch
            catch (NoAccessException e) // no access to objects allowed
            {
                // send message to the user:
                obj.showNoAccessMessage (Operations.OP_VIEW);
            } // catch
            catch (ObjectNotFoundException e) // object not found
            {
                // send message to the user:
                IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                    values.p_app, values.p_sess, values.p_env);
            } // catch
        } // if got object
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else didn't get object
    } // showUpperObject


    /**************************************************************************
     * Goes back in navigation history and shows the last shown object. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void goBack (FunctionValues values)
    {
        this.goBack (values, values.p_env.getIntParam (BOArguments.ARG_GOBACK));
    } // goBack


    /**************************************************************************
     * Builds the Tabs for the object navigation of the upper object. <BR/>
     *
     * @param   values  The values for the function handler.
     * @param   steps   Number of steps to go back.
     */
    protected void goBack (FunctionValues values, int steps)
    {
//trace ("m2FunctionHandler.goBack (" + steps + ")");
        OID oid = null;
        int tabId;
        UserInfo userInfo = values.getUserInfo ();

        // ensure that the wizards are reinitialized:
        this.initializeWizards (values);

        // go to the new position:
        if (userInfo.history.go (steps))
                                        // position within history found?
        {
            oid = userInfo.history.getOid ();
            tabId = userInfo.history.getTabId ();

            if (oid != null)            // oid exists?
            {
                // query
                if (userInfo.history.getName ().equals (HistoryInfo.HISTORY_ENTRY_TYPE_QUERY))
                {
                    this.showSwitchToSearchFrame (values);
                } // else query
                else if (tabId != TabConstants.TAB_NONE) // tab id exists?
                {
                    // get the common tab data:
                    Tab commonTab = values.getTabCache ().get (tabId);

                    switch (commonTab.getKind ())
                    {
                        case TabConstants.TK_VIEW: // tab is view of major object
                            // just display the view of the actual object.

                            // if info or content tab should be shown
                            // use standard method to show tab because
                            // they are not implemented as viewTab with
                            // special class to show view on object.
                            if (TabConstants.TC_INFO.equals (commonTab.getCode ()) ||
                                TabConstants.TC_CONTENT.equals (commonTab.getCode ()))
                            {
                                this.showTab (values, oid, tabId);
                            } // if
                            else
                            {
                                this.showTabView (values, oid, tabId);
                            } // else
                            break;
                        default:
                            // display the object with the activated tab:
                            this.showTab (values, oid, tabId);
                            break;
                    } // switch
                } // if tab id exists
                // no tab id
                else
                {
                    // display the object itself:
                    this.showObject (values, oid);
                } // else no tab id
            } // if oid exists
            else if (userInfo.actObject != null)
            {
                this.showObject (values, userInfo.actObject.oid);
            } // else if
        } // if position within history found
        else if (userInfo.actObject != null)
                                        // actual object found?
        {
            this.showObject (values, userInfo.actObject.oid);
        } // else if actual object found
    } // goBack


    /**************************************************************************
     * Presents the data of the business object determined by a tab of the
     * actual business object to the user. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void objectCleanLog (FunctionValues values)
    {
        LogContainer_01 logObject = null;
        // create a LogContainer_01 object
        // show the content of the Logcontainer

        if ((logObject = (LogContainer_01) values.getObject ()) != null) // got object?
        {
            try
            {
                // show the nearest upper object in the hierarchy:
                logObject.performLogDeleteData (values.p_representationForm);
            } // try
            catch (NoAccessException e)
            {
                // no valid oid => show corresponding message:
                IOHelpers.showMessage ("FailCleanLogBook",
                                       values.p_app, values.p_sess, values.p_env);
            } // catch
        } // if got object

        // create the output:
        IOHelpers.processJavaScriptCode (IOHelpers.getShowObjectJavaScript ("" +
            values.p_oid), values.p_app, values.p_sess, values.p_env);
    } // objectCleanLog


    /**************************************************************************
     * Presents the data of the business object determined by a tab of the
     * actual business object to the user. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void protocolFilter (FunctionValues values)
    {
        LogContainer_01 logObject = null;

        // create a LogContainer_01 object
        // show the content of the log container:
        if ((logObject = (LogContainer_01) values.getObject ()) != null) // got object?
        {
            // show the nearest upper object in the hierarchy:
            logObject.showFilter (values.p_representationForm);
        } // if got object
    } // protocolFilter


    /***************************************************************************
     * Get the data for all available tabs. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void showAvailableTabs (FunctionValues values)
    {
        // get the TabContainerLoader:
        TabContainerLoader tabLoader = new TabContainerLoader (values.getTabCache ());

        try
        {
            // fill the object cache with all the known tabs and their info:
            tabLoader.load (false, true);
            // send the info to the client:
            tabLoader.show (values.p_env);
        } // try
        catch (ListException e)
        {
            values.p_env.write ("Error in tab loader: " + e);
        } // catch
    } // showAvailableTabs


    /**************************************************************************
     * Present the data of a tab of the actual business object to the user. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void showTab (FunctionValues values)
    {
        int tabId = TabConstants.TAB_NONE;  // the id of the tab to be displayed

        // get the tab id out of the parameters and check if it is valid:
        if ((tabId = values.p_env.getIntParam (BOArguments.ARG_TABID)) !=
            IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
                                        // tab was defined?
        {
            // call the common method:
            this.showTab (values, tabId);
        } // if tab was defined
    } // showTab


    /**************************************************************************
     * Present the data of a tab of the actual business object to the user. <BR/>
     *
     * @param   values      The values for the function handler.
     * @param   tabId       The id of the tab to be displayed.
     */
    protected void showTab (FunctionValues values, int tabId)
    {
        this.showTab (values, values.p_oid, tabId);
    } // showTab


    /**************************************************************************
     * Present the data of a tab of the actual business object to the user. <BR/>
     *
     * @param   values      The values for the function handler.
     * @param   oid         Oid of the object for which to show the tab.
     * @param   tabId       The id of the tab to be displayed.
     */
    protected void showTab (FunctionValues values, OID oid, int tabId)
    {
//debug ("IbsFunctionHandler.showTab (" + oid + ", " + tabId + ")");
        BusinessObject obj;             // the actual business object
        Tab commonTab = null;           // common tab data
        Tab actualTab = null;           // the actual tab data
        OID tabOid = oid;               // the oid of the tab object
        String objName = null;          // the name of the object
        String objIcon = null;          // the icon of the object
        String objTypeName = null;          // the typeName of the object


        // get the common tab data:
        commonTab = values.getTabCache ().get (tabId);

        if (commonTab != null)          // found the tab?
        {
            if (commonTab.getFct () != AppFunctions.FCT_SHOWTAB) // not recursive?
            {
                // tab kind specific handling:
                switch (commonTab.getKind ())
                {
                    case TabConstants.TK_VIEW:  // tab is view of major object
                        // just display the view of the actual object:
                        tabOid = oid;

                        // Retrieve the object to find out its name, icon and type name for adding it to the history
                        if ((obj = values.getObject (oid)) != null) // got object?
                        {
                            // remember the object's name and icon:
                            objName = obj.name;
                            objIcon = obj.icon;
                            objTypeName = obj.typeName;
                        } // if got object

                        break;

                    case TabConstants.TK_OBJECT: // tab is an own object
                    case TabConstants.TK_LINK:  // tab is link to other object
                        // get the business object for which to display the tab:
                        if ((obj = values.getObject (oid)) != null) // got object?
                        {
                            try
                            {
                                // get the oid of the (linked) object
                                // determined by the tab:
                                actualTab = obj.getTabObject (tabId);
                                if (actualTab != null) // tab was found?
                                {
                                    tabOid = actualTab.getOid ();
                                } // if tab was found

                                // remember the object's name and icon:
                                objName = obj.name;
                                objIcon = obj.icon;
                                objTypeName = obj.typeName;
                            } // try
                            catch (NoAccessException e)
                                        // no access to objects allowed
                            {
                                // send message to the user:
                                obj.showNoAccessMessage (Operations.OP_VIEW);
                            } // catch
                            catch (ObjectNotFoundException e)
                                        // object not found
                            {
                                // send message to the user:
                                IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                                    BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                                    values.p_app, values.p_sess, values.p_env);
                            } // catch
                        } // if got object
                        else            // didn't get object
                        {
                            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                                values.p_app, values.p_sess, values.p_env);
                        } // else didn't get object
                        break;

                    case TabConstants.TK_FUNCTION: // tab is result of a function
                        // the function has to be evaluated at the current
                        // object - so nothing has to be done here
                        break;

                    default:            // tab kind currently not known
                        break;
                } // switch tab.p_kind

                // display the view of the tab object:
                if (tabOid != null)     // tab oid was found?
                {
                    // add the object to the history:
                    values.getUserInfo ().history.add
                        (oid, commonTab.getIdInt (), objName, objIcon, objTypeName);

                    // display the view:
                    this.evalFunction (commonTab.getFct (), tabOid, values);
                } // if tab oid was found
            } // if not recursive
        } // if found the tab
        else                            // did not find the tab
        {
            // nothing to do
        } // else did not find the tab
    } // showTab


    /**************************************************************************
     * Show the private tab of the user object (must be the actual object).
     * <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void showUserPrivateTab (FunctionValues values)
    {
        User_01 obj;                    // the actual business object

        // get the user object:
        if ((obj = (User_01) values.getObject ()) != null) // got object?
        {
            // get oid of the tab object and display the object:
            this.showObject (values, obj.getPrivateOid ());
        } // if got object
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else didn't get object
    } // showUserPrivateTab


    /**************************************************************************
     * Get initialized view tab with given id of object with given oid. <BR/>
     *
     * @param   values      The values for the function handler.
     * @param   oid         Oid of the object for which to get the tab view.
     * @param   tabId       id of tab to be shown.
     *
     * @return  View tab with given id of object with given oid.
     *          <CODE>null</CODE> if tab does not exist.
     */
    public BusinessObject getTabView (FunctionValues values, OID oid, int tabId)
    {
//debug ("IbsFunctionHandler.getTabView (" + oid + ", " + tabId + ")");
        BusinessObject obj = null;    // the actual business object
        Tab viewTab = null;           // view tab data
        BusinessObject tabObj = null; // businessObject to show tabview

        // get major object of tab:
        obj = values.getObject (oid);

        // get tab to show view on major object:
        viewTab = values.getTabCache ().get (tabId);

        if (viewTab != null && viewTab.getClassName () != null &&
            viewTab.getClassName ().length () > 0)
            // found the tab and class for view is set
        {
//debug ("Class.forName (" + viewTab.getClassName () + ")");
            if (viewTab.getClassName ().equals (obj.getClass ().getName ()))
            {
                tabObj = obj;
            } // if
            else
            {
                try
                {
                    @SuppressWarnings ("unchecked") // suppress compiler warning
                    Class<? extends BusinessObject> cl =
                        (Class<? extends BusinessObject>)
                        Class.forName (viewTab.getClassName ());
                    tabObj = cl.newInstance ();
                } // catch
                catch (ClassNotFoundException e)
                {
                    // class not found.
                    IOHelpers.showMessage ("IbsFunctionHandler.getTabView",
                        "ClassNotFoundException: " + viewTab.getClassName (),
                        values.p_app, values.p_sess, values.p_env);
                    return null;
                } // catch
                catch (InstantiationException e)
                {
                    // instantiation failed.
                    IOHelpers.showMessage ("IbsFunctionHandler.getTabView",
                        "InstantiationException: " + viewTab.getClassName (),
                        values.p_app, values.p_sess, values.p_env);
                    return null;
                } // catch
                catch (IllegalAccessException e)
                {
                    // instantiation failed.
                    IOHelpers.showMessage ("IbsFunctionHandler.getTabView",
                        "IllegalAccessException: " + viewTab.getClassName (),
                        values.p_app, values.p_sess, values.p_env);
                    return null;
                } // catch
                // initialize viewobject as tabview
                tabObj.initObject (obj.oid, values.getUser (), values.p_env,
                                   values.p_sess, values.p_app);
                tabObj.containerId = obj.containerId;
                tabObj.isPhysical = false; // viewTab should not be retrieved
                tabObj.p_isTabView = true;

                // make sure that the right tab is shown
                tabObj.p_tabs = obj.p_tabs;
            } // else

            tabObj.p_tabId = viewTab.getIdInt ();

            // check if there are any tabs, because in function new and
            // to rights, there are no tabs to be shown
            if (tabObj.p_tabs != null)
            {
                // in this moment the only possibility to set
                // the right active tab in object:
                try
                {
                    viewTab = obj.getTabObject (tabId);
                } // try
                catch (NoAccessException e)
                {
                    IOHelpers.showMessage ("IbsFunctionHandler.getTabView", e,
                                           values.p_app, values.p_sess, values.p_env, false);
                } // catch
                catch (ObjectNotFoundException e)
                {
                    IOHelpers.showMessage ("IbsFunctionHandler.getTabView", e,
                                           values.p_app, values.p_sess, values.p_env, false);
                } // catch

                tabObj.p_tabs.setActiveTab (viewTab);
                tabObj.displayTabs = true;
            } // if p_tabs != null

            // set Specific Properties to show view on obj
            tabObj.setSpecificProperties (obj);

            return tabObj;
        } // if tab was found

        // tab not found:
        IOHelpers.showMessage ("IbsFunctionHandler.getTabView",
                               "tab not found. id:" + tabId,
                               values.p_app, values.p_sess, values.p_env);

        return null;
    } // getTabView


    /**************************************************************************
     * Show a specific view on the current object. <BR/>
     *
     * @param   values      The values for the function handler.
     * @param   tabId       id of tab to be shown.
     */
    public void showTabView (FunctionValues values, int tabId)
    {
        this.showTabView (values, values.p_oid, tabId);
    } // showTabView


    /**************************************************************************
     * Show a specific view on the current object. <BR/>
     *
     * @param   values      The values for the function handler.
     * @param   oid         Oid of the object for which to show the tab view.
     * @param   tabId       id of tab to be shown.
     */
    public void showTabView (FunctionValues values, OID oid, int tabId)
    {
//debug ("IbsFunctionHandler.showTabView (" + values.p_oid + ", " + tabId + ")");
        BusinessObject tabObj = null; // businessObject to show tabview

        tabObj = this.getTabView (values, oid, tabId);

        if (tabObj != null)
        {
            // show view on obj
            tabObj.show (values.p_representationForm);
        } // if tabObj != null
    } // showTabView


    /**************************************************************************
     * Forward given event to class due to tab with given tabId. <BR/>
     *
     * @param   values      The values for the function handler.
     * @param   tabId       id of view tab where event was thrown.
     * @param   evt         event to be handled in class due to tab with given tabId.
     */
    public void forwardTabViewEvent (FunctionValues values, int tabId, int evt)
    {
//debug ("IbsFunctionHandler.forwardTabViewEvent (" + values.p_oid + ", " + tabId + ", "
//                                            + evt + ")");
        BusinessObject tabObj = null; // businessObject to show tabview

        tabObj = this.getTabView (values, values.p_oid, tabId);

        if (tabObj != null)
        {
            // show view on obj
            tabObj.handleEvent (evt);
        } // if tabObj != null
    } // forwardTabViewEvent


    /**************************************************************************
     * Forward given event current object of given oid. <BR/>
     *
     * @param   values      The values for the function handler.
     * @param   evt         event to be handled in current object.
     */
    public void forwardEvent (FunctionValues values, int evt)
    {
//debug ("IbsFunctionHandler.forwardEvent (" + values.p_oid + ", " + evt + ")");
        BusinessObject obj = null; // businessObject to show tabview

        obj = values.getObject ();

        if (obj != null)
        {
            // show view on obj
            obj.handleEvent (evt);
        } // if tabObj != null
    } // forwardEvent


    /**************************************************************************
     * Show listChangeForm of RightsContainer after new form. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    public void toRightsTabAfterNew (FunctionValues values)
    {
        // show the listchangeform of the RightsContainer:
        Tab commonTab = values.getTabCache ().find (0, TabConstants.TC_RIGHTS);

        int tabId = commonTab.getIdInt ();
        BusinessObject tabObj = this.getTabView (values, values.p_oid, tabId);

        if (tabObj != null)
        {
            // show view on obj
            tabObj.handleEvent (AppFunctions.FCT_LISTCHANGEFORM);
        } // if tabObj != null
        else
        {
            IOHelpers.showMessage ("Viewtab with id:" + tabId +
                                   " does not exist in type of object " +
                                   "with oid " + values.p_oid,
                                   values.p_app, values.p_sess, values.p_env);
        } // else
    } // toRightsTabAfternew


    /**************************************************************************
     *  Show the master of the actual object. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void showMaster (FunctionValues values)
    {
        BusinessObject obj;
        Attachment_01 master;             // the actual business object
        OID masterOid = null;
        OID oid = values.p_oid;

        // if it is of Type Url or File the oid is
        // already the oid of a Masterattachment. We have to create a object
        // of the masterattachment and execute performshowmaster to display
        // the content of the master in a frame or in a seperate window.


        if ((oid.type == values.getTypeCache ().getTypeId (TypeConstants.TC_File)) ||
            (oid.type == values.getTypeCache ().getTypeId (TypeConstants.TC_Url)) ||
            (oid.type == values.getTypeCache ().getTypeId (TypeConstants.TC_ImportScript)) ||
            (oid.type == values.getTypeCache ().getTypeId (TypeConstants.TC_Attachment)) ||
            (oid.type == values.getTypeCache ().getTypeId (TypeConstants.TC_DocumentTemplate)) ||
            (oid.type == values.getTypeCache ().getTypeId (TypeConstants.TC_WorkflowTemplate)) ||
            (oid.type == values.getTypeCache ().getTypeId (TypeConstants.TC_Translator)) ||
            (oid.type == values.getTypeCache ().getTypeId (TypeConstants.TC_ASCIITranslator)))
                                        // File or Url or DocumentTemplate Type?
        {
            if ((master = (Attachment_01) values.getObject (oid)) != null)
                                        // got object?
            {
                master.performShowMaster ();
            } // if
            else                        // didn't get object
            {
                IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                    values.p_app, values.p_sess, values.p_env);
            } // else didn't get object
        } // if File or Url or DocumentTemplate Type
        else                            // it is no File or Url Type
        {
            if ((obj = values.getObject (oid)) != null) // got object?
            {
                // show the nearest upper object in the hierarchy:

                try
                {
                    if ((masterOid = obj.getMaster ()) != null)
                    {
                        if ((master = (Attachment_01) values.getObject (masterOid)) != null)
                            // got object?
                        {
                            master.performShowMaster ();
                        } // if got object
                        else
                        {
                            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                                values.p_app, values.p_sess, values.p_env);
                        } // else
                    } // if
                    else
                    {
                        IOHelpers.showMessage ("Sorry, no Oid found",
                            values.p_app, values.p_sess, values.p_env);
                    } // else
                } // try
                catch (NoAccessException e) // no access to objects allowed
                {
                    // send message to the user:
                    obj.showNoAccessMessage (Operations.OP_READ);
                } // catch
                catch (ObjectNotFoundException e) // object not found
                {
                    // send message to the user:
                    IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                        BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                        values.p_app, values.p_sess, values.p_env);
                } // catch
            } // if got object
            else                            // didn't get object
            {
                IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                    values.p_app, values.p_sess, values.p_env);
            } // else didn't get object
        } // else it is no File or Url Type
    } // showMaster


    /**************************************************************************
     * Presents the info of a business object to the user. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void showObjectInfo (FunctionValues values)
    {
        BusinessObject obj;             // the business object

        if ((obj = values.getObject ()) != null) // got object?
        {
//trace ("showInfo wird aufgerufen");
            // show the data content of the object:
            obj.showInfo (values.p_representationForm);
        } // if got object
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else didn't get object
    } // showObjectInfo


    /**************************************************************************
     * Presents the data of a new business object to the user within a form.
     * <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void showObjectNewForm (FunctionValues values)
    {
        BusinessObject obj;             // the business object
        int type;                       // type of the new object

        if ((obj = values.getObject (values.p_containerOid)) != null)
        {
//trace ("KR got the object: " + obj);
            // set container id:
            obj.containerId = values.p_containerOid;

            // show the data content of the object:
            type = obj.showNewForm (values.p_representationForm,
                AppFunctions.FCT_OBJECTNEW);
//trace ("KR after displaying newForm: " + type);

            if (type > 0)               // just one type possible?
            {
                // create the new object of the required type:
                this.objectNew (values, Type.createTVersionId (type));
            } // if just one type possible
        } // if
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else didn't get object
    } // showObjectNewForm


    /**************************************************************************
     * Creates the object within the database. <BR/>
     * This method gets the id of the type out of an argument.
     *
     * @param   values      The values for the function handler.
     */
    protected void objectNew (FunctionValues values)
    {
        int num;                        // integer parameter
        int tVersionId = TypeConstants.TYPE_NOTYPE; // type of object
        String typeCode = null;         // the type code

        // get type out of arguments:
        typeCode = values.p_env.getStringParam (BOArguments.ARG_TYPE);
        if (typeCode != null && typeCode.length () > 0)
                                        // parameter exists?
        {
            try
            {
                // try to convert to integer:
                num = Integer.parseInt (typeCode);
                tVersionId = Type.createTVersionId (num);
            } // try
            catch (NumberFormatException e) // no integer?
            {
                // use the type code to find the type info:
                num = values.getTypeCache ().getTVersionId (typeCode);
                if (num != TypeConstants.TYPE_NOTYPE)
                                        // the type code exists?
                {
                    tVersionId = num;
                } // if the type code exists
            } // catch
        } // if parameter exists

        // create the new object:
        this.objectNew (values, tVersionId);
    } // objectNew


    /**************************************************************************
     * Creates the object within the database. <BR/>
     *
     * @param   values      The values for the function handler.
     * @param   tVersionId  Id of the type of the newly created object.
     */
    protected void objectNew (FunctionValues values, int tVersionId)
    {
        BusinessObject obj;             // the newly created business object
        OID oid;                        // oid of the new business object
        boolean isShowPossible = true;  // return value of obj.showChangeForm

//trace ("KR before getNewObject.");
        if ((obj = values.getNewObject (tVersionId)) != null) // got object?
        {
//trace ("KR got new object: " + obj);
            // This code should already be done in the initObject method of the
            // BusinessObject!
            if (values.p_sess.wizardRegistration)
            {
                // ??:
                obj.type = tVersionId;
                obj.name = null;
            } // if

            // ensure that the object has a correct name:
            if (obj.name == null)       // no name defined?
            {
                obj.name = "";          // set default name
            } // if
/* KR: this is not correct
            // set type within oid:
            obj.oid.type = obj.type;
*/
            // get oid of container for new object:
            OID containerOid = values.p_containerOid;
            // check if the object shall be created directly within the
            // container object or within a tab of that object:
            String tabCode = values.p_env.getStringParam (BOArguments.ARG_TABCODE);
            if (tabCode != null)
            {
                // get oid of tab object:
                BusinessObject containerObj = values.getObject (containerOid);
                // check if the container object was found:
                if (containerObj != null)
                {
                    Tab tab = containerObj.getTabBar ().find (0, tabCode);
                    // check if the tab was found:
                    if (tab != null)
                    {
                        OID tabOid = tab.getOid ();
                        // check if the tab has an OID:
                        if (tabOid != null)
                        {
                            containerOid = tabOid;
                        } // if
                    } // if
                } // if
            } // if

            // set container id:
            obj.containerId = containerOid;
            // create the new object:
            oid = obj.create (values.p_representationForm);

            if (oid != null)            // object was created, oid returned?
            {
                // ensure that the object is in the cache:
                if (obj.isPhysical)     // the object is physical?
                {
                    // store it in the session cache:
                    (values.getUserInfo ()).actObject = obj; // store the actual object
                } // if the object is physical

                if (oid.tVersionId == values.getTypeCache ().getTVersionId (TypeConstants.TC_Rights))
                {
                    // show the rights container:
                    this.showObject (values, obj.containerId);
                    return;
                } // if
                // object successfully created => show change form
                // first there must be created a java object of the correct
                // type according to the oid of the object:
/* KR 011217: not necessary because the object was already fetched before
                else if ((obj = values.getObject ()) != null) // got object?
*/

                // normal object:
                // check if a wizard is executed:
                if (values.p_sess.wizardRegistration ||
                    !values.p_sess.p_showObjAfterNew ||
                    values.p_sess.p_afterNewFct != AppFunctions.FCT_NOFUNCTION)
                                    // wizard?
                {
                    obj.showExtendedCreationMenu = false;
                    obj.displayTabs = false;
                } // if wizard
                else                                    // no wizard
                {
                    // show the tabs:
                    obj.displayTabs = true;
                } // else no wizard

                // ensure that the correct function is called for framesets:
                values.p_sess.p_actFct = AppFunctions.FCT_OBJECTCHANGEFORM;
                // show the form:
                isShowPossible =
                    obj.showChangeForm (values.p_representationForm);

                if (!isShowPossible) // object could not be created?
                {
                    // the object could not be created so we have to show a
                    // message to the user and then display the container
                    // within which the new button was pressed:
                    IOHelpers.showPopupMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                        BOMessages.ML_MSG_NOTEMPLATEEXISTS, values.p_env),
                        null, values.p_app, values.p_sess, values.p_env);
                    this.showObject (values, values.p_containerOid);
                } // if object could not be created

/* KR 011217: not necessary because the object was already fetched before
                } // if got object
                else                    // didn't get object
                    showMessage (BOMessages.MSG_OBJECTNOTFOUND);
*/
            } // if object was created, oid returned
            else                        // object not created
            {
                // show corresponding message:
                IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_OBJECTNOTCREATED, values.p_env),
                    values.p_app, values.p_sess, values.p_env);
            } // else object not created
        } // if got object
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else
    } // objectNew


    /**************************************************************************
     * Creates the object with extended options. <BR/>
     * This function is used to create an object from another place
     * then the container the object should be created within.
     * A path or a containerID can be provided that defines the container
     * that object should be created within and an alternative create function
     * can be set that is called after object creation. The oid and the
     * containerId of the object from where the function has been activated
     * will be set as session variables in order to enable the newly created
     * object to use then for any purpose.
     * Note that generally a AppFunction.APP_DUMMYFUNCTIONx should be passed
     * to call the alternative changeForm function. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void objectNewExtended (FunctionValues values)
    {
        BusinessObject obj;             // the newly created business object
        OID oid;                        // oid of the new business object

        int tVersionId;
        OID containerId = null;
        int changeFct;
        boolean isShowPossible;

        // check if subsequent object new extended mode is not active
        // to take the parameter(s) from the environment
        if (values.p_sess.p_modeSubsequentObjectNewExtended != IOConstants.BOOLPARAM_TRUE)
        {
            // first a typecode must be read from the environment
            values.p_sess.p_objectNewExtendedTypecode = values.p_env.getParam (BOArguments.ARG_TYPECODE);
        } // if

        tVersionId = values.getTypeCache ().getTVersionId (values.p_sess.p_objectNewExtendedTypecode);

        // got object?
        if ((obj = values.getNewObject (tVersionId)) != null)
        {
            // check if subsequent object new extended mode is not active
            // to take the parameter(s) from the environment
            if (values.p_sess.p_modeSubsequentObjectNewExtended != IOConstants.BOOLPARAM_TRUE)
            {
                // set the caller data in the session for further use
                values.p_sess.p_callerName = values.p_env.getStringParam (BOArguments.ARG_NAME);
                values.p_sess.p_callerOid = values.p_oid;
                values.p_sess.p_callerContainerId  = values.p_containerOid;
                values.p_sess.p_objectNewExtendedPath = values.p_env.getParam (BOArguments.ARG_PATH);
            } // if
            
            // new that we have an object try to resolve the containerId
            // first check if any path has been set
            if (values.p_sess.p_objectNewExtendedPath != null)
            {
                // resolve the given path
                containerId =
                    BOHelpers.resolveObjectPath (values.p_sess.p_objectNewExtendedPath, obj, values.p_env);
            } // if (path != null)

            // did we get a correct container id?
            // if not check if any alternative path has been set
            if (containerId == null)
            {
                // check if subsequent object new extended mode is not active
                // to take the parameter(s) from the environment
                if (values.p_sess.p_modeSubsequentObjectNewExtended != IOConstants.BOOLPARAM_TRUE)
                {
                    // try to resolve any alternative path
                    values.p_sess.p_objectNewExtendedPathAlt = values.p_env.getParam (BOArguments.ARG_PATH_ALT);
                } // if

                if (values.p_sess.p_objectNewExtendedPathAlt != null)
                {
                    IOHelpers.showMessage (MultilingualTextProvider.getMessage(BOMessages.MSG_BUNDLE,
                            BOMessages.ML_MSG_CREATE_OBJECT_IN_ALTERNATIVE_PATH, 
                            new String[] {values.p_sess.p_objectNewExtendedPathAlt}, values.p_env),
                            values.p_app, values.p_sess, values.p_env);
                    // resolve the given path
                    containerId =
                        BOHelpers.resolveObjectPath (values.p_sess.p_objectNewExtendedPathAlt, obj, values.p_env);
                } // if (path != null)
                else    // no path given
                {
                    // take the container id as path (if given)
                    containerId = values.p_containerOid;
                } // else no path given
            } // if (containerId != null)

            // did we get a correct container id?
            if (containerId != null)
            {
                // ensure that the object has a correct name:
                if (obj.name == null)       // no name defined?
                {
                    obj.name = "";          // set default name
                } // if no name defined
                // set container id:
                obj.containerId = containerId;
                // create the new object:
                oid = obj.create (values.p_representationForm);
                // object was created, oid returned?
                if (oid != null)
                {
                    // activate the objectNewExtended mode by setting the oid
                    values.p_sess.p_modeObjectNewExtendedOID = oid;                    
                    
                    // try to read the alternative change function
                    changeFct = values.p_env.getIntParam (BOArguments.ARG_CHANGEFUNCTION);

                    // got any valid function?
                    if (changeFct != IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
                    {
                        // store the after change function and the oid in the
                        // session. This values will be used in the
                        // objectChange method
                        values.p_sess.p_afterChangeFct = changeFct;
                    } // if (changeFct == IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
                    else if (values.p_sess.p_modeSubsequentObjectNewExtended == IOConstants.BOOLPARAM_TRUE)
                    {
                        // the after change fct is still stored within the session
                    }
                    else    // no after change function set
                    {
                        // reset the after change function settings
                        values.p_sess.p_afterChangeFct = AppFunctions.FCT_NOFUNCTION;
                    } // else no after change function set

                    // ensure that the object is in the cache:
                    if (obj.isPhysical)     // the object is physical?
                    {
                        // store it in the session cache:
                        (values.getUserInfo ()).actObject = obj; // store the actual object
                    } // if the object is physical
                    // show the tabs:
                    obj.displayTabs = true;
                    // ensure that the correct function is called for framesets:
                    values.p_sess.p_actFct = AppFunctions.FCT_OBJECTCHANGEFORM;
                    // show the form:
                    isShowPossible =
                        obj.showChangeForm (values.p_representationForm);
                    // object could not be created?
                    if (!isShowPossible)
                    {
                        // the object could not be created so we have to show a
                        // message to the user and then display the container
                        // within which the new button was pressed:
                        IOHelpers.showPopupMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                            BOMessages.ML_MSG_NOTEMPLATEEXISTS, values.p_env),
                            null, values.p_app, values.p_sess, values.p_env);
                        this.showObject (values, containerId);
                    } // if object could not be created
                } // if object was created, oid returned
                else                        // object not created
                {
                    // show corresponding message:
                    IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                        BOMessages.ML_MSG_OBJECTNOTCREATED, values.p_env),
                        values.p_app, values.p_sess, values.p_env);
                } // else object not created
            } // if (containerId != null)
            else    // container ID not correct
            {
                // show corresponding message:
                IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                    values.p_app, values.p_sess, values.p_env);
            } // else object not created
        } // if ((obj = values.getNewObject (type)) != null)
        else                            // did not get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else did not get object
        
        // Reset the subsequent object new extended mode.
        // It will be reactivated within objectChange () if the user performs Save and New
        values.p_sess.p_modeSubsequentObjectNewExtended = IOConstants.BOOLPARAM_NOTEXISTS;
    } // objectNewExtended


    /**************************************************************************
     * Presents the data of the actual business object to the user
     * within a form. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void showObjectChangeForm (FunctionValues values)
    {
        BusinessObject obj;             // the business object

        if (values.p_oid.tVersionId ==
            values.getTypeCache ().getTVersionId (TypeConstants.TC_Rights))
        {
            obj = values.getUserInfo ().actRightsObject;
            obj.initObject (obj.oid, values.getUser (), values.p_env, values.p_sess, values.p_app);
        } // if
        else
        {
            obj = values.getObject ();
        } // else
        if (obj != null)
        {
            // show the data content of the object:
            obj.showChangeForm (values.p_representationForm);
        } // if
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else
    } // showObjectChangeForm


    /**************************************************************************
     * Changes the object within the database and shows the object info. <BR/>
     *
     * @param   oid         Object id of the actual business object.
     * @param   values      The values for the function handler.
     */
    protected void objectChange (OID oid, FunctionValues values)
    {
        this.objectChange (oid, false, values);
    } // objectChange


    /**************************************************************************
     * Changes the object within the database and shows the object info. <BR/>
     *
     * @param   oid             Object id of the actual business object.
     * @param   showChangeForm  Show change form after changing the object.
     * @param   values      The values for the function handler.
     */
    protected void objectChange (OID oid, boolean showChangeForm,
                                 FunctionValues values)
    {
        BusinessObject obj;             // the business object
        int postAction;                 // action which shall be performed
                                        // after changing
        OID callerOid = null;           // the oid of the caller object

        // defines if the objectNewExtended mode is active for the current OID
        boolean modeObjectNewExtended = false;

        if ((obj = values.getObject (oid)) != null) // got the object?
        {
//trace ("got the Object!");
            // if the obj was just created and should not be shown after
            // creation - do not show it
            if (obj.state == States.ST_CREATED &&
                (!values.p_sess.p_showObjAfterNew ||
                values.p_sess.p_afterNewFct != AppFunctions.FCT_NOFUNCTION))
                                        // don't show the object?
            {
                obj.showAllowed = false;
            } // if don't show the object

            // change object, get post action
            postAction = obj.change (values.p_representationForm, showChangeForm);

            // verify if the object new extended mode is active for this oid
            modeObjectNewExtended = values.p_sess.p_modeObjectNewExtendedOID != null &&
                values.p_sess.p_modeObjectNewExtendedOID.equals (oid);
            
            if (modeObjectNewExtended)
            {
                // reset the object new extended OID info
                values.p_sess.p_modeObjectNewExtendedOID = null;
                
                // check if an afterChange function has been set
                // this should typically be a dummy function
                if (values.p_sess.p_afterChangeFct != AppFunctions.FCT_NOFUNCTION)
                {
                    // call the function:
                    this.evalFunction (values.p_sess.p_afterChangeFct, oid, values);
                    // set the caller oid that will be used when
                    // the container view shall  be activated after changing
                    // the object.
                    callerOid = (OID) values.p_sess.p_callerOid;
                } // if (values.sess.p_afterChangeFct == AppFunctions.FCT_NOFUNCTION && ...
            }
            
            ///////////////////////////
            //
            // SERVICE POINT STARTS WORKFLOW
            //
            // check if service point created object
            if (obj.containerId != null &&
                obj.containerId.type ==
                    values.getTypeCache ().getTypeId (TypeConstants.TC_ServicePoint))
            {
                // create functionobject to handle workflow-events:
                // (includes generation of output and alert-messages according
                //  to settings in workflow-definition)
                WorkflowFunction wffunc = new WorkflowFunction ();

                // initialize function with current oid (is used for lokal search)
                wffunc.initFunction (AppFunctions.FCT_WORKFLOW, values.getUser (),
                                    values.p_env, values.p_sess, values.p_app);

                // add servicepoint to history
                values.getUserInfo ().history.add (obj.containerId,
                    TabConstants.TAB_NONE, "a ServicePoint",
                    "ServicePoint icon", "ServicePoint");

                // start workflow with given object (simulate event
                // startWorkflow in function-handler)
                if (!wffunc.executeEvent (null, obj, null, null,
                    WorkflowEvents.EVT_STARTWORKFLOW))
                {
                    // interruption of 'normal' control-flow required!
                    return;
                } // if
                // else: proceed

            } // if ... object created by service-point
            //
            // SERVICE POINT STARTS WORKFLOW
            //
            ///////////////////////////

/* KR not necessary
///////////////////////////
//
// AJ HACK: UPDATE QUERYCREATOR IN QUERYPOOL START
//
            Type type = null;           // the actual type
            // if a querycreator is changed, the queryPool has to be updated:
            type = (Type) getCache ().getTypeContainer ().find (TypeConstants.TC_QueryCreator);
            if (type.getAllSubTypes ().get (obj.oid.type) != null)
            {
                values.app.queryPool.updateQuery ((QueryCreator_01) obj);
            } // if type == QueryCreator
//
// AJ HACK: UPDATE QUERYCREATOR IN QUERYPOOL END
//
///////////////////////////
*/

            // check if the modeObjectNewExtended is active for the current object
            // and the user did not select Save and New
            if (modeObjectNewExtended && postAction != BOConstants.BONEWMENU_NEW_BUSINESS_OBJECT)
            {
                this.resetObjectNewExtendedParameters (values);
            } // if
            
            switch (postAction)
            {
                case BOConstants.BONEWMENU_NEW_BUSINESS_OBJECT:
                    // create a new business object.
                    // drop all data from actual form:
                    values.p_env.dropFormData ();
                    // call the function:
                    values.p_containerOid = obj.containerId;
                    
                    if (modeObjectNewExtended)
                    {
                        // set the act fct to object new extended
                        values.p_sess.p_actFct = AppFunctions.FCT_OBJECTNEW_EXTENDED;
                        
                        // activate the subsequent object new mode
                        values.p_sess.p_modeSubsequentObjectNewExtended = IOConstants.BOOLPARAM_TRUE;
                    } // if
                    else
                    {
                        // set the act fct to object new
                        values.p_sess.p_actFct = AppFunctions.FCT_OBJECTNEWFORM;
                    } // else
                    
                    this.evalFunction (values.p_sess.p_actFct, obj.containerId, values);
                    break;

                case BOConstants.BONEWMENU_SHOW_BUSINESS_OBJECT:
                    // display the current business object:
                    this.showObject (values, oid);
                    break;

                case BOConstants.BONEWMENU_BACK_TO_THE_CLIPBOARD:
                    // the new object has been saved. Return to the container.
                    BusinessObject backObj;
                    // check if a caller oid has been set
                    // this indicates that the object has been created
                    // from somewhere else then the container of the object
                    if (callerOid == null)
                    {
                        backObj = values.getObject (obj.getMajorContainerOid (), false);
                    } // if
                    else                // no caller oid set
                    {
                        backObj = values.getObject (callerOid, false);
                    } // else
                    // object exists?
                    if (backObj != null) // object exists?
                    {
                        // check if the object is a tab of another object:
                        if (backObj.isTab ())
                                        // object is tab of another object?
                        {
                            BusinessObject upperObject =
                                values.getObject (backObj.containerId, false);
                            if (upperObject != null) // upper object was found?
                            {
                                // get tab bar code of upper object and store it
                                // to the actual object:
                                backObj.p_containerTabs =
                                    upperObject.getTabBar ();
                            } // if upper object was found
                        } // if object is tab of another object

                        backObj.displayButtons = true;
                        // show the data content of the object:
                        backObj.displayTabs = true;
                        backObj.show (values.p_representationForm);
                    } // if object exists
                    else                // object does not exist
                    {
                        IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                            BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env), 
                            values.p_app, values.p_sess, values.p_env);
                    } // else object does not exist
                    break;

                case BOConstants.BONEWMENU_TO_RIGHTS:
                    this.toRightsTabAfterNew (values);
                    break;

                case BOConstants.BONEWMENU_RESHOW_FORM:
                    // delete the object from the history:
                    values.getUserInfo ().history.prev ();
                    break;

                default:                // nothing left to do
                    if (values.p_sess.p_afterNewFct != AppFunctions.FCT_NOFUNCTION)
                    {
                        // call the function:
                        this.evalFunction (values.p_sess.p_afterNewFct, oid, values);
                    } // if
                    break;
            } // switch
        } // if got the object
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else didn't get object
    } // objectChange
    
    
    /**
     * Reset all parameters stored in the session for subsequent
     * objectNewExtended () calls.
     *
     * @param values
     */
    private void resetObjectNewExtendedParameters (FunctionValues values)
    {       
        // reset the afterChangeFct infos
        values.p_sess.p_afterChangeFct = AppFunctions.FCT_NOFUNCTION;
        values.p_sess.p_objectNewExtendedTypecode = null;
        values.p_sess.p_objectNewExtendedPath = null;
        values.p_sess.p_objectNewExtendedPathAlt = null;
    } // resetObjectNewExtendedParameters


    /**************************************************************************
     * Checks out a business object. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void checkOut (FunctionValues values)
    {
        BusinessObject obj;             // the business object

        if ((obj = values.getObject ()) != null)
        {
            // execute the function:
            try
            {
                BusinessObject tempObj = obj.checkOut ();
                // if no error has occurred, display object
                this.showObject (values, tempObj.oid, false);
            } // try
            catch (NoAccessException e) // no access to object allowed
            {
                obj.showNoAccessMessage (Operations.OP_CHANGE);
            } // catch
        } // if
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else didn't get object
    } // checkOut


    /**************************************************************************
     * The method displays the edit form first. Then it makes a new copy
     * of the object and checks in the old one. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void editBeforeCheckIn (FunctionValues values)
    {
        BusinessObject obj;             // the business object

        if ((obj = values.getObject ()) != null)
        {
            // execute the function:
            try
            {
                obj.editBeforeCheckIn (AppFunctions.FCT_CHANGECHECKIN);
            } // try
            catch (NoAccessException e) // no access to object allowed
            {
                obj.showNoAccessMessage (Operations.OP_CHANGE);
            } // catch
        } // if
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else didn't get object
    } // editBeforeCheckIn


    /**************************************************************************
     * Checks in a business object. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void checkIn (FunctionValues values)
    {
        BusinessObject obj;             // the business object

        if ((obj = values.getObject ()) != null)
        {
            // execute the function:
            try
            {
                BusinessObject tempObj = obj.checkIn ();
                tempObj.displayTabs = true;

                // check if the object shall be added to the history:
                // if the object is a tab it shall not be added to the history.
                if (!obj.isTab ())      // object shall be added to history?
                {
                    // add the object to the history:
                    values.getUserInfo ().history.add
                        (values.p_oid, TabConstants.TAB_NONE, obj.name, obj.icon, obj.typeName);
                } // if object shall be added to history

                // show the object:
                tempObj.show (UtilConstants.REP_STANDARD);
            } // try
            catch (NoAccessException e) // no access to object allowed
            {
                obj.showNoAccessMessage (Operations.OP_CHANGE);
            } // catch
        } // if
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else didn't get object
    } // checkIn


    /**************************************************************************
     * Changes a business object and checks it in. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void changeCheckIn (FunctionValues values)
    {
        // first change the object:
        boolean showObjectAfterNew = values.p_sess.p_showObjAfterNew;
        values.p_sess.p_showObjAfterNew = false;
        this.objectChange (values.p_oid, values);
        values.p_sess.p_showObjAfterNew = showObjectAfterNew;

        // then check it in:
        this.checkIn (values);
    } // changeCheckIn


    /**************************************************************************
     * The method displays the edit form first. Then it makes a new copy
     * of the object and checks in the old one. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void editBeforeCheckInContainer (FunctionValues values)
    {
        BusinessObject obj;             // the business object

        if ((obj = values.getObject ()) != null)
        {
            // execute the function:
            try
            {
                if (obj instanceof Container)
                {
                    ((Container) obj).editBeforeCheckInContainer (
                        AppFunctions.FCT_CHANGECHECKINCONTAINER);
                } // if obj is a container
            } // try
            catch (NoAccessException e) // no access to object allowed
            {
                obj.showNoAccessMessage (Operations.OP_CHANGE);
            } // catch
        } // if
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else didn't get object
    } // editBeforeCheckInContainer


    /**************************************************************************
     * The method displays the edit form first. Then it makes a new copy
     * of the object and checks in the old one. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void checkInContainer (FunctionValues values)
    {
        BusinessObject obj;             // the business object

        if ((obj = values.getObject ()) != null)
        {
            // execute the function:
            try
            {
                // perform check in and display the object:
                this.showObject (values, obj.checkInReturnDisplayableObject (),  true);
            } // try
            catch (NoAccessException e) // no access to object allowed
            {
                obj.showNoAccessMessage (Operations.OP_CHANGE);
            } // catch
        } // if
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else didn't get object
    } // checkInContainer


    /**************************************************************************
     * Changes a business object and checks it in within the container. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void changeCheckInContainer (FunctionValues values)
    {
        // first change the object:
        boolean showObjectAfterNew = values.p_sess.p_showObjAfterNew;
        values.p_sess.p_showObjAfterNew = false;
        this.objectChange (values.p_oid, values);
        values.p_sess.p_showObjAfterNew = showObjectAfterNew;

        // then check it in:
        this.checkInContainer (values);
    } // changeCheckInContainer


    /**************************************************************************
     * Check an element out of the container. Which element that is determines
     * the container itself. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void checkOutContainer (FunctionValues values)
    {
        BusinessObject obj;             // the business object

        if ((obj = values.getObject ()) != null)
        {
            // execute the function:
            try
            {
                // if a container object
                if (obj instanceof Container)
                {
                    BusinessObject checkOutObj = ((Container) obj).checkOut ();
                    // show the popup message
                    IOHelpers.showPopupMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                        BOMessages.ML_MSG_OBJECTCHECKEDOUT, new String[] {checkOutObj.name}, values.p_env),
                        null, values.p_app, values.p_sess, values.p_env);
                } // if object is a container

            // if no error has occurred, display object
                this.showObject (values, obj.oid, false);
            } // try
            catch (NoAccessException e) // no access to object allowed
            {
                obj.showNoAccessMessage (Operations.OP_CHANGE);
            } // catch
        } // if
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else didn't get object
    } // checkOutContainer


    /**************************************************************************
     * Checks out a business object to access wirh WebDAV <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void webdavcheckOut (FunctionValues values)
    {
        BusinessObject obj;             // the business object

        if ((obj = values.getObject ()) != null)
        {
            // execute the function:
            try
            {
                // checkOut the Object:
                BusinessObject tempObj = obj.webdavCheckOut ();

                // if no error has occurred, display object:
                this.showObject (values, tempObj.oid, false);
            } // try
            catch (NoAccessException e) // no access to object allowed
            {
                obj.showNoAccessMessage (Operations.OP_CHANGE);
            } // catch
        } // if
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else didn't get object
    } // webdavcheckOut


    /**************************************************************************
     * Checks in a business object thats checked out for WebDAV access. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void webdavcheckIn (FunctionValues values)
    {
        BusinessObject obj;             // the business object

        if ((obj = values.getObject ()) != null)
        {
            // execute the function:
            try
            {
                // checkIn Object:
                BusinessObject tempObj = obj.webdavCheckIn ();
                tempObj.displayTabs = true;

                // check if the object shall be added to the history:
                // if the object is a tab it shall not be added to the history.
                if (!obj.isTab ())      // object shall be added to history?
                {
                    // add the object to the history:
                    values.getUserInfo ().history.add
                        (values.p_oid, TabConstants.TAB_NONE, obj.name, obj.icon, obj.typeName);
                } // if object shall be added to history

                // Move File back from the WebDAV enabled os folder
                // to the m2 os file folder


                // show the object
                tempObj.show (0);
            } // try
            catch (NoAccessException e) // no access to object allowed
            {
                obj.showNoAccessMessage (Operations.OP_CHANGE);
            } // catch
        } // if
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else didn't get object
    } // webdavcheckIn


    /**************************************************************************
     * Shows a confirmation message for deleting the object. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void objectDeleteConfirm (FunctionValues values)
    {
        BusinessObject obj;             // the business object

        if ((obj = values.getObject ()) != null)
        {
            // delete the object:
            obj.showDeleteConfirmation (values.p_representationForm);
        } // if
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else didn't get object
    } // objectDeleteConfirm


    /**************************************************************************
     * Deletes the object from within the database. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void objectDelete (FunctionValues values)
    {
//trace ("IbsFunctionHandler.objectDelete BEGIN");
        BusinessObject obj;             // the business object
        OID upperOid;                   // oid of the upper object
        ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);
        boolean deleteOK = false;       // the return value of obj.delete

        if (values.p_oid.tVersionId == values.getTypeCache ().getTVersionId (TypeConstants.TC_Rights))
        {
            obj = values.getUserInfo ().actRightsObject;
            obj.initObject (obj.oid, values.getUser (), values.p_env, values.p_sess, values.p_app);
        } // if
        else
        {
            obj = values.getObject ();
        } // else

        if (obj != null) // object ready?
        {
            // get the nearest upper object in the hierarchy:
            try
            {
                // get the oid of the next object:
                upperOid = obj.getUpperOid ();
            } // try
            catch (NoMoreElementsException e)
            {
                // show message to user:
                IOHelpers.showPopupMessage (MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                    AppMessages.ML_MSG_TOPLEVEL, values.p_env),
                    null, values.p_app, values.p_sess, values.p_env);
                return;                 // terminate method
            } // catch
            catch (NoAccessException e) // no access to objects allowed
            {
                // send message to the user:
                obj.showNoAccessPopupMessage (Operations.OP_VIEW);
                return;                 // terminate method
            } // catch
            catch (ObjectNotFoundException e) // object not found
            {
                // send message to the user:
                IOHelpers.showPopupMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                    null, values.p_app, values.p_sess, values.p_env);
                return;                 // terminate method

            } // catch

            // delete the object:
            deleteOK = obj.delete (values.p_representationForm);

            if (deleteOK)               // was the delete performed without
                                        // troubles ?
            {
                // ensure that no deleted object is marked for copying:
                values.getUserInfo ().copiedOids.removeElement (obj.oid);

                if (values.getObject (upperOid) != null) // object ready?
                {
                    // generate code to show the object:
                    // show tabs of the upper object and the upper object:
                    script.addScript (IOHelpers.getShowObjectJavaScript ("" + upperOid));

                    IOHelpers.showPopupMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                        BOMessages.ML_MSG_OBJECTDELETED, values.p_env),
                        script, values.p_app, values.p_sess, values.p_env);
                } // if object ready
                else                        // object not ready
                {
                    // upper object not available -> just show a message:
                    IOHelpers.showPopupMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                        BOMessages.ML_MSG_OBJECTDELETED, values.p_env),
                        null, values.p_app, values.p_sess, values.p_env);
                } // else object not ready

///////////////////////////
//
//  DOCUMENTTEMPLATE HACK: START
//
                // Starting with version 2.2 a DocumentTemplate defines a regular m2 type.
                // If a DocumentTemplate is deleted the type cache
                // must be reloaded to reflect this changes.
                if (values.p_oid.type == values.getTypeCache ().getTypeId (TypeConstants.TC_DocumentTemplate))
                {
                    try
                    {
                        values.p_app.p_appInitializer
                            .reloadTypes (new ApplicationContext (values.p_app,
                                values.p_sess, values.p_env), false);
                    } // try
                    catch (ApplicationInitializationException e)
                    {
                        // TODO: handle exception
                    } // catch
                } // if type == DocumentTemplate
//
//  DOCUMENTTEMPLATE HACK: END
//
///////////////////////////


/* KR not necessary
///////////////////////////
//
// AJ HACK: UPDATE QUERYCREATOR IN QUERYPOOL START
//
            // if a querycreator is changed, the queryPool has to be updated
            if (oid.type == getCache ().getTypeId (TypeConstants.TC_QueryCreator))
            {
trace ("IbsFunctionHandler. deleteObject before deleteQuery oid = " + oid);
                this.app.queryPool.deleteQuery (oid);
            } // if type == QueryCreator
//
// AJ HACK: UPDATE QUERYCREATOR IN QUERYPOOL END
//
///////////////////////////
*/


            } // if was the delete performed without troubles
            else                            // object was not deleted
            {
                // display the info of the object
                this.showObjectInfo (values);
            } // else object was not deleted
        } // if object ready
        else                            // object not ready
        {
            IOHelpers.showPopupMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTNOTDELETED, values.p_env),
                null, values.p_app, values.p_sess, values.p_env);
        } // else object not ready
    } // objectDelete


    /**************************************************************************
     * Distribute an object. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void objectDistribute (FunctionValues values)
    {
        // get object oid and display the object:
        if (this.useMultipleOperation (values))
                                        // there are more elements to
                                        // distribute?
        {
            UserInfo userInfo = values.getUserInfo ();
            userInfo.inContentView = false;
            userInfo.distributeContainerId = values.p_oid;
            this.showDistributeSelForm (values);
        } // if there are more elements to distribute
        else                            // there is one element to distribute
        {
            // create a notify function object:
            NotifyFunction nf = new NotifyFunction ();
            // initialize function with current oid (is used for
            // notification):
            nf.initFunction (AppFunctions.FCT_NOTIFICATION, values.getUser (),
                values.p_env, values.p_sess, values.p_app);
            // call sequence control of function:
            nf.start ();
        } // else there is one element to distribute
    } // objectDistribute


    /**************************************************************************
     * Generates a CleanContainer and calls its showDeleteForm method
     * to generate a list off all objects that exceeded their valid until
     * dates and the user has delete rights for. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void showClean (FunctionValues values)
    {
        Container obj;                  // the business object

        // stores that the system is in a deleteform
        if ((obj = (Container) values.getNewObject (TypeConstants.TC_CleanContainer)) != null)
        {
            // show the clean expired objects form
            obj.showDeleteSelForm (values.p_representationForm, AppFunctions.FCT_OBJECTCLEANFORM);
        } // if
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else didn't get object
    } // showClean


    /**************************************************************************
     * Clean expired Objects. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void objectClean (FunctionValues values)
    {
        BusinessObject obj;             // the business object

        if ((obj = values.getObject ()) != null)
        {
            // perform cleaning expired objects
            obj.clean (values.p_representationForm);
        } // if
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else didn't get object
    } // objectClean


    /**************************************************************************
     * Performs the simple search. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    private void performObjectSimpleSearch (FunctionValues values)
    {
        BusinessObject obj = null;      // the business object

        // creates an dummy simple search container object:
        if ((obj = values.getNewObject (TypeConstants.TC_SimpleSearchContainer)) != null)
                                        // got the object?
        {
            // display the object:
            obj.show (values.p_representationForm);
        } // if got the object
    } // performObjectSimpleSearch


    /**************************************************************************
     * Display the actual function. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void display (FunctionValues values)
    {
        Page page = new Page (false);

        // set the document's base:
        IOHelpers.setBase (page, values.p_app, values.p_sess, values.p_env);

        page.body.addElement (new TextElement ("" + values.p_function));
        try
        {
            page.build (values.p_env);
            page = null;
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e.getMsg (),
                                   values.p_app, values.p_sess, values.p_env);
            page = null;
        } // catch
    } // display


    /**************************************************************************
     * Copy a business object within the actual context. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    public void objectCut (FunctionValues values)
    {
        UserInfo userInfo = values.getUserInfo ();
        userInfo.copiedOids.removeAllElements ();
        userInfo.copiedOids.addElement (values.p_oid);
        userInfo.copiedType = AppConstants.CT_CUT;
        userInfo.markedType = values.p_oid.tVersionId;

        IOHelpers.showPopupMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
            BOMessages.ML_MSG_OBJECTCUT, values.p_env),
            null, values.p_app, values.p_sess, values.p_env);
    } // objectCut


    /**************************************************************************
     * Copy a business object within the actual context. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    public void showLogContent (FunctionValues values)
    {
        Date dat = null;
        int num = 0;
        UserInfo userInfo = values.getUserInfo ();

        // get startLogDate:
        if ((dat = values.p_env.getDateParam (BOArguments.ARG_STARTSHOWDATE)) != null)
        {
            userInfo.startLogDate = dat;
        } // if

        // get endLogDate:
        if ((dat = values.p_env.getDateParam (BOArguments.ARG_ENDSHOWDATE)) != null)
        {
            userInfo.endLogDate = dat;
        } // if

        // get kind of LogEntry all, create, change, delete:
        if ((num = values.p_env.getIntParam (BOArguments.ARG_KINDOFLOG)) !=
            IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            userInfo.logTypeEntry = num;
        } // if

        // get show partOfObjekts BOArguments.ARG_PARTOF:
        if ((num = values.p_env.getBoolParam (BOArguments.ARG_PARTOF)) >= IOConstants.BOOLPARAM_FALSE)
        {
            userInfo.showPartOf = num == IOConstants.BOOLPARAM_TRUE;
        } // if
        else
        {
            userInfo.showPartOf = false;
        } // else

        // create the output:
        IOHelpers.processJavaScriptCode (IOHelpers.getShowObjectJavaScript ("" +
            values.p_oid), values.p_app, values.p_sess, values.p_env);
    } // showLogContent


    /**************************************************************************
     * Copy a business object within the actual context. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    public void objectCopy (FunctionValues values)
    {
        BusinessObject obj;             // the business object
        UserInfo userInfo = values.getUserInfo ();

        userInfo.copiedOids.removeAllElements ();
        userInfo.copiedOids.addElement (values.p_oid);
        userInfo.isCopyLinkAllowed = false;

        if ((obj = values.getObject ()) != null) // got the object?
        {
            userInfo.isCopyLinkAllowed =
                obj.checkObjectRights (values.p_oid, values.getUser ().id, Operations.OP_CREATELINK);
        } // if got the object
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else didn't get object

        userInfo.isObjectMarked = true;
        userInfo.copiedType = AppConstants.CT_COPY;
        userInfo.markedType = values.p_oid.tVersionId;

        IOHelpers.showPopupMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
            BOMessages.ML_MSG_OBJECTCOPY, values.p_env),
            null, values.p_app, values.p_sess, values.p_env);
    } // objectCopy


    /**************************************************************************
     * Paste a Businessobject in the new context. You make a Cut-Paste or a
     * Copy-Paste. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void performReference (FunctionValues values)
    {
        OID copiedId;              // oid of the new business object

        copiedId = values.getUserInfo ().copiedOids.firstElement ();

        this.createReference (values, copiedId, values.p_oid);
    } // performReference


    /*************************************************************************
     * PerformCopy copy a Businessobject in the target Object. <BR/>
     * The target is the current object.
     *
     * @param   values      The values for the function handler.
     */
    protected void performCopy (FunctionValues values)
    {
        BusinessObject targetObj = null; // the newly created business object
        BusinessObject copiedObj = null;
        OID copiedId;                   // oid of the new business object
        short copiedType;
        UserInfo userInfo = values.getUserInfo ();

        // is it a physical object or a virtual
        if (!userInfo.copiedOids.isEmpty ())
        {
            // a physical object has been copied. get the oid
            copiedId = userInfo.copiedOids.firstElement ();
            // and the object
            copiedObj = values.getObject (copiedId);
        } // if (!this.sess.userInfo.copiedOids.isEmpty ())
        else    // a virtual object must have been copied
        {
            // check if we have any copied virtual objects
            if (!userInfo.copiedObjects.isEmpty ())
            {
                Enumeration<BusinessObject> enumList =
                    userInfo.copiedObjects.elements ();
                copiedObj = enumList.nextElement ();
            } // if
        } // a virtual object must have been copied

        copiedType = userInfo.copiedType;
        targetObj = values.getObject ();

        if (copiedType == AppConstants.CT_CUT) // cut -> paste?
        {
            // create target and copied object:
            if ((targetObj != null) && (copiedObj != null))
                // objects created correctly?
            {
                copiedObj.move (values.p_oid,
                                values.p_representationForm, targetObj);

                // every object can be moved just once
                userInfo.copiedOids.removeAllElements ();

                targetObj.show (values.p_representationForm);

            } // if objects created correctly
            else                        // no target business object available
            {
                // show corresponding message:
                IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_ONEOBJECTISTNOTHERE, values.p_env),
                    values.p_app, values.p_sess, values.p_env);
            } // else no target business object available
        } // if cut -> paste
        else if (copiedType == AppConstants.CT_COPY) // copy -> paste?
        {
            // create target and copied object:
            if ((targetObj == null) || (copiedObj == null))
                // no target business object is available?
            {
                // show corresponding message:
                IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_OBJECTNOTCREATED, values.p_env),
                    values.p_app, values.p_sess, values.p_env);
            } // if no target business object is available
            else                        // make a link or a realcopy
            {
                // check if we have to copy a physical object
                if (copiedObj.isPhysical)
                {
                    OID newRootOid = null;
                    newRootOid = copiedObj.copy (values.p_oid, values.p_representationForm, targetObj);
                    if (newRootOid == null)
                    {
                        IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                            BOMessages.ML_MSG_OBJECTCOPYFAIL, values.p_env),
                            values.p_app, values.p_sess, values.p_env);
                    } // if
                } // if (copiedObj.isPhysical)
                else    // copy a virtual object
                {
                    // create an objectTransformer service object
                    ObjectTransformer objectTransformer =
                            new ObjectTransformer (values.getUser (), values.p_env,
                                                   values.p_sess, values.p_app);
                    // loop through the objects
                    try
                    {
                        // transform the object
                        objectTransformer.transform (copiedObj,
                                copiedObj.getTypeTranslator (), targetObj.oid);
                    } // try
                    catch (ObjectTransformationException e)
                    {
                        IOHelpers.showMessage (e, values.p_app, values.p_sess, values.p_env);
                    } // catch (ObjectTransformationException e)
                } // copy a virtual object
                targetObj.show (values.p_representationForm);
            } // else
        } // else if copy -> paste
    } // performCopy


    /**************************************************************************
     * Create a reference from an object to an other. <BR/>
     *
     * @param   values      The values for the function handler.
     * @param   referencedId    The oid of the object to create a reference on.
     * @param   targetId        The oid of the object which shall get the
     *                          reference.
     */
    protected void createReference (FunctionValues values, OID referencedId, OID targetId)
    {
        BusinessObject targetObj = null; // the target business object
        OID oid;                        // oid of the new business object

        // get target object:
        if ((targetObj = values.getObject (targetId)) != null)
                                        // got object?
        {
            oid = targetObj.addReference (referencedId);

            if (oid != null) // reference created successfully?
            {
                if (targetObj.oid.tVersionId ==
                    values.getTypeCache ().getTVersionId (TypeConstants.TC_User))
                {
                    // create an personSearchContainer
                    targetObj =
                        values.getNewObject (TypeConstants.TC_PersonSearchContainer);
                } // if

                if (!values.p_sess.wizardRegistration)
                {
                    if (oid != null) // object was created, oid returned?
                    {
                        targetObj.show (values.p_representationForm);
                    } // if object was created, oid returned
                    else        // object not created
                    {
                        // show corresponding message:
                        IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                            BOMessages.ML_MSG_OBJECTNOTCREATED, values.p_env),
                            values.p_app, values.p_sess, values.p_env);
                    }  // else object not created
                } // if
            } // if reference created successfully
        } // if got object
        else                            // no target business object available
        {
            // show corresponding message:
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                 BOMessages.ML_MSG_OBJECTNOTCREATED, values.p_env),
                 values.p_app, values.p_sess, values.p_env);
        } // else no target business object available
    } // createReference


    /**************************************************************************
     * Create a reference from an object to an other. <BR/>
     * This method is used for button 'search and reference'.
     *
     * @param referencedId  oid of object to be referenced.
     * @param targetId      oid of object in which referencetab the ref
     *                      should be created
     * @param   values      The values for the function handler.
     *
     * @return OID of created Link or null.
     */
    protected OID createLinkTabReference (OID referencedId, OID targetId,
                                          FunctionValues values)
    {
//debug ("Application.createLinkTabReference (" + referencedId + ", "
//                                                + targetId + ")");
        BusinessObject targetObj = null;
        BusinessObject refObj = null;
        Tab refTab = null;
        OID refOid = null;          // oid of created reference
        OID referenceTabOid = null; // oid of referencetab

        if ((targetObj = values.getObject (targetId)) != null)
        {
            refTab = values.getTabCache ().find (0, TabConstants.TC_REFERENCES);

            try
            {
                refTab = targetObj.getTabObject (refTab.getIdInt ());
            } // try
            catch (NoAccessException e)
            {
                // show corresponding message:
                IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_OBJECTNOTCREATED, values.p_env),
                    values.p_app, values.p_sess, values.p_env);
                return null;
            } // catch
            catch (ObjectNotFoundException e)
            {
                // show corresponding message:
                IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_OBJECTNOTCREATED, values.p_env),
                    values.p_app, values.p_sess, values.p_env);
                return null;
            } // catch

            referenceTabOid = refTab.getOid ();

            // create reference in referencetab
            if ((refObj = values.getNewObject (TypeConstants.TC_Reference)) != null)
                                    // object ready?
            {
                // set containerId as the oid of the target
                refObj.containerId = referenceTabOid;
                refObj.isLink = true;
                refObj.linkedObjectId = referencedId;
                refObj.name = values.getTypeCache ().getTypeName (TypeConstants.TC_Reference);

                // create the new object:
                refOid = refObj.createActive (values.p_representationForm);

//debug ("AJ Application.createLinkTabReference  refOid = " + refOid);
                return refOid;
            } // if object ready

            // object not created
            // show corresponding message:
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTNOTCREATED, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // if
        else
        {
            // show corresponding message:
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTNOTCREATED, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else

        return null;
    } // createLinkTabReference


    /**************************************************************************
     * Create reference on a just created object in referencetab of
     * sourceobject. <BR/>
     * This method is used for button 'new and reference'.
     *
     * @param   values      The values for the function handler.
     */
    protected void newAndReference (FunctionValues values)
    {
        OID targetId = (OID) values.p_sess.p_sourceObjectOid;

//debug ("Application.newAndReference (" + values.p_oid + ", "
//                                       + targetId + ")");
        this.createLinkTabReference (values.p_oid, targetId, values);

        // check if the object shall be displayed after creating the link:
        if (!values.p_sess.p_showObjAfterNew) // don't display the object?
        {
            this.goBack (values, 0);
        } // if don't display the object
    } // newAndReference


    /**************************************************************************
     * Deletes  all selected Businessobjects and display the previous Container.
     * <BR/>
     *
     * @param   values      The values for the function handler.
     */
    public void deleteSelectedElements (FunctionValues values)
    {
        BusinessObject obj; // the business object
        //  String[] oidString;
        Container con;

//debug ("Bin im deleteSelectedElements aus dem Container mit der oid = " + values.p_oid);
        if ((con = (Container) values.getObject ()) != null)
        {
//debug ("Bin im deleteSelectedElements nach  ConObj  (oid)) != null  " + con);
            String[] oidString = con.getSelectedElements ();
            if (oidString != null)
            {
//HACK!!!
                if (con.oid.tVersionId == 0x010100b1) // listdelete in Group
                {
                    Group_01 group = (Group_01) con;

                    for (int i = 0; i < oidString.length; i++)
                    {
                        try
                        {
                            OID temp = new OID (oidString[i]);
                            group.deleteLink (temp);
                        } // try
                        catch (IncorrectOidException o)
                        {
                            // no valid oid => show corresponding message:
                            this.showIncorrectOidMessage (values, oidString[i]);
                        } // catch
                    } // for
                } // if
                else
                {
                    // create a CheckReferenceContainer for working on the
                    // references:
                    CheckReferenceContainer refCont =
                        new CheckReferenceContainer ();
                    refCont.initObject (values.p_oid, values.getUser (), values.p_env, values.p_sess, values.p_app);
                    int refCount =
                        refCont.checkReferences (this.createOidVector (oidString),
                                                 Operations.OP_DELETE);

                    if (refCount == 0)  // no references found?
                    {
                        for (int i = 0; i < oidString.length; i++)
                        {
                            if ((obj = values.getObject (oidString[i])) != null)
                            {
                                // show the clean expired objects form
                                obj.delete (values.p_representationForm);
                                // ensure that no deleted object is marked for copying:
                                values.getUserInfo ().copiedOids.removeElement (obj.oid);

/* KR not necessary
///////////////////////////
//
// AJ HACK: UPDATE QUERYCREATOR IN QUERYPOOL START
//
                                // if a querycreator is deleted, the queryPool
                                // has to be updated
                                if (obj.oid.type ==
                                    getCache ().getTypeId (TypeConstants.TC_QueryCreator))
                                {
                                    this.app.queryPool.deleteQuery (obj.oid);
                                } // if type == QueryCreator
//
// AJ HACK: UPDATE QUERYCREATOR IN QUERYPOOL END
//
///////////////////////////
*/
                            } // if
                            else
                            {
                                // didn't get object
                                IOHelpers.showMessage (MultilingualTextProvider
                                    .getMessage (BOMessages.MSG_BUNDLE, 
                                        BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                                        values.p_app, values.p_sess, values.p_env);
                            } // else
                        } // for
                    } // if no references found
                    else                // references found
                    {
                        return;         // nothing else to do
                    } // else references found
                } // if

            } // if (oidString != null)

            con.show (values.p_representationForm);
///////////////////////////
//
//  DOCUMENTTEMPLATE HACK: START
//
            // Starting with version 2.2 a DocumentTemplate defines a regular m2 type.
            // If a DocumentTemplate is deleted the type cache
            // must be reloaded to reflect this changes.
            if (values.p_oid.type == values.getTypeCache ().getTypeId (TypeConstants.TC_DocumentTemplateContainer))
            {
                try
                {
                    values.p_app.p_appInitializer
                        .reloadTypes (new ApplicationContext (values.p_app,
                            values.p_sess, values.p_env), false);
                } // try
                catch (ApplicationInitializationException e)
                {
                    // TODO: handle exception
                } // catch
            } // if type == DocumentTemplate
//
//  DOCUMENTTEMPLATE HACK: END
//
///////////////////////////
        } // if ((con = (Container) values.getObject ()) != null)
    } // deleteSelectedElements


    /**************************************************************************
     * Undeletes  all selected Businessobjects and display the previous Container.
     * <BR/>
     *
     * @param   values      The values for the function handler.
     */
    public void undeleteSelectedElements (FunctionValues values)
    {
        BusinessObject obj; // the business object
        OID objOid = null;

        //  String[] oidString;
        Container con;

//debug ("Bin im undeleteSelectedElements aus dem Container mit der oid = " + values.p_oid);
        // create and retrieve container object
        if ((con = (Container) values.getObject ()) != null)
        {
//debug ("Bin im undeleteSelectedElements nach  ConObj  (oid)) != null  " + con);
            String[] oidString = con.getSelectedElements ();
            for (int i = 0; i < oidString.length; i++)
            {
                // create oid for business object
                try
                {
                    objOid = new OID (oidString[i]);
                } // try
                catch (IncorrectOidException e)
                {
                    // OID can NOT be invalid - ignore exception
                } // catch

                // create businessobject
                // remark: do not use getObject (OID), because this method
                //         performs a retrieve; retrieve on deleted objects
                //         is NOT POSSIBLE
                obj = values.getNewObject (objOid.tVersionId);
                obj.setOid (objOid);
//debug ("IbsFunctionHandler.undeleteSelectedElements: " + oidString[i]);

                obj.undelete (values.p_representationForm);
            } // for i
        } // if ((con = (Container) values.getObject ()) != null)

        // show the container after undeletion
        con.show (values.p_representationForm);
    } // undeleteSelectedElements



    /**************************************************************************
     * Copies all selected Business objects and displays the previous Container.
     * <BR/>
     *
     * @param   values      The values for the function handler.
     */
    public void copySelectedElements (FunctionValues values)
    {
        BusinessObject obj; // the business object
        Container con;
        UserInfo userInfo = values.getUserInfo ();

        if ((con = (Container) values.getObject ()) != null)
        {
            String[] oidString = con.getSelectedElements ();
            if (oidString != null)
            {
                userInfo.copiedOids.removeAllElements ();
                userInfo.isCopyLinkAllowed = false;

                for (int i = 0; i < oidString.length; i++)
                {
                    if ((obj = values.getObject (oidString[i])) != null)
                    {
                        userInfo.copiedOids.addElement (obj.oid);
                        if (!userInfo.isCopyLinkAllowed)
                        {
                            userInfo.isCopyLinkAllowed =
                                obj.checkObjectRights (obj.oid, values.getUser ().id,
                                                       Operations.OP_CREATELINK);
                        } // if

                        userInfo.markedType = obj.oid.tVersionId;
                    } // if
                } // for

                userInfo.isObjectMarked = true;
                userInfo.copiedType = AppConstants.CT_COPY;

                IOHelpers.showPopupMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_OBJECTSCOPY, values.p_env),
                    null, values.p_app, values.p_sess, values.p_env);
            } // if
        } // if
        con.show (values.p_representationForm);
    } // copySelectedElements


    /**************************************************************************
     * Cuts all selected Business objects and displays the previous Container.
     * <BR/>
     *
     * @param   values      The values for the function handler.
     */
    public void cutSelectedElements (FunctionValues values)
    {
        BusinessObject obj = null; // the business object
        Container con;
        UserInfo userInfo = values.getUserInfo ();

        if ((con = (Container) values.getObject ()) != null)
        {
            String[] oidString = con.getSelectedElements ();
            if (oidString != null)
            {
                userInfo.copiedOids.removeAllElements ();

                for (int i = 0; i < oidString.length; i++)
                {
                    if ((obj = values.getObject (oidString[i])) != null)
                    {
                        userInfo.copiedOids.addElement (obj.oid);
                    } // if
                } // for

                userInfo.isObjectMarked = true;
                userInfo.copiedType = AppConstants.CT_CUT;
                if (obj != null)
                {
                    userInfo.markedType = obj.oid.tVersionId;
                } // if

                IOHelpers.showPopupMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_OBJECTSCUT, values.p_env),
                    null, values.p_app, values.p_sess, values.p_env);
            } // if
        } // if
        con.show (values.p_representationForm);
    } // cutSelectedElements


    /**************************************************************************
     * Paste all selected Business objects. <BR/>
     * The actual object is used as target.
     *
     * @param   values      The values for the function handler.
     */
    public void pasteSelectedElements (FunctionValues values)
    {
        BusinessObject targetObj = null; // the newly created business object
        BusinessObject copiedObj = null;
        OID copiedId;                   // oid of the new business object
        short copiedType;
        OID[] selectedOids = null;
        BusinessObject[] selectedObjects = null;
        OID targetOid = values.p_oid;

        // get the target object
        targetObj = values.getObject (targetOid);
        // get the type of paste operation
        copiedType = values.getUserInfo ().copiedType;

        // get the list with the oids selected
        selectedOids = this.getSelectedOIDs (values, targetOid);
        // any real objects selected?
        if (selectedOids != null)
        {
            for (int i = 0; i < selectedOids.length; i++)
            {
                copiedId   = selectedOids[i];
                if (copiedType == AppConstants.CT_CUT) // cut -> paste?
                {
                    // create target and copied object:
                    if ((targetObj != null) && // ||
                        ((copiedObj = values.getObject (copiedId)) != null))
                        // objects created correctly?
                    {
                        copiedObj.move (targetOid,
                                        values.p_representationForm, targetObj);
                        values.getUserInfo ().copiedOids.removeElement (copiedObj.oid);
                    } // if objects created correctly
                    else                        // no target business object available
                    {
                        // show corresponding message:
                        IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                            BOMessages.ML_MSG_ONEOBJECTISTNOTHERE, values.p_env),
                            values.p_app, values.p_sess, values.p_env);
                    } // else no target business object available
                } // if cut -> paste
                else if (copiedType == AppConstants.CT_COPY) // copy -> paste?
                {
                    // create target and copied object:
                    if ((targetObj == null) ||
                        ((copiedObj = values.getObject (copiedId)) == null))
                        // no target business object is available?
                    {
                        // show corresponding message:
                        IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                            BOMessages.ML_MSG_OBJECTNOTCREATED, values.p_env),
                            values.p_app, values.p_sess, values.p_env);
                    } // if no target business object is available
                    else                        // make a link or a realcopy
                    {
                        OID newRootOid = null;
                        newRootOid = copiedObj.copy (targetOid, values.p_representationForm, targetObj);
                        if (newRootOid == null)
                        {
                            IOHelpers.showMessage (MultilingualTextProvider
                                .getMessage (BOMessages.MSG_BUNDLE,
                                BOMessages.ML_MSG_OBJECTCOPYFAIL, values.p_env),
                                values.p_app, values.p_sess, values.p_env);
                        } // if
                    } // else
                } // else if copy -> paste
            } // for
        } // if (selectedOids != null)

        // are there virtual objects to be pasted?
        selectedObjects = this.getSelectedObjects (values);
        if (selectedObjects != null)
        {
            // create an objectTransformer service object
            ObjectTransformer objectTransformer =
                    new ObjectTransformer (values.getUser (), values.p_env, values.p_sess, values.p_app);
            // loop through the objects
            for (int i = 0; i < selectedObjects.length; i++)
            {
                try
                {
                    // transform the object
                    objectTransformer.transform (selectedObjects [i],
                            selectedObjects[i].getTypeTranslator (), targetOid);
                } // try
                catch (ObjectTransformationException e)
                {
                    IOHelpers.showMessage (e, values.p_app, values.p_sess, values.p_env);
                } // catch (ObjectTransformationException e)
            } // for ( int i = 0; i < selectedObjects.length; i++)
        } // if (selectedObjects != null)

        targetObj.show (values.p_representationForm);
    } // pasteSelectedElements


    /**************************************************************************
     * Forward all selected business objects. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    public void forwardSelectedElements (FunctionValues values)
    {
        BusinessObject obj;
        OID objOid = null;
        Container con;
        boolean errors = false;
        ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);

        // create and retrieve container object
        if ((con = (Container) values.getObject ()) != null)
        {
            // iterate through selected oids, get and forward them!
            String[] oidString = con.getSelectedElements ();

            // check if object exists
            if (oidString == null)
            {
                // no forwarding; show container
                try
                {
                    script.addScript ("top.goback (0);");
                    StringBuffer buf = new StringBuffer ();
                    script.build (values.p_env, buf);
                    values.p_env.write (buf.toString ());
                } // try
                catch (BuildException e)
                {
                    // nothing to do
                } // catch

                // exit
                return;
            } // if

            // iterate through selected items
            for (int i = 0; i < oidString.length; i++)
            {
                // create oid for business object
                try
                {
                    objOid = new OID (oidString[i]);
                } // try
                catch (IncorrectOidException e)
                {
                    // OID can NOT be invalid - ignore exception
                } // catch

                // get businessobject
                obj = values.getObject (objOid);

                // ... and now, ladies & gentlemen, the object will be
                //     forwarded ...

                // show Object name
                values.p_env.write (IE302.TAG_NEWLINE + "<P>" + obj.name);

                // create workflow-service
                WorkflowService wfservice = null;
                try
                {
                    wfservice
                        = new WorkflowService (values.getUser (), values.p_env,
                                               values.p_sess, values.p_app);
                } // try
                catch (ObjectInitializeException e)
                {
                    // workflow could not be started
                    values.p_env.write (IE302.TAG_NEWLINE + "<P>" + 
                        MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                            WorkflowMessages.ML_MSG_CREATION_FAILED, values.p_env) +
                        " (WorkflowService)");
                    // indicate error!
                    errors = true;
                    // jump to next iteration
                    continue;
                } // catch

                // forward the given object
                try
                {
                    if (!wfservice.forward (obj))
                    {
                        // show basic failure message
                        values.p_env.write (IE302.TAG_NEWLINE + "<P>" + 
                            MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                                WorkflowMessages.ML_MSG_OBJECT_NOT_FORWARDED, values.p_env));
                        // indicate error!
                        errors = true;
                        // jump to next iteration
                        continue;
                    } // else
                } // try
                catch (UserInteractionRequiredException e)
                {
                    // workflow could not be started
                    values.p_env.write (IE302.TAG_NEWLINE + "<P>" + 
                        MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                            WorkflowMessages.ML_MSG_OBJECT_NOT_FORWARDED, values.p_env) +
                        " (UserInteractionRequired)");
                    // indicate error!
                    errors = true;
                    // jump to next iteration
                    continue;
                } // catch
            } // for i
        } // if ((con = (Container) values.getObject ()) != null)

        // show alert! then go back to container
        if (errors)
        {
            script.addScript ("alert (\"" + 
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_OBJECTS_NOT_FORWARDED, values.p_env) + "\"); top.goback (0);");
        } // if
        else
        {
            script.addScript ("alert (\"" + 
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_OBJECT_FORWARDED, values.p_env) + "\"); top.goback (0);");
        } // else

        // build the page and show it to the user:
        try
        {
            StringBuffer buf = new StringBuffer ();
            script.build (values.p_env, buf);
            values.p_env.write (buf.toString ());
        } // try
        catch (BuildException e)
        {
            // nothing to do
        } // catch
    } // forwardSelectedElements


    /**************************************************************************
     * Create a vector out of a string array containing just oids in string
     * representation. <BR/>
     *
     * @param   oidStrings  The array with the oid strings.
     *
     * @return  The vector containing the oids or <CODE>null</CODE> if there
     *          where no oids within the list.
     */
    public Vector<OID> createOidVector (String[] oidStrings)
    {
        OID oid = null;                 // the actual oid
        Vector<OID> oids = null;        // the oid vector

        // check if there are any oids defined:
        if (oidStrings != null)         // array exists?
        {
            // prepare an oid list for all selected elements
            oids = new Vector<OID> (oidStrings.length);
            for (int i = 0; i < oidStrings.length; i++)
            {
                try
                {
                    // create the current oid:
                    oid = new OID (oidStrings[i]);

                    // add the oid to the vector:
                    oids.addElement (oid);
                } // try
                catch (IncorrectOidException e)
                {
                    // nothing to do, the oid shall not be added to the vector
                } // catch
            } // for
        } // if array exists

        // return the computed vector:
        return oids;
    } // createOidVector


    /**************************************************************************
     * Get all selected OIDs. <BR/>
     *
     * @param   values      The values for the function handler.
     * @param   targetId    Object id of the container where the objects
     *                      operation will be performed.
     *
     * @return  The oid array.
     */
    public OID[] getSelectedOIDs (FunctionValues values, OID targetId)
    {
        Container con;                  // the container for the oids
        OID[] selectedOids = null;      // the oids
        Vector<OID> theVector = null;   // the oid vector

        // get the container where the elements for paste were selected
        if ((con = (Container) values.getObject (targetId)) != null)
        {
            // get all selected elements from the container
            String[] oidString = con.getSelectedElements ();
            if (oidString != null)
            {
                // prepare an oid list for all selected elements
                theVector = this.createOidVector (oidString);
                // create the array:
                selectedOids = new OID[theVector.size ()];
                // fill the array:
                theVector.copyInto (selectedOids);
            } // if
        } // if

        // return the computed array:
        return selectedOids;
    } // getSelectedOIDs


    /**************************************************************************
     * Get all selected business objects. <BR/>
     *
     * @param   values      The values for the function handler.
     *
     * @return  The object array.
     */
    public BusinessObject[] getSelectedObjects (FunctionValues values)
    {
        BusinessObject[] selectedObjects = null;
        String[] selectedKeys = null;

        // get the indexes of all selected virtual objects
        selectedKeys = values.p_env.getMultipleParam (BOArguments.ARG_VIRTUALOBJECT);
        // did we got any
        if (selectedKeys != null && selectedKeys.length > 0)
        {
            // create the result
            selectedObjects = new BusinessObject [selectedKeys.length];
            for (int i = 0; i < selectedKeys.length; i++)
            {
                // insert the object from the vector of copied virtual objects
                // and insert it in the result array
                selectedObjects [i] =
                        values.getUserInfo ().copiedObjects.get (selectedKeys[i]);
            } // for (int i = 0; i < selectedIndexes.length; i++)
        } // if (selectedIndexes != null && selectedIndexes.length > 0)
        // return the computed array:
        return selectedObjects;
    } // getSelectedObjects



    /**************************************************************************
     * Add all selected Business objects as links. <BR/>
     * The actual object is used as the target object where the links are
     * pasted.
     *
     * @param   values      The values for the function handler.
     */
    public void addSelectedLinkElements (FunctionValues values)
    {
        OID referencedId;               // oid of the new business object
        BusinessObject obj = null;      // the newly created business objects
        BusinessObject targetObj = null; // the business object in which to
                                        // create the links
        OID[] selectedOids = null;
        boolean isReferenceContainer = false; // is the target object a
                                        // reference container?
        String referenceName = null;    // the name of a reference
        OID oid = null;
        OID targetOid = values.p_oid;

        // get the oids of the objects to be added:
        selectedOids = this.getSelectedOIDs (values, targetOid);

        // check if there are some objects to be handled:
        if (selectedOids == null)       // no objects to be added?
        {
            return;
        } // if no objects to be added

        // get the target object:
        if ((targetObj = values.getObject (targetOid)) != null)
                                        // target object created sucessfully?
        {
            // get the name for the reference:
            referenceName = values.getTypeCache ().getTypeName (TypeConstants.TC_Reference);

            // evaluate if the target object is a reference container:
            isReferenceContainer = targetObj.oid.type ==
                values.getTypeCache ().getTypeId (TypeConstants.TC_ReferenceContainer);

            // paste the links for all selected elements:
            for (int i = 0; i < selectedOids.length; i++)
            {
                referencedId = selectedOids[i];

                // check if the correct type of Object would be filled in the
                // target container or the traget container is a reference
                // container:
                if (targetObj.isAllowedType (referencedId.type) ||
                    isReferenceContainer)
                                        // type is allowed to be inserted?
                {
                    if ((obj = values.getNewObject (TypeConstants.TC_Reference)) != null)
                                        // object ready?
                    {
                        // set containerId as the oid of the target object:
                        obj.containerId = targetOid;
                        obj.isLink = true;
                        obj.linkedObjectId = referencedId;
                        //  set default name:
                        obj.name = referenceName;
                        // create the new object:
                        oid = obj.createActive (values.p_representationForm);
/* KR 011206: according to AJ not longer necessary
                        if (targetObj.oid.type !=
                            getCache ().getTypeId (TypeConstants.TC_ReferenceContainer))
                            // target is a referenceContainer?
                        {
                            if (targetObj.oid.tVersionId == getCache ().getTVersionId (TypeConstants.TC_User))
                            {
                                // create an personSearchContainer
                                targetObj = values.getNewObject (TypeConstants.TC_PersonSearchContainer);
                            } // if
                        }
*/
                    } // if object ready
                    else                // object not created
                    {
                        // show corresponding message:
                        IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                            BOMessages.ML_MSG_OBJECTNOTCREATED, values.p_env),
                            values.p_app, values.p_sess, values.p_env);
                    } // else object not created
                } // if type is allowed to be inserted
                else                    // not the correct type to insert
                {
                    // show corresponding message:
                    IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                        BOMessages.ML_MSG_OBJECTINSERTFAIL, values.p_env),
                        values.p_app, values.p_sess, values.p_env);
                } // else not the correct type to insert
            } // for

            if (!values.p_sess.wizardRegistration)
            {
                if (oid != null)        // object was created, oid returned?
                {
                    targetObj.show (values.p_representationForm);
                } // if object was created, oid returned
                else                    // object not created
                {
                    // show corresponding message:
                    IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                        BOMessages.ML_MSG_OBJECTNOTCREATED, values.p_env),
                        values.p_app, values.p_sess, values.p_env);
                }  // else object not created
            } // if
        } // if target object created sucessfully
    } // addSelectedLinkElements


    /**************************************************************************
     * Show the distribute form to determine the receivers of the selected
     * business objects. <BR/>
     * The actual object is used as the origin of the distributed objects.
     *
     * @param   values      The values for the function handler.
     */
    public void showMultDistributeForm (FunctionValues values)
    {
        Container con;
        BusinessObject obj;
        OID originOid = values.p_oid;
        UserInfo userInfo = values.getUserInfo ();

        if ((con = (Container) values.getObject (originOid)) != null)
        {
            String[] oidString = con.getSelectedElements ();
            if (oidString != null)
            {
                userInfo.distributeElements = new OID[oidString.length];

                for (int i = 0; i < oidString.length; i++)
                {
                    if ((obj = values.getObject (oidString[i])) != null)
                    {
                        userInfo.distributeElements[i] = obj.oid;
                    } // if
                } // for

                if ((obj = values.getObject (oidString[0])) != null)
                {
                    userInfo.okDistributeFunction = AppFunctions.FCT_PERFORMLISTDISTRIBUTE;
                    // create functionobject for notification
                    NotifyFunction nf = new NotifyFunction ();
                    // initialize function with current oid (is used for lokal search)
                    nf.initFunction (AppFunctions.FCT_NOTIFICATION, values.getUser (),
                        values.p_env, values.p_sess, values.p_app);
                    // call sequence control of function
                    nf.start ();
                } // if
            } // if
            else
            {
                IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_NOOBJECTSSELECTED, values.p_env),
                    values.p_app, values.p_sess, values.p_env);
                userInfo.inContentView = true;
                this.showObject (values, originOid);
            } // else
        } // if
    } // showMultDistributeForm


    /**************************************************************************
     * Distribute all selected Business objects. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    public void distributeSelectedElements (FunctionValues values)
    {
        if (values.getUserInfo ().distributeElements != null)
        {
            // create functionobject for notification
            NotifyFunction nf = new NotifyFunction ();
            // initialize function with current oid (is used for lokal search)
            nf.initFunction (AppFunctions.FCT_NOTIFICATION, values.getUser (),
                values.p_env, values.p_sess, values.p_app);
            // call sequence control of function
            nf.start ();
            (values.getUserInfo ()).distributeElements = null;
        } // if

        // executed distribute - clear the old receivers list
        values.p_sess.receivers = new Vector<String[]> ();
    } // distributeSelectedElements


    /**************************************************************************
     * Change a container's content values. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    public void listChange (FunctionValues values)
    {
        Container obj;                  // the container

        // get the container object:
        if ((obj = (Container) values.getObject ()) != null) // got the object
        {
            // change the objects:
            obj.listChange (values.p_representationForm);
        } // if got the object
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else didn't get object
    } // listChange


    /**************************************************************************
     * Create/delete Link to selected/deselected Businessobjects and display
     * the previous Container. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    public void handleSelectedElements (FunctionValues values)
    {
        Container con;

        if ((con = (Container) values.getObject ()) != null)
        {
            con.handleSelectedElements ();
        } // if
        else
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else

        con.show (values.p_representationForm);
    } // handleSelectedElements


    /**************************************************************************
     * ShowListDeleteForm show the content of a container which is able to
     * delete. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    public void showDeleteForm (FunctionValues values)
    {
        Container obj;             // the business object
        // stores that the system is in a deleteform
        if ((obj = (Container) values.getObject ()) != null)
        {
            // show the clean expired objects form
            obj.showDeleteForm (values.p_representationForm);
        } // if
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else didn't get object
    } // showDeleteForm


    /**************************************************************************
     * ShowUnDeleteForm show the content of a container which is able to
     * undelete. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    public void showUnDeleteForm (FunctionValues values)
    {
        WasteBasket_01 obj;             // the wastebasket
        // get object
        if ((obj = (WasteBasket_01) values.getObject ()) != null)
        {
            // show undelete form
            obj.showUndeleteForm ();
        } // if
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else didn't get object
    } // showUnDeleteForm


    /**************************************************************************
     * ShowCopySelForm shows all elements of a container which may be copied. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    public void showCopySelForm (FunctionValues values)
    {
// DEBUG BEGIN
// BB: added for debugging purposes. can be removed when finished.
//
/*
        if (this.sess.userInfo.copiedObjects.isEmpty ())
        {
            BusinessObject note = null;
            // insert a note into the copiedObject vector to simulate a
            // virtual object
            try
            {
                note = getObject (new OID ("0x01016B01000000E9"));
                note.retrieve (Operations.OP_READ);
                note.isPhysical = false;
                note.oid = OID.getEmptyOid ();
            } // try
            catch (NoAccessException e) // no access to object allowed
            {
                note.showNoAccessMessage (Operations.OP_READ);
            } // catch
            catch (AlreadyDeletedException e) // no access to objects allowed
            {
                note.showAlreadyDeletedMessage ();
            } // catch
            catch (IncorrectOidException e)
            {
                IOHelpers.showMessage (e.toString ());
            }
            note.p_targetPhysicalTypeCode = "Url";
            this.sess.userInfo.copiedObjects = new Vector ();
            note.p_virtualId = "" + this.sess.userInfo.copiedObjects.size ();
            this.sess.userInfo.copiedObjects.addElement (note);
            this.sess.userInfo.copiedType = AppConstants.CT_COPY;

showMessage ("Note inserted as virtual object");

        } // if (this.sess.userInfo.copiedObjects.isEmpty ())
*/
//
// DEBUG END

        Container obj;             // the business object
        // stores that the system is in a copyform

        if ((obj = (Container) values.getObject ()) != null)
        {
            // show the copy objects form
            obj.showCopySelForm (values.p_representationForm);
        } // if
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else didn't get object
    } // showCopySelForm


    /**************************************************************************
     * ShowCutSelForm shows all elements of a container which may be cutted.
     * <BR/>
     *
     * @param   values      The values for the function handler.
     */
    public void showCutSelForm (FunctionValues values)
    {
        Container obj;             // the business object
        // stores that the system is in a copyform
        if ((obj = (Container) values.getObject (true)) != null)
        {
            // show the cut objects form:
            obj.showCutSelForm (values.p_representationForm);
        } // if
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else
    } // showCutSelForm


    /**************************************************************************
     * ShowPasteSelForm shows all elements of a container which may be pasted.
     * <BR/>
     *
     * @param   values      The values for the function handler.
     */
    public void showPasteSelForm (FunctionValues values)
    {
        Container obj;                  // the business object
        Container cont;                 // the container used for displaying
                                        // the selection form
        Vector<OID> allowedElements;    // allowed oids

        // get the container object:
        if ((obj = (Container) values.getObject ()) != null)
                                        // got object instance?
        {
            // get all copied objects:
            Vector<BusinessObject> copiedObjects = new Vector<BusinessObject> ();
            Enumeration<BusinessObject> enumList =
                values.getUserInfo ().copiedObjects.elements ();
            while (enumList.hasMoreElements ())
            {
                copiedObjects.addElement (enumList.nextElement ());
            } // while

            // set the virtual objects to be pasted:
            obj.setPasteObjects (copiedObjects);

            // get the relevant objects out of the container:
            allowedElements =
                obj.reduceToAllowedElements (values.getUserInfo ().copiedOids);

            // stores that the system is in a copy form
            if ((cont = (Container)
                    values.getNewObject (TypeConstants.TC_Container)) != null)
                                        // got instance?
            {
                // set the oid of the current container:
                cont.setOid ((OID) (values.p_oid.clone ()));

                // set the virtual objects to be pasted
                cont.setPasteObjects (obj.getPasteObjects ());
                // show the copy objects form
                cont.showPasteSelForm (values.p_representationForm,
                    allowedElements);
            } // if got instance
            else                            // didn't get object
            {
                IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                    values.p_app, values.p_sess, values.p_env);
            } // else
        } // if got object instance
    } // showPasteSelForm


    /**************************************************************************
     * ShowPasteLinkSelForm shows all elements that may be added as link to the
     * current container. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    public void showPasteLinkSelForm (FunctionValues values)
    {
        Container obj;             // the business object
        // stores that the system is in a paste-link-form
        if ((obj = (Container) values.getObject ()) != null)
        {
            // show the paste-link-form
            obj.showPasteLinkSelForm (values.p_representationForm, values.getUserInfo ().copiedOids);
        } // if
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else didn't get object
    } // showPasteLinkSelForm


    /**************************************************************************
     * ShowDistributeSelForm shows all elements of a container which may be
     * distributed. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    public void showDistributeSelForm (FunctionValues values)
    {
        Container obj;             // the business object
        OID originOid = values.getUserInfo ().distributeContainerId;

        // stores that the system is in a distributeform
        if ((obj = (Container) values.getObject (originOid)) != null)
        {
            // show the distribute objects form
            obj.showDistributeSelForm (values.p_representationForm);
        } // if
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else didn't get object
    } // showPasteSelForm


    /**************************************************************************
     * ShowListChangeForm show the content of a container wich is able to
     * change. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    public void showListChangeForm (FunctionValues values)
    {
        Container obj;             // the business object
        if ((obj = (Container) values.getObject ()) != null)
        {
            // show the change expired objects form
            obj.showListChangeForm (values.p_representationForm);
        } // if
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else didn't get object
    } // showListChangeForm


    /**************************************************************************
     * ShowSelectionForm show the content of a container where you can select
     * different object, to insert or delete them from the containercontent.
     * <BR/>
     *
     * @param   values      The values for the function handler.
     */
    public void showSelectionForm (FunctionValues values)
    {
        Container obj;             // the business object
        // stores that the system is in a deleteform
        if ((obj = (Container) values.getObject ()) != null)
        {
            // show the clean expired objects form
            obj.showSelectionForm (values.p_representationForm);
        } // if
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else didn't get object
    } // showSelectionForm


    /**************************************************************************
     * Shows all elements of a container which may be forwarded. <BR/>
     * This includes only objects that are in an active workflow!
     *
     * @param   values      The values for the function handler.
     */
    public void showForwardSelectionForm (FunctionValues values)
    {
        Container obj;                  // the business object
        // stores that the system is in a copyform

        if ((obj = (Container) values.getObject ()) != null)
        {
            // show the copy objects form
            obj.showForwardSelForm (values.p_representationForm);
        } // if
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else didn't get object
    } // showForwardSelectionForm


    /**************************************************************************
     * Show login dialog. <BR/>
     *
     * @param   values      The values for the function handler.
     * @param   onTop       Shall the login form be displayed on top of screen?
     */
    public void showLoginForm (FunctionValues values, boolean onTop)
    {
        User_01 obj = null;             // the business object used for log in

        // get the homepagePath if it is not in the session
        values.setHomepagePath ();

        if (onTop)                      // form should be viewed on top?
        {
            String loginFormUrl = IOHelpers.getBaseUrlGet (values.p_env);

            // create the output:
            IOHelpers.processJavaScriptCode ("top.callUrl (\"" + loginFormUrl + "\", null, null, \"_top\");",
                                             values.p_app, values.p_sess, values.p_env);

            return;
        } // if form should be viewed on top

        if ((obj = (User_01) values.getNewObject (TypeConstants.TC_User)) != null)
            // object created successfully?
        {
            obj.showLoginForm (values.p_representationForm);
        } // if object created successfully
        else                            // no login possible
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                AppMessages.ML_MSG_LOGINNOTPOSSIBLE, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else no login possible
    } // showLoginForm


    /**************************************************************************
     * Perform login and show main page. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void login (FunctionValues values)
    {
        this.login (values, null, null, -1);
    } // login


    /**************************************************************************
     * Perform login and show main page. <BR/>
     *
     * @param   values      The values for the function handler.
     * @param   pUsername   Username to login - if <CODE>null</CODE> then the
     *                      data is retrieved from the url.
     * @param   pPassword   Password to login.
     * @param   pDomain     Domain where to login to.
     */
    protected void login (FunctionValues values, String pUsername,
                          String pPassword, int pDomain)
    {
        User_01 obj = null;             // the business object used for log in
        String str = null;              // a string for getting url parameters
        int num = 0;                    // a number for getting url parameters
/* KR currently not necessary
        int oldDomain = values.sess.userInfo.workspace.domainId;
                                        // the actual domain
*/
        int domain = 0;                 // the new domain
        String username = "";           // the new user name
        String password = "";           // the new password

        // set the actual homepagepath:
/* KR not necessary
        values.setHomepagePath ();
*/
        boolean isUsePwd = true;        // use password for login


        // initialize username, domain:
        if (values.getUser ().username != null)
        {
            username = values.getUser ().username;
        } // if
        if (values.getUser ().oid != null)
        {
            domain = values.getUser ().oid.domain;
        } // if

        if (pUsername == null)
        {
            // get login parameters:
            // get id of domain:
            if ((num = values.p_env.getIntParam (BOArguments.ARG_DOMAIN)) !=
                IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
            {
                domain = num;
            } // if
            // get username:
            if ((str = values.p_env.getStringParam (BOArguments.ARG_USERNAME)) != null)
            {
                username = str;
            } // if
            // get password:
            if ((str = values.p_env.getStringParam (BOArguments.ARG_PASSWORD)) != null)
            {
                password = str;
            } // if
        } // if
        else
        {
            // set the given parameters:
            domain = pDomain;
            username = pUsername;
            password = pPassword;
        } // els

        // check if there is an extended function defined:
        int extendedFunction = values.p_env.getIntParam (BOArguments.ARG_EXTENDED_FUNCTION);
        if (extendedFunction > 0)
        {
            // set the user info to the extended user data for providing it to the extended login function
            ExtendedUserData extendedUserData = new ExtendedUserData (username, password);
            values.p_sess.userInfo.setExtendedUserData (extendedUserData);

            values.getFunctionCache ().evalFunction (extendedFunction, values);

            // validate result of extended function

            // if extended function returned invalid pwd
            if (values.p_sess.userInfo.getExtendedUserData ().p_username
                .equals (AppConstants.EXTLOGIN_INVALID_PWD))
            {
                // extended login not successful.:
                IOHelpers.showMessage (MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                    AppMessages.ML_MSG_LOGINNOTPOSSIBLE, values.p_env),
                    values.p_app, values.p_sess, values.p_env);

                // reset the extended user data
                values.p_sess.userInfo.setExtendedUserData (null);

                // reshow the login dialog:
                // but only if weblink is not active, if it is active
                // then showWeblink does the job
                if (!values.p_sess.weblink)
                {
                    this.showLoginForm (values, false);
                } // if

                // terminate login functionality:
                return;
            } // if ext login returns invalid pwd
            // if extended function returned invalid username
            else if (values.p_sess.userInfo.getExtendedUserData ().p_username
                .equals (AppConstants.EXTLOGIN_INVALID_USER))
            {
                // reset the extended user data
                values.p_sess.userInfo.setExtendedUserData (null);

                // continue with normal login ...

            } // if ext login return invalid username
            // ext login has been successfull
            else
            {
                // take the login for the standard login from the session from the extended user data login field
                username = values.p_sess.userInfo.getExtendedUserData ().p_login;

                // login without pwd has to be performed ...
                isUsePwd = false;
            } // else
        } // if

        if ((obj = (User_01) values.getNewObject (TypeConstants.TC_User)) != null)
        {
            // perform login:
            User newUser = isUsePwd ? obj.login (domain, username, password) : obj.login (domain, username);

            if (newUser != null)        // user correctly logged in?
            {
                // activate the session for the user:
                this.activateUserSession (values, obj);
                // activate the layout for the user:
                this.activateUserLayout (values, obj);
            } // if user correctly logged in
            else                        // user not logged in
            {
                IOHelpers.showMessage (MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                    AppMessages.ML_MSG_LOGINNOTPOSSIBLE, values.p_env),
                    values.p_app, values.p_sess, values.p_env);
            } // else user not logged in
        } // if
        else                            // no login possible
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                AppMessages.ML_MSG_LOGINNOTPOSSIBLE, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else no login possible

        // reshow the login dialog:
        // but only if weblink is not active, if it is active
        // then showWeblink does the job
        if (!values.p_sess.weblink)
        {
            this.showLoginForm (values, false);
        } // if
    } // login

    /**************************************************************************
     * Perform logout of a online user. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    public void logout (FunctionValues values)
    {
//debug ("logout");
        User_01 user = new User_01 ();  // the business object used for log in
        user.initObject (
            values.getUser ().oid, values.getUser (), values.p_env,
            values.p_sess, null);
        user.logout ();
    } // logout


    /**************************************************************************
     * Activate the layout which is currently stored within the user profile.
     * <BR/>
     *
     * @param   values      The values for the function handler.
     * @param   oid     Object id of the object containing the user profile.
     */
    protected void activateLayout (FunctionValues values, OID oid)
    {
        UserProfile_01 userProfile = (UserProfile_01) values.getObject (oid);

/* KR 011205: not necessary because already done in getObject
        // retrieve data of the user profile:
        try
        {
trace ("KR FCT_ACTIVATE: retrieve userProfile begin");
            userProfile.retrieve (Operations.OP_READ);
trace ("KR FCT_ACTIVATE: retrieve userProfile end");
        } // try
        catch (NoAccessException e)
        {
        } // catch
        catch (AlreadyDeletedException e) // no access to objects allowed
        {
            // send message to the user:
            userProfile.showAlreadyDeletedMessage ();
        } // catch
*/

        // store the profile data within the session:
        (values.getUserInfo ()).userProfile = userProfile;

        try
        {
            // activate the layout:
            this.activateLayout (values);
            // re-initialize the user's buttons:
            // build the general button bar with all possible buttons
            this.initializeUserButtons (values);
            // display the layout:
            this.showLayout (values);
        } // try
        catch (NoLayoutDefinedException e)
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                AppMessages.ML_MSG_NOLAYOUTDEFINED, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // catch
        catch (NoLayoutFoundException e)
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                AppMessages.ML_MSG_NOLAYOUTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // catch
        // AppFunctions.FCT_SHOWOBJECT, userProfile.oid
    } // activateLayout


    /**************************************************************************
     * Activate the layout. <BR/>
     *
     * @param   values      The values for the function handler.
     *
     * @exception   NoLayoutDefinedException
     *              There is no layout defined for this user.
     * @exception   NoLayoutFoundException
     *              There was no layout found where the user has access rights
     *              or the layout which is defined for the user does not exist.
     */
    public void activateLayout (FunctionValues values)
        throws NoLayoutDefinedException, NoLayoutFoundException
    {

        LayoutContainer_01 layouts;     // all layouts which are acessible for
                                        // the user
        LayoutContainerElement_01 layout; // the current layout
        Enumeration<ContainerElement> vectEnum; // an enumaration
        boolean found;                  // was the layout found?
        OID oid = null;                 // oid of current object
        UserInfo userInfo = values.getUserInfo ();

        if (userInfo.userProfile != null)
                                        // user profile exists?
        {
            if (userInfo.userProfile.layoutId == null ||
                userInfo.userProfile.layoutId.isEmpty ())
            {
                // no layout defined?
                // raise corresponding exception:
                throw new NoLayoutDefinedException ("");
            } // if

            if (values.p_sess.layouts == null) // currently no layouts defined?
            {
                // get the layouts from the data store:
                // the oid of the layout container:
                oid = OID.getEmptyOid ();

                // create a layout container object:
                layouts = new LayoutContainer_01 (oid, values.getUser ());
                layouts.initObject (oid, values.getUser (), values.p_env,
                    values.p_sess, values.p_app);

                try
                {
                    // get the layouts:
                    layouts.retrieveContent (Operations.OP_READ, BOListConstants.LST_DEFAULTORDERING, BOConstants.ORDER_ASC);
                    // get the data of the workspace
                } // try
                catch (NoAccessException e)
                {
                    // nothing to do
                } // catch

                // store the layouts in the session:
                values.p_sess.layouts = layouts;
            } // if currently no layouts defined
            else                        // there are already some layouts
            {
                layouts = values.p_sess.layouts;
            } // else there are already some layouts

            // get the layouts as enumeration:
            vectEnum = layouts.elements.elements ();
            found = false;              // the layout was not found

            // search for the layout within the enumeration:
            while (vectEnum.hasMoreElements () && (!found))
            {
                // get the actual layout:
                layout = (LayoutContainerElement_01) vectEnum.nextElement ();

                // check if this is the same layout as defined for the user:
                if (layout != null &&
                    layout.oid.toString ().equalsIgnoreCase (userInfo.userProfile.layoutId.toString ()))
                {
                    // set the layout as active layout:
                    values.p_sess.activeLayout = layout;
                    found = true;       // the layout was found
                } // if
            } // while

            if (!found)                 // layout not found?
            {
                // raise corresponding exception
                throw new NoLayoutFoundException (userInfo.userProfile.layoutId.toString ());
            } // if layout not found
        } // if user profile exists
        else                            // user profile does not exist
        {
            // raise corresponding exception
            throw new NoLayoutDefinedException ("");
        } // else user profile does not exist
    } // activateLayout


    /**************************************************************************
     * Performs an observer-function that has been requested by an observer
     * thread. <BR/>
     * m2-Login must be performed to receive session object. <BR/>
     * Suppresses m2 HTML-output and generates XML-response.
     *
     * @param   values      The values for the function handler.
     */
    protected void performObserverFunction (FunctionValues values)
    {
        // generate XML-response object:
        m2XMLResponse response = new m2XMLResponse ();
        response.setIsIncludeLog (false);
        String rs = null;
        int function = values.p_function;

        // disable write to environment
        values.p_env.disableWrite ();

        try
        {
            // login with the data from the agent
            if (this.performAgentLogin (values))
            {
                // init function handler and execute event
                M2ObserverFunctionHandler handler = new M2ObserverFunctionHandler ();
                handler.initFunction (function, values.getUser (), values.p_env,
                                      values.p_sess, values.p_app);
                handler.handleEvent ();

                // generate XML-Response - success
                rs = response.generate (
                    function, "performObserverFunction",
                    DIConstants.RESPONSE_SUCCESS, "0", "OK", null);
            } // if (performAgentLogin ())
            else    // login failed
            {
                // generate XML-Response - login failed
                rs = response.generate (
                    function, "performObserverFunction",
                    DIConstants.RESPONSE_ERROR, "1",
                    MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                        AppMessages.ML_MSG_LOGINNOTPOSSIBLE, values.p_env), null);
            } // login failed
        } // try
        catch (Exception e)
        {
            // generate XML-Response - login failed
            rs = response.generate (function, "performObserverFunction", DIConstants.RESPONSE_ERROR,
                    "1", e.toString (), null);
        } // catch
        finally
        {
            // write response
            values.p_env.enableWrite ();
            values.p_env.write (rs);
            // close the session for the observer:
            values.p_env.abandon ();
        } // finally
    } // performObserverFunction


    /**************************************************************************
     * Performs an observer-gui-function that has been requested due to
     * user-interaction.
     *
     * @param   values      The values for the function handler.
     */
    protected void performObserverGUIFunction (FunctionValues values)
    {
        try
        {
            int function = values.p_function;
            M2ObserverFunctionHandler handler = new M2ObserverFunctionHandler ();
            handler.initFunction (function, values.getUser (), values.p_env, values.p_sess, values.p_app);
            handler.handleEvent ();
        } // try
        catch (Exception e)
        {
            IOHelpers.showMessage (e, values.p_app, values.p_sess, values.p_env, true);
        } // catch
    } // performObserverGUIFunction


    /**************************************************************************
     * Display the form for object imports. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void showObjectImportForm (FunctionValues values)
    {
//debug ("--- start showObjectImportForm" +
//      " - containerId = " + values.p_containerOid);
        ImportIntegrator importIntegrator = new ImportIntegrator ();

        importIntegrator.initObject (OID.getEmptyOid (), values.getUser (),
                                     values.p_env, values.p_sess, values.p_app);
        importIntegrator.containerId = values.p_containerOid;
        importIntegrator.setContainerId (values.p_containerOid);
        // show the import form
        importIntegrator.showImportForm ();
    } // showObjectImportForm


    /**************************************************************************
     * Performs an import. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void objectImport (FunctionValues values)
    {
//debug ("--- start objectImport" +
//      " - oid  = " + values.p_oid +
//      " - containerId = " + values.p_containerOid);

        ImportIntegrator importIntegrator = new ImportIntegrator ();
        importIntegrator.initObject (values.p_oid, values.getUser (), values.p_env, values.p_sess, values.p_app);
        importIntegrator.setContainerId (values.p_containerOid);
        // start the import
        importIntegrator.startImport ();
    } //objectImport


    /**************************************************************************
     * Export current object to specified directory. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void objectExport (FunctionValues values)
    {
//trace ("IbsFunctionHandler.objectExport (" + values.p_oid + ")");
        if (values.p_oid == null)
        {
            IOHelpers.showMessage ("IbsFunctionHandler.objectExport",
                                   "export could not be performed, OID is not set",
                                   values.p_app, values.p_sess, values.p_env);
            return;
        } // oid == null

        String [] oidStrings = {values.p_oid.toString ()};
        OID emptyOid = OID.getEmptyOid ();
        ExportIntegrator exportIntegrator = null;

        // now try to export the objects
        // note that all export settings must be set in the environment
        exportIntegrator = new ExportIntegrator ();
        exportIntegrator.initObject (emptyOid, values.getUser (),
            values.p_env, values.p_sess, values.p_app);
        // configure the export
        exportIntegrator.isGetSettingsFromEnv = true;
        exportIntegrator.isShowSettings = false;
        // start the export
        exportIntegrator.startExport (oidStrings);

        this.showObject (values, values.p_oid);
    } // objectExport


    /**************************************************************************
     * Performs an import. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void performAgentLoginImport (FunctionValues values)
    {
        // login with the data from the agent
        if (this.performAgentLogin (values))
        {
            // get the oids and call the import method
            OID containerId = values.p_env.getOidParam (DIArguments.ARG_IMPORTCONTAINERID);
            // create the importIntegrator
            ImportIntegrator importIntegrator = new ImportIntegrator ();
            importIntegrator.initObject (OID.getEmptyOid (), values.getUser (),
                    values.p_env, values.p_sess, values.p_app);
            importIntegrator.setContainerId (containerId);
            // set the flag to suppress HTML output and force text output
            importIntegrator.setGenerateHTML (false);
            // start the import and suppress HTML output generation
            importIntegrator.startImport ();
            // abandon the current session because agent sessions can get stuck up
            values.p_env.abandon ();
        } // if (performAgentLogin ())
        else    // login failed
        {
            values.p_env.write (MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                AppMessages.ML_MSG_LOGINNOTPOSSIBLE, values.p_env));
        } // login failed
    } // performAgentLoginImport


    /**************************************************************************
     * Performs an export.
     * This method is always called from an agent. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void performAgentLoginExport (FunctionValues values)
    {
        // login with the data from the agent
        if (this.performAgentLogin (values))
        {
            // create the exportIntegrator
            ExportIntegrator exportIntegrator = new ExportIntegrator ();
            exportIntegrator.initObject (OID.getEmptyOid (), values.getUser (), values.p_env, values.p_sess, values.p_app);
            // set the flag to suppress HTML output and force text output
            exportIntegrator.setGenerateHTML (false);
            // start the import and pass the string array with
            // the objects (can be oid or a m2 path)
            exportIntegrator.startExport (values.p_env.getMultipleParam (DIArguments.ARG_OBJECT));
            // abandon the current session because agent sessions can get stuck up
            values.p_env.abandon ();
        } // if (performAgentLogin ())
        else    // login failed
        {
            values.p_env.write (MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                AppMessages.ML_MSG_LOGINNOTPOSSIBLE, values.p_env));
        } // login failed
    } // performAgentLoginExport


    /**************************************************************************
     * Performs an login an restore external ids is possible. <BR/>
     * This method is always called from an agent. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void performAgentLoginRestoreExternalIds (FunctionValues values)
    {
        // login with the data from the agent
        if (this.performAgentLogin (values))
        {
            // create the exportIntegrator
            ExportIntegrator exportIntegrator = new ExportIntegrator ();
            exportIntegrator.initObject (OID.getEmptyOid (), values.getUser (), values.p_env, values.p_sess, values.p_app);
            // set the flag to suppress HTML output and force text output
            exportIntegrator.setGenerateHTML (false);
            // create a response from the argument
            Response response = new Response ();
            response.parseResponse (values.p_env.getStringParam (DIArguments.ARG_RESPONSE));
            // create the log of the export integrator
            exportIntegrator.initLog ();
            // now restore the external ids from the response if possible
            exportIntegrator.restoreExternalIDs (response);
            // abandon the current session because agent sessions can get stuck up
            values.p_env.abandon ();
        } // if (performAgentLogin ())
        else    // login failed
        {
            values.p_env.write (MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                AppMessages.ML_MSG_LOGINNOTPOSSIBLE, values.p_env));
        } // login failed
    } // performAgentLoginRestoreExternalIds


    /**************************************************************************
     * This is a method for the login of import and export agents. <BR/>
     * The username, password and domainid will be read from the environment
     * using the arguments
     * {@link ibs.bo.BOArguments#ARG_USERNAME BOArguments.ARG_USERNAME},
     * {@link ibs.bo.BOArguments#ARG_PASSWORD BOArguments.ARG_PASSWORD}, and
     * {@link ibs.bo.BOArguments#ARG_DOMAIN BOArguments.ARG_DOMAIN}. <BR/>
     *
     * @param   values      The values for the function handler.
     *
     * @return  <CODE>true</CODE> if login was successfull or
     *          <CODE>false</CODE> otherwise
     */
    private boolean performAgentLogin (FunctionValues values)
    {
        User_01 obj = null;             // the business object used for log in
        String str = null;              // a string for getting url parameters
        int num = 0;                    // a number for getting url parameters
//        int oldDomain = this.sess.userInfo.workspace.domainId;
                                        // the actual domain
        int domain = 0;                 // the new domain
        String username = "";           // the new user name
        String password = "";           // the new password

        // set the actual homepagepath:
        values.setHomepagePath ();

        // get login parameters:
        // get id of domain:
        if ((num = values.p_env.getIntParam (BOArguments.ARG_DOMAIN)) !=
            IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            domain = num;
        } // if
        // get username:
        if ((str = values.p_env.getStringParam (BOArguments.ARG_USERNAME)) != null)
        {
            username = str;
        } // if
        // get password:
        if ((str = values.p_env.getStringParam (BOArguments.ARG_PASSWORD)) != null)
        {
            password = str;
        } // if
        if ((obj = (User_01) values.getNewObject (TypeConstants.TC_User)) != null)
        {
            // perform login:
            User newUser = obj.login (domain, username, password);
            if (newUser != null)        // user correctly logged in?
            {
                // activate the session for the user:
                this.activateUserSession (values, obj);

                try
                {
                    this.activateLayout (values);
                } // try
                catch (NoLayoutDefinedException e)
                {
                    // allow that case but not that it can have side effect
                    // in case something layout specific is needed
                } // catch
                catch (NoLayoutFoundException e)
                {
                    // allow that case but not that it can have side effect
                    // in case something layout specific is needed
                } // catch

                // read the menu tabs. this is needed in order to resolve
                // path information. The reading of the menu tabs is usually
                // done when creating the user layout but this is not neccessary
                // for agents.
                try
                {
                    // creates a new container for all tabs one user is allowed to see
                    MenuContainer_01 menuCont =
                        new MenuContainer_01 (OID.getEmptyOid (), newUser);
                    // fills the container with all necessary information from
                    // enviroment, application and session
                    menuCont.setEnv (values.p_env);
                    menuCont.app = values.p_app;
                    menuCont.sess = values.p_sess;
                    menuCont.menuOwner = newUser.id;
                    menuCont.retrieveContent (Operations.OP_READ,
                                              BOListConstants.LST_DEFAULTORDERING,
                                              BOConstants.ORDER_ASC);
                } // try
                catch (NoAccessException e)
                {
                    IOHelpers.showMessage (e, values.p_app, values.p_sess, values.p_env);
                } // catch

                return true;
            } // if user correctly logged in

            // user not logged in
            return false;
        } // if

        // no login possible
        return false;
    } // performAgentLogin


    /**************************************************************************
     * Activate the user session. <BR/>
     *
     * @param   values      The values for the function handler.
     * @param   userObj     The user object to activate the session for.
     */
    private void activateUserSession (FunctionValues values, User_01 userObj)
    {
        Workspace_01 wsp;               // workspace of the user
        UserProfile_01 userProfile;     // profile of the user
        UserInfo userInfo = values.getUserInfo ();

        // set the actual homepagepath:
        // is that neccessary?
        values.setHomepagePath ();
        // get the user data:
        User newUser = userObj.userData;

        // performLogin is set to false to make known the login has been
        // done
//        this.performLogin = false;

        // store user data in session:
        values.setUser (newUser);

        // set the extended user data if is has not been provided by the extended login functionality
        if (userInfo.getExtendedUserData () == null)
        {
            // set the extended user data for the user
            ExtendedUserData extendedUserData = new ExtendedUserData (newUser.username);
            extendedUserData.p_oid = newUser.oid;
            userInfo.setExtendedUserData (extendedUserData);
        } // if

        // initialize tracer:
        // create a new tracer for this user:
        this.openTrace ();

        // clear properties which are restricted to permissions:
        // remove all elements from the cache
        userInfo.cache.removeAll ();
        // clear the layouts:
        values.p_sess.layouts = null;
        // clear the history:
        userInfo.history = new HistoryInfo (values.p_app);
        // clear the workspace:
        userInfo.workspace = null;
        // clear the user profile:
        userInfo.userProfile = null;

        // get the workspace of the user:
        if ((wsp = (Workspace_01) values.getNewObject (TypeConstants.TC_Workspace)) != null)
                                        // workspace exists?
        {
            try
            {
                // get the data of the workspace:
                wsp.retrieveForActualUser (Operations.OP_READ);
            } // try
            catch (NoAccessException e)
            {
                // nothing to do
            } // catch

            // store the workspace in the session:
            userInfo.workspace = wsp;
        } // if workspace exists

        // get the profile of the user:
        if (!userInfo.workspace.profile.isEmpty ())
        {
            if ((userProfile = (UserProfile_01) values
                .getObject (userInfo.workspace.profile)) != null)
                                        // user profile found?
            {
                // if it has not yet been done, set the Path for
                // temporary Upload-Files
                HttpConstants.PATH_UPLOAD_TEMP =
                    values.p_app.p_system.p_m2AbsBasePath +
                    BOPathConstants.PATH_UPLOAD_ABS_FILES_TEMP;

                // get the session.homePath
                values.p_sess.home = values.p_app.p_system.p_m2WwwBasePath;

                // check if the weblink cookie has to be set
                if (userProfile.saveProfile)
                {
                    // set the cookie for the user:
                    userObj.setWeblinkCookie ();
                } // if

                // store the workspace in the session:
                userProfile.setEnv (values.p_env); // set environment
                userProfile.sess = values.p_sess; // get session object
                userInfo.userProfile = userProfile;
            } // if user profile found
            else                // user profile not found
            {
                userInfo.userProfile = null;
            } // else user profile not found
        } // if
        else
        {
            // clear the user profile:
            userInfo.userProfile = null;
        } // else
    } // activateUserSession


    /**************************************************************************
     * Activate the user layout. <BR/>
     *
     * @param   values      The values for the function handler.
     * @param   userObj     The user object to activate the session for.
     */
    private void activateUserLayout (FunctionValues values, User_01 userObj)
    {
        User user = values.getUser ();    // the actual user
        // build the general button bar with all possible buttons
        this.initializeUserButtons (values);
        MenuContainer_01 menuCont = null; // Container for all tabs the user is
                                        // allowed to see

        try
        {
            this.activateLayout (values);
        } // try
        catch (NoLayoutDefinedException e)
        {
            values.p_env.abandon ();    // finish the session

            // display corresponding message:
            Map<String, String> replacements = new HashMap<String, String> ();

            replacements.put (UtilConstants.TAG_NO_LAYOUT_DEF_FOR_USER,
                    MultilingualTextProvider.getText (
                            BOMessages.MSG_BUNDLE, BOMessages.MSG_NO_LAYOUT_DEF_FOR_USER, values.p_env));

            replacements.put (UtilConstants.TAG_PLEASE_CONTACT,
                    MultilingualTextProvider.getText (
                            BOMessages.MSG_BUNDLE, BOMessages.MSG_PLEASE_CONTACT_YOUR, values.p_env));
            
            IOHelpers.showSSIFile (AppConstants.SSI_NOLAYOUTDEFINED,
                    replacements, values.p_env);

            return;                     // terminate method
        } // catch
        catch (NoLayoutFoundException e)
        {
            values.p_env.abandon ();    // finish the session

            // display corresponding message:
            Map<String, String> replacements = new HashMap<String, String> ();

            replacements.put (UtilConstants.TAG_NO_LAYOUT_FOUND_FOR_USER,
                    MultilingualTextProvider.getText (
                            BOMessages.MSG_BUNDLE, BOMessages.MSG_NO_LAYOUT_FOUND_FOR_USER, values.p_env));

            replacements.put (UtilConstants.TAG_PLEASE_CONTACT,
                    MultilingualTextProvider.getText (
                            BOMessages.MSG_BUNDLE, BOMessages.MSG_PLEASE_CONTACT_YOUR, values.p_env));
            
            IOHelpers.showSSIFile (AppConstants.SSI_NOLAYOUTFOUND,
                    replacements, values.p_env);
            
            return;                     // terminate method
        } // catch

        try
        {
            userObj.oid = user.oid;
            userObj.user = user;
//trace ("KR login: retrieve obj (User_01) begin");
            userObj.retrieve (Operations.OP_READ);
//trace ("KR login: retrieve obj (User_01) end");
            // get the data of the workspace
        } // try
        catch (NoAccessException e)
        {
            // nothing to do
        } // catch
        catch (AlreadyDeletedException e) // no access to objects allowed
        {
            // send message to the user:
            userObj.showAlreadyDeletedMessage ();
        } // catch
        catch (ObjectNotFoundException e)
        {
            // cannot happen, nothing to do
        } // catch
        // check at this point whether a person is already associated
        // to the user or not. in case there is no person found start
        // the user data wizard

        try
        {
            // creates a new container for all tabs one user is allowed to see
            menuCont = new MenuContainer_01 (OID.getEmptyOid (), user);
            // fills the container with all necessary information from
            // enviroment, application and session
            menuCont.setEnv (values.p_env);
            menuCont.app = values.p_app;
            menuCont.sess = values.p_sess;
            menuCont.menuOwner = user.id;
            menuCont.retrieveContent (Operations.OP_READ,
                                      BOListConstants.LST_DEFAULTORDERING,
                                      BOConstants.ORDER_ASC);
//trace ("menuCont.retrieveContent DONE");
        } // try
        catch (NoAccessException e)
        {
            IOHelpers.showMessage (e, values.p_app, values.p_sess, values.p_env);
        } // catch

        if (values.getConfiguration ().getWizardLogin () &&
            !user.username.equalsIgnoreCase (IOConstants.USERNAME_ADMINISTRATOR) &&
            !user.username.equalsIgnoreCase (IOConstants.USERNAME_DEBUG) &&
            ((userObj.personOid == null) || userObj.personOid.isEmpty ()))
        {
            // activate the registration wizard
            values.p_sess.wizardRegistration = true;
            return;
        } // if (newUser.personOid == null)

        else if (!values.p_sess.weblink)
        {
            // show the layout only if the weblink is not activated
            // at this point it has to be checked if SSL is not
            // required while a https-request was given. If so a
            // redirect has to be done.
            if (!Ssl.isSslRequired (values.p_sess) &&
                 Ssl.isHttpsRequest (values.p_env, values.p_app) &&
                 ((ServerRecord) values.p_sess.actServerConfiguration).isApplicationServerAvailable ())
                                        // SSL must not be used and
                                        // request was done in secure mode?
            {
                // get the actual SessioninfoId out of the environment:
                String value = values.p_env.getSessionId ();

                // insert the sessioninfo object into the hashtable
                // while using the sessioninfo id as a key:
                values.p_app.insertSessioninfoTable (value, values.p_sess);

                String url = Ssl.getNonSecureUrl (IOHelpers.getBaseUrl (
                    values.p_env), values.p_sess) +
                    HttpArguments.createArg (BOArguments.ARG_FUNCTION,
                        AppFunctions.FCT_CHANGETOINSECURESESSION) +
                    HttpArguments.createArg (BOArguments.ARG_SESSIONINFOID,
                        value);

                // make redirect to get insecure mode
                values.p_env.redirect (url);

/* BM
   the next statement must not be called anymore, the IE 5.0 does not execute
   the redirect statement properly.
*/
                // erase old environment
                // env.abandon ();
            } // if SSL must not be used and request was done in secure mode
            else                        // weblink is not activated and no
                                        // redirect to an insecure session was made
            {
                // show the layout only if the weblink is not activated
                // and no redirect to an insecure session should be made
                // create the frameset
                this.showLayout (values);
            } // else
        } // personOid != null
    } // activateUserLayout


    /**************************************************************************
     * Show the multiple upload form. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void showMultipleUploadForm (FunctionValues values)
    {
        ImportIntegrator importIntegrator = new ImportIntegrator ();

        importIntegrator.initObject (OID.getEmptyOid (), values.getUser (),
                                     values.p_env, values.p_sess, values.p_app);
        // show the multiple upload form
        importIntegrator.showMultipleUploadForm ();
    } // showMultipleUploadForm


    /**************************************************************************
     * ShowListExportForm show the content of a container wich is able to
     * export. This is usually done within an exportContainer object. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    public void showListExportForm (FunctionValues values)
    {
        Container obj;             // the business object
        if ((obj = (Container) values.getObject ()) != null)
        {
            // show the list export form
            obj.showExportSelForm (values.p_representationForm);
        } // if
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else didn't get object
    } // showListExportForm


    /**************************************************************************
     * Display the form for object exports. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void showExportForm (FunctionValues values)
    {
//debug ("--- start showExportForm" + " - oid  = " + values.p_oid);
        OID[] selectedOids = null;
        // get the container where the elements for paste were selected
        // Container con = (Container) values.getObject ();
        String exportOids = values.p_env.getStringParam (BOArguments.ARG_EXPORTOID);
        if (exportOids == null)
        {
            exportOids = "";
        } // if

        if (exportOids != null && exportOids.length () > 0)
        {
            // get the hidden oids
            String[] oidString = values.p_env.getMultipleParam (BOArguments.ARG_EXPORTOID);
            // String[] oidString = con.getSelectedElements (this.fdd);
            if (oidString != null)
            {
                // prepare an oid list for all selected elements
                selectedOids = new OID [oidString.length];
                for (int i = 0; i < oidString.length; i++)
                {
                    selectedOids[i] = (values.getObject (oidString[i])).oid;
                } // for
            } // if (oidString != null)
        } //  if (fdd.exists (BOArguments.ARG_EXPORTOID))
        else
        {
            // get the list of objects selected
            selectedOids = this.getSelectedOIDs (values, values.p_oid);
        } // else
        // check if there have been objects selected for export
        if (selectedOids == null)
        {
            BusinessObject obj = values.getObject ();
            obj.show (values.p_representationForm);
        }   // if (selectedOids == null)
        else        // some objects have been selected for export
        {
            ExportIntegrator exportIntegrator = new ExportIntegrator ();
            exportIntegrator.initObject (new OID (0, 0), values.getUser (),
                values.p_env, values.p_sess, values.p_app);
            exportIntegrator.setContainerId (values.p_oid);
            // show the export form
            exportIntegrator.showExportForm (selectedOids);
        } // else some objects have been selected for export
    } // showExportForm


    /**************************************************************************
     * Export all selected Business objects. <BR/>
     *
     * @param   values      The values for the function handler.
     * @param   selectedOidStrings  The oids of the selected elements in string
     *                              representation.
     */
    public void exportSelectedElements (FunctionValues values,
                                        String[] selectedOidStrings)
    {
        // check if there are any objects selected
        if (selectedOidStrings == null)
        {
            BusinessObject obj = null;
            if ((obj = values.getObject ()) == null)
            {
                IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                    values.p_app, values.p_sess, values.p_env);
            } // if ((obj = values.getObject (exportOid)) == null)
            else
            {
                obj.show (values.p_representationForm);
            } // else
        }   // if (selectedOids == null)
        else        // some objects have been selected for export
        {
            ExportIntegrator exportIntegrator = new ExportIntegrator ();
            exportIntegrator.initObject (OID.getEmptyOid (), values.getUser (),
                                         values.p_env, values.p_sess, values.p_app);
            exportIntegrator.setContainerId (values.p_oid);
            // show the Export form
            exportIntegrator.startExport (selectedOidStrings);
        } // else some objects have been selected for export
    } // exportSelectedElements


    /**************************************************************************
     * Show a weblink - if necessary, the user is logged in first. <BR/>
     * This page displays:
     *
     * @param   values      The values for the function handler.
     */
    private void showWeblink (FunctionValues values)
    {
        User_01 userObj = null;
        String message = null;
        int dom = -1;                   // the domain of the web link
        String username;
        User user = values.getUser ();    // the actual user
        OID oid = values.p_oid;
        boolean performLogin;
        BusinessObject obj = null;      // the actual business object

        // set in the session that the weblink is activated
        values.p_sess.weblink = true;

        // check if we have a user that is already logged in:
        if (values.isUserLoggedIn ())   // user logged in?
        {
            // check if a domain is given:
            if ((dom = values.p_env.getIntParam (BOArguments.ARG_DOMAIN)) !=
                IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
                                        // domain set?
            {
                // check if there is a user logged in, and if it is
                // check if the domain is the same -
                // if not then the login has to be performed
                performLogin = values.getUser ().domain != dom;
            } // if domain set
            else                        // no domain set
            {
                performLogin = false;
            } // else no domain set
        } // if user logged in
        else                            // no user logged in
        {
            performLogin = true;
        } // else no user logged in

        if (performLogin)               // perform login?
        {
            // user is not logged in yet
            // has to be logged in first
            // try if the data is given with the link!
            if (values.p_env.getStringParam (BOArguments.ARG_USERNAME) != null)
                                        // username set?
            {
                // the userdata is given within the link - login user normally
                this.login (values);
                // check if we still need a login:
                performLogin = !values.isUserLoggedIn ();
            } // if username set
            else                        // no username set
            {
                // check if NTLM authentication has been activated:
                // first set the valid domains defined in the configuration
                // before the remoteUser can be read from the environment
                values.p_env.setValidDomains (values.getConfiguration ().getNTDomains ());

                // create the new user object:
                userObj = (User_01) values.getNewObject (TypeConstants.TC_User);

                // check if the username is already in the environment
                // this means that the NTLM negotiation has been done
                username = values.p_env.getRemoteUser ();
                // did we get a username?
                if (username != null && username.length () > 0)
                                        // username not empty?
                {
                    // check íf this user exists within the application
                    // create a user object first
                    if (userObj != null)   // user object created?
                    {
                        // domain should be set in the weblink url
                        int domain = values.p_env.getIntParam (BOArguments.ARG_DOMAIN);
                        // perform login with user name from operating system
                        // that has been negotiated via NTLM
                        // note that the login is don without any password confirmation
                        // and standard domain = 1 (!!!)
                        // it is not possible to pass a m2 domain id via NTLM
                        User userData = userObj.login (domain, username);

                        if (userData != null)        // user correctly logged in?
                        {
                            // set in the session that the
                            // NTLM authentication has been activated
                            values.p_sess.isNtlmActivated = true;

                            // activate the session for the user
                            this.activateUserSession (values, userObj);
                            // activate the layout for the user
                            this.activateUserLayout (values, userObj);
                            // The login data has to be set to enable the
                            // WebDAVLink servlet to pass user credentials to
                            // the WEBDAV servlet
                            user.username = userData.username;
                            user.password = userData.password;
                            user.domain = userData.domain;
                            // mark that the login has been successful:
                            performLogin = false;
                        } // if user correctly logged in
                        else    // login failed
                        {
                            message = "Automatisches Login fehlgeschlagen.";
                        } // else login failed
                    } // if user object created
                } // if username not empty
                else                    // the username is empty
                {
                    // try to read a cookie:
                    // data is not given with the link, Cookie has to be read
                    // create a User-Object
                    if (userObj != null)   // user object created?
                    {
                        username = null;
                        WeblinkInfo info = userObj.getWeblinkCookie (
                            values.p_env.getIntParam (BOArguments.ARG_DOMAIN));

                        if (info != null) // web link info found?
                        {
                            User temp = userObj.getInfo (info.userId, info.domain);
                            if (temp != null)
                            {
                                if (info.hash.equalsIgnoreCase (
                                    StringHelpers.computeHash (
                                        temp.username + temp.password + info.domain)))
                                {
                                    // login with the data from the cookie:
                                    this.login (values, temp.username, temp.password, info.domain);
                                    // mark that the login has been successful:
                                    performLogin = !values.isUserLoggedIn ();
                                    if (!performLogin) // successfully logged in?
                                    {
                                        // since when the info is retrieved from a Cookie,
                                        // the getParameters method will not initialize the
                                        // user, the initialisation is necessary here!
                                        user.username = temp.username;
                                        user.password = temp.password;
                                        user.domain = info.domain;
                                    } // if successfully logged in
                                } // if
                                else
                                {
                                    message = MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                                        AppMessages.ML_MSG_COOKIEDATAINVALID, values.p_env);
                                } // else
                            } // if
                            else
                            {
                                message = MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                                    AppMessages.ML_MSG_USERNOTFOUND, values.p_env);
                            } // else
                        } // if web link info found
                        else            // web link info not found
                        {
                            message = MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                                AppMessages.ML_MSG_COOKIENOTFOUND, values.p_env);
                        } // else web link info not found
                    } // if user object created
                } // else the username is empty
            } // else no username set
        } // if perform login

        // check if there is a message to be shown:
        if (message != null)            // message set?
        {
            IOHelpers.showMessage (message, values.p_app, values.p_sess, values.p_env);
            message = null;             // reset message
        } // if message set

        // is oid set?
        if (oid == null && values.p_sess.p_weblinkOid == null)
        {
            String extKeyIdDomain = values.p_env.getStringParam(BOArguments.ARG_EXTKEYDOMAIN);
            String extKeyId = values.p_env.getStringParam(BOArguments.ARG_EXTKEYID);
            
            // try to get the object from EXTkey pair only when an EXTkey pair is provided
            if (extKeyIdDomain != null && extKeyId != null)
            {
                // try to get the OID over a keymapper entry with the parameters
                // from the environment
                oid = BOHelpers.getOidByExtKey (extKeyIdDomain, extKeyId, values.p_env);

                values.p_oid = oid;
            } // if
        } // if

        // get id of container:
        if (oid == null && values.p_sess.p_weblinkOid == null)
                                        // no oid set?
        {
            String path = "";
            if (values.p_sess.p_opath == null)
            {
                // get the Path from the url!
                String opath = values.p_env.getStringParam (BOArguments.ARG_OPATH);
                String objectName = values.p_env.getStringParam (BOArguments.ARG_OBJECT);

                // check if path and objectname given:
                if (opath != null && objectName != null)
                {
                    // get the Path from the url!
                    path = opath + BOConstants.PATH_FORWARDSEPARATOR + objectName;
                } // if
                else
                {
                    message = MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                        AppMessages.ML_MSG_NOPATHGIVEN, values.p_env);
                } // else
            } // if
            else
            {
                // get the path from the session:
                path = values.p_sess.p_opath;
            } // else

            // check if the user is already logged in:
            if (!performLogin)     // no login?
            {
                // check if there is a path to be resolved:
                if (path != null && path.length () > 0) // there is a path?
                {
                    // resolveObjectPathData is a method of the BusinessObject
                    // so getting a BusinessObject to do the operation is
                    // necessary - since the user has to be logged in now
                    // the workspace (which is a BusinessObject) should be
                    // available in the session, for that this BO is used
                    values.getUserInfo ().workspace.setEnv (values.p_env);  // zur Sicherheit!
                    oid = BOHelpers.resolveObjectPath (path,
                        values.getUserInfo ().workspace, values.p_env);

                    // store the retrieved oid to the oid attribute of values
                    values.p_oid = oid;
                } // if there is a path
            } // if no login
            else                        // perform login
            {
                // login was not successfull - save the path in the session
                // because object has to be retrieved only after a valid user
                // is logged in
                values.p_sess.p_opath = path;
            } // else perform login
        } // if no oid set

        // the login was not successful - the login form is displayed, so it
        // is not allowed to show the object or the frame
        if (performLogin)          // perform login?
        {
            // just save the info in the session if the frame has to be loaded or not
            // get the info either out of the session or from the parameters
            values.p_sess.p_loadFrame = values.p_sess.p_loadFrame ||
                (values.p_env.getBoolParam (BOArguments.ARG_LOADFRAME) == IOConstants.BOOLPARAM_TRUE);
            // save the other data in the session too
            if (values.p_sess.p_weblinkOid == null && values.p_oid != null)
                                        // no weblink oid set?
            {
                values.p_sess.p_weblinkOid = values.p_oid;
            } // if no weblink oid set

            // show the login form:
            this.showLoginForm (values, false);
        } // if perform login
        else if (values.p_sess.p_loadFrame ||
                 values.p_env.getBoolParam (BOArguments.ARG_LOADFRAME) == IOConstants.BOOLPARAM_TRUE)
        {
            // if the frame has to be loaded, then we are saving
            // the rest in the session
            if (values.p_sess.p_weblinkOid == null && values.p_oid != null)
                                        // no weblink oid set?
            {
                values.p_sess.p_weblinkOid = values.p_oid;
            } // if no weblink oid set

            // frame is been shown now, afterwards it is not necessary
            values.p_sess.p_loadFrame = false;
            // now show the Layout (means the frameset is loaded)
            this.showLayout (values);
        } // if
        else                            // already logged in and frameset shown
        {
            // frame has not to be loaded, so the object can be
            // shown right away

            // get the oid which was given - if it is not in
            // the object - variable, then use the one set in the session:
            if (oid == null)            // no oid set?
            {
                oid = (OID) values.p_sess.p_weblinkOid;
            } // if no oid set

            FunctionValues newValues = new FunctionValues (values.p_app,
                values.p_sess, values.p_env);

            // check if there is an oid set:
            if (oid != null)            // oid set?
            {
                newValues.setOid (oid);
                obj = newValues.getObject (newValues.p_oid, false);
            } // if oid set

            // probably the frame was already loaded, or even the
            // call was given in the Application itself - so
            // we delete all data which eventually was saved in the session
            // show the object with the given oid
            // but if it has a file, then show the master
            // call showMaster instead of showObject
            if (oid != null && obj != null && obj.hasFile ()) // the master shall be displayed?
            {
                this.showMaster (newValues);
            } // if the master shall be displayed
            else if (oid != null)       // the object shall be displayed?
            {
                this.showObject (newValues, oid);
            } // else if the object shall be displayed
            else                        // no object to be displayed
            {
                // just display the welcome page:
                this.showWelcome (values);
            } // else no object to be displayed

            // clear the weblink settings in the session
            // note that this must be done AFTER the showObject
            // method in order to correctly display the tabs and buttons!
            values.p_sess.weblink = false;
            values.p_sess.p_weblinkOid = null;
            values.p_sess.p_opath = null;
        } // else already logged in and frameset shown
    } // showWeblink


    /**************************************************************************
     * Show a the welcome page. <BR/>
     * This page displays:
     * <UL>
     * <LI> the m2 logo
     * <LI> Information about the user currently logged in
     * <LI> information about how many entries in the inbox are left unread
     * </UL>
     * <BR/>
     *
     * @param   values      The values for the function handler.
     */
    private void showWelcome (FunctionValues values)
    {
        String linkBegin =
            "<A HREF=\"" + IOConstants.URL_JAVASCRIPT + "top.showObject ('";
        String linkMiddle = "');\">";
        String linkEnd = "</A>";
        String page = "";
//        String baseUrl;
        UserInfo userInfo = values.getUserInfo ();

        // try to find the ssi file:
        try
        {
            // get the Server side include file:
            page = IOHelpers.getSSIFile (AppConstants.SSI_WELCOME,
                                        values.p_env,
                                        values.getConfiguration ());

            // set the homepage path that seems to be missing:
            values.setHomepagePath ();

            page = StringHelpers.replace (page, UtilConstants.TAG_BASE,
                userInfo.homepagePath);
/*
            page = StringHelpers.replace (page, UtilConstants.TAG_BASE,
                values.p_env.getServerVariable (IOConstants.SV_HTTPS) + "://" +
                values.p_env.getServerVariable (IOConstants.SV_SERVER_NAME) + ":" +
                values.p_env.getServerVariable (IOConstants.SV_SERVER_PORT) +
                userInfo.homepagePath);
*/
/*KR not necessary, duplicate
            if (Ssl.isHttpsRequest (values.p_env, values.p_app))
                baseUrl = BOPathConstants.PATH_HTTPSPREFIX;
            else
                baseUrl = BOPathConstants.PATH_HTTPPREFIX;
            baseUrl += values.p_env.getServerName () +
                values.p_app.p_system.p_m2WwwBasePath + "app/";
//            page = StringHelpers.replace (page, UtilConstants.TAG_BASE,
//                this.sess.userInfo.homepagePath);
            page = StringHelpers.replace (page, UtilConstants.TAG_BASE, baseUrl);
*/

            page = StringHelpers.replace (page, UtilConstants.TAG_BROWSERDIR,
                values.p_sess.activeLayout.path + values.p_env.getBrowser () + "/");
        } // try
        catch (SsiFileNotFoundException e)
        {
            IOHelpers.showMessage (e, values.p_app, values.p_sess, values.p_env);
        } // catch

        //
        // show amount of news and a goto news button
        //
        NewsContainer_01 news = (NewsContainer_01)
            BOHelpers.getObject (userInfo.workspace.news, values.p_env,
                false, false, false);

        news.setEnv (values.p_env);              // set environment
        news.sess = values.p_sess;    // get session object
        news.app = values.p_app;

        // get the number of entries
        int newsEntries = news.getEntries ();
        if (newsEntries == 0)
        {
            page = StringHelpers.replace (page, UtilConstants.TAG_NEWS,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NONEWS, values.p_env) + " " + MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_THELAST, values.p_env) + " " +
                    userInfo.userProfile.newsTimeLimit + " " +
                    MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_LASTDAYS, values.p_env) + ". ");
        } // if
        else
        {
            if (newsEntries == 1)
            {
                page = StringHelpers.replace (page, UtilConstants.TAG_NEWS,
                    linkBegin + news.oid.toString () + linkMiddle +
                        newsEntries + " " + MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NEWSENTRY, values.p_env) + " " +
                        MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_THELAST, values.p_env) + " " +
                        userInfo.userProfile.newsTimeLimit + " " +
                        MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_LASTDAYS, values.p_env) + "." + linkEnd);
            } // if
            else
            {
                page = StringHelpers.replace (page, UtilConstants.TAG_NEWS,
                    linkBegin + news.oid.toString () + linkMiddle +
                        newsEntries + " " + MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NEWSENTRIES, values.p_env) + " " +
                        MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_THELAST, values.p_env) + " " +
                        userInfo.userProfile.newsTimeLimit + " " +
                        MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_LASTDAYS, values.p_env) + "." + linkEnd);
            } // else
        } // else

        //
        // show amount of inbox message and a goto inbox button
        //
        Inbox_01 inbox = (Inbox_01)
            BOHelpers.getObject (userInfo.workspace.inBox, values.p_env,
                false, false, false);
        inbox.setEnv (values.p_env);             // set environment
        inbox.sess = values.p_sess;    // get session object
        inbox.app = values.p_app;

        // get the number of entries
        int unreadMessages = inbox.getUnreadMessages ();
        if (unreadMessages == 0)
        {
            page = StringHelpers.replace (page, UtilConstants.TAG_INBOX, "");
//            StringHelpers.replace (page, UtilConstants.TAG_INBOX, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NOMESSAGES, env) + ". ");
        } // if
        else
        {
            if (unreadMessages == 1)
            {
                page = StringHelpers.replace (page, UtilConstants.TAG_INBOX,
                    linkBegin + inbox.oid.toString () + linkMiddle +
                        unreadMessages + " " + MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_UNREADMESSAGE, values.p_env) +
                        "." + linkEnd);
            } // if
            else
            {
                page = StringHelpers.replace (page, UtilConstants.TAG_INBOX,
                    linkBegin + inbox.oid.toString () + linkMiddle +
                        unreadMessages + " " + MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_UNREADMESSAGES, values.p_env) +
                        "." + linkEnd);
            } // else
        } // else


        // compose the user info
        // display the users name or fullname
        page = StringHelpers.replace (page, UtilConstants.TAG_USERNAME, values.getUser ().username);
        page = StringHelpers.replace (page, UtilConstants.TAG_USERFULLNAME, values.getUser ().fullname);

        // display the users last login
        if (userInfo.userProfile.lastLogin != null)
        {
            page = StringHelpers.replace (page, UtilConstants.TAG_LASTLOGIN,
                MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_LASTLOGIN, values.p_env) + " " +
                    DateTimeHelpers.dateTimeToString (userInfo.userProfile.lastLogin));
        } // if
        else
        {
            page = StringHelpers.replace (page, UtilConstants.TAG_LASTLOGIN,
                MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                    AppMessages.ML_MSG_FIRSTLOGIN, values.p_env));
        } // else

        // check if the page contains a TAG_CUSTOMINCLUDES
        // that indicates that there should be any custom code be inserted
        if (page.indexOf (UtilConstants.TAG_INCLUDES) != -1)
        {
            Include_01 welcomeIncludes = new Include_01 ();
            welcomeIncludes.initObject (OID.getEmptyOid (), values.getUser (),
                values.p_env, values.p_sess, values.p_app);
            // get the includes
            String includeStr = welcomeIncludes.getWelcomeIncludes ();
            // did we get any includes?
            if (includeStr != null && includeStr.length () > 0)
            {
                page = StringHelpers.replace (page, UtilConstants.TAG_INCLUDES,
                    includeStr);
            } // if
        } // if (page.indexOf (UtilConstants.TAG_LASTLOGIN) != -1)

        if (userInfo.userProfile != null)
        {
            try
            {
                userInfo.userProfile.setEnv (values.p_env); // set environment
                userInfo.userProfile.sess = values.p_sess; // get session object
                userInfo.userProfile.updateLastLogin ();
            } // try
            catch (NoAccessException e)
            {
                userInfo.userProfile.showNoAccessMessage (Operations.OP_CHANGE);
            } // catch
        } // if

        // Insert change password ssi if necessary:
        try
        {
            String text = "";

            // check if password has to be changed
            if (values.getUser ().p_changePwd)
            {
                // login info:
                text = IOHelpers.getSSIFile (AppConstants.SSI_CHANGEPWD,
                        values.p_env,
                        values.getConfiguration ());

                text = StringHelpers.replace (text, UtilConstants.TAG_CHANGEPWD_INFO_TEXT_1, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_CHANGE_PWD_INFO_1, values.p_env));
                text = StringHelpers.replace (text, UtilConstants.TAG_CHANGEPWD_INFO_TEXT_2, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_CHANGE_PWD_INFO_2, values.p_env));
                text = StringHelpers.replace (text, UtilConstants.TAG_YES, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_BUTTON_YES, values.p_env));
                text = StringHelpers.replace (text, UtilConstants.TAG_NO, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_BUTTON_NO, values.p_env));
            } // if changePwd

            page = StringHelpers.replace (page, UtilConstants.TAG_CHANGEPWD, text);
        } // try
        catch (SsiFileNotFoundException e)
        {
            IOHelpers.showMessage (e, values.p_app, values.p_sess, values.p_env);
        } // catch

        values.p_env.write (page);
    } // showWelcome
    
    
    /**************************************************************************
     * Retrieve multilang client info (text, messages, buttons) by redirecting
     * to the multilang info html page according to the users locale. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    private void retrieveMultilangClientInfo (FunctionValues values)
    {        
        // Retrieve the url of the html page containing the client ml texts
        // for the current user's locale
        String url = values.getUserInfo ().homepagePath + BOPathConstants.PATH_MLI_CLIENT_TEXTS +
            MultilingualTextProvider.getClientMultilangInfoFilename (
                    MultilingualTextProvider.getUserLocale (values.p_env).getLocale ());
        
        // Redirect to this page
        values.p_env.redirect (url);
    } // retrieveMultilangClientInfo
    

    /**************************************************************************
     * Show the switch to search frame page. <BR/>
     * <BR/>
     *
     * @param   values      The values for the function handler.
     */
    private void showSwitchToSearchFrame (FunctionValues values)
    {
        BusinessObject object =
            values.getObject (values.getUserInfo ().history.getOid ());

        String buttonBarCall = object.getButtonBarCall (false);

        StringBuffer page = new StringBuffer ()
            .append ("<HTML>\n")
            .append ("<HEAD>\n")
            .append ("<TITLE>Switch to search form</TITLE>\n")
            .append ("</HEAD>\n")
            .append ("<BODY>\n")
            .append ("<SCRIPT LANGUAGE=\"JavaScript\">\n")
            .append ("<!--\n")
            .append ("top.scripts.showSearchFrame (false, false, false);\n")
            .append (buttonBarCall).append ("\n")
            .append ("//        -->\n")
            .append ("</SCRIPT>\n")
            .append ("</BODY>\n")
            .append ("</HTML>\n");

        values.p_env.write (page.toString ());

    } // showSwitchToSearchFrame


    /**************************************************************************
     * Re-initialize the buttons for the current user. <BR/>
     * The buttons must be set for each new login and for each new layout.
     *
     * @param   values      The values for the function handler.
     */
    protected void initializeUserButtons (FunctionValues values)
    {
        try
        {
            // build the general button bar with all possible buttons
            // make a new buttonBar element:
            (values.getUserInfo ()).buttonBar =
                (ButtonBarElement) values.p_app.p_buttons.clone ();
        } // try
        catch (CloneNotSupportedException e)
        {
            // nothing to do
        } // catch
    } // initializeUserButtons


    /**************************************************************************
     * Disable a specific button for the current user. <BR/>
     * This method searches through the enabled buttons of the current and sets
     * the required button to inactive. If the button is not found, nothing
     * is done.
     *
     * @param   values      The values for the function handler.
     * @param   id          The id of the button.
     */
    private void disableButton (FunctionValues values, int id)
    {
        ButtonElement button = null;    // the button

        // search for the button within the buttons of the current user:
        if ((button = values.getUserInfo ().buttonBar.getButton (id)) != null)
                                        // found the button?
        {
            // set the button inactive:
            button.active = false;
        } // if found the button
    } // disableButton


    /**************************************************************************
     * Sets all possible buttons and constructs a buttonBar element. <BR/>
     *
     * @param   buttonBar   a ButtonBarElement Object where the buttons shall
     *                      be added to.
     */
    public void setButtons (ButtonBarElement buttonBar)
    {
        // go back:
        this.setButton (buttonBar, Buttons.BTN_BACK, "top.goback (1);", 0, 0);

        // go forward
        this.setButton (buttonBar, Buttons.BTN_GOFORWARD, "top.goforward (1);", 0, 0);

        // goto container:
        this.setButton (buttonBar, Buttons.BTN_GOTOCONTAINER,
            "top.content ();", Operations.OP_VIEW, Operations.OP_VIEWELEMS);

        // new:
        this.setButton (buttonBar, Buttons.BTN_NEW, IOHelpers
            .getLoadContJavaScript (AppFunctions.FCT_OBJECTNEWFORM),
            Operations.OP_NEW | Operations.OP_ADDELEM, 0);

        // import:
        this.setButton (buttonBar, Buttons.BTN_IMPORT, IOHelpers
            .getLoadContJavaScript (AppFunctions.FCT_SHOWOBJECTIMPORTFORM),
            Operations.OP_NEW | Operations.OP_ADDELEM, 0);

        // export:
        this.setButton (buttonBar, Buttons.BTN_EXPORT, IOHelpers
            .getLoadJavaScript (AppFunctions.FCT_LISTEXPORTFORM), 0, 0);

        // add rights:
        this.setButton (buttonBar, Buttons.BTN_RIGHTSNEW, IOHelpers
            .getLoadContJavaScript (AppFunctions.FCT_OBJECTNEWFORM),
            Operations.OP_SETRIGHTS, 0);

        // checkout:
        this.setButton (buttonBar, Buttons.BTN_CHECKOUT, IOHelpers
            .getLoadJavaScript (AppFunctions.FCT_CHECKOUT),
            Operations.OP_CHANGE, 0);

        // checkin:
        this.setButton (buttonBar, Buttons.BTN_CHECKIN, IOHelpers
            .getLoadJavaScript (AppFunctions.FCT_CHECKIN),
            Operations.OP_CHANGE, 0);

        // webdavcheckout:
        this.setButton (buttonBar, Buttons.BTN_WEBDAVCHECKOUT, IOHelpers
            .getLoadJavaScript (AppFunctions.FCT_WEBDAVCHECKOUT),
            Operations.OP_CHANGE, 0);

        // webdavcheckin:
        this.setButton (buttonBar, Buttons.BTN_WEBDAVCHECKIN, IOHelpers
            .getLoadJavaScript (AppFunctions.FCT_WEBDAVCHECKIN),
            Operations.OP_CHANGE, 0);

        // edit:
        this.setButton (buttonBar, Buttons.BTN_EDIT, IOHelpers
            .getLoadJavaScript (AppFunctions.FCT_OBJECTCHANGEFORM),
            Operations.OP_EDIT, 0);

        // edit rights:
        this.setButton (buttonBar, Buttons.BTN_RIGHTSEDIT, IOHelpers
            .getLoadJavaScript (AppFunctions.FCT_OBJECTCHANGEFORM),
            Operations.OP_SETRIGHTS, 0);

        // change password:
        this.setButton (buttonBar, Buttons.BTN_CHANGEPASSWORD, IOHelpers
            .getLoadJavaScript (AppFunctions.FCT_CHANGEPASSWORDFORM), 0, 0);

        // listchange:
        this.setButton (buttonBar, Buttons.BTN_LISTCHANGE, IOHelpers
            .getLoadJavaScript (AppFunctions.FCT_LISTCHANGEFORM),
            Operations.OP_NEW | Operations.OP_ADDELEM | Operations.OP_DELELEM,
            0);

        // show selection list:
        this.setButton (buttonBar, Buttons.BTN_ASSIGN, IOHelpers
            .getLoadJavaScript (AppFunctions.FCT_SHOWSELECTFORM),
            Operations.OP_ADDELEM | Operations.OP_DELELEM, 0);

        // assign rights:
        this.setButton (buttonBar, Buttons.BTN_ASSIGNRIGHTS,
            "top.loadTabEvent (" + AppFunctions.FCT_LISTCHANGEFORM + ")",
            Operations.OP_EDITRIGHTS | Operations.OP_SETRIGHTS, 0);

        // delete:
        this.setButton (buttonBar, Buttons.BTN_DELETE, IOHelpers
            .getLoadJavaScript (AppFunctions.FCT_OBJECTDELETECONFIRM,
                "top.temp"), Operations.OP_DELETE | Operations.OP_DELELEM, 0);

        // delete rights:
        this.setButton (buttonBar, Buttons.BTN_RIGHTSDELETE, IOHelpers
            .getLoadJavaScript (AppFunctions.FCT_OBJECTDELETECONFIRM,
                "top.temp"), Operations.OP_SETRIGHTS, 0);

        // delete in list:
        this.setButton (buttonBar, Buttons.BTN_LISTDELETE, IOHelpers
            .getLoadJavaScript (AppFunctions.FCT_LISTDELETEFORM),
            Operations.OP_DELELEM, 0);

        // delete in list for rights:
        this.setButton (buttonBar, Buttons.BTN_LISTDELETERIGHTS,
            "top.loadTabEvent (" + AppFunctions.FCT_LISTDELETEFORM + ")",
            Operations.OP_SETRIGHTS, 0);

        // clean log:
        this.setButton (buttonBar, Buttons.BTN_CLEANLOG, IOHelpers
            .getLoadJavaScript (AppFunctions.FCT_CLEANLOG), 0,
            Operations.OP_VIEWPROTOCOL);

        // delete entries:
        this.setButton (buttonBar, Buttons.BTN_DELETEENTRIES, IOHelpers
            .getLoadJavaScript (AppFunctions.FCT_LISTDELETE), 0,
            Operations.OP_DELELEM);

        // cut:
        this.setButton (buttonBar, Buttons.BTN_CUT, IOHelpers
            .getLoadJavaScript (AppFunctions.FCT_OBJECTCUT, "top.temp"), 0,
            Operations.OP_DELELEM);

        // copy:
        this.setButton (buttonBar, Buttons.BTN_COPY, IOHelpers
            .getLoadJavaScript (AppFunctions.FCT_OBJECTCOPY, "top.temp"),
            Operations.OP_VIEW, 0);

        // paste:
        this.setButton (buttonBar, Buttons.BTN_PASTE, IOHelpers
            .getLoadJavaScript (AppFunctions.FCT_OBJECTPASTE),
            Operations.OP_NEW | Operations.OP_ADDELEM, 0);

        // paste reference:
        this.setButton (buttonBar, Buttons.BTN_REFERENCE, IOHelpers
            .getLoadJavaScript (AppFunctions.FCT_REFERENCE), Operations.OP_NEW |
            Operations.OP_ADDELEM, 0);

        // distribute:
        this.setButton (buttonBar, Buttons.BTN_DISTRIBUTE, IOHelpers
            .getLoadJavaScript (AppFunctions.FCT_NOTIFICATION),
            Operations.OP_DISTRIBUTE, 0);

        // clean:
        this.setButton (buttonBar, Buttons.BTN_CLEAN, IOHelpers
            .getLoadJavaScript (AppFunctions.FCT_OBJECTCLEANFORM), 0, 0);

        // set rights recursive:
        this.setButton (buttonBar, Buttons.BTN_SETRIGHTSREC,
            "top.loadTabEvent (" + AppFunctions.FCT_SETRIGHTSREC + ")",
            Operations.OP_SETRIGHTS, 0);

        // activate:
        this.setButton (buttonBar, Buttons.BTN_ACTIVATE, IOHelpers
            .getLoadJavaScript (AppFunctions.FCT_ACTIVATE, "top"), 0, 0);

        // search:
        this.setButton (buttonBar, Buttons.BTN_SEARCH, IOHelpers
            .getLoadJavaScript (AppFunctions.FCT_OBJECTSEARCH), 0, 0);

        // login:
        this.setButton (buttonBar, Buttons.BTN_LOGIN, IOHelpers
            .getLoadJavaScript (AppFunctions.FCT_LOGINFORM), 0, 0);

        // help:
        this.setButton (buttonBar, Buttons.BTN_HELP, "top.showHelp ()", 0, 0);

        // cut in list:
        this.setButton (buttonBar, Buttons.BTN_LIST_CUT, IOHelpers
            .getLoadJavaScript (AppFunctions.FCT_LISTCUTFORM),
            Operations.OP_DELELEM, 0);

        // copy in list:
        this.setButton (buttonBar, Buttons.BTN_LIST_COPY, IOHelpers
            .getLoadJavaScript (AppFunctions.FCT_LISTCOPYFORM),
            Operations.OP_VIEW, 0);

        // start workflow
        this.setButton (buttonBar, Buttons.BTN_STARTWORKFLOW,
            "top.loadEvent (" + AppFunctions.FCT_WORKFLOW + "," +
                WorkflowEvents.EVT_STARTWORKFLOW + ")",
            Operations.OP_DISTRIBUTE | Operations.OP_CHANGE |
                Operations.OP_READ | Operations.OP_DELETE, 0);

        // workflow: forward object
        this.setButton (buttonBar, Buttons.BTN_FORWARD, "top.loadEvent (" +
            AppFunctions.FCT_WORKFLOW + "," + WorkflowEvents.EVT_FORWARD + ")",
            Operations.OP_READ, 0);

        // finish workflow
        this.setButton (buttonBar, Buttons.BTN_FINISHWORKFLOW,
            "top.loadEvent (" + AppFunctions.FCT_WORKFLOW + "," +
                WorkflowEvents.EVT_FINISHWORKFLOW + ")", Operations.OP_READ, 0);

        // abort workflow
        this.setButton (buttonBar, Buttons.BTN_ABORTWORKFLOW,
            "top.loadEvent (" + AppFunctions.FCT_WORKFLOW + "," +
                WorkflowEvents.EVT_ABORTWORKFLOW + ")", Operations.OP_READ, 0);

        // workflow: show form to forward all objects out of a list
        this.setButton (buttonBar, Buttons.BTN_LISTFORWARD, IOHelpers
            .getLoadJavaScript (AppFunctions.FCT_LISTFORWARDFORM),
            Operations.OP_VIEWELEMS, Operations.OP_READ | Operations.OP_VIEW);
        // hack: normally only the wf-current-owner should be able to
        // see/select/forward objects that are already attached to a
        // workflow --> not possible in this button-architecture!

        // undelete
        this.setButton (buttonBar, Buttons.BTN_UNDELETE, IOHelpers
            .getLoadJavaScript (AppFunctions.FCT_SHOWUNDELETEFORM),
            Operations.OP_READ, 0);

        // editbeforecheckin
        this.setButton (buttonBar, Buttons.BTN_EDITBEFORECHECKIN, IOHelpers
            .getLoadJavaScript (AppFunctions.FCT_EDITBEFORECHECKIN),
            Operations.OP_CHANGE, 0);

        // container checkin
        this.setButton (buttonBar, Buttons.BTN_CHECKINCONTAINER, IOHelpers
            .getLoadJavaScript (AppFunctions.FCT_CHECKINCONTAINER), 0,
            Operations.OP_CHANGE);

        // container checkout
        this.setButton (buttonBar, Buttons.BTN_CHECKOUTCONTAINER, IOHelpers
            .getLoadJavaScript (AppFunctions.FCT_CHECKOUTCONTAINER), 0,
            Operations.OP_CHANGE);

        // editbeforecheckincontainer
        this
            .setButton (
                buttonBar,
                Buttons.BTN_EDITBEFORECHECKINCONTAINER,
                IOHelpers
                    .getLoadJavaScript (AppFunctions.FCT_EDITBEFORECHECKINCONTAINER),
                Operations.OP_CHANGE, 0);

        // generate translator
        this.setButton (buttonBar, Buttons.BTN_GENERATETRANSLATOR, IOHelpers
            .getLoadJavaScript (AppFunctions.FCT_SHOWGENERATETRANSLATORFORM),
            Operations.OP_CHANGE, 0);

        // search and reference
        this.setButton (buttonBar, Buttons.BTN_SEARCHANDREFERENCE,
            "top.showSearch (top.sheet.searchQuery, top.containerId)",
            Operations.OP_READ, 0);

        // new and reference
        this.setButton (buttonBar, Buttons.BTN_NEWANDREFERENCE,
            "top.showSearch (top.sheet.newQuery, top.containerId)",
            Operations.OP_READ, 0);

        // goto container:
        this.setButton (buttonBar, Buttons.BTN_GOTOCONTAINEREXT,
            "top.loadEvent (900,2);", 0, 0);

        // go back:
        this.setButton (buttonBar, Buttons.BTN_NEWEXT,
            "top.loadEvent (900,3);", 0, 0);
    } // setButtons


//    /***************************************************************************
//     * Sets the dependencies of the files filled. <BR/>
//     */
//    
// commented out by gw
// reason: call of empty functions
//    public void setDependentProperties ()
//    {
//        BOMessages.setDependentProperties ();
//        BOTokens.setDependentProperties ();
//        UserTokens.setDependentProperties ();
//        Operations.setDependentProperties ();
//    } // setDependentProperties


    /***************************************************************************
     * Read the configuration data from the configuration files. The data read
     * is stored in the application info. (read when application starts up) <BR/>
     *
     * @param values The values for the function handler.
     */
    public void getConfig (FunctionValues values)
    {
        StringBuffer confMessage = null;      // message from reading the config.
        String message = "";            // message to be displayed
        Page page = null;               // the HTML page
        ParagraphElement para = null;   // the actual paragraph
        Font font = null;               // the used font
        TextElement text = null;        // the current text
        Configuration tempConf;         // the configuration

        // reset the servers
//        values.getConfiguration ().getConfigurationServers ().resetServers ();

        try
        {
            tempConf = new Configuration (values.getConfiguration ()
                .getConfigPath ());
            tempConf.getDbConf ().setDbLogDir (
                FileHelpers.makeFileNameValid (
                    values.p_app.p_system.p_m2AbsBasePath +
                    BOPathConstants.PATH_SQLLOGS));

        } // try
        catch (ConfigurationException e)
        {
            IOHelpers.showMessage (e, values.p_app, values.p_sess,
                values.p_env, true);
            // finish the method:
            return;
        } // catch

        // get the configuration:
        if (tempConf.readConfig ())
        {
            values.p_app.configuration = tempConf;
        } // if
        else
        {
            confMessage = tempConf.getErrors ();
        } // else

//trace ("KR got the configuration.");
        // add the configuration which was read to the message:
        message += "" + values.getConfiguration ().toString ();


        // display the page:
        page = new Page ("get Configuration", false);
        // set the document's base:
        IOHelpers.setBase (page, values.p_app, values.p_sess, values.p_env);

        para = new ParagraphElement ();
        para.addElement (new TextElement ("<H1>Read the Configuration</H1>"));
        page.body.addElement (para);

        // check if there is a message to display:
        if (confMessage != null)        // a message shall be displayed?
        {
            para.addElement (new TextElement ("<H2>Errors occurred:</H2>"));
            // add the message to the page:
            para = new ParagraphElement ();
            font = new Font ();
            font.color = "red";
            text = new TextElement (confMessage.toString ());
            text.font = font;
            para.addElement (text);
            page.body.addElement (para);
            para.addElement (new TextElement (IE302.TAG_NEWLINE +
                "<H2>Old Configuration is active one:</H2>" + IE302.TAG_NEWLINE));
            para.addElement (new TextElement (values.getConfiguration ().toString ()));
        } // if a message shall be displayed
        else
        {
            // add the message to the page body:
            para = new ParagraphElement ();
            para.addElement (new PreElement (new TextElement (message)));
            page.body.addElement (para);
        } // else no error occurred

        // build the page and show it to the user:
        try
        {
            page.build (values.p_env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e.getMsg (), values.p_app, values.p_sess, values.p_env);
        } // catch
    } // getConfig


    /**************************************************************************
     * This method checks if the object identified by oid is a container. If so
     * multiple operation has to be used and therefore true is returned. <BR/>
     *
     * @param   values      The values for the function handler.
     *
     * @return  <CODE>true</CODE> if multiple operation has to be used,
     *          <CODE>false</CODE> if single operation has to be used.
     */
    protected boolean useMultipleOperation (FunctionValues values)
    {
        BusinessObject obj;

        obj = values.getObject ();
        // use multiple operation, if object is a container and the current tab
        // is a content tab
        return (obj.isContainer) && values.getUserInfo ().inContentView;
    } // useMultipleOperation


    /**************************************************************************
     * Builds the tabs for the navigation. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void tabNavigation (FunctionValues values)
    {
//trace ("--- START tabNavigation ---");
        MenuData_01 menDat;             // the data object for the information
                                        // of one tab
        StringBuffer javaScriptCode = new StringBuffer ();
                                        // the javascript code to be processed
        StringBuffer tabscript = new StringBuffer ();

//        getSessionObject ();

        // create script for creating the client-side structure for the
        // navigation bar and displaying it:
        javaScriptCode.append (
            // define function which shall load the navigation tabs:
            "function loadNavTabs ()\n" +
            "{\n" +
            "var v_navBar = top.scripts.actNavBar;\n");

//trace ("this.sess.menus:" + values.p_sess.menus);
        // create the script code for the several navigation tabs:
        if (values.p_sess.menus != null)    // the vector for the tabs isn´t empty ?
        {
            // gets all tabs from the vector and builts for one tab one
            // Java-Script row:
            for (int i = 0; i < values.p_sess.menus.size (); i++)
            {
                menDat = values.p_sess.menus.elementAt (i);
                
                tabscript.append (
                    "v_navBar.setTab ('" +
                        // int String id, in tabId
                        menDat.name + "', " + i + ", '" +
                        // String name, int width, int height
                        menDat.name + "', 35, 74, '" +
                        // String activeClassId
                        menDat.classFront + "', '" +
                        // String inactiveClassId
                        menDat.classBack + "', '" +
                        // As description retrieve the multilang name for the menu tab object:
                        menDat.getMlName (values.p_env) + "', '" +
                        // String htmlfile
                        menDat.filename + "', '" +
                        // OID tabOid
                        menDat.oid + "');\n");
            } // for i
        } // if the vector for the tabs isn´t empty

        javaScriptCode.append (tabscript);

        // finish the script function:
        javaScriptCode.append (
            "v_navBar.loadingFinished ();\n" +
            "} // loadNavTabs\n" +

            // create synchronized call for loading the navigation tabs:
            "top.tryCall (top.getFrameName (window) + '.loadNavTabs ()');");

        // Here should be considered which tab is open for the user
        // or if there should be no tab opened
        String actMenu = values.p_env.getStringParam (BOArguments.ARG_MENU);
        if ((actMenu != null) && (actMenu.equalsIgnoreCase ("none")))
        {
            actMenu = null;
        } // if
        // save the name of the current active Tabmenu in the session
        values.p_sess.p_actMenu = actMenu;

        // activate the correct menu and display the navigation bar:
        if (actMenu != null)            // activate a menu tab?
        {
            // create call for activating the required tab:
            javaScriptCode.append (
                "top.tryCall ('top.scripts.setNavTabNoSheet (\\'" + actMenu + "\\')');");
        } // if activate a menu tab
        else                            // don't activate menu tab
        {
            javaScriptCode.append (
                // create synchronized call for displaying the actual
                // navigation bar:
                "top.tryCall ('top.scripts.actNavBar.show ()');");
        } // else don't activate menu tab

        // create the output:
        IOHelpers.processJavaScriptCode (javaScriptCode.toString (),
                                         values.p_app, values.p_sess, values.p_env);
    } // tabNavigation


    /**************************************************************************
     * This Method builds the menu on the left side. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    public void createMenu (FunctionValues values)
    {
        Menu_01 menu = null;            // the menu object
        OID oid = null;                 // the menu object's oid
        MenuData_01 help = null;        // the actual menu entry
        String actMenuName = null;      // the menu name
        OID rootObjOid = null;          // oid of root object within menu tree
        String openNodes = null;        // the open nodes within the menu tree

        // get the arguments for the actual menu from the enviroment:
        actMenuName = values.p_env.getStringParam (BOArguments.ARG_MENU);
        rootObjOid = values.p_env.getOidParam (BOArguments.ARG_SEARCHROOTCONTAINERID);
        openNodes = values.p_env.getStringParam (BOArguments.ARG_OPENNODES);

        if (actMenuName == null)        // the environment argument is empty?
        {
            actMenuName = values.p_sess.p_actMenu;
        } // if the environment argument is empty

        // get the data class for the actual user, which contains the
        // information of one tab
        for (int i = 0; i < values.p_sess.menus.size (); i++)
        {
            help = values.p_sess.menus.elementAt (i);
            if (actMenuName.compareTo (help.name) == 0)
            {
                oid = help.oid;
                break;
            } // if
        } // for i

        // get the menu object:
        if (oid != null &&
            (menu = (Menu_01) values.getNewObject (TypeConstants.TC_Menu)) != null)
                                        // got the object?
        {
            // set the oid of the underlying object:
            menu.setOid (oid);
            // set the oid of the search root object:
            menu.setRootObjOid (rootObjOid);
            // set the open nodes within the menu:
            menu.setOpenNodes (openNodes);
            // set menu data:
            menu.setMenuData (help);

            try
            {
                // retrieve the common data of the object:
                menu.retrieve (Operations.OP_READ);
                // get the information and show the menu:
//                menu.show (values.p_representationForm);
                // use specific functionality for displaying the menu:
                menu.showMenu ();
            } // try
            catch (NoAccessException e) // no access to object allowed
            {
                menu.showNoAccessMessage (Operations.OP_READ);
            } // catch
            catch (AlreadyDeletedException e) // the object was deleted
            {
                // send message to the user:
                menu.showAlreadyDeletedMessage ();
            } // catch
            catch (ObjectNotFoundException e)
            {
                // send message to the user:
                menu.showAlreadyDeletedMessage ();
            } // catch
        } // if got the object
        else                            // didn't get object
        {
            IOHelpers.showPopupMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env), 
                null, values.p_app, values.p_sess, values.p_env);
        } // else didn't get object
    } // createMenu


    /*************************************************************************
     * This method is executed when a redirect is done from a secure mode
     * (https-URL) to a insecure one (http-URL). Therefore the
     * sessionInfo-Object which temporarily stored is retrieved
     * and the data is copied to the actual sessionInfo-object. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    public void loginChangeToInsecure (FunctionValues values)
    {
        // retrieves the key from the parameters
        String key = values.p_env.getStringParam (BOArguments.ARG_SESSIONINFOID);

        // retrieves the 'old' sessionInfo-object from the hashtable
        SessionInfo s = values.p_app.getSessioninfoTable (key);
        if (s != null)                  // the sessioninfo is found
        {
            // copy the data of the old object to the new sessionInfo-object
            values.p_sess.copy (s);

            // sets the homepagePath to a non-secure URL-string
            (values.getUserInfo ()).homepagePath = Ssl.getNonSecureUrl (values
                .getUserInfo ().homepagePath, values.p_sess);
            // deletes the entry from the hashtable
            values.p_app.deleteSessioninfoTable (key);
            this.showLayout (values);
        } // if the sessioninfo is set already
        else                            // sessioninfo not found, should not occur at all!
        {
            // show login-screen again
            String value = StringHelpers.computeRandomString (); // the calculated random value

            String url = Ssl.getNonSecureUrl (IOHelpers.getBaseUrl (values.p_env),
                                              values.p_sess) +
                         HttpArguments.createArg (
                         BOArguments.ARG_FUNCTION,
                         AppFunctions.FCT_LOGINFORM + value);

            values.p_env.redirect (url);
        } // else sessioninfo not found, should not occur
    } // loginChangeToInsecure


    /**************************************************************************
     * Start the installation. <BR/>
     *
     * @param   values      The values for the function handler.
     *
     * @return  <CODE>true</CODE> if there was something installed,
     *          <CODE>false</CODE> otherwise.
     */
    protected boolean install (FunctionValues values)
    {
        boolean retVal = false;         // the result
        // compute the installation directory:
        // <wwwDir>/app/install/test
        String installDirName =
            values.p_app.p_system.p_m2AbsBasePath + File.separator +
            BOPathConstants.PATHN_APP + File.separator +
            BOPathConstants.PATHN_INSTALL + File.separator;

        // check if a package name has been set
        String pkgName = values.p_env.getParam (BOArguments.ARG_PACKAGE);
        if (pkgName != null && pkgName.length () > 0)
        {
            installDirName += pkgName;
        } // if (pkgName != null && pkgName.length() > 0)
        else    // set the default package directory "xml"
        {
            installDirName += BOPathConstants.PATHN_DEFAULTPACKAGE;
        } // else set the default

        // get a module name that should be reloaded first
        String moduleId = values.p_env.getParam (BOArguments.ARG_MODULE);

        installDirName = FileHelpers.makeFileNameValid (installDirName);
        File installDir = new File (installDirName);
        try
        {
            // try to install the files within the installation
            // directory:
            ApplicationInstaller.install (installDir, values, moduleId);
            retVal = true;
        } // try
        catch (ApplicationInitializationException e)
        {
            IOHelpers.showMessage (
                "install: error in installer",
                        e, values.p_app, values.p_sess, values.p_env, true);
            IOHelpers.printError ("install: error in installer", e, true);
        } // catch

        // return the result:
        return retVal;
    } // install


    /**************************************************************************
     * Call dummy functions which are reserved for future use. <BR/>
     *
     * @param   values      The values for the function handler.
     */
    protected void dummyFunction (FunctionValues values)
    {
        BusinessObject obj;             // the business object

        // get the object:
        if ((obj = values.getObject ()) != null) // got the object?
        {
            // call the function of the object:
            switch (values.p_function)
            {
                case AppFunctions.FCT_DUMMYFUNCTION01:
                    obj.dummyFunction01 ();
                    break;

                case AppFunctions.FCT_DUMMYFUNCTION02:
                    obj.dummyFunction02 ();
                    break;

                case AppFunctions.FCT_DUMMYFUNCTION03:
                    obj.dummyFunction03 ();
                    break;

                case AppFunctions.FCT_DUMMYFUNCTION04:
                    obj.dummyFunction04 ();
                    break;

                case AppFunctions.FCT_DUMMYFUNCTION05:
                    obj.dummyFunction05 ();
                    break;

                case AppFunctions.FCT_DUMMYFUNCTION06:
                    obj.dummyFunction06 ();
                    break;

                case AppFunctions.FCT_DUMMYFUNCTION07:
                    obj.dummyFunction07 ();
                    break;

                case AppFunctions.FCT_DUMMYFUNCTION08:
                    obj.dummyFunction08 ();
                    break;

                case AppFunctions.FCT_DUMMYFUNCTION09:
                    obj.dummyFunction09 ();
                    break;

                case AppFunctions.FCT_DUMMYFUNCTION10:
                    obj.dummyFunction10 ();
                    break;

                default: // nothing to do
            } // switch function
        } // if got the object
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else didn't get object
    } // dummyFunction


    /**************************************************************************
     * Show a message that the requested oid is invalid. <BR/>
     *
     * @param   values      The values for the function handler.
     * @param   oidStr  String representation of incorrect oid to be shown
     *                  within message. <BR/>
     *                  <CODE>null</CODE>: don't show oid.
     */
    public void showIncorrectOidMessage (FunctionValues values, String oidStr)
    {
        // show the message to the user:
        if (oidStr == null)             // no oid for message?
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_INCORRECTOID, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // if no oid for message
        else                            // oid shall be shown in message
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_INCORRECTOID, new String[] {oidStr}, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else oid shall be shown in message
    } // showIncorrectOidMessage


    /**************************************************************************
     * Show debugging text. <BR/>
     *
     * @param   text    Text to be printed out.
     */
    public void debug (StringBuffer text)
    {
        // call common method:
        this.debug (text.toString ());
    } // debug


    /**************************************************************************
     * Show debugging text. <BR/>
     *
     * @param   text    Text to be printed out.
     */
    public void debug (String text)
    {
        String textLocal = this.getClass ().getName () + ": " + text;
        this.trace (textLocal);

//        IOHelpers.debug (text, values.p_sess, values.p_env);
    } // debug


    /**************************************************************************
     * Generate a vcard that can be opened in Outlook. <BR/>
     *
     * @param   values      The values for the function handler.
     *
     * @return  <CODE>true</CODE> if there was something genered
     *          <CODE>false</CODE> otherwise.
     */
    protected boolean generateVCard (FunctionValues values)
    {
/*
        String name = values.p_env.getParam ("name");
        String nName = values.p_env.getParam ("nName");
        String comment = values.p_env.getParam ("comment");
        String title = values.p_env.getParam ("title");
        String organisation = values.p_env.getParam ("organisation");
        // the address must have the format
        // ;;<street>;<city>;;<zipcode>;<country>
        // to be recognized correctly by outlook
        String address = values.p_env.getParam ("address");
        String phone = values.p_env.getParam ("phone");
        String fax = values.p_env.getParam ("fax");
        String mobile = values.p_env.getParam ("mobile");
        String email = values.p_env.getParam ("email");
        String email2 = values.p_env.getParam ("email2");
        String url = values.p_env.getParam ("url");
        // the birthday must have the format YYYYMMDD
        String bday = values.p_env.getParam ("bday");


        String vCard = "BEGIN:VCARD\n"
            + "FN:" + name + "\n"
            + "N:" + nName + "\n"   // added - 16 Aug 01
            + "NOTE;ENCODING=QUOTED-PRINTABLE:" + comment + "\n"   // added - 16 Aug 01
            + "TITLE:" + title + "\n"
            + "ORG:" + organisation + "\n"
            + "ADR;WORK;CHARSET=ASCII:" + address + "\n"
            + "TEL;WORK;VOICE:" + phone + "\n"
            + "TEL;WORK;FAX:" + fax + "\n"
            + "TEL;CELL;VOICE:" + mobile + "\n"
            + "URL;WORK:" + url + "\n"
            + "EMAIL;PREF;INTERNET:" + email + "\n"
            + "EMAIL;INTERNET:" + email2 + "\n"
            + "BDAY:" + bday + "\n"
            + "VERSION:2.1\n"
            + "END:VCARD\n";

        // write the output to the stream:
        values.p_env.setContentType ("text/x-vcard");
        values.p_env.addHeaderEntry ("Content-Length", vCard.length () + "" );
        values.p_env.addHeaderEntry ("Accept-Ranges", "Bytes" );
        values.p_env.addHeaderEntry (HttpConstants.HTTP_HEADER_FILENAME,
            "\"" + name + ".vcf\"");
        values.p_env.write (vCard);
*/
        return true;

    } // generateVCard


    /**************************************************************************
     * Generate a vcalendar that can be opened in Outlook. <BR/>
     *
     * @param   values      The values for the function handler.
     *
     * @return  <CODE>true</CODE> if there was something genered
     *          <CODE>false</CODE> otherwise.
     */
    protected boolean generateVCalendar (FunctionValues values)
    {
        return true;

/* still in progress ...
 *
        String name = "Meeting";
        String location = "Klagenfurt";
        String description  = "This calendar entry has been fully generated out of m2.\n\nEnjoy!";
        String organizer = "MAILTO:m2@trinitec.at";
        String uid = "myUID@trinitec.at";
        Date startDate = DateTimeHelpers.stringToDateTime ("1.1.2007 10:00");
        Date endDate = DateTimeHelpers.stringToDateTime ("2.1.2007 12:30");
        boolean isDateTime = true;

        Calendar vcalendar = new Calendar();
//        vcalendar.getProperties().add (new ProdId ("-//Ben Fortuna//iCal4j 1.0//EN"));
        vcalendar.getProperties().add (
                new ProdId ("-//m2//www.trinitec.at//Version 1.0"));
        vcalendar.getProperties().add (Version.VERSION_2_0);
        vcalendar.getProperties().add (CalScale.GREGORIAN);


        VEvent vEvent = new VEvent ();
        vEvent.getProperties().add (new Summary (name));

        // what kind of calendar entry? full day or exact time
        if (isDateTime)
        {
            // exact day and time
            vEvent.getProperties().add (new DtStart (new DateTime (startDate)));
            vEvent.getProperties().getProperty (Property.DTSTART).getParameters().add (Value.DATE_TIME);
            vEvent.getProperties().add (new DtEnd (new DateTime (endDate)));
            vEvent.getProperties().getProperty (Property.DTEND).getParameters().add (Value.DATE_TIME);
        } // if (isDateTime)
        else // full day
        {
            vEvent.getProperties().add (new DtStart (new net.fortuna.ical4j.model.Date (startDate)));
            vEvent.getProperties().getProperty (Property.DTSTART).getParameters().add (Value.DATE_TIME);
            vEvent.getProperties().add (new DtEnd (new net.fortuna.ical4j.model.Date (endDate)));
            vEvent.getProperties().getProperty (Property.DTEND).getParameters().add (Value.DATE_TIME);
        } // else full day

        try
        {
            // set the location
            vEvent.getProperties().add (new Location (location));
            // set the UID (mandatory for an vEvent
            vEvent.getProperties().add (new Uid (uid));
            // set the description
            vEvent.getProperties().add (new Description (description));
            // set the event to public
            vEvent.getProperties().add (Clazz.PUBLIC);
            // set the sequence
            vEvent.getProperties().add (new Sequence (0));
            // set the priority
            vEvent.getProperties().add (new Priority (0));
            // set the organizer
            vEvent.getProperties().add (new Organizer (new URI (organizer)));
            // add the vevent to the calendar
            vcalendar.getComponents().add (vEvent);

            StringWriter strw = new StringWriter ();
            CalendarOutputter outputter = new CalendarOutputter ();
            outputter.output (vcalendar, strw);

            // write the output to the stream:
            values.p_env.setContentType ("text/x-vcalendar");
            values.p_env.addHeaderEntry ("Content-Length", strw.toString().length () + "" );
            values.p_env.addHeaderEntry ("Accept-Ranges", "Bytes" );
            values.p_env.addHeaderEntry (HttpConstants.HTTP_HEADER_FILENAME,
                "\"" + name + ".vcs\"");
            values.p_env.write (strw.toString());

            return true;
        }
        catch (ValidationException e)
        {
            IOHelpers.showMessage (e.toString (),
                    values.p_app, values.p_sess, values.p_env);
            return false;
        }
        catch (IOException e)
        {
            IOHelpers.showMessage (e.toString (),
                    values.p_app, values.p_sess, values.p_env);
            return false;
        }
        catch (URISyntaxException e)
        {
            IOHelpers.showMessage (e.toString (),
                    values.p_app, values.p_sess, values.p_env);
            return false;
        }
*/
    } // generateVCalendar

} // class IbsFunctionHandler
