/*
 * Class: FileAccessServlet.java
 */

// package:
package ibs.io.servlet;

// imports:
import ibs.tech.http.Mimetypes;
import ibs.util.UtilConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/******************************************************************************
 * This is the FileAccessServlet. <BR/>
 *
 * @version     $Id: FileAccessServlet.java,v 1.12 2009/12/14 14:44:50 btatzmann Exp $
 *
 * @author      Martin Centner (MC), 20020822
 ******************************************************************************
 */
public class FileAccessServlet extends BaseServlet
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: FileAccessServlet.java,v 1.12 2009/12/14 14:44:50 btatzmann Exp $";


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
    static final long serialVersionUID = -7952845699716124731L;


    /**
     * Debug option. <BR/>
     */
    protected boolean p_isDebug = false;

    /**
     * Path to uploaded files. <BR/>
     */
    protected String p_filePath = "/upload/files/";

    /**
     * the default mimetype that is used when the mimetype cannot
     * be read from the servlet context. <BR/>
     */
    protected String p_defaultMimeType = "text/html";

    /**
     * Boolean as string: true. <BR/>
     */
    private static final String BOOL_TRUE = "true";


    /**
     * Header variable cache control: no caching. <BR/>
     */
    private static final String HV_NOCACHE = "no-cache";


    /***************************************************************************
     * Initialize the servlet. <BR/>
     *
     * @throws  ServletException
     *          An exception occurred during initializing the servlet.
     *
     * @see javax.servlet.GenericServlet#init ()
     */
    public void init () throws ServletException
    {
        String value = null;

//        super.init ();

        // set configuration parameters:
        // set the debug option:
        value = this.getInitParameter ("debug");
        if (value != null)
        {
            this.p_isDebug = value.equalsIgnoreCase (FileAccessServlet.BOOL_TRUE);
        } // if

        // set the file path:
        value = this.getInitParameter ("path");
        if (value != null)
        {
            // ensure correct path
            if (!value.startsWith ("/"))
            {
                value = "/" + value;
            } // if
            if (!value.endsWith ("/"))
            {
                value += "/";
            } // if
            this.p_filePath = value;
        } // if (value != null)

        // set the default mimetype
        value = this.getInitParameter ("defaultMimeType");
        if (value != null)
        {
            this.p_defaultMimeType = value;
        } // if
    } // init


    /***************************************************************************
     * Destroy the servlet. <BR/>
     *
     * @see javax.servlet.Servlet#destroy ()
     */
    public void destroy ()
    {
        // Clean up allocated resources
    } // destroy


    /**************************************************************************
     * This method instantiates a Class, which name is returned from method
     * {@link BaseServlet#getAppClassName BaseServlet.getAppClassName}.
     * The Class which is instantiated has to implement the interface
     * {@link ibs.io.servlet.IApplication IApplication}.
     *
     * @param   req     The object representing the client request.
     * @param   res     The object which is responsible for handling the
     *                  response to the client.
     *
     * @exception   ServletException
     *              Any servlet dependent exception.
     * @exception   IOException
     *              An exception during I/O operation.
     */
    protected void performTask (HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException
    {
        byte[] buf = new byte [8192];
        FileInputStream in;
//        InputStream in;
        ServletOutputStream out;
        String contentType;
        int n;
        File file;
        String fileName;
        String filePath;
        String realPath;
        String saveParam;
        boolean isSave;

        // show debugging information
        if (this.p_isDebug)
        {
            this.showRequestInfo (req);
        } // if

        // get the sessioninfo
//        ServletSessioninfo sessionInfo = (ServletSessioninfo) req.getSession ().getAttribute ("Session");

        // get the file path that should have the following format
        // "<OID>/<filename>"
        filePath = req.getParameter ("file");
        // did we get a valid filepath?
        if (filePath != null && filePath.length () > 0)
        {
            // we need to strip the first part of the filePath because
            // it contains the context root and the relative path
            // will be interpreted wrong by getRealPath then
/*
            if (filePath.startsWith ("/"))
                filePath = filePath.substring (2);
            filePath = filePath.substring (filePath.indexOf ("/"));
*/
            // add the path to the uploaded files
            filePath = this.p_filePath + filePath;
            // show debugging information
            if (this.p_isDebug)
            {
                System.out.println ("filePath: " + filePath);
            } // if

            // create the real path
            realPath = this.getServletContext ().getRealPath (filePath);
            file = new File (realPath);
            if (this.p_isDebug)
            {
                System.out.println ("realPath: " + realPath);
            } // if

            // initialize the streams
            in = new FileInputStream (file);
            out = res.getOutputStream ();

            // check if the save only option is set
            saveParam = req.getParameter ("save");
            isSave = saveParam != null && saveParam.equalsIgnoreCase (FileAccessServlet.BOOL_TRUE);
            if (!isSave)
            {
                // try to get the mime type from the servlet context
                contentType = this.getServletContext ().getMimeType (filePath);
                // check if the mimetype was found:
                if (contentType == null)
                {
                    // get the mimetype from the mimetype table:
                    contentType = Mimetypes.getMimetype (filePath);

                    // check if the mimetype was found now:
                    if (contentType == null)
                    {
                        contentType = this.p_defaultMimeType;
                    } // if
                } // if
            } // if (isSave == null || (!isSave.equalsIgnoreCase (FileAccessServlet.BOOL_TRUE)))
            else                        // save option set
            {
                contentType = "application/octet-stream";
            } // else
            // set the content type
            res.setContentType (contentType);
            if (this.p_isDebug)
            {
                System.out.println ("contentType: " + contentType);
            } // if

            // set the filename
            fileName = file.getName ();
            // strip the oid
            if (fileName.startsWith (UtilConstants.NUM_START_HEX))
            {
                fileName = fileName.substring (18);
            } // if

            // set additional headers
            res.setHeader ("Content-Length", file.length () + "");
            res.setHeader ("Accept-Ranges", "Bytes");
            if (isSave)
            {
                res.setHeader ("Content-Disposition", "attachment; filename=\"" + fileName + "\";");
            } // if
            // disable caching
            res.setHeader ("Cache-Control", FileAccessServlet.HV_NOCACHE);
            res.setHeader ("Pragma", FileAccessServlet.HV_NOCACHE);
            res.setDateHeader ("Expires", 0);

            // write the content of the file
            while ((n = in.read (buf)) != -1)
            {
                out.write (buf, 0, n);
            } // while (( n = in.read (buf)) != -1)
        } // if (filePath != null && (!filePath.length () == 0))
        else    // no filepath set
        {
            // send an error
            res.sendError (HttpServletResponse.SC_NOT_FOUND, "Not Found");
            return;
        } // // no filepath set
    } // performTask


    /**************************************************************************
     * Display the request info. <BR/>
     *
     * @param req  the request object
     */
    protected void showRequestInfo (HttpServletRequest req)
    {
        System.out.println ();
        System.out.println ("File Access Request Info");
        System.out.println ("------------------------");

        // Show generic info
        System.out.println ("Encoding : " + req.getCharacterEncoding ());
        System.out.println ("Length : " + req.getContentLength ());
        System.out.println ("Type : " + req.getContentType ());

        System.out.println ();
        System.out.println ("Parameters");

        @SuppressWarnings ("unchecked") // suppress compiler warning
        Enumeration<String> parameters = req.getParameterNames ();

        while (parameters.hasMoreElements ())
        {
            String paramName = parameters.nextElement ();
            String[] values = req.getParameterValues (paramName);
            System.out.print (paramName + " : ");
            for (int i = 0; i < values.length; i++)
            {
                System.out.print (values[i] + ", ");
            } // for i
            System.out.println ();
        } // while

        System.out.println ();

        System.out.println ("Protocol : " + req.getProtocol ());
        System.out.println ("Address : " + req.getRemoteAddr ());
        System.out.println ("Host : " + req.getRemoteHost ());
        System.out.println ("Scheme : " + req.getScheme ());
        System.out.println ("Server Name : " + req.getServerName ());
        System.out.println ("Server Port : " + req.getServerPort ());

        System.out.println ();
        System.out.println ("Attributes");

        @SuppressWarnings ("unchecked") // suppress compiler warning
        Enumeration<String> attributes = req.getAttributeNames ();

        while (attributes.hasMoreElements ())
        {
            String attributeName = attributes.nextElement ();
            System.out.print (attributeName + " : ");
            System.out.println (req.getAttribute (attributeName).toString ());
        } // while (attributes.hasMoreElements ())

        System.out.println ();

        // Show HTTP info
        System.out.println ();
        System.out.println ("HTTP Header Info");
        System.out.println ();

        System.out.println ("Authentication Type : " + req.getAuthType ());
        System.out.println ("HTTP Method : " + req.getMethod ());
        System.out.println ("Path Info : " + req.getPathInfo ());
        System.out.println ("Path translated : " + req.getPathTranslated ());
        System.out.println ("Query string : " + req.getQueryString ());
        System.out.println ("Remote user : " + req.getRemoteUser ());
        System.out.println ("Requested session id : " +
            req.getRequestedSessionId ());
        System.out.println ("Request URI : " + req.getRequestURI ());
        System.out.println ("Context path : " + req.getContextPath ());
        System.out.println ("Servlet path : " + req.getServletPath ());
        System.out.println ("User principal : " + req.getUserPrincipal ());


        System.out.println ();
        System.out.println ("Headers : ");

        @SuppressWarnings ("unchecked") // suppress compiler warning
        Enumeration<String> headers = req.getHeaderNames ();

        while (headers.hasMoreElements ())
        {
            String headerName = headers.nextElement ();
            System.out.print (headerName + " : ");
            System.out.println (req.getHeader (headerName));
        } // while (headers.hasMoreElements ())

        System.out.println ("-------------------");
        System.out.println ();
    } // showRequestInfo

} // FileAccessServlet
