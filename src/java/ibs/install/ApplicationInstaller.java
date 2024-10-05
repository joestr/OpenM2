/*
 * Class: ApplicationInstaller.java
 */

// package:

package ibs.install;

// imports:
import ibs.BaseObject;
import ibs.app.func.FunctionValues;
import ibs.bo.BOPathConstants;
import ibs.bo.OID;
import ibs.bo.cache.ObjectPool;
import ibs.di.DIConstants;
import ibs.di.DIErrorHandler;
import ibs.di.Log_01;
import ibs.install.sql.SQLInstaller;
import ibs.install.xml.XMLInstaller;
import ibs.io.IOHelpers;
import ibs.io.servlet.ApplicationInitializationException;
import ibs.service.conf.IConfiguration;
import ibs.service.module.ConfVarContainer;
import ibs.service.module.Module;
import ibs.service.module.ModuleContainer;
import ibs.tech.html.IE302;
import ibs.tech.xml.XMLReader;
import ibs.tech.xml.XMLReaderException;
import ibs.util.DateTimeHelpers;
import ibs.util.file.FileHelpers;
import ibs.util.list.ListException;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/*******************************************************************************
 * This class is used to install new or update existing data. <BR/>
 *
 * @version $Id: ApplicationInstaller.java,v 1.1 2005/08/04 18:34:44 klreimue
 *          Exp $
 *
 * @author Klaus, 09.01.2004
 *         *****************************************************************************
 */
