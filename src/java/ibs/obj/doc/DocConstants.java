/*
 * Class: DocConstants.java
 */

// package:
package ibs.obj.doc;

// imports:


/******************************************************************************
 * Constants for documents. <BR/>
 *
 * @version     $Id: DocConstants.java,v 1.3 2009/07/21 11:54:25 kreimueller Exp $
 *
 * @author      Klaus, 15.10.2003
 ******************************************************************************
 */
public abstract class DocConstants extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DocConstants.java,v 1.3 2009/07/21 11:54:25 kreimueller Exp $";


    /**
     * A kind of attachment. <BR/>
     */
    public static final short ATT_FILE = 1;

    /**
     * A kind of attachment. <BR/>
     */
    public static final short ATT_HYPERLINK = 2;

    /**
     * A kind of attachment. <BR/>
     */
    public static final short ATT_SHEET = 2;

    /**
     * argumentname for the content of an entry. <BR/>
     */
    public static final String ARG_CONTENT       = "cont";

} // class DocConstants
