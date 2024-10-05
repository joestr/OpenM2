/*
 * Class: Application.java
 */

// package:
package ibs.app;

// imports:
import ibs.BaseObject;
import ibs.app.AppConstants;
import ibs.app.AppFunctions;
import ibs.app.ApplicationInitializer;
import ibs.app.func.FunctionHandlerContainer;
import ibs.app.func.FunctionValues;
import ibs.bo.BOArguments;
import ibs.bo.BOPathConstants;
import ibs.bo.BusinessObject;
import ibs.bo.cache.ObjectPool;
import ibs.io.Environment;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.NoSslAvailableException;
import ibs.io.Ssl;
import ibs.io.SslRequiredException;
import ibs.io.servlet.ApplicationInitializationException;
import ibs.io.servlet.IApplication;
import ibs.io.servlet.SessionInitializationException;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.service.conf.ConfigurationException;
import ibs.service.conf.IConfiguration;
import ibs.service.conf.ServerRecord;
import ibs.service.user.User;
import ibs.tech.html.BodyElement;
import ibs.tech.html.BuildException;
import ibs.tech.html.Page;
import ibs.tech.html.TextElement;
import ibs.util.NoServersConfiguredException;
import ibs.util.ServerRequestNotAllowedException;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;
import ibs.util.UtilExceptions;


/******************************************************************************
 * Application object which is created with each call of a page. <BR/>
 * An object of this class represents the interface between the network and the
 * business logic itself. <BR/>
 * It gets arguments from the user, controls the program flow, and sends data
 * back to the user and his browser. <BR/>
 * There has to be generated an extension class of this class to realize the
 * functions which are specific to the required application.
 *
 * @version     $Id: Application.java,v 1.185 2010/04/07 13:37:16 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR), 980214
 ******************************************************************************
 */
