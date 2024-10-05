/*
 * Class: WebdavLinkServlet.java
 */

// package:
package ibs.obj.webdav;

// imports:
import ibs.bo.BOPathConstants;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.di.DIConstants;
import ibs.io.session.ServletSessionInfo;
import ibs.service.user.User;
import ibs.tech.html.IE302;
import ibs.util.Helpers;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/******************************************************************************
 * This is the WebdavLinkServlet. <BR/>
 *
 * @version     $Id: WebdavLinkServlet.java,v 1.17 2009/12/15 09:44:25 btatzmann Exp $
 *
 * @author      Martin Centner (MC), 20020822
 ******************************************************************************
 */
public class WebdavLinkServlet extends HttpServlet
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: WebdavLinkServlet.java,v 1.17 2009/12/15 09:44:25 btatzmann Exp $";


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
    static final long serialVersionUID = 7653407210395974667L;


    /**
     * Debug option. <BR/>
     */
    protected int debug = 0;

    /**
     * Webdav Base address. <BR/>
     */
    protected String webdavBase = "";

    /**
     * Maximal age of the webdav cookie. <BR/>
     */
    protected int cookieMaxAge = 600;

    /**
     * Name of the webdav cookie. <BR/>
     */
    protected String cookieName = "WEBDAVAUTH";

    /**
     * The MessageDigest object for digesting user credentials (passwords).
     */
    protected MessageDigest md = null;

    /**
     * Digest algorithm used in storing passwords in a non-plaintext format.
     * Valid values are those accepted for the algorithm name by the
     * MessageDigest class, or <code>null</code> if no digesting should
     * be performed.
     */
    protected String digest = null;

    /**
     * Turn caching on and off. <BR/>
     * Valid values are <CODE>true</CODE> to turn caching on and
     * <CODE>false</CODE> to turn caching off.
     * By default caching is turned on.
     */
    protected boolean p_isCaching = true;



    /**************************************************************************
     * Initialize this servlet.
     *
     * @throws  ServletException
     *          ???
     *
     * @see javax.servlet.GenericServlet#init()
     */
    public void init () throws ServletException
    {
        // set configuration parameters
        String value = null;
        try
        {
            ServletConfig config = this.getServletConfig ();
            value = config.getInitParameter ("debug");
            if (value != null)
            {
                this.debug = Integer.parseInt (value);
            } // if

            // IBS-127 webDaveBase is now a relative path
            this.webdavBase = config.getInitParameter ("webdavBase");

            // remove leading path separators
            while (this.webdavBase.indexOf ("/") == 0)
            {
                this.webdavBase =
                    this.webdavBase.substring (1, this.webdavBase.length ());
            } // while

            value = config.getInitParameter ("cookieMaxAge");
            if (value != null)
            {
                this.cookieMaxAge = Integer.parseInt (value);
            } // if
            this.cookieName = config.getInitParameter ("cookieName");
            this.digest = config.getInitParameter ("digest");
            value = config.getInitParameter ("caching");
            if (value != null)
            {
                this.p_isCaching = value.equalsIgnoreCase ("true");
            } // if
        } // try
        catch (Throwable t)
        {
            // should not occur
        } // catch

        // Create a MessageDigest instance for credentials, if desired
        if (this.digest != null)
        {
            try
            {
                this.md = MessageDigest.getInstance (this.digest);
            } // try
            catch (NoSuchAlgorithmException e)
            {
                throw new ServletException (
                    "No such Algorithm : " + this.digest, e);
            } // catch NoSuchAlgorithmException
        } // if
    } // init


    /**************************************************************************
     * Standard servlet destructor. <BR/>
     *
     * @see javax.servlet.Servlet#destroy()
     */
    public void destroy ()
    {
        // Clean up allocated resources
        this.md = null;
    } // destroy


    /**************************************************************************
     * Calculate digest. <BR/>
     *
     * @param   credentials ???
     *
     * @return  ???
     */
    protected String digest (String credentials)
    {
        // If no MessageDigest instance is specified, return unchanged
        if (!this.hasMessageDigest ())
        {
            return credentials;
        } // if

        // Digest the user credentials and return as hexadecimal
        synchronized (this)
        {
            try
            {
                this.md.reset ();
                this.md.update (credentials.getBytes ());
                return WebdavLinkServlet.convert (this.md.digest ());
            } // try
            catch (Exception e)
            {
                this.log ("realmBase.digest", e);
                return credentials;
            } // catch Exception
        } // synchronised
    } // digest


    /**************************************************************************
     * Convert a byte array into a printable format containing a
     * String of hexadecimal digit characters (two per byte).
     *
     * @param   bytes   Byte array representation.
     *
     * @return  The converted string.
     */
    public static String convert (byte[] bytes)
    {
        return WebdavLinkServlet.convert (bytes, 0, bytes.length);
    } // convert


    /**************************************************************************
     * Convert a byte array into a printable format containing a
     * String of hexadecimal digit characters (two per byte).
     *
     * @param   bytes   Byte array representation.
     * @param   offset  Start postion in the bytearray.
     * @param   length  Length of the bytearray.
     *
     * @return  The converted string.
     */
    public static String convert (byte[] bytes, int offset, int length)
    {
        StringBuffer sb = new StringBuffer (bytes.length * 2);
        for (int i = 0; i < length; i++)
        {
            sb.append (WebdavLinkServlet.convertDigit (bytes[offset + i] >> 4));
            sb.append (WebdavLinkServlet.convertDigit (bytes[offset + i] & 0x0f));
        } // for (int i = 0; i < length; i++)
        return sb.toString ().toUpperCase ();
    } // convert


    /**************************************************************************
     * Convert the specified value (0 .. 15) to the corresponding
     * hexadecimal digit. <BR/>
     *
     * @param   value   Value to be converted.
     *
     * @return  The converted digit.
     */
    private static char convertDigit (int value)
    {
        int val = value & 0x0f;
        if (val >= 10)
        {
            return (char) (val - 10 + 'a');
        } // if

        return (char) (val + '0');
    } // convertDigit


    /**************************************************************************
     * Check if a message digest has been set. <BR/>
     *
     * @return  <CODE>true</CODE> if a message digest has been set,
     *          <CODE>false</CODE> otherwise.
     */
    protected boolean hasMessageDigest ()
    {
        return !(this.md == null);
    } // hasMessageDigest


    /**************************************************************************
     * Implement the standard doGet servlet method. <BR/>
     *
     * @param   req     ???
     * @param   res     ???
     *
     * @throws  ServletException
     *          ???
     * @throws  IOException
     *          ???
     *
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doGet (HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException
    {
        // Set the request encoding
        // see also http://java.sun.com/j2ee/1.4/docs/tutorial/doc/WebI18N5.html
        req.setCharacterEncoding (DIConstants.CHARACTER_ENCODING);
        
        String sessionUserOid = "";
        String sessionUserPassword = "";
        OID uOid = null;
        ServletSessionInfo sessionInfo = (ServletSessionInfo) req.getSession ().getAttribute ("Session");

        // check if session is already authenticated
        try
        {
            User user = sessionInfo.userInfo.getUser ();
            uOid = user.oid;
            sessionUserPassword = user.password;
            sessionUserOid = user.oid.toString ();
        } // try
        catch (NullPointerException e)
        {
            if (this.debug < 1)
            {
                res.sendError (HttpServletResponse.SC_FORBIDDEN, "Forbidden");
                return;
            } // if
        } // catch NullPointerException

        sessionUserOid = req.getParameter ("uid");
        try
        {
            uOid = new OID (sessionUserOid);
        } // try
        catch (IncorrectOidException e)
        {
            throw new ServletException ();
        } // catch IncorrectOidException

        String oid = req.getParameter ("oid");
        String fileName = req.getParameter ("file");
        String readOnly = req.getParameter ("readOnly");

        if ((oid == null) || (fileName == null))
        {
            res.sendError (HttpServletResponse.SC_NOT_FOUND, "Not Found");
            return;
        } // if

        String sessionId = req.getSession ().getId ();

        if (sessionId != null)
        {
            Date validUntil = new Date ();
            validUntil.setTime (validUntil.getTime () + this.cookieMaxAge * 1000);

            SimpleDateFormat df = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ssz");

            String secret = uOid + "|" + sessionUserPassword + "|" + df.format (validUntil);
            String mac = this.digest (secret);

            if (this.debug > 2)
            {
                System.out.println ("-Secret: " + secret + " ==> Mac: " + mac);
            } // if

            String value = uOid + "|" + df.format (validUntil) + "|" + mac;

            Cookie cookie = new Cookie (this.cookieName, value);
            cookie.setMaxAge (this.cookieMaxAge);
            cookie.setPath ("/");
            res.addCookie (cookie);

            if (this.debug > 1)
            {
                System.out.println ("Cookie (" + value + ") added!");
            } // if
        } // if

        // set the content type and encoding:
        res.setContentType("text/html; charset=" + DIConstants.CHARACTER_ENCODING);
        // check if caching is enabled:
        if (!this.p_isCaching)          // no caching?
        {
            res.setHeader ("Cache-Control", "no-cache");
            res.setHeader ("Pragma", "no-cache");
            res.setDateHeader ("Expires", 0);
        } // if no caching

        PrintWriter out = res.getWriter ();

        out.println ("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
        out.println ("<html><head>");
        out.println ("<title>m2</title>");
        // check if caching is enabled:
        if (!this.p_isCaching)          // no caching?
        {
            out.println ("<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=" + DIConstants.CHARACTER_ENCODING + "\"/>");
            out.println ("<META HTTP-EQUIV=\"Expires\" CONTENT=\"Tue, 01 Jan 1980 1:00:00 GMT\">");
            out.println ("<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
        } // if no caching
        out.println ("</head>");
        out.println ("<STYLE>BODY { FONT-FAMILY: Verdana, Tahoma, Arial, sans-serif; BACKGROUND: #EEEEEE; }</STYLE>");
        out.println ("<body onLoad=\"setTimeout(myOnLoad,1500)\">");
        out.println ("<STYLE>.httpFolder {behavior: url('#default#httpFolder');}</STYLE>");
        out.println ("<DIV ID = \"oDAV\" CLASS = \"httpFolder\"/>");
        out.println ("<table align=\"center\"><td align=\"center\">");
        out.println (fileName + " wird ge&ouml;ffnet ..." + IE302.TAG_NEWLINE + IE302.HCH_NBSP + "<br/>");
/* BB 20060410: deactivated because never needed
        out.println ("<img src=\"" + BOPathConstants.PATH_SCRIPTS +
                "progress.gif\" alt=\"\" width=\"243\" height=\"14\" border=\"1\" align=\"middle\"/>" + IE302.TAG_NEWLINE + IE302.HCH_NBSP + "<br/>&nbsp<br/>");
*/
        out.println ("<input type=\"button\" value=\"Fenster schließen\"" +
                " onclick=\"top.window.close();\"/>");
        out.println ("</td></table>");

        String mimeType = "";
        if (fileName.toLowerCase ().endsWith (".doc"))
        {
            mimeType = "application/msword";
        } // if
        else if (fileName.toLowerCase ().endsWith (".xls"))
        {
            mimeType = "application/vnd.ms-excel";
        } // else if
        else if (fileName.toLowerCase ().endsWith (".pdf"))
        {
            mimeType = "application/pdf";
        } // else if
        else
        {
            mimeType = "webfolder";
        } // else

        out.println ("<script language=\"JavaScript\" src=\"" +
            BOPathConstants.PATH_SCRIPTS + "office.js\"></script>");
        out.println ("<script language=\"VBScript\" src=\"" +
            BOPathConstants.PATH_SCRIPTS + "office.vbs\"></script>");

        // IBS-127 webDaveBase is now a relative path
        String webDavUrl = Helpers.createUrlString (
            req.getServerName () + "/" + this.webdavBase);

        out.print ("<script language=\"JavaScript\"> function myOnLoad () {" +
            " openDocView (\"" +
                mimeType + "\", \"" +
                webDavUrl + "/" + sessionUserOid + "/" + oid + "/" +
                fileName + "\"");
        // Check if the readOnly flag should be set:
        if (readOnly != null && readOnly.equalsIgnoreCase ("true"))
        {
            out.print (", true");
        } // if
        else
        {
            out.print (", false");
        } // else
        out.println ("); } </script>");
        out.println ("</body></html>");
    } // doGet

} // WebdavLinkServlet
