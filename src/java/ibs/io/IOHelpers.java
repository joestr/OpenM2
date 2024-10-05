/*
 * Class: IOHelpers.java
 */

// package:
package ibs.io;

// imports:
//KR TODO: unsauber
import ibs.app.AppConstants;
import ibs.app.AppMessages;
import ibs.app.CssConstants;
import ibs.app.HistoryInfo;
import ibs.app.UserInfo;
import ibs.bo.BOArguments;
import ibs.bo.BOPathConstants;
import ibs.bo.BOTokens;
import ibs.bo.OID;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.service.conf.IConfiguration;
import ibs.service.conf.ServerRecord;
import ibs.tech.html.BlankElement;
import ibs.tech.html.BuildException;
import ibs.tech.html.Element;
import ibs.tech.html.Font;
import ibs.tech.html.GroupElement;
import ibs.tech.html.IE302;
import ibs.tech.html.ImageElement;
import ibs.tech.html.NewLineElement;
import ibs.tech.html.Page;
import ibs.tech.html.RowElement;
import ibs.tech.html.ScriptElement;
import ibs.tech.html.StyleSheetElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TableElement;
import ibs.tech.html.TextElement;
import ibs.tech.http.HttpArguments;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;
import ibs.util.DateTimeHelpers;
import ibs.util.GeneralException;
import ibs.util.Helpers;
import ibs.util.StringHelpers;
import ibs.util.UtilExceptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;


/******************************************************************************
 * This class implements some useful methods for IO. <BR/>
 *
 * @version     $Id: IOHelpers.java,v 1.45 2012/12/21 08:33:54 btatzmann Exp $
 *
 * @author      Klaus Reimüller (KR), 021016
 ******************************************************************************
 */
public abstract class IOHelpers extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: IOHelpers.java,v 1.45 2012/12/21 08:33:54 btatzmann Exp $";


    /**
     * Default encoder name for URL encoding. <BR/>
     */
