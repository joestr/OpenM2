/**
 * Class: ConversionMessages
 */

// package:
package m2.version.publish.converter;

// imports:
import ibs.bo.BOMessages;


/******************************************************************************
 * The class which contains error messages for the conversion process. <BR/>
 *
 * @version     $Id: ConversionMessages.java,v 1.4 2010/04/07 13:37:05 rburgermann Exp $
 *
 * @author      Bernd Martin (BM) Dec 10, 2001
 ******************************************************************************
 */
public class ConversionMessages
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ConversionMessages.java,v 1.4 2010/04/07 13:37:05 rburgermann Exp $";

    /**
     * Name of bundle where the messages are included. <BR/>
     */
    public static String MSG_BUNDLE = BOMessages.MSG_BUNDLE;

    /**
     * The message when an error occurred while converting a file. <BR/>
     */
    public static final String ML_MSG_CONVERSIONFAILED = "ML_MSG_CONVERSIONFAILED";

} // class ConversionMessages
