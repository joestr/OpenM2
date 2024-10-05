/*
 * Class: AContext.java
 */

// package:
package ibs.io;

// imports:
import ibs.BaseObject;
import ibs.util.Helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;


/******************************************************************************
 * This is the AContext Object.
 *
 * @version     $Id: AContext.java,v 1.21 2009/11/27 13:12:44 btatzmann Exp $
 *
 * @author      Christine Keim (CK), 991221
 ******************************************************************************
 */
public abstract class AContext extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: AContext.java,v 1.21 2009/11/27 13:12:44 btatzmann Exp $";


    /**
     * size of the maximum data-buffer. <BR/>
     * How many bytes can maximum be available for data-processing. <BR/>
     * Default value: <CODE>500</CODE>
     */
    public static int bufferLength = 500;

    /**
     * the portion of the datastream which should be processed next. <BR/>
     * This is the byteStream from the InputStream which has not already
     * been processed by the application.
     */
    public byte[] byteBuffer = null;

    /**
     * string representation of bytebuffer. <BR/>
     * The actual byteBuffer which has been transformed into a
     * string. At the end of the method readFromStream this
     * property holds all the amount of this.bytes charakters.
     */
    public String stringBuffer = null;

    /**
     * number of the valid bytes that the byteBuffer holds. <BR/>
     * Default value: <CODE>0</CODE>
     */
    public int bytes = 0;

    /**
     * was the data returned till the end. <BR/>
     * The data has been processed completely.
     */
    public boolean eof = false;

    /**
     * The End of a field in the form has been reached. <BR/>
     * The data available is at the end of a Filed in the
     * actual Form.
     */
    public boolean eoField = false;

    /**
     * the boundary between fields in the actual form. <BR/>
     * Is set in the AEnvironment.java class to the right delimiter
     * for the actual form through the Method setBoundary. <BR/>
     * Default value: <CODE>--</CODE>
     */
    protected String multipartBoundary = "--";

    /**
     * has the Stream been read till the end. <BR/>
     * If the property is set on true, then the end
     * of the stream (InputStream) has been reached.
     */
    protected boolean streamFinished = false;

    /**
     * The length of the content in bytes. <BR/>
     * How many bytes are on the InputStream
     * before reading anything from it?
     */
    protected int contentLength = -1;

    /**
     * The length of the last boundary. <BR/>
     * The last boundary of the stream exceeds the
     * boundary of the boundaries between the
     * fields by 2 bytes. It is set when the
     * property this.multipartBoundary is set.
     */
    protected int fullBoundaryLength = -1;

    /**
     * temporary buffer for data. <BR/>
     * the data that is read from the stream
     * and can't be processed is saved in this
     * buffer.
     */
    protected byte[] tempBuffer = null;

    /**
     * The number of bytes that were already read from the stream. <BR/>
     * Default value: <CODE>0</CODE>
     */
    protected int bytesRead = 0;

    /**
     * How often was the method called until now? <BR/>
     */
    private int tillNow = -1;

    /**
     * Has the content-type already been initialized? <BR/>
     */
    protected boolean contInit = false;


    /**************************************************************************
     * Creates a new instance of the AContext
     */
    public AContext ()
    {
        // nothing to do
    } // AContext


    /**************************************************************************
     * Sets the boundary of the form handled. <BR/>
     * The boundary between the fields of the form is set.
     *
     * @param boundary       The boundary of the Form handled
     */
    public void setBoundary (String boundary)
    {
        this.multipartBoundary = boundary;
        // we initialize fullBoundaryLength
        this.fullBoundaryLength = this.multipartBoundary.length () + 2;
    } // setBoundary


    /**************************************************************************
     * Returns how many bytes are yet available on the stream. <BR/>
     * Get how many bytes are left on the inputStream to read.
     *
     * @return  The amount of bytes that are available on the stream
     *
     * @throws  java.io.IOException
     *          An IOException occurred while attempting to get the
     *          Content Length from the Request.
     */
    protected abstract int getAvailableBytes () throws IOException;


    /**************************************************************************
     * Skips n bytes of the InputStream. <BR/>
     *
     * @param   n       Number of bytes to be skipped.
     *
     * @return  The amount of bytes that were skipped.
     *
     * @throws  java.io.IOException
     *          An IOException occurred while attempting to read the
     *          InputStream.
     */
    protected abstract int skip (int n) throws IOException;


    /**************************************************************************
     * Reads n bytes out of the InputStream. <BR/>
     *
     * @param target          byteArray where the data is written to
     * @param n               How many bytes should be read from the stream
     *
     * @return                the amount of bytes that were actually read
     *
     * @throws  java.io.IOException
     *          An IOException occurred while attempting to read the
     *          InputStream.
     */
    protected int read (byte[] target, int n) throws IOException
    {
        return this.read (target, 0, n);
    } // read


    /**************************************************************************
     * Reads n bytes out of the InputStream. <BR/>
     *
     * @param target          byteArray where the data is written to
     * @param offset          Index of the byteArray where the data should
     *                        be started to be written
     * @param n               How many bytes should be read from the stream
     *
     * @return                the amount of bytes that were actually read
     *
     * @throws  java.io.IOException
     *          An IOException occurred while attempting to read the
     *          InputStream.
     */
    protected abstract int read (byte[] target, int offset, int n)
        throws IOException;


    /**************************************************************************
     * Writes a string to the outputstream. <BR/>
     *
     * @param s               The string that has to be written to the response-object
     *
     * @throws  java.io.IOException
     *          An IOException occurred while attempting to write on
     *          the Response-object.
     */
    public abstract void write (String s) throws IOException;
