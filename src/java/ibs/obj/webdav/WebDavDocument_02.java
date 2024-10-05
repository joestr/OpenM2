/**
 * Class: WebDavDocument_02.java
 */

// package:
package ibs.obj.webdav;

// imports:
import ibs.bo.BOPathConstants;
import ibs.bo.OID;
import ibs.di.ValueDataElement;
import ibs.obj.webdav.WebDavDocument_01;
import ibs.tech.sql.DBActionException;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;
import ibs.util.NameAlreadyGivenException;
import ibs.util.NoAccessException;
import ibs.util.file.FileHelpers;


/******************************************************************************
 * WebDavDocument_02 handels the Document Form with Templates abd WebDAV
 * support. <BR/>
 *
 * @version     $Id: WebDavDocument_02.java,v 1.7 2008/09/17 16:38:23 kreimueller Exp $
 *
 * @author      Mark Wassermann (MW), 20020911
 ******************************************************************************
 */
public class WebDavDocument_02 extends WebDavDocument_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO = "$Id: WebDavDocument_02.java,v 1.7 2008/09/17 16:38:23 kreimueller Exp $";


    /**************************************************************************
     * Change the data of a business object in the database. <BR/>
     * This method tries to store the object into the database.
     * During this operation a rights check is done, too.
     * If this is all right the object is stored and this method terminates
     * otherwise an exception is raised. <BR/>
     *
     * @param   operation   Operation to be performed with the object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     * @exception   NameAlreadyGivenException
     *              An object with this name already exists. This exception is
     *              only raised by some specific object types which don't allow
     *              more than one object with the same name.
     */
    protected void performChangeData (int operation)
        throws NoAccessException, NameAlreadyGivenException
    {
        boolean emptyFileField = false;
        OID templOID = null;
        String templFilename = new String ();
        String docFilename = new String ();
        String fileUploadPath = new String (this.app.p_system.p_m2AbsBasePath +
                                            BOPathConstants.PATH_UPLOAD_ABS_FILES);
        ValueDataElement value = null;
        ValueDataElement fileValue = null;
        SQLAction action;

        for (int i = 0; i < this.dataElement.values.size (); i++)
        {
            value = this.dataElement.values.elementAt (i);

            // check if file field is empty
            if (value.field.equals ("Dokument"))
            {
                if (value.value.isEmpty ())
                {
                    fileValue = value;
                    emptyFileField = true;
                } // if
            } // if

            // check if template is selected and take if if it is
            if (value.field.equals ("Vorlage"))
            {
                if (!value.value.isEmpty ())
                {
                    if (emptyFileField)
                    {
                        String templQuery = new String ();
                        templQuery =
                            "SELECT t.oid, t.m_template" +
                            " FROM   ibs_Object o, dbm_webdavdoctempl01 t" +
                            " WHERE  o.name like '" + value.value + "'" +
                            "  AND  o.oid = t.oid";

                        action = this.getDBConnection ();
                        try
                        {
                            if (action.execute (templQuery, false) > 0)
                            {
                                if (!action.getEOF ())
                                {
                                    if (!action.getEOF ())
                                    {
                                        templOID = SQLHelpers.getQuOidValue (
                                            action, "oid");
                                    } // if
                                    templFilename = action
                                        .getString ("m_template");

                                    docFilename = this.name +
                                        templFilename.substring (templFilename
                                            .lastIndexOf ("."));

                                    if (!FileHelpers.exists (fileUploadPath +
                                        java.io.File.separator +
                                        this.oid.toString () +
                                        java.io.File.separator))
                                    {
                                        FileHelpers.makeDir (fileUploadPath +
                                            java.io.File.separator +
                                            this.oid.toString () +
                                            java.io.File.separator);
                                    } // if
                                    FileHelpers.copyFile (fileUploadPath +
                                        templOID.toString () +
                                        java.io.File.separator + templFilename,
                                        fileUploadPath + this.oid.toString () +
                                            java.io.File.separator +
                                            docFilename);
                                    fileValue.value = docFilename;
                                } // if
                                action.end ();
                            } // if
                        } // try
                        catch (DBActionException e)
                        {
                            System.out.println ("db error: " + e.getMessage ());
                        } // catch DBActionException
                        catch (DBError e)
                        {
                            System.out.println ("db error: " + e.getMessage ());
                        } // catch
                        finally
                        {
                            this.releaseDBConnection (action);
                        } // finally
                    } // if
                } // if
            } // if
        } // for
        super.performChangeData (operation);
    } // performChangeData

} // WebDavDocument_02
