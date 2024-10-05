/*
 * Class: Filter.java
 */

// package:
package ibs.di.filter;

// imports:
//KR TODO: unsauber
import ibs.bo.BusinessObject;
import ibs.di.DIConstants;
import ibs.di.DataElement;
import ibs.di.DataElementList;
import ibs.io.IOHelpers;
import ibs.tech.xml.DOMHandler;
import ibs.tech.xml.XMLException;
import ibs.tech.xml.XMLWriter;
import ibs.tech.xml.XMLWriterException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/******************************************************************************
 * This abstract class ImportFilter declares all methods to be accessed
 * via the integrator to handle imports. <BR/>
 *
 * @version     $Id: Filter.java,v 1.28 2007/07/31 19:13:54 kreimueller Exp $
 *
 * @author      Buchegger Bernd (BB), 990521
 ******************************************************************************
 */
public abstract class Filter extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Filter.java,v 1.28 2007/07/31 19:13:54 kreimueller Exp $";


    /**
     * flag to include the document type defintion in the export xml file. <BR/>
     */
    public boolean isIncludeDTD = false;

    /**
     * flag pretty print the XML document. <BR/>
     */
    public boolean isPrettyPrint = true;

    /**
     * XML document DOM. <BR/>
     */
    public Document doc;

    /**
     * name of file that holds the data. <BR/>
     */
    public String fileName = null;

    /**
     * path to file that holds the data. <BR/>
     */
    public String path = null;

    /**
     * Holds a the amount of &lt;OBJECTS> elements founds in the document. <BR/>
     */
    public int elementsLength = -1;

    /**
     * The metadata for this filter. <BR/>
     */
    private DataElement p_elementMetadata = null;

    /**
     * Element used as insertion point for new objects to be exported. <BR/>
     */
    public Element p_insertionPoint = null;

    /**
     * Flag to include the result of a queryfield in the export. <BR/>
     */
    protected boolean p_isExportQueryResults = true;


    /**************************************************************************
     * Creates an ImportFilter Object. <BR/>
     */
    public Filter ()
    {
        // nothing to do
    } // ImportFilter


    /**************************************************************************
     * Sets a file name. <BR/>
     *
     * @param   fileName    The value to be set.
     */
    public void setFileName (String fileName)
    {
        String fileNameLocal = fileName; // variable for local assignments
        // first check if the fileName containers any slashes
        // slashes or backslashes will be replaces by a "-"
        fileNameLocal = fileNameLocal.replace ('/', '-');
        fileNameLocal = fileNameLocal.replace ('\\', '-');

        this.fileName = fileNameLocal;
    } // setFileName


    /**************************************************************************
     * Sets Metadata elements. <BR/>
     *
     * @param   dataElement The data element to be set.
     */
    public void setMetadataElement (DataElement dataElement)
    {
        this.p_elementMetadata = dataElement;
    } // setMetadataElement


    /**************************************************************************
     * Returns the Metadata elements. <BR/>
     *
     * @return  The data element.
     */
    public DataElement getMetadataElement ()
    {
        return this.p_elementMetadata;
    } // getMetadataElement


    /**************************************************************************
     * Sets a path. <BR/>
     *
     * @param   path    The value to be set.
     */
    public void setPath (String path)
    {
        this.path = path;
    } // setPath


    /**************************************************************************
     * Returns the file name set in the filter. <BR/>
     *
     * @return  The required value.
     *          <CODE>null</CODE> if the value is not set.
     */
    public String getFileName ()
    {
        return this.fileName;
    } // getFileName


    /**************************************************************************
     * Returns the path set in the filter. <BR/>
     *
     * @return  The required value.
     *          <CODE>null</CODE> if the value is not set.
     */
    public String getPath ()
    {
        return this.path;
    } // getPath


    /**************************************************************************
     * Support function used for hierarchial export.
     *
     * @param isTabInsertion    the new insertion point is for tab objects
     */
    public void setInsertionPoint (boolean isTabInsertion)
    {
        // nothing to do
    } // set InsertionPoint


    /**************************************************************************
     * Support function used for hierarchial export.
     */
    public void revertInsertionPoint ()
    {
        // nothing to do
    } // revertInsertionPoint


    /**************************************************************************
     * Returns the amount of object collections (&lt;OBJECTS> elements)
     * found in the import document. <BR/>
     * Hint: Must be overwritten in the subclasses that can locate the
     * amound. There are possibly filters that can not locate this quantity. <BR/>
     *
     * @return the amount of objects collections found or -1 if amount could
     *          not be read.
     */
    public int getElementsLength ()
    {
        return this.elementsLength;
    } // getElementsLength


    /**************************************************************************
     * This is some kind of initialization procedure that must be called before
     * an import starts.
     * The init method reads in the import file.
     * The init method should read the file name of the
     * import script.
     *
     * @return true initialisation succeeded or false otherwise
     */
    public abstract boolean init ();


    /**************************************************************************
     * Sets processing again to the beginning of all objects. <BR/>
     */
    public abstract void reset ();


    /**************************************************************************
     * Closes the filter and removed the DOM tree of the XML file. <BR/>
     */
    public void close ()
    {
        // check if a document root is set
        if (this.doc != null)
        {
            try
            {
                this.doc.removeChild (this.doc.getDocumentElement ());
                // set the doc to null
                this.doc = null;
            } // try
            catch (Exception e)
            {
                // ignore any error
                IOHelpers.printError ("Exception during closing the filter.",
                    this, e, true);
            } // catch
        } // if (this.doc != null)
    } // close


    /**************************************************************************
     * Creates a xml structure. <BR/>
     * The document is stored in {@link #doc doc}.
     *
     * @return  The constructed document.
     */
    protected Document createDocument ()
    {
        Document retVal = null;         // the return value

        try
        {
            // create a new DOM root
            retVal = XMLWriter.createDocument ();
            this.doc = retVal;
        } // try
        catch (XMLWriterException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch

        // return the result:
        return retVal;
    } // createDocument


    /**************************************************************************
     * Creates the DOM root of a xml structure and stores it in the
     * insertionPoint. <BR/>
     * The document itself is stored in {@link #doc doc}.
     *
     * @param   rootName    The name of the root element which shall be created
     *                      within the document.
     *
     * @return  The root node.
     */
    protected Element createDocumentRoot (String rootName)
    {
        Element retVal = null;          // the return value

        try
        {
            // create a new DOM root
            this.doc = XMLWriter.createDocument (rootName);
            retVal = (Element) this.doc.getFirstChild ();
            this.p_insertionPoint = retVal;
        } // try
        catch (XMLWriterException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch

        // return the result:
        return retVal;
    } // createDocumentRoot


    /**************************************************************************
     * Tests if there are more objects available from this import file. <BR/>
     *
     * @return true if there are more objects false otherwise
     */
    public abstract boolean hasMoreObjects ();


    /**************************************************************************
     * Returns the next DataElement from this importFile. <BR/>
     *
     * @return an DataElement Object that holds the data of an object in the
     *          importFile
     */
    public abstract DataElement nextObject ();


    /**************************************************************************
     * Returns the next DataElementList from this importFile. <BR/>
     *
     * @return an DataElementList object that holds a collection of objects
     *          the importScript can be applied on
     */
    public abstract DataElementList nextObjectCollection ();


    //
    // ABSTRACT METHODS FOR EXPORT
    //
    /**************************************************************************
     * Creates an XML document out from a dataElement. <BR/>
     *
     * @param dataElement   the dataElement to construct the export document from
     *
     * @return  true if the export document has been created sucuessfully
     *          or false otherwise
     */
    public boolean create (DataElement dataElement)
    {
        DataElement[] dataElements = {dataElement};
        return this.create (dataElements);
    } // create


    /**************************************************************************
     * Creates an XML document out of an array of dataElements.
     * This is the method that must be implemented in the subclasses. <BR/>
     *
     * @param dataElements  a dataElement array to construct the export
     *                      document from
     *
     * @return  true if the export document has been created sucuessfully
     *          or false otherwise
     */
    public abstract boolean create (DataElement[] dataElements);


    /**************************************************************************
     * Adds an object definition to the export document.
     *
     * @param dataElement   the dataElement to construct the export document from
     *
     * @return  true if the export document has been created sucuessfully
     *          or false otherwise
     */
    public boolean add (DataElement dataElement)
    {
        DataElement[] dataElements = {dataElement};
        return this.add (dataElements);
    } // create


    /**************************************************************************
     * Adds a set of object definitions to the export document.
     * In case there is no export document created already it will be
     * initialized first. <BR/>
     *
     * @param dataElements  a dataElement array to construct the export
     *                      document from
     *
     * @return  true if the export document has been created sucuessfully
     *          or false otherwise
     */
    public abstract boolean add (DataElement[] dataElements);


    /**************************************************************************
     * Writes the export document to the file path specified in the
     * filename and path properties. <BR/>
     *
     * @return true if succeeded or false otherwise
     */
    public boolean write ()
    {
        return this.write (this.path, this.fileName);
    } // write


    /**************************************************************************
     * Writes the XML document to a file. <BR/>
     *
     * @param path      the file path to write the output file to
     * @param fileName  the filename to use for the output file
     *
     * @return true if succeeded or false otherwise
     */
    public boolean write (String path, String fileName)
    {
        // if we have a document to export
        if (this.doc == null)
        {
            // no document to write:
            return false;
        } // if (this.doc == null)

        // export document exists
        try
        {
            // serialize the dom tree:
            // shall the doctype be included?
            // <!DOCTYPE IMPORT SYSTEM "import.dtd">
            if (this.isIncludeDTD)
            {
                DOMHandler.serializeDOMSecure (this.doc,
                    path + fileName, this.isPrettyPrint,
                    DIConstants.URL_IMPORTDTD);
            } // if (this.isIncludeDTD)
            else                        // do not include the DTD
            {
                DOMHandler.serializeDOMSecure (this.doc,
                    path + fileName, this.isPrettyPrint, null);
            } // else do not include the DTD
            // close the stream
//                fileOutputStream.close ();
            return true;
        } // try
        catch (XMLException e)
        {
            // BB 050208 TODO: note that we loose the error message when
            // just returning true or false as error indicator!!!
            IOHelpers.showMessage (e, this.app, this.sess, this.env);
            return false;
        } // catch (XMLException e)
    } // write


    /**************************************************************************
     * This method gets the isExportQueryResults. <BR/>
     *
     * @return Returns the isExportQueryResults.
     */
    public boolean isExportQueryResults ()
    {
        //get the property value and return the result:
        return this.p_isExportQueryResults;
    } // isExportQueryResults


    /**************************************************************************
     * This method sets the isExportQueryResults. <BR/>
     *
     * @param isExportQueryResults The isExportQueryResults to set.
     */
    public void setExportQueryResults (boolean isExportQueryResults)
    {
        //set the property value:
        this.p_isExportQueryResults = isExportQueryResults;
    } // setExportQueryResults

} // Filter
