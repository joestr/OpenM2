/*
 * Class: Authentication.java
 */

// package:
package ibs.util.trace;

// imports:
import ibs.util.trace.TracerConstants;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/******************************************************************************
 * This class implements the protocol for the authentication of the
 * trace-client against the trace-server.
 *
 * @version     $Id: Authentication.java,v 1.6 2007/07/10 09:16:40 kreimueller Exp $
 *
 * @author      Bernd Martin (BM), 001120
 ******************************************************************************
 */
public class Authentication extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Authentication.java,v 1.6 2007/07/10 09:16:40 kreimueller Exp $";


    /**
     * The actual version of the protocol. <BR/>
     */
    private byte[] actVersion = null;

    /**
     * ???
     */
    private int randomByteNumber;

    /**
     * ???
     */
    private int returnByteNumber;

    /**
     * ???
     */
    private OutputStream streamOut = null;

    /**
     * ???
     */
    private InputStream streamIn = null;

    /**
     * ???
     */
    private String hashalg = TracerConstants.HASHALG;

    /**
     * Answerstrings to the client if he is accepted or not. <BR/>
     */
    protected final String AUTHENTICATED = "0";

    /**
     * ???
     */
    protected final String NOTAUTHENTICATED = "1";



    /**************************************************************************
     * The constructor of this class. It sets the number of bytes needed to
     * transfer depending on the algorithm. <BR/>
     *
     * @param       s           the socket for the input and output stream
     *
     * @throws  IOException
     *          throws the excpetion if no stream can be retrieved
     */
    public Authentication (Socket s) throws IOException
    {
        this.actVersion = TracerConstants.VERSION10.getBytes ();
        this.streamOut = s.getOutputStream ();
        this.streamIn  = s.getInputStream ();

        if (this.hashalg.equals ("MD5"))     // if Hashalgorithm = MD5
        {
            this.returnByteNumber = 16;
        } // if Hashalg = MD5
        else if (this.hashalg.equals ("SHA-1"))
                                        // if Hashalgorithm = SHA1
        {
            this.returnByteNumber = 20;
        } // else if Hashalg = SHA-1

        // the size of the random bytes which are sent to the client
        // to compute the hashfunction
        this.randomByteNumber = 50;
    } // Authentication


    /**************************************************************************
     * The server wants to authenticate the client. <BR/>
     *
     * @param   version The version of the client.
     * @param   pw      The password for computing the hash (MAC)
     *
     * @return  <CODE>true</CODE> if the authorization process ends
     *          successfully, <CODE>false</CODE> otherwise.
     *
     * @throws  IOException
     *          An exception occurred during reading or writing data.
     * @throws  NoSuchAlgorithmException
     *          The necessary algorithm is not available in the caller's
     *          environment.
     */
    protected boolean authorizeClient (String version, byte[] pw)
        throws IOException, NoSuchAlgorithmException
    {
        // define the variables needed for the communication
        byte[] readBytes = new byte[this.returnByteNumber];
        byte[] randomBytes = new byte [this.randomByteNumber];
        byte[] myHash = new byte [this.returnByteNumber];
        SecureRandom rand = null;

        boolean retVal = false;
        int count = 0;
        String sMyHash;
        String ret = "";

        // send the protocol version first
        this.streamOut.write (version.getBytes ());
        this.streamOut.flush ();

        // then get a random number and send it
        // for computing the hashvalue
        rand = new SecureRandom ();
        rand.nextBytes (randomBytes);
        this.streamOut.write (randomBytes);
        this.streamOut.flush ();

        // retrieve the computet hash value of the client
        count = this.streamIn.read (readBytes);

        if (count > 0)              // some bytes received
        {
            // calculate my own hash with the same parameters for
            // evaluating if the client can be authorized or not
            myHash = this.computeHash (this.hashalg, randomBytes, pw);

            sMyHash = new String (myHash);
            ret = "";

            // compare the two hashvalues for equality
            if (sMyHash.equals (new String (readBytes)))
                                        // if the hashvalues are equal
            {
                // sets the last message for the client that he is authorized
                ret = this.AUTHENTICATED;
                // sets the returnvalue of the method that the authorization
                // process succeeded
                retVal = true;
            } // if the hashvalues are equal
            else                        // the authorization process failed
            {
                // sets the last message for the client that he is not authorized
                ret = this.NOTAUTHENTICATED;
                // sets the returnvalue of the method that the authorization
                // process failed
                retVal = false;
            } // the authorization process failed

            // write the returnvalue if the authorization was successfull or not
            this.streamOut.write (ret.getBytes ());
            this.streamOut.flush ();
        } // if some bytes received

        return retVal;
    } // authorizeClient


    /**************************************************************************
     * The client authenticates himself against the server. <BR/>
     *
     * @param   pw      The password for computing the hash (MAC)
     *
     * @return  <CODE>true</CODE> if the authorization process ends
     *          successfully, <CODE>false</CODE> otherwise.
     *
     * @throws  IOException
     *          An exception occurred during reading or writing data.
     * @throws  NoSuchAlgorithmException
     *          The necessary algorithm is not available in the caller's
     *          environment.
     */
    public boolean authorizeMe (byte[] pw)
        throws IOException, NoSuchAlgorithmException
    {
        // initializes the variables for the communication
        byte[] randomBytes = new byte [this.randomByteNumber];
        byte[] returnBytes = new byte [this.returnByteNumber];
        byte[] ok = new byte [1];
        byte[] actV = new byte [this.actVersion.length];

        boolean retVal = false;
        int count = 0;

        // read characters from the stream:
        count = this.streamIn.read (actV);

        // check if there was something read:
        if (count > 0) // read at least one byte?
        {
            // convert the buffer to string representation for the actual
            // version
            String sActV = new String (actV);

            if (sActV.equals (TracerConstants.VERSION10))
            {
                // read random number string to compute hash
                count = this.streamIn.read (randomBytes);

                if (count > 0) // at least one byte was read
                {
                    // write hashvalue to server
                    returnBytes = this.computeHash (this.hashalg, randomBytes, pw);
                    this.streamOut.write (returnBytes);
                    this.streamOut.flush ();

                    // read final answer: ok or not ok
                    count = this.streamIn.read (ok);

                    if (count > 0) // if at least one byte is read
                    {
                        String tmp = new String (ok);

                        if (tmp.equals (this.AUTHENTICATED))
                        // if answer is 0 and client is authenticated
                        {
                            retVal = true;
                        } // if answer is 0
                        else if (tmp.equals (this.NOTAUTHENTICATED))
                        // if answer is 1 and client is not authenticated
                        {
                            retVal = false;
                        } // else answer is 1
                    } // if at least one byte was read
                } // if at least one byte was read
                else
                // no random number given
                {

                    retVal = false;
                } // else no random number given
            } // if protocol is V1.0
            else
            // no supported protocol
            {
                retVal = false;
                // this.caller.showMessage ("Authorization failed - Protocol not
                // supported!\n");
            } // else no supported protocol
        } // if read at least one byte

        return retVal;
    } // authorizeMe


    /***************************************************************************
     * Computes the Hashvalue. <BR/>
     *
     * @param alg ???
     * @param b ???
     * @param pw ???
     *
     * @return The hash value.
     *
     * @throws NoSuchAlgorithmException The necessary algorithm is not available
     *             in the caller's environment.
     */
    protected byte[] computeHash (String alg, byte[] b, byte[] pw)
        throws NoSuchAlgorithmException
    {
        byte[] hash = null;

        // get a MessageDigest Object with the Algorithm alg
        // MD5 (Output 128 bit) and SHA-1 (Output 160 bit)
        // are provided without integrating an extra provider
        MessageDigest md = MessageDigest.getInstance (alg);

        // calculate hash-value hash of the byte-value of the message
        md.update (b);
        // update the hashvalue with the password
        md.update (pw);
        // finally compute the message digest
        hash = md.digest ();

        // return the message digest
        return hash;
    } // computeHash

} // class Authentication
