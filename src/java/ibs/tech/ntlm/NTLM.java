/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *    NTLM.java
 *    Copyright (C) 2002 Luigi Dragone
 *
 */

// package:
package ibs.tech.ntlm;

// imports:
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.DESKeySpec;


/******************************************************************************
 * <p>
 * This class implements the Microsoft NTLM authentication protocol.
 * </p>
 * <p>
 * NTLM is a Microsoft proprietary network authentication protocol used in many
 * situations (e.g. by the Microsoft Proxy Server to authenticate a browser).
 * </p>
 * <p>
 * It requires a JCE compatible MD4 hash algorithm implementation and
 * a DES with no-padding ECB cipher to compute the requested value. <BR/>
 * An open source JCE compatible library is Cryptix JCE and it is available
 * <a href="http://www.cryptix.org/">here</a>. We are assuming that the JCE provider
 * is correctly installed and configured. Notice that the Sun JCE implementation
 * proviedes the DES cipher but doesn't provide the MD4 hashing.
 * </p>
 * <p>
 * To perform an authentication the following information are needed:
 * <ul>
 * <li>the host name (with its own domain);</li>
 * <li>the user name (with its own domain);</li>
 * <li>the user password.</li>
 * </ul>
 * Alternatively, the user password can be replaced with its Lan Manager and
 * NT hashed versions. On a Windows system these data can be collected in the
 * registry, otherwise they can be extracted from a SAMBA password file. <BR/>
 * Notice that the host and user domain could not be the same.
 * </p>
 * <p>
 * To start an NTLM authentication procedure (e.g. with a proxy server) build a
 * request message calling {@link #formatRequest (String, String) formatRequest}
 * and send it to the server. <BR/>
 * Once the challenge packet has been correctly received extract from it the nonce
 * with {@link #getNonce (byte[]) getNonce} function and use it to compute the
 * reply and build the response message with
 * {@link #formatResponse (String, String, String, byte[], byte[], byte[]) formatResponse}
 * method and send it back to the server. <BR/>
 * Repeat the previous steps until the server authenticates the client's identity
 * or a large number of retries has been made. The check of a successful authentication
 * is protocol specific (e.g. code 200 in HTTP), thus it is not performed by this component.
 * </p>
 * <p>
 * We want to access to the page <code>http://www.server.com/page.html</code>
 * through an NTLM proxy <code>proxy.domain.com</code> that accepts connection on
 * port 80. <BR/>
 * We access to proxy from host <code>HOSTDOMAIN\\HOST</code> with the user
 * <code>USERDOMAIN\\user</code> (password <code>"1234567890"</code>). <BR/>
 * As first step we open a socket connection to proxy server and set up the
 * required object. Notice that we use a keep-alive connection, because NTLM
 * authentication is connection based and the connection must be alive through the
 * whole process. <BR/>
 * <pre>
 *     Socket s = new Socket ("proxy.domain.com", 80);
 *     s.setKeepAlive (true);
 *     InputStream is = s.getInputStream ();
 *     OutputStream os = s.getOutputStream ();
 *     BufferedReader r = new BufferedReader (new InputStreamReader (is));
 *     BufferedWriter w = new BufferedWriter (new OutputStreamWriter (os));
 *
 *     String host = "HOST";
 *     String hostDomain = "HOSTDOMAIN";
 *     String user = "user";
 *     String userDomain = "USERDOMAIN";
 *     String password = "1234567890";
 * </pre>
 * Then, we format a request message and send it in a HTTP compliant GET message. <BR/>
 * <pre>
 *     byte[] fstMsg = NTLM.formatRequest (host, hostDomain);
 *     byte[] fstMsg64 = Codecs.base64Encode (fstMsg);
 *     System.out.println ("NTLM Request Packet: " + new String (fstMsg64));
 *
 *     w.write ("GET http://www.server.com/page.html HTTP/1.0\n");
 *     w.write ("Host: www.server.com\n");
 *     w.write ("Proxy-Connection: Keep-Alive\n");
 *     w.write ("Proxy-Authorization: NTLM " + new String (fstMsg64) + "\n\n");
 *     w.flush ();
 *     System.out.println ("First Message Sent");
 * </pre>
 * We wait for the server response and we parse it to extract the nonce. <BR/>
 * <pre>
 *     String resp = "";
 *     int contentLength = 0;
 *     while ((line = r.readLine ()) != null)
 *       if (line.length () == 0)
 *         break;
 *       if (line.startsWith ("Content-Length"))
 *         contentLength = Integer.parseInt (line.substring (line.indexOf (":") + 1).trim ());
 *       else if (line.startsWith ("Proxy-Authenticate"))
 *         resp = line.substring (line.indexOf (":") + 1).trim ();
 *     r.skip (contentLength);
 *     System.out.println ("Second Message Received");
 *     System.out.println ("Content Length: " + contentLength);
 *     System.out.println ("Proxy-Authenticate: " + resp);
 *     resp = resp.substring (resp.indexOf (" ")).trim ();
 *     System.out.println ("NTLM Chellange Packet: " + resp);
 *     resp = Codecs.base64Decode (resp);
 *     byte[] sndMsg = resp.getBytes ();
 *     byte[] nonce = NTLM.getNonce (sndMsg);
 * </pre>
 * With the nonce collected in the previous step we create a response message. <BR/>
 * <pre>
 *     byte[] trdMsg = NTLM.formatResponse (host, user, userDomain,
 *         NTLM.computeLMPassword (password), NTLM.computeNTPassword (password),
 *         nonce);
 *     System.out.println (trdMsg.length);
 *     byte[] trdMsg64 = Codecs.base64Encode (trdMsg);
 *     System.out.println ("NTLM Response Packet: " + new String (trdMsg64));
 * </pre>
 * We sent the message to the server. <BR/>
 * <pre>
 *     w.write ("GET http://www.server.com/page.html HTTP/1.0\n");
 *     w.write ("Proxy-Connection: Keep-Alive\n");
 *     w.write ("Host: www.server.com\n");
 *     w.write ("Proxy-Authorization: NTLM " + new String (trdMsg64) + "\n\n");
 *     w.flush ();
 *     System.out.println ("Third Message Sent");
 * </pre>
 * Finally we wait the server reply. <BR/>
 * <pre>
 *     System.out.println ("Server response: " + r.readLine ());
 * </pre>
 * If the reply is like <code>"HTTP/1.0 200 OK"</code> it has worked fine, else
 * the server response is containing a new nonce.
 * </p>
 * <p>
 * Notice that despite the computing of hashed passwords and of nonce response is
 * exactly the same of the SMB authentication protocol, the message format is slightly
 * different. <BR/>
 * Therefore, the methods {@link #computeLMPassword (String) computeLMPassword},
 * {@link #computeNTPassword (String) computeNTPassword} and
 * {@link #computeNTLMResponse (byte[], byte[], byte[], byte[], byte[]) computeNTLMResponse}
 * can be used to perform a SMB authentication too.
 * </p>
 * <p>
 * This implementation is based on:
 * <ul>
 * <li>the reverse engineering of the NTLM protocol made by Ronald Tschal&auml;r
 * and available <a href="http://www.innovation.ch/java/ntlm.html">here</a>;</li>
 * <li>the documentation about NTLM provided with <a href="http://www.atstake.com/research/lc3/">
 * L0phtCrack 1.5</a>;</li>
 * <li>the &quot;Handbook of Applied Cryptography&quot;, freely available
 * <a href="http://www.cacr.math.uwaterloo.ca/hac/">here</a>;</li>
 * <li>the C source code of NTLM library in the <a href="http://www.samba.org/">
 * SAMBA Project</a>.</li>
 * </ul>
 * Nevertheless, because there isn't any official protocol specification publicly
 * available there is any warranty that code works correctly and that it is
 * conforming to Microsoft NTLM protocol.
 * </p>
 * <p>
 * For implementation reasons only the public members perform argument consistency
 * checks. The public members also catch and hide every exceptions that can be
 * throwed (even though interfaces specify otherwise).
 * </p>
 *
 * @author      Luigi Dragone (<a href="mailto:luigi@luigidragone.com">luigi@luigidragone.com</a>)
 *
 * @version     1.0.1
 * @version     $Id: NTLM.java,v 1.5 2007/07/23 08:17:33 kreimueller Exp $
 *
 * @see <a href="http://www.innovation.ch/java/ntlm.html">NTLM Authentication Scheme for HTTP</a>
 * @see <a href="ftp://ftp.samba.org/pub/samba/docs/textdocs/ENCRYPTION.txt">LanMan and NT Password Encryption in Samba 2.x</a>
 * @see <a href="http://www.cacr.math.uwaterloo.ca/hac/">&quot;Handbook of Applied Cryptography&quot;</a>
 * @see <a href="http://java.sun.com/products/jce/">JCE</a>
 * @see <a href="http://www.cryptix.org/">Cryptix</a>
 ******************************************************************************
 */
