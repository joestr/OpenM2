/*
 * Class: DBMapper.java
 */

// package:
package ibs.di.service;

// imports:
//KR TODO: unsauber
import ibs.bo.BusinessObject;
//KR TODO: unsauber
import ibs.bo.OID;
import ibs.di.DIConstants;
import ibs.di.DataElement;
import ibs.di.filter.m2XMLFilter;
import ibs.di.imp.ImportIntegrator;
import ibs.di.service.ObjectTransformationException;
import ibs.di.service.ServiceMessages;
//KR TODO: unsauber
import ibs.io.Environment;
//KR TODO: unsauber
import ibs.io.IOHelpers;
//KR TODO: unsauber
import ibs.io.session.ApplicationInfo;
//KR TODO: unsauber
import ibs.io.session.SessionInfo;
import ibs.ml.MultilingualTextProvider;
//KR TODO: unsauber
import ibs.service.user.User;
import ibs.tech.xml.DOMHandler;
import ibs.tech.xml.XMLWriter;
import ibs.tech.xml.XMLWriterException;
import ibs.tech.xslt.XSLTTransformationException;
import ibs.tech.xslt.XSLTTransformer;

import java.io.CharArrayWriter;

import org.w3c.dom.Document;


/******************************************************************************
 * This class represents one object of type ObjectTransformer. <BR/>
 * An ObjectTransformer can be used to transform an object of type A
 * into an object of type B via an XSLT transformation. Note that the standard
 * m2 import structure is used.
 *
 * @version     $Id: ObjectTransformer.java,v 1.11 2010/04/07 13:37:10 rburgermann Exp $
 *
 * @author      Bernd Buchegger (BB), 020527
 ******************************************************************************
 */
