/*
 * Class: UniversalFileFilter
 */

// package:
package ibs.util.file;

// imports:
import java.io.File;
import java.io.FilenameFilter;


/******************************************************************************
 * This filter returns always true. <BR/>
 * This means it is equivalent to using the wildcard <CODE>"*"</CODE>.
 * It can be used as dummy if a filter is required.
 *
 * @version     $Id: UniversalFileFilter.java,v 1.2 2007/07/10 09:04:13 kreimueller Exp $
 *
 * @author      Klaus, 31.12.2003
 ******************************************************************************
 */
public class UniversalFileFilter extends Object implements FilenameFilter
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: UniversalFileFilter.java,v 1.2 2007/07/10 09:04:13 kreimueller Exp $";



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a UniversalFileFilter object. <BR/>
     */
    protected UniversalFileFilter ()
    {
        // set properties:
    } // UniversalFileFilter


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
        // all files are accepted, always return true:
        return true;
    } // accept

} // class UniversalFileFilter
