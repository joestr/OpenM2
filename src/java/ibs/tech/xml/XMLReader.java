/*
 * Class: XMLReader.java
 */

// package:
package ibs.tech.xml;

// imports:
import ibs.BaseObject;
import ibs.tech.xml.XMLFactory;
import ibs.tech.xml.XMLFactoryException;
import ibs.tech.xml.XMLReaderException;
import ibs.util.file.FileHelpers;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;


/******************************************************************************
 * This class is used for reading XML files. <BR/>
 *
 * @version     $Id: XMLReader.java,v 1.6 2007/07/23 08:17:33 kreimueller Exp $
 *
 * @author      Klaus, 15.11.2003
 ******************************************************************************
 */
public class XMLReader extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: XMLReader.java,v 1.6 2007/07/23 08:17:33 kreimueller Exp $";


    /**
     * The root element of the document. <BR/>
     */
    private Element p_rootElem = null;

    /**
     * The document. <BR/>
     */
    private Document p_doc;             // the current document



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a XMLReader object and reads the document. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   fileName        The path and name of the file to be read.
     * @param   isValidating    <CODE>true</CODE> if the parser shall validate
     *                          documents which it reads.
     * @param   errorHandler    The error handler
     *
     * @throws  XMLReaderException
     *          There was an exception during trying to read the document. <BR/>
     *          Possible causes: A parser exception, a SAX exception, the file
     *          was not found or is <CODE>null</CODE>, there is no root element.
     */
    public XMLReader (String fileName, boolean isValidating,
                      ErrorHandler errorHandler) throws XMLReaderException
    {
        // call constructor of supe class:
        super ();

        // read the file:
        this.p_rootElem = this.read (fileName, isValidating, errorHandler);
    } // XMLReader


    /**************************************************************************
     * Creates a XMLReader object and reads the document. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   file            The file to be read.
     * @param   isValidating    <CODE>true</CODE> if the parser shall validate
     *                          documents which it reads.
     * @param   errorHandler    The error handler
     *
     * @throws  XMLReaderException
     *          There was an exception during trying to read the document. <BR/>
     *          Possible causes: A parser exception, a SAX exception, the file
     *          was not found or is <CODE>null</CODE>, there is no root element.
     */
    public XMLReader (File file, boolean isValidating, ErrorHandler errorHandler)
        throws XMLReaderException
    {
        // call constructor of supe class:
        super ();

        // read the file:
        this.p_rootElem = this.read (file, isValidating, errorHandler);
    } // XMLReader


    /**************************************************************************
     * Creates a XMLReader object and reads the document. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   xmlReader       A reader for the xml structure.
     * @param   isValidating    <CODE>true</CODE> if the parser shall validate
     *                          documents which it reads.
     * @param   errorHandler    The error handler
     *
     * @throws  XMLReaderException
     *          There was an exception during trying to read the document. <BR/>
     *          Possible causes: A parser exception, a SAX exception, the file
     *          was not found or is <CODE>null</CODE>, there is no root element.
     */
    public XMLReader (Reader xmlReader, boolean isValidating,
                      ErrorHandler errorHandler) throws XMLReaderException
    {
        // call constructor of supe class:
        super ();

        // read the file:
        this.p_rootElem = this.read (new InputSource (xmlReader), isValidating, errorHandler);
    } // XMLReader


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Get the root element. <BR/>
     *
     * @return  The root element.
     *          <CODE>null</CODE> if no root element exists.
     */
    public Element getRootElem ()
    {
        // get the property value and return the result:
        return this.p_rootElem;
    } // getRootElem


    /**************************************************************************
     * Get the document. <BR/>
     *
     * @return  The document.
     *          <CODE>null</CODE> if the document does not exist or is not
     *          valid.
     */
    public Document getDocument ()
    {
        // get the property value and return the result:
        return this.p_doc;
    } // getDocument


    /**************************************************************************
     * Read the contents of a xml input source and put it into a XML document
     * representation. <BR/>
     *
     * @param   inputSource     The input source to be read.
     *                          (File, InputSource, String, etc.)
     * @param   isValidating    <CODE>true</CODE> if the parser shall validate
     *                          documents which it reads.
     * @param   errorHandler    The error handler
     *
     * @return  The document.
     *
     * @throws  XMLReaderException
     *          There was an exception during trying to read the document. <BR/>
     *          Possible causes: A parser exception, a SAX exception, the file
     *          was not found or is <CODE>null</CODE>, there is no root element.
     */
    private Document getDocument (Object inputSource, boolean isValidating,
                          ErrorHandler errorHandler) throws XMLReaderException
    {
        DocumentBuilder db = null;      // the document builder
        Document doc = null;            // the document

        try
        {
            // get the document builder:
            db = XMLFactory.getDocBuilder (isValidating, errorHandler);

            // try to read the document:
            if (inputSource instanceof InputSource)
            {
                doc = db.parse ((InputSource) inputSource);
            } // if
            else if (inputSource instanceof File)
            {
                doc = db.parse ((File) inputSource);
            } // if
        } // try
        catch (XMLFactoryException e)
        {
            throw new XMLReaderException (e);
        } // catch
        catch (SAXNotRecognizedException e)
        {
            throw new XMLReaderException (
                "XML parsing: SAX not recognized.", e);
        } // catch
        catch (SAXNotSupportedException e)
        {
            throw new XMLReaderException (
                "XML parsing: SAX not supported.", e);
        } // catch
        catch (SAXException e)
        {
            throw new XMLReaderException (
                "A parse error occurred.", e);
        } // catch
        catch (IllegalArgumentException e)
        {
            throw new XMLReaderException (
                "The file name is null.", e);
        } // catch
        catch (IOException e)
        {
            throw new XMLReaderException (
                "An IO error occurred.", e);
        } // catch

        // return the result:
        return doc;
    } // read


    /**************************************************************************
     * Read the contents of a xml file. <BR/>
     *
     * @param   fileName        The path and name of the file to be read
     * @param   isValidating    <CODE>true</CODE> if the parser shall validate
     *                          documents which it reads.
     * @param   errorHandler    The error handler
     *
     * @return  The root element of the document.
     *
     * @throws  XMLReaderException
     *          There was an exception during trying to read the document. <BR/>
     *          Possible causes: A parser exception, a SAX exception, the file
     *          was not found or is <CODE>null</CODE>, there is no root element.
     */
    private Element read (String fileName, boolean isValidating,
                          ErrorHandler errorHandler) throws XMLReaderException
    {
        Element elem = null;            // the root element
        File dataFile;                  // the data file

        // check if the file exists:
        if (FileHelpers.exists (fileName))
        {
            // create the file object:
            dataFile = new File (fileName);

            // read the file:
            elem = this.read (dataFile, isValidating, errorHandler);
        } // if
        else
        {
            String errorMsg = "Data file not found: " + fileName + ".";
            System.out.println (errorMsg);
            throw new XMLReaderException (errorMsg);
        } // else

        // return the result:
        return elem;
    } // read


    /**************************************************************************
     * Read the contents of an input source. <BR/>
     *
     * @param   inputSource     The input source to be read (File, InputSource,
     *                          String, etc.).
     * @param   isValidating    <CODE>true</CODE> if the parser shall validate
     *                          documents which it reads.
     * @param   errorHandler    The error handler
     *
     * @return  The root element of the document.
     *
     * @throws  XMLReaderException
     *          There was an exception during trying to read the document. <BR/>
     *          Possible causes: A parser exception, a SAX exception, the file
     *          was not found or is <CODE>null</CODE>, there is no root element.
     */
    private Element read (Object inputSource, boolean isValidating,
                          ErrorHandler errorHandler) throws XMLReaderException
    {
        Element elem = null;            // the root element

        try
        {
            // try to read the document:
            this.p_doc = this.getDocument (inputSource, isValidating, errorHandler);

            // get the root element:
            elem = this.p_doc.getDocumentElement ();

            // check if the element was found:
            if (elem == null)       // did not find element?
            {
                throw new XMLReaderException (
                    "No root element found.");
            } // if did not find element
        } // try
        catch (NullPointerException e)
        {
            throw new XMLReaderException (
                "The document is null.", e);
        } // catch

        // return the result:
        return elem;
    } // read


    /**************************************************************************
     * Read the contents of a xml file. <BR/>
     *
     * @param   file            The file to be read.
     * @param   isValidating    <CODE>true</CODE> if the parser shall validate
     *                          documents which it reads.
     * @param   errorHandler    The error handler
     *
     * @return  The root element of the document.
     *
     * @throws  XMLReaderException
     *          There was an exception during trying to read the document. <BR/>
     *          Possible causes: A parser exception, a SAX exception, the file
     *          was not found or is <CODE>null</CODE>, there is no root element.
     */
    private Element read (File file, boolean isValidating,
                          ErrorHandler errorHandler) throws XMLReaderException
    {
        // compute and return the result:
        return this.read ((Object) file, isValidating, errorHandler);
    } // read


    /**************************************************************************
     * Read the contents of a xml file. <BR/>
     *
     * @param   xmlReader       The input source for the xml structure.
     * @param   isValidating    <CODE>true</CODE> if the parser shall validate
     *                          documents which it reads.
     * @param   errorHandler    The error handler
     *
     * @return  The root element of the document.
     *
     * @throws  XMLReaderException
     *          There was an exception during trying to read the document. <BR/>
     *          Possible causes: A parser exception, a SAX exception, the file
     *          was not found or is <CODE>null</CODE>, there is no root element.
     */
    private Element read (InputSource xmlReader, boolean isValidating,
                          ErrorHandler errorHandler) throws XMLReaderException
    {
        // compute and return the result:
        return this.read ((Object) xmlReader, isValidating, errorHandler);
    } // read

} // class XMLReader