public class NTLM extends Object
{
    /**
     * Cipher name. <BR/>
     */
    private static final String CIPHER = "DES/ECB/NoPadding";

    /**
     * Error message: Null value for password. <BR/>
     */
    private static final String MSG_PASSWORD_NULL =
        "password: null value not allowed";
    /**
     * Error message: Illegal size for LM password. <BR/>
     */
    private static final String MSG_LMPASSWORD_SIZE =
        "lmPassword: illegal size";
    /**
     * Error message: Illegal size for NT password. <BR/>
     */
    private static final String MSG_NTPASSWORD_SIZE =
        "ntPassword: illegal size";
    /**
     * Error message: Illegal size for nonce. <BR/>
     */
    private static final String MSG_NONCE_SIZE = "nonce: illegal size";

    /**
     * NTLM stream start. <BR/>
     */
    private static final String NTLM_START = "NTLMSSP\0";



    /**************************************************************************
     * Creates a NTLM object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     *
     */
    protected NTLM ()
    {
        // nothing to do
    } // NTLM


    /**
     * The magic number used to compute the Lan Manager hashed password.
     */
    protected static final byte[] MAGIC = new byte[]
    {
        0x4B, 0x47, 0x53, 0x21, 0x40, 0x23, 0x24, 0x25,
    };


