/*
 * Class: IbsReorg_01.java
 */

// package:
package ibs.obj.reorg;

// imports:
import ibs.app.ApplicationContext;
import ibs.bo.BOHelpers;
import ibs.bo.BOPathConstants;
import ibs.bo.BusinessObjectInfo;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.bo.ObjectNotFoundException;
import ibs.bo.Operations;
import ibs.bo.SelectionList;
import ibs.bo.cache.ObjectPool;
import ibs.bo.type.Type;
import ibs.bo.type.TypeConstants;
import ibs.di.DIConstants;
import ibs.di.DataElement;
import ibs.di.DocumentTemplate_01;
import ibs.di.ValueDataElement;
import ibs.di.XMLViewer_01;
import ibs.di.service.DBMapper;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.servlet.ApplicationInitializationException;
import ibs.io.servlet.IApplicationContext;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.user.User_01;
import ibs.service.conf.IConfiguration;
import ibs.service.module.ConfVarContainer;
import ibs.service.module.Module;
import ibs.service.module.ModuleContainer;
import ibs.service.user.User;
import ibs.tech.html.IE302;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.StoredProcedure;
import ibs.tech.sql.StoredProcedureConstants;
import ibs.util.AlreadyDeletedException;
import ibs.util.DateTimeHelpers;
import ibs.util.NameAlreadyGivenException;
import ibs.util.NoAccessException;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;
import ibs.util.file.FileHelpers;
import ibs.util.file.FileManager;
import ibs.util.list.ListException;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * This class represents one object of type IbsReorg_0. <BR/>
 * The class handles the reorganisation function. <BR/>
 *
 * @version     $Id: IbsReorg_01.java,v 1.28 2012/10/18 11:56:24 btatzmann Exp $
 *
 * @author      Klaus Reimüller (KR), 040709
 ******************************************************************************
 */
public class IbsReorg_01 extends Reorg
{
    /**
     * Version info of the actual clas. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: IbsReorg_01.java,v 1.28 2012/10/18 11:56:24 btatzmann Exp $";


     /**
      * reorg function: reorg the file system.<BR/>
      */
    public static final String REORGFCT_FILESYSTEMREORG = "reorgFileSystem";

    /**
     * reorg function: repair objectref fields.<BR/>
     */
    public static final String REORGFCT_REPAIROBJECTREF = "repairObjectref";

    /**
     * argument: typenames for repairing objectref fields.<BR/>
     */
    public static final String ARG_REPAIROBJECTREF_TYPECODES =
        "repairObjectref_typecodes";

    /**
     * reorg function: set user workspace. <BR/>
     */
    public static final String REORGFCT_SETWORKSPACES = "setWorkspaces";
    
    /**
     * argument: user fullnames for repairing workspaces.<BR/>
     */
    public static final String ARG_SETWORKSPACES_USERS =
        "setWorkspaces_users";

    /**
     * reorg function: reload module content. <BR/>
     */
    public static final String REORGFCT_RELOADMODULES = "reloadModules";

    /**
     * reorg function: reload multilingual texts. <BR/>
     */
    public static final String REORGFCT_RELOADMULTILANGTEXTS = "reloadMultilangTexts";
    
    /**
     * argument: reload module content. <BR/>
     */
    public static final String ARG_RELOADMODULES_MODULES = "reloadModules_modules";

    /**
     * reorg function: refresh db mapping. <BR/>
     */
    public static final String REORGFCT_REFRESHDBMAPPING = "refreshDBMappings";

    /**
     * argument: refresh db mappings - typecode. <BR/>
     */
    public static final String ARG_REFRESHDBMAPPING_TYPECODES = "refreshDBMappings_typecodes";

    /**
     * reorg function: synchronise xml data files with db entrie. <BR/>
     *
     * @deprecated  KR 20090717 Because of getting rid of xmldata files this
     *              is not longer needed.
     */
    @Deprecated
    public static final String REORGFCT_SYNCXMLDATA = "syncXMLData";

    /**
     * argument: typecodes for synchronising xml data files with db entrie. <BR/>
     *
     * @deprecated  KR 20090717 Because of getting rid of xmldata files this
     *              is not longer needed.
     */
    @Deprecated
    public static final String ARG_SYNCXMLDATA_TYPECODES = "syncXMLData_typecodes";

    /**
     * argument: oids for synchronising xml data files with db entrie. <BR/>
     *
     * @deprecated  KR 20090717 Because of getting rid of xmldata files this
     *              is not longer needed.
     */
    @Deprecated
    public static final String ARG_SYNCXMLDATA_OIDS = "syncXMLData_oids";

    /**
     * reorg function: reorg the upload files folder.<BR/>
     */
    public static final String REORGFCT_UPLOADFILES = "reorgUploadFiles";

    /**
     * argument: remove XML data files. <BR/>
     */
    public static final String ARG_UPLOADFILES_REMOVEXMLDATAFILES = "reorgUploadFiles_removeXMLDataFiles";
    
    /**
     * argument: remove empty folders within upload/files. <BR/>
     */
    public static final String ARG_UPLOADFILES_REMOVEEMTPYFOLDERS = "reorgUploadFiles_removeEmptyFolders";

    /**
     * argument: set flag <code>BusinessObject.p_hasFile</code> for all objects. <BR/>
     */
    public static final String ARG_UPLOADFILES_SETHASFILEFLAGS = "reorgUploadFiles_setHasFileFlags";

    /**
     * field name: filesystem reorg log.<BR/>
     */
    public static final String FLD_FILEREORGLOG = "filesystem reorg log";

    /**
     * field name: workflow receivers.<BR/>
     */
    public static final String FLD_WFRECEIVERS = "WF Empfänger";

    /**
     * prefix of xml data file name
     */
    private static String XMLDATAFILE_NAME_PREFIX = "xmldata";

    /**
     * file extension for xml data files
     */
    private static String XMLDATAFILE_NAME_FILEEXTENSION = ".xml";


    /**************************************************************************
     * This constructor creates a new instance of the class ProcedureObject. <BR/>
     */
    public IbsReorg_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // IbsReorg_01


    /**************************************************************************
     * This constructor calls the corresponding constructor of the super class. <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     */
    public IbsReorg_01 (OID oid, User user)
    {
        // call constructor of super class:
        super ();

        // initialize properties common to all subclasses:
    } // IbsReorg_01


