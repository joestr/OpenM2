/*
 * Created by IntelliJ IDEA.
 * User: hpichler
 * Date: 19.08.2002
 * Time: 09:06:21
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */

// package:
package ibs.service.observer;

// imports:
import ibs.app.AppFunctions;
import ibs.bo.BOArguments;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.service.observer.M2ObserverArguments;
import ibs.service.observer.M2ObserverConfiguration;
import ibs.service.observer.M2ObserverEvents;
import ibs.service.observer.M2ObserverJob;
import ibs.service.observer.Observer;
import ibs.service.observer.ObserverConfiguration;
import ibs.service.observer.ObserverException;
import ibs.service.observer.ObserverJob;
import ibs.tech.ntlm.NTLMClient;
import ibs.tech.ntlm.NTLMException;
import ibs.tech.xml.XMLReader;
import ibs.tech.xml.XMLReaderException;

import java.io.BufferedInputStream;
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
 * An m2Observer is a thread, that periodically checks its ObserverJobs and
 * starts their execution if their next cycle is due. The execution itself will
 * not be started here, but rather by sending a URL to the m2-server.
 *
 * @version     $Id: M2Observer.java,v 1.2 2007/07/31 19:13:58 kreimueller Exp $
 *
 * @author      HORST PICHLER, 19.08.2002
 ******************************************************************************
 */
