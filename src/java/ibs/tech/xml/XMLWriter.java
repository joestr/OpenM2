/*
 * Class: XMLWriter.java
 */

// package:
package ibs.tech.xml;

// imports:
import ibs.BaseObject;
import ibs.tech.xml.XMLFactory;
import ibs.tech.xml.XMLFactoryException;
import ibs.tech.xml.XMLWriterException;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;


/******************************************************************************
 * This class is used for reading XML files. <BR/>
 *
 * @version     $Id: XMLWriter.java,v 1.4 2007/07/23 08:17:33 kreimueller Exp $
 *
 * @author      Klaus, 15.11.2003
 ******************************************************************************
 */
public class XMLWriter extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: XMLWriter.java,v 1.4 2007/07/23 08:17:33 kreimueller Exp $";


    /**
     * Error message to be thrown when a document could not be created. <BR/>
     */
    public static String errCouldNotCreateDoc =
        "Could not create document.";
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
     * Creates a XMLWriter object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @throws  XMLWriterException
     *          There was an exception during trying to write the document. <BR/>
     *          Possible causes: ???.
     */
    public XMLWriter () throws XMLWriterException
    {
        // call constructor of supe class:
        super ();

        // set properties:
    } // XMLWriter


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Get the document. <BR/>
     *
     * @return  The document.
     *          <CODE>null</CODE> if the document does not exist or is not
     *          valid.
     */
    public Document getDoc ()
    {
        // get the property value and return the result:
        return this.p_doc;
    } // getDoc


    /**************************************************************************
     * Create a new document and the root element. <BR/>
     * This method first calls the standard
     * {@link #createDocument () createDocument} method, then creates an element
     * for the given name and adds it as the root element to the document.
     *
     * @param   rootName    The name of the root element which shall be created
     *                      within the document.
     *
     * @return  The document.
     *
     * @throws  XMLWriterException
     *          There was an exception during trying to read the document. <BR/>
     *          Possible causes: the document could not be created,
     *              the root could not be created or not added to the document.
     */
    public static Document createDocument (String rootName) throws XMLWriterException
    {
        Document doc = null;            // the document

        try
        {
            // get the document:
            doc = XMLWriter.createDocument ();

            // create the root of the xml structure:
            doc.appendChild (doc.createElement (rootName));
        } // try
        catch (DOMException e)
        {
            throw new XMLWriterException ("Could not create document root.", e);
        } // catch

        // return the result:
        return doc;
    } // createDocument


    /**************************************************************************
     * Create a new document. <BR/>
     *
     * @return  The document.
     *
     * @throws  XMLWriterException
     *          There was an exception during trying to read the document. <BR/>
     *          Possible causes: the document could not be created.
     */
    public static Document createDocument () throws XMLWriterException
    {
        DocumentBuilder db;             // the document builder
        Document doc = null;            // the document

        try
        {
            // get the document builder:
            db = XMLFactory.getDocBuilder ();

            // try to create the document:
            doc = db.newDocument ();

            // check if the document was created:
            if (doc == null)            // could not create document?
            {
                throw new XMLWriterException (XMLWriter.errCouldNotCreateDoc);
            } // if could not create document
        } // try
        catch (XMLFactoryException e)
        {
            throw new XMLWriterException (e);
        } // catch

        // return the result:
        return doc;
    } // createDocument


    /**************************************************************************
     * Create a new document. <BR/>
     *
     * Creates a DOM Document object of the specified type with its document
     * element.
     *
     * @param   namespaceURI    The namespace URI of the document element to
     *                          create.
     * @param   qualifiedName   The qualified name of the document element to be
     *                          created.
     * @param   docType         The type of document to be created or
     *                          <code>null</code>.
     *
     * @return  The document.
     *
     * @throws  XMLWriterException
     *          There was an exception during trying to read the document. <BR/>
     *          Possible causes: the document could not be created.
     *          chained exception DOMException:
     *   INVALID_CHARACTER_ERR: Raised if the specified qualified name
     *   contains an illegal character.
     *   <br>NAMESPACE_ERR: Raised if the <code>qualifiedName</code> is
     *   malformed, if the <code>qualifiedName</code> has a prefix and the
     *   <code>namespaceURI</code> is <code>null</code>, or if the
     *   <code>qualifiedName</code> has a prefix that is "xml" and the
     *   <code>namespaceURI</code> is different from "
     *   http://www.w3.org/XML/1998/namespace" , or if the DOM
     *   implementation does not support the <code>"XML"</code> feature but
     *   a non-null namespace URI was provided, since namespaces were
     *   defined by XML.
     *   <br>WRONG_DOCUMENT_ERR: Raised if <code>doctype</code> has already
     *   been used with a different document or was created from a different
     *   implementation.
     *   <br>NOT_SUPPORTED_ERR: May be raised by DOM implementations which do
     *   not support the "XML" feature, if they choose not to support this
     *   method. Other features introduced in the future, by the DOM WG or
     *   in extensions defined by other groups, may also demand support for
     *   this method; please consult the definition of the feature to see if
     *   it requires this method.
     *
     * @see org.w3c.dom.DOMImplementation#createDocument (java.lang.String, java.lang.String, org.w3c.dom.DocumentType)
     */
    public static Document createDocument (String namespaceURI,
                                           String qualifiedName,
                                           DocumentType docType)
        throws XMLWriterException
    {
        DocumentBuilder db;             // the document builder
        Document doc = null;            // the document
        DOMImplementation dom = null;   // the dom implementation

        try
        {
            // get the document builder:
            db = XMLFactory.getDocBuilder ();

            // try to create the document:
            dom = db.getDOMImplementation ();
            doc = dom.createDocument (namespaceURI, qualifiedName, docType);

            // check if the document was created:
            if (doc == null)            // could not create document?
            {
                throw new XMLWriterException (XMLWriter.errCouldNotCreateDoc);
            } // if could not create document
        } // try
        catch (DOMException e)
        {
            throw new XMLWriterException (e);
        } // catch
        catch (XMLFactoryException e)
        {
            throw new XMLWriterException (e);
        } // catch

        // return the result:
        return doc;
    } // createDocument

} // class XMLWriter