//    private static String dfltEncName = AccessController.doPrivileged (
//        new GetPropertyAction ("file.encoding"));
    
    /**
     * Default character encoding name for URL encoding. <BR/>
     */
    private static final String DEFAULT_URL_CHARACTER_ENCODING = "UTF-8";

    /**
     * Javascript function for loading an url. <BR/>
     */
    private static String JS_LOAD = "top.load (";

    /**
     * URL character: space. <BR/>
     */
    private static final String URLCH_SPACE = "%20";

    /**
     * The separator used to separate multiple values of a multi selection field in the presentation layer. <BR/>
     */
    public static final String MULTISELECTION_DISPLAY_SAPERATOR = ", ";


    /***************************************************************************
     * Set the base href of a page. <BR/>
     *
     * @param page The page in which to set the base.
     * @param env Environment for getting input and generating output.
     */
    public static void setBase (Page page, Environment env)
    {
        IOHelpers.setBase (page, env.getApplicationInfo (),
            env.getSessionInfo (), env);
    } // setBase


    /***************************************************************************
     * Set the base href of a page. <BR/>
     *
     * @param page The page in which to set the base.
     * @param app The global application info.
     * @param sess The actual session info.
     * @param env Environment for getting input and generating output.
     */
    public static void setBase (Page page, ApplicationInfo app,
                                SessionInfo sess, Environment env)
    {
        String baseUrl = "";            // the base url
        UserInfo userInfo = null;

        if (sess != null && sess.userInfo != null &&
            ((UserInfo) sess.userInfo).homepagePath != null)
                                        // homepagePath exists?
        {
            userInfo = (UserInfo) sess.userInfo;
            if ((userInfo.homepagePath.indexOf (IOConstants.URL_HTTPS) < 0) &&
                (userInfo.homepagePath.indexOf (IOConstants.URL_HTTP) < 0))
                                        // no protocol in homepagepath included
            {
                if ((Ssl.isHttpsRequest (env, app)) &&
                    (((ServerRecord) sess.actServerConfiguration).getSsl ()))
                                        // secure base required
                {
                    baseUrl = Ssl.getSecureUrl (userInfo.homepagePath, sess);
                } // if secure base required
                else                    // non-secure base required
                {
                    baseUrl = Ssl.getNonSecureUrl (userInfo.homepagePath, sess);
                } // else non-secure base required
            } // if no protocol in homepagepath included
            else                        // a protocol already included,
                                        // full homepagepath already set
            {
                // use the full homepagePath:
                baseUrl = userInfo.homepagePath;
            } // else full homepagepath already set

            page.head.baseScript = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);
            page.head.baseScript.addScript (
                "document.write ('<BASE HREF=\"');" +
                "if (\"\" + top.system != \"undefined\")" +
                " document.write (top.system.getBaseDir ());" +
                "else" +
                " document.write ('" + baseUrl + "/');" +
                "document.write ('\">');"
            );
        } // if homepagePath exists
    } // setBase

    
    /**************************************************************************
     * Show a message. <BR/>
     * This method is just a mapper to {@link ibs.io.IOHelpers#showMessage (
     * java.lang.String, boolean, ibs.io.Environment) showMessage}
     *
     * @param   message     Text of the message.
     * @param   env         Environment for getting input and generating output.
     */
    public static void showMessage (String message, Environment env)
    {
        IOHelpers.showMessage (message, true, env);
    } // showMessage

    
    /**************************************************************************
     * Show a message. <BR/>
     * This method is just a mapper to {@link ibs.io.IOHelpers#showMessage (
     * java.lang.String, ibs.tech.html.ScriptElement, int,
     * ibs.io.Environment) showMessage}
     *
     * @param   message     Text of the message.
     * @param   enableMultilang
     *                      Defines if multilang message informations should be used.
     *                      This flag can be used to deactivate multilang when displaying
     *                      messages, when no multilang information is available.
     *                      For example during server startup.
     * @param   env         Environment for getting input and generating output.
     */
    public static void showMessage (String message, boolean enableMultilang, Environment env)
    {
        IOHelpers.showMessage (null, message, null, AppMessages.MST_INFO, enableMultilang, env);
    } // showMessage
    

    /**************************************************************************
     * Show a message. <BR/>
     * This method is just a mapper to {@link ibs.io.IOHelpers#showMessage (
     * java.lang.String, ibs.tech.html.ScriptElement, int,
     * ibs.io.session.ApplicationInfo, ibs.io.session.SessionInfo,
     * ibs.io.Environment) showMessage}
     *
     * @param   message     Text of the message.
     * @param   app         The actual application info.
     * @param   sess        The actual session info.
     * @param   env         Environment for getting input and generating output.
     */
    public static void showMessage (String message,
         ApplicationInfo app, SessionInfo sess, Environment env)
    {
        IOHelpers.showMessage (null, message, null, AppMessages.MST_INFO, app, sess, env);
    } // showMessage


    /**************************************************************************
     * Show a message. <BR/>
     * This method is just a mapper to {@link ibs.io.IOHelpers#showMessage (
     * java.lang.String, java.lang.String, ibs.tech.html.ScriptElement, int,
     * ibs.io.Environment) showMessage}
     *
     * @param   posText     Text which describes the error position
     * @param   message     Text of the message.
     * @param   env         Environment for getting input and generating output.
     */
    public static void showMessage (String posText, String message,
         Environment env)
    {
        IOHelpers.showMessage (posText, message, null, AppMessages.MST_INFO, true, env);
    } // showMessage


    /**************************************************************************
     * Show a message. <BR/>
     * This method is just a mapper to {@link ibs.io.IOHelpers#showMessage (
     * java.lang.String, java.lang.String, ibs.tech.html.ScriptElement, int,
     * ibs.io.session.ApplicationInfo, ibs.io.session.SessionInfo,
     * ibs.io.Environment) showMessage}
     *
     * @param   posText     Text which describes the error position
     * @param   message     Text of the message.
     * @param   app         The actual application info.
     * @param   sess        The actual session info.
     * @param   env         Environment for getting input and generating output.
     */
    public static void showMessage (String posText, String message,
         ApplicationInfo app, SessionInfo sess, Environment env)
    {
        IOHelpers.showMessage (posText, message, null, AppMessages.MST_INFO, app, sess, env);
    } // showMessage


    /**************************************************************************
     * Show a message based on an exception. <BR/>
     * This method is just a mapper to {@link ibs.io.IOHelpers#showMessage (
     * java.lang.String, ibs.tech.html.ScriptElement, int,
     * ibs.io.Environment) showMessage}. <BR/>
     * The text is composed by <CODE>exc.getMessage + exc.getError</CODE>.
     *
     * @param   exc         The exception.
     * @param   env         Environment for getting input and generating output.
     */
    public static void showMessage (GeneralException exc, Environment env)
    {
        IOHelpers.showMessage (null, exc, env, false);
    } // showMessage


    /**************************************************************************
     * Show a message based on an exception. <BR/>
     * This method is just a mapper to {@link ibs.io.IOHelpers#showMessage (
     * java.lang.String, ibs.tech.html.ScriptElement, int,
     * ibs.io.session.ApplicationInfo, ibs.io.session.SessionInfo,
     * ibs.io.Environment) showMessage}. <BR/>
     * The text is composed by <CODE>exc.getMessage + exc.getError</CODE>.
     *
     * @param   exc         The exception.
     * @param   app         The actual application info.
     * @param   sess        The actual session info.
     * @param   env         Environment for getting input and generating output.
     */
    public static void showMessage (GeneralException exc,
         ApplicationInfo app, SessionInfo sess, Environment env)
    {
        IOHelpers.showMessage (null, exc, app, sess, env, false);
    } // showMessage


    /**************************************************************************
     * Show a message based on an exception. <BR/>
     * This method is just a mapper to {@link ibs.io.IOHelpers#showMessage (
     * java.lang.String, ibs.tech.html.ScriptElement, int,
     * ibs.io.Environment) showMessage}. <BR/>
     * The text is composed by <CODE>exc.getMessage + exc.getError</CODE>.
     *
     * @param   exc         The exception.
     * @param   env         Environment for getting input and generating output.
     * @param   addStackTrace   Shall the stacktrace be added to the output?
     */
    public static void showMessage (GeneralException exc, Environment env,
                                    boolean addStackTrace)
    {
        IOHelpers.showMessage (null, exc, env, addStackTrace);
    } // showMessage


    /**************************************************************************
     * Show a message based on an exception. <BR/>
     * This method is just a mapper to {@link ibs.io.IOHelpers#showMessage (
     * java.lang.String, ibs.tech.html.ScriptElement, int,
     * ibs.io.session.ApplicationInfo, ibs.io.session.SessionInfo,
     * ibs.io.Environment) showMessage}. <BR/>
     * The text is composed by <CODE>exc.getMessage + exc.getError</CODE>.
     *
     * @param   exc         The exception.
     * @param   app         The actual application info.
     * @param   sess        The actual session info.
     * @param   env         Environment for getting input and generating output.
     * @param   addStackTrace   Shall the stacktrace be added to the output?
     */
    public static void showMessage (GeneralException exc,
         ApplicationInfo app, SessionInfo sess, Environment env,
         boolean addStackTrace)
    {
        IOHelpers.showMessage (null, exc, app, sess, env, addStackTrace);
    } // showMessage


    /**************************************************************************
     * Show a message based on an exception. <BR/>
     * This method is just a mapper to {@link ibs.io.IOHelpers#showMessage (
     * java.lang.String, java.lang.String, ibs.tech.html.ScriptElement, int,
     * ibs.io.Environment) showMessage}. <BR/>
     * The text is composed by <CODE>exc.getMessage + exc.getError</CODE>.
     *
     * @param   posText     Text which describes the error position
     * @param   exc         The exception.
     * @param   env         Environment for getting input and generating output.
     * @param   addStackTrace   Shall the stacktrace be added to the output?
     */
    public static void showMessage (String posText, GeneralException exc,
                                    Environment env, boolean addStackTrace)
    {
        if (addStackTrace)              // add stacktrace?
        {
            IOHelpers.showMessage (posText,
                IE302.TAG_BOLDBEGIN +
                exc.getMessage () + IE302.TAG_BOLDEND + ";" + IE302.TAG_NEWLINE +
//                IE302.TAG_BOLDBEGIN + exc.getError () + IE302.TAG_BOLDEND + ";" + IE302.TAG_NEWLINE +
                IOHelpers.formatStacktrace (Helpers.getStackTraceFromThrowable (exc).replace ("at ", "<BR/>at ")),
                null, AppMessages.MST_WARNING, true, env);
        } // if add stacktrace
        else                            // no stacktrace
        {
            IOHelpers.showMessage (
                posText, IE302.TAG_BOLDBEGIN + exc.getMessage () +
                    IE302.TAG_BOLDEND + ";" + IE302.TAG_NEWLINE +
                    exc.getError (), null, AppMessages.MST_WARNING, true, env);
        } // else no stacktrace
    } // showMessage


    /**************************************************************************
     * Show a message based on an exception. <BR/>
     * This method is just a mapper to {@link ibs.io.IOHelpers#showMessage (
     * java.lang.String, java.lang.String, ibs.tech.html.ScriptElement, int,
     * ibs.io.session.ApplicationInfo, ibs.io.session.SessionInfo,
     * ibs.io.Environment) showMessage}. <BR/>
     * The text is composed by <CODE>exc.getMessage + exc.getError</CODE>.
     *
     * @param   posText     Text which describes the error position
     * @param   exc         The exception.
     * @param   app         The actual application info.
     * @param   sess        The actual session info.
     * @param   env         Environment for getting input and generating output.
     * @param   addStackTrace   Shall the stacktrace be added to the output?
     */
    public static void showMessage (String posText, GeneralException exc,
         ApplicationInfo app, SessionInfo sess, Environment env,
         boolean addStackTrace)
    {
        if (addStackTrace)              // add stacktrace?
        {
            IOHelpers.showMessage (posText,
                IE302.TAG_BOLDBEGIN +
                exc.getMessage () + IE302.TAG_BOLDEND + ";" + IE302.TAG_NEWLINE +
//                IE302.TAG_BOLDBEGIN + exc.getError () + IE302.TAG_BOLDEND + ";" + IE302.TAG_NEWLINE +
                IOHelpers.formatStacktrace (Helpers.getStackTraceFromThrowable (exc).replace ("at ", "<BR/>at ")),
                null, AppMessages.MST_WARNING, app, sess, env);
        } // if add stacktrace
        else                            // no stacktrace
        {
            IOHelpers.showMessage (posText, IE302.TAG_BOLDBEGIN + exc.getMessage () +
                IE302.TAG_BOLDEND + ";" + IE302.TAG_NEWLINE + exc.getError (), null, AppMessages.MST_WARNING,
                app, sess, env);
        } // else no stacktrace
    } // showMessage


    /**************************************************************************
     * Show a message based on an exception. <BR/>
     * This method is just a mapper to {@link ibs.io.IOHelpers#showMessage (
     * java.lang.String, ibs.tech.html.ScriptElement, int,
     * ibs.io.Environment) showMessage}. <BR/>
     * The text is composed by <CODE>exc.getMessage + exc.getError</CODE>.
     *
     * @param   exc         The exception.
     * @param   env         Environment for getting input and generating output.
     */
    public static void showMessage (BuildException exc, Environment env)
    {
        IOHelpers.showMessage (null, exc, env, false);
    } // showMessage


    /**************************************************************************
     * Show a message based on an exception. <BR/>
     * This method is just a mapper to {@link ibs.io.IOHelpers#showMessage (
     * java.lang.String, ibs.tech.html.ScriptElement, int,
     * ibs.io.session.ApplicationInfo, ibs.io.session.SessionInfo,
     * ibs.io.Environment) showMessage}. <BR/>
     * The text is composed by <CODE>exc.getMessage + exc.getError</CODE>.
     *
     * @param   exc         The exception.
     * @param   app         The actual application info.
     * @param   sess        The actual session info.
     * @param   env         Environment for getting input and generating output.
     */
    public static void showMessage (BuildException exc,
         ApplicationInfo app, SessionInfo sess, Environment env)
    {
        IOHelpers.showMessage (null, exc, app, sess, env, false);
    } // showMessage


    /**************************************************************************
     * Show a message based on an exception. <BR/>
     * This method is just a mapper to {@link ibs.io.IOHelpers#showMessage (
     * java.lang.String, ibs.tech.html.ScriptElement, int,
     * ibs.io.Environment) showMessage}. <BR/>
     * The text is composed by <CODE>exc.getMessage + exc.getError</CODE>.
     *
     * @param   exc         The exception.
     * @param   env         Environment for getting input and generating output.
     * @param   addStackTrace   Shall the stacktrace be added to the output?
     */
    public static void showMessage (BuildException exc, Environment env,
                                    boolean addStackTrace)
    {
        IOHelpers.showMessage (null, exc, env, addStackTrace);
    } // showMessage


    /**************************************************************************
     * Show a message based on an exception. <BR/>
     * This method is just a mapper to {@link ibs.io.IOHelpers#showMessage (
     * java.lang.String, ibs.tech.html.ScriptElement, int,
     * ibs.io.session.ApplicationInfo, ibs.io.session.SessionInfo,
     * ibs.io.Environment) showMessage}. <BR/>
     * The text is composed by <CODE>exc.getMessage + exc.getError</CODE>.
     *
     * @param   exc         The exception.
     * @param   app         The actual application info.
     * @param   sess        The actual session info.
     * @param   env         Environment for getting input and generating output.
     * @param   addStackTrace   Shall the stacktrace be added to the output?
     */
    public static void showMessage (BuildException exc,
         ApplicationInfo app, SessionInfo sess, Environment env,
         boolean addStackTrace)
    {
        IOHelpers.showMessage (null, exc, app, sess, env, addStackTrace);
    } // showMessage


    /**************************************************************************
     * Show a message based on an exception. <BR/>
     * This method is just a mapper to {@link ibs.io.IOHelpers#showMessage (
     * java.lang.String, java.lang.String, ibs.tech.html.ScriptElement, int,
     * ibs.io.Environment) showMessage}. <BR/>
     * The text is composed by <CODE>exc.getMessage + exc.getError</CODE>.
     *
     * @param   posText     Text which describes the error position
     * @param   exc         The exception.
     * @param   env         Environment for getting input and generating output.
     * @param   addStackTrace   Shall the stacktrace be added to the output?
     */
    public static void showMessage (String posText, BuildException exc,
                                    Environment env, boolean addStackTrace)
    {
        if (addStackTrace)              // add stacktrace?
        {
            IOHelpers.showMessage (posText, IE302.TAG_BOLDBEGIN + exc.getMessage () +
                IE302.TAG_BOLDEND + ";" + IE302.TAG_NEWLINE +
                IOHelpers.formatStacktrace (Helpers.getStackTraceFromThrowable (exc).replace ("at ", "<BR/>at ")),
                null, AppMessages.MST_WARNING, true, env);
        } // if add stacktrace
        else                            // no stacktrace
        {
            IOHelpers.showMessage (posText,
                IE302.TAG_BOLDBEGIN + exc.getMessage () + IE302.TAG_BOLDEND + ";", null,
                AppMessages.MST_WARNING, true, env);
        } // else no stacktrace
    } // showMessage


    /**************************************************************************
     * Show a message based on an exception. <BR/>
     * This method is just a mapper to {@link ibs.io.IOHelpers#showMessage (
     * java.lang.String, java.lang.String, ibs.tech.html.ScriptElement, int,
     * ibs.io.session.ApplicationInfo, ibs.io.session.SessionInfo,
     * ibs.io.Environment) showMessage}. <BR/>
     * The text is composed by <CODE>exc.getMessage + exc.getError</CODE>.
     *
     * @param   posText     Text which describes the error position
     * @param   exc         The exception.
     * @param   app         The actual application info.
     * @param   sess        The actual session info.
     * @param   env         Environment for getting input and generating output.
     * @param   addStackTrace   Shall the stacktrace be added to the output?
     */
    public static void showMessage (String posText, BuildException exc,
         ApplicationInfo app, SessionInfo sess, Environment env,
         boolean addStackTrace)
    {
        if (addStackTrace)              // add stacktrace?
        {
            IOHelpers.showMessage (posText, IE302.TAG_BOLDBEGIN + exc.getMessage () +
                IE302.TAG_BOLDEND + ";" + IE302.TAG_NEWLINE +
                IOHelpers.formatStacktrace (Helpers.getStackTraceFromThrowable (exc).replace ("at ", "<BR/>at ")),
                null, AppMessages.MST_WARNING, app, sess, env);
        } // if add stacktrace
        else                            // no stacktrace
        {
            IOHelpers.showMessage (posText,
                IE302.TAG_BOLDBEGIN + exc.getMessage () + IE302.TAG_BOLDEND + ";", null,
                AppMessages.MST_WARNING, app, sess, env);
        } // else no stacktrace
    } // showMessage


    /**************************************************************************
     * Show a message based on an exception. <BR/>
     * This method is just a mapper to {@link ibs.io.IOHelpers#showMessage (
     * java.lang.String, ibs.tech.html.ScriptElement, int,
     * ibs.io.Environment) showMessage}. <BR/>
     * The text is composed by <CODE>exc.getMessage + exc.getError</CODE>.
     *
     * @param   exc         The exception.
     * @param   env         Environment for getting input and generating output.
     */
    public static void showMessage (Throwable exc, Environment env)
    {
        IOHelpers.showMessage (null, exc, env, false);
    } // showMessage


    /**************************************************************************
     * Show a message based on an exception. <BR/>
     * This method is just a mapper to {@link ibs.io.IOHelpers#showMessage (
     * java.lang.String, ibs.tech.html.ScriptElement, int,
     * ibs.io.session.ApplicationInfo, ibs.io.session.SessionInfo,
     * ibs.io.Environment) showMessage}. <BR/>
     * The text is composed by <CODE>exc.getMessage + exc.getError</CODE>.
     *
     * @param   exc         The exception.
     * @param   app         The actual application info.
     * @param   sess        The actual session info.
     * @param   env         Environment for getting input and generating output.
     */
    public static void showMessage (Throwable exc,
         ApplicationInfo app, SessionInfo sess, Environment env)
    {
        IOHelpers.showMessage (null, exc, app, sess, env, false);
    } // showMessage


    /**************************************************************************
     * Show a message based on an exception. <BR/>
     * This method is just a mapper to {@link ibs.io.IOHelpers#showMessage (
     * java.lang.String, ibs.tech.html.ScriptElement, int,
     * ibs.io.Environment) showMessage}. <BR/>
     * The text is composed by <CODE>exc.getMessage + exc.getError</CODE>.
     *
     * @param   exc         The exception.
     * @param   env         Environment for getting input and generating output.
     * @param   addStackTrace   Shall the stacktrace be added to the output?
     */
    public static void showMessage (Throwable exc, Environment env,
                                    boolean addStackTrace)
    {
        IOHelpers.showMessage (null, exc, env, addStackTrace);
    } // showMessage


    /**************************************************************************
     * Show a message based on an exception. <BR/>
     * This method is just a mapper to {@link ibs.io.IOHelpers#showMessage (
     * java.lang.String, ibs.tech.html.ScriptElement, int,
     * ibs.io.session.ApplicationInfo, ibs.io.session.SessionInfo,
     * ibs.io.Environment) showMessage}. <BR/>
     * The text is composed by <CODE>exc.getMessage + exc.getError</CODE>.
     *
     * @param   exc         The exception.
     * @param   app         The actual application info.
     * @param   sess        The actual session info.
     * @param   env         Environment for getting input and generating output.
     * @param   addStackTrace   Shall the stacktrace be added to the output?
     */
    public static void showMessage (Throwable exc,
         ApplicationInfo app, SessionInfo sess, Environment env,
         boolean addStackTrace)
    {
        IOHelpers.showMessage (null, exc, app, sess, env, addStackTrace);
    } // showMessage


    /**************************************************************************
     * Show a message based on an exception. <BR/>
     * This method is just a mapper to {@link ibs.io.IOHelpers#showMessage (
     * java.lang.String, java.lang.String, ibs.tech.html.ScriptElement, int,
     * ibs.io.Environment) showMessage}. <BR/>
     * The text is composed by <CODE>exc.getMessage + exc.getError</CODE>.
     *
     * @param   posText     Text which describes the error position
     * @param   exc         The exception.
     * @param   env         Environment for getting input and generating output.
     * @param   addStackTrace   Shall the stacktrace be added to the output?
     */
    public static void showMessage (String posText, Throwable exc,
                                    Environment env, boolean addStackTrace)
    {
        if (addStackTrace)              // add stacktrace?
        {
            IOHelpers.showMessage (posText, IE302.TAG_BOLDBEGIN + exc.getMessage () +
                IE302.TAG_BOLDEND + ";" + IE302.TAG_NEWLINE +
                IOHelpers.formatStacktrace (Helpers.getStackTraceFromThrowable (exc).replace ("at ", "<BR/>at ")),
                null, AppMessages.MST_WARNING, true, env);
        } // if add stacktrace
        else                            // no stacktrace
        {
            IOHelpers.showMessage (posText,
                IE302.TAG_BOLDBEGIN + exc.getMessage () + IE302.TAG_BOLDEND + ";", null,
                AppMessages.MST_WARNING, true, env);
        } // else no stacktrace
    } // showMessage


    /**************************************************************************
     * Show a message based on an exception. <BR/>
     * This method is just a mapper to {@link ibs.io.IOHelpers#showMessage (
     * java.lang.String, java.lang.String, ibs.tech.html.ScriptElement, int,
     * ibs.io.session.ApplicationInfo, ibs.io.session.SessionInfo,
     * ibs.io.Environment) showMessage}. <BR/>
     * The text is composed by <CODE>exc.getMessage + exc.getError</CODE>.
     *
     * @param   posText     Text which describes the error position
     * @param   exc         The exception.
     * @param   app         The actual application info.
     * @param   sess        The actual session info.
     * @param   env         Environment for getting input and generating output.
     * @param   addStackTrace   Shall the stacktrace be added to the output?
     */
    public static void showMessage (String posText, Throwable exc,
         ApplicationInfo app, SessionInfo sess, Environment env,
         boolean addStackTrace)
    {
        if (addStackTrace)              // add stacktrace?
        {
            IOHelpers.showMessage (posText, IE302.TAG_BOLDBEGIN + exc.getMessage () +
                IE302.TAG_BOLDEND + ";" + IE302.TAG_NEWLINE +
                IOHelpers.formatStacktrace (Helpers.getStackTraceFromThrowable (exc).replace ("at ", "<BR/>at ")),
                null, AppMessages.MST_WARNING, app, sess, env);
        } // if add stacktrace
        else                            // no stacktrace
        {
            IOHelpers.showMessage (posText,
                IE302.TAG_BOLDBEGIN + exc.getMessage () + IE302.TAG_BOLDEND + ";", null,
                AppMessages.MST_WARNING, app, sess, env);
        } // else no stacktrace
    } // showMessage


    /**************************************************************************
     * Show a message. <BR/>
     * This method is just a mapper to {@link ibs.io.IOHelpers#showMessage (
     * java.lang.String, ibs.tech.html.ScriptElement, int,
     * ibs.io.Environment) showMessage}
     *
     * @param   message     Text of the message.
     * @param   script      Script to be performed when the message is shown.
     * @param   env         Environment for getting input and generating output.
     */
    public static void showMessage (String message, ScriptElement script,
                                    Environment env)
    {
        IOHelpers.showMessage (null, message, script, AppMessages.MST_INFO,
            true, env);
    } // showMessage


    /**************************************************************************
     * Show a message. <BR/>
     * This method is just a mapper to {@link ibs.io.IOHelpers#showMessage (
     * java.lang.String, ibs.tech.html.ScriptElement, int,
     * ibs.io.session.ApplicationInfo, ibs.io.session.SessionInfo,
     * ibs.io.Environment) showMessage}
     *
     * @param   message     Text of the message.
     * @param   script      Script to be performed when the message is shown.
     * @param   app         The actual application info.
     * @param   sess        The actual session info.
     * @param   env         Environment for getting input and generating output.
     */
    public static void showMessage (String message, ScriptElement script,
         ApplicationInfo app, SessionInfo sess, Environment env)
    {
        IOHelpers.showMessage (null, message, script, AppMessages.MST_INFO,
            app, sess, env);
    } // showMessage


    /**************************************************************************
     * Show a message. <BR/>
     * This method is just a mapper to {@link ibs.io.IOHelpers#showMessage (
     * java.lang.String, ibs.tech.html.ScriptElement, int,
     * ibs.io.Environment) showMessage}
     *
     * @param   posText     Text which describes the error position
     * @param   message     Text of the message.
     * @param   script      Script to be performed when the message is shown.
     * @param   env         Environment for getting input and generating output.
     */
    public static void showMessage (String posText, String message,
                                    ScriptElement script, Environment env)
    {
        IOHelpers.showMessage (posText, message, script, AppMessages.MST_INFO,
                true, env);
    } // showMessage


    /**************************************************************************
     * Show a message. <BR/>
     * This method is just a mapper to {@link ibs.io.IOHelpers#showMessage (
     * java.lang.String, ibs.tech.html.ScriptElement, int,
     * ibs.io.session.ApplicationInfo, ibs.io.session.SessionInfo,
     * ibs.io.Environment) showMessage}
     *
     * @param   posText     Text which describes the error position
     * @param   message     Text of the message.
     * @param   script      Script to be performed when the message is shown.
     * @param   app         The actual application info.
     * @param   sess        The actual session info.
     * @param   env         Environment for getting input and generating output.
     */
    public static void showMessage (String posText, String message,
                                    ScriptElement script, ApplicationInfo app,
                                    SessionInfo sess, Environment env)
    {
        IOHelpers.showMessage (posText, message, script, AppMessages.MST_INFO,
            app, sess, env);
    } // showMessage


    /**************************************************************************
     * Show a message with a messagetype image. <BR/>
     * This method is just a mapper to {@link ibs.io.IOHelpers#showMessage (
     * java.lang.String, java.lang.String, ibs.tech.html.ScriptElement, int,
     * ibs.io.Environment) showMessage}
     *
     * @param   message     Text of the message.
     * @param   script      Script to be performed when the message is shown.
     * @param   msgType     Type of message which determines the image being
     *                      shown.
     * @param   env         Environment for getting input and generating output.
     */
    public static void showMessage (String message, ScriptElement script,
                                    int msgType, Environment env)
    {
        IOHelpers.showMessage (null, message, script, msgType, true, env);
    } // showMessage


    /**************************************************************************
     * Show a message with a messagetype image. <BR/>
     * This method is just a mapper to {@link ibs.io.IOHelpers#showMessage (
     * java.lang.String, java.lang.String, ibs.tech.html.ScriptElement, int,
     * ibs.io.session.ApplicationInfo, ibs.io.session.SessionInfo,
     * ibs.io.Environment) showMessage}
     *
     * @param   message     Text of the message.
     * @param   script      Script to be performed when the message is shown.
     * @param   msgType     Type of message which determines the image being
     *                      shown.
     * @param   app         The actual application info.
     * @param   sess        The actual session info.
     * @param   env         Environment for getting input and generating output.
     */
    public static void showMessage (String message, ScriptElement script,
                                    int msgType, ApplicationInfo app,
                                    SessionInfo sess, Environment env)
    {
        IOHelpers.showMessage (null, message, script, msgType, true, app, sess, env);
    } // showMessage


    /**************************************************************************
     * Show a message with a messagetype image. <BR/>
     *
     * @param   posText     Text which describes the error position
     * @param   message     Text of the message.
     * @param   script      Script to be performed when the message is shown.
     * @param   msgType     Type of message which determines the image being
     *                      shown.
     * @param   enableMultilang
     *                      Defines if multilang message informations should be used.
     *                      This flag can be used to deactivate multilang when displaying
     *                      messages, when no multilang information is available.
     *                      For example during server startup.
     * @param   env         Environment for getting input and generating output.
     */
    public static void showMessage (String posText, String message,
                                    ScriptElement script, int msgType,
                                    boolean enableMultilang, Environment env)
    {
        IOHelpers.showMessage (posText, message, script, msgType, enableMultilang,
            env.getApplicationInfo (), env.getSessionInfo (), env);
    } // showMessage


    /**************************************************************************
     * Show a message with a messagetype image. <BR/>
     *
     * @param   posText     Text which describes the error position
     * @param   message     Text of the message.
     * @param   script      Script to be performed when the message is shown.
     * @param   msgType     Type of message which determines the image being
     *                      shown.
     * @param   app         The actual application info.
     * @param   sess        The actual session info.
     * @param   env         Environment for getting input and generating output.
     */
    public static void showMessage (String posText, String message,
                                    ScriptElement script, int msgType,
                                    ApplicationInfo app, SessionInfo sess,
                                    Environment env)
    {
        IOHelpers.showMessage (posText, message, script, msgType,
                true, app, sess, env);
    } // showMessage
    
    
    /**************************************************************************
     * Show a message with a messagetype image. <BR/>
     *
     * @param   posText     Text which describes the error position
     * @param   message     Text of the message.
     * @param   script      Script to be performed when the message is shown.
     * @param   msgType     Type of message which determines the image being
     *                      shown.
     * @param   enableMultilang
     *                      Defines if multilang message informations should be used.
     *                      This flag can be used to deactivate multilang when displaying
     *                      messages, when no multilang information is available.
     *                      For example during server startup.
     * @param   app         The actual application info.
     * @param   sess        The actual session info.
     * @param   env         Environment for getting input and generating output.
     */
    public static void showMessage (String posText, String message,
                                    ScriptElement script, int msgType,
                                    boolean enableMultilang,
                                    ApplicationInfo app, SessionInfo sess,
                                    Environment env)
    {
        TableElement table;
        RowElement tr;
        TableDataElement td;
        ImageElement img;
        StringBuffer msgText = new StringBuffer (); // the text to be displayed

        // show the message to the user:
        String messageHeader = enableMultilang ? MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                AppMessages.ML_MSG_MESSAGEHEADER, env) : AppMessages.MSG_DEF_MESSAGEHEADER;
        
        Page page = new Page (messageHeader, false);

        // compose the complete text:
        if (posText != null)
        {
            msgText.append (posText).append (": " + IE302.TAG_NEWLINE);
        } // if
        msgText.append (message);

        // set the document's base:
        IOHelpers.setBase (page, app, sess, env);

        if ((sess != null) && (sess.activeLayout != null))
        {
            // style sheet file is loaded:
            StyleSheetElement style = new StyleSheetElement ();
            style.importSS = sess.activeLayout.path + env.getBrowser () + "/" +
                sess.activeLayout.elems[LayoutConstants.MESSAGE].styleSheet;
            page.head.addElement (style);
        } // if

        page.body.onUnload = null;
        table = new TableElement ();
        table.cellpadding = 2;
        table.border = 0;
        table.width = HtmlConstants.TAV_FULLWIDTH;
        table.classId = "message";

        Font captionFont = new Font (AppConstants.FONT_CAPTION, AppConstants.FONTSIZE_CAPTION);
        captionFont.bold = true;
        captionFont.color = "DARKBLUE";

        tr = new RowElement (2);
