/*
 * Class: m2XMLFilter.java
 */

// package:
package ibs.di.filter;

// imports:
//KR TODO: unsauber
import ibs.BaseObject;
import ibs.IbsGlobals;
import ibs.bo.BOHelpers;
import ibs.bo.BusinessObjectInfo;
import ibs.bo.OID;
import ibs.bo.type.ITemplate;
import ibs.bo.type.TypeConstants;
import ibs.di.DIConstants;
import ibs.di.DIHelpers;
import ibs.di.DataElement;
import ibs.di.DataElementList;
import ibs.di.DocumentTemplate_01;
import ibs.di.InputParamElement;
import ibs.di.ObjectFactory;
import ibs.di.OutputParamElement;
import ibs.di.ReferencedObjectInfo;
import ibs.di.RightDataElement;
import ibs.di.ValueDataElement;
import ibs.io.Environment;
import ibs.io.IOHelpers;
import ibs.obj.query.QueryConstants;
import ibs.obj.query.QueryCreator_01;
import ibs.obj.query.QueryExecutive;
import ibs.obj.query.QueryExecutive_01;
import ibs.obj.query.QueryHelpers;
import ibs.obj.query.QueryNotFoundException;
import ibs.obj.query.QueryParameter;
import ibs.obj.query.QueryPool;
import ibs.service.action.ActionConstants;
import ibs.service.action.ActionException;
import ibs.service.action.Actions;
import ibs.tech.xml.XMLReader;
import ibs.tech.xml.XMLReaderException;
import ibs.tech.xml.XMLWriter;
import ibs.tech.xml.XMLWriterException;
import ibs.util.crypto.EncryptionManager;
import ibs.util.file.FileHelpers;

import java.io.File;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;


/******************************************************************************
 * The m2XMLImportFilter handles all imports from XML datasources that conforms
 * to the m2 import DTD. <BR/>
 *
 * @version     $Id: m2XMLFilter.java,v 1.95 2013/01/15 14:48:29 rburgermann Exp $
 *
 * @author      Buchegger Bernd (BB), 990521
 ******************************************************************************
 */
public class m2XMLFilter extends Filter
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: m2XMLFilter.java,v 1.95 2013/01/15 14:48:29 rburgermann Exp $";


    /**
     * Holds a Pointer to the first OBJECTS node.
     * This is used by the reset method to go back to the beginning of the
     * objects section. <BR/>
     */
    private NodeList objectsNodeList;

    /**
     * the length of the objectsNodeList
     */
    private int objectsNodeListLength = 0;

    /**
     * Holds the actual DataElementList to loop through.
     * This is used to return single DataElement objects. <BR/>
     */
    private Enumeration<DataElement> actualDataElements = null;

    /**
     * Holds the acutal index in the objects nodeList. <BR/>
     */
    private int objectsIndex = 0;

    /**
     * flag to export the rights. <BR/>
     */
    public boolean isExportRights = false;

    /**
     * flag to import also the the attributes reguarding the template files. <BR/>
     * This are the attribures DBTABLE, DBFIELD, CLASS, ICON, ... .
     */
    public boolean isTemplateImport = false;

    /**
     * The version number found in the IMPORT tag of the import document.
     */
    public String p_documentVersion = "";

    /**
     * The operation found in the IMPORT tag of the import document.
     */
    public String p_documentOperation = null;

    /**
     * The template of the document (necessary for the in- and output
     * parameters. <BR/>
     */
    private DocumentTemplate_01 p_documentTemplate = null;


    /**************************************************************************
     * Sets the document template. <BR/>
     * This method stores the document template of the XMLViewer_01 in the
     * m2XMLFilter. This is necassary because the addQueryData needs to know
     * the in- and output parameters to show no error. <BR/>
     *
     * @param   docTemplate     the document template to set
     */
    public void setDocumentTemplate (ITemplate docTemplate)
    {
        // to avoid null pointer exceptions
        if (docTemplate == null)
        {
            return;
        } // if

        this.p_documentTemplate = (DocumentTemplate_01) docTemplate;
    } // setDocumentTemplate


    /**************************************************************************
     * Returns the document template of the XMLViewer_01. <BR/>
     *
     * @return  The document tmplate ot XMLViewer_01.
     */
    private DocumentTemplate_01 getDocumentTemplate ()
    {
        return this.p_documentTemplate;
    } // getDocumentTemplate


    /**************************************************************************
     * Create a new insertion node. All following objects are added as childs
     * of this node. <BR/>
     *
     * @param   isTabInsertion  if true the new insertion point (node)
     *                          is a TABS node intead of an OBJECTS node.
     */
    public void setInsertionPoint (boolean isTabInsertion)
    {
        // isTabInsertion indicates that the following objects are tabs.
        // Create an TABS node and set this new node
        // for the current insertion point.

        Node lastChild = this.p_insertionPoint.getLastChild ();
        if (lastChild == null)
        {
            lastChild = this.p_insertionPoint;
        } // if

        if (isTabInsertion)
        {
            // for tab objects the insertion point is a <TABS> node.
            String tagName = DIConstants.ELEM_TABS;
            // create the insertion node
            Element node = this.doc.createElement (tagName);
            lastChild.appendChild (node);
            this.p_insertionPoint = node;
        } // if is tab insertion point
        else
        {
            // Create an OBJECT node and set this new node
            // for the current insertion point.
            String tagName = DIConstants.ELEM_OBJECTS;
            // create the insertion node
            Element node = this.doc.createElement (tagName);
            lastChild.appendChild (node);
            this.p_insertionPoint = node;
        } // else if is tab insertion point
    } // setInsertionPoint


    /**************************************************************************
     * The function changes the current insertion point to. <BR/>
     * two levels above.
     */
    public void revertInsertionPoint ()
    {
        if (this.p_insertionPoint != null)
        {
            this.p_insertionPoint = (Element) this.p_insertionPoint.getParentNode ();
        } // if
        if (this.p_insertionPoint != null)
        {
            this.p_insertionPoint = (Element) this.p_insertionPoint.getParentNode ();
        } // if
    } // revertInsertionPoint


    /**************************************************************************
     * Creates an ImportFilter Object. <BR/>
     */
    public m2XMLFilter ()
    {
        super ();
    } // m2XMLImportFilterXerces

