/*
 * Class: HTTPScriptConnector_01.java
 */

// package:
package ibs.di.connect;

// imports:
//TODO: unsauber
import ibs.bo.Datatypes;
//TODO: unsauber
import ibs.bo.OID;
import ibs.di.DIArguments;
import ibs.di.DIConstants;
import ibs.di.DIHelpers;
import ibs.di.DIMessages;
import ibs.di.DITokens;
import ibs.di.Log_01;
import ibs.di.connect.ConnectionFailedException;
import ibs.di.connect.Connector_01;
//TODO: unsauber
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
//TODO: unsauber
import ibs.service.user.User;
import ibs.tech.html.TableElement;
import ibs.util.file.FileHelpers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/******************************************************************************
 * The HTTPScriptConnector_01 Class reads and writes files from an to an
 * http server CGI script.<BR/>
 *
 * @version     $Id: HTTPScriptConnector_01.java,v 1.23 2010/04/07 13:37:05 rburgermann Exp $
 *
 * @author      Danny Xavier (DX), 000806
 ******************************************************************************
 */
public class HTTPScriptConnector_01 extends Connector_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: HTTPScriptConnector_01.java,v 1.23 2010/04/07 13:37:05 rburgermann Exp $";


    /**
     *  URL of the HTTP server.<BR/>
     */
    public String importScriptUrl  = null;

     /**
     *  the file to be extracted from the server.<BR/>
     */
    public String dirScriptUrl  = null;

    /**
     * delimiter used for file listing.<BR/>
     */
    public String dirFileDelimiter  =  DIConstants.FILE_DELIMITER;

    /**
     * URL of the script that will read the export file.<BR/>
     */
    public String exportScriptUrl  = null;

    /**
     * MIME type used to send the export file.<BR/>
     */
    public String exportMIMETYPE  = DIConstants.MIMETYPE_FORMDATA;

   /**
    * name of the export file to be sent.<BR/>
    */
    public String exportFileName  = null;

   /**
    * flag to process a HTML header around content from the CGI scripts.<BR/>
    */
    public boolean isUseHTMLHeader = false;


    /**************************************************************************
     * Creates a HTTPScriptConnector_01 Object. <BR/>
     */
    public HTTPScriptConnector_01 ()
    {
        // call constructor of super class Connector_01:
        super ();
    } // HTTPScriptConnector_01


    /**************************************************************************
     * Creates a HTTPScriptConnector_01 Object. <BR/>
     *
     * @param   oid     oid of the object.
     * @param   user    user that created the object.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public HTTPScriptConnector_01 (OID oid, User user)
    {
        // call constructor of super class Connector_01:
        super (oid, user);
    } // HTTPScriptConnector_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set class specifics:
        this.connectorType = DIConstants.CONNECTORTYPE_HTTP;

        // set stored procedure names:
        this.procCreate = "p_Connector_01$create";
        this.procRetrieve = "p_Connector_01$retrieve";
        this.procDelete = "p_Connector_01$delete";
        this.procChange = "p_Connector_01$change";

        // set number of parameters for procedure calls:
        this.specificRetrieveParameters = 12;
        this.specificChangeParameters = 12;
    } // initClassSpecifics


    /**************************************************************************
     * Sets the arguments to class specific properties. <BR/>
     */
    public void setArguments ()
    {
        this.importScriptUrl = this.arg1;
        this.dirScriptUrl = this.arg2;
        this.dirFileDelimiter = this.arg3;
        if (this.dirFileDelimiter == null)
        {
            this.dirFileDelimiter = DIConstants.FILE_DELIMITER;
        } // if
        this.exportScriptUrl = this.arg4;
        this.exportFileName = this.arg5;
        this.exportMIMETYPE = this.arg6;
        if (this.exportMIMETYPE == null)
        {
            this.exportMIMETYPE = DIConstants.MIMETYPE_FORMDATA;
        } // if
    } // setArguments


    /**************************************************************************
     * Helper member function opens a URL connection and gets the content from
     * this URL. <BR/>
     *
     * @param  urlStr      the URL to connect to
     *
     * @exception   ConnectionFailedException
     *              Script failed to respond correctly.
     *
     * @return  string having the content of the file
     */
    private String readUrlContent (String urlStr)
        throws ConnectionFailedException
    {
        String line = "";
        BufferedReader bufferedReader;
        StringBuffer stringBuffer = new  StringBuffer ();
        InputStreamReader inputStreamReader;
        URL url;

        try
        {
            // create a url instance
            url = new URL (urlStr);
            // Open the stream and start reading from it
            inputStreamReader = new InputStreamReader (url.openStream ());
            // store in a buffer
            bufferedReader = new BufferedReader (inputStreamReader);
            while ((line = bufferedReader.readLine ()) != null)
            {
                // append to the buffer
                stringBuffer.append (line);
            } // while ((htmlPage = bufReader.readLine ()) != null)
            // return the content of from the script
            return stringBuffer.toString ();
        } // try
        catch (IOException e)
        {
            // Propagate the exception to the calling function.
            throw new ConnectionFailedException (
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_SCRIPT_FAILURE, env));
        } // catch
    } // readUrlContent


    /**************************************************************************
     * A helper function for returning the string found between the
     * pre tags in the html page.<BR/>
     * The CGI script used for testing produces a header around the content
     * it returns. The future version will not produce the headers anymore.
     * The isUseHTMLHeader is used to turn on and off the processing of
     * the headers.<BR/>
     *
     * @param   htmlPage    The page containing the content.
     *
     * @return  string having the string between the <PRE> </PRE>
     */
    private String getContent (String htmlPage)
    {
        if (this.isUseHTMLHeader)
        {
            String  stringVar = new String ();
            int preTag = htmlPage.indexOf (DIConstants.PRE_TAG);
            //get the index of </pre> tag
            int preEndTag = htmlPage.indexOf (DIConstants.END_PRE_TAG);
            //make string between these two tags <pre> and </pre>
            stringVar = htmlPage.substring (preTag + 5, preEndTag);
            // return the string between the <pre></pre> tags
            return stringVar;
        } // if (this.isUseHTMLHeader)

        // no HTML header
        return htmlPage;
    } // getContent


    /**************************************************************************
     * Initializes the connector and checks the settings. <BR/>
     *
     * @throws  ConnectionFailedException
     *          The connection could not be established.
     */
    public void initConnector ()
        throws ConnectionFailedException
    {
        // check first if we have a directory set where we can copy the files
        // if not create a temp directory
        if (this.isCreateTemp &&
            (this.getPath () == null || this.getPath ().length () == 0))
        {
            this.setPath (this.createTempDir ());
        } // if

        try
        {
            // check if all necessary parameters set:
            if (this.isImportConnector) // should the connector be used for import?
            {
                // construct a url from the string
                URL importScriptURL  = new URL (this.importScriptUrl);
                URL dirScriptURL     = new URL (this.dirScriptUrl);

                if (!importScriptURL.getProtocol ().equalsIgnoreCase (DIConstants.HTTP_PROTOCOL) ||
                    !dirScriptURL.getProtocol ().equalsIgnoreCase (DIConstants.HTTP_PROTOCOL))
                                        // are the necessary parameters for the
                                        // import connector set?
                {
                    throw new ConnectionFailedException (
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_PROTOCOL_NOT_SUPPORTED, env));
                } // if are the necessary parameters for the import connector set?
            } // if should the connector be used for import?

            if (this.isExportConnector) // should the connector be used for export?
            {
                // construct a url from the string
                URL exportScriptURL  = new URL (this.exportScriptUrl);

                if (!exportScriptURL.getProtocol ().equalsIgnoreCase (DIConstants.HTTP_PROTOCOL))
                                        // are the necessary parameters for the
                                        // export connector set?
                {
                    throw new ConnectionFailedException (
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_PROTOCOL_NOT_SUPPORTED, env));
                } // if are the necessary parameters for the export connector set?
            } // if should the connector be used for export?
        } // try
        catch (IOException e)
        {
            throw new ConnectionFailedException (
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_HTTP_CONNECTION_FAILED, env));
        } // catch
    } // initConnector


    /**************************************************************************
     * Initializes the connector for use as backup connector. Will be
     * overwritten in the subclasses to meet the specific need of the various
     * connectors.<BR/>
     * This method must always call {@link #initConnector() initConnector}.
     *
     * @param   processId   Id of import/export process for which the connector
     *                      is used for backup.
     *
     * @throws  ConnectionFailedException
     *          The connection could not be established
     */
    public void initBackupConnector (String processId)
        throws ConnectionFailedException
    {
        // call the standard initialization:
        super.initBackupConnector (processId);

        // check if the processId is defined:
        if (processId != null && processId.length () > 0)
        {
            // add processId to file prefix:
            if (this.p_filePrefix != null)
            {
                this.p_filePrefix += processId + "_";
            } // if
            else
            {
                this.p_filePrefix = processId + "_";
            } // else
        } // if
    } // initBackupConnector


    /**************************************************************************
     * Closes the connector. This method includes all actions that need to
     * be done to close a connection to a data source or destination
     * and deletes the temp directory if applicable.<BR/>
     */
    public void close ()
    {
        // check if the temp directory should be deleted
        if (this.isDeleteTemp)
        {
            this.deleteTempDir ();
        } // if
    } // close


    /**************************************************************************
     * The dir method reads from the import source and returns all importable
     * objects found in a array of strings. <BR/>
     * This means for an HTTPScriptConnector_01 that a connection is opened
     * and the files are extracted.<BR/>
     *
     * @return  an array of strings containing the importable files found or
     *          <CODE>null</CODE> otherwise.
     *
     * @exception   ConnectionFailedException
     *              The connection could not be established.
     */
    public String[] dir ()
        throws ConnectionFailedException
    {
        String[] filesToReturn = null;
        String content;

        try
        {
            // check if a dir script has been specified
            if (this.dirScriptUrl == null)
            {
                // return a dummy import file name that can always be selected
                filesToReturn = new String [1];
                filesToReturn [0] = 
                    MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                        DITokens.ML_DUMMYIMPORTFILENAME, env);
            } // if (this.dirScriptUrl == null)
            else  // dir script available
            {
                // read the content of the url
                content = this.readUrlContent (this.dirScriptUrl);
                // check if an error occurred
                if (content.startsWith (DIConstants.SCRIPT_ERROR))
                {
                    throw new ConnectionFailedException (
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_SCRIPT_FAILURE, env));
                } // if (htmlPage.startsWith (DIConstants.SCRIPT_ERROR))
                // if header is on then extract the content
                if (this.isUseHTMLHeader)
                {
                    content = this.getContent (content);
                } // if
                // specify the  delimiter
                if (this.dirFileDelimiter == null || this.dirFileDelimiter.length () == 0)
                {
                    filesToReturn = DIHelpers.getTokens (content, DIConstants.FILE_DELIMITER);
                } // if
                else
                {
                    filesToReturn = DIHelpers.getTokens (content, this.dirFileDelimiter);
                } // else
            } // else dir script available
            return filesToReturn;
        } // try
        catch (ConnectionFailedException e)
        {
            throw e;
        } // catch
    } // dir


    /**************************************************************************
     * Writes the export file to the http server.<BR/>
     *
     * @param   fileName    The name of the source file.
     *
     * @exception   ConnectionFailedException
     *              The connection could not be established.
     */
    public void write (String fileName)
        throws ConnectionFailedException
    {
        int c;
        URL url;
        HttpURLConnection connection;
        String answerMsg;
        String urlStr;
        StringBuffer stringBuffer;
        DataOutputStream outputStream;
        DataInputStream inputStream;
        BufferedInputStream bufferedInputStream;

        try
        {
            // construct the url
            urlStr = this.exportScriptUrl;
            // if the url has no ending '=' character then add it
            if (!urlStr.endsWith (DIConstants.SCRIPT_EQUALS))
            {
                urlStr += DIConstants.SCRIPT_EQUALS;
            } // if
            // add the encoded filename to the url
            urlStr += IOHelpers.urlEncode (fileName);
            // create the url to write to
            url = new URL (urlStr);
            // open the connection
            connection = (HttpURLConnection) url.openConnection ();
            // set the connection for input/output
            connection.setDoInput (true);
            connection.setDoOutput (true);
            // check if a mime type has been set
            if (this.exportMIMETYPE.length () > 0)
            {
                connection.setRequestProperty (DIConstants.CONTENT_TYPE,
                                               this.exportMIMETYPE);
            } // if
            else
            {
                connection.setRequestProperty (DIConstants.CONTENT_TYPE,
                                               DIConstants.MIMETYPE_TEXTPLAIN);
            } // else

            // get the output stream to the connection
            outputStream = new DataOutputStream (connection.getOutputStream ());
            // set the input stream to the export file we want to write
            inputStream = new DataInputStream (new BufferedInputStream (
                new FileInputStream (this.getPath () + fileName)));
            // read the export file and write to the stream
            while ((c = inputStream.read ()) != -1)
            {
                outputStream.writeByte (c);
            } // while

            // close the streams:
            outputStream.close ();
            inputStream.close ();

            // read the answer from the CGI script
            bufferedInputStream = new BufferedInputStream (connection.getInputStream ());
            stringBuffer = new StringBuffer ();
            // read the answer into a string buffer
            while ((c = bufferedInputStream.read ()) != -1)
            {
                stringBuffer.append ((char) c);
            } // while

            // close the stream:
            inputStream.close ();

            // get the answer string
            answerMsg = stringBuffer.toString ();

            // check the answer.
            if (answerMsg.indexOf (DIConstants.SCRIPT_SUCCESS) < 0)
            {
                // If it contains ERROR in the first line something went wrong
                throw new ConnectionFailedException (
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_SCRIPT_FAILURE, env));
            } // if (answerMsg.startsWith (DIConstants.SCRIPT_ERROR))
        } // try
        catch (ConnectionFailedException e)
        {
            throw e;
        } // catch
        catch (Exception e)
        {
            throw new ConnectionFailedException (
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_COULDNOTWRITETOCONNECTOR, env));
        } // catch
    } // write


    /**************************************************************************
     * Retrieves the file from the http server and writes it into the
     * directory from where the integrator reads the files.<BR/>
     * The method uses the readFile method to accomplish the task.<BR/>
     *
     * @param   fileName    The name of the file to read.
     *
     * @throws  ConnectionFailedException
     *          The connection could not be established.
     */
    public void read (String fileName)
        throws ConnectionFailedException
    {
        try
        {
            // read the file via the readFile method and
            // check if the method returned a valid file size
            if (this.readFile (fileName, this.getPath (), fileName) != -1)
            {
                this.setFileName (fileName);
            } // if
            else
            {
                this.setFileName ("");
            } // else
        } // try
        catch (ConnectionFailedException e)
        {
            throw e;
        } // catch
    } // read


    /**************************************************************************
     * Read a file from the HTTP server and copy it to the destination path.<BR/>
     *
     * @param fileName              name of the file to read
     * @param destinationPath       the path to write the file to
     * @param destinationFileName   name of the copied file.
     *                              If empty fileName will be used.
     *
     * @return the size of the file in case it could have been read successfully or
     *         -1 if an error occurred or the file has not been found
     *
     * @exception   ConnectionFailedException
     *              could not access the file
     *
     * @see ibs.di.connect.Connector_01#readFile
     */
    public long readFile (String fileName, String destinationPath,
                          String destinationFileName)
        throws ConnectionFailedException
    {
        String urlStr;
        String response;
        BufferedInputStream inputStream;
        BufferedOutputStream outputStream;
        URL url;
        int data;
        byte [] dataBuffer = new byte [DIConstants.SCRIPT_ERROR.length ()];

        try
        {
            // construct the string
            urlStr = this.importScriptUrl;
            // if the url has no ending '=' add '=' at the end
            if (!urlStr.endsWith (DIConstants.SCRIPT_EQUALS))
            {
                urlStr += DIConstants.SCRIPT_EQUALS;
            } // if

            // add the filename urlencoded
            urlStr += IOHelpers.urlEncode (fileName);
            // create a url instance
            url = new URL (urlStr);
            // open the stream and start reading from it
            inputStream = new BufferedInputStream (url.openStream ());

            // read the first characters from the stream that could contain the error
            // message. We assume that in case this reaches the end of the stream
            // it is a non valid file because it is too small
            if (inputStream.read (dataBuffer) == -1)
            {
                throw new ConnectionFailedException (
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_SCRIPT_FAILURE, env));
            } // if

            // create a string out of the data buffer
            response = new String (dataBuffer);

            // check if the CGI script returned an error
            if (response.equalsIgnoreCase (DIConstants.SCRIPT_ERROR))
            {
                // close the stream
                inputStream.close ();
                // throw an exception
                throw new ConnectionFailedException (
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_SCRIPT_FAILURE, env));
            } // if (dataBuffer.toString ().equalsIgnoreCase (DIConstants.SCRIPT_ERROR))

            // no error returned
            // create a fileWriter used to write the answer we get from
            outputStream = new BufferedOutputStream (new FileOutputStream (
                destinationPath + destinationFileName));
            // write the buffer into the file
            for (int i = 0; i < dataBuffer.length; i++)
            {
                outputStream.write (dataBuffer [i]);
            } // for i

            int i = 0;
            // read the content and write it into the file
            while ((data = inputStream.read ()) != -1)
            {
                // write first line to the output file
                outputStream.write (data);
                i++;
            } // while ((htmlPage = bufReader.readLine ()) != null)

            // return the content of from the script
            // close the streams
            outputStream.close ();
            inputStream.close ();
            // return the filesize to indicate successful reading
            return FileHelpers.getFileSize (destinationPath, destinationFileName);
        } // try
        catch (ConnectionFailedException e)
        {
            throw e;
        } // catch
        catch (IOException e)
        {
            throw new ConnectionFailedException (
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_CONNECTORFAILED, env));
        } // catch
    }   // readFile


    /**************************************************************************
     * Write a file to a CGI script. <BR/>
     * This method uses the write method of the connector in order to write
     * the file.
     *
     * @param sourcePath        path to read the file from
     * @param fileName          name of the file to read
     *
     * @return the name of the file written or null in case it could not have
     *         been written
     *
     * @exception   ConnectionFailedException
     *              could not access the file
     *
     * @see ibs.di.connect.Connector_01#writeFile
     */
    public String writeFile (String sourcePath, String fileName)
        throws ConnectionFailedException
    {
        String origPath;
        String origFileName;

        // the HTTPConnector does not support export
        try
        {
            origPath = this.getPath ();
            origFileName = this.getFileName ();
            // set the source path
            this.setPath (sourcePath);
            // write the file
            this.write (fileName);
            // restore the path and the filename
            this.setPath (origPath);
            this.setFileName (origFileName);
            // return the filename to indicate successful writing
            // note that we use this feature in case the filename has
            // been changed. We cannot predict a change because the
            // cgi script does not return the filename.
            return fileName;
        } // try
        catch (ConnectionFailedException e)
        {
            throw e;
        } // catch
    } // writeFile


    /**************************************************************************
     * Delete a file via the HTTPScriptConnector.<BR/>
     * Note that this feature is not supported by a HTTPScript connector.<BR/>
     *
     * @param fileName      the name of the file to delete
     *
     * @return true if the file could he deleted or false otherwise
     *
     * @exception   ConnectionFailedException
     *              could not access the file
     *
     * @see ibs.di.connect.Connector_01#deleteFile
     */
    public boolean deleteFile (String fileName)
        throws ConnectionFailedException
    {
        // the HTTPScript Connector does not support this method
        return false;
    } // deleteFile


    /***************************************************************************
     * Represent the properties of a DocumentTemplate_01 object to the user. <BR/>
     *
     * @param   table       Table where the properties should be added.
     *
     * @see ibs.bo.BusinessObject#showProperties
     */
    protected void showProperties (TableElement table)
    {
        super.showProperties (table);
        this.showProperty (table, DIArguments.ARG_ARG1,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_READURL, env),
            Datatypes.DT_TEXT, this.arg1);
        this.showProperty (table, DIArguments.ARG_ARG2,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_DIRURL, env),
            Datatypes.DT_TEXT, this.arg2);
        this.showProperty (table, DIArguments.ARG_ARG3,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_DIRFILEDELIMITER, env),
            Datatypes.DT_TEXT, this.arg3);
        this.showProperty (table, DIArguments.ARG_ARG4,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_WRITEURL, env),
            Datatypes.DT_TEXT, this.arg4);
