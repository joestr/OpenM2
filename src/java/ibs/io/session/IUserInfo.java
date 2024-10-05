/*
 * Class: IUserInfo.java
 */

// package:
package ibs.io.session;

// imports:
import ibs.service.user.ExtendedUserData;
import ibs.service.user.User;
import ibs.util.trace.Tracer;


/******************************************************************************
 * Contains the information for the actual user within the session. <BR/>
 *
 * @version     $Id: IUserInfo.java,v 1.5 2009/07/24 23:29:56 kreimueller Exp $
 *
 * @author      Klaus, 26.12.2003
 ******************************************************************************
 */
public interface IUserInfo
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: IUserInfo.java,v 1.5 2009/07/24 23:29:56 kreimueller Exp $";



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
     * Set the tracer for the user. <BR/>
     *
     * @param   tracer  The tracer.
     */
    public void setTracer (Tracer tracer);


    /**************************************************************************
     * Get the tracer for the user. <BR/>
     *
     * @return  The tracer.
     */
    public Tracer getTracer ();


    /**************************************************************************
     * Set the actual user. <BR/>
     *
     * @param   user    The user object.
     */
    public void setUser (User user);


    /**************************************************************************
     * Get the actual user. <BR/>
     *
     * @return  The user object.
     */
    public User getUser ();


    /**************************************************************************
     * Get the extended user data for the actual user. <BR/>
     *
     * @return  The extended user data object.
     */
    public ExtendedUserData getExtendedUserData ();


    /**************************************************************************
     * Set the extended user data for the actual user. <BR/>
     *
     * @param   extendedUserData    The extended user data object.
     */
    public void setExtendedUserData (ExtendedUserData extendedUserData);

} // interface IUserInfo
