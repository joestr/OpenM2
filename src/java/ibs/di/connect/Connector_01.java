/*
 * Class: Connector_01.java
 */

// package:
package ibs.di.connect;

// imports:
//TODO: unsauber
import ibs.bo.BusinessObject;
//TODO: unsauber
import ibs.bo.Datatypes;
//TODO: unsauber
import ibs.bo.OID;
import ibs.di.DIArguments;
import ibs.di.DIConstants;
import ibs.di.DIErrorHandler;
import ibs.di.DITokens;
import ibs.di.DataElement;
import ibs.di.Log_01;
import ibs.di.Response;
import ibs.di.connect.ConnectionFailedException;
import ibs.di.connect.ConnectorInterface;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
//TODO: unsauber
import ibs.service.user.User;
import ibs.tech.html.TableElement;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.StoredProcedure;
import ibs.tech.xml.XMLReader;
import ibs.tech.xml.XMLReaderException;
import ibs.util.StringComparator;
import ibs.util.file.FileHelpers;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.util.Arrays;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


/******************************************************************************
 * The Connector_01 class represents the encapsulation of import and export
 * streams. The interfaces privide methods to access data streams or write
 * to data streams in order to get or receive data from various data sources
 * or deliver data to export destinations. Each subclass must implement the
 * methods to write to and to read from data streams.<BR/>
 * In the export case the files to export are taken from the file system.
 * That means that the appropriate filter classes always generate
 *
 * @version     $Id: Connector_01.java,v 1.39 2013/01/16 16:14:14 btatzmann Exp $
 *
 * @author      Buchegger Bernd (BB), 991008
 ******************************************************************************
 */
