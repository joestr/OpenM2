/*
 * Class: DocTypeConstants.java
 */

// package:
package m2.doc;

// imports:
import ibs.bo.type.TypeConstants;


/*******************************************************************************
 * Types for m2. <BR/>
 * This abstract class contains all types which are necessary to deal with
 * the classes delivered within this package. <BR/>
 *
 * @version     $Id: DocTypeConstants.java,v 1.20 2007/07/23 08:21:36 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 980529
 *******************************************************************************
 */
public abstract class DocTypeConstants extends TypeConstants
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DocTypeConstants.java,v 1.20 2007/07/23 08:21:36 kreimueller Exp $";


    // type codes:
    /**
     * Code of type Document. <BR/>
     */
    public static String TC_Document = "Dokument";

} // class DocTypeConstants
