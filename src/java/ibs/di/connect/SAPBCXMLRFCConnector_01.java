/*
 * Class: SAPBCXMLRFCConnector_01.java
 */

// package:
package ibs.di.connect;

// imports:
//TODO: unsauber
import ibs.bo.Datatypes;
//TODO: unsauber
import ibs.bo.OID;
import ibs.di.DIArguments;
import ibs.di.DIConstants;
import ibs.di.DIErrorHandler;
import ibs.di.DIMessages;
import ibs.di.DITokens;
import ibs.di.Log_01;
import ibs.di.connect.ConnectionFailedException;
import ibs.di.connect.Connector_01;
//TODO: unsauber
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
//TODO: unsauber
import ibs.service.user.User;
import ibs.tech.html.TableElement;
import ibs.tech.xml.XMLReader;
import ibs.tech.xml.XMLReaderException;
import ibs.util.file.FileHelpers;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;


/******************************************************************************
 * The SAPBCXMLRFCConnector_01 Class writes IDOC XML files
 * to an SAP Business Connector server.<BR/>
 * This is an exclusive export connector.<BR/>
 *
 * @version     $Id: SAPBCXMLRFCConnector_01.java,v 1.19 2010/04/07 13:37:05 rburgermann Exp $
 *
 * @author      Bernd Buchegger (BB), 000628
 ******************************************************************************
 */
