/*
 * Class: User.java
 */

// package:
package ibs.service.user;

// imports:
import ibs.BaseObject;
//KR TODO: unsauber
import ibs.bo.OID;
import ibs.di.DIConstants;
import ibs.tech.xml.XMLWriter;
import ibs.tech.xml.XMLWriterException;
import ibs.util.UtilConstants;

import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/******************************************************************************
 * This class contains all information regarding a user. <BR/>
 * There are the username, maybe the user password, the groups, where the user
 * belongs to, etc.
 *
 * @version     $Id: User.java,v 1.11 2012/11/02 14:01:04 gweiss Exp $
 *
 * @author      Klaus Reimüller (KR), 980303
 ******************************************************************************
 */
public class User extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: User.java,v 1.11 2012/11/02 14:01:04 gweiss Exp $";


    /**
     * Id of the user. <BR/>
     */
    public int id = 0;

    /**
     * Oid of the user. <BR/>
     */
    public OID oid = null;

    /**
     * The domain where the user resides. <BR/>
     */
    public int domain = 0;

    /**
     * The name of the domain where the user resides. <BR/>
     */
    public String domainName = null;

    /**
     * The name of the actual user. <BR/>
     * This property is used for login purposes.
     * Normally this property is set to <A HREF="#username">username</A>.
     */
    public String actUsername = null;

    /**
     * The name the user used for logging in. <BR/>
     */
    public String username = null;

    /**
     * The password the user used for logging in. <BR/>
     */
    public String password = null;

    /**
     * The full name of the user. <BR/>
     */
    public String fullname = null;

    /**
     * Defines if the user has to change the password on the next login.
     */
    public boolean p_changePwd = false;

    /**
     * The groups where the user belongs to. <BR/>
     */
    private Vector<Group> p_groups = null;

    /**
     * String representation of the groups within the list. <BR/>
     */
    private String p_groupsStr = null;

    /**
     * Dom tree of the user data. <BR/>
     */
    private Element p_domTree = null;

    /**************************************************************************
     * Create a new instance representing a user. <BR/>
     * Uses the user id to get the user data.
     *
     * @param   id          Id of the required user.
     */
    public User (int id)
    {
        // set the instance's properties:
        this.id = id;

        // get the username:

        // get the groups where the user belongs to:
    } // User


    /**************************************************************************
     * Create a new instance representing a user. <BR/>
     * Uses the username to get the user data.
     *
     * @param   username    Name which the person used for logging on.
     */
    public User (String username)
    {
        // set the instance's properties:
        this.username = username;
        this.actUsername = this.username;

        // get the id of the user:

        // get the groups where the user belongs to:
    } // User


    /**************************************************************************
     * Create a new instance representing a user. <BR/>
     * Uses the username to get the user data.
     *
     * @param   domain      Domain where the person logged on.
     * @param   username    Name which the person used for logging on.
     * @param   password    Password which the person used for logging on.
     */
    public User (int domain, String username, String password)
    {
        // set the instance's properties:
        this.domain = domain;
        this.username = username;
        this.password = password;
        this.actUsername = this.username;

        // get the id of the user:

        // get the groups where the user belongs to:
    } // User


    /**************************************************************************
     * Check if the user has enough rights to perform the required operation on
     * the object. <BR/>
     * Uses the username to get the user data.
     *
     * @param   oid         The oid of the object, on which to check the rights.
     * @param   operation   The necessary rights.
     *
     * @return  <CODE>true</CODE> if the user has the rights,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean hasRights (OID oid, int operation)
    {
        // define local variables:
        int retVal = UtilConstants.QRY_OK;            // return value of query

        // perform stored procedure in database to get the rights data
/*
        retVal = ...
*/

        // check the rights
        if (retVal == UtilConstants.QRY_INSUFFICIENTRIGHTS) // access not allowed?
        {
            return false;
        } // if access not allowed

        // access allowed
        return true;
    } // hasRights


    /**************************************************************************
     * Get the groups. <BR/>
     *
     * @return  The groups where the user belongs to.
     *          <CODE>null</CODE> if no groups defined.
     */
    public Vector<Group> getGroups ()
    {
        // get the property value and return the result:
        return this.p_groups;
    } // getGroups


    /**************************************************************************
     * Check if this user is member of a group specified by group name
     * 
     * @param	The name of the group in which the membership has to be checked
     *
     * @return  <CODE>true</CODE>, if the user is member of group "groupName";
     * 			<CODE>false</CODE> otherwise.
     */
    public boolean isInGroup (String groupName)
    {
        // get the property value and return the result:
        return this.getGroups ().contains (new Group (0, null, groupName, 0, null));
    } // isInGroup


    /**************************************************************************
     * Set the groups. <BR/>
     * Each group must be an instance of {@link Group Group}.
     *
     * @param   groups  The groups to be set.
     */
    public void setGroups (Vector<Group> groups)
    {
        Vector<Group> groupsLocal = groups; // variable for local assignments
        StringBuffer groupsStr = new StringBuffer ();
        StringBuffer sep = new StringBuffer ();
        StringBuffer sep2 = new StringBuffer (",");

        // check if there are some groups to be set:
        if (groupsLocal != null && groupsLocal.size () == 0)
        {
            // a vector with size 0 is not allowed
            groupsLocal = null;
        } // if

        // set the property value:
        this.p_groups = groupsLocal;

        // check if there are some groups to be set:
        if (groupsLocal != null)
        {
            // compute the groups string:
            // concatenate all groups to a string separated by a comma
            for (Iterator<Group> iter = groupsLocal.iterator (); iter.hasNext ();)
            {
                Group group = iter.next ();

                groupsStr.append (sep).append (group.p_name);
                sep = sep2;
            } // for iter

            // set the concatenated groups string:
            this.setGroupsStr (groupsStr.toString ());
        } // if
    } // setGroups


    /**************************************************************************
     * Get the group names as comma-separated list within string. <BR/>
     *
     * @return  The groups where the user belongs to.
     *          <CODE>null</CODE> if no groups defined.
     */
    public String getGroupsStr ()
    {
        // get the property value and return the result:
        return this.p_groupsStr;
    } // getGroupsStr


    /**************************************************************************
     * Set the groups string. <BR/>
     *
     * @param   groupsStr   The groups string to be set.
     */
    private void setGroupsStr (String groupsStr)
    {
        // set the property value:
        this.p_groupsStr = groupsStr;
    } // setGroupsStr


    /**************************************************************************
     * Get the groups. <BR/>
     *
     * @return  The groups where the user belongs to.
     *          <CODE>null</CODE> if no groups defined.
     *
     * @throws  XMLWriterException
     *          There was an exception during trying to create the document. <BR/>
     *          Possible causes: the document could not be created.
     */
    public Element getDomTree () throws XMLWriterException
    {
        // check if there is already a dom tree set:
        if (this.p_domTree == null)
        {
            // create the dom tree:
            this.createDomTree ();
        } // if

        // get the property value and return the result:
        return this.p_domTree;
    } // getDomTree


    /**************************************************************************
     * Create dom tree. <BR/>
     * This partial dom tree will be used within dom trees for output
     * processing. <BR/>
     * It has the following structure:
     * <PRE>
     *     &lt;USER ID=".." OID=".." NAME=".." DOMAINID=".." DOMAINNAME=".."&gt;
     *         &lt;GROUPS&gt;
     *             &lt;GROUP ID=".." OID=".." NAME=".."&gt;
     *             &lt;/GROUP&gt;
     *             ...
     *         &lt;/GROUPS&gt;
     *     &lt;/USER&gt;
     * </PRE>
     *
     * @throws  XMLWriterException
     *          There was an exception during trying to create the document. <BR/>
     *          Possible causes: the document could not be created.
     */
    private void createDomTree () throws XMLWriterException
    {
        Document doc = null;

        try
        {
            // create a new DOM root:
            doc = XMLWriter.createDocument ();
        } // try
        catch (XMLWriterException e)
        {
            throw e;
        } // catch

        // create the <USER> xml tag and set attributes:
        Element userInfo = doc.createElement (DIConstants.ELEM_USER);
        userInfo.setAttribute (DIConstants.ATTR_ID, Integer.toString (this.id));
        userInfo.setAttribute (DIConstants.ATTR_OID, this.oid.toString ());
        userInfo.setAttribute (DIConstants.ATTR_NAME, this.username);
        userInfo.setAttribute ("DOMAINID", Integer.toString (this.domain));
        userInfo.setAttribute ("DOMAINNAME", this.domainName);

        // create the <GROUPS> tag:
        Element groupsElem = doc.createElement (DIConstants.ELEM_GROUPS);

        // loop through all groups and create the corresponding tag:
        for (Iterator<Group> iter = this.getGroups ().iterator (); iter.hasNext ();)
        {
            Group group = iter.next ();

            // create <GROUP> tag and set attributes:
            Element groupElem = doc.createElement (DIConstants.ELEM_GROUP);
            groupElem.setAttribute (DIConstants.ATTR_ID, Integer.toString (group.p_id));
            groupElem.setAttribute (DIConstants.ATTR_OID, group.p_oid.toString ());
            groupElem.setAttribute (DIConstants.ATTR_NAME, group.p_name);

            // add the group to the GROUPS tag:
            groupsElem.appendChild (groupElem);
        } // for iter

        // add the groups to the USER tag:
        userInfo.appendChild (groupsElem);

        // store the dom tree:
        this.p_domTree = userInfo;
    } // createDomTree


    /**************************************************************************
     * Get a String representation of the User object. <BR/>
     *
     * @return  String representation of the user.
     */
    public String toString ()
    {
        if (this.fullname != null)      // fullname is set?
        {
            return this.fullname;       // return the full name
        } // if
        else if (this.username != null) // username is set?
        {
            return this.username;       // return the username
        } // else if
        else                            // username unknown
        {
            return "";                  // return empty string
        } // else
    } // toString

} // class User
