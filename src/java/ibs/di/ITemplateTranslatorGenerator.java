/**
 * Class: ITemplateTranslatorGenerator.java
 */

// package:
package ibs.di;

// imports:
import ibs.bo.OID;
import ibs.io.Environment;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.service.user.User;

import java.io.ByteArrayOutputStream;

/******************************************************************************
 * Interface for all Template Translator Generator implementations. <BR/>
 *
 * @version     $Id: ITemplateTranslatorGenerator.java,v 1.1 2009/09/04 13:14:35 btatzmann Exp $
 *
 * @author      Bernhard Tatzmann 27082009
 ******************************************************************************
 */
public interface ITemplateTranslatorGenerator
{
    /**************************************************************************
     * Creates an new translator and offers it the user for downloading. <BR/>
     *
     * @param   oldTemplate     Old document template.
     * @param   newTemplate     New document template.
     * @param   user            Object representing the user.
     * @param   env             Enviroment of the application.
     * @param   sess            Session of the user.
     * @param   app             Application informations.
     * @param   oid             Value for the compound object id.
     */
    public void createAndDownloadTranslator (
            DataElement oldTemplate,
            DataElement newTemplate,
            User user,
            Environment env,
            SessionInfo sess,
            ApplicationInfo app,
            OID oid);
    
    /**************************************************************************
     * Creates an translator which change the structure from an given
     * document template to an also given new document template. <BR/>
     * Only the system- and value-tags are changed. <BR/>
     * Returns true if the translator could be created.
     *
     * @param   oldTemplate     Old document template.
     * @param   newTemplate     New document template.
     * @param   user            Object representing the user.
     * @param   env             Enviroment of the application.
     * @param   sess            Session of the user.
     * @param   app             Application informations.
     * @param   oid             Value for the compound object id.
     *
     * @return  The serialized document implementation of the translator
     *          as ByteArrayOutputStream.
     *
     */
    public ByteArrayOutputStream createNewDocumentTemplateTranslator (
             DataElement oldTemplate,
             DataElement newTemplate,
             User user,
             Environment env,
             SessionInfo sess,
             ApplicationInfo app,
             OID oid);
} // ITemplateTranslatorGenerator
