/*
 * Interface Environment
 */

// package:
package ibs.io;

// imports:
//TODO: unsauber
import ibs.bo.OID;
import ibs.io.UploadException;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.IUserInfo;
import ibs.io.session.SessionInfo;

import java.util.Date;


/******************************************************************************
 * This is the Environment Interface, which defines all methods for
 * a specific Environment
 *
 * @version     $Id: Environment.java,v 1.21 2010/07/13 15:59:33 btatzmann Exp $
 *
 * @author      Christine Keim (CK), 990303
 ******************************************************************************
 */
public interface Environment
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Environment.java,v 1.21 2010/07/13 15:59:33 btatzmann Exp $";


    /**************************************************************************
     * Set the application info object. <BR/>
     *
     * @param   appInfo The application info object.
     */
    public void setApplicationInfo (ApplicationInfo appInfo);


    /**************************************************************************
     * Get the application info object. <BR/>
     *
     * @return  The application info object, <CODE>null</CODE> if not object defined.
     */
    public ApplicationInfo getApplicationInfo ();


    /**************************************************************************
     * Set the session info object. <BR/>
     *
     * @param   sessInfo    The session info object.
     */
    public void setSessionInfo (SessionInfo sessInfo);


    /**************************************************************************
     * Get the session info object. <BR/>
     *
     * @return  The session info object, <CODE>null</CODE> if not object defined.
     */
    public SessionInfo getSessionInfo ();


    /**************************************************************************
     * Set the user info object. <BR/>
     *
     * @param   userInfo    The user info object.
     */
    public void setUserInfo (IUserInfo userInfo);


    /**************************************************************************
     * Get the user info object. <BR/>
     *
     * @return  The user info object, <CODE>null</CODE> if not object defined.
     */
    public IUserInfo getUserInfo ();


    /**************************************************************************
     * Set the context which is defined within the session info object. <BR/>
     *
     * @param   sessInfo    The session info object.
     */
    public void setSessionContext (SessionInfo sessInfo);


    /**************************************************************************
     * Drop all form data. <BR/>
     * Within this method all form data contents are thrown away. After this it
     * is not longer possible to access any of the form data values. <BR/>
     * The property <A HREF="#formData">formData</A> is initialized to be empty.
     */
    public void dropFormData ();


    /**************************************************************************
     * Clears the Context (to free the memory). <BR/>
     */
    public void destroy ();


    /**************************************************************************
     * Gets the browser and returns a String with the identified browser. <BR/>
     *
     * @return  String with browser type. (see IOConstants for possible values)
     */
    public String getBrowser ();


    /**************************************************************************
     * Gets a server variable, whose name must be given. <BR/>
     *
     * @param   name    Name of the server variable (see class IOConstants).
     *
     * @return  Value of the server variable.
     */
    // Get the value of a property
    public String getServerVariable (String name);


    /**************************************************************************
     * Gets a HTTP-header variable, whose name must be given. <BR/>
     *
     * @param   name    Name of the header variable (see class IOConstants).
     *
     * @return  Value of the header variable.
     */
    // Get the value of a property
    public String getHttpHeaderVariable (String name);


    /**************************************************************************
     * Gets the name of the request server.
     *
     * @return  Name of the server, <CODE>null</CODE> if not found.
     */
    public String getServerName ();


    /**************************************************************************
     * Get the path of the actual request, i.e. the everything until the last
     * '/'.
     *
     * @param   ssl     A boolean value which indicates the actual context
     *                  (secure mode or insecure one)
     * @param   sess    The actual session object.
     *
     * @return  The url path.
     */
    public String getPath (boolean ssl, SessionInfo sess);


    /**************************************************************************
     * Sets the value of an application variable (given name) to the given
     * object. <BR/>
     *
     * @param   name    Name of the application variable to set.
     * @param   value   Object to set the application variable to.
     */
    public void setApplicationObject (String name, Object value);


    /**************************************************************************
     * Gets the value of the Applicationvariable (given name). <BR/>
     *
     * @param   name    Name of the application variable to set.
     *
     * @return  Value of the application variable.
     */
    public String getApplicationValue (String name);


    /**************************************************************************
     * Gets the value of an integer application variable (given name). <BR/>
     *
     * @param   name    Name of the application variable to get.
     *
     * @return  Value of the application variable.
     */
    public int getIntApplicationValue (String name);


    /**************************************************************************
     * Gets the object in an application variable (given name). <BR/>
     *
     * @param   name    Name of the application object to get.
     *
     * @return  The object stored in the application variable.
     */
    public Object getApplicationObject (String name);


    /**************************************************************************
     * Set the content type of the response. <BR/>
     * This is the MIME type of the response as it is sent to the client. <BR/>
     * ATTENTION! This method should be called before calling write.
     *
     * @param   strContentType  String representation of the content type.
     */
    public void setContentType (String strContentType);


    /**************************************************************************
     * Adds an name value tuple to the http-header. <BR/>
     * ATTENTION!! If the value name is 'Content-disposition' the prefix
     * 'filename=' is automaticaly added to the value.
     *
     * @param   name            Name of the variable to add to the header.
     * @param   value           Value of the variable to add to the header.
     */
    public void addHeaderEntry (String name, String value);


    /**************************************************************************
     * Sets the value of the session variable (given name) to the given object.
     * <BR/>
     *
     * @param   name    Name of the session variable to set.
     * @param   value   Object to set the session variable to.
     */
    public void setSessionObject (String name, Object value);


    /**************************************************************************
     * Gets the value in a session variable (given name). <BR/>
     *
     * @param   name    Name of the session variable to get.
     *
     * @return  Value stored in the session variable or
     *          <CODE>null</CODE> if the session variable was not found.
     */
    public String getSessionValue (String name);


    /**************************************************************************
     * Gets the object in a session variable (given name). <BR/>
     *
     * @param   name    Name of the session variable to get.
     *
     * @return  Object stored in the session variable or
     *          <CODE>null</CODE> if the session variable was not found.
     */
    public Object getSessionObject (String name);


    /**************************************************************************
     * Gets the value of an integer session variable (given name). <BR/>
     *
     * @param   name    Name of the session variable to get.
     *
     * @return  Value of the session variable as integer or
     *          <CODE>0</CODE> if the session variable was not found.
     */
    public int getIntSessionValue (String name);


    /**************************************************************************
     * Writes a String to the response object. <BR/>
     *
     * @param   text    String to write (max. 255 characters).
     */
    public void write (String text);

    
    /**************************************************************************
     * Writes the given data byte[] to the response.
     * Additionally a byte order mark can be provided.<BR/>
     *
     * @param   data            Data to write.
     * @param   byteOrderMark   The byte order mark to write to the response.
     *                          Needed in case of writing files.
     */
    public void write (byte[] data, byte[] byteOrderMark);


    /**************************************************************************
     * Disables write functionality: env.write will be lost. <BR/>
     */
    public void disableWrite ();


    /**************************************************************************
     * Enable write functionality.. <BR/>
     */
    public void enableWrite ();


    /**************************************************************************
     * Checks if a session variable exists. <BR/>
     *
     * @param   name    Name of the session variable.
     *
     * @return <CODE>true</CODE> = exists; <CODE>false</CODE> = does not exist.
     */
    public boolean existsSessionVariable (String name);


    /**************************************************************************
     * Checks if an application variable exists. <BR/>
     *
     * @param   name    Name of the application variable.
     *
     * @return <CODE>true</CODE> = exists; <CODE>false</CODE> = does not exist.
     */
    public boolean existsApplicationVariable (String name);


    /**************************************************************************
     * Gets the base URL.
     *
     * @return  The base URL.
     */
    public String getBaseURL ();


    /**************************************************************************
     * Returns the correct string. For servlets returns the forServlet String
     * for other types (ASP for example) returns the forOther string.
     *
     * @param   forServlets String which is used for servlets.
     * @param   forOther    String which is used for other application kinds.
     *
     * @return  The base URL.
     */
    public String getBaseURL (String forServlets, String forOther);


    /**************************************************************************
     * Gets the value of a URL parameter (GET method). <BR/>
     *
     * @param   name    Name of the parameter to get.
     *
     * @return <CODE>true</CODE> = exists; <CODE>false</CODE> = does not exist
     */
    public String getURLParam (String name);


    /**************************************************************************
     * Gets the values of a multiple URL parameter (GET method). <BR/>
     *
     * @param   name    Name of the parameter to get.
     *
     * @return  The form element splitted into an array of strings.
     */
    public String[] getMultipleURLParam (String name);


    /**************************************************************************
     * Gets the value of a form parameter (POST method). <BR/>
     *
     * @param   name    Name of the parameter to get.
     *
     * @return  The value of the parameter or <CODE>null</CODE> if the parameter
     *          does not exist.
     */
    public String getFormParam (String name);


    /**************************************************************************
     * Gets the values of a multiple form parameter (POST method). <BR/>
     *
     * @param   name    Name of the parameter to get.
     *
     * @return  The form element splitted into an array of strings.
     */
    public String[] getMultipleFormParam (String name);


    /**************************************************************************
     * Gets the value of a parameter indifferent of the method. <BR/>
     *
     * @param   name    Name of the parameter to get.
     *
     * @return  The parameter value or <CODE>null</CODE> if it does not exist.
     */
    public String getParam (String name);


    /**************************************************************************
     * Gets the value of a multiple parameter indifferent of the method. <BR/>
     *
     * @param   name    Name of the parameter to get.
     *
     * @return  Array of Strings of values.
     */
    public String[] getMultipleParam (String name);


    /**************************************************************************
     * Gets a parameter of type OID. <BR/>
     *
     * @param   name    Name of parameter to read.
     *
     * @return  The oid if the parameter exists or <CODE>null</CODE> otherwise.
     */
    public OID getOidParam (String name);


    /**************************************************************************
     * Gets a parameter of type integer. <BR/>
     *
     * @param   name    Name of parameter to read.
     *
     * @return  The integer value if the parameter exists or
     *          <CODE>-1</CODE> otherwise.
     */
    public int getIntParam (String name);


    /**************************************************************************
     * Gets a parameter of type float. <BR/>
     *
     * @param   name    Name of parameter to read.
     *
     * @return  The integer value if the parameter exists or
     *          <CODE>-1</CODE> otherwise.
     */
    public float getFloatParam (String name);


    /**************************************************************************
     * Gets a parameter of type float. <BR/>
     *
     * @param   name    Name of parameter to read.
     *
     * @return  The integer value if the parameter exists or
     *          <CODE>-1</CODE> otherwise.
     */
    public double getDoubleParam (String name);


    /**************************************************************************
     * Gets a parameter of type string. <BR/>
     *
     * @param   name    Name of parameter to read.
     *
     * @return  The string value if the parameter exists or
     *          <CODE>null</CODE> otherwise.
     */
    public String getStringParam (String name);


    /**************************************************************************
     * Gets a parameter of type DateTime. <BR/>
     *
     * @param   name    Name of parameter to read.
     *
     * @return  The date/time if the parameter exists or
     *          <CODE>null</CODE> otherwise.
     */
    public Date getDateTimeParam (String name);


    /**************************************************************************
     * Gets a parameter of type Date. <BR/>
     *
     * @param   name    Name of parameter to read.
     *
     * @return  The date if the parameter exists or
     *          <CODE>null</CODE> otherwise.
     */
    public Date getDateParam (String name);


    /**************************************************************************
     * Gets a parameter of type Time. <BR/>
     *
     * @param   name    Name of parameter to read.
     *
     * @return  The time if the parameter exists or
     *          <CODE>null</CODE> otherwise.
     */
    public Date getTimeParam (String name);


    /**************************************************************************
     * Gets a parameter of type Boolean. <BR/>
     * To check if the parameter is <CODE>true</CODE> you have to check whether
     * the return value is equal to 1.
     * The parameter is <CODE>false</CODE> if and only if the return value is
     * equal to 0.<BR/>
     * examples:<BR/>
     * <CODE>
     * <PRE>
     * if ((num = env.getBoolParam (paramName)) >= 0)
     *     booleanValue = (num == 1);
     *
     * num = env.getBoolParam (paramName);
     * if (num == 1) { &lt;true> }
     * else if (num == 0) { &lt;false> }
     * else { &lt;not set> }
     * </PRE>
     * </CODE>
     *
     * @param   name    Name of parameter to read.
     *
     * @return  0 ... the parameter exists and is false.<BR/>
     *          1 ... the parameter exists and is true.<BR/>
     *          -1 .. the parameter does not exist.<BR/>
     *          -2 .. the parameter has an invalid value.
     */
    public int getBoolParam (String name);


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
    public String getFileParam (String name, String targetDir)
        throws UploadException;


    /**************************************************************************
     * Sets the value of a cookie. <BR/>
     *
     * @param   name    Name of the Cookie to set.
     * @param   value   Value to which the cookie shall be set.
     *
     * @return  <CODE>true</CODE> if the cookie was set successfully,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean setCookie (String name, String value);


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
    public boolean setCookie (String name, String value, Date expires);


    /**************************************************************************
     * Gets the value of a Cookie of the given Name. <BR/>
     *
     * @param   name    Name of the Cookie to get.
     *
     * @return  The value of the cookie.
     */
    public String getCookie (String name);


    /**************************************************************************
     * Redirects the response to the given url. <BR/>
     * Condition : nothing has been written to the response-Object before.
     *
     * @param   url     Url. where the Response will be redirected.
     *
     * @return  if redirect was successful.
     */
    public boolean redirect (String url);


    /**************************************************************************
     * Abandon the current session. <BR/>
     */
    public void abandon ();


    /**************************************************************************
     * Encodes a Struing into a URL. <BR/>
     *
     * @param   str     String to encode
     *
     * @return  The string urlencoded
     */
    public String urlEncode (String str);


    /**************************************************************************
     * Gets the sessionId and gives it back as a string. <BR/>
     *
     * @return  A string containing the sessionId.
     */
    public String getSessionId ();


    /**************************************************************************
     * Gets the path to the written file. <BR/>
     *
     * @param   key     The hashtable key.
     *
     * @return  the path to the written file
     */
    public String getFilePath (String key);


    /**************************************************************************
     * Returns the login of the user making this request,
     * if the user has been authenticated, or null if the user has
     * not been authenticated. Whether the user name is sent with
     * each subsequent request depends on the browser and type
     * of authentication. <BR/>
     * Same as the value of the CGI variable REMOTE_USER.<BR/>
     *
     * @return  A String specifying the login of the user making this request,
     *          or <CODE>null</CODE>.
     */
    public String getRemoteUser ();


    /**************************************************************************
     * Returns the name of the HTTP method with which this request was made,
     * for example, GET, POST, or PUT. Same as the value of the CGI variable
     * REQUEST_METHOD.<BR/>
     *
     * @return  A String specifying the name of the method with which this
     *          request was made.
     */
    public String getMethod ();


    /**************************************************************************
     * Set the domains that are valid for an authentication.<BR/>
     *
     * @param   validDomains    The comma separated valid domains list.
     */
    public void setValidDomains (String validDomains);


    /**************************************************************************
     * Sets the typcode for which the type instantiation should be allowed.
     * This flag indicates that certain  necessary calls on a type are allowed
     * even if e.g. the type translation is currently running.<BR/>
     *
     * Since the flag is set within the environment the flag can be set
     * only for the current request of the user.
     *
     * @param   typeCode    The typecode to allow the type translation for.
     */
    public void allowInstantiation (String typeCode);


    /**************************************************************************
     * Returns if objects of the specific type may be instantiated.
     *
     * @param   typeCode    The typecode to check the status for.
     *
     * @return Returns if objects of the specific type may be instantiated.
     */
    public boolean isInstantiationAllowed (String typeCode);

} // interface Environment
