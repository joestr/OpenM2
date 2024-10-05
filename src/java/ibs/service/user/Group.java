/*
 * Class: Group.java
 */

// package:
package ibs.service.user;

// imports:
import ibs.BaseObject;
//KR TODO: unsauber
import ibs.bo.OID;


/******************************************************************************
 * This class contains all information regarding a Group. <BR/>
 *
 * @version     $Id: Group.java,v 1.3 2012/11/02 14:00:52 gweiss Exp $
 *
 * @author      Klaus Reimüller (KR), 20060105
 ******************************************************************************
 */
public class Group extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Group.java,v 1.3 2012/11/02 14:00:52 gweiss Exp $";


    /**
     * Id of the group. <BR/>
     */
    public int p_id = 0;

    /**
     * Oid of the group. <BR/>
     */
    public OID p_oid = null;

    /**
     * The name of the group. <BR/>
     */
    public String p_name = null;

    /**
     * The domain where the group resides. <BR/>
     */
    public int p_domain = 0;

    /**
     * The name of the domain where the group resides. <BR/>
     */
    public String p_domainName = null;


    /**************************************************************************
     * Create a new instance representing a group. <BR/>
     * Uses the username to get the user data.
     *
     * @param   id          Id of the group.
     * @param   oid         Oid of the group.
     * @param   name        Name of the group.
     * @param   domain      Domain where the group resides.
     * @param   domainName  Name of the domain.
     */
    public Group (int id, OID oid, String name, int domain, String domainName)
    {
        // set the instance's properties:
        this.p_id = id;
        this.p_oid = oid;
        this.p_name = name;
        this.p_domain = domain;
        this.p_domainName = domainName;
    } // Group


    /**************************************************************************
     * Compares this group with the passed group object. The method returns
     * <CODE>true</CODE> if the passed object is not <CODE>null</CODE> and it is
     * of type ibs.service.user.Group and it has the same values as this
     * group object in the following attributes, if they are defined: ID, OID
     * group name, domain and domain name. At least one of them has to be defined.
     * Otherwise it returns <CODE>false</CODE>.
     * 
     * @param	The object to compare with the group
     * 
     * @return	<CODE>true</CODE> if the objects are equal (as defined above),
     * 			<CODE>false</CODE> otherwise.
     */
    public boolean equals (Object obj)
    {
    	// the return value
    	boolean isEqual = false;

    	if (obj != null && obj instanceof Group)
    	{
    		Group grpObj = (Group) obj;

    		isEqual =
    				(this.p_id != 0 && grpObj.p_id != 0 && this.p_id == grpObj.p_id) ||
    				(this.p_oid != null && grpObj.p_oid != null && this.p_oid.equals (grpObj.p_oid)) ||
    				(this.p_name != null && grpObj.p_name != null && this.p_name.equals (grpObj.p_name)) ||
    				(this.p_domain != 0 && grpObj.p_domain != 0 && this.p_domain == grpObj.p_domain) ||
    				(this.p_domainName != null && grpObj.p_domainName != null && this.p_domainName.equals (grpObj.p_domainName));
    	} // if (obj != null && obj instanceof Group)

    	return isEqual;
    }

    /**************************************************************************
     * Get a String representation of the Group object. <BR/>
     *
     * @return  String representation of the group.
     */
    public String toString ()
    {
        StringBuffer result = new StringBuffer ();

        // check if there is also a domain name within the group:
        if (this.p_domainName != null)
        {
            result.append (this.p_domainName).append (".");
        } // if

        // append the name and the id:
        result.append (this.p_name)
            .append (" (").append (Integer.toString (this.p_id)).append (")");

        // return the result:
        return result.toString ();
    } // toString

} // class Group
