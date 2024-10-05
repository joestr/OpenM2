/*
 * Class: BbdTypeConstants.java
 */

// package:
package m2.bbd;

// imports:


/*******************************************************************************
 * Types for m2. <BR/>
 * This abstract class contains all types which are necessary to deal with
 * the classes delivered within this package. <BR/>
 *
 * @version     $Id: BbdTypeConstants.java,v 1.20 2007/07/23 08:21:29 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 980529
 *******************************************************************************
 */
public abstract class BbdTypeConstants extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: BbdTypeConstants.java,v 1.20 2007/07/23 08:21:29 kreimueller Exp $";


    /**
     * Code of type BlackBoard. <BR/>
     */
    public static String TC_BlackBoard = "BlackBoard";

    /**
     * Code of type Discussion. <BR/>
     */
    public static String TC_Discussion = "Discussion";

    /**
     * Code of type XMLDiscussion. <BR/>
     */
    public static String TC_XMLDiscussion = "XMLDiscussion";

    /**
     * Code of type DiscXMLViewer. <BR/>
     */
    public static String TC_DiscXMLViewer = "DiscXMLViewer";

    /**
     * Code of type Thread. <BR/>
     */
    public static String TC_Thread = "Thema";

    /**
     * Code of type Entry. <BR/>
     */
    public static String TC_DiscEntry = "Beitrag";

} // class BbdTypeConstants
