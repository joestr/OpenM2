/*
 * Class: ExtendedUserData.java
 */

// package:
package ibs.service.user;

// imports:
import ibs.BaseObject;
import ibs.bo.OID;


/******************************************************************************
 * This class contains extended data regarding a user. <BR/>
 * There is an additional username, user full name, maybe an additional user
 * password, etc.
 *
 * @version     $Id: ExtendedUserData.java,v 1.3 2009/07/24 21:21:57 kreimueller Exp $
 *
 * @author      Bernhard Tatzmann (BT), 081001
 ******************************************************************************
 */
public class ExtendedUserData extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ExtendedUserData.java,v 1.3 2009/07/24 21:21:57 kreimueller Exp $";


    /**
     * Id of the extended user data. <BR/>
     */
    public int p_id = 0;

    /**
     * Oid of the extended user data. <BR/>
     */
    public OID p_oid = null;

    /**
     * The login name for the extended user data. <BR/>
     */
    public String p_username = null;

    /**
     * The login name for the standard login mechanism. <BR/>
     */
    public String p_login = null;

    /**
     * The full name of the extended user data. <BR/>
     */
    public String p_fullname = null;

    /**
     * The password of the extended user data. <BR/>
     */
    public String p_password = null;



    /**************************************************************************
     * Create a new instance representing the extended user data. <BR/>
     */
    public ExtendedUserData ()
    {
        // nothing to do
    } // ExtendedUserData


    /**************************************************************************
     * Create a new instance representing the extended user data. <BR/>
     *
     * @param   id          Id of the required user.
     */
    public ExtendedUserData (int id)
    {
        // set the instance's properties:
        this.p_id = id;

        // get the username:

        // get the groups where the user belongs to:
    } // ExtendedUserData


    /**************************************************************************
     * Create a new instance representing the extended user data. <BR/>
     * Uses the username to get the user data.
     *
     * @param   username    Name which the person used for logging on.
     */
    public ExtendedUserData (String username)
    {
        // set the instance's properties:
        this.p_username = username;

        // get the id of the user:

        // get the groups where the user belongs to:
    } // ExtendedUserData


    /**************************************************************************
     * Create a new instance representing the extended user data. <BR/>
     * Uses the username to get the user data.
     *
     * @param   username    Name which the person used for logging on.
     * @param   password    Password which the person used for logging on.
     */
    public ExtendedUserData (String username, String password)
    {
        // set the instance's properties:
        this.p_username = username;
        this.p_password = password;

        // get the id of the user:

        // get the groups where the user belongs to:
    } // ExtendedUserData


    /**************************************************************************
     * Get a String representation of the ExtendedUserData object. <BR/>
     *
     * @return  String representation of the extended user data.
     */
    public String toString ()
    {
        if (this.p_fullname != null)      // fullname is set?
        {
            return this.p_fullname;       // return the full name
        } // if
        else if (this.p_username != null) // username is set?
        {
            return this.p_username;       // return the username
        } // else if
        else                            // username unknown
        {
            return "";                  // return empty string
        } // else
    } // toString

} // class ExtendedUserData
