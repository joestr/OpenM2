/*
 * Class: GroupHelpers.java
 */

// package:
package ibs.service.user;

//imports:
import ibs.bo.BOHelpers;
import ibs.bo.OID;
import ibs.bo.States;
import ibs.io.Environment;
import ibs.io.IOHelpers;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLConstants;
import ibs.tech.sql.SQLHelpers;

import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * This class implements some useful methods for user management. <BR/>
 *
 * @version     $Id: GroupHelpers.java,v 1.3 2010/11/19 10:51:08 btatzmann Exp $
 *
 * @author      Bernhard Tatzmann (BT), 20101115
 ******************************************************************************
 */
public abstract class GroupHelpers extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: GroupHelpers.java,v 1.3 2010/11/19 10:51:08 btatzmann Exp $";



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////  
    
    /**************************************************************************
     * Generates a Vector with all user OID belonging to the given groups. <BR/>
     * User OIDs are returned unique even if they belong to several of the <BR/>
     * given groups. <BR/>
     * In case there has no group id be specified all users will be listed
     *
     * @param   groupOIDs     group OID vectory (null ... list all users)
     * @param   usersFilter   Filterstring for the query
     *
     * @return  The users OID vector.
     */
    public static Vector<OID> getUsersForGroups (Vector<OID> groupOIDs, String usersFilter,
            User currentUser, Environment env)
    {
        Vector<OID> userOIDs = new Vector<OID> ();
        SQLAction action = null;        // the action object used to access the
                                        // database
        String viewContent = "v_UserContainer_01$content";
                                        // set view to use for the query
        StringBuffer queryStr;

        // create the SQL String to select all tuples
        // workaround: there are no right checks done
        queryStr =
            new StringBuffer ("SELECT DISTINCT v.oid, u.fullname as name")
                .append (" FROM  ").append (viewContent).append (" v, ibs_user u");

        // check if there is a group filter set:
        if (groupOIDs != null && groupOIDs.size () > 0) // group set?
        {
            queryStr.
                append (", ibs_groupUser gu, ibs_group gr")
                .append (" WHERE  (");

            // Iterate groups
            Iterator<OID> it = groupOIDs.iterator ();
            while (it.hasNext ())
            {
                queryStr.append ("gr.oid = ").append (it.next ().toString ());

                if (it.hasNext ())
                {
                    queryStr.append (" OR ");
                } // if has next
            } // WHILE iterate groups

            queryStr.append (")  AND gu.groupId = gr.id  AND  gu.userId = u.id ");
        } // if group set
        else                            // no group as filter
        {
            queryStr
                .append (" WHERE  u.domainId = ").append (currentUser.domain);
        } // else no group as filter

        queryStr
            .append (" AND v.userId = ").append (currentUser.id)
            .append (" AND v.oid = u.oid ");

        // check if a user name filter has been set:
        if (usersFilter != null && usersFilter.length () > 0)
        {
            queryStr
                .append (" AND ").append (SQLHelpers.getQueryConditionString (
                    "u.fullname", SQLConstants.MATCH_SUBSTRING, usersFilter, false));
        } // if
        queryStr.append (" ORDER BY name ");

        // open db connection -  only workaround - db connection must
        // be handled somewhere else
        action = BOHelpers.getDBConnection (env);

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            action.execute (queryStr, false);

            // get tuples out of db
            while (!action.getEOF ())
            {
                // create entries in list
                userOIDs.addElement (SQLHelpers.getQuOidValue (action, "oid"));

                // step one tuple ahead for the next loop
                action.next ();
            } // while

            // the last tuple has been processed
            // end transaction
            action.end ();
        } // try
        catch (DBError dbErr)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (dbErr, env.getApplicationInfo (), env.getSessionInfo (), env);
        } // catch
        finally
        {
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            BOHelpers.releaseDBConnection (action, env);
        } // finally

        return userOIDs;
    } // getUsersForGroups
    
    
    /**************************************************************************
     * Retrieve oid for each group-name in given list. <BR/>
     * Names of groups that do not exists (no group, deleted, ...)
     * are stored in the second parameter groupNamesNonExistent)
     *
     * @param   groupNames              List of group-names.
     * @param   groupNamesNonExistent   List of group-names of groups not found
     *                                  in system.
     * @param   currentUser             The current user
     * @param   env                     The environment
     *
     * @return  List of group-oids; empty if no groups given or found
     *          <CODE>null</CODE> if an error occurred.
     */
    public static Vector<OID> getGroupOidsByNames (Vector<String> groupNames,
                                         Vector<String> groupNamesNonExistent,
                                         User currentUser, Environment env)
    {
        Vector<OID> groupOIDs = new Vector<OID> ();
        Vector<String> groupNamesFromDB = new Vector<String> ();
        SQLAction action = null;        // the action object used to access the
                                        // database

        // build WHERE-clause for SELECT query
        String whereClause = "";
        String elem;

        for (Iterator<String> iter = groupNames.iterator (); iter.hasNext ();)
        {
            // get next element:
            elem = iter.next ();

            // check if valid:
            if (elem != null)
            {
                // add to clause
                whereClause += "o.name = '" + elem + "'";
            } // if

            if (iter.hasNext ())
            {
                whereClause += " OR ";
            } // if
        } // for iter

        // select group-oids from the ibs_Group table by his/her name
        // ensure that he/she is
        // - in the same domain
        // - active (in ibs_Object)
        String queryStr = " SELECT g.oid, g.name " +
                          " FROM ibs_Group g, ibs_Object o " +
                          " WHERE (" + whereClause + ")" +
                          " AND g.oid = o.oid " +
                          " AND o.state = " + States.ST_ACTIVE +
                          " AND g.domainId = " + currentUser.domain;

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = BOHelpers.getDBConnection (env);
            action.execute (queryStr, false);

            // get tuple out of db
            while (!action.getEOF ())
            {
                // add to vectors
                groupOIDs.addElement (SQLHelpers.getQuOidValue (action, "oid"));
                groupNamesFromDB.addElement (action.getString ("name"));
                // switch to next row
                action.next ();
            } // if
        } // try
        catch (DBError err)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (err, env.getApplicationInfo (), env.getSessionInfo (), env, false);
            groupOIDs = null;
        } // catch
        finally
        {
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            BOHelpers.releaseDBConnection (action, env);
        } // finally

        // compare entries of given groupNames with entries of
        // retrieved groupNamesFromDB; store not-retrieved names
        // in groupNamesNonExistent
        String u1;
        String u2;
        boolean found;

        for (Iterator<String> iter1 = groupNames.iterator (); iter1.hasNext ();)
        {
            // get next name:
            u1 = iter1.next ();

            // try to find it in other list:
            found = false;

            for (Iterator<String> iter2 = groupNamesFromDB.iterator ();
                iter2.hasNext ();)
            {
                // get next name:
                u2 = iter2.next ();

                // compare the names:
                if (u1.equalsIgnoreCase (u2))
                {
                    // entry found
                    found = true;
                    break;
                } // if
            } // for iter2

            // if not found in other list add to groupNamesNonExistent
            if (!found)
            {
                groupNamesNonExistent.addElement (u1);
            } // if
        } // for iter1

        return groupOIDs;
    } // getGroupOidsByNames


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////


} // class GroupHelpers
