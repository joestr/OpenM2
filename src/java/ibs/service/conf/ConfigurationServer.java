/*
 * Class: Configuration.java
 */

// package:
package ibs.service.conf;

// imports:
import ibs.BaseObject;
import ibs.io.Environment;
import ibs.io.IOConstants;
import ibs.ml.MultilingualTextProvider;
import ibs.service.conf.ConfigurationConstants;
import ibs.service.conf.ServerRecord;
import ibs.util.NoServersConfiguredException;
import ibs.util.ServerRequestNotAllowedException;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;
import ibs.util.UtilExceptions;

import java.util.Vector;


/******************************************************************************
 * This class defines the configuration properties. <BR/>
 *
 * @version     $Id: ConfigurationServer.java,v 1.20 2010/04/07 13:37:12 rburgermann Exp $
 *
 * @author      Bernd Martin (BM) 001127
 ******************************************************************************
 */
public class ConfigurationServer extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ConfigurationServer.java,v 1.20 2010/04/07 13:37:12 rburgermann Exp $";

    /**
     * Placeholder that fits all servers.
     * Can be used in order to allow access to the application
     * through any url. <BR/>
     */
    public static final String ALLSERVERS = "*";

    /**
     * The vector of all configurated servers given
     * in the ibssystem.cfg configuration file. <BR/>
     */
    private Vector<ServerRecord> servers = null;


    /**************************************************************************
     * Constructor of ConfigurationServer. <BR/>
     */
    public ConfigurationServer ()
    {
        // initialize a new Vector
        this.servers = new Vector<ServerRecord> ();
    } // ConfigurationServer


    /**************************************************************************
     * Adds a tuple to the vector of possible configuration servers. <BR/>
     *
     * @param   applicationServer       Name of the applicationserver.
     * @param   sslServer               Name of the sslserver.
     * @param   applicationServerPort   Port of the applicationserver.
     * @param   sslServerPort           Port of the sslserver.
     * @param   ssl                     SSL server usable for m2 or not.
     */
    public void addServerTuple (String applicationServer, String sslServer,
        String applicationServerPort, String sslServerPort, String ssl)
    {
        // get a new record and set the values for the attributes
        ServerRecord s = new ServerRecord (applicationServer, sslServer,
            applicationServerPort, sslServerPort, ssl);

        // insert the record in the vector
        this.addServerRecord (s);
    } // addServerRecord


    /**************************************************************************
     * Adds a tuple to the vector of possible configuration servers. <BR/>
     *
     * @param   server  Settings of the server.
     */
    public void addServerRecord (ServerRecord server)
    {
        // if at least one correct entry then add it into the vector, otherwise
        // do not do anything
        if (server != null &&
            ((server.p_applicationServer.length () > 0 &&
             server.p_applicationServerPort > 0) ||
            (server.p_sslServer.length () > 0 && server.p_sslServerPort > 0)))
        {
            this.servers.add (server);
        } // if
    } // addServerRecord


    /**************************************************************************
     * Returns the servers of the configuration. <BR/>
     *
     * @return  String representation of configuration server elements.
     */
    public String toString ()
    {
        int size;
        String msg = "";
        ServerRecord s;

        size = this.servers.size ();

        // loop through the whole vector
        for (int i = 0; i < size; i++)
        {
            if (this.servers.elementAt (i) != null)
            // if a null element found do nothing
            {
                // loop through all the elements
                s = this.servers.elementAt (i);
                msg += s.toString (i);
            } // if a null element found do nothing
        } // for

        msg = size + " tuples declared\n" + msg;

        return msg;
    } // toString


    /***************************************************************************
     * Returns the appropriate server record for the configuration give selected
     * by a given URL. The URL can be an applicationserveraddress or an
     * SSLserveraddress. <BR/>
     *
     * @param url The url for which to get the server record.
     * @param env The current environment
     *
     * @return The ServerRecord for the given URL
     *
     * @throws ServerRequestNotAllowedException Server record not found.
     * @throws NoServersConfiguredException There are no servers in the
     *             configuration.
     */
    public ServerRecord getServerRecord (String url, Environment env)
        throws ServerRequestNotAllowedException, NoServersConfiguredException
    {
        String baseUrl = url;
        ServerRecord tmpServerRecord = null;

        if (this.servers.size () <= 0)
        {
            // if no record is in the record an exception is raised.
            throw new NoServersConfiguredException (MultilingualTextProvider.getMessage (
                UtilExceptions.EXC_BUNDLE, UtilExceptions.ML_E_NOSERVERSCONFIGURED, 
                new String[] {ConfigurationConstants.FILE_IBSSYSTEM}, env));
        } // if

        // if a protocol is included in the url
        int i = url.indexOf (":");

        if (i > -1)                     // not the default protocol is used and
                                        // a protocol is given
        {
            // the baseUrl contains the server name only without the port
            baseUrl = url.substring (0, i);
        } // if not the default protocol is used and a protocol is given

        // reinitialize the temporary counter
        i = 0;

        if (baseUrl.indexOf (IOConstants.URL_HTTP) > -1)
                                        // check if a http protocol
                                        // is included in the url
        {
            i = IOConstants.URL_HTTP.length ();
        } // if http prefix was given
        else if (baseUrl.indexOf (IOConstants.URL_HTTPS) > -1)
                                        // check if a https protocol
                                        // is included in the url
        {
            i = IOConstants.URL_HTTPS.length ();
        } // else if https prefix was given

        // the url does not include any protocol anymore
        baseUrl = baseUrl.substring (i);

        // loop through all the entries and find the appropriate server tuple
        for (int j = 0; j < this.servers.size (); j++)
        {
            if (this.servers.elementAt (j) != null)
                                        // if an entry has been made at this position
            {
                // get the actual record according to the index
                tmpServerRecord = this.servers.elementAt (j);

                // open for all urls?
                if (tmpServerRecord.p_applicationServer != null &&
                    tmpServerRecord.p_applicationServer.equals (ConfigurationServer.ALLSERVERS))
                {
                    // set the baseurl in the serverrecord
                    tmpServerRecord.p_applicationServer = baseUrl;
                    // return the record searched
                    return tmpServerRecord;
                } // if (tmpServerRecord.p_applicationServer.equals (ALLSERVERS))

                // does the baseUrl match the configuration?
                if (((tmpServerRecord.p_applicationServer != null) &&
                     (baseUrl.equalsIgnoreCase (tmpServerRecord.p_applicationServer))) ||
                    ((tmpServerRecord.p_sslServer != null) &&
                    (baseUrl.equalsIgnoreCase (tmpServerRecord.p_sslServer))))
                                        // sslservername or applicationservername is
                                        // equal to the name in the header request given
                                        // with the parameter
                {
                    // return the record searched
                    return tmpServerRecord;
                } // if sslservername or applicationservername is equal to the name in the header request
            } // if vectorelement is not null
        } // for

        // if no record could be found an exception is raised.
        throw new ServerRequestNotAllowedException (MultilingualTextProvider.getMessage (
            UtilExceptions.EXC_BUNDLE, UtilExceptions.ML_E_SERVERREQUESTNOTALLOWED, env));
    } // getServerRecord


    /**************************************************************************
     * Checks if the given port is a p_ssl port or not.
     *
     * @param port the port to check.
     *
     * @return true if the port is an p_ssl port, else false
     */
    public boolean isSslServerPort (int port)
    {
        // traverse the vector
        for (int i = 0; i < this.servers.size (); i++)
        {
            // if the port of the parameter is the same of the p_ssl server
            if (null != this.servers.elementAt (i) &&
                this.servers.elementAt (i).p_sslServerPort == port)
            {
                return true;
            } // if
        } // for i

        // if the port is not a secure port
        return false;
    } // isSslServerPort


    /**************************************************************************
     * returns the number of entries in the vector.
     *
     * @return  the number of entries
     */
    public int size ()
    {
        // returns the size of the vector of server entries
        return this.servers.size ();
    } // size


    /**************************************************************************
     * resets the configuration server vector
     */
    public void resetServers ()
    {
        // the vector is newly initialized
        this.servers = new Vector<ServerRecord> ();
    } // resetServers

} // class ConfigurationServer