public class ObjectTransformer extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ObjectTransformer.java,v 1.11 2010/04/07 13:37:10 rburgermann Exp $";


    /**************************************************************************
     * The constructor for a ObjectTransformer object. <BR/>
     *
     * @param   user    The user object.
     * @param   env     The actual environment.
     * @param   sess    The current session info.
     * @param   app     The global application info.
     */
    public ObjectTransformer (User user, Environment env, SessionInfo sess,
                              ApplicationInfo app)
    {
        // call the constructor of the super class
        super ();
        // initialize the business object
        this.initObject (OID.getEmptyOid (), user, env, sess, app);
    } // ObjectTransformer


    /**************************************************************************
     * Transforms an object with type A into another object of type B
     * via an XSLT transformation. <BR/>
     *
     * @param   obj                 the sourceObject whose data has already
     *                              been retrieved
     * @param   translatorFilePath  the file name and path of the translator
     *                              file
     * @param   containerOid        The oid of the target container.
     *
     * @return the new object or null in case an error occurred
     *
     * @throws  ObjectTransformationException
     *          There occurred an error during the transformation process.
     */
    public BusinessObject transform (BusinessObject obj,
                                     String translatorFilePath,
                                     OID containerOid)
        throws ObjectTransformationException
    {
        DataElement dataElement = null;
        m2XMLFilter filter = null;
        Document resultDoc = null;

        // check if we have a translator
        if (translatorFilePath == null || translatorFilePath.length () == 0)
        {
            // if not return the object itself because no translation will be
            // done. Note that this case is not handled as an error
            return obj;
        } // if (translatorFilePath == null || translatorFilePath.length () == 0)

        // translator file set
        // create an dataElement to hold the data
        dataElement = new DataElement ();
        // create an m2XMLFilter instance
        filter = new m2XMLFilter ();
        filter.initObject (OID.getEmptyOid (), this.user,
                this.env, this.sess, this.app);
        // write the data of the object into the dataElement
        obj.writeExportData (dataElement);
        // feed an filter with the dataElement creating the XML import structure
        // and check if it was successfull
        if (!filter.create (dataElement))
        {
            // throw an exception
            throw new ObjectTransformationException (
                MultilingualTextProvider.getMessage (ServiceMessages.MSG_BUNDLE,
                    ServiceMessages.ML_E_TRANSFORMATION_FAILED, env) + " (" +
                MultilingualTextProvider.getMessage (ServiceMessages.MSG_BUNDLE,
                    ServiceMessages.ML_MSG_COULD_NOT_CREATE_XML_STRUCTURE, env) + ")");
        } // if (filter.add (dataElement))
//printDoc (filter.doc);
        // apply the xsl transformation
        try
        {
            // create the document instance that will hold get the translated
            // xml structure:

            try
            {
                resultDoc = XMLWriter.createDocument ();
            } // try
            catch (XMLWriterException e)
            {
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
                throw new ObjectTransformationException (e);
            } // catch

            // translate the template:
            new XSLTTransformer (translatorFilePath)
                .translateFile (filter.doc, resultDoc);
            // we print the xml structure for debugging purposes
//printDoc (resultDoc);
        } // try
        catch (XSLTTransformationException e)
        {
            throw new ObjectTransformationException (
                MultilingualTextProvider.getMessage (ServiceMessages.MSG_BUNDLE,
                    ServiceMessages.ML_E_TRANSFORMATION_FAILED, env) +
                " (" + e.getMessage () + ")");
        } // catch

        // initialize the filter with the result XML dom
        filter.init (resultDoc);
        // get a data element from the filter
        dataElement = filter.nextObject ();

// AJ BEGIN
        // import translated xml - structure to targetcontainer
        ImportIntegrator integrator = new ImportIntegrator ();
        integrator.initObject (OID.getEmptyOid (), this.user, this.env,
                               this.sess, this.app);
        integrator.isGetSettingsFromEnv = false;
        integrator.initLog ();
        integrator.log.isDisplayLog = false;
        if (!integrator.importElement (dataElement, containerOid,
                                       DIConstants.OPERATION_NEW))
        {
            // throw an exception
            throw new ObjectTransformationException (
                MultilingualTextProvider.getMessage (ServiceMessages.MSG_BUNDLE,
                    ServiceMessages.ML_E_TRANSFORMATION_FAILED, env) + " (" +
                MultilingualTextProvider.getMessage (ServiceMessages.MSG_BUNDLE,
                    ServiceMessages.ML_MSG_COULD_NOT_CREATE_OBJECT, env) + ")");
        }  // if xml - structure was imported sucessfully

        return null;
// AJ END

/*
        // create an object factory instance
        objectFactory = new ObjectFactory ();
        objectFactory.initObject (OID.getEmptyOid (), this.user,
                this.env, this.sess, this.app);
        // create a log object that is needed for the object factory
        log = new Log_01 ();
        log.initObject (OID.getEmptyOid (), this.user, this.env, this.sess, this.app);
        log.isDisplayLog = false;
        log.isWriteLog = false;
        // we need to set a log object
        objectFactory.setLog (log);
        // create the object
        newObj = objectFactory.createObject (dataElement, containerOid, false);
        // could the object be created
        if (newObj != null)
        {
            // return the object that has been created from the objectFactory
            return newObj;
        } // if (newObj != null)
        else    // object could not be created
        {
            // throw an exception
            throw new ObjectTransformationException (
                    ServiceMessages.E_TRANSFORMATION_FAILED + " (" +
                    ServiceMessages.MSG_COULD_NOT_CREATE_OBJECT + ")");
        } // object could not be created
*/
    } // transform


    /**************************************************************************
     * Writes the XML document to a file. <BR/>
     *
     * @param   doc     The xml document to be written to the file.
     */
    public void printDoc (Document doc)
    {
        CharArrayWriter charArrayWriter = null;

        // if we have a document to export
        if (doc != null)
        {
            // open file output stream
            charArrayWriter = new CharArrayWriter ();
            DOMHandler.serializeDOM (doc, charArrayWriter, true, null);

            // close the streams:
            charArrayWriter.close ();
            // show the result:
            IOHelpers.showMessage (charArrayWriter.toString (),
                this.app, this.sess, this.env);
        } // else export document exists
    } // write

} // ObjectTransformer
