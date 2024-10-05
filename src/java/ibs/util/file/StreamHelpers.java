/*
 * Class: StreamHelpers.java
 */

// package:
package ibs.util.file;

// imports:
import ibs.util.file.FileException;
import ibs.io.IOHelpers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;


/******************************************************************************
 * This class contains some helper methods for stream interaction. <BR/>
 *
 * @version     $Id: StreamHelpers.java,v 1.5 2007/07/10 09:04:13 kreimueller Exp $
 *
 * @author      Klaus, 10.01.2004
 ******************************************************************************
 */
public abstract class StreamHelpers extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: StreamHelpers.java,v 1.5 2007/07/10 09:04:13 kreimueller Exp $";



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Copy a file from on location to another.
     * It is possible to rename the file during the move process. <BR/>
     *
     * HINT: This is done via reading in the file via an fileReader and output it
     * to a fileWriter. <BR/>
     *
     * @param   source      The source stream.
     * @param   destination The destination stream.
     * @param   streamName  The name of the stream.
     *                      Used for error messages.
     * @param   oldStr      String to be replaced.
     * @param   newStr      String which shall replace the oldStr.
     *
     * @return  <CODE>true</CODE> if there was something replaced,
     *          false otherwise.
     *
     * @throws  FileException
     *          An error occurred during replacement.
     */
    protected static boolean replaceInStream (InputStream source,
                                              OutputStream destination,
                                              String streamName, String oldStr,
                                              String newStr)
        throws FileException
    {
        Reader reader = null;           // the file reader
        Writer writer = null;           // the file writer
        boolean replacementDone = false; // was there something replaced?

        // create input file reader:
        reader = new BufferedReader (new InputStreamReader (source));

        // create output file writer:
        writer = new BufferedWriter (new OutputStreamWriter (destination));

        // parse the data stream:
        replacementDone =
            StreamHelpers.replaceInStream (reader, writer, streamName, oldStr, newStr);

        // return the result:
        return replacementDone;
    } // replaceInStream


    /**************************************************************************
     * Parse a stream. <BR/>
     * The stream is parsed, each configuration variable is replaced by its
     * value. Then the file is written to the writer.
     *
     * @param   reader      The input stream reader.
     * @param   writer      The output stream writer.
     * @param   streamName  The name of the stream.
     *                      Used for error messages.
     * @param   oldStr      String to be replaced.
     * @param   newStr      String which shall replace the oldStr.
     *
     * @return  <CODE>true</CODE> if there were some replacments done, false
     *          <CODE>false</CODE> otherwise.
     *
     * @throws  FileException
     *          An error occurred during replacement.
     */
    protected static boolean replaceInStream (Reader reader, Writer writer,
                                              String streamName, String oldStr,
                                              String newStr)
        throws FileException
    {
        boolean retVal = false;         // the return value
        int pos = 0;                    // position within string array
        int startPos = 0;               // starting position for search loop
        char[] oldStrChar = oldStr.toCharArray ();
        int oldStrLen = oldStrChar.length; // number of chars. to be replaced
        int bufferLen = oldStrLen;      // number of characters to read in
        char firstChar = oldStrChar[0]; // first character to be searched for
        char[] buffer = new char[bufferLen]; // the buffer
        int bufferStart = 0;            // start of buffer for reading in
        int len = 0;                    // number of bytes which were read
        int i = 0;                      // loop counter

        try
        {
            // read the data into the buffer:
            while ((len = reader.read (buffer, bufferStart, bufferLen -
                bufferStart)) >= (oldStrLen - bufferStart))
            {
                // check if the buffer is identical to the search string:
                pos = bufferStart;
                while (pos < oldStrLen && oldStrChar[pos] == buffer[pos])
                {
                    pos++;
                } // while

                // check if we found the required string or a part of it:
                if (pos == oldStrLen)       // found the search string?
                {
                    // write the string to the output:
                    writer.write (newStr);
                    bufferStart = 0;
                    retVal = true;
                } // if found the search string
                else                        // not found
                {
                    // initialize starting position for search loop:
                    startPos = 0;

                    // loop through the buffer and search for start of search
                    // string:
                    while (true)
                    {
                        // search for a new possible start position:
                        while (startPos < bufferLen &&
                            buffer[startPos] != firstChar)
                        {
                            startPos++;
                        } // while

                        // check if we found the start character:
                        if (startPos < bufferLen) // found start character?
                        {
                            // check if the characters after the start are
                            // identical to the search string:
                            i = 0;
                            while ((startPos + i) < bufferLen &&
                                oldStrChar[i] == buffer[startPos + i])
                            {
                                i++;
                            } // while

                            // check if we found the required string or a part of it:
                            if ((startPos + i) == bufferLen)
                                                // found the beginning of the string?
                            {
                                // write the beginning of the buffer to the
                                // output:
                                writer.write (buffer, 0, startPos);
                                // copy the found characters to the beginning of the
                                // buffer:
                                System.arraycopy (buffer, startPos, buffer, 0, i);
                                bufferStart = i;
                                // finish the search loop:
                                break;
                            } // if found the beginning of the string

                            // search again beginning at the next position:
                            startPos++;
                        } // if found start character
                        else            // did not find start character
                        {
                            // write the buffer to the output:
                            writer.write (buffer);
                            bufferStart = 0;
                            // finish the search loop:
                            break;
                        } // else did not find start character
                    } // while
                } // else not found
            } // while

            // check if there is anything left for writing:
            if ((bufferStart + len) > -1)
            {
                // write the rest of the stream to the output:
                writer.write (buffer, 0, bufferStart + len);
            } // if

            // ensure that the rest of the bytes are written to the output
            // stream:
            writer.flush ();
        } // try
        catch (IOException e)
        {
            String errorStr =
                "replaceInStream: Error when replacing data in stream " +
                streamName;
            IOHelpers.printError (errorStr, e, true);
            throw new FileException (errorStr, e);
        } // catch

        // return the result:
        return retVal;
    } // replaceInStream


    /**************************************************************************
     * Copy The data from one stream to another. <BR/>
     *
     * @param   source      The data source.
     * @param   destination The festination for the data.
     *
     * @return  <CODE>true</CODE> if the data could be copied and
     *          <CODE>false</CODE> if not.
     */
    public static final boolean copyData (InputStream source,
                                          OutputStream destination)
    {
        int data;

        try
        {
            BufferedInputStream reader = new BufferedInputStream (source);
            BufferedOutputStream writer = new BufferedOutputStream (destination);

            // read the data from the reader and output it to the writer:
            data = reader.read ();
            while (data != -1)
            {
                writer.write (data);
                data = reader.read ();
            } // while

            // ensure that the rest of the bytes are written to the output
            // stream:
            writer.flush ();

            // return the result:
            return true;
        } //try
        catch (IOException e)
        {
            // data could not be copied:
            return false;
        } //catch
    } // copyData

} // class StreamHelpers