public class Connector_01 extends BusinessObject implements ConnectorInterface
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Connector_01.java,v 1.39 2013/01/16 16:14:14 btatzmann Exp $";


    /**
     *  path of the importsource or exportdestination
     */
    protected String path = "";

    /**
     *  name of the importfile
     */
    protected String fileName = "";

    /**
     * Argument #1 for connector settings. <BR/>
     */
    protected String arg1 = "";
    /**
     * Argument #2 for connector settings. <BR/>
     */
    protected String arg2 = "";
    /**
     * Argument #3 for connector settings. <BR/>
     */
    protected String arg3 = "";
    /**
     * Argument #4 for connector settings. <BR/>
     */
    protected String arg4 = "";
    /**
     * Argument #5 for connector settings. <BR/>
     */
    protected String arg5 = "";
    /**
     * Argument #6 for connector settings. <BR/>
     */
    protected String arg6 = "";
    /**
     * Argument #7 for connector settings. <BR/>
     */
    protected String arg7 = "";
    /**
     * Argument #8 for connector settings. <BR/>
     */
    protected String arg8 = "";
    /**
     * Argument #9 for connector settings. <BR/>
     */
    protected String arg9 = "";

    /**
     * Flag if connector is switched on or off for import. <BR/>
     */
    protected boolean isImportConnector = false;
    /**
     * Flag if connector is switched on or off for export. <BR/>
     */
    protected boolean isExportConnector = false;

    /**
     * Flag if connector is enabled for import. <BR/>
     * This flag defines if the connector can be used for import. <BR/>
     */
    protected boolean isImportEnabled = true;
    /**
     * Flag if connector is enabled for export. <BR/>
     * This flag defines if the connector can be used for export.<BR/>
     */
    protected boolean isExportEnabled = true;

    /**
     *  array of importfiles available
     */
    protected String[] files;

    /**
     * Absolute base path of the m2 system.<BR/>
     */
    protected String m2AbsBasePath = "";

    /**
     * connectorType.<BR/>
     */
    public int connectorType = DIConstants.CONNECTORTYPE_NONE;

    /**
     * flag if a temp directory should be created.<BR/>
     */
    public boolean isCreateTemp = true;

    /**
     * flag if the temp directory should be deleted.<BR/>
     */
    protected boolean isDeleteTemp = false;

    /**
     * flag if importfiles should be deleted.<BR/>
     * this flag effects the read method because
     * the read method reads the importfiles, write
     * it into the temp directory and will
     * delete the importfile after sucessfull reading.<BR/>
     * BB HINT: note that fileConnectors always operate on
     * server directories and don't use temp directories.
     * That means that the importfile will be deleted by
     * the integrator itself. <BR/>
     */
    public boolean isDeleteImportFiles = false;


    /**
     *  filter for importfiles. <BR/>
     */
    protected String fileFilter = "*.xml";

    /**
     * Constant for the match asterix.<BR/>
     * The asterix is a placeholder for any string.<BR/>
     */
    private static String MATCH_ASTERIX = "*";

    /**
     * Constant for match type: start with search value. <BR/>
     */
    private static final int MATCHTYPE_STARTWITH    = 0;
    /**
     * Constant for match type: end with search value. <BR/>
     */
    private static final int MATCHTYPE_ENDSWITH     = 1;
    /**
     * Constant for match type: contain search value. <BR/>
     */
    private static final int MATCHTYPE_CONTAINS     = 2;
    /**
     * Constant for match type: equals to search value. <BR/>
     */
    private static final int MATCHTYPE_EQUALS       = 3;
    /**
     * Constant for match type: contains search value. <BR/>
     */
    private static final int MATCHTYPE_IN           = 4;

    /**
     *  response from an export destination.<BR/>
     *  Note that the response must be handled individually in the
     *  subclasses.<BR/>
     */
    protected Response p_response = null;

    /**
     *  the string that has been read as response.<BR/>
     */
    protected String p_responseStr = null;

    /**
     * File prefix of connector. <BR/>
     */
    protected String p_filePrefix = null;

    /**
     * Indicates if the connector is used for backup purposes. <BR/>
     */
    protected boolean p_isBackupConnector = false;


    /**************************************************************************
     * Creates a Connector Object. <BR/>
     */
    public Connector_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();
    } // Connector


    /**************************************************************************
     * Creates a Connector Object. <BR/>
     *
     * @param   oid     oid of the object
     * @param   user    user that created the object
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public Connector_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
    } // Connector


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set stored procedure names:
        this.procCreate =    "p_Connector_01$create";
        this.procRetrieve =  "p_Connector_01$retrieve";
        this.procDelete =    "p_Connector_01$delete";
        this.procChange =    "p_Connector_01$change";

        // set number of parameters for procedure calls:
        this.specificRetrieveParameters = 12;
        this.specificChangeParameters = 12;
    } // initClassSpecifics


    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     * The <A HREF="#env">env</A> property is used for getting the parameters.
     * This property must be set before calling this method. <BR/>
     */
    public void getParameters ()
    {
        String text = null;
        int num = 0;

        // get other parameters
        super.getParameters ();

        // get type of connector and the name
        if ((num = this.env.getIntParam (DIArguments.ARG_CONNECTORTYPE)) !=
             IOConstants.INTPARAM_NOTEXISTS_OR_INVALID)
        {
            this.setConnectorType (num);
        } // if ((num = env.getIntParam (DIArguments.ARG_CONNECTORTYPE)) !=  ...

        // get direction of connector
        if ((num = this.env.getBoolParam (DIArguments.ARG_ISIMPORTCONNECTOR)) >=
            IOConstants.BOOLPARAM_FALSE)
        {
            this.isImportConnector = num == IOConstants.BOOLPARAM_TRUE;
        } // if ((num = env.getBoolParam (DIArguments.ARG_ISIMPORTCONNECTOR)) ...
        if ((num = this.env.getBoolParam (DIArguments.ARG_ISEXPORTCONNECTOR)) >=
            IOConstants.BOOLPARAM_FALSE)
        {
            this.isExportConnector = num == IOConstants.BOOLPARAM_TRUE;
        } // if ((num = env.getBoolParam (DIArguments.ARG_ISEXPORTCONNECTOR) ...

        // get specifics of the entry:
        if ((text = this.env.getParam (DIArguments.ARG_ARG1)) != null)
        {
            this.arg1 = text;
        } // if
        if ((text = this.env.getParam (DIArguments.ARG_ARG2)) != null)
        {
            this.arg2 = text;
        } // if
        if ((text = this.env.getParam (DIArguments.ARG_ARG3)) != null)
        {
            this.arg3 = text;
        } // if
        if ((text = this.env.getParam (DIArguments.ARG_ARG4)) != null)
        {
            this.arg4 = text;
        } // if
        if ((text = this.env.getParam (DIArguments.ARG_ARG5)) != null)
        {
            this.arg5 = text;
        } // if
        if ((text = this.env.getParam (DIArguments.ARG_ARG6)) != null)
        {
            this.arg6 = text;
        } // if
        if ((text = this.env.getParam (DIArguments.ARG_ARG7)) != null)
        {
            this.arg7 = text;
        } // if
        if ((text = this.env.getParam (DIArguments.ARG_ARG8)) != null)
        {
            this.arg8 = text;
        } // if
        if ((text = this.env.getParam (DIArguments.ARG_ARG9)) != null)
        {
            this.arg9 = text;
        } // if
    } // getParameters


    /**************************************************************************
     * Sets the m2AbsbasePath. This is the absolute file path to the m2
     * system directories and is stored the in session.
     *
     * @param m2AbsBasePath     the m2AbsBasePath
     */
    public void setm2AbsBasePath (String m2AbsBasePath)
    {
        this.m2AbsBasePath = m2AbsBasePath;
    } // setm2AbsBasePath


    /**************************************************************************
     * Sets the filePath of the importSource or exportdestination.
     *
     * @param   path    the destination or source path
     */
    public void setPath (String path)
    {
        this.path = FileHelpers.addEndingFileSeparator (path);
    } // setPath


    /**************************************************************************
     * Get the path of the logfile. <BR/>
     *
     * @return  the path of the logfile
     */
    public String getPath ()
    {
        return this.path;
    } // getPath


    /**************************************************************************
     * Sets the fileName of the import source or export destination. <BR/>
     *
     * @param   fileName    The name of the file to be set.
     */
    public void setFileName (String fileName)
    {
        // first check if the fileName containers any slashes
        // slashes or backslashes will be replaces by a "-"
        String fileN = fileName.replace ('/', '-');
        fileN = fileN.replace ('\\', '-');

        this.fileName = fileN;
    } // setFileName


    /**************************************************************************
     * Get the name of the log file. <BR/>
     *
     * @return  The name of the log file.
     */
    public String getFileName ()
    {
        return this.fileName;
    } // getFileName


    /**************************************************************************
     * Sets a filter for importfiles. The dir() method will only return
     * files that match the filter condition.<BR/>
     *
     * @param   fileFilter  The filter.
     */
    public void setFileFilter (String fileFilter)
    {
        this.fileFilter = fileFilter;
    } // setFileFilter


    /**************************************************************************
     * Get the filter for importfiles. <BR/>
     *
     * @return the file filter
     */
    public String getFileFilter ()
    {
        return this.fileFilter;
    } // getFileFilter


    /**************************************************************************
     * Get the response. <BR/>
     *
     * @return the response object or null in case it has not been set
     */
    public Response getResponse ()
    {
        return this.p_response;
    } // getResponse


    /**************************************************************************
     * Get the response string. <BR/>
     *
     * @return the response string or null in case it has not been set
     */
    public String getResponseStr ()
    {
        return this.p_responseStr;
    } // getResponseStr


    /**************************************************************************
     * This method gets the file prefix. <BR/>
     *
     * @return Returns the file prefix.
     */
    public String getFilePrefix ()
    {
        //get the property value and return the result:
        return this.p_filePrefix;
    } // getFilePrefix


    /**************************************************************************
     * This method sets the file prefix. <BR/>
     *
     * @param filePrefix The file prefix to set.
     */
    public void setFilePrefix (String filePrefix)
    {
        //set the property value:
        this.p_filePrefix = filePrefix;
    } // setFilePrefix


    /**************************************************************************
     * Reads an response from a stream. <BR/>
     *
     * @param   connection  the HttpUrlConnection to get the inputstream from
     *
     * @return  the response string or null in case it could not have been read
     */
    protected String readResponse (HttpURLConnection connection)
    {
        try
        {
            // did we get a connection?
            if (connection != null)
            {
                return this.readResponse (connection.getInputStream ());
            } // if

            return null;
        } // try
        catch (IOException e)
        {
            return null;
        } // catch (IOException e)
    } // readResponse


    /**************************************************************************
     * Reads an response from a stream. <BR/>
     *
     * @param   responseStream  the inputstream to read the response
     *
     * @return  the response string or null in case it could not have been read
     */
    protected String readResponse (InputStream responseStream)
    {
        StringBuffer stringBuffer;
        int c;

        // reset the response string
        this.p_responseStr = null;

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
                // close the stream
                responseStream.close ();
                // generate the response string
                this.p_responseStr = stringBuffer.toString ();
            } // if (responseStream != null)
        } // try
        catch  (IOException e)
        {
            IOHelpers.showMessage ("Reading response from stream",
                e, this.app, this.sess, this.env, true);
        } // catch

        // return the response string
        return this.p_responseStr;
    } // readResponse


    /**************************************************************************
     * Checks the response the connector got from his export destination. <BR/>
     *
     * @param   responseStr The string that holds the response xml structure.
     *
     * @throws  ConnectionFailedException
     *          A ConnectionFailedException will be thrown in case
     *          the response reported an error.
     */
    public void checkResponse (String responseStr)
        throws ConnectionFailedException
    {
        NodeList returnNodes;
        Document root;

        try
        {
            // check if we got a response string
            if (responseStr != null && responseStr.length () > 0)
            {
                // instantiate errorHandler:
                DIErrorHandler errorHandler = new DIErrorHandler ();
                errorHandler.setEnv (this.env);
                errorHandler.sess = this.sess;

                // read the document:
                // do not validate the document. This speeds up the parsing
                root = new XMLReader (new StringReader (responseStr), false, errorHandler).getDocument ();

                // get all <RETURN> nodes. the result should be exactly one node
                returnNodes = root.getElementsByTagName (DIConstants.ELEM_RETURN);
                // check if we exactly got one return node
                if (returnNodes.getLength () == 1)
                {
                    this.p_response = new Response ();
                    this.p_response.parseResponse (returnNodes.item (0));

                    // check if the response reported an error
                    if (this.p_response.getIsMultipartResponse ())
                    {
                        if (this.p_response.p_responseElements.size () ==
                            this.p_response.p_errorElements.size ())
                        {
                            throw new ConnectionFailedException (this.p_response.getErrorMessage ());
                        } // if (this.p_response.getResponseType () == DIConstants.RESPONSE_ERROR)
                    } // if
                    else
                    {
                        if (this.p_response.getResponseType () == DIConstants.RESPONSE_ERROR)
                        {
                            throw new ConnectionFailedException (this.p_response.getErrorMessage ());
                        } // if (this.p_response.getResponseType () == DIConstants.RESPONSE_ERROR)
                    } // else
                } // if (returnNodes == null)
            } // if (response.length () > 0)
        } // try
        catch (XMLReaderException e)
        {
            throw new ConnectionFailedException (e.getMessage (), e);
        } // catch
    } // checkResponse


    /**************************************************************************
     * Applys a filter to a file listing.<BR/>
     * This is a wrapper class that applies the filter set in the connectors
     * <A HREF="#fileFilter">fileFilter</A> property.
     *
     * @param files     an array of filenames
     *
     * @return  an array of strings containing the files that match the filter
     */
    public String [] applyFilter (String [] files)
    {
        return this.applyFilter (files, this.fileFilter);
    } // applyFilter


    /**************************************************************************
     * Applys a filter to the directory listing if set in the connector.<BR/>
     *
     * @param files         an array of filenames
     * @param filter        the filter to apply
     *
     * @return  an array of strings containing the files that match the filter
     */
    public String [] applyFilter (String [] files, String filter)
    {
        String [] newFiles = null;
        int matched = 0;
        int j;
        int i;
        int matchType;
        String matchStr = "";
        String matchPostfixStr = "";

        // check if a filter has been set and is not empty
        if (files != null && filter != null && filter.length () > 0)
        {
            // check if the filter is the asterix
            if (filter.equals (Connector_01.MATCH_ASTERIX))
            {
                return files;
            } // if

            // set the match type
            if (filter.indexOf (Connector_01.MATCH_ASTERIX) == -1)
            {
                matchType = Connector_01.MATCHTYPE_EQUALS;
                matchStr = filter;
            } // if (filter.indexOf (MATCH_ASTERIX) == -1)
            else if (filter.startsWith (Connector_01.MATCH_ASTERIX) &&
                     filter.endsWith (Connector_01.MATCH_ASTERIX))
            {
                matchType = Connector_01.MATCHTYPE_CONTAINS;
                matchStr = filter.substring (1, filter.length () - 1);
            } // else if (filter.startsWith (MATCH_ASTERIX) && ...
            else if (filter.endsWith (Connector_01.MATCH_ASTERIX))
            {
                matchType = Connector_01.MATCHTYPE_STARTWITH;
                matchStr = filter.substring (0, filter.length () - 1);
            } // else if (filter.startsWith (MATCH_ASTERIX))
            else if (filter.startsWith (Connector_01.MATCH_ASTERIX))
            {
                matchType = Connector_01.MATCHTYPE_ENDSWITH;
                matchStr = filter.substring (1, filter.length ());
            } // else if (filter.startsWith (MATCH_ASTERIX))
            else                        // filter must contains an * in the middle
            {
                matchType = Connector_01.MATCHTYPE_IN;
                matchStr = filter.substring (0, filter.indexOf (Connector_01.MATCH_ASTERIX));
                matchPostfixStr = filter.substring (filter.indexOf (Connector_01.MATCH_ASTERIX) + 1);
            } // filter must contains an * in the middle

            // loop through the file names array and delete entries
            // that do not match the filter
            for (i = 0; i < files.length; i++)
            {
                // if the filename matches the filter we increase a counter
                // else the position in the array will be deleted by setting it
                // to null
                switch (matchType)
                {
                    case Connector_01.MATCHTYPE_EQUALS:
                        if (!files[i].equalsIgnoreCase (matchStr))
                        {
                            files[i] = null;
                        } // if
                        else
                        {
                            matched++;
                        } // else
                        break;
                    case Connector_01.MATCHTYPE_STARTWITH:
                        if (!files[i].startsWith (matchStr))
                        {
                            files[i] = null;
                        } // if
                        else
                        {
                            matched++;
                        } // else
                        break;
                    case Connector_01.MATCHTYPE_ENDSWITH:
                        if (!files[i].endsWith (matchStr))
                        {
                            files[i] = null;
                        } // if
                        else
                        {
                            matched++;
                        } // else
                        break;
                    case Connector_01.MATCHTYPE_CONTAINS:
                        if (files[i].indexOf (matchStr) == -1)
                        {
                            files[i] = null;
                        } // if
                        else
                        {
                            matched++;
                        } // else
                        break;
                    case Connector_01.MATCHTYPE_IN:
                        if (!(files[i].startsWith (matchStr) &&
                              files[i].endsWith (matchPostfixStr)))
                        {
                            files[i] = null;
                        } // if
                        else
                        {
                            matched++;
                        } // else
                        break;
                    default:
                        break;
                } // switch (matchtype)
            } // for (int i = 0; i < filesToReturn.length (); i ++)

            // check if there have been any files that did not match
            if (matched < i)
            {
                // create a new string array that will contain the result
                newFiles = new String [matched];
                j = 0;
                for (i = 0; i < files.length; i++)
                {
                    // is it a deleted fileName
                    if (files[i] != null)
                    {
                        // if not add it to the result array
                        newFiles [j++] = files[i];
                    } // if (filesToReturn [i] != null)
                } // for (int i = 0; i < filesToReturn.length (); i ++)
                // overwrite the original array
                return newFiles;
            } // if (matched < i)
        } // if (this.fileFilter != null &&  this.fileFilter.length () > 0)´

        return files;
    } // applyFilter


    /**************************************************************************
     * Sorts a list of files alphabethically.<BR/>
     *
     * @param   files   An array of filenames.
     */
    public void sortFiles (String [] files)
    {
        if (files != null && files.length > 1)
        {
            // sort the array
            Arrays.sort (files, new StringComparator ());
        } // if (files != null && files.size ())
    } // sortFiles


    /**************************************************************************
     * Sorts a list of files alphabethically using a certain order direction.<BR/>
     *
     * @param   files       An array of filenames.
     * @param   ordering    The ordering direction.
     */
    public void sortFiles (String [] files, int ordering)
    {
        if (files != null && files.length > 1)
        {
            // sort the array
            Arrays.sort (files, new StringComparator (ordering));
        } // if (files != null && files.size ())
    } // sortFiles


    /**************************************************************************
     * Creates a temporary directory. <BR/>
     *
     * @return  The path of the temporaty directory or
     *          <CODE>null</CODE> in case the directory could not be created.
     */
    public String createTempDir ()
    {
        String tempBasePath = this.m2AbsBasePath + DIConstants.PATH_TEMPROOT;
        String tempPath = DIConstants.PATH_TEMPPATH;
        // make a unique temporary directory
        String tempPathName = FileHelpers.getUniqueFileName (tempBasePath, tempPath);
        // create the temporary directory
        tempPath = tempBasePath + tempPathName;

        if (FileHelpers.makeDir (tempPath, true))
        {
            // mark that the temp directory should be deleted
            this.isDeleteTemp = true;
            return tempPath;
        } // if (FileHelpers.makeDir (this.path))

        // could not create the temporary directory
        return null;
    } // createTempDir


    /**************************************************************************
     * Deletes the temporary directory. <BR/>
     */
    protected void deleteTempDir ()
    {
        // delete to content of the directory first:
        String [] tempFiles = FileHelpers.getFilesArray (this.path);
        if (tempFiles != null)
        {
            for (int i = 0; i < tempFiles.length; i++)
            {
                FileHelpers.deleteFile (this.path + tempFiles[i]);
            } // for i
        } // if (tempFiles != null)
        // now delete the temp directory

        FileHelpers.deleteDir (this.path);
        // delete the path setting
        this.path = "";
    } // deleteTempDir


    /**************************************************************************
     * Sets the arguments to class specific properties. <BR/>
     * Must be overwritten in the subclasses.<BR/>
     */
    public void setArguments ()
    {
        // has to be implemented in the subclasses
    } // setArguments


    /**************************************************************************
     * Initializes the connector. Will be overwritten in the subclasses in meet
     * the specific need of the various connectors.<BR/>
     * This is a wrapper class and enables the creation of a temp directory is
     * applicable.<BR/>
     *
     * @throws  ConnectionFailedException
     *          The connection could not be established
     */
    public void initConnector ()
        throws ConnectionFailedException
    {
        // has to be implemented in the subclasses
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
        this.initConnector ();

        // remember that the connector is used for backup:
        this.p_isBackupConnector = true;

        // specifics have to be implemented in the subclasses
    } // initBackupConnector


    /**************************************************************************
     * Closes the connector. This method includes all actions that need to
     * be done to close a connection to a data source or destination
     * and deletes the temp directory if applicable.<BR/>
     * Must be overwritten in the subclasses.<BR/>
     */
    public void close ()
    {
        // has to be implemented in the subclasses
    } // close


    /**************************************************************************
     * The dir method reads from the import source and returns all importable
     * objects found in a array of strings.<BR/>
     * Must be overwritten in the subclasses.<BR/>
     *
     * @return  An array of strings containing the importable objects found or
     *          <CODE>null</CODE> otherwise.
     *
     * @throws  ConnectionFailedException
     *          The connection could not be established.
     */
    public String[] dir ()
        throws ConnectionFailedException
    {
        // has to be implemented in the subclasses
        return null;
    } // dir


    /**************************************************************************
     * Writes the files specified in the sourceFileNames array to the
     * export destination. <BR/>
     *
     * @param   fileNames   an array with the source files nanes
     *
     * @exception   ConnectionFailedException
     *              The connection could not be established
     */
    public void write (String[] fileNames)
        throws ConnectionFailedException
    {
        // check if source path and destination path are identical
        for (int i = 0; i < fileNames.length; i++)
        {
            try
            {
                // write the file to the destination
                this.write (fileNames[i]);
            } // try
            catch (ConnectionFailedException e)
            {
                throw e;
            } // catch
        } // for (int i = 0; i <= sourceFileNames.length; i++)
    } // write


    /**************************************************************************
     * Writes the file specified in the fileName property to the
     * export destination. <BR/>
     *
     * @exception   ConnectionFailedException
     *              The connection could not be established
     */
    public void write ()
        throws ConnectionFailedException
    {
        try
        {
            // write the file to the destination
            this.write (this.fileName);
        } // try
        catch (ConnectionFailedException e)
        {
            throw e;
        } // catch
    } // write


    /**************************************************************************
     * Writes the specified file to the export destination. <BR/>
     * Must be overwritten in the subclasses.<BR/>
     *
     * @param   fileName    the name of the source file
     *
     * @exception   ConnectionFailedException
     *              The connection could not be established
     */
    public void write (String fileName)
        throws ConnectionFailedException
    {
        // has to be implemented in the subclasses
    } // write


    /**************************************************************************
     * Retrieves the files specifed in the fileNames array from the
     * import source and writes it into the directory from where the
     * integrator reads the files.<BR/>
     *
     * @param   fileNames   an array with the source files
     *
     * @exception   ConnectionFailedException
     *              The connection could not be established
     */
    public void read (String[] fileNames)
        throws ConnectionFailedException
    {
        // check if source path and destination path are identical
        for (int i = 0; i < fileNames.length; i++)
        {
            try
            {
                // read the file from the source
                this.read (fileNames[i]);
            } // try
            catch (ConnectionFailedException e)
            {
                throw e;
            } // catch
        } // for (int i = 0; i <= sourceFileNames.length; i++)
    } // read


    /**************************************************************************
     * Retrieves the files specifed in the fileNames array from the
     * import source and writes it into the directory from where the
     * integrator reads the files.<BR/>
     *
     * @throws  ConnectionFailedException
     *          The connection could not be established.
     */
    public void read ()
        throws ConnectionFailedException
    {
        try
        {
            // read the file from the source
            this.read (this.fileName);
        } // try
        catch (ConnectionFailedException e)
        {
            throw e;
        } // catch
    } // read


    /**************************************************************************
     * Retrieves the specified file from the source and writes it
     * into the directory from where the integrator reads the files.<BR/>
     * Must be overwritten in the subclasses.<BR/>
     *
     * @param   fileName    the name of the file to read
     *
     * @throws  ConnectionFailedException
     *          The file could not have been read via the connector.
     */
    public void read (String fileName)
        throws ConnectionFailedException
    {
        // has to be implemented in the subclasses
    } // read


    /**************************************************************************
     * Read a file from the connector and copy it to the destination path. <BR/>
     * This is meant for attachment like files that can have a different
     * handling as importfiles depending on the connector used.<BR/>
     *
     * @param fileName              name of the file to read
     * @param destinationPath       the path to write the file to
     * @param destinationFileName   name of the copied file.
     *                              If empty fileName will be used.
     *
     * @return the size of the file in case it could have been read successfully or
     *         -1 if an error occurred or the file has not been found
     *
     * @throws  ConnectionFailedException
     *          Could not access the file.
     */
    public long readFile (String fileName, String destinationPath,
                          String destinationFileName)
        throws ConnectionFailedException
    {
        // has to be implemented in the subclasses
        return -1;
    }   // readFile


    /**************************************************************************
     * Write an file to a connector. <BR/>
     * This is meant for attachment like files. The method has to ensure a
     * unique file name. It returns the file name used to write the file and
     * null in case the file could not have been written. <BR/>
     *
     * @param sourcePath        path to read the file from
     * @param fileName          name of the file to read
     *
     * @return the name of the file written or null in case it could not have
     *         been written
     *
     * @exception   ConnectionFailedException
     *              could not access the file
     */
    public String writeFile (String sourcePath, String fileName)
        throws ConnectionFailedException
    {
        // has to be implemented in the subclasses
        return fileName;
    }   // writeFile


    /**************************************************************************
     * Delete a file from its original location via the connector.<BR/>
     * This will be used in case the "delete file after import" option
     * has been set within an import and is meant for all sorts of
     * attachment like files.
     * This can be used for the importfile itself and for attachment like
     * files.<BR/>
     *
     * @param fileName            name of the file to delete
     *
     * @return true if the file could be deleted or false otherwiese
     *
     * @throws  ConnectionFailedException
     *          Could not access the file.
     */
    public boolean deleteFile (String fileName)
        throws ConnectionFailedException
    {
        // has to be implemented in the subclasses
        return true;
    }   // deleteFile


    /**************************************************************************
     * Displays the settings of the connector.<BR/>
     *
     * @param   table       Table where the settings shall be added.
     */
    public void showSettings (TableElement table)
    {
        if (this.p_isBackupConnector)   // connector used for backup?
        {
            this.showProperty (table, DIArguments.ARG_BACKUPCONNECTOR,
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                    DITokens.ML_BACKUPCONNECTOR, env),
                Datatypes.DT_NAME, this.name);
        } // if connector used for backup
        else                            // standard connector
        {
            this.showProperty (table, DIArguments.ARG_CONNECTOR,
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                    DITokens.ML_CONNECTOR, env),
                Datatypes.DT_NAME, this.name);
        } // else standard connector
        this.showProperty (table, DIArguments.ARG_CONNECTORTYPENAME,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_CONNECTORTYPENAME, env),
            Datatypes.DT_TEXT, this.typeName);
    } // showSettings


    /**************************************************************************
     * Adds the settings of the connector to a log.<BR/>
     *
     * @param   log     the log to add the setting to
     */
    public void addSettingsToLog (Log_01 log)
    {
//showDebug ("--- START addSettingsToLog ---");
        log.add (DIConstants.LOG_ENTRY,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_CONNECTOR, env) + ": " + this.name, false);
        log.add (DIConstants.LOG_ENTRY,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_CONNECTORTYPENAME, env) + ": " + this.typeName, false);
    } // addSettingsToLog


    /***************************************************************************
     * Represent the properties of a DocumentTemplate_01 object to the user. <BR/>
     *
     * @param   table       Table where the properties should be added.
     *
     * @see ibs.bo.BusinessObject#showProperties
     */
    protected void showProperties (TableElement table)
    {
        super.showProperties (table);
        // display the properties for import/export connector
        this.showProperty (table, DIArguments.ARG_ISIMPORTCONNECTOR,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_ISIMPORTCONNECTOR, env),
            Datatypes.DT_BOOL, "" + this.isImportConnector);
        this.showProperty (table, DIArguments.ARG_ISEXPORTCONNECTOR,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_ISEXPORTCONNECTOR, env),
            Datatypes.DT_BOOL, "" + this.isExportConnector);
        // display a separator line
        this.showProperty (table, null, null, Datatypes.DT_SEPARATOR, (String) null);
    } //  showProperties


     /***************************************************************************
     * Represent the properties of a Connector_01 object to the user
     * within a form. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     *
     * @see ibs.bo.BusinessObject#showFormProperties
     */
    protected void showFormProperties (TableElement table)
    {
        super.showFormProperties (table);
        // display a separator line
        this.showProperty (table, null, null, Datatypes.DT_SEPARATOR, (String) null);
        // display the properties for import/export connector
        if (this.isImportEnabled)
        {
            this.showFormProperty (table, DIArguments.ARG_ISIMPORTCONNECTOR,
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                    DITokens.ML_ISIMPORTCONNECTOR, env),
                Datatypes.DT_BOOL, "" + this.isImportConnector);
        } // if (this.isImportEnabled)
        else            // connector if disabled for import
        {
            this.showProperty (table, DIArguments.ARG_ISIMPORTCONNECTOR,
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                    DITokens.ML_ISIMPORTCONNECTOR, env),
                Datatypes.DT_BOOL, "" + false);
        } // // connector if disabled for import
        if (this.isExportEnabled)
        {
            this.showFormProperty (table, DIArguments.ARG_ISEXPORTCONNECTOR,
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                    DITokens.ML_ISEXPORTCONNECTOR, env),
                Datatypes.DT_BOOL, "" + this.isExportConnector);
        } // if
        else            // connector if disabled for export
        {
            this.showProperty (table, DIArguments.ARG_ISEXPORTCONNECTOR,
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                    DITokens.ML_ISEXPORTCONNECTOR, env),
                Datatypes.DT_BOOL, "" + false);
        } // else connector if disabled for export
        // display a separator line
        this.showProperty (table, null, null, Datatypes.DT_SEPARATOR, (String) null);
        // add a hidden element here for the connectortype
        // this will be handled by a radio box later
        this.showFormProperty (table, DIArguments.ARG_CONNECTORTYPE,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_CONNECTORTYPE, env),
            Datatypes.DT_HIDDEN, "" + this.getConnectorType ());
    } // showFormProperties


    /***************************************************************************
     * Copies the arguments into an other connector object. <BR/>
     *
     * @param connector     the connector object to copy the arguments to
     */
    protected void copyArguments (Connector_01 connector)
    {
        connector.arg1 = this.arg1;
        connector.arg2 = this.arg2;
        connector.arg3 = this.arg3;
        connector.arg4 = this.arg4;
        connector.arg5 = this.arg5;
        connector.arg6 = this.arg6;
        connector.arg7 = this.arg7;
        connector.arg8 = this.arg8;
        connector.arg9 = this.arg9;
    } // copyArguments


    ///////////////////////////////////////////////////////////////////////////
    // database functions
    ///////////////////////////////////////////////////////////////////////////
    /**************************************************************************
     * Set the data for the additional (type specific) parameters for
     * performChangeData. <BR/>
     * This method must be overwritten by all subclasses that have to pass
     * type specific data to the change data stored procedure.
     *
     * @param sp        The stored procedure to add the change parameters to.
     */
    @Override
    protected void setSpecificChangeParameters (StoredProcedure sp)
    {
        // set the specific parameters:
        // Connector type
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, this.getConnectorType ());
        // isImportConnector
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN, this.isImportConnector);
        // isExportConnector
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN, this.isExportConnector);
        // arg1
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.arg1);
        // arg2
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.arg2);
        // arg3
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.arg3);
        // arg4
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.arg4);
        // arg5
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.arg5);
        // arg6
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.arg6);
        // arg7
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.arg7);
        // arg8
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.arg8);
        // arg9
        sp.addInParameter (ParameterConstants.TYPE_STRING, this.arg9);
    } // setSpecificChangeParameters


    /**************************************************************************
     * Set the data for the additional (type specific) parameters for
     * performRetrieveData. <BR/>
     * This method must be overwritten by all subclasses that have to pass
     * type specific data to the retrieve data stored procedure.
     *
     * @param sp        The stored procedure the specific retrieve parameters
     *                  should be added to.
     * @param params    Array of parameters the specific retrieve parameters
     *                  have to be added to for beeing able to retrieve the
     *                  results within getSpecificRetrieveParameters.
     * @param lastIndex The index to the last element used in params thus far.
     *
     * @return  The index of the last element used in params.
     */
    @Override
    protected int setSpecificRetrieveParameters (StoredProcedure sp, Parameter[] params,
                                                 int lastIndex)
    {
        int i = lastIndex;              // initialize params index

        // set the specific parameters:
        // Connector type
        params[++i] = sp.addOutParameter(ParameterConstants.TYPE_INTEGER);
        // isImportConnector
        params[++i] = sp.addOutParameter(ParameterConstants.TYPE_BOOLEAN);
        // isExportConnector
        params[++i] = sp.addOutParameter(ParameterConstants.TYPE_BOOLEAN);
        // arg1
        params[++i] = sp.addOutParameter(ParameterConstants.TYPE_STRING);
        // arg2
        params[++i] = sp.addOutParameter(ParameterConstants.TYPE_STRING);
        // arg3
        params[++i] = sp.addOutParameter(ParameterConstants.TYPE_STRING);
        // arg4
        params[++i] = sp.addOutParameter(ParameterConstants.TYPE_STRING);
        // arg5
        params[++i] = sp.addOutParameter(ParameterConstants.TYPE_STRING);
        // arg6
        params[++i] = sp.addOutParameter(ParameterConstants.TYPE_STRING);
        // arg7
        params[++i] = sp.addOutParameter(ParameterConstants.TYPE_STRING);
        // arg8
        params[++i] = sp.addOutParameter(ParameterConstants.TYPE_STRING);
        // arg9
        params[++i] = sp.addOutParameter(ParameterConstants.TYPE_STRING);

        return i;                       // return the current index
    } // setSpecificRetrieveParameters


    /***************************************************************************
     * Get the data for the additional (type specific) parameters for
     * performRetrieveData. <BR/>
     * This method must be overwritten by all subclasses that have to get
     * type specific data from the retrieve data stored procedure.
     *
     * @param params        The array of parameters from the retrieve data stored
     *                      procedure.
     * @param lastIndex     The index to the last element used in params thus far.
     */
    protected void getSpecificRetrieveParameters (Parameter[] params,
                                                  int lastIndex)
    {
        int i = lastIndex;              // initialize params index

        // get the specific parameters:
        // this may look like a little bit confusing, but we have to set an
        // valid connector which is choosen in getConnectorType:
        this.setConnectorType (this.getConnectorType (params[++i].getValueInteger ()));

        this.isImportConnector = params[++i].getValueBoolean ();
        this.isExportConnector = params[++i].getValueBoolean ();
        this.arg1 = params[++i].getValueString ();
        this.arg2 = params[++i].getValueString ();
        this.arg3 = params[++i].getValueString ();
        this.arg4 = params[++i].getValueString ();
        this.arg5 = params[++i].getValueString ();
        this.arg6 = params[++i].getValueString ();
        this.arg7 = params[++i].getValueString ();
        this.arg8 = params[++i].getValueString ();
        this.arg9 = params[++i].getValueString ();
    } // getSpecificRetrieveParameters


    /**************************************************************************
     * Returns the type of the current connector. <BR/>
     *
     * @param   type        The type of the connector.
     *
     * @return  The type of the current connector.
     */
    public int getConnectorType (int type)
    {
        if (type == DIConstants.CONNECTORTYPE_NONE)
        {
            return this.getConnectorType ();
        } // if

        return type;
    } // getConnectorType


    /**************************************************************************
     * Returns the type of the current connector. <BR/>
     *
     * @return  The type of the current connector.
     */
    public int getConnectorType ()
    {
        return this.connectorType;
    } // getConnectorType


    /**************************************************************************
     * Sets the type of the current connector. <BR/>
     *
     * @param   type        The type of the connector.
     */
    public void setConnectorType (int type)
    {
        this.connectorType = type;
    } // setConnectorType


    /**************************************************************************
     * Reads the object data from an dataElement <BR/>
     *
     * @param dataElement     the dataElement to read the data from
     *
     * @see ibs.bo.BusinessObject#readImportData
     */
    public void readImportData (DataElement dataElement)
    {
        // get business object specific values
        super.readImportData (dataElement);
        // get the type specific values
        if (dataElement.exists ("connectorType"))
        {
            this.setConnectorType (dataElement.getImportIntValue ("connectorType"));
        } // if
        if (dataElement.exists ("isImportConnector"))
        {
            this.isImportConnector = dataElement.getImportBooleanValue ("isImportConnector");
        } // if
        if (dataElement.exists ("isExportConnector"))
        {
            this.isExportConnector = dataElement.getImportBooleanValue ("isExportConnector");
        } // if
        if (dataElement.exists ("arg1"))
        {
            this.arg1 = dataElement.getImportStringValue ("arg1");
        } // if
        if (dataElement.exists ("arg2"))
        {
            this.arg2 = dataElement.getImportStringValue ("arg2");
        } // if
        if (dataElement.exists ("arg3"))
        {
            this.arg3 = dataElement.getImportStringValue ("arg3");
        } // if
        if (dataElement.exists ("arg4"))
        {
            this.arg4 = dataElement.getImportStringValue ("arg4");
        } // if
        if (dataElement.exists ("arg5"))
        {
            this.arg5 = dataElement.getImportStringValue ("arg5");
        } // if
        if (dataElement.exists ("arg6"))
        {
            this.arg6 = dataElement.getImportStringValue ("arg6");
        } // if
        if (dataElement.exists ("arg7"))
        {
            this.arg7 = dataElement.getImportStringValue ("arg7");
        } // if
        if (dataElement.exists ("arg8"))
        {
            this.arg8 = dataElement.getImportStringValue ("arg8");
        } // if
        if (dataElement.exists ("arg9"))
        {
            this.arg9 = dataElement.getImportStringValue ("arg9");
        } // if
    } // readImportData


    /**************************************************************************
     * writes the object data to an dataElement <BR/>
     *
     * @param dataElement     the dataElement to write the data to
     *
     * @see ibs.bo.BusinessObject#writeExportData
     */
    public void writeExportData (DataElement dataElement)
    {
        // set the business object specific values
        super.writeExportData (dataElement);
        // set the connector specific values
        dataElement.setExportValue ("connectorType", this.getConnectorType ());
        dataElement.setExportValue ("isImportConnector", this.isImportConnector);
        dataElement.setExportValue ("isExportConnector", this.isExportConnector);
        // set the connector arguments
        dataElement.setExportValue ("arg1", this.arg1);
        dataElement.setExportValue ("arg2", this.arg2);
        dataElement.setExportValue ("arg3", this.arg3);
        dataElement.setExportValue ("arg4", this.arg4);
        dataElement.setExportValue ("arg5", this.arg5);
        dataElement.setExportValue ("arg6", this.arg6);
        dataElement.setExportValue ("arg7", this.arg7);
        dataElement.setExportValue ("arg8", this.arg8);
        dataElement.setExportValue ("arg9", this.arg9);
    } // writeExportData

} // Connector_01
