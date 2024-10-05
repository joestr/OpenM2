/**
 * Class: PublicationMessages
 */

// package:
package m2.version.publish;

// imports:
import ibs.util.UtilConstants;


/******************************************************************************
 * Konstanten fuer die Fehlermeldungen, welche bei der Publikationsfunktion
 * auftreten koennen. <BR/>
 *
 * @version     $Id: PublicationMessages.java,v 1.4 2007/07/23 08:21:37 kreimueller Exp $
 *
 * @author      Bernd Martin (BM) Dec 7, 2001
 ******************************************************************************
 */
public class PublicationMessages extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: PublicationMessages.java,v 1.4 2007/07/23 08:21:37 kreimueller Exp $";


    /**
     * The message which is displayed when the publication container cannot
     * be found. <BR/>
     */
    public static String MSG_PUBLICATIONCONTAINERNOTFOUND =
        "Publikationscontainer (OID " + UtilConstants.TAG_OID +
        ") konnte nicht gefunden werden!";

    /**
     * Partial message to be appended if something could not be found. <BR/>
     */
    public static String MSG_NOT_FOUND = " konnte nicht gefunden werden.";

    /**
     * The message which is displayed when no publication object is found. <BR/>
     */
    public static String MSG_PUBLICATIONOBJECTNOTFOUND =
        "Das Publikationsobjekt (OID " + UtilConstants.TAG_OID + ")" +
        PublicationMessages.MSG_NOT_FOUND;

    /**
     * The message which is displayed when no main publication object is found. <BR/>
     */
    public static String MSG_FILEPUBLICATIONOBJECTNOTFOUND =
        "Das Haupt-Publikationsobjekt (OID " + UtilConstants.TAG_OID + ")" +
        PublicationMessages.MSG_NOT_FOUND;

    /**
     * The message which is displayed when a transformation error occurs. <BR/>
     */
    public static String MSG_TRANSFORMATIONERROR =
        "Transformationsfehler: " + UtilConstants.TAG_NAME + " " +
        UtilConstants.TAG_NAME2;

    /**
     * Message to be shown if an object was published succsesfully. <BR/>
     */
    public static String MSG_OBJECTPUBLISHED = "Objekt wurde publiziert!";

} // class PublicationMessages
