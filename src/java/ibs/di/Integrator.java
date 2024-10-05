/*
 * Class: Integrator.java
 */

// package:
package ibs.di;

// imports:
//KR TODO: unsauber
import ibs.bo.BOArguments;
import ibs.bo.BOHelpers;
import ibs.bo.BOMessages;
import ibs.bo.BusinessObject;
import ibs.bo.Container;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.bo.Operations;
import ibs.bo.States;
import ibs.bo.type.ITypeContainer;
import ibs.bo.type.Type;
import ibs.bo.type.TypeConstants;
import ibs.bo.type.TypeContainer;
import ibs.di.connect.Connector_01;
import ibs.di.connect.FileConnector_01;
import ibs.di.filter.Filter;
import ibs.di.filter.m2XMLFilter;
import ibs.di.imp.ImportScript_01;
import ibs.di.trans.Translator_01;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.service.notification.INotificationService;
import ibs.service.notification.NotificationFailedException;
import ibs.service.notification.NotificationServiceFactory;
import ibs.service.notification.NotificationTemplate;
import ibs.service.user.User;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;
import ibs.util.DateTimeHelpers;
import ibs.util.list.ListException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * The Integrator class is used to register the components needed to do an
 * import or an export. It supports set methods in order to register:
 * <UL>
 * <LI>the connector</LI>
 * <LI>the translator</LI>
 * <LI>the filter</LI>
 * </UL>
 *
 * @version     $Id: Integrator.java,v 1.59 2010/11/12 10:17:53 btatzmann Exp $
 *
 * @author      Buchegger Bernd (BB), 991008
 ******************************************************************************
 */