    /**************************************************************************
     * Perform reorganisatio. <BR/>
     *
     * @param   isSimulate  Shall the reorganisation be really performed or
     *                      just simulated?
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     * @exception   NameAlreadyGivenException
     *              An object with this name already exists. This exception is
     *              only raised by some specific object types which don't allow
     *              more than one object with the same name.
     */
    protected final void performReorg (boolean isSimulate)
        throws NoAccessException, NameAlreadyGivenException
    {

        // get the reorg function
        String reorgFct = this.env.getParam (ARG_REORGFCT);

        // check if we got a function
        if (reorgFct == null || reorgFct.length () == 0)
        {
            // no function to perform
            return;
        } // if (reorgFct == null || reorgFct.length() == 0)

        // shall the file system reorg be started?
        else if (reorgFct.equalsIgnoreCase (IbsReorg_01.REORGFCT_FILESYSTEMREORG))
        {
            this.startFileReorg (isSimulate);
        } // else if

        // check if OBJECTREF felder should be repaired
        else if (reorgFct.equalsIgnoreCase (IbsReorg_01.REORGFCT_REPAIROBJECTREF))
        {
            this.startRepairObjectrefs (
                isSimulate,
                this.env.getStringParam (IbsReorg_01.ARG_REPAIROBJECTREF_TYPECODES));
        } // else if

        // check if the workspaces shall be set for all users:
        else if (reorgFct.equalsIgnoreCase (IbsReorg_01.REORGFCT_SETWORKSPACES))
        {
            this.startSetWorkspaces (isSimulate,
                    this.env.getStringParam (IbsReorg_01.ARG_SETWORKSPACES_USERS));
        } //  else if
        
        // check if the module contents shall be reloaded:
        else if (reorgFct.equalsIgnoreCase (IbsReorg_01.REORGFCT_RELOADMODULES))
        {
            this.startReloadModuleContents (isSimulate,
                    this.env.getParam (IbsReorg_01.ARG_RELOADMODULES_MODULES));
        } //  else if

        // check if the db mapping shall be refreshed
        else if (reorgFct.equalsIgnoreCase (IbsReorg_01.REORGFCT_REFRESHDBMAPPING))
        {
            this.startRefreshDBMapping (isSimulate,
                    this.env.getParam (IbsReorg_01.ARG_REFRESHDBMAPPING_TYPECODES));
        } //  else if
        
        // check if the multilingual texts shall be reloaded
        else if (reorgFct.equalsIgnoreCase (IbsReorg_01.REORGFCT_RELOADMULTILANGTEXTS))
        {
            this.startReloadMultilingualTexts (isSimulate);
        } //  else if

        // check if the upload files reorganisation shall be performed
        else if (reorgFct.equalsIgnoreCase (IbsReorg_01.REORGFCT_UPLOADFILES))
        {
            this.startReorgUploadFiles (
                    this.env.getBoolParam (IbsReorg_01.ARG_UPLOADFILES_REMOVEXMLDATAFILES) == IOConstants.BOOLPARAM_TRUE,
                    this.env.getBoolParam (IbsReorg_01.ARG_UPLOADFILES_REMOVEEMTPYFOLDERS) == IOConstants.BOOLPARAM_TRUE,
                    this.env.getBoolParam (IbsReorg_01.ARG_UPLOADFILES_SETHASFILEFLAGS) == IOConstants.BOOLPARAM_TRUE,
                    isSimulate);
        } //  else if

        // check if the xmldata files shall be synchronized with the DB
        else if (reorgFct.equalsIgnoreCase (IbsReorg_01.REORGFCT_SYNCXMLDATA))
        {
            this.startSyncXMLData (isSimulate,
                    this.env.getParam (IbsReorg_01.ARG_SYNCXMLDATA_TYPECODES),
                    this.env.getParam (IbsReorg_01.ARG_SYNCXMLDATA_OIDS));
        } //  else if
        else    // unkown function
        {
            this.addError ("Unknown reorganisation function: " + reorgFct);
        } // else unkown function
    } // performReorg


    /**************************************************************************
     * Start the database reorganisation. <BR/>
     */
/*
    private void startDBReorg ()
    {

        addLog ("<DIV ALIGN=\"LEFT\">");
        addLog ("<FONT SIZE=\"2\">");

        int retVal = UtilConstants.QRY_OK;          // return value of query
        int i = 0;                                  // number of actual parameter
        Parameter [] params = new Parameter[1];    // contains the parameters

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        i = -1;                         // initialize parameter number

        // input parameters
        // maxRKey
        addInParameter (params, ++i, ParameterConstants.TYPE_INTEGER, 0);

        try
        {
            // perform the function call to get the statistic before the
            // reorganisation
            addLog ("Statistik vor der Reorganisation:" + IE302.TAG_NEWLINE);
            retVal = performCallFunctionData ("p_reorgGetStats", params, i);
showDebug ("" + retVal);

            // perform the function call that starts the reorganisation
            addLog ("Datenbank Reorganisation wird gestartet ..." + IE302.TAG_NEWLINE);
            retVal = performCallFunctionData ("p_reorg", params, i);
showDebug ("" + retVal);

            // perform the function call to get the statistic after the
            // reorganisation
            addLog ("Statistik nach der Reorganiation:" + IE302.TAG_NEWLINE);
            retVal = performCallFunctionData ("p_reorgGetStats", params, i);
showDebug ("" + retVal);
        } // try
        catch (NoAccessException e)
        {
            addLog (e.toString ());
        } // catch (NoAccessException e)

        // perform the function call that starts the reorganisation
        addLog ("Datenbank Reorganisation abgeschlossen!<P>");
        addLog ("</FONT>");
        addLog ("</DIV>");
    } // startDBReorg
*/


    /**************************************************************************
     * Start the filesystem reorganisatio. <BR/>
     * The reorganisation can be startet in simulation mode that does not
     * really delete the directorie. <BR/>
     *
     * @param isSimulation    turn the simulation mode on.
     */
    private void startFileReorg (boolean isSimulation)
    {
        File dir;
        String uploadFilesPath;
        String [] fileList;
        File file;
        OID dirOid;
        int dirDeleted = 0;
        int dirDeleteFailed = 0;
        int dirActive = 0;
        int dirNotValid = 0;
        int dirWasFile = 0;

        String title = "Filesystem reorganisation";
        this.showReorgHeader (isSimulation, title);

        // get the directory listing of the upload files directory
        uploadFilesPath = this.app.p_system.p_m2AbsBasePath +
                BOPathConstants.PATH_UPLOAD_ABS_FILES;

        this.addLog ("Reading directory '<CODE>" + uploadFilesPath + "</CODE>' ... ");

        dir = new File (uploadFilesPath);
        fileList = dir.list ();

        this.addLog (fileList.length + " directories found." + IE302.TAG_NEWLINE);

        // loop through the file list
        for (int i = 0; i < fileList.length; i++)
        {
            file = new File (uploadFilesPath + fileList [i]);

            this.addLogObj ("Verify: '<CODE>" + fileList [i] + "</CODE>' ... ");

            // check if the file found is a directory
            if (file.isDirectory ())
            {
                // check if it is an directory with an oid in its name
                try
                {
                    dirOid = new OID (fileList [i]);
                    if (this.existsObject (dirOid))
                    {
                        this.addLog ("is active." + IE302.TAG_NEWLINE);
                        dirActive++;
                    } // if (existsObject (dirOid))
                    else     // delete the directory
                    {
                        if (isSimulation)
                        {
                            this.addLog ("<B>can be deleted!</B>" +
                                IE302.TAG_NEWLINE);
                            dirDeleted++;
                        } // if (isSimulation)
                        else
                        // delete physically
                        {
                            // delete the directory and its content
                            if (FileHelpers.deleteDirRec (file.getPath ()))
                            {
                                this.addLog ("<B>has been deleted!</B>" +
                                    IE302.TAG_NEWLINE);
                                dirDeleted++;
                            } // if (file.delete())
                            else
                            // failed to delete
                            {
                                this.addError ("could not be deleted!" +
                                    IE302.TAG_NEWLINE);
                                dirDeleteFailed++;
                            } // failed to delete
                        } // else delete physically
                    } // delete the directory
                } // try
                catch (IncorrectOidException e)
                {
                    this.addError ("is no valid file directory!" + IE302.TAG_NEWLINE);
                    dirNotValid++;
                } // catch (IncorrectOidException e)
            } // if (file.isDirectory())
            else    // not a directory
            {
                this.addWarning ("is a file!" + IE302.TAG_NEWLINE);
                dirWasFile++;
            } // not a directory
        } // for (int i = 0; i < fileList.length; i++)

        if (isSimulation)
        {
            this.addLog ("<B>Summary</B>:" + IE302.TAG_NEWLINE);
            this.addLog ("<LI>Number directories, which could have been deleted: " + dirDeleted + IE302.TAG_NEWLINE);
            this.addLog ("<LI>Number directories, which are still in use: " + dirActive + IE302.TAG_NEWLINE);
            this.addLog ("<LI>Number directories, which are a file: " + dirWasFile + IE302.TAG_NEWLINE);
            this.addLog ("<LI>Number directories, which are not valid object file directory: " + dirNotValid);
        } // if (isSimulation)
        else    // not a simulation
        {
            this.addLog ("<B>Statistic</B>:" + IE302.TAG_NEWLINE);
            this.addLog ("<LI>Number directories, which could have been deleted: " + dirDeleted + IE302.TAG_NEWLINE);
            this.addLog ("<LI>Number directories, which are still in use: " + dirActive + IE302.TAG_NEWLINE);
            this.addLog ("<LI>Number directories, which could not haven been deleted: " + dirDeleteFailed + IE302.TAG_NEWLINE);
            this.addLog ("<LI>Number directories, which are a file: " + dirWasFile + IE302.TAG_NEWLINE);
            this.addLog ("<LI>Number directories, which are not valid object file directory: " + dirNotValid);
            // set the statistic info
            this.dataElement.changeValue (IbsReorg_01.FLD_FILEREORGLOG , "Last file system reorganisation on " +
                DateTimeHelpers.dateTimeToString (this.p_startDate) + "\n" +
                "Number directories, which have been deleted successfully: " + dirDeleted + "\n" +
                "Number directories, which were in use: " + dirActive + "\n" +
                "Number directories, which could not have been deleted: " + dirDeleteFailed + "\n" +
                "Number directories, which were a file: " + dirWasFile + "\n" +
                "Number directories, which were no valid object file directory: " + dirNotValid);
        } // not a simulation

        this.showReorgFooter (isSimulation, title, fileList.length);
    } // startFileReorg


