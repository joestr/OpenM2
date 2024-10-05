/*
 * Class: DOMHandler.java
 */

// package:
package ibs.tech.xml;

// imports:
import ibs.BaseObject;
import ibs.di.DIConstants;
import ibs.io.Environment;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.tech.html.IE302;
import ibs.tech.xslt.XSLTTransformationException;
import ibs.tech.xslt.XSLTTransformer;
import ibs.util.StringHelpers;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;


/*******************************************************************************
 * This class for XSLT processing and other handling with dom trees. <BR/>
 *
 * @version     $Id: DOMHandler.java,v 1.15 2010/04/08 08:16:02 btatzmann Exp $
 *
 * @author      Andreas Jansa (AJ), 020321
 *******************************************************************************
 */
public class DOMHandler extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DOMHandler.java,v 1.15 2010/04/08 08:16:02 btatzmann Exp $";


    /**
     * Error message prefix to be used when an error occurs in serializeDOM.
     * <BR/>
     */
    protected static String errSerializeDomPrefix =
        DOMHandler.class.getName () + ".serializeDOM: ";

    /**
     * Environment for getting input and generating output. <BR/>
     */
    protected Environment env = null;


    /**************************************************************************
     * This constructor creates a new instance of the class Processor. <BR/>
     *
     * @param   env     The current environment.
     * @param   sess    The current session object.
     * @param   app     The application object.
     */
    public DOMHandler (Environment env, SessionInfo sess, ApplicationInfo app)
    {
        this.setTracerHolder (sess);
/* KR not needed
        this.app = app;
        this.sess = sess;
*/
        this.env = env;
    } // Processor


    /**************************************************************************
     * Performs a transformation of a domtree via xslt. <BR/>
     *
     * @param   doc     Root node of domtree to be transformed.
     * @param   xslFile The xslt file for the transformation.
     *
     * @return  String with the result of the xslt translation.
     *          <CODE>null</CODE> if there occurred an error.
     */
    public String process (Document doc, String xslFile)
    {
        try
        {
/*
            Date start = new Date ();
*/

            ByteArrayOutputStream outStream = new ByteArrayOutputStream ();

            // set the user locale to the thread for beeing available
            // to retrieve it during xslt processing
            MultilingualTextProvider.setUserLocaleToThread (this.env);
            
            new XSLTTransformer (xslFile)
                .translateFile (doc, outStream);

/*
            Date stop = new Date ();
            long t = stop.getTime () - start.getTime ();
*/

			// Retrieve the UTF-8 content from the ByteArrayOutputStream
            return outStream.toString (DIConstants.CHARACTER_ENCODING);
        } // try
        catch (XSLTTransformationException e)
        {
            this.env.write (this.getClass ().getName () + ".process: " +
                       "XSLTTranslationException in XSLT File: " + xslFile +
                       IE302.TAG_NEWLINE + "Message: " + e.getMessage ());
        } // catch
        catch (UnsupportedEncodingException e)
        {
            this.env.write (this.getClass ().getName () + ".process: " +
                       "UnsupportedEncodingException during Tranlsation with XSLT File: " + xslFile +
                       IE302.TAG_NEWLINE + "Message: " + e.getMessage ());
        } // catch

        return null;
    } // process


    /**************************************************************************
     * Serialize a DOM tree. <BR/>
     *
     * @param   doc     The DOM tree to be serialized.
     *
     * @return  The stream with the serialized dom tree.
     *          <CODE>null</CODE> if there occurred an error;
     */
    public ByteArrayOutputStream serializeDOM (Document doc)
    {
        try
        {
            // open file output stream
            ByteArrayOutputStream out = new ByteArrayOutputStream ();

            // serialize the document to the stream:
            DOMHandler.serializeDOM (doc, out, true, null);
// trace (out.toString ());

            // return the result:
            return out;
        } // try
        catch (Exception e)
        {
            this.env.write (DOMHandler.errSerializeDomPrefix + e.toString ());
        } // catch

        // return error value:
        return null;
    } // serializeDOM


    /**************************************************************************
     * Serialize a DOM tree. <BR/>
     * In case an error occures the error will be written into the stream. <BR/>
     *
     * @param   doc         The DOM tree to be serialized.
     * @param   stream      The output stream to write the dom tree to.
     * @param   doIndent    Create indentation (for pretty printing).
     * @param   docDtd      The document type (the DTD).
     */
    public static void serializeDOM (Document doc, OutputStream stream,
                                     boolean doIndent, String docDtd)
    {
        try
        {
            // create the serializer:
            XMLSerializer serializer =
                new XMLSerializer (stream,
                    DOMHandler.getOutputFormat (doc, doIndent, docDtd));
            // print document to stream:
            serializer.serialize (doc);
        } // try
        catch (IOException e)
        {
            byte[] msg = (
                "Exception occurred while writing dom tree" + " in " +
                DOMHandler.errSerializeDomPrefix +
                e.getClass ().getName () + " " + e.getMessage ()).getBytes ();

            try
            {
                stream.write (msg);
                stream.flush ();
                //stream.close ();
            } // try
            catch (IOException e1)
            {
                // nothing to do
            } // catch
        } // catch
    } // serializeDOM


    /**************************************************************************
     * Serialize a DOM tree. <BR/>
     * In case an error occures an XMLException will be thrown. <BR/>
     *
     * @param   doc         The DOM tree to be serialized.
     * @param   filePath    The path and file name to serialize the DOM to
     * @param   doIndent    Create indentation (for pretty printing).
     * @param   docDtd      The document type (the DTD).
     *
     * @throws  XMLException
     *          Any exception during serialization.
     */
    public static void serializeDOMSecure (Document doc, String filePath,
                                           boolean doIndent, String docDtd)
        throws XMLException
    {
        FileOutputStream fileOutputStream = null;

        try
        {
            // first serialize the dom into an byte array
            // to test if it succeeds
            ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream ();
            // create the serializer:
            XMLSerializer serializer =
                new XMLSerializer (byteArrayStream, DOMHandler.getOutputFormat (doc, doIndent, docDtd));
            // print document to stream:
            serializer.serialize (doc);
            // note in case an parser error occurred the file will not be written!

            // open file output stream
            fileOutputStream = new FileOutputStream (filePath);
            // if we reach this point it worked and we can write the content
            // of the byte array into a file
            byteArrayStream.writeTo (fileOutputStream);
        } // try
        catch (IOException e)
        {
            throw new XMLWriterException (
                "Exception occurred while writing DOM tree: " +
                e.getMessage ());
        } // catch (IOException e)
        finally
        {
            try
            {
                // check if fileOutputStream needs to be closed
                if (fileOutputStream != null)
                {
                    fileOutputStream.close ();
                } // if
            } // try
            catch (IOException e)
            {
                // error while closing the stream
            } // catch (IOException e)
        } // finally
    } // serializeDOMSecure


    /**************************************************************************
     * Serialize a DOM tree. <BR/>
     * In case an error occurs the error will be written into the stream. <BR/>
     *
     * @param   doc     The DOM tree to be serialized.
     * @param   writer  The output writer to write the dom tree to.
     * @param   doIndent    Create indentation (for pretty printing).
     * @param   docDtd      The document type (the DTD).
     */
    public static void serializeDOM (Document doc, Writer writer,
                                     boolean doIndent, String docDtd)
    {
        try
        {
            // create the serializer:
            XMLSerializer serializer =
                new XMLSerializer (writer,
                                   DOMHandler.getOutputFormat (doc, doIndent, docDtd));
            // print document to stream:
            serializer.serialize (doc);
        } // try
        catch (IOException e)
        {
            String msg =
                "Exception occurred while writing dom tree in " +
                DOMHandler.errSerializeDomPrefix +
                e.getClass ().getName () + " " + e.getMessage ();

            try
            {
                writer.write (msg);
                writer.flush ();
                //stream.close ();
            } // try
            catch (IOException e1)
            {
                // nothing to do
            } // catch
        } // catch
    } // serializeDOM


    /**************************************************************************
     * Get the output format for serializing a dom tree. <BR/>
     *
     * @param   doc         The DOM tree to be serialized.
     * @param   doIndent    Create indentation (for pretty printing).
     * @param   docDtd      The document type (the DTD).
     *
     * @return  The output format.
     */
    private static OutputFormat getOutputFormat (
        Document doc, boolean doIndent, String docDtd)
    {
        // construct the output format:
        OutputFormat outputFormat =
            new OutputFormat (doc, DIConstants.CHARACTER_ENCODING, doIndent);
        // deactivate line wrapping:
        outputFormat.setLineWidth (0);
        outputFormat.setPreserveSpace (true);

        // set options for pretty printing:
        if (doIndent)
        {
            // preserver space must be set false otherwise the
            // indenting will be ignored!
            outputFormat.setPreserveSpace (false);
            // set the intend in case the document will be prettyprinted
            outputFormat.setIndent (DIConstants.XML_INTEND);
            outputFormat.setLineSeparator ("\r\n");
        } // if

        //<?xml version="1.0">
        outputFormat.setVersion ("1.0");

        // check if DTD shall be included:
        if (docDtd != null)
        {
            //<!DOCTYPE IMPORT SYSTEM "import.dtd">
            outputFormat.setDoctype (DIConstants.ELEM_IMPORT, docDtd);
        } // if

        // return the result:
        return outputFormat;
    } // getOutputFormat


    /**************************************************************************
     * This method ... <BR/>
     *
     * @param   doc         ???
     * @param   toStringTag ???
     *
     * @return  ???
     */
    public String domToString (Document doc, String toStringTag)
    {
        try
        {
            // open file output stream:
            ByteArrayOutputStream out = new ByteArrayOutputStream ();
            // serialize the dom tree to the output stream:
            DOMHandler.serializeDOM (doc, out, true, null);
            
            // Retrieve the UTF-8 content from the ByteArrayOutputStream
            String output = out.toString (DIConstants.CHARACTER_ENCODING);
            // replace special strings to show HTML output:
            output = StringHelpers.replace (output, "&", IE302.HCH_AMP);
            output = StringHelpers.replace (output, ">", IE302.HCH_GT);
            output = StringHelpers.replace (output, "<", IE302.HCH_LT);
            output = StringHelpers.replace (output, IE302.HCH_AMP + "apos;",
                IE302.HCH_LSQUO);

            // get String within tag to output
            String endTag = IE302.HCH_LT + "/" + toStringTag + IE302.HCH_GT;
            int firstIndexOfString = output.indexOf (IE302.HCH_LT + toStringTag);
            int lastIndexOfString =
                output.indexOf (endTag, firstIndexOfString + 1);

            // check if we found the end tag:
            if (lastIndexOfString < 0)
            {
                endTag = "/" + IE302.HCH_GT;
                lastIndexOfString =
                    output.indexOf (endTag, firstIndexOfString + 1);
            } // if

            // check if we found the end tag:
            if (lastIndexOfString >= 0)
            {
                String outputWithout = output.substring (firstIndexOfString,
                    lastIndexOfString + (endTag.length ()));
                // write the text non formatted
                return outputWithout;
            } // if
        } // try
        catch (Exception e)
        {
            this.env.write (this.getClass ().getName () + ".domToString: " +
                            e.toString ());
        } // catch
        return null;
    } // domToString

} // class DOMHandler
