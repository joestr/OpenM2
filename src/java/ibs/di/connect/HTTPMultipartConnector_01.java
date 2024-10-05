/*
 * Class: HTTPMultipartConnector_01.java
 */

// package:
package ibs.di.connect;

// imports:
//TODO: unsauber
import ibs.bo.BOTokens;
//TODO: unsauber
import ibs.bo.Datatypes;
//TODO: unsauber
import ibs.bo.OID;
import ibs.di.DIArguments;
import ibs.di.DIConstants;
import ibs.di.DIHTTPConstants;
import ibs.di.DIMessages;
import ibs.di.DITokens;
import ibs.di.Log_01;
import ibs.di.connect.ConnectionFailedException;
import ibs.di.connect.Connector_01;
//TODO: unsauber
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.TableElement;
import ibs.tech.http.HttpArguments;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;
import ibs.util.file.FileHelpers;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownServiceException;
import java.util.Vector;


/******************************************************************************
 * The HTTPMultipartConnector_01 Class writes files as "multipart/form-data" to
 * an http server CGI script. <BR/>
 *
 * @version     $Id: HTTPMultipartConnector_01.java,v 1.18 2010/04/07 13:37:05 rburgermann Exp $
 *
 * @author      Daniel Janesch (DJ), 020312
 ******************************************************************************
 */