    /**************************************************************************
     * Start the upload files reorganisation. <BR/>
     *
     * @param removeXmlDataFiles    defines if xml data file removal should be performed
     * @param removeEmptyFolders    defines if removal of empty folders should be performed
     * @param setHasFileFlag        defines if hasFile flag should be set for object
     * @param isSimulation          defines if simulation mode is active
     */
    private void startReorgUploadFiles (boolean removeXmlDataFiles, boolean removeEmptyFolders, boolean setHasFileFlag,
            boolean isSimulation)
    {
        File dir;
        String uploadFilesPath;
        String [] fileList;
        File folder;

        int xmlDataFilesDeleted = 0;
        int dirDeleted = 0;
        int dirWasFile = 0;

        // [0] ... number of files where no flag was set
        // [1] ... number of files where the flag was set on the folder oid
        // [2] ... number of found xml data files
        // [3] ... number of found xml data files were the flag was set on the oid found within ibs_attachment_01
        // [4] ... total number of processed files
        int[] hasFileCounters = new int [] {0,0,0,0,0};

        String title = "Upload Files reorganisation";
        this.showReorgHeader (isSimulation, title);

        // get the directory listing of the upload files directory
        uploadFilesPath = this.app.p_system.p_m2AbsBasePath +
                BOPathConstants.PATH_UPLOAD_ABS_FILES;

        this.addLog ("Reading directory '<CODE>" + uploadFilesPath + "</CODE>' ... ");

        dir = new File (uploadFilesPath);
        fileList = dir.list ();

        this.addLog (fileList.length + " directories found." + IE302.TAG_NEWLINE);

        // loop through the file list
        for (int i = 0; i < fileList.length; i++)
        {
            int folderNr = i + 1;

            // check if a new 100 series is started
            if ((folderNr) % 100 == 1)
            {
                this.addLogObj (IE302.TAG_NEWLINE + "Processing folders " +
                        folderNr + ".." +
                            // check if the current 100 series ends exceeds the total number of folders
                            (((folderNr + 99) < fileList.length) ? (folderNr + 99) : fileList.length));
            } // if
           
            folder = new File (uploadFilesPath + fileList [i]);

            this.addLog (IE302.TAG_NEWLINE + IE302.TAG_BOLDBEGIN + "Processing folder " + folder + ": " + IE302.TAG_BOLDEND + IE302.TAG_NEWLINE);

            // check if the file found is a directory
            if (folder.isDirectory ())
            {               
                if (removeXmlDataFiles)
                {
                    xmlDataFilesDeleted = performRemoveXmlDataFiles (folder, xmlDataFilesDeleted, isSimulation);
                } // if
                
                if (removeEmptyFolders)
                {
                    if (performDeleteFileFolders (folder, isSimulation))
                    {
                        dirDeleted++;
                    } // if
                } // if
                
                if (setHasFileFlag)
                {
                    hasFileCounters = performSetHasFileFlag (folder, hasFileCounters, isSimulation);
                } // if
                
            } // if (file.isDirectory())
            else    // not a directory
            {
                this.addWarning (folder.getAbsolutePath () + " is a file!" + IE302.TAG_NEWLINE);
                dirWasFile++;
            } // not a directory
        } // for (int i = 0; i < fileList.length; i++)

        if (isSimulation)
        {
            this.addLog (IE302.TAG_NEWLINE + "<B>Summary</B>:" + IE302.TAG_NEWLINE);
            this.addLog ("<LI>Number directories, which are a file: " + dirWasFile + IE302.TAG_NEWLINE);
            
            if (removeXmlDataFiles)
            {
                this.addLog ("<LI><B>XML data files:</B> Number XML Data Files, which could have been deleted: " + xmlDataFilesDeleted + IE302.TAG_NEWLINE);
            } // if
            
            if (removeEmptyFolders)
            {
                this.addLog ("<LI><B>Empty folders:</B>Number directories, which could have been deleted: " + dirDeleted + IE302.TAG_NEWLINE);
            } // if

            if (setHasFileFlag)
            {
                this.addLog ("<LI><B>hasFile:</B> Number of processed upload files: " + hasFileCounters [4] + IE302.TAG_NEWLINE);
            } // if
        } // if (isSimulation)
        else    // not a simulation
        {
            this.addLog (IE302.TAG_NEWLINE + "<B>Statistic</B>:" + IE302.TAG_NEWLINE);
            this.addLog ("<LI>Number directories, which are a file: " + dirWasFile + IE302.TAG_NEWLINE);
            
            if (removeXmlDataFiles)
            {
                this.addLog ("<LI><B>XML data files:</B> Number XML Data Files, which have been deleted: " + xmlDataFilesDeleted + IE302.TAG_NEWLINE);
            } // if

            if (removeEmptyFolders)
            {
                this.addLog ("<LI><B>Empty folders:</B> Number directories, which have been deleted: " + dirDeleted + IE302.TAG_NEWLINE);
            } // if

            if (setHasFileFlag)
            {
                this.addLog ("<LI><B>hasFile:</B> Number of files where no flag was set: " + hasFileCounters [0] + IE302.TAG_NEWLINE);
                this.addLog ("<LI><B>hasFile:</B> Number of files where the flag was set for the object represented by the folder's oid: " + hasFileCounters [1] + IE302.TAG_NEWLINE);
                this.addLog ("<LI><B>hasFile:</B> Number of files were the flag was set on the oid found within ibs_attachment_01: " + hasFileCounters [3] + IE302.TAG_NEWLINE);
                this.addLog ("<LI><B>hasFile:</B> Number of found xml data files: " + hasFileCounters [2] + IE302.TAG_NEWLINE);
                this.addLog ("<LI><B>hasFile:</B> Number of processed upload files: " + hasFileCounters [4] + IE302.TAG_NEWLINE);
            } // if
        } // not a simulation

        this.showReorgFooter (isSimulation, title, fileList.length);
    } // startFileReorg


