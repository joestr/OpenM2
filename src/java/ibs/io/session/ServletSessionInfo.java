/*
 * Class: ServletSessionInfo.java
 */

// package:
package ibs.io.session;

// imports:
import ibs.io.session.SessionInfo;
//TODO: unsauber
import ibs.obj.user.User_01;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;


/******************************************************************************
 * This is the Servlet-SessionInfo Object, which handles specifical
 * Servlet-related Events.
 *
 * @version     $Id: ServletSessionInfo.java,v 1.5 2007/07/20 13:07:56 kreimueller Exp $
 *
 * @author      Christine Keim (CK), 011104
 ******************************************************************************
 */
public class ServletSessionInfo extends SessionInfo implements HttpSessionBindingListener
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ServletSessionInfo.java,v 1.5 2007/07/20 13:07:56 kreimueller Exp $";


    /*************************************************************************
     * Notifies the object that it is being bound to a session and identifies
     * the session. <BR/>
     *
     * @param   event   The event that identifies the session.
     *
     * @see HttpSessionBindingListener
     */
    public void valueBound (HttpSessionBindingEvent event)
    {
        // do nothing
    } // valueBound


    /**************************************************************************
     * Notifies the object that it is being unbound from a session and
     * identifies the session. <BR/>
     *
     * @param   event   The event that identifies the session.
     *
     * @see HttpSessionBindingListener
     */
    public void valueUnbound (HttpSessionBindingEvent event)
    {
        // get a user object from the object pool
//        if ((this.userInfo != null) && (this.userInfo.user != null) && (this.userInfo.user.oid != null))
        if (this.loggedIn)
        {
            User_01 user = new User_01 ();
            user.initObject (this.userInfo.getUser ().oid, this.userInfo.getUser (), null, this, null);
            user.logout ();
        } // if
    } // valueBound

} // ServletSessionInfo
