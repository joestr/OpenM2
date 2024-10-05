/*
 * Class: SQLInstaller.java
 */

// package:
package ibs.install.sql;

// imports:
import ibs.BaseObject;
import ibs.app.func.FunctionValues;
import ibs.di.DIConstants;
import ibs.di.Log_01;
import ibs.io.servlet.ApplicationInitializationException;
import ibs.service.conf.Configuration;
import ibs.tech.sql.DBConf;
import ibs.tech.sql.SQLConstants;
import ibs.util.StringHelpers;
import ibs.util.file.FileHelpers;
import ibs.util.file.FileManager;

import java.io.File;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


/******************************************************************************
 * This class is used to install new or update existing sql data. <BR/>
 *
 * @version     $Id: SQLInstaller.java,v 1.4 2008/09/17 16:35:31 kreimueller Exp $
 *
 * @author      Klaus, 27.08.2005
 ******************************************************************************
 */
public abstract class SQLInstaller extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SQLInstaller.java,v 1.4 2008/09/17 16:35:31 kreimueller Exp $";


    /**
     * Attribute name: source file name. <BR/>
     */
    private static final String ATTR_SOURCEFILE = "sourcefile";

    /**
     * Attribute name: display mode. <BR/>
     */
    private static final String ATTR_DISPMODE = "display mode";

    /**
     * System variable: database directory. <BR/>
     */
    private static final String SYSVAR_DBDIR = "#DBDIR#";

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
     * Perform the installation. <BR/>
     *
     * @param   installDir  The installation directory.
     * @param   installNode The xml node which defines the installation.
     * @param   values      The values for the current function.
     * @param   log         The log.
     *
     * @throws  ApplicationInitializationException
     *          There occurred an error during the installation process.
     */
    public static void install (File installDir, Node installNode,
                                 FunctionValues values, Log_01 log)
        throws ApplicationInitializationException
    {
        NamedNodeMap attributes;        // attributes of the installNode
        Node attribute;                 // attribute node
        String sourcePath = null;       // name and path of source file
        String dispMode = DBExec.DISP_ONE; // display mode

        // get the attributes from the node:
        attributes = installNode.getAttributes ();
        if (attributes != null)
        {
            // get the source file:
            if ((attribute = attributes
                .getNamedItem (SQLInstaller.ATTR_SOURCEFILE)) != null)
            {
                sourcePath = attribute.getNodeValue ();
            } // if

            // get the dsiplay mode:
            if ((attribute = attributes
                .getNamedItem (SQLInstaller.ATTR_DISPMODE)) != null)
            {
                dispMode = attribute.getNodeValue ();
            } // if
        } // if (attributes != null)

        // perform the installation:
        SQLInstaller.install (installDir, sourcePath, dispMode, values, log);
    } // install


    /**************************************************************************
     * Perform the installation. <BR/>
     *
     * @param   installDir  The installation directory.
     * @param   sourcePath  Path where to find the files.
     * @param   dispMode    The display mode.
     * @param   values      The values for the current function.
     * @param   log         The log.
     *
     * @throws  ApplicationInitializationException
     *          There occurred an error during the installation process.
     */
    public static void install (File installDir, String sourcePath,
                                String dispMode, FunctionValues values,
                                Log_01 log)
        throws ApplicationInitializationException
    {
        String sourcePathLocal = sourcePath; // variable for local assignments
        File sourceDir;                 // source directory
        File sourceFile = null;         // source file
        String sourceFileName = null;   // name (and path) of source file
        File[] installFiles = null;     // the files to be installed
        File[] validInstallFiles = null; // all valid install files
        File file = null;               // the current file
        DBExec installer = null;        // the sql installer
        DBConf dbConf = null;           // the database configuration

        // display the settings:
        log.add (DIConstants.LOG_ENTRY, SQLInstaller.ATTR_SOURCEFILE + "='" +
            sourcePathLocal + "'");

        // check constraints for settings:
        if (sourcePathLocal == null || sourcePathLocal.length () == 0)
        {
            log.add (DIConstants.LOG_ERROR,
                "Source file not defined. Install entry ignored.");
        } // if
        else                            // source defined
        {
            // get the database configuration:
            dbConf = ((Configuration) values.getConfiguration ()).getDbConf ();
            // replace database dir placeholder with directory name:
            sourcePathLocal = StringHelpers.replace (sourcePathLocal,
                SQLInstaller.SYSVAR_DBDIR, dbConf.getDbDir ());

            // add the source file name to the install directory and
            // divide the result into directory and file name:
            sourceFile = new File (installDir, FileHelpers
                .makeFileNameValid (sourcePathLocal));
            sourceDir = sourceFile.getParentFile ();
            sourceFileName = sourceFile.getName ();

            // and check if the source directory is a valid directory and
            // the source file exists:
            if ((!sourceDir.exists ()) || (!sourceDir.isDirectory ()))
            {
                log.add (DIConstants.LOG_ERROR,
                    "The source directory " + sourceDir + " is not valid." +
                    " Install entry ignored.");
            } // if
            else                        // source directory valid
            {
                // get all files within the directory which satisfy the
                // file name:
                installFiles = FileHelpers.getFilesArray (sourceDir,
                    FileManager.getFilter ("*"),
                    FileManager.getFilter (sourceFileName),
                    false);

                // check for valid installation files:
                validInstallFiles = SQLInstaller.getValidFiles (installFiles, log);

                // do we finally have any valid installation files:
                if (validInstallFiles != null && validInstallFiles.length > 0)
                {
                    log.add (DIConstants.LOG_ENTRY, "Installing " +
                        validInstallFiles.length + " files into database " +
                        dbConf.getDbSid () + " on server " +
                        dbConf.getDbServerName () + "...");

                    // perform the installation:
                    // loop through all files and install each of them:
                    for (int i = 0; i < validInstallFiles.length; i++)
                    {
                        // get the current file:
                        file = validInstallFiles[i];

                        // get the installer defined through database type:
                        installer = SQLInstaller.getInstaller (dbConf);
                        // perform the sql installation:
                        installer.exec (dbConf, file.getAbsolutePath (),
                            dispMode, log);
                    } // for i
                } // if (validFilesCount == 0)
                else    // no entries found
                {
                    log.add (DIConstants.LOG_WARNING,
                            sourceDir + File.separator + sourceFileName +
                            " did not match any sql files!");
                } // else no entries found
            } // else source directory valid
        } // else source defined
    } // install


    /**************************************************************************
     * Get the installer defined through the database type. <BR/>
     *
     * @param   dbConf  database configuration.
     *
     * @return  The database installer or
     *          <CODE>null</CODE> if there occurred an error.
     */
    private static DBExec getInstaller (DBConf dbConf)
    {
        DBExec installer = null;        // the installer
        String dbType = null;           // database type

        // get the installer defined through database type:
        dbType = dbConf.getDbType ();
        if (dbType.equalsIgnoreCase (SQLConstants.DB_MSSQL_STR))
        {
            installer = new SQLSExec ();
        } // if
        else if (dbType.equalsIgnoreCase (SQLConstants.DB_ORACLE_STR))
        {
            // currently not implemented
        } // else if
        else if (dbType.equalsIgnoreCase (SQLConstants.DB_DB2_STR))
        {
            installer = new Db2Exec ();
        } // else if

        // return the result:
        return installer;
    } // getInstaller


    /**************************************************************************
     * Check a file array if the included files are valid
     * and return the filesnames of the valid files an a string array. <BR/>
     *
     * @param files the File array to check
     * @param log   the log object
     *
     * @return a string array containing the name and path all valid files
     *
     */
    private static File [] getValidFiles (File [] files, Log_01 log)
    {
        // check the install files:
        int validFilesCount = 0;
        File [] resultFiles = null;
        String fileStateStr = null;
        boolean isDropFileEntry = false;

        for (int i = 0; i < files.length; i++)
        {
            // check if the file exists:
            if (files [i].exists () && files [i].isFile ())
            {
                fileStateStr = "found";
                validFilesCount++;
                isDropFileEntry = false;
            } // if
            else                        // file is not valid
            {
                fileStateStr = "is not valid!";
                isDropFileEntry = true;
            } // else file is not valid

            log.add (DIConstants.LOG_ENTRY, "Install file '" +
                files[i].getAbsolutePath () + "' " + fileStateStr);

            if (isDropFileEntry)
            {
                files [i] = null;
            } // if
        } // for i

        if (validFilesCount > 0)
        {
            resultFiles = new File [validFilesCount];
            int j = 0;
            for (int i = 0; i < files.length; i++)
            {
                if (files [i] != null)
                {
                    resultFiles [j++] = files [i];
                } // if (files [i] != null
            } // for (int i = 0; i < files.length; i++)
        } // if (validFilesCount > 0)
        return resultFiles;
    } // getValidFiles

} // class SQLInstaller
