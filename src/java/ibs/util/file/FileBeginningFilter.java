/*
 * Class: FileBeginningFilter.java
 */

// package:
package ibs.util.file;

// imports:
import java.io.File;
import java.io.FilenameFilter;


/******************************************************************************
 * Common filter for file beginning. <BR/>
 * This filter only checks the beginning of a file.
 *
 * @version     $Id: FileBeginningFilter.java,v 1.2 2007/07/10 09:04:13 kreimueller Exp $
 *
 * @author      Klaus, 30.12.2003
 ******************************************************************************
 */
public class FileBeginningFilter extends Object implements FilenameFilter
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: FileBeginningFilter.java,v 1.2 2007/07/10 09:04:13 kreimueller Exp $";


    /**
     * The filter for the file beginning. <BR/>
     */
    private String p_fileBeginning = null;



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a FileExtensionFilter object. <BR/>
     *
     * @param   fileBeginning   The file beginning.
     */
    protected FileBeginningFilter (String fileBeginning)
    {
        // set properties:
        this.p_fileBeginning = fileBeginning;
    } // FileBeginningFilter


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Tests if a specified file should be included in a file list. <BR/>
     *
     * @param   dir     The directory in which the file was found.
     * @param   name    The name of the file.
     *
     * @return  <CODE>true</CODE> if and only if the name should be
     *          included in the file list; <CODE>false</CODE> otherwise.
     */
    public boolean accept (File dir, String name)
    {
        // compare the name with the filter and return the result:
        return name.startsWith (this.p_fileBeginning);
    } // accept

} // class FileBeginningFilter