//        tr.height = "35";
        // build the message icon:
        String path = "";
        tr.classId = CssConstants.CLASS_MESSAGE;

        if (sess.activeLayout != null)
        {
            path = sess.activeLayout.path +
                sess.activeLayout.elems[LayoutConstants.MESSAGE].images;
        } // if
        else
        {
            if ((path = env.getStringParam (BOArguments.ARG_PATH)) != null)
            {
                // check if the path ends with "app"
                if (!path.endsWith (BOPathConstants.PATH_APP))
                {
                    path += BOPathConstants.PATH_APP;
                } // if
                // construct the path:
                path += BOPathConstants.PATH_MESSAGEICONS;
            } // if
            else
            {
                path = BOPathConstants.PATH_MESSAGEICONS;
            } // else
        } // else

        try
        {
            img = new ImageElement (path + AppMessages.MST_IMAGES[msgType]);
        } // try
        catch (ArrayIndexOutOfBoundsException e)
        {
            img = new ImageElement (path + AppMessages.MST_IMAGES[0]);
        } // catch

        td = new TableDataElement (img);
        td.valign = IOConstants.ALIGN_MIDDLE;
        td.width = "1%";
        td.rowspan = 2;
        tr.addElement (td);

        TextElement text;
        // add caption text:
        try
        {
            String header = enableMultilang ?
                    MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                    AppMessages.MST_HEADERS [msgType], env) : AppMessages.MST_DEF_HEADERS [msgType];
            
            text = new TextElement (header);
            td = new TableDataElement (text);
        } // try
        catch (ArrayIndexOutOfBoundsException e)
        {
            String header = enableMultilang ?
                    MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                            AppMessages.MST_HEADERS[0], env) : AppMessages.MST_DEF_HEADERS [0];
                    
            text = new TextElement (header);
            td = new TableDataElement (text);
        } // catch

        td.classId = CssConstants.CLASS_MSGHEADER;
        td.valign = IOConstants.ALIGN_BOTTOM;
        td.width = "99%";
        tr.addElement (td);
        table.addElement (tr);

        // build the message:
        tr = new RowElement (1);
        td = new TableDataElement (new TextElement (msgText.toString ()));
        td.classId = CssConstants.CLASS_MESSAGE;

        td.valign = IOConstants.ALIGN_TOP;
        tr.addElement (td);
        table.addElement (tr);
        page.body.addElement (table);

        if (script != null)
        {
            page.body.addElement (script);
        } // if

        try
        {
            page.build (env);
        } // try
        catch (BuildException e)
        {
            env.write (e.getMsg ());
        } // catch
    } // showMessage


    /**************************************************************************
     * Format the stacktrace from an exception. <BR/>
     *
     * @param   stackTrace  The stack trace to be formatted.
     *
     * @return  The formatted stacktrace.
     *
     * @see ibs.util.Helpers#getStackTraceFromThrowable (Throwable)
     */
    public static String formatStacktrace (String stackTrace)
    {
        return
        stackTrace.replaceAll ("Caused by(.*)\tat ",
            IE302.TAG_NEWLINE + IE302.TAG_BOLDBEGIN + "Caused by$1" +
            IE302.TAG_BOLDEND +
            IE302.TAG_NEWLINE + IE302.HCH_NBSP + IE302.HCH_NBSP +
            IE302.HCH_NBSP + IE302.HCH_NBSP + "at ");
    } // formatStacktrace


    /**************************************************************************
     * Prepare a javascript message for being displayed within an alert box.
     * <BR/>
     * It is important that this message does not contain quotation marks.
     * These must be escaped.
     *
     * @param   message     Text of the message.
     *
     * @return  The prepared message text.
     */
    public static StringBuffer prepareJavaScriptMessage (String message)
    {
        // escape double quotes in order to avoid javascript problems
        // and return the result:
        return IOHelpers.prepareJavaScriptMessage (
            new StringBuffer ().append (message));
    } // prepareJavaScriptMessage


    /**************************************************************************
     * Prepare a javascript message for being displayed within an alert box.
     * <BR/>
     * It is important that this message does not contain quotation marks.
     * These must be escaped.
     *
     * @param   message     Text of the message.
     *
     * @return  The prepared message text.
     */
    public static StringBuffer prepareJavaScriptMessage (StringBuffer message)
    {
        // escape double quotes in order to avoid javascript problems
        // and return the result:
        return StringHelpers.replace (
                StringHelpers.replace (
                    StringHelpers.replace (message, "\\\"", "\""),
                    "\"", "\\\""),
                "'", "\\'");
    } // prepareJavaScriptMessage

    
    /**************************************************************************
     * Show a message as popup and has the option to add an additional script
     * to the page. The additional script will be executed after the message
     * alert. <BR/>
     *
     * @param   mlMessageBundle
     *                          The resource bundle containing the message.
     * @param   mlMessageKey    The resource bundle key for the message to
     *                          display.
     * @param   app         The global application info.
     * @param   sess        The actual session info.
     * @param   env         The actual environment.
     */
    public static void showPopupMessage (String mlMessageBundle, String mlMessageKey, ScriptElement addScript,
        ApplicationInfo app, SessionInfo sess, Environment env)
    {
        IOHelpers.showPopupMessage (MultilingualTextProvider.getMessage (
                mlMessageBundle, mlMessageKey, env),
                addScript, app, sess, env);
    } // showPopupMessage
    

    /**************************************************************************
     * Show a message as popup and has the option to add an additional script
     * to the page. The additional script will be executed after the message
     * alert. <BR/>
     *
     * @param   message     Text of the message.
     * @param   addScript   a javascript to add to the page
     * @param   app         The global application info.
     * @param   sess        The actual session info.
     * @param   env         The actual environment.
     */
    public static void showPopupMessage (String message, ScriptElement addScript,
        ApplicationInfo app, SessionInfo sess, Environment env)
    {
        Page page = new Page (MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
            AppMessages.ML_MSG_MESSAGEHEADER, env), false);
        ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);

        // set the document's base:
        IOHelpers.setBase (page, app, sess, env);

        page.body.onUnload = null;
        page.body.addElement (new BlankElement ());
        page.body.addElement (script);

        // check if any additional script should be added
        if (addScript != null)
        {
            page.body.addElement (addScript);
        } // if (addScript != null)

        // create script and escape doublequotes in order to avoid
        // javascript problems
        StringBuffer scriptBuffer = new StringBuffer ("alert (\"" +
            IOHelpers.prepareJavaScriptMessage (message) + "\");\n");

        // Switch back to search form if necessary
        String userInfoHistory = ((UserInfo) sess.userInfo).history.getName ();

        // check if user history was search result
        if (userInfoHistory != null &&
            userInfoHistory.equals (HistoryInfo.HISTORY_ENTRY_TYPE_QUERY))
        {
            scriptBuffer.append ("top.scripts.showSearchFrame(false,false,false);");
        } // if

        script.addScript (scriptBuffer);

        try
        {
            page.build (env);
        } // try
        catch (BuildException e)
        {
            env.write (e.getMsg ());
        } // catch
    } // showPopupMessage


    /**************************************************************************
     * Show a warning message. <BR/>
     * This method is just a mapper to {@link ibs.io.IOHelpers#printMessage
     * (String, String, int) printMessage}. <BR/>
     * The text is composed by <CODE>exc.getMessage</CODE>.
     *
     * @param   posText     Text which describes the error position
     * @param   obj         The actual object.
     * @param   message     Text of the message.
     */
    public static void printWarning (String posText, Object obj, String message)
    {
        IOHelpers.printMessage (obj.getClass ().getName () + " " + posText,
            message, AppMessages.MST_WARNING);
    } // printWarning


    /***************************************************************************
     * Show a warning message based on an exception. <BR/>
     * This method is just a mapper to {@link ibs.io.IOHelpers#printMessage
     * (String, String, int) printMessage}. <BR/>
     * The text is composed by <CODE>exc.getMessage</CODE>.
     *
     * @param posText Text which describes the error position
     * @param exc The exception.
     * @param addStackTrace Shall the stacktrace be added to the output?
     */
    public static void printWarning (String posText, Throwable exc,
                                     boolean addStackTrace)
    {
        if (addStackTrace)              // add stacktrace?
        {
            IOHelpers.printMessage (posText, IE302.TAG_BOLDBEGIN + exc.getMessage () +
                IE302.TAG_BOLDEND + ";" + IE302.TAG_NEWLINE + Helpers.getStackTraceFromThrowable (exc),
                AppMessages.MST_WARNING);
        } // if add stacktrace
        else                            // no stacktrace
        {
            IOHelpers.printMessage (posText, IE302.TAG_BOLDBEGIN + exc.getMessage () +
                IE302.TAG_BOLDEND + ";", AppMessages.MST_WARNING);
        } // else no stacktrace
    } // printWarning


    /**************************************************************************
     * Show a warning message based on an exception. <BR/>
     * This method is just a mapper to {@link ibs.io.IOHelpers#printMessage
     * (String, String, int) printMessage}. <BR/>
     * The text is composed by <CODE>exc.getMessage</CODE>.
     *
     * @param   posText     Text which describes the error position
     * @param   obj         The actual object.
     * @param   exc         The exception.
     * @param   addStackTrace   Shall the stacktrace be added to the output?
     */
    public static void printWarning (String posText, Object obj, Throwable exc,
                                     boolean addStackTrace)
    {
        if (addStackTrace)              // add stacktrace?
        {
            IOHelpers.printMessage (obj.getClass ().getName () + " " + posText,
                IE302.TAG_BOLDBEGIN + exc.getMessage () + IE302.TAG_BOLDEND + ";" + IE302.TAG_NEWLINE +
                    Helpers.getStackTraceFromThrowable (exc),
                AppMessages.MST_WARNING);
        } // if add stacktrace
        else                            // no stacktrace
        {
            IOHelpers.printMessage (obj.getClass ().getName () + " " + posText,
                IE302.TAG_BOLDBEGIN + exc.getMessage () + IE302.TAG_BOLDEND + ";", AppMessages.MST_WARNING);
        } // else no stacktrace
    } // printWarning


    /**************************************************************************
     * Show an error message. <BR/>
     * This method is just a mapper to {@link ibs.io.IOHelpers#printMessage
     * (String, String, int) printMessage}. <BR/>
     * The text is composed by <CODE>exc.getMessage</CODE>.
     *
     * @param   posText     Text which describes the error position
     * @param   obj         The actual object.
     * @param   message     Text of the message.
     */
    public static void printError (String posText, Object obj, String message)
    {
        IOHelpers.printMessage (obj.getClass ().getName () + " " + posText,
            message, AppMessages.MST_WARNING);
    } // printError


    /***************************************************************************
     * Show an error message based on an exception. <BR/>
     * This method is just a mapper to {@link ibs.io.IOHelpers#printMessage
     * (String, String, int) printMessage}. <BR/>
     * The text is composed by <CODE>exc.getMessage</CODE>.
     *
     * @param posText Text which describes the error position
     * @param exc The exception.
     * @param addStackTrace Shall the stacktrace be added to the output?
     */
    public static void printError (String posText, Throwable exc,
                                   boolean addStackTrace)
    {
        if (addStackTrace)              // add stacktrace?
        {
            IOHelpers.printMessage (posText, IE302.TAG_BOLDBEGIN + exc.getMessage () +
                IE302.TAG_BOLDEND + ";" + IE302.TAG_NEWLINE + Helpers.getStackTraceFromThrowable (exc),
                AppMessages.MST_ERROR);
        } // if add stacktrace
        else                            // no stacktrace
        {
            IOHelpers.printMessage (posText, IE302.TAG_BOLDBEGIN + exc.getMessage () +
                IE302.TAG_BOLDEND + ";", AppMessages.MST_ERROR);
        } // else no stacktrace
    } // printError


    /**************************************************************************
     * Show an error message based on an exception. <BR/>
     * This method is just a mapper to {@link ibs.io.IOHelpers#printMessage
     * (String, String, int) printMessage}. <BR/>
     * The text is composed by <CODE>exc.getMessage</CODE>.
     *
     * @param   posText     Text which describes the error position
     * @param   obj         The actual object.
     * @param   exc         The exception.
     * @param   addStackTrace   Shall the stacktrace be added to the output?
     */
    public static void printError (String posText, Object obj, Throwable exc,
                                   boolean addStackTrace)
    {
        if (addStackTrace)              // add stacktrace?
        {
            IOHelpers.printMessage (obj.getClass ().getName () + " " + posText,
                IE302.TAG_BOLDBEGIN + exc.getMessage () + IE302.TAG_BOLDEND + ";" + IE302.TAG_NEWLINE +
                    Helpers.getStackTraceFromThrowable (exc),
                AppMessages.MST_ERROR);
        } // if add stacktrace
        else                            // no stacktrace
        {
            IOHelpers.printMessage (obj.getClass ().getName () + " " + posText,
                IE302.TAG_BOLDBEGIN + exc.getMessage () + IE302.TAG_BOLDEND + ";", AppMessages.MST_ERROR);
        } // else no stacktrace
    } // printError


    /**************************************************************************
     * Show a message with a messagetype image. <BR/>
     *
     * @param   posText     Text which describes the error position
     * @param   message     Text of the message.
     * @param   msgType     Type of message.
     */
    public static void printMessage (String posText, String message, int msgType)
    {
        StringBuffer msgText = new StringBuffer (); // the text to be displayed
        int headerLen = AppMessages.MST_HEADERS[msgType].length () + 2;
        StringBuffer lineStart = new StringBuffer (
            "                              ".substring (0, headerLen));

        // set the message prefix:
        // TODO RB: Call
        //          MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, AppMessages.MST_HEADERS[msgType], null))
        //          to get the text in the correct language
        msgText.append (AppMessages.MST_HEADERS[msgType])
               .append (": ");

        // compose the complete text:
        if (posText != null)
        {
            msgText.append (posText).append (": \n");
        } // if
        msgText.append (IOHelpers.splitMessage (message, 80, lineStart));

        if (msgType == AppMessages.MST_ERROR)
        {
            System.err.println (msgText);
        } // if
        else
        {
            System.out.println (msgText);
        } // else
    } // printMessage


    /**************************************************************************
     * Show trace message. <BR/>
     *
     * @param   message Text to be printed out.
     */
    public static void printTrace (String message)
    {
        StringBuffer msgText = new StringBuffer (); // the text to be displayed
        String header = DateTimeHelpers.dateTimeToString (new Date (), "hh:mm:ss:SSS");
        int headerLen = header.length () + 1;
        StringBuffer lineStart = new StringBuffer (
            "                              ".substring (0, headerLen));

        // set the message prefix:
        msgText.append (header)
               .append (" ");

        // compose the complete text:
        msgText.append (IOHelpers.splitMessage (message, 80, lineStart).delete (
            0, headerLen));

        System.out.println (msgText);
    } // printTrace


    /**************************************************************************
     * Split a message into several lines. <BR/>
     * The message is splitted into lines of the maximum length.
     * If there are already line separators they are left unchanged.
     *
     * @param   message     Text of the message.
     * @param   lineLength  The maximum length of one line.
     * @param   linePrefix  The prefix for each line.
     *
     * @return  The constructed splitted message.
     */
    public static StringBuffer splitMessage (String message, int lineLength,
                                             StringBuffer linePrefix)
    {
        StringBuffer retVal = new StringBuffer (); // the result
        StringBuffer linePrefixLocal = linePrefix; // variable for local assignments
        int headerLen = linePrefixLocal.length ();
        int textLen = lineLength - headerLen; // length of text part of line
        int fullLen = message.length ();
        int pos = 0;                    // actual string position
        int oldPos = 0;                 // previous string position
        char newLineChar = '\n';
        boolean finished = false;
        StringBuffer newLinePrefix =
            new StringBuffer ().append (newLineChar).append (linePrefixLocal);

        // loop through the message until finished:
        while (!finished)
        {
            // check if there is a separator in the line:
            if ((pos = message.indexOf (newLineChar, oldPos)) >= 0 &&
                pos <= (oldPos + textLen))
            {
                retVal
                    .append (linePrefixLocal)
                    .append (message.substring (oldPos, ++pos));
                oldPos = pos;
            } // if
            // check if this is the last line:
            else if ((fullLen - oldPos) <= textLen)
            {
                // add last part of string:
                retVal
                    .append (linePrefixLocal)
                    .append (message.substring (oldPos));
                finished = true;
            } // if
            else                        // normal line
            {
                retVal
                    .append (linePrefixLocal)
                    .append (message.substring (oldPos, oldPos + textLen));
                oldPos += textLen;
            } // else normal line

            linePrefixLocal = newLinePrefix;
        } // while !finished

        // return the result:
        return retVal;
    } // splitMessage


    /**************************************************************************
     * Prints a message. <BR/>
     * This is used in order to encapsulate the destination
     * of a print command. It will be System.out at the moment but can
     * be extended in the future to print to a file. <BR/>
     *
     * @param   message     the message to be printed
     */
    public static void printMessage (String message)
    {
        System.out.println (message);
    } // printMessage


    /**************************************************************************
     * Create a html page which is just processing a piece of JavaScript code
     * on the client. <BR/>
     *
     * @param   code    The complete code to be processed.
     * @param   env     The actual environment.
     */
    public static void processJavaScriptCode (String code, Environment env)
    {
        IOHelpers.processJavaScriptCode (code, env.getApplicationInfo (),
            env.getSessionInfo (), env);
    } // processJavaScriptCode


    /**************************************************************************
     * Create a html page which is just processing a piece of JavaScript code
     * on the client. <BR/>
     *
     * @param   code    The complete code to be processed.
     * @param   app     The global application info.
     * @param   sess    The actual session info.
     * @param   env     The actual environment.
     */
    public static void processJavaScriptCode (String code, ApplicationInfo app,
                                              SessionInfo sess, Environment env)
    {
        Page page = new Page (MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
            AppMessages.ML_MSG_MESSAGEHEADER, env), false);
        ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);

        // set the document's base:
        IOHelpers.setBase (page, app, sess, env);

        page.body.onUnload = null;
        page.body.addElement (new BlankElement ());
        page.body.addElement (script);

        // check if a code shall be added:
        if (code != null)               // code defined?
        {
            // add the code to the script:
            script.addScript (code);
        } // if code

        try
        {
            page.build (env);
        } // try
        catch (BuildException e)
        {
            env.write (e.getMsg ());
        } // catch
    } // processJavaScriptCode


    /**************************************************************************
     * Show debugging text. <BR/>
     *
     * @param   text    Text to be printed out.
     * @param   env     Environment for getting input and generating output.
     */
    public static void debug (StringBuffer text, Environment env)
    {
        // call common method:
        IOHelpers.debug (text.toString (), env);
    } // debug


    /**************************************************************************
     * Show debugging text. <BR/>
     *
     * @param   text    Text to be printed out.
     * @param   sess        The actual session info.
     * @param   env         Environment for getting input and generating output.
     *
     * @deprecated  20090801 Use {@link #debug(StringBuffer, Environment)}
     *              instead.
     */
    public static void debug (StringBuffer text, SessionInfo sess, Environment env)
    {
        // call common method:
        IOHelpers.debug (text.toString (), sess, env);
    } // debug


    /**************************************************************************
     * Show debugging text. <BR/>
     *
     * @param   text    Text to be printed out.
     * @param   env     Environment for getting input and generating output.
     */
    public static void debug (String text, Environment env)
    {
        // check if debug is possible:
        if (env == null)
        {
            return;
        } // if

        SessionInfo sess = env.getSessionInfo ();
        UserInfo userInfo = null;
        if (sess == null || (userInfo = (UserInfo) sess.userInfo) == null ||
            userInfo.getUser () == null)
        {
            return;
        } // if

        // return for release
        // last line will be automatically substituted with "return;" for
        // release version
        if (userInfo.getUser ().username.equalsIgnoreCase (IOConstants.USERNAME_DEBUG))
        {
            // showMessage (this.getClass ().getName () + ":" + IE302.TAG_NEWLINE + text, AppMessages.MST_DEBUG);
            env.write (text + "<P>");
        } // if
    } // debug


    /**************************************************************************
     * Show debugging text. <BR/>
     *
     * @param   text    Text to be printed out.
     * @param   sess        The actual session info.
     * @param   env         Environment for getting input and generating output.
     *
     * @deprecated  20090801 Use {@link #debug(String, Environment)} instead.
     */
    public static void debug (String text, SessionInfo sess, Environment env)
    {
        UserInfo userInfo = null;

        // check if debug is possible:
        if (sess == null || sess.userInfo == null ||
            ((UserInfo) sess.userInfo).getUser () == null || env == null)
        {
            return;
        } // if

        userInfo = (UserInfo) sess.userInfo;

        // return for release
        // last line will be automatically substituted with "return;" for
        // release version
        if (userInfo.getUser ().username.equalsIgnoreCase (IOConstants.USERNAME_DEBUG))
        {
            // showMessage (this.getClass ().getName () + ":" + IE302.TAG_NEWLINE + text, AppMessages.MST_DEBUG);
            env.write (text + "<P>");
        } // if
    } // debug


    /**************************************************************************
     * Show the call to a stored procedure. <BR/>
     *
     * @param   obj         The calling object (used for getting the class name).
     * @param   sp          The stored procedure with its parameters.
     * @param   env         Environment for getting input and generating output.
     */
    public static void showProcCall (Object obj, StoredProcedure sp,
        Environment env)
    {
        IOHelpers.showProcCall (obj, sp, env.getSessionInfo (), env);
    } // showProcCall


    /**************************************************************************
     * Show the call to a stored procedure. <BR/>
     *
     * @param   obj         The calling object (used for getting the class name).
     * @param   sp          The stored procedure with its parameters.
     * @param   sess        The actual session info.
     * @param   env         Environment for getting input and generating output.
     */
    public static void showProcCall (Object obj, StoredProcedure sp,
        SessionInfo sess, Environment env)
    {
        UserInfo userInfo = null;

        // check all object which are necessary for debugging, to
        // to prefent NullPointerException when debugging:
        if (sess == null || sess.userInfo == null ||
            sess.userInfo == null ||
            ((UserInfo) sess.userInfo).getUser () == null ||
            env == null)
        {
            return;
        } // if

        userInfo = (UserInfo) sess.userInfo;

//trace ("KR show proc call for user " + this.sess.userInfo.user.username + "...");
        if (!userInfo.getUser ().username.equalsIgnoreCase (IOConstants.USERNAME_DEBUG)
//            &&  !userInfo.getUser ().username.equalsIgnoreCase (IOConstants.USERNAME_ADMINISTRATOR)
//          &&  !userInfo.getUser ().username.equalsIgnoreCase ("Admin")
//          &&   userInfo.getUser ().username.length () > 0
            )
        {
            return;                     // terminate the method
        } // if


        StringBuffer comment = new StringBuffer (); // preliminary comments
        StringBuffer declarations = new StringBuffer (); // necessary declarations
        StringBuffer call = new StringBuffer (); // call to be printed
        StringBuffer eval = new StringBuffer (); // evaluations
        StringBuffer procCall;          // the complete procedure call
        StringBuffer className = new StringBuffer (obj.getClass ().getName ());

        // get the call:
        procCall = SQLHelpers.getProcCall (sp, comment, declarations, call, eval);

//trace (comment);

        // show the declarations and the call:
        env.write ("<DIV ALIGN=\"LEFT\">" +
                   comment + IE302.TAG_NEWLINE + "\n" + IE302.TAG_NEWLINE + "\n" +
                   className + ":" + IE302.TAG_NEWLINE +
                   IE302.TAG_PRE +
                   declarations + "\n" +
                   call + "\n" +
                   eval + "\n" +
                   IE302.TAG_PREEND);
        env.write (procCall.toString ());
        env.write ("</DIV>");

//if ( 1 == 1) return;
//trace (className + ":");
//trace (declarations);
//trace (call);
//trace (eval);
    } // showProcCall


    /**************************************************************************
     * Create the content of one text area for representing it to the user. <BR/>
     * The "\n" characters are replaced by new lines.
     * "\n\n" is replaced by a paragraph tag.
     *
     * @param   text        Text to be presented to the user.
     *
     * @return  The constructed element.
     */
    public static Element getTextField (String text)
    {
        GroupElement group = new GroupElement ();
        StringTokenizer st = new StringTokenizer (text, "\n", true);
        String actLine;
        TextElement elem;

        while (st.hasMoreTokens ())
        {
            actLine = st.nextToken ();

            if (actLine.equalsIgnoreCase ("\n")) // new line?
            {
                // add new line to field:
                group.addElement (new NewLineElement ());
            } // if
            else                        // within a line
            {
                elem = new TextElement (actLine); // create text
                group.addElement (elem); // add text to field
            } // else within a line
        } // while
        return group;                   // return the new text field
    } // getTextField


    /**************************************************************************
     * Create the content of one text area containing HTML text for representing
     * it to the user. <BR/>
     *
     * @param   text        Text to be presented to the user.
     *
     * @return  The constructed element.
     */
    public static Element getHtmlTextField (String text)
    {
        // just return the text as is:
        return new TextElement (text);
    } // getHtmlTextField


    /**************************************************************************
     * Show a server side include (SSI) file. <BR/>
     * The filename  is passed as an parameter and will be redirected to
     * the client. <BR/>
     *
     * @param   file    the filename of the message (typically an html file)
     * @param   env     the acutal environment
     */
    public static void showSSIFile (String file,
                                    Environment env)
    {
        String path;

        path = ((UserInfo) env.getUserInfo ()).homepagePath;
        env.redirect (path + BOPathConstants.PATH_INCLUDE + file);
    } // showSSIFile
    
    
    /**************************************************************************
     * Show a server side include (SSI) file. <BR/>
     * The filename is passed as an parameter and will be redirected to
     * the client. <BR/>
     *
     * @param   file            the filename of the message
     *                          (typically an html file)
     * @param   replacements    a map containing the replacements perform
     *                          within the include file
     * @param   env             the acutal environment
     */
    public static void showSSIFile (String file, Map<String, String> replacements,
                                    Environment env)
    {
        try
        {
            String page = IOHelpers.getSSIFile (AppConstants.SSI_NOLAYOUTDEFINED,
                    env,
                    (IConfiguration)env.getApplicationInfo ().configuration);
            
            Iterator<String> it = replacements.keySet ().iterator ();
            while (it.hasNext ())
            {
                String tag = it.next ();
                String value = replacements.get (tag);

                page = StringHelpers.replace (page, tag, value);
            } // while

            env.write (page);
        } // try
        catch (SsiFileNotFoundException e)
        {
            IOHelpers.showMessage (e, env);
        } // catch
    } // showSSIFile


    /**************************************************************************
     * Show a server side include (SSI) file. <BR/>
     * The filename  is passed as an parameter and will be redirected to
     * the client. <BR/>
     *
     * @param   file    the filename of the message (typically an html file)
     * @param   sess    the actual SessionInfo object
     * @param   env     the acutal environment
     *
     * @deprecated  20090801 Use {@link #showSSIFile(String, Environment)} instead.
     */
    public static void showSSIFile (String file,
                                    SessionInfo sess,
                                    Environment env)
    {
        String path;

        path = ((UserInfo) sess.userInfo).homepagePath;
        env.redirect (path + BOPathConstants.PATH_INCLUDE + file);
    } // showSSIFile


    /**************************************************************************
     * Show a message via an server side include (SSI). <BR/>
     * The filename for the message is passed as an parameter and will be
     * loaded directly and output to the client. <BR/>
     *
     * @param messagefile the filename of the message (typically an html file)
     * @param env         the actual environment
     * @param conf        the actual configuration
     *
     * @return  The result string from the SSI file which can be inserted into
     *          the output.
     *
     * @throws  SsiFileNotFoundException
     *          The server-side-include file was not found.
     */
    public static String getSSIFile (String messagefile,
                                     Environment env,
                                     IConfiguration conf)
        throws SsiFileNotFoundException
    {
        try                             // try to get the URL-object
                                        // referenced
        {
            // directory and file:
            URL someURL = new URL (conf.getSsiurl () + messagefile);
            URLConnection urlCon = someURL.openConnection ();
            ByteArrayOutputStream tempBuffer = new ByteArrayOutputStream ();
            InputStream instream = urlCon.getInputStream ();

            if (urlCon.getLastModified () == 0)
                                        // if ssi file is not found
                                        // LastModified is set to 0
            {
                throw new SsiFileNotFoundException (MultilingualTextProvider.getMessage (
                    UtilExceptions.EXC_BUNDLE, UtilExceptions.ML_E_SSIURLNOTFOUND, 
                    new String[] {conf.getSsiurl () + messagefile}, env));
            } // if ssi file is not found

            // the ssi file is found
            int ch;
            while ((ch = instream.read ()) >= 0)
            {
                tempBuffer.write (ch);
            } // while
            return  tempBuffer.toString ();
        } // try
        catch (IOException e)
        {
            throw new SsiFileNotFoundException (MultilingualTextProvider.getMessage (
                UtilExceptions.EXC_BUNDLE, UtilExceptions.ML_E_SSIURLNOTFOUND, 
                new String[] {conf.getSsiurl () + messagefile}, env), e);
        } // catch
    } // getSSIFile


    /**************************************************************************
     * Show a message via an server side include (SSI). <BR/>
     * The filename for the message is passed as an parameter and will be
     * loaded directly and output to the client. <BR/>
     *
     * @param messagefile the filename of the message (typically an html file)
     * @param sess        the actual SessionInfo object
     * @param env         the actual environment
     * @param conf        the actual configuration
     *
     * @return  The result string from the SSI file which can be inserted into
     *          the output.
     *
     * @throws  SsiFileNotFoundException
     *          The server-side-include file was not found.
     *
     * @deprecated  20090801 Use {@link #getSSIFile(String, Environment, IConfiguration)}
     *              instead.
     */
    public static String getSSIFile (String messagefile,
                                     SessionInfo sess,
                                     Environment env,
                                     IConfiguration conf)
        throws SsiFileNotFoundException
    {
        // call common method:
        return IOHelpers.getSSIFile (messagefile, env, conf);
    } // getSSIFile


    /**************************************************************************
     * Returns the base url where the several arguments can be appended. <BR/>
     * This URL contains a random value to ensure that the browser always
     * reloads the page. <BR/>
     * Arguments can be appended with the following code:
     * <CODE><PRE>
     * url = getBaseUrl () +
     *       HttpArguments.createArg (&lt;argument name&gt;, &lt;value&gt;) +
     *       HttpArguments.createArg (&lt;argument name&gt;, &lt;value&gt;) +
     *       ...
     *       HttpArguments.createArg (&lt;argument name&gt;, &lt;value&gt;);
     * </PRE></CODE>. <BR/>
     *
     * @param   operation   Operation to be performed.
     *
     * @return  The base url.
     */
    public static String getBaseUrl (String operation)
    {
        String value = "";              // the calculated random value
        double dValue = Math.random () * 26; // the random number
        value += (char) ((dValue % 26) + 'a'); // the first character
        dValue *= 26;                   // compute next position double value
        value += (char) ((dValue % 26) + 'A'); // the next character
        dValue *= 26;                   // compute next position double value
        value += (char) ((dValue % 26) + 'A'); // the next character
        dValue *= 26;                   // compute next position double value
        value += (char) ((dValue % 26) + 'A'); // the next character

        // compute the complete URL and return it:
        return operation + HttpArguments.ARG_BEGIN + BOArguments.ARG_RANDOM + HttpArguments.ARG_ASSIGN + value;
    } // getBaseUrl


    /**************************************************************************
     * Returns the base url where the several arguments can be appended. <BR/>
     * This URL contains a random value to ensure that the browser always
     * reloads the page. <BR/>
     * Arguments can be appended with the following code:
     * <CODE><PRE>
     * url = getBaseUrl () +
     *       HttpArguments.createArg (&lt;argument name&gt;, &lt;value&gt;) +
     *       HttpArguments.createArg (&lt;argument name&gt;, &lt;value&gt;) +
     *       ...
     *       HttpArguments.createArg (&lt;argument name&gt;, &lt;value&gt;);
     * </PRE></CODE>. <BR/>
     *
     * @param   env     The actual environment.
     *
     * @return  The base URL with a random parameter to prevent caching.
     */
    public static String getBaseUrl (Environment env)
    {
        String temp = "";

        if (env != null)                // the environment is set?
        {
            // get the session info:
            SessionInfo sess = env.getSessionInfo ();
            boolean sslRequired = Ssl.isSslRequired2 (sess);

            if (sslRequired)            // SSL is necessary?
            {
                temp = Ssl.getSecureUrl (IOHelpers.getBaseUrl (env.getServerVariable (IOConstants.SV_URL)),
                                         sess);
            } // if SSL is necessary
            else                        // SSL is not necessary
            {
                temp = Ssl.getNonSecureUrl (IOHelpers.getBaseUrl (env.getServerVariable (IOConstants.SV_URL)),
                                            sess);
            } // else SSL is not necessary
        } // if the environment is set
        else                            // the environment is not set
        {
            temp = "";
        } // else the environment is not set

        return  temp;
    } // getBaseUrl


    /**************************************************************************
     * Returns the base url where the several arguments can be appended. <BR/>
     * This URL contains a random value to ensure that the browser always
     * reloads the page. <BR/>
     * Arguments can be appended with the following code:
     * <CODE><PRE>
     * url = getBaseUrl () +
     *       HttpArguments.createArg (&lt;argument name&gt;, &lt;value&gt;) +
     *       HttpArguments.createArg (&lt;argument name&gt;, &lt;value&gt;) +
     *       ...
     *       HttpArguments.createArg (&lt;argument name&gt;, &lt;value&gt;);
     * </PRE></CODE>. <BR/>
     *
     * @param   sess    The actual session info.
     * @param   env     The actual environment.
     *
     * @return  The base URL with a random parameter to prevent caching.
     *
     * @deprecated  20090801 Use {@link #getBaseUrl(Environment)} instead.
     */
    public static String getBaseUrl (SessionInfo sess, Environment env)
    {
        // call commmon method:
        return IOHelpers.getBaseUrl (env);
    } // getBaseUrl


    /**************************************************************************
     * Returns the base url used for GET requests where the several arguments
     * can be appended. <BR/>
     * This URL contains a random value to ensure that the browser always
     * reloads the page. <BR/>
     * Arguments can be appended with the following code:
     * <CODE><PRE>
     * url = getBaseUrlGet () +
     *       HttpArguments.createArg (&lt;argument name&gt;, &lt;value&gt;) +
     *       HttpArguments.createArg (&lt;argument name&gt;, &lt;value&gt;) +
     *       ...
     *       HttpArguments.createArg (&lt;argument name&gt;, &lt;value&gt;);
     * </PRE></CODE>. <BR/>
     *
     * @param   env     The actual environment.
     *
     * @return  The base URL with a random parameter to prevent caching.
     */
    public static String getBaseUrlGet (Environment env)
    {
        String temp = "";

        if (env != null)                // the environment is set?
        {
            // get the session info:
            SessionInfo sess = env.getSessionInfo ();

            // check if SSL is required and also available
            boolean sslRequired = Ssl.isSslRequired2 (sess);

            if (sslRequired)            // SSL is necessary
            {
                // get the secure URL
                temp = Ssl.getSecureUrl (IOHelpers.getBaseUrl
                    (env.getServerVariable (IOConstants.SV_URL)),
                    sess);
            } // if SSL is necessary
            else                        // SSL is not necessary
            {
                // get the non-secure URL
                temp = Ssl.getNonSecureUrl (IOHelpers.getBaseUrl
                    (env.getServerVariable (IOConstants.SV_URL)),
                    sess);
            } // else SSL is not necessary
        } // if the environment is set
        else                            // the environment is not set
        {
            temp = "";
        } // else the environment is not set

        return  temp;
    } // getBaseUrlGet


    /**************************************************************************
     * Returns the base url used for GET requests where the several arguments
     * can be appended. <BR/>
     * This URL contains a random value to ensure that the browser always
     * reloads the page. <BR/>
     * Arguments can be appended with the following code:
     * <CODE><PRE>
     * url = getBaseUrlGet () +
     *       HttpArguments.createArg (&lt;argument name&gt;, &lt;value&gt;) +
     *       HttpArguments.createArg (&lt;argument name&gt;, &lt;value&gt;) +
     *       ...
     *       HttpArguments.createArg (&lt;argument name&gt;, &lt;value&gt;);
     * </PRE></CODE>. <BR/>
     *
     * @param   sess    The actual session info.
     * @param   env     The actual environment.
     *
     * @return  The base URL with a random parameter to prevent caching.
     *
     * @deprecated  20090801 Use {@link #getBaseUrlGet(Environment)} instead.
     */
    public static String getBaseUrlGet (SessionInfo sess, Environment env)
    {
        // call common method:
        return IOHelpers.getBaseUrlGet (env);
    } // getBaseUrlGet


    /**************************************************************************
     * Returns the base url used for GET requests out of JavaScript where the
     * several arguments can be appended. <BR/>
     * This URL contains a random value to ensure that the browser always
     * reloads the page. <BR/>
     * Arguments can be appended with the following code:
     * <CODE><PRE>
     * url = getBaseUrlJavaScript () + " \"" +
     *       AppArguments.ARG_SEP + &lt;argument name AppArguments.ARG_xxx&gt; + AppArguments.ARG_ASSIGN + &lt;value&gt; +
     *       AppArguments.ARG_SEP + &lt;argument name AppArguments.ARG_xxx&gt; + AppArguments.ARG_ASSIGN + &lt;value&gt; +
     *       ...
     *       AppArguments.ARG_SEP + &lt;argument name AppArguments.ARG_xxx&gt; + AppArguments.ARG_ASSIGN + &lt;value&gt; + "\"";
     * </PRE></CODE>. <BR/>
     *
     * @return  The base url.
     */
    public static String getBaseUrlJavaScript ()
    {
        // compute the base URL and return it:
        return "top.getBaseUrl ()";
    } // getBaseUrlJavaScript


    /**************************************************************************
     * Returns the javascript call used for showing an object within an url.
     * <BR/>
     *
     * @param   oidStr  The string representation of the oid of the object to
     *                  be shown.
     *
     * @return  The javascript call.
     */
    public static String getShowObjectJavaScript (String oidStr)
    {
        // compute the base URL and return it:
        return "top.showObject ('" + oidStr + "');";
    } // getShowObjectJavaScript


    /**************************************************************************
     * Returns the javascript call used for showing an object within an url.
     * <BR/>
     *
     * @param   oidStr  The string representation of the oid of the object to
     *                  be shown.
     *
     * @return  The javascript call.
     */
    public static String getShowObjectJavaScriptUrl (String oidStr)
    {
        // compute the base URL and return it:
        return IOConstants.URL_JAVASCRIPT +
               IOHelpers.getShowObjectJavaScript (oidStr);
    } // getShowObjectJavaScript


    /**************************************************************************
     * Returns the javascript call used for loading a function. <BR/>
     *
     * @param   fct     Function to be called.
     *
     * @return  The javascript call.
     */
    public static String getLoadJavaScript (int fct)
    {
        // compute the base URL and return it:
        return IOHelpers.JS_LOAD + fct + ");";
    } // getLoadJavaScript


    /**************************************************************************
     * Returns the javascript call used for loading a function. <BR/>
     *
     * @param   fct         Function to be called.
     * @param   targetFrame The frame in which to open the url.
     *
     * @return  The javascript call.
     */
    public static String getLoadJavaScript (int fct, String targetFrame)
    {
        // compute the base URL and return it:
        return IOHelpers.JS_LOAD + fct + ", " + targetFrame + ");";
    } // getLoadJavaScript


    /**************************************************************************
     * Returns the javascript call used for loading a function in a container.
     * <BR/>
     *
     * @param   fct     Function to be called.
     *
     * @return  The javascript call.
     */
    public static String getLoadContJavaScript (int fct)
    {
        // compute the base URL and return it:
        return "top.loadCont (" + fct + ");";
    } // getLoadContJavaScript


    /**************************************************************************
     * Encodes a String into a URL. <BR/>
     *
     * @param   str     String to encode
     *
     * @return  The string urlencoded
     */
    public static String urlEncode (final String str)
    {
        String encodedStr = null;

        try
        {
            encodedStr = URLEncoder.encode (str, IOHelpers.DEFAULT_URL_CHARACTER_ENCODING);
            encodedStr = StringHelpers.replace (encodedStr, "+",
                IOHelpers.URLCH_SPACE);
        } // try
        catch (UnsupportedEncodingException e)
        {
            IOHelpers.printError ("error during url encoding", e, true);
        } // catch

        return encodedStr;

//          return URLEncoder.encode (str);
//        return cxt.response.encodeUrl (str) ;
    } // urlEncode


    /**************************************************************************
     * Decodes a String from an URL. <BR/>
     *
     * @param   str     String to decode.
     *
     * @return  The string urldecoded.
     */
    public static String urlDecode (final String str)
    {
        String decodedStr = null;

        try
        {
            decodedStr = StringHelpers.replace (decodedStr,
                IOHelpers.URLCH_SPACE, "+");
            decodedStr = URLDecoder.decode (str, IOHelpers.DEFAULT_URL_CHARACTER_ENCODING);
        } // try
        catch (UnsupportedEncodingException e)
        {
            IOHelpers.printError ("error during url decoding", e, true);
        } // catch

        return decodedStr;

//          return URLDecoder.decode (str);
//        return cxt.response.decodeUrl (str) ;
    } // urlDecode

    
    /**************************************************************************
     * Produces HTML output to show a structure with buttons to hide and show it.
     * It displays a link to a structure object if given. <BR>
     *
     * @param env     	The current environment.
     * @param structure	the dom string
     * @param hideToken	the token for the hide button
     * @param showToken the token for the show button
     * @param linkToken the token for the link 
     * @param structureOid 	the oid of the structure object. 
     * 						If <code>null</code> the link is not shown
     */
    public static void showStructure (Environment env, 
    								  String structure,
    								  String hideToken,
    								  String showToken,
    								  String linkToken,
    								  OID structureOid)
    {    	
    	
    	String structureOidStr = "";
    		
    	// any queryCreator set?
    	if (structureOid != null)
		{
    		structureOidStr = structureOid.toString();
		} // if (structureOid != null)
    	
        env.write ("<DIV ALIGN=\"LEFT\" CLASS=\"devInfo\" STYLE=\"font-size: smaller; \">");

        // show the structure content only if provided
        if (structure != null)
        {
            env.write ("[<A ID=\"dom_show" + structureOidStr + "\" " +
            		" STYLE=\"display: inline;\" "  +
            		" ONCLICK=\"document.getElementById ('dom" + structureOidStr + 
            				"').style.display='block';" +
            			"document.getElementById ('dom_hide" + structureOidStr + 
            				"').style.display='inline';" +
            			"document.getElementById ('dom_show" + structureOidStr + 
            				"').style.display='none';\">" +
            		"+ " + showToken + "</A>");
            env.write ("<A ID=\"dom_hide" + structureOidStr + "\" " +
            		" STYLE=\"display: none;\" "  +
            		" ONCLICK=\"document.getElementById ('dom" + structureOidStr + 
            				"').style.display='none';" +
            			"document.getElementById ('dom_hide" + structureOidStr + 
            				"').style.display='none';" +
            			"document.getElementById ('dom_show" + structureOidStr + 
            				"').style.display='inline';\">" +
        		"- " + hideToken + "</A>]" + IE302.HCH_NBSP + IE302.HCH_NBSP + IE302.HCH_NBSP);            
        } // if

        // display a link to the structure object?
        if (structureOid != null)
        {
            env.write ("[<A HREF=\"" +  
            		getShowObjectJavaScriptUrl (structureOidStr) + 
            		"\">" + linkToken + 
            		" ...</A>]");         	
        } // if (structureOid != null)

        env.write ("<DIV ID=\"dom" + 
            		structureOidStr + "\" STYLE=\"display: none;font-size: medium;\">");
        env.write (IE302.TAG_PRE + structure + IE302.TAG_PREEND);
        env.write ("</DIV>");
        env.write ("</DIV>");   
    } // showDOMInfo    
       
    
} // class IOHelpers