public abstract class ApplicationInstaller extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag to
     * ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ApplicationInstaller.java,v 1.8 2010/03/26 12:53:17 btatzmann Exp $";

    /**
     * File with install directives. <BR/>
     */
    private static final String INSTALLFILENAME = "install.xml";

    /**
     * Filename for the installation log. <BR/>
     */
    private static final String INSTALLLOGFILENAME = "install-log";

    /**
     * Tag name: install package. <BR/>
     */
    private static final String TAG_INSTPKG = "instpkg";

    /**
     * Tag name: install xml files. <BR/>
     */
    private static final String TAG_INSTXML = "instxml";

    /**
     * Tag name: install sql files. <BR/>
     */
    private static final String TAG_INSTSQL = "instsql";

    /**
     * Tag name: import xml files. <BR/>
     *
     * @deprecated This tag is deprecated and shall not be used. Please use
     *             {@link #TAG_INSTXML TAG_INSTXML} instead.
     */
    private static final String TAG_IMPORTXML = "import";

    /**
     * Attribute name: source file name. <BR/>
     */
    private static final String ATTR_SOURCEFILE = "sourcefile";

    /**
     * All valid tags. <BR/>
     */
    private static final String[] VALIDTAGS =
    {
        ApplicationInstaller.TAG_INSTPKG,
        ApplicationInstaller.TAG_INSTSQL,
        ApplicationInstaller.TAG_INSTXML,
        ApplicationInstaller.TAG_IMPORTXML,
    };


    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////

    /***************************************************************************
     * Perform the installation. <BR/>
     *
     * @param   installDir  The directory where to start the installation.
     * @param   values      The values for the current function.
     * @param   moduleId    The ID of a module that should be loaded first
     *
     * @throws  ApplicationInitializationException
     *          There occurred an error during the installation process.
     */
    public static void install (File installDir,
                                FunctionValues values,
                                String moduleId)
        throws ApplicationInitializationException
    {

        // check if a moduleId has beens set
        if (moduleId != null && moduleId.length () > 0)
        {
            values.p_env.write ("<P/><DIV ALIGN=\"LEFT\"><FONT SIZE=\"2\">");
            values.p_env.write ("Searching for module with ID <code>" +
                    moduleId + "</code> ... ");

            // get the module container
            ModuleContainer moduleContainer =
                ((ObjectPool) values.p_app.cache).getModuleContainer ();
            // does the module exist?
            try
            {
                Module module = moduleContainer.get (moduleId);
                // does the module exist?
                if (module != null)
                {
                    values.p_env.write ("found!" + IE302.TAG_NEWLINE);
                    values.p_env.write (
                        "Loading module <code>" + module.getIdVersion () + "</code> ... ");

                    // get the confvars
                    ConfVarContainer confVars =
                        ((IConfiguration) values.p_app.configuration).getConfVars ();
                    // load the module and check if basic ressources have been changed
                    if (moduleContainer.loadContent (confVars, true, module))
                    {
                        // ensure that the application cannot be restarted without
                        // shutting down the server:
                        values.p_app.p_restartPossible = false;

                        values.p_env.write (IE302.TAG_NEWLINE + IE302.TAG_NEWLINE +
                                "Some necessary resources have been changed during" +
                                " initialization. Please reload the application" +
                                " context or restart the web server now.");
                        return;
                    } // if (moduleContainer.loadContent (confVars, true, moduleArray[i].trim ()))

                    // could not load the module
                    values.p_env.write ("loaded!");
                    values.p_env.write ("</FONT></DIV><P/>");
                } // if (moduleContainer.find (moduleArray[i]))
                else    // module not known
                {
                    values.p_env.write ("ERROR: not found!" + IE302.TAG_NEWLINE);
                    values.p_env.write ("</FONT></DIV><P/>");
                    throw new ApplicationInitializationException (
                            "install: module '" + moduleId + "' unknown!");
                } // else module not known
            } // try
            catch (ListException e)
            {
                values.p_env.write ("</FONT></DIV><P/>");
                throw new ApplicationInitializationException (
                        "install: error in installer: " + e.toString ());
            } // catch (ListException e)
        } // if (moduleName != null && moduleName.length () > 0)

        // call common method:
        ApplicationInstaller.install (installDir, new File (installDir,
            FileHelpers
                .makeFileNameValid (ApplicationInstaller.INSTALLFILENAME)),
            values, null, new Date ());
    } // install


    /***************************************************************************
     * Perform the installation. <BR/>
     *
     * @param   installDir  The directory where to start the installation.
     * @param   installFile The installation file.
     * @param   values      The values for the current function.
     * @param   log         The log.
     * @param   startTime   The starting time of the installation process.
     *
     * @throws  ApplicationInitializationException
     *          There occurred an error during the installation process.
     */
    public static void install (File installDir, File installFile,
                                FunctionValues values, Log_01 log,
                                Date startTime)
        throws ApplicationInitializationException
    {
        // check if the directory exists:
        if (!installDir.exists () || !installDir.isDirectory ())
                                        // installation directory does not exist?
        {
            // in case the install directory does not exist yet
            // try to load the module and the specific update directory
            if (!ApplicationInstaller.loadInstallFiles (installDir, values))
            {
                throw new ApplicationInitializationException (
                    "Installation directory " + installDir + " is not valid!");
            } // if (!loadInstallFiles (installFile))
        } // if installation directory does not exist

        // check if the installation file exists:
        if (!installFile.exists () || !installFile.isFile ())
                                        // installation file does not exist?
        {
            throw new ApplicationInitializationException ("Installation file " +
                installFile + " does not exist.");
        } // else if installation file does not exist

        // process the installation file:
        ApplicationInstaller.processInstallFile (installDir, installFile,
            values, log, startTime);
    } // install


    /***************************************************************************
     * Initialize the log. <BR/>
     *
     * @param   values      The values for the current function.
     * @param   logDir      The log directory.
     * @param   logFileName The file name of the log.
     *
     * @return  The log which was opened.
     */
    private static Log_01 openLog (FunctionValues values,
                                   File logDir, String logFileName)
    {
        Log_01 log;
        Date startTime = new Date ();

        // init the log:
        log = new Log_01 ();
        log.initObject (OID.getEmptyOid (), values.getUser (), values.p_env,
            values.p_sess, values.p_app);
        log.isWriteLog = true;
        log.isDisplayLog = true;
        log.isGenerateHtml = true;
        log.setPath (logDir.getAbsolutePath ());
        log.setFileName (logFileName);

        values.p_env.write ("<P/><DIV ALIGN=\"LEFT\"><FONT SIZE=\"2\">");

        // init the log:
        if (!log.initLog ())
        {
            log.add (DIConstants.LOG_WARNING,
                "the log file can not be created: " + log.getPath () +
                    log.getFileName ());
        } // if (! log.initLog ())
        else
        // log initialized
        {
            // display log info:
            log.add (DIConstants.LOG_ENTRY,
                "The log file will be written to: " + log.getPath () +
                    log.getFileName ());
        } // else log initialized

        // add starting info to the log:
        log.add (DIConstants.LOG_ENTRY, ">>> Installation started at " +
            DateTimeHelpers.dateTimeToString (startTime));

        // return the log object:
        return log;
    } // openLog


    /***************************************************************************
     * Close the log. <BR/>
     *
     * @param   log     The log to be closed.
     * @param   values  The values for the current function.
     */
    private static void closeLog (Log_01 log, FunctionValues values)
    {
        Date endTime = null;

        log.add (DIConstants.LOG_ENTRY, ">>> Installation finished at " +
            DateTimeHelpers.dateTimeToString (endTime));
        if (log.isErrorfree ())
        {
            log.add (DIConstants.LOG_ENTRY, "No errors or warnings reported.");
        } // if
        else
        // any errors or warnings occurred
        {
            if (log.hasErrors ())
            {
                log.add (DIConstants.LOG_ENTRY, "Note that" + " " +
                    log.getErrorsCount () + " error(s) occurred!");
            } // if
            if (log.hasWarnings ())
            {
                log.add (DIConstants.LOG_ENTRY, "Note that " +
                    log.getWarningsCount () + " warnings occurred!");
            } // if
        } // else any errors or warnings occurred

        // close the log:
        log.closeLog ();

        values.p_env.write ("</FONT></DIV><P/>");
        /*
         * IOHelpers.showPopupMessage ("Installation finished!\\n" + "The log
         * file can be found at: " + log.getPath () + log.getFileName (), null,
         * values.p_app, values.p_sess, values.p_env);
         */
    } // closeLog


    /***************************************************************************
     * Parse the installation document. <BR/>
     *
     * @param   installDir  The installation directory.
     * @param   installFile The file to be installed.
     * @param   values      The values for the current function.
     * @param   log         The log.
     * @param   startTime   The starting time of the installation process.
     *
     * @throws  ApplicationInitializationException
     *          There occurred an error during the installation process.
     */
    private static void processInstallFile (File installDir, File installFile,
                                            FunctionValues values, Log_01 log,
                                            Date startTime)
        throws ApplicationInitializationException
    {
        Date startTimeLocal = startTime; // variable for local assignments
        Log_01 logLocal = log;          // variable for local assignments
        DIErrorHandler errorHandler = null; // the error listener
        Element rootElem;               // the root element of the document
        NodeList origInstallNodes;
        Vector<Node> installNodes;
        int installNodesLength = 0;
        Node installNode;
        String nodeName = null;         // the name of the actual install node
        boolean isLogOpened = false;    // was the log opened within this
                                        // method?
        Date endTime = null;
        Date installStartTime = null;
        long minutes = 0;
        long seconds = 0;
        int i = 0;                      // loop counter

        // init the start time:
        if (startTimeLocal == null)
        {
            startTimeLocal = new Date ();
        } // if

        // init the log:
        if (logLocal == null) // no log defined?
        {
            // set the logDir. Note that install logs will be written into the
            // logs/install directory.
            // BB20070912: former solution was logDir = installDir;
            // Note this is a bad solution because the importlogs get
            // deleted everytime a new package is installed and the install
            // directory gets emptied every time an installation is done.
            String logPath =
                values.p_app.p_system.p_m2AbsBasePath +
                BOPathConstants.PATHN_LOGS + File.separator +
                BOPathConstants.PATHN_INSTALL + File.separator;

            // ensure that the directory exists
            File logDir = new File (logPath);
            if (!logDir.exists ())
            {
                // create the log directory
                logDir.mkdir ();
            } // if (! logDir.exists ())

            // set the log file name that contains the package name
            String logFileName = ApplicationInstaller.INSTALLLOGFILENAME + "_" +
                installDir.getName ();
            // open a new log:
            logLocal = ApplicationInstaller.openLog (values, logDir, logFileName);
            isLogOpened = true;
        } // if no log defined

        // add first log entry:
        logLocal.add (DIConstants.LOG_ENTRY, "Using installation file: " +
            installFile);

        // instantiate error listener:
        errorHandler = new DIErrorHandler ();
        errorHandler.setEnv (values.p_env);
        errorHandler.sess = values.p_sess;

        try
        {
            // read the file with the installation instructions:
            rootElem = new XMLReader (installFile, true, errorHandler)
                .getRootElem ();

            // get the installNodes:
            origInstallNodes = rootElem.getChildNodes ();
            installNodes = ApplicationInstaller
                .getValidInstallNodes (origInstallNodes);

            // any install nodes found?
            if (installNodes != null)
            {
                installNodesLength = installNodes.size ();
                logLocal.add (DIConstants.LOG_ENTRY, installNodesLength +
                    " install entries found");
                i = 0;

                // loop through the install entries and perform corresponding
                // operations:
                for (Iterator<Node> iter = installNodes.iterator (); iter.hasNext ();)
                {
                    // set new loop counter:
                    i++;
                    // get the install node:
                    installNode = iter.next ();
                    nodeName = installNode.getNodeName ();

                    // add entry for the install node:
                    installStartTime = new Date ();
                    logLocal.add (DIConstants.LOG_ENTRY,
                        ">>> Process install entry " + i + "/" +
                            installNodesLength + ": " + nodeName);

                    // check the node name and perform the corresponding
                    // operation:
                    if (nodeName.equals (ApplicationInstaller.TAG_INSTPKG))
                    {
                        ApplicationInstaller.installPkg (installDir,
                            installNode, values, logLocal, startTimeLocal);
                    } // if TAG_INSTPKG
                    else if (nodeName.equals (ApplicationInstaller.TAG_INSTXML))
                    {
                        XMLInstaller.install (installDir, installNode, values,
                            logLocal);
                    } // else if TAG_INSTXML
                    else if (nodeName.equals (ApplicationInstaller.TAG_INSTSQL))
                    {
                        SQLInstaller.install (installDir, installNode, values,
                            logLocal);
                    } // else if TAG_INSTSQL
                    else if (nodeName.equals (ApplicationInstaller.TAG_IMPORTXML))
                    {
                        XMLInstaller.install (installDir, installNode, values,
                            logLocal);
                    } // else if TAG_IMPORTXML

                    endTime = new Date ();
                    logLocal
                        .add (DIConstants.LOG_ENTRY,
                            "elapsed time for install: " +
                            (endTime.getTime () - installStartTime.getTime ()) +
                            " milliseconds");
                    seconds = (endTime.getTime () - startTimeLocal.getTime ()) / 1000;
                    minutes = seconds / 60;
                    seconds = seconds - (minutes * 60);
                    logLocal.add (DIConstants.LOG_ENTRY,
                        "total elapsed time: " + minutes + " minute(s) " +
                            seconds + " second(s).");
                } // for iter
            } // if
            else
            // did not find any install entries
            {
                logLocal.add (DIConstants.LOG_WARNING, "Installation file \"" +
                    installFile.getAbsolutePath () + "\"does not contain any " +
                    "install entries. Installation aborted!");
            } // else did not find any install entries
        } // try
        catch (XMLReaderException e)
        {
            IOHelpers.showMessage (e, values.p_app, values.p_sess,
                values.p_env, true);
            logLocal.add (DIConstants.LOG_WARNING,
                "Could not read installation instructions from installation " +
                    "file. Installation aborted!");
        } // catch

        // check if the log was opened within this method:
        if (isLogOpened)
        {
            // close the log:
            ApplicationInstaller.closeLog (logLocal, values);
        } // if
    } // processInstallFile


    /***************************************************************************
     * Perform the installation. <BR/>
     *
     * @param   installDir  The installation directory.
     * @param   installNode The xml node which defines the installation.
     * @param   values      The values for the current function.
     * @param   log         The log.
     * @param   startTime   The starting time of the installation process.
     *
     * @throws  ApplicationInitializationException
     *          There occurred an error during the installation process.
     */
    private static void installPkg (File installDir, Node installNode,
                                    FunctionValues values, Log_01 log,
                                    Date startTime)
        throws ApplicationInitializationException
    {
        NamedNodeMap attributes;        // attributes of the installNode
        Node attribute;                 // attribute node
        String sourcePath = null;       // name and path of source file

        // get the attributes from the node:
        attributes = installNode.getAttributes ();
        if (attributes != null)
        {
            // get the sourcefile:
            attribute = attributes.getNamedItem (ApplicationInstaller.ATTR_SOURCEFILE);
            if (attribute != null)
            {
                sourcePath = attribute.getNodeValue ();
            } // if
        } // if (attributes != null)

        // perform the installation:
        ApplicationInstaller.installPkg (installDir, sourcePath, values, log, startTime);
    } // installPkg


    /***************************************************************************
     * Perform the installation. <BR/>
     *
     * @param   installDir  The installation directory.
     * @param   sourcePath  The source path of the installation file.
     *                      This path must be relative to the installDir.
     * @param   values      The values for the current function.
     * @param   log         The log.
     * @param   startTime   The starting time of the installation process.
     *
     * @throws  ApplicationInitializationException
     *          There occurred an error during the installation process.
     */
    private static void installPkg (File installDir, String sourcePath,
                                    FunctionValues values, Log_01 log,
                                    Date startTime)
        throws ApplicationInitializationException
    {
        File sourceDir;                 // source directory
        File sourceFile = null;         // source file
        String sourceFileName = null;   // name of source file

        // display the settings:
        log.add (DIConstants.LOG_ENTRY, ApplicationInstaller.ATTR_SOURCEFILE +
            "='" + sourceFileName + "'");

        // check constraints for settings:
        if (sourcePath == null || sourcePath.length () == 0)
        {
            log.add (DIConstants.LOG_ERROR,
                "Sourcefile not defined. Install entry ignored.");
        } // if
        else                            // source defined
        {
            // add the source path name to the install directory and
            // divide the result into directory and file name:
            sourceFile = new File (installDir, FileHelpers
                .makeFileNameValid (sourcePath));
            sourceDir = sourceFile.getParentFile ();
            sourceFileName = sourceFile.getName ();

            // and check if the source directory is a valid directory and
            // the source file exists:
            if ((!sourceDir.exists ()) || (!sourceDir.isDirectory ()))
            {
                log.add (DIConstants.LOG_ERROR, "The source directory " +
                    sourceDir + " is not valid. Install entry ignored.");
            } // if
            else if ((!sourceFile.exists ()) || (!sourceFile.isFile ()))
            {
                log.add (DIConstants.LOG_ERROR, "The source file " +
                    sourceFile + " is not valid." + " Install entry ignored.");
            } // if
            else                        // source file valid
            {
                // perform the installation:
                ApplicationInstaller.install (sourceDir, sourceFile, values, log, startTime);
            } // else source file valid
        } // else source defined
    } // installPkg


    /**************************************************************************
     * Get all valid install nodes out of a list of all install nodes. <BR/>
     *
     * @param  allInstallNodes  List of all install nodes.
     *
     * @return  A vector containing all valid install nodes.
     */
    private static Vector<Node> getValidInstallNodes (NodeList allInstallNodes)
    {
        Node installNode;
        String nodeName = null;         // the name of the actual install node
        Vector<String> validTags = new Vector<String> ();
        Vector<Node> installNodes = new Vector<Node> (); // the resulting install nodes

        // loop through the valid tags array and put the elements into the
        // vector:
        for (int i = 0; i < ApplicationInstaller.VALIDTAGS.length; i++)
        {
            validTags.add (ApplicationInstaller.VALIDTAGS[i]);
        } // for i

        // loop through the install entries and perform corresponding
        // operations:
        for (int i = 0; i < allInstallNodes.getLength (); i++)
        {
            // get the install node:
            installNode = allInstallNodes.item (i);
            nodeName = installNode.getNodeName ();

            // check if the node name is valid:
            if (ApplicationInstaller.contains (ApplicationInstaller.VALIDTAGS, nodeName))
            {
                // add valid node to the result list:
                installNodes.add (installNode);
            } // if
        } // for i

        // return the result:
        return installNodes;
    } // getValidInstallNodes


    /**************************************************************************
     * Search for an element within a list. <BR/>
     * The comparison is done throught the <CODE>equals</CODE> method.
     *
     * @param   list    The list in which to search for the element.
     * @param   elem    The element to search for.
     *
     * @return  <CODE>true</CODE> if the element was found within the list,
     *          <CODE>false</CODE> otherwise.
     */
    private static boolean contains (Object[] list, Object elem)
    {
        boolean found = false;          // indicates if we found the element

        // loop through the list and check for each element if it is equal
        // to the element we are searching for:
        for (int i = 0; !found && i < ApplicationInstaller.VALIDTAGS.length; i++)
        {
            found = ApplicationInstaller.VALIDTAGS [i].equals (elem);
        } // for i

        // return the result:
        return found;
    } // contains

    /**************************************************************************
     * Try to load install files for a given installdir. <BR/>
     * The installdir must have the format<BR/>
     * xml_&lt;moduleId&gt; or <BR/>
     * xml_&lt;moduleId&gt;_&lt;updatedir&gt;<BR/>
     *
     * @param installDir    the installation directory
     * @param   values      The values for the current function.
     *
     * @return  <CODE>true</CODE> if the install files could have been loaded or
     *          <CODE>false</CODE> otherwise.
     */
    private static boolean loadInstallFiles (File installDir,
                                             FunctionValues values)
    {
        int index;
        String installDirName = null;
        String moduleId = null;
        String updateDir = null;

        // check if any install directory has been given
        if (installDir != null)
        {
            // get the name from the installation directory
            installDirName = installDir.getName ();

            // check if the installdir is the standard xml directory
            // in that case we cannot extract any module id
            if (installDirName.equalsIgnoreCase (BOPathConstants.PATHN_DEFAULTPACKAGE))
            {
                return false;
            } // if (installDir.equalsIgnoreCase (BOPathConstants.PATHN_DEFAULTPACKAGE))
            //
            else if (installDirName.length () > Module.FILEPREFIX_XML.length ())
            {
                values.p_env.write ("<P/><DIV ALIGN=\"LEFT\"><FONT SIZE=\"2\">");
                values.p_env.write ("Trying to load the installation directory: <code>" +
                        installDirName + "</code>" + IE302.TAG_NEWLINE);

                // extract the "xml_" part of the xml_<moduleId>
                moduleId = installDirName.substring (Module.FILEPREFIX_XML.length ());
                // now check if the module name still contains any "_"
                // indicating the format <moduleId>_<updatedir>
                if ((index = moduleId.indexOf ("_")) != -1)
                {
                    updateDir = moduleId.substring (index + 1);
                    moduleId = moduleId.substring (0, index);
                } // if ((index = moduleId.indexOf ("_")) != -1)
                // not try to get the module with the given moduleId

                // check if a moduleId has beens set
                if (moduleId != null && moduleId.length () > 0)
                {
                    values.p_env.write ("Extracted module Id: <code>" +
                            moduleId + "</code>" + IE302.TAG_NEWLINE);
                    values.p_env.write ("Extracted update directy: <code>" +
                            updateDir + "</code>" + IE302.TAG_NEWLINE);
                    values.p_env.write ("Searching for module with ID <code>" +
                            moduleId + "</code> ... ");
                    // get the module container
                    ModuleContainer moduleContainer =
                        ((ObjectPool) values.p_app.cache).getModuleContainer ();
                    // does the module exist?
                    try
                    {
                        Module module = moduleContainer.get (moduleId);
                        // does the module exist?
                        if (module != null)
                        {
                            values.p_env.write ("found!" + IE302.TAG_NEWLINE);
                            // updateDir found?
                            values.p_env.write ("Loading installation files for module <code>" +
                                    module.getIdVersion () + "</code> ... ");
                            // get the confvars
                            ConfVarContainer confVars = ((IConfiguration)
                                    values.p_app.configuration).getConfVars ();
                            // load the module installation files
                            // and force overwriting existing basic module
                            // installation directories
                            module.loadInstallFiles (confVars, updateDir, true);
                            values.p_env.write ("loaded!" + IE302.TAG_NEWLINE);
                            // check again if the installation directory does exist now
                            values.p_env.write ("Now check the installation directory again ... ");
                            if (installDir.exists () && installDir.isDirectory ())
                            {
                                values.p_env.write ("found!" + IE302.TAG_NEWLINE);
                                values.p_env.write ("</FONT></DIV><P/>");
                                return true;
                            } // if (installDir.exists () && installDir.isDirectory ())

                            // installation directory still does not exist
                            values.p_env.write ("ERROR: not found!" +
                                IE302.TAG_NEWLINE);
                            values.p_env.write ("</FONT></DIV><P/>");
                            return false;
                        } // if (moduleContainer.find (moduleArray[i]))

                        // module not known
                        values.p_env.write ("ERROR: not found!" +
                            IE302.TAG_NEWLINE);
                        values.p_env.write ("</FONT></DIV><P/>");
                        return false;
                    } // try
                    catch (ListException e)
                    {
                        values.p_env.write (IE302.TAG_NEWLINE + "ERROR: " + e.toString ());
                        values.p_env.write ("</FONT></DIV><P/>");
                        return false;
                    } // catch (ListException e)
                } // if (moduleName != null && moduleName.length () > 0)

                // no module id found
                values.p_env.write (IE302.TAG_NEWLINE +
                    "ERROR: Could not extract any module information.");
                values.p_env.write ("</FONT></DIV><P/>");
                return false;
            } // else if installDir.length () > BOPathConstants.PATHN_DEFAULTPACKAGE.length ())
            else // install dir name to short
            {
                return false;
            } // else install dir name to short
        } // if (installFile)

        // no install dir given
        return false;
    } // loadInstallFiles

} // class ApplicationInstaller
