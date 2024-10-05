/*
 * Class: IbsGlobals.java
 */

// package:
package ibs;

//imports:
import ibs.io.session.ApplicationInfo;


/******************************************************************************
 * Global properties for ibs. <BR/>
 * This abstract class contains all properties which are available to all ibs
 * objects.
 *
 * @version     $Id: IbsGlobals.java,v 1.1 2009/08/28 11:45:01 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 28.08.2009
 ******************************************************************************
 */
public abstract class IbsGlobals extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: IbsGlobals.java,v 1.1 2009/08/28 11:45:01 kreimueller Exp $";


    /**
     * The global application info object. <BR/>
     * This object is set once during application intialization and never
     * changed lateron.
     */
    public static ApplicationInfo p_app = null;

} // class IbsGlobals
