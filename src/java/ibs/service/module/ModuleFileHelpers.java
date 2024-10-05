/*
 * Class: ModuleFileHelpers.java
 */

// package:
package ibs.service.module;

// imports:
import ibs.di.DIConstants;
import ibs.io.IOHelpers;
import ibs.ml.MultilangConstants;
import ibs.ml.MultilingualTextProvider;
import ibs.util.file.FileHelpers;
import ibs.util.file.FileManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * File helpers for modules. <BR/>
 *
 * @version     $Id: ModuleFileHelpers.java,v 1.14 2011/11/11 12:19:20 btatzmann Exp $
 *
 * @author      Klaus, 17.12.2003
 ******************************************************************************
 */
public abstract class ModuleFileHelpers extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ModuleFileHelpers.java,v 1.14 2011/11/11 12:19:20 btatzmann Exp $";




    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Copy all files from one directory to another. <BR/>
     * Only the files, which match the filePattern, are copied.
     *
     * @param   sourceDirName   The source dir for the files.
     * @param   targetDirName   The target dir where to copy the files to.
     * @param   filePattern     The file pattern.
     *                          If this is <CODE>null</CODE> all files within
     *                          the source directory are copied to the target
     *                          directory.
     * @param   filePrefix      Prefix for all files in the target directory.
     *                          <CODE>null</CODE> means no prefix, use original
     *                          file name.
     *
     * @return  <CODE>true</CODE> if at least one file was copied.
     *          <CODE>false</CODE> if there was no file to copy or the target
     *          files are already existing with the same sizes as the original
     *          files.
     *
     * @throws  ModuleLoadingException
     *          An error occurred during copying the files.
     */
    protected static boolean copyFiles (String sourceDirName,
                                        String targetDirName,
                                        String filePattern,
                                        String filePrefix)
        throws ModuleLoadingException
    {
        String filePatternLocal = filePattern; // variable for local assignments
        boolean retVal = false;         // return value
        FilenameFilter dirFilter = null; // filter for directory
        FilenameFilter fileFilter = null; // filter for files
        File[] files = null;            // all files to be loaded
        File sourceDir = new File (sourceDirName);
        File targetFile = null;         // the target file


        // set universal directory filter which finds all directories:
        dirFilter = FileManager.getFilter ("*");

        // check if there is a pattern set:
        if (filePatternLocal == null)
        {
            filePatternLocal = "*";
        } // if

        fileFilter = FileManager.getFilter (filePatternLocal);

        // get the files recursively:
        files =
            FileHelpers.getFilesArray (sourceDir, dirFilter, fileFilter, true);
//IOHelpers.printMessage ("sourceDir: " + sourceDir);
//IOHelpers.printMessage ("files: " + files.length);

        // loop through all files
        // for each file:
        // - get it
        // - replace configuration variables by the values
        // - store the file to the new location
        for (int i = 0; i < files.length; i++)
        {
            targetFile = new File (targetDirName +
                files[i].getPath ().substring (sourceDirName.length ()));
            if (filePrefix != null)
            {
                targetFile = new File (targetFile.getParent () +
                    File.separator + filePrefix + targetFile.getName ());
            } // if

            // check if the target file already exists and is the same as the
            // file to be copied:
            if (!targetFile.exists () ||
                files[i].length () != targetFile.length ())
            {
                // ensure that the directory exists:
                targetFile.getParentFile ().mkdirs ();

                // copy the file:
                FileHelpers.copyFile (files[i].getPath (), targetFile.getPath ());
                retVal = true;
            } // if
        } // for i

        // return the result:
        return retVal;
    } // copyFiles
    
    
    /**************************************************************************
     * Merges all resource bundles of one locale to the one resource bundle
     * at the target directory. Can be used to cummulate several resource
     * bundles to one to allow that only one set of resource bundles
     * has to be regarded during runtime.
     * 
     * Example:
     * <feature1>_<module1>_<resourceBundle>_<locale1>.properties
     * <feature2>_<module1>_<resourceBundle>_<locale1>.properties
     * <feature2>_<module2>_<resourceBundle>_<locale1>.properties
     *
     * are copied to ...
     * <resourceBundle>_<locale1>.properties
     *
     * <feature1>_<module1>_<resourceBundle>_<locale2>.properties
     * <feature1>_<module2>_<resourceBundle>_<locale2>.properties
     * <feature2>_<module1>_<resourceBundle>_<locale2>.properties
     *
     * are copied to ...
     * <resourceBundle>_<locale2>.properties
     *
     * @param   sourceDirName   The source dir for the files.
     * @param   targetDirName   The target dir for the files.
     * @param   resourceBundle  Defines the name of the resource bundles that
     *                          should be merged to the target dir.
     *
     * @throws  ModuleLoadingException
     *          An error occurred during merging the files.
     */
    protected static void mergeResourceBundles (String sourceDirName,
                                     String targetDir,
                                     String resourceBundle)
        throws ModuleLoadingException
    {
        String rbPattern = "*" + MultilangConstants.FILEEXT_RESOURCE_BUNDLE;
        String specificRbPattern = "*" + resourceBundle + "*";
        
        FilenameFilter dirFilter = null; // filter for directory
        FilenameFilter rbFileFilter = null, specificRbFileFilter = null; // filters for files
        File[] files = null;            // all files to be loaded
        File sourceDir = new File (sourceDirName);

        // set universal directory filter which finds all directories:
        dirFilter = FileManager.getFilter ("*");
        
        // create file filters:
        // since there is no filter which allows a pattern like *x*x two filters are used
        rbFileFilter = FileManager.getFilter (rbPattern);
        specificRbFileFilter = FileManager.getFilter (specificRbPattern);

        // filter files first with resource bundle filter:
        files =
            FileHelpers.getFilesArray (sourceDir, dirFilter, rbFileFilter, true);
        
        // loop through all files
        for (int i = 0; i < files.length; i++)
        {         
            // check if the file also matches the specific filter
            if (specificRbFileFilter.accept (sourceDir, files[i].getName ()))
            {
                // retrieve the start position of the locale postfix 
                int postFixStartPos = files[i].getName ().indexOf (resourceBundle) +
                    resourceBundle.length (); 
             
                // compute the target file name
                String targetFilename = resourceBundle + files[i].getName ().substring (postFixStartPos);
                
                // check if the target file already exists:
                boolean targetFileExists = FileHelpers.exists (targetDir + targetFilename);
                
                // add header:
                FileHelpers.sendToFile (MultilingualTextProvider.getResourceBundleModuleHeader (files[i], targetFileExists).
                        getBytes (),
                        targetDir + targetFilename, true);
                
                // append the file:
                FileHelpers.copyFile (files[i].getPath (), targetDir + targetFilename, true);                
            } // if
        } // for i
    } // copyFiles

    
    /**************************************************************************
     * Copy all files from one directory to another. <BR/>
     * Only the files, which match the filePattern, are copied.
     * While copying the configuration variables within the files are replaced
     * with their current values.
     *
     * @param   sourceDirName   The source dir for the files.
     * @param   targetDirName   The target dir where to copy the files to.
     * @param   filePattern     The file pattern.
     *                          If this is <CODE>null</CODE> all files within
     *                          the source directory are copied to the target
     *                          directory.
     * @param   confVars        All known configuration variables.
     *
     *
     * @throws  ModuleLoadingException
     *          An error occurred during copying the files.
     */
    protected static void copyFiles (String sourceDirName,
                                     String targetDirName,
                                     String filePattern,
                                     ConfVarContainer confVars)
        throws ModuleLoadingException
    {
        copyFiles (sourceDirName, targetDirName, filePattern, null, confVars, null);
    } // copyFiles
    
    
    /**************************************************************************
     * Copy all files from one directory to another. <BR/>
     * Only the files, which match the filePattern and do not match the
     * excludePattern, are copied.
     * While copying the configuration variables within the files are replaced
     * with their current values.
     *
     * @param   sourceDirName   The source dir for the files.
     * @param   targetDirName   The target dir where to copy the files to.
     * @param   filePattern     The file pattern.
     *                          If this is <CODE>null</CODE> all files within
     *                          the source directory are copied to the target
     *                          directory.
     * @param   excludePatterns Specifies which files should be excluded.
     * @param   confVars        All known configuration variables.
     * @param   encoding        The file encoding.
     *
     * @throws  ModuleLoadingException
     *          An error occurred during copying the files.
     */
    protected static void copyFiles (String sourceDirName,
                                     String targetDirName,
                                     String filePattern,
                                     String[] excludePatterns,
                                     ConfVarContainer confVars,
                                     String encoding)
        throws ModuleLoadingException
    {
        String filePatternLocal = filePattern; // variable for local assignments
        FilenameFilter dirFilter = null; // filter for directory
        FilenameFilter fileFilter = null; // filter for files
        Collection<FilenameFilter> excludeFileFilters = new ArrayList<FilenameFilter>(); // exclude filters for files
        File[] files = null;            // all files to be loaded
        File sourceDir = new File (sourceDirName);

        // set universal directory filter which finds all directories:
        dirFilter = FileManager.getFilter ("*");

        // check if there is a pattern set:
        if (filePatternLocal == null)
        {
            filePatternLocal = "*";
        } // if
        fileFilter = FileManager.getFilter (filePatternLocal);
        
        // check if an exclude pattern has been provided
        if (excludePatterns != null)
        {
            for (int k = 0; k < excludePatterns.length; k++)
            {
                // create the exclude filters
                excludeFileFilters.add (FileManager.getFilter (excludePatterns[k]));
            } // for k
        } // if
        
        // get the files recursively:
        files =
            FileHelpers.getFilesArray (sourceDir, dirFilter, fileFilter, true);
//IOHelpers.printMessage ("sourceDir: " + sourceDir);
//IOHelpers.printMessage ("files: " + files.length);

        // loop through all files
        // for each file:
        // - get it
        // - replace configuration variables by the values
        // - store the file to the new location
        for (int i = 0; i < files.length; i++)
        {
            boolean excludeFile = false;

            if (excludeFileFilters != null && excludeFileFilters.size () != 0)
            {
                Iterator<FilenameFilter> excludedFileFiltersIterator = excludeFileFilters.iterator ();
                while (excludedFileFiltersIterator.hasNext ())
                {
                    if (excludedFileFiltersIterator.next ().accept (sourceDir, files[i].getName ()))
                    {
                        excludeFile = true;
                    } // if
                } // while
            } // if
            
            if (!excludeFile)
            {
                ModuleFileHelpers.copyFile (files[i], sourceDirName, targetDirName, confVars, encoding);
            } // if
        } // for i
    } // copyFiles


    /**************************************************************************
     * Copy a file. <BR/>
     * The file is copied from the source directory into the target directory.
     * All occurrencies of the configuration variables are replaced by their
     * current values.
     *
     * @param   file            The file to be loaded.
     * @param   sourceDirName   The source dir for the files.
     * @param   targetDirName   The target dir where to copy the files to.
     * @param   confVars        All known configuration variables.
     * @param   encoding        The file encoding.
     *
     * @throws  ModuleLoadingException
     *          An error occurred during copying the file.
     */
    protected static void copyFile (File file,
                                    String sourceDirName,
                                    String targetDirName,
                                    ConfVarContainer confVars,
                                    String encoding)
        throws ModuleLoadingException
    {
        File outFile = null;            // the output file
        Reader reader = null;           // the file reader
        Writer writer = null;           // the file writer

        outFile = new File (
            targetDirName + file.getPath ().substring (sourceDirName.length ()));

        // ensure that the target directory exists:
        if (!outFile.getParentFile ().exists ())
        {
            String msg = "loadFile: Could not create necessary directory " +
                outFile.getParentFile ();
            try
            {
                // create the directory and its parent directories:
                if (!outFile.getParentFile ().mkdirs ())
                {
                    throw new ModuleLoadingException (msg);
                } // if
            } // try
            catch (SecurityException e)
            {
                throw new ModuleLoadingException (msg, e);
            } // catch
        } // if

        try
        {
            try
            {
                // if encoding is not set try to retrieve the encoding from the content:
                if (encoding == null)
                {
                    String fileExtension = "." + FileHelpers.getExtension (file.getName ());
    
                    // check if the file is of type xml or xsl and ...
                    if ((fileExtension.equals (DIConstants.FILEEXTENSION_XML) ||
                            fileExtension.equals (DIConstants.FILEEXTENSION_XSL)) &&
                            // ... the encoding is set to UTF-8
                            ModuleFileHelpers.isXmlEncoding (file, DIConstants.CHARACTER_ENCODING))
                    {
                        // set UTF-8 as encoding for reader and writer
                        encoding = DIConstants.CHARACTER_ENCODING;
                    } // if
                } // if
            } // try
            catch (IOException e)
            {
                IOHelpers.printWarning ("ModuleFileHelpers.copyFile", null,
                        "loadFile: Error during retrieval of xml file encoding: " + file +
                        ". Default encoding is used.");
            } // catch

            // create input file reader:
            reader =
                encoding != null ?
                        new BufferedReader(new InputStreamReader(new FileInputStream (file), encoding)) :
                        new BufferedReader(new InputStreamReader(new FileInputStream (file)));
        } // try
        catch (FileNotFoundException e)
        {
            throw new ModuleLoadingException (
                "loadFile: Error while opening input file: " + file, e);
        } // catch
        catch (UnsupportedEncodingException e)
        {
            throw new ModuleLoadingException (
                    "loadFile: Unsupported encoding error while creating file reader: " + encoding, e);
        } // catch

        try
        {
            // create output file writer:
            writer = encoding != null ?
                    new BufferedWriter (new OutputStreamWriter (new FileOutputStream (outFile), encoding)) :
                    new BufferedWriter (new OutputStreamWriter (new FileOutputStream (outFile)));
        } // try
        catch (FileNotFoundException e)
        {
            throw new ModuleLoadingException (
                "loadFile: Error while opening output file: " + outFile, e);
        } // catch
        catch (UnsupportedEncodingException e)
        {
            throw new ModuleLoadingException (
                "loadFile: Unsupported encoding error while creating file writer: " + encoding, e);
        } // catch

        // parse the data stream:
        ModuleFileHelpers.parseStream (reader, writer, file.getName (), confVars);

        try
        {
            // close the output data stream:
            writer.close ();
        } // try
        catch (IOException e)
        {
            throw new ModuleLoadingException (
                "loadFile: Error while closing output file: " + outFile, e);
        } // catch

        try
        {
            // close the input data stream:
            reader.close ();
        } // try
        catch (IOException e)
        {
            throw new ModuleLoadingException (
                "loadFile: Error while closing input file: " + file, e);
        } // catch
    } // copyFile


    /**************************************************************************
     * Validates if the given XML file contains the provided encoding within
     * its header.
     *
     * @param   file            The file to be evaluated.
     * @param   encoding        The encoding to compare with.
     *
     * @returns the encoding if it is not the default encoding
     */
    protected static boolean isXmlEncoding (File file, String encoding)
            throws FileNotFoundException, IOException
    {
        boolean encodingEqual = false;

        // initialize the reader
        Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream (file)));
        
        // read the first 100 characters
        char[] cbuf = new char[100];
        reader.read (cbuf);
        String xmlHeader = new String (cbuf);
        
        // check the xml file encoding
        if (xmlHeader.indexOf (encoding) > 0)
        {
            encodingEqual = true;
        } // if
        
        reader.close ();

        return encodingEqual;
    } // isXmlEncoding


    /**************************************************************************
     * Move files from one directory to another. <BR/>
     * Only the files, which match the filePattern, are moved. <BR/>
     * This filePattern is only checked for the first level. Below this
     * all files are moved.
     *
     * @param   sourceDirName   The source dir for the files.
     * @param   targetDirName   The target dir where to move the files to.
     * @param   filePattern     The file pattern.
     *                          If this is <CODE>null</CODE> all files within
     *                          the source directory are moved to the target
     *                          directory.
     * @param   filePrefix      Prefix for all files in the target directory.
     *                          <CODE>null</CODE> means no prefix, use original
     *                          file name.
     *
     * @return  <CODE>true</CODE> if at least one file was moved.
     *          <CODE>false</CODE> if there was no file to move or the target
     *          files are already existing with the same sizes as the original
     *          files.
     *
     * @throws  ModuleLoadingException
     *          An error occurred during moving the files.
     */
    protected static boolean moveFiles (String sourceDirName,
                                        String targetDirName,
                                        String filePattern,
                                        String filePrefix)
        throws ModuleLoadingException
    {
        String filePatternLocal = filePattern; // variable for local assignments
        boolean retVal = false;         // return value
        FilenameFilter fileFilter = null; // filter for files
        Vector<File> files = new Vector<File> ();   // all files to be loaded
        File sourceDir = new File (sourceDirName);
        File targetFile = null;         // the target file
        File[] contents = null;         // the contents of the directory

        // check if there is a pattern set:
        if (filePatternLocal == null)
        {
            filePatternLocal = "*";
        } // if

        // get the file filter:
        fileFilter = FileManager.getFilter (filePatternLocal);

        // get the files for the first level:
        // get the contents of the directory:
        contents = sourceDir.listFiles ();

        // check if there was a content found:
        if (contents != null)
        {
            // handle elements:
            for (int i = 0; i < contents.length; i++)
            {
                // check for the type of the element:
                if (fileFilter.accept (sourceDir, contents[i].getName ()))
                                        // add file?
                {
                    // add the actual file to the result:
                    files.add (contents[i]);
                } // if add file
            } // for i
        } // if

        // loop through all files
        // for each file:
        // - get it
        // - move the file to the new location
        for (Iterator<File> iter = files.iterator (); iter.hasNext ();)
        {
            File file = iter.next ();
            targetFile = new File (targetDirName + File.separator +
                file.getPath ().substring (sourceDirName.length ()));
            if (filePrefix != null)
            {
                targetFile = new File (targetFile.getParent () +
                    File.separator + filePrefix + targetFile.getName ());
            } // if

            // check if the target file already exists and is the same as the
            // file to be moved:
            if (!targetFile.exists () ||
                file.length () != targetFile.length ())
            {
                // ensure that the directory exists:
                targetFile.getParentFile ().mkdirs ();

                // check if the file entry is a directory:
                if (file.isDirectory ())
                {
                    // move the directory:
                    retVal = FileHelpers.moveDirectory (file, targetFile);
                } // if
                else
                {
                    // move the file:
                    retVal = FileHelpers.moveFile (file.getPath (),
                        targetFile.getPath ());
                } // else
            } // if
        } // for iter

        // return the result:
        return retVal;
    } // moveFiles


    /**************************************************************************
     * Parse a stream. <BR/>
     * The stream is parsed, each configuration variable is replaced by its
     * value. Then the file is written to the writer.
     *
     * @param   reader      The input stream reader.
     * @param   writer      The output stream writer.
     * @param   streamName  The name of the stream.
     *                      Used for error messages.
     * @param   confVars    All known configuration variables.
     *
     * @throws  ModuleLoadingException
     *          An error occurred during loading the files.
     */
    protected static void parseStream (Reader reader,
                                Writer writer,
                                String streamName,
                                ConfVarContainer confVars)
        throws ModuleLoadingException
    {
        int type = 0;                   // type of token
        StreamTokenizer tokenizer = null;
        String sepVal = "" + ModuleConstants.CONFVAR_SEPARATOR;
        String sepEmpty = "";
        String sep = sepEmpty;          // separator to write before token
        String value = null;
        boolean beforeVar = false;      // we are immediately before a variable
        boolean afterVar = false;       // immediately after a variable
        Vector<String> undefinedVars = new Vector<String> (); // undefined variables

        try
        {
            // setup syntax table:
            tokenizer = new StreamTokenizer (reader);
            tokenizer.resetSyntax ();
            tokenizer.wordChars (0, 255);
/*
            tokenizer.ordinaryChars (0, '/');
            tokenizer.wordChars (0, '/');
            tokenizer.ordinaryChars (0, '"');
            tokenizer.wordChars (0, '"');
            tokenizer.ordinaryChars (0, '\'');
            tokenizer.wordChars (0, '\'');
            tokenizer.whitespaceChars (ModuleConstants.CONFVAR_SEPARATOR,
                                       ModuleConstants.CONFVAR_SEPARATOR);
*/
            tokenizer.ordinaryChar (ModuleConstants.CONFVAR_SEPARATOR);

            // loop through all tokens of the file:
            while ((type = tokenizer.nextToken ()) != StreamTokenizer.TT_EOF)
            {
                switch (type)
                {
                    case StreamTokenizer.TT_WORD:
                        if (beforeVar)  // variable is possible?
                        {
                            try
                            {
                                // try to get the value for the variable:
                                value = ModuleFileHelpers.getValue (
                                    tokenizer.sval, confVars);
                                if (value != null) // token is a variable?
                                {
                                    // write the value instead of the variable name:
                                    writer.write (value);
                                    afterVar = true;
                                } // if token is a variable
                                else            // the token is no variable
                                {
                                    // write the original value:
                                    writer.write (sep + tokenizer.sval);
                                } // else the token is no variable
                            } // try
                            catch (ModuleLoadingException e)
                            {
                                undefinedVars.add (tokenizer.sval);
                                // write the original value:
                                writer.write (sep + tokenizer.sval);
                            } // catch

                            beforeVar = false;
                        } // if variable is possible
                        else            // no variable possible
                        {
                            // write the original value:
                            writer.write (sep + tokenizer.sval);
                        } // else no variable possible
                        break;

                    case StreamTokenizer.TT_NUMBER:
                        // write the number unchanged to the output:
                        writer.write (sep + tokenizer.nval);
                        beforeVar = false;
                        break;

                    case ModuleConstants.CONFVAR_SEPARATOR:
                        // check state:
                        if (afterVar)   // immediately after a variable?
                        {
                            afterVar = false;
                            sep = sepEmpty;
                        } // if immediately after a variable
                        else if (!beforeVar) // not before a variable
                        {
                            sep = sepVal;
                            beforeVar = true;
                        } // else if not before a variable
                        else            // repeated separator
                        {
                            writer.write (ModuleConstants.CONFVAR_SEPARATOR);
                        } // else repeated separator
                        break;

                    default:
                } // switch type
            } // while

            // write the last separator:
            if (beforeVar)
            {
                writer.write (sep);
            } // if

            // check if there were some undefined variables:
            if (undefinedVars.size () > 0)
            {
                // compose the message:
                String message =
                    "The following variables are not defined in " + streamName;

                for (Iterator<String> iter = undefinedVars.iterator (); iter.hasNext ();)
                {
                    message += "\n    " + iter.next ();
                } // for iter

                // display the message:
                IOHelpers.printWarning ("parseStream", ModuleFileHelpers.class, message);
            } // if
        } // try
        catch (IOException e)
        {
            throw new ModuleLoadingException (
                "parseStream: Error while parsing stream " + streamName, e);
        } // catch
    } // parseStream


    /**************************************************************************
     * Get the value for a configuration variable. <BR/>
     * If the token starts with the correct variable prefix this function tries
     * to get the corresponding value out of the configuration values.
     * If the configuration value is not found, an exception is raised. <BR/>
     * Otherwise <CODE>null</CODE> is returned.
     *
     * @param   token       The token for which to get the value.
     * @param   confVars    All known configuration variables.
     *
     * @return  The variable value or
     *          <CODE>null</CODE> if the token is no variable.
     *
     * @throws  ModuleLoadingException
     *          If the variable is not defined.
     */
    protected static String getValue (String token, ConfVarContainer confVars)
        throws ModuleLoadingException
    {
        String value = null;            // the variable value

//IOHelpers.printMessage ("    token: " + token);
        // check if the variable prefix is part of the variable name:
        if (confVars != null &&
            token.indexOf (ModuleConstants.CONFVAR_PREFIX) > -1)
        {
            // get the variable:
            value = confVars.getValue (
                token.substring (ModuleConstants.CONFVAR_PREFIX.length ()));
//IOHelpers.printMessage ("    found value: " + value);
            if (value == null)
            {
                throw new ModuleLoadingException ("Variable " + token +
                    " not defined.");
            } // if
        } // if

        // return the value:
        return value;
    } // getValue


    /**************************************************************************
     * Delete all files from one directory. <BR/>
     * Only the files, which match the filePattern, are deleted.
     *
     * @param   sourceDirName   The source dir for the files.
     * @param   filePattern     The file pattern.
     *                          If this is <CODE>null</CODE> all files within
     *                          the source directory are deleted.
     *
     * @throws  ModuleLoadingException
     *          An error occurred during copying the files.
     */
    protected static void deleteFiles (String sourceDirName,
                                       String filePattern)
        throws ModuleLoadingException
    {
        String filePatternLocal = filePattern; // variable for local assignments
        FilenameFilter dirFilter = null; // filter for directory
        FilenameFilter fileFilter = null; // filter for files
        File sourceDir = new File (sourceDirName);
        int result = 0;                 // the deletion result

        // set universal directory filter which finds all directories:
        dirFilter = FileManager.getFilter ("*");

        // check if there is a pattern set:
        if (filePatternLocal == null)
        {
            filePatternLocal = "*";
        } // if

        fileFilter = FileManager.getFilter (filePatternLocal);

        // get the files recursively:
        result =
            FileHelpers.deleteFiles (sourceDir, dirFilter, fileFilter, true);

        if (result != 0)
        {
            throw new ModuleLoadingException (
                "Module files in \"" + sourceDirName +
                "\" with pattern \"" + filePatternLocal +
                "\" could not be completely deleted: return value = " + result);
        } // if
    } // deleteFiles

} // class ModuleFileHelpers
