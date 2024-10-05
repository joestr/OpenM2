/*
 * Class: OID.java
 */

// package:
package ibs.bo;

// imports:
import ibs.BaseObject;
import ibs.bo.IncorrectOidException;
import ibs.bo.type.Type;
import ibs.io.IOHelpers;
import ibs.tech.sql.SQLHelpers;
import ibs.util.Helpers;
import ibs.util.UtilConstants;


/******************************************************************************
 * This class represents the OID of a business object. <BR/>
 *
 * @version     $Id: OID.java,v 1.33 2010/08/31 12:39:41 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR), 990115
 *
 * @see         ibs.bo.BusinessObject
 ******************************************************************************
 */
public class OID extends BaseObject implements Cloneable
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: OID.java,v 1.33 2010/08/31 12:39:41 rburgermann Exp $";


    /**
     * Type of error for this class. <BR/>
     * This String is the name of an error which occurs within this class.
     */
    public String ERR_TYPE = this.getClass ().getName () + " Error";

    /**
     * String representation of empty oid. <BR/>
     */
    public static final String EMPTYOID = "0x0000000000000000";

    /**
     * The length of a valid oid. <BR/>
     */
    public static final int OIDLENGTH = 18;

    /**
     * The next id to be used for a temporary oid. <BR/>
     */
    private static int p_nextTempId = 1;


    /**
     * The system wide unique id of a business object. <BR/>
     * To be unique this id must consist of the domain and server where the
     * object resides, the type where the object belongs to and the object id
     * within the type itself. This value is splitted into its parts:
     * <UL>
     * <LI>{@link #domain domain}
     * <LI>{@link #server server}
     * <LI>{@link #type type}
     * <LI>{@link #id id}
     * </UL>
     */
    public byte [] oid = {0, 0, 0, 0, 0, 0, 0, 0};

    /**
     * The domain where the object resides. <BR/>
     * This attribute is extracted from the oid.
     */
    public int domain = 0;

    /**
     * The server where the object resides. <BR/>
     * This attribute is extracted from the oid.
     */
    public int server = 0;

    /**
     * The type where the object belongs to. <BR/>
     * This attribute is extracted from the oid. <BR/>
     * It includes not only the type itself, but also the version of the type.
     */
    public int type = 0;

    /**
     * The id of the object itself. <BR/>
     * This attribute is extracted from the oid.
     */
    public int id = 0;

    /**
     * The id of the type version. <BR/>
     * This attribute is extracted from the oid. <BR/>
     * It includes not only the type and its version, but also the domain and
     * server, i.e. this id is the object number independent part of the oid.
     */
    public int tVersionId = 0;

    /**
     * String representation of the oid. <BR/>
     * This value is for performance optimization.
     */
    private String p_strValue = null;

    /**
     * String buffer representation of the oid. <BR/>
     * This value is for performance optimization.
     *
     * @deprecated  As of Java release JDK 5, an equivalent class was introduced
     *              which is only single-threaded and thus more performant:
     *              StringBuilder. This class shall be used now.
     */
    private StringBuffer p_strBufValue = null;

    /**
     * String builder representation of the oid. <BR/>
     * This value is for performance optimization.
     */
    private StringBuilder p_strBuilderValue = null;


    /**************************************************************************
     * Get an empty oid. <BR/>
     * This method creates a new OID object which represents an empty oid.
     *
     * @return  The generated oid.
     */
    public static OID getEmptyOid ()
    {
        // create a new OID object which corresponds to no object:
        return new OID (0, 0);
    } // getEmptyOid


    /**************************************************************************
     * Get a temporary oid. <BR/>
     * This method creates a new OID object which represents a temporary oid.
     * A tmporary oid is an oid which can be used for temporary objects.
     * It is different from a "normal" oid by the 9th nibble which is always
     * <CODE>F</CODE>. This means that such an oid looks like
     * <CODE>0x01010021F0000001</CODE>.
     *
     * @param   tVersionId  Value for the tVersionId.
     *
     * @return  The generated oid.
     */
    public static OID getTempOid (int tVersionId)
    {
        // create a new OID object with the tVersionId and a new temporary id:
        return new OID (tVersionId, OID.getNextTempId ());
    } // getTempOid


    /**************************************************************************
     * Get the next id for a temporary oid. <BR/>
     * This method gets the next temporary id out of the stored value and
     * stores back the incremented value for the next id. The first value is
     * returned.
     *
     * @return  The next id.
     */
    private static synchronized int getNextTempId ()
    {
        // get the next id:
        int retVal = 0xF0000000 | OID.p_nextTempId;

        // increment the stored value:
        OID.p_nextTempId = (OID.p_nextTempId + 1) % 0x10000000;

        // return the result:
        return retVal;
    } // getNextTempId


    /**************************************************************************
     * Convert an array of oids to an array of strings. <BR/>
     *
     * @param   oids    The oid array.
     *
     * @return  The generated oid string array.
     *          <CODE>null</CODE> if the original array was <CODE>null</CODE>.
     */
    public static String[] oidArrayToString (OID[] oids)
    {
        String[] retVal = null;

        // check if there are any oids to convert:
        if (oids == null)
        {
            return null;
        } // if

        // initialize the array:
        retVal = new String[oids.length];

        // loop through the oids and convert each of them into a string:
        for (int i = 0; i < oids.length; i++)
        {
            // check if the oid exists:
            if (oids[i] != null)
            {
                // convert the actual value:
                retVal[i] = oids[i].toString ();
            } // if
            else                        // oid does not exist
            {
                // let the value unchanged:
                retVal[i] = null;
            } // else oid does not exist
        } // for i

        // return the result:
        return retVal;
    } // oidArrayToString


    /**************************************************************************
     * Convert an array of oids to an array of string buffers. <BR/>
     *
     * @param   oids    The oid array.
     *
     * @return  The generated oid string buffer array.
     *          <CODE>null</CODE> if the original array was <CODE>null</CODE>.
     */
    public static StringBuilder[] oidArrayToStringBuilder (OID[] oids)
    {
        StringBuilder[] retVal = null;

        // check if there are any oids to convert:
        if (oids == null)
        {
            return null;
        } // if

        // initialize the array:
        retVal = new StringBuilder [oids.length];

        // loop through the oids and convert each of them into a string:
        for (int i = 0; i < oids.length; i++)
        {
            // check if the oid exists:
            if (oids[i] != null)
            {
                // convert the actual value:
                retVal[i] = oids[i].toStringBuilder ();
            } // if
            else                        // oid does not exist
            {
                // let the value unchanged:
                retVal[i] = null;
            } // else oid does not exist
        } // for i

        // return the result:
        return retVal;
    } // oidArrayToStringBuilder


    /**************************************************************************
     * Convert an array of oids to an array of string buffers. <BR/>
     *
     * @param   oids    The oid array.
     *
     * @return  The generated oid string buffer array.
     *          <CODE>null</CODE> if the original array was <CODE>null</CODE>.
     *
     * @deprecated  As of Java release JDK 5, an equivalent class was introduced
     *              which is only single-threaded and thus more performant:
     *              StringBuilder. This class shall be used now.
     *              Use {@link #oidArrayToStringBuilder(OID[])} instead.
     */
    public static StringBuffer[] oidArrayToStringBuffer (OID[] oids)
    {
        StringBuffer[] retVal = null;

        // check if there are any oids to convert:
        if (oids == null)
        {
            return null;
        } // if

        // initialize the array:
        retVal = new StringBuffer [oids.length];

        // loop through the oids and convert each of them into a string:
        for (int i = 0; i < oids.length; i++)
        {
            // check if the oid exists:
            if (oids[i] != null)
            {
                // convert the actual value:
                retVal[i] = oids[i].toStringBuffer ();
            } // if
            else                        // oid does not exist
            {
                // let the value unchanged:
                retVal[i] = null;
            } // else oid does not exist
        } // for i

        // return the result:
        return retVal;
    } // oidArrayToStringBuffer


    /**************************************************************************
     * Convert an array of oid strings to an array of oids. <BR/>
     *
     * @param   strings The oid string array.
     *
     * @return  The generated oid array.
     *          <CODE>null</CODE> if the original array was <CODE>null</CODE>.
     *
     * @exception   IncorrectOidException
     *              Is raised if the parameter does not result in a 8 byte id.
     */
    public static OID[] stringArrayToOid (String[] strings)
        throws IncorrectOidException
    {
        OID[] retVal = null;

        // check if there are any strings to convert:
        if (strings == null || strings.length == 0)
        {
            return null;
        } // if

        // initialize the array:
        retVal = new OID[strings.length];

        // loop through the strings and convert each of them into an oid:
        for (int i = 0; i < strings.length; i++)
        {
            // check if the string exists:
            if (strings[i] != null && strings[i].length () > 0)
            {
                // convert the actual value:
                retVal[i] = new OID (strings[i]);
            } // if
            else                        // string does not exist
            {
                // let the value unchanged:
                retVal[i] = null;
            } // else string does not exist
        } // for i

        // return the result:
        return retVal;
    } // stringArrayToOid


    /**************************************************************************
     * Convert an array of oid string buffers to an array of oids. <BR/>
     *
     * @param   strings The oid string buffer array.
     *
     * @return  The generated oid array.
     *          <CODE>null</CODE> if the original array was <CODE>null</CODE>.
     *
     * @exception   IncorrectOidException
     *              Is raised if the parameter does not result in a 8 byte id.
     */
    public static OID[] stringBuilderArrayToOid (StringBuilder[] strings)
        throws IncorrectOidException
    {
        OID[] retVal = null;

        // check if there are any strings to convert:
        if (strings == null || strings.length == 0)
        {
            return null;
        } // if

        // initialize the array:
        retVal = new OID[strings.length];

        // loop through the strings and convert each of them into an oid:
        for (int i = 0; i < strings.length; i++)
        {
            // check if the string exists:
            if (strings[i] != null && strings[i].length () > 0)
            {
                // convert the actual value:
                retVal[i] = new OID (strings[i]);
            } // if
            else                        // string does not exist
            {
                // let the value unchanged:
                retVal[i] = null;
            } // else string does not exist
        } // for i

        // return the result:
        return retVal;
    } // stringBuilderArrayToOid


    /**************************************************************************
     * Convert an array of oid string buffers to an array of oids. <BR/>
     *
     * @param   strings The oid string buffer array.
     *
     * @return  The generated oid array.
     *          <CODE>null</CODE> if the original array was <CODE>null</CODE>.
     *
     * @exception   IncorrectOidException
     *              Is raised if the parameter does not result in a 8 byte id.
     *
     * @deprecated  As of Java release JDK 5, an equivalent class was introduced
     *              which is only single-threaded and thus more performant:
     *              StringBuilder. This class shall be used now.
     *              Use {@link #stringBuilderArrayToOid(StringBuilder[])} instead.
     */
    public static OID[] stringBufferArrayToOid (StringBuffer[] strings)
        throws IncorrectOidException
    {
        OID[] retVal = null;

        // check if there are any strings to convert:
        if (strings == null || strings.length == 0)
        {
            return null;
        } // if

        // initialize the array:
        retVal = new OID[strings.length];

        // loop through the strings and convert each of them into an oid:
        for (int i = 0; i < strings.length; i++)
        {
            // check if the string exists:
            if (strings[i] != null && strings[i].length () > 0)
            {
                // convert the actual value:
                retVal[i] = new OID (strings[i]);
            } // if
            else                        // string does not exist
            {
                // let the value unchanged:
                retVal[i] = null;
            } // else string does not exist
        } // for i

        // return the result:
        return retVal;
    } // stringBufferArrayToOid


    /**************************************************************************
     * Creates an OID object. <BR/>
     * The compound object id is used as base for getting the
     * {@link #domain domain}, {@link #server server}, {@link #type type},
     * {@link #id id} of the business object.
     * These values are stored in the special public attributes of this type.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     *
     * @exception   IncorrectOidException
     *              Is raised if the parameter does not result in a 8 byte id.
     */
    public OID (byte [] oid) throws IncorrectOidException
    {
        // check the oid for correctness:
        if (oid.length != 8)             // not exactly 8 byte?
        {
            IncorrectOidException error =
                new IncorrectOidException (this.ERR_TYPE + " \'" + oid + "\'");
            throw error;
        } // if not exactly 8 byte

        // set the new oid:
        this.oid = oid;

        // get the simple values out of the compound oid:
        this.updateSimpleValues ();

/*
 * ---------------------------------------
 * The following code would be used if the oid was a String:
 * Attention: there is no domain considered!
        // get local representations of properties:
        byte [] l_oid = this.oid;
        int l_domain = this.domain;
        int l_server = this.server;
        int l_type = this.type;
        int l_id = this.id;

        StringTokenizer st = new StringTokenizer (oid);
        try
        {
            if (st.hasMoreTokens ())        // server token found?
            {
                l_server = st.nextToken (); // get the server name
                if (st.hasMoreTokens ())    // type token found?
                {
                    l_type = st.nextToken (); // get the type name
                    if (st.hasMoreTokens ()) // id token found?
                    {
                        l_id = Integer.parseInt (st.nextToken ()); // get the id

                        // there is no error occurred until now =>
                        // set properties of this instance:
                        oid = l_oid;
                        server = l_server;
                        type = l_type;
                        id = l_id;
                    } // if id token found
                } // if type token found
            } // if server token found
        } // try
        catch (NoSuchElementException e)
        {
        } // catch
* ---------------------------------------
*/
    } // OID


    /**************************************************************************
     * Creates an OID object. <BR/>
     * This method try to get the object id out of a string representation of
     * the form "0xddssttttiiiiiiii", where "dd" is the 1 byte domain, "ss" is
     * the 1 byte server id, "tttt" is the 2 byte type version, and "iiiiiiii"
     * represents the 4 byte of the object id itself. <BR/>
     * The compound object id is used as base for getting the
     * {@link #domain domain}, {@link #server server}, {@link #type type}, and
     * {@link #id id} of the business object.
     * These values are stored in the special public attributes of this type.
     * <BR/>
     *
     * @param   oidStr      String representation for the compound object id.
     *
     * @exception   IncorrectOidException
     *              Is raised if the parameter does not result in a 8 byte id.
     */
    public OID (String oidStr) throws IncorrectOidException
    {
        byte [] oid = this.parseString (oidStr);

        // check the oid for correctness:
        if (oid.length != 8)             // not exactly 8 byte?
        {
            IncorrectOidException error =
                new IncorrectOidException (this.ERR_TYPE + " \'" + oidStr + "\'");
            throw error;
        } // if not exactly 8 byte

        // set the new oid:
        this.oid = oid;

        // get the simple values out of the compound oid:
        this.updateSimpleValues ();
    } // OID


    /**************************************************************************
     * Creates an OID object. <BR/>
     * This method try to get the object id out of a string buffer
     * representation of the form "0xddssttttiiiiiiii", where "dd" is the 1
     * byte domain, "ss" is the 1 byte server id, "tttt" is the 2 byte type
     * version, and "iiiiiiii" represents the 4 byte of the object id itself.
     * <BR/>
     * The compound object id is used as base for getting the
     * {@link #domain domain}, {@link #server server}, {@link #type type}, and
     * {@link #id id} of the business object.
     * These values are stored in the special public attributes of this type.
     * <BR/>
     *
     * @param   oidStr      String representation for the compound object id.
     *
     * @exception   IncorrectOidException
     *              Is raised if the parameter does not result in a 8 byte id.
     */
    public OID (StringBuilder oidStr) throws IncorrectOidException
    {
        byte [] oid = this.parseString (oidStr);

        // check the oid for correctness:
        if (oid.length != 8)             // not exactly 8 byte?
        {
            IncorrectOidException error =
                new IncorrectOidException (this.ERR_TYPE + " \'" + oidStr + "\'");
            throw error;
        } // if not exactly 8 byte

        // set the new oid:
        this.oid = oid;

        // get the simple values out of the compound oid:
        this.updateSimpleValues ();
    } // OID


    /**************************************************************************
     * Creates an OID object. <BR/>
     * This method try to get the object id out of a string buffer
     * representation of the form "0xddssttttiiiiiiii", where "dd" is the 1
     * byte domain, "ss" is the 1 byte server id, "tttt" is the 2 byte type
     * version, and "iiiiiiii" represents the 4 byte of the object id itself.
     * <BR/>
     * The compound object id is used as base for getting the
     * {@link #domain domain}, {@link #server server}, {@link #type type}, and
     * {@link #id id} of the business object.
     * These values are stored in the special public attributes of this type.
     * <BR/>
     *
     * @param   oidStr      String representation for the compound object id.
     *
     * @exception   IncorrectOidException
     *              Is raised if the parameter does not result in a 8 byte id.
     *
     * @deprecated  As of Java release JDK 5, an equivalent class was introduced
     *              which is only single-threaded and thus more performant:
     *              StringBuilder. This class shall be used now.
     *              Use {@link #parseString(StringBuilder)} instead.
     */
    public OID (StringBuffer oidStr) throws IncorrectOidException
    {
        byte [] oid = this.parseString (oidStr);

        // check the oid for correctness:
        if (oid.length != 8)             // not exactly 8 byte?
        {
            IncorrectOidException error =
                new IncorrectOidException (this.ERR_TYPE + " \'" + oidStr + "\'");
            throw error;
        } // if not exactly 8 byte

        // set the new oid:
        this.oid = oid;

        // get the simple values out of the compound oid:
        this.updateSimpleValues ();
    } // OID


    /**************************************************************************
     * Creates an OID object. <BR/>
     * The compound object id is created from the {@link #domain domain},
     * {@link #server server}, {@link #type type}, and {@link #id id} values.
     * These values are stored in the special public attributes of this type.
     * <BR/>
     *
     * @param   domain      Value for the domain.
     * @param   server      Value for the server.
     * @param   type        Value for the type.
     * @param   id          Value for the id itself.
     */
    public OID (int domain, int server, int type, int id)
    {
        // create the new oid:
        this.oid[0] = (byte) domain;
        this.oid[1] = (byte) server;
        this.setDomain (domain);
        this.setServer (server);
        this.setType (type);
        this.setId (id);

        // set the new simple values:
        this.updateTVersionId ();
    } // OID


    /**************************************************************************
     * Creates an OID object. <BR/>
     * The compound object id is created from the {@link #domain domain},
     * {@link #server server}, {@link #type type}, and {@link #id id} values.
     * These values are stored in the special public attributes of this type.
     * <BR/>
     *
     * @param   tVersionId  Value for the tVersionId.
     * @param   id          Value for the id itself.
     */
    public OID (int tVersionId, int id)
    {
        // set the new values:
        this.setId (id);
        this.setTVersionId (tVersionId);
    } // OID


    /**************************************************************************
     * Get the simple values out of the compound oid and set them. <BR/>
     * The compound object id is used as base for getting the
     * {@link #domain domain}, {@link #server server}, {@link #type type}, and
     * {@link #id id} of the business object.
     * These values are stored in the special public attributes of this type.
     * <BR/>
     */
    private void updateSimpleValues ()
    {
        // get the simple values out of the compound oid:
        this.domain = this.oid[0] & 0xff;
        this.server = this.oid[1] & 0xff;
        this.type = ((this.oid[2] & 0xff) << 8) | (this.oid[3] & 0xff);
        // convert the byte values to hexadecimal without sign bit and
        // concatenate the values:
        this.id = (this.oid[4] & 0xff) << 24 |
                  (this.oid[5] & 0xff) << 16 |
                  (this.oid[6] & 0xff) << 8 |
                  (this.oid[7] & 0xff);
        this.updateTVersionId ();
    } // updateSimpleValues


    /**************************************************************************
     * Get the string value out of the compound oid and set it. <BR/>
     * The compound object id is used as base for getting the
     * {@link #p_strValue p_strValue} of the business object.
     * This value is stored in the special private attribute of this type. <BR/>
     */
    private void updateStringValue ()
    {
        this.p_strBuilderValue = new StringBuilder ()
            .append (UtilConstants.NUM_START_HEX)
            .append (Helpers.byteToString (this.oid[0]).toUpperCase ())
            .append (Helpers.byteToString (this.oid[1]).toUpperCase ())
            .append (Helpers.byteToString (this.oid[2]).toUpperCase ())
            .append (Helpers.byteToString (this.oid[3]).toUpperCase ())
            .append (Helpers.byteToString (this.oid[4]).toUpperCase ())
            .append (Helpers.byteToString (this.oid[5]).toUpperCase ())
            .append (Helpers.byteToString (this.oid[6]).toUpperCase ())
            .append (Helpers.byteToString (this.oid[7]).toUpperCase ());
        this.p_strValue = this.p_strBuilderValue.toString ();
        this.p_strBufValue = new StringBuffer (this.p_strValue);
    } // updateStringValue


    /**************************************************************************
     * Update the value for the {@link #tVersionId tVersionId}. <BR/>
     * This method is set depending onthe actual values of
     * {@link #domain domain}, {@link #server server},  {@link #type type}.
     */
    private void updateTVersionId ()
    {
        // convert the byte values to hexadecimal without sign bit and
        // concatenate the values:
        this.tVersionId = ((this.domain & 0xff) << 24) |
                          ((this.server & 0xff) << 16) |
                          (this.type & 0xffff);
    } // updateTVersionId


    /**************************************************************************
     * Set the value for the {@link #oid oid}. <BR/>
     * This method also sets all values depending on oid:
     * {@link #domain domain}, {@link #server server}, {@link #type type},
     * {@link #id id}, {@link #tVersionId tVersionId}.
     *
     * @param   oid         New value for the compound object id.
     */
    public void setOid (byte[] oid)
    {
        this.oid = oid;
        this.updateSimpleValues ();
    } // setOid


    /**************************************************************************
     * Set the value for the {@link #domain domain}. <BR/>
     * This method also sets all values depending on domain:
     * {@link #oid oid}, {@link #tVersionId tVersionId}.
     *
     * @param   domain      New value for the domain.
     */
    public void setDomain (int domain)
    {
        this.domain = domain;
        this.oid[0] = (byte) domain;
        this.updateTVersionId ();
    } // setDomain


    /**************************************************************************
     * Set the value for the {@link #server server}. <BR/>
     * This method also sets all values depending on server:
     * {@link #oid oid}, {@link #tVersionId tVersionId}.
     *
     * @param   server      New value for the server.
     */
    public void setServer (int server)
    {
        this.server = server;
        this.oid[1] = (byte) server;
        this.updateTVersionId ();
    } // setServer


    /**************************************************************************
     * Set the value for the {@link #type type}. <BR/>
     * This method also sets all values depending on type:
     * {@link #oid oid}, {@link #tVersionId tVersionId}.
     *
     * @param   type        New value for the type.
     */
    public void setType (int type)
    {
        this.type = type;
        this.oid[2] = (byte) ((type & 0xff00) >>> 8);
        this.oid[3] = (byte) (type & 0x00ff);
        this.updateTVersionId ();
    } // setType


    /**************************************************************************
     * Set the value for the {@link #id id}. <BR/>
     * This method also sets all values depending on id: {@link #oid oid}.
     *
     * @param   id        New value for the id.
     */
    public void setId (int id)
    {
        this.id = id;
        this.oid[4] = (byte) ((id & 0xff000000) >>> 24);
        this.oid[5] = (byte) ((id & 0x00ff0000) >>> 16);
        this.oid[6] = (byte) ((id & 0x0000ff00) >>> 8);
        this.oid[7] = (byte) (id & 0x000000ff);
    } // setId


    /**************************************************************************
     * Set the value for the {@link #tVersionId tVersionId}. <BR/>
     * This method also sets all values depending on tVersionId:
     * {@link #oid oid}, {@link #domain domain}, {@link #server server},
     * {@link #type type}.
     *
     * @param   tVersionId  New value for the id.
     */
    public void setTVersionId (int tVersionId)
    {
        this.tVersionId = tVersionId;
        this.oid[0] = (byte) ((tVersionId & 0xff000000) >>> 24);
        this.oid[1] = (byte) ((tVersionId & 0x00ff0000) >>> 16);
        this.oid[2] = (byte) ((tVersionId & 0x0000ff00) >>> 8);
        this.oid[3] = (byte) (tVersionId & 0x000000ff);
        this.updateSimpleValues ();
    } // setTVersionId


    /**************************************************************************
     * Check if the oid is empty. <BR/>
     * An oid is empty if all values are set to 0.
     *
     * @return  <CODE>true</CODE> if the oid is empty,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean isEmpty ()
    {
        // check if the oid is empty and return the result:
        return this.domain == 0 && this.server == 0 && this.type == 0 &&
               this.id == 0;
    } // isEmpty


    /**************************************************************************
     * Check if the oid is empty within a domain. <BR/>
     * An oid is empty if all values except the domain are set to 0.
     *
     * @return  <CODE>true</CODE> if the oid is empty,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean isEmptyInDomain ()
    {
        // check if the oid is empty and return the result:
        return this.server == 0 && this.type == 0 && this.id == 0;
    } // isEmptyInDomain


    /**************************************************************************
     * Check if the oid is temporary. <BR/>
     * An oid is temporary if the 9th nibble is <CODE>F</CODE>.
     *
     * @return  <CODE>true</CODE> if the oid is temporary,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean isTemp ()
    {
        // check if the oid is empty and return the result:
        return this.id == 0 || (this.oid[4] & 0xF0) == 0xF0;
    } // isTemp


    /**************************************************************************
     * Check if the oid denotes an object which is an instance of a specific
     * type version. <BR/>
     *
     * @param   tVersionId  The type version to be checked.
     *
     * @return  <CODE>true</CODE> if the oid is derived from the type version,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean isInstanceOf (int tVersionId)
    {
        // check if the tVersionId of the oid is identical to the defined
        // tVersionId and return the result:
        return this.tVersionId == tVersionId;
    } // isInstanceOf


    /**************************************************************************
     * Check if the oid denotes an object which is an instance of a specific
     * type. <BR/>
     *
     * @param   type    The type to be checked.
     *
     * @return  <CODE>true</CODE> if the oid is derived from the type,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean isInstanceOf (Type type)
    {
        // check if the tVersionId of the oid is identical to the defined
        // tVersionId and return the result:
        return this.isInstanceOf (type.getTVersionId ());
    } // isInstanceOf


    /**************************************************************************
     * Compares this OID to another OID. <BR/>
     * The result is <CODE>true</CODE> if the argument is not <CODE>null</CODE>
     * and is an OID object that represents the same sequence of bytes as this
     * object.
     * Furthermore the result is <CODE>true</CODE> if the argument is a String
     * or StringBuilder or StringBuffer which equals to the internal String
     * representation of this object.
     *
     * @param   obj     The OID object to compare this OID against.
     *
     * @return  <CODE>true</CODE> if the OIDs are equal;
     *          <CODE>false</CODE> otherwise.
     */
    public boolean equals (Object obj)
    {
        // check for null:
        if (obj == null)                // the other object is null?
        {
            return false;               // not equal
        } // if

        if (obj instanceof OID)         // OID object?
        {
            OID anotherOid = (OID) obj;

            // check for equality; compare tVersionId and id:
            return this.tVersionId == anotherOid.tVersionId &&
                    this.id == anotherOid.id;
/* KR just compare the tVersionId and the id because this is faster
            // check for equality; compare each byte:
            return (this.oid[0] == anotherOid.oid[0] &&
                    this.oid[1] == anotherOid.oid[1] &&
                    this.oid[2] == anotherOid.oid[2] &&
                    this.oid[3] == anotherOid.oid[3] &&
                    this.oid[4] == anotherOid.oid[4] &&
                    this.oid[5] == anotherOid.oid[5] &&
                    this.oid[6] == anotherOid.oid[6] &&
                    this.oid[7] == anotherOid.oid[7]);
*/
        } // if OID object
        else if (obj instanceof String) // String object?
        {
            return this.p_strValue != null &&
                   this.p_strValue.equalsIgnoreCase ((String) obj);
        } // else if String object
        else if (obj instanceof StringBuilder) // StringBuilder object?
        {
            return this.p_strValue != null &&
                this.p_strValue.equalsIgnoreCase (((StringBuilder) obj).toString ());
        } // else if StringBuilder object
        else if (obj instanceof StringBuffer) // StringBuffer object?
        {
            return this.p_strValue != null &&
                this.p_strValue.equalsIgnoreCase (((StringBuffer) obj).toString ());
        } // else if StringBuffer object
        else                            // no OID object
        {
            return false;               // not equal
        } // if no OID object
    } // equals


    /**************************************************************************
     * Returns the hexedecimal string representation of this OID. <BR/>
     * The components of the oid are concatenated to create a string
     * representation according to "0xddssttttiiiiiiii" where "dd" is the domain,
     * "ss" is the server, "tttt" the type and "iiiiiiii" represents the id of
     * the object itself.
     *
     * @return  Hexadecimal represention of the oid stored in a String.
     */
    public String toString ()
    {
        if (this.p_strValue == null)
        {
            // compute the string representation of the oid value and save it:
            this.updateStringValue ();
        } // if (this.p_strValue == null)

        // return the result:
        return this.p_strValue;
    } // toString


    /**************************************************************************
     * Returns the hexedecimal string representation of this OID. <BR/>
     * The components of the oid are concatenated to create a string
     * representation according to "0xddssttttiiiiiiii" where "dd" is the domain,
     * "ss" is the server, "tttt" the type and "iiiiiiii" represents the id of
     * the object itself.
     *
     * @return  Hexadecimal represention of the oid stored in a String.
     */
    public StringBuilder toStringBuilder ()
    {
        if (this.p_strBuilderValue == null)
        {
            // compute the string representation of the oid value and save it:
            this.updateStringValue ();
        } // if

        // return the result:
        return this.p_strBuilderValue;
    } // toStringBuilder


    /**************************************************************************
     * Returns the hexedecimal string representation of this OID. <BR/>
     * The components of the oid are concatenated to create a string
     * representation according to "0xddssttttiiiiiiii" where "dd" is the domain,
     * "ss" is the server, "tttt" the type and "iiiiiiii" represents the id of
     * the object itself.
     *
     * @return  Hexadecimal represention of the oid stored in a String.
     *
     * @deprecated  As of Java release JDK 5, an equivalent class was introduced
     *              which is only single-threaded and thus more performant:
     *              StringBuilder. This class shall be used now.
     *              Use {@link #toStringBuilder} instead.
     */
    public StringBuffer toStringBuffer ()
    {
        if (this.p_strBufValue == null)
        {
            // compute the string representation of the oid value and save it:
            this.updateStringValue ();
        } // if

        // return the result:
        return this.p_strBufValue;
    } // toStringBuffer


    /**************************************************************************
     * Returns the database specific string representation of this oid. <BR/>
     *
     * ORACLE:
     * Returns the string representation of this OID. <BR/>
     * The components of the oid are concatenated to create a string
     * representation according to "'ddssttttiiiiiiii'" where "dd" is the domain,
     * "ss" is the server, "tttt" the type and "iiiiiiii" represents the id of
     * the object itself.<P>
     *
     * SQL-SERVER:
     * Returns the hexedecimal string representation of this OID. <BR/>
     * The components of the oid are concatenated to create a string
     * representation according to "0xddssttttiiiiiiii" where "dd" is the domain,
     * "ss" is the server, "tttt" the type and "iiiiiiii" represents the id of
     * the object itself.<P>
     *
     * This method is needed if oid is used in Select - Statement - String
     * implemented in JAVA
     *
     * @return  The database specific string representation of the oid.
     */
    public String toStringQu ()
    {
        return this.toStringQu (false);
    } // toStringQu


    /**************************************************************************
     * Returns the database specific string representation of this oid.<BR/>
     * A conversion to a db-specific OID can be forced using the
     * forceConversion paramter.<BR/>
     *
     * ORACLE:
     * Returns the string representation of this OID. <BR/>
     * The components of the oid are concatenated to create a string
     * representation according to "'ddssttttiiiiiiii'" where "dd" is the domain,
     * "ss" is the server, "tttt" the type and "iiiiiiii" represents the id of
     * the object itself.<P>
     *
     * SQL-SERVER:
     * Returns the hexedecimal string representation of this OID. <BR/>
     * The components of the oid are concatenated to create a string
     * representation according to "0xddssttttiiiiiiii" where "dd" is the domain,
     * "ss" is the server, "tttt" the type and "iiiiiiii" represents the id of
     * the object itself.<P>
     *
     * This method is needed if oid is used in Select - Statement - String
     * implemented in JAVA
     *
     * @param forceConversion   force a conversion of the string to an OID
     *
     * @return  The database specific string representation of the oid.
     */
    public String toStringQu (boolean forceConversion)
    {
        // compute the string representation of the oid value and return it:
        return SQLHelpers.stringToDbBinary (
            Helpers.byteToString (this.oid[0]) +
            Helpers.byteToString (this.oid[1]) +
            Helpers.byteToString (this.oid[2]) +
            Helpers.byteToString (this.oid[3]) +
            Helpers.byteToString (this.oid[4]) +
            Helpers.byteToString (this.oid[5]) +
            Helpers.byteToString (this.oid[6]) +
            Helpers.byteToString (this.oid[7]),
            forceConversion
        );
    } // toStringQu


    /**************************************************************************
     * Returns the database specific string representation of this oid. <BR/>
     *
     * ORACLE:
     * Returns the string representation of this OID. <BR/>
     * The components of the oid are concatenated to create a string
     * representation according to "'ddssttttiiiiiiii'" where "dd" is the domain,
     * "ss" is the server, "tttt" the type and "iiiiiiii" represents the id of
     * the object itself.<P>
     *
     * SQL-SERVER:
     * Returns the hexedecimal string representation of this OID. <BR/>
     * The components of the oid are concatenated to create a string
     * representation according to "0xddssttttiiiiiiii" where "dd" is the domain,
     * "ss" is the server, "tttt" the type and "iiiiiiii" represents the id of
     * the object itself.<P>
     *
     * This method is needed if oid is used in Select - Statement - String
     * implemented in JAVA
     *
     * @return  The database specific string representation of the oid.
     */
    public StringBuilder toStringBuilderQu ()
    {
        // compute the string representation of the oid value and return it:
        return new StringBuilder ().append (this.toStringQu ());
    } // toStringBuilderQu


    /**************************************************************************
     * Returns the database specific string representation of this oid. <BR/>
     *
     * ORACLE:
     * Returns the string representation of this OID. <BR/>
     * The components of the oid are concatenated to create a string
     * representation according to "'ddssttttiiiiiiii'" where "dd" is the domain,
     * "ss" is the server, "tttt" the type and "iiiiiiii" represents the id of
     * the object itself.<P>
     *
     * SQL-SERVER:
     * Returns the hexedecimal string representation of this OID. <BR/>
     * The components of the oid are concatenated to create a string
     * representation according to "0xddssttttiiiiiiii" where "dd" is the domain,
     * "ss" is the server, "tttt" the type and "iiiiiiii" represents the id of
     * the object itself.<P>
     *
     * This method is needed if oid is used in Select - Statement - String
     * implemented in JAVA
     *
     * @return  The database specific string representation of the oid.
     *
     * @deprecated  As of Java release JDK 5, an equivalent class was introduced
     *              which is only single-threaded and thus more performant:
     *              StringBuilder. This class shall be used now.
     *              Use {@link #toStringBuilderQu} instead.
     */
    public StringBuffer toStringBufferQu ()
    {
        // compute the string representation of the oid value and return it:
        return new StringBuffer (this.toStringQu ());
    } // toStringBufferQu


    /**************************************************************************
     * Parse a String containing the hexadecimal representation of an oid to get
     * the oid. <BR/>
     * The input value must be of the format "0xddssttttiiiiiiii" where "dd" is
     * the domain as 1 byte, "ss" is the server as 1 byte, "tttt" is the 2 byte
     * type version and the 4 bytes "iiiiiiii" represent the id of the object
     * itself. <BR/>
     * The input string value is stored within {@link #p_strValue p_strValue}.
     * <BR/>
     * The method returns an oid value containing the oid which results after
     * parsing the string.
     *
     * @param   oidStr      String representation of the object id.
     *
     * @return  Parsed oid value.
     *
     * @exception   IncorrectOidException
     *              Is raised if the parameter does not result in a 8 byte id.
     */
    private byte [] parseString (String oidStr) throws IncorrectOidException
    {
        String oidStrLocal = oidStr.toUpperCase (); // variable for local assignments

        if (oidStrLocal.startsWith (UtilConstants.NUM_START_HEX_UPPER))
        {
            oidStrLocal = UtilConstants.NUM_START_HEX +
                oidStrLocal.substring (2);
        } // if
        else
        {
            oidStrLocal = UtilConstants.NUM_START_HEX + oidStrLocal;
        } // else

        // check if the length of the oid is correct and the value is valid:
        if (oidStrLocal.length () != OID.OIDLENGTH ||
            // 20090911/20100827 BT/BB/KR/RB:
            // The incorrect OID 0xF0F0F0F0F0F0F0F0 can occur within
            // the database for value domain and fieldref fields.
            // see IBS-254
            oidStrLocal.substring (2).equals ("F0F0F0F0F0F0F0F0"))
        {
            IncorrectOidException error =
                new IncorrectOidException (this.ERR_TYPE + " \'" + oidStrLocal + "\'");
            throw error;
        } // if

        // compute the value of the oid:
        byte [] oidVal =
        {
            Helpers.stringToByte ("" + oidStrLocal.charAt (2) + oidStrLocal.charAt (3)),
            Helpers.stringToByte ("" + oidStrLocal.charAt (4) + oidStrLocal.charAt (5)),
            Helpers.stringToByte ("" + oidStrLocal.charAt (6) + oidStrLocal.charAt (7)),
            Helpers.stringToByte ("" + oidStrLocal.charAt (8) + oidStrLocal.charAt (9)),
            Helpers.stringToByte ("" + oidStrLocal.charAt (10) + oidStrLocal.charAt (11)),
            Helpers.stringToByte ("" + oidStrLocal.charAt (12) + oidStrLocal.charAt (13)),
            Helpers.stringToByte ("" + oidStrLocal.charAt (14) + oidStrLocal.charAt (15)),
            Helpers.stringToByte ("" + oidStrLocal.charAt (16) + oidStrLocal.charAt (17)),
        };

        // store the string value:
        this.p_strValue = oidStrLocal;
        this.p_strBuilderValue = new StringBuilder ().append (this.p_strValue);
        this.p_strBufValue = new StringBuffer ().append (this.p_strValue);

        return oidVal;                  // return the computed oid value
    } // parseString


    /**************************************************************************
     * Parse a String buffer containing the hexadecimal representation of an
     * oid to get the oid. <BR/>
     * The input value must be of the format "0xddssttttiiiiiiii" where "dd" is
     * the domain as 1 byte, "ss" is the server as 1 byte, "tttt" is the 2 byte
     * type version and the 4 bytes "iiiiiiii" represent the id of the object
     * itself. <BR/>
     * The input string value is stored within {@link #p_strValue p_strValue}.
     * <BR/>
     * The method returns an oid value containing the oid which results after
     * parsing the string.
     *
     * @param   oidStrBuf   String representation of the object id.
     *
     * @return  Parsed oid value.
     *
     * @exception   IncorrectOidException
     *              Is raised if the parameter does not result in a 8 byte id.
     *
     * @deprecated  As of Java release JDK 5, an equivalent class was introduced
     *              which is only single-threaded and thus more performant:
     *              StringBuilder. This class shall be used now.
     *              Use {@link #parseString(StringBuilder)} instead.
     */
    private byte [] parseString (StringBuffer oidStrBuf)
        throws IncorrectOidException
    {
        if (oidStrBuf == null)
        {
            IncorrectOidException error =
                new IncorrectOidException (this.ERR_TYPE + " \'" + oidStrBuf + "\'");
            throw error;
        } // if

        // compute the oid value and return the result:
        return this.parseString (oidStrBuf.toString ());
    } // parseString


    /**************************************************************************
     * Parse a String buffer containing the hexadecimal representation of an
     * oid to get the oid. <BR/>
     * The input value must be of the format "0xddssttttiiiiiiii" where "dd" is
     * the domain as 1 byte, "ss" is the server as 1 byte, "tttt" is the 2 byte
     * type version and the 4 bytes "iiiiiiii" represent the id of the object
     * itself. <BR/>
     * The input string value is stored within {@link #p_strValue p_strValue}.
     * <BR/>
     * The method returns an oid value containing the oid which results after
     * parsing the string.
     *
     * @param   oidStrBuilder   String representation of the object id.
     *
     * @return  Parsed oid value.
     *
     * @exception   IncorrectOidException
     *              Is raised if the parameter does not result in a 8 byte id.
     */
    private byte [] parseString (StringBuilder oidStrBuilder)
        throws IncorrectOidException
    {
        if (oidStrBuilder == null)
        {
            IncorrectOidException error =
                new IncorrectOidException (this.ERR_TYPE + " \'" + oidStrBuilder + "\'");
            throw error;
        } // if

        // compute the oid value and return the result:
        return this.parseString (oidStrBuilder.toString ());
    } // parseString


    /**************************************************************************
     * Returns a hash code value for the object. This method is supported for
     * the benefit of hashtables such as those provided by java.util.Hashtable.
     * The general contract of hashCode is: <BR/>
     * Whenever it is invoked on the same object more than once during an
     * execution of a Java application, the hashCode method must consistently
     * return the same integer. This integer need not remain consistent from
     * one execution of an application to another execution of the same
     * application. <BR/>
     * If two objects are equal according to the equals method, then calling
     * the hashCode method on each of the two objects must produce the same
     * integer result.
     *
     * @return  A hash code value for this object.
     *
     * @see java.util.Hashtable
     */
    public int hashCode ()
    {
        // convert the byte values to hexadecimal without sign bit and
        // concatenate the values:
        return (this.oid[2] & 0xff) << 24 |
               (this.oid[3] & 0xff) << 16 |
               (this.oid[6] & 0xff) << 8 |
               (this.oid[7] & 0xff);
    } // hashCode


    /**************************************************************************
     * Creates and returns a copy of this object. <BR/>
     * For any object <tt>x</tt>, the following expressions will be
     * <tt>true</tt>:
     * <blockquote><pre>
     * x.clone () != x
     * x.clone ().getClass () == x.getClass ()
     * x.clone ().equals (x)
     * </pre></blockquote>
     * The object returned by this method is independent of this object (which
     * is being cloned).
     *
     * @return  A clone of this instance. <BR/>
     *
     * @throws  OutOfMemoryError
     *          If there is not enough memory.
     *
     * @see java.lang.Cloneable
     */
    public Object clone () throws OutOfMemoryError
    {
        OID obj = null;                 // the new object

        try
        {
            // call corresponding method of super class:
            obj = (OID) super.clone ();

            // set specific properties:
            // because the clone method of {@link java.lang.Object Object}
            // performs a shallow and not a deep copy of all existing properties
            // we have to perform the deep copy here to ensure that there are
            // no side effects.
            obj.setOid (
                new byte[]
                {
                    this.oid[0], this.oid[1],
                    this.oid[2], this.oid[3],
                    this.oid[4], this.oid[5],
                    this.oid[6], this.oid[7],
                });

            obj.p_strValue = null;
            obj.p_strBufValue = null;
            obj.p_strBuilderValue = null;
        } // try
        catch (CloneNotSupportedException e)
        {
            return e;
        } // catch CloneNotSupportedException
        catch (OutOfMemoryError e)
        {
            // should not occur
            IOHelpers.printError ("Error when cloning oid", e, true);
        } // catch OutOfMemoryError

        // return the new object:
        return obj;
    } // clone

} // class OID
