/*
 * Class: WorkflowObjectHandler.java
 */

// package:
package ibs.service.observer;

// imports:
import ibs.BaseObject;
import ibs.bo.BusinessObject;
import ibs.bo.OID;
import ibs.bo.ObjectClassNotFoundException;
import ibs.bo.ObjectInitializeException;
import ibs.bo.ObjectNotFoundException;
import ibs.bo.States;
import ibs.bo.cache.ObjectPool;
import ibs.bo.type.TypeNotFoundException;
import ibs.io.Environment;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.service.user.User;
import ibs.tech.sql.DBConnector;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;


/******************************************************************************
 * Holds methods to create/handle/modify objects for an ObserverJob in
 * an m2-context. <BR/>
 *
 * @version     $Id: M2ObserverObjectHandler.java,v 1.1 2007/07/24 21:28:12 kreimueller Exp $
 *
 * @author      Horst Pichler (HP), 02.10.2002
 ******************************************************************************
 */
public class M2ObserverObjectHandler extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: M2ObserverObjectHandler.java,v 1.1 2007/07/24 21:28:12 kreimueller Exp $";


    //
    // m2 environment
    //
    /**
     * Holds current m2-user.
     */
    protected User user = null;
    /**
     * Holds m2-environment.
     */
    protected Environment env = null;
    /**
     * Holds m2-Sessioninfo.
     */
    protected SessionInfo sess = null;
    /**
     * Holds m2-Applicationinfo app.
     */
    protected ApplicationInfo app = null;


    /**************************************************************************
     * Creates an WorkflowDefinition Object. <BR/>
     *
     * @param   user    The actual user.
     * @param   env     The actual environment.
     * @param   sess    The actual session info object.
     * @param   app     The global application info object.
     */
    public M2ObserverObjectHandler (User user, Environment env,
                                    SessionInfo sess, ApplicationInfo app)
    {
        // set m2 environment:
        this.user = user;
        this.env = env;
        this.sess = sess;
        this.app = app;
    } // m2ObserverObjectHandler


    /**************************************************************************
     * Retrieves a business object. <BR/>
     *
     * @param   oid     The oid of the object to retrieve.
     * @param   user    User for fetch-operation.
     *
     * @return  The business object, <CODE>null</CODE> if no success.
     *
     * @throws  ObjectNotFoundException
     *          The object was not found.
     * @throws  TypeNotFoundException
     *          The object type was not found.
     * @throws  ObjectClassNotFoundException
     *          The class for the object was not found.
     * @throws  ObjectInitializeException
     *          The object could not be initialized.
     */
    public BusinessObject fetchObject (OID oid, User user)
        throws ObjectNotFoundException, TypeNotFoundException,
            ObjectClassNotFoundException, ObjectInitializeException
    {
        // retrieve object; returns null if not found
        return ((ObjectPool) this.app.cache).fetchObject (oid,
                user, this.sess, this.env, true);
    } // fetchObject


    /**************************************************************************
     * Get a user from the DB by its id (int). <BR/>
     *
     * @return  The user object; <CODE>null</CODE> if not found
     *
     * @throws  DBError
     *          An error occurred during a database operation.
     */
    protected User getSystemUser () throws DBError
    {
        int rowCount = 0;
        User theUser = null;
        SQLAction action = null;        // the action object used to access the
                                        // database

        // select the domains administrator-data; ensure that he is
        // - in the same domain as the current user
        // - active (in ibs_Object)
        String queryStr = " SELECT u.id, u.oid, u.domainId, u.password, u.fullname, u.name " +
                          " FROM ibs_User u, ibs_Object o, ibs_Domain_01 d " +
                          " WHERE d.id = " + this.user.domain +
                          " AND d.adminid = u.id " +
                          " AND u.oid = o.oid " +
                          " AND o.state = " + States.ST_ACTIVE;

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            // open connection - begin transaction
            action = DBConnector.getDBConnection ();
            rowCount = action.execute (queryStr, false);

            // empty resultset or error
            if (rowCount == 1)
            {
                // create user object
                theUser = new User ("");

                // get tuple out of db
                if (!action.getEOF ())
                {
                    theUser.id = action.getInt ("id");
                    theUser.username = action.getString ("name");
                    theUser.oid = SQLHelpers.getQuOidValue (action, "oid");
                    theUser.domain = action.getInt ("domainId");
                    theUser.password = action.getString ("password");
                    theUser.fullname = action.getString ("fullname");
                } // if
            } // if
        } // try
        finally
        {
            action.end ();
            DBConnector.releaseDBConnection (action);
        } // finally

        return theUser;
    } // getSystemUser

} // m2ObserverObjectHandler
