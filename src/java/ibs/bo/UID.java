/*
 * Class: UID.java
 */

// package:
package ibs.bo;

// imports:
import ibs.BaseObject;
import ibs.bo.IncorrectUidException;
import ibs.util.Helpers;
import ibs.util.UtilConstants;


/******************************************************************************
 * This class represents the UID of an user. <BR/>
 * This UID is unique within all servers of one domain.
 * construction: s (8)100u (21)
 * <UL>
 * <LI>s (8) ... 8 bit identifier of server
 * <LI>100 .... fixed 3 bits to identify user type
 * <LI>u (21) .. 21 bit identifier of user within server
 * </UL>
 *
 * @version     $Id: UID.java,v 1.9 2007/07/27 12:01:42 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 980421
 ******************************************************************************
 */
public class UID extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: UID.java,v 1.9 2007/07/27 12:01:42 kreimueller Exp $";


    /**
     * Type of error for this class. <BR/>
     * This String is the name of an error which occurs within this class.
     */
    public String ERR_TYPE = this.getClass ().getName () + " Error";

    /**
     * The domain wide unique id of an user. <BR/>
     * To be unique this id must consist of the domain and server where the
     * object resides, the type where the object belongs to and the object id
     * within the type itself. This value is splitted into its parts:
     * <UL>
     * <LI><A HREF="#server">server</A>
     * <LI><A HREF="#id">id</A>
     * </UL>
     */
    public int uid = 0;

    /**
     * The server where the user resides. <BR/>
     * This attribute is extracted from the uid.
     */
    public int server = 0;

    /**
     * The id of the user itself. <BR/>
     * This attribute is extracted from the uid.
     */
    public int id = 0;


    /**************************************************************************
     * Creates an UID object. <BR/>
     * The compound user id is used as base for getting the
     * <A HREF="#server">server</A> and <A HREF="#id">id</A> of the user.
     * These values are stored in the special public attributes of this type.
     * <BR/>
     *
     * @param   uid         Value for the compound user id.
     *
     * @exception   IncorrectUidException
     *              Is raised if the parameter does not result in a 4 byte uid.
     */
    public UID (int uid) throws IncorrectUidException
    {
        // check the uid for correctness:
        if ((uid & 0x00D00000) != 0x00800000) // no correct uid?
        {
            IncorrectUidException error =
                new IncorrectUidException (this.ERR_TYPE + " \'" + uid + "\'");
            throw error;
        } // if no correct uid

        // set the new uid:
        this.uid = uid;

        // get the simple values out of the compound uid:
        this.server = uid / 0x01000000;
        this.id = uid & 0x001FFFFF;
    } // UID


    /**************************************************************************
     * Creates an UID object. <BR/>
     * This method tries to get the user id out of a string representation of
     * the form "0xssuuuuuu", "ss" is the 1 byte server id and
     * represents the 21 bit user id itself starting with the 3 bits 100. <BR/>
     * The compound user id is used as base for getting the
     * <A HREF="#server">server</A> and <A HREF="#id">id</A> of the user.
     * These values are stored in the special public attributes of this type.
     * <BR/>
     *
     * @param   uidStr      String representation for the compound user id.
     *
     * @exception   IncorrectUidException
     *              Is raised if the parameter does not result in a 4 byte uid.
     */
    public UID (String uidStr) throws IncorrectUidException
    {
        int uid = this.parseString (uidStr);

        // check the uid for correctness:
        if ((uid & 0x00D00000) != 0x00800000) // no correct uid?
        {
            IncorrectUidException error =
                new IncorrectUidException (this.ERR_TYPE + " \'" + uidStr + "\'");
            throw error;
        } // if no correct uid

        // set the new uid:
        this.uid = uid;

        // get the simple values out of the compound oid:
        this.server = uid / 0x01000000;
        this.id = uid & 0x001FFFFF;
    } // UID


    /**************************************************************************
     * Creates an UID object. <BR/>
     * The compound user id is created from the
     * <A HREF="#server">server</A> and <A HREF="#id">id</A> values.
     * These values are stored in the special public attributes of this type.
     * <BR/>
     *
     * @param   server      Value for the server.
     * @param   id          Value for the id itself.
     */
    public UID (int server, int id)
    {
        // set the new simple values:
        this.server = server;
        this.id = id;

        // create the new uid:
        this.uid = server * 0x01000000 + 0x00800000 + id;
    } // UID


    /**************************************************************************
     * Returns the hexedecimal string representation of this UID. <BR/>
     * The components of the oid are concatenated to create a string
     * representation according to "0xssuuuuuu" where
     * "ss" is the server and "uuuuuu" represents the id of
     * the user itself starting with the 3 bits 100. <BR/>
     *
     * @return  Hexadecimal represention of the uid stored in a String.
     */
    public String toString ()
    {
        // compute the string representation of the uid value and return it:
        return UtilConstants.NUM_START_HEX +
            Helpers.byteToString ((byte) (this.uid / 0x01000000)) +
            Helpers.byteToString ((byte) ((this.uid / 0x00010000) % 0x100)) +
            Helpers.byteToString ((byte) ((this.uid / 0x00000100) % 0x100)) +
            Helpers.byteToString ((byte) (this.uid % 0x100));
    } // toString


    /**************************************************************************
     * Parse a String containing the hexadecimal representation of an uid to get
     * the uid. <BR/>
     * The input value must be of the format "0xssuuuuuu" where "ss" is the
     * server as 1 byte and the 3 bytes "uuuuuu" represent the id of the user
     * itself starting with the 3 bits 100. <BR/>
     * The method returns an uid value containing the uid which results after
     * parsing the string.
     *
     * @param   uidStr      String representation of the user id.
     *
     * @return  Parsed uid value.
     *
     * @exception   IncorrectUidException
     *              Is raised if the parameter does not result in a 4 byte uid.
     */
    private int parseString (String uidStr) throws IncorrectUidException
    {
        if (uidStr.length () != 10)
        {
            IncorrectUidException error =
                new IncorrectUidException (this.ERR_TYPE + " \'" + uidStr + "\'");
            throw error;
        } // if

        // compute the value of the uid:
        int uidVal =
            (Helpers.stringToByte ("" + uidStr.charAt (2) + uidStr.charAt (3))) * 0x01000000 +
            (Helpers.stringToByte ("" + uidStr.charAt (4) + uidStr.charAt (5))) * 0x00010000 +
            (Helpers.stringToByte ("" + uidStr.charAt (6) + uidStr.charAt (7))) * 0x00000100 +
            (Helpers.stringToByte ("" + uidStr.charAt (8) + uidStr.charAt (9)));

        return uidVal;                  // return the computed uid value
    } // parseString

} // class UID
