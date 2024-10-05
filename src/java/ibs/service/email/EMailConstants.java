/*
 * Class: EMailConstants.java
 */

// package:
package ibs.service.email;

// imports:


/******************************************************************************
 * Constants for ibs.util.email business objects. <BR/>
 * This abstract class contains all tokens which are necessary to deal with
 * the business objects delivered within this package.
 *
 * @version     $Id: EMailConstants.java,v 1.6 2012/09/18 14:47:50 btatzmann Exp $
 *
 * @author      Bernd Buchegger (BB), 990105
 ******************************************************************************
 */
public abstract class EMailConstants extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: EMailConstants.java,v 1.6 2012/09/18 14:47:50 btatzmann Exp $";


    /**
     * email content type plain text. <BR/>
     */
    public static final int CONTENT_TYPE_PLAIN_TEXT = 0;
    
    /**
     * email content type text/html. <BR/>
     */
    public static final int CONTENT_TYPE_TEXT_HTML = 1;

} // class EMailConstants
