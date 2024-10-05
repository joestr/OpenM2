/*
 * Class: BbdMessages.java
 */

// package:
package m2.bbd;

// imports:
import ibs.util.UtilConstants;


/******************************************************************************
 * Messages for m2. <BR/>
 * This abstract class contains all messages which are necessary to deal with
 * the classes delivered within this package. <P>
 * The messages can use tags for specific values to be inserted at runtime.
 * This tags can be replaced by the values with the
 * <A HREF="Helpers.html#replace">replace</A> function.
 *
 * @version     $Id: BbdMessages.java,v 1.6 2010/04/07 13:37:10 rburgermann Exp $
 *
 * @author      Keim Christine (CK), 980730
 ******************************************************************************
 */
public abstract class BbdMessages extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: BbdMessages.java,v 1.6 2010/04/07 13:37:10 rburgermann Exp $";

    /**
     * Name of bundle where the messages are included. <BR/>
     */
    public static String MSG_BUNDLE = "m2_m2bbd_messages";

    // application specific messages:
    /**
     * Message displayed when a discussion is empty. <BR/>
     */
    public static String ML_MSG_DISCUSSIONEMPTY = "ML_MSG_DISCUSSIONEMPTY";

    /**
     * Message displayed when a discussion is empty. <BR/>
     */
    public static String ML_MSG_BLACKBOARDEMPTY = "ML_MSG_BLACKBOARDEMPTY";

} // class BbdsMessages
