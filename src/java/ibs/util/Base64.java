/*
 * Class: Base64.java
 */

// package:
package ibs.util;

// imports:


/******************************************************************************
 * BASE 64 encoding and decoding of a String or an array of bytes.
 *
 * See also RFC 1421.
 *
 * @version     $Id: Base64.java,v 1.11 2008/09/17 16:01:46 kreimueller Exp $
 *
 * @author      Unknown
 * @author      David W. Croft
 ******************************************************************************
 */
public class Base64 extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Base64.java,v 1.11 2008/09/17 16:01:46 kreimueller Exp $";


    /**
     * The available characters for encoding. <BR/>
     * @deprecated  This variable is never read locally.
     */
/*
    private static final char [ ] ALPHABET =
    {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',   //  0 to  7
        'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',   //  8 to 15
        'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',   // 16 to 23
        'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',   // 24 to 31
        'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',   // 32 to 39
        'o', 'p', 'q', 'r', 's', 't', 'u', 'v',   // 40 to 47
        'w', 'x', 'y', 'z', '0', '1', '2', '3',   // 48 to 55
        '4', '5', '6', '7', '8', '9', '+', '/',   // 56 to 63
    };
*/


    /**************************************************************************
     * Encode a string. <BR/>
     *
     * @param   origString  The string to be encoded.
     *
     * @return  The encoded string.
     */
    public static String encode (String  origString)
    {
        return Base64.encode (origString.getBytes ());
    } // encode


    /**************************************************************************
     * Encode a list of bytes. <BR/>
     *
     * @param   octetString The bytes to be encoded.
     *
     * @return  The encoded string.
     */
    public static String encode (byte [] octetString)
    {
/* KR the method of the apache base64 encoder returns a wrong result
*/
        return org.apache.xerces.impl.dv.util.Base64.encode (octetString);
/*
        int  bits24;
        int  bits6;

        char[] out = new char[ ((octetString.length - 1) / 3 + 1) * 4];

        int outIndex = 0;
        int i        = 0;

        while ((i + 3) <= octetString.length)
        {
            // store the octets
            bits24 = (octetString[i++] & 0xFF) << 16;
            bits24 |= (octetString[i++] & 0xFF) << 8;
            bits24 |= (octetString[i++] & 0xFF) << 0;

            bits6 = (bits24 & 0x00FC0000) >> 18;
            out[outIndex++] = Base64.ALPHABET[bits6];
            bits6 = (bits24 & 0x0003F000) >> 12;
            out[outIndex++] = Base64.ALPHABET[bits6];
            bits6 = (bits24 & 0x00000FC0) >> 6;
            out[outIndex++] = Base64.ALPHABET[bits6];
            bits6 = bits24 & 0x0000003F;
            out[outIndex++] = Base64.ALPHABET[bits6];
        } // while

        if (octetString.length - i == 2)
        {
            // store the octets
            bits24 = (octetString[i] & 0xFF) << 16;
            bits24 |= (octetString[i + 1] & 0xFF) << 8;

            bits6 = (bits24 & 0x00FC0000) >> 18;
            out[outIndex++] = Base64.ALPHABET[bits6];
            bits6 = (bits24 & 0x0003F000) >> 12;
            out[outIndex++] = Base64.ALPHABET[bits6];
            bits6 = (bits24 & 0x00000FC0) >> 6;
            out[outIndex++] = Base64.ALPHABET[bits6];

            // padding
            out[outIndex++] = '=';
        } // if
        else if (octetString.length - i == 1)
        {
            // store the octets
            bits24 = (octetString[i] & 0xFF) << 16;

            bits6 = (bits24 & 0x00FC0000) >> 18;
            out[outIndex++] = Base64.ALPHABET[bits6];
            bits6 = (bits24 & 0x0003F000) >> 12;
            out[outIndex++] = Base64.ALPHABET[bits6];

            // padding
            out[outIndex++] = '=';
            out[outIndex++] = '=';
        } // else if
        return new String (out);
*/
    } // encode


    /**************************************************************************
     * Decodes Base64 data into octects. <BR/>
     *
     * @param   binaryData  Byte array containing Base64 data
     *
     * @return  Array containind decoded data.
     */
    public static byte[] decode (String binaryData)
    {
        return org.apache.xerces.impl.dv.util.Base64.decode (binaryData);
    } // decode

} // class Base64