public class SAPBCXMLRFCConnector_01 extends Connector_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: SAPBCXMLRFCConnector_01.java,v 1.19 2010/04/07 13:37:05 rburgermann Exp $";


    /**
     *  URL of the SAP Business Connector server.<BR/>
     */
    public String p_sapBCGatewayUrl  = null;

    /**
     *  Name of the R/3 server.<BR/>
     */
    public String p_repServerName  = null;

    /**
     *  Possible values for the p_envelope property.<BR/>
     */
    private String [] envelopeTypes  = {"RFC-XML", "bXML"};

    /**
     *  Envelope used for the RFC-XML document.<BR/>
     *  The Business Connector Server supports its own RFC-XML envelope and
     *  the BIZTALK envelope.<BR/>
     */
    public String p_envelope  = this.envelopeTypes [0];


    /**************************************************************************
     * Creates a SAPBCXMLRFCConnector_01 Object. <BR/>
     */
    public SAPBCXMLRFCConnector_01 ()
    {
        // call constructor of super class Connector_01:
        super ();
    } // SAPBCXMLRFCConnector_01


    /**************************************************************************
     * Creates a SAPBCXMLRFCConnector_01 Object. <BR/>
     *
     * @param   oid     oid of the object.
     * @param   user    user that created the object.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public SAPBCXMLRFCConnector_01 (OID oid, User user)
    {
        // call constructor of super class Connector_01:
        super (oid, user);
    } // SAPBCXMLRFCConnector_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set class specifics of super class:
        super.initClassSpecifics ();

        // set class specifics:
        this.connectorType = DIConstants.CONNECTORTYPE_SAPBCXMLRFC;

        // an HTTP connector can only be used for export:
        this.isImportEnabled = false;
        this.isImportConnector = false;
    } // initClassSpecifics


    /**************************************************************************
     * Sets the arguments to class specific properties. <BR/>
     */
    public void setArguments ()
    {
        this.p_sapBCGatewayUrl  = this.arg1;
        this.p_repServerName    = this.arg2;
        this.p_envelope         = this.arg3;
    } // setArguments


    /**************************************************************************
     * Initializes the connector and checks the settings. <BR/>
     *
     * @throws  ConnectionFailedException
     *          The connection could not be established.
     */
    public void initConnector ()
        throws ConnectionFailedException
    {
        // check first if we have a directory set where we can copy the files
        // if not create a temp directory
        if (this.isCreateTemp &&
            (this.getPath () == null || this.getPath ().length () == 0))
        {
            this.setPath (this.createTempDir ());
        } // if
        try
        {
            // check URL
            if (!this.p_sapBCGatewayUrl.toLowerCase ().startsWith (IOConstants.URL_HTTP_LC))
            {
                this.p_sapBCGatewayUrl = IOConstants.URL_HTTP + this.p_sapBCGatewayUrl;
            } // if
            // check closing slash
            this.p_sapBCGatewayUrl = FileHelpers.addEndingURLSeparator (this.p_sapBCGatewayUrl);
            URL  url = new URL (this.p_sapBCGatewayUrl);
            // check the protocol
            if (!url.getProtocol ().equalsIgnoreCase (DIConstants.HTTP_PROTOCOL))
            {
                throw new ConnectionFailedException ( 
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_PROTOCOL_NOT_SUPPORTED, env));
            } // if (! url.getProtocol().equalsIgnoreCase (DIConstants.HTTP_PROTOCOL))
            // protocol is ok
            this.p_sapBCGatewayUrl =
                FileHelpers.addEndingURLSeparator (this.p_sapBCGatewayUrl);
        } // try
        catch (IOException e)
        {
            throw new ConnectionFailedException ( 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_SAPBC_CONNECTION_FAILED, env));
        } // catch
    } // initConnector


    /**************************************************************************
     * Initializes the connector for use as backup connector. Will be
     * overwritten in the subclasses to meet the specific need of the various
     * connectors.<BR/>
     * This method must always call {@link #initConnector() initConnector}.
     *
     * @param   processId   Id of import/export process for which the connector
     *                      is used for backup.
     *
     * @throws  ConnectionFailedException
     *          The connection could not be established
     */
    public void initBackupConnector (String processId)
        throws ConnectionFailedException
    {
        // call the standard initialization:
        super.initBackupConnector (processId);

        // check if the processId is defined:
        if (processId != null && processId.length () > 0)
        {
            // add processId to file prefix:
            if (this.p_filePrefix != null)
            {
                this.p_filePrefix += processId + "_";
            } // if
            else
            {
                this.p_filePrefix = processId + "_";
            } // else
        } // if
    } // initBackupConnector


    /**************************************************************************
     * Closes the connector. This method includes all actions that need to
     * be done to close a connection to a data source or destination
     * and deletes the temp directory if applicable.<BR/>
     */
    public void close ()
    {
        // check if the temp directory should be deleted
        if (this.isDeleteTemp)
        {
            this.deleteTempDir ();
        } // if
    } // close


    /**************************************************************************
     * The dir method reads from the import source and returns all importable
     * objects found in a array of strings. <BR/>
     * This is not support in an SAPBCConnector because it does not
     * support the import mechanism.<BR/>
     *
     * @return  always <CODE>null</CODE> at the moment
     *
     * @exception   ConnectionFailedException
     *              The connection could not be established.
     */
    public String[] dir ()
        throws ConnectionFailedException
    {
        // the SAPBC connector does not yet support a import and dir mechanism
        return null;
    } // dir


    /**************************************************************************
     * Writes the export file to the SAP Business Connnector server.<BR/>
     * We assume that the file has been created in an appropriate IDOC XML
     * format the business connector is able to read.<BR/>
     *
     * @param   fileName    The name of the source file.
     *
     * @exception   ConnectionFailedException
     *              The connection could not be established.
     */
    public void write (String fileName)
        throws ConnectionFailedException
    {
        URL url = null;
        String queryStr = "";
        HttpURLConnection connection = null;
        DataInputStream file = null;
        StringBuffer stringBuffer = null;
        DataOutputStream outputStream = null;
        InputStream responseStream = null;
        String response = "";
        int c;

        // construct the query string to be send to the SAP Business Connector
        // constants
        String invokeUrl = "invoke/sap.demo/handle_RFC_XML_POST";
        String contentType = "text/html";

        try
        {
            // add the name of the R/3 server
            queryStr += "&repServerName=" + IOHelpers.urlEncode (this.p_repServerName);
            // add the envelope type
            queryStr += "&$envelope=" + IOHelpers.urlEncode (this.p_envelope);
            // add the xml file
            // set the input stream to the export file to read from
            file = new DataInputStream (new BufferedInputStream (
                new FileInputStream (this.path + fileName)));
            // read from the file and write to the stream
            stringBuffer = new StringBuffer ();
            while ((c = file.read ()) != -1)
            {
                stringBuffer.append ((char) c);
            } // while

            // close the stream
            file.close ();
            // append the xml data to the query string
            queryStr += "&xmlData=" + IOHelpers.urlEncode (stringBuffer.toString ());

            // create the url to the business connector
            url = new URL (this.p_sapBCGatewayUrl + invokeUrl);

            // open the connection
            connection = (HttpURLConnection) url.openConnection ();
            // set POST as HTTP request method
            connection.setRequestMethod ("POST");
            // set the basic authentication data
/*
            // BB HINT: it is very interesting that the SAP Business Connector
            // does not even require basic authentication!
            String username = IOConstants.USERNAME_ADMINISTRATOR;
            String password = "manage";
            String authenticationData = username + ":" + password;
            BASE64Encoder enc = new BASE64Encoder ();
            authenticationStr = enc.encode(authenticationData.getBytes());
            connection.setRequestProperty("Authorization", "Basic " + authenticationStr);
*/
            // set the connection for input/output
            connection.setDoInput (true);
            connection.setDoOutput (true);
            connection.setAllowUserInteraction (false);
            // set the content type
            connection.setRequestProperty ("Content-type", contentType);
            // set the content length
            connection.setRequestProperty ("Content-length", "" + queryStr.length ());
            // open the connection and send the data
            connection.connect ();

            // set an output stream to the connection we want to write the data to
            outputStream = new DataOutputStream (connection.getOutputStream ());
            // read from the file and write to the stream
            outputStream.writeBytes (queryStr);
            // close the stream
            outputStream.close ();

            // check if we got an Code 200 from the webserver
            // this indicated that the connection was successful
            if (connection.getResponseCode () == HttpURLConnection.HTTP_OK)
            {
                // set the inputStream to read the response of the server
                responseStream = new BufferedInputStream (connection.getInputStream ());
                try
                {
                    // get the response from the connection
                    response = this.getResponse (responseStream);
                    // close the stream
                    responseStream.close ();
                    // close the connection
                    connection.disconnect ();
                    // check if SAP responded that file as ok
                    // the method will throw a ConnectionFailedExcaption in
                    // case the server did not accept the message.
                    this.checkResponse (response);
                } // try
                catch (ConnectionFailedException e)
                {
                    // throw an exception indicating that the
                    // SAP server did not accept the message
                    throw e;
                } // catch
            } // if (connection.getResponseCode () == 200)
            else        // connection failed
            {
                // close the connection
                connection.disconnect ();
                // throw an ConnectionFailedException
                throw new ConnectionFailedException ( 
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_COULD_NOT_INIT_CONNECTOR, env));
            } // else connection failed
        } // try
        catch (MalformedURLException e)
        {
            throw new ConnectionFailedException ( 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_COULD_NOT_INIT_CONNECTOR, env) +
                " (" + e.toString () + ")");
        } // catch
        catch (IOException e)
        {
            throw new ConnectionFailedException ( 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_COULD_NOT_INIT_CONNECTOR, env) +
                " (" + e.toString () + ")");
        } // catch
        catch (Exception e)
        {
            e.printStackTrace (System.out);
            throw new ConnectionFailedException ( 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_COULD_NOT_INIT_CONNECTOR, env) +
                " (" + e.toString () + ")");
        } // catch
        finally
        {
            // disconnect if neccessary
            if (connection != null)
            {
                connection.disconnect ();
            } // if
        } // finally
    } // write


    /**************************************************************************
     * Reads a file from a SAP Business Connector.<BR/>
     *
     * This is not supported by the SAPBCConnector.<BR/>
     *
     * @param   fileName    The name of the file to read.
     *
     * @throws  ConnectionFailedException
     *          The connection could not be established.
     */
    public void read (String fileName)
        throws ConnectionFailedException
    {
        // not supported by a SAPBCConnector !!!
    } // read


    /**************************************************************************
     * Read a file from the SAP Business Connector and copy it to the
     * destination path.<BR/>
     *
     * Note that this feature is not supported by the SAPBCXMLRFCConnector.<BR/>
     *
     * @param fileName              name of the file to read
     * @param destinationPath       the path to write the file to
     * @param destinationFileName   name of the copied file.
     *                              If empty fileName will be used.
     *
     * @return the size of the file in case it could have been read successfully or
     *         -1 if an error occurred or the file has not been found
     *
     * @exception   ConnectionFailedException
     *              could not access the file
     *
     * @see ibs.di.connect.Connector_01#readFile
     */
    public long readFile (String fileName, String destinationPath,
                          String destinationFileName)
        throws ConnectionFailedException
    {
        // the SABBCXMLRFCConnector does not support this method
        return -1;
    } // readFile


    /**************************************************************************
     * Write a file to a SAP Business Connector. <BR/>
     *
     * Note that this feature is not supported by the SAPBCXMLRFCConnector.<BR/>
     *
     * @param sourcePath        path to read the file from
     * @param fileName          name of the file to read
     *
     * @return the name of the file written or null in case it could not have
     *         been written
     *
     * @exception   ConnectionFailedException
     *              could not access the file
     *
     * @see ibs.di.connect.Connector_01#writeFile
     */
    public String writeFile (String sourcePath, String fileName)
        throws ConnectionFailedException
    {
        // the SABBCXMLRFCConnector does not support this method
        return "";
    } // writeFile


    /**************************************************************************
     * Delete a file via the SAP Business Connector.<BR/>
     *
     * Note that this feature is not supported by the SAPBCXMLRFCConnector.<BR/>
     *
     * @param fileName      the name of the file to delete
     *
     * @return true if the file could he deleted or false otherwise
     *
     * @exception   ConnectionFailedException
     *              could not access the file
     *
     * @see ibs.di.connect.Connector_01#deleteFile
     */
    public boolean deleteFile (String fileName)
        throws ConnectionFailedException
    {
        // the SABBCXMLRFCConnector does not support this method
        return false;
    } // deleteFile


    /**************************************************************************
     * Represent the properties to the user. <BR/>
     *
     * @param   table       Table where the properties should be added.
     *
     * @see ibs.bo.BusinessObject#showProperties
     */
    protected void showProperties (TableElement table)
    {
        super.showProperties (table);
        // loop through all properties of this object and display them:
        this.showProperty (table, DIArguments.ARG_ARG1,    
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_SAPBCSERVERURL, env),
            Datatypes.DT_TEXT, this.arg1);
        this.showProperty (table, DIArguments.ARG_ARG2,    
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_SAPBCSERVERNAME, env),
            Datatypes.DT_TEXT, this.arg2);