public abstract class Integrator extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Integrator.java,v 1.59 2010/11/12 10:17:53 btatzmann Exp $";


    /**
     * flag to show the integrator settings. <BR/>
     */
    public boolean isShowSettings = true;

    /**
     * flag to get settings from environment. <BR/>
     */
    public boolean isGetSettingsFromEnv = true;

    /**
     * flag to generate HTML as output. this flag will be set
     * by agents that only need the text output. <BR/>
     */
    public boolean isGenerateHtml = true;

    /**
     * flag to get the settings from an interface object. <BR/>
     */
    public boolean isInterfaceUsed = false;

    /**
     * option to return an xml response after the integration process. <BR/>
     * This option deactivates the p_isShowSettings and the
     * isGenerateHtml option. <BR/>
     */
    public boolean p_isGenerateXMLResponse = false;

    /**
     * option to include the log in the xml response. <BR/>
     */
    public boolean p_isIncludeLog = true;

    /**
     * flag to send a notification to the user in case an error occurred. <BR/>
     */
    public boolean p_isSendErrorNotification = false;

    /**
     * Email Sender for an error notification. <BR/>
     */
    public String p_errorNotificationSender = null;

    /**
     * Email receiver for an error notification. <BR/>
     * In case a receiver has been set, the notification will be done via email. <BR/>
     */
    public String p_errorNotificationReceiver = null;

    /**
     * Subject for an error notification. <BR/>
     */
    public String p_errorNotificationSubject = null;

     /**
     * Id of the filter. <BR/>
     */
    public int filterId = 0;

    /**
     * Oid of the translator. <BR/>
     */
    public OID translatorOid;

    /**
     * Oid of the connector. <BR/>
     */
    public OID connectorOid;

    /**
     * Oid of the backup connector. <BR/>
     */
    public OID p_backupConnectorOid;

    /**
     * Oid of the interface. <BR/>
     */
    public OID interfaceOid;

    /**
     *  the filter object handles the export and import formats
     */
    public Filter filter = null;

    /**
    * flag if the log should be displayed. <BR/>
    */
    public boolean p_isDisplayLog = false;

    /**
    * flag if the log should be saved. <BR/>
    */
    public boolean p_isSaveLog = false;

    /**
    * flag if the new log should be added to the already existing log. <BR/>
    */
    public boolean p_isAddtoLog = false;

    /**
    * Name of the logfile. <BR/>
    */
    public String p_logFileName = "";

    /**
    * Path of the logfile. <BR/>
    */
    public String p_logFilePath = "";

    /**
     * Absolute base path of the m2 system. <BR/>
     */
    protected String m2AbsBasePath = "";

    /**
     * The Connector object handles the import and export channels. <BR/>
     */
    protected Connector_01 connector = null;

    /**
     * The Connector object handles the backup of the import and export
     * channels. <BR/>
     */
    protected Connector_01 p_backupConnector = null;

    /**
     *  the translator object handles format translations
     */
    protected Translator_01 translator = null;

    /**
     *  The log object that will hold the log entries
     */
    public Log_01 log = null;

    /**
     *  the objectFactory instance to create/change objects<BR/>
     */
    protected ObjectFactory objectFactory = null;

    /**
     * Flag to use a connector. <BR/>
     */
    protected boolean isUseConnector = true;

    /**
     * Flag to use a backup connector. <BR/>
     */
    protected boolean p_isCreateBackup = true;

    /**
     * flag to mark if the integration process was successfull not not
     */
    protected boolean p_isSuccessful = false;

    /**
     * Time stamp of integrator. <BR/>
     */
    protected String p_timestamp = DateTimeHelpers.getTimestamp ();

    /**
     * A response object.<BR/>
     */
    protected Response p_response = null;


    /**************************************************************************
     * Creates an Integrator Object. <BR/>
     */
    public Integrator ()
    {
        super ();
    } // Integrator


    /**************************************************************************
     * Creates an Integrator Object. <BR/>
     *
     * @param oid   oid of the object
     * @param user  user that created the object
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    @Deprecated
    public Integrator (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
    } // ExportIntegrator


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // no specifics
    } // initClassSpecifics


    /**************************************************************************
     * Register a connector object. <BR/>
     *
     * @param connector     the connector object to be registered
     */
    public void setConnector (Connector_01 connector)
    {
        this.connector = connector;
    } // setConnector


    /**************************************************************************
     * Register a filter object. <BR/>
     *
     * @param filter        the filter object to be registered
     */
    public void setFilter (Filter filter)
    {
        this.filter = filter;
    } // setFilter


    /**************************************************************************
     * Registers the translator object. <BR/>
     *
     * @param translator    the translator object to be registered
     */
    public void setTranslator (Translator_01 translator)
    {
        this.translator = translator;
    } // setTranslator


    /**************************************************************************
     * Set a log object. <BR/>
     *
     * @param log   the log object to set
     */
    public void setLog (Log_01 log)
    {
        this.log = log;
    } // setLog


    /**************************************************************************
     * Sets the m2AbsbasePath. This is the absolute file path to the m2
     * system directories and is stored in the session object.
     *
     * @param m2AbsBasePath     the m2AbsBasePath
     */
    public void setM2AbsBasePath (String m2AbsBasePath)
    {
        this.m2AbsBasePath = m2AbsBasePath;
    } // setM2AbsBasePath


    /**************************************************************************
     * Sets the isGenerateHtml flag. This flag suppresses the generation
     * of HTML output and forces the output of standard ASCII text.
     * This flag will be set by agents that only need text output. <BR/>
     *
     * @param isGenerateHtml    thw flag is HTML output should be generated or text
     *                          instead
     */
    public void setGenerateHTML (boolean isGenerateHtml)
    {
        this.isGenerateHtml = isGenerateHtml;
    } // setGenerateHTML


    /**************************************************************************
     * Sets the isDiplayLog flag of the log object. <BR/>
     *
     * @param isDisplayLog      send log messages to the display
     */
    public void setDisplayLog (boolean isDisplayLog)
    {
        this.p_isDisplayLog = isDisplayLog;
    } // setDisplayLog


    /**************************************************************************
     * Setter for the IsXMLResponse option. <BR/>
     *
     * @param   isGenerateXMLResponse   Option to generate an XML response.
     */
    public void setIsGenerateXMLResponse (boolean isGenerateXMLResponse)
    {
        this.p_isGenerateXMLResponse = isGenerateXMLResponse;
    } // setIsGenerateXMLResponse


    /**************************************************************************
     * Returns the value of the isSuccessFull property that indicated if an
     * integration process terminated successfully. <BR/>
     *
     * @return  the value of the isSuccessFull property that indicated if an
     *          integration process terminated successfully
     */
    public boolean getIsSuccessful ()
    {
        return this.p_isSuccessful;
    } // getIsSuccessful


    /**************************************************************************
     * Returns the log object. <BR/>
     *
     * @return the log object
     */
    public Log_01 getLog ()
    {
        return this.log;
    } // getLog


    /**************************************************************************
     * Initialises the log. Creates a new log object and sets the environment.
     * Additionally the m2AbsBasePath will be set in order to ensure the
     * correct log default path. <BR/>
     */
    public void initLog ()
    {
        this.log = new Log_01 ();
        this.log.initObject (OID.getEmptyOid (), this.user, this.env, this.sess, this.app);
        this.log.setM2AbsBasePath (this.m2AbsBasePath);
        this.log.isGenerateHtml = this.isGenerateHtml;
    } // resetLog


    /**************************************************************************
     * Returns the objectFactory instance. <BR/>
     *
     * @return the ObjectFactory instance set in the integrator
     */
    protected ObjectFactory getObjectFactory ()
    {
        if (this.objectFactory == null)
        {
            // initialize the object factory instance
            this.initObjectFactory ();
        } // if (this.objectFactory == null)
        return this.objectFactory;
    } // getObjectFactory


    /**************************************************************************
     * Creates an ObjectFactory object and sets the environment. <BR/>
     * The method first checks if an object factory has already been
     * created. If not it creates  a new object factory instance
     * and sets the log and the connector. <BR/>
     */
    protected void initObjectFactory ()
    {
        if (this.objectFactory == null)
        {
            // create the instance
            this.objectFactory = new ObjectFactory ();
            // init the object
            this.objectFactory.initObject (OID.getEmptyOid (), this.user,
                                           this.env, this.sess, this.app);
            // set the absolute  m2 base path
            this.objectFactory.setM2AbsBasePath (this.m2AbsBasePath);
            // set the log
            this.objectFactory.setLog (this.log);
            // set the connector
            this.objectFactory.setConnector (this.connector);
        } // if (this.objectFactory == null)
    } // initObjectFactory


    /**************************************************************************
     * Returns the default filter. The default filter is the m2XMLFilter. <BR/>
     *
     * @return an m2XMLFilter object
     */
    protected Filter getDefaultFilter ()
    {
        m2XMLFilter filter = new m2XMLFilter ();
        filter.initObject (OID.getEmptyOid (), this.user, this.env, this.sess,
            this.app);
        return filter;
    } // getDefaultFilter


    /**************************************************************************
     * Returns the default connector object. This is a fileconnector. <BR/>
     *
     * @return  The FileConnector object.
     */
    protected Connector_01 getDefaultConnector ()
    {
        FileConnector_01 connector = new FileConnector_01 ();
        connector.initObject (OID.getEmptyOid (), this.user, this.env,
            this.sess, this.app);
        return connector;
    } // getDefaultConnector


    /**************************************************************************
     * Returns the default connector object. <BR/>
     * This method initializes the path of the connector with a specific value.
     *
     * @param   path    Path to be set in the connector.
     *
     * @return  The Connector object.
     */
    protected Connector_01 getDefaultConnector (String path)
    {
        Connector_01 connector = this.getDefaultConnector ();
        if (connector != null)
        {
            connector.setPath (path);
        } // if
        return connector;
    } // getDefaultConnector


    /**************************************************************************
     * Init the filter. Sets the session and the environment.
     * If no filter has been set yet a m2XMLExportFilter will be set as default.
     * <BR/>
     */
    protected void initFilter ()
    {
        // check if a filter has been set
        if (this.filter == null)
        {
            // set the m2ImportFilter as default
            this.filter = this.getDefaultFilter ();
        } // if (this.filter == null)
        else    // set the environment
        {
            this.filter.initObject (OID.getEmptyOid (), this.user, this.env, this.sess, this.app);
        } // else set the environment
    } // initFilter


    /**************************************************************************
     * Get an object which is defined through its oid. <BR/>
     *
     * @param   oid     The oid of the object.
     *
     * @return  The object if it was found or
     *          <CODE>null</CODE> otherwise.
     */
    public BusinessObject getObjectFromOid (OID oid)
    {
        // first check if we got a valid oid value:
        if (oid == null || oid.isEmpty ())
        {
            return null;
        } // if

        return this.getObjectFactory ().getObject
            (oid, this.user, this.sess, this.env);
    } // getObjectFromOid


    /**************************************************************************
     * Creates a Connector object from an oid. <BR/>
     *
     * BB HINT: new connector types must be registered here!!!
     *
     * @param   oid     The oid of the connector.
     *
     * @return  The connector object if it was found or
     *          <CODE>null</CODE> otherwise.
     */
    public Connector_01 createConnectorFromOid (OID oid)
    {
        Connector_01 connector;

        // get the connector instance:
        connector = (Connector_01) this.getObjectFromOid (oid);

        // check if we could create the object
        if (connector == null)
        {
            return null;
        } // if (connector == null)
/* KR 020125: not necessary because already done before
        // get the data out of the database
        connector.retrieve (Operations.OP_VIEW);
*/
        // set the arguments
        connector.setArguments ();
        // set the m2 abs base path in order to be able to create
        // a temp directory if necessary
        connector.setm2AbsBasePath (this.m2AbsBasePath);
        return connector;
/* KR 020125: not necessary because already done before
        catch (NoAccessException e)
        {
            showNoAccessMessage (Operations.OP_VIEW);
            return null;
        } // catch
        catch (AlreadyDeletedException e)
        {
            showAlreadyDeletedMessage ();
            return null;
        } // catch
*/
    } // createConnectorFromOid


    /**************************************************************************
     * Creates a Connector object from an oid. <BR/>
     * Remark: it is preferred to use
     * {@link #createTranslatorFromOid(OID) createTranslatorFromOid(OID)}
     * instead of this method. <BR/>
     * BB HINT: new connector types must be registered here!!!
     *
     * @param   oidStr  The oid string of the connector.
     *
     * @return  The connector object if it was found or
     *          <CODE>null</CODE> otherwise.
     */
    public Connector_01 createConnectorFromOid (String oidStr)
    {
        // first check if we got a valid oid value:
        if (oidStr == null || oidStr.equals (OID.EMPTYOID) || oidStr.length () == 0)
        {
            return null;
        } // if (oidStr == null || oidStr == BOConstants.OID_NOOBJECT)

        // oid is set
        try
        {
            return this.createConnectorFromOid (new OID (oidStr));
        } // try
        catch (IncorrectOidException e)
        {
            this.showIncorrectOidMessage (oidStr);
            return null;
        } // catch
    } // createConnectorFromOid


    /**************************************************************************
     * Creates a Translator object from an oid. <BR/>
     *
     * @param   oid     The oid of the translator.
     *
     * @return  The translator object if it was found or
     *          <CODE>null</CODE> otherwise.
     */
    public Translator_01 createTranslatorFromOid (OID oid)
    {
        Translator_01 translator;

        // oid is set
        // get the translator instance:
        translator = (Translator_01) this.getObjectFromOid (oid);

        // check if we got a translator object:
        if (translator == null)
        {
            return null;
        } // if
// KR 020125: not necessary because already done before
// BB 020214: this is not correct. When ignoring the retrieve
//            the containerId will be set to a wrong value causing the
//            translator to crash with a SAXException because it cannot
//            read the XSLT file.
// KR 040821: again the same thing: not necessary because already done in
//            getObject above.
/*
            // get the data out of the database
            translator.retrieve (Operations.OP_VIEW);
*/

        return translator;
/*
        catch (NoAccessException e)
        {
            showNoAccessMessage (Operations.OP_VIEW);
            return null;
        } // catch
        catch (AlreadyDeletedException e)
        {
            showAlreadyDeletedMessage ();
            return null;
        } // catch
        catch (ObjectNotFoundException e)
        {
            showObjectNotFoundMessage ();
            return null;
        } // catch
*/
    } // createTranslatorFromOid


    /**************************************************************************
     * Creates a Translator object from an oid. <BR/>
     * Remark: it is preferred to use
     * {@link #createTranslatorFromOid(OID) createTranslatorFromOid(OID)}
     * instead of this method.
     *
     * @param   oidStr  The oid string of the translator.
     *
     * @return  The Translator object if it was found or
     *          <CODE>null</CODE> otherwise.
     */
    public Translator_01 createTranslatorFromOid (String oidStr)
    {
        // first check if we got a valid oid value:
        if (oidStr == null || oidStr.equals (OID.EMPTYOID) || oidStr.length () == 0)
        {
            return null;
        } // if (oidStr == null || oidStr == BOConstants.OID_NOOBJECT)

        // oid is set
        // get the translator object:
        try
        {
            return this.createTranslatorFromOid (new OID (oidStr));
        } // try
        catch (IncorrectOidException e)
        {
            this.showIncorrectOidMessage (oidStr);
            return null;
        } // catch
    } // createTranslatorFromOid


    /**************************************************************************
     * Creates an Importscript object from an oid. <BR/>
     *
     * @param   oid     The oid the importscript.
     *
     * @return  The importScript object if it was found or
     *          <CODE>null</CODE> otherwise.
     */
    protected ImportScript_01 createImportScriptFromOid (OID oid)
    {
        ImportScript_01 importScript;

        // get the importscript instance:
        importScript = (ImportScript_01) this.getObjectFromOid (oid);

        // check if we could create the object:
        if (importScript != null)
        {
            // init the importScript and check if sucessful:
            if (importScript.init ())
            {
                return importScript;
            } // if
        } // if (connector == null)

        // return error indicator:
        return null;
    } // createImportScriptFromOid


    /**************************************************************************
     * Creates an Importscript object from an oid. <BR/>
     * Remark: it is preferred to use
     * {@link #createImportScriptFromOid(OID) createImportScriptFromOid(OID)}
     * instead of this method.
     *
     * @param   oidStr  The oid string of the importscript.
     *
     * @return  The importScript object if its was found or
     *          <CODE>null</CODE> otherwise.
     */
    protected ImportScript_01 createImportScriptFromOid (String oidStr)
    {
        // first check if we got a valid oid value:
        if (oidStr == null || oidStr.equals (OID.EMPTYOID) || oidStr.length () == 0)
        {
            return null;
        } // if (oidStr == null || oidStr == BOConstants.OID_NOOBJECT)

        // oid is set
        // get the importScript Object:
        try
        {
            return this.createImportScriptFromOid (new OID (oidStr));
        } // try
        catch (IncorrectOidException e)
        {
            this.showIncorrectOidMessage (oidStr);
            return null;
        } // catch
    } // createImportScriptFromOid


    /**************************************************************************
     * Get the OID of an object with a specific name and a specific objecttype.
     * <BR/>
     * In case there has been more then one object found or the object
     * could not be found at all the method will return null.
     *
     * @param   objectType  the specific objecttype we look for
     * @param   name        the name of the object we look for
     *
     * @return the oid of the object found or null otherwise
     */
    public OID getOidFromObjectName (int objectType, String name)
    {
        int [] objectTypes = {objectType};
        return this.getOidFromObjectName (objectTypes, name);
    } // getOidFromObjectName


    /**************************************************************************
     * Get the OID of an object with a specific name and a specific objecttype
     * or a set of specific objecttypes.
     * In case there has been more then one object found or the object could
     * not be found at all the method will return null.
     *
     * @param   objectTypes     the arrays with the specific objecttypes
     * @param   name            the name of the object we look for
     *
     * @return the oid of the object found or null otherwise
     */
    public OID getOidFromObjectName (int[] objectTypes, String name)
    {
        int rowCount;                   // row counter
        int operation = Operations.OP_VIEW;
        OID oid = null;
        String typeQueryStr = "(";
        String comma = "";
        SQLAction action = null;        // the action object used to access the
                                        // database

        // construct the string for the objectType filter
        for (int i = 0; i < objectTypes.length; i++)
        {
            typeQueryStr += comma + Type.createTVersionId (objectTypes[i]);
            comma = ",";
        } // for (int i = 0; i < objectTypes.length; i++)
        typeQueryStr += ")";

        // get the elements out of the database:
        // create the SQL String to select all tuples
        String queryStr =
            " SELECT o.oid" +
            " FROM   v_Container$rights o " +
            " WHERE o.userId = " + this.user.id +
            SQLHelpers.getStringCheckRights (operation) +
            " AND o.name = '" + name + "' " +
            " AND o.tVersionID IN " + typeQueryStr;
// BB HINT: DONT WE NEED THE DOMAINID HERE????

        try
        {
            action = this.getDBConnection ();
            rowCount = action.execute (queryStr, false);
            // the result must be exactly one else there was an db error
            // or the object could not be found or the have been
            // more then one object with this name
            if (rowCount == 1)
            {
                // set the oid
                oid = SQLHelpers.getQuOidValue (action, "oid");
            } //if
            // end transaction
            action.end ();
        } // try
        catch (DBError dbErr)
        {
            // possibly some error handling here
            oid = null;
        } // catch
        finally
        {
            this.releaseDBConnection (action);
        } // finally

        // return the oid
        return oid;
    } // getOidFromObjectName


    /**************************************************************************
     * Returns the container object depending on the container type
     * and value. <BR/>
     *
     * @param containerType         the type of container value
     * @param containerId           the container id or name
     * @param containerIdDomain     the domain of an external id
     * @param containerTabName      the name of a tab
     * @param inheritContainerOid   the ID of the importContainer
     *
     * @return  the contianer object or null
     */
    public BusinessObject getContainerFromType (String containerType,
                                                String containerId,
                                                String containerIdDomain,
                                                String containerTabName,
                                                OID inheritContainerOid)
    {
        OID newContainerOid = null;
        BusinessObject object = null;

        // resolve the container type setting
        newContainerOid = this.resolveContainer (containerType, containerId,
            containerIdDomain, containerTabName, inheritContainerOid);
        // check if the container exists and if the user has the right to access the
        // container object
        if (newContainerOid != null)
        {
            try
            {
                // init the object
                object = this.objectFactory.getObject (newContainerOid,
                    this.user, this.sess, this.env);
/* KR 020125: not necessary because already done before
                // retrieve the object with only a VIEW permission required
                // BB: I am not sure if this is ok because
                // it opens a backdoor. It is possible to import objects
                // into a container the user has no write permission for
                // because we only check the VIEW right
                object.retrieve (Operations.OP_VIEW);
*/
                // check if the object is active
                if (object instanceof Container && object.state == States.ST_ACTIVE)
                {
                    return object;
                } // if

                return null;
            } // try
/* KR 020125: not necessary because already done before
            catch (NoAccessException e) // no access to objects allowed
            {
                showNoAccessMessage (Operations.OP_NONE);
                return null;
            } // catch
            catch (AlreadyDeletedException e)
            {
                showAlreadyDeletedMessage ();
                return null;
            } // catch
*/
            catch (Exception e)
            {
                return null;
            } // catch
        } // if (newContainerOid != null)

        // did not get any oid
        return null;
    } // getContainerFromType


    /**************************************************************************
     * Returns a container oid depending on different type settings. In case of
     * an ID value the method will try to retrieve the object in order to check
     * for correctness. <BR/>
     *
     * @param containerType         the type of container value
     * @param containerId           the container id or name
     * @param containerIdDomain     the domain of an external id
     * @param containerTabName      the name of the tab
     * @param inheritContainerOid   the ID of the importContainer
     *
     * @return  the contianer oid or null
     */
    public OID getContainerOidFromType (String containerType,
                                        String containerId,
                                        String containerIdDomain,
                                        String containerTabName,
                                        OID inheritContainerOid)
    {
        OID newContainerOid = null;

        // resolve the container type setting
        newContainerOid = this.resolveContainer (containerType, containerId,
            containerIdDomain, containerTabName, inheritContainerOid);

/* BB TODO: we skip this check because it produces to much overhead
        // check if the container really exists in case the setting was OID
        // because the resolveContainer method is not performing a check in that
        // case. this is only nesseccary when we resolve an ID setting of a non
        // tab object
        if (newContainerOid != null
            && containerType.equalsIgnoreCase (DIConstants.CONTAINER_ID)
            && containerTabName.length () == 0)
        {
            try
            {
                // init the object
                BusinessObject object = new BusinessObject ();
                object.initObject (newContainerOid, user, this.env, this.sess, this.app);
                // retrieve the object with only a VIEW permission required
                // BB: I am not sure if this is ok because
                // it opens a backdoor. It is possible to import objects
                // into a container the user has no write permission for
                // because we only check the VIEW right
                object.retrieve (Operations.OP_VIEW);
            } // try
            catch (NoAccessException e) // no access to objects allowed
            {
                showNoAccessMessage (Operations.OP_NONE);
                newContainerOid = null;
            } // catch
            catch (AlreadyDeletedException e)
            {
                showAlreadyDeletedMessage ();
                newContainerOid = null;
            } // catch
        } // if (newContainerOid != null)
*/

        // return the container oid
        return newContainerOid;
    } // getContainerOidFromType



    /**************************************************************************
     * Resolves a container oid or a tab object oid depending on different
     * container type settings. <BR/>
     * These settings are:
     * <UL>
     * <LI> INHERIT ... get the ContainerOid from the container the import
     * has been started from
     * <LI> PATH .... set containerOid from a given path (stored procedure call)
     * <LI> ID ... set the containerOid to a OID
     * <LI> EXTKEY ... set the containerOid to a OID
     * </UL>
     *
     * BB HINT: the same method can be found in ibs.di.imp.ImportScript. <BR/>
     *
     * @param containerType         the type of container setting
     * @param containerId           the id or name of the container to be resolved
     * @param containerIdDomain     the domain of an external id
     * @param containerTabName      the name of a tab of the container to be
     *                              resolved
     * @param inheritContainerOid   the ID of the importContainer in case
     *                              the container setting was INHERIT
     *
     * @return  the oid of the resolved container type setting or null in case
     *          the setting could not have been resolved
     */
    private OID resolveContainer (String containerType,
                                  String containerId,
                                  String containerIdDomain,
                                  String containerTabName,
                                  OID inheritContainerOid)
    {
        OID newContainerOid = null;

        // CONSTRAINT: a containerType must be given
        if (containerType == null || containerType.length () == 0)
        {
            return null;
        } // if

        // check the type of the container
        if (containerType.equalsIgnoreCase (DIConstants.CONTAINER_ID))
        {
            try
            {
                // we have an oid of an container. try to generate an oid object
                // and test if oid is correct
                newContainerOid = new OID (containerId);
            } // try
            catch (IncorrectOidException e)
            {
                IOHelpers.showMessage (MultilingualTextProvider.getMessage(BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_INCORRECTOID, new String[] {containerId}, this.env),
                    this.app, this.sess, this.env);
                newContainerOid = null;
            } // catch
        } // if (ise.containerType.equalsIgnoreCase(DIConstants.CONTAINER_ID))
        else if (containerType.equalsIgnoreCase (DIConstants.CONTAINER_PATH))
        {
            // resolve the path
            try
            {
                newContainerOid = BOHelpers.resolveObjectPath (containerId,
                    inheritContainerOid, this, this.env);
            } // try
            catch (Exception e)
            {
//showDebug ("Exception: " + e);
                newContainerOid = null;
            } // catch
        } // else if (ise.containerType.equalsIgnoreCase (DIConstants.CONTAINER_PATH))
        else if (containerType.equalsIgnoreCase (DIConstants.CONTAINER_INHERIT))
        {
            // inherit means that we get the container oid from the
            // container the import function was invoked in
            // this is the default
            newContainerOid = inheritContainerOid;
        } // else if (ise.containerType.equalsIgnoreCase (DIConstants.CONTAINER_INHERIT))
        else if (containerType.equalsIgnoreCase (DIConstants.CONTAINER_EXTKEY))
        {
            // resolve the keymapping and set the oid
            // in case this fails the oid will be null
            newContainerOid = this.getKeyMapper (containerId, containerIdDomain);
        } // else if (ise.containerType.equalsIgnoreCase (DIConstants.CONTAINER_EXTKEY))
        else            // unknown container type
        {
            newContainerOid = null;
        } // else unknown container type

        // check if a tab object has to be resolved
        // this is defined by the containerTabName property
        if (newContainerOid != null && containerTabName != null &&
            containerTabName.length () > 0)
        {
            // try to resolve a tab object oid
            newContainerOid = this.getTabOidFromName (newContainerOid, containerTabName);
        } // if (newContainerOid != null ...

        // return the container we constructed from the importScript
        return newContainerOid;
    } // resolveContainer


    /**************************************************************************
     * Get the OID of a tab object with a specific name and of a specific
     * object. <BR/>
     * In case there has been more then one object found or the object could
     * not be found at all the method will return null. <BR/>
     *
     * @param   objectOid   The oid if the object the tab is located at.
     * @param   tabName     The name of the tab object we look for.
     *
     * @return  The oid of the tab object found or
     *          <CODE>null</CODE> otherwise.
     */
    protected OID getTabOidFromName (OID objectOid, String tabName)
    {
        int rowCount;                   // row counter
        OID tabOid = null;
        SQLAction action = null;        // the action object used to access the DB

        // get the elements out of the database:
        // create the SQL String to select all tuples
        String queryStr =
            " SELECT o.oid" +
            " FROM   ibs_object o " +
            " WHERE  o.name = '" + tabName + "' " +
            " AND    o.containerId = " + objectOid.toStringQu () +
            " AND    o.containerKind = 2" +
            " AND    o.state = " + States.ST_ACTIVE;

        try
        {
            action = this.getDBConnection ();
            rowCount = action.execute (queryStr, false);
            // the result must be exactly one else there was an db error
            // or the object could not be found or the have been
            // more then one object with this name
            if (rowCount == 1)
            {
                // set the oid
                tabOid = SQLHelpers.getQuOidValue (action, "oid");
            } // if (rowCount == 1)
            action.end ();
        } // try
        catch (DBError dbErr)
        {
            // possibly some error handling here
        } // catch
        finally
        {
            this.releaseDBConnection (action);
        } // finally
        // return the oid of the tab we found or null in case an error occurred
        return tabOid;
    } // getTabOidFromName



    /**************************************************************************
     * Resolves a keyMapping between an external object and an internal object.
     * Returns the oid of the internal object if found. <BR/>
     * In case there is no id parameter specified the key mapping will not be
     * resolved. <BR/>
     *
     * @param id        external id
     * @param idDomain  key domain of external id
     *
     * @return if found the oid of the internal object or null otherwise
     */
    protected OID getKeyMapper (String id, String idDomain)
    {
        // check first if an id value has been defined
        if (id != null && id.length () > 0)
        {
            KeyMapper keyMapper = new KeyMapper (this.user, this.env, this.sess, this.app);
            // resolve the key mapping
            return keyMapper.performResolveMapping (
                new KeyMapper.ExternalKey (idDomain, id));
        } // if (id != null && id.length () > 0)

        return null;
    } // getKeyMapper


    /***************************************************************************
     * Get the oid of an object out of the parameters. <BR/>
     * This method checks the arguments in this order:
     * <OL>
     * <LI><CODE>oidArg</CODE>: contains oid of the object</LI>
     * <LI><CODE>nameArg + typeId</CODE>: contains name and type
     * of the object</LI>
     * </OL>
     *
     * @param   oidArg  Argument for object oid.
     * @param   nameArg Argument for object name.
     * @param   typeId  Id of type from which to get object through name.
     *
     * @return  The oid or
     *          <CODE>null</CODE> if no valid oid was defined or
     *          no object with the defined oid could have been found.
     */
    protected OID getCombinedOidParam (String oidArg, String nameArg, int typeId)
    {
        String str = null;              // string parameter value
        OID oid = null;                 // the oid of the object

        // get the oid of the object:
        if (oidArg != null)             // oid argument defined?
        {
            oid = this.env.getOidParam (oidArg);
        } // if oid argument defined

        // check if we got the oid:
        if (oid == null || oid.isEmpty ()) // no oid found?
        {
            // try to get the object's name:
            if (nameArg != null)
            {
                str = this.env.getStringParam (nameArg);
            } // if

            // check if we got the name:
            if ((str != null) && (str.length () > 0))
            {
                // try to resolve the object oid from the name we got
                oid = this.getOidFromObjectName (typeId, str);
            } // if
        } // if no oid found

        // check if we got a valid oid:
        if (oid != null && oid.isEmpty ())
        {
            oid = null;
        } // if

        // return the result:
        return oid;
    } // getCombinedOidParam


    /***************************************************************************
     * Get the oid of an object out of the parameters. <BR/>
     * This method checks the arguments in this order:
     * <OL>
     * <LI><CODE>oidArg</CODE>: contains oid of the object</LI>
     * <LI><CODE>nameArg + typeIds</CODE>: contains name and possible types
     * of the object</LI>
     * </OL>
     *
     * @param   oidArg  Argument for object oid.
     * @param   nameArg Argument for object name.
     * @param   typeIds Ids of types from which to get object through name.
     *
     * @return  The oid or
     *          <CODE>null</CODE> if no valid oid was defined or
     *          no object with the defined oid could have been found.
     */
    protected OID getCombinedOidParam (String oidArg, String nameArg,
                                       int[] typeIds)
    {
        String str = null;              // string parameter value
        OID oid = null;                 // the oid of the object

        // get the oid of the object:
        if (oidArg != null)             // oid argument defined?
        {
            oid = this.env.getOidParam (oidArg);
        } // if oid argument defined

        // check if we got the oid:
        if (oid == null || oid.isEmpty ()) // no oid found?
        {
            // try to get the object's name:
            if (nameArg != null)
            {
                str = this.env.getStringParam (nameArg);
            } // if

            // check if we got the name:
            if ((str != null) && (str.length () > 0))
            {
                // try to resolve the object oid from the name we got
                oid = this.getOidFromObjectName (typeIds, str);
            } // if
        } // if no oid found

        // check if we got a valid oid:
        if (oid != null && oid.isEmpty ())
        {
            oid = null;
        } // if

        // return the result:
        return oid;
    } // getCombinedOidParam


    /***************************************************************************
     * Get an object out of the parameters. <BR/>
     * This method checks the arguments in this order:
     * <OL>
     * <LI><CODE>oidArg</CODE>: contains oid of the object</LI>
     * <LI><CODE>nameArg + typeIds</CODE>: contains name and possible types
     * of the object</LI>
     * </OL>
     *
     * @param   oidArg  Argument for connector oid.
     * @param   nameArg Argument for connector name.
     * @param   typeIds Ids of types from which to get object through name.
     *
     * @return  The object or
     *          <CODE>null</CODE> if no valid oid was defined or
     *          the defined object could not have been found.
     *
     * @see Integrator#getCombinedOidParam(String, String, int[])
     */
    public BusinessObject getCombinedObjectParam (String oidArg,
                                                  String nameArg, int[] typeIds)
    {
        BusinessObject obj = null;      // the object
        OID oid = null;                 // the oid of the object

        // get the oid of the object:
        oid = this.getCombinedOidParam (oidArg, nameArg, typeIds);

        // check if we got the oid:
        if (oid != null)
        {
            // get the object's instance:
            obj = this.getObjectFromOid (oid);
        } // if

        // return the object:
        return obj;
    } // getCombinedObjectParam


    /***************************************************************************
     * Get a connector out of the parameters. <BR/>
     * This method checks the arguments in this order:
     * <OL>
     * <LI>oidArg: contains oid of connector</LI>
     * <LI>nameArg: contains name of connector</LI>
     * <LI>pathArg: contains path from which to create a new file connector</LI>
     * </OL>
     *
     * @param   oidArg  Argument for connector oid.
     * @param   nameArg Argument for connector name.
     * @param   pathArg Argument for connector path.
     *
     * @return  The connector or
     *          <CODE>null</CODE> if no connector was defined or the defined
     *          connector could not have been found.
     *
     * @see Integrator#getCombinedOidParam(String, String, int[])
     */
    public Connector_01 getConnectorParam (String oidArg, String nameArg,
                                           String pathArg)
    {
        Connector_01 connector = null;  // the connector
        String str = null;              // string parameter value
        OID oid = null;                 // the oid of the connector

        // get the oid of the object:
        oid = this.getCombinedOidParam (
            oidArg, nameArg, this.getConnectorTypeIds ());

        // check if we got the oid:
        if (oid != null)
        {
            // get the object's instance:
            connector = this.createConnectorFromOid (oid);
        } // if

        // check if we got the connector:
        if (connector == null)          // no connector set?
        {
            // try to read a path:
            // this indicates that the request is coming from an agent
            if (pathArg != null)
            {
                str = this.env.getStringParam (pathArg);
            } // if

            // check if we found a path:
            if (str != null)        // value for export path found?
            {
                // initialize a file connector (default)
                // and set the export path
                connector = this.getDefaultConnector (str);
            } // if value for export path found?
        } // if no connector set

        // return the connector:
        return connector;
    } // getConnectorParam


    /***************************************************************************
     * Get a translator out of the parameters. <BR/>
     * This method checks the arguments in this order:
     * <OL>
     * <LI>oidArg: contains oid of translator</LI>
     * <LI>nameArg: contains name of translator</LI>
     * </OL>
     *
     * @param   oidArg  Argument for translator oid.
     * @param   nameArg Argument for translator name.
     *
     * @return  The translator or
     *          <CODE>null</CODE> if no translator was defined or the defined
     *          translator could not have been found.
     *
     * @see Integrator#getCombinedOidParam(String, String, int[])
     */
    public Translator_01 getTranslatorParam (String oidArg, String nameArg)
    {
        Translator_01 translator = null; // the translator
        OID oid = null;                 // the oid of the translator

        // get the oid of the object:
        oid = this.getCombinedOidParam (
            oidArg, nameArg, this.getTranslatorTypeIds ());

        // check if we got the oid:
        if (oid != null)
        {
            // get the object's instance:
            translator = this.createTranslatorFromOid (oid);
        } // if

        // return the translator:
        return translator;
    } // getTranslatorParam



    /**************************************************************************
     * Shows a status bar. <BR/>
     * This is used to assure that the user does not loose his connection
     * while importing a large amount of objects. <BR/>
     * The statusbar will only be shown in case the dispayLog flag has been
     * set to false. That means that the statusbar is an alternative to
     * the log. <BR/>
     */
    protected void showStatusBar ()
    {
        if (!this.log.isDisplayLog && (!this.p_isGenerateXMLResponse))
        {
            this.env.write (DIConstants.CHAR_STATUSBAR);
        } // if (! this.log.isDisplayLog && (! this.p_isGenerateXMLResponse))
    } // showStatusBar


    /**************************************************************************
     * Returns an array with all translator type ids available for the
     * integrator. <BR/>
     *
     * @return  An array with all translator type ids available or
     *          <CODE>null</CODE> if there are none.
     */
    public int [] getTranslatorTypeIds ()
    {
        return this.getTypeIds (TypeConstants.TC_Translator);
    } // getTranslatorTypeIds


    /**************************************************************************
     * Returns an array with all connector type ids available for the
     * integrator. <BR/>
     *
     * @return  An array with all connector type ids available or
     *          <CODE>null</CODE> if there are none.
     */
    public int [] getConnectorTypeIds ()
    {
        return this.getTypeIds (TypeConstants.TC_Connector);
    } // getConnectorTypeIds


    /**************************************************************************
     * Returns an array with all type ids that are subtype of a specific
     * type. <BR/>
     *
     * @param   typecode    The type for which to get the sub types.
     *
     * @return  An array with all subtype ids available or
     *          <CODE>null</CODE> if there are none.
     */
    public int [] getTypeIds (String typecode)
    {
        Type type = null;               // the major import translator type
        ITypeContainer<Type> subTypes = null;  // the sub types of the translator
        int [] typeIds = null; // the ids
        int i = 0;                      // counter

        // get the translator type:
        type = this.getTypeCache ().findType (typecode);

        try
        {
            subTypes = new TypeContainer ();
        } // try
        catch (ListException e)
        {
            // nothing to do
        } // catch

        // get the sub types:
        if (type != null && subTypes != null) // the type was found?
        {
            subTypes = type.getAllSubTypes (subTypes);
            // create the new arrays:
            typeIds = new int[subTypes.size () + 1];
            // set the first translator (the common type):
            typeIds [i++] = type.getTVersionId ();
            // loop through all elements and fill the array with the data:
            for (Iterator<Type> iter = subTypes.iterator (); iter.hasNext (); i++)
            {
                // get the actual element:
                type = iter.next ();
                // set the array data:
                typeIds [i] = type.getTVersionId ();
            } // for iter
        } // if the type was found

        // return the computed array:
        return typeIds;
    } // getTypeIds


    /**************************************************************************
     * Print a message. <BR/>
     *
     * @param   message The message to be printed.
     */
    public void print (String message)
    {
        // check if an xml response should be generated
        if (!this.p_isGenerateXMLResponse)
        {
            // has the generate HTML output be deactivated
            if (this.isGenerateHtml)
            {
                IOHelpers.showMessage (message,
                    this.app, this.sess, this.env);
            } // if
            else
            {
                this.env.write (message + "\r\n");
            } // else
        } // if (! this.p_isGenerateXMLResponse)
        else    // add the line to the log
        {
            this.log.add (DIConstants.LOG_ENTRY, message);
        } // add the line to the log
    } // print


    /**************************************************************************
     * Print an error. <BR/>
     *
     * @param   error   The error message to be printed.
     */
    public void printError (String error)
    {
        // check if an XML response should be generated
        if (!this.p_isGenerateXMLResponse)
        {
            // has the generate HTML output be deactivated
            if (this.isGenerateHtml)
            {
                IOHelpers.showMessage (error,
                    this.app, this.sess, this.env);
            } // if
            else
            {
                this.env.write (error + "\r\n");
            } // else
        } // if (! this.p_isGenerateXMLResponse)
        else    // add the line to the log
        {
            this.log.add (DIConstants.LOG_ERROR, error);
        } // add the line to the log
    } // printError


    /**************************************************************************
     * Initializes an xml response object in case the p_isGenerateXMLResponse
     * option has been activated.<BR/>
     *
     * @see #p_isGenerateXMLResponse
     */
    protected void initResponse ()
    {
        // should an xml response be generated
        if (this.p_isGenerateXMLResponse)
        {
            // generate a response object
            this.p_response = new Response ();
            this.p_response.setIsIncludeLog (this.p_isIncludeLog);
        } // if (this.p_isGenerateXMLResponse)
    } // initXMLResponse


    /**************************************************************************
     * Checks if an XML response should be generated and created it in case
     * the option has been activated. <BR/>
     *
     * @param functionName  the name of the function the xml response is
     *                      generated for
     */
    protected void generateXMLResponse (String functionName)
    {
        String errorsStr;
        String warningsStr;

        // should an xml response be generated
        if (this.p_isGenerateXMLResponse && this.p_response != null)
        {
            int function = this.env.getIntParam (BOArguments.ARG_FUNCTION);
            // settings for response
            this.p_response.setFunctionCode (function);
            this.p_response.setFunctionName (functionName);
            this.p_response.setLog (this.log);

            // check if the process was successfull
            if (this.log.isErrorfree ())
            {
                this.p_response.setResponseType (DIConstants.RESPONSE_SUCCESS);
                this.env.write (this.p_response.generate ());
            } // if (this.log.isErrorfree ())
            else    // process failed
            {
                // did we have any errors?
                if (this.log.hasErrors ())
                {
                    errorsStr = this.log.errorsToString ();
                    warningsStr = this.log.warningsToString ();
                    this.p_response.setResponseType (DIConstants.RESPONSE_ERROR);
                    this.p_response.setErrorCode ("0");
                    this.p_response.setErrorMessage (errorsStr + warningsStr);
                    // generate error response
                    this.env.write (this.p_response.generate ());
                } // if (this.log.hasErrors ())
                else    // only warnings
                {
                    warningsStr = this.log.warningsToString ();
                    this.p_response.setResponseType (DIConstants.RESPONSE_WARNING);
                    this.p_response.setErrorCode ("0");
                    this.p_response.setErrorMessage (warningsStr);
                    // generate warning response
                    this.env.write (this.p_response.generate ());
                } // else only warnings
            } // else process failed
        } // if (this.p_isGenerateXMLResponse)
    } // generateXMLResponse


    /**************************************************************************
     * Perform an error notification in case any errors or warnings
     * have been logged. In case an email receiver has been set the
     * error notification will be send as email to that user. If not
     * the actual user will be notified. <BR/>
     *
     * @param   defaultSubject  The default subject for the notification. (???)
     * @param   activity        ??? (ask BB)
     */
    protected void sendErrorNotification (String defaultSubject, String activity)
    {
        // should a notification be send?
        if (this.p_isSendErrorNotification)
        {
            try
            {
                boolean importErrors = false;

                // initialize the string buffer for the errors messages
                StringBuffer errors = new StringBuffer ();
                // add the time stamp of the import start
                errors.append (  
                    MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                        DITokens.ML_IMPORT_STARTED_AT, env) + " " +
                    DateTimeHelpers.dateTimeToString (this.log.processStartDate) + "\n");

                // get the errors
                String errorsStr = this.log.errorsToString ();
                if (errorsStr.length () > 0)
                {
                    errors.append (errorsStr);
                    importErrors = true;
                } // if (errors.length () > 0)
                // get the warnings
                String warningsStr = this.log.warningsToString ();
                if (warningsStr.length () > 0)
                {
                    errors.append (warningsStr);
                    importErrors = true;
                } // if (warningsStr.length () > 0)

                // if there are errors or warnings send a notification
                // to the current user.
                if (importErrors)
                {
                    NotificationTemplate msg = new NotificationTemplate ();

                    if (this.p_errorNotificationSubject == null)
                    {
                        msg.setSubject (this.p_errorNotificationSubject);
                    } // if
                    else
                    {
                        msg.setSubject (defaultSubject);
                    } // else

                    // not no activity:
                    msg.setActivities (activity);
                    // as description set only the errors and warnings:
                    msg.setDescription (errors.toString ());
                    // as content set the whole log:
                    msg.setContent (this.log.toString ());

                    // if the log writer is active attach the log file
                    if (this.log.isWriteLog)
                    {
                        String logFile = this.log.getPath () + this.log.getFileName ();
                        msg.addAttachment (new File (logFile));
                    } // if (this.log.isWriteLog)

                    // init the ???
                    Vector<OID> objs = new Vector<OID> ();
                    INotificationService service = NotificationServiceFactory.getInstance (this.env).getNotificationService ();
                    service.initService (this.user, this.env, this.sess, this.app);

                    // in case no receiver has been set notify the actual user
                    // via standard notification
                    if (this.p_errorNotificationReceiver == null)
                    {
                        Vector<OID> receivers = new Vector<OID> ();
                        receivers.addElement (this.user.oid);
                        service.performNotification (receivers, objs, msg, false);
                    } // if (this.p_errorNotificationReceiver == null)
                    else                // perform email notification
                    {
                        // delete the description because the whole log will
                        // be shown anyway in the content
                        msg.setDescription ("");
                        // send the email
                        service.emailNotification (msg, objs,
                            this.p_errorNotificationSender,
                            this.p_errorNotificationReceiver);
                    } // else perform email notification

                    // BB TODO: a log entry could be generated that the
                    // error notiifcation has been send
                } // if (errors.length () > 0)
            } // try
            catch (NotificationFailedException e)
            {
                this.log.add (DIConstants.LOG_ERROR, e.toString ());
/*
                IOHelpers.showMessage (e.toString (),
                    this.app, this.sess, this.env);
*/
            } // catch (NotificationFailedException e)
            catch (Exception e)
            {
                ByteArrayOutputStream out = new ByteArrayOutputStream ();
                PrintStream stream = new PrintStream (out);
                e.printStackTrace (stream);
                IOHelpers.showMessage (out.toString (),
                    this.app, this.sess, this.env);
            } // catch (Exception e)
        } // if (this.p_isSendNotification)
    } // sendErrorNotification


    /**************************************************************************
     * Returns the response object from the connector in case it exists. <BR/>
     *
     * @return  the response object if exists or null otherwise
     */
    public Response getResponse ()
    {
        // do we have a connector?
        if (this.connector != null)
        {
            // return the response that is comming from the connector
            return this.connector.getResponse ();
        } // if (this.connector != null)

        return null;
    } // getResponse

} // class Integrator
