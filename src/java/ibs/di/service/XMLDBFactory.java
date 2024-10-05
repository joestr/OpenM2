/**
 * Class: XMLDBFactory
 */

// package:
package ibs.di.service;

// imports:
import ibs.BaseObject;
import ibs.io.IOConstants;
import ibs.tech.xml.XMLReader;
import ibs.tech.xml.XMLReaderException;
import ibs.tech.xml.XMLWriter;
import ibs.tech.xml.XMLWriterException;
import ibs.util.DateTimeHelpers;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;

import java.io.CharArrayReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.xerces.parsers.SAXParser;
import org.apache.xml.serialize.Method;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;


/******************************************************************************
 * The XMLDBFactory class provides the functionality to read datas from
 * a external database and to store the data it in a XML file. This file
 * conforms with the import DTD from the framework.
 * It also can take a XML file (conform with the framework export DTD) as input
 * and stores the content in a external database.
 *
 * @version     $Id: XMLDBFactory.java,v 1.21 2007/07/31 19:13:55 kreimueller Exp $
 *
 * @author      Michael Steiner (MS), 000823
 ******************************************************************************
 */
public class XMLDBFactory extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: XMLDBFactory.java,v 1.21 2007/07/31 19:13:55 kreimueller Exp $";


    /**
     * trace status informations. <BR/>
     */
    public boolean traceVerbose = false;
    /**
     * trace the configuration. <BR/>
     */
    public boolean traceConfig = false;
    /**
     * trace query informations. <BR/>
     */
    public boolean traceQuery = false;
    /**
     * trace the names of the result columns. <BR/>
     */
    public boolean traceDBFields = false;
    /**
     * trace mapping information. <BR/>
     */
    public boolean traceMapping = false;
    /**
     * Generate the xml output directly instead to use the DOM Document class. <BR/>
     */
    private boolean rawXMLOutput = true;

    /**
     * Table of valid configuration tags and attributes. <BR/>
     * The table is used to simplify the evaluation of the config (xml).
     */
    private static final String[] ATTRIBUTE_TABLE =
    {
        "DATABASE.DRIVER",
        "DATABASE.SOURCE",
        "DATABASE.ENCODING",
        "DATABASE.USER",
        "DATABASE.PWD",
        "LOGFILE",
        "LOGFILE.MODE",
        "DESTINATION",
        "DESTINATION.ENCODING",
        "DESTINATION.MODE",
        "SOURCE",
    };

    // The corresponding indices of the valid configuration tags and attributes.
    // Attention: the order must match with the attributeTable. <BR/>
    /**
     * Index of attribute within the attributeTable: db driver. <BR/>
     */
    private static final int ATTR_DB_DRIVER     = 0;
    /**
     * Index of attribute within the attributeTable: db driver. <BR/>
     */
    private static final int ATTR_DB_SOURCE     = 1;
    /**
     * Index of attribute within the attributeTable: db driver. <BR/>
     */
    private static final int ATTR_DB_ENCODING   = 2;
    /**
     * Index of attribute within the attributeTable: db user name. <BR/>
     */
    private static final int ATTR_DB_USER       = 3;
    /**
     * Index of attribute within the attributeTable: db password. <BR/>
     */
    private static final int ATTR_DB_PWD        = 4;
    /**
     * Index of attribute within the attributeTable: log file. <BR/>
     */
    private static final int ATTR_LOGFILE       = 5;
    /**
     * Index of attribute within the attributeTable: log file mode. <BR/>
     */
    private static final int ATTR_LOGFILE_MODE  = 6;
    /**
     * Index of attribute within the attributeTable: dest. <BR/>
     */
    private static final int ATTR_DEST          = 7;
    /**
     * Index of attribute within the attributeTable: dest encoding. <BR/>
     */
    private static final int ATTR_DEST_ENCODING = 8;
    /**
     * Index of attribute within the attributeTable: dest mode. <BR/>
     */
    private static final int ATTR_DEST_MODE     = 9;
    /**
     * Index of attribute within the attributeTable: source. <BR/>
     */
    private static final int ATTR_SOURCE        = 10;

    /**
     * The type attribute for the OBJECT/SUBIMPORT/SUBEXPORT node. <BR/>
     */
    private static final String M2_IMPORT_TAG = "IMPORT";
    /**
     * The name of the objects tag in the m2 xml file. <BR/>
     */
    private static final String M2_OBJECTS_TAG = "OBJECTS";
    /**
     * The name of the object tag in the m2 xml file. <BR/>
     */
    private static final String M2_OBJECT_TAG = "OBJECT";
    /**
     * The name of the system tag in the m2 xml file. <BR/>
     */
    private static final String M2_SYSTEM_TAG = "SYSTEM";
    /**
     * The name of the values tag in the m2 xml file. <BR/>
     */
    private static final String M2_VALUES_TAG = "VALUES";
    /**
     * The name of the value tag in the m2 xml file. <BR/>
     */
    private static final String M2_VALUE_TAG = "VALUE";
    /**
     * The name of the id tag in the m2 xml file. <BR/>
     */
    private static final String M2_ID_TAG = "ID";
    /**
     * The name of the domain attribute in the m2 xml file. <BR/>
     */
    private static final String M2_DOMAIN_ATTR = "DOMAIN";
    /**
     * The name of the field attribute in the m2 xml file. <BR/>
     */
    private static final String M2_FIELD_ATTR = "FIELD";
    /**
     * The name of the type code attribute in the m2 xml file. <BR/>
     */
    private static final String M2_TYPECODE_ATTR = "TYPECODE";
    /**
     * The name of the type attribute (for values) in the m2 xml file. <BR/>
     */
    private static final String M2_VALUETYPE_ATTR = "TYPE";
    /**
     * The name of the object tag in the configuration. <BR/>
     */
    private static final String FACTORY_OBJECT_TAG = XMLDBFactory.M2_OBJECT_TAG;
    /**
     * The name of the import tag in the configuration. <BR/>
     */
    private static final String FACTORY_IMPORT_TAG = XMLDBFactory.M2_IMPORT_TAG;
    /**
     * The name of the export tag in the configuration. <BR/>
     */
    private static final String FACTORY_EXPORT_TAG = "EXPORT";
    /**
     * The name of the subimport tag in the configuration. <BR/>
     */
    private static final String FACTORY_SUBIMPORT_TAG = "SUBIMPORT";
    /**
     * The name of the subexport tag in the configuration. <BR/>
     */
    private static final String FACTORY_SUBEXPORT_TAG = "SUBEXPORT";
    /**
     * The name of the query tag in the configuration. <BR/>
     */
    private static final String FACTORY_QUERY_TAG = "QUERY";
    /**
     * The name of the prodecure tag in the configuration. <BR/>
     */
    private static final String FACTORY_PROCEDURE_TAG = "PROCEDURE";
    /**
     * The transaction attribute for the IMPORT node. <BR/>
     */
    private static final String FACTORY_TRANSACTION_ATTR = "TRANSACTION";
    /**
     * File mode new: create a new file. <BR/>
     */
    private static final int FILE_MODE_NEW = 0;
    /**
     * File mode append: append to the existing file. <BR/>
     */
    private static final int FILE_MODE_APPEND = 1;
    /**
     * File mode timestamp: append to the filename a timestamp. <BR/>
     */
    private static final int FILE_MODE_TIMESTAMP = 2;
    /**
     * Class name of the jdbc driver class. <BR/>
     */
    private String jdbcDriver = "";
    /**
     * Datasource for the jdbc connection. <BR/>
     */
    private String jdbcSource = "";
    /**
     * Encoding used in the database. <BR/>
     */
    private String jdbcEncoding = "";
    /**
     * User name for the jdbc connection. <BR/>
     */
    private String jdbcUser = "";
    /**
     * Password for the jdbc connection. <BR/>
     */
    private String jdbcPwd = "";
    /**
     * The jdbc connection object. <BR/>
     */
    private Connection jdbcConnection = null;
    /**
     * Name of the logging file. <BR/>
     */
    private String logFile = "";
    /**
     * Logging mode. <BR/>
     */
    private int logMode = XMLDBFactory.FILE_MODE_NEW;
    /**
     * The output stream for logging messages. <BR/>
     */
    private OutputStream logStream = null;
    /**
     * The configuration (xml) used for import/export. <BR/>
     */
    private String configString = "";
    /**
     * Name of the logging file. <BR/>
     */
    private String destFile = "";
    /**
     * Mode for output file. <BR/>
     */
    private int destMode = XMLDBFactory.FILE_MODE_NEW;
    /**
     * Encoding for the xml output. <BR/>
     */
    private String destEncoding = "";
    /**
     * The output stream for the xml output file. <BR/>
     */
    private OutputStream destStream = null;
    /**
     * Name of the xml input file. <BR/>
     */
    private String srcFile = "";
    /**
     * The input stream for the xml input file. <BR/>
     */
    private InputStream srcStream = null;
    /**
     * The node of the "OBJECT"-tag in the configuration file. <BR/>
     */
    private Node objectRoot = null;
    /**
     * The generated DOM document. <BR/>
     */
    private Document document = null;
    /**
     * The node where to insert new items in the document. <BR/>
     */
    private Node insertionPoint = null;
    /**
     * The serializer object used to write the xml output. <BR/>
     */
    private XMLSerializer serializer = null;
    /**
     * True if the import is performed in a single transaction. <BR/>
     */
    private boolean importTrans = false;
    /**
     * True if the export is performed in a single transaction. <BR/>
     */
    private boolean exportTrans = false;
    /**
     * Holds the number of imported top level objects. <BR/>
     */
    private int importCounter = 0;
    /**
     * Holds the number of exported top level objects. <BR/>
     */
    private int exportCounter = 0;
    /**
     * Holds the number of exported and commited top level objects. <BR/>
     */
    private int exportCommitedCounter = 0;
    /**
     * Holds the export mapping lists for the object types to export. <BR/>
     */
    private Hashtable<String, MappingInfo> mappingHash =
        new Hashtable<String, MappingInfo> ();
    /**
     * Holds the stored procedures for the object types export. <BR/>
     */
    private Hashtable<String, String> procedureHash =
        new Hashtable<String, String> ();

    /**
     * Error message postfix: export mapping for object type is invalid. <BR/>
     * The message contains {@link UtilConstans#TAG_NAME} to be replaced by the
     * object name.
     */
    private static final String ERRM_EXPMAPPING_INVALID =
        "The export mapping for object type '" +
        UtilConstants.TAG_NAME + "' is invalid.";
    /**
     * Error message postfix: stored procedure for object type is invalid. <BR/>
     * The message contains {@link UtilConstans#TAG_NAME} to be replaced by the
     * object name.
     */
    private static final String ERRM_STOREDPROC_INVALID =
        StringHelpers
        .replace ("The stored procedure for object type '<X>' is invalid.",
            "<X>", UtilConstants.TAG_NAME);

    /**
     * Transaction mode: YES. <BR/>
     */
    private static final String TRANS_MODE_YES = "YES";

    /**
     * Predefined value: NEW. <BR/>
     */
    private static final String VAL_NEW = "NEW";
    /**
     * Predefined value: TIMESTAMP. <BR/>
     */
    private static final String VAL_TIMESTAMP = "TIMESTAMP";



    /**************************************************************************
     * This helper class implements a hash table for the variable/value
     * pairs needed to subtitute the variables in the database querys. <BR/>
     *
     * @version     $Id: XMLDBFactory.java,v 1.21 2007/07/31 19:13:55 kreimueller Exp $
     *
     * @author      Michael Steiner (MS), 000823
     **************************************************************************
     */
    class QueryValueHash extends Hashtable<String, String>
    {
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
        static final long serialVersionUID = 1050805142574111370L;


        /**********************************************************************
         * Maps the specified <CODE>key</CODE> to the specified
         * <CODE>value</CODE> in this hashtable. Neither the key nor the
         * value can be <CODE>null</CODE>. <BR/>
         * The key is case insensitive. <BR/>
         *
         * The value can be retrieved by calling the <CODE>get</CODE> method
         * with a key that is equal to the original key.
         *
         * @param   key     The hashtable key.
         * @param   value   The value.
         *
         * @return  The previous value of the specified key in this hashtable,
         *          or <code>null</code> if it did not have one.
         *
         * @throws  NullPointerException
         *          The exception which is thrown by {@link Hashtable#put}
         */
        public String put (String key, String value) throws NullPointerException
        {
//            traceln ("HASH: <" + key +"> <" + value + ">");
            return super.put (key.toLowerCase (), value);
        } // put


        /**********************************************************************
         * Returns the value to which the specified key is mapped in this
         * hashtable. <BR/>
         * The key is case insensitive.<P>
         *
         * @param   key   A key in the hashtable.
         *
         * @return  the value to which the key is mapped in this hashtable;
         *          <CODE>null</CODE> if the key is not mapped to any value in
         *          this hashtable.
         */
        public Object get (String key)
        {
            return super.get (key.toLowerCase ());
        } // get
    } // QueryValueHash


    /**************************************************************************
     * Sets and checks the configuration (xml). <BR/>
     * If a logging stream is given the logging options in the
     * configuration are ignored.
     *
     * @param   config      The configuration (xml)
     * @param   logStream   The OutputStream for logging informations
     *
     * @return  false on error, otherwise true.
     */
    public boolean setConfiguration (String config, OutputStream logStream)
    {
        try
        {
            // clear the current configuration
            this.clearConfiguration ();
            // store the configuration and log stream
            this.configString = config;
            this.logStream = logStream;
            // read the configuration
            this.readConfiguration ();
            // check if the configuration is valid
            this.checkConfiguration ();
            return true;
        } // try
        catch (XMLReaderException e)
        {
            this.showError (e);
            return false;
        } // catch
        catch (Exception e)
        {
            this.showError (e);
            return false;
        } // catch
    } // setConfiguration


    /**************************************************************************
     * Performs the import from the database and writes the xml output to the
     * output stream. <BR/>
     *
     * @param   paramVector The optionaly values for the variables in the import query.
     * @param   outStream   The optionaly output stream for the xml output.
     *
     * @return  false on error, otherwise true.
     */
    public boolean performImport (Vector<String> paramVector, OutputStream outStream)
    {
        // do not use the DOM document class on import.
        this.rawXMLOutput = true;
        return this.doImport (paramVector, outStream);
    } // performImport


    /**************************************************************************
     * Performs the import from the database and returns the DOM document. <BR/>
     *
     * @param   paramVector     The optionaly values for the variables in
     *                          the import query.
     *
     * @return  the DOM document or <CODE>null</CODE> on error.
     */
    public Document performImport (Vector<String> paramVector)
    {
        // use the DOM document class for import.
        this.rawXMLOutput = false;

        if (this.doImport (paramVector, null))
        {
            return this.document;
        } // if

        return null;
    } // performImport


    /**************************************************************************
     * Performs the export. <BR/>
     *
     * @param   validate    Activate the validation of the xml file.
     * @param   inStream    The optionaly input stream for the xml file.
     *
     * @return  false on error, otherwise true.
     */
    public boolean performExport (boolean validate, InputStream inStream)
    {
        try
        {
            // check if the configuration is valid
            this.checkConfiguration ();
            // open the database connection
            this.connect ();
            // start the export
            this.doExport (validate, inStream);
        } // try
        catch (Exception e)
        {
            this.showError (e);
            return false;
        } // catch
        finally
        {
            // close database connection
            this.disconnect ();
        } // finally
        return true;
    } // performExport


    /**************************************************************************
     * Performs the import. <BR/>
     *
     * @param   paramVector The optionaly values for the variables in the import query.
     * @param   outStream   The optionaly output stream for the xml output.
     *
     * @return  false on error, otherwise true.
     */
    private boolean doImport (Vector<String> paramVector, OutputStream outStream)
    {
        try
        {
            // check if the configuration is valid
            this.checkConfiguration ();

            // open the database connection
            this.connect ();

            // hash table for the query parameters
            QueryValueHash hash = new QueryValueHash ();

            // get the optional values for the query parameters.
            // the format is parameter=value.
            if (paramVector != null)
            {
                for (int i = 0; i < paramVector.size (); i++)
                {
                    String s = paramVector.elementAt (i);

                    if (this.traceVerbose)
                    {
                        this.traceln ("PARAMETER: " + s);
                    } // if

                    // get the position of the separator '='
                    int pos = s.indexOf ("=");
                    // on separator not found: error
                    if (pos <= 0)
                    {
                        this.error ("Invalid query parameter: " + s);
                    } // if
                    // insert the param/value pair in the hash table
                    hash.put (s.substring (0, pos), s.substring (pos + 1));
                } // for i
            } // if (paramVector != null)

            // reset the import counter
            this.importCounter = 0;

            // start the import
            this.doImport (hash, outStream);

            // report the number of imported objects.
            this.traceln (this.importCounter + " top level object(s) imported.");
        } // try
        catch (Exception e)
        {
            try
            {
                this.traceln (e.getMessage ());
            } // try
            catch (Exception e2)
            {
                System.err.println (e.getMessage ());
                System.err.println (e2.getMessage ());
            } // catch
            return false;
        } // catch
        finally
        {
            // close database connection
            this.disconnect ();
        } // finally
        return true;
    } // doImport


    /**************************************************************************
     * Checks if all configuration parameters are valid. <BR/>
     *
     * @throws  Exception
     *          One of the checks were not successful.
     */
    private void checkConfiguration () throws Exception
    {
        if (this.configString == null || this.configString.length () == 0)
        {
            this.error ("Configuration missing!");
        } // if

        // check all jdbc parameters
        if (this.jdbcDriver.length () == 0)
        {
            this.error ("JDBC driver missing!");
        } // if
        if (this.jdbcSource.length () == 0)
        {
            this.error ("JDBC source missing!");
        } // if
        if (this.jdbcEncoding.length () == 0)
        {
            this.error ("JDBC encoding missing!");
        } // if
        if (this.jdbcUser.length () == 0)
        {
            this.error ("JDBC user name missing!");
        } // if

        if (this.objectRoot == null)
        {
            this.error ("Import/Export information missing!");
        } // if
    } // checkConfiguration


    /**************************************************************************
     * Clears all configuration parameters. <BR/>
     */
    private void clearConfiguration ()
    {
        this.rawXMLOutput = true;

        this.jdbcDriver = "";
        this.jdbcSource = "";
        this.jdbcEncoding = "";
        this.jdbcUser = "";
        this.jdbcPwd = "";
        this.jdbcConnection = null;

        this.logFile = "";
        this.logMode = XMLDBFactory.FILE_MODE_NEW;
        this.logStream = null;

        this.configString = "";

        this.destFile = "";
        this.destEncoding = "";
        this.destMode = XMLDBFactory.FILE_MODE_NEW;
        this.destStream = null;

        this.srcFile = "";
        this.srcStream = null;

        this.objectRoot = null;
        this.document = null;
        this.insertionPoint = null;
        this.serializer = null;

        this.importTrans = false;
        this.exportTrans = false;
        this.importCounter = 0;
        this.exportCounter = 0;
        this.exportCommitedCounter = 0;

        this.mappingHash = new Hashtable<String, MappingInfo> ();
        this.procedureHash = new Hashtable<String, String> ();
    } // clearConfiguration


    /**************************************************************************
     * Generates a error message if the given condition is not true.
     * This method is used like <CODE>assert(string != null)</CODE>. <BR/>
     *
     * @param   condition   the condition to test
     *
     * @throws  Exception
     *          An error occurred.
     */
    static void doAssert (boolean condition) throws Exception
    {
        if (!condition)
        {
            Exception e = new Exception ("Assertion failed!");
            e.printStackTrace ();
            throw e;
        } // if (!condition)
    } // doAssert


    /**************************************************************************
     * Opens a file with the given name and mode. <BR/>
     *
     * @param   filename    the file name
     * @param   mode        the mode for the file (see the constants FILE_MODE_NEW ...)
     *
     * @return  the stream of the opened file
     *
     * @throws  Exception
     *          An exception occurred.
     */
    private FileOutputStream openOutputFile (String filename, int mode)
        throws Exception
    {
        try
        {
            // append a timestamp to the file name
            if (mode == XMLDBFactory.FILE_MODE_TIMESTAMP)
            {
                String filenameLocal = filename; // variable for local assignments
                String extension = "";
                int dotPos = filenameLocal.lastIndexOf ('.');
                Date date = new Date ();

                if (dotPos > 0)
                {
                    extension = filenameLocal.substring (dotPos);
                    filenameLocal = filenameLocal.substring (0, dotPos);
                } // if (dotPos > 0)

                // add the actual date/time to the file name:
                filenameLocal += "_" +
                    DateTimeHelpers.dateTimeToString (date, "yyyyMMdd-hh-mm") +
                    extension;
            } // if (mode == FILE_MODE_TIMESTAMP)
            return new FileOutputStream (filename, mode == XMLDBFactory.FILE_MODE_APPEND);
        } // try
        catch (IOException e)
        {
            this.error (e.getMessage ());
            return null;
        } // catch
    } // openOutputFile


    /**************************************************************************
     * Writes a message with a appended linefeed in the log file. <BR/>
     *
     * @param   msg     the message to write in the log file
     *
     * @throws  Exception
     *          An exception occurred while trying to send the message.
     */
    private void traceln (String msg) throws Exception
    {
        this.sendTrace (msg + "\n");
    } // traceln


    /**************************************************************************
     * Writes a message in the log file. <BR/>
     *
     * @param   msg     the message to write in the log file
     *
     * @throws  Exception
     *          An exception occurred while trying to send the message.
     */
    private void sendTrace (String msg) throws Exception
    {
        try
        {
            // Open the log stream if not already open
            if (this.logStream == null)
            {
                // if we have a filename we use it to open the file
                // otherwise we use the stderr stream.
                if (this.logFile != null && this.logFile.length () > 0)
                {
                    this.logStream = this.openOutputFile (this.logFile, this.logMode);
                } // if
                else
                {
                    this.logStream = System.err;
                } // else
            } // if (this.logStream == null)

            // write the message
            this.logStream.write (msg.getBytes ());
        } // try
        catch (IOException e)
        {
            this.error (e.getMessage ());
        } // catch
    } // sendTrace


    /**************************************************************************
     * Display an exception. <BR/>
     *
     * @param   e       The exception to be displayed.
     */
    private void showError (Throwable e)
    {
        try
        {
            this.traceln (e.getMessage ());
        } // try
        catch (Exception e2)
        {
            System.err.println (e.getMessage ());
            System.err.println (e2.getMessage ());
        } // catch
    } // showError


    /**************************************************************************
     * Traces the error msg and throws a Exception. <BR/>
     *
     * @param   msg     the error message
     *
     * @throws  Exception
     *          The exception which is thrown.
     */
    private void error (String msg) throws Exception
    {
//        new Exception ().printStackTrace ();
//        System.err.println (msg);
        throw new Exception ("ERROR: " + msg);
    } // error


    /**************************************************************************
     * Connects to the database. <BR/>
     *
     * @throws  Exception
     *          An exception occurred while connecting.
     */
    private void connect () throws Exception
    {
        try
        {
            // load the driver class
            Class.forName (this.jdbcDriver);
//            DriverManager.setLogStream (new PrintStream (this.logStream));
            this.jdbcConnection = java.sql.DriverManager.getConnection (
                                this.jdbcSource, this.jdbcUser, this.jdbcPwd);
        } // try
        catch (Exception e)
        {
            this.error ("Connect error:" + e.getMessage ());
        } // catch
    } // connect


    /**************************************************************************
     * Close the connection to the database. <BR/>
     */
    private void disconnect ()
    {
        try
        {
            // if we have a connection object we close the connection
            if (this.jdbcConnection != null)
            {
                this.jdbcConnection.close ();
                this.jdbcConnection = null;
            } // if
        } // try
        catch (Exception e)
        {
            // should not occur
        } // catch
    } // disconnect


    /**************************************************************************
     * Reads the configuration (xml) and stores the information
     * in instance variables. <BR/>
     *
     * @throws  XMLReaderException
     *          An exception occurred during parsing.
     * @throws  Exception
     *          Any other exception.
     */
    private void readConfiguration () throws XMLReaderException, Exception
    {
        // read the document:
        // validate the document
        Document doc = new XMLReader (new CharArrayReader (this.configString
            .toCharArray ()), true, null).getDocument ();
        Element root = doc.getDocumentElement ();
        // get the node list
        NodeList nodeList = root.getChildNodes ();
        // walk thru the node list and extract all configuration
        // parameters.
        int nodes = nodeList.getLength ();
        for (int i = 0; i < nodes; i++)
        {
            this.storeAttributes (nodeList.item (i));
        } // for i

        // if the object root node is valid
        if (this.objectRoot != null)
        {
            Node importNode = this.getNodeByName (this.objectRoot,
                XMLDBFactory.FACTORY_IMPORT_TAG);
            if (importNode != null)
            {
                // the IMPORT node holds the transaction attribute for the import.
                // if the attribute is set to "YES" the entire import
                // is performed in a single transaction. in this case we disable
                // the auto commit mode of the jdbc driver. the import process must
                // take care of the transaction management.
                // if the attribute is set to "OBJECT" any query is a transaction.
                // in this case we enable the auto commit mode.
                String mode = this.getAttributeValue (importNode,
                    XMLDBFactory.FACTORY_TRANSACTION_ATTR);

                // the default value is 'YES'.
                if (mode == null || mode.equals (XMLDBFactory.TRANS_MODE_YES))
                {
                    // the import is performed in a transaction.
                    this.importTrans = true;
                } // if
                else if (mode.equals (XMLDBFactory.M2_OBJECT_TAG))
                {
                    // any import query is performed in a transaction.
                    this.importTrans = false;
                } // else if
                else
                {
                    // the mode must be 'YES' or 'OBJECT'.
                    this.error ("Invalid import transaction mode: " + mode);
                } // else
                if (this.traceConfig)
                {
                    this.traceln ("IMPORT TRANSACTION MODE: " + mode);
                } // if
            } // if we found the IMPORT node

            Node exportNode = this.getNodeByName (this.objectRoot,
                XMLDBFactory.FACTORY_EXPORT_TAG);
            if (exportNode != null)
            {
                // the EXPORT node holds the transaction attribute for the export.
                // if the attribute is set to "YES" the entire export is performed
                // as a single transaction. if the attribute is set to "OBJECT"
                // any single Object (and his sub objects) are stored in a single
                // transaction.
                // in any case we have to disable the auto commit mode of the
                // jdbc driver and the export process must take care of the
                // transaction management.
                String mode = this.getAttributeValue (exportNode,
                    XMLDBFactory.FACTORY_TRANSACTION_ATTR);

                // the default value is 'YES'.
                if (mode == null || mode.equals (XMLDBFactory.TRANS_MODE_YES))
                {
                    // the export is performed in a transaction.
                    this.exportTrans = true;
                } // if
                else if (mode.equals (XMLDBFactory.M2_OBJECT_TAG))
                {
                    // the export of any top level object is performed in a transaction.
                    this.exportTrans = false;
                } // else if
                else
                {
                    // the mode must be 'YES' or 'OBJECT'.
                    this.error ("Invalid export transaction mode: " + mode);
                } // else
                if (this.traceConfig)
                {
                    this.traceln ("EXPORT TRANSACTION MODE: " + mode);
                } // if
            } // if we found the IMPORT node
        } // if the OBJECT node ist valid
    } // readConfiguration

    /**************************************************************************
     * Searchs the node name in the attribute table and returns the
     * corresponding index. <BR/>
     *
     * @param   nodeName    the name of the node to search
     *
     * @return  the index of the node name in the attribute table or
     *          -1 if the node name is not found in the table. <BR/>
     */
    private int nodeNameToIndex (String nodeName)
    {
        for (int i = 0; i < XMLDBFactory.ATTRIBUTE_TABLE.length; i++)
        {
            if (XMLDBFactory.ATTRIBUTE_TABLE[i].equals (nodeName))
            {
                return i;
            } // if
        } // for i
        // node name not found!
        return -1;
    } // nodeNameToIndex


    /**************************************************************************
     * Stores the given value in the corresponding instance variable. <BR/>
     *
     * @param   attrIndex   the number of the instance variable.
     * @param   value       the value to assign.
     *
     * @throws  Exception
     *          An exception occurred during storing the value.
     */
    private void storeAttributeValue (int attrIndex, String value)
        throws Exception
    {
        // if the index ist valid we trace the attribute and value.
        if (this.traceConfig && attrIndex >= 0)
        {
            // tracing the password is not a good idea
            if (attrIndex != XMLDBFactory.ATTR_DB_PWD)
            {
                this.traceln (XMLDBFactory.ATTRIBUTE_TABLE[attrIndex] + ": " + value);
            } // if
        } // if valid attribute index

        // select the instance variable according to the given index
        // and store the value.
        switch (attrIndex)
        {
            case XMLDBFactory.ATTR_DB_DRIVER:
                this.jdbcDriver = value;
                break;
            case XMLDBFactory.ATTR_DB_SOURCE:
                this.jdbcSource = value;
                break;
            case XMLDBFactory.ATTR_DB_ENCODING:
                this.jdbcEncoding = value;
                break;
            case XMLDBFactory.ATTR_DB_USER:
                this.jdbcUser = value;
                break;
            case XMLDBFactory.ATTR_DB_PWD:
                this.jdbcPwd = value;
                break;
            case XMLDBFactory.ATTR_LOGFILE:
                this.logFile = value;
                break;
            case XMLDBFactory.ATTR_LOGFILE_MODE:
                // set the file mode for the log file
                this.logMode = -1;
                if (value == null || value.equals (XMLDBFactory.VAL_NEW))
                {
                    this.logMode = XMLDBFactory.FILE_MODE_NEW;
                } // if
                if (value.equals ("APPEND"))
                {
                    this.logMode = XMLDBFactory.FILE_MODE_APPEND;
                } // if
                if (value.equals (XMLDBFactory.VAL_TIMESTAMP))
                {
                    this.logMode = XMLDBFactory.FILE_MODE_TIMESTAMP;
                } // if
                if (this.logMode == -1)
                {
                    this.error ("Invalid log file mode: " + value);
                } // if
                break;
            case XMLDBFactory.ATTR_DEST:
                this.destFile = value;
                break;
            case XMLDBFactory.ATTR_DEST_ENCODING:
                this.destEncoding = value;
                break;
            case XMLDBFactory.ATTR_DEST_MODE:
                // set the file mode for the destination file
                this.destMode = -1;
                if (value == null || value.equals (XMLDBFactory.VAL_NEW))
                {
                    this.destMode = XMLDBFactory.FILE_MODE_NEW;
                } // if
                if (value.equals (XMLDBFactory.VAL_TIMESTAMP))
                {
                    this.destMode = XMLDBFactory.FILE_MODE_TIMESTAMP;
                } // if
                if (this.destMode == -1)
                {
                    this.error ("Invalid destination file mode: " + value);
                } // if
                break;
            case XMLDBFactory.ATTR_SOURCE:
                this.srcFile = value;
                break;
            default:
                // a invalid attribute was found in the configuration file.
                this.error ("Invalid attribute value in configuration: " + value);
        } // switch (attrIndex)
    } // storeAttributeValue


    /**************************************************************************
     * Stores the text and attribute values of a document node from the
     * configuration file in member variables. <BR/>
     *
     * @param   node        the document node
     *
     * @throws  Exception
     *          An exception occurred.
     */
    private void storeAttributes (Node node) throws Exception
    {
        // get the node name
        String nodeName = node.getNodeName ();

        // if the node is the OBJECT node we store it for further use.
        // this node holds all the mapping information for import/export.
        if (nodeName.equals (XMLDBFactory.FACTORY_OBJECT_TAG))
        {
            this.objectRoot = node;
            return;
        } // if OBJECT tag
        // get the index of the node name in the attribute table.
        // if the index ist valid (a known name) we can assign the
        // node value to the corresponding member variable.
        int idx = this.nodeNameToIndex (nodeName);
        if (idx >= 0)
        {
            this.storeAttributeValue (idx, this.getNodeText (node));
        } // if (idx >= 0)

        // the node can hold also attributs.
        // get all attributs and store it in instance variables.
        NamedNodeMap attrMap = node.getAttributes ();
        if (attrMap != null)
        {
            int size = attrMap.getLength ();
            for (int i = 0; i < size; i++)
            {
                Node attr = attrMap.item (i);
                // the attribute name is composed from the node name
                // a dot and the name of the attribute node.
                String attrName = nodeName + "." + attr.getNodeName ();
                // get the index of the attribute and store the value.
                this.storeAttributeValue (this.nodeNameToIndex (attrName), attr.getNodeValue ());
            } // for i<size
        } // if (attrMap != null)
    } // storeAttributes


    /**************************************************************************
     * Returns the text of the given document node. <BR/>
     *
     * @param   node    the document node
     *
     * @return  the text of the node ot <CODE>null</CODE> if the node has no text.
     */
    private String getNodeText (Node node)
    {
        // normalize the node to ensure that all text values are together.
        node.normalize ();
        Text text = (Text) node.getFirstChild ();
        if (text != null)
        {
            return text.getNodeValue ();
        } // if
        return null;
    } // getNodeText


    /**************************************************************************
     * Returns the value of the given node and attribute. <BR/>
     *
     * @param   node        the document node
     * @param   attrName    the name of the attribute
     *
     * @return  the value of the attribute or <CODE>null</CODE> if the attribute
     *          not exists.
     */
    private String getAttributeValue (Node node, String attrName)
    {
        NamedNodeMap attrMap = node.getAttributes ();
        if (attrMap != null)
        {
            Node attrNode = attrMap.getNamedItem (attrName);
            if (attrNode != null)
            {
                return attrNode.getNodeValue ();
            } // if
        } // if
        return null;
    } // getAttributeValue


    /**************************************************************************
     * This helper class holds the mapping information for a single
     * configuration XMLTAG in the configuration file. <BR/>
     *
     * @version     $Id: XMLDBFactory.java,v 1.21 2007/07/31 19:13:55 kreimueller Exp $
     *
     * @author      Michael Steiner (MS), 000823
     **************************************************************************
     */
    class MappingInfo
    {
        /**
         * The unique key name for the field. <BR/>
         * For example: SYSTEM.ID, SYSTEM.DOMAIN, FIELD.Name, ...
         */
        String fieldKey = null;
        /**
         * The TYPE attribute of the configuration tag. <BR/>
         */
        String type = null;
        /**
         * The value of the configuration tag. <BR/>
         */
        String value = null;
        /**
         * The extracted field name of the NAME attribute. <BR/>
         */
        String fieldName = null;
        /**
         * True if the value spezifies a database column. <BR/>
         */
        boolean isDatabaseColumn = false;
        /**
         * True if the configuration tag describes a tag in the SYSTEM section. <BR/>
         */
        boolean inSystemSection = false;
        /**
         * The configuration tag decribes a tag in the VALUES section. <BR/>
         */
        boolean inValuesSection = false;
        /**
         * Import node of the embedded object. <BR/>
         */
        Node subimportNode = null;
        /**
         * Next object in the list. <BR/>
         */
        MappingInfo next;
    } // class MappingInfo


    /**************************************************************************
     * Returns a list of objects with the mapping information of all xml tags
     * definded in the given IMPORT/SUBIMPORT or EXPORT/SUBEXPORT node. <BR/>
     *
     * @param   importNode      The IMPORT/SUBIMPORT/EXPORT/SUBEXPORT node
     *                          from the configuration.
     * @param   importMapping   <CODE>true</CODE> for import nodes,
     *                          otherwise <CODE>false</CODE>.
     * @param   rs              The result set of the import query for import
     *                          nodes, <CODE>null</CODE> for export nodes.
     *
     * @return  A list with the mapping information
     *
     * @throws  Exception
     *          An exception occurred.
     */
    private MappingInfo getMappingList (Node importNode, boolean importMapping,
                                        ResultSet rs) throws Exception
    {
        if (importMapping)
        {
            // the node must be a IMPORT or SUBIMPORT node.
            XMLDBFactory.doAssert (importNode.getNodeName ()
                .equals (XMLDBFactory.FACTORY_IMPORT_TAG) ||
                importNode.getNodeName ().equals (XMLDBFactory.FACTORY_SUBIMPORT_TAG));

            // the result set must be valid
            XMLDBFactory.doAssert (rs != null);
        } // if (importMapping)
        else
        {
            // the node must be a EXPORT or SUBEXPORT node.
            XMLDBFactory.doAssert (importNode.getNodeName ()
                .equals (XMLDBFactory.FACTORY_EXPORT_TAG) ||
                importNode.getNodeName ().equals (XMLDBFactory.FACTORY_SUBEXPORT_TAG));

            // the result set must be null
            XMLDBFactory.doAssert (rs == null);
        } // else if (importMapping)

        MappingInfo mappingList = null;
        MappingInfo lastItem = null;

        // get the list of children of the import/export node.
        // in the SYSTEM and VALUES nodes we find the mapping information.
        NodeList nodeList = importNode.getChildNodes ();
        if (nodeList != null)
        {
            // walk thru the node list
            int size = nodeList.getLength ();
            for (int i = 0; i < size; i++)
            {
                // get the node from the list
                Node node = nodeList.item (i);
                // get the node name
                String nodeName = node.getNodeName ();
                //  nodes holds the mapping information.
                if (nodeName != null)
                {
                    // vaiable for the new mapping object
                    MappingInfo mappingInfo = null;
                    // if the node is the SYSTEM tag we get the information now.
                    if (nodeName.equals (XMLDBFactory.M2_SYSTEM_TAG) ||
                        nodeName.equals (XMLDBFactory.M2_VALUES_TAG))
                    {
                        mappingInfo = this.getSectionMapping (node, rs);
                    } // if SYSTEM or VALUES node
                    // if the node is a SUBIMPORT tag we get the information later.
                    else if (nodeName.equals (XMLDBFactory.FACTORY_SUBIMPORT_TAG))
                    {
                        // store the root node of the embedded object.
                        // the evaluation will by done later.
                        mappingInfo = new MappingInfo ();
                        mappingInfo.subimportNode = node;
                    } // if the node name is SUBIMPORT
                    else if (nodeName.equals (XMLDBFactory.FACTORY_SUBEXPORT_TAG))
                    {
                        // subexport tags are ignored for now.
                    } // else if

                    // append the new object/list to the list.
                    if (mappingInfo != null)
                    {
                        if (lastItem == null)
                        {
                            mappingList = mappingInfo;
                        } // if
                        else
                        {
                            lastItem.next = mappingInfo;
                        } // else
                        lastItem = mappingInfo;
                        // get the last element in the list
                        while (lastItem.next != null)
                        {
                            lastItem = lastItem.next;
                        } // while
                    } // if the info is valid
                } // if the node has a name
            } // for i
        } // if (childNode != null)

        return mappingList;
    } // getMappingList


    /**************************************************************************
     * Returns a list of objects with the mapping information of all xml tags
     * definded in the given SYSTEM/VALUES node from the configuration. <BR/>
     *
     * @param   sectionNode     the SYSTEM or VALUES node from the configuration.
     * @param   rs              the result set of the import query.
     *
     * @return  a list with the mapping information
     *
     * @throws  Exception
     *          An exception occurred while setting the mapping.
     */
    private MappingInfo getSectionMapping (Node sectionNode, ResultSet rs)
        throws Exception
    {
        // the node must be a SYSTEM or VALUES node.
        XMLDBFactory.doAssert (sectionNode.getNodeName ().equals (XMLDBFactory.M2_SYSTEM_TAG) ||
                sectionNode.getNodeName ().equals (XMLDBFactory.M2_VALUES_TAG));

        boolean systemSection = sectionNode.getNodeName ().equals (XMLDBFactory.M2_SYSTEM_TAG);

        MappingInfo mappingList = null;
        MappingInfo lastItem = null;

        // get the list of children of the node.
        // in this nodes we find the mapping information.
        NodeList nodeList = sectionNode.getChildNodes ();
        if (nodeList != null)
        {
            // walk thru the node list
            int size = nodeList.getLength ();
            for (int i = 0; i < size; i++)
            {
                // get the node from the list
                Node node = nodeList.item (i);
                // we are interrested only in elemnt nodes
                if (node.getNodeType () == Node.ELEMENT_NODE)
                {
                    // get the node name
                    String nodeName = node.getNodeName ();
                    // check if the node name is valid
                    if (nodeName != null)
                    {
                        // get the mapping object for the node
                        MappingInfo mappingInfo = this.getMappingInfo (node, rs, systemSection);
                        if (mappingInfo != null)
                        {
                            // append the new object to the list.
                            if (lastItem == null)
                            {
                                mappingList = mappingInfo;
                            } // if
                            else
                            {
                                lastItem.next = mappingInfo;
                            } // else
                            lastItem = mappingInfo;
                        } // if (mappingInfo != null)
                    } // if the node has a name
                } // if the node type is ELEMENT_NODE
            } // for i
        } // if (childNode != null)

        return mappingList;
    } // getSectionMapping


    /**************************************************************************
     * Returns a the mapping information of the given node. <BR/>
     * If the node holds a import mapping the result set from the
     * import query must be provided. For a export mapping the passed
     * result set must be <CODE>null</CODE>.
     *
     * @param   node        the tag node.
     * @param   rs          the result set of the import query for import
     *                      mapping, for export mapping <CODE>null</CODE>.
     * @param   systemTag   true if the node is part of the SYSTEM section
     *
     * @return  the mapping information or <CODE>null</CODE> if the node
     *          can be ignored (empty).
     *
     * @throws  Exception
     *          An exception occurred while trying to get the mapping info.
     */
    private MappingInfo getMappingInfo (Node node, ResultSet rs,
                                        boolean systemTag) throws Exception
    {
        // create a new mapping object
        MappingInfo info = new MappingInfo ();

        // get the node name and value.
        // the node value holds the column name or a constant text,
        String tagValue = this.getNodeText (node);
        if (tagValue == null)
        {
            tagValue = "";
        } // if
        String tagName = node.getNodeName ();
        // the tag must have a valid name
        XMLDBFactory.doAssert (tagName != null && tagName.length () > 0);

        // if the result set is valid this is a import tag
        // otherwise the tag is a export tag.
        boolean importMapping = rs != null;

        if (importMapping)
        {
            // for import tags we have to check if the value holds
            // the name of a column or if it holds a constant text.
            if (tagValue.length () > 0)
            {
                // if the value beginns not with '"' we take it
                // as s column name.
                if (tagValue.charAt (0) != '"')
                {
                    try
                    {
                        // check if the column name is a valid column name
                        rs.findColumn (tagValue);
                        // ok, this is a column in the result set
                        info.isDatabaseColumn = true;
                    } // try
                    catch (Exception e)
                    {
                        // oops, the column name is not a valid column name.
                        this.error ("Mapping error for column " + tagValue + ": " + e.getMessage ());
                        return null;
                    } // catch
                } // if the value is a column name
                else
                {
                    // it seams the value is a constant text.
                    // a constant text must begin and end with '"'.
                    int len = tagValue.length ();
                    if (len < 2 || tagValue.charAt (len - 1) != '"')
                    {
                        // oops, the value is not valid.
                        this.error ("Invalid constant tagValue for tag " + tagName);
                        return null;
                    } // if
                    // the value ist the text between the ampercents.
                    tagValue = tagValue.substring (1, len - 1);
                } // else the value is a constant text
            } // if the length of the tagValue is > 0
        } // if importMapping
        else
        {
            // for export tags we check if the value holds a valid
            // parameter number for the stored procedure.

            // first remove tailing/leading spaces
            tagValue = tagValue.trim ();
            try
            {
                if (Integer.parseInt (tagValue) <= 0)
                {
                    throw new Exception ();
                } // if
            } // try
            catch (Exception e)
            {
                if (tagValue.length () > 0)
                {
                    this.error ("'" + tagValue + "' is not a valid parameter number.");
                } // if (tagValue.length () > 0)
                return null;
            } // catch
        } // else if (importMapping)

        // the tag is a SYSTEM tag
        if (systemTag)
        {
            // mark it as a system tag
            info.inSystemSection = true;
            // the tag name is the field name
            info.fieldName = tagName;
            // get the field key
            info.fieldKey = XMLDBFactory.getSystemFieldKey (tagName);
        } // if (systemTag)
        else
        // the tag is a VALUES tag
        {
            // mark it as a value tag
            info.inValuesSection = true;
            // the field attribute holds the name
            info.fieldName = this.getAttributeValue (node, XMLDBFactory.M2_FIELD_ATTR);
            // get the optional TYPE attribute
            info.type = this.getAttributeValue (node, XMLDBFactory.M2_VALUETYPE_ATTR);
            // get the field key
            info.fieldKey = XMLDBFactory.getValuesFieldKey (info.fieldName);
        } // else if (systemTag)

        // set the tag value
        info.value = tagValue;

        // generate a bit of debug output
        if (this.traceMapping)
        {
            this.traceln ("MAPPING: <" + info.fieldName + "> <" + info.value + ">");
        } // if

        return info;
    } // getMappingInfo


    /**************************************************************************
     * Returns a specific child tag node from the given root node. <BR/>
     *
     * @param   rootNode    The root node where to search
     * @param   nodeName    The name of the node to search
     *
     * @return  the node if found otherwise <CODE>null</CODE>.
     */
    private Node getNodeByName (Node rootNode, String nodeName)
    {
        NodeList nodeList = rootNode.getChildNodes ();
        int size = nodeList.getLength ();
        for (int i = 0; i < size; i++)
        {
            Node node = nodeList.item (i);
            if (node.getNodeName ().equals (nodeName))
            {
                return node;
            } // if
        } // for i
        return null;
    } // getNodeByName


    /**************************************************************************
     * Retuns the text of the QUERY tag in the given IMPORT node. <BR/>
     *
     * @param   importNode  The IMPORT/SUBIMPORT node from the configuration
     *
     * @return  the text of the query node or <CODE>null</CODE> if the node
     *           was not foudn or it has no text.
     */
    private String getImportQuery (Node importNode)
    {
        Node queryNode = this.getNodeByName (importNode, XMLDBFactory.FACTORY_QUERY_TAG);
        if (queryNode != null)
        {
            return this.getNodeText (queryNode);
        } // if
        return null;
    } // getImportQuery


    /**************************************************************************
     * Replaces the variables in the query with values. <BR/>
     *
     * @param   query           The query text
     * @param   parentValues    The values of the possible variables
     *
     * @return  the new query text
     *
     * @throws  Exception
     *          An exception occurred during replacing the variables.
     */
    private String replaceQueryVariables (String query,
                                          Hashtable<String, String> parentValues)
        throws Exception
    {
        String queryLocal = query; // variable for local assignments
        // a variable expression in a query must have the form "$(variable)".
        // attention: spaces are not allowed in the variable expression.
        //
        // scan the query text and replace all variables with
        // the corresponding value from the parentValue table.
        //
        // example:
        //    we assume the hash table hold the pair myid/10
        //
        //    query:      "select * from table where id = $(myid)
        //    result:     "select * from table where id = 10
        //
        while (true)
        {
            // search the begin of a variable expression
            int pos = queryLocal.indexOf ("$(");
            if (pos <= 0)
            {
                break;
            } // if
            String queryBegin = queryLocal.substring (0, pos);
            String replace = queryLocal.substring (pos + 2);
            // search the end of the variable expression
            pos = replace.indexOf (")");
            if (pos < 0)
            {
                break;
            } // if
            String queryEnd = replace.substring (pos + 1);
            replace = replace.substring (0, pos);
            String value = parentValues.get (replace);
            if (value == null)
            {
                this.error ("Unknown query variable '" + replace + "'");
            } // if
            queryLocal = queryBegin + value + queryEnd;
        } // while (true)
        return queryLocal;
    } // replaceQueryVariables


    /**************************************************************************
     * Performs the import for the given object definition. <BR/>
     *
     * @param   importNode      The IMPORT/SUBIMPORT node from the configuration file
     * @param   objectName      The type name of the object
     * @param   parentValues    The optionaly parameter values for the import
     *                          query.
     * @param   toplevel        Is this the toplevel node?
     *
     * @throws  Exception
     *          An exception occurred.
     */
    private void importObjects (Node importNode, String objectName,
                                Hashtable<String, String> parentValues,
                                boolean toplevel) throws Exception
    {
        // the given node must be a IMPORT or SUBIMPORT node.
        XMLDBFactory.doAssert (importNode.getNodeName ().equals (XMLDBFactory.FACTORY_IMPORT_TAG) ||
            importNode.getNodeName ().equals (XMLDBFactory.FACTORY_SUBIMPORT_TAG));

        // check if the object type name is valid.
        if (objectName == null || objectName.length () == 0)
        {
            this.error ("Object type name missing in IMPORT/SUBIMPORT tag.");
        } // if not valid object type name

        // get the import query.
        String query = this.getImportQuery (importNode);

        // check if a valid query is returned
        if (query == null)
        {
            this.error ("Import query missing!");
        } // if

        // if we have variable values replace they with values.
        if (parentValues != null)
        {
            query = this.replaceQueryVariables (query, parentValues);
        } // if

        // debug output
        if (this.traceQuery)
        {
            this.traceln ("QUERY: " + query);
        } // if

        // get the data out of the database
        Statement stmt = this.jdbcConnection.createStatement ();
        ResultSet rs = stmt.executeQuery (query);
        if (rs != null)
        {
            // debug output
            if (this.traceDBFields)
            {
                int size = rs.getMetaData ().getColumnCount ();
                for (int i = 1; i <= size; i++)
                {
                    this.traceln ("DB-FIELD: " + rs.getMetaData ().getColumnName (i));
                } // for i
            } // if (this.traceDBFields)

            // get the import mapping information
            MappingInfo mappingList = this.getMappingList (importNode, true, rs);
            if (mappingList == null)
            {
                this.error ("No valid mapping defined.");
            } // if (mappingList == null)

            // objects are enclosed in a OBJECTS tag
            this.openTag (XMLDBFactory.M2_OBJECTS_TAG);

            // walk thru the result set and generate the xml output
            while (rs.next ())
            {
                if (toplevel)
                {
                    // count the imported top level objects.
                    this.importCounter++;
                    if (this.traceVerbose && (this.importCounter % 100 == 0))
                    {
                        this.traceln ("Import: " + this.importCounter);
                    } // if
                } // if (toplevel)
                // import a single object
                this.importObject (objectName, mappingList, rs);
            }  // while (rs.next ())
            // close the result set
            rs.close ();
            // close the statement to release all resources (cursors)
            stmt.close ();

            // object are enclosed in a OBJECTS tag
            this.closeTag (XMLDBFactory.M2_OBJECTS_TAG);

        } // if the result set is not null
    } // importObjects


    /**************************************************************************
     * Opens a new tag in the DOM document. <BR/>
     *
     * @param   tagName     The name of the new tag.
     *
     * @throws  Exception
     *          An error occurred while trying to open the tag.
     */
    private void openTag (String tagName) throws Exception
    {
        // raw xml output ?
        if (this.rawXMLOutput)
        {
            this.serializer.startElement (null, tagName, tagName, null);
        } // if raw xml
        else
        {
            // open a new tag in the document
            this.openElement (tagName, null, null);
        } // else
    } // openTag


    /**************************************************************************
     * Opens a new xml tag with a attribute in the document. <BR/>
     *
     * @param   tagName     the name of the tag to open
     * @param   attrName    the name of the attribute
     * @param   attrValue   the value for the attribute
     *
     * @throws  Exception
     *          An error occurred while trying to open the tag.
     */
    private void openTag (String tagName, String attrName, String attrValue)
        throws Exception
    {
        // if we have to produce raw xml output
        if (this.rawXMLOutput)
        {
            // pass the tag informations to the serializer
            AttributesImpl attrList = new AttributesImpl ();
            attrList.addAttribute (null, attrName, attrName, null, attrValue);
            this.serializer.startElement (null, tagName, tagName, attrList);
        } // if raw xml
        else
        {
            // otherwise open a new element in the document
            this.openElement (tagName, attrName, attrValue);
        } // else if !raw xml
    } // openTag


    /**************************************************************************
     * Creates a new tag with optional attribute and value in the document. <BR/>
     *
     * @param   tagName     The name of the tag.
     * @param   attrName    The attribute's name.
     * @param   attrValue   The attribute's value.
     * @param   tagValue    The value of the tag.
     *
     * @throws  Exception
     *          An exception occurred.
     */
    private void createTag (String tagName, String attrName, String attrValue,
                            String tagValue) throws Exception
    {
        if (this.rawXMLOutput)
        {
            // open the tag: the tag can have a optional attribute
            if (attrName != null)
            {
                this.openTag (tagName, attrName, attrValue);
            } // if
            else
            {
                this.openTag (tagName);
            } // else

            // serialize the tag value
            int len = tagValue.length ();
            char[] chars = new char [len];
            tagValue.getChars (0, len, chars, 0);
            this.serializer.characters (chars, 0, len);

            // close the tag
            this.closeTag (tagName);
        } // if rawXMLOutput
        else
        {
            // otherwise we append a new DOM element to the
            // DOM document.
            Element node = this.newElement (tagName, tagValue);
            if (attrName != null)
            {
                node.setAttribute (attrName, attrValue);
            } // if
            this.insertionPoint.appendChild (node);
        } // else if rawXMLOutput
    } // createTag


    /**************************************************************************
     * Creates a new VALUE tag with the given field name, type and value in
     * the document. <BR/>
     *
     * @param   fieldName   The FIELD attribute of the tag.
     * @param   fieldType   The optional TYPE attribute of the tag.
     * @param   tagValue    The value of the tag.
     *
     * @throws  Exception
     *          An error occurred while trying to create the tag.
     */
    private void createValueTag (String fieldName, String fieldType,
                                 String tagValue) throws Exception
    {
        if (this.rawXMLOutput)
        {
            // pass the tag informations to the serializer
            AttributesImpl attrList = new AttributesImpl ();
            attrList.addAttribute (null, XMLDBFactory.M2_FIELD_ATTR, XMLDBFactory.M2_FIELD_ATTR, null, fieldName);
            attrList.addAttribute (null, XMLDBFactory.M2_VALUETYPE_ATTR, XMLDBFactory.M2_VALUETYPE_ATTR, null, fieldType);
            this.serializer.startElement (null, XMLDBFactory.M2_VALUE_TAG, XMLDBFactory.M2_VALUE_TAG, attrList);

            // serialize the tag value
            int len = tagValue.length ();
            char[] chars = new char [len];
            tagValue.getChars (0, len, chars, 0);
            this.serializer.characters (chars, 0, len);

            // pass the tag to the serializer
            this.serializer.endElement (null, XMLDBFactory.M2_VALUE_TAG, XMLDBFactory.M2_VALUE_TAG);
        } // if rawXMLOutput
        else
        {
            // otherwise we append a new DOM element to the
            // DOM document.
            Element node = this.newElement (XMLDBFactory.M2_VALUE_TAG, tagValue);
            node.setAttribute (XMLDBFactory.M2_FIELD_ATTR, fieldName);
            // set the option TYPE attribute
            if (fieldType != null)
            {
                node.setAttribute (XMLDBFactory.M2_VALUETYPE_ATTR, fieldType);
            } // if
            this.insertionPoint.appendChild (node);
        } // else if rawXMLOutput
    } // createValueTag


    /**************************************************************************
     * Closes the xml tag. <BR/>
     *
     * @param   tagName     The name of the tag to close.
     *
     * @throws  Exception
     *          An exception occurred.
     */
    private void closeTag (String tagName) throws Exception
    {
        // if we have to produce raw xml output
        if (this.rawXMLOutput)
        {
            // pass the tag to the serializer
            this.serializer.endElement (null, tagName, tagName);
        } // if raw xml
        else
        {
            // otherwise set the insertion point to the parent node
            // of the current insertion point.
            this.insertionPoint = this.insertionPoint.getParentNode ();
        } // else if raw xml
    } // closeTag


    /**************************************************************************
     * Generates a OBJECT tag in the xml output file with the informations
     * from the current record in the given result set. <BR/>
     *
     * @param   objectName  the object type name
     * @param   info        the mapping info for this object type
     * @param   rs          the result set from the import query
     *
     * @throws  Exception
     *          An exception occurred while trying to import the object.
     */
    private void importObject (String objectName, MappingInfo info, ResultSet rs)
        throws Exception
    {
        MappingInfo infoLocal = info;   // variable for local assignments

        // open the OBJECT tag with the object type name
        this.openTag (XMLDBFactory.M2_OBJECT_TAG, XMLDBFactory.M2_TYPECODE_ATTR, objectName);

        boolean systemTagOpen = false;
        boolean valuesTagOpen = false;
        String domain = "";

        // walk thru the mapping list
        // every object in the list holds the mapping information for
        // one m2 xml tag (SYSTEM or VALUE tag).
        while (infoLocal != null)
        {
            // if the mapping object holds a import node of an
            // embedded object we import the embedded objects (recursion).
            if (infoLocal.subimportNode != null)
            {
                // if the VALUES tag is open we close it
                if (valuesTagOpen)
                {
                    this.closeTag (XMLDBFactory.M2_VALUES_TAG);
                    valuesTagOpen = false;
                } // if (valuesTagOpen)

                // the hash table for the parameter values needed to
                // resolve the import query in sub imports.
                QueryValueHash hash = new QueryValueHash ();

                int size = rs.getMetaData ().getColumnCount ();
                for (int i = 1; i <= size; i++)
                {
                    String colName = rs.getMetaData ().getColumnName (i);
                    // get the value from the result set
                    String value = rs.getString (rs.findColumn (colName));
                    // null values from the database must by caught.
                    if (value == null)
                    {
                        value = "";
                    } // if
                    // put the column/value pair in the hash table
                    hash.put (colName, value);
                } // for i

                String subObjectType = this.getAttributeValue (
                    infoLocal.subimportNode, XMLDBFactory.M2_TYPECODE_ATTR);
                this.importObjects (infoLocal.subimportNode, subObjectType, hash, false);
                // get the next mapping object
                infoLocal = infoLocal.next;
                // and continue the while loop
                continue;
            } // if valid import node

            String value = null;

            // if the current tag value is the name of a result column
            if (infoLocal.isDatabaseColumn)
            {
                // get the value from the result set
                value = rs.getString (rs.findColumn (infoLocal.value));
                // escape the '\n' character
                XMLDBFactory.escapeLineSeparators (value);
            } // the tag is a column name
            else
            {
                // the value of the tag is a constant text
                // constant text is NOT escaped!
                value = infoLocal.value;
            } // if (info.isDatabaseColumn)

            // if we got no value we set it to an empty string
            if (value == null)
            {
                value = "";
            } // if

            // the tag is subtag of the SYSTEM tag
            if (infoLocal.inSystemSection)
            {
                // open the SYSTEM tag if not already open
                if (!systemTagOpen)
                {
                    this.openTag (XMLDBFactory.M2_SYSTEM_TAG);
                    systemTagOpen = true;
                } // if (!systemTagOpen)
                // get the m2 tag name
                String tag = infoLocal.fieldName;
                // the tag DOMAIN is not a real tag but it is attribute
                // for the ID tag. we store the DOMAIN attribute for later
                // use with the ID tag.
                if (tag.equals (XMLDBFactory.M2_DOMAIN_ATTR))
                {
                    domain = value;
                } // if
                else if (tag.equals (XMLDBFactory.M2_ID_TAG))
                {   // the ID tag has the domain name as attribute.
                    this.createTag (tag, XMLDBFactory.M2_DOMAIN_ATTR, domain, value);
                } // the tag is the ID tag
                else
                { // the tag is not the DOMAIN or ID tag
                    this.createTag (tag, null, null, value);
                } // the tag is not the DOMAIN or ID tag
            } // the tag is a sub tag of the SYSTEM tag
            else if (infoLocal.inValuesSection)
            {
                // if the SYSTEM tag is open we close it
                if (systemTagOpen)
                {
                    this.closeTag (XMLDBFactory.M2_SYSTEM_TAG);
                    systemTagOpen = false;
                } // if (systemTagOpen)
                // open the VALUES tag if not already open
                if (!valuesTagOpen)
                {
                    this.openTag (XMLDBFactory.M2_VALUES_TAG);
                    valuesTagOpen = true;
                } // if (!valuesTagOpen)
                // write the VALUE tag
                this.createValueTag (infoLocal.fieldName, infoLocal.type, value);
            } // the tag is a sub tag of the VALUES tag
            // get the next mapping object in the list
            infoLocal = infoLocal.next;
        } // while (info)

        // if the SYSTEM tag is open we close it
        if (systemTagOpen)
        {
            this.closeTag (XMLDBFactory.M2_SYSTEM_TAG);
        } // if
        // if the VALUES tag is open we close it
        if (valuesTagOpen)
        {
            this.closeTag (XMLDBFactory.M2_VALUES_TAG);
        } // if
        // close the OBJECT tag
        this.closeTag (XMLDBFactory.M2_OBJECT_TAG);
    } // importObject


    /**************************************************************************
     * Performs the import. <BR/>
     *
     * @param   hash    Hash table with the values for the import query.
     * @param   dest    The optionaly output stream.
     *
     * @throws  Exception
     *          An exception occurred.
     */
    private void doImport (QueryValueHash hash, OutputStream dest)
        throws Exception
    {
        // store the optional output stream.
        this.destStream = dest;

        // open the output stream if it is not already open
        if (this.destStream == null)
        {
            // if a destination file name is given we open the file
            // otherwise we use the stdout stream.
            if (this.destFile != null && this.destFile.length () > 0)
            {
                this.destStream = this.openOutputFile (this.destFile, this.destMode);
            } // if
            else
            {
                this.destStream = System.out;
            } // if
        } // output stream not open

        // some initializations
        this.document = null;
        this.insertionPoint = null;
        this.serializer = null;

        // for the raw xml output we use a serializer object.
        if (this.rawXMLOutput)
        {
            OutputFormat fmt = new OutputFormat (Method.XML, this.destEncoding, true);
            // set the indent for prettyprinted
            fmt.setIndent (2);
            // deactivate line wrapping
            fmt.setLineWidth (0);
            // <?xml version="1.0">
            fmt.setVersion ("1.0");
            // <!DOCTYPE IMPORT SYSTEM "import.dtd">
            // fmt.setDoctype (null, "import.dtd");
            // create the serializer
            this.serializer = new XMLSerializer (this.destStream, fmt);
            this.serializer.startDocument ();
        } // if rawXMLOutput
        else
        {
            try
            {
                // create a new empty DOM document:
                this.document = XMLWriter.createDocument ();
            } // try
            catch (XMLWriterException e)
            {
                System.out.println (e.toString ());
                throw new Exception ("Could not create import document", e);
            } // catch
        } // no rawXMLOutput

        // create the IMPORT and OBJECTS tags
        this.openTag (XMLDBFactory.M2_IMPORT_TAG);

        // get the Object type code from the root node. this is the type code
        // of the object to import.
        String objectType = this.getAttributeValue (this.objectRoot,
            XMLDBFactory.M2_TYPECODE_ATTR);
        // check if we have a valid type code.
        if (objectType == null || objectType.length () == 0)
        {
            this.error ("Object type code missing.");
        } // if

        // get the IMPORT node from the root node. this node holds all the
        // informations (query and mapping) for the import.
        Node importNode = this.getNodeByName (this.objectRoot,
            XMLDBFactory.FACTORY_IMPORT_TAG);
        // check if we have a valid import node
        if (importNode == null)
        {
            this.error ("No import section found for object type '" +
                objectType + "'.");
        } // if

        // if the import is to perform in a single transaction
        // we disable the auto commit mode of the driver.
        this.jdbcConnection.setAutoCommit (!this.importTrans);

        // import all object from the database
        this.importObjects (importNode, objectType, hash, true);

        // commit work
        this.jdbcConnection.commit ();

        // close the IMPORT tags
        this.closeTag (XMLDBFactory.M2_IMPORT_TAG);

        // tell the serializer the end of the document.
        if (this.rawXMLOutput)
        {
            this.serializer.endDocument ();
        } // if

        // close the output stream (if it is not the stdout stream)
        if (this.destStream != System.out)
        {
            this.destStream.close ();
        } // if
        this.destStream = null;
    } // doImport


    /**************************************************************************
     * Performs the export. <BR/>
     *
     * @param   validate    Activate the validation of the xml file.
     * @param   inStream    The optionaly xml input stream.
     *
     * @throws  Exception
     *          An error occurred while trying to perform the export.
     */
    private void doExport (boolean validate, InputStream inStream)
        throws Exception
    {
        // store the optional input stream.
        this.srcStream = inStream;

        // open the input stream if it is not already open
        if (this.srcStream == null)
        {
            // if a source file name is given we open the file
            // otherwise we use the stdin stream.
            if (this.srcFile != null && this.srcFile.length () > 0)
            {
                this.srcStream = new FileInputStream (this.srcFile);
            } // if
            else
            {
                this.srcStream = System.in;
            } // else
        } // if the output stream is not open

        try
        {
            // we disable the auto commit mode of the driver.
            this.jdbcConnection.setAutoCommit (false);

            try
            {
                // the sax parser is used to scan the input xml file
                SAXParser p = new SAXParser ();
                // activate/deactivate the validation of the xml file.
                p.setFeature (IOConstants.URL_HTTP + "xml.org/sax/features/validation", validate);
                // register the event handlers
                p.setContentHandler (new FactorySaxHandler ());
                // parse the input source.
                p.parse (new InputSource (this.srcStream));
                // reset the parser to free all memory used.
                p.reset ();
            } // try
            catch (SAXException e)
            {
                this.error ("SAXParser: " + e.getMessage ());
            } // catch

            // commit work
            this.jdbcConnection.commit ();

            // report the number of exported objects.
            this.traceln (this.exportCounter + " top level object(s) exported.");

            // close the input stream (if it is not stdin)
            if (this.srcStream != System.in)
            {
                this.srcStream.close ();
            } // if
            this.srcStream = null;
        } // try
        catch (Exception e)
        {
            // on error perform a rollback on the database.
            this.traceln ("Performing a rollback.");
            this.jdbcConnection.rollback ();

            // report the number of commited objects.
            this.traceln (this.exportCommitedCounter + " top level object(s) commited.");

            throw e;
        } // catch
    } // doExport


    /**************************************************************************
     * This helper class hold the information of a object. <BR/>
     *
     * @version     $Id: XMLDBFactory.java,v 1.21 2007/07/31 19:13:55 kreimueller Exp $
     *
     * @author      Michael Steiner (MS), 000823
     **************************************************************************
     */
    private class ExportInfo
    {
        /**
         * The names for export. <BR/>
         */
        public Vector<String> names = new Vector<String> ();

        /**
         * The values for export. <BR/>
         */
        public Vector<String> values = new Vector<String> ();

        /**
         * The name of the object for export. <BR/>
         */
        public String objectName = null;

        /**
         * Was the object exported. <BR/>
         */
        public boolean isExported = false;


        /**************************************************************************
         * Constructor of the class. <BR/>
         *
         * @param   objectName  The name of the object to be exported.
         */
        public ExportInfo (String objectName)
        {
            this.objectName = objectName;
//            DebugClient.debugln ("new ExportInfo for object type " + objectName);
        } // ExportInfo
    } // class ExportInfo


    /**************************************************************************
     * This helper class handles all content events generated by the SAXParser. <BR/>
     *
     * @version     $Id: XMLDBFactory.java,v 1.21 2007/07/31 19:13:55 kreimueller Exp $
     *
     * @author      Michael Steiner (MS), 000823
     **************************************************************************
     */
    class FactorySaxHandler extends DefaultHandler
    {
        /**
         * True if the begin of a tag is recognized. <BR/>
         */
        private boolean tagOpen = false;
        /**
         * The name of the current xml tag. <BR/>
         */
        private String tagName = "";
        /**
         * The value of the current xml tag. <BR/>
         */
        private String tagValue = "";
        /**
         * The attributes of the current xml tag. <BR/>
         */
        private Attributes tagAttr = null;
        /**
         * True if the current tag is part of the SYSTEM section. <BR/>
         */
        private boolean inSystemSection = false;
        /**
         * True if the current tag is part of the VALUES section. <BR/>
         */
        private boolean inValuesSection = false;
        /**
         * Holds the informations for the current object. <BR/>
         */
        private ExportInfo info = null;
        /**
         * Counts the object levels. <BR/>
         * The level 1 indicates a top level object.
         * Levels > 1 indicates sub objects.
         */
        private int objectLevel = 0;


        /**********************************************************************
         * Receive notification of character data inside an element.
         * This event is fired when the value of a tag is scanned.
         *
         * <p>By default, do nothing.  Application writers may override this
         * method to take specific actions for each chunk of character data
         * (such as adding the data to a node or buffer, or printing it to
         * a file).</p>
         *
         * @param ch The characters.
         * @param start The start position in the character array.
         * @param length The number of characters to use from the
         *               character array.
         * @exception org.xml.sax.SAXException Any SAX exception, possibly
         *            wrapping another exception.
         * @see org.xml.sax.ContentHandler#characters
         */
        public void characters (char[] ch, int start, int length)
            throws SAXException
        {
            if (this.tagOpen)
            {
                this.tagValue = String.copyValueOf (ch, start, length);
            } // if (this.tagOpen)
        } // characters


        /**********************************************************************
         * Receive notification of the start of an element.
         * This event is fired on the start of a new element.
         *
         * <p>By default, do nothing.  Application writers may override this
         * method in a subclass to take specific actions at the start of
         * each element (such as allocating a new tree node or writing
         * output to a file).</p>
         *
         * @param namespaceURI The Namespace URI, or the empty string if the
         *        element has no Namespace URI or if Namespace
         *        processing is not being performed.
         * @param localName The local name (without prefix), or the
         *        empty string if Namespace processing is not being
         *        performed.
         * @param rawName The qualified name (with prefix), or the
         *        empty string if qualified names are not available.
         * @param atts The attributes attached to the element.  If
         *        there are no attributes, it shall be an empty
         *        Attributes object.
         * @exception org.xml.sax.SAXException Any SAX exception, possibly
         *            wrapping another exception.
         * @see org.xml.sax.ContentHandler#startElement
         */
        public void startElement (String namespaceURI, String localName,
                                  String rawName, Attributes atts)
            throws SAXException
        {
            // if the element is a OBJECT tag
            // we start with a empty ExportInfo object.
            if (localName.equals (XMLDBFactory.M2_OBJECT_TAG))
            {
                // increment the object level
                // if the level is 1 the current object is a
                // top level object.
                this.objectLevel++;
                // if the information of the previous object is valid
                // we store the information in the database.
                if (this.info != null)
                {
                    this.storeObject ();
                } // if
                // create a new empty info object
                this.info = new ExportInfo (atts
                    .getValue (XMLDBFactory.M2_TYPECODE_ATTR));
            } // if OBJECT tag
            else if (localName.equals (XMLDBFactory.M2_SYSTEM_TAG))
            {
                this.inSystemSection = true;
                this.inValuesSection = false;
            } //if SYSTEM tag
            else if (localName.equals (XMLDBFactory.M2_VALUES_TAG))
            {
                this.inValuesSection = true;
                this.inSystemSection = false;
            } // if VALUES tag

            // store the name and the attribute of the tag
            this.tagName = localName;
            this.tagAttr = atts;
            // reset the value field
            this.tagValue = "";
            // now we are inside a tag
            this.tagOpen = true;
        } // startElement


        /**********************************************************************
         * Receive notification of the end of an element.
         *
         * <p>By default, do nothing.  Application writers may override this
         * method in a subclass to take specific actions at the end of
         * each element (such as finalising a tree node or writing
         * output to a file).</p>
         *
         * @param uri The Namespace URI, or the empty string if the
         *        element has no Namespace URI or if Namespace
         *        processing is not being performed.
         * @param localName The local name (without prefix), or the
         *        empty string if Namespace processing is not being
         *        performed.
         * @param rawName The qualified name (with prefix), or the
         *        empty string if qualified names are not available.
         * @exception org.xml.sax.SAXException Any SAX exception, possibly
         *            wrapping another exception.
         * @see org.xml.sax.ContentHandler#endElement
         */
        public void endElement (String uri, String localName, String rawName)
            throws SAXException
        {
            if (localName.equals (XMLDBFactory.M2_OBJECT_TAG))
            {
                if (this.info != null)
                {
                    this.storeObject ();
                    this.info = null;
                } // if
                // if we reached the end of o top level object
                // we can commit the transaction on the database.
                if (this.objectLevel == 1)
                {
                    try
                    {
                        commitData ();
                    } // try
                    catch (Exception e)
                    {
                        throw new SAXException (e);
                    } // catch
                } // if objectLevel == 1
                this.objectLevel--;
            } // if OBJECT tag
            // check if we enter in the SYSTEM section.
            if (localName.equals (XMLDBFactory.M2_SYSTEM_TAG))
            {
                this.inSystemSection = false;
            } // if SYSTEM tag
            // check if we enter in the VALUES section.
            else if (localName.equals (XMLDBFactory.M2_VALUES_TAG))
            {
                this.inValuesSection = false;
            } // if VALUE tag
            else
            {
                // the information of the tag in the SYSTEM/VALUES section
                // is stored in the information object.
                if (this.info != null)
                {
                    // the ID tag in the SYSTEM section holds also the
                    // domain attribute.
                    if (this.inSystemSection)
                    {
                        // the ID tag holds the DOMAIN attribute
                        if (this.tagName.equals (XMLDBFactory.M2_ID_TAG))
                        {
                            String domain = this.tagAttr
                                .getValue (XMLDBFactory.M2_DOMAIN_ATTR);
                            this.info.names
                                .addElement (XMLDBFactory
                                    .getSystemFieldKey (XMLDBFactory.M2_DOMAIN_ATTR));
                            this.info.values.addElement (domain);
                        } // if (tag == ID tag)
                        this.info.names.addElement (XMLDBFactory
                            .getSystemFieldKey (this.tagName));
                        this.info.values.addElement (this.tagValue);
                    } // if in SYSTEM section
                    // get the information of tags in the VALUES section.
                    if (this.inValuesSection)
                    {
                        // the name is composed from the text 'FIELD' a dot
                        // and the field name.
                        String field = this.tagAttr
                            .getValue (XMLDBFactory.M2_FIELD_ATTR);
                        this.info.names.addElement (XMLDBFactory
                            .getValuesFieldKey (field));
                        this.info.values.addElement (this.tagValue);
                    } // if in VALUES section
                } // if info != null
            } // if other tag

            // clear all parsed information.
            this.tagValue = "";
            this.tagName = "";
            this.tagAttr = null;
            this.tagOpen = false;
        } // endElement


        /**********************************************************************
         * Stores the recogniced object. <BR/>
         *
         * @throws  SAXException
         *          ???
         */
        private void storeObject () throws SAXException
        {
            try
            {
                // the info object must be valid.
                XMLDBFactory.doAssert (this.info != null);
                // store the object in the database.
                storeData (this.info.names, this.info.values, this.info.objectName);
            } // try
            catch (Exception e)
            {
                throw new SAXException (e);
            } // catch
        } // storeObject
    } // class FactorySaxHandler


    /**************************************************************************
     * Creates a new tag in the document width the given name and
     * attribute and sets the insertion point to this new tag. <BR/>
     *
     * @param   name        The name of the new tag
     * @param   attrName    The attribute name
     * @param   attrValue   The attribute value
     *
     * @throws  Exception
     *          An exception occurred while trying to open the element.
     */
    private void openElement (String name, String attrName, String attrValue)
        throws Exception
    {
        // cannot create a new element without a document.
        XMLDBFactory.doAssert (this.document != null);

        Element item = this.document.createElement (name);
        if (attrName != null)
        {
            item.setAttribute (attrName, attrValue);
        } // if
        // add the new element to the xml document
        if (this.insertionPoint != null)
        {
            this.insertionPoint.appendChild (item);
        } // if
        else
        {
            this.document.appendChild (item);
        } // else
        // set the insertion point to the new element
        this.insertionPoint = item;
    } // openElement


    /**************************************************************************
     * Creates a new document element width the given name and value. <BR/>
     *
     * @param   name    The name for the new element
     * @param   value   The text value for the new element
     *
     * @return          The new element.
     *
     * @throws  Exception
     *          An exception occurred while trying to create the element.
     */
    private Element newElement (String name, String value) throws Exception
    {
        Element item = this.document.createElement (name);
        item.appendChild (this.document.createTextNode (value));
        return item;
    } // newElement


    /**************************************************************************
     * Stores the given values in the database by executing
     * the stored procedure for this object type. <BR/>
     *
     * @param   names       The names for the values.
     * @param   values      The values to store.
     * @param   objectName  The object type name.
     *
     * @throws  Exception
     *          An error occurred.
     * @throws  SQLException
     *          Error during database operation.
     */
    void storeData (Vector<String> names, Vector<String> values, String objectName)
        throws Exception, SQLException
    {
        XMLDBFactory.doAssert (names.size () == values.size ());

        // lookup the mapping list in the hashtable.
        MappingInfo mappingList = this.mappingHash.get (objectName);
        // if the mapping list is not found we have to generate it.
        if (mappingList == null)
        {
            // search the EXPORT/SUBEXPORT node for the object type name
            Node exportNode = this.getNodeByName (this.objectRoot,
                XMLDBFactory.FACTORY_EXPORT_TAG);
            if (exportNode != null)
            {
                // the OBJECT node holds the type code for the EXPORT tag.
                String typeCode = this.getAttributeValue (this.objectRoot,
                    XMLDBFactory.M2_TYPECODE_ATTR);
                // if the type code does not match we search a SUBIMPORT node.
                if (typeCode == null || !typeCode.equals (objectName))
                {
                    // search a SUBEXPORT tag with the given object name.
                    do
                    {
                        exportNode = this.getNodeByName (exportNode,
                            XMLDBFactory.FACTORY_SUBEXPORT_TAG);
                        if (exportNode != null)
                        {
                            // get the object type code of the export node
                            String attr = this.getAttributeValue (exportNode,
                                XMLDBFactory.M2_TYPECODE_ATTR);
                            if (attr != null && attr.equals (objectName))
                            {
                                break;
                            } // if
                        } // if valid SUBEXPORT node
                    } while (exportNode != null);
                } // type name does not match
            } // if valid EXPORT node

            if (exportNode == null)
            {
                this.error ("Export mapping for object type '" + objectName + "' not found.");
            } // if

            // get the export mapping information.
            mappingList = this.getMappingList (exportNode, false, null);

            // check if we have a valid mapping list.
            if (mappingList == null)
            {
                this.error (StringHelpers.replace (
                    XMLDBFactory.ERRM_EXPMAPPING_INVALID,
                    UtilConstants.TAG_NAME, objectName));
            } // if

            // bubble sort
            boolean sorted = false;
            while (!sorted)
            {
                sorted = true;
                MappingInfo item = mappingList;
                MappingInfo prev = null;
                while (item != null)
                {
                    MappingInfo next = item.next;
                    if (next != null)
                    {
                        // convert the string values to integers
                        int nextNr = Integer.parseInt (next.value);
                        int itemNr = Integer.parseInt (item.value);

                        if (nextNr < itemNr)
                        {
                            sorted = false;
                            if (prev != null)
                            {
                                prev.next = next;
                            } // if
                            else
                            {
                                mappingList = next;
                            } // else
                            item.next = next.next;
                            next.next = item;
                        } // if unsorted
                    } // if item.next
                    prev = item;
                    item = item.next;
                } // while item
            } // while !sorted

            // insert the mapping list in the hash table.
            this.mappingHash.put (objectName, mappingList);

//traceln ("searching proc for object " + objectName);

            // lookup the stored procedure for this object type.
            Node procNode = this.getNodeByName (exportNode,
                XMLDBFactory.FACTORY_PROCEDURE_TAG);
            if (procNode != null)
            {
                String proc = this.getNodeText (procNode);

//traceln ("found proc node for object: " + proc);

                // insert the stored procedure in the hash table.
                this.procedureHash.put (objectName, proc);
            } // if valid PROCEDURE node
        } // if (mappingList == null)

        String storedProc = this.procedureHash.get (objectName);
        // check if we have a valid stored procedure.
        if (storedProc == null || storedProc.length () == 0)
        {
            this.error (StringHelpers.replace (
                XMLDBFactory.ERRM_STOREDPROC_INVALID,
                UtilConstants.TAG_NAME, objectName));
        } // if

        String sql = "";

        // count the parameters and check if the mapping list is sorted
        // and has no holes.
        int argNumber = 0;
        MappingInfo mapping = mappingList;
        while (mapping != null)
        {
            int nr = new Integer (mapping.value).intValue ();
            if (nr != argNumber + 1)
            {
                this.error (StringHelpers.replace (
                    XMLDBFactory.ERRM_EXPMAPPING_INVALID,
                    UtilConstants.TAG_NAME, objectName));
            } // if
            if (argNumber > 0)
            {
                sql += ", ";
            } // if
            sql += "?";
            argNumber = nr;
            mapping = mapping.next;
        } // while mapping

        sql = "{ call " + storedProc + "(" + sql + ") }";

        CallableStatement call = this.jdbcConnection.prepareCall (sql);

        // set the parameters for the stored procedure.
        // the order is the order of the mapping list.
        String argList = "";
        argNumber = 1;
        mapping = mappingList;
        while (mapping != null)
        {
            String value = "";
            int size = values.size ();
            // search the matching values
            for (int i = 0; i < size; i++)
            {
                String name = names.elementAt (i);
                if (name.equals (mapping.fieldKey))
                {
                    // replace the character sequence '\\' + 'n' with '\n'
                    value = XMLDBFactory.unescapeLineSeparators (values.elementAt (i));
                    break;
                } // if (name.equals (mapping.fieldKey))
            } // for i
            // set the argument value
            call.setString (argNumber, value);

            // add the value to the argument list for tracing
            if (argList.length () > 0)
            {
                argList += ", ";
            } // if
            argList += "'" + value + "'";

            argNumber++;
            mapping = mapping.next;
        } // while mapping

        if (this.traceQuery)
        {
            this.traceln ("call " + storedProc + "(" + argList + ")");
        } // if (this.traceQuery)

        // execute the stored procedure call.
        call.execute ();
        // close the statement to release resources immediatly.
        call.close ();
    } // storeData


    /**************************************************************************
     * Commits the current transaction in the database. <BR/>
     *
     * @throws  SQLException
     *          An exception occurred during database access.
     */
    void commitData () throws SQLException
    {
        // if not the entire export is a transaction
        // we commit after the export of any top level object.
        if (!this.exportTrans)
        {
            this.jdbcConnection.commit ();
            // count the commited top level objects.
            this.exportCommitedCounter++;
        } // if (!this.exportTrans)
        // count the exported top level objects.
        this.exportCounter++;
        // show a bit of status information.
        if (this.traceVerbose && (this.exportCounter % 100) == 0)
        {
            try
            {
                this.traceln ("Export: " + this.exportCounter);
            } // try
            catch (Exception e)
            {
                // should not occur
                // throw the exception:
                throw new SQLException (
                    "Exception in XMLDBFactory.commitData: " + e.toString ());
            } // catch
        } // if traceVerbose
    } // commitData


    /**************************************************************************
     * Returns a unique key for the given system field. <BR/>
     *
     * @param   field   the field name
     *
     * @return  the key for the field
     */
    static String getSystemFieldKey (String field)
    {
        return XMLDBFactory.M2_SYSTEM_TAG + "." + field;
    } // getSystemFieldKey


    /**************************************************************************
     * Returns a unique key for the given values field. <BR/>
     *
     * @param   field   the field name
     *
     * @return  the key for the field
     */
    static String getValuesFieldKey (String field)
    {
        return XMLDBFactory.M2_VALUES_TAG + "."  + XMLDBFactory.M2_FIELD_ATTR + "." + field;
    } // getValuesFieldKey


    /**************************************************************************
     * Replaces the character '\n' with the character sequenze '\\' + 'n' and
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
        String s = "";
        int len = str.length ();
        for (int i = 0; i < len; i++)
        {
            char c = str.charAt (i);
            switch (c)
            {
                case '\n':
                    // '\n' is escaped
                    s += "\\n";
                    break;
                case '\\':
                    if (i < len - 1 && str.charAt (i + 1) == 'n')
                    {
                        s += "\\\\";
                    } // if
                    else
                    {
                        s += c;
                    } // else
                    break;
                case '\r':
                    // '\r' is ignored
                    break;
                default:
                    s += c;
            } // switch (c)
        } // for i
        return s;
    } // replaceLineSeparators


    /**************************************************************************
     * Replaces the character sequenze '\\' + 'n' with '\n'. <BR/>
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
        String s = "";
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
                    s += "\n";
                    i++;
                } // if ('n')
                else if (c2 == '\\' && i < len - 2 && str.charAt (i + 2) == 'n')
                {
                    // ignore the first '\\'
                    s += "\\n";
                    i += 2;
                } // if ('\\' + 'n')
                else
                {
                    s += c;
                } // else
            } // if ('\\')
            else
            {
                s += c;
            } // else
        } // for i
        return s;
    } // unescapeLineSeparators

} // class XMLDBFactory