    /**************************************************************************
     * Converts an unsigned byte to an unsigned integer. <BR/>
     * Notice that Java bytes are always signed, but the cryptographic
     * algorithms rely on unsigned ones, that can be simulated in this way. <BR/>
     * A bit mask is employed to prevent that the signum bit is extended to
     * MSBs.
     *
     * @param   b       The byte to be converted.
     *
     * @return  The result of the conversion.
     */
    protected static int unsignedByteToInt (byte b)
    {
        return b & 0xFF;
    } // unsignedByteToInt


    /**************************************************************************
     * Get lower byte of character. <BR/>
     *
     * @param   c       The character.
     *
     * @return  The lower byte value of the character.
     */
    protected static byte getLoByte (char c)
    {
        return (byte) c;
    } // getLoByte


    /**************************************************************************
     * Get higher byte of character. <BR/>
     *
     * @param   c       The character.
     *
     * @return  The higher byte value of the character.
     */
    protected static byte getHiByte (char c)
    {
        return (byte) ((c >>> 8) & 0xFF);
    } // getHiByte


    /**************************************************************************
     * Swap bytes of a number. <BR/>
     *
     * @param   s       The number within which to swap the bytes.
     *
     * @return  The resulting number.
     */
    protected static short swapBytes (short s)
    {
        return (short) (((s << 8) & 0xFF00) | ((s >>> 8) & 0x00FF));
    } // swapBytes