    /***************************************************************************
     * Removes the xml data file from the provided folder.
     *
     * @param folder                the folder to check for an xml data file
     * @param xmlDataFilesDeleted   the number of xml data files, which have
     *                              already been deleted
     * @param isSimulation          if simulation mode is active
     * 
     * @return the incremented counter provided within param xmlDataFilesDeleted
     */
    private int performRemoveXmlDataFiles (File folder, int xmlDataFilesDeleted, boolean isSimulation)
    {       
        // retrieve the xml data file for the provided folder
        File[] files = FileHelpers.getFilesArray (folder,
                FileManager.getFilter ("*"),
                FileManager.getFilter (XMLDATAFILE_NAME_PREFIX + "*"),
                true);
        
        for (int i = 0; i < files.length; i++)
        {
            if (isXmlDataFile (files [i].getName ()))
            {
                if (!isSimulation)
                {
                    if (files [i].delete ())
                    {
                        xmlDataFilesDeleted++;

                        this.addLog ("... xml data file " + files [i].getAbsolutePath () + " removed" + IE302.TAG_NEWLINE);
                    } // if
                } // if
                else
                {
                    xmlDataFilesDeleted++;

                    this.addLog ("... xml data file " + files [i].getAbsolutePath () + " found" + IE302.TAG_NEWLINE);
                } // else
            } // if
        } // for
        
        return xmlDataFilesDeleted;
    } // performRemoveXmlDataFiles


    /***************************************************************************
     * Deletes the provided folder if the folder is empty.
     *
     * @param folder        the root folder to check for files
     * @param isSimulation  if simulation mode is active
     * 
     * @return   if the folder has been deleted
     */
    private boolean performDeleteFileFolders (File folder, boolean isSimulation)
    {        
        String [] files = FileHelpers.getFilesArray (folder.getAbsolutePath ());
        
        if (files.length == 0)
        {
            if (!isSimulation)
            {
                FileHelpers.deleteDir (folder.getAbsolutePath ());

                this.addLog ("... folder " + folder.getAbsolutePath () + " deleted" + IE302.TAG_NEWLINE);
            } // if
            else
            {
                this.addLog ("... folder " + folder.getAbsolutePath () + " is empty" + IE302.TAG_NEWLINE);
            } // else
            
            return true;
        } // if
        else
        {
            return false;
        } // else
    } // performDeleteFileFolders


    /***************************************************************************
     * Sets the <code>BusinessObject.hasFile</code> flag for all files found within
     * the provided folder.
     *
     * @param folder        the root folder to check for files
     * @param counters      an integer array with counters:
     *                      [0] ... number of files where no flag was set
     *                      [1] ... number of files where the flag was set on the folder oid
     *                      [2] ... number of found xml data files
     *                      [3] ... number of found xml data files were the flag was set on the oid found within ibs_attachment_01
     *                      [4] ... total number of processed files
     * @param isSimulation  if simulation mode is active
     * 
     * @return the incremented counters
     */
    private int[] performSetHasFileFlag (File folder, int[] counters, boolean isSimulation)
    {       
        File[] files = FileHelpers.getFilesArray (folder,
                FileManager.getFilter ("*"),
                FileManager.getFilter ("*"),
                true);
               
        for (int i = 0; i < files.length; i++)
        {
            StoredProcedure sp = new StoredProcedure (
                "p_setHasFile", StoredProcedureConstants.RETURN_VALUE);

            try
            {
                this.addLog ("... processing file " + files [i].getAbsolutePath () + " ");
                
                OID folderOid = new OID (files [i].getParentFile ().getName ());
                
                String path = "/" + files [i].getParentFile ().getAbsolutePath ().
                    // replace the file path with the web path
                    replace (this.app.p_system.p_m2AbsBasePath, this.app.p_system.p_m2AbsBaseDir.getName ()+ File.separator).
                    // prepare the path for the database
                    replace (File.separator, "/") + "/";
                
                String fileName = files [i].getName ();
                
                if (isXmlDataFile (fileName))
                {
                    this.addLog ("==> file is xml data file; no flag is set." + IE302.TAG_NEWLINE);
                    counters [2]++;
                } // if
                else
                {
                    // parameter definitions:
                    // must be in right sequence (like SQL stored procedure def.)
        
                    // oid:
                    BOHelpers.addInParameter (sp, folderOid);
                    // name:
                    sp.addInParameter (ParameterConstants.TYPE_STRING, path);
                    // isUser:
                    sp.addInParameter (ParameterConstants.TYPE_STRING, fileName);
        
                    try
                    {
                        if (!isSimulation)
                        {
                            // perform the function call:
                            int retVal = BOHelpers.performCallFunctionData (sp, this.env);
                            
                            switch (retVal)
                            {
                                case 0:
                                    this.addWarning ("No flag set for file: " + files [i].getAbsolutePath ());
                                    counters [0]++;
                                    break;
                                case 1:
                                    this.addLog ("==> flag set for object with oid=" + folderOid);
                                    counters [1]++;
                                    break;
                                case 3:
                                    this.addLog ("==> flag set for the oid found within ibs_Attachment_01");
                                    counters [3]++;
                                    break;
                            } // switch
                        } // if

                        this.addLog (IE302.TAG_NEWLINE);

                        counters [4]++;
                    } // try
                    catch (NoAccessException e)
                    {
                        this.addError (e.getLocalizedMessage ());
                    } // catch
                } // else
            } // try
            catch (IncorrectOidException e1)
            {
                this.addError (e1.getLocalizedMessage ());
            } // catch
        } // for
        
        return counters;
    } // performSetHasFileFlag


    /***************************************************************************
     * Returns if the file with the provided fileName is an xml data file.
     *
     * @param fileName  the filename
     * @return  if the file with the provided fileName is an xml data file
     */
    private boolean isXmlDataFile (String fileName)
    {
        return (fileName.startsWith (XMLDATAFILE_NAME_PREFIX + "_") && fileName.endsWith (XMLDATAFILE_NAME_FILEEXTENSION)) ||
            fileName.equals (XMLDATAFILE_NAME_PREFIX + XMLDATAFILE_NAME_FILEEXTENSION);
    } // isXmlDataFile