public class HTTPMultipartConnector_01 extends Connector_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: HTTPMultipartConnector_01.java,v 1.18 2010/04/07 13:37:05 rburgermann Exp $";


    /**
     * URL of the script that will read the export file.<BR/>
     */
    public String p_exportUrl = null;

    /**
     * MIME type used to send the export file.<BR/>
     */
    public String p_exportMIMETYPE  = DIConstants.MIMETYPE_FORMDATA;

    /**
     * additional attachments for the export stream.<BR/>
     * Vector that holds path-name strings for the files to be added to the
     * output stream.<BR/>
     */
    private Vector<String[]> p_attachments = new Vector<String[]> ();


    ////////////////////////////////////////////////////////////////////////////
    // not yet implemented methods START
    ////////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * The dir method reads from the import source and returns all importable
     * objects found in a array of strings.<BR/>
     * <B>ATTENTION!! Not implemented in the HTTPMultipartConnector_01.</B><BR/>
     *
     * @return  An array of strings containing the importable objects found or
     *          <CODE>null</CODE> otherwise.
     *
     * @throws  ConnectionFailedException
     *          The connection could not be established.
     */
    public String[] dir () throws ConnectionFailedException
    {
        // !!ATTENTION!!
        // This method is not implemented in this class!!
        // !!ATTENTION!!
        return null;
    } // dir


    /**************************************************************************
     * Retrieves the specified file from the source and writes it
     * into the directory from where the integrator reads the files.<BR/>
     * <B>ATTENTION!! Not implemented in the HTTPMultipartConnector_01.</B><BR/>
     *
     * @param   fileName    the name of the file to read
     *
     * @throws  ConnectionFailedException
     *          The file could not have been read via the connector.
     */
    public void read (String fileName) throws ConnectionFailedException
    {
        // !!ATTENTION!!
        // This method is not implemented in this class!!
        // !!ATTENTION!!
    } // read


    /**************************************************************************
     * Read a file from the connector and copy it to the destination path. <BR/>
     * This is meant for attachment like files that can have a different
     * handling as importfiles depending on the connector used.<BR/>
     * <B>ATTENTION!! Not implemented in the HTTPMultipartConnector_01.</B><BR/>
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
        String destinationFileName) throws ConnectionFailedException
    {
        // !!ATTENTION!!
        // This method is not implemented in this class!!
        // !!ATTENTION!!
        return -1;
    } // dir


    /**************************************************************************
     * Delete a file from its original location via the connector.<BR/>
     * This will be used in case the "delete file after import" option
     * has been set within an import and is meant for all sorts of
     * attachment like files.
     * This can be used for the importfile itself and for attachment like
     * files.<BR/>
     * <B>ATTENTION!! Not implemented in the HTTPMultipartConnector_01.</B><BR/>
     *
     * @param fileName            name of the file to delete
     *
     * @return true if the file could be deleted or false otherwiese
     *
     * @throws  ConnectionFailedException
     *          Could not access the file.
     */
    public boolean deleteFile (String fileName) throws ConnectionFailedException
    {
        // !!ATTENTION!!
        // This method is not implemented in this class!!
        // !!ATTENTION!!
        return false;
    } // dir

    ////////////////////////////////////////////////////////////////////////////
    // not yet implemented methods END
    ////////////////////////////////////////////////////////////////////////////


    /**************************************************************************
     * Creates a HTTPMultipartConnector_01 Object. <BR/>
     */
    public HTTPMultipartConnector_01 ()
    {
        // call constructor of super class Connector_01:
        super ();
    } // HTTPMultipartConnector_01


    /**************************************************************************
     * Creates a HTTPMultipartConnector_01 Object. <BR/>
     *
     * @param   oid     oid of the object.
     * @param   user    user that created the object.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public HTTPMultipartConnector_01 (OID oid, User user)
    {
        // call constructor of super class Connector_01:
        super (oid, user);
    } // HTTPMultipartConnector_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // call the super method because everything is equal except the
        // connectorType:
        super.initClassSpecifics ();

        // set class specifics:
        this.connectorType = DIConstants.CONNECTORTYPE_HTTPMULTIPART;

        // this conntector can not be used for import:
        this.isImportEnabled = false;
        this.isImportConnector = false;
    } // initClassSpecifics


    /**************************************************************************
     * Sets the arguments to class specific properties. <BR/>
     */
    public void setArguments ()
    {
        this.p_exportUrl = this.arg4;
        this.p_exportMIMETYPE = this.arg6;
        if (this.p_exportMIMETYPE == null)
        {
            this.p_exportMIMETYPE = DIConstants.MIMETYPE_FORMDATA;
        } // if
    } // setArguments


    /*************************************************************************
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
            if (this.isExportConnector) // should the connector be used for export?
            {
                // construct a url from the string
                URL exportScriptURL  = new URL (this.p_exportUrl);

                if (!exportScriptURL.getProtocol ().equalsIgnoreCase (DIConstants.HTTP_PROTOCOL))
                                        // are the necessary parameters for the
                                        // export connector set?
                {
                    throw new ConnectionFailedException ( 
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_PROTOCOL_NOT_SUPPORTED, env));
                } // if are the necessary parameters for the export connector set?
            } // if should the connector be used for export?
        } // try
        catch (IOException e)
        {
            throw new ConnectionFailedException ( 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_HTTP_CONNECTION_FAILED, env));
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
     * Writes the export file to the export destination.<BR/>
     * The method creates an outputstream that will contain the export file and
     * any additional files. The file will be the first file in the
     * outputstream. <BR/>
     *
     * @param   fileName        The name of the source file
     *
     * @exception   ConnectionFailedException
     *              The connection could not be established
     */
    public void write (String fileName)
        throws ConnectionFailedException
    {
        HttpURLConnection con = null;   // Connection type for the output stream
        DataOutputStream outputStream = null;
                                        // The output stream to wirte the file(s)
        URL url = null;                 // The target url
        Vector<String> params = null;   // The parameter(s) contained in the url
        StringBuffer outBuffer = new StringBuffer ();
                                        // Containing the whole output stream
        String[] tmpPathName = null;    // Temporary array for path-name tuple
        String urlStr = "";             // The target url as string
        String fileParamName = "id";    // The name of the data object file
        String responseStr = null;

        // create the output buffer:
        try
        {
            // construct the url
            urlStr = this.p_exportUrl;
            url = new URL (urlStr);

            // open the connection:
            con = (HttpURLConnection) url.openConnection ();

            // set the connection for input/output:
            con.setDoInput (true);
            con.setDoOutput (true);

            // set the mime type of the connection to "multipart/form-data":
            con.setRequestProperty (DIHTTPConstants.CONTENT_TYPE,
                this.p_exportMIMETYPE + DIHTTPConstants.CONTENT_TYPEDEL +
                StringHelpers.replace (DIHTTPConstants.CONTENT_BOUNDARY,
                    UtilConstants.TAG_NAME,
                    DIHTTPConstants.MULTIPART_BOUNDARY));

            // get the parameters out of the url:
            params = this.getURLParameters (urlStr);

            // add all possible parameters to the output buffer:
            if (params != null && params.size () > 0)
                                        // are there some parameters ?
            {
                // parse the params vector:
                for (int i = 0; i < params.size (); i += 2)
                {
                    // add one name-value section to output buffer:
                    outBuffer.append (this.getMultipartVar (params.elementAt (i),
                        params.elementAt (i + 1)));
                } // for i
            } // if are there some parameters

            // the dataobject file:
            try
            {
                // add one name-filename-file section to output buffer:
                outBuffer.append (this.getMultipartFile (fileParamName,
                    this.getPath (), fileName));
            } // try
            catch (FileNotFoundException e)
            {
                throw new ConnectionFailedException (e.getMessage ());
            } // catch


            // attachment file(s):
            if (this.p_attachments != null && this.p_attachments.size () > 0)
                                        // are there files to send ?
            {
                int j = 1;
                for (int i = 0; i < this.p_attachments.size (); i++, j++)
                {
                    // add to the param name the number of the file in the vector:
                    fileParamName = "id" + (j < 10 ? "0" + j : "" + j);

                    // get the next path-name string array out of the vector:
                    tmpPathName = this.p_attachments.elementAt (i);

                    try
                    {
                        // add one name-filename-file section to output buffer:
                        outBuffer.append (this.getMultipartFile (fileParamName,
                            tmpPathName[0], tmpPathName[1]));
                    } // try
                    catch (FileNotFoundException e)
                    {
                        throw new ConnectionFailedException (e.getMessage ());
                    } // catch
                } // for i
            } // if are there files send

            // end:
            outBuffer.append (this.getMultipartEnd ());

            // set the content length
            con.setRequestProperty (DIHTTPConstants.CONTENT_LENGTH, "" + outBuffer.length ());

        } // try
        catch (MalformedURLException e)
        {
            throw new ConnectionFailedException ("Export-" +  
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_MALFORMEDURL, env));
        } // catch
        catch (IOException e)
        {
            throw new ConnectionFailedException ( 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_COULDNOTWRITETOCONNECTOR, env));
        } // catch

        // open a connection and try to send the output buffer:
        try
        {
            // get the output stream to the connection
            outputStream = new DataOutputStream (con.getOutputStream ());

            // write the string to the stream:
            outputStream.writeBytes (outBuffer.toString ());

            // the response from the connection where we wrote the informations:

            if (con.getResponseCode () != HttpURLConnection.HTTP_OK)
                                        // an error occurred ?
            {
                throw new ConnectionFailedException ( 
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_HTTP_MULTIPART_ERROR, 
                        new String[] {con.getResponseMessage (), 
                            "" + con.getResponseCode (), url.getPath ()}, env));
            } // an error occurred

            // read the response
            // get the response str
            responseStr = this.readResponse (con);
            // check the response
            this.checkResponse (responseStr);

            // close the output stream:
            outputStream.close ();
        } // try
        catch (UnknownServiceException e)
        {
            throw new ConnectionFailedException ( 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_COULDNOTWRITETOCONNECTOR, env));
        } // catch
        catch (IOException e)
        {
            throw new ConnectionFailedException ( 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_COULDNOTWRITETOCONNECTOR, env));
        } // catch
        finally
        {
            try
            {
                // Check if the outputStream was created.
                // If it was created close it, otherwise do nothing:
                if (outputStream != null)
                {
                    outputStream.close ();
                } // if
            } // try
            catch (IOException e1)
            {
                throw new ConnectionFailedException ( 
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_COULDNOTWRITETOCONNECTOR, env));
            } // catch
        } // finally
    } // write


    /**************************************************************************
     * Write an file to a connector. <BR/>
     * For a HTTPMultipartConnector this means that the path and the name of
     * the file is added to the attachments vector and will be added to the
     * output stream when the export is written. <BR/>
     *
     * @param sourcePath        path to read the file from
     * @param fileName          name of the file to read
     *
     * @return  The name of the file written or null in case it could not have
     *          been written.
     *
     * @exception   ConnectionFailedException
     *              could not access the file
     *
     * @see ibs.di.connect.Connector_01#writeFile
     */
    public String writeFile (String sourcePath, String fileName)
        throws ConnectionFailedException
    {
        // ensure ending file separator
        String srcPath = FileHelpers.addEndingFileSeparator (sourcePath);
        // create the file
        File file = new File (srcPath + fileName);

        // check if file exists and is really a file and not a directory
        if (file.exists () && file.isFile ())
        {
            String[] pathName = new String[2];
            pathName[0] = srcPath;
            pathName[1] = fileName;

            // add the path and name of file to the attachments vector:
            this.p_attachments.addElement (pathName);
        } // if (FileHelpers.exists (sourcePath + fileName))
        else                            // file does not exist
        {
            throw new ConnectionFailedException ( 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_COULD_NOT_WRITE_FILE, env));
        } // else file does not exist
        // create the file
        return fileName;
    } // writeFile


    /***************************************************************************
     * Creates an vector which contains all parameters of the given url in the
     * following form: <CODE><B>name1,value1, name2,value2, ...,
     *                 nameN,valueN</B></CODE><BR/>
     *
     * @param   url     The url from which you want to get the parameters out of.
     *
     * @return  A vector which contains all parameters of the given url.
     *          If the given url contains no parameters <CODE><B>null</B></CODE>
     *          is returned.
     */
    private Vector<String> getURLParameters (String url)
    {
        URL tmpURL = null;
        Vector<String> params = null;
        String urlParams = "";
        String urlParam = "";
        int paramSepPos = -1;
        int assignPos = -1;

        try
        {
            tmpURL = new URL (url);
            urlParams = tmpURL.getQuery ();
        } // try
        catch (MalformedURLException e)
        {
            return null;
        } // catch

        if (urlParams != null)          // are there paramters in the url ?
        {
            // add the '&' that we have to do nothing after the end of the wile:
            urlParams = "&" + urlParams;

            // initialize the vector:
            params = new Vector<String> ();

            // parse the url parameters and store it into an vector:
            while ((paramSepPos = urlParams.indexOf (HttpArguments.ARG_SEP)) > -1)
            {
                // get the next parameter out of the url:
                urlParams = urlParams.substring (paramSepPos + 1);
                // get the next '&' which is the start for the next parameter:
                if ((paramSepPos = urlParams.indexOf (HttpArguments.ARG_SEP)) == -1)
                {
                    paramSepPos = urlParams.length ();
                } // if
                // get the next parameter out of the parameter string:
                urlParam = urlParams.substring (0, paramSepPos);

                // now the name value tuple is splitted into two strings:
                // the the position of the '=' out of the name=value string:
                assignPos = urlParam.indexOf (HttpArguments.ARG_ASSIGN);
                // add the name of the parameter to the vector:
                params.addElement (urlParam.substring (0, assignPos));
                // add the value of the parameter to the vector:
                if (assignPos < urlParam.length ())
                {
                    params.addElement (urlParam.substring (assignPos + 1));
                } // if
                else
                {
                    params.addElement (new String (""));
                } // else
            } // while
        } // if are there paramters in the url

        return params;
    } // getURLParameters


    /***************************************************************************
     * Creates a string of an "multipart/form-data" name-value section. <BR/>
     * This name-value section is equal to the "multipart/form-data"
     * definition (<B>www.ietf.org/rfc/rfc1867.txt</B>). <BR/>
     * It looks like the following (<CODE>AXBCE ... is the boundary</CODE>):<BR/>
     * <CODE><B>--AXBCE<BR/>
     * content-disposition: form-data; name="NAME". <BR/>
     * VALUE<BR/></B></CODE>
     *
     * @param   name        Name of the multipart-variable.
     *                      (<CODE><B>NAME</B></CODE>)
     * @param   value       Value of the multipart-variable.
     *                      (<CODE><B>VALUE</B></CODE>)
     *
     * @return  The name-value section.
     */
    private String getMultipartVar (String name, String value)
    {
        StringBuffer multipartVar = new StringBuffer ();

        // add the section separator:
        multipartVar.append (DIHTTPConstants.MULTIPART_PARAMDEL);
        // add the boundary:
        multipartVar.append (DIHTTPConstants.MULTIPART_BOUNDARY);
        // add a line break:
        multipartVar.append (DIHTTPConstants.MULTIPART_LINEBREAK);
        // add the name:
        multipartVar.append (StringHelpers.replace (DIHTTPConstants.MULTIPART_NAME,
            UtilConstants.TAG_NAME, name));
        // add a line break:
        multipartVar.append (DIHTTPConstants.MULTIPART_LINEBREAK);
        // add a line break:
        multipartVar.append (DIHTTPConstants.MULTIPART_LINEBREAK);
        // add the value:
        multipartVar.append (value);
        // add a line break:
        multipartVar.append (DIHTTPConstants.MULTIPART_LINEBREAK);

        // return the name-value section:
        return multipartVar.toString ();
    } // getMultipartVar


    /***************************************************************************
     * Creates a string of an "multipart/form-data" name-filename-file
     * section. <BR/>
     * This name-filename-file section is equal to the "multipart/form-data"
     * definition (<B>www.ietf.org/rfc/rfc1867.txt</B>). <BR/>
     * It looks like the following (<CODE>AXBCE ... is the boundary</CODE>):<BR/>
     * <CODE><B>--AXBCE<BR/>
     * content-disposition: form-data; name="NAME"; filename="FNAME". <BR/>
     * Content-Type: CONTTYPE<BR/><BR/>
     * FILECONT  ...<BR/></B></CODE>
     *
     * @param   name            Name of the multipart-variable.
     *                          (<CODE><B>NAME</B></CODE>)
     * @param   path            The path of the file to add to the section.
     * @param   filename        The name of the file to add to the section.
     *
     * @return  The name-filename-file section or <CODE><B>null</B></CODE> if
     *          an error occurred.
     *
     * @exception   FileNotFoundException
     *              Some problem at reading the file.
     */
    private String getMultipartFile (String name, String path, String filename)
        throws FileNotFoundException
    {
        StringBuffer multipartFile = new StringBuffer ();

        if (path == null || path.length () == 0)
                                        // don't we get an path ?
        {
            throw new FileNotFoundException ( 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_EMPTY_EXPORT_STRING,
                    new String[] {MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                        DITokens.ML_PATH, env)}, env));
        } // if did we get an path

        if (filename == null || filename.length () == 0)
                                        // don't we get an filename ?
        {
            throw new FileNotFoundException (
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_EMPTY_EXPORT_STRING,
                    new String[] {MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                        BOTokens.ML_NAME, env)}, env));
        } // if did we get an filename

        // add the section separator:
        multipartFile.append (DIHTTPConstants.MULTIPART_PARAMDEL);
        // add the boundary:
        multipartFile.append (DIHTTPConstants.MULTIPART_BOUNDARY);
        // add a line break:
        multipartFile.append (DIHTTPConstants.MULTIPART_LINEBREAK);
        // add the name:
        multipartFile.append (StringHelpers.replace (DIHTTPConstants.MULTIPART_NAME,
            UtilConstants.TAG_NAME, name));
        // add a separator:
        multipartFile.append (DIHTTPConstants.MULTIPART_DISPDEL);
        // add the filename:
        multipartFile.append (StringHelpers.replace (DIHTTPConstants.MULTIPART_FILENAME,
            UtilConstants.TAG_NAME, filename));
        // add a line break:
        multipartFile.append (DIHTTPConstants.MULTIPART_LINEBREAK);
        // add the content type of the file:
        multipartFile.append (DIHTTPConstants.CONTENT_TYPE + ": ");
        multipartFile.append (this.getMimeType (filename));
        // add a line break:
        multipartFile.append (DIHTTPConstants.MULTIPART_LINEBREAK);
        // add a line break:
        multipartFile.append (DIHTTPConstants.MULTIPART_LINEBREAK);
        try
        {
            // add the file content:
            multipartFile.append (this.getFileCont (path + filename));
        } // try
        catch (FileNotFoundException e)
        {
            throw new FileNotFoundException (e.getMessage ());
        } // catch
        // add a line break:
        multipartFile.append (DIHTTPConstants.MULTIPART_LINEBREAK);

        // return the name-filename-file section:
        return multipartFile.toString ();
    } // getMultipartFile


    /***************************************************************************
     * Returns the "mimeType" of the file. <BR/>
     * The "mimeType" would be choosen by the extension of the fileName. <BR/>
     * If the extension is not defined yet in this method or no extension is
     * given, <CODE><B>application/octet-stream</B></CODE> will be returned.
     * <BR/>
     * <B>ATTENTION!! This method just knows the extension ".xml". For all other
     * extension "application/octet-stream" is returned.</B>
     *
     * @param   fileName        The name of the file.
     *
     * @return  If the extension is known the "mimeType" of the file,
     *          otherwise <CODE><B>application/octet-stream</B></CODE>. <BR/>
     */
    protected String getMimeType (String fileName)
    {
        String mimeType = "application/octet-stream";

        if (fileName.endsWith (DIConstants.FILEEXTENSION_XML))
        {
            mimeType = DIHTTPConstants.MIMETYPE_TEXTXML;
        } // if

        return mimeType;
    } // getMimeType


    /***************************************************************************
     * Returns the content of the file. <BR/>
     *
     * @param   filePathName    Path and name of the file.
     *
     * @return  The content of the file. <BR/>
     *
     * @exception   FileNotFoundException
     *              Some problem at reading the file.
     *
     */
    protected String getFileCont (String filePathName) throws FileNotFoundException
    {
        DataInputStream inputStream = null;
        String filename =
            filePathName.substring (filePathName.lastIndexOf (File.separator));
                                        // the filename out of the filePathName
        byte[] fileCont = null;

        try
        {
            // set the input stream to the file we want to export:
            inputStream = new DataInputStream (
                new BufferedInputStream (new FileInputStream (filePathName)));

            // read the export file:
            fileCont = new byte[inputStream.available ()];
            inputStream.read (fileCont);

            // close the inputstream:
            inputStream.close ();
        } // try
        catch (IOException e)
        {
            throw new FileNotFoundException ( 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_COULD_NOT_READ_FILE,
                    new String[] {filename}, env));
        } // catch
        finally
        {
            try
            {
                // close the inputstream:
                inputStream.close ();
            } // try
            catch (IOException e1)
            {
                throw new FileNotFoundException ( 
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_COULD_NOT_READ_FILE,
                        new String[] {filename}, env));
            } // catch
        } // finally

        try
        {
            return new String (fileCont, DIConstants.CHARACTER_ENCODING);
        } // try
        catch (UnsupportedEncodingException e)
        {
            return new String ("");
        } // catch
    } // getFileCont


    /***************************************************************************
     * Returns the end-section of an "multipart/form-data" content. <BR/>
     * This multipart end-section is equal to the "multipart/form-data"
     * definition (<B>www.ietf.org/rfc/rfc1867.txt</B>). <BR/>
     * It looks like the following (<CODE>AXBCE ... is the boundary</CODE>):<BR/>
     * <CODE><B>--AXBCE--</B></CODE>
     *
     * @return  If the extension is known the "mimeType" of the file,
     *          otherwise <CODE><B>text/plain</B></CODE>.
     */
    protected String getMultipartEnd ()
    {
        StringBuffer multipartVar = new StringBuffer ();

        // add the section separator:
        multipartVar.append (DIHTTPConstants.MULTIPART_PARAMDEL);
        // add the boundary:
        multipartVar.append (DIHTTPConstants.MULTIPART_BOUNDARY);
        // add the section separator:
        multipartVar.append (DIHTTPConstants.MULTIPART_PARAMDEL);
        // add 2 carriage returns
        multipartVar.append (DIHTTPConstants.MULTIPART_LINEBREAK);
        multipartVar.append (DIHTTPConstants.MULTIPART_LINEBREAK);

        // return the name-value section:
        return multipartVar.toString ();
    } // getMultipartEnd


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

        this.showProperty (table, DIArguments.ARG_ARG4, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_WRITEURL, env),
            Datatypes.DT_TEXT, this.arg4);
        this.showProperty (table, DIArguments.ARG_ARG6,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EXPORTMIMETYPE, env),
            Datatypes.DT_TEXT, this.arg6);
    } // showProperties


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

        this.showFormProperty (table, DIArguments.ARG_ARG4,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_WRITEURL, env),
            Datatypes.DT_URL, this.arg4);
        this.showProperty (table, DIArguments.ARG_ARG6,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EXPORTMIMETYPE, env),
            Datatypes.DT_TEXT, this.p_exportMIMETYPE);
    } // showFormProperties


    /**************************************************************************
     * Displays the settings of the connector.<BR/>
     *
     * @param   table       Table where the settings shall be added.
     */
    public void showSettings (TableElement table)
    {
        super.showSettings (table);

        this.showProperty (table, DIArguments.ARG_ARG4,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_WRITEURL, env),
            Datatypes.DT_TEXT, this.arg4);
        this.showProperty (table, DIArguments.ARG_ARG6,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EXPORTMIMETYPE, env),
            Datatypes.DT_TEXT, this.arg6);
    } // showSettings


    /**************************************************************************
     * Adds the settings of the connector to a log.<BR/>
     *
     * @param   log     the log to add the setting to
     */
    public void addSettingsToLog (Log_01 log)
    {
        super.addSettingsToLog (log);
        // add the connector specific setting to the log
        log.add (DIConstants.LOG_ENTRY, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_WRITEURL, env) + ": " +
            this.p_exportUrl , false);
        log.add (DIConstants.LOG_ENTRY, 
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_EXPORTMIMETYPE, env) + ": " +
            this.p_exportMIMETYPE , false);
    } // addSettingsToLog

} // HTTPMultipartConnector_01
