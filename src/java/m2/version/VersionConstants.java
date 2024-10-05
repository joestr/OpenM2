/*
 * Class: VersionConstants.java
 */

// package:
package m2.version;

// imports:


/******************************************************************************
 * Constants for m2. <BR/>
 * This abstract class contains all constants which are necessary to deal with
 * the classes delivered within this package.<P>
 * Contains application defined object types and derived types with IDs from
 * 0x0101 to 0xFFFF. <BR/>
 *
 * @version     $Id: VersionConstants.java,v 1.13 2007/07/10 21:20:20 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 980204
 ******************************************************************************
 */
public abstract class VersionConstants extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: VersionConstants.java,v 1.13 2007/07/10 21:20:20 kreimueller Exp $";


    /**
     * Type code of version. <BR/>
     */
    protected static final String TC_VERSION =
            new String ("m2Version");

} // class VersionConstants
