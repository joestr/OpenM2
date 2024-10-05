/*
 * Class: FileHelpers.java
 */

// package:
package ibs.util.file;

// imports:
import ibs.app.AppConstants;
import ibs.di.DIConstants;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.AccessControlException;
import java.util.Vector;


/******************************************************************************
 * This class contains some helper methods for file interaction. <BR/>
 *
 * @version     $Id: FileHelpers.java,v 1.24 2010/07/13 16:00:31 btatzmann Exp $
 *
 * @author      Bernd Buchegger (BB), 990208
 ******************************************************************************
 */
public abstract class FileHelpers extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: FileHelpers.java,v 1.24 2010/07/13 16:00:31 btatzmann Exp $";

    /**
     * Type of operating system we are in. <BR/>
     */
    static final String OS = AppConstants.OS_NT;

    /**
     * Double file separator. <BR/>
     */
    private static final String DBLFILESEP = File.separator + File.separator;

    /**
     * System property user directory. <BR/>
     */
    private static final String SYSPROP_USERDIR = "user.dir";


    /**************************************************************************
     * Deletes a file. <BR/>
     *
     * @param   pathName    path and name of the file to be deleted
     *
     * @return  <CODE>true</CODE> if the file could be deleted,
     *         <CODE>false</CODE> if not.
     */
    public static final boolean deleteFile (String pathName)
    {
        File file = new File (pathName);
        return FileHelpers.deleteFile (file) == 0;

        // check if file exists and if path contains a file
/* NOT NEEDED ANYMORE (BM)
        if (file.exists () && file.isFile ())
        {
//System.out.print ("try to delete file: " + pathName + "\r\n");
            // delete file:
            try
            {
                return (file.delete ());
            } // try
            catch (SecurityException e)
            {
//System.out.print ("SecurityException: " + e);
                return false;
            } // catch

//            if (file.delete ())
//            {
//               // file has been deleted
//                return true;
//            }//if
//            else
//            {
//                // could not delete file
//                return false;
//            } //else

        } //if
        else
        {
            // delete failed. file could not be found
            return false;
        } //else
*/
    } // deleteFile


    /**************************************************************************
     * Deletes the file denoted by this file object. <BR/>
     *
     * @param file  The file object representing the file which should be deleted.
     *
     * @return 0  if the deletion of an existing file was successful
     *            or the file did not exist
     *        -1  parameter was a file but deletion was not successful
     *        -2  if the given parameter was not a file
     */
    public static final int deleteFile (File file)
    {
        // check if file exists and if path contains a file
        if (file.exists ())
        {
            if (file.isFile ())             // is a file?
            {
                if (!file.delete ())
                {
//System.out.println ("Could not delete file \"" + file.getPath () + "\".");
                    return -1;
                } // if deletion successfull
            } //if is a file
            else                            // is a directory?
            {
                return -2;
            } // else is a directory
        } // if file exists

        // file does not exist or deleting was successfull
        return 0;
    } // deleteFile


    /**************************************************************************
     * Delete the files within a directory. <BR/>
     *
     * @param   dir             The directory to search in.
     * @param   dirFilter       The filter for the directories.
     * @param   fileFilter      The filter for the files.
     * @param   searchRecursive Shall the search be performed recursively?
     *
     * @return 0  If the deletion of an all files was successful
     *            or the files did not exist.
     *        -1  At least one file could not be deleted.
     *        -2  One result was not a file.
     */
    public static final int deleteFiles (File dir, FilenameFilter dirFilter,
                                         FilenameFilter fileFilter,
                                         boolean searchRecursive)
    {
        File[] files = new File[0];     // the files to be deleted
        int retVal = 0;                 // the result value

        // get the files:
        files = FileHelpers.getFilesArray (dir, dirFilter, fileFilter, searchRecursive);

        // loop through all files and delete each of them:
        for (int i = 0; retVal == 0 && i < files.length; i++)
        {
            // delete the current file:
            retVal = FileHelpers.deleteFile (files[i]);
        } // for i

        // return the result:
        return retVal;
    } // deleteFiles
    
    
    /**************************************************************************
     * Delete the files within a directory. <BR/>
     *
     * @param   dir             The directory to search in.
     * @param   dirFilter       The filter for the directories.
     * @param   fileFilter      The filter for the files.
     * @param   excludePattern  The exclude filter for the files.
     * @param   searchRecursive Shall the search be performed recursively?
     *
     * @return 0  If the deletion of an all files was successful
     *            or the files did not exist.
     *        -1  At least one file could not be deleted.
     *        -2  One result was not a file.
     */
    public static final int deleteFiles (File dir, FilenameFilter dirFilter,
                                         FilenameFilter fileFilter,
                                         FilenameFilter excludeFilter,
                                         boolean searchRecursive)
    {
        File[] files = new File[0];     // the files to be deleted
        int retVal = 0;                 // the result value
        
        // get the files:
        files = FileHelpers.getFilesArray (dir, dirFilter, fileFilter, searchRecursive);

        // loop through all files and delete each of them:
        for (int i = 0; retVal == 0 && i < files.length; i++)
        {
            if (excludeFilter == null || !excludeFilter.accept (dir, files[i].getName ()))
            {
                // delete the current file:
                retVal = FileHelpers.deleteFile (files[i]);
            } // if
        } // for i

        // return the result:
        return retVal;
    } // deleteFiles


    /**************************************************************************
     * Deletes a directory. <BR/>
     *
     * @param   path    Path and name of the file to be deleted
     *
     * @return  true    if file could be deleted successfully
     *          false   if the parameter was not a directory or the deletion was
     *                  not successfull.
     */
    public static final boolean deleteDir (String path)
    {
        File file = new File (path);

        // check if file exists and if path contains a file
        if (file.exists () && file.isDirectory ())
        {
            // delete file
            if (!file.delete ())
            {
                // could not delete file
                return false;
            } //else
        } //if
        else if (file.isFile ())
        {
            return false;
        } // else if file is file

        // file does not exist or deletion successfull
        return true;
    } // deleteDir


    /**************************************************************************
     * Rename a file. <BR/>
     *
     * @param   pathName    Path and name of the file to be renamed.
     * @param   newName     New name of the file.
     *
     * @return  <CODE>true</CODE> if file could be renamed,
     *          <CODE>false</CODE> otherwise.
     */
    public static final boolean renameFile (String pathName, String newName)
    {
        File file;
        File newFile;

        file = new File (pathName);
        newFile = new File (newName);
        // check if file exists and if path contains a file
        if (file.exists () && file.isFile ())
        {
            // delete file
            if (file.renameTo (newFile))
            {
                // file has been renamed
                return true;
            } // if

            // file could not be renamed
            return false;
        } // if

        // could not find file
        return false;
    } // renameFile


    /**************************************************************************
     * Rename a directory. <BR/>
     *
     * @param   dirOld  The old directory entry.
     * @param   dirNew  The new directory entry.
     *
     * @return  <CODE>true</CODE> if directory could be renamed,
     *          <CODE>false</CODE> otherwise.
     */
    public static final boolean renameDir (File dirOld, File dirNew)
    {
        // check if the file entry exists and is a directory:
        if (dirOld.exists () && dirOld.isDirectory ())
        {
            // rename directory:
            if (dirOld.renameTo (dirNew))
            {
                // directory has been renamed:
                return true;
            } // if

            // directory could not be renamed:
            return false;
        } // if

        // could not find directory:
        return false;
    } // renameDir


    /**************************************************************************
     * Moves a file from on location to another.
     * It is possible to rename the file during the move process. <BR/>
     *
     * HINT: The move method uses the move command from the operating system.
     * It is therefore system dependent and must be adopted to the various
     * operating systems it is used with. <BR/>
     *
     * HINT: this method is still under construction!. <BR/>
     *
     * @param sourceFilePath        path and file name of source
     * @param destinationFilePath   path and file name of destination
     *
     * @return  <CODE>true</CODE> if the file was move successfully,
     *          <CODE>false</CODE> otherwise.
     */
    public static final boolean moveFileNative (String sourceFilePath, String destinationFilePath)
    {
        Runtime runtime;
        Process process;
        String cmd = "";
        int exitValue;

        if (FileHelpers.OS.equalsIgnoreCase (AppConstants.OS_NT))
        {
            // move command in windows nt 4.0 environment
            cmd = "move \"" + sourceFilePath + "\" \"" + destinationFilePath + "\"";
        } //if
        else if (FileHelpers.OS.equalsIgnoreCase (AppConstants.OS_UNIX))
        {
            // move command in unix environment
            cmd = "mv \"" + sourceFilePath + "\" \"" + destinationFilePath + "\"";
        } //else if
        else
        {
            // operting system not known
            return false;
        } //else

        try
        {
            // create a new runtime object
            runtime = Runtime.getRuntime ();
            // now execute the command
            process = runtime.exec (cmd);
            // wait for process to finish
            try
            {
                exitValue = process.waitFor ();
                // check if process terminated correctly
                // is the return value system independent?
                if (exitValue != 0)
                {
                    // file could not be moved
                    return false;
                } // if

                // everything went ok
                // file moved
                return true;
            } // try
            catch (InterruptedException e)
            {
                // file could not be moved
                return false;
            } // catch
        } // try
        catch (IOException e)
        {
            // file could not be moved
            return false;
        } // catch
    } // moveFileNative


    /**************************************************************************
     * Copy a file from on location to another.
     * It is possible to rename the file during the move process. <BR/>
     *
     * HINT: The copy method uses the copy command from the operating system.
     * It is therefore system dependent and must me adopted to the various
     * operating systems it is used with. <BR/>
     *
     * HINT: this method is still under construction!. <BR/>
     *
     * @param sourceFilePath        path and file name of source
     * @param destinationFilePath   path and file name of destination
     *
     * @return  <CODE>true</CODE> if file could be copied,
     *          <CODE>false</CODE> otherwise.
     */
    public static final boolean copyFileNative (String sourceFilePath,
                                                String destinationFilePath)
    {
        Runtime runtime;
        Process process;
        String cmd = "";
        int exitValue;

        if (FileHelpers.OS.equalsIgnoreCase (AppConstants.OS_NT))
        {
            // move command in windows nt 4.0 environment
            cmd = "copy \"" + sourceFilePath + "\" \"" + destinationFilePath + "\"";
        } //if
        else if (FileHelpers.OS.equalsIgnoreCase (AppConstants.OS_UNIX))
        {
            // move command in unix environment
            cmd = "cp \"" + sourceFilePath + "\" \"" + destinationFilePath + "\"";
        } //else if
        else
        {
            // operating system not known:
            return false;
        } //else

        try
        {
            // create a new runtime object
            runtime = Runtime.getRuntime ();
            // now execute the command
            process = runtime.exec (cmd);
            // wait for process to finish
            try
            {
                exitValue = process.waitFor ();
                // check if process terminated correctly
                // is the return value system independent?
                if (exitValue != 0)
                {
                    // file could not be moved
                    return false;
                } // if

                // everything went ok
                // file moved
                return true;
            } // try
            catch (InterruptedException e)
            {
                // file could not be moved
                return false;
            } // catch
        } // try
        catch (IOException e)
        {
            // file could not be moved
            return false;
        } // catch
    } // copyFileNative

    /**************************************************************************
     * Writes a file with the given data to the given destination. <BR/>
     *
     * @param   b            The file data as byte[].
     * @param    filename    Name of the output file.
     * @param    extension    File extension of the output file.
     * @param   targetDir   Target directory where to write a file within the
     *                      form data.
     * @param    overwrite    If the file should be overwrite if it already exists.
     *                         If set to false and the file already exist the next free
     *                         number is appended to the file name.
     *
     * @return  The resulting target file name.
     *
     */
    public static String writeFile (byte[] b, String filename,
                                    String extension, String targetDir,
                                    boolean overwrite)
    {
        String targetDirLocal = targetDir; // variable for local assignments
        String fileName;                // the name of the file itself
        String newFile;                 // the new file path and name
        int lastSepIndex;               // index of the last separator in this.filename
        String extensionLocal = extension; // variable for local assignments

//        targetDir = targetDir.substring (0, targetDir.length ()-1);
        targetDirLocal = targetDirLocal.replace ('/', File.separatorChar);
        targetDirLocal = targetDirLocal.replace ('\\', File.separatorChar);

        // there are two possible separators: \ and /
        // both have to be checked, because the client file system is unknown
        // first try the DOS-based separator
        lastSepIndex = filename.lastIndexOf ('\\');
        // if there is no DOS-based separator found it has to be a linux based
        if (lastSepIndex < 0)
        {
            lastSepIndex = filename.lastIndexOf ('/');
        } // if

        // Get the filename out of the file data:
        fileName = filename.substring (lastSepIndex + 1);

        // compute the new filename:
        if (targetDirLocal.charAt (targetDirLocal.length () - 1) != File.separatorChar)
        {
            targetDirLocal += File.separatorChar;
        } // if

        // check if the extension starts with .
        if (!extensionLocal.startsWith ("."))
        {
            extensionLocal = "." + extensionLocal;
        } // if extension does not start with "."

        // ensure that the directory exists:
        if (!FileHelpers.createDirectory (targetDirLocal)) // the directory could not be created?
        {
            // output directory could not be created
            return null;
        } // if

        newFile = targetDirLocal + fileName + extensionLocal;
        File f = new File (newFile);

        // check if the file already exists
        if (f.exists ())
        {
            // overwrite handling
            if (overwrite)
            {
                // delete it
                FileHelpers.deleteFile (newFile);
            } // if overwrite
            else // !overwrite
            {
                String changedFilename = fileName + "1";

                int postfix = 1;
                // get the next free filename
                while ((new File (targetDirLocal + changedFilename + extensionLocal)).exists ())
                {
                    postfix++;
                    changedFilename = fileName + postfix;
                } // while

                // adapt the filename
                fileName = changedFilename;
                newFile = targetDirLocal + fileName + extensionLocal;
            } // else !overwrite
        } // if exists

        // send the data to the file:
        if (!FileHelpers.sendToFile (b, newFile, false))
        {
            // file could not be written
            return null;
        } // if file could not be written

        // return the target file name:
        return fileName + extensionLocal;
    } // writeFile


    /**************************************************************************
     * Sends the given data to a file. <BR/>
     *
     * @param   b           byte[] containing the binary data.
     * @param   fileName    The file path and name.
     * @param   append      Append to an already existing target file.
     *
     * @return  <CODE>true</CODE> if writing to file was successful,
     *          <CODE>false</CODE> otherwise.
     */
    public static boolean sendToFile (byte[] b, String fileName, boolean append)
    {
        // define the local file stream handler
        FileOutputStream fileOut = null;

        try
        {
            // open file stream handlers:
            fileOut = new FileOutputStream (fileName, append);

            // write the byte[] to the file:
            fileOut.write (b);
            fileOut.flush ();
        } // try
        catch (IOException e)
        {
            // file could not be written
            return false;
        } // catch
        finally
        {
            if (fileOut != null)
            {
                try
                {
                    // close the file and the connection:
                    fileOut.close ();
                } // try
                catch (IOException e)
                {
                    // file could not be closed
                    return false;
                } // catch
            } // if
        } // finally

        return true;
    } // sendToFile

    /**************************************************************************
     * Ensures that a specific directory exists. <BR/>
     * This method first checks if the method already exists and tries to
     * create it if it is not existent. <BR/>
     *
     * @param path  path to create
     *
     * @return  <CODE>true</CODE> if the directory exists after performing
     *          this method, <CODE>false</CODE> otherwise.
     */
    public static boolean createDirectory (String path)
    {
        // set directory - absolute path on target server
        File dir = new File (path);

        // does dir already exist
        if (!dir.exists ())             // directory does not exist?
        {
            // create directory
            // note: if directory already exists nothing happens
            //       e.g. for images no directory will be created
            //       because the upload path already exists
            return dir.mkdir ();
        } // if directory does not exist

        // directory already exists
        // ok
        return true;
    } // createDirectory

    /**************************************************************************
     * Copy a file from on location to another.
     * It is possible to rename the file during the move process. <BR/>
     *
     * HINT: This is done via reading in the file via an fileReader and output it
     * to a fileWriter. <BR/>
     *
     * @param sourceFilePath        path and file name of source
     * @param destinationFilePath   path and file name of destination
     *
     * @return  <CODE>true</CODE> if file could be copied,
     *          <CODE>false</CODE> otherwise.
     */
    public static final boolean copyFile (String sourceFilePath,
                                          String destinationFilePath)
    {
        return FileHelpers.copyFile (sourceFilePath, destinationFilePath, false);
    } // copyFile

    
    /**************************************************************************
     * Copy a file from on location to another.
     * It is possible to rename the file during the move process and to append
     * to an already existing file. <BR/>
     *
     * HINT: This is done via reading in the file via an fileReader and output it
     * to a fileWriter. <BR/>
     *
     * @param sourceFilePath        path and file name of source
     * @param destinationFilePath   path and file name of destination
     * @param append                append to an already existing target file
     *
     * @return  <CODE>true</CODE> if file could be copied,
     *          <CODE>false</CODE> otherwise.
     */
    public static final boolean copyFile (String sourceFilePath,
                                          String destinationFilePath,
                                          boolean append)
    {
        boolean retVal = false;

        try
        {
            // create the streams:
            FileInputStream fis = new FileInputStream (sourceFilePath);
            FileOutputStream fos = new FileOutputStream (destinationFilePath, append);

            // read the data from the reader and output it to the writer
            retVal = StreamHelpers.copyData (fis, fos);

            // close the data streams:
            fis.close ();
            fos.close ();

            // return the result:
            return retVal;
        } //try
        catch (IOException e)
        {
            // file could not be copied:
            return false;
        } //catch
    } // copyFile


    /**************************************************************************
     * Moves a file from on location to another. It is possible to rename
     * the file during the move process. The file will be copied to the
     * destination location and the source file will be deleted afterwards. <BR/>
     *
     * @param sourceFilePath        path and file name of source
     * @param destinationFilePath   path and file name of destination
     *
     * @return  <CODE>true</CODE> if file could be moved,
     *          <CODE>false</CODE> otherwise.
     */
    public static final boolean moveFile (String sourceFilePath,
                                          String destinationFilePath)
    {
        if (FileHelpers.copyFile (sourceFilePath, destinationFilePath))
        {
            return FileHelpers.deleteFile (sourceFilePath);
        } // if

        return false;
    } // moveFile
    
    
    /**************************************************************************
     * Renames the files corresponding to the given filters.
     *
     * @param   dir             The directory to search in.
     * @param   dirFilter       The filter for the directories.
     * @param   fileFilter      The filter for the files.
     * @param   regEx           The regular expression to which this string is to be matched
     * @param   replacement     The string to be substituted for each match
     * @param   searchRecursive Shall the search be performed recursively?
     *
     * @return if the renaming operation was successful.
     */
    public static final boolean renameFiles (File dir, FilenameFilter dirFilter,
                                          FilenameFilter fileFilter,
                                          String regEx, String replacement,
                                          boolean searchRecursive)
    {
        File[] files = new File[0];     // the files to be deleted
        boolean retVal = true;                 // the result value

        // get the files:
        files = FileHelpers.getFilesArray (dir, dirFilter, fileFilter, searchRecursive);

        // loop through all files and delete each of them:
        for (int i = 0; retVal && i < files.length; i++)
        {
            // create the new filename
            String newFileName = files [i].getName ().replaceAll (regEx, replacement);
            
            // rename the current file:
            retVal = FileHelpers.renameFile (files [i].getPath (), files [i].getParent () + File.separator + newFileName);
        } // for i

        // return the result:
        return retVal;
    } // renameFiles


    /**************************************************************************
     * Checks if a file exists. <BR/>
     *
     * @param   filePath    full file name with path
     *
     * @return  true if file exists and false if not
     */
    public static final boolean exists (String filePath)
    {
        File file = new File (filePath);
        return file.exists ();
    } // exists


    /**************************************************************************
     * Creates a directory. <BR/>
     *
     * @param dirPath          path of directory to be created
     *
     * @return  true if directory could have been created or already exists
     *          and false if directory could not have been created
     */
    public static final boolean makeDir (String dirPath)
    {
        return FileHelpers.makeDir (dirPath, false);
    } // makeDir


    /**************************************************************************
     * Creates a directory. <BR/>
     *
     * @param dirPath           path of directory to be created
     * @param isParentInclude   flag if all parent directories should also be
     *                          included.
     *
     * @return  true if directory could have been created or already exists
     *          and false if directory could not have been created
     */
    public static final boolean makeDir (String dirPath, boolean isParentInclude)
    {
        File file = new File (dirPath);
        // check if the file is already an existing directory
        if (file.isDirectory ())
        {
            return true;
        } // if (file.isDirectory ())

        // directory does not exist
        // check if parent directories should also be created:
        if (isParentInclude)
        {
            return file.mkdirs ();
        } // if

        return file.mkdir ();
    } // makeDir


    /**************************************************************************
     * Returns all files found in a directory. <BR/>
     *
     * @param filePath  the path to look for files
     *
     * @return an array of fileNames or null if directory is not valid
     */
    public static final String[] getFilesArray (String filePath)
    {
        return FileHelpers.getFilesArray (filePath, null);
    } // getFilesArray


    /**************************************************************************
     * Returns all files found in a directory.
     * Additionally a file filter can be set. <BR/>
     *
     * @param filePath  the path to look for files
     * @param filter    the fileFilter
     *
     * @return an array of fileNames or null if directory is not valid
     */
    public static final String[] getFilesArray (String filePath, FilenameFilter filter)
    {
        File dir;

        // check if filePath exists:
        if (filePath != null && filePath.trim ().length () > 0)
        {
            // check if the file path we got is a correct directory:
            dir = new File (filePath);
            if (dir.isDirectory ())
            {
                // check if we got a filter:
                if (filter != null)
                {
                    // get the filenames from the directory through a filter
                    return dir.list (filter);
                } // if

                // get the filenames from the directory without filter
                return dir.list ();
            } // if

            // path invalid
            return null;
        } // if

        // no filePath specified
        return null;
    } // getFilesArray


    /**************************************************************************
     * Load the contents of a directory. <BR/>
     *
     * @param   dir             The directory to search in.
     * @param   dirFilter       The filter for the directories.
     * @param   fileFilter      The filter for the files.
     * @param   searchRecursive Shall the search be performed recursively?
     *
     * @return  All files matching the filter.
     */
    public static final File[] getFilesArray (File dir,
                                              FilenameFilter dirFilter,
                                              FilenameFilter fileFilter,
                                              boolean searchRecursive)
    {
        // call the recursive search function and return the result:
        return FileHelpers.getFilesVector (new Vector<File> (100, 10), dir,
            dirFilter, fileFilter, searchRecursive).toArray (new File[0]);
    } // getFilesArray


    /***************************************************************************
     * Get the contents of a directory. <BR/> The files are added to the files
     * vector.
     *
     * @param files The vector of already known files.
     * @param dir The directory to search in.
     * @param dirFilter The filter for the directories.
     * @param fileFilter The filter for the files.
     * @param searchRecursive Shall the search be performed recursively?
     *
     * @return All files matching the filter.
     */
    private static final Vector<File> getFilesVector (Vector<File> files,
                                                File dir,
                                                FilenameFilter dirFilter,
                                                FilenameFilter fileFilter,
                                                boolean searchRecursive)
    {
        Vector<File> filesLocal = files;  // variable for local assignments
        File[] contents = null;         // the contents of the actual directory
        boolean addFiles =
            dirFilter.accept (dir.getParentFile (), dir.getName ());
                                        // shall the files of the actual
                                        // directory be added?

        // get the contents of the directory:
        contents = dir.listFiles ();

        // check if there was a content found:
        if (contents != null)
        {
            // handle elements:
            for (int i = 0; i < contents.length; i++)
            {
                // check for the type of the element:
                if (searchRecursive && contents[i].isDirectory ())
                                        // directory?
                {
                    // call this method recursively and add the result:
                    filesLocal = FileHelpers.getFilesVector (
                        filesLocal, contents[i], dirFilter, fileFilter,
                        searchRecursive);
                } // if directory
                else if (addFiles && contents[i].isFile () &&
                         fileFilter.accept (dir, contents[i].getName ()))
                                        // add file?
                {
                    // add the actual file to the result:
                    filesLocal.add (contents[i]);
                } // else if add file
            } // for i
        } // if

        // return the result:
        return filesLocal;
    } // getFilesVector


    /**************************************************************************
     * Get all directories within the actual directory. <BR/>
     *
     * @param   dir         The directory in which to search.
     * @param   dirFilter   The directory filter.
     * @param   searchRecursive Shall the search be performed recursively?
     *
     * @return  All directories matching the filter.
     */
    public static final File[] getDirectoriesArray (File dir,
                                                    FilenameFilter dirFilter,
                                                    boolean searchRecursive)
    {
        // call the recursive search function and return the result:
        return FileHelpers.getDirectoriesVector (new Vector<File> (20, 10),
            dir, dirFilter, searchRecursive).toArray (new File[0]);
    } // getDirectoriesArray


    /**************************************************************************
     * Get all directories within the actual directory. <BR/>
     * The directories are added to the directories vector.
     *
     * @param   directories The directories vector to be filled.
     * @param   dir         The directory in which to search.
     * @param   dirFilter   The directory filter.
     * @param   searchRecursive Shall the search be performed recursively?
     *
     * @return  All directories matching the filter.
     */
    private static final Vector<File> getDirectoriesVector (
                                                            Vector<File> directories,
                                                            File dir,
                                                            FilenameFilter dirFilter,
                                                            boolean searchRecursive)
    {
        Vector<File> directoriesLocal = directories;
                                        // variable for local assignments
        File[] contents = null;         // the contents of the actual directory

        // get the contents of the directory:
        contents = dir.listFiles ();

        // check if there was a content found:
        if (contents != null)
        {
            // handle elements:
            for (int i = 0; i < contents.length; i++)
            {
                // check for the type of the element:
                if (contents[i].isDirectory ()) // directory?
                {
                    // check for recursive:
                    if (searchRecursive) // recursive search?
                    {
                        // call this method recursively and add the result:
                        directoriesLocal = FileHelpers.getDirectoriesVector (
                            directoriesLocal, contents[i], dirFilter, searchRecursive);
                    } // if recursive search

                    // check if the directory shall be added:
                    if (dirFilter.accept (dir, contents[i].getName ()))
                    {
                        // add the actual directory to the result:
                        directoriesLocal.add (contents[i]);
                    } // if
                } // if directory
            } // for i
        } // if

        // return the result:
        return directoriesLocal;
    } // getDirectoriesVector


    /**************************************************************************
     * Checks if a path ends with a valid file separator and add one in case it
     * is missing. This method does nothing in case the path already ends
     * with the appropriate file separator. <BR/>
     *
     * @param   path        the path to test
     *
     * @return a path with an valid file separator at the end
     */
    public static String addEndingFileSeparator (String path)
    {
        String pathLocal = path;        // variable for local assignments

        if (pathLocal != null && !pathLocal.endsWith (File.separator))
        {
            pathLocal += File.separator;
        } // if

        return pathLocal;
    } // addEndingFileSeparator


    /**************************************************************************
     * Checks if a url end with a valid url separator and add one in case it
     * is missing. This method does nothing in case the url already ends
     * with the appropriate url separator. <BR/>
     *
     * @param   url       the url to test
     *
     * @return a url with an valid url separator at the end
     */
    public static String addEndingURLSeparator (String url)
    {
        String urlLocal = url;          // variable for local assignments

        if (urlLocal != null && !urlLocal.endsWith ("/"))
        {
            urlLocal += "/";
        } // if

        return urlLocal;
    } // addEndingURLSeparator


    /**************************************************************************
     * Returns the size of an file. <BR/>
     * If the path has no ending path separator it will be added automatically. <BR/>
     *
     * @param path      the path of the file
     * @param fileName  the name of the file
     *
     * @return the fileSize or -1 if file does not exist
     */
    public static final long getFileSize (String path, String fileName)
    {
        // get size of the file in Bytes
        String filePath = FileHelpers.addEndingFileSeparator (path) + fileName;
        File file = new File (filePath);
        // does the file exists?
        if (file.isFile ())
        {
            return file.length ();
        } // if the file exists

        // file does not esist
        return -1;
    } // getFileSize


    /**************************************************************************
     * Moves all files in a directory. <BR/>
     *
     * @param dirOld        pathName of the directory where the files to move are
     * @param dirNew        pathName of the directory where the files should be moved
     *
     * @return true if the files could be moved and false if not
     */
    public static final boolean moveDirectory (String dirOld, String dirNew)
    {
        String dirOldLocal = dirOld;    // variable for local assignments
        String dirNewLocal = dirNew;    // variable for local assignments
        String[] files = FileHelpers.getFilesArray (dirOldLocal);
        boolean allright = true;

        if (files != null)
        {
            // ensure that the paths end with the correct file separator:
            dirOldLocal = FileHelpers.addEndingFileSeparator (dirOldLocal);
            dirNewLocal = FileHelpers.addEndingFileSeparator (dirNewLocal);

            // loop through directory
            for (int i = 0; i < files.length; i++)
            {
                if (!FileHelpers.renameFile (dirOldLocal + files[i],
                    dirNewLocal + files[i]))
                {
                    allright = false;
                } // if
            } // for
        } // if

        return allright;
    } // moveDirectory


    /**************************************************************************
     * Move a directory. <BR/>
     *
     * @param   dirOld  The old directory entry.
     * @param   dirNew  The new directory entry.
     *
     * @return  <CODE>true</CODE> if the directory could be moved,
     *          <CODE>false</CODE> otherwise.
     */
    public static final boolean moveDirectory (File dirOld, File dirNew)
    {
        // move the directory and return the result:
        return !FileHelpers.renameDir (dirOld, dirNew);
    } // moveDirectory


    /**************************************************************************
     * Copies all files in a directory. <BR/>
     *
     * @param dirOld        pathName of the directory where the files to copy are
     * @param dirNew        pathName of the directory where the files should be copied
     *
     * @return true if the files could be moved and false if not
     */
    public static final boolean copyDirectory (String dirOld, String dirNew)
    {
        String[] files = FileHelpers.getFilesArray (dirOld);
        boolean allright = true;
        if (files != null)
        {
            for (int i = 0; i < files.length; i++)
            {
                if (!FileHelpers.copyFile (dirOld + files[i], dirNew + files[i]))
                {
                    allright = false;
                } // if
            } // for i
        } // if

        return allright;
    } // copyDirectory


    /**************************************************************************
     * Make a file name valid for the actual operating system. <BR/>
     * This method replaces all occurrences of <CODE>"/"</CODE> with
     * <CODE>File.separator</CODE> and ensures that there is no duplicate
     * separator.
     *
     * @param   fileName    The file name to be checked.
     *
     * @return  The valid file name or
     *          <CODE>null</CODE> if the original file name was <CODE>null</CODE>.
     */
    public static final String makeFileNameValid (String fileName)
    {
        return StringHelpers.replace (StringHelpers.replace (
                fileName, "/", File.separator),
            FileHelpers.DBLFILESEP, File.separator);
    } // makeFileNameValid


    /**************************************************************************
     * Returns a unique file name. <BR/>
     * Checks if a file already exists and adds a number to the beginning of
     * the file until there is no file anymore with the generated filename. <BR/>
     * A generated filename will look like "1_file.xml", "2_file.xml" etc. <BR/>
     *
     * @param   path        the file path
     * @param   fileName    the name of the file to test
     *
     * @return a string with the new unique fileName
     */
    public static String getUniqueFileName (String path, String fileName)
    {
        String pathLocal = path;        // variable for local assignments
        String newFileName = fileName;
        int counter = 1;
        int postfixIndex;
        String postfix = "";
        String name = "";

        // ensure a correct path:
        pathLocal = FileHelpers.addEndingFileSeparator (pathLocal);

        // get the postfix
        postfixIndex = fileName.lastIndexOf (".");
        // check if the filename contains a postfix
        if (postfixIndex > -1)
        {
            postfix = fileName.substring (postfixIndex);
            name = fileName.substring (0, postfixIndex);
        }  // if (postfixIndex > -1)
        else    // no postfix found
        {
            name = fileName;
            postfix = "";
        } // else no postfix found

        // try to find an unique filename
        while (FileHelpers.exists (pathLocal + newFileName))
        {
            newFileName = name + "_" + counter + postfix;
            counter++;
        } // while (FileHelpers.exists (destinationPath + newFileName))
        return newFileName;
    } // getUniqueFileName

    
    /**************************************************************************
     * Retrieves the line separator character from the System Properties
     * and returns it as String. <BR/>
     *
     * @return a string including the line separator character from the
     *         System Properties
     */
    public static String getLineSeparator ()
    {
        return System.getProperty("line.separator");
    } // getLineSeparator


    /**************************************************************************
     * Deletes the directory denoted by this pathname recursively. <BR/>
     *
     * @param dirName  Path and name of the directory to be deleted.
     *
     * @return  true if the deletion was successfull
     *          false if the directory could not be found or an error occurred
     */
    public static final boolean deleteDirRec (String dirName)
    {
        File file = new File (dirName);
        boolean retVal = true;

        // check if directory exists and is a directory
        if (file.exists () && file.isDirectory ())        // is a directory?
        {
            // if directory is not empty then delete all files and
            // subdirectories first.
            if (FileHelpers.isEmptyDir (dirName) != 0)
            {
                String[] fileNames = FileHelpers.getFilesArray (dirName);
                File tempFile = null;

                for (int i = 0; i < fileNames.length; i++)
                {
                    tempFile = new File (dirName + File.separator + fileNames[i]);

                    // check if file or directory
                    if (tempFile.isFile ()) // entry is a file?
                    {
                        retVal &= FileHelpers.deleteFile (tempFile) == 0;
                    } // if entry is a file
                    else if (tempFile.isDirectory ()) // entry is a directory?
                    {
                        retVal &= FileHelpers.deleteDirRec (dirName + fileNames[i]);
                    } // else entry is a directory
                } // for all entries in the actual directory
            } // if is not empty dir

            // double check if directory is empty now.
            FileHelpers.deleteDir (dirName);
        } // if is a directory
        else                            // no directory or does not exist?
        {
            // directory not found
            retVal = false;
        } // else no directory or does not exist?

        return retVal;
    } // deleteDirRec


    /**************************************************************************
     * Returns if a directory denoted by this pathname is empty or not. <BR/>
     *
     * @param dirName  Path and name of the directory.
     *
     * @return 0 if and only if the directory exists and is empty
     *         1 if the directory exists and is not empty
     *        -1 if the directory does not exist
     */
    public static final int isEmptyDir (String dirName)
    {
        File file = new File (dirName);

        if (file.exists ())
        {
            if (file.isDirectory ())        // is a directory?
            {
                if (file.list ().length < 1)// directory is empty
                {
                    return 0;
                } // if directory is empty

                // directory is not empty or does not exist
                return 1;
            } // if file is a directory
        } // if a directory

        // not a directory?
        return -1;
    } // isEmptyDir


    /**************************************************************************
     * Ensure that the file name has a specific extension. <BR/>
     * This method does not manipulate any files, it only changes the string.
     * If the file name already ends with the extension it is not changed.
     *
     * @param   fileName    The file name to be changed (may also contain the
     *                      path information).
     * @param   extension   The extension to be set.
     *
     * @return  The new file name.
     */
    public static final String setNameExtension (String fileName,
                                                 String extension)
    {
        String extensionLocal = extension; // variable for local assignments
        String newFileName = fileName;  // the computed file name
        int pos = 0;                    // actual position within the string

        // ensure that the extension starts with a '.':
        if (extensionLocal.charAt (0) != '.') // extension does not start with a '.'?
        {
            // prepend the '.':
            extensionLocal = "." + extensionLocal;
        } // if extension does not start with a '.'

        // search for the last '.' in the file name which denotes the beginning
        // of the extension:
        if ((pos = fileName.lastIndexOf ('.')) >= 0) // found the extension?
        {
            newFileName = fileName.substring (0, pos);
        } // if found the extension

        // append the new extension:
        newFileName += extensionLocal;

        // return the new file name:
        return newFileName;
    } // setNameExtension


    /**************************************************************************
     * Copies all files in a directory. <BR/>
     *
     * @param   dir         The directory in which to replace the file contents.
     * @param   oldStr      String to be replaced.
     * @param   newStr      String which shall replace the oldStr.
     *
     * @throws  FileException
     *          An error occurred during replacement.
     */
    public static final void replaceInDirectory (File dir, String oldStr,
                                                 String newStr)
        throws FileException
    {
        FileHelpers.replaceInDirectory (
            dir, FileManager.getFilter ("*"), FileManager.getFilter ("*"),
            oldStr, newStr);
    } // replaceInDirectory


    /**************************************************************************
     * Copies all files in a directory. <BR/>
     *
     * @param   dir         The directory in which to replace the file contents.
     * @param   dirFilter   The directory filter.
     * @param   fileFilter  The file filter.
     * @param   oldStr      String to be replaced.
     * @param   newStr      String which shall replace the oldStr.
     *
     * @throws  FileException
     *          An error occurred during replacement.
     */
    public static final void replaceInDirectory (File dir,
                                                 FilenameFilter dirFilter,
                                                 FilenameFilter fileFilter,
                                                 String oldStr, String newStr)
        throws FileException
    {
        File[] contents = null;         // the contents of the directory

        // check if the files of this directory shall be added:
        if (dirFilter.accept (dir.getParentFile (), dir.getName ()))
        {
            // get the contents of the directory:
            contents =
                FileHelpers.getFilesArray (dir, dirFilter, fileFilter, false);

            // check if there was a content found:
            if (contents != null)
            {
                // handle elements:
                for (int i = 0; i < contents.length; i++)
                {
                    // make the replacement within the file:
                    FileHelpers.replaceInFile (contents[i], oldStr, newStr);
                } // for i
            } // if
        } // if
    } // replaceInDirectory


    /**************************************************************************
     * Copy a file from on location to another.
     * It is possible to rename the file during the move process. <BR/>
     *
     * HINT: This is done via reading in the file via an fileReader and output it
     * to a fileWriter. <BR/>
     *
     * @param   file        The file in which the replacement shall be done.
     * @param   oldStr      String to be replaced.
     * @param   newStr      String which shall replace the oldStr.
     *
     * @throws  FileException
     *          An error occurred during replacement.
     */
    public static final void replaceInFile (File file, String oldStr, String newStr)
        throws FileException
    {
        File outFile = null;
        InputStream fis = null;         // the input reader
        OutputStream fos = null;        // the output writer
        boolean replacementDone = false; // was there something replaced?

        try
        {
            // create the output file:
            outFile = File.createTempFile (file.getName (), null);
        } // try
        catch (IOException e)
        {
            throw new FileException (
                "replaceInFile: Error while creating temp file for " + file, e);
        } // catch

        try
        {
            // create input file reader:
            fis = new FileInputStream (file);
        } // try
        catch (FileNotFoundException e)
        {
            throw new FileException (
                "replaceInFile: Error while opening input file: " + file, e);
        } // catch

        try
        {
            // create output file writer:
            fos = new FileOutputStream (outFile);
        } // try
        catch (FileNotFoundException e)
        {
            throw new FileException (
                "replaceInFile: Error while opening output file: " + outFile, e);
        } // catch

        // parse the data stream:
        replacementDone = StreamHelpers.replaceInStream (
            fis, fos, file.getName (), oldStr, newStr);

        try
        {
            // close the output data stream:
            fos.close ();
        } // try
        catch (IOException e)
        {
            throw new FileException (
                "replaceInFile: Error while closing output file: " + outFile, e);
        } // catch

        try
        {
            // close the input data stream:
            fis.close ();
        } // try
        catch (IOException e)
        {
            throw new FileException (
                "replaceInFile: Error while closing input file: " + file, e);
        } // catch

        // check if the original file have to be overwritten:
        if (replacementDone)
        {
            // delete the original file:
            if (!file.delete ())
            {
                throw new FileException (
                    "replaceInFile: Error while deleting original file: " + file + ".");
            } // if

            // replace the original file with the out file:
            if (!outFile.renameTo (file))
            {
                throw new FileException (
                    "replaceInFile: Error while overwriting file: " + file +
                    " with " + outFile + ".");
            } // if
        } // if
        else
        {
            // delete the created file:
            if (!outFile.delete ())
            {
                throw new FileException (
                    "replaceInFile: Error while deleting temporary file: " +
                    outFile + ".");
            } // if
        } // else
    } // replaceInFile


    /**************************************************************************
     * Get the content of a file. <BR/>
     * Read each character of the file and write it into a string.
     *
     * @param   fileName    The name of the file to be read (may also contain
     *                      the path information).
     *
     * @return  The content of the file or <CODE>null</CODE> if there was an
     *          error.
     */
    public static final String getContent (String fileName)
    {
        StringBuffer retVal = new StringBuffer (); // the resulting string
        char[] buffer = new char[1024]; // the buffer for reading
        int count = 0;                  // number of characters read

        try
        {
            BufferedReader reader =
                new BufferedReader (new FileReader (fileName));

            // read the data from the reader and save it to the return value:
            while ((count = reader.read (buffer, 0, 1024)) != -1)
            {
                retVal.append (buffer, 0, count);
            } // while

            // close the data stream:
            reader.close ();
        } // try
        catch (IOException e)
        {
            // no result found:
            retVal = null;
        } // catch

        // return the result:
        return retVal.toString ();
    } // getContent


    /**************************************************************************
     * Get the content of a file with the provided encoding. <BR/>
     * Read each character of the file and write it into a string.
     *
     * @param   fileName    The name of the file to be read (may also contain
     *                      the path information).
     * @param   encoding    Encoding of the file.
     *
     * @return  The content of the file or <CODE>null</CODE> if there was an
     *          error.
     */
    public static final String getContent (String fileName, String encoding)
    {
        StringBuffer retVal = new StringBuffer (); // the resulting string
        char[] buffer = new char[1024]; // the buffer for reading
        int count = 0;                  // number of characters read

        try
        {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream (fileName), encoding));

            // read the data from the reader and save it to the return value:
            while ((count = reader.read (buffer, 0, 1024)) != -1)
            {
                retVal.append (buffer, 0, count);
            } // while

            // close the data stream:
            reader.close ();
        } // try
        catch (IOException e)
        {
            // no result found:
            retVal = null;
        } // catch

        // return the result:
        return retVal.toString ();
    } // getContent


    /**************************************************************************
     * Drop the oid from a filename if applicable. <BR/>
     * Note that filenames can have the format
     * &lt;oid>&lt;filename>.&lt;extension>. <BR/>
     *
     * @param   fileName    The name of the file.
     *
     * @return  The fileName without the oid.
     */
    public static final String trimOid (String fileName)
    {
        // check if the fileName contains an oid:
        if (fileName != null &&
            fileName.startsWith (UtilConstants.NUM_START_HEX) &&
            fileName.length () > 18)
        {
            return fileName.substring (18);
        } // if

        return fileName;
    } // trimOid


    /**************************************************************************
     * Get the extension of a filename. <BR/>
     *
     * @param   fileName    The name of the file
     *
     * @return  the extension
     */
    public static final String getExtension (String fileName)
    {
        String extension = "";
        int pos;

        // check if the fileName contains an oid:
        if ((pos = fileName.lastIndexOf (".")) != -1)
        {
            extension = fileName.substring (pos + 1);
        } // if
        // return the extension
        return extension;
    } // getExtension


    /**************************************************************************
     * Resolve a path. <BR/>
     * If the path is absolute this is already the resulting path for the file.
     * If it is relative the base for the path is the baseDir.
     *
     * @param   baseDir     Directory where the path begins if it is relative.
     * @param   path        The path to be resolved.
     *
     * @return  The resolved path.
     */
    public static File resolvePath (File baseDir, String path)
    {
        String userDir = null;          // the current user directory
        File file = null;               // the actual file

        // make backup of system property user directory:
        userDir = System.getProperty (FileHelpers.SYSPROP_USERDIR);

        try
        {
            System.setProperty (FileHelpers.SYSPROP_USERDIR, baseDir.getAbsolutePath ());

            // resolve the path:
            file = new File (path);
            file = file.getAbsoluteFile ();

            // set user directory back to the original value:
            System.setProperty (FileHelpers.SYSPROP_USERDIR, userDir);
        } // try
        catch (AccessControlException e)
        {
            System.err.println (
                "No write access to system property \"user.dir\"");
        } // catch

        // return the result:
        return file;
    } // resolvePath

} // class FileHelpers
