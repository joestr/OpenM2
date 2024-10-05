/*
* Class m2XDiffMessagess.java
*/

// package:
package ibs.di.diff;

// imports:


/******************************************************************************
 * Error Message Constants for m2XDiff. <BR/>
 * This abstract class contains all error tokens which are necessary to deal with
 * the command line arguments for m2XDiff class. <BR/>
 *
 * @version     $Id: m2XDiffMessages.java,v 1.5 2007/07/31 19:13:54 kreimueller Exp $
 *
 * @author      CHINNI RANJITH KUMAR
 ******************************************************************************
 */
public abstract class m2XDiffMessages
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: m2XDiffMessages.java,v 1.5 2007/07/31 19:13:54 kreimueller Exp $";


    /**
     * Message when no old import xml file has been defined. <BR/>
     */
    public static final String MISSING_OLD = "Old file name missing in the command line arguments";

    /**
     * Message when no new import xml file has been defined. <BR/>
     */
    public static final String MISSING_NEW = "New file name missing in the command line arguments";

    /**
     * Message when no resultant import xml file has been defined. <BR/>
     */
    public static final String MISSING_OUT = "Out file name missing in the command line arguments";

    /**
     * Message when the import file doesn't contain .xml extension. <BR/>
     */
    public static final String MISSING_EXTENSION = ".xml extension missing for the file";

    /**
     * Message when specified import file doesn't exist. <BR/>
     */
    public static final String MISSING_FILE = "Specified file does not exist";

    /**
     * Message when Document root is missing or cannot be created. <BR/>
     */
    public static final String MISSING_DOCROOT = "Document root can not be created";

    /**
     * Message when IMPORT tag missing in the import file. <BR/>
     */
    public static final String MISSING_IMPORT = "IMPORT tag missing in the import file";

    /**
     * Message when no child nodes found in the import file for IMPORT tag. <BR/>
     */
    public static final String MISSING_CHILDS = "No child nodes found for IMPORT tag";

    /**
     * Message when no child nodes found in the import file for OBJECTS tag. <BR/>
     */
    public static final String MISSING_OBJECTS = "No child nodes found for OBJECTS tag";

    /**
     * Message when specified import file names are invalid. <BR/>
     */
    public static final String WRONG_FILE_NAMES = "File names are not valid";

    /**
     * Message when Document root is null. <BR/>
     */
    public static final String DOC_NULL = "Document is null result file can not be created";

    /**
     * Message when wrong command line parameters are entered. <BR/>
     */
    public static final String WRONG_PARAMS = "Wrong parameters";

    /**
     * Message when no there are no command line parameters.
     */
    public static final String NO_PARAMS = "No command line parameters";

} // m2XDiffMessages
