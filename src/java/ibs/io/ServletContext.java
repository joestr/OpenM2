/*
 * Class ServletContext
 */

// package:
package ibs.io;

// imports:
import ibs.di.DIConstants;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/******************************************************************************
 * This is the ServletContext Object, which holds all Objects a Servlet manages.
 *
 * @version     $Id: ServletContext.java,v 1.13 2010/07/13 15:59:58 btatzmann Exp $
 *
 * @author      Christine Keim (CK), 990225
 ******************************************************************************
 */
class ServletContext extends AContext
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ServletContext.java,v 1.13 2010/07/13 15:59:58 btatzmann Exp $";


    // initialization flags
    /**
     * The Request object. <BR/>
     */
    public HttpServletRequest  request  = null;
    /**
     * The Response object. <BR/>
     */
    public HttpServletResponse response = null;

    /**
     * The object representing the session. <BR/>
     */
    public HttpSession session = null;

    /**
     * The output stream. <BR/>
     */
    public PrintWriter out = null;

    /**
     * The ServletInputStream
     */
    private ServletInputStream is = null;


    /**************************************************************************
     * Creates a new instance of the ServletContext
     *
     * @param req   the RequestObject of the Servlet
     * @param res   the Responset of the Servlet
     */
    protected ServletContext (HttpServletRequest req, HttpServletResponse res)
    {
        this.request = req;
        this.response = res;
        this.session = req.getSession (true); // get the session
        try
        {
            // Set the request encoding
            // see also http://java.sun.com/j2ee/1.4/docs/tutorial/doc/WebI18N5.html
            req.setCharacterEncoding (DIConstants.CHARACTER_ENCODING);
            
            // BT 20100713: Initializing this.out (PrintWriter) has been moved to the
            // write method for beeing able to use the res.getOutputStream () method
            // as well.
        } // try
        catch (IOException e)
        {
            this.out = null;
        } // catch
    } // ServletContext


    /**************************************************************************
     * Sets all local variables to null. <BR/>
     */
    void destroy ()
    {
        this.request     = null;
        this.response    = null;
        //        this.server      = null;
        this.session     = null;
        this.out = null;
        //        this.application = null;
    } // destroy


    /**************************************************************************
     * Returns how many bytes are yet available on the stream. <BR/>
     * Gets how many bytes are left on the inputStream to read.
     *
     * @return  The amount of bytes that are available on the stream.
     *
     * @exception java.io.IOException
     *            An IOException occurred while attempting to get the
     *            Content Length from the Request
     */
    protected int getAvailableBytes () throws IOException
    {
        // if the contentLength wasn't read, get it from the request-object
        if (this.contentLength == -1)
        {
            this.contentLength = this.request.getContentLength ();
        } // if
        // returns the contentLength minus the bytes that were already
        // read from the InputStream
        return this.contentLength - this.bytesRead;
    } // getAvailableBytes


    /**************************************************************************
     * Skips n bytes of the InputStream. <BR/>
     * Returns how many bytes were actually skipped. It is possible that
     * not the whole amount of bytes is skipped because the read-Method is
     * not able to read the amount of bytes at once.
     *
     * @param   n       Number of bytes to skip.
     *
     * @return  The amount of bytes that were skipped.
     *
     * @exception java.io.IOException
     *            An IOException occurred while attempting to read from
     *            the Input Stream
     */
    protected final int skip (int n) throws IOException
    {
        // temporary byteArray to skip the bytes
        byte[] temp = new byte[n];
        // reads n bytes from the stream if possible
        // and returns how much bytes have been read
        // the informations read are discarded...
        return this.read (temp, n);
    } // skip


    /**************************************************************************
     * Reads n bytes out of the InputStream. <BR/>
     * Gets given bytes out of the InputStream and writes them into the given
     * byteArray beginning at the offset-position.
     *
     * @param target          ByteArray where the data is written to
     * @param offset          Index of the byteArray where the data should
     *                        be started to be written
     * @param n               How many bytes should be read from the stream
     *
     * @return                The amount of bytes that were actually read
     *
     * @exception java.io.IOException
     *            An IOException occurred while attempting to read
     *            from the InputStream
     */
    protected final int read (byte[] target, int offset, int n) throws IOException
    {
        // if the stream has not been retrieved yet, then do it now
        if (this.is == null)
        {
            this.is = this.request.getInputStream ();
        } // if
        // if something has to be read
        if (n > 0)
        {
            // read it from the inputStream with an offset
            return this.is.read (target, offset, n);
        } // if

        // nothing to read, return 0
        return 0;
    } // read


    /**************************************************************************
     * Writes a string to the response by using the print writer. <BR/>
     *
     * @param   s       The string that has to be written to the response-object
     */
    public void write (String s)
    {
        if (!this.contInit)
        {
            // Encoding has to be set before calling getWriter ()
            // see http://java.sun.com/products/servlet/2.2/javadoc/javax/servlet/ServletResponse.html
            this.response.setContentType("text/html; charset=" + DIConstants.CHARACTER_ENCODING);
            this.contInit = true;
        } // if
        
        try
        {
            // has the print writer already been retrieved from the response
            if (this.out == null)
            {
                this.out = this.response.getWriter (); // the output stream            
            } // if
        } // try
        catch (IOException ex)
        {
            throw new RuntimeException ("Can not write data. Error during retrieval of writer from response:" + ex);
        } // catch
        
        if (s != null)               // there is a text to write?
        {
            // write the string to the outputstream (= PrintWriter out)
            this.out.print (s);
            // flushes the output (all infos written to the outoutstream
            // till now is actually put onto the Response object
            this.out.flush ();
        } // if
    } // write
    
    
    /**************************************************************************
     * Writes a byte[] to the output stream.
     * Additionally a byte order mark can be provided.<BR/>
     *
     * @param   data            The string that has to be written to the
     *                          response-object
     * @param   byteOrderMark   The byte order mark to write to the
     *                          output stream. Needed in case of writing files.
     */
    public void write (byte[] data, byte[] byteOrderMark)
    {
        ServletOutputStream outputStream;
        
        try
        {
            // Retrieve the output stream from the response        
            outputStream = this.response.getOutputStream();
        } // try
        catch (IOException ex)
        {
            throw new RuntimeException ("Can not write data. Error during creation of output stream:" + ex);
        } // catch
        
        try
        {
            // Write the byte order mark to the output stream if set
            if (byteOrderMark != null)
            {
              outputStream.write(byteOrderMark);
            } // if
            
            // Write the string with the provided encoding to the output stream
            outputStream.write(data);
            outputStream.flush ();
        } // try
        catch (IOException ex)
        {
            throw new RuntimeException ("Can not write data. Error during writing to output stream:" + ex);
        } // catch
    } // write


    /**************************************************************************
     * Writes a string to the outputstream. <BR/>
     *
     * @param name  The http header variable name.
     *
     * @return the string representing the http header variable.
     */
    public String getHttpHeaderVariable (String name)
    {
        // return the header variable with the given name
        return this.request.getHeader (name);
    } // getHttpHeaderVariable


    /**************************************************************************
     * Gets the sessionId and gives it back as a string. <BR/>
     *
     * @return  A string containing the sessionId.
     */
    public String getSessionId ()
    {
        return this.session.getId ();
    } // getSessionId

} // class ServletContext
