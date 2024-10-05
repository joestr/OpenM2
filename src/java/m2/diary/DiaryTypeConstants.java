/*
 * Class: DiaryTypeConstants.java
 */

// package:
package m2.diary;

// imports:


/*******************************************************************************
 * Types for m2. <BR/>
 * This abstract class contains all types which are necessary to deal with
 * the classes delivered within this package. <BR/>
 *
 * @version     $Id: DiaryTypeConstants.java,v 1.20 2007/07/23 08:21:36 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 980529
 *******************************************************************************
 */
public abstract class DiaryTypeConstants extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DiaryTypeConstants.java,v 1.20 2007/07/23 08:21:36 kreimueller Exp $";


    // type codes:
    /**
     * Code of type OverlapContainer. <BR/>
     */
    public static String TC_OverlapContainer = "OverlapContainer";

    /**
     * Code of type Participant. <BR/>
     */
    public static String TC_Participant = "Participant";

} // class DiaryTypeConstants
