/*
 * Class: UserHelpers.java
 */

// package:
package ibs.service.user;

//imports:
import ibs.bo.BOHelpers;
import ibs.bo.OID;
import ibs.io.Environment;
import ibs.io.IOHelpers;
import ibs.io.session.SessionInfo;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.SelectQuery;

import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * This class implements some useful methods for user management. <BR/>
 *
 * @version     $Id: UserHelpers.java,v 1.3 2010/11/19 10:51:08 btatzmann Exp $
 *
 * @author      Klaus Reimüller (KR), 20100220
 ******************************************************************************
 */
public abstract class UserHelpers extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: UserHelpers.java,v 1.3 2010/11/19 10:51:08 btatzmann Exp $";



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Check if an user is within a specific group. <BR/>
     *
     * @param   userOid     Oid of user for whom to find out the group dependency.
     * @param   groupOid    Oid of group which shall be checked.
     * @param   env         The actual environment.
     * @param   sess        The actuel user session.
     * @param   caller      The caller object of this method.
     *
     * @return  <CODE>true</CODE> if the user is within the group,
     *          <CODE>false</CODE> otherwise.
     */
    public static boolean performCheckIsInGroup (OID userOid, OID groupOid,
                                                 Environment env,
                                                 SessionInfo sess, Object caller)
    {
        // create the query string:
        SelectQuery query = new SelectQuery (
            new StringBuilder ()
                .append ("g.id, g.oid, g.name,")
                .append (" d.id AS domainId, od.name AS domainName"),
            new StringBuilder ()
                .append (" ibs_GroupUser gu inner join ibs_Group g on gu.groupId = g.id")
                .append (" inner join ibs_User u on gu.userId = u.id")
                .append (" inner join ibs_Domain_01 d on g.domainId = d.id")
                .append (" inner join ibs_Object od on d.oid = od.oid"),
            new StringBuilder ()
                .append (" g.oid = ").append (groupOid.toStringQu ())
                .append (" AND u.oid = ").append (userOid.toStringQu ()),
            null, null, null);
        query.setUseDistinct (true);

        // execute the query and return the result:
        Vector<Group> groups = performGetGroups (query, env, sess, caller);
        return (groups != null && groups.size () > 0);
    } // performCheckIsInGroup


    /**************************************************************************
     * Get the groups where the user belongs to. <BR/>
     *
     * @param   userOid Oid of user for whom to get the groups.
     * @param   env     The actual environment.
     * @param   sess    The actuel user session.
     * @param   caller  The caller object of this method.
     *
     * @return  The groups where the user belongs to,
     *          <CODE>null</CODE> if there was no group found.
     */
    public static Vector<Group> performGetGroups (OID userOid, Environment env,
                                                  SessionInfo sess,
                                                  Object caller)
    {
        // create the query string:
        SelectQuery query = new SelectQuery (
            new StringBuilder ()
                .append ("g.id, g.oid, g.name,")
                .append (" d.id AS domainId, od.name AS domainName"),
            new StringBuilder ()
                .append (" ibs_GroupUser gu inner join ibs_Group g on gu.groupId = g.id")
                .append (" inner join ibs_User u on gu.userId = u.id")
                .append (" inner join ibs_Domain_01 d on g.domainId = d.id")
                .append (" inner join ibs_Object od on d.oid = od.oid"),
            new StringBuilder ()
                .append (" u.oid = ").append (userOid.toStringQu ()),
            null, null, null);
        query.setUseDistinct (true);

        // execute the query and return the result:
        return performGetGroups (query, env, sess, caller);
    } // performGetGroups


    /**************************************************************************
     * Get the groups where the user belongs to. <BR/>
     *
     * @param   userId  Id of user for whom to get the groups.
     * @param   env     The actual environment.
     * @param   sess    The actuel user session.
     * @param   caller  The caller object of this method.
     *
     * @return  The groups where the user belongs to,
     *          <CODE>null</CODE> if there was no group found.
     */
    public static Vector<Group> performGetGroups (int userId, Environment env,
                                                  SessionInfo sess,
                                                  Object caller)
    {
        // create the query string:
        SelectQuery query = new SelectQuery (
            new StringBuilder ()
                .append ("g.id, g.oid, g.name,")
                .append (" d.id AS domainId, od.name AS domainName"),
            new StringBuilder ()
                .append (" ibs_GroupUser gu, ibs_Group g,")
                .append (" ibs_Domain_01 d, ibs_Object od"),
            new StringBuilder ()
                .append (" gu.groupId = g.id")
                .append (" AND g.domainId = d.id")
                .append (" AND d.oid = od.oid")
                .append (" AND gu.userId = ").append (Integer.toString (userId)),
            null, null, null);
        query.setUseDistinct (true);

        // execute the query and return the result:
        return performGetGroups (query, env, sess, caller);
    } // performGetGroups


    /**************************************************************************
     * Get the groups where the user belongs to. <BR/>
     *
     * @param   query   The query to be executed.
     * @param   env     The actual environment.
     * @param   sess    The actuel user session.
     * @param   caller  The caller object of this method.
     *
     * @return  The groups where the user belongs to,
     *          <CODE>null</CODE> if there was no group found.
     */
    private static Vector<Group> performGetGroups (SelectQuery query,
                                                   Environment env,
                                                   SessionInfo sess,
                                                   Object caller)
    {
        Vector<Group> groups = new Vector<Group> ();  // the groups
        SQLAction action = null;        // the action object used to access the DB
        int rowCount;

        // constraint: the query must be defined:
        if (query == null)
        {
            return groups;
        } // if

        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = BOHelpers.getDBConnection (env, sess, caller);
            // execute the queryString, indicate that we're not performing an
            // action query:
            rowCount = action.execute (query);

            // check if the resultset is not empty:
            if (rowCount > 0)           // groups found?
            {
                // get tuples out of db:
                while (!action.getEOF ())
                {
                    // create new group instance:
                    Group group = new Group (action.getInt ("id"),
                        SQLHelpers.getQuOidValue (action, "oid"),
                        action.getString ("name"),
                        action.getInt ("domainId"),
                        action.getString ("domainName"));

                    // append the group to the result vector:
                    groups.add (group);

                    // step one tuple ahead for the next loop:
                    action.next ();
                } // while
            } // if rowcount > 0
            else                        // no groups found
            {
                groups = null;
            } // else no domains found
        } // try
        catch (DBError e)
        {
            // show error message:
            IOHelpers.showMessage (e, env, true);
        } // catch
        finally
        {
            // close db connection in every case -  only workaround -
            // db connection must be handled somewhere else
            BOHelpers.releaseDBConnection (action, env, sess);
        } // finally

        // return the result:
        return groups;
    } // performGetGroups


    /**************************************************************************
     * Check if the given user is in one(!) of the given groups. <BR/>
     *
     * @param   groupNames              List of group-names.
     * @param   currentUser             The current user
     * @param   env                     The environment
     *
     * @return  <CODE>true</CODE> if the user is within the group,
     *          <CODE>false</CODE> otherwise.
     */
    public static boolean performCheckIsCurrentUserInGroups (Vector<String> groupNames,
            User currentUser, Environment env)
    {
        Iterator<Group> it = currentUser.getGroups().iterator();
        
        while(it.hasNext())
        {
            Group group = it.next();
            
            if(groupNames.contains (group.p_name))
            {
                return true;
            } // if
        } // while
        
        return false;
    } // performCheckIsCurrentUserInGroups



    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////


} // class UserHelpers
