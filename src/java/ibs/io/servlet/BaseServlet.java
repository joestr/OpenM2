/*
 * Class BaseServlet
 */

// package:
package ibs.io.servlet;

// imports:
import ibs.di.DIConstants;
import ibs.io.Environment;
import ibs.io.IOHelpers;
import ibs.io.ServletEnvironment;
import ibs.io.UploadException;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.ServletSessionInfo;
import ibs.io.session.SessionInfo;
import ibs.tech.html.IE302;
import ibs.util.Helpers;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/******************************************************************************
 * This is the base servlet which is used as super class for all servlets. <BR/>
 *
 * @version     $Id: BaseServlet.java,v 1.43 2012/04/26 16:26:14 gweiss Exp $
 *
 * @author      Christine Keim (CK), 990303
 ******************************************************************************
 */
public class BaseServlet extends HttpServlet implements java.io.Serializable //implements SingleThreadModel
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: BaseServlet.java,v 1.43 2012/04/26 16:26:14 gweiss Exp $";


    /**
     * Serializable version number. <BR/>
     * This value is used by the serialization runtime during deserialization
     * to verify that the sender and receiver of a serialized object have
     * loaded classes for that object that are compatible with respect to
     * serialization. <BR/>
     * If the receiver has loaded a class for the object that has a different
     * serialVersionUID than that of the corresponding sender's class, then
     * deserialization will result in an {@link java.io.InvalidClassException}.
     * <BR/>
     * This field's value has to be changed every time any serialized property
     * definition is changed. Use the tool serialver for that purpose.
     */
    static final long serialVersionUID = -5955519508573858074L;


    /**
     * The object where values of the application are stored. <BR/>
     */
    protected ApplicationInfo p_app = null;

    /**
     * Application startup. <BR/>
     * This flag is set to <CODE>true</CODE> if the current call is the first
     * call to the application after server start.
     * Default: <CODE>false</CODE>
     */
    private boolean p_isAppStartup = false;

    /**
     * Session startup. <BR/>
     * This flag is set to <CODE>true</CODE> if the current call is the first
     * call to the application within the current user session.
     * Default: <CODE>false</CODE>
     */
    private boolean p_isSessStartup = false;

    /**
     * Standard state. <BR/>
     * Out of this state the other states can be reached.
     */
    private static final int ST_STANDARD = 1;

    /**
     * Application initialization phase. <BR/>
     */
    private static final int ST_APPINIT = 2;

    /**
     * Session initialization phase. <BR/>
     */
    private static final int ST_SESSINIT = 3;

    /**
     * Name of session variable. <BR/>
     */
    private static final String SESSVAR_NAME = "Session";

    /**
     * The current state of the servlet.
     * Default: <CODE>ST_STANDARD</CODE>
     */
    private int p_state = BaseServlet.ST_STANDARD;

    /**
     * Is the restart of the application possible?. <BR/>
     */
    private boolean p_restartPossible = true;

    /**
     * The base path of the application. <BR/>
     */
    private String p_basePath = null;


    /**************************************************************************
     * This method initializes the servlet. <BR/>
     *
     * @param   servletconfig   The servlet configuration.
     *
     * @throws  ServletException
     *          An exception occurred during initializing the servlet.
     *
     * @see javax.servlet.http.HttpServlet
     */
    public void init (ServletConfig servletconfig) throws ServletException
    {
        super.init (servletconfig);

        this.p_basePath = servletconfig.getServletContext ().getRealPath ("");

        try
        {
            // initialize the global application info:
            this.initApplicationInfo ();
        } // try
        catch (ApplicationInitializationException e)
        {
//            throw new ServletException ("Could not initialize servlet.", e);
        } // catch
    } // init


    /**************************************************************************
     * This method initializes the global application info. <BR/>
     *
     * @throws  ApplicationInitializationException
     *          An exception occurred during initialization.
     */
    public void initApplicationInfo ()
        throws ApplicationInitializationException
    {
        // create the application info:
        this.p_app = this.createApplicationInfo ();
        
        synchronized (VERSIONINFO) {
        	this.p_isAppStartup = true;
        }
    } // initApplicationInfo


    /**************************************************************************
     * Get the name of the application class to be called for performing the
     * required operation. <BR/>
     * The class name must include the package information in the form
     * <CODE>pkg1.pkg2.className</CODE>.
     *
     * @return  The class name for the application object.
     */
    public String getAppClassName ()
    {
        // return the class name:
        return "ibs.io.servlet.IApplication";
    } // getAppClassName


    /**************************************************************************
     * This method is called when the Servlet is requested with the
     * get - Method.
     *
     * @param req   the Request-Object
     * @param res   the Response-Object
     *
     * @throws  ServletException
     *          Any servlet dependent exception.
     * @throws  IOException
     *          An exception during I/O operation.
     */
    public void doGet (HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException
    {
        // Set the request encoding
        // see also http://java.sun.com/j2ee/1.4/docs/tutorial/doc/WebI18N5.html
        req.setCharacterEncoding (DIConstants.CHARACTER_ENCODING);
        
        this.performTask (req, res);
    } // doGet


    /**************************************************************************
     * This method is called when the Servlet is requested with the
     * post - Method.
     *
     * @param req   the Request-Object
     * @param res   the Response-Object
     *
     * @throws  ServletException
     *          Any servlet dependent exception.
     * @throws  IOException
     *          An exception during I/O operation.
     */
    public void doPost (HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException
    {
        this.performTask (req, res);
    } // doPost


    /**************************************************************************
     * Set the session object within the actual application. <BR/>
     * The session object is gotten out of the environment. If no SessionObject
     * can be retrieved (i.e. the session is just opened) then there will be a
     * new one created. <BR/>
     *
     * @param   app     The application within to set the session object.
     * @param   env     The environment which contains the session object.
     *
     * @throws  SessionInitializationException
     *          There occurred an error during initializating the session.
     */
    protected void setSessionObject (IApplication app, Environment env)
        throws SessionInitializationException
    {
        SessionInfo session = null;     // the actual session to be used

        try
        {
            // get and set the session object:
            session = (SessionInfo) env.getSessionObject (BaseServlet.SESSVAR_NAME);
            app.setSessionObject (session);
            if (session.isNew)
            {
                session.isNew = false;
            } // if
        } // try
        catch (Exception e)             // currently no session object available
        {
            try
            {
                // get and set a new session object:
                session = this.createSessionInfo ();
                this.p_isSessStartup = true;
                env.setSessionObject (BaseServlet.SESSVAR_NAME, session);
                app.setSessionObject (session);

                // ensure that a login is performed first:
                app.setPerformLogin (true);
            } // try
            catch (Exception e2)
            {
                env.write ("Exception when setting session object: " + e2 +
                    IE302.TAG_NEWLINE + " STACKTRACE: " +
                    Helpers.getStackTraceFromThrowable (e2));
                return;
            } // catch
        } // catch
    } // setSessionObject


    /**************************************************************************
     * Set the application object within the actual application. <BR/>
     * The application object is a variable which is initiated in the
     * init-Method of the servlet and is therefore available to all calls to
     * the servlet thereafter.
     *
     * @param   app     The application within to set the application object.
     */
    protected void setApplicationObject (IApplication app)
    {
        app.setApplicationObject (this.p_app);
    } // setApplicationObject


    /**************************************************************************
     * Set a new state. <BR/>
     * A state different from <CODE>ST_STANDARD</CODE> can only be set from
     * <CODE>ST_STANDARD</CODE>. <BR/>
     * <CODE>ST_STANDARD</CODE> can only be set if the state is different
     * from <CODE>ST_STANDARD</CODE>.
     *
     * @param   state   The state to be set.
     *
     * @return  <CODE>true</CODE> if the state was set. <BR/>
     *          <CODE>false</CODE> if the state could not be set, because one
     *          of the above conditions was not satisfied.
     */
    private synchronized boolean setState (int state)
    {
        boolean retVal = false;         // the result

        // check if the state can be set:
        if (this.p_state != state &&
            ((this.p_state == BaseServlet.ST_STANDARD) ||
             (state == BaseServlet.ST_STANDARD)))
        {
            // set the new state:
            this.p_state = state;
            retVal = true;
        } // if

        // return the result:
        return retVal;
    } // setState


    /**************************************************************************
     * This method instantiates a Class, which name is returned from method
     * {@link #getAppClassName getAppClassName}.
     * The Class which is instantiated has to implement the interface
     * {@link ibs.io.servlet.IApplication IApplication}.
     *
     * @param   req     The object representing the client request.
     * @param   res     The object which is responsible for handling the
     *                  response to the client.
     *
     * @throws  ServletException
     *          Any servlet dependent exception.
     * @throws  IOException
     *          An exception during I/O operation.
     */
    protected void performTask (HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException
    {
        Environment env = null;         // the actuel environment
        UploadException backup = null;  // exception during upload
        IApplication app = null;        // the current application object
        boolean abandonApp = false;     // shall the application be abandoned
                                        // if there occurred an error?

        try
        {
            // create the new environment:
            env = this.createEnvironment (req, res);
        } // try
        catch (UploadException e)
        {
            // backup the exception for later output:
            backup = e;
        } // catch

/*
        // check if the NTLM authentication is still running:
        // in that case we skip further actions in order to complete
        // the authentication
        if (env.isNTLMAuthenticationInProgress)
        {
            return;
        } // if
*/

//!!! The following code must be changed if there shall be anything returned
//!!! which is not pure HTML!!!
        // set header field first:
        env.setContentType ("text/html");

        try
        {
            // ensure that the application info is initialized:
            if (this.p_app == null)
            {
                // initialize the global application info:
                this.initApplicationInfo ();
            } // if

            this.p_app.connections++;   // increase the number of connections

            // create the application:
            app = this.createApplicationObject (env);

            // check consistency of session info object:
            SessionInfo sess = app.getSessionObject ();
            sess.setApplicationContext (this.p_app);

            // store the session context within the environment:
            env.setSessionContext (sess);

            // check if the application shall be initialized:
            synchronized (VERSIONINFO) {
            	if (this.p_isAppStartup)    // application initialization necessary?
	            {
	                // perform application startup:
	                abandonApp = this.appStartup (app, env);
	            } // if application initialization necessary
            }

            // check if the session shall be initialized:
            if (this.p_isSessStartup)   // session initialization necessary?
            {
                // perform session startup:
                this.sessionStartup (app, env);
            } // if session initialization necessary

            // check if there occurred an exception before:
            if (backup == null)
            {
                // start with the application execution:
                app.performTask ();
            } // if
            else
            {
                // display the corresponding message:
                IOHelpers.showMessage (
                    "Error during initializing environment", backup, env, true);
            } // else
        } // try
        catch (ApplicationInitializationException e)
        {
            // check if it is possible to restart the application in next call:
            if (this.p_app.p_restartPossible)
            {
                // display the corresponding message:
                IOHelpers
                    .showMessage (
                        "Error during initializing application. The application was abandoned",
                        e, env, true);

                System.out.println ();
                IOHelpers.printError ("Error during initializing application." +
                    " The application was abandoned.", this, e, true);
            } // if
            else
            {
                // set corresponding value:
                this.p_restartPossible = false;

                // display the corresponding message:
                IOHelpers.showMessage (e.getMessage (), false, env);

                System.out.println ();
                IOHelpers.printMessage (e.getMessage ());
            } // else

            // abandon the session:
            env.abandon ();

            // check if the application shall be abandoned, too:
            if (abandonApp || e.p_isAbandonApp)
            {
                // abandon the application:
                this.p_app = null;
            } // if
        } // catch
        catch (SessionInitializationException e)
        {
            // display the corresponding message:
            IOHelpers
                .showMessage (
                    "Error during initializing session. The session was abandoned.",
                    e, env, true);
            // abandon the session:
            env.setSessionObject (BaseServlet.SESSVAR_NAME, null);
            env.abandon ();
        } // catch
        finally
        {
            // ensure correct state:
            this.setState (BaseServlet.ST_STANDARD);
        } // finally
    } // performTask


    /**************************************************************************
     * Perform application startup. <BR/>
     *
     * @param   app     The current application object.
     * @param   env     The actual environment.
     *
     * @return  <CODE>true</CODE> if the application shall be abandoned after
     *          an error, <CODE>false</CODE> otherwise.
     *
     * @throws  ApplicationInitializationException
     *          An exception occurred while initializing the application.
     */
    protected boolean appStartup (IApplication app, Environment env)
        throws ApplicationInitializationException
    {
        boolean abandonApp = false;     // shall the application be abandoned

        // check if the restart is possible:
        if (!this.p_restartPossible)
        {
            // display message:
            env.write (
                "<HTML>\n<BODY>\n" +
                "<B>Startup of application not possible.</B>" +
                IE302.TAG_NEWLINE + "\n" +
                "Please restart the application server first.\n" +
                "</BODY>\n</HTML>");
            // finish the method:
            return abandonApp;
        } // if

        try
        {
            // try to set the state:
            if (this.setState (BaseServlet.ST_APPINIT))
            {
                abandonApp = true;
                // initialize the application:
                app.initApplication ();
                this.p_isAppStartup = false;
                this.setState (BaseServlet.ST_STANDARD);
                IOHelpers.printMessage ("Application Startup finished.\n");
                env.write (
                    "<INPUT TYPE=\"BUTTON\" VALUE=\"    OK    \" " +
                    "onClick=\"self.location.reload ();\" />");

                // abandon the session:
                env.abandon ();

                return abandonApp;
            } // if
        } // try
        catch (ApplicationInitializationException e)
        {
            // ensure that the application will be abandoned:
            e.p_isAbandonApp = true;
            throw e;
        } // catch ApplicationInitializationException

        // state could not be set
        this.printInitializationErrorMessage (env);
        throw new ApplicationInitializationException ("" + this, false);
    } // appStartup


    /**************************************************************************
     * Perform session startup. <BR/>
     *
     * @param   app     The application within which to perform session startup.
     * @param   env     The current user environment.
     *
     * @throws  SessionInitializationException
     *          Something occurred during initialization of session.
     */
    protected void sessionStartup (IApplication app, Environment env)
        throws SessionInitializationException
    {
        // try to set the state:
        if (this.setState (BaseServlet.ST_SESSINIT))
        {
            // initialize the session:
            app.initSession ();
            this.p_isSessStartup = false;
            this.setState (BaseServlet.ST_STANDARD);
            IOHelpers.printMessage ("Session Startup finished.\n");
        } // if
        else                    // state could not be set
        {
            this.printInitializationErrorMessage (env);
            throw new SessionInitializationException ("");
        } // else state could not be set
    } // sessionStartup


    /**************************************************************************
     * Create an Environment object. <BR/>
     *
     * @param   req     The object representing the client request.
     * @param   res     The object which is responsible for handling the
     *                  response to the client.
     *
     * @return  The environment which was created.
     *
     * @exception   UploadException
     *              An exception during initializing the environment object.
     * @exception   IOException
     *              An exception during I/O operation.
     */
    protected Environment createEnvironment (HttpServletRequest req, HttpServletResponse res)
        throws UploadException, IOException
    {
        ServletEnvironment env = null;  // the actual environment

        // create the new environment:
        env = new ServletEnvironment ();

        // set the environment values:
        env.set (req, res);

        // return the result:
        return env;
    } // createEnvironment


    /**************************************************************************
     * This method instantiates a Class whose name has been set in
     * this.applicationClassName. The Class which is instantiated has
     * to implement the interface
     * {@link ibs.io.servlet.IApplication IApplication}.
     *
     * @param   env     The environment.
     *
     * @return  The created application object.
     *          <CODE>null</CODE> if there occurred an exception.
     *
     * @throws  SessionInitializationException
     *          There occurred an error during initializating the session.
     */
    protected IApplication createApplicationObject (Environment env)
        throws SessionInitializationException
    {
        IApplication app = null;        // the current application object
        String applicationClassName = null; // the name of the application class

        // get the application class name:
        applicationClassName = this.getAppClassName ();

        // initialize the application:
        try
        {
            app = (IApplication) Class.forName (applicationClassName).newInstance ();
            app.setEnv (env);           // set the actual environment
            this.setApplicationObject (app);
            this.setSessionObject (app, env);
        } // try
        catch (ClassNotFoundException e)
        {
            env.write ("ClassNotFoundException occurred : " + e.toString ());
        } // catch ClassNotFoundException
        catch (InstantiationException e)
        {
            env.write ("InstantiationException occurred : " + e.toString ());
        } // catch InstantiationException
        catch (IllegalAccessException e)
        {
            env.write ("IllegalAccessException occurred : " + e.toString ());
        } // catch IllegalAccessException

        // return the result:
        return app;
    } // createApplicationObject


    /**************************************************************************
     * This method creates and initializes a new application info object. <BR/>
     * This object is intended to hold the data which is valid throughout the
     * whole time the application is up and running.
     *
     * @return  The created session info object.
     *
     * @throws  ApplicationInitializationException
     *          There occurred an error during initializating the session.
     */
    protected ApplicationInfo createApplicationInfo ()
        throws ApplicationInitializationException
    {
        ApplicationInfo app = null;     // the actual application info

        // create the application info object:
        app = new ApplicationInfo (this.p_basePath);

        // return the result:
        return app;
    } // createApplicationInfo


    /**************************************************************************
     * This method creates and initializes a new session info object. <BR/>
     * This object is intended to hold the data which is valid within one user
     * session.
     *
     * @return  The created session info object.
     *
     * @throws  SessionInitializationException
     *          There occurred an error during initializating the session.
     */
    protected SessionInfo createSessionInfo ()
        throws SessionInitializationException
    {
        SessionInfo session = null;     // the actual session to be used

        // get and set a new session object:
        session = new ServletSessionInfo ();

        // return the result:
        return session;
    } // createSessionInfo


    /**************************************************************************
     * Returns information about the servlet, such as author, version, and
     * copyright.
     *
     * @return String information about this servlet.
     */
    public String getServletInfo ()
    {
        return "The Base Application Servlet";
    } // getServletInfo


    /**************************************************************************
     * Display a message according to the current state. <BR/>
     *
     * @param   env     The actual environment.
     */
    private void printInitializationErrorMessage (Environment env)
    {
        switch (this.p_state)
        {
            case BaseServlet.ST_APPINIT:
                env.write ("The application is currently being initialized. Please try again in a few seconds.");
                break;

            case BaseServlet.ST_SESSINIT:
                env.write ("The session is currently being initialized. Please try again in a few seconds.");
                break;

            default:
                env.write ("Error in initialization. State: " + this.p_state + ".");
                break;
        } // switch
    } // printInitializationErrorMessage

} // class BaseServlet