//    protected abstract void write (String s) throws IOException;


    /**************************************************************************
     * This method writes the content of the actuall request to a file. <BR/>
     * <B>ATTENTION!! Because of reading the whole content of the stream this
     * method should only be used for debuging.</B>
     */
    public void writeStreamToFile ()
    {
        int available = -1;             // available bytes on the stream
        byte[] target = null;           // holds the whole stream
        FileOutputStream fos = null;    // stream to store the byte[] into a file

        try
        {
            // get the number of available bytes in the stream:
            available = this.getAvailableBytes ();
            target = new byte[available];

            this.read (target, 0, available);

            // constructs a date time string with the following format to make
            // output the file unique because of writing more than one files at
            // one request: filename__YYMMDD_HHMMSSMM
            // set the date and time strings with current date and time:
            GregorianCalendar cal = new GregorianCalendar ();
            String date = "__";             // The string containing the date.
            String time = "_";              // The string containing the time.
            int tmp = 0;

            tmp = cal.get (Calendar.YEAR);
            date += ("" + tmp).substring (2);
            tmp = cal.get (Calendar.MONTH) + 1;
            date += tmp < 10 ? ("0" + tmp) : "" + tmp;
            tmp = cal.get (Calendar.DAY_OF_MONTH);
            date += tmp < 10 ? ("0" + tmp) : "" + tmp;

            tmp = cal.get (Calendar.HOUR_OF_DAY);
            time += tmp < 10 ? "0" + tmp : "" + tmp;
            tmp = cal.get (Calendar.MINUTE);
            time += tmp < 10 ? "0" + tmp : "" + tmp;
            tmp = cal.get (Calendar.SECOND);
            time += tmp < 10 ? "0" + tmp : "" + tmp;
            tmp = cal.get (Calendar.MILLISECOND);
            time += tmp < 10 ? "0" + tmp : (tmp < 100 ? "0" + tmp : "" + tmp);

            // create the file and all neccessary directories:
            String path = "c:\\temp\\m2\\streams\\";
            String filename = "stream" + date + time + ".txt";
            File dir = new File (path);
            dir.mkdirs ();
            File output = new File (path + filename);

            fos = new java.io.FileOutputStream (output);
            fos.write (target);

            // IOException happened
            this.eof = true;
        } // try
        catch (IOException e)
        {
            // IOException happened
            this.eof = true;
        } // catch
        finally
        {
            // IOException happened
            this.eof = true;

            try
            {
                fos.close ();
            } // try
            catch (IOException e)
            {
                // IOException happened
                this.eof = true;
            } // catch
        } // finally
    } // writeStreamToFile


    /**************************************************************************
     * Reads n Bytes from the Stream. <BR/>
     *
     * @return  The amount of bytes the method has retrieved from the stream.
     *
     * @throws  UploadException
     *          An exception occurred during reding the data.
     */
    public final int readFromStream () throws UploadException
    {
        int pos = -1;              // the position where the delimiter is found
        int stringPos = -1;        // the position where the delimiter is found within
                                   // the encoded string
        int offSet = 0;            // Helper variable
        int skipped = 0;           // how many bytes were skipped at the beginning
        int available = -1;        // how many bytes are available on the stream?
        int next = -1;             // Helper variable

        // ATTENTION!! This method should only be used for dbugging because it
        // reads the whole content of the request and writes it to an file -->
        // the application has no request data to perform.
        if (false)
        {
            this.writeStreamToFile ();
        } // if

        // already read all from the stream - return 0 bytes read
        if (this.eof)
        {
            return 0;
        } // if

        if (this.contentLength < 0)
        {
            this.tillNow = 1;
        } // if

/*
        // necessary for not getting a timeout while uploading big files
        // writes maximum 10 times '..' on the output-stream
        // in the value of a <TAG> - Tag
        try
        {
            // necessary so that the browser does not get a timeout
            // it is not visible to the user though
            if (this.contentLength < 0)
            {
                this.write ("<HTML>");
                this.write ("<HEAD>");
                this.write ("<SCRIPT LANGUAGE='JavaScript'>\n");
                this.write ("var baseDir;\nif (window.opener == null) {");
                this.write ("\nif (top.system) {");
                this.write ("baseDir = top.system.getBaseDir (); }\n");
                this.write (" else { baseDir = (this.location.href).substring (0, this.location.href.lastIndexOf ('app') + 4);}\n");
                this.write ("} else { baseDir = window.opener.top.system.getBaseDir (); }\n");
                this.write ("this.document.write ('<BASE HREF=\"' + baseDir + '\">');");
                this.write ("</SCRIPT></HEAD>");
//                this.write ("<BODY>");
                this.write ("<BODY><INPUT TYPE=HIDDEN VALUE=\"");
                this.tillNow = 1;
            } // if ... the first time the method is called
            else
            {
                if (!this.streamFinished)
                {
                    if (this.overall < 0)
                        this.overall = this.contentLength/this.bufferLength;
                    this.tillNow++;
                    int temp;
                    temp = this.overall/10;
                    if (temp == 0)
                        temp = 3;
                    if ((this.tillNow%temp) == 0)
                    {
                         this.write ("..");
                    } // if
                } // if
                else if (!this.tokenWritten)
                {
                    this.write ("\"></BODY></HTML>\n");
                    this.tokenWritten = true;
                } // else if
            } // else
        } // try
        catch (IOException io)
        {
        } // catch
*/

        // reinitialize the Flag End of Field
        this.eoField = false;

        // getting available
        try
        {
            available = this.getAvailableBytes ();
        } // try
        catch (IOException io)
        {
            // IOException happened
            this.eof = true;
            this.bytes = 0;
            return this.bytes;
        } // catch

        // if bufferLength is invalid, then read all at once from the stream
        if (AContext.bufferLength <= 0)
        {
            AContext.bufferLength = this.contentLength;
        } // if

        // if there have been read all bytes on the stream
        if (!this.streamFinished && (available <= 0))
        {
            this.streamFinished = true;
        } // if

        // the stream has already been read completely
        // so the tempBuffer has to be checked, if it is null,
        // then there is nothing left to process
        if (this.streamFinished)
        {
            if (this.tempBuffer == null)
            {
                // the temporary buffer is empty - all data processed
                this.eof = true;
                this.bytes = 0;
            } // if
            else // get the remaining bytes from the tempBuffer
            {
                // we get all of a field, so eoField = true!
                this.eoField = true;

                // transform the tempBuffer in String
                this.stringBuffer = Helpers.byteToString (this.tempBuffer);
               
                // control if the boundary is in the Buffer
                // determines how far from the byteArray shall be processed
                
                // IBS-288 m2ml - Unicode support - Handling for Multipart Form Data
                // This has to be done via a 1-byte string. Within the encoded string
                // (this.stringBuffer) there is no 1:1 relation between the numer of
                // characters within the string and the byte array
                pos = new String (this.tempBuffer).indexOf (this.multipartBoundary);

                // determines how far from the string shall be processed
                stringPos = this.stringBuffer.indexOf (this.multipartBoundary);
                
                // tempBuffer becomes byteBuffer
                this.byteBuffer = this.tempBuffer;
                if (pos > -1)
                {
                    // delimiter found
                    this.bytes = pos;

                    // truncate the stringBuffer
                    this.stringBuffer = this.stringBuffer.substring (0, stringPos);

                    // is the data ending?
                    if ((this.tempBuffer.length - pos) <= 2 * (this.multipartBoundary.length () + 2))
                    // nothing more to come
                    {
                        // end of data reached
                        this.eof = true;
                        this.tempBuffer = null;
                    } // if
                    else
                    {
                        // calculate an offSet - how many bytes have to be saved?
                        offSet = this.tempBuffer.length - this.multipartBoundary.length () - pos;
                        // no bytes have to be saved - should never be the case
                        if (offSet <= 0)
                        {
                            this.eof = true;
                        } // if
                        else
                        {
                            // save the bytes in the tempBuffer
                            // the tempBuffer is initialized
                            // and then filled with the byteArray
                            // beginning with the data
                            // after the first delimiter
                            this.tempBuffer = new byte[offSet];
                            for (int i = 0; i < offSet; i++)
                            {
                                this.tempBuffer[i] = this.byteBuffer[i + pos +
                                    this.multipartBoundary.length ()];
                            } // for i
                        } // else
                    } // else
                } // if
                else
                {
                    // no Boundary found ... get the last few Bytes out of the Buffer
                    this.bytes = this.tempBuffer.length;
                    this.eof = true;
                } // else
            } // else
            return this.bytes;
        } // if

        // byteBuffer == null? it's the case when it's the first call
        // nothing has been read from the stream until now
        if ((this.byteBuffer == null) && (this.tillNow == 1))
        {
            // create a new byte[] that reads more bytes than normal
            this.byteBuffer = new byte[AContext.bufferLength + this.fullBoundaryLength];
            // reads bufferLength + boundary bytes
            try
            {
                // skip the first few bytes just to make sure the delimiter is skipped!
                skipped = this.skip (this.multipartBoundary.length () - 1);
                // the skipped bytes must be recorded to be read too.
                this.bytesRead += skipped;
                // read from the stream
                // to avoid to loose a delimiter we read fullBoundaryLength bytes more
                this.bytes = this.read (this.byteBuffer, 0, AContext.bufferLength + this.fullBoundaryLength);
                this.bytesRead += this.bytes;
                if (this.bytesRead >= this.contentLength)
                {
                    this.streamFinished = true;
                } // if
            } // try
            catch (IOException e)
            {
                this.eof = true;
                UploadException up = new UploadException (e.toString (), e);
                throw up;
            } // catch
            finally
            {
                this.tempBuffer = null;
            } // finally
        } // if it's the first time the stream is read (byteBuffer == null)
        else // not first time, that means byteBuffer is filled!
        {
            // has to retrieve the first few bytes from the tempBuffer
            offSet = 0;
            // write them in the byteBuffer;
            if (this.tempBuffer != null)
            {
                offSet = this.tempBuffer.length;
                for (int i = 0; i < offSet; i++)
                {
                    this.byteBuffer[i] = this.tempBuffer[i];
                } // for i
            } // if tempBuffer is not null

            // read the bytes in
            try
            {
                // read the rest of the bytes in to fill the array completely
                this.bytes = this.read (this.byteBuffer, offSet, this.byteBuffer.length - offSet);
                while (((this.bytes + offSet) < this.byteBuffer.length) &&
                    ((this.bytesRead + this.bytes) < this.contentLength))
                {
                    this.bytes += this.read (this.byteBuffer, offSet +
                        this.bytes, this.byteBuffer.length - offSet -
                        this.bytes);
                } // while
                this.bytesRead += this.bytes;
                if (this.bytesRead >= this.contentLength)
                {
                    this.streamFinished = true;
                } // if
            } // try
            catch (IOException e)
            {
                this.eof = true;
                UploadException up = new UploadException (e.toString (), e);
                throw up;
            } //catch
            finally
            {
                this.tempBuffer = null;
            } // finally

            this.bytes += offSet;
        } // else

        // stream was read with multipartBoundary-bytes more than usual
        // if the stream is Finished truncate the array!
        if (this.streamFinished)
        {
            byte[] temp = new byte[this.bytes];
            for (int i = 0; i < this.bytes; i++)
            {
                temp[i] = this.byteBuffer[i];
            } // for i
            this.byteBuffer = temp;
        } // if

        // Stream to String
        this.stringBuffer = Helpers.byteToString (this.byteBuffer);
               
        // control if the boundary is in the Buffer
        // determines how far from the byteArray shall be processed

        // IBS-288 m2ml - Unicode support - Handling for Multipart Form Data
        // This has to be done via a 1-byte string. Within the encoded string
        // (this.stringBuffer) there is no 1:1 relation between the numer of
        // characters within the string and the byte array
        pos = new String (this.byteBuffer).indexOf (this.multipartBoundary);
        
        // determines how far from the string shall be processed
        stringPos = this.stringBuffer.indexOf (this.multipartBoundary);
        
        // Control if the Stream has a multipartBoundary in it
        if (pos > -1 && pos <= AContext.bufferLength)  // delimiter has been found
            // set the bytes read to the position of the delimiter!!!
        {
            // the Field ends where the delimiter is found
            this.eoField = true;
            
            this.bytes = pos;
            
            // next -> keeps record where the next Field begins (after delimiter)
            next = pos + this.multipartBoundary.length ();

            // is it already the end of the data?
            if ((this.stringBuffer.length () - pos) + available <=
                2 * (this.multipartBoundary.length () + 2))
            // the data is at the end
            {
                this.streamFinished = true;
                this.eof = true;
                next = -1;
            } // if
            
            // get only the necessary part until the delimeter from the string
            this.stringBuffer = this.stringBuffer.substring (0, stringPos);
            
        } // if
        else // no delimiter has been found
        {
            // if more bytes then bufferLength have
            // been read, then only bufferlength# bytes are
            // relevant to be processed as data
            // else the read bytes are relevant
            this.bytes = (this.bytes > AContext.bufferLength)? AContext.bufferLength : this.bytes;
            next = this.bytes;
            
            // transform only the necessary bytes to String
            this.stringBuffer = Helpers.byteToString (this.byteBuffer, 0, this.bytes);
        } // else
        
        // fill the tempBuffer with the remaining bytes
        if (next > 0) // some bytes have to be temporarely saved
        {
            // establish a new tempBuffer with the necessary size to hold the remaining bytes
            this.tempBuffer = new byte[this.byteBuffer.length - next];
            // fill the tempBuffer with the remaining bytes
            for (int i = 0; i < this.tempBuffer.length; i++)
            {
                this.tempBuffer[i] = this.byteBuffer[next + i];
            } // for i
        } // if
        else if (next == 0) // should never be
        {
            // save the whole byteBuffer
            this.tempBuffer = this.byteBuffer;
        } // else if

        return this.bytes;
    } // readFromStream


    /**************************************************************************
     * Gets the sessionId and gives it back as a string. <BR/>
     *
     * @return  A string containing the sessionId.
     */
    public abstract String getSessionId ();


    /**************************************************************************
     * Gets a http header variable and gives it back as a string. <BR/>
     *
     * @param name The name of the header variable.
     *
     * @return  A string containing the header variable.
     */
    public abstract String getHttpHeaderVariable (String name);

} // class AContext
