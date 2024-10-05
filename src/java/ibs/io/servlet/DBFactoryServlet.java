/**
 * class DBFactoryServlet.java
 */

// package:
package ibs.io.servlet;

// imports:
import ibs.di.service.XMLDBFactory;
import ibs.io.servlet.DBFactoryServletConstants;
import ibs.io.servlet.DBFactoryServletMessages;
import ibs.tech.xml.XMLReader;
import ibs.tech.xml.XMLReaderException;
import ibs.util.DateTimeHelpers;
import ibs.util.StringHelpers;
import ibs.util.file.FileHelpers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/******************************************************************************
 * DBFactoryServlet is a wrapper class for the DBFactory.
 * DBFactoryServlet takes in it's parameters through the browser
 * and makes use of DBFactory for writing data to a database
 * or reading data from a database.
 *
 * @version     $Id: DBFactoryServlet.java,v 1.10 2007/07/24 21:29:09 kreimueller Exp $
 *
 * @author      CHINNI RANJITH KUMAR
 ******************************************************************************
 */
public class DBFactoryServlet extends HttpServlet
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DBFactoryServlet.java,v 1.10 2007/07/24 21:29:09 kreimueller Exp $";


    /**
     * Serializable version number. <BR/>
     * This value is used by the serialization runtime during deserialization
     * to verify that the sender and receiver of a serialized object have
     * loaded classes for that object that are compatible with respect to
     * serialization. <BR/>
     * If the receiver has loaded a class for the object that has a different
     * serialVersionUID than that of the corresponding sender's class, then
     * deserialization will result in an {@link java.io.InvalidClassException}.
     * <BR/>
     * This field's value has to be changed every time any serialized property
     * definition is changed. Use the tool serialver for that purpose.
     */
    static final long serialVersionUID = -2618704266550082123L;


    /**
     * Holds the action parameter value of URL.
     */
    String action = null;
    /**
     * Holds the id parameter value of URL.
     */
    String id = null;
    /**
     * Holds the cfg parameter value of URL.
     */
    String exportCfg = null;
    /**
     * Holds the configfile (servlet config file) parameter value of URL.
     */
    String servletConfigFile = DBFactoryServletConstants.CONFIGFILENAME;
    /**
     * Holds the query parameter value of URL.
     */
    String query = null;
    /**
     * Holds the where parameter value of URL.
     */
    String where = null;
    /**
     * Holds the table parameter value of URL.
     */
    String table = null;
    /**
     * Holds the attribute parameter value of URL.
     */
    String attribute = null;
    /**
     * Holds the debug parameter value of URL.
     */
    boolean isDebug = false;
    /**
     * Holds the verbose parameter value of URL.
     */
    boolean isVerbose = false;
    /**

     * read from DBFactoryServlet config file.
     */
    Vector<String> importConfigFiles = new Vector<String> ();
    /**
     * Holds the DBFactory export config file names
     * read from DBFactoryServlet config file.
     */
    Vector<String> exportConfigFiles = new Vector<String> ();
    /**
     * Holds the DOM root of DBFactoryServlet config file (xml).
     */
    Document document;
    /**
     * Holds the list of IP Addresses read from DBFactoryServlet
     * config file.
     */
    Vector<String> ip = new Vector<String> ();
    /**
     * Holds the name of the servlet startup directory.
     * the
     */
    String servletDir = null;
    /**
     * Holds the name of the config directory read from DBFactoryServlet
     * config file.
     */
    String workingDir = null;
    /**
     * Holds the name of the config directory read from DBFactoryServlet
     * config file.
     */
    String configDir = null;
    /**
     * Holds logging status.
     */
    boolean isLogging = false;
    /**
     * Holds log file name if logging is on.
     */
    String logFile = null;
    /**
     * Log file Stream.
     */
    FileOutputStream logFileStream = null;
    /**
     * Holds maximum number of log entries.
     */
    int maxLogEntries = 0;
    /**
     * Holds the HTTP Response Stream.
     */
    PrintWriter responseStream;
    /**
     * Holds the IP Address of the client requesting for the DBFactoryServlet.
     */
    String clientIP = null;
    /**
     * Holds the instance of XMLDBFactory.
     */
    XMLDBFactory factory = null;
    /**
     * Holds the list of query string parameters read from
     * URL. This vector is passed to the XMLDBFactory for exporting or
     * importing.
     */
    Vector<String> factoryVector = new Vector<String> ();
    /**
     * Holds the DOM root of the import file.
     */
    Document importDocument;
    /**
     * Holds the name of the import file to be created in working directory.
     */
    String importFile = DBFactoryServletConstants.IMPORT_FILE;
    /**
     * Holds the name of the export file to be created in working directory.
     */
    String exportFile = DBFactoryServletConstants.EXPORT_FILE;
    /**
     * Holds the HTTP response object.
     */
    HttpServletResponse response = null;
    /**
     * Holds the HTTP request object. <BR/>
     */
    HttpServletRequest request = null;
    /**
     * Holds the query string passed to the Servlet.
     */
    String queryString = null;
    /**
     * Holds the log count. <BR/>
     */
    static int logCount = 0;

    /**
     * Tag name used to identify values. <BR/>
     */
    private static final String TAG_VALUE = "value";



   /**************************************************************************
     * This Function sets all class variable to default values.
     * This is required because the servlet retains it's state between each
     * request.
     */
    private void setDefaults ()
    {
        this.servletDir = this.getInitParameter ("servletPath") + "/";

       // Set all data members to default values.
        this.action = null;
        this.id = null;
        this.exportCfg = null;
        this.servletConfigFile = DBFactoryServletConstants.CONFIGFILENAME;
        this.document = null;
        this.importConfigFiles = new Vector<String> ();
        this.exportConfigFiles = new Vector<String> ();
        this.ip = new Vector<String> ();
        this.workingDir = this.servletDir;
        this.configDir = this.servletDir;
        this.isLogging = false;
        this.logFile = null;
        this.logFileStream = null;
        this.maxLogEntries = 0;
        this.clientIP = null;
        this.query = null;
        this.isDebug = false;
        this.isVerbose = false;
        this.where = null;
        this.table = null;
        this.attribute = null;
        this.factory = null;
        this.factoryVector = new Vector<String> ();
        this.importDocument = null;
        this.importFile = DBFactoryServletConstants.IMPORT_FILE;
        this.exportFile = DBFactoryServletConstants.EXPORT_FILE;
        this.response = null;
        this.request = null;
        this.queryString = null;
    } // setDefaults


    /**************************************************************************
     * This is the function which is called when a request comes for a servlet
     * via POST method.
     *
     * @param req   HTTP request object
     * @param resp  HTTP response object
     *
     * @throws  ServletException
     *          This exception is thrown if any internal servlet exception occurs
     * @throws  IOException
     *          An IOException occurred.
     */
    protected void doPost (HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
        try
        {
            // set all data members to it's default values
            this.setDefaults ();
            // set the response data member
            this.response = resp;
            // set the request data member
            this.request = req;
            // get the client IP Address
            this.clientIP = req.getRemoteAddr ();
            // get the response stream
            this.responseStream = resp.getWriter ();
            // get the query string
            this.queryString = req.getQueryString ();
            // set the response content type
            this.response.setContentType ("text/html");

//          this.sendMessage ("servletDir=" + this.configDir);

            // get the DOM root of servlet config file
            this.document = this.parseFile (this.servletDir + this.servletConfigFile);
            // check if we have root
            if (this.document != null)
            {
                // traverse through the DOM tree and retrieve the values of config file for servlet
                if (!this.traverseDOM (this.document))
                {
                    // throw exception if something wrong with config file
                    throw new Exception ();
                } // if
            } // if (this.document != null)
            else
            {
                throw new Exception ();
            } // else if (this.document != null)

            // open the log file
            this.setLogFile ();

            this.writeToLog ("Request from host " +
                this.clientIP + ": " + this.queryString);

            // read the query string params of servlet URL
            if (!this.readQueryString ())
            {
                // throw exception if unknown query string params found in URL
                throw new Exception ();
            } // if

            // check for rights
            if (!this.checkRights ())
            {
                this.writeToLog ("Client have no Rights no access the servlet");
                throw new Exception ();
            } // if (!checkRights ())

            // the client has got rights
            // creates instance of FileOutputStream for storing
            // logging info if logging is on

            // build the vector of query strings read from URL
            // this vector will be passed to the DBFactory which will
            // use this vector for replacing any dummy values inside DBFactory config file
            this.buildFactoryVector ();
//                this.writeToLog ("biulding vector successful");
            // perform the action:
            this.performAction ();
        } // try
        catch (XMLReaderException e)
        {
            this.dbg (e.toString ());
            try
            {
                this.writeToLog (e.toString ());
            } // try
            catch (Exception e1)
            {
                // should not occur
            } // catch
            this.sendMessage (DBFactoryServletMessages.MSG_ERROR);
        } // catch
        catch (IOException e)
        {
            this.dbg (e.toString ());
            try
            {
                this.writeToLog (e.toString ());
            } // try
            catch (Exception e1)
            {
                // should not occur
            } // catch
            this.sendMessage (DBFactoryServletMessages.MSG_ERROR);
        } // catch
        catch (Exception e)
        {
            this.dbg (e.toString ());
            try
            {
                this.writeToLog (e.toString ());
            } // try
            catch (Exception e1)
            {
                // should not occur
            } // catch
            this.sendMessage (DBFactoryServletMessages.MSG_ERROR);
        } // catch

        if (this.logFileStream != null)
        {
            this.logFileStream.close ();
        } // if
        this.responseStream.close ();
    } // doPost


    /**************************************************************************
     * Perform action. <BR/>
     *
     * @throws  IOException
     *          An IOException occurred.
     * @throws  Exception
     *          Something went wrong.
     */
    protected void performAction ()
        throws IOException, Exception
    {
        // see if we have action param in URL
        if (this.action != null)
        {
            // check if it is dir
            if (this.action.equals (DBFactoryServletConstants.ACTION_DIR))
            {
                // check the query string params of URL
                if (!this.checkQueryString ())
                {
                    this.writeToLog ("Malformed query string");
                    throw new Exception ();
                } // if
                this.writeToLog ("Calling dir function");
                // call the dir function
                String files = this.dir ();
                if (files != null && !files.equals (DBFactoryServletConstants.BLANK))
                {
                    this.writeToLog ("Dir performed successfully");
                    this.sendMessage (files);
                } // if
                else
                {
                    this.writeToLog ("DBFactory config file names not found in Servlet config file");
                    this.sendMessage (DBFactoryServletMessages.MSG_ERROR);
                } // else
            } // if dir
            // check if it read
            else if (this.action.equals (DBFactoryServletConstants.ACTION_READ))
            {
                // check for presence of ID param in URL
                if (!this.checkForId ())
                {
                    this.writeToLog ("DBFactory import config file name missing");
                    throw new Exception ();
                } // if
                this.writeToLog ("Calling read function");
                // call the read function
                if (!this.read ())
                {
                    this.writeToLog ("Read function failed");
                    throw new Exception ();
                } // if
                this.writeToLog ("Read function performed successfully");
            } // if read
            // check if it is write
            else if (this.action.equals (DBFactoryServletConstants.ACTION_WRITE))
            {
                // check for the presence of ID param in URL
                if (!this.checkForExportCfg ())
                {
                    this.writeToLog ("DBFactory export config file missing");
                    throw new Exception ();
                } // if
                this.writeToLog ("Calling write function");
                // call the write function
                if (!this.write ())
                {
                    this.writeToLog ("Write function failed");
                    throw new Exception ();
                } // if
                this.writeToLog ("Write function performed successfully");
                this.sendMessage (DBFactoryServletMessages.MSG_OK);
            } // if write
            else  // throw exception if the action is unknown
            {
                this.writeToLog ("Unknown action");
                throw new Exception ();
            } // else
        } // if
        else // throw exception if action is null
        {
            throw new Exception ();
        } // else
    } // performAction


    /**************************************************************************
     * Checks whether logging is on or not and creates log stream if logging is
     * on. <BR/>
     *
     * @param   msg     The message to be written.
     *
     * @throws  IOException
     *          An exception occurred during accessing the log.
     */
    private void writeToLog (String msg) throws IOException
    {
        String msgLocal = msg;          // variable for local assignments

        // check if logging is activated
        if (this.isLogging)
        {
            // check if the log stream is valid
            if (this.logFileStream != null)
            {
                // add a time stamp and '\n' to the message
                msgLocal = DateTimeHelpers.dateTimeToString (new Date ()) + " " + msgLocal + "\n";
                // write the message to the log stream
                this.logFileStream.write (msgLocal.getBytes ());
                DBFactoryServlet.logCount++;
            } // if (this.logFileStream != null)
        } // if (this.isLogging)
    } // writeToLog


    /**************************************************************************
     * Checks whether logging is on or not and creates log stream if logging is
     * on.
     *
     * @throws  IOException
     *          An error occurred during accessing the log.
     */
    private void setLogFile () throws IOException
    {
        // check if logging is on
        if (this.isLogging)
        {
            // check if we have proper log file name
            if (this.logFile != null &&
                !this.logFile.equals (DBFactoryServletConstants.BLANK))
            {
                // check if the log counter reaches the maximum count.
                if (this.maxLogEntries > 0 && DBFactoryServlet.logCount > this.maxLogEntries)
                {
                    // if exist the log file copy it to <logFile>.old
                    if (FileHelpers.exists (this.logFile))
                    {
                        FileHelpers.copyFile (this.logFile, this.logFile + ".old");
                        // delete the old log file
                        FileHelpers.deleteFile (this.logFile);
                    } // if (exist log file)
                    // reset the log counter
                    DBFactoryServlet.logCount = 0;
                } // if (max log entries reached)
                // open the log file in append mode
                this.logFileStream = new FileOutputStream (this.logFile, true);
            } // if (logFile is valid)
        } // if (this.isLogging)
    } // setLogFile


    /**************************************************************************
     * This is the function which is called when a request comes for a servlet
     * via GET method.
     *
     * @param req   HTTP request object
     * @param resp  HTTP response object
     *
     * @throws  ServletException
     *          This exception is thrown if any internal servlet exception occurs
     * @throws  IOException
     *          An IOException occurred.
     */
    protected void doGet (HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
        // call the dopost method
        this.doPost (req, resp);
    } // doGet


    /**************************************************************************
     * Function checks for the validity of query string.
     * It is called when the action is dir because when the action is dir
     * there is only one more parameter that can be passed as query string parameter
     * that is the name of the config file for servlet (xml).
     *
     * @return  true/false
     */
    private boolean checkQueryString ()
    {
        // check if we have attribute parameter
        if (this.attribute != null)
        {
            // return false if it is there
            return false;
        } // if
        // check if we have id parameter
        if (this.id != null)
        {
            // return false if it exists
            return false;
        } // if
        // check if we have query parameter
        if (this.query != null)
        {
            // return false if it exists
            return false;
        } // if
        // check if we have table parameter
        if (this.table != null)
        {
            // return false if it exists
            return false;
        } // if
        // check if we have where parameter
        if (this.where != null)
        {
            // return false if it exists
            return false;
        } // if
        // return true if everything is fine
        return true;
    } // checkQueryString


    /**************************************************************************
     * This function bulids the vector containing query string parameters
     * which have to be passed to the DBFactory during  import.
     */
    private void buildFactoryVector ()
    {
        // check if we have query parameter as part of URL
        if (this.query != null && this.query.length () > 0)
        {
            // add this parameter to the vector
            String queryString = DBFactoryServletConstants.PARAM_QUERY;
            queryString += DBFactoryServletConstants.SEPARATOR_EQUAL;
            queryString += this.query.trim ();
            this.factoryVector.addElement (queryString);
        } // if
        // chekc if we have tab;e parameter as part of URL
        if (this.table != null && this.table.length () > 0)
        {
            // add this parameter to the vector
            String queryString = DBFactoryServletConstants.PARAM_TABLE;
            queryString += DBFactoryServletConstants.SEPARATOR_EQUAL;
            queryString += this.table.trim ();
            this.factoryVector.addElement (queryString);
        } // if
        // check if we have where parametr as part of URL
        if (this.where != null && this.where.length () > 0)
        {
            // add this parameter to the vector
            String queryString = DBFactoryServletConstants.PARAM_WHERE;
            queryString += DBFactoryServletConstants.SEPARATOR_EQUAL;
            queryString += this.where.trim ();
            this.factoryVector.addElement (queryString);
        } // if
        // check if we have attribute parameter as part of URL
        if (this.attribute != null && this.attribute.length () > 0)
        {
            // add this parameter to the vector
            String queryString = DBFactoryServletConstants.PARAM_ATTRIBUTE;
            queryString += DBFactoryServletConstants.SEPARATOR_EQUAL;
            queryString += this.attribute;
            this.factoryVector.addElement (queryString);
        } // if
    } // buildFactoryVector


    /**************************************************************************
     * Function checks for the existence of id parameter in URL.
     * This function is called when the action is import or export.
     *
     * @return  true/false
     */
    private boolean checkForId ()
    {
        // check if we have id parameter
        if (this.id == null || this.id.equals (DBFactoryServletConstants.BLANK))
        {
            // return false if id doesn't exists
            return false;
        } // if
        // return true if id exists
        return true;
    } // checkForId


    /**************************************************************************
     * Function checks for the existence of cfg parameter in URL.
     * This function is called when the export action.
     *
     * @return  true/false
     */
    private boolean checkForExportCfg ()
    {
        // check if we have id parameter
        if (this.exportCfg == null || this.exportCfg.equals (DBFactoryServletConstants.BLANK))
        {
            // return false if id doesn't exists
            return false;
        } // if
        // return true if id exists
        return true;
    } // checkForId


    /**************************************************************************
     * Returns comma separated list of import config files for DBFactory.
     *
     * @return  Comma separated list of config files.
     */
    private String dir ()
    {
        String fileList = "";
        int size = this.importConfigFiles.size ();
        // loop through the importConfigFiles vector
        for (int i = 0; i < size; i++)
        {
            // append the file name
            fileList += this.importConfigFiles.elementAt (i);
            // check if it last file name in the vector
            if (i + 1 != size)
            {
                // append comma
                fileList += DBFactoryServletConstants.SEPARATOR_COMMA;
            } // if (i + 1 != size)
        } // for
        // return the string
        return fileList;
    } // dir


    /**************************************************************************
     * Reads in the config file and set the configuration for the dbfactory. <BR/>
     *
     * @param   configFile  The file from which to get the configuration values.
     *
     * @throws  Exception
     *            Thrown if an error occurred while importing.
     */
    private void initFactory (String configFile) throws Exception
    {
        // create an instance of the DBFactory
        this.factory = new XMLDBFactory ();
        // check for verbose parameters of servlet URL.
        if (this.isVerbose)
        {
            this.writeToLog ("Verbose enabled for DBFactory");
            // enable the verbose mode of DBFactory
            this.factory.traceVerbose = true;
            this.factory.traceConfig = true;
        } // if
        // check for debug parameters of servlet URL.
        if (this.isDebug)
        {
            this.writeToLog ("Debug enabled for DBFactory");
            // enable the debug mode of DBFactory
            this.factory.traceDBFields = true;
            this.factory.traceMapping = true;
            this.factory.traceQuery = true;
        } // if
        // open the configuration file and read the content.
        FileInputStream cfgFile = new FileInputStream (configFile);
        byte [] cfg = new byte [cfgFile.available ()];
        cfgFile.read (cfg);
        cfgFile.close ();
        // set the configuration for the DBFactory
        if (!this.factory.setConfiguration (new String (cfg), this.logFileStream))
        {
            this.writeToLog ("Factory configuration error!");
            throw new Exception ("DBFactory configuration error!");
        } // if
        this.writeToLog ("Configuration set for the DBFactory");
    } // initFactory


    /**************************************************************************
     * Function reads data from database,builds a xml file from the resultset
     * and also writes the xml file to response stream.
     *
     * @return  true/false
     *
     * @exception Exception
     *                    Thrown when any error occurs while importing
     */
    private boolean read () throws Exception
    {
        // set the configuration for the dbfactory
        this.initFactory (this.configDir + "/" + this.id);
        // get a unique file name for storing the import file in working directory
        this.importFile = this.getUniqueFileName (this.importFile, this.workingDir);
        this.writeToLog ("Import file name: " + this.importFile);
        // create a file output stream
        FileOutputStream fileOutputStream = new FileOutputStream (this.importFile);

        // call the import function of DBFactory
        if (!this.factory.performImport (this.factoryVector, fileOutputStream))
        {
            this.writeToLog ("Import not successful");
            // return false if any occurs while importing
            return false;
        } // if
        // open the resulting xml file and write the content
        // to the response stream of the http request.
        FileInputStream f = new FileInputStream (this.importFile);
        while (f.available () > 0)
        {
            this.responseStream.write (f.read ());
        } // while (f.available () > 0)
        // return true if everything is fine
        return true;
    } // read


    /**************************************************************************
     * Function reads the servlet request's input stream and writes the
     * data in the xml stream to database using DBFactory.
     *
     * @return  true/false
     *
     * @exception Exception
     *                    Thrown when any error occurs while exporting
     */
    private boolean write () throws Exception
    {
        // read the servlet request's input stream and serialize it
        if (!this.readInputStream ())
        {
            return false;
        } // if

        // set the export configuration for the dbfactory
        this.initFactory (this.configDir + "/" + this.exportCfg);

        // get the file input stream of export
        FileInputStream fileInputStream = new FileInputStream (this.exportFile);

        // call the export function of DBFactory
        if (!this.factory.performExport (false, fileInputStream))
        {
            this.writeToLog ("Export not successful");
            // return false if any error occurs
            return false;
        } // if (!this.factory.performExport (true, fileInputStream))
        this.writeToLog ("Export done");
        // return true if everything is fine
        return true;
    } // write


    /**************************************************************************
     * Checks if a file already exists and adds a number to the beginning of
     * the file until there can be found no file anymore with this filename. <BR/>
     *
     * @param   fileName    the name of the file to test
     * @param   path        the file path
     *
     * @return  A string with the new unique fileName.
     */
    public String getUniqueFileName (String fileName, String path)
    {
        String pathLocal = path;        // variable for local assignments
        String newFileName = fileName;
        int counter = 0;

        // if the path has no tailing separator character add it.
        if (!pathLocal.endsWith (DBFactoryServletConstants.SEPARATOR_FORWARDSLASH) &&
            !pathLocal.endsWith (DBFactoryServletConstants.SEPARATOR_BACKWARDSLASH))
        {
            pathLocal += DBFactoryServletConstants.UNIX_SEPARATOR;
        } // if path has no tailing separator character

        // try to find an unique filename
        while (FileHelpers.exists (pathLocal + newFileName))
        {
            newFileName = counter++ + fileName;
        } // while (FileHelpers.exists (destinationPath + newFileName))
        return pathLocal + newFileName;
    } // getUniqueFileName


    /**************************************************************************
     * Function reads the data from servlet request's input stream and serializes the
     * data to working directory.
     *
     * @return  true/false
     *
     * @exception   Exception
     *                    Thrown if any error while reading or writing
     */
    private boolean readInputStream ()throws Exception
    {
        this.writeToLog ("Trying to read servlet input stream");

        // get a unique file name for export file
        this.exportFile = this.getUniqueFileName (this.exportFile, this.workingDir);
        // create file output stream for export file
        FileOutputStream fileOutputStream = new FileOutputStream (this.exportFile);

        // get the servlet request's input stream
        ServletInputStream inputStream = this.request.getInputStream ();

        int cnt = 0;
        while (inputStream.available () > 0)
        {
            fileOutputStream.write (inputStream.read ());
            cnt++;
        } // while (inputStream.available () > 0)

        this.writeToLog (cnt + " bytes read from servlet input stream");

        // close the input stream
        inputStream.close ();
        // close the file stream
        fileOutputStream.close ();

        if (cnt == 0)
        {
            return false;
        } // if

        this.writeToLog ("File successfully written to system: " + this.exportFile);
        // return true if everything is fine
        return true;
    } // readInputStream


    /**************************************************************************
     * Function reads the query string parameters of servlet URL. <BR/>
     *
     * @return  <CODE>true</CODE> if everything is all right,
     *          <CODE>false</CODE> if there occurred an error.
     */
    private boolean readQueryString ()
    {
        // create an instance of string tokenizer with "&" as token
        StringTokenizer tokenizer =
            new StringTokenizer (this.queryString, DBFactoryServletConstants.SEPARATOR_PARAM);
        // loop through the tokenizer while tokens exists
        while (tokenizer.hasMoreTokens ())
        {
            // create tokenizer with "=" as token
            StringTokenizer token = new StringTokenizer ((String)tokenizer.nextElement (), DBFactoryServletConstants.SEPARATOR_EQUAL);
            // get a token out of tokenizer
            String paramName = token.nextToken ();
            // check for validity
            if (paramName == null || paramName.equals (DBFactoryServletConstants.BLANK))
            {
                return false;
            } // if
            // get the param value
            String paramValue = this.urlDecode (token.nextToken ());

            // check for validity
            if (paramValue == null || paramValue.equals (DBFactoryServletConstants.BLANK))
            {
                return false;
            } // if
            // replace "+" by " ":
            // This replacement is needed for the case where the parameter value
            // has space like:
            // where=where id < 20 will come to servlet like where=where+id+<+20
            StringHelpers.replace (paramValue,
                DBFactoryServletConstants.SEPARATOR_PLUS, " ");

            // check if it is action
            if (paramName.equals (DBFactoryServletConstants.PARAM_ACTION))
            {
                // set it to action
                this.action = paramValue;
            } // if
            // check if it is id
            else if (paramName.equals (DBFactoryServletConstants.PARAM_ID))
            {
                // set it to id
                this.id = paramValue;
            } // if
            // check if it is cfg
            else if (paramName.equals (DBFactoryServletConstants.PARAM_CFG))
            {
                // set it to exportCfg
                this.exportCfg = paramValue;
            } // if
            // check if it is debug
            else if (paramName.equals (DBFactoryServletConstants.PARAM_DEBUG))
            {
                // check if it is on or off
                if (paramValue.equals (DBFactoryServletConstants.ON))
                {
                    this.isDebug = true;
                } // if
            } // if
            // check if it is verbose
            else if (paramName.equals (DBFactoryServletConstants.PARAM_VERBOSE))
            {
                // check if it is on or off
                if (paramValue.equals (DBFactoryServletConstants.ON))
                {
                    this.isVerbose = true;
                } // if
            } // if
            // check if it is query
            else if (paramName.equals (DBFactoryServletConstants.PARAM_QUERY))
            {
                // query parameter is allowed only if the action is read or write
                // check if we have action
                if (this.isReadOrWriteAction ())
                {
                    this.query = paramValue;
                } // if
                else
                {
                    return false;
                } // else
            } // if
            // check if it is where
            else if (paramName.equals (DBFactoryServletConstants.PARAM_WHERE))
            {
                // where parameter is allowed only if the action is read or write
                // check if we have action
                if (this.isReadOrWriteAction ())
                {
                    this.where = paramValue;
                } // if
                else
                {
                    return false;
                } // else
            } // if
            // check if it is table
            else if (paramName.equals (DBFactoryServletConstants.PARAM_TABLE))
            {
                // table parameter is allowed only if the action is read or write
                // check if we have action
                if (this.isReadOrWriteAction ())
                {
                    this.table = paramValue;
                } // if
                else
                {
                    return false;
                } // else
            } // if
            // check if it is attribute
            else if (paramName.equals (DBFactoryServletConstants.PARAM_ATTRIBUTE))
            {
                // attribute parameter is allowed only if the action is read or write
                // check if we have action
                if (this.isReadOrWriteAction ())
                {
                    this.attribute = paramValue;
                } // if
                else
                {
                    return false;
                } // else
            } // if
            // check if it is config file
            else if (paramName.equals (DBFactoryServletConstants.PARAM_CONFIGFILE))
            {
                // config file parameter is allowed only if the action is dir
                // check if we have action
                if (this.isDirAction ())
                {
                    this.servletConfigFile = paramValue;
                } // if
                else
                {
                    return false;
                } // else
            } // if
            else
            {
                return false;
            } // else
        } // while

        // return true if everything is fine
        return true;
    } // readQueryString


    /**************************************************************************
     * Check if the action is READ or WRITE. <BR/>
     *
     * @return  <CODE>true</CODE> if the action is READ or WRITE,
     *          <CODE>false</CODE> otherwise.
     */
    private boolean isReadOrWriteAction ()
    {
        // query parameter is allowed only if the action is read or write
        // check if we have action
        if (this.action != null && !this.action.equals (DBFactoryServletConstants.BLANK))
        {
            // check if action is read or write:
            if (this.action.equals (DBFactoryServletConstants.ACTION_READ) ||
                this.action.equals (DBFactoryServletConstants.ACTION_WRITE))
            {
                return true;
            } // if
        } // if

        return false;
    } // isReadOrWriteAction


    /**************************************************************************
     * Check if the action is DIR. <BR/>
     *
     * @return  <CODE>true</CODE> if the action is DIR,
     *          <CODE>false</CODE> otherwise.
     */
    private boolean isDirAction ()
    {
        // query parameter is allowed only if the action is read or write
        // check if we have action
        if (this.action != null && !this.action.equals (DBFactoryServletConstants.BLANK))
        {
            // check if action is dir:
            if (this.action.equals (DBFactoryServletConstants.ACTION_DIR))
            {
                return true;
            } // if
        } // if

        return false;
    } // isDirAction


    /**************************************************************************
     * Function parses xml file.
     *
     * @param fileName   Name of the file to be parsed
     *
     * @return  DOM root of the xml file.
     *
     * @throws  XMLReaderException
     *          Thrown if any error occurs while parsing.
     */
    private Document parseFile (String fileName) throws XMLReaderException
    {
        // parse the file and return the resulting document:
        return new XMLReader (fileName, false, null).getDocument ();
    } // parseFile


    /**************************************************************************
     * Function traverses through the DOM tree and retrieves values
     * from DOM and stores in the datamembers.
     *
     * @param doc   DOM tree root
     *
     * @return  true/false
     */
    private boolean traverseDOM (Document doc)
    {
        NodeList nodeList;
        Element root;
        Element node;
        Node attributeNode;
        NamedNodeMap attributes;
        String attributeValue;
        // get the root
        root = doc.getDocumentElement ();
        // check if we have root
        if (root == null)
        {
            return false;
        } // if
        // check the node name of root
        if (!root.getNodeName ().equals (DBFactoryServletConstants.NODE_CONFIG))
        {
            return false;
        } // if
        // get all child node with node name param
        nodeList = root.getElementsByTagName (DBFactoryServletConstants.NODE_PARAM);
        // check if nodes with name param exists
        if (nodeList == null)
        {
            return false;
        } // if
        // loop through the nodelist
        for (int i = 0; i < nodeList.getLength (); i++)
        {
//TODO KR: the cast is a possible cause for an error.
// it should be sure that the item is an element
            // get the first node out of list
            node = (Element) nodeList.item (i);
            // get the attribute list of the node
            attributes = node.getAttributes ();
            // check if we have any attributes
            if (attributes == null)
            {
                return false;
            } // if
            // get the named attribute from attribute list
            attributeNode = attributes.getNamedItem (DBFactoryServletConstants.ATTR_NAME);
            // check if we have attribute node
            if (attributeNode == null)
            {
                return false;
            } // if
            // get the attribute value
            attributeValue = attributeNode.getNodeValue ();
            // check for the validity of the value
            if (attributeValue == null || attributeValue.length () == 0)
            {
                return false;
            } // if
            // check if the value equals to importconfig
            if (attributeValue.equals (DBFactoryServletConstants.ATTR_VALUE_IMPORTCONFIG))
            {
                // read the configfile name values from the child nodes
                if (!this.readConfigFileNames (node, true))
                {
                    return false;
                } // if
            } // if
            // check if the value equals to exportconfig
            else if (attributeValue.equals (DBFactoryServletConstants.ATTR_VALUE_EXPORTCONFIG))
            {
                // read the configfile name values from the child nodes
                if (!this.readConfigFileNames (node, false))
                {
                    return false;
                } // if
            } // if
            // check if the value equals to ipfilter
            else if (attributeValue.equals (DBFactoryServletConstants.ATTR_VALUE_IPFILTER))
            {
                // read the IP Address values from child nodes
                this.readIPValues (node);
            } // if
            // check if the value is logfile
            else if (attributeValue.equals (DBFactoryServletConstants.ATTR_VALUE_LOGFILE))
            {
                // read the log file name from child nodes
                this.readLogFileName (node);
            } // if
            // check if it equals to logging
            else if (attributeValue.equals (DBFactoryServletConstants.ATTR_VALUE_LOGGING))
            {
                // get the logging status
                this.setLogging (node);
            } // if
            // check if it equals maxlogentries
            else if (attributeValue.equals (DBFactoryServletConstants.ATTR_VALUE_MAXLOGENTRIES))
            {
                // get the maximum log entries
                this.setMaxLogEntries (node);
            } // if
            // check if it is workingdir
            else if (attributeValue.equals (DBFactoryServletConstants.ATTR_VALUE_WORKINGDIR))
            {
                // read the working directory name
                this.readWorkingDir (node);
            } // if
            else if (attributeValue.equals (DBFactoryServletConstants.ATTR_VALUE_CONFIGDIR))
            {
                // read the config directory name
                this.readConfigDir (node);
            } // if
            else  // return false if unknown tag occurs
            {
                return false;
            } // else
        } // for i
        // check for the log file name if logging is on
        if (this.isLogging)
        {
            if (this.logFile == null || this.logFile.equals (DBFactoryServletConstants.BLANK))
            {
                return false;
            } // if
        } // if
        // check for the existance of working directory
        if (this.workingDir == null || this.workingDir.equals (DBFactoryServletConstants.BLANK))
        {
            return false;
        } // if
        // check for the existance of config directory
        if (this.configDir == null || this.configDir.equals (DBFactoryServletConstants.BLANK))
        {
            return false;
        } // if
        // return true if everything is fine
        return true;
    } // traverseDOM


    /**************************************************************************
     * Function reads config file names for DBFactory from the
     * DOM tree of DBFactoryServlet config file (xml).
     *
     * @param nodeImpl          node referring to the config file names
     * @param isImportConfig    set true if the node holds the import config files
     *
     * @return  true/false
     */
    public boolean readConfigFileNames (Element nodeImpl, boolean isImportConfig)
    {
        // get the list of nodes with name value
        NodeList nodeList = nodeImpl.getElementsByTagName (DBFactoryServlet.TAG_VALUE);
        // check if we have any nodes
        if (nodeList == null)
        {
            return false;
        } // if

        boolean isConfigOk = false;
        // loop through the nodelist
        for (int i = 0; i < nodeList.getLength (); i++)
        {
            // get the node out of list
            Node node = nodeList.item (i);
            // get the child of the node
            Node firstChild = node.getFirstChild ();
            // check if child exists
            if (firstChild == null)
            {
                continue;
            } // if
            // get the node value
            String fileName = firstChild.getNodeValue ();
            // check for the validity of node value
            if (fileName != null || !fileName.equals (DBFactoryServletConstants.BLANK))
            {
                // add the node value to the config file vector
                if (isImportConfig)
                {
                    this.importConfigFiles.addElement (fileName.trim ());
                } // if
                else
                {
                    this.exportConfigFiles.addElement (fileName.trim ());
                } // else

                isConfigOk = true;
            } // if
        } // for
        // return true if everything is fine
        return isConfigOk;
    } // readConfigFileNames


    /**************************************************************************
     * Function reads the IP addresses from the DOM tree of DBFactoryServlet config file.
     *
     * @param nodeImpl    node referring to IP values
     */
    public void readIPValues (Element nodeImpl)
    {
        // get the list of nodes with name value
        NodeList nodeList = nodeImpl.getElementsByTagName (DBFactoryServlet.TAG_VALUE);
        // check if we have any nodes
        if (nodeList == null)
        {
            return;
        } // if
        // loop through the node list
        for (int i = 0; i < nodeList.getLength (); i++)
        {
            // get a node out of list
            Node node = nodeList.item (i);
            // get the first child of the node
            Node firstChild = node.getFirstChild ();
            // check if we have any child nodes
            if (firstChild == null)
            {
                continue;
            } // if
            // get the node value
            String ipString = firstChild.getNodeValue ();
            // check for the validity
            if (ipString != null || !ipString.equals (DBFactoryServletConstants.BLANK))
            {
                this.ip.addElement (ipString.trim ());
            } // if
        } // for i
    } // readIPValues


    /**************************************************************************
     * Function reads the IP addresses from the DOM tree of DBFactoryServlet
     * config file. <BR/>
     *
     * @param nodeImpl     node referring to Ip addresses
     */
    public void readWorkingDir (Element nodeImpl)
    {
        // get the working directory value:
        String workingDir = this.readValue (nodeImpl);
        // check for validity:
        if (workingDir != null)
        {
            this.workingDir = workingDir;
        } // if
    } // readWorkingDir


    /**************************************************************************
     * Reads the config directory name from the DOM tree of DBFactoryServlet
     * config file. <BR/>
     *
     * @param nodeImpl     node referring to config directory
     */
    public void readConfigDir (Element nodeImpl)
    {
        // get the working directory value
        String dir = this.readValue (nodeImpl);
        // check for validity:
        if (dir != null)
        {
            this.configDir = dir;
        } // if
    } // readConfigDir


    /**************************************************************************
     * Function reads log file name from DOM.
     *
     * @param  nodeImpl   node referring to logfile name
     */
    public void readLogFileName (Element nodeImpl)
    {
        // get the log file name:
        String logFileName = this.readValue (nodeImpl);
        // check for validity:
        if (logFileName != null)
        {
            this.logFile = logFileName;
        } // if
    } // readLogFileName


    /**************************************************************************
     * Function checks for the logging status.
     *
     * @param nodeImpl  node referring to logging status
     */
    public void setLogging (Element nodeImpl)
    {
        // get the log status:
        String logging = this.readValue (nodeImpl);
        // check for validity:
        if (logging != null)
        {
            // check if logging is on:
            if (logging.equals (DBFactoryServletConstants.LOGGING_ON))
            {
                this.isLogging = true;
            } // if
        } // if
    } // setLogging


    /**************************************************************************
     * Function reads maximum log entries value from DOM.
     *
     * @param nodeImpl  node referring to maxlogentries
     */
    public void setMaxLogEntries (Element nodeImpl)
    {
        // get the max entries of logging:
        String maxEntries = this.readValue (nodeImpl);
        // check for validity:
        if (maxEntries != null)
        {
            // convert string to integer:
            this.maxLogEntries = new Integer (maxEntries).intValue ();
        } // if
    } // setMaxLogEntries


    /**************************************************************************
     * Read a value name from DOM. <BR/>
     *
     * @param   nodeImpl    Node referring to the value.
     *
     * @return  The value or
     *          <CODE>null</CODE> if the value does not exist or is empty.
     */
    private String readValue (Element nodeImpl)
    {
        // get the list of nodes
        NodeList nodeList = nodeImpl.getElementsByTagName (DBFactoryServlet.TAG_VALUE);
        // get a node out of list
        Node node = nodeList.item (0);
        // check if we have any node
        if (node == null)
        {
            return null;
        } // if
        // get the first child
        Node firstChild = node.getFirstChild ();
        // check if we any childs
        if (firstChild == null)
        {
            return null;
        } // if
        // get the log file name
        String value = firstChild.getNodeValue ();
        // check fro validity
        if (value.equals (DBFactoryServletConstants.BLANK)) // value is empty?
        {
            value = null;
        } // if value is empty
        else                            // value not empty
        {
            value = value.trim ();
        } // else value not empty

        // return the result:
        return value;
    } // readValue


    /**************************************************************************
     * Function sends error message to servlet response stream.
     *
     * @param   message Error message to be displayed
     *
     * @throws  IOException
     *          Error during accessing the output stream.
     */
    public void sendMessage (String message) throws IOException
    {
        // print the message to servlet response stream
        this.responseStream.print (message);

        this.dbg (message);
    } // sendMessage


    /**************************************************************************
     * Send debugging message to servlet log stream.
     *
     * @param message   Debugging message to be displayed
     */
    public void dbg (String message)
    {
        this.getServletContext ().log (message);
    } // dbg


    /**************************************************************************
     * Function checks for the rights.
     *
     * @return  true/false
     */
    public boolean checkRights ()
    {
        // check if we have any IP addresses read from config file
        if (this.ip.size () == 0)
        {
            // return true if config file doesn't contain any IP
            return true;
        } // if
        // loop through the ip values vector
        for (int i = 0; i < this.ip.size (); i++)
        {
            // get the ip value from vector
            String ipAddress = this.ip.elementAt (i);
             // this.responsestream.println ("ipAddress::"+ipAddress);
            // check if the client got permission or not
            if (ipAddress.equals (this.clientIP))
            {
                return true;
            } // if
        } // for i
        return false;
    } // checkRights


    /**************************************************************************
     * Decodes a url encodes string. <BR/>
     *
     * @param   s   the encoded string
     *
     * @return  The decoded string.
     */
    private String urlDecode (String s)
    {
        if (s == null)
        {
            return s;
        } // if

        String hexTab = "0123456789ABCDEF";
        String res = "";
        int len = s.length ();
        int i = 0;
        while (i < len)
        {
            char c = s.charAt (i);
            switch (c)
            {
                case '%':   // encoded special character
                    i++;
                    if (i >= len)
                    {
                        break;
                    } // if
                    String c1 = "" + s.charAt (i);
                    i++;
                    if (i >= len)
                    {
                        break;
                    } // if
                    String c2 = "" + s.charAt (i);
                    int n1 = hexTab.indexOf (c1.toUpperCase ());
                    int n2 = hexTab.indexOf (c2.toUpperCase ());
                    res += "" + (char) (n1 * 16 + n2);
                    break;
                case '+':   // encoded space
                    res += ' ';
                    break;
                default:
                    res += c;
            } // case (c)
            i++;
        } // while (i < len)
        return res;
    } // urlDecode

} // DBFactoryServlet