/* BB: the envelope feature will possibly be activated in the future
        this.showProperty (table, DIArguments.ARG_ARG3, DITokens.TOK_SAPBCENVELOPE,
                      Datatypes.DT_TEXT, this.arg3);
*/
    } //  showProperties


    /**************************************************************************
     * Represent the properties of the object to the user within a form. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     *
     * @see ibs.bo.BusinessObject#showFormProperties
     */
    protected void showFormProperties (TableElement table)
    {
        super.showFormProperties (table);
        // loop through all properties of this object and display them:
        this.showFormProperty (table, DIArguments.ARG_ARG1,    
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_SAPBCSERVERURL, env),
            Datatypes.DT_NAME, this.arg1);
        this.showFormProperty (table, DIArguments.ARG_ARG2,   
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_SAPBCSERVERNAME, env),
            Datatypes.DT_NAME, this.arg2);
/* BB: the envelope feature will possibly be activated in the future
        this.showFormProperty (table, DIArguments.ARG_ARG3, DITokens.TOK_SAPBCENVELOPE,
                          Datatypes.DT_SELECT, this.arg3, this.envelopeTypes, this.envelopeTypes,
                          0, false);
*/
    } // showFormProperties


    /**************************************************************************
     * Displays the settings of the connector.<BR/>
     *
     * @param   table       Table where the settings shall be added.
     */
    public void showSettings (TableElement table)
    {
        super.showSettings (table);
        // display connector specific settings
        this.showProperty (table, DIArguments.ARG_ARG1,    
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_SAPBCSERVERURL, env),
            Datatypes.DT_TEXT, this.p_sapBCGatewayUrl);
        this.showProperty (table, DIArguments.ARG_ARG2,    
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_SAPBCSERVERNAME, env),
            Datatypes.DT_TEXT, this.p_repServerName);