//        this.showProperty (table, DIArguments.ARG_ARG5, 
//            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
//                DITokens.TOK_EXPORTFILENAME, env),
//            Datatypes.DT_TEXT, this.arg5);
        this.showProperty (table, DIArguments.ARG_ARG6,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EXPORTMIMETYPE, env),
            Datatypes.DT_TEXT, this.arg6);
    } // showProperties


     /***************************************************************************
     * Represent the properties of a Connector_01 object to the user
     * within a form. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     *
     * @see ibs.bo.BusinessObject#showFormProperties
     */
    protected void showFormProperties (TableElement table)
    {
        super.showFormProperties (table);
        this.showFormProperty (table, DIArguments.ARG_ARG1,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_READURL, env),
            Datatypes.DT_URL, this.arg1);
        this.showFormProperty (table, DIArguments.ARG_ARG2,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_DIRURL, env),
            Datatypes.DT_URL, this.arg2);
        this.showFormProperty (table, DIArguments.ARG_ARG3, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_DIRFILEDELIMITER, env),
            Datatypes.DT_TEXT, this.arg3);
        this.showFormProperty (table, DIArguments.ARG_ARG4,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_WRITEURL, env),
            Datatypes.DT_URL, this.arg4);
//        this.showFormProperty (table, DIArguments.ARG_ARG5,
//            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
//                DITokens.TOK_EXPORTFILENAME, env), 
//            Datatypes.DT_TEXT, this.arg5);
        this.showFormProperty (table, DIArguments.ARG_ARG6, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EXPORTMIMETYPE, env),
            Datatypes.DT_TEXT, this.arg6);
    } // showFormProperties


    /**************************************************************************
     * Displays the settings of the connector.<BR/>
     *
     * @param   table       Table where the settings shall be added.
     */
    public void showSettings (TableElement table)
    {
        super.showSettings (table);
        this.showProperty (table, DIArguments.ARG_ARG1, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_READURL, env),
            Datatypes.DT_TEXT, this.arg1);
        this.showProperty (table, DIArguments.ARG_ARG2,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_DIRURL, env),
            Datatypes.DT_TEXT, this.arg2);
        this.showProperty (table, DIArguments.ARG_ARG3,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_DIRFILEDELIMITER, env),
            Datatypes.DT_TEXT, this.arg3);
        this.showProperty (table, DIArguments.ARG_ARG4, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_WRITEURL, env),
            Datatypes.DT_TEXT, this.arg4);
