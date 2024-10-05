/*
* Class m2XDiffConstants.java
*/

// package:
package ibs.di.diff;

// imports:


/******************************************************************************
 * Constants for m2XDiff. <BR/>
 * This abstract class contains all tokens which are necessary to deal with
 * the command line arguments for m2XDiff class. <BR/>
 *
 * @version     $Id: m2XDiffConstants.java,v 1.9 2009/12/18 10:26:05 btatzmann Exp $
 *
 * @author      CHINNI RANJITH KUMAR
 ******************************************************************************
 */
public abstract class m2XDiffConstants extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: m2XDiffConstants.java,v 1.9 2009/12/18 10:26:05 btatzmann Exp $";


    // m2XDiff command line arguments.
    /**
     * m2XDiff command line argument: help. <BR/>
     */
    public static final String ARG_HELP     = "-?";
    /**
     * m2XDiff command line argument: old. <BR/>
     */
    public static final String ARG_OLD      = "-old";
    /**
     * m2XDiff command line argument: new. <BR/>
     */
    public static final String ARG_NEW      = "-new";
    /**
     * m2XDiff command line argument: out. <BR/>
     */
    public static final String ARG_OUT      = "-out";
    /**
     * m2XDiff command line argument: grouped. <BR/>
     */
    public static final String ARG_GROUPED  = "-grouped";
    /**
     * m2XDiff command line argument: rights. <BR/>
     */
    public static final String ARG_RIGHTS   = "-rights";
    /**
     * m2XDiff command line argument: references. <BR/>
     */
    public static final String ARG_REFERENCES = "-references";
    /**
     * m2XDiff command line argument: deleted. <BR/>
     */
    public static final String ARG_DELETED  = "-deleted";
    /**
     * m2XDiff command line argument: unique. <BR/>
     */
    public static final String ARG_UNIQUE   = "-unique";
    /**
     * m2XDiff command line argument: debug. <BR/>
     */
    public static final String ARG_DEBUG    = "-debug";

    /**
     * Constant for resulting file extension. <BR/>
     */
    public static final String EXTENSION_NEW = "_new.xml";

    /**
     * Constant for resulting file extension. <BR/>
     */
    public static final String EXTENSION_DEL = "_del.xml";

    /**
     * Constant for file extension. <BR/>
     */
    public static final String EXTENSION_XML = ".xml";

    /**
     * Constant for version. <BR/>
     */
    public static final String VERSION = "1.0";

    /**
     * Constant holding type name of object.
     */
    public static final String GERMAN_TYPE = "Gruppe";

    /**
     * Constant holding type name of object.
     */
    public static final String ENGLISH_TYPE = "Group";

} // m2XDiffConstants
