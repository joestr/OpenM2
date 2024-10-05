/*
 * Class: IApplicationContext.java
 */

// package:
package ibs.io.servlet;

// imports:
import ibs.io.Environment;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
//KR TODO: unsauber
import ibs.service.user.User;


/******************************************************************************
 * Context for an application. <BR/>
 *
 * @version     $Id: IApplicationContext.java,v 1.5 2007/07/20 13:07:56 kreimueller Exp $
 *
 * @author      Klaus, 23.12.2003
 ******************************************************************************
 */
public interface IApplicationContext
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: IApplicationContext.java,v 1.5 2007/07/20 13:07:56 kreimueller Exp $";



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
     * Get the application info object. <BR/>
     *
     * @return  The global application info object.
     */
    public ApplicationInfo getApp ();


    /**************************************************************************
     * Get the session object. <BR/>
     *
     * @return  The current session object.
     */
    public SessionInfo getSess ();


    /**************************************************************************
     * Get the environment. <BR/>
     *
     * @return  The current environment.
     */
    public Environment getEnv ();


    /**************************************************************************
     * Get the actual user. <BR/>
     *
     * @return  The user object.
     */
    public User getUser ();


    /**************************************************************************
     * Get the configuration path. <BR/>
     *
     * @return  The configuration path.
     */
    public String getConfigPath ();


    /**************************************************************************
     * Display a message. <BR/>
     * All messages are concatenated together and can be retrieved via
     * {@link #getMessages () getMessages}.
     *
     * @param   message The message to be displayed.
     */
    public void write (String message);


    /**************************************************************************
     * Get all messages. <BR/>
     *
     * @return  The concatenated messages.
     */
    public String getMessages ();

} // interface IApplicationContext
