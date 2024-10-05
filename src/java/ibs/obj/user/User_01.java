/*
 * Class: User_01.java
 */

// package:
package ibs.obj.user;

// imports:
//KR TODO: unsauber
import ibs.app.AppConstants;
import ibs.app.AppFunctions;
import ibs.app.AppMessages;
import ibs.app.UserInfo;
import ibs.bo.BOArguments;
import ibs.bo.BOConstants;
import ibs.bo.BOHelpers;
import ibs.bo.BOMessages;
import ibs.bo.BOPathConstants;
import ibs.bo.BOTokens;
import ibs.bo.BusinessObject;
import ibs.bo.Buttons;
import ibs.bo.Datatypes;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.bo.ObjectNotFoundException;
import ibs.bo.Operations;
import ibs.bo.SelectionList;
import ibs.bo.States;
import ibs.bo.type.Type;
import ibs.bo.type.TypeConstants;
import ibs.di.DIConstants;
import ibs.di.DIHelpers;
import ibs.di.DataElement;
import ibs.di.KeyMapper;
import ibs.di.KeyMapper.ExternalKey;
import ibs.di.connect.FileConnector_01;
import ibs.di.imp.ImportIntegrator;
import ibs.io.HtmlConstants;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.LayoutConstants;
import ibs.io.SsiFileNotFoundException;
import ibs.io.Ssl;
import ibs.io.SslRequiredException;
import ibs.ml.MultilingualTextProvider;
import ibs.service.action.ActionConstants;
import ibs.service.action.ActionException;
import ibs.service.action.Variable;
import ibs.service.action.Variables;
import ibs.service.conf.Configuration;
import ibs.service.conf.ServerRecord;
import ibs.service.user.User;
import ibs.service.user.UserHelpers;
import ibs.tech.html.BuildException;
import ibs.tech.html.Element;
import ibs.tech.html.FormElement;
import ibs.tech.html.InputElement;
import ibs.tech.html.Page;
import ibs.tech.html.ScriptElement;
import ibs.tech.html.StyleSheetElement;
import ibs.tech.html.TableElement;
import ibs.tech.sql.DBError;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;
import ibs.tech.sql.StoredProcedureConstants;
import ibs.util.DateTimeHelpers;
import ibs.util.FormFieldRelation;
import ibs.util.FormFieldRestriction;
import ibs.util.GeneralException;
import ibs.util.NameAlreadyGivenException;
import ibs.util.NoAccessException;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;
import ibs.util.UtilExceptions;
import ibs.util.WeblinkInfo;
import ibs.util.crypto.EncryptionManager;
import ibs.util.file.FileHelpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;


/******************************************************************************
 * This class represents one object of type User with version 01. <BR/>
 *
 * @version     $Id: User_01.java,v 1.80 2013/01/16 16:14:10 btatzmann Exp $
 *
 * @author      Klaus Reimüller (KR), 980528
 ******************************************************************************
 */
