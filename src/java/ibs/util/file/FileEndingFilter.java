/*
 * Class: FileEndingFilter.java
 */

// package:
package ibs.util.file;

// imports:
import java.io.File;
import java.io.FilenameFilter;


/******************************************************************************
 * Common filter for file endings. <BR/>
 * This filter only checks the ending of a file.
 *
 * @version     $Id: FileEndingFilter.java,v 1.2 2007/07/10 09:04:13 kreimueller Exp $
 *
 * @author      Klaus, 30.12.2003
 ******************************************************************************
 */
public class FileEndingFilter extends Object implements FilenameFilter
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: FileEndingFilter.java,v 1.2 2007/07/10 09:04:13 kreimueller Exp $";


    /**
     * The filter for the file ending. <BR/>
     */
    private String p_fileEnding = null;



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a FileEndingFilter object. <BR/>
     *
     * @param   fileEnding  The file extension.
     */
    protected FileEndingFilter (String fileEnding)
    {
        // set properties:
        this.p_fileEnding = fileEnding;
    } // FileEndingFilter


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
        return name.endsWith (this.p_fileEnding);
    } // accept

} // class FileEndingFilter
