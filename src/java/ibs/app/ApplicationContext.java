/*
 * Class: ApplicationContext.java
 */

// package:
package ibs.app;

// imports:
import ibs.io.Environment;
import ibs.io.IOHelpers;
import ibs.io.servlet.IApplicationContext;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.service.user.User;
import ibs.tech.html.IE302;
import ibs.util.StringHelpers;


/******************************************************************************
 * This class contains the complete context which is necessary for application
 * execution. <BR/>
 *
 * @version     $Id: ApplicationContext.java,v 1.8 2007/07/31 19:13:51 kreimueller Exp $
 *
 * @author      Klaus, 23.12.2003
 ******************************************************************************
 */
public class ApplicationContext extends Object implements IApplicationContext
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ApplicationContext.java,v 1.8 2007/07/31 19:13:51 kreimueller Exp $";


    /**
     * The global application info. <BR/>
     */
    private ApplicationInfo p_app = null;

    /**
     * The current session object. <BR/>
     */
    private SessionInfo p_sess = null;

    /**
     * The current environment. <BR/>
     */
    private Environment p_env = null;

    /**
     * The messages. <BR/>
     */
    private StringBuffer p_messages = new StringBuffer ();



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a ApplicationContext object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   app     The global application info.
     * @param   sess    The current session info.
     * @param   env     The current environment.
     */
    public ApplicationContext (
        ApplicationInfo app, SessionInfo sess, Environment env)
    {
        super ();

        // set properties:
        this.p_app = app;
        this.p_sess = sess;
        this.p_env = env;
    } // ApplicationContext


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Get the application info object. <BR/>
     *
     * @return  The global application info object.
     */
    public ApplicationInfo getApp ()
    {
        return this.p_app;
    } // getApp


    /**************************************************************************
     * Get the session object. <BR/>
     *
     * @return  The current session object.
     */
    public SessionInfo getSess ()
    {
        return this.p_sess;
    } // getSess


    /**************************************************************************
     * Get the environment. <BR/>
     *
     * @return  The current environment.
     */
    public Environment getEnv ()
    {
        return this.p_env;
    } // getEnv


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
     * Get the actual user. <BR/>
     *
     * @return  The user object.
     */
    public User getUser ()
    {
        return this.getUserInfo ().getUser ();
    } // getUser


    /**************************************************************************
     * Get the configuration path. <BR/>
     *
     * @return  The configuration path.
     */
    public String getConfigPath ()
    {
        return this.p_app.p_system.p_configPath;
    } // getConfigPath


    /**************************************************************************
     * Display a message. <BR/>
     * All messages are concatenated together and can be retrieved via
     * {@link #getMessages () getMessages}.
     *
     * @param   message The message to be displayed.
     */
    public void write (String message)
    {
        IOHelpers.printMessage (message);
        this.p_env.write (
            StringHelpers.replace (
                StringHelpers.replace (message, " ", IE302.HCH_NBSP), "\n", IE302.TAG_NEWLINE) +
                IE302.TAG_NEWLINE + "\n");
        this.p_messages.append (message).append ("\n");
    } // write


    /**************************************************************************
     * Get all messages. <BR/>
     *
     * @return  A String containing all displayed messages.
     */
    public String getMessages ()
    {
        return this.p_messages.toString ();
    } // getMessages

} // class ApplicationContext
