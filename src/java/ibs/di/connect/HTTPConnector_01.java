/*
 * Class: HTTPConnector_01.java
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
import ibs.di.DIMessages;
import ibs.di.DITokens;
import ibs.di.Log_01;
import ibs.di.connect.ConnectionFailedException;
import ibs.di.connect.Connector_01;
//TODO: unsauber
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.GroupElement;
import ibs.tech.html.SelectElement;
import ibs.tech.html.TableElement;
import ibs.util.StringHelpers;
import ibs.util.file.FileHelpers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Vector;

import org.apache.commons.net.io.Util;

//import com.oroinc.io.Util;


/******************************************************************************
 * The HTTPConnector_01 Class reads and writes files from an to an http server.<BR/>
 *
 * @version     $Id: HTTPConnector_01.java,v 1.23 2010/04/07 13:37:05 rburgermann Exp $
 *
 * @author      Danny xavier (DX), 000628
 ******************************************************************************
 */
public class HTTPConnector_01 extends Connector_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: HTTPConnector_01.java,v 1.23 2010/04/07 13:37:05 rburgermann Exp $";


    /**
     *  name of the HTTP server
     */
    private String httpServerUrl = null;

    /**
     *  name of the file to be read from the url
     */
    private String importFileName = null;

    /**
     *  type of the webserver to read the file names from
     */
    private String httpServerType = null;

    /**
     *  array containing the possible serverType values.<BR/>
     *  will be used to construct a selection box.<BR/>
     */
    private String[] serverTypes =
    {
        DIConstants.SERVERTYPENAME_APACHE,
        DIConstants.SERVERTYPENAME_IIS,
    };

    /***************************************************************************
     * Creates a HTTPConnector_01 Object. <BR/>
     */
    public HTTPConnector_01 ()
    {
        // call constructor of super class Connector_01:
        super ();
    } // HTTPConnector_01


    /**************************************************************************
     * Creates a HTTPConnector_01 Object. <BR/>
     *
     * @param   oid     oid of the object.
     * @param   user    user that created the object.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public HTTPConnector_01 (OID oid, User user)
    {
        // call constructor of super class Connector_01:
        super (oid, user);
    } // HTTPConnector_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set class specifics of super class:
        super.initClassSpecifics ();

        // set class specifics
        this.connectorType = DIConstants.CONNECTORTYPE_HTTP;
        // an HTTP connector can only be used for import
        this.isExportEnabled = false;
        this.isExportConnector = false;
    } // initClassSpecifics


    /**************************************************************************
     * Sets the arguments to class specific properties. <BR/>
     */
    public void setArguments ()
    {
        this.httpServerUrl = this.arg1;
        this.importFileName = this.arg2.trim ();
        this.httpServerType = this.arg3;
    } // setArguments


    /*************************************************************************
     * This method returns the file names from the directory listing. <BR/>
     *
     * @return  an array of file names or null if reading failed
     */
    private String[] getFileNames ()
    {
        // check server type:
        if (this.httpServerType.equals (DIConstants.SERVERTYPENAME_APACHE))
        {
            // get the files form the directory listing for Apache
            return this.getApacheDir ();
        } // if server is Apache

        // else the server is IIS
        if (this.httpServerType.equals (DIConstants.SERVERTYPENAME_IIS))
        {
            // get the files from the directory listing for IIS
            return this.getIISDir ();
        } // server is IIS

        // the server is not apache or IIS
        return null;
    } // getFileNames


    /*************************************************************************
     * This method returns the file names from a Internet Information Server
     * HTTP directory listing. <BR/>
     *
     * @return  an array of strings having the filenames under a directory
     */
    private String[] getIISDir ()
    {
        String inputLine = "";
        String fileName = "";
        String checkLine = "";
        String[] files = null;      // array of file to be returned
        URL httpServerUrl;
        Vector<String> fileNamesVector = new Vector<String> ();
        BufferedReader bufferedReader;
        String htmlContent = "";
        int index;
        int beginIndex;
        int lastIndex;

        try
        {
            // create a url Object from the string
            httpServerUrl = new URL (this.httpServerUrl);
            // start reading from the URL
            InputStreamReader inputStreamReader =
                new InputStreamReader (httpServerUrl.openStream ());
            // buffered reader to read from the input stream
            bufferedReader = new BufferedReader (inputStreamReader);
            // read the complete html file
            while ((inputLine = bufferedReader.readLine ()) != null)
            {
                htmlContent += inputLine;
            } // while ((inputLine = bufferedReader.readLine ()) != null)
            // create a buffered reader on the string
            bufferedReader = new BufferedReader (
                new CharArrayReader (htmlContent.toCharArray ()));
            // first find the <A HREF="/"> line. this is the [to Parental Directory] entry
            // that marks the beginning of the filelist.
            if ((index = htmlContent.indexOf ("<A HREF=\"/\">")) != -1)
            {
                // increase the index in order to avoid finding the same entry again
                index += 12;
                lastIndex = index;
                // now ce can look for other <A HREF="...">...</A> occurences
                // they contain the file names
                while ((index = htmlContent.indexOf ("<A HREF=\"", lastIndex)) != -1)
                {
                    // check first if this is a directory by testing if a
                    // <dir> string can be found between the <A HREF="..."> and
                    // the string before
                    checkLine = htmlContent.substring (lastIndex, index);

                    if (checkLine.indexOf ("&lt;dir&gt;") == -1)
                    {
                        // we extract the text between the <A HREF="..."> and the </A>
                        // this is the filename we are looking for
                        beginIndex = htmlContent.indexOf ("\">", index) + 2;
                        // set the index to the next occurrence of the "</A>"
                        index = htmlContent.indexOf ("</A>", beginIndex);
                        fileName = htmlContent.substring (beginIndex, index);
                        // add the fileName to the result vector
                        fileNamesVector.addElement (fileName);
                    } // if (checkLine.indexOf ("<dir>") == -1)
                    else    // directory entry found
                    {
                        // shift the index to the next occurrence of the "</A>"
                        index = htmlContent.indexOf ("</A>", index);
                    } // directory entry found
                    lastIndex = index;
                } // while ((index = htmlContent.indexOf ("<A HREF=\"")) != -1)
                // copy the filenames from the vector into a string array
                files = new String [fileNamesVector.size ()];
                for (int i = 0; i < fileNamesVector.size (); i++)
                {
                    // insert into the array
                    files [i] = fileNamesVector.elementAt (i);
                } // while (st.hasMoreTokens() ends here
            } // if ((index = htmlContent.indexOf ("<A HREF=\"/\">")) != -1)
            else // no files found or invalid page
            {
                // set the files names string array to  null to indicate that no
                // files have been found
                files = null;
            } // else no files found or invalid page
        } // try
        catch (FileNotFoundException e)
        {
            files = null;
        } // catch ends here
        catch (IOException e)
        {
            files = null;
        } // catch  ends here
        // return the resulting files names string array
        return files;
    } // getIISDir


    /*************************************************************************
     * This method returns the file names from an Apache
     * HTTP directory listing. <BR/>
     *
     * @return  an array of strings having the filenames under a directory
     */
    private String[] getApacheDir ()
    {
        String inputLine = new String ();
        String[] files = null;              // array of files to be returned
        BufferedReader bufferedReader;
        URL httpServerUrl;
        InputStreamReader inputStream;
        Vector<String> fileNamesVector = new Vector<String> ();
        String fileName;
        int index;
        int indexBegin;
        int indexEnd;

        try
        {
            httpServerUrl = new URL (this.httpServerUrl);
            inputStream = new InputStreamReader (httpServerUrl.openStream ());
            bufferedReader = new BufferedReader (inputStream);
            // read the output lines and extract the filenames
            while ((inputLine = bufferedReader.readLine ()) != null)
            {
                // looks for the presence of a ALT="[TXT]" string
                // this indicates a line where a file name is displayed
                index = inputLine.indexOf ("ALT=\"[");
                // if the ALT string is found fetch the file name
                if (index != -1)
                {
                    // check if the entry is not a directory
                    if (inputLine.indexOf ("[DIR]", index) == -1)
                    {
                        // find the presence of <A HREF="
                        indexBegin = inputLine.indexOf ("<A HREF=\"");
                        // and push until ">
                        indexBegin = inputLine.indexOf ("\">", indexBegin) + 2;
                        // and set end to </A>
                        indexEnd = inputLine.indexOf ("</A>", indexBegin);
                        // the string between the index positions is the fileName
                        fileName = inputLine.substring (indexBegin, indexEnd);
                        // add the files into the vector
                        fileNamesVector.addElement (fileName);
                    } // if (inputLine.indexOf ("[DIR]", index) == -1)
                } // if (index!=-1)
            } // while ((inputLine = bufferedReader.readLine ()) != null)
            // close the InputStreamReader
            inputStream.close ();
            // close the bufferedReader
            bufferedReader.close ();
            // construct the file names array to be returned by the method
            files = new String [fileNamesVector.size ()];
            fileNamesVector.copyInto (files);
        } // try
        catch (FileNotFoundException f)
        {
            files = null;
        } // catch
        catch (IOException e)
        {
            files = null;
        } // catch  ends here
        return files;
    } // getApacheDir


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
        if (this.isCreateTemp && this.getPath ().length () == 0)
        {
            this.setPath (this.createTempDir ());
        } // if

        try
        {
            URL  url = new URL (this.httpServerUrl);

            // check the protocol:
            if (!url.getProtocol ().equalsIgnoreCase (DIConstants.HTTP_PROTOCOL))
            {
                throw new ConnectionFailedException (
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_PROTOCOL_NOT_SUPPORTED, env));
            } // if (! url.getProtocol().equalsIgnoreCase (DIConstants.HTTP_PROTOCOL))
            // protocol correct
            this.httpServerUrl =
                FileHelpers.addEndingURLSeparator (this.httpServerUrl);
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
        // this connector cannot be used for export
        // so it is not possible to use it as backup connector
        throw new ConnectionFailedException (
            MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                DIMessages.ML_MSG_BACKUP_CONNECTOR_NOT_ALLOWED, env));
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
     * This means for an HTTPConnector_01 that a connection is opened
     * and the files are read from this connection.<BR/>
     *
     * @return  an array of strings containing the importable files found or
     *          <CODE>null</CODE> otherwise.
     *
     * @exception   ConnectionFailedException
     *              The connection could not be established.
     */
    public String [] dir ()
        throws ConnectionFailedException
    {
        String [] files = null;

        try
        {
            // check first if a fixed filename has been set
            if (this.importFileName != null && this.importFileName.length () > 0)
            {
                // try to open a connection to the file specified
                URL url = new URL (this.httpServerUrl + this.importFileName);
                InputStreamReader inputStreamReader = new InputStreamReader (url.openStream ());
                inputStreamReader.close ();
                // set the filename
                files = new String [] {this.importFileName};
            } // if (this.importFileName != null && this.importFileName.length () > 0)
            else    // read the directory
            {
                files = this.getFileNames ();
            } // else read the directory
            return files;
        } // try
        catch (IOException e)
        {
            throw new ConnectionFailedException (
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_HTTP_CONNECTION_FAILED, env));
        } // catch (IOException e)
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
             // nothing to be done because HTTPConnectors do not support export
    } // write


    /**************************************************************************
     * Retrieves the file from the HTTP server and writes it into the
     * temporary directory of the connector.<BR/>
     *
     * @param   fileName    The name of the file to read.
     *
     * @throws  ConnectionFailedException
     *          The connection could not be established.
     */
    public void read (String fileName)
        throws ConnectionFailedException
    {
        URL url;
        InputStreamReader inputStreamReader;
        BufferedReader reader;
        FileWriter fileWriter;

        // start reading a file from a Http-server and copy it into the
        // destination directory
        try
        {

            // convert spaces to %20 first otherwise we cannot read the file
            // from the webserver
            String fileN = StringHelpers.replace (fileName, " ", "%20");
            // create the url
            url = new URL (this.httpServerUrl + fileN);
            // set the streams
            inputStreamReader = new InputStreamReader (url.openStream ());
            reader = new BufferedReader (inputStreamReader);
            fileWriter = new FileWriter (this.getPath () + fileN);
            // write the content of the url into a file
            Util.copyReader (reader, fileWriter);
            // close the fileWriter
            fileWriter.close ();
            // close the buffered reader
            reader.close ();
            // close the inputStreamReader
            inputStreamReader.close ();
            // set  the file name after successfully reading it
            this.setFileName (fileN);
        } // try
        catch (IOException e)
        {
            throw new ConnectionFailedException (
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_CONNECTORFAILED, env));
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
        URL url;
        BufferedInputStream inputStream;
        BufferedOutputStream outputStream;

        // start reading a file from a Http-server and copy it into the
        // destination directory
        try
        {
            // convert spaces to %20 first otherwise we cannot read the file
            // from the webserver
            String fileN = StringHelpers.replace (fileName, " ", "%20");
            // create the url
            url = new URL (this.httpServerUrl + fileN);
            // open the stream to the HTTPConnection
            inputStream = new BufferedInputStream (url.openStream ());
            // create a stream to the destination file
            outputStream = new BufferedOutputStream (
                new FileOutputStream (destinationPath + destinationFileName));
            // write the content of the url into a file
            int data = 0;
            // read the content and write it into the file
            while ((data = inputStream.read ()) != -1)
            {
                // write first line to the output file
                outputStream.write (data);
            } // while ((data = inputStream.read ()) != -1)
            // close the output stream
            outputStream.close ();
            // close the input stream
            inputStream.close ();
            // return the filesize to indicate that copying was successful
            return FileHelpers.getFileSize (destinationPath, destinationFileName);
        } // try
        catch (IOException e)
        {
            throw new ConnectionFailedException (
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_CONNECTORFAILED, env));
        } // catch
    }   // readFile


    /**************************************************************************
     * Write a file to a HTTP server.<BR/>
     * Note that this feature is not supported by a HTTP connector.<BR/>
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
        // the HTTPConnector does not support export
        return "";
    } // writeFile


    /**************************************************************************
     * Delete a file from the HTTP server.<BR/>
     * Note that this feature is not supported by a HTTP connector.<BR/>
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
        // the HTTP Connector does not support this method
        return false;

    } // deleteFile


    /**************************************************************************
     * Represent the properties of a DocumentTemplate_01 object to the user. <BR/>
     *
     * @param   table       Table where the properties should be added.
     *
     * @see ibs.bo.BusinessObject#showProperties
     */
    protected void showProperties (TableElement table)
    {
        super.showProperties (table);
        // loop through all properties of this object and display them:
        this.showProperty (table, DIArguments.ARG_ARG1,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_HTTPSERVERURL, env),
            Datatypes.DT_TEXT, this.arg1);
        this.showProperty (table, DIArguments.ARG_ARG3,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_HTTPSERVERTYPE, env),
            Datatypes.DT_TEXT, this.arg3);
        this.showProperty (table, DIArguments.ARG_ARG2,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_IMPORTFILE, env),
            Datatypes.DT_TEXT, this.arg2);
    } // showProperties


    /**************************************************************************
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
        // loop through all properties of this object and display them:
        this.showFormProperty (table, DIArguments.ARG_ARG1,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_HTTPSERVERURL, env),
            Datatypes.DT_NAME, this.arg1);
        // construct a selection box for the HTTPServerTypen values
        GroupElement group = new GroupElement ();
        SelectElement select = new SelectElement (DIArguments.ARG_ARG3, false);
        group.addElement (select);
        select.size = 1;
        for (int i = 0; i < this.serverTypes.length; i++)
        {
            if (this.serverTypes [i].equals (this.arg3))
            {
                select.addOption (this.serverTypes [i], this.serverTypes [i], true);
            } // if
            else
            {
                select.addOption (this.serverTypes [i], this.serverTypes [i]);
            } // else
        } // for (int i = 0; i < this.serverTypes.length ; i++)
        this.showFormProperty (table,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_HTTPSERVERTYPE, env), group);

        this.showFormProperty (table, DIArguments.ARG_ARG2,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_IMPORTFILE, env),
            Datatypes.DT_NAME, this.arg2);
    } // showFormProperties


    /**************************************************************************
     * Displays the settings of the connector.<BR/>
     *
     * @param   table       Table where the settings shall be added.
     */
    public void showSettings (TableElement table)
    {
        super.showSettings (table);
        // display connector specific settings
        this.showProperty (table, DIArguments.ARG_ARG1,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_HTTPSERVERURL, env),
            Datatypes.DT_TEXT, this.httpServerUrl);
        this.showProperty (table, DIArguments.ARG_ARG3,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_HTTPSERVERTYPE, env),
            Datatypes.DT_TEXT, this.httpServerType);
        this.showProperty (table, DIArguments.ARG_ARG2,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_IMPORTFILE, env),
            Datatypes.DT_TEXT, this.importFileName);
    } // showSettings


    /**************************************************************************
     * Adds the settings of the connector to a log.<BR/>
     *
     * @param   log     the log to add the setting to
     */
    public void addSettingsToLog (Log_01 log)
    {
        super.addSettingsToLog (log);
        // add connector specific settings
        log.add (DIConstants.LOG_ENTRY,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_HTTPSERVERURL, env) + ": " +
            this.httpServerUrl, false);
        log.add (DIConstants.LOG_ENTRY,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_HTTPSERVERTYPE, env) + ": " +
            this.httpServerType, false);
        log.add (DIConstants.LOG_ENTRY,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_IMPORTFILE, env) + ": " +
            this.importFileName, false);
    } // addSettingsToLog

} // HTTPConnector_01
