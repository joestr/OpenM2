/*
 * Class: FileIdentityFilter.java
 */

// package:
package ibs.util.file;

// imports:
import java.io.File;
import java.io.FilenameFilter;


/******************************************************************************
 * Common filter for files. <BR/>
 * This filter tests for identical file names.
 *
 * @version     $Id: FileIdentityFilter.java,v 1.2 2007/07/10 09:04:13 kreimueller Exp $
 *
 * @author      Klaus, 16.12.2003
 ******************************************************************************
 */
public class FileIdentityFilter extends Object implements FilenameFilter
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: FileIdentityFilter.java,v 1.2 2007/07/10 09:04:13 kreimueller Exp $";


    /**
     * The filter for the file name. <BR/>
     */
    private String p_fileNameFilter = null;


    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a FileIdentityFilter object. <BR/>
     *
     * @param   fileNameFilter  The filter string.
     */
    protected FileIdentityFilter (String fileNameFilter)
    {
        // set properties:
        this.p_fileNameFilter = fileNameFilter;
    } // FileIdentityFilter


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
        return name.equals (this.p_fileNameFilter);
    } // accept

} // class FileIdentityFilter