    /**************************************************************************
     * <p>
     * Computes an odd DES key from 56 bits represented as a 7-bytes array.
     * </p>
     * <p>
     * Keeps elements from index <code>offset</code> to index
     * <code>offset + 7</code> of supplied array.
     * </p>
     *
     * @param keyData a byte array containing the 56 bits used to compute the
     *            DES key
     * @param offset the offset of the first element of the 56-bits key data
     *
     * @return the odd DES key generated
     *
     * @throws  InvalidKeyException
     *          The key was invalid.
     * @throws  NoSuchAlgorithmException
     *          The algorithm dows not exist.
     * @throws  InvalidKeySpecException
     *          The key specification was invalid.
     */
    protected static Key computeDESKey (byte[] keyData, int offset)
        throws InvalidKeyException, NoSuchAlgorithmException,
        InvalidKeySpecException
    {
        byte[] desKeyData = new byte[8];
        int[] k = new int[7];

        for (int i = 0; i < 7; i++)
        {
            k[i] = NTLM.unsignedByteToInt (keyData[offset + i]);
        } // for i

        desKeyData[0] = (byte) (k[0] >>> 1);
        desKeyData[1] = (byte) (((k[0] & 0x01) << 6) | (k[1] >>> 2));
        desKeyData[2] = (byte) (((k[1] & 0x03) << 5) | (k[2] >>> 3));
        desKeyData[3] = (byte) (((k[2] & 0x07) << 4) | (k[3] >>> 4));
        desKeyData[4] = (byte) (((k[3] & 0x0F) << 3) | (k[4] >>> 5));
        desKeyData[5] = (byte) (((k[4] & 0x1F) << 2) | (k[5] >>> 6));
        desKeyData[6] = (byte) (((k[5] & 0x3F) << 1) | (k[6] >>> 7));
        desKeyData[7] = (byte) (k[6] & 0x7F);

        for (int i = 0; i < 8; i++)
        {
            desKeyData[i] = (byte) (NTLM.unsignedByteToInt (desKeyData[i]) << 1);
        } // for i

        KeySpec desKeySpec = new DESKeySpec (desKeyData);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance ("DES");
        SecretKey secretKey = keyFactory.generateSecret (desKeySpec);
        return secretKey;
    } // computeDESKey


    /**************************************************************************
     * Encrypts the 8-bytes plain text three times with the 3 56-bits DES keys
     * and puts the result in a 24-bytes array.
     *
     * @param keys a 21-bytes array containing 3 56-bits DES keys
     * @param plaintext a 8-bytes array to be encrypted
     *
     * @return a 24-bytes array containing the plaintext DES encrypted with the
     *         supplied keys
     *
     * @throws  InvalidKeyException
     *          if the given key is inappropriate for initializing this cipher,
     *          or if this cipher is being initialized for decryption and
     *          requires algorithm parameters that cannot be determined from
     *          the given key, or if the given key has a keysize that exceeds
     *          the maximum allowable keysize (as determined from the
     *          configured jurisdiction policy files).
     * @throws  NoSuchAlgorithmException
     *          if transformation is null, empty, in an invalid format, or if no
     *          Provider supports a CipherSpi implementation for the specified
     *          algorithm.
     * @throws  javax.crypto.NoSuchPaddingException
     *          if transformation contains a padding scheme that is not available.
     * @throws  InvalidKeySpecException
     *          The key specification was invalid.
     * @throws  BadPaddingException
     *          if this cipher is in decryption mode, and (un)padding has been
     *          requested, but the decrypted data is not bounded by the
     *          appropriate padding bytes
     * @throws  IllegalBlockSizeException
     *          if this cipher is a block cipher, no padding has been requested
     *          (only in encryption mode), and the total input length of the
     *          data processed by this cipher is not a multiple of block size;
     *          or if this encryption algorithm is unable to process the input
     *          data provided.
     * @throws  ShortBufferException
     *          if the given output buffer is too small to hold the result
     */
    protected static byte[] encrypt (byte[] keys, byte[] plaintext)
        throws InvalidKeyException, NoSuchAlgorithmException,
        javax.crypto.NoSuchPaddingException, InvalidKeySpecException,
        BadPaddingException, IllegalBlockSizeException, ShortBufferException
    {
        byte[] ciphertext = new byte[24];
        Cipher c = Cipher.getInstance (NTLM.CIPHER);
        Key k = NTLM.computeDESKey (keys, 0);
        c.init (Cipher.ENCRYPT_MODE, k);
        c.doFinal (plaintext, 0, 8, ciphertext, 0);
        k = NTLM.computeDESKey (keys, 7);
        c.init (Cipher.ENCRYPT_MODE, k);
        c.doFinal (plaintext, 0, 8, ciphertext, 8);
        k = NTLM.computeDESKey (keys, 14);
        c.init (Cipher.ENCRYPT_MODE, k);
        c.doFinal (plaintext, 0, 8, ciphertext, 16);
        return ciphertext;
    } // encrypt


