/*
 * Class: NTLMServer
 */

// package:
package ibs.tech.ntlm;

// imports:
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/******************************************************************************
 * This class implements the methods to negotiate NTLM authentication for
 * the server side. <BR/>
 *
 * @version     $Id: NTLMServer.java,v 1.5 2007/07/23 08:17:33 kreimueller Exp $
 *
 * @author      Bernd Buchegger (BB) 031118
 ******************************************************************************
 */
public class NTLMServer extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: NTLMServer.java,v 1.5 2007/07/23 08:17:33 kreimueller Exp $";

    /**
     * Authentication header. <BR/>
     */
    private static final String AUTH_HEADER = "WWW-Authenticate";
    /**
     * Authentication prefix for NTLM authentication. <BR/>
     */
    protected static final String AUTH_NTLM_PREFIX = "NTLM ";


    /**************************************************************************
     * Constructor for NTLMServer. <BR/>
     */
    public NTLMServer ()
    {
        // nothing to do
    } // NTLMServer


    /**************************************************************************
     * Performs an NTLM negotiation with a client. <BR/>
     *
     * @param   request     The request.
     * @param   response    The response.
     *
     * @return    the "domain\\username" string passed via ntlm authentication
     *            or <CODE>null</CODE>otherwise
     */
    public static String negotiateNTLM (HttpServletRequest request,
                                        HttpServletResponse response)
    {
        String resultUsername = null;

        try
        {
            // get the authorization setting from the clients request
            String auth = request.getHeader ("Authorization");
            // check if client send an authorization response
            if (auth == null)
            {
                // send NTLM authentication response:
                //     401 Unauthorized
                //     WWW-Authenticate: NTLM
                response.setStatus (HttpServletResponse.SC_UNAUTHORIZED);
                response.setHeader (NTLMServer.AUTH_HEADER, "NTLM");
                response.flushBuffer ();
            } // if (auth == null)
            // did the client respond with an NTLM authentication message?
            else if (auth.startsWith (NTLMServer.AUTH_NTLM_PREFIX))
            {
                byte[] msg = new sun.misc.BASE64Decoder ().decodeBuffer (auth.substring (5));
                int off = 0;
                int length = 0;
                int offset = 0;
                // did we get type1 or type3 message from client
                if (msg[8] == 1)
                {
//write ("got base64-encoded type-1-message: " + auth);
                    // we received from the client:
                    //  GET ...
                    //  Authorization: NTLM <base64-encoded type-1-message>

                    // send:
                    //   401 Unauthorized
                    //   WWW-Authenticate: NTLM <base64-encoded type-2-message>
                    byte z = 0;
                    byte[] msg1 = {
                        (byte) 'N', (byte) 'T', (byte) 'L', (byte) 'M',
                        (byte) 'S', (byte) 'S', (byte) 'P',
                        z, (byte)2, z, z, z, z, z, z, z, (byte)40, z, z, z,
                        (byte)1, (byte)130, z, z, z, (byte)2, (byte)2,
                        (byte)2, z, z, z, z, z, z, z, z, z, z, z, z,
                    };
                    response.setHeader (NTLMServer.AUTH_HEADER,
                        NTLMServer.AUTH_NTLM_PREFIX +
                        new sun.misc.BASE64Encoder ().encodeBuffer (msg1).trim ());
                    response.sendError (HttpServletResponse.SC_UNAUTHORIZED);
                } // if (msg[8] == 1)
                else if (msg[8] == 3)
                {
                    // we receiveed:
                    //   GET ...
                    //   Authorization: NTLM <base64-encoded type-3-message>
                    // --> NTLM negotation is finished!
                    off = 30;
/*KR HINT: never used
                    // extract the remoteHost
                    length = msg[off + 17] * 256 + msg[off + 16];
                    offset = msg[off + 19] * 256 + msg[off + 18];
                    String remoteHost = new String (msg, offset, length);
*/
                    // extract the domain:
                    length = msg[off + 1] * 256 + msg[off];
                    offset = msg[off + 3] * 256 + msg[off + 2];
                    String domain = new String (msg, offset, length);
                    // extract the username
                    length = msg[off + 9] * 256 + msg[off + 8];
                    offset = msg[off + 11] * 256 + msg[off + 10];
                    String username = new String (msg, offset, length);
                    // the resulting username includes the domain!
                    resultUsername = domain + "\\" + username;
                } // else if (msg[8] == 3)
            } // if
            else    // authorisation type not supported
            {
                // send: 401 Unauthorized
                response.sendError (HttpServletResponse.SC_UNAUTHORIZED);
            } // if (auth == null)
        } // try
        catch (IOException e)
        {
            return resultUsername;
        } // catch (IOException e)
        // return the result
        return resultUsername;
    } // negotiateNTLM


} // class NTLMServer
