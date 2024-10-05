/*
 * Class: FormDataValue.java
 */

// package:
package ibs.tech.http;

// imports:
//KR TODO: unsauber
import ibs.io.AContext;
import ibs.io.IOHelpers;
//KR TODO: unsauber
import ibs.io.UploadException;
import ibs.ml.MultilingualTextProvider;
import ibs.tech.http.HttpConstants;
import ibs.util.StringHelpers;
import ibs.util.UtilExceptions;
import ibs.util.file.FileHelpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/******************************************************************************
 * Handle the data of a form. <BR/>
 *
 * @version     $Id: FormDataValue.java,v 1.23 2010/04/07 13:37:17 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR), 981002
 ******************************************************************************
 */
public class FormDataValue extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: FormDataValue.java,v 1.23 2010/04/07 13:37:17 rburgermann Exp $";


    /**
     * The name of the form data. <BR/>
     */
    public String name = null;

    /**
     * The type of the form data. <BR/>
     */
    public int type = 0;

    /**
     * The value of the form data. <BR/>
     */
    public String value = null;

    /**
     * The size of the content, i.e. the value. <BR/>
     */
    public int size = 0;

    /**
     * Beginning of the value within the raw data string. <BR/>
     */
    public int startPos = 0;

    /**
     * The content type. <BR/>
     */
    public String contentType = null;

    /**
     * The filename if file upload. <BR/>
     */
    public String filename = null;

    /**
     * The actual context. <BR/>
     */
    private AContext cxt = null;

    /**
     * Was the file already written?. <BR/>
     */
    private boolean alreadyWritten = false;

    /**
     * Was the file already written?. <BR/>
     */
    private boolean alreadyMoved = false;

    /**
     * Could the file not be written?. <BR/>
     */
    private boolean noFilewritePossible = false;

    /**
     * Could the file not be moved?. <BR/>
     */
    private boolean noMovePossible = false;

    /**
     * Could the directory not be created?. <BR/>
     */
    private boolean noDirectoryCreated = false;

    /**
     * The path where the uploaded file has been saved
     * if writing on the disk was possible. <BR/>
     */
    private String targetDir = HttpConstants.PATH_UPLOAD_TEMP;

    /**
     * File name indicator in multipart forms. <BR/>
     */
    private static final String MULT_FILENAME = "; filename=";
    /**
     * Name indicator in multipart forms. <BR/>
     */
    private static final String MULT_NAME = "; name=";


    /**************************************************************************
     * Create a new FormDataValue instance. <BR/>
     */
    public FormDataValue ()
    {
        // nothing to do
    } // FormDataValue


    /**************************************************************************
     * Create a new FormDataValue instance. <BR/>
     *
     * @param   data        The raw form of the data.
     * @param   startPos    The starting position of this form data within the
     *                      stream.
     * @param   pCxt        The current context.
     *
     * @throws  UploadException
     *          Any error occurred during uploading.
     */
    public FormDataValue (String data, int startPos, AContext pCxt)
        throws UploadException
    {
        this.startPos = startPos;
        this.cxt = pCxt;
        this.getData (data, startPos);
    } // FormDataElement


    /**************************************************************************
     * Create a new FormDataValue instance. <BR/>
     * The size is computed from the length of the value.
     *
     * @param   value       The value itself.
     * @param   size        The size of the value.
     * @param   startPos    The starting position of this value's data within
     *                      the stream.
     * @param   pCxt        The current context.
     */
    public FormDataValue (String value, int size, int startPos, AContext pCxt)
    {
        this.value = value;
        this.size = this.value.length ();
        this.startPos = startPos;
        this.cxt = pCxt;
    } // FormDataValue


    /**************************************************************************
     * Create a new FormDataValue instance. <BR/>
     * The size is computed from the length of the value.
     *
     * @param   name        The name for the value.
     * @param   value       The value itself.
     * @param   startPos    The starting position of this value's data within
     *                      the stream.
     * @param   pCxt        The current context.
     */
    public FormDataValue (String name, String value, int startPos, AContext pCxt)
    {
        this.name = name;
        this.value = value;
        this.size = this.value.length ();
        this.startPos = startPos;
        this.cxt = pCxt;
    } // FormDataValue


    /**************************************************************************
     * Parse the input stream for multipart data. <BR/>
     *
     * @param   data        The raw form of the data.
     * @param   startPos    The starting position of this form data within the
     *                      stream.
     *
     * @throws  UploadException
     *          Any error occurred during uploading.
     */
    private void getData (String data, int startPos) throws UploadException
    {
        // read the Stream for the first time
        this.cxt.readFromStream ();

        // check if the data contains a file:
        if (this.cxt.stringBuffer.indexOf (FormDataValue.MULT_FILENAME) != -1)
        {
            this.getFileData (data, startPos);  // get the file data and return it
        } // if
        else                              // a simple form data
        {
            this.getPostData (data, startPos);  // get the data and return int
        } // else a simple form data
    } // getData


    /**************************************************************************
     * Parse the input stream for multipart data. <BR/>
     * The properties <A HREF="#name">name</A>, <A HREF="#value">value</A>,
     * and <A HREF="#size">size</A> are set.
     *
     * @param   data    The raw form of the data.
     * @param   startPos    The starting position of this form data within the
     *                      stream.
     *
     * @throws  UploadException
     *          Any error occurred during uploading.
     */
    private void getPostData (String data, int startPos) throws UploadException
    {

        int pos = 0;
        int firstQuote = 0;
        int lastQuote = 0;

        this.type = HttpConstants.T_POST;

        // get the name of the raw data:
        pos = this.cxt.stringBuffer.indexOf (FormDataValue.MULT_NAME);
        if (pos >= 0)
        {
            firstQuote = this.cxt.stringBuffer.indexOf ("\"", pos);
            if (firstQuote >= 0)
            {
                lastQuote = this.cxt.stringBuffer.indexOf ("\"", firstQuote + 1);
                if (lastQuote >= 0)
                {
                    this.name = this.cxt.stringBuffer.substring (firstQuote + 1, lastQuote);
                } // if
            } // if
        } // if


        // get the first valuepart out of the raw data:
        pos = this.cxt.stringBuffer.indexOf ("" + (char) 13 + (char) 10, lastQuote);


        // set the value:
        this.value = this.cxt.stringBuffer.substring (pos + 4, this.cxt.stringBuffer.length ());

        // while there is more data to come for this Field of the Form
        // the rest of the data has to be read from the stream and
        // concatenated to the value of this field.
        while (!this.cxt.eoField)
        {
            // read more bytes
            if (this.cxt.readFromStream () > 0)    // there are more bytes?
            {
                // concatenate String:
                this.value += this.cxt.stringBuffer;
            } // if there are more bytes
        } // while
        this.size = this.value.length ();
    } // getPostData


    /**************************************************************************
     * Parse the input stream for multipart data. <BR/>
     * The properties <A HREF="#name">name</A>, <A HREF="#value">value</A>,
     * <A HREF="#contentType">contentType</A>, <A HREF="#filename">filename</A>,
     * and <A HREF="#size">size</A> are set.
     *
     * @param   data        The raw form of the data.
     * @param   startPos    The starting position of this form data within the
     *                      stream.
     *
     * @throws  UploadException
     *          Any error occurred during uploading.
     */
    private void getFileData (String data, int startPos) throws UploadException
    {
        // Create a string from the byte array with default encoding which
        // results in 1 byte for 1 char
        String stringBufferDefEncoding = new String (this.cxt.byteBuffer);
        
        // holds the positions within the string with default encoding
        int pos = 0, firstQuote = 0, lastQuote = 0;
        
        // holds the positions wihtin the properly encoded string buffer
        int firstQuoteString = 0, posString = 0, lastQuoteString = 0;
        
        this.type = HttpConstants.T_FILE;

        // get the name of the raw data:
        pos = stringBufferDefEncoding.indexOf (FormDataValue.MULT_NAME);
        posString = this.cxt.stringBuffer.indexOf (FormDataValue.MULT_NAME);
        if (posString >= 0)
        {
            firstQuote = stringBufferDefEncoding.indexOf ("\"", pos);
            firstQuoteString = this.cxt.stringBuffer.indexOf ("\"", posString);
            if (firstQuoteString >= 0)
            {
                lastQuote = stringBufferDefEncoding.indexOf ("\"", firstQuote + 1);
                lastQuoteString = this.cxt.stringBuffer.indexOf ("\"", firstQuoteString + 1);
                if (lastQuoteString >= 0)
                {
                    this.name = this.cxt.stringBuffer.substring (firstQuoteString + 1, lastQuoteString);
                } // if
            } // if
        } // if

        // get the filename of the raw data:
        pos = stringBufferDefEncoding.indexOf (FormDataValue.MULT_FILENAME, lastQuote + 1);
        posString = this.cxt.stringBuffer.indexOf (FormDataValue.MULT_FILENAME, lastQuoteString + 1);
        if (posString >= 0)
        {
            firstQuote = stringBufferDefEncoding.indexOf ("\"", pos);
            firstQuoteString = this.cxt.stringBuffer.indexOf ("\"", posString);
            if (firstQuoteString >= 0)
            {
                lastQuote = stringBufferDefEncoding.indexOf ("\"", firstQuote + 1);
                lastQuoteString = this.cxt.stringBuffer.indexOf ("\"", firstQuoteString + 1);
                if (lastQuoteString >= 0)
                {
                    this.filename = this.cxt.stringBuffer.substring (firstQuoteString + 1, lastQuoteString);

                    // ensure that there is no leading path information within
                    // the file name:
                    if ((posString = this.filename.lastIndexOf ('/')) >= 0)
                                        // found path separator?
                    {
                        this.filename = this.filename.substring (posString + 1);
                    } // if found path separator
                    if ((posString = this.filename.lastIndexOf ('\\')) >= 0)
                                        // found path separator?
                    {
                        this.filename = this.filename.substring (posString + 1);
                    } // if found path separator
                } // if
            } // if
        } // if

        if (this.filename != null)
        {
            this.filename = StringHelpers.substituteSpecialCharacters (this.filename);
        } // if

        // get the content type of the raw data:
        pos = stringBufferDefEncoding.indexOf ("Content-Type:", lastQuote + 1);
        posString = this.cxt.stringBuffer.indexOf ("Content-Type:", lastQuoteString + 1);
        if (posString >= 0)
        {
            firstQuote = pos + 13;
            firstQuoteString = posString + 13;
            if (firstQuoteString >= 0)
            {
                lastQuote = stringBufferDefEncoding.indexOf ("\n", firstQuote + 1);
                lastQuoteString = this.cxt.stringBuffer.indexOf ("\n", firstQuoteString + 1);
                if (lastQuoteString >= 0)
                {
                    this.contentType = this.cxt.stringBuffer.substring (firstQuoteString + 1, lastQuoteString);
                } // if
            } // if
        } // if

        // get the position where the raw data starts:
        posString = this.cxt.stringBuffer.indexOf ("" + (char) 13 + (char) 10, lastQuoteString);

        this.startPos = stringBufferDefEncoding.indexOf ("" + (char) 13 + (char) 10, lastQuote) + 2;
        this.value = this.targetDir + this.filename;
        this.size = this.value.length ();

        if (this.filename != null && this.filename.length () > 0)
        {
            // create file and write the data to the file:
            this.writeFile (null, HttpConstants.PATH_UPLOAD_TEMP + this.cxt.getSessionId ());
        } // if
        else
        {
            this.filename = null;
            this.alreadyMoved = true;
            this.alreadyWritten = true;
            while ((!this.cxt.eoField) && (!this.cxt.eof))
            {
                // read from stream next bit of bytes
                this.cxt.readFromStream ();
            } // while
        } // else
    } // getFileData


    /**************************************************************************
     * Sends a file data part of a multipart form to a file. <BR/>
     *
     * @param   stream      The stream where the raw data reside.
     * @param   targetDir   Target directory where to write a file within the
     *                      form data.
     *
     * @return  The resulting target file name.
     *
     * @throws  UploadException
     *          Any error occurred during uploading.
     */
    // Perhaps it would be possible to use the method FileHelpers.writeFile()
    public String writeFile (byte[] stream, String targetDir)
        throws UploadException
    {
        String targetDirLocal = targetDir; // variable for local assignments
        String fileName;                // the name of the file itself
        String newFile;                 // the new file path and name
        int lastSepIndex;               // index of the last separator in this.filename

//        targetDir = targetDir.substring (0, targetDir.length ()-1);
        targetDirLocal = targetDirLocal.replace ('/', File.separatorChar);
        targetDirLocal = targetDirLocal.replace ('\\', File.separatorChar);

        // there are two possible separators: \ and /
        // both have to be checked, because the client file system is unknown
        // first try the DOS-based separator
        lastSepIndex = this.filename.lastIndexOf ('\\');
        // if there is no DOS-based separator found it has to be a linux based
        if (lastSepIndex < 0)
        {
            lastSepIndex = this.filename.lastIndexOf ('/');
        } // if

        // Get the filename out of the file data:
/*
        dir = this.filename.substring (0, lastSepIndex + 1);
*/
        fileName = this.filename.substring (lastSepIndex + 1);

        // compute the new filename:
        if (targetDirLocal.charAt (targetDirLocal.length () - 1) != File.separatorChar)
        {
            targetDirLocal += File.separatorChar;
        } // if

        newFile = targetDirLocal + fileName;

        // ensure that the directory exists:
        if (!this.createDirectory (targetDirLocal)) // the directory could not be created?
        {
            // if the file wasn't already written we have to skip the bytes in the
            // stream where the content of the file is held
            if (!this.alreadyWritten)
            {
                while ((!this.cxt.eoField) && (!this.cxt.eof))
                {
                    // read from stream next bit of bytes
                    this.cxt.readFromStream ();
                } // while
                this.alreadyWritten = true;
                this.noFilewritePossible = true;
                this.noDirectoryCreated = true;
            } // if
        } // if

        if ((this.alreadyWritten) && (!this.noFilewritePossible) &&
            (!this.alreadyMoved))
        {
            // file has already been written and not yet been moved
            // move the file to this directory
            // if the file already exists in the directory, delete it!
            FileHelpers.deleteFile (newFile);
            // now rename the file to the new filename!
            if (FileHelpers.renameFile (this.targetDir + fileName, newFile))
            {
                // if the renaming was successful,
                // delete the temporary directory
                FileHelpers.deleteDir (this.targetDir);
                // and change the targetDir
                this.targetDir = targetDirLocal;
            } // if
            else
            {
                this.noMovePossible = true;
            } // else
            this.alreadyMoved = true;
        } // if
        else if ((!this.noFilewritePossible) && (!this.alreadyWritten))
        {
            this.targetDir = targetDirLocal;

            // send the data to the file:
            this.sendToFile (newFile);
        } // else

        // possible Exceptions
        if (this.noDirectoryCreated)
        {
            // TODO RB: Call  
            //          MultilingualTextProvider.getMessage (UtilExceptions.EXC_BUNDLE,
            //              UtilExceptions.ML_E_NODIRECTORYCREATED, env)
            //          to get the text in the correct language
            throw new UploadException (UtilExceptions.ML_E_NODIRECTORYCREATED + targetDirLocal);
        } // if
        else if (this.noFilewritePossible)
        {
            // TODO RB: Call  
            //          MultilingualTextProvider.getMessage (UtilExceptions.EXC_BUNDLE,
            //              UtilExceptions.ML_E_NOFILEWRITE, env)
            //          to get the text in the correct language
            throw new UploadException (UtilExceptions.ML_E_NOFILEWRITE +
                this.targetDir + fileName);
        } // else if
        else if (this.noMovePossible)
        {
            // TODO RB: Call  
            //          MultilingualTextProvider.getMessage (UtilExceptions.EXC_BUNDLE,
            //              UtilExceptions.ML_E_NOFILEMOVE, env)
            //          to get the text in the correct language
            throw new UploadException (UtilExceptions.ML_E_NOFILEMOVE +
                this.targetDir + fileName + "-->" + newFile);
        } // else if

        // return the target file name:
        return fileName;
    } // writeFile


    /**************************************************************************
     * Send the data to a file. <BR/>
     *
     * @param   file    The file path and name
     *
     * @throws  UploadException
     *          Any error occurred during uploading.
     */
    // Perhaps it would be possible to use the method FileHelpers.sendToFile()
    protected void sendToFile (String file) throws UploadException
    {
        // define the local file stream handler
        FileOutputStream fileOut = null;

        try
        {
            // open file stream handlers
            fileOut = new FileOutputStream (file);

            // send the data:
            try
            {
                // write the first part of the file on the stream
                fileOut.write (this.cxt.byteBuffer, this.startPos,
                    this.cxt.bytes - this.startPos);
                // more bytes to come?
                while ((!this.cxt.eoField) && (!this.cxt.eof))
                {
                    // read from stream next bit of bytes
                    this.cxt.readFromStream ();
                    // write the raw data out
                    fileOut.write (this.cxt.byteBuffer, 0, this.cxt.bytes);
                } // while

                this.noFilewritePossible = false;
                // File has been written
                this.alreadyWritten = true;
            } // try
            catch (IOException e)
            {
                // over read the bytes for this field
                while ((!this.cxt.eoField) && (!this.cxt.eof))
                {
                    // read from stream next bit of bytes:
                    this.cxt.readFromStream ();
                } // while

                // I/O error during writing to file
                this.noFilewritePossible = true;
            } // catch
        } // try
        catch (IOException e)
        {
            // over read the bytes for this field
            while ((!this.cxt.eoField) && (!this.cxt.eof))
            {
                // read from stream next bit of bytes:
                this.cxt.readFromStream ();
            } // while

            // an I/O error occurred during opening or closing the file
            this.noFilewritePossible = true;
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
                    // display error message:
                    IOHelpers.printError (
                        "The output file could not be closed.",
                        this, e, true);
                } // catch
            } // if
        } // finally
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
    // Perhaps it would be possible to use the method FileHelpers.createDirectory()
    protected boolean createDirectory (String path)
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
     * Gets the path to the written file. <BR/>
     *
     * @return  The path to the written file.
     */
    public String getFilePath ()
    {
        int lastSepIndex = this.filename.lastIndexOf ('\\');
        String fileName = null;
        fileName = this.filename.substring (lastSepIndex + 1);

        return this.targetDir + fileName;
    } // getFilePath

} // class FormDataValue