    /**************************************************************************
     * Computes the Lan Manager hashed version of a password.
     *
     * @param password the user password
     *
     * @return the Lan Manager hashed version of the password in a 16-bytes
     *         array
     *
     * @exception IllegalArgumentException if the supplied password is null
     * @exception javax.crypto.NoSuchPaddingException if there isn't any
     *                suitable padding method
     * @exception NoSuchAlgorithmException if there isn't any suitable cipher
     *                algorithm
     */
    public static byte[] computeLMPassword (String password)
        throws IllegalArgumentException, NoSuchPaddingException,
        NoSuchAlgorithmException
    {
        if (password == null)
        {
            throw new IllegalArgumentException (NTLM.MSG_PASSWORD_NULL);
        } // if
        try
        {
            // Gets the first 14-bytes of the ASCII upper cased password
            int len = password.length ();
            if (len > 14)
            {
                len = 14;
            } // if
            Cipher c = Cipher.getInstance (NTLM.CIPHER);

            byte[] lmPw = new byte[14];
            byte[] bytes = password.toUpperCase ().getBytes ();
            int i;
            for (i = 0; i < len; i++)
            {
                lmPw[i] = bytes[i];
            } // for i
            for (; i < 14; i++)
            {
                lmPw[i] = 0;
            } // for i

            byte[] lmHpw = new byte[16];
            // Builds a first DES key with its first 7 bytes
            Key k = NTLM.computeDESKey (lmPw, 0);
            c.init (Cipher.ENCRYPT_MODE, k);
            // Hashes the MAGIC number with this key into the first 8 bytes of
            // the result
            c.doFinal (NTLM.MAGIC, 0, 8, lmHpw, 0);

            // Repeats the work with the last 7 bytes to gets the last 8 bytes
            // of the result
            k = NTLM.computeDESKey (lmPw, 7);
            c.init (Cipher.ENCRYPT_MODE, k);
            c.doFinal (NTLM.MAGIC, 0, 8, lmHpw, 8);

            return lmHpw;
        } // try
        catch (InvalidKeySpecException ex)
        {
            return null;
        } // catch
        catch (InvalidKeyException ex)
        {
            return null;
        } // catch
        catch (BadPaddingException ex)
        {
            return null;
        } // catch
        catch (IllegalBlockSizeException ex)
        {
            return null;
        } // catch
        catch (ShortBufferException ex)
        {
            return null;
        } // catch
    } // computeLMPassword


    /**************************************************************************
     * Computes the NT hashed version of a password.
     *
     * @param password the user password
     *
     * @return the NT hashed version of the password in a 16-bytes array
     *
     * @exception IllegalArgumentException if the supplied password is null
     * @exception NoSuchAlgorithmException if there isn't any suitable cipher
     *                algorithm
     */
    public static byte[] computeNTPassword (String password)
        throws IllegalArgumentException, NoSuchAlgorithmException
    {
        if (password == null)
        {
            throw new IllegalArgumentException (NTLM.MSG_PASSWORD_NULL);
        } // if
        // Gets the first 14-bytes of the UNICODE password
        int len = password.length ();
        if (len > 14)
        {
            len = 14;
        } // if
        byte[] ntPw = new byte[2 * len];
        for (int i = 0; i < len; i++)
        {
            char ch = password.charAt (i);
            ntPw[2 * i] = NTLM.getLoByte (ch);
            ntPw[2 * i + 1] = NTLM.getHiByte (ch);
        } // for i

        // Return its MD4 digest as the hashed version
        MessageDigest md = MessageDigest.getInstance ("MD4");
        return md.digest (ntPw);
    } // computeNTPassword


