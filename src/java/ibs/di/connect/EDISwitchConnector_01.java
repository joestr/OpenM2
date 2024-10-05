/*
 * Class: EDISwitchConnector_01.java
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
import ibs.di.connect.ConnectorInterface;
import ibs.di.connect.Connector_01;
//TODO: unsauber
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.TableElement;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;
import ibs.util.file.FileHelpers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;


/******************************************************************************
 * The EDISwitchConnector_01 Class reads and writes files from an to an FTP server.<BR/>
 *
 * @version     $Id: EDISwitchConnector_01.java,v 1.18 2010/04/07 13:37:05 rburgermann Exp $
 *
 * @author      Danny xavier (DX), 20000628
 ******************************************************************************
 */
public class EDISwitchConnector_01 extends Connector_01 implements
                ConnectorInterface
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: EDISwitchConnector_01.java,v 1.18 2010/04/07 13:37:05 rburgermann Exp $";


    /**
     *  name of EDISwitch FTP server. <BR/>
     */
    public String ftpServer = "";

    /**
     *  port of EDISwitch FTP server. <BR/>
     */
    public String ftpServerPort = "";

    /**
     *  user to login at EDISwitch FTP server. <BR/>
     */
    public String ftpUser = "";

    /**
     *  password for login at EDISwitch FTP server. <BR/>
     */
    public String ftpPassword = "";

    /**
     *  reference number of the recipient. <BR/>
     */
    public String ftpRecipientId = "";

    /**
     *  reference number of the sender. <BR/>
     */
    public String ftpSenderRefNum = "";

    /**
     *  application reference that is associated with the EDI data. <BR/>
     */
    public String ftpApplicationRef = "";

    /**
     *  the FTP client object to connect to the ftp server
     */
    private FTPClient ftpClient = null;


    /**************************************************************************
     * Creates a EDISwitchConnector_01 Object. <BR/>
     */
    public EDISwitchConnector_01 ()
    {
        // call constructor of super class Connector_01:
        super ();
    } // EDISwitchConnector_01


    /**************************************************************************
     * Creates a EDISwitchConnector_01 Object. <BR/>
     *
     * @param   oid     oid of the object.
     * @param   user    user that created the object.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public EDISwitchConnector_01 (OID oid, User user)
    {
        // call constructor of super class Connector_01:
        super (oid, user);
    } // EDISwitchConnector_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set class specifics of super class:
        super.initClassSpecifics ();

        // set class specifics:
        this.connectorType = DIConstants.CONNECTORTYPE_EDISWITCH;
    } // initClassSpecifics


    /**************************************************************************
     * Sets the arguments to class specific properties. <BR/>
     */
    public void setArguments ()
    {
        // set the connector specific properties
        // using the connector neutral arguments
        this.ftpServer = this.arg1;
        this.ftpServerPort = this.arg2;
        this.ftpUser = this.arg3;
        this.ftpPassword = this.arg4;
        this.ftpSenderRefNum = this.arg5;
        this.ftpRecipientId = this.arg6;
        this.ftpApplicationRef = this.arg7;
    } // setArguments


    /**************************************************************************
     * Initializes the connector and checks the settings. <BR/>
     * InitConnector tries to open a connection to the specified FTP server
     * and log in with the use data defined. In case the connection could not
     * be established or the login failed an exception will be generated.<BR/>
     *
     * @throws  ConnectionFailedException
     *          The connection could not be established.
     */
    public void initConnector ()
        throws ConnectionFailedException
    {
        int reply;                      // the reply code of the ftp client
        int port;                       // the port number

        // create an FTP client instance
        this.ftpClient = new FTPClient ();
        // check first if we have a directory set where we can copy the files
        // if not create a temp directory
        if (this.isCreateTemp &&
            (this.getPath () == null || this.getPath ().length () == 0))
        {
            this.setPath (this.createTempDir ());
        } // if

        try
        {
            // first convert the ftpServerPort string to an integer
            port = Integer.parseInt (this.ftpServerPort);
            // try to open a connection to the ftp server
            this.ftpClient.connect (InetAddress.getByName (this.ftpServer), port);
            // after connection attempt, check the reply code to verify success.
            reply = this.ftpClient.getReplyCode ();

            if (!FTPReply.isPositiveCompletion (reply))
            {
                this.ftpClient.disconnect ();
                throw new ConnectionFailedException (
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_EDISWITCH_SERVER_NOT_FOUND, 
                        new String[] {this.ftpServer}, env) + ":" + this.ftpServerPort);
            } // if (! FTPReply.isPositiveCompletion (reply))
            // try to login to ftp server
            if (!this.ftpClient.login (this.ftpUser, this.ftpPassword))
            {
                // could not login
                this.ftpClient.disconnect ();
                throw new ConnectionFailedException (
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_COULD_NOT_LOGIN,
                        new String[] {this.ftpUser}, env));
            } // if (! ftpClient.login (this.ftpUser, this.ftpPassword))
            // login successful
            // set the binary file type flag
            this.ftpClient.setFileType (FTP.BINARY_FILE_TYPE);
        } // try
        catch (IOException e)
        {
            if (this.ftpClient.isConnected ())
            {
                try
                {
                    this.ftpClient.disconnect ();
                } // try
                catch (IOException f)
                {
                    // client is disconnected
                } // catch
            } // if (ftp.isConnected())
            throw new ConnectionFailedException (
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_EDISWITCH_CONNECTION_FAILED, env));
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
     * This means for FTPConnectors:
     * <UL>
     * <LI>close the FTP connection.
     * <LI>check if there has been a temp directory created and delete it.
     * </UL>
     */
    public void close ()
    {
        // close the ftp connection
        if (this.ftpClient != null)
        {
            if (this.ftpClient.isConnected ())
            {
                try
                {
                    this.ftpClient.disconnect ();
                } // try
                catch (IOException f)
                {
                    // do nothing
                } // catch
            } // if (this.ftpClient.isConnected ())
        } // if (this.ftpClient != null)
        // check if the temp directory should be deleted
        if (this.isDeleteTemp)
        {
            this.deleteTempDir ();
        } // if
    } // close


    /**************************************************************************
     * The dir method reads from the import source and returns all importable
     * objects found in a array of strings. <BR/>
     * This means for an EDISwitchConnector_01 that a connection is opened to the ftp
     * server (in case it has timed out) and the files are read from the
     * ftp server path. <BR/>
     *
     * @return  an array of strings containing the importable files found or
     *          <CODE>null</CODE> otherwise.
     *
     * @throws  ConnectionFailedException
     *          The connection could not be established.
     */
    public String[] dir ()
        throws ConnectionFailedException
    {
        String fileName = null;         // the filename
/* KR never read
        String senderString = null;     // the sender value
        String stString = null;         // the ST(atus) value
        String APRFString = null;       // application reference
        String SNRFString = null;       // sender reference number
*/
        String serviceRefString = null; // service reference value
        String [] linesToParse = null;  // the output of the FTP server
        String [] returnFiles = null;   // the file names to return
        Vector<String> vector = new Vector<String> ();  // a vector
        StringTokenizer st = null;      // a string tokenizer

        try
        {
            // first ensure that we have an ftp connection
            if (this.ftpClient == null || !this.ftpClient.isConnected ())
            {
                this.initConnector ();
            } // if
            // get the output for the command
            linesToParse = this.ftpClient.listNames ();
            // parse the output lines
            // ignore the first 2 lines because they just show
            // the headers and a separator line
            for (int i = 3; i < linesToParse.length; i++)
            {
                // create a tokenizer with default delimiter "\t\n\r"
                st = new StringTokenizer (linesToParse [i], "\t\n\r ", false);
                // jump to the 4. token
                // this is the position of the filename
                try
                {
                    /*senderString = */st.nextToken ();
                    /*stString = */st.nextToken ();
                    /*APRFString = */st.nextToken ();
                    /*SNRFString = */st.nextToken ();
                    serviceRefString = st.nextToken ();
                    // the ServiceRefString is the filename we want to extract
                    fileName = serviceRefString;
                    // this token represents the filename
                    vector.addElement (fileName);
                } // try
                catch (NoSuchElementException e)
                {
                    // do nothing: this entry will be ignored
                } // catch
            } // for (int i = 3; i < linesToParse.length; i++)
            // create the array with the filenames
            returnFiles = new String [vector.size ()];
            vector.copyInto (returnFiles);
            // return the resulting string array
            return returnFiles;
        } // try
        catch (ConnectionFailedException e)
        {
            throw e;
        } // catch
        catch (IOException e)
        {
            throw new ConnectionFailedException (
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_COULD_NOT_READ_EDISWITCH_DIR, env));
        } // catch
    } // dir


    /**************************************************************************
     * Writes the export file to the ftp server.<BR/>
     *
     * @param   fileName    The name of the source file.
     *
     * @exception   ConnectionFailedException
     *              The connection could not be established.
     */
    public void write (String fileName)
        throws ConnectionFailedException
    {
        FileInputStream input;

        // start writing an exportdocument to the ftp server
        try
        {
            // first ensure that we have an ftp connection
            if (this.ftpClient == null || !this.ftpClient.isConnected ())
            {
                this.initConnector ();
            } // if

            // get an importstream to the file we want to write
            input = new FileInputStream (this.path + fileName);
            // check if this.ftpApplicationRef is empty
            this.ftpApplicationRef = this.ftpApplicationRef.trim ();
            this.ftpRecipientId = this.ftpRecipientId.trim ();
            this.ftpSenderRefNum = this.ftpSenderRefNum.trim ();
            // define the EDISwitch filename with the options set
            String remoteFile = "%" + this.ftpRecipientId + "%" + this.ftpApplicationRef +
                                "%" + this.ftpSenderRefNum + "%";
            // now write the local file to the ftp server
            this.ftpClient.storeFile (remoteFile, input);

//showDebug ("replyCode = " + this.ftpClient.getReplyCode ());
/* BB HINT: show the reply of the server
String [] reply = this.ftpClient.getReplyStrings ();
for (int i = 0; i < reply.length;  i++)
    this.trace (reply [i] + IE302.TAG_NEWLINE);
*/
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
    } // write


    /**************************************************************************
     * Retrieves the file from the ftp server and writes it into the
     * directory from where the integrator reads the files.<BR/>
     *
     * @param   fileName    The name of the file to read.
     *
     * @throws  ConnectionFailedException
     *          The connection could not be established.
     */
    public void read (String fileName)
        throws ConnectionFailedException
    {
        FileOutputStream output;

        // start reading a file from a FTP-server an copy it into the
        // destination directory
        try
        {
            // first ensure that we have an ftp connection
            if (this.ftpClient == null || !this.ftpClient.isConnected ())
            {
                this.initConnector ();
            } // if
            // now write the file
            output = new FileOutputStream (this.path + fileName);
            // now read the file from the ftp server
            this.ftpClient.retrieveFile (fileName, output);
            // set the name of the import file read
            this.setFileName (fileName);
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
    } // read


    /**************************************************************************
     * Read a file from the EDI Switch server and copy it to the
     * destination path.<BR/>
     *
     * Note this is possibly something the EDISwitch Server is not really meant
     * to support. <BR/>
     *
     * Note that this feature is not supported by the EDISwitchConnector. <BR/>
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
        FileOutputStream output;

        // start reading a file from a FTP-server an copy it into the
        // destination directory
        try
        {
            // first ensure that we have an ftp connection
            if (this.ftpClient == null || !this.ftpClient.isConnected ())
            {
                this.initConnector ();
            } // if

            // now write the file
            output = new FileOutputStream (destinationPath + destinationFileName);
            // now read the file from the ftp server
            this.ftpClient.retrieveFile (fileName, output);
            // return the size of the file to indicate that the reading
            // was successful
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
    } // readFile


    /**************************************************************************
     * Write a file to a EDISwitch Server. <BR/>
     *
     * Note this is possibly something the EDISwitch Server is not really meant
     * to support. <BR/>
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
        FileInputStream input;

        try
        {
            // first ensure that we have an ftp connection
            if (this.ftpClient == null || !this.ftpClient.isConnected ())
            {
                this.initConnector ();
            } // if

            // get an importstream to the file we want to write
            input = new FileInputStream (sourcePath + fileName);
            // check if this.ftpApplicationRef is empty
            this.ftpApplicationRef = this.ftpApplicationRef.trim ();
            this.ftpRecipientId = this.ftpRecipientId.trim ();
            this.ftpSenderRefNum = this.ftpSenderRefNum.trim ();
            // define the EDISwitch filename with the options set
            // note that we cannot use the application ref because
            // we have to declare it a binary file
            String remoteFile = "%" + this.ftpRecipientId + "%%" +
                                this.ftpSenderRefNum + "%b";
            // now write the local file to the ftp server
            this.ftpClient.storeFile (remoteFile, input);
            // return the filename to indicate that writing was successful
            // note that the EDISwitch server will assign a transaction number
            // as filename but we cannot predict the right filename
            // therefore the fileName we return here can be wrong
            return fileName;
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
    } // writeFile


    /**************************************************************************
     * Delete a file from the EDISwitch Server.<BR/>
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
        // start reading a file from a FTP-server an copy it into the
        // destination directory
        try
        {
            // first ensure that we have an ftp connection
            if (this.ftpClient == null || !this.ftpClient.isConnected ())
            {
                this.initConnector ();
            } // if

            // try to delete the file and return if it was successful
            return this.ftpClient.deleteFile (fileName);
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
                DITokens.ML_EDISERVER, env),
            Datatypes.DT_TEXT, this.arg1);
        this.showProperty (table, DIArguments.ARG_ARG2,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EDISERVERPORT, env),
            Datatypes.DT_TEXT, this.arg2);
        this.showProperty (table, DIArguments.ARG_ARG3,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EDIUSER, env),
            Datatypes.DT_TEXT, this.arg3);
        this.showProperty (table, DIArguments.ARG_ARG4,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EDIPASSWORD, env),
            Datatypes.DT_PASSWORD, this.arg4);
        this.showProperty (table, DIArguments.ARG_ARG5,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EDISENDERID, env),
            Datatypes.DT_TEXT, this.arg5);
        this.showProperty (table, DIArguments.ARG_ARG6,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EDIRECEIVERID, env),
            Datatypes.DT_TEXT, this.arg6);
        this.showProperty (table, DIArguments.ARG_ARG7,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EDIAPPLICATIONREF, env),
            Datatypes.DT_TEXT, this.arg7);
    } //  showProperties


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
                DITokens.ML_EDISERVER, env),
            Datatypes.DT_NAME, this.arg1);
        this.showFormProperty (table, DIArguments.ARG_ARG2,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EDISERVERPORT, env),
            Datatypes.DT_NAME, this.arg2);
        this.showFormProperty (table, DIArguments.ARG_ARG3,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EDIUSER, env),
            Datatypes.DT_NAME, this.arg3);
        this.showFormProperty (table, DIArguments.ARG_ARG4,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EDIPASSWORD, env),
            Datatypes.DT_PASSWORD, this.arg4);
        this.showFormProperty (table, DIArguments.ARG_ARG5,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EDISENDERID, env),
            Datatypes.DT_TEXT, this.arg5);
        this.showFormProperty (table, DIArguments.ARG_ARG6,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EDIRECEIVERID, env),
            Datatypes.DT_TEXT, this.arg6);
        this.showFormProperty (table, DIArguments.ARG_ARG7,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EDIAPPLICATIONREF, env),
            Datatypes.DT_TEXT, this.arg7);
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
                DITokens.ML_EDISERVER, env),
            Datatypes.DT_TEXT, this.arg1);
        this.showProperty (table, DIArguments.ARG_ARG2,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EDISERVERPORT, env),
            Datatypes.DT_TEXT, this.arg2);
        this.showProperty (table, DIArguments.ARG_ARG3,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EDIUSER, env),
            Datatypes.DT_TEXT, this.arg3);
        // BB HINT: we dont show the password
        this.showProperty (table, DIArguments.ARG_ARG5,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EDISENDERID, env),
            Datatypes.DT_TEXT, this.arg5);
        this.showProperty (table, DIArguments.ARG_ARG6,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EDIRECEIVERID, env),
            Datatypes.DT_TEXT, this.arg6);
        this.showProperty (table, DIArguments.ARG_ARG7,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EDIAPPLICATIONREF, env),
            Datatypes.DT_TEXT, this.arg7);
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
                DITokens.ML_EDISERVER, env) + ": " + 
            this.ftpServer, false);
        log.add (DIConstants.LOG_ENTRY,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EDISERVERPORT, env) + ": " +
            this.ftpServerPort, false);
        log.add (DIConstants.LOG_ENTRY,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EDIUSER, env) + ": " +
            this.ftpUser, false);
        // we dont show the password in the log
        log.add (DIConstants.LOG_ENTRY,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EDISENDERID, env) + ": " +
            this.ftpSenderRefNum, false);
        log.add (DIConstants.LOG_ENTRY, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EDIRECEIVERID, env) + ": " +
            this.ftpRecipientId, false);
        log.add (DIConstants.LOG_ENTRY, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EDIAPPLICATIONREF, env) + ": " +
            this.ftpApplicationRef, false);
    } // addSettingsToLog

} // EDISwitchConnector_01
