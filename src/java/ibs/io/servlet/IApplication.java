/*
 * Class: IApplication.java
 */

// package:
package ibs.io.servlet;

//imports:
import ibs.io.Environment;
import ibs.io.servlet.ApplicationInitializationException;
import ibs.io.servlet.SessionInitializationException;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;


/******************************************************************************
 * Interface containing the minimum functionality for web applications. <BR/>
 *
 * @version     $Id: IApplication.java,v 1.6 2007/07/20 13:07:56 kreimueller Exp $
 *
 * @author      Klaus, 19.10.2003
 ******************************************************************************
 */
public interface IApplication
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: IApplication.java,v 1.6 2007/07/20 13:07:56 kreimueller Exp $";


    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////


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
        throws ApplicationInitializationException;


    /**************************************************************************
     * Method which is called once for the session, performs operations to be
     * done at session startup. <BR/>
     *
     * @throws  SessionInitializationException
     *          An exception occurred during the session initialization.
     */
    public void initSession ()
        throws SessionInitializationException;


    /**************************************************************************
     * Sets the application info object of the Application. <BR/>
     *
     * @param   appInfo The global application info.
     */
    public void setApplicationObject (ApplicationInfo appInfo);


    /**************************************************************************
     * Gets the application info object of the Application. <BR/>
     *
     * @return  The global application info.
     */
    public ApplicationInfo getApplicationObject ();


    /**************************************************************************
     * Sets the session info object of the Application . <BR/>
     *
     * @param   sessInfo    The actual session info.
     */
    public void setSessionObject (SessionInfo sessInfo);


    /**************************************************************************
     * Gets the session info object of the Application . <BR/>
     *
     * @return  The actual session info.
     */
    public SessionInfo getSessionObject ();


    /**************************************************************************
     * Sets the environment of this object. <BR/>
     *
     * @param   env     Value for the environment.
     */
    public void setEnv (Environment env);


    /**************************************************************************
     * Set the perform login value. <BR/>
     * <CODE>true</CODE> means that the application shall perform a new login
     * as soon as possible.
     *
     * @param   performLogin    Shall the login be performed.
     */
    public void setPerformLogin (boolean performLogin);


    /**************************************************************************
     * Initializes and starts the Application. <BR/>
     */
    public void performTask ();

} // class IApplication
