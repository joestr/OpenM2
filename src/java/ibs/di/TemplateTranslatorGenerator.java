/**
 * Class: TemplateTranslatorGenerator.java
 */

// package:
package ibs.di;

// imports:
import ibs.BaseObject;
import ibs.bo.BOPathConstants;
import ibs.bo.OID;
import ibs.io.Environment;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.xml.DOMHandler;
import ibs.tech.xml.XMLHelpers;
import ibs.tech.xml.XMLReader;
import ibs.tech.xml.XMLReaderException;
import ibs.tech.xml.XMLWriter;
import ibs.tech.xml.XMLWriterException;
import ibs.util.file.FileHelpers;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/******************************************************************************
 * This is the TemplateTranslatorGenerator for XSLT. <BR/>
 *
 * @version     $Id: TemplateTranslatorGenerator.java,v 1.27 2011/11/16 15:29:58 gweiss Exp $
 *
 * @author      Daniel Janesch (DJ), 011114
 ******************************************************************************
 */
public class TemplateTranslatorGenerator extends BaseObject implements ITemplateTranslatorGenerator
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: TemplateTranslatorGenerator.java,v 1.27 2011/11/16 15:29:58 gweiss Exp $";

    // BT 20090907 TODO: Messages have to be moved to DIMessages
    /**
     * Message: set a default value. <BR/>
     */
    public static String MSG_SET_DEFAULT_VALUE = " set any default value here ";

    /**
     * Message: check the default value from the document template. <BR/>
     */
    public static String MSG_DEFAULT_VALUE_FROM_DOCUMENT_TEMPLATE =
        " check the default value which has been taken from the document template ";
    
    /**************************************************************************
     * Creates an new translator and offers it the user for downloading. <BR/>
     *
     * @param   oldTemplate     Old document template.
     * @param   newTemplate     New document template.
     * @param   user            Object representing the user.
     * @param   env             Enviroment of the application.
     * @param   sess            Session of the user.
     * @param   app             Application informations.
     * @param   oid             Value for the compound object id.
     */
    public void createAndDownloadTranslator (
            DataElement oldTemplate,
            DataElement newTemplate,
            User user,
            Environment env,
            SessionInfo sess,
            ApplicationInfo app,
            OID oid)
    {
        ByteArrayOutputStream translator = null;
        String templateFileName;
        String translatorStr;

        // creates a new translator
        translator =
            this.createNewDocumentTemplateTranslator (
                    oldTemplate, newTemplate, user,
                env, sess, app, oid);

        // get the translator to save:
        try
        {
            translatorStr = translator.toString (DIConstants.CHARACTER_ENCODING);

        // sets the attributes for the translator download in the HTTP-Header:
    //        // set the content type for download:
    //        // (DJ HINT: I tried with different mime-types but that was noneffecitve.
    //        //  I only noticed that the file-extension is decisive for what
    //        //  the browser does with the string.)
    //        env.setContentType ("application/x-qt-stream");
            env.setContentType ("text/xslt");
            // set the filename of downloadable string
            // (in this case no extension is set):
            env.addHeaderEntry ("Content-Length", (translatorStr.getBytes(DIConstants.CHARACTER_ENCODING).length) + "");
            env.addHeaderEntry ("Accept-Ranges", "Bytes");
            // generate the filename:
            if (newTemplate.p_typeCode == null || newTemplate.p_typeCode.length () == 0)
            {
                templateFileName = MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                    DITokens.ML_CHANGE_TO_VALID_FILENAME, env);
            } // if
            else
            {
                templateFileName = newTemplate.p_typeCode + "-TT" +
                    DIConstants.FILEEXTENSION_XSL;
            } // else
            // set the filename in the header
            env.addHeaderEntry ("Content-Disposition", "attachment; filename=\"" + templateFileName + "\";");
            // disable caching
            env.addHeaderEntry ("Cache-Control", "no-cache");
            env.addHeaderEntry ("Pragma", "no-cache");
            env.addHeaderEntry ("Expires", "0");
    
            // writes the string to the enviroment and sets the mime type to
            // a every time downladable:
            env.write (translatorStr);
        } // try
        catch (UnsupportedEncodingException e)
        {
            IOHelpers.showMessage ("Could not write translator because of UnsupportedEncodingException.",
                e, app, sess, env, true);
        } // catch
    } // createAndDownloadTranslator

    /**************************************************************************
     * Creates an translator which change the structure from an given
     * document template to an also given new document template. <BR/>
     * Only the system- and value-tags are changed. <BR/>
     * Returns true if the translator could be created.
     *
     * @param   oldTemplate     Old document template.
     * @param   newTemplate     New document template.
     * @param   user            Object representing the user.
     * @param   env             Enviroment of the application.
     * @param   sess            Session of the user.
     * @param   app             Application informations.
     * @param   oid             Value for the compound object id.
     *
     * @return  The serialized document implementation of the translator
     *          as ByteArrayOutputStream.
     *
     */
    public ByteArrayOutputStream createNewDocumentTemplateTranslator (
                                     DataElement oldTemplate,
                                     DataElement newTemplate,
                                     User user,
                                     Environment env,
                                     SessionInfo sess,
                                     ApplicationInfo app,
                                     OID oid)
    {
        XMLViewer_01 dummyViewer = null; // a dummy XMLViewer to use the
                                        // file methods
        Document newStructure = null;   // the document implementation of the
                                        // template without import informations
        Document translator = null;     // the document implementation of the
                                        // new translator
        ValueDataElement tmpOldValue;   // a temporary copy of one value tag of
                                        // the old template
        NodeList valueList = null;      // contains all value tags of the
                                        // new template
        Node tmpNode = null;        // temporary node
        ByteArrayOutputStream out = null; // the serialized translator
        String tmpPath = "";            // temporary file path
        String tmpFilename = "";        // temporary file name
        int i = 0;                      // counter

        try
        {
            // get an initialized instance of an XMLViewer:
            dummyViewer = this.getXMLViewer (
                user, env, sess, app);
            // create the temporary file path:
            tmpPath = app.p_system.p_m2AbsBasePath +
                      BOPathConstants.PATH_UPLOAD_ABS_FILES_TEMP;
            // create the temporary file name (typename + oid):
            tmpFilename = newTemplate.p_typeCode +  "_" + oid + ".xsl";
            // Write the template to temp m2 path and then read it to be sure that
            // the document implementation only contains informations which are
            // important for the system afterwards the file is deleted.
            // The file name is a string concatenation of the typename and the oid.
            dummyViewer.writeDataFile (newTemplate, tmpFilename, tmpPath);

            newStructure =
                new XMLReader (
                    FileHelpers.addEndingFileSeparator (tmpPath) + tmpFilename,
                    true, null).getDocument ();

            FileHelpers.deleteFile (tmpPath + tmpFilename);

            // get the document implementation out of the parser:
            // build the translator:
            translator = XMLWriter.createDocument ();

            // <xsl:stylesheet>
            Element root = translator.createElement ("xsl:stylesheet");
            // the version of the namespace
            root.setAttribute ("version", "1.0");
            // the place where to find the namespace declaration
            root.setAttribute ("xmlns:xsl", IOConstants.URL_HTTP + "www.w3.org/1999/XSL/Transform");
            // add the root to the xml document
            translator.appendChild (root);
            // <xsl:output>
            Element output = translator.createElement ("xsl:output");
            // the output method
            output.setAttribute ("method", "xml");
            // the encoding of the output
            output.setAttribute ("encoding", DIConstants.CHARACTER_ENCODING);
            // if no the carriage returns would not be put into the output
            output.setAttribute ("indent", "yes");
            // add the output to the xml document
            root.appendChild (output);
            // <xsl:template>
            Element rootTemplate = translator.createElement ("xsl:template");
            // the match expresion
            rootTemplate.setAttribute ("match", "/");
            // add the template to the xml document
            root.appendChild (rootTemplate);

            // import the new template with the necessary nodes:
            this.importNewTemplateToTranslatorDocument (oldTemplate, newTemplate, translator, rootTemplate, newStructure, env);

            // loop trough the elements for the system section
            // and add the corresponding copy-of statements:
            valueList = this.getNodeContent (translator, DIConstants.ELEM_SYSTEM);
            // loop through the nodes:
            for (i = 0; i < valueList.getLength (); i++)
            {
                tmpNode = valueList.item (i);
                if (tmpNode.getNodeType () == Node.ELEMENT_NODE)
                {
                    Element elementNode = (Element) tmpNode;
                    this.addSystemValueOfOld (translator, elementNode);
                } // if (tmpNode.getNodeType () == Node.ELEMENT_NODE)
            } // for (i = 0; i < valueList.getLength (); i++)

            // get all nodes in the values node:
            valueList = this.getNodeContent (translator, DIConstants.ELEM_VALUES);

            // loop through the nodes:
            for (i = 0; i < valueList.getLength (); i++)
            {
                tmpNode = valueList.item (i);
                if (tmpNode.getNodeType () == Node.ELEMENT_NODE)
                                        // is the node of type element ?
                {
                    Element elementNode = (Element) tmpNode;

                    if ((tmpOldValue = oldTemplate.getValueElement (
                        this.getFieldAttr (elementNode))) != null)
                                        // exists the value in the old template
                                        // too ?
                    {                       
                        // The value data element 
                        ValueDataElement vde = newTemplate.getValueElement (
                                this.getFieldAttr (elementNode));
                        
                        boolean changedToMandatory = hasChangedToMandatory (tmpOldValue,
                                vde);
                        
                        // The default value of the current value data element within the new template
                        String defaultValue = vde.value;
                        
                        String typeAttr = this.getTypeAttr (elementNode);
                        if (tmpOldValue.type.equalsIgnoreCase (typeAttr))
                                        // are the types equal ?
                        {
                            if ((typeAttr.equalsIgnoreCase (DIConstants.VTYPE_QUERYSELECTIONBOX) ||
                                 typeAttr.equalsIgnoreCase (DIConstants.VTYPE_QUERYSELECTIONBOXINT) ||
                                 typeAttr.equalsIgnoreCase (DIConstants.VTYPE_QUERYSELECTIONBOXNUM)) &&
                                 tmpOldValue.refresh != null &&
                                 !tmpOldValue.refresh.equals (DIConstants.ATTRVAL_ALWAYS) &&
                                 !DataElement.resolveBooleanValue (tmpOldValue.refresh))
                                        // is the value of type
                                        // queryselectionbox and refreshing = no ?
                            {
                                // set the options-attribute of the node:
                                this.setOptionsAttr (elementNode);
                            } // if is the value of type queryselectionbox and
                              // refreshing = no

                            // adds an value-of command, which refers to
                            // the old field, to the document:
                            this.addValueOfOld (translator, elementNode, true, changedToMandatory, defaultValue);
                        } // if are the types equal
                        else
                        {
                            // handle type conversion
                            this.handleTypeConversion (translator, elementNode, tmpOldValue.type,
                                    typeAttr, changedToMandatory, defaultValue, env);
                        } // else are the types equal
                    } // if exists the value in the old template too
                    else    // this is a new value
                    {
                        // even for a new value we insert a code
                        // that checks if the value can be read from
                        // an existing old column
                        this.addCheckValueOfOld (translator, elementNode);
                    } // else this is a new value

                    // Remove non instance attributes
                    this.removeNonInstanceAttributes (elementNode);
                } // if is the node of type element
            } // for
            out = this.serializeDOM (translator, env);
        } // try
        catch (XMLReaderException e)
        {
            IOHelpers.showMessage (e, app, sess, env, true);
        } // catch
        catch (XMLWriterException e)
        {
            IOHelpers.showMessage ("Could not build translator.",
                e, app, sess, env, true);
        } // catch
        catch (Exception e)
        {
            env.write (e.toString ());
        } // catch

        return out;
    } // createNewDocumentTemplateTranslator

    /**
     * Returns for the given old and new value data element if the mandatory
     * flag has been changed from false to true.
     * 
     * @param   oldValueDataElement   The old value data element.
     * @param   newValueDataElement   The new value data element.
     * @return  Returns if the mandatory flag has been changed from false to true
     */
    private boolean hasChangedToMandatory (
            ValueDataElement oldValueDataElement,
            ValueDataElement newValueDataElement)
    {
        return (oldValueDataElement.mandatory == null || !oldValueDataElement.mandatory.equalsIgnoreCase (DIConstants.ATTRVAL_YES)) &&
                (newValueDataElement.mandatory != null && newValueDataElement.mandatory.equalsIgnoreCase (DIConstants.ATTRVAL_YES));
    } // hasChangedToMandatory

    /**************************************************************************
     * This method imports the root node for the translator document from
     * the given new structure document. <BR/>
     * The current implementation starts at the OBJECT node. 
     *
     * @param   oldTemplate     Old document template.
     * @param   newTemplate     New document template.
     * @param   translator      The translator document.
     * @param   rootTemplate    The template node of the XSLT translator.
     * @param   newStructure    The new template as document.
     * @param   env             The current environment.
     */
    private void importNewTemplateToTranslatorDocument (
                                                        DataElement oldTemplate,
                                                        DataElement newTemplate,
                                                        Document translator,
                                                        Element rootTemplate,
                                                        Document newStructure,
                                                        Environment env)
    {
//        // old version: The whole structure was imported. Not only OBJECTS node.
//        Node node = translator.importNode (newStructure.getDocumentElement (), true);
//
//        // insert the node in the document:
//        rootTemplate.appendChild (node);

        // Retrieve the OBJECTS node and import it to the translator document
        Node node = translator.importNode (XMLHelpers.getNodeByName (newStructure, DIConstants.ELEM_OBJECT, env), true);

        // insert the node in the document:
        rootTemplate.appendChild (node);

        Node systemNode = XMLHelpers.getNodeByName (translator, DIConstants.ELEM_SYSTEM, env);
        Node valuesNode = XMLHelpers.getNodeByName (translator, DIConstants.ELEM_VALUES, env);

        // Remove all nodes first
        this.removeContent (node);

        // Insert the META node:
        Element metaElement = translator.createElement (DIConstants.ELEM_META_TRANSLATOR);

        Element typeCode = translator.createElement (DIConstants.ELEM_TYPECODE);
        typeCode.appendChild (translator.createTextNode (newTemplate.p_typeCode));
        metaElement.appendChild (typeCode);

        Element fromVersion = translator.createElement (DIConstants.ELEM_FROMVERSION);
        // TODO: fromVersion.appendChild(translator.createTextNode (oldTemplate.p_typeVersion));
        metaElement.appendChild (fromVersion);

        Element toVersion = translator.createElement (DIConstants.ELEM_TOVERSION);
        // TODO: toVersion.appendChild(translator.createTextNode (newTemplate.p_typeVersion));
        metaElement.appendChild (toVersion);

        node.appendChild (metaElement);

        // Add the necessary nodes:
        if (systemNode != null)
        {
            node.appendChild (systemNode); // SYSTEM node
        } // if
        if (valuesNode != null)
        {
            node.appendChild (valuesNode); // VALUES node
        } // if
        // Add further nodes here if necessary
        // ...
    } // importNewTemplateToTranslatorDocument


    /**************************************************************************
     * Remove all non instance attributes from the given VALUE element. <BR/>
     *
     * @param   elementNode     The VALUE element
     */
    private void removeNonInstanceAttributes (Element elementNode)
    {
        int length = elementNode.getAttributes ().getLength ();
        List<Node> attributesToRemove = new ArrayList<Node> (length);

        String typeAttr = this.getTypeAttr (elementNode);

        for (int i = 0; i < length; i++)
        {
            Node node = elementNode.getAttributes ().item (i);

            // Check first if it is not the FIELD attribute. Because this attribute must not be removed for all types.
            if (node.getNodeName () != DIConstants.ATTR_FIELD)
            {
                // Check if the TYPE = REMINDER
                if (typeAttr.equals (DIConstants.VTYPE_REMINDER))
                {
                    // Check if is no REMINDER instance attribute
                    if (node.getNodeName () != DIConstants.ATTR_ESCALATEDAYS &&
                            node.getNodeName () != DIConstants.ATTR_ESCALATEDAYS &&
                            node.getNodeName () != DIConstants.ATTR_ESCALATETEXT &&
                            node.getNodeName () != DIConstants.ATTR_REMIND1DAYS &&
                            node.getNodeName () != DIConstants.ATTR_REMIND1RECIP &&
                            node.getNodeName () != DIConstants.ATTR_REMIND1TEXT &&
                            node.getNodeName () != DIConstants.ATTR_REMIND2DAYS &&
                            node.getNodeName () != DIConstants.ATTR_REMIND2RECIP &&
                            node.getNodeName () != DIConstants.ATTR_REMIND2TEXT)
                    {
                        // Add the attribute to the remove list
                        attributesToRemove.add (node);
                    } // if no REMINDER instance attribute
                } // if reminder
                // -----------------------
                // Add further TYPES HERE
                // -----------------------
                else
                {
                    // Add the attribute to the remove list
                    attributesToRemove.add (node);
                } // else
            } // if
        } // for

        Iterator<Node> it = attributesToRemove.iterator ();
        while (it.hasNext ())
        {
            // Remote the attribute
            elementNode.removeAttribute (it.next ().getNodeName ());
        } // while
    } // removeNonInstanceAttributes


    /**************************************************************************
     * This method handles type conversion for value fields. If an automatic
     * type translation can be performed the necessary XSLT code is added to
     * the translator. Otherwise a comment is inserted at this position. <BR/>
     *
     * @param   translator          The translator document.
     * @param   elementNode         The value node.
     * @param   oldType             The old type of the value field.
     * @param   newType             The new type of the value field.
     * @param   changedToMandatory  Indicates if a check from non mandatory to
     *                              mandatory has been performed.
     * @param   defaultValue        The default value of the current field.
     * @param   env                 The current environment 
     */
    private void handleTypeConversion (Document translator,
                                       Element elementNode, String oldType,
                                       String newType, boolean changedToMandatory,
                                       String defaultValue,
                                       Environment env)
    {
        // Check if a conversion from numeric to a textual type is performed
        if ((newType.equals (DIConstants.VTYPE_TEXT) ||
             newType.equals (DIConstants.VTYPE_LONGTEXT)) &&
            (oldType.equals (DIConstants.VTYPE_FLOAT) ||
                oldType.equals (DIConstants.VTYPE_DOUBLE) ||
                oldType.equals (DIConstants.VTYPE_INT) ||
                oldType.equals (DIConstants.VTYPE_NUMBER)))
        {
            // Handle type conversion of compatible types
            this.handleCompatibleTypeConversion (translator, elementNode, oldType, newType,
                    changedToMandatory, defaultValue, env);
        } // if numeric to textual
        // Check if a conversion from TEXT to LONGTEXT is made
        else if (newType.equals (DIConstants.VTYPE_LONGTEXT) &&
            oldType.equals (DIConstants.VTYPE_TEXT))
        {
            // Handle type conversion of compatible types
            this.handleCompatibleTypeConversion (translator, elementNode, oldType, newType,
                    changedToMandatory, defaultValue, env);
        } // if conversion from TEXT to LONGTEXT is made
        // Check if a conversion between HTMLTEXT and LONGTEXT is made
        else if ((newType.equals (DIConstants.VTYPE_LONGTEXT) &&
                  oldType.equals (DIConstants.VTYPE_HTMLTEXT)) ||
                 (newType.equals (DIConstants.VTYPE_HTMLTEXT) &&
                  oldType.equals (DIConstants.VTYPE_LONGTEXT)))
        {
            // Handle type conversion of compatible types
            this.handleCompatibleTypeConversion (translator, elementNode, oldType, newType,
                    changedToMandatory, defaultValue, env);
        } // if conversion between HTMLTEXT and LONGTEXT is made
        // Check if a conversion from objectref to fieldref is performed
        else if (newType.equals (DIConstants.VTYPE_FIELDREF) &&
            oldType.equals (DIConstants.VTYPE_OBJECTREF))
        {
            Node fields = this.getSubNodeByName (elementNode, DIConstants.ELEM_FIELDS);

            // Add the type change comments:
            this.addTypeChangeComment (translator, elementNode, oldType, newType,
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_TYPE_CHANGE_CHECK_CONTENT, env), true, env);

            // create the test command for the objectref to fieldref translation
            String testCommand = "/" + DIConstants.ELEM_OBJECT +
                "/" + DIConstants.ELEM_VALUES + "/" + DIConstants.ELEM_VALUE +
                "[@" + DIConstants.ATTR_FIELD + " = '" +
                this.getFieldAttr (elementNode) + "']";
            
            // create the select command for the objectref to fieldref translation
            String selectCommand = "substring (/" + DIConstants.ELEM_OBJECT +
                "/" + DIConstants.ELEM_VALUES + "/" + DIConstants.ELEM_VALUE +
                "[@" + DIConstants.ATTR_FIELD + " = '" +
                this.getFieldAttr (elementNode) + "']" +
                "/child::node(), 0, 19)";

            // add the value-of command.
            this.addValueOf (translator, elementNode, newType,
                    testCommand,selectCommand, changedToMandatory, defaultValue);

            if (fields != null)
            {
                // add the fields tag again
                elementNode.appendChild (fields);
            } // if fields != null
        } // else if objectref to fieldref
        // otherwise
        else
        {
            // Add the type change comments:
            this.addTypeChangeComment (translator, elementNode, oldType, newType, null, false, env);
        } // otherwise
    } // handleTypeConversion


    /**************************************************************************
     * Generates the translation code for a type change with two compatible
     * types (e.g. like a generation from TEXT to LONGTEXT.
     *
     * @param   doc                 The document.
     * @param   node                The node where to add the comment.
     * @param   oldType             The old type of the value field
     * @param   newType             The new type of the value field
     * @param   changedToMandatory  Indicates if a check from non mandatory to
     *                              mandatory has been performed.
     * @param   defaultValue        The default value of the current field.
     * @param   env                 The current environment
     */
    private void handleCompatibleTypeConversion (Document doc, Element node,
            String oldType, String newType, boolean changedToMandatory,
            String defaultValue, Environment env)
    {
        // Add the type change comments:
        this.addTypeChangeComment (
            doc, node, oldType, newType,
            MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                DIMessages.ML_MSG_TYPE_CHANGE_CHECK_CONTENT, env), true, env);

        // adds an value-of command, which refers to
        // the old field, to the document:
        this.addValueOfOld (doc, node, false, changedToMandatory, defaultValue);
    } // addComment


    /**************************************************************************
     * Adds a type mismatch comment to the content of the node. <BR/>
     * Before the comment is added the whole content is removed.
     *
     * @param   doc             The document.
     * @param   node            The node where to add the comment.
     * @param   oldType         The old type of the value field
     * @param   newType         The new type of the value field
     * @param   modifyMessage   The modify message tells gives the user
     *                          information about the generated tranlsation code.
     *                          If this is null a standard text is generated
     *                          saying that no tranlsation could have been
     *                          generated.
     * @param   removeContent   Defines if the whole content should be removed
     *                          before the command is added.
     * @param   env             The current environment
     */
    private void addTypeChangeComment (Document doc, Node node,
            String oldType, String newType,
            String modifyMessage,
            boolean removeContent,
            Environment env)
    {
        String modifyMessageLocal = modifyMessage; // variable for local assignments

        if (removeContent)
        {
            this.removeContent (node);
        } // if removeContent

        // Create a comment with the information, how the datatype has changed:
        StringBuilder sb = new StringBuilder ()
            .append (
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_TYPE_CHANGE, env))
            .append (oldType).append (" -> ")
            .append (newType);
        Comment commentTypeChange = doc.createComment (sb.toString ());
        node.appendChild (commentTypeChange);

        // Crate a comment with the modify info for the user:
        if (modifyMessageLocal == null)
        {
            modifyMessageLocal = 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_CHANGE_CONTENT, env);
        } // if message == null
        Comment comment = doc.createComment (modifyMessageLocal);
        node.appendChild (comment);
    } // addTypeChangeComment


    /**************************************************************************
     * Retrieves the sub node with the given name of the given node.
     *
     * @param   parentNnode    The node including the sub node.
     * @param   subNodeName    The name of sub node to return.
     *
     * @return  Returns the subnode with the given name.
     *
     */
    private Node getSubNodeByName (Element parentNnode, String subNodeName)
    {
        Node subNode = null;

        // Find the FIELDS tag
        for (int i = 0; i < parentNnode.getChildNodes ().getLength (); i++)
        {
            Node node = parentNnode.getChildNodes ().item (i);

            // Check first if it is not the FIELD attribute. Because this attribute must not be removed for all types.
            if (node.getNodeName () == subNodeName)
            {
                subNode = node;
                break;
            } // if FIELDS elem
        } // for

        return subNode;
    } // getSubNodeByName


    /**************************************************************************
     * Creates a XMLViewer_01 object and sets the environment. <BR/>
     *
     * @param   user            Object representing the user.
     * @param   env             Enviroment of the application.
     * @param   sess            Session of the user.
     * @param   app             Application informations.
     *
     * @return  an XMLViewer_01 object
     */
    private XMLViewer_01 getXMLViewer (User user, Environment env,
        SessionInfo sess, ApplicationInfo app)
    {
        XMLViewer_01 viewer = new XMLViewer_01 ();
        viewer.initObject (OID.getEmptyOid (), user, env, sess, app);
        return viewer;
    } // getXMLViewer


    /**************************************************************************
     * Serializes an document implementation to show it to the user. <BR/>
     *
     * @param   translator      Document implementation of the new translator.
     * @param   env             Enviroment of the application.
     *
     * @return  If no error occurs the serialized translator is returned,
     *          otherwise <CODE>null</CODE>.
     *
     */
    private ByteArrayOutputStream serializeDOM (Document translator,
        Environment env)
    {
        DOMHandler serializer = new DOMHandler (env, null, null);
        return serializer.serializeDOM (translator);
    } // serializeDOM


    /**************************************************************************
     * Reads the content of the XML file and returns an DOM document.
     * <BR/>
     *
     * @param   filePathName    The path and the filename of the xml-file.
     *
     * @return  The document implementation of the xml-structure.
     *
     */
    private Document getDocument (String filePathName)
    {
        try
        {
            // read the document:
            // do not validate the document. This speeds up the parsing
            Document doc = new XMLReader (filePathName, false, null)
                .getDocument ();

            // return the document:
            return doc;
        } //try
        catch (XMLReaderException e)
        {
            System.out.println (e.toString ());
            return null;
        } // catch
/*KR old
        // create a DOMParser instance
        DOMParser parser = new DOMParser ();

        try
        {
            // do not validate the document. This speeds up the parsing
            parser.setFeature (IOConstants.URL_HTTP + "xml.org/sax/features/validation", false);

            // turn on namespace support
            parser.setFeature (IOConstants.URL_HTTP + "xml.org/sax/features/namespaces", true);

            // get th parser:
            parser.parse (new InputSource (new FileReader (filePathName)));

            // read the stream and return the document
            Document doc = parser.getDocument();

            // return the document
            return doc;
        } //try
        catch (SAXException e)
        {
            e.toString ();
            return null;
        } //catch
        catch (IOException e)
        {
            e.toString ();
            return null;
        } //catch
*/
    } // getDocument


    /**************************************************************************
     * Store the value in the DOM node. <BR/>
     * The value is stored in the first child node (#TEXT node).
     *
     * @param   node        The DOM node where to store the value.
     * @param   value       The value to store.
     */
    protected final void setNodeValue (Element node, String value)
    {
        // get the first child node
        Node textNode = node.getFirstChild ();

        // check if there is a child node
        if (textNode == null)
        {
            // set a new #TEXT node
            if (value != null)
            {
                node.appendChild (node.getOwnerDocument ().createTextNode (value));
            } // if
        } // if (textNode == null)
        else
        {
            // if the first child is a #TEXT node set the new value
            if (textNode.getNodeType () == Node.TEXT_NODE)
            {
                // set the new value for the node
                if (value != null)
                {
                    textNode.setNodeValue (value);
                } // if
            } // if valid #TEXT node
            else
            {
                // insert a new #TEXT node on top of the child list
                if (value != null)
                {
                    node.insertBefore (node.getOwnerDocument ().createTextNode (value), textNode);
                } // if
            } // else if #TEXT node
        } // else if (textNode == null)
    } // setNodeValue


    /**************************************************************************
     * Sets the content of the options-attribute. <BR/>
     *
     * @param   node        Node which contains the attribute.
     *
     */
    private void setOptionsAttr (Element node)
    {
        String valuePath = "{" +
            "/" + DIConstants.ELEM_OBJECT +
            "/" + DIConstants.ELEM_VALUES + "/" + DIConstants.ELEM_VALUE +
            "[@" + DIConstants.ATTR_FIELD + " = '" +
            this.getFieldAttr (node) + "']" +
            "/@" + DIConstants.ATTR_OPTIONS + "}";

        // fill the attribute with the new content:
        node.setAttribute (DIConstants.ATTR_OPTIONS, valuePath);
    } // setOptionsAttr


    /**************************************************************************
     * Get the whole content (all type of nodes) out of an node which is given
     * by the node name. <BR/>
     * If this node is more than one times in the document than the first node
     * is taken.
     *
     * @param   doc         The document.
     * @param   nodeName    The name of node where the contents should taken.
     *
     * @return  If the node was found a list of nodes which represents all nodes,
     *          otherwise null.
     *
     */
    private NodeList getNodeContent (Document doc, String nodeName)
    {
        NodeList nodes = doc.getElementsByTagName (nodeName);

        if (nodes.getLength () > 0)     // was the node found ?
        {
            return nodes.item (0).getChildNodes ();
        } // if was the node found

        // the node was not found:
        return null;
    } // getNodeContent


    /**************************************************************************
     * Get the content of the the type attribute of the given node.
     *
     * @param   node        Node of which you want the type attribute content.
     *
     * @return  The content of the type attribute.
     *
     * @see #getAttr(Node, String)
     */
    private String getTypeAttr (Node node)
    {
        return this.getAttr (node, DIConstants.ATTR_TYPE);
    } // getTypeAttr


    /**************************************************************************
     * Get the content of the the field attribute of the given node.
     *
     * @param   node        Node of which you want the field attribute content.
     *
     * @return  The content of the field attribute.
     *
     * @see #getAttr(Node, String)
     */
    private String getFieldAttr (Node node)
    {
        return this.getAttr (node, DIConstants.ATTR_FIELD);
    } // getFieldAttr


    /**************************************************************************
     * Get the content of the the one attribute of the given node and name.
     *
     * @param   node        Node of which you want the type attribute content.
     * @param   name        Name of attribute.
     *
     * @return  The content of the attribute.
     */
    private String getAttr (Node node, String name)
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

        // there are no attribs:
        return null;
    } // getAttr

    /**************************************************************************
     * Adds a value-of command to the content of the node. <BR/>
     * This command selects the content of the value with the same field
     * attribute. Before the command is added the whole content is removed.
     *
     * @param   doc                 The document.
     * @param   node                The node where to add the command.
     * @param   removeNodeContent   Defines if the whole content should be removed
     *                              before the command is added.
     * @param   changedToMandatory  Indicates if a check from non mandatory to
     *                              mandatory has been performed.
     * @param   defaultValue        The default value of the current field. 
     */
    private void addValueOfOld (Document doc, Element node,
            boolean removeNodeContent, boolean changedToMandatory,
            String defaultValue)
    {
        // For certain types it is necessary to cache an existing subnode for re appanding it
        // later on.
        Node subNode = null;

        String typeAttr = this.getTypeAttr (node);
        if (typeAttr.equalsIgnoreCase (DIConstants.VTYPE_FIELDREF) ||
                typeAttr.equalsIgnoreCase (DIConstants.VTYPE_VALUEDOMAIN))
        {
            subNode = this.getSubNodeByName (node, DIConstants.ELEM_FIELDS);
        } // if fieldref or value domain

        // check if the node content should be removed
        if (removeNodeContent)
        {
            // deletes all nodes out of the conetent:
            this.removeContent (node);
        } // if removeNodeContent

        String valuePath = "/" + DIConstants.ELEM_OBJECT +
            "/" + DIConstants.ELEM_VALUES + "/" + DIConstants.ELEM_VALUE +
            "[@" + DIConstants.ATTR_FIELD + " = '" +
            this.getFieldAttr (node) + "']";

        // add the value-of command.
        this.addValueOf (doc, node, typeAttr, valuePath, null, changedToMandatory,
                defaultValue);

        // chekc if the sub node is not null
        if (subNode != null)
        {
            // re append the cached sub node
            node.appendChild (subNode);
        } // if subnode != null
    } // addValueOfOld

    /**************************************************************************
     * Adds a value-of command to the content of the node by using the given
     * select command.
     *
     * @param   doc                 The document.
     * @param   valueElementNode    The value element node.
     * @param   valueType           The type of the data value element
     *                              represented by the node.
     * @param   valuePath           The XSLT command to retrieve the former value.
     * @param   selectCmd           The XSLT select command to retrieve the
     *                              former value.
     * @param   changedToMandatory  Indicates if a check from non mandatory to
     *                              mandatory has been performed.
     * @param   defaultValue        The default value of the current field.
     *                              
     */
    private void addValueOf (
            Document doc,
            Node valueElementNode,
            String valueType,
            String valuePath, String selectCmd,
            boolean changedToMandatory,
            String defaultValue)
    {
        // Local select and test command variables
        String selectCmdLocal;
        String testCmdLocal = valuePath + "!=''";
        
        // Check if a select command has been provided:
        if (selectCmd != null)
        {
            // take the provided select command
            selectCmdLocal = selectCmd;
        }
        // no select command provided
        else
        {
            // create the select command from the test command
            selectCmdLocal = valuePath + "/child::node()";
        }// else no select command provided        

        if (!changedToMandatory)
        {
            // <xsl:value-of>
            Element valueOf = doc.createElement ("xsl:copy-of");
            valueOf.setAttribute ("select", selectCmdLocal);
            valueElementNode.appendChild (valueOf);
        }
        else
        {          
            // initialize the variable
            boolean isDefaultValueSet = false;
            
            if (defaultValue != null && !defaultValue.isEmpty ())
            {
                    isDefaultValueSet = true;
            } // if defaultValue is set
                       
            // add the following xsl code:
            // <xsl:choose>
            // <xsl:when test="/OBJECT/VALUES/VALUE[@FIELD = '...']!=''">
            //    <xsl:copy-of select="/OBJECT/VALUES/VALUE[@FIELD = '...']/child::node()"/>
            // </xsl:when>
            // <xsl:otherwise>
            //     <!-- set any default value here -->
            //     <xsl:value-of select="''"/>
            // </xsl:otherwise>

            // xsl:choose
            Element chooseNode = doc.createElement ("xsl:choose");
            valueElementNode.appendChild (chooseNode);
            // xsl:when
            Element whenNode = doc.createElement ("xsl:when");
            whenNode.setAttribute ("test", testCmdLocal);
            chooseNode.appendChild (whenNode);
            // xsl:copy-of
            Element copyOfNode = doc.createElement ("xsl:copy-of");
            copyOfNode.setAttribute ("select", selectCmdLocal);
            whenNode.appendChild (copyOfNode);
            // xsl:otherwise
            Element otherwiseNode = doc.createElement ("xsl:otherwise");
            chooseNode.appendChild (otherwiseNode);
            // comment
            otherwiseNode.appendChild (
                doc.createComment (isDefaultValueSet ?
                        TemplateTranslatorGenerator.MSG_DEFAULT_VALUE_FROM_DOCUMENT_TEMPLATE :
                            TemplateTranslatorGenerator.MSG_SET_DEFAULT_VALUE));
            // xsl:value-of
            Element valueOfNode = doc.createElement ("xsl:value-of");
            valueOfNode.setAttribute ("select", isDefaultValueSet ? "'" + defaultValue + "'" : "'[" + valueType + "] - default value'");
            otherwiseNode.appendChild (valueOfNode);
        }
    } // addValueOfOld


    /**************************************************************************
     * Adds the following code to get any value or set a default value:. <BR/>
     * <CODE>
     * &lt;xsl:choose&gt;. <BR/>
     *  &lt;xsl:when test="/OBJECT/VALUES/VALUE[@FIELD = '...']"&gt;. <BR/>
     *   &lt;xsl:copy-of select="/OBJECT/VALUES/VALUE[@FIELD = '...']/child::node()"/&gt;. <BR/>
     *  &lt;/xsl:when&gt;. <BR/>
     * &lt;xsl:otherwise&gt;. <BR/>
     *  &lt;!-- set any default value here --&gt;. <BR/>
     *  &lt;xsl:value-of select="''"/&gt;. <BR/>
     * &lt;/xsl:otherwise&gt;. <BR/>
     * </CODE>
     *
     * @param   doc         The document.
     * @param   node        The node where to add the command.
     */
    private void addCheckValueOfOld (Document doc, Node node)
    {
        // create the xpath expressions
        String valuePath = "/" + DIConstants.ELEM_OBJECT +
            "/" + DIConstants.ELEM_VALUES + "/" + DIConstants.ELEM_VALUE +
            "[@" + DIConstants.ATTR_FIELD + " = '" +
                this.getFieldAttr (node) + "']";
        String valuePathChild = valuePath + "/child::node()";

        // deletes all nodes out of the content:
        this.removeContent (node);
        // add the following xsl code:
        // <xsl:choose>
        // <xsl:when test="/OBJECT/VALUES/VALUE[@FIELD = '...']">
        //    <xsl:copy-of select="/OBJECT/VALUES/VALUE[@FIELD = '...']/child::node()"/>
        // </xsl:when>
        // <xsl:otherwise>
        //     <!-- set any default value here -->
        //     <xsl:value-of select="''"/>
        // </xsl:otherwise>

        // xsl:choose
        Element chooseNode = doc.createElement ("xsl:choose");
        node.appendChild (chooseNode);
        // xsl:when
        Element whenNode = doc.createElement ("xsl:when");
        whenNode.setAttribute ("test", valuePath);
        chooseNode.appendChild (whenNode);
        // xsl:copy-of
        Element copyOfNode = doc.createElement ("xsl:copy-of");
        copyOfNode.setAttribute ("select", valuePathChild);
        whenNode.appendChild (copyOfNode);
        // xsl:otherwise
        Element otherwiseNode = doc.createElement ("xsl:otherwise");
        chooseNode.appendChild (otherwiseNode);
        // comment
        otherwiseNode.appendChild (
            doc.createComment (TemplateTranslatorGenerator.MSG_SET_DEFAULT_VALUE));
        // xsl:value-of
        Element valueOfNode = doc.createElement ("xsl:value-of");
        valueOfNode.setAttribute ("select", "''");
        otherwiseNode.appendChild (valueOfNode);
    } // addCheckValueOfOld


    /***************************************************************************
     * Adds a value-of command to the content of a system node. <BR/>
     * This command selects the content of the system value with the same field
     * attribute. Before the command is added the whole content is removed.
     *
     * @param   doc         The document.
     * @param   node        The node where to add the command.
     */
    private void addSystemValueOfOld (Document doc, Element node)
    {
        String valuePath = "/" + DIConstants.ELEM_OBJECT +
            "/" + DIConstants.ELEM_SYSTEM + "/" + node.getNodeName () +
            "/child::node()";

        // deletes all nodes out of the conetent:
        this.removeContent (node);

        // <xsl:value-of>
        Element valueOf = doc.createElement ("xsl:copy-of");
        valueOf.setAttribute ("select", valuePath);
        node.appendChild (valueOf);
    } // addSystemValueOfOld


    /**************************************************************************
     * Removes all child nodes of a node. <BR/>
     *
     * @param   node        The node where to add the comment.
     */
    private void removeContent (Node node)
    {
        while (node.hasChildNodes ())   // removes all child nodes:
        {
            // deletes always the first child node:
            node.removeChild ((node.getChildNodes ()).item (0));
        } // removes all child nodes
    } // removeContent

} // class TemplateTranslatorGenerator