//        this.showProperty (table, DIArguments.ARG_ARG5, 
//            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
//                 DITokens.TOK_EXPORTFILENAME, env)
//            Datatypes.DT_TEXT, this.arg5);
        this.showProperty (table, DIArguments.ARG_ARG6, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EXPORTMIMETYPE, env),
            Datatypes.DT_TEXT, this.arg6);
    } // showSettings


    /**************************************************************************
     * Adds the settings of the connector to a log.<BR/>
     *
     * @param   log     the log to add the setting to
     */
    public void addSettingsToLog (Log_01 log)
    {
        super.addSettingsToLog (log);
        // add the connector specific setting to the log
        log.add (DIConstants.LOG_ENTRY, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_READURL, env) + ": " +
            this.importScriptUrl , false);
        log.add (DIConstants.LOG_ENTRY, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_DIRURL, env) + ": " +
            this.dirScriptUrl , false);
        log.add (DIConstants.LOG_ENTRY, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_DIRFILEDELIMITER, env) + ": " +
            this.dirFileDelimiter , false);
        log.add (DIConstants.LOG_ENTRY, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_WRITEURL, env) + ": " +
            this.exportScriptUrl , false);
        log.add (DIConstants.LOG_ENTRY, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EXPORTMIMETYPE, env) + ": " +
            this.exportMIMETYPE , false);
//        log.add (DIConstants.LOG_ENTRY, DITokens.TOK_EXPORTMIMETYPE + ": " +
//                 this.exportFileName , false);
    } // addSettingsToLog

} // HTTPScriptConnector_01
