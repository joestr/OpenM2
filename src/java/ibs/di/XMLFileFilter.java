/*
 * Class: XMLFIleFilter.java
 */

// package:
package ibs.di;

// imports:
import java.io.File;
import java.io.FilenameFilter;


/******************************************************************************
 * This class implements a filename filter that only allows files that
 * end with .xml. <BR/>
 *
 * @version     $Id: XMLFileFilter.java,v 1.7 2007/07/31 19:13:53 kreimueller Exp $
 *
 * @author      Buchegger Bernd (BB), 990113
 ******************************************************************************
 */
public class XMLFileFilter extends Object implements FilenameFilter
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: XMLFileFilter.java,v 1.7 2007/07/31 19:13:53 kreimueller Exp $";


    /**
     * use a fileName as filter. <BR/>
     */
    private String fileName = "";

    /**
     * File extension. <BR/>
     */
    private static final String FILE_EXTENSION = ".XML";


    /**************************************************************************
     * Creates an XMLFileFilter Object. <BR/>
     *
     */
    public XMLFileFilter ()
    {
        // nothing to do
    } // XMLFileFilter


    /**************************************************************************
     * Creates an XMLFileFilter Object and sets a filename as filter. <BR/>
     * If the fileName does not end with .XML a .xml will be added to the
     * filename automatically. <BR/>
     *
     * @param   fileName    The file name to be set as filter.
     */
    public XMLFileFilter (String fileName)
    {
        if (fileName.toUpperCase ().endsWith (XMLFileFilter.FILE_EXTENSION))
        {
            this.fileName = fileName;
        } // if
        else
        {
            this.fileName += fileName + ".xml";
        } // else
    } // XMLFileFilter


    /***************************************************************************
     * implements the accept method in the filenamefilter interface. <BR/>
     * only returns true for eccept in the file ends with .xml. <BR/>
     *
     * @param   dir     the directory as a file-object
     * @param   name    the filename to check
     *
     * @return true if the file ends with .xml
     */
    public boolean accept (File dir, String name)
    {
        if (this.fileName.length () == 0)
        {
            return name.toUpperCase ().endsWith (XMLFileFilter.FILE_EXTENSION);
        } // if

        return name.equalsIgnoreCase (this.fileName);
    } // accept

} // class XMLFileFilter