public class M2Observer extends Observer
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: M2Observer.java,v 1.2 2007/07/31 19:13:58 kreimueller Exp $";


    /**************************************************************************
     * Constructor for an m2Observer object. <BR/>
     */
    public M2Observer ()
    {
        // call constructor of super class:
        super ();
    } // m2Observer


    /**************************************************************************
     * Constructor for an m2Observer object. A value for testCycles > 0
     * indicates that thread runs in test-mode. In testmode main-loop will
     * be executed only testCycles times (not eternally).
     *
     * @param   config      The configuration for the observer.
     * @param   testCycles  The number of test cycles.
     */
    protected M2Observer (ObserverConfiguration config, int testCycles)
    {
        // call constructor of super class:
        super (config, testCycles);
    } // Observer


    /**************************************************************************
     * Perform the ObserverJob execution by sending an HTTP request
     * to the m2 server (methods simulates a web client). The connection will
     * be established with the parameters given in the observer configuration
     * file under M2CONNECTION. <BR/>
     *
     * A certain function (executeObserverJob) is called at the m2 server that
     * will first perform a login with the username and password given and will
     * start the job execution in case the login has been sucessfull. <BR/>
     *
     * The agent reads the XML-response from the webserver, interprets it and
     * reacts according to the result.
     *
     * @param   j       The job to be executed.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    protected void executeObserverJob (ObserverJob j) throws ObserverException
    {
        this.log ("Executing job " + j.getJdata ().getName () + ".", true);

        // check instance-class ==> could also be implemented with instanceof
        M2ObserverJob job = null;
        try
        {
            job = (M2ObserverJob) j;
        } // try
        catch (Exception e)
        {
            throw new ObserverException ("m2Observer.executeObserverJob: " +
                    " Classname of given job is no instance of m2ObserverJob." +
                    " job=" + job.toString ());
        } // catch

        // refetch jobs data and execute
        job.refetch ();

        URL url = null;
        String loginQueryStr = "";
        String executeQueryStr = "";
        String m2ServletServerUrl = "";
        String responseStr = "";
        HttpURLConnection connection = null;
        BufferedInputStream responseStream = null;

        // type-cast configuration object
        M2ObserverConfiguration conf = (M2ObserverConfiguration) this.getConfig ();

        // establish connection; call m2 observerjob-interface; wait for xml-response
        try
        {
            String s = conf.getM2ConnectionServer () + conf.getM2ConnectionApppath ();
            boolean isServlet = M2ObserverConfiguration.TYPE_SERVLET.equals (conf.getM2ConnectionType ());

            // construct the URL to the m2 server
// KR HACK: The check for http and https is missing!!!
// KR HACK: There should not be the name of the servlet!!!
            m2ServletServerUrl = IOConstants.URL_HTTP + s + "ApplicationServlet";

            // check if we need to send the data needed for a servlet environment
            if (isServlet)
            {
                loginQueryStr = "?" + BOArguments.ARG_PATH + "=" + IOHelpers.urlEncode (s) + "&";
            } // if
            else
            {
                loginQueryStr = "?";
            } // else

            // construct the query string for the login
            loginQueryStr += BOArguments.ARG_USERNAME + "=" +
                IOHelpers.urlEncode (conf.getM2ConnectionUsername ()) +
                "&" +
                BOArguments.ARG_PASSWORD + "=" +
                IOHelpers.urlEncode (conf.getM2ConnectionPassword ()) +
                "&" +
                BOArguments.ARG_DOMAIN + "=" +
                IOHelpers.urlEncode ("" + conf.getM2ConnectionDomain ()) +
                "&" +
                BOArguments.ARG_FUNCTION + "=" +
                IOHelpers.urlEncode ("" + AppFunctions.FCT_OBSERVER) +
                "&" +
                BOArguments.ARG_EVENT + "=" +
                IOHelpers.urlEncode ("" + M2ObserverEvents.EVT_EXECOBSERVERJOB);
            // set the id of the job to execute
            executeQueryStr += "&" + M2ObserverArguments.ARG_JOBID + "=" +
                    IOHelpers.urlEncode ("" + job.getJdata ().getId ());
            executeQueryStr += "&" + M2ObserverArguments.ARG_OBS + "=" +
                    IOHelpers.urlEncode ("" + job.getContext ().getName ());

            url = new URL (m2ServletServerUrl + loginQueryStr + executeQueryStr);

            // open a http url connection
            this.log ("Connecting to m2-server: " + url.toString (), true);
            connection = (HttpURLConnection) url.openConnection ();
            connection.setDoInput (true);
            connection.setDoOutput (true);
            connection.connect ();

            // did we get an 401 UNAUTHORIZED code back from the server?
            if (connection.getResponseCode () == HttpURLConnection.HTTP_UNAUTHORIZED)
            {
                // try to negotiate an NTLM authentication:
                connection = NTLMClient.negotiateNTLM (url,
                    conf.getNTLMDomain (), conf.getNTLMUsername (),
                    conf.getNTLMPassword (), null);
            } // if (connection.getResponseCode () == HttpURLConnection.HTTP_UNAUTHORIZED)

            // do we have a valid connection?
            if (connection != null)
            {
                // set the inputStream and read the response of the server
                responseStream = new BufferedInputStream (connection.getInputStream ());
                this.log ("Waiting for output.", true);
                responseStr = this.readResponse (responseStream);
            } // if (connection != null)
            else                        // NTLM connection must have failed
            {
                throw new ObserverException ("NTLM communication failed!");
            } // else NTLM connection must have failed
        } // try
        catch (MalformedURLException e)
        {
            throw new ObserverException ("Connection to m2-server broken: " +
                e.toString (), e);
        } // catch
        catch (IOException e)
        {
            throw new ObserverException ("Error while communicating to m2-server: " +
                e.toString (), e);
        } // catch
        catch (NTLMException e)
        {
            throw new ObserverException ("Error while negotiating NTLM with m2-server: " +
                e.toString (), e);
        } // catch
        catch (Exception e)
        {
            throw new ObserverException ("Exception while communicating to m2-server: " +
                e.toString (), e);
        } // catch
        finally
        {
            try
            {
                if (responseStream != null)
                {
                    responseStream.close ();
                } // if
                if (connection != null)
                {
                    connection.disconnect ();
                } // if
            } // try
            catch (IOException e)
            {
                throw new ObserverException ("Error while closing connection to m2-server: " +
                    e.toString (), e);
            } // catch
        } // finally

        // check if m2 responded ok for this jobs execution, otherwise
        // an ObserverException will be thrown.
        this.checkResponse (responseStr);
    } // executeObserverJob


    /**************************************************************************
     * Reads an response from a stream. <BR/>
     *
     * @param   responseStream  The stream to read from.
     *
     * @return  The response as a string.
     *
     * @throws  ObserverException
     *          An error occurred.
     * @throws  IOException
     *          An error occurred during IO operation.
     */
    private String readResponse (InputStream responseStream)
        throws IOException, ObserverException
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
            } // if (responseStream != null)
            else
            {
                throw new ObserverException ("Invalid/Empty response from m2-server.");
            } // else
            return response;
        } // try
        catch  (IOException e)
        {
            throw new ObserverException ("Error while reading response from m2-server: " +
                e.toString ());
        } // catch
    } // readResponse


    /**************************************************************************
     * Checks the response of an SAP Business Connector. <BR/>
     * The response will be an XML structure in XMLRFC notation.
     * The method extracts the &lt;RETURN> section that looks like this:
     * <m2:RESPONSE VERSION="1.0" xmlns:m2="urn:www.intos.com:m2">
     *   <ACTION>
     *     <CODE>1234</CODE>
     *     <NAME>ExecuteObserverJob</NAME>
     *   </ACTION>
     *   <RETURN>
     *     <TYPE>S = Success, E = Error, W = Warning, I = Information</TYPE>
     *     <CODE>Meldungs-Code</CODE>
     *     <MESSAGE>Meldungstext</MESSAGE>
     *   </RETURN>
     * </m2:RESPONSE>
     * In case the &lt;TYPE> is not "S" a ObserverException will be thrown.
     *
     * @param   response    The response to be checked.
     *
     * @throws  ObserverException
     *          An error occurred.
     */
    public void checkResponse (String response)
        throws ObserverException
    {
        NodeList nodes;
        Node node;
        Document root;
        Text text;
        String value;
        String errstr;
        String code;
        String message;

        try
        {
            // check if we got a response string that indicates that the SAP Business connector could not
            // recognize the message we send. In case we send an XML message
            // the SAP Business Connector is not able to process we get an
            // empty response string
            if (response != null && response.length () > 0)
            {
                // read the document:
                // do not validate the document. This speeds up the parsing
                root = new XMLReader (new StringReader (response), false, null).getDocument ();

//
// QUICK & DIRTY: extract TYPE - if it not "S" (=success) throw exception with CODE/MESSAGE as information
//
                // get all <TYPE>-nodes. the result should be exactly one node
                nodes = root.getElementsByTagName ("TYPE");
                // check if we exactly got one return node
                if (nodes.getLength () != 1)
                {
                    throw new ObserverException (
                        "Error while parsing response of m2-server: " +
                            "wrong format " + root.toString ());
                } // if

                // get content of <TYPE>-node
                node = nodes.item (0);
                node.normalize ();
                text = (Text) node.getFirstChild ();
                if (text == null)
                {
                    throw new ObserverException (
                        "Error while parsing response of m2-server: " +
                            "wrong format " + root.toString ());
                } // if
                value = text.getNodeValue ();
                if (value == null ||
                    (!value.equals ("E") && !value.equals ("S") &&
                        !value.equals ("I") && !value.equals ("W")))
                {
                    throw new ObserverException (
                        "Error while parsing response of m2-server: " +
                            "wrong TYPE: " + value);
                } // if

                // check if response indicates problems (not S for success)
                if (!value.equals ("S"))
                {
                    // create error message
                    errstr = "Response from m2 indicates error while executing job. Errorcode: ";

                    // get all <CODE>-nodes. the result should be exactly two nodes
                    nodes = root.getElementsByTagName ("CODE");
                    code = "none";
                    // check if we exactly got two return node
                    if (nodes.getLength () == 2)
                    {
                        // get content of 2nd <CODE>-node
                        node = nodes.item (1);
                        node.normalize ();
                        text = (Text) node.getFirstChild ();
                        if (text != null)
                        {
                            code = text.getNodeValue ();
                        } // if
                    } // if

                    // add to error message
                    errstr += code + "; Message: ";

                    // get all <MESSAGE>-nodes. the result should be exactly one node
                    nodes = root.getElementsByTagName ("MESSAGE");
                    message = "---";
                    // check if we exactly got one return-node
                    if (nodes.getLength () == 1)
                    {
                        // get content of 2nd <CODE>-node
                        node = nodes.item (0);
                        node.normalize ();
                        text = (Text) node.getFirstChild ();
                        if (text != null)
                        {
                            message = text.getNodeValue ();
                        } // if
                    } // if

                    // add to error message
                    errstr += message;

                    // throw error
                    throw new ObserverException (errstr);
                } // if
            } // if (! response.length () == 0)
            else
            {
                throw new ObserverException (
                    "Error while parsing response of m2-server: " +
                        "response was empty.");
            } // if
        } // try
        catch (XMLReaderException e)
        {
            throw new ObserverException (
                "Error while parsing response of m2-server: " + e.toString (),
                e);
        } // catch
    } // checkResponse

} // M2Observer
