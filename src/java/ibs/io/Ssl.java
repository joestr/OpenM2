/*
 * Class: Ssl.java
 */

// package:
package ibs.io;

// imports:
import ibs.BaseObject;
import ibs.io.Environment;
import ibs.io.IOConstants;
import ibs.io.NoSslAvailableException;
import ibs.io.SslRequiredException;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
//TODO: unsauber
import ibs.service.conf.Configuration;
//TODO: unsauber
import ibs.service.conf.ServerRecord;
//TODO: unsauber
import ibs.ml.MultilingualTextProvider;
import ibs.obj.user.User_01;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;
import ibs.util.UtilExceptions;

import java.io.IOException;
import java.net.Socket;


/******************************************************************************
 * This class contains some methods for the SSL functionality. <BR/>
 *
 * @version     $Id: Ssl.java,v 1.13 2010/04/07 13:37:10 rburgermann Exp $
 *
 * @author      Bernd Martin (BM), 001002
 ******************************************************************************
 */
public abstract class Ssl extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Ssl.java,v 1.13 2010/04/07 13:37:10 rburgermann Exp $";


    /**************************************************************************
     * Gets a secure-URL-String out of a non-secure one or from a relative
     * String too, eg. gets
     * 'https://www.myserver.com:443/dir/file.htm' out of
     * 'http://www.myserver.com:80/dir/file.htm' but also from
     * '/dir/file.htm'. If the URL was already a secure one
     * nothing will be changed. <BR/>
     *
     * @param  nonSecureUrl Contains the string which represents an
     *                      absolute or relative non-secure URL.
     * @param  sess         The actual SessionInfo with the
     *                      parameters set of the configuration file.
     *
     * @return      The string representing the secure-URL.
     */
    public static String getSecureUrl (String nonSecureUrl,
                                       SessionInfo sess)
    {
        String secureServer = ((ServerRecord) sess.actServerConfiguration).getSslServer ();
        int secureServerPort = ((ServerRecord) sess.actServerConfiguration).getSslServerPort ();
        String server = "";

        if (secureServer != null && secureServer.length () > 0)
        {
            server = IOConstants.URL_HTTPS +
                     secureServer +
                     ":" +
                     secureServerPort;
        } // if

        String secureUrl = nonSecureUrl;

        // check if a protocol is already used. if not, the input
        // parameter nonSecureUrl is meant to be a relative path
        // so the returnvalue is the secure-url-prefix (protocol +
        // servername + port) concatenated with the relative path
        // given as the input
        if ((nonSecureUrl.indexOf (IOConstants.URL_HTTPS) < 0) &&
            (nonSecureUrl.indexOf (IOConstants.URL_HTTP) < 0))
                                        // no protocol was given?
        {
                                        // concatenate a secure URL
            secureUrl = server + nonSecureUrl;
        } // if no protocol was given
        // http-prefix is given, that means that the protocol, servername
        // and portnumber has to be replaced with the secure ones.
        // if the prefix is already https then no changes will be done.
        else                            // some protocol was given
        {
            // if the given url is a non-secure Url-string.
            if (nonSecureUrl.indexOf (IOConstants.URL_HTTP) > -1)
                                        // http-protocol given?
            {
                // pos is the position after the string :PortNo occurs the first time.
                // then the base-url is replaced with the secure url protocol,
                // server and portnumber.
                int pos = nonSecureUrl.indexOf (":" + ((ServerRecord) sess.actServerConfiguration).getApplicationServerPort ()) +
                          Integer.toString (((ServerRecord) sess.actServerConfiguration).getApplicationServerPort ()).length () + 1;

                // secureUrl contains now the substring after the portnumber in the url
                secureUrl = secureUrl.substring (pos);

                // secureUrl is concatenated with the https-protocol-prefix, the sslserver-name
                // and the sslserver-portnumber
                secureUrl = server + secureUrl;
            } // if http-prefix was given
        } // else some protocol was given

        return secureUrl;
    } // getSecureUrl


    /**************************************************************************
     * Gets a non-secure-URL-String out of a secure one or a relative String
     * only too, eg. gets
     * 'http://www.myserver.com:80/dir/file.htm' out of
     * 'https://www.myserver.com:443/dir/file.htm' but also from
     * '/dir/file.htm'. <BR/>
     *
     * @param   secureUrl Contains the absolute or relative secure
     *                    url to be changed.
     * @param   sess      The actual SessionInfo.
     *
     * @return  The string representing the non-secure URL.
     */
    public static String getNonSecureUrl (String secureUrl,
                                          SessionInfo sess)
    {
        String applicationServer = ((ServerRecord) sess.actServerConfiguration).getApplicationServer ();
        int applicationServerPort = ((ServerRecord) sess.actServerConfiguration).getApplicationServerPort ();
        String server = "";

        if (applicationServer != null && applicationServer.length () > 0)
        {
            server = IOConstants.URL_HTTP +
                applicationServer + ":" + applicationServerPort;
        } // if

        String nonSecureUrl = secureUrl;

        // check if a protocol is already used within the url.
        // if not, the input parameter nonSecureUrl is meant to
        // be a relative path so the returnvalue is the
        // nonsecure-url-prefix (protocol +
        // servername + port) concatenated with the relative path
        // given as the input
        if ((nonSecureUrl.indexOf (IOConstants.URL_HTTPS) < 0) &&
            (nonSecureUrl.indexOf (IOConstants.URL_HTTP) < 0))
                                        // no protocol was given?
        {
            nonSecureUrl = server + secureUrl;
        } // if no protocol was given
        else                            // some protocol was given
        {
            // if the given Url-string is a secure one.
            if (nonSecureUrl.indexOf (IOConstants.URL_HTTPS) > -1)
            {

                // pos is the position after the string :PortNo occurs the first time.
                // then the base-url is replaced with the nonsecure url protocol,
                // server and portnumber.
                int pos = secureUrl.indexOf (":" + ((ServerRecord) sess.actServerConfiguration).getSslServerPort ()) +
                          Integer.toString (((ServerRecord) sess.actServerConfiguration).getSslServerPort ()).length () + 1;

                // nonSecureUrl contains now the string after the portnumber found
                // in the given Url-string
                nonSecureUrl = nonSecureUrl.substring (pos);

                // nonSecureUrl is concatenated with the http-protocol-prefix,
                // the applicationserver-name and applicationserver-portnumber
                nonSecureUrl = server + nonSecureUrl;
            } // if httpsprefix is found
        } // else some protocol was given

        return nonSecureUrl;
    } // getNonSecureUrl


    /**************************************************************************
     * Checks if the last request was done in secure mode or not. Therefore
     * the server-variable HTTPS has to be checked but also the port. Apache
     * doesn't set the server-variable properly if SSL is installed on another
     * port than the default one. In this case the port where the last request
     * was sent and the port given in the configuration file with sslserverport
     * are compared and if equal the method returns true. <BR/>
     *
     * @param   env     The actual environment to get some server variables.
     * @param   app     The actual applicationInfo object to get the
     *                  configuration. Needed for the Apache special case.
     *
     * @return  <CODE>true</CODE> or <CODE>false</CODE>,
     *          reqresenting if the request was sent in secure
     *          (with SSL) or insecure mode (without SSL).
     */
    public static boolean isHttpsRequest (Environment env,
                                          ApplicationInfo app)
    {
        String requestUrl = "";
        int pos = -1;
        int port = 0;

        // read the server-variable HTTPS and compare it with the secure-protocol constant:
        boolean isHttps =
                env.getServerVariable (IOConstants.SV_HTTPS)
                   .equalsIgnoreCase (IOConstants.PROT_HTTPS);

        // if no secure-protocol is returned we have to compare the actual portnumber
        // with the portnumber given in the configuration-file for the sslserverport.
        // if those values are the same, it is assumed that a ssl-request was given.
        if (!isHttps)                   // the server variable was not set yet,
            // then check the port.
        {
            // if the https-servervariable is not set then it is checked if the
            // portnumber on which the request was given is equal to the given
            // sslserver-portnumber
            requestUrl = env.getHttpHeaderVariable (IOConstants.HV_HOST);
            pos = requestUrl.indexOf (":");

            if (pos > 0)
            {
                port = Integer.parseInt (requestUrl.substring (pos + 1));
                if (app.configuration != null &&
                    ((Configuration) app.configuration).getConfigurationServers () != null &&
                    ((Configuration) app.configuration).getConfigurationServers ().isSslServerPort (port))
                {
                    isHttps = true;
                } // if
            } // if SV_SERVER_PORT = sslServerPort
        } // if the server variable was not set yet

        return isHttps;
    } // isHttpsRequest


    /*************************************************************************
     * This method should be called to check if the security-condition
     * is satisfied. If SSL must be used we have to check the last request.
     * Wasn't it done in a secure mode or is SSL not available but SSL is
     * required then an exception is raised. <BR/>
     *
     * @param   user    The user object to get the actual configuration.
     * @param   env     The actual environment.
     * @param   sess    The actual session object.
     *
     * @throws  SslRequiredException
     *          The exception is thrown if the request was made with http
     *          but SSL must be used.
     */
    public static void satisfySecurityCondition (User_01 user,
                                                 Environment env,
                                                 SessionInfo sess)
        throws SslRequiredException
    {
        // checks if SSL is not available for m2 or the request was not given in
        // the secure mode and SSL is required in the actual situation
        if ((!((ServerRecord) user.sess.actServerConfiguration).getSsl () ||
             !Ssl.isHttpsRequest (env, user.app)) &&
            Ssl.isSslRequired (user.sess))
            // if no SSL used or usable but required
        {
            // to give an appropriate error-message it is checked again
            // if the obligation to use SSL was given by the user- or
            // -requrirements
            if (sess.sslRequiredUser)
                // user must use SSL
            {
                throw new SslRequiredException (MultilingualTextProvider.getMessage (
                    UtilExceptions.EXC_BUNDLE, UtilExceptions.ML_E_SSLMUSTBEUSEDUSER,
                    new String[] {user.userData.actUsername}, env));
            } // if user must use SSL
            else if (sess.sslRequiredDomain)
                // domain requires SSL
            {
                throw new SslRequiredException (MultilingualTextProvider.getMessage (
                    UtilExceptions.EXC_BUNDLE, UtilExceptions.ML_E_SSLMUSTBEUSEDDOMAIN,
                    new String[] {user.userData.domainName}, env));
            } // else if domain requires SSL
        } // if no SSL used or usable but required
    } // satisfySecurityCondition


    /*************************************************************************
     * Check if the SSL Server with Port is available for m2. Therefore
     * it is tried to open a socket on the host given with the specified port.
     * if this try is successfull it is assumed that ssl is available on the
     * webserver. <BR/>
     *
     * @param   host Hostname of the Server
     * @param   port Portnumber of the Server
     *
     * @return  true if SSL seems to be available
     *
     * @exception NoSslAvailableException
     *          If there is no SSL-server (with the given name and port)
     *          availabe the exception is thrown
     */
    public static boolean isSslAvailableOnServer (String host, int port)
        throws NoSslAvailableException
    {
        Socket s = null;

        // try to make a socket on the server and port given. if
        // this fails an exception is raised that ssl is not available.
        try
        {
            s = new Socket (host, port);
            s.toString ();
        } // try to make a socket
        catch (IOException ioe)         // no socket
        {
            // TODO RB: Call  
            //          MultilingualTextProvider.getMessage (UtilExceptions.EXC_BUNDLE,
            //              UtilExceptions.ML_E_SSLWRONGCONFIGURED, env)
            //          to get the text in the correct language
            throw new NoSslAvailableException (UtilExceptions.ML_E_SSLWRONGCONFIGURED
                + " " + ioe.getMessage () + ". ");
        } // catch if no socket was possible

        return true;
    } // isSslAvailableOnServer


    /*************************************************************************
     * If an SSL-Request has been done and no SSL is installed then
     * an exception is raised. Otherwise nothing happens. This has to be done
     * when a non-secure request is done to a domain where SSL must
     * be used. <BR/>
     *
     * @param   sess    The actual session info.
     * @param   app     The gloabl application info.
     * @param   env     The actual environment.
     *
     * @throws  NoSslAvailableException
     *          If there was an <CODE>https</CODE>-request and
     *          no SSL is available in m2 the exception is thrown.
     */

    public static void checkSslCondition (SessionInfo sess,
                                          ApplicationInfo app,
                                          Environment env)
        throws NoSslAvailableException
    {
        // the previous request was given in secure-mode but no SSL
        // is available in m2
        if (Ssl.isHttpsRequest (env, app) &&
            !((ServerRecord) sess.actServerConfiguration).getSsl ())
                                        // https request but no ssl available?
        {
            throw new NoSslAvailableException (MultilingualTextProvider.getMessage (
                UtilExceptions.EXC_BUNDLE, UtilExceptions.ML_E_SSLISNOTAVAILABLE, env));
        } // if https request but no ssl available
    } // checkSslCondition


    /*************************************************************************
     * This method proofs if SSL must be used
     *  - for the actual domain
     *  - for the actual user. <BR/>
     *
     * @param   sess    The actual session info.
     *
     * @return  <CODE>true</CODE> if SSL is required,
     *          <CODE>false</CODE> otherwise.
     */
    public static boolean isSslRequired (SessionInfo sess)
    {
        boolean ssl = false;            // initialize the result

        // checks the flags for SSL for the actual domain and
        // for the actual user
        if ((sess.sslRequiredDomain) ||
            (sess.sslRequiredUser))
        {
            ssl = true;
        } // if sslRequired

        return ssl;                     // return the result
    } // isSslRequired


    /**************************************************************************
     * Check if SSL is required for the current session. <BR/>
     * This method calls {@link ibs.io.Ssl#isSslRequired isSslRequired} and
     * then checks the actual server configuration for additional SSL
     * information.
     *
     * @param   sess    The object representing the actual session.
     *
     * @return  <CODE>true</CODE> if SSL is required,
     *          <CODE>false</CODE> otherwise.
     */
    public static boolean isSslRequired2 (SessionInfo sess)
    {
        // check if SSL is required and return the result:
        return Ssl.isSslRequired (sess) &&
               ((ServerRecord) sess.actServerConfiguration).getSsl ();
    } // isSslRequired2


    /*************************************************************************
     * This method proofs if SSL must be used by some 'special-users'.
     * Therefore (at the moment) their login names are proofed. <BR/>
     *
     * @param   name    Login name of the actual user
     * @param   sess    The actual session info to get the configuration.
     *
     * @return  <CODE>true</CODE> if SSL seems to be available,
     *          else <CODE>false</CODE>
     */
    public static boolean isAdminUser (String name,
                                       SessionInfo sess)
    {
        if (name.equalsIgnoreCase ("Admin") &&
            ((ServerRecord) sess.actServerConfiguration).getSsl ())
            // if user is Admin and ssl is available
        {
            return true;
        } // if user is Admin and ssl is available

        return false;
    } // isAdminUser

} // class Ssl
