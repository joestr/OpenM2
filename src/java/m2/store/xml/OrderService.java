/*
 * Class: OrderService.java
 */

// package:
package m2.store.xml;

// imports:
import ibs.bo.BusinessObject;
import ibs.bo.OID;
import ibs.io.Environment;
import ibs.io.IOHelpers;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.service.user.User;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;


/******************************************************************************
 * . <BR/>
 *
 * @version     $Id: OrderService.java,v 1.6 2007/07/23 08:21:37 kreimueller Exp $
 *
 * @author      Andreas Jansa (BW) 010322
 ******************************************************************************
 */
public class OrderService extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: OrderService.java,v 1.6 2007/07/23 08:21:37 kreimueller Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class OrderSevice. <BR/>
     */
    public OrderService ()
    {
        // nothing to do
    } // OrderService


    /**************************************************************************
     * Initializes a Servize. <BR/>
     *
     * @param   aUser   Object representing the user.
     * @param   aEnv    The actual environment object.
     * @param   aSess   The actual session info.
     * @param   aApp    The actual application info.
     */
    public void initService (User aUser, Environment aEnv,
                             SessionInfo aSess, ApplicationInfo aApp)
    {
// HACK AJ BEGIN
        // there has to be an empty oid to mark this function as
        // non-physical object. this is necessary for some GUI-Methods which are
        // trying to read rights from DB
        this.oid = OID.getEmptyOid ();
        this.isPhysical = false;
// HACK AJ END

        super.initObject (this.oid, aUser, aEnv, aSess, aApp);
    } // initService


    /**************************************************************************
     * Reads the oid of the ordersContainer for one user out of the db. <BR/>
     *
     * @param   aUserId  userid of the owner of the searched ordercontainer
     *
     * @return  oid of the ordercontainer for the given user
     *          null if somenthing went wrong
     */
    public OID performRetrieveOrdersContainerOid (int aUserId)
    {
        SQLAction aAction = null;
        int rowCount = 0;
        OID orderOid = null;

        String queryStr = " SELECT orders " +
                          " FROM ibs_workspace" +
                          " WHERE userId = " + aUserId;


        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection - only workaround - db connection must
            // be handled somewhere else
            aAction = this.getDBConnection ();
            rowCount = aAction.execute (queryStr, false);


            // empty resultset or error
            if (rowCount <= 0)
            {
                return null;                 // terminate this method
            } // if

            // get tupl out of db
            if (!aAction.getEOF ())
            {
                orderOid = SQLHelpers.getQuOidValue (aAction, "orders");
            } // if
        } // try
        catch (DBError e)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (e, this.app, this.sess, this.env);
        } // catch
        finally
        {
            // close db connection in every case - only workaround - db
            // connection must be handled somewhere else
            this.releaseDBConnection (aAction);
        } // finally

        return orderOid;
    } // performRetrieveOrdersContainerOid

} // class QueryObjectElement
