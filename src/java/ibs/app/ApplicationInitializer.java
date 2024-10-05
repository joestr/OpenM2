/*
 * Class: ApplicationInitializer.java
 */

// package:
package ibs.app;

// imports:
import ibs.IbsGlobals;
import ibs.IbsObject;
import ibs.app.func.FunctionHandlerContainer;
import ibs.app.system.SystemLoader;
import ibs.bo.BOConstants;
import ibs.bo.BOPathConstants;
import ibs.bo.Buttons;
import ibs.bo.OID;
import ibs.bo.cache.ObjectPool;
import ibs.bo.tab.TabContainer;
import ibs.bo.type.MayContainContainerLoader;
import ibs.bo.type.TypeContainer;
import ibs.bo.type.TypeContainerLoader;
import ibs.di.DIConstants;
import ibs.extdata.APIConnectionPool;
import ibs.io.IOHelpers;
import ibs.io.servlet.ApplicationInitializationException;
import ibs.io.servlet.IApplicationContext;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.IApplicationInitializer;
import ibs.ml.MultilangConstants;
import ibs.ml.MultilingualTextProvider;
import ibs.ml.MultilingualTextProviderException;
import ibs.ml.export.MultilingualTextExporterException;
import ibs.ml.export.ResourceBundlePreloader;
import ibs.ml.export.TextToClientExporter;
import ibs.obj.lang.LanguageStringContainer_01;
import ibs.obj.query.QueryPool;
import ibs.service.conf.Configuration;
import ibs.service.conf.ConfigurationException;
import ibs.service.conf.IConfiguration;
import ibs.service.module.ConfValueContainer;
import ibs.service.module.ConfValueLoader;
import ibs.service.module.ConfVarContainer;
import ibs.service.module.Module;
import ibs.service.module.ModuleConstants;
import ibs.service.module.ModuleContainer;
import ibs.service.module.ModuleContainerLoader;
import ibs.service.observer.ObserverException;
import ibs.service.observer.ObserverLoader;
import ibs.service.reporting.ReportingException;
import ibs.service.reporting.birt.BIRTReportingEngine;
import ibs.tech.html.ButtonBarElement;
import ibs.tech.sql.DBConnector;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLConstants;
import ibs.util.DateTimeHelpers;
import ibs.util.file.FileHelpers;
import ibs.util.list.ListException;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;


/******************************************************************************
 * This class is used for initializing the application. <BR/>
 * The method {@link #initApplication (IApplicationContext) initApplication}
 * should be called at application/servlet startup.
 *
 * @version     $Id: ApplicationInitializer.java,v 1.218 2012/08/27 08:09:22 gweiss Exp $
 *
 * @author      Klaus, 22.12.2003
 ******************************************************************************
 */
