/*
 * Class: ConnectionDataHandler.java
 */

// package:
package ibs.extdata;

// import:
import ibs.bo.BusinessObject;
import ibs.bo.OID;
import ibs.bo.ObjectClassNotFoundException;
import ibs.bo.ObjectInitializeException;
import ibs.bo.ObjectNotFoundException;
import ibs.bo.type.TypeNotFoundException;
import ibs.di.DataElement;
import ibs.di.ValueDataElement;
import ibs.di.XMLViewer_01;
import ibs.io.IOHelpers;
import ibs.obj.user.User_01;
import ibs.service.user.User;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;

import java.util.Vector;


/******************************************************************************
 * This pool caches XMLViewer - Objects of m2 - Type 'APIConnector'. <BR/>
 * This APIConnector has to have at least the values:
 *     m2user, server, system, user, password
 * One Entry in Pool is identified via the values m2user, server, systemId
 *
 * @version     $Id: APIConnectionPool.java,v 1.11 2007/07/31 19:13:55 kreimueller Exp $
 *
 * @author      Andreas Jansa (AJ) 020117
 ******************************************************************************
 */
public class APIConnectionPool extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: APIConnectionPool.java,v 1.11 2007/07/31 19:13:55 kreimueller Exp $";


    /**
     * Type code for API connector.
     */
    protected final String TC_APICONNECTOR = "APIConnector";

    /**
     * All registert connections to external systems of all users of
     * current m2 installation as vector.
     */
    protected Vector<XMLViewer_01> connections = new Vector<XMLViewer_01> ();


    /**************************************************************************
     * Creates an APIConnectionPool Object. <BR/>
     */
    public APIConnectionPool ()
    {
        super ();
    } // APIConnectionPool


    /**************************************************************************
     * Initializes a business object. <BR/>
     */
    public void initClassSpecifics ()
    {
        // nothing to do
    } // initClassSpecifics


    /**************************************************************************
     * Add a new connection to the pool. <BR/>
     *
     * @param   connection  The connection to be added.
     */
    public void add (XMLViewer_01 connection)
    {
        if (connection != null)
        {
            this.connections.addElement (connection);
        } // if
    } // addConnectionData


    /**************************************************************************
     * Get a list of connections out of the pool. <BR/>
     *
     * @param   user    The user for whom to get the connections.
     *
     * @return  The connections.
     */
    public Vector<XMLViewer_01> fetch (User_01 user)
    {
        Vector<XMLViewer_01> userConnections = new Vector<XMLViewer_01> ();

        // get all connections for given user in connections vector:
        return userConnections;
    } // addConnectionData


    /**************************************************************************
     * Get connection data to specific system on specific server for
     * specific m2 user. <BR/>
     *
     * @param   m2user      current m2 user
     * @param   server      name of server to connect to
     * @param   systemId    id of system on external server
     *
     * @return  XMLViewer with VALUES:
     *
     * <VALUE FIELD="m2user" TYPE="OBJECTREF"/>
     * <VALUE FIELD="server" TYPE="QUERYSELECTIONBOX"/>
     * <VALUE FIELD="system" TYPE="QUERYSELECTIONBOX"/>
     * <VALUE FIELD="user" TYPE="CHAR"/>
     * <VALUE FIELD="password" TYPE="CHAR"/>
     *
     * this values could be accessed with the java code:
     * ...
     * ValueDataElement val = XMLViewer_01.dataElement.getValueElement ("name");
     * String value = val.value;
     * ...
     */
    public XMLViewer_01 fetch (User m2user, String server, String systemId)
    {
        return this.fetch (m2user.actUsername, server, systemId);
    } // fetch


    /**************************************************************************
     * Get connection data to specific system on specific server for
     * specific m2 user. <BR/>
     *
     * @param   name        name of user to get connection from
     * @param   server      name of server to connect to
     * @param   systemId    id of system on external server
     *
     * @return  XMLViewer with VALUES:
     *
     * <VALUE FIELD="m2user" TYPE="OBJECTREF"/>
     * <VALUE FIELD="server" TYPE="QUERYSELECTIONBOX"/>
     * <VALUE FIELD="system" TYPE="QUERYSELECTIONBOX"/>
     * <VALUE FIELD="user" TYPE="CHAR"/>
     * <VALUE FIELD="password" TYPE="CHAR"/>
     *
     * this values could be accessed with the java code:
     * ...
     * ValueDataElement val = XMLViewer_01.dataElement.getValueElement ("name");
     * String value = val.value;
     * ...
     */
    public XMLViewer_01 fetch (String name, String server, String systemId)
    {
        XMLViewer_01 connection = null;
        DataElement elem = null;
        ValueDataElement val = null;
        boolean found = false;

        // go through all registered connections
        for (int i = 0; i < this.connections.size () && !found; i++)
        {
            connection = this.connections.elementAt (i);
            elem = connection.dataElement;
/*
    HARDCODIERTE XML FIELDS
*/
            val = elem.getValueElement ("m2user");
            String userName = (val.value.indexOf (",") != -1) ?
                val.value.substring (val.value.indexOf (",") + 1,
                                     val.value.length ()) :
                null;

            if (val.value != null && name.equalsIgnoreCase (userName))
            {
                found = true;
            } // if


            val = elem.getValueElement ("server");
            if (found && !server.equalsIgnoreCase (val.value))
            {
                found = false;
            } // if

            val = elem.getValueElement ("system");
            if (found && !systemId.equalsIgnoreCase (val.value))
            {
                found = false;
            } // if
        } // for

        // check if connection was found
        if (found)
        {
            return connection;
        } // if

        return null;
    } // fetch


    /**************************************************************************
     * Get connection with specific oid. <BR/>
     *
     * @param   oid     oid of connection to be fetched
     *
     * @return  connection if connection was found,
     *          <CODE>null</CODE> if connection was not found.
     */
    public XMLViewer_01 fetch (OID oid)
    {
        XMLViewer_01 connection = null;
        // go through all registered connections
        for (int i = 0; i < this.connections.size (); i++)
        {
            connection = this.connections.elementAt (i);

            if (connection.oid.equals (oid))
            {
                return connection;
            } // if
        } // for
        return null;
    } // fetch


    /**************************************************************************
     * fills all APIConnections into Pool. <BR/>
     *
     * @throws  DBError
     *          An exception occurred during database operation.
     */
    public void fill () throws DBError
    {
        SQLAction action = null;
        OID conOid = null;
        int rowCount = 0;

        // get all querycreators of this domain with queryType SYSTEM
        // = 3'rd bit in bit pattern queryType

        String queryStr =
            " select o.oid" +
            " from ibs_Object o, ibs_Type t" +
            " where o.state = 2" +
            "   and o.tVersionId = t.actVersion" +
/*
 * HARDCODIERTER XML TYPECODE
 */
            "   and t.code = '" + this.TC_APICONNECTOR + "'";

        // open db connection -  only workaround - db connection must
        // be handled somewhere else
        action = this.getDBConnection ();

        // execute query:
        rowCount = action.execute (queryStr, false);

        // empty result set or error
        if (rowCount <= 0)
        {
            this.releaseDBConnection (action);
            return;         // leave method
        } // empty result set or error

        String errorMsg = "APIConnectionPool.fill () ERROR:";

        // get tuples out of db:
        while (!action.getEOF ())
        {
            // initialize query creator:
            conOid = SQLHelpers.getQuOidValue (action, "oid");

            XMLViewer_01 connection = null;
            try
            {
                connection = (XMLViewer_01)
                    this.getObjectCache ().fetchObject
                        (conOid, this.user, this.sess, this.env, false);
            } // try
            catch (ObjectNotFoundException e)
            {
                IOHelpers.showMessage (
                    errorMsg + e.getMessage (),
                    this.app, this.sess, this.env);
            } // catch
            catch (TypeNotFoundException e)
            {
                IOHelpers.showMessage (
                    errorMsg + e.getMessage (),
                    this.app, this.sess, this.env);
            } // catch
            catch (ObjectClassNotFoundException e)
            {
                IOHelpers.showMessage (
                    errorMsg + e.getMessage (),
                    this.app, this.sess, this.env);
            } // catch
            catch (ObjectInitializeException e)
            {
                IOHelpers.showMessage (
                    errorMsg + e.getMessage (),
                    this.app, this.sess, this.env);
            } // catch


            // check if connection was instantiated
            if (connection != null)
            {
                this.connections.addElement (connection);
            } // if

            action.next ();
        } // while

        // the last tuple has been processed
        // end transaction:
        action.end ();

        // close db connection in every case - only workaround -
        // db connection must be handled somewhere else:
        this.releaseDBConnection (action);
    } // fill


    /**************************************************************************
     * get count of connectiondata. <BR/>
     *
     * @return  Number of contained connection data objects. <BR/>
     */
    public int size ()
    {
        return this.connections.size ();
    } // addConnectionData


    /**************************************************************************
     * Returns a string representation of the object. <BR/>
     *
     * @return  a string representation of the object.
     */
    public String toString ()
    {
        XMLViewer_01 connection = null;
        DataElement elem = null;
        ValueDataElement val = null;
        boolean found = false;
        String output = "";

        // go through all registered connections
        for (int i = 0; i < this.connections.size () && !found; i++)
        {
            connection = this.connections.elementAt (i);
            elem = connection.dataElement;

            for (int j = 0; j < elem.values.size (); j++)
            {
                val = elem.values.elementAt (j);

                output += "[" + val.field + "='" + val.value + "'];";
            } // for
        } // for

        return output;
    } // to String


    /**************************************************************************
     * update connection. if connection does not exist, its added. <BR/>
     *
     * @param   con     Connection to be updated.
     */
    public void update (XMLViewer_01 con)
    {
        XMLViewer_01 connection = null;

        // go through all registered connections
        for (int i = 0; i < this.connections.size (); i++)
        {
            connection = this.connections.elementAt (i);

            if (con.oid.equals (connection.oid))
            {
                // if connection exist already chagne connection
                this.connections.setElementAt (con, i);
                return;
            } // if
        } // for

        this.add (con);
    } // addConnectionData

} // class ConnectionDataHandler
