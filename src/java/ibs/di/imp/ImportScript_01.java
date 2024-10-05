/*
 * Class: ImportScript_01.java
 */

// package:
package ibs.di.imp;

// imports:
//KR TODO: unsauber
import ibs.bo.BOHelpers;
//KR TODO: unsauber
import ibs.bo.BOMessages;
//KR TODO: unsauber
import ibs.bo.BOPathConstants;
//KR TODO: unsauber
import ibs.bo.BOTokens;
//KR TODO: unsauber
import ibs.bo.BusinessObject;
//KR TODO: unsauber
import ibs.bo.Buttons;
//KR TODO: unsauber
import ibs.bo.Datatypes;
//KR TODO: unsauber
import ibs.bo.IncorrectOidException;
//KR TODO: unsauber
import ibs.bo.OID;
//KR TODO: unsauber
import ibs.bo.States;
import ibs.di.DIArguments;
import ibs.di.DIConstants;
import ibs.di.DIErrorHandler;
import ibs.di.DIHelpers;
import ibs.di.DITokens;
import ibs.di.KeyMapper;
import ibs.di.Log_01;
import ibs.di.imp.ImportScriptElement;
//KR TODO: unsauber
import ibs.io.IOHelpers;
//KR TODO: unsauber
import ibs.ml.MultilingualTextProvider;
import ibs.obj.doc.DocConstants;
//KR TODO: unsauber
import ibs.obj.doc.File_01;
//KR TODO: unsauber
import ibs.service.user.User;
import ibs.tech.html.TableElement;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.xml.XMLReader;
import ibs.tech.xml.XMLReaderException;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;
import ibs.util.file.FileHelpers;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;


/******************************************************************************
 * The ImportScript Object holds the information of an import script file. <BR/>
 *
 * @version     $Id: ImportScript_01.java,v 1.40 2010/04/07 13:37:15 rburgermann Exp $
 *
 * @author      Buchegger Bernd (BB), 990107
 ******************************************************************************
 */