/* BB: the envelope feature will possibly be activated in the future
        this.showProperty (table, DIArguments.ARG_ARG3, DITokens.TOK_SAPBCENVELOPE,
                      Datatypes.DT_TEXT, this.p_envelope);
*/
    } // showSettings


    /**************************************************************************
     * Adds the settings of the connector to a log.<BR/>
     *
     * @param   log     the log to add the setting to
     */
    public void addSettingsToLog (Log_01 log)
    {
        super.addSettingsToLog (log);
        // add connector specific settings
        log.add (DIConstants.LOG_ENTRY,    
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_SAPBCSERVERURL, env) + ": " +
            this.p_sapBCGatewayUrl, false);
        log.add (DIConstants.LOG_ENTRY,    
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_SAPBCSERVERNAME, env) + ": " +
            this.p_repServerName, false);
/* BB: the envelope feature will possibly be activated in the future
        log.add (DIConstants.LOG_ENTRY, DITokens.TOK_SAPBCENVELOPE + ": " +
                 this.p_envelope, false);
*/
    } // addSettingsToLog


    /**************************************************************************
     * Reads an response from a stream. <BR/>
     *
     * @param   responseStream  the inputstream to read the response
     *
     * @return  the response string or "" in case it could not have been read
     */
    private String getResponse (InputStream responseStream)
    {
        String response = "";
        StringBuffer stringBuffer;
        int c;

        try
        {
            // check if we got an respone stream
            if (responseStream != null)
            {
                // first read the response into a string
                stringBuffer = new StringBuffer ();
                while ((c = responseStream.read ()) != -1)
                {
                    // append the character to the string buffer
                    stringBuffer.append ((char) c);
                } // while ((c = responseStream.read ()) != -1)
                response = stringBuffer.toString ();
//showDebug ("response = " + response);
            } // if (responseStream != null)
            return response;
        } // try
        catch  (IOException e)
        {
            IOHelpers.showMessage ("Error when getting response",
                e, this.app, this.sess, this.env, true);
            return null;
        } // catch
    } // getResponse


    /**************************************************************************
     * Checks the response of an SAP Business Connector. <BR/>
     * The response will be an XML structure in XMLRFC notation.
     * The method extracts the %lt;RETURN&gt; section that looks like this:
     * <pre>
     * <RETURN>
     *   <TYPE>S = Success, E = Error, W = Warning, I = Information</TYPE>
     *   <CODE>Meldungs-Code</CODE>
     *   <MESSAGE>Meldungstext</MESSAGE>
     *   <LOG_NO/>
     *   <LOG_MSG_NO>000000</LOG_MSG_NO>
     * </RETURN>
     * </pre>
     * In case the &lt;TYPE&gt; is not "S" a ConnectionFailedException will be thrown.
     *
     * @param   response     the response from the SAP Business Connector
     *
     * @exception   ConnectionFailedException
     *              A ConnectionFailedException will be thrown in case
     *              the SAP Server did not accept the message we sent
     */
    public void checkResponse (String response)
        throws ConnectionFailedException
    {
        String returnType = "";
        String errorMessage =  
            MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                DIMessages.ML_MSG_SAP_ERROR, env);
        NodeList returnNodes;
        NodeList faultNodes;
        NodeList returnValueNodes;
        Document root;
        Node node;
        String nodeName;
        String nodeValue;
        Text text;

        try
        {

            // check if we got a response string
            // that indicates that the SAP Business connector could not
            // recognize the message we send. In case we send an XML message
            // the SAP Business Connector is not able to process we get an
            // empty response string
            if (response != null && response.length () > 0)
            {
                // instantiate errorHandler
                DIErrorHandler errorHandler = new DIErrorHandler ();
                errorHandler.setEnv (this.env);
                errorHandler.sess = this.sess;

                // read the document:
                // do not validate the document. This speeds up the parsing
                root = new XMLReader (new StringReader (response), false, errorHandler).getDocument ();
                returnNodes = root.getElementsByTagName ("RETURN");
                // check if we exactly got one return node
                if (returnNodes.getLength () == 0)
                {
                    // check if we can read an <rfcprop:Fault> tag that indicates
                    // that the SAP server returned an error message
                    faultNodes = root.getElementsByTagName ("rfcprop:Fault");
                    // did we get exactly one entry?
                    if (faultNodes.getLength () == 1)
                    {
                        // get the error message
                        errorMessage = this.getSAPErrorMessage (faultNodes.item (0));
                    } // if (faultNodes.getLength () == 1)
                } // if (returnNodes == null)
                else if (returnNodes.getLength () == 1)
                {
                    returnValueNodes = returnNodes.item (0).getChildNodes ();
                    // check if any nodes could be found
                    if (returnValueNodes != null)
                    {
                        // loop though the list
                        for (int i = 0; i < returnValueNodes.getLength (); i++)
                        {
                            node = returnValueNodes.item (i);

                            if (node.getNodeType () == Node.ELEMENT_NODE)
                            {
                                // get the node name
                                nodeName = node.getNodeName ();
                                // get the node value
                                text = (Text) node.getFirstChild ();
                                if (text != null)
                                {
                                    nodeValue = text.getNodeValue ();
                                } // if
                                else
                                {
                                    nodeValue = "";
                                } // else

                                // <TYPE>S = Success, E = Error, W = Warning, I = Information</TYPE>
                                if (nodeName.equalsIgnoreCase ("TYPE"))
                                {
                                    // if no value has been returned we assume that everything was ok
                                    if (nodeValue.length () == 0)
                                    {
                                        returnType = "S";
                                    } // if
                                    else
                                    {
                                        returnType = nodeValue;
                                    } // else
                                    errorMessage += ":" + returnType;
                                } // if (nodeName.equals ("TYPE"))
                                // <CODE>message code</CODE>
                                if (nodeName.equalsIgnoreCase ("CODE"))
                                {
                                    errorMessage += ":" + nodeValue;
                                } // if
                                // <MESSAGE>message text</MESSAGE>
                                if (nodeName.equalsIgnoreCase ("MESSAGE"))
                                {
                                    errorMessage += ":" + nodeValue;
                                } // if
                                // <LOG_NO>number of log entry</LOG_NO>
                                if (nodeName.equalsIgnoreCase ("LOG_NO"))
                                {
                                    errorMessage += ":" + nodeValue;
                                } // if
                                // <LOG_MSG_NO>000000</LOG_MSG_NO>
                                if (nodeName.equalsIgnoreCase ("LOG_MSG_NO"))
                                {
                                    errorMessage += ":" + nodeValue;
                                } // if
                            } // if (node.getNodeType () == Node.ELEMENT_NODE)
                        } // for (int i = 0; i < returnValueNodes.getLength (); i++)
                    } // if (returnValueNodes != null)
                    else                // no return value found
                    {
                        // nothing to do
                    } // else no return value found
                } // else if (returnNodes.getLength () == 1)
            } // if (response.length () > 0)
            // check if the return code was "S"
            // which indicates that the processing was successful
            if (!returnType.equals ("S"))
            {
                // throw an ConnectionFailedException with the return code and
                // error message we got from the SAP system
                throw new ConnectionFailedException (errorMessage);
            } // if (! returnType.equals ("S"))
        } // try
        catch (XMLReaderException e)
        {
            throw new ConnectionFailedException (e.getMessage (), e);
        } // catch
    } // checkResponse


    /**************************************************************************
     * Get the error message out of an SAP error return message. <BR/>
     * Such a message looks like this:
     * <pre>
     * <?xml version="1.0"?>
     * <sap:Envelope xmlns:sap="urn:sap-com:document:sap" version="1.0">
     * <sap:Body>
     *   <rfcprop:Fault xmlns:rfcprop="urn:sap-com:document:sap:rfc:properties">
     *     <faultcode>401</faultcode>
     *     <faultstring>com.wm.app.b2b.server.ServiceException</faultstring>
     *     <detail>
     *       <name>SBC_EXCEPTION</name>
     *       <message>
     *         <text>No RFC Function Name Supplied</text>
     *       </message>
     *     </detail>
     *   </rfcprop:Fault>
     * </sap:Body>
     * </sap:Envelope>
     * </pre>
     *
     * @param   node        the &lt;rfcprop:Fault&gt; node
     *
     * @return  the faultstring the name and the message text as a string
     *          in the form "&lt;faultstring&gt;:&lt;name&gt;:&lt;messagetext&gt;"
     */
    private String getSAPErrorMessage (Node node)
    {
        String errorString = "SAP";
        NodeList faultNodes;
        Node faultNode;
        NodeList detailNodes;
        Node detailNode;
        NodeList messageNodes;
        Node messageNode;
        Text text;

        try
        {
            // get the child nodes
            faultNodes = node.getChildNodes ();
            for (int i = 0; i < faultNodes.getLength (); i++)
            {
                faultNode = faultNodes.item (i);

                // check if we found the <faultcode> tag
                if (faultNode.getNodeName ().equals ("faultcode"))
                {
                    // get the <faultcode> and append it into the errorString
                    text = (Text) faultNode.getFirstChild ();
                    if (text != null)
                    {
                        errorString += ":" + text.getNodeValue ();
                    } // if
                } // if (node.getNodeName ().equals ("faultcode"))
                // check if we found the <detail> tag
                else if (faultNode.getNodeName ().equalsIgnoreCase ("detail"))
                {
                    detailNodes = faultNode.getChildNodes ();
                    for (int j = 0; j < detailNodes.getLength (); j++)
                    {
                        detailNode = detailNodes.item (j);

                        // check if we found the <name> tag
                        if (detailNode.getNodeName ().equalsIgnoreCase ("name"))
                        {
                            // get the <name> and append it into the errorString
                            text = (Text) detailNode.getFirstChild ();
                            if (text != null)
                            {
                                errorString += ":" + text.getNodeValue ();
                            } // if
                        } // if (detailNode.getNodeName ().equals ("name"))
                        else if (detailNode.getNodeName ().equalsIgnoreCase ("message"))
                        {
                            messageNodes = detailNode.getChildNodes ();
                            for (int k = 0; k < messageNodes.getLength (); k++)
                            {
                                messageNode = messageNodes.item (k);

                                // check if we found a text node
                                if (messageNode.getNodeName ().equalsIgnoreCase ("text"))
                                {
                                    // get the <name> and append it into the errorString
                                    text = (Text) messageNode.getFirstChild ();
                                    if (text != null)
                                    {
                                        errorString += ":" + text.getNodeValue ();
                                    } // if
                                } // if (messageNode.getNodeName ("text"))
                            } // for (int k = 0; k < messageNodes.getLength (); k ++)
                        } // else if (detailNode.getNodeName ().equals ("message"))
                    } // for (int j = 0; j < detailNodes.getLength (); j++)
                } // else if (node.getNodeName ().equals ("detail"))
            } // for (int i = 0; i < faultNodes.getLength (); i++)
            return errorString;
        } // try
        catch (Exception e)
        {
            IOHelpers.showMessage ("Error when getting SAP error message",
                e, this.app, this.sess, this.env, true);
            return errorString;
        } // catch
    } // getSAPErrorMessage

} // SAPBCXMLRFCConnector_01