public class ApplicationInitializer
    extends IbsObject
    implements IApplicationInitializer
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ApplicationInitializer.java,v 1.218 2012/08/27 08:09:22 gweiss Exp $";


    /**
     * The application object itself. <BR/>
     */
    private ApplicationInfo p_app = null;

    /**
     * The date time format for the init messages. <BR/>
     */
    private String TIME_FORMAT = "HH:mm:ss";

    /**
     * The argument to reload module contents. <BR/>
     */
    public String ARG_RELOADMODULES = "reloadmodules";
    
    /**
     * Register types: Perform load all. <BR/>
     */
    private static int REGISTER_TYPES_LOAD_ALL = 0;

    /**
     * Register types: If this is set, the types are not registered,
     * just extended info like mayContainTypes. <BR/>
     */    
    private static int REGISTER_TYPES_LOAD_JUST_EXTENDEND_INFO = 1;

    /**
     * Register types: If this is, the types and the extended info
     * is not loaded only the multilang info. <BR/>
     */        
    private static int REGISTER_TYPES_LOAD_JUST_MULTILANG = 2;

    /**
     * Register queries: Perform load all (queries and multilang). <BR/>
     */
    private static int REGISTER_QUERIES_LOAD_ALL = 0;

    /**
     * Register queries: If this is set, only the multilang info
     * is loaded. <BR/>
     */        
    private static int REGISTER_QUERIES_LOAD_JUST_MULTILANG = 1;

    
    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates an ApplicationInitializer object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     * <CODE>p_app</CODE> is initialized to <CODE>app</CODE>. <BR/>
     *
     * @param   app     The global application info.
     */
    public ApplicationInitializer (ApplicationInfo app)
    {
        // call constructor of super class:
        super ();

//trace ("in ApplicationInfo");
        // initialize the instance properties:
        this.p_app = app;
    } // ApplicationInitializer


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Method which is called once for the Application. <BR/>
     * It fills the language specific files and makes other initializations
     * to be done at application startup.
     *
     * @param   context     The application context.
     *
     * @throws  ApplicationInitializationException
     *          An exception occurred during the application initialization.
     */
    public void initApplication (IApplicationContext context)
        throws ApplicationInitializationException
    {
        Configuration conf = null;      // the configuration
        Date startDate = new Date ();

        // store the application info object:
        IbsGlobals.p_app = this.p_app;

        this.createHTMLHeader (context.getApp (), context.getSess (), context.getEnv ());
        context.getEnv ().write ("<SPAN ID=\"initApp\" STYLE=\"font-family:courier new, fixed; font-size: 12px;\">");

        this.writeMessage (context, "Starting application initialization on " +
            context.getEnv ().getServerName () + "...");

        try
        {
            this.writeMessage (context, "Loading configuration");

            conf = new Configuration (context.getConfigPath ());
            conf.getDbConf ().setDbLogDir (
                FileHelpers.makeFileNameValid (
                    context.getApp ().p_system.p_m2AbsBasePath +
                    BOPathConstants.PATH_SQLLOGS));

        } // try
        catch (ConfigurationException e)
        {
            throw new ApplicationInitializationException (e);
        } // catch

        // if an error occurred while reading the config-file the
        // error string is stored in this attribute.
        if (conf.readConfig ())
        {
            try
            {
                this.writeMessage (context, "Initializing database connectivity.");

                // initialize the database connector:
                DBConnector.init ();
            } // try
            catch (ListException e)
            {
                throw new ApplicationInitializationException (e);
            } // catch

            this.p_app.configuration = conf;
            DBConnector.setConfiguration (conf.getDbConf ());

            try
            {
                // try to get a db connection:
                DBConnector.getDBConnection ();
            } // try
            catch (DBError e)
            {
//IOHelpers.printMessage (conf.toString ());
                throw new ApplicationInitializationException (
                    "Could not establish connection to database \"" +
                    conf.getDbSid () + "\" on server \"" +
                    conf.getDbServerName () + "\" (connection string: \"" +
                    conf.getDbConnectionString () + "\"). Please check if the" +
                    " database name is correct and if" +
                    " the database server is started.", e);
            } // catch

            // load the values which are necessary at application runtime:
            this.loadValues (context);

            try
            {
                this.writeMessage (context, "Starting observers.");

                // set observer loader:
                this.p_app.p_observerLoader = ObserverLoader.instance ();
                if (this.p_app.p_observerLoader != null)
                {
                    ((ObserverLoader) this.p_app.p_observerLoader)
                        .start (context.getConfigPath () + "observers.xml");
                } // if
            } // try
            catch (ObserverException e)
            {
                System.out.println (">>>>>> Application.start: Error when starting ObserverLoader: " +
                    e.toString ());
                System.out.println (">>>>>> Application Server proceeds - without observers.");
            } // catch

            // load the reporting engine
            try
            {
                // load the reporting engine
                if (this.loadReportingEngine (context))
                {
                    this.writeMessage (context, "Loading reporting engine ... loaded");
                } // if (! loadReportingEngine (context))
                else    // not loaded
                {
                    this.writeMessage (context, "Loading reporting engine ... not loaded");
                    System.out.println (">>>>>> Application.start: no reporting engine activated.");
                } // else not loaded
            } // try
            catch (ReportingException e)
            {
                this.writeMessage (context, "Loading reporting engine ... failed: " +
                        e.toString ());

                System.out.println (">>>>>> Application.start: Error when loading ReportingEngine: " +
                        e.toString ());
                System.out.println (">>>>>> Application Server proceeds - without reporting engine.");
            } // catch (ReportingException e)

        } // if configuration read without errors
        else                    // write errors to variable
        {
            this.p_app.configErrors = conf.getErrors ();
            throw new ApplicationInitializationException (
                this.p_app.configErrors.toString ());
        } // else write errors to variable

        this.writeMessage (context, "Application initialization finished.");

        Date endDate = new Date ();
        long milliseconds = endDate.getTime () - startDate.getTime ();
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        context.write ("Initialization duration: " +
                minutes + " minute (s) " + seconds + " second (s).");

        context.getEnv ().write ("</SPAN>");
        
    } // initApplication


    /**************************************************************************
     * Load application values. <BR/>
     * This method loads all values which are necessary for application runtime.
     *
     * @param   context     The application context.
     *
     * @throws  ApplicationInitializationException
     *          An error occurred during loading the values.
     */
    protected void loadValues (IApplicationContext context)
        throws ApplicationInitializationException
    {
    	boolean loadModules = false;
        String str;

        this.writeMessage (context, "Loading values - getting system info");

        // register the modules:
        if (this.getModuleCache ().size () <= 0)
                                        // no modules registered yet?
        {
            this.writeMessage (context, "Loading values - register modules");

            // call the module register function:
            this.registerModules (context);
            loadModules = true;
        } // if no modules registered yet

        // register the configuration variables:
        if (this.getConfiguration ().getConfVars ().size () <= 0)
                                        // no variables registered yet?
        {
            this.writeMessage (context, "Loading values - register configuration variables");

            // call the configuration variables register function:
            this.registerConfVars (context);
        } // if no variables registered yet

        // load the content of the modules:
        if (loadModules)
        {
            // check if the modules needs to be reloaded
            if (context.getEnv ().getBoolParam (this.ARG_RELOADMODULES) != 0)
            {
                this.writeMessage (context, "Loading values - loading module contents");
                try
                {
                    // load the content and check if a server restart is neccessary
                    // this is the case when any jar have been changed / deleted
                    if (this.getModuleCache ().loadContent (
                        this.getConfiguration ().getConfVars (), false, Module.LOAD_RB))
                    {
                        // ensure that the application cannot be restarted without
                        // shutting down the server:
                        this.p_app.p_restartPossible = false;

                        throw new ApplicationInitializationException (
                            "Some necessary resources have been changed during" +
                            " initialization. Please restart the web server now.");
                    } // if
                } // try
                catch (ListException e)
                {
                    throw new ApplicationInitializationException (e);
                } // catch
            } // if (context.getEnv ().getBoolParam (ARG_RELOADMODULES) != 0)
            else    // loading module content disabled
            {
                this.writeMessage (context, "Loading values - loading module contents has been disabled");
            } // elseloading module content disabled
        } // if (loadModules)


        // setting configuration values in static class properties
        // values for deadlock resolution
        try
        {
            // set the RETRIES_ON_DEADLOCK value
            str = this.getConfiguration ().getConfVars ().getValue ("ibsbase.retriesOnDeadlock");
            if (str != null && str.trim ().length () > 0)
            {
                SQLConstants.RETRIES_ON_DEADLOCK = Integer.parseInt (str);
            } // if
        } // try
        catch (NumberFormatException e)
        {
            IOHelpers.printWarning ("invalid configuration value for " +
                    "CONFVAR.ibsbase.retriesOnDeadlock", this, e, false);
        } // catch (NumberFormatException e)
        try
        {
            // set the RETRYTIME_ON_DEADLOCK value
            str = this.getConfiguration ().getConfVars ().getValue ("ibsbase.retryTimeOnDeadlock");
            if (str != null && str.trim ().length () > 0)
            {
                SQLConstants.RETRYTIME_ON_DEADLOCK = Integer.parseInt (str);
            } // if
        } // try
        catch (NumberFormatException e)
        {
            IOHelpers.printWarning ("invalid configuration value for " +
                    "CONFVAR.ibsbase.retryTimeOnDeadlock", this, e, false);
        } // catch (NumberFormatException e)
        try
        {
            // set the RETRYTIMEOFFSET_ON_DEADLOCK value
            str = this.getConfiguration ().getConfVars ().getValue ("ibsbase.retryTimeOffsetOnDeadlock");
            if (str != null && str.trim ().length () > 0)
            {
                SQLConstants.RETRYTIMEOFFSET_ON_DEADLOCK = Integer.parseInt (str);
            } // if
        } // try
        catch (NumberFormatException e)
        {
            IOHelpers.printWarning ("invalid configuration value for " +
                    "CONFVAR.ibsbase.retryTimeOffsetOnDeadlock", this, e, false);
        } // catch (NumberFormatException e)
        
        // values for reconnecting after DB connection loss
        try
        {
            // set the RETRYTIMEOFFSET_ON_DBCONNECTION_LOSS value
            str = this.getConfiguration ().getConfVars ().getValue ("ibsbase.retryTimeOffsetOnDBConnectionLoss");
            if (str != null && str.trim ().length () > 0)
            {
                SQLConstants.RETRYTIMEOFFSET_ON_DBCONNECTION_LOSS = Integer.parseInt (str);
            } // if
        } // try
        catch (NumberFormatException e)
        {
            IOHelpers.printWarning ("invalid configuration value for " +
                    "CONFVAR.ibsbase.retryTimeOffsetOnDBConnectionLoss", this, e, false);
        } // catch (NumberFormatException e)
        try
        {
            // set the RETRIES_FOR_DBCONNECTION value
            str = this.getConfiguration ().getConfVars ().getValue ("ibsbase.retriesForDBConnection");
            if (str != null && str.trim ().length () > 0)
            {
                SQLConstants.RETRIES_FOR_DBCONNECTION = Integer.parseInt (str);
            } // if
        } // try
        catch (NumberFormatException e)
        {
            IOHelpers.printWarning ("invalid configuration value for " +
                    "CONFVAR.ibsbase.retriesForDBConnection", this, e, false);
        } // catch (NumberFormatException e)
        try
        {
            // set the THRESHOLD_OF_CONNECTION_LOSSES value
            str = this.getConfiguration ().getConfVars ().getValue ("ibsbase.dbConnectionLossThreshold");
            if (str != null && str.trim ().length () > 0)
            {
                SQLConstants.THRESHOLD_OF_CONNECTION_LOSSES = Integer.parseInt (str);
            } // if
        } // try
        catch (NumberFormatException e)
        {
            IOHelpers.printWarning ("invalid configuration value for " +
                    "CONFVAR.ibsbase.dbConnectionLossThreshold", this, e, false);
        } // catch (NumberFormatException e)
        try
        {
            // set the TIMESPAN_OF_CONNECTION_LOSSES value
            str = this.getConfiguration ().getConfVars ().getValue ("ibsbase.dbConnectionLossTimespan");
            if (str != null && str.trim ().length () > 0)
            {
                SQLConstants.TIMESPAN_OF_CONNECTION_LOSSES = Integer.parseInt (str);
            } // if
        } // try
        catch (NumberFormatException e)
        {
            IOHelpers.printWarning ("invalid configuration value for " +
                    "CONFVAR.ibsbase.dbConnectionLossTimespan", this, e, false);
        } // catch (NumberFormatException e)
        
        // value for query result expiration
        try
        {
            // setting the QUERYFIELDRESULT_EXPIRES value
            str = this.getConfiguration ().getConfVars ().getValue ("ibsbase.queryFieldResultExpires");
            if (str != null && str.trim ().length () > 0)
            {
                DIConstants.QUERYFIELDRESULT_EXPIRES = Integer.parseInt (str);
            } // if
        } // try
        catch (NumberFormatException e)
        {
            IOHelpers.printWarning ("invalid configuration value for " +
                    "CONFVAR.ibsbase.queryFieldResultExpires", this, e, false);
        } // catch (NumberFormatException e)



        // register the function handlers:
        if (this.getFunctionCache ().size () <= 0) // no handlers registered yet?
        {
            this.writeMessage (context, "Loading values - register function handlers");

            // call the function handler register function:
            this.registerFunctionHandlers (context);
        } // if no handlers registered yet

        // get the system information:
        this.getSystemInfo (context);

        // get LanguageStringContainer_01
        LanguageStringContainer_01 lang = this.getLanguageStringContainer (context);

/*        
        try
        {

            this.writeMessage (context, " *DISABLED* - Loading values - loading message texts");

            // Messages:
            lang.tableName = LanguageStringContainer_01.tables[LanguageStringContainer_01.MESSAGES];
            lang.retrieveContent (Operations.OP_READ, 0, "");

//trace ("got messages.");

            this.writeMessage (context, " *DISABLED* - Loading values - loading exception texts");

            // Exceptions:
            lang.tableName = LanguageStringContainer_01.tables[LanguageStringContainer_01.EXCEPTIONS];
            lang.retrieveContent (Operations.OP_READ, 0, "");

//trace ("got exceptions.");

            this.writeMessage (context, " *DISABLED* - Loading values - loading token texts");

            // Tokens:
            lang.tableName = LanguageStringContainer_01.tables[LanguageStringContainer_01.TOKENS];
            lang.retrieveContent (Operations.OP_READ, 0, "");
*/
//trace ("got tokens.");

            // set the dependent properties for the loaded values:
        
        	//  commented out by gw
        	//  reason: call of empty functions
            //this.getFunctionCache ().setDependentProperties ();
/*
        } // try
*/
/*
        catch (NoAccessException e)
        {
            IOHelpers.printWarning ("loadValues", this, e, false);
//System.out.println ("got exception: " + Helpers.getStackTraceFromThrowable (e));
// KR throw further not necessary, because this error does not any harm to the
// application
//            throw new ApplicationInitializationException (e);
        } // catch
*/

        // load the buttons:
        if (this.p_app.p_buttons == null)
        {
            // build the general button bar with all possible buttons
            // make a new buttonBar element:
            this.p_app.p_buttons = new ButtonBarElement ();
            this.p_app.p_buttons.function = BOConstants.CALL_SHOWBUTTON;
            // set all possible buttons:
            this.getFunctionCache ().setButtons (this.p_app.p_buttons);
            Buttons.BTN_MAX = this.p_app.p_buttons.getMaxButtonId ();
        } // if

        // register the types:
        if (this.getTypeCache ().size () <= 0) // no types registered yet?
        {
            this.writeMessage (context, "Loading values - register types");

            // call the types register function:
            this.registerTypes (context, REGISTER_TYPES_LOAD_ALL, true);
        } // if

        // register systemqueries
        if (((QueryPool) this.p_app.queryPool).size () <= 0) // no types registered yet?
        {
            this.writeMessage (context, "Loading values - register queries");

            // call the types register function:
            this.registerQueries (REGISTER_QUERIES_LOAD_ALL, context);
        } // if

        if (((APIConnectionPool) this.p_app.apiConPool).size () <= 0) // no types registered yet?
        {
            this.writeMessage (context, "Loading values - register API connections");

            // call the types register function:
            this.registerAPIConnections (context);
        } // if

        // Preload mli texts for the client
        this.writeMessage (context, "Loading values - preloading client mli texts");
        this.preloadMliTextsForClient (context);

        // Preload mli texts for the objects
        this.writeMessage (context, "Loading values - preloading objects mli texts");
        this.preloadResourceBundles (context);
        
/* KR not used
        AppInfoSingleton singleton = AppInfoSingleton.getInstance ();
        singleton.setAppInfo (this.p_app);
*/

// DEBUG
//        this.env.write (this.app.apiConPool.toString ());
    } // loadValues


    /**************************************************************************
     * Preloads the mli texts for the client 
     *
     * @param   context     The application context.
     * 
     * @throws ApplicationInitializationException
     */
    private void preloadMliTextsForClient (IApplicationContext context) throws ApplicationInitializationException
    {
        for (int i = 0; i < MultilangConstants.CLIENT_RESOURCE_BUNDLES.length; i ++)
        {
            try
            {
                // Export texts using TextToClientExporter
                TextToClientExporter clientExporter = new TextToClientExporter();            
                Collection<String> warnings = clientExporter.
                    performExportOfTexts (MultilangConstants.CLIENT_RESOURCE_BUNDLES [i], i==0, context.getEnv ());
                
                // Check if warnings occurred 
                if(!warnings.isEmpty ())
                {
                    StringBuilder warningsSb = new StringBuilder ().
                        append ("The following resource bundle entries have not been found during preloading of client texts").
                        append (" (no text for default locale provided):\n");
                    
                    // Process each warning and print it to the screen 
                    for (Iterator<String> warnIterator = warnings.iterator (); warnIterator.hasNext ();)
                    {
                        warningsSb.append (warnIterator.next ()).append ("\n");
                    } // for
                    
                    this.writeMessage (context, warningsSb.toString ());
                } // if
            } // try
            catch (MultilingualTextProviderException e)
            {
                throw new ApplicationInitializationException (e);
            } // catch
            catch (MultilingualTextExporterException e)
            {
                throw new ApplicationInitializationException (e);
            } // catch
        } // for
    } // preloadMliTextsForClient
    
    
    /**************************************************************************
     * Preloads resource bundles.
     * 
     * All resource bundles where a fallback to another resource bundle is not
     * possible during runtime have to be preloaded here.
     *
     * @param   context     The application context.
     * 
     * @throws ApplicationInitializationException
     */
    private void preloadResourceBundles (IApplicationContext context) throws ApplicationInitializationException
    {
        for (int i = 0; i < MultilangConstants.PRELOAD_RESOURCE_BUNDLES.length; i ++)
        {
            try
            {
                // Export texts using ResourceBundlePreloader
                ResourceBundlePreloader preloader = new ResourceBundlePreloader();            
                Collection<String> warnings = preloader.
                    performExportOfTexts (MultilangConstants.PRELOAD_RESOURCE_BUNDLES [i], true, context.getEnv ());
                
                // Check if warnings occurred 
                if(!warnings.isEmpty ())
                {
                    StringBuilder warningsSb = new StringBuilder ().
                        append ("The following resource bundle entries have not been found during preloading of resource bundles").
                        append (" (no text for default locale provided):\n");
                    
                    // Process each warning and print it to the screen 
                    for (Iterator<String> warnIterator = warnings.iterator (); warnIterator.hasNext ();)
                    {
                        warningsSb.append (warnIterator.next ()).append ("\n");
                    } // for
                    
                    this.writeMessage (context, warningsSb.toString ());
                } // if
            } // try
            catch (MultilingualTextProviderException e)
            {
                throw new ApplicationInitializationException (e);
            } // catch
            catch (MultilingualTextExporterException e)
            {
                throw new ApplicationInitializationException (e);
            } // catch
        } // for
    } // preloadMliTextsForObjects


    /**************************************************************************
     * Gets the LanguageStringContainer_01 - Object of the Application. <BR/>
     *
     * @param   context     The application context.
     *
     * @return  The actual LanguageStringContainer
     */
    protected LanguageStringContainer_01 getLanguageStringContainer (
                                                                     IApplicationContext context)
    {
        LanguageStringContainer_01 lang = new LanguageStringContainer_01 ();
        OID temp = null;

        temp = OID.getEmptyOid ();
        lang.initObject (temp, null, context.getEnv (), context.getSess (), this.p_app);

        return lang;
    } // getLanguageStringContainer


    /**************************************************************************
     * Get all system information. <BR/>
     * This method instantiates the SystemLoader and uses this
     * object to get all system information out of the database.
     *
     * @param   context     The application context.
     *
     * @see ibs.app.system.SystemLoader
     *
     * @throws  ApplicationInitializationException
     *          An error occurred during loading the values.
     */
    protected void getSystemInfo (IApplicationContext context)
        throws ApplicationInitializationException
    {
        // get the system loader:
        SystemLoader loader = new SystemLoader (this.p_app.p_system);

        try
        {
            // load the system information:
            loader.load (false, true);
        } // try
        catch (ListException e)
        {
            context.write ("Error in system loader: " + e);
            throw new ApplicationInitializationException (e);
        } // catch
    } // getSystemInfo


    /**************************************************************************
     * Reloads the preloaded MLI texts for the client and other preloaded
     * resource bundles. <BR/>
     * 
     * For reloading MLI texts for types reloadTypes (appCtx, true) has to
     * be called. 
     * 
     * This method should be called when there are some mli text changes
     * during runtime.
     *
     * @param   context             The application context.
     *
     * @throws  ApplicationInitializationException
     *          An error occurred during reloading the mli client texts.
     */
    public void reloadPreloadedMliTexts (IApplicationContext context)
        throws ApplicationInitializationException
    {               
        // Preload mli texts for the client
        this.preloadMliTextsForClient (context);

        // Preload resource bundles
        this.preloadResourceBundles (context);
    } // reloadPreloadedMliTexts
    
    
    /**************************************************************************
     * Reload all types. <BR/>
     * This method should be called when there are some types changed during
     * runtime.
     *
     * @param   context             The application context.
     * @param   onlyMultilangInfo   Indicates if only the multilang info should
     *                              be reloaded.
     *
     * @throws  ApplicationInitializationException
     *          An error occurred during getting the type info.
     */
    public void reloadTypes (IApplicationContext context, boolean onlyMultilangInfo)
        throws ApplicationInitializationException
    {       
        // register the types with all information:
        this.registerTypes (context, onlyMultilangInfo ?
                REGISTER_TYPES_LOAD_JUST_MULTILANG : REGISTER_TYPES_LOAD_ALL, false);
    } // reloadTypes
    
    
    /**************************************************************************
     * Reload all queries. <BR/>
     * This method should be called when there are some queries or query
     * texts changed during runtime.
     *
     * @param   context             The application context.
     * @param   onlyMultilangInfo   Indicates if only the multilang info should
     *                              be reloaded.
     *
     * @throws  ApplicationInitializationException
     *          An error occurred during getting the type info.
     */
    public void reloadQueries (IApplicationContext context, boolean onlyMultilangInfo)
        throws ApplicationInitializationException
    {       
        // register the types with all information:
        this.registerQueries (onlyMultilangInfo ?
                REGISTER_QUERIES_LOAD_JUST_MULTILANG : REGISTER_QUERIES_LOAD_ALL, context);
    } // reloadQueries


    /**************************************************************************
     * Register all types. <BR/>
     *
     * @param   context     The application context.
     * @param   loadInfo    Defines what should be reloaded.
     *                      Possible values:
     *                      <CODE>REGISTER_TYPES_LOAD_ALL</CODE>
     *                      <CODE>REGISTER_TYPES_LOAD_JUST_EXTENDEND_INFO</CODE>
     *                      <CODE>REGISTER_TYPES_LOAD_JUST_MULTILANG</CODE>
     * @param   initMliProvider
     *                      Defines if the MLI provider should be initialized.
     *                      This can be done as soon as all types have been registered.
     *
     * @throws  ApplicationInitializationException
     *          An error occurred during getting the type info.
     */
    protected void registerTypes (IApplicationContext context, int loadInfo, boolean initMliProvider)
        throws ApplicationInitializationException
    {
        // get the type loader:
        TypeContainerLoader typeLoader =
            new TypeContainerLoader (this.getTypeCache (),
                                     this.getObjectCache (),
                                     context);

        if (loadInfo == REGISTER_TYPES_LOAD_ALL)          // register the base type info?
        {
            try
            {
                // fill the object cache with all the known types and their
                // versions with classNames:
                typeLoader.load (false, true);
            } // try
            catch (ListException e)
            {
                context.write ("Error in type loader: " + e);
                throw new ApplicationInitializationException (e);
            } // catch
        } // if register the base type info

        // register the extended info?
        if (loadInfo == REGISTER_TYPES_LOAD_ALL || loadInfo == REGISTER_TYPES_LOAD_JUST_EXTENDEND_INFO)
        {
            // get the may contain loader:
            MayContainContainerLoader mayContainLoader =
                new MayContainContainerLoader (this.getTypeCache ());
            try
            {
                // store all types which may be contained in each type within
                // that type (don't clear the type container):
                mayContainLoader.load (true, true);
            } // try
            catch (ListException e)
            {
                context.write ("Error in may contain loader: " + e);
                throw new ApplicationInitializationException (e);
            } // catch
        } // if
        
        // initialize the MLI provider ?
        if (initMliProvider)
        {
            // initialize the MultilingualTextProvider:
            this.writeMessage (context, "Initializing MultilingualTextProvider.");        
            MultilingualTextProvider.init (context.getEnv ());
        } // if
        
        // register the multilang info?
        if (loadInfo == REGISTER_TYPES_LOAD_ALL || loadInfo == REGISTER_TYPES_LOAD_JUST_MULTILANG)
        {
            // load multilang info for all types
            try
            {               
                typeLoader.loadMultilangInfo (
                    MultilingualTextProvider.getAllLocales (context.getEnv ()),
                    context.getEnv ());
            } // try
            catch (ListException e)
            {
                context.write ("List exception during loading multilang info for types: " + e);
                throw new ApplicationInitializationException (e);
            } // catch
            catch (Exception e)
            {
                context.write ("Error during loading multilang info for types: " + e);
                throw new ApplicationInitializationException (e);
            } // catch
        } // if
    } // registerTypes


    /**************************************************************************
     * Register all customizing - queries in QueryPool. <BR/>
     *
     * @param   loadInfo    Defines what should be reloaded.
     *                      Possible values:
     *                      <CODE>REGISTER_QUERIES_LOAD_ALL</CODE>
     *                      <CODE>REGISTER_QUERIES_LOAD_JUST_MULTILANG</CODE>
     * @param   context     The application context.
     *
     * @throws  ApplicationInitializationException
     *          An error occurred during loading the values.
     */
    protected void registerQueries (int loadInfo, IApplicationContext context)
        throws ApplicationInitializationException
    {
//trace ("AJ Application.registerQueries");

        // initialize querypool in ApplicationInfo
        OID temp = null;

        // create noobject oid
        temp = OID.getEmptyOid ();
        ((QueryPool) this.p_app.queryPool).initObject (temp, null, context
            .getEnv (), context.getSess (), this.p_app);

        // Load the queries into the query pool ?
        if (loadInfo == REGISTER_QUERIES_LOAD_ALL)
        {
            // try to fill querypool
            try
            {
                // load queries and multilang info
                ((QueryPool) this.p_app.queryPool).fill ();
    
            } // try
            catch (DBError e)
            {
                IOHelpers.printError ("registerQueries", this, e, true);
                IOHelpers.showMessage ("ApplicationInitializer.registerQueries", e,
                                       this.p_app, context.getSess (), context.getEnv (), false);
                throw new ApplicationInitializationException (e);
            } // catch
        } // if
        
        // Load the multilang info ?
        if (loadInfo == REGISTER_QUERIES_LOAD_JUST_MULTILANG)
        {
            // try to load the multilang info for all queries
            try
            {   
                ((QueryPool) this.p_app.queryPool).loadMultilangInfo (
                    MultilingualTextProvider.getAllLocales (
                        context.getEnv ()), 
                    context.getEnv ());
            } // try
            catch (ListException e)
            {
                context.write ("Error during loading multilang info for queries: " + e);
                throw new ApplicationInitializationException (e);
            } // catch
            catch (DBError e)
            {
                context.write ("Error during loading multilang info for queries: " + e);
                throw new ApplicationInitializationException (e);
            } // catch
        } // if
        
    } // registerQueries


    /**************************************************************************
     * Register all modules. <BR/>
     *
     * @param   context     The application context.
     *
     * @throws  ApplicationInitializationException
     *          An error occurred during loading the values.
     */
    protected void registerModules (IApplicationContext context)
        throws ApplicationInitializationException
    {
        ModuleContainer modules = this.getModuleCache ();
        ModuleContainer inactiveModules = this.getInactiveModuleCache ();
        Module[] allModules = new Module[0]; // all registered modules

        // get the loader:
        ModuleContainerLoader loader = new ModuleContainerLoader (modules,
            this.p_app.p_system.p_m2AbsBasePath + ModuleConstants.DIR_MODULES);

        try
        {
            // fill the module cache with all the known modules:
            loader.load (false, true);

            // drop the inactive modules from the module container:
            allModules = modules.toArray (allModules);
            for (int i = 0; i < allModules.length; i++)
            {
                if (!allModules[i].isActive ())
                {
                    // move the module to the inactive module container:
                    modules.move (allModules[i], inactiveModules);
                } // if
            } // for i

            // ensure that the module container is consistent:
            modules.checkConstraints ();
        } // try
        catch (ListException e)
        {
            context.write ("Error in module loader: " + e);
            throw new ApplicationInitializationException (e);
        } // catch
    } // registerModules


    /**************************************************************************
     * Register all function handlers. <BR/>
     *
     * @param   context     The application context.
     *
     * @throws  ApplicationInitializationException
     *          An error occurred during loading the values.
     */
    protected void registerFunctionHandlers (IApplicationContext context)
        throws ApplicationInitializationException
    {
        FunctionHandlerContainer functionHandlers = this.getFunctionCache ();
                                        // the function handler container

        // the function handlers are already registered within the modules
        // so what we have to do here is to get the function handlers out of
        // the modules and add them to the function handler container

        try
        {
            // loop through all modules, get their function handlers
            // and add them to the function handler container:
            for (Iterator<Module> iter = this.getModuleCache ().iterator (); iter.hasNext ();)
            {
                Module module = iter.next ();

                functionHandlers.addAll (module.p_functionHandlers);
            } // for iter

            // check constraints:
            functionHandlers.checkConstraints ();
        } // try
        catch (ListException e)
        {
            throw new ApplicationInitializationException (e);
        } // catch
/*KR not necessary, because the function handlers were already loaded into
 *the modules
        // get the loader:
        FunctionHandlerContainerLoader loader =
            new FunctionHandlerContainerLoader
                (this.getFunctionCache (),
                 this.p_app.p_system.p_m2AbsBasePath + File.separator + ModuleConstants.DIR_MODULES);

        try
        {
            // fill the object cache with all the known function handlers:
            loader.load (false);
        } // try
        catch (ListException e)
        {
            context.write ("Error in function handler loader: " + e);
            throw new ApplicationInitializationException (e);
        } // catch
*/
    } // registerFunctionHandlers


    /**************************************************************************
     * Register all configuration variables. <BR/>
     *
     * @param   context     The application context.
     *
     * @throws  ApplicationInitializationException
     *          An error occurred during loading the values.
     */
    protected void registerConfVars (IApplicationContext context)
        throws ApplicationInitializationException
    {
        ConfValueContainer confValues = null;
                                        // the config values container
        ConfVarContainer confVars = this.getConfiguration ().getConfVars ();
                                        // the config variables container
        ConfValueLoader loader;         // container loader

        // the configuration variables are already registered within the modules
        // so what we have to do here is to get the config variables out of
        // the modules and add them to the configuration variables container

        try
        {
            // loop through all modules, get their configuration variables
            // and add them to the container:
            for (Iterator<Module> iter = this.getModuleCache ().iterator (); iter.hasNext ();)
            {
                Module module = iter.next ();

                confVars.addAll (module.p_confVars);
            } // for iter

            // load the values for the configuration variables:
            confValues = new ConfValueContainer ();
            loader = new ConfValueLoader (confValues, this
                .getConfiguration ().getConfigPath ());
            loader.setFileNameFilter (ModuleConstants.FILE_CONFVALUES);
            loader.setTagName (ModuleConstants.TAG_CONFVALUE);
            loader.load (true, true);
//IOHelpers.printMessage ("    confValues: " + confValues);

            // set the values within the configuration variables:
            confVars.setValues (confValues);
            // check if there is any variable which has not value set:
            confVars.checkConstraints ();
            // sort the configuration variables:
            confVars.sort ();
        } // try
        catch (ListException e)
        {
            throw new ApplicationInitializationException (e);
        } // catch
    } // registerConfVars


    /**************************************************************************
     * Register all connections to external APIs. <BR/>
     *
     * @param   context     The application context.
     *
     * @throws  ApplicationInitializationException
     *          An error occurred during loading the values.
     */
    protected void registerAPIConnections (IApplicationContext context)
        throws ApplicationInitializationException
    {
//trace ("Application.registerAPIConnections");
        ((APIConnectionPool) this.p_app.apiConPool).initObject (OID
            .getEmptyOid (), null, context.getEnv (), context.getSess (),
            this.p_app);

        // try to fill api connection pool
        try
        {
            ((APIConnectionPool) this.p_app.apiConPool).fill ();
        } // try
        catch (DBError e)
        {
            IOHelpers.showMessage ("Application.registerAPIConnections", e,
                                   this.p_app, context.getSess (), context.getEnv (), true);
            throw new ApplicationInitializationException (e);
        } // catch
    } // registerAPIConnections


    /**************************************************************************
     * Load the reporting engine if applicable. <BR/>
     *
     * Note that this method does only know the BIRTReportingEngine at this point.
     *
     * @param   context     The application context.
     *
     * @return <code>true</code> if the reporting engine has been set or
     *         <code>false</code> otherwise
     *
     * @throws  ReportingException
     *          An error occurred during loading the reporting engine.
     */
    protected boolean loadReportingEngine (IApplicationContext context)
        throws ReportingException
    {
        // create a reporting engine object
        // Note that only the BirtReportingEngine is supported at the moment
        BIRTReportingEngine reportingEngine = new BIRTReportingEngine ();
        // load the configuration
        reportingEngine.loadConfiguration (context.getEnv (), context.getApp ());
        // check if the reporting engine is active now
        if (reportingEngine.isActivated ())
        {
            // validate the configuration
            // in case of an error a ReportingException will be thrown
            reportingEngine.validateConfiguration ();
            // in case we reach this point everything is ok
            // set the new reporting engine in the application
            this.p_app.setReportingEngine (reportingEngine);
            return true;
        } // if (reportingEngine.isActivated ())

        // reporting engine not activated
        return false;
    } // loadReportingEngine


    /**************************************************************************
     * Write a message with . <BR/>
     *
     * @param context   the application context to write the message to
     * @param message    the message to write
     */
    protected void writeMessage (IApplicationContext context, String message)
    {
        context.write ("[" +
            DateTimeHelpers.dateTimeToString (new Date (), this.TIME_FORMAT) +
            "] " + message);
    } // writeMessage


    /**************************************************************************
     * Get the object cache. <BR/>
     *
     * @return  The cache object.
     */
    protected ObjectPool getObjectCache ()
    {
        return (ObjectPool) this.p_app.cache;
    } // getObjectCache


    /**************************************************************************
     * Get the type cache. <BR/>
     *
     * @return  The cache object.
     */
    protected TypeContainer getTypeCache ()
    {
        return ((ObjectPool) this.p_app.cache).getTypeContainer ();
    } // getTypeCache


    /**************************************************************************
     * Get the tab cache. <BR/>
     *
     * @return  The cache object.
     */
    protected TabContainer getTabCache ()
    {
        return ((ObjectPool) this.p_app.cache).getTabContainer ();
    } // getTabCache


    /**************************************************************************
     * Get the function handler cache. <BR/>
     *
     * @return  The cache object.
     */
    protected FunctionHandlerContainer getFunctionCache ()
    {
        return ((ObjectPool) this.p_app.cache).getFunctionHandlerContainer ();
    } // getFunctionCache


    /**************************************************************************
     * Get the module cache. <BR/>
     *
     * @return  The cache object.
     */
    protected ModuleContainer getModuleCache ()
    {
        return ((ObjectPool) this.p_app.cache).getModuleContainer ();
    } // getModuleCache


    /**************************************************************************
     * Get the cache with inactive modules. <BR/>
     *
     * @return  The cache object.
     */
    protected ModuleContainer getInactiveModuleCache ()
    {
        return ((ObjectPool) this.p_app.cache).getInactiveModuleContainer ();
    } // getInactiveModuleCache


    /**************************************************************************
     * Get the configuration cache. <BR/>
     *
     * @return  The cache object.
     */
    protected IConfiguration getConfiguration ()
    {
        return (IConfiguration) this.p_app.configuration;
    } // getConfiguration

} // class ApplicationInitializer