public class User_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: User_01.java,v 1.80 2013/01/16 16:14:10 btatzmann Exp $";

    /**
     * The ext key id of the base workspace template.
     */
    public static final String BASE_WORKSPACE_TEMPLATE_EXT_KEY_ID = "ibs_install";

    /**
     * The ext key domain of the base workspace template.
     */
    public static final String BASE_WORKSPACE_TEMPLATE_EXT_KEY_DOMAIN = "wsptemplate";
    
    /**
     * The user combined with this business object. <BR/>
     * This object can be used to get the data of the actual user.
     */
    public User userData = null;

    /**
     * Stored procedure for logging in. <BR/>
     */
    protected String p_procLogin = "p_User_01$login";

    /**
     * Stored procedure for logging out. <BR/>
     */
    protected String p_procLogout = "p_User_01$logout";

    /**
     * Stored procedure for changing the users password. <BR/>
     */
    protected String p_procChangePassword = "p_User_01$changePassword";

    /**
     * The fullname of the user. <BR/>
     */
    private String p_fullname = "";

    /**
     * The username of the user. <BR/>
     */
    private String p_username = "";

    /**
     * The domain of the user. <BR/>
     */
    private int p_domain;

    /**
     * The password of the user. <BR/>
     */
    private String p_password = "";

    /**
     * Oid of the workSpace. <BR/>
     */
    private OID p_workSpace = null;

    /**
     * Oids of the workSpaceTemplates - not persistent, only used when
     * creating user. <BR/>
     */
    private OID[] p_workspaceTemplateOids = null;

    /**
     * Oid for the memberShip. <BR/>
     */
    public OID memberShip = null;

    /**
     * Array with the ids of the domains. <BR/>
     */
    private String [] p_domainIds = null;

    /**
     * Array with the names of the domains. <BR/>
     */
    private String [] p_domainNames = null;

    /**
     * Defines if the user has to change the password on the next login.
     */
    public boolean p_changePwd = false;

    /**
     * Array with the names of the domains. <BR/>
     */
    public OID personOid;

    /**
     * flag if login has been called from application or not. <BR/>
     * this flag controls wheather there wil be a server side include
     * file added to the login form or not
     */
    private boolean p_isOnTop = false;


    /**************************************************************************
     * This constructor creates a new instance of the class User_01. <BR/>
     */
    public User_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // User_01


    /**************************************************************************
     * This constructor creates a new instance of the class User_01. <BR/>
     * The compound object id is used as base for getting the
     * {@link ibs.bo.OID#server server}, {@link ibs.bo.OID#type type}, and
     * {@link ibs.bo.OID#id id} of the business object. These values are stored
     * in the special public attributes of this type. <BR/>
     * The {@link #user user} object is also stored in a specific
     * attribute of this object to make sure that the user's context can be
     * used for getting his/her rights.
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public User_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:
    } // User_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set the instance's attributes:
        this.procCreate =       "p_User_01$create";
        this.procChange =       "p_User_01$change";
        this.procRetrieve =     "p_User_01$retrieve";
        this.procDelete =       "p_User_01$delete";
        this.procDeleteRec =    "p_User_01$delete";
        this.procChangeState =  "p_User_01$changeState";

        // set number of parameters for procedure call:
        this.specificRetrieveParameters = 6;
        this.specificChangeParameters = 4;
    } // initClassSpecifics


    /**************************************************************************
     * Show login dialog. <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    public void showLoginForm (int representationForm)
    {
        String page = null;
        StringBuffer buf = null;
        Element elem;
        InputElement input;
        String selectedValue;           // value with autoselect
        int preselected = 0;
        String baseUrl = null;


        // try to find the ssi file:
        try
        {
            // get the Server side include file:
            page = IOHelpers.getSSIFile (AppConstants.SSI_LOGINDIALOG,
                this.sess,
                this.env,
                (Configuration) this.app.configuration);

            // get the document base:
            if ((baseUrl = this.getUserInfo ().homepagePath) != null)
                                        // homepagepath is set?
            {
                if (Ssl.isHttpsRequest (this.env, this.app))
                                        // it is a https request?
                {
                    // get the secure URL:
                    baseUrl = Ssl.getSecureUrl (baseUrl, this.sess);
                } // if it is a https request
                else                    // no https request
                {
                    // get the non-secure URL:
                    baseUrl = Ssl.getNonSecureUrl (baseUrl, this.sess);
                } // else no https request
            } // if homepagepath is set

            // set the document base:
            // note that the ending slash is neccessary for some browsers
            page = StringHelpers.replace (page, UtilConstants.TAG_BASE, baseUrl + "/");

            // get the url of the form:
            String actionUrl = null;

            if (((ServerRecord) this.sess.actServerConfiguration).getSsl ())
                                            // a secure url is needed for login
            {
                actionUrl = Ssl.getSecureUrl (this.getBaseUrlPost (), this.sess);
            } // if a secure url is needed for login
            else                            // a non secure url is needed for login
            {
                actionUrl = Ssl.getNonSecureUrl (this.getBaseUrlPost (), this.sess);
            } // else a non secure url is needed to login

            // set the url of the form:
            page = StringHelpers.replace (page, UtilConstants.TAG_FORMURL,
                actionUrl);


            // function number:
            input = new InputElement (BOArguments.ARG_FUNCTION,
                InputElement.INP_HIDDEN, String
                    .valueOf (AppFunctions.FCT_LOGIN));
            buf = new StringBuffer ();
            input.build (this.env, buf);
            // create the fct input field:
            page = StringHelpers.replace (page, UtilConstants.TAG_FCT,
                buf.toString ());

            // set the multilang label for the login page header:
            page = StringHelpers.replace (page, UtilConstants.TAG_LOGIN_PAGE_HEADER,
                    MultilingualTextProvider.getText (
                            BOTokens.TOK_BUNDLE, BOTokens.ML_LOGIN_PAGE_HEADER, env));

            // username:
            selectedValue = "" + this.getUserInfo ().loginLastUsername + "\" onFocus=\"this.select ();";
            input = new InputElement (BOArguments.ARG_USERNAME, InputElement.INP_TEXT, selectedValue);
            // set view-size of input field:
            input.size = 40;        // set default
            // set maximum size of input field's content:
            input.maxlength = 63;
            input.classId = "username";
            buf = new StringBuffer ();
            input.build (this.env, buf);
            // set the multilang label for the username field:
            page = StringHelpers.replace (page, UtilConstants.TAG_USERNAME_LABEL,
                    MultilingualTextProvider.getText (
                            BOTokens.TOK_BUNDLE, BOTokens.ML_USERNAME, env));
            // create the username input field:
            page = StringHelpers.replace (page, UtilConstants.TAG_USERNAME,
                buf.toString ());

            // password:
            selectedValue = "" + "\" onFocus=\"this.select ();";
            input = new InputElement (BOArguments.ARG_PASSWORD, InputElement.INP_PASSWORD, selectedValue);
            // set view-size of input field:
            input.size = 40;        // set default
            // set maximum size of input field's content:
            input.maxlength = 63;
            input.classId = "password";
            buf = new StringBuffer ();
            input.build (this.env, buf);
            // set the multilang label for the password field:
            page = StringHelpers.replace (page, UtilConstants.TAG_PASSWORD_LABEL,
                    MultilingualTextProvider.getText (
                            BOTokens.TOK_BUNDLE, BOTokens.ML_PASSWORD, env));

            // create the password input field:
            page = StringHelpers.replace (page, UtilConstants.TAG_PASSWORD,
                buf.toString ());


            // get the domains out of the database:
            this.performGetDomains ();

            // domain:
            elem = this.getFormSelectProperty (BOArguments.ARG_DOMAIN,
                Integer.toString (this.getUserInfo ().loginLastDomain),
                this.p_domainIds, this.p_domainNames, preselected);
            buf = new StringBuffer ();
            elem.build (this.env, buf);
            // set the multilang label for the domain field:
            page = StringHelpers.replace (page, UtilConstants.TAG_DOMAIN_LABEL,
                    MultilingualTextProvider.getText (
                            BOTokens.TOK_BUNDLE, BOTokens.ML_DOMAIN, env));

            // create the domains selection field:
            page = StringHelpers.replace (page, UtilConstants.TAG_LOGINDOMAINS,
                buf.toString ());
            
            // set the multilang labels for the buttons:
            page = StringHelpers.replace (page, UtilConstants.TAG_OK_BUTTON,
                    MultilingualTextProvider.getText (
                            BOTokens.TOK_BUNDLE, BOTokens.ML_BUTTONOK, env));
            page = StringHelpers.replace (page, UtilConstants.TAG_CANCEL_BUTTON,
                    MultilingualTextProvider.getText (
                            BOTokens.TOK_BUNDLE, BOTokens.ML_BUTTONCANCEL, env));

            // check if the login has been called from the application or not
            if (this.p_isOnTop)
            {
                // add a server side include html file
                // this file is not shown within the application but only
                // when the login form is on top
                String text = "";

                try                         // try to get the ssi file
                {
                    // login info:
                    text = IOHelpers.getSSIFile (AppConstants.SSI_LOGININFO,
                                                 this.sess,
                                                 this.env,
                                                 (Configuration) this.app.configuration);
                    
                    text = StringHelpers.replace (text, UtilConstants.TAG_LOGIN_INFO,
                            MultilingualTextProvider.getText (
                                    BOMessages.MSG_BUNDLE, BOMessages.MSG_LOGIN_INFO, env));
                    
                    page = StringHelpers.replace (page, UtilConstants.TAG_LOGININFO, text);
                } // try to get the ssi file
                catch (SsiFileNotFoundException e)
                                            // ssi file was not found
                {
                    IOHelpers.showMessage (e, this.app, this.sess, this.env, false);
                } // catch ssi file was not found

                // add the actual system info (given as html file) to the body:
                try                         // try to get the ssi file
                {
                    // sys info:
                    text = IOHelpers.getSSIFile (AppConstants.SSI_SYSINFO,
                                               this.sess,
                                               this.env,
                                               (Configuration) this.app.configuration);
                    
                    text = StringHelpers.replace (text, UtilConstants.TAG_INSTALLATION_DATE_LABEL,
                            MultilingualTextProvider.getText (
                                    BOTokens.TOK_BUNDLE, BOTokens.ML_INSTALLTION_DATE, env));
                    
                    text = StringHelpers.replace (text, UtilConstants.TAG_INSTALLATION_LANGUAGE_LABEL,
                            MultilingualTextProvider.getText (
                                    BOTokens.TOK_BUNDLE, BOTokens.ML_INSTALLTION_LANGUAGE, env));
                    
                    page = StringHelpers.replace (page, UtilConstants.TAG_SYSINFO, text);
                } // try to get the ssi file
                catch (SsiFileNotFoundException e)
                                            // ssi file was not found
                {
                    IOHelpers.showMessage (e, this.app, this.sess, this.env, false);
                } // catch ssi file was not found
            } // if (this.p_isOnTop)
            else // disable sysinfo and logininfo
            {
                page = StringHelpers.replace (page, UtilConstants.TAG_LOGININFO, "");
                page = StringHelpers.replace (page, UtilConstants.TAG_SYSINFO, "");
            } // else disable sysinfo and logininfo
        } // try
        catch (SsiFileNotFoundException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env);
        } // catch
        catch (BuildException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env);
        } // catch

        // build the page and show it to the user:
        this.env.write (page);
    } // showLoginForm


    /**************************************************************************
     * Show change password dialog. <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    public void showChangePasswordForm (int representationForm)
    {
        // create new page:
        Page page = new Page (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_CHANGEPASSWORD, env), false);

        // Stylesheetfile wird geladen
        StyleSheetElement style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path + this.env.getBrowser () + "/" +
            this.sess.activeLayout.elems[LayoutConstants.SHEETINFO].styleSheet;
        page.head.addElement (style);

        // create form header
        FormElement form = this.createFormHeader (
            page, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_CHANGEPASSWORD, env), this.getNavItems (), null, null,
            HtmlConstants.FRM_SHEET, null, this.containerName);

        // create form body:
        TableElement table = this.createFrame (representationForm, 2);
        form.addElement (table);

        // loop through properties and display them:
        this.showProperty (table, BOArguments.ARG_USERNAME, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_USERNAME, env),
            Datatypes.DT_NAME, this.getUser ().username);

        this.showFormProperty (table, BOArguments.ARG_OLDPASSWORD,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_OLDPASSWORD, env), Datatypes.DT_PASSWORD, "");
        // restriction: empty not allowed
        this.formFieldRestriction = new FormFieldRestriction (false);

        this.showFormProperty (table, BOArguments.ARG_NEWPASSWORD,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NEWPASSWORD, env), Datatypes.DT_PASSWORD, "");
        // restriction: empty not allowed
        this.formFieldRestriction = new FormFieldRestriction (false);

        this.showFormProperty (table, BOArguments.ARG_CONFIRMPASSWORD,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_CONFIRMPASSWORD, env), Datatypes.DT_PASSWORD, "");

        FormFieldRelation rel1 =
            new FormFieldRelation (Datatypes.DT_PASSWORD,
                BOArguments.ARG_NEWPASSWORD, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NEWPASSWORD, env),
                BOArguments.ARG_CONFIRMPASSWORD, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_CONFIRMPASSWORD, env),
                UtilConstants.FF_REL_EQUALIGNORECASE);
        this.addFormFieldRelation (rel1);

        form.addElement (new InputElement (
            BOArguments.ARG_FUNCTION,
            InputElement.INP_HIDDEN,
            "" + AppFunctions.FCT_CHANGEPASSWORD));

        // check if the redirect to welcome page flag is set
        if (this.env.getSessionObject (BOArguments.ARG_REDIRECT) != null &&
            this.env.getSessionObject (BOArguments.ARG_REDIRECT).equals (
                BOConstants.ARG_VALUE_WELCOME))
        {
            // create form footer with cancel action:
            this.createFormFooter (form, null, "top.callUrl (131, '&" +
                BOArguments.ARG_DISABLE_CHG_PWD + "=true', null, 'sheet');");
        } // if redirect to welcome
        else
        {
            // create form footer without ok action:
            this.createFormFooter (form);
        } // else

        // build the page and show it to the user:
        try
        {
            // try to build the page
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            // show Message
            IOHelpers.showMessage ("User_01.showChangePasswordForm", e.getMsg (),
                                   this.app, this.sess, this.env);
        } // catch
    } // showChangePasswordForm


    /**************************************************************************
     * Perform a login. <BR/>
     *
     * @param   domain      The id of the domain where the user wants to log in.
     * @param   username    Name the user used for authentification.
     * @param   password    The password which the user typed in.
     *
     * @return  The object representing the user if login was successful,
     *          <CODE>null</CODE> otherwise.
     *
     * @see #performLoginData(int, String, String, int)
     */
    public User login (int domain, String username, String password)
    {
        // call common method:
        return this.login (domain, username, password, true);
    } // login


    /**************************************************************************
     * Perform a login without the password given. <BR/>
     * This is used by the NTLM authentication feature that uses the
     * operating system username for login to the application.
     * Note that the given username must match an m2 user! <BR/>
     * The password set in application will be ignored. <BR/>
     *
     * @param   domain      The id of the domain where the user wants to log in.
     * @param   username    Name the user used for authentification.
     *
     * @return  The object representing the user if login was successful,
     *          <CODE>null</CODE> otherwise.
     *
     * @see #performLoginData(int, String, String, int, boolean)
     */
    public User login (int domain, String username)
    {
        User userData = null;
        String password = null;

        // get the password from the user out of the database
        // note that the password is encoded!
        password = this.getPassword (domain, username);

        // check if we got a password
        if (password != null)
        {
            // call common method:
            userData = this.login (domain, username, password, false);
        } // if (password != null)
        else                            // password not found
        {
            // send message to the user:
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                AppMessages.ML_MSG_USERNOTFOUND, this.env), this.app, this.sess, this.env);
        } // else password not found

        // return the user data:
        return userData;
    } // login


    /**************************************************************************
     * Perform a login. <BR/>
     *
     * @param   domain      The id of the domain where the user wants to log in.
     * @param   username    Name the user used for authentification.
     * @param   password    The password which the user typed in.
     * @param   doEncrypt   If <CODE>true</CODE> the encryption manager is
     *                      started to encrypt the password.
     *                      If <CODE>false</CODE> the password must already
     *                      be encrypted.
     *
     * @return  The object representing the user if login was successful,
     *          <CODE>null</CODE> otherwise.
     *
     * @see #performLoginData(int, String, String, int)
     */
    public User login (int domain, String username, String password,
                       boolean doEncrypt)
    {
        int domainLocal = domain;       // variable for local assignments

        if (true)                       // log in on this type?
        {
            // check if a valid domain is set:
            if (domainLocal == 0)            // use default domain?
            {
                // set default domain:
                domainLocal = 1;
            } // if use default domain

            try
            {
                // store the actual try to log in:
                UserInfo userInfo = this.getUserInfo ();
                userInfo.loginLastDomain = domainLocal;
                userInfo.loginLastUsername = username;

                // try to log in:
                this.performLoginData (domainLocal, username, password,
                    Operations.OP_LOGIN, doEncrypt);

/*
                if ((this.userData != null) &&
                    (this.userData.domainName != null) &&
                    this.userData.domainName.equalsIgnoreCase ("Central Network"))
                {
                    this.env.setCookie ("orderidentity", username);
                } // if
*/

                // if the user data where set ensure that the user groups are
                // also stored within these data:
                if (this.userData != null)
                {
                    // set the groups for the user:
                    this.userData.setGroups (UserHelpers.performGetGroups (
                        this.userData.id, this.env, this.sess, this));
                } // if

                // clean login
                // logout later necessary.
                this.sess.loggedIn = true;
            } // try
            catch (NoAccessException e) // no access to objects allowed
            {
                // send message to the user:
                this.showNoAccessMessage (Operations.OP_LOGIN);
            } // catch
        } // if log in on this type
        else                            // log in on another type
        {
            // log in on the other type:
        } // else log in on another type

        return this.userData;           // return the user object
    } // login


    /**************************************************************************
     * Perform a logout. <BR/>
     *
     * @see #performLogoutData
     */
    public void logout ()
    {
        if (true)                       // log in on this type?
        {
            // try to log in:
            this.performLogoutData (this.user);
        } // if log in on this type
        else                            // log in on another type
        {
            // log in on the other type:
        } // else log in on another type
    } // logout


    /**************************************************************************
     * Call the logout procedure for the given user. <BR/>
     *
     * @param   user       The user object with all infos of the user to logout.
     */
    protected void performLogoutData (User user)
    {
        // define local variables:
        int retVal = UtilConstants.QRY_OK;            // return value of query

        // create the stored procedure call:
        StoredProcedure sp = new StoredProcedure(
                this.p_procLogout,
                StoredProcedureConstants.RETURN_VALUE);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // input parameters
        // userId:
        sp.addInParameter(ParameterConstants.TYPE_INTEGER, user.id);

        // oid:
        sp.addInParameter (ParameterConstants.TYPE_VARBYTE, user.oid);

        try
        {
            // perform the function call:
            retVal = BOHelpers.performCallFunctionData(sp, this.env);
            if (retVal == UtilConstants.QRY_OBJECTNOTFOUND) // user not found?
            {
                IOHelpers.showMessage ("User_01.performLogoutData",
                    MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                        AppMessages.ML_MSG_USERNOTFOUND, this.env),
                        this.app, this.sess, this.env);
            } // else if user not found
            else if (retVal == UtilConstants.QRY_NOTVALID) // user is not valid?
            {
                IOHelpers.showMessage ("User_01.performLogoutData",
                    MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                        AppMessages.ML_MSG_USERRANOUT, this.env),
                        this.app, this.sess, this.env);
            } // else if user is not valid
            else
            {
                // user is logged out
                if (this.sess != null)
                {
                    this.sess.loggedIn = false;
                } // if

                // was the call given from the application?
                if (this.env != null)
                {
                    this.debug ("call from Application, session is abandoned");
                    this.showPopupMessage (MultilingualTextProvider
                        .getMessage (AppMessages.MSG_BUNDLE, 
                            AppMessages.ML_MSG_LOGOUT, env));
                    // the current session should be abandoned
                    this.env.abandon ();
                } // if
            } // all right
        } // try
        catch (NoAccessException e)
        {
            // should not occur, nothing to do
        } // catch
    } // performLogoutData


    /**************************************************************************
     * Perform change users password. <BR/>
     *
     * @param   representationForm  The form for displaying an user form.
     */
    public void changePassword (int representationForm)
    {
        String str = null;              // temporary string variable
        String oldPassword = null;      // the old password
        String newPassword = null;      // the new password
        String confirmPassword = null;  // the password confirmation

        // get parameters from change password form:
        // get old password:
        if ((str = this.env.getStringParam (BOArguments.ARG_OLDPASSWORD)) == null)
                                        // parameter is empty?
        {
            str = "";                   // set default value
        } // if
        oldPassword = str;

        // get new password:
        if ((str = this.env.getStringParam (BOArguments.ARG_NEWPASSWORD)) == null)
                                        // parameter is empty?
        {
            str = "";                   // set default value
        } // if
        newPassword = str;

        // get password confirmation:
        str = this.env.getStringParam (BOArguments.ARG_CONFIRMPASSWORD);
        if (str == null || str.isEmpty ())
                                        // parameter is empty?
        {
            str = "";                   // set default value
        } // if
        confirmPassword = str;

        // BM, note:
        // The first two checks should not occur anymore,
        // restrictions and form field relations added in the form

        // check if password was not empty
        if (newPassword.isEmpty ())     // the password was empty?
        {
            // show according message:
            IOHelpers.showMessage ("User_01.changePassword",
                MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                    AppMessages.ML_MSG_PASSWORDEMPTY, this.env),
                    this.app, this.sess, this.env);
            // show change password form again:
            this.showChangePasswordForm (representationForm);
        } // if the password was empty
        // check if password confirmation equals new password:
        else if (!newPassword.equalsIgnoreCase (confirmPassword))
                                        // confirmation wrong?
        {
            // show according message:
            IOHelpers.showMessage ("User_01.changePassword",
                MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                    AppMessages.ML_MSG_PASSWORDCONFIRMFAIL, this.env),
                    this.app, this.sess, this.env);
            // show change password form again:
            this.showChangePasswordForm (representationForm);
        } // else if confirmation wrong
        else                            // confirmation correct?
        {
            try
            {
                // try to log in:
                this.performChangePassword (oldPassword, newPassword);
            } // try
            catch (NoAccessException e) // no access to objects allowed
            {
                // send message to the user:
                this.showNoAccessMessage (Operations.OP_LOGIN);
            } // catch
        } // else confrimation correct
    } // changePassword


    /**************************************************************************
     * Perform the login process. <BR/>
     * This is a wrapper method.
     *
     * @param   domain      The id of the domain where the user wants to log in.
     * @param   username    Name the user used for authentification.
     * @param   password    The password which the user typed in.
     * @param   operation   Operation to be performed with the object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    protected void performLoginData (int domain, String username,
                                     String password, int operation)
        throws NoAccessException
    {
        this.performLoginData (domain, username, password, operation, true);
    } // performLoginData


    /**************************************************************************
     * Perform the login process. <BR/>
     *
     * @param   domain      The id of the domain where the user wants to log in.
     * @param   username    Name the user used for authentification.
     * @param   password    The password which the user typed in.
     * @param   operation   Operation to be performed with the object.
     * @param   doEncrypt   If <CODE>true</CODE> the encryption manager is
     *                      started to encrypt the password.
     *                      If <CODE>false</CODE> the password must already
     *                      be encrypted.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    protected void performLoginData (int domain, String username,
                                     String password, int operation,
                                     boolean doEncrypt)
        throws NoAccessException
    {
        int retVal = UtilConstants.QRY_OK; // return value of query
        boolean isHomepagepathSet = false;
        UserInfo userInfo = this.getUserInfo ();

        // initialize user data:
        this.userData = null;

        // create the stored procedure call:
        StoredProcedure sp = new StoredProcedure(
                this.p_procLogin,
                StoredProcedureConstants.RETURN_VALUE);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // input parameters
        // domainId:
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, domain);
        // username:
        sp.addInParameter (ParameterConstants.TYPE_STRING, username);
        // password:
        if (doEncrypt)
        {
            sp.addInParameter (ParameterConstants.TYPE_STRING, EncryptionManager.encrypt (password));
        } // if
        else                            // encryption turned off
        {
            sp.addInParameter (ParameterConstants.TYPE_STRING, password);
        } // else encryption turned off

        // output parameters
        // oid:
        Parameter oidOutParam = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // id:
        Parameter idOutParam = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
        // fullname:
        Parameter fullnameOutParam = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // domainName:
        Parameter domainNameOutParam = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // sslRequired option for the domain:
        Parameter sslRequiredOutParam = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);
        // changePwd:
        Parameter changePwdOutParam = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);

        // perform the function call:
        retVal = BOHelpers.performCallFunctionData(sp, this.env);

        if (userInfo.homepagePath == null) // hompagepath not set?
        {
            // setting homepagepath now, that it is available already
            // in case of errors (for showmessage). otherwise the
            // icons won't be displayed because of a wrong base-path.
            userInfo.homepagePath = this.getActualHomepagePath ();
        } // if hompagepath not set
        else
        {
            isHomepagepathSet = true;
        } // else

        // check the rights:
        if (retVal == UtilConstants.QRY_INSUFFICIENTRIGHTS)
                                        // access not allowed?
        {
            // raise no access exception
            NoAccessException error =
                new NoAccessException (MultilingualTextProvider.getMessage (
                    UtilExceptions.EXC_BUNDLE, UtilExceptions.ML_E_NOACCESSEXCEPTION, env));
            throw error;
        } // if access not allowed
        else if (retVal == UtilConstants.QRY_OBJECTNOTFOUND) // user not found?
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                AppMessages.ML_MSG_USERNOTFOUND, this.env),
                this.app, this.sess, this.env);
        } // else if user not found
        else if (retVal == UtilConstants.QRY_PASSWORDWRONG) // wrong password?
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                AppMessages.ML_MSG_PASSWORDWRONG, env),
                this.app, this.sess, this.env);
        } // else if wrong password
        else if (retVal == UtilConstants.QRY_NOTVALID) // user is not valid?
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                AppMessages.ML_MSG_USERRANOUT, env),
                this.app, this.sess, this.env);
        } // else if user is not valid
        else                            // access allowed
        {
            try
            {
                this.p_username = username;
                this.p_password = password;

                // set object properties - get them out of parameters
                this.userData =
                    new User (this.p_domain, this.p_username, this.p_password);
                this.userData.oid = SQLHelpers.getSpOidParam (oidOutParam);
                this.userData.id = idOutParam.getValueInteger ();
                this.userData.fullname = fullnameOutParam.getValueString ();
                this.userData.domainName = domainNameOutParam.getValueString ();

                // SSL must be used for the actual domain
                this.sess.sslRequiredDomain =
                    sslRequiredOutParam.getValueBoolean ();

                // changePwd flag
                this.userData.p_changePwd =
                    changePwdOutParam.getValueBoolean ();

                // the actual user is the systemadministrator
                this.sess.sslRequiredUser =
                    Ssl.isAdminUser (username, this.sess);

                if (!isHomepagepathSet) // hompagepath not set?
                {
                    // update homepagepath again - to set the appropriate
                    // path for the actual mode (ssl or not ssl)
                    userInfo.homepagePath = this.getActualHomepagePath ();
                } // if hompagepath not set

                // if SSL is required it must be used, that means
                // SSL must be installed and available and the request
                // must have been done in a secure mode.
                Ssl.satisfySecurityCondition (this, this.env, this.sess);
            } // try
            catch (SslRequiredException sslreq)
            {
                IOHelpers.showMessage ("User_01.performLoginData", sslreq,
                                       this.app, this.sess, this.env, false);
            } //catch
        } // else access allowed
    } // performLoginData


    /**************************************************************************
     * Perform users password change on database. <BR/>
     *
     * @param   oldPassword     The users old password.
     * @param   newPassword     The users new password.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    protected void performChangePassword (String oldPassword, String newPassword)
        throws NoAccessException
    {
        // define local variables:
        int retVal = UtilConstants.QRY_OK;            // return value of query

        // create the stored procedure call:
        StoredProcedure sp = new StoredProcedure(
                this.p_procChangePassword,
                StoredProcedureConstants.RETURN_VALUE);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // input parameters
        // id of the actual user:
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.getUser ().id);
        // users old password:
        sp.addInParameter (ParameterConstants.TYPE_STRING, EncryptionManager.encrypt (oldPassword));
        // users new password:
        sp.addInParameter (ParameterConstants.TYPE_STRING, EncryptionManager.encrypt (newPassword));

        // perform the function call:
        retVal = BOHelpers.performCallFunctionData (sp, this.env);

        // create the page that loads the tabs and the info
        Page page = new Page ("reload", false);
        ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);

        if (retVal == UtilConstants.QRY_OBJECTNOTFOUND) // user not found?
        {
            script.addScript (
                "alert('" + MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                    AppMessages.ML_MSG_USERNOTFOUND, env) + "');\n" +
                "top.load (" + AppFunctions.FCT_CHANGEPASSWORDFORM + ");");
        } // else if user not found
        else if (retVal == UtilConstants.QRY_PASSWORDWRONG) // wrong password?
        {
            script.addScript (
                "alert('" + MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                    AppMessages.ML_MSG_PASSWORDTOCHANGEWRONG, env) + "');\n" +
                "top.load (" + AppFunctions.FCT_CHANGEPASSWORDFORM + ");");
        } // else if wrong password
        else                            // all right?
        {
            // check if change password flag is set
            // reset the change password flag
            (this.getUser ()).p_changePwd = false;

            // check if the redirect to welcome page flag is set
            if (this.env.getSessionObject (BOArguments.ARG_REDIRECT) != null &&
                this.env.getSessionObject (BOArguments.ARG_REDIRECT).equals (
                    BOConstants.ARG_VALUE_WELCOME))
            {
                script.addScript (
                    "alert('" + MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                        AppMessages.ML_MSG_PASSWORDCHANGED, env) + "');\n" +
                    IOHelpers.getLoadJavaScript (131, "top.sheet"));
            } // if change password flag set
            else
            {
                script.addScript (
                    "alert('" + MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                        AppMessages.ML_MSG_PASSWORDCHANGED, env) + "');\n" +
                    IOHelpers.getShowObjectJavaScript ("" + this.getUserInfo ().userProfile.oid));
            } // else
        } // else
        page.body.addElement (script);
        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage ("User_01.performLoginData", e.getMsg (),
                                   this.app, this.sess, this.env);
        } // catch
    } // performLoginData


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
        // fullname:
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // password:
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // workSpace:
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // memberShip:
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // personOid:
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // changePwd:
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);

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
        // full name:
        this.p_fullname = params[++i].getValueString ();
        // password:
        this.p_password = EncryptionManager.decrypt (params[++i].getValueString ());
        // work space:
        this.p_workSpace = SQLHelpers.getSpOidParam (params[++i]);
        // membership:
        this.memberShip = SQLHelpers.getSpOidParam (params[++i]);
        // person oid:
        this.personOid = SQLHelpers.getSpOidParam (params[++i]);
        // changePwd:
        this.p_changePwd = params[++i].getValueBoolean ();
    } // getSpecificRetrieveParameters


    /**************************************************************************
     * Represent the properties of an User_01 object to the user. <BR/>
     *
     * @param   table   Table where the properties shall be added.
     *
     * @see ibs.IbsObject#showProperty(TableElement, String, String, int, String)
     */
    protected void showProperties (TableElement table)
    {
        // display the base object's properties:
        super.showProperties (table);
        // loop through all properties of this object and display them:
        this.showProperty (table, UserArguments.ARG_FULLNAME, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_FULLNAME, env),
            Datatypes.DT_NAME, this.p_fullname);

        this.showProperty (table, UserArguments.ARG_CHANGEPASSWORD, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_CHANGE_PASSWORD, env),
                Datatypes.DT_BOOL, "" + this.p_changePwd);
    } // showProperties


    /**************************************************************************
     * Represent the properties of an User_01 object to the user
     * within a form. <BR/>
     *
     * @param   table   Table where the properties shall be added.
     *
     * @see ibs.IbsObject#showFormProperty(TableElement, String, String, int, String)
     */
    protected void showFormProperties (TableElement table)
    {
        // display the base object's properties:
        super.showFormProperties (table);
        // loop through all properties of this object and display them:
        this.showFormProperty (table, UserArguments.ARG_FULLNAME,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_FULLNAME, env), Datatypes.DT_NAME, this.p_fullname);

        if (this.state == States.ST_CREATED)
                                        // the object is new,
                                        // a password must be entered
        {
            // restriction: empty not allowed
            this.formFieldRestriction = new FormFieldRestriction (false);
        } // if object is new

        // password:
        this.showFormProperty (table, BOArguments.ARG_PASSWORD,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NEWPASSWORD, env), Datatypes.DT_PASSWORD, "");

        if (this.state == States.ST_CREATED)
                                        // the object is new,
                                        // a password must be entered
        {
            // restriction: empty not allowed
            this.formFieldRestriction = new FormFieldRestriction (false);
        } // if object is new

        // password confirmation:
        this.showFormProperty (table, UserArguments.ARG_CHECKPASSWORD,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_CONFIRMPASSWORD, env), Datatypes.DT_PASSWORD, "");

        if (this.state == States.ST_CREATED)
                                        // the object is new, a password
                                        // confirmation must be entered
        {
            // restriction: empty not allowed
            this.formFieldRestriction = new FormFieldRestriction (false);
        } // if the object is new, ...

        // change password flag:
        this.showFormProperty (table, UserArguments.ARG_CHANGEPASSWORD,
                    MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_CHANGE_PASSWORD, env), Datatypes.DT_BOOL, "" + this.p_changePwd);

        FormFieldRelation rel1 =
            new FormFieldRelation (Datatypes.DT_PASSWORD,
                BOArguments.ARG_PASSWORD, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NEWPASSWORD, env),
                UserArguments.ARG_CHECKPASSWORD, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_CONFIRMPASSWORD, env),
                UtilConstants.FF_REL_EQUALIGNORECASE);
        this.addFormFieldRelation (rel1);

        // workspaceTemplate
        if (this.state == States.ST_CREATED)
        {
            SelectionList workSpSelList = this.getWorkspaceSelectionList ();
            
            // check if selection contains any workspace templates
            if (workSpSelList != null && workSpSelList.ids.length > 0)
            {                
                workSpSelList = filterWorkspaceTemplateSelectionList (workSpSelList);
            } // if
            
            this.showFormProperty (table
                      , BOArguments.ARG_WORKSPACETEMPLATE
                      , MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_WORKSPACETEMPLATE, env)
                      , Datatypes.DT_MULTISELECT
                      , OID.oidArrayToString (this.p_workspaceTemplateOids)
                      , workSpSelList.ids
                      , workSpSelList.values
                      , 0 // index of preselected if preselected value (brandNameOid) is not set
            );
        } // if
    } // showFormProperties

    
    /**
     * Filters the provided workspace template selection list by filtering
     * out the base workspace template.
     *  
     * @param workSpSelList The workspace template selection list to filter.
     * 
     * @return  The filtered workspace template selection list.
     */
    private SelectionList filterWorkspaceTemplateSelectionList (
            SelectionList workSpSelList)
    {
        // Base Workspace Template will be removed so length is current length - 1
        int selBoxNewLength = workSpSelList.ids.length - 1;
        
        // selection list ids and values
        String [] idsLocal = new String [selBoxNewLength];
        String [] valuesLocal = new String [selBoxNewLength];
                       
        // Retrieve the OID of the base workspace template
        OID baseWorkspaceTemplateOid = BOHelpers.getOidByExtKey (BASE_WORKSPACE_TEMPLATE_EXT_KEY_ID, BASE_WORKSPACE_TEMPLATE_EXT_KEY_DOMAIN, this.env);
        
        int localI = 0;
        
        // Filter out base workspace template
        for (int i = 0; workSpSelList != null && i < workSpSelList.ids.length; i ++)
        {
            // Check if the current item is the base workspace template
            if (!workSpSelList.ids [i].equals(baseWorkspaceTemplateOid.toString()))
            {
                idsLocal [localI] = workSpSelList.ids [i];
                valuesLocal [localI] = workSpSelList.values [i];
                
                localI ++;
            } // if
        } // for
        
        workSpSelList = new SelectionList (idsLocal, valuesLocal);
        
        return workSpSelList;
    } // filterWorkspaceTemplateSelectionList


    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     * The {@link #env env} property is used for getting the parameters.
     * This property must be set before calling this method. <BR/>
     */
    public void getParameters ()
    {
        String str = null;
        int num = 0;
        String[] multipleStr = null;

        // get other parameters:
        super.getParameters ();

        // get full name of the user:
        if ((str = this.env.getStringParam (UserArguments.ARG_FULLNAME)) != null)
        {
            this.p_fullname = str;
        } // if

        // get id of domain:
        if ((num = this.env.getIntParam (BOArguments.ARG_DOMAIN)) !=
                IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            // check if a valid domain is set
            if (num == 0)
            {
                // set default domain
                num = 1;
            } // if
            
            this.p_domain = num;
        } // if

        // get username:
        if ((str = this.env.getStringParam (BOArguments.ARG_USERNAME)) != null)
        {
            this.p_username = str;
        } // if

        // get password:
        str = this.env.getStringParam (BOArguments.ARG_PASSWORD);
        if (str != null && !str.isEmpty ())
                                        // if password is not null and not ""
        {
            this.p_password = str;
        } // if password is not null and not ""

        // get change password:
        if ((num = this.env.getBoolParam (this
            .adoptArgName (UserArguments.ARG_CHANGEPASSWORD))) >=
                IOConstants.BOOLPARAM_FALSE)
        {
            this.p_changePwd = num == IOConstants.BOOLPARAM_TRUE;
        } // if

        // oid of workspacetemplate
        if (this.state == States.ST_CREATED &&
            (multipleStr = this.env
                .getMultipleParam (BOArguments.ARG_WORKSPACETEMPLATE)) != null)
        {
            try
            {
                // convert the strings to oids:
                this.p_workspaceTemplateOids = OID.stringArrayToOid (multipleStr);
            } // try
            catch (IncorrectOidException e)
            {
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            } // catch
        } // if

        // application. When called from top the argument ARG_FUNCTION does
        // not exist:
        this.p_isOnTop = (this.env.getIntParam (BOArguments.ARG_FUNCTION)) ==
                        IOConstants.INTPARAM_NOTEXISTS_OR_INVALID;
    } // getParameters


    /**************************************************************************
     * Set the workspace templates for the user. <BR/>
     *
     * @param   templateOids    The template oids.
     */
    public void setWorkspaceTemplateOids (OID[] templateOids)
    {
        // set the property value:
        this.p_workspaceTemplateOids = templateOids;
    } // setWorkspaceTemplateOids


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
        // fullname
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        this.p_fullname);
        // state
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
                        this.state);
        // password
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        EncryptionManager.encrypt (this.p_password));

        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN,
                        this.p_changePwd);
    } // setSpecificChangeParameters


    /**************************************************************************
     * This method is used in the showLoginForm method to get the ids and
     * names of the domains which have no own homepage. <BR/>
     */
    private void performGetDomains ()
    {
        SQLAction action = null;        // the action object used to access the database
        int rowCount;
        int tmpCount = 0;
        StringBuffer queryStr;          // the query string
        String requestLine;             // the request line sent by the client
        Vector<String> domIds = new Vector<String> (10, 10); // initialize elements vector
        Vector<String> domNames = new Vector<String> (10, 10); // initialize elements vector
        String applicationServer;       // the current application type
        String ssl = "";                // String to be concatenated for secure
                                        // domains
        int domainId = -1;

        requestLine = this.getUserInfo ().homepagePath;

        // is a domain set in the user info?
        domainId = this.getUserInfo ().loginLastDomain;
        if (domainId == 0)
        {
            // try to get a domain setting from the environment:
            domainId = this.env.getIntParam (BOArguments.ARG_DOMAIN);
        } // if (domainId == 0)

        // get all the ids and names of the domains:
        if (domainId <= 0)              // currently no domain?
        {
            queryStr = new StringBuffer ()
                .append ("SELECT d.id, o.name, d.sslRequired")
                .append (" FROM   ibs_Domain_01 d, ibs_Object o")
                .append (" WHERE  o.oid = d.oid")
                    .append (" AND (")
                .append (SQLHelpers.getQueryConditionAttribute (
                    "\'" + requestLine + "\'", SQLConstants.MATCH_SUBSTRING,
                    "d.homepagePath", true))
                        .append (" OR  (")
                            .append (" NOT EXISTS (")
                                .append (" SELECT  id")
                                .append (" FROM    ibs_Domain_01")
                                .append (" WHERE ")
                                    .append (SQLHelpers.getQueryConditionAttribute (
                                        "\'" + requestLine + "\'", SQLConstants.MATCH_SUBSTRING,
                                        "homepagePath", true))
                            .append (" )")
                            .append (" AND d.homePagePath IS NULL")
                        .append ("   )")
                    .append (" )")
                    .append (" AND o.state = ").append (States.ST_ACTIVE)
                .append (" ORDER BY o.name");
        } // if currently no domain
// --- HACK ---------------------------
        else                            // already logged into domain
        {
            queryStr = new StringBuffer ()
                .append (" SELECT d.id, o.name, d.sslRequired")
                .append (" FROM   ibs_Domain_01 d, ibs_Object o, ibs_Domain_01 actD")
                .append (" WHERE  actD.id =").append (domainId)
                    .append (" AND d.homepagePath = actD.homepagePath")
                    .append (" AND o.oid = d.oid")
                    .append (" AND o.state = ").append (States.ST_ACTIVE)
                .append (" ORDER BY o.name");
        } // else already logged into domain

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();
            rowCount = action.execute (queryStr, false);

            if (rowCount > 0)           // domains found?
            {
                // create string which is concatenated to secure domains
                ssl = " " + MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_DOMAINSSLINDICATOR, env);
                // get the application type:
                applicationServer =
                    ((ServerRecord) this.sess.actServerConfiguration).getApplicationServer ();

                // get tuples out of db
                while (!action.getEOF ())
                {
                    // if SSL is available and installed and
                    // SSL must be used for this domain then
                    // the 'indicator'-string is concatenated
                    // with the domainname
                    if ((((ServerRecord) this.sess.actServerConfiguration).getSsl ()) &&
                        (action.getBoolean ("sslRequired")))
                                        // ssl-required domains should be
                                        // displayed only when SSL is available
                    {
                        domIds.addElement (action.getString ("id"));
                        domNames.addElement (action.getString ("name") + ssl);

                        // increment counter to indicate that at least one
                        // domain was found:
                        tmpCount++;
                    } // if ssl-required domains should be displayed
                    else if (!action.getBoolean ("sslRequired") &&
                             (applicationServer != null &&
                              !applicationServer.isEmpty ()))
                                        // if ssl is not required for the
                                        // domain it should be always displayed
                    {
                        // increment counter to indicate that at least one
                        // domain was found:
                        tmpCount++;

                        domIds.addElement (action.getString ("id"));
                        domNames.addElement (action.getString ("name"));
                    } // else the domain should be displayed at all

                    // step one tuple ahead for the next loop
                    action.next ();
                } // while
                this.p_domainIds = new String[domIds.size ()];
                this.p_domainNames = new String[domNames.size ()];
                domIds.copyInto (this.p_domainIds);
                domNames.copyInto (this.p_domainNames);
            } // if rowcount > 0
            else                        // no domains found
            {
                this.p_domainIds = new String[1];
                this.p_domainNames = new String[1];
                this.p_domainIds[0] = "0";
                this.p_domainNames[0] = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NODOMAINNAME, env);
            } // else no domains found

            if (tmpCount == 0)          // set the 'nodomainname' if no regular
                                        // domain to display
            {
                this.p_domainIds = new String[1];
                this.p_domainNames = new String[1];
                this.p_domainIds[0] = "0";
                this.p_domainNames[0] = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NODOMAINNAME, env);
            } // if at least one domain was found.
        } // try
        catch (DBError dbErr)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (dbErr,
                                   this.app, this.sess, this.env, true);
        } // catch
        finally
        {
            // close db connection in every case -  only workaround -
            // db connection must be handled somewhere else
            this.releaseDBConnection (action);
        } // finally
    } // performGetDomains();


    /**************************************************************************
     * Check if the user is within a specific group. <BR/>
     *
     * @param   groupOid    OID of the group.
     *
     * @return  <CODE>true</CODE> if the user is within the group,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean isInGroup (OID groupOid)
    {
        if (groupOid != null)
        {
            // check if the user is within the group and return the result:
            return (UserHelpers.performCheckIsInGroup (
                        this.oid, groupOid, this.env, this.sess, this));
        } // if

        // the user is not within the group:
        return false;
    } // isInGroup


    /**************************************************************************
     * Move a business object in the Database. <BR/>
     * A user may not be moved. All users have to stay in the users container.
     * "Moving" a user means adding him to a user group. <BR/>
     *
     * @param   targetId    The oid of the target object where to move the
     *                      actual object to. This should be a user group oid.
     * @param   operation   Operation to be performed with the object.
     *                      This parameter is ignored.
     *
     * @exception   NoAccessException
     *              The current user does not have access to this object to
     *              perform the required operation.
     */
    public void performMoveData (OID targetId, int operation)
        throws NoAccessException
    {
        // check if the targetId is a user group:
        Type groupType = this.getTypeCache ().findType (TypeConstants.TC_Group);
        
        if (groupType != null && targetId.isInstanceOf (groupType))
        {
            // create the relationship between group and user:
            this.addToGroup (targetId);
        } // if
    } // performMoveData


    /**************************************************************************
     * Add the user to a specific group. <BR/>
     *
     * @param   groupOid    OID of the group.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    public void addToGroup (OID groupOid)
        throws NoAccessException
    {
        // check if the user is already in this group:
        if (!this.isInGroup (groupOid))
        {
            // call implementation method:
            this.performAddToGroup (groupOid);
        } // if
    } // addToGroup


    /**************************************************************************
     * Add the user to a specific group. <BR/>
     *
     * @param   groupOid    OID of the group.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     */
    private void performAddToGroup (OID groupOid)
        throws NoAccessException
    {
        StoredProcedure sp = new StoredProcedure ("p_Referenz_01$create",
            StoredProcedureConstants.RETURN_VALUE);

        
        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // userId
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
            (this.user != null) ? this.user.id : 0);
        // operation
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, Operations.OP_ADDELEM);
        // tVersionId
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
            this.getTypeCache ().getTVersionId (TypeConstants.TC_Reference));
        // name
        sp.addInParameter (ParameterConstants.TYPE_STRING,
            this.getTypeCache ().getTypeName (TypeConstants.TC_Reference));
        // containerId_s
        BOHelpers.addInParameter (sp, groupOid); // Group
        // containerKind
        sp.addInParameter (ParameterConstants.TYPE_INTEGER,
            BOConstants.CONT_STANDARD);
        // isLink
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN, true);
        // linkedObjectId_s
        BOHelpers.addInParameter (sp, this.oid);
        // description
        sp.addInParameter(ParameterConstants.TYPE_STRING, "");
        // oid_s
        sp.addOutParameter (ParameterConstants.TYPE_STRING);

        // perform the function call:
        BOHelpers.performCallFunctionData (sp, this.env);
    } // performAddToGroup



    /**************************************************************************
     * Change the data of a business object in the database. <BR/>
     * Note that this method overwrites the business object method but calls
     * the method later via the super.peformChangeData .
     * It is checked if the name is not aready in use by another project and
     * performs an transformation on the project template file is it has
     * been selected and performs an import if the project structure with the
     * transformed template. <BR/>
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
    protected void performChangeData (int operation)
        throws NoAccessException, NameAlreadyGivenException
    {
        super.performChangeData (operation);

        OID workspaceOid;   // oid of workspace
        
        // get workspaceTemplate Oid
        // AJ HINT:  in next version oid of template should be read
        // via getParameters from user-selectionbox
        workspaceOid = this.getWorkspaceOid ();
        
        // create/change the ext key for the workspace
        if (!createWorkspaceExtKey (workspaceOid))
        {
            IOHelpers.showMessage(
                    "Error during creating/changing the workspace key mapper entry.",
                    this.app, this.sess, this.env);
        } // if
        
        // if the user is newly created - import xml structure in workspace

        if (this.state == States.ST_CREATED ||
            this.p_workspaceTemplateOids != null)
        {           
            // Add the base workspace template by default:
            
            // retrieve the OID of the base workspace template
            OID baseWorkspaceTemplateOid = BOHelpers.getOidByExtKey (BASE_WORKSPACE_TEMPLATE_EXT_KEY_ID, BASE_WORKSPACE_TEMPLATE_EXT_KEY_DOMAIN, this.env);
            
            // check if a workspace template has been set by the user
            if (this.p_workspaceTemplateOids !=null && this.p_workspaceTemplateOids.length > 0)
            {   
                // add the standard workspace template  and copy the templates selected by the user
                OID[] localWorkspaceTemplateOids = new OID [this.p_workspaceTemplateOids.length + 1];
                
                // add the base workspace template
                localWorkspaceTemplateOids [0] = baseWorkspaceTemplateOid;
                
                // iterate through all template set by the user
                for (int i = 0; i < this.p_workspaceTemplateOids.length; i++)
                {
                    localWorkspaceTemplateOids [i + 1] = this.p_workspaceTemplateOids [i];
                } // for
                
                // set the instance variable of workspace oids to the just created array
                this.p_workspaceTemplateOids = localWorkspaceTemplateOids;
            } // if
            else
            {
                // instantiate a new array and add the base workspace template
                this.p_workspaceTemplateOids = new OID [1];
                this.p_workspaceTemplateOids [0] = baseWorkspaceTemplateOid;
            } // else

            if (workspaceOid != null)
            {
                for (int i = 0; i < this.p_workspaceTemplateOids.length; i++)
                {
                    if (this.p_workspaceTemplateOids[i] != null)
                    {
                        // try to import xml-structure for workspace:
                        this.importWorkspaceTemplate (
                            this.p_workspaceTemplateOids[i], workspaceOid);
                    } // if
                } // for i
            } // if
            
            this.assignStandardWorkspaceObjects ();
        } // if ST_CREATED
    } // performChangeData
    
    
    /***************************************************************************
     * Create the external key for the workspace with the given oid.
     *
     * @param workspaceOid  The oid of the user's workspace
     */
    private boolean createWorkspaceExtKey (OID workspaceOid)
    {
        // instantiate the Keymapper to resolve the external object key:
        KeyMapper keyMapper = new KeyMapper (this.user, this.env, this.sess, this.app);
        
        // try to resolve the external key:
        KeyMapper.ExternalKey extKey = keyMapper.performResolveMapping (workspaceOid, false);
        
        // set the external key
        extKey = new ExternalKey (
                workspaceOid,
                "ibs_instobj", "wsp_root_" + this.name
                );
        
        return keyMapper.performCreateMapping (extKey);
    } // createWorkspaceExtKey


    /**************************************************************************
     * This method is used after importing a xml - structure in the
     * workspace of the user to assign standardobjects like Inbox, Outbox,
     * ShoppingCart, NewsContainer, Hotlist etc. to Workspace
     * (Table ibs_workspace).
     * The objects to be assigned are identified via their type and only the
     * objects are assigned where no other object is assigned to Workspace
     * in procedure p_Workspace$createObjects. <BR/>
     *
     * @throws  NoAccessException
     *          The user does not have the necessary permissions to perform the
     *          operation.
     */
    private void assignStandardWorkspaceObjects ()
        throws NoAccessException
    {
        // create the stored procedure call:
        StoredProcedure sp = new StoredProcedure(
                "p_Workspace$assignStdObjects",
                StoredProcedureConstants.RETURN_VALUE);

        // input parameters
        // oid
        BOHelpers.addInParameter (sp, this.oid);

        // perform the function call:
        BOHelpers.performCallFunctionData (sp, this.env);
    } // assignStandardWorkspaceObjects


    /**************************************************************************
     * import template for workspace of current user. <BR/>
     *
     * @param   templateOid The oid of the template to be imported.
     * @param   targetOid   The oid of the object which will contain the
     *                      template's objects.
     *
     * @return  <CODE>true</CODE> if everything was o.k.,
     *          <CODE>false</CODE> otherwise.
     */
    private boolean importWorkspaceTemplate (OID templateOid, OID targetOid)
    {
        this.debug ("User_01.importWorkspaceTemplate (" + templateOid + ", " + targetOid + ")");
        if (templateOid == null || targetOid == null)
        {
            this.debug ("User_01.importWorkspaceTemplate: wrong parameter values");
            return false;
        } // if

        String templateFileName;
        String templatePath;
        String tempPath;
        WorkspaceTemplate_01 workspaceTemplate = null;
        FileConnector_01 connector;
        ImportIntegrator integrator;

        try
        {
            // initialize workspaceTemplate
            workspaceTemplate = (WorkspaceTemplate_01)
                this.getObjectCache ().fetchObject
                    (templateOid, this.user, this.sess, this.env, false);
        } // try
        catch (GeneralException e)
        {
            IOHelpers.showMessage ("User_01.importWorkspaceTemplate: " +
                                   "Could not fetch workspaceTemplate with oid " +
                                   templateOid.toString (), e,
                                   this.app, this.sess, this.env, false);
            return false;
        } // catch

        templateFileName = workspaceTemplate.fileName;
        templatePath = workspaceTemplate.getPhysicalPath ();

        // create the temporary to replace system variables in import file
        tempPath = this.createTempDir ();

        // create a temporary file from the project template
        // file and replace the projectname in the file
        if (this.transformTemplate (templatePath, tempPath, templateFileName))
        {

            // now start an import
            connector = new FileConnector_01 ();
            connector.initObject (OID.getEmptyOid (), this.user, this.env,
                                  this.sess, this.app);
            connector.setPath (tempPath);
            connector.setFileName (templateFileName);
            // create the integrator object and set the properties
            // to start the import
            integrator = 
            	new ImportIntegrator (this.user, this.env, this.sess, this.app);
            integrator.setContainerId (targetOid);
            integrator.isGetSettingsFromEnv = false;
            integrator.isShowSettings = false;
            integrator.isGenerateHtml = false;
            integrator.setDisplayLog (false);
            integrator.setConnector (connector);
            this.env.dropFormData ();
                        
            this.createHTMLHeader (this.app, this.sess, this.env);
            integrator.startImport ();
            this.createHTMLFooter (this.env);
            
            // remove the temporary directory
            this.deleteTempDir (tempPath);
            // check if import was successfull
            return integrator.getIsSuccessful ();
        } // if

        // remove the temporary directory:
        this.deleteTempDir (tempPath);
        return false;
    } // importWorkspaceTemplate


    /**************************************************************************
     * get oid of workspace of user.
     *
     * @return  The oid of the user's workspace.
     */
    private OID getWorkspaceOid ()
    {
        StringBuffer queryStr = new StringBuffer ()
            .append (" SELECT workspace AS oid FROM ibs_Workspace w, ibs_User u")
            .append (" WHERE userId = u.id")
            .append ("   AND u.oid = ").append (this.oid.toStringQu ());
//debug ("User_01.getWorkspaceOid QUERY: " + queryStr);

        return this.getQueryOid (queryStr);
    } // getWorkspaceOid


    /**************************************************************************
     * Get oid with query which have exactly one resultrow with one column
     * named 'oid'. <BR/>
     * The query is executed and the result, i.e. an oid returned.
     *
     * @param   queryStr    The query to be executed.
     *
     * @return  the oid
     */
    private OID getQueryOid (StringBuffer queryStr)
    {
        SQLAction action = null;        // the action object used to access the
                                        // database
        OID oidValue = null;

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();
            int rowCount = action.execute (queryStr, false);

            if (rowCount > 0)           // domains found?
            {
                // get tuples out of db
                while (!action.getEOF ())
                {
                    oidValue = SQLHelpers.getQuOidValue (action, "oid");
                    // step one tuple ahead for the next loop
                    action.next ();
                } // while
            } // if
        } // try
        catch (DBError dbErr)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage ("User_01.getWorkspaceTemplateOid", dbErr,
                                   this.app, this.sess, this.env, true);
        } // catch
        finally
        {
            // close db connection in every case -  only workaround -
            // db connection must be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        return oidValue;
    } // getQueryOid


    /**************************************************************************
     * Get selection list of all available workspace templates. <BR/>
     *
     * @return  A selection list with the workspace templates.
     */
    private SelectionList getWorkspaceSelectionList ()
    {
        // get the workspace template selection list and return the result:
        return this.performRetrieveSelectionListData (
            this.getTypeCache ().getTVersionId (TypeConstants.TC_WorkspaceTemplate), false);
    } // getWorkspaceSelectionList


    /**************************************************************************
     * Get list of workspace template oids. <BR/>
     * If the parameter <CODE>templateNames</CODE> is <CODE>null</CODE> all
     * available workspace templates are returned.
     * If <CODE>templateNames</CODE> is an empty array the result is also empty.
     *
     * @param   templateNames   The names of the required templates.
     *
     * @return  An array with the workspace template oids.
     *
     * @throws  ObjectNotFoundException
     *          At least one of the names does not describe a valid workspace
     *          template.
     */
    private OID[] getWorkspaceTemplateOids (String[] templateNames)
        throws ObjectNotFoundException
    {
        SelectionList workSpSelList = this.getWorkspaceSelectionList ();
        Vector<String> ids = new Vector<String> ();     // found ids
        boolean found = false;          // was the actual name found?

        if (templateNames == null)
        {
            try
            {
                // convert the strings to oids and return the result:
                return OID.stringArrayToOid (workSpSelList.ids);
            } // try
            catch (IncorrectOidException e)
            {
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            } // catch
        } // if

        // loop through all names and try to find the corresponding templates:
        for (int i = 0; i < templateNames.length; i++)
        {
            // initialize loop variable:
            found = false;

            // search for the current templateName within the selection list:
            for (int j = 0; j < workSpSelList.values.length; j++)
            {
                if (workSpSelList.values[j].equals (templateNames[i]))
                {
                    ids.addElement (workSpSelList.ids[j]);
                    found = true;
                } // if
            } // for j

            // check if the actual name was found:
            if (!found)                 // the name was not found?
            {
                // throw the corresponding exception:
                ObjectNotFoundException error = new ObjectNotFoundException (
                    MultilingualTextProvider.getMessage (UtilExceptions.EXC_BUNDLE,
                    UtilExceptions.ML_E_OBJECTNOTFOUNDEXCEPTION, 
                    new String[] {templateNames[i]}, env));

                throw error;
            } // if the name was not found
        } // for i

        try
        {
            // create oid array and return it:
            return OID.stringArrayToOid (ids.toArray (new String[0]));
        } // try
        catch (IncorrectOidException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch

        return null;
    } // getWorkspaceTemplateOids


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * object info view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  an array with button ids that can be displayed
     */
    protected int[] setInfoButtons ()
    {
        // define buttons to be displayed:
        int [] buttons =
        {
            Buttons.BTN_EDIT,
            Buttons.BTN_DELETE,
            Buttons.BTN_COPY,
            Buttons.BTN_SEARCH,
        }; // buttons

        // return button array
        return buttons;
    } // showInfoButtons


    //
    // import / export methods
    //
    /**************************************************************************
     * Reads the object data from a data element. <BR/>
     *
     * @param dataElement     the dataElement to read the data from
     */
    public void readImportData (DataElement dataElement)
    {
        // get business object specific values
        super.readImportData (dataElement);
        // get the type specific values
        if (dataElement.exists ("fullname"))
        {
            this.p_fullname = dataElement.getImportStringValue ("fullname");
        } // if
        if (dataElement.exists ("password"))
        {
            this.p_password =
                EncryptionManager.decrypt (dataElement.getImportStringValue ("password"));
        } // if
        if (dataElement.exists ("templates"))
        {
            // get the template names:
            String[] templateNames =
                DIHelpers.getTokens (dataElement.getImportStringValue ("templates"),
                            DIConstants.OPTION_DELIMITER);
/*
for (int i = 0; i < templateNames.length; i++)
{
    System.out.println ("*****template " + i + ": |" + templateNames[i] + "|");
} // for
*/
            try
            {
                this.p_workspaceTemplateOids =
                    this.getWorkspaceTemplateOids (templateNames);
            } // try
            catch (ObjectNotFoundException e)
            {
                IOHelpers.showMessage (e, this.app, this.sess, this.env, false);
            } // catch
        } // if templates
    } // readImportData


    /**************************************************************************
     * Writes the object data to a data element. <BR/>
     *
     * @param dataElement     the dataElement to write the data to
     */
    public void writeExportData (DataElement dataElement)
    {
        // set the business object specific values
        super.writeExportData (dataElement);
        // set the type specific values
        dataElement.setExportValue ("fullname", this.p_fullname);
        dataElement.setExportValue ("password",
            EncryptionManager.encrypt (this.p_password));
    } // writeExportData


    /**************************************************************************
     * Return the oid of the object that represents the private tab. <BR/>
     *
     * @return  The oid of the private object.
     */
    public OID getPrivateOid ()
    {
        return this.p_workSpace;
    } // getPrivateOid


    /**************************************************************************
     * Get the weblinkCookie. <BR/>
     *
     * @param   domId   ???
     *
     * @return  ???
     */
    public WeblinkInfo getWeblinkCookie (int domId)
    {
        WeblinkInfo info = new WeblinkInfo ();
        String value = null;
        boolean found = false;

        value = this.env.getCookie ("weblink");
        if (value != null)
        {
            // separate the different domains
            StringTokenizer t = new StringTokenizer (value, "*");
            while (!found && (t.hasMoreElements ()))
            {
                // separate values
                value = (String) t.nextElement ();
                StringTokenizer t2 = new StringTokenizer (value, "|");
                // get the domainId out of the tokenizer
                if (t2.countTokens () == 3)
                {
                    info.userId = Integer.parseInt ((String) t2.nextElement ());
                    info.hash = (String) t2.nextElement ();
                    info.domain = Integer.parseInt ((String) t2.nextElement ());
                    if (info.domain == domId)
                    {
                        found = true;
                    } // if
                } // if
            } // while
        } // if

        if (!found)
        {
            return null;
        } // if

        return info;
    } // getWeblinkCookie


    /**************************************************************************
     * Set the weblinkCookie. <BR/>
     *
     * @return  ???
     */
    public boolean setWeblinkCookie ()
    {
        String newValue = "";
        String value = null;
        WeblinkInfo info = new WeblinkInfo ();
        boolean found = false;

        // compute the hash
        String hash = StringHelpers.computeHash (
            this.getUser ().username +
            this.getUser ().password +
            this.getUser ().domain);

        // get old Cookie information
        value = this.env.getCookie ("weblink");
        if (value != null)
        {
            // separate the different domains
            StringTokenizer t = new StringTokenizer (value, "*");
            while (!found && (t.hasMoreElements ()))
            {
                // separate values
                value = (String) t.nextElement ();
                StringTokenizer t2 = new StringTokenizer (value, "|");
                // get the domainId out of the tokenizer
                if (t2.countTokens () == 3)
                {
                    info.userId = Integer.parseInt ((String) t2.nextElement ());
                    info.hash = (String) t2.nextElement ();
                    info.domain = Integer.parseInt ((String) t2.nextElement ());
                    if (info.domain == this.getUser ().domain)
                    {
                        found = true;
                        newValue += this.getUser ().id + "|" + hash +
                            "|" + this.p_domain + "*";
                    } // if
                    else
                    {
                        newValue += value + "*";
                    } // else
                } // if
            } // while
        } // if

        if (!found)
        {
            // compute the string of the value of the Cookie
            newValue += this.getUser ().id + "|" + hash +
                "|" + this.p_domain;
        } // if

        Date temp = DateTimeHelpers.getCurAbsDate ();
        long yearMillis = 31556952000L; // milli seconds per year
        // set the temp date to 15 years in the future
        temp.setTime (temp.getTime () + 15 * yearMillis);

        return this.env.setCookie ("weblink", newValue, temp);
    } // setWeblinkCookie


    /**************************************************************************
     * Gets the info of a user out of the db. <BR/>
     *
     * @param   userId  ???
     * @param   domId   ???
     *
     * @return  ???
     */
    public User getInfo (int userId, int domId)
    {
        User user = null;
        int retVal = UtilConstants.QRY_OK; // return value of query

        // create the stored procedure call:
        StoredProcedure sp = new StoredProcedure(
                "p_User_01$getInfo",
                StoredProcedureConstants.RETURN_VALUE);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // input parameters
        // userId
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, userId);
        // DomainId
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, domId);

        // output parameters
        // userName
        Parameter userNameOutParam = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // password
        Parameter passwordOutParam = sp.addOutParameter (ParameterConstants.TYPE_STRING);

        try
        {
            // perform the function call:
            retVal = BOHelpers.performCallFunctionData (sp, this.env);
        } // try
        catch (NoAccessException e)
        {
            // should not occur, nothing to do
        } // catch

        if (retVal == UtilConstants.QRY_OK)
        {
            // get the parameters
            // initialize User with domainId, username, password
            user = new User (domId, userNameOutParam.getValueString (), passwordOutParam.getValueString ());
        } // if

        // return the user-object
        return user;
    } // getInfo


    /**************************************************************************
     * This method retrieves the homepagepath which should be used
     * in the current situation. It depends on the fact if SSL is required
     * and if SSL is installed. <BR/>
     *
     * @return  An URL representing the homepagepath to use.
     */
    private String getActualHomepagePath ()
    {
        boolean sslRequired     = false;
        String dummy            = "";
        String servletName = null;


        // get configuration variable for servlet name:
        servletName = this.getConfiguration ().getConfVars ().getValue (
            "ibsbase.appServlet");
        if (servletName == null)
        {
            // set default servlet name:
            servletName = "ApplicationServlet";
        } // if

        sslRequired = Ssl.isSslRequired (this.sess) &&
            ((ServerRecord) this.sess.actServerConfiguration).getSsl ();

        String homepagePath = this.env.getStringParam (BOArguments.ARG_PATH);

        if (homepagePath == null)       // homepagepath was no argument?
        {
            homepagePath = this.env.getServerVariable (IOConstants.SV_URL);
        } // if homepagepath was no argument

        if (sslRequired)                // SSL is necessary
        {
            dummy = Ssl.getSecureUrl (homepagePath, this.sess);
        } // if SSL is necessary
        else
        // SSL is not necessary
        {
            dummy = Ssl.getNonSecureUrl (homepagePath, this.sess);
        } // else SSL is not necessary

        // construct the resulting homepagepath
        if (dummy.indexOf (BOPathConstants.PATH_APP) > -1)
        {
            return dummy.substring (0,
                dummy.indexOf (BOPathConstants.PATH_APP) +
                    BOPathConstants.PATH_APP.length ());
        } // if
        else if (dummy.indexOf (servletName) > -1)
        {
            return dummy.substring (0,
                dummy.indexOf (servletName)) + BOPathConstants.PATH_APP;
        } // else if
        else                            // unknown url
        {
            return null;
        } // else unknown url
    } // getActualHomepagePath


    /**************************************************************************
     * Creates a temporary directory. <BR/>
     *
     * @return  the path of the temporaty directory or null in case the
     *          directory could not be created.
     */
    protected String createTempDir ()
    {
        String m2AbsBasePath = StringHelpers.replace (this.app.p_system.p_m2AbsBasePath,
            File.separator + File.separator, File.separator);
        String tempBasePath = m2AbsBasePath + DIConstants.PATH_TEMPROOT;
        String tempPath = DIConstants.PATH_TEMPPATH;
        // make a unique temporary directory
        String tempPathName = FileHelpers.getUniqueFileName (tempBasePath, tempPath);
        // create the temporary directory
        tempPath = tempBasePath + tempPathName + File.separator;

        if (FileHelpers.makeDir (tempPath, true))
        {
            return tempPath;
        } // if (FileHelpers.makeDir (this.path))

        // could not make the temporaty directory
        return null;
    } // createTempDir


    /**************************************************************************
     * deletes the temporary directory. <BR/>
     *
     * @param   path    The temporary directory to be deleted.
     */
    protected void deleteTempDir (String path)
    {
        if (path == null)
        {
            return;
        } // path == null
        // delete to content of the directory first
        String [] tempFiles = FileHelpers.getFilesArray (path);
        if (tempFiles != null)
        {
            for (int i = 0; i < tempFiles.length; i++)
            {
                FileHelpers.deleteFile (path + tempFiles[i]);
            } // for i
        } // if (tempFiles != null)
        // now delete the temp directory
        FileHelpers.deleteDir (path);
    } // deleteTempDir


    /**************************************************************************
     * replace all system variables and special workspace variables like
     * #SYSVAR.WORKSPACEUSERNAME# in given workspace structure. <BR/>
     * saves the new file in targetPath/fileName
     *
     * @param filePath      path of source templatefile without filename
     * @param targetPath    path of target templatefile without filename
     * @param fileName      name of templatefile
     *
     * @return true if the transformation succeeded otherwise false
     */
    private boolean transformTemplate (String filePath, String targetPath,
                                        String fileName)
    {
        this.debug ("User_01.transformProjFile (" + filePath + ")");
        String line = null;
        String newLine = null;
        File inputFile = new File (filePath + fileName);
        File outputFile = new File (targetPath + fileName);
        BufferedReader inputBufferedReader = null;
        FileWriter outputFileWriter = null;

        // read the input file
        try
        {
            inputBufferedReader = new BufferedReader (new FileReader (inputFile));
            outputFileWriter = new FileWriter (outputFile);
            // check if input file exists
            if (!inputFile.exists ())
            {
                IOHelpers.showMessage ("User_01.transformTemplate file " + filePath +
                                       fileName + " does not exists.",
                                       this.app, this.sess, this.env);
                return false;
            } // if

            try
            {
                // initialize system variables to be replaced in import structure to
                // import workspace for current created user. <BR/>
                Variables vars = new Variables ();

                vars.addSysVars (this);

                // variable for username of workspaceowner
                Variable var = new Variable (
                                "#SYSVAR.WORKSPACEUSERNAME#",
                                ActionConstants.VARIABLETYPE_TEXT,
                                "63",
                                "",
                                this.name);
                vars.changeEntry (var);

                // variable for useroid of workspaceowner
                var = new Variable (
                                "#SYSVAR.WORKSPACEUSEROID#",
                                ActionConstants.VARIABLETYPE_TEXT,
                                "63",
                                "",
                                "" + this.oid);
                vars.changeEntry (var);


                // replace system variables in each line of source file and
                // write it to tempfile
                while ((line = inputBufferedReader.readLine ()) != null)
                {
                    newLine = vars.replaceWithValue (line, this.env);
                    newLine += "\n";
                    outputFileWriter.write (newLine, 0, newLine.length ());
                }  // while
            } // try
            catch (ActionException e)
            {
                IOHelpers.showMessage ("User_01.transformProjTemplate", e,
                                       this.app, this.sess, this.env, false);
                inputBufferedReader.close ();
                outputFileWriter.close ();
                return false;
            } // catch

            inputBufferedReader.close ();
            outputFileWriter.close ();
            return true;
        } // try
        catch (IOException e)
        {
            IOHelpers.showMessage ("User_01.transformProjTemplate",
                                   e.getMessage (), this.app, this.sess, this.env);
            return false;
        } // catch
    } // transformProjTemplate


    /**************************************************************************
     * Get the orgunit a user is associated with via a staff object. <BR/>
     *
     * @param   domain      The domain where to search for the orgunit.
     * @param   username    The user name.
     *
     * @return  The orgunit if found or <CODE>""</CODE> otherwise.
     */
    public String getPassword (int domain, String username)
    {
        String password = null;
        int rowCount;
        SQLAction action = null;        // the action object used to access the
                                        // database

        // create the SQL String to select the project order
        String queryStr =
            " SELECT u.password " +
            " FROM ibs_object o, ibs_user u " +
            " WHERE o.oid = u.oid " +
            " AND o.state = " + States.ST_ACTIVE +
            " AND o.isLink = " + IOConstants.BOOLPARAM_FALSE +
            " AND u.domainId = " + domain +
            " AND u.name = '" + username + "'";

        try
        {
            action = this.getDBConnection ();
            rowCount = action.execute (queryStr, false);
            // check if we got any data
            if (rowCount > 0)
            {
                password = action.getString ("password");
            } // if (rowCount == 1)
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

        // return the result
        return password;
    } // getPassword

} // class User_01
