/*
 * Class: Response.java
 */

// package:
package ibs.di;

// imports:
import ibs.tech.xml.DOMHandler;
import ibs.tech.xml.XMLReader;
import ibs.tech.xml.XMLReaderException;
import ibs.tech.xml.XMLWriter;
import ibs.tech.xml.XMLWriterException;

import java.io.CharArrayWriter;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;


/******************************************************************************
 * The Response class reads and generates responses.<BR/>
 * There are two structures supported:
 *
 * The basic response structure:<BR/>
 *<pre>
 * &lt;ACTION>
 *      &lt;CODE>...&lt;/CODE>
 *      &lt;NAME>...&lt;/NAME>
 * &lt;/ACTION>
 * &lt;LOG>...&lt;/LOG>
 * &lt;RETURN>
 *      &lt;TYPE>...&lt;/TYPE>
 *      &lt;CODE>...&lt;/CODE>
 *      &lt;MESSAGE>...&lt;/MESSAGE>
 *      &lt;ID>...&lt;/ID>
 *      &lt;VALUES>
 *         &lt;VALUE FIELD="...">...&lt;/VALUE>
 *         &lt;VALUE FIELD="...">...&lt;/VALUE>
 *      &lt;/VALUES>
 * &lt;/RETURN>
 * </pre>
 *
 * <BR/>
 * The multipart response structure, where ObjectId and values from the object
 * can be added to read:<BR/>
 *
 * <pre>
 * &lt;ACTION>
 *      &lt;CODE>...&lt;/CODE>
 *      &lt;NAME>...&lt;/NAME>
 * &lt;/ACTION>
 * &lt;LOG>...&lt;/LOG>
 * &lt;RETURN>
 *   &lt;LINE>
 *      &lt;TYPE>...&lt;/TYPE>
 *      &lt;CODE>...&lt;/CODE>
 *      &lt;MESSAGE>...&lt;/MESSAGE>
 *      &lt;ID>...&lt;/ID>
 *      &lt;VALUES>
 *         &lt;VALUE FIELD="...">...&lt;/VALUE>
 *         &lt;VALUE FIELD="...">...&lt;/VALUE>
 *      &lt;/VALUES>
 *   &lt;/LINE>
 *   &lt;LINE>...&lt;/LINE>
 * &lt;/RETURN>
 * </pre>
 *
 * @version     $Id: Response.java,v 1.14 2008/09/17 16:18:37 kreimueller Exp $
 *
 * @author      Buchegger Bernd (BB), 020430
 ******************************************************************************
 */
