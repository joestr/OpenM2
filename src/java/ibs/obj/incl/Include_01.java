/*
 * Class: Include_01.java
 */

// package:
package ibs.obj.incl;

// imports:
import ibs.bo.Operations;
import ibs.di.XMLViewer_01;
import ibs.io.IOHelpers;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.util.StringHelpers;

import java.util.Vector;


/******************************************************************************
 * Include_01 handles the user specific includes what can be inserted
 * in any include page. <BR/>
 *
 * @version     $Id: Include_01.java,v 1.3 2009/07/24 09:49:31 kreimueller Exp $
 *
 * @author      Bernd Buchegger (BB), 20060718
 ******************************************************************************
 */
public class Include_01 extends XMLViewer_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Include_01.java,v 1.3 2009/07/24 09:49:31 kreimueller Exp $";


    /**
     * Include topic for welcome page. <BR/>
     */
    public static final String TOPIC_WELCOME = "welcome";


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // call common initializer:
        super.initClassSpecifics ();

    } // initClassSpecifics


    /**************************************************************************
     * Get the includes ordered by priority and optionally filtered by topic.
     *
     * @return  the includes in a string array or <code>null</code> otherwise
     */
    public String getWelcomeIncludes ()
    {
        return StringHelpers.stringArrayToString (
            this.getIncludes (Include_01.TOPIC_WELCOME));
    } // getWelcomeIncludes


    /**************************************************************************
     * Get the includes ordered by priority and optionally filtered by topic.
     *
     * @param topic     an optional topic can be set to filter the includes
     *
     * @return  the includes in a string array or <code>null</code> otherwise
     */
    public String [] getIncludes (String topic)
    {
        Vector<String> includes = new Vector<String> ();
        String [] result = null;
        int rowCount;
        SQLAction action = null;        // action object used to access the DB

        StringBuffer queryStr = new StringBuffer ()
            .append (" SELECT incl.m_content")
            .append (" FROM v_container$rights oincl, dbm_include incl ")
            .append (" WHERE oincl.oid = incl.oid ")
            .append (" AND oincl.userid = ").append (this.user.id)
            .append (SQLHelpers.getStringCheckRights (Operations.OP_VIEW, "oincl.rights"));
        if (topic != null)
        {
            queryStr.append (" AND ").append (
                    SQLHelpers.getQueryConditionString ("incl.m_topic",
                            SQLConstants.MATCH_EXACT, topic, false));
        } // if (topic != null)
        // order by priority
        queryStr.append (" ORDER BY incl.m_priority DESC");

        try
        {
            action = this.getDBConnection ();
            rowCount = action.execute (queryStr, false);
            // check if we got any data
            if (rowCount > 0)
            {
                // loop through the result
                while (!action.getEOF ())
                {
                    includes.add (action.getString ("m_content"));
                    action.next ();
                } // while (!action.getEOF())
            } // if (rowCount == 1)
            // end transaction
            action.end ();
        } // try
        catch (DBError e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
        finally
        {
            this.releaseDBConnection (action);
        } // finally
        // got any data?
        if (includes.size () > 0)
        {
            // construct the result string array
            result = includes.toArray (new String[includes.size ()]);
        } // if (includes.size() > 0)
        // return the result
        return result;
    } // getSubmissionOrderReceivers

} // Include_01
