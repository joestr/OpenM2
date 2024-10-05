/*
 * Class: Module.java
 */

// package:
package ibs.service.module;

// imports:
import ibs.app.AppConstants;
import ibs.app.func.FunctionHandlerContainer;
import ibs.app.func.FunctionHandlerContainerLoader;
import ibs.bo.BOPathConstants;
import ibs.di.DIConstants;
import ibs.io.IOHelpers;
import ibs.ml.MultilangConstants;
import ibs.service.list.XMLElement;
import ibs.service.list.XMLElementContainerLoader;
import ibs.tech.xml.XMLHelpers;
import ibs.util.file.FileHelpers;
import ibs.util.list.ElementId;
import ibs.util.list.IElementId;
import ibs.util.list.ListException;

import java.io.File;
import java.util.Iterator;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


/******************************************************************************
 * A module. <BR/>
 *
 * @version     $Id: Module.java,v 1.35 2011/11/28 14:48:31 btatzmann Exp $
 *
 * @author      Klaus, 17.12.2003
 ******************************************************************************
 */
public class Module extends XMLElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Module.java,v 1.35 2011/11/28 14:48:31 btatzmann Exp $";


    /**
     * The version of the module. <BR/>
     */
    private ModuleVersion p_version = null;

    /**
     * The build number of the module. <BR/>
     */
    public String p_build = "";

    /**
     * The name of the module provider. <BR/>
     */
    public String p_providerName = "";

    /**
     * Is the module active?. <BR/>
     * Default: <CODE>true</CODE>
     */
    private boolean p_isActive = true;

    /**
     * All modules which are required for this module. <BR/>
     */
    private ModuleContainer p_requires = null;

    /**
     * References to all modules which are required for this module. <BR/>
     */
    private ModuleReferenceContainer p_requiresRef = null;

    /**
     * All function handlers for this module. <BR/>
     */
    public FunctionHandlerContainer p_functionHandlers = null;

    /**
     * All configuration variables for this module. <BR/>
     */
    public ConfVarContainer p_confVars = null;

    /**
     * The module directory. <BR/>
     */
    public File p_dir = null;

    /**
     * File extension: XML. <BR/>
     */
    private static final String FILEEXT_XML = "*.xml";
    /**
     * File extension: XSLT. <BR/>
     */
    private static final String FILEEXT_XSLT = "*.xsl*";
    /**
     * File extension: SQL. <BR/>
     */
    private static final String FILEEXT_SQL = "*.sql";
    /**
     * File extension: JavaScript. <BR/>
     */
    private static final String FILEEXT_JS = "*.js";
    /**
     * File extension: HTML. <BR/>
     */
    private static final String FILEEXT_HTML = "*.htm*";
    /**
     * File extension: CSS (cascading style sheets). <BR/>
     */
    private static final String FILEEXT_CSS = "*.css";
    /**
     * File extension: Visual Basic Script. <BR/>
     */
    private static final String FILEEXT_VBS = "*.vbs";
    /**
     * File extension: configuration. <BR/>
     */
    private static final String FILEEXT_CFG = "*.cfg";
    /**
     * File extension: resource bundle. <BR/>
     */
    private static final String FILEEXT_RESOURCE_BUNDLE = "*" + MultilangConstants.FILEEXT_RESOURCE_BUNDLE;
    
    /**
     * File prefix: XML install directory. <BR/>
     */
    public static final String FILEPREFIX_XML = "xml_";
    /**
     * File prefix: SQL install directory. <BR/>
     */
    public static final String FILEPREFIX_SQL = "sql_";
    /**
     * File prefix: Language install directory. <BR/>
     */
    public static final String FILEPREFIX_LANG = "lang_";
    
    /**
     * Error message: error while loading module contents. <BR/>
     */
    private static final String ERRM_LOAD = "Error in loadContent";

    /**
     * Load resource bundles <BR/>
     */
    public static final int LOAD_RB = 0;
    
    /**
     * Load ONLY resource bundles <BR/>
     */
    public static final int LOAD_ONLY_RB = 1;
    
    /**
     * Do not load resource bundles <BR/>
     */
    public static final int LOAD_EXCLUDE_RB = 2;

    
    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a Module object. <BR/>
     *
     * @param   id          Id of the element.
     * @param   name        The element's name.
     *
     * @throws  ListException
     *          An error occurred during initializing the container.
     */
    public Module (IElementId id, String name)
        throws ListException
    {
        // call constructor of super class:
        super (id, name);

        // initialize the other instance properties:
        this.p_requires = new ModuleContainer ();
        this.p_requiresRef = new ModuleReferenceContainer ();
        this.p_functionHandlers = new FunctionHandlerContainer ();
        this.p_confVars = new ConfVarContainer ();
    } // Module


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Get the version. <BR/>
     *
     * @return  The version if already set or <CODE>null</CODE> if not set.
     */
    public ModuleVersion getVersion ()
    {
        return this.p_version;
    } // getVersion


    /**************************************************************************
     * Check if the module is active. <BR/>
     *
     * @return  <CODE>true</CODE> if the module is active,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean isActive ()
    {
        return this.p_isActive;
    } // isActive


    /**************************************************************************
     * Get the element type specific data out of the actual element data. <BR/>
     * This method is used to get all values of one element out of the
     * result set. <BR/>
     * <B>example:</B>. <BR/>
     * <PRE>
     * NamedNodeMap attributes = elemData.getAttributes ();
     * this.p_value = XMLHelpers.getAttributeValue (attributes, "itemName");
     * </PRE>
     *
     * @param   elemData    The data for the element.
     * @param   dataFile    The file which contains the data.
     *
     * @throws  ListException
     *          An error occurred during parsing the element.
     */
    public void setProperties (Node elemData, File dataFile)
        throws ListException
    {
        NamedNodeMap attributes = elemData.getAttributes ();
                                        // the attributes
        String str;                     // actual string
        ModuleReferenceContainerLoader refLoader; // container loader
        // container loader:
        XMLElementContainerLoader<ConfVarContainer, ConfVar> confVarLoader;
        String methodName = "setProperties";

        // initialize the module:
        this.init (new ElementId (XMLHelpers.getAttributeValue (attributes, "id")),
                   XMLHelpers.getAttributeValue (attributes, "name"));

        // get properties out of the data file and the dom tree node:
        this.p_dir = dataFile.getParentFile ();
        try
        {
            this.p_version = new ModuleVersion (XMLHelpers.getAttributeValue (
                attributes, "version"));
        } // try
        catch (ModuleVersionException e)
        {
            throw this.createListException (methodName, e);
        } // catch
        this.p_build = XMLHelpers.getAttributeValue (attributes, "build");
        this.p_providerName = XMLHelpers.getAttributeValue (attributes, "providername");

        // check if the module is active:
        str = XMLHelpers.getAttributeValue (attributes, "active");
        if (str != null)
        {
            this.p_isActive = str.equals ("true");
        } // if
        else
        {
            this.p_isActive = false;
        } // else

        if (!this.p_dir.getName ().equals (this.getIdVersion ()))
        {
            IOHelpers.printWarning (methodName, this,
                "The module \"" + this.getIdVersion () + "\" is defined in" +
                " the directory \"" + this.p_dir.getName () + "\"." +
                " Instead it should be defined in the directory \"" +
                this.getIdVersion () + "\".\n" +
                "Attention: This can lead to problems.\n" +
                "The application execution is continued with the current directory.");
        } // if

        // check if the module has to be unloaded:
        if (!this.p_isActive)
        {
            IOHelpers.printWarning (methodName, this, "Module " +
                this.getIdVersion () + " is not active:");

            // if the module is not active, ensure that the module contents
            // are not loaded:
            this.unloadContent ();
        } // if
//    System.out.println (this.getName ());

        // load the required modules:
        refLoader = new ModuleReferenceContainerLoader (this.p_requiresRef,
            this.p_dir.getPath ());
        refLoader.setTagName (ModuleConstants.TAG_REQMODULE);
        refLoader.load (false, false);
//        this.p_requires.addAll
//            (loader.loadElements ((Element) elemData, dataFile));

        // load the configuration variables:
        confVarLoader = new XMLElementContainerLoader<ConfVarContainer, ConfVar> (
            this.p_confVars, this.p_dir.getPath ());
        confVarLoader.setFileNameFilter (ModuleConstants.FILE_CONFVARS);
        confVarLoader.setTagName (ModuleConstants.TAG_CONFVARDEF);
        confVarLoader.load (false, false);
//ibs.io.IOHelpers.printMessage ("module " + this.toString () + ":");
//ibs.io.IOHelpers.printMessage ("    confVars: " + this.p_confVars);

        // load the content of the module:
//        loadContent ();
    } // setProperties


    /**************************************************************************
     * Load the content of the module. <BR/>
     * Normally the installation files (xml, sql, lang) are only reloaded if
     * on or more jar files have been changed. The runtime files (layout,
     * include, stylesheets, scripts, etc.) are only loaded if there has no jar
     * file been changed since last module loading. <BR/>
     * If <CODE>reloadAll</CODE> is set to <CODE>true</CODE> both the
     * installation and the runtime files are loaded for the module.
     *
     * @param   allConfVars All known configuration variables.
     * @param   reloadAll   Shall all module contents be reloaded?
     * @param   loadResourceBundles
     *          Shall the resource bundles be reloaded? Possible values:
     *          <CODE>Module.LOAD_RB</CODE>
     *          <CODE>Module.LOAD_ONLY_RB</CODE>
     *          <CODE>Module.LOAD_EXCLUDE_RB</CODE>
     * @param   resetResourceBundles
     *          Defines if the resource bundles should be reset first within
     *          the target destination. Can be used to remove the resource bundles
     *          before loading the content for the first bundle.
     *
     * @return  <CODE>true</CODE> if some basic resources have changed
     *          and the system has to be restarted. (e.g. java libraries).
     *          <CODE>false</CODE> otherwise.
     *
     * @throws  ListException
     *          An error occurred during parsing the element.
     */
    public boolean loadContent (ConfVarContainer allConfVars, boolean reloadAll,
            int loadResourceBundles, boolean resetResourceBundles)
        throws ListException
    {
        boolean retVal = false;         // the return value
        FunctionHandlerContainerLoader loader; // container loader
        String absBasePath = null;      // base path of application
        String modulePath = null;       // path of module
        String xmlInstallDir = null;    // install dir for xml files
        String sqlInstallDir = null;    // install dir for sql files
        String langInstallDir = null;   // install dir for language files

        absBasePath = this.p_dir.getParentFile ().getParentFile ().getPath () +
            File.separator;
        modulePath = this.p_dir.getPath () + File.separator;
        xmlInstallDir = absBasePath + BOPathConstants.PATH_ABS_APPINSTALL +
            Module.FILEPREFIX_XML + this.getId () + File.separator;
        xmlInstallDir = FileHelpers.makeFileNameValid (xmlInstallDir);
        sqlInstallDir = absBasePath + BOPathConstants.PATH_ABS_APPINSTALL +
            Module.FILEPREFIX_SQL + this.getId () + File.separator;
        sqlInstallDir = FileHelpers.makeFileNameValid (sqlInstallDir);
        langInstallDir = absBasePath + BOPathConstants.PATH_ABS_APPINSTALL +
            Module.FILEPREFIX_LANG + this.getId () + File.separator;
        langInstallDir = FileHelpers.makeFileNameValid (langInstallDir);
       
        try
        {
            IOHelpers.printMessage ("loading module " + this.getIdVersion () + ".");
          //IOHelpers.printMessage ("loading jar files, absBasePath = " + absBasePath + ", modulePath = " + modulePath);

            if (resetResourceBundles) // reset the resource bundles ?
            {
                ModuleFileHelpers.deleteFiles (
                        absBasePath + BOPathConstants.PATH_RESOURCE_BUNDLES,
                        Module.FILEEXT_RESOURCE_BUNDLE);
            } // if
            
            // check if ONLY resource bundles have to be reloaded
            if (loadResourceBundles == LOAD_ONLY_RB)
            {
                loadResourceBundles (allConfVars, modulePath, absBasePath);
                
                // return the result:
                return retVal;
            } // if
            
            // load the libraries for the module:
            retVal =
                ModuleFileHelpers.copyFiles (
                    modulePath + ModuleConstants.DIR_LIB,
                    absBasePath + BOPathConstants.PATH_LIB,
                    "*", this.getIdVersion () + "_");

            // check if jar files have been changed or we have to reload all
            // contents:
            if (retVal || reloadAll)    // reload module contents?
            {
                this.loadInstallFiles (allConfVars);
            } // if reload module contents
        } // try
        catch (ModuleLoadingException e)
        {
            throw this.createListException (Module.ERRM_LOAD, e);
        } // catch

//IOHelpers.printMessage ("loaded jar files, retVal = " + retVal);
        // check if some necessary resources have changed:
        // if yes we don't have to reload these files
        if (!retVal || reloadAll)       // no jar changes?
        {
            // load the function handlers:
            loader = new FunctionHandlerContainerLoader (
                this.p_functionHandlers, this.p_dir.getPath ());
            loader.load (false, false);
//        this.p_functionHandlers.addAll
//            (loader.loadElements ((Element) elemData, dataFile));

            try
            {
                // load the html include files for the module:
                ModuleFileHelpers.copyFiles (
                    modulePath + ModuleConstants.DIR_INCLUDE,
                    absBasePath + BOPathConstants.PATH_APPINCLUDE,
                    Module.FILEEXT_HTML, allConfVars);

                // load all image files for the module:
                ModuleFileHelpers.copyFiles (
                    modulePath + ModuleConstants.DIR_IMAGES,
                    absBasePath + BOPathConstants.PATH_APPIMAGES,
                    "*", "");

                // load all layout files for the module:
                ModuleFileHelpers.copyFiles (
                    modulePath + ModuleConstants.DIR_LAYOUTS,
                    absBasePath + BOPathConstants.PATH_APPLAYOUTS,
                    "*", "");

                // load the html layout files for the module:
                ModuleFileHelpers.copyFiles (
                    modulePath + ModuleConstants.DIR_LAYOUTS,
                    absBasePath + BOPathConstants.PATH_APPLAYOUTS,
                    Module.FILEEXT_HTML, allConfVars);

                // load the css layout files for the module:
                ModuleFileHelpers.copyFiles (
                    modulePath + ModuleConstants.DIR_LAYOUTS,
                    absBasePath + BOPathConstants.PATH_APPLAYOUTS,
                    Module.FILEEXT_CSS, allConfVars);

                // load the css layout files for the module:
                ModuleFileHelpers.copyFiles (
                    modulePath + ModuleConstants.DIR_REPORTS,
                    absBasePath + BOPathConstants.PATH_APPREPORTS,
                    "*", "");

                // load the js script files for the module:
                ModuleFileHelpers.copyFiles (
                    modulePath + ModuleConstants.DIR_SCRIPTFILES,
                    absBasePath + BOPathConstants.PATH_SCRIPTS,
                    Module.FILEEXT_JS, allConfVars);
                // load the visual basic script files for the module:
                ModuleFileHelpers.copyFiles (
                    modulePath + ModuleConstants.DIR_SCRIPTFILES,
                    absBasePath + BOPathConstants.PATH_SCRIPTS,
                    Module.FILEEXT_VBS, allConfVars);
                // load the buttons.html for the module:
                ModuleFileHelpers.copyFiles (
                    modulePath + ModuleConstants.DIR_SCRIPTFILES,
                    absBasePath + BOPathConstants.PATH_SCRIPTS,
                    AppConstants.FILE_BUTTONS,
                    allConfVars);

                // load the xslt stylesheet files for the module:
                ModuleFileHelpers.copyFiles (
                    modulePath + ModuleConstants.DIR_XSLTSTYLESHEETS,
                    absBasePath + BOPathConstants.PATH_XSLT,
                    Module.FILEEXT_XSLT, allConfVars);

                // load the xslt translator files for the module:
                ModuleFileHelpers.copyFiles (
                    modulePath + ModuleConstants.DIR_TRANSLATORS,
                    absBasePath + BOPathConstants.PATH_TRANS,
                    Module.FILEEXT_XSLT, allConfVars);

                // load the cfg configuration files for the module:
                ModuleFileHelpers.copyFiles (
                    modulePath + ModuleConstants.DIR_FORMCFG,
                    absBasePath + BOPathConstants.PATH_APPFORMCFG,
                    Module.FILEEXT_CFG, allConfVars);
                
                if (loadResourceBundles != LOAD_EXCLUDE_RB) // load resource bundles ?
                {
                    // load the resource bundle files for the module:
                    loadResourceBundles (allConfVars, modulePath, absBasePath);
                } // if
            } // try
            catch (ModuleLoadingException e)
            {
                throw this.createListException (Module.ERRM_LOAD, e);
            } // catch
        } // if no jar changes

        // return the result:
        return retVal;
    } // loadContent

    
    /**************************************************************************
     * Load the resource bundles containing the multilingual texts of the
     * module into the install directory and substitute the confvars in the
     * files.<BR/>
     *
     * @param   allConfVars     All known configuration variables.
     * @param   modulePath      The module path
     * @param   absBasePath     The absBasePath
     *
     * @throws  ModuleLoadingException
     *          An error occurred during loading the resource bundles.
     */
    private void loadResourceBundles (ConfVarContainer allConfVars,
            String modulePath, String absBasePath)
        throws ModuleLoadingException
    {
        IOHelpers.printMessage ("loading resource bundles for module " + this.getIdVersion () + ".");
        
        // load the resource bundle files for the module, 
        // excluding the formtemplates, queries and tabs bundle
        // resource bundles:
        ModuleFileHelpers.copyFiles (
                modulePath + ModuleConstants.DIR_RESOURCE_BUNDLES,
                absBasePath + BOPathConstants.PATH_RESOURCE_BUNDLES,
                Module.FILEEXT_RESOURCE_BUNDLE,
                new String[] {"*" + MultilangConstants.RESOURCE_BUNDLE_FORMTEMPLATES_NAME + "*",
                              "*" + MultilangConstants.RESOURCE_BUNDLE_QUERIES_NAME + "*",
                              "*" + MultilangConstants.RESOURCE_BUNDLE_REPORTS_NAME + "*",
                              "*" + MultilangConstants.RESOURCE_BUNDLE_TABS_NAME + "*",
                              "*" + MultilangConstants.RESOURCE_BUNDLE_TYPES_NAME + "*",
                              "*" + MultilangConstants.RESOURCE_BUNDLE_OBJECTS_NAME + "*"},
                allConfVars,
                DIConstants.CHARACTER_ENCODING);
        
        // merge all form template resource bundle files to the target directory:
        ModuleFileHelpers.mergeResourceBundles (
                modulePath + ModuleConstants.DIR_RESOURCE_BUNDLES,
                absBasePath + BOPathConstants.PATH_RESOURCE_BUNDLES,
                MultilangConstants.RESOURCE_BUNDLE_FORMTEMPLATES_NAME);

        // merge all query resource bundle files to the target directory:
        ModuleFileHelpers.mergeResourceBundles (
                modulePath + ModuleConstants.DIR_RESOURCE_BUNDLES,
                absBasePath + BOPathConstants.PATH_RESOURCE_BUNDLES,
                MultilangConstants.RESOURCE_BUNDLE_QUERIES_NAME);

        // merge all report resource bundle files to the target directory:
        ModuleFileHelpers.mergeResourceBundles (
                modulePath + ModuleConstants.DIR_RESOURCE_BUNDLES,
                absBasePath + BOPathConstants.PATH_RESOURCE_BUNDLES,
                MultilangConstants.RESOURCE_BUNDLE_REPORTS_NAME);
        
        // merge all tab resource bundle files to the target directory:
        ModuleFileHelpers.mergeResourceBundles (
                modulePath + ModuleConstants.DIR_RESOURCE_BUNDLES,
                absBasePath + BOPathConstants.PATH_RESOURCE_BUNDLES,
                MultilangConstants.RESOURCE_BUNDLE_TABS_NAME);
        
        // merge all type resource bundle files to the target directory:
        ModuleFileHelpers.mergeResourceBundles (
                modulePath + ModuleConstants.DIR_RESOURCE_BUNDLES,
                absBasePath + BOPathConstants.PATH_RESOURCE_BUNDLES,
                MultilangConstants.RESOURCE_BUNDLE_TYPES_NAME);
        
        // merge all object resource bundle files to the target directory:
        ModuleFileHelpers.mergeResourceBundles (
                modulePath + ModuleConstants.DIR_RESOURCE_BUNDLES,
                absBasePath + BOPathConstants.PATH_RESOURCE_BUNDLES,
                MultilangConstants.RESOURCE_BUNDLE_OBJECTS_NAME);
    } // loadResourceBundles
    

    /**************************************************************************
     * Load the install files of the module into the install firectory
     * and substitute the confvars in the files.<BR/>
     *
     * @param   allConfVars All known configuration variables.
     *
     * @throws  ListException
     *          An error occurred during parsing the element.
     */
    public void loadInstallFiles (ConfVarContainer allConfVars)
        throws ListException
    {
        this.loadInstallFiles (allConfVars, "update_*", false);
    } // loadInstallFiles


    /**************************************************************************
     * Load the install files of the module into the install firectory
     * and substitute the confvars in the files.<BR/>
     * Use the updateFileFilter to load specific update packages.<BR/>
     *
     * @param allConfVars         All known configuration variables.
     * @param updateFileFilter     any file filter for the updates
     * @param isForced            force overwriting
     *
     * @throws  ListException
     *          An error occurred during parsing the element.
     */
    public void loadInstallFiles (ConfVarContainer allConfVars,
                                  String updateFileFilter,
                                  boolean isForced)
        throws ListException
    {
        String absBasePath = null;      // base path of application
        String modulePath = null;       // path of module
        String xmlSourceDir = null;     // directory with xml installation files
        String xmlInstallDir = null;    // install dir for xml files
        String sqlSourceDir = null;     // directory with sql installation files
        String sqlInstallDir = null;    // install dir for sql files
        String langSourceDir = null;    // directory with language installation
                                        // files
        String langInstallDir = null;   // install dir for language files

        absBasePath = this.p_dir.getParentFile ().getParentFile ().getPath () +
            File.separator;
        modulePath = this.p_dir.getPath () + File.separator;
        xmlSourceDir = FileHelpers.makeFileNameValid (modulePath +
            ModuleConstants.DIR_INSTALLXML);
        xmlInstallDir = absBasePath + BOPathConstants.PATH_ABS_APPINSTALL +
            Module.FILEPREFIX_XML + this.getId () + File.separator;
        xmlInstallDir = FileHelpers.makeFileNameValid (xmlInstallDir);
        sqlSourceDir = FileHelpers.makeFileNameValid (modulePath +
            ModuleConstants.DIR_INSTALLSQL);
        sqlInstallDir = absBasePath + BOPathConstants.PATH_ABS_APPINSTALL +
            Module.FILEPREFIX_SQL + this.getId () + File.separator;
        sqlInstallDir = FileHelpers.makeFileNameValid (sqlInstallDir);
        langSourceDir = FileHelpers.makeFileNameValid (modulePath +
            ModuleConstants.DIR_INSTALLLANG);
        langInstallDir = absBasePath + BOPathConstants.PATH_ABS_APPINSTALL +
            Module.FILEPREFIX_LANG + this.getId () + File.separator;
        langInstallDir = FileHelpers.makeFileNameValid (langInstallDir);

        try
        {
            // check if xml installation directory does not exist yet:
            if (isForced || !FileHelpers.exists (xmlInstallDir))
            {
                // load all xml install files for the module:
                ModuleFileHelpers.copyFiles (xmlSourceDir, xmlInstallDir, "*",
                    "");

                // load the xml install files for the module with variable
                // replacement:
                ModuleFileHelpers.copyFiles (xmlSourceDir, xmlInstallDir,
                    Module.FILEEXT_XML, allConfVars);

                // load the xslt install files for the module with variable
                // replacement:
                ModuleFileHelpers.copyFiles (xmlSourceDir, xmlInstallDir,
                    Module.FILEEXT_XSLT, allConfVars);

                // move the module specific update directories to the install
                // directory:
                ModuleFileHelpers.moveFiles (xmlInstallDir, absBasePath +
                    BOPathConstants.PATH_ABS_APPINSTALL, Module.FILEPREFIX_XML +
                    "*", Module.FILEPREFIX_XML + this.getId () + "_");

                // move the module specific update directories to the install
                // directory in case any updateFileFilter has been set
                if (updateFileFilter != null && !updateFileFilter.isEmpty ())
                {
                    ModuleFileHelpers.moveFiles (xmlInstallDir, absBasePath +
                        BOPathConstants.PATH_APP + "install", updateFileFilter,
                        Module.FILEPREFIX_XML + this.getId () + "_");
                } // if (updateFileFilter != null && updateFileFilter.length() > 0)

                // load the app files for the module:
                ModuleFileHelpers.copyFiles (modulePath +
                    ModuleConstants.DIR_APP, absBasePath +
                    BOPathConstants.PATH_APP, "*", "");
            } // if (isForced || !FileHelpers.exists (xmlInstallDir))

            // check if sql installation directory does not exist yet:
            if (isForced || !FileHelpers.exists (sqlInstallDir))
            {
                // load all sql install files for the module:
                ModuleFileHelpers.copyFiles (sqlSourceDir, sqlInstallDir, "*",
                    "");

                // load the sql install files for the module with variable
                // replacement:
                ModuleFileHelpers.copyFiles (sqlSourceDir, sqlInstallDir,
                    Module.FILEEXT_SQL, allConfVars);
            } // if (isForced || !FileHelpers.exists (sqlInstallDir))

            // check if language installation directory does not exist yet:
            if (isForced || !FileHelpers.exists (langInstallDir))
            {
                // load all language install files for the module:
                ModuleFileHelpers.copyFiles (langSourceDir, langInstallDir,
                    "*", "");

                // load the language install files for the module with variable
                // replacement:
                ModuleFileHelpers.copyFiles (langSourceDir, langInstallDir,
                    Module.FILEEXT_SQL, allConfVars);

                // load the language install files for the module with variable
                // replacement:
                ModuleFileHelpers.copyFiles (langSourceDir, langInstallDir,
                    Module.FILEEXT_JS, allConfVars);

                // load the language install files for the module with variable
                // replacement:
                ModuleFileHelpers.copyFiles (langSourceDir, langInstallDir,
                    Module.FILEEXT_HTML, allConfVars);

                // load the language install files for the module with variable
                // replacement:
                ModuleFileHelpers.copyFiles (langSourceDir, langInstallDir,
                    Module.FILEEXT_XML, allConfVars);
            } // if (isForced || !FileHelpers.exists (langInstallDir))
        } // try
        catch (ModuleLoadingException e)
        {
            throw this.createListException (Module.ERRM_LOAD, e);
        } // catch
    } // loadInstallFiles


    /***************************************************************************
     * Unload the content of the module. <BR/> All files coming from this module
     * are deleted.
     *
     * @throws ListException An error occurred during parsing the element.
     */
    public void unloadContent ()
        throws ListException
    {
        String absBasePath = null;      // base path of application

        absBasePath = this.p_dir.getParentFile ().getParentFile ().getPath () +
            File.separator;

        try
        {
            // delete the stylesheet files for all modules:
            ModuleFileHelpers.deleteFiles (
                absBasePath + BOPathConstants.PATH_XSLT,
                "*.xsl");

            // delete the script files for all modules:
            ModuleFileHelpers.deleteFiles (
                absBasePath + BOPathConstants.PATH_SCRIPTS,
                "*");

            // delete the libraries for the module:
            ModuleFileHelpers.deleteFiles (
                absBasePath + BOPathConstants.PATH_LIB,
                this.getIdVersion () + "_*");
        } // try
        catch (ModuleLoadingException e)
        {
            throw this.createListException ("unloadContent", e);
        } // catch
    } // unloadContent


    /**************************************************************************
     * Check constraints for the element. <BR/>
     *
     * @throws  ListException
     *          Error when checking the constraints.
     */
    public void checkConstraints ()
        throws ListException
    {
        Iterator<ConfVar> iter = this.p_confVars.iterator ();

        // check only the first configuration variable:
        if (iter.hasNext ())             // at least one variable exists?
        {
            ConfVar elem = iter.next ();

            // check if the id and the version are identical to the actual:
            if (!elem.p_moduleId.equals (this.getId ().getIdStr ()) ||
                !elem.p_moduleVersion.isMatch (this.p_version,
                                               ModuleConstants.MATCH_PERFECT))
            {
                throw new ListException (
                    "The configuration variables within module \"" +
                    this.getIdVersion () +
                    "\" are defined for a different module: " +
                    "\"" + elem.getIdVersion () + "\".");
            } // if
        } // if at least one variable exists
    } // checkConstraints


    /**************************************************************************
     * Resolve the dependencies between this element and other elements out of
     * the container. <BR/>
     * If there are any dependencies ensure that these dependencies are object
     * references instead of (textual) descriptions.
     *
     * @param   elems       The container with the other elements.
     *
     * @throws  ListException
     *          There occurred an error during the operation.
     */
    public void resolveDependencies (ModuleContainer elems)
        throws ListException
    {
        try
        {
            // resolve dependencies for the required modules:
            this.p_requiresRef.resolveDependencies (elems);

            // loop through all module references and resolve the dependencies
            // for each one:
            for (Iterator<ModuleReference> iter = this.p_requiresRef.iterator (); iter.hasNext ();)
            {
                // add the referenced object to the container:
                this.p_requires.add (
                    (iter.next ()).p_referencedElement);
            } // for iter
        } // try
        catch (ListException e)
        {
            throw this.createListException ("resolveDependencies", e);
        } // catch
    } // resolveDependencies


    /**************************************************************************
     * Check if the actual module depends on another one. <BR/>
     *
     * @param   otherObj    The other object to be checked.
     *
     * @return  <CODE>true</CODE> if the object depends on the other object,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean depends (Module otherObj)
    {
        return this.p_requires.containsEqual (otherObj);
    } // depends


    /**************************************************************************
     * Returns the string representation of the id and the version. <BR/>
     * The id and the version name are concatenated to create a string
     * representation according to <CODE>"id_version"</CODE>. <BR/>
     * e.g.: <CODE>"ibsbase_2.4.1"</CODE>
     *
     * @return  String represention of the object.
     */
    public String getIdVersion ()
    {
        // compute the string and return it:
        return this.getId () + "_" + this.p_version;
    } // getIdVersion


    /**************************************************************************
     * Returns the string representation of this object. <BR/>
     * The id and the name are concatenated to create a string
     * representation according to "id, name".
     *
     * @return  String represention of the object.
     */
    public String toString ()
    {
        // compute the string and return it:
        return this.getIdVersion () + "," + this.getName ();
    } // toString


    /**************************************************************************
     * Create a ListException within a module. <BR/>
     *
     * @param   posText A text describing the actual position
     *                  (e.g. the method name).
     * @param   exc     The exception to be chained.
     *
     * @return  The created exception.
     */
    public ListException createListException (String posText, Throwable exc)
    {
        return new ListException (
            "Error in " + posText + " for module \"" +
            this.getIdVersion () + "\"", exc);
    } // createListException

} // class Module
