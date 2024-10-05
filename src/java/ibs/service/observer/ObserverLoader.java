/*
 * Created by IntelliJ IDEA.
 * User: hpichler
 * Date: 09.08.2002
 * Time: 11:52:34
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */

// package:
package ibs.service.observer;

// imports:
import ibs.service.observer.Observer;
import ibs.service.observer.ObserverConfiguration;
import ibs.service.observer.ObserverContext;
import ibs.service.observer.ObserverException;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;
import ibs.util.file.FileHelpers;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/******************************************************************************
 * The ObserverLoader provides methods to load, configure and start an
 * arbitrary number of Observers. Observers are identified via their name
 * and must be subclasses of the Observer-Class (a Thread-implementation).
 * It also provides an interface to register and unregister ObserverJobs for
 * given Observers.
 *
 * This class is implemented according to the singleton-pattern (means, that
 * only 1 instance of the class can be initiated per VM at a time).
 *
 * @version     $Id: ObserverLoader.java,v 1.6 2007/07/24 21:27:33 kreimueller Exp $
 *
 * @author      HORST PICHLER, 29.08.2002
 ******************************************************************************
 */
public final class ObserverLoader
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ObserverLoader.java,v 1.6 2007/07/24 21:27:33 kreimueller Exp $";


    /**
     * A static indicator to assure that class is singleton.
     */
    private static ObserverLoader p_instance = null;

    /**
     * Holds path to configuration file 'Observers.xml'.
     */
    private String p_configPath = null;
    /**
     * Holds configurations for all observers.
     */
    private Vector<ObserverConfiguration> p_configs = null;
    /**
     * Holds context for all observers.
     */
    private Vector<ObserverContext> p_contexts = null;
    /**
     * Holds all observers
     */
    private Vector<Observer> p_observers = null;
    /**
     * Holds class-names of observerjobs used in application
     */
    private Vector<String> p_observerJobClassNames = null;

    /**
     * Error prefix: error while loading configurations. <BR/>
     */
    private static final String ERRP_CONFLOADING =
        "Error while loading configurations: ";
    /**
     * Error prefix: error while creating observer configuration. <BR/>
     */
    private static final String ERRP_CONFCREATE =
        "Error while creating ObserverConfiguration: ";
    /**
     * Error prefix: error while creating observer. <BR/>
     */
    private static final String ERRP_OBSCREATE =
        "Error while creating Observer: ";

    /**
     * Error message: error in observer loader. <BR/>
     */
    private static final String ERRM_GENERAL = "Error during ObserverLoader.";
    /**
     * Error message: configuration not set. <BR/>
     */
    private static final String ERRM_CONF_NOTSET =
        "Configuration not set.";
    /**
     * Error message: error while starting observer. <BR/>
     */
    private static final String ERRM_OBSSTART =
        "Error during ObserverLoader.startObservers";
    /**
     * Error message: observers not initialized. <BR/>
     */
    private static final String ERRM_OBS_NOTINIT =
        " Observers not initialized.";
    /**
     * Error message: observer contexts not initialized. <BR/>
     */
    private static final String ERRM_OBS_CONTEXT_NOTINIT =
        " ObserverContexts not initialized.";
    /**
     * Error message: something does not exist. <BR/>
     */
    private static final String ERRM_NOTEXISTING = "' does not exist.";
    /**
     * Error message: observer does not exist. <BR/>
     */
    private static final String ERRM_OBS_NOTEXISTING =
        "Observer '" + UtilConstants.TAG_NAME + ObserverLoader.ERRM_NOTEXISTING;
    /**
     * Error message: observer configuration does not exist. <BR/>
     */
    private static final String ERRM_OBSCONF_NOTEXISTING =
        "Configuration for Observer '" + UtilConstants.TAG_NAME +
        ObserverLoader.ERRM_NOTEXISTING;
    /**
     * Error message: observer context does not exist. <BR/>
     */
    private static final String ERRM_OBSCONTEXT_NOTEXISTING =
        "Context for Observer '" + UtilConstants.TAG_NAME +
        ObserverLoader.ERRM_NOTEXISTING;

    /**
     * Reference to name of configuration. <BR/>
     */
    private static final String REF_CONF_NAME = ". config=";


    /**************************************************************************
     * Constructor for an ObserverLoader object. <BR/>
     */
    private ObserverLoader ()
    {
        // nothing to do
    } // ObserverLoader


    /**************************************************************************
     * Instantiation method that ensures that class is a singleton. <BR/>
     *
     * @return  A new instance of the loader.
     */
    public static synchronized ObserverLoader instance ()
    {
        // create and return singleton instance
        if (ObserverLoader.p_instance == null)
        {
            ObserverLoader.p_instance = new ObserverLoader ();
        } // if

        return ObserverLoader.p_instance;
    } // instance


    //
    // getter & setter
    //

    /**************************************************************************
     * Get the configuration path. <BR/>
     *
     * @return  The config path.
     */
    public String getConfigPath ()
    {
        return this.p_configPath;
    } // getConfigPath


    /**************************************************************************
     * Set the configuration path. <BR/>
     *
     * @param   configPath  The path to be set.
     */
    public void setConfigPath (String configPath)
    {
        this.p_configPath = configPath;
    } // setConfigPath


    /**************************************************************************
     * Starts all configured Observers. <BR/>
     *
     * @param   configPath  The configuration path.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    public void start (String configPath)
        throws ObserverException
    {
        // set the configuration path:
        this.p_configPath = configPath;

        // load configurations and start observers:
        this.loadConfigurations ();
        this.startObservers ();
    } // start


    /**************************************************************************
     * Stops all running Observers. <BR/>
     *
     * CAUTION: SHUTDOWN can take up to 10 seconds -- according to shut-down
     * implementation of observer. According to this it is possible that
     * some observers are still running (up to 10 more seconds) after this
     * method exited.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    public void stop ()
        throws ObserverException
    {
        // signal "stop" to every observer
        for (int i = 0; i < this.p_observers.size (); i++)
        {
            Observer observer = this.p_observers.elementAt (i);
            observer.setShouldIStop (true);
        } // for

/*
==>     DO NOT USE for-loop: bad for processor-utilization (100% utilized)
        // wait for every observer to stop!
        for (int i = 0; i < p_observers.size (); i++)
        {
            Observer observer = (Observer) p_observers.elementAt (i);
            while (observer.isAlive ())
            {
                // do nothing
            } // while
        } // for
*/

        System.out.println (">>>>> OBSERVERLOADER: SIGNALED 'STOP' TO ALL OBSERVERS.");
    } // stop


    /**************************************************************************
     * Restarts (stops & starts) all Observers. In contrast to 'start' the
     * configuration-file will not be loaded again. <BR/>
     *
     * Important: This method can take some time, because it waits until
     * all observers are stopped before starting them again. Stop-delay can
     * be up to 10 seconds.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    public void restart ()
        throws ObserverException
    {
        this.stop ();

        // wait for every observer to stop!
        for (int i = 0; i < this.p_observers.size (); i++)
        {
            Observer observer = this.p_observers.elementAt (i);
            while (observer.isAlive ())
            {
/*
                try
                {
                    Thread.sleep (500);
                } // try
                catch (InterruptedException e)
                {
                    throw new ObserverException ("Error while waiting for observer to stop. ");
                } // catch
*/
            } // while
        } // for

        // try to restart observers
        for (int i = 0; i < this.p_observers.size (); i++)
        {
            Observer observer = this.p_observers.elementAt (i);
            observer.start ();
        } // for

        System.out.println (">>>>> OBSERVERLOADER: RESTARTED OBSERVERS.");
    } // restart


    /**************************************************************************
     * Starts given Observer. <BR/>
     *
     * CAUTION: SHUTDOWN can take up to 10 seconds -- according to shut-down
     * implementation of observer. According to this it is possible that
     * the observer are still running (up to 10 more seconds) after this
     * method exited.
     *
     * @param   obsName The name of the observer to be started.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    public void startObserver (String obsName)
        throws ObserverException
    {
        Observer obs = this.getObserver (obsName);
        obs.start ();
        System.out.println (">>>>> OBSERVERLOADER: STARTED OBSERVER '" + obsName + "'.");
    } // stop


    /**************************************************************************
     * Stops given Observer. <BR/>
     *
     * CAUTION: SHUTDOWN can take up to 10 seconds -- according to shut-down
     * implementation of observer. According to this it is possible that
     * the observer are still running (up to 10 more seconds) after this
     * method exited.
     *
     * @param   obsName The name of the observer to be stopped.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    public void stopObserver (String obsName)
        throws ObserverException
    {
        Observer obs = this.getObserver (obsName);
        obs.setShouldIStop (true);
        System.out.println (">>>>> OBSERVERLOADER: SIGNALED 'STOP' TO OBSERVER '" + obsName + "'.");
    } // stop


    /**************************************************************************
     * Restarts a given Observer. The configuration-file will not be loaded
     * again. <BR/>
     *
     * Important: This method can take some time, because it waits until
     * observer has been stopped before starting it again. Stop-delay can
     * be up to 10 seconds.
     *
     * @param   obsName The name of the observer to be restarted.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    public void restartObserver (String obsName)
        throws ObserverException
    {
        this.stopObserver (obsName);

        // wait for observer to stop!
        Observer obs = this.getObserver (obsName);

        // wait until observer stopped
        while (obs.isAlive ())
        {
/*
            try
            {
                this.wait (500);
            } // try
            catch (InterruptedException e)
            {
                throw new ObserverException ("Error while waiting for observer to stop. ");
            } // catch
*/
        } // while

        // restart observer
        obs.start ();

        System.out.println (">>>>> OBSERVERLOADER: RESTARTED OBSERVER '" + obsName + "'.");
    } // restartObserver


    /**************************************************************************
     * Load configuration file, interpret the attribute CONFIGURATIONCLASS
     * of the OBSERVER-tag and store data in (sub)class (of)
     * ObserverConfiguration. <BR/>
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected void loadConfigurations ()
        throws ObserverException
    {
        String fileName = null;

        this.p_configs = new Vector<ObserverConfiguration> ();
        this.p_contexts = new Vector<ObserverContext> ();
        this.p_observers = new Vector<Observer> ();

        // check initialization
        if (this.p_configPath == null || this.p_configPath.length () == 0)
        {
            throw new ObserverException (ObserverLoader.ERRP_CONFLOADING +
                "Given configPath not initialized.");
        } // if
        if (this.p_configs == null)
        {
            throw new ObserverException (ObserverLoader.ERRP_CONFLOADING +
                ObserverLoader.ERRM_CONF_NOTSET);
        } // if
        if (this.p_contexts == null)
        {
            throw new ObserverException (ObserverLoader.ERRP_CONFLOADING +
                ObserverLoader.ERRM_OBS_CONTEXT_NOTINIT);
        } // if
        if (this.p_observers == null)
        {
            throw new ObserverException (ObserverLoader.ERRP_CONFLOADING +
                ObserverLoader.ERRM_OBS_NOTINIT);
        } // if

        if (!this.p_configPath.endsWith (File.separator))            // should add trailing separator?
        {
            this.p_configPath += File.separator;
        } // if path ends with separator

        fileName = this.p_configPath;

        this.loadConfigurations (fileName);
    } // loadConfigurations


    /**************************************************************************
     * Load configuration file, interpret the attribute CONFIGURATIONCLASS
     * of the OBSERVER-tag and store data in (sub)class (of)
     * ObserverConfiguration. <BR/>
     *
     * @param   fileName    The name of the file from which to load the
     *                      configuration.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected void loadConfigurations (String fileName)
        throws ObserverException
    {
        NamedNodeMap nm = null;
        Node attrConfigClass = null;
        Node n = null;
        String value = null;
        String sConfigClass = null;

        if (FileHelpers.exists (fileName))              // given file exists?
        {
            try
            {
                // create DOM-parser and parse file (incl. dtd-validation) --> dom-tree
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance ();
                dbf.setValidating (true);    // check document against dtd
                DocumentBuilder db = dbf.newDocumentBuilder ();
                Document doc = db.parse (new File (fileName));

                // get all OBSERVERJOBCLASS-elements
                NodeList nodes = doc.getElementsByTagName (ObserverConfiguration.TAG_OBSERVERJOBCLASS);
                if (nodes.getLength () > 0)  // does at least 1 exist?
                {
                    this.p_observerJobClassNames = new Vector<String> ();
                    for (int i = 0; i < nodes.getLength (); i++)
                    {
                       // get next OBSERVERJOBCLASS-element
                        n = nodes.item (i);
                        value = ObserverConfiguration.getNodeText (n, "OBSERVER:OBSERVERJOBCLASS", false);
                        this.p_observerJobClassNames.add (value);
                    } // for
                } // if no element

                // get all OBSERVER-elements
                nodes = doc.getElementsByTagName (ObserverConfiguration.TAG_OBSERVER);
                if (nodes.getLength () < 1)  // does at least 1 exist?
                {
                    throw new ObserverException (ObserverLoader.ERRP_CONFLOADING +
                        "No OBSERVER-element found.");
                } // if no element

                // iterate trough OBSERVER-elements
                for (int i = 0; i < nodes.getLength (); i++)
                {
                    // get next OBSERVER-element
                    n = nodes.item (i);

                    // get attribute CONFIGURATIONCLASS of OBSERVER-element
                    nm = n.getAttributes ();
                    if (nm == null)  // exists?
                    {
                        throw new ObserverException (ObserverLoader.ERRP_CONFLOADING +
                            "No OBSERVER-attribute found.");
                    } // if
                    attrConfigClass = nm.getNamedItem (ObserverConfiguration.ATTR_CONFIGURATIONCLASS);
                    if (attrConfigClass == null)
                    {
                        throw new ObserverException (ObserverLoader.ERRP_CONFLOADING +
                            "OBSERVER-attribute CONFIGCLASS not set.");
                    } // if
                    sConfigClass = attrConfigClass.getNodeValue ();

                    // instantiate class and read configuration
                    try
                    {
                        @SuppressWarnings ("unchecked") // suppress compiler warning
                        Class<? extends ObserverConfiguration> cl =
                            (Class<? extends ObserverConfiguration>) Class.forName (sConfigClass);
                        ObserverConfiguration oc = cl.newInstance ();
                        oc.setConfigurationData (n);

                        // check if name given in configuration is unique!
                        boolean proceed = false;
                        try
                        {
                            this.getObserverConfiguration (oc.getName ());
                        } // try
                        catch (ObserverException e)
                        {
                            // exception occurred --> name already exists
                            proceed = true;
                        } // catch

                        // create not-unique-exception
                        if (!proceed)
                        {
                            throw new ObserverException (ObserverLoader.ERRP_CONFCREATE +
                                "Observers name " + oc.getName () + " is not unique.");
                        } // if

                        // add objects to lists
                        this.p_configs.addElement (oc);
                        this.p_contexts.addElement (oc.createObserverContext ());
                    } // catch
                    catch (ClassNotFoundException e)
                    {
                        throw new ObserverException (ObserverLoader.ERRP_CONFCREATE +
                                e.toString ());
                    } // catch
                    catch (InstantiationException e)
                    {
                        throw new ObserverException (ObserverLoader.ERRP_CONFCREATE +
                                e.toString ());
                    } // catch
                    catch (IllegalAccessException e)
                    {
                        throw new ObserverException (ObserverLoader.ERRP_CONFCREATE +
                                e.toString ());
                    } // catch
                } // for
            } // try
            catch (ParserConfigurationException e)
            {
                throw new ObserverException (ObserverLoader.ERRP_CONFLOADING + e.toString ());
            } // catch
            catch (SAXException e)
            {
                throw new ObserverException (ObserverLoader.ERRP_CONFLOADING + e.toString ());
            } // catch
            catch (IOException e)
            {
                throw new ObserverException (ObserverLoader.ERRP_CONFLOADING + e.toString ());
            } // catch
        } // if
        else
        {
            throw new ObserverException ("Could not find configuration-file: " + fileName + ".");
        } // else
    } // loadConfigurations


    /**************************************************************************
     * Start the currently known observers. <BR/>
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected void startObservers ()
        throws ObserverException
    {
        ObserverConfiguration conf = null;

        // check initialization
        if (this.p_configs == null)
        {
            throw new ObserverException (ObserverLoader.ERRM_OBSSTART +
                ObserverLoader.ERRM_CONF_NOTSET);
        } // if
        if (this.p_observers == null)
        {
            throw new ObserverException (ObserverLoader.ERRM_OBSSTART +
                ObserverLoader.ERRM_OBS_NOTINIT);
        } // if

        // loop through observer-configuration and start each of them
        for (int i = 0; i < this.p_configs.size (); i++)
        {
            conf = this.p_configs.elementAt (i);

            // add job class names to configuration
            conf.addObserverJobClassNames (this.p_observerJobClassNames);

            //  create observer job object of given type and set values
            try
            {
                @SuppressWarnings ("unchecked") // suppress compiler warning
                Class<? extends Observer> cl =
                    (Class<? extends Observer>) Class.forName (conf.getClassName ());
                Observer obs = cl.newInstance ();
                obs.init (conf);
                this.p_observers.addElement (obs);
                obs.start ();
            } // catch
            catch (ClassNotFoundException e)
            {
                throw new ObserverException (ObserverLoader.ERRP_OBSCREATE +
                    e.toString () + ObserverLoader.REF_CONF_NAME +
                    conf.toString ());
            } // catch
            catch (InstantiationException e)
            {
                throw new ObserverException (ObserverLoader.ERRP_OBSCREATE +
                    e.toString () + ObserverLoader.REF_CONF_NAME +
                    conf.toString ());
            } // catch
            catch (IllegalAccessException e)
            {
                throw new ObserverException (ObserverLoader.ERRP_OBSCREATE +
                    e.toString () + ObserverLoader.REF_CONF_NAME +
                    conf.toString ());
            } // catch
        } // for

        if (this.p_observers == null || this.p_observers.size () == 0)
        {
            System.out.println (">>>>> OBSERVERLOADER: NO OBSERVERS CONFIGURED.");
        } // if
        else
        {
            System.out.println (">>>>> OBSERVERLOADER: STARTED " + this.p_observers.size () +  " OBSERVERS.");
        } // else
    } // startObservers


    /**************************************************************************
     * Gets observer by given name. <BR/>
     *
     * @param   name    The name of the observer to be found.
     *
     * @return  The found observer.
     *
     * @throws  ObserverException
     *          An error occurred. No observer found.
     */
    private Observer getObserver (String name)
        throws ObserverException
    {
        this.checkInitialization ("getObserver");

        Observer obs = null;

        for (int i = 0; i < this.p_observers.size (); i++)
        {
            obs = this.p_observers.elementAt (i);
            if (obs.getName ().equalsIgnoreCase (name))
            {
                return obs;
            } // if
        } // for

        // otherwise
        throw new ObserverException (StringHelpers.replace (ObserverLoader.ERRM_OBS_NOTEXISTING, UtilConstants.TAG_NAME, name));
    } // startObservers


    /**************************************************************************
     * Gets ObserverConfiguration by given name. <BR/>
     *
     * @param   name    The name of the observer for which to get the
     *                  configuration.
     *
     * @return  The observer configuration.
     *
     * @throws  ObserverException
     *          An error occurred. Configuration not found.
     */
    public ObserverConfiguration getObserverConfiguration (String name)
        throws ObserverException
    {
        this.checkInitialization ("getObserverConfiguration");

        ObserverConfiguration conf = null;
        for (Iterator<ObserverConfiguration> iter = this.p_configs.iterator (); iter.hasNext ();)
        {
            conf = iter.next ();

            if (conf.getName ().equalsIgnoreCase (name))
            {
                return conf;
            } // if
        } // for iter

        // otherwise
        throw new ObserverException (StringHelpers.replace (ObserverLoader.ERRM_OBSCONF_NOTEXISTING, UtilConstants.TAG_NAME, name));
    } // getObserver


    /**************************************************************************
     * Gets ObserverContext by given name. <BR/>
     *
     * @param   name    The name of the observer for which to get the context.
     *
     * @return  The observer context.
     *
     * @throws  ObserverException
     *          An error occurred. Context not found.
     */
    public ObserverContext getObserverContext (String name)
        throws ObserverException
    {
        this.checkInitialization ("getObserverContext");

        ObserverContext cont = null;
        for (Iterator<ObserverContext> iter = this.p_contexts.iterator (); iter.hasNext ();)
        {
            cont = iter.next ();

            if (cont.getName ().equalsIgnoreCase (name))
            {
                return cont;
            } // if
        } // for iter

        // otherwise
        throw new ObserverException (StringHelpers.replace (ObserverLoader.ERRM_OBSCONTEXT_NOTEXISTING, UtilConstants.TAG_NAME, name));
    } // getObserver


    /**************************************************************************
     * Provides a list of names. <BR/>
     *
     * @return  The names of all known observers.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    public Vector<String> getObserverNames ()
        throws ObserverException
    {
        this.checkInitialization ("getObserverNames");

        Vector<String> v = new Vector<String> ();
        for (int i = 0; i < this.p_configs.size (); i++)
        {
            v.addElement (this.p_configs.elementAt (i).getName ());
        } // for

        return v;
    } // getObserverNames


    /**************************************************************************
     * Provides a list of classnames. <BR/>
     *
     * @return  The class names for all known observers.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    public Vector<String> getObserverClassNames ()
        throws ObserverException
    {
        this.checkInitialization ("getObserverClassNames");

        Vector<String> v = new Vector<String> ();
        for (int i = 0; i < this.p_configs.size (); i++)
        {
            v.addElement (this.p_configs.elementAt (i).getClassName ());
        } // for

        return v;
    } // getObserverNames


    /**************************************************************************
     * Check if Observer with given name is up and running. <BR/>
     *
     * @param   name    The name of the observer to be checked.
     *
     * @return  <CODE>true</CODE> if the observer is up and running,
     *          <CODE>false</CODE> otherwise.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    public boolean checkAlive (String name)
        throws ObserverException
    {
        this.checkInitialization ("checkAlive");

        Observer obs = null;
        for (int i = 0; i < this.p_observers.size (); i++)
        {
            obs = this.p_observers.elementAt (i);
            if (obs.getName ().equalsIgnoreCase (name))
            {
                return obs.isAlive ();
            } // if
        } // for i

        // otherwise
        throw new ObserverException (StringHelpers.replace (ObserverLoader.ERRM_OBS_NOTEXISTING, UtilConstants.TAG_NAME, name));
    } // checkActive


    /**************************************************************************
     * Check the initializiation for a specific operation. <BR/>
     *
     * @param   op      The operation to be checked.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    private final void checkInitialization (String op)
        throws ObserverException
    {
        if (this.p_configs == null)
        {
            throw new ObserverException (ObserverLoader.ERRM_GENERAL + op +
                ObserverLoader.ERRM_CONF_NOTSET);
        } // if
        if (this.p_observers == null)
        {
            throw new ObserverException (ObserverLoader.ERRM_GENERAL + op +
                ObserverLoader.ERRM_OBS_NOTINIT);
        } // if
        if (this.p_contexts == null)
        {
            throw new ObserverException (ObserverLoader.ERRM_GENERAL + op +
                ObserverLoader.ERRM_OBS_CONTEXT_NOTINIT);
        } // if
    } // checkInitialization

} // ObserverLoader
