/*
 * Class: XSLTTransformer.java
 */

// package:
package ibs.tech.xslt;

// imports:
import ibs.BaseObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.xalan.processor.TransformerFactoryImpl;
import org.apache.xml.utils.DefaultErrorHandler;
import org.w3c.dom.Document;


/******************************************************************************
 * This class implements transformation algorithms for XML files based on the
 * XSLT (Xml Stylesheet Language for Transformation) technology. <BR/>
 *
 * @version     $Id: XSLTTransformer.java,v 1.14 2009/11/19 13:52:48 btatzmann Exp $
 *
 * @author      Klaus, 30.09.2003
 ******************************************************************************
 */
public class XSLTTransformer extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: XSLTTransformer.java,v 1.14 2009/11/19 13:52:48 btatzmann Exp $";


    /**
     * The default name for the target file. <BR/>
     */
    private static final String DEFAULT_TARGETFILE = "out.xml";

    /**
     * Error message to be displayed when document type is not supported. <BR/>
     */
    public static String errDocTypeNotSupported =
        "Required document type not supported.";

    /**
     * The parsed translator file as Source object for translation. <BR/>
     */
    private Source p_translator = null;



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Main method of this class. <BR/>
     *
     * @param   args    The command line arguments.
     */
    public static void main (String[] args)
    {
        // evaluate the arguments:
        XSLTTransformer.evalArgs (args);
    } // main


    /**************************************************************************
     * Display the syntax of the program. <BR/>
     *
     * @param   args    The command line arguments.
     */
    private static void syntax (String[] args)
    {
        String fileName = XSLTTransformer.class.getName ();

        XSLTTransformer.println (
            fileName + ": transform one XML file into another.\n" +
            "\n" +
            "Syntax: " + fileName + " sourcefile transformer [targetfile]\n" +
            "    " + fileName + " ....... this program\n" +
            "    sourcefile ... the name of the source file to be transformed\n" +
            "    transformer .. the XSLT file to be used as transformer\n" +
            "    targetfile ... the name of the target file\n" +
            "                   default: \"" +
            XSLTTransformer.DEFAULT_TARGETFILE + "\"\n" +
            "\n" +
            "example:\n" +
            fileName + " d:\\xmlfiles\\infile.xml d:\\xsltfiles\\transformer.xsl d:\\xmlfiles\\infile_t.xml"
        );
    } // syntax


    /**************************************************************************
     * Evaluate the command line arguments. <BR/>
     * This method checks the command line arguments and calls the
     * {@link #translate translate} method if everything was o.k.
     *
     * @param   args    The command line arguments.
     *
     * @return  <CODE>true</CODE> if everything was o.k.,
     *          <CODE>false</CODE> otherwise
     */
    private static boolean evalArgs (String[] args)
    {
        boolean failure = false;        // was there a failure?
        String inFileName;              // name of input file
        String transformerFileName;     // name of transformer file;
        String outFileName = XSLTTransformer.DEFAULT_TARGETFILE; // name of target file;

        // check the syntax:
        if (args.length >= 2 && args.length <= 3) // correct number of arguments?
        {
            // set the values:
            inFileName = args[0];
            transformerFileName = args[1];

            if (args.length >= 3)       // output file name set?
            {
                outFileName = args[2];
            } // if display mode set

            // perform the translation:
            failure = !XSLTTransformer.translate (inFileName,
                transformerFileName, outFileName);
        } // if correct number of arguments
        else                            // wrong syntax in call
        {
            failure = true;
        } // else wrong syntax in call

        if (failure)                    // there was a failure?
        {
            // display the correct syntax:
            XSLTTransformer.syntax (args);
        } // if there was a failure

        // return if there was no failure:
        return !failure;
    } // evalArgs


    /**************************************************************************
     * Perform the translation. <BR/>
     *
     * @param   inFile      Name of input file.
     * @param   transformer Name of transformer file.
     * @param   outFile     Name of output file.
     *
     * @return  <CODE>true</CODE> if everything was o.k.,
     *          <CODE>false</CODE> otherwise
     */
    private static boolean translate (String inFile, String transformer,
                                      String outFile)
    {
        boolean failure = false;        // was there a failure?

        try
        {
            // perform the translation:
            new XSLTTransformer (transformer)
                .translateFile (inFile, outFile);
        } // try
        catch (XSLTTransformationException e)
        {
            e.printStackTrace ();
            failure = true;
        } // catch

        // return if there was no failure:
        return !failure;
    } // translate


    /**************************************************************************
     * Display a message. <BR/>
     *
     * @param   msg     The message to be displayed.
     */
    protected static final void print (String msg)
    {
        System.out.print (msg);
    } // print


    /**************************************************************************
     * Display a line message. <BR/>
     *
     * @param   msg     The message to be displayed.
     */
    protected static final void println (String msg)
    {
        System.out.println (msg);
    } // println


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a XSLTTransformer object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   translatorFile  The path and name of the translator file.
     *                          (must be conform to the URI syntax)
     *
     * @throws  XSLTTransformationException
     *          There occurred an error during the translation process.
     *          The filename could not be resolved to an InputStream.
     */
    public XSLTTransformer (String translatorFile)
        throws XSLTTransformationException
    {
        // call constructor of super class:
        super ();

        // create Source object:
        this.p_translator =
            new StreamSource (new File (translatorFile));
    } // XSLTTransformer


    /**************************************************************************
     * Creates a XSLTTransformer object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   translator  The translator to be set.
     */
    public XSLTTransformer (Document translator)
    {
        // call constructor of super class:
        super ();

        // create Source object:
        this.p_translator = new DOMSource (translator);
    } // XSLTTransformer


    /**************************************************************************
     * Creates a XSLTTransformer object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     * BB note: passing an XSL stylesheet via a stream causes the xsl processor
     * loosing the path context! Relative path in import statements can
     * not be used properly anymore! Thus this method can only be used on
     * stylesheets that do not use relative path definitions!
     *
     * @param   translatorStream    The data stream containing the translator.
     */
    public XSLTTransformer (InputStream translatorStream)
    {
        // call constructor of super class:
        super ();

        // create Source object:
        this.p_translator = new StreamSource (translatorStream);
    } // XSLTTransformer


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Converts one xml file with the given translator to a new structure. <BR/>
     *
     * @param   orig    The original document.
     * @param   target  The target document.
     *
     * @throws  XSLTTransformationException
     *          There occurred an error during the translation process.
     */
    public void translateFile (Source orig, Result target)
        throws XSLTTransformationException
    {
        TransformerFactoryImpl tFactory = null;        // factory for transformer
        Transformer transformer;                    // the transformer
        // set the printWriter to collect the error messages from the parser
        StringWriter parseErrors = new StringWriter ();
        DefaultErrorHandler errorHandler = new DefaultErrorHandler ();

        try
        {
            // 1. create a transformer factory
            // Use the static TransformerFactory.newInstance () method
            // to instantiate a TransformerFactory. The
            // javax.xml.transform.TransformerFactory system property
            // setting determines the actual class to instantiate --
            // org.apache.xalan.transformer.TransformerImpl.
            tFactory = (TransformerFactoryImpl) TransformerFactory.newInstance ();
            tFactory.setErrorListener (errorHandler);

            // check if the necessary source and reasult types can be handled:
            this.checkSourceTypeAllowed (tFactory, this.p_translator);
            this.checkSourceTypeAllowed (tFactory, orig);
            this.checkResultTypeAllowed (tFactory, target);

            // 2. use the transformer factory to process the stylesheet source
            // Use the TransformerFactory to instantiate a Transformer
            // that will work with the stylesheet you specify. This
            // method call also processes the stylesheet into a compiled
            // Templates object.
            transformer = tFactory.newTransformer (this.p_translator);
            // CONTRAINT: the transformer must not be null
            // if null a serious error must have been occurred
            if (transformer != null)
            {
                // 3. use the transformer to transform an xml source and send the
                // output to a result object
                // Use the Transformer to apply the associated Templates
                // object to an XML document and write the output to a file
                transformer.transform (orig, target);
                // clear the transformer:
                transformer = null;
            } // if (transformer != null)
            else    // an error must have been occurred
            {
                throw new XSLTTransformationException ("Constraint Violation: transformer = null!");
            } // else    // an error must have been occurred
        } // try
        catch (TransformerConfigurationException e)
        {
            DefaultErrorHandler.ensureLocationSet (e);
            DefaultErrorHandler.printLocation (new PrintWriter (parseErrors), e);
            throw new XSLTTransformationException (
                parseErrors.toString () + e.getLocalizedMessage ());
        } // catch
        catch (TransformerException e)
        {
            DefaultErrorHandler.ensureLocationSet (e);
            DefaultErrorHandler.printLocation (new PrintWriter (parseErrors), e);
            throw new XSLTTransformationException (
                parseErrors.toString () + e.getLocalizedMessage ());
        } // catch
    } // translateFile


    /**************************************************************************
     * Converts one xml file with the given translator to a new structure. <BR/>
     *
     * @param   origFile    The original file to be translated.
     * @param   targetFile  The target file name.
     *
     * @throws  XSLTTransformationException
     *          There occurred an error during the translation process.
     */
    public void translateFile (String origFile, String targetFile)
        throws XSLTTransformationException
    {
        StreamSource orig;
        StreamResult target;
        InputStream inputStream;
        OutputStream outputStream;

        try
        {
            // create the source stream:
            inputStream = new FileInputStream (origFile);
            orig = new StreamSource (inputStream);
            orig.setSystemId (new File (origFile));
            // create the target stream:
            outputStream = new FileOutputStream (targetFile);
            target = new StreamResult (outputStream);
            target.setSystemId (new File (targetFile));

            // call the translation method:
            this.translateFile (orig, target);

            // close the streams:
            outputStream.close ();
            inputStream.close ();
        } // try
        catch (FileNotFoundException e)
        {
            // create a new exception and throw it:
            XSLTTransformationException exc = new XSLTTransformationException (e);
            throw exc;
        } // catch
        catch (IOException e)
        {
            // create a new exception and throw it:
            XSLTTransformationException exc = new XSLTTransformationException (e);
            throw exc;
        } // catch
    } // translateFile


    /**************************************************************************
     * Converts one xml file with the given translator to a new structure. <BR/>
     *
     * @param   origDoc     The original document to be translated.
     * @param   targetDoc   The target document object.
     *
     * @throws  XSLTTransformationException
     *          There occurred an error during the translation process.
     */
    public void translateFile (Document origDoc, Document targetDoc)
        throws XSLTTransformationException
    {
        // call the translation method:
        this.translateFile (new DOMSource (origDoc), new DOMResult (targetDoc));
    } // translateFile


    /**************************************************************************
     * Converts one xml file with the given translator to a new structure. <BR/>
     *
     * @param   origDoc         The original document to be translated.
     * @param   targetStream    The target stream.
     *
     * @throws  XSLTTransformationException
     *          There occurred an error during the translation process.
     */
    public void translateFile (Document origDoc, OutputStream targetStream)
        throws XSLTTransformationException
    {
        // call the translation method:
        this.translateFile (new DOMSource (origDoc), new StreamResult (targetStream));
    } // translateFile


    /**************************************************************************
     * Converts one xml file with the given translator to a new structure. <BR/>
     *
     * @param   origDoc The original document to be translated.
     * @param   targetFile  The target file name.
     *
     * @throws  XSLTTransformationException
     *          There occurred an error during the translation process.
     */
    public void translateFile (Document origDoc, String targetFile)
        throws XSLTTransformationException
    {
        OutputStream outputStream;

        try
        {
            // create the output stream:
            outputStream = new FileOutputStream (targetFile);

            // call the translation method:
            this.translateFile (origDoc, outputStream);

            // close the stream:
            outputStream.close ();
        } // try
        catch (FileNotFoundException e)
        {
            // create a new exception and throw it:
            XSLTTransformationException exc = new XSLTTransformationException (e);
            throw exc;
        } // catch
        catch (IOException e)
        {
            // create a new exception and throw it:
            XSLTTransformationException exc = new XSLTTransformationException (e);
            throw exc;
        } // catch
    } // translateFile


    /**************************************************************************
     * Converts one xml string with the given translator to a new structure. <BR/>
     *
     * @param   origStr The original string to be translated.
     *
     * @return  A string containing the resulting structure.
     *
     * @throws  XSLTTransformationException
     *          There occurred an error during the translation process.
     */
    public String translateString (String origStr)
        throws XSLTTransformationException
    {
        StreamSource orig;
        StreamResult target;
        StringReader inputStream;
        StringWriter outputStream;

        try
        {
            // create the source stream:
            inputStream = new StringReader (origStr);
            orig = new StreamSource (inputStream);
            // create the target stream:
            outputStream = new StringWriter ();
            target = new StreamResult (outputStream);

            // call the translation method:
            this.translateFile (orig, target);

            // close the streams:
            outputStream.close ();
            inputStream.close ();

            // return the result:
            return outputStream.toString ();
        } // try
        catch (FileNotFoundException e)
        {
            // create a new exception and throw it:
            XSLTTransformationException exc = new XSLTTransformationException (e);
            throw exc;
        } // catch
        catch (IOException e)
        {
            // create a new exception and throw it:
            XSLTTransformationException exc = new XSLTTransformationException (e);
            throw exc;
        } // catch
    } // translateString


    /**************************************************************************
     * Converts one xml string with the given translator to a new structure. <BR/>
     *
     * @param   origStr The original string to be translated.
     *
     * @return  A string containing the resulting structure.
     *
     * @throws  XSLTTransformationException
     *          There occurred an error during the translation process.
     */
    public StringBuffer translateString (StringBuffer origStr)
        throws XSLTTransformationException
    {
        StreamSource orig;
        StreamResult target;
        StringReader inputStream;
        StringWriter outputStream;

        try
        {
            // create the source stream:
            inputStream = new StringReader (origStr.toString ());
            orig = new StreamSource (inputStream);
            // create the target stream:
            outputStream = new StringWriter ();
            target = new StreamResult (outputStream);

            // call the translation method:
            this.translateFile (orig, target);

            // close the streams:
            outputStream.close ();
            inputStream.close ();

            // return the result:
            return outputStream.getBuffer ();
        } // try
        catch (FileNotFoundException e)
        {
            // create a new exception and throw it:
            XSLTTransformationException exc = new XSLTTransformationException (e);
            throw exc;
        } // catch
        catch (IOException e)
        {
            // create a new exception and throw it:
            XSLTTransformationException exc = new XSLTTransformationException (e);
            throw exc;
        } // catch
    } // translateString


    /**************************************************************************
     * Check if the feature which is necessary for handling a specific data
     * source type is available. <BR/>
     *
     * @param   tFactory    The transformer factory for creating the
     *                      necessary transformer.
     * @param   source      The data source.
     *
     * @throws  XSLTTransformationException
     *          There occurred an error during checking the feature.
     */
    private void checkSourceTypeAllowed (TransformerFactory tFactory, Source source)
        throws XSLTTransformationException
    {
        String feature = null;          // the feature to be checked

        // get the type of the source:
        if (source instanceof DOMSource) // DOMSource?
        {
            feature = DOMSource.FEATURE;
        } // if DOMSource
        else if (source instanceof SAXSource) // SAXSource?
        {
            feature = SAXSource.FEATURE;
        } // else if SAXSource
        else if (source instanceof StreamSource) // StreamSource?
        {
            feature = StreamSource.FEATURE;
        } // else if StreamSource
/*
        else if (source instanceof XSLTCSource) // XSLTCSource?
        {
            feature = XSLTCSource.FEATURE;
        } // else if XSLTCSource
*/

        // check if the type is supported:
        if (!tFactory.getFeature (feature)) // document type not supported?
        {
            throw new XSLTTransformationException (
                XSLTTransformer.errDocTypeNotSupported);
        } // if document type not supported
    } // checkSourceTypeAllowed


    /**************************************************************************
     * Check if the feature which is necessary for handling a specific data
     * result type is available. <BR/>
     *
     * @param   tFactory    The transformer factory for creating the
     *                      necessary transformer.
     * @param   result      The data result.
     *
     * @throws  XSLTTransformationException
     *          There occurred an error during checking the feature.
     */
    private void checkResultTypeAllowed (TransformerFactory tFactory, Result result)
        throws XSLTTransformationException
    {
        String feature = null;          // the feature to be checked

        // get the type of the result:
        if (result instanceof DOMResult) // DOMResult?
        {
            feature = DOMResult.FEATURE;
        } // if DOMResult
        else if (result instanceof SAXResult) // SAXResult?
        {
            feature = SAXResult.FEATURE;
        } // else if SAXResult
        else if (result instanceof StreamResult) // StreamResult?
        {
            feature = StreamResult.FEATURE;
        } // else if StreamResult
/*
        else if (Result instanceof XSLTCResult) // XSLTCResult?
        {
            feature = XSLTCResult.FEATURE;
        } // else if XSLTCResult
*/

        // check if the type is supported:
        if (!tFactory.getFeature (feature)) // document type not supported?
        {
            throw new XSLTTransformationException (
                XSLTTransformer.errDocTypeNotSupported);
        } // if document type not supported
    } // checkResultTypeAllowed

} // class XSLTTransformer