public class ImportScript_01 extends File_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ImportScript_01.java,v 1.40 2010/04/07 13:37:15 rburgermann Exp $";


    /**
     * name of the import script. <BR/>
     */
    public String scriptname = "";

    /**
     * version of the import script. <BR/>
     */
    public String version = "";

    /**
     * name of a importscenario. <BR/>
     */
    public String scenario = "";

    /**
     * Vector for the importscript elements in an import script file.
     * The importScriptElements represent the &lt;OBJECT> sections in the
     * importscript file. <BR/>
     */
    public Vector<ImportScriptElement> importScriptElements;

    /**
     * A global operation settings. If set it is valid for all objects. <BR/>
     */
    private String p_globalOperation = null;

    /**
     * A global container settings. The setting will only be used when an global
     * operation has been set. <BR/>
     */
    private OID p_globalContainerOid = null;

    /**
     * Error message: required object not existing. <BR/>
     * The tag {@link UtilConstants#TAG_OID} is part of the string and can
     * be replaced by the oid of the required object.
     */
    private static final String ERRM_REQOBJECT_NOTEXIST =
        "The required object " + UtilConstants.TAG_OID + " does not exist.";


    /**************************************************************************
     * Creates an ImportScript. <BR/>
     */
    public ImportScript_01 ()
    {
        // call constructor of super class:
        super ();
        // call constructor of super class ObjectReference:
        this.importScriptElements = new Vector<ImportScriptElement> ();
    } // ImportScript


    /**************************************************************************
     * Creates an ImportScript. <BR/>
     *
     * @param oid   oid of the object
     * @param user  user that created the object
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    @Deprecated
    public ImportScript_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
        // call constructor of super class ObjectReference:
        this.importScriptElements = new Vector<ImportScriptElement> ();
    } // ImportScript


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        super.initClassSpecifics ();    // has same specifics as super class

        this.attachmentType = DocConstants.ATT_FILE;
        this.isMaster = false;
        // set the default to true
        this.showInNews = false;
    } // initClassSpecifics


    /**************************************************************************
     * Read form the User the data used in the Object. <BR/>
     */
    public void getParameters ()
    {
        super.getParameters ();
    } // getParameters


    /**************************************************************************
     * Sets the name of the logfile. <BR/>
     *
     * @param fileName  name of the logfile
     */
    public void setFileName (String fileName)
    {
        this.fileName = fileName;
    } // setFileName


    /**************************************************************************
     * Sets the path of the logfile. <BR/>
     *
     * @param   path    Path and name of the log file.
     */
    public void setPath (String path)
    {
        this.path = FileHelpers.addEndingFileSeparator (path);
    } // setPath


    /**************************************************************************
     * Sets the global operation. <BR/>
     *
     * @param operation  the operation to set
     */
    public void setGlobalOperation (String operation)
    {
        this.p_globalOperation = operation;
    } // setGlobalOperation


    /**************************************************************************
     * Sets the global container oid. <BR/>
     *
     * @param   oid     The value to set.
     */
    public void setGlobalContainerOid (OID oid)
    {
        this.p_globalContainerOid = oid;
    } // setGlobalContainerOid


    /**************************************************************************
     * Get the name of the logfile. <BR/>
     *
     * @return the name of the logfile
     */
    public String getFileName ()
    {
        return this.fileName;
    } // getFileName


    /**************************************************************************
     * Get the path of the logfile. <BR/>
     *
     * @return the path of the logfile
     */
    public String getFilePath ()
    {
        return this.path;
    } // getFilePath


    /**************************************************************************
     * Add an ImportScriptElement to the ImportScriptElements vector. <BR/>
     *
     * @param   importScriptElement The ImportScriptElement to be added to
     *                              the vector.
     */
    public void addElement (ImportScriptElement importScriptElement)
    {
        try
        {
            this.importScriptElements.addElement (importScriptElement);
        } // try
        catch (Exception e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
    } // addElement


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in an
     * object's info view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  An array with button ids that can potentially be displayed.
     */
    protected int[] setInfoButtons ()
    {
        // define buttons to be displayed:
        int [] buttons =
        {
            Buttons.BTN_EDIT,
            Buttons.BTN_DELETE,
            Buttons.BTN_CUT,
            Buttons.BTN_COPY,
            Buttons.BTN_DISTRIBUTE,
            Buttons.BTN_SEARCH,
        }; // buttons

        // return button array
        return buttons;
    } // setInfoButtons



    /**************************************************************************
     * Finds an importScriptElement with a corresponding typename or
     * typecode within the vector of importScriptElements. <BR/>
     *
     * @param   typename    The typename or typecode to look for in the
     *                      importScript vector.
     * @param   typecode    The type code to search for.
     *
     * @return the importScriptElement we found or <CODE>null</CODE> otherwise
     */
    public ImportScriptElement find (String typename, String typecode)
    {
        ImportScriptElement importScriptElement = null;

        // loop through the importscript elements and
        // try to find the related import script
        for  (int i = 0; i < this.importScriptElements.size (); i++)
        {
            // get an importscriptelement from the vector
            importScriptElement = this.importScriptElements.elementAt (i);
            // check if we look for the typecode or the typename
            // in future only typecode matches will be valid
            if (importScriptElement.typeCodeRef != null)
            {
                // check if typecode matches
                // (allow wildcard)
                if (importScriptElement.typeCodeRef.equalsIgnoreCase (typecode) ||
                    importScriptElement.typeCodeRef.equalsIgnoreCase ("*"))
                {
                    return importScriptElement;
                } // if
            } // else if (importScriptElement.typeCodeRef != null)
            else if (importScriptElement.typeRef != null)
            {
                // check if typename matches
                // (allow wildcard)
                if (importScriptElement.typeRef.equalsIgnoreCase (typename) ||
                    importScriptElement.typeRef.equalsIgnoreCase ("*"))
                {
                    return importScriptElement;
                } // if
            } // if (importScriptElement.typeRef != null)
        } // while (enum.hasMoreElements())

        // in case no entries have been found check if any global operation has
        // been set
        // note that a global container alone will not be returned
        if (this.p_globalOperation != null)
        {
            importScriptElement = new ImportScriptElement ();
            importScriptElement.operationType = this.p_globalOperation;
            // check if a global container id has been set
            if (this.p_globalContainerOid != null)
            {
                importScriptElement.containerOid = this.p_globalContainerOid;
            } // if
            else    // set the INHERIT type in order to use the import container
            {
                importScriptElement.containerType = DIConstants.CONTAINER_INHERIT;
            } // else
            return importScriptElement;
        } // if (this.p_globalOperation != null)

        // no importscript entry found
        return null;
    } // find


    /**************************************************************************
     * Initialized the importscript by parsing the specified importscript file
     * and reading the importscript elements. <BR/>
     *
     * @return true if the initialization was successfull or false otherwise
     */
    public boolean init ()
    {
        Document doc;
        Node root;
        boolean isInitOk;

        // first check if there is a file name specified
        if (this.fileName == null || this.fileName.length () == 0)
        {
            return false;
        } // if

        // filename exists
        // construct the full path
        this.path = this.app.p_system.p_m2AbsBasePath +
            BOPathConstants.PATH_UPLOAD_ABS_FILES +
            DIHelpers.getOidFromPath (this.path);
        // parse the importscript file and get the document
        doc = this.getParseTreeFromFile (this.path, this.fileName);
        // check if we got the document
        if (doc != null)
        {
            // get the root element
            root = doc.getDocumentElement ();
            if (root != null)
            {
                // parse the importscript xml file
                isInitOk = this.parse (root);
                // now remove the XML structure in order to free the
                // memory
                try
                {
                    root = doc.removeChild (root);
                    root = null;
                    doc = null;
                    return isInitOk;
                } // try
                catch (Exception e)
                {
                    IOHelpers.showMessage (e, this.app, this.sess, this.env,
                        true);
                    return false;
                } // catch
            } // if (root != null)

            // could not create the DOM
            return false;
        } // if (doc != null)

        // could not read the importScript
        return false;
    } // init


    /**************************************************************************
     * Parses a importscript file and create the importScriptElement vector
     * that holds the import script data. <BR/>
     *
     * @param root      root node of the subtree to traverse
     *
     * @return  the importScriptElement that holds the data from the
     *          file or null if importScript file could not be processed
     */
    private boolean parse (Node root)
    {
        ImportScriptElement importScriptElement = null;
        NamedNodeMap attributes;
        Node node;
        Node attrNode;
        Node objectnode;
        NodeList nodelist;
        NodeList objectsnodelist;
        String nodename;
        int size;
        int objectsize;

        // get the name of the root node
        nodename = root.getNodeName ();
        //test if this is the IMPORT element
        if (nodename != null && !nodename.equals (DIConstants.ELEM_IMPORTSCRIPT))
        {
            return false;
        } // if (nodename != null && ! nodename.equals(ibs.di.DIConstants.ELEM_IMPORTSCRIPT))

        // importscript root found
        //get the attributes of the importscript
        attributes = root.getAttributes ();
        // check if attributes are set
        if (attributes != null)
        {
            // get importscript version
            if ((node = attributes.getNamedItem (DIConstants.ATTR_VERSION)) != null)
            {
                this.version = node.getNodeValue ();
            } // if
            // get importscript name
            // note that this has no functional effect
            if ((node = attributes.getNamedItem (DIConstants.ATTR_NAME)) != null)
            {
                this.scriptname = node.getNodeValue ();
            } // if
            // get name of importscenario class
            // BB HINT: For down compatibility CUSTOM is equal to SCENARIO
            if ((node = attributes.getNamedItem (DIConstants.ATTR_SCENARIO)) != null)
            {
                this.scenario = node.getNodeValue ();
            } // if
            else if ((node = attributes.getNamedItem (DIConstants.ATTR_CUSTOM)) != null)
            {
                this.scenario = node.getNodeValue ();
            } // else if
        } // if (attributes != null)

        // check if there are import script objects defined
        if (root.hasChildNodes ())
        {
            // traverse through the OBJECT sections
            nodelist = root.getChildNodes ();
            // get amount of objects
            size = nodelist.getLength ();
            // find the <OBJECTS> element
            for (int i = 0; i < size; i++)
            {
                // get the node
                node = nodelist.item (i);
                // check if node is the <OBJECT> element
                if (node.getNodeName ().equals (DIConstants.ELEM_OBJECT))
                {
                    // create a new importScriptElement
                    importScriptElement = new ImportScriptElement ();
                    //get typeref of importscript
                    attributes = node.getAttributes ();
                    // check if the attribute is available
                    if (attributes != null)
                    {
                        // get the TYPECODEREF attribute
                        if ((attrNode = attributes.getNamedItem (DIConstants.ATTR_TYPECODEREF)) != null)
                        {
                            importScriptElement.typeCodeRef = attrNode.getNodeValue ();
                        } // if
                        // get the TYPEREF attribute
                        if ((attrNode = attributes.getNamedItem (DIConstants.ATTR_TYPEREF)) != null)
                        {
                            importScriptElement.typeRef = attrNode.getNodeValue ();
                        } // if
                    } // if (attributes != null)
                    // parse the nodes that contain the <OPERATION> and
                    // the <CONTAINER> elements
                    objectsnodelist = node.getChildNodes ();
                    // get amount of objects
                    objectsize = objectsnodelist.getLength ();
                    // loop through the objects
                    for (int ii = 0; ii < objectsize; ii++)
                    {
                        // get the node
                        objectnode = objectsnodelist.item (ii);
                        // look for operation and container definitions
                        if (objectnode.getNodeName ().equalsIgnoreCase (DIConstants.ELEM_OPERATION))
                        {
                            // parse the operatoin node and put th results into
                            // the importScriptElement
                            this.parseOperationNode (objectnode, importScriptElement);
                        } // if (objectnode.getNodeName().equals(DIConstants.ELEM_OPERATION))
                        else if (objectnode.getNodeName ().equalsIgnoreCase (DIConstants.ELEM_CONTAINER))
                        {
                            // parse the container node and put th results into
                            // the importScriptElement
                            this.parseContainerNode (objectnode, importScriptElement);
                        } // else if (objectnode.getNodeName().equals(DIConstants.ELEM_CONTAINER))
                    } // for (int ii = 0; ii < objectsize; ii++)
                    // add the importSciptElement to the importScript Object
                    this.addElement (importScriptElement);
                } // if (node.getNodeName().equals(DIConstants.ELEM_OBJECT))
            } // for (int i = 0; i < size; i++)

            // now remove the child node because it is not needed anymore
            // this has to be done in order to avoid memory leaks
            try
            {
                for (int j = size - 1; j >= 0; j--)
                {
                    node = nodelist.item (j);
                    node = root.removeChild (node);
                    node = null;
                } // for (int j = 0; j < size; j++)
            } // try
            catch (DOMException e)
            {
                // show error message:
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            } // catch
            // return true to indicate correct parsing of the importscript file
            return true;
        } // if (root.hasChildNodes())

        // return false to  indicate that we could not get the data
        return false;
    } // parse


    /**************************************************************************
     * Parses the operation section of an importscript document and sets its
     * values in an importScriptElement instance. <BR/>
     *
     * @param   operationNode       root node of the subtree to traverse
     * @param   importScriptElement the importScriptElement instance to store
     *                              the values in
     */
    private void parseOperationNode (Node operationNode,
                                     ImportScriptElement importScriptElement)
    {
        NamedNodeMap attributes;
        String operationType = "";
        String operation = "";
        Text text;

        // read the type attribute
        attributes = operationNode.getAttributes ();
        if (attributes != null)
        {
            operationType = attributes.getNamedItem (ibs.di.DIConstants.ATTR_TYPE).getNodeValue ();

/* BB 20060323: UPDATE ist not supported anymore
 *           allowing incomplete structures to be imported
 *           this only works on already existing objects like CHANGEONLY
 *
            // the new operation UPDATE is mapped to the already
            // existing operation CHANGEONLY because UPDATE is only
            // an alias for CHANGEONLY.
            if (operationType != null &&
                operationType.equalsIgnoreCase (DIConstants.OPERATION_UPDATE))
            {
                operationType = DIConstants.OPERATION_CHANGEONLY;
            } // if UPDATE operation
*/
            // set operation type
            importScriptElement.operationType = operationType;
            // read the value for custom operation
            text = (Text) operationNode.getFirstChild ();
            // text node found?
            if (text != null)
            {
                operation = text.getNodeValue ();
            } // if
            // set the operation name and type
            importScriptElement.operationType = operationType;
            importScriptElement.operationName = operation;
        } // if (attributes != null)
    } // parseOperationNode


    /**************************************************************************
     * Parses the container section of an importscript document and sets its
     * values in an importScriptElement instance. <BR/>
     *
     * @param containerNode         root node of the subtree to traverse
     * @param importScriptElement   the importScriptElement instance to store
     *                              the values in
     */
    private void parseContainerNode (Node containerNode,
                                     ImportScriptElement importScriptElement)
    {
        NamedNodeMap attributes;
        String containerType = "";
        String containerId = "";
        String containerIdDomain = "";
        String containerTabName = "";
        Text text;
        NodeList nodelist;
        Node node;
        Node attributeNode;
        int size;

        // read the type attribute
        attributes = containerNode.getAttributes ();
        if (attributes != null)
        {
            // get container tab name
            attributeNode = attributes.getNamedItem (ibs.di.DIConstants.ATTR_TABNAME);
            if (attributeNode != null)
            {
                containerTabName = attributeNode.getNodeValue ();
            } // if
            // get container type
            attributeNode = attributes.getNamedItem (ibs.di.DIConstants.ATTR_TYPE);
            if (attributeNode != null)
            {
                containerType = attributeNode.getNodeValue ();
            } // if
            // check if the containerType is a external key.
            // in that case we have to read a more complex structure
            // in the form:
            // <CONTAINER TYPE="EXTKEY">
            //    <ID DOMAIN="app">123</ID>
            // </CONTAINER>
            if (containerType.equalsIgnoreCase (DIConstants.CONTAINER_EXTKEY))
            {
                // check if there are sub nodes
                if (containerNode.hasChildNodes ())
                {
                    // traverse through the OBJECT sections
                    nodelist = containerNode.getChildNodes ();
                    // get amount of objects
                    size = nodelist.getLength ();
                    // find the <OBJECTS> element
                    for (int i = 0; i < size; i++)
                    {
                        // get the node
                        node = nodelist.item (i);
                        // check if it is an ID node
                        if (node.getNodeName ().equalsIgnoreCase (DIConstants.ELEM_ID))
                        {
                            // get the id domain attribute
                            attributes = node.getAttributes ();
                            attributeNode = attributes.getNamedItem (DIConstants.ATTR_DOMAIN);
                            // check if DOMAIN attribute has been set
                            if (attributeNode != null)
                            {
                                containerIdDomain = attributeNode.getNodeValue ();
                            } // if
                            // get the text value
                            text = (Text) node.getFirstChild ();
                            if (text != null)
                            {
                                containerId = text.getNodeValue ();
                            } // if
                        } // if (systemNode.getNodeName().equalsIgnoreCase (DIConstants.ELEM_ID))
                    } // for (int i = 0; i < size; i++)
                } // if (containerNode.hasChildNodes ())
            } // if (containerType.equalsIgnoreCase (DIConstants.CONTAINER_EXTKEY))
            else    // container is not an external key
            {
                // read the value for custom operation
                text = (Text) containerNode.getFirstChild ();
                // text node found?
                if (text != null)
                {
                    containerId = text.getNodeValue ();
                } // if
            } // else container is not an external key
            // set the values in the importScriptElement
            importScriptElement.containerType = containerType;
            importScriptElement.containerId = containerId;
            importScriptElement.containerIdDomain = containerIdDomain;
            importScriptElement.containerTabName = containerTabName;
        } // if (attributes != null)
    } // parseContainerNode


    /**************************************************************************
     * Takes a path and filename as input and parses the xml document. <BR/>
     * It returns the document as parsed DOM tree. <BR/>
     *
     * @param path      path of the importfile
     * @param filename  filename of the importfile
     *
     * @return  the parsed XML document as a DOM tree
     */
    protected Document getParseTreeFromFile (String path, String filename)
    {
        // check path
        String pathLocal = FileHelpers.addEndingFileSeparator (path);
        // instantiate errorListener
        DIErrorHandler errorHandler = new DIErrorHandler ();
        errorHandler.setEnv (this.env);
        errorHandler.sess = this.sess;

        try
        {
            // read the document:
            // do not validate the document. This speeds up the parsing
            return new XMLReader (pathLocal + filename, false, errorHandler)
                    .getDocument ();
        } // try
        catch (XMLReaderException e)
        {
            return null;
        } // catch
    } //getParseTreeFromFile


    /**************************************************************************
     * Returns the importContainerOid depending on the type of containerID
     * source set in the importscript. <BR/>
     *
     * @param importScriptElement   the ImportscriptElement that holds the data
     * @param importContainerId     the ID of the importContainer
     *
     * @return  the oid of the import container to use
     */
    public OID getContainerFromType (ImportScriptElement importScriptElement,
                                 OID importContainerId)
    {
        // first check if the container oid has already been resolved
        if (importScriptElement.containerOid != null)
        {
            return importScriptElement.containerOid;
        } // if

        // resolve container oid
        // get the oid and set in in the importscriptelement:
        importScriptElement.containerOid =
            this.getContainerFromType (importScriptElement.containerType,
                              importScriptElement.containerId,
                              importScriptElement.containerIdDomain,
                              importScriptElement.containerTabName,
                              importContainerId);

        // return the result:
        importScriptElement.containerId = importScriptElement.containerOid.toString ();
        return importScriptElement.containerOid;
    } // getContainerFromType


    /**************************************************************************
     * Returns the containerOid depending on differnent typ settings. <BR/>
     * The possible settings are:
     * <UL>
     * <LI> INHERIT ... get the ContainerOid from the Container the import
     * has been started from
     * <LI> PATH .... set containerOid from a given path (stored procedure call)
     * <LI> ID ... set the containerOid to a OID
     * <LI> EXTKEY ... an external key to be reolved via keymapping
     * </UL>
     *
     * BB HINT: the same method can be found in ibs.di.Integrator. <BR/>
     *
     * @param   containerType       The type of container value.
     * @param   containerId         The id (or name) of the container.
     * @param   containerIdDomain   The domain of an external id.
     * @param   containerTabName    The name of the tab with the container.
     * @param   inheritContainerOid The ID of the importContainer.
     *
     * @return  The oid of the container to be used.
     */
    public OID getContainerFromType (String containerType,
                                     String containerId,
                                     String containerIdDomain,
                                     String containerTabName,
                                     OID inheritContainerOid)
    {
        OID newContainerOid = null;

        // check the type of the container
        if (containerType.equalsIgnoreCase (DIConstants.CONTAINER_ID))
        {
            // we have an oid of an container. try to generate an oid object
            // and test if oid is correct
            try
            {
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
                IOHelpers.showMessage ("getContainerFromType Exception.",
                    e, this.app, this.sess, this.env, true);
                newContainerOid = null;
            } // catch
        } // else if (ise.containerType.equalsIgnoreCase(DIConstants.CONTAINER_PATH))
        else if (containerType.equalsIgnoreCase (DIConstants.CONTAINER_INHERIT))
        {
            // inherit means that we get the container oid from the
            // container the import function was invoked in
            // this is the default
            newContainerOid = inheritContainerOid;
        } // else if (ise.containerType.equalsIgnoreCase(DIConstants.CONTAINER_INHERIT))
        else if (containerType.equalsIgnoreCase (DIConstants.CONTAINER_EXTKEY))
        {
            // resolve the keymapping and set the oid
            // in case this fails the oid will be null
            newContainerOid = this.getKeyMapper (containerId, containerIdDomain);
        } // else if (ise.containerType.equalsIgnoreCase(DIConstants.CONTAINER_EXTKEY))
        else            // unknown container type
        {
            newContainerOid = null;
        } // else unknown container type

        // check if the container really exists
        if (newContainerOid != null &&
            containerType.equalsIgnoreCase (DIConstants.CONTAINER_ID))
        {
            try
            {
                // check if the object exists
                // init the object
                // retrieve the object without permission check done
                // BB: I am not sure if this is ok because
                // it opens a backdoor. It is possible to import objects
                // into a container the user has no write permission for
                BusinessObject object = BOHelpers.getObject (newContainerOid,
                    this.env, false, false, false);
                if (object == null)
                {
                    IOHelpers.showMessage (StringHelpers.replace (
                        ImportScript_01.ERRM_REQOBJECT_NOTEXIST,
                        UtilConstants.TAG_OID, "" + newContainerOid), this.app,
                        this.sess, this.env);
                } // if
            } // try
            catch (NullPointerException e)
            {
                IOHelpers.showMessage (StringHelpers.replace (
                    ImportScript_01.ERRM_REQOBJECT_NOTEXIST,
                    UtilConstants.TAG_OID, "" + newContainerOid), e, this.app,
                    this.sess, this.env, true);
            } // catch
        } // if (newContainerOid != null)

        // check if a tab object has to be resolved
        // this is defined by the containerTabName property
        if (newContainerOid != null && containerTabName != null &&
            containerTabName.length () > 0)
        {
            // try to resolve a tab object oid
            newContainerOid = this.getTabOidFromName (newContainerOid, containerTabName);
        } // if (newContainerOid != null ...

        // return the container oid we constructed from the importScript
        return newContainerOid;
    } // getContainerFromType


    /**************************************************************************
     * Get the OID of a tab object with a specific name and of a specific
     * object. <BR/>
     * In case there has been more then one object found or the object could
     * not be found at all the method will return null. <BR/>
     *
     * @param   objectOid   The oid if the object the tab is located at.
     * @param   tabName     The name of the tab object we look for.
     *
     * @return  The oid of the tab object found or <CODE>null</CODE> otherwise.
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
            return keyMapper.performResolveMapping (new KeyMapper.ExternalKey (idDomain, id));
        } // if (id != null && !id.length () == 0)

        // no id value specified
        return null;
    } // getKeyMapper


    /**************************************************************************
     * Displays the settings of the connector. <BR/>
     *
     * @param   table       Table where the settings shall be added.
     */
    public void showSettings (TableElement table)
    {
        // display name of importScript
        this.showProperty (table, DIArguments.ARG_IMPORTSCRIPT,  
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_IMPORTSCRIPT, env),
            Datatypes.DT_NAME, this.name);
        // display name of importScript file
/* BB HINT: deactivated because not neccessary
        showProperty (table, AppArguments.ARG_FILE, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_FILE, env),
                      Datatypes.DT_TEXT, fileName);
*/
    } // showSettings


    /**************************************************************************
     * Adds the settings of the connector to a log. <BR/>
     *
     * @param   log     the log to add the setting to
     */
    public void addSettingsToLog (Log_01 log)
    {
        // add connector specific settings
        log.add (DIConstants.LOG_ENTRY,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_IMPORTSCRIPT, env) + ": " +
            this.name, false);
        log.add (DIConstants.LOG_ENTRY, 
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                BOTokens.ML_FILE, env) + ": " +
            this.fileName, false);
    } // addSettingsToLog

} // ImportScript_01
