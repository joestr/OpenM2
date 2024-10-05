/*
 * Class: WeblinkInfo.java
 */

// package:
package ibs.util;

// imports:
//KR TODO: unsauber
import ibs.BaseObject;


/******************************************************************************
 * This class defines the configuration properties. <BR/>
 *
 * @version     $Id: WeblinkInfo.java,v 1.6 2007/07/10 09:24:29 kreimueller Exp $
 *
 * @author      Christine Keim (CK) 000903
 ******************************************************************************
 */
public class WeblinkInfo extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: WeblinkInfo.java,v 1.6 2007/07/10 09:24:29 kreimueller Exp $";


    /**
     * userId of the Weblink
     */
    public int userId  = -1;

    /**
     * hash of the Weblink
     */
    public String hash  = null;

    /**
     * domainId of the Weblink
     */
    public int domain  = -1;

} // class WeblinkInfo