    /**************************************************************************
     * Start repairing objectref field. <BR/>
     * The repair can be startet in simulation mode that does not
     * change any condition. <BR/>
     *
     * @param isSimulation  turn the simulation mode on.
     * @param typeCodes     the typecodes of the form objects to rewrite
     *                      multiple entries possible but must be comma separated
     *
     */
    private void startRepairObjectrefs (boolean isSimulation, String typeCodes)
    {
        XMLViewer_01 xmlviewer = new XMLViewer_01 ();
        String tVersionIdsClause = "";
        ValueDataElement value;
        String oldValue = "";
        String searchObjectRef;
        Vector<BusinessObjectInfo> objects = null;
        BusinessObjectInfo objInfo = null;

        String title = "Repair OBJECTREF-fields";
        this.showReorgHeader (isSimulation, title);

        // construct a query clause from the tversionids
        tVersionIdsClause = this.getTVersionIdsClause (typeCodes);
        // did we get a valid typenames clause for the query?
        if (tVersionIdsClause == null)
        {
            this.addLog (IE302.TAG_NEWLINE + "Canceling process!");
            this.showReorgFooter (isSimulation, title);
            return;
        } // if (tVersionIdsClause == null)

        this.addLog (">>> Searching objects ...");
        objects = this.getObjects (tVersionIdsClause, null, null);

        // empty resultset?
        if (objects != null && objects.size () > 0)
        {
            this.addLog (objects.size () + " object(s) found.");

            // loop through the objects:
            for (Iterator<BusinessObjectInfo> iter = objects.iterator (); iter.hasNext ();)
            {
                objInfo = iter.next ();

                // show object link
                this.addLogObj (
                    objInfo.p_oid.toString (), objInfo.p_name,
                    objInfo.p_typeName);

                // get the object
                xmlviewer = (XMLViewer_01) objInfo.getObject (this.env);

                // check if we got the object:
                if (xmlviewer != null) // got the object?
                {
                    // now loop through the fields to get the objectref fields
                    for (int i = 0; i < xmlviewer.dataElement.values.size (); i++)
                    {
                        value = xmlviewer.dataElement.values.elementAt (i);
                        // did we get an OBJECTREF fields?
                        if (value.type.equalsIgnoreCase (DIConstants.VTYPE_OBJECTREF))
                        {
                            // objectref found
                            this.addLog (IE302.TAG_NEWLINE + "OBJECTREF field: <CODE>" +
                                "'" + value.field + "' = ");
                            //
                            if (value.value != null && value.value.length () > 18)
                            {
                                this.addLog ("'<A HREF=\"" +
                                    IOHelpers.getShowObjectJavaScriptUrl (
                                        value.value.substring (0, 18)) +
                                    "\"><CODE>" +
                                    value.value + "</CODE></A>'");
                            } // if (value.value != null && value.value.length() > 18)
                            else // empty value
                            {
                                this.addLog ("'" + value.value + "'");
                            } // else empty value
                            this.addLog (IE302.TAG_NEWLINE + "TYPECODEFILTER='" + value.typeFilter + "'" +
                                " - SEARCHROOT='" + value.searchRoot + "'" +
                                " - SEARCHROOTIDDOMAIN='" + value.searchRootIdDomain + "'" +
                                " - SEARCHROOTID='" + value.searchRootId + "'" +
                                " - SEARCHRECURSIVE='" + value.searchRecursive + "'</CODE>");
                            // remember the old value
                            oldValue = value.value;

                            // check if an objectref has been set
                            if (value.value != null && (!value.value.isEmpty ()))
                            {
                                // is the value too short?
                                if (value.value.length () < 18)
                                {
                                    this.addError ("Value invalid! Deleting Objektref.");
                                    value.value = "";
                                } // if (value.value.equals (","))
                                else    // obviously
                                {
                                    // try to find an object with the given name
                                    // and the objectref settings
                                    searchObjectRef = this.searchObject (
                                        xmlviewer,
                                        value.value.substring (19),
                                        value.searchRoot,
                                        value.searchRootIdDomain,
                                        value.searchRootId,
                                        DataElement.resolveBooleanValue (
                                            value.searchRecursive),
                                        value.typeFilter);
                                    // object found?
                                    if (searchObjectRef != null)
                                    {
                                        // did we find an exact match?
                                        if (value.value.equals (searchObjectRef))
                                        {
                                            this.addLog (IE302.TAG_NEWLINE + "Referenced object found.");
                                        } // if (value.value.equals (searchObjectRef))
                                        else // if (value.value.equals (searchObjectRef))
                                        {
                                            this.addError (
                                                "Object found but OIDs do not match!");
                                            this.addLog (IE302.TAG_NEWLINE + "<B>New value</B>: " +
                                                "<A HREF=\"" +
                                                IOHelpers.getShowObjectJavaScriptUrl (
                                                    searchObjectRef.substring (0, 18)) +
                                                "\">" +
                                                "'<CODE>" + searchObjectRef + "</CODE>'</A>!");
                                            value.value = searchObjectRef;
                                        } // else // if (value.value.equals (searchObjectRef))
                                    } // if (searchObjectRef != null)
                                    else    // object not found
                                    {
                                        // try to seach by oid
                                        this.addLog (IE302.TAG_NEWLINE + "Object not found via name " +
                                            "and OBJECTREF settings.");
                                        // try to find an object with the given oid
                                        // and the objectref settings
                                        try
                                        {
                                            this.addLog (IE302.TAG_NEWLINE + "Searching object " +
                                                "via OID and OBJECTREF settings...");
                                            searchObjectRef = this.searchObject (
                                                xmlviewer,
                                                new OID (value.value.substring (0, 18)),
                                                value.searchRoot,
                                                value.searchRootIdDomain,
                                                value.searchRootId,
                                                DataElement.resolveBooleanValue (
                                                    value.searchRecursive),
                                                value.typeFilter);
                                            if (searchObjectRef != null)
                                            {
                                                this.addError ("Object found but names do not match!");
                                                this.addLog (IE302.TAG_NEWLINE + "<B>New value</B>: " +
                                                    "<A HREF=\"" +
                                                    IOHelpers.getShowObjectJavaScriptUrl (
                                                        searchObjectRef.substring (0, 18)) +
                                                    "\">" +
                                                    "'<CODE>" + searchObjectRef + "</CODE>'</A>!");
                                                value.value = searchObjectRef;
                                            } // if (searchObjectRef != null)
                                            else // object not found
                                            {
                                                this.addError ("referenced object" +
                                                    " not found!" +
                                                    " Object will be deleted.");
                                                value.value = "";
                                            } // else object not found
                                        } // try
                                        catch (IncorrectOidException e)
                                        {
                                            this.addError (e.toString ());
                                        } // catch (IncorrectOidException e)
                                    } // else object not found
                                } // if (value.value != null && (!value.value.equals ("")))

                                // any changes to the value?
                                if (!value.value.equals (oldValue))
                                {
                                    try
                                    {
                                        this.addLog (IE302.TAG_NEWLINE + "Setting new value: " +
                                            "<CODE>'" + value.value + "'</CODE> ... ");
                                        // do we need to change the object?
                                        if (!isSimulation)
                                        {
                                            xmlviewer.performChange (Operations.OP_NONE);
                                            this.addLog ("Object changed.");
                                        } // if (! isSimulation)
                                    } // try
                                    catch (NoAccessException e)
                                    {
                                        this.addError (e.toString () +
                                            " Object could not be edited.");
                                    } // catch (NoAccessException e)
                                    catch (NameAlreadyGivenException e)
                                    {
                                        this.addError (e.toString () +
                                            " Object could not be edited.");
                                    } // catch (NameAlreadyGivenException e)
                                } // if (value.value.equals (oldValue))
                                else    // no change
                                {
                                    this.addLog (IE302.TAG_NEWLINE + "no changes.");
                                } // else
                            } // if (value.value != null && (!value.value.equals ("")))
                            else
                            {
                                this.addLog (IE302.TAG_NEWLINE + "no object assigned.");
                            } // else
                        } // if (value.type.equalsIgnoreCase (DIConstants.VTYPE_OBJECTREF))
                    } // for (int i = 0; i < xmlviewer.dataElement.values.size(); i++)
                } // if got the object
                else    // could not get the object
                {
                    this.addError ("Could not read object!");
                } // else could not get the object
            } // for (Iterator<String[]> iter = objects.iterator(); iter.hasNext ();)
        } // if (rowCount > 0)
        else // no data found
        {
            this.addLog ("<LI><B>Remarkt</B>:No forms found!");
        } // else no data found

        this.showReorgFooter (isSimulation, title);
    } // startRepairObjectrefs


