/*
 * Class: FileConnector_01.java
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
import ibs.util.file.FileHelpers;

import java.io.File;


/******************************************************************************
 * the FileConnector_01 Class reads
 *
 * @version     $Id: FileConnector_01.java,v 1.17 2010/04/07 13:37:04 rburgermann Exp $
 *
 * @author      Buchegger Bernd (BB), 991008
 ******************************************************************************
 */
public class FileConnector_01 extends Connector_01 implements ConnectorInterface
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: FileConnector_01.java,v 1.17 2010/04/07 13:37:04 rburgermann Exp $";

    /**
     * The prefix for a relative path. <BR/>
     */
    public static final String RELATIVEPATH_PREFIX = "." + File.separator;

    /**
     * The original path. <BR/>
     * This is used to find out if a specific path has been set.
     */
    private String p_origPath = null;


    /**************************************************************************
     * Creates a FileConnector_01 Object. <BR/>
     */
    public FileConnector_01 ()
    {
        // call constructor of super class Connector_01:
        super ();
    } // FileConnector_01


    /**************************************************************************
     * Creates a FileConnector_01 Object. <BR/>
     *
     * @param   oid     oid of the object
     * @param   user    user that created the object
     *
     * @see     ibs.bo.BusinessObject#BusinessObject(OID, User)
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public FileConnector_01 (OID oid, User user)
    {
        // call constructor of super class Connector_01:
        super (oid, user);
    } // FileConnector_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set class specifics
        this.connectorType = DIConstants.CONNECTORTYPE_FILE;

        // set stored procedure names
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
        String pathStr = this.arg1;
        // check if any path has been set
        if (pathStr != null && pathStr.length () > 0)
        {
            // check if the path starts with "./"
            // in that case the ./ will be substituted with the
            // absbasepath
            if (pathStr.startsWith (FileConnector_01.RELATIVEPATH_PREFIX))
            {
                pathStr = this.app.p_system.p_m2AbsBasePath +
                    pathStr.substring (FileConnector_01.RELATIVEPATH_PREFIX.length ());
            } // if (pathStr.startsWith (RELATIVEPATH_PREFIX))
        } // if (pathStr != null && pathStr.length() > 0)
        this.setPath (FileHelpers.addEndingFileSeparator (pathStr));
    } // setArguments


    /**************************************************************************
     * Initializes the connector and checks the settings. <BR/>
     *
     * @throws  ConnectionFailedException
     *          The connection could not be established
     *
     * @see ibs.di.connect.Connector_01#initConnector
     */
    public void initConnector ()
        throws ConnectionFailedException
    {
        // remember the original path:
        this.p_origPath = this.path;

        // now check if the server directory exists and is a directory
        File dir = new File (this.path);
        if (!dir.isDirectory ())
        {
            throw new ConnectionFailedException ( 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_DIRECTORY_NOT_EXISTS, env));
        } // if
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
            // set the new path:
            this.path =
                FileHelpers.addEndingFileSeparator (this.path + processId);
            // ensure that the new directory exists:
            FileHelpers.makeDir (this.path);
        } // if
    } // initBackupConnector


    /**************************************************************************
     * Closes the connector. This method includes all actions that need to
     * be done to close a connection to a data source or destination
     * and deletes the temp directory if applicable.<BR/>
     * This means for FileConnectors:
     * <UL>
     * <LI>check if there has been a temp directory created and delete it.
     * </UL>
     *
     * @see ibs.di.connect.Connector_01#close
     */
    public void close ()
    {
        // check if the temp directory should be deleted
        if (this.isDeleteTemp)
        {
            this.deleteTempDir ();
        } // if

        // check if there has been set a specific directory:
        if (!this.path.equals (this.p_origPath))
        {
            // try to delete the directory:
            // if it is not empty it will not be deleted
            FileHelpers.deleteDir (this.path);
        } // if
    } // close


    /**************************************************************************
     * The dir method reads from the import source and returns all importable
     * objects found in a array of strings. <BR/>
     *
     * @return  an array of strings containing the importable objects found or
     *          null otherwise
     *
     * @exception   ConnectionFailedException
     *              The connection could not be established
     *
     * @see ibs.di.connect.Connector_01#dir
     */
    public String[] dir ()
        throws ConnectionFailedException
    {
        String [] files;
        String [] resultFiles;
        File file;
        int found = 0;
        int j = 0;

        // get the files in the directory:
        files = FileHelpers.getFilesArray (this.path);
        // check if we got any files
        if (files == null || files.length == 0)
        {
            return null;
        } // if

        // files found
        // loop through the files array and create a result file array
        // that contains only files. The problem is that the getFilesArray
        // method also returned the directories we do not need in the list
        for (int i = 0; i < files.length; i++)
        {
            // create a file
            file = new File (this.path + files [i]);
            // and check if it is a valid file
            if (!file.exists () || !file.isFile ())
            {
                // delete the entry by setting it to null
                files [i] = null;
            } // if (!file.exists () || !file.isFile ())
            else
            {
                found++;
            } // else

        } // for (int i = 0; i < files.length; i++)
        // check if we found any valid files
        if (found == 0)
        {
            return null;
        } // if

        // valid files found
        // now copy the valid file into the result array
        resultFiles = new String [found];
        for (int i = 0; i < files.length; i++)
        {
            if (files [i] != null)
            {
                resultFiles [j++] = files [i];
            } // if (files [i] != null)
        } // for (int i = 0; i < files.length; i++)
        return resultFiles;
    } // dir


    /**************************************************************************
     * Writes the export file to the export destination.<BR/>
     *
     * BB HINT: the FileConnector_01 is a special case because
     * source path and export destination path are identical
     * therefore do nothing. <BR/>
     *
     * @param   fileName    the name of the source file
     *
     * @exception   ConnectionFailedException
     *              The connection could not be established
     *
     * @see ibs.di.connect.Connector_01#write(String)
     */
    public void write (String fileName)
        throws ConnectionFailedException
    {
        // do nothing
    } // write


    /**************************************************************************
     * Retrieves the file from the import source and writes it into the
     * directory from where the integrator reads the files.<BR/>
     *
     * BB HINT: the FileConnector_01 is a special case
     * because we only need to set the fileName we want to read.
     * The file already resides in the directory from where
     * the integrator can read. <BR/>
     *
     * @param   fileName    the name of the file to read
     *
     * @throws  ConnectionFailedException
     *          The connection could not be established
     *
     * @see ibs.di.connect.Connector_01#read(String)
     */
    public void read (String fileName)
        throws ConnectionFailedException
    {
        this.setFileName (fileName);
    } // read


    /**************************************************************************
     * Read a file from the connector and copy it to the destination path. <BR/>
     * This is meant for attachment like files that can have a different
     * handling as importfiles depending on the connector used.<BR/>
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
    public long readFile (String fileName,
                          String destinationPath,
                          String destinationFileName)
        throws ConnectionFailedException
    {
        // ensure ending file separator
        String destPath = FileHelpers.addEndingFileSeparator (destinationPath);
        // copy the file
        if (FileHelpers.copyFile (this.getPath () + fileName,
                                  destPath + destinationFileName))
        {
            // return the size of the file
            long filesize = FileHelpers.getFileSize (destPath,
                destinationFileName);

            return filesize;
        } // if

        // file could not be copied
        // return -1 in order to indicate that file could not have been copied
        return -1;
    } // readFile


    /**************************************************************************
     * Write an file to a connector. <BR/>
     * This is meant for attachment like files. The method has to ensure a
     * unique file name. It returns the file name used to write the file and
     * null in case the file could not have been written. <BR/>
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
        String uniqueFileName;
        String prefix = "";

        // ensure ending file separator
        String srcPath = FileHelpers.addEndingFileSeparator (sourcePath);
        // ensure a unique filename
        if (this.p_filePrefix != null && this.p_filePrefix.length () > 0)
        {
            prefix = this.p_filePrefix;
        } // if
        uniqueFileName =
            FileHelpers.getUniqueFileName (this.getPath (), prefix + fileName);

        // copy the file
        if (FileHelpers.copyFile (srcPath + fileName,
            this.getPath () + uniqueFileName))
        {
            return uniqueFileName;
        } // if

        return null;
    }   // writeFile


    /**************************************************************************
     * Delete a file from its original location via the connector.<BR/>
     * This will be used in case the "delete file after import" option
     * has been set within an import and is meant for all sorts of
     * attachment like files.
     * This can be used for the importfile itself and for attachment like
     * files.<BR/>
     *
     * @param fileName            name of the file to delete
     *
     * @return true if the file could be deleted or false otherwiese
     *
     * @exception   ConnectionFailedException
     *              could not access the file
     *
     * @see ibs.di.connect.Connector_01#deleteFile
     */
    public boolean deleteFile (String fileName)
        throws ConnectionFailedException

    {
        return FileHelpers.deleteFile (this.getPath () + fileName);
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
                DITokens.ML_SERVERDIRECTORY, env),
            Datatypes.DT_TEXT, this.arg1);
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
                DITokens.ML_SERVERDIRECTORY, env),
            Datatypes.DT_NAME, this.arg1);
    } // showFormProperties


    /**************************************************************************
     * Displays the settings of the connector.<BR/>
     *
     * @param   table       Table where the settings shall be added.
     *
     * @see ibs.di.connect.Connector_01#showSettings
     */
    public void showSettings (TableElement table)
    {
        super.showSettings (table);

        // display connector specific settings
        if (this.p_isBackupConnector)   // connector used for backup?
        {
            this.showProperty (table, DIArguments.ARG_IMPORTPATH,
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                    DITokens.ML_BACKUPPATH, env),
                Datatypes.DT_TEXT, this.path);
        } // if connector used for backup
        else
        // standard connector
        {
            this.showProperty (table, DIArguments.ARG_IMPORTPATH,
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                    DITokens.ML_PATH, env),
                Datatypes.DT_TEXT, this.path);
        } // else standard connector
    } // showSettings


    /**************************************************************************
     * Adds the settings of the connector to a log.<BR/>
     *
     * @param   log     the log to add the setting to
     *
     * @see ibs.di.connect.Connector_01#addSettingsToLog
     */
    public void addSettingsToLog (Log_01 log)
    {
        super.addSettingsToLog (log);
        // add connector specific settings
        log.add (DIConstants.LOG_ENTRY,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_PATH, env) + ": " + this.path, false);
    } // addSettingsToLog

} // FileConnector_01