public class Response extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Response.java,v 1.14 2008/09/17 16:18:37 kreimueller Exp $";


    /**
     * Response tag: ACTION. <BR/>
     */
    public static final String RESPONSETAG_ACTION = "ACTION";

    /**
     * Response tag: CODE. <BR/>
     */
    public static final String RESPONSETAG_CODE = "CODE";

    /**
     * Response tag: NAME. <BR/>
     */
    public static final String RESPONSETAG_NAME = "NAME";

    /**
     * Response tag: LOG. <BR/>
     */
    public static final String RESPONSETAG_LOG = "LOG";

    /**
     * Response tag: RETURN. <BR/>
     */
    public static final String RESPONSETAG_RETURN = "RETURN";

    /**
     * Response tag: TYPE. <BR/>
     */
    public static final String RESPONSETAG_TYPE = "TYPE";

    /**
     * Response tag: MESSAGE. <BR/>
     */
    public static final String RESPONSETAG_MESSAGE = "MESSAGE";

    /**
     * Response tag: LINE. <BR/>
     */
    public static final String RESPONSETAG_LINE = "LINE";

    /**
     * Response tag: ID. <BR/>
     */
    public static final String RESPONSETAG_ID = "ID";

    /**
     * Response tag: VALUES. <BR/>
     */
    public static final String RESPONSETAG_VALUES = "VALUES";

    /**
     * Response tag: VALUE. <BR/>
     */
    public static final String RESPONSETAG_VALUE = "VALUE";

    /**
     * Response attribute: FIELD. <BR/>
     */
    public static final String RESPONSEATTR_FIELD = "FIELD";


    /**
     * The namespace URI. <BR/>
     */
    public static final String NAMESPACE_URI = "urn:www.trinitec.at:m2";

    /**
     * the qualified name for the namespace. <BR/>
     */
    public static final String NAMESPACE_QUALIFIEDNAME = "m2:RESPONSE";

    /**
     * The version attribute. <BR/>
     */
    public static final String ATTR_VERSION = "VERSION";

    /**
     * the response version. <BR/>
     */
    public static final String VERSION = "1.0";

    /**
     * the XMLNS attribute. <BR/>
     */
    public static final String ATTR_XMLNS = "xmlns:m2";

    /**
     * the xmlns value. <BR/>
     */
    public static final String XMLNS = "urn:www.trinitec.at:m2";


    /**
     * Code of the function that has been called. (if applicable). <BR/>
     */
    protected int p_functionCode = 0;

    /**
     * Name of the function that has been called. (if applicable). <BR/>
     */
    protected String p_functionName = "";

    /**
     * type of the response (Success, Error, Warning, Info). <BR/>
     */
    protected int p_responseType = DIConstants.RESPONSE_SUCCESS;

    /**
     * Code of the error in case there has been one. <BR/>
     */
    protected String p_errorCode = "";

    /**
     * Message of the error in case there has been one. <BR/>
     */
    protected String p_errorMessage = "";

    /**
     * Reference to the object this part of the response is adressing. <BR/>
     */
    protected String p_objectReference = "";

    /**
     * Vector that holds the values in case they have been passed
     * in the response. This will be a vector of ValueDataElement
     * instances. <BR/>
     */
    protected Vector<ValueDataElement> p_values = null;

    /**
     * Vector that holds the elements of a multipart response. <BR/>
     */
    public Vector<MultipartElement> p_responseElements = null;

    /**
     * Vector that holds the elements of a multipart response
     * that are errors. <BR/>
     */
    public Vector<MultipartElement> p_errorElements = null;

    /**
     * Vector that holds the elements of a multipart response
     * that are warnings. <BR/>
     */
    public Vector<MultipartElement> p_warningElements = null;

    /**
     * option if this will be a multipart response. <BR/>
     */
    protected boolean p_isMultipartResponse = false;

    /**
     * Cache to store a multipart element. just for the case it should be
     * reused. <BR/>
     */
    protected MultipartElement p_multipartCache = null;

    /**
     * option to include the log in the response. <BR/>
     */
    protected boolean p_isIncludeLog = true;

    /**
     * A log to include in the message. <BR/>
     */
    protected Log_01 p_log = null;


    /**************************************************************************
     * Creates an Response Object.. <BR/>
     */
    public Response ()
    {
        // will this be created as a multipart response?
        if (this.p_isMultipartResponse)
        {
            this.p_responseElements = new Vector<MultipartElement> ();
            this.p_errorElements = new Vector<MultipartElement> ();
            this.p_warningElements = new Vector<MultipartElement> ();
        } // if (this.p_isMultipartResponse)
    } // Response


    /**************************************************************************
     * Getter method for the p_functionCode property. <BR/>
     *
     * @return the value of the p_functionCode property
     */
    public int getFunctionCode ()
    {
        return this.p_functionCode;
    } // getFunctionCode


    /**************************************************************************
     * Setter method for the p_functionCode property. <BR/>
     *
     * @param functionCode    the value to set
     */
    public void setFunctionCode (int functionCode)
    {
        this.p_functionCode = functionCode;
    } // setFunctionCode


    /**************************************************************************
     * Getter method for the p_functionName property. <BR/>
     *
     * @return the value of the p_functionName property
     */
    public String getFunctionName ()
    {
        return this.p_functionName;
    } // getFunctionName


    /**************************************************************************
     * Setter method for the p_functionName property. <BR/>
     *
     * @param functionName    the value to set
     */
    public void setFunctionName (String functionName)
    {
        this.p_functionName = functionName;
    } // setFunctionName


    /**************************************************************************
     * Getter method for the p_responseType property. <BR/>
     *
     * @return the value of the p_responseState property
     */
    public int getResponseType ()
    {
        return this.p_responseType;
    } // getResponseType


    /**************************************************************************
     * Setter method for the p_responseType property. <BR/>
     *
     * @param responseType    the value to set
     */
    public void setResponseType (int responseType)
    {
        this.p_responseType = responseType;
    } // setResponseType


    /**************************************************************************
     * Getter method for the p_errorCode property. <BR/>
     *
     * @return the value of the p_errorCode property
     */
    public String getErrorCode ()
    {
        return this.p_errorCode;
    } // getErrorCode


    /**************************************************************************
     * Setter method for the p_errorCode property. <BR/>
     *
     * @param errorCode    the value to set
     */
    public void setErrorCode (String errorCode)
    {
        this.p_errorCode = errorCode;
    } // setErrorCode


    /**************************************************************************
     * Getter method for the p_errorMessage property. <BR/>
     *
     * @return the value of the p_errorMessage property
     */
    public String getErrorMessage ()
    {
        return this.p_errorMessage;
    } // getErrorMessage


    /**************************************************************************
     * Setter method for the p_errorMessage property. <BR/>
     *
     * @param errorMessage    the value to set
     */
    public void setErrorMessage (String errorMessage)
    {
        this.p_errorMessage = errorMessage;
    } // setErrorMessage


    /**************************************************************************
     * Getter method for the p_objectReference property. <BR/>
     *
     * @return the value of the p_objectReference property
     */
    public String getObjectReference ()
    {
        return this.p_objectReference;
    } // getObjectReference


    /**************************************************************************
     * Setter method for the p_objectReference property. <BR/>
     *
     * @param objectReference    the value to set
     */
    public void setObjectReference (String objectReference)
    {
        this.p_objectReference = objectReference;
    } // setObjectReference


    /**************************************************************************
     * Getter method for the p_isMultipartResponseproperty. <BR/>
     *
     * @return the value of the p_isMultipartResponse property
     */
    public boolean getIsMultipartResponse ()
    {
        return this.p_isMultipartResponse;
    } // getIsMultipartResponse


    /**************************************************************************
     * Setter method for the p_isMultipartResponse property. <BR/>
     *
     * @param isMultipartResponse     the value to set
     */
    public void setIsMultipartResponse (boolean isMultipartResponse)
    {
        this.p_isMultipartResponse = isMultipartResponse;
    } // setIsMultipartResponse


    /**************************************************************************
     * Set the option to include the log in the response. <BR/>
     *
     * @param isIncludeLog  include the log?
     */
    public void setIsIncludeLog (boolean isIncludeLog)
    {
        this.p_isIncludeLog = isIncludeLog;
    } // setIsIncludeLog


    /**************************************************************************
     * Set the log to include into the message. <BR/>
     *
     * @param log    the log to set
     */
    public void setLog (Log_01 log)
    {
        this.p_log = log;
    } // setLog


    /**************************************************************************
     * Set the values to include into the message. <BR/>
     *
     * @param values     the vector containing the values
     */
    public void setValues (Vector<ValueDataElement> values)
    {
        this.p_values = values;
    } // setValues


    /**************************************************************************
     * Get the basic message values. <BR/>
     *
     * @return  The vector containing the values.
     */
    public Vector<ValueDataElement> getValues ()
    {
        return this.p_values;
    } // getValues


    /**************************************************************************
     * Add a return value to be included in the response. <BR/>
     *
     * @param value        the value to be added
     */
    public void addValue (ValueDataElement value)
    {
        // do we need to initialize the values vector?
        if (this.p_values == null)
        {
            this.p_values = new Vector<ValueDataElement> ();
        } // if (this.p_values == null)
        this.p_values.add (value);
    } // addValue


    /**************************************************************************
     * Add a return value to be included in the response. <BR/>
     *
     * @param fieldname        the fieldname of the value
     * @param value            the value
     */
    public void addValue (String fieldname, String value)
    {
        this.addValue (new ValueDataElement (fieldname, value));
    } // addValue


    /**************************************************************************
     * Add a multipart element. In case the response has not been marked
     * as a multipart response the p_isMultipartResponse option will be
     * set true and the element vectors will be initialized. <BR/>
     *
     * @param responseType      the type of the response
     * @param errorCode         the error code if applicable
     * @param errorMessage      the error message if applicable
     * @param oidReference      the string of an oid reference if applicable
     * @param valueDataElements a Vector that can hold value data elements
     */
    public void addMultipart (int responseType, String errorCode,
                              String errorMessage, String oidReference,
                              Vector<ValueDataElement> valueDataElements)
    {
        // check if the response is already marked as multipart
        if (!this.p_isMultipartResponse)
        {
            // automaticall switch to multipart message in case any multipart
            // is added
            this.setIsMultipartResponse (true);
            // initialize the vectors
            this.p_responseElements = new Vector<MultipartElement> ();
            this.p_errorElements = new Vector<MultipartElement> ();
            this.p_warningElements = new Vector<MultipartElement> ();
        } // if (! this.p_isMultipartResponse)

        // create the multipart element
        MultipartElement multipartElement = new MultipartElement ();
        multipartElement.p_responseType = responseType;
        multipartElement.p_errorCode = errorCode;
        multipartElement.p_errorMessage = errorMessage;
        multipartElement.p_objectReference = oidReference;
        multipartElement.p_values = valueDataElements;

        // add the multipart element to the vector of all elements
        this.p_responseElements.add (multipartElement);

        // check if the response element must be added to the error or warning
        // vectors
        switch (responseType)
        {
            case DIConstants.RESPONSE_ERROR:
                this.p_errorElements.add (multipartElement);
                // in case any error occurred in a part of the response
                // the whole response must report an error
                this.p_responseType = DIConstants.RESPONSE_ERROR;
                // add the error message
                this.p_errorMessage += errorMessage + "; ";
                break;
            case DIConstants.RESPONSE_WARNING:
                this.p_warningElements.add (multipartElement);
                // check if the response is already an error response
                if (this.p_responseType != DIConstants.RESPONSE_ERROR)
                {
                    this.p_responseType = DIConstants.RESPONSE_WARNING;
                } // if
                // add the warning message
                this.p_errorMessage += errorMessage + "; ";
                break;
            default:
                // nothing to do
        } // switch (responseType)
    } // addMultipart


    /**************************************************************************
     * Check if the response contains an error message for an object and
     * returns the error message if found. <BR/>
     *
     * @param oidStr        the oid string of the object
     *
     * @return  the error message if found or null otherwise
     */
    public String getMultipartError (String oidStr)
    {
        MultipartElement multipartElement;

        // search the entry in the error vector
        multipartElement = this.searchMultipart (oidStr, this.p_errorElements);
        // has the element been found
        if (multipartElement != null)
        {
            return multipartElement.p_errorMessage;
        } // if

        // not found
        return null;
    } // getMultipartError


    /**************************************************************************
     * Check if the response contains an warning message for an object and
     * returns the warning message if found. <BR/>
     *
     * @param oidStr        the oid string of the object
     *
     * @return  the error message if found or null otherwise
     */
    public String getMultipartWarning (String oidStr)
    {
        MultipartElement multipartElement;

        // search the entry in the warning vector
        multipartElement = this.searchMultipart (oidStr, this.p_warningElements);
        // has the element been found
        if (multipartElement != null)
        {
            return multipartElement.p_errorMessage;
        } // if

        // not found
        return null;
    } // getMultipartWarning


    /**************************************************************************
     * Read the value of the field with the given fieldname of a
     * multipart element with a given object reference oid string. <BR/>
     *
     * @param oidStr        the oid string of the object reference
     * @param fieldname     the name of the field to read the value from
     *
     * @return  the value if found or null otherwise
     */
    public String getValue (String oidStr, String fieldname)
    {
        // do we have a multipart message?
        if (this.p_isMultipartResponse)
        {
            // do we have the multipart element already in the cache?
            if (this.p_multipartCache == null ||
                    (!this.p_multipartCache.p_objectReference.equals (oidStr)))
            {
                // get the multipart entry
                this.searchMultipart (oidStr);
            } // if (this.p_multipartCache != null && ...
            // did we found the entry?
            if (this.p_multipartCache != null)
            {
                // get and return the value
                return this.p_multipartCache.getValue (fieldname);
            } // if (this.p_multipartCache != null)

            // did not find the multipart element:
            return null;
        } // if (this.p_isMultipartResponse)

        // a non-multipart message does not contain any values:
        return null;
    } // getValue


    /**************************************************************************
      * Searches a multipart element and sets in in the cache. <BR/>
      *
      * @param oidStr        the oid string of the object reference
      *
      * @return  true if the multipart element could have been found or false
      *          otherwise
      */
    public boolean searchMultipart (String oidStr)
    {
        this.p_multipartCache =
            this.searchMultipart (oidStr, this.p_responseElements);
        // return if the multipart element has been found
        return this.p_multipartCache != null;
    } // searchMultipart


    /**************************************************************************
     * Check if the response contains an error message for an object. <BR/>
     *
     * @param oidStr        the oid string of the object
     * @param elements      the vector that holds the elements
     *
     * @return  the MultipartElement if found or null otherwise
     */
    private MultipartElement searchMultipart (String oidStr,
                                              Vector<MultipartElement> elements)
    {
        MultipartElement multipartElement;

        // do we have an errorElement vector?
        if (elements != null)
        {
            for (int i = 0; i < elements.size (); i++)
            {
                multipartElement = elements.elementAt (i);
                // does the vector contain the object?
                if (multipartElement.p_objectReference.equals (oidStr))
                {
                    return multipartElement;
                } // if
            } // for (int i = 0; i < this.p_errorElements.size () && (! isError); i++)
        } // if (this.p_errorElements != null)
        // not found
        return null;
    } // searchMultipart


    /**************************************************************************
     * Parse a response string and read the content into the response. <BR/>
     *
     * @param   responseStr A string containing a response.
     */
    public void parseResponse (String responseStr)
    {
        NodeList returnNodes;
        Document root;

        try
        {
            // check if we got a response string
            if (responseStr != null && responseStr.length () > 0)
            {
                // read the document:
                // do not validate the document. This speeds up the parsing
//                root = new XMLReader (new StringReader (responseStr), false, errorHandler).getDocument ();
                root = new XMLReader (new StringReader (responseStr), false, null).getDocument ();

                // get all <RETURN> nodes but the result should be exactly one node!
                returnNodes = root.getElementsByTagName (DIConstants.ELEM_RETURN);
                // check if we exactly got one return node
                if (returnNodes.getLength () == 1)
                {
                    // now parse the rest of the structure
                    this.parseResponse (returnNodes.item (0));
                } // if (returnNodes.getLength () == 1)
                else    // more than one return node found
                {
                    System.out.println ("Response.parseResponse: More that one RETURN node found in response!");
                } // else more than one return node found
            } // if (response.length () > 0)
        } // try
        catch (XMLReaderException e)
        {
            // TOOD: Where to write the exception?
            System.out.println (e.getMessage ());
        } // catch (SAXException e)
    } // parseResponse


    /**************************************************************************
     * Parses a response XML structure that has this structure: <BR/>
     * <pre>
     * &lt;RETURN>
     *      &lt;TYPE>...&lt;/TYPE>
     *      &lt;CODE>...&lt;/CODE>
     *      &lt;MESSAGE>...&lt;/MESSAGE>
     *      &lt;ID>...&lt;/ID>
     *      &lt;VALUES>
     *         &lt;VALUE FIELD="...">...&lt;/VALUE>
     *         &lt;VALUE FIELD="...">...&lt;/VALUE>
     *      &lt;/VALUES>
     * &lt;/RETURN>
     * </pre>
     *
     * or in case of a multipart message:
     * <pre>
     * &lt;RETURN>
     *   &lt;LINE>
     *      &lt;TYPE>...&lt;/TYPE>
     *      &lt;CODE>...&lt;/CODE>
     *      &lt;MESSAGE>...&lt;/MESSAGE>
     *      &lt;ID>...&lt;/ID>
     *      &lt;VALUES>
     *         &lt;VALUE FIELD="...">...&lt;/VALUE>
     *         &lt;VALUE FIELD="...">...&lt;/VALUE>
     *      &lt;/VALUES>
     *   &lt;/LINE>
     *   &lt;LINE>...&lt;/LINE>
     * &lt;/RETURN>
     * </pre>
     *
     * @param rootNode    a &lt;RETURN> node that should contain the XML
     *              structure described above
     */
    public void parseResponse (Node rootNode)
    {
        NodeList returnValueNodes;
        NodeList lineNodes;
        Node lineNode;
        String nodeName;
        String nodeValue = "";
        Text text;
        int responseType = DIConstants.RESPONSE_UNKNOWN;
        String errorMessage = "";
        String errorCode = "";
        String objectReference = "";
        Vector<ValueDataElement> valueDataElements = null;
        Node node = rootNode;

        returnValueNodes = node.getChildNodes ();
        // check if any nodes could be found
        if (returnValueNodes != null)
        {
            // loop though the list and ignore whitespaces
            for (int i = 0; i < returnValueNodes.getLength (); i++)
            {
                node = returnValueNodes.item (i);
                if (node.getNodeType () == Node.ELEMENT_NODE)
                {
                    // get the node name
                    nodeName = node.getNodeName ();
                    // check if the node is a <LINE> tag
                    // this indicates a multipart response
                    // Note that is an e&i specific extension
                    if (nodeName.equalsIgnoreCase (Response.RESPONSETAG_LINE))
                    {
                        lineNodes = node.getChildNodes ();
                        // init the values for the multipart response
                        responseType = DIConstants.RESPONSE_SUCCESS;
                        errorCode = "";
                        errorMessage = "";
                        objectReference = "";
                        valueDataElements = null;
                        // loop through the list and ignore whitespaces
                        for (int j = 0; j < lineNodes.getLength (); j++)
                        {
                            lineNode = lineNodes.item (j);
                            if (lineNode.getNodeType () == Node.ELEMENT_NODE)
                            {
                                nodeName = lineNode.getNodeName ();
                                if (!nodeName.equalsIgnoreCase (Response.RESPONSETAG_VALUES))
                                {
                                    // get the node value
                                    text = (Text) lineNode.getFirstChild ();
                                    if (text != null)
                                    {
                                        nodeValue = text.getNodeValue ();
                                    } // if
                                    else
                                    {
                                        nodeValue = "";
                                    } // else
                                } // if (!nodeName.equalsIgnoreCase (RESPONSETAG_VALUES))
                                // determine tag
                                if (nodeName.equalsIgnoreCase (Response.RESPONSETAG_TYPE))
                                {
                                    responseType = this.getResponseType (nodeValue);
                                } // if
                                else if (nodeName.equalsIgnoreCase (Response.RESPONSETAG_CODE))
                                {
                                    errorCode = nodeValue;
                                } // else if
                                else if (nodeName.equalsIgnoreCase (Response.RESPONSETAG_MESSAGE))
                                {
                                    errorMessage = nodeValue;
                                } // else if
                                else if (nodeName.equalsIgnoreCase (Response.RESPONSETAG_ID))
                                {
                                    objectReference = nodeValue;
                                } // else if
                                else if (nodeName.equalsIgnoreCase (Response.RESPONSETAG_VALUES))
                                {
                                    valueDataElements = this.parseValues (lineNode);
                                } // else if
                            } // if (node.getNodeType () == Node.ELEMENT_NODE)
                        } // for (int j = 0; j < lineNodes.getLength (); j++)
                        // create the multipart entry
                        this.addMultipart (responseType, errorCode, errorMessage,
                                objectReference, valueDataElements);
                    } // if (nodeName.equalsIgnoreCase("LINE"))
                    else    // a basic message
                    {
                        // get the node value
                        nodeValue = "";
                        text = (Text) node.getFirstChild ();
                        if (text != null)
                        {
                            nodeValue = text.getNodeValue ();
                        } // if
                        // set the response values
                        // Note that LOG_NO and LOG_MSG_NO are ignored
                        if (nodeName.equalsIgnoreCase (Response.RESPONSETAG_TYPE))
                        {
                            // set the response type
                            this.setResponseType (this.getResponseType (nodeValue));
                        } // if (nodeName.equals ("TYPE"))
                        else if (nodeName.equalsIgnoreCase (Response.RESPONSETAG_CODE))
                        {
                            // set the error code
                            this.setErrorCode (nodeValue);
                        } // else if (nodeName.equalsIgnoreCase ("CODE"))
                        else if (nodeName.equalsIgnoreCase (Response.RESPONSETAG_MESSAGE))
                        {
                            // set the error message
                            this.setErrorMessage (nodeValue);
                        } // else if (nodeName.equalsIgnoreCase ("MESSAGE"))
                        else if (nodeName.equalsIgnoreCase (Response.RESPONSETAG_ID))
                        {
                            // set the object reference
                            this.setObjectReference (nodeValue);
                        } // else if (nodeName.equalsIgnoreCase (RESPONSETAG_ID))
                        else if (nodeName.equalsIgnoreCase (Response.RESPONSETAG_VALUES))
                        {
                            // set the values
                            this.setValues (this.parseValues (node));
                        } // else if (nodeName.equalsIgnoreCase (RESPONSETAG_VALUES))
                    } // if (node.getNodeType () == Node.ELEMENT_NODE)
                } // not a multipart message
            } // for (int i = 0; i < returnValueNodes.getLength (); i++)
        } // if (returnValueNodes != null)
        else    // no <return> nodes found
        {
            // no <RETURN> nodes found
        } // else no return value found
    } // parseResponse


    /**************************************************************************
     * Parse the &lt;VALUES> part of a multipart response. <BR/>
     * This part looks like:
     * <pre>
     *   ...
     *   &lt;VALUES>
     *     &lt;VALUE FIELD="...">...&lt;/VALUE>
     *     &lt;VALUE FIELD="...">...&lt;/VALUE>
     *   &lt;/VALUES>
     *   ...
     * </pre>
     *
     * @param   node        the &lt;VALUES> node
     *
     * @return  a vector with valueDataElements that holds all values found
     */
    private Vector<ValueDataElement> parseValues (Node node)
    {

        NodeList valueNodes;
        Node valueNode;
        Node attrNode;
        NamedNodeMap attributes;
        Text text;
        String nodeName = null;
        String nodeValue = null;
        String fieldName = null;
        Vector<ValueDataElement> returnVector = null;
        ValueDataElement valueDataElement = null;

        valueNodes = node.getChildNodes ();
        // check if any nodes could be found
        if (valueNodes != null)
        {
            returnVector = new Vector<ValueDataElement> ();
            // loop though the list and ignore whitespaces
            for (int i = 0; i < valueNodes.getLength (); i++)
            {
                valueNode = valueNodes.item (i);
                if (valueNode.getNodeType () == Node.ELEMENT_NODE)
                {
                    // get the node name
                    nodeName = valueNode.getNodeName ();
                    // check if it is a <VALUE> node
                    if (nodeName.equals (Response.RESPONSETAG_VALUE))
                    {
                        // get the node value
                        text = (Text) valueNode.getFirstChild ();
                        if (text != null)
                        {
                            nodeValue = text.getNodeValue ();
                        } // if
                        // get the <VALUE FIELD="..."> attribute
                        fieldName = null;
                        attributes = valueNode.getAttributes ();
                        if (attributes != null)
                        {
                            attrNode = attributes.getNamedItem (Response.RESPONSEATTR_FIELD);
                            if (attrNode != null)
                            {
                                fieldName = attrNode.getNodeValue ();
                            } // if
                        } // if (attributes != null)
                        // did we get a valid fieldname
                        if (fieldName != null)
                        {
                            // create a new ValueDataElement instance
                            valueDataElement = new ValueDataElement ();
                            valueDataElement.field = fieldName;
                            valueDataElement.value = nodeValue;
                            // and add it to the result vector
                            returnVector.addElement (valueDataElement);
                        } // if (fieldName != null)
                    } // if (nodeName.equals (DIConstants.ELEM_VALUE))
                } // if (valueNode.getNodeType () == Node.ELEMENT_NODE)
            } // for (int i = 0; i < valueNodes.getLength (); i++)
        } // if (returnValueNodes != null)
        else    // no <VALUES> node found
        {
            // no <VALUES> node found
        } // else no return value found
        // set the return vector
        return returnVector;
    } // parseValues


    /**************************************************************************
     * Creates a string representation of the response. Used for debugging. <BR/>
     *
     * @return  The generated string.
     */
    public String toString ()
    {
        String str = "";
        ValueDataElement value = null;

        str += "FUNCTIONCODE: " + this.p_functionCode + "\r\n";
        str += "FUNCTIONNAME: " + this.p_functionName + "\r\n";
        str += "TYPE: " + this.p_responseType + "\r\n";
        str += "CODE: " + this.p_errorCode + "\r\n";
        str += "MESSAGE: " + this.p_errorMessage + "\r\n";
        str += "IS MULTIPART: " + this.p_isMultipartResponse + "\r\n";
        str += "=======================================\r\n";

        // check if we have a multipart message
        if (this.p_responseElements != null && this.p_responseElements.size () > 0)
        {
            MultipartElement multipartElement;
            for (int i = 0; i < this.p_responseElements.size (); i++)
            {
                multipartElement = this.p_responseElements.elementAt (i);
                str += "TYPE: " + multipartElement.p_responseType + "\r\n";
                str += "CODE: " + multipartElement.p_errorCode + "\r\n";
                str += "MESSAGE: " + multipartElement.p_errorMessage + "\r\n";
                str += "ID: " + multipartElement.p_objectReference + "\r\n";
                if (multipartElement.p_values != null)
                {
                    for (int j = 0; j < multipartElement.p_values.size (); j++)
                    {
                        value =  multipartElement.p_values.elementAt (j);
                        str += "FIELD '" + value.field + "' : " + value.value + "\r\n";
                    } // for (int j = 0; j < multipartElement.p_values.size (); j++)
                } // if (multipartElement.p_values != null)
                str += "---------------------------------------\r\n";
            } // for (int i = 0; i < this.p_responseElements.size (); i++)
        } // if (this.p_responseElements != null && this.p_responseElements.size () > 0)
        return str;
    } // toString

    /**************************************************************************
     * Generates an XML response with the given parameters and adds the log
     * in case the isDisplayLog has been activated. The response will be
     * returned in a string. <BR/>
     *
     * @param   functionCode    The code of the function.
     * @param   functionName    The name/description of the function.
     * @param   responseType    The type of the generated response.
     * @param   errorCode       The error code (if applicable).
     * @param   errorMessage    The errorMessage (if applicable).
     * @param   objectReference the object reference
     * @param   values            any values to set in th response
     * @param   log             The log object to which to add log messages.
     *
     * @return  The XML response in a string.
     */
    public String generate (int functionCode, String functionName,
                            int responseType, String errorCode,
                            String errorMessage, String objectReference,
                            Vector<ValueDataElement> values,
                            Log_01 log)
    {
        this.setObjectReference (objectReference);
        this.setValues (values);

        return this.generate (functionCode, functionName, responseType,
            errorCode, errorMessage, log);
    } // generate


    /**************************************************************************
     * Generates an XML response with the given parameters and adds the log
     * in case the isDisplayLog has been activated. The response will be
     * returned in a string. <BR/>
     *
     * @param   functionCode    The code of the function.
     * @param   functionName    The name/description of the function.
     * @param   responseType    The type of the generated response.
     * @param   errorCode       The error code (if applicable).
     * @param   errorMessage    The errorMessage (if applicable).
     * @param   log             The log object to which to add log messages.
     *
     * @return  The XML response in a string.
     */
    public String generate (int functionCode, String functionName,
                            int responseType, String errorCode,
                            String errorMessage, Log_01 log)
    {
        // set the parameters in the response
        this.setFunctionCode (functionCode);
        this.setFunctionName (functionName);
        this.setResponseType (responseType);
        this.setErrorCode (errorCode);
        this.setErrorMessage (errorMessage);
        this.setLog (log);

        return this.generate ();
    } // generate

    /**************************************************************************
     * Generates an XML response with the given parameters and adds the log
     * in case the isDisplayLog has been activated. The response will be
     * returned in a string. <BR/>
     *
     * @return  The XML response in a string.
     */
    public String generate ()
    {
        Document responseDoc = null;

        // create a new DOM root
        try
        {
            responseDoc = XMLWriter.createDocument ();
        } // try
        catch (XMLWriterException e)
        {
            return e.toString ();
        } // catch

        // create the root <RESPONSE>
        // <m2:RESPONSE xmlns:m2="urn:www.trinitec.at:m2" VERSION="1.0">
        Element responseElem =
                responseDoc.createElementNS (Response.NAMESPACE_URI, Response.NAMESPACE_QUALIFIEDNAME);
        // add the VERSION="1.0" attribute
        responseElem.setAttribute (Response.ATTR_VERSION, Response.VERSION);
        // add the namespace declaration
        responseElem.setAttribute (Response.ATTR_XMLNS, Response.XMLNS);
        // add the head to the xml document
        responseDoc.appendChild (responseElem);

        // generate the response content
        if (this.generate (responseDoc, responseElem))
        {
            // now serialize the xml document into a string;
            CharArrayWriter charArrayWriter = new CharArrayWriter ();
            DOMHandler.serializeDOM (responseDoc, charArrayWriter, true, null);
            // close the writer:
            charArrayWriter.close ();
            // return the response:
            return charArrayWriter.toString ();
        } // if (responseContentNode != null)

        // got no content
        // return null to indicate an error:
        return null;
    } // generate



    /**************************************************************************
     * Generates an XML response with the given parameters and adds the log
     * in case the isDisplayLog has been activated. The response will be
     * returned in a string. <BR/>
     * <BR/>
     * The basic response structure:<BR/>
     *<pre>
     * &lt;ACTION>
     *      &lt;CODE>...&lt;/CODE>
     *      &lt;NAME>...&lt;/NAME>
     * &lt;/ACTION>
     * &lt;LOG>...&lt;/LOG>
     * &lt;RETURN>
     *      &lt;TYPE>...&lt;/TYPE>
     *      &lt;CODE>...&lt;/CODE>
     *      &lt;MESSAGE>...&lt;/MESSAGE>
     *      &lt;ID>...&lt;/ID>
     *      &lt;VALUES>
     *         &lt;VALUE FIELD="...">...&lt;/VALUE>
     *         &lt;VALUE FIELD="...">...&lt;/VALUE>
     *      &lt;/VALUES>
     * &lt;/RETURN>
     * </pre>
     *
     * <BR/>
     * The multipart response structure:<BR/>
     * <pre>
     * &lt;ACTION>
     *      &lt;CODE>...&lt;/CODE>
     *      &lt;NAME>...&lt;/NAME>
     * &lt;/ACTION>
     * &lt;LOG>...&lt;/LOG>
     * &lt;RETURN>
     *   &lt;LINE>
     *      &lt;TYPE>...&lt;/TYPE>
     *      &lt;CODE>...&lt;/CODE>
     *      &lt;MESSAGE>...&lt;/MESSAGE>
     *      &lt;ID>...&lt;/ID>
     *      &lt;VALUES>
     *         &lt;VALUE FIELD="...">...&lt;/VALUE>
     *         &lt;VALUE FIELD="...">...&lt;/VALUE>
     *      &lt;/VALUES>
     *   &lt;/LINE>
     *   &lt;LINE>...&lt;/LINE>
     * &lt;/RETURN>
     * </pre>
     *
     * @param   responseDoc     The response document for which to generate.
     * @param   responseElem    The response element to be generated.
     *
     * @return  The XML response in a string.
     */
    public boolean generate (Document responseDoc, Element responseElem)
    {
        ValueDataElement value;
        Element valueElem;

        // should the action description be added
        if (this.getFunctionCode ()  > 0)
        {
            // <ACTION>
            //      <CODE>...</CODE>
            //      <NAME>...</NAME>
            // </ACTION>
            Element actionElem = responseDoc.createElement (Response.RESPONSETAG_ACTION);
            responseElem.appendChild (actionElem);
            // <CODE>
            Element actionCodeElem = responseDoc.createElement (Response.RESPONSETAG_CODE);
            actionCodeElem.appendChild (responseDoc.createTextNode (
                    "" + this.getFunctionCode ()));
            actionElem.appendChild (actionCodeElem);
            // <NAME>
            Element actionNameElem = responseDoc.createElement (Response.RESPONSETAG_NAME);
            actionNameElem.appendChild (responseDoc.createTextNode (
                this.getFunctionName ()));
            actionElem.appendChild (actionNameElem);
        } // if (functionCode != null && functionCode > 0)

        // should the log be included?
        if (this.p_log != null && this.p_isIncludeLog)
        {
            // <LOG>
            //     ...
            // </LOG>
            Element logElem = responseDoc.createElement (Response.RESPONSETAG_LOG);
            // add the log as text
            logElem.appendChild (responseDoc.createTextNode (
                    this.p_log.toString ()));
            responseElem.appendChild (logElem);
        } // if (this.p_isIncludeLog)

        // the
        // <RETURN>
        //       ...
        // </RETURN>
        // section contains the return messags. Can be multipart or basic
        Element returnElem = responseDoc.createElement (Response.RESPONSETAG_RETURN);
        responseElem.appendChild (returnElem);

        // check if we got a multipart message
        if (this.getIsMultipartResponse () && this.p_responseElements != null &&
            this.p_responseElements.size () > 0)
        {
            // add the following structure for every multipart element
            // <RETURN>
            //    <LINE>
            //       <TYPE>...</TYPE>
            //       <CODE>...</CODE>
            //       <MESSAGE>...</MESSAGE>
            //       <ID>...</ID>
            //       <VALUES>
            //          <VALUE FIELD="...">...</VALUE>
            //          ...
            //       </VALUES>
            // </RETURN>
            // <TYPE>
            this.generateMultipart (responseDoc, returnElem);
        } // if (getIsMultipartResponse () && this.p_responseElements != null ...
        else    // basic response
        {
            // add the structure for a basic response
            // <RETURN>
            //    <TYPE>...</TYPE>
            //    <CODE>...</CODE>
            //    <MESSAGE>...</MESSAGE>
            //    <ID>...</ID>
            //      <VALUES>
            //         <VALUE FIELD="...">...</VALUE>
            //         <VALUE FIELD="...">...</VALUE>
            //      </VALUES>
            // </RETURN>
            // <TYPE>
            Element typeElem = responseDoc.createElement (Response.RESPONSETAG_TYPE);
            typeElem.appendChild (responseDoc.createTextNode (
                this.getResponseTypeStr (this.getResponseType ())));
            returnElem.appendChild (typeElem);
            // <CODE>
            Element codeElem = responseDoc.createElement (Response.RESPONSETAG_CODE);
            codeElem.appendChild (responseDoc.createTextNode (this.getErrorCode ()));
            returnElem.appendChild (codeElem);
            // <MESSAGE>
            Element messageElem = responseDoc.createElement (Response.RESPONSETAG_MESSAGE);
            messageElem.appendChild (responseDoc.createTextNode (this.getErrorMessage ()));
            returnElem.appendChild (messageElem);
            // <ID>
            if (this.p_objectReference != null && !this.p_objectReference.isEmpty ())
            {
                Element idElem = responseDoc.createElement (Response.RESPONSETAG_ID);
                idElem.appendChild (responseDoc.createTextNode (this.getObjectReference ()));
                returnElem.appendChild (idElem);
            } // if (this.p_objectReference != null)
            // <VALUES>
            if (this.p_values != null && this.p_values.size () > 0)
            {
                Element valuesElem = responseDoc.createElement (Response.RESPONSETAG_VALUES);
                returnElem.appendChild (valuesElem);
                for (Iterator<ValueDataElement> values = this.p_values.iterator ();
                         values.hasNext ();)
                {
                    value = values.next ();
                    // <VALUE FIELD="...">...</VALUE>
                    valueElem = responseDoc.createElement (Response.RESPONSETAG_VALUE);
                    valueElem.setAttribute (Response.RESPONSEATTR_FIELD, value.field);
                    valueElem.appendChild (responseDoc.createTextNode (value.value));
                    valuesElem.appendChild (valueElem);
                } // for (Iterator<ValueDataElement> values ...
            } // if (this.p_values != null && this.p_values.size() > 0)
        } // else basic response
        // return true to indicate success
        return true;
    } // generate


    /**************************************************************************
     * Generates the XML response structure for multipart messages:<BR/>
     * <pre>
     * &lt;LINE>
     *    &lt;TYPE>...&lt;/TYPE>
     *    &lt;CODE>...&lt;/CODE>
     *    &lt;MESSAGE>...&lt;/MESSAGE>
     *    &lt;ID>...&lt;/ID>
     *    &lt;VALUES>
     *       &lt;VALUE FIELD="...">...&lt;/VALUE>
     *       &lt;VALUE FIELD="...">...&lt;/VALUE>
     *    &lt;/VALUES>
     * &lt;/LINE>
     * &lt;LINE>...&lt;/LINE>
     * </pre>
     *
     * @param   responseDoc The target document for which to create the structure.
     * @param   returnElem  The element for which to create the structure.
     */
    protected void generateMultipart (Document responseDoc, Element returnElem)
    {
        MultipartElement multipart;
        Element lineElem;
        Element typeElem;
        Element codeElem;
        Element messageElem;
        Element idElem;
        Element valuesElem;
        Element valueElem;
        ValueDataElement value;

        // any elements available?
        if (this.p_responseElements != null && this.p_responseElements.size () > 0)
        {
            for (Iterator<MultipartElement> iter = this.p_responseElements.iterator ();
                   iter.hasNext ();)
            {
                multipart = iter.next ();

                lineElem  = responseDoc.createElement (Response.RESPONSETAG_LINE);
                returnElem.appendChild (lineElem);

                typeElem = responseDoc.createElement (Response.RESPONSETAG_TYPE);
                typeElem.appendChild (responseDoc.createTextNode (
                    this.getResponseTypeStr (multipart.p_responseType)));
                lineElem.appendChild (typeElem);
                // <CODE>
                codeElem = responseDoc.createElement (Response.RESPONSETAG_CODE);
                codeElem.appendChild (responseDoc.createTextNode (multipart.p_errorCode));
                lineElem.appendChild (codeElem);
                // <MESSAGE>
                messageElem = responseDoc.createElement (Response.RESPONSETAG_MESSAGE);
                messageElem.appendChild (responseDoc.createTextNode (multipart.p_errorMessage));
                lineElem.appendChild (messageElem);
                // <ID>
                idElem = responseDoc.createElement (Response.RESPONSETAG_ID);
                idElem.appendChild (responseDoc.createTextNode (multipart.p_objectReference));
                lineElem.appendChild (idElem);
                // <VALUES> if applicable
                if (multipart.p_values != null && multipart.p_values.size () > 0)
                {
                    valuesElem = responseDoc.createElement (Response.RESPONSETAG_VALUES);
                    lineElem.appendChild (valuesElem);
                    for (Iterator<ValueDataElement> values = multipart.p_values.iterator ();
                             values.hasNext ();)
                    {
                        value = values.next ();
                        // <VALUE FIELD="...">...</VALUE>
                        valueElem = responseDoc.createElement (Response.RESPONSETAG_VALUE);
                        valueElem.setAttribute (Response.RESPONSEATTR_FIELD, value.field);
                        valueElem.appendChild (responseDoc.createTextNode (value.value));
                        valuesElem.appendChild (valueElem);
                    } // for (Iterator<ValueDataElement> values ...
                } // if (multipart.p_values != null && multipart.p_values.size () > 0)
            } // for (Iterator<MultipartElement> iter ...
        } // if (this.p_responseElements != null && this.p_responseElements.size() > 0)

    } // generateMultipart

    /**************************************************************************
     * Get the response type code from a given response type string. <BR/>
     *
     * @param   responseTypeStr     the string holding response type
     *
     * @return the response type code
     */
    private int getResponseType (String responseTypeStr)
    {
        // determine the response type
        if (responseTypeStr.equalsIgnoreCase (DIConstants.RESPONSESTR_SUCCESS))
        {
            return DIConstants.RESPONSE_SUCCESS;
        } // if
        else if (responseTypeStr.equalsIgnoreCase (DIConstants.RESPONSESTR_ERROR))
        {
            return DIConstants.RESPONSE_ERROR;
        } // else if
        else if (responseTypeStr.equalsIgnoreCase (DIConstants.RESPONSESTR_WARNING))
        {
            return DIConstants.RESPONSE_WARNING;
        } // else if
        else if (responseTypeStr.equalsIgnoreCase (DIConstants.RESPONSESTR_INFO))
        {
            return DIConstants.RESPONSE_INFO;
        } // else if
        else if (responseTypeStr.equalsIgnoreCase (DIConstants.RESPONSESTR_DEBUG))
        {
            return DIConstants.RESPONSE_DEBUG;
        } // else if
        else
        {
            return DIConstants.RESPONSE_UNKNOWN;
        } // else
    } // getResponseType


    /**************************************************************************
     * Get the response type string from a given response type code. <BR/>
     *
     * @param   responseType    the response type code
     *
     * @return the response type string
     */
    public String getResponseTypeStr (int responseType)
    {
        switch (responseType)
        {
            case DIConstants.RESPONSE_SUCCESS:
                return DIConstants.RESPONSESTR_SUCCESS;
            case DIConstants.RESPONSE_ERROR:
                return DIConstants.RESPONSESTR_ERROR;
            case DIConstants.RESPONSE_WARNING:
                return DIConstants.RESPONSESTR_WARNING;
            case DIConstants.RESPONSE_INFO:
                return DIConstants.RESPONSESTR_INFO;
            case DIConstants.RESPONSE_DEBUG:
                return DIConstants.RESPONSESTR_DEBUG;
            default:
                return DIConstants.RESPONSESTR_UNKNOWN;
        } // switch (responseType)
    } // getResponseTypeStr

} // class Response