    /**************************************************************************
     * Set the workspaces for all users. <BR/>
     * The reorganisation can be startet in simulation mode that does not
     * really add the workspaces. <BR/>
     *
     * @param   isSimulation    Turn the simulation mode on.
     */
    private void startSetWorkspaces (boolean isSimulation, String userFullnames)
    {
        Vector<String[]> users = new Vector<String[]> ();   // a vector with all users
        OID userOid;                    // the oid of the actual user
        User_01 user;                   // the actual user object
        OID[] wspTemplateOids = null;   // the oids of the workspace templates
        int i = 0;                      // loop counter

        if (isSimulation)
        {
            this.addLog (">>> Simulation of setting workspace not supported." + IE302.TAG_NEWLINE);
            return;
        } // if

        String title = "Setting workspace";
        this.showReorgHeader (isSimulation, title);

        this.addLog (">>> Identifying available workspaces ..." + IE302.TAG_NEWLINE);

        // get the oids of the workspace templates:
        SelectionList wspSelList = this.getWorkspaceSelectionList ();
        try
        {
            wspTemplateOids = OID.stringArrayToOid (wspSelList.ids);

            for (i = 0; i < wspSelList.ids.length; i++)
            {
                // show object link
                this.addLogObj (wspSelList.ids[i], wspSelList.values[i], null);
            } // for i

            // get the users:
            this.addLog (IE302.TAG_NEWLINE);
            this.addLog (">>> Identifying users ..." + IE302.TAG_NEWLINE);

            String[] userList;
            
            // check the user filter:
            if (userFullnames == null || userFullnames.isEmpty () || // no filter defined
                    userFullnames.equals ("*")) // wildcard defined 
            {
                userList = null; // process all users 
            } // if
            else // users defined
            {
                userList = StringHelpers.stringToStringArray (userFullnames, ';');
            } // else
            
            // retrieve the users for the given filter
            users = this.createUsersVector (null, userList);

            // loop through all users and set the workspaces for each of them:
            this.addLog (">>> Settings workspaces for users ..." + IE302.TAG_NEWLINE);
            i = 0;
            for (Iterator<String[]> iter = users.iterator (); iter.hasNext ();)
            {
                String[] elem = iter.next ();
                ++i;

                // show object link
                this.addLogObj (elem[1], elem[0], null);

                try
                {
                    // get the user oid:
                    userOid = new OID (elem[1]);

                    // get the user object instance:
                    user = (User_01)
                        BOHelpers.getObject (userOid, this.env, false, false);

                    if (user != null)   // found user?
                    {
                        // set the workspace template oids:
                        user.setWorkspaceTemplateOids (wspTemplateOids);
                        // ensure that the workspace objects are created:
                        user.showAllowed = false;
                        user.change (UtilConstants.REP_STANDARD);
                    } // if found user
                    else
                    {
                        this.addError ("User not existing!");
                    } // else
                } // try
                catch (IncorrectOidException e)
                {
                    // nothing to do, just ignore the oid
                    this.addError (e.toString ());
                } // catch
            } // for iter

            this.showReorgFooter (isSimulation, title);
        } // try
        catch (IncorrectOidException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch

    } // startSetWorkspaces


    /**************************************************************************
     * Reload modules contents of the given modules.<BR/>
     *
     * @param isSimulation  turn the simulation mode on.
     * @param modules        a comma separated list of module names to be reloaded
     */
    private void startReloadModuleContents (boolean isSimulation, String modules)
    {
        boolean isReloadNeccessary = false;
        Module module;
        String moduleId;

        String title = "Loading module contents";
        this.showReorgHeader (isSimulation, title);

        try
        {
            // get the module container
            ModuleContainer moduleContainer = ((ObjectPool) this.app.cache).getModuleContainer ();
            ConfVarContainer confVars = ((IConfiguration) this.app.configuration).getConfVars ();

            // any modules found?
            if (modules == null || modules.isEmpty ())
            {
                this.addError ("No modules set.");
                this.addLog (IE302.TAG_NEWLINE + "Cancelling process.");
            } // if (modules == null || modules.trim().length() == 0)
            else if (modules.equals (ALL))
            {
                if (!isSimulation)
                {
                    this.addLog ("Loading all modules ...");
                    // loop through all alements and check the constraints for each one:
                    for (Iterator<Module> iter = moduleContainer.iterator (); iter.hasNext ();)
                    {
                        module = iter.next ();

                        this.addLogObj ("Loading module '<code>" + module.toString () + "</code>' ... ");
                        // resolve the dependencies for the actual element:
                        if (module.loadContent (confVars, true, Module.LOAD_EXCLUDE_RB, false))
                        {
                            isReloadNeccessary = true;
                        } // if (module.loadContent (confVars, true))
                    } // for (Iterator<Module> iter = moduleContainer.iterator(); iter.hasNext ();)
                    this.addLog (IE302.TAG_NEWLINE + "All modules successfully loaded");
                } // if (! isSimulation)
                else    // simulation mode
                {
                    // display all modules
                    this.addLog ("The following can be loaded:");
                    for (Iterator<Module> iter = moduleContainer.iterator (); iter.hasNext ();)
                    {
                        module = iter.next ();
                        this.addLogObj ("Module: '" + module.getId () +
                                   "' Version: " + module.getVersion ());
                    } // for (Iterator<Module> iter = moduleContainer.iterator(); iter.hasNext ();)
                } // else    // simulation mode
            } // if (modules.equals (ALL))
            else    // some separate modules have been set
            {
                String [] moduleArray = StringHelpers.stringToStringArray (modules, LIST_SEPARATOR);
                // loop through the modules
                for (int i = 0; i < moduleArray.length; i++)
                {
                    this.addLogObj ("Loading module '<code>" + moduleArray[i] + "</code>' ... ");
                    // prepare the module id
                    moduleId = moduleArray[i].trim ().toLowerCase ();

                    // does the module exist?
                    if (moduleContainer.get (moduleId) != null)
                    {
                        if (!isSimulation)
                        {
                            // load the module
                            if (moduleContainer.loadContent (confVars, true, moduleId))
                            {
                                isReloadNeccessary = true;
                            } // if (moduleContainer.loadContent (confVars, true, moduleArray[i].trim()))
                            this.addLog ("successfully loaded.");
                        } // if (! isSimulation)
                        else    // simulation mode
                        {
                            this.addLog ("can be loaded.");
                        } // else simulation mode
                    } // if (moduleContainer.find (moduleArray[i]))
                    else    // module not known
                    {
                        this.addError ("Module unkown!");
                    } // else    // module not known
                } // for (int i = 0; i < moduleArray.length; i++)
            } // else    // some separate modules have been set

            this.showReorgFooter (isSimulation, title);

            // must the application be restartet?
            if (isReloadNeccessary)
            {
                // ensure that the application cannot be restarted without
                // shutting down the server:
                this.app.p_restartPossible = false;

                this.addLog (IE302.TAG_NEWLINE + IE302.TAG_NEWLINE +
                        "Some necessary resources have been changed during" +
                        " initialization. Please reload the application" +
                        " context or restart the web server now.");
            } // if (isReloadNeccessary)
        } // try
        catch (ListException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
    } // startReloadModuleContents
    
    
    /**************************************************************************
     * Reload the multilingual texts.<BR/>
     *
     * @param isSimulation  turn the simulation mode on.
     */
    private void startReloadMultilingualTexts (boolean isSimulation)
    {
        this.addLog ("Reloading multilingual texts ..." + IE302.TAG_NEWLINE + IE302.TAG_NEWLINE);
        
        this.addLog (">>>Resetting resouce bundle cache" + IE302.TAG_NEWLINE + IE302.TAG_NEWLINE);
        
        // Reset the multilingual text cache
        if (!isSimulation)
        {            
            MultilingualTextProvider.resetMultilingualTextCache (this.env);
            this.addLog ("Resource Bundle cache has been reset" + IE302.TAG_NEWLINE + IE302.TAG_NEWLINE);
        } // if
        else
        {
            this.addLog ("Resource Bundle can be reset" + IE302.TAG_NEWLINE + IE302.TAG_NEWLINE);            
        } // else
        
        this.addLog (">>>Reloading Resource Bundles" + IE302.TAG_NEWLINE + IE302.TAG_NEWLINE);

        //  get the module container
        ModuleContainer moduleContainer = ((ObjectPool) this.app.cache).getModuleContainer ();
        ConfVarContainer confVars = ((IConfiguration) this.app.configuration).getConfVars ();
        
        try
        {
            if (!isSimulation)
            {
                this.addLog ("Loading resource bundles for all modules...");
                moduleContainer.loadContent (confVars, false, Module.LOAD_ONLY_RB);
                this.addLog (IE302.TAG_NEWLINE + "Resource bundles for all module successfully reloaded");
            } // if (! isSimulation)
            else    // simulation mode
            {
                this.addLog ("Resource bundles for the following modules can be reloaded");
            } // else    // simulation mode
        } // try
        catch (ListException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
        
        this.addLog (">>>Perform preloading operations" + IE302.TAG_NEWLINE + IE302.TAG_NEWLINE);
        
        if (!isSimulation)
        {
            try
            {
                // Instantiate the application context
                IApplicationContext appCtx = new ApplicationContext (this.app, this.sess, this.env);
                
                // Reload multilang info for types
                this.addLog ("Start reloading multilang info for types" + IE302.TAG_NEWLINE + IE302.TAG_NEWLINE);                
                this.app.p_appInitializer.reloadTypes (appCtx, true);
                this.addLog ("Finished reloading multilang info for types" + IE302.TAG_NEWLINE + IE302.TAG_NEWLINE);

                // Reload multilang info for queries
                this.addLog ("Start reloading multilang info for queries" + IE302.TAG_NEWLINE + IE302.TAG_NEWLINE);                
                this.app.p_appInitializer.reloadQueries (appCtx, true);
                this.addLog ("Finished reloading multilang info for queries" + IE302.TAG_NEWLINE + IE302.TAG_NEWLINE);
                
                // Reload multilang info for client
                this.addLog ("Start reloading multilang info for client and preloaded resource bundles" +
                        IE302.TAG_NEWLINE + IE302.TAG_NEWLINE);
                this.app.p_appInitializer.reloadPreloadedMliTexts (appCtx);                
                this.addLog ("Finished reloading multilang info for client" + IE302.TAG_NEWLINE + IE302.TAG_NEWLINE);

                // reload the multilang info on client for the current user
                MultilingualTextProvider.reloadMultilangClientInfo (this.env);

            } // try
            catch (ApplicationInitializationException e)
            {
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            } // catch
        } // if
        else
        {
            this.addLog ("Multilang info for types can be reloaded" + IE302.TAG_NEWLINE + IE302.TAG_NEWLINE);
            this.addLog ("Multilang info for queries can be reloaded" + IE302.TAG_NEWLINE + IE302.TAG_NEWLINE);
            this.addLog ("Multilang info for client and preloaded resource bundles can be reloaded" +
                    IE302.TAG_NEWLINE + IE302.TAG_NEWLINE);
        } // else
        
        this.addLog ("Reloading multilingual texts finished." + IE302.TAG_NEWLINE + IE302.TAG_NEWLINE);
    } // startReloadMultilingualTexts


    /**************************************************************************
     * Start refreshing DBMapping stored procedures and tabl. <BR/>
     *
     * @param isSimulation  turn the simulation mode on.
     * @param typeCodes     the typecodes of the objecttypes to be refreshed
     */
// PROB TODO KR 20090824 only recreating of stored procedures is necessary.
    private void startRefreshDBMapping (boolean isSimulation,
                                        String typeCodes)
    {
        String [] typeCodeArray;
        String typeCode;
        Type type;
        DocumentTemplate_01 template;
        Vector<Type> dbMapperTypes = new Vector<Type> ();
        DataElement dataElement;

        String title = "Renew DBMapping";
        this.showReorgHeader (isSimulation, title);

        // contraint: any typecode must be set
        if (typeCodes == null || typeCodes.isEmpty ())
        {
            this.addLog ("No typecodes set.");
            this.showReorgFooter (isSimulation, title);
            return;
        } // if (typeCodes == null || typeCodes.length() == 0)
        else if (typeCodes.equals (ALL))
        {
            this.addLog (IE302.TAG_NEWLINE + "Renew all object types ...");

            // only add those types that have a dbmapping
            for (Iterator<Type> iter = this.getTypeCache ().iterator ();
                iter.hasNext ();)
            {
                type = iter.next ();
                this.addLog (IE302.TAG_NEWLINE + "Verify type '<CODE>" +
                        type.getCode () + "</CODE>' ... ");

                // check if the type has a template
                template = (DocumentTemplate_01) type.getTemplate ();
                if (template != null)
                {
                    dbMapperTypes.addElement (type);
                    this.addLog ("DBMapping activated.");
                } // if (template != null)
                else // template not found
                {
                    this.addLog ("No template found. Objecttyp is ignored.");
                } // else template not found
            } // for (Iterator<Type> iter = this.getTypeCache().iterator(); iter.hasNext ();)
        } // else if (typeCodes.equals(ALL))
        else    // typecodes set
        {
            typeCodeArray = StringHelpers.stringToStringArray (typeCodes, LIST_SEPARATOR);
            for (int i = 0; i < typeCodeArray.length; i++)
            {
                typeCode = typeCodeArray[i].trim ();
                this.addLog (IE302.TAG_NEWLINE + "Verify type '<CODE>" +
                        typeCode + "</CODE>' ... ");

                // check if the typecode exists
                type = this.getTypeCache ().find (typeCode);
                // did we found the type?
                if (type != null)
                {
                    // get the template
                    template = (DocumentTemplate_01) type.getTemplate ();
                    // template found?
                    if (template != null)
                    {
                        this.addLog ("Typecode found. ");
                        dbMapperTypes.addElement (type);
                        this.addLog ("DBMapping activated.");
                    } // if (template != null)
                    else // template not found
                    {
                        this.addLog ("No template found! Objecttype is ignored.");
                    } // else template not found
                } // if (type != null)
                else    // type not found
                {
                    this.addError ("Typecode is invalid! Objecttype is ignored.");
                } // else type not found
            } //  for (int i = 0; i < typeCodeArray.length; i++)
        } // else    // typecodes set

        this.addLog (IE302.TAG_NEWLINE + "Verification finished." +
                IE302.TAG_NEWLINE + IE302.TAG_NEWLINE);

        // any db-mappable type found?
        if (dbMapperTypes.size () > 0)
        {
            // get the dbmapper object
            DBMapper mapper = new DBMapper (this.user, this.env, this.sess, this.app);
            // loop through the typecodes
            for (Iterator<Type> iter = dbMapperTypes.iterator (); iter.hasNext ();)
            {
                type = iter.next ();

                this.addLogObj ("Processing typecode '<CODE>" + type.getCode () + "</CODE>' ... ");

                // simulation mode?
                if (!isSimulation)
                {
                    // get the dataElement from the template
                    dataElement = ((DocumentTemplate_01) type.getTemplate ())
                        .getTemplateDataElement ();

                    // Check if a dataElement exist and is NOT NULL
                    if (dataElement != null)
                    {
                        // an oid must be set in the dataElement
                        // otherwise the createAllDBEntries () will produce an error
                    	dataElement.oid = this.oid;

/* DEL KR 20090828 This is not longer necessary since the data is only existing within the table.
	                    if (isRefreshTable)
	                    {
	                        this.addLog (IE302.TAG_NEWLINE + "DBMapper Tabelle und Stored Procedures werden gelöscht ...");
	                        // delete the mapping tables and stored procedures
	                        if (mapper.deleteFormTemplateDBTable (dataElement, true))
	                        {
	                            this.addLog ("erfolgreich gelöscht.");
	                        } // if (mapper.deleteFormTemplateDBTable (dataElement, true))
	                        else    // error when deleting stored procedures and table
	                        {
	                            this.addError ("DBMapper Tabelle und Stored Procedures konnten nicht gelöscht werden!");
	                        } // else error when deleting stored procedures and table
	
	                        this.addLog (IE302.TAG_NEWLINE + "DBMapper Tabelle und Stored Procedures werden neu erstellt ...");
	                        // create the dbmapping stored procedures
	                        if (mapper.createFormTemplateDBTable (dataElement, true))
	                        {
	                            this.addLog ("erfolgreich erstellt.");
	                            this.addLog (IE302.TAG_NEWLINE + "DBMapper Tabelle wird mit Daten befüllt ...");
	                            // refresh the complete dbmapper table
	                            if (mapper.createAllDBEntries (dataElement, false))
	                            {
	                                this.addLog (IE302.TAG_NEWLINE + "DBMapper Tabelle mit Daten erfolgreich befüllt.");
	                            } // if (mapper.createAllDBEntries (template.getTemplateDataElement(), false))
	                            else    // error when creating db entries
	                            {
	                                this.addError ("DBMapper Tabelle konnte nicht befüllt werden!");
	                            } // else error when creating db entries
	                        } // if (mapper.createFormTemplateDBTable (template.getTemplateDataElement(), true))
	                        else    // error when deleting creating procedures and table
	                        {
	                            this.addError ("DBMapper Tabelle und Stored Procedures konnten nicht generiert werden!");
	                        } // else    // error when creating stored procedures and table
	                    } // if (isRefreshTable)
	                    else    // no refresh table
	                    {
*/
	                    this.addLog (IE302.TAG_NEWLINE + "Deleting DBMapper stored procedures ...");
	                    // delete the mapping tables and stored procedures
	                    if (mapper.deleteFormTemplateDBTable (dataElement, false))
	                    {
	                        this.addLog ("successfully deleted.");
	                    } // if (mapper.deleteFormTemplateDBTable (template.getTemplateDataElement(), false))
	                    else    // error when deleting stored procedures
	                    {
	                        this.addError ("Could not deleted DBMapper stored procedures!");
	                    } // else error when deleting stored procedures
	
	                    this.addLog (IE302.TAG_NEWLINE + "Recreating DBMapper stored procedures ...");
	                    // create the dbmapping stored procedures
	                    if (mapper.createFormTemplateDBTable (dataElement, false))
	                    {
	                        this.addLog ("successfully created.");
	                    } // if (mapper.createFormTemplateDBTable (template.getTemplateDataElement(), false))
	                    else    // error when creating stored procedures
	                    {
	                        this.addError ("Could not generate DBMapper stored procedures!");
	                    } // else error when creating stored procedures
                    }
                    else
                    {
                        this.addError ("DataElement is NULL for " + type.getCode());
                    } // Check if a dataElement exist and is NOT NULL

                    /* DEL KR 20090828 This is not longer necessary since the data is only existing within the table.
	                    } // no refresh table
*/
                } // if (!isSimulation)
                else    // simulation mode
                {
                    this.addLog ("DBMapping can be rebuild");
                } // else simulation mode
            } // for (Iterator<DocumentTemplate_01> iter = dbMapperTypes.iterator(); iter.hasNext ();)
            this.addLog (IE302.TAG_NEWLINE + "Creating DBMappings finished.");

        } // if (dbMapperTypes.size() > 0)
        else    // no dbmapable types found
        {
            this.addWarning ("No object types with activated DB Mapping found!");
        } // else no dbmapable types found

        this.showReorgFooter (isSimulation, title);
    } // startRefreshDBMapping


    /**************************************************************************
     * Start refreshing DBMapping stored procedures and tabl. <BR/>
     *
     * @param isSimulation  turn the simulation mode on.
     * @param typeCodes     the typecodes of the objecttypes to be refreshed
     * @param objOids       Oids of the relevant objects.
     *
     * @deprecated  KR 20090823 Because of getting rid of xmldata files this
     *              is not longer needed.
     */
    @Deprecated
    private void startSyncXMLData (boolean isSimulation,
                                   String typeCodes,
                                   String objOids)
    {
        String tVersionIdClause = null;
        String oidClause = null;
        Vector <BusinessObjectInfo> objects = null;
        XMLViewer_01 xmlviewer = null;


        String title = "Synchronizing forms with data from db";
        this.showReorgHeader (isSimulation, title);

        // create the sql query clause for the typeCodes
        tVersionIdClause = this.getTVersionIdsClause (typeCodes);

        // create the query sql clause for the OIDs
        oidClause = this.getOIDsClause (objOids);

        // Constraint: typeCodes must be set or any objOids. if not: abort
        if (tVersionIdClause == null && oidClause == null)
        {
            this.addError ("Valid typecodes or OIDs have to be defined.");
            this.addLog (IE302.TAG_NEWLINE + "Cancelling process.");
            this.showReorgFooter (isSimulation, title);
            return;
        } // if (tVersionIdClause == null && (objOids == null || objOids.length() == 0)

        this.addLog (IE302.TAG_NEWLINE + IE302.TAG_NEWLINE +
                ">>> Searching objects ...");
        // get the objects
        objects = this.getObjects (tVersionIdClause, oidClause, null);

        // empty resultset?
        if (objects != null && objects.size () > 0)
        {
            this.addLog (objects.size () + " Object(s) found.");

            // get the oids
            for (Iterator<BusinessObjectInfo> iter = objects.iterator (); iter.hasNext ();)
            {
                BusinessObjectInfo objInfo = iter.next ();

                // show object link
                this.addLogObj (
                    objInfo.p_oid.toString (), objInfo.p_name,
                    objInfo.p_typeName);

                // get the object
                // Note that we read the object with the getParameters
                // option activated. This is neccessary because
                // some objects set parameter reminders in the getParamters
                // method in order to compare them within a performChange
                // and recognize a change.
                // If the option is deactivated these objects will not be able
                // anymore to recognize a true change because the former
                // values are always NULL.
                xmlviewer = (XMLViewer_01) objInfo.getObject (this.env);

                // check if we got the object:
                if (xmlviewer != null) // got the object?
                {
                    // simulation mode?
                    if (!isSimulation)
                    {
                        try
                        {
                            // perform a retrieve from the db
                            xmlviewer.retrieve (Operations.OP_NONE);
                            // now write back the data to the DB:
                            xmlviewer.performChange (Operations.OP_NONE);
                            // thats it!
                            this.addLog ("erfolgreich synchronisiert.");
                        } // try
                        catch (NoAccessException e)
                        {
                            this.addError (e.toString ());

                        } // catch (NoAccessException e)
                        catch (ObjectNotFoundException e)
                        {
                            this.addError (e.toString ());

                        } // catch (ObjectNotFoundException e)
                        catch (AlreadyDeletedException e)
                        {
                            this.addError (e.toString ());
                        } // catch (AlreadyDeletedException e)
                        catch (NameAlreadyGivenException e)
                        {
                            this.addError (e.toString ());
                        } // catch (NameAlreadyGivenException e)
                    } // if (!isSimulation)
                    else    // simulation mode
                    {
                        this.addLog ("can be synchronised.");
                    } // else simulation mode
                } // if got the object
                else    // could not get the object
                {
                    this.addError ("could not be read!");
                } // else could not get the object
            } // for (Iterator<String[]> iter = objects.iterator(); iter.hasNext ();)
        } // if (rowCount > 0)
        else    // no objects found
        {
            this.addLog (IE302.TAG_NEWLINE + "No objects found!");
        } // else no objects found

        this.showReorgFooter (isSimulation, title);
    } // startSyncXMLData


    /**************************************************************************
     * Get selection list of all available workspace templates. <BR/>
     *
     * @return  A selection list with the workspace templates.
     */
    private SelectionList getWorkspaceSelectionList ()
    {
        // get the workspace template selection list and return the result:
        return this.performRetrieveSelectionListData (
            this.getTypeCache ().getTVersionId (TypeConstants.TC_WorkspaceTemplate), false);
    } // getWorkspaceSelectionList

} // class IbsReorg_01
