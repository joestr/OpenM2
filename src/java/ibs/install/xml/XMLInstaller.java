/*
 * Class: XMLInstaller.java
 */

// package:
package ibs.install.xml;

// imports:
import ibs.BaseObject;
import ibs.app.func.FunctionValues;
import ibs.bo.BOHelpers;
import ibs.bo.OID;
import ibs.di.DIConstants;
import ibs.di.DataElement;
import ibs.di.KeyMapper;
import ibs.di.Log_01;
import ibs.di.imp.ImportIntegrator;
import ibs.di.imp.ImportScript_01;
import ibs.io.servlet.ApplicationInitializationException;
import ibs.util.file.FileHelpers;
import ibs.util.file.FileManager;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


/******************************************************************************
 * This class is used to install new or update existing xml data. <BR/>
 *
 * @version     $Id: XMLInstaller.java,v 1.6 2009/09/04 20:13:04 kreimueller Exp $
 *
 * @author      Klaus, 27.08.2005
 ******************************************************************************
 */
public abstract class XMLInstaller extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: XMLInstaller.java,v 1.6 2009/09/04 20:13:04 kreimueller Exp $";


    /**
     * Attribute name: source file name. <BR/>
     */
    private static final String ATTR_SOURCEFILE = "sourcefile";

    /**
     * Attribute name: destination path. <BR/>
     */
    private static final String ATTR_DESTINATIONPATH = "destinationpath";

    /**
     * Attribute name: destination path. <BR/>
     */
    private static final String ATTR_DESTPATH = "destpath";

    /**
     * Attribute name: extkey id of destination object. <BR/>
     */
    private static final String ATTR_DESTID = "destid";

    /**
     * Attribute name: extkey iddomain of destination object. <BR/>
     */
    private static final String ATTR_DESTIDDOMAIN = "destiddomain";

    /**
     * Attribute name: shalle the system variables be replaced in the import
     * files. <BR/>
     */
    private static final String ATTR_REPLACESYSVARS = "replacesysvars";



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
        String destPath = null;         // destination path within application
        String destId = null;           // extkey id of destination container
        String destIddomain = null;     // extkey iddomain of dest. container
        boolean isReplaceSysVars = false; // shall the system variables be
                                        // replaced?

        // get the attributes from the node:
        attributes = installNode.getAttributes ();
        if (attributes != null)
        {
            // get the sourcefile:
            if ((attribute = attributes.getNamedItem (XMLInstaller.ATTR_SOURCEFILE)) != null)
            {
                sourcePath = attribute.getNodeValue ();
            } // if

            // get the destination path:
            if ((attribute = attributes.getNamedItem (XMLInstaller.ATTR_DESTINATIONPATH)) != null)
            {
                destPath = attribute.getNodeValue ();
            } // if

            // get the destination path, new syntax:
            if ((attribute = attributes.getNamedItem (XMLInstaller.ATTR_DESTPATH)) != null)
            {
                destPath = attribute.getNodeValue ();
            } // if

            // get the destination id:
            if ((attribute = attributes.getNamedItem (XMLInstaller.ATTR_DESTID)) != null)
            {
                destId = attribute.getNodeValue ();
            } // if

            // get the destination id domain:
            if ((attribute = attributes.getNamedItem (XMLInstaller.ATTR_DESTIDDOMAIN)) != null)
            {
                destIddomain = attribute.getNodeValue ();
            } // if

            // get the value of the isReplaceSysVars attribute:
            if ((attribute = attributes.getNamedItem (XMLInstaller.ATTR_REPLACESYSVARS)) != null)
            {
                isReplaceSysVars =
                    DataElement.resolveBooleanValue (attribute.getNodeValue ());
            } // if
        } // if (attributes != null)

        // perform the installation:
        XMLInstaller.install (installDir, sourcePath, destPath, destId, destIddomain,
            isReplaceSysVars, values, log);
    } // install


    /**************************************************************************
     * Perform the installation. <BR/>
     *
     * @param   installDir  The installation directory.
     * @param   sourcePath  Path where to find the files.
     * @param   destPath    Path of destination container within application.
     * @param   destId      Extkey id of destination container.
     * @param   destIddomain Extkey iddomain of destination container.
     * @param   isReplaceSysVars Shall the system variables in the files be
     *                      replaced?
     * @param   values      The values for the current function.
     * @param   log         The log.
     *
     * @throws  ApplicationInitializationException
     *          There occurred an error during the installation process.
     */
    public static void install (File installDir, String sourcePath,
                                String destPath, String destId,
                                String destIddomain, boolean isReplaceSysVars,
                                FunctionValues values, Log_01 log)
        throws ApplicationInitializationException
    {
        File sourceDir;                 // source directory
        File sourceFile = null;         // source file
        String sourceFileName = null;   // name (and path) of source file
        File[] installFiles = null;     // the files to be installed
        String [] validInstallFiles = null; // paths of all valid install files
        OID destinationOid = null;      // oid of actual destination containers
        Vector<OID> destinationOids = null; // oids of destination containers
        ImportIntegrator importIntegrator;
        ImportScript_01 importScript;

        // display the settings:
        log.add (DIConstants.LOG_ENTRY, XMLInstaller.ATTR_SOURCEFILE + "='" +
            sourcePath + "'");
        log.add (DIConstants.LOG_ENTRY, XMLInstaller.ATTR_DESTPATH + "='" +
            destPath + "'");
        log.add (DIConstants.LOG_ENTRY, XMLInstaller.ATTR_DESTID + "='" +
            destId + "'");
        log.add (DIConstants.LOG_ENTRY, XMLInstaller.ATTR_DESTIDDOMAIN + "='" +
            destIddomain + "'");
        log.add (DIConstants.LOG_ENTRY, XMLInstaller.ATTR_REPLACESYSVARS + "='" +
            isReplaceSysVars + "'");

        // check constraints for settings:
        if (sourcePath == null || sourcePath.length () == 0)
        {
            log.add (DIConstants.LOG_ERROR,
                "Source file not defined. Install entry ignored.");
        } // if
        else if ((destPath == null || destPath.length () == 0) &&
                 (destId == null || destId.length () == 0 ||
                  destIddomain == null || destIddomain.length () == 0))
        {
            log.add (DIConstants.LOG_ERROR,
                "Destination path and destination id not defined. " +
                "Install entry ignored.");
        } // else if
        else                            // both source and destination defined
        {
            // add the source file name to the install directory and
            // divide the result into directory and file name:
            sourceFile = new File (installDir, FileHelpers
                .makeFileNameValid (sourcePath));
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
                validInstallFiles = XMLInstaller.getValidFiles (installFiles, log);

                // do we finally have any valid installation files:
                if (validInstallFiles != null && validInstallFiles.length > 0)
                {
                    // create an import integrator:
                    importIntegrator = new ImportIntegrator ();
                    importIntegrator.initObject (OID.getEmptyOid (), values.getUser (),
                        values.p_env, values.p_sess, values.p_app);
                    // enable/disable name type key mapping:
                    importIntegrator.setIsNameTypeMapping (false);
                    // enable/disable replacing of system variables:
                    importIntegrator.setIsReplaceSysVars (isReplaceSysVars);

                    // create an importscript with a global operation:
                    importScript = new ImportScript_01 ();
                    importScript.initObject (OID.getEmptyOid (), values.getUser (),
                        values.p_env, values.p_sess, values.p_app);
                    importScript.setGlobalOperation (DIConstants.OPERATION_CHANGE);
                    importScript.name = "--- OPERATION=CHANGE ---";
                    // and set it in the import integrator:
                    importIntegrator.setImportScript (importScript);

                    // resolve the destination path:
                    destinationOids = XMLInstaller.getDestinationContainers (destPath, destId,
                        destIddomain, values, importIntegrator);

                    if (destinationOids != null)
                    {
                        // loop through all entries and perform an import
                        // for each destination:
                        for (Iterator<OID> iter = destinationOids.iterator ();
                             iter.hasNext ();)
                        {
                            // get the actual destination oid:
                            destinationOid = iter.next ();

                            // set the destination path as container id:
                            importIntegrator.setContainerId (destinationOid);
                            importIntegrator.startImport (validInstallFiles,
                                sourceDir.getPath (), log);
                        } // for iter
                    } // if (destinationOid != null)
                    else // destination path not valid
                    {
                        log.add (DIConstants.LOG_ERROR,
                            "Destinationpath '" + destPath +
                            "' is not valid! Install entry ignored!");
                    } // destination path not valid
                } // if (validFilesCount == 0)
            } // else source directory valid
        } // else both source and destination defined
    } // install


    /**************************************************************************
     * Get the oids of the containers into which to perform the installation.
     * <BR/>
     * These can be several containers because of destination paths with
     * placeholders resulting in multiple different containers.
     *
     * @param   destPath    Path of destination container within application.
     * @param   destId      Extkey id of destination container.
     * @param   destIddomain Extkey iddomain of destination container.
     * @param   values      The values for the current function.
     * @param   importIntegrator The import integrator used for resolving the
     *                      object path.
     *
     * @return  The oids of the destination containers,
     *          <CODE>null</CODE> if the containers could not be resolved.
     */
    private static Vector<OID> getDestinationContainers (String destPath,
                                                    String destId,
                                                    String destIddomain,
                                                    FunctionValues values,
                                                    ImportIntegrator importIntegrator)
    {
        OID destinationOid = null;      // oid of destination container
        Vector<OID> destinationOids = null; // list of oids of all destination
                                        // containers

        if (destPath != null && destPath.length () > 0)
        {
            // resolve the destination path which can return multiple objects:
            destinationOids = BOHelpers.resolveMultipleObjectPath (destPath,
                importIntegrator, values.p_env);
        } // if
        else
        {
            // resolve the ext key:
            // get key mapper:
            KeyMapper keyMapper = new KeyMapper (values.getUser (),
                values.p_env, values.p_sess, values.p_app);
            // resolve the key mapping:
            destinationOid = keyMapper.performResolveMapping (
                new KeyMapper.ExternalKey (destIddomain, destId));

            // check if we found the destination oid:
            if (destinationOid != null)
            {
                // create a new vector containing only the destination oid:
                destinationOids = new Vector<OID> ();
                destinationOids.add (destinationOid);
            } // if
        } // else

        // return the oids:
        return destinationOids;
    } // getDestinationContainer


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
    private static String [] getValidFiles (File [] files, Log_01 log)
    {
        // check the import files
        int validFilesCount = 0;
        String [] resultFiles = null;
        String fileFoundStr = null;
        boolean isDropFileEntry = false;

        for (int i = 0; i < files.length; i++)
        {
            // check if the file exists
            if (files [i].exists () && files [i].isFile ())
            {
                fileFoundStr = "found.";
                validFilesCount++;
                isDropFileEntry = false;
            } // if (importFiles [i].isFile())
            else    // file is not valid
            {
                fileFoundStr = "is not valid!";
                isDropFileEntry = true;
            } // else file is not valid
            log.add (DIConstants.LOG_ENTRY,  "Importfile '" +
                files[i].getAbsolutePath () + "' " + fileFoundStr);
            if (isDropFileEntry)
            {
                files [i] = null;
            } // if
        } // for (int j = 0; j < importFiles.length; i++)

        if (validFilesCount > 0)
        {
            resultFiles = new String [validFilesCount];
            int j = 0;
            for (int i = 0; i < files.length; i++)
            {
                if (files [i] != null)
                {
                    resultFiles [j++] = files [i].getName ();
                } // if (files [i] != null
            } // for (int i = 0; i < files.length; i++)
        } // if (validFilesCount > 0)
        return resultFiles;
    } // getValidFiles

} // class XMLInstaller
