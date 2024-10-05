/**
 * Class: EncryptionManager.java
 */

// package:
package ibs.util.crypto;

// imports:
import ibs.util.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;





/******************************************************************************
 * This is the EncryptionManager. <BR/>
 *
 * @version     $Id: EncryptionManager.java,v 1.4 2007/09/11 17:35:25 bbuchegger Exp $
 *
 * @author      Daniel Janesch (DJ), 020614
 *
 * @see org.apache.xerces.impl.dv.util.Base64
 ******************************************************************************
 */
public abstract class EncryptionManager extends Object
{
    /**
     * The key from which we get the DES-key. <BR/>
     */
    private static final char[] STANDARD_KEY =
    {
        't',
        'H',
        'a',
        ' ',
        'p',
        'L',
        'a',
        'Y',
        'a',
        ' ',
        '\'',
        'd',
        'e',
        'e',
        'j',
        'a',
        'y',
        '_',
        '\'',
        ' ',
        'r',
        'U',
        'l',
        'E',
        's',
        '!',
        ' ',
        'c',
        'E',
        'e',
        'Y',
        'a',
    };

    /**
     * Encoding: ISO-8859-1. <BR/>
     */
    private static final String ENCODING_ISO_LATIN_1 = "ISO-8859-1";

    /**
     * This prefix is set to all encrypted strings. It is needed to prevent that
     * a string would be encrypted more thene once. <BR/>
     */
    private static final String ENCRYPTION_PREFIX = "isEnc_";

    /**
     * Work mode: encrypt. <BR/>
     */
    private static final String MODE_ENC = "enc";

    /**
     * Work mode: decrypt. <BR/>
     */
    private static final String MODE_DEC = "dec";


    /**************************************************************************
     * The main method to en-/decrypt from command line. <BR/>
     *
     * @param   args        The additional arguments.
     *                      (1st ... 'enc'/'dec'; 2cnd ... the string)
     */
    public static void main (String[] args)
    {
        String className = EncryptionManager.class.getName ();

        StringBuffer correctSyntax = new StringBuffer ();
        StringBuffer noChange = new StringBuffer ();
        StringBuffer error = new StringBuffer ();

        String mode = "";
        String toWork = "";
        String result = "";
        String pref1 = "\n *  ";
        String pref2 = pref1 + "    ";

        noChange.append (pref1 + "NOTE: The en-/decrypted string is same as the given string because of the following reason:");
        noChange.append (pref1);
        noChange.append (pref2 + "    The following restriction (s) must be observed:");
        noChange.append (pref2 + "    --> encryption: the given string must not start with '" + EncryptionManager.ENCRYPTION_PREFIX + "'.");
        noChange.append (pref2 + "    --> decryption: the given string must start with '" + EncryptionManager.ENCRYPTION_PREFIX + "'.");

        correctSyntax.append (pref1 + "Syntax: java " + className + " string [mode]");
        correctSyntax.append (pref1);
        correctSyntax.append (pref1);
        correctSyntax.append (pref2 + "string ..... The string which should be en-/decrypted");
        correctSyntax.append (pref2 + "mode ....... The encryption mode");
        correctSyntax.append (pref2 + "                 'dec' for decrypting (Default) or");
        correctSyntax.append (pref2 + "                 'enc' for encrypting");
        correctSyntax.append (pref1);
        correctSyntax.append (pref1);
        correctSyntax.append (noChange);

        if (args.length < 1 || args.length > 2)
        {
            error.append ("Wrong number of arguments!!");
        } // if
        else
        {
            mode = (args.length == 2) ? args[1] : EncryptionManager.MODE_DEC;
            toWork = args[0];

            if (mode.equalsIgnoreCase (EncryptionManager.MODE_ENC))
            {
                result = EncryptionManager.encrypt (toWork);
            } // if
            else if (mode.equalsIgnoreCase (EncryptionManager.MODE_DEC))
            {
                result = EncryptionManager.decrypt (toWork);
            } // else if
            else
            {
                error.append ("Wrong argument mode: '" + mode + "'");
            } // else
        } // else

        EncryptionManager.writeOut (pref1);

        if (error.length () == 0)
        {
            EncryptionManager.writeOut (pref1 + "Starting to en-/decrypt the given string ...");
            EncryptionManager.writeOut (pref1);
            EncryptionManager.writeOut (pref1);
            EncryptionManager.writeOut (pref1);
            EncryptionManager.writeOut (pref1);
            EncryptionManager.writeOut (pref1 + "string is en-/decrypted:");
            EncryptionManager.writeOut (pref1);
            EncryptionManager.writeOut (pref2 + result);
            EncryptionManager.writeOut (pref1);

            if (toWork.equals (result))
            {
                EncryptionManager.writeOut (pref1);
                EncryptionManager.writeOut (noChange.toString ());
                EncryptionManager.writeOut (pref1);
            } // if

            EncryptionManager.writeOut ("\n");
        } // if
        else
        {
            EncryptionManager.writeOut (pref1 + "Error: " + error.toString ());
            EncryptionManager.writeOut (pref1);
            EncryptionManager.writeOut (pref1);
            EncryptionManager.writeOut (pref1 + correctSyntax.toString ());
            EncryptionManager.writeOut (pref1);
        } // else
    } // main


