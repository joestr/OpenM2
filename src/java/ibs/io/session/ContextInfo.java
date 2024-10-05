/**
 * Class: ContextInfo.java
 */

// package:
package ibs.io.session;

// imports:
import ibs.BaseObject;

import java.util.Date;


/******************************************************************************
 * This is the ContextInfo Object, which holds all context-relevant
 * information
 *
 * @version     $Id: ContextInfo.java,v 1.5 2007/07/20 13:07:56 kreimueller Exp $
 *
 * @author        Christine Keim  (CK)    980304
 ******************************************************************************
 */
public class ContextInfo extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ContextInfo.java,v 1.5 2007/07/20 13:07:56 kreimueller Exp $";


    /**
     * login. Logintime, beginning of the session. <BR/>
     */
    public Date login;
    /**
     * browser. Browsertype and version. <BR/>
     */
    public String browser;


    /**************************************************************************
     * Create a new instance representing Information about the Context. <BR/>
     * The property <A HREF="#login">login</A> is set to the current date.
     * All the other properties are set to null.
     */
    public ContextInfo ()
    {
        this.login = new Date ();
        this.browser = null;
    } // ContextInfo

} // class ContextInfo
