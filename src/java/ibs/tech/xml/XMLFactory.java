/*
 * Class: XMLFactory.java
 */

// package:
package ibs.tech.xml;

// imports:
import ibs.BaseObject;
import ibs.tech.xml.XMLFactoryException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.ErrorHandler;


/******************************************************************************
 * This class is used for reading XML files. <BR/>
 *
 * @version     $Id: XMLFactory.java,v 1.3 2007/07/31 19:14:00 kreimueller Exp $
 *
 * @author      Klaus, 15.11.2003
 ******************************************************************************
 */
public abstract class XMLFactory extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: XMLFactory.java,v 1.3 2007/07/31 19:14:00 kreimueller Exp $";


    /**
     * The factory which is used to get document builders. <BR/>
     */
    private static DocumentBuilderFactory p_dbf = null;

    /**
     * The document builder which is used to read and write documents. <BR/>
     */
//    private static DocumentBuilder p_db = null;



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Get a document builder. <BR/>
     *
     * @return  The document builder.
     *
     * @throws  XMLFactoryException
     *          There was an exception during trying to get the document builder.
     *. <BR/>
     *          Possible causes: the factory could not be created,
     *          the parser is misconfigured.
     */
    public static DocumentBuilder getDocBuilder () throws XMLFactoryException
    {
        // call common method and return result:
        return XMLFactory.getDocBuilder (false, null);
    } // creategetDocBuilder


    /**************************************************************************
     * Get a document builder. <BR/>
     *
     * @param   isValidating    <CODE>true</CODE> if the parser shall validate
     *                          documents which it reads.
     * @param   errorHandler    The error handler
     *
     * @return  The document builder.
     *
     * @throws  XMLFactoryException
     *          There was an exception during trying to get the document builder.
     *. <BR/>
     *          Possible causes: the factory could not be created,
     *          the parser is misconfigured.
     */
    public static DocumentBuilder getDocBuilder (boolean isValidating,
                                                 ErrorHandler errorHandler)
        throws XMLFactoryException
    {
        DocumentBuilder db = null;      // the actual document builder

        try
        {
            // check if there is already a document builder factory initialized:
            if (XMLFactory.p_dbf == null) // no factory found?
            {
                // try to get the document builder factory:
                XMLFactory.p_dbf = DocumentBuilderFactory.newInstance ();
            } // if no factory found

            // get the document builder out of the factory:
            XMLFactory.p_dbf.setValidating (isValidating);
//            p_dbf.setNamespaceAware (true|false);
            db = XMLFactory.p_dbf.newDocumentBuilder ();
            db.setErrorHandler (errorHandler);
//            db.setEntityResolver (er);

            // check if the document builder was created:
            if (db == null)             // could not create document builder?
            {
                throw new XMLFactoryException (
                    "Could not create document builder.");
            } // if could not create document builder
        } // try
        catch (FactoryConfigurationError e)
        {
            throw new XMLFactoryException (
                "A DocumentBuilderFactory cannot be created.", e);
        } // catch
        catch (ParserConfigurationException e)
        {
            throw new XMLFactoryException (
                "A DocumentBuilder cannot be created which " +
                "satisfies the configuration requested.", e);
        } // catch

        // return the result:
        return db;
    } // creategetDocBuilder

} // class XMLFactory