    /**************************************************************************
     * <p>
     * Computes the NTLM response to the nonce based on the supplied hashed
     * passwords.
     * </p>
     * <p>
     * If the hashed password are not available they can be computed from the
     * cleartext password by the means of
     * {@link #computeLMPassword (String) computeLMPassword} and
     * {@link #computeNTPassword (String) computeNTPassword} methods.
     * </p>
     *
     * @param lmPassword a 16-bytes array containing the Lan Manager hashed
     *            password
     * @param ntPassword a 16-bytes array containing the Lan Manager hashed
     *            password
     * @param nonce a 8-bytes array representing the server's nonce
     * @param lmResponse a 24-bytes array that will contain the Lan Manager
     *            response after the method invocation
     * @param ntResponse a 24-bytes array that will contain the NT response
     *            after the method invocation
     *
     * @exception IllegalArgumentException if a parameter has an illegal size
     * @exception javax.crypto.NoSuchPaddingException if there isn't any
     *                suitable padding method
     * @exception NoSuchAlgorithmException if there isn't any suitable cipher
     *                algorithm
     */
    public static void computeNTLMResponse (byte[] lmPassword,
                                            byte[] ntPassword, byte[] nonce,
                                            byte[] lmResponse, byte[] ntResponse)
        throws IllegalArgumentException, NoSuchPaddingException,
        NoSuchAlgorithmException
    {
        if (lmPassword.length != 16)
        {
            throw new IllegalArgumentException (NTLM.MSG_LMPASSWORD_SIZE);
        } // if
        if (ntPassword.length != 16)
        {
            throw new IllegalArgumentException (NTLM.MSG_NTPASSWORD_SIZE);
        } // if
        if (nonce.length != 8)
        {
            throw new IllegalArgumentException (NTLM.MSG_NONCE_SIZE);
        } // if
        if (lmResponse.length != 24)
        {
            throw new IllegalArgumentException ("lmResponse : illegal size");
        } // if
        if (ntResponse.length != 24)
        {
            throw new IllegalArgumentException ("ntResponse : illegal size");
        } // if
        try
        {
            // Puts the hashed passwords into 21-bytes arrays with trailing 0s
            byte[] lmHPw = new byte[21];
            byte[] ntHPw = new byte[21];
            System.arraycopy (lmPassword, 0, lmHPw, 0, 16);
            System.arraycopy (ntPassword, 0, ntHPw, 0, 16);
            for (int i = 16; i < 21; i++)
            {
                lmHPw[i] = 0;
                ntHPw[i] = 0;
            } // for i
            // Encrypts the nonce with the padded hashed passwords to compute
            // the responses
            System.arraycopy (NTLM.encrypt (lmHPw, nonce), 0, lmResponse, 0, 24);
            System.arraycopy (NTLM.encrypt (ntHPw, nonce), 0, ntResponse, 0, 24);
        } // try
        catch (ShortBufferException ex)
        {
            // display error message:
            System.err.println (ex);
        } // catch
        catch (IllegalBlockSizeException ex)
        {
            // display error message:
            System.err.println (ex);
        } // catch
        catch (BadPaddingException ex)
        {
            // display error message:
            System.err.println (ex);
        } // catch
        catch (InvalidKeySpecException ex)
        {
            // display error message:
            System.err.println (ex);
        } // catch
        catch (InvalidKeyException ex)
        {
            // display error message:
            System.err.println (ex);
        } // catch
    } // computeNTLMResponse


    /**************************************************************************
     * <p>
     * Builds a request message for the host of the specified domain that can be
     * send to the server to start the NTLM protocol.
     * </p>
     * <p>
     * The returned message should be encoded according to protocol specific
     * rules (e.g. base 64 encoding). <BR/>
     * The message format is discussed <a
     * href="http://www.innovation.ch/java/ntlm.html">here</a>.
     * </p>
     *
     * @param host the name of the host that is authenticating
     * @param hostDomain the name of the domain to which the host belongs
     *
     * @return the request message to send to server to open an authentication
     *         procedure
     *
     * @exception IOException if an error occurs during the message formatting
     *
     * @see <a href="http://www.innovation.ch/java/ntlm.html">NTLM
     *      Authentication Scheme for HTTP</a>
     *
     */
    public static byte[] formatRequest (String host, String hostDomain)
        throws IOException
    {
        String hostDomainLocal = hostDomain.toUpperCase ();
        String hostLocal = host.toUpperCase ();
        short domainLen = (short) hostDomainLocal.length ();
        short hostLen = (short) hostLocal.length ();
        short hostOff = 0x20;
        short domainOff = (short) (hostOff + hostLen);
        ByteArrayOutputStream os = new ByteArrayOutputStream (1024);
        DataOutputStream dataOut = new DataOutputStream (os);
        dataOut.writeBytes (NTLM.NTLM_START);
        dataOut.writeByte (0x01);
        dataOut.writeByte (0x00);
        dataOut.writeByte (0x00);
        dataOut.writeByte (0x00);
        // dataOut.writeShort (swapBytes ((short)0xb203));
        dataOut.writeShort (NTLM.swapBytes ((short) 0xb207));
        dataOut.writeShort (0x0000);
        dataOut.writeShort (NTLM.swapBytes (domainLen));
        dataOut.writeShort (NTLM.swapBytes (domainLen));
        dataOut.writeShort (NTLM.swapBytes (domainOff));
        dataOut.writeShort (0x0000);
        dataOut.writeShort (NTLM.swapBytes (hostLen));
        dataOut.writeShort (NTLM.swapBytes (hostLen));
        dataOut.writeShort (NTLM.swapBytes (hostOff));
        dataOut.writeShort (0x0000);
        dataOut.write (hostLocal.getBytes ());
        dataOut.write (hostDomainLocal.getBytes ());
        dataOut.flush ();
        return os.toByteArray ();
    } // formatRequest


