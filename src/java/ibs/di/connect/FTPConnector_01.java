/*
 * Class: FTPConnector_01.java
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
import ibs.di.WindowsFTPFileListParser;
import ibs.di.connect.ConnectionFailedException;
import ibs.di.connect.ConnectorInterface;
import ibs.di.connect.Connector_01;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
//TODO: unsauber
import ibs.service.user.User;
import ibs.tech.html.TableElement;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;
import ibs.util.file.FileHelpers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.DefaultFTPFileListParser;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileListParser;
import org.apache.commons.net.ftp.FTPReply;

//import com.oroinc.net.ftp.DefaultFTPFileListParser;
//import com.oroinc.net.ftp.FTP;
//import com.oroinc.net.ftp.FTPClient;
//import com.oroinc.net.ftp.FTPFile;
//import com.oroinc.net.ftp.FTPFileListParser;
//import com.oroinc.net.ftp.FTPReply;


/******************************************************************************
 * The FTPConnector_01 class reads and writes files from an to an FTP
 * server.<BR/>
 * This connector is able to read file directories from Microsoft and
 * Unix environments. Both do have a different file structure. A Microsoft
 * directory will look like:
 * <pre>
 * 02-20-01  01:58PM                  986 file.xml
 * 03-06-01  09:49AM       &lt;DIR>          directory
 * </pre>
 * and unix ftp directories look like
 * <pre>
 * drwxr-xr-x   2 bernd    entwickl     1024 Oct  9 18:24 directory
 * -rw-r--r--   1 bernd    entwickl      229 Mar 22  2000 file.xml
 * </pre>
 * Note that in case a ftp server supports a directory structure that does not
 * comply with these two definitions above the ftp connector will fail to read
 * the directory.<BR/>
 *
 * @version     $Id: FTPConnector_01.java,v 1.26 2010/04/07 13:37:05 rburgermann Exp $
 *
 * @author      Buchegger Bernd (BB), 991008
 ******************************************************************************
 */