    /**************************************************************************
     * To write to the system. <BR/>
     *
     * @param   string  The String to be written.
     */
    private static void writeOut (String string)
    {
        System.out.print (string);
    } // writeOut


    /**************************************************************************
     * Decrypts the given string. <BR/>
     *
     * @param   value       The string which should be decrypted.
     *
     * @return  If everything was right --> <B>decrypted string</B>. <BR/>
     *          If value is null --> <B>null</B>. <BR/>
     *          If value did not start with
     *          {@link #ENCRYPTION_PREFIX} -->
     *          <B>value</B>.
     */
    public static String decrypt (String value)
    {
        String tmpValue = value;
        if (value != null && value.startsWith (EncryptionManager.ENCRYPTION_PREFIX))
        {
            char[] tmp = EncryptionManager.getBase64AndURLDecoded (tmpValue);
            char[] decTmp = new char[tmp.length / 2];

            // double the length of the original string to prevent an equal length:
            int k = tmp.length - 2;
            for (int i = 0; i < decTmp.length; i++, k -= 2)
            {
                decTmp[i] = tmp[k];
            } // for

            tmpValue = new String (decTmp);
        } // if

        return tmpValue;
    } // decrypt


    /**************************************************************************
     * Encrypts the given string. <BR/>
     *
     * @param   value       The string which should be encrypted.
     *
     * @return  If everything was right --> <B>encrypted string</B>. <BR/>
     *          If value is null --> <B>null</B>. <BR/>
     *          If value did start with
     *          {@link #ENCRYPTION_PREFIX} -->
     *          <B>value</B>.
     */
    public static String encrypt (String value)
    {
        String tmpValue = value;
        if (value != null && !value.startsWith (EncryptionManager.ENCRYPTION_PREFIX))
        {
            char[] tmp = value.toCharArray ();
            char[] encTmp = new char[tmp.length * 2];
            int standardKeyLen = EncryptionManager.STANDARD_KEY.length;

            // double the length of the original string to prevent an equal length:
            int k = tmp.length - 1;
            for (int i = 0; i < tmp.length; i++, k--)
            {
                encTmp[i * 2] = tmp[k];
                encTmp[ (i * 2) + 1] = EncryptionManager.STANDARD_KEY[i % standardKeyLen];
            } // for

            // now do a BASE64 and a URL encoding to ensure that there are no
            // special characters in the given string:
            tmpValue = EncryptionManager.getBase64AndURLEcoded (encTmp);
        } // if

        return tmpValue;
    } // encrypt


    /**************************************************************************
     * The given string is first URL decoded and aftrewards BASE64 decoded. <BR/>
     * Before the decoding starts the {@link #ENCRYPTION_PREFIX} is deleted from
     * the value.
     *
     * @param   value       The value which should be decoded.
     *
     * @return  A URL and BASE64 decoded character array.
     */
    private static char[] getBase64AndURLDecoded (String value)
    {
        String urlDecoded = null;
        String base64Decoded = null;
        String valueString = null;

        try
        {
            // convert the character array to a string for :
            valueString = value.substring (EncryptionManager.ENCRYPTION_PREFIX.length ());
            urlDecoded = URLDecoder.decode (valueString, EncryptionManager.ENCODING_ISO_LATIN_1);
            base64Decoded = new String (Base64.decode (urlDecoded));
            // returns the encoded string:
            return base64Decoded.toCharArray ();
        } // try
        catch (UnsupportedEncodingException e)
        {
            EncryptionManager.writeOut (e.toString ());
            return null;
        } // catch (UnsupportedEncodingException e)
    } // getBase64AndURLDecoded


    /**************************************************************************
     * The given string is first BASE64 encoded and aftrewards URL encoded. <BR/>
     * This is done to ensusre that there are no special characters in the
     * given string. Also the {@link #ENCRYPTION_PREFIX} is
     * added to the string as prefix.
     *
     * @param   value       The value which should be encoded.
     *
     * @return  A BASE64 and URL encoded string with
     *          {@link #ENCRYPTION_PREFIX} as prefix.
     */
    private static String getBase64AndURLEcoded (char[] value)
    {
        String base64Encoded = null;
        String valueString = null;
        String urlEncoded = null;

        try
        {
            // convert the character array to a string for :
            valueString = new String (value);
            base64Encoded = new String (Base64.encode (valueString.getBytes ()));
            urlEncoded = URLEncoder.encode (base64Encoded,
                EncryptionManager.ENCODING_ISO_LATIN_1);
            // returns the encoded string:
            return EncryptionManager.ENCRYPTION_PREFIX + urlEncoded;
        } // try
        catch (UnsupportedEncodingException e)
        {
            EncryptionManager.writeOut (e.toString ());
            return null;
        } // catch (UnsupportedEncodingException e)
    } // getBase64AndURLEcoded


    /**************************************************************************
     * Check if a string starts with the encryption prefix. This indicates
     * an encrypted value.<BR/>
     * Note that this test is not very strong, because the rest of the
     * string is not checked.<BR/>
     *
     * @param   value       The string which should be tested
     *
     * @return  true is the string is encrypted
     */
    public static boolean isEncrypted (String value)
    {
        return value.startsWith (EncryptionManager.ENCRYPTION_PREFIX);
    } // isEncrypted

} // class EncryptionManager
