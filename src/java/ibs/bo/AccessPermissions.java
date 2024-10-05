/*
 * Class: AccessPermissions.java
 */

// package:
package ibs.bo;

// imports:
import ibs.BaseObject;
import ibs.service.user.User;


/******************************************************************************
 * Holds the data for the access of one user to a specific business object.
 * <BR/>
 *
 * @version     $Id: AccessPermissions.java,v 1.4 2007/07/31 19:13:52 kreimueller Exp $
 *
 * @author      Klaus, 14.10.2003
 ******************************************************************************
 */
public class AccessPermissions extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: AccessPermissions.java,v 1.4 2007/07/31 19:13:52 kreimueller Exp $";


    /**
     * The oid of the object for which the user has the permissions. <BR/>
     */
    public OID p_objOid = null;

    /**
     * Is the object a container? <BR/>
     */
    public boolean p_isContainer = false;

    /**
     * The user, who has the permissions. <BR/>
     */
    public User p_user = null;

    /**
     * The permissions of the user on the object itself. <BR/>
     */
    public int p_objectRights = 0;

    /**
     * The permissions of the user on the object's container. <BR/>
     */
    public int p_containerRights = 0;


    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a AccessPermissions object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     */
    public AccessPermissions ()
    {
        // call constructor of super class:
        super ();
    } // AccessPermissions


    /**************************************************************************
     * Creates a AccessPermissions object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid     The oid of the object on which the permissions are
     *                  defined.
     * @param   user    The user, who has the permissions.
     */
    public AccessPermissions (OID oid, User user)
    {
        // call constructor of super class:
        super ();

        // set specific properties:
        this.p_objOid = oid;
        this.p_user = user;
    } // AccessPermissions


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Check if the permissions are for a specific object/user combination. <BR/>
     *
     * @param   oid     The oid of the required object.
     * @param   user    The user.
     *
     * @return  <CODE>true</CODE> if the oid and the user are identical,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean isForObjectUser (OID oid, User user)
    {
        // check if the oid and the user are identical and return the result:
        return (this.p_objOid == oid) && (this.p_user == user);
    } // isForObjectUser


    /**************************************************************************
     * Set the permissions for the actual object and user. <BR/>
     *
     * @param   objectRights    The object permissions.
     * @param   containerRights The container permissions.
     * @param   isContainer     Is the object a container?
     */
    public void setPermissions (int objectRights, int containerRights,
                                boolean isContainer)
    {
        // set the property values:
        this.p_objectRights = objectRights;
        this.p_containerRights = containerRights;
        this.p_isContainer = isContainer;
    } // isForObjectUser


    /**************************************************************************
     * Check if the user has specific permissions on object. <BR/>
     *
     * @param   rights  The rights the user should have.
     *
     * @return  <CODE>true</CODE> if the user has the rights,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean checkObjectPermissions (int rights)
    {
        // check if the user has the permissions and return the result:
        return (this.p_objectRights & rights) == rights;
    } // checkObjectPermissions


    /**************************************************************************
     * Check if the user has specific permissions on the container. <BR/>
     *
     * @param   rights  The rights the user should have.
     *
     * @return  <CODE>true</CODE> if the user has the rights,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean checkContainerPermissions (int rights)
    {
        // check if the user has the permissions and return the result:
        return (this.p_containerRights & rights) == rights;
    } // checkContainerPermissions

} // class AccessPermissions