//
// IMPORT FILTER METHODS
//

    /**************************************************************************
     * Checks how many true element nodes have been found in the import
     * document and returns the value found. <BR/>
     * Hint: this is time consuming but an import document can container
     * text elements as whitespaces which corrupt the correct value. <BR/>
     *
     * @return the amount of objects collections found or -1 if amount could
     *          not be read.
     */
    private int checkElementsLength ()
    {
        // check if objectsNodeList exists
        if (this.objectsNodeList != null)
        {
            int objectsSize = this.objectsNodeListLength;
            int actElementsLength = 0;
            Node objectsNode;
            // check if there are any elements
            if (objectsSize > 0)
            {
                // now go through all the objects
                for (int i = 0; i < objectsSize; i++)
                {
                    //get a node from the nodelist
                    objectsNode = this.objectsNodeList.item (i);
                    // check if node is an element node (it should be an
                    // <OBJECTS> tag
                    if (objectsNode.getNodeType () == Node.ELEMENT_NODE)
                    {
                        // gte the <OBJECT> nodes in the <OBJECTS> node
                        NodeList objNodeList = objectsNode.getChildNodes ();
                        if (objNodeList != null)
                        {
                            int objectSize = objNodeList.getLength ();
                            for (int j = 0; j < objectSize; j++)
                            {
                                //get a node from the nodelist
                                Node objectNode = objNodeList.item (j);
                                // check if node is an element node (it should be an
                                // <OBJECT> tag
                                if (objectNode.getNodeType () == Node.ELEMENT_NODE)
                                {
                                    // found a node - we assume that it is a <OBJECT> node
                                    actElementsLength++;
                                } // if (objectNode.getNodeType () == Node.ELEMENT_NODE)
                            } // for (int j = 0; j<objectSize; j++)
                        } // if (objNodeList != null)
                    } // if (node.getNodeType () == Node.ELEMENT_NODE)
                } // for (int i = 0; i < size; i++)
            } // if (size > 0)
            return actElementsLength;
        } // if (this.objectsNodeList != null)

        return -1;
    } // checkElementsLength


    /**************************************************************************
     * The init method sets a document that must be the import xml structure
     * and performs the parsing. <BR/>
     *
     * @param   actDoc  The document that should contain the import xml structure.
     *
     * @return true if the initialization succeeded or false otherwise
     */
    public boolean init (Document actDoc)
    {
        // check if we get a document
        if (actDoc != null)
        {
            // set the document
            this.doc = actDoc;
            // get the documentRoot
            Element root = this.doc.getDocumentElement ();
            return this.initImport (root);
        } // if (this.doc != null)

        return false;
    } // init


    /**************************************************************************
     * The init method reads in the import file and performs the parsing. <BR/>
     *
     * @return  <CODE>true</CODE> if initialisation succeeded or
     *          <CODE>false</CODE> otherwise.
     */
    public boolean init ()
    {
        String filePath;

        // check if we have an name of the importFile
        if (this.fileName == null || this.fileName.length () == 0)
        {
            return false;
        } // if

        // filename ok:
        filePath = FileHelpers.addEndingFileSeparator (this.path) + this.fileName;
        File file = new File (filePath);
        // first check if file exists:
        if (!(file.exists () && file.isFile ()))
        {
            return false;
        } // if

        // file exists:
        try
        {
            // read the xml document from a file:
            XMLReader reader = new XMLReader (filePath, true, null);
            this.doc = reader.getDocument ();
            Element root = reader.getRootElem ();
            return this.initImport (root);
        } // try
        catch (XMLReaderException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            return false;
        } // catch

/*KR old
        this.doc = getParseTreeFromFile (this.path, this.fileName);
        // check if we could read the file:
        if (this.doc != null)       // the document exists?
        {
            // get the documentRoot
            Element root = this.doc.getDocumentElement ();
            return initImport (root);
        } // if the document exists
        else                        // file does not exist
        {
            return false;
        } // else file does not exist
*/
    } // init


    /**************************************************************************
     * Sets processing again to the beginning of all objects. <BR/>
     */
    public void reset ()
    {
        this.objectsIndex = 0;
    } // reset


    /**************************************************************************
     * parses the importfile and create the objects. <BR/>
     *
     * @param root      root node of the subtree to parse
     *
     * @return  <CODE>true</CODE> if everything was o.k.,
     *          <CODE>false</CODE> otherwise.
     */
    protected boolean initImport (Element root)
    {
        NamedNodeMap attributes;
        Node node;
        String nodename;

        // clear the version number attribute.
        this.p_documentVersion = "";

        // check if root is not null
        if (root == null)
        {
            return false;
        } // if

        nodename = root.getNodeName ();
        // test if this is the IMPORT element
        if (nodename == null || !nodename.equals (DIConstants.ELEM_IMPORT))
        {
            return false;
        } // if (nodename == null || !nodename.equals (DIConstants.ELEM_IMPORT))


        // get attributes of the IMPORT tag.
        attributes = root.getAttributes ();
        if (attributes != null)
        {
            // get the VERSION attribute from the IMPORT tag
            node = attributes.getNamedItem (DIConstants.ATTR_VERSION);
            if (node != null)
            {
                this.p_documentVersion = node.getNodeValue ();
            } // if

            // get the OPERATION attribute from the IMPORT tag
            node = attributes.getNamedItem (DIConstants.ATTR_OPERATION);
            if (node != null)
            {
                // HINT: This variable is not used at the moment
                // TODO: add specific logic in ImportIntegrator.performProcessImport()
                this.p_documentOperation = node.getNodeValue ();
            } // if
        } // if

/* BB: Not supported anymore
        // get the importScript name from the ACTION attribute in
        // the IMPORT element
        attributes = root.getAttributes ();
        node = attributes.getNamedItem (DIConstants.ATTR_ACTION);
        // check if there is an ACTION attribute set
        if (node != null)
            this.importScriptFileName = node.getNodeValue ();
        else
            this.importScriptFileName = "";
        // check if the importScript filename is just empty string
        if ("".equals (this.importScriptFileName.trim ()))
        {
            this.importScriptFileName = null;
        } // if
*/

        // check if there are OBJECTS sections we can get the data from
        if (root.hasChildNodes ())
        {
            // store the objects and the total abount of objects found
            this.objectsNodeList = root.getChildNodes ();
            this.objectsNodeListLength = this.objectsNodeList.getLength ();
            this.objectsIndex = 0;
            this.elementsLength = this.checkElementsLength ();
            return true;
        } // if (root.hasChildNodes ())

        // no child nodes:
        return false;
    } // initImport


    /**************************************************************************
     * parses an objects section and create the objects. <BR/>
     *
     * @param   root                Root node of the subtree to parse.
     * @param   isTemplateImport    Flag indicating if a template import is
     *                              performed. For template imports also the
     *                              template attributes are  regarded.
     * @param   env                 The current environment.
     *
     * @return  A list containing the parsed data elements,
     *          <CODE>null</CODE> if there occurred an error.
     */
    public static DataElementList parseObjects (Node root,
                                                boolean isTemplateImport,
                                                Environment env)
    {
        Node node;
        NodeList nodelist;
        NamedNodeMap attributes;
        String objectsOperation = null;        
        int actSize;
        DataElement dataElement = null;
        DataElementList dataElementList = null;

        if (root == null)
        {
            return null;
        } // if (root == null)

        // root is not null:
        // test if this really an OBJECTS element
        if ((!root.getNodeName ().equals (DIConstants.ELEM_OBJECTS)) &&
            (!root.getNodeName ().equals (DIConstants.ELEM_TABS)))
        {
            return null;
        } // if (!root.getNodeName ().equals (DIConstants.ELEM_OBJECTS)) && ...

        // we found an OBJECTS section:
        
        // get a possible OPERATION attribute from the OBJECTS Tag
        attributes = root.getAttributes ();
        node = attributes.getNamedItem (DIConstants.ATTR_OPERATION);
        // was an operation attribute found?
        if (node != null)
        {
            objectsOperation = node.getNodeValue ();
        } // if

        // parse through the OBJECT sections
        nodelist = root.getChildNodes ();
        actSize = nodelist.getLength ();
        // check if there are no objects
        if (actSize > 0)
        {
            // initialize an import element list
            dataElementList = new DataElementList ();
            // set the operation from the OBJECTS tag to the DataElementlist
            dataElementList.operation = objectsOperation;
            
            // now go through all the objects
            for (int i = 0; i < actSize; i++)
            {
                //get a node from the nodelist
                node = nodelist.item (i);
                // check if node is an element node
                if (node.getNodeType () == Node.ELEMENT_NODE)
                {
                    // parse OBJECTS section and create an import element:
                    dataElement = m2XMLFilter.parseObject (node, null, isTemplateImport, false, env);
                    // check if we got an DataElement
                    if (dataElement != null)
                    {
                        // store the DataElement in a collection
                        // and process it later together with the importscript
                        dataElementList.addElement (dataElement);
                    } // if (DataElement != null)
                    // now remove the child node because it is not needed anymore
                    // this has to be done in order to avoid memory leaks
                } // if (node.getNodeType () == Node.ELEMENT_NODE)
            } // for (int i = 0; i < size; i++)

            return dataElementList;
        } // if (size > 0)

        // no objects found:
        return null;
    } //parseObjects


    /**************************************************************************
     * Parses an objects section and create the objects. <BR/>
     *
     * @param   root                The root node. Must be either an object
     *                              or an tab object tag. 
     * @param   targetDataElement   The target data element which should be
     *                              filled with the parsed data.
     * @param   isTemplateImport    Flag indicating if a template import is
     *                              performed. For template imports also the
     *                              template attributes are  regarded.
     * @param   isTypeTranslation   Flag indicating if the method is called
     *                              within the type translation process.
     * @param   env                 The current environment.
     *
     * @return  Returns a data element fill with the parsed data.
     */
    public static DataElement parseObject (Node root,
                                           DataElement targetDataElement,
                                           boolean isTemplateImport,
                                           boolean isTypeTranslation,
                                           Environment env)
    {
        NamedNodeMap attributes;
        Node node = null;
        NodeList nodelist = null;
        int actSize = 0;
        String nodename;
        DataElement dataElement = null; // import element that holds the data

        // check if root is not null
        if (root != null)
        {
            // test if this is an OBJECT or an TABOBJECT element so that we
            // have a correct node:
            if (root.getNodeName ().equals (DIConstants.ELEM_OBJECT) ||
                root.getNodeName ().equals (DIConstants.ELEM_TABOBJECT))
            {
                // retrieve the dataElement from the target data element if it is not null
                // otherwise initialize a new data element:
                dataElement = (targetDataElement != null) ? targetDataElement : new DataElement ();

                // get the object type name from the TYPE attribute
                attributes = root.getAttributes ();

                // starting with version 2.2 the object type is defined by the TYPECODE
                // attribute of the OBJECT tag. The type code is the language independent
                // name of the object type.
                // in older xml files only the TYPE attribute is defined witch holds the
                // language specific object type name. for this files we have to generate
                // the object code from the given object name.

                // get the object type code from the TYPECODE attribute
                node = attributes.getNamedItem (DIConstants.ATTR_TYPECODE);
                // set the type code in the DataElement
                if (node != null)
                {
                    // set the type code for the data element:
                    dataElement.p_typeCode = node.getNodeValue ();

                    // for document templates set the typename found in the xml file
                    if (isTemplateImport)
                    {
                        node = attributes.getNamedItem (DIConstants.ATTR_TYPE);
                        // set the type name in the DataElement
                        if (node != null)
                        {
                            dataElement.typename = node.getNodeValue ();
                        } // if
                    } // if (this.isTemplateImport)
                    else
                    {
                        // for a normal import file get the language specific
                        // type name from the type cache.
                        dataElement.typename = BOHelpers.getTypeCache ().getTypeName (dataElement.p_typeCode);
                    } // else if (this.isTemplateImport)

                    // if no type name was found set the type code as type name.
                    if (dataElement.typename == null || dataElement.typename.length () == 0)
                    {
                        dataElement.typename = dataElement.p_typeCode;
                    } // if
                } // if (node != null)
                else        // no type code defined
                {
                    // if no type code is defined in the xml file we generate the type code from
                    // the given type name.
                    // get the object type name from the TYPE attribute
                    node = attributes.getNamedItem (DIConstants.ATTR_TYPE);
                    if (node != null)
                    {
                        // set the type name in the DataElement
                        dataElement.typename = node.getNodeValue ();
                        // get the type code for this type name
                        dataElement.p_typeCode = ObjectFactory.getTypeCodeFromName (IbsGlobals.p_app, dataElement.typename);
                        // if no type code was found set the type name as type code
                        if (dataElement.p_typeCode.isEmpty ())
                        {
                            dataElement.p_typeCode = dataElement.typename;
                        } // if
                    } // if (node != null)
                } // else no type code defined

                // get the operation from the OBJECT Tag 
                node = attributes.getNamedItem (DIConstants.ATTR_OPERATION);

                if (node != null)
                {
                    // set the operation for the data element:
                    dataElement.operation = node.getNodeValue ();
                } // if

                // try to get the attribute TABCODE from tabobject:
                if (root.getNodeName ().equals (DIConstants.ELEM_TABOBJECT))
                {
                     // get the object type code from the TABCODE attribute
                    node = attributes.getNamedItem (
                        DIConstants.ATTR_TABCODE);

                    if (node != null)
                    {

                        // set the type code for the data element.
                        dataElement.p_tabCode = node.getNodeValue ();
                    } // if (node != null)
                    // get the object type code from the TABCODE attribute
                    node = attributes.getNamedItem (DIConstants.ATTR_PRIORITY);

                    if (node != null)
                    {
                        String priority = node.getNodeValue ();
                        Integer prioInt = null;
                        try
                        {
                            prioInt = new Integer (priority);
                        } // try
                        catch (NumberFormatException e)
                        {
                            env.write ("No Number in Attribute PRIORITY of " +
                                    "Tag TABOBJECT with TYPECODE=" +
                                    dataElement.p_typeCode);
                            prioInt = new Integer (1);
                        } // catch
                        // set the type code for the data element.
                        dataElement.p_priority = prioInt.intValue ();
                    } // if (node != null)
                } // if (root.getNodeName ().equals (DIConstants.ELEM_TABOBJECT)

                // for document template files we import also the extended attributes
                // of the OBJECT tag.
                if (isTemplateImport)
                {
                    // get the DBTABLE attribute
                    node = attributes.getNamedItem (DIConstants.ATTR_DBTABLE);
                    // set the table name in the DataElement
                    if (node != null)
                    {
                        dataElement.tableName = node.getNodeValue ();
                    } // if

                    // get the CLASS attribute
                    node = attributes.getNamedItem (DIConstants.ATTR_CLASS);
                    // set the class name in the DataElement
                    if (node != null)
                    {
                        dataElement.p_className = node.getNodeValue ();
                    } // if

                    // get the ICON attribute
                    node = attributes.getNamedItem (DIConstants.ATTR_ICON);
                    // set the icon name in the DataElement
                    if (node != null)
                    {
                        dataElement.p_iconName = node.getNodeValue ();
                    } // if

                    // get the MAYEXISTIN attribute
                    node = attributes.getNamedItem (DIConstants.ATTR_MAYEXISTIN);
                    // set the icon name in the DataElement
                    if (node != null)
                    {
                        dataElement.p_mayExistIn = node.getNodeValue ();
                    } // if

                    // get the SUPERTYPECODE attribute
                    node = attributes.getNamedItem (DIConstants.ATTR_SUPERTYPECODE);
                    // set the super type code in the DataElement
                    if (node != null)
                    {
                        dataElement.p_superTypeCode = node.getNodeValue ();
                    } // if

                    // get the ISSEARCHABLE attribute
                    node = attributes.getNamedItem (DIConstants.ATTR_ISSEARCHABLE);
                    // set the isSearchable flag in the DataElement
                    if (node != null)
                    {
                        dataElement.p_isSearchable = DataElement
                            .resolveBooleanValue (node.getNodeValue ());
                    } // if

                    // get the ISINHERITABLE attribute
                    node = attributes.getNamedItem (DIConstants.ATTR_ISINHERITABLE);
                    // set the isInheritable flag in the DataElement
                    if (node != null)
                    {
                        dataElement.p_isInheritable = DataElement
                            .resolveBooleanValue (node.getNodeValue ());
                    } // if

                    // get the SHOWINMENU attribute
                    node = attributes.getNamedItem (DIConstants.ATTR_SHOWINMENU);
                    // set the showInMenu flag in the DataElement
                    if (node != null)
                    {
                        dataElement.p_isShowInMenu = DataElement
                            .resolveBooleanValue (node.getNodeValue ());
                    } // if

                    // get the SHOWINNEWS attribute
                    node = attributes.getNamedItem (DIConstants.ATTR_SHOWINNEWS);
                    // set the showInNews flag in the DataElement
                    if (node != null)
                    {
                        dataElement.p_isShowInNews = DataElement
                            .resolveBooleanValue (node.getNodeValue ());
                    } // if

                    // get the KIND attribute
                    node = attributes.getNamedItem (DIConstants.ATTR_KIND);
                    // set the kind in the DataElement
                    if (node != null)
                    {
                        dataElement.p_tabKind = node.getNodeValue ();
                    } // if

                    // get the TABCODE attribute
                    node = attributes.getNamedItem (DIConstants.ATTR_TABCODE);
                    // set the kind in the DataElement
                    if (node != null)
                    {
                        dataElement.p_tabCode = node.getNodeValue ();
                    } // if
                } // if (this.isTemplateImport)

                // check if there are more nodes:
                if (root.hasChildNodes ())
                {
                    // get the child nodes
                    nodelist = root.getChildNodes ();
                    actSize = nodelist.getLength ();
                    for (int i = 0; i < actSize; i++)
                    {
                        // parse nodes:
                        node = nodelist.item (i);
                        if (node.getNodeType () == Node.ELEMENT_NODE)
                        {
                            nodename = node.getNodeName ();
                            // config section:
                            if (nodename.equals (DIConstants.ELEM_CONFIG))
                            {
                                // parse the config section:
                                m2XMLFilter.parseConfigSection (dataElement, node, env);
                            } // if (nodename.equals (DIConstants.ELEM_SYSTEM))
                            // system section:
                            else if (nodename.equals (DIConstants.ELEM_SYSTEM))
                            {
                                // parse the system element section:
                                m2XMLFilter.parseSystemSection (dataElement, node, env);
                            } // else if (nodename.equals (DIConstants.ELEM_VALUES))
                            // values section:
                            else if (nodename.equals (DIConstants.ELEM_VALUES))
                            {
                                // parse the values section:
                                m2XMLFilter.parseValuesSection (dataElement, node, isTemplateImport, isTypeTranslation, env);
                            } // else if (nodename.equals (DIConstants.ELEM_VALUES))
                            else if (nodename.equals (DIConstants.ELEM_RIGHTS))
                            {
                                // parse the rights section:
                                m2XMLFilter.parseRightsSection (dataElement, node, env);
                            } // else if (nodename.equals (DIConstants.ELEM_RIGHTS))
                            else if (nodename.equals (DIConstants.ELEM_REFERENCES))
                            {
                                // parse the references section:
                                m2XMLFilter.parseReferencesSection (dataElement, node, env);
                            } // else if (nodename.equals (DIConstants.ELEM_REFERENCES))
                            else if (nodename.equals (DIConstants.ELEM_OBJECTS))
                            {
                                dataElement.dataElementList = m2XMLFilter.parseObjects (node, isTemplateImport, env);
                            } // else if (nodename.equals (DIConstants.ELEM_OBJECTS))
                            else if (nodename.equals (DIConstants.ELEM_TABS))
                            {
                                dataElement.tabElementList = m2XMLFilter.parseObjects (node, isTemplateImport, env);
                            } // else if (nodename.equals (DIConstants.ELEM_TABS))
                            else if (nodename.equals (DIConstants.ELEM_ATTACHMENTS))
                            {
                                m2XMLFilter.parseAttachmentsSection (dataElement, node, env);
                            } // else if (nodename.equals (DIConstants.ELEM_ATTACHMENTS))
                            else if (nodename.equals (DIConstants.ELEM_CONTENT))
                            {
                                m2XMLFilter.parseContentSection (dataElement, node, isTemplateImport, env);
                            } // else if (nodename.equals (DIConstants.ELEM_CONTENT))
                            else if (nodename.equals (DIConstants.ELEM_LOGIC))
                            {
                                m2XMLFilter.parseLogicSection (dataElement, node, isTemplateImport, env);
                            } // else if (nodename.equals (DIConstants.ELEM_LOGIC))
                            else
                            {
                                // node not valid therefore ignore it
                            } // else
                        } // if (node.getNodeType == Node.ElementNode)
                    } // for (int i = 0; i < size; i++)
                } // if (root.hasChildNodes ())
                // now remove the child node because it is not needed anymore
                // this has to be done in order to avoid memory leaks
                try
                {
                    for (int j = actSize - 1; j >= 0; j--)
                    {
                        node = nodelist.item (j);
                        node = root.removeChild (node);
                        node = null;
                    } // for (int j = 0; j < size; j++)
                } // try
                catch (DOMException e)
                {
                    // ignore any error
                } // catch
            } // if object found
            else                        // object not found
            {
//                showDebug ("no object or tab node" + root.getNodeName ());
                return null;
            } // else object not found
        } // if root is not null
        else                            // root is null
        {
            return null;
        } // else root is null

        return dataElement;
    } // parseObject


    /**************************************************************************
     * Get the value of a node. <BR/>
     *
     * @param   node    Node from which to get the value.
     *
     * @return  The value of the node.
     *          <CODE>""</CODE> if the node is empty.
     */
    protected static String getNodeValue2 (Node node)
    {
        Text text;
        String retVal = null;

        // get the text value:
        text = (Text) node.getFirstChild ();
        if (text != null)
        {
            // set the value:
            retVal = text.getNodeValue ();
        } // if
        else
        {
            // initialize the value with an empty string to signal
            // the presence of the tag:
            // This is important for the update import process.
            retVal = "";
        } // else

        // return the result:
        return retVal;
    } // getNodeValue2


    /**************************************************************************
     * Parse the CONFIG section of the import xml document and
     * store found entries in the data element. <BR/>
     *
     * @param   dataElement Data element where to store the entry values.
     * @param   root        Root node of the section.
     * @param   env         The current environment.
     */
    protected static void parseConfigSection (DataElement dataElement,
                                              Node root, Environment env)
    {
        NodeList nodeList = null;
        int nodeCount = 0;
        Node node = null;
        String nodeName = null;
        String infoButtonList = null;
        String contentButtonList = null;
        String transformation = null;
        String nameTemplate = null;

        // get the subnodes:
        nodeList = root.getChildNodes ();
        nodeCount = nodeList.getLength ();

        // loop through the elements
        for (int i = 0; i < nodeCount; i++)
        {
            node = nodeList.item (i);

            // check if this is an element node
            if (node.getNodeType () == Node.ELEMENT_NODE)
            {
                // normalize the node to ensure that all text values are together
                node.normalize ();
                nodeName = node.getNodeName ();

                // check the kind of element we found:
                // INFOBUTTONS:
                if (nodeName.equalsIgnoreCase (DIConstants.ELEM_INFOBUTTONS))
                {
                    // set the value:
                    infoButtonList = m2XMLFilter.getNodeValue2 (node);
                } // if INFOBUTTONS
                // CONTENTBUTTONS:
                else if (nodeName.equalsIgnoreCase (DIConstants.ELEM_CONTENTBUTTONS))
                {
                    // set the value:
                    contentButtonList = m2XMLFilter.getNodeValue2 (node);
                } // else if CONTENTBUTTONS
                // TRANSFORMATION:
                else if (nodeName.equalsIgnoreCase (DIConstants.ELEM_TRANSFORMATION))
                {
                    // set the value:
                    transformation = m2XMLFilter.getNodeValue2 (node);
                } // else if TRANSFORMATION
                // NAMETEMPLATE:
                else if (nodeName.equalsIgnoreCase (DIConstants.ELEM_NAMETEMPLATE))
                {
                    // set the value:
                    nameTemplate = m2XMLFilter.getNodeValue2 (node);
                } // else if NAMETEMPLATE
                else
                {
                    IOHelpers.showMessage (
                        "Invalid node in XML tree: " + nodeName,
                        env);
                } // else
            } // if
        } // for i

        // store the config values in the dataElement
        dataElement.setConfigValues (infoButtonList, contentButtonList,
            transformation, nameTemplate);
    } // parseConfigSection


    /**************************************************************************
     * parses the SYSTEM section of the import xml document and
     * stores value entries found in the DataElement. <BR/>
     *
     * @param   dataElement Data element where to store the entry values.
     * @param   root        Root node of the section.
     * @param   env         The current environment.
     */
    protected static void parseSystemSection (DataElement dataElement,
                                              Node root, Environment env)
    {
        NodeList systemNodeList;
        int systemSize;
        Node systemNode;
        Node attributeNode;
        Text text;
        String importId                 = "";
        String importIdDomain           = "";
        boolean importIdAddWspUser      = false;
        String importContainerType      = "";
        String importContainerId        = "";
        String importContainerIdDomain  = "";
        String importContainerTabName   = "";
        String importShowSystemSection  = "";

        // if this values are not present in the import node
        // a <code>null</code> value must be passed to the method
        // dataElement.setSystemValues ().
        String importName               = null;
        String importDescription        = null;
        String importValidUntil         = null;
        String importShowInNews         = null;

        // get the 'DISPLAY' attribute of the 'SYSTEM' node
        NamedNodeMap attributes = root.getAttributes ();
        Node attrNode = attributes.getNamedItem (DIConstants.ATTR_DISPLAY);
        // get the value of the DISPLAY attribute
        if (attrNode != null)
        {
            importShowSystemSection = attrNode.getNodeValue ();
        } // if
        // get the system subnodes
        systemNodeList = root.getChildNodes ();
        systemSize = systemNodeList.getLength ();
        // loop through the elements
        for (int is = 0; is < systemSize; is++)
        {
            systemNode = systemNodeList.item (is);
            // check if this is an element node
            if (systemNode.getNodeType () == Node.ELEMENT_NODE)
            {
                // normalize the node to ensure that all text values are together
                systemNode.normalize ();
                // check the kind of element we found
                if (systemNode.getNodeName ().equalsIgnoreCase (DIConstants.ELEM_ID))
                {
                    // get the id domain attribute
                    attributes = systemNode.getAttributes ();
                    attributeNode = attributes.getNamedItem (DIConstants.ATTR_DOMAIN);
                    // check if DOMAIN attribute has been set
                    if (attributeNode != null)
                    {
                        importIdDomain = attributeNode.getNodeValue ();
                    } // if

                    attributeNode = attributes.getNamedItem (DIConstants.ATTR_IDADDWSPUSER);
                    // check if ADDWSPUSER attribute has been set
                    if (attributeNode != null)
                    {
                        String value =
                            attributeNode.getNodeValue ().trim ().toLowerCase ();
                        importIdAddWspUser =
                            value.equals ("true") || value.equals ("y");
                    } // if

                    // get the text value
                    text = (Text) systemNode.getFirstChild ();
                    if (text != null)
                    {
                        importId = text.getNodeValue ();
                    } // if
                } // if (systemNode.getNodeName ().equalsIgnoreCase (DIConstants.ELEM_ID))
                else if (systemNode.getNodeName ().equalsIgnoreCase (
                    DIConstants.ELEM_NAME))
                {
                    // initialize the value with an empty string to signal the
                    // presence of the NAME tag. This is important for the
                    // update import process.
                    importName = "";
                    text = (Text) systemNode.getFirstChild ();
                    if (text != null)
                    {
                        importName = text.getNodeValue ();
                    } // if
                } //else if (systemNode.getNodeName ().equalsIgnoreCase (DIConstants.ELEM_NAME))
                else if (systemNode.getNodeName ().equalsIgnoreCase (
                    DIConstants.ELEM_DESCRIPTION))
                {
                    // initialize the value with an empty string to signal the
                    // presence of the DESCRIPTION tag. This is important for the
                    // update import process.
                    importDescription = "";
                    text = (Text) systemNode.getFirstChild ();
                    if (text != null)
                    {
                        importDescription = text.getNodeValue ();
                    } // if
                } // else if (systemNode.getNodeName ().equalsIgnoreCase (DIConstants.ELEM_DESCRIPTION))
                else if (systemNode.getNodeName ().equalsIgnoreCase (
                    DIConstants.ELEM_VALIDUNTIL))
                {
                    // initialize the value with an empty string to signal the
                    // presence of the VALIDUNTIL tag. This is important for the
                    // update import process.
                    importValidUntil = "";
                    text = (Text) systemNode.getFirstChild ();
                    if (text != null)
                    {
                        importValidUntil = text.getNodeValue ();
                    } // if
                } // else if (systemNode.getNodeName ().equalsIgnoreCase (DIConstants.ELEM_VALIDUNTIL))
                else if (systemNode.getNodeName ().equalsIgnoreCase (
                    DIConstants.ELEM_CONTAINER))
                {
                    // parse the container node and set the values
                    m2XMLFilter.parseContainerNode (systemNode, dataElement);
                    // because we set the values again below we must store the
                    // container settings. This is a workaround in order
                    // to be able to use the
                    importContainerType = dataElement.containerType;
                    importContainerId = dataElement.containerId;
                    importContainerIdDomain = dataElement.containerIdDomain;
                    importContainerTabName = dataElement.containerTabName;
                } // else if (systemNode.getNodeName ().equalsIgnoreCase (DIConstants.ELEM_CONTAINER))
                else if (systemNode.getNodeName ().equalsIgnoreCase (
                    DIConstants.ELEM_SHOWINNEWS))
                {
                    // initialize the value with an empty string to signal the
                    // presence of the SHOWINNEWS tag. This is important for the
                    // update import process.
                    importShowInNews = "";
                    text = (Text) systemNode.getFirstChild ();
                    if (text != null)
                    {
                        importShowInNews = text.getNodeValue ();
                    } // if
                } // else if (systemNode.getNodeName ().equalsIgnoreCase (DIConstants.ELEM_SHOWINNEWS))
                else
                {
//showDebug ("invalid node: " + systemNode.getNodeName ());
                } //else
            } // if (node.getNodeTyoe == Node.ELEMENT_TYPE)
        } // for (int is = 0; is < systemSize; is++)

        // replace the sequence '\\' + 'n' with '\n'.
        importDescription = m2XMLFilter.unescapeLineSeparators (importDescription);

        // select the correct display mode for the system section.
        // there are 3 possibilitys:
        // "YES" -> show system section on top of the form (default)
        // "NO"  -> hide system section
        // "BOTTOM" -> show system section on bottom of the form
        int displayMode = DataElement.DSP_MODE_TOP;
        if (importShowSystemSection.equalsIgnoreCase (DIConstants.DISPLAY_NO))
        {
            displayMode = DataElement.DSP_MODE_HIDE;
        } // if
        else if (importShowSystemSection.equalsIgnoreCase (DIConstants.DISPLAY_BOTTOM))
        {
            displayMode = DataElement.DSP_MODE_BOTTOM;
        } // else if

        // store the systems values in the dataElement
        dataElement.setSystemValues (importId, importIdDomain,
                                     importIdAddWspUser,
                                     importName, importDescription,
                                     importValidUntil, importContainerType,
                                     importContainerId, importContainerIdDomain,
                                     importContainerTabName,
                                     importShowInNews, displayMode);
    } // parseSystemSection


    /**************************************************************************
     * Parses the container tag in an system section of an  document and
     * sets its values in an dataElement instance. <BR/>
     *
     * @param containerNode         root node of the subtree to parse
     * @param dataElement           the dataElement instance to store
     *                              the values in
     */
    private static void parseContainerNode (Node containerNode,
                                     DataElement dataElement)
    {
        NamedNodeMap attributes;
        String containerType        = "";
        String actContainerId       = "";
        String containerIdDomain    = "";
        String containerTabName     = "";
        Node text;
        NodeList nodelist;
        Node node;
        Node attributeNode;
        int actSize;

        // read the type attribute
        attributes = containerNode.getAttributes ();
        // check if we got any attributes
        if (attributes != null)
        {
            // container tab name
            attributeNode = attributes.getNamedItem (DIConstants.ATTR_TABNAME);
            if (attributeNode != null)
            {
                containerTabName = attributeNode.getNodeValue ();
            } // if
            // container type
            attributeNode = attributes.getNamedItem (DIConstants.ATTR_TYPE);
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
                    // parse through the OBJECT sections
                    nodelist = containerNode.getChildNodes ();
                    // get amount of objects
                    actSize = nodelist.getLength ();
                    // find the <OBJECTS> element
                    for (int i = 0; i < actSize; i++)
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
                            text = node.getFirstChild ();
                            if (text != null)
                            {
                                actContainerId = text.getNodeValue ();
                            } // if
                        } // if (systemNode.getNodeName ().equalsIgnoreCase (DIConstants.ELEM_ID))
                    } // for (int i = 0; i < size; i++)
                } // if (containerNode.hasChildNodes ())
            } // if (containerType.equalsIgnoreCase (DIConstants.CONTAINER_EXTKEY))
            else    // container is not an external key
            {
                // read the value for custom operation:
                text = containerNode.getFirstChild ();
                // text node found?
                if (text != null)
                {
                    actContainerId = text.getNodeValue ();
                } // if
            } // else container is not an external key
        } // if (attributes != null)
        // set the values in the importScriptElement
        dataElement.containerType = containerType;
        dataElement.containerId = actContainerId;
        dataElement.containerIdDomain = containerIdDomain;
        dataElement.containerTabName = containerTabName;
    } // parseContainerNode


    /**************************************************************************
     * parses the VALUES section of the import xml document and
     * stores value entries found in the DataElement. <BR/>
     *
     * @param   dataElement         The data element that holds the values info.
     * @param   root                The values node.
     * @param   isTemplateImport    Flag indicating if a template import is
     *                              performed. For template imports also the
     *                              template attributes are  regarded.
     * @param   isTypeTranslation   Flag indicating if the method is called
     *                              within the type translation process.
     *                              
     * @param   env                 The current environment.
     */
    protected static void parseValuesSection (DataElement dataElement,
                                              Node root,
                                              boolean isTemplateImport,
                                              boolean isTypeTranslation,
                                              Environment env)
    {
        NodeList valuesNodeList;
        int valuesSize;
        Node valueNode;
        NamedNodeMap valuesAttributes;
        String valueName;
        String valueMappingName;
        String valueType;
        String valueValue;
        String valueMandatory;
        String valueReadonly;
        String valueInfo;
        String valueTypeFilter;
        String valueSearchRoot;
        String valueSearchRootIdDomain;
        String valueSearchRootId;
        String valueSearchRecursive;
        String valueQueryName;
        String valueOptions;
        String valueUnit;
        String valueDomain;
        String valueContext;
        String valueViewType;
        String valueNoColumns;
        String valueMultiSelection;
        String valueRefType;
        String valueMlKey;
        String valueShowInLinks;

        long valueSize;
        // Must be of class <CODE>Serializable</CODE>
        // because this is a common super class of
        // <CODE>String</CODE> and <CODE>Integer</CODE>.
        Vector<Serializable> reminderParams;
        Vector<Serializable> fileParams;
        Node text;
        String valueEmptyOption;
        String valueRefresh;
        // Must be of class <CODE>Serializable</CODE>
        // because this is a common super class of
        // <CODE>String</CODE> and <CODE>Integer</CODE>.
        Vector<?> valueParams;

        valuesNodeList = root.getChildNodes ();
        valuesSize = valuesNodeList.getLength ();

        Vector<ValueDataElement> templateValues = null;

        // check if a type translation is performed
        if (isTypeTranslation)
        {
            // check if the data element' values vector's size of the current data element
            // matches the one of the node
            if (dataElement.values != null && dataElement.values.size () == valuesSize)
            {
                // assign the values of the data element to a temporary variable
                // for beeing able to initialize every data element value with the
                // default values
//                @SuppressWarnings ("unchecked") // suppress compiler warning
                templateValues = (Vector<ValueDataElement>) dataElement.values.clone ();

                // remove the values for being able to refill the vector
                dataElement.values.removeAllElements ();
            } // if
            // mistmatch
            else
            {
                env.write ("The number of data element values does not match the number of values" +
                        "within the XML node to parse. No default values can be set.");

                return;
            } // else mismatch
        } // isTypeTranslation

        // now go through all the values
        for (int i = 0; i < valuesSize; i++)
        {
            // check if the values of the template are available
            if (templateValues != null)
            {
                ValueDataElement vde = templateValues.get (i);

                // initialize the values with the default values
                valueName               = vde.field;
                valueMappingName        = vde.mappingField;
                valueType               = vde.type;
                valueValue              = null;
                valueMandatory          = vde.mandatory;
                valueReadonly           = vde.p_readonly;
                valueInfo               = vde.info;
                valueTypeFilter         = vde.typeFilter;
                valueSearchRoot         = vde.searchRoot;
                valueSearchRootIdDomain = vde.searchRootIdDomain;
                valueSearchRootId       = vde.searchRootId;
                valueSearchRecursive    = vde.searchRecursive;
                valueQueryName          = vde.queryName;
                valueOptions            = vde.options;
                valueUnit               = vde.p_valueUnit;
                valueEmptyOption        = vde.emptyOption;
                valueRefresh            = vde.refresh;
                valueDomain             = vde.p_domain;
                valueSize               = vde.p_size;
                valueParams             = vde.p_subTags;
                reminderParams          = null;
                valueContext            = vde.p_context;
                valueViewType           = vde.viewType;
                valueNoColumns          = vde.noColumns;
                valueMultiSelection     = vde.multiSelection;
                fileParams              = null;
                valueRefType            = null;
                valueMlKey              = vde.mlKey;
                valueShowInLinks        = vde.p_showInLinks;
            } // if
            // no template values available
            else
            {
                // initialize the values
                valueName               = null;
                valueMappingName        = null;
                valueType               = null;
                valueValue              = null;
                valueMandatory          = null;
                valueReadonly           = null;
                valueInfo               = null;
                valueTypeFilter         = null;
                valueSearchRoot         = null;
                valueSearchRootIdDomain = null;
                valueSearchRootId       = null;
                valueSearchRecursive    = null;
                valueQueryName          = null;
                valueOptions            = null;
                valueUnit               = null;
                valueEmptyOption        = null;
                valueRefresh            = null;
                valueDomain             = null;
                valueSize               = -1;
                valueParams             = null;
                reminderParams          = null;
                valueContext            = null;
                valueViewType           = null;
                valueNoColumns          = null;
                valueMultiSelection     = null;
                fileParams              = null;
                valueRefType            = null;
                valueMlKey              = null;
                valueShowInLinks        = null;                
            } // else no template values available

            // get value node from values nodelist
            valueNode = valuesNodeList.item (i);
            // check if this is an element node
            if (valueNode.getNodeType () == Node.ELEMENT_NODE)
            {
                // get the values from an VALUES element
                // get field name
                valuesAttributes = valueNode.getAttributes ();

                if (valuesAttributes.getNamedItem (DIConstants.ATTR_FIELD) != null)
                {
                    valueName = valuesAttributes.getNamedItem (DIConstants.ATTR_FIELD).getNodeValue ();
                } // if

                // for document template files we import also the extended attributes
                // of the VALUE tag.
                if (isTemplateImport)
                {
                    // get mapping field name
                    if (valuesAttributes.getNamedItem (DIConstants.ATTR_DBFIELD) != null)
                    {
                        valueMappingName = valuesAttributes.getNamedItem (DIConstants.ATTR_DBFIELD).getNodeValue ();
                    } // if
                } // if (this.isTemplateImport)

                // get multilang key
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_MLKEY) != null)
                {
                    valueMlKey = valuesAttributes.getNamedItem (
                        DIConstants.ATTR_MLKEY).getNodeValue ();
                } // if
                
                // get field type
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_TYPE) != null)
                {
                    valueType = valuesAttributes.getNamedItem (
                        DIConstants.ATTR_TYPE).getNodeValue ();
                } // if

                // get mandatory flag
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_MANDATORY) != null)
                {
                    valueMandatory = valuesAttributes.getNamedItem (
                        DIConstants.ATTR_MANDATORY).getNodeValue ();
                } // if

                // get readonly flag:
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_READONLY) != null)
                {
                    valueReadonly = valuesAttributes.getNamedItem (
                        DIConstants.ATTR_READONLY).getNodeValue ();
                } // if

                // get field info
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_INFO) != null)
                {
                    valueInfo = valuesAttributes.getNamedItem (
                        DIConstants.ATTR_INFO).getNodeValue ();
                } // if

                // starting with release 2.2 all object types are referenced by the type code and not
                // by the type name. For this reason we introduced the new attribute TYPECODEFILTER.
                // the TYPECODEFILTER holds a list of type CODES. for backward compatibility
                // we support also the old attribute TYPEFILTER witch holds a list of type NAMES.
                //
                // first we get the new attribute TYPECODEFILTER for objectref fields.
                // if the attribute not exists we get the old attribute TYPEFILTER and
                // convert the type names in type codes.

                Node attr = valuesAttributes.getNamedItem (DIConstants.ATTR_TYPECODEFILTER);
                if (attr != null)
                {
                    // set the type code list
                    valueTypeFilter = attr.getNodeValue ();
                } // if (attr != null)
                else
                {
                    // get the type name list and convert the names in codes
                    attr = valuesAttributes.getNamedItem (DIConstants.ATTR_TYPEFILTER);
                    if (attr != null)
                    {
                        String nameList = attr.getNodeValue ();
                        StringBuffer codeList = new StringBuffer ();
                        // tokenize the name list
                        StringTokenizer tokenizer =
                            new StringTokenizer (nameList, DIConstants.OPTION_DELIMITER);
                        // loop through all the type names
                        while (tokenizer.hasMoreTokens ())
                        {
                            // get a typename
                            String actTypeName = tokenizer.nextToken ();
                            String typeCode = ObjectFactory
                                .getTypeCodeFromName (
                                    env.getApplicationInfo (), actTypeName);
                            if (typeCode != null && typeCode.length () > 0)
                            {
                                if (codeList.length () > 0)
                                {
                                    codeList.append (DIConstants.OPTION_DELIMITER);
                                } // if
                                codeList.append (typeCode);
                            } // if (typeCode != null && typeCode.length () > 0)
                        } // while (tokenizer.hasMoreTokens ())
                        valueTypeFilter = codeList.toString ();
                    } // if (attr != null)
                } // else if (attr != null)

                // get the searchroot for objectref field
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_SEARCHROOT) != null)
                {
                    valueSearchRoot = valuesAttributes.getNamedItem (
                        DIConstants.ATTR_SEARCHROOT).getNodeValue ();
                } // if

                // get the searchroot id domain for objectref field
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_SEARCHROOTIDDOMAIN) != null)
                {
                    valueSearchRootIdDomain = valuesAttributes.getNamedItem (
                        DIConstants.ATTR_SEARCHROOTIDDOMAIN).getNodeValue ();
                } // if

                // get the searchroot id for objectref field
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_SEARCHROOTID) != null)
                {
                    valueSearchRootId = valuesAttributes.getNamedItem (
                        DIConstants.ATTR_SEARCHROOTID).getNodeValue ();
                } // if

                // get the searchrecursive flag for objectref field
                if (valuesAttributes
                    .getNamedItem (DIConstants.ATTR_SEARCHRECURSIVE) != null)
                {
                    valueSearchRecursive = valuesAttributes.getNamedItem (
                        DIConstants.ATTR_SEARCHRECURSIVE).getNodeValue ();
                } // if

                // get the name of the query to be performed to get data for this field
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_QUERYNAME) != null)
                {
                    valueQueryName = valuesAttributes.getNamedItem (
                        DIConstants.ATTR_QUERYNAME).getNodeValue ();
                } // if

                // get the options of the QUERYSELECTIONBOX or selectionbox
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_OPTIONS) != null)
                {
                    valueOptions = valuesAttributes.getNamedItem (
                        DIConstants.ATTR_OPTIONS).getNodeValue ();
                } // if

                // get the refresh attribute of the QUERYSELECTIONBOX value
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_REFRESH) != null)
                {
                    valueRefresh = valuesAttributes.getNamedItem (
                        DIConstants.ATTR_REFRESH).getNodeValue ();
                } // if

                // get the emptyoption of the queryselection value
                if (valuesAttributes
                    .getNamedItem (DIConstants.ATTR_EMPTYOPTION) != null)
                {
                    valueEmptyOption = valuesAttributes.getNamedItem (
                        DIConstants.ATTR_EMPTYOPTION).getNodeValue ();
                } // if

                // get value of the field unit
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_UNIT) != null)
                {
                    valueUnit = valuesAttributes.getNamedItem (
                        DIConstants.ATTR_UNIT).getNodeValue ();
                } // if

                // get value of the domain attribute
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_DOMAIN) != null)
                {
                    valueDomain = valuesAttributes.getNamedItem (
                        DIConstants.ATTR_DOMAIN).getNodeValue ();
                } // if

                // get value of the viewType
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_VIEWTYPE) != null)
                {
                    valueViewType = valuesAttributes.getNamedItem (
                        DIConstants.ATTR_VIEWTYPE).getNodeValue ();
                } // if

                // get value of the context attribute
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_CONTEXT) != null)
                {
                    valueContext = valuesAttributes.getNamedItem (
                        DIConstants.ATTR_CONTEXT).getNodeValue ();
                } // if

                // get value of the noColumns
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_NO_COLUMNS) != null)
                {
                    valueNoColumns = valuesAttributes.getNamedItem (
                        DIConstants.ATTR_NO_COLUMNS).getNodeValue ();
                } // if

                // get value of the multiSelection
                if ((attr = valuesAttributes.getNamedItem (DIConstants.ATTR_MULTISELECTION)) != null)
                {
                    valueMultiSelection = attr.getNodeValue ();
                } // if

                // get value of the refType
                if ((attr = valuesAttributes.getNamedItem (DIConstants.ATTR_REF_TYPE)) != null)
                {
                    valueRefType = attr.getNodeValue ();
                } // if

                // get the options for the SIZE field type
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_SIZE) != null)
                {
                    try
                    {
                        valueSize = Long.parseLong (valuesAttributes
                            .getNamedItem (DIConstants.ATTR_SIZE)
                                .getNodeValue ().trim ());
                    } // try
                    catch (NumberFormatException e)
                    {
                        // should not occur; print error message:
/*
                        IOHelpers.showMessage (e, this.app, this.sess,
                            this.env, true);
*/
                    } // catch
                    catch (DOMException e)
                    {
                        // should not occur; print error message:
/*
                        IOHelpers.showMessage (e, this.app, this.sess,
                            this.env, true);
*/
                    } // catch
                } // if

                // get show in links flag
                if (valuesAttributes.getNamedItem (DIConstants.ATTR_SHOWINLINKS) != null)
                {
                    valueShowInLinks = valuesAttributes.getNamedItem (
                        DIConstants.ATTR_SHOWINLINKS).getNodeValue ();
                } // if

                // if type is QUERY, QUERYSELECTIONBOX, QUERYSELECTIONBOXINT or
                // QUERYSELECTIONBOXNUM
                if (DIConstants.VTYPE_QUERY.equals (valueType) ||
                    DIConstants.VTYPE_QUERYSELECTIONBOX.equals (valueType) ||
                    DIConstants.VTYPE_QUERYSELECTIONBOXINT.equals (valueType) ||
                    DIConstants.VTYPE_QUERYSELECTIONBOXNUM.equals (valueType))
                {
                    valueParams = m2XMLFilter.parseParametersSection (valueNode, env);
                } // if
                // if type is FIELDREF or VALUEDOMAIN
                else if (DIConstants.VTYPE_FIELDREF.equals (valueType) ||
                         DIConstants.VTYPE_VALUEDOMAIN.equals (valueType))
                {
                    valueParams = m2XMLFilter.parseSubRefSection (valueNode, valueType, env);
                } // else if
                // if type is FIELDREF
                else if (DIConstants.VTYPE_REMINDER.equals (valueType))
                {
                    reminderParams = m2XMLFilter.parseReminderAttributes (
                        valuesAttributes, templateValues != null ?
                            templateValues.get (i) : null);
                } // else if type is REMINDER
                else if (DIConstants.VTYPE_FILE.equals (valueType))
                {
                    fileParams = m2XMLFilter.parseFileAttributes (
                        valuesAttributes, templateValues != null ?
                            templateValues.get (i) : null);
                } // else if type is FILE

                // check if the ref type is an extkey or path
                // and the type is OBJECTREF, FIELDREF or VALUEDOMAIN
                if (valueRefType != null &&
                        (valueRefType.equals (DIConstants.ATTRVAL_REF_TYPE_EXTKEY) ||
                            valueRefType.equals (DIConstants.ATTRVAL_REF_TYPE_PATH)) &&
                            (DIConstants.VTYPE_OBJECTREF.equals (valueType) ||
                                DIConstants.VTYPE_FIELDREF.equals (valueType) ||
                                DIConstants.VTYPE_VALUEDOMAIN.equals (valueType)))
                {
                    StringBuilder idDomain = new StringBuilder ();
                    // if reftype is extkey
                    if (valueRefType.equals (DIConstants.ATTRVAL_REF_TYPE_EXTKEY))
                    {
                        valueValue = m2XMLFilter.parseExtkey (valueType,
                                valueNode, env, idDomain);
                    } // if
                    // else if reftype is path
                    else if (valueRefType.equals (DIConstants.ATTRVAL_REF_TYPE_PATH))
                    {
                        valueValue = m2XMLFilter.parsePath (valueType,
                                valueNode, env, idDomain);
                    } // if
                    // check if there was an idDomain set:
                    if (idDomain.length () > 0)
                    {
                        valueDomain = idDomain.toString ();
                    } // if
                } // if
                else
                {
                    // normalize the node to ensure that all text values are together
                    valueNode.normalize ();
                    // get field value
                    text = valueNode.getFirstChild ();
                    if (text != null)
                    {
                        if (isTypeTranslation &&
                                valueType.startsWith (DIConstants.VTYPE_SELECTIONBOX))
                        {
                            // IBS-577 SELECTIONBOX value gets lost during type translation
                            // SELECTIONBOX value fiels have the OPTION nodes within the 
                            // dom tree before the value elem.
                            //
                            // <VALUE FIELD="Fieldname">
                            // <OPTION SELECTED="1">Value1</OPTION>
                            // <OPTION>Value2</OPTION>
                            // <OPTION>Value3</OPTION>Value3</VALUE>
                            //
                            // So pass by all OPTION nodes.
                            while (text.getNextSibling () != null)
                            {
                                text = text.getNextSibling ();
                            } // while
                        } // if
                        
                        valueValue = text.getNodeValue ();
                        // when it is a password encrypt it:
                        if (DIConstants.VTYPE_PASSWORD.equals (valueType))
                                            // is it a password ?
                        {
                            // encrypt the password:
                            valueValue = EncryptionManager.decrypt (valueValue);
                        } // if is it a password
                        // special case: due to the substructure of FIELDREF or
                        // VALUEDOMAIN in can happen that the 
                        // value that is returned includes or consists of a "\n"
                        // character. Normalize() does not remove such characters 
                        // This must be removed!
                        /* BB: Can be activated if neccessary
                        else if (DIConstants.VTYPE_FIELDREF.equals (valueType) ||
                                DIConstants.VTYPE_VALUEDOMAIN.equals (valueType))
                        {
                            valueValue = valueValue.replaceAll("\n", "");
                        } // else if (DIConstants.VTYPE_FIELDREF.equals (valueType) ...
                        */
                    } // if (text != null)
                    else
                    {
                        valueValue = "";
                    } // else
                } // else

                // if no value type is defined we assume it as TEXT
                if (valueType == null)
                {
                    valueType = DIConstants.VTYPE_TEXT;
                } // if

                // replace the sequence '\\' + 'n' with '\n'.
                valueValue = m2XMLFilter.unescapeLineSeparators (valueValue);
                // store the value in the dataElement
                dataElement.addValue (valueName, valueType, valueValue,
                    valueMandatory, valueReadonly, valueInfo, valueTypeFilter,
                    valueSearchRoot, valueSearchRootIdDomain, valueSearchRootId, valueSearchRecursive, valueMappingName,
                    valueQueryName, valueOptions, valueUnit, valueEmptyOption,
                    valueRefresh, valueParams, valueDomain, valueSize,
                    reminderParams, valueContext, valueViewType, valueNoColumns,
                    valueMultiSelection, fileParams, valueMlKey, valueShowInLinks);
            } // if (valueNode.getNodeType () == Node.ELEMENT_NODE)
        } // for (int iv = 0; iv < valuesSize; iv++)
    } // parseValuesSection

    
    /**************************************************************************
     * Parses the value node content if the refType is EXTKEY. <BR/>
     *
     * @param   valueType   The value data element's type.
     * @param   valueNode   The value node.
     * @param   env         The current environment.
     * @param   idDomainValue   Value for id domain. This field will be filled
     *                      with id domain info for EXTKEY.
     * 
     * @return The resolved reference:<BR/>
     * <OID>,<NAME> (for OBJECTREF)<BR/>
     * <OID> (for FIELDREF, VALUEDOMAIN)
     */
    protected static String parseExtkey (String valueType, Node valueNode,
                                         Environment env,
                                         StringBuilder idDomainValue)
    {
        String methodName = "m2XMLFilter.parseExtkey";
        
        // ret value 
        StringBuilder ret = null;
        // ext key values
        String idDomain = null;
        String id = null;
        
        // parse through the child nodes
        NodeList nodelist = valueNode.getChildNodes ();
        // get amount of objects
        int actSize = nodelist.getLength ();
        // find the <ID> element
        for (int i = 0; i < actSize; i++)
        {  
             // get the node
            Node idNode = nodelist.item (i);
            
            // check if it is an ID node
            if (idNode.getNodeName ().equalsIgnoreCase (DIConstants.ELEM_ID))
            {
                // get the id domain attribute
                NamedNodeMap attributes = idNode.getAttributes ();
                Node attributeNode = attributes.getNamedItem (DIConstants.ATTR_DOMAIN);
                // check if DOMAIN attribute has been set
                if (attributeNode != null)
                {
                    idDomain = attributeNode.getNodeValue ();
                } // if
                // get the text value
                Node text = idNode.getFirstChild ();
                if (text != null)
                {
                    id = text.getNodeValue ();
                } // if
                
                // exit the loop
                break;
            } // if
        } // for
        
        // check first if an id value has been defined
        if (id != null && id.length () > 0)
        {
            // resolve the key mapping
            OID oid = BOHelpers.getOidByExtKey (idDomain, id, env);

            // check if an oid was found:
            if (oid != null)
            {
                // set return value according to the value type:
                ret = getRefFieldContentFromOid (oid, valueType, env);
            } // if
            else
            {
                // store the id domain and the id for later usage:
                idDomainValue.append (idDomain);
                ret = new StringBuilder (id);
            } // else
    } // if
        else
        {
            IOHelpers.showMessage (
                    methodName +
                    " Error: No ID found within ID (=EXTKEY) tag",
                    env);
        } // else
        
        return ret.toString ();
    } // parseExtkey
    
    
    /**************************************************************************
     * Parses the value node content if the refType is PATH. <BR/>
     *
     * @param   valueType   The value data element's type.
     * @param   node        The value node child node.
     * @param   env         The current environment.
     * @param   idDomainValue   Value for id domain. This field will be filled
     *                      with id domain info for EXTKEY.
     * 
     * @return The resolved reference:<BR/>
     * <OID>,<NAME> (for OBJECTREF)<BR/>
     * <OID> (for FIELDREF, VALUEDOMAIN)
     */
    protected static String parsePath (String valueType, Node valueNode,
                                       Environment env,
                                       StringBuilder idDomainValue)
    {
        // ret value 
        StringBuilder ret = null;
        
        OID oid = null;
        boolean [] boolArray = {true};
        
        String methodName = "m2XMLFilter.parsePath";
        
        // normalize the node to ensure that all text values are together
        valueNode.normalize ();
        // get field value
        Node text = valueNode.getFirstChild ();
        if (text != null)
        {
            String path = text.getNodeValue ();
        
            // we assume that the value is a path
            // definition and try to resolve it
            oid = BOHelpers.resolveObjectPath (
                    path, boolArray, null, env);
            
            // check if an oid was found:
            if (oid != null)
            {
                ret = getRefFieldContentFromOid (oid, valueType, env);
            } // if
            else
            {
                // store the PATH for later usage:
                idDomainValue.append (DIConstants.PATH_IDDOMAIN);
                ret = new StringBuilder (path);
            } // else
        } // if
        else
        {
            IOHelpers.showMessage (
                    methodName +
                    " Error: No content found for VALUE field",
                    env);
        } // else
        
        return ret.toString ();
    } // parsePath
    
    
    /**************************************************************************
     * Create the value field content from the OID depending on the given
     * type. <BR/>
     *
     * @param   oid         The OID.
     * @param   valueType   The value field type.
     * @param   env         The current environment.
     * 
     * @return The value field content depending on the given type:<BR/>
     * <OID>,<NAME> (for OBJECTREF)
     * <OID> (for FIELDREF, VALUEDOMAIN)
     */
    public static StringBuilder getRefFieldContentFromOid (OID oid,
                                                           String valueType,
                                                           Environment env)
    {
        // ret value 
        StringBuilder ret = new StringBuilder ();
     
        String methodName = "m2XMLFilter.getReffieldValueFromOid";
        
        ret.append (oid.toString ());
        
        if (DIConstants.VTYPE_OBJECTREF.equals (valueType))
        {
            ret.append (DIConstants.OBJECTREF_DELIMITER);
            Vector<BusinessObjectInfo> infos =
                BOHelpers.findObjects (null, null, new StringBuilder (oid.toStringBuilderQu ()), null, env);
            
            // check if an object has been found
            if (infos != null && infos.size () > 0)
            {
                ret.append (infos.get (0).p_name);
            } // else
            else
            {
                IOHelpers.showMessage (
                        methodName +
                        " Error: No object found for OID " + oid.toString (),
                        env);
            } // else
        } // if
        
        return ret;
    } // getReffieldContentFromOid
   

    /**************************************************************************
     * Parse Section for parameters. <BR/>
     *
     * @param   node            Node could contain <CODE>INPARAMS</CODE> and
     *                          <CODE>OUTPARAMS</CODE>.
     * @param   env             The current environment.
     *
     * @return  A vector which contains two vectors. <BR/>
     *          The first contains all input parameters of the given node.
     *          (empty if no inputparameter is given). <BR/>
     *          The second contains all output parameters of the given node.
     *          (empty if no outputparameter is given)
     *
     */
    protected static Vector<Vector<? extends BaseObject>> parseParametersSection (
                                                                                  Node node,
                                                                                  Environment env)
    {
        Vector<Vector<? extends BaseObject>> inOutParams =
            new Vector<Vector<? extends BaseObject>> ();
        Vector<InputParamElement> inParams = new Vector<InputParamElement> ();
        Vector<OutputParamElement> outParams = new Vector<OutputParamElement> ();
        NodeList inOutParamNodeList = null;
        Node tmpNode = null;

        inOutParamNodeList = node.getChildNodes ();

        for (int i = 0; i < inOutParamNodeList.getLength (); i++)
                                        // parse all child nodes
        {
            tmpNode = inOutParamNodeList.item (i);

            if (tmpNode.getNodeType () == Node.ELEMENT_NODE)
                                        // is the node of type element ?
            {
                if (tmpNode.getNodeName ().equalsIgnoreCase (ActionConstants.ELEM_INPARAMS))
                                        // is the node the inparams node ?
                {
                    inParams = m2XMLFilter.parseInputParametersSection (tmpNode, env);
                } // if is the node the inparams node
                else if (tmpNode.getNodeName ().equalsIgnoreCase (ActionConstants.ELEM_OUTPARAMS))
                                        // is the node the outparams node ?
                {
                    outParams = m2XMLFilter.parseOutputParametersSection (tmpNode);
                } // else if  is the node the outparams node
                else                    // the node is neither inparams nor ouparams node
                {
                    // Sometimes here could a new parameter type be checked!!
                } // else the node is neither inparams nor ouparams node
            } // if is the node of type element
        } // for parse all child nodes

        inOutParams.addElement (inParams);
        inOutParams.addElement (outParams);

        return inOutParams;
    } // parseParameters


    /**************************************************************************
     * Parse Section for input parameters. <BR/>
     *
     * @param   paramsNode  Should only contain <CODE>PARAMETER</CODE>.
     * @param   env         The current environment.
     *
     * @return  A vector which contains all input parameters of the given node.
     *          (empty if no input parameter is given). <BR/>
     *
     */
    private static Vector<InputParamElement> parseInputParametersSection (
                                                   Node paramsNode, Environment env)
    {
        Vector<InputParamElement> inParams = new Vector<InputParamElement> ();

        NodeList paramNodeList = null;
        Node paramNode = null;

        paramNodeList = paramsNode.getChildNodes ();

        for (int i = 0; i < paramNodeList.getLength (); i++)
        {
            paramNode = paramNodeList.item (i);

            if (paramNode.getNodeType () == Node.ELEMENT_NODE)
                                        // is the node of type element ?
            {
                Element elem = (Element) paramNode;

                if (elem.getNodeName ().equalsIgnoreCase (ActionConstants.ELEM_PARAMETER))
                                        // is the node name PARAMETER ?
                {
                    inParams.addElement (new InputParamElement (m2XMLFilter
                        .getNameAttr (elem), m2XMLFilter.getNodeValue (elem, env)));
                } // if is the node name PARAMETER
            } // if is the node of type element
        } // for

        return inParams;
    } // parseInputParametersSection


    /**************************************************************************
     * Parse Section for output parameters. <BR/>
     *
     * @param   paramsNode  Should only contain <CODE>PARAMETER</CODE>.
     *
     * @return  A vector which contains all output parameters of the given node.
     *          (empty if no output parameter is given)
     *
     */
    private static Vector<OutputParamElement> parseOutputParametersSection (
                                                                     Node paramsNode)
    {
        Vector<OutputParamElement> outParams = new Vector<OutputParamElement> ();

        NodeList paramNodeList = null;
        Node paramNode = null;

        paramNodeList = paramsNode.getChildNodes ();

        for (int i = 0; i < paramNodeList.getLength (); i++)
        {
            paramNode = paramNodeList.item (i);

            if (paramNode.getNodeType () == Node.ELEMENT_NODE)
                                        // is the node of type element ?
            {
                if (paramNode.getNodeName ().equalsIgnoreCase (
                    ActionConstants.ELEM_PARAMETER))
                                        // is the node name PARAMETER ?
                {
                    outParams.addElement (new OutputParamElement (m2XMLFilter
                        .getNameAttr (paramNode), paramNode.getNodeValue ()));
                } // if is the node name PARAMETER
            } // if is the node of type element
        } // for

        return outParams;
    } // parseOutputParametersSection


    /**************************************************************************
     *
     * Parse Section for all ref types with sub tags (e.g. "FIELDREF", "VALUEDOMAIN", ...) for import. <BR/>
     *
     * @param   valueNode   From ref type value.
     * @param   contextType The context type for parsing.
     * @param   env         The current environment.
     *
     * @return  The found field references,
     *          <CODE>null</CODE> if there occurred an error.
     */
    protected static Vector<ReferencedObjectInfo> parseSubRefSection (
                                                                      Node valueNode,
                                                                      String contextType,
                                                                      Environment env)
    {
        Vector<ReferencedObjectInfo> valueSubTags = new Vector<ReferencedObjectInfo> ();
        NodeList valueNodeList = null;
        Node valueSubNode = null;
        NamedNodeMap valuesSubNodeAttributes = null;
        Node fieldsNode = null;
        NodeList fieldNodeList = null;
        String fieldName = null;
        String fieldToken = null;
        String methodName = "m2XMLFilter.parseSubRefSection";

        // find FIELDS TAG
        valueNodeList = valueNode.getChildNodes ();
        int valueSize = valueNodeList.getLength ();

        for (int i = 0; i < valueSize; i++)
        {
            fieldsNode = valueNodeList.item (i);

            if (fieldsNode.getNodeType () == Node.ELEMENT_NODE)
            {
                fieldNodeList = fieldsNode.getChildNodes ();
            } // if
        } // for i

        // check if Tag FIELDS was found
        if (fieldNodeList == null)
        {
            IOHelpers.showMessage (
                methodName +
                " Error: Tag FIELDS is missing in " + contextType + "-Value.",
                env);
            return null;
        } // else

        int actSize = (fieldNodeList != null) ? fieldNodeList.getLength () : 0;

        // now go through all the values
        for (int i = 0; i < actSize; i++)
        {
            valueSubNode = fieldNodeList.item (i);
            // check if this is an element node
            if (valueSubNode.getNodeType () == Node.ELEMENT_NODE)
            {

                // check if node = SYSFIELD or FIELD
                if (!valueSubNode.getNodeName ().equalsIgnoreCase (
                        DIConstants.ELEM_SYSFIELD) &&
                    !valueSubNode.getNodeName ().equalsIgnoreCase (
                        DIConstants.ELEM_FIELD))
                {
                    IOHelpers.showMessage (
                        methodName +
                        " Error: Wrong Tagname in FIELDS section " +
                        "of " + contextType + "-Value: [" +
                         valueSubNode.getNodeName () + "]",
                        env);
                } // if other Tag than SYSFIELD or FIELD is used

                valuesSubNodeAttributes =
                    valueSubNode.getAttributes ();

                // fieldName
                if (valuesSubNodeAttributes
                    .getNamedItem (DIConstants.ATTR_NAME) != null)
                {
                    fieldName = valuesSubNodeAttributes.getNamedItem (
                        DIConstants.ATTR_NAME).getNodeValue ();
                } // if

                // fieldToken
                if (valuesSubNodeAttributes
                    .getNamedItem (DIConstants.ATTR_TOKEN) != null)
                {
                    fieldToken = valuesSubNodeAttributes.getNamedItem (
                        DIConstants.ATTR_TOKEN).getNodeValue ();
                } // if
                else
                {
                    fieldToken = null;
                } // else

                // isSysField
                if (valueSubNode.getNodeName ().equalsIgnoreCase (
                        DIConstants.ELEM_SYSFIELD))
                {
                    valueSubTags.addElement (
                        new ReferencedObjectInfo (
                            ReferencedObjectInfo.TYPE_SYSTEM,
                            fieldName, fieldToken));
                } // if (isSysField)
                else    // not a system field
                {
                    valueSubTags.addElement (
                        new ReferencedObjectInfo (
                            ReferencedObjectInfo.TYPE_STANDARD, fieldName,
                            fieldToken));
                } // else not a system field
            } // if
        } // for i

        // return the result:
        return valueSubTags;
    } // parseSubRefSection


    /***************************************************************************
     * Parse Section for VALUE FIELD="REMINDER" for import. <BR/> Reminder
     * parameters:
     *
     * <PRE>
     * 0 ... display type for reminder
     * 1 ... days interval for reminder 1
     * 2 ... notification text for reminder 1
     * 3 ... recipient(s) for reminder 1
     * 4 ... possible recipients for reminder 1
     * 5 ... days interval for reminder 2
     * 6 ... notification text for reminder 2
     * 7 ... recipient(s) for reminder 2
     * 8 ... possible recipients for reminder 2
     * 9 ... days interval for escalation
     * 10 ... notification text for escalation
     * 11 ... recipient(s) for escalation
     * 12 ... possible recipients for escalation
     * </PRE>
     *
     * @param valuesAttributes Attributes of the REMINDER value.
     * @param valueDataElement The value data element with the default values
     *
     * @return  The found reminder parameters, <CODE>null</CODE> if there
     *          occurred an error.
     *          Return value must be of class <CODE>Serializable</CODE>
     *          because this is a common super class of
     *          <CODE>String</CODE> and <CODE>Integer</CODE>.
     */
    protected static Vector<Serializable> parseReminderAttributes (NamedNodeMap valuesAttributes, ValueDataElement valueDataElement)
    {
        // vector containing the reminder parameters:
        // Must be aof class <CODE>Serializable</CODE>
        // because this is a common super class of
        // <CODE>String</CODE> and <CODE>Integer</CODE>.
        Vector<Serializable> reminderParams = new Vector<Serializable> ();

        // is valueDataElement != null ?
        if (valueDataElement != null)
        {
            // get value for display type:
            m2XMLFilter.addAttributeValue (reminderParams, valuesAttributes,
                DIConstants.ATTR_DISPLAY, valueDataElement.p_displayType);

            // get values for reminder 1:
            m2XMLFilter.addAttributeValueInt (reminderParams, valuesAttributes,
                DIConstants.ATTR_REMIND1DAYS, valueDataElement.p_remind1Days);
            m2XMLFilter.addAttributeValue (reminderParams, valuesAttributes,
                DIConstants.ATTR_REMIND1TEXT, valueDataElement.p_remind1Text);
            m2XMLFilter.addAttributeValue (reminderParams, valuesAttributes,
                DIConstants.ATTR_REMIND1RECIP, valueDataElement.p_remind1Recip);
            m2XMLFilter.addAttributeValue (reminderParams, valuesAttributes,
                DIConstants.ATTR_REMIND1RECIPQUERY, valueDataElement.p_remind1RecipQuery);

            // get values for reminder 2:
            m2XMLFilter.addAttributeValueInt (reminderParams, valuesAttributes,
                DIConstants.ATTR_REMIND2DAYS, valueDataElement.p_remind2Days);
            m2XMLFilter.addAttributeValue (reminderParams, valuesAttributes,
                DIConstants.ATTR_REMIND2TEXT, valueDataElement.p_remind2Text);
            m2XMLFilter.addAttributeValue (reminderParams, valuesAttributes,
                DIConstants.ATTR_REMIND2RECIP, valueDataElement.p_remind2Recip);
            m2XMLFilter.addAttributeValue (reminderParams, valuesAttributes,
                DIConstants.ATTR_REMIND2RECIPQUERY, valueDataElement.p_remind2RecipQuery);

            // get values for escalation:
            m2XMLFilter.addAttributeValueInt (reminderParams, valuesAttributes,
                DIConstants.ATTR_ESCALATEDAYS, valueDataElement.p_escalateDays);
            m2XMLFilter.addAttributeValue (reminderParams, valuesAttributes,
                DIConstants.ATTR_ESCALATETEXT, valueDataElement.p_escalateText);
            m2XMLFilter.addAttributeValue (reminderParams, valuesAttributes,
                DIConstants.ATTR_ESCALATERECIP, valueDataElement.p_escalateRecip);
            m2XMLFilter.addAttributeValue (reminderParams, valuesAttributes,
                DIConstants.ATTR_ESCALATERECIPQUERY, valueDataElement.p_escalateRecipQuery);
        } // if
        else
        {
            // get value for display type:
            m2XMLFilter.addAttributeValue (reminderParams, valuesAttributes,
                DIConstants.ATTR_DISPLAY, null);

            // get values for reminder 1:
            m2XMLFilter.addAttributeValueInt (reminderParams, valuesAttributes,
                DIConstants.ATTR_REMIND1DAYS, null);
            m2XMLFilter.addAttributeValue (reminderParams, valuesAttributes,
                DIConstants.ATTR_REMIND1TEXT, null);
            m2XMLFilter.addAttributeValue (reminderParams, valuesAttributes,
                DIConstants.ATTR_REMIND1RECIP, null);
            m2XMLFilter.addAttributeValue (reminderParams, valuesAttributes,
                DIConstants.ATTR_REMIND1RECIPQUERY, null);

            // get values for reminder 2:
            m2XMLFilter.addAttributeValueInt (reminderParams, valuesAttributes,
                DIConstants.ATTR_REMIND2DAYS, null);
            m2XMLFilter.addAttributeValue (reminderParams, valuesAttributes,
                DIConstants.ATTR_REMIND2TEXT, null);
            m2XMLFilter.addAttributeValue (reminderParams, valuesAttributes,
                DIConstants.ATTR_REMIND2RECIP, null);
            m2XMLFilter.addAttributeValue (reminderParams, valuesAttributes,
                DIConstants.ATTR_REMIND2RECIPQUERY, null);

            // get values for escalation:
            m2XMLFilter.addAttributeValueInt (reminderParams, valuesAttributes,
                DIConstants.ATTR_ESCALATEDAYS, null);
            m2XMLFilter.addAttributeValue (reminderParams, valuesAttributes,
                DIConstants.ATTR_ESCALATETEXT, null);
            m2XMLFilter.addAttributeValue (reminderParams, valuesAttributes,
                DIConstants.ATTR_ESCALATERECIP, null);
            m2XMLFilter.addAttributeValue (reminderParams, valuesAttributes,
                DIConstants.ATTR_ESCALATERECIPQUERY, null);
        } // else

        // return the computed vector:
        return reminderParams;
    } // parseReminderAttributes


    /***************************************************************************
     * Parse Section for VALUE FIELD="FILE" for import. <BR/> File parameters:
     *
     * <PRE>
     * 0 ... ENCODING=NO|BASE64
     * 1 ... FILENAME
     * 2 ... CONTENT_TYPE
     * 3 ... EXTENSION
     * </PRE>
     *
     * @param   valuesAttributes  Attributes of the FILE value.
     * @param   valueDataElement  The value data element with the default values.
     *
     * @return  The found file parameters, <CODE>null</CODE> if there
     *          occurred an error.
     *          Return value must be of class <CODE>Serializable</CODE>
     *          because this is a common super class of
     *          <CODE>String</CODE> and <CODE>Integer</CODE>.
     */
    protected static Vector<Serializable> parseFileAttributes (NamedNodeMap valuesAttributes,
           ValueDataElement valueDataElement)
    {
        // vector containing the reminder parameters:
        // Must be aof class <CODE>Serializable</CODE>
        // because this is a common super class of
        // <CODE>String</CODE> and <CODE>Integer</CODE>.
        Vector<Serializable> fileParams = new Vector<Serializable> ();

        // is valueDataElement != null ?
        if (valueDataElement != null)
        {
            // get values
            m2XMLFilter.addAttributeValue (fileParams, valuesAttributes,
                DIConstants.ATTR_ENCODING, valueDataElement.p_encoding);
            m2XMLFilter.addAttributeValue (fileParams, valuesAttributes,
                DIConstants.ATTR_FILENAME, valueDataElement.p_filename);
            m2XMLFilter.addAttributeValue (fileParams, valuesAttributes,
                DIConstants.ATTR_CONTENT_TYPE, valueDataElement.p_contentType);
            m2XMLFilter.addAttributeValue (fileParams, valuesAttributes,
                DIConstants.ATTR_EXTENSION, valueDataElement.p_extension);
        } // if
        else
        {
            // get values
            m2XMLFilter.addAttributeValue (fileParams, valuesAttributes,
                DIConstants.ATTR_ENCODING, null);
            m2XMLFilter.addAttributeValue (fileParams, valuesAttributes,
                DIConstants.ATTR_FILENAME, null);
            m2XMLFilter.addAttributeValue (fileParams, valuesAttributes,
                DIConstants.ATTR_CONTENT_TYPE, null);
            m2XMLFilter.addAttributeValue (fileParams, valuesAttributes,
                DIConstants.ATTR_EXTENSION, null);
        } // else

        // return the computed vector:
        return fileParams;
    } // parseFileAttributes


    /**************************************************************************
     * Add the value of an attribute to a vector. <BR/>
     * If the value was not found <CODE>null</CODE> is added to the vector.
     *
     * @param   values          Vector to which the value shall be added.
     *                          Must be of class <CODE>Serializable</CODE>
     *                          because this is a common super class of
     *                          <CODE>String</CODE> and <CODE>Integer</CODE>.
     * @param   attributes      Attributes where to search.
     * @param   attrName        Name of the attribute.
     * @param   defaultValue    A default value which is set if the current
     *                          attribute's value is not set.
     */
    protected static void addAttributeValue (Vector<Serializable> values,
                                             NamedNodeMap attributes,
                                             String attrName,
                                             String defaultValue)
    {
        // get value of the field unit
        if (attributes.getNamedItem (attrName) != null)
        {
            // add the value to the vector:
            values.add (attributes.getNamedItem (attrName).getNodeValue ());
        } // if
        else
        {
            // add null value:
            values.add (defaultValue);
        } // else
    } // addAttributeValue


    /**************************************************************************
     * Add the value of an attribute to a vector. <BR/>
     * The value will be converted to Integer. <BR/>
     * If the value was not found <CODE>null</CODE> is added to the vector.
     *
     * @param   values          Vector to which the value shall be added.
     *                          Must be of class <CODE>Serializable</CODE>
     *                          because this is a common super class of
     *                          <CODE>String</CODE> and <CODE>Integer</CODE>.
     * @param   attributes      Attributes where to search.
     * @param   attrName        Name of the attribute.
     * @param   defaultValue    A default value which is set if the current
     *                          attribute's value is not set.
     */
    protected static void addAttributeValueInt (Vector<Serializable> values,
                                                NamedNodeMap attributes,
                                                String attrName,
                                                Integer defaultValue)
    {
        // get value of the field unit
        if (attributes.getNamedItem (attrName) != null)
        {
            try
            {
                // add the value to the vector:
                values.add (Integer.valueOf (attributes.getNamedItem (attrName)
                    .getNodeValue (), 10));
            } // try
            catch (NumberFormatException e)
            {
                // should not occur, display error message:
                IOHelpers.printError (
                    "Error during getting attribute value for \"" +
                    attrName + "\"",
                    e, true);
            } // catch
            catch (DOMException e)
            {
                IOHelpers.printError (
                    "Error during adding attribute value for \"" +
                    attrName + "\"",
                    e, true);
            } // catch
        } // if
        else
        {
            // add null value:
            values.add (defaultValue);
        } // else
    } // addAttributeValue


    /**************************************************************************
     * parses the RIGHTS section of the import xml document and
     * stores right entries found in the DataElement. <BR/>
     *
     * @param   dataElement The DataElement that holds the rights info.
     * @param   root        The rights node.
     * @param   env         The current environment.
     */
    protected static void parseRightsSection (DataElement dataElement,
                                              Node root, Environment env)
    {
        NodeList rightsNodeList;
        int rightsSize;
        Node rightNode;
        NamedNodeMap rightsAttributes;
        String rightName = "";
        String rightType = "";
        String rightProfile = "";
        Node nameNode;
        Node typeNode;
        Node profileNode;

        rightsNodeList = root.getChildNodes ();
        rightsSize = rightsNodeList.getLength ();
        // now go through all the values
        for (int i = 0; i < rightsSize; i++)
        {
            // reset the values
            rightName = "";
            rightType = "";
            rightProfile = "";
            // get value node from values nodelist
            rightNode = rightsNodeList.item (i);
            // check if this is an element node
            if (rightNode.getNodeType () == Node.ELEMENT_NODE)
            {
                // get the values from an VALUES element
                // get attributes
                rightsAttributes = rightNode.getAttributes ();
                // get name attribute
                nameNode = rightsAttributes.getNamedItem (DIConstants.ATTR_NAME);
                if (nameNode != null)
                {
                    rightName = nameNode.getNodeValue ();
                } // if
                else
                {
                    rightName = "";
                } // else
                // get type attribute
                typeNode = rightsAttributes.getNamedItem (DIConstants.ATTR_TYPE);
                if (typeNode != null)
                {
                    rightType = typeNode.getNodeValue ();
                } // if
                else
                {
                    rightType = "";
                } // else
                // get profile or alias attribuite
                // both are valid at the moment but alias should be the one used in the future
                profileNode = rightsAttributes.getNamedItem (DIConstants.ATTR_PROFILE);
                if (profileNode != null)
                {
                    rightProfile = profileNode.getNodeValue ();
                } // if (aliasNode != null)
                else    // no profile attribute available try the alias attribute
                {
                    // try to get the alias attribute
                    profileNode = rightsAttributes.getNamedItem (DIConstants.ATTR_ALIAS);
                    if (profileNode != null)
                    {
                        rightProfile = profileNode.getNodeValue ();
                    } // if
                    else
                    {
                        rightProfile = "";
                    } // else
                } // else no profile attribute available try the alias attribute
/*
//showDebug ("RIGHT: Name: " + rightName +
             " - Type: " + rightType +
             " - profile: " + rightProfile);
*/
                // store the value but only if there are all values available
                if (!(rightName.length () == 0 || rightType.length () == 0 ||
                       rightProfile.length () == 0))
                {
                    dataElement.addRight (rightName, rightType, rightProfile);
                } // if
            } // if (rightNode.getNodeType () == Node.ELEMENT_NODE)
        } // for (int i = 0; i < rightsSize; i++)
    } // parseRightsSection


    /**************************************************************************
     * parses the REFERENCES section of the import xml document and
     * stores reference entries found in the DataElement. <BR/>
     *
     * @param   dataElement The DataElement that holds the rights info.
     * @param   root        The rights node.
     * @param   env         The current environment.
     */
    protected static void parseReferencesSection (DataElement dataElement,
                                                  Node root, Environment env)
    {
        NodeList refNodeList;
        int refSize;
        Node refNode;
        NamedNodeMap refAttributes;
        NamedNodeMap attributes;
        String containerType        = "";
        String actContainerId       = "";
        String containerIdDomain    = "";
        String containerTabName     = "";
        NodeList nodelist;
        Node node;
        Node attributeNode;
        Text text;
        int actSize;

        refNodeList = root.getChildNodes ();
        refSize = refNodeList.getLength ();
        // now go through all the values
        for (int i = 0; i < refSize; i++)
        {
            // get value node from values nodelist
            refNode = refNodeList.item (i);
            // check if this is an element node
            if (refNode.getNodeType () == Node.ELEMENT_NODE)
            {
                // init the values
                containerType       = "";
                actContainerId      = "";
                containerIdDomain   = "";
                containerTabName    = "";
                // get the attributes
                refAttributes = refNode.getAttributes ();
                // check if we got any attributes
                if (refAttributes != null)
                {
                    // get the container tab name
                    attributeNode =  refAttributes.getNamedItem (DIConstants.ATTR_TABNAME);
                    if (attributeNode != null)
                    {
                        containerTabName = attributeNode.getNodeValue ();
                    } // if
                    // get the container type
                    attributeNode = refAttributes.getNamedItem (DIConstants.ATTR_TYPE);
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
                        if (refNode.hasChildNodes ())
                        {
                            // parse through the OBJECT sections
                            nodelist = refNode.getChildNodes ();
                            // get amount of objects
                            actSize = nodelist.getLength ();
                            // find the <OBJECTS> element
                            for (int ii = 0; ii < actSize; ii++)
                            {
                                // get the node
                                node = nodelist.item (ii);
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
                                        actContainerId = text.getNodeValue ();
                                    } // if
                                } // if (systemNode.getNodeName ().equalsIgnoreCase (DIConstants.ELEM_ID))
                            } // for (int ii = 0; ii < size; ii++)
                        } // if (containerNode.hasChildNodes ())
                    } // if (containerType.equalsIgnoreCase (DIConstants.CONTAINER_EXTKEY))
                    else    // container is not an external key
                    {
                        // read the value for custom operation
                        text = (Text) refNode.getFirstChild ();
                        // text node found?
                        if (text != null)
                        {
                            actContainerId = text.getNodeValue ();
                        } // if
                    } // else container is not an external key
/*
//showDebug ("REFERENCE found " +
           " - containerType: " + containerType +
           " - containerId: " + containerId +
           " - containerIdDomain: " + containerIdDomain +
           " - containerTabName: " + containerTabName);
*/
                    // store the value but only if there are all values available
                    if (containerType.length () > 0)
                    {
                        dataElement.addReference (containerType,
                                                  actContainerId,
                                                  containerIdDomain,
                                                  containerTabName);
                    } // if
                } // if (refAttributes != null)
            } // if (refNode.getNodeType () == Node.ELEMENT_NODE)
        } // for (int i = 0; i < referencesSize; i++)
    } // parseReferencesSection


    /**************************************************************************
     * parses the ATTACHMENTS section of the import xml document and
     * stores xml attachments in the DataElement. <BR/>
     *
     * @param   dataElement The DataElement that holds the rights info.
     * @param   root        The attachments node.
     * @param   env         The current environment.
     */
    protected static void parseAttachmentsSection (DataElement dataElement,
                                                   Node root, Environment env)
    {
        NodeList nodeList = root.getChildNodes ();
        int actSize = nodeList.getLength ();

        // now go through all the nodes:
        for (int i = 0; i < actSize; i++)
        {
            Node node = nodeList.item (i);

            // check if this is an XMLDATA element node
            if (node.getNodeType () == Node.ELEMENT_NODE &&
                node.getNodeName ().equals (DIConstants.ELEM_XMLDATA))
            {
                // get the first element node from the child list
                node = node.getFirstChild ();
                while (node != null && node.getNodeType () != Node.ELEMENT_NODE)
                {
                    node = node.getNextSibling ();
                } // while (node != null && node.getNodeType () != Node.ELEMENT_NODE)
                // the XMLDATA node must contain a element node
                if (node != null)
                {
/*
                    Document nodeDoc = node.getOwnerDocument ();
*/

                    // any attachment is stored as a DOM document:
                    Document actDoc = null;

                    try
                    {
                        // create the document:
                        actDoc = XMLWriter.createDocument ();
/*
                        doc = XMLWriter.createDocument
                            (nodeDoc.getNamespaceURI (), nodeDoc.getNodeName (),
                             nodeDoc.getDoctype ());
*/

                        Node data = actDoc.importNode (node, true);
                        actDoc.appendChild (data);
                        // add the document to the attachment list
                        dataElement.attachmentList.addElement (actDoc);
                    } // try
                    catch (XMLWriterException e)
                    {
                        IOHelpers.showMessage (e, env, true);
                    } // catch
                } // if (node.hasChildNodes ())
            } // if (node.getNodeName () == DIConstants.ELEM_XMLDATA)
        } // for (int i = 0; i < size; i++)
    } // parseAttachmentsSection


    /**************************************************************************
     * parses the CONTENT section of the import xml document and
     * stores the informations in the DataElement. <BR/>
     * This section appares only in template files and holds
     * the parameter for container templates.
     *
     * @param   dataElement         The DataElement where to store the parameters.
     * @param   root                The CONTENT node.
     * @param   isTemplateImport    Flag indicating if a template import is
     *                              performed. For template imports also the
     *                              template attributes are  regarded.
     * @param   env                 The current environment.
     */
    protected static void parseContentSection (DataElement dataElement,
                                               Node root,
                                               boolean isTemplateImport,
                                               Environment env)
    {
        // this section is only read for template files.
        if (!isTemplateImport)
        {
            return;
        } // if

        NodeList nodeList = root.getChildNodes ();
        int actSize = nodeList.getLength ();

        // now go through all the nodes
        for (int i = 0; i < actSize; i++)
        {
            Node node = nodeList.item (i);
            // check if this is an MAYCONTAIN element node
            if (node.getNodeType () == Node.ELEMENT_NODE)
            {
                if (node.getNodeName ().equals (DIConstants.ELEM_MAYCONTAIN))
                {
                    node.normalize ();
                    // get the content of the node
                    Text text = (Text) node.getFirstChild ();
                    if (text != null)
                    {
                        dataElement.p_mayContain = text.getNodeValue ();
                    } // if
                } // if (node.getNodeName () == DIConstants.ELEM_MAYCONTAIN)
                // any EXTENSION set?
                else if (node.getNodeName ().equals (DIConstants.ELEM_EXTENSION))
                {
                    NamedNodeMap attributes = node.getAttributes ();
                    if (attributes != null)
                    {
                        // get the QUERYNAME attribute
                        Node attr = attributes.getNamedItem (DIConstants.ATTR_QUERYNAME);
                        if (attr != null)
                        {
                            dataElement.p_extensionQueryName =
                                attr.getNodeValue ();
                        } // if (attr != null)
                    } // if (attributes != null)
                } // else if (node.getNodeName ().equals (DIConstants.ELEM_EXTENSION))
                else if (node.getNodeName ().equals (DIConstants.ELEM_COLUMNS))
                {
                    m2XMLFilter.parseColumnsSection (dataElement, node, isTemplateImport, env);
                } // else if (node.getNodeName ().equals (DIConstants.ELEM_COLUMNS))
            } // if (node.getNodeType () == Node.ELEMENT_NODE)
        } // for (int i = 0; i < size; i++)
    } // parseContentSection


    /**************************************************************************
     * parses the COLUMNS section of the import xml document and
     * stores the informations in the DataElement. <BR/>
     * This section appares only in template files and holds
     * the parameter for container templates.
     *
     * @param   dataElement         The DataElement where to store the parameters.
     * @param   root                The COLUMNS node.
     * @param   isTemplateImport    Flag indicating if a template import is
     *                              performed. For template imports also the
     *                              template attributes are  regarded.
     * @param   env                 The current environment.
     */
    protected static void parseColumnsSection (DataElement dataElement,
                                               Node root,
                                               boolean isTemplateImport,
                                               Environment env)
    {
        NamedNodeMap attributes;
        Node attr;
        Node name;
        Node token;
        String columnName;
        String columnToken;
        int columnType;

        // this section is only read for template files.
        if (!isTemplateImport)
        {
            return;
        } // if (!this.isTemplateImport)

        // try to read the NAMETOKEN attribute
        attributes = root.getAttributes ();
        // check if we got any attributes
        if (attributes != null)
        {
            // get the nametoken attribute
            attr =  attributes.getNamedItem (DIConstants.ATTR_NAMETOKEN);
            if (attr != null)
            {
                dataElement.p_nameToken = attr.getNodeValue ();
            } // if (attr != null)
        } // if (attributes != null)

        NodeList nodeList = root.getChildNodes ();
        int actSize = nodeList.getLength ();

        dataElement.p_headerFields = new Vector<ReferencedObjectInfo> ();

        // now go through all the column nodes
        for (int i = 0; i < actSize; i++)
        {
            Node node = nodeList.item (i);
            // check if this is an element node
            if (node.getNodeType () == Node.ELEMENT_NODE)
            {
                // check if we have a SYSCOLUMN
                if (node.getNodeName ().equals (DIConstants.ELEM_SYSCOLUMN))
                {
                    columnType = ReferencedObjectInfo.TYPE_SYSTEM;
                } // if (node.getNodeName ().equals (DIConstants.ELEM_SYSCOLUMN))
                // check if we have a EXTCOLUMN
                else if (node.getNodeName ().equals (DIConstants.ELEM_EXTCOLUMN))
                {
                    columnType = ReferencedObjectInfo.TYPE_EXTENDED;
                } // else if (node.getNodeName ().equals (DIConstants.ELEM_EXTCOLUMN))
                else if (node.getNodeName ().equals (DIConstants.ELEM_COLUMN))
                {
                    columnType = ReferencedObjectInfo.TYPE_STANDARD;
                } // else if (node.getNodeName ().equals (DIConstants.ELEM_COLUMN))
                else // unknown column type
                {
                    columnType = ReferencedObjectInfo.TYPE_UNKNOWN;
                } // else unknown column type

                // did we get a valid column type?
                // any unkown type will be ignored
                if (columnType != ReferencedObjectInfo.TYPE_UNKNOWN)
                {
                    // get the attributes of the node
                    columnName = null;
                    columnToken = null;
                    attributes = node.getAttributes ();
                    // the NAME attribute must have been set
                    name = attributes.getNamedItem (DIConstants.ATTR_NAME);
                    if (name != null)
                    {
                        columnName = name.getNodeValue ();
                    } // if (name != null)
                    // get the token is possible
                    token = attributes.getNamedItem (DIConstants.ATTR_TOKEN);
                    if (token != null)
                    {
                        columnToken = token.getNodeValue ();
                    } // if (token != null)

                    // the columnName must be set else it will be ignored
                    if (columnName != null && columnName.length () > 0)
                    {
                        // BT IBS-415: The logic has been moved to
                        // DataElement.initMultilangInfo () for MLI handling
//                        // check if token is empty
//                        if (columnToken == null || columnToken.length () == 0)
//                        {
//                            // the SYSCOLUMNS get their name from the
//                            // BOTokens that have multilanguage support
//                            if (columnType == ReferencedObjectInfo.TYPE_SYSTEM)
//                            {
//                                columnToken = DIHelpers.getSysFieldToken (columnName);
//                            } // if (columnType == FieldRefInfo.TYPE_SYSTEM)
//                            else    // not a system type
//                            {
//                                // for non syscolumns set the token = name
//                                columnToken = columnName;
//                            } // else not a system type
//                        } // if (columnToken == null || columnToken.length () == 0)
                        // add it as header field
                        dataElement.p_headerFields.addElement (
                                new ReferencedObjectInfo (columnType, columnName, columnToken));
                    } // if (columnName != null && columnName.length () > 0)
                } // if (columnType != FieldRefInfo.TYPE_UNKNOWN)
            } // if (node.getNodeType () == Node.ELEMENT_NODE)
        } // for (int i = 0; i < size; i++)
    } // parseColumnsSection


    /**************************************************************************
     * parses the LOGIC section of the xml document and
     * stores xml attachments in the DataElement. <BR/>
     *
     * @param   dataElement         The DataElement that holds the rights info.
     * @param   root                The attachments node.
     * @param   isTemplateImport    Defines if a templateImport is performed.
     * @param   env                 The current environment.
     */
    protected static void parseLogicSection (DataElement dataElement, Node root, boolean isTemplateImport, Environment env)
    {
        // this section is only read for template files.
        if (isTemplateImport)
        {
            try
            {
                NodeList nodeList = root.getChildNodes ();
                int actSize = nodeList.getLength ();
                // now go through all the nodes
                for (int i = 0; i < actSize; i++)
                {
                    Node node = nodeList.item (i);
                    // check if this is an ACTIONS element node
                    if (node.getNodeType () == Node.ELEMENT_NODE)
                    {
                        dataElement.p_initActions = new Actions ();
                        dataElement.p_initActions.loadActions (node, env);
                    } // if (node.getNodeType () == Node.ELEMENT_NODE)
                } // for (int i = 0; i < size; i++)
            } // try
            catch (ActionException e)
            {
                IOHelpers.showMessage (e.toString (), env);
            } // catch
        } // if (this.isTemplateImport)
    } // parseLogicSection


    /**************************************************************************
     * Tests if there are more objects available from this import file. <BR/>
     *
     * @return  <CODE>true</CODE> if there are more objects available,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean hasMoreObjects ()
    {
        if (this.objectsNodeList != null)
        {
            if (this.objectsIndex < this.objectsNodeListLength)
            {
                return true;
            } // if

            // no more objects:
            return false;
        } // if (this.objectsNodeList != null)

        // no objects available:
        return false;
    } // hasMoreObjects


    /**************************************************************************
     * Returns the next DataElement from this importFile. <BR/>
     *
     * @return an DataElement Object that holds the data of an object in the
     *          importFile
     */
    public DataElement nextObject ()
    {
        DataElementList dataElementList;

        // check if we already have any DataElements
        if (this.actualDataElements == null)
        {
            dataElementList = this.nextObjectCollection ();
            if (dataElementList != null)
            {
                // set the DataElements
                this.actualDataElements = dataElementList.dataElements.elements ();
            } // if
            else
            {
                return null;
            } // else
        } // if (actualDataElements == null)

        // check if there are still DataElements in this collection
        if (this.actualDataElements.hasMoreElements ())
        {
            return this.actualDataElements.nextElement ();
        } // if (this.actualDataElements.hasMoreElements ())

        // end of OBJECTS section reached - read next:
        // increase the objects index
        // this is already done in the nextObjectCollection method
//            this.objectsIndex ++;

        // read next collection
        dataElementList = this.nextObjectCollection ();
        // check if we got DataElements
        if (dataElementList != null)
        {
            // check if the DataElementList has DataElements set
            if (dataElementList.dataElements != null)
            {
                this.actualDataElements = dataElementList.dataElements.elements ();
                // check if the DataElementList contains elements
                if (this.actualDataElements.hasMoreElements ())
                {
                    return this.actualDataElements.nextElement ();
                } // if (actualDataElements.hasMoreElements ())

                return null;
            } // if (dataElementList.dataElements != null)

            // dataElementList was empty:
            return null;
        } // if (dataElementList != null)

        // no dataElements found:
        return null;
    } // nextObject


    /**************************************************************************
     * Returns the next DataElementList from this importFile. <BR/>
     *
     * @return an DataElementList object that holds a collection of objects
     *          the importScript can be applied on
     */
    public DataElementList nextObjectCollection ()
    {
        Node node;

        if (this.objectsNodeList != null)
        {
            while (this.objectsIndex < this.objectsNodeListLength)
            {
                // get the node and increase the objects index
                node = this.objectsNodeList.item (this.objectsIndex++);
                // check if node is an element
                if (node.getNodeType () == Node.ELEMENT_NODE)
                {
                    return m2XMLFilter.parseObjects (
                        node, this.isTemplateImport, this.env);
                } //if (node.getNodeType () == Node.ELEMENT_NODE)
            } // while (objectsIndex++ < objectsNodeListSize)
            return null;
        } // // this.objectsNodeList != null)

        return null;
    } // nextObjectCollection


    //
    // EXPORT FILTER METHODS
    //
    /**************************************************************************
     * Creates an XML document for export of m2 objects.
     */
    private void initExport ()
    {
        // create a new DOM root:
        Document actDoc = this.createDocument ();

        // add the comment
        // <!-- ibs export file -->
//        doc.appendChild (doc.createComment (" ibs export file "));
        // create the root <IMPORT>
        Element root = actDoc.createElement (DIConstants.ELEM_IMPORT);
        // add the <IMPORT VERSION="1.0"> attribute
        root.setAttribute (DIConstants.ATTR_VERSION, DIConstants.DOCUMENT_VERSION);
        // add the head to the xml document
        actDoc.appendChild (root);

        // if there is a Metadata Information ... write it into the root
        if (this.getMetadataElement () != null)
        {
            // add the metadata to the root
            root.appendChild (this.createMetadataSection (this
                .getMetadataElement ()));
        } // if (elementMetadata != null)

        // create the first <OBJECTS> node
        Element objects = actDoc.createElement (DIConstants.ELEM_OBJECTS);
        root.appendChild (objects);
        // now set this point as insertion point:
        this.p_insertionPoint = objects;
    } // initExport


    /**************************************************************************
     * Create a XML element for a m2 object, which may be included within a
     * xml dom tree. <BR/>
     *
     * @param dataElement   The data element from which to create the dom
     *                      element.
     *
     * @return  The generated Element.
     */
    private Element createObjectElement (DataElement dataElement)
    {
        Element object = null;

        // create the <OBJECT> element
        if (dataElement.p_isTabObject)
        {
            object = this.doc.createElement (DIConstants.ELEM_TABOBJECT);
            // add the attribute TABCODE
            object.setAttribute ("TABCODE", dataElement.p_tabCode);
        } // if the object is a tab
        else
        {
            object = this.doc.createElement (DIConstants.ELEM_OBJECT);
        } // else if the object is a tab

        // if the data element contains a valid type code
        // we set the TYPECODE attribute otherwise we set the TYPE attribute.
        if (dataElement.p_typeCode != null &&
            !dataElement.p_typeCode.isEmpty ())
        {
            object.setAttribute (DIConstants.ATTR_TYPECODE, dataElement.p_typeCode);
        } // if (dataElement.p_typeCode.length () > 0)
        else // if (dataElement.p_typeCode.length () > 0)
        {
            // check if a typename has been set
            if (dataElement.typename != null)
            {
                object.setAttribute (DIConstants.ATTR_TYPE, dataElement.typename);
            } // if
        } // else if (dataElement.p_typeCode.length () > 0)

        // add the type name as comment
        // BB: this is deactivated
        //CommentImpl comment = (CommentImpl) this.doc.createComment (" " + dataElement.typename + " ");
        //object.appendChild (comment);

        // create the <SYSTEM> section add it to he <OBJECT> node
        object.appendChild (this.createSystemSection (dataElement));
        // create the <VALUES> section and add it to he <OBJECT> node
        object.appendChild (this.createValuesSection (dataElement));

        // check if we have a query object and queries shall be executed:
        if (this.isExportQueryResults () &&
            dataElement.oid != null &&
            (dataElement.oid.tVersionId ==
                this.getTypeCache ().getTVersionId (TypeConstants.TC_QueryCreator) ||
             dataElement.oid.tVersionId ==
                this.getTypeCache ().getTVersionId (TypeConstants.TC_DBQueryCreator)))
        {
            // create the <RESULTS> section and add it to he <OBJECT> node
            object.appendChild (this.createResultsSection (dataElement));
        } // if

        // create the TABS section
        // Note that only the Tab obects will be exported but not the content of
        // a tab. Only the system section of the tab will be exported because it
        // is just used to define the tabs. The data of the tabs is stored
        // in the tabs objects that have been created when creating the XML viewer
        // object
        if (dataElement.tabElementList != null)
        {
            Element tabs = this.doc.createElement (DIConstants.ELEM_TABS);
            object.appendChild (tabs);
            // get the tab elements:
            for (Iterator<DataElement> iter =
                    dataElement.tabElementList.dataElements.iterator ();
                 iter.hasNext ();)
            {
                DataElement tabDataElement = iter.next ();
                Element tab = this.doc.createElement (DIConstants.ELEM_TABOBJECT);
                tabs.appendChild (tab);
                // if the tabDataElement contains a valid type code
                // we set the TYPECODE attribute otherwise we set the TYPE attribute.
                if (tabDataElement.p_typeCode != null &&
                    !tabDataElement.p_typeCode.isEmpty ())
                {
                    tab.setAttribute (DIConstants.ATTR_TYPECODE, tabDataElement.p_typeCode);
                } // if
                else                    // add the typename as attribute
                {
                    if (tabDataElement.typename != null)
                    {
                        tab.setAttribute (DIConstants.ATTR_TYPE, tabDataElement.typename);
                    } // if
                } // else add the typename as attribute

                // create the <SYSTEM> section add it to he <TABOBJECT> node
                tab.appendChild (this.createSystemSection (dataElement));
                // if all contentobjects of a tab should be exported, too...
            } // for iter
        } // if (dataElement.tabElementList != null)

        // check if right should be exported
        // BB HINT: this is still a missing feature
        if (this.isExportRights)
        {
            // create the <RIGHTS> section and add it to the <OBJECT> node
            object.appendChild (this.createRightsSection (dataElement));
        } // if (this.isExportRights)

        // are there any XML attachments defined?
        if ((dataElement.attachmentList != null) && (!dataElement.attachmentList.isEmpty ()))
        {
            // create the <ATTACHMENTS> section and add it to the <OBEJCT> node
            object.appendChild (this.createAttachmentSection (dataElement));
        } // if ((dataElement.attachmentList != null) && (!dataElement.attachmentList.isEmpty ()))

        // return the object we constructed
        return object;
    } // createObjectElement


    /**************************************************************************
     * Create the &lt;SYSTEM> section of an export dokument from the data of an
     * dataElement. <BR/>
     *
     * @param dataElement  the dataElement that stores the data of the values
     *
     * @return the created &lt;SYSTEM> node
     */
    private Node createSystemSection (DataElement dataElement)
    {
        Element system;

        // create the <SYSTEM> element
        system = this.doc.createElement (DIConstants.ELEM_SYSTEM);
        // if the display mode for the system section is not default mode (on top)
        // we set the DISPLAY attribute for the SYSTEM node.
        switch (dataElement.getSystemSectionDisplayMode ())
        {
            case DataElement.DSP_MODE_TOP:
                // default value
                break;
            case DataElement.DSP_MODE_HIDE:
                system.setAttribute (DIConstants.ATTR_DISPLAY, DIConstants.DISPLAY_NO);
                break;
            case DataElement.DSP_MODE_BOTTOM:
                system.setAttribute (DIConstants.ATTR_DISPLAY, DIConstants.DISPLAY_BOTTOM);
                break;
            default:
//                showDebug ("WARNING! unknown display mode.");
        } // switch (dataElement.getSystemSectionDisplayMode ())

        // skip the ID-Tag for tab objects
        // tab object cannot have an EXTKEY
        if (!dataElement.p_isTabObject)
        {
            //<OID></OID>
            Element elemOid = this.doc.createElement (DIConstants.ELEM_OID);
            if (dataElement.oid != null)
            {
                elemOid.appendChild (this.doc.createTextNode (dataElement.oid.toString ()));
            } // if
            system.appendChild (elemOid);

            //<ID DOMAIN=""></ID>
            Element id = this.doc.createElement (DIConstants.ELEM_ID);

            // add the <ID DOMAIN=""> attribute
            if (dataElement.idDomain != null)
            {
                id.setAttribute (DIConstants.ATTR_DOMAIN, dataElement.idDomain);
            } // if
            // add the <ID> value
            if (dataElement.id != null)
            {
                id.appendChild (this.doc.createTextNode (dataElement.id));
            } // if
            system.appendChild (id);
        } // if is not a tab object
        //<NAME>
        Element actName = this.doc.createElement (DIConstants.ELEM_NAME);
        if (dataElement.name != null)
        {
            actName.appendChild (this.doc.createTextNode (dataElement.name));
        } // if
        system.appendChild (actName);
        //<DESCRIPTION>
        // Escape the '\n' and remove all '\r' in the description string.
        String desc = m2XMLFilter.escapeLineSeparators (dataElement.description);
        Element actDescription = this.doc.createElement (DIConstants.ELEM_DESCRIPTION);
        if (desc != null)
        {
            actDescription.appendChild (this.doc.createTextNode (desc));
        } // if
        system.appendChild (actDescription);
        //<VALIDUNTIL>
        Element validuntil = this.doc.createElement (DIConstants.ELEM_VALIDUNTIL);
        if (dataElement.validUntil != null)
        {
            validuntil.appendChild (this.doc.createTextNode (dataElement.validUntil));
        } // if
        system.appendChild (validuntil);
        // <SHOWINNEWS>
        Element elemShowInNews = this.doc.createElement (DIConstants.ELEM_SHOWINNEWS);
        if (dataElement.showInNews != null)
        {
            elemShowInNews.appendChild (this.doc.createTextNode ("" + dataElement.showInNews));
        } // if
        system.appendChild (elemShowInNews);

        // return the <SYSTEM> node
        return system;
    } // createSystemSection


    /**************************************************************************
     * Create the &lt;METADATA> section of an export dokument from the data of an
     * dataElement. <BR/>
     *
     * @param   dataElement The dataElement that stores the data of the values.
     *
     * @return  The created &lt;METADATA> node.
     */
    private Node createMetadataSection (DataElement dataElement)
    {
        Node values = null;
        Element metadata = null;

        if (dataElement != null)
        {
            // create the metadata tag
            metadata = this.doc.createElement (DIConstants.ELEM_META);
            values = this.createValuesSection (dataElement);
            metadata.appendChild (values);
        } // if (dataElement != null)

        return metadata;
    } // createMetadataSection


    /**************************************************************************
     * Create the &lt;VALUES> section of an export dokument from the data of an
     * dataElement. <BR/>
     *
     * @param dataElement  the dataElement that stores the data of the values
     *
     * @return the created &lt;VALUES> node
     */
    private Node createValuesSection (DataElement dataElement)
    {
        Element values = null;

        // is query required for value type?
        boolean requireQuery = true;

        // create the values tag
        values = this.doc.createElement (DIConstants.ELEM_VALUES);
        // include values stored in the DataElement values vector:
        // loop through the values:
        for (Iterator<ValueDataElement> iter = dataElement.values.iterator (); iter.hasNext ();)
        {
            // get a value data element instance from the values vector:
            ValueDataElement vie = iter.next ();
//showDebug ("\tVALUE: FIELD=\"" + vie.field + "\" <" + vie.value + ">");
            // create the <VALUE> tag
            Element value = this.doc.createElement (DIConstants.ELEM_VALUE);
            // only add when value not null
            if (vie.field != null)
            {
                value.setAttribute (DIConstants.ATTR_FIELD, vie.field);
            } // if
            // only add when ml key not null
            if (vie.mlKey != null)
            {
                value.setAttribute (DIConstants.ATTR_MLKEY, vie.mlKey);
            } // if
            // only add when value not null
            if (vie.type != null)
            {
                value.setAttribute (DIConstants.ATTR_TYPE, vie.type);
            } // if
            // only add when value not null
            if (vie.mandatory != null)
            {
                value.setAttribute (DIConstants.ATTR_MANDATORY, vie.mandatory);
            } // if
            // only add when value not null
            if (vie.p_readonly != null)
            {
                value.setAttribute (DIConstants.ATTR_READONLY, vie.p_readonly);
            } // if
            // only add when value not null
            if (vie.p_valueUnit != null)
            {
                value.setAttribute (DIConstants.ATTR_UNIT, vie.p_valueUnit);
            } // if

            // if this is an OBJECTREF value or a SELECTION
            // there have to be 3 more attributes
            if (vie.type.equalsIgnoreCase (DIConstants.VTYPE_OBJECTREF) ||
                vie.type.equalsIgnoreCase (DIConstants.VTYPE_SELECTION))
            {
                // starting with release 2.2 we use the new attribute TYPECODEFILTER
                // witch holds a list of type codes.
                // the old attribute TYPEFILTER is no longer supported.
                if (vie.typeFilter != null)
                {
                    value.setAttribute (DIConstants.ATTR_TYPECODEFILTER, vie.typeFilter);
                } // if
                if (vie.searchRecursive != null)
                {
                    value.setAttribute (DIConstants.ATTR_SEARCHRECURSIVE, vie.searchRecursive);
                } // if
                if (vie.searchRoot != null)
                {
                    value.setAttribute (DIConstants.ATTR_SEARCHROOT, vie.searchRoot);
                } // if
                if (vie.searchRootIdDomain != null)
                {
                    value.setAttribute (DIConstants.ATTR_SEARCHROOTIDDOMAIN, vie.searchRootIdDomain);
                } // if
                if (vie.searchRootId != null)
                {
                    value.setAttribute (DIConstants.ATTR_SEARCHROOTID, vie.searchRootId);
                } // if
            } // if (vie.type.equalsIgnoreCase (DIConstants.VTYPE_OBJECTREF)) || ...
            // is it a value of type QUERY?
            else if (vie.type.equalsIgnoreCase (DIConstants.VTYPE_QUERY))
            {
                // check if query name has been set
                if (vie.queryName != null)
                {
                    value.setAttribute (DIConstants.ATTR_QUERYNAME, vie.queryName);
                    // check if the result of a query should be added to the
                    // export structure.
                    // BB TODO: Note that exporting the query result is not
                    // always neccessary and can cause bad performance when
                    // exporting objects. Note that we use cached query results
                    if (this.p_isExportQueryResults)
                    {
                        // get the cached query result node of the query field
                        Node queryResultNode = vie.getQueryResultNode ();
                        // check if we got a result?
                        if (queryResultNode == null)
                        {
                            // add RESULTROW and RESULTELMENT Tags from Query
                            QueryHelpers.addQueryData (this, value,
                                dataElement.oid, vie.field, vie.queryName,
                                DIHelpers.getTemplateSubTags (this
                                    .getDocumentTemplate (), vie.field), env);
                            // cache the query node
                            vie.setQueryResultNode (value);
                        } // if (vie.p_queryNode == null)
                        else    // include the cached query node
                        {
                            value = (Element) this.doc.importNode (queryResultNode, true);
                        } // else include the cached query node
                    } // if (this.p_isExportQueryResults)
                } // if (vie.queryName != null)
                if (vie.refresh != null)
                {
                    value.setAttribute (DIConstants.ATTR_REFRESH, vie.refresh);
                } // if
            } // if (vie.type.equalsIgnoreCase (DIConstants.VTYPE_QUERY))
            // is this a value of type QUERYSELECTIONBOX?
            else if (vie.type.startsWith (DIConstants.VTYPE_QUERYSELECTIONBOX))
            {
                if (vie.queryName != null)
                {
                    value.setAttribute (DIConstants.ATTR_QUERYNAME, vie.queryName);
                } // if
                if (vie.options != null)
                {
                    value.setAttribute (DIConstants.ATTR_OPTIONS, vie.options);
                } // if
                if (vie.emptyOption != null)
                {
                    value.setAttribute (DIConstants.ATTR_EMPTYOPTION, vie.emptyOption);
                } // if
                if (vie.refresh != null)
                {
                    value.setAttribute (DIConstants.ATTR_REFRESH, vie.refresh);
                } // if
                if (vie.viewType != null)
                {
                    value.setAttribute (DIConstants.ATTR_VIEWTYPE, vie.viewType);
                } // if
                if (vie.noColumns != null)
                {
                    value.setAttribute (DIConstants.ATTR_NO_COLUMNS, vie.noColumns);
                } // if
                if (vie.type.equals (DIConstants.VTYPE_QUERYSELECTIONBOX) &&
                    vie.multiSelection != null)
                {
                    value.setAttribute (DIConstants.ATTR_MULTISELECTION, vie.multiSelection);
                } // if
            } // if (vie.type.equalsIgnoreCase (DIConstants.VTYPE_QUERYSELECTIONBOX))
            // is this a value of type SELECTIONBOX
            else if (vie.type.startsWith (DIConstants.VTYPE_SELECTIONBOX))
            {
                if (vie.options != null)
                {
                    value.setAttribute (DIConstants.ATTR_OPTIONS, vie.options);
                } // if
                if (vie.viewType != null)
                {
                    value.setAttribute (DIConstants.ATTR_VIEWTYPE, vie.viewType);
                } // if
                if (vie.noColumns != null)
                {
                    value.setAttribute (DIConstants.ATTR_NO_COLUMNS, vie.noColumns);
                } // if
                if (vie.type.equals (DIConstants.VTYPE_SELECTIONBOX) &&
                    vie.multiSelection != null)
                {
                    value.setAttribute (DIConstants.ATTR_MULTISELECTION, vie.multiSelection);
                } // if
            } // if (vie.type.equalsIgnoreCase (DIConstants.VTYPE_SELECTIONBOX))
            // set the domain attribute for EXTKEY values
            else if (vie.type.startsWith (DIConstants.VTYPE_EXTKEY))
            {
                value.setAttribute (DIConstants.ATTR_DOMAIN, vie.p_domain);
            } // if EXTKEY value
            // set the SIZE attribute:
            else if (vie.type.startsWith (DIConstants.VTYPE_FILE) ||
                vie.type.startsWith (DIConstants.VTYPE_IMAGE))
            {
                value.setAttribute (DIConstants.ATTR_SIZE, "" + vie.p_size);
            } // if FILE or IMAGE value
            // set the REMINDER attributes:
            else if (vie.type.startsWith (DIConstants.VTYPE_REMINDER))
            {
                // display type:
                value.setAttribute (DIConstants.ATTR_DISPLAY, "" +
                    vie.p_displayType);

                // attributes for reminder 1:
                value.setAttribute (DIConstants.ATTR_REMIND1DAYS, "" +
                    vie.p_remind1Days);
                value.setAttribute (DIConstants.ATTR_REMIND1TEXT, "" +
                    vie.p_remind1Text);
                value.setAttribute (DIConstants.ATTR_REMIND1RECIP, "" +
                    vie.p_remind1Recip);
                value.setAttribute (DIConstants.ATTR_REMIND1RECIPQUERY, "" +
                    vie.p_remind1RecipQuery);

                // attributes for reminder 2:
                value.setAttribute (DIConstants.ATTR_REMIND2DAYS, "" +
                    vie.p_remind2Days);
                value.setAttribute (DIConstants.ATTR_REMIND2TEXT, "" +
                    vie.p_remind2Text);
                value.setAttribute (DIConstants.ATTR_REMIND2RECIP, "" +
                    vie.p_remind2Recip);
                value.setAttribute (DIConstants.ATTR_REMIND2RECIPQUERY, "" +
                    vie.p_remind2RecipQuery);

                // attributes for escalation:
                value.setAttribute (DIConstants.ATTR_ESCALATEDAYS, "" +
                    vie.p_escalateDays);
                value.setAttribute (DIConstants.ATTR_ESCALATETEXT, "" +
                    vie.p_escalateText);
                value.setAttribute (DIConstants.ATTR_ESCALATERECIP, "" +
                    vie.p_escalateRecip);
                value.setAttribute (DIConstants.ATTR_ESCALATERECIPQUERY, "" +
                    vie.p_escalateRecipQuery);
            } // if FILE or IMAGE value

            // escape the '\n' and remove all '\r' in the description string.
            String valueStr = m2XMLFilter.escapeLineSeparators (vie.value);
            // only add the value in case it is not null
            if (valueStr != null)
            {
                if (vie.type.equalsIgnoreCase (DIConstants.VTYPE_PASSWORD))
                                        // is it a password ?
                {
                    // decrypt the password:
                    valueStr = EncryptionManager.encrypt (valueStr);
                } // if is it a password

                value.appendChild (this.doc.createTextNode (valueStr));
            } // if
            values.appendChild (value);

            // append FIELDREF - DOM after value
            if (vie.type.equalsIgnoreCase (DIConstants.VTYPE_FIELDREF))
            {
                // add additional reference fields to object
                this.addReferencedObjectData (value, vie, requireQuery);
            } // if (vie.type.equalsIgnoreCase (DIConstants.VTYPE_QUERY))

            // is this a value of type VALUEDOMAIN?
            else if (vie.type.startsWith (DIConstants.VTYPE_VALUEDOMAIN))
            {
                // VALUEDOMAIN do not require query
                requireQuery = false;

                // check if emptyOption is set
                if (vie.emptyOption != null)
                {
                    value.setAttribute (DIConstants.ATTR_EMPTYOPTION, vie.emptyOption);
                } // if
                // check if refresh is set
                if (vie.refresh != null)
                {
                    value.setAttribute (DIConstants.ATTR_REFRESH, vie.refresh);
                } // if
                // check if viewType is set
                if (vie.viewType != null)
                {
                    value.setAttribute (DIConstants.ATTR_VIEWTYPE, vie.viewType);
                } // if
                // check if noColumns is set
                if (vie.noColumns != null)
                {
                    value.setAttribute (DIConstants.ATTR_NO_COLUMNS, vie.noColumns);
                } // if
                // check if p_context is set
                if (vie.p_context != null)
                {
                    value.setAttribute (DIConstants.ATTR_CONTEXT, vie.p_context);
                } // if
                if (vie.multiSelection != null)
                {
                    value.setAttribute (DIConstants.ATTR_MULTISELECTION, vie.multiSelection);
                } // if

                // add additional reference fields to object
                this.addReferencedObjectData (value, vie, requireQuery);
            } // if (vie.type.startsWith (DIConstants.VTYPE_VALUEDOMAIN))
        } // for iter

        return values;
    } // createValuesSection


    /**************************************************************************
     * Create the &lt;RESULTS> section of an export document from the data of a
     * query. <BR/>
     *
     * @param   dataElement The dataElement that stores the data of the results.
     *
     * @return  The created &lt;RESULTS> node.
     */
    private Node createResultsSection (DataElement dataElement)
    {
        Element results = null;
        String queryName = null;
        String fieldName = null;

        // check if the result of a query should be added to the
        // export structure.
        // BB TODO: Note that exporting the query result is not
        // always neccessary and can cause bad performance when
        // exporting objects. Note that we use cached query results
        if (this.p_isExportQueryResults)
        {
            // create the results tag:
            results = this.doc.createElement (DIConstants.ELEM_RESULTS);
            queryName = dataElement.name;

            // check if query name has been set
            if (queryName != null && queryName.length () > 0)
            {
                fieldName = queryName;
                // add RESULTROW and RESULTELMENT Tags from Query:
                QueryHelpers.addQueryData (this, results, dataElement.oid,
                    fieldName, queryName, null, env);
            } // if (vie.queryName != null)
        } // if

        return results;
    } // createResultsSection


    /**************************************************************************
     * Create the &lt;RIGHTS> section of an export dokument from the data of an
     * dataElement. <BR/>
     *
     * @param dataElement  the dataElement that stores the data of the values
     *
     * @return the created &lt;RIGHTS> node
     */
    private Node createRightsSection (DataElement dataElement)
    {
        Element rights = this.doc.createElement (DIConstants.ELEM_RIGHTS);
        // include rights stored in the DataElement rights vector:
        for (Iterator<RightDataElement> iter = dataElement.rights.iterator (); iter.hasNext ();)
        {
            RightDataElement rie = iter.next ();
            String profile;
            // <RIGHT NAME="" TYPE="" PROFILE=""/>
            // loop through the profiles. Add a right element for each profile set
            for (Iterator<String> profileIter = rie.profiles.iterator ();
                 profileIter.hasNext ();)
            {
                profile = profileIter.next ();
                Element right = this.doc.createElement (DIConstants.ELEM_RIGHT);
                // set the name
                if (rie.name != null)
                {
                    right.setAttribute (DIConstants.ATTR_NAME, rie.name);
                } // if
                // set the type (user or group)
                if (rie.type != null)
                {
                    right.setAttribute (DIConstants.ATTR_TYPE, rie.type);
                } // if
                // set the profile
                if (profile != null)
                {
                    right.setAttribute (DIConstants.ATTR_PROFILE, profile);
                } // if
                rights.appendChild (right);
            } // for profileIter
        } // for iter

        // return the RIGHTS element
        return rights;
    } // createRightsSection


    /**************************************************************************
     * Create the &lt;ATTACHMENTS> section of an export dokument from the data of an
     * dataElement. <BR/>
     *
     * @param dataElement  the dataElement that stores the data of the values
     *
     * @return the created &lt;ATTACHMENTS> node
     */
    private Node createAttachmentSection (DataElement dataElement)
    {
        Element attachments = null;

        // add the ATTACHMENTS node
        attachments = this.doc.createElement (DIConstants.ELEM_ATTACHMENTS);
        // get the elements form the attachmentList:
        // loop through the elements:
        for (Iterator<? extends Node> iter = dataElement.attachmentList.iterator ();
             iter.hasNext ();)
        {
            // add the XMLDATA node
            Element xmlData = this.doc.createElement (DIConstants.ELEM_XMLDATA);
            attachments.appendChild (xmlData);
            Document actDoc = (Document) iter.next ();
            // import the node with all sub nodes
            Node data = this.doc.importNode (actDoc.getDocumentElement (), true);
            xmlData.appendChild (data);
        } // for iter

        // return the <ATTACHMENTS> node
        return attachments;
    } // createAttachmentSection


    /**************************************************************************
     * add data for a referenced object field (like fieldref,valuedomain, ...)
     * to dom tree for export. <BR/>
     * the domtree-part for a query looks like this:
     *
     *  <VALUE FIELD="Field" INPUT="_FIELD" TYPE="FIELDREF|VALUEDOMAIN|..." (QUERYNAME="searchQuery")?>
     *     0x010104032000233
     *     <FIELDS>
     *       <SYSFIELD NAME="Name" TOKEN="Name"/>
     *       <FIELD NAME="xxx" TOKEN="xxx"/>
     *     </FIELDS>
     *   </VALUE>
     *
     * QUERYNAME is mandatory for FIELDREF and optional for VALUEDOMAIN.
     *
     * @param valueNode         xml-node of value with type of the referenced object field.
     * @param vde               valueDataElement for the referenced object field.
     * @param requireQuery      boolean query required?.
     */
    protected void addReferencedObjectData (Element valueNode,
                                            ValueDataElement vde, boolean requireQuery)
    {
        Document actDoc = valueNode.getOwnerDocument ();

        // check if query is required
        if (requireQuery)
        {
            // remove additional query for valuedomain
            valueNode.setAttribute (DIConstants.ATTR_QUERYNAME, vde.queryName);
        } // if

        // check if there is exist already an oid as value
        if (vde.value.trim ().length () == 0)
        {
            valueNode.appendChild (
                this.doc.createTextNode (OID.EMPTYOID));
        } // if

        // instanciation of all possible node objects
        // Tag <FIELDS>
        Element fieldsNode =
            actDoc.createElement (DIConstants.ELEM_FIELDS);
        // Tag <FIELD> or <SYSFIELD>
        Element fieldNode = null;

        for (int i = 0; vde.p_subTags != null && i < vde.p_subTags.size (); i++)
        {
            ReferencedObjectInfo fri = (ReferencedObjectInfo) vde.p_subTags.elementAt (i);

            if (fri.isSysField ())
            {
                fieldNode =
                    actDoc.createElement (DIConstants.ELEM_SYSFIELD);
            } // if <SYSFIELD>
            else
            {
                fieldNode =
                    actDoc.createElement (DIConstants.ELEM_FIELD);
            } // else <FIELD>

            fieldNode.setAttribute (DIConstants.ATTR_NAME, fri.getName ());
            fieldNode.setAttribute (DIConstants.ATTR_TOKEN, fri.getToken ());

            fieldsNode.appendChild (fieldNode);
        } // for

        valueNode.appendChild (fieldsNode);
    } // addReferencedObjectData

    /**************************************************************************
     * add Results of a m2 - systemquery to dom tree.
     *
     * the domtree-part for a query looks like this:
     *      ...........
     *      <VALUES>
     *          ......
     *            <VALUE FIELD="xxx" TYPE="QUERY" QUERYNAME="xxx">
     *                <INPARAMS>
     *                    <PARAMETER NAME="inpname1p">inpvalue1p</PARAMETER>
     *                    <PARAMETER NAME="inpname2p">inpvalue2p</PARAMETER>
     *                    ........
     *                    <PARAMETER NAME="inpname2p">inpvaluenp</PARAMETER>
     *                </INPARAMS>
     *                <OUTPARAMS>
     *                    <PARAMETER NAME="outpname1p">outpvalue1p</PARAMETER>
     *                    <PARAMETER NAME="outpname2p">outpvalue2p</PARAMETER>
     *                    ........
     *                    <PARAMETER NAME="outpname2p">outpvaluenp</PARAMETER>
     *                </OUTPARAMS>
     *                <RESULTROW>
     *                    <RESULTELEMENT NAME="xnamex" TYPE="xtypey">
     *                      xvaluex</RESULTELEMENT>
     *                </RESULTROW>
     *                <RESULTROW>
     *                    <RESULTELEMENT NAME="xnamex" TYPE="xtypey">
     *                      xvaluex</RESULTELEMENT>
     *                </RESULTROW>
     *            </VALUE>
     *          ......
     *      </VALUES>
     *
     * @param   queryValueNode  ???
     * @param   currentObjOid   ???
     * @param   fieldName       ???
     * @param   queryName       ???
     * @param   subTags         ???
     *
     * @deprecated  This method is replaced by
     *              {@link
     *              ibs.obj.query.QueryHelpers#addQueryData (ibs.bo.BusinessObject, Node, OID, String, String, Vector)
     *              QueryHelpers.addQueryData}.
     */
    @Deprecated
    protected void addQueryData1 (Node queryValueNode, OID currentObjOid,
            String fieldName, String queryName, Vector<Vector<InputParamElement>> subTags)
    {
        int inSize = 0;
        Vector<InputParamElement> inParams = null;
        Vector<QueryParameter> queryParams = null;
        Document actDoc = null;
        String oidString = null;
        QueryExecutive qe = null;
        // instanciation of all possible node objects
        Node rowNode = null;
        Element colNode = null;

        // get the DOM
        actDoc = queryValueNode.getOwnerDocument ();
        // create the QueryExecutive object
        qe = new QueryExecutive ();
        qe.initObject (this.oid, this.user, this.env, this.sess, this.app);
        // has a oid been set?
        if (currentObjOid != null)
        {
            oidString = currentObjOid.toString ();
        } // if (currentObjOid != null)
        else    // no currentObjOid set
        {
            oidString = OID.EMPTYOID;
        } // else no currentObjOid set
        // check if we got any subTags
        if (subTags != null)
        {
            inParams = subTags.elementAt (0);
            inSize = inParams.size ();

            if (inSize > 0)                 // input parameters found?
            {
                for (int i = 0; i < inParams.size (); i++)
                                            // loop through all input parameters
                {
                    InputParamElement inParam = inParams.elementAt (i);

                    qe.addInParameter (inParam.getName (),
                        QueryConstants.FIELDTYPE_STRING,
                        BOHelpers.replaceSysVar (this, inParam.getValue ()));
                } // for loop through all input parameters
            } // if input parameters found
        } // if

        // check if there where any parameters set in variable definition:
        if (inSize == 0)                // no fixed parameters set?
        {
            try
            {
                // get query creator:
                // important: this is the original query creator which
                // shall only be used for reading!
                QueryCreator_01 queryCreator =
                    ((QueryPool) this.app.queryPool)
                        .fetch (queryName, this.user.domain);

                // get environment values for the query search fields
                // if available:
                queryParams = QueryExecutive_01.getSearchfieldParameters (
                    queryCreator.getInputParameters (),
                    this.env, fieldName + "_");
                inSize = queryParams.size ();

                if (inSize > 0)                 // input parameters found?
                {
                    // loop through all input parameters:
                    for (Iterator<QueryParameter> iter = queryParams.iterator (); iter.hasNext ();)
                    {
                        QueryParameter qp = iter.next ();
                        if (qp.getName ().equals ("referenceOid"))
                        {
                            qp.setValue (oidString);
                        } // if
                        qe.addInParameter (qp);
                    } // for iter
                } // if input parameters found
            } // try
            catch (QueryNotFoundException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace ();
            } // catch
        } // if no fixed parameters set

/* KR 20050510: the setting of the referenceOid is not longer necessary because
 *              this is done within setting the QueryCreator parameters above
        // check if there are any input parameters:
        if (inSize == 0)                // there are no input parameters
        {
            qe.addInParameter ("referenceOid",
                QueryConstants.FIELDTYPE_OBJECTID,
                oidString,
                QueryConstants.MATCH_EXACT);
        } // if there are no input parameters
 */

        // if query with name exist and could be executed
        if (qe.execute (queryName))
        {
            int columnCount = qe.getColCount ();

            while (!qe.getEOF ())
            {
                // create row node
                rowNode = actDoc.createElement ("RESULTROW");

                for (int i = 0; i < columnCount; i++)
                {
                    // instanciate nodes for resultelements
                    colNode = actDoc.createElement ("RESULTELEMENT");
                    colNode.setAttribute (DIConstants.ATTR_NAME, qe
                        .getColName (i));
                    colNode.setAttribute (DIConstants.ATTR_TYPE, qe
                        .getColType (i));
                    colNode.appendChild (actDoc.createTextNode (qe
                        .getColValue (i)));
                    // add multilang information
                    colNode.setAttribute (DIConstants.ATTR_MLNAME, qe
                        .getMlColName (i));
                    colNode.setAttribute (DIConstants.ATTR_MLDESCRIPTION, qe
                        .getMlColDescription (i));
                    
                    // add columnnode to rownode in domtree
                    rowNode.appendChild (colNode);
                } // for

                // add rownode to queryNode
                queryValueNode.appendChild (rowNode);  // WEG
                qe.next ();
            } // while
        } // if
    } // addQueryData


    /**************************************************************************
     * Creates an XML document for export of m2 objects. <BR/>
     * This method takes an arry of DataElement objects in order to create the
     * appropriate XML structure. The export document will be initialized first
     * and the DataElement objects will be added to the export document. <BR/>
     *
     * @param dataElements  a dataElement array to create the export document
     *                      from
     *
     * @return  true if the export document has been created succuessfully
     *          or false otherwise
     */
    public boolean create (DataElement[] dataElements)
    {
        // check if we got an DataElement
        if (dataElements == null)
        {
            return false;
        } // if
        // init the export document
        this.initExport ();
        // add the
        return this.add (dataElements);
    } // create


    /**************************************************************************
     * Adds a set of object definitions to an export document.<BR<
     * In case there is no export document created already it will be
     * initialized first.
     * The method uses the insertionPoint property in order to determine
     * where to add the object definitions contained in the dataElement array.
     * <BR/>
     *
     * @param dataElements  a dataElement array to add to the export document
     *
     * @return  true if the export document has been created succuessfully
     *          or false otherwise
     */
    public boolean add (DataElement[] dataElements)
    {
        // check if we got an DataElement
        if (dataElements == null)
        {
            return false;
        } // if
        // this works only for one data element because the hierarchical
        // export is handled by the caller (ExportIntegrator.java)
        if (dataElements.length != 1)
        {
            return false;
        } // if array length != 1

        if (this.p_insertionPoint == null)
        {
            this.initExport ();
        } // if

        // create an object element
        Element node = this.createObjectElement (dataElements[0]);
        // add the DOM node to the current insrtion point
        this.p_insertionPoint.appendChild (node);
        // true to indicate that everything is ok
        return true;
    } // add

    /**************************************************************************
     * Replaces the character '\n' with the character sequence '\\' + 'n' and
     * deletes all LINE_SEPARATOR ('\r') characters in a string. <BR/>
     * The LINE_SEPARATOR character creates problems on xml output.
     * This character appears only in input controls in the Windows
     * environment and is inserted before the '\n' character to mark the
     * end of a line.
     *
     * @param str   the original string
     *
     * @return      the string without LINE_SEPARATOR characters
     */
    public static String escapeLineSeparators (String str)
    {
        if (str == null)
        {
            return null;
        } // if

        StringBuffer s = new StringBuffer ();
        int len = str.length ();
        for (int i = 0; i < len; i++)
        {
            char c = str.charAt (i);
            switch (c)
            {
                case '\n':
                    // '\n' is escaped
                    s.append ("\\n");
                    break;
                case '\\':
                    if (i < len - 1 && str.charAt (i + 1) == 'n')
                    {
                        s.append ("\\\\");
                    } // if
                    else
                    {
                        s.append (c);
                    } // else
                    break;
                case '\r':
                    // '\r' is ignored
                    break;
                default:
                    s.append (c);
            } // switch (c)
        } // for (int i = 0; i < len; i++)
//          debug.DebugClient.debugln ("escape '" + str + "' -> '" + s + "'");
        return s.toString ();
    } // replaceLineSeparators

    /**************************************************************************
     * Replaces the character sequence '\\' + 'n' with '\n'. <BR/>
     *
     * @param str   the original string
     *
     * @return      the replaced string
     */
    public static String unescapeLineSeparators (String str)
    {
        if (str == null)
        {
            return null;
        } // if
        StringBuffer s = new StringBuffer ();
        int len = str.length ();
        for (int i = 0; i < len; i++)
        {
            char c = str.charAt (i);

            // unescape the sequence '\\' + '\\' + 'n'
            if (c == '\\' && i < len - 1)
            {
                char c2 = str.charAt (i + 1);
                if (c2 == 'n')
                {
                    s.append ("\n");
                    i++;
                } // if (c2 == 'n')
                else if (c2 == '\\' && i < len - 2 && str.charAt (i + 2) == 'n')
                {
                    // ignore the first '\\'
                    s.append ("\\n");
                    i += 2;
                } // else if (c2 == '\\' && i < len - 2 && str.charAt (i + 2) == 'n')
                else
                {
                    s.append (c);
                } // else
            } // if (c == '\\' && i < len-1)
            else
            {
                s.append (c);
            } // else
        } // for (int i = 0; i < len; i++)
        return s.toString ();
    } // unescapeLineSeparators

    /**************************************************************************
     * Get the content of the the name attribute of the given node.
     *
     * @param   node        Node of which you want the name attribute content.
     *
     * @return  The content of the name attribute.
     *
     * @see #getAttr (Node, String)
     */
    private static String getNameAttr (Node node)
    {
        return m2XMLFilter.getAttr (node, DIConstants.ATTR_NAME);
    } // getFieldAttr


    /**************************************************************************
     * Get the content of the the one attribute of the given node and name.
     *
     * @param   node        Node of which you want the type attribute content.
     * @param   name        Name of attribute.
     *
     * @return  The content of the attribute.
     */
    private static String getAttr (Node node, String name)
    {
        NamedNodeMap attribs = node.getAttributes ();
        Node attrNode = null;

        if (attribs.getLength () > 0)     // are there attribs ?
        {
            // get the attrib:
            attrNode = attribs.getNamedItem (name);

            // return the content:
            return attrNode.getNodeValue ();
        } // if are there attribs

        // no attributes:
        return null;
    } // getAttr


    /**************************************************************************
     * Get the text content of the given node.
     *
     * @param   node        Node of which you want the text content.
     * @param   env         The current environment.
     *
     * @return  A String which contains the content of the node.
     */
    private static String getNodeValue (Element node, Environment env)
    {
        String cont = null;
        Node child = node.getFirstChild ();

        if (child != null)
        {
            try
            {
                cont = child.getNodeValue ();
            } // try
            catch (DOMException e)
            {
                IOHelpers.showMessage (e, env, true);
            } // catch
        } // if
        else
        {
            IOHelpers.showMessage (
                "Error in m2XMLFilter.getNodeValue: Element " +
                node.getNodeName () + " has no content!!",
                env);
        } // else

        return cont;
    } // getNodeValue

} // m2XMLImportFilter
