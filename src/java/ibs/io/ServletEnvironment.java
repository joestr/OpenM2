/*
 * Class ServletEnvironment
 */

// package:
package ibs.io;

// imports:
import ibs.tech.http.FormDataDictionary;
import ibs.tech.http.FormDataElement;
import ibs.tech.http.HttpConstants;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/******************************************************************************
 * This is the ServletEnvironment Object, which simplifies the interaction with
 * the Servlet-Environment in java. It enables to get SessionVariables and
 * Formparameters.
 *
 * @version     $Id: ServletEnvironment.java,v 1.22 2011/07/25 13:04:43 btatzmann Exp $
 *
 * @author      Christine Keim (CK), 990304
 ******************************************************************************
 */
public class ServletEnvironment extends AEnvironment
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ServletEnvironment.java,v 1.22 2011/07/25 13:04:43 btatzmann Exp $";


    /**
     * Flag to indicate that the NTLM authentication is running. <BR/>
     * This will be used by the ApplicationServlet in order to
     * complete the authorization before any other actions can take place. <BR/>
     */
    public boolean p_isNTLMAuthenticationInProgress = false;

    /**
     * Remembers the Context of the Application (Objects of Servlet). <BR/>
     */
    protected ServletContext cxt = null;

    /**
     * The "domain\\username" data hat has been negotiated via NTLM. <BR/>
     */
    protected String remoteUser = null;


    /**************************************************************************
     * Creates a new instance of the ServletEnvironment
     */
    public ServletEnvironment ()
    {
        // empty constructor because nothing to do
    } // ServletEnvironment


    /**************************************************************************
     * Initializes the ServletEnvironment  and creates a new Context Object. <BR/>
     * The two given Object hold all the necessary informations.
     *
     * @param   req    the request-object
     * @param   res    the response-object
     *
     * @throws  IOException
     *          Exception when accessing data.
     * @throws  UploadException
     *          Exception during uploading files.
     */
    public void set (HttpServletRequest req, HttpServletResponse res)
        throws IOException, UploadException
    {
        this.cxt = new ServletContext (req, res);
        this.acxt = this.cxt;

/*
        // check if an authorization request has been sent
        // Note that in case the client send an authorization request
        // this must be the NTLM base64-encoded type-1-message
        // or the this must be the NTLM base64-encoded type-3-message
        // the type-3-message holds the username and all form data!
        if (this.cxt.request.getHeader ("Authorization") != null)
        {
            // negotiate NTLM authentication
            // in case we got an username we can perform
            // standard environment initialization
            if (negotiateNTLM () != null)
            {
                // authentication completed
                this.isNTLMAuthenticationInProgress = false;
                // initialize the environment
                initEnvironment ();
            } // if (username != null)
            else
                this.isNTLMAuthenticationInProgress = true;
            // else we just have received base64-encoded type-1-message
            // and have to wait for the base64-encoded type-3-message
        } // if (auth != null)
        else    // standard initialize the environment
            initEnvironment ();
*/

        // initialize the environment
        this.initEnvironment ();
    } // set


    /**************************************************************************
     * Parse the context for www-data. <BR/>
     *
     * @return  A vector containing the several data parts of the stream.
     */
    protected final FormDataDictionary parseWWWForm ()
    {
        FormDataDictionary result;      // the resulting dictionary consisting
        // of the several parts of the environment-variables

        result = new FormDataDictionary ();

        @SuppressWarnings ("unchecked") // suppress compiler warning
        Enumeration<String> e = this.cxt.request.getParameterNames ();
        while (e.hasMoreElements ())
        {
            String name = e.nextElement ();
            String[] vals = this.cxt.request.getParameterValues (name);
            if (vals != null)
            {
                FormDataElement temp = new FormDataElement ();
                temp.name = name;
                for (int i = 0; i < vals.length; i++)
                {
                    temp.put (vals[i]);
                } // for i
                result.putItem (temp);
            } // if
        } // while
        /*
        String[] dummy = result.getMultipleString ("di");
        write (IE302.TAG_NEWLINE + dummy.length);
        for (int i = 0; i < dummy.length; i++)
        write (IE302.TAG_NEWLINE + "di -->" + dummy[i]);

        dummy = result.getMultipleString ("mi");
        for (int i = 0; i < dummy.length; i++)
        write (IE302.TAG_NEWLINE + "mi -->" + dummy[i]);
        */

        return result;                  // return the result vector
    } // parseWWWForm


    /**************************************************************************
     * Parse the context for www-data. <BR/>
     *
     * @return  A vector containing the several data parts of the stream.
     */
    protected final FormDataDictionary parseWWWGet ()
    {
        return this.parseWWWForm ();    // return the result vector
    } // parseWWWGet


    ///////////////////////////////////////////////////////////////////////////
    // protected methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Clears the Context (to free the memory). <BR/>
     */
    public void destroy ()
    {
        this.cxt.destroy ();
        this.cxt = null;
    } // destroy


    /**************************************************************************
    * Gets a server variable, whose name must be given. <BR/>
    *
    * @param   name    Name of the server variable (see class IOConstants).
    *
    * @return  Value of the server variable.
    */
    // Get the value of a property
    public String getServerVariable (String name)
    {
        if (name.equalsIgnoreCase (IOConstants.SV_AUTH_TYPE))
        {
            return this.cxt.request.getAuthType ();
        } // if
        else if (name.equalsIgnoreCase (IOConstants.SV_CONTENT_LENGTH))
        {
            return "" + this.cxt.request.getContentLength ();
        } // else if
        else if (name.equalsIgnoreCase (IOConstants.SV_CONTENT_TYPE))
        {
            return this.cxt.request.getContentType ();
        } // else if
        else if (name.equalsIgnoreCase (IOConstants.SV_USER_AGENT))
        {
            return this.cxt.request.getHeader ("User-Agent");
        } // else if
        else if (name.equalsIgnoreCase (IOConstants.SV_PATH_INFO))
        {
            return this.cxt.request.getPathInfo ();
        } // else if
        else if (name.equalsIgnoreCase (IOConstants.SV_PATH_TRANSLATED))
        {
            return this.cxt.request.getPathTranslated ();
        } // else if
        else if (name.equalsIgnoreCase (IOConstants.SV_QUERY_STRING))
        {
            return this.cxt.request.getQueryString ();
        } // else if
        else if (name.equalsIgnoreCase (IOConstants.SV_REMOTE_ADDR))
        {
            return this.cxt.request.getRemoteAddr ();
        } // else if
        else if (name.equalsIgnoreCase (IOConstants.SV_LOGON_USER))
        {
            return this.cxt.request.getRemoteUser ();
        } // else if
        else if (name.equalsIgnoreCase (IOConstants.SV_REMOTE_HOST))
        {
            return this.cxt.request.getRemoteHost ();
        } // else if
        else if (name.equalsIgnoreCase (IOConstants.SV_REQUEST_METHOD))
        {
            return this.cxt.request.getMethod ();
        } // else if
        else if (name.equalsIgnoreCase (IOConstants.SV_SERVER_NAME))
        {
            return this.cxt.request.getServerName ();
        } // else if
        else if (name.equalsIgnoreCase (IOConstants.SV_SERVER_PORT))
        {
            return "" + this.cxt.request.getServerPort ();
        } // else if
        else if (name.equalsIgnoreCase (IOConstants.SV_SERVER_PROTOCOL))
        {
            return this.cxt.request.getProtocol ();
        } // else if
        else if (name.equalsIgnoreCase (IOConstants.SV_URL))
        {
            return this.cxt.request.getRequestURI ();
        } // else if
        else if (name.equalsIgnoreCase (IOConstants.SV_HTTPS))
        {
            return this.cxt.request.getScheme ();
        //        else if (name.equalsIgnoreCase (IOConstants.SV_GATEWAY_INTERFACE))
        //        else if (name.equalsIgnoreCase (IOConstants.SV_REQUEST_LINE))
        //        else if (name.equalsIgnoreCase (IOConstants.SV_SCRIPT_NAME))
        //        else if (name.equalsIgnoreCase (IOConstants.SV_SCRIPT_MAP))
        //        else if (name.equalsIgnoreCase (IOConstants.SV_SERVER_PORT_SECURE))
        //        else if (name.equalsIgnoreCase (IOConstants.SV_SERVER_SOFTWARE))
        } // else if
        else
        {
            return null;
        } // else
    } // getServerVariable


    /**************************************************************************
     * Gets a server variable, whose name must be given. <BR/>
     *
     * @param   name    Name of the server variable (see class IOConstants).
     *
     * @return  Value of the server variable.
     */
    // Get the value of a property
    public String getHttpHeaderVariable (String name)
    {
        return this.cxt.getHttpHeaderVariable (name);
    } // getHttpHeaderVariable


    /**************************************************************************
     * returns the right string. For servlets returns the forServlet String
     * for other types (ASP for example) returns the forOther string.
     *
     * @param   forServlets String which is used for servlets.
     * @param   forOther    String which is used for other application kinds.
     *
     * @return  The base URL.
     */
    public String getBaseURL (String forServlets, String forOther)
    {
        return forServlets;
    } // getBaseURL


    /**************************************************************************
     * Gets the name of the request server.
     *
     * @return  Name of the server, <CODE>null</CODE> if not found.
     */
    public String getServerName ()
    {
        return this.cxt.request.getServerName ();
    } // getServerName


    /**************************************************************************
     * Sets the value of an application variable (given name) to the given
     * object. <BR/>
     * not available for now ... only with 2.1 API
     *
     * @param   name    Name of the application variable to set.
     * @param   value   Object to set the application variable to.
     */
    public void setApplicationObject (String name, Object value)
    {
        //        cxt.application.putValue (name, new Variant (value));
    } // setApplicationValue


    /**************************************************************************
     * Gets the value of the Applicationvariable (given name). <BR/>
     * not available for now ... only with 2.1 API
     *
     * @param   name    Name of the application variable to set.
     *
     * @return  Value of the application variable.
     */
    public String getApplicationValue (String name)
    {
        return null;
    } // getApplicationValue


    /**************************************************************************
     * Gets the object in an application variable (given name). <BR/>
     * not available for now ... only with 2.1 API
     *
     * @param   name    Name of the application object to get.
     *
     * @return  The object stored in the application variable.
     */
    public Object getApplicationObject (String name)
    {
        return null;
    } // getApplicationObject


    /**************************************************************************
     * Adds an name value tuple to the http-header. <BR/>
     * ATTENTION!! If the value name is 'Content-disposition' the prefix
     * 'filename=' is automaticaly added to the value.
     *
     * @param   name            Name of the variable to add to the header.
     * @param   value           Value of the variable to add to the header.
     */
    public void addHeaderEntry (String name, String value)
    {
        if (name.equals (HttpConstants.HTTP_HEADER_FILENAME))
                                        // set file name?
        {
            String userAgent = this.getServerVariable ("HTTP_USER_AGENT");
            if (userAgent != null && userAgent.indexOf ("MSIE 5.5") >= 0)
            {
                // MSIE 5.5 is not conform to RFC 1806:
                this.cxt.response.setHeader (name, "filename=" + value);
            } // if
            else
            {
                // all other browser types:
                this.cxt.response.setHeader (name, "attachment; filename=" + value);
            } // if
        } // if set file name
        else
        {
            this.cxt.response.setHeader (name, value);
        } // else
    } // addHeaderEntry


    /**************************************************************************
     * Sets the value of the session variable (given name) to the given object.
     * <BR/>
     *
     * @param   name    Name of the session variable to set.
     * @param   value   Object to set the session variable to.
     */
    public void setSessionObject (String name, Object value)
    {
        this.cxt.session.setAttribute (name, value);
    } // setSessionObject


    /**************************************************************************
     * Gets the value in a session variable (given name). <BR/>
     *
     * @param   name    Name of the session variable to get.
     *
     * @return  Value stored in the session variable or
     *          <CODE>null</CODE> if the session variable was not found.
     */
    public String getSessionValue (String name)
    {
        Object temp = this.cxt.session.getAttribute (name);
        if (temp != null)
        {
            return temp.toString ();
        } // if

        return null;
    } // getSessionValue


    /**************************************************************************
     * Gets the object in a session variable (given name). <BR/>
     *
     * @param   name    Name of the session variable to get.
     *
     * @return  Object stored in the session variable or
     *          <CODE>null</CODE> if the session variable was not found.
     */
    public Object getSessionObject (String name)
    {
        return this.cxt.session.getAttribute (name);
    } // getSessionObject


    /**************************************************************************
     * Writes a String to the response object. <BR/>
     *
     * @param   text    String to write (max. 255 characters).
     */
    public void write (String text)
    {
        // check if the write is enabled:
        if (this.p_writeEnabled)        // write enabled?
        {
            this.cxt.write (text);
        } // if write enabled
    } // write
    
    
    /**************************************************************************
     * Writes the given data byte[] to the response.
     * Additionally a byte order mark can be provided.<BR/>
     *
     * @param   data            Data to write.
     * @param   byteOrderMark   The byte order mark to write to the response.
     *                          Needed in case of writing files.
     */
    public void write (byte[] data, byte[] byteOrderMark)
    {
        // check if the write is enabled:
        if (this.p_writeEnabled)        // write enabled?
        {
            if (data != null)           // there is a text to write?
            {
                this.cxt.write (data, byteOrderMark);
            } // if there is a text to write
        } // if write enabled
    } // write


    /**************************************************************************
     * Gets the value of a multiple parameter indifferent of the method. <BR/>
     *
     * @param   name    Name of the parameter to get.
     *
     * @return  Array of Strings of values.
     */
    public String[] getMultipleParam (String name)
    {

        if (this.type == IOConstants.CT_WWWGET)
        {
            return this.cxt.request.getParameterValues (name);
        } // if

        return this.getMultipleFormParam (name);
    } // getMultipleParam


    /**************************************************************************
     * Sets the value of a cookie. <BR/>
     *
     * @param   name    Name of the Cookie to set.
     * @param   value   Value to which the cookie shall be set.
     * @param   expires Date/time when the cookie expires, i.e. is deleted
     *                  automatically.
     * @param   domain  Serverdomain, to which cookie should be readable
     *                  if null is given, then the value is not set in the cookie
     *                  which means the Cookie has automatically the calling
     *                  serverdomain set.
     * @param   path    Path of the domain, to which cookie should be readable
     *                  (example: /m2/) - if null is given, then the path is set
     *                  to /
     * @param   secure  If true, the Cookie is readable only to secure connections
     *                  (if all other conditions are met)
     *
     * @return  <CODE>true</CODE> if everything was o.k.,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean setCookie (String name, String value, Date expires, String domain, String path, boolean secure)
    {
        Date actDate = new Date ();
        Cookie cookie = new Cookie (name,  value);
        if (expires != null)
        {
            int age = 0;
            // the expiring date must be an integer value representing the SECONDS the cookie is valid
            // a negative value means that the cookie is not persistent and expires when the browser exits.
            age = (int) (expires.getTime () - actDate.getTime ()) / 1000;
            cookie.setMaxAge (age);
        } // if

        if (domain != null)
        {
            cookie.setDomain (domain);
        } // if
        if (path != null)
        {
            cookie.setPath (path);
        } // if
        cookie.setSecure (secure);

        try
        {
            this.cxt.response.addCookie (cookie);
            /*
            cookie.setPath ("www.linux.tectum.at");
            //            write ("domain " + cookie.getDomain ());
            write (IE302.TAG_NEWLINE + "path " + getPath ());
            cookie.setPath ("www.linux.tectum.at");
            write (IE302.TAG_NEWLINE + "path " + getPath ());
            */
            return true;
        } // try
        catch (Exception e)
        {
            return false;
        } // catch
    } // setCookie


    /**************************************************************************
     * Gets the value of a Cookie of the given Name. <BR/>
     *
     * @param   name    Name of the Cookie to get.
     *
     * @return  The value of the cookie.
     *          <CODE>null</CODE> if the cookie was not found.
     */
    public String getCookie (String name)
    {
        Cookie[] cookies = this.cxt.request.getCookies ();

        // check if we got any cookies:
        if (cookies != null)            // got cookies?
        {
            // loop over all set cookies:
            for (int i = 0; i < cookies.length; i++)
            {
                // check if the actual cookies is the requested one:
                if ((cookies[i] != null) &&
                    cookies[i].getName ().equalsIgnoreCase (name))
                {
                    return cookies[i].getValue ();
                } // if
            } // for i
        } // if got cookies

        // cookie not found - return defailt value:
        return null;
    } // getCookie


    /**************************************************************************
     * Redirects the response to the given url. <BR/>
     * Condition : nothing has been written to the response object before.
     *
     * @param   url     Url. where the Response will be redirected.
     *
     * @return  if redirect was successful.
     */
    public boolean redirect (String url)
    {
        try
        {
            this.cxt.response.sendRedirect (url);
            return true;                // redirect was successful
        } // try
        catch (IOException e)
        {
            return false;               // redirect was not successful
        } // catch
    } // redirect


    /**************************************************************************
     * Abandon the current session. <BR/>
     */
    public void abandon ()
    {
        try
        {
            this.cxt.session.invalidate ();
        } // try
        catch (IllegalStateException e)
        {
            // already abandoned
        } // catch
    } // abandon


    /**************************************************************************
     * Encodes a String into a URL. <BR/>
     *
     * @param   str     String to encode
     *
     * @return  The string urlencoded
     */
    public String urlEncode (String str)
    {
        return IOHelpers.urlEncode (str);
    } // urlEncode


    /**************************************************************************
     * Encodes a String into a URL. <BR/>
     *
     * @param   str     String to encode
     *
     * @return  The string urlencoded
     */
    public String urlDecode (String str)
    {
        return IOHelpers.urlDecode (str);
    } // urlDecode


    /**************************************************************************
     * Set the content type of the response. <BR/>
     * This is the MIME type of the response as it is sent to the client. <BR/>
     * ATTENTION! This method should be called before calling write.
     *
     * @param   strContentType  String representation of the content type.
     */
    public void setContentType (String strContentType)
    {
        // call the corresponding method of the response object:
        this.cxt.response.setContentType (strContentType);
    } // setContentType


    /**************************************************************************
     * Returns the login of the user making this request,
     * if the user has been authenticated, or null if the user has
     * not been authenticated. Whether the user name is sent with
     * each subsequent request depends on the browser and type
     * of authentication. <BR/>
     * Same as the value of the CGI variable REMOTE_USER. <BR/>
     * Note that the remoteUser will have the format: domain/username. <BR/>
     *
     * @return  A String specifying the login of the user making this request,
     *          or <CODE>null</CODE>.
     */
    public String getRemoteUser ()
    {
        /*
        String domain = null;
        String username = null;
        int pos;
        */
        
        // Retrieve the remote user from the request
        String remoteUser = this.cxt.request.getRemoteUser ();

        // did we get a valid remoteUser?
        if (remoteUser != null)
        {
            //IBS-675 openM2 Single Sign On v2 - Implementation
            //Domain check is not necessary anymore since Kerberos REALM and Domain
            //are specified within Kerberos configuration file.
            return remoteUser;
            
            /*
            // remoteUser should have the format: domain\\username
            pos = remoteUser.indexOf ("\\");
            // did we find the \\?
            if (pos >= 0)
            {
                domain = remoteUser.substring (0, pos).toUpperCase ();
                username =  remoteUser.substring (pos + 1);
            } // if (pos >= 0)
            // note that in case no domain separator has been found
            // we invalidate the remoteUser by returning null
            if (domain != null)
            {
                // check the domain
                if (this.checkDomain (domain))
                {
                    // return the username to indicate that the remote username
                    // is valid
                    return username;
                } // if (checkDomain (domain))
            } // if (domain != null)
            */
        } // if (remoteUser != null)
        // return null to indicate that no valid user has been found
        return null;
    } // getRemoteUser


    /**************************************************************************
     * Check if a domain is valid. <BR/>
     * This is done by checking the list of domains set in the system
     * configuration. <BR/>
     *
     * @param   domain  The domain to check.
     *
     * @return  <CODE>true</CODE> if the domain is valid,
     *          <CODE>false</CODE> otherwise.
     */
    private boolean checkDomain (String domain)
    {
        // the domain must be in the list of valid domains set in the
        // servlet environment
        return this.p_validDomains.indexOf ("," + domain + ",") >= 0;
    } // checkDomain


    /**************************************************************************
     * Returns the name of the HTTP method with which this request was made,
     * for example, GET, POST, or PUT. Same as the value of the CGI variable
     * REQUEST_METHOD. <BR/>
     *
     * @return  A String specifying the name of the method with which this
     *          request was made.
     */
    public String getMethod ()
    {
        return this.cxt.request.getMethod ();
    } // getMethod


    /**************************************************************************
     * Set the domains that are valid for an authentication. <BR/>
     *
     * @param   validDomains    The comma separated valid domains list.
     */
    public void setValidDomains (String validDomains)
    {
        this.p_validDomains = validDomains.toUpperCase ();
    } // setValidDomains

} // class ServletEnvironment
