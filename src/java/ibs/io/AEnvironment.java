/*
 * Class AEnvironment
 */

// package:
package ibs.io;

// imports:
//TODO: unsauber
import ibs.bo.BOArguments;
//TODO: unsauber
import ibs.bo.IncorrectOidException;
//TODO: unsauber
import ibs.bo.OID;
import ibs.io.AContext;
import ibs.io.Environment;
import ibs.io.IOConstants;
import ibs.io.Ssl;
import ibs.io.UploadException;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.IUserInfo;
import ibs.io.session.SessionInfo;
import ibs.tech.http.FormDataDictionary;
import ibs.tech.http.FormDataValue;
import ibs.util.DateTimeHelpers;

import java.util.Date;
import java.util.StringTokenizer;


/******************************************************************************
 * This is the AEnvironment Object, an abstract Superclass of all
 * specific implementations of an Environment.
 *
 * @version     $Id: AEnvironment.java,v 1.29 2010/07/13 15:59:33 btatzmann Exp $
 *
 * @author      Christine Keim (CK), 990404
 ******************************************************************************
 */
public abstract class AEnvironment extends Object implements Environment
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: AEnvironment.java,v 1.29 2010/07/13 15:59:33 btatzmann Exp $";


    ///////////////////////////////////////////////////////////////////////////
    // protected properties
    ///////////////////////////////////////////////////////////////////////////

    /**
     * The vector containing the data of a form. <BR/>
     */
    protected FormDataDictionary formData = null;

    /**
     * Content type of HTTP request. <BR/>
     */
    protected String contentType;
    /**
     * Boundary within multipart form. <BR/>
     */
    protected String multipartBoundary = "--";

    /**
     * Bytes gotten from the input stream. <BR/>
     */
    protected byte[] bStream = null;

    /**
     * Indicates if env.write will be ignored or executed. <BR/>
     */
    protected boolean p_writeEnabled = true;

    /**
     * The list of valid domains to check for the NTLM authentication.<BR/>
     */
    protected String p_validDomains = "";

    /**
     * Parameter value: true. <BR/>
     */
    private static final String PARAMVALUE_TRUE = "true";
    /**
     * Parameter value: false. <BR/>
     */
    private static final String PARAMVALUE_FALSE = "false";

    /**
     * Application info object. <BR/>
     */
    private ApplicationInfo p_appInfo = null;

    /**
     * Session info object. <BR/>
     */
    private SessionInfo p_sessInfo = null;

    /**
     * Indicates for a specific type if objects of this type may be
     * initialized. <BR/>
     * Note: This is a functionality which may only be used for special cases
     * like type translation, etc.
     */
    private String p_instantiationAllowedTypeCode = null;


    ///////////////////////////////////////////////////////////////////////////
    // public properties
    ///////////////////////////////////////////////////////////////////////////

    /**
     * The type of the form. <BR/>
     */
    public int type = 0;

    /**
     * The Context of the Environment. <BR/>
     * To have access to the method readFormStream
     * even in this class this property has to be set.
     */
    public AContext acxt = null;


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a new instance of the AEnvironment.
     */
    public AEnvironment ()
    {
        // nothing to do
    } // AEnvironment


    ///////////////////////////////////////////////////////////////////////////
    // protected methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Initialize the environment. <BR/>
     *
     * @throws  UploadException
     *          Exception during uploading files.
     */
    protected final void initEnvironment () throws UploadException
    {
        String contentTypeAddInfo = ""; // additional information of content
                                        // type
        int pos = 0;                    // actual position within string

        this.contentType = this.getServerVariable (IOConstants.SV_CONTENT_TYPE);
        if (this.contentType == null)
        {
            this.contentType = "";
        } // if
        if ((pos = this.contentType.indexOf (IOConstants.CONT_DELIMITER)) != -1)
            // there is some additional information?
        {
            contentTypeAddInfo = this.contentType.substring (pos + 1);
            this.contentType = this.contentType.substring (0, pos);
        } // if there is some additional information

        if (this.contentType.equals (IOConstants.CONT_WWWFORM)) // www form?
        {
            this.type = IOConstants.CT_WWWFORM;
            // get the data out of the environment:
            this.formData = this.parseWWWForm ();
            //            write ("WWW form." + IE302.TAG_NEWLINE);
        } // if www form
        else if (this.contentType.equals (IOConstants.CONT_MULTIPARTFORM)) // multipart form?
        {
            this.type = IOConstants.CT_MULTIPARTFORM;
            if ((pos = contentTypeAddInfo.indexOf ("=")) != -1)
            {
                this.multipartBoundary = contentTypeAddInfo.substring (pos + 1);
            } // if
            // get the multipart data out of the stream:
            this.formData = this.parseMultipart ();
        } // else if multipart form
        else                            // unknown content type
        {
            this.type = IOConstants.CT_WWWGET;
            this.formData = this.parseWWWGet ();
            //            write ("unknown content type." + IE302.TAG_NEWLINE);
        } // else unknown content type

    } // initEnvironment


    /**************************************************************************
     * Gets the www parameters sent by the client. <BR/>
     *
     * @return  A string containing the data sent over the stream.
     */
    protected abstract FormDataDictionary parseWWWForm ();


    /**************************************************************************
     * Gets the www parameters sent by the client with the get method. <BR/>
     *
     * @return  A string containing the data sent over the stream.
     */
    protected abstract FormDataDictionary parseWWWGet ();


    /**************************************************************************
     * Parse the input stream for multipart data. <BR/>
     *
     * @return  A vector containing the several data parts of the stream.
     *
     * @throws  UploadException
     *          An error occurred during reading the upload stream.
     */
    protected final FormDataDictionary parseMultipart () throws UploadException
    {
        FormDataDictionary result;      // the resulting dictionary consisting
        // of the several parts of the input
        // stream

        // input stream
        FormDataValue formDataValue = null; // actual form data element's value

        // create a new FormDataDictionary with no elements
        result = new FormDataDictionary (null);

        // set the delimiter
        String delim = "" + (char) 10 + "-------";     // delimiter within the stream
        if (this.multipartBoundary != null)
        {
            delim = (char) 10 + "--" + this.multipartBoundary;
        } // if

        // set the multipartBoundary (delimiter) of the Context
        this.acxt.setBoundary ((char) 13 + delim);

        // cache the UploadException
        // it is necessary that all other
        // parameters are read
        // the exception is thrown afterwards again
        UploadException backup = null;

        //write ("The delimiter is-->" + delim + IE302.TAG_NEWLINE);
        while (!this.acxt.eof)
        {
            try
            {
                // get one Value out of the Stream
                formDataValue = new FormDataValue (null, 0, this.acxt);
                // put it into the RequestDictionary
                result.putValue (formDataValue);
            } // try
            catch (UploadException e)
            {
                // UploadException has been thrown
                formDataValue = null;
                // cache the UploadException
                backup = e;
            } // catch UploadException
        } // while the stream has not been read completely

        if (backup != null)
        {
            // to guarantee the formData to be kept
            // the global variable is set here
            this.formData = result;
            // throw the Exception
            throw backup;
        } // if

        return result;                  // return the result vector
    } // parseMultipart


    /**************************************************************************
     * Disassembles a String (depends on a given tokenizer). <BR/>
     *
     * @param   string      The string to be splitted.
     * @param   separator   The separator between each two parts of the string.
     *
     * @return   An array of the parts of the string or <CODE>null</CODE> if
     *          the string is <CODE>null</CODE>.
     */
    protected final String[] parseString (String string, String separator)
    {
        String[] result;

        if (string != null)
        {
            StringTokenizer tokenizer = new StringTokenizer (string, separator);
            result = new String[tokenizer.countTokens ()];
            for (int i = 0; i < result.length; i++)
            {
                result[i] = tokenizer.nextToken ();
                result[i] = result[i].trim ();
            } // for
        } // if
        else
        {
            result = null;
        } // else
        return result;
    } // parseString


    /**************************************************************************
     * Drop all form data. <BR/>
     * Within this method all form data contents are thrown away. After this it
     * is not longer possible to access any of the form data values. <BR/>
     * The property <A HREF="#formData">formData</A> is initialized to be empty.
     */
    public final void dropFormData ()
    {
        // create a new FormDataDictionary with no elements
        this.formData = new FormDataDictionary (null);
    } // dropFormData


    /**************************************************************************
     * Clears the Context (to free the memory). <BR/>
     */
    public abstract void destroy ();


    /**************************************************************************
     * Gets the browser and returns a String with the identified browser. <BR/>
     *
     * @return  String with browser type. (see IOConstants for possible values)
     */
    public final String getBrowser ()
    {

        // we do not differentiate the browsers anymore
        // all browser shall read their stylesheets from a common directory
        // we therefore return the name if the css directory as browsername
        String browser = IOConstants.ALL_BROWSERS;

/* BB20070606:
        // get the user agent:
        String temp = getServerVariable (IOConstants.SV_USER_AGENT);

        // check if the user agent was found:
        if (temp != null)               // user agent found?
        {
            if (temp.indexOf ("MSIE 4.0") > -1 ||
                temp.indexOf ("MSIE 5") > -1 ||
                temp.indexOf ("MSIE 6") > -1)
            {
                browser = IOConstants.MSIE4;
            } // if
            else if (temp.indexOf ("Netscape6") > -1)
            {
                browser = IOConstants.NS6;
            } // else
            else if (temp.charAt (8) == '3')
            {
                browser = IOConstants.NS3;
                // temp.substring (8,12);
            } // else if
            else
            {
                // set default browser type:
                browser = IOConstants.NS4;
            } // else
        } // if user agent found
        else                                    // user agent not found
        {
            // set default browser type:
            browser = IOConstants.NS4;
        } // else user agent not found
*/
        return browser;
    } // getBrowser


    /**************************************************************************
     * Gets a server variable, whose name must be given. <BR/>
     *
     * @param   name    Name of the server variable (see class IOConstants).
     *
     * @return  Value of the server variable.
     */
    public abstract String getServerVariable (String name);


    /**************************************************************************
     * Gets a HTTP-header variable, whose name must be given. <BR/>
     *
     * @param   name    Name of the header variable (see class IOConstants).
     *
     * @return  Value of the header variable.
     */
    public abstract String getHttpHeaderVariable (String name);


    /**************************************************************************
     * Gets the name of the request server.
     *
     * @return  Name of the server, <CODE>null</CODE> if not found.
     */
    public String getServerName ()
    {
        return this.getServerVariable (IOConstants.SV_SERVER_NAME);
    } // getServerName


    /**************************************************************************
     * Get the path of the actual request, i.e. the everything until the last
     * '/'.
     *
     * @param   sslRequired A boolean value which indicates the actual context
     *                      (secure mode or insecure one).
     * @param   sess        The actual session info.
     *
     * @return  The url path.
     */
    public String getPath (boolean sslRequired,
                           SessionInfo sess)
    {
        // get the actual url after the server name:
        String path = this.getServerVariable (IOConstants.SV_URL);
        int pos = -1;                   // position of last '/'

        // cut the end of the path:
        if (path != null && (pos = path.lastIndexOf ('/')) >= 0)
                                        // path contains a string and a slash
        {
            // get the value until the last slash in the string
            path = path.substring (0, pos + 1);
        } // end if path contains a string and a slash

        // depending on the actual context the url returned is
        // for the secure mode or insecure one.
        if (sslRequired)      // SSL is required and enabled
        {
            return Ssl.getSecureUrl (path, sess);
        } // if SSL is required

        // SSL is not required
        return Ssl.getNonSecureUrl (path, sess);
    } // getPath


    /**************************************************************************
     * Sets the value of an application variable (given name) to the given
     * object. <BR/>
     *
     * @param   name    Name of the application variable to set.
     * @param   value   Object to set the application variable to.
     */
    public abstract void setApplicationObject (String name, Object value);


    /**************************************************************************
     * Gets the value of the application variable (given name). <BR/>
     *
     * @param   name    Name of the application variable to set.
     *
     * @return  Value of the application variable.
     */
    public abstract String getApplicationValue (String name);


    /**************************************************************************
     * Gets the value of an integer application variable (given name). <BR/>
     *
     * @param   name    Name of the application variable to get.
     *
     * @return  Value of the application variable.
     */
    public final int getIntApplicationValue (String name)
    {
        int num  = -1;
        String param = this.getApplicationValue (name);

        if (param != null && param.length () > 0) // parameter exists?
        {
            try
            {
                // read the parameter:
                num = Integer.parseInt (param);
            } // try
            catch (NumberFormatException e) // parameter not correctly set?
            {
                num = -1;               // set default value
            } // catch
        } // if parameter exists

        return num;
    } // getIntApplicationValue


    /**************************************************************************
     * Gets the object in an application variable (given name). <BR/>
     *
     * @param   name    Name of the application object to get.
     *
     * @return  The object stored in the application variable.
     */
    public abstract Object getApplicationObject (String name);


    /**************************************************************************
     * Set the content type of the response. <BR/>
     * This is the MIME type of the response as it is sent to the client. <BR/>
     * ATTENTION! This method should be called before calling write.
     *
     * @param   strContentType  String representation of the content type.
     *
     */
    public abstract void setContentType (String strContentType);


    /**************************************************************************
     * Adds an name value tuple to the http-header. <BR/>
     * ATTENTION!! If the value name is 'Content-disposition' the prefix
     * 'filename=' is automaticaly added to the value.
     *
     * @param   name            Name of the variable to add to the header.
     * @param   value           Value of the variable to add to the header.
     *
     */
    public abstract void addHeaderEntry (String name, String value);


    /**************************************************************************
     * Sets the value of the session variable (given name) to the given object.
     * <BR/>
     *
     * @param   name    Name of the session variable to set.
     * @param   value   Object to set the session variable to.
     */
    public abstract void setSessionObject (String name, Object value);


    /**************************************************************************
     * Gets the value in a session variable (given name). <BR/>
     *
     * @param   name    Name of the session variable to get.
     *
     * @return  Value stored in the session variable or
     *          <CODE>null</CODE> if the session variable was not found.
     */
    public abstract String getSessionValue (String name);


    /**************************************************************************
     * Gets the object in a session variable (given name). <BR/>
     *
     * @param   name    Name of the session variable to get.
     *
     * @return  Object stored in the session variable or
     *          <CODE>null</CODE> if the session variable was not found.
     */
    public abstract Object getSessionObject (String name);


    /**************************************************************************
     * Gets the value of an integer session variable (given name). <BR/>
     *
     * @param   name    Name of the session variable to get.
     *
     * @return  Value of the session variable as integer or
     *          <CODE>0</CODE> if the session variable was not found.
     */
    public final int getIntSessionValue (String name)
    {
        int num  = -1;
        String param = this.getSessionValue (name);

        if (param != null && param.length () > 0) // parameter exists?
        {
            try
            {
                // read the parameter:
                num = Integer.parseInt (param);
            } // try
            catch (NumberFormatException e) // parameter not correctly set?
            {
                num = -1;               // set default value
            } // catch
        } // if parameter exists

        return num;
    } // getIntSessionValue


    /**************************************************************************
     * Writes a String to the response object. <BR/>
     *
     * @param   text    String to write (max. 255 characters).
     */
    public abstract void write (String text);

    
    /**************************************************************************
     * Writes the given data byte[] to the response.
     * Additionally a byte order mark can be provided.<BR/>
     *
     * @param   data            Data to write.
     * @param   byteOrderMark   The byte order mark to write to the response.
     *                          Needed in case of writing files.
     */
    public abstract void write (byte[] data, byte[] byteOrderMark);


    /**************************************************************************
     * Disables write functionality: env.write will be lost. <BR/>
     */
    public void disableWrite ()
    {
        // set the property:
        this.p_writeEnabled = false;
    } // disableWrite


    /**************************************************************************
     * Enable write functionality. <BR/>
     */
    public void enableWrite ()
    {
        // set the property:
        this.p_writeEnabled = true;
    } // disableWrite


    /**************************************************************************
     * Checks if a session variable exists. <BR/>
     *
     * @param   name    Name of the session variable.
     *
     * @return <CODE>true</CODE> = exists; <CODE>false</CODE> = does not exist.
     */
    public final boolean existsSessionVariable (String name)
    {
        if (this.getSessionValue (name) == null)
        {
            return false;
        } // if

        return true;
    } // existsSessionVariable


    /**************************************************************************
     * Checks if an application variable exists. <BR/>
     *
     * @param   name    Name of the application variable.
     *
     * @return <CODE>true</CODE> = exists; <CODE>false</CODE> = does not exist.
     */
    public final boolean existsApplicationVariable (String name)
    {
        if (this.getApplicationValue (name) == null)
        {
            return false;
        } // if

        return true;
    } // existsApplicationVariable


    /**************************************************************************
     * Gets the base-URL
     *
     * @return  The base URL.
     */
    public final String getBaseURL ()
    {
        return this.getServerVariable (IOConstants.SV_URL);
    } // getBaseUrl


    /**************************************************************************
     * returns the right string. For servlets returns the forServlet String
     * for other types (ASP for example) returns the forOther string
     *
     * @param   forServlets String which is used for servlets.
     * @param   forOther    String which is used for other application kinds.
     *
     * @return  The base URL.
     */
    public abstract String getBaseURL (String forServlets, String forOther);


    /**************************************************************************
     * Gets the value of a URL parameter (GET method). <BR/>
     *
     * @param   name    Name of the parameter to get.
     *
     * @return <CODE>true</CODE> = exists; <CODE>false</CODE> = does not exist
     */
    public final String getURLParam (String name)
    {
        return this.getParam (name);
    } // getURLParam


    /**************************************************************************
     * Gets the values of a multiple URL parameter (GET method). <BR/>
     *
     * @param   name    Name of the parameter to get.
     *
     * @return  The form element splitted into an array of strings.
     */
    public final String[] getMultipleURLParam (String name)
    {
        return this.formData.getMultipleString (name);
        // return parseString (getURLParam (name), ",");
    } // getMultipleURLParam


    /**************************************************************************
     * Gets the value of a form parameter (POST method). <BR/>
     *
     * @param   name    Name of the parameter to get.
     *
     * @return  The value of the parameter or <CODE>null</CODE> if the parameter
     *          does not exist.
     */
    public final String getFormParam (String name)
    {
        // get the value by name:
        return this.formData.getString (name);
    } // getFormParam


    /**************************************************************************
     * Gets the value of a multiple parameter indifferent of the method. <BR/>
     *
     * @param   name    Name of the parameter to get.
     *
     * @return  Array of Strings of values.
     */
    public String[] getMultipleParam (String name)
    {
        return this.formData.getMultipleString (name);
        /*
        if (getServerVariable (IOConstants.SV_REQUEST_METHOD).equalsIgnoreCase ("GET"))
        return getMultipleURLParam (name);
        else
        return getMultipleFormParam (name);
        */
    } // getMultipleParam


    /**************************************************************************
    * Gets the values of a multiple form parameter (POST method). <BR/>
    *
    * @param   name    Name of the parameter to get.
    *
    * @return  The form element splitted into an array of strings.
    */
    // Get the value of a form (POST Method)
    public final String[] getMultipleFormParam (String name)
    {
        return this.formData.getMultipleString (name);
    } // getMultipleFormParam


    /**************************************************************************
     * Gets the value of a parameter indifferent of the method. <BR/>
     *
     * @param   name    Name of the parameter to get.
     *
     * @return  The parameter value or <CODE>null</CODE> if it does not exist.
     */
    public final String getParam (String name)
    {
        return this.formData.getString (name);
    } // getParam


    /**************************************************************************
     * Gets a parameter of type OID. <BR/>
     *
     * @param   name    Name of parameter to read.
     *
     * @return  The oid if the parameter exists or <CODE>null</CODE> otherwise.
     */
    public final OID getOidParam (String name)
    {
        String param = null;
        OID oid = null;

        // get parameter:
        param = this.getParam (name);
        if (param != null && param.length () > 0) // parameter exists?
        {
            try
            {
                oid = new OID (param);
            } // try
            catch (IncorrectOidException e)
            {
                // should not occur
                // the return value will be null to indicate the error
            } // catch
        } // if parameter exists

        return oid;                     // return the computed oid
    } // getOidParam


    /**************************************************************************
     * Gets a parameter of type integer. <BR/>
     *
     * @param   name    Name of parameter to read.
     *
     * @return  The integer value if the parameter exists or
     *          <CODE>INTPARAM_NOTEXISTS_OR_INVALID</CODE> otherwise.
     */
    public final int getIntParam (String name)
    {
        String param = null;
        int num = IOConstants.INTPARAM_NOTEXISTS_OR_INVALID;

        // get order column
        param = this.getParam (name);
        if (param != null && param.length () > 0) // parameter exists?
        {
            try
            {
                // read the parameter:
                num = Integer.parseInt (param);
            } // try
            catch (NumberFormatException e) // parameter not correctly set?
            {
                num = IOConstants.INTPARAM_NOTEXISTS_OR_INVALID; // set not-exists-or-invalid value
            } // catch
        } // if parameter exists

        return num;                     // return the computed number
    } // getIntParam


    /**************************************************************************
     * Gets a parameter of type float. <BR/>
     *
     * @param   name    Name of parameter to read.
     *
     * @return  The integer value if the parameter exists or
     *          <CODE>-1</CODE> otherwise.
     */
    public final float getFloatParam (String name)
    {
        String param = null;
        float num = -1;

        // get order column
        param = this.getParam (name);
        if (param != null && param.length () > 0) // parameter exists?
        {
            try
            {
                // read the parameter:
                num = Float.valueOf (param).floatValue ();
            } // try
            catch (NumberFormatException e) // parameter not correctly set?
            {
                num = -1;               // set default value
            } // catch
        } // if parameter exists

        return num;                     // return the computed number
    } // getFloatParam


    /**************************************************************************
     * Gets a parameter of type double. <BR/>
     *
     * @param   name    Name of parameter to read.
     *
     * @return  The integer value if the parameter exists or
     *          <CODE>-1</CODE> otherwise.
     */
    public final double getDoubleParam (String name)
    {
        String param = null;
        double num = -1;

        // get order column
        param = this.getParam (name);
        if (param != null && param.length () > 0) // parameter exists?
        {
            try
            {
                // read the parameter:
                num = Double.valueOf (param).doubleValue ();
            } // try
            catch (NumberFormatException e) // parameter not correctly set?
            {
                num = -1;               // set default value
            } // catch
        } // if parameter exists

        return num;                     // return the computed number
    } // getDoubleParam


    /**************************************************************************
     * Gets a parameter of type string. <BR/>
     *
     * @param   name    Name of parameter to read.
     *
     * @return  The string value if the parameter exists or
     *          <CODE>null</CODE> otherwise.
     */
    public final String getStringParam (String name)
    {
        // get the parameter value and return it:
        return this.getParam (name);
    } // getStringParam


    /**************************************************************************
     * Gets a parameter of type DateTime. <BR/>
     *
     * @param   name    Name of parameter to read.
     *
     * @return  The date/time if the parameter exists or
     *          <CODE>null</CODE> otherwise.
     */
    public final Date getDateTimeParam (String name)
    {
        String dateParam = null;
        String timeParam = null;
        Date date = null;

        // get date parameter:
        dateParam = this.getParam (name + BOArguments.ARG_DATE_EXTENSION);

        // get time parameter:
        timeParam = this.getParam (name + BOArguments.ARG_TIME_EXTENSION);

        if (dateParam != null && dateParam.length () > 0)
            // date parameter exists?
        {
            if (timeParam != null && timeParam.length () > 0)
            {
                // time parameter exists?
                date = DateTimeHelpers.stringToDateTime (dateParam + " " + timeParam);
            } // if
            else                        // time parameter does not exist
            {
                date = DateTimeHelpers.stringToDate (dateParam);
            } // else
        } // if date parameter exists
        else                            // date parameter does not exist
        {
            if (timeParam != null && timeParam.length () > 0)
            {
                // time parameter exists?
                date = DateTimeHelpers.stringToTime (timeParam);
            } // if
        } // else date parameter does not exist

        return date;                    // return the computed date/time
    } // getDateTimeParam


    /**************************************************************************
     * Gets a parameter of type Date. <BR/>
     *
     * @param   name    Name of parameter to read.
     *
     * @return  The date if the parameter exists or
     *          <CODE>null</CODE> otherwise.
     */
    public final Date getDateParam (String name)
    {
        String param = null;
        Date date = null;

        // get parameter:
        param = this.getParam (name);
        if (param != null && param.length () > 0) // parameter exists?
        {
            date = DateTimeHelpers.stringToDate (param);
        } // if

        return date;                    // return the computed date
    } // getDateParam


    /**************************************************************************
     * Gets a parameter of type Time. <BR/>
     *
     * @param   name    Name of parameter to read.
     *
     * @return  The time if the parameter exists or
     *          <CODE>null</CODE> otherwise.
     */
    public final Date getTimeParam (String name)
    {
        String param = null;
        Date date = null;

        // get parameter:
        param = this.getParam (name);
        if (param != null && param.length () > 0) // parameter exists?
        {
            date = DateTimeHelpers.stringToTime (param);
        } // if

        return date;                    // return the computed time
    } // getTimeParam


    /**************************************************************************
     * Gets a parameter of type Boolean. <BR/>
     * To check if the parameter is <CODE>true</CODE> you have to check whether
     * the return value is equal to 1.
     * The parameter is <CODE>false</CODE> if and only if the return value is
     * equal to 0.<BR/>
     * examples:<BR/>
     * <CODE>
     * <PRE>
     * if ((num = env.getBoolParam (paramName)) >= IOConstants.BOOLPARAM_FALSE)
     *     booleanValue = (num == IOConstants.BOOLPARAM_TRUE);
     *
     * num = env.getBoolParam (paramName);
     * if (num == IOConstants.BOOLPARAM_TRUE) { &lt;true> }
     * else if (num == IOConstants.BOOLPARAM_FALSE) { &lt;false> }
     * else { &lt;not set> }
     * </PRE>
     * </CODE>
     *
     * @param   name    Name of parameter to read.
     *
     * @return  BOOLPARAM_FALSE     ... the parameter exists and is false.<BR/>
     *          BOOLPARAM_TRUE      ... the parameter exists and is true.<BR/>
     *          BOOLPARAM_NOTEXISTS ... the parameter does not exist.<BR/>
     *          BOOLPARAM_INVALID   ... the parameter has an invalid value.<BE/>
     */
    public final int getBoolParam (String name)
    {
        String param = null;
        int val = IOConstants.BOOLPARAM_NOTEXISTS;

        // get parameter:
        param = this.getParam (name);
        if (param != null)              // parameter exists?
        {
            if (param.equalsIgnoreCase (AEnvironment.PARAMVALUE_TRUE)) // true value?
            {
                val = IOConstants.BOOLPARAM_TRUE;      // set return value for true
            } // if
            else if (param.equalsIgnoreCase (AEnvironment.PARAMVALUE_FALSE)) // false value?
            {
                val = IOConstants.BOOLPARAM_FALSE;     // set return value for false
            } // else if
            else                                       // no valid value
            {
                val = IOConstants.BOOLPARAM_INVALID;   // set return value for invalid
            } // else
        } // if parameter exists
        else                            // parameter does not exist
        {
            // check if an additional parameter is set that indicates the negative
            // value of the parameter field:
            param = this.getParam (name + "_bool");
            if (param != null)          // additional parameter exists?
            {
                if (param.equalsIgnoreCase (AEnvironment.PARAMVALUE_TRUE)) // true value?
                {
                    val = IOConstants.BOOLPARAM_TRUE;      // set return value for true
                } // if
                else if (param.equalsIgnoreCase (AEnvironment.PARAMVALUE_FALSE)) // false value?
                {
                    val = IOConstants.BOOLPARAM_FALSE;     // set return value for false
                } // else if
                else                                       // no valid value
                {
                    val = IOConstants.BOOLPARAM_INVALID;   // set return value for invalid
                } // else
            } // if additional parameter exists
        } // else parameter does not exist

        return val;                     // return the computed value
    } // getBoolParam


    /**************************************************************************
     * Gets a parameter of type File. <BR/>
     * The parameter is gotten and the file is saved to the target directory.
     * <BR/>
     *
     * @param   name        Name of parameter to read.
     * @param   targetDir   Directory where to save the file.
     *
     * @return  The full path and name of the saved file and the file size as a
     *          string or <CODE>null</CODE> if the parameter was not found. <BR/>
     *          Syntax for return value:
     *          <CODE>&lt;path&gt;&lt;filename&gt; (&lt;size&gt; Bytes)</CODE>.
     *
     * @throws  UploadException
     *          Error during reading the uploaded data.
     */
    public final String getFileParam (String name, String targetDir)
        throws UploadException
    {
        // get the parameter, save the file and return the filename:
        return this.formData.getFileString (name, targetDir);
    } // getFileParam


    /**************************************************************************
     * Sets the value of a cookie. <BR/>
     *
     * @param   name    Name of the Cookie to set.
     * @param   value   Value to which the cookie shall be set.
     *
     * @return  <CODE>true</CODE> if the cookie was set successfully,
     *          <CODE>false</CODE> otherwise.
     */
    public final boolean setCookie (String name, String value)
    {
        return this.setCookie (name, value, null, null, null, false);
    } // setCookie


    /**************************************************************************
     * Sets the value of a cookie. <BR/>
     *
     * @param   name    Name of the Cookie to set.
     * @param   value   Value to which the cookie shall be set.
     * @param   expires Date/time when the cookie expires, i.e. is deleted
     *                  automatically.
     *
     * @return  <CODE>true</CODE> if the cookie was set successfully,
     *          <CODE>false</CODE> otherwise.
     */
    public final boolean setCookie (String name, String value, Date expires)
    {
        return this.setCookie (name, value, expires, null, null, false);
    } // setCookie


    /**************************************************************************
     * Sets the value of a cookie. <BR/>
     *
     * @param   name    Name of the Cookie to set.
     * @param   value   Value to which the cookie shall be set.
     * @param   expires Date/time when the cookie expires, i.e. is deleted
     *                  automatically.
     * @param   domain  Server domain, to which cookie should be readable
     *                  if null is given, then the value is not set in the cookie
     *                  which means the Cookie has automatically the calling
     *                  server domain set.
     * @param   path    Path of the domain, to which cookie should be readable
     *                  (example: /m2/) - if null is given, then the path is set
     *                  to /
     * @param   secure  If true, the Cookie is readable only to secure connections
     *                  (if all other conditions are met)
     *
     * @return  <CODE>true</CODE> if the cookie was set successfully,
     *          <CODE>false</CODE> otherwise.
     */
    public abstract boolean setCookie (String name, String value, Date expires, String domain, String path, boolean secure);


    /**************************************************************************
     * Gets the value of a Cookie of the given Name. <BR/>
     *
     * @param   name    Name of the Cookie to get.
     *
     * @return  The value of the cookie.
     */
    public abstract String getCookie (String name);


    /**************************************************************************
     * Redirects the response to the given url. <BR/>
     * Condition : nothing has been written to the response-Object before.
     *
     * @param   url     Url. where the Response will be redirected.
     *
     * @return  if redirect was successful.
     */
    public abstract boolean redirect (String url);


    /**************************************************************************
     * Abandon the current session. <BR/>
     */
    public abstract void abandon ();


    /**************************************************************************
     * Encodes a String into a URL. <BR/>
     *
     * @param   str     String to encode
     *
     * @return  The string url-encoded.
     */
    public abstract String urlEncode (String str);


    /**************************************************************************
     * Gets the sessionId and gives it back as a string. <BR/>
     *
     * @return  A string containing the sessionId.
     */
    public String getSessionId ()
    {
        return this.acxt.getSessionId ();
    } // getSessionId


    /**************************************************************************
     * Gets the path to the written file. <BR/>
     *
     * @param   key     The hash table key.
     *
     * @return  the path to the written file
     */
    public String getFilePath (String key)
    {
        return this.formData.getFilePath (key);
    } // getFilePath


    /**************************************************************************
     * Set the application info object. <BR/>
     *
     * @param   appInfo The application info object.
     */
    public void setApplicationInfo (ApplicationInfo appInfo)
    {
        this.p_appInfo = appInfo;
    } // setApplicationInfo


    /**************************************************************************
     * Get the application info object. <BR/>
     *
     * @return  The application info object, <CODE>null</CODE> if not object defined.
     */
    public ApplicationInfo getApplicationInfo ()
    {
        return this.p_appInfo;
    } // getApplicationInfo


    /**************************************************************************
     * Set the session info object. <BR/>
     *
     * @param   sessInfo    The session info object.
     */
    public void setSessionInfo (SessionInfo sessInfo)
    {
        this.p_sessInfo = sessInfo;
    } // setSessionInfo


    /**************************************************************************
     * Get the session info object. <BR/>
     *
     * @return  The session info object, <CODE>null</CODE> if not object defined.
     */
    public SessionInfo getSessionInfo ()
    {
        return this.p_sessInfo;
    } // getSessionInfo


    /**************************************************************************
     * Set the context which is defined within the session info object. <BR/>
     *
     * @param   sessInfo    The session info object.
     */
    public void setSessionContext (SessionInfo sessInfo)
    {
        this.setApplicationInfo (sessInfo.getApplicationInfo ());
        this.setSessionInfo (sessInfo);
        this.setUserInfo (sessInfo.userInfo);
    } // setSessionContext


    /**************************************************************************
     * Set the user info object. <BR/>
     * The user info object is set directly within the session info.
     *
     * @param   userInfo    The user info object.
     */
    public void setUserInfo (IUserInfo userInfo)
    {
        this.p_sessInfo.userInfo = userInfo;
    } // setUserInfo


    /**************************************************************************
     * Get the user info object. <BR/>
     * The user info object is retrieved directly from the session info.
     *
     * @return  The user info object, <CODE>null</CODE> if not object defined.
     */
    public IUserInfo getUserInfo ()
    {
        return this.p_sessInfo.userInfo;
    } // getUserInfo


    /**************************************************************************
     * Returns the login of the user making this request,
     * if the user has been authenticated, or null if the user has
     * not been authenticated. Whether the user name is sent with
     * each subsequent request depends on the browser and type
     * of authentication.<BR/>
     * Same as the value of the CGI variable REMOTE_USER.<BR/>
     *
     * @return  A String specifying the login of the user making this request,
     *          or <CODE>null</CODE>.
     */
    public abstract String getRemoteUser ();


    /**************************************************************************
     * Returns the name of the HTTP method with which this request was made,
     * for example, GET, POST, or PUT. Same as the value of the CGI variable
     * REQUEST_METHOD.<BR/>
     *
     * @return  A String specifying the name of the method with which this
     *          request was made.
     */
    public abstract String getMethod ();


    /**************************************************************************
     * Set the domains that are valid for an authentication.<BR/>
     *
     * @param   validDomains    The comma separated valid domains list.
     */
    public abstract void setValidDomains (String validDomains);


    /**************************************************************************
     * Sets the typcode for which the type instantiation should be allowed.
     * This flag indicates that certain  necessary calls on a type are allowed
     * even if e.g. the type translation is currently running.<BR/>
     * 
     * Since the flag is set within the environment the flag can be set
     * only for the current request of the user.
     *
     * @param   typcode    The type code to allow the type translation for.
     */
    public void allowInstantiation (String typeCode)
    {
        this.p_instantiationAllowedTypeCode = typeCode;
    } // allowTranslationAllowed

    
    /**************************************************************************
     * Returns if objects of the specific type may be instantiated.
     *
     * @param   typcode    The typecode to check the status for.
     * 
     * @return Returns if objects of the specific type may be instantiated.
     */
    public boolean isInstantiationAllowed (String typeCode)
    {
        return this.p_instantiationAllowedTypeCode != null && this.p_instantiationAllowedTypeCode == typeCode;
    } // isTypeTranslationAllowed

} // class AEnvironment
