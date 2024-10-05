/*
 * Class: NTLMClient
 */


// package:
package ibs.tech.ntlm;

// imports:
import ibs.util.Helpers;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.xerces.impl.dv.util.Base64;

import cryptix.jce.provider.CryptixCrypto;


// BB 040712 Use the encode algorithm of the apache class because there are some
// problems with NTLM authentification.
//import ibs.util.Base64;
/******************************************************************************
 * This class implements the methods to negotiate NTLM authentication for
 * the client side. <BR/>
 *
 * @version     $Id: NTLMClient.java,v 1.7 2007/10/05 12:58:16 bbuchegger Exp $
 *
 * @author      Bernd Buchegger (BB) 031118
 ******************************************************************************
 */
public class NTLMClient extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: NTLMClient.java,v 1.7 2007/10/05 12:58:16 bbuchegger Exp $";

    /**
     * Request property: authorization. <BR/>
     */
    private static final String REQ_AUTHORIZATION = "Authorization";
    /**
     * Request property: content length. <BR/>
     */
    private static final String REQ_CONTENTLENGTH = "Content-length";


    /**************************************************************************
     * Constructor for NTLMClient. <BR/>
     */
    public NTLMClient ()
    {
        // nothing to do
    } // NTLMClient


    /**************************************************************************
     * Negotiate NTLM with an server. <BR/>
     *
     * @param url       the url to connect to
     * @param domain    the domain used for the NTLM authentication
     * @param username  the username used for the NTLM authentication
     * @param password  the password used for the NTLM authentication
     * @param payload   the payload to send to the server (otpional)
     *
     * @return the HTTPUrlConnection object used to negotiate the connection
     *
     * @throws  NTLMException
     *          An error occurred during the negotiation process.
     */
    public static HttpURLConnection negotiateNTLM (URL url, String domain,
            String username, String password, String payload)
        throws NTLMException
    {

        HttpURLConnection connection;
        String ntlmResponseStr = "";
        String ntlmAuthorizationStr = "";
        DataOutputStream outputStream = null;
        InputStream responseStream = null;
        String host = "";
        String response;

        try
        {
            // register the cryptix security provider that supports MD4
            // which is used to calculate the 3th message for NTLM
            java.security.Security.addProvider (new CryptixCrypto ());
            // construct the NTLM string
            ntlmAuthorizationStr = NTLMServer.AUTH_NTLM_PREFIX + new String (
                Base64.encode (NTLM.formatRequest (host, domain)));
            // open the connection
            connection = (HttpURLConnection) url.openConnection ();
            connection.setDoInput (true);
            connection.setDoOutput (true);
            connection.setAllowUserInteraction (false);
            // set headers
            connection.setRequestProperty (NTLMClient.REQ_AUTHORIZATION, ntlmAuthorizationStr);
            connection.setRequestProperty (NTLMClient.REQ_CONTENTLENGTH, "0");
            // open the connection and send the data
            connection.connect ();
/*
            // set an output stream to the connection we want to write the data to
            outputStream = new DataOutputStream (connection.getOutputStream ());
            // write the payload to the stream
            if (payload != null)
                outputStream.writeBytes (payload);
            // close the stream
            outputStream.close ();
*/
            // get the response from the server
            if (connection.getResponseCode () != HttpURLConnection.HTTP_UNAUTHORIZED)
            {
                responseStream = new BufferedInputStream (connection.getErrorStream ());
                // get the response from the connection
                // This must be done because of connection.setDoOutput (true);
                response = NTLMClient.readResponse (responseStream);
                // close the stream
                responseStream.close ();
                throw new NTLMException ("Error negotiating NTLM: " +
                    connection.getResponseCode () + " " +
                    connection.getResponseMessage () + " : " + response);
            } // if (connection.getResponseCode () != HttpURLConnection.HTTP_OK)

            // get the NTLM message the server returned
            ntlmResponseStr = connection.getHeaderField ("WWW-Authenticate");
            // check if we got a valid NTLM header field
            if (ntlmResponseStr != null && ntlmResponseStr.startsWith ("NTLM"))
            {
                connection = (HttpURLConnection) url.openConnection ();
                // set POST as HTTP request method in case a payload has been set
                if (payload == null)
                {
                    connection.setRequestMethod ("GET");
                } // if
                else
                {
                    connection.setRequestMethod ("POST");
                } // else
                // set the connection for input/output
                connection.setDoInput (true);
                connection.setDoOutput (true);
                connection.setAllowUserInteraction (false);
                // set header properties
                connection.setRequestProperty ("Content-type", "text/html");
                if (payload != null)
                {
                    connection.setRequestProperty (NTLMClient.REQ_CONTENTLENGTH, "" + payload.length ());
                } // if
                else
                {
                    connection.setRequestProperty (NTLMClient.REQ_CONTENTLENGTH, "0");
                } // else
                // set authorization information
                byte [] lmPassword = NTLM.computeLMPassword (password);
                byte [] ntPassword = NTLM.computeNTPassword (password);
                byte [] nonce = NTLM.getNonce (Base64.decode (ntlmResponseStr.substring (5)));
                ntlmAuthorizationStr = NTLMServer.AUTH_NTLM_PREFIX +
                    new String (Base64.encode (NTLM.formatResponse (host,
                        username, domain, lmPassword, ntPassword, nonce)));
                connection.setRequestProperty (NTLMClient.REQ_AUTHORIZATION, ntlmAuthorizationStr);
                // open the connection and send the data
                connection.connect ();
                // write the payload to the stream in case it has been set
                if (payload != null)
                {
                    // set an output stream to the connection we want to write the data to
                    outputStream = new DataOutputStream (connection.getOutputStream ());
                    outputStream.writeBytes (payload);
                    // close the stream
                    outputStream.close ();
                } // if (payload != null)
                return connection;
            } // if (ntlmResponseStr != null && ntlmResponseStr.startsWith ("NTLM"))

            // something wrong with NTLM
            throw new NTLMException ("Server did not return valid NTLM response!");
        } // try
        catch (Exception e)
        {
            throw new NTLMException (Helpers.getStackTraceFromThrowable (e));
        } // catch (Exception e)
    } // negotiateNTLM


    /**************************************************************************
     * Reads an response from a stream. <BR/>
     *
     * @param   responseStream  the inputstream to read the response
     *
     * @return  the response string or null in case it could not have been read
     */
    protected static String readResponse (InputStream responseStream)
    {
        StringBuffer stringBuffer;
        String responseStr = null;
        int c;

        try
        {
            // check if we got an respone stream
            if (responseStream != null)
            {
                // first read the response into a string
                stringBuffer = new StringBuffer ();
                while ((c = responseStream.read ()) != -1)
                {
                    // append the character to the string buffer
                    stringBuffer.append ((char) c);
                } // while ((c = responseStream.read ()) != -1)
                // close the stream
                responseStream.close ();
                // generate the response string
                responseStr = stringBuffer.toString ();
            } // if (responseStream != null)
        } // try
        catch  (IOException e)
        {
            responseStr = null;
        } // catch
        // return the response string
        return responseStr;
    } // readResponse

} // class NTLMClient