public class Application extends BaseObject
    implements IApplication
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Application.java,v 1.185 2010/04/07 13:37:16 rburgermann Exp $";

    /**
     * The application object itself. <BR/>
     */
    private ApplicationInfo p_app = null;

    /**
     * The session object itself. <BR/>
     */
    private SessionInfo p_sess = null;

    /**
     * Environment for getting input and generating output. <BR/>
     */
    private Environment p_env = null;

    /**
     * Shall a new login be performed? <BR/>
     */
    private boolean p_performLogin = false;


    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Get the homepagepath out of the environment and the actual situation.
     * <BR/>
     * If SSL is required and available then a secure-mode URL is retourned,
     * otherwise a non-secure-mode URL. <BR/>
     *
     * @param   app     The application object to get the current context.
     *
     * @return  The actual homepagePath depending on the actual mode.
     *
     * @deprecated  ???
     */
    @Deprecated
    private static String getHomePagePath (Application app)
    {
        String dummy = "";              // a dummy string

        // check if SSL is required and available:
        boolean sslRequired =
            Ssl.isSslRequired (app.p_sess) &&
            ((ServerRecord) app.p_sess.actServerConfiguration).getSsl ();

        if (sslRequired)                // SSL is necessary?
        {
            dummy = Ssl.getSecureUrl (
                app.p_env.getServerVariable (IOConstants.SV_URL),
                app.p_sess);
        } // if SSL is necessary
        else                            // SSL is not necessary
        {
            dummy = Ssl.getNonSecureUrl (
                app.p_env.getServerVariable (IOConstants.SV_URL),
                app.p_sess);
        } // else SSL is not necessary

        if (dummy.indexOf (BOPathConstants.PATH_APP) < 0)
                                        // the app/ directory could not be found
        {
            return null;
        } // if the app/ directory could not be found

        // app/ was found
        return dummy.substring (0, dummy.indexOf (BOPathConstants.PATH_APP) + 4);
    } // getHomePagePath


    /*************************************************************************
     * sets the homepagepath in the current userInfo. <BR/>
     *
     * @param   app     The application object to get the current context.
     *
     * @deprecated  ???
     */
    @Deprecated
    private static void setHomepagePath (Application app)
    {
        String homepagePath;            // the homepage path value
        UserInfo userInfo = app.getUserInfo ();

        if (userInfo.homepagePath == null)
                                        // no homepagpath set yet?
        {
            // gets the homepagePath-value of the parameter ARG_PATH
            homepagePath = app.p_env.getStringParam (BOArguments.ARG_PATH);

            if (homepagePath == null)   // no parameter was set
            {
                // get the homepage path which is already stored:
                homepagePath = Application.getHomePagePath (app);
            } // else no parameter was set

            userInfo.homepagePath = homepagePath;
        } // if no homepagpath set yet
    } // setHomepagePath


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates an Application object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public Application ()
    {
        // call constructor of super class:
        super ();

        // open the application tracer:
        this.openTrace ();

//trace ("in Application");
        // initialize the instance's public/protected properties:
        this.p_env = null;
    } // Application


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Initializes and starts the Application. <BR/>
     *
     * @throws  ApplicationInitializationException
     *          An exception occurred during the application initialization.
     */
    public void initApplication ()
        throws ApplicationInitializationException
    {
        ApplicationContext appContext =
            new ApplicationContext (this.p_app, this.p_sess, this.p_env);

        // check if there exists already an application initializer:
        if (this.p_app.p_appInitializer == null)
                                // no application initializer set?
        {
            // create a new application initializer and store it in
            // the application info:
            this.p_app.p_appInitializer = new ApplicationInitializer (this.p_app);
        } // if no application initializer set

        this.p_app.p_appInitializer.initApplication (appContext);
    } // initApplication


    /**************************************************************************
     * Method which is called once for the session, performs operations to be
     * done at session startup. <BR/>
     *
     * @throws  SessionInitializationException
     *          An exception occurred during the session initialization.
     */
    public void initSession ()
        throws SessionInitializationException
    {
        // initialize the user login:
        this.initLogin ();
    } // initSession


    /**************************************************************************
     * Initialize the login data for a user. <BR/>
     *
     * @throws  SessionInitializationException
     *          An exception occurred during the session initialization.
     */
    public void initLogin ()
        throws SessionInitializationException
    {
        User user = null;               // the actual user
        // set the homepagePath:
        Application.setHomepagePath (this);

        // get user from session:
        user = this.getUser ();

        if (user == null)               // no user defined yet?
        {
            // create new user object:
            user = new User ("");       // create default user
            user.actUsername = AppConstants.DEFAULT_USERNAME;
            user.password = AppConstants.DEFAULT_PASSWORD;

            // store user in session variable:
            this.setUser (user);
        } // if no user defined yet
// DEBUG
//        this.env.write (this.app.apiConPool.toString ());
    } // initLogin


    /**************************************************************************
     * The method checks if the request was allowed or not. <BR/>
     * It throws a ServerRequestNotAllowed exception if the request was not
     * allowed because the server where the request was sent to was not
     * configured for m2. If no servers have been configured at all the
     * NoServersConfiguredException is thrown. <BR/>
     * If the actual domain requires ssl - that means it is configured in the
     * domain configuration - then SslRequiredException is thrown.
     *
     * @throws ServerRequestNotAllowedException
     *         If the server where the request was sent to was not configured
     *         then this exception is thrown.
     * @throws ibs.util.NoServersConfiguredException
     *         If no servers have been configured at all then this exception is
     *         thrown.
     * @throws ibs.io.SslRequiredException
     *         This exception is thrown if SSL must be used.
     * @throws ibs.io.NoSslAvailableException
     *         This exception is thrown if SSL is used but is not available.
     */
    private void verifyServerRequest () throws ServerRequestNotAllowedException,
                                               NoServersConfiguredException,
                                               SslRequiredException,
                                               NoSslAvailableException
    {
        // temporary variables needed, their description is given later
        ServerRecord actConf = null;
        String actServerPort = "";
        boolean isCalledFirstTime = false;
        boolean isHttpsRequest = false;
        boolean userProfileAvailable = false;
        boolean appServerGiven = false;
        boolean actAppServerPortEqualsActReqServerPort  = false;
        boolean sslServerGiven = false;
        boolean actSslServerPortEqualsActReqServerPort = false;


        // check for first call of application:
        isCalledFirstTime =
            this.p_app.connections == 0 ||
            this.getConfiguration () == null;

        // check that configuration is not null and a login should be
        // performed then the actual server configuration should be set
        if (this.getConfiguration () != null && this.p_performLogin)
        {
            // set the actual server configuration for the current session.
            // this record will be retrieved, where the servername matches
            // with the http header variable 'HOST'
            this.p_sess.actServerConfiguration = (this.getConfiguration ())
                .getConfigurationServers ().getServerRecord (
                    this.p_env.getHttpHeaderVariable (IOConstants.HV_HOST), this.p_env);
        } // if configuration is not null and a login should be performed

        // define a variable which contains the actual server configuration
        actConf = (ServerRecord) this.p_sess.actServerConfiguration;

        // get the actual server port ouf of the environment to know where the
        // request was sent to
        actServerPort = this.p_env.getServerVariable (IOConstants.SV_SERVER_PORT);

        // a flag indicating if the last request was sent in a secure mode
        // or not
        isHttpsRequest = Ssl.isHttpsRequest (this.p_env, this.p_app);

        // checks if a login has been done already. This can be checked with checking
        // if the properties are null or not
        userProfileAvailable = this.p_sess != null &&
                               this.getUserInfo () != null &&
                               this.getUserInfo ().userProfile != null;

        // this flag indicates if the applicationserver value is given.
        appServerGiven = !(actConf.getApplicationServer () == null ||
                           actConf.getApplicationServer ().length () == 0);

        // this flag indicates if the actual port where the request was sent to
        // is the same as the configured applicationserverport for the actual server
        actAppServerPortEqualsActReqServerPort =
            ("" + (actConf.getApplicationServerPort ())).equals (actServerPort);

        // this flag indicates if the sslserver value is given.
        sslServerGiven = !(actConf.getSslServer () == null ||
                           actConf.getSslServer ().length () == 0);

        // this flag indicates if the actual port where the request was sent to
        // is the same as the configured sslport for the actual server
        actSslServerPortEqualsActReqServerPort =
                    ("" + (actConf.getSslServerPort ())).equals (actServerPort);

        // if no https request but the applicationserver is not set
        // properly or the port is not the correct one then throw an exception
        if (!isHttpsRequest &&
            (!appServerGiven || !actAppServerPortEqualsActReqServerPort))
                                        // no applicationserver given in the
                                        // configuration  for a http request?
        {
            throw new ServerRequestNotAllowedException (MultilingualTextProvider
                    .getMessage (UtilExceptions.EXC_BUNDLE,
                        UtilExceptions.ML_E_SERVERREQUESTNOTALLOWED, this.p_env));
        } // if no applicationserver given in the...
        // if https request but the sslserver is not set
        // properly or the port is not the correct one then throw an exception
        else if (isHttpsRequest &&
                 (!sslServerGiven || !actSslServerPortEqualsActReqServerPort))
                                // no sslserver given in the configuration for a https request
        {
            throw new ServerRequestNotAllowedException (MultilingualTextProvider
                .getMessage (UtilExceptions.EXC_BUNDLE,
                    UtilExceptions.ML_E_SERVERREQUESTNOTALLOWED, this.p_env));
        } // else if the sslserver is not available
        // if application not yet initialized
        else if (isCalledFirstTime)
                                // application not initialized?
        {
            throw new ServerRequestNotAllowedException (MultilingualTextProvider
                .getMessage (UtilExceptions.EXC_BUNDLE,
                    UtilExceptions.ML_E_APPLICATIONNOTINITIALIZED, this.p_env));
        } // else if application not initialized
        // if the actual http request was sent during a valid session already. it
        // has to be checked if it is a valid request. Therefore is is checked
        // if the domain requires ssl but the request was sent without the
        // https protocol
        else if (!this.p_performLogin && userProfileAvailable &&
                 !isHttpsRequest && Ssl.isSslRequired (this.p_sess))
        {
            throw new SslRequiredException (MultilingualTextProvider
                .getMessage (UtilExceptions.EXC_BUNDLE,
                    UtilExceptions.ML_E_SSLMUSTBEUSEDDOMAIN, 
                    new String[] {this.getUserInfo ().userProfile.user.domainName},this.p_env));
        } // else if ssl is not used but required
/* BM HACK - SSL
 if it should be checked that SSL is only used when the domain requires SSL
 and in no other case then uncomment the 5 lines below. Otherwise it is possible
 to use SSL with a domain which does not require SSL
*/

        // if the actual https request was sent during a valid session already. it
        // has to be checked if it is a valid request. Therefore is is checked
        // if the domain does not require ssl but the request was sent with the
        // https protocol
/* BM HACK ...
        else if (!this.performLogin && userProfileAvailable &&
                 isHttpsRequest && !Ssl.isSslRequired (this.sess))
        {
           throw new NoSslAvailableException (UtilExceptions.E_SSLISNOTAVAILABLE);
        } // else if ssl is not used but required
*/
    } // verifyServerRequest


    /**************************************************************************
     * Initializes and starts the Application. <BR/>
     */
    public void performTask ()
    {
        boolean calledFirstTime = false; // the flag is stored temporarily
                                        // indicating if the initGet method is
                                        // called the first time.
        User user;                      // the actual user
        UserInfo userInfo = null;

        try
        {
            // verify the server request at each request:
            this.verifyServerRequest ();

            // check if a configuration error occurred:
            if (this.p_app.configErrors != null) // configuration error?
            {
                throw new ConfigurationException (this.p_app.configErrors
                    .toString ());
            } // if configuration error

            userInfo = this.getUserInfo ();

            // initialize login name:
            if (userInfo.loginLastUsername == null)
                                        // no username set yet?
            {
                // set default name:
                userInfo.loginLastUsername = AppConstants.DEFAULT_USERNAME;
            } // if no username set yet

            // get user from session:
            user = this.getUser ();

            // check if the user is set:
            if (user == null)           // no user set?
            {
                this.initLogin ();
            } // if no user set

            // set if login shall be performed:
            if (!this.p_performLogin &&
                (user.username == null || user.username.isEmpty ()))
            {
                // no username set yet?
                this.p_performLogin = true;  // a new login shall be performed
            } // set if login shall be performed


            // check if the SslCondition is satisfied - if an https-request
            // was given, ssl must be installed and available, otherwise
            // an exception is raised.
            Ssl.checkSslCondition  (this.p_sess,
                                    this.p_app,
                                    this.p_env);

            this.performFunction ();
        } // try
        catch (ServerRequestNotAllowedException e)
        {
            this.resetConnections (calledFirstTime);
            this.writeSecurityExceptionOutput (e.toString ());
        } // catch ServerRequestNotAllowedException
        catch (NoServersConfiguredException e)
        {
            this.resetConnections (calledFirstTime);
            // server configuration was not given at all
            // this is the reason why env.write is used and not
            // show message. The paths can be incorrect.
            this.p_env.write (e.toString ());
        } // catch NoServerConfiguredException
        catch (NoSslAvailableException e)
        {
            this.resetConnections (calledFirstTime);
            this.writeSecurityExceptionOutput (e.toString ());
        } // catch NoSslAvailableException
        catch (ConfigurationException e)
        {
            this.resetConnections (calledFirstTime);
            IOHelpers.showMessage (e, this.p_app, this.p_sess, this.p_env);
        } // catch ConfigurationException
        catch (SslRequiredException e)
        {
            this.resetConnections (calledFirstTime);
            this.writeSecurityExceptionOutput (e.toString ());
        } // catch ServerRequestNotAllowedException
        catch (SessionInitializationException e)
        {
            IOHelpers.showMessage (e, this.p_app, this.p_sess, this.p_env, true);
        } // catch
    } // performTask


    /**************************************************************************
     * . <BR/>
     */
    public void performFunction ()
    {
        int function;                   // actual function
        BusinessObject obj;             // the actual business object
        FunctionValues values;          // the values for the function handlers

        // get the function values:
        values = new FunctionValues (this.p_app, this.p_sess, this.p_env);
        function = values.p_function;

        // first check if the user is logged in already.
        // if not show a login form
        // exception: an agent tries to login and perform an import
        // this is done in one separate method.
        // all agent methods need to be added here
        if (this.p_performLogin &&
            function != AppFunctions.FCT_READCONFIGFILE &&
            function != AppFunctions.FCT_LOGIN &&
            function != AppFunctions.FCT_AGENTLOGINIMPORT &&
            function != AppFunctions.FCT_AGENTLOGINEXPORT &&
            function != AppFunctions.FCT_AGENTLOGINRESTOREEXTERNALIDS &&
            function != AppFunctions.FCT_WEBLINK &&
            function != AppFunctions.FCT_CHANGETOINSECURESESSION &&
            function != AppFunctions.FCT_OBSERVER)
                                        // a new login shall be performed and
                                        // is not already done?
        {
            function = AppFunctions.FCT_LOGINFORM;   // set login function
        } // if a new login shall be performed...

        // store the actual function:
        this.p_sess.p_actFct = function;
        values.p_function = function;

        // evaluate the function:
        // call the function handlers
        function = this.getFunctionCache ().evalFunction (function, values);

        // check if the function could be performed:
        if (function != AppFunctions.FCT_NOFUNCTION)
                                        // function not finished?
        {
            if (values.p_oid != null && !values.p_oid.isEmpty ()) // oid set?
            {
                // try to create the business object instance:
                if ((obj = values.getObject ()) != null)
                                        // got the object?
                {
                    // perform the function within the business object:
                    function = obj.evalFunction (function);
                } // if got the object
            } // if oid set
        } // if function not finished

        // check if the function was found:
        if (function != AppFunctions.FCT_NOFUNCTION)
                                        // function not existing?
        {
            // display the function:
            this.display (function);
        } // if function not existing
    } // performFunction


    /**************************************************************************
     * Display the actual function. <BR/>
     *
     * @param   function    The function to be displayed.
     */
    protected void display (int function)
    {
        Page page = new Page (false);
        String message = null;

        // set the document's base:
        IOHelpers.setBase (page, this.p_app, this.p_sess, this.p_env);

        message = "Function \"" + function + "\" was not found!";

        page.body.addElement (new TextElement (message));
        try
        {
            page.build (this.p_env);
            page = null;
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e.getMsg (),
                                   this.p_app, this.p_sess, this.p_env);
            page = null;
        } // catch

        IOHelpers.printError ("performFunction", this, message);
    } // display


    /**************************************************************************
     * This method should not be used with the new IO encapsulator anymore. It
     * is used for writing a javascript message on the top window of the client.
     * This is necessary especially for security purposes where the desired
     * url can't be loaded. <BR/>
     *
     * @param s The string message to write.
     */
    private void writeSecurityExceptionOutput (String s)
    {
        // create a new page with a body which executes a javascript on loading.
        Page p = new Page (false);
        p.body = new BodyElement ();
        // write the javascript
        p.body.onLoad = "top.document.write ('" + s + "');";

        try
        {
            p.build (this.p_env);
        } // try
        catch (BuildException e)
        {
            this.p_env.write (s);
        } // catch BuildException
    } // writeSecurityExceptionOutput


    /**************************************************************************
     * Resets the application to read the configuration again if the input
     * parameter is true. <BR/>
     *
     * @param   reset   The flag which indicates if connections property
     *                  should be set to 0 again.
     */
    private void resetConnections (boolean reset)
    {
        // set the connection to 0 if the reset flag is true
        if (reset)                      // reset should be done?
        {
            // re-initialize the number of connections:
            this.p_app.connections = 0;
        } // if
    } // if the reset flag was true set the connection to 0


    /**************************************************************************
     * Set the perform login value. <BR/>
     * <CODE>true</CODE> means that the application shall perform a new login
     * as soon as possible.
     *
     * @param   performLogin    Shall the login be performed.
     */
    public void setPerformLogin (boolean performLogin)
    {
        // set the perform login value:
        this.p_performLogin = performLogin;
    } // setPerformLogin


    /**************************************************************************
     * Sets the ApplicationObject of the Application. <BR/>
     *
     * @param   appInfo The global application info.
     */
    public void setApplicationObject (ApplicationInfo appInfo)
    {
        this.p_app = appInfo;
    } // setApplicationObject


    /**************************************************************************
     * Gets the application info object of the Application. <BR/>
     *
     * @return  The global application info.
     */
    public ApplicationInfo getApplicationObject ()
    {
        // get the value and return it:
        return this.p_app;
    } // getApplicationObject


    /**************************************************************************
     * Sets the Sessionobject of the Application . <BR/>
     *
     * @param   sessInfo    The actual session info.
     */
    public void setSessionObject (SessionInfo sessInfo)
    {
        this.p_sess = sessInfo;
        this.setTracerHolder (sessInfo);
    } // setSessionObject


    /**************************************************************************
     * Gets the session info object of the Application . <BR/>
     *
     * @return  The actual session info.
     */
    public SessionInfo getSessionObject ()
    {
        // get the value and return it:
        return this.p_sess;
    } // getSessionObject


    /**************************************************************************
     * Sets the environment of this object. <BR/>
     * It is stored in the <A HREF="#env">env</A> property of this object.
     * <BR/>
     *
     * @param   env         Value for the environment.
     */
    public void setEnv (Environment env)
    {
        // set the new environment:
        this.p_env = env;
    } // setEnv


    /**************************************************************************
     * Get the actual user info. <BR/>
     *
     * @return  The user info object.
     */
    private UserInfo getUserInfo ()
    {
        return (UserInfo) this.p_sess.userInfo;
    } // getUserInfo


    /**************************************************************************
     * Set the actual user. <BR/>
     *
     * @param   user    The user object to be set.
     */
    private void setUser (User user)
    {
        this.getUserInfo ().setUser (user);
    } // setUser


    /**************************************************************************
     * Get the actual user. <BR/>
     *
     * @return  The user object.
     */
    private User getUser ()
    {
        return this.getUserInfo ().getUser ();
    } // getUser


    /**************************************************************************
     * Get the function handler cache. <BR/>
     *
     * @return  The cache object.
     */
    private FunctionHandlerContainer getFunctionCache ()
    {
        return ((ObjectPool) this.p_app.cache).getFunctionHandlerContainer ();
    } // getFunctionCache


    /**************************************************************************
     * Get the configuration cache. <BR/>
     *
     * @return  The cache object.
     */
    private IConfiguration getConfiguration ()
    {
        return (IConfiguration) this.p_app.configuration;
    } // getConfiguration

} // class Application
