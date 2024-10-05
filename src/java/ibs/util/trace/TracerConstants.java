/*
 * Class: TracerConstants.java
 */

// package:
package ibs.util.trace;

// imports:
import ibs.util.UtilConstants;


/******************************************************************************
 * This abstract class contains the constants but also some variables
 * used in the tracing mechanism.
 *
 * @version     $Id: TracerConstants.java,v 1.9 2007/07/31 19:14:01 kreimueller Exp $
 *
 * @author      Bernd Martin (BM)  001120
 ******************************************************************************
 */
public abstract class TracerConstants extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: TracerConstants.java,v 1.9 2007/07/31 19:14:01 kreimueller Exp $";


    /**
     * Default port of the trace server. <BR/>
     * This port is used if there is no other specified.
     */
    public static final int TRACESERVERPORT_DEFAULT = 1733;

    /**
     * Default path for trace files. <BR/>
     */
    public static final String TRACERPATH_DEFAULT = "c:\\Inetpub\\wwwroot\\m2\\debug\\";

    /**
     * The hashalgorithm used for the hashfunction in the
     * authentication. <BR/>
     * Possible values are:. <BR/>
     * - MD5
     * - SHA-1
     */
    protected static final String HASHALG = "MD5";

    /**
     * The name of the protocol version used in the
     * authentication process. <BR/>
     */
    protected static final String VERSION10 = "V1.0";

    /**
     * Default host name of the server. <BR/>
     * This host name is used if there is no other name specified.
     */
    public static final String TRACESERVER_DEFAULT = "localhost";

    /**
     * Default password for connecting to server. <BR/>
     * This password is used if there is no other password specified. <BR/>
     */
    public static final byte[] TRACERPASSWORD_DEFAULT = "myPwd73".getBytes ();

    /**
     * Message which is sent to the client if there are too many messages. <BR/>
     * The tag <A HREF="UtilConstants.html#TAG_NUMBER">UtilConstants.TAG_NUMBER</A>
     * is used to represent the number of discarded messages.
     */
    public static String MSG_TOOMANYMESSAGES =
        "There are too many messages which should be sent to the client. " +
        UtilConstants.TAG_NUMBER + " messages discarded.";


    /**
     * The host name of tracerserver. <BR/>
     * Default: <CODE>""</CODE> (empty)
     */
    public static String traceServer = "";

    /**
     * The port of the tracerserver. <BR/>
     * Default: <CODE>-1</CODE>
     */
    public static volatile int traceServerPort = -1;
//    public static volatile int traceServerPort = -1;

    /**
     * Default . <BR/>
     * This password is used to authenticate the clients. <BR/>
     * It is read from the configuration file ibssystem.cfg. <BR/>
     * Default: <CODE>TRACERPASSWORD_DEFAULT</CODE>
     */
    public static byte[] password = TracerConstants.TRACERPASSWORD_DEFAULT;

    /**
     * Maximum number of messages to be sent to the clients. <BR/>
     * If the current number of messages exceeds this value the other messages
     * are discarded and a message is send to the client. <BR/>
     * Default: <CODE>100</CODE>
     */
    public static int maxMessages = 100;

} // class TracerConstants
