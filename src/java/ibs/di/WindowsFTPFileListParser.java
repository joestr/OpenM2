/*
 * Class: WindowsFTPFileListParser.java
 */

// package:
package ibs.di;

// imports:
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileListParser;
//import com.oroinc.net.ftp.FTPFile;
//import com.oroinc.net.ftp.FTPFileListParser;


/******************************************************************************
 * WindowsFileListParser defines the interface for parsing FTP file
 * listings and converting that information into an array of FTPFile
 * instances. <BR/>
 * Example:
 * 01-31-01  11:34AM       &lt;DIR>          ftp
 * 01-25-01  04:12PM                  427 m2SchwarzesBrett.xml
 * 02-02-01  11:51AM                 1755 test_XMLViewer_alle_Feldtypen.xml
 *
 * @version     $Id: WindowsFTPFileListParser.java,v 1.7 2008/09/17 16:21:17 kreimueller Exp $
 *
 * @author      Buchegger Bernd (BB), 20010205
 ******************************************************************************
 */
public class WindowsFTPFileListParser implements FTPFileListParser
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: WindowsFTPFileListParser.java,v 1.7 2008/09/17 16:21:17 kreimueller Exp $";


    /**************************************************************************
     * Creates a WindowsFTPFileListParser Object. <BR/>
     */
    public WindowsFTPFileListParser ()
    {
        // nothing to do
    } // WindowsFTPFileListParser


    /**************************************************************************
     * Parses an FTP server file listing and converts it into a usable format
     * in the form of an array of FTPFile instances. <BR/>
     * If the file list contains no files, null should be returned,
     * otherwise an array of FTPFile instances representing the files in
     * the directory is returned. <BR/>
     *
     * @param listStream    The InputStream from which the file list should be read.
     *
     * @return  The list of file information contained in the given path.
     *          null if the list could not be obtained or if there
     *          are no files in the directory.
     *
     * @exception   IOException
     *              If an I/O error occurs reading the listStream.
     */
    public FTPFile[] parseFileList (InputStream listStream)
        throws IOException
    {
        String line = "";
        Vector<FTPFile> entries = new Vector<FTPFile> ();
        FTPFile ftpFile = null;
        BufferedReader bufferedReader;

        try
        {
            bufferedReader = new BufferedReader (new InputStreamReader (listStream));
            // read the output lines and extract the filenames
            while ((line = bufferedReader.readLine ()) != null)
            {
                ftpFile = this.parseFTPEntry (line);
                // check if wh could read an entry
                if (ftpFile != null && ftpFile.getType () == FTPFile.FILE_TYPE)
                {
                    // add the entry to the vector
                    entries.addElement (ftpFile);
                } // if (ftpFile != null && ftpFile.getType () == FTPFile.FILE_TYPE)
            } // while ((line = bufferedReader.readLine ()) != null)
            // check if we found any entries
            if (entries.size () == 0)
            {
                return null;
            } // if (entries.size == 0)

            // entries found
            // convert the vector to an array:
            FTPFile[] ftpFiles = new FTPFile[entries.size ()];
            for (int i = 0; i < entries.size (); i++)
            {
                ftpFiles[i] = entries.elementAt (i);
            } // for (int i = 0; i < entries.size; i ++)
            return ftpFiles;
        } // try
        catch (IOException e)
        {
            throw e;
        } // catch
    } // parseFileList


    /**************************************************************************
     * Parses an FTP server listing entry (a single line) and returns an
     * FTPFile instance with the resulting information.
     * If the entry could not be parsed, returns null.
     *
     * @param entry     A single line of an FTP server listing with the end of line truncated.
     *
     * @return  An FTPFile instance representing the file information.
     *          null if the entry could be parsed, returns null.
     */
    public FTPFile parseFTPEntry (String entry)
    {
        FTPFile ftpFile = new FTPFile ();

        try
        {
            // set file name
            ftpFile.setName (entry.substring (39));
            // first check if the entry is a directory or a file
            if (entry.substring (24, 29).equals ("<DIR>"))
            {
                ftpFile.setType (FTPFile.DIRECTORY_TYPE);
            } // if (entry.substring (12,15).equals ("<DIR>"))
            else    // is a file
            {
                ftpFile.setType (FTPFile.FILE_TYPE);
                // set file size
                ftpFile.setSize (Integer.parseInt (entry.substring (17, 38).trim ()));
            } // else is a file
            return ftpFile;
        } // try
        catch (ArrayIndexOutOfBoundsException e)
        {
            return null;
        } // catch
        catch (NumberFormatException e)
        {
//            ftpFile.setName (ftpFile.getName () + "("  + entry.substring (17,38).trim () + ")");
            return null;
        } // catch
    } // parseFTPEntry

} // class WindowsFTPFileListParser