public class FTPConnector_01 extends Connector_01 implements ConnectorInterface
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: FTPConnector_01.java,v 1.26 2010/04/07 13:37:05 rburgermann Exp $";


    /**
     *  name of FTP server
     */
    public String ftpServer = "";

    /**
     *  path at FTP server
     */
    public String ftpPath = "";

    /**
     *  user to login at FTP server
     */
    public String ftpUser = "";

    /**
     *  password for login at FTP server
     */
    public String ftpPassword = "";

    /**
     *  the FTP client object to connect to the ftp server
     */
    private FTPClient ftpClient = null;


    /**************************************************************************
     * Creates a FTPConnector_01 Object. <BR/>
     */
    public FTPConnector_01 ()
    {
        // call constructor of super class Connector_01:
        super ();
    } // FTPConnector_01


    /**************************************************************************
     * Creates a FTPConnector_01 Object. <BR/>
     *
     * @param   oid     oid of the object.
     * @param   user    user that created the object.
     *
     * @see     ibs.bo.BusinessObject#BusinessObject(OID, User)
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public FTPConnector_01 (OID oid, User user)
    {
        // call constructor of super class Connector_01:
        super (oid, user);
    } // FTPConnector_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set class specifics:
        this.connectorType = DIConstants.CONNECTORTYPE_FTP;

        // set stored procedure names:
        this.procCreate =    "p_Connector_01$create";
        this.procRetrieve =  "p_Connector_01$retrieve";
        this.procDelete =    "p_Connector_01$delete";
        this.procChange =    "p_Connector_01$change";

        // set number of parameters for procedure calls:
        this.specificRetrieveParameters = 12;
        this.specificChangeParameters = 12;
    } // initClassSpecifics


    /**************************************************************************
     * Sets the arguments to class specific properties. <BR/>
     */
    public void setArguments ()
    {
        // set the server directory which is hold in the 1. argument
        this.ftpServer = this.arg1;
        this.ftpPath = this.arg2;
        this.ftpUser = this.arg3;
        this.ftpPassword = this.arg4;
    } // setArguments


    /**************************************************************************
     * Initializes the connector and checks the settings. <BR/>
     *
     * @throws  ConnectionFailedException
     *          The connection could not be established.
     */
    public void initConnector ()
        throws ConnectionFailedException
    {
        int reply;
        String ftpServerStr;

        // trim the path because it is sometimes set to " "
        if (this.ftpPath != null)
        {
            this.ftpPath = this.ftpPath.trim ();
        } // if
        // try to login to the FTP server
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
            // check if a server has been specified
            if (this.ftpServer == null || this.ftpServer.length () == 0)
            {
                throw new ConnectionFailedException (
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_FTP_SERVER_NOT_FOUND,
                        new String[] {this.ftpServer}, env));
            } // if (this.ftpServer == null || this.ftpServer.length () == 0)
            // check if ftpServer starts with "ftp://"
            // this should be valid but must be cut off
            if (this.ftpServer.toLowerCase ().startsWith ("ftp://"))
            {
                ftpServerStr = this.ftpServer.substring (6);
            } // if
            else
            {
                ftpServerStr = this.ftpServer;
            } // else

            // try to open a connection to the ftp server
            this.ftpClient.connect (ftpServerStr);

            // After connection attempt, check the reply code to verify success.
            reply = this.ftpClient.getReplyCode ();
            if (!FTPReply.isPositiveCompletion (reply))
            {
                this.ftpClient.disconnect ();
                throw new ConnectionFailedException (
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_FTP_SERVER_NOT_FOUND,
                        new String[] {this.ftpServer}, env));
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
            // try to change directory if applicable
            if (this.ftpPath != null && this.ftpPath.length () > 0)
            {
                // send a change directory command and check if it was successful
                if (!this.ftpClient.changeWorkingDirectory (this.ftpPath))
                {
                    // could not change to directory
                    this.ftpClient.disconnect ();
                    throw new ConnectionFailedException (
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_FTP_PATH_NOT_FOUND,
                            new String[] {this.ftpPath}, env));
                } // if (! this.ftpClient.changeWorkingDirectory (this.ftpPath))
            } // if (this.ftpPath.length () > 0)
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
                    IOHelpers.showMessage ("Disconnecting from FTP server",
                        e, this.app, this.sess, this.env, true);
                } // catch
            } // if (ftp.isConnected())
            throw new ConnectionFailedException (
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_FTP_CONNECTION_FAILED, env));
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
     * This means for an FTPConnector_01 that a connection is opened to the ftp
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
        int j = 0;
        String fileName;
        String [] files = null;
        FTPFile [] ftpFiles = null;
        String [] returnFiles = null;
        FTPFileListParser listParser = null;

        try
        {
            // first ensure that we have an ftp connection
            if (this.ftpClient == null || !this.ftpClient.isConnected ())
            {
                this.initConnector ();
            } // if

            // check which server type has been set
            if (this.ftpClient.getSystemName ().toUpperCase ().startsWith (DIConstants.SERVERTYPENAME_UNIX))
            {
                // set the default parser
                listParser = new DefaultFTPFileListParser ();
            } // if (this.ftpServerType.equals (DIConstants.SERVERTYPENAME_UNIX))
            else if (this.ftpClient.getSystemName ().toUpperCase ().startsWith (DIConstants.SERVERTYPENAME_WINDOWS))
            {
                // set the windows ftp parser
                listParser = new WindowsFTPFileListParser ();
            } // else if (this.ftpServerType.equals (DIConstants.SERVERTYPENAME_WINDOWS))
            else    // else set the default parser
            {
                listParser = new DefaultFTPFileListParser ();
            } // else set the default parser
            // read the files from the ftp directory
            ftpFiles = this.ftpClient.listFiles (listParser);
            // check if any files have been found
            if (ftpFiles == null)       // no files found?
            {
                return null;
            } // if no files found

            // files found
            // copy the files from the FTPFile array into a String array
            files = new String [ftpFiles.length];
            for (int i = 0; i < ftpFiles.length; i++)
            {
                // ensure it is a file
                if (ftpFiles [i].isFile ())
                {
                    // get the name of the file
                    fileName = ftpFiles[i].getName ();
                    // add the fileName to the files array
                    files[j++] = fileName;
                } // if (ftpFiles [i].isFile ())
            } // for (int i = 0; i < ftpFiles.length (); i++)
            // the effective number of files available could be less
            // then the size of the allocated array.
            // we need to copy the files again
            returnFiles = new String [j];

            for (int i = 0; i < j; i++)
            {
                returnFiles [i] = files [i];
            } // for (int i = 0; i < files.length (); i++)

            // return the resulting files array
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
                    DIMessages.ML_MSG_COULD_NOT_READ_FTP_DIR, env));
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
        String uniqueFileName;
        String prefix = "";
        boolean isOk;

        // first check if the fileName containers any slashes
        // slashes or backslashes will be replaces by a "-"
        String fileN = fileName.replace ('/', '-');
        fileN = fileN.replace ('\\', '-');

        // start writing an exportdocument to the ftp server
        try
        {
            // first ensure that we have an ftp connection
            if (this.ftpClient == null || !this.ftpClient.isConnected ())
            {
                this.initConnector ();
            } // if

            // now write the file
            input = new FileInputStream (this.path + fileN);
            // ensure a unique filename on the FTPServer
            // We cannot access the new name in case it has been created
            // we therefore must implement the storeUniqueFile mechanism ourselves
            // there could be performance loss because of the communication
            // between the FTP server und the FTPConnector
            if (this.p_filePrefix != null && this.p_filePrefix.length () > 0)
            {
                prefix = this.p_filePrefix;
            } // if
            uniqueFileName = this.getUniqueFTPFileName (prefix + fileN);
            // now write the local file to the ftp server
            // ensuring that the file will be stored under a
            // unique filename
            isOk = this.ftpClient.storeFile (uniqueFileName, input);
            // close the file input stream
            input.close ();
            // check if writing was successful
            if (!isOk)
            {
                // throw an exception
                throw new ConnectionFailedException (
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_COULD_NOT_WRITE_FILE,
                        new String[] {fileN}, env));
            } // if (!isOk)
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
        // start reading a file from a FTP-server and copy it into the
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
            if (this.ftpClient.retrieveFile (fileName, output))
            {
                // set the filename to indicate successful retrieval
                this.setFileName (fileName);
            } // if (this.ftpClient.retrieveFile (fileName, output))
            else    // could not retrieve file
            {
                // reset filename to indicate that file could not be retrieved
                this.setFileName ("");
            } // could not retrieve file
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
     * Read a file from the FTP server and copy it to the destination path.<BR/>
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
        // start reading a file from a FTP-server and copy it into the
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
            if (this.ftpClient.retrieveFile (fileName, output))
            {
                // return the size of the file
                return FileHelpers.getFileSize (destinationPath, destinationFileName);
            } // if (this.ftpClient.retrieveFile (fileName, output))

            // could not retrieve file
            // return -1 to indicate that file could not be copied
            return -1;
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
     * Write a file to a FTP server.<BR/>
     * Note that the method generated a unique filename at the FTP server. This
     * will be done by adding a counter to the beginning of the fileName.<BR/>
     *
     * @param   sourcePath  Path to read the file from.
     * @param   fileName    Name of the file to read.
     *
     * @return  The name of the file written or null in case it could not have
     *          been written.
     *
     * @throws  ConnectionFailedException
     *          Could not access the file.
     *
     * @see ibs.di.connect.Connector_01#writeFile
     */
    public String writeFile (String sourcePath, String fileName)
        throws ConnectionFailedException
    {
        FileInputStream input;
        boolean isOk;
        String uniqueFileName;
        String prefix = "";

        // start writing an exportdocument to the ftp server
        try
        {
            // first ensure that we have an ftp connection
            if (this.ftpClient == null || !this.ftpClient.isConnected ())
            {
                this.initConnector ();
            } // if

            // now write the file
            input = new FileInputStream (sourcePath + fileName);
            // ensure a unique filename on the FTPServer
            // We cannot access the new name in case it has been created
            // we therefore must implement the storeUniqueFile mechanism ourselves
            // there could be performance loss because of the communication
            // between the FTP server und the FTPConnector
            if (this.p_filePrefix != null && this.p_filePrefix.length () > 0)
            {
                prefix = this.p_filePrefix;
            } // if
            uniqueFileName = this.getUniqueFTPFileName (prefix + fileName);
            // now write the local file to the ftp server
            // ensuring that the file will be stored under a
            // unique filename
            isOk = this.ftpClient.storeFile (uniqueFileName, input);
            // close the file input stream
            input.close ();
            //  return the fileName in case the file could be written
            if (isOk)
            {
                return uniqueFileName;
            } // if

            return "";
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
     * Delete a file from the FTP server.<BR/>
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
        try
        {
            // first ensure that we have an ftp connection
            if (this.ftpClient == null || !this.ftpClient.isConnected ())
            {
                this.initConnector ();
            } // if
            // delete the file with the specified name
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
     * Create a unique filename on the FTP server. <BR/>
     * The method checks if a fileName already exists on the fileserver
     * and adds a counter to the beginning of the filename until the
     * filename is unique.<BR/>
     *
     * @param fileName      the filename to be checked
     *
     * @return the unique filename
     *
     * @exception   ConnectionFailedException
     *              could not access the file
     */
    private String getUniqueFTPFileName (String fileName)
        throws ConnectionFailedException
    {
        String [] fileNames;
        int counter = 1;
        int postfixIndex;
        String postfix = "";
        String name = "";
        boolean isFound = true;
        String fileN = fileName;

        // get the postfix
        postfixIndex = fileN.lastIndexOf (".");
        // check if the filename contains a postfix
        if (postfixIndex > -1)
        {
            postfix = fileN.substring (postfixIndex);
            name = fileN.substring (0, postfixIndex);
        }  // if (postfixIndex > -1)
        else    // no postfix found
        {
            name = fileN;
            postfix = "";
        } // else no postfix found

        try
        {
            // read the filesnames from the ftp directory
            fileNames = this.ftpClient.listNames ();
            // check if we got any files
            if (fileNames == null || fileNames.length == 0)
            {
                return fileN;
            } // if

            // loop as long until we cannot find the fileName in the
            // fileNames array anymore
            while (isFound)
            {
                isFound = false;
                // loop thourgh the filenames list and check if we can find the
                // filename. abort the loop when we find the file
                for (int i = 0; i < fileNames.length && !isFound; i++)
                {
                    // check if we found the file
                    if (fileNames [i].equals (fileN))
                    {
                        isFound = true;
                    } // if (fileNames [i].equals (fileName))
                } // for (int i = 0; i < fileNames.length; i++)
                // check if the filename already existed
                if (isFound)
                {
                    // create a new filename by adding a counter
                    // to the filename
                    fileN = name + "_" + counter + postfix;
                    // increase the counter
                    counter++;
                } // if (isFound)
            } // while (isFound)
            // return the generated fileName
            return fileN;
        } // catch
        catch (IOException e)
        {
            throw new ConnectionFailedException (
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_CONNECTORFAILED, env));
        } // catch
    } // getUniqueFTPFileName


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
        // loop through all properties of this object and display them:
        this.showProperty (table, DIArguments.ARG_ARG1, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_FTPSERVER, env),
            Datatypes.DT_TEXT, this.arg1);
        this.showProperty (table, DIArguments.ARG_ARG2,  
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_FTPPATH, env),
            Datatypes.DT_TEXT, this.arg2);
        this.showProperty (table, DIArguments.ARG_ARG3,  
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_FTPUSER, env),
            Datatypes.DT_TEXT, this.arg3);
        this.showProperty (table, DIArguments.ARG_ARG4,  
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_FTPUSERPASSWORD, env),
            Datatypes.DT_PASSWORD, this.arg4);
    } //  showProperties


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
        // loop through all properties of this object and display them:
        this.showFormProperty (table, DIArguments.ARG_ARG1,  
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_FTPSERVER, env),
            Datatypes.DT_NAME, this.arg1);
        this.showFormProperty (table, DIArguments.ARG_ARG2,  
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_FTPPATH, env),
            Datatypes.DT_NAME, this.arg2);
        this.showFormProperty (table, DIArguments.ARG_ARG3,  
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_FTPUSER, env),
            Datatypes.DT_NAME, this.arg3);
        this.showFormProperty (table, DIArguments.ARG_ARG4,  
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_FTPUSERPASSWORD, env),
            Datatypes.DT_PASSWORD, this.arg4);
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
                DITokens.ML_FTPSERVER, env),
            Datatypes.DT_TEXT, this.ftpServer);
        this.showProperty (table, DIArguments.ARG_ARG4,  
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_FTPPATH, env),
            Datatypes.DT_TEXT, this.ftpPath);
        this.showProperty (table, DIArguments.ARG_ARG2,  
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_FTPUSER, env),
            Datatypes.DT_TEXT, this.ftpUser);
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
                DITokens.ML_FTPSERVER, env) + ": " +
            this.ftpServer, false);
        log.add (DIConstants.LOG_ENTRY, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_FTPPATH, env) + ": " +
            this.ftpPath, false);
        log.add (DIConstants.LOG_ENTRY,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_FTPUSER, env) + ": " +
            this.ftpUser, false);
    } // addSettingsToLog

} // FTPConnector_01