    /**************************************************************************
     * <p>
     * Extracts from the server challenge response the nonce required to perform
     * the authentication.
     * </p>
     * <p>
     * The received message should be decoded according to protocol specific
     * rules (e.g. base 64 encoding). <BR/>
     * The message format is discussed <a
     * href="http://www.innovation.ch/java/ntlm.html">here</a>.
     * </p>
     *
     * @param msg a byte array containing the server challenge message
     *
     * @return  The nonce.
     *
     * @exception IllegalArgumentException if a parameter has an illegal size
     *
     * @see <a href="http://www.innovation.ch/java/ntlm.html">NTLM
     *      Authentication Scheme for HTTP</a>
     */
    public static byte[] getNonce (byte[] msg) throws IllegalArgumentException
    {
        if (msg.length < 32)
        {
            throw new IllegalArgumentException ("msg : illegal size");
        } // if
        byte[] nonce = new byte[8];
        System.arraycopy (msg, 24, nonce, 0, 8);
        return nonce;
    } // getNonce


    /**************************************************************************
     * <p>
     * Builds the nonce response message.
     * </p>
     * <p>
     * It requires the Lan Manager and NT hashed version of user password, that
     * can be computed from the cleartext version by
     * {@link #computeNTPassword (String) computeNTPassword} and
     * {@link #computeNTLMResponse (byte[], byte[], byte[], byte[], byte[]) computeNTLMResponse},
     * and the nonce obtained from the server by
     * {@link #getNonce (byte[]) getNonce}. <BR/>
     * The returned message should be encoded according to protocol specific
     * rules (e.g. base 64 encoding). <BR/>
     * The message format is discussed <a
     * href="http://www.innovation.ch/java/ntlm.html">here</a>.
     * </p>
     *
     * @param host the name of the host that is authenticating
     * @param user the name of the user
     * @param userDomain the name of the domain to which the user belongs
     * @param lmPassword a 16-bytes array containing the Lan Manager hashed
     *            password
     * @param ntPassword a 16-bytes array containing the NT hashed password
     * @param nonce a 8-byte array containing the nonce sent by server to reply
     *            to the request message
     *
     * @return the challenge response message to send to server to complete the
     *         authentication procedure
     *
     * @exception IOException if an error occurs during the message formatting
     * @exception IllegalArgumentException if a parameter has an illegal size
     * @exception javax.crypto.NoSuchPaddingException if there isn't any
     *                suitable padding method
     * @exception NoSuchAlgorithmException if there isn't any suitable cipher
     *                algorithm
     *
     * @see <a href="http://www.innovation.ch/java/ntlm.html">NTLM
     *      Authentication Scheme for HTTP</a>
     */
    public static byte[] formatResponse (String host, String user,
                                         String userDomain, byte[] lmPassword,
                                         byte[] ntPassword, byte[] nonce)
        throws IllegalArgumentException, IOException, NoSuchAlgorithmException,
        NoSuchPaddingException
    {
        if (host == null)
        {
            throw new IllegalArgumentException ("host : null value not allowed");
        } // if
        if (user == null)
        {
            throw new IllegalArgumentException ("user : null value not allowed");
        } // if
        if (userDomain == null)
        {
            throw new IllegalArgumentException (
                "userDomain : null value not allowed");
        } // if
        if (lmPassword == null)
        {
            throw new IllegalArgumentException (
                "lmPassword : null value not allowed");
        } // if
        if (ntPassword == null)
        {
            throw new IllegalArgumentException (
                "ntPassword : null value not allowed");
        } // if
        if (nonce == null)
        {
            throw new IllegalArgumentException (
                "nonce : null value not allowed");
        } // if
        if (lmPassword.length != 16)
        {
            throw new IllegalArgumentException (NTLM.MSG_LMPASSWORD_SIZE);
        } // if
        if (ntPassword.length != 16)
        {
            throw new IllegalArgumentException (NTLM.MSG_NTPASSWORD_SIZE);
        } // if
        if (nonce.length != 8)
        {
            throw new IllegalArgumentException (NTLM.MSG_NONCE_SIZE);
        } // if

        byte[] lmResponse = new byte[24];
        byte[] ntResponse = new byte[24];

        NTLM.computeNTLMResponse (lmPassword, ntPassword, nonce, lmResponse,
            ntResponse);

        String userDomainLocal = userDomain.toUpperCase ();
        String hostLocal = host.toUpperCase ();
        short lmRespLen = (short) 0x18;
        short ntRespLen = (short) 0x18;
        short domainLen = (short) (2 * userDomainLocal.length ());
        short hostLen = (short) (2 * hostLocal.length ());
        short userLen = (short) (2 * user.length ());
        short domainOff = (short) 0x40;
        short userOff = (short) (domainOff + domainLen);
        short hostOff = (short) (userOff + userLen);
        short lmRespOff = (short) (hostOff + hostLen);
        short ntRespOff = (short) (lmRespOff + lmRespLen);
        short msgLen = (short) (ntRespOff + ntRespLen);
        ByteArrayOutputStream os = new ByteArrayOutputStream (1024);
        DataOutputStream dataOut = new DataOutputStream (os);
        dataOut.writeBytes (NTLM.NTLM_START);
        dataOut.writeByte (0x03);
        dataOut.writeByte (0x00);
        dataOut.writeByte (0x00);
        dataOut.writeByte (0x00);
        dataOut.writeShort (NTLM.swapBytes (lmRespLen));
        dataOut.writeShort (NTLM.swapBytes (lmRespLen));
        dataOut.writeShort (NTLM.swapBytes (lmRespOff));
        dataOut.writeShort (0x0000);
        dataOut.writeShort (NTLM.swapBytes (ntRespLen));
        dataOut.writeShort (NTLM.swapBytes (ntRespLen));
        dataOut.writeShort (NTLM.swapBytes (ntRespOff));
        dataOut.writeShort (0x0000);
        dataOut.writeShort (NTLM.swapBytes (domainLen));
        dataOut.writeShort (NTLM.swapBytes (domainLen));
        dataOut.writeShort (NTLM.swapBytes (domainOff));
        dataOut.writeShort (0x0000);
        dataOut.writeShort (NTLM.swapBytes (userLen));
        dataOut.writeShort (NTLM.swapBytes (userLen));
        dataOut.writeShort (NTLM.swapBytes (userOff));
        dataOut.writeShort (0x0000);
        dataOut.writeShort (NTLM.swapBytes (hostLen));
        dataOut.writeShort (NTLM.swapBytes (hostLen));
        dataOut.writeShort (NTLM.swapBytes (hostOff));
        dataOut.writeShort (0x0000);
        dataOut.writeInt (0x00000000);
        dataOut.writeShort (NTLM.swapBytes (msgLen));
        dataOut.writeShort (0x0000);
        dataOut.writeShort (0x0000); //    dataOut.writeShort (swapBytes ((short)0x8201));
        dataOut.writeShort (0x0000);

        for (int i = 0; i < userDomainLocal.length (); i++)
        {
            dataOut.writeShort (NTLM.swapBytes ((short) userDomainLocal.charAt (i)));
        } // for i
        for (int i = 0; i < user.length (); i++)
        {
            dataOut.writeShort (NTLM.swapBytes ((short) user.charAt (i)));
        } // for i
        for (int i = 0; i < hostLocal.length (); i++)
        {
            dataOut.writeShort (NTLM.swapBytes ((short) hostLocal.charAt (i)));
        } // for i
        dataOut.write (lmResponse);
        dataOut.write (ntResponse);
        dataOut.flush ();
        return os.toByteArray ();
    } // formatResponse

} // class NTLM

